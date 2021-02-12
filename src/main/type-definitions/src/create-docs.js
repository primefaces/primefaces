//@ts-check

const { Tags } = require("./constants");
const { createMethodCodeInfo } = require("./create-code-info-method");
const { createMethodDocInfoFromTags, createMethodCodeInfoFromTags } = require("./create-code-info-object");
const { createConstantCodeInfo } = require("./create-code-info-constant");
const { createConstantDocInfo } = require("./create-doc-info-constant");
const { createPropCodeInfo } = require("./create-code-info-prop");
const { createMethodDocInfo } = require("./create-doc-info-method");
const { createPropDocInfo } = require("./create-doc-info-prop");
const { createDocComment, createTag } = require("./doc-comments");
const { handleError, newNodeErrorMessage, newPropErrorMessage } = require("./error");
const { compareBy, createIndent, indentLines, strJoin } = require("./lang");
const { mergeAndValidateMethodData } = require("./merge-validate-method-data");
const { mergeAndValidateConstantData } = require("./merge-validate-constant-data");
const { mergeAndValidatePropData } = require("./merge-and-validate-prop-data");
const { createNamespace, createType, createNamespaceWithType, getEmptyNamespaceSpec, typeToNamespacedName } = require("./ts-export");
const { createTagForTypeParameter, createTagForParameter, createTagForPositionalParameter } = require("./create-tags");
const {
    createConstantSignature,
    createGenericsSignature,
    createMethodSignature,
    createPropSignature,
    toAnonymousMethodSignature,
    toLambdaMethodSignature,
    toObjectShorthandMethodSignature,
    toObjectPropSignature,
    toExportFunctionSignature,
    toExportPropSignature
} = require("./ts-types");

/**
 * @param {PropCodeInfo} propCodeInfo
 * @param {PropDocInfo} propDocInfo
 * @return {CodeWithDocComment}
 */
function generatePropertyDoc(propCodeInfo, propDocInfo) {
    // Part 1: create doc comments
    const propDocComment = createPropDocComment(propDocInfo);
    // Part 2: create property signature
    const propSignature = createPropSignature(propCodeInfo);
    return {
        doc: propDocComment, 
        signature: propSignature,
    };
}

/**
 * @param {ConstantCodeInfo} constantCodeInfo 
 * @param {ConstantDocInfo} constantDocInfo 
 * @return {ConstantDoc}
 */
function generateConstantDoc(constantCodeInfo, constantDocInfo) {
    // Part 1: Generate new docs
    const methodDocComment = createConstantDocComment(constantDocInfo);
    // Part 2: Generate method signature
    const signature = createConstantSignature(constantCodeInfo);
    return {
        doc: methodDocComment,
        signature: signature,
    }
}

/**
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @return {MethodDoc}
 */
function generateMethodDoc(methodCodeInfo, methodDocInfo) {
    const sortedDocMethods = sortDocMethods(methodCodeInfo, methodDocInfo);
    // Part 1: Generate new docs
    const methodDocComment = createMethodDocComment(methodDocInfo, sortedDocMethods);
    // Part 2: Generate method signature
    const signature = createMethodSignature(methodCodeInfo);
    return {
        doc: methodDocComment,
        signature: signature,
    }
}

/**
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @return {DocInfoVariable[]}
 */
function sortDocMethods(methodCodeInfo, methodDocInfo) {
    const map = new Map(methodCodeInfo.variables.map(x => [x.name, x.start]));
    return Array.from(methodDocInfo.variables.values()).sort(compareBy(x => map.get(x.name) || 0));
}

/**
 * @param {PropDocInfo} propDocInfo
 * @return {string[]}
 */
function createPropDocComment(propDocInfo) {
    return createDocComment(propDocInfo.description, propDocInfo.additionalTags);
}

/**
 * @param {TypedefFunctionInfo} fnInfo 
 * @param {SeveritySettingsConfig} severitySettings
 * @return {MethodDoc | undefined}
 */
