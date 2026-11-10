/**
 * The configuration for the {@link OverlayPanel} widget.
 */
export interface OverlayPanelCfg extends PrimeFacesTypes.widget.DynamicOverlayWidgetCfg {
    /** Appends the overlayPanel to the given search expression. */
    appendTo: string | null;
    /** Whether to hide overlay when hovering over overlay content when using custom show/hide. */
    autoHide: boolean;
    /** Position of the target relative to the panel. */
    at: string;
    /** `true` to load the content via AJAX when the overlay panel is opened, `false` to load the content immediately. */
    dynamic: boolean;
    /**
     * Only relevant for dynamic="true": Defines if activating the panel should load the contents from server again. For
     * cache="true" (default) the panel content is only loaded once.
     */
    cache: boolean;
    /** Event on target to hide the panel. */
    hideEvent: string;
    /**
     * When the positioned element overflows the window in some direction, move it to an alternative position. Similar
     * to my and at, this accepts a single value or a pair for horizontal/vertical, e.g., `flip`, `fit`, `fit flip`,
     * `fit none`.
     */
    collision: string;
    /** When set `true`, clicking outside of the panel hides the overlay. */
    dismissable: boolean;
    /**
     * Specifies whether the document should be shielded with a partially transparent mask to require the user to close
     * the panel before being able to activate any elements in the document.
     */
    modal: boolean;
    /** Position of the panel relative to the target. */
    my: string;
    /** Client side callback to execute when the panel is hidden. */
    onHide: PrimeFacesTypes.widget.OverlayPanel.OnHideCallback;
    /** Client side callback to execute when the panel is shown. */
    onShow: PrimeFacesTypes.widget.OverlayPanel.OnShowCallback;
    /** Displays a close icon to hide the overlay, default is `false`. */
    showCloseIcon: boolean;
    /** Delay in milliseconds applied when the overlay panel is shown. */
    showDelay: number;
    /**
     * Event on target to show the panel. If showEvent is 'none', the overlay panel will only be displayed by `show()`
     * or `toggle()`.
     */
    showEvent: string;
    /** Search expression for target component to display panel next to. */
    target: string;
}

/**
 * __PrimeFaces OverlayPanel Widget__
 *
 * OverlayPanel is a generic panel component that can be displayed on top of other content.
 */
