/**
 * PrimeFaces Tooltip Widget
 */
PrimeFaces.widget.Tooltip = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent + '.tooltip' : 'mouseover.tooltip';
        this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent + '.tooltip' : 'mouseout.tooltip';
        this.cfg.showEffect = this.cfg.showEffect ? this.cfg.showEffect : 'fade';
        this.cfg.hideEffect = this.cfg.hideEffect ? this.cfg.hideEffect : 'fade';
        this.cfg.showDelay = this.cfg.showDelay||150;
        this.cfg.hideDelay = this.cfg.hideDelay||0;
        this.cfg.hideEffectDuration = this.cfg.target ? 250 : 1;
        
        if(this.cfg.target)
            this.bindTarget();
        else
            this.bindGlobal();
        
        this.removeScriptElement(this.id);
    },
    
    refresh: function(cfg) {
        if(cfg.target) {
            if($(PrimeFaces.escapeClientId(cfg.id)).length > 1) 
                $(document.body).children(PrimeFaces.escapeClientId(cfg.id)).remove();
        }
        else {
            $(document.body).children('.ui-tooltip-global').remove();
        }
        
        this._super(cfg);
    },
    
    bindGlobal: function() {
        this.jq = $('<div class="ui-tooltip ui-tooltip-global ui-widget ui-widget-content ui-corner-all ui-shadow" />').appendTo('body');
        this.cfg.globalSelector = this.cfg.globalSelector||'a,:input,:button';
        this.cfg.escape = (this.cfg.escape === undefined) ? true : this.cfg.escape;
        var $this = this;
        
        $(document).off(this.cfg.showEvent + ' ' + this.cfg.hideEvent, this.cfg.globalSelector)
                    .on(this.cfg.showEvent, this.cfg.globalSelector, function(e) {
                        var element = $(this);
                        if(element.prop('disabled')) {
                            return;
                        }
                        
                        if($this.cfg.trackMouse) {
                            $this.mouseEvent = e;
                        }
                
                        var title = element.attr('title');
                        if(title) {
                            element.data('tooltip', title).removeAttr('title');
                        }
                        
                        if(element.hasClass('ui-state-error')) {
                            $this.jq.addClass('ui-state-error');
                        }

                        var text = element.data('tooltip');
                        if(text) {
                            if($this.cfg.escape)
                                $this.jq.text(text);
                            else
                                $this.jq.html(text);
                                
                            $this.globalTitle = text;
                            $this.target = element;
                            $this.show();
                        }
                    })
                    .on(this.cfg.hideEvent + '.tooltip', this.cfg.globalSelector, function() {
                        if($this.globalTitle) {
                            $this.hide();
                            $this.globalTitle = null;
                            $this.target = null;
                            $this.jq.removeClass('ui-state-error');
                        }
                    });
                    
        var resizeNS = 'resize.tooltip';
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
            }
        });
    },
    
    bindTarget: function() {
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(this.jqId);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
        
        var $this = this;
        this.target.off(this.cfg.showEvent + ' ' + this.cfg.hideEvent)
                    .on(this.cfg.showEvent, function(e) {
                        if($this.cfg.trackMouse) {
                            $this.mouseEvent = e;
                        }
                        
                        var text = $.trim($this.jq.text());
                        if($this.jq.children().length > 0 || text !== '') {
                            $this.show();
                        }
                    })
                    .on(this.cfg.hideEvent + '.tooltip', function() {
                        $this.hide();
                    });

        this.jq.appendTo(document.body);

        if($.trim(this.jq.html()) === '') {
            this.jq.html(this.target.attr('title'));
        }

        this.target.removeAttr('title');
        
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
            }
        });
    },

    align: function() {
         this.jq.css({
            left:'', 
            top:'',
            'z-index': ++PrimeFaces.zindex
        });
        
        if(this.cfg.trackMouse && this.mouseEvent) {
            this.jq.position({
                my: 'left top+15',
                at: 'right bottom',
                of: this.mouseEvent,
                collision: 'flipfit'
            });
            
            this.mouseEvent = null;
        }
        else {
            this.jq.position({
                my: 'left top',
                at: 'right bottom',
                of: this.target,
                collision: 'flipfit'
            });
        }
    },
    
    show: function() {
        if(this.target) {
            var $this = this;
            this.clearTimeout();

            this.timeout = setTimeout(function() {
                $this._show();
            }, this.cfg.showDelay);
        }
    },
    
    _show: function() {
        var $this = this;
        
        if(this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this);
            if(retVal === false) {
                return;
            }
        }
        
        this.align();
        if(this.cfg.trackMouse) {
            this.followMouse();
        }
        this.jq.show(this.cfg.showEffect, {}, 250, function() {
            if($this.cfg.onShow) {
                $this.cfg.onShow.call();
            }
        });
    },
    
    hide: function() {
        var $this = this;
        this.clearTimeout();

        if(this.cfg.hideDelay) {
            this.timeout = setTimeout(function() {
                $this._hide();
            }, this.cfg.hideDelay);
        }
        else {
            this._hide();
        }
    },
    
    _hide: function() {
        var $this = this;
        
        if(this.isVisible()) {
            this.jq.hide(this.cfg.hideEffect, {}, this.cfg.hideEffectDuration, function() {
                $(this).css('z-index', '');
                if($this.cfg.trackMouse) {
                    $this.unfollowMouse();
                }

                if($this.cfg.onHide) {
                    $this.cfg.onHide.call();
                }
            });
        }
    },
    
    clearTimeout: function() {
        if(this.timeout) {
            clearTimeout(this.timeout);
        }
    },
    
    followMouse: function() {
        var $this = this;
        
        this.target.on('mousemove.tooltip-track', function(e) {
            $this.jq.position({
                my: 'left top+15',
                at: 'right bottom',
                of: e,
                collision: 'flipfit'
            });
        });       
    },
    
    unfollowMouse: function() {
        this.target.off('mousemove.tooltip-track'); 
    },
    
    isVisible: function() {
        return this.jq.is(':visible');
    }
    
});