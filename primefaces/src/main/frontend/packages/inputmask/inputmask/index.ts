/// <reference types="inputmask" preserve="true" />

import "inputmask/dist/jquery.inputmask.js";
import _Inputmask from "inputmask/dist/jquery.inputmask.js";

import "./src/inputmask.widget.js";

// Expose InputMask to the global scope
Object.assign(window, { Inputmask: _Inputmask });