import fs from "node:fs/promises";
import path from "node:path";

/**
 * Gets the stats of a file or directory, if it exists.
 * @param {string} fileOrFolder
 * @returns {Promise<import("node:fs").Stats | undefined>}
 */
async function fileStats(fileOrFolder) {
    try {
        const stats = await fs.stat(fileOrFolder);
        return stats;
    } catch {
        return undefined;
    }
}

/**
 * Checks if the given path exists (neither file nor directory).
 * @param {string} fileOrFolder 
 * @returns {Promise<boolean>}
 */
export async function exists(fileOrFolder) {
    return (await fileStats(fileOrFolder)) !== undefined;
}

/**
 * Checks if the given path is a file and exists.
 * @param {string} fileOrFolder 
 * @returns {Promise<boolean>}
 */
export async function existsAndIsFile(fileOrFolder) {
    return (await fileStats(fileOrFolder))?.isFile() ?? false;
}

/**
 * Checks if the given path is a directory and exists.
 * @param {string} fileOrFolder 
 * @returns {Promise<boolean>}
 */
export async function existsAndIsDirectory(fileOrFolder) {
    return (await fileStats(fileOrFolder))?.isDirectory() ?? false;
}

/**
 * Asserts that the given path is a file and exists.
 * @param {string} fileOrFolder Path to the file or directory.
 * @param {string} [reason] Optional reason for the assertion.
 */
export async function assertExistsAndIsFile(fileOrFolder, reason) {
    if (!await existsAndIsFile(fileOrFolder)) {
        throw new Error(`File does not exist: ${fileOrFolder}${reason ?` - ${reason}` : ""}`);
    }
}

/**
 * Asserts that the given path does not exist (neither file nor directory).
 * @param {string} fileOrFolder 
 * @param {string} [reason]
 */
export async function assertDoesNotExist(fileOrFolder, reason) {
    if (await exists(fileOrFolder)) {
        throw new Error(`File must not exist: ${fileOrFolder}${reason ? ` - ${reason}` : ""}`);
    }
}

/**
 * Check if the given directory exists and create it if it doesn't.
 * Creates all parent directories if they don't exist.
 * @param {string} folder Path to the directory.
 */
export async function ensureDirectoryExists(folder) {
    await fs.mkdir(folder, { recursive: true });
}

/**
 * Check if the given file exists and create an empty if it doesn't.
 * Creates all parent directories if they don't exist.
 * @param {string} file Path to the directory.
 */
export async function ensureFileExists(file) {
    await ensureDirectoryExists(path.dirname(file));
    if (!await existsAndIsFile(file)) {
        await fs.writeFile(file, "");
    }
}

/**
 * Deletes the file or directory if it exists. Does nothing
 * if it doesn't exist.
 * @param {string} fileOrFolder Path to the file or directory.
 * @param {boolean} [log] Whether to log the deletion.
 */
export async function deleteIfExists(fileOrFolder, log = false) {
    const stats = await fileStats(fileOrFolder);
    if (stats?.isDirectory()) {
        if (log) {
            console.log(`Deleting directory: ${fileOrFolder}`);
        }
        await fs.rm(fileOrFolder, { recursive: true });
    } else if (stats?.isFile()) {
        if (log) {
            console.log(`Deleting file: ${fileOrFolder}`);
        }
        await fs.unlink(fileOrFolder);
    }
}