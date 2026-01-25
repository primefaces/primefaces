/**
 * __PrimeFaces OverlayPanel Widget__
 * 
 * OverlayPanel is a generic panel component that can be displayed on top of other content.
 * 
 * @typedef PrimeFaces.widget.OverlayPanel.OnShowCallback Callback that is invoked when the panel is shown. The overlay
 * panel widget instance ins passed as the this context.
 * @this {PrimeFaces.widget.OverlayPanel} PrimeFaces.widget.OverlayPanel.OnShowCallback
 * 
 * @typedef PrimeFaces.widget.OverlayPanel.OnHideCallback Callback that is invoked when the panel is hidden. The data table
 * widget instance ins passed as the this context.
 * @this {PrimeFaces.widget.OverlayPanel} PrimeFaces.widget.OverlayPanel.OnHideCallback
 *
 * @prop {JQuery} closerIcon The DOM element for the icon that closes the overlay panel.
 * @prop {JQuery} content The DOM element for the content of the overlay panel.
 * @prop {PrimeFaces.UnbindCallback} [hideOverlayHandler] Unbind callback for the hide overlay handler.
 * @prop {boolean} loaded When dynamic loading is enabled, whether the content was already loaded.
 * @prop {PrimeFaces.UnbindCallback} [resizeHandler] Unbind callback for the resize handler.
 * @prop {PrimeFaces.UnbindCallback} [scrollHandler] Unbind callback for the scroll handler.
 * @prop {number} showTimeout The set-timeout timer ID of the timer used for showing the overlay panel.
 * @prop {JQuery} target The DOM element for the target component that triggers this overlay panel.
 * @prop {JQuery} targetElement The DOM element for the resolved target component that triggers this overlay panel.
 * @prop {number} targetZindex The z-index of the target component that triggers this overlay panel.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * @prop {boolean} allowHide Variable used to control whether the overlay is being hovered in autoHide mode
 * 
 * @interface {PrimeFaces.widget.OverlayPanelCfg} cfg The configuration for the {@link  OverlayPanel| OverlayPanel widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DynamicOverlayWidgetCfg} cfg
 * 
 * @prop {string} cfg.appendTo Appends the overlayPanel to the given search expression.
 * @prop {string} cfg.autoHide Whether to hide overlay when hovering over overlay content when using custom show/hide.
 * @prop {string} cfg.at Position of the target relative to the panel.
 * @prop {boolean} cfg.dynamic `true` to load the content via AJAX when the overlay panel is opened, `false` to load
 * the content immediately.
 * @prop {boolean} cfg.cache Only relevant for dynamic="true": Defines if activating the panel should load the contents from server again. For cache="true" (default) the panel content is only loaded once.
 * @prop {string} cfg.hideEvent Event on target to hide the panel.
 * @prop {string} cfg.collision When the positioned element overflows the window in some direction, move it to an
 * alternative position. Similar to my and at, this accepts a single value or a pair for horizontal/vertical, e.g.,
 * `flip`, `fit`, `fit flip`, `fit none`.
 * @prop {boolean} cfg.dismissable When set `true`, clicking outside of the panel hides the overlay.
 * @prop {boolean} cfg.modal Specifies whether the document should be shielded with a partially transparent mask to
 * require the user to close the panel before being able to activate any elements in the document.
 * @prop {string} cfg.my Position of the panel relative to the target.
 * @prop {PrimeFaces.widget.OverlayPanel.OnHideCallback} cfg.onHide Client side callback to execute when the panel is
 * shown.
 * @prop {PrimeFaces.widget.OverlayPanel.OnShowCallback} cfg.onShow Client side callback to execute when the panel is
 * hidden.
 * @prop {boolean} cfg.showCloseIcon Displays a close icon to hide the overlay, default is `false`.
 * @prop {number} cfg.showDelay Delay in milliseconds applied when the overlay panel is shown.
 * @prop {string} cfg.showEvent Event on target to hide the panel. If showEvent is 'none', the overlay panel will only be displayed by `show()` or `toggle()`.
 * @prop {string} cfg.target Search expression for target component to display panel next to.
 */
