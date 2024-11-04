import Quill from "quill";
import "../../src/texteditor/1-texteditor.js";

// Expose Quill to the global scope
// Not needed for our code, but may already be used by external code
Object.assign(window, { Quill });