PrimeFaces.widget.AccordionPanel = function(id, cfg) {
	this.id = id;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.cfg = cfg;
	
	jQuery(this.jqId + '_acco').accordion(this.cfg);
	
	jQuery(this.jqId + '_acco').bind('accordionchange', {acco:this}, function(event, ui) {
		jQuery(event.data.acco.jqId + '_active').val(ui.options.active);
	});
}