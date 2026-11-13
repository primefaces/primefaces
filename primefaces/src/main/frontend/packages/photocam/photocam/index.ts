/// <reference types="webcamjs" preserve="true" />

import Webcam from "webcamjs";
import { PhotoCam } from "./src/photocam.js";

// Expose webcam.js to the global scope
Object.assign(window, { Webcam });

// Expose components to the global scope
PrimeFaces.component.PhotoCam = PhotoCam;

// Global types
declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            /**
             * __PrimeFaces PhotoCam Component__
             *
             * PhotoCam is used to take photos with webcam and send them to the Faces backend model.
             */
            PhotoCam: typeof PhotoCam;
        }
    }

    namespace PrimeFacesTypes.component {
        /**
         * The configuration for the {@link PhotoCam} component.
         * 
         * You can access this configuration via {@link PhotoCam.cfg component.cfg}.
         * Please note that this configuration is usually meant to be read-only and should
         * not be modified.
         */
        export type PhotoCamCfg = import("./src/photocam.js").PhotoCamCfg;
    }

    namespace PrimeFacesTypes.component.PhotoCam {
        /**
         * Callback invoked when an error is caught by the Webcam.js engine.
         * See also {@link PhotoCamCfg.onCameraError}.
         */
        export type CameraErrorCallback =
            /**
             * @param errorObj The error object containing the error information.
             */
            (this: PhotoCam<PhotoCamCfg>, errorObj: Error) => void;
    }
}
