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
 * @param {TypeDeclarationBundleFiles} sourceFiles 
 * @param {CliArgs} cliArgs
 */
async function generateTypedocs(sourceFiles, cliArgs) {
    const targetDir = cliArgs.typedocOutputDir || join(cliArgs.declarationOutputDir, "jsdocs");
    const app = new typedoc.Application();
    app.bootstrap({
        mode: "file",
        name: "PrimeFaces JavaScript API Docs",
        tsconfig: Paths.TsConfigPath,
        readme: Paths.JsdocReadmePath,
        exclude: createExcludes(cliArgs),
        ignoreCompilerErrors: true,
        includeDeclarations: true,
        listInvalidSymbolLinks: false, // TODO: enable once typedoc support {@link Class#method} syntax
        disableSources: true,
    });
    await withTemporaryFileOnDisk(sourceFiles, Paths.NpmVirtualDeclarationFile, tempFiles => {
        app.generateDocs(app.expandInputFiles([
            tempFiles.ambient,
            tempFiles.module,
            Paths.NpmTypesDir,
        ]), targetDir);
    });
}

module.exports = {
    generateTypedocs,
}