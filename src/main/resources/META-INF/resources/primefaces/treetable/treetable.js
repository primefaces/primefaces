/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.propagateUp = this.cfg.propagateUp === false ? false : true;
        this.cfg.propagateDown = this.cfg.propagateDown === false ? false : true;
        
        this.thead = $(this.jqId + '_head');
        this.tbody = $(this.jqId + '_data');

        var $this = this;
        if(this.jq.is(':visible')) {
            this.setupDimensionalConfig();
        }
        else {
            var hiddenParent = this.jq.parents('.ui-hidden-container:first'),
            hiddenParentWidget = hiddenParent.data('widget');

            if(hiddenParentWidget) {
                hiddenParentWidget.addOnshowHandler(function() {
                    return $this.setupDimensionalConfig();
                });
            }
        }
        
        this.bindEvents();
    },
    
    refresh: function(cfg) {
        this.columnWidthsFixed = false;
        this.init(cfg);
    },
    
    setupDimensionalConfig: function() {
        if(this.jq.is(':visible')) {
            if(this.cfg.scrollable) {
                this.setupScrolling();
            }
        
            if(this.cfg.resizableColumns) {
                this.setupResizableColumns();
            }
            
            return true;
        } 
        else {
            return false;
        }
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
                                element.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').addClass('ui-state-hover');
                            }
                        }
                    })
                    .on('mouseout.treeTable', rowSelector, null, function(e) {
                        var element = $(this);
                        if(!element.hasClass('ui-state-highlight')) {
                            element.removeClass('ui-state-hover');
                            
                            if($this.isCheckboxSelection()) {
                                element.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').removeClass('ui-state-hover');
                            }
                        }
                    })
                    .on('click.treeTable', rowSelector, null, function(e) {
                        $this.onRowClick(e, $(this));
                        e.preventDefault();
                    });
                    
        if(this.isCheckboxSelection()) {
           var checkboxSelector = this.jqId + ' .ui-treetable-data tr.ui-treetable-selectable-node td:first-child div.ui-chkbox-box';
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
    
        node.attr('aria-expanded', false).find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');

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
        if($(event.target).is('td,span:not(.ui-c)')) {
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
                    if(this.isSingleSelection()||(this.isMultipleSelection() && !metaKey)) {
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
            node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').removeClass('ui-state-hover')
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
            node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon').removeClass('ui-icon ui-icon-check ui-icon-minus');
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
            this.unselectNode(node);
        else
            this.selectNode(node);
        
        if(this.cfg.propagateDown) {
            var descendants = this.getDescendants(node);
            for(var i = 0; i < descendants.length; i++) {
                var descendant = descendants[i];

                if(selected)
                    this.unselectNode(descendant, true);
                else
                    this.selectNode(descendant, true);
            }
        }
        
        if(this.cfg.propagateUp) {
            if(parentNode) {
                this.propagateUp(parentNode);
            }
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
        checkboxIcon = node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon');

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
        this.scrollHeader = this.jq.children('div.ui-treetable-scrollable-header');
        this.scrollBody = this.jq.children('div.ui-treetable-scrollable-body');
        this.scrollFooter = this.jq.children('div.ui-treetable-scrollable-footer');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        this.scrollHeaderBox = this.scrollHeader.children('div.ui-treetable-scrollable-header-box');
        this.scrollFooterBox = this.scrollFooter.children('div.ui-treetable-scrollable-footer-box');
        this.headerTable = this.scrollHeaderBox.children('table');
        this.bodyTable = this.scrollBody.children('table');
        this.footerTable = this.scrollFooterBox.children('table');
        this.colgroup = this.bodyTable.children('colgroup');
        this.headerCols = this.headerTable.find('> thead > tr > th');
        this.footerCols = this.footerTable.find('> tfoot > tr > td');
        
        if(this.cfg.scrollHeight) {
            if(this.cfg.scrollHeight.indexOf('%') != -1) {
                var height = (this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100)) - (this.scrollHeader.innerHeight() + this.scrollFooter.innerHeight());
                this.scrollBody.height(parseInt(height));
            }
        }
        
        var $this = this;
        
        var marginRight = $.browser.webkit ? '15px' : PrimeFaces.calculateScrollbarWidth();
        this.scrollHeaderBox.css('margin-right', marginRight);
        this.scrollBody.css('padding-right', marginRight);
        this.scrollFooterBox.css('margin-right', marginRight);
        
        this.fixColumnWidths();
        
        if(this.cfg.scrollWidth) {
            var swidth = this.cfg.scrollWidth;
            if(this.cfg.scrollWidth.indexOf('%') != -1) {
                swidth = parseInt((this.jq.parent().innerWidth() * (parseInt(this.cfg.scrollWidth) / 100)));
            }
            
            this.scrollBody.css('padding-right', 0);
            this.scrollHeader.width(swidth);
            this.scrollBody.width(swidth);
            this.scrollFooter.width(swidth);
        }
        
        this.restoreScrollState();
        
        this.scrollBody.scroll(function() {
            var scrollLeft = $this.scrollBody.scrollLeft();
            $this.scrollHeaderBox.css('margin-left', -scrollLeft);
            $this.scrollFooterBox.css('margin-left', -scrollLeft);
            
            $this.saveScrollState();
        });
    },
    
    fixColumnWidths: function() {
        var $this = this;
        
        if(!this.columnWidthsFixed) {
            if(this.cfg.scrollable) {
                this.headerCols.each(function() {
                    var headerCol = $(this),
                    colIndex = headerCol.index(),
                    width = headerCol.width(),
                    innerWidth = headerCol.innerWidth();
                    
                    headerCol.width(width);                    
                    $this.colgroup.children().eq(colIndex).width(innerWidth + 1);
                    if($this.footerCols.length > 0) {
                        var footerCol = $this.footerCols.eq(colIndex);
                        footerCol.width(width);
                    }
                });
            }
            else {
                this.jq.find('> table > thead > tr > th').each(function() {
                    var col = $(this);
                    col.width(col.width());
                });
            }
            
            this.columnWidthsFixed = true;
        }
    },

    restoreScrollState: function() {
        var scrollState = this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        this.scrollBody.scrollLeft(scrollValues[0]);
        this.scrollBody.scrollTop(scrollValues[1]);
    },
    
    saveScrollState: function() {
        var scrollState = this.scrollBody.scrollLeft() + ',' + this.scrollBody.scrollTop();
        
        this.scrollStateHolder.val(scrollState);
    },
    
    setupResizableColumns: function() {
        this.fixColumnWidths();
        
        if(!this.cfg.liveResize) {
            this.resizerHelper = $('<div class="ui-column-resizer-helper ui-state-highlight"></div>').appendTo(this.jq);
        }
        
        this.thead.find('> tr > th.ui-resizable-column:not(:last-child)').prepend('<span class="ui-column-resizer">&nbsp;</span>');        
        var resizers = this.thead.find('> tr > th > span.ui-column-resizer'),
        $this = this;
            
        resizers.draggable({
            axis: 'x',
            start: function() {
                if($this.cfg.liveResize) {
                    $this.jq.css('cursor', 'col-resize');
                }
                else {
                    var height = $this.cfg.scrollable ? $this.scrollBody.height() : $this.thead.parent().height() - $this.thead.height() - 1;
                    $this.resizerHelper.height(height);
                    $this.resizerHelper.show();
                }
            },
            drag: function(event, ui) {
                if($this.cfg.liveResize) {
                    $this.resize(event, ui);
                }
                else {
                    $this.resizerHelper.offset({
                        left: ui.helper.offset().left + ui.helper.width() / 2, 
                        top: $this.thead.offset().top + $this.thead.height()
                    });  
                }                
            },
            stop: function(event, ui) {
                var columnHeader = ui.helper.parent();
                ui.helper.css('left','');
                
                if($this.cfg.liveResize) {
                    $this.jq.css('cursor', 'default');
                } else {
                    $this.resize(event, ui);
                    $this.resizerHelper.hide();
                }
                
                var options = {
                    source: $this.id,
                    process: $this.id,
                    params: [
                        {name: $this.id + '_colResize', value: true},
                        {name: $this.id + '_columnId', value: columnHeader.attr('id')},
                        {name: $this.id + '_width', value: columnHeader.width()},
                        {name: $this.id + '_height', value: columnHeader.height()}
                    ]
                }
                
                if($this.hasBehavior('colResize')) {
                    $this.cfg.behaviors['colResize'].call($this, event, options);
                }
            },
            containment: this.jq
        });
    },
    
    resize: function(event, ui) {
        var columnHeader = ui.helper.parent(),
        nextColumnHeader = columnHeader.next(),
        change = null, newWidth = null, nextColumnWidth = null;
        
        if(this.cfg.liveResize) {
            change = columnHeader.outerWidth() - (event.pageX - columnHeader.offset().left),
            newWidth = (columnHeader.width() - change),
            nextColumnWidth = (nextColumnHeader.width() + change);
        } 
        else {
            change = (ui.position.left - ui.originalPosition.left),
            newWidth = (columnHeader.width() + change),
            nextColumnWidth = (nextColumnHeader.width() - change);
        }
        
        if(newWidth > 15 && nextColumnWidth > 15) {
            columnHeader.width(newWidth);
            nextColumnHeader.width(nextColumnWidth);
            var colIndex = columnHeader.index();

            if(this.cfg.scrollable) {
                var padding = columnHeader.innerWidth() - columnHeader.width();
                this.colgroup.children().eq(colIndex).width(newWidth + padding + 1);
                this.colgroup.children().eq(colIndex + 1).width(nextColumnWidth + padding + 1);

                if(this.footerCols.length > 0) {
                    var footerCol = this.footerCols.eq(colIndex),
                    nextFooterCol = footerCol.next();

                    footerCol.width(newWidth);
                    nextFooterCol.width(nextColumnWidth);
                }
            }
        }
    }
});