// @ts-check

const ts = require("typescript");
const tsx = require("./ts-utils");

/**
 * Classifies a declaration file. If it contains `import` or `declare module` statements, it is classified as a `module`
 * file. Otherwise, it is classified as a `ambient` file.
 * @param {string} source Source code of the declaration file to classify.
 * @return {DeclarationFileType}
 */
function classifyDeclarationFile(source) {
    const sourceFile = ts.createSourceFile("tmp.js", source, ts.ScriptTarget.Latest, true, ts.ScriptKind.JS);
    const state = {
        encounteredDeclareModule: false,
        encounteredImportStatement: false,
    }
    tsx.depthFirstNode(sourceFile, node => {
        if (ts.isModuleDeclaration(node) && tsx.hasNeitherFlag(node, ts.NodeFlags.Namespace)) {
            state.encounteredDeclareModule = true;
        }
        if (ts.isImportDeclaration(node)) {
            state.encounteredImportStatement = true;
        }
        return true;
    });
    return state.encounteredDeclareModule || state.encounteredImportStatement ? "module" : "ambient";
}

module.exports = {
    classifyDeclarationFile,
};