import "./colorpicker.widget.js";

// Expose Coloris to the global scope
// Not needed for our code, but may already be used by external code
import Coloris from "@melloware/coloris";
Object.assign(window, { Coloris });