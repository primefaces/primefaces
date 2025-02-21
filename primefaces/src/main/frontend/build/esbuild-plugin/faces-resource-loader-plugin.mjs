import * as fs from "node:fs/promises";
import * as path from "node:path";
import { pathToFileURL, fileURLToPath, URL } from "node:url";

/**
 * @typedef {{
 * extensions: string[];
 * inputDir: string;
 * outputDir: string;
 * resourceBase: string;
 * useLibrary: boolean;
 * }} FacesResourceLoaderPluginOptions
 */
undefined;

/**
 * @typedef {{
 * absInputDir: string;
 * absOutputDir: string;
 * absResourceBase: string;
 * cwd: string;
 * useLibrary: boolean;
 * }} PluginConfig
 */
undefined;

const namespace = "faces-resource-loader-plugin";

/**
 * Appends the suffix to the string if it is not already present.
 * @param {string} str String to which the suffix should be appended.
 * @param {string} suffix Suffix to append.
 * @returns {string} String with the suffix appended.
 */
function appendIfMissing(str, suffix) {
    return str.endsWith(suffix) ? str : str + suffix;
}

/**
 * Returns all parent directories of the given directory.
 * @param {string} dir Directory to process.
 * @returns {string[]} Parent directories, starting from the root.
 */
function parents(dir) {
    /** @type {string[]} */
    const result = [];
    let current = dir;
    while (current !== path.dirname(current)) {
        current = path.dirname(current);
        result.push(current);
    }
    return result.reverse();
}

/**
 * Pre-computes some data that is the same for every invocation of the build hooks. 
 * @param {import("esbuild").BuildOptions} buildOptions 
 * @param {FacesResourceLoaderPluginOptions} pluginOptions
 * @returns {PluginConfig}
 */
function createConfig(buildOptions, pluginOptions) {
    const cwd = buildOptions.absWorkingDir ?? process.cwd();
    const absInputDir = path.resolve(cwd, pluginOptions.inputDir);
    const absOutputDir = path.resolve(cwd, pluginOptions.outputDir);
    const absResourceBase = path.resolve(cwd, pluginOptions.resourceBase);
    return {
        cwd,
        absInputDir,
        absOutputDir,
        absResourceBase,
        useLibrary: pluginOptions.useLibrary,
    };
}

/**
 * Resolves the file to copy to the output directory; and
 * returns the path of the input and the target file.
 * @param {import("esbuild").OnResolveArgs} resolveArgs
 * @param {PluginConfig} config 
 * @returns {Promise<{sourceUrl: URL, sourceFile: string; targetFile: string}>}
 */
async function resolveImportFileAndTarget(resolveArgs, config) {
    // The resolveArgs.path is a URL and may contain query params or fragments. 
    const baseUrl = pathToFileURL(appendIfMissing(resolveArgs.resolveDir, "/"));
    const sourceUrl = new URL(resolveArgs.path, baseUrl);
    const sourceFile = fileURLToPath(sourceUrl);

    const relativeSourceFile = path.relative(config.absInputDir, sourceFile);
    const targetFile = path.join(config.absOutputDir, relativeSourceFile);
    return { sourceUrl, sourceFile, targetFile };
}

/**
 * Construct a Faces resource expression for the given file, e.g. `#{resource['library:file/path.txt']}`.
 * @param {string} file Absolute path of the file.
 * @param {URL} url The original URL of the file.
 * @param {PluginConfig} config Plugin configuration.
 */
function createFacesResourceExpression(file, url, config) {
    const fileParents = parents(file);
    if (!fileParents.includes(config.absResourceBase)) {
        throw new Error("File is not in the resource base.");
    }
    const relativePath = path.relative(config.absResourceBase, file);
    const parts = relativePath.split(/[\\/]/);
    const params = `${url.search}${url.hash}`;
    if (config.useLibrary && parts.length > 1) {
        const [library, ...pathParts] = parts;
        return `#{resource['${library}:${pathParts.join("/")}']}${params}`;
    } else {
        return `#{resource['${parts.join("/")}']}${params}`;
    }
}

/**
 * Plugin for esbuild that modifies the URL of imported resources in CSS files
 * to Faces resource expressions.
 * 
 * ESBuild plugin for Faces resources. Jakarta Faces uses a custom resource loading
 * mechanism via the Faces servlet. When a CSS file wishes to reference e.g. an image
 * or font, it must use a special EL expression to refer to the resource, e.g.
 * `#{resource['library:file/path.txt']}`.
 * 
 * This plugin adjust the URL of referenced resources accordingly. This allows authors
 * to use normal relative paths in CSS files, such as `url(../images/image.png)`, and
 * have them automatically adjusted to the Faces resource expression during build time. 
 * 
 * Usage:
 * 
 * ```js
 * import { facesResourceLoaderPlugin } from "path/to/faces-resource-loader-plugin.mjs";
 * esbuild.build({
 *     entryPoints: ["src/index.js"],
 *     ...,
 *     plugins: [
 *         facesResourceLoaderPlugin({
 *              * Resources to which the plugin should apply 
 *             extensions: ["png", "gif", "jpg", "jpeg", "svg", "woff", "woff2", "ttf", "eot"],
 * 
 *             // Directory for input and output files. Used to construct the relative path
 *             // when copying files to the output directory.
 *             inputDir: "src/main/frontend/src",
 *             outputDir: "target/generated-resources/META-INF/resources/library",
 * 
 *             // Base directory of the webapp resources, used to create the resource expression.
 *             resourceBase: "target/generated-resources/META-INF/resources",
 * 
 *             // Whether to use the library name in the resource expression.
 *             // true:  #{resource['library:file/path.txt']}
 *             // false: #{resource['library/file/path.txt']}
 *             useLibrary: true,
 *         }),
 *     ],
 * });
 * ```
 *
 * @param {FacesResourceLoaderPluginOptions} options 
 * @returns {import("esbuild").Plugin}
 */
export function facesResourceLoaderPlugin(options) {
    const filter = new RegExp(`\.(${options.extensions.join('|')})(#.*)?$`);
    return {
        name: namespace,
        setup: build => {
            const config = createConfig(build.initialOptions, options);
            /** @type {Map<string, string>} */
            const filesToCopy = new Map();
            build.onResolve(
                { filter },
                async args => {
                    if (args.namespace !== "file") {
                        return { errors: [{ text: "Faces resource loader plugin only supports resources from files" }] };
                    }
                    const importerExtension = path.extname(args.importer);
                    if (importerExtension !== ".css") {
                        return { warnings: [{ text: "Faces resource loader plugin only supports resources imported from CSS files" }] };
                    }
                    // Only store resources to copy, and do the copy at the end.
                    // Otherwise, we might copy resources multiple times and simultaneously,
                    // which is bad in general and may also fail in Windows when it locks the target file.
                    const { sourceUrl, sourceFile, targetFile } = await resolveImportFileAndTarget(args, config);
                    filesToCopy.set(sourceFile, targetFile);
                    const facesResourceExpression = createFacesResourceExpression(targetFile, sourceUrl, config);
                    return {
                        external: true,
                        namespace,
                        path: facesResourceExpression,
                    };
                },
            );
            build.onEnd(async () => {
                for (const [sourceFile, targetFile] of filesToCopy.entries()) {
                    await fs.mkdir(path.dirname(targetFile), { recursive: true });
                    await fs.copyFile(sourceFile, targetFile);
                }
            });
        },
    };
};
