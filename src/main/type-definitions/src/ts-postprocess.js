//@ts-check

const ts = require("typescript");
const { asyncForEach, asyncFlatMap, asyncReduce, splitLines } = require("./lang");
const { createEmptyHooks } = require("./ts-postprocess-defaulthooks");
const { createCommentHandler } = require("./ts-commenthandler");
const { fixupNodes: fixupParents } = require("./ts-utils");

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
 * @param {string} declarationFile 
 * @param {TsPostProcessingHooks} postProcessorsHooks
 * @return {Promise<import("typescript").Program>}
 */
async function createProgram(declarationFile, postProcessorsHooks) {
    const options = await createCompilerOptions(postProcessorsHooks);
    const host = await invokeHookToResult(postProcessorsHooks.compilerHost.createCompilerHost, undefined, options, [declarationFile]);
    return ts.createProgram({
        options,
        rootNames: [declarationFile],
        host: host,
    });
}

/**
 * @param {import("typescript").Program} program
 * @param {import("typescript").SourceFile} sourceFile
 * @param {TsDocCommentAccessor} docCommentAccessor
 * @param {TsPostProcessingHooks} postProcessorsHooks
 * @return {Promise<Error | undefined>}
 */
async function validateProgram(program, sourceFile, docCommentAccessor, postProcessorsHooks) {
    const validationResult = await asyncFlatMap(
        postProcessorsHooks.validateProgram.validate,
        hook => invokeHook(hook, undefined, program, [sourceFile], docCommentAccessor),
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
        return new Error("Program failed to validate:\n" + errors.map(error => error.stack).join("\n\n"));
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
    for (let i = 0; i < lines.length; ++i) {
        const line = lines[i].trimEnd();
        const indexSlashStarStar = line.lastIndexOf("/**");
        if (indexSlashStarStar >= 0 && i < lines.length - 1) {
            const nextLine = lines[i + 1];
            const indexStar = nextLine.indexOf("*");
            const lineBeforeComment = line.substr(0, indexSlashStarStar).trimRight();
            if (indexStar >= 0 && lineBeforeComment.length > 0 && nextLine.substr(0, indexStar).trim().length === 0) {
                const commentBeginLine = " ".repeat(indexStar - 1) + "/**";
                result.push(lineBeforeComment);
                result.push(commentBeginLine);
            }
            else {
                result.push(line);
            }
        }
        else {
            result.push(line);
        }
    }
    return result.join("\n");
}

/**
 * @param {import("typescript").SourceFile} sourceFile 
 * @param {TsPostProcessingHooks} postProcessorsHooks
 * @return {string}
 */
function stringifySourceFile(sourceFile, postProcessorsHooks) {
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
    const sourceCode = printer.printFile(sourceFile);
    return fixupInlineBlockComments(sourceCode);
}

/**
 * @param {string} filePath 
 * @param {import("typescript").Program} program 
 * @return {import("typescript").SourceFile}
 */
function getSourceFile(filePath, program) {
    const sourceFile = program.getSourceFile(filePath);
    if (sourceFile === undefined) {
        throw new Error(`Source file ${filePath} not found in program`);
    }
    return sourceFile;
}

/**
 * Performs post-processing of  a `*.d.ts` file, such as whether it is valid.
 * @param {string} filePath Path of the declaration file to post-process.
 * @param {SeveritySettingsConfig} severitySettings
 * @param {Partial<TsPostProcessingHooks>} postProcessorsHooks
 * @return {Promise<string>} Content of the post-processed file.
 */
async function postprocessDeclarationFile(filePath, severitySettings, postProcessorsHooks = {}) {
    // Read compiler options and parse program
    const resolvedHooks = Object.assign(createEmptyHooks(), postProcessorsHooks);
    const program = await createProgram(filePath, resolvedHooks);
    const sourceFile = getSourceFile(filePath, program);
    // Prepare handler for modifying doc comment
    fixupParents(program.getTypeChecker(), sourceFile);
    const commentHandler = createCommentHandler(sourceFile, severitySettings);
    // Validate program
    const error = await validateProgram(program, sourceFile, commentHandler, resolvedHooks);
    if (error) {
        throw error;
    }
    // Perform other processing
    await asyncForEach(resolvedHooks.process.processAst, hook => invokeHook(hook, undefined, program, [sourceFile], commentHandler, severitySettings));
    // Write declaration file back
    commentHandler.applyToSourceFile(sourceFile);
    return stringifySourceFile(sourceFile,resolvedHooks);
}

module.exports = {
    postprocessDeclarationFile,
};