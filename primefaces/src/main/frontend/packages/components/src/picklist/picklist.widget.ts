/**
 * The configuration for the {@link PickList} widget.
 */
export interface PickListCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether items can be moved via drag and drop, `false` disables it. */
    dragDrop: boolean;
    /** Name of the animation to display. */
    effect: string;
    /** Speed of the animation. */
    effectSpeed: string;
    /** Whether the item values are escaped for HTML. */
    escapeValue: boolean;
    /** Delay to wait in milliseconds before sending each filter query. Default is `300`. */
    filterDelay: number;
    /** Client side event to invoke picklist filtering for input fields. Default is `keyup`. */
    filterEvent: string;
    /** Defines if filtering would be done using normalized values. */
    filterNormalize: boolean;
    /** A custom filter function that is used when `filterMatchMode` is set to `custom`. */
    filterFunction: PrimeFacesTypes.widget.PickList.FilterFunction;
    /** Mode of the filter. When set to `custom`, a `filterFunction` must be specified. */
    filterMatchMode: PrimeFacesTypes.widget.PickList.FilterMatchMode;
    /** Callback that is invoked when items are transferred from one list to the other. */
    onTransfer: PrimeFacesTypes.widget.PickList.OnTransferCallback;
    /** When true, a checkbox is displayed next to each item. */
    showCheckbox: boolean;
    /** Specifies visibility of reorder buttons of source list. */
    showSourceControls: boolean;
    /** Specifies visibility of reorder buttons of target list. */
    showTargetControls: boolean;
    /** Position of the element in the tabbing order. */
    tabindex: string;
    /** Whether an item is transferred when it is clicked with the checkbox. */
    transferOnCheckboxClick: boolean;
    /** Whether an item is transferred when it is double clicked, `false` disables it. */
    transferOnDblclick: boolean;
}

/**
 * __PrimeFaces PickList Widget__
 *
 * PickList is used for transferring data between two different collections.
 */
