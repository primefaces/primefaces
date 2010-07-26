PrimeFaces.widget.ConfirmDialog = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	
	this.cfg.resizable = false;
	this.cfg.autoOpen = false;
	
	jQuery(this.jqId).dialog(this.cfg);
	
	//Setup button pane
	var buttons = this.jqId + '_buttons';
	jQuery(buttons).appendTo(jQuery(buttons).parent().parent()).addClass('ui-dialog-buttonpane ui-widget-content ui-helper-clearfix');
	
	//Close icon
	if(this.cfg.closable == false) {
		jQuery(this.jqId).parent().find('.ui-dialog-titlebar-close').hide();
	}

}

PrimeFaces.widget.ConfirmDialog.prototype.show = function() {
	jQuery(this.jqId).dialog('open');	
}

PrimeFaces.widget.ConfirmDialog.prototype.hide = function() {
	jQuery(this.jqId).dialog('close');
}