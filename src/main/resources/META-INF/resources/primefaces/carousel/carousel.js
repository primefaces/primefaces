PrimeFaces.widget.Carousel = function(id, config) {
	PrimeFaces.widget.Carousel.superclass.constructor.call(this, id, config);
	
	this.pagerStateHolder = config.pagerStateHolder;
	this.selectedItemStateHolder = config.selectedItemStateHolder;
	
	this.addListener("afterScroll", this.savePageState);
	this.addListener("selectedItemChange", this.saveSelectedItem);
}

YAHOO.lang.extend(PrimeFaces.widget.Carousel, YAHOO.widget.Carousel,
{
	pagerStateHolder : null,
	
	selectedItemStateHolder : null,
	
	savePageState : function(evt) {
		document.getElementById(this.pagerStateHolder).value = evt.first;
	},

	saveSelectedItem : function(evt) {
		document.getElementById(this.selectedItemStateHolder).value = evt.newValue;
	}
});