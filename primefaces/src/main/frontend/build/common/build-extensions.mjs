/**
 * Allows a sub folder to affect the build settings when building the
 * frontend project (via ESBuild).
 * @typedef {{
 * importExpressions?: Record<string, string>
 * }} BuildExtension
 */
undefined;

/**
 * An empty build extension that does nothing.
 * @type {BuildExtension}
 */
const emptyBuildExtension = {};

/**
 * Loads the build extension for the given frontend project (if it has any).
 * @param {import("./frontend-project.mjs").FrontendProject} frontendProject
 * @returns {Promise<BuildExtension>}
 */
export async function loadBuildExtension(frontendProject) {
    const path = frontendProject.buildExtension;
    if (path === undefined) {
        return emptyBuildExtension;
    }
    const imported = await import(path);
    if (typeof imported.default !== "object" || imported.default === null) {
        throw new Error(`Build extension at ${path} must default export the build extension!`);
    }
    return imported.default;
}