export class OverlayPanel<Cfg extends OverlayPanelCfg = OverlayPanelCfg> extends PrimeFaces.widget.DynamicOverlayWidget<Cfg> {
    /** The DOM element for the icon that closes the overlay panel. */
    closerIcon?: JQuery;
    /** The DOM element for the content of the overlay panel. */
    content: JQuery = $();
    /** Unbind callback for the hide overlay handler. */
    hideOverlayHandler?: PrimeFacesTypes.Unbindable;
    /** When dynamic loading is enabled, whether the content was already loaded. */
    loaded?: boolean;
    /** Unbind callback for the resize handler. */
    resizeHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback for the scroll handler. */
    scrollHandler?: PrimeFacesTypes.Unbindable;
    /** The set-timeout timer ID of the timer used for showing the overlay panel. */
    showTimeout?: number;
    /** The DOM element for the target component that triggers this overlay panel. */
    target?: JQuery;
    /** The DOM element for the resolved target component that triggers this overlay panel. */
    targetElement?: JQuery | null;
    /** The z-index of the target component that triggers this overlay panel. */
    targetZindex?: number;
    /** Handler for CSS transitions used by this widget. */
    transition?: PrimeFacesTypes.CssTransitionHandler | null;
    /** Variable used to control whether the overlay is being hovered in autoHide mode. */
    allowHide: boolean = true;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        if (cfg.target) {
            this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, cfg.target);
            if (this.target.hasClass('p-splitbutton')) {
                this.target = this.target.find('.p-splitbutton-menubutton');
            }
        }
        super.init(cfg, undefined, undefined, this.target);

        this.content = this.jq.children('div.p-overlaypanel-content');

        //configuration
        this.cfg.my = this.cfg.my || 'left top';
        this.cfg.at = this.cfg.at || 'left bottom';
        this.cfg.collision = this.cfg.collision || 'flip';
        this.cfg.showEvent = this.cfg.showEvent || 'click.p-overlaypanel';
        this.cfg.hideEvent = this.cfg.hideEvent || 'click.p-overlaypanel';
        this.cfg.dismissable = this.cfg.dismissable !== false;
        this.cfg.showDelay = PrimeFaces.utils.defaultNumeric(this.cfg.showDelay, 0);
        this.cfg.autoHide = this.cfg.autoHide !== false;
        this.cfg.cache = this.cfg.cache !== false;
        this.allowHide = true;

        if (this.cfg.showCloseIcon) {
            this.closerIcon = PrimeFaces.skinCloseAction($('<a href="#" class="p-overlaypanel-close p-state-default"><span class="p-icon p-close-icon p-overlaypanel-close-icon"></span></a>'))
                .appendTo(this.jq);
        }

        this.bindCommonEvents();

        if (this.target) {
            this.bindTargetEvents();

            // set aria attributes
            this.target.attr({
                'aria-expanded': false,
                'aria-controls': this.id
            });
        }

        this.transition = PrimeFaces.utils.registerCSSTransition(this.jq, 'p-connected-overlay');
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this._cleanup();
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
        // fix #4307
        this.loaded = false;

        // see #setupDialogSupport
        if (!this.cfg.appendTo) {
            PrimeFaces.utils.removeDynamicOverlay(this, this.jq, this.id as string, $(document.body));
        }

        this.jq.remove();
    }

    /**
     * Sets up the event listeners for the target component that triggers this overlay panel.
     */
    private bindTargetEvents(): void {
        const target = this.target as JQuery;

        //mark target and descandants of target as a trigger for a primefaces overlay
        target.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);

        //show and hide events for target
        if (this.cfg.showEvent === this.cfg.hideEvent) {
            // own namespace, as other overlay panels could use the same target
            const namespace = '.p-overlaypanel.' + this.id;
            const event = (this.cfg.showEvent as string).split(' ').map((e) => { return e + namespace; }).join(' ');

            target.off(event).on(event, () => {
                this.toggle();
            });
        }
        else {
            const showEvent = this.cfg.showEvent + '.p-overlaypanel',
                hideEvent = this.cfg.hideEvent + '.p-overlaypanel';

            target.off(showEvent + ' ' + hideEvent).on(showEvent, (e) => {
                if (!this.isVisible()) {
                    this.show();
                    if (showEvent === 'contextmenu.p-overlaypanel') {
                        e.preventDefault();
                    }
                }
            })
            .on(hideEvent, (e) => {
                clearTimeout(this.showTimeout);
                if (this.isVisible()) {
                    // GitHub #8546
                    if (!this.isAutoHide() && $((e as JQuery.MouseEventBase).relatedTarget as Element).is('div.p-overlaypanel-content')) {
                        this.allowHide = false;
                        return;
                    }

                    this.hide();
                }
            });
        }

        if (this.cfg.showEvent !== 'none') {
            target.off('keyup.p-overlaypanel').on('keyup.p-overlaypanel', (e) => {
                if (PrimeFaces.utils.blockEnterKey(e)) {
                    this.toggle();
                }
            });
        }

        this.bindAutoHide();
    }

    /**
     * Sets up mouse listeners if autoHide is disabled to keep the overlay open if overlay has focus.
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
                if ($((e as JQuery.MouseEventBase).relatedTarget as Element).is(this.target as JQuery)) {
                    return;
                }
                this.allowHide = true;
                this.hide();
            });
    }

    /**
     * Sets up some common event listeners always required by this widget.
     */
    private bindCommonEvents(): void {
        if (this.cfg.showCloseIcon) {
            (this.closerIcon as JQuery).on('mouseover.p-overlaypanel', (e) => {
                $(e.currentTarget).addClass('p-state-hover');
            })
                .on('mouseout.p-overlaypanel', (e) => {
                    $(e.currentTarget).removeClass('p-state-hover');
                })
                .on('click.p-overlaypanel', (e) => {
                    this.hide();
                    e.preventDefault();
                })
                .on('focus.p-overlaypanel', (e) => {
                    $(e.currentTarget).addClass('p-state-focus');
                })
                .on('blur.p-overlaypanel', (e) => {
                    $(e.currentTarget).removeClass('p-state-focus');
                });
        }
    }

    /**
     * Sets up all panel event listeners
     */
    private bindPanelEvents(): void {
        //hide overlay when mousedown is at outside of overlay
        if (this.cfg.dismissable && !this.cfg.modal) {
            // anything focused outside the overlay will close it
            const eventNamespace = 'keyup.' + this.id + '_hide mousedown.' + this.id + '_hide';
            this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, eventNamespace, this.jq,
                () => { return this.target; },
                (_e, eventTarget) => {
                    if (!(this.jq.is(eventTarget) || this.jq.has(eventTarget[0] ?? "").length > 0 || eventTarget.closest('.p-input-overlay').length > 0)) {
                        this.hide();
                    }
                });
        }

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.jq, () => {
            this.handleViewportChange();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.target, () => {
            this.handleViewportChange();
        });
    }

    /**
     * Fired when the browser viewport is resized or scrolled.  In Mobile environment we don't want to hider the overlay
     * we want to re-align it.  This is because on some mobile browser the popup may force the browser to trigger a
     * resize immediately and close the overlay. See GitHub #7075.
     */
    private handleViewportChange(): void {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.align(this.target);
        } else {
            this.hide();
        }
    }

    /**
     * Unbind all panel event listeners
     */
    private unbindPanelEvents(): void {
        if (this.hideOverlayHandler) {
            this.hideOverlayHandler.unbind();
        }

        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }

        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    }

    /**
     * Brings up the overlay panel if it is currently hidden, or hides it if it is currently displayed.
     */
    toggle(): void {
        if (!this.isVisible()) {
            this.show();
        }
        else {
            clearTimeout(this.showTimeout);
            this.hide();
        }
    }

    /**
     * Brings up the overlay panel so that is displayed and visible.
     * @param target ID or DOM element of the target component that triggers this overlay panel.
     */
    show(target?: string | JQuery): void {
        if (this.isVisible()) {
            return;
        }
        this.showTimeout = PrimeFaces.queueTask(() => {
            if (!this.loaded && this.cfg.dynamic) {
                this.loadContents(target);
            }
            else {
                this._show(target);
            }
        }, this.cfg.showDelay);
    }

    /**
     * Makes the overlay panel visible.
     * @param target ID or DOM element of the target component that triggers this overlay panel.
     */
    private _show(target?: string | JQuery): void {
        if (this.transition) {
            const showWithCSSTransition = () => {
                this.transition!.show({
                    onEnter: () => {
                        PrimeFaces.nextZindex(this.jq);
                        this.align(target);
                    },
                    onEntered: () => {
                        this.bindPanelEvents();
                        this.postShow();

                        if (this.cfg.modal) {
                            this.enableModality();
                        }
                    }
                });
            };

            const targetEl = this.getTarget(target);
            if (this.isVisible() && this.targetElement && !this.targetElement.is(targetEl as JQuery)) {
                this.hide(() => {
                    showWithCSSTransition();
                });
            }
            else {
                showWithCSSTransition();
            }
        }
    }

    /**
     * Get new target element using selector param.
     * @param target ID or DOM element of the target component that triggers this overlay panel.
     * @returns DOM Element or null
     */
    private getTarget(target?: string | JQuery): JQuery | null {
        if (target) {
            return PrimeFaces.resolveAs$(target);
        }
        else if (this.target) {
            return this.target;
        }

        return null;
    }

    /**
     * Aligns the overlay panel so that it is shown at the correct position.
     * @param target ID or DOM element of the target component that triggers this overlay panel.
     */
    private align(target?: string | JQuery): void {
        const allowedNegativeValuesByParentOffset = this.jq.offsetParent().offset() as JQuery.Coordinates;

        this.targetElement = this.getTarget(target);
        if (this.targetElement && this.targetElement.hasClass('p-splitbutton-menubutton')) {
            this.targetElement = this.targetElement.parent();
        }
        if (this.targetElement) {
            this.targetZindex = this.targetElement.zIndex();
        }

        PrimeFaces.utils.position(this.jq.css({ 'left': '', 'top': '', 'transform-origin': 'center top' }), {
            my: this.cfg.my
            , at: this.cfg.at
            , of: this.targetElement
            , collision: this.cfg.collision
            , using: function(pos, directions) {
                if (pos.top < -allowedNegativeValuesByParentOffset.top) {
                    pos.top = -allowedNegativeValuesByParentOffset.top;
                }

                if (pos.left < -allowedNegativeValuesByParentOffset.left) {
                    pos.left = -allowedNegativeValuesByParentOffset.left;
                }

                $(this).css('transform-origin', 'center ' + directions.vertical).css({ ...pos });
            }
        });

        const widthOffset = (this.jq.width() as number) - (this.content.width() as number);
        this.jq.css('max-width', ($(window).width() as number) - widthOffset + 'px');
    }

    /**
     * Hides this overlay panel so that it is not displayed anymore.
     * @param callback Custom callback that is invoked after this overlay panel was closed.
     */
    hide(callback?: () => void): void {
        if (this.transition) {
            this.transition.hide({
                onExit: () => {
                    this.unbindPanelEvents();
                },
                onExited: () => {
                    if (this.cfg.modal) {
                        this.disableModality();
                    }

                    this.postHide();

                    if (callback) {
                        callback();
                    }
                }
            });
        }
    }

    /**
     * Callback that is invoked after this overlay panel was opened.
     */
    private postShow(): void {

        this.callBehavior('show');

        PrimeFaces.invokeDeferredRenders(this.id);

        if (this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }

        this.applyFocus();

        if (this.target) {
            this.target.attr('aria-expanded', 'true');
        }
    }

    /**
     * Callback that is invoked after this overlay panel was closed.
     */
    private postHide(): void {
        this.callBehavior('hide');

        if (this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }

        if (this.target) {
            this.target.attr('aria-expanded', 'false');
        }
    }

    /**
     * Loads the contents of this overlay panel dynamically via AJAX, if dynamic loading is enabled.
     * @param target ID or DOM element of the target component that triggers this overlay panel.
     */
    private loadContents(target?: string | JQuery): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
                source: this.id as string,
                process: this.id as string,
                update: this.id as string,
                ignoreAutoUpdate: true,
                params: [
                    { name: this.id + '_contentLoad', value: true }
                ],
                onsuccess: (responseXML, status, xhr) => {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            this.content.html(content);
                            this.loaded = this.cfg.cache;
                        }
                    });

                    return true;
                },
                oncomplete: () => {
                    this._show(target);
                }
            };

        if(this.hasBehavior('loadContent')) {
            this.callBehavior('loadContent', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Checks whether this overlay panel is currently visible.
     * @returns `true` if this overlay panel is currently displayed, or `false` otherwise.
     */
    isVisible(): boolean {
        return this.jq.is(':visible');
    }

    /**
     * Applies focus to the first focusable element of the content in the panel.
     */
    applyFocus(): void {
        PrimeFaces.focus(null, this.getId());
    }

    override enableModality(): void {
        super.enableModality();

        if (this.targetElement) {
            this.targetElement.css('z-index', String(this.jq.css('z-index')));
        }
    }

    override disableModality(): void {
        super.disableModality();

        if (this.targetElement) {
            this.targetElement.css('z-index', String(this.targetZindex));
        }
    }

    protected override getModalTabbables(): JQuery {
        let tabbables = this.jq.find(':tabbable');

        if (this.targetElement && this.targetElement.is(':tabbable')) {
            tabbables = tabbables.add(this.targetElement);
        }

        return tabbables;
    }

    /**
     * Checks if the target has the autoHide property enabled or disabled to keep the overlay open.
     * @returns Whether this overlay should be left showing or closed.
     */
    isAutoHide(): boolean {
        return this.jq.data('autohide') || this.cfg.autoHide;
    }
}
