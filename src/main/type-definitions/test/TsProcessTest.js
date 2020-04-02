//@ts-check

const { main } = require("./base");
const { Paths } = require("../src/constants");
const { createDefaultSeveritySettings, Level } = require("../src/error-types");
const { postprocessDeclarationFile } = require("../src/ts-postprocess");
const { createBaseHooks, createHookFnProcessAst, createHookFnCompilerOptionsCreate, createHookFnCompilerOptionsModify } = require("../src/ts-postprocess-defaulthooks");
const { splitLines } = require("../src/lang");

const Filenames = {
    inputFilename: "input.d.ts",
    expectedFilename: "expected.d.ts",
    errorFilename: "error.txt"
};

const SeveritySettings = createDefaultSeveritySettings({
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
        process: {
            processAst: createHookFnProcessAst(false),
        },
    });
    return [
        ...splitLines(fileContent.ambient.join("\n")),
        ...splitLines(fileContent.module.join("\n")),
    ];
}

/** @type {StartTestFn} */
const startTest = cliArgs => {
    return main(Paths.TsProcessTestDir, Filenames, processInput, cliArgs);
}

module.exports = {
    startTest,
};