/**
 * PrimeFaces Mobile Dialog Widget
 */
PrimeFaces.widget.Dialog = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.content = this.jq.children('.ui-content');
        this.titlebar = this.jq.children('.ui-header');
        this.closeIcon = this.titlebar.children('.ui-icon-delete');
        
        this.jq.popup({
            positionTo: 'window',
            dismissible: false,
            overlayTheme: 'b'
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
        this.jq.removeClass('ui-dialog-container').popup('open', {transition:this.cfg.showEffect});
    },
    
    hide: function() {
        this.jq.popup('close');
    }
});