PrimeFaces.widget.AjaxStatus = function(clientId) {
	this.clientId = PrimeFaces.escapeClientId(clientId);
}

PrimeFaces.widget.AjaxStatus.prototype.bindFacet = function(eventName, facetToShow) {
	jQuery(this.clientId).bind(eventName, function() {
		jQuery(this).children().hide();
	
		jQuery(PrimeFaces.escapeClientId(facetToShow)).show();
	});
}

PrimeFaces.widget.AjaxStatus.prototype.bindCallback = function(eventName, fn) {
	jQuery(document).bind(eventName, fn);
}