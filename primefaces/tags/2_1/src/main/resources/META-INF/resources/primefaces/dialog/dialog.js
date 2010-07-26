PrimeFaces.widget.Dialog = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	
	jQuery(this.jqId).dialog(this.cfg);	
	
	if(this.cfg.ajaxClose) {
		jQuery(this.jqId).bind('dialogclose', {dialog: this}, this.triggerCloseEvent);
	}
	
	if(this.cfg.closable == false) {
		jQuery(this.jqId).parent().find('.ui-dialog-titlebar-close').hide();
	}
}

PrimeFaces.widget.Dialog.prototype.show = function() {
	jQuery(this.jqId).dialog('open');	
}

PrimeFaces.widget.Dialog.prototype.hide = function() {
	jQuery(this.jqId).dialog('close');
}

PrimeFaces.widget.Dialog.prototype.triggerCloseEvent = function(event, ui) {
	var d = event.data.dialog,
	params = {};
	params[d.id + "_closed"] = true;
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = d.id;
	
	if(d.cfg.onCloseUpdate) {
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = d.cfg.onCloseUpdate;
	}
	
	PrimeFaces.ajax.AjaxRequest(d.cfg.url, {}, params);
}