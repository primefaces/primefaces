/**
 * PrimeFaces OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
    
    //configuration
    this.cfg.my = this.cfg.my||'left top';
    this.cfg.at = this.cfg.at||'left bottom';
    this.cfg.showEvent = this.cfg.showEvent||'mousedown';
    this.cfg.hideEvent = this.cfg.hideEvent||'mousedown';
    
    this.bindEvents();
    
    if(this.cfg.appendToBody) {
        this.jq.appendTo(document.body);
    }
    
    //ie7 width auto fix
    if($.browser.msie && parseInt($.browser.version, 10) == 7) {
        this.jq.width(300);
    }
    
    //replace visibility hidden with display none
    this.jq.css({
        'display':'none'
        ,'visibility':'visible'
    })
            
    //dialog support
    this.setupDialogSupport();

    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.OverlayPanel, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.OverlayPanel.prototype.bindEvents = function() {
    var _self = this;
    
    //mark target and descandants of target as a trigger for a primefaces overlay
    this.target.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
    
    //show and hide events for target
    if(this.cfg.showEvent == this.cfg.hideEvent) {
        this.target.bind(this.cfg.showEvent + '.ui-overlay', function(e) {
            if(_self.jq.is(':hidden')) {
                _self.show();
            } else {
                _self.hide();
            }
        })
    }
    else {
        this.target.bind(this.cfg.showEvent + '.ui-overlay', function(e) {
            if(_self.jq.is(':hidden')) {
                _self.show();
            }
        })
        .bind(this.cfg.hideEvent + '.ui-overlay', function(e) {
            if(_self.jq.is(':visible')) {
                _self.hide();
            }
        });
    }
    
    //hide overlay when mousedown is at outside of overlay
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
    if(!this.loaded && this.cfg.dynamic) {
        this.loadContents();
    }
    else {
        this._show();
    }
}

PrimeFaces.widget.OverlayPanel.prototype._show = function() {
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
    
    this.jq.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex})
            .position({
                my: this.cfg.my
                ,at: this.cfg.at
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
}

PrimeFaces.widget.OverlayPanel.prototype.postHide = function() {
    if(this.cfg.onHide) {
        this.cfg.onHide.call(this);
    }
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

PrimeFaces.widget.OverlayPanel.prototype.loadContents = function() {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id
    },
    _self = this;

    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                _self.jq.html(content);
                _self.loaded = true;
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }

        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
        
        return true;
    };
    
    options.oncomplete = function() {
        _self._show();
    };

    var params = [];
    params[this.id + '_contentLoad'] = true;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}