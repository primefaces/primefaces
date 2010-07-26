PrimeFaces.widget.Terminal = function(id, cfg) {
	this.clientId = PrimeFaces.escapeClientId(id);
	this.cfg = cfg;
	
	jQuery(this.clientId).wterm(this.cfg);
}