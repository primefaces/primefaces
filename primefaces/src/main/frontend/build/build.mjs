// @ts-check

import path from "node:path";
import { fileURLToPath } from "node:url";
import fs from "node:fs/promises";

import * as esbuild from "esbuild";

import { globalCodeSplitPluginFactory } from "./global-code-split-plugin.mjs";
import { facesResourceLoaderPlugin } from "./faces-resource-loader-plugin.mjs";
import { createMetaFile } from "./create-meta-file.mjs";

const isProduction = process.env.NODE_ENV !== "development";

const baseDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const bundlesDir = path.join(baseDir, "bundles");
const srcDir = path.join(baseDir, "src");
const resourcesDir = path.resolve(baseDir, "..", "..", "..", "target", "generated-resources", "META-INF", "resources");
const outputDir = path.join(resourcesDir, "primefaces");

const globalCodeSplitPlugin = globalCodeSplitPluginFactory();

// PrimeFaces consists of several source files. External libraries are
// included as separate JavaScript files. Linking with other source
// files is done via a global variable (window.PrimeFacesLibs). The
// following defines the NPM packages which need to be linked.
const LinkedLibraries = {
    autoNumeric: [/^autonumeric$/],
    autosize: [/^autosize$/],
    chartJs: [/^chart\.js$/, /^chart\.js\/.*$/, /^hammerjs$/],
    coloris: [/^@melloware\/coloris$/],
    cropperJs: [/^cropperjs$/, /^jquery-cropper$/],
    fileUpload: [/^blueimp-file-upload(\/.*)?$/],
    fullCalendar: [/^@fullcalendar\/.*$/],
    inputMask: [/^inputmask$/, /^inputmask\/.*$/],
    jsCookie: [/^js-cookie$/],
    jQuery: [/^jquery$/],
    jsPlumb: [/^jsplumb$/],
    moment: [/^moment$/, /^moment-jdateformatparser$/],
    momentTimeZone: [/^moment-timezone$/],
    quill: [/^quill$/],
    raphael: [/^raphael$/],
    webcamJs: [/^webcamjs$/],
};

/**
 * Base options for all ESBuild tasks.
 * @type {import("esbuild").BuildOptions}
 */
const BaseOptions = {
    absWorkingDir: baseDir,
    bundle: true,
    charset: "utf8",
    target: "es2016",
    format: "iife",
    platform: "browser",
    legalComments: "external",
    minify: isProduction,
    sourcemap: isProduction ? "external" : "inline",
    plugins: [
        facesResourceLoaderPlugin({
            extensions: ["png", "jpg", "jpeg", "gif", "svg", "woff", "woff2", "ttf", "eot"],
            inputDir: srcDir,
            outputDir: outputDir,
            resourceBase: resourcesDir,
            useLibrary: true,
        }),
    ],
};

/**
 * Creates a new ESBuild task that compresses the given JavaScript or CSS
 * file and writes the output to the given path. The source path is relative
 * to the `bundles` directory, the target path is relative to the main output
 * directory.
 * @param {string} from The source file.
 * @param {string} to The target file.
 * @param {{include?: (keyof typeof LinkedLibraries)[]}} [settings] External libraries to include in the bundle.   
 * @returns {import("esbuild").BuildOptions} The ESBuild task.
 */
function buildTask(from, to, settings = {}) {
    const fromPath = path.join(bundlesDir, from);
    const toPath = path.join(outputDir, to);

    const includes = settings.include ?? []
    /** @type {import("./global-code-split-plugin.mjs").GlobalCodeSplitModule[]} */
    const modules = Object.entries(LinkedLibraries).flatMap(([key, patterns]) => {
        const mode = includes.some(k => k === key) ? "expose" : "link";
        return patterns.map(pattern => ({ mode, pattern }));
    });

    const buildTask = { ...BaseOptions, entryPoints: [fromPath], outfile: toPath, };
    buildTask.plugins = [...buildTask.plugins ?? []];
    buildTask.plugins.push(globalCodeSplitPlugin.newPlugin({ scope: "PrimeFacesLibs", modules }));
    return buildTask;
}

/**
 * Creates the individual ESBuild tasks for the locale files in the
 * `src/locales` directory. Each locale file will be compressed and
 * written to a separate file in the output directory.
 * @returns {Promise<import("esbuild").BuildOptions[]>}
 */
async function createLocaleBuildTasks() {
    const localeDir = path.join(srcDir, "locales");
    const localeFiles = await fs.readdir(localeDir);
    return localeFiles.map(file => {
        const fromPath = path.join(localeDir, file);
        const toPath = path.join(outputDir, "locales", file);
        return {
            ...BaseOptions,
            entryPoints: [fromPath],
            outfile: toPath,
        };
    });
}

/** @returns {import("esbuild").BuildOptions[]} */
function createLibraryBuildTasks() {
    return [
        buildTask("libs/jquery.ts", "jquery/jquery.js", { include: ["jQuery"] }),
        buildTask("libs/moment.ts", "moment/moment.js", { include: ["moment"] }),
        buildTask("libs/moment-timezone-with-data.ts", "moment/moment-timezone-with-data.js", { include: ["momentTimeZone"] }),
        buildTask("libs/raphael.ts", "raphael/raphael.js", { include: ["raphael"] }),
    ];
}

/** @returns {import("esbuild").BuildOptions[]} */
function createCoreBuildTasks() {
    return [
        buildTask("base/core.ts", "core.js", { include: ["jsCookie"] }),
        buildTask("base/components.ts", "components.js"),
        buildTask("base/components.css", "components.css"),
        buildTask("base/jquery-plugins.ts", "jquery/jquery-plugins.js", { include: ["autosize"] }),
    ];
}

