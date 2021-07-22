//@ts-check

const { diffTrimmedLines } = require("diff");
const { basename, join, resolve } = require("path");
const { tmpNameSync } = require("tmp");

const { Paths, Tags } = require("../src/constants");
const { createInclusionHandler } = require("../src/inclusion-handler");
const { asyncMap, isNotEmpty, isNotUndefined, splitLines } = require("../src/lang");
const { deleteFile, readDir, readFileUtf8, writeFileUtf8 } = require("../src/lang-fs");
const { makeStackLine } = require("../src/error");

/**
 * @param {(input: string, sourceName: string, sourceLocation: TypeDeclarationBundleFiles, inclusionHandler: InclusionHandler) => string[] | Promise<string[]>} processInput
 * @param {string} input
 * @param {string} sourceName
 * @param {TypeDeclarationBundleFiles} sourceLocation
 * @param {InclusionHandler} inclusionHandler
 * @return {Promise<{success: true, lines: string[]} | {success: false, error: Error}>}
 */
async function invokeTest(processInput, input, sourceName, sourceLocation, inclusionHandler) {
    try {
        const lines = await processInput(input, sourceName, sourceLocation, inclusionHandler);
        return {
            success: true,
            lines: lines,
        };
    }
    catch (e) {
        return {
            success: false,
            error: e instanceof Error ? e : new Error(String(e)),
        }
    }
}

/**
 * @param {Error} error
 * @param {string} errorFilename
 * @param {string} dir
 * @param {import("fs").Dirent} dirEntry
 * @return {Promise<string[]>}
 */
async function checkError(error, errorFilename, dir, dirEntry) {
    /** @type {string | undefined} */
    let fileContent;
    try {
        fileContent = await readFileUtf8(join(dir, dirEntry.name, errorFilename));
    }
    catch (e) {
        return [
            `Test threw an error, but file '${errorFilename}' with the expected error could not be read. Either add the expected error to this file or fix the code / test.`,
            e instanceof Error && e.stack ? e.stack : String(e),
        ];
    }
    const expected = splitLines(fileContent, {
        trimLines: true,
        excludeEmptyLines: true,
        removeTrailingLines: " ",
    });
    const actual = splitLines(error.stack || "", {
        trimLines: true,
        excludeEmptyLines: true,
        removeTrailingLines: " ",
    }).map(x => x.replace(/\\/g, "/"));
    if (expected.length === 0) {
        return [`Test threw an error, but file '${errorFilename}' with the expected error is empty. Either add the expected error to this file or fix the code / test.`];
    }
    if (expected.length > actual.length) {
        return ["Expected error contains more lines than actual error."];
    }
    let i = 0;
    let j = 0;
    const projectRootDir = Paths.ProjectRootDir.replace(/\\/g, "/");
    while (i < expected.length && j < actual.length) {
        const expectedLine = expected[i]
            .trim()
            .replace(/\$\{PROJECT_BASEDIR\}/g, projectRootDir);
        while (j < actual.length && actual[j].trim() !== expectedLine) {
            j += 1;
        }
        if (j >= actual.length) {
            return [
                `Expected error stack trace at line number ${i + 1} not found in actual error`,
                "-" + expectedLine,
            ];
        }
        i += 1;
    }
    return [];
}

/**
 * @param {string[]} actual 
 * @param {string} expectedFilename
 * @param {string} dir
 * @param {import("fs").Dirent} dirEntry
 * @return {Promise<string[]>}
 */
