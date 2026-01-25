/** @import { BuildOptions, BuildResult, Metafile } from "esbuild" */
/** @import { BannedDependency } from "@xenorange/esbuild-plugin-banned-dependencies" */
/** @import { LoadFromExpressionPluginExpressions } from "@xenorange/esbuild-plugin-load-from-expression" */
/** @import { FrontendProject} from "./common.mjs" */

import * as fs from "node:fs/promises";
import * as path from "node:path";

import { bannedDependenciesPlugin } from "@xenorange/esbuild-plugin-banned-dependencies";
import { facesResourceLoaderPlugin } from "@xenorange/esbuild-plugin-faces-resource-loader";
import { loadFromExpressionPlugin } from "@xenorange/esbuild-plugin-load-from-expression";
import * as esbuild from "esbuild";

import { Env, findFrontendProjects } from "./common.mjs";

/**
 * List of dependencies that are banned from being imported in the source code.
 * @type {BannedDependency[]}
 */
const BannedDependencies = [
    {
        pattern: /^vis-(timeline|data|util)$/,
        reason: "Do not use vis packages directly, use their ESM variant, e.g. 'vis-data/esnext/esm/vis-data.js'. Only these match the (peer) dependencies declared in their respective package.json.",
    }
];

main().catch(e => {
    console.error(e);
    process.exit(1);
});

async function main() {
    console.log(`Building PrimeFaces resources with mode ${Env.IsProduction ? "production" : "development"}...`);

    console.log(`Ensuring target folder <${Env.TargetPrimeFacesResourceDir}> exists`)
    await fs.mkdir(Env.TargetPrimeFacesResourceDir, { recursive: true })

    // Create all bundles that need to be built.
    const t1 = Date.now();
    const allBuildTasks = [
        // packages/core, packages/components etc.
        ...await createFrontendBuildTasks(),
        // packages/locales are separate because they don't have a tsconfig.json
        ...await createLocaleBuildTasks(),
    ];

    const t2 = Date.now();
    console.log(`Created all bundles in ${t2 - t1}ms`);

    // Start all promises and wait for them to finish.
    // This will build all tasks in parallel, speeding up the build process.
    const allResults = await runEsBuild(allBuildTasks);

    const t3 = Date.now();
    console.log(`Built all bundles in ${t3 - t2}ms`);

    const exceptions = allResults.filter(r => r.status === "rejected");
    if (exceptions.length > 0) {
        throw new AggregateError(exceptions.map(r => r.reason));
    }

    const successResults = allResults.filter(r => r.status === "fulfilled").map(r => r.value);
    const finalMetaFile = mergeMetaFiles(successResults.map(r => r.metafile).filter(m => m != null));

    // You can https://esbuild.github.io/analyze/ to analyze the final bundle
    // E.g. to see how much size each dependency takes
    if (Env.IsAnalyze) {
        await writeCombinedMetaFile(finalMetaFile);
    }

    // Each NPM dependency should be included only in one bundle file to avoid duplicates
    failOnDuplicateModulesInOutputs(finalMetaFile)
}

/**
 * Runs all ESBuild tasks in parallel and waits for them to finish.
 * 
 * This method temporarily removes the `type` field from the `package.json`.
 * This is needed to make ESBuild use Babel's interpretation for default exports
 * from CommonJS modules, i.e. so that it treats exports as ESM when marked
 * with the special `__esModule` flag. This will hopefully stop being necessary
 * in the future when all dependencies are ESM (or are true CommonJS). For now,
 * this seems to affect only `@fullcalendar`, and the new version 6 of
 * FullCalendar is ESM already (we are still using version 5).
 * 
 * See also 
 * - https://esbuild.github.io/content-types/#default-interop
 * - https://github.com/evanw/esbuild/issues/2480
 * - https://github.com/evanw/esbuild/issues/3852
 * @param {BuildOptions[]} buildTasks 
 * @returns {Promise<PromiseSettledResult<BuildResult>[]>}
 */
async function runEsBuild(buildTasks) {
    const original = await fs.readFile(Env.PackageJsonPath, "utf-8");
    try {
        // Prevent issues regarding default imports/exports when "type": "module"
        // is set in the package.json. Remove this entry during the build, then
        // add it back afterwards.
        // See https://esbuild.github.io/content-types/#default-interop
        /** @type {Partial<Record<string, unknown>>} */
        const packageJson = JSON.parse(original);
        delete packageJson.type;
        console.log(`Temporarily setting {\"type\": \"module\"} in <${Env.PackageJsonPath}>...`);
        await fs.writeFile(Env.PackageJsonPath, JSON.stringify(packageJson, null, 2), "utf-8");
        console.log("Running esbuild in parallel on all bundles...");
        const result = await Promise.allSettled(buildTasks.map(task => esbuild.build(task)));
        console.log("All bundles built successfully");
        return result;
    } finally {
        console.log(`Restoring <${Env.PackageJsonPath}> to its original state...`);
        await fs.writeFile(Env.PackageJsonPath, original, "utf-8");
    }
}

