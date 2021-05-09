/**
 * __PrimeFaces DataTable Widget__
 *
 * DataTable displays data in tabular format.
 *
 * @typedef {number | JQuery} PrimeFaces.widget.DataTable.RowSpecifier Either the 0-based index of a row, or the row
 * element (`TR`) itself.
 *
 * @typedef {"ASCENDING" | "DESCENDING" | "UNSORTED"} PrimeFaces.widget.DataTable.SortOrder The available sort order
 * types for the data table.
 *
 * @typedef {"single" | "multiple"} PrimeFaces.widget.DataTable.CmSelectionMode Indicates whether multiple rows or only
 * a single row of a data table can be selected.
 *
 * @typedef {"radio" | "checkbox"} PrimeFaces.widget.DataTable.SelectionMode Indicates whether rows are selected via
 * radio buttons or via checkboxes.
 *
 * @typedef {"single" | "multiple"} PrimeFaces.widget.DataTable.SortMode Indicates whether a data table can be sorted
 * by multiple columns or only by a single column.
 *
 * @typedef {"single" | "multiple"} PrimeFaces.widget.DataTable.RowExpandMode Indicates whether multiple columns of a
 * data table can be expanded at the same time, or whether other expaned rows should be collapsed when a new row is
 * expanded.
 *
 * @typedef {"eager" | "lazy"} PrimeFaces.widget.DataTable.RowEditMode Indicates whether row editors are loaded eagerly
 * or on-demand.
 *
 * @typedef {"eager" | "lazy"} PrimeFaces.widget.DataTable.CellEditMode Indicates whether cell editors are loaded
 * eagerly or on-demand.
 *
 * @typedef {"expand" | "fit"} PrimeFaces.widget.DataTable.ResizeMode Indicates the resize behavior of columns.
 *
 * @typedef {"new" | "add" | "checkbox"} PrimeFaces.widget.DataTable.RowSelectMode Indicates how rows of a data table
 * may be selected. `new` always unselects other rows, `add` preserves the currently selected rows, and `checkbox` adds
 * a checkbox next to each row.
 *
 * @typedef {"cancel" | "save"} PrimeFaces.widget.DataTable.RowEditAction When a row is editable: whether to `save` the
 * current contents of the row or `cancel` the row edit and discard all changes.
 *
 * @typedef PrimeFaces.widget.DataTable.OnRowClickCallback Callback that is invoked when the user clicks on a row of the
 * data table.
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.DataTable.OnRowClickCallback.event The click event that occurred.
 * @param {JQuery} PrimeFaces.widget.DataTable.OnRowClickCallback.row The TR row that was clicked.
 *
 * @interface {PrimeFaces.widget.DataTable.RowMeta} RowMeta Describes the meta information of row, such as its index and
 * its row key.
 * @prop {string | undefined} RowMeta.key The unique key of the row. `undefined` when no key was defined for the rows.
 * @prop {number} RowMeta.index The 0-based index of the row in the data table.
 *
 *
 * @interface {PrimeFaces.widget.DataTable.SortMeta} SortMeta Describes a sorting operation of the data table. The
 * items of the data table may be sorted by multiple column, in which case the sorting operation is describes by a list
 * of these objects.
 * @prop {string} SortMeta.col ID of the column to sort by.
 * @prop {-1 | 1} SortMeta.order Whether to sort the items by the column value in an ascending or descending order.
 *
 * @implements {PrimeFaces.widget.ContextMenu.ContextMenuProvider<PrimeFaces.widget.DataTable>}
 *
 * @prop {boolean} allLoadedLiveScroll Whether all available items were  already loaded.
 * @prop {string} ascMessage Localized message for sorting a column in ascending order.
 * @prop {JQuery} bodyTable The DOM element for the body part of the table.
 * @prop {Record<number, string>} cacheMap Cache for the contents of a row. Key is the row index, value the HTML content
 * of the row.
 * @prop {number} cacheRows Number of rows to cache.
 * @prop {JQuery} checkAllToggler DOM element of the container with the `check all` checkbox in the header.
 * @prop {JQuery} checkAllTogglerInput DOM element of the `check all` checkbox in the header.
 * @prop {JQuery} clone Clone of the table header.
 * @prop {boolean} columnWidthsFixed Whether column widths are fixed or may be resized.
 * @prop {boolean} contextMenuClick Whether the context menu was clicked.
 * @prop {PrimeFaces.widget.ContextMenu} contextMenuWidget Widget with the context menu for the data table.
 * @prop {JQuery} currentCell Current cell to be edited.
 * @prop {number | null} cursorIndex 0-based index of row where the the cursor is located.
 * @prop {string} descMessage Localized message for sorting a column in descending order.
 * @prop {JQuery} dragIndicatorBottom DOM element of the icon that indicates a column is draggable.
 * @prop {JQuery} dragIndicatorTop DOM element of the icon that indicates a column is draggable.
 * @prop {number[]} expansionProcess List of row indices to expand.
 * @prop {number} filterTimeout ID as returned by `setTimeout` used during filtering.
 * @prop {JQuery | null} focusedRow DOM element of the currently focused row.
 * @prop {boolean} focusedRowWithCheckbox Whether the focused row includes the checkbox for selecting the row.
 * @prop {JQuery} footerCols The DOM elements for the footer columns.
 * @prop {JQuery} footerTable The DOM elements for the footer table.
 * @prop {JQuery} groupResizers The DOM elements for the resizer button of each group.
 * @prop {boolean} hasColumnGroup Whether the table has any column groups.
 * @prop {JQuery} headerTable The DOM elements for the header table.
 * @prop {JQuery} headers DOM elements for the `TH` headers of this data table.
 * @prop {boolean} incellClick Whether a click occurred inside a table cell.
 * @prop {boolean} isRTL Whether the writing direction is set to right-to-left.
 * @prop {boolean} isRowTogglerClicked Whether a row toggler was clicked.
 * @prop {boolean} liveScrollActive Whether live scrolling is currently active.
 * @prop {boolean} loadingLiveScroll Whether data is currently being loaded due to the live scrolling feature.
 * @prop {boolean} mousedownOnRow Whether a mousedown event occurred on a row.
 * @prop {boolean} ignoreRowHoverEvent Whether to ignore row hover event.
 * @prop {JQuery} orderStateHolder INPUT element storing the current column / row order.
 * @prop {number | null} originRowIndex The original row index of the row that was clicked.
 * @prop {PrimeFaces.widget.Paginator} paginator When pagination is enabled: The paginator widget instance used for
 * paging.
 * @prop {boolean} percentageScrollHeight The current relative vertical scroll position.
 * @prop {boolean} percentageScrollWidth The current relative horizontal scroll position.
 * @prop {boolean} reflowDD `true` if reflow is enabled, `false` otherwise.
 * @prop {number} relativeHeight The height of the table viewport, relative to the total height, used for scrolling.
 * @prop {string[]} resizableState A list with the current widths for each resizable column.
 * @prop {JQuery} resizableStateHolder INPUT element storing the current widths for each resizable column.
 * @prop {number} resizeTimeout The set-timeout timer ID of the timer used for resizing.
 * @prop {JQuery} resizerHelper The DOM element for the resize helper.
 * @prop {string} rowSelector The CSS selector for the table rows.
 * @prop {string} rowSelectorForRowClick The CSS selector for the table rows that can be clicked.
 * @prop {JQuery} scrollBody The DOM element for the scrollable body of the table.
 * @prop {JQuery} scrollFooter The DOM element for the scrollable body of the table.
 * @prop {JQuery} scrollFooterBox The DOM element for the scrollable footer box of the table.
 * @prop {JQuery} scrollHeader The DOM element for the scrollable header of the table.
 * @prop {JQuery} scrollHeaderBox The DOM element for the scrollable header box of the table.
 * @prop {number} scrollOffset The current scroll position.
 * @prop {JQuery} scrollStateHolder INPUT element storing the current scroll position.
 * @prop {number} scrollTimeout The set-timeout timer ID of the timer used for scrolling.
 * @prop {string} scrollbarWidth CSS attribute for the scrollbar width, eg. `20px`.
 * @prop {string[]} selection List of row keys for the currently selected rows.
 * @prop {string} selectionHolder ID of the INPUT element storing the currently selected rows.
 * @prop {boolean} shouldLiveScroll Whether live scrolling is currently enabled.
 * @prop {Record<string, PrimeFaces.widget.DataTable.SortMeta>} sortMeta Information about how each column is sorted.
 * Key is the column key.
 * @prop {JQuery} sortableColumns DOM elements for the columns that are sortable.
 * @prop {JQuery} stickyContainer The DOM element for the sticky container of the table.
 * @prop {JQuery} tbody DOM element of the `TBODY` element of this data table, if it exists.
 * @prop {JQuery} tfoot DOM element of the `TFOOT` element of this data table, if it exists.
 * @prop {JQuery} thead DOM element of the `THEAD` element of this data table, if it exists.
 * @prop {JQuery} theadClone The DOM element for the cloned table head.
 * @prop {boolean} virtualScrollActive Whether virtual scrolling is currently active.
 *
 *
 * @interface {PrimeFaces.widget.DataTableCfg} cfg The configuration for the {@link  DataTable| DataTable widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {boolean} cfg.allowUnsorting When true columns can be unsorted upon clicking sort.
 * @prop {string} cfg.cellEditMode Defines the cell edit behavior.
 * @prop {string} cfg.cellSeparator Separator text to use in output mode of editable cells with multiple components.
 * @prop {boolean} cfg.clientCache Caches the next page asynchronously.
 * @prop {boolean} cfg.disableContextMenuIfEmpty `true` to disable the context menu when the data table has got on
 * data row, or `false` otherwise.
 * @prop {boolean} cfg.disabledTextSelection Disables text selection on row click.
 * @prop {boolean} cfg.draggableColumns Columns can be reordered with drag & drop when enabled.
 * @prop {boolean} cfg.draggableRows When enabled, rows can be reordered using drag & drop.
 * @prop {string} cfg.editInitEvent Event that triggers row/cell editing.
 * @prop {PrimeFaces.widget.DataTable.CellEditMode} cfg.editMode Whether rows may be edited as a whole or whether each
 * cell can be edited individually.
 * @prop {boolean} cfg.editable Controls incell editing.
 * @prop {boolean} cfg.expansion `true` if rows are expandable, or `false` otherwise.
 * @prop {boolean} cfg.filter `true` if filtering is enabled, or `false` otherwise.
 * @prop {number} cfg.filterDelay Delay for filtering in milliseconds.
 * @prop {string} cfg.filterEvent Event to invoke filtering for input filters.
 * @prop {number} cfg.frozenColumns The number of frozen columns.
 * @prop {boolean} cfg.liveResize Columns are resized live in this mode without using a resize helper.
 * @prop {boolean} cfg.liveScroll Enables live scrolling.
 * @prop {number} cfg.liveScrollBuffer Percentage of the height of the buffer between the bottom of the page and the
 * scroll position to initiate the load for the new chunk. This value is in the range `0...100`.
 * @prop {boolean} cfg.multiSort `true` if sorting by multiple columns is enabled, or `false` otherwise.
 * @prop {boolean} cfg.multiViewState Whether multiple resize mode is enabled.
 * @prop {boolean} cfg.nativeElements `true` to use native radio button and checkbox elements, or `false` otherwise.
 * @prop {PrimeFaces.widget.DataTable.OnRowClickCallback} cfg.onRowClick Callback that is invoked when the user clicked on
 * a row of the data table.
 * @prop {boolean} cfg.reflow Reflow mode is a responsive mode to display columns as stacked depending on screen size.
 * @prop {boolean} cfg.resizableColumns Enables column resizing.
 * @prop {PrimeFaces.widget.DataTable.ResizeMode} cfg.resizeMode Defines the resize behavior.
 * @prop {string} cfg.rowDragSelector CSS selector for the draggable handle.
 * @prop {PrimeFaces.widget.DataTable.RowEditMode} cfg.rowEditMode Defines the row edit.
 * @prop {PrimeFaces.widget.DataTable.RowExpandMode} cfg.rowExpandMode Defines row expand mode.
 * @prop {boolean} cfg.rowHover Adds hover effect to rows. Hover is always on when selection is enabled.
 * @prop {PrimeFaces.widget.DataTable.RowSelectMode} cfg.rowSelectMode Defines row selection mode for multiple
 * selection.
 * @prop {string} cfg.rowSelector CSS selector find finding the rows of this data table.
 * @prop {boolean} cfg.saveOnCellBlur Saves the changes in cell editing on blur, when set to false changes are
 * discarded.
 * @prop {string} cfg.scrollHeight Scroll viewport height.
 * @prop {number} cfg.scrollLimit Maximum number of rows that may be loaded via live scrolling.
 * @prop {number} cfg.scrollStep Number of additional rows to load in each live scroll.
 * @prop {string} cfg.scrollWidth Scroll viewport width.
 * @prop {boolean} cfg.scrollable Makes data scrollable with fixed header.
 * @prop {PrimeFaces.widget.DataTable.SelectionMode} cfg.selectionMode Enables row selection.
 * @prop {boolean} cfg.selectionPageOnly When using a paginator and selection mode is `checkbox`, the select all
 * checkbox in the header will select all rows on the current page if `true`, or all rows on all pages if `false`.
 * Default is `true`.
 * @prop {boolean} cfg.sorting `true` if sorting is enabled on the data table, `false` otherwise.
 * @prop {string[]} cfg.sortMetaOrder IDs of the columns by which to order. Order by the first column, then by the
 * second, etc.
 * @prop {boolean} cfg.stickyHeader Sticky header stays in window viewport during scrolling.
 * @prop {string} cfg.stickyTopAt Selector to position on the page according to other fixing elements on the top of the
 * table.
 * @prop {string} cfg.tabindex The value of the `tabindex` attribute for this data table.
 * @prop {boolean} cfg.virtualScroll Loads data on demand as the scrollbar gets close to the bottom.
 *
 * @interface {PrimeFaces.widget.DataTable.WidthInfo} WidthInfo Describes the width information of a DOM element.
 * @prop {number | string} WidthInfo.width The width of the element. It's either a unit-less numeric pixel value or a
 * string containing the width including an unit.
 * @prop {boolean} WidthInfo.isOuterWidth Tells whether the width includes the border-box or not.
 */
