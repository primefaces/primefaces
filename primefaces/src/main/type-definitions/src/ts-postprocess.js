//@ts-check

const ts = require("typescript");
const { asyncForEach, asyncFlatMap, asyncReduce, splitLines, withIndex } = require("./lang");
const { createEmptyHooks } = require("./ts-postprocess-defaulthooks");
const { createCommentHandler } = require("./ts-commenthandler");
const { fixupNodes: fixupParents } = require("./ts-utils");
const { promises: fs } = require("fs");
const { formatError } = require("./error");

/**
 * @template {(...args: any[]) => any} THook
 * @param {THook | undefined} hook
 * @param {ResultFactory<ReturnType<THook>>} defaultValue
 * @param {Parameters<THook>} args
 * @return {Promise<ResultType<THook> | undefined>}
 */
async function invokeHook(hook, defaultValue = undefined, ...args) {
    let result = undefined;
    if (hook) {
        result = await hook(...args);
    }
    if (result === undefined && defaultValue !== undefined) {
        result = await defaultValue();
    }
    return result;
}

/**
 * @template {(...args: any[]) => any} THook
 * @param {THook | undefined} hook
 * @param {ResultFactory<ReturnType<THook>>} defaultValue
 * @param {Parameters<THook>} args
 * @return {Promise<ResultType<THook>>}
 */
async function invokeHookToResult(hook, defaultValue = undefined, ...args) {
    const result = await invokeHook(hook, defaultValue, ...args);
    if (result === undefined) {
        throw new Error("Hook did not return a result: " + hook);
    }
    return result;
}

/**
 * @param {TsPostProcessingHooks} postProcessorsHooks
 * @return {Promise<import("typescript").CompilerOptions>}
 */
async function createCompilerOptions(postProcessorsHooks) {
    const options = await invokeHookToResult(postProcessorsHooks.compilerOptions.createCompilerOptions);
    const modifiedOptions = await asyncReduce(
        postProcessorsHooks.compilerOptions.modifyCompilerOptions,
        (opts, hook) => invokeHook(hook, undefined, opts),
        options
    );
    return modifiedOptions;
}

/**
 * @param {TypeDeclarationBundleFiles} declarationFiles 
 * @param {TsPostProcessingHooks} postProcessorsHooks
 * @return {Promise<import("typescript").Program>}
 */
async function createProgram(declarationFiles, postProcessorsHooks) {
    const options = await createCompilerOptions(postProcessorsHooks);
    const host = await invokeHookToResult(postProcessorsHooks.compilerHost.createCompilerHost, undefined, options, declarationFiles);
    return ts.createProgram({
        options,
        rootNames: [declarationFiles.ambient, declarationFiles.module],
        host: host,
    });
}

/**
 * @param {import("typescript").Program} program
 * @param {TypeDeclarationBundleSourceFiles} sourceFiles
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {TsPostProcessingHooks} postProcessorsHooks
 * @return {Promise<Error | undefined>}
 */
async function validateProgram(program, sourceFiles, docCommentAccessor, postProcessorsHooks) {
    const validationResult = await asyncFlatMap(
        postProcessorsHooks.validateProgram.validate,
        hook => invokeHook(hook, undefined, program, [sourceFiles.ambient, sourceFiles.module], docCommentAccessor),
    );
    /** @type {TsGroupedValidationMessages} */
    const validationMessages = {
        errors: validationResult.filter(x => x.severity === "error"),
        messages: validationResult.filter(x => x.severity === "message"),
        suggestions: validationResult.filter(x => x.severity === "suggestion"),
        warnings: validationResult.filter(x => x.severity === "warning"),
    };
    const errors = await asyncFlatMap(
        postProcessorsHooks.validateProgram.report,
        hook => invokeHook(hook, undefined, validationMessages),
    );
    if (errors.length > 0) {
        return new Error(`Program failed to validate:\n${errors.map(formatError).join("\n\n")}`);
    }
    else {
        return undefined;
    }
}

