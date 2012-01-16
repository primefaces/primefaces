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
    
    //ie7 width auto fix
    if($.browser.msie && parseInt($.browser.version, 10) == 7) {
        this.jq.width(300);
    }
    
    this.jq.data('widget', this);
            
    //dialog support
    this.setupDialogSupport();

    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.OverlayPanel, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.OverlayPanel.prototype.bindEvents = function() {
    var _self = this;
    
    this.target.bind('mousedown.ui-overlay', function(e) {
        if(_self.jq.is(":hidden")) {
            _self.show();
        } else {
            _self.hide();
        }
    });
    
    //hide overlay when outside is clicked
    $(document.body).bind('mousedown.ui-overlay', function (e) {
        if(_self.jq.is(":hidden")) {
            return;
        }
        
        //do nothing on target mousedown
        var target = $(e.target);
        if(_self.target.is(target)||_self.target.has(target).length > 0) {
            return;
        }

        //hide overlay if mousedown is on outside
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
    
    this.align();
    
    if(this.cfg.showEffect) {
        this.jq.show(this.cfg.showEffect, {}, 200, function() {
            _self.postShow();
        });
    }
    else {
        this.jq.show();
        this.postShow();
    }
}

PrimeFaces.widget.OverlayPanel.prototype.align = function() {
    var fixedPosition = this.jq.css('position') == 'fixed',
    win = $(window),
    positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;
    
    this.jq.css({'left':'', 'top':'', 'z-Index': ++PrimeFaces.zindex})
            .position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.target
                ,offset: positionOffset
            });
}

PrimeFaces.widget.OverlayPanel.prototype.hide = function() {
    var _self = this;
    
    if(this.cfg.hideEffect) {
        this.jq.hide(this.cfg.hideEffect, {}, 200, function() {
            _self.postHide();
        });
    }
    else {
        this.jq.hide();
        this.postHide();
    }
}

PrimeFaces.widget.OverlayPanel.prototype.postShow = function() {
    if(this.cfg.onShow) {
        this.cfg.onShow.call(this);
    }
    
    //execute onshowHandlers and remove successful ones
    this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
		return !fn.call();
	});
}

PrimeFaces.widget.OverlayPanel.prototype.postHide = function() {
    if(this.cfg.onHide) {
        this.cfg.onHide.call(this);
    }
}

PrimeFaces.widget.OverlayPanel.prototype.addOnshowHandler = function(fn) {
    this.onshowHandlers.push(fn);
}

PrimeFaces.widget.OverlayPanel.prototype.setupDialogSupport = function() {
    var dialog = this.target.parents('.ui-dialog:first');
    
    if(dialog.length == 1) {
        //set position as fixed to scroll with dialog
        this.jq.css('position', 'fixed');
        
        //append to body if not already appended by user choice
        if(!this.cfg.appendToBody) {
            this.jq.appendTo(document.body);
        }
    }
}