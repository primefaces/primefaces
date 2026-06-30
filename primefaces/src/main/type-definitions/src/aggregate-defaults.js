//@ts-check

const { findAndParseLastBlockComment, getIdentMemberPath } = require("./acorn-util");
const { Tags } = require("./constants");
const { getTag, getTagDescriptionWithName, hasTag } = require("./doc-comments");
const { assertNever } = require("./lang");
const { typeToNamespacedName } = require("./ts-export");


/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").AssignmentExpression} node
 * @param {CommentedAst<import("estree").Program>} program
 * @param {SeveritySettingsConfig} severitySettings
 * @return {DocumentableHandlerResult | undefined}
 */
function handleAssignmentExpression(inclusionHandler, node, program, severitySettings) {
    const path = getIdentMemberPath(node.left);
    if (path && path.length > 0) {
        const namespacedName = typeToNamespacedName(path.join("."));
        const commentData = program.map.get(node);
        if (node.right.type === "FunctionExpression") {
            return createFunctionHandlerResult(inclusionHandler, node.right, undefined, namespacedName, commentData, severitySettings);
        }
        else if (node.right.type === "ObjectExpression") {
            return createTypeHandlerResult(inclusionHandler, node.right, namespacedName, commentData, severitySettings);
        }
        else {
            return createConstantHandlerResult(inclusionHandler, node.right, namespacedName, commentData, severitySettings);
        }
    }
    else {
        return undefined;
    }
}

/**
 * @param {DocumentableHandlerResult} handlerResult 
 * @param {ConstantDef | ObjectDef | FunctionDef | undefined} newDeclaration 
 */
function appendResult(handlerResult, newDeclaration) {
    if (newDeclaration !== undefined) {
        switch (newDeclaration.kind) {
            case "constant":
                handlerResult.constants.push(newDeclaration);
                break;
            case "function":
                handlerResult.functions.push(newDeclaration);
                break;
            case "object":
                handlerResult.objects.push(newDeclaration);
                break;
            default:
                assertNever(newDeclaration);
        }
    }
}

/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").VariableDeclaration} node
 * @param {CommentedAst<import("estree").Program>} program
 * @param {SeveritySettingsConfig} severitySettings
 * @return {DocumentableHandlerResult | undefined}
 */
function handleVariableDeclaration(inclusionHandler, node, program, severitySettings) {
    const kind = node.kind;
    const topCommentData = program.map.get(node);
    /** @type {DocumentableHandlerResult} */
    const handlerResult = {
        constants: [],
        functions: [],
        objects: [],
        recurse: false,
    }
    node.declarations.forEach((declarator, index) => {
        const commentData = program.map.get(declarator) || (index === 0 ? topCommentData : undefined);
        if (declarator.id.type === "Identifier") {
            const namespacedName = {
                name: declarator.id.name,
                namespace: [],
            };
            if (declarator.init && declarator.init.type === "FunctionExpression") {
                const fn = createFunction(inclusionHandler, declarator.init, undefined, namespacedName, commentData, severitySettings);
                appendResult(handlerResult, fn);
            }
            else if (declarator.init && declarator.init.type === "ObjectExpression") {
                const type = createType(inclusionHandler, declarator.init, namespacedName, commentData, kind, severitySettings);
                appendResult(handlerResult, type);
            }
            else {
                const constant = createConstant(inclusionHandler, declarator, node, namespacedName, commentData, kind, severitySettings);
                appendResult(handlerResult, constant);
            }
        }
    });
    return handlerResult;
}


/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").FunctionDeclaration | import("estree").FunctionExpression} node
 * @param {import("estree").MethodDefinition | undefined} method
 * @param {CommentedAst<import("estree").Program>} program
 * @param {SeveritySettingsConfig} severitySettings
 * @return {DocumentableHandlerResult | undefined}
 */
function handleFunctionDeclaration(inclusionHandler, node, method, program, severitySettings) {
    return createFunctionHandlerResult(inclusionHandler, node, method, {
        name: node.id ? node.id.name || "" : "",
        namespace: [],
    }, program.map.get(node), severitySettings);
}

/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").FunctionDeclaration | import("estree").FunctionExpression} node
 * @param {import("estree").MethodDefinition | undefined} method
 * @param {NamespacedName} namespacedName
 * @param {CommentData[] | undefined} commentData
 * @param {SeveritySettingsConfig} severitySettings
 * @return {FunctionDef | undefined}
 */
function createFunction(inclusionHandler, node, method, namespacedName, commentData, severitySettings) {
    if (commentData === undefined || commentData.length === 0) {
        return undefined;
    }
    const jsdoc = findAndParseLastBlockComment(commentData, severitySettings);
    if (!hasTag(jsdoc, Tags.Func, Tags.Function)) {
        return undefined;
    }
    if (!inclusionHandler.isIncludeFunction(jsdoc, namespacedName.name)) {
        return undefined;
    }
    const typeTag = getTag(jsdoc, Tags.Interface, Tags.Class);
    /** @type {TypeSpec | undefined} */
    const spec = typeTag ? {
        abstract: false,
        additionalTags: [],
        description: getTagDescriptionWithName(typeTag),
        extends: new Set(),
        generics: [],
        implements: new Set(),
        type: typeTag.tag === Tags.Interface ? "interface" : "class",
    } : undefined;
    return {
        comments: commentData || [],
        method: method,
        functionNode: node,
        kind: "function",
        spec: spec,
        name: namespacedName.name,
        namespace: namespacedName.namespace,
    }
}

