//@ts-check

// methods for working with typescript types

const { generate } = require("astring");
const { removeInitializerFromPattern } = require("./acorn-util");
const { assertNever, strJoin } = require("./lang");

const unescapedIdentifier = /^[a-zA-Z_$][0-9a-zA-Z_$]*$/;


/**
 * If the property name contains special characters, return `"THE-NAME"`, otherwise just `"THENAME"`.
 * @param {string} name 
 * @return {string}
 */
function escapeObjectPropertyName(name) {
    if (unescapedIdentifier.test(name)) {
        return name;
    }
    else {
        const escapedName = name.replace(/\"/g, "\\\"");
        return `"${escapedName}"`;
    }
}

/**
 * 
 * @param {string} type 
 * @param {boolean} optional
 */
function getConstantType(type, optional) {
    if (type === "" || type === "*") {
        type = "any";
    }
    if (optional) {
        type = `(${type})|undefined`;
    }
    return type;
}

/**
 * Returns the default `any` type when no more specific type is known.
 * @param {"ident" | "param" | "array" | "object"} type Whether it is a normal, array-spread or object-spread parameter.
 * @param {boolean} rest Whether is it a rest parameter.
 * @param {string} baseType Base type for the argument.
 * @return {string} The default type.
 */
function getArgumentType(type, rest, baseType = "any") {
    if (baseType === "" || baseType === "*") {
        baseType = "any";
    }
    switch (type) {
        case "ident":
        case "param":
            return rest ? `Iterable<${baseType}>` : baseType;
        case "array":
            return rest ? `Iterable<${baseType}>` : `${baseType}[]`;
        case "object":
            return rest ? `Iterable<${baseType}>` : `{[key: any]}: ${baseType}}`;
        default:
            return assertNever(type);
    }
}

/**
 * @param {ReturnSpec} spec The base return type and whether is is an async or generator function.
 * @return {string} The return type for the function or generator.
 */
function getReturnType({ async, generator, baseTypeNext, baseTypeReturn, baseTypeYield }) {
    if (baseTypeReturn === "" || baseTypeReturn === "*") {
        baseTypeReturn = "any";
    }
    if (baseTypeYield === "" || baseTypeYield === "*") {
        baseTypeYield = "any";
    }
    if (baseTypeNext === "" || baseTypeNext === "*") {
        baseTypeNext = "any";
    }
    if (generator) {
        const returnType = generator.hasReturn ? baseTypeReturn : "void";
        const yieldType = generator.hasYield ? baseTypeYield : "never";
        const nextType = generator.hasNext ? baseTypeNext : "unknown";
        if (async) {
            if (generator.hasNext) {
                return `AsyncGenerator<${yieldType}, ${returnType}, ${nextType}>`;
            }
            else {
                return `AsyncGenerator<${yieldType}, ${returnType}>`;
            }
        }
        else {
            if (generator.hasNext) {
                return `Generator<${yieldType}, ${returnType}, ${nextType}>`;
            }
            else {
                return `Generator<${yieldType}, ${returnType}>`;
            }
        }
    }
    else {
        if (async) {
            return `Promise<${baseTypeReturn}>`;
        }
        else {
            return baseTypeReturn;
        }
    }
}

/**
 * @param {string} type Type to process.
 * @return {string} The type, without the rest "..." indicator if present.
 */
function removeRestFromType(type) {
    return hasRestSpecifier(type) ? type.substr(3) : type;
}

/**
 * @param {string} type Type to check
 * @return {boolean} Whether the given type has a rest specifier
 */
function hasRestSpecifier(type) {
    return type.startsWith("...");
}

/**
 * Generates the methods of generics, eg. `<T, K extends keyof T, R>`
 * @param {DocInfoTemplate[]} generics A list of generics info.
 * @param {boolean} allowExtends If `false`, do not generate `extends ...`, even when specified.
 * @return {string} The signature of the given generics.
 */
