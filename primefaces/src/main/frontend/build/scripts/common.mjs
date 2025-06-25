import * as path from "node:path";
import * as fs from "node:fs/promises";
import { fileURLToPath } from "node:url";

const dirName = path.dirname(fileURLToPath(import.meta.url));

/**
 * Constants with various environment info, such as paths and environment
 * variables.
 */
export const Env = {
    get RootDir() { return path.normalize(path.resolve(dirName, "..", ".."))},

    get MavenRootDir() { return path.resolve(this.RootDir, "..", "..", "..") },
    get PackagesDir() { return path.resolve(this.RootDir, "packages") },
    get DistDir() { return path.resolve(this.RootDir, "dist") },
    get DocsDir() { return path.resolve(this.RootDir, "docs") },
    
    get PackageJsonPath() { return path.resolve(this.RootDir, "package.json") },
    get PnPDataPath() { return path.resolve(this.RootDir, ".pnp.data.json") },
    
    get TargetResourceDir() { return path.resolve(this.RootDir, "..", "..", "..", "target", "generated-resources", "META-INF", "resources") },
    get TargetPrimeFacesResourceDir() { return path.join(this.TargetResourceDir, "primefaces") },
    
    get TarBall() { return path.resolve(this.RootDir, "package.tgz") },
    
    get IsProduction() { return process.env.NODE_ENV !== "development" },
    get IsVerbose() { return process.argv.includes("--verbose") },
    get IsAnalyze() { return process.argv.includes("--analyze") },
};

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
        Env.PackagesDir,
        { recursive: true, withFileTypes: true }
    );

    // Find sub package to build in the packages folder
    // A sub package is a folder with tsconfig.json
    const folders = entries
        .filter(e => e.isFile() && e.name === "tsconfig.json")
        .map(e => path.normalize(e.parentPath));

    // For each sub package, create an object with data on that package,
    // such as the paths of various files (index.ts etc.).
    const frontendProjects = await Promise.all(folders.map(async root => {
        const name = path.relative(Env.PackagesDir, root);
        const dist = path.resolve(root, "dist");
        const tsConfig = path.resolve(root, "tsconfig.json");
        const indexJs = path.resolve(root, "index.ts");
        const indexCss = path.resolve(root, "index.css");
        const buildSettingsPath = path.resolve(root, "build-settings.json");
        const buildSettings = await existsAndIsFile(buildSettingsPath) ? buildSettingsPath : undefined;
        
        const indexScript = await existsAndIsFile(indexJs) ? indexJs : undefined;
        const indexStyle = await existsAndIsFile(indexCss) ? indexCss : undefined;
    
        if (indexScript === undefined && indexStyle === undefined) {
            throw new Error(`Frontend project ${name} must have at least an index.ts file or an index.css file!`);
        }
    
        if (!await existsAndIsFile(tsConfig)) {
            throw new Error(`Frontend package folder must have a tsconfig.json file: ${tsConfig}`);
        }
    
        return { buildSettings, dist, indexScript, indexStyle, name, root, tsConfig };
    }));

    frontendProjects.sort((a, b) => a.name < b.name ? -1 : a.name > b.name ? 1 : 0);

    return frontendProjects;
}

/**
 * Checks if the given path is a file and exists.
 * @param {string} fileOrFolder 
 * @returns {Promise<boolean>}
 */
async function existsAndIsFile(fileOrFolder) {
    try {
        const stats = await fs.stat(fileOrFolder)
        return stats?.isFile() ?? false;
    } catch {
        return false;
    }
}

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