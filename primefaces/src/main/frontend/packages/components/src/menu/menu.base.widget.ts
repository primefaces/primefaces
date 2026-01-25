/**
 * The configuration for the {@link Menu} widget.
 * 
 * You can access this configuration via {@link Menu.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface MenuCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * Search expression for the element to which the menu overlay is appended.
     */
    appendTo: string;
    /**
     * Defines which position on the target element to align the positioned element against
     */
    at: string;
    /**
     * When the positioned element overflows the window in some direction, move it to an
     * alternative position.
     */
    collision: string;
    /**
     * Defines which position on the element being positioned to align with the target element.
     */
    my: string;
    /**
     * `true` if this menu is displayed as an overlay, or `false` otherwise.
     */
    overlay: boolean;
    /**
     * Describes how to align this menu.
     */
    pos: JQueryUI.JQueryPositionOptions;
    /**
     * The tab index of the menu.
     */
    tabIndex: string;
    /**
     * ID of the event which triggers this menu.
     */
    trigger: string;
    /**
     * Event which triggers this menu.
     */
    triggerEvent: string;
}

/**
 * __PrimeFaces Menu Widget__
 * 
 * Base class for the different menu widgets, such as the `PlainMenu` or the `TieredMenu`.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class Menu<Cfg extends MenuCfg = MenuCfg>  extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * Unbind callback for the hide overlay handler.
     */
    hideOverlayHandler?: PrimeType.Unbindable;

    /**
     * `true` if a menu item was clicked and the mouse button is still pressed.
     */
    itemMouseDown: boolean = false;

    /**
     * The DOM element for the form element that can be targeted via arrow or tab keys.
     */
    keyboardTarget: JQuery = $();

    /**
     * Unbind callback for the resize handler.
     */
    resizeHandler?: PrimeType.Unbindable;

    /**
     * Unbind callback for the scroll handler.
     */
    scrollHandler?: PrimeType.Unbindable;

    /**
     * The tab index of the menu.
     */
    tabIndex: string = "0";

    /**
     * Handler for CSS transitions used by this widget.
     */
    transition?: PrimeType.CssTransitionHandler | null;

    /**
     * DOM element which triggers this menu.
     */
    trigger: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        
        this.tabIndex = this.cfg.tabIndex || "0";

        if (this.cfg.overlay) {
            this.initOverlay();
        }
    }

    /**
     * Gets the Menu jQuery element.  Override in subclasses to define the menu panel.
     * @returns The jQuery object for the menu.
     */
    protected getMenuElement(): JQuery {
        return this.jq;
    }

    /**
     * Initializes the overlay. Finds the element to which to append this menu and appends it to that element.
     */
    protected initOverlay(): void {
        var $menu = this.getMenuElement();
        $menu.addClass('ui-menu-overlay');

        this.cfg.trigger = this.cfg.trigger?.replace(/\\\\:/g, "\\:");

        // register trigger and events
        this.bindTrigger();

        if (!this.cfg.appendTo) {
            this.cfg.appendTo = '@(body)';
        }
        PrimeFaces.utils.registerDynamicOverlay(this, $menu, this.getId());
        this.transition = PrimeFaces.utils.registerCSSTransition($menu, 'ui-connected-overlay');

        // register for AJAX updates on trigger
        this.bindAjaxListener();

        //dialog support
        this.setupDialogSupport();
    }

    /**
      * Sets up the event listener on the trigger.
      */
    private bindTrigger(): void {
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
            using: function(pos: { top: number; left: number }, directions: {horizontal: number; vertical: number;}) {
                $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
            }
        };
    }

    /**
      * Sets up the global event listeners on the document in case trigger has been updated in DOM
      */
    private bindAjaxListener(): void {
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
     */
    protected bindPanelEvents(): void {
        const $menu = this.getMenuElement();

        //hide overlay on document click
        this.itemMouseDown = false;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $menu,
            () => this.trigger,
            (_, eventTarget) => {
                const menuItemLink = '.ui-menuitem-link:not(.ui-submenu-link, .ui-state-disabled)';

                if (eventTarget.is(menuItemLink) || eventTarget.closest(menuItemLink).length) {
                    this.itemMouseDown = true;
                }
                else if (!($menu.is(eventTarget) || $menu.has(eventTarget[0] ?? "").length > 0)) {
                    this.hide();
                }
            });

        var mouseUpEventName = 'mouseup.' + this.id;
        $(document.body).off(mouseUpEventName).on(mouseUpEventName, () => {
            if (this.itemMouseDown) {
                this.hide();
                this.itemMouseDown = false;
            }
        });
        this.addDestroyListener(function() {
            $(document.body).off(mouseUpEventName);
        });

        //Hide overlay on resize
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', $menu, () => {
            this.handleViewportChange();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.trigger, () => {
            this.handleViewportChange();
        });
    }

    /**
     * Unbind all panel event listeners
     */
    protected unbindPanelEvents(): void {
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
     */
    private handleViewportChange(): void {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.align();
        } else {
            this.hide();
        }
    }

    /**
     * Performs some setup required to make this overlay menu work with dialogs.
     */
    protected setupDialogSupport(): void {
        var dialog = this.trigger.parents('.ui-dialog:first');

        if (dialog.length == 1 && dialog.css('position') === 'fixed') {
            this.getMenuElement().css('position', 'fixed');
        }
    }

    /**
     * Shows (displays) this menu so that it becomes visible and can be interacted with.
     */
    show(): void {
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    PrimeFaces.nextZindex($this.getMenuElement());
                    $this.align();
                },
                onEntered: function() {
                    $this.bindPanelEvents();
                    $this.resetFocus(true);
                    $this.getMenuElement().find('a.ui-menuitem-link:not(.ui-state-disabled):focusable:first').trigger('focus');
                }
            });
        }
    }

    /**
     * Hides this menu so that it becomes invisible and cannot be interacted with any longer.
     */
    hide(): void {
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
    align(): void {
        this.getMenuElement().css({ left: '0', top: '0', 'transform-origin': 'center top' }).position(this.cfg.pos ?? {});
    }

    /**
     * Resets all menu items to tabindex="0" except the first item if resetFirst
     * @param resetFirst whether to reset to the first cell to tabindex="0"
     */
    resetFocus(resetFirst: boolean): void {
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
     * @param menulink - The menu item (`<a>`) to select.
     * @param event - The event that triggered the focus.
     */
    focus(menulink: JQuery, event?: JQuery.TriggeredEvent): void {
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
     * @param menulink Menu item (`A`) to unselect.
     * @param event - The event that triggered the unfocus.
     */
    unfocus(menulink: JQuery, event?: JQuery.TriggeredEvent): void {
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
