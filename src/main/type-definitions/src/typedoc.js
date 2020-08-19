//@ts-check

const { join, dirname } = require("path");
const typedoc = require("typedoc");

const { Paths } = require("./constants");
const { readDir, isExistingFileOrDir, withTemporaryFileOnDisk } = require("./lang-fs");

/**
 * @param {CliArgs} cliArgs 
 * @return {Promise<string[]>}
 */
async function createExcludes(cliArgs) {
    const packageJson = await isExistingFileOrDir(cliArgs.packageJson);
    if (cliArgs.includeModules.length === 0) {
        return ["node_modules/**/*"];
    }
    else if (packageJson.exists) {
        const nodeModulesDir = join(dirname(cliArgs.packageJson), "node_modules");
        const nodeModulesTypesDir = join(dirname(cliArgs.packageJson), "node_modules", "@types");
        const main = await readDir(nodeModulesDir);
        const types = await readDir(nodeModulesTypesDir);
        const excludesMain = main
            .filter(pkg => !pkg.isFile())
            .map(pkg => pkg.name)
            .filter(pkg => pkg !== "@types")
            .filter(pkg => !cliArgs.includeModules.includes(pkg))
            .map(pkg => `node_modules/${pkg}/**/*`);
        const excludesTypes = types
            .filter(pkg => !pkg.isFile())
            .map(pkg => pkg.name)
            .filter(pkg => pkg !== "@types")
            .filter(pkg => !cliArgs.includeModules.includes(pkg))
            .map(pkg => `node_modules/@types/${pkg}/**/*`);
        return [...excludesMain, ...excludesTypes];
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
    /** @type {Partial<import("typedoc").TypeDocAndTSOptions>} */
    const typedocOpts = {
        mode: "file",
        name: "PrimeFaces JavaScript API Docs",
        tsconfig: Paths.TsConfigPath,
        readme: Paths.JsdocReadmePath,
        exclude: await createExcludes(cliArgs),
        ignoreCompilerErrors: true,
        includeDeclarations: true,
        listInvalidSymbolLinks: false, // TODO: enable once typedoc support {@link Class#method} syntax
        disableSources: true,
    };
    console.log("Typedoc options:", JSON.stringify(typedocOpts));
    app.bootstrap(typedocOpts);
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