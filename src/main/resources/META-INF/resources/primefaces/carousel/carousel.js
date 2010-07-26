PrimeFaces.widget.Carousel = function(id, cfg) {
	PrimeFaces.widget.Carousel.superclass.constructor.call(this, id + "_container", cfg);
	
	this.id = id;
	this.cfg = cfg;
	
	this.addListener("afterScroll", this.savePageState, this, true);
	this.addListener("selectedItemChange", this.saveSelectedItem, this, true);
}

YAHOO.lang.extend(PrimeFaces.widget.Carousel, YAHOO.widget.Carousel,
{	
	savePageState : function(e) {
		document.getElementById(this.id + "_first").value = e.first;
	},

	saveSelectedItem : function(e) {
		document.getElementById(this.id + "_selected").value = e.newValue;
	}
});