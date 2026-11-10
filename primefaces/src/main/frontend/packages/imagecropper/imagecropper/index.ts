import "jquery-cropper";
import Cropper from "cropperjs";

import { ImageCropper } from "./src/imagecropper.widget.js";

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
        export type ImageCropperCfg = import("./src/imagecropper.widget.js").ImageCropperCfg;
    }
}

// Expose Cropper as a global variable
Object.assign(window, { Cropper });
