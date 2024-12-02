/** @import { FrontendProject} from "../common/frontend-project.mjs" */

import path from "node:path";
import fs from "node:fs/promises";

import * as esbuild from "esbuild";

import { facesResourceLoaderPlugin } from "../esbuild-plugin/faces-resource-loader-plugin.mjs";
import { bannedDependenciesPlugin } from "../esbuild-plugin/banned-dependencies-plugin.mjs";

import { mergeMetaFiles } from "../common/create-meta-file.mjs";

import { ensureDirectoryExists } from "../lang/file.mjs";
import { IsProduction, IsVerbose, MavenRootDir, PackagesDir, RootDir, TargetPrimeFacesResourceDir, TargetResourceDir } from "../common/environment.mjs";
import { findFrontendProjects } from "../common/frontend-project.mjs";
import { loadBuildSettings } from "../common/build-settings.mjs";
import { loadFromExpressionPlugin } from "../esbuild-plugin/load-from-expression-plugin.mjs";
import { logError } from "../lang/error.mjs";

const isAnalyze = process.argv.includes("--analyze");

/**
 * @typedef {esbuild.BuildOptions & Required<Pick<esbuild.BuildOptions, "plugins">>} BaseBuildOptions
 */
undefined;

/**
 * List of dependencies that are banned from being imported in the source code.
 * @type {import("../esbuild-plugin/banned-dependencies-plugin.mjs").BannedDependency[]}
 */
const BannedDependencies = [
    {
        pattern: /^vis-(timeline|data|util)$/,
        reason: "Do not use vis packages directly, use their ESM variant, e.g. 'vis-data/esnext/esm/vis-data.js'. Only these match the (peer) dependencies declared in their respective package.json.",
    }
];

/**
 * Base options for all ESBuild tasks.
 * @returns {BaseBuildOptions}
 */
function createBaseOptions() {
    return {
        absWorkingDir: RootDir,
        bundle: true,
        charset: "utf8",
        target: "es2016",
        format: "iife",
        platform: "browser",
        legalComments: "external",
        metafile: true,
        minify: IsProduction,
        sourcemap: IsProduction ? false : "inline",
        write: true,
        plugins: [
            bannedDependenciesPlugin({ bannedDependencies: BannedDependencies }),
            facesResourceLoaderPlugin({
                extensions: ["png", "jpg", "jpeg", "gif", "svg", "woff", "woff2", "ttf", "eot"],
                inputDir: PackagesDir,
                outputDir: TargetPrimeFacesResourceDir,
                resourceBase: TargetResourceDir,
                useLibrary: true,
            }),
        ],
    };
};

/**
 * Creates the individual ESBuild tasks for the locale files in the
 * `src/locales` directory. Each locale file will be compressed and
 * written to a separate file in the output directory.
 * @returns {Promise<import("esbuild").BuildOptions[]>} The ESBuild tasks.
 */
