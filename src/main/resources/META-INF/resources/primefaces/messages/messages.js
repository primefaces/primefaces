/**
 * PrimeFaces Message Widget
 */
PrimeFaces.widget.Message = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        var text = this.jq.children('.ui-message-error-detail').text();
        
        if(text) {
           var target = $(PrimeFaces.escapeClientId(this.cfg.target));
           
           if (this.cfg.tooltip) {
              target.data('tooltip', text);
           }
           
           target.attr('aria-describedby', this.id + '_error-detail');
        } 
   }
});