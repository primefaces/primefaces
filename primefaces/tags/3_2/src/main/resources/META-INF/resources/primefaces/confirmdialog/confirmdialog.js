/**
 * PrimeFaces ConfirmDialog Widget
 */
PrimeFaces.widget.ConfirmDialog = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
	this.jq = jQuery(this.jqId);
	this.cfg.resizable = false;
	this.cfg.autoOpen = false;

    //Remove scripts to prevent duplicate widget issues
    this.jq.find("script").remove();

    //Create dialog
	this.jq.dialog(this.cfg);
	
	//Setup button pane
	var buttons = jQuery(this.jqId + '_buttons');
	buttons.appendTo(buttons.parent().parent()).addClass('ui-dialog-buttonpane ui-widget-content ui-helper-clearfix');
	
	//Close icon
	if(this.cfg.closable == false) {
		jQuery(this.jqId).parent().find('.ui-dialog-titlebar-close').hide();
	}

    if(this.cfg.appendToBody) {
        this.jq.parent().appendTo(document.body);
    }

}

PrimeFaces.widget.ConfirmDialog.prototype.show = function() {
	this.jq.dialog('open');
}

PrimeFaces.widget.ConfirmDialog.prototype.hide = function() {
	this.jq.dialog('close');
}