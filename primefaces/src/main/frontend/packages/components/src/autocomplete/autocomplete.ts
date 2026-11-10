/**
 * The configuration for the {@link AutoComplete} widget.
 */
export interface AutoCompleteCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether autocompletion search is initially active. */
    active: boolean;
    /** ID of the container to which the suggestion box is appended. */
    appendTo: string | null;
    /** Defines which position on the target element to align the positioned element against. */
    atPos: string;
    /** Highlights the first suggested item automatically. */
    autoHighlight: boolean;
    /** Selector specifies what content to identify for highlighting in search results. */
    highlightSelector: string;
    /**
     * Defines if auto selection of items that are equal to the typed input is enabled. If `true`, an item that is
     * equal to the typed input is selected.
     */
    autoSelection: boolean;
    /** When enabled autocomplete caches the searched result list. */
    cache: boolean;
    /** Timeout in milliseconds value for cached results. */
    cacheTimeout: number;
    /** Whether the itemtip is moved next to the panel when the panel has a scrollbar, set on init. */
    checkForScrollbar: boolean;
    /** The delay in milliseconds before an autocomplete search is triggered. */
    delay: number;
    /** Specifies the behavior of the dropdown button. */
    dropdownMode: PrimeFacesTypes.widget.AutoComplete.DropdownMode;
    /**
     * Defines if dynamic loading is enabled for the element's panel. If the value is `true`, the overlay is not
     * rendered on page load to improve performance.
     */
    dynamic: boolean;
    /** Whether the text of the suggestion items is escaped for HTML. */
    escape: boolean;
    /** Text to display when there is no data to display. */
    emptyMessage: string;
    /** Whether a footer facet is present. */
    hasFooter: boolean;
    /** Whether one of the available suggestion items is forced to be preselected. */
    forceSelection: boolean;
    /** Whether suggestion items are grouped. */
    grouping: boolean;
    /** Whether a tooltip is shown for the suggestion items. */
    itemtip: boolean;
    /** Position of item corner relative to item tip. */
    itemtipAtPosition: string;
    /** Position of itemtip corner relative to item. */
    itemtipMyPosition: string;
    /** Minimum length before an autocomplete search is triggered. */
    minLength: number;
    /** When `true`, enables multiple selection. */
    multiple: boolean;
    /** Defines which position on the element being positioned to align with the target element. */
    myPos: string;
    /** Client side callback to invoke when value changes, the original `onchange` handler of the input. */
    onChange: PrimeFacesTypes.widget.AutoComplete.OnChangeCallback | null;
    /** Whether the hidden input with the selected value exists (POJO support), set on init. */
    pojo: boolean;
    /** Event to initiate the the autocomplete search. */
    queryEvent: PrimeFacesTypes.widget.AutoComplete.QueryEvent;
    /** Specifies query mode, whether autocomplete contacts the server. */
    queryMode: PrimeFacesTypes.widget.AutoComplete.QueryMode;
    /** Limits the number of simultaneously selected items. Default is unlimited. */
    selectLimit: number;
    /** Height of the container with the suggestion items. */
    scrollHeight: number;
    /** Whether to display the emptyMessage or not. */
    showEmptyMessage: boolean;
    /** Ensures uniqueness of the selected items. */
    unique: boolean;
    /**
     * REST endpoint for fetching autocomplete suggestions. Takes precedence over the bean command specified via
     * `completeMethod` on the component.
     */
    completeEndpoint: string;
    /** The text shown in the panel when the number of suggestions is greater than `maxResults`. */
    moreText: string;
}

/**
 * __PrimeFaces AutoComplete Widget__
 *
 * AutoComplete provides live suggestions while the user is entering text
 */
