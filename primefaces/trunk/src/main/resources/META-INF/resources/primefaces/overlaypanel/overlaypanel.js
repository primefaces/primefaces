/**
 * PrimeFaces OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
    this.onshowHandlers = [];
    
    this.bindEvents();
    
    if(this.cfg.appendToBody) {
        this.jq.appendTo(document.body);
    }
    
    this.jq.data('widget', this);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.OverlayPanel, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.OverlayPanel.prototype.bindEvents = function() {
    var _self = this;
    
    this.target.bind('click', function(e) {
        if(_self.jq.is(":hidden")) {
            _self.show();
        }
        
        e.stopPropagation();
    });
    
    //hide overlay when outside is clicked
    $(document.body).bind('click.ui-overlaypanel', function (e) {
        if(_self.jq.is(":hidden")) {
            return;
        }

        var offset = _self.jq.offset();
        if(e.pageX < offset.left ||
            e.pageX > offset.left + _self.jq.outerWidth() ||
            e.pageY < offset.top ||
            e.pageY > offset.top + _self.jq.outerHeight()) {
                        
            _self.hide();
        }
    });
    
    //Hide overlay on resize
    var resizeNS = 'resize.' + this.id;
    $(window).unbind(resizeNS).bind(resizeNS, function() {
        if(_self.jq.is(':visible')) {
            _self.hide();
        }
    });
}

PrimeFaces.widget.OverlayPanel.prototype.show = function() {
    var _self = this;
    
    this.jq.css({'left':'', 'top':'', 'z-Index': ++PrimeFaces.zindex})
            .position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.target
            });
            
    if(this.cfg.showEffect) {
        this.jq.show(this.cfg.showEffect, {}, 800, function() {
            _self.postShow();
        });
    }
    else {
        this.jq.show();
        this.postShow();
    }
}

PrimeFaces.widget.OverlayPanel.prototype.postShow = function() {
    //execute onshowHandlers and remove successful ones
    this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
		return !fn.call();
	});
}

PrimeFaces.widget.OverlayPanel.prototype.hide = function() {
    if(this.cfg.hideEffect)
        this.jq.hide(this.cfg.hideEffect);
    else
        this.jq.hide();
}

PrimeFaces.widget.OverlayPanel.prototype.addOnshowHandler = function(fn) {
    this.onshowHandlers.push(fn);
}