// @ts-check

import * as path from "node:path";
import { fileURLToPath } from "node:url";
import fs from "node:fs/promises";

import * as esbuild from "esbuild";

import { customModuleSourcePlugin } from "./custom-module-source-plugin.mjs";
import { facesResourceLoaderPlugin } from "./faces-resource-loader-plugin.mjs";

const isProduction = process.env.NODE_ENV !== "development";

const baseDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const bundlesDir = path.join(baseDir, "bundles");
const srcDir = path.join(baseDir, "src");
const resourcesDir = path.resolve(baseDir, "..", "..", "..", "target", "generated-resources", "META-INF", "resources");
const outputDir = path.join(resourcesDir, "primefaces");

// PrimeFaces consists of several source files. External libraries are included
// as separate JavaScript files. Linking with other source files is done via global
// variables. The following objects define the global variables that are used by
// the external libraries. The key is the import path (name of the NPM package),
// the value is the global variable name that contains the library.

const LibsChartJs = {
    "chart.js": "window.PrimeFacesLibs.ChartJs",
    "chart.js/helpers": "window.PrimeFacesLibs.ChartJsHelpers",
    "hammerjs": "window.PrimeFacesLibs.Hammer",
}

const LibsJsCookie = {
    "js-cookie": "window.Cookies",
}

const LibsCropperJs = {
    "cropperjs": "window.Cropper",
};

const LibsFullCalendar = {
    "@fullcalendar/core": "window.PrimeFacesLibs.FullCalendarCore",
    "@fullcalendar/interaction": "window.PrimeFacesLibs.FullCalendarInteraction",
    "@fullcalendar/daygrid": "window.PrimeFacesLibs.FullCalendarDayGrid",
    "@fullcalendar/timegrid": "window.PrimeFacesLibs.FullCalendarTimeGrid",
    "@fullcalendar/list": "window.PrimeFacesLibs.FullCalendarList",
    "@fullcalendar/moment": "window.PrimeFacesLibs.FullCalendarMoment",
    "@fullcalendar/moment-timezone": "window.PrimeFacesLibs.FullCalendarMomentTimezone",
    "@fullcalendar/core/locales-all": "window.PrimeFacesLibs.FullCalendarLocalesAll",
};

const LibsJQuery = {
    "jquery": "window.$",
};

const LibsJsPlumb = {
    "jsplumb": "window.PrimeFacesLibs.jsPlumb",
};

const LibsMoment = {
    "moment": "window.moment",
    "moment-jdateformatparser": "window.momentJDateFormatParserSetup",
}

const LibsMomentTimezone = {
    "moment-timezone": "window.moment",
};

const LibsQuill = {
    "quill": "window.Quill",
};

const LibsRaphael = {
    "raphael": "window.Raphael",
};

const LibsWebcamJs = {
    "webcamjs": "window.Webcam",
};

