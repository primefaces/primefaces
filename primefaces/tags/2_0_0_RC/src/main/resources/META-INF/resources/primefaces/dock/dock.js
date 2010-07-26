if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Dock = function(id, cfg) {
	var elId = PrimeFaces.escapeClientId(id);
	jQuery(elId).Fisheye(cfg);
}