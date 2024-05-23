/**
 * __PrimeFaces MegaMenu Widget__
 *
 * MegaMenu is a horizontal navigation component that displays sub menus together.
 *
 * @prop {boolean} active Whether the current menu is active and displayed.
 * @prop {JQuery | null} [activeitem] The currently active (highlighted) menu item.
 * @prop {JQuery} rootLinks The DOM elements for the root level menu links with the class `.ui-menuitem-link`.
 * @prop {JQuery} rootList The DOM elements for the root level menu items with the class `.ui-menu-list`.
 * @prop {JQuery} subLinks The DOM elements for all menu links not a the root level, with the class `.ui-menuitem-link`.
 * @prop {number} [timeoutId] Timeout ID, used for the animation when the menu is shown.
 * @prop {boolean} isRTL Whether the writing direction is set to right-to-left.
 *
 * @interface {PrimeFaces.widget.MegaMenuCfg} cfg The configuration for the {@link  MegaMenu| MegaMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {number} cfg.activeIndex Index of the menu item initially active.
 * @prop {boolean} cfg.autoDisplay Defines whether sub menus will be displayed on mouseover or not. When set to false,
 * click event is required to display.
 * @prop {number} cfg.delay Delay in milliseconds before displaying the sub menu. Default is 0 meaning immediate.
 * @prop {boolean} cfg.vertical `true` if the mega menu is displayed with a vertical layout, `false` if displayed with a
 * horizontal layout.
 */
