/// <reference types="webcamjs" preserve="true" />

import Webcam from "webcamjs";
import { PhotoCam } from "./src/photocam.widget.js";

// Expose webcam.js to the global scope
Object.assign(window, { Webcam });

// Expose widgets to the global scope
PrimeFaces.widget.PhotoCam = PhotoCam;

// Global types
declare global {
    namespace PrimeType {
        export interface WidgetRegistry {
            /**
             * __PrimeFaces PhotoCam Widget__
             *
             * PhotoCam is used to take photos with webcam and send them to the Faces backend model.
             */
            PhotoCam: typeof PhotoCam;
        }
    }

    namespace PrimeType.widget {
        /**
         * The configuration for the {@link PhotoCam} widget.
         * 
         * You can access this configuration via {@link PhotoCam.cfg widget.cfg}.
         * Please note that this configuration is usually meant to be read-only and should
         * not be modified.
         */
        export type PhotoCamCfg = import("./src/photocam.widget.js").PhotoCamCfg;
    }

    namespace PrimeType.widget.PhotoCam {
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
