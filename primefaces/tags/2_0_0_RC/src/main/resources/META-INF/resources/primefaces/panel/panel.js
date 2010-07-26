if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Panel = function(id, cfg) {
	this.id = PrimeFaces.escapeClientId(id);
	this.bodySelector = this.id + "_bd";
	this.togglerSelector = this.id + "_toggler";
	this.stateHolder = this.id + "_state";
	this.cfg = cfg;
}

PrimeFaces.widget.Panel.prototype.toggle = function() {
	jQuery(this.bodySelector).slideToggle(this.cfg.toggleSpeed);
	
	if(this.cfg.collapsed) {
		jQuery(this.togglerSelector).addClass('pf-panel-toggler-expanded').removeClass('pf-panel-toggler-collapsed');
		this.cfg.collapsed = false;
		jQuery(this.stateHolder).val(0);
	}
	else {
		jQuery(this.togglerSelector).addClass('pf-panel-toggler-collapsed').removeClass('pf-panel-toggler-expanded');
		this.cfg.collapsed = true;
		jQuery(this.stateHolder).val(1);
	}
}