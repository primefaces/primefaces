// @ts-check

import * as path from "node:path";
import { fileURLToPath } from "node:url";
import fs from "node:fs/promises";

import * as esbuild from "esbuild";

import { customModuleSourcePlugin } from "./custom-module-source-plugin.mjs";

const isProduction = process.env.NODE_ENV !== "development";

const baseDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const bundlesDir = path.join(baseDir, "bundles");
const outputDir = path.join(baseDir, "..", "..", "..", "target", "generated-resources", "META-INF", "resources", "primefaces");


const LibsChartJs = {
    "chart.js": "window.ChartJs",
    "chart.js/helpers": "window.ChartJsHelpers",
    "hammerjs": "window.Hammer",
}

const LibsCropperJs = {
    "cropperjs": "window.Cropper",
};

const LibsJQuery = {
    "jQuery": "window.$",
};

const LibsMoment = {
    "moment": "window.moment",
    "moment-jdateformatparser": "window.momentJDateFormatParserSetup",
}

const LibsMomentTimezone = {
    "moment-timezone": "window.moment",
};

const LibsRaphael = {
    "raphael": "window.Raphael",
};

const ExternalLibraries = {
    ...LibsChartJs,
    ...LibsCropperJs,
    ...LibsJQuery,
    ...LibsMoment,
    ...LibsMomentTimezone,
    ...LibsRaphael,
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
    target: "es2016",
    minify: isProduction,
    sourcemap: isProduction ? false : "inline",
    plugins: [],
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
    const localeDir = path.join(baseDir, "src", "locales");
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
    buildTask("libs/jquery.js", "jquery/jquery.js", LibsJQuery),
    buildTask("libs/moment.js", "moment/moment.js", LibsMoment),
    buildTask("libs/moment-timezone-with-data.js", "moment/moment-timezone-with-data.js", LibsMomentTimezone),
    buildTask("libs/raphael.js", "raphael/raphael.js", LibsRaphael),
];

/** @type {import("esbuild").BuildOptions[]} */
const CoreBuildTasks = [
    buildTask("base/core.js", "core.js"),
    buildTask("base/components.js", "components.js"),
    buildTask("base/components.css", "components.css"),
    buildTask("base/jquery-plugins.js", "jquery/jquery-plugins.js"),
];

/** @type {import("esbuild").BuildOptions[]} */
const ComponentsBuildTasks = [
    buildTask("components/calendar.js", "calendar/calendar.js"),
    buildTask("components/calendar.css", "calendar/calendar.css"),
    buildTask("components/chart.ts", "chart/chart.js", LibsChartJs),
    buildTask("components/clock.js", "clock/clock.js"),
    buildTask("components/clock.css", "clock/clock.css"),
    buildTask("components/colorpicker.js", "colorpicker/colorpicker.js"),
    buildTask("components/colorpicker.css", "colorpicker/colorpicker.css"),
    buildTask("components/datepicker.js", "datepicker/datepicker.js"),
    buildTask("components/diagram.js", "diagram/diagram.js"),
    buildTask("components/diagram.css", "diagram/diagram.css"),
    buildTask("components/dock.js", "dock/dock.js"),
    buildTask("components/dock.css", "dock/dock.css"),
    buildTask("components/filedownload.js", "filedownload/filedownload.js"),
    buildTask("components/fileupload.js", "fileupload/fileupload.js"),
    buildTask("components/fileupload.css", "fileupload/fileupload.css"),
    buildTask("components/galleria.js", "fileupload/galleria.js"),
    buildTask("components/galleria.css", "fileupload/galleria.css"),
    buildTask("components/gmap.js", "gmap/gmap.js"),
    buildTask("components/hotkey.js", "hotkey/hotkey.js"),
    buildTask("components/idlemonitor.js", "idlemonitor/idlemonitor.js"),
    buildTask("components/imagecompare.js", "imagecompare/imagecompare.js"),
    buildTask("components/imagecompare.css", "imagecompare/imagecompare.css"),
    buildTask("components/imagecropper.js", "imagecropper/imagecropper.js"),
    buildTask("components/imagecropper.css", "imagecropper/imagecropper.css"),
    buildTask("components/imageswitch.js", "imageswitch/imageswitch.js"),
    buildTask("components/inputmask.js", "inputmask/inputmask.js"),
    buildTask("components/inputnumber.js", "inputnumber/inputnumber.js"),
    buildTask("components/keyboard.js", "keyboard/keyboard.js"),
    buildTask("components/keyboard.css", "keyboard/keyboard.css"),
    buildTask("components/keyfilter.js", "keyfilter/keyfilter.js"),
    buildTask("components/knob.js", "knob/knob.js"),
    buildTask("components/lifecycle.js", "lifecycle/lifecycle.js"),
    buildTask("components/lifecycle.css", "lifecycle/lifecycle.css"),
    buildTask("components/log.js", "log/log.js"),
    buildTask("components/log.css", "log/log.css"),
    buildTask("components/mindmap.js", "mindmap/mindmap.js"),
    buildTask("components/organigram.js", "organigram/organigram.js"),
    buildTask("components/organigram.css", "organigram/organigram.css"),
    buildTask("components/photocam.js", "photocam/photocam.js"),
    buildTask("components/primeicons.css", "primeicons/primeicons.css"),
    buildTask("components/printer.js", "printer/printer.js"),
    buildTask("components/schedule.js", "schedule/schedule.js"),
    buildTask("components/schedule.css", "schedule/schedule.css"),
    buildTask("components/scrollpanel.js", "scrollpanel/scrollpanel.js"),
    buildTask("components/scrollpanel.css", "scrollpanel/scrollpanel.css"),
    buildTask("components/signature.js", "signature/signature.js"),
    buildTask("components/signature.css", "signature/signature.css"),
    buildTask("components/stack.js", "stack/stack.js"),
    buildTask("components/stack.css", "stack/stack.css"),
    buildTask("components/terminal.js", "terminal/terminal.js"),
    buildTask("components/terminal.css", "terminal/terminal.css"),
    buildTask("components/texteditor.js", "texteditor/texteditor.js"),
    buildTask("components/texteditor.css", "texteditor/texteditor.css"),
    buildTask("components/timeline.js", "timeline/timeline.js"),
    buildTask("components/timeline.css", "timeline/timeline.css"),
];

async function main() {
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
 * @template T
 * @template {keyof T} K
 * @param {T} obj 
 * @param {K[]} keys 
 * @returns {Omit<T, K>}
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
 * @template {{}} T
 * @param {T} obj 
 * @returns {(keyof T)[]}
 */
function recordKeys(obj) {
    return /** @type {(keyof T)[]} */(Object.keys(obj));
}

main().catch(e => {
    console.error(e);
    process.exit(1);
});