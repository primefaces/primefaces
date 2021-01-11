//@ts-check

const { full } = require("acorn-walk");
const { makeVisitors } = require("./acorn-util");

/**
 * Scans the entire source code (AST) for function, constants and classes that
 * are to be documented. You can customize what to include in the documentation
 * by defining your own custom handlers. Each node is passed to the handlers,
 * the first handler that returns a result wins.
 * @param {CommentedAst<import("estree").Program>} program
 * @param {InclusionHandler} inclusionHandler
 * @param {SeveritySettingsConfig} severitySettings
 * @param {DocumentableHandler[]} handlers
 * @return {DocumentableInfo}
 */
function aggregateDocumentable(program, inclusionHandler, severitySettings, handlers) {
    /** @type {ConstantDef[]} */
    const constants = [];
    /** @type {FunctionDef[]} */
    const functions = [];
    /** @type {ObjectDef[]} */
    const objects = [];

    /** @type {DocumentableState} */
    const state = {};
    full(program.node, ()=>{}, makeVisitors((node, state) => {
        let recurse = true;
        for (const handler of handlers) {
            const handleResult = handler(node, program, inclusionHandler, severitySettings);
            if (handleResult !== undefined) {
                recurse = handleResult.recurse;
                constants.push(...handleResult.constants);
                functions.push(...handleResult.functions);
                objects.push(...handleResult.objects);
                break;
            }
        }
        return recurse;
    }, state), state);

    return {
        constants: constants,
        functions: functions,
        objects: objects,
    };
}

module.exports = {
    aggregateDocumentable,
}