/**
 * __PrimeFaces Menu Widget__
 * 
 * Base class for the different menu widgets, such as the `PlainMenu` or the `TieredMenu`.
 * 
 * @prop {PrimeFaces.UnbindCallback} [hideOverlayHandler] Unbind callback for the hide overlay handler.
 * @prop {boolean} itemMouseDown `true` if a menu item was clicked and the mouse button is still pressed.
 * @prop {PrimeFaces.UnbindCallback} [resizeHandler] Unbind callback for the resize handler.
 * @prop {PrimeFaces.UnbindCallback} [scrollHandler] Unbind callback for the scroll handler.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * @prop {JQuery} trigger DOM element which triggers this menu.
 * 
 * @interface {PrimeFaces.widget.MenuCfg} cfg The configuration for the {@link  Menu| Menu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg 
 * 
 * @prop {string} cfg.appendTo Search expression for the element to which the menu overlay is appended.
 * @prop {string} cfg.at Defines which position on the target element to align the positioned element against
 * @prop {string} cfg.collision When the positioned element overflows the window in some direction, move it to an
 * alternative position.
 * @prop {string} cfg.my Defines which position on the element being positioned to align with the target element.
 * @prop {boolean} cfg.overlay `true` if this menu is displayed as an overlay, or `false` otherwise.
 * @prop {JQueryUI.JQueryPositionOptions} cfg.pos Describes how to align this menu.
 * @prop {string} cfg.trigger ID of the event which triggers this menu.
 * @prop {string} cfg.triggerEvent Event which triggers this menu.
 * @prop {string} cfg.tabIndex The default tabIndex of this component. Default to 0.
 * @prop {string | undefined} tabIndex The default tabIndex of this component. Default to 0.
 */
