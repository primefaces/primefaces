//@ts-check

const ts = require("typescript");
const { depthFirstNode } = require("./ts-utils");
const { mapCompute } = require("./lang");
const { createDocComment, parseSingleComment } = require("./doc-comments");

/**
 * @param {ts.SyntaxKind} kind
 * @param {string | string[]} commentText
 * @return {string}
 */
function prepareSyntheticComment(kind, commentText) {
    const firstLine = Array.isArray(commentText) ? commentText[0] || "" : commentText;
    if (kind === ts.SyntaxKind.MultiLineCommentTrivia && (firstLine.startsWith("*") || firstLine.startsWith(" *") || firstLine.startsWith("/**"))) {
        const lines = Array.isArray(commentText) ? commentText : commentText.split("\n");
        if (lines[0].startsWith("/**")) {
            lines[0] = "*";
            lines[lines.length - 1] = " ";
        }
        return lines
            .map((line, index) => index === 0 ? line.trim() : " " + line.trim())
            .join("\n");
    }
    else {
        return Array.isArray(commentText) ? commentText.join("\n") : commentText;
    }
}

/**
 * @param {WeakMap<TsNode, TsDocCommentWithPos[]>} docsByNode
 * @return {TsDocCommentHandler}
 */
function makeCommentHandler(docsByNode) {
    /**
     * @param {TsNode} node 
     * @return {TsDocCommentWithPos[] | undefined}
     */
    const getByNode = node => {
        const docs = docsByNode.get(node);
        return docs;
    };
    return {
        getClosestDocComment(node) {
            const docs = getByNode(node);
            return docs === undefined || docs.length === 0 ? undefined : docs[docs.length - 1].doc;
        },
        getDocComment(node, index) {
            const docs = getByNode(node);
            return docs === undefined || index < 0 || index >= docs.length ? undefined : docs[index].doc;
        },
        getDocCommentCount(node) {
            const docs = getByNode(node)
            return docs === undefined ? 0 : docs.length;
        },
        getDocComments(node) {
            const docs = getByNode(node);
            return docs === undefined ? undefined : docs.map(x => x.doc); 
        },
        hasDocComment(node) {
            const docs = getByNode(node);
            return docs !== undefined && docs.length > 0;
        },
        replaceClosestDocComment(node, comment) {
            const docs = getByNode(node);
            if (docs !== undefined) {
                /** @type {TsDocCommentWithPos} */
                const commentWithPos = {
                    doc: comment,
                    pos: docs.length === 0 ? node.pos : docs[docs.length - 1].pos,
                };
                if (docs.length === 0) {
                    docs.push(commentWithPos);
                }
                else {
                    docs[docs.length - 1] = commentWithPos;
                }
            }
        },
        applyToSourceFile(sourceFile) {
            /** @type {Set<number>} */
            const processedDocs = new Set();
            depthFirstNode(sourceFile, node => {
                const docs = getByNode(node);
                if (docs !== undefined) {
                    for (const doc of docs) {
                        if (!processedDocs.has(doc.pos)) {
                            processedDocs.add(doc.pos);
                            const commentText = createDocComment(doc.doc.description, doc.doc.tags);
                            ts.addSyntheticLeadingComment(
                                node,
                                ts.SyntaxKind.MultiLineCommentTrivia,
                                prepareSyntheticComment(ts.SyntaxKind.MultiLineCommentTrivia, commentText),
                                true,
                            );
                        }
                    }
                }
                return true;
            });
        }
    };
}

/**
 * @param {string} fullText 
 * @param {import("typescript").CommentRange} commentRange 
 * @param {SeveritySettingsConfig} severitySettings
 * @return {TsSourceComment | undefined}
 */
