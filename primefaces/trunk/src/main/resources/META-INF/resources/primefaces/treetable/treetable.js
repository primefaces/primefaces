/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.tbody = $(this.jqId + '_data');
        this.cfg.scrollable = this.jq.hasClass('ui-treetable-scrollable');
        this.cfg.resizable = this.jq.hasClass('ui-treetable-resizable');

        //scrolling
        if(this.cfg.scrollable) {
            this.setupScrolling();
        }
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this,
        togglerSelector = this.jqId + ' .ui-treetable-toggler';
        
        //expand and collapse
        $(document).off('click.treeTable', togglerSelector)
                    .on('click.treeTable', togglerSelector, null, function(e) {
                        var toggler = $(this),
                        node = toggler.closest('tr');
            
                        if(toggler.hasClass('ui-icon-triangle-1-e'))
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });
            
        //selection
        if(this.cfg.selectionMode) {
            this.jqSelection = $(this.jqId + '_selection');
            var selectionValue = this.jqSelection.val();
            this.selection = selectionValue === "" ? [] : selectionValue.split(',');

            this.bindSelectionEvents();
        }
    },
    
    bindSelectionEvents: function() {
        var $this = this,
        rowSelector = this.jqId + ' .ui-treetable-data tr.ui-treetable-selectable-node';
        
        $(document).off('mouseover.treeTable mouseout.treeTable click.treeTable', rowSelector)
                    .on('mouseover.treeTable', rowSelector, null, function(e) {
                        var element = $(this);
                        if(!element.hasClass('ui-state-highlight')) {
                            element.addClass('ui-state-hover');
                        
                            if($this.isCheckboxSelection()) {
                                element.find('> td:first-child > div.ui-tt-c > div.ui-chkbox > div.ui-chkbox-box').addClass('ui-state-hover');
                            }
                        }
                    })
                    .on('mouseout.treeTable', rowSelector, null, function(e) {
                        var element = $(this);
                        if(!element.hasClass('ui-state-highlight')) {
                            element.removeClass('ui-state-hover');
                            
                            if($this.isCheckboxSelection()) {
                                element.find('> td:first-child > div.ui-tt-c > div.ui-chkbox > div.ui-chkbox-box').removeClass('ui-state-hover');
                            }
                        }
                    })
                    .on('click.treeTable', rowSelector, null, function(e) {
                        $this.onRowClick(e, $(this));
                        e.preventDefault();
                    });
                    
        if(this.isCheckboxSelection()) {
           var checkboxSelector = this.jqId + ' .ui-treetable-data tr.ui-treetable-selectable-node td:first-child div.ui-tt-c div.ui-chkbox-box';
           
           $(document).off('click.treeTable', checkboxSelector)
                      .on('click.treeTable', checkboxSelector, null, function(e) {
                          var node = $(this).closest('tr.ui-treetable-selectable-node');
                          $this.toggleCheckbox(node);
                      });
        }
    },
    
    expandNode: function(node) {
        var options = {
            source: this.id,
            process: this.id,
            update: this.id
        },
        $this = this,
        nodeKey = node.attr('data-rk'),
        parentNodeKey = node.attr('data-prk');

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == $this.id){
                    node.replaceWith(content);
                    node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
                    node.attr('aria-expanded', true);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };

        options.params = [
            {name: this.id + '_expand', value: nodeKey},
            {name: this.id + '_expandParent', value: parentNodeKey}
        ];

        if(this.hasBehavior('expand')) {
            var expandBehavior = this.cfg.behaviors['expand'];

            expandBehavior.call(this, node, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
    
    collapseNode: function(node) {
        var nodeKey = node.attr('data-rk'),
        nextNodes = node.nextAll();
        
        for(var i = 0; i < nextNodes.length; i++) {
            var nextNode = nextNodes.eq(i),
            nextNodeRowKey = nextNode.attr('data-rk');
            
            if(nextNodeRowKey.indexOf(nodeKey) != -1) {
               nextNode.remove();
            } 
            else {
                break;
            }
        }
    
        node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');

        node.attr('aria-expanded', false);

        if(this.hasBehavior('collapse')) {
            var collapseBehavior = this.cfg.behaviors['collapse'],
            nodeKey = node.attr('data-rk');

            var ext = {
                params : [
                    {name: this.id + '_collapse', value: nodeKey}
                ]
            };

            collapseBehavior.call(this, node, ext);
        }
    },
    
    onRowClick: function(event, node) {
        if($(event.target).is('.ui-tt-c,td,span:not(.ui-c)')) {
            var selected = node.hasClass('ui-state-highlight'),
            metaKey = event.metaKey||event.ctrlKey,
            shiftKey = event.shiftKey;
            
            if(this.isSingleSelection()) {
                if(selected && metaKey)
                    this.unselectNode(node);
                else
                    this.selectNode(node)
            }
            else if(this.isMultipleSelection()) { 
                if(shiftKey) {
                    this.selectNodesInRange(node);
                }
                else {
                    if(!metaKey) {
                        this.unselectAllNodes();
                    }
                    
                    if(selected && metaKey) {
                        this.unselectNode(node);
                    }
                    else {
                        this.selectNode(node);
                        this.cursorNode = node;
                    }
                }                    
            }
            else if(this.isCheckboxSelection()) {
                this.toggleCheckbox(node);
            }

            PrimeFaces.clearSelection();
        }
    },
    
    selectNode: function(node, silent) {
        var nodeKey = node.attr('data-rk');

        node.removeClass('ui-state-hover').addClass('ui-state-highlight').attr('aria-selected', true);
        this.addSelection(nodeKey);
        this.writeSelections();
        
        if(this.isCheckboxSelection()) {
            node.find('> td:first-child > div.ui-tt-c > div.ui-chkbox > div.ui-chkbox-box').removeClass('ui-state-hover')
                .children('span.ui-chkbox-icon').removeClass('ui-icon ui-icon-minus').addClass('ui-icon ui-icon-check');
        }

        if(!silent) {
            this.fireSelectNodeEvent(nodeKey);
        }
    },
    
    unselectNode: function(node, silent) {
        var nodeKey = node.attr('data-rk');
        
        node.removeClass('ui-state-highlight').attr('aria-selected', false);
        this.removeSelection(nodeKey);
        this.writeSelections();
        
        if(this.isCheckboxSelection()) {
            node.find('> td:first-child > div.ui-tt-c > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon').removeClass('ui-icon ui-icon-check ui-icon-minus');
        }

        if(!silent) {
            this.fireUnselectNodeEvent(nodeKey);
        }
    },
    
    unselectAllNodes: function() {
        var selectedNodes = this.tbody.children('tr.ui-state-highlight');
        
        for(var i = 0; i < selectedNodes.length; i++) {
            this.unselectNode(selectedNodes.eq(i), true);
        }
    },
    
    selectNodesInRange: function(node) {
        if(this.cursorNode) {
            this.unselectAllNodes();

            var currentNodeIndex = node.index(),
            cursorNodeIndex = this.cursorNode.index(),
            startIndex = (currentNodeIndex > cursorNodeIndex) ? cursorNodeIndex : currentNodeIndex,
            endIndex = (currentNodeIndex > cursorNodeIndex) ? (currentNodeIndex + 1) : (cursorNodeIndex + 1),
            nodes = this.tbody.children();

            for(var i = startIndex ; i < endIndex; i++) {
                this.selectNode(nodes.eq(i), true);
            }
        } 
        else {
            this.selectNode(node);
        }
    },
    
    toggleCheckbox: function(node) {
        var selected = node.hasClass('ui-state-highlight'),
        parentNode = this.getParent(node);
                
        //toggle itself
        if(selected)
            this.unselectNode(node, true);
        else
            this.selectNode(node, true);
        
        //propagate down
        var descendants = this.getDescendants(node);
        for(var i = 0; i < descendants.length; i++) {
            var descendant = descendants[i];
            
            if(selected)
                this.unselectNode(descendant, true);
            else
                this.selectNode(descendant, true);
        }
        
        //propagate up
        this.propagateUp(parentNode);
    },
    
    getDescendants: function(node) {
        var nodeKey = node.attr('data-rk'),
        nextNodes = node.nextAll(),
        descendants = [];
        
        for(var i = 0; i < nextNodes.length; i++) {
            var nextNode = nextNodes.eq(i),
            nextNodeRowKey = nextNode.attr('data-rk');
            
            if(nextNodeRowKey.indexOf(nodeKey) != -1) {
                descendants.push(nextNode);
            } 
            else {
                break;
            }
        }
        
        return descendants;
    },
    
    getChildren: function(node) {
        var nodeKey = node.attr('data-rk'),
        nextNodes = node.nextAll(),
        children = [];
        
        for(var i = 0; i < nextNodes.length; i++) {
            var nextNode = nextNodes.eq(i),
            nextNodeParentKey = nextNode.attr('data-prk');
            
            if(nextNodeParentKey === nodeKey) {
                children.push(nextNode);
            }
        }
        
        return children;
    },
    
    propagateUp: function(node, select) {
        var children = this.getChildren(node),
        allSelected = true,
        partialSelected = false,
        checkboxIcon = node.find('> td:first-child > div.ui-tt-c > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon');

        for(var i = 0; i < children.length; i++) {
            var child = children[i],
            childSelected = child.hasClass('ui-state-highlight');
            
            allSelected = allSelected&childSelected;
            partialSelected = partialSelected||childSelected||child.hasClass('ui-treetable-partialselected');
        }
        
       
        if(allSelected) {
            node.removeClass('ui-treetable-partialselected');
            this.selectNode(node, true);
        }
        else if(partialSelected) {
            node.removeClass('ui-state-highlight').addClass('ui-treetable-partialselected');
            checkboxIcon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');
        }
        else {
            node.removeClass('ui-state-highlight ui-treetable-partialselected');
            checkboxIcon.removeClass('ui-icon ui-icon-check ui-icon-minus');
        }
        
        var parent = this.getParent(node);
        if(parent) {
            this.propagateUp(this.getParent(node));
        }
    },
    
    getParent: function(node) {
        var parent = $(this.jqId + '_node_' + node.attr('data-prk'));
        
        return parent.length === 1 ? parent : null;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },
    
    removeSelection: function(nodeKey) {
        this.selection = $.grep(this.selection, function(value) {
            return value != nodeKey;
        });
    },
    
    addSelection: function(nodeKey) {
        if(!this.isSelected(nodeKey)) {
            this.selection.push(nodeKey);
        }
    },
    
    isSelected: function(nodeKey) {
        var selection = this.selection,
        selected = false;

        $.each(selection, function(index, value) {
            if(value === nodeKey) {
                selected = true;

                return false;
            } 
            else {
                return true;
            }
        });

        return selected;
    },
    
    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },
    
    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple';
    },
    
    isCheckboxSelection: function() {
        return this.cfg.selectionMode == 'checkbox';
    },
    
    writeSelections: function() {
        this.jqSelection.val(this.selection.join(','));
    },
    
    fireSelectNodeEvent: function(nodeKey) {
        if(this.hasBehavior('select')) {
            var selectBehavior = this.cfg.behaviors['select'],
            ext = {
                params: [
                    {name: this.id + '_instantSelect', value: nodeKey}
                ]
            };

            selectBehavior.call(this, nodeKey, ext);
        }
    },
    
    fireUnselectNodeEvent: function(nodeKey) {
        if(this.hasBehavior('unselect')) {
            var unselectBehavior = this.cfg.behaviors['unselect'],
             ext = {
                params: [
                    {name: this.id + '_instantUnselect', value: nodeKey}
                ]
            };
            
            unselectBehavior.call(this, nodeKey, ext);
        }
    },
    
    setupScrolling: function() {
        var scrollHeader = $(this.jqId + ' .ui-treetable-scrollable-header'),
        scrollBody = $(this.jqId + ' .ui-treetable-scrollable-body'),
        scrollFooter = $(this.jqId + ' .ui-treetable-scrollable-footer');

        scrollBody.scroll(function() {
            scrollHeader.scrollLeft(scrollBody.scrollLeft());
            scrollFooter.scrollLeft(scrollBody.scrollLeft());
        });
    }
});