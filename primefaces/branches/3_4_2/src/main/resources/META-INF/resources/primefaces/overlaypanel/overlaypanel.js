/**
 * PrimeFaces OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.targetId = PrimeFaces.escapeClientId(this.cfg.target);
        this.target = $(this.targetId);
    
        //configuration
        this.cfg.my = this.cfg.my||'left top';
        this.cfg.at = this.cfg.at||'left bottom';
        this.cfg.showEvent = this.cfg.showEvent||'click.ui-overlay';
        this.cfg.hideEvent = this.cfg.hideEvent||'click.ui-overlay';

        this.bindEvents();

        if(this.cfg.appendToBody) {
            this.jq.appendTo(document.body);
        }

        //dialog support
        this.setupDialogSupport();
    },
    
    bindEvents: function() {
        //mark target and descandants of target as a trigger for a primefaces overlay
        this.target.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);

        //show and hide events for target
        if(this.cfg.showEvent == this.cfg.hideEvent) {
            var event = this.cfg.showEvent;
            
            $(document).off(event, this.targetId).on(event, this.targetId, this, function(e) {
                e.data.toggle();
            });
        }
        else {
            var showEvent = this.cfg.showEvent + '.ui-overlay',
            hideEvent = this.cfg.hideEvent + '.ui-overlay';
            
            $(document).off(showEvent + ' ' + hideEvent, this.targetId).on(showEvent, this.targetId, this, function(e) {
                var _self = e.data;

                if(!_self.isVisible()) {
                    _self.show();
                }
            })
            .on(hideEvent, this.targetId, this, function(e) {
                var _self = e.data;

                if(_self.isVisible()) {
                    _self.hide();
                }
            });
        }
        
        //enter key support for mousedown event
        this.bindKeyEvents();
        
        var _self = this;

        //hide overlay when mousedown is at outside of overlay
        $(document.body).bind('mousedown.ui-overlay', function (e) {
            if(_self.jq.hasClass('ui-overlay-hidden')) {
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
            if(_self.jq.hasClass('ui-overlay-visible')) {
                _self.align();
            }
        });
    },
    
    bindKeyEvents: function() {
        $(document).off('keydown.ui-overlay keyup.ui-overlay', this.targetId).on('keydown.ui-overlay', this.targetId, this, function(e) {
            var keyCode = $.ui.keyCode, key = e.which;
            
            if(key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) {
                e.preventDefault();
            }
        })
        .on('keyup.ui-overlay', this.targetId, this, function(e) {
            var keyCode = $.ui.keyCode, key = e.which;
            
            if(key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) {
                e.data.toggle();
                e.preventDefault();
            }
        });
    },
    
    toggle: function() {
        if(!this.isVisible()) {
            this.show();
        } 
        else {
            this.hide();
        }
    },
    
    show: function() {
        if(!this.loaded && this.cfg.dynamic) {
            this.loadContents();
        }
        else {
            this._show();
        }
    },
    
    _show: function() {
        var _self = this;

        this.align();

        //replace visibility hidden with display none for effect support, toggle marker class
        this.jq.removeClass('ui-overlay-hidden').addClass('ui-overlay-visible').css({
            'display':'none'
            ,'visibility':'visible'
        });

        if(this.cfg.showEffect) {
            this.jq.show(this.cfg.showEffect, {}, 200, function() {
                _self.postShow();
            });
        }
        else {
            this.jq.show();
            this.postShow();
        }
    },
    
    align: function() {
        var fixedPosition = this.jq.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

        this.jq.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex})
                .position({
                    my: this.cfg.my
                    ,at: this.cfg.at
                    ,of: document.getElementById(this.cfg.target)
                    ,offset: positionOffset
                });
    },
    
    hide: function() {
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
    },
    
    postShow: function() {
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
        
        this.applyFocus();
    },
    
    postHide: function() {
        //replace display block with visibility hidden for hidden container support, toggle marker class
        this.jq.removeClass('ui-overlay-visible').addClass('ui-overlay-hidden').css({
            'display':'block'
            ,'visibility':'hidden'
        });
        
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }

        
    },
    
    setupDialogSupport: function() {
        var dialog = this.target.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            //set position as fixed to scroll with dialog
            this.jq.css('position', 'fixed');

            //append to body if not already appended by user choice
            if(!this.cfg.appendToBody) {
                this.jq.appendTo(document.body);
            }
        }
    },
    
    loadContents: function() {
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

        options.params = [
            {name: this.id + '_contentLoad', value: true}
        ];

        PrimeFaces.ajax.AjaxRequest(options);
    },
    
    isVisible: function() {
        return this.jq.hasClass('ui-overlay-visible');
    },
    
    applyFocus: function() {
        this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
    }

});