async function checkSuccess(actual, expectedFilename, dir, dirEntry) {
    let fileContent;
    try {
        fileContent = await readFileUtf8(join(dir, dirEntry.name, expectedFilename));
    }
    catch (e) {
        return [`Test did not throw an error, but file '${expectedFilename}' with the expected output could not be read. Either add the expected output this file or fix the code / test.`];
    }
    const expected = splitLines(fileContent);
    const diffs = diffTrimmedLines(expected.join("\n"), actual.join("\n"), {
        newlineIsToken: true,
        ignoreWhitespace: true,
    });
    return diffs
        .flatMap((diff, line) => {
            if (diff.added === true || diff.removed === true) {
                return splitLines(diff.value.trim()).map(x => `${diff.added ? "+" : "-"} ${x}`);
            }
            return [undefined];
        })
        .filter(isNotUndefined);
}
/**
 * @param {(input: string, sourceName: string, sourceLocation: TypeDeclarationBundleFiles, inclusionHandler: InclusionHandler) => string[] | Promise<string[]>} processInput
 * @param {{inputFilename: string, expectedFilename: string, errorFilename: string}} filenames 
 * @param {string} dir
 * @param {import("fs").Dirent} dirEntry
 * @param {InclusionHandler} inclusionHandler 
 * @param {boolean} verbose 
 * @return {Promise<string | undefined>}
 */
async function runTest(processInput, filenames, dir, dirEntry, inclusionHandler, verbose) {
    const resolvedSourceDir = resolve(join(dir, dirEntry.name));
    const resolvedSourceLocation = resolve(join(resolvedSourceDir, filenames.inputFilename));
    const moduleLocation = tmpNameSync({
        dir: basename(resolvedSourceDir),
        tmpdir: resolve(resolvedSourceDir, ".."),
        prefix: "module",
        postfix: ".js",
    });
    const files = {
        ambient: resolvedSourceLocation,
        module: moduleLocation,
    };
    try {
        await writeFileUtf8(moduleLocation, { data: "// dummy" });
        const input = await readFileUtf8(resolvedSourceLocation);
        const actual = await invokeTest(processInput, input, dirEntry.name, files, inclusionHandler);
        const errors = actual.success ?
            await checkSuccess(actual.lines, filenames.expectedFilename, dir, dirEntry) :
            await checkError(actual.error, filenames.errorFilename, dir, dirEntry);
        // Log errors
        if (errors.length > 0) {
            const message = `Failure in test for '${dirEntry.name}', expected differs from actual:\n\n${errors.join("\n")}\n`;
            const actualText = actual.success ? actual.lines.join("\n") : actual.error.stack;
            const withActual = `${message}\nActual is:\n\n${actualText}\n`;
            const stack = makeStackLine(dirEntry.name, resolvedSourceLocation, 1, 1);
            if (verbose) {
                console.warn(" -> failure");
            }
            return `${withActual}\n${stack}`;
        }
        else {
            return undefined;
        }
    }
    finally {
        await deleteFile(moduleLocation);
    }
}

/**
 * @param {string} dir
 * @param {{inputFilename: string, expectedFilename: string, errorFilename: string}} filenames
 * @param {(input: string, sourceName: string, sourceLocation: TypeDeclarationBundleFiles, inclusionHandler: InclusionHandler) => string[] | Promise<string[]>} processInput
 * @param {TestCliArgs} cliArgs
 */
async function main(dir, filenames, processInput, cliArgs) {
    const dirContent = await readDir(dir);
    const inclusionHandler = createInclusionHandler([Tags.Internal, Tags.Exclude, Tags.Ignore]);
    const subTests = cliArgs.items.map(x => x.trim()).filter(isNotEmpty);
    const testItems = dirContent
        .filter(x => x.isDirectory)
        .filter(x => subTests.length === 0 || subTests.includes(x.name));
    if (testItems.length === 0) {
        throw new Error("No tests found");
    }
    const failures = await asyncMap(testItems, async dirEntry => {
        if (cliArgs.verbose) {
            console.log("Testing item", dirEntry.name, "...");
        }
        return runTest(processInput, filenames, dir, dirEntry, inclusionHandler, cliArgs.verbose);
    });
    if (failures.length > 0) {
        throw new Error(failures.join("\n"));
    }
}

module.exports = {
    main,
};