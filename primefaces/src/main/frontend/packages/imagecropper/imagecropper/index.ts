import "jquery-cropper";
import Cropper from "cropperjs";

import { ImageCropper } from "./src/imagecropper.js";

PrimeFaces.component.ImageCropper = ImageCropper;

declare global {
    interface Window {
        Cropper: typeof Cropper;
    }

    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            ImageCropper: typeof ImageCropper;
        }
    }

    namespace PrimeFacesTypes.component {
        export type ImageCropperCfg = import("./src/imagecropper.js").ImageCropperCfg;
    }
}

// Expose Cropper as a global variable
Object.assign(window, { Cropper });
