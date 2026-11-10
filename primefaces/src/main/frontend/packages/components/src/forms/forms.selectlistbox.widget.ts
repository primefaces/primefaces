/**
 * The configuration for the {@link SelectListbox} widget.
 */
export interface SelectListboxCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether filtering is case-sensitive. */
    caseSensitive: boolean;
    /** Whether filtering uses normalized values (accents removed). */
    filterNormalize: boolean;
    /** Whether the options can be filtered. */
    filter: boolean;
    /** The filter function used with `filterMatchMode` `custom`. */
    filterFunction: PrimeFacesTypes.widget.SelectListbox.FilterFunction;
    /** Mode of the filter, normalized to `startsWith` on init if not set. */
    filterMatchMode: PrimeFacesTypes.widget.SelectListbox.FilterMatchMode;
}

/**
 * __PrimeFaces SelectListbox Widget__
 *
 * Base class for the listbox widgets, {@link SelectOneListbox} and {@link SelectManyMenu}.
 */
export class SelectListbox<Cfg extends SelectListboxCfg = SelectListboxCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The hidden select with the value. */
    input: JQuery<HTMLSelectElement> = $();
    /** The container of the list. */
    listContainer: JQuery = $();
    /** The list of items. */
    listElement: JQuery = $();
    /** The options of the hidden select. */
    options: JQuery<HTMLOptionElement> = $();
    /** All items, including the disabled ones. */
    allItems: JQuery = $();
    /** The enabled items. */
    items: JQuery = $();
    /** The filter input, only with `filter`. */
    filterInput?: JQuery;
    /** The filter functions per filter match mode. */
    filterMatchers: Record<PrimeFacesTypes.widget.SelectListbox.FilterMatchMode, PrimeFacesTypes.widget.SelectListbox.FilterFunction | undefined> = {
        startsWith: undefined,
        contains: undefined,
        endsWith: undefined,
        custom: undefined
    };
    /** The filter function of the current filter match mode. */
    filterMatcher?: PrimeFacesTypes.widget.SelectListbox.FilterFunction;
    /** Whether this listbox was disabled via {@link disable}, `undefined` before. */
    disabled?: boolean;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.input = $(this.jqId + '_input') as JQuery<HTMLSelectElement>;
        this.listContainer = this.jq.children('.p-selectlistbox-listcontainer');
        this.listElement = this.listContainer.children('.p-selectlistbox-list');
        this.options = this.input.children('option') as JQuery<HTMLOptionElement>;
        this.allItems = this.listElement.find('.p-selectlistbox-item');
        this.items = this.allItems.filter(':not(.p-state-disabled)');

        // scroll to selected
        const selected = this.options.filter(':selected:not(:disabled)');
        if (selected.length) {
            // the index of the option is the index of all items, including the disabled ones
            const selectedItem = this.allItems.eq(selected.eq(0).index());
            PrimeFaces.scrollInView(this.listContainer, selectedItem);
            const selectedId = selectedItem.attr('id');
            if (selectedId !== undefined) {
                this.listElement.attr('aria-activedescendant', selectedId);
            }
        }

        // aria-label
        this.listElement.attr('aria-label', this.getAriaLabel('listLabel'));

        this.bindEvents();

        // pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    /**
     * Binds the events of the items, the input and the filter.
     */
    protected bindEvents(): void {
        // also called by enable()
        this.items.off('.selectListbox');
        this.input.off('.selectListbox');
        this.filterInput?.off('.selectListbox');

        this.items.on('mouseover.selectListbox', (e) => {
            const item = $(e.currentTarget);
            if (!item.hasClass('p-state-highlight')) {
                item.addClass('p-state-hover');
            }
        })
        .on('mouseout.selectListbox', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('dblclick.selectListbox', (e) => {
            this.input.trigger('dblclick');

            PrimeFaces.clearSelection();
            e.preventDefault();
        });

        this.input.on('focus.selectListbox', () => {
            this.jq.addClass('p-state-focus');
        }).on('blur.selectListbox', () => {
            this.jq.removeClass('p-state-focus');
        });

        if (this.cfg.filter) {
            this.filterInput = this.jq.find('div.p-selectlistbox-filter-container > input.p-selectlistbox-filter');
            PrimeFaces.skinInput(this.filterInput);
            this.filterInput.on('keyup.selectListbox', (e) => {
                this.filter((e.currentTarget as HTMLInputElement).value);
            });

            this.setupFilterMatcher();
        }
    }

    /**
     * Unselects all items.
     */
    unselectAll(): void {
        this.items.removeClass('p-state-highlight p-state-hover');
        this.options.filter(':selected').prop('selected', false);
    }

    /**
     * Selects the given item, other selected items stay selected.
     * @param item The item to select.
     */
    selectItem(item: JQuery): void {
        item.addClass('p-state-highlight').removeClass('p-state-hover').attr('aria-selected', 'true');
        const itemId = item.attr('id');
        if (itemId !== undefined) {
            this.listElement.attr('aria-activedescendant', itemId);
        }
        const itemSelected = this.options.eq(item.index());
        itemSelected.prop('selected', true);
        if (this.hasBehavior('itemSelect')) {
            const ext = {
                params: [{ name: this.getId() + '_itemSelect', value: itemSelected.text() }]
            };

            this.callBehavior('itemSelect', ext);
        }
    }

    /**
     * Unselects the given item, other selected items stay selected.
     * @param item The item to unselect.
     */
    unselectItem(item: JQuery): void {
        item.removeClass('p-state-highlight').attr('aria-selected', 'false');
        this.listElement.removeAttr('aria-activedescendant');
        this.options.eq(item.index()).prop('selected', false);
        const itemUnselected = this.options.eq(item.index());
        itemUnselected.prop('selected', false);
        if (this.hasBehavior('itemUnselect')) {
            const ext = {
                params: [{ name: this.getId() + '_itemUnselect', value: itemUnselected.text() }]
            };

            this.callBehavior('itemUnselect', ext);
        }
    }

    private setupFilterMatcher(): void {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode || 'startsWith';
        this.filterMatchers = {
            startsWith: this.startsWithFilter,
            contains: this.containsFilter,
            endsWith: this.endsWithFilter,
            custom: this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    }

    /**
     * Filter function for the mode `startsWith`.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` if the text starts with the filter value.
     */
    startsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter) === 0;
    }

    /**
     * Filter function for the mode `contains`.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` if the text contains the filter value.
     */
    containsFilter(value: string, filter: string): boolean {
        return value.indexOf(filter) !== -1;
    }

    /**
     * Filter function for the mode `endsWith`.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` if the text ends with the filter value.
     */
    endsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    }

    /**
     * Shows only the items matching the given filter value.
     * @param value The filter value.
     */
    filter(value: string): void {
        const lowercase = !this.cfg.caseSensitive;
        const normalize = !!this.cfg.filterNormalize;
        const filterValue = PrimeFaces.toSearchable(PrimeFaces.trim(value), lowercase, normalize);

        // the filtered out items also get p-hidden: p-rows-striped counts the visible items only (CSS :nth-child of S)
        if (filterValue === '') {
            this.allItems.filter(':hidden').show();
            this.allItems.removeClass('p-hidden');
        }
        else {
            for (let i = 0; i < this.options.length; i++) {
                const option = this.options.eq(i);
                const itemLabel = PrimeFaces.toSearchable(option.text(), lowercase, normalize);
                const item = this.allItems.eq(i);

                if ((this.filterMatcher as PrimeFacesTypes.widget.SelectListbox.FilterFunction)(itemLabel, filterValue)) {
                    item.removeClass('p-hidden').show();
                }
                else {
                    item.addClass('p-hidden').hide();
                }
            }
        }
    }

    /**
     * Enables this listbox so that the user can select items.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.disabled = false;
        if (this.cfg.disabled) {
            // disabled at render: the items are disabled too and their events were not bound
            this.cfg.disabled = false;
            // the options are disabled too, aria-disabled tells the items disabled on their own
            this.allItems.each((i, item) => {
                if ($(item).attr('aria-disabled') !== 'true') {
                    $(item).removeClass('p-state-disabled').find('.p-chkbox-box').removeClass('p-state-disabled');
                    this.options.eq(i).prop('disabled', false);
                }
            });
            this.items = this.allItems.filter(':not(.p-state-disabled)');
            this.jq.find('input.p-selectlistbox-filter').removeClass('p-state-disabled').prop('disabled', false);
            this.bindEvents();
        }
    }

    /**
     * Disables this listbox so that the user cannot select items anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.disabled = true;
    }
}
