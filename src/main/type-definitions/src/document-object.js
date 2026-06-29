//@ts-check

const { makeNamespacedTypeForObject } = require("./create-code-info-object");
const { parseObjectComment } = require("./create-docs");
const { createObjectDocInfo } = require("./create-doc-info-object");
const { createDocComment } = require("./doc-comments");
const { handleError, newMethodErrorMessage, newPropErrorMessage } = require("./error");
const { createConstDocs, createCommentDocs, createInvocationDocs, createPropertyDocs, createTypedefDocs } = require("./create-docs");
const { compareBy, indentLines, strJoin } = require("./lang");
const { parseObjectCode } = require("./parse-object-code");
const { toExportFunctionSignature, toObjectShorthandMethodSignature, toExportPropSignature, toObjectPropSignature } = require("./ts-types");

/**
 * @param {string[]} propertyDocs 
 * @param {string[]} methodDocs 
 * @param {number} indent
 * @return {string[]}
 */
function createCodeDoc(propertyDocs, methodDocs, indent) {
    /** @type {string[]} */
    const lines = [];
            
    lines.push(...indentLines(propertyDocs, indent));
    lines.push(...indentLines(methodDocs, indent));

    return lines;
}

/**
 * @param {ObjectCode} objectCode
 * @param {ObjectDocInfo} objectDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function validateObjectCodeAgainstComments(objectCode, objectDocInfo, severitySettings) {
    const methodMap = new Map(objectCode.methods.map(x => [x.name, x]));
    const propMap = new Map(objectCode.properties.map(x => [x.name, x]));
    for (const name of objectDocInfo.shape.props.keys()) {
        const codeMethod = methodMap.get(name);
        const codeProp = propMap.get(name);
        if (codeMethod !== undefined) {
            handleError("tagConflictingMethodInComments", severitySettings, () => newMethodErrorMessage(`Code already specifies method '${name}', but found additional '@method ${name}' or '@prop ${name}' in doc comments`, codeMethod));
            objectDocInfo.shape.props.delete(name);
        }
        if (codeProp !== undefined) {
            handleError("tagConflictingPropInComments", severitySettings, () => newPropErrorMessage(`Code already specifies property '${name}', but found additional '@method ${name}' or '@prop ${name}' in doc comments`, codeProp));
            objectDocInfo.shape.props.delete(name);
        }
    }
}

/**
 * 
 * @param {PropDocResult[]} propertyDocs 
 * @param {CommentedAst<import("estree").Program>} program
 * @param {string[]} namespace
 * @param {InclusionHandler} inclusionHandler
 * @param {SeveritySettingsConfig} severitySettings
 * @return {string[]}
 */
function documentSubPropertyObjects(propertyDocs, program, namespace, inclusionHandler, severitySettings) {
    /** @type {string[]} */
    const lines = [];
    for (const propertyDoc of propertyDocs) {
        if (propertyDoc.subObject !== undefined) {
            const comments = program.map.get(propertyDoc.subObject.propertyNode);
            if (comments !== undefined && comments.length > 0) {
                lines.push(...documentObject({
                    classDefinition: propertyDoc.subObject.objectNode,
                    comments: [{
                        content: createDocComment(propertyDoc.subObject.description, propertyDoc.subObject.tags).join("\n"),
                        end: comments[0].start,
                        isBlockComment: true,
                        start: comments[0].end,
                    }],
                    kind: "object",
                    name: propertyDoc.subObject.name,
                    namespace: namespace,
                    spec: {
                        abstract: false,
                        additionalTags: [],
                        description: "",
                        extends: new Set(),
                        generics: [],
                        implements: new Set(),
                        type: "unspecified",
                    }        
                }, program, inclusionHandler, severitySettings, 0));
            }
        }
    }
    return lines;
}

/**
 * @param {ObjectDef} objectDefinition 
 * @param {CommentedAst<import("estree").Program>} program
 * @param {number} indent
 * @param {SeveritySettingsConfig} severitySettings
 * @param {InclusionHandler} inclusionHandler
 * @return {string[]}
 */
