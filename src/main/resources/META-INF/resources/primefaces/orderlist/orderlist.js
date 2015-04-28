/**
 * PrimeFaces OrderList Widget
 */
PrimeFaces.widget.OrderList = PrimeFaces.widget.BaseWidget.extend({
    
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

            this.bindEvents();

            //Enable dnd
            this.list.sortable({
                revert: 1,
                start: function(event, ui) {
                    PrimeFaces.clearSelection();
                }
                ,update: function(event, ui) {
                    $this.onDragDrop(event, ui);
                }
            });
        }
    },
    
    generateItems: function() {
        var $this = this;

        this.list.children('.ui-orderlist-item').each(function() {
            var item = $(this),
            itemValue = item.data('item-value');

            $this.input.append('<option value="' + itemValue + '" selected="selected">' + itemValue + '</option>');
        });
    },
    
    bindEvents: function() {
        var $this = this;
        
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
    
    setupButtons: function() {
        var $this = this;

        PrimeFaces.skinButton(this.jq.find('.ui-button'));

        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-up').click(function() {$this.moveUp($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-top').click(function() {$this.moveTop($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-down').click(function() {$this.moveDown($this.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-bottom').click(function() {$this.moveBottom($this.sourceList);});
    },
    
    onDragDrop: function(event, ui) {
        ui.item.removeClass('ui-state-highlight');
        this.saveState();
        this.fireReorderEvent();
    },
    
    saveState: function() {
        this.input.children().remove();

        this.generateItems();
    },
    
    moveUp: function() {
        var $this = this,
        selectedItems = this.items.filter('.ui-state-highlight'),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

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
    
    moveTop: function() {
        var $this = this,
        selectedItems = this.items.filter('.ui-state-highlight'),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        selectedItems.each(function() {
            var item = $(this);

            if(!item.is(':first-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.prependTo(item.parent()).show($this.cfg.effect, {}, 'fast', function(){
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
    
    moveDown: function() {
        var $this = this,
        selectedItems = $(this.items.filter('.ui-state-highlight').get().reverse()),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

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
    
    moveBottom: function() {
        var $this = this,
        selectedItems = this.items.filter('.ui-state-highlight'),
        itemsToMoveCount = selectedItems.length,
        movedItemsCount = 0;

        selectedItems.each(function() {
            var item = $(this);

            if(!item.is(':last-child')) {
                item.hide($this.cfg.effect, {}, 'fast', function() {
                    item.appendTo(item.parent()).show($this.cfg.effect, {}, 'fast', function() {
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
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }
    
        return false;
    },
    
    fireItemSelectEvent: function(item, e) {
        if(this.hasBehavior('select')) {
            var itemSelectBehavior = this.cfg.behaviors['select'],
            ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()},
                    {name: this.id + '_metaKey', value: e.metaKey},
                    {name: this.id + '_ctrlKey', value: e.ctrlKey}
                ]
            };

            itemSelectBehavior.call(this, ext);
        }
    },
    
    fireItemUnselectEvent: function(item) {
        if(this.hasBehavior('unselect')) {
            var itemUnselectBehavior = this.cfg.behaviors['unselect'],
            ext = {
                params: [
                    {name: this.id + '_itemIndex', value: item.index()}
                ]
            };

            itemUnselectBehavior.call(this, ext);
        }
    },
    
    fireReorderEvent: function() {
        if(this.hasBehavior('reorder')) {
            this.cfg.behaviors['reorder'].call(this);
        }
    }

});