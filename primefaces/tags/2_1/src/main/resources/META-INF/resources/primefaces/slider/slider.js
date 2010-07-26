PrimeFaces.widget.Slider = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.jqInput = PrimeFaces.escapeClientId(this.cfg.input);
	if(this.cfg.output) {
		this.jqOutput = PrimeFaces.escapeClientId(this.cfg.output);
	}
	
	jQuery(this.jqId).slider(this.cfg);
	
	jQuery(this.jqId).bind("slide", {slider:this}, this.onSlide);
}

PrimeFaces.widget.Slider.prototype.onSlide = function(event, ui) {
	var s = event.data.slider;
	
	jQuery(s.jqInput).val(ui.value);
	
	if(s.jqOutput) {
		jQuery(s.jqOutput).html(ui.value);
	}
}