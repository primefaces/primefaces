/**
 * PrimeFaces Tooltip Widget
 */
PrimeFaces.widget.Tooltip = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    
    //remove previous element to support ajax updates
    $(document.body).children(this.jqId).remove();
    
    this.jq = $(this.jqId);
	this.cfg = cfg;
    this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
    
    //options
    this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent : 'mouseover';
    this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent : 'mouseout';
    this.cfg.showEffect = this.cfg.showEffect ? this.cfg.showEffect : 'fade';
    this.cfg.hideEffect = this.cfg.hideEffect ? this.cfg.hideEffect : 'fade';
    
    //bind tooltip to the target
    this.bindEvents();
	
	//append to body
    this.jq.appendTo(document.body);
    
    //use target title if value is blank
    if($.trim(this.jq.html()) == '') {
        this.jq.html(this.target.attr('title'));
    }
    
    //remove target's title
    this.target.removeAttr('title');

    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Tooltip, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Tooltip.prototype.bindEvents = function() {
    var _self = this;
    
    this.target.bind(this.cfg.showEvent, function() {
        _self.show();
    })
    .bind(this.cfg.hideEvent, function() {
        _self.hide();
    });
}

PrimeFaces.widget.Tooltip.prototype.show = function() {
    var _self = this;
    
    this.jq.css({
        left:'', 
        top:'',
        'z-index': ++PrimeFaces.zindex
    })
    .position({
        my: 'left top',
        at: 'right bottom',
        of: this.target
    });
    
    this.timeout = setTimeout(function() {
        _self.jq.show(_self.cfg.showEffect, {}, 400);
    }, 150);
}

PrimeFaces.widget.Tooltip.prototype.hide = function() {
    clearTimeout(this.timeout);
    
    this.jq.hide(this.cfg.hideEffect, {}, 400, function() {
        $(this).css('z-index', '');
    });
}