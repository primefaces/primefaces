import * as path from "node:path";
import pnpApi from "pnpapi";

/**
 * @typedef {{
 * expressions: {
 *   importSpecifier?: Record<string, string>;
 *   modulePath?: Record<string, string>;
 * };
 * }} LoadFromExpressionPluginOptions
 */
undefined;

const NamespaceImportSpecifier = "load-from-expression/bare";
const NamespaceModulePath = "load-from-expression/module-path";

/**
 * Creates a regexp pattern that matches the given literal text.
 * @param {string} text A piece of literal text. 
 * @returns {string} The resulting RegExp pattern.
 */
function escapeRegExp(text) {
    return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

/**
 * Resolves a request to a path using the PnP API, relative against an importer.
 * @param {string} request The requested path.
 * @param {string} importer The path of the module that imports the requested path.
 * @returns {string} The resolved path.
 */
function resolveRequest(request, importer) {
    try {
        const resolved = pnpApi.resolveRequest(request, importer);
        if (resolved !== null) {
            return resolved;
        }
    } catch {}

    // Try *.ts extension if *.js extension is requested.
    if (request.endsWith(".js")) {
        const resolved = pnpApi.resolveRequest(request.substring(0, request.length - 3) + ".ts", importer);
        if (resolved !== null) {
            return resolved;
        }
    }

    throw new Error(`Could not resolve request: <${request}> against importer: <${importer}>`);
}

/**
 * @param {string} resolved
 */
function resolveToPackagePath(resolved) {
    const locator = pnpApi.findPackageLocator(resolved ?? "");
    if (!locator || !locator.name) {
        throw new Error(`Could not find package locator for ${resolved}`);
    }
    const packageInfo = pnpApi.getPackageInformation(locator);
    const relative = path.relative(packageInfo.packageLocation, resolved);
    return relative !== "" ? `${locator.name}/${relative}` : locator.name;
}

/**
 * An ESBuild plugin that allows to load modules from an expression. E.g.
 * you can load `import $ from "jquery"` from `window.JQuery` instead.
 * 
 * Usage:
 * 
 * ```js
 * import { loadFromExpressionPlugin } from "path/to/load-from-expression-plugin.mjs";
 * esbuild.build({
 *     entryPoints: ["src/index.js"],
 *     ...,
 *     plugins: [
 *         loadFromExpressionPlugin({
 *             expressions: {
 *                 // You can target either an import specifier such as import("jquery")
 *                 importSpecifier: {
 *                     "jquery": "window.jQuery",
 *                 },
 * 
 *                 // Or a resolved path within a module. E.g. when import("../moment")
 *                 // is used by the file "locales/de.js" from the module "moment", this
 *                 // import resolves to the file "moment.js" from the module "moment"
 *                 // To target this resolved file, use:
 *                 modulePath: {
 *                     "moment/moment.js": "window.moment",
 *                 },
 *          },
 *     ],
 * });
 * ```
 * @param {LoadFromExpressionPluginOptions} options
 * @returns {import("esbuild").Plugin}
 */
export function loadFromExpressionPlugin(options) {
    return {
        name: "load-from-expression",
        setup(build) {
            const importSpecifier = options.expressions.importSpecifier ?? {};
            if (Object.keys(importSpecifier).length > 0) {
                const pattern = `^(${Object.keys(importSpecifier).map(escapeRegExp).join("|")})$`;
                build.onResolve({ filter: new RegExp(pattern) }, (args) => {
                    return { namespace: NamespaceImportSpecifier, path: args.path };
                });
                build.onLoad({ filter: /.*/, namespace: NamespaceImportSpecifier }, (args) => {
                    const expression = importSpecifier[args.path];
                    return {
                        contents: `module.exports = ${expression};`,
                        loader: "js",
                    };
                });
            }

            const modulePath = options.expressions.modulePath ?? {};
            if (Object.keys(modulePath).length > 0) {
                build.onResolve({ filter: /.*/ }, (args) => {
                    const resolved = resolveRequest(args.path, args.importer);
                    const packagePath = resolveToPackagePath(resolved);
                    if (packagePath in modulePath) {
                        return { path: packagePath, namespace: NamespaceModulePath };
                    }
                    return undefined;
                });
                build.onLoad({ filter: /.*/, namespace: NamespaceModulePath }, (args) => {
                    const expression = modulePath[args.path];
                    if (expression === undefined) {
                        throw new Error(`No expression found for <${args.path}>`);
                    }
                    return {
                        contents: `module.exports = ${expression};`,
                        loader: "js",
                    };
                });
            }
        },
    };
}