function createGenericsSignature(generics, allowExtends = true) {
    const genericArgs = generics.map(generic => {
        const extendsSpecifier = generic.extends.length === 0 || !allowExtends ? "" : ` extends ${generic.extends}`;
        const defaultSpecifier = generic.initializer.length === 0 ? "" : ` = ${generic.initializer}`;
        return `${generic.name}${extendsSpecifier}${defaultSpecifier}`;
    });
    const signature = genericArgs.length === 0 ? "" : `<${strJoin(genericArgs, ", ")}>`;
    return signature;
}


/**
 * @param {ConstantCodeInfo} constantCodeInfo 
 * @return {ConstantSignature}
 */
function createConstantSignature(constantCodeInfo) {
    return {
        constantType: constantCodeInfo.type || "let",
        name: constantCodeInfo.name,
        type: getConstantType(constantCodeInfo.typedef || "", constantCodeInfo.optional),
    };
}

/**
 * Create the method signature with the type definitions.
 * @param {MethodCodeInfo} methodCodeInfo
 * @return {MethodSignature}
 */
function createMethodSignature(methodCodeInfo) {
    // Create visibility modifier
    const visibility = methodCodeInfo.visibility ? methodCodeInfo.visibility : "public";

    // Create abstract modifier
    const abstract = methodCodeInfo.abstract ? "abstract " : "";

    // Create async modifier
    const async = methodCodeInfo.isAsync ? "async " : "";

    // Create generics
    const generics = createGenericsSignature(methodCodeInfo.generics);

    // Create generator marker *
    const generator = methodCodeInfo.isGenerator ? "* " : "";

    // Create arguments
    const args = methodCodeInfo.arguments.map(arg => {
        let param;
        if (arg.type === "param") {
            param = arg.name;
        }
        else {
            param = generate(removeInitializerFromPattern(arg.node));
        }
        const rest = arg.rest ? "..." : "";
        const typedef = arg.typedef !== "" && arg.typedef !== "..." ? removeRestFromType(arg.typedef) : getArgumentType(arg.type, arg.rest);
        // Rest argument cannot be optional
        // Argument with initializer cannot be optional
        const optional = !arg.required && !arg.rest && arg.initializer === "" ? "?" : "";
        return {
            // Rest argument cannot have an initializer
            initializer: arg.initializer === "" || arg.rest ? "" : ` = ${arg.initializer}`,
            optional: optional,
            param: param,
            rest: rest,
            type: arg.type,
            typedef: typedef,
        };
    });

    if (methodCodeInfo.thisTypedef) {
        args.unshift({
            initializer: "",
            optional: "",
            param: "this",
            rest: "",
            type: "param",
            typedef: getArgumentType("param", false, methodCodeInfo.thisTypedef),
        });
    }

    // Create return and yield type
    const baseTypeReturn = methodCodeInfo.return.node !== undefined ? methodCodeInfo.return.typedef : "void";
    const baseTypeYield = methodCodeInfo.yield.node !== undefined ? methodCodeInfo.yield.typedef : "void";
    const baseTypeNext = methodCodeInfo.next.node !== undefined ? methodCodeInfo.next.typedef : "void";
    const returnType = getReturnType({
        async: methodCodeInfo.isAsync,
        baseTypeNext: baseTypeNext,
        baseTypeReturn: baseTypeReturn,
        baseTypeYield: baseTypeYield,
        constructor: methodCodeInfo.isConstructor,
        generator: !methodCodeInfo.isGenerator ? undefined : {
            hasNext: methodCodeInfo.next.node !== undefined,
            hasReturn: methodCodeInfo.return.node !== undefined,
            hasYield: methodCodeInfo.yield.node !== undefined,
        },
    });

    return {
        abstract: abstract,
        args: args,
        async: async,
        constructor: methodCodeInfo.isConstructor,
        generics: generics,
        generator: generator,
        name: methodCodeInfo.name,
        returnType: returnType,
        visibility: visibility,
    };
}

/**
 * If `optional` is `true`, appends `undefined` to the type.
 * @param {string} type 
 * @param {boolean} typeReferencesNamespace
 * @param {boolean} optional 
 * @return {string}
 */
function toPropType(type, typeReferencesNamespace, optional) {
    if (typeReferencesNamespace) {
        type = `typeof ${type}`;
    }
    if (optional) {
        type = `(${type})|undefined`;
    }
    return type;
}

/**
 * @param {PropCodeInfo} propCodeInfo
 * @return {PropSignature}
 */
