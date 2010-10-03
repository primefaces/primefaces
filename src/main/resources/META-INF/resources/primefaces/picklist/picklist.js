/**
 * PrimeFaces PickList Widget
 */
PrimeFaces.widget.PickList = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.sourceState = jQuery(this.jqId + '_source');
    this.targetState = jQuery(this.jqId + '_target');

    //Buttons
    this.setupButtons();

    //Sortable lists
    var _self = this;
    jQuery('.ui-picklist ul').sortable({
       connectWith:'.ui-picklist-list',
       revert:true,
       receive: function(event, ui) {
           _self.handleReceive(event, ui);
       }
    });

     //Selection
     jQuery(this.jqId + ' .ui-picklist-item').mousedown(function() {
        jQuery(this).toggleClass('ui-state-highlight');
     });
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
        text: true
    }).click(function() {
        _self.add();
    });

    //Add all button
    jQuery(this.jqId + ' .ui-picklist-button-add-all').button({
        icons: {
            primary: "ui-icon-arrowstop-1-e"
        },
        text: true
    }).click(function() {
        _self.addAll();
    });

    //Remove button
    jQuery(this.jqId + ' .ui-picklist-button-remove').button({
        icons: {
            primary: "ui-icon-arrow-1-w"
        },
        text: true
    }).click(function() {
        _self.remove();
    });

    //Remove all button
    jQuery(this.jqId + ' .ui-picklist-button-remove-all').button({
        icons: {
            primary: "ui-icon-arrowstop-1-w"
        },
        text: true
    }).click(function() {
        _self.removeAll();
    });   
}

PrimeFaces.widget.PickList.prototype.add = function() {
    var _self = this;

    jQuery(this.jqId + ' .ui-picklist-source .ui-picklist-item.ui-state-highlight').fadeOut('fast', function() {
        jQuery(this).removeClass('ui-state-highlight').appendTo(_self.jqId + ' .ui-picklist-target').fadeIn();
        _self.saveState();
    });
}

PrimeFaces.widget.PickList.prototype.addAll = function() {
    var _self = this;

    jQuery(this.jqId + ' .ui-picklist-source .ui-picklist-item').fadeOut('fast', function() {
        jQuery(this).removeClass('ui-state-highlight').appendTo(_self.jqId + ' .ui-picklist-target').fadeIn();
        _self.saveState();
    });
}

PrimeFaces.widget.PickList.prototype.remove = function() {
    var _self = this;

    jQuery(this.jqId + ' .ui-picklist-target .ui-picklist-item.ui-state-highlight').fadeOut('fast', function() {
        jQuery(this).removeClass('ui-state-highlight').appendTo(_self.jqId + ' .ui-picklist-source').fadeIn();
        _self.saveState();
    });
}

PrimeFaces.widget.PickList.prototype.removeAll = function() {
    var _self = this;
    
    jQuery(this.jqId + ' .ui-picklist-target .ui-picklist-item').fadeOut('fast', function() {
        jQuery(this).removeClass('ui-state-highlight').appendTo(_self.jqId + ' .ui-picklist-source').fadeIn();
        _self.saveState();
    });
}

PrimeFaces.widget.PickList.prototype.handleReceive = function(event, ui) {
    ui.item.removeClass('ui-state-highlight');

    this.saveState();
}

PrimeFaces.widget.PickList.prototype.saveState = function() {
    this.saveListState('.ui-picklist-source', this.sourceState);
    this.saveListState('.ui-picklist-target', this.targetState);
}

PrimeFaces.widget.PickList.prototype.saveListState = function(list, holder) {
    var values = [];
    jQuery(this.jqId + ' ' + list).children('li.ui-picklist-item').each(function() {
        values.push(jQuery(this).html());
    });
    
    holder.val(values.join(','));
}
