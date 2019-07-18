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

    removeOutline: function() {
        if(this.focusedItem && this.focusedItem.hasClass('ui-picklist-outline')) {
            this.focusedItem.removeClass('ui-picklist-outline');
        }
    },

    selectItem: function(item, silent) {
        item.removeClass('ui-state-hover').addClass('ui-state-highlight');

        if(this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.ui-chkbox-box'));
        }

        if(silent) {
            this.fireItemSelectEvent(item);
        }

        this.updateButtonsState();
    },

    unselectItem: function(item, silent) {
        item.removeClass('ui-state-hover ui-state-highlight');

        if(this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.ui-chkbox-box'));
        }

        if(silent) {
            this.fireItemUnselectEvent(item);
        }

        this.updateButtonsState();
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

    bindTextFilter: function(filter) {
        if(this.cfg.filterEvent === 'enter')
            this.bindEnterKeyFilter(filter);
        else
            this.bindFilterEvent(filter);
    },

    bindEnterKeyFilter: function(filter) {
        var $this = this;

        filter.bind('keydown', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                e.preventDefault();
            }
        }).bind('keyup', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                $this.filter(this.value, $this.getFilteredList($(this)));

                e.preventDefault();
            }
        });
    },

    bindFilterEvent: function(filter) {
        var $this = this;

        //prevent form submit on enter key
        filter.on(this.cfg.filterEvent, function(e) {
            var input = $(this),
            key = e.which,
            keyCode = $.ui.keyCode,
            ignoredKeys = [keyCode.END, keyCode.HOME, keyCode.LEFT, keyCode.RIGHT, keyCode.UP, keyCode.DOWN,
                keyCode.TAB, 16/*Shift*/, 17/*Ctrl*/, 18/*Alt*/, 91, 92, 93/*left/right Win/Cmd*/,
                keyCode.ESCAPE, keyCode.PAGE_UP, keyCode.PAGE_DOWN,
                19/*pause/break*/, 20/*caps lock*/, 44/*print screen*/, 144/*num lock*/, 145/*scroll lock*/];

            if (ignoredKeys.indexOf(key) > -1) {
                return;
            }

            if($this.filterTimeout) {
                clearTimeout($this.filterTimeout);
            }

            $this.filterTimeout = setTimeout(function() {
                $this.filter(input.val(), $this.getFilteredList(input));
                $this.filterTimeout = null;
            },
            $this.cfg.filterDelay);
        })
        .on('keydown', this.blockEnterKey);
    },

    blockEnterKey: function(e) {
        var key = e.which,
        keyCode = $.ui.keyCode;

        if((key === keyCode.ENTER)) {
            e.preventDefault();
        }
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

    startsWithFilter: function(value, filter) {
        return value.toLowerCase().indexOf(filter) === 0;
    },

    containsFilter: function(value, filter) {
        return value.toLowerCase().indexOf(filter) !== -1;
    },

    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    getFilteredList: function(filter) {
        return filter.hasClass('ui-source-filter-input') ? this.sourceList : this.targetList;
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

        if(this.hasBehavior('transfer')) {
            var ext = {
                params: []
            },
            paramName = this.id + '_transferred',
            isAdd = from.hasClass('ui-picklist-source');

            items.each(function(index, item) {
                ext.params.push({name:paramName, value:$(item).attr('data-item-value')});
            });

            ext.params.push({name:this.id + '_add', value:isAdd});

            this.callBehavior('transfer', ext);
        }
        $(this.jqId + ' ul').sortable('enable');

        this.updateButtonsState();
    },

    getListName: function(element){
        return element.parent().hasClass("ui-picklist-source") ? "source" : "target";
    },

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

    fireReorderEvent: function() {
        this.callBehavior('reorder');
    },

    isAnimated: function() {
        return (this.cfg.effect && this.cfg.effect != 'none');
    },

    setTabIndex: function() {
        var tabindex = (this.cfg.disabled) ? '-1' : this.getTabIndex();
        this.sourceList.attr('tabindex', tabindex);
        this.targetList.attr('tabindex', tabindex);
        $(this.jqId + ' button').attr('tabindex', tabindex);
        $(this.jqId + ' .ui-picklist-filter-container > input').attr('tabindex', tabindex);
    },
    
    getTabIndex: function() {
        return this.cfg.tabindex||'0';
    },

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

    disableButton: function (button) {
        if (button.hasClass('ui-state-focus')) {
            button.blur();
        }
        
        button.attr('disabled', 'disabled').addClass('ui-state-disabled');
        button.attr('tabindex', '-1');
    },

    enableButton: function (button) {
        button.removeAttr('disabled').removeClass('ui-state-disabled');
        button.attr('tabindex', this.getTabIndex());
    },

    updateListRole: function() {
        this.sourceList.children('li:visible').length > 0 ? this.sourceList.attr('role', 'menu') : this.sourceList.removeAttr('role');
        this.targetList.children('li:visible').length > 0 ? this.targetList.attr('role', 'menu') : this.targetList.removeAttr('role');
    }

});