PrimeFaces.widget.Draggable = function(clientId, cfg) {
	if(cfg.proxy)
		this.dd = new YAHOO.util.DDProxy(clientId);
	else
		this.dd = new YAHOO.util.DD(clientId);
	
	this.dd.cfg = cfg;
	
	if(!this.dd.cfg.dragOnly) {
		this.dd.onDragDrop = this.onDragDrop;
		this.dd.onInvalidDrop = this.onInvalidDrop;
		this.dd.position = YAHOO.util.Dom.getXY(clientId);
	}
}

PrimeFaces.widget.Draggable.prototype.onDragDrop = function(e, id) {
	var DOM = YAHOO.util.Dom;
	var targetPosition = DOM.getXY(id);
	
	new YAHOO.util.Motion(  
			this.id, {points: {  
							to: targetPosition
			                  } 
			                }, 0.3, YAHOO.util.Easing.easeOut).animate();
	
	this.position = targetPosition;
	
	//Send ajax request
	var xhrOptions = {formId:this.cfg.formId};
	
	var params = {};
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = id;
	params['dragId'] = this.id;
	params[id] = id;
	
	if(this.cfg.update) {
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.update;
	}
	
	PrimeFaces.ajax.AjaxRequest(this.cfg.actionURL, xhrOptions, params);
}

PrimeFaces.widget.Draggable.prototype.onInvalidDrop = function(e) {
	new YAHOO.util.Motion(  
				this.id, {points: {  
								to: this.position 
				                  } 
				                }, 0.3, YAHOO.util.Easing.easeOut).animate(); 
}

PrimeFaces.widget.Droppable = function(clientId, groupId, cfg) {
	PrimeFaces.widget.Droppable.superclass.constructor.call(this, clientId, groupId, cfg);
}

YAHOO.lang.extend(PrimeFaces.widget.Droppable, YAHOO.util.DDTarget,
{
	
});