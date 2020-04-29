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
 * 
 * @prop {Cropper} cropper The current {@link Cropper} instance.
 * @prop {JQuery} image DOM element of the image element to crop. You can use this element to access the {@link Cropper}.
 * @prop {JQuery} jqCoords DOM element of the hidden INPUT element that stores the selected crop area.
 * 
 * @interface {PrimeFaces.widget.ImageCropperCfg} cfg The configuration for the {@link  ImageCropper|ImageCropper widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * @extends {Cropper.Options} cfg
 * 
 * @prop {string} cfg.image ID of the IMAGE element.
 * @prop {[number, number, number, number]} cfg.initialCoords Initial coordinates of the cropper area (x, y, width,
 * height).
 */
PrimeFaces.widget.ImageCropper = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init : function (cfg) {
        this._super(cfg);
        this.image = $(PrimeFaces.escapeClientId(this.cfg.image));
        this.jqCoords = $(this.jqId + '_coords');

        // calculate the min and max of the cropper box
        var imageWidth = this.image[0].naturalWidth,
            imageHeight = this.image[0].naturalHeight;
        this.cfg.minCropBoxWidth = this.cfg.minSize ? this.cfg.minSize[0] : 0;
        this.cfg.minCropBoxHeight = this.cfg.minSize ? this.cfg.minSize[1] : 0;
        this.cfg.maxCropBoxWidth = Math.min(imageWidth, this.cfg.maxSize ? this.cfg.maxSize[0] : imageWidth);
        this.cfg.maxCropBoxHeight = Math.min(imageHeight, this.cfg.maxSize ? this.cfg.maxSize[1] : imageHeight);

        this.cfg.data = {
            width : (this.cfg.minCropBoxWidth + this.cfg.maxCropBoxWidth) / 2,
            height : (this.cfg.minCropBoxHeight + this.cfg.maxCropBoxHeight) / 2,
        };

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @inheritdoc
     * @protected
     */
    _render : function () {
        var $this = this;

        // initialize the cropper
        this.image.cropper(this.cfg);

        // update coordinates as the box is adjusted
        this.image.on('crop', function (event) {
            $this.onCrop(event)
        });

        this.image.on('ready', function () {
            // set the initial coordinates
            if ($this.cfg.initialCoords) {
                this.cropper.setCropBoxData({
                    left : $this.cfg.initialCoords[0],
                    top : $this.cfg.initialCoords[1],
                    width : $this.cfg.initialCoords[2],
                    height : $this.cfg.initialCoords[3]
                });
            }
        });

        // Get the Cropper.js instance after initialized
        this.cropper = this.image.data('cropper');
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();

        // clean up memory
        if (this.cropper) {
            this.cropper.destroy();
        }
    },

    /**
     * Callback for when a crop was performed.
     * @private
     * @param {JQueryCropper.CropEvent} event The crop event that occurred.
     */
    onCrop : function (event) {
        if (this.cropping) {
            return;
        }

        var width = event.detail.width;
        var height = event.detail.height;

        // constrain the box if necessary
        if (width < this.cfg.minCropBoxWidth
                || height < this.cfg.minCropBoxHeight
                || width > this.cfg.maxCropBoxWidth
                || height > this.cfg.maxCropBoxHeight) {

            width = Math.max(this.cfg.minCropBoxWidth, Math.min(this.cfg.maxCropBoxWidth, width));
            height = Math.max(this.cfg.minCropBoxHeight, Math.min(this.cfg.maxCropBoxHeight, height));

            this.cropping = true;
            this.cropper.setCropBoxData({
                width : width,
                height : height
            });
        }

        // set the new box coordinates
        var cropCoords = event.detail.x + "_" + event.detail.y + "_" + width + "_" + height;
        this.jqCoords.val(cropCoords);
        this.cropping = false;
    },

    /**
     * Reset the image and crop box to their initial states.
     */
    reset: function() {
        if (this.cropper) {
            this.cropper.reset();
        }
    },

    /**
     * Clears the crop box.
     */
    clear: function() {
        if (this.cropper) {
            this.cropper.clear();
        }
    },

    /**
     * Enables (unfreezes) the cropper.
     */
    enable: function() {
        if (this.cropper) {
            this.cropper.enable();
        }
    },

    /**
     * Disables (freezes) the cropper.
     */
    disable: function() {
        if (this.cropper) {
            this.cropper.disable();
        }
    }
});