async function createLocaleBuildTasks() {
    const localesDir = path.join(PackagesDir, "locales");
    const localeFiles = await fs.readdir(localesDir);
    return localeFiles.map(file => {
        const fromPath = path.join(localesDir, file);
        const toPath = path.join(TargetPrimeFacesResourceDir, "locales", file);
        if (IsVerbose) {
            const relFrom = path.relative(RootDir, fromPath);
            const relTo = path.relative(MavenRootDir, toPath);
            console.log(`Creating build task\n from <${relFrom}> to <${relTo}>`);
        }
        return {
            ...createBaseOptions(),
            entryPoints: [fromPath],
            outfile: toPath,
        };
    });
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
 * @returns {Promise<esbuild.BuildOptions[]>} The ESBuild tasks.
 */
async function createFrontendBuildTask(project) {
    const buildSettings = await loadBuildSettings(project);

    const fromRelative = path.relative(PackagesDir, project.root);
    const targetPath = path.resolve(TargetPrimeFacesResourceDir, fromRelative);

    /** @type {esbuild.BuildOptions[]} */
    const buildTasks = [];

    if (project.indexScript !== undefined) {
        /** @type {BaseBuildOptions} */
        const buildOptions = {
            ...createBaseOptions(),
            entryPoints: [project.indexScript],
            outfile: `${targetPath}.js`,
        };
        if (buildSettings.loadFromExpression !== undefined) {
            buildOptions.plugins.push(loadFromExpressionPlugin({
                expressions: buildSettings.loadFromExpression,
            }));
        }
        buildTasks.push(buildOptions);
    }

    if (project.indexStyle !== undefined) {
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
 * Creates the individual ESBuild tasks for the project folder in the
 * `packages` directory. The contents of each folder are be bundled,
 * compressed and written to a separate file in the output directory.
 * A package folder is a folder with a `tsconfig.json` file.
 * @returns {Promise<import("esbuild").BuildOptions[]>} The ESBuild tasks.
 */
async function createFrontendBuildTasks() {
    const projects = await findFrontendProjects();
    const tasks = await Promise.all(projects.filter(project => project.name !== "types").map(createFrontendBuildTask));
    return tasks.flat();
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
 * @param {esbuild.Metafile} metaFile Meta file with the build results
 * to analyze for duplicate modules.
 */
async function failOnDuplicateModulesInOutputs(metaFile) {
    /** @type {Map<string, string>} */
    const entryPointByInput = new Map();
    for (const {entryPoint, inputs} of Object.values(metaFile.outputs)) {
        if (entryPoint === undefined) {
            continue;
        }
        for (const input of Object.keys(inputs)) {
            // This is our ESBuild plugin that loads resources from the global scope.
            if (input.startsWith("load-from-expression/bare:") || input.startsWith("load-from-expression/modulePath:")) {
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
}

/**
 * Write the combined meta file from each individual build result to the dist dir.
 * This is useful for analyzing the bundle contents.
 * 
 * You can upload and visualize the meta file at https://esbuild.github.io/analyze/
 * 
 * @param {esbuild.Metafile} metaFile Meta file to write to the dist dir.
 */
async function writeCombinedMetaFile(metaFile) {
    const finalMetaFilePath = path.join(RootDir, "dist", "meta.json");
    await fs.mkdir(path.dirname(finalMetaFilePath), { recursive: true });
    await fs.writeFile(finalMetaFilePath, JSON.stringify(metaFile, null, 2));
    console.log("Meta file written to ", finalMetaFilePath);
}

async function main() {
    console.log(`Building PrimeFaces resources with mode ${IsProduction ? "production" : "development"}...`);

    ensureDirectoryExists(TargetPrimeFacesResourceDir);

    // Create all bundles that need to be built.
    const t1 = Date.now();
    const allBuildTasks = [
        ...await createFrontendBuildTasks(),
        ...await createLocaleBuildTasks(),
    ];

    // Start all promises and wait for them to finish.
    // This will build all tasks in parallel, speeding up the build process.
    const t2 = Date.now();
    const allResults = await Promise.allSettled(allBuildTasks.map(task => esbuild.build(task)));

    const t3 = Date.now();
    const exceptions = allResults.filter(r => r.status === "rejected");
    if (exceptions.length > 0) {
        throw new AggregateError(exceptions.map(r => r.reason));
    }

    const successResults = allResults.filter(r => r.status === "fulfilled").map(r => r.value);
    const finalMetaFile = mergeMetaFiles(successResults.map(r => r.metafile).filter(m => m != null));

    if (isAnalyze) {
        await writeCombinedMetaFile(finalMetaFile);
    }

    failOnDuplicateModulesInOutputs(finalMetaFile)

    console.log(`Created all bundles in ${t2 - t1}ms`);
    console.log(`Built all bundles in ${t3 - t2}ms`);
}

main().catch(e => {
    logError(e);
    process.exit(1);
});

