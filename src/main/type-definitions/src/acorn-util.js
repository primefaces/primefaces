//@ts-check

// generic functions related to parsing JavaScript

const { parse, parseExpressionAt } = require("acorn");
const { full, make, recursive } = require("acorn-walk");
const { readFileUtf8 } = require("./lang-fs");
const { join, resolve } = require("path");

const { getEmptyDocComment, parseSingleComment } = require("./doc-comments");
const { DefaultParseOptions } = require("./constants");
const { makeStackLine } = require("./error");

/**
 * Checks whether the node is of the given type. Includes a type guard that narrows down
 * the node to the given type automatically. 
 * @template {import("acorn-walk").NodeType} K Name of the node type to check for.
 * @param {import("estree").Node} node Node to check.
 * @param {K} type Name of the node type to check for.
 * @return {node is import("acorn-walk").NarrowNode<K>} Whether the node is of the given type.
 */
function is(node, type) {
    return node.type === type;
}

/**
 * If the given node is a member expression that is comprised of  `Identifier`s and `Literal`s
 * only, return a list of these identifiers. Otherwise, returns `undefined`.
 * 
 * For example, given the expression `Foo.bar.baz` or `Foo.bar["baz"]`, this returns
 * `["Foo", "bar", "baz"]`. If given `Foo.bar["ba" + "z"]`, returns `undefined`.
 * @param {import("estree").Node} node Node to process.
 * @return {undefined | string[]} The member expression path.
 */
function getIdentMemberPath(node) {
    /** @type {MemberPathState} */
    const state = { path: [], valid: true };
    full(node, (node, state, type) => {
        if (node.type !== "Identifier" && node.type !== "MemberExpression") {
            state.valid = false
        }
    }, make({
        /**
         * @param {MemberPathState} state
         * @param {import("acorn-walk").WalkerCallback<MemberPathState>} callback
         */
        Identifier(node, state, callback) {
            state.path.push(node.name);
        },
        /**
         * @param {MemberPathState} state}
         * @param {import("acorn-walk").WalkerCallback<MemberPathState>} callback
         */
        Literal(node, state, callback) {
            state.path.push(String(node.value));
        },
        /**
         * @param {MemberPathState} state}
         * @param {import("acorn-walk").WalkerCallback<MemberPathState>} callback
         */
        MemberExpression(node, state, callback) {
            callback(node.object, state);
            callback(node.property, state);
        },
    }), state);
    return state.valid ? state.path : undefined;
}

/**
 * Takes a member expression and checks whether that member expression consists
 * solely of `Identifier`s and `Literal`s, and whether these identifiers match the given path.
 * For example, `Foo.bar.baz` is such a member expression with the path
 * `["Foo", "bar", "baz"]`.
 * @param {import("estree").MemberExpression} node
 * @param {string[]} expectedPath Identifiers to check for. If not given or empty, skip ths check.
 * @return {boolean} Whether the member expression matches the given path.
 */
function isIdentMemberExpression(node, ...expectedPath) {
    const path = getIdentMemberPath(node);
    if (path === undefined) {
        return false;
    }
    if (expectedPath.length === 0) {
        return true;
    }
    if (expectedPath.length !== path.length) {
        return false;
    }
    return expectedPath.every((part, index) => !part || part === "*" || path[index] === part);
}

/**
 * Makes an object with visitor functions that can be used by `acorn-walk#full`. If a visitor is not specified for
 * a certain node type, uses the default visitor (that just descends into the children).
 * @template TState
 * @param {(node: import("estree").Node, state: TState) => boolean} callback Callback that is called for each node. If it returns false, do not descend the current node any further.
 * @param {TState} state Ignored. May be passed explicitly to let typescript deduce the type of the generics parameter `TState`.
 * @return {import("acorn-walk").RecursiveVisitors<import("acorn-walk").NodeType, TState>}
 */
