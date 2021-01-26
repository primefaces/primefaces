/**
 * __PrimeFaces Tooltip Widget__
 * 
 * Tooltip goes beyond the legacy HTML title attribute by providing custom effects, events, HTML content and advance
 * theme support.
 * 
 * @typedef {"right" | "left" | "top" | "bottom"} PrimeFaces.widget.Tooltip.TooltipPosition Position of the tooltip,
 * relative to the target component.
 * 
 * @typedef PrimeFaces.widget.Tooltip.BeforeShowCallback Client side callback to execute before tooltip is  shown.
 * Returning false will prevent display. See also {@link TooltipCfg.beforeShow}.
 * @this {PrimeFaces.widget.Tooltip} PrimeFaces.widget.Tooltip.BeforeShowCallback 
 * @return {boolean} PrimeFaces.widget.Tooltip.BeforeShowCallback `true` to show the tooltip, or `false` to prevent it
 * from being shown.
 * 
 * @typedef PrimeFaces.widget.Tooltip.OnHideCallback Client side callback to execute after tooltip is shown. See also
 * {@link TooltipCfg.onHide}.
 * @this {PrimeFaces.widget.Tooltip} PrimeFaces.widget.Tooltip.OnHideCallback 
 * 
 * @typedef PrimeFaces.widget.Tooltip.OnShowCallback Client side callback to execute after tooltip is shown. See also
 * {@link TooltipCfg.onShow}.
 * @this {PrimeFaces.widget.Tooltip} PrimeFaces.widget.Tooltip.OnShowCallback 
 * 
 * @prop {string} globalTitle The text that is shown as the global title.
 * @prop {JQuery.TriggeredEvent} mouseEvent The mouse event that occurred for this tooltip.
 * @prop {JQuery} target The DOM element for the target component.
 * @prop {number} timeout The set-timeout timer ID of the time for the tooltip delay.
 * 
 * @interface {PrimeFaces.widget.TooltipCfg} cfg The configuration for the {@link  Tooltip| Tooltip widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {PrimeFaces.widget.Tooltip.BeforeShowCallback} cfg.beforeShow Client side callback to execute before tooltip is
 * shown. Returning false will prevent display.
 * @prop {string} cfg.delegate Search expression for overriding the {@link target}.
 * @prop {boolean} cfg.escape Defines whether HTML would be escaped or not.
 * @prop {string} cfg.globalSelector A jQuery selector for global tooltip, defaults to `a,:input,:button`.
 * @prop {number} cfg.hideDelay Delay time to hide tooltip in milliseconds.
 * @prop {string} cfg.hideEffect Effect to be used for hiding.
 * @prop {number} cfg.hideEffectDuration Delay time to hide tooltip in milliseconds.
 * @prop {string} cfg.hideEvent Event hiding the tooltip.
 * @prop {PrimeFaces.widget.Tooltip.OnHideCallback} cfg.onHide Client side callback to execute after tooltip is shown.
 * @prop {PrimeFaces.widget.Tooltip.OnShowCallback} cfg.onShow Client side callback to execute after tooltip is shown.
 * @prop {PrimeFaces.widget.Tooltip.TooltipPosition} cfg.position Position of the tooltip.
 * @prop {number} cfg.showDelay Delay time to show tooltip in milliseconds.
 * @prop {string} cfg.showEffect Effect to be used for displaying.
 * @prop {string} cfg.showEvent Event displaying the tooltip. 
 * @prop {string} cfg.styleClass Style class of the tooltip.
 * @prop {string} cfg.myPos Position of tooltip with respect to target. If set overrides the 'position' attribute.
 * @prop {string} cfg.atPos Position of tooltip with respect to target. If set overrides the 'position' attribute.
 * @prop {string} cfg.target Search expression for the component to which the tooltip is attached.
 * @prop {boolean} cfg.trackMouse Whether the tooltip position should follow the mouse or pointer.
 */
