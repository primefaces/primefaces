// @ts-check

/**
 * Resolves the entry point settings from EsBuild to a list of input files.
 * @param {import("esbuild").BuildOptions["entryPoints"]} entryPoints The entry points setting.
 * @returns {string[]} The list of input files.
 */
export function resolveEntryPointInput(entryPoints) {
    if (entryPoints === undefined) {
        return [];
    }
    if (Array.isArray(entryPoints)) {
        return entryPoints.map(e => typeof e === "string" ? e : e.in);
    }
    return Object.keys(entryPoints);
}

/**
 * Filters the given entry points by the given predicate that received the entry point input.
 * @param {import("esbuild").BuildOptions["entryPoints"]} entryPoints The entry points to filter.
 * @param {(input: string) => boolean} test The predicate to test the entry point input.
 * @returns {import("esbuild").BuildOptions["entryPoints"]} The filtered entry points.
 */
export function filterEntryPoints(entryPoints, test) {
    if (entryPoints === undefined) {
        return undefined;
    }
    if (Array.isArray(entryPoints)) {
        return /** @type {import("esbuild").BuildOptions["entryPoints"]}*/(
            entryPoints.filter(e => typeof e === "string" ? test(e) : test(e.in))
        );
    }
    return Object.fromEntries(Object.entries(entryPoints).filter(([inFile]) => test(inFile)));
}

