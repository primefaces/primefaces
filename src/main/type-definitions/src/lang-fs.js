const { promises: fs } = require("fs");

/**
 * Copies the source file to the target file path, invokes the callback, then
 * removes the target file
 * @template T
 * @param {string} sourceFile 
 * @param {string} targetFile 
 * @param {(targetFile: string) => T | Promise<T>} callback 
 * @return {Promise<T>}
 */
async function withTemporaryFileOnDisk(sourceFile, targetFile, callback) {
    await fs.copyFile(sourceFile, targetFile);
    try {
        return await callback(targetFile);
    }
    finally {
        await fs.unlink(targetFile);
    }
}

module.exports = {
    withTemporaryFileOnDisk,
};