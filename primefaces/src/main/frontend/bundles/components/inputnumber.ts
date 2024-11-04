import * as AutoNumeric from "autonumeric";
import AutoNumericDefault from "autonumeric";
import "../../src/inputnumber/1-inputnumber.js";

window["PrimeFacesLibs"] ??= {};
Object.assign(window["PrimeFacesLibs"], { AutoNumeric });

// Expose AutoNumeric to the global scope
// Not needed for our code, but might already be used by external code
Object.assign(window, { AutoNumeric: AutoNumericDefault });