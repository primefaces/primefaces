import "./src/photocam.widget.js";

// Expose webcamjs to the global scope
import Webcam from "webcamjs";
Object.assign(window, { Webcam });