const { promises: fs } = require("fs");

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

module.exports = {
    withTemporaryFileOnDisk,
};