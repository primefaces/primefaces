/**
 * PrimeFaces Mobile Dialog Widget
 */
PrimeFaces.widget.Dialog = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.popupElement = this.jq.children('.ui-popup');
        this.mask = this.jq.prev('.ui-popup-screen');
        this.content = this.popupElement.children('.ui-content');
        this.header = this.popupElement.children('.ui-header');
        this.closeIcon = this.header.children('.ui-icon-delete');
        
        //cleanup duplicate masks due to ajax update
        var orphanMask = this.mask.prev('.ui-popup-screen');
        if(orphanMask.length) {
            orphanMask.remove();
        } 
        
        this.popupElement.popup({
            positionTo: 'window',
            dismissible: false,
            overlayTheme: 'b',
            enhanced: true
        });
    
        this.bindEvents();
    },
        
    bindEvents: function() {
        var $this = this;
        
        this.closeIcon.on('click', function(e) {
            $this.hide();
            e.preventDefault();
        });
    },
    
    show: function() {
        this.popupElement.popup('open', {transition:this.cfg.showEffect});
    },
    
    hide: function() {
        this.popupElement.popup('close');
    }
});