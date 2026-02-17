/**
 * The configuration for the {@link PanelMenu} widget.
 * 
 * You can access this configuration via {@link PanelMenu.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface PanelMenuCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * Whether multiple accordion menu items are allowed to be expanded at the same time.
     */
    multiple: boolean;

    /**
     * Whether the UI state (expanded menu items) should be persisted in an HTML5 Local Store.
     */
    stateful: boolean;

    /**
     * When enabled, menu state is saved globally across pages. If disabled then state
     * is stored per view/page.
     */
    statefulGlobal: boolean;
}

/**
 * __PrimeFaces PanelMenu Widget__
 * 
 * PanelMenu is a hybrid component of accordionPanel and tree components.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class PanelMenu<Cfg extends PanelMenuCfg = PanelMenuCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * A list of IDs of the menu items that are currently expanded.
     */
    expandedNodes: string[] = [];

    /**
     * The DOM elements for the menu item that is currently focused.
     */
    focusedItem: JQuery | null = null;

    /**
     * The DOM elements for the accordion panel headers that can be expanded and collapsed.
     */
    headers: JQuery = $();

    /**
     * The DOM elements for the content container of each accordion panel.
     */
    menuContent: JQuery = $();

    /**
     * The DOM elements for the text of each menu entry in the accordion panels.
     */
    menuText: JQuery = $();

    /**
     * The DOM elements for the menu items inside each accordion panel that can be clicked.
     */
    menuitemLinks: JQuery = $();

    /**
     * Key used to store the UI state (expanded items) in an HTML5 Local Store.
     */
    stateKey: string = "";

    /**
     * The DOM elements for the clickable links with a sub menu that is shown upon clicking the
     * link.
     */
    treeLinks: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
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
        this.treeLinks.find('> .ui-menuitem-text').attr('aria-expanded', "false");

        this.bindEvents();

        if(this.cfg.stateful) {
            this.cfg.statefulGlobal = !!this.cfg.statefulGlobal;
            this.createStorageKey();
        }

        this.restoreState();
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
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
    }

    /**
     * Sets up the keyboard event listeners required by this panel menu widget.
     */
    private bindKeyEvents(): void {
        var $this = this;

        this.headers.on('focus.panelmenu', function(){
            $(this).addClass('ui-menuitem-outline');
        })
        .on('blur.panelmenu', function(){
            $(this).removeClass('ui-menuitem-outline ui-state-hover');
        })
        .on('keydown.panelmenu', function(e) {
            if (PrimeFaces.utils.isActionKey(e)) {
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
            }
        });

        this.menuContent.off('keydown.panelmenu blur.panelmenu').on('keydown.panelmenu', function(e) {
            if(!$this.focusedItem) {
                return;
            }
            var itemToFocus = null;

            switch("code" in e ? e.code : e.key) {
                case 'ArrowLeft':
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

                case 'ArrowRight':
                    if($this.focusedItem.hasClass('ui-menu-parent') && !$this.isExpanded($this.focusedItem)) {
                        $this.focusedItem.children('.ui-menuitem-link').trigger('click');
                    }
                    e.preventDefault();
                break;

                case 'ArrowUp':
                    var prevItem = $this.focusedItem.prev();

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

                case 'ArrowDown':
                    var firstVisibleChildItem = $this.focusedItem.find('> ul > li:visible:first');

                    if(firstVisibleChildItem.length) {
                        itemToFocus = firstVisibleChildItem;
                    }
                    else if($this.focusedItem.next().length) {
                        itemToFocus = $this.focusedItem.next();
                    }
                    else if($this.focusedItem.next().length === 0) {
                        itemToFocus = $this.searchDown($this.focusedItem);
                    }

                    if(itemToFocus && itemToFocus.length) {
                        $this.focusItem(itemToFocus);
                    }

                    e.preventDefault();
                break;

                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    var currentLink = $this.focusedItem.children('.ui-menuitem-link');
                    PrimeFaces.queueTask(function(){
                        currentLink.trigger('click');
                    });
                    $this.jq.trigger("blur");

                    var href = currentLink.attr('href');
                    if(href && href !== '#') {
                        window.location.href = href;
                    }
                    e.preventDefault();
                break;

                case 'Tab':
                    if($this.focusedItem) {
                        $(this).trigger('focus');
                    }
                break;
            }
        }).on('blur.panelmenu', function(e) {
            $this.removeFocusedItem();
        });

        var clickNS = 'click.' + this.id;
        //remove focusedItem when document is clicked
        $(document.body).off(clickNS).on(clickNS, function(event) {
            if(!$(event.target).closest('.ui-panelmenu').length) {
               $this.removeFocusedItem();
            }
        });
        this.addDestroyListener(function() {
            $(document.body).off(clickNS);
        });
    }

    /**
     * Create the key where the state for this component is stored.  By default PanelMenu state is global so it is 
     * remembered when navigating between pages.
     */
    createStorageKey(): void {
        this.stateKey = PrimeFaces.createStorageKey(this.getId(), 'PanelMenu', this.cfg.statefulGlobal);
    }

    /**
     * Collapses all siblings of the given header column.
     * @param header The header column that was clicked. 
     * @param animate Whether to animate the collapse.
     */
    private collapseActiveSibling(header: JQuery, animate = true): void {
        this.collapseRootSubmenu(header.parent().siblings().children('.ui-panelmenu-header.ui-state-active').eq(0), animate);
    }

    /**
     * Finds the next menu item to focus and highlight when the user presses the down arrow key.
     * @param item An item where to start the search.
     * @return The found item that should receive focus, or `null` if no item was found.
     */
    private searchDown(item: JQuery): JQuery {
        const nextOfParent = item.closest('ul').parent('li').next();
        let itemToFocus: JQuery;

        if (nextOfParent.length) {
            itemToFocus = nextOfParent;
        }
        else if (item.closest('ul').parent('li').length === 0){
            itemToFocus = item;
        }
        else {
            itemToFocus = this.searchDown(item.closest('ul').parent('li'));
        }

        return itemToFocus;
    }

    /**
     * Finds the first child menu item of the given content element.
     * @param content Some content element of this panel menu.
     * @return The first child menu item of the given content, with the class `.ui-menuitem`.
     */
    private getFirstItemOfContent(content: JQuery): JQuery {
        return content.find('> .ui-menu-list > .ui-menuitem:visible:first-child');
    }

    /**
     * Finds the displayed text of the given menu item.
     * @param item A menu item of this panel menu.
     * @return The displayed text of the given menu item, not including the text of sub menu items.
     */
    getItemText(item: JQuery): JQuery {
        return item.find('> .ui-menuitem-link > span.ui-menuitem-text');
    }

    /**
     * Puts focus on the given menu item.
     * @param item A menu item to focus. 
     */
    focusItem(item: JQuery): void {
        this.removeFocusedItem();
        this.getItemText(item).addClass('ui-menuitem-outline').trigger('focus');
        this.focusedItem = item;
    }

    /**
     * Callback invoked after the focused menu item receives a blur.
     */
    private removeFocusedItem(): void {
        if(this.focusedItem) {
            this.getItemText(this.focusedItem).removeClass('ui-menuitem-outline');
            this.focusedItem = null;
        }
    }

    /**
     * Checks whether the given menu items is currently expanded or collapsed.
     * @param item A menu item to check.
     * @return `true` if the given menu item is expanded (children are shown), or `false` otherwise. 
     */
    isExpanded(item: JQuery): boolean {
        return item.children('ul.ui-menu-list').is(':visible');
    }

    /**
     * Collapses the given accordion panel, hiding the menu entries it contains.
     * @param header A menu panel to collapse.
     * @param animate Whether to animate the collapse.
     */
    collapseRootSubmenu(header: JQuery, animate = true): void {
        var panel = header.next();

        header.attr('aria-expanded', "false").removeClass('ui-state-active').addClass('ui-state-hover')
            .children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        panel.attr('aria-hidden', "true").slideUp(animate ? 400 : 0, 'easeInOutCirc');

        this.updateExpandedNodes();
    }

    /**
     * Expands the given accordion panel, showing the menu entries it contains.
     * @param header A menu panel to collapse.
     * @param restoring Whether this method was called from `restoreState`.
     */
    expandRootSubmenu(header: JQuery, restoring?: boolean): void {
        var panel = header.next();

        header.attr('aria-expanded', "true").addClass('ui-state-active').removeClass('ui-state-hover')
                .children('.ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        if(restoring) {
            panel.attr('aria-hidden', "false").show();
        }
        else {
            panel.attr('aria-hidden', "false").slideDown(400, 'easeInOutCirc');

            this.updateExpandedNodes();
        }
    }

    /**
     * Expands the given tree-like sub menu item, showing the sub menu entries it contains.
     * @param submenu A sub menu tree item to expand.
     * @param restoring Whether this method was called from `restoreState`.
     */
    expandTreeItem(submenu: JQuery, restoring?: boolean): void {
        var submenuLink = submenu.find('> .ui-menuitem-link');

        submenuLink.find('> .ui-menuitem-text').attr('aria-expanded', "true");
        submenuLink.find('> .ui-panelmenu-icon').addClass('ui-icon-triangle-1-s');
        submenu.children('.ui-menu-list').show();

        if(!restoring) {
            this.updateExpandedNodes();
        }
    }

    /**
     * Collapses the given tree-like sub menu item, hiding the sub menu entries it contains.
     * @param submenu A sub menu tree item to collapse.
     */
    collapseTreeItem(submenu: JQuery): void {
        var submenuLink = submenu.find('> .ui-menuitem-link');

        submenuLink.find('> .ui-menuitem-text').attr('aria-expanded', "false");
        submenuLink.find('> .ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
        submenu.children('.ui-menu-list').hide();

        this.updateExpandedNodes();
    }

    /**
     * Writes the UI state of this panel menu to an HTML5 Local Store. Used to preserve the state during AJAX updates as well as
     * between page reloads.
     */
    private saveState(): void {
        if(this.cfg.stateful && this.expandedNodes) {
            const uniqueExpandedNodes = Array.from(new Set(this.expandedNodes));
            const expandedNodeIds = uniqueExpandedNodes.join(',');
            localStorage.setItem(this.stateKey, expandedNodeIds);
        }
    }

    /**
     * Read the UI state of this panel menu stored in an HTML5 Local Store and reapplies to this panel menu. Used to preserve the
     * state during AJAX updates as well as between page reloads.
     */
    private restoreState(): void {
        let expandedNodeIds: string | null = null;

        if (this.cfg.stateful) {
            expandedNodeIds = localStorage.getItem(this.stateKey);
        }

        // if no stateKey is found at all its initial state from server side
        if (expandedNodeIds === null) {
            return;
        }

        // state key was found but its empty so all nodes are collapsed
        if (expandedNodeIds.length === 0) {
            this.collapseAll();
            return;
        }

        // state key was found and its not empty so we need to expand the nodes
        this.collapseAll();
        this.expandedNodes = expandedNodeIds.split(',');

        for (const expandedNode of this.expandedNodes) {
            const element = $(PrimeFaces.escapeClientId(expandedNode).replace(/\|/g, "\\|"));
            if (element.is('div.ui-panelmenu-content')) {
                this.expandRootSubmenu(element.prev(), true);
            }
            else if (element.is('li.ui-menu-parent')) {
                this.expandTreeItem(element, true);
            }
        }

        this.updateExpandedNodes();
    }

    /**
     * Recalculates and updates the list of currently expanded menu nodes.
     *
     * This method examines the DOM to find all expanded root headers and tree submenu items, collects
     * their IDs, and updates the {@link PanelMenu.expandedNodes} array. After updating the expanded nodes,
     * the method persists the state via {@link saveState}.
     *
     * Expanded nodes are determined as follows:
     * - Any panel header element (`this.headers`) that has the `ui-state-active` class is considered expanded;
     *   its associated content panel ID (the header's next sibling's `id` attribute) is added.
     * - Any sub-menu panel (`.ui-menu-list`) that is visible (does not have `ui-helper-hidden`), whose parent
     *   is a menu parent node (`.ui-menu-parent`), will have that parent's `id` attribute added.
     */
    private updateExpandedNodes(): void {
        this.expandedNodes = [];

        // Find all active root panel headers and expanded submenus.
        const activeHeaders = this.headers.filter('.ui-state-active'),
            activeTreeSubmenus = this.jq.find('.ui-menu-parent > .ui-menu-list:not(.ui-helper-hidden)');

        // Collect IDs of expanded panel headers.
        for (let j = 0; j < activeHeaders.length; j++) {
            this.expandedNodes.push(activeHeaders.eq(j).next().attr('id') ?? "");
        }

        // Collect IDs of expanded submenu parents.
        for (let k = 0; k < activeTreeSubmenus.length; k++) {
            this.expandedNodes.push(activeTreeSubmenus.eq(k).parent().attr('id') ?? "");
        }

        // Save the current expanded state.
        this.saveState();
    }

    /**
     * Deletes the UI state of this panel menu stored in an HTML5 Local Store.
     */
    clearState(): void {
        if (this.cfg.stateful) {
            localStorage.removeItem(this.stateKey);
        }
    }

    /**
     * Collapses all menu panels that are currently expanded.
     */
    collapseAll(): void {
        let $this = this;
        $this.headers.filter('.ui-state-active').each(function() {
            const header = $(this);
            $this.collapseActiveSibling(header, false);
            $this.collapseRootSubmenu(header, false);
            header.removeClass('ui-state-hover');
        });

        $this.jq.find('.ui-menu-parent > .ui-menu-list:not(.ui-helper-hidden)').each(function() {
            $this.collapseTreeItem($(this));
        });
    }

    /**
     * Expands all menu panels that are currently collapsed.
     */
    expandAll(): void {
        let $this = this;
        $this.headers.filter(':not(.ui-state-active)').each(function() {
            const header = $(this);
            if (!$this.cfg.multiple) {
                $this.collapseActiveSibling(header);
            }
            $this.expandRootSubmenu(header, false);
        });

        $this.jq.find('.ui-menu-parent').each(function() {
            const submenu = $(this);
            const submenuList = submenu.children('.ui-menu-list');
            if (!submenuList.is(':visible')) {
                $this.expandTreeItem(submenu, false);
            }
        });
    }
}
