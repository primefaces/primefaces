// @ts-check

import path from "node:path";

import { resolveEntryPointInput } from "../esbuild/entry-points.mjs";
import { joinRegExp } from "../reg-exp.mjs";
import pnpapi from "pnpapi";

/**
 * @typedef {"link" | "expose"} ModuleSplitMode
 */
undefined;

/**
 * @typedef {{
 * moduleName: string;
 * relativePath: string;
 * }} ModuleFileInfo
 */
undefined;

/**
 * @typedef {import("esbuild").Metafile["inputs"][string]} MetaFileInput
 */
undefined;

/**
 * @typedef {{
 * importKindsByPath: Map<string, Set<import("esbuild").ImportKind>>;
 * importsByEntryPoint: Map<string, Set<string>>;
 * moduleFileInfoByPath: Map<string, ModuleFileInfo>;
 * importsByInput: Map<string, Map<string, string>;
 * }} BundleMeta
 */
undefined;

/**
 * @typedef {{
 * readonly baseDir: string;
 * readonly bundleMeta: BundleMeta;
 * readonly helperPath: string;
 * readonly logMessages: string[];
 * readonly scope: string;
 * readonly verbose: boolean;
 * }} PluginExecutionContext
 */
undefined;

/**
 * Defines how a module should be treated.
 * 
 * - pattern: The pattern to match against the module path.
 * - mode: Either "link" or "expose". When set to "link", the module will be linked
 *   via the global scope (`window[scope]`). When set to "expose", the module will
 *   be included in the bundle, and also exposed via the global scope.
 * @typedef {{
 * pattern: RegExp;
 * mode: ModuleSplitMode;
 * }} GlobalCodeSplitModule
*/
undefined;

/**
 * Options for the global code split plugin.
 * 
 * - modules: Defines which modules should be exposed and which modules should be linked.
 *   When there are multiple matches, the order of precedence is "expose", "link".
 *   Any modules for which there is no match are included in the bundle normally.
 * - scope: The global scope name to use for exposing and linking modules. E.g. when set
 *   to Scope, `window.Scope` will be used to expose and link modules.
 * - verbose: When set to true, the plugin will log additional information to the console.
 * @typedef {{
 *   modules: GlobalCodeSplitModule[];
 *   scope: string;
 *   verbose?: boolean;
 * }} GlobalCodeSplitPluginOptions
 */
undefined;

/**
 * @typedef {{
 *   newPlugin: (options: GlobalCodeSplitPluginOptions) => import("esbuild").Plugin;
 *   setMetaFile: (metaFile: import("esbuild").Metafile, baseDir: string) => void;
 * }} GlobalCodeSplitPluginFactory
 */
undefined;

const PluginName = "custom-module-source-plugin";

const NamespaceBase = PluginName;
const NamespaceExpose = `${NamespaceBase}/expose`;
const NamespaceExposeImport = `${NamespaceBase}/expose-import`;
const NamespaceExposeRequire = `${NamespaceBase}/expose-require`;
const NamespaceLinkImport = `${NamespaceBase}/link-import`;
const NamespaceLinkRequire = `${NamespaceBase}/link-require`;

const ScriptExtensions = new Set([".js", ".cjs", ".mjs", ".ts", ".cts", ".mts"]);

/**
 * @template T
 * @param {Set<T> | undefined} set 
 * @param {T[]} values
 */
function setContainsAny(set, ...values) {
    if (set === undefined) {
        return false;
    }
    for (const value of values) {
        if (set.has(value)) {
            return true;
        }
    }
    return false;
}

/**
 * Resolves an absolute path to a file of a module the info regarding that file.
 * E.g. `~/.yarn/berry/cache/package.zip/nodes_modules/moment/moment.js` refers
 * to the module `moment`, and the file is located at the relative path
 * `moment/moment.js`.
 * @param {string} absPath
 * @returns {ModuleFileInfo | undefined} 
 */
