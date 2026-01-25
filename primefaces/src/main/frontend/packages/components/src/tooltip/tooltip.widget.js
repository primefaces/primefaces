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
 * @prop {boolean} allowHide Variable used to control whether the tooltip is being hovered in autoHide mode
 * 
 * @interface {PrimeFaces.widget.TooltipCfg} cfg The configuration for the {@link  Tooltip| Tooltip widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {PrimeFaces.widget.Tooltip.BeforeShowCallback} cfg.beforeShow Client side callback to execute before tooltip is
 * shown. Returning false will prevent display.
 * @prop {string} cfg.autoHide Whether to hide tooltip when hovering over tooltip content.
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
PrimeFaces.widget.Tooltip = class Tooltip extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent.split(' ').map(e => e + '.tooltip').join(' ') : 'mouseenter.tooltip';
        this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent.split(' ').map(e => e + '.tooltip').join(' ') : 'mouseleave.tooltip';
        this.cfg.showEffect = this.cfg.showEffect ? this.cfg.showEffect : 'fade';
        this.cfg.hideEffect = this.cfg.hideEffect ? this.cfg.hideEffect : 'fade';
        this.cfg.showDelay = PrimeFaces.utils.defaultNumeric(this.cfg.showDelay, 400);
        this.cfg.hideDelay = PrimeFaces.utils.defaultNumeric(this.cfg.hideDelay, 0);
        this.cfg.hideEffectDuration = this.cfg.target ? 250 : 1;
        this.cfg.position = this.cfg.position || 'right';
        this.cfg.escape = (this.cfg.escape === undefined) ? true : this.cfg.escape;
        this.cfg.autoHide = (this.cfg.autoHide === undefined) ? true : this.cfg.autoHide;
        this.allowHide = true;

        if (this.cfg.target)
            this.bindTarget();
        else
            this.bindGlobal();
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        this._cleanup();
        super.refresh(cfg);
    }

    /**
     * @override
     * @inheritdoc
     */
    destroy() {
        super.destroy();
        this._cleanup();
    }

    /**
     * Clean up this widget and remove elements from DOM.
     * @private
     */
    _cleanup() {
        if (this.cfg.target) {
            var targetTooltip = $(document.body).children(PrimeFaces.escapeClientId(this.cfg.id));
            if (targetTooltip.length)
                targetTooltip.remove();
        }
        else {
            $(document.body).children('.ui-tooltip-global').remove();
        }
    }

    /**
     * Sets up all global event listeners that are required for the tooltip.
     * @private
     */
    bindGlobal() {
        this.jq = $('<div class="ui-tooltip ui-tooltip-global ui-widget ui-tooltip-' + this.cfg.position + '" role="tooltip"></div>')
            .appendTo('body');
        this.jq.append('<div class="ui-tooltip-arrow"></div><div class="ui-tooltip-text ui-shadow"></div>');

        this.jq.addClass(this.cfg.styleClass);

        this.cfg.globalSelector = this.cfg.globalSelector || 'a,:input,:button';
        var $this = this;

        var namespace = '.' + this.id;
        $(document).off(this.cfg.showEvent + namespace + ' ' + this.cfg.hideEvent + namespace, this.cfg.globalSelector)
            .on(this.cfg.showEvent + namespace, this.cfg.globalSelector, function(e) {
                $this._hide();
                var element = $(this);
                if (element.prop('disabled')) {
                    return;
                }

                if ($this.cfg.trackMouse) {
                    $this.mouseEvent = e;
                }

                var title = element.attr('title');
                if (title) {
                    element.data('tooltip', title).removeAttr('title');
                }

                var arrow = $this.jq.children('.ui-tooltip-arrow');

                if (element.hasClass('ui-state-error')) {
                    $this.jq.children('.ui-tooltip-text').addClass('ui-state-error');
                    arrow.addClass('ui-state-error');
                }
                else {
                    arrow.removeClass('ui-state-error');
                }

                var text = element.data('tooltip');
                if (text) {
                    if ($this.cfg.escape)
                        $this.jq.children('.ui-tooltip-text').text(text);
                    else
                        $this.jq.children('.ui-tooltip-text').html(text);

                    $this.clearTimeout();
                    $this.timeout = PrimeFaces.queueTask(function() {
                        $this.globalTitle = text;
                        $this.target = element;
                        $this._show();
                    }, $this.cfg.showDelay);
                }
            })
            .on(this.cfg.hideEvent + namespace, this.cfg.globalSelector, function() {
                $this.hide();
            });
        this.addDestroyListener(function() {
            $(document).off(namespace);
        });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.tooltip' + '_align', $this.jq, function() {
            $this.align();
        });

    }

    /**
     * Sets up all event listeners on the target component that are required for the tooltip.
     * @private
     */
    bindTarget() {
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(this.jqId);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.target);

        var describedBy = this.target.attr("aria-describedby");
        if (!describedBy || 0 === describedBy.length) {
            describedBy = this.id;
        } else {
            describedBy += " " + this.id;
        }
        this.target.attr("aria-describedby", describedBy);

        var $this = this;
        if (this.cfg.delegate) {
            // try to get jq selectors from pf target selector to bind on all elements, not on the 1st one only
            var matchResult = this.cfg.target.match('@\\((.+)\\)');
            var targetSelector = (matchResult && matchResult.length > 1) ? matchResult[1] : "*[id='" + this.target.attr('id') + "']";

            var namespace = '.' + this.id;
            $(document).off(this.cfg.showEvent + namespace + ' ' + this.cfg.hideEvent + namespace, targetSelector)
                .on(this.cfg.showEvent + namespace, targetSelector, function(e) {
                    if ($this.cfg.trackMouse) {
                        $this.mouseEvent = e;
                    }

                    if (PrimeFaces.trim($this.jq.children('.ui-tooltip-text').html()) !== '') {
                        $this.target = $(this);
                        $this.show();
                    }
                })
                .on(this.cfg.hideEvent + namespace, function() {
                    $this.hide();
                });
            this.addDestroyListener(function() {
                $(document).off(namespace);
            });
        }
        else {
            // GitHub #9941 Helper to remove tooltips when elements are removed
            this.target.off('remove.tooltip').on('remove.tooltip', function() {
                $this.allowHide = true;
                $this.hide();
            });      

            this.target.off(this.cfg.showEvent + ' ' + this.cfg.hideEvent)
                .on(this.cfg.showEvent, function(e) {
                    if ($this.cfg.trackMouse) {
                        $this.mouseEvent = e;
                    }

                    if (PrimeFaces.trim($this.jq.children('.ui-tooltip-text').html()) !== '') {
                        $this.target = $(this);
                        $this.show();
                    }
                })
                .on(this.cfg.hideEvent, function(e) {
                    $this.shouldAllowHideBasedOnMouseTarget(e);
                });

            this.bindAutoHide();
        }

        this.jq.appendTo(document.body);

        if (PrimeFaces.trim(this.jq.children('.ui-tooltip-text').html()) === '') {
            var text = this.target.attr('title');
            if (this.cfg.escape)
                this.jq.children('.ui-tooltip-text').text(text);
            else
                this.jq.children('.ui-tooltip-text').html(text);
        }

        this.target.removeAttr('title');


        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.jq, function() {
            $this.align();
        });
    }

    /**
      * Sets up mouse listeners if autoHide is disabled to keep the toolip open if tooltip has focus.
      * @private
      */
    bindAutoHide() {
        if (this.isAutoHide()) {
            return;
        }
        var $this = this;
        this.jq.off("mouseenter.tooltip mouseleave.tooltip")
            .on("mouseenter.tooltip", function(e) {
                $this.allowHide = false;
            })
            .on("mouseleave.tooltip", function(e) {
                $this.shouldAllowHideBasedOnMouseTarget(e);
            });
    }

    /**
     * Sets up the escape key event listener to hide the tooltip when escape is pressed.
     * @private
     */
    bindEscapeKey() {
        var $this = this;
        $(document).off('keydown.tooltip' + this.id).on('keydown.tooltip' + this.id, function(e) {
            if(!e.isDefaultPrevented() && e.key === 'Escape' && $this.isVisible()) {
                $this.hide();
            }
        });
    }

    /**
     * Removes the escape key event listener.
     * @private
     */
    unbindEscapeKey() {
        $(document).off('keydown.tooltip' + this.id);
    }

    /**
     * Determines if the tooltip should be allowed to hide based on the mouse event's related target.
     * @param {JQuery.TriggeredEvent} e - The jQuery event object that triggered the mouse event.
     * @private
     */
    shouldAllowHideBasedOnMouseTarget(e) {
        if (this.isAutoHide()) {
            this.allowHide = true;
        }
        else {
            var mouseTarget = $(e.relatedTarget);
            var previousElement = this.target;
            this.allowHide = !(mouseTarget.is(previousElement) ||
                               previousElement.attr('aria-describedby') === mouseTarget.closest(PrimeFaces.escapeClientId(this.id)).attr('id') ||
                               mouseTarget.attr('aria-describedby') === this.id ||
                               mouseTarget.parent().attr('aria-describedby') === this.id);
        }
        if (this.allowHide) {
            this.hide();
        }
    }

    /**
     * Aligns the position of this tooltip via the given options.
     * @private
     * @param {PrimeFaces.widget.Tooltip.TooltipPosition} position Position where the tooltip should be shown.
     * @param {Record<string, string>} feedback Feedback about the position and dimensions of both elements, as well as
     * calculations to their relative position.
     */
    alignUsing(position, feedback) {
        // Determine the position of the tooltip
        // If trackMouse is enabled, use cfg.position
        // Otherwise, use cfg.atPos if available, or fall back to cfg.position
        var configPosition = this.cfg.trackMouse ? this.cfg.position : (this.cfg.atPos || this.cfg.position);
        
        // Split the configPosition into an array of strings e.g. "left center"
        configPosition = configPosition.split(' ');

        // Initialize the tooltipClass with 'ui-tooltip-'
        var tooltipClass = 'ui-tooltip-';
        for (var i = 0; i < configPosition.length; i++) {
            var pos = configPosition[i];
            if (pos === "right" || pos === "left") {
                tooltipClass += feedback['horizontal'] == 'left' ? 'right' : 'left';
                break;
            }
            else if (pos === "top" || pos === "bottom") {
                tooltipClass += feedback['vertical'] == 'top' ? 'bottom' : 'top';
                break;
            }
        }
        
        this.jq.removeClass('ui-tooltip-left ui-tooltip-right ui-tooltip-top ui-tooltip-bottom')
               .addClass(tooltipClass)
               .css({
                   left: position['left'] + 'px',
                   top: position['top'] + 'px'
               });
    }

    /**
     * Aligns the position of this tooltip so that it is shown next to the target component.
     */
    align() {
        var $this = this;
        // #10100 make sure z-Index is above any dynamically changing zindex like dialogs.
        var zIndex = (parseInt(PrimeFaces.nextZindex(), 10) + 1000);
        this.jq.css({
            left: '',
            top: '',
            'z-index': zIndex
        });

        if (this.cfg.trackMouse && this.mouseEvent) {
            this.jq.position({
                my: 'left+3 top',
                of: this.mouseEvent,
                collision: 'flipfit',
                using: function(p, f) {
                    $this.alignUsing(p, f);
                }
            });

            this.mouseEvent = null;
        }
        else {
            var _my = this.cfg.myPos,
                _at = this.cfg.atPos;

            if (!_my || !_at) {
                switch (this.cfg.position) {
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
                using: function(p, f) {
                    $this.alignUsing(p, f);
                }
            });
        }
    }

    /**
     * Brings up this tooltip and displays it next to the target component.
     */
    show() {
        if (this.isVisible()) {
            return;
        }

        if (this.getTarget()) {
            var $this = this;
            this.clearTimeout();

            this.timeout = PrimeFaces.queueTask(function() {
                $this._show();
            }, this.cfg.showDelay);
        }
    }

    /**
     * Callback for when the tooltip is brought up, also invokes the appropriate behaviors.
     * @private
     */
    _show() {
        var $this = this;

        if (this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this);
            if (retVal === false) {
                return;
            }
        }

        this.bindEscapeKey();

        // allow pointer events when autohide is disabled
        var pointerEvents = '';
        if (this.isAutoHide()) {
            pointerEvents = 'none';
        }

        this.jq.css({ 'display': 'block', 'opacity': '0', 'pointer-events': pointerEvents });

        this.align();

        this.jq.css({ 'display': 'none', 'opacity': '' });

        if (this.cfg.trackMouse) {
            this.followMouse();
        }
        this.jq.show(this.cfg.showEffect, {}, 250, function() {
            if ($this.cfg.onShow) {
                $this.cfg.onShow.call();
            }
        });
    }

    /**
     * Hides this tooltip so that it is not shown any longer.
     */
    hide() {
        var $this = this;
        this.clearTimeout();

        this.timeout = PrimeFaces.queueTask(function() {
            $this._hide();
        }, this.cfg.hideDelay);
    }

    /**
     * Callback for when the tooltip is hidden, also invokes the appropriate behaviors.
     * @private
     */
    _hide() {
        var $this = this;

        if (this.isVisible()) {
            if (!this.isAutoHide() && this.allowHide === false) {
                return;
            }

            this.jq.hide(this.cfg.hideEffect, {}, this.cfg.hideEffectDuration, function() {
                $(this).css('z-index', '');
                if ($this.cfg.trackMouse) {
                    $this.unfollowMouse();
                }

                if ($this.cfg.onHide) {
                    $this.cfg.onHide.call();
                }
            });

            if ($this.globalTitle) {
                $this.globalTitle = null;
                $this.target = null;
                $this.jq.children('.ui-tooltip-text').removeClass('ui-state-error');
            }

            this.unbindEscapeKey();
        }
    }

    /**
     * Clears the current set-timeout timer, if any.
     * @private
     */
    clearTimeout() {
        if (this.timeout) {
            clearTimeout(this.timeout);
        }
    }

    /**
     * Adds the event listener for moving the tooltip to the current position of the mouse. Used when the tooltip is
     * brought up.
     * @private
     */
    followMouse() {
        var $this = this;

        this.getTarget().on('mousemove.tooltip-track', function(e) {
            $this.jq.position({
                my: 'left+3 top',
                of: e,
                collision: 'flipfit'
            });
        });
    }

    /**
     * Removes the event listener for moving the tooltip to the current position of the mouse. Used when the tooltip
     * is hidden.
     * @private
     */
    unfollowMouse() {
        var target = this.getTarget();
        if (target) {
            target.off('mousemove.tooltip-track');
        }
    }

    /**
     * Checks whether this tooltip is visible.
     * @return {boolean} Whether this tooltip is currently shown.
     */
    isVisible() {
        return this.jq.is(':visible');
    }

    /**
     * Checks if the target has the autoHide property enabled or disabled to keep the tooltip open.
     * @return {boolean} Whether this tooltip should be left showing or closed.
     */
    isAutoHide() {
        return this.jq.data('autohide') || this.cfg.autoHide;
    }

    /**
     * Finds the component for which this tooltip is shown.
     * @private
     * @return {JQuery} The target component for this tooltip.
     */
    getTarget() {
        if (this.cfg.delegate)
            return PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.target);
        else
            return this.target;
    }

}
