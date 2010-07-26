PrimeFaces.widget.Tooltip = function(options) {
	this.cfg = options;
	var target = "";
	
	if(options.global) {
		target = "*[title]";
	}else {
		target = PrimeFaces.escapeClientId(options.forComponent);
	}
	
	jQuery(target).qtip(this.cfg);
}