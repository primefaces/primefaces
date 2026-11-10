import { SelectListbox, type SelectListboxCfg } from "./selectlistbox.js";

/**
 * The configuration for the {@link SelectManyMenu} widget.
 */
export interface SelectManyMenuCfg extends SelectListboxCfg {
    /** Whether the meta key (CTRL or CMD) must be held down to select several items, normalized on init (default `true`). */
    metaKeySelection: boolean;
    /** Whether a checkbox is displayed for each item. */
    showCheckbox: boolean;
}

/**
 * __PrimeFaces SelectManyMenu Widget__
 *
 * SelectManyMenu is an extended version of the standard SelectManyMenu.
 */
export class SelectManyMenu<Cfg extends SelectManyMenuCfg = SelectManyMenuCfg> extends SelectListbox<Cfg> {
    /** The last selected item, the start of a range selected with SHIFT. */
    cursorItem?: JQuery;
    /** Whether a checkbox was clicked, so the click on the item is ignored. */
    checkboxClick?: boolean;
    /** The checkboxes of the items, only with `showCheckbox`. */
    checkboxes?: JQuery;
    /** The checkbox that selects or unselects all items, if rendered. */
    toggler: JQuery = $();
    /** The footer with the number of selected items, if rendered. */
    footer: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.metaKeySelection = this.cfg.metaKeySelection != undefined ? this.cfg.metaKeySelection : true;

        this.allItems.filter('.p-state-highlight').find('> .p-chkbox > .p-chkbox-box').addClass('p-state-active');
        // Checkbox is inside TD element when using custom content
        this.allItems.filter('.p-state-highlight').find('> td > .p-chkbox > .p-chkbox-box').addClass('p-state-active');

