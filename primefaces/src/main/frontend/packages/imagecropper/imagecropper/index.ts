import "jquery-cropper";
import Cropper from "cropperjs";

import "./src/imagecropper.widget.js";

declare global {
    interface Window {
        Cropper: typeof Cropper;
    }
}

// Expose Cropper as a global variable
Object.assign(window, { Cropper });
