import "jquery-cropper";
import Cropper from "cropperjs";

import { ImageCropper } from "./src/imagecropper.js";

PrimeFaces.widget.ImageCropper = ImageCropper;

declare global {
    interface Window {
        Cropper: typeof Cropper;
    }

    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ImageCropper: typeof ImageCropper;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ImageCropperCfg = import("./src/imagecropper.js").ImageCropperCfg;
    }
}

// Expose Cropper as a global variable
Object.assign(window, { Cropper });
