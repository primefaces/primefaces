/**
 * The configuration for the {@link SelectCheckboxMenu} widget.
 */
export interface SelectCheckboxMenuCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** The search expression for the element to which the overlay panel should be appended. */
    appendTo: string | null;
    /** Defines if filtering would be case sensitive. */
    caseSensitive: boolean;
    /** Defines if filtering would be done using normalized values. */
    filterNormalize: boolean;
    /**
     * Defines if dynamic loading is enabled for the element's panel. If the value is `true`, the overlay is not
     * rendered on page load to improve performance.
     */
    dynamic: boolean;
    /** Label to be shown in updateLabel mode when no item is selected. If not set the label is shown. */
    emptyLabel: string;
    /** Label to be shown in updateLabel mode when one or more items are selected. If not set the label is shown. */
    selectedLabel: string;
    /** `true` if the options can be filtered, or `false` otherwise. */
    filter: boolean;
    /** A custom filter function that is used when `filterMatchMode` is set to `custom`. */
    filterFunction: PrimeFacesTypes.widget.SelectCheckboxMenu.FilterFunction;
    /** Mode of the filter. When set to `custom`, a `filterFunction` must be specified. */
    filterMatchMode: PrimeFacesTypes.widget.SelectCheckboxMenu.FilterMatchMode;
    /** Placeholder text to show when filter input is empty. */
    filterPlaceholder: string;
    /** Initial height of the item container in pixels. */
    initialHeight: number;
    /** Separator for joining item labels if updateLabel is set to true. Default is `, `. */
    labelSeparator: string;
    /** Whether to show selected items as multiple labels. */
    multiple: boolean;
    /** Callback that is invoked when a checkbox option was checked or unchecked. */
    onChange: PrimeFacesTypes.widget.SelectCheckboxMenu.OnChangeCallback;
    /** Callback that is invoked when the overlay panel was hidden. */
    onHide: PrimeFacesTypes.widget.SelectCheckboxMenu.OnHideCallback;
    /** Callback that is invoked when the overlay panel was brought up. */
    onShow: PrimeFacesTypes.widget.SelectCheckboxMenu.OnShowCallback;
    /** Inline style of the overlay panel. */
    panelStyle: string;
    /** Style class of the overlay panel */
    panelStyleClass: string;
    /** Maximum height of the overlay panel. */
    scrollHeight: number;
    /** When enabled, the header of overlay panel is displayed. */
    showHeader: boolean;
    /** When enabled, the selected items are displayed on the label. */
    updateLabel: boolean;
    /** Renders panel content on client. */
    renderPanelContentOnClient: boolean;
}

/**
 * __PrimeFaces SelectCheckboxMenu Widget__
 *
 * SelectCheckboxMenu is a multi select component that displays options in an overlay.
 */
