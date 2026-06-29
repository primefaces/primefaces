/**
 * __PrimeFaces PickList Widget__
 *
 * PickList is used for transferring data between two different collections.
 *
 * @typedef {"source" | "target"} PrimeFaces.widget.PickList.ListName The type for the two lists comprising the pick
 * list, i.e. whether a list contain the source or target items.
 *
 * @typedef {"command" | "dblclick" | "dragdrop"} PrimeFaces.widget.PickList.TransferType Indicates how an item was
 * transferred from one list to the other.
 * - `command`: The item was transferred as a result of the user clicking one of the command buttons next to the lists.
 * - `dblclick`: The item was transferred as a result of a double click by the user.
 * - `dragdrop`:  The item was transferred as a result of a drag&drop interaction by the user.
 *
 * @typedef {"startsWith" |  "contains" |  "endsWith" | "custom"} PrimeFaces.widget.PickList.FilterMatchMode
 * Available modes for filtering the options of a pick list. When `custom` is set, a `filterFunction` must be specified.
 *
 * @typedef PrimeFaces.widget.PickList.FilterFunction A function for filtering the options of a pick list box.
 * @param {string} PrimeFaces.widget.PickList.FilterFunction.itemLabel The label of the currently selected text.
 * @param {string} PrimeFaces.widget.PickList.FilterFunction.filterValue The value to search for.
 * @return {boolean} PrimeFaces.widget.PickList.FilterFunction `true` if the item label matches the filter value, or
 * `false` otherwise.
 *
 * @typedef PrimeFaces.widget.PickList.OnTransferCallback Callback that is invoked when items are transferred from one
 * list to the other. See also {@link PickListCfg.onTransfer}.
 * @param {PrimeFaces.widget.PickList.TransferData} PrimeFaces.widget.PickList.OnTransferCallback.transferData Details
 * about the pick list item that was transferred.
 *
 * @interface {PrimeFaces.widget.PickList.TransferData} TransferData Callback that is invoked when an item was
 * transferred from one list to the other.
 * @prop {JQuery} TransferData.items Items that were transferred from one list to the other.
 * @prop {JQuery} TransferData.from List from which the items were transferred.
 * @prop {JQuery} TransferData.to List to which the items were transferred.
 * @prop {PrimeFaces.widget.PickList.TransferType} TransferData.type Type of the action that caused the items to be
 * transferred.
 *
 * @prop {JQuery} ariaRegion The DOM element for the aria region with the `aria-*` attributes
 * @prop {JQuery} checkboxes The DOM elements for the checkboxes next to each pick list item.
 * @prop {boolean} checkboxClick UI state indicating whether a checkbox was just clicked.
 * @prop {JQuery} cursorItem The currently selected item.
 * @prop {boolean} dragging Whether the user is currently transferring an item via drag&drop.
 * @prop {number} filterTimeout The set-timeout timer ID of the timer for the delay when filtering the source or target
 * list.
 * @prop {PrimeFaces.widget.PickList.FilterFunction} filterMatcher The filter that was selected and is currently used.
 * @prop {Record<PrimeFaces.widget.PickList.FilterMatchMode, PrimeFaces.widget.PickList.FilterFunction>} filterMatchers
 * Map between the available filter types and the filter implementation.
 * @prop {JQuery} focusedItem The DOM element for the currently focused pick list item, if any.
 * @prop {JQuery} items The DOM elements for the pick list items in the source and target list.
 * @prop {PrimeFaces.widget.PickList.ListName} itemListName When sorting items: to which list the items belong.
 * @prop {JQuery} sourceFilter The DOM element for the filter input for the source list.
 * @prop {JQuery} sourceInput The DOM element for the hidden input storing the value of the source list.
 * @prop {JQuery} sourceList The DOM element for the source list.
 * @prop {} targetFilter The DOM element for the filter input for the target list.
 * @prop {} targetInput The DOM element for the hidden input storing the value of the target list.
 * @prop {JQuery} targetList The DOM element for the target list.
 *
 * @interface {PrimeFaces.widget.PickListCfg} cfg The configuration for the {@link  PickList| PickList widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.disabled Whether this pick list is initially disabled.
 * @prop {string} cfg.effect Name of the animation to display.
 * @prop {string} cfg.effectSpeed Speed of the animation.
 * @prop {boolean} cfg.escapeValue Whether the item values are escaped for HTML.
 * @prop {number} cfg.filterDelay Delay to wait in milliseconds before sending each filter query. Default is `300`.
 * @prop {string} cfg.filterEvent Client side event to invoke picklist filtering for input fields. Default is `keyup`.
 * @prop {PrimeFaces.widget.PickList.FilterFunction} cfg.filterFunction A custom filter function that is used when
 * `filterMatchMode` is set to `custom`.
 * @prop {PrimeFaces.widget.PickList.FilterMatchMode} cfg.filterMatchMode Mode of the filter. When set to `custom, a
 * `filterFunction` must be specified.
 * @prop {PrimeFaces.widget.PickList.OnTransferCallback} cfg.onTransfer Callback that is invoked when items are
 * transferred from one list to the other.
 * @prop {boolean} cfg.showCheckbox When true, a checkbox is displayed next to each item.
 * @prop {boolean} cfg.showSourceControls Specifies visibility of reorder buttons of source list.
 * @prop {boolean} cfg.showTargetControls Specifies visibility of reorder buttons of target list.
 * @prop {string} cfg.tabindex Position of the element in the tabbing order.
 */
