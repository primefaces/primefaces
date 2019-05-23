/**
 * PrimeFaces ImageCropper Widget
 */
 PrimeFaces.widget.ImageCropper = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.image = $(PrimeFaces.escapeClientId(this.cfg.image));
        this.jqCoords = $(this.jqId + '_coords');

        var $this = this;
        this.cfg.onSelect = function(c) {$this.saveCoords(c);};
        this.cfg.onChange = function(c) {$this.saveCoords(c);};

        this.renderDeferred();
    },
    
    _render: function() {
        this.image.Jcrop(this.cfg);
    },
    
    saveCoords: function(c) {
        var cropCoords = c.x + "_" + c.y + "_" + c.w + "_" + c.h;

        this.jqCoords.val(cropCoords);
    }
    
});