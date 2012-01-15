/**
 * PrimeFaces Tree Widget
 */
PrimeFaces.widget.Tree = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.cfg.formId = this.jq.parents('form:first').attr('id');

    if(this.cfg.selectionMode) {
        this.selectionHolder = $(this.jqId + '_selection');
        var selectionsValue = this.selectionHolder.val();
        this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

        if(this.cfg.selectionMode == 'checkbox')
            this.preselectCheckboxPropagation();
    }

    this.bindEvents();
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Tree, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Tree.prototype.bindEvents = function() {
    var _self = this,
    selectionMode = this.cfg.selectionMode;

    //expand-collapse
    $(this.jqId + ' .ui-tree-icon')
        .die()
        .live('click',function(e) {
            var icon = $(this),
            node = icon.parents('li:first');

            if(icon.hasClass('ui-icon-triangle-1-e'))
                _self.expandNode(node);
            else
                _self.collapseNode(node);
        });

    //selection hover
    if(selectionMode) {
        var clickTargetSelector = this.jqId  + ' .ui-tree-selectable-node';

        $(clickTargetSelector)
            .die('mouseover.tree mouseout.tree click.tree contextmenu.tree')
            .live('mouseover.tree', function() {
                var element = $(this);
                
                if(!element.hasClass('ui-state-highlight'))
                    $(this).addClass('ui-state-hover');
            })
            .live('mouseout.tree', function() {
                var element = $(this);
                
                if(!element.hasClass('ui-state-highlight'))
                    $(this).removeClass('ui-state-hover');
            })
            .live('click.tree', function(e) {
                _self.onNodeClick(e, $(this).parents('li:first'));
            })
            .live('contextmenu.tree', function(e) {
                _self.onNodeClick(e, $(this).parents('li:first'));
                e.preventDefault();
            });
    }
}

PrimeFaces.widget.Tree.prototype.onNodeClick = function(e, node) {
    PrimeFaces.clearSelection();
                    
    if($(e.target).is(':not(.ui-tree-icon)')) {
        if(this.cfg.onNodeClick) {
            this.cfg.onNodeClick.call(this, node);
        }
        
        if(this.isNodeSelected(node))
            this.unselectNode(e, node);
        else
            this.selectNode(e, node);
    }
}

PrimeFaces.widget.Tree.prototype.expandNode = function(node) {
    var _self = this;

    if(this.cfg.dynamic) {

        if(this.cfg.cache && node.children('.ui-tree-nodes').children().length > 0) {
            this.showNodeChildren(node);
            
            return;
        }

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId
        };

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();
                
                if(id == _self.id){
                    node.children('.ui-tree-nodes').append(content);
                    
                    _self.showNodeChildren(node);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }
            
            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };
        
        var params = {};
        params[this.id + '_expandNode'] = _self.getNodeId(node);

        options.params = params;

        if(this.hasBehavior('expand')) {
            var expandBehavior = this.cfg.behaviors['expand'];

            expandBehavior.call(this, node, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    }
    else {
        this.showNodeChildren(node);
        this.fireExpandEvent(node);
    }
}

PrimeFaces.widget.Tree.prototype.fireExpandEvent = function(node) {
    if(this.cfg.behaviors) {
        var expandBehavior = this.cfg.behaviors['expand'];
        if(expandBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_expandNode'] = this.getNodeId(node);
            
            expandBehavior.call(this, node, ext);
        }
    }
}

PrimeFaces.widget.Tree.prototype.collapseNode = function(node) {
    var _self = this,
    icon = node.find('.ui-tree-icon:first'),
    lastClass = node.attr('class').split(' ').slice(-1),
    nodeIcon = icon.next(),
    iconState = this.cfg.iconStates[lastClass];

    icon.addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');

    if(iconState) {
        nodeIcon.removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
    }

    var childNodeContainer = node.children('.ui-tree-nodes');
    childNodeContainer.hide();
        
    if(_self.cfg.dynamic && !_self.cfg.cache) {
        childNodeContainer.empty();
    }
    
    _self.fireCollapseEvent(node);
}

PrimeFaces.widget.Tree.prototype.fireCollapseEvent = function(node) {
    if(this.cfg.behaviors) {
        var collapseBehavior = this.cfg.behaviors['collapse'];
        if(collapseBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_collapseNode'] = this.getNodeId(node);
            
            collapseBehavior.call(this, node, ext);
        }
    }
}

PrimeFaces.widget.Tree.prototype.showNodeChildren = function(node) {
    var icon = node.find('.ui-tree-icon:first'),
    lastClass = node.attr('class').split(' ').slice(-1),
    nodeIcon = icon.next(),
    iconState = this.cfg.iconStates[lastClass];

    icon.addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');

    if(iconState) {
        nodeIcon.removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
    }

    node.children('.ui-tree-nodes').show();
}

