if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.AjaxStatus = function(id) {
	this.id = PrimeFaces.escapeClientId(id);
}

PrimeFaces.widget.AjaxStatus.prototype.bindFacet = function(eventName, facetToShow) {
	var childrenSelector = this.id + " div";
	jQuery().bind(eventName, function() {
		jQuery(childrenSelector).hide();
	
		jQuery(PrimeFaces.escapeClientId(facetToShow)).show();
	});
}

PrimeFaces.widget.AjaxStatus.prototype.bindCallback = function(eventName, fn) {
	jQuery().bind(eventName, fn);
}