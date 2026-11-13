import { TextEditor } from "./src/texteditor.js";
import _Quill from "quill";

PrimeFaces.component.TextEditor = TextEditor;

declare global{
    const Quill: typeof _Quill;
    interface Window {
        Quill: typeof _Quill;
    }

    namespace PrimeFaces {
        export interface ComponentRegistry {
            TextEditor: typeof TextEditor;
        }
    }
}

// Expose Quill to the global scope
Object.assign(window, { Quill: _Quill });
