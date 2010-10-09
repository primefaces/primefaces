/**
 * PrimeFaces PickList Widget
 */
PrimeFaces.widget.PickList = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.sourceList = jQuery(this.jqId + ' ul.ui-picklist-source');
    this.targetList = jQuery(this.jqId + ' ul.ui-picklist-target');
    this.sourceState = jQuery(this.jqId + '_source');
    this.targetState = jQuery(this.jqId + '_target');

    //Buttons
    this.setupButtons();

    if(this.cfg.disabled) {
        jQuery(this.jqId + ' li.ui-picklist-item').addClass('ui-state-disabled');
        jQuery(this.jqId + ' button').attr('disabled', 'disabled').addClass('ui-state-disabled');
    }
    else {
        var _self = this;

        //Sortable lists
        jQuery(this.jqId + ' ul').sortable({
            connectWith: this.jqId + ' .ui-picklist-list',
            revert: true,
            receive: function(event, ui) {
                _self.handleReceive(event, ui);
            }
        });

        //Visual selection
        jQuery(this.jqId + ' li.ui-picklist-item').mousedown(function() {
            jQuery(this).toggleClass('ui-state-highlight');
        });

        //Pojo support
        if(this.cfg.pojo) {
            this.parseItemValues(this.sourceState, this.sourceList);
            this.parseItemValues(this.targetState, this.targetList);
        }
    }
}

/**
 * Creates button controls using progressive enhancement
 */
PrimeFaces.widget.PickList.prototype.setupButtons = function() {
    var _self = this;
    
    //Add button
    jQuery(this.jqId + ' .ui-picklist-button-add').button({
        icons: {
            primary: "ui-icon-arrow-1-e"
        },
        text: (!_self.cfg.iconOnly)
    }).click(function() {
        _self.add();
    });

    //Add all button
    jQuery(this.jqId + ' .ui-picklist-button-add-all').button({
        icons: {
            primary: "ui-icon-arrowstop-1-e"
        },
        text: (!_self.cfg.iconOnly)
    }).click(function() {
        _self.addAll();
    });

    //Remove button
    jQuery(this.jqId + ' .ui-picklist-button-remove').button({
        icons: {
            primary: "ui-icon-arrow-1-w"
        },
        text: (!_self.cfg.iconOnly)
    }).click(function() {
        _self.remove();
    });

    //Remove all button
    jQuery(this.jqId + ' .ui-picklist-button-remove-all').button({
        icons: {
            primary: "ui-icon-arrowstop-1-w"
        },
        text: (!_self.cfg.iconOnly)
    }).click(function() {
        _self.removeAll();
    });   
}

PrimeFaces.widget.PickList.prototype.add = function() {
    var _self = this;

    this.sourceList.children('li.ui-picklist-item.ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer(this, _self.targetList);
    });
}

PrimeFaces.widget.PickList.prototype.addAll = function() {
    var _self = this;


    this.sourceList.children('li.ui-picklist-item').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer(this, _self.targetList);
    });
}

PrimeFaces.widget.PickList.prototype.remove = function() {
    var _self = this;

    this.targetList.children('li.ui-picklist-item.ui-state-highlight').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer(this, _self.sourceList);
    });
}

PrimeFaces.widget.PickList.prototype.removeAll = function() {
    var _self = this;
    
    this.targetList.children('li.ui-picklist-item').hide(_self.cfg.effect, {}, _self.cfg.effectSpeed, function() {
        _self.transfer(this, _self.sourceList);
    });
}

PrimeFaces.widget.PickList.prototype.handleReceive = function(event, ui) {
    ui.item.removeClass('ui-state-highlight');

    this.saveState();
}

PrimeFaces.widget.PickList.prototype.saveState = function() {
    this.saveListState(this.sourceList, this.sourceState);
    this.saveListState(this.targetList, this.targetState);
}

PrimeFaces.widget.PickList.prototype.transfer = function(element, to) {
    jQuery(element).removeClass('ui-state-highlight').appendTo(to).show(this.cfg.effect, {}, this.cfg.effectSpeed);
    
    this.saveState();
}

PrimeFaces.widget.PickList.prototype.saveListState = function(list, holder) {
    var values = [],
    pojo = this.cfg.pojo;
    
    jQuery(list).children('li.ui-picklist-item').each(function() {
        var item = jQuery(this),
        itemValue = pojo ? item.data('itemValue') : item.html();

        values.push(itemValue);
    });
    
    holder.val(values.join(','));
}

/**
 * Parses item values and assigns to li elements
 */
PrimeFaces.widget.PickList.prototype.parseItemValues = function(state, list) {
    var itemValues = state.val().split(','),
    itemElements = list.children('li');

    for(var i in itemValues) {
        jQuery(itemElements.get(i)).data('itemValue', itemValues[i]);
    }
}
