/**
 * The configuration for the {@link Tooltip} widget.
 */
export interface TooltipCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Client side callback to execute before tooltip is shown. Returning false will prevent display. */
    beforeShow: PrimeFacesTypes.widget.Tooltip.BeforeShowCallback;
    /** Whether to hide tooltip when hovering over tooltip content. */
    autoHide: boolean;
    /** Search expression for overriding the {@link Tooltip.target | target}. */
    delegate: string;
    /** Defines whether HTML would be escaped or not. */
    escape: boolean;
    /** A jQuery selector for global tooltip, defaults to `a,:input,:button`. */
    globalSelector: string;
    /** Delay time to hide tooltip in milliseconds. */
    hideDelay: number;
    /** Effect to be used for hiding. */
    hideEffect: string;
    /** Duration of the hide effect in milliseconds. */
    hideEffectDuration: number;
    /** Event hiding the tooltip. */
    hideEvent: string;
    /** Client side callback to execute after tooltip is hidden. */
    onHide: PrimeFacesTypes.widget.Tooltip.OnHideCallback;
    /** Client side callback to execute after tooltip is shown. */
    onShow: PrimeFacesTypes.widget.Tooltip.OnShowCallback;
    /** Position of the tooltip. */
    position: PrimeFacesTypes.widget.Tooltip.TooltipPosition;
    /** Delay time to show tooltip in milliseconds. */
    showDelay: number;
    /** Effect to be used for displaying. */
    showEffect: string;
    /** Event displaying the tooltip. */
    showEvent: string;
    /** Style class of the tooltip. */
    styleClass: string;
    /** Position of tooltip with respect to target. If set overrides the 'position' attribute. */
    myPos: string;
    /** Position of tooltip with respect to target. If set overrides the 'position' attribute. */
    atPos: string;
    /** Search expression for the component to which the tooltip is attached. */
    target: string;
    /** Whether the tooltip position should follow the mouse or pointer. */
    trackMouse: boolean;
}

/**
 * __PrimeFaces Tooltip Widget__
 *
 * Tooltip goes beyond the legacy HTML title attribute by providing custom effects, events, HTML content and advance
 * theme support.
 */
