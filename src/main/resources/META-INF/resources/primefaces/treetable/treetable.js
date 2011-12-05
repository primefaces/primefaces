/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
	this.cfg = cfg;
    this.cfg.scrollable = this.jq.hasClass('ui-treetable-scrollable');
    this.cfg.resizable = this.jq.hasClass('ui-treetable-resizable');
    
    this.bindToggleEvents();
        
    //scrolling
    if(this.cfg.scrollable) {
        this.alignColumnWidths();
        this.setupScrolling();
    }

    //selection
    if(this.cfg.selectionMode) {
        this.jqSelection = $(this.jqId + '_selection');
        var selectionValue = this.jqSelection.val();
        
        this.selection = selectionValue === "" ? [] : selectionValue.split(',');
        
        this.bindSelectionEvents();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.TreeTable, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.TreeTable.prototype.bindToggleEvents = function() {
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
}

PrimeFaces.widget.TreeTable.prototype.bindSelectionEvents = function() {
    var _self = this;
    
    $(this.jqId + ' .ui-treetable-data tr.ui-treetable-selectable-node').die('mouseover.treetable mouseout.treetable click.treetable contextmenu.treetable')
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
            })            
            .live('contextmenu.treetable', function(event) {
               _self.onRowClick(event, $(this));
               event.preventDefault();
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
                node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
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
    if($(e.target).is('div.ui-tt-c,td')) {
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
    
    this.fireSelectNodeEvent(e, nodeKey);
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
        
        this.fireUnselectNodeEvent(e, nodeKey);
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

PrimeFaces.widget.TreeTable.prototype.fireSelectNodeEvent = function(e, nodeKey) {
    if(this.hasBehavior('select')) {
        var selectBehavior = this.cfg.behaviors['select'],
        options = {
            params : {}
        };
        
        options.params[this.id + '_instantSelect'] = nodeKey;
        
        selectBehavior.call(this, e, options);
    }
}

PrimeFaces.widget.TreeTable.prototype.fireUnselectNodeEvent = function(e, nodeKey) {
    if(this.hasBehavior('unselect')) {
        var unselectBehavior = this.cfg.behaviors['unselect'],
        options = {
            params : {}
        };
        
        options.params[this.id + '_instantUnselect'] = nodeKey;
        
        unselectBehavior.call(this, e, options);
    }
}

PrimeFaces.widget.TreeTable.prototype.setupScrolling = function() {
    var scrollHeader = $(this.jqId + ' .ui-treetable-scrollable-header'),
    scrollBody = $(this.jqId + ' .ui-treetable-scrollable-body'),
    scrollFooter = $(this.jqId + ' .ui-treetable-scrollable-footer');
    
    if(this.cfg.scrollWidth) {
        scrollHeader.width(this.cfg.scrollWidth);
        scrollBody.width(this.cfg.scrollWidth);
        scrollFooter.width(this.cfg.scrollWidth);
    }
        
    scrollBody.scroll(function() {
        scrollHeader.scrollLeft(scrollBody.scrollLeft());
        scrollFooter.scrollLeft(scrollBody.scrollLeft());
    });
}

/**
 * Moves widths of columns to column wrappers
 */
PrimeFaces.widget.TreeTable.prototype.alignColumnWidths = function() {
    this.jq.find('div.ui-tt-c').each(function() {
        var wrapper = $(this),
        column = wrapper.parent();
        
        wrapper.width(column.width());
        column.width('');
    });
}