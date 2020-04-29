//@ts-check

const ts = require("typescript");

/**
 * @return {ts.LanguageServiceHost & {addFile: (fileName: string, text: string) => void}}
 */
function createLanguageHost() {
    /** @type {ts.MapLike<ts.IScriptSnapshot>} */
    const files = {};
    return {
        /**
         * 
         * @param {string} fileName 
         * @param {string} text 
         */
        addFile(fileName, text) {
            files[fileName] = ts.ScriptSnapshot.fromString(text);
        },

        // for ts.LanguageServiceHost

        getCompilationSettings() {
            return ts.getDefaultCompilerOptions();
        },

        getScriptFileNames() {
            return Object.keys(files);
        },

        getScriptVersion (_fileName){
            return "0";
        },

        getScriptSnapshot(fileName) {
            return files[fileName];
        },
        
        getCurrentDirectory(){
            return process.cwd();
        },

        getDefaultLibFileName(options) {
            return ts.getDefaultLibFilePath(options);
        },
    };
}

/**
 * 
 * @param {string} fileName 
 * @param {string} text 
 * @param {ts.FormatCodeSettings} options 
 */
function format(fileName, text, options) {
    const host = createLanguageHost();
    host.addFile(fileName, text);
    const languageService = ts.createLanguageService(host);
    const edits = languageService.getFormattingEditsForDocument(fileName, options);
    edits
        .sort((a, b) => a.span.start - b.span.start)
        .reverse()
        .forEach(edit => {
            const head = text.slice(0, edit.span.start);
            const tail = text.slice(edit.span.start + edit.span.length);
            text = `${head}${edit.newText}${tail}`;
        });

    return text;
}

module.exports = {
    format,
};