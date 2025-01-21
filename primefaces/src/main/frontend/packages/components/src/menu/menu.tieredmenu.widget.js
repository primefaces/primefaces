/**
 * __PrimeFaces TieredMenu Widget__
 * 
 * TieredMenu is used to display nested submenus with overlays.
 * 
 * @typedef {"hover" | "click"} PrimeFaces.widget.TieredMenu.ToggleEvent Allowed event types for toggling a tiered menu.
 * 
 * @prop {boolean} [active] Whether the menu is currently active.
 * @prop {JQuery | null} [activeitem] The active menu item, if any.
 * @prop {JQuery | null} [lastFocusedItem] The last root menu that had focus, if any.
 * @prop {boolean} [itemClick] Set to `true` an item was clicked and se to `false` when the user clicks
 * outside the menu.
 * @prop {JQuery} links DOM element with all links for the menu entries of this tiered menu.
 * @prop {JQuery} rootLinks DOM element with all links for the root (top-level) menu entries of this tiered menu.
 * @prop {number} [timeoutId] Timeout ID, used for the animation when the menu is shown.
 * @prop {boolean} isRTL Whether the writing direction is set to right-to-left.
 * @prop {boolean} isVertical Whether component is vertical orientation like TieredMenu.
 * @prop {boolean} isHorizontal Whether component is horizontal orientation like MenuBar.
 * 
 * @interface {PrimeFaces.widget.TieredMenuCfg} cfg The configuration for the {@link  TieredMenu| TieredMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.MenuCfg} cfg 
 * 
 * @prop {boolean} cfg.autoDisplay Defines whether the first level of submenus will be displayed on mouseover or not.
 * When set to `false`, click event is required to display this tiered menu.
 * @prop {number} cfg.showDelay Number of milliseconds before displaying menu. Default to 0 immediate.
 * @prop {number} cfg.hideDelay Number of milliseconds before hiding menu, if 0 not hidden until document.click.
 * @prop {PrimeFaces.widget.TieredMenu.ToggleEvent} cfg.toggleEvent Event to toggle the submenus.
 */
