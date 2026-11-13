import { InputNumber } from "./src/inputnumber.js";
import _AutoNumeric from "autonumeric";

PrimeFaces.component.InputNumber = InputNumber;

declare global {
    const AutoNumeric: typeof _AutoNumeric;
    interface Window {
        AutoNumeric: typeof _AutoNumeric;
    }

    namespace PrimeFaces {
        export interface ComponentRegistry {
            InputNumber: typeof InputNumber;
        }
    }
}

// Expose AutoNumeric to the global scope
Object.assign(window, { AutoNumeric: _AutoNumeric });
