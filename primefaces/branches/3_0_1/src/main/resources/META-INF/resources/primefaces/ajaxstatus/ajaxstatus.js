PrimeFaces.widget.AjaxStatus = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.AjaxStatus, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.AjaxStatus.prototype.bindFacet = function(eventName, facetToShow) {
    var _self = this;
    
	$(document).bind(eventName, function() {
		$(_self.jqId).children().hide();
	
		$(_self.jqId + '_' + facetToShow).show();
	});
}

PrimeFaces.widget.AjaxStatus.prototype.bindCallback = function(eventName, fn) {
	$(document).bind(eventName, fn);
}