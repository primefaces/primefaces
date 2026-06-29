//@ts-check

const { CLIEngine } = require("eslint");
const { Paths } = require("./constants");
const { makeStackLine } = require("./error");
const { withTemporaryFileOnDisk } = require("./lang-fs");

/**
 * 
 * @param {TypeDeclarationBundleFiles} sourceFileNames 
 */
async function lintTsFile(sourceFileNames) {
    const report = await withTemporaryFileOnDisk(sourceFileNames, Paths.NpmVirtualDeclarationFile, file => {
        const linter = new CLIEngine({
            configFile: Paths.EsLintRcPath,
            useEslintrc: true,
            fix: false,
        });
        return linter.executeOnFiles([file.ambient, file.module]);
    });
    console.log(`Linting done, found ${report.errorCount} error(s) and ${report.warningCount} warning(s)`);
    for (const result of report.results) {
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
    if (report.errorCount > 0) {
        throw new Error(`Linting of files '${sourceFileNames.ambient}' and '${sourceFileNames.module}' failed see above for details`);
    }
}

module.exports = {
    lintTsFile,
};