import { InputNumber } from "./src/inputnumber.js";
import _AutoNumeric from "autonumeric";

PrimeFaces.widget.InputNumber = InputNumber;

declare global {
    const AutoNumeric: typeof _AutoNumeric;
    interface Window {
        AutoNumeric: typeof _AutoNumeric;
    }

    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            InputNumber: typeof InputNumber;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type InputNumberCfg = import("./src/inputnumber.js").InputNumberCfg;
    }

    namespace PrimeFacesTypes.widget.InputNumber {
        /**
         * Alias for the AutoNumeric options, required for technical reasons.
         */
        export type AutoNumericOptions = import("autonumeric").Options;
    }
}

// Expose AutoNumeric to the global scope
Object.assign(window, { AutoNumeric: _AutoNumeric });
