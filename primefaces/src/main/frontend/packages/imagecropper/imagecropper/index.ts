import "jquery-cropper";
import Cropper from "cropperjs";

import { ImageCropper } from "./src/imagecropper.js";

PrimeFaces.component.ImageCropper = ImageCropper;

declare global {
    interface Window {
        Cropper: typeof Cropper;
    }

    namespace PrimeFaces {
        export interface ComponentRegistry {
            ImageCropper: typeof ImageCropper;
        }
    }
}

// Expose Cropper as a global variable
Object.assign(window, { Cropper });
