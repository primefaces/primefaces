//@ts-check

const { findFunctionYieldStatement, findFunctionNonEmptyReturnStatement, findFunctionYieldExpression, isCanCompleteNormally } = require("./acorn-util");
const { getArgVariableInfo, getArgumentInfo } = require("./create-code-info-params");

/**
 * @param {string} name
 * @param {ObjectCodeMethod} method
 * @return {MethodCodeInfo}
 */
function createMethodCodeInfo(name, method) {
    const fn = method.node;
    const returnStatement = findFunctionNonEmptyReturnStatement(fn.body);
    const canCompleteNormally = isCanCompleteNormally(fn.body);
    const yieldStatement = findFunctionYieldExpression(fn.body);
    const nextStatement = findFunctionYieldStatement(fn.body);
    return {
        abstract: false,
        arguments: fn.params.map(getArgumentInfo),
        canCompleteNormally: canCompleteNormally,
        generics: [],
        isAsync: fn.async !== undefined ? fn.async : false,
        isConstructor: method.method !== undefined && method.method.kind === "constructor",
        isGenerator: fn.generator !== undefined ? fn.generator : false,
        name: name,
        next: {
            node: nextStatement !== undefined ? nextStatement : undefined,
            typedef: "",
        },
        return: {
            node: returnStatement !== undefined ? returnStatement : undefined,
            typedef: "",
        },
        thisTypedef: "",
        variables: fn.params.flatMap(getArgVariableInfo),
        visibility: undefined,
        yield: {
            node: yieldStatement !== undefined ? yieldStatement : undefined,
            typedef: "",
        },
    };
}

module.exports = {
    createMethodCodeInfo,
}