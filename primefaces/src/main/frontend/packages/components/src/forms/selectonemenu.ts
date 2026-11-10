/**
 * The configuration for the {@link SelectOneMenu} widget.
 */
export interface SelectOneMenuCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** `true` if the label of the selected item should always be set on the visible input, `false` otherwise. */
    alwaysDisplayLabel: boolean;
    /** Appends the overlay to the element defined by search expression. Defaults to the document body. */
    appendTo: string | null;
    /**
     * Calculates a fixed width based on the width of the maximum option label. Possible values: `auto` (default, only
     * without a `p-fluid` parent), `true`, `false`.
     */
    autoWidth: "auto" | "true" | "false";
    /** Defines if filtering would be case sensitive. */
    caseSensitive: boolean;
    /** Defines if filtering would be done using normalized values. */
    filterNormalize: boolean;
    /**
     * Defines if dynamic loading is enabled for the element's panel. If the value is `true`, the overlay is not
     * rendered on page load to improve performance.
     */
    dynamic: boolean;
    /** When true, the input field becomes editable. */
    editable: boolean;
    /** Name of the toggle animation of the overlay panel, defaults to `fade`. */
    effect: string;
    /** Duration of the toggle animation of the overlay panel, defaults to `normal`. */
    effectSpeed: PrimeFacesTypes.widget.SelectOneMenu.EffectSpeed;
    /** `true` if the options can be filtered, or `false` otherwise. */
    filter: boolean;
    /** A custom filter function that is used when `filterMatchMode` is set to `custom`. */
    filterFunction: PrimeFacesTypes.widget.SelectOneMenu.FilterFunction;
    /** Mode of the filter. When set to `custom` a `filterFunction` must be specified. */
    filterMatchMode: PrimeFacesTypes.widget.SelectOneMenu.FilterMatchMode;
    /** Initial height of the overlay panel in pixels. */
    initialHeight: number;
    /** Text of the label for the input. */
    label: string;
    /**
     * Displays label of the element in a custom template. Valid placeholder is `{0}`, which is replaced with the value
     * of the currently selected item.
     */
    labelTemplate: string;
    /**
     * `true` if the option that gets the focus while navigating with the keyboard is selected, or `false` if it only
     * gets the focus until it is selected with enter, tab or click.
     */
    selectOnFocus: boolean;
    /** Updates the title of the component with the description of the selected item. */
    syncTooltip: boolean;
    /** Renders panel content on client. */
    renderPanelContentOnClient: boolean;
}

/**
 * __PrimeFaces SelectOneMenu Widget__
 *
 * SelectOneMenu is an extended version of the standard SelectOneMenu.
 */
