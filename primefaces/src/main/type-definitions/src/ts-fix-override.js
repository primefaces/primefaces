const ts = require("typescript");

const tsx = require("./ts-utils");

/**
 * @param {import("typescript").Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {import("typescript").MethodDeclaration | ts.MethodSignature} node 
 */
function handleMethod(program, docCommentAccessor, node) {
    const overriddenMethods = tsx.getOverriddenMethodsRecursive(program.getTypeChecker(), node);
    const overridesInterface = [...overriddenMethods.keys()].some(tsx.isInterfaceType);
    const overridesClass = [...overriddenMethods.keys()].some(tsx.isClassType);

    if (overridesInterface && !overridesClass) {
        const mods = (/** @type {ts.Modifier[] | undefined} */(node.modifiers));
        if (tsx.isOverride(node) && mods !== undefined) {
            for (let i = mods.length - 1; i >= 0; i -= 1) {
                if (mods[i]?.kind === ts.SyntaxKind.OverrideKeyword) {
                    mods.splice(i, 1);
                }
            }
        }
    }
}

/**
 * @param {ts.Program} program 
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {ts.Node} node 
 * @return {boolean}
 */
function processNode(program, docCommentAccessor, node) {
    if (tsx.isMethodLike(node)) {
        handleMethod(program, docCommentAccessor, node);
    }
    return true;
}

/**
 * @type {TsHookFnFixupAst}
 */
const fixOverride = (program, sourceFiles, docCommentAccessor) => {
    for (const sourceFile of sourceFiles) {
        tsx.depthFirstNode(sourceFile, node => processNode(program, docCommentAccessor, node));
    }
};

module.exports = {
    fixOverride,
};