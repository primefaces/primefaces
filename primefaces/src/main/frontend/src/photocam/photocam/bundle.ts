import "./photocam.widget.js";

// Expose webcamjs to the global scope
// Not needed for our code, but may already be used by external code
import Webcam from "webcamjs";
Object.assign(window, { Webcam });