function findModuleFileInfoByAbsolutePath(absPath) {
    const locator = pnpapi.findPackageLocator(absPath);
    if (locator && locator.name) {
        const info = pnpapi.getPackageInformation(locator);
        const internalPath = path.relative(info.packageLocation, absPath);
        const relativePath = path.join(locator.name, internalPath);
        const normalizedRelativePath = relativePath.replace(/\\/g, "/");
        return { moduleName: locator.name, relativePath: normalizedRelativePath };
    }
    return undefined;
}

/**
 * Creates the path to the helper module with additional functions for exposing and linking modules.
 * The returned path is a JavaScript string literal, i.e. with quotation marks at the beginning and end.
 * @param {import("esbuild").PluginBuild} build Current ESBuild build.
 * @returns {string} The path to the helper module.
 */
function createHelperPath(build) {
    const baseDir = build.initialOptions.absWorkingDir ?? process.cwd();
    const helperPath = path.resolve(baseDir, "build", "esbuild-plugin", "global-code-split-plugin-helper.mjs");
    return JSON.stringify(helperPath);
}

/**
 * Finds a short descriptive name for the entry points from
 * the given build task.
 * @param {string} baseDir
 * @param {import("esbuild").BuildOptions} buildTask
 */
function findShortBundleName(baseDir, buildTask) {
    const inputs = resolveEntryPointInput(buildTask.entryPoints);
    const relInputs = inputs.map(input => path.relative(baseDir, input));
    if (relInputs.length === 0) {
        return "<empty>";
    }
    return relInputs.length === 1
        ? relInputs[0]
        : `bundle(${relInputs[0]} and ${relInputs.length - 1} more)`;
}

/**
 * Creates a map with the import kind of each bare module specifier.
 * @param {string} baseDir
 * @param {import("esbuild").Metafile} metaFile 
 * @returns {Map<string, Set<import("esbuild").ImportKind>>}
 */
function collectImportKindsByPath(baseDir, metaFile) {
    /** @type {Map<string, Set<import("esbuild").ImportKind>>} */
    const map = new Map();
    for (const input of Object.values(metaFile.inputs)) {
        for (const _import of input.imports) {
            const filePath = _import.path;
            if (filePath !== undefined) {
                const absPath = path.resolve(baseDir, filePath);
                if (!map.has(absPath)) {
                    map.set(absPath, new Set());
                }
                map.get(absPath)?.add(_import.kind);
            }
        }
    }
    return map;
}

/**
 * Creates a map with the module format of each absolute path.
 * @param {string} baseDir
 * @param {import("esbuild").Metafile} metaFile 
 * @returns {Map<string, ModuleFileInfo>}
 */
function collectModuleFileInfoByPath(baseDir, metaFile) {
    /** @type {Map<string, ModuleFileInfo>} */
    const map = new Map();
    for (const filePath of Object.keys(metaFile.inputs)) {
        const absPath = path.resolve(baseDir, filePath);
        const modulePath = findModuleFileInfoByAbsolutePath(absPath);
        if (modulePath !== undefined) {
            map.set(absPath, modulePath);
        }
    }

    return map;
}

/**
 * Collects all imports by input entry point.
 * @param {string} baseDir
 * @param {import("esbuild").Metafile} metaFile
 * @returns {Map<string, Set<string>>} 
 */
function collectImportsByEntryPoint(baseDir, metaFile) {
    /** @type {Map<string, Set<string>>} */
    const map = new Map();
    for (const output of Object.values(metaFile.outputs)) {
        const imports = Object.keys(output.inputs)
            .map(inputPath => metaFile.inputs[inputPath])
            .filter(input => input !== undefined)
            .flatMap(input => input.imports)
            .map(_import => path.resolve(baseDir, _import.path))
            .filter(original => original !== undefined);
        if (output.entryPoint !== undefined) {
            const absInput = path.normalize(path.resolve(baseDir, output.entryPoint));
            map.set(absInput, new Set(imports));
        }
    }
    return map;
}

/**
 * Creates a map with all imports of each input file. This is a map
 * ```txt
 * inputPath -> {importPath -> resolvedPath}
 * ```
 * Where the `inputPath` is the absolute path to the input file, the
 * `importPath` is the path as it was used in the `import` statement,
 * and the `resolvedPath` is the absolute resolved path to the
 * imported file.
 * @param {string} baseDir 
 * @param {import("esbuild").Metafile} metaFile
 * @returns {Map<string, Map<string, string>>}
 */
