import "./inputnumber.widget.js";

// Expose AutoNumeric to the global scope
// Not needed for our code, but might already be used by external code
import AutoNumeric from "autonumeric";
Object.assign(window, { AutoNumeric });