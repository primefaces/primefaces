import { Menu } from "./menu.base.widget.js";

/**
 * The configuration for the {@link  MegaMenu} widget.
 * 
 * You can access this configuration via {@link PanelMenu.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface MegaMenuCfg extends PrimeType.widget.MenuCfg {
    /**
     * Index of the menu item initially active.
     */
    activeIndex: number;
    /**
     * Defines whether sub menus will be displayed on mouseover or not. When set to false,
     * click event is required to display.
     */
    autoDisplay: boolean;
    /**
     * Delay in milliseconds before displaying the sub menu. Default is 0 meaning immediate.
     */
    delay: number;
    /**
     * `true` if the mega menu is displayed with a vertical layout, `false` if displayed with a
     * horizontal layout.
     */
    vertical: boolean;
}

/**
 * __PrimeFaces MegaMenu Widget__
 *
 * MegaMenu is a horizontal navigation component that displays sub menus together.
 *
 * @typeParam Cfg Type of the configuration object.
 */
export class MegaMenu<Cfg extends MegaMenuCfg = MegaMenuCfg> extends Menu<Cfg> {
    /**
     * Whether the current menu is active and displayed.
     */
    active: boolean = false;

    /**
     * The currently active (highlighted) menu item.
     */
    activeitem?: JQuery | null;

    /**
     * Whether the writing direction is set to right-to-left.
     */
    isRTL: boolean = false;

    /**
     * The last root menu that had focus, if any.
     */
    lastFocusedItem?: JQuery | null = null;

    /**
     * The DOM elements for the root level menu links with the class `.ui-menuitem-link`.
     */
    rootLinks: JQuery = $();

    /**
     * The DOM elements for the root level menu items with the class `.ui-menu-list`.
     */
    rootList: JQuery = $();

    /**
     * The DOM elements for all menu links not a the root level, with the class `.ui-menuitem-link`.
     */
    subLinks: JQuery = $();