PrimeFaces.widget.Menu = class Menu extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        
        this.tabIndex = this.cfg.tabIndex || "0";

        if (this.cfg.overlay) {
            this.initOverlay();
        }
    }

    /**
     * Gets the Menu jQuery element.  Override in subclasses to define the menu panel.
     * @returns {JQuery} The jQuery object for the menu.
     * @protected
     */
    getMenuElement() {
        return this.jq;
    }

    /**
     * Initializes the overlay. Finds the element to which to append this menu and appends it to that element.
     * @protected
     */
    initOverlay() {
        var $menu = this.getMenuElement();
        $menu.addClass('ui-menu-overlay');

        this.cfg.trigger = this.cfg.trigger.replace(/\\\\:/g, "\\:");

        // register trigger and events
        this.bindTrigger();

        if (!this.cfg.appendTo) {
            this.cfg.appendTo = '@(body)';
        }
        PrimeFaces.utils.registerDynamicOverlay(this, $menu, this.id);
        this.transition = PrimeFaces.utils.registerCSSTransition($menu, 'ui-connected-overlay');

        // register for AJAX updates on trigger
        this.bindAjaxListener();

        //dialog support
        this.setupDialogSupport();
    }

    /**
      * Sets up the event listener on the trigger.
      * @private
      */
    bindTrigger() {
        var $this = this;
        var $menu = this.getMenuElement();
        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector($menu, this.cfg.trigger);

        //mark trigger and descendants of trigger as a trigger for a primefaces overlay
        this.trigger.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        this.trigger.off(this.cfg.triggerEvent + '.ui-menu').on(this.cfg.triggerEvent + '.ui-menu', function(e) {
            var trigger = $(this);

            if ($menu.is(':visible')) {
                $this.hide();
            }
            else {
                $this.show();

                if (trigger.is(':button')) {
                    trigger.addClass('ui-state-focus');
                }

                e.preventDefault();
            }
        });

        this.cfg.pos = {
            my: this.cfg.my,
            at: this.cfg.at,
            of: this.trigger,
            collision: this.cfg.collision || "flip",
            using: function(pos, directions) {
                $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
            }
        };
    }

    /**
      * Sets up the global event listeners on the document in case trigger has been updated in DOM
      * @private
      */
    bindAjaxListener() {
        var $this = this,
            ajaxEventName = 'pfAjaxUpdated.' + this.id;

        //listen global ajax send and complete callbacks
        $(document).off(ajaxEventName).on(ajaxEventName, function(e, xhr, settings) {
            // #3921 if the trigger is updated we need to re-subscribe
            if (PrimeFaces.ajax.Utils.isXhrSourceATrigger($this, settings, true)) {
                PrimeFaces.queueTask(function() { $this.bindTrigger() });
            }
        });
        this.addDestroyListener(function() {
            $(document).off(ajaxEventName);
        });
    }

    /**
     * Sets up all panel event listeners
     * @protected
     */
    bindPanelEvents() {
        var $this = this;
        var $menu = this.getMenuElement();

        //hide overlay on document click
        this.itemMouseDown = false;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $menu,
            function() { return $this.trigger; },
            function(e, eventTarget) {
                var menuItemLink = '.ui-menuitem-link:not(.ui-submenu-link, .ui-state-disabled)';

                if (eventTarget.is(menuItemLink) || eventTarget.closest(menuItemLink).length) {
                    $this.itemMouseDown = true;
                }
                else if (!($menu.is(eventTarget) || $menu.has(eventTarget).length > 0)) {
                    $this.hide(e);
                }
            });

        var mouseUpEventName = 'mouseup.' + this.id;
        $(document.body).off(mouseUpEventName).on(mouseUpEventName, function(e) {
            if ($this.itemMouseDown) {
                $this.hide(e);
                $this.itemMouseDown = false;
            }
        });
        this.addDestroyListener(function() {
            $(document.body).off(mouseUpEventName);
        });

        //Hide overlay on resize
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', $menu, function() {
            $this.handleViewportChange();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.trigger, function() {
            $this.handleViewportChange();
        });
    }

    /**
     * Unbind all panel event listeners
     * @protected
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

        $(document.body).off('mouseup.' + this.id);
    }

    /**
     * Fired when the browser viewport is resized or scrolled.  In Mobile environment we don't want to hider the overlay
     * we want to re-align it.  This is because on some mobile browser the popup may force the browser to trigger a 
     * resize immediately and close the overlay. See GitHub #7075.
     * @private
     */
    handleViewportChange() {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.align();
        } else {
            this.hide();
        }
    }

    /**
     * Performs some setup required to make this overlay menu work with dialogs.
     * @protected
     */
    setupDialogSupport() {
        var dialog = this.trigger.parents('.ui-dialog:first');

        if (dialog.length == 1 && dialog.css('position') === 'fixed') {
            this.getMenuElement().css('position', 'fixed');
        }
    }

    /**
     * Shows (displays) this menu so that it becomes visible and can be interacted with.
     */
    show() {
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    $this.getMenuElement().css('z-index', PrimeFaces.nextZindex());
                    $this.align();
                },
                onEntered: function() {
                    $this.bindPanelEvents();
                    $this.resetFocus(true);
                    $this.getMenuElement().find('a.ui-menuitem-link:focusable:first').trigger('focus');
                }
            });
        }
    }

    /**
     * Hides this menu so that it becomes invisible and cannot be interacted with any longer.
     */
    hide() {
        if (this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    if ($this.trigger && $this.trigger.is(':button')) {
                        $this.trigger.trigger('focus');
                    }
                }
            });
        }
    }

    /**
     * Aligns this menu as specified in its widget configuration (property `pos`).
     */
    align() {
        this.getMenuElement().css({ left: '0', top: '0', 'transform-origin': 'center top' }).position(this.cfg.pos);
    }

    /**
     * Resets all menu items to tabindex="0" except the first item if resetFirst
     * @param {boolean} resetFirst whether to reset to the first cell to tabindex="0"
     */
    resetFocus(resetFirst) {
        // default all links to not focusable
        var $container = this.getMenuElement();
        var focusableLinks = $container.find("a.ui-menuitem-link:not(.ui-state-disabled)");
        focusableLinks.removeClass('ui-state-hover ui-state-active').attr('tabindex', "-1");

        if (resetFirst) {
            // reset aria-expanded
            focusableLinks.each(function() {
                var $link = $(this);
                if ($link.attr('aria-expanded') !== undefined) {
                    $link.attr('aria-expanded', 'false');
                }
            });

            // the very first link should be focusable
            var defaultTabIndex = this.tabIndex || "0";
            var focused = focusableLinks.filter(':focusable:first').first();
            focused.addClass('ui-state-hover ui-state-active').attr('tabindex', defaultTabIndex);
        }
    }

    /**
     * Selects the menu item link by making it focused and setting tabindex to "0" for ARIA.
     * 
     * @param {JQuery} menulink - The menu item (`<a>`) to select.
     * @param {JQuery.TriggeredEvent} [event] - The event that triggered the focus.
     */
    focus(menulink, event) {
        if (menulink.hasClass('ui-state-disabled')) {
            return;
        }
        this.resetFocus(false);
        var defaultTabIndex = this.tabIndex || "0";
        var cssClass = 'ui-state-hover';
        if (!event || !event.type.startsWith('mouse')) {
             // only add active class if the event is not a mouse event like a click or keyboard event
            cssClass = cssClass + ' ui-state-active';
        }
        menulink.addClass(cssClass).attr('tabindex', defaultTabIndex).trigger('focus');
    }

    /**
     * Unselect the menu item link by removing focus and tabindex=-1 for ARIA.
     * @param {JQuery} menulink Menu item (`A`) to unselect.
     * @param {JQuery.TriggeredEvent} [event] - The event that triggered the unfocus.
     */
    unfocus(menulink, event) {
        if (menulink.hasClass('ui-state-disabled')) {
            return;
        }

        var cssClass = 'ui-state-hover';
        if (!event || !event.type.startsWith('mouse')) {
            // only remove active class if the event is not a mouse event like a click or keyboard event
            cssClass = cssClass + ' ui-state-active';
        }
        menulink.removeClass(cssClass).attr('tabindex', -1);
    }
}





