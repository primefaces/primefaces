//@ts-check

const ts = require("typescript");

const { Tags } = require("./constants");
const { hasParseResultAnyDescription, hasTag } = require("./doc-comments");
const { newTsErrorMessage } = require("./error");
const tsx = require("./ts-utils");

/**
 * @param {TsValidationMessage[]} messages
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.ClassDeclaration | ts.ClassExpression | ts.InterfaceDeclaration} node 
 */
function handleType(messages, program, docCommentAccessor, node) {
    const jsdoc = docCommentAccessor.getClosestDocComment(node);
    const type = program.getTypeChecker().getTypeAtLocation(node);
    if (type.isClassOrInterface()) {
        const typeName = tsx.getTypeName(node);
        const hasInheritDoc = jsdoc && hasTag(jsdoc, Tags.InheritDoc);
        const heritageNodes = tsx.getAllHeritageNodes(program.getTypeChecker(), node);
        const hasHeritageTypes = heritageNodes.length > 0;
        const hasAnyParentDoc = heritageNodes.some(typeNode => {
            const doc = docCommentAccessor.getClosestDocComment(typeNode);
            return doc !== undefined && (hasTag(doc, Tags.InheritDoc) || hasParseResultAnyDescription(doc));
        });

        if (hasInheritDoc && !hasHeritageTypes) {
            messages.push({
                message: newTsErrorMessage(
                    `Type '${typeName}' specifies '@inheritdoc' in doc comments, but does not have any base types. Use '@extends' or '@implements' to specify at least one base type.`,
                    node,
                    typeName,
                ),
                severity: "error",
                type: "tsSuperfluousOverride",
            });
        }
        if (hasInheritDoc && !hasAnyParentDoc) {
            messages.push({
                message: newTsErrorMessage(
                    `Type '${typeName}' specifies '@inheritdoc' in doc comments, but no parent type has got a doc comment. Add a doc comment.`,
                    node,
                    typeName,
                ),
                severity: "error",
                type: "tsMissingParentDoc",
            });    
        }
    }
}

/**
 * @param {TsValidationMessage[]} messages
 * @param {import("typescript").Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {import("typescript").MethodDeclaration | ts.MethodSignature} node 
 */
function handleMethod(messages, program, docCommentAccessor, node) {
    const jsdoc = docCommentAccessor.getClosestDocComment(node);
    const typeName = tsx.getTypeName(node.parent);
    const propertyName = tsx.getPropertyName(node.name);
    
    const overriddenMethods = tsx.getOverridenMethodsRecursive(program.getTypeChecker(), node);
    const isMethodOverriding = overriddenMethods.size > 0;

    const hasOverride = jsdoc && hasTag(jsdoc, Tags.Override, Tags.Overrides);
    const hasInheritDoc = jsdoc && hasTag(jsdoc, Tags.InheritDoc);
    const hasOverrideOrInheritDoc = hasOverride || hasInheritDoc;

    const hasAnyParentDoc = Array
        .from(overriddenMethods.values())
        .some(methods => methods.some(method => {
            const doc = docCommentAccessor.getClosestDocComment(method);
            return doc !== undefined && (hasTag(doc, Tags.InheritDoc) || hasParseResultAnyDescription(doc));
        }));

    if (hasOverrideOrInheritDoc && !isMethodOverriding) {
        messages.push({
            message: newTsErrorMessage(
                `'@override' or '@inheritdoc found on property '${propertyName}' of type '${typeName}', but it does not override any parent methods. Remove these tags.`,
                node,
                typeName,
            ),
            severity: "error",
            type: "tsSuperfluousOverride",
        });
    }

    if (hasInheritDoc && !hasAnyParentDoc) {
        // Create list of overriden methods in the format 'Type#method'
        const overridenMethods = Array.from(
            overriddenMethods.keys(),
            type => `${tsx.getNameOfType(type)}#${propertyName}`
        );
        messages.push({
            message: newTsErrorMessage(
                `'@inheritdoc found on property '${propertyName}' of type '${typeName}', but none of the parent methods ${overridenMethods.join(", ")} have got a doc comment. Add a doc comment.`,
                node,
                typeName,
            ),
            severity: "error",
            type: "tsMissingParentDoc",
        });
    }

    if (!hasOverride && isMethodOverriding) {
        // Create list of overriden methods in the format 'Type#method'
        const overridenMethods = Array.from(
            overriddenMethods.keys(),
            type => `${tsx.getNameOfType(type)}#${propertyName}`
        );
        messages.push({
            message: newTsErrorMessage(
                `'@override' not found on method '${propertyName}' of type '${typeName}', but it does override the parent methods ${overridenMethods.join(", ")}. Add the '@override' tag.`,
                node,
                typeName,
            ),
            severity: "error",
            type: "tsMissingOverride",
        });
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
    else if (tsx.isMethodLike(node)) {
        handleMethod(messages, program, docCommentAccessor, node);
    }
    return true;
}

/**
 * @type {TsHookFnValidateProgram}
 */
const validateOverride = (program, sourceFiles, docCommentAccessor) => {
    /** @type {TsValidationMessage[]} */
    const messages = [];
    for (const sourceFile of sourceFiles) {
        tsx.depthFirstNode(sourceFile, node => processNode(messages, program, docCommentAccessor, node));
    }
    return messages;
};

module.exports = {
    validateOverride,
};