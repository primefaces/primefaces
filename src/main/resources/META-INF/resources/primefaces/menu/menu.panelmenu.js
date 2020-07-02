/**
 * __PrimeFaces PanelMenu Widget__
 * 
 * PanelMenu is a hybrid component of accordionPanel and tree components.
 * 
 * @prop {string[]} expandedNodes A list of IDs of the menu items that are currently expanded.
 * @prop {boolean} focusCheck Flag for IE to keep track of whether an item was focused.
 * @prop {JQuery | null} focusedItem The DOM elements for the menu item that is currently focused.
 * @prop {JQuery} headers The DOM elements for the accordion panel headers that can be expanded and collapsed.
 * @prop {JQuery} menuitemLinks The DOM elements for the menu items inside each accordion panel that can be clicked.
 * @prop {JQuery} menuContent The DOM elements for the content container of each accordion panel.
 * @prop {JQuery} menuText The DOM elements for the text of each menu entry in the accordion panels.
 * @prop {string} stateKey Key used to store the UI state (expanded items) in an HTML5 Local Store. 
 * @prop {JQuery} treeLinks  The DOM elements for the clickable links with a sub menu that is shown upon clicking the
 * link. 
 * 
 * @interface {PrimeFaces.widget.PanelMenuCfg} cfg The configuration for the {@link  PanelMenu| PanelMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.multiple Whether multiple accordion menu items are allowed to be expanded at the same time.
 * @prop {boolean} cfg.stateful Whether the UI state (expanded menu items) should be persisted in an HTML5 Local Store.
 */
