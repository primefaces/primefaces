
/**
 * __PrimeFaces PlainMenu Widget__
 * 
 * Menu is a navigation component with sub menus and menu items.
 * 
 * @prop {JQuery} menuitemLinks DOM elements with the links of each menu item.
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

        //events
        this.bindEvents();

        if(this.cfg.toggleable) {
            this.collapsedIds = [];
            this.stateKey = PrimeFaces.createStorageKey(this.id, 'PlainMenu');
            this.restoreState();
        }
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.menuitemLinks.on("mouseenter", function(e) {
            if($this.jq.is(':focus')) {
                $this.jq.trigger("blur");
            }

            $(this).addClass('ui-state-hover');
        })
        .on("mouseleave", function(e) {
            $(this).removeClass('ui-state-hover');
        });

        if(this.cfg.overlay) {
            this.menuitemLinks.on("click", function() {
                $this.hide();
            });

            this.trigger.on('keydown.ui-menu', function(e) {
                var keyCode = $.ui.keyCode;

                switch(e.which) {
                    case keyCode.DOWN:
                        $this.keyboardTarget.trigger('focus.menu');
                        e.preventDefault();
                    break;

                    case keyCode.TAB:
                        if($this.jq.is(':visible')) {
                            $this.hide();
                        }
                    break;
                }
            });
        }

        if(this.cfg.toggleable) {
            this.jq.find('> .ui-menu-list > .ui-widget-header').on('mouseover.menu', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.menu', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.menu', function(e) {
                var header = $(this);

                if(header.find('> h3 > .ui-icon').hasClass('ui-icon-triangle-1-s'))
                    $this.collapseSubmenu(header, true);
                else
                    $this.expandSubmenu(header, true);

                PrimeFaces.clearSelection();
                e.preventDefault();
            });
        }

        this.keyboardTarget.on('focus.menu', function() {
            $this.menuitemLinks.eq(0).addClass('ui-state-hover');
        })
        .on('blur.menu', function() {
            $this.menuitemLinks.filter('.ui-state-hover').removeClass('ui-state-hover');
        })
        .on('keydown.menu', function(e) {
            var currentLink = $this.menuitemLinks.filter('.ui-state-hover'),
            keyCode = $.ui.keyCode;

            switch(e.which) {
                    case keyCode.UP:
                        var prevItem = currentLink.parent().prevAll('.ui-menuitem:first');
                        if(prevItem.length) {
                            currentLink.removeClass('ui-state-hover');
                            prevItem.children('.ui-menuitem-link').addClass('ui-state-hover');
                        }

                        e.preventDefault();
                    break;

                    case keyCode.DOWN:
                        var nextItem = currentLink.parent().nextAll('.ui-menuitem:first');
                        if(nextItem.length) {
                            currentLink.removeClass('ui-state-hover');
                            nextItem.children('.ui-menuitem-link').addClass('ui-state-hover');
                        }

                        e.preventDefault();
                    break;

                    case keyCode.ENTER:
                        currentLink.trigger('click');
                        $this.jq.trigger("blur");
                        PrimeFaces.utils.openLink(e, currentLink);
                    break;

                    case keyCode.ESCAPE:
                        $this.hide();

                        if($this.cfg.overlay) {
                            $this.trigger.trigger('focus');
                        }
                    break;

            }
        });
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

        if(stateful) {
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

        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        items.filter('.ui-submenu-child').show();

        if(stateful) {
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

        if(collapsedIdsAsString) {
            this.collapsedIds = collapsedIdsAsString.split(',');

            for(var i = 0 ; i < this.collapsedIds.length; i++) {
                this.collapseSubmenu($(PrimeFaces.escapeClientId(this.collapsedIds[i])), false);
            }
        }
    },

    /**
     * Clear the saved state (collapsed / expanded menu items) of this plain menu.
     * @private
     */
    clearState: function() {
        localStorage.removeItem(this.stateKey);
    }

});