function createPropSignature(propCodeInfo) {
    const name = propCodeInfo.name;
    const visibility = propCodeInfo.visibility ? propCodeInfo.visibility : "public";
    const type = propCodeInfo.typedef !== "" && propCodeInfo.typedef !== "*" ? propCodeInfo.typedef : "any";
    return {
        name: name,
        optional: propCodeInfo.optional,
        readonly: propCodeInfo.readonly,
        type: type,
        visibility: visibility,
    };
}

/**
 * @param {PropSignature} propSignature 
 * @param {boolean} typeReferencesNamespace
 * @return {string[]}
 */
function toObjectPropSignature(propSignature, typeReferencesNamespace) {
    const readonly = propSignature.readonly ? "readonly " : "";
    const visibility = propSignature.visibility !== "public" ? propSignature.visibility + " " : "";
    const optional = propSignature.optional ? "?" : "";
    const escapedPropertyName = escapeObjectPropertyName(propSignature.name);
    return [
        `${visibility}${readonly}${escapedPropertyName}${optional}: ${toPropType(propSignature.type, typeReferencesNamespace, false)};`
    ];
}

/**
 * Creates a signature like
 * ```typescript
 * export const foo: string|undefined;
 * export let foo: string|undefined;
 * ```
 * @param {PropSignature} propSignature 
 * @param {boolean} typeReferencesNamespace
 * @return {string[]}
 */
function toExportPropSignature(propSignature, typeReferencesNamespace) {
    if (propSignature.readonly) {
        return toExportConstPropSignature(propSignature, typeReferencesNamespace);
    }
    else {
        return toExportLetPropSignature(propSignature, typeReferencesNamespace);
    }
}

/**
 * Creates a signature like
 * ```typescript
 * export const foo: string|undefined;
 * ```
 * @param {PropSignature} propSignature 
 * @param {boolean} typeReferencesNamespace
 * @return {string[]}
 */
function toExportConstPropSignature(propSignature, typeReferencesNamespace) {
    return [
        `export const ${propSignature.name}: ${toPropType(propSignature.type, typeReferencesNamespace, propSignature.optional)};`
    ];
}

/**
 * Creates a signature like
 * ```typescript
 * export let foo: string|undefined;
 * ```
 * @param {PropSignature} propSignature 
 * @param {boolean} typeReferencesNamespace
 * @return {string[]}
 */
function toExportLetPropSignature(propSignature, typeReferencesNamespace) {
    return [
        `export let ${propSignature.name}: ${toPropType(propSignature.type, typeReferencesNamespace, propSignature.optional)};`
    ];
}


/**
 * @param {ArgSignature[]} args
 * @param {boolean} ambientContext `false` if the generated signature will appear in actual JavaScript code.
 */
function argsToSignature(args, ambientContext) {
    return strJoin(
        args.map(arg => {
            /** @type {string[]} */
            const parts = [];
            parts.push(arg.rest);
            parts.push(arg.param);
            if (ambientContext) {
                if (arg.initializer.length > 0) {
                    // Cannot have initializer in ambient context, so just mark as optional
                    parts.push("?");
                }
                else {
                    parts.push(arg.optional);
                }
            }
            else {
                // BindingPattern can't be marked optional using question mark in implementation signature
                if (arg.type === "param") {
                    parts.push(arg.optional);
                }
            }

            parts.push(": ");
            parts.push(arg.typedef);
            // A parameter initializer is only allowed in a function or constructor implementation
            if (!ambientContext) {
                parts.push(arg.initializer);
            }

            return strJoin(parts, "");
        }), ", "
    );
}

/**
 * Creates a signature like
 * ```typescript
 * abstract async foo<T>(x: T): string;
 * constructor<T>(x: T);
 * ```
 * @param {MethodSignature} signature Signature to convert.
 * @param {boolean} ambientContext `false` if the generated signature will appear in actual JavaScript code.
 * @return {string[]} Code lines with the method signature as a named method.
 */
