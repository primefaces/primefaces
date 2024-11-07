import "../../src/colorpicker/1-colorpicker.js";

// Expose Coloris to the global scope
// Not necessary for our code, but expose to global scope for backwards compatibility
import Coloris from "@melloware/coloris";
Object.assign(window, { Coloris });