PrimeFaces.widget.MegaMenu = PrimeFaces.widget.Menu.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.cfg.vertical = this.jq.hasClass('ui-megamenu-vertical');
        this.rootList = this.jq.children('ul.ui-menu-list');
        this.rootLinks = this.rootList.find('> li.ui-menuitem > a.ui-menuitem-link:not(.ui-state-disabled)');
        this.subLinks = this.jq.find('.ui-menu-child a.ui-menuitem-link:not(.ui-state-disabled)');
        this.isRTL = this.jq.hasClass('ui-menu-rtl');

        if(this.cfg.activeIndex !== undefined) {
            this.rootLinks.eq(this.cfg.activeIndex).addClass('ui-state-hover').closest('li.ui-menuitem').addClass('ui-menuitem-active');
        }

        this.bindEvents();
        this.bindKeyEvents();
    },


    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.rootLinks.on("mouseenter", function() {
            var link = $(this),
            menuitem = link.parent();

            var current = menuitem.siblings('.ui-menuitem-active');
            if(current.length > 0) {
                current.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(current, false);
            }

            if($this.cfg.autoDisplay||$this.active) {
                $this.activate(menuitem);
            }
            else {
                $this.highlight(menuitem);
            }

        });

        if(this.cfg.autoDisplay === false) {
            this.rootLinks.data('primefaces-megamenu', this.id).find('*').data('primefaces-megamenu', this.id);

            this.rootLinks.on("click", function(e) {
                var link = $(this),
                menuitem = link.parent(),
                submenu = link.next();

                if(submenu.length === 1) {
                    if(submenu.is(':visible')) {
                        $this.active = false;
                        $this.deactivate(menuitem, true);
                    }
                    else {
                        $this.active = true;
                        $this.activate(menuitem);
                    }
                }
                else {
                    PrimeFaces.utils.openLink(e, link);
                }

                e.preventDefault();
            });
        }
        else {
            this.rootLinks.filter('.ui-submenu-link').on("click", function(e) {
                e.preventDefault();
            });
        }

        this.subLinks.on("mouseenter", function() {
            if($this.activeitem && !$this.isRootLink($this.activeitem)) {
                $this.deactivate($this.activeitem);
            }
            $this.highlight($(this).parent());
        })
        .on("mouseleave", function() {
            if($this.activeitem && !$this.isRootLink($this.activeitem)) {
                $this.deactivate($this.activeitem);
            }
            $(this).removeClass('ui-state-hover');
        });

        this.rootList.on("mouseleave", function() {
            var activeitem = $this.rootList.children('.ui-menuitem-active');
            if(activeitem.length === 1) {
                $this.deactivate(activeitem, false);
            }
        });

        this.rootList.find('> li.ui-menuitem > ul.ui-menu-child').on("mouseleave", function(e) {
            e.stopPropagation();
        });
 
        var clickNS = 'click.' + this.id;
        $(document.body).off(clickNS).on(clickNS, function(e) {
            var target = $(e.target);
            if(target.data('primefaces-megamenu') === $this.id) {
                return;
            }

            $this.active = false;
            $this.deactivate($this.rootList.children('li.ui-menuitem-active'), true);
        });
        this.addDestroyListener(function() {
            $(document.body).off(clickNS);
        });
    },

    /**
     * Sets up all keyboard-related event listeners.
     * @private
     */
    bindKeyEvents: function() {
        var $this = this;

        // make first focusable
        var firstLink = this.rootLinks.filter(':not([disabled])').first();
        firstLink.attr("tabindex", "0");
        this.resetFocus(true);
        firstLink.removeClass('ui-state-hover ui-state-active');

        this.jq.on("blur.menu focusout.menu", function(e) {
            if (!$this.jq.has(e.relatedTarget).length) {
                $this.reset();
            }
        });

        this.rootLinks.on("mouseenter.menu click.menu", function() {
            var $link = $(this),
                $menuitem = $link.parent();
            $this.deactivate($menuitem);
            $link.trigger('focus');
        }).on("focusin.menu", function() {
            var $link = $(this);
            $this.highlight($link.parent());
        }).on('keydown.megamenu', function(e) {
            var currentitem = $this.activeitem;
            if (!currentitem) {
                return;
            }

            var isRootLink = $this.isRootLink(currentitem);
            var submenu = currentitem.children('.ui-menu-child');
            var parentItem = currentitem.closest('ul.ui-menu-child').parent();
            var prevItem = null;
            var nextItem = null;

            switch (e.code) {
                case 'ArrowLeft':
                    if (isRootLink && !$this.cfg.vertical) {
                        prevItem = $this.findPrevItem(currentitem);
                        if (prevItem.length) {
                            $this.deactivate(currentitem);
                            $this.highlight(prevItem);
                        }

                        e.preventDefault();
                    }
                    else {
                        if (currentitem.hasClass('ui-menu-parent') && currentitem.children('.ui-menu-child').is(':visible')) {
                            $this.deactivate(currentitem);
                            $this.highlight(currentitem);
                        }
                        else if (parentItem.length) {
                            $this.deactivate(currentitem);
                            $this.deactivate(parentItem);
                            $this.highlight(parentItem);
                        }
                    }
                    break;

                case 'ArrowRight':
                    if (isRootLink && !$this.cfg.vertical) {
                        nextItem = currentitem.nextAll('.ui-menuitem:visible:first');
                        if (nextItem.length) {
                            $this.deactivate(currentitem);
                            $this.highlight(nextItem);
                        }

                        e.preventDefault();
                    }
                    else if (currentitem.hasClass('ui-menu-parent')) {
                        if (submenu.is(':visible')) {
                            $this.highlight(submenu.find('ul.ui-menu-list:visible > .ui-menuitem:visible:first'));
                        }
                        else {
                            $this.activate(currentitem);
                        }
                    }
                    break;

                case 'ArrowUp':
                    if (!isRootLink || $this.cfg.vertical) {
                        prevItem = $this.findPrevItem(currentitem);
                        if (prevItem.length) {
                            $this.deactivate(currentitem);
                            $this.highlight(prevItem);
                        }
                    }

                    e.preventDefault();
                    break;

                case 'ArrowDown':
                    if (isRootLink && !$this.cfg.vertical) {
                        if (submenu.is(':visible')) {
                            var firstMenulist = $this.getFirstMenuList(submenu);
                            $this.highlight(firstMenulist.children('.ui-menuitem:visible:first'));
                        }
                        else {
                            $this.activate(currentitem);
                        }
                    }
                    else {
                        nextItem = $this.findNextItem(currentitem);
                        if (nextItem.length) {
                            $this.deactivate(currentitem);
                            $this.highlight(nextItem);
                        }
                    }

                    e.preventDefault();
                    break;

                case 'Enter':
                case 'Space':
                case 'NumpadEnter':
                    var currentLink = currentitem.children('.ui-menuitem-link');
                    currentLink.trigger('click');
                    PrimeFaces.utils.openLink(e, currentLink);
                    $this.deactivate(currentitem);
                    e.preventDefault();
                    break;

                case 'Escape':
                    if (currentitem.hasClass('ui-menu-parent')) {
                        var submenuPopup = currentitem.children('ul.ui-menu-list:visible');
                        if (submenuPopup.length > 0) {
                            submenuPopup.hide();
                        }
                    }
                    else if (parentItem.length) {
                        $this.deactivate(currentitem);
                        $this.deactivate(parentItem);
                        $this.highlight(parentItem);
                    }
                    e.preventDefault();
                    break;
            }
        });
    },

    /**
     * Finds the menu items that preceeded the given item.
     * @param {JQuery} menuitem One of the menu items of this mega menu, with the class `.ui-menuitem`.
     * @return {JQuery} The menu item before the given item. Empty JQuery instance if the given item is the first.
     */
    findPrevItem: function(menuitem) {
        var previtem = menuitem.prev('.ui-menuitem');

        if(!previtem.length) {
            var prevSubmenu = menuitem.closest('ul.ui-menu-list').prev('.ui-menu-list');

            if(!prevSubmenu.length) {
                prevSubmenu = menuitem.closest('td').prev('td').children('.ui-menu-list:visible:last');
            }

            if(prevSubmenu.length) {
                previtem = prevSubmenu.find('li.ui-menuitem:visible:last');
            }
        }
        return previtem;
    },

    /**
     * Finds the menu items that succeeds the given item.
     * @param {JQuery} menuitem One of the menu items of this mega menu, with the class `.ui-menuitem`.
     * @return {JQuery} The menu item after the given item. Empty JQuery instance if the given item is the last.
     */
    findNextItem: function(menuitem) {
        var nextitem = menuitem.next('.ui-menuitem');

        if(!nextitem.length) {
            var nextSubmenu = menuitem.closest('ul.ui-menu-list').next('.ui-menu-list');
            if(!nextSubmenu.length) {
                nextSubmenu = menuitem.closest('td').next('td').children('.ui-menu-list:visible:first');
            }

            if(nextSubmenu.length) {
                nextitem = nextSubmenu.find('li.ui-menuitem:visible:first');
            }
        }
        return nextitem;
    },

    /**
     * Finds the the menu group of the given submenu, i.e. the children of the given item.
     * @param {JQuery} submenu A submenu with children.
     * @return {JQuery} The first sub menu list, an item with the class `.ui-menu-list`.
     */
    getFirstMenuList: function(submenu) {
        return submenu.find('.ui-menu-list:not(.ui-state-disabled):first');
    },

    /**
     * Checks whether the given menu item is the root menu item element.
     * @param {JQuery} menuitem One of the menu items of this mega menu.
     * @return {boolean} `true` if the given menu item is the root, or `false` otherwise.
     */
    isRootLink: function(menuitem) {
        var submenu = menuitem.closest('ul');
        return submenu.parent().hasClass('ui-menu');
    },

    /**
     * Resets the entire mega menu, i.e. closes all opened sub menus.
     */
    reset: function() {
        var $this = this;
        this.active = false;

        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });
        this.resetFocus(true);
        this.rootLinks.removeClass('ui-state-hover');
    },

    /**
     * Deactivates the menu item, i.e. closes the sub menu.
     * @param {JQuery} menuitem A menu item to close.
     * @param {boolean} [animate] If `true`, closes the sub menu with an animation, or `false` otherwise.
     */
    deactivate: function(menuitem, animate) {
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
    },

    /**
     * Highlight the given menu entry, as if the user were to hover it.
     * @param {JQuery} menuitem A menu entry to highlight.
     */
    highlight: function(menuitem) {
        this.activeitem = menuitem;
        menuitem.addClass('ui-menuitem-active ui-menuitem-highlight');
        menuitem.children('a.ui-menuitem-link').addClass('ui-state-hover');
    },

    /**
     * Activates a menu item so that it can be clicked and interacted with.
     * 
     * @param {JQuery} menuitem - The menu item to activate.
     * @param {boolean} [showSubMenu=true] - If false, only focuses the menu item without showing the submenu.
     */
    activate: function(menuitem, showSubMenu = true) {
        this.highlight(menuitem);

        // focus the menu item when activated
        this.focus(menuitem.children('a.ui-menuitem-link'));

        if (showSubMenu) {
            var submenu = menuitem.children('ul.ui-menu-child');
            if (submenu.length == 1) {
                this.showSubmenu(menuitem, submenu);
            }
        }
    },

    /**
     * Opens and shows the sub menu of the given menu item.
     * @param {JQuery} menuitem A menu item with a submenu.
     * @param {JQuery} submenu One of the submenus of the given menu item to show.
     * @private
     */
    showSubmenu: function(menuitem, submenu) {
        var pos = null;

        if(this.cfg.vertical) {
            pos = {
                my: this.isRTL ? 'right bottom' : 'left top',
                at: this.isRTL ? 'left bottom' : 'right top',
                of: menuitem,
                collision: 'flipfit'
            };
        }
        else {
            pos = {
                my: this.isRTL ? 'right top' : 'left top',
                at: this.isRTL ? 'right bottom' : 'left bottom',
                of: menuitem,
                collision: 'flipfit'
            };
        }

        //avoid queuing multiple runs
        if(this.timeoutId) {
            clearTimeout(this.timeoutId);
        }

        this.timeoutId = PrimeFaces.queueTask(function () {
           submenu.css('z-index', PrimeFaces.nextZindex())
                  .show()
                  .position(pos)
        }, this.cfg.delay);
    }

});
