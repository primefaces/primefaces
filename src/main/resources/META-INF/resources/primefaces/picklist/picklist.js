/**
 * PrimeFaces PickList Widget
 */
PrimeFaces.widget.PickList = PrimeFaces.widget.BaseWidget.extend({
    
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
                
        //generate input options
        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);

        if(this.cfg.disabled) {
            $(this.jqId + ' li.ui-picklist-item').addClass('ui-state-disabled');
            $(this.jqId + ' button').attr('disabled', 'disabled').addClass('ui-state-disabled');
        }
        else {
            var $this = this;

            //Sortable lists
            $(this.jqId + ' ul').sortable({
                cancel: '.ui-state-disabled,.ui-chkbox-box',
                connectWith: this.jqId + ' .ui-picklist-list',
                revert: 1,
                update: function(event, ui) {
                    $this.unselectItem(ui.item);

                    $this.saveState();
                },
                receive: function(event, ui) {
                    $this.fireTransferEvent(ui.item, ui.sender, ui.item.parents('ul.ui-picklist-list:first'), 'dragdrop');
                },
                
                start: function(event, ui) {
                    $this.dragging = true;
                },
                
                stop: function(event, ui) {
                    $this.dragging = false;
                }
            });
            
            this.bindItemEvents();

            this.bindButtonEvents();
            
            this.bindFilterEvents();
        }
    },
    
    bindItemEvents: function() {
        var $this = this;
        
        this.items.on('mouseover.pickList', function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight')) {
                $(this).addClass('ui-state-hover');
            }
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
            metaKey = (e.metaKey||e.ctrlKey);
            
            if(!e.shiftKey) {
                if(!metaKey) {
                    $this.unselectAll();
                }

                if(metaKey && item.hasClass('ui-state-highlight')) {
                    $this.unselectItem(item);
                } 
                else {
                    $this.selectItem(item);
                    $this.cursorItem = item;
                }
            }
            else {
                $this.unselectAll();
                
                if($this.cursorItem && ($this.cursorItem.parent().is(item.parent()))) {
                    var currentItemIndex = item.index(),
                    cursorItemIndex = $this.cursorItem.index(),
                    startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex,
                    endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1),
                    parentList = item.parent();
                    
                    for(var i = startIndex ; i < endIndex; i++) {
                        var it = parentList.children('li.ui-picklist-item').eq(i);
                        
                        if(it.is(':visible')) {
                            $this.selectItem(it);
                        }
                    }
                } 
                else {
                    $this.selectItem(item);
                    $this.cursorItem = item;
                }
            }
        })
        .on('dblclick.pickList', function() {
            var item = $(this);

            if($(this).parent().hasClass('ui-picklist-source'))
                $this.transfer(item, $this.sourceList, $this.targetList, 'dblclick');
            else
                $this.transfer(item, $this.targetList, $this.sourceList, 'dblclick');

            PrimeFaces.clearSelection();
        });
        
        if(this.cfg.showCheckbox) {
            this.checkboxes.on('mouseover.pickList', function(e) {
                var chkbox = $(this);
                
                if(!chkbox.hasClass('ui-state-active'))
                    chkbox.addClass('ui-state-hover');
            })
            .on('mouseout.pickList', function(e) {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.pickList', function(e) {
                $this.checkboxClick = true;
                
                var item = $(this).closest('li.ui-picklist-item');
                if(item.hasClass('ui-state-highlight'))
                    $this.unselectItem(item);
                else
                    $this.selectItem(item);
            });
        }
    },
    
    selectItem: function(item) {
        item.removeClass('ui-state-hover').addClass('ui-state-highlight');
        
        if(this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },
    
    unselectItem: function(item) {
        item.removeClass('ui-state-hover ui-state-highlight');
        
        if(PrimeFaces.isIE(8)) {
            item.css('filter','');
        }
        
        if(this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },
    
    unselectAll: function() {
        var selectedItems = this.items.filter('.ui-state-highlight');
        for(var i = 0; i < selectedItems.length; i++) {
            this.unselectItem(selectedItems.eq(i));
        }
    },
   
    selectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-hover').addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
    },
    
    unselectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
    },
        
    generateItems: function(list, input) {   
        list.children('.ui-picklist-item').each(function() {
            var item = $(this),
            itemValue = PrimeFaces.escapeHTML(item.attr('data-item-value')),
            itemLabel = item.attr('data-item-label'),
            escapedItemLabel = (itemLabel) ? PrimeFaces.escapeHTML(itemLabel) : '';
            
            input.append('<option value="' + itemValue + '" selected="selected">' + escapedItemLabel + '</option>');
        });
    },
    
    bindButtonEvents: function() {
        var _self = this;

        //visuals
        PrimeFaces.skinButton(this.jq.find('.ui-button'));

        //events
        $(this.jqId + ' .ui-picklist-button-add').click(function() {_self.add();});
        $(this.jqId + ' .ui-picklist-button-add-all').click(function() {_self.addAll();});
        $(this.jqId + ' .ui-picklist-button-remove').click(function() {_self.remove();});
        $(this.jqId + ' .ui-picklist-button-remove-all').click(function() {_self.removeAll();});

        if(this.cfg.showSourceControls) {
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.sourceList);});
            $(this.jqId + ' .ui-picklist-source-controls .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.sourceList);});
        }

        if(this.cfg.showTargetControls) {
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.targetList);});
            $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.targetList);});
        }
    },
        
    bindFilterEvents: function() {
        this.setupFilterMatcher();
        
        this.sourceFilter = $(this.jqId + '_source_filter');
        this.targetFilter = $(this.jqId + '_target_filter');
        var _self = this;
        
        PrimeFaces.skinInput(this.sourceFilter);
        PrimeFaces.skinInput(this.targetFilter);
        
        this.sourceFilter.on('keyup', function(e) {
            _self.filter(this.value, _self.sourceList);
        });
        
        this.targetFilter.on('keyup', function(e) {
            _self.filter(this.value, _self.targetList);
        });
    },
    
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
    
    filter: function(value, list) {
        var filterValue = $.trim(value).toLowerCase(),
        items = list.children('li.ui-picklist-item'),
        animated = this.isAnimated();
        
        if(filterValue === '') {
            items.filter(':hidden').show();
        }
        else {
            for(var i = 0; i < items.length; i++) {
                var item = items.eq(i),
                itemLabel = item.attr('data-item-label'),
                matches = this.filterMatcher(itemLabel, filterValue);

                if(matches) {
                    if(animated)
                        item.fadeIn('fast');
                    else
                        item.show();
                }
                else {
                    if(animated)
                        item.fadeOut('fast');
                    else
                        item.hide();
                }
            }
        }
    },
    
    startsWithFilter: function(value, filter) {
        return value.toLowerCase().indexOf(filter) === 0;
    },
    
    containsFilter: function(value, filter) {
        return value.toLowerCase().indexOf(filter) !== -1;
    },
    
    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },
    
    add: function() {
        var items = this.sourceList.children('li.ui-picklist-item.ui-state-highlight')
        
        this.transfer(items, this.sourceList, this.targetList, 'command');
    },
    
    addAll: function() {
        var items = this.sourceList.children('li.ui-picklist-item:visible:not(.ui-state-disabled)');
        
        this.transfer(items, this.sourceList, this.targetList, 'command');
    },
    
    remove: function() {
        var items = this.targetList.children('li.ui-picklist-item.ui-state-highlight');
        
        this.transfer(items, this.targetList, this.sourceList, 'command');
    },
    
    removeAll: function() {
        var items = this.targetList.children('li.ui-picklist-item:visible:not(.ui-state-disabled)');
        
        this.transfer(items, this.targetList, this.sourceList, 'command');
    },
    
    moveUp: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;
            
        items.each(function() {
            var item = $(this);

            if(!item.is(':first-child')) {
                
                if(animated) {
                    item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                        item.insertBefore(item.prev()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            movedCount++;

                            if(movedCount === itemsCount) {
                                _self.saveState();
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
        }
        
    },
    
    moveTop: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        list.children('.ui-state-highlight').each(function() {
            var item = $(this);

            if(!item.is(':first-child')) {
                
                if(animated) {
                    item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                        item.prependTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function(){
                            movedCount++;
                            
                            if(movedCount === itemsCount) {
                                _self.saveState();
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
        }
    },
    
    moveDown: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        $(list.children('.ui-state-highlight').get().reverse()).each(function() {
            var item = $(this);

            if(!item.is(':last-child')) {
                if(animated) {
                    item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                        item.insertAfter(item.next()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            movedCount++;
                            
                            if(movedCount === itemsCount) {
                                _self.saveState();
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
        }
    },
    
    moveBottom: function(list) {
        var _self = this,
        animated = _self.isAnimated(),
        items = list.children('.ui-state-highlight'),
        itemsCount = items.length,
        movedCount = 0;

        list.children('.ui-state-highlight').each(function() {
            var item = $(this);

            if(!item.is(':last-child')) {
                
                if(animated) {
                    item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                        item.appendTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                            movedCount++;
                            
                            if(movedCount === itemsCount) {
                                _self.saveState();
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
        }
    },
    
    /**
     * Clear inputs and repopulate them from the list states 
     */ 
    saveState: function() {
        this.sourceInput.children().remove();
        this.targetInput.children().remove();

        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);
        this.cursorItem = null;
    },
    
    transfer: function(items, from, to, type) {  
        var $this = this,
        itemsCount = items.length,
        transferCount = 0;
        
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
        }
    },
    
    /**
     * Fire transfer ajax behavior event
     */
    fireTransferEvent: function(items, from, to, type) {
        if(this.cfg.onTransfer) {
            var obj = {};
            obj.items = items;
            obj.from = from;
            obj.to = to;
            obj.type = type;

            this.cfg.onTransfer.call(this, obj);
        }
        
        if(this.cfg.behaviors) {
            var transferBehavior = this.cfg.behaviors['transfer'];

            if(transferBehavior) {
                var ext = {
                    params: []
                },
                paramName = this.id + '_transferred',
                isAdd = from.hasClass('ui-picklist-source');
                
                items.each(function(index, item) {
                    ext.params.push({name:paramName, value:$(item).attr('data-item-value')});
                });
                
                ext.params.push({name:this.id + '_add', value:isAdd});

                transferBehavior.call(this, ext);
            }
        }
    },
    
    isAnimated: function() {
        return (this.cfg.effect && this.cfg.effect != 'none');
    }

});