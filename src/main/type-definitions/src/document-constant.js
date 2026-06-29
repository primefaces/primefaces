//@ts-check

const { findAndParseLastBlockComment } = require("./acorn-util");
const { Tags } = require("./constants");
const { createConstantDocs, createTypedefDocs } = require("./create-docs");
const { getTag, hasTag } = require("./doc-comments");
const { checkTagHasNoDescription } = require("./doc-comment-check-tags");
const { handleError, newConstantErrorMessage } = require("./error");
const { indentLines } = require("./lang");
const { createNamespace, getEmptyNamespaceSpec, typeToNamespacedName } = require("./ts-export");
const { toDeclareConstantSignature, toExportConstantSignature } = require("./ts-types");

/**
 * @param {ConstantDef} constantDefinition 
 * @param {SeveritySettingsConfig} severitySettings
 * @return {CodeConstant}
 */
function createCodeConstantFromNode(constantDefinition, severitySettings) {
    const jsdoc = findAndParseLastBlockComment(constantDefinition.comments, severitySettings);
    return {
        declaration: constantDefinition.declaration,
        jsdoc: jsdoc,
        name: constantDefinition.name,
        node: constantDefinition.node,
    };
}

/**
 * @param {ConstantDef} constantDefinition
 * @param {CodeConstant} constantDocs
 * @param {MessageFactory} factory
 * @param {number} indent
 * @param {SeveritySettingsConfig} severitySettings
 * @return {{name: string, namespace: IndentSpec | undefined}}
 */
function createNameAndNamespace(constantDefinition, constantDocs, factory, indent, severitySettings) {
    const constantTag = getTag(constantDocs.jsdoc, Tags.Const, Tags.Constant);
    if (constantTag === undefined && !hasTag(constantDocs.jsdoc, Tags.Type)) {
        handleError("tagMissingConstant", severitySettings, () => factory(`Constant ${constantDefinition.name} must be annotated with '@constant' or '@type', but no such tag was found in the doc comments`));
    }
    /** @type {NamespacedName | undefined} */
    let namespacedName = undefined;
    if (constantTag !== undefined) {
        checkTagHasNoDescription(constantTag, severitySettings, factory);
        namespacedName = typeToNamespacedName(constantTag.type || "");
    }
    const name = namespacedName && namespacedName.name && namespacedName.name.length > 0 ? namespacedName.name : (constantDefinition.name || "");
    const namespace = namespacedName && namespacedName.namespace && namespacedName.namespace.length > 0 ? namespacedName.namespace : constantDefinition.namespace || [];
    return {
        name: name,
        namespace: namespace.length > 0 ? createNamespace(namespace, indent, getEmptyNamespaceSpec()) : undefined,
    };
}

/**
 * 
 * @param {ConstantDef} constantDefinition 
 * @param {number} indent 
 * @param {SeveritySettingsConfig} severitySettings
 * @return {string[]}
 */
function documentConstant(constantDefinition, severitySettings, indent = 0) {
    const codeConstant = createCodeConstantFromNode(constantDefinition, severitySettings);
    /** @type {MessageFactory} */
    const factory = message => newConstantErrorMessage(message, codeConstant, constantDefinition.node);
    const { namespace, name } = createNameAndNamespace(constantDefinition, codeConstant, factory, indent, severitySettings); 
    codeConstant.name = name;

    const signatureConverter = namespace ? toExportConstantSignature : toDeclareConstantSignature;
    const constantDocResult = createConstantDocs(signatureConverter, codeConstant, severitySettings);

    /** @type {string[]} */
    const namespaceOpen = [];
    /** @type {string[]} */
    const namespaceClose = [];

    // Open namespace
    if (namespace) {
        indent += namespace.indent;
        namespaceOpen.push(...namespace.open);
    }

    // Create signature + docs for typedefs
    const typedefDocs = createTypedefDocs(constantDocResult.typedefs, severitySettings, undefined, indent);

    // Document constant
    const methodDocs = indentLines(constantDocResult.docs, indent);

    // Close namespace
    if (namespace) {
        indent -= namespace.indent;
        namespaceClose.push(...namespace.close);
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
    documentConstant,
};