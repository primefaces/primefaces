/**
 * PrimeFaces Inplace Widget
 */
PrimeFaces.widget.Inplace = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.display = jQuery(this.jqId + '_display');
    this.content = jQuery(this.jqId + '_content');

    var _self = this;
	
	if(!cfg.disabled) {
		this.display.click(function(){
            _self.show();
        });
        
        this.display.mouseover(function(){
            jQuery(this).toggleClass("ui-state-highlight");
        }).mouseout(function(){
            jQuery(this).toggleClass("ui-state-highlight");
        });
	}
}

PrimeFaces.widget.Inplace.prototype.show = function() {
    this.toggle(this.content, this.display);
}

PrimeFaces.widget.Inplace.prototype.hide = function() {
    this.toggle(this.display, this.content);
}

PrimeFaces.widget.Inplace.prototype.toggle = function(elToShow, elToHide) {
    var _self = this;

    if(this.cfg.effect == 'fade') {
        elToHide.fadeOut(this.cfg.effectSpeed,
            function(){
                elToShow.fadeIn(_self.cfg.effectSpeed);
            });
    }
    else if(this.cfg.effect == 'slide') {
            elToHide.slideUp(this.cfg.effectSpeed,
                function(){
                    elToShow.slideDown(_self.cfg.effectSpeed);
            });
    }
    else if(this.cfg.effect == 'none') {
            elToHide.hide();
            elToShow.show();
    }
}