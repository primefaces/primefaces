
/**
 * @typedef {{
 * expressions: Record<string, string>;
 * }} LoadFromExpressionPluginOptions
 */
undefined;

/**
 * Escapes a string to be used in a regular expression.
 * @param {string} string 
 * @returns {string}
 */
function escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

const Namespace = "load-from-expression";

/**
 * An ESBuild plugin that allows to load modules from an expression. E.g.
 * you can load `import $ from "jquery"` from `window.JQuery` instead.
 * @param {LoadFromExpressionPluginOptions} options
 * @returns {import("esbuild").Plugin}
 */
export function newLoadFromExpressionPlugin(options) {
    return {
        name: "load-from-expression",
        setup(build) {
            const pattern = `^${Object.keys(options.expressions).map(escapeRegExp).join("|")}$`;
            build.onResolve({ filter: new RegExp(pattern) }, (args) => {
                return { namespace: Namespace, path: args.path };
            });
            build.onLoad({ filter: /.*/, namespace: Namespace }, (args) => {
                const expression = options.expressions[args.path];
                return {
                    contents: `module.exports = ${expression};`,
                    loader: "js",
                };
            });
        },
    };
}