PrimeFaces.widget.OverlayPanel = class OverlayPanel extends PrimeFaces.widget.DynamicOverlayWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        if (cfg.target) {
            this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, cfg.target);
            if (this.target.hasClass('ui-splitbutton')) {
                this.target = this.target.find('.ui-splitbutton-menubutton');
            }
        }
        super.init(cfg, null, null, this.target);

        this.content = this.jq.children('div.ui-overlaypanel-content');

        //configuration
        this.cfg.my = this.cfg.my || 'left top';
        this.cfg.at = this.cfg.at || 'left bottom';
        this.cfg.collision = this.cfg.collision || 'flip';
        this.cfg.showEvent = this.cfg.showEvent || 'click.ui-overlaypanel';
        this.cfg.hideEvent = this.cfg.hideEvent || 'click.ui-overlaypanel';
        this.cfg.dismissable = this.cfg.dismissable !== false;
        this.cfg.showDelay = PrimeFaces.utils.defaultNumeric(this.cfg.showDelay, 0);
        this.cfg.autoHide = this.cfg.autoHide !== false;
        this.cfg.cache = this.cfg.cache !== false;
        this.allowHide = true;

        if (this.cfg.showCloseIcon) {
            this.closerIcon = PrimeFaces.skinCloseAction($('<a href="#" class="ui-overlaypanel-close ui-state-default"><span class="ui-icon ui-icon-closethick"></span></a>'))
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

        this.transition = PrimeFaces.utils.registerCSSTransition(this.jq, 'ui-connected-overlay');
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
        // fix #4307
        this.loaded = false;

        // see #setupDialogSupport
        if (!this.cfg.appendTo) {
            PrimeFaces.utils.removeDynamicOverlay(this, this.jq, this.id, $(document.body));
        }

        this.jq.remove();
    }

    /**
     * Sets up the event listeners for the target component that triggers this overlay panel.
     * @private
     */
    bindTargetEvents() {
        var $this = this;

        //mark target and descandants of target as a trigger for a primefaces overlay
        this.target.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);

        //show and hide events for target
        if (this.cfg.showEvent === this.cfg.hideEvent) {
            var event = this.cfg.showEvent;

            this.target.on(event, function(e) {
                $this.toggle();
            });
        }
        else {
            var showEvent = this.cfg.showEvent + '.ui-overlaypanel',
                hideEvent = this.cfg.hideEvent + '.ui-overlaypanel';

            this.target.off(showEvent + ' ' + hideEvent).on(showEvent, function(e) {
                if (!$this.isVisible()) {
                    $this.show();
                    if (showEvent === 'contextmenu.ui-overlaypanel') {
                        e.preventDefault();
                    }
                }
            })
            .on(hideEvent, function(e) {
                clearTimeout($this.showTimeout);
                if ($this.isVisible()) {
                    // GitHub #8546
                    if (!$this.isAutoHide() && $(e.relatedTarget).is('div.ui-overlaypanel-content')) {
                        $this.allowHide = false;
                        return;
                    }

                    $this.hide();
                }
            });
        }

        if (this.cfg.showEvent !== 'none') {
            $this.target.off('keyup.ui-overlaypanel').on('keyup.ui-overlaypanel', function(e) {
                if (PrimeFaces.utils.blockEnterKey(e)) {
                    $this.toggle();
                }
            });
        }

        this.bindAutoHide();
    }

    /**
      * Sets up mouse listeners if autoHide is disabled to keep the overlay open if overlay has focus.
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
                if ($(e.relatedTarget).is($this.target)) {
                    return;
                }
                $this.allowHide = true;
                $this.hide();
            });
    }

    /**
     * Sets up some common event listeners always required by this widget.
     * @private
     */
    bindCommonEvents() {
        var $this = this;

        if (this.cfg.showCloseIcon) {
            this.closerIcon.on('mouseover.ui-overlaypanel', function() {
                $(this).addClass('ui-state-hover');
            })
                .on('mouseout.ui-overlaypanel', function() {
                    $(this).removeClass('ui-state-hover');
                })
                .on('click.ui-overlaypanel', function(e) {
                    $this.hide();
                    e.preventDefault();
                })
                .on('focus.ui-overlaypanel', function() {
                    $(this).addClass('ui-state-focus');
                })
                .on('blur.ui-overlaypanel', function() {
                    $(this).removeClass('ui-state-focus');
                });
        }
    }

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents() {
        var $this = this;

        //hide overlay when mousedown is at outside of overlay
        if (this.cfg.dismissable && !this.cfg.modal) {
            // anything focused outside the overlay will close it
            var eventNamespace = 'keyup.' + this.id + '_hide mousedown.' + this.id + '_hide';
            this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, eventNamespace, this.jq,
                function() { return $this.target; },
                function(e, eventTarget) {
                    if (!($this.jq.is(eventTarget) || $this.jq.has(eventTarget).length > 0 || eventTarget.closest('.ui-input-overlay').length > 0)) {
                        $this.hide();
                    }
                });
        }

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.jq, function() {
            $this.handleViewportChange();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.target, function() {
            $this.handleViewportChange();
        });
    }

    /**
     * Fired when the browser viewport is resized or scrolled.  In Mobile environment we don't want to hider the overlay
     * we want to re-align it.  This is because on some mobile browser the popup may force the browser to trigger a 
     * resize immediately and close the overlay. See GitHub #7075.
     * @private
     */
    handleViewportChange() {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.align(this.target);
        } else {
            this.hide();
        }
    }

    /**
     * Unbind all panel event listeners
     * @private
     */
    unbindPanelEvents() {
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
    toggle() {
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
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     */
    show(target) {
        if (this.isVisible()) {
            return;
        }
        var thisPanel = this;
        this.showTimeout = PrimeFaces.queueTask(function() {
            if (!thisPanel.loaded && thisPanel.cfg.dynamic) {
                thisPanel.loadContents(target);
            }
            else {
                thisPanel._show(target);
            }
        }, this.cfg.showDelay);
    }

    /**
     * Makes the overlay panel visible.
     * @private
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     */
    _show(target) {
        var $this = this;

        if (this.transition) {
            var showWithCSSTransition = function() {
                $this.transition.show({
                    onEnter: function() {
                        PrimeFaces.nextZindex($this.jq);
                        $this.align(target);
                    },
                    onEntered: function() {
                        $this.bindPanelEvents();
                        $this.postShow();

                        if ($this.cfg.modal) {
                            $this.enableModality();
                        }
                    }
                });
            };

            var targetEl = this.getTarget(target);
            if (this.isVisible() && this.targetElement && !this.targetElement.is(targetEl)) {
                this.hide(function() {
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
     * @private
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     * @return {JQuery|null} DOM Element or null
     */
    getTarget(target) {
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
     * @private
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     */
    align(target) {
        var allowedNegativeValuesByParentOffset = this.jq.offsetParent().offset();

        this.targetElement = this.getTarget(target);
        if (this.targetElement && this.targetElement.hasClass('ui-splitbutton-menubutton')) {
            this.targetElement = this.targetElement.parent();
        }
        if (this.targetElement) {
            this.targetZindex = this.targetElement.zIndex();
        }

        this.jq.css({ 'left': '', 'top': '', 'transform-origin': 'center top' })
            .position({
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

                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });

        var widthOffset = this.jq.width() - this.content.width();
        this.jq.css('max-width', $(window).width() - widthOffset + 'px');
    }

    /**
     * Hides this overlay panel so that it is not displayed anymore.
     * @param {() => void} [callback] Custom callback that is invoked after this overlay panel was closed.
     */
    hide(callback) {
        if (this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    if ($this.cfg.modal) {
                        $this.disableModality();
                    }

                    $this.postHide();

                    if (callback) {
                        callback();
                    }
                }
            });
        }
    }

    /**
     * Callback that is invoked after this overlay panel was opened.
     * @private
     */
    postShow() {

        this.callBehavior('show');

        PrimeFaces.invokeDeferredRenders(this.id);

        if (this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }

        this.applyFocus();

        if (this.target) {
            this.target.attr('aria-expanded', true);
        }
    }

    /**
     * Callback that is invoked after this overlay panel was closed.
     * @private
     */
    postHide() {
        this.callBehavior('hide');

        if (this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }

        if (this.target) {
            this.target.attr('aria-expanded', false);
        }
    }

    /**
     * Loads the contents of this overlay panel dynamically via AJAX, if dynamic loading is enabled.
     * @private
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     */
    loadContents(target) {
        var $this = this,
            options = {
                source: this.id,
                process: this.id,
                update: this.id,
                ignoreAutoUpdate: true,
                params: [
                    { name: this.id + '_contentLoad', value: true }
                ],
                onsuccess: function(responseXML, status, xhr) {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                            this.loaded = this.cfg.cache;
                        }
                    });

                    return true;
                },
                oncomplete: function() {
                    $this._show(target);
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
     * @return {boolean} `true` if this overlay panel is currently displayed, or `false` otherwise.
     */
    isVisible() {
        return this.jq.is(':visible');
    }

    /**
     * Applies focus to the first focusable element of the content in the panel.
     */
    applyFocus() {
        PrimeFaces.focus(null, this.getId());
    }

    /**
     * @override
     * @inheritdoc
     */
    enableModality() {
        super.enableModality();

        if (this.targetElement) {
            this.targetElement.css('z-index', String(this.jq.css('z-index')));
        }
    }

    /**
     * @override
     * @inheritdoc
     */
    disableModality() {
        super.disableModality();

        if (this.targetElement) {
            this.targetElement.css('z-index', String(this.targetZindex));
        }
    }

    /**
     * @override
     * @inheritdoc
     * @return {JQuery}
     */
    getModalTabbables() {
        var tabbables = this.jq.find(':tabbable');

        if (this.targetElement && this.targetElement.is(':tabbable')) {
            tabbables = tabbables.add(this.targetElement);
        }

        return tabbables;
    }

    /**
     * Checks if the target has the autoHide property enabled or disabled to keep the overlay open.
     * @return {boolean} Whether this overlay should be left showing or closed.
     */
    isAutoHide() {
        return this.jq.data('autohide') || this.cfg.autoHide;
    }
}
