/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.tbody = $(this.jqId + '_data');
        this.cfg.scrollable = this.jq.hasClass('ui-treetable-scrollable');
        this.cfg.resizable = this.jq.hasClass('ui-treetable-resizable');

        if(this.cfg.scrollable) {
            this.setupScrolling();
        }
        
        if(this.cfg.resizableColumns) {
            this.setupResizableColumns();
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
            
            if(this.isCheckboxSelection()) {
                this.preselectCheckbox();
            }
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
                          $this.toggleCheckboxNode(node);
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
        nodeKey = node.attr('data-rk');

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == $this.id){
                    node.after(content);
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
            {name: this.id + '_expand', value: nodeKey}
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
            
            if(this.isCheckboxSelection()) {
                this.toggleCheckboxNode(node);
            }
            else {
                if(selected && metaKey) {
                    this.unselectNode(node);
                }
                else {
                    if(this.isSingleSelection() || (this.isMultipleSelection() && !metaKey)) {
                        this.unselectAllNodes();
                    }

                    if(this.isMultipleSelection && shiftKey) {
                        this.selectNodesInRange(node);
                    }
                    else {
                        this.selectNode(node);
                        this.cursorNode = node;
                    }
                }
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
    
    toggleCheckboxNode: function(node) {
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
        
        if(parentNode) {
            this.propagateUp(parentNode);
        }
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
        
    propagateUp: function(node) {
        var children = this.getChildren(node),
        allSelected = true,
        partialSelected = false,
        checkboxIcon = node.find('> td:first-child > div.ui-tt-c > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon');

        for(var i = 0; i < children.length; i++) {
            var child = children[i],
            childSelected = child.hasClass('ui-state-highlight');
            
            allSelected = allSelected&&childSelected;
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
    
    preselectCheckbox: function() {
        var selectedNodes = this.tbody.children('tr.ui-state-highlight');
        
        for(var i = 0; i < selectedNodes.length; i++) {
            var parent = this.getParent(selectedNodes.eq(i));
            if(parent) {
                this.propagateUp(parent);
            }
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
    },
    
    /**
     * Add resize behavior to columns
     */
    setupResizableColumns: function() {
        //Add resizers and resizer helper        
        $(this.jqId + ' thead tr th.ui-resizable-column div.ui-tt-c').prepend('<span class="ui-column-resizer">&nbsp;</span>');
        this.jq.append('<div class="ui-column-resizer-helper ui-state-highlight"></div>');

        //Variables
        var resizerHelper = $(this.jqId + ' .ui-column-resizer-helper'),
        resizers = $(this.jqId + ' thead th span.ui-column-resizer'),
        scrollHeader = $(this.jqId + ' .ui-treetable-scrollable-header'),
        scrollBody = $(this.jqId + ' .ui-treetable-scrollable-body'),
        table = $(this.jqId + ' table'),
        thead = $(this.jqId + ' thead'),  
        tfoot = $(this.jqId + ' tfoot'),
        _self = this;

        //Main resize events
        resizers.draggable({
            axis: 'x',
            start: function(event, ui) {
                var height = _self.cfg.scrollable ? scrollBody.height() : table.height() - thead.height() - 1;

                //Set height of resizer helper
                resizerHelper.height(height);
                resizerHelper.show();
            },
            drag: function(event, ui) {
                resizerHelper.offset(
                    {
                        left: ui.helper.offset().left + ui.helper.width() / 2, 
                        top: thead.offset().top + thead.height()
                    });  
            },
            stop: function(event, ui) {
                var columnHeaderWrapper = ui.helper.parent(),
                columnHeader = columnHeaderWrapper.parent(),
                minWidth = columnHeaderWrapper.data('minwidth'),
                maxWidth = columnHeaderWrapper.data('maxwidth'),
                oldPos = ui.originalPosition.left,
                newPos = ui.position.left,
                change = (newPos - oldPos),
                newWidth = (columnHeaderWrapper.width() + change - (ui.helper.width() / 2));

                ui.helper.css('left','');
                resizerHelper.hide();
                
                if(minWidth && newWidth < minWidth) {
                    newWidth = minWidth;
                } else if(maxWidth && newWidth > maxWidth) {
                    newWidth = maxWidth;
                }

                columnHeaderWrapper.width(newWidth);
                columnHeader.css('width', '');

                if(columnHeader.index() === 0) {
                    var headerPaddingLeft = parseInt(columnHeaderWrapper.css('padding-left'));
                    _self.tbody.find('> tr > td:first-child > div.ui-tt-c').each(function() {
                        var cell = $(this),
                        paddingLeft = parseInt(cell.css('padding-left'));
                        
                        cell.width(newWidth + (headerPaddingLeft - paddingLeft));
                    });
                }
                else {
                    _self.tbody.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);            
                    tfoot.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);
                }
                
                scrollHeader.scrollLeft(scrollBody.scrollLeft());

                //Sync width change with server side state
                var options = {
                    source: _self.id,
                    process: _self.id,
                    params: [
                        {name: _self.id + '_colResize', value: true},
                        {name: _self.id + '_columnId', value: columnHeader.attr('id')},
                        {name: _self.id + '_width', value: newWidth},
                        {name: _self.id + '_height', value: columnHeader.height()}
                    ]
                }
                
                if(_self.hasBehavior('colResize')) {
                    var colResizeBehavior = _self.cfg.behaviors['colResize'];
                    
                    colResizeBehavior.call(_self, event, options);
                }
                else {
                    PrimeFaces.ajax.AjaxRequest(options);
                }
                
            },
            containment: this.jq
        });
    }
});