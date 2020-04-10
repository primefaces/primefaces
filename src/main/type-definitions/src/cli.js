//@ts-check

const { extname, join, resolve, isAbsolute } = require("path");

const { parseJs } = require("./acorn-util");
const { aggregateDocumentable } = require("./aggregate-documentable");
const { aggregateHandlerDefaults } = require("./aggregate-defaults");
const { aggregateHandlerWidgets } = require("./aggregate-widgets");
const { Paths } = require("./constants");
const { documentConstant } = require("./document-constant");
const { documentFunction } = require("./document-function");
const { documentObject } = require("./document-object");
const { createDefaultSeveritySettings, getSortedSeveritySettingKeys, isSeverityLevel, isSeveritySetting, Level } = require("./error-types");
const { createInclusionHandler } = require("./inclusion-handler");
const { isNotEmpty, splitLines } = require("./lang");
const { mkDirRecursive, readDir, readFileUtf8, writeFileUtf8 } = require("./lang-fs");
const { classifyDeclarationFile } = require("./ts-classify-declaration-file");
const { postprocessDeclarationFile } = require("./ts-postprocess");
const { createDefaultHooks } = require("./ts-postprocess-defaulthooks");
const { lintTsFile } = require("./ts-lint");
const { generateTypedocs } = require("./typedoc");

/** @type {CliArgs} */
const DefaultCliArgs = {
    exclusionTags: ["ignore", "exclude", "hidden"],
    includeModules: [],
    inputDir: Paths.ComponentsMainDir,
    outputDir: Paths.TargetMainDir,
    outputFilename: "PrimeFaces.d.ts",
    moduleOutputFilename: "PrimeFaces-module.d.ts",
    severitySettings: {},
    skipEsLint: false,
    skipPostProcess: false,
    skipTypedocGenerate: false,
    typedocOutputDir: "",
    verbose: false,
};

/**
 * @return {Promise<Set<string>>} List of files to be excluded from the type
 * declaration file. Mainly third-party libraries we do not want to document.
 */
async function readBlacklist() {
    const blacklist = await readFileUtf8(Paths.BlacklistPath);
    const lines = splitLines(blacklist, {
        excludeEmptyLines: true,
        trimLines: true,
    });
    /** @type {string[]} */
    const list = [];
    for (const line of lines) {
        if (!line.startsWith("#")) {
            list.push(line);
        }
    }
    return new Set(list);
}

/**
 * Walks the file systems and finds  a list of all PrimeFaces components and the associated JavaScript files.
 * @param {string} componentPath Path to the component directory. Each directory for a component must be a direct
 * child of this directory.
 * @return {AsyncIterable<Component>} List of widgets.
 */
async function* listComponents(componentPath) {
    const blacklist = await readBlacklist();
    const componentDirs = await readDir(componentPath);
    for (const componentDir of componentDirs.filter(x => x.isDirectory())) {
        const componentFiles = await readDir(join(componentPath, componentDir.name));
        yield {
            name: componentDir.name,
            path: join(componentPath, componentDir.name),
            typedefFiles: componentFiles
                .filter(x => x.isFile())
                .filter(x => x.name.endsWith(".d.ts"))
                .map(x => x.name),
            files: componentFiles
                .filter(x => x.isFile())
                .filter(x => extname(x.name) === ".js")
                .filter(x => !blacklist.has(`${componentDir.name}/${x.name}`))
                .map(x => x.name),
        }
    }
}

/**
 * Simple parser for CLI arguments. Throws an error in case an unknown option is passed.
 * @return {CliArgs}
 */
