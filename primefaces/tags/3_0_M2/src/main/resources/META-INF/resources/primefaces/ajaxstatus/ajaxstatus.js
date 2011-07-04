PrimeFaces.widget.AjaxStatus = function(id) {
    this.id = id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
}

PrimeFaces.widget.AjaxStatus.prototype.bindFacet = function(eventName, facetToShow) {
    var _self = this;
	jQuery(document).bind(eventName, function() {
		jQuery(_self.jqId).children().hide();
	
		jQuery(_self.jqId + '_' + facetToShow).show();
	});
}

PrimeFaces.widget.AjaxStatus.prototype.bindCallback = function(eventName, fn) {
	jQuery(document).bind(eventName, fn);
}