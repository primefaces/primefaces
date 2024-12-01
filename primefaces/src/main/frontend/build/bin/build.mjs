/** @import { FrontendProject} from "../common/frontend-project.mjs" */

import path from "node:path";
import fs from "node:fs/promises";

import * as esbuild from "esbuild";

import { facesResourceLoaderPlugin } from "../esbuild-plugin/faces-resource-loader-plugin.mjs";
import { bannedDependenciesPlugin } from "../esbuild-plugin/banned-dependencies-plugin.mjs";

import { mergeMetaFiles } from "../common/create-meta-file.mjs";

import { ensureDirectoryExists } from "../lang/file.mjs";
import { IsProduction, IsVerbose, MavenRootDir, PackagesDir, RootDir, TargetPrimeFacesResourceDir, TargetResourceDir } from "../common/environment.mjs";
import { findFrontendProjects } from "../common/frontend-project.mjs";
import { loadBuildExtension } from "../common/build-extensions.mjs";
import { newLoadFromExpressionPlugin } from "../esbuild-plugin/load-from-expression-plugin.mjs";
import { logError } from "../lang/error.mjs";

const isAnalyze = process.argv.includes("--analyze");

/**
 * @typedef {esbuild.BuildOptions & Required<Pick<esbuild.BuildOptions, "plugins">>} BaseBuildOptions
 */
undefined;

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
 * are read automatically by findBundleFolders. You can use
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
 * @type {import("../esbuild-plugin/banned-dependencies-plugin.mjs").BannedDependency[]}
 */
const BannedDependencies = [
    {
        pattern: /^vis-(timeline|data|util)$/,
        reason: "Do not use vis packages directly, use their ESM variant, e.g. 'vis-data/esnext/esm/vis-data.js'. Only these match the (peer) dependencies declared in their respective package.json.",
    }
];

/**
 * Base options for all ESBuild tasks.
 * @returns {BaseBuildOptions}
 */
function createBaseOptions() {
    return {
        absWorkingDir: RootDir,
        bundle: true,
        charset: "utf8",
        target: "es2016",
        format: "iife",
        platform: "browser",
        legalComments: "external",
        metafile: isAnalyze,
        minify: IsProduction,
        sourcemap: IsProduction ? false : "inline",
        write: true,
        plugins: [
            bannedDependenciesPlugin({ bannedDependencies: BannedDependencies }),
            facesResourceLoaderPlugin({
                extensions: ["png", "jpg", "jpeg", "gif", "svg", "woff", "woff2", "ttf", "eot"],
                inputDir: PackagesDir,
                outputDir: TargetPrimeFacesResourceDir,
                resourceBase: TargetResourceDir,
                useLibrary: true,
            }),
        ],
    };
};

/**
 * Creates the individual ESBuild tasks for the locale files in the
 * `src/locales` directory. Each locale file will be compressed and
 * written to a separate file in the output directory.
 * @returns {Promise<import("esbuild").BuildOptions[]>} The ESBuild tasks.
 */
async function createLocaleBuildTasks() {
    const localesDir = path.join(PackagesDir, "locales");
    const localeFiles = await fs.readdir(localesDir);
    return localeFiles.map(file => {
        const fromPath = path.join(localesDir, file);
        const toPath = path.join(TargetPrimeFacesResourceDir, "locales", file);
        if (IsVerbose) {
            const relFrom = path.relative(RootDir, fromPath);
            const relTo = path.relative(MavenRootDir, toPath);
            console.log(`Creating build task\n from <${relFrom}> to <${relTo}>`);
        }
        return {
            ...createBaseOptions(),
            entryPoints: [fromPath],
            outfile: toPath,
        };
    });
}

/**
 * Creates ESBuild tasks for the given {@link findFrontendProjects frontend project}.
 * 
 * The output files are written to the {@link TargetPrimeFacesResourceDir} directory,
 * preserving the directory structure of the frontend project.
 * 
 * For example, if the frontend project is `packages/diagram/diagram/index.ts`,
 * the output files are `<TargetPrimeFacesResourceDir>/diagram/diagram.js`
 * and `<TargetPrimeFacesResourceDir>/diagram/diagram.css`.
 * 
 * @param {FrontendProject} project A frontend project.
 * @returns {Promise<esbuild.BuildOptions[]>} The ESBuild tasks.
 */
