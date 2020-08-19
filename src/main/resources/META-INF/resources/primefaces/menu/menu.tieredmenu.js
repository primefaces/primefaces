/**
 * __PrimeFaces TieredMenu Widget__
 * 
 * TieredMenu is used to display nested submenus with overlays.
 * 
 * @typedef {"hover" | "click"} PrimeFaces.widget.TieredMenu.ToggleEvent Allowed event types for toggling a tiered menu.
 * 
 * @prop {JQuery} links DOM element with all links for the menu entries of this tiered menu.
 * @prop {JQuery} rootLinks DOM element with all links for the root (top-level) menu entries of this tiered menu.
 * 
 * @interface {PrimeFaces.widget.TieredMenuCfg} cfg The configuration for the {@link  TieredMenu| TieredMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.MenuCfg} cfg 
 * 
 * @prop {boolean} cfg.autoDisplay Defines whether the first level of submenus will be displayed on mouseover or not.
 * When set to `false`, click event is required to display this tiered menu.
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

        this.cfg.toggleEvent = this.cfg.toggleEvent||'hover';
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        this.rootLinks = this.jq.find('> ul.ui-menu-list > .ui-menuitem > .ui-menuitem-link');

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
        if(this.cfg.toggleEvent === 'hover')
            this.bindHoverModeEvents();
        else if(this.cfg.toggleEvent === 'click')
            this.bindClickModeEvents();
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
            if(activeSibling.length === 1) {
                activeSibling.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(activeSibling);
            }

            if($this.cfg.autoDisplay||$this.active) {
                if(menuitem.hasClass('ui-menuitem-active'))
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

            if(submenu.length === 1) {
                if(submenu.is(':visible')) {
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
           if($this.activeitem) {
               $this.deactivate($this.activeitem);
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

            if(!menuitem.hasClass('ui-menuitem-active')) {
                menuitem.addClass('ui-menuitem-highlight').children('a.ui-menuitem-link').addClass('ui-state-hover');
            }
        })
        .on("mouseleave", function() {
            var menuitem = $(this).parent();

            if(!menuitem.hasClass('ui-menuitem-active')) {
                menuitem.removeClass('ui-menuitem-highlight').children('a.ui-menuitem-link').removeClass('ui-state-hover');
            }
        });

        this.links.filter('.ui-submenu-link').on('click.tieredMenu', function(e) {
            var link = $(this),
            menuitem = link.parent(),
            submenu = menuitem.children('ul.ui-menu-child');

            $this.itemClick = true;

            var activeSibling = menuitem.siblings('.ui-menuitem-active');
            if(activeSibling.length) {
                activeSibling.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(activeSibling);
            }

            if(submenu.length) {
                if(submenu.is(':visible')) {
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
        //not implemented
    },

    /**
     * Registers a delegated event listener for a mouse click on a menu entry.
     * @protected
     */
    bindDocumentHandler: function() {
        var $this = this,
        clickNS = 'click.' + this.id;

        $(document.body).off(clickNS).on(clickNS, function(e) {
            if($this.itemClick) {
                $this.itemClick = false;
                return;
            }

            $this.reset();
        });
    },

    /**
     * Deactivates a menu item so that it cannot be clicked and interacted with anymore.
     * @param {JQuery} menuitem Menu item (`LI`) to deactivate.
     * @param {boolean} [animate] `true` to animate the transition to the disabled state, `false` otherwise.
     */
    deactivate: function(menuitem, animate) {
        this.activeitem = null;
        menuitem.children('a.ui-menuitem-link').removeClass('ui-state-hover ui-state-active');
        menuitem.removeClass('ui-menuitem-active ui-menuitem-highlight');

        if(animate)
            menuitem.children('ul.ui-menu-child').fadeOut('fast');
        else
            menuitem.children('ul.ui-menu-child').hide();
    },

    /**
     * Activates a menu item so that it can be clicked and interacted with.
     * @param {JQuery} menuitem Menu item (`LI`) to activate.
     */
    activate: function(menuitem) {
        this.highlight(menuitem);

        var submenu = menuitem.children('ul.ui-menu-child');
        if(submenu.length == 1) {
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

        if(activeChilditem.length == 1) {
            _self.deactivate(activeChilditem);
        }
    },

    /**
     * Highlights the given menu item by applying the proper CSS classes.
     * @param {JQuery} menuitem Menu item to highlight.
     */
    highlight: function(menuitem) {
        this.activeitem = menuitem;
        menuitem.children('a.ui-menuitem-link').addClass('ui-state-hover');
        menuitem.addClass('ui-menuitem-active');
    },

    /**
     * Shows the given submenu of a menu item.
     * @param {JQuery} menuitem A menu item (`LI`) with children.
     * @param {JQuery} submenu A child of the menu item.
     */
    showSubmenu: function(menuitem, submenu) {
        var pos ={
            my: 'left top',
            at: 'right top',
            of: menuitem,
            collision: 'flipfit'
        };

        submenu.css('z-index', PrimeFaces.nextZindex())
            .show()
            .position(pos);
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
    }
    
});
