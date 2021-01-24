//@ts-check
const { main } = require("./base");

const { parseJsProgram } = require("../src/acorn-util");
const { Paths } = require("../src/constants");
const { documentFunction } = require("../src/document-function");
const { documentObject } = require("../src/document-object");
const { createDefaultSeveritySettings, Level } = require("../src/error-types");

const Filenames = {
    inputFilename: "input.js",
    expectedFilename: "expected.d.ts",
    errorFilename: "error.txt"
};

const SeveritySettings = createDefaultSeveritySettings({
    tagMissingType: Level.ignore,
    symbolMissingDesc: Level.ignore,
    propMissingDesc: Level.ignore,
    tagMissingDesc: Level.ignore,
    tagParamMissingDesc: Level.ignore,
    tagReturnMissingDesc: Level.ignore,
    tagTemplateMissingDesc: Level.ignore,
    tagTypedefMissingDesc: Level.ignore,
});


/**
 * @param {string} input 
 * @param {string} sourceName 
 * @param {TypeDeclarationBundleFiles} sourceLocation
 * @param {InclusionHandler} inclusionHandler
 * @return {string[]}
 */
function processInput(input, sourceName, sourceLocation, inclusionHandler) {
    const program = parseJsProgram(input, sourceName, sourceLocation.ambient, "latest");
    const firstNode = program.node.body.length === 1 && program.node.body[0];
    if (
        firstNode &&
        firstNode.type === "ExpressionStatement" &&
        firstNode.expression.type === "ObjectExpression"
    ) {
        const jsdoc = program.map.get(firstNode);
        if (jsdoc !== undefined) {
            return documentObject({
                classDefinition: firstNode.expression,
                comments: jsdoc,
                name: sourceName,
                namespace: ["PrimeFaces", "widget"],
                kind: "object",
                spec: {
                    abstract: false,
                    additionalTags: [],
                    description: "",
                    extends: new Set(),
                    generics: [],
                    implements: new Set(),
                    type: "class",
                },
            }, program, inclusionHandler, SeveritySettings);
        }
        else {
            throw new Error("Test component input file must consist of a single doc comment and an expression statement with an object, function or type literal");
        }
    }
    else if (
        firstNode &&
        firstNode.type === "ExpressionStatement" &&
        firstNode.expression.type === "FunctionExpression"
    ) {
        const jsdoc = program.map.get(firstNode);
        if (jsdoc !== undefined) {
            return documentFunction({
                comments: jsdoc,
                functionNode: firstNode.expression,
                method: undefined,
                name: sourceName,
                kind: "function",
                namespace: ["PrimeFaces", "method"],
                spec: undefined,
            }, SeveritySettings);
        }
        else {
            throw new Error("Test component input file must consist of a single doc comment and an expression statement with an object, function or type literal");
        }
    }
    else {
        throw new Error("Test component input file must consist of a single doc comment and an expression statement with an object literal");
    }
}

/** @type {StartTestFn} */
const startTest = cliArgs => {
    return main(Paths.ComponentsTestDir, Filenames, processInput, cliArgs);
}

module.exports = {
    startTest,
};