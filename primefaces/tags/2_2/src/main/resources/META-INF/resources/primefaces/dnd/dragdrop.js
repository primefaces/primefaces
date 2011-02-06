PrimeFaces.widget.Draggable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
	
    jQuery(PrimeFaces.escapeClientId(this.cfg.target)).draggable(this.cfg);
}

PrimeFaces.widget.Droppable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
	
    this.setupDropHandler();
	
    jQuery(PrimeFaces.escapeClientId(this.cfg.target)).droppable(this.cfg);
}

PrimeFaces.widget.Droppable.prototype.setupDropHandler = function() {
    var _self = this;
	
    this.cfg.drop = function(event, ui) {
        if(_self.cfg.onDrop) {
            _self.cfg.onDrop.call(this, event, ui);
        }

        var options = {
            source: _self.id,
            process: _self.id,
            formId: _self.cfg.formId
        };

        if(_self.cfg.onDropUpdate) {
            options.update = _self.cfg.onDropUpdate;
        }
	
        var params = {};
        params[_self.id + "_dragId"] = ui.draggable.attr('id');
        params[_self.id + "_dropId"] = _self.cfg.target;
		
        PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
    };
}