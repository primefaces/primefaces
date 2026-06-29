//@ts-check

// Inspects each type with a @validateForcedProps comment
// Checks that it does not declare any properties that are
// not contained in the props specified by the tag.

const ts = require("typescript");

const { Tags } = require("./constants");
const { getTag, removeTags } = require("./doc-comments");
const { newTsErrorMessage } = require("./error");
const { setDiff, isNotUndefined, parseJsonOrDefault, mergingMapValues, mergingSets } = require("./lang");
const { combiningObjectDocPropertyData } = require("./scan-method-for-this-properties");
const tsx = require("./ts-utils");

/**
 * @param {ts.InterfaceType} type 
 * @param {boolean} omitMethods
 * @returns {Set<string>}
 */
function getActualProps(type, omitMethods) {
    /** @type {Set<string>} */
    const props = new Set();

    for (const apparentProp of type.getApparentProperties()) {
        if (!omitMethods || !tsx.isMethodSymbol(apparentProp)) {
            props.add(apparentProp.name);
        }
    }
    for (const prop of type.getProperties()) {
        if (!omitMethods || !tsx.isMethodSymbol(prop)) {
            props.add(prop.name);
        }
    }

    return props;
}

/**
 * @param {import("comment-parser").Comment} jsdoc
 * @returns {Map<string, ObjectDocPropertyData> | undefined}
 */
function getDefinitiveProps(jsdoc) {
    const tag = getTag(jsdoc, Tags.ValidateDefinitiveProps);
    if (tag !== undefined) {
        const value = tag.description;
        /** @type {Record<string, ObjectDocPropertyData>} */
        const items = JSON.parse(value);
        return new Map(Object.entries(items));
    }
    else {
        return undefined;
    }
}

/**
 * @param {import("comment-parser").Comment} jsdoc
 * @returns {Set<string>}
 */
function getForcedProps(jsdoc) {
    const tag = getTag(jsdoc, Tags.ValidateForcedProps);
    const value = tag?.description ?? "[]";
    /** @type {string[]} */
    const items = parseJsonOrDefault(value, []);
    return new Set(items);
}

/**
 * @template T
 * @template S
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.ClassDeclaration | ts.ClassExpression | ts.InterfaceDeclaration} node 
 * @param {(jsdoc: import("comment-parser").Comment) => T | undefined} extractor
 * @param {Reducer<T, S>} combiner
 * @returns {S}
 */
function getDocDataFromAllHeritageTypes(program, docCommentAccessor, node, extractor, combiner) {
    const heritageNodes = [node, ...tsx.getAllHeritageNodesRecursive(program.getTypeChecker(), node)];
    return heritageNodes
        .map(heritageNode => {
            const jsdoc = docCommentAccessor.getClosestDocComment(heritageNode);
            return jsdoc !== undefined ? extractor(jsdoc) : undefined;
        })
        .filter(isNotUndefined)
        .reduce(...combiner);
}

/**
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.ClassDeclaration | ts.ClassExpression | ts.InterfaceDeclaration} node 
 * @returns {Map<string, ObjectDocPropertyData>}
 */
function getAllDefinitiveProps(program, docCommentAccessor, node) {
    return getDocDataFromAllHeritageTypes(
        program, docCommentAccessor, node,
        getDefinitiveProps,
        mergingMapValues(combiningObjectDocPropertyData())
    );
}

/**
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.ClassDeclaration | ts.ClassExpression | ts.InterfaceDeclaration} node 
 * @returns {Set<string>}
 */
 function getAllForcedProps(program, docCommentAccessor, node) {
    return getDocDataFromAllHeritageTypes(
        program, docCommentAccessor, node,
        getForcedProps,
        mergingSets()
    );
}

/**
 * 
 * @param {Set<string>} missingProps 
 * @param {Map<string, ObjectDocPropertyData>} definitiveProps 
 * @returns {string}
 */
function createMissingPropStackTrace(missingProps, definitiveProps) {
    return [...missingProps]
        .sort()
        .map(name => {
            const data = definitiveProps.get(name);
            const locations = data !== undefined ? data.location.map(loc => {
                return loc.nodes.length > 0
                    ? loc.nodes.map(node => `    declared at ${loc.name} (${loc.sourceFile}:${node.loc?.startLine ?? 1}:${1 + (node.loc?.startColumn ?? 0)})`).join("\n")
                    : `    declared at ${loc.name} (${loc.sourceFile}:1:1)`;
            }) : [];
            return locations.length > 0
                ? `  Property <${name}>\n${locations.join("\n")}`
                : `  Property <${name}> declared at <unknown>`;
        })
        .join("\n");
}

/**
 * @param {Map<string, ObjectDocPropertyData>} definitiveProps
 * @param {Set<string>} definitivePropsSet 
 * @param {Set<string>} actualProps 
 * @param {TsValidationMessage[]} messages
 * @param {ts.ClassDeclaration | ts.ClassExpression | ts.InterfaceDeclaration} node 
 */
