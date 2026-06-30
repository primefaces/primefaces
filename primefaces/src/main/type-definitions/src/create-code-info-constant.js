//@ts-check

/**
 * 
 * @param {"const" | "let" | "var"} kind 
 * @return {ConstantType | undefined}
 */
function toConstantType(kind) {
    if (kind === "const") {
        return "constant";
    }
    else if (kind === "let") {
        return "let";
    }
    else {
        return undefined;
    }
}

/**
 * @param {string} name
 * @param {import("estree").Node} node
 * @param {import("estree").VariableDeclaration | undefined} declaration
 * @return {ConstantCodeInfo}
 */
function createConstantCodeInfo(name, node, declaration) {
    if (node.type === "VariableDeclaration" && declaration === undefined) {
        declaration = node;
    }
    return {
        name: name,
        optional: false,
        type: declaration !== undefined ? toConstantType(declaration.kind) : undefined,
        typedef: "",
    };
}

module.exports = {
    createConstantCodeInfo,
};