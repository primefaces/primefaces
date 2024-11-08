// @ts-check

import path from "node:path";

import { resolveEntryPointInput } from "./esbuild-util.mjs";
import { joinRegExp } from "./helper.mjs";

/**
 * @typedef {"link" | "expose"} ModuleSplitMode
 */
undefined;

/**
 * @typedef {{
 *  allImports: string[];
 *  importKinds: Map<string, Set<import("esbuild").ImportKind>>;
 *  importsByEntryPoint: Map<string, Set<string>>;
 * }} BundleMeta
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
 * @typedef {{
 *   modules: GlobalCodeSplitModule[];
 *   scope: string;
 * }} GlobalCodeSplitPluginOptions
 */
undefined;

/**
 * @typedef {{
 *   newPlugin: (options: GlobalCodeSplitPluginOptions) => import("esbuild").Plugin;
 *   setMetaFile: (metaFile: import("esbuild").Metafile) => void;
 * }} GlobalCodeSplitPluginFactory
 */
undefined;

const PluginName = "custom-module-source-plugin";

const NamespaceExpose = "custom-module-source-plugin/expose";
const NamespaceExposeImport = "custom-module-source-plugin/expose-import";
const NamespaceExposeRequire = "custom-module-source-plugin/expose-require";
const NamespaceLinkImport = "custom-module-source-plugin/link-import";
const NamespaceLinkRequire = "custom-module-source-plugin/link-require";

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
 * Collects all kinds of imports used for a module path. For example,
 * jquery is often imported both via an import statement and a require call.
 * @param {import("esbuild").Metafile} metaFile 
 * @returns {Map<string, Set<import("esbuild").ImportKind>>}
 */
function collectImportKinds(metaFile) {
    /** @type {Map<string, Set<import("esbuild").ImportKind>>} */
    const map = new Map();
    for (const input of Object.values(metaFile.inputs)) {
        for (const _import of input.imports) {
            const original = _import.original;
            if (original !== undefined) {
                if (!map.has(original)) {
                    map.set(original, new Set());
                }
                map.get(original)?.add(_import.kind);
            }
        }
    }
    return map;
}

/**
 * Collects all imports by input entry point.
 * @param {import("esbuild").BuildOptions} buildTask
 * @param {import("esbuild").Metafile} metaFile
 * @returns {Map<string, Set<string>>} 
 */
function collectImportsByEntryPoint(buildTask, metaFile) {
    const baseDir = buildTask.absWorkingDir ?? process.cwd();

    /** @type {Map<string, Set<string>>} */
    const map = new Map();
    for (const output of Object.values(metaFile.outputs)) {
        const imports = Object.keys(output.inputs)
            .map(inputPath => metaFile.inputs[inputPath])
            .filter(input => input !== undefined)
            .flatMap(input => input.imports)
            .map(_import => _import.original)
            .filter(original => original !== undefined);
        if (output.entryPoint !== undefined) {
            const absInput = path.normalize(path.resolve(baseDir, output.entryPoint));
            map.set(absInput, new Set(imports));
        }
    }
    return map;
}

/**
 * Collects all imports from all entry points.
 * @param {import("esbuild").Metafile} metaFile
 * @returns {string[]} 
 */
function collectAllImports(metaFile) {
    const allImports = new Set();
    for (const input of Object.values(metaFile.inputs)) {
        for (const _import of input.imports) {
            const original = _import.original;
            if (original !== undefined) {
                allImports.add(original);
            }
        }
    }
    return [...allImports];
}

/**
 * @param {import("esbuild").BuildOptions} buildTask
 * @param {import("esbuild").Metafile} metaFile 
 * @returns {BundleMeta}
 */
