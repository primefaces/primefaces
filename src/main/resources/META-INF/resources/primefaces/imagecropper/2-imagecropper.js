/**
 * PrimeFaces ImageCropper Widget
 */
PrimeFaces.widget.ImageCropper = PrimeFaces.widget.DeferredWidget.extend({

    init : function (cfg) {
        this._super(cfg);
        this.image = $(PrimeFaces.escapeClientId(this.cfg.image));
        this.jqCoords = $(this.jqId + '_coords');

        // calculate the min and max of the cropper box
        this.cfg.minCropBoxWidth = this.cfg.minSize ? this.cfg.minSize[0] : 0;
        this.cfg.minCropBoxHeight = this.cfg.minSize ? this.cfg.minSize[1] : 0;
        this.cfg.maxCropBoxWidth = this.cfg.maxSize ? this.cfg.maxSize[0] : this.image.width();
        this.cfg.maxCropBoxHeight = this.cfg.maxSize ? this.cfg.maxSize[1] : this.image.height();

        this.cfg.data = {
            width : (this.cfg.minCropBoxWidth + this.cfg.maxCropBoxWidth) / 2,
            height : (this.cfg.minCropBoxHeight + this.cfg.maxCropBoxHeight) / 2,
        };

        this.renderDeferred();
    },

    _render : function () {
        var $this = this;

        // initialize the cropper
        this.image.cropper(this.cfg);

        // update coordinates as the box is adjusted
        this.image.on('crop', function (event) {
            $this.onCrop(event)
        });

        // set the initial coordinates
        if (this.cfg.initialCoords) {
            this.image.on('ready', function () {
                this.cropper.setCropBoxData({
                    left : $this.cfg.initialCoords[0],
                    top : $this.cfg.initialCoords[1],
                    width : $this.cfg.initialCoords[2],
                    height : $this.cfg.initialCoords[3]
                });
            });
        }

        // Get the Cropper.js instance after initialized
        this.cropper = this.image.data('cropper');
    },

    //@Override
    destroy: function() {
        this._super();

        // clean up memory
        if (this.cropper) {
            this.cropper.destroy();
        }
    },

    onCrop : function (event) {
        var width = event.detail.width;
        var height = event.detail.height;

        // constrain the box if necessary
        if (width < this.cfg.minCropBoxWidth
                || height < this.cfg.minCropBoxHeight
                || width > this.cfg.maxCropBoxWidth
                || height > this.cfg.maxCropBoxHeight) {

            width = Math.max(this.cfg.minCropBoxWidth, Math.min(this.cfg.maxCropBoxWidth, width));
            height = Math.max(this.cfg.minCropBoxHeight, Math.min(this.cfg.maxCropBoxHeight, height));

            this.cropper.setCropBoxData({
                width : width,
                height : height
            });
        }

        // set the new box coordinates
        var cropCoords = event.detail.x + "_" + event.detail.y + "_" + width + "_" + height;
        this.jqCoords.val(cropCoords);
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
     * Clear the crop box.
     */
    clear: function() {
        if (this.cropper) {
            this.cropper.clear();
        }
    },

    /**
     * Enable (unfreeze) the cropper.
     */
    enable: function() {
        if (this.cropper) {
            this.cropper.enable();
        }
    },

    /**
     * Disable (freeze) the cropper.
     */
    disable: function() {
        if (this.cropper) {
            this.cropper.disable();
        }
    }
});