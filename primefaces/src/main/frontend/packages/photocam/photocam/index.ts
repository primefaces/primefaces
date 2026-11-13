/// <reference types="webcamjs" preserve="true" />

import Webcam from "webcamjs";
import { PhotoCam } from "./src/photocam.js";

// Expose webcam.js to the global scope
Object.assign(window, { Webcam });

// Expose components to the global scope
PrimeFaces.component.PhotoCam = PhotoCam;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            PhotoCam: typeof PhotoCam;
        }
    }
}
