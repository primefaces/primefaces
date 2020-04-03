//@ts-check

const { findFunctionYieldStatement, findFunctionNonEmptyReturnStatement, findFunctionYieldExpression } = require("./acorn-util");
const { getArgVariableInfo, getArgumentInfo } = require("./create-code-info-params");

/**
 * @param {string} name
 * @param {import("estree").FunctionExpression | import("estree").FunctionDeclaration} method
 * @return {MethodCodeInfo}
 */
function createMethodCodeInfo(name, method) {
    const returnStatement = findFunctionNonEmptyReturnStatement(method.body);
    const yieldStatement = findFunctionYieldExpression(method.body);
    const nextStatement = findFunctionYieldStatement(method.body);
    return {
        abstract: false,
        arguments: method.params.map(getArgumentInfo),
        generics: [],
        isAsync: method.async !== undefined ? method.async : false,
        isGenerator: method.generator !== undefined ? method.generator : false,
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
        variables: method.params.flatMap(getArgVariableInfo),
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