function makeVisitors(callback, state) {
    /** @type {import("acorn-walk").RecursiveVisitors<import("acorn-walk").NodeType, TState>} */
    const visitors = make({});
    for (const key in visitors) {
        // @ts-ignore
        const visitor = visitors[key];
        /**
         * @param {import("estree").Node} node 
         * @param {TState} state 
         * @param {import("acorn-walk").WalkerCallback<TState>} c 
         */
        const newVisitor = (node, state, c) => {
            if (callback(node, state)) {
                visitor(node, state, c);
            }
        };
        // @ts-ignore
        visitors[key] = newVisitor; 
    }
    return visitors;
}

/**
 * Fix for broken type declarations
 * @param {string} source 
 * @return {import("estree").Expression}
 */
function parseToEstreeExpressionAt(source) {
    //@ts-ignore
    return parseExpressionAt(source);
}

/**
 * Fix for broken type declarations
 * @param {string} source
 * @param {import("acorn").Options} options
 * @return {import("estree").Program}
 */
function parseToEstree(source, options) {
    //@ts-ignore
    return parse(source, options);
}

/**
 * @param {string} data JavaScript to parse.
 * @param {string} sourceName Name of the program. 
 * @param {string} sourceLocation Location or source path of the script, does have to be a real file path. Added to the source
 * location of each node.
 * @param {3 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 2015 | 2016 | 2017 | 2018 | 2019 | 2020 | 2021 | 'latest'} ecmaVersion
 * Ecma version to use for parsing.
 * @return {CommentedAst<import("estree").Program>} The parsed programs with access to the doc comments.
 */
function parseJsProgram(data, sourceName, sourceLocation, ecmaVersion) {
    /** @type {CommentData[]} */
    const comments = [];
    let program;
    /** @type {import("acorn").Options} */
    const overrideOptions = {
        sourceFile: sourceLocation,
        ecmaVersion: ecmaVersion,
        onComment(isBlockComment, content, start, end) {
            comments.push({
                isBlockComment,
                content,
                start,
                end,
            });
        }
    };
    try {
        program = parseToEstree(data, Object.assign({}, DefaultParseOptions, overrideOptions));
    }
    catch (e) {
        /** @type {AcornSyntaxError} */
        const error = e;
        throw new Error(`Could not parse JavaScript source file\n${makeStackLine(sourceName, sourceLocation, error.loc.line, error.loc.column)}\n${error.stack}`);
    }
    return makeCommentedAst(program, comments);
}

/**
 * Takes a component and parses all JavaScript files it contains.
 * @param {string} basePath Base path (directory) with the files to parse.
 * @param {string[]} files One or more files to parse. If the path is relative, it must be relative to the given `basePath`
 * @param {3 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 2015 | 2016 | 2017 | 2018 | 2019 | 2020 | 2021 | 'latest'} ecmaVersion
 * Ecma version to use for parsing.
 * @return {AsyncIterable<CommentedAst<import("estree").Program>>} A list of parsed programs with access to the doc comments.
 */
async function* parseJs(files, ecmaVersion, basePath = "/") {
    for (const file of files) {
        const sourceFile = join(basePath, file);
        const resolvedSourceFile = resolve(sourceFile);
        const source = await readFileUtf8(sourceFile);
        const parsed = parseJsProgram(source, file, resolvedSourceFile, ecmaVersion);
        yield parsed;
    }
}

 /**
  * Maps comments found in the source code to the source nodes. Later, this is used to retrieve the comments for a certain node.
  * A node is considered as belonging to a comment if it is the first node following the comment, according to the position where it
  * starts in the source code.
  * @param {import("estree").Node} node Root node to start at.
  * @param {CommentData[]} comments Comments that were encountered during parsing.
  * @return {Map<import("estree").Node, CommentData[]>} A map between nodes and the associated comments.
  */
 function buildCommentMap(node, comments) {
    /** @type {BuildCommentMapState} */
    const state = {map: new Map(), index: 0, len: comments.length, start: -1, comments: []};
    full(node, ()=>{}, makeVisitors((node, state) => {
        /** @type {number} */
        //@ts-ignore
        const start = node.start;
        if (state.start > -1 && start > state.start) {
            state.start = -1;
            state.comments = [];
        }
        if (state.index < state.len) {
            if (start > comments[state.index].start) {
                /** @type {CommentData[]} */
                const c = [];
                do {
                    c.push(comments[state.index]);
                    state.index += 1;
                } while( state.index < state.len && start > comments[state.index].start);
                state.map.set(node, c);
                state.comments = c;
                state.start = start;
            }
        }
        if (start == state.start && state.comments.length > 0) {
            state.map.set(node, state.comments);
        }
        return true;
    }, state), state);
    return state.map;
}

