/**
 * __PrimeFaces MegaMenu Widget__
 *
 * MegaMenu is a horizontal navigation component that displays submenus together.
 *
 * @prop {boolean} active Whether the current menu is active and displayed.
 * @prop {JQuery} activeitem The currently active (highlighted) menu item.
 * @prop {JQuery} keyboardTarget The DOM element for the input element accessible via keyboard keys.
 * @prop {JQuery} rootLinks The DOM elements for the root level menu links with the class `.ui-menuitem-link`.
 * @prop {JQuery} rootList The DOM elements for the root level menu items with the class `.ui-menu-list`.
 * @prop {JQuery} subLinks The DOM elements for all menu links not a the root level, with the class `.ui-menuitem-link`.
 *
 * @interface {PrimeFaces.widget.MegaMenuCfg} cfg The configuration for the {@link  MegaMenu| MegaMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {number} cfg.activeIndex Index of the menu item initially active.
 * @prop {boolean} cfg.autoDisplay Defines whether submenus will be displayed on mouseover or not. When set to false,
 * click event is required to display.
 * @prop {number} cfg.delay Delay in milliseconds before displaying the submenu. Default is 0 meaning immediate.
 * @prop {boolean} cfg.vertical `true` if the mega menu is displayed with a vertical layout, `false` if displayed with a
 * horizontal layout.
 */
PrimeFaces.widget.MegaMenu = PrimeFaces.widget.BaseWidget.extend({

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
        this.keyboardTarget = this.jq.children('.ui-helper-hidden-accessible');

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

        this.rootLinks.on("mouseenter", function(e) {
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

        this.rootList.on("mouseleave", function(e) {
            var activeitem = $this.rootList.children('.ui-menuitem-active');
            if(activeitem.length === 1) {
                $this.deactivate(activeitem, false);
            }
        });

        this.rootList.find('> li.ui-menuitem > ul.ui-menu-child').on("mouseleave", function(e) {
            e.stopPropagation();
        });

        $(document.body).on("click", function(e) {
            var target = $(e.target);
            if(target.data('primefaces-megamenu') === $this.id) {
                return;
            }

            $this.active = false;
            $this.deactivate($this.rootList.children('li.ui-menuitem-active'), true);
        });
    },

    /**
     * Sets up all keyboard-related event listeners.
     * @private
     */
    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('focus.megamenu', function(e) {
            $this.highlight($this.rootLinks.eq(0).parent());
        })
        .on('blur.megamenu', function() {
            $this.reset();
        })
        .on('keydown.megamenu', function(e) {
            var currentitem = $this.activeitem;
            if(!currentitem) {
                return;
            }

            var isRootLink = $this.isRootLink(currentitem),
            keyCode = $.ui.keyCode;

            switch(e.which) {
                    case keyCode.LEFT:
                        if(isRootLink && !$this.cfg.vertical) {
                            var prevItem = currentitem.prevAll('.ui-menuitem:first');
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }

                            e.preventDefault();
                        }
                        else {
                            if(currentitem.hasClass('ui-menu-parent') && currentitem.children('.ui-menu-child').is(':visible')) {
                                $this.deactivate(currentitem);
                                $this.highlight(currentitem);
                            }
                            else {
                                var parentItem = currentitem.closest('ul.ui-menu-child').parent();
                                if(parentItem.length) {
                                    $this.deactivate(currentitem);
                                    $this.deactivate(parentItem);
                                    $this.highlight(parentItem);
                                }
                            }
                        }
                    break;

                    case keyCode.RIGHT:
                        if(isRootLink && !$this.cfg.vertical) {
                            var nextItem = currentitem.nextAll('.ui-menuitem:visible:first');
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }

                            e.preventDefault();
                        }
                        else {

                            if(currentitem.hasClass('ui-menu-parent')) {
                                var submenu = currentitem.children('.ui-menu-child');
                                if(submenu.is(':visible')) {
                                    $this.highlight(submenu.find('ul.ui-menu-list:visible > .ui-menuitem:visible:first'));
                                }
                                else {
                                    $this.activate(currentitem);
                                }
                            }
                        }
                    break;

                    case keyCode.UP:
                        if(!isRootLink || $this.cfg.vertical) {
                            var prevItem = $this.findPrevItem(currentitem);
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }
                        }

                        e.preventDefault();
                    break;

                    case keyCode.DOWN:
                        if(isRootLink && !$this.cfg.vertical) {
                            var submenu = currentitem.children('ul.ui-menu-child');
                            if(submenu.is(':visible')) {
                                var firstMenulist = $this.getFirstMenuList(submenu);
                                $this.highlight(firstMenulist.children('.ui-menuitem:visible:first'));
                            }
                            else {
                                $this.activate(currentitem);
                            }
                        }
                        else {
                            var nextItem = $this.findNextItem(currentitem);
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }
                        }

                        e.preventDefault();
                    break;

                    case keyCode.ENTER:
                        var currentLink = currentitem.children('.ui-menuitem-link');
                        currentLink.trigger('click');
                        $this.jq.trigger("blur");
                        var href = currentLink.attr('href');
                        if(href && href !== '#') {
                            window.location.href = href;
                        }
                        $this.deactivate(currentitem);
                        e.preventDefault();
                    break;

                    case keyCode.ESCAPE:
                        if(currentitem.hasClass('ui-menu-parent')) {
                            var submenu = currentitem.children('ul.ui-menu-list:visible');
                            if(submenu.length > 0) {
                                submenu.hide();
                            }
                        }
                        else {
                            var parentItem = currentitem.closest('ul.ui-menu-child').parent();
                            if(parentItem.length) {
                                $this.deactivate(currentitem);
                                $this.deactivate(parentItem);
                                $this.highlight(parentItem);
                            }
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
    },

    /**
     * Deactivates the menu item, i.e. closes the sub menu.
     * @param {JQuery} menuitem A menu item to close.
     * @param {boolean} [animate] If `true`, closes the sub menu with an animation, or `false` otherwise.
     */
    deactivate: function(menuitem, animate) {
        var link = menuitem.children('a.ui-menuitem-link'),
        submenu = link.next();

        menuitem.removeClass('ui-menuitem-active');
        link.removeClass('ui-state-hover');
        this.activeitem = null;

        if(submenu.length > 0) {
            if(animate)
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
        var link = menuitem.children('a.ui-menuitem-link');

        menuitem.addClass('ui-menuitem-active');
        link.addClass('ui-state-hover');
        this.activeitem = menuitem;
    },

    /**
     * Activates the menu item, i.e. opens the sub menu.
     * @param {JQuery} menuitem A menu item to open.
     */
    activate: function(menuitem) {
        var submenu = menuitem.children('.ui-menu-child'),
        $this = this;

        $this.highlight(menuitem);

        if(submenu.length > 0) {
            $this.showSubmenu(menuitem, submenu);
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
                my: 'left top',
                at: 'right top',
                of: menuitem,
                collision: 'flipfit'
            };
        }
        else {
            pos = {
                my: 'left top',
                at: 'left bottom',
                of: menuitem,
                collision: 'flipfit'
            };
        }

        //avoid queuing multiple runs
        if(this.timeoutId) {
            clearTimeout(this.timeoutId);
        }

        this.timeoutId = setTimeout(function () {
           submenu.css('z-index', PrimeFaces.nextZindex())
                  .show()
                  .position(pos)
        }, this.cfg.delay);
    }

});
