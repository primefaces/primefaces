// @ts-check

import path from "node:path";
import { fileURLToPath } from "node:url";
import fs from "node:fs/promises";
import fsSync from "node:fs";

import * as esbuild from "esbuild";

import { globalCodeSplitPluginFactory } from "./esbuild-plugin/global-code-split-plugin.mjs";
import { facesResourceLoaderPlugin } from "./esbuild-plugin/faces-resource-loader-plugin.mjs";
import { bannedDependenciesPlugin } from "./esbuild-plugin/banned-dependencies-plugin.mjs";

import { createMetaFile, mergeMetaFiles } from "./esbuild/create-meta-file.mjs";

import { createObfuscator } from "./util/obfuscate.mjs";
import { escapeRegExp } from "./util/reg-exp.mjs";
import { comparingBy, comparingInStages } from "./util/comparator.mjs";

import PackageJson from "../package.json" assert { type: "json" };

/**
 * Additional settings for {@link buildTask}.
 * 
 * - expose: External libraries to include in the bundle and expose to the global
 * scope. By default, all external libraries (from NPM) are excluded and loaded
 * from the global scope (`window.PrimeFacesLibs`). One bundle should include the
 * library, so that it is actually available in the global scope.
 * 
 * @typedef {{
 * expose?: (keyof typeof LinkedLibraries)[];
 * }} BuildTaskSettings
 */
undefined;

/**
 * The type of a bundled file, either JavaScript or CSS.
 * @typedef {"js" | "css"} FileType
 */
undefined;

const isProduction = process.env.NODE_ENV !== "development";
const isAnalyze = process.argv.includes("--analyze");
const isVerbose = process.argv.includes("--verbose");

const baseDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const srcDir = path.join(baseDir, "src");
const resourcesDir = path.resolve(baseDir, "..", "..", "..", "target", "generated-resources", "META-INF", "resources");
const outputDir = path.join(resourcesDir, "primefaces");

const globalCodeSplitPlugin = globalCodeSplitPluginFactory();

const ModuleNameEncoder = createObfuscator(`${PackageJson.name}-${PackageJson.version}`);

/**
 * PrimeFaces consists of several source files. External libraries are
 * included as separate JavaScript files. Linking with other source
 * files is done via a global variable (window.PrimeFacesLibs). The
 * following defines the NPM packages which need to be linked.
 * 
 * When a package name ends with a slash, it is treated as a prefix.
 * 
 * E.g. ""autonumeric/" will match "autonumeric" as well as
 * "autonumeric/dist/autoNumeric.min.js" etc.
 */
const LinkedLibraries = {
    "calendar/calendar": ["jquery-ui/ui/widgets/datepicker.js", "jquery-ui-timepicker-addon/"],
    "chart/chart": ["chart.js/"],
    "colorpicker/colorpicker": ["@melloware/coloris/"],
    "core": ["js-cookie/"],
    "diagram/diagram": ["jsplumb/"],
    "filedownload/filedownload": ["downloadjs/"],
    "fileupload/fileupload": ["blueimp-file-upload/"],
    "imagecropper/imagecropper": ["cropperjs/", "jquery-cropper/"],
    "inputmask/inputmask": ["inputmask/"],
    "inputnumber/inputnumber": ["autonumeric/"],
    "inputtextarea/inputtextarea": ["autosize/"],
    "jquery/jquery": ["jquery/", "jquery.browser/"],
    "jquery/jquery-plugins": ["jquery-mousewheel/", "jquery-ui/", "rangyinputs/"],
    "moment/moment": ["moment/", "moment-jdateformatparser/"],
    "moment/moment-timezone-with-data": ["moment-timezone/"],
    "photocam/photocam": ["webcamjs/"],
    "raphael/raphael": ["raphael/"],
    "schedule/schedule": ["@fullcalendar/"],
    "scrollpanel/scrollpanel": ["jscrollpane/"],
    "texteditor/texteditor": ["quill/"],
    "timeline/timeline": ["moment/locale/", "vis-timeline/", "vis-data/", "vis-util/"],
    "touch/touchswipe": ["jquery-touchswipe/"],
};