PrimeFaces.widget.PickList = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.sourceList = this.jq.find('ul.ui-picklist-source');
        this.targetList = this.jq.find('ul.ui-picklist-target');
        this.sourceInput = $(this.jqId + '_source');
        this.targetInput = $(this.jqId + '_target');
        this.items = this.jq.find('.ui-picklist-item:not(.ui-state-disabled)');
        if(this.cfg.showCheckbox) {
            this.checkboxes = this.items.find('div.ui-chkbox > div.ui-chkbox-box');
        }
        this.focusedItem = null;
        this.ariaRegion = $(this.jqId + '_ariaRegion');

        var sourceCaption = this.sourceList.prev('.ui-picklist-caption'),
            targetCaption = this.targetList.prev('.ui-picklist-caption');

        if(sourceCaption.length) {
            var captionText = sourceCaption.text();

            this.sourceList.attr('aria-label', captionText);
            this.sourceInput.attr('title', captionText);
        }

        if(targetCaption.length) {
            var captionText = targetCaption.text();

            this.targetList.attr('aria-label', captionText);
            this.targetInput.attr('title', captionText);
        }

        this.setTabIndex();

        //generate input options
        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);

        if(this.cfg.disabled) {
            $(this.jqId + ' li.ui-picklist-item').addClass('ui-state-disabled');
            $(this.jqId + ' button').attr('disabled', 'disabled').addClass('ui-state-disabled');
            $(this.jqId + ' .ui-picklist-filter-container').addClass('ui-state-disabled').children('input').attr('disabled', 'disabled');
        }
        else {
            var $this = this,
                reordered = true;

            //Sortable lists
            $(this.jqId + ' ul').sortable({
                cancel: '.ui-state-disabled,.ui-chkbox-box',
                connectWith: this.jqId + ' .ui-picklist-list',
                revert: 1,
                helper: 'clone',
                placeholder: "ui-picklist-item ui-state-highlight",
                forcePlaceholderSize: true,
                update: function(event, ui) {
                    $this.unselectItem(ui.item);

                    $this.saveState();
                    if(reordered) {
                        $this.fireReorderEvent();
                        reordered = false;
                    }
                },
                receive: function(event, ui) {
                    $this.fireTransferEvent(ui.item, ui.sender, ui.item.parents('ul.ui-picklist-list:first'), 'dragdrop');
                },

                start: function(event, ui) {
                    $this.itemListName = $this.getListName(ui.item);
                    $this.dragging = true;
                },

                stop: function(event, ui) {
                    $this.dragging = false;
                },

                beforeStop:function(event, ui) {
                    if($this.itemListName !== $this.getListName(ui.item)) {
                        reordered = false;
                    }
                    else {
                        reordered = true;
                    }
                }
            });

            this.bindItemEvents();

            this.bindButtonEvents();

            this.bindFilterEvents();

            this.bindKeyEvents();

            this.updateButtonsState();

            this.updateListRole();
        }
    },

    /**
     * Sets up the event listeners for selecting and transferring pick list items.
     * @private
     */
    bindItemEvents: function() {
        var $this = this;

        this.items.on('mouseover.pickList', function(e) {
            $(this).addClass('ui-state-hover');
        })
        .on('mouseout.pickList', function(e) {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.pickList', function(e) {
            //stop propagation
            if($this.checkboxClick||$this.dragging) {
                $this.checkboxClick = false;
                return;
            }

            var item = $(this),
            parentList = item.parent(),
            metaKey = (e.metaKey||e.ctrlKey);

            if(!e.shiftKey) {
                if(!metaKey) {
                    $this.unselectAll();
                }

                if(metaKey && item.hasClass('ui-state-highlight')) {
                    $this.unselectItem(item, true);
                }
                else {
                    $this.selectItem(item, true);
                    $this.cursorItem = item;
                }
            }
            else {
                $this.unselectAll();

                if($this.cursorItem && ($this.cursorItem.parent().is(item.parent()))) {
                    var currentItemIndex = item.index(),
                    cursorItemIndex = $this.cursorItem.index(),
                    startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex,
                    endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1);

                    for(var i = startIndex ; i < endIndex; i++) {
                        var it = parentList.children('li.ui-picklist-item').eq(i);

                        if(it.is(':visible')) {
                            if(i === (endIndex - 1))
                                $this.selectItem(it, true);
                            else
                                $this.selectItem(it);
                        }
                    }
                }
                else {
                    $this.selectItem(item, true);
                    $this.cursorItem = item;
                }
            }

            /* For keyboard navigation */
            $this.removeOutline();
            $this.focusedItem = item;
            parentList.trigger('focus.pickList');
        })
        .on('dblclick.pickList', function() {
            var item = $(this);

            if($(this).parent().hasClass('ui-picklist-source'))
                $this.transfer(item, $this.sourceList, $this.targetList, 'dblclick');
            else
                $this.transfer(item, $this.targetList, $this.sourceList, 'dblclick');

            /* For keyboard navigation */
            $this.removeOutline();
            $this.focusedItem = null;

            PrimeFaces.clearSelection();
        });

        if(this.cfg.showCheckbox) {
            this.checkboxes.on('mouseenter.pickList', function(e) {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseleave.pickList', function(e) {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.pickList', function(e) {
                $this.checkboxClick = true;

                var item = $(this).closest('li.ui-picklist-item');
                if(item.hasClass('ui-state-highlight')) {
                    $this.unselectItem(item, true);
                }
                else {
                    $this.selectItem(item, true);
                }
                $this.focusedItem = item;
            });
        }
    },

    /**
     * Sets up the keyboard event listeners for navigating the pick list via keyboard keys.
     * @private
     */
    bindKeyEvents: function() {
        var $this = this,
            listSelector = 'ul.ui-picklist-source, ul.ui-picklist-target';

        this.jq.off('focus.pickList blur.pickList keydown.pickList', listSelector).on('focus.pickList', listSelector, null, function(e) {
            var list = $(this),
                activeItem = $this.focusedItem||list.children('.ui-state-highlight:visible:first');
            if(activeItem.length) {
                $this.focusedItem = activeItem;
            }
            else {
                $this.focusedItem = list.children('.ui-picklist-item:visible:first');
            }

            setTimeout(function() {
                if ($this.focusedItem) {
                    PrimeFaces.scrollInView(list, $this.focusedItem);
                    $this.focusedItem.addClass('ui-picklist-outline');
                    $this.ariaRegion.text($this.focusedItem.data('item-label'));
                }
            }, 100);
        })
        .on('blur.pickList', listSelector, null, function() {
            $this.removeOutline();
            $this.focusedItem = null;
        })
        .on('keydown.pickList', listSelector, null, function(e) {

            if(!$this.focusedItem) {
                return;
            }

            var list = $(this),
                keyCode = $.ui.keyCode,
                key = e.which;

            switch(key) {
                case keyCode.UP:
                    $this.removeOutline();

                    if(!$this.focusedItem.hasClass('ui-state-highlight')) {
                        $this.selectItem($this.focusedItem);
                    }
                    else {
                        var prevItem = $this.focusedItem.prevAll('.ui-picklist-item:visible:first');
                        if(prevItem.length) {
                            $this.unselectAll();
                            $this.selectItem(prevItem);
                            $this.focusedItem = prevItem;

                            PrimeFaces.scrollInView(list, $this.focusedItem);
                        }
                    }
                    $this.ariaRegion.text($this.focusedItem.data('item-label'));
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    $this.removeOutline();

                    if(!$this.focusedItem.hasClass('ui-state-highlight')) {
                        $this.selectItem($this.focusedItem);
                    }
                    else {
                        var nextItem = $this.focusedItem.nextAll('.ui-picklist-item:visible:first');
                        if(nextItem.length) {
                            $this.unselectAll();
                            $this.selectItem(nextItem);
                            $this.focusedItem = nextItem;

                            PrimeFaces.scrollInView(list, $this.focusedItem);
                        }
                    }
                    $this.ariaRegion.text($this.focusedItem.data('item-label'));
                    e.preventDefault();
                break;

                case keyCode.ENTER:
                case keyCode.SPACE:
                    if($this.focusedItem && $this.focusedItem.hasClass('ui-state-highlight')) {
                        $this.focusedItem.trigger('dblclick.pickList');
                        $this.focusedItem = null;
                    }
                    e.preventDefault();
                break;
                default:
                    // #3304 find first item matching the character typed
                    var keyChar = String.fromCharCode(key).toLowerCase();
                    list.children('.ui-picklist-item').each(function() {
                        var item = $(this),
                            itemLabel = item.attr('data-item-label');
                        if (itemLabel.toLowerCase().indexOf(keyChar) === 0) {
                            $this.removeOutline();
                            $this.unselectAll();
                            $this.selectItem(item);
                            $this.focusedItem = item;
                            PrimeFaces.scrollInView(list, $this.focusedItem);
                            $this.ariaRegion.text($this.focusedItem.data('item-label'));
                            e.preventDefault();
                            return false;
                        }
                    });
                break;
            };
        });

    },

    /**
     * Removes the outline from the item that is currently focused.
     * @private
     */
    removeOutline: function() {
        if(this.focusedItem && this.focusedItem.hasClass('ui-picklist-outline')) {
            this.focusedItem.removeClass('ui-picklist-outline');
        }
    },

    /**
     * Select the given pick list item in the source or target list.
     * @param {JQuery} item A picklist item to select, with the class `ui-picklist-item`.
     * @param {boolean} [silent] `true` to imit triggering event listeners and behaviors, or `false` otherwise.
     */
    selectItem: function(item, silent) {
        item.addClass('ui-state-highlight');

        if(this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.ui-chkbox-box'));
        }

        if(silent) {
            this.fireItemSelectEvent(item);
        }

        this.updateButtonsState();
    },

    /**
     * Unselect the given pick list item in the source or target list.
     * @param {JQuery} item A picklist item to unselect, with the class `ui-picklist-item`.
     * @param {boolean} [silent] `true` to imit triggering event listeners and behaviors, or `false` otherwise.
     */
    unselectItem: function(item, silent) {
        item.removeClass('ui-state-highlight');

        if(this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.ui-chkbox-box'));
        }

        if(silent) {
            this.fireItemUnselectEvent(item);
        }

        this.updateButtonsState();
    },

    /**
     * Unselects all items in the source and target list.
     */
    unselectAll: function() {
        var selectedItems = this.items.filter('.ui-state-highlight');
        for(var i = 0; i < selectedItems.length; i++) {
            this.unselectItem(selectedItems.eq(i));
        }
    },

    /**
     * Selects the given checkbox that belongs to a pick list item.
     * @private
     * @param {JQuery} chkbox The hidden checkbox of a pick list item that was selected.
     */
    selectCheckbox: function(chkbox) {
        chkbox.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
    },

    /**
     * Unselects the given checkbox that belongs to a pick list item.
     * @private
     * @param {JQuery} chkbox The hidden checkbox of a pick list item that was unselected.
     */
    unselectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
    },

    /**
     * Stores the current items in the given list in a hidden form field. Used for submitting the current value of this
     * pick list.
     * @private
     * @param {JQuery} list The source or target list with items to store.
     * @param {JQuery} input The hidden form field where the items are stored.
     */
    generateItems: function(list, input) {
        var $this = this;
        list.children('.ui-picklist-item').each(function() {
            var item = $(this),
            itemValue = item.attr('data-item-value'),
            itemLabel = item.attr('data-item-label') ? PrimeFaces.escapeHTML(item.attr('data-item-label')) : '',
            option = $('<option selected="selected"></option>');

            if ($this.cfg.escapeValue) {
               itemValue = PrimeFaces.escapeHTML(itemValue);
            }
            option.prop('value', itemValue).text(itemLabel);
            input.append(option);
        });
    },

    /**
     * Sets tup the event listeners for when the command buttons (move up, move down etc.) are pressed.
     * @private
     */
    bindButtonEvents: function() {
        var _self = this;

        //visuals
        PrimeFaces.skinButton(this.jq.find('.ui-button'));

        //events
        $(this.jqId + ' .ui-picklist-button-add').on("click", function() {_self.add();});
        $(this.jqId + ' .ui-picklist-button-add-all').on("click", function() {_self.addAll();});
        $(this.jqId + ' .ui-picklist-button-remove').on("click", function() {_self.remove();});
        $(this.jqId + ' .ui-picklist-button-remove-all').on("click", function() {_self.removeAll();});

        if(this.cfg.showSourceControls) {
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-up').on("click", function() {_self.moveUp(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-top').on("click", function() {_self.moveTop(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-down').on("click", function() {_self.moveDown(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-bottom').on("click", function() {_self.moveBottom(_self.sourceList);});
        }

        if(this.cfg.showTargetControls) {
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-up').on("click", function() {_self.moveUp(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-top').on("click", function() {_self.moveTop(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-down').on("click", function() {_self.moveDown(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-bottom').on("click", function() {_self.moveBottom(_self.targetList);});
        }
    },

    /**
     * Sets up all event listeners for filtering the source and target lists.
     * @private
     */
    bindFilterEvents: function() {
        this.cfg.filterEvent = this.cfg.filterEvent||'keyup';
        this.cfg.filterDelay = this.cfg.filterDelay||300;
        this.setupFilterMatcher();

        this.sourceFilter = $(this.jqId + '_source_filter');
        this.targetFilter = $(this.jqId + '_target_filter');

        PrimeFaces.skinInput(this.sourceFilter);
        this.bindTextFilter(this.sourceFilter);

        PrimeFaces.skinInput(this.targetFilter);
        this.bindTextFilter(this.targetFilter);
    },

    /**
     * Sets up the event listeners for when text is entered into the filter input of the source or target list.
     * @private
     * @param {JQuery} filter The filter input of the source or target list.
     */
    bindTextFilter: function(filter) {
        if(this.cfg.filterEvent === 'enter')
            this.bindEnterKeyFilter(filter);
        else
            this.bindFilterEvent(filter);
    },

    /**
     * Sets up the event listeners for when the enter key is pressed while inside a filter input of the source or target
     * list.
     * @private
     * @param {JQuery} filter The filter input of the source or target list.
     */
    bindEnterKeyFilter: function(filter) {
        var $this = this;

        filter.on('keydown', PrimeFaces.utils.blockEnterKey)
        .on('keyup', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                $this.filter(this.value, $this.getFilteredList($(this)));

                e.preventDefault();
            }
        });
    },

    /**
     * Sets up the event listeners for filtering the source and target lists.
     * @private
     * @param {JQuery} filter The filter input of the source or target list.
     */
    bindFilterEvent: function(filter) {
        var $this = this;

        //prevent form submit on enter key
        filter.on(this.cfg.filterEvent, function(e) {
            if (PrimeFaces.utils.ignoreFilterKey(e)) {
                return;
            }

            var input = $(this);

            if($this.filterTimeout) {
                clearTimeout($this.filterTimeout);
            }

            $this.filterTimeout = setTimeout(function() {
                $this.filter(input.val(), $this.getFilteredList(input));
                $this.filterTimeout = null;
            },
            $this.cfg.filterDelay);
        })
        .on('keydown', PrimeFaces.utils.blockEnterKey);
    },

    /**
     * Finds and stores the filter function which is to be used for filtering the options of this pick list.
     * @private
     */
    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    /**
     * Filters the available options in the source or target list.
     * @param {string} value A value against which the available options are matched.
     * @param {JQuery} list The source or target list that is to be filtered.
     * @param {boolean} [animate] If it should be animated.
     */
    filter: function(value, list, animate) {
        var filterValue = PrimeFaces.trim(value).toLowerCase(),
        items = list.children('li.ui-picklist-item'),
        animated = animate || this.isAnimated();

        list.removeAttr('role');

        if(filterValue === '') {
            items.filter(':hidden').show();
            list.attr('role', 'menu');
        }
        else {
            for(var i = 0; i < items.length; i++) {
                var item = items.eq(i),
                itemLabel = item.attr('data-item-label'),
                matches = this.filterMatcher(itemLabel, filterValue);

                if(matches) {
                    var hasRole = list[0].hasAttribute('role');
                    if(animated) {
                        item.fadeIn('fast', function() {
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
                else {
                    if(animated) {
                        item.fadeOut('fast');
                    }
                    else {
                        item.hide();
                    }
                }
            }
        }

    },

    /**
     * Implementation of a `PrimeFaces.widget.PickList.FilterFunction` that matches the given option when it starts with
     * the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the options starts with the filter value, or `false` otherwise.
     */
    startsWithFilter: function(value, filter) {
        return value.toLowerCase().indexOf(filter) === 0;
    },

    /**
     * Implementation of a `PrimeFaces.widget.PickList.FilterFunction` that matches the given option when it contains
     * the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the contains the filter value, or `false` otherwise.
     */
    containsFilter: function(value, filter) {
        return value.toLowerCase().indexOf(filter) !== -1;
    },

    /**
     * Implementation of a `PrimeFaces.widget.PickList.FilterFunction` that matches the given option when it ends with
     * the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the options ends with the filter value, or `false` otherwise.
     */
    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    /**
     * Finds the list belonging to the given filter input.
     * @private
     * @param {JQuery} filter The filter input of either the target or source list.
     * @return {JQuery} The list to which the given filter input applies.
     */
    getFilteredList: function(filter) {
        return filter.hasClass('ui-source-filter-input') ? this.sourceList : this.targetList;
    },

    /**
     * Adds all selected items in the source list by transferring them to the target list.
     */
    add: function() {
        var items = this.sourceList.children('li.ui-picklist-item.ui-state-highlight')

        this.transfer(items, this.sourceList, this.targetList, 'command');
    },

    /**
     * Adds all items to the target list by transferring all items from the source list to the target list.
     */
    addAll: function() {
        var items = this.sourceList.children('li.ui-picklist-item:visible:not(.ui-state-disabled)');

        this.transfer(items, this.sourceList, this.targetList, 'command');
    },

    /**
     * Removes all selected items in the target list by transferring them to the source list.
     */
    remove: function() {
        var items = this.targetList.children('li.ui-picklist-item.ui-state-highlight');

        this.transfer(items, this.targetList, this.sourceList, 'command');
    },

    /**
     * Removes all items in the target list by transferring all items from the target list to the source list.
     */
    removeAll: function() {
        var items = this.targetList.children('li.ui-picklist-item:visible:not(.ui-state-disabled)');

        this.transfer(items, this.targetList, this.sourceList, 'command');
    },

    /**
     * Moves the items that are currently selected up by one.
     * @param {JQuery} list The source or target list with items to move up.
     */
    moveUp: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        if(itemsCount) {
            items.each(function() {
                var item = $(this);

                if(!item.is(':first-child')) {

                    if(animated) {
                        item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            item.insertBefore(item.prev()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    _self.saveState();
                                    _self.fireReorderEvent();
                                }
                            });
                        });
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

    },

    /**
     * Moves the items that are currently selected to the top of the source of target list.
     * @param {JQuery} list The source or target list with items to move to the top.
     */
    moveTop: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        if(itemsCount) {
            items.each(function() {
                var item = $(this);

                if(!item.is(':first-child')) {

                    if(animated) {
                        item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            item.prependTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function(){
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    _self.saveState();
                                    _self.fireReorderEvent();
                                }
                            });
                        });
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
    },

    /**
     * Moves the items that are currently selected down by one.
     * @param {JQuery} list The source or target list with items to move down.
     */
    moveDown: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        if(itemsCount) {
            $(items.get().reverse()).each(function() {
                var item = $(this);

                if(!item.is(':last-child')) {
                    if(animated) {
                        item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            item.insertAfter(item.next()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    _self.saveState();
                                    _self.fireReorderEvent();
                                }
                            });
                        });
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
    },

    /**
     * Moves the items that are currently selected to the bottom of the source of target list.
     * @param {JQuery} list The source or target list with items to move to the bottom.
     */
    moveBottom: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        if(itemsCount) {
            items.each(function() {
                var item = $(this);

                if(!item.is(':last-child')) {

                    if(animated) {
                        item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            item.appendTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                                movedCount++;

                                if(movedCount === itemsCount) {
                                    _self.saveState();
                                    _self.fireReorderEvent();
                                }
                            });
                        });
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
    },

    /**
     * Saves the current state of this widget, i.e. to which list the items are currently assigned. Clears inputs and
     * repopulates them from the list states.
     * @private
     */
    saveState: function() {
        this.sourceInput.children().remove();
        this.targetInput.children().remove();

        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);
        this.cursorItem = null;
    },

    /**
     * Transfers the given items from the source or target list to the other list.
     * @param {JQuery} items Items that were transferred from one list to the other.
     * @param {JQuery} from List from which the items were transferred.
     * @param {JQuery} to List to which the items were transferred.
     * @param {PrimeFaces.widget.PickList.TransferType} type Type of the action that caused the items to be transferred.
     */
    transfer: function(items, from, to, type) {
        $(this.jqId + ' ul').sortable('disable');
        var $this = this;
        var itemsCount = items.length;
        var transferCount = 0;

        if(this.isAnimated()) {
            items.hide(this.cfg.effect, {}, this.cfg.effectSpeed, function() {
                var item = $(this);
                $this.unselectItem(item);

                item.appendTo(to).show($this.cfg.effect, {}, $this.cfg.effectSpeed, function() {
                    transferCount++;

                    //fire transfer when all items are transferred
                    if(transferCount == itemsCount) {
                        $this.saveState();
                        $this.fireTransferEvent(items, from, to, type);
                    }
                });

                $this.updateListRole();
            });
        }
        else {
            items.hide();

            if(this.cfg.showCheckbox) {
                items.each(function() {
                    $this.unselectItem($(this));
                });
            }

            items.appendTo(to).show();

            this.saveState();
            this.fireTransferEvent(items, from, to, type);
            this.updateListRole();
        }
    },

    /**
     * Triggers the behavior for when pick list items are transferred from the source to the target list or vice-versa.
     * @private
     * @param {JQuery} items Items that were transferred from one list to the other.
     * @param {JQuery} from List from which the items were transferred.
     * @param {JQuery} to List to which the items were transferred.
     * @param {PrimeFaces.widget.PickList.TransferType} type Type of the action that caused the items to be transferred.
     */
    fireTransferEvent: function(items, from, to, type) {
        var $this = this;

        if(this.cfg.onTransfer) {
            var obj = {};
            obj.items = items;
            obj.from = from;
            obj.to = to;
            obj.type = type;

            this.cfg.onTransfer.call(this, obj);
        }

        if(this.hasBehavior('transfer')) {
            var isAdd = from.hasClass('ui-picklist-source');

            var options = {
                params: [
                    {name: $this.id + '_add', value: isAdd}
                ],
                oncomplete: function() {
                    $this.refilterSource();
                    $this.refilterTarget();
                    $($this.jqId + ' ul').sortable('enable');
                    $this.updateButtonsState();
                }
            };

            items.each(function(index, item) {
                options.params.push({name: $this.id + '_transferred', value: $(item).attr('data-item-value')});
            });

            this.callBehavior('transfer', options);
        }
        else {
            $($this.jqId + ' ul').sortable('enable');
            $this.updateButtonsState();
        }
    },

    /**
     * Finds the type of the given list, i.e. whether the list represents the source or target list.
     * @private
     * @param {JQuery} element A list element to check.
     * @return {PrimeFaces.widget.PickList.ListName} Whether the element represents the source or target list.
     */
    getListName: function(element){
        return element.parent().hasClass("ui-picklist-source") ? "source" : "target";
    },

    /**
     * Triggers the behavior for when pick list items are selected.
     * @private
     * @param {JQuery} item A pick list item that was selected.
     */
    fireItemSelectEvent: function(item) {
        if(this.hasBehavior('select')) {
            var listName = this.getListName(item),
            inputContainer = (listName === "source") ? this.sourceInput : this.targetInput,
            ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_listName', value: listName}
                ],
                onstart: function() {
                    if(!inputContainer.children().length) {
                        return false;
                    }
                }
            };

            this.callBehavior('select', ext);
        }
    },

    /**
     * Triggers the behavior for when pick list items are unselected.
     * @private
     * @param {JQuery} item A pick list item that was unselected.
     */
    fireItemUnselectEvent: function(item) {
        if(this.hasBehavior('unselect')) {
            var ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_listName', value: this.getListName(item)}
                ]
            };

            this.callBehavior('unselect', ext);
        }
    },

    /**
     * Triggers the behavior for when pick list items are reordered.
     * @private
     */
    fireReorderEvent: function() {
        this.callBehavior('reorder');
    },

    /**
     * Checks whether UI actions of this pick list are animated.
     * @return {boolean} `true` if this pick list is animated, or `false` otherwise.
     */
    isAnimated: function() {
        return (this.cfg.effect && this.cfg.effect != 'none');
    },

    /**
     * Applies the tab index to this pick list widget.
     * @private
     */
    setTabIndex: function() {
        var tabindex = (this.cfg.disabled) ? '-1' : this.getTabIndex();
        this.sourceList.attr('tabindex', tabindex);
        this.targetList.attr('tabindex', tabindex);
        $(this.jqId + ' button').attr('tabindex', tabindex);
        $(this.jqId + ' .ui-picklist-filter-container > input').attr('tabindex', tabindex);
    },

    /**
     * Finds the tab index of this pick list widget.
     * @private
     * @return {string} The tab index of this pick list.
     */
    getTabIndex: function() {
        return this.cfg.tabindex||'0';
    },

    /**
     * Updates the state of all buttons of this pick list, such as whether they are disabled or enabled.
     * @private
     */
    updateButtonsState: function () {
        var addButton = $(this.jqId + ' .ui-picklist-button-add');
        var sourceListButtons = $(this.jqId + ' .ui-picklist-source-controls .ui-button');
        if (this.sourceList.find('li.ui-state-highlight').length) {
            this.enableButton(addButton);
            this.enableButton(sourceListButtons);
        }
        else {
            this.disableButton(addButton);
            this.disableButton(sourceListButtons);
        }

        var removeButton = $(this.jqId + ' .ui-picklist-button-remove');
        var targetListButtons = $(this.jqId + ' .ui-picklist-target-controls .ui-button');
        if (this.targetList.find('li.ui-state-highlight').length) {
            this.enableButton(removeButton);
            this.enableButton(targetListButtons);
        }
        else {
            this.disableButton(removeButton);
            this.disableButton(targetListButtons);
        }

        var addAllButton = $(this.jqId + ' .ui-picklist-button-add-all');
        if (this.sourceList.find('li.ui-picklist-item:not(.ui-state-disabled)').length) {
            this.enableButton(addAllButton);
            this.sourceList.attr('tabindex', this.getTabIndex());
        }
        else {
            this.disableButton(addAllButton);
            this.sourceList.attr('tabindex', '-1');
        }

        var removeAllButton = $(this.jqId + ' .ui-picklist-button-remove-all');
        if (this.targetList.find('li.ui-picklist-item:not(.ui-state-disabled)').length) {
            this.enableButton(removeAllButton);
            this.targetList.attr('tabindex', this.getTabIndex());
        }
        else {
            this.disableButton(removeAllButton);
            this.targetList.attr('tabindex', '-1');
        }
    },

    /**
     * Reapply filtering the current source list.
     * @private
     */
    refilterSource: function() {
        this.filter(this.sourceFilter.val(), this.sourceList, false);
    },

    /**
     * Reapply filtering to the current target list.
     * @private
     */
    refilterTarget: function() {
        this.filter(this.targetFilter.val(), this.targetList, false);
    },

    /**
     * Disables the given button belonging to this pick list.
     * @private
     * @param {JQuery} button A button to disable.
     */
    disableButton: function (button) {
        if (button.hasClass('ui-state-focus')) {
            button.trigger("blur");
        }

        button.attr('disabled', 'disabled').addClass('ui-state-disabled');
        button.attr('tabindex', '-1');
    },

    /**
     * Enables the given button belonging to this pick list.
     * @private
     * @param {JQuery} button A button to enable.
     */
    enableButton: function (button) {
        button.removeAttr('disabled').removeClass('ui-state-disabled');
        button.attr('tabindex', this.getTabIndex());
    },

    /**
     * Updates the `role` attribute of the source and target pick list items.
     * @private
     */
    updateListRole: function() {
        this.sourceList.children('li:visible').length > 0 ? this.sourceList.attr('role', 'menu') : this.sourceList.removeAttr('role');
        this.targetList.children('li:visible').length > 0 ? this.targetList.attr('role', 'menu') : this.targetList.removeAttr('role');
    }

});