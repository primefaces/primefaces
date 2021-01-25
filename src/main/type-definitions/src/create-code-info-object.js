//@ts-check

const { parsePattern } = require("./acorn-util");
const { createTagForTypeParameter } = require("./create-tags");
const { getArgVariableInfo, getArgumentInfo } = require("./create-code-info-params");
const { checkTagHasType } = require("./doc-comment-check-tags");
const { handleError, newNodeErrorMessage } = require("./error");
const { NativeInsertionOrderMap } = require("./InsertionOrderMap");
const { countNonEmptyArraySlots } = require("./lang");
const { createType, createNamespace, getEmptyNamespaceSpec, typeToNamespacedName } = require("./ts-export");
const { hasRestSpecifier, removeRestFromType } = require("./ts-types");

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
 * @param {SeveritySettingsConfig} severitySettings
 * @param {MethodDocShape} method
 * @param {import("comment-parser").Tag[]} additionalTemplates
 * @return {MethodCodeInfo | undefined}
 */
function createMethodCodeInfoFromTags(severitySettings, method, additionalTemplates) {
    if (method === undefined) {
        return undefined;
    }
    else {
        /** @type {MessageFactory} */
        const factory = message => newNodeErrorMessage(message, method.node);
        const templates = [...method.templates.values(), ...additionalTemplates];
        /** @type {ArgumentInfo[]} */
        const argumentInfo = [];
        /** @type {Set<string>} */
        const destructuredVars = new Set();
        for (const [index, arg] of method.destructuring) {
            if (arg.structure && arg.pattern) {
                const pattern = parsePattern(arg.structure.type);
                const argInfo = getArgumentInfo(pattern);
                const argVarInfo = getArgVariableInfo(pattern);
                argInfo.typedef = removeRestFromType(arg.pattern.type);
                argInfo.rest = argInfo.rest || hasRestSpecifier(arg.pattern.type);
                argInfo.initializer = arg.pattern.default || argInfo.initializer || "";
                argInfo.required = !arg.pattern.optional;
                argumentInfo[index] = argInfo;
                argVarInfo.map(x => x.name).forEach(x => destructuredVars.add(x));
            }
        }
        const remainingParamsMap = new NativeInsertionOrderMap(method.params);
        for (const name of destructuredVars) {
            if (!method.params.has(name)) {
                handleError("tagMissingParamForStructure", severitySettings, () => factory(`Found destructured variable ${name} in @structure tag, but no corresponding '@param {...} ${name}' was found`));
            }
            remainingParamsMap.delete(name);
        }
        const remainingParams = remainingParamsMap.toArray().reverse().filter(x => !destructuredVars.has(x.name));
        const argCount = Math.max(argumentInfo.length, countNonEmptyArraySlots(argumentInfo) + remainingParams.length);
        for (let index = 0; index < argCount; ++index) {
            if (argumentInfo[index] === undefined) {
                const tag = remainingParams.pop();
                if (tag === undefined) {
                    handleError("tagTooFewParams", severitySettings, () => factory(`A pattern was specified, but not enough parameters were found to fill the parameters up to the index indicated by that pattern`));
                    argumentInfo[index] = {
                        type: "param",
                        initializer: "",
                        name: "$unused" + index,
                        required: false,
                        rest: false,
                        typedef: "any",
                    };
                }
                else {
                    const type = checkTagHasType(tag, severitySettings, factory);
                    const rest = type.startsWith("...");
                    argumentInfo[index] = {
                        type: "param",
                        initializer: tag.default || "",
                        name: tag.name,
                        required: !tag.optional,
                        rest: rest,
                        typedef: rest ? type.substr(3) : type,
                    };
                }
            }
        }

        /** @type {VariableInfo[]} */
        const variableInfo = method.params.toArray().map((param, index) => ({
            name: param.name,
            initializer: param.default || "",
            rest: param.type.startsWith("..."),
            start: index,
        }));

        /** @type {ReturnInfo} */
        const returnInfo = {
            node: method.return === undefined ? undefined : {
                type: "ReturnStatement",
            },
            typedef: method.return === undefined || method.return.type === undefined ? "" : method.return.type,
        };

        /** @type {YieldInfo} */
        const yieldInfo = {
            node: method.yield === undefined ? undefined : {
                type: "YieldExpression",
                delegate: false,
            },
            typedef: method.yield === undefined || method.yield.type === undefined ? "" : method.yield.type,
        };

        /** @type {YieldInfo} */
        const nextInfo = {
            node: method.next === undefined ? undefined : {
                type: "YieldExpression",
                delegate: false,
            },
            typedef: method.next === undefined || method.next.type === undefined ? "" : method.next.type,
        };


        /** @type {DocInfoTemplate[]} */
        const templateInfo = tagToDocInfoTemplate(templates);
        return {
            abstract: method.abstract,
            canCompleteNormally: true,
            arguments: argumentInfo,
            generics: templateInfo,
            isAsync: method.async,
            isConstructor: method.constructor,
            isGenerator: method.generator,
            name: method.name,
            next: nextInfo,
            return: returnInfo,
            thisTypedef: method.thisTypedef,
            variables: variableInfo,
            yield: yieldInfo,
            visibility: method.visibility,
        };
    }
}

/**
 * @param {MethodDocShape} method
 * @param {import("comment-parser").Tag[]} additionalTemplates
 * @param {import("comment-parser").Tag[]} additionalTags
 * @return {MethodDocInfo | undefined}
 */