function extractComment(fullText, commentRange, severitySettings) {
    const commentLength = commentRange.end - commentRange.pos;
    const isTripleSlashComment = commentLength >= 3 && ts.SyntaxKind.SingleLineCommentTrivia === commentRange.kind && fullText.substr(commentRange.pos, 3) === "///";
    const isDocComment = commentLength >= 3 && ts.SyntaxKind.MultiLineCommentTrivia === commentRange.kind && fullText.substr(commentRange.pos, 3) === "/**";
    if (isTripleSlashComment) {
        return undefined;
    }
    else if (isDocComment) {
        return {
            kind: "DocComment",
            jsdoc: fullText => {
                const commentText = fullText.substring(commentRange.pos, commentRange.end);
                const jsdoc = parseSingleComment(severitySettings, commentText);
                return jsdoc;
            },
        };
    }
    else {
        return {
            kind: commentRange.kind === ts.SyntaxKind.SingleLineCommentTrivia ? "SingleLine" : "MultiLine",
            text: fullText => fullText.substring(
                commentRange.pos + 2,
                commentRange.kind === ts.SyntaxKind.SingleLineCommentTrivia ? commentRange.end : commentRange.end - 2
            ),
        };
    }
}

/**
 * 
 * @param {string} fullText 
 * @param {TsNode} node
 * @param {Set<number>} plainCommentsByStartpos
 * @param {Map<number, import("comment-parser").Comment>} docsByStartpos
 * @param {WeakMap<TsNode, TsDocCommentWithPos[]>} docsByNode
 * @param {SeveritySettingsConfig} severitySettings
 */
function processNode(fullText, node, plainCommentsByStartpos, docsByStartpos, docsByNode, severitySettings) {
    const commentRanges = ts.getLeadingCommentRanges(fullText, node.pos);
    if (commentRanges) {
        for (const commentRange of commentRanges) {
            const comment = extractComment(fullText, commentRange, severitySettings);
            if (comment !== undefined) {
                if (comment.kind === "DocComment") {
                    // Store doc comments temporarily and add them later
                    const docComments = mapCompute(docsByNode, node, n => []);
                    const jsdoc = mapCompute(docsByStartpos, commentRange.pos, pos => comment.jsdoc(fullText));
                    docComments.push({
                        doc: jsdoc,
                        pos: commentRange.pos,
                    })
                }
                else {
                    // Add normal comment only to the first node
                    if (!plainCommentsByStartpos.has(commentRange.pos)) {
                        ts.addSyntheticLeadingComment(
                            node,
                            commentRange.kind,
                            prepareSyntheticComment(commentRange.kind, comment.text(fullText)),
                            commentRange.hasTrailingNewLine
                        );
                        plainCommentsByStartpos.add(commentRange.pos);
                    }
                }
            }
        }
    }
}

/**
 * @param {TypeDeclarationBundleSourceFiles} sourceFiles
 * @param {SeveritySettingsConfig} severitySettings
 * @return {TsDocCommentHandler}
 */
function createCommentHandler(sourceFiles, severitySettings) {
    const fullTextAmbient = sourceFiles.ambient.getFullText();
    const fullTextModule = sourceFiles.module.getFullText();

    /** @type {Set<number>} */
    const plainCommentsByStartpos = new Set();
    /** @type {Map<number, import("comment-parser").Comment>} */
    const docsByStartpos = new Map();
    /** @type {WeakMap<TsNode, TsDocCommentWithPos[]>} */
    const docsByNode = new WeakMap();

    depthFirstNode(sourceFiles.ambient, node => {
        if (node.kind === ts.SyntaxKind.JSDocComment || node.kind === ts.SyntaxKind.JsxText) {
            return false;
        }
        if (node.pos !== node.end && node.kind !== ts.SyntaxKind.SyntaxList && node.kind !== ts.SyntaxKind.SourceFile) {
            processNode(fullTextAmbient, node, plainCommentsByStartpos, docsByStartpos, docsByNode, severitySettings);
        }
        return true;
    });

    depthFirstNode(sourceFiles.module, node => {
        if (node.kind === ts.SyntaxKind.JSDocComment || node.kind === ts.SyntaxKind.JsxText) {
            return false;
        }
        if (node.pos !== node.end && node.kind !== ts.SyntaxKind.SyntaxList && node.kind !== ts.SyntaxKind.SourceFile) {
            processNode(fullTextModule, node, plainCommentsByStartpos, docsByStartpos, docsByNode, severitySettings);
        }
        return true;
    });

    return makeCommentHandler(docsByNode);
}

module.exports = {
    createCommentHandler,
};