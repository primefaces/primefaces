if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

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
	var xhrOptions = {partialSubmit:true, formClientId:this.cfg.formClientId};
	
	if(this.cfg.update != undefined) {
		PrimeFaces.ajax.AjaxRequest(this.cfg.actionURL, xhrOptions, "update=" + this.cfg.update + "&dragId=" + this.id + "&" + id + "=" + id);
	}
	else {
		PrimeFaces.ajax.AjaxRequest(this.cfg.actionURL, xhrOptions, "&dragId=" + this.id + "&" + id + "=" + id);
	}
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