function createFunctionDoc(severitySettings, fnInfo) {
    /** @type {MethodDocShape} */
    const method = {
        ...fnInfo,
        abstract: false,
        constructor: false,
        name: "",
        method: undefined,
        visibility: undefined,
    };
    const methodCodeInfo = createMethodCodeInfoFromTags(severitySettings, method, []);
    const methodDocInfo = createMethodDocInfoFromTags(method, [], []);
    if (methodCodeInfo !== undefined && methodDocInfo !== undefined) {
        return generateMethodDoc(methodCodeInfo, methodDocInfo);
    }
    else {
        return undefined;
    }
}

/**
 * @param {DocInfoTypedef} typedef
 * @param {number} indent
 * @return {string[]}
 */
function createTypedefDocComment(typedef, indent) {
    const templateTags = Array.from(typedef.templates.values()).flatMap(template => createTagForTypeParameter(
        template.name,
        template.description,
        template.default,
    ));
    return indentLines(createDocComment(typedef.description, [
        ...templateTags,
    ]), indent);
}

/**
 * @param {SeveritySettingsConfig} severitySettings
 * @param {NamespacedName} namespacedName
 * @param {DocInfoTypedef} typedef
 * @param {number} indent
 * @return {string[]}
 */
function createTypedefSignatureWithDocs(severitySettings, namespacedName, typedef, indent) {
    const indentSpec = namespacedName.namespace.length > 0 ? createNamespace(namespacedName.namespace, indent, getEmptyNamespaceSpec()) : undefined;

    if (indentSpec) {
        indent += indentSpec.indent;
    }

    const docComment = createTypedefDocComment(typedef, indent);

    const docInfoTemplate = tagToDocInfoTemplate(Array.from(typedef.templates.values()));
    const genericsSignature = createGenericsSignature(docInfoTemplate, true);

    const signatureBase = `${createIndent(indent)}export type ${namespacedName.name}${genericsSignature} =`;
    /** @type {string[]} */
    const signature = [];

    const node = typedef.function ? typedef.function.node : undefined;
    const functionDoc = typedef.function ? createFunctionDoc(severitySettings, typedef.function) : undefined;

    if (functionDoc !== undefined && node !== undefined) {
        /** @type {MessageFactory} */
        const factory = message => newNodeErrorMessage(message, node);
        if (typedef.typedef) {
            handleError("tagSuperfluousTypedef", severitySettings, () => factory(`Found type signature on @typedef {${typedef.typedef}} ${typedef.name}, but method tags (@param, @return etc.) are present already. Remove the type signature.`));
        }
        signature.push(signatureBase);
        signature.push(...indentLines([
            ...functionDoc.doc,
            ...toLambdaMethodSignature(functionDoc.signature),
        ], indent));
    }
    else {
        if (!typedef.typedef) {
            /** @type {MessageFactory} */
            const factory = message => newNodeErrorMessage(message, typedef.node);
            handleError("tagMissingTypedef", severitySettings, () => factory(`Tag @typedef must specify a type, eg. '@typedef {string} ${typedef.name}'`));
        }
        signature.push(`${signatureBase} ${typedef.typedef};`);
    }
    
    if (indentSpec) {
        indent -= indentSpec.indent;
    }

    return [
        ...(indentSpec ? indentSpec.open : []),
        ...docComment,
        ...signature,
        ...(indentSpec ? indentSpec.close : []),
    ];
}

/**
 * @param {string} name
 * @param {string} type
 * @param {string} description
 * @return {string[]}
 */
function createConstSignatureWithDocs(name, type, description) {
    const signature = `export const ${name}: ${type};`;
    return [
        ...createDocComment(description, []),
        signature,
    ];
}

/**
 * @param {ConstantDocInfo} constantDocInfo 
 * @return {string[]}
 */
