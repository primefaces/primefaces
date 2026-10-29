/**
 * The configuration for the {@link PanelMenu} widget.
 * 
 * You can access this configuration via {@link PanelMenu.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface PanelMenuCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
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

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.headers = this.jq.find('> .p-panelmenu-panel > h3.p-panelmenu-header:not(.p-state-disabled)');
        this.menuContent = this.jq.find('> .p-panelmenu-panel > .p-panelmenu-content');
        this.menuitemLinks = this.menuContent.find('.p-menuitem-link:not(.p-state-disabled)');
        this.menuText = this.menuitemLinks.find('.p-menuitem-text');
        this.treeLinks = this.menuContent.find('.p-menu-parent > .p-menuitem-link:not(.p-state-disabled)');

        //keyboard support
        this.focusedItem = null;
        this.menuText.attr('tabindex', -1);

        //ScreenReader support
        this.menuText.attr('role', 'menuitem');

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
            if(!element.hasClass('p-state-active')) {
                element.addClass('p-state-hover');
            }
        }).on("mouseout", function() {
            var element = $(this);
            if(!element.hasClass('p-state-active')) {
                element.removeClass('p-state-hover');
            }
        }).on("click", function(e) {
            var header = $(this);

            if (!$this.cfg.multiple) {
                $this.collapseActiveSibling(header);
            }

            if (header.hasClass('p-state-active'))
                $this.collapseRootSubmenu($(this));
            else
                $this.expandRootSubmenu($(this), false);

            $this.removeFocusedItem();
            header.trigger('focus');
            e.preventDefault();
        });

        this.menuitemLinks.on("mouseover", function() {
            $(this).addClass('p-state-hover');
        }).on("mouseout", function() {
            $(this).removeClass('p-state-hover');
        }).on("click", function(e) {
            var currentLink = $(this);
            $this.focusItem(currentLink.closest('.p-menuitem'));
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
            $(this).addClass('p-menuitem-outline');
        })
        .on('blur.panelmenu', function(){
            $(this).removeClass('p-menuitem-outline p-state-hover');
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
                        $this.focusedItem.children('.p-menuitem-link').trigger('click');
                    }
                    else {
                        var parentListOfItem = $this.focusedItem.closest('ul.p-menu-list');

                        if(parentListOfItem.parent().is(':not(.p-panelmenu-content)')) {
                            $this.focusItem(parentListOfItem.closest('li.p-menuitem'));
                        }
                    }

                    e.preventDefault();
                break;

                case 'ArrowRight':
                    if($this.focusedItem.hasClass('p-menu-parent') && !$this.isExpanded($this.focusedItem)) {
                        $this.focusedItem.children('.p-menuitem-link').trigger('click');
                    }
                    e.preventDefault();
                break;

                case 'ArrowUp':
                    var prevItem = $this.focusedItem.prev();

                    if(prevItem.length) {
                        itemToFocus = prevItem.find('li.p-menuitem:visible:last');
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
                    var currentLink = $this.focusedItem.children('.p-menuitem-link');
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
            if(!$(event.target).closest('.p-panelmenu').length) {
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
        this.collapseRootSubmenu(header.parent().siblings().children('.p-panelmenu-header.p-state-active').eq(0), animate);
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
     * @return The first child menu item of the given content, with the class `.p-menuitem`.
     */
    private getFirstItemOfContent(content: JQuery): JQuery {
        return content.find('> .p-menu-list > .p-menuitem:visible:first-child');
    }

    /**
     * Finds the displayed text of the given menu item.
     * @param item A menu item of this panel menu.
     * @return The displayed text of the given menu item, not including the text of sub menu items.
     */
    getItemText(item: JQuery): JQuery {
        return item.find('> .p-menuitem-link > span.p-menuitem-text');
    }

    /**
     * Puts focus on the given menu item.
     * @param item A menu item to focus. 
     */
    focusItem(item: JQuery): void {
        this.removeFocusedItem();
        this.getItemText(item).addClass('p-menuitem-outline').trigger('focus');
        this.focusedItem = item;
    }

    /**
     * Callback invoked after the focused menu item receives a blur.
     */
    private removeFocusedItem(): void {
        if(this.focusedItem) {
            this.getItemText(this.focusedItem).removeClass('p-menuitem-outline');
            this.focusedItem = null;
        }
    }

    /**
     * Checks whether the given menu items is currently expanded or collapsed.
     * @param item A menu item to check.
     * @return `true` if the given menu item is expanded (children are shown), or `false` otherwise. 
     */
    isExpanded(item: JQuery): boolean {
        return item.children('ul.p-menu-list').is(':visible');
    }

    /**
     * Collapses the given accordion panel, hiding the menu entries it contains.
     * @param header A menu panel to collapse.
     * @param animate Whether to animate the collapse.
     */
    collapseRootSubmenu(header: JQuery, animate = true): void {
        var panel = header.next();

        header.attr('aria-expanded', "false").removeClass('p-state-active').addClass('p-state-hover');

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

        header.attr('aria-expanded', "true").addClass('p-state-active').removeClass('p-state-hover');

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
        submenu.find('> .p-menuitem-link > .p-menuitem-text').attr('aria-expanded', "true");
        submenu.children('.p-menu-list').removeClass('p-hidden').show();

        if(!restoring) {
            this.updateExpandedNodes();
        }
    }

    /**
     * Collapses the given tree-like sub menu item, hiding the sub menu entries it contains.
     * @param submenu A sub menu tree item to collapse.
     */
    collapseTreeItem(submenu: JQuery): void {
        submenu.find('> .p-menuitem-link > .p-menuitem-text').attr('aria-expanded', "false");
        submenu.children('.p-menu-list').addClass('p-hidden').hide();

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
            const element = $(PrimeFaces.escapeClientId(expandedNode));
            if (element.is('div.p-panelmenu-content')) {
                this.expandRootSubmenu(element.prev(), true);
            }
            else if (element.is('li.p-menu-parent')) {
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
     * - Any panel header element (`this.headers`) that has the `p-state-active` class is considered expanded;
     *   its associated content panel ID (the header's next sibling's `id` attribute) is added.
     * - Any sub-menu panel (`.p-menu-list`) that is visible (does not have `p-hidden`), whose parent
     *   is a menu parent node (`.p-menu-parent`), will have that parent's `id` attribute added.
     */
    private updateExpandedNodes(): void {
        this.expandedNodes = [];

        // Find all active root panel headers and expanded submenus.
        const activeHeaders = this.headers.filter('.p-state-active'),
            activeTreeSubmenus = this.jq.find('.p-menu-parent > .p-menu-list:not(.p-hidden)');

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
        $this.headers.filter('.p-state-active').each(function() {
            const header = $(this);
            $this.collapseActiveSibling(header, false);
            $this.collapseRootSubmenu(header, false);
            header.removeClass('p-state-hover');
        });

        $this.jq.find('.p-menu-parent > .p-menu-list:not(.p-hidden)').each(function() {
            $this.collapseTreeItem($(this).parent());
        });
    }

    /**
     * Expands all menu panels that are currently collapsed.
     */
    expandAll(): void {
        let $this = this;
        $this.headers.filter(':not(.p-state-active)').each(function() {
            const header = $(this);
            if (!$this.cfg.multiple) {
                $this.collapseActiveSibling(header);
            }
            $this.expandRootSubmenu(header, false);
        });

        $this.jq.find('.p-menu-parent').each(function() {
            const submenu = $(this);
            const submenuList = submenu.children('.p-menu-list');
            if (!submenuList.is(':visible')) {
                $this.expandTreeItem(submenu, false);
            }
        });
    }
}
