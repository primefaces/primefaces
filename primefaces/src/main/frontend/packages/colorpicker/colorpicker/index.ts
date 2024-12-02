import Coloris from "@melloware/coloris";
import "./src/colorpicker.widget.js";

declare global {
    interface Window {
        Coloris: typeof Coloris;
    }
}

// Expose Coloris to the global scope
Object.assign(window, { Coloris });