function createConstantDocComment(constantDocInfo) {
    return createDocComment(constantDocInfo.description, [
        ...constantDocInfo.additionalTags,
    ]);
}

/**
 * @param {MethodDocInfo} methodDocInfo
 * @param {DocInfoVariable[]} sortedDocVariables
 * @return {string[]}
 */
function createMethodDocComment(methodDocInfo, sortedDocVariables) {
    // @template ...
    const templateTags = methodDocInfo.templates.flatMap(template => createTagForTypeParameter(
        template.name,
        template.description,
        template.initializer
    ));
    // @param param0 ...
    const positionalParamTags = Array.from(methodDocInfo.patterns.values()).map(pattern => createTagForPositionalParameter(
        pattern.index,
        pattern.description,
        pattern.initializer,        
        pattern.required,
    ));
    // @param ...
    const paramTags = sortedDocVariables.map(variable => createTagForParameter(
        variable.name,
        variable.description,
        variable.initializer,
        variable.required,
    ));
    // @return ...
    const returnTag = !methodDocInfo.return.hasReturn ? undefined : createTag(Tags.Return, {
        description: methodDocInfo.return.description,
    });
    // @yield ...
    const yieldTag = !methodDocInfo.yield.hasYield ? undefined : createTag(Tags.Yield, {
        description: methodDocInfo.yield.description,
    });
    // @next ...
    const nextTag = !methodDocInfo.next.hasNext ? undefined : createTag(Tags.Next, {
        description: methodDocInfo.next.description,
    });
    return createDocComment(methodDocInfo.description, [
        ...templateTags,
        ...positionalParamTags,
        ...paramTags,
        ...(yieldTag ? [yieldTag] : []),
        ...(nextTag ? [nextTag] : []),
        ...(returnTag ? [returnTag] : []),
        ...methodDocInfo.additionalTags,
    ]);
}

/**
 * Inspects the property and its doc comments and creates the type definitions for it.
 * @param {PropSignatureConverter} signatureConverter
 * @param {ObjectCodeProperty} property Property to document.
 * @param {SeveritySettingsConfig} severitySettings
 * @return {PropDocResult} The individual lines with the type definitions.
 */
function createPropertyDocs(signatureConverter, property, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newPropErrorMessage(message, property);
    /** @type {string[]} */
    const external = [];

    // Check doc comment on property / getter / setter
    const getterJsDoc  = property.getter !== undefined  && property.getter.hasComment ? property.getter.jsdoc : undefined;
    const setterJsDoc  = property.setter !== undefined && property.setter.hasComment ? property.setter.jsdoc : undefined;
    if (getterJsDoc !== undefined && setterJsDoc !== undefined) {
        handleError("docOnGetterAndSetter", severitySettings, () => factory(`Doc comments for property '${property.name}' must not be present on both the getter and setter.`));
    }
    const propDoc = getterJsDoc || setterJsDoc || property.jsdoc;

    // Take a look at the JavaScript code
    const propCodeInfo = createPropCodeInfo(property);

    // Take a look at the (hopefully present) JSDocs
    const propDocInfo = createPropDocInfo(property.name, propDoc, property, severitySettings);
    mergeAndValidatePropData(property, propCodeInfo, propDocInfo, severitySettings);

    // Is property object with interface or class?
    /** @type {PropSubObject | undefined} */
    let subObject = undefined;
    if (propDocInfo.subObject) {
        if (property.node.type !== "ObjectExpression") {
            handleError("propInvalidIfaceOrClass", severitySettings, () => factory(`Found @interface or @class in doc comments of property '${property.name}', but property value is not an object literal`));
        }
        else {
            subObject = {
                description: propDocInfo.subObject.description,
                objectNode: property.node,
                propertyNode: property.nodeProperty,
                name: property.name,
                tags: propDocInfo.subObject.tags,
            };    
        }
    }
    else {
        for (const constInfo of propDocInfo.constants) {
            external.push(...createConstDocs(constInfo, propCodeInfo.typedef, 0));            
        }
    }

    // Generate doc comments + signature
    const generatedDoc = generatePropertyDoc(propCodeInfo, propDocInfo);
    return {
        docs: [
            ...generatedDoc.doc,
            ...signatureConverter(generatedDoc.signature, false),
        ],
        external: external,
        subObject: subObject,
        typedefs: propDocInfo.typedefs,
    }
}

