import "inputmask/dist/jquery.inputmask.js";
import "./src/inputmask.widget.js";

// Expose InputMask to the global scope
// Not needed for our code, but might already be used by external code
import Inputmask from "inputmask/dist/jquery.inputmask.js";
Object.assign(window, { Inputmask });