// @ts-check

/**
 * @typedef {{
 *   pattern: RegExp;
 *   reason?: string;
 * }} BannedDependency
 */
undefined;

/**
 * @typedef {{
 * bannedDependencies: BannedDependency[];
 * }} BannedDependenciesPluginOptions
 */
undefined;

const PluginName = "banned-dependencies-plugin";

/**
 * Finds the first banned dependency rule that matches the given path,
 * and returns the reason for banning it.
 * @param {BannedDependency[]} bannedDependencies All banned dependencies.
 * @param {string} path The path to match against the banned dependencies.
 * @returns {string | undefined} The reason for banning the dependency, if any.
 */
function findReason(bannedDependencies, path) {
    return bannedDependencies.find(({ pattern }) => pattern.test(path))?.reason;
}

/**
 * Plugin for esbuild raises an error when a banned dependency is imported.
 * @param {BannedDependenciesPluginOptions} options 
 * @returns {import("esbuild").Plugin}
 */
export function bannedDependenciesPlugin(options) {
    const filter = new RegExp(`(${options.bannedDependencies.map(x => x.pattern.source).join(')|(')})`);
    return {
        name: PluginName,
        setup: build => {
            build.onResolve(
                { filter },
                args => {
                    const reason = findReason(options.bannedDependencies, args.path);
                    const message = reason !== undefined
                        ? `Dependency '${args.path}' is banned: ${reason}`
                        : `Dependency '${args.path}' is banned`;
                    return { errors: [{ text: message }] };
                },
            );
        },
    };
};

