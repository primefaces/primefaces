PrimeFaces.widget.AjaxStatus = function(clientId) {
	this.clientId = PrimeFaces.escapeClientId(clientId);
}

PrimeFaces.widget.AjaxStatus.prototype.bindFacet = function(eventName, facetToShow) {
	var childrenSelector = this.clientId + " div";
	jQuery(document).bind(eventName, function() {
		jQuery(childrenSelector).hide();
	
		jQuery(PrimeFaces.escapeClientId(facetToShow)).show();
	});
}

PrimeFaces.widget.AjaxStatus.prototype.bindCallback = function(eventName, fn) {
	jQuery(document).bind(eventName, fn);
}