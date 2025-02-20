import fs from "node:fs/promises";

/**
 * Allows a sub folder to affect the build settings when building the
 * frontend project (via ESBuild).
 * @typedef {{
 * loadFromExpression?: {
 *  importSpecifier?: Record<string, string>;
 *  modulePath?: Record<string, string>;
 * };
 * }} BuildSettings
 */
undefined;

/**
 * An empty build extension that does nothing.
 * @type {BuildSettings}
 */
const emptyBuildSettings = {};

/**
 * Loads the build settings for the given frontend project (if it has any).
 * Each directory in packages can have its own build settings in a JSON
 * file that affect the build process.
 * @param {import("./frontend-project.mjs").FrontendProject} frontendProject
 * @returns {Promise<BuildSettings>}
 */
export async function loadBuildSettings(frontendProject) {
    const path = frontendProject.buildSettings;
    if (path === undefined) {
        return emptyBuildSettings;
    }
    const buildSettingsJson = JSON.parse(await fs.readFile(path, { encoding: "utf8"} ));
    if (typeof buildSettingsJson !== "object" || buildSettingsJson === null) {
        throw new Error(`Build settings at ${path} must be a JSON object!`);
    }
    return buildSettingsJson;
}
