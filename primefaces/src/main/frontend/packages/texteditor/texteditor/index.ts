import "./src/texteditor.widget.js";

// Expose Quill to the global scope
// Not needed for our code, but may already be used by external code
import Quill from "quill";
Object.assign(window, { Quill });