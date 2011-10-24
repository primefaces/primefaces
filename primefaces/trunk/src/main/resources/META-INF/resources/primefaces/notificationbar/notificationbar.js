PrimeFaces.widget.NotificationBar = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
	this.jq = $(this.jqId);
    var _self = this;
	
    //relocate
	this.jq.css(this.cfg.position, '0').appendTo($('body'));

    //display initially
	if(this.cfg.autoDisplay) {
		$(this.jq).css('display','block')
    }
    
    //bind events
    this.jq.children('.ui-notificationbar-close').click(function() {
        _self.hide();
    });
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.NotificationBar, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.NotificationBar.prototype.show = function() {
	if(this.cfg.effect === 'slide')
		$(this.jq).slideDown(this.cfg.effect);
	else if(this.cfg.effect === 'fade')
		$(this.jq).fadeIn(this.cfg.effect);
	else if(this.cfg.effect === 'none')
		$(this.jq).show();
}

PrimeFaces.widget.NotificationBar.prototype.hide = function() {
	if(this.cfg.effect === 'slide')
		$(this.jq).slideUp(this.cfg.effect);
	else if(this.cfg.effect === 'fade')
		$(this.jq).fadeOut(this.cfg.effect);
	else if(this.cfg.effect === 'none')
		$(this.jq).hide();
}