/**
 * __PrimeFaces TreeTable Widget__
 * 
 * TreeTable is is used for displaying hierarchical data in tabular format.
 * 
 * @typedef {"single" | "multiple" | "checkbox"} PrimeFaces.widget.TreeTable.SelectionMode Indicates how a row may be
 * selected.
 * - `single`: Only a single row may be selected at any time by clicking on it. Selecting another row will unselect the
 * currently selected row.
 * - `multiple`: Multiple rows can be selected via clicking while holding the ctrl or shift key. 
 * - `checkbox`: One or more rows can be selected by clicking on the checkbox next to each row.
 * 
 * @typedef {"ASCENDING" | "DESCENDING"} PrimeFaces.widget.TreeTable.SortOrder The order how the rows of a tree table
 * are sorted.
 * - `ASCENDING`: The rows are ordered in an ascending order, from the first to the last item.
 * - `DESCENDING`: The rows are ordered in an ascending order, from the last to the first item.
 * 
 * @typedef {"eager" | "lazy"} PrimeFaces.widget.TreeTable.CellEditMode If cell editing mode is enabled, whether the
 * cell editors are loaded lazily.
 * - `eager`: Cell editors are loaded with the original page load or when the tree table is loaded.
 * - `lazy`: Cell editors are loaded via AJAX when inline editing is requested.
 * 
 * @typedef {"row" | "cell"} PrimeFaces.widget.TreeTable.EditMode How the data in a tree table can be edited.
 * - `row`: A row is switched to edit mode and all cells can be edited at once.
 * - `cell`: An individual cell is switched to edit mode and can be edited. 
 * 
 * @typedef {"children" | "self"} PrimeFaces.widget.TreeTable.ExpandMode Defines which rows are expanded when the expand
 * icon next to a row is clicked.
 * - `self`: Only the row itself is expanded.
 * - `children`: The row and its children are expanded.

 * @implements {PrimeFaces.widget.ContextMenu.ContextMenuProvider<PrimeFaces.widget.TreeTable>}
 * 
 * @prop {JQuery} bodyTable The DOM element for the main TABLE element.
 * @prop {JQuery} clone The DOM element for the  clone of the table head.
 * @prop {boolean} columnWidthsFixed Whether the width of all columns needs to stay fixed.
 * @prop {boolean} contextMenuClick Whether a click was performed on the context menu.
 * @prop {JQuery} currentCell The DOM element for the currently selected cell, when using inline editing.
 * @prop {JQuery} cursorNode The DOM element for the row at the cursor position, used for selecting multiple rows when
 * holding the shift key.
 * @prop {number} filterTimeout The set-timeout timer ID for the timer used for the delay during filtering.
 * @prop {JQuery} footerCols The DOM element for the TD columns in the table footer.
 * @prop {JQuery} footerTable The DOM element for the TABLE element of the footer.
 * @prop {JQuery} headerCols The DOM element for the TH columns in the header.
 * @prop {JQuery} headerTable The DOM element for the TABLE element of the header.
 * @prop {boolean} incellClick Whether a click was performed inside a cell.
 * @prop {JQuery} jqSelection The DOM element for the hidden input storing the selected rows. 
 * @prop {string} marginRight CSS unit for the right margin of this tree table, determined from the scrollbar width.
 * @prop {PrimeFaces.widget.Paginator} paginator The paginator widget instance used for filtering.
 * @prop {boolean} percentageScrollHeight Whether the scroll height was specified in percent.
 * @prop {boolean} percentageScrollWidth Whether the scroll width was specified in percent.
 * @prop {number} relativeHeight The height of the visible scroll area relative to the total height of this tree table.
 * @prop {JQuery} resizerHelper The DOM element for the draggable handle for resizing columns. 
 * @prop {JQuery} scrollBody The DOM element for the scrollable DIV with the body table.
 * @prop {JQuery} scrollFooter The DOM element for the scrollable DIV with the footer table.
 * @prop {JQuery} scrollFooterBox The DOM element for the container DIV of the footer table.
 * @prop {JQuery} scrollHeader The DOM element for the scrollable DIV with the header table.
 * @prop {JQuery} scrollHeaderBox The DOM element for the container DIV of the header table. 
 * @prop {JQuery} scrollStateHolder The DOM element for the hidden input storing the current scroll position.  
 * @prop {string} scrollStateVal The value of the {@link scrollStateHolder}.
 * @prop {string[]} selections A list of row keys of the currently selected rows.
 * @prop {JQuery} sortableColumns The DOM elements for the list of sortable columns. 
 * @prop {JQuery} stickyContainer The DOM element for the container with the sticky header.
 * @prop {JQuery} tbody The DOM element for the table body of this tree table.
 * @prop {JQuery} thead The DOM element for the table header of this tree table.
 * @prop {JQuery} theadClone The DOM element for the clone of the table header.
 * 
 * @interface {PrimeFaces.widget.TreeTableCfg} cfg The configuration for the {@link  TreeTable| TreeTable widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * 
 * @prop {PrimeFaces.widget.TreeTable.CellEditMode} cfg.cellEditMode Whether cell editors are loaded lazily.
 * @prop {string} cfg.cellSeparator Separator text to use in output mode of editable cells with multiple components.
 * @prop {boolean} cfg.disabledTextSelection Disables text selection on row click.
 * @prop {PrimeFaces.widget.TreeTable.EditMode} cfg.editMode If editing is enables and whether entire rows or individual
 * cells can be edited.
 * @prop {boolean} cfg.editable Whether data in this data table can be edited.
 * @prop {string} cfg.event Prefix of the event namespace used by the tree table.
 * @prop {PrimeFaces.widget.TreeTable.ExpandMode} cfg.expandMode Updates children only when set to `children` or the
 * node itself with children when set to `self` on node expand.
 * @prop {boolean} cfg.filter Whether filtering is enabled on this tree table.
 * @prop {number} cfg.filterDelay Delay in milliseconds the filtering.
 * @prop {string} cfg.filterEvent Event that trigger the tree table to be filtered.
 * @prop {string} cfg.formId ID of the form to use for AJAX requests.
 * @prop {boolean} cfg.liveResize Columns are resized live in this mode without using a resize helper.
 * @prop {boolean} cfg.nativeElements Whether native checkbox elements should be used for selection.
 * @prop {string} cfg.nodeType Type of the row nodes of this tree table.
 * @prop {Partial<PrimeFaces.widget.PaginatorCfg>} cfg.paginator When pagination is enabled: The paginator configuration
 * for the paginator.
 * @prop {boolean} cfg.resizableColumns Defines if columns can be resized or not.
 * @prop {number} cfg.scrollHeight Height of scrollable data.
 * @prop {number} cfg.scrollWidth Width of scrollable data.
 * @prop {boolean} cfg.scrollable Whether or not the data should be scrollable.
 * @prop {PrimeFaces.widget.TreeTable.SelectionMode} cfg.selectionMode How rows may be selected.
 * @prop {boolean} cfg.stickyHeader Sticky header stays in window viewport during scrolling.
 * @prop {string} cfg.editInitEvent Event that triggers row/cell editing.
 */
