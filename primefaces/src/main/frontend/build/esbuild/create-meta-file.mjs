// @ts-check

import path from "node:path";

import * as esbuild from "esbuild";
import { filterEntryPoints } from "./entry-points.mjs";

/** 
 * Loaders to skip everything that is not a JavaScript / TypeScript file.
 * @type {import("esbuild").BuildOptions["loader"]}
 */
const EmptyLoaders = {
    ".css": "empty",
    ".html": "empty",
    ".json": "empty",
    ".gif": "empty",
    ".png": "empty",
    ".jpeg": "empty",
    ".jpg": "empty",
    ".svg": "empty",
    ".ttf": "empty",
    ".otf": "empty",
    ".eot": "empty",
    ".woff": "empty",
    ".woff2": "empty",
};

/**
 * Merges all given meta files into a single meta file.
 * @param {import("esbuild").Metafile[]} metaFiles 
 * @return {import("esbuild").Metafile}
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
 * Analyzes all given bundles via ESBuild and creates a meta file with the
 * information about the bundle.
 * @param {import("esbuild").BuildOptions[]} buildTasks
 * @returns {Promise<import("esbuild").Metafile>} 
 */
export async function createMetaFile(buildTasks) {
    const jsExtensions = [".ts", ".js", ".cjs", ".mjs", ".cts", ".mts"];
    const metaBuildTasks = buildTasks.map(buildTask => {
        /** @type {import("esbuild").BuildOptions & {metafile: true}} */
        const metaBuildTask = {
            ...buildTask,
            entryPoints: filterEntryPoints(buildTask.entryPoints, input => jsExtensions.includes(path.extname(input))),
            metafile: true,
            bundle: true,
            minify: false,
            write: false,
            sourcemap: false,
            legalComments: "none",
            target: "esnext",
            logLevel: "silent",
            loader: EmptyLoaders,
            plugins: [],
        };
        return metaBuildTask;
    });

    const results = await Promise.allSettled(metaBuildTasks.map(task => esbuild.build(task)));
    const exceptions = results.filter(r => r.status === "rejected");
    if (exceptions.length > 0) {
        throw new AggregateError(exceptions.map(r => r.reason));
    }
    return mergeMetaFiles(results.filter(r => r.status === "fulfilled").map(r => r.value.metafile));
}
