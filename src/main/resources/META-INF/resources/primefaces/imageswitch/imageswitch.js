if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.ImageSwitch = function(clientId, cfg, images) {
	
	this.imgClientId = PrimeFaces.escapeClientId(clientId);
	this.cfg = cfg;
	this.images = images;
	this.imgIdx = 0;

	for(var i=0; i<images.length;i++) {
		jQuery.ImagePreload(images[i]);
	}
	
	if(this.cfg.slideshowAuto)
		this.startSlideshow();
}

PrimeFaces.widget.ImageSwitch.prototype.switchImage = function() {
	if(!jQuery(this.imgClientId).ImageAnimating()){  
        jQuery(this.imgClientId).ImageSwitch(
        						{
        							Type: this.cfg.effect,  
        							NewImage:this.images[this.imgIdx], 
        							Speed:this.cfg.speed  
                                });
    }
}

PrimeFaces.widget.ImageSwitch.prototype.startSlideshow = function() {
	var imageSwitch = this;
	this.animation = setInterval(
							function(){
								imageSwitch.next();
							}, this.cfg.slideshowSpeed);
}

PrimeFaces.widget.ImageSwitch.prototype.stopSlideshow = function() {
	clearInterval(this.animation); 
}

PrimeFaces.widget.ImageSwitch.prototype.next = function() {
	if(this.imgIdx == (this.images.length - 1))
		this.imgIdx = 0;
	else
		this.imgIdx++;
	
	this.switchImage();
}

PrimeFaces.widget.ImageSwitch.prototype.previous = function() {
	if(this.imgIdx == 0)
		this.imgIdx = this.images.length - 1;
	else
		this.imgIdx--;
	
	this.switchImage();
}