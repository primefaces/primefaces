/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.thead = $(this.jqId + '_head');
        this.tbody = $(this.jqId + '_data');
        this.cfg.expandMode = this.cfg.expandMode||"children";

        this.renderDeferred();
    },

    _render: function() {
        if(this.cfg.scrollable) {
            this.setupScrolling();
        }

        if(this.cfg.filter) {
            this.setupFiltering();
        }

        if(this.cfg.resizableColumns) {
            this.setupResizableColumns();
        }

        if(this.cfg.stickyHeader) {
            this.setupStickyHeader();
        }

        if(this.cfg.editable) {
            this.bindEditEvents();
        }

        this.bindEvents();
    },

    //@override
    refresh: function(cfg) {
        this.columnWidthsFixed = false;
        this.scrollStateVal = this.scrollStateHolder ? this.scrollStateHolder.val() : null;

        this._super(cfg);
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
            this.cfg.disabledTextSelection = this.cfg.disabledTextSelection === false ? false : true;

            this.bindSelectionEvents();
        }

        //sorting
        this.bindSortEvents();

        if(this.cfg.paginator) {
            this.cfg.paginator.paginate = function(newState) {
                $this.handlePagination(newState);
            };

            this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
        }
    },

    /**
     * Binds filter events to standard filters
     */
    setupFiltering: function() {
        var $this = this,
        filterColumns = this.thead.find('> tr > th.ui-filter-column');
        this.cfg.filterEvent = this.cfg.filterEvent||'keyup';
        this.cfg.filterDelay = this.cfg.filterDelay||300;

        filterColumns.children('.ui-column-filter').each(function() {
            var filter = $(this);

            if(filter.is('input:text')) {
                PrimeFaces.skinInput(filter);
                $this.bindTextFilter(filter);
            }
            else {
                PrimeFaces.skinSelect(filter);
                $this.bindChangeFilter(filter);
            }
        });
    },

    /**
     * Clears table filters
     */
    clearFilters: function() {
        columnFilters = this.thead.find('> tr > th.ui-filter-column > .ui-column-filter').val('');
        $(this.jqId + '\\:globalFilter').val('');
        this.filter();
    },

    bindTextFilter: function(filter) {
        if(this.cfg.filterEvent === 'enter')
            this.bindEnterKeyFilter(filter);
        else
            this.bindFilterEvent(filter);
    },

    bindChangeFilter: function(filter) {
        var $this = this;

        filter.change(function() {
            $this.filter();
        });
    },

    bindEnterKeyFilter: function(filter) {
        var $this = this;

        filter.on('keydown', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if(key === keyCode.ENTER) {
                e.preventDefault();
            }
        }).on('keyup', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if(key === keyCode.ENTER) {
                $this.filter();

                e.preventDefault();
            }
        });
    },

    bindFilterEvent: function(filter) {
        var $this = this;

        //prevent form submit on enter key
        filter.on('keydown.treeTable-blockenter', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if(key === keyCode.ENTER) {
                e.preventDefault();
            }
        })
        .on(this.cfg.filterEvent + '.treeTable', function(e) {
            if($this.filterTimeout) {
                clearTimeout($this.filterTimeout);
            }

            $this.filterTimeout = setTimeout(function() {
                $this.filter();
                $this.filterTimeout = null;
            },
            $this.cfg.filterDelay);
        });
    },

    /**
     * Ajax filter
     */
    filter: function() {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_filtering', value: true},
                     {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.tbody.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                var paginator = $this.getPaginator();
                if(args && args.totalRecords) {
                    if(paginator) {
                        paginator.setTotalRecords(args.totalRecords);
                    }
                }
            }
        };

        if(this.hasBehavior('filter')) {
            this.callBehavior('filter', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    handlePagination: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [
                {name: this.id + '_pagination', value: true},
                {name: this.id + '_first', value: newState.first},
                {name: this.id + '_rows', value: newState.rows}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.tbody.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
        };

        if(this.hasBehavior('page')) {
            this.callBehavior('page', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    getPaginator: function() {
        return this.paginator;
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

    bindContextMenu : function(menuWidget, targetWidget, targetId, cfg) {
        var targetSelector = targetId + ' .ui-treetable-data > ' + (cfg.nodeType ? 'tr.ui-treetable-selectable-node.' + cfg.nodeType : 'tr.ui-treetable-selectable-node');
        var targetEvent = cfg.event + '.treetable';

        $(document).off(targetEvent, targetSelector).on(targetEvent, targetSelector, null, function(e) {
            targetWidget.onRowRightClick(e, $(this));
            menuWidget.show(e);
        });
    },

    setupStickyHeader: function() {
        var table = this.thead.parent(),
        offset = table.offset(),
        win = $(window),
        $this = this;

        this.stickyContainer = $('<div class="ui-treetable ui-treetable-sticky ui-widget"><table></table></div>');
        this.clone = this.thead.clone(false);
        this.stickyContainer.children('table').append(this.thead);
        table.append(this.clone);

        this.stickyContainer.css({
            position: 'absolute',
            width: table.outerWidth(),
            top: offset.top,
            left: offset.left,
            'z-index': ++PrimeFaces.zindex
        });

        this.jq.prepend(this.stickyContainer);

        if(this.cfg.resizableColumns) {
            this.relativeHeight = 0;
        }

        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_align', function() {
            var scrollTop = win.scrollTop(),
            tableOffset = table.offset();

            if(scrollTop > tableOffset.top) {
                $this.stickyContainer.css({
                                        'position': 'fixed',
                                        'top': '0px'
                                    })
                                    .addClass('ui-shadow ui-sticky');

                if($this.cfg.resizableColumns) {
                    $this.relativeHeight = scrollTop - tableOffset.top;
                }

                if(scrollTop >= (tableOffset.top + $this.tbody.height()))
                    $this.stickyContainer.hide();
                else
                    $this.stickyContainer.show();
            }
            else {
                $this.stickyContainer.css({
                                        'position': 'absolute',
                                        'top': tableOffset.top
                                    })
                                    .removeClass('ui-shadow ui-sticky');

                if($this.stickyContainer.is(':hidden')) {
                    $this.stickyContainer.show();
                }

                if($this.cfg.resizableColumns) {
                    $this.relativeHeight = 0;
                }
            }
        });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.sticky-' + this.id + '_align', null, function() {
            $this.stickyContainer.width(table.outerWidth());
        });
    },

    bindEditEvents: function() {
        var $this = this;
        this.cfg.cellSeparator = this.cfg.cellSeparator||' ';

        if(this.cfg.editMode === 'row') {
            var rowEditorSelector = '> tr > td > div.ui-row-editor';
            this.tbody.off('click.treetable', rowEditorSelector)
                        .on('click.treetable', rowEditorSelector, null, function(e) {
                            var element = $(e.target),
                            row = element.closest('tr');

                            if(element.hasClass('ui-icon-pencil')) {
                                $this.switchToRowEdit(row);
                                element.hide().siblings().show();
                            }
                            else if(element.hasClass('ui-icon-check')) {
                                $this.saveRowEdit(row);
                            }
                            else if(element.hasClass('ui-icon-close')) {
                                $this.cancelRowEdit(row);
                            }

                            e.preventDefault();
                        });
        }
        else if(this.cfg.editMode === 'cell') {
            var cellSelector = '> tr > td.ui-editable-column';

            this.tbody.off('click.treetable-cell', cellSelector)
                        .on('click.treetable-cell', cellSelector, null, function(e) {
                            if(!$(e.target).is('span.ui-treetable-toggler.ui-c')) {
                                $this.incellClick = true;

                                var cell = $(this);
                                if(!cell.hasClass('ui-cell-editing')) {
                                    $this.showCellEditor($(this));
                                }
                            }
                        });

            $(document).off('click.treetable-cell-blur' + this.id)
                        .on('click.treetable-cell-blur' + this.id, function(e) {
                            if((!$this.incellClick && $this.currentCell && !$this.contextMenuClick)) {
                                $this.saveCell($this.currentCell);
                            }

                            $this.incellClick = false;
                            $this.contextMenuClick = false;
                        });
        }
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
            oncomplete: function(xhr, status, args, data) {
                if($this.cfg.selectionMode && args.selection) {
                    $this.selections = args.selection.split(',');
                    $this.writeSelections();
                }
            }
        };

        if(this.hasBehavior('sort')) {
            this.callBehavior('sort', options);
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
                            if($this.cfg.expandMode === "self")
                                node.replaceWith(content);
                            else
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
                $this.updateVerticalScroll();
            }
        };

        if(this.hasBehavior('expand')) {
            this.callBehavior('expand', options);
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
            var $this = this,
            nodeKey = node.attr('data-rk'),
            options = {
                source: this.id,
                process: this.id,
                update: this.id,
                params: [
                    {name: this.id + '_collapse', value: nodeKey}
                ],
                onsuccess: function(responseXML, status, xhr) {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                            widget: $this,
                            handle: function(content) {
                                // do nothing
                            }
                        });

                    return true;
                },
                oncomplete: function() {
                    $this.updateVerticalScroll();
                }
            };

            this.callBehavior('collapse', options);
        }
        else {
            this.updateVerticalScroll();
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

            if(this.cfg.disabledTextSelection) {
                PrimeFaces.clearSelection();
            }
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

        if(this.cfg.disabledTextSelection) {
            PrimeFaces.clearSelection();
        }
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

    toggleCheckboxNode: function(node) {
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

            options.oncomplete = function(xhr, status, args, data) {
                if(args.descendantRowKeys && args.descendantRowKeys !== '') {
                    var rowKeys = args.descendantRowKeys.split(',');
                    for(var i = 0; i < rowKeys.length; i++) {
                        $this.addToSelection(rowKeys[i]);
                    }
                    $this.writeSelections();
                }
            }

            if(this.hasBehavior('select')) {
                this.callBehavior('select', options);
            }
            else {
                PrimeFaces.ajax.Request.handle(options);
            }
        }
        else {
            if(this.hasBehavior('select')) {
                var ext = {
                    params: [
                        {name: this.id + '_instantSelection', value: nodeKey}
                    ]
                };

                this.callBehavior('select', ext);
            }
        }
    },

    fireUnselectNodeEvent: function(nodeKey) {
        if(this.hasBehavior('unselect')) {
            var ext = {
                params: [
                    {name: this.id + '_instantUnselection', value: nodeKey}
                ]
            };

            this.callBehavior('unselect', ext);
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
        this.percentageScrollHeight = this.cfg.scrollHeight && (this.cfg.scrollHeight.indexOf('%') !== -1);
        this.percentageScrollWidth = this.cfg.scrollWidth && (this.cfg.scrollWidth.indexOf('%') !== -1);
        var $this = this;

        if(this.cfg.scrollHeight) {
            if(this.cfg.scrollHeight.indexOf('%') !== -1) {
                this.adjustScrollHeight();
            }

            if(this.cfg.scrollHeight.indexOf('vh') !== -1)  {
                this.applyViewPortScrollHeight();
            }

            this.marginRight = this.getScrollbarWidth() + 'px';
            this.scrollHeaderBox.css('margin-right', this.marginRight);
            this.scrollFooterBox.css('margin-right', this.marginRight);
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

        this.updateVerticalScroll();

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

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.jq, function() {
            if ($this.percentageScrollHeight) {
                $this.adjustScrollHeight();
            }
            if ($this.percentageScrollWidth) {
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

    updateColumnWidths: function() {
        this.columnWidthsFixed = false;
        this.jq.find('> table > thead > tr > th').each(function() {
            var col = $(this);
            col.css('width', '');
        });
        this.fixColumnWidths();
    },

    adjustScrollHeight: function() {
        var relativeHeight = this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100),
        tableHeaderHeight = this.jq.children('.ui-treetable-header').outerHeight(true),
        tableFooterHeight = this.jq.children('.ui-treetable-footer').outerHeight(true),
        scrollersHeight = (this.scrollHeader.outerHeight(true) + this.scrollFooter.outerHeight(true)),
        height = (relativeHeight - (scrollersHeight + tableHeaderHeight + tableFooterHeight));

        this.scrollBody.height(height);
    },

    applyViewPortScrollHeight: function() {
        this.scrollBody.height(this.cfg.scrollHeight);
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
        var scrollState = this.scrollStateVal||this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        this.scrollBody.scrollLeft(scrollValues[0]);
        this.scrollBody.scrollTop(scrollValues[1]);
        this.scrollStateVal = null;
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
                    var header = $this.cfg.stickyHeader ? $this.clone : $this.thead,
                        height = $this.cfg.scrollable ? $this.scrollBody.height() : header.parent().height() - header.height() - 1;

                    if($this.cfg.stickyHeader) {
                        height = height - $this.relativeHeight;
                    }

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
                        {name: $this.id + '_width', value: parseInt(columnHeader.width())},
                        {name: $this.id + '_height', value: parseInt(columnHeader.height())}
                    ]
                };

                if($this.hasBehavior('colResize')) {
                    $this.callBehavior('colResize', options);
                }

                if($this.cfg.stickyHeader) {
                    $this.reclone();
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
    },

    reclone: function() {
        this.clone.remove();
        this.clone = this.thead.clone(false);
        this.jq.children('table').append(this.clone);
    },

    switchToRowEdit: function(row) {
        this.showRowEditors(row);

        if(this.hasBehavior('rowEditInit')) {
            var rowIndex = row.data('rk');

            var ext = {
                params: [{name: this.id + '_rowEditIndex', value: rowIndex}]
            };

            this.callBehavior('rowEditInit', ext);
        }
    },

    showRowEditors: function(row) {
        row.addClass('ui-state-highlight ui-row-editing').children('td.ui-editable-column').each(function() {
            var column = $(this);

            column.find('.ui-cell-editor-output').hide();
            column.find('.ui-cell-editor-input').show();
        });
    },

    /**
     * Saves the edited row
     */
    saveRowEdit: function(rowEditor) {
        this.doRowEditRequest(rowEditor, 'save');
    },

    /**
     * Cancels row editing
     */
    cancelRowEdit: function(rowEditor) {
        this.doRowEditRequest(rowEditor, 'cancel');
    },

    /**
     * Sends an ajax request to handle row save or cancel
     */
    doRowEditRequest: function(rowEditor, action) {
        var row = rowEditor.closest('tr'),
        rowIndex = row.data('rk'),
        expanded = row.hasClass('ui-expanded-row'),
        $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_rowEditIndex', value: rowIndex},
                     {name: this.id + '_rowEditAction', value: action}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            if(expanded) {
                                this.collapseRow(row);
                            }

                            this.updateRows(row, content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                if(args && args.validationFailed) {
                    $this.invalidateRow(rowIndex);
                }
            }
        };

        if(action === 'save') {
            this.getRowEditors(row).each(function() {
                options.params.push({name: this.id, value: this.id});
            });
        }

        if(action === 'save' && this.hasBehavior('rowEdit')) {
            this.callBehavior('rowEdit', options);
        }
        else if(action === 'cancel' && this.hasBehavior('rowEditCancel')) {
            this.callBehavior('rowEditCancel', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /*
     * Updates rows with given content
     */
    updateRows: function(row, content) {
        this.tbody.children('tr').filter('[data-prk^="'+ row.data('rk') +'"]').remove();
        row.replaceWith(content);
    },

    /**
     * Displays row editors in invalid format
     */
    invalidateRow: function(index) {
        this.tbody.children('tr').eq(index).addClass('ui-widget-content ui-row-editing ui-state-error');
    },

    /**
     * Finds all editors of a row
     */
    getRowEditors: function(row) {
        return row.find('div.ui-cell-editor');
    },

    collapseRow: function(row) {
        row.removeClass('ui-expanded-row').next('.ui-expanded-row-content').remove();
    },

    showCellEditor: function(c) {
        this.incellClick = true;

        var cell = null;

        if(c) {
            cell = c;

            //remove contextmenu selection highlight
            if(this.contextMenuCell) {
                this.contextMenuCell.parent().removeClass('ui-state-highlight');
            }
        }
        else {
            cell = this.contextMenuCell;
        }

        var editorInput = cell.find('> .ui-cell-editor > .ui-cell-editor-input');
        if(editorInput.length !== 0 && editorInput.children().length === 0 && this.cfg.editMode === 'cell') {
            // for lazy cellEditMode
            this.cellEditInit(cell);
        }
        else {
            this.showCurrentCell(cell);
        }
    },

    showCurrentCell: function(cell) {
        var $this = this;

        if(this.currentCell) {
            $this.saveCell(this.currentCell);
        }

        this.currentCell = cell;

        var cellEditor = cell.children('div.ui-cell-editor'),
        displayContainer = cellEditor.children('div.ui-cell-editor-output'),
        inputContainer = cellEditor.children('div.ui-cell-editor-input'),
        inputs = inputContainer.find(':input:enabled'),
        multi = inputs.length > 1;

        cell.addClass('ui-state-highlight ui-cell-editing');
        displayContainer.hide();
        inputContainer.show();
        inputs.eq(0).focus().select();

        //metadata
        if(multi) {
            var oldValues = [];
            for(var i = 0; i < inputs.length; i++) {
                oldValues.push(inputs.eq(i).val());
            }

            cell.data('multi-edit', true);
            cell.data('old-value', oldValues);
        }
        else {
            cell.data('multi-edit', false);
            cell.data('old-value', inputs.eq(0).val());
        }

        //bind events on demand
        if(!cell.data('edit-events-bound')) {
            cell.data('edit-events-bound', true);

            inputs.on('keydown.treetable-cell', function(e) {
                    var keyCode = $.ui.keyCode,
                    shiftKey = e.shiftKey,
                    key = e.which,
                    input = $(this);

                    if(key === keyCode.ENTER) {
                        $this.saveCell(cell);

                        e.preventDefault();
                    }
                    else if(key === keyCode.TAB) {
                        if(multi) {
                            var focusIndex = shiftKey ? input.index() - 1 : input.index() + 1;

                            if(focusIndex < 0 || (focusIndex === inputs.length)) {
                                $this.tabCell(cell, !shiftKey);
                            } else {
                                inputs.eq(focusIndex).focus();
                            }
                        }
                        else {
                            $this.tabCell(cell, !shiftKey);
                        }

                        e.preventDefault();
                    }
                    else if(key === keyCode.ESCAPE) {
                        $this.doCellEditCancelRequest(cell);
                        e.preventDefault();
                    }
                })
                .on('focus.treetable-cell click.treetable-cell', function(e) {
                    $this.currentCell = cell;
                });
        }
    },

    tabCell: function(cell, forward) {
        var targetCell = forward ? cell.nextAll('td.ui-editable-column:first') : cell.prevAll('td.ui-editable-column:first');
        if(targetCell.length == 0) {
            var tabRow = forward ? cell.parent().next() : cell.parent().prev();
            targetCell = forward ? tabRow.children('td.ui-editable-column:first') : tabRow.children('td.ui-editable-column:last');
        }

        this.showCellEditor(targetCell);
    },

    saveCell: function(cell) {
        var inputs = cell.find('div.ui-cell-editor-input :input:enabled'),
        changed = false,
        $this = this;

        if(cell.data('multi-edit')) {
            var oldValues = cell.data('old-value');
            for(var i = 0; i < inputs.length; i++) {
                if(inputs.eq(i).val() != oldValues[i]) {
                    changed = true;
                    break;
                }
            }
        }
        else {
            changed = (inputs.eq(0).val() != cell.data('old-value'));
        }

        if(changed)
            $this.doCellEditRequest(cell);
        else
            $this.viewMode(cell);

        this.currentCell = null;
    },

    viewMode: function(cell) {
        var cellEditor = cell.children('div.ui-cell-editor'),
        editableContainer = cellEditor.children('div.ui-cell-editor-input'),
        displayContainer = cellEditor.children('div.ui-cell-editor-output');

        cell.removeClass('ui-cell-editing ui-state-error ui-state-highlight');
        displayContainer.show();
        editableContainer.hide();
        cell.removeData('old-value').removeData('multi-edit');

        if(this.cfg.cellEditMode === "lazy") {
            editableContainer.children().remove();
        }
    },

    doCellEditRequest: function(cell) {
        var cellEditor = cell.children('.ui-cell-editor'),
        cellEditorId = cellEditor.attr('id'),
        cellIndex = cell.index(),
        cellInfo = cell.closest('tr').data('rk') + ',' + cellIndex,
        $this = this;

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [{name: this.id + '_cellInfo', value: cellInfo},
                     {name: cellEditorId, value: cellEditorId}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            cellEditor.children('.ui-cell-editor-output').html(content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                if(args.validationFailed)
                    cell.addClass('ui-state-error');
                else
                    $this.viewMode(cell);
            }
        };

        if(this.hasBehavior('cellEdit')) {
            this.callBehavior('cellEdit', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    doCellEditCancelRequest: function(cell) {
        var cellEditor = cell.children('.ui-cell-editor'),
        cellIndex = cell.index(),
        cellInfo = cell.closest('tr').data('rk') + ',' + cellIndex,
        $this = this;

        this.currentCell = null;

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [{name: this.id + '_cellEditCancel', value: true},
                     {name: this.id + '_cellInfo', value: cellInfo}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            cellEditor.children('.ui-cell-editor-input').html(content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                $this.viewMode(cell);
                cell.data('edit-events-bound', false);
            }
        };

        if(this.hasBehavior('cellEditCancel')) {
            this.callBehavior('cellEditCancel', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    cellEditInit: function(cell) {
        var cellEditor = cell.children('.ui-cell-editor'),
        cellIndex = cell.index(),
        cellInfo = cell.closest('tr').data('rk') + ',' + cellIndex,
        $this = this;

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            global: false,
            params: [{name: this.id + '_cellEditInit', value: true},
                     {name: this.id + '_cellInfo', value: cellInfo}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            cellEditor.children('.ui-cell-editor-input').html(content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                cell.data('edit-events-bound', false);
                $this.showCurrentCell(cell);
            }
        };

        if(this.hasBehavior('cellEditInit')) {
            this.callBehavior('cellEditInit', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    updateVerticalScroll: function() {
        if(this.cfg.scrollable && this.cfg.scrollHeight) {
            if(this.bodyTable.outerHeight() < this.scrollBody.outerHeight()) {
                this.scrollHeaderBox.css('margin-right', 0);
                this.scrollFooterBox.css('margin-right', 0);
            }
            else {
                this.scrollHeaderBox.css('margin-right', this.marginRight);
                this.scrollFooterBox.css('margin-right', this.marginRight);
            }
        }
    }
});