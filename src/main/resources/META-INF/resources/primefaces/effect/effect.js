/**
 * PrimeFaces Effect Widget
 */
PrimeFaces.widget.Effect = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);

        this.source = $(PrimeFaces.escapeClientId(this.cfg.source));
        var _self = this;

        this.runner = function() {
            //avoid queuing multiple runs
            if(_self.timeoutId) {
                clearTimeout(_self.timeoutId);
            }

            _self.timeoutId = setTimeout(_self.cfg.fn, _self.cfg.delay);
        };

        if(this.cfg.event == 'load') {
            this.runner.call();
        } 
        else {
            this.source.on(this.cfg.event, this.runner);
        }
    }
    
});