/**
 * @param {ConstantSignatureConverter} signatureConverter
 * @param {CodeConstant} constant Constant to document.
 * @param {SeveritySettingsConfig} severitySettings
 * @return {ConstantDocResult} A type declaration file (`.d.ts`) snippet for the constant.
 */
function createConstantDocs(signatureConverter, constant, severitySettings) {
    // Take a look at the JavaScript code
    const constantCodeInfo = createConstantCodeInfo(constant.name, constant.node, constant.declaration);
    // Take a look at the (hopefully present) JSDocs
    const constantDocInfo = createConstantDocInfo(constant.name, constant.jsdoc, constant, severitySettings);
    // Merge both and validate
    mergeAndValidateConstantData(constant, constantCodeInfo, constantDocInfo, severitySettings);
    const constantDoc = generateConstantDoc(constantCodeInfo, constantDocInfo);
    return {
        docs: [
            ...constantDoc.doc,
            ...signatureConverter(constantDoc.signature),
        ],
        typedefs: constantDocInfo.typedefs, 
    };
}

/**
 * @param {MethodSignatureConverter} signatureConverter
 * @param {ObjectCodeMethod} method Method to document.
 * @param {SeveritySettingsConfig} severitySettings
 * @return {MethodDocResult} A type definitions file (`.d.ts`) snippet for the method.
 */
function createInvocationDocs(signatureConverter, method, severitySettings) {
    // Take a look at the JavaScript code
    const methodCodeInfo = createMethodCodeInfo(method.name, method);
    // Take a look at the (hopefully present) JSDocs
    const methodDocInfo = createMethodDocInfo(method.name, method.jsdoc, method, severitySettings);
    // Merge both and validate
    mergeAndValidateMethodData(method, methodCodeInfo, methodDocInfo, severitySettings);
    const methodDoc = generateMethodDoc(methodCodeInfo, methodDocInfo);
    return {
        docs: [
            ...methodDoc.doc,
            ...signatureConverter(methodDoc.signature, true),
        ],
        typedefs: methodDocInfo.typedefs, 
    };
}

/**
 * @param {ObjectShapeDoc} docs
 * @param {NamespacedName} namespacedName
 * @param {number} currentIndent
 */
function createIndentSpec(docs, namespacedName, currentIndent) {
    const exportType = docs.export.type;
    const tagsTemplate = docs.templates.flatMap(template => createTagForTypeParameter(
        template.name,
        template.description,
        template.initializer,
    ));
    if (exportType === "namespace") {
        const namespace = [
            ...namespacedName.namespace,
            namespacedName.name
        ];
        return createNamespace(namespace, currentIndent, {
            additionalTags: [
                ...tagsTemplate,
                ...docs.additionalTags
            ],
            description: docs.export.description,    
        });
    }
    else {
        const typeSpec = {
            abstract: docs.abstract,
            additionalTags: [
                ...tagsTemplate,
                ...docs.additionalTags
            ],
            description: docs.export.description,
            extends: docs.extends,
            generics: docs.templates,
            implements: docs.implements,
            type: exportType,
        };
        if (namespacedName.namespace.length > 0) {
            return createNamespaceWithType(namespacedName, currentIndent, typeSpec, getEmptyNamespaceSpec());    
        }
        else {
            return createType(namespacedName.name, currentIndent, typeSpec, false);
        }
    }
}

