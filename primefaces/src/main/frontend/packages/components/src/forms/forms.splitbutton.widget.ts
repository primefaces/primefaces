/**
 * The configuration for the {@link SplitButton} widget.
 * 
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface SplitButtonCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * The search expression for the element to which the overlay panel should be appended.
     */
    appendTo: string;

    /**
     * Whether this input is currently disabled.
     */
    disabled: boolean;

    /**
     * Whether client side filtering feature is enabled.
     */
    filter: boolean;

    /**
     * Custom JavaScript function for filtering the
     * available split button actions.
     */
    filterFunction: PrimeType.widget.SplitButton.FilterFunction;

    /**
     * Defines if the filter should receive focus on overlay popup.
     */
    filterInputAutoFocus: boolean;

    /**
     * Match mode for filtering, how the search
     * term is matched against the items.
     */
    filterMatchMode: PrimeType.widget.SplitButton.FilterMatchMode;

    /**
     * Defines if filtering would be done using normalized values.
     */
    filterNormalize: boolean;

    /**
     * Defines if dynamic loading is enabled for the element's menu. If the value is `true`,
     * the menu is not rendered on page load to improve performance.
     */
    dynamic: boolean;
}

/**
 * __PrimeFaces SplitButton Widget__
 *
 * SplitButton displays a command by default and additional ones in an overlay.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class SplitButton<Cfg extends SplitButtonCfg = SplitButtonCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * The DOM element for the main button.
     */
    button: JQuery = $();

    /**
     * The DOM element for the filter input field
     */
    filterInput: JQuery = $();

    /**
     * The current filter function.
     */
    filterMatcher?: PrimeType.widget.SplitButton.FilterFunction;

    /**
     * A map of all flter functions. The
     * key is the name of the filter function.
     */
    filterMatchers: Partial<Record<string, PrimeType.widget.SplitButton.FilterFunction>> = {};

    /**
     * Unbind callback for the hide overlay handler.
     */
    hideOverlayHandler?: PrimeType.Unbindable;

    /**
     * The DOM element for the additional buttons actions.
     */
    menu: JQuery = $();

    /**
     * The DOM element for the button that triggers the overlay panel with the additional buttons
     * actions.
     */
    menuButton: JQuery = $();

    /**
     * The prefix shared ny the different IDs of the components of this widget.
     */
    menuId: string = "";

    /**
     * The DOM element for the container of the additional buttons actions.
     */
    menuitemContainer: JQuery = $();

    /**
     * The DOM elements for the individual additional button actions.
     */
    menuitems: JQuery = $();

    /**
     * Unbind callback for the resize handler.
     */
    resizeHandler?: PrimeType.Unbindable;

    /**
     * Unbind callback for the scroll handler.
     */
    scrollHandler?: PrimeType.Unbindable;

    /**
     * Handler for CSS transitions used by this widget.
     */
    transition?: PrimeType.CssTransitionHandler | null;

    /**
     * If dynamic loading is enabled, whether the menu content was loaded already.
     */
    isDynamicLoaded: boolean = false;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
         super.init(cfg);
        this.button = $(this.jqId + '_button');
        this.menuButton = $(this.jqId + '_menuButton');
        this.menuId = this.jqId + '_menu';
        this.menu = $(this.menuId);
        this.cfg.disabled = this.button.is(':disabled');
        this.cfg.filterInputAutoFocus = (this.cfg.filterInputAutoFocus === undefined) ? true : this.cfg.filterInputAutoFocus;
        this.cfg.dynamic = this.cfg.dynamic || false;
        this.isDynamicLoaded = !this.cfg.dynamic;

        if (this.menu.length > 0) {
            this.menuitemContainer = this.menu.find('.ui-menu-list');
            this.menuitems = this.menuitemContainer.children('.ui-menuitem:not(.ui-state-disabled)');
            PrimeFaces.utils.registerDynamicOverlay(this, this.menu, this.id + '_menu');
            this.transition = PrimeFaces.utils.registerCSSTransition(this.menu, 'ui-connected-overlay');
        }

        this.bindEvents();

        //pfs metadata
        this.button.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.menuButton.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        this.menuButton.off('click.splitbutton');
        if (this.menuitems.length > 0) {
            this.menuitems.off('mouseover.splitbutton mouseout.splitbutton click.splitbutton');
        }
        this.menuButton.off('keydown.splitbutton keyup.splitbutton');
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);

        super.refresh(cfg);
    }

    /**
     * Disables this button so that the user cannot press the button anymore.
     */
    override disable(): void {
        this.cfg.disabled = true;
        this.hide();
        PrimeFaces.utils.disableButton(this.button);
        PrimeFaces.utils.disableButton(this.menuButton);
    }

    /**
     * Enables this button so that the user can press the button.
     */
    override enable(): void {
        this.cfg.disabled = false;
        PrimeFaces.utils.enableButton(this.button);
        PrimeFaces.utils.enableButton(this.menuButton);
    }

    /**
     * Sets up all event listeners required by this widget.
     */
    private bindEvents(): void {
        PrimeFaces.skinButton(this.button).skinButton(this.menuButton);

        // mark button and descendants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        // toggle menu
        this.menuButton.attr('aria-label', this.getAriaLabel('listLabel'));
        this.menuButton.on('click.splitbutton', () => {
            if(!this.cfg.disabled && (this.menu.length === 0 || this.menu.is(':hidden')))
                this.show();
            else
                this.hide();
        });

        //menuitem visuals
        if (this.menuitems.length > 0) {
            this.menuitems.on('mouseover.splitbutton', function() {
                var menuitem = $(this),
                menuitemLink = menuitem.children('.ui-menuitem-link');

                if(!menuitemLink.hasClass('ui-state-disabled')) {
                    menuitem.addClass('ui-state-hover');
                }
            }).on('mouseout.splitbutton', function() {
                $(this).removeClass('ui-state-hover');
            }).on('click.splitbutton', () => {
                this.hide();
            });
        }

        //keyboard support
        this.menuButton.on('keydown.splitbutton', (e) => {
            if (this.cfg.disabled) {
                return;
            }
            switch("code" in e ? e.code : e.key) {
                case 'ArrowUp':
                    this.highlightPrev(e);
                    break;

                case 'ArrowDown':
                    this.highlightNext(e);
                    break;

                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    this.handleEnterKey(e);
                    break;


                case 'Escape':
                case 'Tab':
                    this.handleEscapeKey();
                    break;
            }
        }).on('keyup.splitbutton', (e) => {
            if (e.key === ' ') {
                e.preventDefault(); // Keep menu open in Firefox #7614
            }
        });

        PrimeFaces.bindButtonInlineAjaxStatus(this, this.button, (widget, settings) => {
            // Checks whether the ID of the button, or one if its menu items equals the source ID from the provided settings.
            var sourceId = PrimeFaces.ajax.Utils.getSourceId(settings);
            if (sourceId === null) {
                return false;
            }
            if (this.id === sourceId) {
                return true;
            }
            return this.menuitems.find('[id="' + sourceId + '"]').length > 0;
        });

        if(this.cfg.filter) {
            this.setupFilterMatcher();
            this.filterInput = this.menu.find('> div.ui-splitbuttonmenu-filter-container > input.ui-splitbuttonmenu-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.bindFilterEvents();
        }
    }

    /**
     * Sets up all panel event listeners
     */
    private bindPanelEvents(): void {
        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.menu, null,
            (_, eventTarget) => {
                if (!(this.menu.is(eventTarget) || this.menu.has(eventTarget[0] ?? "").length > 0)) {
                    this.button.removeClass('ui-state-focus ui-state-hover');
                    this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', this.menu, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.hide();
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.hide();
            }
        });
    }

    /**
     * Unbind all panel event listeners
     */
    private unbindPanelEvents(): void {
        if (this.hideOverlayHandler) {
            this.hideOverlayHandler.unbind();
        }

        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }

        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    }

    /**
     * Sets up the event listeners for filtering the available buttons actions via a search field.
     */
    private bindFilterEvents(): void {
        const $this = this;

        this.filterInput.on('keyup.ui-splitbutton', function(e) {
            if (PrimeFaces.utils.ignoreFilterKey(e)) {
                return;
            }
            var metaKey = e.metaKey||e.ctrlKey;
            if(!metaKey) {
                const value = $(this).val();
                $this.filter(typeof value === "string" ? value : "");
            }
        })
        .on('keydown.ui-splitbutton',(e) => {
            switch(e.key) {
                case 'ArrowUp':
                    this.highlightPrev(e);
                break;

                case 'ArrowDown':
                    this.highlightNext(e);
                break;

                case 'Enter':
                    this.handleEnterKey(e);
                break;

                case ' ': {
                    const target = $(e.target);

                    if(target.is('input') && target.hasClass('ui-splitbuttonmenu-filter')) {
                        return;
                    }

                    this.handleEnterKey(e);
                }
                break;

                case 'Escape':
                case 'Tab':
                    this.handleEscapeKey();
                break;

                default:
                break;
            }
        }).on('paste.ui-splitbutton', () => {
            PrimeFaces.queueTask(() => {
                const value = this.filterInput.val();
                this.filter(typeof value === "string" ? value : "");
            });
    	});
    }

    /**
     * Highlights the next button action, usually when the user navigates via the keyboard arrows.
     * @param event Keyboard arrow event that caused the next item to be highlighted.
     */
    private highlightNext(event: JQuery.TriggeredEvent): void {
        const highlightedItem = this.menuitems.filter('.ui-state-hover');
        const nextItems = highlightedItem.length
            ? highlightedItem.nextAll(':not(.ui-divider, .ui-widget-header):visible')
            : this.menuitems.filter(':visible').eq(0);

        if(nextItems.length) {
            highlightedItem.removeClass('ui-state-hover');
            nextItems.eq(0).addClass('ui-state-hover');
        }

        event.preventDefault();
    }

    /**
     * Highlights the previous button action, usually when the user navigates via the keyboard arrows.
     * @param event Keyboard arrow event that caused the previous item to be highlighted.
     */
    private highlightPrev(event: JQuery.TriggeredEvent): void {
        const highlightedItem = this.menuitems.filter('.ui-state-hover');
        const prevItems = highlightedItem.length 
            ? highlightedItem.prevAll(':not(.ui-divider, .ui-widget-header):visible')
            : null;

        if(prevItems && prevItems.length) {
            highlightedItem.removeClass('ui-state-hover');
            prevItems.eq(0).addClass('ui-state-hover');
        }

        event.preventDefault();
    }

    /**
     * Callback that is invoked when the enter key is pressed. When overlay panel with the additional buttons actions is
     * shown, activates the selected buttons action. Otherwise, opens the overlay panel.
     * @param event Keyboard event of the enter press.
     */
    private handleEnterKey(event: JQuery.TriggeredEvent): void {
        if(this.menu.is(':visible')) {
            const link = this.menuitems.filter('.ui-state-hover').children('a');
            link.trigger('click');

            const href = link.attr('href');
            if(href && href !== '#') {
                window.location.href = href;
            }
        }
        else {
            this.show();
        }

        event.preventDefault();
    }

    /**
     * Callback that is invoked when the escape key is pressed while the overlay panel with the additional buttons
     * actions is shown. Hides that overlay panel.
     */
    private handleEscapeKey(): void {
        this.hide();
    }

    /**
     * Creates the filter functions for filtering the button actions.
     */
    private setupFilterMatcher(): void {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            startsWith: (value, filter) => this.startsWithFilter(value, filter),
            contains: (value, filter) => this.containsFilter(value, filter),
            endsWith: (value, filter) => this.endsWithFilter(value, filter),
            custom: this.cfg.filterFunction,
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    }

    /**
     * A filter function that takes a value and a search and returns true if the value starts with the search term.
     * @param value Value to be filtered
     * @param filter Filter or search term to apply.
     * @return `true` if the given value starts with the search term, or `false` otherwise.
     */
    startsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter) === 0;
    }

    /**
     * A filter function that takes a value and a search and returns true if the value contains the search term.
     * @param value Value to be filtered
     * @param filter Filter or search term to apply.
     * @return `true` if the given value contains the search term, or `false` otherwise.
     */
    containsFilter(value: string, filter: string): boolean {
        return value.indexOf(filter) !== -1;
    }

    /**
     * A filter function that takes a value and a search and returns true if the value ends with the search term.
     * @param value Value to be filtered
     * @param filter Filter or search term to apply.
     * @return `true` if the given value ends with the search term, or `false` otherwise.
     */
    endsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    }

    /**
     * Filters the overlay panel with the additional buttons actions, leaving only the buttons that match the given
     * search term.
     * @param value Search term for filtering.
     */
    filter(value: string): void {
        const normalize = this.cfg.filterNormalize ?? false;
        const filterValue = PrimeFaces.toSearchable(PrimeFaces.trim(value), true, normalize);

        if(filterValue === '') {
            this.menuitems.filter(':hidden').show();
            this.menuitemContainer.children('.ui-widget-header').show();
            this.menuitemContainer.children('.ui-divider').show();
        }
        else {
            for(let i = 0; i < this.menuitems.length; i++) {
                const menuitem = this.menuitems.eq(i);
                const itemLabel = PrimeFaces.toSearchable(menuitem.find('.ui-menuitem-text').text(), true, normalize);

                /* for keyboard support */
                menuitem.removeClass('ui-state-hover');

                if (this.filterMatcher === undefined || this.filterMatcher(itemLabel, filterValue)) {
                    menuitem.show();
                }
                else {
                    menuitem.hide();
                }
            }

            //groups
            const groupHeaders = this.menuitemContainer.children('.ui-widget-header');
            for(var g = 0; g < groupHeaders.length; g++) {
                var group = groupHeaders.eq(g);

                if(g === (groupHeaders.length - 1)) {
                    if(group.nextAll('.ui-submenu-child').filter(':visible').length === 0) {
                        group.hide();
                    }
                    else {
                        group.show();
                    }
                }
                else if(group.nextUntil('.ui-widget-header').filter(':visible').length === 0) {
                    group.hide();
                }
                else {
                    group.show();
                }
            }

            const separators = this.menuitemContainer.children('.ui-divider');
            for(let s = 0; s < separators.length; s++) {
                const separator = separators.eq(s);
                if(separator.nextAll().filter(':visible').length === 0 || separator.prevAll().filter(':visible').length === 0) {
                    separator.hide();
                }
                else {
                    separator.show();
                }
            }
        }

        this.alignPanel();
    }

    /**
     * Shows the overlay panel with the additional buttons actions.
     */
    private show(): void {
        if(this.cfg.disabled) {
           return;
        }

        if (this.cfg.dynamic && !this.isDynamicLoaded) {
            this.loadDynamicMenu();
            return;
        }

        if (this.transition) {
            this.transition.show({
                onEnter: () => {
                    PrimeFaces.nextZindex(this.menu);
                    this.alignPanel();
                },
                onEntered: () => {
                    this.bindPanelEvents();

                    this.menuButton.attr('aria-expanded', "true");

                    if (this.cfg.filter && this.cfg.filterInputAutoFocus) {
                        this.filterInput.trigger('focus');
                    }
                    else {
                        this.menuButton.trigger('focus');
                    }
                }
            });
        }
    }

    /**
     * Loads the menu content dynamically via AJAX when dynamic mode is enabled.
     * @private
     */
    private loadDynamicMenu(): void {
        const $this = this;
        const options: PrimeFaces.ajax.Configuration = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [
                { name: this.id + '_dynamicload', value: true }
            ],
            onsuccess: function(responseXML: XMLDocument, status: string, xhr: JQueryXHR) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content: string) {
                        // The response contains the menu element
                        const tempContainer = $('<div>').html(content);
                        const menuElement = tempContainer.find($this.menuId);
                        
                        if (menuElement.length > 0) {
                            // Append menu to the splitbutton container if it doesn't exist
                            if ($this.menu.length === 0) {
                                $this.jq.append(menuElement);
                            } else {
                                // Replace existing menu
                                $this.menu.replaceWith(menuElement);
                            }
                            
                            $this.menu = menuElement;
                            $this.menuitemContainer = $this.menu.find('.ui-menu-list');
                            $this.menuitems = $this.menuitemContainer.children('.ui-menuitem:not(.ui-state-disabled)');
                            
                            // Re-register dynamic overlay
                            PrimeFaces.utils.registerDynamicOverlay($this, $this.menu, $this.id + '_menu');
                            $this.transition = PrimeFaces.utils.registerCSSTransition($this.menu, 'ui-connected-overlay');
                            
                            // Re-bind filter events if filter is enabled
                            if ($this.cfg.filter) {
                                $this.setupFilterMatcher();
                                $this.filterInput = $this.menu.find('> div.ui-splitbuttonmenu-filter-container > input.ui-splitbuttonmenu-filter');
                                PrimeFaces.skinInput($this.filterInput);
                                $this.bindFilterEvents();
                            }
                            
                            // Re-bind menu item events
                            $this.menuitems.on('mouseover.splitbutton', function() {
                                const menuitem = $(this);
                                const menuitemLink = menuitem.children('.ui-menuitem-link');
                                if(!menuitemLink.hasClass('ui-state-disabled')) {
                                    menuitem.addClass('ui-state-hover');
                                }
                            }).on('mouseout.splitbutton', function() {
                                $(this).removeClass('ui-state-hover');
                            }).on('click.splitbutton', () => {
                                $this.hide();
                            });
                            
                            $this.isDynamicLoaded = true;
                            $this.cfg.disabled = false;
                            
                            // Now show the menu
                            $this.show();
                        }
                    }
                });
                return true;
            }
        };
        
        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Hides the overlay panel with the additional buttons actions.
     */
    private hide(): void {
        if (this.transition) {
            this.transition.hide({
                onExit: () => {
                    this.unbindPanelEvents();
                },
                onExited: () => {
                    this.menuButton.attr('aria-expanded', "false");
                    this.menuitems.filter('.ui-state-hover').removeClass('ui-state-hover');
                    this.menuButton.removeClass('ui-state-focus');
                }
            });
        }
    }

    /**
     * Align the overlay panel with the additional buttons actions.
     */
    alignPanel(): void {
        this.menu.css({ left:'', top:'', 'transform-origin': 'center top' });

        if(this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px',
            });
        }
        else {
            this.menu.position({
                my: 'left top',
                at: 'left bottom',
                of: this.jq,
                collision: 'flipfit',
                using: function(pos: { top: number; left: number }, directions: {horizontal: number; vertical: number;}) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                },
            });
        }
    }
}
