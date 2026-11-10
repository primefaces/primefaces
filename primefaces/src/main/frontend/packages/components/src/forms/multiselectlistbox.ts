/**
 * The configuration for the {@link MultiSelectListbox} widget.
 */
export interface MultiSelectListboxCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether the label of a group is displayed as header of its items. */
    showHeaders: boolean;
    /** Effect of {@link PrimeFaces.effects} used when a group of items is shown. */
    effect: string;
}

/**
 * __PrimeFaces MultiSelectListbox Widget__
 *
 * MultiSelectListbox is used to select an item from a collection of listboxes that are in parent-child relationship.
 */
export class MultiSelectListbox<Cfg extends MultiSelectListboxCfg = MultiSelectListboxCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The container of the root list. */
    root: JQuery = $();
    /** The items of all lists. */
    items: JQuery = $();
    /** The hidden input with the selected value. */
    input: JQuery<HTMLInputElement> = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.root = this.jq.children('div.p-multiselectlistbox-listcontainer');
        this.items = this.jq.find('li.p-multiselectlistbox-item');
        this.input = $(this.jqId + '_input') as JQuery<HTMLInputElement>;
        this.cfg.disabled = this.jq.hasClass('p-state-disabled');

        if (!this.cfg.disabled) {
            this.bindEvents();
        }

        const value = this.input.val() as string;
        if (value !== '') {
            this.preselect(value);
        }
    }

    private bindEvents(): void {
        this.items.on('mouseover.multiSelectListbox', (e) => {
            const item = $(e.currentTarget);

            if (!item.hasClass('p-state-highlight')) {
                item.addClass('p-state-hover');
            }
        })
        .on('mouseout.multiSelectListbox', (e) => {
            const item = $(e.currentTarget);

            if (!item.hasClass('p-state-highlight')) {
                item.removeClass('p-state-hover');
            }
        })
        .on('click.multiSelectListbox', (e) => {
            const item = $(e.currentTarget);

            if (!item.hasClass('p-state-highlight')) {
                this.showOptionGroup(item);
            }
        });
    }

    private unbindEvents(): void {
        this.items.off('mouseover.multiSelectListbox mouseout.multiSelectListbox click.multiSelectListbox');
    }

    /**
     * Selects the item and shows the list of its children, if any.
     * @param item The item that was clicked.
     */
    private showOptionGroup(item: JQuery): void {
        item.addClass('p-state-highlight').removeClass('p-state-hover').siblings().filter('.p-state-highlight').removeClass('p-state-highlight');
        item.closest('.p-multiselectlistbox-listcontainer').nextAll().remove();
        this.input.val(item.attr('data-value') ?? '');

        const childItemsContainer = item.children('ul');

        if (childItemsContainer.length) {
            const groupContainer = $('<div class="p-multiselectlistbox-listcontainer" style="display:none"></div>');
            childItemsContainer.clone(true).appendTo(groupContainer).addClass('p-multiselectlistbox-list p-inputfield p-widget-content').removeClass('p-hidden');

            if (this.cfg.showHeaders) {
                groupContainer.prepend('<div class="p-multiselectlistbox-header p-widget-header">' + PrimeFaces.escapeHTML(item.children('span').text()) + '</div>');
            }

            this.jq.append(groupContainer);

            if (this.cfg.effect) {
                PrimeFaces.effects.show(groupContainer, this.cfg.effect);
            }
            else {
                groupContainer.show();
            }
        }
        else {
            this.triggerChange();
        }
    }

    /**
     * Enables this list box so that the user can select an item.
     */
    override enable(): void {
        if (this.cfg.disabled) {
            this.cfg.disabled = false;
            PrimeFaces.utils.enableInputWidget(this.jq, this.input);
            this.bindEvents();

            // disable() removed the lists of the selected groups
            const value = this.input.val() as string;
            if (value !== '' && this.jq.children('div.p-multiselectlistbox-listcontainer').length === 1) {
                this.preselect(value);
            }
        }
    }

    /**
     * Disables this list box so that the user cannot select items anymore.
     */
    override disable(): void {
        if (!this.cfg.disabled) {
            this.cfg.disabled = true;
            PrimeFaces.utils.disableInputWidget(this.jq, this.input);
            this.unbindEvents();
            this.root.nextAll().remove();
        }
    }

    /**
     * Selects the item with the given value and shows all lists up to it.
     * @param value Value of the item.
     */
    preselect(value: string): void {
        const item = this.items.filter('[data-value="' + CSS.escape(value) + '"]');

        if (item.length === 0) {
            return;
        }

        const ancestors = item.parentsUntil('.p-multiselectlistbox-list');
        const selectedIndexMap: number[] = [];

        for (let i = (ancestors.length - 1); i >= 0; i--) {
            const ancestor = ancestors.eq(i);

            if (ancestor.is('li')) {
                selectedIndexMap.push(ancestor.index());
            }
            else if (ancestor.is('ul')) {
                const groupContainer = $('<div class="p-multiselectlistbox-listcontainer" style="display:none"></div>');
                ancestor.clone(true).appendTo(groupContainer).addClass('p-multiselectlistbox-list p-inputfield p-widget-content').removeClass('p-hidden');

                if (this.cfg.showHeaders) {
                    groupContainer.prepend('<div class="p-multiselectlistbox-header p-widget-header">' + PrimeFaces.escapeHTML(ancestor.prev('span').text()) + '</div>');
                }

                this.jq.append(groupContainer);
            }
        }

        // highlight item
        const lists = this.jq.children('div.p-multiselectlistbox-listcontainer');
        const clonedItem = lists.find(' > ul.p-multiselectlistbox-list > li.p-multiselectlistbox-item').filter('[data-value="' + CSS.escape(value) + '"]');
        clonedItem.addClass('p-state-highlight');

        // highlight ancestors
        for (let j = 0; j < selectedIndexMap.length; j++) {
            lists.eq(j).find('> .p-multiselectlistbox-list > li.p-multiselectlistbox-item').eq(selectedIndexMap[j] as number).addClass('p-state-highlight');
        }

        this.jq.children('div.p-multiselectlistbox-listcontainer:hidden').show();
    }

    /**
     * Calls the `change` behavior after an item without children was selected.
     */
    private triggerChange(): void {
        this.callBehavior('change');
    }
}