/**
 * @param {CommentDocInfo} doc
 * @param {import("estree").Node} node
 * @param {NamespacedName | undefined} name
 * @param {SeveritySettingsConfig} severitySettings
 * @param {number} indent
 * @return {DocResult}
 */
function createCommentDocs(doc, node, name = undefined, severitySettings, indent = 0) {
    /** @type {MessageFactory} */
    const factory = message => newNodeErrorMessage(message, node);

    /** @type {DocResult} */
    const result = {
        internal: [],
        external: [],
    };

    const stack = [{
        nestedDoc: doc,
        namespacedName: name,
        indent: indent,
        lines: result.internal,
        /** @type {string[]} */
        path: [],
    }];

    for (let stackEl = stack.pop(); stackEl !== undefined; stackEl = stack.pop()) {
        let {nestedDoc, namespacedName, indent: currentIndent, lines, path} = stackEl;
        if (nestedDoc.docs) {
            // Do not create constants for root (top level) objects
            if (path.length > 0) {
                result.external.push(...createPropertyConstants(nestedDoc));
            }

            // open namespace + interface
            const namespaceOnly = nestedDoc.docs.export.type === "namespace";
            const methodSignatureConverter = namespaceOnly ? toExportFunctionSignature : toObjectShorthandMethodSignature;
            const propSignatureConverter = namespaceOnly ? toExportPropSignature : toObjectPropSignature;
            let indentSpec = namespacedName !== undefined ? createIndentSpec(nestedDoc.docs, namespacedName, currentIndent) : undefined;
            if (indentSpec) {
                currentIndent += indentSpec.indent;
                lines.push(...indentSpec.open);
            }

            // add properties
            if (nestedDoc.docs.method !== undefined) {
                if (namespaceOnly) {
                    handleError("namespaceInvalidMethodSignature", severitySettings, () => factory(`The object export exported as namespace '${namespacedName ? strJoin(namespacedName.namespace, ",") : ""}.${namespacedName ? namespacedName.name : ""}' cannot have a function signature.`));
                }
                else {
                    lines.push(...indentLines(nestedDoc.docs.method.doc, currentIndent));
                    lines.push(...indentLines(toAnonymousMethodSignature(nestedDoc.docs.method.signature, true), currentIndent));    
                }
            }
            for (const [name, prop] of nestedDoc.docs.props) {
                const nested = nestedDoc.nested.get(name);
                const method = nested && nested.docs ? nested.docs.method : undefined;
                const typedef = nested && nested.docs ? nested.docs.typedef : "";
                const exportName = nested && nested.docs && nested.docs.export.name ? nested.docs.export.name : typedef || "";
                const hasProperty = nested && nested.docs && nested.docs.hasProperty;

                if (exportName !== "") {
                    const hasExtendsOrImplements = nested && nested.docs && (nested.docs.extends.size > 0 || nested.docs.implements.size > 0);
                    const hasSubProperties = nested && nested.nested.size > 0;
                    const hasName = nested && nested.docs && nested.docs.name != "";
                    if (nested && (hasSubProperties || (hasExtendsOrImplements && hasName))) {
                        // externalize nested + create it
                        stack.push({
                            nestedDoc: nested,
                            namespacedName: typeToNamespacedName(exportName),
                            lines: result.external,
                            indent: 0,
                            path: [...path, name],
                        });
                        if (hasProperty) {
                            const typeReferencesNamespace = nested.docs !== undefined && nested.docs.export.type === "namespace" && !nested.docs.export.name;
                            lines.push(...indentLines(prop.doc, currentIndent));
                            lines.push(...indentLines(propSignatureConverter(prop.signature, typeReferencesNamespace), currentIndent));
                        }
                    }
                    else {
                        // reference to some type
                        if (method !== undefined) {
                            handleError("tagConflictingMethodAndProp", severitySettings, () => factory(`Cannot have both @prop and @method in doc comments, unless the property has nested properties`));
                        }
                        else {
                            lines.push(...indentLines(prop.doc, currentIndent));
                            lines.push(...indentLines(propSignatureConverter(prop.signature, false), currentIndent));
                            if (nested) {
                                result.external.push(...createPropertyConstants(nested));
                            }
                        }
                    }
                }
                else {
                    if (nested !== undefined && nested.nested.size > 0) {
                        // nested inline interface
                        const optional = nested.docs && nested.docs.optional ? "?" : "";
                        const docs = createCommentDocs(nested, node, undefined, severitySettings, currentIndent + 1);
                        lines.push(...indentLines(prop.doc, currentIndent));
                        lines.push(`${createIndent(currentIndent)}${name}${optional}: {`);
                        lines.push(...docs.internal);
                        lines.push(`${createIndent(currentIndent)}};`);
                        result.external.push(...docs.external);
                    }
                    else {
                        // basic method
                        if (nested && nested.docs && nested.docs.method) {
                            if (nested.docs.hasProperty) {
                                handleError("tagConflictingMethodAndProp", severitySettings, () => factory(`Cannot have both @prop and @method in doc comments, unless the property has nested properties`));
                            }
                            else {
                                lines.push(...indentLines(nested.docs.method.doc, currentIndent));
                                lines.push(...indentLines(methodSignatureConverter(nested.docs.method.signature, true), currentIndent));
                                result.external.push(...createMethodConstants(nested.docs.constants, nested.docs.method.signature));
                            }
                        }
                        // basic property
                        else {
                            lines.push(...indentLines(prop.doc, currentIndent));
                            lines.push(...indentLines(toObjectPropSignature(prop.signature, false), currentIndent));
                            if (nested) {
                                result.external.push(...createPropertyConstants(nested));
                            }
                        }
                    }
                }
            }

            // close namespace + interface
            if (indentSpec) {
                lines.push(...indentSpec.close);
                currentIndent -= indentSpec.indent;
            }
        }
    }
    return result;
}