/** @returns {import("esbuild").BuildOptions[]} */
function createComponentsBuildTasks() {
    return [
        buildTask("components/calendar.ts", "calendar/calendar.js"),
        buildTask("components/calendar.css", "calendar/calendar.css"),
        buildTask("components/captcha.ts", "captcha/captcha.js"),
        buildTask("components/chart.ts", "chart/chart.js", { include: ["chartJs"] }),
        buildTask("components/clock.ts", "clock/clock.js"),
        buildTask("components/clock.css", "clock/clock.css"),
        buildTask("components/colorpicker.ts", "colorpicker/colorpicker.js", { include: ["coloris"] }),
        buildTask("components/colorpicker.css", "colorpicker/colorpicker.css"),
        buildTask("components/datepicker.ts", "datepicker/datepicker.js"),
        buildTask("components/diagram.ts", "diagram/diagram.js", { include: ["jsPlumb"] }),
        buildTask("components/diagram.css", "diagram/diagram.css"),
        buildTask("components/dock.ts", "dock/dock.js"),
        buildTask("components/dock.css", "dock/dock.css"),
        buildTask("components/filedownload.ts", "filedownload/filedownload.js"),
        buildTask("components/fileupload.ts", "fileupload/fileupload.js", { include: ["fileUpload"] }),
        buildTask("components/fileupload.css", "fileupload/fileupload.css"),
        buildTask("components/galleria.ts", "galleria/galleria.js"),
        buildTask("components/galleria.css", "galleria/galleria.css"),
        buildTask("components/gmap.ts", "gmap/gmap.js"),
        buildTask("components/hotkey.ts", "hotkey/hotkey.js"),
        buildTask("components/idlemonitor.ts", "idlemonitor/idlemonitor.js"),
        buildTask("components/imagecompare.ts", "imagecompare/imagecompare.js"),
        buildTask("components/imagecompare.css", "imagecompare/imagecompare.css"),
        buildTask("components/imagecropper.ts", "imagecropper/imagecropper.js", { include: ["cropperJs"] }),
        buildTask("components/imagecropper.css", "imagecropper/imagecropper.css"),
        buildTask("components/imageswitch.ts", "imageswitch/imageswitch.js"),
        buildTask("components/inputmask.ts", "inputmask/inputmask.js", { include: ["inputMask"] }),
        buildTask("components/inputnumber.ts", "inputnumber/inputnumber.js", { include: ["autoNumeric"] }),
        buildTask("components/keyboard.ts", "keyboard/keyboard.js"),
        buildTask("components/keyboard.css", "keyboard/keyboard.css"),
        buildTask("components/keyfilter.ts", "keyfilter/keyfilter.js"),
        buildTask("components/knob.ts", "knob/knob.js"),
        buildTask("components/lifecycle.ts", "lifecycle/lifecycle.js"),
        buildTask("components/lifecycle.css", "lifecycle/lifecycle.css"),
        buildTask("components/log.ts", "log/log.js"),
        buildTask("components/log.css", "log/log.css"),
        buildTask("components/mindmap.ts", "mindmap/mindmap.js"),
        buildTask("components/organigram.ts", "organigram/organigram.js"),
        buildTask("components/organigram.css", "organigram/organigram.css"),
        buildTask("components/photocam.ts", "photocam/photocam.js", { include: ["webcamJs"] }),
        buildTask("components/primeicons.css", "primeicons/primeicons.css"),
        buildTask("components/printer.ts", "printer/printer.js"),
        buildTask("components/schedule.ts", "schedule/schedule.js", { include: ["fullCalendar"] }),
        buildTask("components/scrollpanel.ts", "scrollpanel/scrollpanel.js"),
        buildTask("components/scrollpanel.css", "scrollpanel/scrollpanel.css"),
        buildTask("components/signature.ts", "signature/signature.js"),
        buildTask("components/signature.css", "signature/signature.css"),
        buildTask("components/stack.ts", "stack/stack.js"),
        buildTask("components/stack.css", "stack/stack.css"),
        buildTask("components/terminal.ts", "terminal/terminal.js"),
        buildTask("components/terminal.css", "terminal/terminal.css"),
        buildTask("components/texteditor.ts", "texteditor/texteditor.js", { include: ["quill"] }),
        buildTask("components/texteditor.css", "texteditor/texteditor.css"),
        buildTask("components/timeline.ts", "timeline/timeline.js"),
        buildTask("components/timeline.css", "timeline/timeline.css"),
        buildTask("components/touchswipe.ts", "touch/touchswipe.js"),
        buildTask("components/validation.bv.ts", "validation/validation.bv.js"),
    ];
}

async function main() {
    console.log(`Building PrimeFaces resources with mode ${isProduction ? "production" : "development"}...`);

    const t1 = Date.now();
    const allBuildTasks = [
        ...createLibraryBuildTasks(),
        ...createCoreBuildTasks(),
        ...createComponentsBuildTasks(),
        ...await createLocaleBuildTasks(),
    ];

    const t2 = Date.now();
    const metaFile = await createMetaFile(allBuildTasks);
    globalCodeSplitPlugin.setMetaFile(metaFile);

    // Start all promises and wait for them to finish.
    // This will build all tasks in parallel, speeding up the build process.
    const t3 = Date.now();
    const allResults = await Promise.allSettled(allBuildTasks.map(task => esbuild.build(task)));
    const t4 = Date.now();

    const exceptions = allResults.filter(r => r.status === "rejected");
    if (exceptions.length > 0) {
        throw new AggregateError(exceptions.map(r => r.reason));
    }

    console.log(`Created all bundles in ${t2 - t1}ms`);
    console.log(`Analyzed bundles in ${t3 - t2}ms`);
    console.log(`Built all bundles in ${t4 - t3}ms`);
}

main().catch(e => {
    console.error(e);
    process.exit(1);
});
