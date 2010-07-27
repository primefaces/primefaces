PrimeFaces.widget.AccordionPanel = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jqAcco = this.jqId + '_acco';
    this.stateHolder = this.jqId + '_active';
	
    jQuery(this.jqAcco).accordion(this.cfg);
	
    jQuery(this.jqAcco).bind('accordionchange', {acco:this}, this.onChange);
}

PrimeFaces.widget.AccordionPanel.prototype.onChange = function(event, ui) {
    var _self = event.data.acco;

    jQuery(_self.stateHolder).val(ui.options.active);
}

PrimeFaces.widget.AccordionPanel.prototype.select = function(index) {
    jQuery(this.jqAcco).accordion('activate', index);
}

PrimeFaces.widget.AccordionPanel.prototype.collapseAll = function() {
    jQuery(this.jqAcco).accordion('activate', 'false');
}