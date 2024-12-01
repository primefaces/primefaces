import "jquery-cropper";
import "./imagecropper.widget.js";

// Expose Cropper as a global variable
// Not needed for our code, but might already be used by external code
import Cropper from "cropperjs";
Object.assign(window, { Cropper });
