if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Terminal = function(id, cfg) {
	this.clientId = PrimeFaces.escapeClientId(id);
	this.cfg = cfg;
	
	jQuery(this.clientId).wterm(this.cfg);
}