/**
 * Settings for the bundles. A bundle is a folder with a `bundle.ts`
 * or `bundle.css` file, within the `src` directory. These folders
 * are read automatically by {@link findBundleFolders}. You can use
 * this object to customize the build process for specific bundles.
 * The key is the name of the bundle folder, relative to the `src`
 * directory. The value is an object with additional settings for
 * the bundle build task.
 * @type {Record<string, BuildTaskSettings>}
 */
const BundleSettings = {
    "calendar/calendar": { expose: ["calendar/calendar"] },
    "chart/chart": { expose: ["chart/chart"] },
    "colorpicker/colorpicker": { expose: ["colorpicker/colorpicker"] },
    "core": { expose: ["core"] },
    "diagram/diagram": { expose: ["diagram/diagram"] },
    "filedownload/filedownload": { expose: ["filedownload/filedownload"] },
    "fileupload/fileupload": { expose: ["fileupload/fileupload"] },
    "imagecropper/imagecropper": { expose: ["imagecropper/imagecropper"] },
    "inputmask/inputmask": { expose: ["inputmask/inputmask"] },
    "inputnumber/inputnumber": { expose: ["inputnumber/inputnumber"] },
    "inputtextarea/inputtextarea": { expose: ["inputtextarea/inputtextarea"] },
    "jquery/jquery": { expose: ["jquery/jquery"] },
    "jquery/jquery-plugins": { expose: ["jquery/jquery-plugins"] },
    "moment/moment": { expose: ["moment/moment"] },
    "moment/moment-timezone-with-data": { expose: ["moment/moment-timezone-with-data"] },
    "raphael/raphael": { expose: ["raphael/raphael"] },
    "photocam/photocam": { expose: ["photocam/photocam"] },
    "schedule/schedule": { expose: ["schedule/schedule"] },
    "scrollpanel/scrollpanel": { expose: ["scrollpanel/scrollpanel"] },
    "texteditor/texteditor": { expose: ["texteditor/texteditor"] },
    "timeline/timeline": { expose: ["timeline/timeline"] },
    "touch/touchswipe": { expose: ["touch/touchswipe"] },
};

/**
 * List of dependencies that are banned from being imported in the source code.
 * @type {import("./esbuild-plugin/banned-dependencies-plugin.mjs").BannedDependency[]}
 */
const BannedDependencies = [
    {
        pattern: /^vis-(timeline|data|util)$/,
        reason: "Do not use vis packages directly, use their ESM variant, e.g. 'vis-data/esnext/esm/vis-data.js'. Only these match the (peer) dependencies declared in their respective package.json.",
    }
];

/**
 * Base options for all ESBuild tasks.
 * @returns {import("esbuild").BuildOptions}
 */
function createBaseOptions() {
    return {
        absWorkingDir: baseDir,
        bundle: true,
        charset: "utf8",
        target: "es2016",
        format: "iife",
        platform: "browser",
        legalComments: "external",
        minify: isProduction,
        sourcemap: isProduction ? false : "inline",
        plugins: [
            bannedDependenciesPlugin({ bannedDependencies: BannedDependencies }),
            facesResourceLoaderPlugin({
                extensions: ["png", "jpg", "jpeg", "gif", "svg", "woff", "woff2", "ttf", "eot"],
                inputDir: srcDir,
                outputDir: outputDir,
                resourceBase: resourcesDir,
                useLibrary: true,
            }),
        ],
    };
};

/**
 * Creates a new ESBuild task that compresses the given JavaScript or CSS
 * file and writes the output to the given path. The source path is relative
 * to the `bundles` directory, the target path is relative to the main output
 * directory.
 * @param {string} from The source file.
 * @param {string} to The target file.
 * @param {BuildTaskSettings} [settings] Additional settings for build task.    
 * @returns {import("esbuild").BuildOptions} The ESBuild task.
 */
