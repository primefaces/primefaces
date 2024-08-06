/**
 * __PrimeFaces TabMenu Widget__
 * 
 * TabMenu is a navigation component that displays menuitems as tabs.
 * 
 * @prop {JQuery} items The DOM elements for the tab menu entries.
 * @prop {string} stateKey Name of the HTML5 Store that is used to store the state of this tab menu
 * 
 * @interface {PrimeFaces.widget.TabMenuCfg} cfg The configuration for the {@link  TabMenu| TabMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.MenuCfg} cfg
 */
PrimeFaces.widget.TabMenu = PrimeFaces.widget.Menu.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);

        this.items = this.jq.find('> .ui-tabmenu-nav > li:not(.ui-state-disabled)');
        this.createStorageKey();

        if (!this.cfg.activeIndex) {
            this.restoreState();
        }

        this.bindMouseEvents();
        this.bindKeyEvents();
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindMouseEvents: function () {
        var $this = this;
        this.items.on('mouseover.tabmenu', function () {
            var element = $(this);
            if (!element.hasClass('ui-state-active')) {
                element.addClass('ui-state-hover');
            }
        }).on('mouseout.tabmenu', function () {
            $(this).removeClass('ui-state-hover');
        }).on('click.tabmenu', function () {
            var index = $this.items.index($(this));
            $this.saveState(index);
        });
    },

    /**
     * Sets up all keyboard event listeners that are required by this widget.
     * @private
     */
    bindKeyEvents: function () {
        this.items.on('focus.tabmenu', function (e) {
            $(this).addClass('ui-menuitem-outline');
        }).on('blur.tabmenu', function () {
            $(this).removeClass('ui-menuitem-outline');
        }).on('keydown.tabmenu', function (e) {
            if (PrimeFaces.utils.isActionKey(e)) {
                var $menuitem = $(this);
                var currentLink = $menuitem.children('a');
                currentLink.trigger('click');
                PrimeFaces.utils.openLink(e, currentLink);
            }
        });
    },

    /**
     * Create the key where the state for this component is stored.
     */
    createStorageKey: function () {
        this.stateKey = PrimeFaces.createStorageKey(this.id, 'TabMenu', true);
    },

    /**
     * Saves the current state (expanded / collapsed menu items) of this plain menu. Used to preserve the state during
     * AJAX updates as well as between page reloads. The state is stored in an HTML5 Session Store.
     * @param {number} index The index of the active tab.
     * @private
     */
    saveState: function (index) {
        sessionStorage.setItem(this.stateKey, index);
    },

    /**
     * Restores that state as stored by `saveState`. Usually called after an AJAX update and on page load.
     * @private
     */
    restoreState: function () {
        var restoreIndex = sessionStorage.getItem(this.stateKey) || 0;
        var $restoreMenuItem = this.items.eq(restoreIndex);

        if ($restoreMenuItem && $restoreMenuItem.length) {
            this.items.removeClass('ui-state-active').attr('aria-selected', 'false').attr('aria-expanded', 'false');
            $restoreMenuItem.addClass('ui-state-active').attr('aria-selected', 'true').attr('aria-expanded', 'true');
        }
    }
});