        this.footer = this.jq.children('.p-selectmanymenu-footer');
        this.updateSelectionInfo();
    }

    protected override bindEvents(): void {
        super.bindEvents();

        this.toggler = this.jq.find('> .p-selectmanymenu-header > .p-chkbox > .p-chkbox-box').attr('role', 'checkbox');

        if (!this.cfg.disabled) {
            this.toggler.attr('tabindex', '0').off('.selectManyMenu').on('click.selectManyMenu', (e) => {
                if ($(e.currentTarget).hasClass('p-state-active')) {
                    this.unselectAll();
                }
                else {
                    this.selectAll();
                }

                this.input.trigger('change');
            }).on('keydown.selectManyMenu', (e) => {
                if ((e as JQuery.TriggeredEvent & { code?: string }).code === 'Space') {
                    $(e.currentTarget).trigger('click');
                    e.preventDefault();
                }
            }).on('focus.selectManyMenu', (e) => {
                $(e.currentTarget).addClass('p-state-focus');
            }).on('blur.selectManyMenu', (e) => {
                $(e.currentTarget).removeClass('p-state-focus');
            });

            this.items.on('click.selectListbox', (e) => {
                // stop propagation
                if (this.checkboxClick) {
                    this.checkboxClick = false;
                    return;
                }

                const item = $(e.currentTarget);
                const metaKey = this.cfg.metaKeySelection && (e.metaKey || e.ctrlKey);

                if (!e.shiftKey) {
                    if (!metaKey && !this.cfg.showCheckbox) {
                        this.unselectAll();
                    }

                    if ((metaKey || this.cfg.showCheckbox) && item.hasClass('p-state-highlight')) {
                        this.unselectItem(item);
                    }
                    else {
                        this.selectItem(item);
                        this.cursorItem = item;
                    }
                }
                else {
                    // range selection
                    if (this.cursorItem) {
                        this.unselectAll();

                        const currentItemIndex = item.index();
                        const cursorItemIndex = this.cursorItem.index();
                        const startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex;
                        const endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1);

                        for (let i = startIndex; i < endIndex; i++) {
                            const it = this.allItems.eq(i);

                            if (it.is(':visible') && !it.hasClass('p-state-disabled')) {
                                this.selectItem(it);
                            }
                        }
                    }
                    else {
                        this.selectItem(item);
                        this.cursorItem = item;
                    }
                }

                this.input.trigger('change');
                this.input.trigger('click');
                PrimeFaces.clearSelection();
                e.preventDefault();
            }).on('keydown.selectListbox', (event) => {
                this.onKeyDown(event);
            });

            if (this.cfg.showCheckbox) {
                const checkboxes = this.jq.find('.p-selectlistbox-item:not(.p-state-disabled) div.p-chkbox > div.p-chkbox-box');
                this.checkboxes = checkboxes;

                checkboxes.off('.selectManyMenu').on('mouseenter.selectManyMenu', (e) => {
                    $(e.currentTarget).addClass('p-state-hover');
                })
                    .on('mouseleave.selectManyMenu', (e) => {
                        $(e.currentTarget).removeClass('p-state-hover');
                    })
                    .on('click.selectManyMenu', (e) => {
                        this.checkboxClick = true;

                        const item = $(e.currentTarget).closest('.p-selectlistbox-item');
                        if (item.hasClass('p-state-highlight')) {
                            this.unselectItem(item);
                        }
                        else {
                            this.selectItem(item);
                        }

                        this.input.trigger('change');
                    });
            }
        }
    }

    /**
     * Selects, unselects and focuses items with the keyboard.
     * @param event The keydown event on an item.
     */
    private onKeyDown(event: JQuery.TriggeredEvent): void {
        const item = $(event.currentTarget);
        const isMetaKey = (event.metaKey || event.ctrlKey);
        const metaKey = this.cfg.metaKeySelection && isMetaKey;

        switch ((event as JQuery.TriggeredEvent & { code?: string }).code) {
            case 'Enter':
            case 'NumpadEnter':
            case 'Space':
                if (!metaKey && !this.cfg.showCheckbox) {
                    this.unselectAll();
                }

                if ((metaKey || this.cfg.showCheckbox) && item.hasClass('p-state-highlight')) {
                    this.unselectItem(item);
                }
                else {
                    this.selectItem(item);
                    this.cursorItem = item;
                }
                event.preventDefault();
                break;
            case 'KeyA':
                if (metaKey) {
                    this.selectAll();
                }
                event.preventDefault();
                break;
            case 'KeyD':
                if (metaKey) {
                    this.unselectAll();
                }
                event.preventDefault();
                break;
            case 'ArrowUp':
            case 'ArrowDown':
                if (!event.shiftKey) {
                    const newItem = event.key === 'ArrowDown' ? item.next() : item.prev();
                    if (!newItem.hasClass('p-selectlistbox-item')) {
                        return;
                    }
                    this.focus(newItem);
                }
                event.preventDefault();
                break;
            case 'Home':
            case 'PageUp':
                this.focus(this.items.first());
                event.preventDefault();
                break;
            case 'End':
            case 'PageDown':
                this.focus(this.items.last());
                event.preventDefault();
                break;
        }
    }

    /**
     * Focuses the given item.
     * @param item The item to focus.
     */
    focus(item: JQuery): void {
        this.cursorItem = item;
        item.trigger('focus');
    }

    /**
     * Selects all items that are not filtered out.
     */
    selectAll(): void {
        // ~~~ PERF ~~~
        // See https://github.com/primefaces/primefaces/issues/2089
        //
        // 1) don't use jquery wrappers
        // 2) don't use existing methods like selectItem
        const filteredItems = this.items.not('.p-hidden');

        for (let i = 0; i < filteredItems.length; i++) {
            const item = filteredItems.eq(i);
            const itemNative = item[0];
            if (!itemNative) {
                continue;
            }

            itemNative.classList.add('p-state-highlight');
            itemNative.classList.remove('p-state-hover');
            itemNative.setAttribute('aria-selected', 'true');

            if (this.cfg.showCheckbox) {
                const checkbox = item.find('div.p-chkbox').children('div.p-chkbox-box');

                checkbox[0]?.classList.remove('p-state-hover');
                checkbox[0]?.classList.add('p-state-active');

                checkbox.children('span.p-chkbox-icon')[0]?.classList.add('p-checkbox-checked-icon');
            }
        }

        // filtered out and disabled items keep their state
        for (let j = 0; j < this.options.length; j++) {
            const option = this.options[j];
            const item = this.allItems[j];
            if (option && item) {
                option.selected = item.classList.contains('p-state-highlight');
            }
        }

        this.updateSelectionInfo();
    }

    /**
     * Unselects all items that are not filtered out.
     */
    override unselectAll(): void {
        // ~~~ PERF ~~~
        // See https://github.com/primefaces/primefaces/issues/2089
        //
        // 1) don't use jquery wrappers
        // 2) don't use existing methods like unselectItem
        const filteredItems = this.items.not('.p-hidden');

        for (let i = 0; i < filteredItems.length; i++) {
            const item = filteredItems.eq(i);
            const itemNative = item[0];
            if (!itemNative) {
                continue;
            }

            itemNative.classList.remove('p-state-highlight');
            itemNative.setAttribute('aria-selected', 'false');

            if (this.cfg.showCheckbox) {
                const checkbox = item.find('div.p-chkbox').children('div.p-chkbox-box');

                checkbox[0]?.classList.remove('p-state-active');

                checkbox.children('span.p-chkbox-icon')[0]?.classList.remove('p-checkbox-checked-icon');
            }
        }

        // filtered out and disabled items keep their state
        for (let j = 0; j < this.options.length; j++) {
            const option = this.options[j];
            const item = this.allItems[j];
            if (option && item) {
                option.selected = item.classList.contains('p-state-highlight');
            }
        }

        this.updateSelectionInfo();
    }

    override selectItem(item: JQuery): void {
        super.selectItem(item);

        if (this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.p-chkbox-box'));
        }

        this.updateSelectionInfo();
    }

    override unselectItem(item: JQuery): void {
        super.unselectItem(item);

        if (this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.p-chkbox-box'));
        }

        this.updateSelectionInfo();
    }

    override filter(value: string): void {
        super.filter(value);
        this.updateSelectionInfo();
    }

    /**
     * Updates the number of selected items in the footer and the state of the toggle all checkbox.
     */
    private updateSelectionInfo(): void {
        if (this.footer.length) {
            const count = this.input[0]?.selectedOptions.length ?? 0;
            this.footer.text(count ? (this.getLabel('selectionMessage') ?? '').replace('{0}', String(count)) : (this.getLabel('emptySelectionMessage') ?? ''));
        }

        if (this.toggler.length) {
            const selectable = this.items.not('.p-hidden');
            const checked = selectable.length > 0 && selectable.not('.p-state-highlight').length === 0;

            if (checked) {
                this.selectCheckbox(this.toggler);
            }
            else {
                this.unselectCheckbox(this.toggler);
            }

            this.toggler.attr({
                'aria-checked': String(checked),
                'aria-label': this.getAriaLabel(checked ? 'selectAll' : 'unselectAll')
            });
        }
    }

    /**
     * Checks the given checkbox.
     * @param chkbox The checkbox.
     */
    selectCheckbox(chkbox: JQuery): void {
        chkbox.addClass('p-state-active').children('span.p-chkbox-icon').addClass('p-checkbox-checked-icon');
    }

    /**
     * Unchecks the given checkbox.
     * @param chkbox The checkbox.
     */
    unselectCheckbox(chkbox: JQuery): void {
        chkbox.removeClass('p-state-active').children('span.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
    }
}
