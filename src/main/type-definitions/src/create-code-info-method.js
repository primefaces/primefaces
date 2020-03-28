//@ts-check

const { findFunctionNonEmptyReturnStatement, findFunctionNonEmptyYieldExpression } = require("./acorn-util");
const { getArgVariableInfo, getArgumentInfo } = require("./create-code-info-params");

/**
 * @param {string} name
 * @param {import("estree").FunctionExpression | import("estree").FunctionDeclaration} method
 * @return {MethodCodeInfo}
 */
function createMethodCodeInfo(name, method) {
    const returnStatement = findFunctionNonEmptyReturnStatement(method.body);
    const yieldStatement = findFunctionNonEmptyYieldExpression(method.body);
    return {
        abstract: false,
        arguments: method.params.map(getArgumentInfo),
        generics: [],
        return: {
            node: returnStatement !== undefined ? returnStatement : undefined,
            typedef: "",
        },
        isAsync: method.async !== undefined ? method.async : false,
        isGenerator: method.generator !== undefined ? method.generator : false,
        name: name,
        thisTypedef: "",
        variables: method.params.flatMap(getArgVariableInfo),
        yield: {
            node: yieldStatement !== undefined ? yieldStatement : undefined,
            typedef: "",
        },
        visibility: undefined,
    };
}

module.exports = {
    createMethodCodeInfo,
}