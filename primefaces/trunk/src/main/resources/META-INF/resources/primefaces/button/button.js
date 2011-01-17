/* 
 * CommandButton
 */
PrimeFaces.widget.CommandButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = jQuery(this.jqId);
	
	this.jq.button(this.cfg);
	
	//firefox focus fix
	this.jq.mouseout(function() {
		jQuery(this).removeClass('ui-state-focus');
	});
}

PrimeFaces.widget.CommandButton.prototype.disable = function() {
    this.jq.button('disable');
}

PrimeFaces.widget.CommandButton.prototype.enable = function() {
    this.jq.button('enable');
}

/* 
 * LinkButton
 * @deprecated
 */
PrimeFaces.widget.LinkButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	
	jQuery(this.jqId).button(this.cfg);
}

/*
 * Button
 */
PrimeFaces.widget.Button = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);

	jQuery(this.jqId).button(this.cfg);
}