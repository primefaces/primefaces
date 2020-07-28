//@ts-check
const { main } = require("./base");

const { Paths } = require("../src/constants");
const { createDefaultSeveritySettings } = require("../src/error-types");
const { postprocessDeclarationFile } = require("../src/ts-postprocess");
const { createBaseHooks, createHookFnValidateProgram, createHookFnValidateReport, createHookFnCompilerOptionsCreate, createHookFnCompilerOptionsModify } = require("../src/ts-postprocess-defaulthooks");
const { splitLines } = require("../src/lang");

const Filenames = {
    inputFilename: "input.d.ts",
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
 * @return {Promise<string[]>}
 */
async function processInput(input, sourceName, sourceLocation, inclusionHandler) {
    const fileContent = await postprocessDeclarationFile(sourceLocation, SeveritySettings, {
        ...createBaseHooks(),
        compilerOptions: {
            createCompilerOptions: createHookFnCompilerOptionsCreate(),
            modifyCompilerOptions: [
                ...createHookFnCompilerOptionsModify({
                    baseLib: true,
                    noTypes: true,
                }),
            ],
        },
        validateProgram: {
            report: createHookFnValidateReport(SeveritySettings, false),
            validate: createHookFnValidateProgram(false),
        },

    });
    return [
        ...splitLines(fileContent.ambient.join("\n")),
        ...splitLines(fileContent.module.join("\n")),
    ];
}

/** @type {StartTestFn} */
const startTest = cliArgs => {
    return main(Paths.TsValidateTestDir, Filenames, processInput, cliArgs);
}

module.exports = {
    startTest,
};