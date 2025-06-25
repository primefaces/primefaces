// Script that checks whether multiple versions of the same package are installed in the project.
// Exits with a non-zero exit code if it finds such packages.

import * as fs from "node:fs/promises";

import { Env } from "./common.mjs";

main().catch(e => {
    console.error(e);
    process.exit(1);
});

/**
 * Runs several verification checks on the frontend project. Currently, the
 * following checks are performed:
 * 
 * -Check for duplicate dependencies, see {@link checkForDuplicateDependencies}
 */
async function main() {
    await checkForDuplicateDependencies();
    // more checks will be added later
}

/**
 * Check for packages that have duplicate versions installed. For example, when
 * package X needs package Z at version 1.2 and package Y needs package Z at
 * version 2.3.
 * 
 * This would result in Z getting included in the bundle multiple times,
 * increasing the bundle size. While this may sometimes be unavoidable, we want
 * to avoid this scenario if possible.
 * 
 * Currently, there are no duplicates and this check does not allow for
 * exceptions. In the future, we may need to add exception of delete this check
 * completely.
 */
async function checkForDuplicateDependencies() {
    const pnpData = await readPnpData(Env.PnPDataPath);
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

/**
 * @param {string} pnpDataPath
 * @return {Promise<import("../pnp-manifest.js").PnpData>}
 */
async function readPnpData(pnpDataPath) {
    const pnpDataContent = await fs.readFile(pnpDataPath, "utf8");
    return JSON.parse(pnpDataContent);
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
 * Parses a virtual protocol, see also
 * https://yarnpkg.com/advanced/pnp-spec#virtual-folders and
 * https://yarnpkg.com/advanced/lexicon#virtual-package
 * 
 * For example, input:
 * 
 * ```
 * virtual:347bb18d2a38d7aca875cad4ab2bac8197f8ccbf8f8b0338773d1179bfa25f19774c30a08f9432c3caa70a4600c4c641203a7a4fbe0256d2cb61c9bb08f82a5f#patch:jquery-cropper@npm%3A1.0.0#~/.yarn/patches/jquery-cropper-npm-1.0.0-148aa161fe.patch::version=1.0.0&hash=be759a
 * ```
 * 
 * Output:
 * 
 * ```js
 * {
 *   hash: "347bb18d2a38d7aca875cad4ab2bac8197f8ccbf8f8b0338773d1179bfa25f19774c30a08f9432c3caa70a4600c4c641203a7a4fbe0256d2cb61c9bb08f82a5f"
 *   real: "patch:jquery-cropper@npm%3A1.0.0#~/.yarn/patches/jquery-cropper-npm-1.0.0-148aa161fe.patch::version=1.0.0&hash=be759a"
 * }
 * ```
 * @param {string} ref The virtual reference to resolve.
 * @returns {VirtualReference} The parsed reference
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
 * @typedef {{
* hash: string;
* real: string;
* }} VirtualReference
*/
undefined;