PrimeFaces.widget.TieredMenu = class TieredMenu extends PrimeFaces.widget.Menu {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.cfg.toggleEvent = this.cfg.toggleEvent || 'hover';
        this.cfg.showDelay = this.cfg.showDelay || 0;
        this.cfg.hideDelay = this.cfg.hideDelay || 0;
        this.tabIndex = this.cfg.tabIndex || "0";
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        this.rootLinks = this.jq.find('> ul.ui-menu-list > .ui-menuitem > .ui-menuitem-link');
        this.isRTL = this.jq.hasClass('ui-menu-rtl');
        this.isVertical = this.jq.find('ul.ui-menu-list').attr('aria-orientation') === 'vertical';
        this.isHorizontal = !this.isVertical;

        this.bindEvents();
    }

    /**
     * Sets up all event listeners required by this widget.
     * @protected
     */
    bindEvents() {
        this.bindItemEvents();
        this.bindKeyEvents();
        this.bindDocumentHandler();
        this.bindFocusEvents();
    }

    /**
     * Sets up all event listeners for the mouse events on the menu entries (`click` / `hover`).
     * @protected
     */
    bindItemEvents() {
        if (this.cfg.toggleEvent === 'click' || PrimeFaces.env.isTouchable(this.cfg))
            this.bindClickModeEvents();
        else if (this.cfg.toggleEvent === 'hover')
            this.bindHoverModeEvents();
    }

    /**
     * Sets up all event listeners required for focus interactions. This includes:
     * - Making the first menu item focusable by setting its tabindex
     * - Handling mouse enter and click events to manage focus state
     * - Handling focus events to highlight active menu items
     * @protected
     */
    bindFocusEvents() {
        var $this = this;

        // Make first menu item focusable
        var firstLink = this.links.filter(':not([disabled])').first();
        firstLink.attr("tabindex", $this.tabIndex);
        this.resetFocus(true);
        firstLink.removeClass('ui-state-hover ui-state-active');

        // Build event string based on toggle mode
        var focusOnClick = this.cfg.toggleEvent === 'click';
        var linkEvents = "mouseenter.tieredFocus" + (focusOnClick ? " click.tieredFocus" : "");
        
        // Bind mouse/click events to manage focus
        this.links.on(linkEvents, function(e) {
            var $link = $(this),
                $menuitem = $link.parent();
            $this.deactivate($menuitem);
            if (e.type === 'mouseenter') {
                $this.highlight($menuitem);
            }
            else {
                $link.trigger('focus');
            }
        }).on("focusin.tieredFocus", function() {
            var menuitem = $(this).parent();
            $this.highlight(menuitem);
        });
    }

    /**
     * Sets up all event listeners when `toggleEvent` is set to `hover`.
     * @protected
     */
    bindHoverModeEvents() {
        var $this = this;

        this.links.on("mouseenter.tieredHover", function() {
            var link = $(this),
                menuitem = link.parent();

            if ($this.cfg.autoDisplay || $this.active) {
                $this.activate(menuitem, false, true);
            }
            else {
                $this.highlight(menuitem);
            }
        }).on("mouseleave.tieredHover", function() {
            // clear timeout of possible delayed show event if mouseleave is fired before showDelay was over
            if (($this.cfg.autoDisplay || $this.active) && $this.cfg.showDelay > 0 && $this.timeoutId) {
                clearTimeout($this.timeoutId);
            }
        });

        this.rootLinks.on("click.tieredHover", function() {
            var link = $(this),
                menuitem = link.parent(),
                submenu = menuitem.children('ul.ui-menu-child');

            $this.itemClick = true;

            if (submenu.length === 1) {
                if (submenu.is(':visible')) {
                    $this.active = false;
                    $this.deactivate(menuitem);
                }
                else {
                    $this.active = true;
                    $this.highlight(menuitem);
                    $this.showSubmenu(menuitem, submenu);
                }
            }
        });

        this.links.filter('.ui-submenu-link').on("click", function(e) {
            $this.itemClick = true;
            e.preventDefault();
        });

        this.jq.on("mouseleave", function(e) {
            $this.deactivateAndReset(e);
        });
    }

    /**
     * Sets up all event listeners when `toggleEvent` is set to `click`.
     * @protected
     */
    bindClickModeEvents() {
        var $this = this;

        this.links.filter('.ui-submenu-link').on('click.tieredClick', function(e) {
            var link = $(this),
                menuitem = link.parent(),
                submenu = menuitem.children('ul.ui-menu-child');

            $this.itemClick = true;

            if (submenu.length) {
                if (submenu.is(':visible')) {
                    $this.deactivate(menuitem);
                }
                else {
                    $this.showSubmenu(menuitem, submenu);
                }
            }

            e.preventDefault();
        }).on('mousedown.tieredClick', function(e) {
            e.stopPropagation();
        });
    }

    /**
     * Sets up all event listners required for keyboard interactions.
     * @protected
     */
    bindKeyEvents() {
        var $this = this;

        this.links.on('keydown.tieredMenu', function(e) {
            var link = $(this),
                menuitem = link.parent(),
                isRootLink = false;

            // menubar is horizontal and is only one we care about if this is a root link
            if ($this.isHorizontal) {
                isRootLink = !menuitem.closest('ul').hasClass('ui-menu-child')
            }

            // Helper functionto navigate to a menu item
            function navigateTo(item) {
                if (item.length && item.children('a.ui-menuitem-link').length) {
                    $this.deactivate(menuitem);
                    $this.activate(item, true, false);
                }
            }

            // Helper function to close the submenu if its open
            function closeSubmenu() {
                var submenu = $('.ui-submenu-link[aria-expanded="true"]').last().parent();
                if (submenu.length === 1) {
                    $this.deactivate(menuitem);
                    $this.deactivate(submenu);
                    $this.activate(submenu, true,false);
                }
            }

            // Helper function to open the submenu if its open
            function openSubmenu() {
                if (menuitem.hasClass('ui-menu-parent')) {
                    $this.activate(menuitem);
                }
            }

            // Helper function close and hide the menu and refocus original trigger
            function closeMenuPanel() {
                if ($this.cfg.overlay) {
                    $this.reset();
                    $this.hide();

                    // re-focus original trigger if there is one
                    if ($this.trigger && $this.trigger.length > 0) {
                        $this.trigger.trigger('focus');
                    }
                }
            }

            switch (e.code) {
                case 'Home':
                case 'PageUp':
                    navigateTo(menuitem.prevAll('.ui-menuitem:last'));
                    e.preventDefault();
                    break;
                case 'End':
                case 'PageDown':
                    navigateTo(menuitem.nextAll('.ui-menuitem:last'));
                    e.preventDefault();
                    break;
                case 'ArrowUp':
                    var navigatedTo = menuitem.prevAll('.ui-menuitem:first');
                    if ((isRootLink || navigatedTo.length === 0) && !$this.isVertical) {
                        closeSubmenu();
                    }
                    else {
                        navigateTo(navigatedTo);
                    }
                    e.preventDefault();
                    break;
                case 'ArrowDown':
                    if (isRootLink) {
                        openSubmenu();
                    }
                    else {
                        navigateTo(menuitem.nextAll('.ui-menuitem:first'));
                    }
                    e.preventDefault();
                    break;
                case 'ArrowRight':
                    if (isRootLink) {
                        navigateTo(menuitem.nextAll('.ui-menuitem:first'));
                    }
                    else if ($this.isRTL) {
                        closeSubmenu();
                    }
                    else {
                        openSubmenu();
                    }
                    e.preventDefault();
                    break;
                case 'ArrowLeft':
                    if (isRootLink) {
                        navigateTo(menuitem.prevAll('.ui-menuitem:first'));
                    }
                    else if ($this.isRTL) {
                        openSubmenu();
                    }
                    else {
                        closeSubmenu();
                    }
                    e.preventDefault();
                    break;
                case 'Space':
                case 'Enter':
                case 'NumpadEnter':
                    if (menuitem.hasClass('ui-menu-parent')) {
                        $this.activate(menuitem);
                    } else {
                        link.trigger('click');
                        PrimeFaces.utils.openLink(e, link);
                        closeMenuPanel();
                    }
                    e.preventDefault();
                    break;
                case 'Escape':
                    if ($this.cfg.overlay) {
                        closeMenuPanel();
                    } else {
                        closeSubmenu();
                    }
                    e.preventDefault();
                    break;
                case 'Tab':
                    $this.reset();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Registers a delegated event listener for a mouse click on a menu entry.
     * @protected
     */
    bindDocumentHandler() {
        var $this = this,
            clickNS = 'click.' + this.id;

        $(document.body).off(clickNS).on(clickNS, function() {
            if ($this.itemClick) {
                $this.itemClick = false;
                return;
            }

            $this.reset();
        });
        this.addDestroyListener(function() {
            $(document.body).off(clickNS);
        });
    }

    /**
     * Deactivates a menu item so that it cannot be clicked and interacted with anymore.
     * @param {JQuery} menuitem Menu item (`LI`) to deactivate.
     * @param {boolean} [animate] `true` to animate the transition to the disabled state, `false` otherwise.
     */
    deactivate(menuitem, animate) {
        var $this = this;
        this.activeitem = null;
        menuitem.removeClass('ui-menuitem-active ui-menuitem-highlight');
        var $link = menuitem.children('a.ui-menuitem-link');
        this.unfocus($link);

        var activeSibling = menuitem.siblings('.ui-menuitem-active');
        if (activeSibling.length) {
            activeSibling.find('li.ui-menuitem-active').each(function() {
                $this.deactivate($(this));
            });
            $this.deactivate(activeSibling);
        }

        var submenu = menuitem.children('ul.ui-menu-child');
        if (submenu.length > 0) {
            $link.attr('aria-expanded', 'false');
            if (animate)
                submenu.fadeOut('fast');
            else
                submenu.hide();
        }
    }

    /**
     * Activates a menu item so that it can be clicked and interacted with.
     * 
     * @param {JQuery} menuitem - The menu item to activate.
     * @param {boolean} [focus=true] - If false, does not focus the menu item.
     * @param {boolean} [showSubMenu=true] - If false, does not show the submenu.
     */
    activate(menuitem, focus = true, showSubMenu = true) {
        this.highlight(menuitem);

        // if this is a root menu item.
        if (menuitem.parent().is('ul.ui-menu-list:not(.ui-menu-child)')) {
            this.lastFocusedItem = menuitem;
        }

        // focus the menu item when activated
        if (focus) {
            this.focus(menuitem.children('a.ui-menuitem-link'));
        }

        if (showSubMenu) {
            var submenu = menuitem.children('ul.ui-menu-child');
            if (submenu.length == 1) {
                this.showSubmenu(menuitem, submenu, focus);
            }
        }
    }

    /**
     * Highlights the given menu item by applying the proper CSS classes and focusing the associated link.
     *
     * @param {JQuery} menuitem - The menu item to highlight.
     */
    highlight(menuitem) {
        this.activeitem = menuitem;
        menuitem.addClass('ui-menuitem-active ui-menuitem-highlight');
        menuitem.children('a.ui-menuitem-link').addClass('ui-state-hover');
    }

    /**
     * Shows the given submenu of a menu item.
     * @param {JQuery} menuitem A menu item (`LI`) with children.
     * @param {JQuery} submenu A child of the menu item.
     * @param {boolean} [focus=true] - If false, does not focus the submenu.
     */
    showSubmenu(menuitem, submenu, focus = true) {
        var pos = {
            my: this.isRTL ? 'right top' : 'left top',
            at: this.isRTL ? 'left top' : 'right top',
            of: menuitem,
            collision: 'flipfit'
        };

        submenu.css('z-index', PrimeFaces.nextZindex())
            .show()
            .position(pos);

        //avoid queuing multiple runs
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }

        this.timeoutId = PrimeFaces.queueTask(function() {
            submenu.css('z-index', PrimeFaces.nextZindex())
                .show()
                .position(pos);
            var $link = menuitem.children('a.ui-menuitem-link');
            $link.attr('aria-expanded', 'true');
            if (focus) {
                submenu.find('a.ui-menuitem-link:focusable:first').trigger('focus');
            }
        }, this.cfg.showDelay);
    }

    /**
     * Deactivates all items and resets the state of this widget to its orignal state such that only the top-level menu
     * items are shown. 
     */
    reset() {
        var $this = this;
        this.active = false;

        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });
        this.resetFocus(!this.lastFocusedItem);
        if (this.lastFocusedItem) {
            this.lastFocusedItem.children('a.ui-menuitem-link').attr('tabindex', $this.tabIndex);
        }
        this.links.removeClass('ui-state-active ui-state-hover');
    }

    /**
     * Deactivates the current active menu item and resets the menu state after a delay.
     * 
     * @param {Event} [e] - The event object (optional).
     */
    deactivateAndReset(e) {
        var $this = this;

        // Deactivate the current active menu item if it exists
        if (this.activeitem) {
            this.deactivate(this.activeitem);
        }

        // If hideDelay is configured, reset the menu state after the delay
        if (this.cfg.hideDelay > 0) {
            this.timeoutId = PrimeFaces.queueTask(() => {
                $this.reset();
            }, this.cfg.hideDelay);
        }
        else {
            this.reset();
        }
    }

}