function collectImportsByInput(baseDir, metaFile) {
    /** @type {Map<string, Map<string, string>>} */
    const map = new Map();
    for (const [inputPath, input] of Object.entries(metaFile.inputs)) {
        const absInputPath = path.resolve(baseDir, inputPath);
        const imports = new Map();
        for (const _import of input.imports) {
            const absImportPath = path.resolve(baseDir, _import.path);
            imports.set(_import.original, absImportPath);
        }
        map.set(absInputPath, imports);
    }
    return map;
}

/**
 * @param {string} baseDir
 * @param {import("esbuild").Metafile} metaFile 
 * @returns {BundleMeta}
 */
function createBundleMeta(baseDir, metaFile) {
    return {
        importKindsByPath: collectImportKindsByPath(baseDir, metaFile),
        importsByEntryPoint: collectImportsByEntryPoint(baseDir, metaFile),
        importsByInput: collectImportsByInput(baseDir, metaFile),
        moduleFileInfoByPath: collectModuleFileInfoByPath(baseDir, metaFile),
    };
}

/**
 * Registers the plugin hooks for exposing modules to the global scope.
 * 
 * The idea is as follows: We add a resolve hook that rewrites each entry point
 * to inject additional modules that expose the external modules to the global
 * scope. For example, if we wish to expose "jquery" and the entry point
 * `bundle.js` looks like this:
 * 
 * ```js
 * // bundle.js
 * import $ from "jquery";
 * $(document).ready(() => console.log("Hello, world!"));
 * ```
 * 
 * Then we rewrite the entry point like this:
 * 
 * ```js
 * import "@global/expose/bundle.js";
 * ```
 * 
 * The virtual entry point `@global/expose/bundle.js` contains the following code:
 * 
 * ```js
 * import "bundle.js";
 * import "@global/expose/bundle.js";
 * import "@global/require/bundle.js";
 * ```
 * 
 * And finally, the virtual modules `@global/import/bundle.js` and
 * `@global/require/bundle.js` expose the modules to the global scope
 * like this:
 * 
 * ```js
 * // "@global/import/bundle.js"
 * const Scope = window[scope] ??= {};
 * import * as Module from "jquery";
 * const Container = Scope["jquery"] = Scope["jquery"] || {cjs: {}, esm: {}};
 * Container.esm = Module;
 * 
 * // "@global/require/bundle.js"
 * const Scope = window[scope] ??= {};
 * const Module = require("jquery");
 * const Container = Scope["jquery"] ??= {cjs: {}, esm: {}};
 * Container.cjs = Module;
 * ```
 * 
 * We expose both the ESM and CJS variants of the module to the global scope.
 * This is because we may have both CommonJs and ESM modules in the bundle, and
 * both might import the "jquery" module. As an optimization, we analyze the bundle
 * beforehand and skip exposing the CommonJS or ESM export if it is never used.
 * 
 * This is also necessary because some module may have different content for the
 * CommonJS and ESM exports (they shouldn't, but that's reality). For example,
 * the ESM variant of `@fullcalendar@5.x` includes the CSS imports, while the
 * CommonJS variant does not.
 * 
 * @param {import("esbuild").PluginBuild} build 
 * @param {PluginExecutionContext} context
 * @param {string[]} exposePaths
 */
