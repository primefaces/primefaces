PrimeFaces.widget.NotificationBar = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jq = PrimeFaces.escapeClientId(this.id);
	
	jQuery(this.jq).css(this.cfg.position,'0');
	
	jQuery(this.jq).appendTo(jQuery('body'));
	
	if(this.cfg.autoDisplay)
		jQuery(this.jq).css({'display':'block'});
}

PrimeFaces.widget.NotificationBar.prototype.show = function() {
	if(this.cfg.effect === "slide")
		jQuery(this.jq).slideDown(this.cfg.effect);
	else if(this.cfg.effect === "fade")
		jQuery(this.jq).fadeIn(this.cfg.effect);
	else if(this.cfg.effect === "none")
		jQuery(this.jq).show();
}

PrimeFaces.widget.NotificationBar.prototype.hide = function() {
	if(this.cfg.effect === "slide")
		jQuery(this.jq).slideUp(this.cfg.effect);
	else if(this.cfg.effect === "fade")
		jQuery(this.jq).fadeOut(this.cfg.effect);
	else if(this.cfg.effect === "none")
		jQuery(this.jq).hide();
}