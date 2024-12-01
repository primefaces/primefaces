import "./diagram.widget.js";

// Expose jsPlumb to the global scope
// Not needed for our code, but might be used by external code
import * as jsPlumb from "jsplumb";
Object.assign(window, { ...jsPlumb });