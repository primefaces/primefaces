/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);        
        this.thead = $(this.jqId + '_head');
        this.tbody = $(this.jqId + '_data');

        this.renderDeferred();
    },
            
    _render: function() {
        if(this.cfg.scrollable) {
            this.setupScrolling();
        }

        if(this.cfg.resizableColumns) {
            this.setupResizableColumns();
        }
        
        this.bindEvents();
    },
    
    refresh: function(cfg) {
        this.columnWidthsFixed = false;
        this.init(cfg);
    },
    
    bindEvents: function() {
        var $this = this,
        togglerSelector = '> tr > td:first-child > .ui-treetable-toggler';
        
        //expand and collapse
        this.tbody.off('click.treeTable-toggle', togglerSelector)
                    .on('click.treeTable-toggle', togglerSelector, null, function(e) {
                        var toggler = $(this),
                        node = toggler.closest('tr');
                        
                        if(!node.data('processing')) {
                            node.data('processing', true);
                            
                            if(toggler.hasClass('ui-icon-triangle-1-e'))
                                $this.expandNode(node);
                            else
                                $this.collapseNode(node);
                        }
                    });
            
        //selection
        if(this.cfg.selectionMode) {
            this.jqSelection = $(this.jqId + '_selection');
            var selectionValue = this.jqSelection.val();
            this.selections = selectionValue === "" ? [] : selectionValue.split(',');

            this.bindSelectionEvents();
        }
        
        //sorting
        this.bindSortEvents();
    },
    
    bindSelectionEvents: function() {
        var $this = this,
        rowSelector = '> tr.ui-treetable-selectable-node';
        
        this.tbody.off('mouseover.treeTable mouseout.treeTable click.treeTable', rowSelector)
                    .on('mouseover.treeTable', rowSelector, null, function(e) {
                        var element = $(this);
                        if(!element.hasClass('ui-state-highlight')) {
                            element.addClass('ui-state-hover');
                        
                            if($this.isCheckboxSelection() && !$this.cfg.nativeElements) {
                                element.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').addClass('ui-state-hover');
                            }
                        }
                    })
                    .on('mouseout.treeTable', rowSelector, null, function(e) {
                        var element = $(this);
                        if(!element.hasClass('ui-state-highlight')) {
                            element.removeClass('ui-state-hover');
                            
                            if($this.isCheckboxSelection() && !$this.cfg.nativeElements) {
                                element.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').removeClass('ui-state-hover');
                            }
                        }
                    })
                    .on('click.treeTable', rowSelector, null, function(e) {
                        $this.onRowClick(e, $(this));
                    });
                    
        if(this.isCheckboxSelection()) {
           var checkboxSelector =  this.cfg.nativeElements ? '> tr.ui-treetable-selectable-node > td:first-child :checkbox':
                    '> tr.ui-treetable-selectable-node > td:first-child div.ui-chkbox-box';
                    
                this.tbody.off('click.treeTable-checkbox', checkboxSelector)
                      .on('click.treeTable-checkbox', checkboxSelector, null, function(e) {
                          var node = $(this).closest('tr.ui-treetable-selectable-node');
                          $this.toggleCheckboxNode(node);
                      });
                      
                      
                //initial partial selected visuals
                if(this.cfg.nativeElements) {
                    this.indeterminateNodes(this.tbody.children('tr.ui-treetable-partialselected'));
                }
        }
    },
    
    bindSortEvents: function() {
        var $this = this;
        this.sortableColumns = this.thead.find('> tr > th.ui-sortable-column');
                
        this.sortableColumns.filter('.ui-state-active').each(function() {
            var columnHeader = $(this),
            sortIcon = columnHeader.children('span.ui-sortable-column-icon'),
            sortOrder = null;
            
            if(sortIcon.hasClass('ui-icon-triangle-1-n'))
                sortOrder = 'ASCENDING';
            else
                sortOrder = 'DESCENDING';
            
            columnHeader.data('sortorder', sortOrder);       
        });
        
        this.sortableColumns.on('mouseenter.treeTable', function() {
            var column = $(this);
            
            if(!column.hasClass('ui-state-active'))
                column.addClass('ui-state-hover');
        })
        .on('mouseleave.treeTable', function() {
            var column = $(this);
            
            if(!column.hasClass('ui-state-active'))
                column.removeClass('ui-state-hover');
        })
        .on('click.treeTable', function(e) {
            //Check if event target is not a clickable element in header content
            if($(e.target).is('th,span:not(.ui-c)')) {
                PrimeFaces.clearSelection();

                var columnHeader = $(this),
                sortOrder = columnHeader.data('sortorder')||'DESCENDING';

                if(sortOrder === 'ASCENDING')
                    sortOrder = 'DESCENDING';
                else if(sortOrder === 'DESCENDING')
                    sortOrder = 'ASCENDING';

                $this.sort(columnHeader, sortOrder);
            }
        });
    },
    
    sort: function(columnHeader, order) {  
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [
                {name: this.id + '_sorting', value: true},
                {name: this.id + '_sortKey', value: columnHeader.attr('id')},
                {name: this.id + '_sortDir', value: order}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.tbody.html(content);
                            
                            columnHeader.siblings().filter('.ui-state-active').removeData('sortorder').removeClass('ui-state-active')
                                            .find('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');
                            
                            columnHeader.removeClass('ui-state-hover').addClass('ui-state-active').data('sortorder', order);
                            var sortIcon = columnHeader.find('.ui-sortable-column-icon');

                            if(order === 'DESCENDING')
                                sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');
                            else if(order === 'ASCENDING')
                                sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {
                if($this.cfg.selectionMode && args.selection) {
                    $this.selections = args.selection.split(',');
                    $this.writeSelections();
                }
            }
        };
        
        if(this.hasBehavior('sort')) {
            var sortBehavior = this.cfg.behaviors['sort'];
            sortBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    expandNode: function(node) {
        var $this = this,
        nodeKey = node.attr('data-rk'),
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_expand', value: nodeKey}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            var nextRow = node.next();
                            node.after(content);
                            node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
                            node.attr('aria-expanded', true);
                            $this.indeterminateNodes($this.tbody.children('tr.ui-treetable-partialselected'));
                            
                            if(this.cfg.scrollable) {
                                this.alignScrollBody();
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                node.data('processing', false);
            }
        };
        
        if(this.hasBehavior('expand')) {
            var expandBehavior = this.cfg.behaviors['expand'];
            expandBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    collapseNode: function(node) {
        var nodeKey = node.attr('data-rk'),
        nextNodes = node.nextAll();
        
        for(var i = 0; i < nextNodes.length; i++) {
            var nextNode = nextNodes.eq(i),
            nextNodeRowKey = nextNode.attr('data-rk');
            
            if(nextNodeRowKey.indexOf(nodeKey) !== -1) {
               nextNode.remove();
            } 
            else {
                break;
            }
        }
    
        node.attr('aria-expanded', false).find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
        node.data('processing', false);
        
        if(this.cfg.scrollable) {
            this.alignScrollBody();
        }

        if(this.hasBehavior('collapse')) {
            var collapseBehavior = this.cfg.behaviors['collapse'],
            nodeKey = node.attr('data-rk');

            var ext = {
                params : [
                    {name: this.id + '_collapse', value: nodeKey}
                ]
            };

            collapseBehavior.call(this, ext);
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

                    if(this.isMultipleSelection() && shiftKey) {
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
            
    onRowRightClick: function(event, node) {
        var selected = node.hasClass('ui-state-highlight');
        
        if(this.isCheckboxSelection()) {
            if(!selected) {
                this.toggleCheckboxNode(node);
            }
        }
        else {
            if(this.isSingleSelection() || !selected ) {
                this.unselectAllNodes();
            }
            this.selectNode(node);
        }
 
        PrimeFaces.clearSelection();        
    },
    
    selectNode: function(node, silent) {
        var nodeKey = node.attr('data-rk');

        node.removeClass('ui-state-hover ui-treetable-partialselected').addClass('ui-state-highlight').attr('aria-selected', true);
        this.addToSelection(nodeKey);
        this.writeSelections();
        
        if(this.isCheckboxSelection()) {
            if(this.cfg.nativeElements)
                node.find('> td:first-child > :checkbox').prop('checked', true).prop('indeterminate', false);
            else
                node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box').removeClass('ui-state-hover').children('span.ui-chkbox-icon').removeClass('ui-icon-blank ui-icon-minus').addClass('ui-icon-check');
        }
        
        if(!silent) {
            this.fireSelectNodeEvent(nodeKey);
        }
    },
    
    unselectNode: function(node, silent) {
        var nodeKey = node.attr('data-rk');
        
        node.removeClass('ui-state-highlight ui-treetable-partialselected').attr('aria-selected', false);
        this.removeSelection(nodeKey);
        this.writeSelections();
        
        if(this.isCheckboxSelection()) {
            if(this.cfg.nativeElements)
                node.find('> td:first-child > :checkbox').prop('checked', false).prop('indeterminate', false);
            else
                node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check ui-icon-minus');
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
        
        this.selections = [];
        this.writeSelections();
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
    
    indeterminateNodes: function(nodes) {
        for(var i = 0; i < nodes.length; i++) {
            nodes.eq(i).find('> td:first-child > :checkbox').prop('indeterminate', true);
        }
    },
    
    toggleCheckboxNode: function(node) {;
        var selected = node.hasClass('ui-state-highlight'),
        rowKey = node.data('rk');
     
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
        
        if(selected) {
           this.removeDescendantsFromSelection(node.data('rk')); 
        }
        
        //propagate up
        var parentNode = this.getParent(node);
        if(parentNode) {
            this.propagateUp(parentNode);
        }
        
        this.writeSelections();
        
        if(selected)
            this.fireUnselectNodeEvent(rowKey);
        else
            this.fireSelectNodeEvent(rowKey);
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
        checkbox = this.cfg.nativeElements ? node.find('> td:first-child > :checkbox') : 
                            node.find('> td:first-child > div.ui-chkbox > div.ui-chkbox-box > span.ui-chkbox-icon');

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
            
            if(this.cfg.nativeElements)
                checkbox.prop('indeterminate', true);
            else
                checkbox.removeClass('ui-icon-blank ui-icon-check').addClass('ui-icon-minus');
    
            this.removeSelection(node.attr('data-rk'));
        }
        else {
            node.removeClass('ui-state-highlight ui-treetable-partialselected');
            
            if(this.cfg.nativeElements)
                checkbox.prop('indeterminate', false).prop('checked', false);
            else
                checkbox.addClass('ui-icon-blank').removeClass('ui-icon-check ui-icon-minus');
            
            this.removeSelection(node.attr('data-rk'));
        }
        
        var parent = this.getParent(node);
        if(parent) {
            this.propagateUp(parent);
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
            
    removeDescendantsFromSelection: function(rowKey) {
        this.selections = $.grep(this.selections, function(value) {
            return value.indexOf(rowKey + '_') !== 0;
        });
    },
    
    removeSelection: function(nodeKey) {
        this.selections = $.grep(this.selections, function(value) {
            return value !== nodeKey;
        });
    },
    
    addToSelection: function(rowKey) {
        if(!this.isSelected(rowKey)) {
            this.selections.push(rowKey);
        }
    },
    
    isSelected: function(nodeKey) {
        return PrimeFaces.inArray(this.selections, nodeKey);
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
        this.jqSelection.val(this.selections.join(','));
    },
    
    fireSelectNodeEvent: function(nodeKey) {
        if(this.isCheckboxSelection()) {
            var $this = this,
            options = {
                source: this.id,
                process: this.id
            };
            
            options.params = [
                {name: this.id + '_instantSelection', value: nodeKey}
            ];
            
            options.oncomplete = function(xhr, status, args) {
                if(args.descendantRowKeys && args.descendantRowKeys !== '') {
                    var rowKeys = args.descendantRowKeys.split(',');
                    for(var i = 0; i < rowKeys.length; i++) {
                        $this.addToSelection(rowKeys[i]);
                    }
                    $this.writeSelections();
                }
            }
            
            if(this.hasBehavior('select')) {
                var selectBehavior = this.cfg.behaviors['select'];
                selectBehavior.call(this, options);
            }
            else {
                PrimeFaces.ajax.AjaxRequest(options);
            }
        }
        else {
            if(this.hasBehavior('select')) {
                var selectBehavior = this.cfg.behaviors['select'],
                ext = {
                    params: [
                        {name: this.id + '_instantSelection', value: nodeKey}
                    ]
                };
                
                selectBehavior.call(this, ext);
            }
        }
    },
    
    fireUnselectNodeEvent: function(nodeKey) {
        if(this.hasBehavior('unselect')) {
            var unselectBehavior = this.cfg.behaviors['unselect'],
             ext = {
                params: [
                    {name: this.id + '_instantUnselection', value: nodeKey}
                ]
            };
            
            unselectBehavior.call(this, ext);
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
        this.headerCols = this.headerTable.find('> thead > tr > th');
        this.footerCols = this.footerTable.find('> tfoot > tr > td');
        var $this = this;
        
        if(this.cfg.scrollHeight) {
            if(this.cfg.scrollHeight.indexOf('%') !== -1) {
                this.adjustScrollHeight();
            }
        
            var marginRight = this.getScrollbarWidth();
            this.scrollHeaderBox.css('margin-right', marginRight);
            this.scrollFooterBox.css('margin-right', marginRight);
            this.alignScrollBody();
        }
        
        this.fixColumnWidths();
        
        if(this.cfg.scrollWidth) {
            if(this.cfg.scrollWidth.indexOf('%') !== -1) {
                this.adjustScrollWidth();
            }
            else {
                this.setScrollWidth(parseInt(this.cfg.scrollWidth));
            }
        }
        
        this.cloneHead();
        
        this.restoreScrollState();
        
        this.scrollBody.scroll(function() {
            var scrollLeft = $this.scrollBody.scrollLeft();
            $this.scrollHeaderBox.css('margin-left', -scrollLeft);
            $this.scrollFooterBox.css('margin-left', -scrollLeft);
            
            $this.saveScrollState();
        });
        
         this.scrollHeader.on('scroll.treeTable', function() {
            $this.scrollHeader.scrollLeft(0);
        });
        
        this.scrollFooter.on('scroll.treeTable', function() {    
            $this.scrollFooter.scrollLeft(0);
        });
        
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                if($this.percentageScrollHeight)
                    $this.adjustScrollHeight();
                
                if($this.percentageScrollWidth)
                    $this.adjustScrollWidth();
            }
        });
    },
            
    cloneHead: function() {
        this.theadClone = this.headerTable.children('thead').clone();
        this.theadClone.find('th').each(function() {
            var header = $(this);
            header.attr('id', header.attr('id') + '_clone');
        });
        this.theadClone.removeAttr('id').addClass('ui-treetable-scrollable-theadclone').height(0).prependTo(this.bodyTable);
    },
    
     fixColumnWidths: function() {
        var $this = this;
        
        if(!this.columnWidthsFixed) {
            if(this.cfg.scrollable) {
                this.headerCols.each(function() {
                    var headerCol = $(this),
                    colIndex = headerCol.index(),
                    width = headerCol.width();
                    
                    headerCol.width(width);                    
                    
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

    adjustScrollHeight: function() {
        var relativeHeight = this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100),
        tableHeaderHeight = this.jq.children('.ui-treetable-header').outerHeight(true),
        tableFooterHeight = this.jq.children('.ui-treetable-footer').outerHeight(true),
        scrollersHeight = (this.scrollHeader.outerHeight(true) + this.scrollFooter.outerHeight(true)),
        height = (relativeHeight - (scrollersHeight + tableHeaderHeight + tableFooterHeight));
        
        this.scrollBody.height(height);
    },
            
    adjustScrollWidth: function() {
        var width = parseInt((this.jq.parent().innerWidth() * (parseInt(this.cfg.scrollWidth) / 100)));
        this.setScrollWidth(width);
    },
            
    setOuterWidth: function(element, width) {
        var diff = element.outerWidth() - element.width();
        element.width(width - diff);
    },
            
    hasVerticalOverflow: function() {
        return (this.cfg.scrollHeight && this.bodyTable.outerHeight() > this.scrollBody.outerHeight());
    },
    
    setScrollWidth: function(width) {
        var $this = this;
        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), width);
        });
        this.scrollHeader.width(width);
        this.scrollBody.css('padding-right', 0).width(width);
        this.scrollFooter.width(width);
    },
    
    alignScrollBody: function() {
        if(!this.cfg.scrollWidth) {
            if(this.hasVerticalOverflow())
                this.scrollBody.css('padding-right', 0);
            else
                this.scrollBody.css('padding-right', this.getScrollbarWidth());
        }
    },
    
    getScrollbarWidth: function() {        
        return $.browser.webkit ? '15' : PrimeFaces.calculateScrollbarWidth();
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
                    $this.cfg.behaviors['colResize'].call($this, options);
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
                this.theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);
                this.theadClone.find(PrimeFaces.escapeClientId(nextColumnHeader.attr('id') + '_clone')).width(nextColumnWidth);

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