//@ts-check

const { ESLint } = require("eslint");
const { Paths } = require("./constants");
const { makeStackLine } = require("./error");
const { withTemporaryFileOnDisk } = require("./lang-fs");

/**
 * @param {TypeDeclarationBundleFiles} sourceFileNames 
 */
async function lintTsFile(sourceFileNames) {
    const report = await withTemporaryFileOnDisk(sourceFileNames, Paths.NpmVirtualDeclarationFile, file => {
        const eslint = new ESLint({
            overrideConfigFile: Paths.EsLintRcPath,
            useEslintrc: true,
            fix: false,
        });
        return eslint.lintFiles([file.ambient, file.module]);
    });
    const errorCount = report.map(r => r.errorCount).reduce((s,x) => s + x, 0);
    const warningCount = report.map(r => r.warningCount).reduce((s,x) => s + x, 0);
    console.log(`Linting done, found ${errorCount} error(s) and ${warningCount} warning(s)`);
    for (const result of report) {
        for (const message of result.messages) {
            const stack = makeStackLine(message.ruleId ? `[rule:${message.ruleId}]` : "[linting-message]", result.filePath, message.line || 0, message.column || 0);
            const msg = `${stack}\n${message.message}\n`;
            switch (message.severity) {
                case 0: // off
                    break;
                case 1: // warn
                    console.warn("Warning:", msg);
                    break;
                case 2: // error
                    console.error("Error:", msg);
                    break;
                default:
                    throw new Error("Unknown severity level: " + message.severity);
            }
        }
    }
    if (errorCount > 0) {
        throw new Error(`Linting of files '${sourceFileNames.ambient}' and '${sourceFileNames.module}' failed see above for details`);
    }
}

module.exports = {
    lintTsFile,
};