/**
 * @param {CommentDocInfo} nested
 * @return {string[]}
 */
function createPropertyConstants(nested) {
    /** @type {string[]} */
    const lines = [];
    if (nested.docs !== undefined) {
        for (const constInfo of nested.docs.constants) {
            const constType = nested.docs.export.name || nested.docs.typedef || "";
            if (constType.length > 0) {
                lines.push(...createConstDocs(constInfo, constType, 0));
            }
        }
    }
    return lines;
}

/**
 * @param {ConstantInfo[]} constants 
 * @param {MethodSignature} signature 
 * @return {string[]}
 */
function createMethodConstants(constants, signature) {
    /** @type {string[]} */
    const lines = [];
    for (const constInfo of constants) {
        const constType = toAnonymousMethodSignature(signature, true);
        if (constType.length > 0) {
            lines.push(...createConstDocs(constInfo, strJoin(constType, "\n"), 0));
        }
    }
    return lines;
}

/**
 * @param {ConstantInfo} constInfo 
 * @param {string} type
 * @param {number} indent
 * @return {string[]}
 */
function createConstDocs(constInfo, type, indent) {
    const {name, namespace} = typeToNamespacedName(constInfo.name || "");
    /** @type {string[]} */
    const lines = [];
    if (name.length > 0) {
        const indentSpec = namespace.length > 0 ? createNamespace(namespace, indent, getEmptyNamespaceSpec()) : undefined;
        if (indentSpec) {
            indent += indentSpec.indent;
            lines.push(...indentSpec.open);
        }
        lines.push(...indentLines(createConstSignatureWithDocs(name, type, constInfo.description), indent));
        if (indentSpec) {
            lines.push(...indentSpec.close);
            indent -= indentSpec.indent;
        }
    }
    return lines;
}

/**
 * @param {DocInfoTypedef[]} typedefs 
 * @param {SeveritySettingsConfig} severitySettings
 * @param {string[] | undefined} internalNamespace
 * @param {number} internalIndent
 * @return {TypedefResult}
 */
