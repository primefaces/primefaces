import * as Inputmask from "inputmask/dist/inputmask.es6.js";
import "inputmask/lib/jquery.inputmask.js";
import "../../src/inputmask/1-inputmask.js";

window["PrimeFacesLibs"] ??= {};
Object.assign(window["PrimeFacesLibs"], { Inputmask });

// Expose InputMask to the global scope
// Not needed for our code, but might already be used by external code
Object.assign(window, { Inputmask: Inputmask.default });