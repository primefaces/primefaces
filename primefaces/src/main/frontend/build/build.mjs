// @ts-check

import path from "node:path";
import { fileURLToPath } from "node:url";
import fs from "node:fs/promises";

import * as esbuild from "esbuild";

import { globalCodeSplitPluginFactory } from "./esbuild-plugin/global-code-split-plugin.mjs";
import { facesResourceLoaderPlugin } from "./esbuild-plugin/faces-resource-loader-plugin.mjs";
import { bannedDependenciesPlugin } from "./esbuild-plugin/banned-dependencies-plugin.mjs";

import { createMetaFile } from "./esbuild/create-meta-file.mjs";
import { escapeRegExp } from "./reg-exp.mjs";

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
//
// When a package name ends with a slash, it is treated as a prefix.
// E.g. ""autonumeric/" will match "autonumeric" as well as
// "autonumeric/dist/autoNumeric.min.js" etc.
const LinkedLibraries = {
    calendar: ["jquery-ui/ui/widgets/datepicker.js", "jquery-ui-timepicker-addon/"],
    chart: ["chart.js/"],
    colorPicker: ["@melloware/coloris/"],
    core: ["js-cookie/"],
    imageCropper: ["cropperjs/", "jquery-cropper/"],
    fileDownload: ["downloadjs/"],
    fileUpload: ["blueimp-file-upload/"],
    schedule: ["@fullcalendar/"],
    inputMask: ["inputmask/"],
    inputNumber: ["autonumeric/"],
    inputTextArea: ["autosize/"],
    jQuery: ["jquery/", "jquery.browser/"],
    jQueryPlugins: ["jquery-mousewheel/", "jquery-ui/", "rangyinputs/"],
    jsPlumb: ["jsplumb/"],
    textEditor: ["quill/"],
    moment: ["moment/", "moment-jdateformatparser/"],
    momentTimeZone: ["moment-timezone/"],
    raphael: ["raphael/"],
    scrollPanel: ["jscrollpane/"],
    timeline: ["moment/dist/locale/", "vis-timeline/", "vis-data/", "vis-util/"],
    touchSwipe: ["jquery-touchswipe/"],
    webcamJs: ["webcamjs/"],
};

const BannedDependencies = [
    {
        pattern: /^vis-(timeline|data|util)$/,
        reason: "Do not use vis packages directly, use their ESM variant, e.g. 'vis-data/esnext/esm/vis-data.js'. Only these match the (peer) dependencies declared in their respective package.json.",
    }
];

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
    const fromPath = path.join(bundlesDir, from);
    const toPath = path.join(outputDir, to);

    const modulesToExpose = (settings.expose ?? []).flatMap(expose => LinkedLibraries[expose] ?? []);
    /** @type {import("./global-code-split-plugin.mjs").GlobalCodeSplitModule[]} */
    const modules = Object.entries(LinkedLibraries).flatMap(([key, names]) => {
        return names.map(name => {
            const isPrefix = name.endsWith("/");
            const baseName = isPrefix ? name.slice(0, name.length - 1) : name;
            const pattern = new RegExp(`^${escapeRegExp(baseName)}${isPrefix ? "(/.+)?" : ""}$`);
            const mode = modulesToExpose.includes(name) ? "expose" : "link";
            return { mode, pattern };
        });
    });

    const buildTask = { ...createBaseOptions(), entryPoints: [fromPath], outfile: toPath, };
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
            ...createBaseOptions(),
            entryPoints: [fromPath],
            outfile: toPath,
        };
    });
}

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

/** @returns {import("esbuild").BuildOptions[]} */
function createLibraryBuildTasks() {
    return [
        buildTask("libs/jquery.ts", "jquery/jquery.js", { expose: ["jQuery"] }),
        buildTask("libs/moment.ts", "moment/moment.js", { expose: ["moment"] }),
        buildTask("libs/moment-timezone-with-data.ts", "moment/moment-timezone-with-data.js", { expose: ["momentTimeZone"] }),
        buildTask("libs/raphael.ts", "raphael/raphael.js", { expose: ["raphael"] }),
        buildTask("libs/touchswipe.ts", "touch/touchswipe.js", { expose: ["touchSwipe"] }),
    ];
}

