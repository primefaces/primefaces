/**
 * __PrimeFaces TieredMenu Widget__
 * 
 * TieredMenu is used to display nested submenus with overlays.
 * 
 * @typedef {"hover" | "click"} PrimeFaces.widget.TieredMenu.ToggleEvent Allowed event types for toggling a tiered menu.
 * 
 * @prop {boolean} [active] Whether the menu is currently active.
 * @prop {JQuery | null} [activeitem] The active menu item, if any.
 * @prop {boolean} [itemClick] Set to `true` an item was clicked and se to `false` when the user clicks
 * outside the menu.
 * @prop {JQuery} links DOM element with all links for the menu entries of this tiered menu.
 * @prop {JQuery} rootLinks DOM element with all links for the root (top-level) menu entries of this tiered menu.
 * @prop {number} [timeoutId] Timeout ID, used for the animation when the menu is shown.
 * @prop {boolean} isRTL Whether the writing direction is set to right-to-left.
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
PrimeFaces.widget.TieredMenu = PrimeFaces.widget.Menu.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.cfg.toggleEvent = this.cfg.toggleEvent || 'hover';
        this.cfg.showDelay = this.cfg.showDelay || 0;
        this.cfg.hideDelay = this.cfg.hideDelay || 0;
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        this.rootLinks = this.jq.find('> ul.ui-menu-list > .ui-menuitem > .ui-menuitem-link');
        this.isRTL = this.jq.hasClass('ui-menu-rtl');

        this.bindEvents();
    },

    /**
     * Sets up all event listeners required by this widget.
     * @protected
     */
    bindEvents: function() {
        this.bindItemEvents();
        this.bindKeyEvents();
        this.bindDocumentHandler();
    },

    /**
     * Sets up all event listeners for the mouse events on the menu entries (`click` / `hover`).
     * @protected
     */
    bindItemEvents: function() {
        var $this = this;
        if (this.cfg.toggleEvent === 'click' || PrimeFaces.env.isTouchable(this.cfg))
            this.bindClickModeEvents();
        else if (this.cfg.toggleEvent === 'hover')
            this.bindHoverModeEvents();
            
        this.jq.on("blur.tieredMenu focusout.tieredMenu", function(e) {
            if (!$this.jq.has(e.relatedTarget).length) {
                if ($this.activeitem) {
                    $this.deactivate($this.activeitem);
                }

                if ($this.cfg.hideDelay > 0) {
                    $this.timeoutId = PrimeFaces.queueTask(function() {
                        $this.reset();
                    }, $this.cfg.hideDelay);
                }
            }
        });
    },

    /**
     * Sets up all event listeners when `toggleEvent` is set to `hover`.
     * @protected
     */
    bindHoverModeEvents: function() {
        var $this = this;

        this.links.on("mouseenter", function() {
            var link = $(this),
                menuitem = link.parent();

            var activeSibling = menuitem.siblings('.ui-menuitem-active');
            if (activeSibling.length === 1) {
                activeSibling.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(activeSibling);
            }

            if ($this.cfg.autoDisplay || $this.active) {
                if (menuitem.hasClass('ui-menuitem-active'))
                    $this.reactivate(menuitem);
                else
                    $this.activate(menuitem);
            }
            else {
                $this.highlight(menuitem);
            }
        });

        this.rootLinks.on("click", function(e) {
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

        this.jq.find('ul.ui-menu-list').on("mouseleave", function(e) {
            if ($this.activeitem) {
                $this.deactivate($this.activeitem);
            }
            
            if ($this.cfg.hideDelay > 0) {
                $this.timeoutId = PrimeFaces.queueTask(function() {
                    $this.reset();
                }, $this.cfg.hideDelay);
            }

            e.stopPropagation();
        });
    },

    /**
     * Sets up all event listeners when `toggleEvent` is set to `click`.
     * @protected
     */
    bindClickModeEvents: function() {
        var $this = this;

        this.links.on("mouseenter", function() {
            var menuitem = $(this).parent();

            if (!menuitem.hasClass('ui-menuitem-active')) {
                menuitem.addClass('ui-menuitem-highlight').children('a.ui-menuitem-link').addClass('ui-state-hover');
            }
        }).on("mouseleave", function() {
            var menuitem = $(this).parent();

            if (!menuitem.hasClass('ui-menuitem-active')) {
                menuitem.removeClass('ui-menuitem-highlight').children('a.ui-menuitem-link').removeClass('ui-state-hover');
            }
        });

        this.links.filter('.ui-submenu-link').on('click.tieredMenu', function(e) {
            var link = $(this),
                menuitem = link.parent(),
                submenu = menuitem.children('ul.ui-menu-child');

            $this.itemClick = true;

            var activeSibling = menuitem.siblings('.ui-menuitem-active');
            if (activeSibling.length) {
                activeSibling.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(activeSibling);
            }

            if (submenu.length) {
                if (submenu.is(':visible')) {
                    $this.deactivate(menuitem);
                    menuitem.addClass('ui-menuitem-highlight').children('a.ui-menuitem-link').addClass('ui-state-hover');
                }
                else {
                    menuitem.addClass('ui-menuitem-active').children('a.ui-menuitem-link').removeClass('ui-state-hover').addClass('ui-state-active');
                    $this.showSubmenu(menuitem, submenu);
                }
            }

            e.preventDefault();
        }).on('mousedown.tieredMenu', function(e) {
            e.stopPropagation();
        });
    },

    /**
     * Sets up all event listners required for keyboard interactions.
     * @protected
     */
    bindKeyEvents: function() {
        var $this = this;

        // make first focusable
        var firstLink = this.links.filter(':not([disabled])').first();
        firstLink.attr("tabindex", "0");
        this.resetFocus(true);
        firstLink.removeClass('ui-state-hover ui-state-active');

        this.links.on("mouseenter.tieredMenu click.tieredMenu", function(e) {
            $(this).trigger('focus');
        }).on("focusin.tieredMenu", function(e) {
            $this.highlight($(this).parent(), false);
        }).on("focusout.tieredMenu mouseleave.tieredMenu", function(e) {
            $this.deactivate($(this));
        }).on('keydown.tieredMenu', function(e) {
            var link = $(this),
                menuitem = link.parent(),
                submenu = menuitem.children('ul.ui-menu-child');

            switch (e.key) {
                case 'ArrowUp':
                    var prevItem = menuitem.prevAll('.ui-menuitem:first');
                    if (prevItem.length) {
                        $this.deactivate(menuitem);
                        $this.highlight(prevItem);
                    }
                    e.preventDefault();
                    break;

                case 'ArrowDown':
                    var nextItem = menuitem.nextAll('.ui-menuitem:first');
                    if (nextItem.length) {
                        $this.deactivate(menuitem);
                        $this.highlight(nextItem);
                    }
                    e.preventDefault();
                    break;
                case 'ArrowRight':
                    if (menuitem.hasClass('ui-menu-parent')) {
                        $this.activate(menuitem);
                    }
                    e.preventDefault();
                    break;
                case 'Enter':
                case 'NumpadEnter':
                    var currentLink = menuitem.children('.ui-menuitem-link');
                    currentLink.trigger('click');
                    PrimeFaces.utils.openLink(e, currentLink);
                    e.preventDefault();
                    break;

                case 'ArrowLeft':
                    $this.deactivate($('.ui-submenu-link[aria-expanded="true"]').last().parent());
                    break;
                case 'Escape':
                    if ($this.cfg.overlay) {
                        $this.hide();
                        $this.trigger.trigger('focus');
                    }
                    else {
                        $this.deactivate($('.ui-submenu-link[aria-expanded="true"]').last().parent());
                    }
                    e.preventDefault();
                    break;
            }
        });
    },

    /**
     * Registers a delegated event listener for a mouse click on a menu entry.
     * @protected
     */
    bindDocumentHandler: function() {
        var $this = this,
            clickNS = 'click.' + this.id;

        $(document.body).off(clickNS).on(clickNS, function(e) {
            if ($this.itemClick) {
                $this.itemClick = false;
                return;
            }

            $this.reset();
        });
        this.addDestroyListener(function() {
            $(document.body).off(clickNS);
        });
    },

    /**
     * Deactivates a menu item so that it cannot be clicked and interacted with anymore.
     * @param {JQuery} menuitem Menu item (`LI`) to deactivate.
     * @param {boolean} [animate] `true` to animate the transition to the disabled state, `false` otherwise.
     */
    deactivate: function(menuitem, animate) {
        this.activeitem = null;
        this.unfocus(menuitem.children('a.ui-menuitem-link'));
        menuitem.removeClass('ui-menuitem-active ui-menuitem-highlight');

        var submenu = menuitem.children('ul.ui-menu-child').attr('aria-expanded', 'false');
        if (animate)
            submenu.fadeOut('fast');
        else
            submenu.hide();
    },

    /**
     * Activates a menu item so that it can be clicked and interacted with.
     * @param {JQuery} menuitem Menu item (`LI`) to activate.
     */
    activate: function(menuitem) {
        this.highlight(menuitem);

        var submenu = menuitem.children('ul.ui-menu-child');
        if (submenu.length == 1) {
            submenu.attr('aria-expanded', 'true');
            this.showSubmenu(menuitem, submenu);
        }
    },

    /**
     * Reactivates the given menu item.
     * @protected
     * @param {JQuery} menuitem Menu item (`LI`) to reactivate.
     */
    reactivate: function(menuitem) {
        this.activeitem = menuitem;
        var submenu = menuitem.children('ul.ui-menu-child'),
            activeChilditem = submenu.children('li.ui-menuitem-active:first'),
            _self = this;

        if (activeChilditem.length == 1) {
            _self.deactivate(activeChilditem);
        }
    },

    /**
     * Highlights the given menu item by applying the proper CSS classes.
     * @param {JQuery} menuitem Menu item to highlight.
     */
    highlight: function(menuitem, shouldFocus) {
        menuitem.addClass('ui-menuitem-active');
        this.activeitem = menuitem;
        var link = menuitem.children('a.ui-menuitem-link');
        this.focus(link);
        if (shouldFocus === true || shouldFocus === undefined) {
            link.trigger('focus');
        }
    },

    /**
     * Shows the given submenu of a menu item.
     * @param {JQuery} menuitem A menu item (`LI`) with children.
     * @param {JQuery} submenu A child of the menu item.
     */
    showSubmenu: function(menuitem, submenu) {
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
            submenu.find('a.ui-menuitem-link:focusable:first').trigger('focus');
        }, this.cfg.showDelay);
    },

    /**
     * Deactivates all items and resets the state of this widget to its orignal state such that only the top-level menu
     * items are shown. 
     */
    reset: function() {
        var $this = this;
        this.active = false;

        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });
        this.resetFocus(true);
        this.links.removeClass('ui-state-hover');
    }

});
