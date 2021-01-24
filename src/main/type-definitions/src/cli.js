//@ts-check

const { dirname, extname } = require("path");
const { match } = require("minimatch");

const { parseJs } = require("./acorn-util");
const { aggregateDocumentable } = require("./aggregate-documentable");
const { aggregateHandlerDefaults } = require("./aggregate-defaults");
const { aggregateHandlerWidgets } = require("./aggregate-widgets");
const { Names, Paths } = require("./constants");
const { documentConstant } = require("./document-constant");
const { documentFunction } = require("./document-function");
const { documentObject } = require("./document-object");
const { createDefaultSeveritySettings, getSortedSeveritySettingKeys, isSeverityLevel, isSeveritySetting, Level } = require("./error-types");
const { createInclusionHandler } = require("./inclusion-handler");
const { isJsonObject, isNotEmpty, parseJsonObject, splitLines, asyncMap, isNotUndefined, asyncFlatMap } = require("./lang");
const { isExistingFileOrDir, mkDirRecursive, readDir, readFileUtf8, resolvePath, writeFileUtf8, normalize, makeRelative, readDirRecursive } = require("./lang-fs");
const { classifyDeclarationFile } = require("./ts-classify-declaration-file");
const { postprocessDeclarationFile } = require("./ts-postprocess");
const { createDefaultHooks } = require("./ts-postprocess-defaulthooks");
const { lintTsFile } = require("./ts-lint");
const { generateTypedocs } = require("./typedoc");