function parseCliArgs() {
    const result = Object.assign({}, DefaultCliArgs);
    const stack = process.argv.slice(2).reverse();
    while (stack.length > 0) {
        const arg = stack.pop();
        if (arg) {
            switch (arg.toLowerCase()) {
                case "-h":
                case "--help":
                    console.log(`Usage: ${process.argv[0]} ${process.argv[1]} -- --outputDir <doc-out-dir>`)
                    console.log(`Options:`);
                    console.log(` -i`);
                    console.log(` --inputdir`);
                    console.log(`     Specifies the main input directory with the individual JavaScript components.`);
                    console.log(`     Defaults to '../../resources/META-INF/resources/primefaces'`);
                    console.log(` -o`);
                    console.log(` --outputdir`);
                    console.log(`     Specifies the output directory for the generated *.d.ts declaration file.`);
                    console.log(`     Defaults to ../../../../target/generated-resources/type-definitions`);
                    console.log(` -f`);
                    console.log(` --outputfilename`);
                    console.log(`     Specifies the filename for the generated *.d.ts declaration file.`);
                    console.log(`     Default to 'PrimeFaces.d.ts'`);
                    console.log(` -n`);
                    console.log(` --moduleoutputfilename`);
                    console.log(`     Specifies the filename for the generated *.module.d.ts declaration file.`);
                    console.log(`     Default to 'PrimeFaces-module.d.ts'`);
                    console.log(` -g`);
                    console.log(` --skiptypedocgenerate`);
                    console.log(`     If set to true, the typedoc documentation HTML files are not generated.`);
                    console.log(` -s`);
                    console.log(` --skipeslint`);
                    console.log(`     If set to true, the linter is not run on the generated *.d.ts declaration file.`);
                    console.log(` -t`);
                    console.log(` --typedocoutputdir`);
                    console.log(`     Specifies the directory for the generated typedoc HTML files`);
                    console.log(` -v`);
                    console.log(` --verbose`);
                    console.log(`     Enables more verbose output`);
                    console.log(` -e`);
                    console.log(` --exclusiontags`);
                    console.log(`     Comma separated list of tags in a doc comment that cause a constant, function, or class to be skipped and not documented`);
                    console.log(`     Default to 'internal,ignore,exclude'`);
                    console.log(` -m`);
                    console.log(` --includemodules`);
                    console.log(`     Comma separated list of node modules to include in the generated typedoc files`);
                    console.log(`     Default to no additional modules`);
                    console.log(` -l`);
                    console.log(` --level`);
                    console.log(`     Specifies how severe a type of error is considered, in the format '<error-type>=<level>'. May be specified multiple times.`);
                    console.log(`     Available severity levels are 'error', 'fatal', ignore', 'info', 'warning'. See below for the available error types.`);
                    console.log(` --ignore`);
                    console.log(`     Comma separated list of errors that are ignored and do not make the build fail`);
                    console.log(`     Default to none`);
                    console.log(`     Available error types are: ${getSortedSeveritySettingKeys()}`)
                    process.exit(0);
                case "-i":
                case "--inputdir":
                    result.inputDir = stack.pop() || "";
                    break;
                case "-o":
                case "--outputdir":
                    result.outputDir = stack.pop() || "";
                    break;
                case "-f":
                case "--outputfilename":
                    result.outputFilename = stack.pop() || "";
                    break;
                case "-n":
                case "--moduleoutputfilename":
                    result.moduleOutputFilename = stack.pop() || "";
                    break;
                case "-g":
                case "--skiptypedocgenerate":
                    result.skipTypedocGenerate = stack.pop() === "true";
                    break;
                case "-s":
                case "--skipeslint":
                    result.skipEsLint = stack.pop() === "true";
                    break;
                case "--skippostprocess":
                    result.skipPostProcess = stack.pop() === "true";
                    break;
                case "-t":
                case "--typedocoutputdir":
                    result.typedocOutputDir = stack.pop() || "";
                    break;
                case "-v":
                case "--verbose":
                    result.verbose = true;
                    break;
                case "-e":
                case "--exclusiontags":
                    result.exclusionTags = (stack.pop() || "")
                        .split(",")
                        .filter(isNotEmpty)
                        .map(x => x.trim())
                        .map(x => x.toLowerCase());
                    break;
                case "-m":
                case "--includemodules":
                    result.includeModules = (stack.pop() || "")
                        .split(",")
                        .filter(isNotEmpty)
                        .map(x => x.trim())
                        .map(x => x.toLowerCase());
                    break;
                case "--ignore": {
                    const settingsToIgnore = (stack.pop() || "")
                        .split(",")
                        .map(x => x.trim())
                        .filter(isSeveritySetting);
                    for (const settingToIgnore of settingsToIgnore) {
                        if (settingToIgnore in result.severitySettings) {
                            throw new Error("Level for severity setting '" + settingToIgnore + "' was specified twice");
                        }
                        result.severitySettings[settingToIgnore] = Level.ignore;
                    }
                    break;
                }
                case "-l":
                case "--level": {
                    const [setting, level] = (stack.pop() || "").split("=");
                    if (!isSeveritySetting(setting)) {
                        throw new Error("Invalid severity settings '" + setting + "'");
                    }
                    if (!isSeverityLevel(level)) {
                        throw new Error("Invalid severity level '" + level + "'");
                    }
                    else if (setting in result.severitySettings) {
                        throw new Error("Level for severity setting '" + setting + "' was specified twice");
                    }
                    else {
                        result.severitySettings[setting] = level;
                    }
                    break;
                }
                case "--":
                    break;
                default:
                    throw new Error("Unknown CLI argument encountered: '" + arg + "'");
            }
        }
    }
    result.outputDir = resolve(Paths.ProjectRootDir, result.outputDir);
    if (result.typedocOutputDir) {
        isAbsolute
        result.typedocOutputDir = resolve(Paths.ProjectRootDir, result.typedocOutputDir);
    }
    console.log("Parsed CLI arguments:", JSON.stringify(result));
    return result;
}

/**
 * @param {Component} component 
 * @return {AsyncIterableIterator<{source: string, type: DeclarationFileType}>}
 */
async function* readTypedefFiles(component) {
    for (const file of component.typedefFiles.sort()) {
        const sourceFile = join(component.path, file);
        const source = await readFileUtf8(sourceFile);
        const type = classifyDeclarationFile(source);
        yield { source, type };
    }
}

