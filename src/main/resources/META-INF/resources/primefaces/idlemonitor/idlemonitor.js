PrimeFaces.widget.IdleMonitor = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;

    var _self = this;
	
    jQuery(document).bind("idle.idleTimer", function(){

        if(_self.cfg.onidle) {
            _self.cfg.onidle.call(_self);
        }
		
        if(_self.cfg.hasIdleListener) {
            var options = {
                source: _self.id,
                process: _self.id,
                formId: _self.cfg.formId
            };

            if(_self.cfg.update) {
                options.update = _self.cfg.update;
            }

            var params = {};
            params[_self.id] = _self.id;

            PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
        }
    });
	
    jQuery(document).bind("active.idleTimer", function(){
        if(_self.cfg.onactive) {
            _self.cfg.onactive.call(this);
        }
    });
    
    jQuery.idleTimer(this.cfg.timeout);
}