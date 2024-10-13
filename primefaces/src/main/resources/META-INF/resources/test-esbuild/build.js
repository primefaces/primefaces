import * as esbuild from "esbuild";

const BaseOptions = {
    bundle: true,
    external: ["jquery", "cropperjs", "./vendor/jquery.ui.widget"],
    target: "es2016",
    minify: true,
};

function buildTask(from, to) {
    return {...BaseOptions, entryPoints: [from], outfile: to,};
}

const BaseBuildTasks = [
    buildTask("bundles-base/core.js", "dist/core.js"),
    buildTask("bundles-base/components.js", "dist/components.js"),
    buildTask("bundles-base/jquery-plugins.js", "dist/jquery-plugins.js"),
    buildTask("bundles-base/components.css", "dist/components.css"),
];

const ComponentsBuildTasks = [
    buildTask("bundles-components/calendar.js", "dist/calendar.js"),
    buildTask("bundles-components/calendar.css", "dist/calendar.css"),
    buildTask("bundles-components/chart.js", "dist/chart.js"),
    buildTask("bundles-components/clock.js", "dist/clock.js"),
    buildTask("bundles-components/clock.css", "dist/clock.css"),
    buildTask("bundles-components/colorpicker.js", "dist/colorpicker.js"),
    buildTask("bundles-components/colorpicker.css", "dist/colorpicker.css"),
    buildTask("bundles-components/datepicker.js", "dist/datepicker.js"),
    buildTask("bundles-components/datepicker.css", "dist/datepicker.css"),
    buildTask("bundles-components/diagram.js", "dist/diagram.js"),
    buildTask("bundles-components/diagram.css", "dist/diagram.css"),
    buildTask("bundles-components/dock.js", "dist/dock.js"),
    buildTask("bundles-components/dock.css", "dist/dock.css"),
    buildTask("bundles-components/filedownload.js", "dist/filedownload.js"),
    buildTask("bundles-components/fileupload.js", "dist/fileupload.js"),
    buildTask("bundles-components/fileupload.css", "dist/fileupload.css"),
    buildTask("bundles-components/galleria.js", "dist/galleria.js"),
    buildTask("bundles-components/galleria.css", "dist/galleria.css"),
    buildTask("bundles-components/gmap.js", "dist/gmap.js"),
    buildTask("bundles-components/hotkey.js", "dist/hotkey.js"),
    buildTask("bundles-components/idlemonitor.js", "dist/idlemonitor.js"),
    buildTask("bundles-components/imagecompare.js", "dist/imagecompare.js"),
    buildTask("bundles-components/imagecompare.css", "dist/imagecompare.css"),
    buildTask("bundles-components/imagecropper.js", "dist/imagecropper.js"),
    buildTask("bundles-components/imagecropper.css", "dist/imagecropper.css"),
    buildTask("bundles-components/imageswitch.js", "dist/imageswitch.js"),
    buildTask("bundles-components/inputmask.js", "dist/inputmask.js"),
    buildTask("bundles-components/inputnumber.js", "dist/inputnumber.js"),
    buildTask("bundles-components/keyboard.js", "dist/keyboard.js"),
    buildTask("bundles-components/keyboard.css", "dist/keyboard.css"),
    buildTask("bundles-components/keyfilter.js", "dist/keyfilter.js"),
    buildTask("bundles-components/knob.js", "dist/knob.js"),
    buildTask("bundles-components/lifecycle.js", "dist/lifecycle.js"),
    buildTask("bundles-components/lifecycle.css", "dist/lifecycle.css"),
    buildTask("bundles-components/log.js", "dist/log.js"),
    buildTask("bundles-components/log.css", "dist/log.css"),
    buildTask("bundles-components/mindmap.js", "dist/mindmap.js"),
    buildTask("bundles-components/organigram.js", "dist/organigram.js"),
    buildTask("bundles-components/organigram.css", "dist/organigram.css"),
    buildTask("bundles-components/photocam.js", "dist/photocam.js"),
    buildTask("bundles-components/primeicons.css", "dist/primeicons.css"),
    buildTask("bundles-components/printer.js", "dist/printer.js"),
    buildTask("bundles-components/scrollpanel.js", "dist/scrollpanel.js"),
    buildTask("bundles-components/scrollpanel.css", "dist/scrollpanel.css"),
    buildTask("bundles-components/signature.js", "dist/signature.js"),
    buildTask("bundles-components/signature.css", "dist/signature.css"),
    buildTask("bundles-components/stack.js", "dist/stack.js"),
    buildTask("bundles-components/stack.css", "dist/stack.css"),
    buildTask("bundles-components/terminal.js", "dist/terminal.js"),
    buildTask("bundles-components/terminal.css", "dist/terminal.css"),
    buildTask("bundles-components/texteditor.js", "dist/texteditor.js"),
    buildTask("bundles-components/texteditor.css", "dist/texteditor.css"),
    buildTask("bundles-components/texteditor.js", "dist/texteditor.js"),
    buildTask("bundles-components/texteditor.css", "dist/texteditor.css"),
    buildTask("bundles-components/timeline.js", "dist/timeline.js"),
    buildTask("bundles-components/timeline.css", "dist/timeline.css"),
    buildTask("bundles-components/schedule.js", "dist/schedule.js"),
    buildTask("bundles-components/schedule.css", "dist/schedule.css"),
];

await Promise.allSettled([...BaseBuildTasks, ...ComponentsBuildTasks].map(task => esbuild.build(task)));