async function createFrontendBuildTask(project) {
    const buildExtension = await loadBuildExtension(project);

    const fromRelative = path.relative(PackagesDir, project.root);
    const targetPath = path.resolve(TargetPrimeFacesResourceDir, fromRelative);

    /** @type {esbuild.BuildOptions[]} */
    const buildTasks = [];

    if (project.indexScript !== undefined) {
        /** @type {BaseBuildOptions} */
        const buildOptions = {
            ...createBaseOptions(),
            entryPoints: [project.indexScript],
            outfile: `${targetPath}/index.js`,
        };
        if (buildExtension.importExpressions !== undefined) {
            buildOptions.plugins.push(newLoadFromExpressionPlugin({
                expressions: buildExtension.importExpressions,
            }));
        }
        buildTasks.push(buildOptions);
    }

    if (project.indexStyle !== undefined) {
        /** @type {BaseBuildOptions} */
        const buildOptions = {
            ...createBaseOptions(),
            entryPoints: [project.indexStyle],
            outfile: `${targetPath}/index.css`,
        };
        buildTasks.push(buildOptions);
    }

    return buildTasks;
}

/**
 * Creates the individual ESBuild tasks for the project folder in the
 * `packages` directory. The contents of each folder are be bundled,
 * compressed and written to a separate file in the output directory.
 * A package folder is a folder with a `tsconfig.json` file.
 * @returns {Promise<import("esbuild").BuildOptions[]>} The ESBuild tasks.
 */
async function createFrontendBuildTasks() {
    const projects = await findFrontendProjects();
    const tasks = await Promise.all(projects.filter(project => project.name !== "types").map(createFrontendBuildTask));
    return tasks.flat();
}

/**
 * Combines the meta files from each individual build result into a single
 * meta file, and writes it to the dist dir This is useful for analyzing
 * the bundle contents.
 * 
 * You can upload and visualize the meta file at https://esbuild.github.io/analyze/
 * 
 * @param {esbuild.BuildResult[]} successResults 
 */
async function writeCombinedMetaFile(successResults) {
    const finalMetaFile = mergeMetaFiles(successResults.map(r => r.metafile).filter(m => m != null));
    const finalMetaFilePath = path.join(RootDir, "dist", "meta.json");
    await fs.mkdir(path.dirname(finalMetaFilePath), { recursive: true });
    await fs.writeFile(finalMetaFilePath, JSON.stringify(finalMetaFile, null, 2));
    console.log("Meta file written to ", finalMetaFilePath);
}

async function main() {
    console.log(`Building PrimeFaces resources with mode ${IsProduction ? "production" : "development"}...`);

    ensureDirectoryExists(TargetPrimeFacesResourceDir);

    // Create all bundles that need to be built.
    const t1 = Date.now();
    const allBuildTasks = [
        ...await createFrontendBuildTasks(),
        ...await createLocaleBuildTasks(),
    ];

    // Start all promises and wait for them to finish.
    // This will build all tasks in parallel, speeding up the build process.
    const t2 = Date.now();
    const allResults = await Promise.allSettled(allBuildTasks.map(task => esbuild.build(task)));

    const t3 = Date.now();
    const exceptions = allResults.filter(r => r.status === "rejected");
    if (exceptions.length > 0) {
        throw new AggregateError(exceptions.map(r => r.reason));
    }

    if (isAnalyze) {
        const successResults = allResults.filter(r => r.status === "fulfilled").map(r => r.value);
        await writeCombinedMetaFile(successResults);
    }

    console.log(`Created all bundles in ${t2 - t1}ms`);
    console.log(`Built all bundles in ${t3 - t2}ms`);
}

main().catch(e => {
    logError(e);
    process.exit(1);
});

