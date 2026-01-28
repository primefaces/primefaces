
/**
 * __PrimeFaces PlainMenu Widget__
 * 
 * Menu is a navigation component with sub menus and menu items.
 * 
 * @prop {JQuery} menuitemLinks DOM elements with the links of each menu item.
 * @prop {JQuery} headers DOM elements for each toggleable header item
 * @prop {string} stateKey Name of the HTML5 Local Store that is used to store the state of this plain menu (expanded / collapsed
 * menu items).
 * @prop {string[]} collapsedIds A list with the ID of each menu item (with children) that is collapsed.
 * 
 * @interface {PrimeFaces.widget.PlainMenuCfg} cfg The configuration for the {@link  PlainMenu| PlainMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.MenuCfg} cfg
 * 
 * @prop {boolean} cfg.toggleable `true` if grouped items can be toggled (expanded / collapsed), or `false` otherwise.
 * @prop {boolean} cfg.statefulGlobal When enabled, menu state is saved globally across pages. If disabled then state 
 * is stored per view/page.
 */
PrimeFaces.widget.PlainMenu = PrimeFaces.widget.Menu.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.menuitemLinks = this.jq.find('.ui-menuitem-link:not(.ui-state-disabled)');
        this.headers = this.jq.find('> .ui-menu-list > .ui-widget-header');

        this.bindEvents();
        this.bindToggleable();
        this.bindOverlay();
    },

    /**
     * Binds the necessary events for the menu if it is toggleable. This includes setting up the state management
     * by initializing the storage key and restoring the state from storage.
     * @private
     */
    bindToggleable: function() {
        if (!this.cfg.toggleable) return;
        
        var $this = this;
        this.cfg.statefulGlobal = Boolean(this.cfg.statefulGlobal);
        // Initialize collapsedIds with currently collapsed headers
        this.collapsedIds = this.headers
            .filter(function () {
                return $(this)
                    .find('> h3 > .ui-icon')
                    .hasClass('ui-icon-triangle-1-e');
            })
            .map(function () {
                return $(this).attr('id') || '';
            })
            .get()
            .filter(function (id) {
                return id !== '';
            });
        this.createStorageKey();
        this.restoreState();

        this.headers.on('mouseover.menu', function() {
            $(this).addClass('ui-state-hover');
        }).on('mouseout.menu', function() {
            $(this).removeClass('ui-state-hover');
        }).on('click.menu', function(e) {
            var header = $(this);
            var icon = header.find('> h3 > .ui-icon');

            if (icon.hasClass('ui-icon-triangle-1-s')) {
                $this.collapseSubmenu(header, true);
            } else {
                $this.expandSubmenu(header, true);
            }

            PrimeFaces.clearSelection();
            e.preventDefault();
        });
    },

    /**
     * Binds overlay-specific event handlers if the overlay configuration is enabled.
     * This includes hiding the menu on certain key presses or clicks, and managing focus.
     * @private
     */
    bindOverlay: function() {
        var $this = this;

        if (this.cfg.overlay) {
            // Hide menu when any menu item link is clicked
            this.menuitemLinks.on("click", function() {
                $this.hide();
            });

            // Handle keyboard navigation for overlay
            this.trigger.on('keydown.ui-menu', function(e) {
                switch (e.key) {
                    case 'ArrowDown':
                        if (!$this.jq.is(':visible')) {
                            $this.show();
                        }
                        e.preventDefault();
                        break;

                    case 'Tab':
                        if ($this.jq.is(':visible')) {
                            $this.hide();
                        }
                        break;
                }
            });
        } else {
            // Handle focus events for the menu
            this.jq.off('focusout.menu focusin.menu').on({
                "focusout.menu": function(e) {
                    if (!e.relatedTarget || !$this.jq.has(e.relatedTarget).length) {
                        $this.resetFocusState();
                    }
                },
                "focusin.menu": function(e) {
                    if (e.relatedTarget && !$this.jq.has(e.relatedTarget).length) {
                        $this.focus($this.menuitemLinks.filter(':not([disabled])').first(), e);
                    }
                }
            });
        }
    },
 
    /**
     * Binds event handlers to menu item links for interaction via mouse and keyboard.
     * This includes setting the initial focus, handling mouse enter and leave, click events,
     * and keyboard navigation using arrow keys, space, and enter.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        // Set the first focusable menu item
        this.resetFocusState();

        // Bind mouse and click events to focus items
        this.menuitemLinks.on("mouseenter.menu click.menu", function(e) {
            $this.focus($(this), e);
        }).on("mouseleave.menu", function(e) {
            $this.unfocus($(this), e);
        });

        // Bind keyboard navigation events
        this.menuitemLinks.on('keydown.menu', function(e) {
            var currentLink = $this.menuitemLinks.filter('.ui-state-active:first');
            switch (e.code) {
                case 'Home':
                case 'PageUp':
                    $this.navigateMenu(e, currentLink, 'prev', 'last');
                    e.preventDefault();
                    break;
                case 'End':
                case 'PageDown':
                    $this.navigateMenu(e, currentLink, 'next', 'last');
                    e.preventDefault();
                    break;
                case 'ArrowUp':
                    $this.navigateMenu(e, currentLink, 'prev', 'first');
                    e.preventDefault();
                    break;
                case 'ArrowDown':
                    $this.navigateMenu(e, currentLink, 'next', 'first');
                    e.preventDefault();
                    break;
                case 'Space':
                case 'Enter':
                case 'NumpadEnter':
                    currentLink.trigger('click');
                    PrimeFaces.utils.openLink(e, currentLink);
                    break;
                case 'Escape':
                    $this.hide();
                    if ($this.cfg.overlay) {
                        $this.trigger.trigger('focus');
                    }
                    break;
            }
        });
    },

    /**
     * Resets the focus state of the menu.
     * This method sets the first focusable menu item and removes hover and active states for non-overlay menus.
     * @private
     */
    resetFocusState: function() {
        // Set the first focusable menu item
        this.resetFocus(true);

        // plain menu does not have hover and active states
        if (!this.cfg.overlay) {
            this.menuitemLinks.removeClass('ui-state-hover ui-state-active');
        }
    },

    /**
     * Navigates the menu items in the specified direction ('prev' or 'next').
     * @param {JQuery.TriggeredEvent} event - The event that triggered the focus.
     * @param {JQuery} currentLink The currently focused menu item link.
     * @param {string} direction The direction to navigate ('prev' or 'next').
     * @param {string} firstOrLast The first or last item to navigate to ('first' or 'last').
     * @private
     */
    navigateMenu: function(event, currentLink, direction, firstOrLast) {
        var targetItem = currentLink.parent()[direction + 'All']('.ui-menuitem:not(:has(.ui-state-disabled)):' + firstOrLast);
        if (targetItem.length) {
            this.unfocus(currentLink, event);
            this.focus(targetItem.children('.ui-menuitem-link'), event);
        }
    },

    /**
     * Create the key where the state for this component is stored.  By default it is stored per view. Override this 
     * method to change the behavior to be global.
     */
    createStorageKey: function() {
        this.stateKey = PrimeFaces.createStorageKey(this.id, 'PlainMenu', this.cfg.statefulGlobal);
    },

    /**
     * Collapses the given sub menu so that the children of that sub menu are not visible anymore.
     * @param {JQuery} header Menu item with children to collapse.
     * @param {boolean} [stateful] `true` if the new state of this menu (which items are collapsed and expanded) should
     * be saved (in an HTML5 Local Store), `false` otherwise. 
     */
    collapseSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');

        header.attr('aria-expanded', false)
            .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        items.filter('.ui-submenu-child').hide();

        if (stateful) {
            this.collapsedIds.push(header.attr('id'));
            this.saveState();
        }
    },

    /**
     * Expands the given sub menu so that the children of that sub menu become visible.
     * @param {JQuery} header Menu item with children to expand.
     * @param {boolean} [stateful] `true` if the new state of this menu (which items are collapsed and expanded) should
     * be saved (in an HTML5 Local Store), `false` otherwise. 
     */
    expandSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');

        header.attr('aria-expanded', true)
            .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        items.filter('.ui-submenu-child').show();

        if (stateful) {
            var id = header.attr('id');
            this.collapsedIds = $.grep(this.collapsedIds, function(value) {
                return (value !== id);
            });
            this.saveState();
        }
    },

    /**
     * Saves the current state (expanded / collapsed menu items) of this plain menu. Used to preserve the state during
     * AJAX updates as well as between page reloads. The state is stored in an HTML5 Local Store.
     * @private
     */
    saveState: function() {
        localStorage.setItem(this.stateKey, this.collapsedIds.join(','));
    },

    /**
     * Restores that state as stored by `saveState`. Usually called after an AJAX update and on page load.
     * @private
     */
    restoreState: function() {
        var collapsedIdsAsString = localStorage.getItem(this.stateKey);

        if (collapsedIdsAsString) {
            this.collapsedIds = collapsedIdsAsString.split(',');
        } else {
            this.collapsedIds = [];
        }

        // Iterate through headers once: collapse items in collapsedIds, expand others
        var $this = this;
        this.headers.each(function() {
            var header = $(this);
            var id = header.attr('id');
            if (id) {
                if ($this.collapsedIds.indexOf(id) !== -1) {
                    $this.collapseSubmenu(header, false);
                } else {
                    $this.expandSubmenu(header, false);
                }
            }
        });
    },

    /**
     * Clear the saved state (collapsed / expanded menu items) of this plain menu.
     * @private
     */
    clearState: function() {
        localStorage.removeItem(this.stateKey);
    }

});