/**
 * Makes sure JSDoc block comments begin on a new line.
 * @param {string} sourceCode
 * @return {string}
 */
function fixupInlineBlockComments(sourceCode) {
    const lines = splitLines(sourceCode);
    /** @type {string[]} */
    const result = [];
    for (const [line, i] of withIndex(lines)) {
        const trimmed = line.trimEnd();
        const indexSlashStarStar = trimmed.lastIndexOf("/**");
        if (indexSlashStarStar >= 0 && i < lines.length - 1) {
            const nextLine = lines[i + 1] ?? "";
            const indexStar = nextLine.indexOf("*");
            const lineBeforeComment = trimmed.slice(0, indexSlashStarStar).trimRight();
            if (indexStar >= 0 && lineBeforeComment.length > 0 && nextLine.slice(0, indexStar).trim().length === 0) {
                const commentBeginLine = " ".repeat(indexStar - 1) + "/**";
                result.push(lineBeforeComment);
                result.push(commentBeginLine);
            }
            else {
                result.push(trimmed);
            }
        }
        else {
            result.push(trimmed);
        }
    }
    return result.join("\n");
}

/**
 * @param {TypeDeclarationBundleSourceFiles} sourceFiles 
 * @param {TsPostProcessingHooks} postProcessorsHooks
 * @return {TypeDeclarationBundleContent}
 */
function stringifySourceFile(sourceFiles, postProcessorsHooks) {
    const printer = ts.createPrinter(
        {
            newLine: ts.NewLineKind.LineFeed,
        },
        {
            onEmitNode(hint, node, callback) {
                if (node) {
                    ts.setEmitFlags(node, ts.EmitFlags.NoComments);
                }
                callback(hint, node);
            },
            substituteNode(hint, node) {
                if (node && postProcessorsHooks.emit.transform) {
                    return postProcessorsHooks.emit.transform.reduce((sum, hook) => hook(sum), node);
                }
                else {
                    return node;
                }
            }
        }
    );
    const sourceCodeAmbient = printer.printFile(sourceFiles.ambient);
    const sourceCodeModule = printer.printFile(sourceFiles.module);
    return {
        ambient: [fixupInlineBlockComments(sourceCodeAmbient)],
        module: [fixupInlineBlockComments(sourceCodeModule)],
    };
}

/**
 * @param {TypeDeclarationBundleFiles} filePaths 
 * @param {import("typescript").Program} program 
 * @return {TypeDeclarationBundleSourceFiles}
 */
function getSourceFile(filePaths, program) {
    const sourceFileAmbient = program.getSourceFile(filePaths.ambient);
    const sourceFileModule = program.getSourceFile(filePaths.module);
    if (sourceFileAmbient === undefined) {
        throw new Error(`Source file ${filePaths.ambient} not found in program`);
    }
    if (sourceFileModule === undefined) {
        throw new Error(`Source file ${filePaths.module} not found in program`);
    }
    return { ambient: sourceFileAmbient, module: sourceFileModule };
}

/**
 * Performs post-processing of  a `*.d.ts` file, such as whether it is valid.
 * @param {TsPostProcessingHooks} resolvedHooks Path of the declaration file to post-process.
 * @param {SeveritySettingsConfig} severitySettings
 * @param {import("typescript").Program} program
 * @param {TypeDeclarationBundleSourceFiles} sourceFiles
 * @param {TsDocCommentHandler} commentHandler
 * @return {Promise<void>} Content of the post-processed file.
 */
async function processFixup(resolvedHooks, severitySettings, program, sourceFiles, commentHandler) {
    await asyncForEach(
        resolvedHooks.process.fixupAst,
        hook => invokeHook(
            hook,
            undefined,
            program,
            [sourceFiles.ambient, sourceFiles.module],
            commentHandler,
            severitySettings
        )
    );
}