function validatePropsWithMissingDoc(definitiveProps, definitivePropsSet, actualProps, messages, node) {
    const typeName = tsx.getTypeName(node);
    const missingProps = setDiff(definitivePropsSet, actualProps);
    if (missingProps.size > 0) {
        const propsString = createMissingPropStackTrace(missingProps, definitiveProps);
        messages.push({
            message: newTsErrorMessage(
                `Code for type '${typeName}' accesses property, but no doc comment exists. Add a doc comment. Missing properties:\n${propsString}`,
                node,
                typeName,
            ),
            severity: "error",
            type: "propDocMissing",
        });
    };
}

/**
 * @param {TsValidationMessage[]} messages
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.ClassDeclaration | ts.ClassExpression | ts.InterfaceDeclaration} node 
 * @param {ts.InterfaceType} type
 * @param  {import("comment-parser").Comment} jsdoc
 */
function validatePropsMissingCode(messages, program, docCommentAccessor, node, type, jsdoc) {
    const actualPropsWithoutMethods = getActualProps(type, true);
    const allDefinitiveProps = getAllDefinitiveProps(program, docCommentAccessor, node);
    const allForcedProps = getAllForcedProps(program, docCommentAccessor, node);

    for (const actualProp of actualPropsWithoutMethods) {
        if (!allDefinitiveProps.has(actualProp) && !allForcedProps.has(actualProp)) {
            const typeName = tsx.getTypeName(node);
            const sourceFile = node.getSourceFile();
            let stackTrace;
            if (sourceFile !== undefined) {
                const pos = ts.getLineAndCharacterOfPosition(sourceFile, node.pos)
                stackTrace = `  at ${node.name?.text ?? "<unknown>"} (${sourceFile.fileName}:${pos.line}:${pos.character})`;
            }
            else {
                stackTrace = `  at <unknown>`;
            }
            messages.push({
                message: newTsErrorMessage(
                    `Type ${typeName} declares property <${actualProp}>, but no such property is used in the code. Remove the doc comment, or use @forcedProp instead of @prop if you really want to include it.\n${stackTrace}`,
                    node,
                    typeName,
                ),
                severity: "error",
                type: "propDocSuperfluous",
            });
        }
    }
}

/**
 * @param {TsValidationMessage[]} messages
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.ClassDeclaration | ts.ClassExpression | ts.InterfaceDeclaration} node 
 */
function postHandleType(messages, program, docCommentAccessor, node) {
    // Remove validate tags
    const jsdoc = docCommentAccessor.getClosestDocComment(node);
    if (jsdoc !== undefined) {
        removeTags(jsdoc, Tags.ValidateDefinitiveProps, Tags.ValidateForcedProps);
        docCommentAccessor.replaceClosestDocComment(node, jsdoc);
    }
}

/**
 * @param {TsValidationMessage[]} messages
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.ClassDeclaration | ts.ClassExpression | ts.InterfaceDeclaration} node 
 */
function handleType(messages, program, docCommentAccessor, node) {
    const jsdoc = docCommentAccessor.getClosestDocComment(node);
    const type = program.getTypeChecker().getTypeAtLocation(node);

    if (jsdoc !== undefined && type.isClassOrInterface()) {
        const actualProps = getActualProps(type, false);
        const definitiveProps = getDefinitiveProps(jsdoc);
        const definitivePropsSet = definitiveProps !== undefined ? new Set([...definitiveProps.keys()]) : undefined;

        if (definitiveProps !== undefined && definitivePropsSet !== undefined) {
            // Check for props declared in the code but for which no doc comment exists
            validatePropsWithMissingDoc(definitiveProps, definitivePropsSet, actualProps, messages, node);

            // Check for props for which a doc comment exists, but that was not declared in the code
            validatePropsMissingCode(messages, program, docCommentAccessor, node, type, jsdoc);
        }
    }
}

/**
 * @param {TsValidationMessage[]} messages
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.Node} node 
 * @return {boolean}
 */
function processNode(messages, program, docCommentAccessor, node) {
    if (tsx.isTypeLike(node)) {
        handleType(messages, program, docCommentAccessor, node);
    }
    return true;
}

/**
 * @param {TsValidationMessage[]} messages
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.Node} node 
 * @return {boolean}
 */
function postProcessNode(messages, program, docCommentAccessor, node) {
    if (tsx.isTypeLike(node)) {
        postHandleType(messages, program, docCommentAccessor, node);
    }
    return true;
}

/**
 * @type {TsHookFnValidateProgram}
 */
const validateDeclaredProperties = (program, sourceFiles, docCommentAccessor) => {
    /** @type {TsValidationMessage[]} */
    const messages = [];
    for (const sourceFile of sourceFiles) {
        tsx.depthFirstNode(sourceFile, node => processNode(messages, program, docCommentAccessor, node));
    }
    for (const sourceFile of sourceFiles) {
        tsx.depthFirstNode(sourceFile, node => postProcessNode(messages, program, docCommentAccessor, node));
    }
    return messages;
};

module.exports = {
    validateDeclaredProperties,
};