PrimeFaces.widget.Tooltip = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent + '.tooltip' : 'mouseover.tooltip';
        this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent + '.tooltip' : 'mouseout.tooltip';
        this.cfg.showEffect = this.cfg.showEffect ? this.cfg.showEffect : 'fade';
        this.cfg.hideEffect = this.cfg.hideEffect ? this.cfg.hideEffect : 'fade';
        this.cfg.showDelay = this.cfg.showDelay||150;
        this.cfg.hideDelay = this.cfg.hideDelay||0;
        this.cfg.hideEffectDuration = this.cfg.target ? 250 : 1;
        this.cfg.position = this.cfg.position||'right';
        this.cfg.escape = (this.cfg.escape === undefined) ? true : this.cfg.escape;

        if(this.cfg.target)
            this.bindTarget();
        else
            this.bindGlobal();
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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

    /**
     * Sets up all global event listeners that are required for the tooltip.
     * @private
     */
    bindGlobal: function() {
        this.jq = $('<div class="ui-tooltip ui-tooltip-global ui-widget ui-tooltip-' + this.cfg.position + '" role="tooltip"></div>')
            .appendTo('body');
        this.jq.append('<div class="ui-tooltip-arrow"></div><div class="ui-tooltip-text ui-shadow ui-corner-all"></div>');

        this.jq.addClass(this.cfg.styleClass);
        
        this.cfg.globalSelector = this.cfg.globalSelector||'a,:input,:button';
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

    /**
     * Sets up all event listeners on the target component that are required for the tooltip.
     * @private
     */
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

                            if(PrimeFaces.trim($this.jq.children('.ui-tooltip-text').html()) !== '') {
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

                            if(PrimeFaces.trim($this.jq.children('.ui-tooltip-text').html()) !== '') {
                                $this.show();
                            }
                        })
                        .on(this.cfg.hideEvent + '.tooltip', function() {
                            $this.hide();
                        });
        }

        this.jq.appendTo(document.body);

        if(PrimeFaces.trim(this.jq.children('.ui-tooltip-text').html()) === '') {
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

    /**
     * Aligns the position of this tooltip via the given options.
     * @private
     * @param {PrimeFaces.widget.Tooltip.TooltipPosition} position Position where the tooltip should be shown.
     * @param {Record<string, string>} feedback Feedback about the position and dimensions of both elements, as well as
     * calculations to their relative position.
     */
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
            left: position['left'] + 'px',
            top: position['top'] + 'px'
        });
    },

    /**
     * Aligns the position of this tooltip so that it is shown next to the target component.
     */
    align: function() {
        var $this = this;
         this.jq.css({
            left:'',
            top:'',
            'z-index': PrimeFaces.nextZindex()
        });

        if(this.cfg.trackMouse && this.mouseEvent) {
            this.jq.position({
                my: 'left+3 top',
                of: this.mouseEvent,
                collision: 'flipfit',
                using: function(p,f) {
                    $this.alignUsing.call($this,p,f);
                }
            });

            this.mouseEvent = null;
        }
        else {
            var _my = this.cfg.myPos, 
            _at = this.cfg.atPos;

            if (!_my || !_at) {
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

    /**
     * Brings up this tooltip and displays it next to the target component.
     */
    show: function() {
        if(this.getTarget()) {
            var $this = this;
            this.clearTimeout();

            this.timeout = setTimeout(function() {
                $this._show();
            }, this.cfg.showDelay);
        }
    },

    /**
     * Callback for when the tooltip is brought up, also invokes the appropriate behaviors.
     * @private
     */
    _show: function() {
        var $this = this;

        if(this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this);
            if(retVal === false) {
                return;
            }
        }

        this.jq.css({'display':'block', 'opacity':'0', 'pointer-events': 'none'});
    
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

    /**
     * Hides this tooltip so that it is not shown any longer.
     */
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

    /**
     * Callback for when the tooltip is hidden, also invokes the appropriate behaviors.
     * @private
     */
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

    /**
     * Clears the current set-timeout timer, if any.
     * @private
     */
    clearTimeout: function() {
        if(this.timeout) {
            clearTimeout(this.timeout);
        }
    },

    /**
     * Adds the event listener for moving the tooltip to the current position of the mouse. Used when the tooltip is
     * brought up.
     * @private
     */
    followMouse: function() {
        var $this = this;

        this.getTarget().on('mousemove.tooltip-track', function(e) {
            $this.jq.position({
                my: 'left+3 top',
                of: e,
                collision: 'flipfit'
            });
        });
    },

    /**
     * Removes the event listener for moving the tooltip to the current position of the mouse. Used when the tooltip
     * is hidden.
     * @private
     */
    unfollowMouse: function() {
        var target = this.getTarget();
        if(target) {
            target.off('mousemove.tooltip-track');
        }
    },

    /**
     * Checks whether this tooltip is visible.
     * @return {boolean} Whether this tooltip is currently shown.
     */
    isVisible: function() {
        return this.jq.is(':visible');
    },

    /**
     * Finds the component for which this tooltip is shown.
     * @private
     * @return {JQuery} The target component for this tooltip.
     */
    getTarget: function() {
        if(this.cfg.delegate)
            return PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
        else
            return this.target;
    }

});
