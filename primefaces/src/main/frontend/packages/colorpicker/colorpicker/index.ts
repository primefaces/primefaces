import Coloris from "@melloware/coloris";
import { ColorPicker } from "./src/colorpicker.js";

PrimeFaces.widget.ColorPicker = ColorPicker;

declare global {
    interface Window {
        Coloris: typeof Coloris;
    }

    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ColorPicker: typeof ColorPicker;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ColorPickerCfg = import("./src/colorpicker.js").ColorPickerCfg;
    }

    namespace PrimeFacesTypes.widget.ColorPicker {
        /**
         * Display mode of a color picker. `inline` renders the color picker within the normal content flow, `popup`
         * creates an overlay that is displayed when the user clicks on the color.
         */
        export type DisplayMode = "inline" | "popup";
    }
}

// Expose Coloris to the global scope
Object.assign(window, { Coloris });