PrimeFaces.widget.TreeTable = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.thead = $(this.jqId + '_head');
        this.tbody = $(this.jqId + '_data');
        this.cfg.expandMode = this.cfg.expandMode||"children";

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
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

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.columnWidthsFixed = false;
        this.scrollStateVal = this.scrollStateHolder ? this.scrollStateHolder.val() : null;

        this._super(cfg);
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
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
            this.paginator.bindSwipeEvents(this.jq, this.cfg);
        }
    },

    /**
     * Sets up all event listeners required for the standard filters. Also skins the filter inputs.
     * @private
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
     * Clear the filter input of this tree table and shows all rows again.
     */
    clearFilters: function() {
        columnFilters = this.thead.find('> tr > th.ui-filter-column > .ui-column-filter').val('');
        $(this.jqId + '\\:globalFilter').val('');
        this.filter();
    },

    /**
     * Sets up the event listeners required for filtering this tree table, filtering either when enter is pressed or
     * when the {@link TreeTableCfg.filterEvent|configured event} occurs.
     * @private
     * @param {JQuery} filter The filter input field.
     */
    bindTextFilter: function(filter) {
        if(this.cfg.filterEvent === 'enter')
            this.bindEnterKeyFilter(filter);
        else
            this.bindFilterEvent(filter);
    },

    /**
     * Sets up the event listeners required for filtering this tree table when the filter input has changed.
     * @private
     * @param {JQuery} filter The filter input field.
     */
    bindChangeFilter: function(filter) {
        var $this = this;

        filter.on('change', function() {
            $this.filter();
        });
    },

    /**
     * Sets up the event listeners required for filtering this tree table when the enter key is pressed.
     * @private
     * @param {JQuery} filter The filter input field.
     */
    bindEnterKeyFilter: function(filter) {
        var $this = this;

        filter.on('keydown', PrimeFaces.utils.blockEnterKey)
        .on('keyup', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if(key === keyCode.ENTER) {
                $this.filter();

                e.preventDefault();
            }
        });
    },

    /**
     * Sets up the event listeners required for filtering this tree table.
     * @private
     * @param {JQuery} filter The filter input field.
     */
    bindFilterEvent: function(filter) {
        var $this = this;

        //prevent form submit on enter key
        filter.on('keydown.treeTable-blockenter', PrimeFaces.utils.blockEnterKey)
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
     * Reads the current value of the filter input and performs a filtering operation. Sends an AJAX requests to the
     * server and updates this tree table with the result. Also invokes the appropriate behaviors.
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

    /**
     * Handles a pagination event by updating this tree table and invoking the appropriate behaviors.
     * @private
     * @param {PrimeFaces.widget.Paginator.PaginationState} newState The new pagination state to apply. 
     */
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

    /**
     * Returns the paginator instance if any is exists.
     * @return {PrimeFaces.widget.Paginator | undefined} The paginator instance for this widget, or `undefined` if
     * paging is not enabled.
     */
    getPaginator: function() {
        return this.paginator;
    },

    /**
     * Sets up all events listeners required for selecting one or multiple rows of this tree table.
     * @private
     */
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

    /**
     * Sets up all events listeners required for sorting the rows of this tree table.
     * @private
     */
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
            if(!$this.shouldSort(e, this)) {
                return;
            }

            PrimeFaces.clearSelection();

            var columnHeader = $(this),
            sortOrder = columnHeader.data('sortorder')||'DESCENDING';

            if(sortOrder === 'ASCENDING')
                sortOrder = 'DESCENDING';
            else if(sortOrder === 'DESCENDING')
                sortOrder = 'ASCENDING';

            $this.sort(columnHeader, sortOrder);
        });
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.widget.ContextMenu} menuWidget
     * @param {PrimeFaces.widget.TreeTable} targetWidget
     * @param {string} targetId
     * @param {PrimeFaces.widget.ContextMenuCfg} cfg 
     */
    bindContextMenu : function(menuWidget, targetWidget, targetId, cfg) {
        var targetSelector = targetId + ' .ui-treetable-data > ' + (cfg.nodeType ? 'tr.ui-treetable-selectable-node.' + cfg.nodeType : 'tr.ui-treetable-selectable-node');
        var targetEvent = cfg.event + '.treetable';

        $(document).off(targetEvent, targetSelector).on(targetEvent, targetSelector, null, function(e) {
            targetWidget.onRowRightClick(e, $(this));
            menuWidget.show(e);
        });
    },

    /**
     * Sets up the CSS and event listeners for the sticky header feature, if it is enabled.
     * @private
     */
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
            width: table.outerWidth() + 'px',
            top: offset.top + 'px',
            left: offset.left + 'px',
            'z-index': PrimeFaces.nextZindex()
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
                                        'top': tableOffset.top + 'px'
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

    /**
     * Sets up all events listeners required for editing entire rows or individual cells.
     * @private
     */
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
            var editEvent = (this.cfg.editInitEvent !== 'click') ? this.cfg.editInitEvent + '.treetable-cell click.treetable-cell' : 'click.treetable-cell';

            this.tbody.off(editEvent, cellSelector)
                .on(editEvent, cellSelector, null, function(e) {
                    if(!$(e.target).is('span.ui-treetable-toggler.ui-c')) {
                        $this.incellClick = true;
				
                        var item = $(this);
                        var cell = item.hasClass('ui-editable-column') ? item : item.closest('.ui-editable-column');
				        
                        if(!cell.hasClass('ui-cell-editing') && e.type === $this.cfg.editInitEvent) {
                            $this.showCellEditor($(this));
				            
                            if($this.cfg.editInitEvent === "dblclick") {
                                $this.incellClick = false;
                            }
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

    /**
     * Sort this tree table by the given column, either in ascending or descending order.
     * @param {JQuery} columnHeader A column to sort by, must be a TH element of the THEAD.
     * @param {PrimeFaces.widget.TreeTable.SortOrder} order Whether to sort the rows in ascending or descending order. 
     */
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

    /**
     * Expands the given row of this tree table.
     * @param {JQuery} node A node to expand, must be a TR element.
     */
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

    /**
     * Collapses the given row of this tree table.
     * @param {JQuery} node A node to collapse, must be a TR element.
     */
    collapseNode: function(node) {
        var $this = this,
        nodeKey = node.attr('data-rk'),
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
  
        var options = {
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

        if(this.hasBehavior('collapse')) {
            this.callBehavior('collapse', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Callback for when a row was clicked. Selects or unselects the row, if that feature is enabled.
     * @private
     * @param {JQuery.Event} event The click event that occurred. 
     * @param {JQuery} node The node that was clicked. 
     */
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

    /**
     * Callback for when a right click was performed on a node. Selects or unselects the row, if that feature is
     * enabled.
     * @private
     * @param {JQuery.Event} event The click event that occurred. 
     * @param {JQuery} node The node that was clicked. 
     */
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

    /**
     * Selects the given row. The {@link TreeTableCfg.selectionMode} must not be set to `checkbox`.
     * @param {JQuery} node A row to select, must be a TR element.
     * @param {boolean} [silent] If set to `true`, does not trigger event listeners. 
     */
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

    /**
     * Unselects the given row. The {@link TreeTableCfg.selectionMode} must not be set to `checkbox`.
     * @param {JQuery} node A row to unselect, must be a TR element.
     * @param {boolean} [silent] If set to `true`, does not trigger event listeners. 
     */
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

    /**
     * Unselects all selected rows. The {@link TreeTableCfg.selectionMode} must not be set to `checkbox`.
     */
    unselectAllNodes: function() {
        var selectedNodes = this.tbody.children('tr.ui-state-highlight');
        for(var i = 0; i < selectedNodes.length; i++) {
            this.unselectNode(selectedNodes.eq(i), true);
        }

        this.selections = [];
        this.writeSelections();
    },

    /**
     * Selects all rows between the current row and the row that was just clicked. Used for multiple selections while
     * the shift key is pressed.
     * @private
     * @param {JQuery} node  A row that was just clicked.
     */
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

    /**
     * Sets the `indeterminate` attribute of the given rows to `true`.
     * @private
     * @param {JQuery} nodes List of rows to process.
     */
    indeterminateNodes: function(nodes) {
        for(var i = 0; i < nodes.length; i++) {
            nodes.eq(i).find('> td:first-child > :checkbox').prop('indeterminate', true);
        }
    },

    /**
     * When the {@link TreeTableCfg.selectionMode} is set to `checkbox`: select the given row if is is currently
     * unselected, or unselects it otherwise.
     * @param {JQuery} node A row to toggle, must be a TR element.
     */
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

    /**
     * Finds all descendants of the given row, i.e. all children, grandchildren etc.
     * @param {JQuery} node A node for which to get the descendants.
     * @return {JQuery} The descendants of the given row. An empty jQuery instance in case the row does not have
     * descendants. 
     */
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

    /**
     * Finds the children of the given row.
     * @param {JQuery} node A row for which to find the children.
     * @return {JQuery} The children of the given row. An empty jQuery instance in case the row does not have children.
     */
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

    /**
     * Propagates a select or unselect event up to the parents of the given row.
     * @private
     * @param {JQuery} node A node that was selected or unselected.
     */
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

    /**
     * Finds the parent row of the given row of this tree table.
     * @param {JQuery} node A row for which to find the parent.
     * @return {JQuery | null} The parent of the given row, or `null` if it does not have a parent.
     */
    getParent: function(node) {
        var parent = $(this.jqId + '_node_' + node.attr('data-prk'));

        return parent.length === 1 ? parent : null;
    },

    /**
     * Removes all children of the given row from the list of currently selected rows.
     * @private
     * @param {string} rowKey A row with children that were unselected.
     */
    removeDescendantsFromSelection: function(rowKey) {
        this.selections = $.grep(this.selections, function(value) {
            return value.indexOf(rowKey + '_') !== 0;
        });
    },

    /**
     * Removes the given row from the list of currenlty selected rows.
     * @param {string} nodeKey A row that was unselected. 
     */
    removeSelection: function(nodeKey) {
        this.selections = $.grep(this.selections, function(value) {
            return value !== nodeKey;
        });
    },

    /**
     * Adds the given row to the list of currently selected rows.
     * @private 
     * @param {string} rowKey A row that was selected. 
     */
    addToSelection: function(rowKey) {
        if(!this.isSelected(rowKey)) {
            this.selections.push(rowKey);
        }
    },

    /**
     * Checks whether the given row is currently selected.
     * @param {string} nodeKey Key of a row to check. 
     * @return {boolean} Whether the given row is selected.
     */
    isSelected: function(nodeKey) {
        return PrimeFaces.inArray(this.selections, nodeKey);
    },

    /**
     * Checks whether only a single row of this tree table can be selected via clicking.
     * @return {boolean} `true` if the {@link TreeTableCfg.selectionMode} is set to `single`, or `false` otherwise.
     */
    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },

    /**
     * Checks whether multiple rows of this tree table may be selected.
     * @return {boolean} `true` if the {@link TreeTableCfg.selectionMode} is set to `multiple`, or `false` otherwise.
     */
    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple';
    },

    /**
     * Checks whether rows of this tree table are selected via checkboxes.
     * @return {boolean} `true` if the {@link TreeTableCfg.selectionMode} is set to `checkbox`, or `false` otherwise.
     */
    isCheckboxSelection: function() {
        return this.cfg.selectionMode == 'checkbox';
    },

    /**
     * Saves the currently selected rows in the hidden input field.
     * @private
     */
    writeSelections: function() {
        this.jqSelection.val(this.selections.join(','));
    },

    /**
     * Callback for when a node was selected. Invokes the appropriate behaviors.
     * @private
     * @param {string} nodeKey Key of the row that was selected. 
     */
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

    /**
     * Callback for when a node was unselected. Invokes the appropriate behaviors.
     * @private
     * @param {string} nodeKey Key of the row that was unselected. 
     */
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

    /**
     * Initializes scrolling and sets up the appropriate event handlers.
     * @private
     */
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

        this.scrollBody.on('scroll.treeTable', function() {
            var scrollLeft = $this.scrollBody.scrollLeft();
            $this.scrollHeaderBox.css('margin-left', -scrollLeft + 'px');
            $this.scrollFooterBox.css('margin-left', -scrollLeft + 'px');

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

    /**
     * Makes a clone of the table header and adds it to the DOM.
     * @private
     */
    cloneHead: function() {
        this.theadClone = this.headerTable.children('thead').clone();
        this.theadClone.find('th').each(function() {
            var header = $(this);
            header.attr('id', header.attr('id') + '_clone');
        });
        this.theadClone.removeAttr('id').addClass('ui-treetable-scrollable-theadclone').height(0).prependTo(this.bodyTable);
    },

    /**
     * Applies the desired width to all columns.
     * @private
     */
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

    /**
     * Recomputes and applies the target width of all columns.
     * @private
     */
    updateColumnWidths: function() {
        this.columnWidthsFixed = false;
        this.jq.find('> table > thead > tr > th').each(function() {
            var col = $(this);
            col.css('width', '');
        });
        this.fixColumnWidths();
    },

    /**
     * Adjust the view and scrolling position for the current height of the table.
     * @private
     */
    adjustScrollHeight: function() {
        var relativeHeight = this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100),
        tableHeaderHeight = this.jq.children('.ui-treetable-header').outerHeight(true),
        tableFooterHeight = this.jq.children('.ui-treetable-footer').outerHeight(true),
        scrollersHeight = (this.scrollHeader.outerHeight(true) + this.scrollFooter.outerHeight(true)),
        height = (relativeHeight - (scrollersHeight + tableHeaderHeight + tableFooterHeight));

        this.scrollBody.height(height);
    },

    /**
     * Sets the height of the scroll body to the value of this widget's configuration.
     * @private
     */
    applyViewPortScrollHeight: function() {
        this.scrollBody.height(this.cfg.scrollHeight);
    },

    /**
     * Adjust the view and scrolling position for the current width of the table.
     * @private
     */
    adjustScrollWidth: function() {
        var width = parseInt((this.jq.parent().innerWidth() * (parseInt(this.cfg.scrollWidth) / 100)));
        this.setScrollWidth(width);
    },

    /**
     * Applies the given outer width to an element.
     * @private
     * @param {JQuery} element An element to modify. 
     * @param {number} width The new (outer) width for the element. 
     */
    setOuterWidth: function(element, width) {
        var diff = element.outerWidth() - element.width();
        element.width(width - diff);
    },

    /**
     * Checks if there is any vertical overflow present currently.
     * @private
     * @return {boolean} `true` if there is overflow in the vertical y direction, or `false` otherwise.
     */
    hasVerticalOverflow: function() {
        return (this.cfg.scrollHeight && this.bodyTable.outerHeight() > this.scrollBody.outerHeight());
    },

    /**
     * Adjust the view for the given scrollbar width.
     * @private
     * @param {number} width The width of the scrollbar.
     */
    setScrollWidth: function(width) {
        var $this = this;
        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), width);
        });
        this.scrollHeader.width(width);
        this.scrollBody.css('padding-right', '0px').width(width);
        this.scrollFooter.width(width);
    },

    /**
     * Aligns the scroll body element, taking into account the width of the scrollbar.
     * @private
     */
    alignScrollBody: function() {
        if(!this.cfg.scrollWidth) {
            if(this.hasVerticalOverflow())
                this.scrollBody.css('padding-right', '0px');
            else
                this.scrollBody.css('padding-right', this.getScrollbarWidth() + 'px');
        }
    },

    /**
     * Attempts to find a width for the scrollbar of the browser.
     * @private
     * @return {number} An estimate in pixels for the width of the native scrollbar.
     */
    getScrollbarWidth: function() {
        return $.browser.webkit ? '15' : PrimeFaces.calculateScrollbarWidth();
    },

    /**
     * Reads the scroll position from the hidden input element and applies it.
     * @private
     */
    restoreScrollState: function() {
        var scrollState = this.scrollStateVal||this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        this.scrollBody.scrollLeft(scrollValues[0]);
        this.scrollBody.scrollTop(scrollValues[1]);
        this.scrollStateVal = null;
    },

    /**
     * Stores the current scroll position in a hidden input element.
     * @private
     */
    saveScrollState: function() {
        var scrollState = this.scrollBody.scrollLeft() + ',' + this.scrollBody.scrollTop();

        this.scrollStateHolder.val(scrollState);
    },

    /**
     * Sets up the JQuery UI draggable with the appropriate event listeners for resizing columns.
     * @private
     */
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

    /**
     * Callback for when a row was resized. Adjust the column widths.
     * @private
     * @param {JQuery.Event} event Event that triggered the resize.
     * @param {JQueryUI.DraggableEventUIParams} ui Details about the resize.
     */
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

    /**
     * Removes the cloned table header and create a new clone.
     * @private
     */
    reclone: function() {
        this.clone.remove();
        this.clone = this.thead.clone(false);
        this.jq.children('table').append(this.clone);
    },

    /**
     * Switches a row to edit mode and displays the editors for that row.
     * @param {JQuery} row A row for which to activate the editors. Must be a TR element. 
     */
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

    /**
     * Hides the row and display the row editors.
     * @private
     * @param {JQuery} row A row for which to show the editors.
     */
    showRowEditors: function(row) {
        row.addClass('ui-state-highlight ui-row-editing').children('td.ui-editable-column').each(function() {
            var column = $(this);

            column.find('.ui-cell-editor-output').hide();
            column.find('.ui-cell-editor-input').show();
        });
    },

    /**
     * When a row is currently being edited: Saves the edited row and hides the editors.
     * @param {JQuery} rowEditor A row to save, must be a TR element.
     */
    saveRowEdit: function(rowEditor) {
        this.doRowEditRequest(rowEditor, 'save');
    },

    /**
     * When a row is currently being edited: cancels row editing and discards the entered data.
     * @param {JQuery} rowEditor A row for which to cancel editing, must be a TR element.
     */
    cancelRowEdit: function(rowEditor) {
        this.doRowEditRequest(rowEditor, 'cancel');
    },

    /**
     * Sends an AJAX request to the server to handle a row save or cancel event.
     * @private
     * @param {JQuery} rowEditor The inline editor with data that needs to be saved or discarded.
     * @param {string} action The action to perform, either `save` or `cancel`.
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

    /**
     * Updates a row with the given HTML content.
     * @private
     * @param {JQuery} row A row to update.
     * @param {string | HTMLElement | HTMLElement[] | JQuery} content The new HTML content of the row.
     */
    updateRows: function(row, content) {
        this.tbody.children('tr').filter('[data-prk^="'+ row.data('rk') +'"]').remove();
        row.replaceWith(content);
    },

    /**
     * Callback for when validation did not succeed. Switches all editors of the given row to the error state.
     * @private
     * @param {number} index 0-based index of the row with cell editors.
     */
    invalidateRow: function(index) {
        this.tbody.children('tr').eq(index).addClass('ui-widget-content ui-row-editing ui-state-error');
    },

    /**
     * Finds all editors of a row.
     * @private
     * @param {JQuery} row A row for which to find all cell editors.
     * @return {JQuery} All cell editors of the given row. 
     */
    getRowEditors: function(row) {
        return row.find('div.ui-cell-editor');
    },

    /**
     * Collapses the given row of this tree table after saving the contents of an inline editor.
     * @private
     * @param {JQuery} row A row to collapse. 
     */
    collapseRow: function(row) {
        row.removeClass('ui-expanded-row').next('.ui-expanded-row-content').remove();
    },

    /**
     * Activates the inline editor for the given cell.
     * @param {JQuery} c The cell TD element for which to activate the inline editor.
     */
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

            if(this.hasBehavior('cellEditInit')) {
                var cellInfo = this.getCellMeta(cell);
                var ext = {
                    params: [{name: this.id + '_cellInfo', value: cellInfo}]
                };
                this.callBehavior('cellEditInit', ext);
            }
        }
    },

    /**
     * Makes the inline cell visible and sets up the appropriate event listeners.
     * @private
     * @param {JQuery} cell The cell TD element for which to activate inline editing mode.
     */
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
        inputs.eq(0).trigger('focus').trigger('select');

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
                                inputs.eq(focusIndex).trigger('focus');
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

    /**
     * Callback for when the tab key is pressed, switches (focuses) to the next or previous cell editor.
     * @private
     * @param {JQuery} cell The currently focused cell. 
     * @param {boolean} forward `true` to move to the next cell, or `false` to move to the previous cell.
     */
    tabCell: function(cell, forward) {
        var targetCell = forward ? cell.nextAll('td.ui-editable-column:first') : cell.prevAll('td.ui-editable-column:first');
        if(targetCell.length == 0) {
            var tabRow = forward ? cell.parent().next() : cell.parent().prev();
            targetCell = forward ? tabRow.children('td.ui-editable-column:first') : tabRow.children('td.ui-editable-column:last');
        }

        this.showCellEditor(targetCell);
    },

    /**
     * Saves the current data entered into a cell's inline editor. Checks whether the data has changed and if so, sends
     * it to the server.
     * @param {JQuery} cell A cell with an inline editor to save.
     */
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

    /**
     * Switch from edit mode to view mode, Hides the inline editor and displays the data.
     * @private
     * @param {JQuery} cell The cell with an activate inline editor to hide.
     */
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

    /**
     * When the inline editor a cell is active and the user wants to save the changes: send the newly entered data to
     * the server and hide the editor.
     * @private
     * @param {JQuery} cell The cell with an inline editor to be saved.
     */
    doCellEditRequest: function(cell) {
        var cellEditor = cell.children('.ui-cell-editor'),
        cellEditorId = cellEditor.attr('id'),
        cellInfo = this.getCellMeta(cell),
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

    /**
     * When the inline editor a cell is active and the user requests a cancel: discards the data and loads the
     * original content of the cell.
     * @private
     * @param {JQuery} cell The cell for which editing should be canceled.
     */
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

    /**
     * When activating the inline editor of a cell, starts an AJAX request to fetch the editor's HTML. Also invokes
     * the appropriate behaviors.
     * @private
     * @param {JQuery} cell The cell for which inline editing should be activated.
     */
    cellEditInit: function(cell) {
        var cellEditor = cell.children('.ui-cell-editor'),
        cellInfo = this.getCellMeta(cell),
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

    /**
     * Retrieves the meta data of the given cell.
     * @private
     * @param {JQuery} cell A cell to inspect.
     * @return {string} The meta data for the given cell.
     */
    getCellMeta: function(cell) {
        var cellIndex = cell.index(),
            cellInfo = cell.closest('tr').data('rk') + ',' + cellIndex;
        return cellInfo;
    },

    /**
     * Updates the vertical scroll position and adjusts the margin.
     * @private
     */
    updateVerticalScroll: function() {
        if(this.cfg.scrollable && this.cfg.scrollHeight) {
            if(this.bodyTable.outerHeight() < this.scrollBody.outerHeight()) {
                this.scrollHeaderBox.css('margin-right', '0px');
                this.scrollFooterBox.css('margin-right', '0px');
            }
            else {
                this.scrollHeaderBox.css('margin-right', this.marginRight);
                this.scrollFooterBox.css('margin-right', this.marginRight);
            }
        }
    },

    /**
     * Checks whether the tree table should be sorted.
     * @private
     * @param {JQuery.Event} event Event that occurred.
     * @param {JQuery} column Column that was clicked.
     * @return {boolean} Whether the tree table should be sorted.
     */
    shouldSort: function(event, column) {
        if(this.isEmpty()) {
            return false;
        }

        var target = $(event.target);
        if(target.closest('.ui-column-customfilter', column).length) {
            return false;
        }

        return target.is('th,span');
    },

    /**
     * Checks whether any data is currently displayed.
     * @return {boolean} Whether there is any data displayed currently.
     */
    isEmpty: function() {
        return this.tbody.children('tr.ui-treetable-empty-message').length === 1;
    }
});