function createTypedefDocs(typedefs, severitySettings, internalNamespace = undefined, internalIndent = 0) {
    typedefs.sort(compareBy(x => x.name));

    /** @type {TypedefResult} */
    const result = {
        external: [],
        internal: [],
    };

    for (const typedef of typedefs) {
        const namespacedName = typeToNamespacedName(typedef.name);
        const hasNamespace = namespacedName.namespace.length > 0;
        if (internalNamespace !== undefined && !hasNamespace) {
            namespacedName.namespace = internalNamespace;
        }
        const typedefDocs = createTypedefSignatureWithDocs(severitySettings, namespacedName, typedef, hasNamespace ? 0 : internalIndent);
        if (hasNamespace) {
            result.external.push(...typedefDocs);
        }
        else {
            result.internal.push(...typedefDocs);
        }
    }
    
    return result;
}



/**
 * @param {import("comment-parser").Tag[]} templates 
 * @return {DocInfoTemplate[]}
 */
function tagToDocInfoTemplate(templates) {
    return templates.map(template => ({
        description: template.description || "",
        extends: template.type || "",
        initializer: template.default || "",
        name: template.name,
    }));
}

/**
 * @param {ObjectDocInfo} objectDocInfo
 * @param {import("estree").Node} node
 * @param {SeveritySettingsConfig} severitySettings
 * @return {CommentDocInfo}
 */
function parseObjectComment(objectDocInfo, node, severitySettings) {
    /** @type {CommentDocInfo} */
    const result = {
        nested: new Map(),
        docs: undefined,
    }
    validateObjectDocInfo(objectDocInfo, node, severitySettings);
    for (const {path, shape} of eachNestedShape(objectDocInfo.shape)) {
        const nested = getOrCreateNestedObjectDocAtPath(result, path);
        const docs = createObjectDocsFromShape(severitySettings, shape);
        nested.docs = docs;
    }
    return result;
}

/**
 * @param {ObjectDocShape} shape
 * @param {SeveritySettingsConfig} severitySettings
 * @return {ObjectShapeDoc}
 */
function createObjectDocsFromShape(severitySettings, shape) {
    /** @type {ObjectShapeDoc} */
    const result = {
        abstract: shape.abstract,
        additionalTags: shape.additionalTags,
        constants: shape.constants,
        export: shape.export,
        extends: shape.extends,
        implements: shape.implements,
        hasProperty: shape.jsdoc !== undefined,
        name: shape.name,
        method: undefined,
        optional: shape.jsdoc ? shape.jsdoc.optional : false,
        props: new Map(),
        readonly: shape.readonly,
        templates: tagToDocInfoTemplate(shape.templates.toArray()),
        typedef: shape.jsdoc ? shape.jsdoc.type || "" : "",
    }
    if (shape.method) {
        const methodCodeInfo = createMethodCodeInfoFromTags(severitySettings, shape.method, shape.templates.toArray());
        const methodDocInfo = createMethodDocInfoFromTags(shape.method, shape.templates.toArray(), shape.additionalTags);
        if (methodCodeInfo !== undefined && methodDocInfo !== undefined) {
            result.method = generateMethodDoc(methodCodeInfo, methodDocInfo);
        }
    }
    for (const [name, prop] of Array.from(shape.props).sort(compareBy(x => x[0]))) {
        const propCodeInfo = createPropCodeInfoFromDocs(prop);
        const propDocInfo = createPropDocInfoFromDocs(prop);
        const propDocs = generatePropertyDoc(propCodeInfo, propDocInfo);
        result.props.set(name, propDocs);
    }
    return result;
}

/**
 * @param {ObjectDocShape} prop
 * @return {PropCodeInfo}
 */
function createPropCodeInfoFromDocs(prop) {
    return {
        name: prop.name,
        optional: prop.optional,
        readonly: prop.readonly,
        typedef: prop.jsdoc ? prop.jsdoc.type || "" : "",
        visibility: prop.visibility,
    };
}