/**
 * Takes a pattern and remove all (nested) initializers
 * > {x: x2 = 9, [y = 9]}
 * is transformed into
 * > {x: x2, [y]}
 * @template {import("estree").ArrayPattern | import("estree").ObjectPattern} TNode
 * @param {TNode} pattern 
 * @return {TNode}
 */
function removeInitializerFromPattern(pattern) {
    recursive(pattern, undefined, {
        ArrayPattern(node, state, callback) {
            // remove initializer
            node.elements = node.elements.map(element => {
                if (element && element.type === "AssignmentPattern") {
                    return element.left;
                }
                else {
                    return element;
                }
            });
            for (const element of node.elements) {
                // may be an empty slot, so check
                if (element) {
                    callback(element, state);
                }
            }
        },
        ObjectPattern(node, state, callback) {
            // typedefs are wrong...
            /** @type {(import("estree").Property | import("estree").RestElement)[]} */
            const properties = node.properties;
            for (const property of properties) {
                if (property.type === "Property") {
                    // remove initializer
                    if (property.value.type === "AssignmentPattern") {
                        property.value = property.value.left;
                    }    
                    // only descend into value, don't care about how computed values are computed
                    callback(property.value, state);
                }
                else if (property.type === "RestElement") {
                    callback(property, state)
                }
            }
        },    
    });
    return pattern;        
}

/**
 * Takes the root node and the comments encountered during parsing, and creates an object
 * that lets you access the comments for a particular node via a map.
 * @template {import("estree").Node} TNode
 * @param {TNode} node Root node to start at.
 * @param {CommentData[]} comments Comments encountered during parsing.
 * @return {CommentedAst<TNode>} The node with access to comments.
 */
function makeCommentedAst(node, comments) {
    return {
        node,
        comments,
        map: buildCommentMap(node, comments),
    };
}

/**
 * @param {CommentData[]} comments A list of comments.
 * @param {SeveritySettingsConfig} severitySettings
 * @return {import("comment-parser").Comment} The result of parsing the last comment that is a block comment.
 */
function findAndParseLastBlockComment(comments, severitySettings) {
    for (let i = comments.length; i --> 0;) {
        if (comments[i].isBlockComment) {
            return parseSingleComment(severitySettings, comments[i].content);
        }
    }
    return getEmptyDocComment();
}

/**
 * @param {import("estree").Node} node
 * @param {"ReturnStatement" | "YieldExpression"} type
 * @return {node is import("estree").ReturnStatement} `true` if the node is a `ReturnStatement` or `YieldStatement` that
 * (possibly) returns a value (`return x;` or `yield x`), `false` otherwise (`return;` or `yield`).
 */
function isNonEmptyReturn(node, type) {
    return node.type === type && node.argument !== null && node.argument !== undefined;
}

/**
 * Checks whether a (function body) block contains an explicit `return` statement that is non-empty, ie.
 * return a value. This makes sure to ignore `return` statements within nested functions or lambdas.
 * @param {import("estree").BlockStatement} block A block statement to check (usually a function body)
 * @return {import("estree").ReturnStatement | undefined} The non-empty return statement, or `undefined`
 * if no such statement exists.
 */
function findFunctionNonEmptyReturnStatement(block) {
    /** @type {{found: import("estree").ReturnStatement | undefined}} */
    const state = {found: undefined};
    recursive(block, state, {
        ReturnStatement(node, state, callback) {
            if (isNonEmptyReturn(node, "ReturnStatement") && state.found === undefined) {
                state.found = node;
            }
            // Need to visit return, YieldExpression can be inside ReturnStatement ("return yield* [1,2,3]")
            if (node.argument) {
                callback(node.argument, state);
            }
        },
        FunctionDeclaration(node, state, callback) {
            // ignore return in nested functions / lambdas
        },
        FunctionExpression(node, state, callback) {
            // ignore return in nested functions / lambdas
        }
    });
    return state.found;
}

