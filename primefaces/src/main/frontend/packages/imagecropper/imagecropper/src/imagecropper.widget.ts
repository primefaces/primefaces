import type Cropper from "cropperjs";

/**
 * The configuration for the {@link ImageCropper} widget.
 */
export interface ImageCropperCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg, Cropper.Options<HTMLImageElement> {
    /** ID of the IMAGE element. */
    image: string;
    /** Initial coordinates of the cropper area (x, y, width, height). */
    initialCoords: [number, number, number, number];
    /** Maximum height of the crop box, computed from {@link maxSize} and the size of the image. */
    maxCropBoxHeight: number;
    /** Maximum width of the crop box, computed from {@link maxSize} and the size of the image. */
    maxCropBoxWidth: number;
    /** Maximum size of the crop box (width, height). */
    maxSize: [number, number];
    /** Minimum size of the crop box (width, height). */
    minSize: [number, number];
}

/**
 * __PrimeFaces ImageCropper Widget__
 *
 * ImageCropper allows cropping a certain region of an image. A new image is created containing the cropped area and
 * assigned to a `CroppedImage` instanced on the server side. Uses CropperJS - to interact with the image cropper
 * programmatically, use the Cropper JQuery plugin. For example:
 *
 * ```javascript
 * PF("myImageCropperWidget").image.cropper("rotate", 90);
 * ```
 */
export class ImageCropper<Cfg extends ImageCropperCfg = ImageCropperCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** The configuration, the crop box limits are computed by {@link init}. */
    declare cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>
        & { minCropBoxWidth: number; minCropBoxHeight: number; maxCropBoxWidth: number; maxCropBoxHeight: number; };

    /** Whether the image is currently being cropped. */
    cropping?: boolean;
    /** The current {@link Cropper} instance. */
    cropper?: Cropper | null;
    /** DOM element of the image element to crop. You can use this element to access the {@link Cropper}. */
    image: JQuery<HTMLImageElement> = $();
    /** DOM element of the hidden INPUT element that stores the selected crop area. */
    jqCoords: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.image = $(PrimeFaces.escapeClientId(this.cfg.image!)) as JQuery<HTMLImageElement>;
        this.jqCoords = $(this.jqId + '_coords');

        // calculate the min and max of the cropper box
        const imageWidth = this.image[0]!.naturalWidth,
            imageHeight = this.image[0]!.naturalHeight;
        this.cfg.minCropBoxWidth = this.cfg.minSize ? this.cfg.minSize[0] : 0;
        this.cfg.minCropBoxHeight = this.cfg.minSize ? this.cfg.minSize[1] : 0;
        this.cfg.maxCropBoxWidth = Math.min(imageWidth, this.cfg.maxSize ? this.cfg.maxSize[0] : imageWidth);
        this.cfg.maxCropBoxHeight = Math.min(imageHeight, this.cfg.maxSize ? this.cfg.maxSize[1] : imageHeight);

        this.cfg.data = {
            width : (this.cfg.minCropBoxWidth + this.cfg.maxCropBoxWidth) / 2,
            height : (this.cfg.minCropBoxHeight + this.cfg.maxCropBoxHeight) / 2,
        };

        this.renderDeferred();
    }

    protected override _render(): void {
        // initialize the cropper
        this.image.cropper(this.cfg as CropperOptions);

        // update coordinates as the box is adjusted
        this.image.on('crop', (event) => {
            this.onCrop(event);
        });

        this.image.on('ready', (e) => {
            // set the initial coordinates
            if (this.cfg.initialCoords) {
                // cropperjs stores its instance in the property `cropper` of the image
                (e.currentTarget as HTMLImageElement & { cropper: Cropper }).cropper.setCropBoxData({
                    left : this.cfg.initialCoords[0],
                    top : this.cfg.initialCoords[1],
                    width : this.cfg.initialCoords[2],
                    height : this.cfg.initialCoords[3]
                });
            }
        });

        // Get the Cropper.js instance after initialized
        this.cropper = this.image.data('cropper');
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        // release the window/document listeners of the previous cropper instance
        if (this.cropper) {
            this.cropper.destroy();
            this.cropper = null;
        }

        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();

        // clean up memory
        if (this.cropper) {
            this.cropper.destroy();
        }
    }

    /**
     * Callback for when a crop was performed.
     * @param event The crop event that occurred, its `detail` are the crop data.
     */
    private onCrop(event: JQuery.TriggeredEvent): void {
        if (this.cropping) {
            return;
        }

        // jQuery copies the detail of the CustomEvent of cropperjs
        const detail = event.detail as unknown as Cropper.Data;
        let width = detail.width;
        let height = detail.height;

        // constrain the box if necessary
        if (width < this.cfg.minCropBoxWidth
                || height < this.cfg.minCropBoxHeight
                || width > this.cfg.maxCropBoxWidth
                || height > this.cfg.maxCropBoxHeight) {

            width = Math.max(this.cfg.minCropBoxWidth, Math.min(this.cfg.maxCropBoxWidth, width));
            height = Math.max(this.cfg.minCropBoxHeight, Math.min(this.cfg.maxCropBoxHeight, height));

            this.cropping = true;
            this.cropper!.setCropBoxData({
                width : width,
                height : height
            });
        }

        // set the new box coordinates
        const cropCoords = detail.x + "_" + detail.y + "_" + width + "_" + height;
        this.jqCoords.val(cropCoords);
        this.cropping = false;
    }

    /**
     * Reset the image and crop box to their initial states.
     */
    reset(): void {
        if (this.cropper) {
            this.cropper.reset();
        }
    }

    /**
     * Clears the crop box.
     */
    clear(): void {
        if (this.cropper) {
            this.cropper.clear();
        }
    }

    /**
     * Enables (unfreezes) the cropper.
     */
    override enable(): void {
        if (this.cropper) {
            this.cropper.enable();
        }
    }

    /**
     * Disables (freezes) the cropper.
     */
    override disable(): void {
        if (this.cropper) {
            this.cropper.disable();
        }
    }
}
