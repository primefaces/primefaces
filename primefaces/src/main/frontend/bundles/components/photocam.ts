import Webcam from "webcamjs";
import "../../src/photocam/1-photocam.js";

// Expose webcamjs to the global scope
// Not required for our code, but may already be used by external code
Object.assign(window, { Webcam });