export class PickList<Cfg extends PickListCfg = PickListCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the aria region with the `aria-*` attributes */
    ariaRegion: JQuery = $();
    /** The DOM elements for the checkboxes next to each pick list item. */
    checkboxes: JQuery = $();
    /** UI state indicating whether a checkbox was just clicked. */
    checkboxClick?: boolean;
    /** The currently selected item. */
    cursorItem?: JQuery | null;
    /** Whether the user is currently transferring an item via drag&drop. */
    dragging?: boolean;
    /** The filter that was selected and is currently used. */
    filterMatcher?: PrimeFacesTypes.widget.PickList.FilterFunction;
    /** Map between the available filter types and the filter implementation. */
    filterMatchers: Partial<Record<PrimeFacesTypes.widget.PickList.FilterMatchMode, PrimeFacesTypes.widget.PickList.FilterFunction>> = {};
    /** The DOM element for the currently focused pick list item, if any. */
    focusedItem: JQuery | null = null;
    /** The DOM elements for the pick list items in the source and target list. */
    items: JQuery = $();
    /** When sorting items: to which list the items belong. */
    itemListName?: PrimeFacesTypes.widget.PickList.ListName;
    /** The DOM element for the filter input for the source list. */
    sourceFilter?: JQuery;
    /** The DOM element for the hidden input storing the value of the source list. */
    sourceInput: JQuery = $();
    /** The DOM element for the source list. */
    sourceList: JQuery = $();
    /** The DOM element for the filter input for the target list. */
    targetFilter?: JQuery;
    /** The DOM element for the hidden input storing the value of the target list. */
    targetInput: JQuery = $();
    /** The DOM element for the target list. */
    targetList: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.cfg.transferOnDblclick = this.cfg.transferOnDblclick !== false;
        this.cfg.transferOnCheckboxClick = this.cfg.transferOnCheckboxClick || false;

        this.sourceList = this.jq.find('ul.p-picklist-source');
        this.targetList = this.jq.find('ul.p-picklist-target');
        this.sourceInput = $(this.jqId + '_source');
        this.targetInput = $(this.jqId + '_target');
        this.items = this.jq.find('.p-picklist-item:not(.p-state-disabled)');
        if(this.cfg.showCheckbox) {
            this.checkboxes = this.items.find('div.p-chkbox > div.p-chkbox-box');
        }
        this.focusedItem = null;
        this.ariaRegion = $(this.jqId + '_ariaRegion');

        const sourceCaption = this.sourceList.prev('.p-picklist-caption'),
            targetCaption = this.targetList.prev('.p-picklist-caption');
        let captionText: string | null = null;

        if(sourceCaption.length) {
            captionText = sourceCaption.text();

            this.sourceList.attr('aria-label', captionText);
            this.sourceInput.attr('title', captionText);
        }

        if(targetCaption.length) {
            captionText = targetCaption.text();

            this.targetList.attr('aria-label', captionText);
            this.targetInput.attr('title', captionText);
        }

        this.setTabIndex();

        //generate input options
        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);

        //always bind button events and labels
        this.bindButtonEvents();

        if(this.cfg.disabled) {
            $(this.jqId + ' li.p-picklist-item').addClass('p-state-disabled');
            $(this.jqId + ' button').attr('disabled', 'disabled').addClass('p-state-disabled');
            $(this.jqId + ' .p-picklist-filter-container').addClass('p-state-disabled').children('input').attr('disabled', 'disabled');
        }
        else {
            this.bindDragDopEvents();

            this.bindItemEvents();

            this.bindFilterEvents();

            this.bindKeyEvents();

            this.updateButtonsState();

            this.updateListRole();
        }
    }

    /**
     * Sets up the drag and drop event listeners for reordering and transferring pick list items between source and target lists.
     * Only enabled for non-touch devices.
     * @see PrimeFaces.env.isTouchable
     */
    private bindDragDopEvents(): void {
        let reordered = true;

        // #13131 always disable drag and drop on touch devices
        const dragDropDisabled = (this.cfg.dragDrop === false) || PrimeFaces.env.isTouchable(this.cfg);

        //Sortable lists
        PrimeFaces.interactions.sortable($(this.jqId + ' ul'), {
            cancel: '.p-state-disabled,.p-chkbox-box',
            connectWith: this.jqId + ' .p-picklist-list',
            disabled: dragDropDisabled,
            revert: 1,
            helper: 'clone',
            placeholder: "p-picklist-item p-state-highlight",
            forcePlaceholderSize: true,
            update: (_event, ui: PrimeFacesTypes.interactions.SortableUi) => {
                this.unselectItem(ui.item);

                this.saveState();
                if(reordered) {
                    this.fireReorderEvent();
                    reordered = false;
                }
            },
            receive: (_event, ui: PrimeFacesTypes.interactions.SortableUi) => {
                const parentList = ui.item.parents('ul.p-picklist-list:first');
                const item = ui.item;

                if (this.cfg.transferOnCheckboxClick) {
                    if (parentList.hasClass('p-picklist-source')) {
                        this.unselectCheckbox(item.find('div.p-chkbox-box'));
                    }
                    else {
                        this.selectCheckbox(item.find('div.p-chkbox-box'));
                    }
                }

                this.fireTransferEvent(item, ui.sender!, parentList, 'dragdrop');
            },

            start: (_event, ui: PrimeFacesTypes.interactions.SortableUi) => {
                this.itemListName = this.getListName(ui.item);
                this.dragging = true;
            },

            stop: () => {
                this.dragging = false;
            },

            beforeStop: (_event, ui: PrimeFacesTypes.interactions.SortableUi) => {
                if(this.itemListName !== this.getListName(ui.item)) {
                    reordered = false;
                }
                else {
                    reordered = true;
                }
            }
        });
    }

    /**
     * Sets up the event listeners for selecting and transferring pick list items.
     */
    private bindItemEvents(): void {
        this.items.on('mouseover.pickList', (e) => {
            $(e.currentTarget).addClass('p-state-hover');
        })
        .on('mouseout.pickList', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('mousedown.pickList', (e) => {
            const item = $(e.currentTarget).closest('li.p-picklist-item');
            this.focusedItem = item;
        })
        .on('click.pickList', (e) => {
            //stop propagation
            if(this.checkboxClick||this.dragging) {
                this.checkboxClick = false;
                return;
            }

            const item = $(e.currentTarget),
            parentList = item.parent(),
            metaKey = (e.metaKey||e.ctrlKey);

            if(!e.shiftKey) {
                if(!metaKey) {
                    this.unselectAll();
                }

                if(metaKey && item.hasClass('p-state-highlight')) {
                    this.unselectItem(item, true);
                }
                else {
                    this.selectItem(item, true);
                    this.cursorItem = item;
                }
            }
            else {
                this.unselectAll();

                if(this.cursorItem && (this.cursorItem.parent().is(item.parent()))) {
                    const currentItemIndex = item.index(),
                    cursorItemIndex = this.cursorItem.index(),
                    startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex,
                    endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1);

                    for(let i = startIndex ; i < endIndex; i++) {
                        const it = parentList.children('li.p-picklist-item').eq(i);

                        if(it.is(':visible')) {
                            if(i === (endIndex - 1))
                                this.selectItem(it, true);
                            else
                                this.selectItem(it);
                        }
                    }
                }
                else {
                    this.selectItem(item, true);
                    this.cursorItem = item;
                }
            }

            /* For keyboard navigation */
            this.removeOutline();
            this.focusedItem = item;
            // #15476 only re-trigger focus if the list does not already own it, otherwise
            // the focus handler runs twice and the screen reader announces the item twice
            if (!parentList.is(document.activeElement as Element)) {
                parentList.trigger('focus.pickList');
            }
            else {
                parentList.children('.p-picklist-outline').removeClass('p-picklist-outline');
                item.addClass('p-picklist-outline');
                this.updateAriaRegion();
            }
        });

        if (this.cfg.transferOnDblclick) {
            this.items.on('dblclick.pickList', (e) => {
                const item = $(e.currentTarget);

                if ($(e.currentTarget).parent().hasClass('p-picklist-source')) {
                    this.transfer(item, this.sourceList, this.targetList, 'dblclick');
                }
                else {
                    this.transfer(item, this.targetList, this.sourceList, 'dblclick');
                }

                /* For keyboard navigation */
                this.removeOutline();
                this.focusedItem = null;

                PrimeFaces.clearSelection();
            });
        }

        if(this.cfg.showCheckbox) {
            this.checkboxes.off().on('mouseenter.pickList', (e) => {
                $(e.currentTarget).addClass('p-state-hover');
            })
            .on('mouseleave.pickList', (e) => {
                $(e.currentTarget).removeClass('p-state-hover');
            })
            .on('mousedown.pickList', (e) => {
                const item = $(e.currentTarget).closest('li.p-picklist-item');
                this.focusedItem = item;
            })
            .on('click.pickList', (e) => {
                this.checkboxClick = true;

                const item = $(e.currentTarget).closest('li.p-picklist-item');
                if (this.cfg.transferOnCheckboxClick) {
                    if (item.parent().hasClass('p-picklist-source')) {
                        this.transfer(item, this.sourceList, this.targetList, 'checkbox', () => {
                            this.unselectItem(item);
                        });
                    }
                    else {
                        this.transfer(item, this.targetList, this.sourceList, 'checkbox', () => {
                            this.unselectItem(item);
                        });
                    }
                }
                else {
                    if (item.hasClass('p-state-highlight')) {
                        this.unselectItem(item, true);
                    }
                    else {
                        this.selectItem(item, true);
                    }
                    this.focusedItem = item;
                }
            });
        }
    }

    /**
     * Sets up the keyboard event listeners for navigating the pick list via keyboard keys.
     */
    private bindKeyEvents(): void {
        const listSelector = 'ul.p-picklist-source, ul.p-picklist-target';

        this.jq.off('focus.pickList blur.pickList keydown.pickList', listSelector).on('focus.pickList', listSelector, null, (e) => {
            const list = $(e.currentTarget),
                activeItem = this.focusedItem||list.children('.p-state-highlight:visible:first');
            if(activeItem.length) {
                this.focusedItem = activeItem;
            }
            else {
                this.focusedItem = list.children('.p-picklist-item:visible:first');
            }

            list.children('.p-picklist-outline').removeClass('p-picklist-outline');

            PrimeFaces.queueTask(() => {
                if (this.focusedItem) {
                    PrimeFaces.scrollInView(list, this.focusedItem);
                    this.focusedItem.addClass('p-picklist-outline');
                    this.updateAriaRegion();
                }
            }, 100);
        })
        .on('blur.pickList', listSelector, null, () => {
            this.removeOutline();
            this.focusedItem = null;
            // #15476 clear the live region so the item is announced again on next focus
            this.ariaRegion.text('');
        })
        .on('keydown.pickList', listSelector, null, (e) => {

            if(!this.focusedItem) {
                return;
            }

            const list = $(e.currentTarget);

            switch((e as JQuery.TriggeredEvent & { code?: string }).code) {
                case 'ArrowUp':
                    this.removeOutline();

                    if(!this.focusedItem.hasClass('p-state-highlight')) {
                        this.selectItem(this.focusedItem);
                    }
                    else {
                        const prevItem = this.focusedItem.prevAll('.p-picklist-item:visible:first');
                        if(prevItem.length) {
                            this.unselectAll();
                            this.selectItem(prevItem);
                            this.focusedItem = prevItem;

                            PrimeFaces.scrollInView(list, this.focusedItem);
                        }
                    }
                    this.updateAriaRegion();
                    e.preventDefault();
                break;

                case 'ArrowDown':
                    this.removeOutline();

                    if(!this.focusedItem.hasClass('p-state-highlight')) {
                        this.selectItem(this.focusedItem);
                    }
                    else {
                        const nextItem = this.focusedItem.nextAll('.p-picklist-item:visible:first');
                        if(nextItem.length) {
                            this.unselectAll();
                            this.selectItem(nextItem);
                            this.focusedItem = nextItem;

                            PrimeFaces.scrollInView(list, this.focusedItem);
                        }
                    }
                    this.updateAriaRegion();
                    e.preventDefault();
                break;

                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    if(this.focusedItem && this.focusedItem.hasClass('p-state-highlight')) {
                        this.focusedItem.trigger('dblclick.pickList');
                        this.focusedItem = null;
                    }
                    e.preventDefault();
                break;
                default:
                    // #3304 find first item matching the character typed
                    if (PrimeFaces.utils.isPrintableKey(e)) {
                        const keyChar = e.key!.toLowerCase();
                        list.children('.p-picklist-item').each((_i, el) => {
                            const item = $(el),
                                itemLabel = item.attr('data-item-label');
                            if (itemLabel && itemLabel.toLowerCase().indexOf(keyChar) === 0) {
                                this.removeOutline();
                                this.unselectAll();
                                this.selectItem(item);
                                this.focusedItem = item;
                                PrimeFaces.scrollInView(list, this.focusedItem);
                                this.updateAriaRegion();
                                e.preventDefault();
                                return false;
                            }
                            return undefined;
                        });
                    }
            }
        });
    }

    /**
     * Removes the outline from the item that is currently focused.
     */
    private removeOutline(): void {
        if(this.focusedItem && this.focusedItem.hasClass('p-picklist-outline')) {
            this.focusedItem.removeClass('p-picklist-outline');
        }
    }

    /**
     * Select the given pick list item in the source or target list.
     * @param item A picklist item to select, with the class `p-picklist-item`.
     * @param silent `true` to imit triggering event listeners and behaviors, or `false` otherwise.
     */
    selectItem(item: JQuery, silent?: boolean): void {
        item.addClass('p-state-highlight');

        if(this.cfg.showCheckbox && !this.cfg.transferOnCheckboxClick) {
            this.selectCheckbox(item.find('div.p-chkbox-box'));
        }

        if(silent) {
            this.fireItemSelectEvent(item);
        }

        this.updateButtonsState();
    }

    /**
     * Unselect the given pick list item in the source or target list.
     * @param item A picklist item to unselect, with the class `p-picklist-item`.
     * @param silent `true` to imit triggering event listeners and behaviors, or `false` otherwise.
     */
    unselectItem(item: JQuery, silent?: boolean): void {
        item.removeClass('p-state-hover');
        item.removeClass('p-state-highlight');

        if(this.cfg.showCheckbox) {
            const chkbox = item.find('div.p-chkbox-box');
            chkbox.removeClass('p-state-hover');

            if (!this.cfg.transferOnCheckboxClick) {
                this.unselectCheckbox(item.find('div.p-chkbox-box'));
            }
        }

        if(silent) {
            this.fireItemUnselectEvent(item);
        }

        this.updateButtonsState();
    }

    /**
     * Unselects all items in the source and target list.
     */
    unselectAll(): void {
        const selectedItems = this.items.filter('.p-state-highlight');
        for(let i = 0; i < selectedItems.length; i++) {
            this.unselectItem(selectedItems.eq(i));
        }
    }

    /**
     * Selects the given checkbox that belongs to a pick list item.
     * @param chkbox The hidden checkbox of a pick list item that was selected.
     */
    private selectCheckbox(chkbox: JQuery): void {
        chkbox.addClass('p-state-active').children('span.p-chkbox-icon').addClass('p-checkbox-checked-icon');
    }

    /**
     * Unselects the given checkbox that belongs to a pick list item.
     * @param chkbox The hidden checkbox of a pick list item that was unselected.
     */
    private unselectCheckbox(chkbox: JQuery): void {
        chkbox.removeClass('p-state-active').children('span.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
    }

    /**
     * Stores the current items in the given list in a hidden form field. Used for submitting the current value of this
     * pick list.
     * @param list The source or target list with items to store.
     * @param input The hidden form field where the items are stored.
     */
    private generateItems(list: JQuery, input: JQuery): void {
        list.children('.p-picklist-item').each((_i, el) => {
            const item = $(el),
            itemLabel = item.attr('data-item-label') ? PrimeFaces.escapeHTML(item.attr('data-item-label') as string) : '',
            option = $('<option selected="selected"></option>');
            let itemValue = item.attr('data-item-value');

            if (this.cfg.escapeValue) {
               itemValue = PrimeFaces.escapeHTML(itemValue as string);
            }
            option.prop('value', itemValue).text(itemLabel);
            input.append(option);
        });
    }

    /**
     * Sets tup the event listeners for when the command buttons (move up, move down etc.) are pressed.
     */
    private bindButtonEvents(): void {
        //visuals
        PrimeFaces.skinButton(this.jq.find('.p-button'));

        //events
        const moveToTarget = $(this.jqId + ' .p-picklist-button-add');
        const moveToTargetAria = this.getAriaLabel('moveToTarget');
        moveToTarget.attr('title', moveToTargetAria).find('.p-button-text').text(moveToTargetAria);
        moveToTarget.on("click", () => {
            this.add();
        });

        const moveAllToTarget = $(this.jqId + ' .p-picklist-button-add-all');
        const moveAllToTargetAria = this.getAriaLabel('moveAllToTarget');
        moveAllToTarget.attr('title', moveAllToTargetAria).find('.p-button-text').text(moveAllToTargetAria);
        moveAllToTarget.on("click", () => {
            this.addAll();
        });

        const moveToSource = $(this.jqId + ' .p-picklist-button-remove');
        const moveToSourceAria = this.getAriaLabel('moveToSource');
        moveToSource.attr('title', moveToSourceAria).find('.p-button-text').text(moveToSourceAria);
        moveToSource.on("click", () => {
            this.remove();
        });

        const moveAllToSource = $(this.jqId + ' .p-picklist-button-remove-all');
        const moveAllToSourceAria = this.getAriaLabel('moveAllToSource');
        moveAllToSource.attr('title', moveAllToSourceAria).find('.p-button-text').text(moveAllToSourceAria);
        moveAllToSource.on("click", () => {
            this.removeAll();
        });

        const moveUpAria = this.getAriaLabel('moveUp');
        const moveDownAria = this.getAriaLabel('moveDown');
        const moveTopAria = this.getAriaLabel('moveTop');
        const moveBottomAria = this.getAriaLabel('moveBottom');

        if(this.cfg.showSourceControls) {
            const moveUpSource = $(this.jqId + ' .p-picklist-source-controls .p-picklist-button-move-up');
            moveUpSource.attr('title', moveUpAria).find('.p-button-text').text(moveUpAria);
            moveUpSource.on("click", () => {
                this.moveUp(this.sourceList);
            });
            const moveTopSource = $(this.jqId + ' .p-picklist-source-controls .p-picklist-button-move-top');
            moveTopSource.attr('title', moveTopAria).find('.p-button-text').text(moveTopAria);
            moveTopSource.on("click", () => {
                this.moveTop(this.sourceList);
            });
            const moveDownSource = $(this.jqId + ' .p-picklist-source-controls .p-picklist-button-move-down');
            moveDownSource.attr('title', moveDownAria).find('.p-button-text').text(moveDownAria);
            moveDownSource.on("click", () => {
                this.moveDown(this.sourceList);
            });
            const moveBottomSource = $(this.jqId + ' .p-picklist-source-controls .p-picklist-button-move-bottom');
            moveBottomSource.attr('title', moveBottomAria).find('.p-button-text').text(moveBottomAria);
            moveBottomSource.on("click", () => {
                this.moveBottom(this.sourceList);
            });
        }

        if(this.cfg.showTargetControls) {
            const moveUpTarget = $(this.jqId + ' .p-picklist-target-controls .p-picklist-button-move-up');
            moveUpTarget.attr('title', moveUpAria).find('.p-button-text').text(moveUpAria);
            moveUpTarget.on("click", () => {
                this.moveUp(this.targetList);
            });
            const moveTopTarget = $(this.jqId + ' .p-picklist-target-controls .p-picklist-button-move-top');
            moveTopTarget.attr('title', moveTopAria).find('.p-button-text').text(moveTopAria);
            moveTopTarget.on("click", () => {
                this.moveTop(this.targetList);
            });
            const moveDownTarget = $(this.jqId + ' .p-picklist-target-controls .p-picklist-button-move-down');
            moveDownTarget.attr('title', moveDownAria).find('.p-button-text').text(moveDownAria);
            moveDownTarget.on("click", () => {
                this.moveDown(this.targetList);
            });
            const moveBottomTarget = $(this.jqId + ' .p-picklist-target-controls .p-picklist-button-move-bottom');
            moveBottomTarget.attr('title', moveBottomAria).find('.p-button-text').text(moveBottomAria);
            moveBottomTarget.on("click", () => {
                this.moveBottom(this.targetList);
            });
        }
    }

    /**
     * Sets up all event listeners for filtering the source and target lists.
     */
    private bindFilterEvents(): void {
        this.cfg.filterEvent = this.cfg.filterEvent||'keyup';
        this.cfg.filterDelay = this.cfg.filterDelay||300;
        this.setupFilterMatcher();

        this.sourceFilter = $(this.jqId + '_source_filter');
        this.targetFilter = $(this.jqId + '_target_filter');

        PrimeFaces.skinInput(this.sourceFilter);
        this.bindTextFilter(this.sourceFilter);

        PrimeFaces.skinInput(this.targetFilter);
        this.bindTextFilter(this.targetFilter);
    }

    /**
     * Sets up the event listeners for when text is entered into the filter input of the source or target list.
     * @param filter The filter input of the source or target list.
     */
    private bindTextFilter(filter: JQuery): void {
        if(this.cfg.filterEvent === 'enter') {
            this.bindEnterKeyFilter(filter);
        }
        else {
            this.bindFilterEvent(filter);
        }
    }

    /**
     * Sets up the event listeners for when the enter key is pressed while inside a filter input of the source or target
     * list.
     * @param filter The filter input of the source or target list.
     */
    private bindEnterKeyFilter(filter: JQuery): void {
        filter.on('keyup', (e) => {
            if (PrimeFaces.utils.blockEnterKey(e)) {
                this.filter((e.currentTarget as HTMLInputElement).value, this.getFilteredList($(e.currentTarget)));
            }
        });
    }

    /**
     * Sets up the event listeners for filtering the source and target lists.
     * @param filter The filter input of the source or target list.
     */
    private bindFilterEvent(filter: JQuery): void {
        //prevent form submit on enter key
        filter.on(this.cfg.filterEvent as string, (e) => {
            if (PrimeFaces.utils.ignoreFilterKey(e) || PrimeFaces.utils.blockEnterKey(e)) {
                return;
            }

            const input = $(e.currentTarget);

            PrimeFaces.debounce(() => this.filter(input.val() as string, this.getFilteredList(input)), this.cfg.filterDelay);
        });
    }

    /**
     * Finds and stores the filter function which is to be used for filtering the options of this pick list.
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
     * Filters the available options in the source or target list.
     * @param value A value against which the available options are matched.
     * @param list The source or target list that is to be filtered.
     * @param animate If it should be animated.
     */
    filter(value: string, list: JQuery, animate?: boolean): void {
        const normalize = this.cfg.filterNormalize as boolean,
            filterValue = PrimeFaces.toSearchable(PrimeFaces.trim(value), true, normalize),
            items = list.children('li.p-picklist-item'),
            animated = animate || this.isAnimated();

        list.removeAttr('role');

        if(filterValue === '') {
            items.filter(':hidden').show();
            list.attr('role', 'menu');
        }
        else {
            for(let i = 0; i < items.length; i++) {
                const item = items.eq(i),
                itemLabel = PrimeFaces.toSearchable(item.attr('data-item-label') as string, false, normalize),
                matches = this.filterMatcher!(itemLabel, filterValue);

                if(matches) {
                    const hasRole = list[0]!.hasAttribute('role');
                    if(animated) {
                        item.fadeIn('fast', () => {
                            if(!hasRole) {
                                list.attr('role', 'menu');
                            }
                        });
                    }
                    else {
                        item.show();
                        if(!hasRole) {
                            list.attr('role', 'menu');
                        }
                    }
                }
                else if(animated) {
                    item.fadeOut('fast');
                }
                else {
                    item.hide();
                }
            }
        }

    }

    /**
     * Implementation of a `PrimeFaces.widget.PickList.FilterFunction` that matches the given option when it starts with
     * the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the options starts with the filter value, or `false` otherwise.
     */
    startsWithFilter(value: string, filter: string): boolean {
        return value.toLowerCase().indexOf(filter) === 0;
    }

    /**
     * Implementation of a `PrimeFaces.widget.PickList.FilterFunction` that matches the given option when it contains
     * the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the contains the filter value, or `false` otherwise.
     */
    containsFilter(value: string, filter: string): boolean {
        return value.toLowerCase().indexOf(filter) !== -1;
    }

    /**
     * Implementation of a `PrimeFaces.widget.PickList.FilterFunction` that matches the given option when it ends with
     * the given search text.
     * @param value Text of an option.
     * @param filter Value of the filter.
     * @returns `true` when the text of the options ends with the filter value, or `false` otherwise.
     */
    endsWithFilter(value: string, filter: string): boolean {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    }

    /**
     * Finds the list belonging to the given filter input.
     * @param filter The filter input of either the target or source list.
     * @returns The list to which the given filter input applies.
     */
    private getFilteredList(filter: JQuery): JQuery {
        return filter.hasClass('p-source-filter-input') ? this.sourceList : this.targetList;
    }

    /**
     * Adds all selected items in the source list by transferring them to the target list.
     */
    add(): void {
        const items = this.sourceList.children('li.p-picklist-item.p-state-highlight');

        this.transfer(items, this.sourceList, this.targetList, 'command');
    }

    /**
     * Adds all items to the target list by transferring all items from the source list to the target list.
     */
    addAll(): void {
        const items = this.sourceList.children('li.p-picklist-item:visible:not(.p-state-disabled)');

        this.transfer(items, this.sourceList, this.targetList, 'command');
    }

    /**
     * Removes all selected items in the target list by transferring them to the source list.
     */
    remove(): void {
        const items = this.targetList.children('li.p-picklist-item.p-state-highlight');

        this.transfer(items, this.targetList, this.sourceList, 'command');
    }

    /**
     * Removes all items in the target list by transferring all items from the target list to the source list.
     */
    removeAll(): void {
        const items = this.targetList.children('li.p-picklist-item:visible:not(.p-state-disabled)');

        this.transfer(items, this.targetList, this.sourceList, 'command');
    }

    /**
     * Moves the items that are currently selected up by one.
     * @param list The source or target list with items to move up.
     */
    moveUp(list: JQuery): void {
        const animated = this.isAnimated(),
        items = list.children('.p-state-highlight'),
        itemsCount = items.length;
        let movedCount = 0;

        if(itemsCount) {
            items.each((_i, el) => {
                const item = $(el);

                if(!item.is(':first-child')) {

                    if(animated) {
                        PrimeFaces.effects.hide(item, this.cfg.effect, { duration: this.cfg.effectSpeed, complete: () => {
                            PrimeFaces.effects.show(item.insertBefore(item.prev()), this.cfg.effect, { duration: this.cfg.effectSpeed, complete: () => {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    this.saveState();
                                    this.fireReorderEvent();
                                }
                            } });
                        } });
                    }
                    else {
                        item.hide().insertBefore(item.prev()).show();
                    }

                }
            });

            if(!animated) {
                this.saveState();
                this.fireReorderEvent();
            }
        }

    }

    /**
     * Moves the items that are currently selected to the top of the source of target list.
     * @param list The source or target list with items to move to the top.
     */
    moveTop(list: JQuery): void {
        const animated = this.isAnimated(),
        items = list.children('.p-state-highlight'),
        itemsCount = items.length;
        let movedCount = 0;

        if(itemsCount) {
            items.each((_i, el) => {
                const item = $(el);

                if(!item.is(':first-child')) {

                    if(animated) {
                        PrimeFaces.effects.hide(item, this.cfg.effect, { duration: this.cfg.effectSpeed, complete: () => {
                            PrimeFaces.effects.show(item.prependTo(item.parent()), this.cfg.effect, { duration: this.cfg.effectSpeed, complete: () => {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    this.saveState();
                                    this.fireReorderEvent();
                                }
                            } });
                        } });
                    }
                    else {
                        item.hide().prependTo(item.parent()).show();
                    }
                }
            });

            if(!animated) {
                this.saveState();
                this.fireReorderEvent();
            }
        }
    }

    /**
     * Moves the items that are currently selected down by one.
     * @param list The source or target list with items to move down.
     */
    moveDown(list: JQuery): void {
        const animated = this.isAnimated(),
        items = list.children('.p-state-highlight'),
        itemsCount = items.length;
        let movedCount = 0;

        if(itemsCount) {
            $(items.get().reverse()).each((_i, el) => {
                const item = $(el);

                if(!item.is(':last-child')) {
                    if(animated) {
                        PrimeFaces.effects.hide(item, this.cfg.effect, { duration: this.cfg.effectSpeed, complete: () => {
                            PrimeFaces.effects.show(item.insertAfter(item.next()), this.cfg.effect, { duration: this.cfg.effectSpeed, complete: () => {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    this.saveState();
                                    this.fireReorderEvent();
                                }
                            } });
                        } });
                    }
                    else {
                        item.hide().insertAfter(item.next()).show();
                    }
                }

            });

            if(!animated) {
                this.saveState();
                this.fireReorderEvent();
            }
        }
    }

    /**
     * Moves the items that are currently selected to the bottom of the source of target list.
     * @param list The source or target list with items to move to the bottom.
     */
    moveBottom(list: JQuery): void {
        const animated = this.isAnimated(),
        items = list.children('.p-state-highlight'),
        itemsCount = items.length;
        let movedCount = 0;

        if(itemsCount) {
            items.each((_i, el) => {
                const item = $(el);

                if(!item.is(':last-child')) {

                    if(animated) {
                        PrimeFaces.effects.hide(item, this.cfg.effect, { duration: this.cfg.effectSpeed, complete: () => {
                            PrimeFaces.effects.show(item.appendTo(item.parent()), this.cfg.effect, { duration: this.cfg.effectSpeed, complete: () => {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    this.saveState();
                                    this.fireReorderEvent();
                                }
                            } });
                        } });
                    }
                    else {
                        item.hide().appendTo(item.parent()).show();
                    }
                }

            });

            if(!animated) {
                this.saveState();
                this.fireReorderEvent();
            }
        }
    }

    /**
     * Saves the current state of this widget, i.e. to which list the items are currently assigned. Clears inputs and
     * repopulates them from the list states.
     */
    private saveState(): void {
        this.sourceInput.children().remove();
        this.targetInput.children().remove();

        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);
        this.cursorItem = null;
    }

    /**
     * Transfers the given items from the source or target list to the other list.
     * @param items Items that were transferred from one list to the other.
     * @param from List from which the items were transferred.
     * @param to List to which the items were transferred.
     * @param type Type of the action that caused the items to be transferred.
     * @param callback after transfer finished.
     */
    transfer(items: JQuery, from: JQuery, to: JQuery, type: PrimeFacesTypes.widget.PickList.TransferType, callback?: () => void): void {
        this.toggleDragDrop(false);
        const $this = this;
        const itemsCount = items.length;
        let transferCount = 0;

        if(this.isAnimated()) {
            PrimeFaces.effects.hide(items, this.cfg.effect, { duration: this.cfg.effectSpeed, complete: function() {
                const item = $(this);
                $this.unselectItem(item);

                if ($this.cfg.transferOnCheckboxClick) {
                    if (from.hasClass('p-picklist-source')) {
                        $this.selectCheckbox(item.find('div.p-chkbox-box'));
                    }
                    else {
                        $this.unselectCheckbox(item.find('div.p-chkbox-box'));
                    }
                }

                PrimeFaces.effects.show(item.appendTo(to), $this.cfg.effect, { duration: $this.cfg.effectSpeed, complete: function() {
                    transferCount++;

                    //fire transfer when all items are transferred
                    if(transferCount == itemsCount) {
                        $this.saveState();
                        $this.fireTransferEvent(items, from, to, type);
                    }
                } });

                $this.updateListRole();

                if (callback) {
                    callback.call($this);
                }
            } });
        }
        else {
            items.hide();

            if(this.cfg.showCheckbox) {
                items.each((_i, el) => {
                    const item = $(el);
                    this.unselectItem(item);

                    if (this.cfg.transferOnCheckboxClick) {
                        if (from.hasClass('p-picklist-source')) {
                            this.selectCheckbox(item.find('div.p-chkbox-box'));
                        }
                        else {
                            this.unselectCheckbox(item.find('div.p-chkbox-box'));
                        }
                    }
                });
            }

            items.appendTo(to).show();

            this.saveState();
            this.fireTransferEvent(items, from, to, type);
            this.updateListRole();

            if (callback) {
                callback.call($this);
            }
        }
    }

    /**
     * Triggers the behavior for when pick list items are transferred from the source to the target list or vice-versa.
     * @param items Items that were transferred from one list to the other.
     * @param from List from which the items were transferred.
     * @param to List to which the items were transferred.
     * @param type Type of the action that caused the items to be transferred.
     */
    private fireTransferEvent(items: JQuery, from: JQuery, to: JQuery, type: PrimeFacesTypes.widget.PickList.TransferType): void {
        if(this.cfg.onTransfer) {
            const obj = {} as PrimeFacesTypes.widget.PickList.TransferData;
            obj.items = items;
            obj.from = from;
            obj.to = to;
            obj.type = type;

            this.cfg.onTransfer.call(this, obj);
        }

        if(this.hasBehavior('transfer')) {
            const isAdd = from.hasClass('p-picklist-source');

            const options = {
                params: [
                    {name: this.id + '_add', value: isAdd}
                ] as PrimeFacesTypes.ajax.RequestParameter[],
                oncomplete: () => {
                    this.refilterSource();
                    this.refilterTarget();
                    this.toggleDragDrop(true);
                    this.updateButtonsState();
                }
            };

            items.each((_index, item) => {
                options.params.push({name: this.id + '_transferred', value: $(item).attr('data-item-value')});
            });

            this.callBehavior('transfer', options);
        }
        else {
            this.toggleDragDrop(true);
            this.updateButtonsState();
        }

        this.fireInputChanged();
    }

    /**
     * Finds the type of the given list, i.e. whether the list represents the source or target list.
     * @param element A list element to check.
     * @returns Whether the element represents the source or target list.
     */
    private getListName(element: JQuery): PrimeFacesTypes.widget.PickList.ListName {
        return element.parent().hasClass("p-picklist-source") ? "source" : "target";
    }

    /**
     * Triggers the behavior for when pick list items are selected.
     * @param item A pick list item that was selected.
     */
    private fireItemSelectEvent(item: JQuery): void {
        if(this.hasBehavior('select')) {
            const listName = this.getListName(item),
            inputContainer = (listName === "source") ? this.sourceInput : this.targetInput,
            ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_listName', value: listName}
                ],
                onstart: () => {
                    if(!inputContainer.children().length) {
                        return false;
                    }
                    // the AJAX request is only aborted when onstart returns false
                    return true;
                }
            };

            this.callBehavior('select', ext);
        }
    }

    /**
     * Triggers the behavior for when pick list items are unselected.
     * @param item A pick list item that was unselected.
     */
    private fireItemUnselectEvent(item: JQuery): void {
        if(this.hasBehavior('unselect')) {
            const ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_listName', value: this.getListName(item)}
                ]
            };

            this.callBehavior('unselect', ext);
        }
    }

    /**
     * Triggers the behavior for when pick list items are reordered.
     */
    private fireReorderEvent(): void {
        this.callBehavior('reorder');
        this.fireInputChanged();
        this.updateButtonsState();
    }

    /**
     * Triggers change events on the input fields.
     */
    private fireInputChanged(): void {
        // #11540: trigger change events on inputs for form handling
        this.targetInput.trigger("change");
        this.sourceInput.trigger("change");
    }

    /**
     * Checks whether UI actions of this pick list are animated.
     * @returns A truthy value if this pick list is animated, or a falsy value otherwise.
     */
    isAnimated(): boolean | "" | undefined {
        return (this.cfg.effect && this.cfg.effect != 'none');
    }

    /**
     * Applies the tab index to this pick list widget.
     */
    private setTabIndex(): void {
        const tabindex = (this.cfg.disabled) ? '-1' : this.getTabIndex();
        this.sourceList.attr('tabindex', tabindex);
        this.targetList.attr('tabindex', tabindex);
        $(this.jqId + ' button').attr('tabindex', tabindex);
        $(this.jqId + ' .p-picklist-filter-container > input').attr('tabindex', tabindex);
    }

    /**
     * Finds the tab index of this pick list widget.
     * @returns The tab index of this pick list.
     */
    private getTabIndex(): string {
        return this.cfg.tabindex||'0';
    }

    /**
     * Updates the state of all buttons of this pick list, such as whether they are disabled or enabled.
     */
    private updateButtonsState(): void {
        const addButton = $(this.jqId + ' .p-picklist-button-add');
        const sourceListButtons = $(this.jqId + ' .p-picklist-source-controls .p-button');
        const sourceSelectedItems = this.sourceList.find('li.p-state-highlight');
        if (sourceSelectedItems.length) {
            this.enableButton(addButton);

            // Handle move-up and move-down buttons separately
            const sourceMoveUpButton = $(this.jqId + ' .p-picklist-source-controls .p-picklist-button-move-up');
            const sourceMoveDownButton = $(this.jqId + ' .p-picklist-source-controls .p-picklist-button-move-down');
            const sourceMoveTopButton = $(this.jqId + ' .p-picklist-source-controls .p-picklist-button-move-top');
            const sourceMoveBottomButton = $(this.jqId + ' .p-picklist-source-controls .p-picklist-button-move-bottom');

            // Get first and last selected items
            const firstSelectedItem = sourceSelectedItems.first();
            const lastSelectedItem = sourceSelectedItems.last();

            // Check if first selected item is the first child (can't move up)
            if (firstSelectedItem.is(':first-child')) {
                this.disableButton(sourceMoveUpButton);
                this.disableButton(sourceMoveTopButton);
            }
            else {
                this.enableButton(sourceMoveUpButton);
                this.enableButton(sourceMoveTopButton);
            }

            // Check if last selected item is the last child (can't move down)
            if (lastSelectedItem.is(':last-child')) {
                this.disableButton(sourceMoveDownButton);
                this.disableButton(sourceMoveBottomButton);
            }
            else {
                this.enableButton(sourceMoveDownButton);
                this.enableButton(sourceMoveBottomButton);
            }
        }
        else {
            this.disableButton(addButton);
            this.disableButton(sourceListButtons);
        }

        const removeButton = $(this.jqId + ' .p-picklist-button-remove');
        const targetListButtons = $(this.jqId + ' .p-picklist-target-controls .p-button');
        const targetSelectedItems = this.targetList.find('li.p-state-highlight');
        if (targetSelectedItems.length) {
            this.enableButton(removeButton);

            // Handle move-up and move-down buttons separately
            const targetMoveUpButton = $(this.jqId + ' .p-picklist-target-controls .p-picklist-button-move-up');
            const targetMoveDownButton = $(this.jqId + ' .p-picklist-target-controls .p-picklist-button-move-down');
            const targetMoveTopButton = $(this.jqId + ' .p-picklist-target-controls .p-picklist-button-move-top');
            const targetMoveBottomButton = $(this.jqId + ' .p-picklist-target-controls .p-picklist-button-move-bottom');

            // Get first and last selected items
            const firstSelectedItem = targetSelectedItems.first();
            const lastSelectedItem = targetSelectedItems.last();

            // Check if first selected item is the first child (can't move up)
            if (firstSelectedItem.is(':first-child')) {
                this.disableButton(targetMoveUpButton);
                this.disableButton(targetMoveTopButton);
            }
            else {
                this.enableButton(targetMoveUpButton);
                this.enableButton(targetMoveTopButton);
            }

            // Check if last selected item is the last child (can't move down)
            if (lastSelectedItem.is(':last-child')) {
                this.disableButton(targetMoveDownButton);
                this.disableButton(targetMoveBottomButton);
            }
            else {
                this.enableButton(targetMoveDownButton);
                this.enableButton(targetMoveBottomButton);
            }
        }
        else {
            this.disableButton(removeButton);
            this.disableButton(targetListButtons);
        }

        const addAllButton = $(this.jqId + ' .p-picklist-button-add-all');
        if (this.sourceList.find('li.p-picklist-item:not(.p-state-disabled)').length) {
            this.enableButton(addAllButton);
            this.sourceList.attr('tabindex', this.getTabIndex());
        }
        else {
            this.disableButton(addAllButton);
            this.sourceList.attr('tabindex', '-1');
        }

        const removeAllButton = $(this.jqId + ' .p-picklist-button-remove-all');
        if (this.targetList.find('li.p-picklist-item:not(.p-state-disabled)').length) {
            this.enableButton(removeAllButton);
            this.targetList.attr('tabindex', this.getTabIndex());
        }
        else {
            this.disableButton(removeAllButton);
            this.targetList.attr('tabindex', '-1');
        }
    }

    /**
     * Reapply filtering the current source list.
     */
    private refilterSource(): void {
        this.filter(this.sourceFilter!.val() as string, this.sourceList, false);
    }

    /**
     * Reapply filtering to the current target list.
     */
    private refilterTarget(): void {
        this.filter(this.targetFilter!.val() as string, this.targetList, false);
    }

    /**
     * Disables the given button belonging to this pick list.
     * @param button A button to disable.
     */
    private disableButton(button: JQuery): void {
        if (button.hasClass('p-state-focus')) {
            button.trigger("blur");
        }

        button.attr('disabled', 'disabled').addClass('p-state-disabled');
        button.attr('tabindex', '-1');
    }

    /**
     * Enables the given button belonging to this pick list.
     * @param button A button to enable.
     */
    private enableButton(button: JQuery): void {
        button.prop('disabled', false).removeClass('p-state-disabled');
        button.attr('tabindex', this.getTabIndex());
    }

    /**
     * Enables or disables drag and drop functionality for the pick list items.
     * Has no effect on touch devices where drag and drop is always disabled.
     * @param enable Set to true to enable drag and drop, false to disable
     */
    private toggleDragDrop(enable: boolean = false): void {
        if (PrimeFaces.env.isTouchable(this.cfg)) {
            return;
        }
        if (enable) {
            PrimeFaces.interactions.enable($(this.jqId + ' ul'), 'sortable');
        }
        else {
            PrimeFaces.interactions.disable($(this.jqId + ' ul'), 'sortable');
        }
    }

    /**
     * Updates the `role` attribute of the source and target pick list items.
     */
    private updateListRole(): void {
        this.sourceList.children('li:visible').length > 0 ? this.sourceList.attr('role', 'menu') : this.sourceList.removeAttr('role');
        this.targetList.children('li:visible').length > 0 ? this.targetList.attr('role', 'menu') : this.targetList.removeAttr('role');
    }

    /**
     * Updates the `aria-grion` with the focused label text.
     */
    private updateAriaRegion(): void {
        if (!this.focusedItem || !this.focusedItem.length) {
            return;
        }
        let labelText = this.focusedItem.data('item-label');
        labelText = (labelText === undefined || labelText === null) ? '' : String(labelText);
        // #15476 do not touch the live region if the label did not change, any DOM mutation
        // of an aria-live region causes screen readers to announce it again
        if (this.ariaRegion.text() === labelText) {
            return;
        }
        this.ariaRegion.text(labelText);
    }

}