/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").FunctionDeclaration | import("estree").FunctionExpression} node
 * @param {import("estree").MethodDefinition | undefined} method
 * @param {NamespacedName} namespacedName
 * @param {CommentData[] | undefined} commentData
 * @param {SeveritySettingsConfig} severitySettings
 * @return {DocumentableHandlerResult | undefined}
 */
function createFunctionHandlerResult(inclusionHandler, node, method, namespacedName, commentData, severitySettings) {
    const functionDef = createFunction(inclusionHandler, node, method, namespacedName, commentData, severitySettings);
    if (functionDef === undefined) {
        return undefined;
    }
    else {
        return {
            constants: [],
            functions: [functionDef],
            objects: [],
            recurse: false,
        }
    }
}

/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").Node} node
 * @param {import("estree").VariableDeclaration | undefined} declaration
 * @param {NamespacedName} namespacedName
 * @param {CommentData[] | undefined} commentData
 * @param {"var" | "let" | "const" | undefined} kind
 * @param {SeveritySettingsConfig} severitySettings
 * @return {ConstantDef | undefined}
 */
function createConstant(inclusionHandler, node, declaration, namespacedName, commentData, kind, severitySettings) {
    if (commentData === undefined || commentData.length === 0) {
        return undefined;
    }
    const jsdoc = findAndParseLastBlockComment(commentData, severitySettings);
    if (!hasTag(jsdoc, Tags.Type)) {
        return undefined;
    }
    if (!inclusionHandler.isIncludeVariable(jsdoc, namespacedName.name)) {
        return undefined;
    }
    return {
        comments: commentData || [],
        declaration: declaration,
        kind: "constant",
        name: namespacedName.name,
        namespace: namespacedName.namespace,
        node: node,
        variableKind: kind === "const" ? "constant" : kind === "var" || kind === "let" ? "let" : undefined,
    };
}

/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").Node} node
 * @param {NamespacedName} namespacedName
 * @param {CommentData[] | undefined} commentData
 * @param {SeveritySettingsConfig} severitySettings
 * @return {DocumentableHandlerResult | undefined}
 */
function createConstantHandlerResult(inclusionHandler, node, namespacedName, commentData, severitySettings) {
    const constant = createConstant(inclusionHandler, node, undefined, namespacedName, commentData, undefined, severitySettings);
    if (constant === undefined) {
        return undefined;
    }
    else {
        return {
            constants: [constant],
            functions: [],
            objects: [],
            recurse: false,
        };
    }
}


/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").ObjectExpression} node
 * @param {NamespacedName} namespacedName
 * @param {CommentData[] | undefined} commentData
 * @param { "var" | "let" | "const" | undefined} kind
 * @param {SeveritySettingsConfig} severitySettings
 * @return {ObjectDef | ConstantDef | undefined}
 */
function createType(inclusionHandler, node, namespacedName, commentData, kind, severitySettings) {
    if (commentData === undefined || commentData.length === 0) {
        return undefined;
    }
    const jsdoc = findAndParseLastBlockComment(commentData, severitySettings);
    if (!hasTag(jsdoc, Tags.Class, Tags.Interface, Tags.Namespace)) {
        return createConstant(inclusionHandler, node, undefined, namespacedName, commentData, kind, severitySettings);
    }
    if (!inclusionHandler.isIncludeType(jsdoc, namespacedName.name)) {
        return undefined;
    }
    return {
        classDefinition: node,
        comments: commentData,
        kind: "object",
        name: namespacedName.name,
        namespace: namespacedName.namespace,
        spec: {
            abstract: false,
            additionalTags: [],
            description: "",
            extends: new Set(),
            generics: [],
            implements: new Set(),
            type: "interface",
        },
    };
}

/**
 * @param {InclusionHandler} inclusionHandler
 * @param {import("estree").ObjectExpression} node
 * @param {NamespacedName} namespacedName
 * @param {CommentData[] | undefined} commentData
 * @param {SeveritySettingsConfig} severitySettings
 * @return {DocumentableHandlerResult | undefined}
 */
function createTypeHandlerResult(inclusionHandler, node, namespacedName, commentData, severitySettings) {
    const result = createType(inclusionHandler, node, namespacedName, commentData, "const", severitySettings);
    if (result === undefined) {
        return undefined;
    }
    else if (result.kind === "object") {
        return {
            constants: [],
            functions: [],
            objects: [result],
            recurse: false,
        };
    }
    else {
        return {
            constants: [result],
            functions: [],
            objects: [],
            recurse: false,
        };
    }
}

/** @type {DocumentableHandler} */
const aggregateHandlerDefaults = (node, program, inclusionHandler, severitySettings) => {
    switch (node.type) {
        case "AssignmentExpression":
            return handleAssignmentExpression(inclusionHandler, node, program, severitySettings);
        case "MethodDefinition":
            return handleFunctionDeclaration(inclusionHandler, node.value, node, program, severitySettings);
        case "FunctionDeclaration":
            return handleFunctionDeclaration(inclusionHandler, node, undefined, program, severitySettings);
        case "VariableDeclaration":
            return handleVariableDeclaration(inclusionHandler, node, program, severitySettings);
        default:
            return undefined;
    }
};

module.exports = {
    aggregateHandlerDefaults,
};