import "./src/texteditor.widget.js";
import _Quill from "quill";

declare global{
    const Quill: typeof _Quill;
    interface Window {
        Quill: typeof _Quill;
    }
}

// Expose Quill to the global scope
Object.assign(window, { Quill: _Quill });
