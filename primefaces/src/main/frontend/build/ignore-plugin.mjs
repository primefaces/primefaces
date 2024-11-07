// @ts-check

const PluginName = "ignore-plugin";

import { joinRegExp } from "./helper.mjs";

/**
 * @typedef {{
 *   modules: string[];
 * }} IgnorePluginOptions
 */
undefined;

const NamespaceImport = "ignore-plugin/import";
const NamespaceRequire = "ignore-plugin/require";
const NamespaceOther = "ignore-plugin/other";

/**
 * Simple plugin for EsBuild that ignores the given modules.
 * @param { IgnorePluginOptions } options 
 * @returns {import("esbuild").Plugin}
 */
export function ignorePlugin(options) {
    return {
        name: PluginName,
        setup: build => {
            const filter = joinRegExp(options.modules, "^", "$");
            build.onResolve({ filter }, args => {
                if (args.kind === "import-statement" || args.kind === "dynamic-import") {
                    return { path: args.path, namespace: NamespaceImport };
                } else if (args.kind === "require-call" || args.kind === "require-resolve") {
                    return { path: args.path, namespace: NamespaceRequire };
                } else {
                    return { path: args.path, namespace: NamespaceOther };
                }
            });
            build.onLoad({ filter: /.*/, namespace: NamespaceImport }, args => {
                return { contents: "export {};", loader: "js" };
            });
            build.onLoad({ filter: /.*/, namespace: NamespaceRequire }, args => {
                return { contents: "module.exports = {};", loader: "js" };
            });
            build.onLoad({ filter: /.*/, namespace: NamespaceOther }, args => {
                return { contents: "" };
            });
        },
    };
}