//@ts-check

const { resolve } = require("path");
const ts = require("typescript");
const {assertNever } = require("./lang");

/**
 * Creates a message that reference a stack element. Tested with vscode, it turns it into a link that
 * can be used to jump to the file quickly.
 * @param {string} name Name of the method or symbol that caused the error.
 * @param {string|undefined} file File where the error occurred. Should be an absolute path. If not, it is resolved against the cwd.
 * @param {number} line Line where the error occured.
 * @param {number} column Column where the error occured.
 */
function makeStackLine(name, file, line, column) {
    const resolvedPath = file ? resolve(file) : "<unknown>";
    return `at ${name} (${resolvedPath}:${line}:${column})`;
}

/**
 * @param {string} message 
 * @param {TsNode} node 
 * @param {string} location Human readable name of the method or location where the error occured.
 * @return {string}
 */
function newTsErrorMessage(message, node, location = "<unknown>") {
    const sourceFile = node.getSourceFile();
    const position = ts.getLineAndCharacterOfPosition(sourceFile, node.pos);
    const stackElement = makeStackLine(location || "<unknown>", sourceFile.fileName, position.line + 1, position.character + 1);
    return `${stackElement}\n${message}`;
}

/**
 * @param {string} message Message with details about the error.
 * @param {import("estree").Node} node Node where the error or issue occured.
 * @param {string} location Human readable name of the method or location where the error occured.
 * @return {string}
 */
function newNodeErrorMessage(message, node, location = "<unknown>") {
    const source = node.loc && node.loc.source ? node.loc.source : undefined;
    const line = node.loc ? node.loc.start.line : 0;
    const column = node.loc ? node.loc.start.column : 0;
    return `${message}\n${makeStackLine(location, source, line, column)}`;
}

/**
 * @param {string} message 
 * @param {ObjectCodeMethod} method Method to document.
 * @param {import("estree").Node | undefined} node Optional node to indicate a more specific location of the error.
 */
function newMethodErrorMessage(message, method, node = undefined) {
    return newNodeErrorMessage(message, node || method.node, `method ${method.name}`);
}

/**
 * @param {string} message 
 * @param {ObjectCodeMethod} method Method to document.
 * @param {import("estree").Node | undefined} node Optional node to indicate a more specific location of the error.
 */
function newFunctionErrorMessage(message, method, node = undefined) {
    return newNodeErrorMessage(message, node || method.node, `function ${method.name}`);
}

/**
 * @param {string} message 
 * @param {CodeConstant} constant Method to document.
 * @param {import("estree").Node | undefined} node Optional node to indicate a more specific location of the error.
 */
function newConstantErrorMessage(message, constant, node = undefined) {
    return newNodeErrorMessage(message, node || constant.node, `constant ${constant.name}`);
}

/**
 * @param {string} message 
 * @param {ObjectCodeProperty} property Method to document.
 * @param {import("estree").Node | undefined} node Optional node to indicate a more specific location of the error.
 */
function newPropErrorMessage(message, property, node = undefined) {
    return newNodeErrorMessage(message, node || property.node, `property ${property.name}`);
}

/**
 * @param {string} key
 * @param {() => string} messageSupplier
 * @return {string}
 */
function makeMessage(key, messageSupplier) {
    return `[${key}] ${messageSupplier()}`;
}

/**
 * Throws an error, warns the user about or ignores it, depending on the severity setting.
 * @param {keyof SeveritySettingsConfig} key
 * @param {SeveritySettingsConfig} severitySettings
 * @param {() => string} messageSupplier 
 */
function handleError(key, severitySettings, messageSupplier) {
    const error = handleAndGetError(key, severitySettings, messageSupplier);
    if (error.length > 0) {
        throw error[0];
    }
}

/**
 * Same as `handleError`, but only returns an Error.
 * @param {keyof SeveritySettingsConfig} key 
 * @param {SeveritySettingsConfig} severitySettings
 * @param {() => string} messageSupplier 
 * @return {Error[]}
 */
function handleAndGetError(key, severitySettings, messageSupplier) {
    const level = severitySettings[key];
    switch (level) {
        case "fatal":
            return [new Error(makeMessage(key, messageSupplier))];
        case "error":
            console.error("[ERROR]", makeMessage(key, messageSupplier));
            return [];
        case "warning":
            console.warn("[WARNING]", makeMessage(key, messageSupplier));
            return [];
        case "info":
            console.info("[INFO]", makeMessage(key, messageSupplier));
            return [];
        case "ignore": 
            // ignore this message
            return [];
        default: assertNever(level);
    }
}

module.exports = {
    handleError,
    handleAndGetError,
    makeStackLine,
    newConstantErrorMessage,
    newFunctionErrorMessage,
    newMethodErrorMessage,
    newNodeErrorMessage,
    newPropErrorMessage,
    newTsErrorMessage,
};
