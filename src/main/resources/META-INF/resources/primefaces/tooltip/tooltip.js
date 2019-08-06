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
        this.cfg.position = this.cfg.position||'right';

        if(this.cfg.target)
            this.bindTarget();
        else
            this.bindGlobal();

        this.removeScriptElement(this.id);
    },

    //@override
    refresh: function(cfg) {
        if(cfg.target) {
            var targetTooltip = $(document.body).children(PrimeFaces.escapeClientId(cfg.id));
            if(targetTooltip.length)
                targetTooltip.remove();
        }
        else {
            $(document.body).children('.ui-tooltip-global').remove();
        }

        this._super(cfg);
    },

    bindGlobal: function() {
        this.jq = $('<div class="ui-tooltip ui-tooltip-global ui-widget ui-tooltip-' + this.cfg.position + '" role="tooltip"></div>')
            .appendTo('body');
        this.jq.append('<div class="ui-tooltip-arrow"></div><div class="ui-tooltip-text ui-shadow ui-corner-all"></div>');

        this.jq.addClass(this.cfg.styleClass);
        
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

                        var arrow = $this.jq.children('.ui-tooltip-arrow');

                        if(element.hasClass('ui-state-error')) {
                            $this.jq.children('.ui-tooltip-text').addClass('ui-state-error');
                            arrow.addClass('ui-state-error');
                        }
                        else {
                            arrow.removeClass('ui-state-error');
                        }

                        var text = element.data('tooltip');
                        if(text) {
                            if($this.cfg.escape)
                                $this.jq.children('.ui-tooltip-text').text(text);
                            else
                                $this.jq.children('.ui-tooltip-text').html(text);

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
                            $this.jq.children('.ui-tooltip-text').removeClass('ui-state-error');
                        }
                    });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.tooltip' + '_align', $this.jq, function() {
            $this.align();
        });

    },

    bindTarget: function() {
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(this.jqId);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);

        var describedBy = this.target.attr("aria-describedby");
        if (!describedBy || 0 === describedBy.length) {
            describedBy = this.id;
        } else {
            describedBy += " " + this.id;
        }
        this.target.attr("aria-describedby", describedBy);

        var $this = this;
        if(this.cfg.delegate) {
            var targetSelector = "*[id='" + this.target.attr('id') + "']";

            $(document).off(this.cfg.showEvent + ' ' + this.cfg.hideEvent, targetSelector)
                        .on(this.cfg.showEvent, targetSelector, function(e) {
                            if($this.cfg.trackMouse) {
                                $this.mouseEvent = e;
                            }

                            if($.trim($this.jq.children('.ui-tooltip-text').html()) !== '') {
                                $this.show();
                            }
                        })
                        .on(this.cfg.hideEvent + '.tooltip', function() {
                            $this.hide();
                        });
        }
        else {
            this.target.off(this.cfg.showEvent + ' ' + this.cfg.hideEvent)
                        .on(this.cfg.showEvent, function(e) {
                            if($this.cfg.trackMouse) {
                                $this.mouseEvent = e;
                            }

                            if($.trim($this.jq.children('.ui-tooltip-text').html()) !== '') {
                                $this.show();
                            }
                        })
                        .on(this.cfg.hideEvent + '.tooltip', function() {
                            $this.hide();
                        });
        }

        this.jq.appendTo(document.body);

        if($.trim(this.jq.children('.ui-tooltip-text').html()) === '') {
            var text = this.target.attr('title');
            if(this.cfg.escape)
                this.jq.children('.ui-tooltip-text').text(text);
            else
                this.jq.children('.ui-tooltip-text').html(text);
        }

        this.target.removeAttr('title');


        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.jq, function() {
            $this.align();
        });
    },

    alignUsing: function(position,feedback) {
        this.jq.removeClass('ui-tooltip-left ui-tooltip-right ui-tooltip-top ui-tooltip-bottom');
        switch (this.cfg.position) {
        case "right":
        case "left":
            this.jq.addClass('ui-tooltip-'+
                    (feedback['horizontal']=='left'?'right':'left'));
            break;
        case "top":
        case "bottom":
            this.jq.addClass('ui-tooltip-'+
                    (feedback['vertical']=='top'?'bottom':'top'));
            break;
        }
        this.jq.css({
            left: position['left'],
            top: position['top']
        });
    },

    align: function() {
        var $this = this;
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
                collision: 'flipfit',
                using: function(p,f) {
                    $this.alignUsing.call($this,p,f);
                }
            });

            this.mouseEvent = null;
        }
        else {
            var _my, _at;

            switch(this.cfg.position) {
                case 'right':
                    _my = 'left center';
                    _at = 'right center';
                break;

                case 'left':
                    _my = 'right center';
                    _at = 'left center';
                break;

                case 'top':
                    _my = 'center bottom';
                    _at = 'center top';
                break;

                case 'bottom':
                    _my = 'center top';
                    _at = 'center bottom';
                break;
            }

            this.jq.position({
                my: _my,
                at: _at,
                of: this.getTarget(),
                collision: 'flipfit',
                using: function(p,f) {
                    $this.alignUsing.call($this,p,f);
                }
            });
        }
    },

    show: function() {
        if(this.getTarget()) {
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

        this.jq.css({'display':'block', 'opacity':0, 'pointer-events': 'none'});
    
        this.align();

        this.jq.css({'display':'none', 'opacity':'', 'pointer-events': ''});
        
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

        this.getTarget().on('mousemove.tooltip-track', function(e) {
            $this.jq.position({
                my: 'left top+15',
                at: 'right bottom',
                of: e,
                collision: 'flipfit'
            });
        });
    },

    unfollowMouse: function() {
        var target = this.getTarget();
        if(target) {
            target.off('mousemove.tooltip-track');
        }
    },

    isVisible: function() {
        return this.jq.is(':visible');
    },

    getTarget: function() {
        if(this.cfg.delegate)
            return PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
        else
            return this.target;
    }

});