/**
 * Performs post-processing of  a `*.d.ts` file, such as whether it is valid.
 * @param {TsPostProcessingHooks} resolvedHooks Path of the declaration file to post-process.
 * @param {SeveritySettingsConfig} severitySettings
 * @param {import("typescript").Program} program
 * @param {TypeDeclarationBundleSourceFiles} sourceFiles
 * @param {TsDocCommentHandler} commentHandler
 * @return {Promise<void>} Content of the post-processed file.
 */
async function processProcessAst(resolvedHooks, severitySettings, program, sourceFiles, commentHandler) {
    await asyncForEach(
        resolvedHooks.process.processAst,
        hook => invokeHook(
            hook,
            undefined,
            program,
            [sourceFiles.ambient, sourceFiles.module],
            commentHandler,
            severitySettings
        )
    );
}

/**
 * @param {TypeDeclarationBundleFiles} filePaths Path of the declaration file to post-process.
 * @param {TsPostProcessingHooks} resolvedHooks
 * @param {SeveritySettingsConfig} severitySettings
 * @return {Promise<{commentHandler: TsDocCommentHandler, program: import("typescript").Program, sourceFiles: TypeDeclarationBundleSourceFiles}>}
 */
async function readProgram(filePaths, resolvedHooks, severitySettings) {
    const program = await createProgram(filePaths, resolvedHooks);
    const sourceFiles = getSourceFile(filePaths, program);
    // Prepare handler for modifying doc comment
    fixupParents(program.getTypeChecker(), sourceFiles.ambient);
    fixupParents(program.getTypeChecker(), sourceFiles.module);
    const commentHandler = createCommentHandler(sourceFiles, severitySettings);
    return { commentHandler, program, sourceFiles };
}

/**
 * @param {import("typescript").Program} program
 * @param {TsPostProcessingHooks} resolvedHooks
 * @param {TypeDeclarationBundleSourceFiles} sourceFiles
 * @param {TsDocCommentHandler} commentHandler
 */
async function runValidation(program, sourceFiles, commentHandler, resolvedHooks) {
    const error = await validateProgram(program, sourceFiles, commentHandler, resolvedHooks);
    if (error) {
        throw error;
    }
}

/**
 * @param {TsPostProcessingHooks} resolvedHooks
 * @param {TypeDeclarationBundleSourceFiles} sourceFiles
 * @param {TsDocCommentHandler} commentHandler
 */
async function convertProgramToString(resolvedHooks, sourceFiles, commentHandler) {
    // Write declaration file back
    commentHandler.applyToSourceFile(sourceFiles.ambient);
    commentHandler.applyToSourceFile(sourceFiles.module);
    return stringifySourceFile(sourceFiles, resolvedHooks);
}

/**
 * @param {TypeDeclarationBundleFiles} filePaths Path of the declaration file to post-process.
 * @param {TypeDeclarationBundleContent} processed
 */
async function writeProgramToFileSystem(filePaths, processed) {
    await fs.writeFile(filePaths.ambient, processed.ambient, { encoding: "utf-8" });
    await fs.writeFile(filePaths.module, processed.module, { encoding: "utf-8" });
}

/**
 * @param {TypeDeclarationBundleFiles} filePaths Path of the declaration file to read.
 * @return {Promise<TypeDeclarationBundleRawContent>} filePaths Path of the declaration file.
 */
async function readFiles(filePaths) {
    return {
        ambient: await fs.readFile(filePaths.ambient, { encoding: "utf-8" }),
        module: await fs.readFile(filePaths.module, { encoding: "utf-8" }),
    };
}

/**
 * @param {TypeDeclarationBundleFiles} filePaths Path of the declaration file to write.
 * @param {TypeDeclarationBundleRawContent} files The content to write.
 */
async function writeFiles(filePaths, files) {
    await fs.writeFile(filePaths.ambient, files.ambient, { encoding: "utf-8" });
    await fs.writeFile(filePaths.module, files.module, { encoding: "utf-8" });
}

