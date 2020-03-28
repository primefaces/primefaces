//@ts-check

const { join } = require("path");
const typedoc = require("typedoc");

const { Paths } = require("./constants");
const { withTemporaryFileOnDisk } = require("./lang-fs");

/**
 * @param {CliArgs} cliArgs 
 * @return {string[]}
 */
function createExcludes(cliArgs) {
    if (cliArgs.exclusionTags.length === 0) {
        return ["node_modules/**/*"];
    }
    else {
        return [
            `node_modules/!(@types|${cliArgs.includeModules.join("|")})/**/*`,
            `node_modules/@types/!(${cliArgs.includeModules.join("|")})/**/*`
        ];
    }
}

/**
 * Generates typedocs for the given `*.d.ts` file.
 * @param {string} sourceFileName 
 * @param {CliArgs} cliArgs
 */
function generateTypedocs(sourceFileName, cliArgs) {
    const targetDir = cliArgs.typedocOutputDir || join(cliArgs.outputDir, "jsdocs");
    const app = new typedoc.Application();
    app.bootstrap({
        mode: "file",
        name: "PrimeFaces JavaScript API Docs",
        plugin: ["typedoc-neo-theme"],
        tsconfig: Paths.TsConfigPath,
        readme: Paths.JsdocReadmePath,
        theme: "./node_modules/typedoc-neo-theme/bin/default",
        exclude: createExcludes(cliArgs),
        ignoreCompilerErrors: true,
        includeDeclarations: true,
        disableSources: true,
        // @ts-ignore
        links: [
            {
                label: "Homepage",
                url: "https://www.primefaces.org/#",
            },
            {
                label: "Github",
                url: "https://github.com/primefaces/primefaces"
            },
            {
                label: "User guide",
                url: "https://primefaces.github.io/primefaces"
            },
        ],
        outline: [{
            "PrimeFaces": {
                "PrimeFaces object": "primefaces",
                "AJAX module": "primefaces.ajax",
                "Dialog module": "primefaces.dialog",
                "Expression module": "primefaces.expressions",
                "Resources module": "primefaces.resources",
                "Utils module": "primefaces.utils",
                "Widget module": "primefaces.widget",
            },
            "JQuery": {
                "Plugins": "jquery",
                "Statics": "jquerystatic",
                "Event Map": "interfaces/jquery.typetotriggeredeventmap",
            },
            "Other 3rd party libraries": {
                "autosize": "autosize",
                "chart.js": "classes/chart",
                "ContentFlow": "classes/contentflow",
                "FullCalendar": "__fullcalendar_core_calendar_",
                "Google Maps": "google.maps",
                "jsplumb": "jsplumb",
                "JuxtaposeJS": "juxtapose",
                "moment.js": "moment",
                "Quill": "classes/quill",
                "Raphaël": "raphael",
                "Timeline/Graph2D": "classes/timeline",
                "WebcamJS": "webcam",
            }
        }]

    });
    withTemporaryFileOnDisk(sourceFileName, Paths.NpmVirtualDeclarationFile, file => {
        app.generateDocs(app.expandInputFiles([
            file,
            Paths.NpmTypesDir,
        ]), targetDir);
    });
}

module.exports = {
    generateTypedocs,
}