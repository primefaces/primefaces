/**
 * PrimeFaces Draggable Widget
 */
PrimeFaces.widget.Draggable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(PrimeFaces.escapeClientId(this.cfg.target));
	
    this.jq.draggable(this.cfg);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Draggable, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Draggable.prototype.getScriptTag = function() {
    return $(this.jqId + '_script');
}

/**
 * PrimeFaces Droppable Widget
 */
PrimeFaces.widget.Droppable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(PrimeFaces.escapeClientId(this.cfg.target));
	
    this.bindDropListener();
    	
    this.jq.droppable(this.cfg);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Droppable, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Droppable.prototype.getScriptTag = function() {
    return $(this.jqId + '_script');
}

PrimeFaces.widget.Droppable.prototype.bindDropListener = function() {
    var _self = this;
    
    this.cfg.drop = function(event, ui) {
        if(_self.cfg.onDrop) {
            _self.cfg.onDrop.call(_self, event, ui);
        }
        if(_self.cfg.behaviors) {
            var dropBehavior = _self.cfg.behaviors['drop'];

            if(dropBehavior) {
                var ext = {
                    params: {}
                };
                ext.params[_self.id + "_dragId"] = ui.draggable.attr('id');
                ext.params[_self.id + "_dropId"] = _self.cfg.target;
                
                dropBehavior.call(_self, event, ext);
            }
        }
    };
}