//@ts-check
const { main } = require("./base");

const { parseJsProgram } = require("../src/acorn-util");
const { aggregateDocumentable } = require("../src/aggregate-documentable");
const { aggregateHandlerDefaults } = require("../src/aggregate-defaults");
const { aggregateHandlerWidgets } = require("../src/aggregate-widgets");
const { Paths } = require("../src/constants");
const { createDefaultSeveritySettings } = require("../src/error-types");
const { documentConstant } = require("../src/document-constant");
const { documentFunction } = require("../src/document-function");
const { documentObject } = require("../src/document-object");

const Filenames = {
    inputFilename: "input.js",
    expectedFilename: "expected.d.ts",
    errorFilename: "error.txt"
};

const SeveritySettings = createDefaultSeveritySettings({

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
    const documentable = aggregateDocumentable(program, inclusionHandler, SeveritySettings, [
        aggregateHandlerWidgets,
        aggregateHandlerDefaults,
    ]);
    return [
        ...documentable.constants.flatMap(x => documentConstant(x, SeveritySettings)),
        ...documentable.functions.flatMap(x => documentFunction(x, SeveritySettings)),
        ...documentable.objects.flatMap(x => documentObject(x, program, inclusionHandler, SeveritySettings)),
    ];
}


/** @type {StartTestFn} */
const startTest = cliArgs => {
    return main(Paths.AggregateTestDir, Filenames, processInput, cliArgs);
}

module.exports = {
    startTest,
};