function documentObject(objectDefinition, program, inclusionHandler, severitySettings, indent = 0) {
    const objectCode = parseObjectCode(objectDefinition, program, inclusionHandler, severitySettings);
    if (objectCode) {
        // Look at the properties and methods of the object
        const objectDocInfo = createObjectDocInfo(objectCode.jsdoc, objectCode.node, severitySettings);

        // Check for duplicate properties or methods in doc comments
        validateObjectCodeAgainstComments(objectCode, objectDocInfo, severitySettings);

        const {
            namespaceOnly,
            namespacedType: {
                namespaceSpec,
                ifaceSpec,
                name,
                namespace
            }
        } = makeNamespacedTypeForObject(objectDefinition, objectDocInfo, indent);

        // Create docs for properties
        const propertyDocs = objectCode.properties
            .sort(compareBy(x => x.name))
            .flatMap(x => createPropertyDocs(namespaceOnly ? toExportPropSignature : toObjectPropSignature, x, severitySettings));
        // Create docs for methods
        const methodDocs = objectCode.methods
            .sort(compareBy(x => x.name))
            .flatMap(x => createInvocationDocs(namespaceOnly ? toExportFunctionSignature : toObjectShorthandMethodSignature, x, severitySettings));
        // Create docs for comments
        const commentDocInfo = parseObjectComment(objectDocInfo, objectCode.node, severitySettings);

        /** @type {string[]} */
        const namespaceOpen = [];
        /** @type {string[]} */
        const namespaceClose = [];
        /** @type {string[]} */
        const ifaceOpen = [];
        /** @type {string[]} */
        const ifaceClose = [];

        // Constant exports
        const constDocs = [];
        for (const constInfo of objectDocInfo.shape.constants) {
            if (name) {
                const constType = namespace.length > 0 ? strJoin([...namespace, name], ".") : name;
                constDocs.push(...createConstDocs(constInfo, constType, 0));
            }
        }

        // Open namespace
        if (namespaceSpec) {
            indent += namespaceSpec.indent;
            namespaceOpen.push(...namespaceSpec.open);
        }

        // Create signature + docs for typedefs
        const typedefDocs = createTypedefDocs([
            ...methodDocs.flatMap(x => x.typedefs),
            ...propertyDocs.flatMap(x => x.typedefs),
            ...objectDocInfo.typedefs
        ], severitySettings, undefined, indent);

        // Open iface or class
        if (ifaceSpec) {
            indent += ifaceSpec.indent;
            ifaceOpen.push(...ifaceSpec.open);
        }
        
        // Create docs with namespace from properties & methods
        const codeDocs = createCodeDoc(propertyDocs.flatMap(x => x.docs), methodDocs.flatMap(x => x.docs), indent);
        // Create docs with namespace from object comments
        const commentDocs = createCommentDocs(commentDocInfo, objectCode.node, undefined, severitySettings, indent);

        // Close iface or class
        if (ifaceSpec) {
            indent -= ifaceSpec.indent;
            ifaceClose.push(...ifaceSpec.close);
        }
        // Close namespace
        if (namespaceSpec) {
            indent -= namespaceSpec.indent;
            namespaceClose.push(...namespaceSpec.close);
        }

        // Create docs for sub objects
        const subPropertyDocs = documentSubPropertyObjects(propertyDocs, program, namespace, inclusionHandler, severitySettings);

        return [
            ...typedefDocs.external,
            ...namespaceOpen,
            ...typedefDocs.internal,
            ...ifaceOpen,
            ...commentDocs.internal,
            ...codeDocs,
            ...ifaceClose,
            ...namespaceClose,
            ...commentDocs.external,
            ...propertyDocs.flatMap(x => x.external),
            ...constDocs,
            ...subPropertyDocs,
        ];
    }
    else {
        return [];
    }
}

module.exports = {
    documentObject,
}