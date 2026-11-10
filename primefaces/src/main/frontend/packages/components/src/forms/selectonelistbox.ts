import { SelectListbox, type SelectListboxCfg } from "./selectlistbox.js";

/**
 * The configuration for the {@link SelectOneListbox} widget.
 */
export interface SelectOneListboxCfg extends SelectListboxCfg {
}

/**
 * __PrimeFaces SelectOneListbox Widget__
 *
 * SelectOneListbox is an extended version of the standard selectOneListbox component.
 */
export class SelectOneListbox<Cfg extends SelectOneListboxCfg = SelectOneListboxCfg> extends SelectListbox<Cfg> {
    /** The item with the keyboard focus. */
    focusedItem: JQuery | null = null;

    protected override bindEvents(): void {
        super.bindEvents();

        if (!this.cfg.disabled) {
            this.focusedItem = null;

            this.items.on('click.selectListbox', (e) => {
                const item = $(e.currentTarget);
                const selectedItem = this.items.filter('.p-state-highlight');

                if (item.index() !== selectedItem.index()) {
                    if (selectedItem.length) {
                        this.unselectItem(selectedItem);
                    }

                    this.selectItem(item);
                    this.input.trigger('change');
                }

                // For keyboard navigation
                this.removeOutline();
                this.focusedItem = item;
                this.input.trigger('focus');
                this.input.trigger('click');

                PrimeFaces.clearSelection();
                e.preventDefault();
            });
        }

        this.bindKeyEvents();
    }

    private bindKeyEvents(): void {
        this.input.off('focus.selectListbox blur.selectListbox keydown.selectListbox')
            .on('focus.selectListbox', () => {
                this.jq.addClass('p-state-focus');

                const activeItem = this.focusedItem || this.items.filter('.p-state-highlight:visible').first();
                if (activeItem.length) {
                    this.focusedItem = activeItem;
                }
                else {
                    this.focusedItem = this.items.filter(':visible').first();
                }

                PrimeFaces.queueTask(() => {
                    if (this.focusedItem) {
                        PrimeFaces.scrollInView(this.listContainer, this.focusedItem);
                        this.focusedItem.addClass('p-listbox-outline');
                    }
                });
            })
            .on('blur.selectListbox', () => {
                this.jq.removeClass('p-state-focus');
                this.removeOutline();
                this.focusedItem = null;
            })
            .on('keydown.selectListbox', (e) => {
                if (!this.focusedItem) {
                    return;
                }

                switch ((e as JQuery.TriggeredEvent & { code?: string }).code) {
                    case 'ArrowLeft':
                    case 'ArrowUp':
                        this.select(this.focusedItem.prevAll('.p-selectlistbox-item:visible').first());
                        e.preventDefault();
                        break;
                    case 'ArrowRight':
                    case 'ArrowDown':
                        this.select(this.focusedItem.nextAll('.p-selectlistbox-item:visible').first());
                        e.preventDefault();
                        break;
                    case 'Home':
                    case 'PageUp':
                        this.select(this.items.first());
                        e.preventDefault();
                        break;
                    case 'End':
                    case 'PageDown':
                        this.select(this.items.last());
                        e.preventDefault();
                        break;
                }
            });
    }

    /**
     * Selects the given item, or the focused item if it is not selected yet.
     * @param item The item to select.
     */
    select(item: JQuery): void {
        if (!this.focusedItem) {
            // no item has the keyboard focus
            return;
        }
        if (!this.focusedItem.hasClass('p-state-highlight')) {
            this.focusedItem.trigger('click.selectListbox');
        }
        else if (item && item.length) {
            item.trigger('click.selectListbox');
            PrimeFaces.scrollInView(this.listContainer, item);
        }
    }

    private removeOutline(): void {
        if (this.focusedItem && this.focusedItem.hasClass('p-listbox-outline')) {
            this.focusedItem.removeClass('p-listbox-outline');
        }
    }
}
