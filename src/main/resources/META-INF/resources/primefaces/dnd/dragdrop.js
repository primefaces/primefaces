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
	var droppable = this;
	
	this.cfg.drop = function(event, ui) {
		if(droppable.cfg.onDrop) {
			droppable.cfg.onDrop.call(this, event, ui);
		}
		
		var params = {};
		params[droppable.id] = droppable.id;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = droppable.id;
		params[droppable.id + "_dragId"] = ui.draggable.attr('id');
		params[droppable.id + "_dropId"] = droppable.cfg.target;
		
		if(droppable.cfg.onDropUpdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = droppable.cfg.onDropUpdate;
		}
		
		PrimeFaces.ajax.AjaxRequest(droppable.cfg.url, {
			formId: droppable.cfg.formId
		}, params);
	};
}