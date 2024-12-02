// Script that checks whether multiple versions of the same package are installed in the project.
// Exits with a non-zero exit code if it finds such packages.

import fs from "node:fs/promises";

import { PnPDataPath } from "../common/environment.mjs";

/**
 * @typedef {{
 * hash: string;
 * real: string;
 * }} VirtualReference
 */
undefined;

/**
 * @param {string} ref
 * @returns {VirtualReference}
 */
function parseVirtualReference(ref) {
    if (!ref.startsWith("virtual:")) {
        throw new Error(`Invalid virtual reference: ${ref}`);
    }
    const hashIndex = ref.indexOf("#");
    if (hashIndex < 0) {
        throw new Error(`Invalid virtual reference: ${ref}`);
    }
    const hash = ref.substring("virtual:".length, hashIndex);
    const real = ref.substring(hashIndex + 1);
    return { hash, real };
}

/**
 * @param {string[]} refs
 * @returns {Set<string>}
 */
function resolveReferences(refs) {
    /** @type {Set<string>} */
    const resolvedRefs = new Set();
    for (const ref of refs) {
        if (ref.startsWith("virtual:")) {
            resolvedRefs.add(parseVirtualReference(ref).real);
        } else {
            resolvedRefs.add(ref);
        }
    }
    return resolvedRefs;
}

/**
 * @param {string} pnpDataPath
 * @return {Promise<import("../common/pnp-manifest.js").PnpData>}
 */
async function readPnpData(pnpDataPath) {
    const pnpDataContent = await fs.readFile(pnpDataPath, "utf8");
    return JSON.parse(pnpDataContent);
}

async function main() {
    const pnpData = await readPnpData(PnPDataPath);
    const duplicatePackages = pnpData.packageRegistryData
        .filter(entry => entry[0] !== null)
        .filter(([, references]) => resolveReferences(references.map(([reference]) => reference)).size > 1);
    const errors = duplicatePackages
        .map(([packageIdent, references]) => {
            const versions = [...resolveReferences(references.map(([reference]) => reference))];
            return new Error(`Package ${packageIdent} has multiple versions installed: ${versions.join(", ")}`);
        });
    if (errors.length > 0) {
        throw new AggregateError(errors, "Found multiple installed versions of the same package. This will result in the package getting included multiple times in the bundle. You can either try loosening the version requirements in the package.json (e.g. '~1.0.0' or '^1.0.0' instead of '1.0.0'); or enforce a certain version by adding/editing the 'resolutions' field of the package.json.");
    }
}

main().catch(e => {
    console.error(e);
    process.exit(1);
});
