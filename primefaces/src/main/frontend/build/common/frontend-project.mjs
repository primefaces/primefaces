import path from "node:path";
import fs from "node:fs/promises";

import { assertExistsAndIsFile, ensureDirectoryExists, existsAndIsFile } from "../lang/file.mjs";
import { PackagesDir } from "./environment.mjs";

/**
 * Represents a frontend project with JavaScript and CSS files.
 * Each frontend project gets bundles into a single JavaScript
 * and CSS file.
 * @typedef {{
 * readonly buildSettings?: string;
 * readonly dist: string;
 * readonly indexScript?: string;
 * readonly indexStyle?: string;
 * readonly name: string;
 * readonly root: string;
 * readonly tsConfig: string;
 * }} FrontendProject
 */
undefined;

/**
 * Returns the given file path if it exists and is not a directory,
 * or undefined otherwise.
 * @param {string} filePath Path to the file. 
 * @returns {Promise<string | undefined>} The file path if it exists or undefined.
 */
async function undefinedIfNotFile(filePath) {
    return await existsAndIsFile(filePath) ? filePath : undefined;
}

/**
 * Given the base folder of a frontend project, finds all relevant paths,
 * performs some basic checks, and returns a {@link FrontendProject}.
 * @param {string} root Base folder of the frontend project.
 * @returns {Promise<FrontendProject>} The frontend project.
 */
async function createFrontendProject(root) {
    const name = path.relative(PackagesDir, root);
    const dist = path.resolve(root, "dist");
    const tsConfig = path.resolve(root, "tsconfig.json");
    const indexJs = path.resolve(root, "index.ts");
    const indexCss = path.resolve(root, "index.css");
    const buildSettings = await undefinedIfNotFile(path.resolve(root, "build-settings.json"));

    const indexScript = await undefinedIfNotFile(indexJs);
    const indexStyle = await undefinedIfNotFile(indexCss);

    if (indexScript === undefined && indexStyle === undefined) {
        throw new Error(`Frontend project ${name} must have at least an index.ts file or an index.css file!`);
    }

    await Promise.all([
        ensureDirectoryExists(root),
        assertExistsAndIsFile(tsConfig),
    ]);

    return { buildSettings, dist, indexScript, indexStyle, name, root, tsConfig };
}

/**
 * Finds all package paths in the workspace. That is, all
 * directories with an index.ts or index.js file. After
 * bundling, each package results in a JavaScript and/or
 * CSS file. 
 * 
 * Projects are returned in alphabetical order, see
 * {@link FrontendProject.name}.
 * @returns {Promise<FrontendProject[]>}
 */
export async function findFrontendProjects() {
    // Search for all packages with a tsconfig.json file
    // This way, we don't have to create an extra file
    // and list out all packages manually.
    const entries = await fs.readdir(
        PackagesDir,
        { recursive: true, withFileTypes: true }
    );

    const folders = entries
        .filter(e => e.isFile() && e.name === "tsconfig.json")
        .map(e => path.normalize(e.parentPath));

    const frontendProjects = await Promise.all(folders.map(createFrontendProject));
    frontendProjects.sort((a, b) => a.name < b.name ? -1 : a.name > b.name ? 1 : 0);
    return frontendProjects;
}
