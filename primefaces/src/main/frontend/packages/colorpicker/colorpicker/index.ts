import Coloris from "@melloware/coloris";
import { ColorPicker } from "./src/colorpicker.js";

PrimeFaces.component.ColorPicker = ColorPicker;

declare global {
    interface Window {
        Coloris: typeof Coloris;
    }

    namespace PrimeFaces {
        export interface ComponentRegistry {
            ColorPicker: typeof ColorPicker;
        }
    }
}

// Expose Coloris to the global scope
Object.assign(window, { Coloris });