/**
 * @param {ObjectDocShape} prop
 * @return {PropDocInfo}
 */
function createPropDocInfoFromDocs(prop) {
    return {
        additionalTags: prop.additionalTags,
        constants: Array.from(prop.constants),
        description: prop.jsdoc ? prop.jsdoc.description || "" : "",
        name: prop.name,
        optional: prop.optional,
        readonly: prop.readonly,
        subObject: undefined,
        typedef: prop.jsdoc ? prop.jsdoc.type || "" : "",
        typedefs: [],
        visibility: prop.visibility,
    };
}


/**
 * 
 * @param {CommentDocInfo} current 
 * @param {string[]} path
 * @return {CommentDocInfo}
 */
function getOrCreateNestedObjectDocAtPath(current, path) {
    for (const part of path) {
        const nested = current.nested.get(part);
        if (nested === undefined) {
            /** @type {CommentDocInfo} */
            const newNested = {
                nested: new Map(),
                docs: undefined,
            };
            current.nested.set(part, newNested);
            current = newNested;
        }
        else {
            current = nested;
        }
    }
    return current;
}

/**
 * Validates information gained purely from doc comments.
 * @param {ObjectDocInfo} objectDocInfo
 * @param {import("estree").Node} node
 * @param {SeveritySettingsConfig} severitySettings
 */
function validateObjectDocInfo(objectDocInfo, node, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newNodeErrorMessage(message, node);

    for (const {path, shape} of eachNestedShape(objectDocInfo.shape)) {
        if (shape.method !== undefined) {
            for (const [key, data] of Array.from(shape.method.destructuring)) {
                if (data.pattern === undefined && data.structure !== undefined) {
                    handleError("tagMissingPattern", severitySettings, () => factory(`Found '@structure ${path.join(".")}.${key}' in doc comments, but no corresponding '@pattern {...} ${path.join(",")}.${key}'`));
                    shape.method.destructuring.delete(key);
                }
                if (data.pattern !== undefined && data.structure === undefined) {
                    handleError("tagMissingStructure", severitySettings, () => factory(`Found '@pattern ${path.join(".")}.${key}' in doc comments, but no corresponding '@structure {...} ${path.join(",")}.${key}'`));
                    shape.method.destructuring.delete(key);
                }
            }
            if (shape.method.method === undefined) {
                handleError("tagMissingMethod", severitySettings, () => factory(`Method tags for property path '${path.join(".")}' were found in doc comments, but no corresponding '@method ${path.join(".")}' was found`));
                shape.method = undefined;    
            }
        }
        if (shape.name !== "" && shape.jsdoc === undefined && shape.method === undefined && shape.export.type === "unspecified") {
            handleError("tagMissingParentProp", severitySettings, () => factory(`Missing tag '@prop ${path.join(".")}', make sure to add a tag even for nested properties`))
        }
    }
}

/**
 * @param {ObjectDocShape} shape
 * @return {Iterable<{path: string[], shape: ObjectDocShape}>}
 */
function* eachNestedShape(shape) {
    /** @type {{path: string[], shape: ObjectDocShape}[]} */
    const shapeStack = [{
        path: shape.name ? [shape.name] : [],
        shape: shape,
    }];
    while (shapeStack.length > 0) {
        const {path, shape} = shapeStack[shapeStack.length - 1];
        shapeStack.pop();
        for (const prop of shape.props.values()) {
            shapeStack.push({
                path: path.concat(prop.name),
                shape: prop,
            });
        }
        yield {path, shape};
    }
}



module.exports = {
    createConstDocs,
    createConstantDocs,
    createTypedefDocs,
    createInvocationDocs,
    createPropertyDocs,
    createCommentDocs,
    generateMethodDoc,
    generatePropertyDoc,
    parseObjectComment,
};