/**
 * PrimeFaces PickList Widget
 */
PrimeFaces.widget.PickList = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.sourceList = this.jq.find('.ui-picklist-source');
    this.targetList = this.jq.find('.ui-picklist-target');
    this.sourceInput = $(this.jqId + '_source');
    this.targetInput = $(this.jqId + '_target');
    this.items = this.jq.find('.ui-picklist-item:not(.ui-state-disabled)');

    //generate input options
    this.generateItems(this.sourceList, this.sourceInput);
    this.generateItems(this.targetList, this.targetInput);

    //Buttons
    this.setupButtons();

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
            update: function(event, ui) {
                ui.item.removeClass('ui-state-highlight');
                
                _self.saveState();
            },
            receive: function(event, ui) {
                _self.fireOnTransferEvent(ui.item, ui.sender, ui.item.parents('ul.ui-picklist-list:first'), 'dragdrop');
            }
        });

        //Visual selection and Double click transfer
        this.items.mouseover(function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).addClass('ui-state-hover');
        })
        .mouseout(function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).removeClass('ui-state-hover');
        })
        .mousedown(function(e) {
            var element = $(this),
            metaKey = (e.metaKey||e.ctrlKey);
            
            if(!metaKey) {
                element.removeClass('ui-state-hover').addClass('ui-state-highlight')
                .siblings('.ui-state-highlight').removeClass('ui-state-highlight');
            }
            else {
                if(element.hasClass('ui-state-highlight'))
                    element.removeClass('ui-state-highlight');
                else
                    element.removeClass('ui-state-hover').addClass('ui-state-highlight');
            }
        })
        .dblclick(function() {
            var item = $(this);

            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                if($(this).parent().hasClass('ui-picklist-source'))
                    _self.transfer($(this), _self.sourceList, _self.targetList, 'dblclick');
                else
                    _self.transfer($(this), _self.targetList, _self.sourceList, 'dblclick');
            });

            PrimeFaces.clearSelection();
        });
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.PickList, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.PickList.prototype.generateItems = function(list, input) {   
    list.children('.ui-picklist-item').each(function(i, item) {
        var item = $(this),
        itemValue = item.data('item-value');
        
        input.append('<option value="' + itemValue + '" selected="selected">' + itemValue + '</option>');
    });
}

PrimeFaces.widget.PickList.prototype.setupButtons = function() {
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
        $(this.jqId + ' .ui-picklist-source-controls  .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.sourceList);});
    }

    if(this.cfg.showTargetControls) {
        $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-up').click(function() {_self.moveUp(_self.targetList);});
        $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-top').click(function() {_self.moveTop(_self.targetList);});
        $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-down').click(function() {_self.moveDown(_self.targetList);});
        $(this.jqId + ' .ui-picklist-target-controls .ui-picklist-button-move-bottom').click(function() {_self.moveBottom(_self.targetList);});
    }

}

PrimeFaces.widget.PickList.prototype.add = function() {
    var _self = this;

    this.sourceList.children('li.ui-picklist-item.ui-state-highlight').removeClass('ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer($(this), _self.sourceList, _self.targetList, 'command');
    });
}

PrimeFaces.widget.PickList.prototype.addAll = function() {
    var _self = this;

    this.sourceList.children('li.ui-picklist-item:not(.ui-state-disabled)').removeClass('ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer($(this), _self.sourceList, _self.targetList, 'command');
    });
}

PrimeFaces.widget.PickList.prototype.remove = function() {
    var _self = this;

    this.targetList.children('li.ui-picklist-item.ui-state-highlight').removeClass('ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer($(this), _self.targetList, _self.sourceList, 'command');
    });
}

PrimeFaces.widget.PickList.prototype.removeAll = function() {
    var _self = this;
    
    this.targetList.children('li.ui-picklist-item:not(.ui-state-disabled)').removeClass('ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer($(this), _self.targetList, _self.sourceList, 'command');
    });
}

PrimeFaces.widget.PickList.prototype.moveUp = function(list) {
    var _self = this;

    list.children('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':first-child')) {
            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                item.insertBefore(item.prev()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                    _self.saveState();
                });
            });
        }
    });
}

PrimeFaces.widget.PickList.prototype.moveTop = function(list) {
    var _self = this;

    list.children('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':first-child')) {
            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                item.prependTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function(){
                    _self.saveState();
                });
            });
        }

    });
}

PrimeFaces.widget.PickList.prototype.moveDown = function(list) {
    var _self = this;

    list.children('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':last-child')) {
            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                item.insertAfter(item.next()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                    _self.saveState();
                });
            });
        }

    });
}

PrimeFaces.widget.PickList.prototype.moveBottom = function(list) {
    var _self = this;

    list.children('.ui-state-highlight').each(function() {
        var item = $(this);

        if(!item.is(':last-child')) {
            item.hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                item.appendTo(item.parent()).show(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
                    _self.saveState();
                });
            });
        }

    });
}

/**
 * Clear inputs and repopulate them from the list states 
 */
PrimeFaces.widget.PickList.prototype.saveState = function() {
    this.sourceInput.children().remove();
    this.targetInput.children().remove();
    
    this.generateItems(this.sourceList, this.sourceInput);
    this.generateItems(this.targetList, this.targetInput);
}

PrimeFaces.widget.PickList.prototype.transfer = function(item, from, to, type) {    
    var _self = this;
    
    item.appendTo(to).removeClass('ui-state-highlight').show(this.cfg.effect, {}, this.cfg.effectSpeed, function() {
        _self.saveState();
        _self.fireOnTransferEvent(item, from, to, type);
    });
}

PrimeFaces.widget.PickList.prototype.fireOnTransferEvent = function(item, from, to, type) {
    if(this.cfg.onTransfer) {
        var obj = {};
        obj.item = item;
        obj.from = from;
        obj.to = to;
        obj.type = type;

        this.cfg.onTransfer.call(this, obj);
    }
}