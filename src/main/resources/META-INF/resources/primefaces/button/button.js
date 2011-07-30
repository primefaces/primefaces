/* 
 * CommandButton
 */
PrimeFaces.widget.CommandButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $(this.jqId);
	
	this.jq.button(this.cfg);
	
	if(this.jq.attr('title') === 'ui-button') {
        this.jq.removeAttr('title');
    }
}

PrimeFaces.widget.CommandButton.prototype.disable = function() {
    this.jq.button('disable');
}

PrimeFaces.widget.CommandButton.prototype.enable = function() {
    this.jq.button('enable');
}

/*
 * Button
 */
PrimeFaces.widget.Button = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);

	$(this.jqId).button(this.cfg);
    
    if(this.jq.attr('title') === 'ui-button') {
        this.jq.removeAttr('title');
    }
}