//@ts-check

const { join } = require("path");
const typedoc = require("typedoc");

const { Paths } = require("./constants");
const { withTemporaryFileOnDisk } = require("./lang-fs");

/**
 * @param {CliArgs} cliArgs 
 * @return {string[]}
 */
function createExcludes(cliArgs) {
    if (cliArgs.exclusionTags.length === 0) {
        return ["node_modules/**/*"];
    }
    else {
        return [
            `node_modules/!(@types|${cliArgs.includeModules.join("|")})/**/*`,
            `node_modules/@types/!(${cliArgs.includeModules.join("|")})/**/*`
        ];
    }
}

/**
 * Generates typedocs for the given `*.d.ts` file.
 * @param {string} sourceFileName 
 * @param {CliArgs} cliArgs
 */
function generateTypedocs(sourceFileName, cliArgs) {
    const targetDir = cliArgs.typedocOutputDir || join(cliArgs.outputDir, "jsdocs");
    const app = new typedoc.Application();
    app.bootstrap({
        mode: "file",
        name: "PrimeFaces JavaScript API Docs",
        tsconfig: Paths.TsConfigPath,
        readme: Paths.JsdocReadmePath,
        exclude: createExcludes(cliArgs),
        ignoreCompilerErrors: true,
        includeDeclarations: true,
        disableSources: true,
    });
    withTemporaryFileOnDisk(sourceFileName, Paths.NpmVirtualDeclarationFile, file => {
        app.generateDocs(app.expandInputFiles([
            file,
            Paths.NpmTypesDir,
        ]), targetDir);
    });
}

module.exports = {
    generateTypedocs,
}