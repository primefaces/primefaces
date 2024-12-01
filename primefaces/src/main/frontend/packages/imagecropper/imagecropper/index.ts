import "jquery-cropper";
import "./src/imagecropper.widget.js";

// Expose Cropper as a global variable
import Cropper from "cropperjs";
Object.assign(window, { Cropper });
