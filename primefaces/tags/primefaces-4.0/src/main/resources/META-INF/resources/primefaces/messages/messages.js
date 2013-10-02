/**
 * PrimeFaces Message Widget
 */
PrimeFaces.widget.Message = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        var text = this.jq.children('.ui-message-error-detail').text();
        
        if(text) {
           $(PrimeFaces.escapeClientId(this.cfg.target)).data('tooltip', text);
        }
    }
});