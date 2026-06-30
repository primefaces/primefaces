//@ts-check

const { findAndParseLastBlockComment } = require("./acorn-util");
const { Tags } = require("./constants");
const { createInvocationDocs, createTypedefDocs, } = require("./create-docs");
const { getTag } = require("./doc-comments");
const { checkTagHasNoDescription } = require("./doc-comment-check-tags");
const { handleError, newFunctionErrorMessage } = require("./error");
const { indentLines } = require("./lang");
const { createNamespace, createType, createNamespaceWithType, getEmptyNamespaceSpec, typeToNamespacedName } = require("./ts-export");
const { toDeclareFunctionSignature, toExportFunctionSignature, toObjectShorthandMethodSignature } = require("./ts-types");

/**
 * @param {FunctionDef} fnDefinition 
 * @param {SeveritySettingsConfig} severitySettings
 * @return {ObjectCodeMethod}
 */
function createCodeMethodFromFunction(fnDefinition, severitySettings) {
    const jsdoc = findAndParseLastBlockComment(fnDefinition.comments, severitySettings);
    return {
        jsdoc: jsdoc,
        method: fnDefinition.method,
        name: fnDefinition.name,
        node: fnDefinition.functionNode,
    };
}

/**
 * @param {FunctionDef} fnDefinition 
 * @param {ObjectCodeMethod} methodDocs
 * @param {MessageFactory} factory
 * @param {number} indent
 * @param {SeveritySettingsConfig} severitySettings
 * @return {{name: string, indentSpec: IndentSpec | undefined, isType: boolean}}
 */
function createNameAndIndentSpec(fnDefinition, methodDocs, factory, indent, severitySettings) {
    const fnTag = getTag(methodDocs.jsdoc, Tags.Func, Tags.Function);
    const typeTag = getTag(methodDocs.jsdoc, Tags.Interface, Tags.Class);
    if (fnTag === undefined) {
        handleError("tagMissingFunction", severitySettings, () => factory(`Function ${fnDefinition.name} must be annotated with @function, but no such tag was found in the doc comments`));
    }

    /** @type {NamespacedName | undefined} */
    let namespacedName = undefined;
    if (fnTag !== undefined) {
        checkTagHasNoDescription(fnTag, severitySettings, factory);
        namespacedName = typeToNamespacedName(fnTag.type || "");
    }
    const name = namespacedName && namespacedName.name && namespacedName.name.length > 0 ? namespacedName.name : (fnDefinition.name || "");
    const namespace = namespacedName && namespacedName.namespace && namespacedName.namespace.length > 0
            ? namespacedName.namespace
            : fnDefinition.namespace || [];


    /** @type {NamespacedName} */
    let namespacedNameType = {name: name, namespace: []};
    if (typeTag !== undefined) {
        if (typeTag.type && typeTag.type.length > 0) {
            namespacedNameType = typeToNamespacedName(typeTag.type);
        }
        else {
            namespacedNameType = {
                name: namespace[namespace.length - 1] || name,
                namespace: namespace.slice(0, -1),
            }
        }
    }

    if (typeTag !== undefined && fnDefinition.spec) {
        return {
            isType: true,
            name: name,
            indentSpec: namespacedNameType.namespace.length > 0
                ? createNamespaceWithType(namespacedNameType, 0, fnDefinition.spec, getEmptyNamespaceSpec())
                : createType(namespacedNameType.name, indent, fnDefinition.spec, false),
        };
    }
    else {
        return {
            isType: false,
            name: name,
            indentSpec: namespace.length > 0 ? createNamespace(namespace, indent, getEmptyNamespaceSpec()) : undefined,
        };
    }
}

/**
 * 
 * @param {FunctionDef} fnDefinition 
 * @param {SeveritySettingsConfig} severitySettings
 * @param {number} indent 
 * @return {string[]}
 */
function documentFunction(fnDefinition, severitySettings, indent = 0) {
    const codeMethod = createCodeMethodFromFunction(fnDefinition, severitySettings);
    /** @type {MessageFactory} */
    const factory = message => newFunctionErrorMessage(message, codeMethod, fnDefinition.functionNode);
    const { isType, indentSpec, name } = createNameAndIndentSpec(fnDefinition, codeMethod, factory, indent, severitySettings);
    codeMethod.name = name;

    // If interface or class, use object shorthand method "foo(){}"
    // If inside a namespace, use an export "export function foo(){}"
    // Otherwise, just declare "function foo(){}"
    const signatureConverter = isType
        ? toObjectShorthandMethodSignature
        : indentSpec
            ? toExportFunctionSignature
            : toDeclareFunctionSignature;

    const methodDocResult = createInvocationDocs(signatureConverter, codeMethod, severitySettings);

    /** @type {string[]} */
    const namespaceOpen = [];
    /** @type {string[]} */
    const namespaceClose = [];

    // Open namespace and/or type
    if (indentSpec) {
        indent += indentSpec.indent;
        namespaceOpen.push(...indentSpec.open);
    }

    // Create signature + docs for typedefs
    const typedefDocs = createTypedefDocs(methodDocResult.typedefs, severitySettings, undefined, indent);

    // Document function
    const methodDocs = indentLines(methodDocResult.docs, indent);

    // Close namespace and/or type
    if (indentSpec) {
        indent -= indentSpec.indent;
        namespaceClose.push(...indentSpec.close);
    }

    return [
        ...typedefDocs.external,
        ...namespaceOpen,
        ...typedefDocs.internal,
        ...methodDocs,
        ...namespaceClose,
    ];
}

module.exports = {
    documentFunction,
};