export class SelectCheckboxMenu<Cfg extends SelectCheckboxMenuCfg = SelectCheckboxMenuCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the checkboxes that can be selected. */
    checkboxes?: JQuery;
    /** The DOM element for the button that closes the overlay panel with the select options (when the panel is shown). */
    closer?: JQuery;
    /** The default label (text of the label on init), only with `updateLabel`. */
    defaultLabel?: string;
    /** Whether this widget is currently disabled. */
    disabled: boolean = false;
    /** DOM element of the input element used for entering a filter text, if filtering is enabled. */
    filterInput?: JQuery;
    /** DOM element of the wrapper that holds the input element used for entering a filter text, if filtering is enabled. */
    filterInputWrapper?: JQuery;
    /** The DOM elements for the headers of each option group. */
    groupHeaders?: JQuery;
    /** The filter that was selected and is currently used. */
    filterMatcher?: PrimeFacesTypes.widget.SelectCheckboxMenu.FilterFunction;
    /** Map between the available filter types and the filter implementation. */
    filterMatchers?: Record<PrimeFacesTypes.widget.SelectCheckboxMenu.FilterMatchMode, PrimeFacesTypes.widget.SelectCheckboxMenu.FilterFunction | undefined>;
    /** DOM element of the header of the overlay panel with the available select items, when the overlay panel is shown. */
    header?: JQuery;
    /** Unbind callback for the hide overlay handler. */
    hideOverlayHandler?: PrimeFacesTypes.Unbindable;
    /** The DOM elements for the hidden inputs for each checkbox option. */
    inputs: JQuery<HTMLInputElement> = $();
    /** When loading the panel with the available options lazily: if they have been loaded already. */
    isDynamicLoaded: boolean = false;
    /** The DOM elements for the the available checkbox options. */
    items?: JQuery;
    /** The DOM element for the container with the available checkbox options. */
    itemContainer?: JQuery;
    /** The DOM element for the wrapper with the container with the available checkbox options. */
    itemContainerWrapper: JQuery = $();
    /** The DOM element for the hidden input element that that can be selected via pressing tab. */
    keyboardTarget: JQuery = $();
    /** The DOM element for the label indicating the currently selected option. */
    label: JQuery = $();
    /** The DOM element for the container with the label indicating the currently selected option. */
    labelContainer: JQuery = $();
    /** ID of the label element that indicates the currently selected option. */
    labelId: string = "";
    /** The DOM element for the icon for bringing up the overlay panel. */
    menuIcon: JQuery = $();
    /** The DOM element for the container with the tags representing the selected options. */
    multiItemContainer?: JQuery;
    /** The DOM element for the overlay panel with the available checkbox options. */
    panel: JQuery = $();
    /** ID of the DOM element for the overlay panel with the available checkbox options. */
    panelId: string = "";
    /** Unbind callback for the resize handler. */
    resizeHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback for the scroll handler. */
    scrollHandler?: PrimeFacesTypes.Unbindable;
    /** Tab index of this widget. */
    tabindex?: string;
    /**
     * The wrapping DOM element of the toggler for selecting or unselecting all options in the overlay panel with all
     * selected items (when the overlay panel is shown).
     */
    toggler?: JQuery;
    /**
     * The DOM element with the toggler checkbox for selecting or unselecting all options in the overlay panel with all
     * selected items (when the overlay panel is shown).
     */
    togglerBox?: JQuery;
    /** The DOM element for the hidden input of the Select All checkbox. */
    selectAllTogglerCheckbox?: JQuery;
    /** Handler for CSS transitions used by this widget. */
    transition?: PrimeFacesTypes.CssTransitionHandler | null;
    /** The DOM elements for the buttons that can trigger (hide or show) the overlay panel with the available checkbox options. */
    triggers: JQuery = $();
    /** Whether the width of the overlay panel was aligned already. */
    widthAligned: boolean = false;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.labelContainer = this.jq.find('.p-selectcheckboxmenu-label-container');
        this.label = this.jq.find('.p-selectcheckboxmenu-label');
        this.menuIcon = this.jq.children('.p-selectcheckboxmenu-trigger');
        this.triggers = this.jq;
        this.disabled = this.jq.hasClass('p-state-disabled');
        //get the hidden checkboxes except that ones in overlay panel
        this.inputs = this.jq.children('div.p-hidden:not(.p-input-overlay)').children(':checkbox') as JQuery<HTMLInputElement>;
        this.panelId = this.jqId + '_panel';
        this.labelId = this.id + '_label';
        this.panel = $(this.panelId);
        this.widthAligned = false;
        this.itemContainerWrapper = this.panel.children('.p-selectcheckboxmenu-items-wrapper');
        this.keyboardTarget = $(this.jqId + '_focus');
        this.tabindex = this.keyboardTarget.attr('tabindex');
        this.cfg.showHeader = (this.cfg.showHeader === undefined) ? true : this.cfg.showHeader;
        this.cfg.dynamic = !!this.cfg.dynamic;
        this.isDynamicLoaded = false;
        this.cfg.labelSeparator = (this.cfg.labelSeparator === undefined) ? ', ' : this.cfg.labelSeparator;

        if (!this.disabled) {
            if (!this.cfg.dynamic) {
                this._renderPanel();
            }

            this.bindEvents();
            this.bindKeyEvents();

            if (!this.cfg.multiple) {
                this.label.attr('id', this.labelId);
                this.keyboardTarget.attr('aria-labelledby', this.labelId);
            }
            this.keyboardTarget.attr('aria-expanded', 'false');
        }

        this.renderLabel();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.refresh(cfg);
    }

    /**
     * Creates the overlay panel with the checkboxes for the selectable option, and also sets up the event listeners
     * for the panel.
     */
    private _renderPanel(): void {
        this.renderPanel();

        this.panel.find('a, input').attr('tabindex', this.tabindex as string);
        this.panel.find('.p-selectcheckboxmenu-item-input').attr('tabindex', "-1");

        this.checkboxes = this.itemContainer!.find('.p-chkbox-box:not(.p-state-disabled)');

        this.bindPanelContentEvents();
        this.bindPanelKeyEvents();

        this.isDynamicLoaded = true;
    }

    /**
     * Creates the overlay panel with the checkboxes for the selectable option.
     */
    private renderPanel(): void {
        //init panel content rendered by SelectCheckboxMenuRenderer
        this.header = this.panel.children('.p-selectcheckboxmenu-header');
        this.toggler = this.header.children('.p-chkbox');
        this.togglerBox = this.toggler.children('.p-chkbox-box');
        this.selectAllTogglerCheckbox = this.toggler.find('> div.p-hidden-accessible > input');
        this.filterInputWrapper = this.header.children('.p-selectcheckboxmenu-filter-container');
        this.filterInput = this.filterInputWrapper.children('.p-inputtext');
        this.closer = this.header.children('.p-selectcheckboxmenu-close');
        PrimeFaces.skinCloseAction(this.closer);

        if (this.cfg.renderPanelContentOnClient && this.itemContainerWrapper.children().length === 0) {
            this.renderItems();
        }
        else {
            //init items rendered by SelectCheckboxMenuRenderer
            this.itemContainer = this.itemContainerWrapper.children('.p-selectcheckboxmenu-items');
            this.items = this.itemContainer.find('.p-selectcheckboxmenu-item');
            this.groupHeaders = this.itemContainer.find('.p-selectcheckboxmenu-item-group');
        }

        if (this.cfg.scrollHeight) {
            this.itemContainerWrapper.css('max-height', this.cfg.scrollHeight);
        }
        else if (this.inputs.length > 10) {
            this.itemContainerWrapper.css('max-height', '200px');
        }

        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this as unknown as Parameters<typeof PrimeFaces.utils.resolveAppendTo>[0], this.jq, this.panel);

        PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
        this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'p-connected-overlay');

        this.keyboardTarget.attr('aria-controls', this.itemContainer!.attr('id') as string);
    }

    /**
     * Create the label to display values
     */
    private renderLabel(): void {
        if (!this.cfg.updateLabel) {
            this.registerTrigger();
            return;
        }
        if (this.cfg.multiple) {
            // Get or initialize the container for multiple items
            this.multiItemContainer = this.multiItemContainer || this.jq.children('.p-selectcheckboxmenu-multiple-container');
            const items = this.multiItemContainer.children();

            // If no items selected, show empty label
            if (!items.length) {
                const emptyLabel = this.multiItemContainer.data('label') || '&nbsp;';
                this.multiItemContainer.empty().append(`<li class="p-selectcheckboxmenu-emptylabel">${emptyLabel}</li>`);
            }
        }
        else if (this.cfg.updateLabel) {
            // For single select, store default label and add ellipsis styling
            this.defaultLabel = this.label.text();
            this.label.css({
                'text-overflow': 'ellipsis',
                'overflow': 'hidden'
            });

            this.updateLabel();
        }
        this.registerTrigger();
    }

    /**
     * Mark trigger and descandants of trigger as a trigger for a primefaces overlay.
     */
    private registerTrigger(): void {
        if (!this.disabled) {
            this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
        }
    }

    /**
     * Creates the individual checkboxes for each selectable option in the overlay panel.
     */
    private renderItems(): void {
        this.itemContainer = $('<ul id="'+this.id+'_list" class="p-selectcheckboxmenu-items p-selectcheckboxmenu-list p-widget-content p-widget"></ul>')
            .appendTo(this.itemContainerWrapper);

        //check if inputs must be grouped
        const grouped = this.inputs.filter('[data-group-label]');

        let currentGroupName: string | null | undefined = null;
        for (let i = 0; i < this.inputs.length; i++) {
            const input = this.inputs.eq(i),
                label = input.next(),
                disabled = input.is(':disabled'),
                checked = input.is(':checked'),
                title = input.attr('title'),
                escaped = input.data('escaped');
            let boxClass = 'p-chkbox-box p-widget p-state-default',
                itemClass = 'p-selectcheckboxmenu-item p-selectcheckboxmenu-list-item';

            if (grouped.length && currentGroupName !== input.attr('data-group-label')) {
                currentGroupName = input.attr('data-group-label');
                const itemGroup = $('<li class="p-selectcheckboxmenu-item-group p-selectcheckboxmenu-group-list-item"></li>');
                itemGroup.text(currentGroupName as string);
                this.itemContainer.append(itemGroup);
            }

            if (disabled) {
                boxClass += " p-state-disabled";
            }

            if (checked) {
                boxClass += " p-state-active";
            }

            const iconClass = checked ? 'p-chkbox-icon p-icon p-checkbox-icon p-checkbox-checked-icon' : 'p-chkbox-icon p-icon p-checkbox-icon';
            itemClass = checked ? itemClass + ' p-selectcheckboxmenu-checked' : itemClass + ' p-selectcheckboxmenu-unchecked';

            const item = $('<li class="' + itemClass + '"></li>');
            item.append('<div class="p-chkbox p-widget"><div class="p-hidden-accessible"><input type="checkbox"></input></div>' +
                '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div></div>');

            const uuid = PrimeFaces.uuid();
            const itemLabel = $('<label for=' + uuid + '></label>'),
                labelHtml = label.html().trim(),
                labelLength = labelHtml.length;
            if (labelHtml === '&nbsp;') {
                itemLabel.html('&nbsp;');
            } else if (labelLength > 0) {
                escaped ? itemLabel.text(label.text()) : itemLabel.html(label.html());
            } else {
                itemLabel.text(input.val() as string);
            }

            itemLabel.appendTo(item);

            if (title) {
                item.attr('title', title);
            }

            if (this.cfg.multiple) {
                item.attr('data-item-value', input.val() as string);
            }

            item.find('> .p-chkbox > .p-hidden-accessible > input')
                .prop('checked', checked)
                .attr('class', 'p-selectcheckboxmenu-item-input')
                .attr('role', 'option')
                .attr('aria-selected', String(checked))
                .attr('aria-setsize', this.inputs.length)
                .attr('aria-posinset', i + 1)
                .attr('id', uuid);
            this.itemContainer.attr('role', 'listbox').attr('aria-multiselectable', 'true');

            this.itemContainer.append(item);
        }

        this.items = this.itemContainer.children('li.p-selectcheckboxmenu-item');
        this.groupHeaders = this.itemContainer.children('li.p-selectcheckboxmenu-item-group');
    }

    /**
     * Sets up all event listeners required by this widget.
     */
    private bindEvents(): void {
        //Events to show/hide the panel
        this.triggers.on('mouseenter.selectCheckboxMenu', () => {
            if (!this.disabled) {
                this.jq.addClass('p-state-hover');
                this.triggers.addClass('p-state-hover');
            }
        }).on('mouseleave.selectCheckboxMenu', () => {
            if (!this.disabled) {
                this.jq.removeClass('p-state-hover');
                this.triggers.removeClass('p-state-hover');
            }
        }).on('click.selectCheckboxMenu', (e) => {
            if (!this.disabled) {
                if (this.cfg.multiple && $(e.target).is('.p-selectcheckboxmenu-token-icon')) {
                    return;
                }

                if (!this.isLoaded()) {
                    this._renderPanel();
                }

                this.togglePanel();
            }

            this.keyboardTarget.trigger('focus');
            e.preventDefault();
        });

        if (this.cfg.multiple) {
            this.bindMultipleModeEvents();
        }

        //Client Behaviors
        if (this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors as unknown as Parameters<typeof PrimeFaces.attachBehaviors>[1]);
        }
    }

    /**
     * Sets up the event listeners for the overlay panel with the selectable checkbox options.
     */
    private bindPanelContentEvents(): void {
        //Events for checkboxes
        this.bindCheckboxHover(this.checkboxes!);

        //Toggler
        if (this.cfg.showHeader) {
            this.bindCheckboxHover(this.togglerBox!);
            this.togglerBox!.on('click.selectCheckboxMenu', () => {
                this.selectAllTogglerCheckbox!.trigger('focus').trigger('change');
            });

            //filter
            if (this.cfg.filter) {
                this.setupFilterMatcher();

                PrimeFaces.skinInput(this.filterInput!);

                this.filterInput!.on('keyup.selectCheckboxMenu', (e) => {
                    this.filter($(e.currentTarget).val() as string);
                }).on('keydown.selectCheckboxMenu', (e) => {
                    switch (e.key) {
                        case 'Escape':
                            this.hide();
                            e.preventDefault();
                            break;
                        case 'ArrowDown':
                            this.focusFirstItem();
                            e.stopPropagation();
                            e.preventDefault();
                            break;
                        case 'Tab':
                            this.focusFirstItem();
                            e.stopPropagation();
                            e.preventDefault();
                            break;
                    }
                });
            }
        }

        //Checkboxes and labels
        this.items!.on('click.selectCheckboxMenu', (e) => {
            const checkbox = $(e.currentTarget).find('.p-chkbox').children('.p-chkbox-box');
            this.toggleItem(checkbox);
            checkbox.removeClass('p-state-hover');
            PrimeFaces.clearSelection();
            e.preventDefault();
        });
    }

    /**
     * Sets up all panel event listeners
     */
    private bindPanelEvents(): void {
        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            () => this.triggers,
            (e, eventTarget) => {
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
     * Sets up the event listeners for all keyboard related events other than the overlay panel, such as pressing space
     * to bring up the overlay panel.
     */
    private bindKeyEvents(): void {
        this.keyboardTarget.on('focus.selectCheckboxMenu', () => {
            this.jq.addClass('p-state-focus');
            this.menuIcon.addClass('p-state-focus');
        }).on('blur.selectCheckboxMenu', () => {
            this.jq.removeClass('p-state-focus');
            this.menuIcon.removeClass('p-state-focus');
        }).on('keydown.selectCheckboxMenu', (e) => {
            if (!this.isLoaded()) {
                this._renderPanel();
            }

            const isPopupVisible = this.panel.is(":visible");
            const isPopupHidden = !isPopupVisible;

            switch (e.code) {
                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    if (isPopupHidden) {
                        this.show();
                        e.stopPropagation(); // GitHub #8340
                    }
                    e.preventDefault();
                    break;
                case 'ArrowDown':
                    if (isPopupHidden) {
                        this.show();
                    }
                    e.preventDefault();
                    break;

                case 'ArrowUp':
                    if (isPopupHidden) {
                        this.show('.p-selectcheckboxmenu-item-input:focusable:last');
                    }
                    else if (e.altKey || e.shiftKey) {
                        this.hide();
                    }

                    e.preventDefault();
                    break;
                case 'Escape':
                    // Dismisses the popup if it is visible. Optionally, if the popup is hidden before Escape is pressed, clears the combobox.
                    if (isPopupVisible) {
                         this.hide();
                    }
                    else {
                         this.resetValue(false);
                    }
                    e.preventDefault();
                    break;
            };
        });
    }

    /**
     * Sets up the keyboard event listeners for the given checkbox options.
     * @param items Checkbox options for which to add the event listeners.
     */
    private bindCheckboxKeyEvents(items: JQuery): void {
        items.on('focus.selectCheckboxMenu', (e) => {
            const input = $(e.currentTarget),
                box = input.parent().next();

            box.addClass('p-state-focus');

            PrimeFaces.scrollInView(this.itemContainerWrapper, box);
        }).on('blur.selectCheckboxMenu', (e) => {
            const input = $(e.currentTarget),
                box = input.parent().next();

            box.removeClass('p-state-focus');
        }).on('keydown.selectCheckboxMenu', (e) => {
            const currentCheckbox = $(e.currentTarget);
            let index = 0;
            switch (e.code) {
                case 'Tab':
                    this.focusFirstItem(e.shiftKey ?  ':focusable:first': '.p-selectcheckboxmenu-close');
                    e.preventDefault();
                    break;
                case 'Space':
                    if (!e.isDefaultPrevented()) {
                        this.toggleItem(currentCheckbox.parent().next());
                    }
                    e.preventDefault();
                    break;
                case 'Enter':
                case 'NumpadEnter':
                    this.toggleItem(currentCheckbox.parent().next());
                    e.preventDefault();
                    break;
                case 'PageUp':
                case 'Home':
                    items.first().trigger('focus');
                    e.preventDefault();
                    break;
                case 'PageDown':
                case 'End':
                    items.last().trigger('focus');
                    e.preventDefault();
                    break;
                case 'ArrowUp':
                    if (e.altKey || e.shiftKey) {
                        this.hide();
                    }
                    else {
                        index = items.index(currentCheckbox) - 1;
                        while (index >= 0 && !items.eq(index).is(':visible')) {
                            index--;
                        }
                        if (index >= 0) {
                            items.eq(index).trigger('focus');
                        }
                    }
                    e.preventDefault();
                    break;
                case 'ArrowDown':
                    index = items.index(currentCheckbox) + 1;
                    while (index < items.length && !items.eq(index).is(':visible')) {
                        index++;
                    }
                    if (index < items.length) {
                        items.eq(index).trigger('focus');
                    }
                    e.preventDefault();
                    break;
                case 'Escape':
                    this.hide();
                    break;
            }
        });
    }

    /**
     * Configures the ARIA label for the select all checkbox.
     */
    private configureSelectAllAria(): void {
        if (this.selectAllTogglerCheckbox) {
           const ariaLabel = this.selectAllTogglerCheckbox.prop('checked') ? this.getAriaLabel('selectAll') : this.getAriaLabel('unselectAll');
           this.selectAllTogglerCheckbox.attr('aria-label', ariaLabel);
           this.selectAllTogglerCheckbox.removeAttr('aria-selected');
        }
    }

    /**
     * Sets up the event listeners for all keyboard related events of the overlay panel, such as pressing space to
     * toggle a checkbox.
     */
    private bindPanelKeyEvents(): void {
        if (this.cfg.showHeader) {
            //Closer
            this.closer!.on({
                'focus.selectCheckboxMenu': () => {
                    this.closer!.addClass('p-state-focus');
                },
                'mouseenter.selectCheckboxMenu': () => {
                    this.closer!.addClass('p-state-hover');
                },
                'blur.selectCheckboxMenu': () => {
                    this.closer!.removeClass('p-state-focus');
                },
                'mouseleave.selectCheckboxMenu': () => {
                    this.closer!.removeClass('p-state-hover');
                },
                'click.selectCheckboxMenu': (e: JQuery.TriggeredEvent) => {
                    this.hide();
                    e.preventDefault();
                },
                'keydown.selectCheckboxMenu': (e: JQuery.KeyDownEvent) => {
                    switch (e.code) {
                        case 'Enter':
                        case 'Space':
                        case 'Escape':
                            this.hide();
                            e.preventDefault();
                            break;
                        case 'Tab':
                            if (e.shiftKey) {
                                this.focusFirstItem();
                            }
                            else {
                                this.selectAllTogglerCheckbox!.trigger('focus');
                            }
                            e.preventDefault();
                            break;
                    }
                }
            });

            this.configureSelectAllAria();
            this.selectAllTogglerCheckbox!.on({
                'focus.selectCheckboxMenu': (e: JQuery.TriggeredEvent) => {
                    $(e.currentTarget).parent().next().addClass('p-state-focus');
                },
                'blur.selectCheckboxMenu': (e: JQuery.TriggeredEvent) => {
                    $(e.currentTarget).parent().next().removeClass('p-state-focus');
                },
                'click.selectCheckboxMenu': (e: JQuery.TriggeredEvent) => {
                    // for screenreader click must be implemented
                    $(e.currentTarget).trigger('change');
                },
                'keydown.selectCheckboxMenu': (e: JQuery.KeyDownEvent) => {
                    switch (e.code) {
                        case 'Enter':
                        case 'Space':
                            $(e.currentTarget).trigger('change');
                            e.preventDefault();
                            break;
                        case 'Escape':
                            this.hide();
                            break;
                        case 'Tab':
                            if (e.shiftKey) {
                                this.closer!.trigger('focus');
                                e.preventDefault();
                            }
                            break;
                        case 'ArrowDown':
                            this.focusFirstItem();
                            e.stopPropagation();
                            e.preventDefault();
                            break;
                    }
                },
                'change.selectCheckboxMenu': (e: JQuery.TriggeredEvent) => {
                    this.toggleSelection(!$(e.currentTarget).prop('checked'));
                    $(e.currentTarget).removeAttr('aria-selected');
                }
            });
        }

        //".p-chkbox" is a grandchild when columns are used!
        const itemKeyInputs = this.items!.find('div.p-chkbox > div.p-hidden-accessible > input');
        this.bindCheckboxKeyEvents(itemKeyInputs);
        itemKeyInputs.on('change.selectCheckboxMenu', (e) => {
            const input = $(e.currentTarget),
                box = input.parent().next();
            this.toggleItem(box, input);
        });
    }

    /**
     * When multi select mode is enabled: Sets up the event listeners for the overlay panel.
     */
    private bindMultipleModeEvents(): void {
        this.multiItemContainer = this.jq.children('.p-selectcheckboxmenu-multiple-container');

        const closeSelector = '> li.p-selectcheckboxmenu-token > .p-selectcheckboxmenu-token-icon';
        this.multiItemContainer.off('click', closeSelector).on('click', closeSelector, null, (e) => {
            if (!this.isLoaded()) {
                this._renderPanel();
            }
            const itemValue = $(e.currentTarget).parent().data("item-value");
            const item = this.items!.filter('[data-item-value="' + CSS.escape(itemValue) + '"]');
            if (item && item.length) {
                //".p-chkbox" is a grandchild when columns are used!
                this.uncheck(item.find('.p-chkbox').children('.p-chkbox-box'), true);
            }

            e.stopPropagation();
        });
    }

    /**
     * Sets up the event listeners for hovering over the checkboxes. Adds the appropriate hover style classes.
     * @param item A checkbox for which to add the event listeners.
     */
    private bindCheckboxHover(item: JQuery): void {
        item.on('mouseenter.selectCheckboxMenu', (e) => {
            const item = $(e.currentTarget);
            if (!item.hasClass('p-state-disabled')) {
                item.addClass('p-state-hover');
            }
        }).on('mouseleave.selectCheckboxMenu', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        });
    }

    /**
     * Filters the available options in the overlay panel by the given search value. Note that this does not bring up
     * the overlay panel, use `show` for that.
     * @param value A value against which the available options are matched.
     */
    filter(value: string): void {
        this.cfg.initialHeight = this.cfg.initialHeight||this.itemContainerWrapper.height();
        const lowercase = !this.cfg.caseSensitive,
                normalize = !!this.cfg.filterNormalize,
                filterValue = PrimeFaces.toSearchable(PrimeFaces.trim(value), lowercase, normalize);

        if (filterValue === '') {
            this.items!.filter(':hidden').show();
            this.groupHeaders!.show();
        }
        else {
            for (let i = 0; i < this.inputs.length; i++) {
                const input = this.inputs.eq(i),
                    label = input.next(),
                    itemLabel = PrimeFaces.toSearchable(label.text(), lowercase, normalize),
                    item = this.items!.eq(i);

                if(item.hasClass('p-noselection-option')) {
                    item.hide();
                }
                else {
                    if(this.filterMatcher!(itemLabel, filterValue)) {
                        item.show();
                    }
                    else {
                        item.hide();
                    }
                }
            }

            for (const groupHeader of this.groupHeaders!) {
                const header = $(groupHeader),
                    groupedItems = header.nextUntil('.p-selectcheckboxmenu-item-group');

                if (groupedItems.length === groupedItems.filter(':hidden').length) {
                    header.hide();
                }
                else {
                    header.show();
                }
            }
        }

        // update ARIA
        const selectItems = this.items!.filter(':visible:not(.p-state-disabled)');
        selectItems.each(function(index, selectItem) {
            $(selectItem).find('input').attr({
                'aria-setsize': selectItems.length,
                'aria-posinset': index + 1
            });
        });

        const firstVisibleItem = selectItems.first();
        if (firstVisibleItem.length) {
            PrimeFaces.scrollInView(this.itemContainerWrapper, firstVisibleItem);
        }

        if(this.itemContainer!.height()! < this.cfg.initialHeight!) {
            //shrink to fit filtered content size
            this.itemContainerWrapper.css('height', 'auto');
        }
        else {
            //resize to initial (max.) height
            this.itemContainerWrapper.height(this.cfg.initialHeight!);
        }

        this.updateToggler();
        this.alignPanel();
    }

    /**
     * Finds and stores the filter function which is to be used for filtering the options of this select checkbox menu.
     */
    private setupFilterMatcher(): void {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode || 'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            , 'contains': this.containsFilter
            , 'endsWith': this.endsWithFilter
            , 'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    }

    /**
     * Implementation of a `PrimeFaces.widget.SelectCheckboxMenu.FilterFunction` that matches the given option when it
     * starts with the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the options starts with the filter value, or `false` otherwise.
     */
    startsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter) === 0;
    }

    /**
     * Implementation of a `PrimeFaces.widget.SelectCheckboxMenu.FilterFunction` that matches the given option when it
     * contains the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the contains the filter value, or `false` otherwise.
     */
    containsFilter(value: string, filter: string): boolean {
        return value.indexOf(filter) !== -1;
    }

    /**
     * Implementation of a `PrimeFaces.widget.SelectCheckboxMenu.FilterFunction` that matches the given option when it
     * ends with the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the options ends with the filter value, or `false` otherwise.
     */
    endsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    }

    /**
     * Toggles either selecting all items or unselecting all items.
     * @param selectAll true to select all items and false to uncheck all items
     */
    toggleSelection(selectAll: boolean): void {
        if (selectAll) {
            this.checkAll();
        }
        else {
            this.uncheckAll();
        }
        this.configureSelectAllAria();
    }

    /**
     * Selects all available options.
     * @param silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    checkAll(silent?: boolean): void {
        if (!this.isLoaded()) {
            this._renderPanel();
        }
        const panelVisible = this.panel.is(':visible');
        for (let i = 0; i < this.items!.length; i++) {
            const el = this.items!.eq(i);
            if (!panelVisible || (panelVisible && el.is(':visible'))) {
                const input = this.inputs.eq(i);
                const inputNative = input[0]!;

                if (!inputNative.disabled) {
                    input.prop('checked', true).attr('aria-selected', 'true');
                    //".p-chkbox" is a grandchild when columns are used!
                    this.check(el.find('.p-chkbox').children('.p-chkbox-box'));

                    if (this.cfg.multiple) {
                        this.createMultipleItem(el);
                    }
                }
            }
        }

        this.check(this.togglerBox!);

        if (!silent) {
            const togglerInput = this.togglerBox!.prev().children('input');
            if (this.cfg.onChange) {
                this.cfg.onChange.call(this);
            }

            if (!this.togglerBox!.hasClass('p-state-disabled')) {
                togglerInput.trigger('focus.selectCheckboxMenu');
                this.togglerBox!.addClass('p-state-active');
            }

            if (this.cfg.multiple) {
                this.alignPanel();
            }

            this.fireToggleSelectEvent(true);
        }
    }

    /**
     * Unselects all available options.
     * @param silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    uncheckAll(silent?: boolean): void {
        if (!this.isLoaded()) {
            this._renderPanel();
        }
        const panelVisible = this.panel.is(':visible');
        for (let i = 0; i < this.items!.length; i++) {
            const el = this.items!.eq(i);
            if (!panelVisible || (panelVisible && el.is(':visible'))) {
                const input = this.inputs.eq(i);
                const inputNative = input[0]!;

                if (!inputNative.disabled) {
                    this.inputs.eq(i).prop('checked', false).attr('aria-selected', 'false');
                    //".p-chkbox" is a grandchild when columns are used!
                    this.uncheck(el.find('.p-chkbox').children('.p-chkbox-box'));

                    if (this.cfg.multiple) {
                        this.removeMultipleItem(el);
                    }
                }
            }
        }


        this.uncheck(this.togglerBox!);

        if (!silent) {
            const togglerInput = this.togglerBox!.prev().children('input');
            if (this.cfg.onChange) {
                this.cfg.onChange.call(this);
            }

            if (!this.togglerBox!.hasClass('p-state-disabled')) {
                togglerInput.trigger('focus.selectCheckboxMenu');
            }

            if (this.cfg.multiple) {
                this.alignPanel();
            }

            this.fireToggleSelectEvent(false);
        }

        this.renderLabel();
    }

    /**
     * Triggers the select behavior, if any, when a checkbox option was selected or unselected.
     * @param checked Whether the checkbox option is now checked.
     */
    private fireToggleSelectEvent(checked: boolean): void {
        if (this.hasBehavior('toggleSelect')) {
            const ext = {
                params: [{ name: this.id + '_checked', value: checked }]
            };

            this.callBehavior('toggleSelect', ext);
        }
    }

    /**
     * Selects the given checkbox option.
     * @param checkbox Checkbox option to select.
     * @param updateInput If `true`, update the hidden input field with the current value of this widget.
     */
    private check(checkbox: JQuery, updateInput?: boolean): void {
        if (!checkbox.hasClass('p-state-disabled')) {
            const checkedInput = checkbox.prev().children('input'),
                item = checkbox.closest('.p-selectcheckboxmenu-item');

            checkedInput.prop('checked', true).attr('aria-selected', 'true');
            if (updateInput) {
                checkedInput.trigger('focus.selectCheckboxMenu');
            }

            checkbox.addClass('p-state-active').children('.p-chkbox-icon').addClass('p-checkbox-checked-icon');
            item.removeClass('p-selectcheckboxmenu-unchecked').addClass('p-selectcheckboxmenu-checked');

            if (updateInput) {
                const itemGroups = item.prevAll('.p-selectcheckboxmenu-item-group'),
                    input = this.inputs.eq(item.index() - itemGroups.length);
                input.prop('checked', true).attr('aria-selected', 'true').trigger('change');

                this.updateToggler();

                if (this.cfg.multiple) {
                    this.createMultipleItem(item);
                    this.alignPanel();
                }

                if (this.hasBehavior('itemSelect')) {
                    const ext = {
                        params: [
                            { name: this.id + '_itemSelect', value: item.data("item-value") }
                        ]
                    };

                    this.callBehavior('itemSelect', ext);
                }
            }

            this.updateLabel();
        }
    }

    /**
     * Unselects the given checkbox option.
     * @param checkbox Checkbox option to unselect.
     * @param updateInput If `true`, update the hidden input field with the current value of this widget.
     */
    private uncheck(checkbox: JQuery, updateInput?: boolean): void {
        if (!checkbox.hasClass('p-state-disabled')) {
            const uncheckedInput = checkbox.prev().children('input'),
                item = checkbox.closest('.p-selectcheckboxmenu-item');
            checkbox.removeClass('p-state-active').children('.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
            checkbox.closest('.p-selectcheckboxmenu-item').addClass('p-selectcheckboxmenu-unchecked').removeClass('p-selectcheckboxmenu-checked');
            uncheckedInput.prop('checked', false).attr('aria-selected', 'false');

            if (updateInput) {
                const itemGroups = item.prevAll('.p-selectcheckboxmenu-item-group'),
                    input = this.inputs.eq(item.index() - itemGroups.length);
                input.prop('checked', false).attr('aria-selected', 'false').trigger('change');
                uncheckedInput.trigger('focus.selectCheckboxMenu');
                this.updateToggler();

                if (this.cfg.multiple) {
                    this.removeMultipleItem(item);
                    this.alignPanel();
                }

                if (this.hasBehavior('itemUnselect')) {
                    const ext = {
                        params: [
                            { name: this.id + '_itemUnselect', value: item.data("item-value") }
                        ]
                    };

                    this.callBehavior('itemUnselect', ext);
                }
            }

            this.updateLabel();
        }
    }

    /**
     * Focuses the first focusable item in the select checkbox menu panel.
     * This method queues the focus action to ensure it is executed at the right time in the event loop.
     * @param focusSelector The selector to use to focus the first item in the panel.
     * @param delay Delay in ms before focusing
     */
    focusFirstItem(focusSelector?: string, delay: number = 0): void {
        focusSelector = focusSelector || '.p-selectcheckboxmenu-item-input:focusable:first';
        PrimeFaces.queueTask(() => this.panel.find(focusSelector).trigger('focus'), delay);
    }

    /**
     * Bring up the overlay panel if its not showing or hide it if it is showing.
     */
    togglePanel(): void {
        if (this.panel.is(":hidden")) {
            this.show();
        }
        else {
            this.hide();
        }
    }

    /**
     * Brings up the overlay panel with the available checkbox options.
     * @param focusSelector The selector to use to focus the first item in the panel.
     */
    show(focusSelector?: string): void {
        if (this.panel.is(":hidden") && this.transition) {
            this.transition.show({
                onEnter: () => {
                    PrimeFaces.nextZindex(this.panel);
                    this.alignPanel();
                },
                onEntered: () => {
                    this.keyboardTarget.attr('aria-expanded', 'true');
                    this.postShow();
                    this.bindPanelEvents();
                    this.focusFirstItem(focusSelector);
                }
            });
        }
    }

    /**
     * Hides the overlay panel with the available checkbox options.
     */
    hide(): void {
        if (this.panel.is(':visible') && this.transition) {
            this.transition.hide({
                onExit: () => {
                    this.unbindPanelEvents();
                },
                onExited: () => {
                    this.keyboardTarget.attr('aria-expanded', 'false');
                    this.postHide();
                }
            });

            this.keyboardTarget.trigger('focus');
        }
    }

    /**
     * Callback that is invoked after the overlay panel with the checkbox options was made visible.
     */
    private postShow(): void {
        if (this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    }

    /**
     * Callback that is invoked after the overlay panel with the checkbox options was hidden.
     */
    private postHide(): void {
        if (this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    }

    /**
     * Align the overlay panel with the available checkbox options so that is is positioned next to the the button.
     */
    alignPanel(): void {
        const panelStyle = this.panel.attr('style');

        this.panel.css({
            'left': '',
            'top': '',
            'z-index': PrimeFaces.nextZindex(),
            'transform-origin': 'center top'
        });

        if (this.panel.parent().attr('id') === this.id) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px'
            });
        }
        else {
            PrimeFaces.utils.position(this.panel, {
                my: 'left top'
                , at: 'left bottom'
                , of: this.jq
                , collision: 'flipfit'
                , using: function(pos: { top: number; left: number }, directions: PrimeFacesTypes.utils.PositionFeedback) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }

        if (!this.widthAligned && (this.panel.width()! < this.jq.width()!) && (!panelStyle || panelStyle.toLowerCase().indexOf('width') === -1)) {
            this.panel.width(this.jq.width()!);
            this.widthAligned = true;
        }
    }

    /**
     * Select or unselect the given checkbox option.
     * @param checkbox One of the checkbox options of this widget to toggle.
     * @param input (optional) input element representing the value of the checkbox
     */
    toggleItem(checkbox: JQuery, input?: JQuery): void {
        if (!checkbox.hasClass('p-state-disabled')) {
            const isChecked = !input || input.prop('checked');
            if (checkbox.hasClass('p-state-active') && isChecked) {
                this.uncheck(checkbox, true);
            }
            else {
                this.check(checkbox, true);
            }
        }
    }

    /**
     * Updates the `select all` / `unselect all` toggler so that it reflects the currently selected options.
     */
    private updateToggler(): void {
        if (this.cfg.showHeader) {
            const visibleItems = this.items!.filter(':visible');

            if (visibleItems.length && visibleItems.filter('.p-selectcheckboxmenu-unchecked').length === 0) {
                this.check(this.togglerBox!);
            }
            else {
                this.uncheck(this.togglerBox!);
            }
        }
    }

    /**
     * When multi mode is disabled: Upates the label that indicates the currently selected item.
     */
    private updateLabel(): void {
        const checkedItems = this.jq.find(':checked');
        let labelText = '';

        if (checkedItems && checkedItems.length) {
            // Convert checkedItems to an array if it's not already
            const checkedArray = Array.from(checkedItems);
            // Generate label text
            labelText = this.cfg.selectedLabel
                ? this.cfg.selectedLabel
                : checkedArray.map(item => $(item).next().text() || '').join(this.cfg.labelSeparator || '');

             // screen reader set to current values to be read aloud
             this.keyboardTarget.val(labelText);

            this.labelContainer.addClass('p-state-active');
        } else {
            labelText = (typeof this.cfg.emptyLabel !== 'undefined') ? this.cfg.emptyLabel : (this.defaultLabel || '');
            this.keyboardTarget.val(labelText);
            this.labelContainer.removeClass('p-state-active');
        }

        const showLabel = this.cfg.updateLabel && !(this.cfg.multiple && labelText.length > 0);
        const showSelectedLabel = this.cfg.updateLabel && this.cfg.selectedLabel;

        if (showLabel || showSelectedLabel) {
             this.label.text(labelText);
             this.labelContainer.attr('title', labelText);
        }
        this.registerTrigger();
    }

    /**
     * When multi mode is enabled: Creates a tag for the given item that was checked.
     * @param item The checkbox item that was checked.
     */
    private createMultipleItem(item: JQuery): void {
        const items = this.multiItemContainer!.children();
        const itemCount = !items.length ? 0 : items.filter('[data-item-value="' + CSS.escape(item.data('item-value')) + '"]').length;
        if (itemCount > 0) {
            return;
        }

        const itemGroups = item.prevAll('.p-selectcheckboxmenu-item-group'),
            inputIndex = item.index() - itemGroups.length,
            input = this.inputs.eq(inputIndex),
            escaped = input.data('escaped'),
            labelHtml = input.next().html().trim();
        let label = '';

        if (labelHtml === '&nbsp;') {
            label = '&nbsp;';
        } else if (labelHtml.length > 0) {
            label = escaped ? PrimeFaces.escapeHTML(input.next().text()) : input.next().html();
        } else {
            label = PrimeFaces.escapeHTML(input.val() as string);
        }

        const itemDisplayMarkup = `<li class="p-selectcheckboxmenu-token p-state-active" data-item-value="${PrimeFaces.escapeHTML(input.val() as string)}">` +
                                `<span class="p-selectcheckboxmenu-token-icon p-icon p-close-icon p-selectcheckboxmenu-chip-remove-icon"></span>` +
                                `<span class="p-selectcheckboxmenu-token-label">${label}</span></li>`;

        if (items.filter('[class="p-selectcheckboxmenu-emptylabel"]').length) {
            this.multiItemContainer!.empty();
        }
        this.multiItemContainer!.append(itemDisplayMarkup);
    }

    /**
     * When multi mode is enabled: Removes all visible tags with the same value as the given checkbox item.
     * @param item Checkbox item that was unchecked.
     */
    private removeMultipleItem(item: JQuery): void {
        const items = this.multiItemContainer!.children();
        if (items.length) {
            items.filter('[data-item-value="' + CSS.escape(item.data('item-value')) + '"]').remove();
        }
        // update the label if there are no more items to display empty
        this.renderLabel();
    }

    /**
     * Checks the checkbox option with the given value.
     * @param value Value of the option to check.
     */
    selectValue(value: string): void {
        let idx = -1;
        // find input-index
        for (let i = 0; i < this.inputs.length; i++) {
            if (this.inputs.eq(i).val() === value) {
                idx = i;
                break;
            }
        }
        if (idx === -1) {
            return;
        }
        const input = this.inputs.eq(idx);   // the hidden input
        const item = this.items!.eq(idx);    // the Overlay-Panel-Item (li)

        // check (see this.checkAll())
        input.prop('checked', true).attr('aria-selected', 'true');
        //".p-chkbox" is a grandchild when columns are used!
        this.check(item.find('.p-chkbox').children('.p-chkbox-box'));

        if (this.cfg.multiple) {
            this.createMultipleItem(item);
        }
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.inputs);
        this.disabled = false;
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.inputs);
        this.disabled = true;
    }

    /**
     * Has the panel been loaded with checkbox data yet?
     * @returns `true` when the panel has been loaded with checkbox items
     */
    isLoaded(): boolean {
        return this.cfg.dynamic === false || this.isDynamicLoaded;
    }

    /**
     * Resets the input.
     * @param silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    resetValue(silent?: boolean): void {
        if (this.isLoaded()) {
            this.uncheckAll(silent);
        }
    }

}
