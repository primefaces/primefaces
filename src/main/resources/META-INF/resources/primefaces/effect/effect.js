/**
 * PrimeFaces Effect Widget
 */
PrimeFaces.widget.Effect = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
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
        this.source.bind(this.cfg.event, this.runner);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Effect, PrimeFaces.widget.BaseWidget);