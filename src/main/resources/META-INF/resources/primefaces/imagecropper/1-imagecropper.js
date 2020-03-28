/**
 * __PrimeFaces ImageCropper Widget__
 * 
 * ImageCropper allows cropping a certain region of an image. A new image is created containing the cropped area and
 * assigned to a `CroppedImage` instanced on the server side. Uses Jcrop, to interact with the image croper
 * programatically, use the Jcrop JQuery plugin. For example:
 * 
 * ```javascript
 * PF("myImageCropperWidget").image.Jcrop("animateTo", [20, 20, 400, 400]);
 * ```
 * 
 * @prop {JQuery} image DOM element of the image element to crop. You can use this element to access the
 * Jcrop.
 * @prop {JQuery} jqCoords DOM element of the hidden INPUT element that stores the selected crop area.
 * 
 * @interface {PrimeFaces.widget.ImageCropperCfg} cfg The configuration for the {@link  ImageCropper| ImageCropper widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * @extends {JQueryJcrop.Configuration} cfg
 * 
 * @prop {string} cfg.image ID of the IMAGE element.
 */
 PrimeFaces.widget.ImageCropper = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        
        this.image = $(PrimeFaces.escapeClientId(this.cfg.image));
        this.jqCoords = $(this.jqId + '_coords');

        var $this = this;
        this.cfg.onSelect = function(c) {$this.saveCoords(c);};
        this.cfg.onChange = function(c) {$this.saveCoords(c);};

        this.renderDeferred();
    },
    
    /**
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.image.Jcrop(this.cfg);
    },
    
    /**
     * Callback for when the selected crop area changes. Saves the crop area to the hidden input field so that it can
     * be transmitted to the server.
     * @param {JQueryJcrop.OffsetAreaWithBottomLeft} c The selected crop area to save.
     * @private
     */
    saveCoords: function(c) {
        var cropCoords = c.x + "_" + c.y + "_" + c.w + "_" + c.h;

        this.jqCoords.val(cropCoords);
    }
    
});