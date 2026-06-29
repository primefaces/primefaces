//@ts-check

// methods for working with export signatures, eg.
//
// declare namespace { ... }
// export interface { ... }
// export class { ... }
// declare const ... 

const { createDocComment } = require("./doc-comments");
const { createIndent, indentLines, isNotEmpty, strJoin } = require("./lang");
const { createGenericsSignature } = require("./ts-types");

let counterDefaultName = 0;

/**
 * @param {string} base 
 * @return {string}
 */
function createDefaultName(base) {
    base = base || "generated";
    counterDefaultName += 1;
    return `${base}$$${counterDefaultName}`;
}

/**
 * @param {string[]} namespace
 * @param {number} indent
 * @param {NamespaceSpec} spec
 * @return {IndentSpec}
 */
function createNamespace(namespace, indent, spec) {
    const docs = spec.description.length > 0 || spec.additionalTags.length > 0 ? createDocComment(spec.description, spec.additionalTags) : [];
    const open = `${createIndent(indent)}declare namespace ${namespace.map(part => part || createDefaultName("namespace")).join(".")} {`;
    const close = `${createIndent(indent)}}`;
    return {
        close: [close],
        indent: 1,
        open: [
            ...indentLines(docs, indent),
            open,
        ],
    };
}

/**
 * @param {string} name
 * @param {number} indent
 * @param {TypeSpec} spec
 * @param {boolean} allowExport
 * @return {IndentSpec}
 */
function createType(name, indent, spec, allowExport) {
    const type = spec.type && spec.type !== "unspecified" ? spec.type : "interface";
    name = name || createDefaultName(type);
    
    const extendedTypes = spec.extends ? Array.from(spec.extends).filter(isNotEmpty).sort() : [];
    const implementedTypes = spec.implements ? Array.from(spec.implements).filter(isNotEmpty).sort() : [];

    const abstract = spec.abstract ? "abstract " : "";
    const docs = spec.description.length > 0 || spec.additionalTags.length > 0 ? createDocComment(spec.description, spec.additionalTags) : [];
    const generics = spec.generics ? createGenericsSignature(spec.generics) : "";
    const extendDecl = extendedTypes.length > 0 ? ` extends ${strJoin(extendedTypes, ", ")}` : "";
    const implDecl = implementedTypes.length > 0 ? ` implements ${strJoin(implementedTypes, ", ")}` : "";
    const exportDecl = allowExport ? "export " : ""; 
    const open = `${createIndent(indent)}${exportDecl}${abstract}${type} ${name}${generics}${extendDecl}${implDecl} {`;
    const close = `${createIndent(indent)}}`;
    return {
        close: [close],
        indent: 1,
        open: [
            ...indentLines(docs, indent),
            open,
        ],
    };
}

/**
 * @param {NamespacedName} namespacedName
 * @param {number} indent
 * @param {TypeSpec} ifaceSpec
 * @param {NamespaceSpec} namespaceSpec
 * @return {IndentSpec}
 */
function createNamespaceWithType(namespacedName, indent, ifaceSpec, namespaceSpec) {
    const {close: nspaceClose, indent: nspaceIndent, open: nspaceOpen} = createNamespace(namespacedName.namespace, indent, namespaceSpec);
    const {close: ifaceClose, indent: ifaceIndent, open: ifaceOpen} = createType(namespacedName.name, indent + nspaceIndent, ifaceSpec, true);
    return {
        close: [
            ...ifaceClose,
            ...nspaceClose,
        ],
        indent: nspaceIndent + ifaceIndent,
        open: [
            ...nspaceOpen,
            ...ifaceOpen,
        ],
    };
}

/**
 * @param {string} type
 * @return {NamespacedName}
 */
function typeToNamespacedName(type) {
    const parts = type.split(".");
    return {
        name: parts[parts.length - 1] || "",
        namespace: parts.slice(0, -1),
    };
}

/**
 * @return {NamespaceSpec}
 */
function getEmptyNamespaceSpec() {
    return {
        additionalTags: [],
        description: "",
    };
}

module.exports = {
    createType,
    createNamespace,
    createNamespaceWithType,
    getEmptyNamespaceSpec,
    typeToNamespacedName,
}