/**
 * Write the combined meta file from each individual build result to the dist dir.
 * This is useful for analyzing the bundle contents.
 * 
 * You can upload and visualize the meta file at https://esbuild.github.io/analyze/
 * 
 * @param {Metafile} metaFile Meta file to write to the dist dir.
 */
async function writeCombinedMetaFile(metaFile) {
    console.log("Combining build meta files from all bundles...");
    const finalMetaFilePath = path.join(Env.RootDir, "dist", "meta.json");
    await fs.mkdir(path.dirname(finalMetaFilePath), { recursive: true });
    await fs.writeFile(finalMetaFilePath, JSON.stringify(metaFile, null, 2));
    console.log("Meta file written to ", finalMetaFilePath);
}

/**
 * Analyze the meta file for duplicate modules in different outputs.
 * Fails if the same module is written to multiple outputs. Only
 * one output file should contain the module and expose it to the global
 * scope. The other output files should use the module from the global
 * scope.
 * 
 * For example, jquery is already provided by the `jquery/jquery` package.
 * All other packages should use `window.$` to access the jquery module.
 *
 * @param {Metafile} metaFile Meta file with the build results
 * to analyze for duplicate modules.
 */
async function failOnDuplicateModulesInOutputs(metaFile) {
    console.log("Checking for duplicate modules in built bundles...");
    /** @type {Map<string, string>} */
    const entryPointByInput = new Map();
    for (const { entryPoint, inputs } of Object.values(metaFile.outputs)) {
        if (entryPoint === undefined) {
            continue;
        }
        for (const input of Object.keys(inputs)) {
            // This is the esbuild plugin that loads resources from the global scope.
            if (input.startsWith("load-from-expression/bare:") || input.startsWith("load-from-expression/module-path:")) {
                continue;
            }

            const otherEntryPoint = entryPointByInput.get(input);
            if (otherEntryPoint !== undefined && entryPoint !== otherEntryPoint) {
                const message = [
                    `Both <${entryPoint}> and <${otherEntryPoint}> import and include module <${input}>.`,
                    `One package should import the module and expose it to the global scope,`,
                    `the other package should use the module from the global scope.`,
                ].join(" ");
                throw new Error(message);
            }

            entryPointByInput.set(input, entryPoint);
        }
    }
    console.log("No duplicates found");
}

/**
 * Creates the individual ESBuild tasks for the project folder in the
 * `packages` directory. The contents of each folder are be bundled,
 * compressed and written to a separate file in the output directory.
 * A package folder is a folder with a `tsconfig.json` file.
 * @returns {Promise<import("esbuild").BuildOptions[]>} The ESBuild tasks.
 */
async function createFrontendBuildTasks() {
    console.log(`Scanning <${Env.PackagesDir}> for frontend packages to build...`);
    const projects = await findFrontendProjects();
    const tasks = await Promise.all(projects.filter(project => project.name !== "types").map(createFrontendBuildTask));
    return tasks.flat();
}

/**
 * Creates ESBuild tasks for the given {@link findFrontendProjects frontend project}.
 * 
 * The output files are written to the {@link TargetPrimeFacesResourceDir} directory,
 * preserving the directory structure of the frontend project.
 * 
 * For example, if the frontend project is `packages/diagram/diagram/index.ts`,
 * the output files are `<TargetPrimeFacesResourceDir>/diagram/diagram.js`
 * and `<TargetPrimeFacesResourceDir>/diagram/diagram.css`.
 * 
 * @param {FrontendProject} project A frontend project.
 * @returns {Promise<BuildOptions[]>} The ESBuild tasks.
 */
async function createFrontendBuildTask(project) {
    const buildSettings = await loadBuildSettings(project);

    const fromRelative = path.relative(Env.PackagesDir, project.root);
    const targetPath = path.resolve(Env.TargetPrimeFacesResourceDir, fromRelative);

    /** @type {BuildOptions[]} */
    const buildTasks = [];

    // Create an esbuild task for the index.js file, if it exists
    if (project.indexScript !== undefined) {
        console.log(`Creating build task from <${path.relative(Env.MavenRootDir, project.indexScript)}> to <${path.relative(Env.MavenRootDir, targetPath)}.js>`);
        /** @type {BaseBuildOptions} */
        const buildOptions = {
            ...createBaseOptions(),
            entryPoints: [project.indexScript],
            outfile: `${targetPath}.js`,
        };
        // Replace imports with window globals, e.g. <import "jquery"> with <window.$>
        if (buildSettings.loadFromExpression !== undefined) {
            buildOptions.plugins.push(loadFromExpressionPlugin({
                expressions: buildSettings.loadFromExpression,
                conditions: ["import", "module"],
            }));
        }
        buildTasks.push(buildOptions);
    }

    // Create an esbuild task for the index.css file, if it exists
    if (project.indexStyle !== undefined) {
        console.log(`Creating build task from <${path.relative(Env.MavenRootDir, project.indexStyle)}> to <${path.relative(Env.MavenRootDir, targetPath)}.css>`);
        /** @type {BaseBuildOptions} */
        const buildOptions = {
            ...createBaseOptions(),
            entryPoints: [project.indexStyle],
            outfile: `${targetPath}.css`,
        };
        buildTasks.push(buildOptions);
    }

    return buildTasks;
}