function createBundleMeta(buildTask, metaFile) {
    return {
        allImports: collectAllImports(metaFile),
        importKinds: collectImportKinds(metaFile),
        importsByEntryPoint: collectImportsByEntryPoint(buildTask, metaFile),
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
 * @param {string} scope
 * @param {string[]} includeModules
 * @param {BundleMeta} bundleMeta
 */
function registerExposeHooks(build, scope, includeModules, bundleMeta) {
    const baseDir = build.initialOptions.absWorkingDir ?? process.cwd();
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

    // Expose the ESM variant of the module to the global scope: windows[Scope][moduleName].esm
    build.onLoad({ filter: /^@global\/expose\/.*$/, namespace: NamespaceExpose }, args => {
        const entryPoint = args.path.substring("@global/expose/".length);
        const contents = [
            `import "${entryPoint}";`,
            `import "@global/require/${entryPoint}";`,
            `import "@global/import/${entryPoint}";`,
        ];
        return { contents: contents.join("\n"), resolveDir: baseDir };
    });

    // Expose the ESM variant of the modules to the global scope: windows[Scope][moduleName].esm
    build.onLoad({ filter: /^@global\/import\/.*$/, namespace: NamespaceExposeImport }, args => {
        const entryPoint = args.path.substring("@global/expose/".length);
        const entryPointImports = bundleMeta.importsByEntryPoint.get(entryPoint) ?? new Set();
        const moduleNames = includeModules
            .filter(moduleName => entryPointImports.has(moduleName))
            .filter(moduleName => setContainsAny(bundleMeta.importKinds.get(moduleName), "import-statement", "dynamic-import"));

        if (moduleNames.length === 0) {
            return { contents: "export {}", resolveDir: baseDir };
        }

        let i = 1;
        const contents = [`const Scope = window.${scope} ??= {};`];
        for (const moduleName of moduleNames) {
            contents.push(
                `import * as Module${i} from "${moduleName}";`,
                `const Container${i} = Scope["${moduleName}"] = Scope["${moduleName}"] || {cjs: undefined, esm: undefined};`,
                `Container${i}.esm = Module${i};`
            );
            i += 1;
        }
        return { contents: contents.join("\n"), resolveDir: baseDir };
    });

    // Expose the CommonJS variant of the modules to the global scope: windows[Scope][moduleName].cjs
    build.onLoad({ filter: /^@global\/require\/.*$/, namespace: NamespaceExposeRequire }, args => {
        const entryPoint = args.path.substring("@global/require/".length);
        const entryPointImports = bundleMeta.importsByEntryPoint.get(entryPoint) ?? new Set();
        const moduleNames = includeModules
            .filter(moduleName => entryPointImports.has(moduleName))
            .filter(moduleName => setContainsAny(bundleMeta.importKinds.get(moduleName), "require-call", "require-resolve"));

        if (moduleNames.length === 0) {
            return { contents: "module.exports = {}", resolveDir: baseDir };
        }

        let i = 1;
        const contents = [`const Scope = window.${scope} ??= {};`];
        for (const moduleName of moduleNames) {
            contents.push(
                `const Module${i} = require("${moduleName}");`,
                `const Container${i} = Scope["${moduleName}"] ??= {cjs: undefined, esm: undefined};`,
                `Container${i}.cjs = Module${i};`
            );
            i += 1;
        }
        return { contents: contents.join("\n"), resolveDir: baseDir };
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
 * if (!Module || !Module.esm) throw new Error("ESM module not found: jquery");
 * $ = Module.esm.default;
 * 
 * // CommonJS
 * const Module = window.Scope["jquery"];
 * if (!Module || !Module.cjs) throw new Error("CJS module not found: jquery");
 * $ = Module.cjs;
 * ```
 * 
 * @param {import("esbuild").PluginBuild} build 
 * @param {string} scope
 * @param {string[]} linkModules
 * @param {BundleMeta} bundleMeta
 */
function registerLinkHooks(build, scope, linkModules, bundleMeta) {
    const filterLinkModules = joinRegExp(linkModules, "^", "$");

    // Resolve all configured modules that should be linked via the global scope.
    build.onResolve(
        { filter: filterLinkModules },
        args => {
            if (args.kind === "import-statement" || args.kind === "dynamic-import") {
                return { path: args.path, namespace: NamespaceLinkImport };
            } else if (args.kind === "require-call" || args.kind === "require-resolve") {
                return { path: args.path, namespace: NamespaceLinkRequire };
            }
            return undefined;
        },
    );

    // Load an imported module from the global scope.: windows[Scope][moduleName].esm
    build.onLoad(
        { filter: /.*/, namespace: NamespaceLinkImport },
        args => {
            const contents = [
                `const Module = window.${scope}["${args.path}"];`,
                `if (!Module || !Module.esm) throw new Error("ESM module not found: ${args.path}");`,
                `module.exports = Module.esm.default;`,
            ].join("\n");
            return { contents };
        }
    );

    // Load a required module from the global scope: windows[Scope][moduleName].cjs
    build.onLoad(
        { filter: /.*/, namespace: NamespaceLinkRequire },
        args => {
            const contents = [
                `const Module = window.${scope}["${args.path}"];`,
                `if (!Module || !Module.cjs) throw new Error("CJS module not found: ${args.path}");`,
                `module.exports = Module.cjs;`,
            ].join("\n");
            return { contents };
        }
    );
}

/**
 * @param {GlobalCodeSplitModule[]} modules 
 * @param {BundleMeta} bundleMeta
 * @param {ModuleSplitMode} mode
 * @returns {Set<string>}
 */
function matchPackages(modules, bundleMeta, mode) {
    const matches = modules
        .filter(m => m.mode === mode).map(m => m.pattern)
        .flatMap(p => bundleMeta.allImports.filter(i => p.test(i)))
    return new Set(matches);
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
    /** @type {import("esbuild").Metafile | undefined}*/
    let metaFile = undefined;
    return {
        newPlugin: options => {
            const scope = options.scope;
            const modules = options.modules;
            return {
                name: PluginName,
                setup: build => {
                    const finalMetaFile = metaFile;
                    if (finalMetaFile === undefined) {
                        throw new Error("The meta file must be set before the plugin is used.");
                    }

                    const bundleMeta = createBundleMeta(build.initialOptions, finalMetaFile);

                    const exposeModules = matchPackages(modules, bundleMeta, "expose");
                    const linkModules = matchPackages(modules, bundleMeta, "link");
                    // Order of precedence: expose, link
                    exposeModules.forEach(m => linkModules.delete(m));

                    if (linkModules.size > 0) {
                        registerLinkHooks(build, scope, [...linkModules], bundleMeta);
                    }
                    if (exposeModules.size > 0) {
                        registerExposeHooks(build, scope, [...exposeModules], bundleMeta);
                    }
                },
            }
        },

        setMetaFile: (newMetaFile) => {
            metaFile = newMetaFile;
        },
    };
};