const ExternalLibraries = {
    ...LibsChartJs,
    ...LibsCropperJs,
    ...LibsFullCalendar,
    ...LibsJsCookie,
    ...LibsJQuery,
    ...LibsJsPlumb,
    ...LibsMoment,
    ...LibsMomentTimezone,
    ...LibsQuill,
    ...LibsRaphael,
    ...LibsWebcamJs,
    // Non-existent import used by "1-jquery.fileupload", just ignore it...
    "./vendor/jquery.ui.widget": "{}",
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
 * @template {Partial<typeof ExternalLibraries>} Excludes
 * @param {string} from The source file.
 * @param {string} to The target file.
 * @param {Excludes} [excludes] Excludes for import replacements. 
 * @returns {import("esbuild").BuildOptions} The ESBuild task.
 */
function buildTask(from, to, excludes) {
    const fromPath = path.join(bundlesDir, from);
    const toPath = path.join(outputDir, to);
    const buildTask = { ...BaseOptions, entryPoints: [fromPath], outfile: toPath, };
    buildTask.plugins = [...buildTask.plugins ?? []];
    buildTask.plugins.push(
        customModuleSourcePlugin(excludeKeys(ExternalLibraries, recordKeys(excludes ?? {})))
    );
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

/** @type {import("esbuild").BuildOptions[]} */
const LibraryBuildTasks = [
    buildTask("libs/jquery.ts", "jquery/jquery.js", LibsJQuery),
    buildTask("libs/moment.ts", "moment/moment.js", LibsMoment),
    buildTask("libs/moment-timezone-with-data.ts", "moment/moment-timezone-with-data.js", LibsMomentTimezone),
    buildTask("libs/raphael.ts", "raphael/raphael.js", LibsRaphael),
];

/** @type {import("esbuild").BuildOptions[]} */
const CoreBuildTasks = [
    buildTask("base/core.ts", "core.js", LibsJsCookie),
    buildTask("base/components.ts", "components.js"),
    buildTask("base/components.css", "components.css"),
    buildTask("base/jquery-plugins.ts", "jquery/jquery-plugins.js"),
];

/** @type {import("esbuild").BuildOptions[]} */
const ComponentsBuildTasks = [
    buildTask("components/calendar.ts", "calendar/calendar.js"),
    buildTask("components/calendar.css", "calendar/calendar.css"),
    buildTask("components/captcha.ts", "captcha/captcha.js"),
    buildTask("components/chart.ts", "chart/chart.js", LibsChartJs),
    buildTask("components/clock.ts", "clock/clock.js"),
    buildTask("components/clock.css", "clock/clock.css"),
    buildTask("components/colorpicker.ts", "colorpicker/colorpicker.js"),
    buildTask("components/colorpicker.css", "colorpicker/colorpicker.css"),
    buildTask("components/datepicker.ts", "datepicker/datepicker.js"),
    buildTask("components/diagram.ts", "diagram/diagram.js", LibsJsPlumb),
    buildTask("components/diagram.css", "diagram/diagram.css"),
    buildTask("components/dock.ts", "dock/dock.js"),
    buildTask("components/dock.css", "dock/dock.css"),
    buildTask("components/filedownload.ts", "filedownload/filedownload.js"),
    buildTask("components/fileupload.ts", "fileupload/fileupload.js"),
    buildTask("components/fileupload.css", "fileupload/fileupload.css"),
    buildTask("components/galleria.ts", "galleria/galleria.js"),
    buildTask("components/galleria.css", "galleria/galleria.css"),
    buildTask("components/gmap.ts", "gmap/gmap.js"),
    buildTask("components/hotkey.ts", "hotkey/hotkey.js"),
    buildTask("components/idlemonitor.ts", "idlemonitor/idlemonitor.js"),
    buildTask("components/imagecompare.ts", "imagecompare/imagecompare.js"),
    buildTask("components/imagecompare.css", "imagecompare/imagecompare.css"),
    buildTask("components/imagecropper.ts", "imagecropper/imagecropper.js", LibsCropperJs),
    buildTask("components/imagecropper.css", "imagecropper/imagecropper.css", LibsCropperJs),
    buildTask("components/imageswitch.ts", "imageswitch/imageswitch.js"),
    buildTask("components/inputmask.ts", "inputmask/inputmask.js"),
    buildTask("components/inputnumber.ts", "inputnumber/inputnumber.js"),
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
    buildTask("components/photocam.ts", "photocam/photocam.js", LibsWebcamJs),
    buildTask("components/primeicons.css", "primeicons/primeicons.css"),
    buildTask("components/printer.ts", "printer/printer.js"),
    buildTask("components/schedule.ts", "schedule/schedule.js", LibsFullCalendar),
    buildTask("components/schedule.css", "schedule/schedule.css"),
    buildTask("components/scrollpanel.ts", "scrollpanel/scrollpanel.js"),
    buildTask("components/scrollpanel.css", "scrollpanel/scrollpanel.css"),
    buildTask("components/signature.ts", "signature/signature.js"),
    buildTask("components/signature.css", "signature/signature.css"),
    buildTask("components/stack.ts", "stack/stack.js"),
    buildTask("components/stack.css", "stack/stack.css"),
    buildTask("components/terminal.ts", "terminal/terminal.js"),
    buildTask("components/terminal.css", "terminal/terminal.css"),
    buildTask("components/texteditor.ts", "texteditor/texteditor.js", LibsQuill),
    buildTask("components/texteditor.css", "texteditor/texteditor.css"),
    buildTask("components/timeline.ts", "timeline/timeline.js"),
    buildTask("components/timeline.css", "timeline/timeline.css"),
    buildTask("components/touchswipe.ts", "touch/touchswipe.js"),
    buildTask("components/validation.bv.ts", "validation/validation.bv.js"),
];

async function main() {
    console.log(`Building PrimeFaces resources with mode ${isProduction ? "production" : "development"}...`);
    // Start all promises and wait for them to finish.
    // This will build all tasks in parallel, speeding up the build process.
    const LocaleBuildTasks = await createLocaleBuildTasks();
    const result = await Promise.allSettled([
        ...LibraryBuildTasks,
        ...CoreBuildTasks,
        ...ComponentsBuildTasks,
        ...LocaleBuildTasks,
    ].map(task => esbuild.build(task)));
    const errors = result.filter(r => r.status === "rejected");
    if (errors.length > 0) {
        throw new AggregateError(errors.map(r => r.reason));
    }
}

/**
 * Creates a new object without the given keys.
 * @template T Type of the object.
 * @template {keyof T} K Type of the keys to exclude.
 * @param {T} obj Object from which to exclude keys.
 * @param {K[]} keys Keys to exclude.
 * @returns {Omit<T, K>} Object without the given keys.
 */
function excludeKeys(obj, keys) {
    const result = { ...obj };
    for (const key of keys) {
        delete result[key];
    }
    return result;
}

/**
 * Returns the keys of the given object.
 * @template {{}} T Type of the object.
 * @param {T} obj Object from which to get the keys.
 * @returns {(keyof T)[]} Keys of the object.
 */
function recordKeys(obj) {
    return /** @type {(keyof T)[]} */(Object.keys(obj));
}

main().catch(e => {
    console.error(e);
    process.exit(1);
});