function registerExposeHooks(build, context, exposePaths) {
    const entryPointInputs = resolveEntryPointInput(build.initialOptions.entryPoints).map(e => path.isAbsolute(e) ? e : `${path.sep}${e}`);
    const filterEntryPoints = joinRegExp(entryPointInputs, "", "$");

    // Replace the entry point with a virtual module that forwards to the actual entry point,
    // but also exposes all configured modules via the global scope.
    build.onResolve({ filter: filterEntryPoints, namespace: "file" }, args => {
        if (args.kind !== "entry-point") {
            throw new Error("Should have been an entry point, but wasn't: " + args.path);
        }
        return { path: `@global/expose/${args.path}`, namespace: NamespaceExpose };
    });

    // Add resolve hook so esbuild knows we have a loader that can resolve these virtual modules.
    build.onResolve({ filter: /^@global\/(import|require)\/.*$/, namespace: NamespaceExpose }, args => {
        const prefix = args.path.startsWith("@global/import") ? "@global/import/" : "@global/require/";
        const entryPoint = args.path.substring(prefix.length);
        const namespace = args.path.startsWith("@global/import") ? NamespaceExposeImport : NamespaceExposeRequire;
        const absEntryPoint = path.normalize(path.resolve(args.resolveDir, entryPoint));
        return { path: `${prefix}${absEntryPoint}`, namespace };
    });

    // Expose the module to the global scope: windows[Scope][moduleName]
    build.onLoad({ filter: /^@global\/expose\/.*$/, namespace: NamespaceExpose }, args => {
        const entryPoint = args.path.substring("@global/expose/".length);
        const contents = [
            `import "${entryPoint}";`,
            `import "@global/require/${entryPoint}";`,
            `import "@global/import/${entryPoint}";`,
        ];
        return { contents: contents.join("\n"), resolveDir: context.baseDir };
    });

    // Expose the ESM variant of the modules to the global scope: windows[Scope][moduleName].esm
    build.onLoad({ filter: /^@global\/import\/.*$/, namespace: NamespaceExposeImport }, args => {
        // Find all modules that are imported (directly or transitively) by this
        // entry point and that are marked for exposure to the global scope.
        const entryPoint = args.path.substring("@global/expose/".length);
        const entryPointImports = context.bundleMeta.importsByEntryPoint.get(entryPoint) ?? new Set();
        const filePaths = exposePaths
            .filter(m => entryPointImports.has(m))
            .filter(m => setContainsAny(context.bundleMeta.importKindsByPath.get(m), "import-statement", "dynamic-import"));
        if (filePaths.length === 0) {
            return { contents: "export {}", resolveDir: context.baseDir };
        }

        if (context.verbose) {
            for (const filePath of filePaths) {
                const info = context.bundleMeta.moduleFileInfoByPath.get(filePath);
                context.logMessages.push(`Exposing module <${info?.relativePath ?? filePath}> as ESM`);
            }
        }

        // Import all modules and expose their contents to the global window scope.
        const contents = [`import { exposeEsmModule } from ${context.helperPath};`];
        let i = 1;
        for (const filePath of filePaths) {
            const fileInfo = context.bundleMeta.moduleFileInfoByPath.get(filePath);
            if (fileInfo === undefined) {
                throw new Error("Module file info not found for path: " + filePath);
            }
            const linkName = fileInfo.relativePath;
            contents.push(
                `import * as Module${i} from "${filePath}";`,
                `exposeEsmModule("${context.scope}", "${linkName}", Module${i});`
            );
            i += 1;
        }
        return { contents: contents.join("\n"), resolveDir: context.baseDir };
    });

    // Expose the CommonJS variant of the modules to the global scope: windows[Scope][moduleName].cjs
    build.onLoad({ filter: /^@global\/require\/.*$/, namespace: NamespaceExposeRequire }, args => {
        // Find all modules that are imported (directly or transitively) by this
        // entry point and that are marked for exposure to the global scope.
        const entryPoint = args.path.substring("@global/require/".length);
        const entryPointImports = context.bundleMeta.importsByEntryPoint.get(entryPoint) ?? new Set();
        const filePaths = exposePaths
            .filter(p => entryPointImports.has(p))
            .filter(p => setContainsAny(context.bundleMeta.importKindsByPath.get(p), "require-call", "require-resolve"));
        if (filePaths.length === 0) {
            return { contents: "module.exports = {}", resolveDir: context.baseDir };
        }

        if (context.verbose) {
            for (const filePath of filePaths) {
                const fileInfo = context.bundleMeta.moduleFileInfoByPath.get(filePath);
                context.logMessages.push(`Exposing module <${fileInfo?.relativePath ?? filePath}> as CommonJS`);
            }
        }

        // Import all modules and expose their contents to the global window scope.
        const contents = [`const { exposeCommonJsModule } = require(${context.helperPath});`];
        for (const filePath of filePaths) {
            const fileInfo = context.bundleMeta.moduleFileInfoByPath.get(filePath);
            if (fileInfo === undefined) {
                throw new Error("Module file info not found for path: " + filePath);
            }
            const linkName = fileInfo.relativePath;
            contents.push(`exposeCommonJsModule("${context.scope}", "${linkName}", require("${filePath}"))`);
        }
        return { contents: contents.join("\n"), resolveDir: context.baseDir };
    });
}