PrimeFaces.widget.PanelMenu = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.headers = this.jq.find('> .ui-panelmenu-panel > h3.ui-panelmenu-header:not(.ui-state-disabled)');
        this.menuContent = this.jq.find('> .ui-panelmenu-panel > .ui-panelmenu-content');
        this.menuitemLinks = this.menuContent.find('.ui-menuitem-link:not(.ui-state-disabled)');
        this.menuText = this.menuitemLinks.find('.ui-menuitem-text');
        this.treeLinks = this.menuContent.find('.ui-menu-parent > .ui-menuitem-link:not(.ui-state-disabled)');

        //keyboard support
        this.focusedItem = null;
        this.menuText.attr('tabindex', -1);

        //ScreenReader support
        this.menuText.attr('role', 'menuitem');
        this.treeLinks.find('> .ui-menuitem-text').attr('aria-expanded', false);

        this.bindEvents();

        if(this.cfg.stateful) {
            this.stateKey = PrimeFaces.createStorageKey(this.id, 'PanelMenu');
        }

        this.restoreState();
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.headers.on("mouseover", function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')) {
                element.addClass('ui-state-hover');
            }
        }).on("mouseout", function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')) {
                element.removeClass('ui-state-hover');
            }
        }).on("click", function(e) {
            var header = $(this);

            if (!$this.cfg.multiple) {
                $this.collapseActiveSibling(header);
            }

            if (header.hasClass('ui-state-active'))
                $this.collapseRootSubmenu($(this));
            else
                $this.expandRootSubmenu($(this), false);

            $this.removeFocusedItem();
            header.trigger('focus');
            e.preventDefault();
        });

        this.menuitemLinks.on("mouseover", function() {
            $(this).addClass('ui-state-hover');
        }).on("mouseout", function() {
            $(this).removeClass('ui-state-hover');
        }).on("click", function(e) {
            var currentLink = $(this);
            $this.focusItem(currentLink.closest('.ui-menuitem'));
            PrimeFaces.utils.openLink(e, currentLink);
        });

        this.treeLinks.on("click", function(e) {
            var link = $(this),
            submenu = link.parent(),
            submenuList = link.next();

            if(submenuList.is(':visible'))
                $this.collapseTreeItem(submenu);
            else
                $this.expandTreeItem(submenu, false);

            e.preventDefault();
        });

        this.bindKeyEvents();
    },

    /**
     * Sets up the keyboard event listeners required by this panel menu widget.
     * @private
     */
    bindKeyEvents: function() {
        var $this = this;

        if(PrimeFaces.env.isIE()) {
            this.focusCheck = false;
        }

        this.headers.on('focus.panelmenu', function(){
            $(this).addClass('ui-menuitem-outline');
        })
        .on('blur.panelmenu', function(){
            $(this).removeClass('ui-menuitem-outline ui-state-hover');
        })
        .on('keydown.panelmenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                $(this).trigger('click');
                e.preventDefault();
            }
        });

        this.menuContent.on('mousedown.panelmenu', function(e) {
            if($(e.target).is(':not(:input:enabled)')) {
                e.preventDefault();
            }
        }).on('focus.panelmenu', function(){
            if(!$this.focusedItem) {
                $this.focusItem($this.getFirstItemOfContent($(this)));
                if(PrimeFaces.env.isIE()) {
                    $this.focusCheck = false;
                }
            }
        });

        this.menuContent.off('keydown.panelmenu blur.panelmenu').on('keydown.panelmenu', function(e) {
            if(!$this.focusedItem) {
                return;
            }

            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.LEFT:
                    if($this.isExpanded($this.focusedItem)) {
                        $this.focusedItem.children('.ui-menuitem-link').trigger('click');
                    }
                    else {
                        var parentListOfItem = $this.focusedItem.closest('ul.ui-menu-list');

                        if(parentListOfItem.parent().is(':not(.ui-panelmenu-content)')) {
                            $this.focusItem(parentListOfItem.closest('li.ui-menuitem'));
                        }
                    }

                    e.preventDefault();
                break;

                case keyCode.RIGHT:
                    if($this.focusedItem.hasClass('ui-menu-parent') && !$this.isExpanded($this.focusedItem)) {
                        $this.focusedItem.children('.ui-menuitem-link').trigger('click');
                    }
                    e.preventDefault();
                break;

                case keyCode.UP:
                    var itemToFocus = null,
                    prevItem = $this.focusedItem.prev();

                    if(prevItem.length) {
                        itemToFocus = prevItem.find('li.ui-menuitem:visible:last');
                        if(!itemToFocus.length) {
                            itemToFocus = prevItem;
                        }
                    }
                    else {
                        itemToFocus = $this.focusedItem.closest('ul').parent('li');
                    }

                    if(itemToFocus.length) {
                        $this.focusItem(itemToFocus);
                    }

                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    var itemToFocus = null,
                    firstVisibleChildItem = $this.focusedItem.find('> ul > li:visible:first');

                    if(firstVisibleChildItem.length) {
                        itemToFocus = firstVisibleChildItem;
                    }
                    else if($this.focusedItem.next().length) {
                        itemToFocus = $this.focusedItem.next();
                    }
                    else {
                        if($this.focusedItem.next().length === 0) {
                            itemToFocus = $this.searchDown($this.focusedItem);
                        }
                    }

                    if(itemToFocus && itemToFocus.length) {
                        $this.focusItem(itemToFocus);
                    }

                    e.preventDefault();
                break;

                case keyCode.ENTER:
                case keyCode.SPACE:
                    var currentLink = $this.focusedItem.children('.ui-menuitem-link');
                    //IE fix
                    setTimeout(function(){
                        currentLink.trigger('click');
                    },1);
                    $this.jq.trigger("blur");

                    var href = currentLink.attr('href');
                    if(href && href !== '#') {
                        window.location.href = href;
                    }
                    e.preventDefault();
                break;

                case keyCode.TAB:
                    if($this.focusedItem) {
                        if(PrimeFaces.env.isIE()) {
                            $this.focusCheck = true;
                        }
                        $(this).trigger('focus');
                    }
                break;
            }
        }).on('blur.panelmenu', function(e) {
            if(PrimeFaces.env.isIE() && !$this.focusCheck) {
                return;
            }

            $this.removeFocusedItem();
        });

        var clickNS = 'click.' + this.id;
        //remove focusedItem when document is clicked
        $(document.body).off(clickNS).on(clickNS, function(event) {
            if(!$(event.target).closest('.ui-panelmenu').length) {
               $this.removeFocusedItem();
            }
        });
    },

    /**
     * Collapses all siblings of the given header column.
     * @private
     * @param {JQuery} header The header column that was clicked. 
     */
    collapseActiveSibling: function(header) {
        this.collapseRootSubmenu(header.parent().siblings().children('.ui-panelmenu-header.ui-state-active').eq(0));
    },

    /**
     * Finds the next menu item to focus and highlight when the user presses the down arrow key.
     * @param {JQuery} item An item where to start the search.
     * @return {JQuery | null} The found item that should receive focus, or `null` if no item was found.
     * @private
     */
    searchDown: function(item) {
        var nextOfParent = item.closest('ul').parent('li').next(),
        itemToFocus = null;

        if(nextOfParent.length) {
            itemToFocus = nextOfParent;
        }
        else if(item.closest('ul').parent('li').length === 0){
            itemToFocus = item;
        }
        else {
            itemToFocus = this.searchDown(item.closest('ul').parent('li'));
        }

        return itemToFocus;
    },

    /**
     * Finds the first child menu item of the given content element.
     * @param {JQuery} content Some content element of this panel menu.
     * @return {JQuery} The first child menu item of the given content, with the class `.ui-menuitem`.
     * @private
     */
    getFirstItemOfContent: function(content) {
        return content.find('> .ui-menu-list > .ui-menuitem:visible:first-child');
    },

    /**
     * Finds the displayed text of the given menu item.
     * @param {JQuery} item A menu item of this panel menu.
     * @return {string} The displayed text of the given menu item, not including the text of sub menu items.
     */
    getItemText: function(item) {
        return item.find('> .ui-menuitem-link > span.ui-menuitem-text');
    },

    /**
     * Puts focus on the given menu item.
     * @param {JQuery} item A menu item to focus. 
     */
    focusItem: function(item) {
        this.removeFocusedItem();
        this.getItemText(item).addClass('ui-menuitem-outline').trigger('focus');
        this.focusedItem = item;
    },

    /**
     * Callback invoked after the focused menu item receives a blur.
     * @private
     */
    removeFocusedItem: function() {
        if(this.focusedItem) {
            this.getItemText(this.focusedItem).removeClass('ui-menuitem-outline');
            this.focusedItem = null;
        }
    },

    /**
     * Checks whether the given menu items is currently expanded or collapsed.
     * @param {JQuery} item A menu item to check.
     * @return {boolean} `true` if the given menu item is expanded (children are shown), or `false` otherwise. 
     */
    isExpanded: function(item) {
        return item.children('ul.ui-menu-list').is(':visible');
    },

    /**
     * Collapses the given accordional panel, hiding the menu entries it contains.
     * @param {JQuery} header A menu panel to collapse.
     */
    collapseRootSubmenu: function(header) {
        var panel = header.next();

        header.attr('aria-expanded', false).removeClass('ui-state-active ui-corner-top').addClass('ui-state-hover ui-corner-all')
                            .children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        panel.attr('aria-hidden', true).slideUp('normal', 'easeInOutCirc');

        this.removeAsExpanded(panel);
    },

    /**
     * Expands the given accordional panel, showing the menu entries it contains.
     * @param {JQuery} header A menu panel to collapse.
     * @param {boolean} [restoring] Whether this method was called from `restoreState`.
     */
    expandRootSubmenu: function(header, restoring) {
        var panel = header.next();

        header.attr('aria-expanded', true).addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
                .children('.ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        if(restoring) {
            panel.attr('aria-hidden', false).show();
        }
        else {
            panel.attr('aria-hidden', false).slideDown('normal', 'easeInOutCirc');

            this.addAsExpanded(panel);
        }
    },

    /**
     * Expands the given tree-like sub menu item, showing the sub menu entries it contains.
     * @param {JQuery} submenu A sub menu tree item to expand.
     * @param {boolean} [restoring] Whether this method was called from `restoreState`.
     */
    expandTreeItem: function(submenu, restoring) {
        var submenuLink = submenu.find('> .ui-menuitem-link');

        submenuLink.find('> .ui-menuitem-text').attr('aria-expanded', true);
        submenuLink.find('> .ui-panelmenu-icon').addClass('ui-icon-triangle-1-s');
        submenu.children('.ui-menu-list').show();

        if(!restoring) {
            this.addAsExpanded(submenu);
        }
    },

    /**
     * Collapses the given tree-like sub menu item, hiding the sub menu entries it contains.
     * @param {JQuery} submenu A sub menu tree item to collapse.
     */
    collapseTreeItem: function(submenu) {
        var submenuLink = submenu.find('> .ui-menuitem-link');

        submenuLink.find('> .ui-menuitem-text').attr('aria-expanded', false);
        submenuLink.find('> .ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s');
        submenu.children('.ui-menu-list').hide();

        this.removeAsExpanded(submenu);
    },

    /**
     * Writes the UI state of this panel menu to an HTML5 Local Store. Used to preserve the state during AJAX updates as well as
     * between page reloads.
     * @private
     */
    saveState: function() {
        if(this.cfg.stateful) {
            var expandedNodeIds = this.expandedNodes.join(',');

            localStorage.setItem(this.stateKey, expandedNodeIds);
        }
    },

    /**
     * Read the UI state of this panel menu stored in an HTML5 Local Store and reapplies to this panel menu. Used to preserve the
     * state during AJAX updates as well as between page reloads.
     * @private
     */
    restoreState: function() {
        var expandedNodeIds = null;

        if(this.cfg.stateful) {
            expandedNodeIds = localStorage.getItem(this.stateKey);
        }

        if(expandedNodeIds) {
            this.collapseAll();
            this.expandedNodes = expandedNodeIds.split(',');

            for(var i = 0 ; i < this.expandedNodes.length; i++) {
                var element = $(PrimeFaces.escapeClientId(this.expandedNodes[i]));
                if(element.is('div.ui-panelmenu-content'))
                    this.expandRootSubmenu(element.prev(), true);
                else if(element.is('li.ui-menu-parent'))
                    this.expandTreeItem(element, true);
            }
        }
        else {
            this.expandedNodes = [];
            var activeHeaders = this.headers.filter('.ui-state-active'),
            activeTreeSubmenus = this.jq.find('.ui-menu-parent > .ui-menu-list:not(.ui-helper-hidden)');

            for(var i = 0; i < activeHeaders.length; i++) {
                this.expandedNodes.push(activeHeaders.eq(i).next().attr('id'));
            }

            for(var i = 0; i < activeTreeSubmenus.length; i++) {
                this.expandedNodes.push(activeTreeSubmenus.eq(i).parent().attr('id'));
            }
        }
    },

    /**
     * Callback invoked after a menu item was collapsed. Saves the current UI state in an HTML5 Local Store.
     * @param {JQuery} element Element that was collapsed.
     * @private
     */
    removeAsExpanded: function(element) {
        var id = element.attr('id');

        this.expandedNodes = $.grep(this.expandedNodes, function(value) {
            return value != id;
        });

        this.saveState();
    },

    /**
     * Callback invoked after a menu item was expanded. Saves the current UI state in an HTML5 Local Store.
     * @param {JQuery} element Element that was expanded.
     * @private
     */
    addAsExpanded: function(element) {
        this.expandedNodes.push(element.attr('id'));

        this.saveState();
    },

    /**
     * Deletes the UI state of this panel menu stored in an HTML5 Local Store.
     * @private
     */
    clearState: function() {
        if(this.cfg.stateful) {
            localStorage.removeItem(this.stateKey);
        }
    },

    /**
     * Collapses all menu panels that are currently expanded.
     */
    collapseAll: function() {
        this.headers.filter('.ui-state-active').each(function() {
            var header = $(this);
            header.removeClass('ui-state-active').children('.ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
            header.next().addClass('ui-helper-hidden');
        });

        this.jq.find('.ui-menu-parent > .ui-menu-list:not(.ui-helper-hidden)').each(function() {
            $(this).addClass('ui-helper-hidden').prev().children('.ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
        });
    }

});