/** @type {CliArgs} */
const DefaultCliArgs = {
    declarationOutputDir: Paths.TargetMainDir,
    exclusionTags: ["ignore", "exclude", "hidden"],
    additionalEntries: [],
    inputDir: Paths.ComponentsMainDir,
    outputFilename: Names.PrimeFacesDeclaration,
    packageJson: Paths.PackageJsonFile,
    moduleOutputFilename: Names.PrimeFacesModuleDeclaration,
    rootDir: Paths.ProjectRootDir,
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
        const componentFiles = await readDir(resolvePath(componentPath, componentDir.name));
        yield {
            name: componentDir.name,
            path: resolvePath(componentPath, componentDir.name),
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
 * Resolves the paths in the args against the given root dir.
 * @param {string} root Root path against which to resolve. 
 * @param {Partial<CliArgs>} args Arguments to resolve. 
 * @return {Partial<CliArgs>} A copy of the args, with the paths resolved.
 */
function resolveArgsPaths(root, args) {
    if (root) {
        const copy = Object.assign({}, args);
        if (copy.inputDir) {
            copy.inputDir = resolvePath(root, copy.inputDir);
        }
        if (copy.declarationOutputDir) {
            copy.declarationOutputDir = resolvePath(root, copy.declarationOutputDir);
        }
        if (copy.packageJson) {
            copy.packageJson = resolvePath(root, copy.packageJson);
        }
        if (copy.typedocOutputDir) {
            copy.typedocOutputDir = resolvePath(root, copy.typedocOutputDir);
        }
        return copy;
    }
    else {
        return args;
    }
}

/**
 * @param {string} string 
 * @return {string}
 */
function trimSlashes(string) {
    return string.replace(/^[\/\\]+|[\/\\]+$/g, "");
}

/**
 * @param {JsonObject} packageJson 
 * @return {string[]}
 */
function resolveTypesVersions(packageJson) {
    const typesVersions = packageJson.typesVersions || {};
    const pattern = /(?<operator>\>=|\>|\<=|\<|=)(?<major>\d+)(?:\.(?<minor>\d+))?/;
    const [major, minor] = require("typescript").versionMajorMinor.split(".").map(parseInt);
    for (const version of Object.keys(typesVersions)) {
        const match = pattern.exec(version);
        if (match) {
            let versionMatch = false;
            const requestedMajor = parseInt(match.groups?.major ?? "0");
            const requestedMinor = parseInt(match.groups?.minor ?? "0");
            switch (match.groups?.operator) {
                case "<":
                    versionMatch = major < requestedMajor || (major === requestedMajor && minor < requestedMinor);
                    break;
                case "<=":
                    versionMatch = major <= requestedMajor || (major === requestedMajor && minor <= requestedMinor);
                    break;
                case ">":
                    versionMatch = major > requestedMajor || (major === requestedMajor && minor > requestedMinor);
                    break;
                case ">=":
                    versionMatch = major >= requestedMajor || (major === requestedMajor && minor >= requestedMinor);
                    break;
                case "=":
                    versionMatch = major === requestedMajor && minor === requestedMinor;
                    break;
            }
            if (versionMatch && typeof typesVersions === "object" && !Array.isArray(typesVersions)) {
                const specifiers = typesVersions[version];
                if (specifiers && typeof specifiers === "object" && !Array.isArray(specifiers)) {
                    const allFiles = specifiers["*"];
                    if (Array.isArray(allFiles)) {
                        return allFiles.map(String);
                    }
                }
                throw new Error("Unknown typeVersions entry: " + typesVersions);
            }
        }
    }
    return [];
}

/**
 * Given a node_modules package, finds the main typings file for that package.
 * This is either given by the `types` / `typings` field of the package json,
 * the `main` field when it refers to a `.ts` file, or `index.ts` / `index.d.ts`
 * by default.
 * @param {string} dependency 
 * @return {Promise<string[]>}
 */
async function readMainTypesFile(dependency) {
    const depPath = resolvePath(Paths.NpmNodeModulesDir, dependency);
    const packageJson = resolvePath(Paths.NpmNodeModulesDir, dependency, "package.json");
    const json = await readFileUtf8(packageJson);
    const parsed = parseJsonObject(json);
    const types = trimSlashes(String(parsed.types || parsed.typings || ""));
    const main = trimSlashes(String(parsed.main || ""));
    const typesVersion = resolveTypesVersions(parsed);
    /** @type {string[]} */
    let file;
    if (typesVersion.length > 0) {
        const allFiles = await readDirRecursive(depPath);
        const relFiles = allFiles.map(f => makeRelative(depPath, resolvePath(f.dir, f.entry.name)));
        const matching = typesVersion.flatMap(t => match(relFiles, t));
        file = [...new Set([...matching])].map(f => `node_modules/${dependency}/${f}`);
    }
    else if (types) {
        file = [normalize(`node_modules/${dependency}/${types}`, "/")];
    }
    else if (main && main.endsWith(".ts")) {
        file = [normalize(`node_modules/${dependency}/${main}`, "/")];
    }
    else {
        const decl = normalize(`node_modules/${dependency}/index.d.ts`, "/");
        const impl = normalize(`node_modules/${dependency}/index.ts`, "/")
        const stats = await isExistingFileOrDir(resolvePath(Paths.NpmRootDir, impl));
        file = [stats.exists ? impl : decl];
    }
    file = await asyncMap(file, async f => {
        if (!f.endsWith(".ts")) {
            const decl = f + ".d.ts";
            const impl = f + ".ts";
            const stats = await isExistingFileOrDir(resolvePath(Paths.NpmRootDir, impl));
            return stats.exists ? impl : decl;
        }
        return f;
    });
    file = await asyncMap(file, async f => {
        const stats = await isExistingFileOrDir(resolvePath(Paths.NpmRootDir, f));
        if (stats.exists && f.endsWith(".ts")) {
            return f;
        }
        return undefined;
    });

    file = file.filter(isNotUndefined);
    if (file.length === 0) {
        throw new Error("No typings found for package " + dependency);
    }
    return file;
}

/**
 * Reads the the arguments from the package JSON file, if it exists.
 * @param {string} packageJson 
 * @return {Promise<Partial<CliArgs>>}
 */
async function parsePackageArgs(packageJson) {
    /** @type {Partial<CliArgs>} */
    const result = {};
    const stats = await isExistingFileOrDir(packageJson);
    if (packageJson && stats.exists === true && stats.type === "file") {
        const json = await readFileUtf8(packageJson);
        const parsed = parseJsonObject(json);
        if (isJsonObject(parsed.jsdocs)) {
            delete parsed.jsdocs.packageJson;
            delete parsed.jsdocs.rootDir;
            Object.assign(result, parsed.jsdocs);
        }
        if (result.additionalEntries === undefined && isJsonObject(parsed.dependencies)) {
            const modules = await asyncFlatMap(Object.keys(parsed.dependencies), readMainTypesFile);
            if (modules.length > 0) {
                result.additionalEntries = [...new Set(modules)].sort();
            }
        }
    }
    return result;
}

/**
 * Simple parser for CLI arguments. Throws an error in case an unknown option is passed.
 * @return {Partial<CliArgs>}
 */
function parseCliArgs() {
    /** @type {Partial<CliArgs>} */
    const result = {};
    const stack = process.argv.slice(2).reverse();
    while (stack.length > 0) {
        const arg = stack.pop();
        if (arg) {
            switch (arg.toLowerCase()) {
                case "-h":
                case "--help":
                    console.log(`Usage: ${process.argv[0]} ${process.argv[1]} -- --packagejson <path-to-package-json>`)
                    console.log(`Options:`);
                    console.log(` -p`);
                    console.log(` --packageJson`);
                    console.log(`     Specifies the path to a package.json file to use. This package.json may contain a field "jsdocs" with these settings (camel case). Paths in the 'package.json' are relative to that file. Argument specified on the command line take precedence over arguments in the 'package.json'.`);
                    console.log(` -i`);
                    console.log(` --inputDir`);
                    console.log(`     Specifies the main input directory with the individual JavaScript components.`);
                    console.log(`     Defaults to '../../resources/META-INF/resources/primefaces'`);
                    console.log(` -o`);
                    console.log(` --declarationOutputDir`);
                    console.log(`     Specifies the output directory for the generated *.d.ts declaration file.`);
                    console.log(`     Defaults to ../../../../target/generated-resources/type-definitions`);
                    console.log(` -f`);
                    console.log(` --outputFileName`);
                    console.log(`     Specifies the filename for the generated *.d.ts declaration file.`);
                    console.log(`     Default to 'PrimeFaces.d.ts'`);
                    console.log(` -n`);
                    console.log(` --moduleOutputFilename`);
                    console.log(`     Specifies the filename for the generated *.module.d.ts declaration file.`);
                    console.log(`     Default to 'PrimeFaces-module.d.ts'`);
                    console.log(` -g`);
                    console.log(` --skipTypedocGenerate`);
                    console.log(`     If set to true, the typedoc documentation HTML files are not generated.`);
                    console.log(` -s`);
                    console.log(` --skipEsLint`);
                    console.log(`     If set to true, the linter is not run on the generated *.d.ts declaration file.`);
                    console.log(` -t`);
                    console.log(` --typedocOutputDir`);
                    console.log(`     Specifies the directory for the generated typedoc HTML files`);
                    console.log(` -v`);
                    console.log(` --verbose`);
                    console.log(`     Enables more verbose output`);
                    console.log(` -e`);
                    console.log(` --exclusionTags`);
                    console.log(`     Comma separated list of tags in a doc comment that cause a constant, function, or class to be skipped and not documented`);
                    console.log(`     Default to 'internal,ignore,exclude'`);
                    console.log(` -m`);
                    console.log(` --additionalEntries`);
                    console.log(`     Comma separated list of entry points to include in the generated typedoc files`);
                    console.log(`     Default to no additional entry points; or, if a 'package.json' is defined, the "dependencies" of that 'package.json'`);
                    console.log(` -l`);
                    console.log(` --level`);
                    console.log(`     Specifies how severe a type of error is considered, in the format '<error-type>=<level>'. May be specified multiple times.`);
                    console.log(`     Available severity levels are 'error', 'fatal', ignore', 'info', 'warning'. See below for the available error types.`);
                    console.log(` --ignore`);
                    console.log(`     Comma separated list of errors that are ignored and do not make the build fail`);
                    console.log(`     Default to none`);
                    console.log(``);
                    console.log(`     Available error types are: ${getSortedSeveritySettingKeys()}`)
                    console.log(``);
                    console.log(`The 'level' option is not available when specifying the options via a 'package.json' file. Instead, use "severitySettings". It must be an object with the key being the name of the severity setting and the value being the severity level, such as '{"tagMissingParam": "error"}'.`);
                    process.exit(0);
                case "-i":
                case "--inputdir":
                    result.inputDir = stack.pop() || "";
                    break;
                case "-o":
                case "--declarationoutputdir":
                    result.declarationOutputDir = stack.pop() || "";
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
                case "-p":
                case "--packagejson":
                    result.packageJson = stack.pop() || "";
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
                case "--additionalEntries":
                    result.additionalEntries = (stack.pop() || "")
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
                    result.severitySettings = result.severitySettings || {};
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
                    else if (result.severitySettings && setting in result.severitySettings) {
                        throw new Error("Level for severity setting '" + setting + "' was specified twice");
                    }
                    else {
                        result.severitySettings = result.severitySettings || {};
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
    return result;
}

/**
 * Reads the arguments from the package.json and CLI, merges them and return the result.
 * @return {Promise<CliArgs>}
 */
async function readArguments() {
    const cliArgs = parseCliArgs();

    const rootDir = cliArgs.rootDir !== undefined ? cliArgs.rootDir : DefaultCliArgs.rootDir;

    const resolvedCliArgs = resolveArgsPaths(rootDir, cliArgs);

    const packageJson = resolvedCliArgs.packageJson !== undefined ? resolvedCliArgs.packageJson : DefaultCliArgs.packageJson;

    const packageArgs = await parsePackageArgs(packageJson);

    const resolvedPackageArgs = resolveArgsPaths(packageJson ? dirname(packageJson) : "", packageArgs);

    const mergedArgs = Object.assign({}, DefaultCliArgs, resolvedPackageArgs, resolvedCliArgs);

    console.log("Root dir:", rootDir);
    console.log("Package JSON dir:", packageJson);
    //console.debug("Parsed CLI arguments:", JSON.stringify(resolvedCliArgs));
    //console.debug("Parsed package arguments:", JSON.stringify(resolvedPackageArgs));
    console.log("Final arguments:", JSON.stringify(mergedArgs));

    return mergedArgs;
}

/**
 * @param {Component} component 
 * @return {AsyncIterableIterator<{source: string, type: DeclarationFileType}>}
 */
async function* readTypedefFiles(component) {
    for (const file of component.typedefFiles.sort()) {
        const sourceFile = resolvePath(component.path, file);
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
    await mkDirRecursive(cliArgs.declarationOutputDir);

    const outputFileAmbient = resolvePath(cliArgs.declarationOutputDir, cliArgs.outputFilename);
    const outputFileModule = resolvePath(cliArgs.declarationOutputDir, cliArgs.moduleOutputFilename);

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
        console.log(`Processing component directory ${component.path}`);

        const count = {
            constants: 0,
            functions: 0,
            objects: 0,
            typedefs: 0,
        };

        // Include all *.d.ts file found in the directory
        for await (const typedefFile of readTypedefFiles(component)) {
            bundle[typedefFile.type].push(typedefFile.source);
            count.typedefs += 1;
        }

        // Parse the JavaScript source files for symbols to document
        for await (const program of parseJs(component.files, 2016, component.path)) {
            logVerbose(cliArgs, "  -> Processing program ", program.node.loc ? program.node.loc.source : "<unknown>");

            // Scan source code and find all objects, functions, and constants that are to be documented
            const documentable = aggregateDocumentable(program, inclusionHandler, severitySettings, [
                aggregateHandlerWidgets,
                aggregateHandlerDefaults,
            ]);

            count.constants += documentable.constants.length;
            count.functions += documentable.functions.length;
            count.objects += documentable.objects.length;

            // Document constants (const x = ...)
            for (const constantDefinition of documentable.constants) {
                logVerbose(cliArgs, "    -> Processing constant definition", constantDefinition.name);
                const moreLines = documentConstant(constantDefinition, severitySettings);
                bundle.ambient.push(...moreLines);
                bundle.ambient.push("");
            }

            // Document functions (function foo() {...})
            for (const fnDefinition of documentable.functions) {
                logVerbose(cliArgs, "    -> Processing function definition", fnDefinition.name);
                const moreLines = documentFunction(fnDefinition, severitySettings);
                bundle.ambient.push(...moreLines);
                bundle.ambient.push("");
            }

            // Document objects and types (class A { ... }, const x = { ... })
            for (const objectDefinition of documentable.objects) {
                logVerbose(cliArgs, "    -> Processing object definition", objectDefinition.name);
                const moreLines = documentObject(objectDefinition, program, inclusionHandler, severitySettings);
                bundle.ambient.push(...moreLines);
                bundle.ambient.push("");
            }
        }
        // Warn if directory contains any JavaScript source files but no documentation was found
        // Usually happens when you add a new 
        if (component.files.length > 0 || component.typedefFiles.length > 0) {
            if (count.objects === 0 && count.functions === 0 && count.constants === 0 && count.typedefs === 0) {
                console.error("  -> Did not find any documentation in this directory, did you just add a new components?");
                console.error("  -> Make sure to document it, see src/main/type-definitions/README.md for details");
                console.error("  -> Add an empty *.d.ts file in this directory to disable this error");
                throw new Error(`No documentation found in ${component.path}`);
            }
        }
        logVerbose(cliArgs, `  -> Found ${count.objects} object(s), ${count.functions} function(s), ${count.constants} constant(s), and ${count.typedefs} custom type declaration file(s)`);
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
    readArguments,
};