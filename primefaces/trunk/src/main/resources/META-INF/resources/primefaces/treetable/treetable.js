/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = function(id, cfg) {
	this.id = id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
	this.cfg = cfg;
    
    this.bindEvents();
    
    if(this.cfg.selectionMode != 'none') {
        this.jqSelection = $(this.jqId + '_selection');
        var selectionValue = this.jqSelection.val();
        
        this.selection = selectionValue === "" ? [] : selectionValue.split(',');
    }
}

PrimeFaces.widget.TreeTable.prototype.bindEvents = function() {
    var _self = this;
    
    //expand and collapse
    $(this.jqId + ' .ui-treetable-toggler').die('click.treetable')
        .live('click.treetable', function(e) {
            var toggler = $(this),
            node = toggler.parents('tr:first');
            
            if(toggler.hasClass('ui-icon-triangle-1-e'))
                _self.expandNode(e, node);
            else {
                _self.collapseNode(e, node);
            }
        });
        
    //selection
    $(this.jqId + ' .ui-treetable-data tr').die('click.treetable')
            .live('mouseover.treetable', function(e) {
                var element = $(this);

                if(!element.hasClass('ui-selected')) {
                    element.addClass('ui-state-highlight');
                }
            })
            .live('mouseout.treetable', function(e) {
                var element = $(this);

                if(!element.hasClass('ui-selected')) {
                    element.removeClass('ui-state-highlight');
                }
            })
            .live('click.treetable', function(e) {
                _self.onRowClick(e, $(this));
                e.preventDefault();
            });
}

PrimeFaces.widget.TreeTable.prototype.expandNode = function(e, node) {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id
    },
    _self = this,
    nodeKey = node.attr('id').split('_node_')[1];
    
    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                node.replaceWith(content);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
            
            node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
        }

        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
        
        return true;
    };
    
    var params = {};
    params[this.id + '_expand'] = nodeKey;
    
    options.params = params;
    
    if(this.hasBehavior('expand')) {
        var expandBehavior = this.cfg.behaviors['expand'];
        
        expandBehavior.call(this, e, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

PrimeFaces.widget.TreeTable.prototype.collapseNode = function(e, node) {
    node.siblings('[id^="' + node.attr('id') + '"]').remove();

    node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
    
    if(this.hasBehavior('collapse')) {
        var collapseBehavior = this.cfg.behaviors['collapse'],
        nodeKey = node.attr('id').split('_node_')[1];
        
        var options = {
            params : {}
        };
        options.params[this.id + '_collapse'] = nodeKey;
        
        collapseBehavior.call(this, e, options);
    }
}

PrimeFaces.widget.TreeTable.prototype.onRowClick = function(e, node) {
    
    //Check if rowclick triggered this event not an element in row content
    if($(e.target).is('td,div,span')) {
        var selected = node.hasClass('ui-selected');

        if(selected)
            this.unselectNode(e, node);
        else
            this.selectNode(e, node);
    }
}

PrimeFaces.widget.TreeTable.prototype.selectNode = function(e, node) {
    var nodeKey = node.attr('id').split('_node_')[1];
    
    //unselect previous selection
    if(this.isSingleSelection() || (this.isMultipleSelection() && !e.metaKey)) {
        node.siblings('.ui-selected').removeClass('ui-selected ui-state-highlight'); 
        this.selection = [];
    }

    //add to selection
    node.addClass('ui-state-highlight ui-selected');
    this.addSelection(nodeKey);

    //save state
    this.writeSelections();
}

PrimeFaces.widget.TreeTable.prototype.unselectNode = function(e, node) {
    var nodeKey = node.attr('id').split('_node_')[1];

    if(e.metaKey) {
        //remove visual style
        node.removeClass('ui-selected ui-state-highlight');

        //remove from selection
        this.removeSelection(nodeKey);

        //save state
        this.writeSelections();
    }
    else if(this.isMultipleSelection()){
        this.selectRow(e, node);
    }
}


PrimeFaces.widget.TreeTable.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

/**
 * Remove given rowIndex from selection
 */
PrimeFaces.widget.TreeTable.prototype.removeSelection = function(nodeKey) {
    var selection = this.selection;
    
    $.each(selection, function(index, value) {
        if(value === nodeKey) {
            selection.remove(index);
            
            return false;       //break
        } 
        else {
            return true;        //continue
        }
    });
}

/**
 * Adds given rowIndex to selection if it doesn't exist already
 */
PrimeFaces.widget.TreeTable.prototype.addSelection = function(nodeKey) {
    if(!this.isSelected(nodeKey)) {
        this.selection.push(nodeKey);
    }
}

PrimeFaces.widget.TreeTable.prototype.isSelected = function(nodeKey) {
    var selection = this.selection,
    selected = false;
    
    $.each(selection, function(index, value) {
        if(value === nodeKey) {
            selected = true;
            
            return false;       //break
        } 
        else {
            return true;        //continue
        }
    });
    
    return selected;
}

PrimeFaces.widget.TreeTable.prototype.isSingleSelection = function() {
    return this.cfg.selectionMode == 'single';
}

PrimeFaces.widget.TreeTable.prototype.isMultipleSelection = function() {
    return this.cfg.selectionMode == 'multiple';
}

/**
 * Writes selected row ids to state holder
 */
PrimeFaces.widget.TreeTable.prototype.writeSelections = function() {
    this.jqSelection.val(this.selection.join(','));
}