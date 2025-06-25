import Webcam from "webcamjs";

/**
 * The configuration for the {@link PhotoCam} widget.
 * 
 * You can access this configuration via {@link PhotoCam.cfg widget.cfg}.
 * Please note that this configuration is usually meant to be read-only and should
 * not be modified.
 */
export interface PhotoCamCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * Whether access to the camera should be requested automatically upon page load.
     */
    autoStart: boolean;

    /**
     * The ID of device to retrieve images
     */
    device: string;

    /**
     * Format of the image file.
     */
    format: Webcam.ImageFormat;

    /**
     * Height of the camera viewport in pixels.
     */
    height: number;

    /**
     * Quality of the image between `0` and `100` when the format is `jpeg`, default value is `90`.
     */
    jpegQuality: number;

    /**
     * Custom error handler invoked when a snapshot could not be taken.
     */
    onCameraError: PrimeType.widget.PhotoCam.CameraErrorCallback;

    /**
     * Height of the captured photo in pixels, defaults to height.
     */
    photoHeight: number;

    /**
     * Width of the captured photo in pixels, defaults to width.
     */
    photoWidth: number;

    /**
     * Identifiers of components to process during capture.
     */
    process: string;

    /**
     * Identifiers of components to update during capture.
     */
    update: string;

    /**
     * Width of the camera viewport in pixels.
     */
    width: number;
}

/**
 * Default error handler for webcam events
 * @param errorObj Error object containing message, stacktrace and so on.
 */
const defaultErrorCallback: PrimeType.widget.PhotoCam.CameraErrorCallback = (errorObj) => {
    const message = errorObj instanceof Webcam.errors.WebcamError
        ? errorObj.message
        : `Could not access webcam: ${errorObj.name}: ${errorObj.message} ${errorObj.toString()}`;
    alert("Webcam.js caught an error: " + message);
};

/**
 * __PrimeFaces PhotoCam Widget__
 *
 * PhotoCam is used to take photos with webcam and send them to the Jakarta Faces backend model.
 */
export class PhotoCam<Cfg extends PhotoCamCfg = PhotoCamCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * Whether the camera is currently attached and can take photos.
     */
    attached: boolean = false;

    /**
     * The ID of device to retrieve images.
     */
    device: string = "";

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.width = this.cfg.width||320;
        this.cfg.height = this.cfg.height||240;
        this.cfg.photoWidth = this.cfg.photoWidth||this.cfg.width;
        this.cfg.photoHeight = this.cfg.photoHeight||this.cfg.height;
        this.cfg.jpegQuality = this.cfg.jpegQuality ||90;
        if (!("autoStart" in this.cfg)) {
            this.cfg.autoStart = true;
        }

        this.device = this.cfg.device ?? "";

        if (this.cfg.autoStart) {
            this.attach();
        }
    }

    override destroy(): void {
        super.destroy();

        this.detach();
    }

    /**
     * Attaches the web camera, requesting access to the camera of the user.
     */
    attach(): void {
        if (!this.attached) {
            Webcam.reset();
            Webcam.set({
                width: this.cfg.width,
                height: this.cfg.height,
                dest_width: this.cfg.photoWidth,
                dest_height: this.cfg.photoHeight,
                image_format: this.cfg.format,
                jpeg_quality: this.cfg.jpegQuality,
                force_flash: false,
                enable_flash: false,
                device: this.device,
            });

            const errorCallback = this.cfg.onCameraError ?? defaultErrorCallback;
            Webcam.on("error", error => errorCallback.call(this, error));

            Webcam.attach(this.getId());
            this.attached = true;
        }
    }

    /**
     * Detaches the web camera so that no more photos can be taken.
     */
    detach(): void {
        if (this.attached) {
            Webcam.reset();
            this.attached = false;
        }
    }

    /**
     * Takes a photo with the web cam. Logs an error if no photo can be takes, such as when the user does not have
     * a camera or did not allow access to the camera.
     */
    capture(): void {
        if (this.attached) {
            Webcam.snap((data) => {
                const options: PrimeType.ajax.Configuration = {
                    source: this.getId(),
                    process: this.cfg.process ? this.getId() + ' ' + this.cfg.process : this.getId(),
                    update: this.cfg.update,
                    params: [{name: this.getId() + '_data', value: data }]
                };
                PrimeFaces.ajax.Request.handle(options);
            });
        } else {
            PrimeFaces.error('Capture error: AdvancedPhotoCam not attached to the camera');
        }
    }

    /**
     * Retrieves the available video input device list.
     * @returns The available video input device list, or `null` if the browser does
     * not support media devices enumeration.
     */
    getAvailableDevices(): Promise<MediaDeviceInfo[]> | null {
        if (navigator.mediaDevices && navigator.mediaDevices.enumerateDevices) {
            return navigator.mediaDevices.enumerateDevices().then((devices) => {
                return devices.filter((device) => device.kind === "videoinput");
            });
        } else {
            return null;
        }
    }

    /**
     * Utility to detach and attach the video again.
     */
    reload(): void {
        if (this.attached) {
            this.detach();
            this.attach();
        }
    }
}