PrimeFaces.widget.Tree.prototype.selectNode = function(e, node) {

    if(this.isCheckboxSelection()) {
        this.toggleCheckbox(node, true);
    }
    else {
        if(this.isSingleSelection() || (this.isMultipleSelection() && !e.metaKey)) {
            //clean all selections
            this.selections = [];
            this.jq.find('.ui-tree-node-content.ui-state-highlight').removeClass('ui-state-highlight');
        }

        node.find('.ui-tree-node-content:first').removeClass('ui-state-hover').addClass('ui-state-highlight');

        this.addToSelection(this.getNodeId(node));
    }

    this.writeSelections();

    this.fireNodeSelectEvent(node);
}

PrimeFaces.widget.Tree.prototype.unselectNode = function(e, node) {
    var nodeId = this.getNodeId(node);

    //select node
    if(this.isCheckboxSelection()) {
        this.toggleCheckbox(node, false);
        this.writeSelections();
        this.fireNodeUnselectEvent(node);
    }
    else if(e.metaKey) {
        //remove visual style    
        node.find('.ui-tree-node-content:first').removeClass('ui-state-highlight');

        //remove from selection
        this.removeFromSelection(nodeId);
        
        this.writeSelections();

        this.fireNodeUnselectEvent(node);
    } 
    else if(this.isMultipleSelection()){
        this.selectNode(e, node);
    }
}

PrimeFaces.widget.Tree.prototype.writeSelections = function() {    
    this.selectionHolder.val(this.selections.join(','));
}

PrimeFaces.widget.Tree.prototype.fireNodeSelectEvent = function(node) {
    if(this.cfg.behaviors) {
        var selectBehavior = this.cfg.behaviors['select'];
        
        if(selectBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_instantSelection'] = this.getNodeId(node);
            
            selectBehavior.call(this, node, ext);
        }
    }
}

PrimeFaces.widget.Tree.prototype.fireNodeUnselectEvent = function(node) {
    if(this.cfg.behaviors) {
        var unselectBehavior = this.cfg.behaviors['unselect'];
        
        if(unselectBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_instantUnselection'] = this.getNodeId(node);
            
            unselectBehavior.call(this, node, ext);
        }
    }
}

PrimeFaces.widget.Tree.prototype.getNodeId = function(node) {
    return node.attr('id').split('_node_')[1];
}

PrimeFaces.widget.Tree.prototype.isNodeSelected = function(node) {
    return $.inArray(this.getNodeId(node), this.selections) != -1;
}

PrimeFaces.widget.Tree.prototype.isSingleSelection = function() {
    return this.cfg.selectionMode == 'single';
}

PrimeFaces.widget.Tree.prototype.isMultipleSelection = function() {
    return this.cfg.selectionMode == 'multiple';
}

PrimeFaces.widget.Tree.prototype.isCheckboxSelection = function() {
    return this.cfg.selectionMode == 'checkbox';
}

PrimeFaces.widget.Tree.prototype.addToSelection = function(nodeId) {
    this.selections.push(nodeId);
}

PrimeFaces.widget.Tree.prototype.removeFromSelection = function(nodeId) {
    this.selections = $.grep(this.selections, function(r) {
        return r != nodeId;
    });
}

PrimeFaces.widget.Tree.prototype.toggleCheckbox = function(node, check) {
    var _self = this;

    //propagate selection down
    node.find('.ui-tree-checkbox-icon').each(function() {
        var icon = $(this),
        nodeId = _self.getNodeId(icon.parents('li:first'));

        if(check) {
            if($.inArray(nodeId, _self.selections) == -1) {
                icon.addClass('ui-icon ui-icon-check');
            
                _self.addToSelection(nodeId);
            }
        }
        else {
            icon.removeClass('ui-icon ui-icon-check');

            _self.removeFromSelection(nodeId);
        }
    });

    //propagate selection up
    node.parents('li').each(function() {
        var parentNode = $(this),
        nodeId = _self.getNodeId(parentNode),
        icon = parentNode.find('.ui-tree-checkbox-icon:first'),
        checkedChildren = parentNode.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon.ui-icon-check'),
        allChildren = parentNode.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon');

        if(check) {
            if(checkedChildren.length == allChildren.length) {
                icon.removeClass('ui-icon ui-icon-minus').addClass('ui-icon ui-icon-check');

                _self.addToSelection(nodeId);
            } else {
                icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');
            }
        }
        else {
            if(checkedChildren.length > 0) {
                icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');
            } else {
                icon.removeClass('ui-icon ui-icon-minus ui-icon-check');
            }

            _self.removeFromSelection(nodeId);
        }

    });
}

PrimeFaces.widget.Tree.prototype.preselectCheckboxPropagation = function() {
    this.jq.find('.ui-tree-checkbox-icon').not('.ui-icon-check').each(function() {
        var icon = $(this),
        node = icon.parents('li:first');

        if(node.find('.ui-tree-checkbox-icon.ui-icon-check').length > 0)
            $(this).addClass('ui-icon ui-icon-minus');
    });
}

PrimeFaces.widget.Tree.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}