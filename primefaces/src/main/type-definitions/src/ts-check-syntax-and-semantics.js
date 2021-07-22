//@ts-check

const ts = require("typescript");

const { makeStackLine } = require("./error");

/**
 * @param {import("typescript").Program} program 
 * @return {TsValidationMessage[]}
 */
function validateSyntaxAndSemantics(program) {
    const emitResult = program.emit();
    const allDiagnostics = ts
        .getPreEmitDiagnostics(program)
        .concat(emitResult.diagnostics);


    /** @type {TsDiagnostics} */
    const initial = { warnings: [], errors: [], suggestions: [], messages: [] };

    return allDiagnostics.map(diagnostic => {
        const message = ts.flattenDiagnosticMessageText(diagnostic.messageText, "\n");
        let output;
        if (diagnostic.file) {
            let pos = { line: 1, character: 1 };
            if (diagnostic.start !== undefined) {
                pos = diagnostic.file.getLineAndCharacterOfPosition(diagnostic.start);
            }
            const stackElement = makeStackLine(diagnostic.source || "<unknown>", diagnostic.file.fileName, pos.line + 1, pos.character + 1);
            output = `${stackElement}\n${message}`
        }
        else {
            output = message;
        }
        switch (diagnostic.category) {
            case ts.DiagnosticCategory.Error:
                return {
                    message: output,
                    severity: "error",
                    type: "tsDiagnosticsError",
                };
            case ts.DiagnosticCategory.Warning:
                return {
                    message: output,
                    severity: "warning",
                    type: "tsDiagnosticsWarning",
                };
            case ts.DiagnosticCategory.Suggestion:
                return {
                    message: output,
                    severity: "suggestion",
                    type: "tsDiagnosticsSuggestion",
                };
            case ts.DiagnosticCategory.Message:
                return {
                    message: output,
                    severity: "message",
                    type: "tsDiagnosticsMessage",
                };
            default:
                return {
                    message: output,
                    severity: "error",
                    type: "tsDiagnosticsError",
                };
        }
    }, initial);
}

module.exports = {
    validateSyntaxAndSemantics,
};