PrimeFaces.widget.DataTable = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * Map between the sort order names and the multiplier for the comparator.
     * @protected
     * @type {Record<PrimeFaces.widget.DataTable.SortOrder, -1 | 0 | 1>}
     */
    SORT_ORDER: {
        ASCENDING: 1,
        DESCENDING: -1,
        UNSORTED: 0
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.thead = this.getThead();
        this.tbody = this.getTbody();
        this.tfoot = this.getTfoot();

        if(this.cfg.paginator) {
            this.bindPaginator();
        }

        if(this.cfg.sorting) {
            this.bindSortEvents();
        }

        if(this.cfg.rowHover) {
            this.setupRowHover();
        }

        if(this.cfg.selectionMode) {
            this.setupSelection();
        }

        if(this.cfg.filter) {
            this.setupFiltering();
        }

        if(this.cfg.expansion) {
            this.expansionProcess = [];
            this.bindExpansionEvents();
        }

        if(this.cfg.editable) {
            this.bindEditEvents();
        }

        if(this.cfg.draggableRows) {
            this.makeRowsDraggable();
        }

        if(this.cfg.reflow) {
            this.initReflow();
        }

        if(this.cfg.resizableColumns) {
            this.resizableStateHolder = $(this.jqId + '_resizableColumnState');
            this.resizableState = [];

            if(this.resizableStateHolder.attr('value')) {
                this.resizableState = this.resizableStateHolder.val().split(',');
            }
        }

        this.updateEmptyColspan();
        this.renderDeferred();
    },

    /**
     * @include
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.isRTL = this.jq.hasClass('ui-datatable-rtl');
        this.cfg.partialUpdate = (this.cfg.partialUpdate === false) ? false : true;

        if(this.cfg.scrollable) {
            this.setupScrolling();
        }

        if(this.cfg.groupColumnIndexes) {
            this.groupRows();
            this.bindToggleRowGroupEvents();
        }

        if(this.cfg.resizableColumns) {
            this.setupResizableColumns();
        }

        if(this.cfg.draggableColumns) {
            this.setupDraggableColumns();
        }

        if(this.cfg.stickyHeader) {
            this.setupStickyHeader();
        }

        if(this.cfg.onRowClick) {
            this.bindRowClick();
        }

        if(this.cfg.expansion) {
            this.initRowExpansion();
            this.updateExpandedRowsColspan();
        }
        if(this.cfg.reflow) {
           this.jq.css('visibility', 'visible');
        }
    },

    /**
     * Retrieves the table header of this data table.
     * @return {JQuery} DOM element of the table header.
     */
    getThead: function() {
        return $(this.jqId + '_head');
    },

    /**
     * Retrieves the table body of this data table.
     * @return {JQuery} DOM element of the table body.
     */
    getTbody: function() {
        return $(this.jqId + '_data');
    },

    /**
     * Retrieves the table footer of this data table.
     * @return {JQuery} DOM element of the table footer.
     */
    getTfoot: function() {
        return $(this.jqId + '_foot');
    },

    /**
     * Sets the given HTML string as the content of the body of this data table. Afterwards, sets up all required event
     * listeners etc.
     * @protected
     * @param {string} data HTML string to set on the body.
     * @param {boolean} [clear] Whether the contents of the table body should be removed beforehand.
     */
    updateData: function(data, clear) {
        var empty = (clear === undefined) ? true: clear;

        if(empty)
            this.tbody.html(data);
        else
            this.tbody.append(data);

        this.postUpdateData();
    },

    /**
     * Called after an AJAX update. Binds the appropriate event listeners again.
     * @private
     */
    postUpdateData: function() {
        if(this.cfg.draggableRows) {
            this.makeRowsDraggable();
        }

        if(this.cfg.reflow) {
            this.initReflow();
        }

        if(this.cfg.groupColumnIndexes) {
            this.groupRows();
            this.bindToggleRowGroupEvents();
        }

        if(this.cfg.expansion) {
            this.initRowExpansion();
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.columnWidthsFixed = false;
        this.ignoreRowHoverEvent = false;

        this.unbindEvents();

        this._super(cfg);
    },

    /**
     * Removes event listeners needed if refreshing to prevent multiple sort and pagination events.
     *
     * Cancels all current drag and drop events.
     * @private
     */
    unbindEvents: function() {
        if (this.sortableColumns) {
            this.sortableColumns.off();
        }
        if (this.paginator) {
            this.paginator.unbindEvents();
        }

        // #5582: destroy any current draggable items
        if (this.cfg.draggableColumns || this.cfg.draggableRows) {
            var dragdrop = $.ui.ddmanager.current;
            if (dragdrop && dragdrop.helper) {
                var item = dragdrop.currentItem || dragdrop.element;
                if(item.closest('.ui-datatable')[0] === this.jq[0]) {
                    document.body.style.cursor = 'default';
                    dragdrop.cancel();
                }
            }
        }
    },

    /**
     * Binds the change event listener and renders the paginator
     * @private
     */
    bindPaginator: function() {
        var _self = this;
        this.cfg.paginator.paginate = function(newState) {
            if(_self.cfg.clientCache) {
                _self.loadDataWithCache(newState);
            }
            else {
                _self.paginate(newState);
            }
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
        this.paginator.bindSwipeEvents(this.jq, this.cfg);

        if(this.cfg.clientCache) {
            this.cacheRows = this.paginator.getRows();
            var newState = {
                first:  this.paginator.getFirst(),
                rows: this.paginator.getRows(),
                page: this.paginator.getCurrentPage()
            };
            this.clearCacheMap();
            this.fetchNextPage(newState);
        }
    },

    /**
     * Applies events related to sorting in a non-obstrusive way
     * @private
     */
    bindSortEvents: function() {
        var $this = this,
            hasAriaSort = false;
        this.cfg.tabindex = this.cfg.tabindex||'0';
        this.cfg.multiSort = this.cfg.multiSort||false;
        this.cfg.allowUnsorting = this.cfg.allowUnsorting||false;
        this.headers = this.thead.find('> tr > th');
        this.sortableColumns = this.headers.filter('.ui-sortable-column');
        this.sortableColumns.attr('tabindex', this.cfg.tabindex);

        //aria messages
        this.ascMessage = PrimeFaces.getAriaLabel('datatable.sort.SORT_ASC');
        this.descMessage = PrimeFaces.getAriaLabel('datatable.sort.SORT_DESC');
        this.otherMessage = PrimeFaces.getAriaLabel('datatable.sort.SORT_LABEL');

        //reflow dropdown
        this.reflowDD = $(this.jqId + '_reflowDD');

        this.sortMeta = [];

        for(var i = 0; i < this.sortableColumns.length; i++) {
            var columnHeader = this.sortableColumns.eq(i),
            columnHeaderId = columnHeader.attr('id'),
            sortIcon = columnHeader.children('span.ui-sortable-column-icon'),
            sortOrder = null,
            resolvedSortMetaIndex = null,
            ariaLabel = columnHeader.attr('aria-label');

            if (columnHeader.hasClass('ui-state-active')) {
                if (sortIcon.hasClass('ui-icon-triangle-1-n')) {
                    sortOrder = this.SORT_ORDER.ASCENDING;
                    columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.descMessage));
                    if (!hasAriaSort) {
                        columnHeader.attr('aria-sort', 'ascending');
                        hasAriaSort = true;
                    }
                }
                else if (sortIcon.hasClass('ui-icon-triangle-1-s')) {
                    sortOrder = this.SORT_ORDER.DESCENDING;
                    columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.otherMessage));
                    if (!hasAriaSort) {
                        columnHeader.attr('aria-sort', 'descending');
                        hasAriaSort = true;
                    }
                } else {
                    sortOrder = this.SORT_ORDER.UNSORTED;
                    columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.ascMessage));
                    if (!hasAriaSort) {
                        columnHeader.attr('aria-sort', 'other');
                        hasAriaSort = true;
                    }
                }

                if (this.cfg.multiSort && this.cfg.sortMetaOrder) {
                    resolvedSortMetaIndex = $.inArray(columnHeaderId, this.cfg.sortMetaOrder);

                    this.sortMeta[resolvedSortMetaIndex] = {
                        col: columnHeaderId,
                        order: sortOrder
                    };
                }

                $this.updateReflowDD(columnHeader, sortOrder);
            }
            else {
                sortOrder = this.SORT_ORDER.UNSORTED;
                columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.ascMessage));
                if(!hasAriaSort && i == (this.sortableColumns.length - 1)) {
                    this.sortableColumns.eq(0).attr('aria-sort', 'other');
                    hasAriaSort = true;
                }
            }

            columnHeader.data('sortorder', sortOrder);
        }

        this.sortableColumns.on('mouseenter.dataTable', function() {
            var column = $(this);
            column.addClass('ui-state-hover');
        })
        .on('mouseleave.dataTable', function() {
            var column = $(this);
            column.removeClass('ui-state-hover');
        })
        .on('blur.dataTable', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('focus.dataTable', function() {
            $(this).addClass('ui-state-focus');
        })
        .on('keydown.dataTable', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER) && $(e.target).is(':not(:input)')) {
                $(this).trigger('click.dataTable', (e.metaKey||e.ctrlKey));
                e.preventDefault();
            }
        })
        .on('click.dataTable', function(e, metaKeyOn) {
            if(!$this.shouldSort(e, this)) {
                return;
            }

            PrimeFaces.clearSelection();

            var columnHeader = $(this),
                sortOrderData = columnHeader.data('sortorder'),
                sortOrder = (sortOrderData === $this.SORT_ORDER.UNSORTED) ? $this.SORT_ORDER.ASCENDING :
                    (sortOrderData === $this.SORT_ORDER.ASCENDING) ? $this.SORT_ORDER.DESCENDING :
                        $this.cfg.allowUnsorting ? $this.SORT_ORDER.UNSORTED : $this.SORT_ORDER.ASCENDING,
                metaKey = e.metaKey || e.ctrlKey || metaKeyOn;

            if(!$this.cfg.multiSort || !metaKey) {
                $this.sortMeta = [];
            }

            $this.addSortMeta({
                col: columnHeader.attr('id'),
                order: sortOrder
            });

            $this.sort(columnHeader, sortOrder, $this.cfg.multiSort && metaKey);

            if($this.cfg.scrollable) {
                $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).trigger('focus');
            }

            $this.updateReflowDD(columnHeader, sortOrder);
        });

        $this.updateSortPriorityIndicators();

        if(this.reflowDD && this.cfg.reflow) {
            PrimeFaces.skinSelect(this.reflowDD);
            this.reflowDD.on('change', function(e) {
                var arrVal = $(this).val().split('_'),
                    columnHeader = $this.sortableColumns.eq(parseInt(arrVal[0])),
                    sortOrder = parseInt(arrVal[1]);

                    columnHeader.data('sortorder', sortOrder);
                    columnHeader.trigger('click.dataTable');
            });
        }
    },

    /**
     * Creates the sort order message shown to indicate what the current sort order is.
     * @private
     * @param {string | undefined} ariaLabel Optional label text from an aria attribute.
     * @param {string} sortOrderMessage Sort order message.
     * @return {string} The sort order message to use.
     */
    getSortMessage: function(ariaLabel, sortOrderMessage) {
        var headerName = ariaLabel ? ariaLabel.split(':')[0] : '';
        return headerName + ': ' + sortOrderMessage;
    },

    /**
     * Called in response to a click. Checks whether this data table should now be sorted. Returns `false` when there
     * are no items to be sorted, or when no sorting button was clicked.
     * @private
     * @param {JQuery.TriggeredEvent} event (Click) event that occurred.
     * @param {JQuery} column Column Column of this data table on which the event occurred.
     * @return {boolean} `true` to perform a sorting operation, `false` otherwise.
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
     * Adds the given sorting to the list of sortings. Each sorting describes a column by which to sort. This data table
     * may be sorted by multiple columns.
     * @param {PrimeFaces.widget.DataTable.SortMeta} meta Sorting to add.
     * @private
     */
    addSortMeta: function(meta) {
        this.sortMeta = $.grep(this.sortMeta, function(value) {
            return value.col !== meta.col;
        });

        this.sortMeta.push(meta);
    },

    /**
     * Binds filter events to standard filters
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
     * Sets up the event listeners for the text filters on a column.
     * @private
     * @param {JQuery} filter INPUT element of the text filter.
     */
    bindTextFilter: function(filter) {
        if(this.cfg.filterEvent === 'enter')
            this.bindEnterKeyFilter(filter);
        else
            this.bindFilterEvent(filter);
    },

    /**
     * Sets up the change event listeners on the column filter elements.
     * @private
     * @param {JQuery} filter DOM element of a column filter
     */
    bindChangeFilter: function(filter) {
        var $this = this;

        filter.off('change')
        .on('change', function() {
            $this.filter();
        });
    },

    /**
     * Sets up the enter key event listeners for the text filters on a column.
     * @private
     * @param {JQuery} filter INPUT element of the text filter.
     */
    bindEnterKeyFilter: function(filter) {
        var $this = this;

        filter.off('keydown keyup')
        .on('keydown', PrimeFaces.utils.blockEnterKey)
        .on('keyup', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                $this.filter();

                e.preventDefault();
            }
        });
    },

    /**
     * Sets up all event listeners for the given filter element of a column filter.
     * @private
     * @param {JQuery} filter DOM element of a column filter.
     */
    bindFilterEvent: function(filter) {
        var $this = this;
        var filterEventName = this.cfg.filterEvent + '.dataTable';

        //prevent form submit on enter key
        filter.off('keydown.dataTable-blockenter ' + filterEventName)
        .on('keydown.dataTable-blockenter', PrimeFaces.utils.blockEnterKey)
        .on(filterEventName, function(e) {
            if (PrimeFaces.utils.ignoreFilterKey(e)) {
                return;
            }

            if($this.filterTimeout) {
                clearTimeout($this.filterTimeout);
            }

            $this.filterTimeout = setTimeout(function() {
                $this.filter();
                $this.filterTimeout = null;
            },
            $this.cfg.filterDelay);
        });

        // #89 IE clear "x" button
        if (PrimeFaces.env.isIE()) {
            filter.off('mouseup.dataTable').on('mouseup.dataTable', function(e) {
                var input = $(this),
                oldValue = input.val();

                if(oldValue == "") {
                    return;
                }

                setTimeout(function() {
                    var newValue = input.val();
                    if(newValue == "") {
                        $this.filter();
                    }
                }, 1);
            });
        }
    },

    /**
     * Sets up the data table and adds all event listeners required for hovering over rows.
     * @private
     */
    setupRowHover: function() {
        var selector = '> tr.ui-widget-content';
        if(!this.cfg.selectionMode || this.cfg.selectionMode === 'checkbox') {
            this.bindRowHover(selector);
        }
    },

    /**
     * Sets up the data table and adds all event listener required for selecting rows.
     * @private
     */
    setupSelection: function() {
        this.selectionHolder = this.jqId + '_selection';
        this.cfg.rowSelectMode = this.cfg.rowSelectMode||'new';
        this.rowSelector = '> tr.ui-widget-content.ui-datatable-selectable';
        this.cfg.disabledTextSelection = this.cfg.disabledTextSelection === false ? false : true;
        this.cfg.selectionPageOnly = this.cfg.selectionPageOnly === false ? !this.cfg.paginator : true;
        this.rowSelectorForRowClick = this.cfg.rowSelector||'td:not(.ui-column-unselectable):not(.ui-grouped-column),span:not(.ui-c)';

        var preselection = $(this.selectionHolder).val();
        this.selection = !preselection ? [] : preselection.split(',');

        //shift key based range selection
        this.originRowIndex = null;
        this.cursorIndex = null;

        this.bindSelectionEvents();
    },

    /**
     * Applies events related to selection in a non-obstrusive way
     * @private
     */
    bindSelectionEvents: function() {
        if(this.cfg.selectionMode === 'radio') {
            this.bindRadioEvents();
            this.bindRowEvents();
        }
        else if(this.cfg.selectionMode === 'checkbox') {
            this.bindCheckboxEvents();
            this.updateHeaderCheckbox();

            if(this.cfg.rowSelectMode !== 'checkbox') {
                this.bindRowEvents();
            }
        }
        else {
            this.bindRowEvents();
        }
    },

    /**
     * Sets up all event listeners for event triggered on a row of this data table.
     * @private
     */
     bindRowEvents: function() {
        var $this = this;
    
        this.bindRowHover(this.rowSelector);
    
        this.tbody.off('click.dataTable mousedown.dataTable', this.rowSelector).on('mousedown.dataTable', this.rowSelector, null, function(e) {
            $this.mousedownOnRow = true;
        })
        .on('click.dataTable', this.rowSelector, null, function(e) {
            $this.onRowClick(e, this);
            $this.mousedownOnRow = false;
        });
    
        //double click
        if (this.hasBehavior('rowDblselect')) {
            this.tbody.off('dblclick.dataTable', this.rowSelector).on('dblclick.dataTable', this.rowSelector, null, function(e) {
                $this.onRowDblclick(e, $(this));
            });
        };

        this.bindSelectionKeyEvents();
    },

    /**
     * Sets up all delegated event listeners on the table body.
     * @private
     */
    bindSelectionKeyEvents: function() {
        var $this = this;

        this.getFocusableTbody().on('focus', function(e) {
            //ignore mouse click on row
            if(!$this.mousedownOnRow) {
                $this.focusedRow = $this.tbody.children('tr.ui-widget-content.ui-datatable-selectable.ui-state-highlight').eq(0);
                if ($this.focusedRow.length == 0) {
                    $this.focusedRow = $this.tbody.children('tr.ui-widget-content.ui-datatable-selectable').eq(0);
                }

                $this.highlightFocusedRow();

                if($this.cfg.scrollable) {
                    PrimeFaces.scrollInView($this.scrollBody, $this.focusedRow);
                }
            }
        })
        .on('blur', function() {
            if($this.focusedRow) {
                $this.unhighlightFocusedRow();
                $this.focusedRow = null;
            }
        })
        .on('keydown', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if($(e.target).is(':input')) {
                return;
            }

            if($this.focusedRow) {
                switch(key) {
                    case keyCode.UP:
                    case keyCode.DOWN:
                        var rowSelector = 'tr.ui-widget-content.ui-datatable-selectable',
                        row = key === keyCode.UP ? $this.focusedRow.prevAll(rowSelector).eq(0) : $this.focusedRow.nextAll(rowSelector).eq(0);

                        if(row.length) {
                            $this.unhighlightFocusedRow();

                            if($this.isCheckboxSelectionEnabled()) {
                                row.find('> td.ui-selection-column .ui-chkbox input').trigger('focus');
                            }
                            else {
                                $this.focusedRow = row;
                            }

                            $this.highlightFocusedRow();

                            if($this.cfg.scrollable) {
                                PrimeFaces.scrollInView($this.scrollBody, $this.focusedRow);
                            }
                        }
                        e.preventDefault();
                    break;

                    case keyCode.ENTER:
                    case keyCode.SPACE:
                        if($this.focusedRowWithCheckbox) {
                            $this.focusedRow.find('> td.ui-selection-column > div.ui-chkbox > div.ui-chkbox-box').trigger('click.dataTable');
                        }
                        else {
                            e.target = $this.focusedRow.children().eq(0).get(0);
                            $this.onRowClick(e,$this.focusedRow.get(0));
                        }

                        e.preventDefault();
                    break;

                    default:
                    break;
                };
            }
        });

    },

    /**
     * Highlights the currently focused row (if any) by adding the appropriate CSS class.
     * @protected
     */
    highlightFocusedRow: function() {
        this.focusedRow.addClass('ui-state-hover');
    },

    /**
     * Unhighlights the currently focused row (if any) by adding the appropriate CSS class.
     * @protected
     */
    unhighlightFocusedRow: function() {
        this.focusedRow.removeClass('ui-state-hover');
    },

    /**
     * Stores the row which is currently focused.
     * @protected
     * @param {JQuery} row Row to set as the focused row.
     */
    assignFocusedRow: function(row) {
        this.focusedRow = row;
    },

    /**
     * Sets up the event listeners for hovering over a data table row.
     * @protected
     * @param {string} rowSelector Selector for the row elements. Any hover event that does not reach an element that
     * matches this selector will be ignored.
     */
    bindRowHover: function(rowSelector) {
        var $this = this;
        this.tbody.off('mouseenter.dataTable mouseleave.dataTable', rowSelector)
            .on('mouseenter.dataTable', rowSelector, null, function() {
                if (!$this.ignoreRowHoverEvent) {
                    $(this).addClass('ui-state-hover');
                }
            })
            .on('mouseleave.dataTable', rowSelector, null, function() {
                if (!$this.ignoreRowHoverEvent) {
                    $(this).removeClass('ui-state-hover');
                }
            });

        if (this.cfg.groupColumnIndexes) {
            var columnSelector = rowSelector + ' > td';
            this.tbody.off('mouseenter.dataTable mouseleave.dataTable', columnSelector)
                .on('mouseenter.dataTable', columnSelector, null, function() {
                    var row = $(this).parent();
                    if ($(this).hasClass('ui-grouped-column')) {
                        row.removeClass('ui-state-hover');
                        $this.ignoreRowHoverEvent = true;
                    }
                    else {
                        row.addClass('ui-state-hover');
                    }
                })
                .on('mouseleave.dataTable', columnSelector, null, function() {
                    if (!$(this).hasClass('ui-grouped-column')) {
                        $this.ignoreRowHoverEvent = false;
                    }
                });
        }
    },

    /**
     * Sets up the event listeners for radio buttons contained in this data table.
     * @protected
     */
    bindRadioEvents: function() {
        var $this = this,
        radioInputSelector = '> tr.ui-widget-content:not(.ui-datatable-empty-message) > td.ui-selection-column :radio';

        if(this.cfg.nativeElements) {
            this.tbody.off('click.dataTable', radioInputSelector).on('click.dataTable', radioInputSelector, null, function(e) {
                var radioButton = $(this);

                if(!radioButton.prop('checked'))
                    $this.selectRowWithRadio(radioButton);
            });
        }
        else {
            var radioSelector = '> tr.ui-widget-content:not(.ui-datatable-empty-message) > td.ui-selection-column .ui-radiobutton .ui-radiobutton-box';
            this.tbody.off('click.dataTable mouseenter.dataTable mouseleave.dataTable', radioSelector)
                .on('mouseenter.dataTable', radioSelector, null, function() {
                    var radio = $(this);
                    if(!radio.hasClass('ui-state-disabled')) {
                        radio.addClass('ui-state-hover');
                    }
                })
                .on('mouseleave.dataTable', radioSelector, null, function() {
                    var radio = $(this);
                    radio.removeClass('ui-state-hover');
                })
                .on('click.dataTable', radioSelector, null, function() {
                    var radio = $(this),
                    checked = radio.hasClass('ui-state-active'),
                    disabled = radio.hasClass('ui-state-disabled');

                    if (!disabled) {
                        radio.prev().children(':radio').trigger('focus.dataTable');
                        if (!checked) {
                            $this.selectRowWithRadio(radio);
                        }
                    }
                });
        }

        //keyboard support
        this.tbody.off('focus.dataTable blur.dataTable change.dataTable', radioInputSelector)
            .on('focus.dataTable', radioInputSelector, null, function() {
                var input = $(this),
                box = input.parent().next();

                box.addClass('ui-state-focus');
            })
            .on('blur.dataTable', radioInputSelector, null, function() {
                var input = $(this),
                box = input.parent().next();

                box.removeClass('ui-state-focus');
            })
            .on('change.dataTable', radioInputSelector, null, function() {
                var currentInput = $this.tbody.find(radioInputSelector).filter(':checked'),
                currentRadio = currentInput.parent().next();

                $this.selectRowWithRadio(currentRadio);
            });

    },

    /**
     * Sets up the event listeners for radio buttons contained in this data table.
     * @protected
     */
    bindCheckboxEvents: function() {
        var $this = this,
        checkboxSelector;

        if(this.cfg.nativeElements) {
            checkboxSelector = '> tr.ui-widget-content.ui-datatable-selectable > td.ui-selection-column :checkbox';
            this.checkAllToggler = this.thead.find('> tr > th.ui-selection-column > :checkbox');

            this.checkAllToggler.on('click', function() {
                $this.toggleCheckAll();
            });

            this.tbody.off('click.dataTable', checkboxSelector).on('click.dataTable', checkboxSelector, null, function(e) {
                var checkbox = $(this);

                if(checkbox.prop('checked'))
                    $this.selectRowWithCheckbox(checkbox);
                else
                    $this.unselectRowWithCheckbox(checkbox);
            });
        }
        else {
            checkboxSelector = '> tr.ui-widget-content.ui-datatable-selectable > td.ui-selection-column > div.ui-chkbox > div.ui-chkbox-box';
            this.checkAllToggler = this.thead.find('> tr > th.ui-selection-column > div.ui-chkbox.ui-chkbox-all > div.ui-chkbox-box');

            this.checkAllToggler.on('mouseenter', function() {
                var box = $(this);
                if(!box.hasClass('ui-state-disabled')) {
                    box.addClass('ui-state-hover');
                }
            })
            .on('mouseleave', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click', function() {
                var box = $(this);
                if(!box.hasClass('ui-state-disabled')) {
                    $this.toggleCheckAll();
                }
            })
            .on('keydown', function(e) {
                var keyCode = $.ui.keyCode,
                key = e.which;

                switch(key) {
                    case keyCode.ENTER:
                    case keyCode.SPACE:
                        if(!$(this).hasClass('ui-state-disabled')) {
                            $this.toggleCheckAll();
                        }
                    break;
                    default:
                    break;
                }
            });

            this.tbody.off('mouseenter.dataTable mouseleave.dataTable click.dataTable', checkboxSelector)
                        .on('mouseenter.dataTable', checkboxSelector, null, function() {
                            $(this).addClass('ui-state-hover');
                        })
                        .on('mouseleave.dataTable', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover');
                        })
                        .on('click.dataTable', checkboxSelector, null, function() {
                            var checkbox = $(this);

                            if(checkbox.attr('aria-checked') === "true") {
                                $this.unselectRowWithCheckbox(checkbox);
                            }
                            else {
                                $this.selectRowWithCheckbox(checkbox);
                            }
                        });
        }

        //keyboard support
        this.tbody.off('focus.dataTable blur.dataTable change.dataTable', checkboxSelector)
                    .on('focus.dataTable', checkboxSelector, null, function() {
                        var input = $(this);

                        input.addClass('ui-state-focus');

                        $this.focusedRow = input.closest('.ui-datatable-selectable');
                        $this.focusedRowWithCheckbox = true;
                    })
                    .on('blur.dataTable', checkboxSelector, null, function() {
                        var input = $(this);

                        input.removeClass('ui-state-focus');

                        $this.unhighlightFocusedRow();
                        $this.focusedRow = null;
                        $this.focusedRowWithCheckbox = false;
                    })
                    .on('change.dataTable', checkboxSelector, null, function(e) {
                        var input = $(this);

                        if(input.attr('aria-checked') === "true" || input.prop('checked')) {
                            $this.selectRowWithCheckbox(input);
                        }
                        else {
                            $this.unselectRowWithCheckbox(input);
                        }
                    });

        this.checkAllToggler.on('focus.dataTable', function(e) {
                        var input = $(this);

                        if(!input.hasClass('ui-state-disabled')) {
                            input.addClass('ui-state-focus');
                        }
                    })
                    .on('blur.dataTable', function(e) {
                        var input = $(this);

                        input.removeClass('ui-state-focus');
                    })
                    .on('change.dataTable', function(e) {
                        var input = $(this);

                        if(!input.hasClass('ui-state-disabled')) {
                            if((input.attr('aria-checked') !== "true") && !input.prop('checked')) {
                                input.addClass('ui-state-active');
                            }

                            $this.toggleCheckAll();

                            if(input.attr('aria-checked') === "true" || input.prop('checked')) {
                                input.removeClass('ui-state-active');
                            }
                        }
                    });
    },

    /**
     * Expands or collapses the given row, depending on whether it is currently collapsed or expanded, respectively.
     * @param {JQuery} row A row (`TR`) to expand or collapse.
     */
    toggleRow: function(row) {
        if(row && !this.isRowTogglerClicked) {
            var toggler = row.find('> td > div.ui-row-toggler');
            this.toggleExpansion(toggler);
        }
        this.isRowTogglerClicked = false;
    },

    /**
     * Applies events related to row expansion in a non-obstrusive way
     * @protected
     */
    bindExpansionEvents: function() {
        var $this = this,
        togglerSelector = '> tr > td > div.ui-row-toggler';

        this.tbody.off('click.datatable-expansion', togglerSelector)
            .on('click.datatable-expansion', togglerSelector, null, function() {
                $this.isRowTogglerClicked = true;
                $this.toggleExpansion($(this));
            })
            .on('keydown.datatable-expansion', togglerSelector, null, function(e) {
                var key = e.which,
                keyCode = $.ui.keyCode;

                if((key === keyCode.ENTER)) {
                    $this.toggleExpansion($(this));
                    e.preventDefault();
                }
            });
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.widget.ContextMenu} menuWidget
     * @param {PrimeFaces.widget.DataTable} targetWidget
     * @param {string} targetId
     * @param {PrimeFaces.widget.ContextMenuCfg} cfg
     */
    bindContextMenu : function(menuWidget, targetWidget, targetId, cfg) {
        var $this = this;
        var targetSelector = targetId + ' tbody.ui-datatable-data > tr.ui-widget-content';
        var targetEvent = cfg.event + '.datatable';
        this.contextMenuWidget = menuWidget;

        $(document).off(targetEvent, targetSelector).on(targetEvent, targetSelector, null, function(e) {
            var row = $(this);

            if(targetWidget.cfg.selectionMode && row.hasClass('ui-datatable-selectable')) {
                targetWidget.onRowRightClick(e, this, cfg.selectionMode);
                targetWidget.updateContextMenuCell(e, targetWidget);
                menuWidget.show(e);
            }
            else if(targetWidget.cfg.editMode === 'cell') {
                targetWidget.updateContextMenuCell(e, targetWidget);
                menuWidget.show(e);
            }
            else if(row.hasClass('ui-datatable-empty-message') && !$this.cfg.disableContextMenuIfEmpty) {
                menuWidget.show(e);
            }
        });

        if(this.cfg.scrollable && this.scrollBody) {
            this.scrollBody.off('scroll.dataTable-contextmenu').on('scroll.dataTable-contextmenu', function() {
                if($this.contextMenuWidget.jq.is(':visible')) {
                    $this.contextMenuWidget.hide();
                }
            });
        }
    },

    /**
     * Updates the currently selected cell based on where the context menu right click occurred.
     * @private
     * @param {JQuery.TriggeredEvent} event Event that occurred.
     * @param {PrimeFaces.widget.DataTable} targetWidget The current widget
     */
    updateContextMenuCell: function(event, targetWidget) {
        var target = $(event.target),
        cell = target.is('td.ui-editable-column') ? target : target.parents('td.ui-editable-column:first');

        if(targetWidget.contextMenuCell) {
            targetWidget.contextMenuCell.removeClass('ui-state-highlight');
        }

        targetWidget.contextMenuClick = true;
        targetWidget.contextMenuCell = cell;
        targetWidget.contextMenuCell.addClass('ui-state-highlight');
    },

    /**
     * Sets up the event listeners for clicking on a row.
     * @private
     */
    bindRowClick: function() {
        var $this = this,
        rowSelector = '> tr.ui-widget-content:not(.ui-expanded-row-content)';
        this.tbody.off('click.dataTable-rowclick', rowSelector).on('click.dataTable-rowclick', rowSelector, null, function(e) {
            var target = $(e.target),
            row = target.is('tr.ui-widget-content') ? target : target.closest('tr.ui-widget-content');

            $this.cfg.onRowClick.call(this, row);
        });
    },

    /**
     * Reflow mode is a responsive mode to display columns as stacked depending on screen size.
     * @private
     */
    initReflow: function() {
        var headerColumns = this.thead.find('> tr > th');

        for(var i = 0; i < headerColumns.length; i++) {
            var headerColumn = headerColumns.eq(i),
            reflowHeaderText = headerColumn.find('.ui-reflow-headertext:first').text(),
            colTitleEl = headerColumn.children('.ui-column-title'),
            title = (reflowHeaderText && reflowHeaderText.length) ? reflowHeaderText : colTitleEl.text();
            this.tbody.find('> tr:not(.ui-datatable-empty-message,.ui-datatable-summaryrow) > td:nth-child(' + (i + 1) + ')').prepend('<span class="ui-column-title">' + PrimeFaces.escapeHTML(title) + '</span>');
        }
    },

    /**
     * Prepares this data table for the current scrolling settings and sets up all related event handlers.
     * @protected
     */
    setupScrolling: function() {
        this.scrollHeader = this.jq.children('.ui-datatable-scrollable-header');
        this.scrollBody = this.jq.children('.ui-datatable-scrollable-body');
        this.scrollFooter = this.jq.children('.ui-datatable-scrollable-footer');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        this.scrollHeaderBox = this.scrollHeader.children('div.ui-datatable-scrollable-header-box');
        this.scrollFooterBox = this.scrollFooter.children('div.ui-datatable-scrollable-footer-box');
        this.headerTable = this.scrollHeaderBox.children('table');
        this.bodyTable = this.cfg.virtualScroll ? this.scrollBody.children('div').children('table') : this.scrollBody.children('table');
        this.footerTable = this.scrollFooter.children('table');
        this.footerCols = this.scrollFooter.find('> .ui-datatable-scrollable-footer-box > table > tfoot > tr > td');
        this.percentageScrollHeight = this.cfg.scrollHeight && (this.cfg.scrollHeight.indexOf('%') !== -1);
        this.percentageScrollWidth = this.cfg.scrollWidth && (this.cfg.scrollWidth.indexOf('%') !== -1);
        var $this = this,
        scrollBarWidth = this.getScrollbarWidth() + 'px',
        hScrollWidth = this.scrollBody[0].scrollWidth;

        if(this.cfg.scrollHeight) {
            if(this.percentageScrollHeight) {
                this.adjustScrollHeight();
            }

            if(this.hasVerticalOverflow()) {
                this.scrollHeaderBox.css('margin-right', scrollBarWidth);
                this.scrollFooterBox.css('margin-right', scrollBarWidth);
            }
        }

        this.fixColumnWidths();

        if(this.cfg.scrollWidth) {
            if(this.percentageScrollWidth)
                this.adjustScrollWidth();
            else
                this.setScrollWidth(parseInt(this.cfg.scrollWidth));
        }

        this.cloneHead();

        if(this.cfg.liveScroll) {
            this.clearScrollState();
            this.scrollOffset = 0;
            this.cfg.liveScrollBuffer = (100 - this.cfg.liveScrollBuffer) / 100;
            this.shouldLiveScroll = true;
            this.loadingLiveScroll = false;
            this.allLoadedLiveScroll = $this.cfg.scrollStep >= $this.cfg.scrollLimit;
        }

        this.restoreScrollState();

        if(this.cfg.virtualScroll) {
            var row = this.bodyTable.children('tbody').children('tr.ui-widget-content');
            if(row) {
                var hasEmptyMessage = row.eq(0).hasClass('ui-datatable-empty-message'),
                scrollLimit = $this.cfg.scrollLimit;

                if(hasEmptyMessage) {
                    scrollLimit = 1;
                    $this.bodyTable.css('top', '0px');
                }

                this.rowHeight = row.outerHeight();
                this.scrollBody.children('div').css('height', parseFloat((scrollLimit * this.rowHeight + 1) + 'px'));

                if(hasEmptyMessage && this.cfg.scrollHeight && this.percentageScrollHeight) {
                    setTimeout(function() {
                        $this.adjustScrollHeight();
                    }, 10);
                }
            }
        }

        this.scrollBody.on('scroll.dataTable', function() {
            var scrollLeft = $this.scrollBody.scrollLeft();

            if ($this.isRTL) {
                $this.scrollHeaderBox.css('margin-right', (scrollLeft - hScrollWidth + this.clientWidth) + 'px');
                $this.scrollFooterBox.css('margin-right', (scrollLeft - hScrollWidth + this.clientWidth) + 'px');
            }
            else {
                $this.scrollHeaderBox.css('margin-left', -scrollLeft + 'px');
                $this.scrollFooterBox.css('margin-left', -scrollLeft + 'px');
            }

            if($this.isEmpty()) {
                return;
            }

            if($this.cfg.virtualScroll) {
                var virtualScrollBody = this;

                clearTimeout($this.scrollTimeout);
                $this.scrollTimeout = setTimeout(function() {
                    var viewportHeight = $this.scrollBody.outerHeight(),
                    tableHeight = $this.bodyTable.outerHeight(),
                    pageHeight = $this.rowHeight * $this.cfg.scrollStep,
                    virtualTableHeight = parseFloat(($this.cfg.scrollLimit * $this.rowHeight) + 'px'),
                    pageCount = (virtualTableHeight / pageHeight)||1;

                    if(virtualScrollBody.scrollTop + viewportHeight > parseFloat($this.bodyTable.css('top')) + tableHeight || virtualScrollBody.scrollTop < parseFloat($this.bodyTable.css('top'))) {
                        var page = Math.floor((virtualScrollBody.scrollTop * pageCount) / (virtualScrollBody.scrollHeight)) + 1;
                        $this.loadRowsWithVirtualScroll(page, function () {
                            $this.bodyTable.css('top', ((page - 1) * pageHeight) + 'px');
                        });
                    }
                }, 200);
            }
            else if($this.shouldLiveScroll) {
                var scrollTop = Math.ceil(this.scrollTop),
                scrollHeight = this.scrollHeight,
                viewportHeight = this.clientHeight;

                if((scrollTop >= ((scrollHeight * $this.cfg.liveScrollBuffer) - (viewportHeight))) && $this.shouldLoadLiveScroll()) {
                    $this.loadLiveRows();
                }
            }

            $this.saveScrollState();
        });

        this.scrollHeader.on('scroll.dataTable', function() {
            $this.scrollHeader.scrollLeft(0);
        });

        this.scrollFooter.on('scroll.dataTable', function() {
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
     * When live scrolling (loading more items on-demand) is enabled, checks whether more items are allowed to be loaded
     * right now. Returns `false` when live scroling is disabled or items are currently being loaded already.
     * @private
     * @return {boolean} `true` if more items may be loaded, `false` otherwise.
     */
    shouldLoadLiveScroll: function() {
        return (!this.loadingLiveScroll && !this.allLoadedLiveScroll);
    },

    /**
     * Clones a table header and removes duplicate IDs.
     * @private
     * @param {JQuery} thead The head (`THEAD`) of the table to clone.
     * @param {JQuery} table The table to which the head belongs.
     * @return {JQuery} The cloned table head.
     */
    cloneTableHeader: function(thead, table) {
        var clone = thead.clone();
        clone.find('th').each(function() {
            var header = $(this);
            header.attr('id', header.attr('id') + '_clone');
            $(this).children().not('.ui-column-title').remove();
            $(this).children('.ui-column-title').children().remove();
        });
        clone.removeAttr('id').addClass('ui-datatable-scrollable-theadclone').height(0).prependTo(table);

        return clone;
    },

    /**
     * Creates and stores a cloned copy of the table head(er) of this data table, and sets up some event handers.
     * @protected
     */
    cloneHead: function() {
        var $this = this;

        if (this.theadClone) {
            this.theadClone.remove();
        }
        this.theadClone = this.cloneTableHeader(this.thead, this.bodyTable);

        //reflect events from clone to original
        if(this.cfg.sorting) {
            this.sortableColumns.removeAttr('tabindex').off('blur.dataTable focus.dataTable keydown.dataTable');

            var clonedColumns = this.theadClone.find('> tr > th'),
            clonedSortableColumns = clonedColumns.filter('.ui-sortable-column');
            clonedColumns.each(function() {
                var col = $(this),
                originalId = col.attr('id').split('_clone')[0];
                if(col.hasClass('ui-sortable-column')) {
                    col.data('original', originalId);
                }

                $(PrimeFaces.escapeClientId(originalId))[0].style.width = col[0].style.width;
            });

            clonedSortableColumns.on('blur.dataTable', function() {
                $(PrimeFaces.escapeClientId($(this).data('original'))).removeClass('ui-state-focus');
            })
            .on('focus.dataTable', function() {
                $(PrimeFaces.escapeClientId($(this).data('original'))).addClass('ui-state-focus');
            })
            .on('keydown.dataTable', function(e) {
                var key = e.which,
                keyCode = $.ui.keyCode;

                if((key === keyCode.ENTER) && $(e.target).is(':not(:input)')) {
                    $(PrimeFaces.escapeClientId($(this).data('original'))).trigger('click.dataTable', (e.metaKey||e.ctrlKey));
                    e.preventDefault();
                }
            });
        }
    },

    /**
     * Adjusts the height of the body of this data table for the current scrolling settings.
     * @protected
     */
    adjustScrollHeight: function() {
        var relativeHeight = this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100),
        headerChilden = this.jq.children('.ui-datatable-header'),
        footerChilden = this.jq.children('.ui-datatable-footer'),
        tableHeaderHeight = (headerChilden.length > 0) ? headerChilden.outerHeight(true) : 0,
        tableFooterHeight = (footerChilden.length > 0) ? footerChilden.outerHeight(true) : 0,
        scrollersHeight = (this.scrollHeader.outerHeight(true) + this.scrollFooter.outerHeight(true)),
        paginatorsHeight = this.paginator ? this.paginator.getContainerHeight(true) : 0,
        height = (relativeHeight - (scrollersHeight + paginatorsHeight + tableHeaderHeight + tableFooterHeight));

        if(this.cfg.virtualScroll) {
            this.scrollBody.css('max-height', height + 'px');
        }
        else {
            this.scrollBody.height(height);
        }
    },

    /**
     * Adjusts the width of the header, body, and footer of this data table to fit the current settings.
     * @protected
     */
    adjustScrollWidth: function() {
        var width = parseInt((this.jq.parent().innerWidth() * (parseInt(this.cfg.scrollWidth) / 100)));
        this.setScrollWidth(width);
    },

    /**
     * Applies the given width to this data table.
     * @private
     * @param {JQuery} element Element of the data table.
     * @param {number} width New width in pixels to set.
     */
    setOuterWidth: function(element, width) {
        if (element.css('box-sizing') === 'border-box') { // Github issue: #5014
            element.outerWidth(width);
        }
        else {
            element.width(width);
        }
    },

    /**
     * Retrieves width information of the given column.
     * @private
     * @param {JQuery} col The column of which the width should be retrieved.
     * @param {boolean} isIncludeResizeableState Tells whether the width should be retrieved from the resizable state,
     * if it exists.
     * @return {PrimeFaces.widget.DataTable.WidthInfo} The width information of the given column.
     */
    getColumnWidthInfo: function(col, isIncludeResizeableState) {
        var $this = this;
        var width, isOuterWidth;

        if(isIncludeResizeableState && this.resizableState) {
            width = $this.findColWidthInResizableState(col.attr('id'));
            isOuterWidth = false;
        }

        if(!width) {
            width = col[0].style.width;
            isOuterWidth = width && (col.css('box-sizing') === 'border-box');
        }

        if(!width) {
            width = col.width();
            isOuterWidth = false;
        }

        return {
            width: width,
            isOuterWidth: isOuterWidth
        };
    },

    /**
     * Applies the width information to the given element.
     * @private
     * @param {JQuery} element The element to which the width should be applied.
     * @param {PrimeFaces.widget.DataTable.WidthInfo} widthInfo The width information (retrieved using the method {@link getColumnWidthInfo}).
     */
    applyWidthInfo: function(element, widthInfo) {
        if(widthInfo.isOuterWidth) {
            element.outerWidth(widthInfo.width);
        }
        else {
            element.width(widthInfo.width);
        }
    },

    /**
     * Applies the given scroll width to this data table.
     * @protected
     * @param {number} width Scroll width in pixels to set.
     */
    setScrollWidth: function(width) {
        var $this = this;
        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), width);
        });
        this.scrollHeader.width(width);
        this.scrollBody.css('margin-right', '0px').width(width);
        this.scrollFooter.width(width);
    },

    /**
     * Adds some margin to the scroll body to make it align properly.
     * @private
     */
    alignScrollBody: function() {
        var marginRight = this.hasVerticalOverflow() ? this.getScrollbarWidth() + 'px' : '0px';

        this.scrollHeaderBox.css('margin-right', marginRight);
        this.scrollFooterBox.css('margin-right', marginRight);
    },

    /**
     * Finds the width of the current scrollbar used for this data table.
     * @private
     * @return {number} The width in pixels of the scrollbar of this data table.
     */
    getScrollbarWidth: function() {
        if(!this.scrollbarWidth) {
            this.scrollbarWidth = PrimeFaces.env.browser.webkit ? '15' : PrimeFaces.calculateScrollbarWidth();
        }

        return this.scrollbarWidth;
    },

    /**
     * Checks whether the body of this data table overflow vertically.
     * @protected
     * @return {boolean} `true` if any content overflow vertically, `false` otherwise.
     */
    hasVerticalOverflow: function() {
        return (this.cfg.scrollHeight && this.bodyTable.outerHeight() > this.scrollBody.outerHeight());
    },

    /**
     * Reads the saved scroll state and applies it. This helps to preserve the current scrolling position during AJAX
     * updates.
     * @private
     */
    restoreScrollState: function() {
        var scrollState = this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        if (scrollValues[0] == '-1') {
            scrollValues[0] = this.scrollBody[0].scrollWidth;
        }

        this.scrollBody.scrollLeft(scrollValues[0]);
        this.scrollBody.scrollTop(scrollValues[1]);
    },

    /**
     * Saves the current scrolling position. This helps to preserve the current scrolling position during AJAX updates.
     * @private
     */
    saveScrollState: function() {
        var scrollState = this.scrollBody.scrollLeft() + ',' + this.scrollBody.scrollTop();

        this.scrollStateHolder.val(scrollState);
    },

    /**
     * Clears the saved scrolling position.
     * @private
     */
    clearScrollState: function() {
        this.scrollStateHolder.val('0,0');
    },

    /**
     * Adjusts the width of the given columns to fit the current settings.
     * @protected
     */
    fixColumnWidths: function() {
        var $this = this;

        if(!this.columnWidthsFixed) {
            if(this.cfg.scrollable) {
                this.scrollHeader.find('> .ui-datatable-scrollable-header-box > table > thead > tr > th').each(function() {
                    var headerCol = $(this),
                    colIndex = headerCol.index(),
                    widthInfo = $this.getColumnWidthInfo(headerCol, true);

                    $this.applyWidthInfo(headerCol, widthInfo);

                    if($this.footerCols.length > 0) {
                        var footerCol = $this.footerCols.eq(colIndex);
                        $this.applyWidthInfo(footerCol, widthInfo);
                    }
                });
            }
            else {
                var columns = this.jq.find('> .ui-datatable-tablewrapper > table > thead > tr > th'),
                    visibleColumns = columns.filter(':visible'),
                    hiddenColumns = columns.filter(':hidden');

                this.setColumnsWidth(visibleColumns);
                /* IE fixes */
                this.setColumnsWidth(hiddenColumns);
            }

            this.columnWidthsFixed = true;
        }
    },

    /**
     * Applies the appropriated width to all given column elements.
     * @param {JQuery} columns A list of column elements.
     * @private
     */
    setColumnsWidth: function(columns) {
        if(columns.length) {
            var $this = this;

            columns.each(function() {
                var col = $(this),
                widthInfo = $this.getColumnWidthInfo(col, true);

                $this.applyWidthInfo(col, widthInfo);
            });
        }
    },

    /**
     * Use only when live scrolling is enabled: Loads the next set of rows on-the-fly.
     */
    loadLiveRows: function() {
        if(this.liveScrollActive||(this.scrollOffset + this.cfg.scrollStep > this.cfg.scrollLimit)) {
            return;
        }

        this.liveScrollActive = true;
        this.scrollOffset += this.cfg.scrollStep;

        //Disable scroll if there is no more data left
        if(this.scrollOffset === this.cfg.scrollLimit) {
            this.shouldLiveScroll = false;
        }

        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_scrolling', value: true},
                            {name: this.id + '_first', value: 1},
                            {name: this.id + '_skipChildren', value: true},
                            {name: this.id + '_scrollOffset', value: this.scrollOffset},
                            {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        //insert new rows
                        this.updateData(content, false);

                        this.liveScrollActive = false;
                    }
                });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                if(typeof args.totalRecords !== 'undefined') {
                    $this.cfg.scrollLimit = args.totalRecords;
                }

                $this.loadingLiveScroll = false;
                $this.allLoadedLiveScroll = ($this.scrollOffset + $this.cfg.scrollStep) >= $this.cfg.scrollLimit;

                // reset index of shift selection on multiple mode
                $this.originRowIndex = null;
            }
        };

        if (this.hasBehavior('liveScroll')) {
            this.callBehavior('liveScroll', options);
        } else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * When live scrolling is enabled: Loads the next set of rows via AJAX.
     * @private
     * @param {number} page 0-based index of the page to load.
     * @param {() => void} callback Callback that is invoked after the rows have been loaded and inserted into the DOM.
     */
    loadRowsWithVirtualScroll: function(page, callback) {
        if(this.virtualScrollActive) {
            return;
        }

        this.virtualScrollActive = true;

        var $this = this,
        first = (page - 1) * this.cfg.scrollStep,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_scrolling', value: true},
                            {name: this.id + '_skipChildren', value: true},
                            {name: this.id + '_first', value: first},
                            {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        //insert new rows
                        this.updateData(content);
                        callback();
                        this.virtualScrollActive = false;
                    }
                });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                if(typeof args.totalRecords !== 'undefined') {
                    $this.cfg.scrollLimit = args.totalRecords;
                }

                // reset index of shift selection on multiple mode
                $this.originRowIndex = null;
            }
        };
        if (this.hasBehavior('virtualScroll')) {
            this.callBehavior('virtualScroll', options);
        } else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Switches to the given page by loading the content via AJAX. Compare with `loadDataWithCache`, which first checks
     * whether the data is already cached and loads it from the server only when not found in the cache.
     * @private
     * @param {PrimeFaces.widget.Paginator.PaginationState} newState The new values for the current page and the rows
     * per page count.
     */
    paginate: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_pagination', value: true},
                    {name: this.id + '_first', value: newState.first},
                    {name: this.id + '_rows', value: newState.rows},
                    {name: this.id + '_skipChildren', value: true},
                    {name: this.id + '_encodeFeature', value: true}]
        };

        if (!this.cfg.partialUpdate) {
            options.params.push({name: this.id + '_fullUpdate', value: true});

            options.onsuccess = function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.jq.replaceWith(content);
                        }
                    });

                return true;
            };
        }
        else {
            options.onsuccess = function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);

                            if(this.checkAllToggler) {
                                this.updateHeaderCheckbox();
                            }

                            if(this.cfg.scrollable) {
                                this.alignScrollBody();
                            }

                            if(this.cfg.clientCache) {
                                this.cacheMap[newState.first] = content;
                            }
                        }
                    });

                return true;
            };

            options.oncomplete = function(xhr, status, args, data) {
                $this.paginator.cfg.page = newState.page;
                if(args && typeof args.totalRecords !== 'undefined') {
                    $this.paginator.updateTotalRecords(args.totalRecords);
                }
                else {
                    $this.paginator.updateUI();
                }
                $this.updateColumnsView();
                // reset index of shift selection on multiple mode
                $this.originRowIndex = null;
            };
        }

        if(this.hasBehavior('page')) {
            this.callBehavior('page', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Loads next page asynchronously to keep it at viewstate and Updates viewstate
     * @private
     * @param {PrimeFaces.widget.Paginator.PaginationState} newState The new values for the current page and the rows
     * per page count.
     */
    fetchNextPage: function(newState) {
        var rows = newState.rows,
        first = newState.first,
        $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            global: false,
            params: [{name: this.id + '_skipChildren', value: true},
                    {name: this.id + '_encodeFeature', value: true},
                    {name: this.id + '_first', value: first},
                    {name: this.id + '_rows', value: rows},
                    {name: this.id + '_pagination', value: true},
                    {name: this.id + '_clientCache', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        if(content.length) {
                            var nextFirstValue = first + rows;
                            $this.cacheMap[nextFirstValue] = content;
                        }
                    }
                });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    /**
     * Updates and syncs the current pagination state with the server.
     * @param {PrimeFaces.widget.Paginator.PaginationState} newState The new values for the current page and the rows
     * per page count.
     * @private
     */
    updatePageState: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            global: false,
            params: [{name: this.id + '_pagination', value: true},
                    {name: this.id + '_encodeFeature', value: true},
                    {name: this.id + '_pageState', value: true},
                    {name: this.id + '_first', value: newState.first},
                    {name: this.id + '_rows', value: newState.rows}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        // do nothing
                    }
                });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    /**
     * Performs a sorting operation on the rows of this data table via AJAX
     * @param {JQuery} columnHeader Header of the column by which to sort.
     * @param {-1 | 0 | 1} order Whether to "unsort" or to sort by the column value in an ascending or descending order.
     * @param {boolean} multi `true` if sorting by multiple columns is enabled, or `false` otherwise.
     * @private
     */
    sort: function(columnHeader, order, multi) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_sorting', value: true},
                     {name: this.id + '_skipChildren', value: true},
                     {name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_sortKey', value: $this.joinSortMetaOption('col')},
                     {name: this.id + '_sortDir', value: $this.joinSortMetaOption('order')}]
        };

        if (!this.cfg.partialUpdate) {
            options.params.push({name: this.id + '_fullUpdate', value: true});

            options.onsuccess = function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.jq.replaceWith(content);
                        }
                    });

                return true;
            };
        }
        else {
            options.onsuccess = function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);

                            if(this.checkAllToggler) {
                              this.updateHeaderCheckbox();
                            }
                        }
                    });

                return true;
            };

            options.oncomplete = function(xhr, status, args, data) {
                var paginator = $this.getPaginator();
                if(args) {
                    if(args.totalRecords) {
                        $this.cfg.scrollLimit = args.totalRecords;

                        if(paginator && paginator.cfg.rowCount !== args.totalRecords) {
                            paginator.setTotalRecords(args.totalRecords);
                        }
                    }

                    if(!args.validationFailed) {
                        if(paginator) {
                            paginator.setPage(0, true);
                        }

                        // remove aria-sort
                        var activeColumns = $this.sortableColumns.filter('.ui-state-active');
                        if(activeColumns.length) {
                            activeColumns.removeAttr('aria-sort');
                        }
                        else {
                            $this.sortableColumns.eq(0).removeAttr('aria-sort');
                        }

                        if(!multi) {
                            //aria reset
                            for(var i = 0; i < activeColumns.length; i++) {
                                var activeColumn = $(activeColumns.get(i)),
                                    ariaLabelOfActive = activeColumn.attr('aria-label');

                                activeColumn.attr('aria-label', $this.getSortMessage(ariaLabelOfActive, $this.ascMessage));
                                $(PrimeFaces.escapeClientId(activeColumn.attr('id') + '_clone')).removeAttr('aria-sort').attr('aria-label', $this.getSortMessage(ariaLabelOfActive, $this.ascMessage));
                            }

                            activeColumns.data('sortorder', $this.SORT_ORDER.UNSORTED).removeClass('ui-state-active')
                                        .find('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');
                        }

                        columnHeader.data('sortorder', order).addClass('ui-state-active');
                        var sortIcon = columnHeader.find('.ui-sortable-column-icon'),
                        ariaLabel = columnHeader.attr('aria-label');

                        if (order === $this.SORT_ORDER.DESCENDING) {
                            sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');
                            columnHeader.attr('aria-sort', 'descending').attr('aria-label', $this.getSortMessage(ariaLabel, $this.otherMessage));
                            $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).attr('aria-sort', 'descending')
                                .attr('aria-label', $this.getSortMessage(ariaLabel, $this.otherMessage));
                        } else if (order === $this.SORT_ORDER.ASCENDING) {
                            sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');
                            columnHeader.attr('aria-sort', 'ascending').attr('aria-label', $this.getSortMessage(ariaLabel, $this.descMessage));
                            $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).attr('aria-sort', 'ascending')
                                .attr('aria-label', $this.getSortMessage(ariaLabel, $this.descMessage));
                        } else {
                            sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-carat-2-n-s');
                            columnHeader.removeClass('ui-state-active ').attr('aria-sort', 'other')
                                .attr('aria-label', $this.getSortMessage(ariaLabel, $this.ascMessage));
                            $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).attr('aria-sort', 'other')
                                .attr('aria-label', $this.getSortMessage(ariaLabel, $this.ascMessage));
                        }

                        $this.updateSortPriorityIndicators();
                    }
                }

                if($this.cfg.virtualScroll) {
                    $this.resetVirtualScrollBody();
                }
                else if($this.cfg.liveScroll) {
                    $this.scrollOffset = 0;
                    $this.liveScrollActive = false;
                    $this.shouldLiveScroll = true;
                    $this.loadingLiveScroll = false;
                    $this.allLoadedLiveScroll = $this.cfg.scrollStep >= $this.cfg.scrollLimit;
                }

                if($this.cfg.clientCache) {
                    $this.clearCacheMap();
                }

                $this.updateColumnsView();

                // reset index of shift selection on multiple mode
                $this.originRowIndex = null;
            }
        }

        if (this.hasBehavior('sort')) {
            this.callBehavior('sort', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * In multi-sort mode this will add number indicators to let the user know the current
     * sort order. If only one column is sorted then no indicator is displayed and will
     * only be displayed once more than one column is sorted.
     * @private
     */
    updateSortPriorityIndicators: function() {
        var $this = this;

        // remove all indicator numbers first
        $this.sortableColumns.find('.ui-sortable-column-badge').text('').addClass('ui-helper-hidden');

        // add 1,2,3 etc to columns if more than 1 column is sorted
        var sortMeta =  $this.sortMeta;
        if (sortMeta && sortMeta.length > 1) {
            $this.sortableColumns.each(function() {
                var id = $(this).attr("id");
                for (var i = 0; i < sortMeta.length; i++) {
                    if (sortMeta[i].col == id) {
                        $(this).find('.ui-sortable-column-badge').text(i + 1).removeClass('ui-helper-hidden');
                    }
                }
            });
        }
    },

    /**
     * Serializes the option from the sort meta items.
     * @private
     * @param {keyof PrimeFaces.widget.DataTable.SortMeta} option Property of the sort meta to use.
     * @return {string} All values from the current sort meta list for the given option.
     */
    joinSortMetaOption: function(option) {
        var value = '';

        for(var i = 0; i < this.sortMeta.length; i++) {
            value += this.sortMeta[i][option];

            if(i !== (this.sortMeta.length - 1)) {
                value += ',';
            }
        }

        return value;
    },

    /**
     * Filters this data table. Uses the current values of the filter inputs. This will result in an AJAX request being
     * sent.
     */
    filter: function() {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_filtering', value: true},
                     {name: this.id + '_encodeFeature', value: true}]
        };


        if (!this.cfg.partialUpdate){
            options.params.push({name: this.id + '_fullUpdate', value: true});

            options.onsuccess = function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.jq.replaceWith(content);
                        }
                    });

                return true;
            };
        }
        else {
            options.onsuccess = function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);

                            if(this.cfg.scrollable) {
                                this.alignScrollBody();
                            }

                            if(this.isCheckboxSelectionEnabled()) {
                                this.updateHeaderCheckbox();
                            }
                        }
                    });

                return true;
            };

            options.oncomplete = function(xhr, status, args, data) {
                var paginator = $this.getPaginator();
                if(args && typeof args.totalRecords !== 'undefined') {
                    $this.cfg.scrollLimit = args.totalRecords;

                    if(paginator) {
                        paginator.setTotalRecords(args.totalRecords);
                    }
                }

                if($this.cfg.clientCache) {
                    $this.clearCacheMap();
                }

                if($this.cfg.virtualScroll) {
                    var row = $this.bodyTable.children('tbody').children('tr.ui-widget-content');
                    if(row) {
                        var hasEmptyMessage = row.eq(0).hasClass('ui-datatable-empty-message'),
                        scrollLimit = $this.cfg.scrollLimit;

                        if(hasEmptyMessage) {
                            scrollLimit = 1;
                        }

                        $this.resetVirtualScrollBody();

                        $this.rowHeight = row.outerHeight();
                        $this.scrollBody.children('div').css({'height': parseFloat((scrollLimit * $this.rowHeight + 1) + 'px')});

                        if(hasEmptyMessage && $this.cfg.scrollHeight && $this.percentageScrollHeight) {
                            setTimeout(function() {
                                $this.adjustScrollHeight();
                            }, 10);
                        }
                    }
                }
                else if($this.cfg.liveScroll) {
                    $this.scrollOffset = 0;
                    $this.liveScrollActive = false;
                    $this.shouldLiveScroll = true;
                    $this.loadingLiveScroll = false;
                    $this.allLoadedLiveScroll = $this.cfg.scrollStep >= $this.cfg.scrollLimit;
                }

                $this.updateColumnsView();
                $this.updateEmptyColspan();

                // reset index of shift selection on multiple mode
                $this.originRowIndex = null;
            }
        }

        if(this.hasBehavior('filter')) {
            this.callBehavior('filter', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Callback for a click event on a row.
     * @private
     * @param {JQuery.TriggeredEvent} event Click event that occurred.
     * @param {HTMLElement} rowElement Row that was clicked
     * @param {boolean} silent `true` to prevent behaviors from being invoked, `false` otherwise.
     */
    onRowClick: function(event, rowElement, silent) {
        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is(this.rowSelectorForRowClick)) {
            var row = $(rowElement),
            selected = row.hasClass('ui-state-highlight'),
            metaKey = event.metaKey||event.ctrlKey,
            shiftKey = event.shiftKey;

            this.assignFocusedRow(row);

            //unselect a selected row if metakey is on
            if(selected && metaKey) {
                this.unselectRow(row, silent);
            }
            else {
                //unselect previous selection if this is single selection or multiple one with no keys
                if(this.isSingleSelection() || (this.isMultipleSelection() && event && !metaKey && !shiftKey && this.cfg.rowSelectMode === 'new' )) {
                    this.unselectAllRows();
                }

                //range selection with shift key
                if(this.isMultipleSelection() && event && event.shiftKey && this.originRowIndex !== null) {
                    this.selectRowsInRange(row);
                }
                else if(this.cfg.rowSelectMode === 'add' && selected) {
                    this.unselectRow(row, silent);
                }
                //select current row
                else {
                    this.originRowIndex = row.index();
                    this.cursorIndex = null;
                    this.selectRow(row, silent);
                }
            }

            if(this.cfg.disabledTextSelection) {
                PrimeFaces.clearSelection();
            }

            //#3567 trigger client row click on ENTER/SPACE
            if (this.cfg.onRowClick && event.type === "keydown") {
                this.cfg.onRowClick.call(this, row);
            }
        }
    },

    /**
     * Callback for a double click event on a row.
     * @private
     * @param {JQuery.TriggeredEvent} event Event that occurred.
     * @param {JQuery} row Row that was clicked.
     */
    onRowDblclick: function(event, row) {
        if(this.cfg.disabledTextSelection) {
            PrimeFaces.clearSelection();
        }

        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is(this.rowSelectorForRowClick)) {
            var rowMeta = this.getRowMeta(row);

            this.fireRowSelectEvent(rowMeta.key, 'rowDblselect');
        }
    },

    /**
     * Callback for a right click event on a row. May bring up the context menu
     * @private
     * @param {JQuery.TriggeredEvent} event Event that occurred.
     * @param {JQuery} rowElement Row that was clicked.
     * @param {PrimeFaces.widget.DataTable.CmSelectionMode} cmSelMode The current selection mode.
     */
    onRowRightClick: function(event, rowElement, cmSelMode) {
        var row = $(rowElement),
        rowMeta = this.getRowMeta(row),
        selected = row.hasClass('ui-state-highlight');

        this.assignFocusedRow(row);

        if(cmSelMode === 'single' || !selected) {
            this.unselectAllRows();
        }

        this.selectRow(row, true);

        this.fireRowSelectEvent(rowMeta.key, 'contextMenu');

        if(this.cfg.disabledTextSelection) {
            PrimeFaces.clearSelection();
        }
    },

    /**
     * Converts a row specifier to the row element. The row specifier is either a row index or the row element itself.
     *
     * __In case this data table has got expandable rows, please not that a new table row is created for each expanded row.__
     * This may result in the given index not pointing to the intended row.
     * @param {PrimeFaces.widget.DataTable.RowSpecifier} r The row to convert.
     * @return {JQuery} The row, or an empty JQuery instance of no row was found.
     */
    findRow: function(r) {
        var row = r;

        if(PrimeFaces.isNumber(r)) {
            row = this.tbody.children('tr:eq(' + r + ')');
        }

        return row;
    },

    /**
     * Select the rows between the cursor and the given row.
     * @private
     * @param {JQuery} row A row of this data table.
     */
    selectRowsInRange: function(row) {
        var rows = this.tbody.children(),
        rowMeta = this.getRowMeta(row),
        $this = this;

        //unselect previously selected rows with shift
        if(this.cursorIndex !== null) {
            var oldCursorIndex = this.cursorIndex,
            rowsToUnselect = oldCursorIndex > this.originRowIndex ? rows.slice(this.originRowIndex, oldCursorIndex + 1) : rows.slice(oldCursorIndex, this.originRowIndex + 1);

            rowsToUnselect.each(function(i, item) {
                $this.unselectRow($(item), true);
            });
        }

        //select rows between cursor and origin
        this.cursorIndex = row.index();

        var rowsToSelect = this.cursorIndex > this.originRowIndex ? rows.slice(this.originRowIndex, this.cursorIndex + 1) : rows.slice(this.cursorIndex, this.originRowIndex + 1);

        rowsToSelect.each(function(i, item) {
            $this.selectRow($(item), true);
        });

        this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
    },

    /**
     * Selects the given row, according to the current selection mode.
     * @param {PrimeFaces.widget.DataTable.RowSpecifier} r A row of this data table to select.
     * @param {boolean} [silent] `true` to prevent behaviors and event listeners from being invoked, or `false`
     * otherwise.
     */
    selectRow: function(r, silent) {
        var row = this.findRow(r);
        if(!row.hasClass('ui-datatable-selectable')) {
            return;
        }

        // #5944 in single select all other rows should be unselected
        if (this.isSingleSelection() || this.isRadioSelectionEnabled()) {
            this.unselectAllRows();
        }

        var rowMeta = this.getRowMeta(row);

        this.highlightRow(row);

        if(this.isCheckboxSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.ui-selection-column').find(':checkbox').prop('checked', true);
            else
                this.selectCheckbox(row.children('td.ui-selection-column').find('> div.ui-chkbox > div.ui-chkbox-box'));

            this.updateHeaderCheckbox();
        }

        if(this.isRadioSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.ui-selection-column').find(':radio').prop('checked', true);
            else
                this.selectRadio(row.children('td.ui-selection-column').find('> div.ui-radiobutton > div.ui-radiobutton-box'));
        }

        this.addSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
        }
    },

    /**
     * Unselects the given row.
     * @param {PrimeFaces.widget.DataTable.RowSpecifier} r A row of this data table to unselect.
     * @param {boolean} [silent] `true` to prevent behaviors and event listeners from being invoked, or `false`
     * otherwise.
     */
    unselectRow: function(r, silent) {
        var row = this.findRow(r);
        if(!row.hasClass('ui-datatable-selectable')) {
            return;
        }

        var rowMeta = this.getRowMeta(row);

        this.unhighlightRow(row);

        if(this.isCheckboxSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.ui-selection-column').find(':checkbox').prop('checked', false);
            else
                this.unselectCheckbox(row.children('td.ui-selection-column').find('> div.ui-chkbox > div.ui-chkbox-box'));

            this.updateHeaderCheckbox();
        }

        if(this.isRadioSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.ui-selection-column').find(':radio').prop('checked', false);
            else
                this.unselectRadio(row.children('td.ui-selection-column').find('> div.ui-radiobutton > div.ui-radiobutton-box'));
        }

        this.removeSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselect");
        }
    },

    /**
     * Highlights row to mark it as selected.
     * @protected
     * @param {JQuery} row Row to highlight.
     */
    highlightRow: function(row) {
        row.addClass('ui-state-highlight').attr('aria-selected', true);
    },

    /**
     * Unhighlights row so it is no longer marked as selected.
     * @protected
     * @param {JQuery} row Row to unhighlight.
     */
    unhighlightRow: function(row) {
        row.removeClass('ui-state-highlight').attr('aria-selected', false);
    },

    /**
     * Sends a row select event on server side to invoke a row select listener if defined.
     * @private
     * @param {string} rowKey The key of the row that was selected.
     * @param {string} behaviorEvent Name of the event to fire.
     */
    fireRowSelectEvent: function(rowKey, behaviorEvent) {
        if(this.hasBehavior(behaviorEvent)) {
            var ext = {
                    params: [{name: this.id + '_instantSelectedRowKey', value: rowKey}
                ]
            };

            this.callBehavior(behaviorEvent, ext);
        }
    },

    /**
     * Sends a row unselect event on server side to invoke a row unselect listener if defined
     * @private
     * @param {string} rowKey The key of the row that was deselected.
     * @param {string} behaviorEvent Name of the event to fire.
     */
    fireRowUnselectEvent: function(rowKey, behaviorEvent) {
        if(this.hasBehavior(behaviorEvent)) {
            var ext = {
                params: [
                {
                    name: this.id + '_instantUnselectedRowKey',
                    value: rowKey
                }
                ]
            };

            this.callBehavior(behaviorEvent, ext);
        }
    },

    /**
     * Selects the corresponding row of a radio based column selection
     * @private
     * @param {JQuery} radio A radio INPUT element
     */
    selectRowWithRadio: function(radio) {
        var row = radio.closest('tr'),
        rowMeta = this.getRowMeta(row);

        //clean selection
        this.unselectAllRows();

        //select current
        if(!this.cfg.nativeElements) {
            this.selectRadio(radio);
        }

        this.highlightRow(row);
        this.addSelection(rowMeta.key);
        this.writeSelections();
        this.fireRowSelectEvent(rowMeta.key, 'rowSelectRadio');
    },

    /**
     * Selects the corresponding row of a checkbox based column selection
     * @private
     * @param {JQuery} checkbox A checkox INPUT element
     * @param {boolean} [silent] `true` to prevent behaviors from being invoked, `false` otherwise.
     */
    selectRowWithCheckbox: function(checkbox, silent) {
        var row = checkbox.closest('tr');
        if(!row.hasClass('ui-datatable-selectable')) {
            return;
        }

        var rowMeta = this.getRowMeta(row);

        this.highlightRow(row);

        if(!this.cfg.nativeElements) {
            this.selectCheckbox(checkbox);
        }

        this.addSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.updateHeaderCheckbox();
            this.fireRowSelectEvent(rowMeta.key, "rowSelectCheckbox");
        }
    },

    /**
     * Unselects the corresponding row of a checkbox based column selection
     * @private
     * @param {JQuery} checkbox A checkox INPUT element
     * @param {boolean} [silent] `true` to prevent behaviors from being invoked, `false` otherwise.
     */
    unselectRowWithCheckbox: function(checkbox, silent) {
        var row = checkbox.closest('tr');
        if(!row.hasClass('ui-datatable-selectable')) {
            return;
        }

        var rowMeta = this.getRowMeta(row);

        this.unhighlightRow(row);

        if(!this.cfg.nativeElements) {
            this.unselectCheckbox(checkbox);
        }

        this.removeSelection(rowMeta.key);

        this.uncheckHeaderCheckbox();

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselectCheckbox");
        }
    },

    /**
     * Unselects all rows of this data table so that no rows are selected. This includes all rows on all pages,
     * irrespective of whether they are on the currently shown page.
     */
    unselectAllRows: function() {
        var selectedRows = this.tbody.children('tr.ui-state-highlight'),
        checkboxSelectionEnabled = this.isCheckboxSelectionEnabled(),
        radioSelectionEnabled = this.isRadioSelectionEnabled();

        for(var i = 0; i < selectedRows.length; i++) {
            var row = selectedRows.eq(i);
            if(!row.hasClass('ui-datatable-selectable')) {
                continue;
            }

            this.unhighlightRow(row);

            if(checkboxSelectionEnabled) {
                if(this.cfg.nativeElements)
                    row.children('td.ui-selection-column').find(':checkbox').prop('checked', false);
                else
                    this.unselectCheckbox(row.children('td.ui-selection-column').find('> div.ui-chkbox > div.ui-chkbox-box'));
            }
            else if(radioSelectionEnabled) {
                if(this.cfg.nativeElements)
                    row.children('td.ui-selection-column').find(':radio').prop('checked', false);
                else
                    this.unselectRadio(row.children('td.ui-selection-column').find('> div.ui-radiobutton > div.ui-radiobutton-box'));
            }
        }

        if(checkboxSelectionEnabled) {
            this.uncheckHeaderCheckbox();
        }

        this.selection = [];
        this.writeSelections();
    },

    /**
     * Select all rows on the currently shown page. Compare with `selectAllRows`.
     */
    selectAllRowsOnPage: function() {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            this.selectRow(row, true);
        }
    },

    /**
     * Unselect all rows on the currently shown page. Compare with `unselectAllRows`.
     */
    unselectAllRowsOnPage: function() {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            this.unselectRow(row, true);
        }
    },

     /**
     * Selects all rows of this data table so that no rows are selected. This includes all rows on all pages,
     * irrespective of whether they are on the currently shown page.
     */
    selectAllRows: function() {
        this.selectAllRowsOnPage();
        this.selection = new Array('@all');
        this.writeSelections();
    },

    /**
     * Toggles the `selected all` checkbox in the header of this data table. When no rows are selected, this will select
     * all rows. When some rows are selected, this will unselect all rows.
     */
    toggleCheckAll: function() {
        var shouldCheckAll = true;
        if(this.cfg.nativeElements) {
            var checkboxes = this.tbody.find('> tr.ui-datatable-selectable > td.ui-selection-column > :checkbox:visible'),
            checked = this.checkAllToggler.prop('checked'),
            $this = this;

            checkboxes.each(function() {
                if(checked) {
                    var checkbox = $(this);
                    checkbox.prop('checked', true);
                    $this.selectRowWithCheckbox(checkbox, true);
                }
                else {
                    var checkbox = $(this);
                    checkbox.prop('checked', false);
                    $this.unselectRowWithCheckbox(checkbox, true);
                    shouldCheckAll = false;
                }
            });
        }
        else {
            var checkboxes = this.tbody.find('> tr.ui-datatable-selectable > td.ui-selection-column > div.ui-chkbox > div.ui-chkbox-box:visible'),
            checked = this.checkAllToggler.attr('aria-checked') === "true";
            $this = this;

            if(checked) {
                this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
                this.checkAllToggler.attr('aria-checked', false);
                shouldCheckAll = false;

                checkboxes.each(function() {
                    $this.unselectRowWithCheckbox($(this), true);
                });
            }
            else {
                this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
                this.checkAllToggler.attr('aria-checked', true);

                checkboxes.each(function() {
                    $this.selectRowWithCheckbox($(this), true);
                });
            }
        }

        // GitHub #6730 user wants all rows not just displayed rows
        if(!this.cfg.selectionPageOnly && shouldCheckAll) {
            this.selectAllRows();
        }

        //save state
        this.writeSelections();

        //fire toggleSelect event
        if(this.hasBehavior('toggleSelect')) {
            var ext = {
                params: [{name: this.id + '_checked', value: !checked}]
            };

            this.callBehavior('toggleSelect', ext);
        }
    },

    /**
     * Selects the given checkbox from a row.
     * @private
     * @param {JQuery} checkbox A checkbox to select.
     */
    selectCheckbox: function(checkbox) {
        checkbox.addClass('ui-state-active');

        if (this.cfg.nativeElements) {
            checkbox.prop('checked', true);
        }
        else {
            checkbox.children('span.ui-chkbox-icon:first').removeClass('ui-icon-blank').addClass('ui-icon-check');
            checkbox.attr('aria-checked', true);
        }
    },

    /**
     * Unselects the given checkbox from a row.
     * @private
     * @param {JQuery} checkbox A checkbox to unselect.
     */
    unselectCheckbox: function(checkbox) {
        checkbox.removeClass('ui-state-active');

        if (this.cfg.nativeElements) {
            checkbox.prop('checked', false);
        }
        else {
            checkbox.children('span.ui-chkbox-icon:first').addClass('ui-icon-blank').removeClass('ui-icon-check');
            checkbox.attr('aria-checked', false);
        }
    },

    /**
     * Selects the given radio button from a row.
     * @private
     * @param {JQuery} radio A radio button to select.
     */
    selectRadio: function(radio){
        radio.addClass('ui-state-active');
        radio.children('.ui-radiobutton-icon').addClass('ui-icon-bullet').removeClass('ui-icon-blank');
        radio.prev().children('input').prop('checked', true);
    },

    /**
     * Unselects the given radio button from a row.
     * @private
     * @param {JQuery} radio A radio button to unselect.
     */
    unselectRadio: function(radio){
        radio.removeClass('ui-state-active').children('.ui-radiobutton-icon').addClass('ui-icon-blank').removeClass('ui-icon-bullet');
        radio.prev().children('input').prop('checked', false);
    },

    /**
     * Expands a row to display its detailed content
     * @private
     * @param {JQuery} toggler The row toggler of a row to expand.
     */
    toggleExpansion: function(toggler) {
        var row = toggler.closest('tr'),
        rowIndex = this.getRowMeta(row).index,
        iconOnly = toggler.hasClass('ui-icon'),
        labels = toggler.children('span'),
        expanded = iconOnly ? toggler.hasClass('ui-icon-circle-triangle-s'): toggler.children('span').eq(0).hasClass('ui-helper-hidden'),
        $this = this;

        //Run toggle expansion if row is not being toggled already to prevent conflicts
        if($.inArray(rowIndex, this.expansionProcess) === -1) {
            this.expansionProcess.push(rowIndex);

            if(expanded) {
                if(iconOnly) {
                    toggler.addClass('ui-icon-circle-triangle-e').removeClass('ui-icon-circle-triangle-s').attr('aria-expanded', false);
                }
                else {
                    labels.eq(0).removeClass('ui-helper-hidden');
                    labels.eq(1).addClass('ui-helper-hidden');
                }

                this.collapseRow(row);
                $this.expansionProcess = $.grep($this.expansionProcess, function(r) {
                    return (r !== rowIndex);
                });
                this.fireRowCollapseEvent(row);
            }
            else {
                if(this.cfg.rowExpandMode === 'single') {
                    this.collapseAllRows();
                }

                if(iconOnly) {
                    toggler.addClass('ui-icon-circle-triangle-s').removeClass('ui-icon-circle-triangle-e').attr('aria-expanded', true);
                }
                else {
                    labels.eq(0).addClass('ui-helper-hidden');
                    labels.eq(1).removeClass('ui-helper-hidden');
                }

                this.loadExpandedRowContent(row);
            }
        }
    },

    /**
     * Loads the detailed content for the given expandable row.
     * @private
     * @param {JQuery} row A row with content to load.
     */
    loadExpandedRowContent: function(row) {
        // To check whether or not any hidden expansion content exists to avoid reloading multiple duplicate nodes in DOM
        var expansionContent = row.next('.ui-expanded-row-content');
        if(expansionContent.length > 0) {
            expansionContent.remove();
        }

        var $this = this,
        rowIndex = this.getRowMeta(row).index,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_rowExpansion', value: true},
                     {name: this.id + '_expandedRowIndex', value: rowIndex},
                     {name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_skipChildren', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            if(content && PrimeFaces.trim(content).length) {
                                row.addClass('ui-expanded-row');
                                this.rowExpansionLoaded(rowIndex);
                                this.displayExpandedRow(row, content);
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.expansionProcess = $.grep($this.expansionProcess, function(r) {
                    return r !== rowIndex;
                });
            }
        };

        if(this.hasBehavior('rowToggle')) {
            this.callBehavior('rowToggle', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Display the given HTML string in the specified row. Called mainly after an AJAX request.
     * @protected
     * @param {JQuery} row Row to display.
     * @param {string} content HTML string of the content to add to the row
     */
    displayExpandedRow: function(row, content) {
        row.after(content);
        this.updateRowspan(row);
        this.updateColspan(row.next());
    },

    /**
     * Calls the behaviors and event listeners when a row is collapsed.
     * @private
     * @param {JQuery} row A row of this data table.
     */
    fireRowCollapseEvent: function(row) {
        var rowIndex = this.getRowMeta(row).index;

        if(this.hasBehavior('rowToggle')) {
            var ext = {
                params: [
                    {name: this.id + '_collapsedRowIndex', value: rowIndex},
                    {name: this.id + '_skipChildren', value: true}
                ]
            };
            this.callBehavior('rowToggle', ext);
        }
    },

    /**
     * Collapses the given row, if it is expandable. Use `findRow` to get a row by its index. Does not update the row
     * expansion toggler button.
     * @protected
     * @param {JQuery} row Row to collapse.
     */
    collapseRow: function(row) {
        // #942: need to use "hide" instead of "remove" to avoid incorrect form mapping when a row is collapsed
        row.removeClass('ui-expanded-row').next('.ui-expanded-row-content').hide();
        this.updateRowspan(row);
    },

    /**
     * Collapses all rows that are currently expanded.
     */
    collapseAllRows: function() {
        var $this = this;

        this.getExpandedRows().each(function() {
            var expandedRow = $(this);
            $this.collapseRow(expandedRow);

            var columns = expandedRow.children('td');
            for(var i = 0; i < columns.length; i++) {
                var column = columns.eq(i),
                toggler = column.children('.ui-row-toggler');

                if(toggler.length > 0) {
                    if(toggler.hasClass('ui-icon')) {
                        toggler.addClass('ui-icon-circle-triangle-e').removeClass('ui-icon-circle-triangle-s');
                    }
                    else {
                        var labels = toggler.children('span');
                        labels.eq(0).removeClass('ui-helper-hidden');
                        labels.eq(1).addClass('ui-helper-hidden');
                    }
                    break;
                }
            }
        });
    },

    /**
     * Finds the list of row that are currently expanded.
     * @return {JQuery} All rows (`TR`) that are currently expanded.
     */
    getExpandedRows: function() {
        return this.tbody.children('.ui-expanded-row');
    },

    /**
     * Binds editor events non-obstrusively
     * @private
     */
    bindEditEvents: function() {
        var $this = this;
        this.cfg.saveOnCellBlur = (this.cfg.saveOnCellBlur === false) ? false : true;

        if(this.cfg.editMode === 'row') {
            var rowEditorSelector = '> tr > td > div.ui-row-editor > a';

            this.tbody.off('click.datatable focus.datatable blur.datatable', rowEditorSelector)
                        .on('click.datatable', rowEditorSelector, null, function(e) {
                            var element = $(this),
                            row = element.closest('tr');

                            if(element.hasClass('ui-row-editor-pencil')) {
                                $this.switchToRowEdit(row);
                                element.hide().siblings().show();
                            }
                            else if(element.hasClass('ui-row-editor-check')) {
                                $this.saveRowEdit(row);
                            }
                            else if(element.hasClass('ui-row-editor-close')) {
                                $this.cancelRowEdit(row);
                            }

                            e.preventDefault();
                        })
                        .on('focus.datatable', rowEditorSelector, null, function(e) {
                            $(this).addClass('ui-row-editor-outline');
                        })
                        .on('blur.datatable', rowEditorSelector, null, function(e) {
                            $(this).removeClass('ui-row-editor-outline');
                        });

            // GitHub #433 Allow ENTER to submit ESC to cancel row editor
            $(document).off("keydown.datatable", "tr.ui-row-editing")
                        .on("keydown.datatable", "tr.ui-row-editing", function(e) {
                            var keyCode = $.ui.keyCode;
                            switch (e.which) {
                                case keyCode.ENTER:
                                    var target = $(e.target);
                                    // GitHub #7028
                                    if(target.is("textarea")) {
                                         return true;
                                    }
                                    $(this).closest("tr").find(".ui-row-editor-check").trigger("click");
                                    return false; // prevents executing other event handlers (adding new row to the table)
                                case keyCode.ESCAPE:
                                    $(this).closest("tr").find(".ui-row-editor-close").trigger("click");
                                    return false;
                                default:
                                    break;
                }
            });
        }
        else if(this.cfg.editMode === 'cell') {
            var originalCellSelector = '> tr > td.ui-editable-column'
            cellSelector = this.cfg.cellSeparator || originalCellSelector,
            editEvent = (this.cfg.editInitEvent !== 'click') ? this.cfg.editInitEvent + '.datatable-cell click.datatable-cell' : 'click.datatable-cell';

            if (this.cfg.cellSeparator) {
                this.tbody.off(editEvent, originalCellSelector)
                    .on(editEvent, originalCellSelector, null, function (e) {
                        $this.incellClick = true;

                        if (!$(this).hasClass('ui-cell-editing') && e.type === $this.cfg.editInitEvent && $this.cfg.editInitEvent === "dblclick") {
                            $this.incellClick = false;
                        }
                    });
            }

            this.tbody.off(editEvent, cellSelector)
                        .on(editEvent, cellSelector, null, function(e) {
                            $this.incellClick = true;

                            var item = $(this),
                            cell = item.hasClass('ui-editable-column') ? item : item.closest('.ui-editable-column');

                            if(!cell.hasClass('ui-cell-editing') && e.type === $this.cfg.editInitEvent) {
                                $this.showCellEditor(cell);

                                if($this.cfg.editInitEvent === "dblclick") {
                                    $this.incellClick = false;
                                }
                            }
                        });

            $(document).off('click.datatable-cell-blur' + this.id)
                        .on('click.datatable-cell-blur' + this.id, function(e) {
                            var target = $(e.target);
                            if(!$this.incellClick && (target.is('.ui-input-overlay') || target.closest('.ui-input-overlay').length || target.closest('.ui-datepicker-buttonpane').length)) {
                                $this.incellClick = true;
                            }

                            if(!$this.incellClick && $this.currentCell && !$this.contextMenuClick && !$.datepicker._datepickerShowing && $('.p-datepicker-panel:visible').length === 0) {
                                if($this.cfg.saveOnCellBlur)
                                    $this.saveCell($this.currentCell);
                                else
                                    $this.doCellEditCancelRequest($this.currentCell);
                            }

                            $this.incellClick = false;
                            $this.contextMenuClick = false;
                        });
        }
    },

    /**
     * Switch all editable columns of the given row to their editing mode, if editing is enabled on this data table.
     * Use `findRow` to get a row by its index.
     * @param {JQuery} row A row (`TR`) to switch to edit mode.
     */
    switchToRowEdit: function(row) {
        // #1499 disable rowReorder while editing
        if (this.cfg.draggableRows) {
            this.tbody.sortable("disable");
        }

        if(this.cfg.rowEditMode === "lazy") {
            this.lazyRowEditInit(row);
        }
        else {
            this.showRowEditors(row);

            if(this.hasBehavior('rowEditInit')) {
                var rowIndex = this.getRowMeta(row).index;

                var ext = {
                    params: [{name: this.id + '_rowEditIndex', value: rowIndex}]
                };

                this.callBehavior('rowEditInit', ext);
            }
        }
    },

    /**
     * Shows the row editor(s) for the given row (and hides the normal output display).
     * @protected
     * @param {JQuery} row Row for which to show the row editor.
     */
    showRowEditors: function(row) {
        row.addClass('ui-state-highlight ui-row-editing').children('td.ui-editable-column').each(function() {
            var column = $(this);

            column.find('.ui-cell-editor-output').hide();
            column.find('.ui-cell-editor-input').show();
        });

        var inputs=row.find(':input:enabled');
        if (inputs.length > 0) {
            inputs.first().trigger('focus');
        }
    },

    /**
     * Finds the meta data for a given cell.
     * @param {JQuery} cell A cell for which to get the meta data.
     * @return {string} The meta data of the given cell or NULL if not found
     */
    getCellMeta: function(cell) {
        var rowMeta = this.getRowMeta(cell.closest('tr')),
            cellIndex = cell.index();

        if(this.cfg.scrollable && this.cfg.frozenColumns) {
            cellIndex = (this.scrollTbody.is(cell.closest('tbody'))) ? (cellIndex + $this.cfg.frozenColumns) : cellIndex;
        }

        if (rowMeta === undefined || rowMeta.index === undefined) {
            return null;
        }
        var cellInfo = rowMeta.index + ',' + cellIndex;
        if(rowMeta.key) {
            cellInfo = cellInfo + ',' + rowMeta.key;
        }

        return cellInfo;
    },

    /**
     * Initializes the given cell so that its content can be edited (when row editing is enabled)
     * @private
     * @param {JQuery} cell A cell of this data table to set up.
     */
    cellEditInit: function(cell) {
        var cellInfo = this.getCellMeta(cell),
        cellEditor = cell.children('.ui-cell-editor'),
        $this = this;

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            global: false,
            params: [{name: this.id + '_encodeFeature', value: true},
                    {name: this.id + '_cellEditInit', value: true},
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
     * When cell editing is enabled, shows the cell editor for the given cell that lets the user edit the cell content.
     * @param {JQuery} c A cell (`TD`) of this data table to edit.
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
                if (cellInfo) {
                    var ext = {
                        params: [{name: this.id + '_cellInfo', value: cellInfo}]
                    };
                    this.callBehavior('cellEditInit', ext);
                }
            }
        }
    },

    /**
     * Shows the cell editors for the given cell.
     * @private
     * @param {JQuery} cell A cell of this data table.
     */
    showCurrentCell: function(cell) {
        var $this = this;

        if(this.currentCell) {
            if(this.cfg.saveOnCellBlur)
                this.saveCell(this.currentCell);
            else if(!this.currentCell.is(cell))
                this.doCellEditCancelRequest(this.currentCell);
        }

        if(cell && cell.length) {
            this.currentCell = cell;

            var cellEditor = cell.children('div.ui-cell-editor'),
            displayContainer = cellEditor.children('div.ui-cell-editor-output'),
            inputContainer = cellEditor.children('div.ui-cell-editor-input'),
            inputs = inputContainer.find(':input:enabled'),
            multi = inputs.length > 1;

            cell.addClass('ui-state-highlight ui-cell-editing');
            displayContainer.hide();
            inputContainer.show();
            var input = inputs.eq(0);
            input.trigger('focus');
            input.trigger('select');

            //metadata
            if(multi) {
                var oldValues = [];
                for(var i = 0; i < inputs.length; i++) {
                    var input = inputs.eq(i);

                    if(input.is(':checkbox')) {
                        oldValues.push(input.val() + "_" + input.is(':checked'));
                    }
                    else {
                        oldValues.push(input.val());
                    }
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

                inputs.on('keydown.datatable-cell', function(e) {
                        var keyCode = $.ui.keyCode,
                        shiftKey = e.shiftKey,
                        key = e.which,
                        input = $(this);

                        if(key === keyCode.ENTER) {
                            // GitHub #7028
                            if(input.is("textarea")) {
                                return true;
                            }
                            $this.saveCell(cell);
                            $this.currentCell = null;

                            e.preventDefault();
                        }
                        else if(key === keyCode.TAB) {
                            if(multi) {
                                var focusIndex = shiftKey ? input.index() - 1 : input.index() + 1;

                                if(focusIndex < 0 || (focusIndex === inputs.length) || input.parent().hasClass('ui-inputnumber') || input.parent().hasClass('ui-helper-hidden-accessible')) {
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
                    .on('focus.datatable-cell click.datatable-cell', function(e) {
                        $this.currentCell = cell;
                    });
            }
        }
        else {
            this.currentCell = null;
        }
    },

    /**
     * Moves to the next or previous editable cell when the tab key was pressed.
     * @private
     * @param {JQuery} cell The currently focused cell
     * @param {boolean} forward `true` if tabbing forward, `false` otherwise.
     */
    tabCell: function(cell, forward) {
        var targetCell = forward ? cell.nextAll('td.ui-editable-column:first') : cell.prevAll('td.ui-editable-column:first');
        if(targetCell.length == 0) {
            var tabRow = forward ? cell.parent().next() : cell.parent().prev();
            targetCell = forward ? tabRow.children('td.ui-editable-column:first') : tabRow.children('td.ui-editable-column:last');
        }

        var cellEditor = targetCell.children('div.ui-cell-editor'),
        inputContainer = cellEditor.children('div.ui-cell-editor-input');

        if(inputContainer.length) {
            var inputs = inputContainer.find(':input'),
            disabledInputs = inputs.filter(':disabled');

            if(inputs.length === disabledInputs.length) {
                this.tabCell(targetCell, forward);
                return;
            }
        }

        this.showCellEditor(targetCell);
    },

    /**
     * After the user is done editing a cell, saves the content of the given cell and switches back to view mode.
     * @param {JQuery} cell A cell (`TD`) in edit mode.
     */
    saveCell: function(cell) {
        var inputs = cell.find('div.ui-cell-editor-input :input:enabled'),
        changed = false,
        valid = cell.data('valid'),
        $this = this;

        if(cell.data('multi-edit')) {
            var oldValues = cell.data('old-value');
            for(var i = 0; i < inputs.length; i++) {
                var input = inputs.eq(i),
                    inputVal = input.val(),
                    oldValue = oldValues[i];

                if(input.is(':checkbox') || input.is(':radio')) {
                    inputVal = inputVal + "_" + input.is(':checked');
                }

                if(inputVal != oldValue) {
                    changed = true;
                    break;
                }
            }
        }
        else {
            var input = inputs.eq(0),
                inputVal = input.val(),
                oldValue = cell.data('old-value');

            if(input.is(':checkbox') || input.is(':radio')) {
                inputVal = inputVal + "_" + input.is(':checked');
            }
            changed = (inputVal != oldValue);
        }

        if(changed || valid == false)
            $this.doCellEditRequest(cell);
        else
            $this.viewMode(cell);

        if(this.cfg.saveOnCellBlur) {
            this.currentCell = null;
        }
    },

    /**
     * Switches the given cell to its view mode (not editable).
     * @private
     * @param {JQuery} cell A cell of this data table.
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
     * When the users clicks on an editable cell, runs the AJAX request to show the inline editor for the given cell.
     * @private
     * @param {JQuery} cell The cell to switch to edit mode.
     */
    doCellEditRequest: function(cell) {
        var rowMeta = this.getRowMeta(cell.closest('tr')),
        cellEditor = cell.children('.ui-cell-editor'),
        cellEditorId = cellEditor.attr('id'),
        cellIndex = cell.index(),
        $this = this;

        if(this.cfg.scrollable && this.cfg.frozenColumns) {
            cellIndex = (this.scrollTbody.is(cell.closest('tbody'))) ? (cellIndex + $this.cfg.frozenColumns) : cellIndex;
        }

        var cellInfo = rowMeta.index + ',' + cellIndex;
        if(rowMeta.key) {
            cellInfo = cellInfo + ',' + rowMeta.key;
        }

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [{name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_cellInfo', value: cellInfo},
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
                if(args.validationFailed){
                    cell.data('valid', false);
                    cell.addClass('ui-state-error');
                }
                else{
                    cell.data('valid', true);
                    $this.viewMode(cell);
                }

                if($this.cfg.clientCache) {
                    $this.clearCacheMap();
                }
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
     * When the user wants to discard the edits to a cell, performs the required AJAX request for that.
     * @private
     * @param {JQuery} cell The cell in edit mode with changes to discard.
     */
    doCellEditCancelRequest: function(cell) {
        var rowMeta = this.getRowMeta(cell.closest('tr')),
        cellEditor = cell.children('.ui-cell-editor'),
        cellIndex = cell.index(),
        $this = this;

        if(this.cfg.scrollable && this.cfg.frozenColumns) {
            cellIndex = (this.scrollTbody.is(cell.closest('tbody'))) ? (cellIndex + $this.cfg.frozenColumns) : cellIndex;
        }

        var cellInfo = rowMeta.index + ',' + cellIndex;
        if(rowMeta.key) {
            cellInfo = cellInfo + ',' + rowMeta.key;
        }

        this.currentCell = null;

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [{name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_cellEditCancel', value: true},
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

                if($this.cfg.clientCache) {
                    $this.clearCacheMap();
                }
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
     * When the given row is currently being edited, saves the contents of the edited row and switch back to view mode.
     * Use `findRow` to get a row by its index.
     * @param {JQuery} rowEditor A row (`TR`) in edit mode to save.
     */
    saveRowEdit: function(rowEditor) {
        this.doRowEditRequest(rowEditor, 'save');
    },

    /**
     * When the given row is currently being edited, cancel the editing operation and discard the entered data. Use
     * `findRow` to get a row by its index.
     * @param {JQuery} rowEditor A row (`TR`) in edit mode.
     */
    cancelRowEdit: function(rowEditor) {
        this.doRowEditRequest(rowEditor, 'cancel');
    },

    /**
     * Sends an AJAX request to handle row save or cancel
     * @private
     * @param {JQuery} rowEditor The curent row editor
     * @param {PrimeFaces.widget.DataTable.RowEditAction} action Whether to save or cancel the row edit.
     */
    doRowEditRequest: function(rowEditor, action) {
        var row = rowEditor.closest('tr'),
        rowIndex = this.getRowMeta(row).index,
        expanded = row.hasClass('ui-expanded-row'),
        $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_rowEditIndex', value: this.getRowMeta(row).index},
                     {name: this.id + '_rowEditAction', value: action},
                     {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            if(expanded) {
                                this.collapseRow(row);
                            }

                            this.updateRow(row, content);

                            // #1499 enable rowReorder when done editing
                            if (this.cfg.draggableRows && $('tr.ui-row-editing').length === 0) {
                                this.tbody.sortable("enable");
                            }

                            // #258 must reflow after editing
                            this.postUpdateData();
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                if(args && args.validationFailed) {
                    $this.invalidateRow(rowIndex);
                }
                else {
                    if($this.cfg.rowEditMode === "lazy") {
                        var index = ($this.paginator) ? (rowIndex % $this.paginator.getRows()) : rowIndex,
                        newRow = $this.tbody.children('tr').eq(index);
                        $this.getRowEditors(newRow).children('.ui-cell-editor-input').children().remove();
                    }
                }

                if($this.cfg.clientCache) {
                    $this.clearCacheMap();
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
     * Performs the required initialization for making a row editable. Only called on-demand when the row actually needs
     * to be edited.
     * @private
     * @param {JQuery} row A row of this data table.
     */
    lazyRowEditInit: function(row) {
        var rowIndex = this.getRowMeta(row).index,
        $this = this;

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            global: false,
            params: [{name: this.id + '_encodeFeature', value: true},
                    {name: this.id + '_rowEditInit', value: true},
                    {name: this.id + '_rowEditIndex', value: rowIndex}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            $this.updateRow(row, content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                var index = ($this.paginator) ? (rowIndex % $this.paginator.getRows()) : rowIndex,
                newRow = $this.tbody.children('tr').eq(index);
                $this.showRowEditors(newRow);
            }
        };

        if(this.hasBehavior('rowEditInit')) {
            this.cfg.behaviors['rowEditInit'].call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Updates a row with the given content
     * @protected
     * @param {JQuery} row Row to update.
     * @param {string} content HTML string to set on the row.
     */
    updateRow: function(row, content) {
        row.replaceWith(content);
    },

    /**
     * Displays row editors in invalid format.
     * @protected
     * @param {number} index 0-based index of the row to invalidate.
     */
    invalidateRow: function(index) {
        var i = (this.paginator) ? (index % this.paginator.getRows()) : index;
        this.tbody.children('tr[data-ri]').eq(i).addClass('ui-widget-content ui-row-editing ui-state-error');
    },

    /**
     * Finds all editors of a row. Usually each editable column has got an editor.
     * @protected
     * @param {JQuery} row A row for which to find its row editors.
     * @return {JQuery} A list of row editors for each editable column of the given row
     */
    getRowEditors: function(row) {
        return row.find('div.ui-cell-editor');
    },

    /**
     * Returns the paginator instance if any exists.
     * @return {PrimeFaces.widget.Paginator | undefined} The paginator instance for this widget, or `undefined` if
     * paging is not enabled.
     */
    getPaginator: function() {
        return this.paginator;
    },

    /**
     * Writes selected row ids to state holder
     * @private
     */
    writeSelections: function() {
        $(this.selectionHolder).val(this.selection.join(','));
    },

    /**
     * Checks whether only one row may be selected at a time.
     * @return {boolean} `true` if selection mode is set to `single`, or `false` otherwise.
     */
    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },

    /**
     * Checks whether multiples rows may be selected at a time.
     * @return {boolean} `true` if selection mode is set to `multiple`, or `false` otherwise.
     */
    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple' || this.isCheckboxSelectionEnabled();
    },

    /**
     * Clears the saved list of selected rows.
     * @private
     */
    clearSelection: function() {
        this.selection = [];

        $(this.selectionHolder).val('');
    },

    /**
     * Checks whether the user may select the rows of this data table.
     * @return {boolean} `true` is rows may be selected, or `false` otherwise.
     */
    isSelectionEnabled: function() {
        return this.cfg.selectionMode != undefined || this.cfg.columnSelectionMode != undefined;
    },

    /**
     * Checks whether the rows of this data table are selected via checkboxes.
     * @return {boolean} `true` if selection mode is set to `checkbox`, or `false` otherwise.
     */
    isCheckboxSelectionEnabled: function() {
        return this.cfg.selectionMode === 'checkbox';
    },

    /**
     * Checks whether the rows of this data table are selected via radio buttons.
     * @return {boolean} `true` if selection mode is set to `radio`, or `false` otherwise.
     */
    isRadioSelectionEnabled: function() {
        return this.cfg.selectionMode === 'radio';
    },

    /**
     * Clears all table filters and shows all rows that may have been hidden by filters.
     */
    clearFilters: function() {
        this.thead.find('> tr > th.ui-filter-column > .ui-column-filter').val('');
        this.thead.find('> tr > th.ui-filter-column > .ui-column-customfilter :input').val('');
        $(this.jqId + '\\:globalFilter').val('');

        this.filter();
    },

    /**
     * Sets up the event listeners to enable columns to be resized.
     * @private
     */
    setupResizableColumns: function() {
        this.cfg.resizeMode = this.cfg.resizeMode||'fit';

        this.fixColumnWidths();

        this.hasColumnGroup = this.hasColGroup();
        if(this.hasColumnGroup) {
            this.addGhostRow();
        }

        if(!this.cfg.liveResize) {
            this.resizerHelper = $('<div class="ui-column-resizer-helper ui-state-highlight"></div>').appendTo(this.jq);
        }

        this.addResizers();

        var resizers = this.thead.find('> tr > th > span.ui-column-resizer'),
        $this = this;

        resizers.draggable({
            axis: 'x',
            start: function(event, ui) {
                ui.helper.data('originalposition', ui.helper.offset());

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
                ui.helper.css({
                    'left': '',
                    'top': '0px'
                });

                if($this.cfg.liveResize) {
                    $this.jq.css('cursor', 'default');
                } else {
                    $this.resize(event, ui);
                    $this.resizerHelper.hide();
                }

                if($this.cfg.resizeMode === 'expand') {
                    setTimeout(function() {
                        $this.fireColumnResizeEvent(ui.helper.parent());
                    }, 5);
                }
                else {
                    $this.fireColumnResizeEvent(ui.helper.parent());
                }

                if($this.cfg.stickyHeader) {
                    $this.reclone();
                }
            },
            containment: this.cfg.resizeMode === "expand" ? "document" : this.jq
        });
    },

    /**
     * Invokes the behaviors and event listeners when a column is resized.
     * @private
     * @param {JQuery} columnHeader Header of the column which was resized.
     */
    fireColumnResizeEvent: function(columnHeader) {
        if(this.hasBehavior('colResize')) {
            var options = {
                source: this.id,
                process: this.id,
                params: [
                    {name: this.id + '_colResize', value: true},
                    {name: this.id + '_columnId', value: columnHeader.attr('id')},
                    {name: this.id + '_width', value: parseInt(columnHeader.width())},
                    {name: this.id + '_height', value: parseInt(columnHeader.height())}
                ]
            };

            this.callBehavior('colResize', options);
        }
    },

    /**
     * Checks whether this data table has got any column groups.
     * @protected
     * @return {boolean} `true` if this data table has got any column groups, or `false` otherwise.
     */
    hasColGroup: function() {
        return this.thead.children('tr').length > 1;
    },

    /**
     * Adds and sets up an invisible row for internal purposes.
     * @protected
     */
    addGhostRow: function() {
        var firstRow = this.tbody.find('tr:first');
        if(firstRow.hasClass('ui-datatable-empty-message')) {
            return;
        }

        var columnsOfFirstRow = firstRow.children('td'),
        dataColumnsCount = columnsOfFirstRow.length,
        columnMarkup = '';

        for(var i = 0; i < dataColumnsCount; i++) {
            var colWidth = columnsOfFirstRow.eq(i).width() + 1,
            id = this.id + '_ghost_' + i;

            if (this.resizableState) {
                colWidth = this.findColWidthInResizableState(id) || colWidth;
            }

            columnMarkup += '<th id="' + id + '" style="height:0px;border-bottom-width: 0px;border-top-width: 0px;padding-top: 0px;padding-bottom: 0px;outline: 0 none; width:' + colWidth + 'px" class="ui-resizable-column"></th>';
        }

        this.thead.prepend('<tr>' + columnMarkup + '</tr>');

        if(this.cfg.scrollable) {
            this.theadClone.prepend('<tr>' + columnMarkup + '</tr>');
            this.footerTable.children('tfoot').prepend('<tr>' + columnMarkup + '</tr>');
        }
    },

    /**
     * Finds the group resizer element for the given drag event data.
     * @protected
     * @param {JQueryUI.DraggableEventUIParams} ui Data for the drag event.
     * @return {JQuery|null} The resizer DOM element.
     */
    findGroupResizer: function(ui) {
        for(var i = 0; i < this.groupResizers.length; i++) {
            var groupResizer = this.groupResizers.eq(i);
            if(groupResizer.offset().left === ui.helper.data('originalposition').left) {
                return groupResizer;
            }
        }

        return null;
    },

    /**
     * Adds the resizers for change the width of a column of this data table.
     * @protected
     */
    addResizers: function() {
        var resizableColumns = this.thead.find('> tr > th.ui-resizable-column');
        resizableColumns.prepend('<span class="ui-column-resizer">&nbsp;</span>');

        if(this.cfg.resizeMode === 'fit') {
            resizableColumns.filter(':last-child').children('span.ui-column-resizer').hide();
        }

        if(this.hasColumnGroup) {
            this.groupResizers = this.thead.find('> tr:first > th > .ui-column-resizer');
        }
    },

    /**
     * Resizes this data table, row, or columns in response to a drag event of a resizer element.
     * @protected
     * @param {JQuery.TriggeredEvent} event Event triggered for the drag.
     * @param {JQueryUI.DraggableEventUIParams} ui Data for the drag event.
     */
    resize: function(event, ui) {
        var columnHeader, nextColumnHeader, change = null, newWidth = null, nextColumnWidth = null,
        expandMode = (this.cfg.resizeMode === 'expand'),
        table = this.thead.parent(),
        $this = this;

        if(this.hasColumnGroup) {
            var groupResizer = this.findGroupResizer(ui);
            if(!groupResizer) {
                return;
            }

            columnHeader = groupResizer.parent();
        }
        else {
            columnHeader = ui.helper.parent();
        }

        var title = columnHeader.children('.ui-column-title');
        if(PrimeFaces.env.isIE()) {
            title.css('display', 'none');
        }

        var nextColumnHeader = columnHeader.nextAll(':visible:first');

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

        var minWidth = parseInt(columnHeader.css('min-width'));
        minWidth = (minWidth == 0) ? 15 : minWidth;

        if(PrimeFaces.env.isIE()) {
            title.css('display', '');
        }

        if((newWidth > minWidth && nextColumnWidth > minWidth) || (expandMode && newWidth > minWidth)) {
            if(expandMode) {
                table.width(table.width() + change);
                setTimeout(function() {
                    columnHeader.width(newWidth);
                    $this.updateResizableState(columnHeader, nextColumnHeader, table, newWidth, null);
                }, 1);
            }
            else {
                columnHeader.width(newWidth);
                nextColumnHeader.width(nextColumnWidth);
                this.updateResizableState(columnHeader, nextColumnHeader, table, newWidth, nextColumnWidth);
            }

            if(this.cfg.scrollable) {
                var cloneTable = this.theadClone.parent(),
                colIndex = columnHeader.index();

                if(expandMode) {
                    //body
                    cloneTable.width(cloneTable.width() + change);

                    //footer
                    this.footerTable.width(this.footerTable.width() + change);

                    setTimeout(function() {
                        if($this.hasColumnGroup) {
                            $this.theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth);            //body
                            $this.footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);   //footer
                        }
                        else {
                            $this.theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);   //body
                            $this.footerCols.eq(colIndex).width(newWidth);                                                          //footer
                        }
                    }, 1);
                }
                else {
                    if(this.hasColumnGroup) {
                        //body
                        this.theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth);
                        this.theadClone.find('> tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);

                        //footer
                        this.footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);
                        this.footerTable.find('> tfoot > tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);
                    }
                    else {
                        //body
                        this.theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);
                        this.theadClone.find(PrimeFaces.escapeClientId(nextColumnHeader.attr('id') + '_clone')).width(nextColumnWidth);

                        //footer
                        if(this.footerCols.length > 0) {
                            var footerCol = this.footerCols.eq(colIndex),
                            nextFooterCol = footerCol.next();

                            footerCol.width(newWidth);
                            nextFooterCol.width(nextColumnWidth);
                        }
                    }
                }
            }
        }
    },

    /**
     * Remove given row from the list of selected rows.
     * @private
     * @param {string} rowKey Key of the row to remove.
     */
    removeSelection: function(rowKey) {
        if(this.selection.includes('@all')) {
            // GitHub #3535 if @all was previously selected just select values on page
            this.clearSelection();
            var rows = this.tbody.children('tr');
            for(var i = 0; i < rows.length; i++) {
                var rowMeta = this.getRowMeta(rows.eq(i));
                if(rowMeta.key !== rowKey) {
                    this.addSelection(rowMeta.key);
                }
            }
        }
        else {
            this.selection = $.grep(this.selection, function(value) {
                return value !== rowKey;
            });
        }
    },

    /**
     * Adds given row to the list of selected rows.
     * @private
     * @param {number} rowKey Key of the row to add.
     */
    addSelection: function(rowKey) {
        if(!this.isSelected(rowKey)) {
            this.selection.push(rowKey);
        }
    },

    /**
     * Checks whether the given row is currently selected.
     * @param {string} rowKey The key of a row from this data table.
     * @return {boolean} `true` if the given row is currently selected, or `false` otherwise.
     */
    isSelected: function(rowKey) {
        return PrimeFaces.inArray(this.selection, rowKey);
    },

    /**
     * Finds the index and the row key for the given row.
     * @param {JQuery} row The element (`TR`) of a row of this data table.
     * @return {PrimeFaces.widget.DataTable.RowMeta} The meta for the row with the index and the row key.
     */
    getRowMeta: function(row) {
        var meta = {
            index: row.data('ri'),
            key:  row.attr('data-rk')
        };

        return meta;
    },

    /**
     * Sets up all event listeners required for making column draggable and reorderable.
     * @private
     */
    setupDraggableColumns: function() {
        this.orderStateHolder = $(this.jqId + '_columnOrder');
        this.saveColumnOrder();

        this.dragIndicatorTop = $('<span class="ui-icon ui-icon-arrowthick-1-s" style="position:absolute"></span>').hide().appendTo(this.jq);
        this.dragIndicatorBottom = $('<span class="ui-icon ui-icon-arrowthick-1-n" style="position:absolute"></span>').hide().appendTo(this.jq);

        var $this = this;

        $(this.jqId + ' thead th.ui-draggable-column').draggable({
            appendTo: 'body',
            opacity: 0.75,
            cursor: 'move',
            scope: this.id,
            cancel: ':input,.ui-column-resizer',
            start: function(event, ui) {
                ui.helper.css('z-index', PrimeFaces.nextZindex());
            },
            drag: function(event, ui) {
                var droppable = ui.helper.data('droppable-column');

                if(droppable) {
                    var droppableOffset = droppable.offset(),
                    topArrowY = droppableOffset.top - 10,
                    bottomArrowY = droppableOffset.top + droppable.height() + 8,
                    arrowX = null;

                    //calculate coordinates of arrow depending on mouse location
                    if(event.originalEvent.pageX >= droppableOffset.left + (droppable.width() / 2)) {
                        var nextDroppable = droppable.next();
                        if(nextDroppable.length == 1)
                            arrowX = nextDroppable.offset().left - 9;
                        else
                            arrowX = droppable.offset().left + droppable.innerWidth() - 9;

                        ui.helper.data('drop-location', 1);     //right
                    }
                    else {
                        arrowX = droppableOffset.left  - 9;
                        ui.helper.data('drop-location', -1);    //left
                    }

                    $this.dragIndicatorTop.offset({
                        'left': arrowX,
                        'top': topArrowY - 3
                    }).show();

                    $this.dragIndicatorBottom.offset({
                        'left': arrowX,
                        'top': bottomArrowY - 3
                    }).show();
                }
            },
            stop: function(event, ui) {
                //hide dnd arrows
                $this.dragIndicatorTop.css({
                    'left':'0px',
                    'top':'0px'
                }).hide();

                $this.dragIndicatorBottom.css({
                    'left':'0px',
                    'top':'0px'
                }).hide();
            },
            helper: function() {
                var header = $(this),
                helper = $('<div class="ui-widget ui-state-default" style="padding:4px 10px;text-align:center;"></div>');

                helper.width(header.width());
                helper.height(header.height());

                helper.html(header.html());

                return helper.get(0);
            }
        }).droppable({
            hoverClass:'ui-state-highlight',
            tolerance:'pointer',
            scope: this.id,
            over: function(event, ui) {
                ui.helper.data('droppable-column', $(this));
            },
            drop: function(event, ui) {
                var draggedColumnHeader = ui.draggable,
                dropLocation = ui.helper.data('drop-location'),
                droppedColumnHeader =  $(this),
                draggedColumnFooter = null,
                droppedColumnFooter = null;

                var draggedCells = $this.tbody.find('> tr:not(.ui-expanded-row-content) > td:nth-child(' + (draggedColumnHeader.index() + 1) + ')'),
                droppedCells = $this.tbody.find('> tr:not(.ui-expanded-row-content) > td:nth-child(' + (droppedColumnHeader.index() + 1) + ')');

                if($this.tfoot.length) {
                    var footerColumns = $this.tfoot.find('> tr > td'),
                    draggedColumnFooter = footerColumns.eq(draggedColumnHeader.index()),
                    droppedColumnFooter = footerColumns.eq(droppedColumnHeader.index());
                }

                //drop right
                if(dropLocation > 0) {
                    if($this.cfg.resizableColumns) {
                        if(droppedColumnHeader.next().length) {
                            droppedColumnHeader.children('span.ui-column-resizer').show();
                            draggedColumnHeader.children('span.ui-column-resizer').hide();
                        }
                    }

                    draggedColumnHeader.insertAfter(droppedColumnHeader);

                    draggedCells.each(function(i, item) {
                        $(this).insertAfter(droppedCells.eq(i));
                    });

                    if(draggedColumnFooter && droppedColumnFooter) {
                        draggedColumnFooter.insertAfter(droppedColumnFooter);
                    }

                    //sync clone
                    if($this.cfg.scrollable) {
                        var draggedColumnClone = $(document.getElementById(draggedColumnHeader.attr('id') + '_clone')),
                        droppedColumnClone = $(document.getElementById(droppedColumnHeader.attr('id') + '_clone'));
                        draggedColumnClone.insertAfter(droppedColumnClone);
                    }
                }
                //drop left
                else {
                    draggedColumnHeader.insertBefore(droppedColumnHeader);

                    draggedCells.each(function(i, item) {
                        $(this).insertBefore(droppedCells.eq(i));
                    });

                    if(draggedColumnFooter && droppedColumnFooter) {
                        draggedColumnFooter.insertBefore(droppedColumnFooter);
                    }

                    //sync clone
                    if($this.cfg.scrollable) {
                        var draggedColumnClone = $(document.getElementById(draggedColumnHeader.attr('id') + '_clone')),
                        droppedColumnClone = $(document.getElementById(droppedColumnHeader.attr('id') + '_clone'));
                        draggedColumnClone.insertBefore(droppedColumnClone);
                    }
                }

                //save order
                $this.saveColumnOrder();

                //fire colReorder event
                if($this.hasBehavior('colReorder')) {
                    var ext = null;

                    if($this.cfg.multiViewState) {
                        ext = {
                            params: [{name: this.id + '_encodeFeature', value: true}]
                        };
                    }

                    $this.callBehavior('colReorder', ext);
                }
            }
        });

        // GitHub #5013 Frozen Columns should not be draggable/droppable
        if($this.cfg.frozenColumns) {
            var frozenHeaders = this.frozenThead.find('.ui-frozen-column');
            frozenHeaders.draggable('disable');
            frozenHeaders.droppable('disable');
            frozenHeaders.disableSelection();
        }
    },

    /**
     * Saves the current column order, used to preserve the state between AJAX updates etc.
     * @protected
     */
    saveColumnOrder: function() {
        var columnIds = [],
        columns = $(this.jqId + ' thead:first th');

        columns.each(function(i, item) {
            columnIds.push($(item).attr('id'));
        });

        this.orderStateHolder.val(columnIds.join(','));
    },

    /**
     * Makes the rows of this data table draggable via JQueryUI.
     * @private
     */
    makeRowsDraggable: function() {
        var $this = this,
        draggableHandle = this.cfg.rowDragSelector||'td,span:not(.ui-c)';

        this.tbody.sortable({
            placeholder: 'ui-datatable-rowordering ui-state-active',
            cursor: 'move',
            handle: draggableHandle,
            appendTo: document.body,
            start: function(event, ui) {
                ui.helper.css('z-index', PrimeFaces.nextZindex());
            },
            helper: function(event, ui) {
                var cells = ui.children(),
                helper = $('<div class="ui-datatable ui-widget"><table><tbody class="ui-datatable-data"></tbody></table></div>'),
                helperRow = ui.clone(),
                helperCells = helperRow.children();

                for(var i = 0; i < helperCells.length; i++) {
                    var helperCell = helperCells.eq(i);
                    helperCell.width(cells.eq(i).width());
                    // #5584 reflow must remove column title span
                    helperCell.children().remove('.ui-column-title');
                }

                helperRow.appendTo(helper.find('tbody'));

                return helper;
            },
            update: function(event, ui) {
                var fromIndex = ui.item.data('ri'),
                fromNode = ui.item;
                itemIndex = ui.item.index(),
                toIndex = $this.paginator ? $this.paginator.getFirst() + itemIndex : itemIndex;
                isDirectionUp = fromIndex >= toIndex;

                // #5296 must not count header group rows
                // #6557 must not count expanded rows
                if (isDirectionUp) {
                    for (i = 0; i <= toIndex; i++) {
                        fromNode = fromNode.next('tr');
                        if (fromNode.hasClass('ui-rowgroup-header') || fromNode.hasClass('ui-expanded-row-content')){
                            toIndex--;
                        }
                    }
                } else {
                    fromNode.prevAll('tr').each(function() {
                        var node = $(this);
                        if (node.hasClass('ui-rowgroup-header') || node.hasClass('ui-expanded-row-content')){
                            toIndex--;
                        }
                    });
                }
                toIndex = Math.max(toIndex, 0);

                $this.syncRowParity();

                var options = {
                    source: $this.id,
                    process: $this.id,
                    params: [
                        {name: $this.id + '_rowreorder', value: true},
                        {name: $this.id + '_fromIndex', value: fromIndex},
                        {name: $this.id + '_toIndex', value: toIndex},
                        {name: this.id + '_skipChildren', value: true}
                    ]
                }

                if($this.hasBehavior('rowReorder')) {
                    $this.callBehavior('rowReorder', options);
                }
                else {
                    PrimeFaces.ajax.Request.handle(options);
                }
            },
            change: function(event, ui) {
                if($this.cfg.scrollable) {
                    PrimeFaces.scrollInView($this.scrollBody, ui.placeholder);
                }
            }
        });
    },

    /**
     * Sets the style class on each, depending whether it is an even-numbered or odd-numbered row.
     * @private
     */
    syncRowParity: function() {
        var rows = this.tbody.children('tr.ui-widget-content'),
        first = this.paginator ? this.paginator.getFirst(): 0;

        for(var i = first; i < rows.length; i++) {
            var row = rows.eq(i);

            row.data('ri', i).removeClass('ui-datatable-even ui-datatable-odd');

            if(i % 2 === 0)
                row.addClass('ui-datatable-even');
            else
                row.addClass('ui-datatable-odd');

        }
    },

    /**
     * Checks whether this data table has got any rows. When there are no rows, usually the message `no items found` is
     * shown.
     * @return {boolean} `true` if this data table has got no rows, `false` otherwise.
     */
    isEmpty: function() {
        return this.tbody.children('tr.ui-datatable-empty-message').length === 1;
    },

    /**
     * Finds the number of rows that are selected.
     * @return {number} The number of rows that are currently selected.
     */
    getSelectedRowsCount: function() {
        return this.isSelectionEnabled() ? this.selection.length : 0;
    },

    /**
     * Updates the `check all` checkbox in the header of this data table.
     * @private
     */
    updateHeaderCheckbox: function() {
        if(this.isEmpty()) {
            this.uncheckHeaderCheckbox();
            this.disableHeaderCheckbox();
        }
        else if(!this.cfg.selectionPageOnly) {
            if(this.selection.includes('@all')) {
                this.enableHeaderCheckbox();
                this.checkHeaderCheckbox();
            }
        }
        else {
            var checkboxes, selectedCheckboxes, enabledCheckboxes, disabledCheckboxes;

            if(this.cfg.nativeElements) {
                checkboxes = this.tbody.find('> tr > td.ui-selection-column > :checkbox');
                enabledCheckboxes = checkboxes.filter(':enabled');
                disabledCheckboxes = checkboxes.filter(':disabled');
                selectedCheckboxes = enabledCheckboxes.filter(':checked');
            }
            else {
                checkboxes = this.tbody.find('> tr > td.ui-selection-column > div.ui-chkbox > .ui-chkbox-box');
                enabledCheckboxes = checkboxes.filter(':not(.ui-state-disabled)');
                disabledCheckboxes = checkboxes.filter('.ui-state-disabled');
                selectedCheckboxes = checkboxes.filter("div[aria-checked='true']");
            }

            if(enabledCheckboxes.length && enabledCheckboxes.length === selectedCheckboxes.length)
               this.checkHeaderCheckbox();
            else
               this.uncheckHeaderCheckbox();

            if(checkboxes.length === disabledCheckboxes.length)
               this.disableHeaderCheckbox();
            else
               this.enableHeaderCheckbox();
        }
    },

    /**
     * Checks the `select all` checkbox in the header of this data table.
     * @private
     */
    checkHeaderCheckbox: function() {
        if(this.cfg.nativeElements) {
            this.checkAllToggler.prop('checked', true);
        }
        else {
            this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
            this.checkAllToggler.attr('aria-checked', true);
        }
    },

    /**
     * Unchecks the `select all` checkbox in the header of this data table.
     * @private
     */
    uncheckHeaderCheckbox: function() {
        if(this.cfg.nativeElements) {
            this.checkAllToggler.prop('checked', false);
        }
        else {
            this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            this.checkAllToggler.attr('aria-checked', false);
        }
    },

    /**
     * Disables the `select all` checkbox in the header of this data table.
     * @private
     */
    disableHeaderCheckbox: function() {
        if(this.cfg.nativeElements)
            this.checkAllToggler.prop('disabled', true);
        else
            this.checkAllToggler.addClass('ui-state-disabled');
    },

    /**
     * Enables the `select all` checkbox in the header of this data table.
     * @private
     */
    enableHeaderCheckbox: function() {
        if(this.cfg.nativeElements)
            this.checkAllToggler.prop('disabled', false);
        else
            this.checkAllToggler.removeClass('ui-state-disabled');
    },

    /**
     * Applies the styling and event listeners required for the sticky headers feature.
     * @private
     */
    setupStickyHeader: function() {
        var table = this.thead.parent(),
        offset = table.offset(),
        win = $(window),
        $this = this,
        orginTableContent = this.jq.find('> .ui-datatable-tablewrapper > table'),
        fixedElementsOnTop = this.cfg.stickyTopAt ? $(this.cfg.stickyTopAt) : null,
        fixedElementsHeight = 0;

        if (fixedElementsOnTop && fixedElementsOnTop.length) {
            for (var i = 0; i < fixedElementsOnTop.length; i++) {
                fixedElementsHeight += fixedElementsOnTop.eq(i).outerHeight();
            }
        }

        this.stickyContainer = $('<div class="ui-datatable ui-datatable-sticky ui-widget"><table></table></div>');
        this.clone = this.thead.clone(false);
        this.stickyContainer.children('table').append(this.thead);
        table.prepend(this.clone);

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

        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id, function() {
            var scrollTop = win.scrollTop(),
            tableOffset = table.offset();

            if(scrollTop + fixedElementsHeight > tableOffset.top) {
                $this.stickyContainer.css({
                                        'position': 'fixed',
                                        'top': fixedElementsHeight + 'px'
                                    })
                                    .addClass('ui-shadow ui-sticky');

                if($this.cfg.resizableColumns) {
                    $this.relativeHeight = (scrollTop + fixedElementsHeight) - tableOffset.top;
                }

                if(scrollTop + fixedElementsHeight >= (tableOffset.top + $this.tbody.height()))
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

        PrimeFaces.utils.registerResizeHandler(this, 'resize.sticky-' + this.id, null, function(e) {
            var _delay = e.data.delay;

            if (_delay !== null && typeof _delay === 'number' && _delay > -1) {
                if ($this.resizeTimeout) {
                    clearTimeout($this.resizeTimeout);
                }

                $this.stickyContainer.hide();
                $this.resizeTimeout = setTimeout(function() {
                    $this.stickyContainer.css('left', orginTableContent.offset().left + 'px');
                    $this.stickyContainer.width(table.outerWidth());
                    $this.stickyContainer.show();
                }, _delay);
            }
            else {
                $this.stickyContainer.width(table.outerWidth());
            }
        }, { delay: null });

        //filter support
        this.clone.find('.ui-column-filter').prop('disabled', true);
    },

    /**
     * Initializes the expansion state
     * @private
     */
    initRowExpansion: function() {
        var $this = this;

        this.expansionHolder = $(this.jqId + '_rowExpansionState');
        this.loadedExpansionRows = this.tbody.children('.ui-expanded-row-content').prev().map(function() {
            return $this.getRowMeta($(this)).index;
        }).get();

        this.writeRowExpansions();
    },

    /**
     * Write row expansion state.
     * @private
     */
    writeRowExpansions: function() {
        this.expansionHolder.val(this.loadedExpansionRows.join(','));
    },

    /**
     * Detect if row expansion for this row has been loaded and if not load it.
     * @protected
     * @param {number} rowIndex The row index to check for expansion
     */
    rowExpansionLoaded: function(rowIndex) {
        if(!PrimeFaces.inArray(this.loadedExpansionRows, rowIndex)) {
            this.loadedExpansionRows.push(rowIndex);
            this.writeRowExpansions();
        }
    },

    /**
     * Finds the body of this data table with the property that the user can focus it.
     * @protected
     * @return {JQuery} The body of this data table.
     */
    getFocusableTbody: function() {
        return this.tbody;
    },

    /**
     * Removes the current clone of the table header from the DOM, and creates a new clone.
     * @private
     */
    reclone: function() {
        this.clone.remove();
        this.clone = this.thead.clone(false);
        this.jq.find('.ui-datatable-tablewrapper > table').prepend(this.clone);
    },

    /**
     * Fetches the last row from the backend and inserts a row instead of updating the table itself.
     */
    addRow: function() {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [{name: this.id + '_addrow', value: true},
                    {name: this.id + '_skipChildren', value: true},
                    {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        this.tbody.append(content);
                    }
                });

                if ($this.isEmpty()) {
                    $this.tbody.children('tr.ui-datatable-empty-message').remove();
                }

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    /**
     * Clears all cached rows so that they are loaded from the server the next time they are requested.
     * @private
     */
    clearCacheMap: function() {
        this.cacheMap = {};
    },

    /**
     * Loads the data for the given page and displays it. When some rows exist in the cache, do not reload them from the
     * server.
     * @param {PrimeFaces.widget.Paginator.PaginationState} newState The new values for the current page and the rows
     * per page count.
     * @private
     */
    loadDataWithCache: function(newState) {
        var isRppChanged = false;
        if(this.cacheRows != newState.rows) {
            this.clearCacheMap();
            this.cacheRows = newState.rows;
            isRppChanged = true;
        }

        var pageFirst = newState.first,
            nextPageFirst = newState.rows + pageFirst,
            lastPageFirst = this.cfg.paginator.pageCount * newState.rows,
            hasNextPage = (!this.cacheMap[nextPageFirst]) && nextPageFirst < lastPageFirst;

        if(this.cacheMap[pageFirst] && !isRppChanged) {
            this.updateData(this.cacheMap[pageFirst]);
            this.paginator.cfg.page = newState.page;
            this.paginator.updateUI();

            if(!hasNextPage) {
                this.updatePageState(newState);
            }
        }
        else {
            this.paginate(newState);
        }

        if(hasNextPage) {
            this.fetchNextPage(newState);
        }
    },

    /**
     * Reflow mode is a responsive mode to display columns as stacked depending on screen size. Updates the reflow for
     * the given header.
     * @private
     * @param {JQuery} columnHeader Header of a column to update.
     * @param {number} sortOrder Sort order of the column.
     */
    updateReflowDD: function(columnHeader, sortOrder) {
        if(this.reflowDD && this.cfg.reflow) {
            var options = this.reflowDD.children('option'),
            orderIndex = sortOrder > 0 ? 0 : 1;
            var header = columnHeader.text();
            var filterby = header.indexOf("Filter by");
            if (filterby !== -1) {
                header = header.substring(0, filterby);
            }
            header = $.escapeSelector(header);

            options.each(function() {
                var optionText = $.escapeSelector(this.text);
                this.selected = optionText.startsWith(header) && this.value.endsWith("_" + orderIndex);
            });
        }
    },

    /**
     * When row grouping is enabled, groups all rows accordingly.
     * @protected
     */
    groupRows: function() {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < this.cfg.groupColumnIndexes.length; i++) {
            this.groupRow(this.cfg.groupColumnIndexes[i], rows);
        }

        rows.children('td.ui-duplicated-column').remove();
    },

    /**
     * Called by `groupRows`, this method performs the grouping of a single set of rows that belong to one row group.
     * @private
     * @param {number} colIndex Index of the column to group.
     * @param {JQuery} rows Rows to group into one row group.
     */
    groupRow: function(colIndex, rows) {
        var groupStartIndex = null, rowGroupCellData = null, rowGroupCount = null;

        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            var column = row.children('td').eq(colIndex);
            var columnData = column.text();
            if(rowGroupCellData != columnData) {
                groupStartIndex = i;
                rowGroupCellData = columnData;
                rowGroupCount = 1;

                if (this.cfg.liveScroll && column[0].hasAttribute('rowspan')) {
                    rowGroupCount = parseInt(column.attr('rowspan'));
                    i += rowGroupCount - 1;
                }

                row.addClass('ui-datatable-grouped-row');
            }
            else {
                column.addClass('ui-duplicated-column');
                rowGroupCount++;
            }

            if(groupStartIndex != null && rowGroupCount > 1) {
                rows.eq(groupStartIndex).children('td').eq(colIndex).attr('rowspan', rowGroupCount);
            }
        }
    },

    /**
     * Sets up the event handlers for row group events.
     * @protected
     */
    bindToggleRowGroupEvents: function() {
        var expandableRows = this.tbody.children('tr.ui-rowgroup-header'),
            toggler = expandableRows.find('> td:first > a.ui-rowgroup-toggler');

        toggler.off('click.dataTable-rowgrouptoggler').on('click.dataTable-rowgrouptoggler', function(e) {
           var link = $(this),
           togglerIcon = link.children('.ui-rowgroup-toggler-icon'),
           parentRow = link.closest('tr.ui-rowgroup-header');

           if(togglerIcon.hasClass('ui-icon-circle-triangle-s')) {
               link.attr('aria-expanded', false);
               togglerIcon.addClass('ui-icon-circle-triangle-e').removeClass('ui-icon-circle-triangle-s');
               parentRow.nextUntil('tr.ui-rowgroup-header').hide();
           }
           else {
               link.attr('aria-expanded', true);
               togglerIcon.addClass('ui-icon-circle-triangle-s').removeClass('ui-icon-circle-triangle-e');
               parentRow.nextUntil('tr.ui-rowgroup-header').show();
           }

           e.preventDefault();
        });
    },

    /**
     * Computes the `colspan value for the table rows.
     * @private
     * @return {number} The computed `colspan` value.
     */
    calculateColspan: function() {
        var visibleHeaderColumns = this.thead.find('> tr:first th:not(.ui-helper-hidden):not(.ui-grouped-column)'),
            colSpanValue = 0;

        for(var i = 0; i < visibleHeaderColumns.length; i++) {
            var column = visibleHeaderColumns.eq(i);
            if(column.is('[colspan]')) {
                colSpanValue += parseInt(column.attr('colspan'));
            }
            else {
                colSpanValue++;
            }
        }

        return colSpanValue;
    },

    /**
     * Updates the `colspan` attribute of the given row.
     * @private
     * @param {JQuery} row A row to update.
     * @param {number} [colspanValue] The new `colspan` value. If not given, computes the value automatically.
     */
    updateColspan: function(row, colspanValue) {
        row.children('td').attr('colspan', colspanValue || this.calculateColspan());
    },

    /**
     * Updates the colspan attribute for the message shown when no rows are available.
     * @private
     */
    updateEmptyColspan: function() {
        var emptyRow = this.tbody.children('tr:first');
        if(emptyRow && emptyRow.hasClass('ui-datatable-empty-message')) {
            this.updateColspan(emptyRow);
        }
    },

    /**
     * Updates the `rowspan` attribute of the given row.
     * @private
     * @param {JQuery} row A column to update.
     */
    updateRowspan: function(row) {
        if (this.cfg.groupColumnIndexes) {
            var isGroupedRow = row.hasClass('ui-datatable-grouped-row');
            var groupedRow = isGroupedRow ? row : row.prevAll('.ui-datatable-grouped-row:first');
            var groupedColumn = groupedRow.children('.ui-grouped-column:first');
            var rowSpan = groupedRow.nextUntil('.ui-datatable-grouped-row').not(':hidden').length + 1;
            var diff = rowSpan - parseInt(groupedColumn.attr('rowspan') || 1);
            groupedColumn.attr('rowspan', rowSpan);
    
            var groupedColumnIndex = groupedColumn.index();
            if (groupedColumnIndex > 0) {
                var columns = row.children('td:visible');
                for (var i = 0; i < groupedColumnIndex; i++) {
                    var column = columns.eq(i);
                    if (column) {
                        column.attr('rowspan', parseInt(column.attr('rowspan') || 1) + diff);
                    }
                }
            }
        }
    },

    /**
     * Updates the `colspan` attributes of all expanded rows.
     * @private
     */
    updateExpandedRowsColspan: function() {
        var colspanValue = this.calculateColspan(),
            $this = this;
        this.getExpandedRows().each(function() {
            $this.updateColspan($(this).next('.ui-expanded-row-content'), colspanValue);
        });
    },

    /**
     * Computes and saves the resizable state of this data table, ie. which columns have got which width. May be used
     * later to restore the current column width after an AJAX update.
     * @private
     * @param {JQuery} columnHeader Element of a column header of this data table.
     * @param {JQuery} nextColumnHeader Element of the column header next to the given column header.
     * @param {JQuery} table The element for this data table.
     * @param {number} newWidth New width to be applied.
     * @param {number | null} nextColumnWidth Width of the column next to the given column header.
     */
    updateResizableState: function(columnHeader, nextColumnHeader, table, newWidth, nextColumnWidth) {
        var expandMode = (this.cfg.resizeMode === 'expand'),
        currentColumnId = columnHeader.attr('id'),
        nextColumnId = nextColumnHeader.attr('id'),
        tableId = this.id + "_tableWidthState",
        currentColumnState = currentColumnId + '_' + newWidth,
        nextColumnState = nextColumnId + '_' + nextColumnWidth,
        tableState = tableId + '_' + parseInt(table.css('width')),
        currentColumnMatch = false,
        nextColumnMatch = false,
        tableMatch = false;

        for(var i = 0; i < this.resizableState.length; i++) {
            var state = this.resizableState[i];
            if(state.indexOf(currentColumnId) === 0) {
                this.resizableState[i] = currentColumnState;
                currentColumnMatch = true;
            }
            else if(!expandMode && state.indexOf(nextColumnId) === 0) {
                this.resizableState[i] = nextColumnState;
                nextColumnMatch = true;
            }
            else if(expandMode && state.indexOf(tableId) === 0) {
                this.resizableState[i] = tableState;
                tableMatch = true;
            }
        }

        if(!currentColumnMatch) {
            this.resizableState.push(currentColumnState);
        }

        if(!expandMode && !nextColumnMatch) {
            this.resizableState.push(nextColumnState);
        }

        if(expandMode && !tableMatch) {
            this.resizableState.push(tableState);
        }

        this.resizableStateHolder.val(this.resizableState.join(','));
    },

    /**
     * Finds the saved width of the given column. The width of resizable columns may be saved to restore it after an
     * AJAX update.
     * @private
     * @param {string} id ID of a column
     * @return {string | undefined} The saved width of the given column in pixels. `undefined` when the given column
     * does not exist.
     */
    findColWidthInResizableState: function(id) {
        for (var i = 0; i < this.resizableState.length; i++) {
            var state = this.resizableState[i];
            if (state.indexOf(id) === 0) {
                return state.substring(state.lastIndexOf('_') + 1, state.length);
            }
        }

        return null;
    },

    /**
     * Updates some style classes for all columns.
     * @private
     */
    updateColumnsView: function() {
        if(this.isEmpty()) {
            return;
        }

        // update the visibility of columns but ignore expanded rows and GitHub #7255 grouped headers
        if(this.headers && !this.hasColGroup()) {
            for(var i = 0; i < this.headers.length; i++) {
                var header = this.headers.eq(i),
                    col = this.tbody.find('> tr:not(.ui-expanded-row-content) > td:nth-child(' + (header.index() + 1) + ')');

                if(header.hasClass('ui-helper-hidden')) {
                    col.addClass('ui-helper-hidden');
                }
                else {
                    col.removeClass('ui-helper-hidden');
                }
            }
        }

        // update the colspan of the expanded rows
        if(this.cfg.expansion) {
            this.updateExpandedRowsColspan();
        }
    },

    /**
     * Resets the scroll state of the body to a non-scrolled state.
     * @protected
     */
    resetVirtualScrollBody: function() {
        this.bodyTable.css('top', '0px');
        this.scrollBody.scrollTop(0);
        this.clearScrollState();
    }

});

/**
 * __PrimeFaces DataTable with Frozen Columns Widget__
 *
 * @prop {JQuery} frozenBody The DOM element for the frozen body.
 * @prop {JQuery} frozenBodyTable The DOM element for the frozen body TABLE.
 * @prop {JQuery} frozenContainer The DOM element for the container of the frozen table.
 * @prop {JQuery} frozenFooter The DOM element for the frozen footer.
 * @prop {JQuery} frozenFooterCols The DOM elements for the frozen columns of the footer.
 * @prop {JQuery} frozenFooterTable The DOM element for the frozen data table footer TABLE.
 * @prop {JQuery} frozenGroupResizers The DOM element for the frozen group resizers of the footer.
 * @prop {JQuery} frozenHeader The DOM element for the frozen header.
 * @prop {JQuery} frozenLayout The DOM element for the frozen layout container.
 * @prop {JQuery} frozenTbody The DOM element for the header TBODY.
 * @prop {JQuery} frozenThead The DOM element for the header THEAD.
 * @prop {JQuery} frozenTheadClone The DOM element for the clone of the frozen THEAD.
 * @prop {JQuery} scrollBodyTable The DOM element for the TABLE of the scrollable body.
 * @prop {JQuery} scrollContainer The DOM element for the container of the scrollable body.
 * @prop {JQuery} scrollFooterCols The DOM element for the scrollable columns of the footer.
 * @prop {JQuery} scrollFooterTable The DOM element for the TABLE of the scrollable footer.
 * @prop {JQuery} scrollGroupResizers The DOM element for the group resizers of the scrollable body.
 * @prop {JQuery} scrollHeaderTable The DOM element for the TABLE of the scrollable header.
 * @prop {JQuery} scrollLayout The DOM element for the scrollable layout container.
 * @prop {JQuery} scrollTbody The DOM element for the scrollable TBODY.
 * @prop {JQuery} scrollThead The DOM element for the scrollable THEAD.
 * @prop {JQuery} scrollTheadClone The DOM element for the clone of the scrollable THEAD.
 *
 * @interface {PrimeFaces.widget.FrozenDataTableCfg} cfg The configuration for the {@link  FrozenDataTable| FrozenDataTable widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DataTableCfg} cfg
 */
PrimeFaces.widget.FrozenDataTable = PrimeFaces.widget.DataTable.extend({

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    setupScrolling: function() {
        this.scrollLayout = this.jq.find('> table > tbody > tr > td.ui-datatable-frozenlayout-right');
        this.frozenLayout = this.jq.find('> table > tbody > tr > td.ui-datatable-frozenlayout-left');
        this.scrollContainer = this.jq.find('> table > tbody > tr > td.ui-datatable-frozenlayout-right > .ui-datatable-scrollable-container');
        this.frozenContainer = this.jq.find('> table > tbody > tr > td.ui-datatable-frozenlayout-left > .ui-datatable-frozen-container');
        this.scrollHeader = this.scrollContainer.children('.ui-datatable-scrollable-header');
        this.scrollHeaderBox = this.scrollHeader.children('div.ui-datatable-scrollable-header-box');
        this.scrollBody = this.scrollContainer.children('.ui-datatable-scrollable-body');
        this.scrollFooter = this.scrollContainer.children('.ui-datatable-scrollable-footer');
        this.scrollFooterBox = this.scrollFooter.children('div.ui-datatable-scrollable-footer-box');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        this.scrollHeaderTable = this.scrollHeaderBox.children('table');
        this.scrollBodyTable = this.cfg.virtualScroll ? this.scrollBody.children('div').children('table') : this.scrollBody.children('table');
        this.scrollThead = this.thead.eq(1);
        this.scrollTbody = this.tbody.eq(1);
        this.scrollFooterTable = this.scrollFooterBox.children('table');
        this.scrollFooterCols = this.scrollFooter.find('> .ui-datatable-scrollable-footer-box > table > tfoot > tr > td');
        this.frozenHeader = this.frozenContainer.children('.ui-datatable-scrollable-header');
        this.frozenBody = this.frozenContainer.children('.ui-datatable-scrollable-body');
        this.frozenBodyTable = this.cfg.virtualScroll ? this.frozenBody.children('div').children('table') : this.frozenBody.children('table');
        this.frozenThead = this.thead.eq(0);
        this.frozenTbody = this.tbody.eq(0);
        this.frozenFooter = this.frozenContainer.children('.ui-datatable-scrollable-footer');
        this.frozenFooterTable = this.frozenFooter.find('> .ui-datatable-scrollable-footer-box > table');
        this.frozenFooterCols = this.frozenFooter.find('> .ui-datatable-scrollable-footer-box > table > tfoot > tr > td');
        this.percentageScrollHeight = this.cfg.scrollHeight && (this.cfg.scrollHeight.indexOf('%') !== -1);
        this.percentageScrollWidth = this.cfg.scrollWidth && (this.cfg.scrollWidth.indexOf('%') !== -1);

        this.frozenThead.find('> tr > th').addClass('ui-frozen-column');

        var $this = this,
        scrollBarWidth = this.getScrollbarWidth() + 'px',
        hScrollWidth = this.scrollBody[0].scrollWidth;

        if(this.cfg.scrollHeight) {
            if(this.percentageScrollHeight) {
                this.adjustScrollHeight();
            }

            if(this.hasVerticalOverflow()) {
                this.scrollHeaderBox.css('margin-right', scrollBarWidth);
                this.scrollFooterBox.css('margin-right', scrollBarWidth);
            }
        }

        if(this.cfg.selectionMode) {
            this.scrollTbody.removeAttr('tabindex');
        }

        this.fixColumnWidths();

        if(this.cfg.scrollWidth) {
            if(this.percentageScrollWidth)
                this.adjustScrollWidth();
            else
                this.setScrollWidth(parseInt(this.cfg.scrollWidth));

            if(this.hasVerticalOverflow()) {
                var browser = PrimeFaces.env.browser;
                if(browser.webkit === true || browser.mozilla === true)
                    this.frozenBody.append('<div style="height:' + scrollBarWidth + ';border:1px solid transparent"></div>');
                else
                    this.frozenBodyTable.css('margin-bottom', scrollBarWidth);
            }
        }

        this.cloneHead();

        if(this.cfg.liveScroll) {
            this.clearScrollState();
            this.scrollOffset = 0;
            this.cfg.liveScrollBuffer = (100 - this.cfg.liveScrollBuffer) / 100;
            this.shouldLiveScroll = true;
            this.loadingLiveScroll = false;
            this.allLoadedLiveScroll = $this.cfg.scrollStep >= $this.cfg.scrollLimit;
        }

        this.restoreScrollState();

        if(this.cfg.virtualScroll) {
            var row = this.scrollTbody.children('tr.ui-widget-content');
            if(row) {
                this.rowHeight = row.outerHeight();
                this.scrollBody.children('div').css('height', parseFloat((this.cfg.scrollLimit * this.rowHeight) + 'px'));
                this.frozenBody.children('div').css('height', parseFloat((this.cfg.scrollLimit * this.rowHeight) + 'px'));
            }

            if(!this.cfg.scrollHeight) {
                this.frozenBody.css('height', this.scrollBody.height());
            }
        }

        this.scrollBody.on('scroll.datatable', function() {
            var scrollLeft = $this.scrollBody.scrollLeft(),
            scrollTop = $this.scrollBody.scrollTop();

            if ($this.isRTL) {
                $this.scrollHeaderBox.css('margin-right', (scrollLeft - hScrollWidth + this.clientWidth) + 'px');
                $this.scrollFooterBox.css('margin-right', (scrollLeft - hScrollWidth + this.clientWidth) + 'px');
            }
            else {
                $this.scrollHeaderBox.css('margin-left', -scrollLeft + 'px');
                $this.scrollFooterBox.css('margin-left', -scrollLeft + 'px');
            }

            $this.frozenBody.scrollTop(scrollTop);

            if($this.cfg.virtualScroll) {
                var virtualScrollBody = this;

                clearTimeout($this.scrollTimeout);
                $this.scrollTimeout = setTimeout(function() {
                    var viewportHeight = $this.scrollBody.outerHeight(),
                    tableHeight = $this.scrollBodyTable.outerHeight(),
                    pageHeight = $this.rowHeight * $this.cfg.scrollStep,
                    virtualTableHeight = parseFloat(($this.cfg.scrollLimit * $this.rowHeight) + 'px'),
                    pageCount = (virtualTableHeight / pageHeight)||1;

                    if(virtualScrollBody.scrollTop + viewportHeight > parseFloat($this.scrollBodyTable.css('top')) + tableHeight || virtualScrollBody.scrollTop < parseFloat($this.scrollBodyTable.css('top'))) {
                        var page = Math.floor((virtualScrollBody.scrollTop * pageCount) / (virtualScrollBody.scrollHeight)) + 1;
                        $this.loadRowsWithVirtualScroll(page, function () {
                            $this.scrollBodyTable.css('top', ((page - 1) * pageHeight) + 'px');
                            $this.frozenBodyTable.css('top', ((page - 1) * pageHeight) + 'px');
                        });
                    }
                }, 200);
            }
            else if($this.shouldLiveScroll) {
                var scrollTop = Math.ceil(this.scrollTop),
                scrollHeight = this.scrollHeight,
                viewportHeight = this.clientHeight;

                if((scrollTop >= ((scrollHeight * $this.cfg.liveScrollBuffer) - (viewportHeight))) && $this.shouldLoadLiveScroll()) {
                    $this.loadLiveRows();
                }
            }

            $this.saveScrollState();
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
     * @override
     * @protected
     * @inheritdoc
     */
    cloneHead: function() {
        if (this.frozenTheadClone) {
            this.frozenTheadClone.remove();
        }
        this.frozenTheadClone = this.cloneTableHeader(this.frozenThead, this.frozenBodyTable);

        if (this.scrollTheadClone) {
            this.scrollTheadClone.remove();
        }
        this.scrollTheadClone = this.cloneTableHeader(this.scrollThead, this.scrollBodyTable);
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @return {boolean}
     */
    hasVerticalOverflow: function() {
        return this.scrollBodyTable.outerHeight() > this.scrollBody.outerHeight();
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    adjustScrollHeight: function() {
        var relativeHeight = this.jq.parent().innerHeight() * (parseInt(this.cfg.scrollHeight) / 100),
        headerChilden = this.jq.children('.ui-datatable-header'),
        footerChilden = this.jq.children('.ui-datatable-footer'),
        tableHeaderHeight = (headerChilden.length > 0) ? headerChilden.outerHeight(true) : 0,
        tableFooterHeight = (footerChilden.length > 0) ? footerChilden.outerHeight(true) : 0,
        scrollersHeight = (this.scrollHeader.innerHeight() + this.scrollFooter.innerHeight()),
        paginatorsHeight = this.paginator ? this.paginator.getContainerHeight(true) : 0,
        height = (relativeHeight - (scrollersHeight + paginatorsHeight + tableHeaderHeight + tableFooterHeight));

        if(this.cfg.virtualScroll) {
            this.scrollBody.css('max-height', height + 'px');
            this.frozenBody.css('max-height', height + 'px');
        }
        else {
            this.scrollBody.height(height);
            this.frozenBody.height(height);
        }
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    adjustScrollWidth: function() {
        var scrollLayoutWidth = this.jq.parent().innerWidth() - this.frozenLayout.innerWidth(),
        width = parseInt((scrollLayoutWidth * (parseInt(this.cfg.scrollWidth) / 100)));

        this.setScrollWidth(width);
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {number} width
     */
    setScrollWidth: function(width) {
        this.scrollHeader.width(width);
        this.scrollBody.css('margin-right', '0px').width(width);
        this.scrollFooter.width(width);

        var $this = this,
        headerWidth = width + this.frozenLayout.width();

        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), headerWidth);
        });
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    fixColumnWidths: function() {
        var $this = this;
        if(!this.columnWidthsFixed) {

            if(this.cfg.scrollable) {
                this._fixColumnWidths(this.scrollHeader, this.scrollFooterCols, this.scrollColgroup);
                this._fixColumnWidths(this.frozenHeader, this.frozenFooterCols, this.frozenColgroup);
            }
            else {
                this.jq.find('> .ui-datatable-tablewrapper > table > thead > tr > th').each(function() {
                    var col = $(this),
                    widthInfo = $this.getColumnWidthInfo(col);

                    $this.applyWidthInfo(col, widthInfo);
                });
            }

            this.columnWidthsFixed = true;
        }
    },

    /**
     * Adjusts the width of the given columns to fit the current settings.
     * @protected
     * @param {JQuery} header Header of this data table.
     * @param {JQuery} footerCols The columns to adjust.
     */
    _fixColumnWidths: function(header, footerCols) {
        var $this = this;

        header.find('> .ui-datatable-scrollable-header-box > table > thead > tr > th').each(function() {
            var headerCol = $(this),
            colIndex = headerCol.index(),
            widthInfo = $this.getColumnWidthInfo(headerCol);

            $this.applyWidthInfo(headerCol, widthInfo);

            if(footerCols.length > 0) {
                var footerCol = footerCols.eq(colIndex);
                $this.applyWidthInfo(footerCol, widthInfo);
            }
        });
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {string} data
     * @param {boolean | undefined} clear
     */
    updateData: function(data, clear) {
        var table = $('<table><tbody>' + data + '</tbody></table>'),
        rows = table.find('> tbody > tr'),
        empty = (clear === undefined) ? true: clear;

        if(empty) {
            this.frozenTbody.children().remove();
            this.scrollTbody.children().remove();
        }

        //find slice index by checking how many rendered columns there are in frozen part
        var firstRow = this.frozenTbody.children('tr:first'),
        frozenColumnCount = firstRow.length ? firstRow.children('td').length: this.cfg.frozenColumns;

        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i),
            columns = row.children('td'),
            frozenRow = this.copyRow(row),
            scrollableRow = this.copyRow(row);

            if(row.hasClass('ui-datatable-empty-message')) {
                var colspan = columns.attr('colspan'),
                cloneColumns = columns.clone();

                frozenRow.append(columns.attr('colspan', this.cfg.frozenColumns));
                scrollableRow.append(cloneColumns.attr('colspan', (colspan - this.cfg.frozenColumns)));
            }
            else {
                frozenRow.append(columns.slice(0, frozenColumnCount));
                scrollableRow.append(columns.slice(frozenColumnCount));
            }

            this.frozenTbody.append(frozenRow);
            this.scrollTbody.append(scrollableRow);
        }

        this.postUpdateData();
    },

    /**
     * Clones the given row and returns it
     * @param {JQuery} original DOM element of the original row.
     * @return {JQuery} The cloned row.
     */
    copyRow: function(original) {
        return $('<tr></tr>').attr('data-ri', original.data('ri')).attr('data-rk', original.data('rk')).addClass(original.attr('class')).
                attr('role', 'row').attr('aria-selected', original.attr('aria-selected'));
    },

    /**
     * @override
     * @inheritdoc
     * @return {JQuery}
     */
    getThead: function() {
        return $(this.jqId + '_frozenThead,' + this.jqId + '_scrollableThead');
    },

    /**
     * @override
     * @inheritdoc
     * @return {JQuery}
     */
    getTbody: function() {
        return $(this.jqId + '_frozenTbody,' + this.jqId + '_scrollableTbody');
    },

    /**
     * @override
     * @inheritdoc
     * @return {JQuery}
     */
    getTfoot: function() {
        return $(this.jqId + '_frozenTfoot,' + this.jqId + '_scrollableTfoot');
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {string} selector
     */
    bindRowHover: function(selector) {
        var $this = this;

        this.tbody.off('mouseenter.datatable mouseleave.datatable', selector)
                    .on('mouseenter.datatable', selector, null, function() {
                        var row = $(this),
                        twinRow = $this.getTwinRow(row);

                        row.addClass('ui-state-hover');
                        twinRow.addClass('ui-state-hover');
                    })
                    .on('mouseleave.datatable', selector, null, function() {
                        var row = $(this),
                        twinRow = $this.getTwinRow(row);

                        row.removeClass('ui-state-hover');
                        twinRow.removeClass('ui-state-hover');
                    });
    },

    /**
     * Finds the twin row of the given row. The data table body has got two sets of rows.
     * @param {JQuery} row Row for which to find the twin
     * @return {JQuery} DOM element of the twin row.
     */
    getTwinRow: function(row) {
        var twinTbody = (this.tbody.index(row.parent()) === 0) ? this.tbody.eq(1) : this.tbody.eq(0);

        return twinTbody.children().eq(row.index());
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} row
     */
    highlightRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} row
     */
    unhighlightRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} row
     * @param {string} content
     */
    displayExpandedRow: function(row, content) {
        var twinRow = this.getTwinRow(row);
        row.after(content);
        this.updateRowspan(row);
        var expansionRow = row.next();
        this.updateColspan(expansionRow);
        expansionRow.show();

        twinRow.after('<tr class="ui-expanded-row-content ui-widget-content"><td></td></tr>');
        twinRow.next().children('td').attr('colspan', this.updateColspan(twinRow)).height(expansionRow.children('td').height());
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} row
     */
    collapseRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },

    /**
     * @override
     * @inheritdoc
     * @return {JQuery}
     */
    getExpandedRows: function() {
        return this.frozenTbody.children('.ui-expanded-row');
    },

    /**
     * @override
     * @inheritdoc
     * @protected
     * @param {JQuery} row
     */
    showRowEditors: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },

    /**
     * @override
     * @inheritdoc
     * @protected
     * @param {JQuery} row
     * @param {string} content
     */
    updateRow: function(row, content) {
        var table = $('<table><tbody>' + content + '</tbody></table>'),
        newRow = table.find('> tbody > tr'),
        columns = newRow.children('td'),
        frozenRow = this.copyRow(newRow),
        scrollableRow = this.copyRow(newRow),
        twinRow = this.getTwinRow(row);

        frozenRow.append(columns.slice(0, this.cfg.frozenColumns));
        scrollableRow.append(columns.slice(this.cfg.frozenColumns));

        row.replaceWith(frozenRow);
        twinRow.replaceWith(scrollableRow);
    },

    /**
     * @override
     * @inheritdoc
     * @param {number} index
     */
    invalidateRow: function(index) {
        this.frozenTbody.children('tr').eq(index).addClass('ui-widget-content ui-row-editing ui-state-error');
        this.scrollTbody.children('tr').eq(index).addClass('ui-widget-content ui-row-editing ui-state-error');
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} row
     * @return {JQuery}
     */
    getRowEditors: function(row) {
        return row.find('div.ui-cell-editor').add(this.getTwinRow(row).find('div.ui-cell-editor'));
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQueryUI.DraggableEventUIParams} ui Data of the drag event.
     * @return {JQuery|null}
     */
    findGroupResizer: function(ui) {
        var resizer = this._findGroupResizer(ui, this.frozenGroupResizers);
        if(resizer) {
            return resizer;
        }
        else {
            return this._findGroupResizer(ui, this.scrollGroupResizers);
        }
    },

    /**
     * Finds the resizer DOM element that matches the given draggable event params.
     * @protected
     * @param {JQueryUI.DraggableEventUIParams} ui Data of the drag event.
     * @param {JQuery} resizers List of all available resizers.
     * @return {JQuery|null} DOM element of the resizer.
     */
    _findGroupResizer: function(ui, resizers) {
        for(var i = 0; i < resizers.length; i++) {
            var groupResizer = resizers.eq(i);
            if(groupResizer.offset().left === ui.helper.data('originalposition').left) {
                return groupResizer;
            }
        }

        return null;
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    addResizers: function() {
        var frozenColumns = this.frozenThead.find('> tr > th.ui-resizable-column'),
        scrollableColumns = this.scrollThead.find('> tr > th.ui-resizable-column');

        frozenColumns.prepend('<span class="ui-column-resizer">&nbsp;</span>');
        scrollableColumns.prepend('<span class="ui-column-resizer">&nbsp;</span>')

        if(this.cfg.resizeMode === 'fit') {
            frozenColumns.filter(':last-child').addClass('ui-frozen-column-last');
            scrollableColumns.filter(':last-child').children('span.ui-column-resizer').hide();
        }

        if(this.hasColumnGroup) {
            this.frozenGroupResizers = this.frozenThead.find('> tr:first > th > .ui-column-resizer');
            this.scrollGroupResizers = this.scrollThead.find('> tr:first > th > .ui-column-resizer');
        }
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery.TriggeredEvent} event
     * @param {JQueryUI.DraggableEventUIParams} ui
     */
    resize: function(event, ui) {
        var columnHeader = null,
        change = null,
        newWidth = null,
        nextColumnWidth = null,
        expandMode = (this.cfg.resizeMode === 'expand');

        if(this.hasColumnGroup) {
            var groupResizer = this.findGroupResizer(ui);
            if(!groupResizer) {
                return;
            }

            columnHeader = groupResizer.parent();
        }
        else {
            columnHeader = ui.helper.parent();
        }

        var nextColumnHeader = columnHeader.next();

        var colIndex = columnHeader.index(),
        lastFrozen = columnHeader.hasClass('ui-frozen-column-last');

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

        var minWidth = parseInt(columnHeader.css('min-width'));
        minWidth = (minWidth == 0) ? 15 : minWidth;
        var shouldChange = (expandMode && newWidth > minWidth) || (lastFrozen ? (newWidth > minWidth) : (newWidth > minWidth && nextColumnWidth > minWidth));
        if(shouldChange) {
            var frozenColumn = columnHeader.hasClass('ui-frozen-column'),
            theadClone = frozenColumn ? this.frozenTheadClone : this.scrollTheadClone,
            originalTable = frozenColumn ? this.frozenThead.parent() : this.scrollThead.parent(),
            cloneTable = theadClone.parent(),
            footerCols = frozenColumn ? this.frozenFooterCols : this.scrollFooterCols,
            footerTable = frozenColumn ? this.frozenFooterTable:  this.scrollFooterTable,
            $this = this;

            if(expandMode) {
                if(lastFrozen) {
                    this.frozenLayout.width(this.frozenLayout.width() + change);
                }

                var originalTableWidth = originalTable.width(),
                cloneTableWidth = cloneTable.width(),
                footerTableWidth = footerTable.width();

                //header
                originalTable.width(originalTableWidth + change);

                //body
                cloneTable.width(cloneTableWidth + change);

                //footer
                footerTable.width(footerTableWidth + change);

                setTimeout(function() {
                    columnHeader.width(newWidth);

                    if($this.hasColumnGroup) {
                        theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth);                          //body
                        footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);                 //footer
                    }
                    else {
                        theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);     //body
                        footerCols.eq(colIndex).width(newWidth);                                                            //footer
                    }
                }, 1);


            }
            else {
                if(lastFrozen) {
                    this.frozenLayout.width(this.frozenLayout.width() + change);
                }

                columnHeader.width(newWidth);
                nextColumnHeader.width(nextColumnWidth);

                if(this.hasColumnGroup) {
                    //body
                    theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth);
                    theadClone.find('> tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);

                    //footer
                    footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);
                    footerTable.find('> tfoot > tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);
                }
                else {
                    theadClone.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);
                    theadClone.find(PrimeFaces.escapeClientId(nextColumnHeader.attr('id') + '_clone')).width(nextColumnWidth);

                    if(footerCols.length > 0) {
                        var footerCol = footerCols.eq(colIndex),
                        nextFooterCol = footerCol.next();

                        footerCol.width(newWidth);
                        nextFooterCol.width(nextColumnWidth);
                    }
                }
            }
        }
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @return {boolean}
     */
    hasColGroup: function() {
        return this.frozenThead.children('tr').length > 1 || this.scrollThead.children('tr').length > 1;
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    addGhostRow: function() {
        this._addGhostRow(this.frozenTbody, this.frozenThead, this.frozenTheadClone, this.frozenFooter.find('table'), 'ui-frozen-column');
        this._addGhostRow(this.scrollTbody, this.scrollThead, this.scrollTheadClone, this.scrollFooterTable);
    },

    /**
     * Adds an invisible row for internal purposes.
     * @protected
     * @param {JQuery} body Body of this data table.
     * @param {JQuery} header Header of this data table.
     * @param {JQuery} headerClone Cloned header of this data table, see method `cloneHead`.
     * @param {JQuery} footerTable Footer of this data table.
     * @param {string} [columnClass] Optional CSS class for the ghost columns.
     */
    _addGhostRow: function(body, header, headerClone, footerTable, columnClass) {
        var dataColumns = body.find('tr:first').children('td'),
        dataColumnsCount = dataColumns.length,
        columnMarkup = '',
        columnStyleClass = columnClass ? 'ui-resizable-column ' + columnClass : 'ui-resizable-column';

        for(var i = 0; i < dataColumnsCount; i++) {
            columnMarkup += '<th style="height:0px;border-bottom-width: 0px;border-top-width: 0px;padding-top: 0px;padding-bottom: 0px;outline: 0 none;width:' + dataColumns.eq(i).width() + 'px" class="' + columnStyleClass + '"></th>';
        }

        header.prepend('<tr>' + columnMarkup + '</tr>');

        if(this.cfg.scrollable) {
            headerClone.prepend('<tr>' + columnMarkup + '</tr>');
            footerTable.children('tfoot').prepend('<tr>' + columnMarkup + '</tr>');
        }
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @return {JQuery}
     */
    getFocusableTbody: function() {
        return this.tbody.eq(0);
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    highlightFocusedRow: function() {
        this._super();
        this.getTwinRow(this.focusedRow).addClass('ui-state-hover');
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    unhighlightFocusedRow: function() {
        this._super();
        this.getTwinRow(this.focusedRow).removeClass('ui-state-hover');
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} row
     */
    assignFocusedRow: function(row) {
        this._super(row);

        if(!row.parent().attr('tabindex')) {
            this.frozenTbody.trigger('focus');
        }
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    saveColumnOrder: function() {
        var columnIds = [],
        columns = $(this.jqId + '_frozenThead:first th,' + this.jqId + '_scrollableThead:first th');

        columns.each(function(i, item) {
            columnIds.push($(item).attr('id'));
        });

        this.orderStateHolder.val(columnIds.join(','));
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    resetVirtualScrollBody: function() {
        this.scrollBodyTable.css('top', '0px');
        this.frozenBodyTable.css('top', '0px');
        this.scrollBody.scrollTop(0);
        this.frozenBody.scrollTop(0);
        this.clearScrollState();
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    groupRows: function() {
        var scrollRows = this.scrollTbody.children('tr'),
        frozenRows = this.frozenTbody.children('tr');

        for (var i = 0; i < this.cfg.groupColumnIndexes.length; i++) {
            var groupColumnIndex = this.cfg.groupColumnIndexes[i];

            if (groupColumnIndex >= this.cfg.frozenColumns) {
                this.groupRow(groupColumnIndex - this.cfg.frozenColumns, scrollRows);
            }
            else {
                this.groupRow(groupColumnIndex, frozenRows);
            }
        }

        scrollRows.children('td.ui-duplicated-column').remove();
        frozenRows.children('td.ui-duplicated-column').remove();
    }
});
