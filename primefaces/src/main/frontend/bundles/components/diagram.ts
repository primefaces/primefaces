import * as jsPlumb from "jsplumb";

import "../../src/diagram/1-diagram.js";

window["PrimeFacesLibs"] ??= {};
Object.assign(window["PrimeFacesLibs"], { jsPlumb });

// Expose jsPlumb to the global scope
// Not needed for our code, but might be used by external code
Object.assign(window, { ...jsPlumb });