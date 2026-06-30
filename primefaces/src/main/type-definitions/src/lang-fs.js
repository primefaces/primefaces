const { promises: fs } = require("fs");
const path = require("path");

const { LineBreak, ReadDirOpts, ReadFileOpts, WriteFileOpts } = require("./constants");
const { strJoin, normalizeLineBreaksUnix, asyncFlatMap } = require("./lang");

/**
 * Copies the source file to the target file path, invokes the callback, then
 * removes the target file
 * @template T
 * @param {TypeDeclarationBundleFiles} bundle 
 * @param {TypeDeclarationBundleFiles} target 
 * @param {(targetFile: TypeDeclarationBundleFiles) => T | Promise<T>} callback 
 * @return {Promise<T>}
 */
async function withTemporaryFileOnDisk(bundle, target, callback) {
    console.info("Copying", bundle.ambient, "to", target.ambient);
    console.info("Copying", bundle.module, "to", target.module);
    await Promise.all([
        fs.copyFile(bundle.ambient, target.ambient),
        fs.copyFile(bundle.module, target.module)
    ]);
    try {
        return await callback(target);
    }
    finally {
        await Promise.all([
            fs.unlink(target.ambient),
            fs.unlink(target.module)
        ]);
    }
}

/**
 * @param {string} file
 * @param {string} sep
 * @return {string}
 */
function normalize(file, sep = path.sep) {
    const normalized = path.normalize(file);
    return normalized.replaceAll(path.sep, sep);
}

/**
 * Reads th text of a non-binary text file encoded in UTF-8. Also normalizes
 * line breaks to UNIX style.
 * @param {string} path Path to a file to read.
 * @return {Promise<string>} The contents of the file.
 */
async function readFileUtf8(path) {
    const data = await fs.readFile(path, ReadFileOpts);
    const normalized = normalizeLineBreaksUnix(data);
    return normalized;
}

/**
 * Writes the contents to the given file, using UTF-8 encoding.
 * @param {string} path Path to a file to which the contents are written.
 * @param {{data: string | string[]}} content Content to write to the file. If an array, the array items are joined with UNIX
 * style line breaks.
 */
async function writeFileUtf8(path, content) {
    const data = Array.isArray(content.data) ? strJoin(content.data, LineBreak) : content.data;
    await fs.writeFile(path, data, WriteFileOpts);
}

/**
 * Creates the given directory. Creates parent directories if they do not exist yet.
 * @param {string} path Path of a directory to create.
 */
async function mkDirRecursive(path) {
    await fs.mkdir(path, {
        recursive: true,
    });
}

/**
 * Reads all files and directories in the given path.
 * @param {string} path A directory to read.
 * @return {Promise<import("fs").Dirent[]>} All files and directories in the given directory.
 */
async function readDir(path) {
    const entries = await fs.readdir(path, ReadDirOpts);
    return entries;
}

/**
 * Reads all files and directories in the given path, recursively
 * @param {string} path A directory to read.
 * @return {Promise<{dir:string,entry:import("fs").Dirent}[]>} All files and directories in the given directory.
 */
async function readDirRecursive(path) {
    const entries = await fs.readdir(path, ReadDirOpts);
    const mapped = await asyncFlatMap(entries, async e => {
        if (e.isDirectory()) {
            return await readDirRecursive(resolvePath(path, e.name));
        }
        else {
            return {dir: path, entry: e};
        }
    });
    return mapped;
}

/**
 * @param {string} from 
 * @param {string} to
 * @returns {string} 
 */
function makeRelative(from, to) {
    return path.relative(from, to);
}

/**
 * Checks whether the path is an existing file or directory.
 * @param {string} path Path to check.
 * @return {Promise<{exists: true, type: "file" | "dir"} | {exists: false}>} Whether the path is an existing file or
 * directory.
 */
async function isExistingFileOrDir(path) {
    try {
        const stat = await fs.stat(path);
        /** @type {"file" | "dir"} */
        let type;
        if (stat.isFile()) {
            type = "file";
        }
        else if (stat.isDirectory()) {
            type = "dir";
        }
        else {
            throw new Error(`File system entry at '${path}' exists, but is neither a file nor a directory.`);
        }
        return {
            exists: true,
            type: type,
        }
    }
    catch {
        return { exists: false };
    }
}

/**
 * Deletes a file, if it exists. If it does not exist, do nothing. Throw an error if it is an existing directory.
 * @param {string} path Path to a file to delete.
 */
async function deleteFile(path) {
    try {
        const stat = await fs.stat(path);
        if (stat.isFile()) {
            await fs.unlink(path);
        }
        else {
            throw new Error(`Cannot delete, given path is directory: ${path}`);
        }
    }
    catch {
        // ignore, file does not exist
    }
}

/**
 * Checks if two paths point two the same file or directory. Handles UNIX and Windows path separators. Does not check
 * if the file or directory actually exists.
 * @param {string} pathLhs First path to check
 * @param {string} pathRhs Second path to check 
 * @param {string} root Optional root path for resolving relative paths.
 */
function arePathsEqual(pathLhs, pathRhs, root = "") {
    const lhs = root.length > 0 ? path.resolve(path.join(root, pathLhs)) : path.resolve(pathLhs);
    const rhs = root.length > 0 ? path.resolve(path.join(root, pathRhs)) : path.resolve(pathRhs);
    return lhs === rhs;
}

/**
 * Resolves the given sub paths against the root path. If the sub path is already absolute, the root path is not used.
 * @param {string} rootPath 
 * @param {string} subPath 
 * @param  {string[]} morePaths 
 * @return {string}
 */
function resolvePath(rootPath, subPath, ...morePaths) {
    return path.resolve(...[rootPath, subPath, ...morePaths]);
}

/**
 * Makes the given path absolute, relative to the current directory.
 * @param {string} somePath
 * @return {string}
 */
function toAbsolutePath(somePath) {
    return path.resolve(somePath);
}

module.exports = {
    arePathsEqual,
    deleteFile,
    isExistingFileOrDir,
    mkDirRecursive,
    normalize,
    readDir,
    readDirRecursive,
    readFileUtf8,
    makeRelative,
    resolvePath,
    toAbsolutePath,
    withTemporaryFileOnDisk,
    writeFileUtf8,
};