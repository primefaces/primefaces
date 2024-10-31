// @ts-check

/**
 * @typedef {Record<string, string>} CustomModuleSourcePluginOptions
 */
undefined;

const namespace = "custom-module-source-plugin";

/**
 * Plugin for esbuild that loads modules from other sources, such as from a global variable.
 * Based on https://github.com/yanm1ng/esbuild-plugin-external-global
 * @param {CustomModuleSourcePluginOptions} externals 
 * @returns {import("esbuild").Plugin}
 */
export function customModuleSourcePlugin(externals) {
    return {
        name: namespace,
        setup(build) {
            build.onResolve(
                { filter: new RegExp(`^(${Object.keys(externals).join('|')})$`) },
                args => ({
                    path: args.path,
                    namespace: namespace,
                })
            );
            build.onLoad(
                { filter: /.*/, namespace },
                args => {
                    const contents = `module.exports = ${externals[args.path]};`;
                    return { contents };
                }
            );
        },
    }
};