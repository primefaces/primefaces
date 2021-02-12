/**
 * __PrimeFaces OrderList Widget__
 * 
 * OrderList is used to sort a collection featuring drag&drop based reordering, transition effects and POJO support.
 * 
 * @prop {JQuery} input The DOM element for the hidden form field storing the current order of the items.
 * @prop {JQuery} items The DOM elements for the available items that can be reordered.
 * @prop {JQuery} list The DOM element for the container with the items.
 * 
 * @interface {PrimeFaces.widget.OrderListCfg} cfg The configuration for the {@link  OrderList| OrderList widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.disabled Whether this widget is disabled initially.
 * @prop {string} cfg.effect Name of animation to display.
 */
PrimeFaces.widget.OrderList = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.list = this.jq.find('.ui-orderlist-list'),
        this.items = this.list.children('.ui-orderlist-item');
        this.input = $(this.jqId + '_values');
        this.cfg.effect = this.cfg.effect||'fade';
        this.cfg.disabled = this.jq.hasClass('ui-state-disabled');
        var $this = this;

        if(!this.cfg.disabled) {
            this.generateItems();

            this.setupButtons();

            //Enable dnd
            this.list.sortable({
                revert: 1,
                placeholder: "ui-orderlist-item ui-state-highlight",
                forcePlaceholderSize: true,
                start: function(event, ui) {
                    PrimeFaces.clearSelection();
                }
                ,update: function(event, ui) {
                    $this.onDragDrop(event, ui);
                }
            });
            
            this.bindEvents();
        }
    },

    /**
     * Reads the current item order and stores it in a hidden form field.
     * @private
     */
    generateItems: function() {
        var $this = this;

        this.list.children('.ui-orderlist-item').each(function() {
            var item = $(this),
            itemValue = item.data('item-value'),
            option = $('<option selected="selected"></option>');

            option.prop('value', itemValue).text(itemValue);
            $this.input.append(option);
        });
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;
        
        if (PrimeFaces.env.browser.mobile) {
            var disabledSortable = function() {
                $this.list.sortable('disable');
                $this.items.css('touch-action', 'auto');
            };
            
            disabledSortable();
            
            this.items.on('touchend.orderList-mobile', function() {
                disabledSortable();
            })
            .on('click.orderList-mobile', function() {
                $this.list.sortable('enable');
            });
        }

        this.items.on('mouseover.orderList', function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).addClass('ui-state-hover');
        })
        .on('mouseout.orderList', function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).removeClass('ui-state-hover');
        })
        .on('mousedown.orderList', function(e) {
            var element = $(this),
            metaKey = (e.metaKey||e.ctrlKey);

            if(!metaKey) {
                element.removeClass('ui-state-hover').addClass('ui-state-highlight')
                .siblings('.ui-state-highlight').removeClass('ui-state-highlight');

                $this.fireItemSelectEvent(element, e);
            }
            else {
                if(element.hasClass('ui-state-highlight')) {
                    element.removeClass('ui-state-highlight');
                    $this.fireItemUnselectEvent(element);
                }
                else {
                    element.removeClass('ui-state-hover').addClass('ui-state-highlight');
                    $this.fireItemSelectEvent(element, e);
                }
            }
        });
    },

    /**
     * Sets up the buttons and corresponding event listeners for moving order list items up and down.
     * @private
     */
    setupButtons: function() {
        var $this = this;

        PrimeFaces.skinButton(this.jq.find('.ui-button'));

        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-up').on("click", function() {$this.moveUp($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-top').on("click", function() {$this.moveTop($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-down').on("click", function() {$this.moveDown($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-bottom').on("click", function() {$this.moveBottom($this.sourceList);});
    },

    /**
     * Callback that is invoked when an order list item was moved via drag and drop. Saves the new order of the items
     * and invokes the appropriate behaviors.
     * @private
     * @param {JQuery.TriggeredEvent} event The event that triggered the drag or drop.
     * @param {JQueryUI.SortableUIParams} ui The UI params as passed by JQuery UI to the event handler.
     */
    onDragDrop: function(event, ui) {
        ui.item.removeClass('ui-state-highlight');
        this.saveState();
        this.fireReorderEvent();
    },

    /**
     * Saves the current value of this order list, i.e. the order of the items.  The value is saved in a hidden form
     * field.
     * @private
     */
    saveState: function() {
        this.input.children().remove();

        this.generateItems();
    },

    /**
     * Moves the selected order list items up by one, as if the `move up` button were pressed.
     */
    moveUp: function() {
        var $this = this,
        selectedItems = $this.list.children('.ui-orderlist-item.ui-state-highlight'),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0,
        hasFirstChild = selectedItems.is(':first-child');

        if(hasFirstChild) {
            return;
        }

        selectedItems.each(function() {
            var item = $(this);

            if(!item.is(':first-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.insertBefore(item.prev()).show($this.cfg.effect, {}, 'fast', function() {
                        movedItemsCount++;

                        if(itemsToMoveCount === movedItemsCount) {
                            $this.saveState();
                            $this.fireReorderEvent();
                        }
                    });
                });
            }
            else {
                itemsToMoveCount--;
            }
        });
    },

    /**
     * Moves the selected order list items to the top, as if the `move to top` button were pressed.
     */
    moveTop: function() {
        var $this = this,
        selectedItems = $this.list.children('.ui-orderlist-item.ui-state-highlight'),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0,
        hasFirstChild = selectedItems.is(':first-child'),
        firstSelectedItemIndex = selectedItems.eq(0).index();

        if(hasFirstChild) {
            return;
        }

        selectedItems.each(function(index) {
            var item = $(this),
                currentIndex = (index === 0) ? 0 : (item.index() - firstSelectedItemIndex);

            if(!item.is(':first-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.insertBefore($this.list.children('.ui-orderlist-item').eq(currentIndex)).show($this.cfg.effect, {}, 'fast', function(){
                        movedItemsCount++;

                        if(itemsToMoveCount === movedItemsCount) {
                            $this.saveState();
                            $this.fireReorderEvent();
                        }
                    });
                });
            }
            else {
                itemsToMoveCount--;
            }
        });
    },

    /**
     * Moves the selected order list items down by one, as if the `move down` button were pressed.
     */
    moveDown: function() {
        var $this = this,
        selectedItems = $($this.list.children('.ui-orderlist-item.ui-state-highlight').get().reverse()),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0,
        hasFirstChild = selectedItems.is(':last-child');

        if(hasFirstChild) {
            return;
        }

        selectedItems.each(function() {
            var item = $(this);

            if(!item.is(':last-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.insertAfter(item.next()).show($this.cfg.effect, {}, 'fast', function() {
                        movedItemsCount++;

                        if(itemsToMoveCount === movedItemsCount) {
                            $this.saveState();
                            $this.fireReorderEvent();
                        }
                    });
                });
            }
            else {
                itemsToMoveCount--;
            }
        });
    },

    /**
     * Moves the selected order list items to the bottom, as if the `move to bottom` button were pressed.
     */
    moveBottom: function() {
        var $this = this,
        selectedItems = $($this.list.children('.ui-orderlist-item.ui-state-highlight').get().reverse()),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0,
        hasFirstChild = selectedItems.is(':last-child'),
        lastSelectedItemIndex = selectedItems.eq(0).index(),
        itemsLength = this.items.length;

        if(hasFirstChild) {
            return;
        }

        selectedItems.each(function(index) {
            var item = $(this),
                currentIndex = (index === 0) ? itemsLength - 1 : (item.index() - lastSelectedItemIndex) - 1;

            if(!item.is(':last-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.insertAfter($this.list.children('.ui-orderlist-item').eq(currentIndex)).show($this.cfg.effect, {}, 'fast', function() {
                        movedItemsCount++;

                        if(itemsToMoveCount === movedItemsCount) {
                            $this.saveState();
                            $this.fireReorderEvent();
                        }
                    });
                });
            }
            else {
                itemsToMoveCount--;
            }
        });
    },

    /**
     * Invokes the appropriate behavior for when an item of the order list was selected.
     * @private
     * @param {JQuery} item The item that was selected.
     * @param {JQuery.TriggeredEvent} e The event that occurred.
     */
    fireItemSelectEvent: function(item, e) {
        if(this.hasBehavior('select')) {
            var ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_metaKey', value: e.metaKey},
                    {name: this.id + '_ctrlKey', value: e.ctrlKey}
                ]
            };

            this.callBehavior('select', ext);
        }
    },

    /**
     * Invokes the appropriate behavior for when an item of the order list was unselected.
     * @private
     * @param {JQuery} item The item that was unselected.
     */
    fireItemUnselectEvent: function(item) {
        if(this.hasBehavior('unselect')) {
            var ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()}
                ]
            };

            this.callBehavior('unselect', ext);
        }
    },

    /**
     * Invokes the appropriate behavior for when the order list was reordered.
     * @private
     */
    fireReorderEvent: function() {
        if(this.hasBehavior('reorder')) {
            this.callBehavior('reorder');
        }
    }

});