/**
 * @param {TypeDeclarationBundleContent} bundle 
 * @param {CliArgs} cliArgs
 * @return {Promise<TypeDeclarationBundleFiles>} The path to the written file.
 */
async function writeOutput(bundle, cliArgs) {
    await mkDirRecursive(cliArgs.outputDir);

    const outputFileAmbient = resolve(join(cliArgs.outputDir, cliArgs.outputFilename));
    const outputFileModule = resolve(join(cliArgs.outputDir, cliArgs.moduleOutputFilename));

    console.log("Writing ambient output file to ", outputFileAmbient);
    console.log("Writing module output file to ", outputFileModule);

    await Promise.all([
        writeFileUtf8(outputFileAmbient, { data: bundle.ambient }),
        writeFileUtf8(outputFileModule, { data: bundle.module })
    ]);

    return { ambient: outputFileAmbient, module: outputFileModule };
}

/**
 * @param {CliArgs} cliArgs 
 * @param  {...any} message 
 */
function logVerbose(cliArgs, ...message) {
    if (cliArgs.verbose) {
        console.log(...message);
    }
}

/**
 * Main method that call the pipeline to create the type definitions file
 * @param {CliArgs} cliArgs
 */
async function main(cliArgs) {
    const inclusionHandler = createInclusionHandler(cliArgs.exclusionTags);
    const severitySettings = createDefaultSeveritySettings(cliArgs.severitySettings);

    /** @type {TypeDeclarationBundleContent} */
    const bundle = {
        ambient: [],
        module: [],
    };

    // Add core declaration files
    bundle.ambient.push(...await Promise.all([
        readFileUtf8(Paths.CoreDeclarationFile),
        readFileUtf8(Paths.CoreJsfDeclarationFile),
    ]));

    // Scan the base directory and list the directories with the JavaScript source files to process
    for await (const component of listComponents(cliArgs.inputDir)) {
        console.log("Processing component directory", component.path);

        // Include all *.d.ts file found in the directory
        for await (const typedefFile of readTypedefFiles(component)) {
            bundle[typedefFile.type].push(typedefFile.source);
        }

        // Parse the JavaScript source files for symbols to document
        for await (const program of parseJs(component.files, component.path)) {
            logVerbose(cliArgs, "  -> Processing program ", program.node.loc ? program.node.loc.source : "<unknown>");

            // Scan source code and find all objects, functions, and constants that are to be documented
            const documentable = aggregateDocumentable(program, inclusionHandler, severitySettings, [
                aggregateHandlerWidgets,
                aggregateHandlerDefaults,
            ]);

            // Document constants (const x = ...)
            for (const constantDefinition of documentable.constants) {
                logVerbose(cliArgs, "    -> Processing constant defintion", constantDefinition.name);
                const moreLines = documentConstant(constantDefinition, severitySettings);
                bundle.ambient.push(...moreLines);
                bundle.ambient.push("");
            }

            // Document functions (function foo() {...})
            for (const fnDefinition of documentable.functions) {
                logVerbose(cliArgs, "    -> Processing function defintion", fnDefinition.name);
                const moreLines = documentFunction(fnDefinition, severitySettings);
                bundle.ambient.push(...moreLines);
                bundle.ambient.push("");
            }

            // Document objects and types (class A { ... }, const x = { ... })
            for (const objectDefinition of documentable.objects) {
                logVerbose(cliArgs, "    -> Processing object defintion", objectDefinition.name);
                const moreLines = documentObject(objectDefinition, program, inclusionHandler, severitySettings);
                bundle.ambient.push(...moreLines);
                bundle.ambient.push("");
            }
        }
    }

    // Create the type declaration file
    const filePaths = await writeOutput(bundle, cliArgs);

    // Post process the type declaration file: fix formatting and run it through
    // the typescript parser to check whether all types can be resolved and are
    // consistent
    if (!cliArgs.skipPostProcess) {
        console.info("Running post-processing on generated type declarations file");
        const fileContents = await postprocessDeclarationFile(filePaths, severitySettings, createDefaultHooks(severitySettings, true));
        await Promise.all([
            writeFileUtf8(filePaths.ambient, { data: fileContents.ambient }),
            writeFileUtf8(filePaths.module, { data: fileContents.module }),
        ]);
        console.info("Post-processing step complete");
    }
    else {
        console.info("Skipping post-process pass");
    }

    // Run linter on the generated type declaration file, i.e. forbid Object or Array<...> types
    if (!cliArgs.skipEsLint) {
        console.log("Linting declaration file");
        await lintTsFile(filePaths);
    }
    else {
        console.info("Skipping linter pass");
    }

    // Generate the documentation based on the type declaration file
    if (!cliArgs.skipTypedocGenerate) {
        console.info("Generating typedocs");
        await generateTypedocs(filePaths, cliArgs);
    }
    else {
        console.info("Skipping typedoc generation");
    }
}

module.exports = {
    main,
    parseCliArgs,
};