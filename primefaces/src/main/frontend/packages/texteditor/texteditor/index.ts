import { TextEditor } from "./src/texteditor.js";
import _Quill from "quill";

PrimeFaces.widget.TextEditor = TextEditor;

declare global{
    const Quill: typeof _Quill;
    interface Window {
        Quill: typeof _Quill;
    }

    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            TextEditor: typeof TextEditor;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type TextEditorCfg = import("./src/texteditor.js").TextEditorCfg;
    }
}

// Expose Quill to the global scope
Object.assign(window, { Quill: _Quill });