/**
 * Creates the individual ESBuild tasks for the locale files in the
 * `src/locales` directory. Each locale file will be compressed and
 * written to a separate file in the output directory.
 * @returns {Promise<import("esbuild").BuildOptions[]>} The ESBuild tasks.
 */
async function createLocaleBuildTasks() {
    const localesDir = path.join(Env.PackagesDir, "locales");
    const localeFiles = await fs.readdir(localesDir);
    return localeFiles.map(file => {
        const fromPath = path.join(localesDir, file);
        const toPath = path.join(Env.TargetPrimeFacesResourceDir, "locales", file);
        console.log(`Creating build task from <${path.relative(Env.MavenRootDir, fromPath)}> to <${path.relative(Env.MavenRootDir, toPath)}>`);
        return {
            ...createBaseOptions(),
            entryPoints: [fromPath],
            outfile: toPath,
        };
    });
}

/**
 * Base options for all ESBuild tasks.
 * @returns {BaseBuildOptions}
 */
function createBaseOptions() {
    return {
        absWorkingDir: Env.RootDir,
        bundle: true,
        charset: "utf8",
        target: "es2016",
        format: "iife",
        platform: "browser",
        legalComments: "external",
        metafile: true,
        minify: Env.IsProduction,
        sourcemap: Env.IsProduction ? false : "inline",
        write: true,
        plugins: [
            bannedDependenciesPlugin({ bannedDependencies: BannedDependencies }),
            facesResourceLoaderPlugin({
                extensions: ["png", "jpg", "jpeg", "gif", "svg", "webp", "woff", "woff2", "ttf", "eot"],
                inputDir: Env.PackagesDir,
                outputDir: Env.TargetPrimeFacesResourceDir,
                resourceBase: Env.TargetResourceDir,
                useLibrary: true,
            }),
        ],
    };
};

/**
 * Merges all given esbuild meta files into a single meta file. An esbuild
 * meta file contains data regarding the individual files that were combined into
 * a single bundle.
 * 
 * We are creating multiple bundles such as core.js, components.js, or
 * poll/poll.js. There's a separate esbuild task for each bundle, and each
 * creates a meta file. This function combines all meta files into a single
 * file.
 * @param {import("esbuild").Metafile[]} metaFiles List of esbuild meta files
 * to combine.
 * @return {import("esbuild").Metafile} The combined meta file.
 */
function mergeMetaFiles(metaFiles) {
    /** @type {import("esbuild").Metafile} */
    const merged = { inputs: {}, outputs: {} };

    for (const metaFile of metaFiles) {
        for (const [key, input] of Object.entries(metaFile.inputs)) {
            // Same input produces same metadata, so we can just overwrite it if it exists already (or skip it, does not matter)
            merged.inputs[key] = input;
        }
        for (const [key, output] of Object.entries(metaFile.outputs)) {
            if (key in merged.outputs) {
                throw new Error(`Duplicate output file: ${key}`);
            }
            merged.outputs[key] = output;
        }
    }

    return merged;
}

/**
 * Loads the build settings for the given frontend project (if it has any). Each
 * frontend project, such as packages/core, packages/chart/chart,
 * packages/gmap/gmap etc. can have an optional build-settings.json file with
 * custom settings that affect how that bundle should be built.
 * 
 * @param {import("./common.mjs").FrontendProject} frontendProject The frontend
 * project for which to load the build settings.
 * @returns {Promise<BuildSettings>} The build settings for the frontend 
 * project. An empty settings object if no build-settings.json exists.
 */
export async function loadBuildSettings(frontendProject) {
    const path = frontendProject.buildSettings;
    if (path === undefined) {
        return {};
    }
    const buildSettingsJson = JSON.parse(await fs.readFile(path, { encoding: "utf8" }));
    if (typeof buildSettingsJson !== "object" || buildSettingsJson === null) {
        throw new Error(`Build settings at ${path} must be a JSON object!`);
    }
    return buildSettingsJson;
}


/**
 * @typedef {BuildOptions & Required<Pick<BuildOptions, "plugins">>} BaseBuildOptions
 */
undefined;

/**
 * Type for the build-settings.json file each frontend project can contain, which
 * affects how the bundle is built via esbuild
 * (e.g. packages/knob/knob/build-settings.json).
 * @typedef {Object} BuildSettings
 * @property {LoadFromExpressionPluginExpressions} [loadFromExpression] Settings
 * for the esbuild-plugin-load-from-expression plugin that e.g. replacing
 * <import "jquery"> with `window.$`.
 */
undefined;