function buildTask(from, to, settings = {}) {
    const fromPath = path.resolve(srcDir, from);
    const toPath = path.resolve(outputDir, to);

    /** @type {import("./esbuild-plugin/global-code-split-plugin.mjs").GlobalCodeSplitModule[]} */
    let modules;

    if (to.endsWith(".css")) {
        modules = [];
    } else {
        const modulesToExpose = (settings.expose ?? []).flatMap(expose => LinkedLibraries[expose] ?? []);
        /** @type {import("./esbuild-plugin/global-code-split-plugin.mjs").GlobalCodeSplitModule[]} */
        modules = Object.values(LinkedLibraries).flatMap(names => {
            return names.map(name => {
                // When the name ends with a slash, it is a prefix that matches all sub paths.
                const isPrefix = name.endsWith("/");
                const baseName = isPrefix ? name.slice(0, name.length - 1) : name;
                const pattern = new RegExp(`^${escapeRegExp(baseName)}${isPrefix ? "(/.+)?" : ""}$`);
    
                const mode = modulesToExpose.includes(name) ? "expose" : "link";
                return { mode, pattern };
            });
        });
    }

    if (isVerbose) {
        const sortedModules = [...modules].sort(comparingInStages(
            comparingBy(m => ["expose", "link"].indexOf(m.mode)),
            comparingBy(m => m.pattern.toString())
        ));
        const moduleLines = sortedModules.map(m => `  ${m.mode.padEnd(6, " ")} = ${m.pattern}`).join("\n  ");
        const relFrom = path.relative(baseDir, fromPath);
        const relTo = path.relative(baseDir, toPath);
        console.log(`Creating build task\n from <${relFrom}> to <${relTo}>\n  ${moduleLines}\n`);
    }

    /** @type {import("esbuild").BuildOptions} */
    const buildTask = {
        ...createBaseOptions(),
        entryPoints: [fromPath],
        outfile: toPath,
        metafile: isAnalyze,
    };
    buildTask.plugins = [...buildTask.plugins ?? []];
    buildTask.plugins.push(globalCodeSplitPlugin.newPlugin({
        // PrimeFacesLibs is internal, discourage people from ever using it.
        encoder: isProduction ? ModuleNameEncoder : undefined,
        scope: "PrimeFacesLibs",
        modules,
        verbose: isVerbose,
    }));
    return buildTask;
}

/**
 * Creates build tasks for the given bundle folder. A bundle folder is the
 * relative path to a directory in the `src` directory. The bundle folder
 * must contain at least a `bundle.ts` or `bundle.css` file.
 * 
 * The output files are written to the `outputDir` directory, with the same
 * name as the bundle directory.
 * 
 * For example, if the bundle name is `components`, the source files are
 * `src/components/bundle.ts` and `src/components/bundle.css`. The output files
 * are `<outputDir>/bundle.js` and `<outputDir>/bundle.css`.
 * 
 * @param {string} bundleFolder Relative path of the bundle folder,
 * relative to the `src` dir.
 * @param {BuildTaskSettings} [settings] Additional settings for build task.    
 * @returns {import("esbuild").BuildOptions[]} The ESBuild task.
 */
function bundleBuildTasks(bundleFolder, settings = {}) {
    const jsFile = path.join(srcDir, `${bundleFolder}/bundle.ts`);
    const cssFile = path.join(srcDir, `${bundleFolder}/bundle.css`);
    /** @type {import("esbuild").BuildOptions[]} */
    const buildTasks = [];
    if (fsSync.existsSync(jsFile)) {
        const toPath = path.join(outputDir, `${bundleFolder}.js`);
        const expose = [...settings.expose ?? []];
        expose.push(/** @type {keyof typeof LinkedLibraries} */(bundleFolder));
        buildTasks.push(buildTask(jsFile, toPath, { ...settings, expose }));
    }
    if (fsSync.existsSync(cssFile)) {
        const toPath = path.join(outputDir, `${bundleFolder}.css`);
        buildTasks.push(buildTask(cssFile, toPath, { ...settings, expose: [] }));
    }
    if (buildTasks.length === 0) {
        throw new Error(`Neither a JS nor a CSS source file found for ${bundleFolder}`);
    }
    return buildTasks;
}

/**
 * Finds all folders (recursively) in the given directory that represent
 * a bundle, i.e. contains at least one of `bundle.ts` or `bundle.css` file.
 * These folders can be passed to {@link bundleBuildTasks} to create the
 * ESBuild tasks.
 * 
 * @param {string} folder The directory to search for bundle folders.
 * @returns {Promise<string[]>} The bundle folders.
 */