/**
 * Checks whether a (function body) block contains an explicit `yield` statement. This makes sure to ignore `yield`
 * statements within nested functions or lambdas.
 * @param {import("estree").BlockStatement} block A block statement to check (usually a function body)
 * @return {import("estree").YieldExpression | undefined} The non-empty yield expression, or `undefined`
 * if no such expression exists.
 */
function findFunctionYieldExpression(block) {
    /** @type {{found: import("estree").YieldExpression | undefined}} */
    const state = {found: undefined};
    recursive(block, state, {
        YieldExpression(node, state, callback) {
            if (state.found === undefined) {
                state.found = node;
            }
        },
        FunctionDeclaration(node, state, callback) {
            // ignore return in nested functions / lambdas
        },
        FunctionExpression(node, state, callback) {
            // ignore return in nested functions / lambdas
        }
    });
    return state.found;
}

/**
 * Checks whether a (function body) block contains an explicit `yield` statement that is not a top-level statement, i.e.
 * whose return value might be used in some way. This makes sure to ignore `yield` statements within nested functions or
 * lambdas.
 * @param {import("estree").BlockStatement} block A block statement to check (usually a function body)
 * @return {import("estree").YieldExpression | undefined}
 * The yield statement, or `undefined` if no such statement exists.
 */
function findFunctionYieldStatement(block) {
    /** @type {{includes: Set<import("estree").YieldExpression>, excludes: Set<import("estree").YieldExpression>}} */
    const state = {includes: new Set(), excludes: new Set()};
    recursive(block, state, {
        ExpressionStatement(node, state, callback) {
            if (node.expression.type === "YieldExpression") {
                state.excludes.add(node.expression);
            }
            callback(node.expression, state);
        },
        YieldExpression(node, state, callback) {
            state.includes.add(node);
        },
        FunctionDeclaration(node, state, callback) {
            // ignore return in nested functions / lambdas
        },
        FunctionExpression(node, state, callback) {
            // ignore return in nested functions / lambdas
        }
    });
    state.excludes.forEach(exclude => state.includes.delete(exclude));
    return [...state.includes][0];
}

/**
 * Checks whether a (function body) block can complete normally, either by finishing execution or returning.
 * @param {import("estree").BlockStatement} block A block statement to check (usually a function body)
 * @return {boolean} `true` if the block can complete normally, `false` otherwise.
 */
function isCanCompleteNormally(block) {
    const singularThrow = block.body.length === 1 && block.body[0].type === "ThrowStatement";
    return !singularThrow;
    // further analyisis may be required in the future
}

/**
 * Parses the given string as a JavaScript (destructuring) pattern.
 * @param {string} pattern 
 * @return {import("estree").ArrayPattern | import("estree").ObjectPattern}
 */
function parsePattern(pattern) {
    const patternSource = `${pattern} = null`
    let parsed;
    try {
        parsed = parseToEstreeExpressionAt(patternSource);
    }
    catch (e) {
        throw new Error("Type of tag @structure must be a valid array or object pattern, but is: '" + pattern + "'\n" + e.stack);
    }
    if (is(parsed, "AssignmentExpression") && (is(parsed.left, "ArrayPattern") || is(parsed.left, "ObjectPattern"))) {
        return parsed.left;
    }
    else {
        throw new Error("Type of tag @structure must be a valid array or object pattern, but is: '" + pattern + "'");
    }    
}


module.exports = {
    isCanCompleteNormally,
    findAndParseLastBlockComment,
    findFunctionNonEmptyReturnStatement,
    findFunctionYieldExpression,
    findFunctionYieldStatement,
    getIdentMemberPath,
    is,
    isIdentMemberExpression,
    makeCommentedAst,
    makeVisitors,
    parsePattern,
    parseToEstree,
    parseToEstreeExpressionAt,
    parseJs,
    parseJsProgram,
    removeInitializerFromPattern,
};