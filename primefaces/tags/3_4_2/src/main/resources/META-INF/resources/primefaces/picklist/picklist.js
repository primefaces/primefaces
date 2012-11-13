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
                
        //generate input options
        this.generateItems(this.sourceList, this.sourceInput);
        this.generateItems(this.targetList, this.targetInput);

        if(this.cfg.disabled) {
            $(this.jqId + ' li.ui-picklist-item').addClass('ui-state-disabled');
            $(this.jqId + ' button').attr('disabled', 'disabled').addClass('ui-state-disabled');
        }
        else {
            var _self = this;

            //Sortable lists
            $(this.jqId + ' ul').sortable({
                cancel: '.ui-state-disabled',
                connectWith: this.jqId + ' .ui-picklist-list',
                revert: true,
                containment: this.jq,
                update: function(event, ui) {
                    ui.item.removeClass('ui-state-highlight');

                    _self.saveState();
                },
                receive: function(event, ui) {
                    _self.fireTransferEvent(ui.item, ui.sender, ui.item.parents('ul.ui-picklist-list:first'), 'dragdrop');
                }
            });
            
            this.bindItemEvents();

            this.bindButtonEvents();
            
            this.bindFilterEvents();
        }
    },
    
    bindItemEvents: function() {
        var _self = this;
        
        this.items.mouseover(function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight')) {
                $(this).addClass('ui-state-hover');
            }
        })
        .mouseout(function(e) {
            $(this).removeClass('ui-state-hover');
        })
        .mousedown(function(e) {
            var element = $(this),
            metaKey = (e.metaKey||e.ctrlKey);

            if(metaKey) {
                if(element.hasClass('ui-state-highlight'))
                    element.removeClass('ui-state-highlight');
                else
                    element.removeClass('ui-state-hover').addClass('ui-state-highlight');
            }
            else {
                element.removeClass('ui-state-hover').addClass('ui-state-highlight')
                    .siblings('.ui-state-highlight').removeClass('ui-state-highlight');
            }
        })
        .dblclick(function() {
            var item = $(this);

            if($(this).parent().hasClass('ui-picklist-source'))
                _self.transfer(item, _self.sourceList, _self.targetList, 'dblclick');
            else
                _self.transfer(item, _self.targetList, _self.sourceList, 'dblclick');

            PrimeFaces.clearSelection();
        });
    },
    
    generateItems: function(list, input) {   
        list.children('.ui-picklist-item').each(function() {
            var item = $(this),
            itemValue = PrimeFaces.escapeHTML(item.attr('data-item-value')),
            itemLabel = PrimeFaces.escapeHTML(item.attr('data-item-label'));
            
            input.append('<option value="' + itemValue + '" selected="selected">' + itemLabel + '</option>');
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
            $(this.jqId + ' td.ui-picklist-source-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.sourceList);});
            $(this.jqId + ' td.ui-picklist-source-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.sourceList);});
            $(this.jqId + ' td.ui-picklist-source-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.sourceList);});
            $(this.jqId + ' td.ui-picklist-source-controls .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.sourceList);});
        }

        if(this.cfg.showTargetControls) {
            $(this.jqId + ' td.ui-picklist-target-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.targetList);});
            $(this.jqId + ' td.ui-picklist-target-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.targetList);});
            $(this.jqId + ' td.ui-picklist-target-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.targetList);});
            $(this.jqId + ' td.ui-picklist-target-controls .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.targetList);});
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
        items = list.children('li.ui-picklist-item');
        
        if(filterValue === '') {
            items.filter(':hidden').show();
        }
        else {
            for(var i = 0; i < items.length; i++) {
                var item = items.eq(i),
                itemLabel = item.attr('data-item-label');

                if(this.filterMatcher(itemLabel, filterValue)) {
                    item.fadeIn('fast');
                }
                else {
                    item.fadeOut('fast');
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
    },
    
    transfer: function(items, from, to, type) {  
        var _self = this,
        itemsCount = items.length,
        transferCount = 0;
        
        if(this.isAnimated()) {
            items.removeClass('ui-state-highlight').hide(this.cfg.effect, {}, this.cfg.effectSpeed, function() {
                var item = $(this);

                item.appendTo(to).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                    transferCount++;

                    //fire transfer when all items are transferred
                    if(transferCount == itemsCount) {
                        _self.saveState();
                        _self.fireTransferEvent(items, from, to, type);
                    }
                });
            });
        }
        else {
            items.removeClass('ui-state-highlight').hide().appendTo(to).show();
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

                transferBehavior.call(this, null, ext);
            }
        }
    },
    
    isAnimated: function() {
        return (this.cfg.effect && this.cfg.effect != 'none');
    }

});