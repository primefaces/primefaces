//@ts-check

const { join } = require("path");
const ts = require("typescript");

const { Paths } = require("./constants");
const { arePathsEqual } = require("./lang-fs");

/**
 * @return {Promise<import("typescript").CompilerOptions>}
 */
async function readCompilerOptionsFromTsConfig() {
    const tsconfig = ts.readConfigFile(Paths.TsConfigPath, ts.sys.readFile);
    if (tsconfig.error) {
        throw new Error("Could not read configuration file " + Paths.TsConfigPath + ": " + tsconfig.error.messageText);
    }
    if (!tsconfig.config) {
        throw new Error("Could not read configuration file " + Paths.TsConfigPath);
    }
    const conversionResult = ts.convertCompilerOptionsFromJson(
        tsconfig.config.compilerOptions,
        Paths.NpmRootDir,
        Paths.TsConfigPath
    );
    if (conversionResult.errors.length > 0) {
        throw new Error("Could not parse configuration file " + Paths.TsConfigPath + ": " + conversionResult.errors.map(x => x.messageText).join("\n"));
    }
    const options = conversionResult.options;
    options.rootDir = Paths.NpmRootDir;
    return options;
}

/**
 * @param {ts.CompilerOptions} options 
 * @param {TypeDeclarationBundleFiles} declarationFile
 * @param {string[]} moduleSearchLocations 
 * @return {ts.CompilerHost}
 */
function createFallbackCompilerHost(options, declarationFile, moduleSearchLocations) {
    const host = ts.createCompilerHost(options);

    host.resolveTypeReferenceDirectives = (typeReferenceDirectiveNames, containingFile, redirectedReference, options) => {
        // Declaration file may be located in a directory outside the npm dir
        if (arePathsEqual(containingFile, declarationFile.ambient)) {
            containingFile = Paths.NpmVirtualDeclarationFile.ambient;
        }
        if (arePathsEqual(containingFile, declarationFile.module)) {
            containingFile = Paths.NpmVirtualDeclarationFile.module;
        }
        /** @type {Map<string, ts.ResolvedTypeReferenceDirective | undefined>} */
        const cache = new Map();
        return typeReferenceDirectiveNames.map(name => {
            if (cache.has(name)) {
                return cache.get(name);
            }
            else {
                const result = ts.resolveTypeReferenceDirective(name, containingFile, options, host, redirectedReference).resolvedTypeReferenceDirective;
                cache.set(name, result);
                return result;
            }
        });
    };

    host.resolveModuleNames = (moduleNames, containingFile, reusedName, redirectedReference, options) => {
        return moduleNames.map(moduleName => {
            // Declaration file may be located in a directory outside the npm dir
            if (arePathsEqual(containingFile, declarationFile.ambient)) {
                containingFile = Paths.NpmVirtualDeclarationFile.ambient;
            }
            if (arePathsEqual(containingFile, declarationFile.module)) {
                containingFile = Paths.NpmVirtualDeclarationFile.module;
            }
            // Try to use standard resolution
            const resolutionResult = ts.resolveModuleName(moduleName, containingFile, options, {
                fileExists: ts.sys.fileExists,
                readFile: ts.sys.readFile,
            });
            if (resolutionResult.resolvedModule) {
                return resolutionResult.resolvedModule;
            }
            else {
                // check fallback locations, for simplicity assume that module at location
                // should be represented by '.d.ts' file
                for (const location of moduleSearchLocations) {
                    const modulePath = join(location, moduleName + ".d.ts");
                    if (ts.sys.fileExists(modulePath)) {
                        return {
                            resolvedFileName: modulePath
                        };
                    }
                }
            }
            // Module not found
            return undefined;
        });
    };
    
    return host;
}

module.exports = {
    readCompilerOptionsFromTsConfig,
    createFallbackCompilerHost,
};