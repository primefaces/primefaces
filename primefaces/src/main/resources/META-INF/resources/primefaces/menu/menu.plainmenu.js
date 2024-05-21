
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

        //events
        this.bindEvents();

        if(this.cfg.toggleable) {
            this.cfg.statefulGlobal = this.cfg.statefulGlobal === true ? true : false;
            this.collapsedIds = [];
            this.createStorageKey();
            this.restoreState();
        }
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;
        
        // make first focusable
        this.menuitemLinks.filter(':not([disabled])').first().attr("tabindex", "0");
        this.resetFocus(true);

        this.menuitemLinks.on("mouseenter.menu click.menu", function(e) {
            $(this).trigger('focus');
        })
        .on("focusin.menu", function(e) {
            $this.focus($(this));
        })
        .on("focusout.menu mouseleave.menu", function(e) {
            $this.unfocus($(this));
        })
        .on('keydown.menu', function(e) {
            var currentLink = $this.menuitemLinks.filter('.ui-state-hover');
            switch(e.code) {
                    case 'ArrowUp':
                        var prevItem = currentLink.parent().prevAll('.ui-menuitem:first');
                        if (prevItem.length) {
                            $this.unfocus(currentLink);
                            $this.focus(prevItem.children('.ui-menuitem-link'));
                        }

                        e.preventDefault();
                    break;

                    case 'ArrowDown':
                        var nextItem = currentLink.parent().nextAll('.ui-menuitem:first');
                        if(nextItem.length) {
                            $this.unfocus(currentLink);
                            $this.focus(nextItem.children('.ui-menuitem-link'));
                        }

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

                        if($this.cfg.overlay) {
                            $this.trigger.trigger('focus');
                        }
                    break;

            }
        });

        if(this.cfg.overlay) {
            this.menuitemLinks.on("click", function() {
                $this.hide();
            });

            this.trigger.on('keydown.ui-menu', function(e) {
                switch(e.key) {
                    case 'ArrowDown':
                        if(!$this.jq.is(':visible')) {
                            $this.show();
                        }
                        e.preventDefault();
                    break;

                    case 'Tab':
                        if($this.jq.is(':visible')) {
                            $this.hide();
                        }
                    break;
                }
            });
        }
        else {
            this.jq.on("focusout.menu", function(e) {
                if (!$this.jq.has(e.relatedTarget).length) {
                    $this.resetFocus(true);
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

        header.attr('aria-expanded', true)
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
                var id = this.collapsedIds[i];
                if (id) {
                    this.collapseSubmenu($(PrimeFaces.escapeClientId(id).replace(/\|/g,"\\|")), false);
                }
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