export class Tooltip<Cfg extends TooltipCfg = TooltipCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The text that is shown as the global title. */
    globalTitle?: string | null;
    /** The mouse event that occurred for this tooltip. */
    mouseEvent?: JQuery.TriggeredEvent | null;
    /** The DOM element for the target component. */
    target?: JQuery | null;
    /** The set-timeout timer ID of the time for the tooltip delay. */
    timeout?: number;
    /** Variable used to control whether the tooltip is being hovered in autoHide mode. */
    allowHide: boolean = true;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
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

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        if (this.cfg.target) {
            // remove the old element, which was moved to body - an update of the tooltip itself replaces it in
            // place inside body, so it must not be removed by its id
            this.jq.remove();
        }
        else {
            this._cleanup();
        }
        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();
        this._cleanup();
    }

    /**
     * Clean up this widget and remove elements from DOM.
     */
    private _cleanup(): void {
        if (this.cfg.target) {
            const targetTooltip = $(document.body).children(PrimeFaces.escapeClientId(this.cfg.id as string));
            if (targetTooltip.length)
                targetTooltip.remove();
        }
        else {
            // only the own element, other global tooltips have their own one
            this.jq.remove();
        }
    }

    /**
     * Appends the given namespace to every event of the given space separated list of events.
     * @param events Space separated list of events.
     * @param namespace The namespace to append, e.g. `.myId`.
     * @returns The namespaced events.
     */
    private namespaceEvents(events: string, namespace: string): string {
        return events.split(' ').map((event) => {
            return event + namespace;
        }).join(' ');
    }

    /**
     * Sets up all global event listeners that are required for the tooltip.
     */
    private bindGlobal(): void {
        this.jq = $('<div class="p-tooltip p-tooltip-global p-widget p-tooltip-' + this.cfg.position + '" role="tooltip"></div>')
            .appendTo('body');
        this.jq.append('<div class="p-tooltip-arrow"></div><div class="p-tooltip-text"></div>');

        this.jq.addClass(this.cfg.styleClass as string);

        this.cfg.globalSelector = this.cfg.globalSelector || 'a,:input,:button';

        const namespace = '.' + this.id;
        const showEvents = this.namespaceEvents(this.cfg.showEvent as string, namespace);
        const hideEvents = this.namespaceEvents(this.cfg.hideEvent as string, namespace);
        $(document).off(showEvents + ' ' + hideEvents, this.cfg.globalSelector)
            .on(showEvents, this.cfg.globalSelector, (e) => {
                this._hide();
                const element = $(e.currentTarget);
                if (element.prop('disabled')) {
                    return;
                }

                if (this.cfg.trackMouse) {
                    this.mouseEvent = e;
                }

                const title = element.attr('title');
                if (title) {
                    element.data('tooltip', title).removeAttr('title');
                }

                const arrow = this.jq.children('.p-tooltip-arrow');

                if (element.hasClass('p-state-error')) {
                    this.jq.children('.p-tooltip-text').addClass('p-state-error');
                    arrow.addClass('p-state-error');
                }
                else {
                    arrow.removeClass('p-state-error');
                }

                const text = element.data('tooltip');
                if (text) {
                    if (this.cfg.escape)
                        this.jq.children('.p-tooltip-text').text(text);
                    else
                        this.jq.children('.p-tooltip-text').html(text);

                    this.clearTimeout();
                    this.timeout = PrimeFaces.queueTask(() => {
                        this.globalTitle = text;
                        this.target = element;
                        this._show();
                    }, this.cfg.showDelay);
                }
            })
            .on(hideEvents, this.cfg.globalSelector, () => {
                this.hide();
            });
        this.addDestroyListener(() => {
            $(document).off(namespace);
        });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.tooltip' + '_align', this.jq, () => {
            this.align();
        });

    }

    /**
     * Sets up all event listeners on the target component that are required for the tooltip.
     */
    private bindTarget(): void {
        this.id = this.cfg.id as string;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(this.jqId);
        const target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.target);
        this.target = target;

        let describedBy = target.attr("aria-describedby");
        if (!describedBy || 0 === describedBy.length) {
            describedBy = this.id;
        } else if (!describedBy.split(' ').includes(this.id)) {
            describedBy += " " + this.id;
        }
        target.attr("aria-describedby", describedBy);

        if (this.cfg.delegate) {
            // try to get jq selectors from pf target selector to bind on all elements, not on the 1st one only
            const matchResult = (this.cfg.target as string).match('@\\((.+)\\)');
            const targetSelector = (matchResult && matchResult.length > 1) ? matchResult[1] as string : "*[id='" + target.attr('id') + "']";

            const namespace = '.' + this.id;
            $(document).off(namespace)
                .on(this.namespaceEvents(this.cfg.showEvent as string, namespace), targetSelector, (e) => {
                    if (this.cfg.trackMouse) {
                        this.mouseEvent = e;
                    }

                    if (PrimeFaces.trim(this.jq.children('.p-tooltip-text').html()) !== '') {
                        this.target = $(e.currentTarget);
                        this.show();
                    }
                })
                .on(this.namespaceEvents(this.cfg.hideEvent as string, namespace), () => {
                    this.hide();
                });
            this.addDestroyListener(() => {
                $(document).off(namespace);
            });
        }
        else {
            // GitHub #9941 Helper to remove tooltips when elements are removed
            target.off('remove.tooltip').on('remove.tooltip', () => {
                this.allowHide = true;
                this.hide();
            });

            target.off(this.cfg.showEvent + ' ' + this.cfg.hideEvent)
                .on(this.cfg.showEvent as string, (e) => {
                    if (this.cfg.trackMouse) {
                        this.mouseEvent = e;
                    }

                    if (PrimeFaces.trim(this.jq.children('.p-tooltip-text').html()) !== '') {
                        this.target = $(e.currentTarget);
                        this.show();
                    }
                })
                .on(this.cfg.hideEvent as string, (e) => {
                    this.shouldAllowHideBasedOnMouseTarget(e);
                });

            this.bindAutoHide();
        }

        this.jq.appendTo(document.body);

        if (PrimeFaces.trim(this.jq.children('.p-tooltip-text').html()) === '') {
            const text = target.attr('title') as string;
            if (this.cfg.escape)
                this.jq.children('.p-tooltip-text').text(text);
            else
                this.jq.children('.p-tooltip-text').html(text);
        }

        target.removeAttr('title');


        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', this.jq, () => {
            this.align();
        });
    }

    /**
     * Sets up mouse listeners if autoHide is disabled to keep the toolip open if tooltip has focus.
     */
    private bindAutoHide(): void {
        if (this.isAutoHide()) {
            return;
        }
        this.jq.off("mouseenter.tooltip mouseleave.tooltip")
            .on("mouseenter.tooltip", () => {
                this.allowHide = false;
            })
            .on("mouseleave.tooltip", (e) => {
                this.shouldAllowHideBasedOnMouseTarget(e);
            });
    }

    /**
     * Sets up the escape key event listener to hide the tooltip when escape is pressed.
     */
    private bindEscapeKey(): void {
        $(document).off('keydown.tooltip' + this.id).on('keydown.tooltip' + this.id, (e) => {
            if(!e.isDefaultPrevented() && e.key === 'Escape' && this.isVisible()) {
                this.hide();
            }
        });
    }

    /**
     * Removes the escape key event listener.
     */
    private unbindEscapeKey(): void {
        $(document).off('keydown.tooltip' + this.id);
    }

    /**
     * Determines if the tooltip should be allowed to hide based on the mouse event's related target.
     * @param e The jQuery event object that triggered the mouse event.
     */
    private shouldAllowHideBasedOnMouseTarget(e: JQuery.TriggeredEvent): void {
        if (this.isAutoHide()) {
            this.allowHide = true;
        }
        else {
            const mouseTarget = $((e as JQuery.MouseEventBase).relatedTarget as Element);
            const previousElement = this.target as JQuery;
            this.allowHide = !(mouseTarget.is(previousElement) ||
                               previousElement.attr('aria-describedby') === mouseTarget.closest(PrimeFaces.escapeClientId(this.id as string)).attr('id') ||
                               mouseTarget.attr('aria-describedby') === this.id ||
                               mouseTarget.parent().attr('aria-describedby') === this.id);
        }
        if (this.allowHide) {
            this.hide();
        }
    }

    /**
     * Aligns the position of this tooltip via the given options.
     * @param position Position where the tooltip should be shown.
     * @param feedback Feedback about the position and dimensions of both elements, as well as calculations to their
     * relative position.
     */
    private alignUsing(position: PrimeFacesTypes.utils.PositionCoordinates, feedback: PrimeFacesTypes.utils.PositionFeedback): void {
        // Determine the position of the tooltip
        // If trackMouse is enabled, use cfg.position
        // Otherwise, use cfg.atPos if available, or fall back to cfg.position
        const configPositionValue = (this.cfg.trackMouse ? this.cfg.position : (this.cfg.atPos || this.cfg.position)) as string;

        // Split the configPosition into an array of strings e.g. "left center"
        const configPosition = configPositionValue.split(' ');

        // Initialize the tooltipClass with 'p-tooltip-'
        let tooltipClass = 'p-tooltip-';
        for (let i = 0; i < configPosition.length; i++) {
            const pos = configPosition[i];
            if (pos === "right" || pos === "left") {
                tooltipClass += feedback['horizontal'] == 'left' ? 'right' : 'left';
                break;
            }
            else if (pos === "top" || pos === "bottom") {
                tooltipClass += feedback['vertical'] == 'top' ? 'bottom' : 'top';
                break;
            }
        }

        this.jq.removeClass('p-tooltip-left p-tooltip-right p-tooltip-top p-tooltip-bottom')
               .addClass(tooltipClass)
               .css({
                   left: position['left'] + 'px',
                   top: position['top'] + 'px'
               });
    }

    /**
     * Aligns the position of this tooltip so that it is shown next to the target component.
     */
    align(): void {
        // #10100 make sure z-Index is above any dynamically changing zindex like dialogs.
        const zIndex = (parseInt(PrimeFaces.nextZindex(), 10) + 1000);
        this.jq.css({
            left: '',
            top: '',
            'z-index': zIndex
        });

        if (this.cfg.trackMouse && this.mouseEvent) {
            PrimeFaces.utils.position(this.jq, {
                my: 'left+3 top',
                of: this.mouseEvent,
                collision: 'flipfit',
                using: (p, f) => {
                    this.alignUsing(p, f);
                }
            });

            this.mouseEvent = null;
        }
        else {
            let _my = this.cfg.myPos,
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

            PrimeFaces.utils.position(this.jq, {
                my: _my,
                at: _at,
                of: this.getTarget(),
                collision: 'flipfit',
                using: (p, f) => {
                    this.alignUsing(p, f);
                }
            });
        }
    }

    /**
     * Brings up this tooltip and displays it next to the target component.
     */
    show(): void {
        if (this.isVisible()) {
            return;
        }

        if (this.getTarget()) {
            this.clearTimeout();

            this.timeout = PrimeFaces.queueTask(() => {
                this._show();
            }, this.cfg.showDelay);
        }
    }

    /**
     * Callback for when the tooltip is brought up, also invokes the appropriate behaviors.
     */
    private _show(): void {
        if (this.cfg.beforeShow) {
            const retVal = this.cfg.beforeShow.call(this);
            if (retVal === false) {
                return;
            }
        }

        this.bindEscapeKey();

        // allow pointer events when autohide is disabled
        let pointerEvents = '';
        if (this.isAutoHide()) {
            pointerEvents = 'none';
        }

        this.jq.css({ 'display': 'block', 'opacity': '0', 'pointer-events': pointerEvents });

        this.align();

        this.jq.css({ 'display': 'none', 'opacity': '' });

        if (this.cfg.trackMouse) {
            this.followMouse();
        }
        PrimeFaces.effects.show(this.jq, this.cfg.showEffect, { duration: 250, complete: () => {
            if (this.cfg.onShow) {
                this.cfg.onShow.call(undefined);
            }
        } });
    }

    /**
     * Hides this tooltip so that it is not shown any longer.
     */
    hide(): void {
        this.clearTimeout();

        this.timeout = PrimeFaces.queueTask(() => {
            this._hide();
        }, this.cfg.hideDelay);
    }

    /**
     * Callback for when the tooltip is hidden, also invokes the appropriate behaviors.
     */
    private _hide(): void {
        const $this = this;

        if (this.isVisible()) {
            if (!this.isAutoHide() && this.allowHide === false) {
                return;
            }

            PrimeFaces.effects.hide(this.jq, this.cfg.hideEffect, { duration: this.cfg.hideEffectDuration, complete: function() {
                $(this).css('z-index', '');
                if ($this.cfg.trackMouse) {
                    $this.unfollowMouse();
                }

                if ($this.cfg.onHide) {
                    $this.cfg.onHide.call(undefined);
                }
            } });

            if (this.globalTitle) {
                this.globalTitle = null;
                this.target = null;
                this.jq.children('.p-tooltip-text').removeClass('p-state-error');
            }

            this.unbindEscapeKey();
        }
    }

    /**
     * Clears the current set-timeout timer, if any.
     */
    private clearTimeout(): void {
        if (this.timeout) {
            clearTimeout(this.timeout);
        }
    }

    /**
     * Adds the event listener for moving the tooltip to the current position of the mouse. Used when the tooltip is
     * brought up.
     */
    private followMouse(): void {
        (this.getTarget() as JQuery).on('mousemove.tooltip-track', (e) => {
            PrimeFaces.utils.position(this.jq, {
                my: 'left+3 top',
                of: e,
                collision: 'flipfit'
            });
        });
    }

    /**
     * Removes the event listener for moving the tooltip to the current position of the mouse. Used when the tooltip
     * is hidden.
     */
    private unfollowMouse(): void {
        const target = this.getTarget();
        if (target) {
            target.off('mousemove.tooltip-track');
        }
    }

    /**
     * Checks whether this tooltip is visible.
     * @returns Whether this tooltip is currently shown.
     */
    isVisible(): boolean {
        return this.jq.is(':visible');
    }

    /**
     * Checks if the target has the autoHide property enabled or disabled to keep the tooltip open.
     * @returns Whether this tooltip should be left showing or closed.
     */
    isAutoHide(): boolean {
        return this.jq.data('autohide') || this.cfg.autoHide;
    }

    /**
     * Finds the component for which this tooltip is shown.
     * @returns The target component for this tooltip.
     */
    private getTarget(): JQuery | null | undefined {
        if (this.cfg.delegate)
            return PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.target);
        else
            return this.target;
    }

}