/**
 * Registers the plugin hooks for linking modules via the global scope.
 * 
 * Instead of including the module in the bundle, it is loaded from the global
 * scope. For example, if we wish to link "jquery" and the scope was set to
 * `Scope`, the import statement:
 * 
 * ```js
 * import $ from "jquery";
 * ```
 * 
 * Would be rewritten to:
 * 
 * ```js
 * // ESM
 * const Module = window.Scope["jquery"];
 * if (!Module || !Module.esmDefined) throw new Error("ESM module not found: jquery");
 * $ = Module.esm.default;
 * 
 * // CommonJS
 * const Module = window.Scope["jquery"];
 * if (!Module || !Module.cjsDefined) throw new Error("CJS module not found: jquery");
 * $ = Module.cjs;
 * ```
 * 
 * @param {import("esbuild").PluginBuild} build 
 * @param {PluginExecutionContext} context
 * @param {Set<string>} externalModulesToLink
 */
function registerLinkHooks(build, context, externalModulesToLink) {
    const helperPath = createHelperPath(build);

    // Check if an imported or required module was configured to be linked.
    // If so, resolve the module to a virtual module that retrieves
    // the module from the global scope.
    build.onResolve({ filter: /.*/, namespace: "file" }, args => {
        const isImport = args.kind === "dynamic-import" || args.kind === "import-statement";
        const isRequire = args.kind === "require-call" || args.kind === "require-resolve";
        // Use pre-computed data from the meta file to resolve the requested module. 
        const imports = context.bundleMeta.importsByInput.get(args.importer);
        const resolved = imports?.get(args.path);
        // If the imported module should be omitted from the bundle and retrieved from the global scope
        // instead, forward to a virtual module that simply access the corresponding global variable.
        if (resolved && ScriptExtensions.has(path.extname(resolved))) {
            const shouldLink = externalModulesToLink.has(resolved);
            if (shouldLink && isImport) {
                return { path: `@global/link:${resolved}`, namespace: NamespaceLinkImport };
            }
            if (shouldLink && isRequire) {
                return { path: `@global/link:${resolved}`, namespace: NamespaceLinkRequire };
            }
        }
        return undefined;
    });

    // Load an imported ESM module from the global scope:
    // windows[Scope][moduleName].esm
    build.onLoad(
        { filter: /@global\/link:.+/, namespace: NamespaceLinkImport },
        args => {
            const origPath = args.path.substring("@global/link:".length);
            // Don't use the absolute path from the file system as the key to
            // store the module in the global window scope. Instead, use the
            // relative path to the module. For example, instead of
            // `/home/user/.yarn/berry/cache/jquery.zip/node_modules/jquery/dist/jquery.js`,
            // use `jquery/dist/jquery.js`.
            const fileInfo = context.bundleMeta.moduleFileInfoByPath.get(origPath);
            if (fileInfo === undefined) {
                throw new Error("Module file info not found for path: " + origPath);
            }
            const relativeModulePath = fileInfo.relativePath;

            if (context.verbose) {
                context.logMessages.push(`Retrieving ESM module <${relativeModulePath}> from the global scope.`);
            }

            return {
                contents: [
                    `const { retrieveLinkedEsmModule } = require(${helperPath});`,
                    `const mod = retrieveLinkedEsmModule("${context.scope}", "${relativeModulePath}");`,
                    `module.exports = mod;`,
                ].join("\n"),
                resolveDir: build.initialOptions.absWorkingDir ?? process.cwd(),
            };
        }
    );

    // Load an imported CJS module from the global scope:
    // windows[Scope][moduleName].cjs
    build.onLoad(
        { filter: /@global\/link:.+/, namespace: NamespaceLinkRequire },
        args => {
            const origPath = args.path.substring("@global/link:".length);

            // Don't use the absolute path from the file system as the key to
            // store the module in the global window scope. Instead, use the
            // relative path to the module. For example, instead of
            // `/home/user/.yarn/berry/cache/jquery.zip/node_modules/jquery/dist/jquery.js`,
            // use `jquery/dist/jquery.js`.
            const fileInfo = context.bundleMeta.moduleFileInfoByPath.get(origPath);
            if (fileInfo === undefined) {
                throw new Error("Module file info not found for path: " + origPath);
            }
            const relativeModulePath = fileInfo.relativePath;

            if (context.verbose) {
                context.logMessages.push(`Retrieving CommonJS module <${relativeModulePath} from the global scope.`);
            }

            return {
                contents: [
                    `const { retrieveLinkedCommonJsModule } = require(${helperPath});`,
                    `module.exports = retrieveLinkedCommonJsModule("${context.scope}", "${relativeModulePath}");`,
                ].join("\n"),
                resolveDir: build.initialOptions.absWorkingDir ?? process.cwd(),
            };
        }
    );
}

