import Webcam from "webcamjs";

/**
 * __PrimeFaces PhotoCam Widget__
 *
 * PhotoCam is used to take photos with webcam and send them to the JSF backend model.
 *
 * @typedef PrimeFaces.widget.PhotoCam.onCameraError Callback invoked when an error is caught by the Webcam.js engine.
 * See also {@link PhotoCamCfg.onCameraError}.
 * @this {PrimeFaces.widget.PhotoCam} PrimeFaces.widget.PhotoCam.onCameraError
 * @param {Error} PrimeFaces.widget.PhotoCam.onCameraError.errorObj The error object containing the error information.
 *
 * @prop {string} device The ID of device to retrieve images.
 *
 * @interface {PrimeFaces.widget.PhotoCamCfg} cfg The configuration for the {@link  PhotoCam| PhotoCam widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.autoStart Whether access to the camera should be requested automatically upon page load.
 * @prop {Webcam.ImageFormat} cfg.format Format of the image file.
 * @prop {string} cfg.device The ID of device to retrieve images
 * @prop {number} cfg.height Height of the camera viewport in pixels.
 * @prop {number} cfg.jpegQuality Quality of the image between `0` and `100` when the format is `jpeg`, default value is `90`.
 * @prop {number} cfg.photoHeight Height of the captured photo in pixels, defaults to height.
 * @prop {number} cfg.photoWidth Width of the captured photo in pixels, defaults to width.
 * @prop {string} cfg.process Identifiers of components to process during capture.
 * @prop {string} cfg.update Identifiers of components to update during capture.
 * @prop {number} cfg.width Width of the camera viewport in pixels.
 * @prop {PrimeFaces.widget.PhotoCam.onCameraError} cfg.onCameraError Custom Webcam.js error handler
 */
PrimeFaces.widget.PhotoCam = class PhotoCam extends PrimeFaces.widget.BaseWidget {
    /**
     * Whether the camera is currently attached and can take photos.
     * @type {boolean}
     */
    attached= false;

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.cfg.width = this.cfg.width||320;
        this.cfg.height = this.cfg.height||240;
        this.cfg.photoWidth = this.cfg.photoWidth||this.cfg.width;
        this.cfg.photoHeight = this.cfg.photoHeight||this.cfg.height;
        this.cfg.jpegQuality = this.cfg.jpegQuality ||90;
        if (!("autoStart" in this.cfg)) {
            this.cfg.autoStart = true;
        }

        if(this.cfg.onCameraError) {
            this.onCameraError = this.cfg.onCameraError;
        }

        this.device = this.cfg.device;

        if (this.cfg.autoStart) {
            this.attach();
        }

    }

    /**
     * @override
     * @inheritdoc
     */
    destroy() {
        super.destroy();

        this.detach();
    }

    /**
     * Attaches the web camera, requesting access to the camera of the user.
     */
    attach() {
        if (!this.attached) {
            Webcam.reset();
            var $this = this;
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
                user_callback: function(data) {
                    var options = {
                        source: $this.id,
                        process: $this.cfg.process ? $this.id + ' ' + $this.cfg.process : $this.id,
                        update: $this.cfg.update,
                        params: [
                            {name: $this.id + '_data', value: data}
                        ]
                    };
                    PrimeFaces.ajax.Request.handle(options);
                }
            });

            $this = this;
            Webcam.on("error", this.onCameraError);

            Webcam.attach(this.id);
            this.attached = true;
        }
    }

    /**
     * Default error handler for webcam events
     * @private
     * @param {Error} errorObj Error object containing message, stacktrace and so on.
     */
    onCameraError(errorObj) {
        var message;
        if (errorObj instanceof Webcam.errors.WebcamError) {
            message = errorObj.message;
        } else {
            message = "Could not access webcam: " + errorObj.name + ": " +
                errorObj.message + " " + errorObj.toString();
        }

        alert("Webcam.js caught an error: " + message);
    }

    /**
     * Detaches the web camera so that no more photos can be taken.
     */
    detach() {
        if (this.attached) {
            Webcam.reset();
            this.attached = false;
        }
    }

    /**
     * Takes a photo with the web cam. Logs an error if no photo can be takes, such as when the user does not have
     * a camera or did not allow access to the camera.
     */
    capture() {
        if (this.attached) {
            Webcam.snap();
        } else {
            PrimeFaces.error('Capture error: AdvancedPhotoCam not attached to the camera');
        }
    }

    /**
     * Retrieves the available video input device list.
     * @return {Promise<MediaDeviceInfo[]> | null} The available video input device list, or `null` if the browser does
     * not support media devices enumeration.
     */
    getAvailableDevices() {
        var result = null;
        if (navigator.mediaDevices && navigator.mediaDevices.enumerateDevices) {
            result = navigator.mediaDevices.enumerateDevices().then(function (devices) {
                return devices.filter(
                    function (device) {
                        return device.kind === "videoinput";
                    });
            });
        }
        return result;
    }

    /**
     * Utility to detach and attach the video again.
     */
    reload() {
        if (this.attached) {
            this.detach();
            this.attach();
        }
    }

}