export class SelectOneMenu<Cfg extends SelectOneMenuCfg = SelectOneMenuCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** Whether the value of this widget was changed from its original value. */
    changed?: boolean;
    /** Whether a custom value, which does not have to match one of the available options, was entered (editable only). */
    customInput?: boolean;
    /** The custom value that was entered by the user which does not have to match one the available options. */
    customInputVal?: string;
    /** Whether this widget is currently disabled. */
    disabled: boolean = false;
    /** The DOM element for the input field that lets the user enter a search term to filter the list of available options. */
    filterInput?: JQuery;
    /** The filter that was selected and is currently used. */
    filterMatcher?: PrimeFacesTypes.widget.SelectOneMenu.FilterFunction;
    /** Map between the available filter types and the filter implementation. */
    filterMatchers?: Record<PrimeFacesTypes.widget.SelectOneMenu.FilterMatchMode, PrimeFacesTypes.widget.SelectOneMenu.FilterFunction | undefined>;
    /** Unbind callback for the hide overlay handler. */
    hideOverlayHandler?: PrimeFacesTypes.Unbindable;
    /** The DOM element for the hidden input with the current value. */
    input: JQuery = $();
    /** Whether the contents of the overlay panel were loaded. */
    isDynamicLoaded: boolean = false;
    /** Whether the current process is handling the tab key. */
    isTabbing: boolean = false;
    /** The DOM elements for the available selectable options, `null` while refreshing. */
    items?: JQuery | null;
    /** The DOM element for the container with the available selectable options. */
    itemsContainer?: JQuery;
    /** Whether an AJAX request for the autocompletion items is currently in progress. */
    querying?: boolean;
    /** The DOM element for the wrapper with the container with the available selectable options. */
    itemsWrapper: JQuery = $();
    /** The hidden input that can be focused via the tab key etc. (only used with editable="true") */
    focusInput?: JQuery;
    /** Is this component wrapped in a float label. */
    hasFloatLabel: boolean = false;
    /** The DOM element for the label indicating the currently selected option. */
    label: JQuery = $();
    /** The DOM element used as target for keyboard - events. */
    keyboardTarget: JQuery = $();
    /** The DOM element for the icon for bringing up the overlay panel. */
    menuIcon: JQuery = $();
    /** The DOM elements for the available selectable options. */
    options: JQuery<HTMLOptionElement> = $();
    /** The number of option groups. */
    optGroupsSize?: number;
    /** The DOM element for the overlay panel with the available selectable options. */
    panel: JQuery = $();
    /** ID of the DOM element for the overlay panel with the available selectable options. */
    panelId: string = "";
    /** The DOM element for the selected option that is shown before the overlay panel is brought up. */
    preShowValue?: JQuery<HTMLOptionElement>;
    /** The DOM element for the label connected to he SelectOneMenu. */
    labeledBy?: JQuery | null;
    /** Unbind callback for the resize handler. */
    resizeHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback for the scroll handler. */
    scrollHandler?: PrimeFacesTypes.Unbindable;
    /** Letters typed for selection. (#4682, only used with editable="false") */
    searchValue: string = "";
    /** ID of the timeout for clearing the typed letters. */
    searchTimer?: number;
    /** Handler for CSS transitions used by this widget. */
    transition?: PrimeFacesTypes.CssTransitionHandler | null;
    /** The DOM elements for the buttons that can trigger (hide or show) the overlay panel with the available selectable options. */
    triggers: JQuery = $();
    /** The current value of this select one menu. */
    value?: string;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.panelId = this.jqId + '_panel';
        this.input = $(this.jqId + '_input');
        if (this.cfg.editable) {
            this.focusInput = $(this.jqId + '_focus');
        }
        this.label = this.jq.find('.p-selectonemenu-label');
        this.menuIcon = this.jq.children('.p-selectonemenu-trigger');

        this.keyboardTarget = this.cfg.editable ? this.focusInput! : this.label;

        this.panel = $(this.panelId);
        this.disabled = this.jq.hasClass('p-state-disabled');
        this.hasFloatLabel = PrimeFaces.utils.hasFloatLabel(this.jq);
        this.itemsWrapper = this.panel.children('.p-selectonemenu-items-wrapper');
        this.options = this.input.find('option');
        this.cfg.effect = this.cfg.effect||'fade';

        this.cfg.effectSpeed = this.cfg.effectSpeed||'normal';
        this.cfg.autoWidth = this.cfg.autoWidth === undefined ? 'auto' : this.cfg.autoWidth;
        this.cfg.dynamic = !!this.cfg.dynamic;
        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this as unknown as Parameters<typeof PrimeFaces.utils.resolveAppendTo>[0], this.jq, this.panel);
        this.cfg.renderPanelContentOnClient = this.cfg.renderPanelContentOnClient === true;
        this.cfg.selectOnFocus = this.cfg.selectOnFocus !== false;
        this.isDynamicLoaded = false;
        this.isTabbing = false;
        this.searchValue = '';

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        if(this.cfg.dynamic || (this.itemsWrapper.children().length === 0)) {
            const selectedOption = this.options.filter(':selected'),
            labelVal = this.cfg.editable ? this.label.val() as string : selectedOption.text();

            this.setLabel(labelVal);
        }
        else {
            this.initContents();
            this.bindItemEvents();
        }

        //triggers
        this.triggers = this.cfg.editable ? this.jq.find('.p-selectonemenu-trigger') : this.jq.find('.p-selectonemenu-trigger, .p-selectonemenu-label');

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        if(!this.disabled) {
            this.bindEvents();

            PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'p-connected-overlay');
        }

        // float label
        this.bindFloatLabel();

        // see #7602
        if (this.focusInput && PrimeFaces.env.isTouchable(this.cfg)) {
            this.focusInput.attr('readonly', 'readonly');
        }

        if (!this.cfg.editable) {
            // non-editable menus omit the label "for" (a <span> is not labelable), so wire click-to-focus
            // from the aria-labelledby label to the focusable span ourselves
            const labeledBy = this.label.attr('aria-labelledby');
            this.labeledBy = null;
            if (labeledBy) {
                const eltLabeledBy = document.getElementById(labeledBy);
                if (eltLabeledBy) {
                    this.labeledBy = $(eltLabeledBy);
                    this.labeledBy.off('click.selectonemenu').on('click.selectonemenu', () => {
                        this.keyboardTarget.focus();
                    });
                }
            }
        }

        this.renderDeferred();
    }

    /**
     * Finds and initializes the DOM elements that make up this widget.
     */
    private initContents(): void {
        this.itemsContainer = this.itemsWrapper.children('.p-selectonemenu-items');
        this.items = this.itemsContainer.find('.p-selectonemenu-item');
        this.optGroupsSize = this.itemsContainer.find('li.p-selectonemenu-item-group').length;

        const selectedOption = this.options.filter(':selected'),
        highlightedItem = this.selectItemFromOption(selectedOption);

        //disable options
        this.items.filter('[disabled]').addClass('p-state-disabled');

        //activate selected
        if(this.cfg.editable) {
            const customInputVal = this.label.val() as string;

            //predefined input
            if(customInputVal === selectedOption.text()) {
                this.highlightItem(highlightedItem);
            }
            //custom input
            else {
                this.items.eq(0).addClass('p-state-highlight');
                this.customInput = true;
                this.customInputVal = customInputVal;
            }
        }
        else {
            this.highlightItem(highlightedItem);
        }

        if(this.cfg.syncTooltip) {
            this.syncTitle(selectedOption);
        }

        // ARIA
        for(let i = 0; i < this.items.length; i++) {
            this.items.eq(i).attr('id', this.id + '_' + i);
        }

        const highlightedItemId = highlightedItem.attr('id'),
            itemsContainerId = this.itemsContainer.attr('id');
        this.jq.attr('aria-owns', itemsContainerId as string);
        this.keyboardTarget
            .attr('aria-activedescendant', highlightedItemId as string)
            .attr('aria-disabled', String(this.disabled));

        this.itemsContainer.attr('aria-activedescendant', highlightedItemId as string);
    }

    protected override _render(): void {
        if (this.cfg.autoWidth != 'false') {
            const contentStyle = this.jq.attr('style');
            const hasWidth = contentStyle && contentStyle.indexOf('width') != -1;

            if (!hasWidth) {
                // 'true' -> always calculate min-width
                let calculateMinWidth = true;

                // 'auto' -> only calculate it without a p-fluid parent
                if (this.cfg.autoWidth == 'auto') {
                    const hasFluidParent = this.jq[0]!.closest('.p-fluid') != undefined;
                    calculateMinWidth = !hasFluidParent;
                }

                if (calculateMinWidth) {
                    this.jq.css('min-width', this.input.outerWidth() + 'px');
                }
            }
        }
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this.items = null;

        super.refresh(cfg);
    }

    /**
     * Adjust the width of the overlay panel to match the width of the select menu.
     * If the panel is narrower than the select menu, expands it to match.
     * If the panel is wider, preserves its current width.
     * Only adjusts width once per panel instance.
     */
    private alignPanelWidth(): void {
        const jqWidth = this.jq.outerWidth()!;

        /* Ensures that the width of a potential scrollbar is considered in the following panel width calculation */
        const hasVerticalScrollbar = this.itemsWrapper[0]!.scrollHeight > this.itemsWrapper[0]!.clientHeight;
        if (hasVerticalScrollbar) {
            this.itemsWrapper.css({'overflow': 'scroll'});
        }

        if(this.panel.outerWidth()! < jqWidth) {
            this.panel.outerWidth(jqWidth);
        }
        else {
            this.panel.outerWidth(this.panel.outerWidth()!);
        }

        this.itemsWrapper.css({'overflow': ''});
    }

    /**
     * Handles floating label CSS if wrapped in a floating label.
     * @param input the input
     */
    private updateFloatLabel(input: JQuery | undefined): void {
        PrimeFaces.utils.updateFloatLabel(this.jq, input, this.hasFloatLabel);
    }

    /**
     * Sets up all event listeners required by this widget.
     */
    private bindEvents(): void {
        //Triggers
        this.triggers.on("mouseenter", () => {
            if(!this.jq.hasClass('p-state-focus')) {
                this.jq.addClass('p-state-hover');
                this.menuIcon.addClass('p-state-hover');
            }
        })
        .on("mouseleave", () => {
            this.jq.removeClass('p-state-hover');
            this.menuIcon.removeClass('p-state-hover');
        })
        .on("click", (e) => {
            if(this.panel.is(":hidden")) {
                this.show();
            }
            else {
                this.hide();

                this.revert();
                this.changeAriaValue(this.getActiveItem());
            }

            this.jq.removeClass('p-state-hover');
            this.menuIcon.removeClass('p-state-hover');
            if (this.focusInput) {
                this.focusInput.trigger('focus.p-selectonemenu')
            }
            else if ($(e.currentTarget).hasClass('p-selectonemenu-trigger')) {
                this.keyboardTarget.trigger('focus.p-selectonemenu');
            }
            e.preventDefault();
            e.stopPropagation();
        });

        this.input.off('focus.p-selectonemenu').on('focus.p-selectonemenu', () => {
            this.keyboardTarget.trigger('focus.p-selectonemenu');
        });

        this.keyboardTarget.on('focus.p-selectonemenu', (e) => {
            this.jq.addClass('p-state-focus');
            this.menuIcon.addClass('p-state-focus');
            if(!this.cfg.dynamic && !this.items) {
                // the old code passes the result of handleTabKey() (undefined), so it is called before
                this.handleTabKey();
                this.callHandleMethod(undefined, e);
            }
            if (this.hasFloatLabel) {
                this.jq.addClass('p-inputwrapper-focus');
            }
        })
        .on('blur.p-selectonemenu', () => {
            this.jq.removeClass('p-state-focus');
            this.menuIcon.removeClass('p-state-focus');
            if (this.hasFloatLabel) {
                this.jq.removeClass('p-inputwrapper-focus');
            }
            this.callBehavior('blur');
        });

        //onchange handler for editable input
        if(this.cfg.editable) {
            this.label.on('change', (e) => {
                this.triggerChange(true);
                this.callHandleMethod(this.handleLabelChange, e);
            }).on('keyup', (e) => {
                switch (e.code) {
                    case 'Backspace':
                    case 'Delete':
                        if ($(e.currentTarget).val() === '') {
                            this.callBehavior('clear');
                        }
                        break;
                }
            });
        }

        //key bindings
        this.bindKeyEvents();

        //filter
        if(this.cfg.filter) {
            this.cfg.initialHeight = this.itemsWrapper.height();
            this.setupFilterMatcher();
            this.filterInput = this.panel.find('> div.p-selectonemenu-filter-container > input.p-selectonemenu-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.bindFilterEvents();
        }
    }

    /**
     * Sets up the event listeners if this is bound to a floating label.
     */
    private bindFloatLabel(): void {
        if (!this.hasFloatLabel) {
            return;
        }
        this.panel.addClass('p-input-overlay-panel');
        this.jq.addClass('p-inputwrapper');

        this.updateFloatLabel(this.input);

        this.input.off('change').on('change', (e) => {
            this.updateFloatLabel($(e.currentTarget));
        });

        if (this.cfg.editable) {
            this.label.on('input', (e) => {
                this.updateFloatLabel($(e.currentTarget));
            }).on('focus', () => {
                this.jq.addClass('p-inputwrapper-focus');
            }).on('blur', (e) => {
                this.jq.removeClass('p-inputwrapper-focus');
                this.updateFloatLabel($(e.currentTarget));
            });
        }
    }

    /**
     * Sets up the event listeners for the selectable items.
     */
    private bindItemEvents(): void {
        if(!this.items) {
            return;
        }

        //Items
        this.items.filter(':not(.p-state-disabled)').on('mouseover.selectonemenu', (e) => {
            const el = $(e.currentTarget);

            if(!el.hasClass('p-state-highlight'))
                $(e.currentTarget).addClass('p-state-hover');
        })
        .on('mouseout.selectonemenu', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('click.selectonemenu', (e) => {
            this.revert();
            this.selectItem($(e.currentTarget));
            this.changeAriaValue($(e.currentTarget));
        });
    }

    /**
     * Sets up all panel event listeners
     */
    private bindPanelEvents(): void {
        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            () => this.label.add(this.menuIcon),
            (e, eventTarget) => {
                if(!(this.panel.is(eventTarget) || this.panel.has(eventTarget[0] ?? "").length > 0)) {
                    this.hide();
                    PrimeFaces.queueTask(() => {
                        this.revert();
                        this.changeAriaValue(this.getActiveItem());
                    });
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, () => {
            this.handleViewportChange();
        });

        // GitHub #1173/#4609 keep panel with select while scrolling
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
     * Removes some event listeners when this widget was disabled.
     */
    private unbindEvents(): void {
        if (this.items) {
            this.items.off();
        }
        this.triggers.off();
        this.input.off();
        if (this.focusInput) {
            this.focusInput.off();
        }
        this.label.off();
        if (this.labeledBy) {
            this.labeledBy.off();
        }
    }

    /**
     * Unselect the selected item, if any, and select the `please select` option.
     */
    revert(): void {
        if(this.cfg.editable && this.customInput) {
            this.setLabel(this.customInputVal!);
            this.items!.filter('.p-state-active').removeClass('p-state-active');
            this.items!.eq(0).addClass('p-state-active');
        }
        else {
            this.highlightItem(this.selectItemFromOption(this.preShowValue!));
        }
    }

    /**
     * Highlight the given selectable option.
     * @param item Option to highlight.
     */
    private highlightItem(item: JQuery): void {
        this.items!.attr('aria-selected', 'false');
        this.items!.filter('.p-state-highlight, .p-state-focus').removeClass('p-state-highlight p-state-focus');

        if(item.length > 0) {
            item.addClass('p-state-highlight');
            item.attr('aria-selected', 'true');
            this.setLabel(item.data('label'));
        }
    }

    /**
     * Moves the focus to the given option while navigating with the keyboard or filtering. With `selectOnFocus` the
     * option is highlighted as the selected one. Otherwise it only gets the focus, and the selected option keeps its
     * highlight, `aria-selected` and the label until an option is selected.
     * @param item Option to focus.
     */
    private focusItem(item: JQuery): void {
        if(this.cfg.selectOnFocus) {
            this.highlightItem(item);
        }
        else {
            this.items!.filter('.p-state-focus').removeClass('p-state-focus');
            item.addClass('p-state-focus');
        }
    }

    /**
     * Callback for an option found by typing on the closed menu. Selects the option with `selectOnFocus`, otherwise
     * brings up the overlay panel with the option focused.
     * @param item The option that was found.
     */
    private focusOrSelectItem(item: JQuery): void {
        if(this.cfg.selectOnFocus) {
            this.selectItem(item);
        }
        else {
            this.focusItem(item);
            this.changeAriaValue(item);
            this.show();
        }
    }

    /**
     * Triggers the event listeners when the value of this widget changed.
     * @param edited Whether the value was edited by the user. If it was, checks which option is now selected.
     */
    private triggerChange(edited?: boolean): void {
        this.changed = false;

        this.input.trigger('change');

        if(!edited) {
            this.value = this.options.filter(':selected').val() as string;
        }
    }

    /**
     * Callback for when the user selects an item with the mouse.
     * @param item The option to select.
     * @param silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    private selectItem(item: JQuery, silent?: boolean): void {
        const selectedOption = this.selectOptionFromItem(item),
        currentOption = this.options.filter(':selected'),
        sameOption = selectedOption.val() == currentOption.val();
        let shouldChange: boolean | null = null;

        if(this.cfg.editable) {
            shouldChange = (!sameOption)||(selectedOption.text() != this.label.val());
        }
        else {
            shouldChange = !sameOption;
        }

        if(shouldChange) {
            this.highlightItem(item);
            this.input.val(selectedOption.val() as string);

            if(!silent) {
                this.triggerChange();
            }

            if(this.cfg.editable) {
                this.customInput = false;
            }

            if(this.cfg.syncTooltip) {
                this.syncTitle(selectedOption);
            }
        }

        if(!silent) {
            this.callBehavior('itemSelect');
            if(this.focusInput) {
                this.focusInput.trigger('focus');
            }
        }

        if(this.panel.is(':visible')) {
            this.hide();
        }
    }

    /**
     * Adjust the value of the title attribute to match selected option.
     * @param option The option that was selected.
     */
    private syncTitle(option: JQuery<HTMLOptionElement>): void {
        const optionTitle = this.selectItemFromOption(option).attr('title');
        if(optionTitle)
            this.jq.attr('title', optionTitle);
        else
            this.jq.removeAttr('title');
    }

    /**
     * Sets up the event listeners for all keyboard related events other than the overlay panel, such as pressing space
     * to bring up the overlay panel.
     */
    private bindKeyEvents(): void {
        this.keyboardTarget.on('keydown.p-selectonemenu', (e) => {
            switch (e.code) {
                case 'ArrowUp':
                case 'ArrowLeft':
                    this.callHandleMethod(this.highlightPrev, e);
                    break;

                case 'ArrowDown':
                case 'ArrowRight':
                    this.callHandleMethod(this.highlightNext, e);
                    break;

                case 'Enter':
                case 'NumpadEnter':
                    this.handleEnterKey(e);
                    break;

                case 'Tab':
                    this.handleTabKey(e);
                    break;

                case 'Escape':
                    this.handleEscapeKey(e);
                    break;

                case 'Space':
                    this.handleSpaceKey(e);
                    break;
            }
        }).on('keyup.p-selectonemenu', (e) => {
            if (PrimeFaces.utils.ignoreFilterKey(e)) {
                return;
            }
            if (!PrimeFaces.utils.isPrintableKey(e) || e.metaKey || e.ctrlKey || e.altKey) {
                return;
            }

            let matchedOptions: JQuery<HTMLOptionElement> | false | null = null;

            clearTimeout(this.searchTimer);

            // #4682: check for word match
            let text = $(e.currentTarget).val() as string;
            if (!this.focusInput) {
                this.searchValue += e.key;
                text = this.searchValue;
            }

            matchedOptions = this.matchOptions(text);
            if (matchedOptions && matchedOptions.length) {
                const matchIndex = matchedOptions[0]!.index;
                if (this.panel.is(':hidden')) {
                    this.callHandleMethod(() => {
                        this.focusOrSelectItem(this.items!.eq(matchIndex));
                    }, e);
                }
                else {
                    const highlightItem = this.items!.eq(matchIndex);
                    this.focusItem(highlightItem);
                    this.changeAriaValue(highlightItem);
                    PrimeFaces.scrollInView(this.itemsWrapper, highlightItem);
                }
            } else {
                // #4682: check for first letter match
                text = e.key.toLowerCase();
                // find all options with the same first letter
                matchedOptions = this.matchOptions(text);
                if (matchedOptions && matchedOptions.length) {
                    this.callHandleMethod(() => {
                        let selectedIndex = -1;
                        const activeItem = this.getActiveItem();

                        // is current selection one of our matches?
                        (matchedOptions as JQuery<HTMLOptionElement>).each((i, el): false | void => {
                            const option = $(el);
                            const currentIndex = option[0]!.index;
                            const currentItem = this.items!.eq(currentIndex);
                            if (currentItem.is(activeItem)) {
                                selectedIndex = currentIndex;
                                return false;
                            }
                        });

                        (matchedOptions as JQuery<HTMLOptionElement>).each((i, el): false | void => {
                            const option = $(el);
                            const currentIndex = option[0]!.index;
                            const currentItem = this.items!.eq(currentIndex);

                            // select next item after the current selection
                            if (currentIndex > selectedIndex) {
                                if (this.panel.is(':hidden')) {
                                    this.focusOrSelectItem(currentItem);
                                }
                                else {
                                    this.focusItem(currentItem);
                                    this.changeAriaValue(currentItem);
                                    PrimeFaces.scrollInView(this.itemsWrapper, currentItem);
                                }
                                return false;
                            }
                        });
                    }, e);
                }
            }

            this.searchTimer = PrimeFaces.queueTask(() => {
                this.searchValue = '';
                if (this.focusInput) {
                    this.focusInput.val('');
                }
            }, 1000);
        });
    }

    /**
     * Finds all options that match the given search string.
     * @param text The search string against which to match the options.
     * @returns All selectable options that match (start with) the given search string, `false` for an empty string.
     */
    private matchOptions(text: string): JQuery<HTMLOptionElement> | false {
        if(!text) {
            return false;
        }
        return this.options.filter((i, el) => {
            const option = $(el);
            return !option.is(':disabled') && option.text().toLowerCase().indexOf(text.toLowerCase()) === 0;
        });
    }

    /**
     * Sets up the event listeners for the filter input in the overlay panel.
     */
    private bindFilterEvents(): void {
        this.filterInput!.on('keyup.p-selectonemenu', (e) => {
            if (PrimeFaces.utils.ignoreFilterKey(e)) {
                return;
            }
            const metaKey = e.metaKey||e.ctrlKey;

            if(!metaKey) {
                this.filter($(e.currentTarget).val() as string);
            }
        })
        .on('keydown.p-selectonemenu', (e) => {

            switch(e.code) {
                case 'ArrowUp':
                    this.highlightPrev(e);
                break;

                case 'ArrowDown':
                    this.highlightNext(e);
                break;

                case 'Enter':
                case 'NumpadEnter':
                case 'Tab':
                    this.keyboardTarget.trigger("focus");
                    this.keyboardTarget.trigger(jQuery.Event('keydown', { key: e.key, code: e.code }));
                break;

                case 'Escape':
                    this.handleEscapeKey(e);
                break;

                case 'Space':
                    this.handleSpaceKey(e);
                break;

                default:
                break;
            }
        }).on('input.p-selectonemenu paste.p-selectonemenu drop.p-selectonemenu', (e) => {
            if (e.type === 'paste' || e.type === 'drop') {
                PrimeFaces.queueTask(() => this.filter(this.filterInput!.val() as string), 10);
            }
        });
    }

    /**
     * Highlights the next option after the currently highlighted option in the overlay panel.
     * @param event The event of the keypress.
     */
    private highlightNext(event: JQuery.TriggeredEvent): void {
        // Get the currently active item
        const activeItem = this.getActiveItem();

        // Check if the panel is hidden
        const isHidden = this.panel.is(':hidden');

        // Create a selector for the next valid option
        // If panel is hidden, select the first option, otherwise select the first visible option
        const selector = '[role="option"]:not(.p-state-disabled)' + (isHidden ? ':first' : ':visible:first');

        // Try to find the next valid option after the active item
        let next = activeItem.nextAll(selector);

        if (next.length === 0) {
            const index = this.items!.index(activeItem) + 1;
            if (index >= 0) {
                next = this.items!.eq(index);
            }
        }

        if(event.altKey || (isHidden && !this.cfg.selectOnFocus)) {
            this.show();
        }
        else if(next.length === 1) {
            if(this.panel.is(':hidden')) {
                this.selectItem(next);
            }
            else {
                this.focusItem(next);
                PrimeFaces.scrollInView(this.itemsWrapper, next);
            }
            this.changeAriaValue(next);
        }

        event.preventDefault();
    }

    /**
     * Highlights the previous option before the currently highlighted option in the overlay panel.
     * @param event The event of the keypress.
     */
    private highlightPrev(event: JQuery.TriggeredEvent): void {
        // Get the currently active item
        const activeItem = this.getActiveItem();

        // Check if the panel is hidden
        const isHidden = this.panel.is(':hidden');

        // Create a selector for the next valid option
        // If panel is hidden, select the first option, otherwise select the first visible option
        const selector = '[role="option"]:not(.p-state-disabled)' + (isHidden ? ':first' : ':visible:first');

        // Try to find the previous valid option after the active item
        let prev = activeItem.prevAll(selector);

        // If no next option found, look for the first option in the next group (ul)
        if (prev.length === 0) {
            const index = this.items!.index(activeItem) - 1;
            if (index >= 0) {
                prev = this.items!.eq(index);
            }
        }

        if(isHidden && !this.cfg.selectOnFocus) {
            this.show();
        }
        else if(prev.length === 1) {
            if(this.panel.is(':hidden')) {
                this.selectItem(prev);
            }
            else {
                this.focusItem(prev);
                PrimeFaces.scrollInView(this.itemsWrapper, prev);
            }
            this.changeAriaValue(prev);
        }

        event.preventDefault();
    }

    /**
     * Callback for when the enter key was pressed. Brings up the overlay panel or accepts the highlighted option.
     * @param event The event of the keypress.
     */
    private handleEnterKey(event: JQuery.TriggeredEvent): void {
        if(this.panel.is(':visible')) {
            this.selectItem(this.getActiveItem());
            // #8308 prevent Default Command while panel is open
            event.stopPropagation();
        }

        event.preventDefault();
    }

    /**
     * Callback for when the space key was pressed. Brings up or hides the overlay panel.
     * @param event The event of the keypress.
     */
    private handleSpaceKey(event: JQuery.TriggeredEvent): void {
        const target = $(event.target);

        if(target.is('input') && target.hasClass('p-selectonemenu-filter')) {
            return;
        }

        if(this.panel.is(":hidden")) {
            this.show();
        }
        else {
            this.hide();

            this.revert();
            this.changeAriaValue(this.getActiveItem());
        }

        event.preventDefault();
    }

    /**
     * Callback for when the escape key was pressed. Hides the overlay panel.
     * @param event The event of the keypress.
     */
    private handleEscapeKey(event: JQuery.TriggeredEvent): void {
        if(this.panel.is(':visible')) {
            this.revert();
            this.hide();
            event.preventDefault();
        }
    }

    /**
     * Callback for when the tab key was pressed. Selects the next option.
     * @param _event The keyboard event for the TAB.
     */
    private handleTabKey(_event?: JQuery.TriggeredEvent): void {
        this.isTabbing = this.panel.is(':visible');
        if(this.isTabbing) {
            this.selectItem(this.getActiveItem());
        }
    }

    /**
     * Callback that adjusts the label, invoked when the selected option has changed.
     * @param event The event that triggered the change.
     */
    private handleLabelChange(event: JQuery.TriggeredEvent): void {
        this.customInput = true;
        this.customInputVal = $(event.target).val() as string;
        this.items!.filter('.p-state-active').removeClass('p-state-active');
        this.items!.eq(0).addClass('p-state-active');
    }

    /**
     * Brings up the overlay panel with the available selectable options.
     */
    show(): void {
        this.callHandleMethod(this._show, null);
    }

    /**
     * Brings up the overlay panel with the available selectable options. Compared this `show`, this does not ensure
     * the the overlay panel is loaded already (when dynamic loading is enabled).
     */
    private _show(): void {
        if (this.transition) {
            //value before panel is shown, set before the transition as items are clickable during the fade-in
            this.preShowValue = this.options.filter(':selected');

            this.transition.show({
                onEnter: () => {
                    PrimeFaces.nextZindex(this.panel);
                    this.alignPanel();
                },
                onEntered: () => {
                    this.bindPanelEvents();

                    this.keyboardTarget.attr('aria-expanded', 'true');

                    PrimeFaces.scrollInView(this.itemsWrapper, this.getActiveItem());

                    if (this.cfg.filter) {
                        this.focusFilter();
                    }
                }
            });
        }
    }

    /**
     * Hides the overlay panel with the available selectable options.
     */
    hide(): void {
        if (this.panel.is(':visible') && this.transition) {
            this.searchValue = '';
            this.items!.filter('.p-state-focus').removeClass('p-state-focus');

            this.transition.hide({
                onExit: () => {
                    this.unbindPanelEvents();
                },
                onExited: () => {
                    this.panel.css('z-index', '');
                    this.keyboardTarget.attr('aria-expanded', 'false');

                    // refocus input if not tabbing and active is owned by this component
                    if (!this.isTabbing) {
                        const activeElement = $(document.activeElement!);
                        if (this.jq.has(activeElement[0] ?? "").length || this.panel.has(activeElement[0] ?? "").length) {
                            this.keyboardTarget.trigger('focus.p-selectonemenu');
                        }
                    }
                    this.isTabbing = false;
                }
            });
        }
    }

    /**
     * Puts focus on this widget.
     */
    focus(): void {
        this.keyboardTarget.trigger('focus');
    }

    /**
     * Puts focus on the filter input in the overlay panel.
     * @param timeout Amount of time in milliseconds to wait before attempting to focus the input.
     */
    focusFilter(timeout?: number): void {
        if(timeout) {
            PrimeFaces.queueTask(() => {
                this.focusFilter();
            }, timeout);
        }
        else {
            this.filterInput!.trigger('focus');
        }
    }

    /**
     * Removes focus from this widget.
     */
    blur(): void {
        this.keyboardTarget.trigger("blur");
        this.searchValue = '';

        this.callBehavior('blur');
    }

    /**
     * Disables this widget so that the user cannot select any option.
     */
    override disable(): void {
        if (!this.disabled) {
            this.disabled = true;
            this.jq.addClass('p-state-disabled');
            this.input.attr('disabled', 'disabled');
            if(this.cfg.editable) {
                this.label.attr('disabled', 'disabled');
            }
            this.unbindEvents();
        }
    }

    /**
     * Enables this widget so that the user can select an option.
     */
    override enable(): void {
        if (this.disabled) {
            this.disabled = false;
            this.jq.removeClass('p-state-disabled');
            this.input.removeAttr('disabled');
            if(this.cfg.editable) {
                this.label.removeAttr('disabled');
            }

            this.bindEvents();
            this.bindItemEvents();
        }
    }

    /**
     * Align the overlay panel with the available selectable options so that is is positioned next to the the button.
     */
    alignPanel(): void {
        this.alignPanelWidth();

        if(this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px',
                'transform-origin': 'center top'
            });
        }
        else {
            PrimeFaces.utils.position(this.panel.css({left:'0px', top:'0px', 'transform-origin': 'center top'}), {
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
                ,using: function(pos: { top: number; left: number }, directions: PrimeFacesTypes.utils.PositionFeedback) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    }

    /**
     * Sets the label text that indicates the currently selected item to the item with the given value.
     * @param value Value of the item that was selected.
     */
    private setLabel(value: string): void {
        const displayedLabel = this.getLabelToDisplay(value);
        const valueEmpty = value === '&nbsp;' || value === '\xa0';
        if (this.cfg.editable) {
            if (valueEmpty)
                this.label.val('');
            else
                this.label.val(displayedLabel);

            const hasPlaceholder = this.label[0]!.hasAttribute('placeholder');
            this.updatePlaceholderClass((hasPlaceholder && valueEmpty));
        }
        else if (this.cfg.alwaysDisplayLabel && this.cfg.label) {
            this.label.text(this.cfg.label);
        }
        else {
            let labelText: string | null | undefined = this.label.data('placeholder');
            if (labelText == null || labelText == "") {
                labelText = '&nbsp;';
            }

            this.updatePlaceholderClass((valueEmpty && labelText !== '&nbsp;'));

            if (valueEmpty) {
                if (labelText != '&nbsp;') {
                    this.label.text(labelText);
                    // the placeholder is the visible text, so that is what should be announced
                    this.label.attr('aria-label', labelText);
                } else {
                    // nothing is visible, so fall back to the localized "nothing selected" text
                    this.label.html(labelText + this.renderEmptyItemLabel());
                    this.label.attr('aria-label', this.getEmptyItemLabel());
                }
            }
            else {
                this.label.removeClass('p-state-disabled');

                let option: JQuery | null = null;
                if (this.items) {
                    const selectedItem = this.items.filter('[data-label="' + CSS.escape(value) + '"]');
                    option = this.selectOptionFromItem(selectedItem);
                }
                else {
                    option = this.options.filter(':selected');
                }

                if (option && option.data('escape') === false) {
                    this.label.html(displayedLabel);
                } else {
                    this.label.text(displayedLabel);
                }
                this.label.attr('aria-label', displayedLabel);
            }
        }
    }

    /**
     * Selects the option with the given value.
     * @param value Value of the option to select.
     */
    selectValue(value: string): void {
        if(!this.items || this.items.length === 0) {
           this.callHandleMethod(null, null);
        }

        let item: JQuery;
        if (this.optGroupsSize === 0) {
            const option = this.options.filter('[value="' + CSS.escape(value) + '"]');
            item = this.selectItemFromOption(option);
        }
        else {
            item = this.items!.filter('[data-value="' + CSS.escape(value) + '"]');
        }
        this.selectItem(item, true);
    }

    /**
     * Selects the item corresponding to the given option.
     * @param option The option element to select.
     * @returns The selected item element.
     */
    selectItemFromOption(option: JQuery<HTMLOptionElement>): JQuery {
        let item: JQuery;
        if (this.optGroupsSize === 0) {
            item = this.items!.eq(option.index());
        }
        else {
            item = this.items!.filter('[data-value="' + CSS.escape(option.val() as string) + '"]');
        }
        return item;
    }

    /**
     * Selects the option corresponding to the given item.
     * @param item The item element to select.
     * @returns The selected option element.
     */
    selectOptionFromItem(item: JQuery): JQuery<HTMLOptionElement> {
        let option: JQuery<HTMLOptionElement>;
        if (this.optGroupsSize === 0) {
            option = this.options.eq(item.index());
        }
        else {
            option = this.options.filter('[value="' + CSS.escape(item.attr('data-value') as string) + '"]');
        }
        return option;
    }

    /**
     * Resets the input.
     * @param silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    resetValue(silent?: boolean): void {
        if(!this.items || this.items.length === 0) {
           this.callHandleMethod(null, null);
        }

        let option = this.options.filter('[value=""]');
        if (option.length === 0) {
            // if no empty value option found, fallback to first in list like Faces default
            option = this.options.eq(0);
        }
        this.selectItem(this.selectItemFromOption(option), silent);
    }

    /**
     * Finds the element for the currently active option of this select one menu: the option that got the focus while
     * navigating when `selectOnFocus` is disabled, otherwise the selected option.
     * @returns The DOM element that represents the currently active option.
     */
    getActiveItem(): JQuery {
        const focusedItem = this.items!.filter('.p-state-focus');
        return focusedItem.length ? focusedItem : this.items!.filter('.p-state-highlight');
    }

    /**
     * Finds and stores the filter function which is to be used for filtering the options of this select one menu.
     */
    private setupFilterMatcher(): void {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    }

    /**
     * Implementation of a `PrimeFaces.widget.SelectOneMenu.FilterFunction` that matches the given option when it starts
     * with the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the options starts with the filter value, or `false` otherwise.
     */
    startsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter) === 0;
    }

    /**
     * Implementation of a `PrimeFaces.widget.SelectOneMenu.FilterFunction` that matches the given option when it
     * contains the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the contains the filter value, or `false` otherwise.
     */
    containsFilter(value: string, filter: string): boolean {
        return value.indexOf(filter) !== -1;
    }

    /**
     * Implementation of a `PrimeFaces.widget.SelectOneMenu.FilterFunction` that matches the given option when it ends
     * with the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the options ends with the filter value, or `false` otherwise.
     */
    endsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    }

    /**
     * Filters the available options in the overlay panel by the given search value. Note that this does not bring up
     * the overlay panel, use `show` for that.
     * @param value A value against which the available options are matched.
     */
    filter(value: string): void {
        this.cfg.initialHeight = this.cfg.initialHeight||this.itemsWrapper.height();
        const lowercase = !this.cfg.caseSensitive,
                normalize = !!this.cfg.filterNormalize,
                filterValue = PrimeFaces.toSearchable(PrimeFaces.trim(value), lowercase, normalize);

        // the filtered out items also get p-hidden: p-rows-striped counts the visible items only (CSS :nth-child of S)
        if(filterValue === '') {
            this.items!.filter(':hidden').show();
            this.items!.removeClass('p-hidden');
            this.itemsContainer!.find('li.p-selectonemenu-item-group').show();
        }
        else {
            let hide: JQuery[] = [];
            let show: JQuery[] = [];

            for(let i = 0; i < this.options.length; i++) {
                const option = this.options.eq(i),
                item = this.items!.eq(i);

                if(item.hasClass('p-noselection-option')) {
                    hide.push(item);
                } else {
                    let itemLabel = PrimeFaces.toSearchable(option.text(), lowercase, normalize);
                    if(this.filterMatcher!(itemLabel, filterValue)) {
                        show.push(item);
                    } else {
                        if(item.is('.p-selectonemenu-item-group-children')) {
                            itemLabel = PrimeFaces.toSearchable(option.parent().attr('label') as string, lowercase, normalize);
                            if (this.filterMatcher!(itemLabel, filterValue)) {
                                show.push(item);
                                continue;
                            }
                        }
                        hide.push(item);
                    }
                }
            }

            $.each(hide, function(i, o) { o.addClass('p-hidden').hide(); });
            $.each(show, function(i, o) { o.removeClass('p-hidden').show(); });
            hide = [];
            show = [];

            //Toggle groups
            const groups = this.itemsContainer!.find('li.p-selectonemenu-item-group');
            for(let g = 0; g < groups.length; g++) {
                const group = groups.eq(g);

                if(g === (groups.length - 1)) {
                    if(group.nextAll().filter('.p-selectonemenu-item-group-children:visible').length === 0)
                        hide.push(group);
                    else
                        show.push(group);
                }
                else if(group.nextUntil('li.p-selectonemenu-item-group').filter('.p-selectonemenu-item-group-children:visible').length === 0) {
                    hide.push(group);
                } else {
                    show.push(group);
                }
            }

            $.each(hide, function(i, o) { o.hide(); });
            $.each(show, function(i, o) { o.show(); });
        }

        const firstVisibleItem = this.items!.filter(':visible:not(.p-state-disabled):first');
        if(firstVisibleItem.length) {
            this.focusItem(firstVisibleItem);
            this.changeAriaValue(firstVisibleItem);
            PrimeFaces.scrollInView(this.itemsWrapper, firstVisibleItem);
        }

        if(this.itemsContainer!.height()! < this.cfg.initialHeight!) {
            this.itemsWrapper.css('height', 'auto');
        }
        else {
            this.itemsWrapper.height(this.cfg.initialHeight!);
        }

        this.alignPanel();
    }

    /**
     * Finds the value of the currently selected item, if any.
     * @returns The value of the currently selected item. Empty string if none is selected.
     */
    getSelectedValue(): string {
        return this.input.val() as string;
    }

    /**
     * Finds the label of the currently selected item, if any.
     * @returns The label of the currently selected item. Empty string if none is selected.
     */
    getSelectedLabel(): string {
        return this.options.filter(':selected').text();
    }

    /**
     * Finds the label of the option with the given value.
     * @param value The value of a selectable option.
     * @returns The label of the option with the given value.
     */
    private getLabelToDisplay(value: string): string {
        if(this.cfg.labelTemplate && value !== '&nbsp;') {
            return this.cfg.labelTemplate.replace('{0}', value);
        }
        return String(value);
    }

    /**
     * Adjusts the value of the aria attributes for the given selectable option.
     * @param item An option for which to set the aria attributes.
     */
    private changeAriaValue(item: JQuery): void {
        const itemId = item.attr('id');

        this.keyboardTarget.attr('aria-activedescendant', itemId as string);
        this.itemsContainer!.attr('aria-activedescendant', itemId as string);
    }

    /**
     * Loads the overlay panel with the selectable options, if dynamic mode is enabled.
     */
    private dynamicPanelLoad(): void {
        this.setQuerying(true);

        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            global: false,
            params: [{name: this.id + '_dynamicload', value: true}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        const $content = $($.parseHTML(content) as Element[]);

                        const $itemContent = $content.filter('ul, table');
                        this.itemsWrapper.empty();
                        this.itemsWrapper.append($itemContent);

                        const $select = $content.filter('select');
                        this.input.replaceWith($select);
                    }
                });

                return true;
            },
            oncomplete: () => {
                this.isDynamicLoaded = true;
                this.input = $(this.jqId + '_input');
                this.options = this.input.children('option');

                this.renderPanelContentFromHiddenSelect(false);

                this.initContents();
                this.bindItemEvents();
                this.setQuerying(false);
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Invokes the given method after making sure that the overlay panel was loaded (in case dynamic mode is enabled).
     * @param handleMethod Callback method to invoke after the dynamic overlay panel was loaded.
     * @param event An event that is passed to the callback.
     */
    private callHandleMethod<E extends JQuery.TriggeredEvent | null>(handleMethod: ((this: SelectOneMenu<Cfg>, event: E) => void) | null | undefined, event: E): void {
        if(this.cfg.dynamic && !this.isDynamicLoaded) {
            this.dynamicPanelLoad();

            const interval = setInterval(() => {
                if(this.isDynamicLoaded) {
                    if (handleMethod) {
                        handleMethod.call(this, event);
                    }

                    clearInterval(interval);
                }
            }, 10);
        }
        else {
            this.renderPanelContentFromHiddenSelect(true);

            if (handleMethod) {
                handleMethod.call(this, event);
            }
        }
    }

    /**
     * Sets the querying state.
     * @param state Querying state to set.
     */
    private setQuerying(state: boolean): void {
        if (state && !this.querying) {
            this.jq.addClass('p-state-loading');
            this.menuIcon.prepend('<span class="p-icon-loading p-icon p-loading-icon p-ajaxstatus-loading-icon"></span>');

        }
        else if (!state && this.querying) {
            this.jq.removeClass('p-state-loading');
            this.menuIcon.find('.p-icon-loading').remove();
        }
        this.querying = state;
    }

    /**
     * Renders panel content based on hidden select.
     * @param initContentsAndBindItemEvents `true` to call {@link initContents} and {@link bindItemEvents}
     * after rendering, `false` otherwise.
     */
    private renderPanelContentFromHiddenSelect(initContentsAndBindItemEvents: boolean): void {
         if (this.cfg.renderPanelContentOnClient && this.itemsWrapper.children().length === 0) {
             let panelContent = '<div id="' + this.id + '_items" class="p-selectonemenu-items p-widget-content p-widget" role="listbox">';
             panelContent += this.renderSelectItems(this.input);
             panelContent += '</div>';

             this.itemsWrapper.append(panelContent);

             if (initContentsAndBindItemEvents) {
                 this.initContents();
                 this.bindItemEvents();
             }
         }
    }

    /**
     * Renders panel HTML-code for all select items.
     * @param parentItem A parent item (select, optgroup) for which to render HTML code.
     * @param isGrouped Indicated whether the elements of the parent item should be marked as grouped.
     * @returns The rendered HTML string.
     */
    private renderSelectItems(parentItem: JQuery, isGrouped?: boolean): string {
        let content = "";
        isGrouped = isGrouped || false;

        const opts = parentItem.children("option, optgroup");
        const hasOptgroup = opts.filter("optgroup").length > 0;

        if (!hasOptgroup && !isGrouped) {
            content += '<ul role="group" class="p-selectonemenu-list p-widget-content p-widget">';
            content += opts.map((index, element) => {
                return this.renderSelectItem(element, isGrouped);
            }).get().join('');
            content += '</ul>';
        } else if (hasOptgroup && !isGrouped) {
            // Mixed root-level options and optgroups: wrap consecutive ungrouped options
            // in their own <ul> so all items share the same DOM depth as optgroup <ul>s.
            let pendingOptions: HTMLElement[] = [];
            opts.each((index, element) => {
                if (element.tagName === "OPTGROUP") {
                    if (pendingOptions.length > 0) {
                        content += '<ul role="group" class="p-selectonemenu-list p-widget-content p-widget">';
                        pendingOptions.forEach((opt) => {
                            content += this.renderSelectItem(opt, false);
                        });
                        content += '</ul>';
                        pendingOptions = [];
                    }
                    content += this.renderSelectItem(element, false);
                } else {
                    pendingOptions.push(element);
                }
            });
            if (pendingOptions.length > 0) {
                content += '<ul role="group" class="p-selectonemenu-list p-widget-content p-widget">';
                pendingOptions.forEach((opt) => {
                    content += this.renderSelectItem(opt, false);
                });
                content += '</ul>';
            }
        } else {
            content += opts.map((index, element) => {
                return this.renderSelectItem(element, isGrouped);
            }).get().join('');
        }

        return content;
    }

    /**
     * Renders panel HTML code for one select item (group).
     * @param item An option (group) element for which to render HTML code.
     * @param isGrouped Indicates whether the item is part of a group.
     * @returns The rendered HTML string.
     */
    private renderSelectItem(item: HTMLElement, isGrouped?: boolean): string {
        const $item = $(item);
        const isOptgroup = item.tagName === "OPTGROUP";
        const id = PrimeFaces.uuid();
        const escape = $item.data("escape");
        let label = (isOptgroup ? $item.attr("label") : $item.text()) as string;

        if (escape) {
            label = isOptgroup ? $("<div>").text(label).html() : $item.html();
            label = label === "&nbsp;" ? " " : label;
        }

        const cssClass = isOptgroup ? "p-selectonemenu-item-group" :
                       "p-selectonemenu-item p-selectonemenu-list-item" +
                       (isGrouped ? " p-selectonemenu-item-group-children" : "") +
                       ($item.data("noselection-option") ? " p-noselection-option" : "");

        let content = isOptgroup ? '<ul role="group" class="p-selectonemenu-list p-widget-content p-widget" aria-labelledby="' + id + '">' : '';

        content += '<li id="' + id + '" class="' + cssClass + '" tabindex="-1" role="' + (isOptgroup ? "presentation" : "option") + '"';

        const title = $item.data("title");
        if (title) {
            content += ' title="' + (escape ? PrimeFaces.escapeHTML(title) : title) + '"';
        }
        if ($item.is(':disabled')) {
            content += ' disabled';
        }

        const dataLabel = escape ? label.split('"').join('&quot;') : PrimeFaces.escapeHTML(label, true); // replaceAll, not in the ES2016 lib
        label = label === "&amp;nbsp;" ? "&nbsp;" : label;
        if (!isOptgroup && this.isBlankItemLabel(label)) {
            // an item without a label has no accessible name, so give screen readers something to announce
            content += ' aria-label="' + PrimeFaces.escapeHTML(this.getEmptyItemLabel(), true) + '"';
        }
        content += ' data-label="' + dataLabel + '" data-value="' + PrimeFaces.escapeHTML($item.val() as string, true) + '">' + label + '</li>';

        if (isOptgroup) {
            content += this.renderSelectItems($item, true) + '</ul>';
        }

        return content;
    }

    /**
     * Checks whether the given item label carries no text a screen reader could announce. An empty
     * `itemLabel` is rendered as `&nbsp;` by the server, so a blank label is not necessarily an empty string.
     * @param label The label of a select item.
     * @returns `true` if the label has no announceable text, or `false` otherwise.
     */
    private isBlankItemLabel(label: string | null | undefined): boolean {
        if (label === null || label === undefined) {
            return true;
        }
        // replaceAll, not in the ES2016 lib
        return String(label).split('&amp;nbsp;').join(' ').split('&nbsp;').join(' ').split('\xa0').join(' ').trim() === '';
    }

    /**
     * Returns the text a screen reader should announce for an item without a label. Defaults to the localized
     * `nullLabel` ARIA label ("Not Selected"), which can be overridden application-wide via the locale settings
     * or per component via `cfg.labels.aria.nullLabel`.
     * @returns The accessible text for an item without a label.
     */
    private getEmptyItemLabel(): string {
        return this.getAriaLabel('nullLabel');
    }

    /**
     * Builds the screen reader only text that gives the closed menu something to announce when the selected item
     * has no label. A screen reader takes the value of a combobox from its content, so an `aria-label` alone is
     * not enough: that is the accessible name, and it is ignored altogether once the menu is labelled by an
     * `outputLabel`, because `aria-labelledby` wins over `aria-label`.
     * @returns The markup for the screen reader only text of an item without a label.
     */
    private renderEmptyItemLabel(): string {
        return '<span class="p-sr-only">' + PrimeFaces.escapeHTML(this.getEmptyItemLabel()) + '</span>';
    }

    /**
     * Updates the style class of the label that indicates the currently selected item.
     * @param add `true` if a placeholder should be displayed, or `false` otherwise.
     */
    updatePlaceholderClass(add: boolean): void {
        if (add) {
            this.label.addClass('p-selectonemenu-label-placeholder');
        }
        else {
            this.label.removeClass('p-selectonemenu-label-placeholder');
        }
    }

}