/**
 * @param {GlobalCodeSplitModule[]} modules 
 * @param {BundleMeta} bundleMeta
 * @param {ModuleSplitMode} mode
 * @returns {Set<string>}
 */
function matchPackagePaths(modules, bundleMeta, mode) {
    const patterns = modules.filter(m => m.mode === mode).map(m => m.pattern);
    const paths = new Set();
    for (const [absPath, moduleFileInfo] of bundleMeta.moduleFileInfoByPath) {
        const matches = patterns.some(p => p.test(moduleFileInfo.relativePath));
        if (matches) {
            paths.add(absPath);
        }
    }
    return paths;
}

/**
 * Plugin for esbuild that for linking code together via the global scope.
 * 
 * For each import name, you can mark it as either `link` or `include`.
 * 
 * Imports marked as `include` will be included in the bundle and exposed
 * via the global scope (`window[scope]`).
 * 
 * Imports marked as `link` will be omitted from the bundle and instead
 * accessed via the global scope (`window[scope]`).
 * 
 * Any other imports not mentioned in the configuration will processed as normal.
 * 
 * You can customize the global `scope` name via the `scope` option.
 * 
 * @returns {GlobalCodeSplitPluginFactory}
 */
export function globalCodeSplitPluginFactory() {
    /** @type {BundleMeta | undefined}*/
    let bundleMeta = undefined;
    return {
        newPlugin: options => {
            const scope = options.scope;
            const modules = options.modules;
            const verbose = options.verbose ?? false;
            return {
                name: PluginName,
                setup: build => {
                    const finalBundleMeta = bundleMeta;
                    if (finalBundleMeta === undefined) {
                        throw new Error("The meta file must be set before the plugin is used.");
                    }

                    /** @type {PluginExecutionContext} */
                    const context = {
                        baseDir: build.initialOptions.absWorkingDir ?? process.cwd(),
                        bundleMeta: finalBundleMeta,
                        helperPath: createHelperPath(build),
                        logMessages: [],
                        scope,
                        verbose,
                    };

                    const exposePaths = matchPackagePaths(modules, finalBundleMeta, "expose");
                    const linkPaths = matchPackagePaths(modules, finalBundleMeta, "link");
                    // Order of precedence: expose, link
                    exposePaths.forEach(m => linkPaths.delete(m));

                    if (linkPaths.size > 0) {
                        registerLinkHooks(build, context, linkPaths);
                    }

                    if (exposePaths.size > 0) {
                        registerExposeHooks(build, context, [...exposePaths]);
                    }

                    build.onEnd(() => {
                        if (context.logMessages.length > 0) {
                            const bundleName = findShortBundleName(context.baseDir, build.initialOptions)
                            console.log(`[${bundleName}]\n${context.logMessages.join("\n")}\n`);
                        }
                    });
                },
            }
        },

        setMetaFile: (newMetaFile, baseDir) => {
            bundleMeta = createBundleMeta(baseDir, newMetaFile);
        },
    };
};

