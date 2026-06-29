//@ts-check

const { join } = require("path");

const { Paths } = require("./constants");
const { handleAndGetError } = require("./error");
const { validateOverride } = require("./ts-check-override");
const { validateSyntaxAndSemantics } = require("./ts-check-syntax-and-semantics");
const { createFallbackCompilerHost, readCompilerOptionsFromTsConfig } = require("./ts-create-compiler");
const { resolveInheritDocs } = require("./ts-resolve-inheritdoc");

/**
 * @return {TsHookFnCompilerHostCreate}
 */
function createHookFnCompilerHostCreate() {
    return (options, sourceFiles) => {
        return createFallbackCompilerHost(options, sourceFiles, [
            join(Paths.NpmTypesDir, "node"),
        ]);
    };
}

/**
 * @param {{baseLib?: boolean, noTypes?: boolean}} param0
 * @return {TsHookFnCompilerOptionsModify[]}
 */
function createHookFnCompilerOptionsModify({
    baseLib = false,
    noTypes = false,
} = {}) {
    return [
        options => {
            options.rootDir = Paths.NpmRootDir;
            options.noEmit = true;
            if (baseLib) {
                options.lib = ["lib.es2015.d.ts"];
            }
            if (noTypes) {
                options.typeRoots = [];
                options.types = [];
            }
        }
    ];
}

/**
 * @return {TsHookFnCompilerOptionsCreate}
 */
function createHookFnCompilerOptionsCreate() {
    return readCompilerOptionsFromTsConfig;
}

/**
 * Takes a TypeScript program and collects all syntactical and semantic errors (types, overrides etc.)
 * @param {boolean} verbose
 * @return {TsHookFnValidateProgram[]}
 */
function createHookFnValidateProgram(verbose) {
    return [
        program => {
            if (verbose) {
                console.log("Validating syntax...");
            }
            return validateSyntaxAndSemantics(program);
        },
        (program, sourceFiles, docCommentAccessor) => {
            if (verbose) {
                console.log("Validating @override and @inheritdoc...");
            }
            return validateOverride(program, sourceFiles, docCommentAccessor);
        }
    ];
}

/**
 * @param {SeveritySettingsConfig} severitySettings
 * @param {boolean} verbose
 * @return {TsHookFnValidateReport[]}
 */
function createHookFnValidateReport(severitySettings, verbose) {
    return [
        validationResult => {
            if (verbose) {
                console.info(`Validation complete, found ${validationResult.errors.length} error(s) and ${validationResult.warnings.length} warning(s).`);
            }
            return [
                ...validationResult.errors.flatMap(v => handleAndGetError(v.type, severitySettings, () => v.message)),
                ...validationResult.warnings.flatMap(v => handleAndGetError(v.type, severitySettings, () => v.message)),
                ...validationResult.suggestions.flatMap(v => handleAndGetError(v.type, severitySettings, () => v.message)),
                ...validationResult.messages.flatMap(v => handleAndGetError(v.type, severitySettings, () => v.message)),
            ];
        }
    ];
}

/**
 * @param {boolean} verbose
 * @return {TsHookFnProcessAst[]}
 */
function createHookFnProcessAst(verbose) {
    return [
        (program, sourceFiles, docCommentAccessor, severitySettings) => {
            if (verbose) {
                console.info("Resolving inheritdoc...");
            }
            resolveInheritDocs(program, sourceFiles, docCommentAccessor, severitySettings);
        },
    ];
}

/**
 * @return {Partial<TsPostProcessingHooks>}
 */
function createBaseHooks() {
    return {
        compilerHost: {
            createCompilerHost: createHookFnCompilerHostCreate(),
        },
        compilerOptions: {
            createCompilerOptions: createHookFnCompilerOptionsCreate(),
            modifyCompilerOptions: createHookFnCompilerOptionsModify(),
        },
    };
}

/**
 * @return {TsPostProcessingHooks}
 */
function createEmptyHooks() {
    return {
        compilerHost: {},
        compilerOptions: {},
        emit: {},
        process: {},
        validateProgram: {},
    };
}

/**
 * @param {SeveritySettingsConfig} severitySettings
 * @param {boolean} verbose
 * @return {Partial<TsPostProcessingHooks>}
 */
function createDefaultHooks(severitySettings, verbose) {
    return {
        ...createBaseHooks(),
        process: {
            processAst: createHookFnProcessAst(verbose),
        },
        validateProgram: {
            report: createHookFnValidateReport(severitySettings, verbose),
            validate: createHookFnValidateProgram(verbose),
        },
    };
}

module.exports = {
    createBaseHooks,
    createDefaultHooks,
    createEmptyHooks,
    createHookFnCompilerHostCreate,
    createHookFnCompilerOptionsCreate,
    createHookFnCompilerOptionsModify,
    createHookFnProcessAst,
    createHookFnValidateProgram,
    createHookFnValidateReport,
};