function toObjectShorthandMethodSignature(signature, ambientContext) {
    const visibility = signature.visibility !== "public" ? signature.visibility + " " : "";
    const signatureName = signature.constructor ? "constructor" : signature.name;
    const returnType = signature.constructor ? "" : `: ${signature.returnType}`;
    const escapedName = escapeObjectPropertyName(signatureName);
    return [
        `${visibility}${signature.abstract}${ambientContext ? "" : signature.async}${escapedName}${signature.generics}(${argsToSignature(signature.args, ambientContext)})${returnType};`
    ];
}

/**
 * Creates a signature like
 * ```typescript
 * export function foo<T>(x: T): string;
 * export new<T>(x: T);
 * ```
 * @param {MethodSignature} signature Signature to convert.
 * @param {boolean} ambientContext `false` if the generated signature will appear in actual JavaScript code.
 * @return {string[]} Code lines with the method signature as an exported function
 */
function toExportFunctionSignature(signature, ambientContext) {
    if (signature.constructor) {
        throw new Error("Cannot export a newable as a function");   
    }
    return [
        `export ${ambientContext ? "" : signature.async}function ${signature.name}${signature.generics}(${argsToSignature(signature.args, ambientContext)}): ${signature.returnType};`
    ];
}

/**
 * Creates a signature like
 * ```typescript
 * declare function foo<T>(x: T): string;
 * ```
 * @param {MethodSignature} signature Signature to convert.
 * @param {boolean} ambientContext `false` if the generated signature will appear in actual JavaScript code.
 * @return {string[]} Code lines with the method signature as an exported function
 */
function toDeclareFunctionSignature(signature, ambientContext) {
    if (signature.constructor) {
        throw new Error("Cannot declare a newable as a function");   
    }
    return [
        `declare ${ambientContext ? "" : signature.async}function ${signature.name}${signature.generics}(${argsToSignature(signature.args, ambientContext)}): ${signature.returnType};`
    ];
}

/**
 * Creates a signature like
 * ```typescript
 * async <T>(x: number): string;
 * new<T>(x: number): string;
 * ```
 * @param {MethodSignature} signature Signature to convert.
 * @param {boolean} ambientContext `false` if the generated signature will appear in actual JavaScript code.
 * @return {string[]} Code lines with the method signature as an anonymous method.
 */
function toAnonymousMethodSignature(signature, ambientContext) {
    const visibility = signature.visibility !== "public" ? signature.visibility + " " : "";
    const newable = signature.constructor ? "new" : "";
    return [
        `${visibility}${ambientContext ? "" : signature.async}${newable}${signature.generics}(${argsToSignature(signature.args, ambientContext)}): ${signature.returnType};`
    ];
}

/**
 * Creates a signature like
 * ```typescript
 * <T>(x: number) => string;
 * ```
 * @param {MethodSignature} signature Signature to convert.
 * @return {string[]} Code lines with the method signature as an anonymous method.
 */
function toLambdaMethodSignature(signature) {
    return [
        `${signature.generics}(${argsToSignature(signature.args, false)}) => ${signature.returnType};`
    ];
}

/**
 * Creates a signature like
 * ```typescript
 * export const foo: string;
 * ```
 * @param {ConstantSignature} signature
 * @return {string[]}
 */
function toExportConstantSignature(signature) {
    return [
        `export ${signature.constantType === "constant" ? "const" : "let"} ${signature.name}: ${signature.type};`
    ];

}

/**
 * Creates a signature like
 * ```typescript
 * declare let foo: string;
 * declare const foo: string;
 * ```
 * @param {ConstantSignature} signature
 * @return {string[]}
 */
function toDeclareConstantSignature(signature) {
    return [
        `declare ${signature.constantType === "constant" ? "const" : "let"} ${signature.name}: ${signature.type};`
    ];
}

module.exports = {
    createConstantSignature,
    createGenericsSignature,
    createMethodSignature,
    createPropSignature,
    getArgumentType,
    getReturnType,
    hasRestSpecifier,
    removeRestFromType,
    toAnonymousMethodSignature,
    toDeclareConstantSignature,
    toDeclareFunctionSignature,
    toExportConstantSignature,
    toExportFunctionSignature,
    toExportPropSignature,
    toLambdaMethodSignature,
    toObjectPropSignature,
    toObjectShorthandMethodSignature,
};
