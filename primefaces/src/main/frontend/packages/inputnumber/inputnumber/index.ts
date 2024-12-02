import "./src/inputnumber.widget.js";
import _AutoNumeric from "autonumeric";

declare global {
    const AutoNumeric: typeof _AutoNumeric;
    interface Window {
        AutoNumeric: typeof _AutoNumeric;
    }
}

// Expose AutoNumeric to the global scope
Object.assign(window, { AutoNumeric: _AutoNumeric });