if(PrimeFaces.touch == undefined) PrimeFaces.touch = {};

PrimeFaces.touch.Application = function(config) {
	this.cfg = config;
	/*this.cfg.preloadImages = [
		                         config.themePath + '/img/grayButton.png',
		                         config.themePath + '/img/whiteButton.png',
		                         config.themePath + '/img/loading.gif'
	                         ];*/
	
	this.cfg.slideSelector = '#touchfaces_primefaces';
	
    this.jqt = new jQuery.jQTouch(this.cfg);
}

PrimeFaces.touch.Application.prototype.goTo = function(viewId, effect) {
	this.jqt.goTo('#' + viewId, effect);
	
	jQuery('.active').removeClass('active');
}

PrimeFaces.touch.Application.prototype.goBack = function() {
	this.jqt.goBack();
}