/**
 * @param {TypeDeclarationBundleFiles} input Path of the declaration file to post-process.
 * @param {TypeDeclarationBundleFiles} output Path of the declaration file to post-process.
 * @param {SeveritySettingsConfig} severitySettings
 * @param {TsPostProcessingHooks} resolvedHooks
 * @return {Promise<TypeDeclarationBundleContent>} Content of the post-processed file.
 */
async function postProcessPass1(input, output, severitySettings, resolvedHooks) {
    const { commentHandler, program, sourceFiles } = await readProgram(input, resolvedHooks, severitySettings);
    // Fixup AST
    await processFixup(resolvedHooks, severitySettings, program, sourceFiles, commentHandler);
    // Write declaration file back
    const processed = await convertProgramToString(resolvedHooks, sourceFiles, commentHandler);
    await writeProgramToFileSystem(output, processed);
    return processed;
}

/**
 * @param {TypeDeclarationBundleFiles} input Path of the declaration file to post-process.
 * @param {TypeDeclarationBundleFiles} output Path of the declaration file to post-process.
 * @param {SeveritySettingsConfig} severitySettings
 * @param {TsPostProcessingHooks} resolvedHooks
 * @return {Promise<TypeDeclarationBundleContent>} Content of the post-processed file.
 */
async function postProcessPass2(input, output, severitySettings, resolvedHooks) {
    const { commentHandler, program, sourceFiles } = await readProgram(input, resolvedHooks, severitySettings);
    // Validate program
    await runValidation(program, sourceFiles, commentHandler, resolvedHooks);
    // Write declaration file back
    const processed = await convertProgramToString(resolvedHooks, sourceFiles, commentHandler);
    await writeProgramToFileSystem(output, processed);
    return processed;
}

/**
 * @param {TypeDeclarationBundleFiles} input Path of the declaration file to post-process.
 * @param {TypeDeclarationBundleFiles} output Path of the declaration file to post-process.
 * @param {SeveritySettingsConfig} severitySettings
 * @param {TsPostProcessingHooks} resolvedHooks
 * @return {Promise<TypeDeclarationBundleContent>} Content of the post-processed file.
 */
async function postProcessPass3(input, output, severitySettings, resolvedHooks) {
    const { commentHandler, program, sourceFiles } = await readProgram(input, resolvedHooks, severitySettings);
    // Perform other processing on AST
    await processProcessAst(resolvedHooks, severitySettings, program, sourceFiles, commentHandler);
    // Write declaration file back
    const processed = await convertProgramToString(resolvedHooks, sourceFiles, commentHandler);
    await writeProgramToFileSystem(output, processed);
    return processed;
}

/**
 * Performs post-processing of  a `*.d.ts` file, such as whether it is valid.
 * @param {TypeDeclarationBundleFiles} filePaths Path of the declaration file to post-process.
 * @param {SeveritySettingsConfig} severitySettings
 * @param {Partial<TsPostProcessingHooks>} postProcessorsHooks
 * @return {Promise<TypeDeclarationBundleContent>} Content of the post-processed file.
 */
async function postprocessDeclarationFile(filePaths, severitySettings, postProcessorsHooks = {}) {
    // Read compiler options and parse program
    const resolvedHooks = Object.assign(createEmptyHooks(), postProcessorsHooks);

    const original = await readFiles(filePaths);

    try {
        console.log("Running pass 1...");
        await postProcessPass1(filePaths, filePaths, severitySettings, resolvedHooks);
        console.log("Running pass 2...");
        await postProcessPass2(filePaths, filePaths, severitySettings, resolvedHooks);
        console.log("Running pass 3...");
        const result = await postProcessPass3(filePaths, filePaths, severitySettings, resolvedHooks);
        return result;
    }
    finally {
        await writeFiles(filePaths, original);
    }
}

module.exports = {
    postprocessDeclarationFile,
};