async function findBundleFolders(folder) {
    const contents = await fs.readdir(folder, { withFileTypes: true, recursive: true });
    return contents
        .filter(f => f.isDirectory())
        .map(f => path.join(f.path, f.name))
        .filter(f => {
            const jsFile = path.join(f, "bundle.ts");
            const cssFile = path.join(f, "bundle.css");
            return fsSync.existsSync(jsFile) || fsSync.existsSync(cssFile);
        })
        .map(f => path.relative(folder, f));
}

/**
 * Creates the individual ESBuild tasks for the locale files in the
 * `src/locales` directory. Each locale file will be compressed and
 * written to a separate file in the output directory.
 * @returns {Promise<import("esbuild").BuildOptions[]>} The ESBuild tasks.
 */
async function createLocaleBuildTasks() {
    const localeDir = path.join(srcDir, "locales");
    const localeFiles = await fs.readdir(localeDir);
    return localeFiles.map(file => {
        const fromPath = path.join(localeDir, file);
        const toPath = path.join(outputDir, "locales", file);
        return {
            ...createBaseOptions(),
            entryPoints: [fromPath],
            outfile: toPath,
        };
    });
}

/**
 * Creates the individual ESBuild tasks for the bundle folders in the
 * `src` directory. Each bundle folder will be compressed and written
 * to a separate file in the output directory. A bundle folder is a 
 * folder with at least a `bundle.ts` or `bundle.css` file.
 * @returns {Promise<import("esbuild").BuildOptions[]>} The ESBuild tasks.
 */
async function createBundleBuildTasks() {
    const bundleFolders = await findBundleFolders(srcDir);
    const bundleSettingKeys = new Set(Object.keys(BundleSettings));
    bundleFolders.forEach(bundleFolder => bundleSettingKeys.delete(bundleFolder));
    if (bundleSettingKeys.size > 0) {
        throw new Error(`Bundle settings found for non-existing bundles, remove those: ${[...bundleSettingKeys].join(", ")}`);
    }
    for (const bundleFolder of bundleFolders) {
        LinkedLibraries[bundleFolder] ??= [];
        LinkedLibraries[bundleFolder].push(`${PackageJson.name}/src/${bundleFolder}/`);
    }
    return bundleFolders.flatMap(bundleFolder => bundleBuildTasks(bundleFolder, BundleSettings[bundleFolder]));
}

async function main() {
    console.log(`Building PrimeFaces resources with mode ${isProduction ? "production" : "development"}...`);

    const t1 = Date.now();
    const allBuildTasks = [...await createBundleBuildTasks(), ...await createLocaleBuildTasks()];

    const t2 = Date.now();
    const metaFile = await createMetaFile(allBuildTasks);
    globalCodeSplitPlugin.setMetaFile(metaFile, baseDir);

    // Start all promises and wait for them to finish.
    // This will build all tasks in parallel, speeding up the build process.
    const t3 = Date.now();
    const allResults = await Promise.allSettled(allBuildTasks.map(task => esbuild.build(task)));
    const t4 = Date.now();

    const exceptions = allResults.filter(r => r.status === "rejected");
    if (exceptions.length > 0) {
        throw new AggregateError(exceptions.map(r => r.reason));
    }

    if (isAnalyze) {
        const successResults = allResults.filter(r => r.status === "fulfilled");
        const finalMetaFile = mergeMetaFiles(successResults.map(r => r.value.metafile).filter(m => m != null));
        const finalMetaFilePath = path.join(baseDir, "dist", "meta.json");
        await fs.mkdir(path.dirname(finalMetaFilePath), { recursive: true });
        await fs.writeFile(finalMetaFilePath, JSON.stringify(finalMetaFile, null, 2));
        console.log("Meta file written to ", finalMetaFilePath);
    }

    console.log(`Created all bundles in ${t2 - t1}ms`);
    console.log(`Analyzed bundles in ${t3 - t2}ms`);
    console.log(`Built all bundles in ${t4 - t3}ms`);
}

main().catch(e => {
    console.error(e);
    process.exit(1);
});
