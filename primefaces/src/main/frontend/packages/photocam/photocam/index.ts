import Webcam from "webcamjs";
import "./src/photocam.widget.js";

// Expose webcamjs to the global scope
Object.assign(window, { Webcam });