/** @returns {import("esbuild").BuildOptions[]} */
function createCoreBuildTasks() {
    return [
        buildTask("base/core.ts", "core.js", { expose: ["core"] }),
        buildTask("base/components.ts", "components.js"),
        buildTask("base/components.css", "components.css"),
        buildTask("base/jquery-plugins.ts", "jquery/jquery-plugins.js", { expose: ["jQueryPlugins"] }),
    ];
}

/** @returns {import("esbuild").BuildOptions[]} */
function createComponentsBuildTasks() {
    return [
        buildTask("components/calendar.ts", "calendar/calendar.js", { expose: ["calendar"] }),
        buildTask("components/calendar.css", "calendar/calendar.css"),
        buildTask("components/captcha.ts", "captcha/captcha.js"),
        buildTask("components/chart.ts", "chart/chart.js", { expose: ["chart"] }),
        buildTask("components/clock.ts", "clock/clock.js"),
        buildTask("components/clock.css", "clock/clock.css"),
        buildTask("components/colorpicker.ts", "colorpicker/colorpicker.js", { expose: ["colorPicker"] }),
        buildTask("components/colorpicker.css", "colorpicker/colorpicker.css"),
        buildTask("components/datepicker.ts", "datepicker/datepicker.js"),
        buildTask("components/diagram.ts", "diagram/diagram.js", { expose: ["jsPlumb"] }),
        buildTask("components/diagram.css", "diagram/diagram.css"),
        buildTask("components/dock.ts", "dock/dock.js"),
        buildTask("components/dock.css", "dock/dock.css"),
        buildTask("components/filedownload.ts", "filedownload/filedownload.js", { expose: ["fileDownload"] }),
        buildTask("components/fileupload.ts", "fileupload/fileupload.js", { expose: ["fileUpload"] }),
        buildTask("components/fileupload.css", "fileupload/fileupload.css"),
        buildTask("components/galleria.ts", "galleria/galleria.js"),
        buildTask("components/galleria.css", "galleria/galleria.css"),
        buildTask("components/gmap.ts", "gmap/gmap.js"),
        buildTask("components/hotkey.ts", "hotkey/hotkey.js"),
        buildTask("components/idlemonitor.ts", "idlemonitor/idlemonitor.js"),
        buildTask("components/imagecompare.ts", "imagecompare/imagecompare.js"),
        buildTask("components/imagecompare.css", "imagecompare/imagecompare.css"),
        buildTask("components/imagecropper.ts", "imagecropper/imagecropper.js", { expose: ["imageCropper"] }),
        buildTask("components/imagecropper.css", "imagecropper/imagecropper.css"),
        buildTask("components/imageswitch.ts", "imageswitch/imageswitch.js"),
        buildTask("components/inputmask.ts", "inputmask/inputmask.js", { expose: ["inputMask"] }),
        buildTask("components/inputnumber.ts", "inputnumber/inputnumber.js", { expose: ["inputNumber"] }),
        buildTask("components/inputtextarea.ts", "forms/inputtextarea.js", { expose: ["inputTextArea"] }),
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
        buildTask("components/photocam.ts", "photocam/photocam.js", { expose: ["webcamJs"] }),
        buildTask("components/primeicons.css", "primeicons/primeicons.css"),
        buildTask("components/printer.ts", "printer/printer.js"),
        buildTask("components/schedule.ts", "schedule/schedule.js", { expose: ["schedule"] }),
        buildTask("components/scrollpanel.ts", "scrollpanel/scrollpanel.js", { expose: ["scrollPanel"] }),
        buildTask("components/scrollpanel.css", "scrollpanel/scrollpanel.css"),
        buildTask("components/signature.ts", "signature/signature.js"),
        buildTask("components/signature.css", "signature/signature.css"),
        buildTask("components/stack.ts", "stack/stack.js"),
        buildTask("components/stack.css", "stack/stack.css"),
        buildTask("components/terminal.ts", "terminal/terminal.js"),
        buildTask("components/terminal.css", "terminal/terminal.css"),
        buildTask("components/texteditor.ts", "texteditor/texteditor.js", { expose: ["textEditor"] }),
        buildTask("components/texteditor.css", "texteditor/texteditor.css"),
        buildTask("components/timeline.ts", "timeline/timeline.js", { expose: ["timeline"] }),
        buildTask("components/timeline.css", "timeline/timeline.css"),
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
