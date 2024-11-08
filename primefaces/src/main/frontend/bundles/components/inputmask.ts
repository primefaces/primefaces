import "inputmask/lib/jquery.inputmask.js";
import "../../src/inputmask/1-inputmask.js";

// Expose InputMask to the global scope
// Not needed for our code, but might already be used by external code
import Inputmask from "inputmask/lib/inputmask.js";
Object.assign(window, { Inputmask });