export class AutoComplete<Cfg extends AutoCompleteCfg = AutoCompleteCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Whether the autocomplete is active. */
    active: boolean = true;
    /** The cache for the results of an autocomplete search. */
    cache?: Record<string, string>;
    /** The set-interval timer ID for the cache timeout. */
    cacheTimeout?: number;
    /** Whether the click event is fired on the selected items when a `blur` occurs. */
    checkMatchedItem?: boolean;
    /** Column span count for the options in the overlay panel with the available completion items. */
    colspan?: number;
    /** Current option group when creating the options in the overlay  with the available completion items. */
    currentGroup?: string;
    /** Current value in the input field where the user can search for completion items. */
    currentInputValue: string = '';
    /** Currently selected items, when `forceSelection` is enabled. */
    currentItems?: (string | undefined)[];
    /** Text currently entered in the input field. */
    currentText?: string;
    /** The DOM element for the container with the dropdown suggestions. */
    dropdown: JQuery = $();
    /** Unbind callback for the hide overlay handler. */
    hideOverlayHandler?: PrimeFacesTypes.Unbindable;
    /** The DOM element for the input element. */
    input: JQuery<HTMLInputElement> = $();
    /** If dynamic loading is enabled, whether the content was loaded already. */
    isDynamicLoaded: boolean = false;
    /** Whether the tab key is currently pressed. */
    isTabPressed: boolean = false;
    /** The DOM element for the hidden input with the selected value. */
    hinput: JQuery = $();
    /**
     * When multiple mode is enabled that allows multiple items to be selected: The DOM element of the container with
     * the input element used to enter text and search for an item to select.
     */
    inputContainer: JQuery = $();
    /** Whether to use a drop down menu when searching for completion options. */
    isSearchWithDropdown?: boolean;
    /** The DOM elements for the suggestion items. */
    items?: JQuery;
    /** The DOM element for the tooltip of a suggestion item. */
    itemtip: JQuery = $();
    /** Whether an item was clicked. */
    itemClick?: boolean;
    /** The DOM element for the container with the suggestion items. */
    itemContainer: JQuery = $();
    /** Whether an item was selected via the enter key. */
    itemSelectedWithEnter?: boolean;
    /** The DOM element for the container with multiple selection items. */
    multiItemContainer: JQuery = $();
    /** The DOM element for the overlay panel with the suggestion items. */
    panel: JQuery = $();
    /** The client ID of the overlay panel with the suggestion items. */
    panelId: string = '';
    /** Placeholder shown in the input field when no text is entered. */
    placeholder: string | undefined = undefined;
    /** Whether to suppress the change event when the input's value changes. */
    preventInputChangeEvent?: boolean;
    /** Text previously entered in the input field. */
    previousText?: string;
    /** Whether an AJAX request for the autocompletion items is currently in progress. */
    querying?: boolean;
    /** Unbind callback for the resize handler. */
    resizeHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback for the scroll handler. */
    scrollHandler?: PrimeFacesTypes.Unbindable;
    /** Tracking number to make sure search requests match up in query mode */
    requestId?: number;
    /** The DOM element for the autocomplete status ARIA element. */
    status: JQuery = $();
    /** Whether key input events should be ignored currently. */
    suppressInput: boolean = true;
    /** Timeout ID for the timer used to delay the autocomplete search. */
    timeout?: number | null;
    /** Whether a touch is made on the dropdown button. */
    touchToDropdownButton: boolean = false;
    /** Handler for CSS transitions used by this widget. */
    transition?: PrimeFacesTypes.CssTransitionHandler | null;
    /** The starting HTML with the wrapper element of the suggestions box. */
    wrapperStartTag?: string;
    /** The finishing HTML with the wrapper element of the suggestions box. */
    wrapperEndTag?: string;
    /** Hint text for screen readers to provide information about the search results. */
    resultsMessage?: string;
    /** Text to display when there is no data to display. */
    emptyMessage?: string;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.panelId = this.jqId + '_panel';
        this.input = $(this.jqId + '_input') as JQuery<HTMLInputElement>;
        this.hinput = $(this.jqId + '_hinput');
        this.panel = this.jq.children(this.panelId);
        this.dropdown = this.jq.children('.p-button');
        this.active = true;
        this.cfg.pojo = this.hinput.length == 1;
        this.cfg.minLength = this.cfg.minLength != undefined ? this.cfg.minLength : 1;
        this.cfg.cache = this.cfg.cache || false;
        this.cfg.dropdownMode = this.cfg.dropdownMode || 'blank';
        this.cfg.autoHighlight = this.cfg.autoHighlight === undefined ? true : this.cfg.autoHighlight;
        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this as unknown as InstanceType<typeof PrimeFaces.widget.DynamicOverlayWidget>, this.jq, this.panel);
        this.cfg.myPos = this.cfg.myPos || 'left top';
        this.cfg.atPos = this.cfg.atPos || 'left bottom';
        this.cfg.highlightSelector = this.cfg.highlightSelector || 'span';
        this.cfg.active = this.cfg.active === undefined ? true : this.cfg.active;
        this.cfg.autoSelection = this.cfg.autoSelection === undefined ? true : this.cfg.autoSelection;
        this.cfg.escape = this.cfg.escape === undefined ? true : this.cfg.escape;
        this.cfg.showEmptyMessage = this.cfg.showEmptyMessage === undefined ? true : this.cfg.showEmptyMessage;
        this.suppressInput = true;
        this.touchToDropdownButton = false;
        this.isTabPressed = false;
        this.isDynamicLoaded = false;
        this.currentInputValue = '';
        this.configureLocale();

        if (this.cfg.cache) {
            this.initCache();
        }

        if (this.cfg.queryMode !== 'server') {
            this.fetchItems();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        this.placeholder = this.input.attr('placeholder');

        // visuals and float label handling
        PrimeFaces.skinInput(this.input);

        if (this.cfg.multiple) {
            this.setupMultipleMode();

            this.multiItemContainer.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

            if ((this.cfg.selectLimit as number) >= 0 && this.multiItemContainer.children('li.p-autocomplete-token').length === this.cfg.selectLimit) {
                this.input.hide();
                this.disableDropdown();
            }
        }
        else {
            this.input.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
            this.dropdown.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
        }

        //core events
        this.bindStaticEvents();

        //client Behaviors
        if (this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors as unknown as Parameters<typeof PrimeFaces.attachBehaviors>[1]);
        }

        //force selection
        if (this.cfg.forceSelection) {
            this.setupForceSelection();
        }

        //Panel management
        if (this.panel.length) {
            this.appendPanel();
            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'p-connected-overlay');
        }

        //itemtip
        if (this.cfg.itemtip) {
            this.itemtip = $('<div id="' + this.id + '_itemtip" class="p-autocomplete-itemtip p-state-highlight p-widget"></div>').appendTo(document.body);
            this.cfg.itemtipMyPosition = this.cfg.itemtipMyPosition || 'left top';
            this.cfg.itemtipAtPosition = this.cfg.itemtipAtPosition || 'right bottom';
            this.cfg.checkForScrollbar = (this.cfg.itemtipAtPosition.indexOf('right') !== -1);
            const removeItemtip = () => {
                this.itemtip.remove();
            };
            this.addRefreshListener(removeItemtip);
            this.addDestroyListener(removeItemtip);
        }

        //aria
        this.input.attr('aria-autocomplete', 'list');
        this.jq.append('<span role="status" aria-live="polite" class="p-autocomplete-status p-hidden-accessible"></span>');
        this.status = this.jq.children('.p-autocomplete-status');
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        if (this.cfg.dynamic && this.isDynamicLoaded && this.panel.length) {
            this.unbindPanelEvents();
            this.panel.remove();
        }

        super.refresh(cfg);
    }

    /**
     * Localizes the ARIA accessibility labels for the autocomplete.
     */
    private configureLocale(): void {
        this.emptyMessage = this.cfg.emptyMessage || this.getLabel('emptySearchMessage');
        this.resultsMessage = this.getLabel('searchMessage');
        if (this.dropdown) {
            this.dropdown.attr('aria-label', this.getLabel('choose') as string);
        }
    }

    /**
     * Appends the overlay panel to the DOM.
     */
    private appendPanel(): void {
        PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
    }

    /**
     * Initializes the cache that stores the retrieved suggestions for a search term.
     */
    private initCache(): void {
        this.cache = {};

        this.cacheTimeout = setInterval(() => {
            this.clearCache();
        }, this.cfg.cacheTimeout);
        const stopCacheTimeout = () => {
            clearInterval(this.cacheTimeout);
        };
        this.addRefreshListener(stopCacheTimeout);
        this.addDestroyListener(stopCacheTimeout);
    }

    /**
     * Clears the cache with the results of an autocomplete search.
     */
    private clearCache(): void {
        this.cache = {};
    }

    /**
     * Binds events for multiple selection mode.
     */
    private setupMultipleMode(): void {
        this.multiItemContainer = this.jq.children('ul');
        this.inputContainer = this.multiItemContainer.children('.p-autocomplete-input-token');

        this.multiItemContainer.on("mouseenter", (e) => {
            $(e.currentTarget).addClass('p-state-hover');
        }).on("mouseleave", (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        }).on("click", () => {
            this.input.trigger('focus');
        });

        //delegate events to container
        this.input.on("focus", () => {
            this.multiItemContainer.addClass('p-state-focus');
        }).on("blur", () => {
            this.multiItemContainer.removeClass('p-state-focus');
        });

        const closeSelector = '> li.p-autocomplete-token > .p-autocomplete-token-icon';
        this.multiItemContainer.off('click', closeSelector).on('click', closeSelector, null, (event) => {
            if (this.multiItemContainer.children('li.p-autocomplete-token').length === this.cfg.selectLimit) {
                this.input.css('display', 'inline');
                this.enableDropdown();
            }
            this.removeItem($(event.currentTarget).parent());
        });
    }

    /**
     * Sets up all global event listeners for the overlay.
     */
    private bindStaticEvents(): void {
        this.bindKeyEvents();

        this.bindDropdownEvents();

        if (PrimeFaces.env.browser.mobile) {
            this.dropdown.on('touchstart', () => {
                this.touchToDropdownButton = true;
            });
        }
    }

    /**
     * Sets up all panel event listeners
     */
    private bindPanelEvents(): void {
        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            () => { return this.itemtip; },
            (_e, eventTarget) => {
                if (!(this.panel.is(eventTarget) || this.panel.has(eventTarget[0] ?? "").length > 0)) {
                    this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, () => {
            this.handleViewportChange();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, () => {
            this.handleViewportChange();
        });
    }

    /**
     * Fired when the browser viewport is resized or scrolled.  In Mobile environment we don't want to hider the overlay
     * we want to re-align it.  This is because on some mobile browser the popup may force the browser to trigger a
     * resize immediately and close the overlay. See GitHub #7075.
     */
    private handleViewportChange(): void {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.alignPanel();
        } else {
            this.hide();
        }
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
     * Sets up all event listeners for the dropdown menu.
     */
    private bindDropdownEvents(): void {
        PrimeFaces.skinButton(this.dropdown);

        this.dropdown.on("mouseup", () => {
            if (this.active) {
                this.searchWithDropdown();
                PrimeFaces.queueTask(() => { this.input.trigger('focus'); }, 1);
            }
        }).on("keyup", (e) => {
            if (PrimeFaces.utils.isActionKey(e)) {
                this.searchWithDropdown();
                this.input.trigger('focus');
                e.preventDefault();
                e.stopPropagation();
            }
        });
    }

    /**
     * Disables the dropdown menu.
     */
    private disableDropdown(): void {
        if (this.dropdown.length) {
            this.dropdown.off().prop('disabled', true).addClass('p-state-disabled');
        }
    }

    /**
     * Enables the dropdown menu.
     */
    private enableDropdown(): void {
        if (this.dropdown.length && this.dropdown.prop('disabled')) {
            this.bindDropdownEvents();
            this.dropdown.prop('disabled', false).removeClass('p-state-disabled');
        }
    }

    /**
     * Sets up all keyboard related event listeners.
     */
    private bindKeyEvents(): void {
        // #14152 with CSP the change handler is registered by PrimeFaces.csp.register, which runs AFTER the widget
        // init on AJAX updates, so if it is not found yet, try again once CSP has registered it (see #13269)
        this.cfg.onChange = null;
        if (!this.captureOriginalOnchange()) {
            PrimeFaces.queueTask(() => {
                if (this.input[0] && this.input[0].isConnected) {
                    this.captureOriginalOnchange();
                }
            }, 1);
        }

        if (this.cfg.queryEvent !== 'enter') {
            this.input.on('input propertychange', (e) => {
                this.processKeyEvent(e);
            });
        }

        this.input.on('keyup.autoComplete', (e) => {
            const key = e.key;

            if (this.cfg.queryEvent === 'enter' && (key === 'Enter')) {
                if (this.itemSelectedWithEnter)
                    this.itemSelectedWithEnter = false;
                else
                    this.search(this.input.val() as string);
            }

            if (this.panel.is(':visible')) {
                if (key === 'Escape') {
                    this.hide();
                }
                else if (key === 'ArrowUp' || key === 'ArrowDown') {
                    const highlightedItem = this.items!.filter('.p-state-highlight');
                    if (highlightedItem.length) {
                        this.displayAriaStatus(highlightedItem.data('item-label'));
                        this.changeAriaValue(highlightedItem[0]);
                    }
                }
            }

            this.checkMatchedItem = true;
            this.isTabPressed = false;

        }).on('keydown.autoComplete', (e) => {
            this.suppressInput = false;
            if (this.panel.is(':visible')) {
                const highlightedItem = this.items!.filter('.p-state-highlight');

                switch (e.key) {
                    case 'ArrowUp':
                        const prev = highlightedItem.length == 0 ? this.items!.eq(0) : highlightedItem.prevAll('.p-autocomplete-item:first');

                        if (prev.length == 1) {
                            this.highlightItem(highlightedItem, false);
                            this.highlightItem(prev, true);

                            if (this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(this.panel, prev);
                            }

                            if (this.cfg.itemtip) {
                                this.showItemtip(prev);
                            }
                        }

                        e.preventDefault();
                        break;

                    case 'ArrowDown':
                        const next = highlightedItem.length == 0 ? this.items!.eq(0) : highlightedItem.nextAll('.p-autocomplete-item:first');

                        if (next.length == 1) {
                            this.highlightItem(highlightedItem, false);
                            this.highlightItem(next, true);

                            if (this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(this.panel, next);
                            }

                            if (this.cfg.itemtip) {
                                this.showItemtip(next);
                            }
                        }

                        e.preventDefault();
                        break;

                    case 'Enter':
                    case 'NumpadEnter':
                        if (this.timeout) {
                            this.deleteTimeout();
                        }

                        if (highlightedItem.length > 0) {
                            this.preventInputChangeEvent = true;
                            highlightedItem.trigger("click");
                            this.itemSelectedWithEnter = true;
                        } else {
                            this.hide();
                        }

                        e.preventDefault();
                        e.stopPropagation();

                        break;

                    case 'Alt':
                        break;

                    case 'Tab':
                        if (highlightedItem.length && this.cfg.autoSelection) {
                            highlightedItem.trigger('click');
                        } else {
                            this.hide();
                            if (this.timeout) {
                                this.deleteTimeout();
                            }
                        }
                        this.isTabPressed = true;
                        break;
                }
            }
            else {
                switch (e.key) {
                    case 'Tab':
                        if (this.timeout) {
                            this.deleteTimeout();
                        }
                        this.isTabPressed = true;
                        break;

                    case 'Enter':
                    case 'NumpadEnter':
                        const itemValue = $(e.currentTarget).val() as string;
                        let valid: boolean | undefined = true;
                        if (this.cfg.queryEvent === 'enter' || ((this.timeout as number) > 0) || this.querying) {
                            e.preventDefault();
                        }

                        if (this.cfg.queryEvent !== 'enter') {
                            valid = this.isValid(itemValue, true);
                            if (!this.cfg.forceSelection) {
                                valid = true;
                            }
                        }

                        if (this.cfg.multiple && itemValue) {
                            if (valid) {
                                this.addItem(itemValue);
                            }

                            e.preventDefault();
                            e.stopPropagation();
                        }
                        break;

                    case 'Backspace':
                        if (this.cfg.multiple && !(this.input.val() as string).length) {

                            if (e.metaKey || e.ctrlKey || e.shiftKey) {
                                this.removeAllItems();
                            } else {
                                this.removeItem($(e.currentTarget).parent().prev());
                            }

                            e.preventDefault();
                        }
                        break;
                }
            }

        }).on('paste.autoComplete', () => {
            this.suppressInput = false;
            this.checkMatchedItem = true;
        }).on('change.autoComplete', (e) => {
            const value = e.currentTarget.value,
                valid = this.isValid(value, false);

            if (this.cfg.forceSelection && this.currentInputValue === '' && !valid) {
                this.preventInputChangeEvent = true;
            }

            if (this.cfg.onChange && !this.preventInputChangeEvent) {
                this.cfg.onChange.call(e.currentTarget);
            }

            this.currentInputValue = this.cfg.forceSelection && !valid ? '' : value;
            this.preventInputChangeEvent = false;
        });
    }

    /**
     * Takes over the user's change handler of the input, so the widget decides when to call it (e.g. not when the
     * input is blurred by clicking a suggestion item).
     * GitHub #6711 use DOM if non-CSP and JQ event if CSP.
     * @returns `true` if a change handler was found.
     */
    private captureOriginalOnchange(): boolean {
        // skip the widget's own handler
        const originalOnchange = PrimeFaces.utils.getOriginalEventHandler(this.input, 'change', 'autoComplete');
        if (originalOnchange) {
            this.input.prop('onchange', null).off('change', originalOnchange);
        }

        this.cfg.onChange = (originalOnchange || null) as PrimeFacesTypes.widget.AutoComplete.OnChangeCallback | null;
        return !!originalOnchange;
    }

    /**
     * Sets up all event listeners for mouse and click events.
     */
    private bindDynamicEvents(): void {
        //visuals and click handler for items
        this.items!.off('click.autocomplete mousedown.autocomplete mouseover.autocomplete')
            .on('mouseover.autocomplete', (e) => {
                const item = $(e.currentTarget);

                if (!item.hasClass('p-state-highlight')) {
                    this.items!.filter('.p-state-highlight').removeClass('p-state-highlight').attr('aria-selected', 'false');
                    this.highlightItem(item, true);

                    if (this.cfg.itemtip) {
                        this.showItemtip(item);
                    }
                }
            })
            .on('click.autocomplete', (event) => {
                const item = $(event.currentTarget),
                    isMoreText = item.hasClass('p-autocomplete-moretext');

                if (isMoreText) {
                    this.input.trigger('focus');
                    this.invokeMoreTextBehavior();
                }
                else {
                    this.addItem(item);
                }

                // #11570 allow CTRL+Click to select multiple
                if (!this.cfg.multiple || !PrimeFaces.utils.isMetaKey(event)) {
                    this.hide();
                }
            })
            .on('mousedown.autocomplete', () => {
                this.preventInputChangeEvent = true;
                this.checkMatchedItem = false;
            });

        if (this.cfg.showEmptyMessage) {
            this.panel.off('click.emptyMessage').on('click.emptyMessage', (e) => {
                const panel = e.currentTarget;
                if (!panel.children) {
                    return;
                }
                const item = $(panel.children[0] as Element),
                    isEmptyMessage = item.hasClass('p-autocomplete-empty-message');

                if (isEmptyMessage) {
                    this.invokeEmptyMessageBehavior();
                }
            });
        }

        if (PrimeFaces.env.browser.mobile) {
            this.items!.off('touchstart.autocomplete').on('touchstart.autocomplete', () => {
                if (!this.touchToDropdownButton) {
                    this.itemClick = true;
                }
            });
        }
    }

    /**
     * Callback for when a key event occurred.
     * @param e Key event that occurred.
     */
    private processKeyEvent(e: JQuery.TriggeredEvent): void {
        if (this.suppressInput) {
            e.preventDefault();
            return;
        }

        // for touch event on mobile
        if (PrimeFaces.env.browser.mobile) {
            this.touchToDropdownButton = false;
            if (this.itemClick) {
                this.itemClick = false;
                return;
            }
        }

        const value = this.input.val() as string;

        // #15304 with forceSelection the hidden input only holds validated values, as a queued request
        // may serialize the form before the typed text has been validated on blur
        if (this.cfg.pojo && !this.cfg.multiple && (!this.cfg.forceSelection || !value.length)) {
            this.hinput.val(value);
        }

        if (!value.length) {
            this.hide();
            this.deleteTimeout();
        }

        if (value.length >= (this.cfg.minLength as number)) {
            if (this.timeout) {
                this.deleteTimeout();
            }
            this.timeout = PrimeFaces.queueTask(() => {
                this.timeout = null;
                this.search(value);
            }, this.cfg.delay);
        }
        else if (value.length === 0) {
            if (this.timeout) {
                this.deleteTimeout();
            }
            this.fireClearEvent();
        }
    }

    /**
     * Shows the tooltip for the given suggestion item.
     * @param item Item with a tooltip.
     */
    private showItemtip(item: JQuery): void {
        if (item.hasClass('p-autocomplete-moretext')) {
            this.itemtip.hide();
        }
        else {
            let content: JQuery;
            if (item.is('li')) {
                content = item.next('.p-autocomplete-itemtip-content');
            } else if (item.children('td:last').hasClass('p-autocomplete-itemtip-content')) {
                content = item.children('td:last');
            } else {
                this.itemtip.hide();
                return;
            }

            this.itemtip.html(content.html())
                .css({
                    'left': '',
                    'top': '',
                    'z-index': PrimeFaces.nextZindex(),
                    'width': content.outerWidth() + 'px'
                });
            PrimeFaces.utils.position(this.itemtip, {
                my: this.cfg.itemtipMyPosition
                , at: this.cfg.itemtipAtPosition
                , of: item
            });

            //scrollbar offset
            if (this.cfg.checkForScrollbar) {
                if ((this.panel.innerHeight() as number) < (this.panel.children('.p-autocomplete-items').outerHeight(true) as number)) {
                    const panelOffset = this.panel.offset()!;
                    this.itemtip.css('left', (panelOffset.left + (this.panel.outerWidth() as number)) + 'px');
                }
            }

            this.itemtip.show();
        }
    }

    /**
     * Performs the search for the available suggestion items.
     * @param query Keyword for the search.
     */
    private showSuggestions(query: string): void {
        this.items = this.panel.find('.p-autocomplete-item');
        this.items.attr('role', 'option');
        this.panel.children('.p-autocomplete-items').attr('aria-label', this.getAriaLabel('listLabel'));

        if (this.cfg.grouping) {
            this.groupItems();
        }

        this.bindDynamicEvents();

        const hidden = this.panel.is(':hidden');

        if (hidden) {
            this.show();
        }
        else {
            this.alignPanel();
        }

        // #8717 always clear list before trying to fill it
        if (this.cfg.forceSelection) {
            this.currentItems = [];
        }

        if (this.items.length > 0) {
            const firstItem = this.items.eq(0);

            //highlight first item
            if (this.cfg.autoHighlight && firstItem.length) {
                this.highlightItem(firstItem, true);
                this.changeAriaValue(firstItem[0]);
            }

            //highlight query string
            if (query.length > 0) {
                // replace with a global regex replaces all matches like replaceAll
                const cleanedQuery = query.trim().replace(/(\s+)/g, ' ');
                if (cleanedQuery.length > 0) {
                    const queryParts = cleanedQuery.split(' ');
                    for (let i = 0; i < queryParts.length; i++) {
                        queryParts[i] = PrimeFaces.escapeRegExp(queryParts[i]!);
                    }
                    const re = new RegExp('(' + queryParts.join('|') + ')', 'gi');
                    const isCustomContent = this.panel.children().is('table');
                    const queryResults = isCustomContent ? this.panel.children().find(this.cfg.highlightSelector as string) : this.items;
                    queryResults.filter(':not(.p-autocomplete-moretext)').each((_i, el) => {
                        const item = $(el);
                        const escape = this.cfg.escape;
                        let text = escape ? item.html() : item.text();
                        const escaped_re = escape ? /${PrimeFaces.escapeHTML(text)}/g : /${text}/g;
                        text = text.replace(escaped_re, '$&');
                        item.html(text.replace(re, '<span class="p-autocomplete-query">$&</span>'));
                    });
                }
            }

            if (this.cfg.forceSelection) {
                this.items.each((_i, item) => {
                    this.currentItems!.push($(item).attr('data-item-label'));
                });
            }

            //show itemtip if defined
            if (this.cfg.autoHighlight && this.cfg.itemtip && firstItem.length === 1) {
                this.showItemtip(firstItem);
            }

            this.displayAriaStatus(this.resultsMessage!.replace('{0}', String(this.items.length)));
        }
        else {
            if (this.cfg.showEmptyMessage && this.emptyMessage) {
                const emptyText = '<div class="p-autocomplete-empty-message p-widget">' + PrimeFaces.escapeHTML(this.emptyMessage) + '</div>';
                this.panel.prepend(emptyText);
            }
            else if (!this.cfg.hasFooter) {
                this.panel.hide();
            }

            this.input.removeAttr('aria-activedescendant');
            this.displayAriaStatus(this.emptyMessage as string);
        }
    }

    /**
     * Performs a search the same ways as if the user had opened the dropdown menu. Depending on the configured
     * `dropdownMode`, performs the search either with an empty string or with the current value.
     */
    searchWithDropdown(): void {
        this.isSearchWithDropdown = true;

        if (this.cfg.dropdownMode === 'current')
            this.search(this.input.val() as string);
        else
            this.search('');
    }

    /**
     * Initiates a search with given value, that is, look for matching options and present the options that were found
     * to the user.
     * @param query Keyword for the search.
     */
    search(query: string): void {
        //allow empty string but not undefined or null
        if (!this.cfg.active || query === undefined || query === null) {
            return;
        }

        if (this.cfg.cache && !(this.cfg.dynamic && !this.isDynamicLoaded)) {
            if (this.cache![query]) {
                this.panel.html(this.cache![query]);
                this.showSuggestions(query);
                return;
            }
            else if (this.cfg.queryMode === 'client') {
                if (this.isSearchWithDropdown) {
                    let suggestions = this.wrapperStartTag as string;
                    const re = new RegExp(this.wrapperStartTag + '|' + this.wrapperEndTag, 'g');
                    // Object.keys instead of Object.entries (ES2017, not in lib), same keys in the same order
                    Object.keys(this.cache!).map((key) => {
                        suggestions += this.cache![key]!.replace(re, '');
                    });
                    suggestions += this.wrapperEndTag;

                    this.panel.html(suggestions);

                    this.isSearchWithDropdown = false;
                }
                else {
                    this.panel.empty();
                }

                this.showSuggestions(query);
                return;
            }
        }

        if (!this.active) {
            return;
        }

        this.setQuerying(true);

        if (this.cfg.itemtip) {
            this.itemtip.hide();
        }

        let options: PrimeFacesTypes.ajax.Configuration | undefined;

        if (!this.cfg.completeEndpoint) {
            this.requestId = (this.requestId as number) + 1 || 1;
            const currentRequestId = this.requestId;
            // #15078 capture the dynamic-load decision now, since `isDynamicLoaded` is mutable and can
            // flip (via hide() or another request's oncomplete) before this request's response is handled
            const wasDynamicLoadRequested = this.cfg.dynamic && !this.isDynamicLoaded;
            options = {
                source: this.getId(),
                process: this.getId(),
                update: this.getId(),
                formId: this.getParentFormId(),
                onsuccess: (responseXML, status, xhr) => {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            // #8725 ensure its the same request when slow server
                            if (this.requestId !== currentRequestId) {
                                return;
                            }
                            if (wasDynamicLoadRequested) {
                                this.panel = $(content);
                                this.appendPanel();
                                this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'p-connected-overlay');
                                content = this.panel.get(0)!.innerHTML;
                            } else {
                                this.panel.html(content);
                            }

                            if (this.cfg.cache) {
                                if (this.cfg.queryMode !== 'server' && !wasDynamicLoadRequested && this.cache![query]) {
                                    this.panel.html(this.cache![query]);
                                } else {
                                    this.cache![query] = content;
                                }
                            }

                            this.showSuggestions(query);
                        }
                    });

                    return true;
                },
                oncomplete: () => {
                    this.setQuerying(false);
                    this.isDynamicLoaded = this.requestId === currentRequestId;
                }
            };

            options.params = [
                { name: this.id + '_query', value: query }
            ];

            if (this.cfg.queryMode === 'hybrid') {
                options.params.push({ name: this.id + '_clientCache', value: true });
            }

            if (wasDynamicLoadRequested) {
                options.params.push({ name: this.id + '_dynamicload', value: true });
            }
        }

        if (this.hasBehavior('query')) {
            this.callBehavior('query', options);
        }
        else if (this.cfg.completeEndpoint) {
            $.ajax({
                url: this.cfg.completeEndpoint,
                data: { query: query },
                dataType: 'json'
            }).done((suggestions: PrimeFacesTypes.widget.AutoComplete.CompleteEndpointResponse) => {
                let html = '<ul class="p-autocomplete-items p-autocomplete-list p-widget-content p-widget" role="listbox">';
                suggestions.suggestions.forEach((suggestion, index) => {
                    const labelEncoded = $("<div>").text(suggestion.label).html();
                    let itemValue = labelEncoded;
                    if (suggestion.value) {
                        itemValue = $("<div>").text(suggestion.value).html();
                    }
                    html += '<li id="' + this.id + '_item_' + index + '" class="p-autocomplete-item p-autocomplete-list-item" data-item-value="' + PrimeFaces.escapeHTML(itemValue, true) + '" data-item-label="' + PrimeFaces.escapeHTML(labelEncoded, true) + '" role="option">' + PrimeFaces.escapeHTML(labelEncoded, true) + '</li>';
                });
                if (suggestions.moreAvailable && this.cfg.moreText) {
                    const moreTextEncoded = PrimeFaces.escapeHTML($("<div>").text(this.cfg.moreText).html(), true);
                    html += '<li id="' + this.id + '_item_more' + '" class="p-autocomplete-item p-autocomplete-moretext" role="option" aria-label="' + moreTextEncoded + '" data-item-label="' + moreTextEncoded + '">' + moreTextEncoded + '</li>';
                }
                html += '</ul>';

                this.panel.html(html);

                this.showSuggestions(query);
            }).always(() => {
                this.setQuerying(false);
            });
        }
        else {
            PrimeFaces.ajax.Request.handle(options!);
        }

    }

    /**
     * Sets the querying state.
     * @param state Querying state to set.
     */
    private setQuerying(state: boolean): void {
        if (state && !this.querying) {
            this.jq.addClass('p-state-loading')
                .append('<span class="p-icon-loading p-icon p-loading-icon p-ajaxstatus-loading-icon"></span>');
        }
        else if (!state && this.querying) {
            this.jq.removeClass('p-state-loading')
                .find('.p-icon-loading').remove();
        }
        this.querying = state;
    }

    /**
     * Shows the panel with the suggestions.
     */
    show(): void {
        if (this.transition) {
            this.transition.show({
                onEnter: () => {
                    PrimeFaces.nextZindex(this.panel);
                    this.alignPanel();
                },
                onEntered: () => {
                    this.bindPanelEvents();
                    this.input.attr('aria-expanded', 'true');
                }
            });
        }
    }

    /**
     * Hides the panel with the suggestions.
     */
    hide(): void {
        if (this.panel.is(':visible') && this.transition) {
            if (this.cfg.dynamic && this.cfg.queryMode === 'server') {
                this.isDynamicLoaded = false;
            }

            this.transition.hide({
                onExit: () => {
                    this.unbindPanelEvents();
                },
                onExited: () => {
                    this.panel.css('height', 'auto');
                    this.input.attr('aria-expanded', 'false');
                    this.input.removeAttr('aria-activedescendant');
                }
            });
        }

        if (this.cfg.itemtip) {
            this.itemtip.hide();
        }
    }

    /**
     * Invokes the appropriate behavior for when a suggestion item was selected.
     * @param itemValue Value of the selected item.
     */
    private invokeItemSelectBehavior(itemValue: string): void {
        if (this.hasBehavior('itemSelect')) {
            const ext = {
                params: [
                    { name: this.id + '_itemSelect', value: itemValue }
                ]
            };

            this.callBehavior('itemSelect', ext);
        }
    }

    /**
     * Invokes the appropriate behavior when a suggestion item was unselected.
     * @param itemValue Value of the unselected item.
     */
    private invokeItemUnselectBehavior(itemValue: string): void {
        if (this.hasBehavior('itemUnselect')) {
            const ext = {
                params: [
                    { name: this.id + '_itemUnselect', value: itemValue }
                ]
            };

            this.callBehavior('itemUnselect', ext);
        }
    }

    /**
     * Invokes the appropriate behavior for when more text was selected.
     */
    private invokeMoreTextBehavior(): void {
        if (this.hasBehavior('moreTextSelect')) {
            const ext = {
                params: [
                    { name: this.id + '_moreTextSelect', value: true }
                ]
            };

            this.callBehavior('moreTextSelect', ext);
        }
    }

    /**
     * Invokes the appropriate behavior for when empty message was selected.
     */
    private invokeEmptyMessageBehavior(): void {
        if (this.hasBehavior('emptyMessageSelect')) {
            const ext = {
                params: [
                    { name: this.id + '_emptyMessageSelect', value: true }
                ]
            };

            this.callBehavior('emptyMessageSelect', ext);
        }
    }

    /**
     * Add the given suggestion item.
     * @param item Suggestion item to add.
     */
    addItem(item: JQuery | string): void {
        let itemValue: string | undefined = '',
            itemStyleClass: string | undefined = '',
            itemLabel: string | undefined = '';

        if (this.input.hasClass('p-state-disabled') || this.input.attr("readonly")) {
            return;
        }

        if (typeof item === 'string' || item instanceof String) {
            itemValue = item as string;
            itemLabel = item as string;
        }
        else {
            itemValue = item.attr('data-item-value');
            itemLabel = item.attr('data-item-label');
            itemStyleClass = item.attr('data-item-class');
        }

        if (!itemValue) {
            return;
        }

        if (this.cfg.multiple) {
            let found = false;
            if (this.cfg.unique) {
                found = this.multiItemContainer.children("li[data-token-value='" + CSS.escape(itemValue) + "']").length != 0;
            }

            if (!found) {
                if (this.multiItemContainer.children('li.p-autocomplete-token').length >= (this.cfg.selectLimit as number)) {
                    return;
                }

                const itemLabelEscaped = PrimeFaces.escapeHTML(itemLabel as string);
                const itemValueEscaped = PrimeFaces.escapeHTML(itemValue);
                let itemDisplayMarkup = '<li data-token-value="' + itemValueEscaped;
                itemDisplayMarkup += '"class="p-autocomplete-token p-state-active p-hidden';
                itemDisplayMarkup += (itemStyleClass === '' ? '' : ' ' + itemStyleClass) + '" ';
                itemDisplayMarkup += 'role="option" aria-label="' + itemLabelEscaped + '" ';
                itemDisplayMarkup += 'aria-selected="true">';
                itemDisplayMarkup += '<span class="p-autocomplete-token-icon p-icon p-close-icon p-autocomplete-chip-remove-icon" aria-hidden="true"></span>';
                itemDisplayMarkup += '<span class="p-autocomplete-token-label">' + itemLabelEscaped + '</span></li>';

                this.inputContainer.before(itemDisplayMarkup);
                this.multiItemContainer.children('.p-hidden').fadeIn();
                this.input.val('');
                this.input.removeAttr('placeholder');

                this.hinput.append('<option value="' + itemValueEscaped + '" selected="selected"></option>');
                if (this.multiItemContainer.children('li.p-autocomplete-token').length >= (this.cfg.selectLimit as number)) {
                    this.input.css('display', 'none').trigger("blur");
                    this.disableDropdown();
                }

                this.invokeItemSelectBehavior(itemValue);
            }
        } else {
            this.input.val((item as JQuery).attr('data-item-label') as string);

            this.currentText = this.input.val() as string;
            this.previousText = this.input.val() as string;

            if (this.cfg.pojo) {
                this.hinput.val(itemValue);
            }

            this.invokeItemSelectBehavior(itemValue);
        }

        if (this.cfg.onChange) {
            this.cfg.onChange.call(this);
        }

        if (!this.isTabPressed) {
            this.input.trigger('focus');
        }
    }

    /**
     * Removes the given suggestion item.
     * @param item Suggestion item to remove.
     */
    removeItem(item: JQuery | string): void {
        const $this = this;
        let itemValue: string | undefined = '';
        if (this.input.hasClass('p-state-disabled') || this.input.attr("readonly")) {
            return;
        }

        if (typeof item === 'string' || item instanceof String) {
            itemValue = item as string;
        }
        else {
            itemValue = item.attr('data-token-value');
        }

        const foundItem = this.multiItemContainer.children("li.p-autocomplete-token[data-token-value='" + CSS.escape(itemValue as string) + "']");
        if (!foundItem.length) {
            return;
        }
        const itemIndex = foundItem.index();
        if (!itemValue || itemIndex === -1) {
            return;
        }

        //remove from options
        this.hinput.children('option').eq(itemIndex).remove();

        //remove from items
        foundItem.fadeOut('fast', function() {
            const token = $(this);
            token.remove();
            $this.invokeItemUnselectBehavior(itemValue);
        });

        // if empty return placeholder
        if (this.placeholder && this.hinput.children('option').length === 0) {
            this.input.attr('placeholder', this.placeholder);
        }
    }

    /**
     * Removes all items if in multiple mode.
     */
    removeAllItems(): void {
        if (this.cfg.multiple && !(this.input.val() as string).length) {
            this.multiItemContainer.find('.p-autocomplete-token').each((_index, el) => {
                this.removeItem($(el));
            });
        }
    }

    /**
     * Sets up the event listener for the blur event to force a selection, when that feature is enabled.
     */
    private setupForceSelection(): void {
        this.currentItems = [this.input.val() as string];

        this.input.on('blur', (e) => {
            // #5731: do not fire clear event if selecting item
            const isNotPanel = e.relatedTarget == null || PrimeFaces.escapeClientId((e.relatedTarget as HTMLElement).id) !== this.panelId,
                value = $(e.currentTarget).val() as string,
                valid = this.isValid(value, isNotPanel);

            if (this.cfg.autoSelection && valid && this.checkMatchedItem && this.items && !this.isTabPressed && !this.itemSelectedWithEnter && isNotPanel) {
                const selectedItem = this.items.filter('[data-item-label="' + CSS.escape(value) + '"]');
                if (selectedItem.length) {
                    selectedItem.trigger("click");
                }
            }

            this.checkMatchedItem = false;
        });
    }

    /**
     * Disables the component.
     */
    override disable(): void {
        this.jq.addClass("p-state-disabled");
        PrimeFaces.utils.disableInputWidget(this.input, undefined);
        if (this.dropdown.length) {
            this.disableDropdown();
        }
    }

    /**
     * Enables the component.
     */
    override enable(): void {
        this.jq.removeClass("p-state-disabled");
        PrimeFaces.utils.enableInputWidget(this.input);
        if (this.dropdown.length) {
            this.enableDropdown();
        }
    }

    /**
     * Hides suggested items menu.
     */
    close(): void {
        this.hide();
    }

    /**
     * Deactivates search behavior.
     */
    deactivate(): void {
        this.active = false;
    }

    /**
     * Activates search behavior.
     */
    activate(): void {
        this.active = true;
    }

    /**
     * Aligns (positions) the overlay panel that shows the found suggestions.
     */
    alignPanel(): void {
        let panelWidth: number | null | undefined = null;

        if (this.cfg.multiple) {
            panelWidth = this.multiItemContainer.outerWidth();
        }
        else {
            if (this.panel.is(':visible')) {
                panelWidth = this.panel.children('.p-autocomplete-items').outerWidth();
            }
            else {
                this.panel.css({ 'visibility': 'hidden', 'display': 'block' });
                panelWidth = this.panel.children('.p-autocomplete-items').outerWidth();
                this.panel.css({ 'visibility': 'visible', 'display': 'none' });
            }

            const inputWidth = this.input.outerWidth();
            if ((panelWidth as number) < (inputWidth as number)) {
                panelWidth = inputWidth;
            }
        }

        if (this.cfg.scrollHeight) {
            const heightConstraint = this.panel.is(':hidden') ? this.panel.height() : this.panel.children().height();
            if ((heightConstraint as number) > this.cfg.scrollHeight)
                this.panel.height(this.cfg.scrollHeight);
            else
                this.panel.css('height', 'auto');
        }

        this.panel.css({
            'left': '',
            'top': '',
            'width': panelWidth + 'px',
            'z-index': PrimeFaces.nextZindex(),
            'transform-origin': 'center top'
        });

        if (this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px',
                'transform-origin': 'center top'
            });
        }
        else {
            PrimeFaces.utils.position(this.panel, {
                my: this.cfg.myPos
                , at: this.cfg.atPos
                , of: this.cfg.multiple ? this.jq : this.input
                , collision: 'flipfit'
                , using: function(pos: { top: number; left: number }, directions: PrimeFacesTypes.utils.PositionFeedback) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    }

    /**
     * Adds the given text to the ARIA status label element.
     * @param text Label text to display.
     */
    private displayAriaStatus(text: string): void {
        this.status.html('<div>' + PrimeFaces.escapeHTML(text) + '</div>');
    }

    /**
     * Adjusts the value of the aria attributes for the given selectable option.
     * @param item An option for which to set the aria attributes.
     */
    private changeAriaValue(item: Element | undefined): void {
        if (item) {
            this.input.attr('aria-activedescendant', item.id);
        }
    }

    /**
     * Adjusts the highlighting and aria attributes for the given selectable option.
     * @param item An option for which to set the aria attributes.
     * @param highlight Flag to indicate to highlight or not
     */
    private highlightItem(item: JQuery, highlight: boolean): void {
        if (highlight) {
            item.addClass('p-state-highlight');
        }
        else {
            item.removeClass('p-state-highlight');
        }
        item.attr('aria-selected', String(highlight));
    }

    /**
     * Takes the available suggestion items and groups them.
     */
    private groupItems(): void {
        if (this.items!.length) {
            this.itemContainer = this.panel.children('.p-autocomplete-items');

            const firstItem = this.items!.eq(0);
            if (!firstItem.hasClass('p-autocomplete-moretext')) {
                this.currentGroup = firstItem.data('item-group');
                const currentGroupTooltip = firstItem.data('item-group-tooltip');

                firstItem.before(this.getGroupItem(this.currentGroup as string, this.itemContainer, currentGroupTooltip));
            }

            this.items!.filter(':not(.p-autocomplete-moretext)').each((i) => {
                const item = this.items!.eq(i),
                    itemGroup = item.data('item-group'),
                    itemGroupTooltip = item.data('item-group-tooltip');

                if (this.currentGroup !== itemGroup) {
                    this.currentGroup = itemGroup;
                    item.before(this.getGroupItem(itemGroup, this.itemContainer, itemGroupTooltip));
                }
            });
        }
    }

    /**
     * Creates the grouped suggestion item for the given parameters.
     * @param group A group where to look for the item.
     * @param container Container element of the group.
     * @param tooltip Optional tooltip for the group item.
     * @returns The newly created group item.
     */
    private getGroupItem(group: string, container: JQuery, tooltip?: string): JQuery {
        let element: JQuery | null = null;

        if (container.is('.p-autocomplete-table')) {
            if (!this.colspan) {
                this.colspan = this.items!.eq(0).children('td').length;
            }

            element = $('<tr class="p-autocomplete-group p-widget-header"><td colspan="' + this.colspan + '">' + group + '</td></tr>');
        }
        else {
            element = $('<li class="p-autocomplete-group p-autocomplete-list-item p-widget-header">' + group + '</li>');
        }

        if (element) {
            element.attr('title', tooltip as string);
        }

        return element;
    }

    /**
     * Clears the set-timeout timer for the autocomplete search.
     */
    private deleteTimeout(): void {
        clearTimeout(this.timeout ?? undefined);
        this.timeout = null;
    }

    /**
     * Triggers the behavior for when the input was cleared.
     */
    private fireClearEvent(): void {
        this.callBehavior('clear');
        this.previousText = this.currentText;
        this.currentText = '';
    }

    /**
     * Checks whether the given value is part of the available suggestion items.
     * @param value A value to check.
     * @param shouldFireClearEvent `true` if clear event should be fired.
     * @returns Whether the given value matches a value in the list of available suggestion items;
     * or `undefined` if {@link AutoCompleteCfg.forceSelection} is set to `false`.
     */
    isValid(value: string, shouldFireClearEvent?: boolean): boolean | undefined {
        if (!this.cfg.forceSelection) {
            return;
        }

        let valid = false;

        for (const item of this.currentItems!) {
            const strippedItem = item ? item.replace(/\r?\n/g, '') : undefined;
            if (strippedItem === value) {
                valid = true;
                break;
            }
        }

        if (valid) {
            // #15304 sync the hidden input when a matching label was typed instead of selected
            if (this.cfg.pojo && !this.cfg.multiple && this.items) {
                const matchedItem = this.items.filter(':not(.p-autocomplete-moretext)[data-item-label="' + CSS.escape(value) + '"]');
                if (matchedItem.length) {
                    this.hinput.val(matchedItem.attr('data-item-value') as string);
                }
            }
        }
        else {
            this.input.val('');
            if (!this.cfg.multiple) {
                this.hinput.val('');
            }
            shouldFireClearEvent = shouldFireClearEvent && (!this.cfg.multiple && !!this.currentText);
            if (shouldFireClearEvent) {
                this.fireClearEvent();
            }
        }

        return valid;
    }

    /**
     * Fetches the suggestion items for the current query from the server.
     */
    private fetchItems(): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            formId: this.getParentFormId(),
            global: false,
            params: [{ name: this.id + '_clientCache', value: true }],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.setCache($(content));
                    }
                });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Adds the suggestions items in the given wrapper to the local cache of suggestion items.
     * @param wrapper Wrapper element with the suggestions fetched from the server.
     */
    private setCache(wrapper: JQuery): void {
        const items = wrapper.find('.p-autocomplete-item');
        let prevKey: string | null = null;

        if (!this.wrapperStartTag || !this.wrapperEndTag) {
            this.findWrapperTag(wrapper);
        }

        for (let i = 0; i < items.length; i++) {
            const item = items.eq(i),
                key = item.data('item-key') as string;

            this.cache![key] = (this.cache![key] || this.wrapperStartTag) + item.get(0)!.outerHTML;

            if (prevKey !== null && prevKey !== key) {
                this.cache![prevKey] += this.wrapperEndTag as string;
            }

            if (i === items.length - 1) {
                this.cache![key] += this.wrapperEndTag as string;
            }

            prevKey = key;
        }
    }

    /**
     * Finds and sets the wrapper HTML snippets on this instance.
     * @param wrapper Wrapper element with the suggestions fetched from the server.
     */
    private findWrapperTag(wrapper: JQuery): void {
        if (wrapper.is('ul')) {
            this.wrapperStartTag = '<ul class="p-autocomplete-items p-autocomplete-list p-widget-content p-widget" role="listbox">';
            this.wrapperEndTag = '</ul>';
        }
        else {
            const header = wrapper.find('> table > thead');
            this.wrapperStartTag = '<table class="p-autocomplete-items p-autocomplete-table p-widget-content p-widget" role="listbox">' +
                (header.length ? header[0]!.outerHTML : '') +
                '<tbody>';
            this.wrapperEndTag = '</tbody></table>';
        }
    }

    /**
     * Clears the input field.
     */
    clear(): void {
        this.input.val('');
        if (this.cfg.multiple) {
            this.removeAllItems();
        }
        else if (this.cfg.pojo) {
            this.hinput.val('');
        }
    }

}
