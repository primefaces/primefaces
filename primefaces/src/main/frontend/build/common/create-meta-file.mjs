/**
 * Merges all given ESBuild meta files into a single meta file.
 * @param {import("esbuild").Metafile[]} metaFiles 
 * @return {import("esbuild").Metafile}
 */
export function mergeMetaFiles(metaFiles) {
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