function createMethodDocInfoFromTags(method, additionalTemplates, additionalTags) {
    if (method === undefined) {
        return undefined;
    }
    else {
        const templates = [...method.templates.values(), ...additionalTemplates];
        return {
            abstract: method.abstract,
            additionalTags: additionalTags,
            constructor: method.constructor,
            typedefs: [],
            description: method.method ? method.method.description || "" : "",
            next: {
                description: method.next !== undefined ? method.next.description : "",
                hasNext: method.next !== undefined,
                typedef: method.next !== undefined ? method.next.type : "",
            },
            patterns: new Map(Array.from(method.destructuring, ([index, info]) => [index, {
                index: index,
                typedef: info.pattern ? info.pattern.type || "" : "",
                initializer: info.pattern ? info.pattern.default || "" : "",
                required: !info.pattern || !info.pattern.optional,
                description: info.pattern ? info.pattern.description || "" : "",
            }])),
            return: {
                description: method.return !== undefined ? method.return.description : "",
                hasReturn: method.return !== undefined,
                typedef: method.return !== undefined ? method.return.type : "",
            },
            templates: tagToDocInfoTemplate(templates),
            thisTypedef: method.thisTypedef,
            variables: new Map(method.params.toArray().map(param => [param.name, {
                name: param.name,
                typedef: param.type || "",
                initializer: param.default || "",
                required: !param.optional,
                description: param.description || "",
            }])),
            visibility: method.visibility,
            yield: {
                description: method.yield !== undefined ? method.yield.description : "",
                hasYield: method.yield !== undefined,
                typedef: method.yield !== undefined ? method.yield.type : "",
            },
        };
    }
}



/**
 * @param {ObjectDef} objectDefinition
 * @param {ObjectDocInfo} objectDocInfo 
 * @return {{description: string, tags: import("comment-parser").Tag[]}}
 */
function createTopLevelDocData(objectDefinition, objectDocInfo) {
    // @template

    /** @type {Set<string>} */
    const processedTypeParams = new Set();
    /** @type {import("comment-parser").Tag[]} */
    const templateTags = [];
    for (const [, template] of objectDocInfo.shape.templates) {
        if (!processedTypeParams.has(template.name)) {
            processedTypeParams.add(template.name);
            templateTags.push(...createTagForTypeParameter(
                template.name,
                template.description,
                template.default,
            ));
        }
    }
    for (const generic of objectDefinition.spec.generics) {
        if (!processedTypeParams.has(generic.name)) {
            processedTypeParams.add(generic.name);
            templateTags.push(...createTagForTypeParameter(
                generic.name,
                generic.description,
                generic.initializer,
            ));
        }
    }

    return {
        description: objectDocInfo.description,
        tags: [
            ...templateTags,
            ...objectDocInfo.shape.additionalTags
        ],
    }
}

/**
 * @param {ObjectDef} objectDefinition
 * @param {ObjectDocInfo} docInfo
 * @param {number} indent
 * @return {{namespacedType: NamespacedType, namespaceOnly: boolean}}
 */
function makeNamespacedTypeForObject(objectDefinition, docInfo, indent) {
    const shape = docInfo.shape;
    const { name: exportName, namespace: exportNamespace } = typeToNamespacedName(shape.export.name || "");
    const docData = createTopLevelDocData(objectDefinition, docInfo);
    const exportType = shape.export.type && shape.export.type !== "unspecified" ? shape.export.type : objectDefinition.spec.type;
    const namespaceOnly = exportType === "namespace";

    /** @type {NamespacedType} */
    const result = {
        ifaceSpec: undefined,
        namespaceSpec: undefined,
        namespace: [],
        name: "",
    }

    const name = exportName || objectDefinition.name;

    // Create namespace
    const namespace = exportNamespace.length > 0 ? exportNamespace : objectDefinition.namespace;
    if (exportType === "namespace") {
        namespace.push(name);
    }
    if (namespace.length > 0) {
        result.namespaceSpec = createNamespace(namespace, indent, !namespaceOnly ? getEmptyNamespaceSpec() : {
            additionalTags: docData.tags,
            description: docData.description,
        });
    }

    // Create interface or class
    if (name && exportType !== "namespace") {
        const ifaceSpec = {
            abstract: shape.abstract || objectDefinition.spec.abstract,
            additionalTags: docData.tags,
            description: docData.description,
            extends: shape.extends.size > 0 ? shape.extends : objectDefinition.spec.extends,
            implements: shape.implements.size > 0 ? shape.implements : objectDefinition.spec.implements,
            generics: [...objectDefinition.spec.generics, ...tagToDocInfoTemplate(shape.templates.toArray())],
            type: exportType,
        };
        indent += result.namespaceSpec ? result.namespaceSpec.indent : 0;
        result.ifaceSpec = createType(name, indent, ifaceSpec, result.namespaceSpec !== undefined);
        indent -= result.namespaceSpec ? result.namespaceSpec.indent : 0;
    }

    if (namespace) {
        result.namespace = namespace;
    }
    result.name = name;

    return {
        namespaceOnly: namespaceOnly,
        namespacedType: result,
    };
}

module.exports = {
    createMethodCodeInfoFromTags,
    createMethodDocInfoFromTags,
    makeNamespacedTypeForObject,
};