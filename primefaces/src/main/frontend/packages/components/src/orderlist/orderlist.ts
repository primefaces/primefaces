/**
 * The configuration for the {@link OrderList} widget.
 */
export interface OrderListCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Name of animation to display. */
    effect: string;
}

/**
 * __PrimeFaces OrderList Widget__
 *
 * OrderList is used to sort a collection featuring drag&drop based reordering, transition effects and POJO support.
 */
export class OrderList<Cfg extends OrderListCfg = OrderListCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the hidden form field storing the current order of the items. */
    input: JQuery = $();
    /** The DOM elements for the available items that can be reordered. */
    items: JQuery = $();
    /** The DOM element for the container with the items. */
    list: JQuery = $();
    /** The DOM element for the source list, when used with a pick list. */
    sourceList?: JQuery;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.list = this.jq.find('.p-orderlist-list');
        this.items = this.list.children('.p-orderlist-item');
        this.input = $(this.jqId + '_values');
        this.cfg.effect = this.cfg.effect||'fade';
        this.cfg.disabled = this.jq.hasClass('p-state-disabled');

        if(!this.cfg.disabled) {
            this.generateItems();

            this.setupButtons();

            //Enable dnd
            PrimeFaces.interactions.sortable(this.list, {
                revert: 1,
                placeholder: "p-orderlist-item p-state-highlight",
                forcePlaceholderSize: true,
                start: () => {
                    PrimeFaces.clearSelection();
                }
                ,update: (event, ui) => {
                    this.onDragDrop(event, ui);
                }
            });

            this.bindEvents();
        }
    }

    /**
     * Reads the current item order and stores it in a hidden form field.
     */
    private generateItems(): void {
        this.list.children('.p-orderlist-item').each((_i, el) => {
            const item = $(el),
            itemValue = item.data('item-value'),
            option = $('<option selected="selected"></option>');

            option.prop('value', itemValue).text(itemValue);
            this.input.append(option);
        });
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        // #4716 on mobile the list scrolls by touch, a tap on an item enables the sorting until the next touch ends
        if (PrimeFaces.env.browser.mobile) {
            const disabledSortable = () => {
                PrimeFaces.interactions.disable(this.list, 'sortable');
                this.items.css('touch-action', 'auto');
            };

            disabledSortable();

            this.items.on('touchend.orderList-mobile', () => {
                disabledSortable();
            })
            .on('click.orderList-mobile', () => {
                PrimeFaces.interactions.enable(this.list, 'sortable');
                // the items must not scroll anymore, else the browser takes over the pointer of the drag
                this.items.css('touch-action', '');
            });
        }

        this.items.on('mouseover.orderList', (e) => {
            const element = $(e.currentTarget);

            if(!element.hasClass('p-state-highlight'))
                $(e.currentTarget).addClass('p-state-hover');
        })
        .on('mouseout.orderList', (e) => {
            const element = $(e.currentTarget);

            if(!element.hasClass('p-state-highlight'))
                $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('mousedown.orderList', (e) => {
            const element = $(e.currentTarget),
            metaKey = (e.metaKey||e.ctrlKey);

            if(!metaKey) {
                element.removeClass('p-state-hover').addClass('p-state-highlight')
                .siblings('.p-state-highlight').removeClass('p-state-highlight');

                this.fireItemSelectEvent(element, e);
            }
            else if(element.hasClass('p-state-highlight')) {
                element.removeClass('p-state-highlight');
                this.fireItemUnselectEvent(element);
            }
            else {
                element.removeClass('p-state-hover').addClass('p-state-highlight');
                this.fireItemSelectEvent(element, e);
            }
        });
    }

    /**
     * Sets up the buttons and corresponding event listeners for moving order list items up and down.
     */
    private setupButtons(): void {
        PrimeFaces.skinButton(this.jq.find('.p-button'));

        // the move methods take no argument, the old code passed this.sourceList which was ignored
        this.jq.find(' .p-orderlist-controls .p-orderlist-button-move-up').on("click", () => {this.moveUp();});
        this.jq.find(' .p-orderlist-controls .p-orderlist-button-move-top').on("click", () => {this.moveTop();});
        this.jq.find(' .p-orderlist-controls .p-orderlist-button-move-down').on("click", () => {this.moveDown();});
        this.jq.find(' .p-orderlist-controls .p-orderlist-button-move-bottom').on("click", () => {this.moveBottom();});
    }

    /**
     * Callback that is invoked when an order list item was moved via drag and drop. Saves the new order of the items
     * and invokes the appropriate behaviors.
     * @param _event The event that triggered the drag or drop.
     * @param ui The details about the sorting.
     */
    private onDragDrop(_event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.SortableUi): void {
        ui.item.removeClass('p-state-highlight');
        this.saveState();
        this.fireReorderEvent();
    }

    /**
     * Saves the current value of this order list, i.e. the order of the items.  The value is saved in a hidden form
     * field.
     */
    private saveState(): void {
        this.input.children().remove();

        this.generateItems();
    }

    /**
     * Moves the selected order list items up by one, as if the `move up` button were pressed.
     */
    moveUp(): void {
        const selectedItems = this.list.children('.p-orderlist-item.p-state-highlight'),
        hasFirstChild = selectedItems.is(':first-child');
        let itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        if(hasFirstChild) {
            return;
        }

        selectedItems.each((_i, el) => {
            const item = $(el);

            if(!item.is(':first-child')) {
                PrimeFaces.effects.hide(item, this.cfg.effect, { duration: 'fast', complete: () => {
                    PrimeFaces.effects.show(item.insertBefore(item.prev()), this.cfg.effect, { duration: 'fast', complete: () => {
                        movedItemsCount++;

                        if(itemsToMoveCount === movedItemsCount) {
                            this.saveState();
                            this.fireReorderEvent();
                        }
                    } });
                } });
            }
            else {
                itemsToMoveCount--;
            }
        });
    }

    /**
     * Moves the selected order list items to the top, as if the `move to top` button were pressed.
     */
    moveTop(): void {
        const selectedItems = this.list.children('.p-orderlist-item.p-state-highlight'),
        hasFirstChild = selectedItems.is(':first-child'),
        firstSelectedItemIndex = selectedItems.eq(0).index();
        let itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        if(hasFirstChild) {
            return;
        }

        selectedItems.each((index, el) => {
            const item = $(el),
                currentIndex = (index === 0) ? 0 : (item.index() - firstSelectedItemIndex);

            if(!item.is(':first-child')) {
                PrimeFaces.effects.hide(item, this.cfg.effect, { duration: 'fast', complete: () => {
                    PrimeFaces.effects.show(item.insertBefore(this.list.children('.p-orderlist-item').eq(currentIndex)), this.cfg.effect, { duration: 'fast', complete: () => {
                        movedItemsCount++;

                        if(itemsToMoveCount === movedItemsCount) {
                            this.saveState();
                            this.fireReorderEvent();
                        }
                    } });
                } });
            }
            else {
                itemsToMoveCount--;
            }
        });
    }

    /**
     * Moves the selected order list items down by one, as if the `move down` button were pressed.
     */
    moveDown(): void {
        const selectedItems = $(this.list.children('.p-orderlist-item.p-state-highlight').get().reverse()),
        hasFirstChild = selectedItems.is(':last-child');
        let itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        if(hasFirstChild) {
            return;
        }

        selectedItems.each((_i, el) => {
            const item = $(el);

            if(!item.is(':last-child')) {
                PrimeFaces.effects.hide(item, this.cfg.effect, { duration: 'fast', complete: () => {
                    PrimeFaces.effects.show(item.insertAfter(item.next()), this.cfg.effect, { duration: 'fast', complete: () => {
                        movedItemsCount++;

                        if(itemsToMoveCount === movedItemsCount) {
                            this.saveState();
                            this.fireReorderEvent();
                        }
                    } });
                } });
            }
            else {
                itemsToMoveCount--;
            }
        });
    }

    /**
     * Moves the selected order list items to the bottom, as if the `move to bottom` button were pressed.
     */
    moveBottom(): void {
        const selectedItems = $(this.list.children('.p-orderlist-item.p-state-highlight').get().reverse()),
        hasFirstChild = selectedItems.is(':last-child'),
        lastSelectedItemIndex = selectedItems.eq(0).index(),
        itemsLength = this.items.length;
        let itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        if(hasFirstChild) {
            return;
        }

        selectedItems.each((index, el) => {
            const item = $(el),
                currentIndex = (index === 0) ? itemsLength - 1 : (item.index() - lastSelectedItemIndex) - 1;

            if(!item.is(':last-child')) {
                PrimeFaces.effects.hide(item, this.cfg.effect, { duration: 'fast', complete: () => {
                    PrimeFaces.effects.show(item.insertAfter(this.list.children('.p-orderlist-item').eq(currentIndex)), this.cfg.effect, { duration: 'fast', complete: () => {
                        movedItemsCount++;

                        if(itemsToMoveCount === movedItemsCount) {
                            this.saveState();
                            this.fireReorderEvent();
                        }
                    } });
                } });
            }
            else {
                itemsToMoveCount--;
            }
        });
    }

    /**
     * Invokes the appropriate behavior for when an item of the order list was selected.
     * @param item The item that was selected.
     * @param e The event that occurred.
     */
    private fireItemSelectEvent(item: JQuery, e: JQuery.TriggeredEvent): void {
        if(this.hasBehavior('select')) {
            const ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_metaKey', value: e.metaKey},
                    {name: this.id + '_ctrlKey', value: e.ctrlKey}
                ]
            };

            this.callBehavior('select', ext);
        }
    }

    /**
     * Invokes the appropriate behavior for when an item of the order list was unselected.
     * @param item The item that was unselected.
     */
    private fireItemUnselectEvent(item: JQuery): void {
        if(this.hasBehavior('unselect')) {
            const ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()}
                ]
            };

            this.callBehavior('unselect', ext);
        }
    }

    /**
     * Invokes the appropriate behavior for when the order list was reordered.
     */
    private fireReorderEvent(): void {
        if(this.hasBehavior('reorder')) {
            this.callBehavior('reorder');
        }
    }

}