    /**
     * Timeout ID, used for the animation when the menu is shown.
     */
    timeoutId?: number = undefined;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

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
    }


    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
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
    }

    /**
     * Sets up all keyboard-related event listeners.
     */
    private bindKeyEvents(): void {
        const $this = this;

        // make first focusable
        const firstLink = this.rootLinks.filter(':not([disabled])').first();
        firstLink.attr("tabindex", "0");
        this.resetFocus(true);
        firstLink.removeClass('ui-state-hover ui-state-active');

        this.jq.on("blur.menu focusout.menu", (e) => {
            const fe = e as JQuery.FocusEventBase;
            if (fe.relatedTarget instanceof Element && this.jq.has(fe.relatedTarget).length === 0) {
                this.reset();
            }
        });

        this.rootLinks.on("mouseenter.menu click.menu", function() {
            const $link = $(this);
            const $menuitem = $link.parent();
            $this.deactivate($menuitem);
            $link.trigger('focus');
        }).on("focusin.menu", function() {
            const $link = $(this);
            $this.highlight($link.parent());
        }).on('keydown.megamenu', (e) => {
            const currentItem = this.activeitem;
            if (!currentItem) {
                return;
            }

            const isRootLink = this.isRootLink(currentItem);
            const submenu = currentItem.children('.ui-menu-child');
            const parentItem = currentItem.closest('ul.ui-menu-child').parent();
            let prevItem = null;
            let nextItem = null;

            switch ("code" in e ? e.code : e.key) {
                case 'ArrowLeft':
                    if (isRootLink && !this.cfg.vertical) {
                        prevItem = this.findPrevItem(currentItem);
                        if (prevItem.length) {
                            this.deactivate(currentItem);
                            this.highlight(prevItem);
                        }

                        e.preventDefault();
                    }
                    else if (currentItem.hasClass('ui-menu-parent') && currentItem.children('.ui-menu-child').is(':visible')) {
                        this.deactivate(currentItem);
                        this.highlight(currentItem);
                    }
                    else if (parentItem.length) {
                        this.deactivate(currentItem);
                        this.deactivate(parentItem);
                        this.highlight(parentItem);
                    }
                    break;

                case 'ArrowRight':
                    if (isRootLink && !this.cfg.vertical) {
                        nextItem = currentItem.nextAll('.ui-menuitem:visible:first');
                        if (nextItem.length) {
                            this.deactivate(currentItem);
                            this.highlight(nextItem);
                        }

                        e.preventDefault();
                    }
                    else if (currentItem.hasClass('ui-menu-parent')) {
                        if (submenu.is(':visible')) {
                            this.highlight(submenu.find('ul.ui-menu-list:visible > .ui-menuitem:visible:first'));
                        }
                        else {
                            this.activate(currentItem);
                        }
                    }
                    break;

                case 'ArrowUp':
                    if (!isRootLink || this.cfg.vertical) {
                        prevItem = this.findPrevItem(currentItem);
                        if (prevItem.length) {
                            this.deactivate(currentItem);
                            this.highlight(prevItem);
                        }
                    }

                    e.preventDefault();
                    break;

                case 'ArrowDown':
                    if (isRootLink && !this.cfg.vertical) {
                        if (submenu.is(':visible')) {
                            var firstMenulist = this.getFirstMenuList(submenu);
                            this.highlight(firstMenulist.children('.ui-menuitem:visible:first'));
                        }
                        else {
                            this.activate(currentItem);
                        }
                    }
                    else {
                        nextItem = this.findNextItem(currentItem);
                        if (nextItem.length) {
                            this.deactivate(currentItem);
                            this.highlight(nextItem);
                        }
                    }

                    e.preventDefault();
                    break;

                case 'Enter':
                case 'Space':
                case 'NumpadEnter':
                    var currentLink = currentItem.children('.ui-menuitem-link');
                    currentLink.trigger('click');
                    PrimeFaces.utils.openLink(e, currentLink);
                    this.deactivate(currentItem);
                    e.preventDefault();
                    break;

                case 'Escape':
                    if (currentItem.hasClass('ui-menu-parent')) {
                        var submenuPopup = currentItem.children('ul.ui-menu-list:visible');
                        if (submenuPopup.length > 0) {
                            submenuPopup.hide();
                        }
                    }
                    else if (parentItem.length) {
                        this.deactivate(currentItem);
                        this.deactivate(parentItem);
                        this.highlight(parentItem);
                    }
                    e.preventDefault();
                    break;
            }
        });
    }

    /**
     * Finds the menu items that precedes the given item.
     * @param menuitem One of the menu items of this mega menu, with the class `.ui-menuitem`.
     * @return The menu item before the given item. Empty JQuery instance if the given item is the first.
     */
    findPrevItem(menuitem: JQuery): JQuery {
        let prevItem = menuitem.prev('.ui-menuitem');

        if(!prevItem.length) {
            let prevSubmenu = menuitem.closest('ul.ui-menu-list').prev('.ui-menu-list');

            if(!prevSubmenu.length) {
                prevSubmenu = menuitem.closest('td').prev('td').children('.ui-menu-list:visible:last');
            }

            if(prevSubmenu.length) {
                prevItem = prevSubmenu.find('li.ui-menuitem:visible:last');
            }
        }
        return prevItem;
    }

    /**
     * Finds the menu items that succeeds the given item.
     * @param menuItem One of the menu items of this mega menu, with the class `.ui-menuitem`.
     * @return The menu item after the given item. Empty JQuery instance if the given item is the last.
     */
    findNextItem(menuItem: JQuery): JQuery {
        let nextItem = menuItem.next('.ui-menuitem');

        if(!nextItem.length) {
            let nextSubmenu = menuItem.closest('ul.ui-menu-list').next('.ui-menu-list');
            if(!nextSubmenu.length) {
                nextSubmenu = menuItem.closest('td').next('td').children('.ui-menu-list:visible:first');
            }

            if(nextSubmenu.length) {
                nextItem = nextSubmenu.find('li.ui-menuitem:visible:first');
            }
        }
        return nextItem;
    }

    /**
     * Finds the the menu group of the given submenu, i.e. the children of the given item.
     * @param submenu A submenu with children.
     * @return The first sub menu list, an item with the class `.ui-menu-list`.
     */
    getFirstMenuList(submenu: JQuery): JQuery {
        return submenu.find('.ui-menu-list:not(.ui-state-disabled):first');
    }

    /**
     * Checks whether the given menu item is the root menu item element.
     * @param menuItem One of the menu items of this mega menu.
     * @return `true` if the given menu item is the root, or `false` otherwise.
     */
    isRootLink(menuItem: JQuery): boolean {
        const submenu = menuItem.closest('ul');
        return submenu.parent().hasClass('ui-menu');
    }

    /**
     * Resets the entire mega menu, i.e. closes all opened sub menus.
     */
    reset(): void {
        const $this = this;
        this.active = false;

        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });
        this.resetFocus(!this.lastFocusedItem);
        if (this.lastFocusedItem) {
            this.lastFocusedItem.children('a.ui-menuitem-link').attr('tabindex', this.tabIndex);
        }
        this.rootLinks.removeClass('ui-state-hover');
    }

    /**
     * Deactivates the menu item, i.e. closes the sub menu.
     * @param menuitem A menu item to close.
     * @param animate If `true`, closes the sub menu with an animation, or `false` otherwise.
     */
    deactivate(menuitem: JQuery, animate?: boolean): void {
        const $this = this;
        this.activeitem = null;
        menuitem.removeClass('ui-menuitem-active ui-menuitem-highlight');
        const $link = menuitem.children('a.ui-menuitem-link');
        this.unfocus($link);

        var activeSibling = menuitem.siblings('.ui-menuitem-active');
        if (activeSibling.length) {
            activeSibling.find('li.ui-menuitem-active').each(function() {
                $this.deactivate($(this));
            });
            $this.deactivate(activeSibling);
        }

        const submenu = menuitem.children('ul.ui-menu-child');
        if (submenu.length > 0) {
            $link.attr('aria-expanded', 'false');
            if (animate) {
                submenu.fadeOut('fast');
            }
            else {
                submenu.hide();
            }
        }
    }

    /**
     * Highlight the given menu entry, as if the user were to hover it.
     * @param menuItem A menu entry to highlight.
     */
    highlight(menuItem: JQuery): void {
        this.activeitem = menuItem;
        menuItem.addClass('ui-menuitem-active ui-menuitem-highlight');
        menuItem.children('a.ui-menuitem-link').addClass('ui-state-hover');
    }

    /**
     * Activates a menu item so that it can be clicked and interacted with.
     * 
     * @param menuitem - The menu item to activate.
     * @param showSubMenu - If false, only focuses the menu item without showing the submenu.
     */
    activate(menuitem: JQuery, showSubMenu: boolean = true): void {
        this.highlight(menuitem);
        
        // if this is a root menu item.
        if (menuitem.parent().is('ul.ui-menu-list:not(.ui-menu-child)')) {
            this.lastFocusedItem = menuitem;
        }


        // focus the menu item when activated
        this.focus(menuitem.children('a.ui-menuitem-link'));

        if (showSubMenu) {
            var submenu = menuitem.children('ul.ui-menu-child');
            if (submenu.length == 1) {
                this.showSubmenu(menuitem, submenu);
            }
        }
    }

    /**
     * Opens and shows the sub menu of the given menu item.
     * @param menuItem A menu item with a submenu.
     * @param submenu One of the submenus of the given menu item to show.
     */
    private showSubmenu(menuItem: JQuery, submenu: JQuery): void {
        let pos: JQueryUI.JQueryPositionOptions | undefined;

        if (this.cfg.vertical) {
            pos = {
                my: this.isRTL ? 'right bottom' : 'left top',
                at: this.isRTL ? 'left bottom' : 'right top',
                of: menuItem,
                collision: 'flipfit'
            };
        }
        else {
            pos = {
                my: this.isRTL ? 'right top' : 'left top',
                at: this.isRTL ? 'right bottom' : 'left bottom',
                of: menuItem,
                collision: 'flipfit'
            };
        }

        //avoid queuing multiple runs
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }

        this.timeoutId = PrimeFaces.queueTask(() => {
           submenu.css('z-index', PrimeFaces.nextZindex())
                  .show()
                  .position(pos)
        }, this.cfg.delay);
    }
}
