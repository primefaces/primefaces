/**
 * The configuration for the {@link DataTable} widget. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg | BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 */
export interface DataTableCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** When true columns can be unsorted upon clicking sort. */
    allowUnsorting: boolean;
    /** Defines the cell edit behavior. */
    cellEditMode: PrimeFacesTypes.widget.DataTable.CellEditMode;
    /**
     * Whether the cells can be navigated with the keyboard. Enabled by default, unless editing, reflow or row selection
     * is enabled.
     */
    cellNavigation: boolean;
    /** Separator text to use in output mode of editable cells with multiple components. */
    cellSeparator: string;
    /** Caches the next page asynchronously. */
    clientCache: boolean;
    /** The column selection mode, when set, rows may be selected. */
    columnSelectionMode: string;
    /** `true` to disable the context menu when the DataTable has got on data row, or `false` otherwise. */
    disableContextMenuIfEmpty: boolean;
    /** Disables text selection on row click. */
    disabledTextSelection: boolean;
    /** Columns can be reordered with drag & drop when enabled. */
    draggableColumns: boolean;
    /** When enabled, rows can be reordered using drag & drop. */
    draggableRows: boolean;
    /** Event that triggers row/cell editing. */
    editInitEvent: string;
    /** Whether rows may be edited as a whole or whether each cell can be edited individually. */
    editMode: "row" | "cell";
    /** Controls in-cell editing. */
    editable: boolean;
    /** `true` if rows are expandable, or `false` otherwise. */
    expansion: boolean;
    /** `true` if filtering is enabled, or `false` otherwise. */
    filter: boolean;
    /** Delay for filtering in milliseconds. */
    filterDelay: number;
    /** Event to invoke filtering for input filters. */
    filterEvent: string;
    /** The number of frozen columns. */
    frozenColumns: number;
    /** The alignment of frozen columns. */
    frozenColumnsAlignment: string;
    /** The indices of the columns by which the rows are grouped. */
    groupColumnIndexes: number[];
    /** Columns are resized live in this mode without using a resize helper. */
    liveResize: boolean;
    /** Enables live scrolling. */
    liveScroll: boolean;
    /**
     * Percentage of the height of the buffer between the bottom of the page and the scroll position to initiate the
     * load for the new chunk. This value is in the range `0...100`.
     */
    liveScrollBuffer: number;
    /** `true` if sorting by multiple columns is enabled, or `false` otherwise. */
    multiSort: boolean;
    /** Whether multiple resize mode is enabled. */
    multiViewState: boolean;
    /** `true` to use native radio button and checkbox elements, or `false` otherwise. */
    nativeElements: boolean;
    /** Callback that is invoked when the user clicked on a row of the DataTable. */
    onRowClick: PrimeFacesTypes.widget.DataTable.OnRowClickCallback;
    /** When pagination is enabled: The configuration of the paginator. */
    paginator: PrimeFacesTypes.widget.PartialWidgetCfg<PrimeFacesTypes.widget.PaginatorCfg>;
    /** `false` to update the whole DataTable on paging, sorting and filtering, `true` to update only the body. */
    partialUpdate: boolean;
    /** Reflow mode is a responsive mode to display columns as stacked depending on screen size. */
    reflow: boolean;
    /** Enables column resizing. */
    resizableColumns: boolean;
    /** Defines the resize behavior. */
    resizeMode: PrimeFacesTypes.widget.DataTable.ResizeMode;
    /** CSS selector for the draggable handle. */
    rowDragSelector: string;
    /** Defines the row edit. */
    rowEditMode: PrimeFacesTypes.widget.DataTable.RowEditMode;
    /** Defines row expand mode. */
    rowExpandMode: PrimeFacesTypes.widget.DataTable.RowExpandMode;
    /** Adds hover effect to rows. Hover is always on when selection is enabled. */
    rowHover: boolean;
    /** CSS selector find finding the rows of this DataTable. */
    rowSelector: string;
    /** Saves the changes in cell editing on blur, when set to false changes are discarded. */
    saveOnCellBlur: boolean;
    /** Scroll viewport height. */
    scrollHeight: string;
    /** Maximum number of rows that may be loaded via live scrolling. */
    scrollLimit: number;
    /** Number of additional rows to load in each live scroll. */
    scrollStep: number;
    /** Scroll viewport width. */
    scrollWidth: string;
    /** Makes data scrollable with fixed header. */
    scrollable: boolean;
    /** Enables row selection. */
    selectionMode: PrimeFacesTypes.widget.DataTable.CmSelectionMode | PrimeFacesTypes.widget.DataTable.SelectionMode;
    /**
     * When using a paginator and selection mode is `checkbox`, the select all checkbox in the header will select all
     * rows on the current page if `true`, or all rows on all pages if `false`. Default is `true`.
     */
    selectionPageOnly: boolean;
    /** Defines row selection mode when clicking on the row itself. */
    selectionRowMode: PrimeFacesTypes.widget.DataTable.SelectionRowMode;
    /** `true` if sorting is enabled on the DataTable, `false` otherwise. */
    sorting: boolean;
    /** IDs of the columns by which to order. Order by the first column, then by the second, etc. */
    sortMetaOrder: string[];
    /** Sticky header stays in window viewport during scrolling. */
    stickyHeader: boolean;
    /** Selector to position on the page according to other fixing elements on the top of the table. */
    stickyTopAt: string;
    /** The value of the `tabindex` attribute for this DataTable. */
    tabindex: string;
    /** Loads data on demand as the scrollbar gets close to the bottom. */
    virtualScroll: boolean;
}

/**
 * __PrimeFaces DataTable Widget__
 *
 * DataTable displays data in tabular format.
 */
export class DataTable<Cfg extends DataTableCfg = DataTableCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {

    /**
     * Map between the sort order names and the multiplier for the comparator.
     */
    static SORT_ORDER: Record<PrimeFacesTypes.widget.DataTable.SortOrder, -1 | 0 | 1> = {
        ASCENDING: 1,
        DESCENDING: -1,
        UNSORTED: 0
    }

    /** Whether all available items were already loaded. */
    allLoadedLiveScroll?: boolean;
    /** Localized message for sorting a column in ascending order. */
    ascMessage?: string;
    /** The DOM element for the body part of the table. */
    bodyTable?: JQuery;
    /** Cache for the contents of a row. Key is the row index, value the HTML content of the row. */
    cacheMap?: Record<number, string>;
    /** Number of rows to cache. */
    cacheRows?: number;
    /** DOM element of the container with the `check all` checkbox in the header. */
    checkAllToggler?: JQuery;
    /** Clone of the table header. */
    clone?: JQuery;
    /** Whether column widths are fixed or may be resized. */
    columnWidthsFixed?: boolean;
    /** DOM element of the table cell for which the context menu was opened. */
    contextMenuCell?: JQuery;
    /** Widget with the context menu for the DataTable. */
    contextMenuWidget?: PrimeFacesTypes.widget.ContextMenuLikeWidget;
    /** Current cell to be edited. */
    currentCell?: JQuery | null;
    /** 0-based index of row where the the cursor is located. */
    cursorRowMeta?: PrimeFacesTypes.widget.DataTable.RowMeta | null;
    /** Localized message for sorting a column in descending order. */
    descMessage?: string;
    /** DOM element of the icon that indicates a column is draggable. */
    dragIndicatorBottom?: JQuery;
    /** DOM element of the icon that indicates a column is draggable. */
    dragIndicatorTop?: JQuery;
    /**
     * DOM element of the hidden input that holds the row keys of the rows that are expanded. Used to preserve the
     * expansion state during AJAX updates.
     */
    expansionHolder?: JQuery;
    /** List of row indices to expand. */
    expansionProcess?: number[];
    /** DOM element of the currently focused row. */
    focusedRow?: JQuery | null;
    /** Whether the focused row includes the checkbox for selecting the row. */
    focusedRowWithCheckbox?: boolean;
    /** The DOM elements for the footer columns. */
    footerCols?: JQuery;
    /** The DOM elements for the footer table. */
    footerTable?: JQuery;
    /** The DOM element for the header THEAD. */
    frozenThead?: JQuery;
    /** The DOM elements for the resizer button of each group. */
    groupResizers?: JQuery;
    /** Whether the table has any column groups. */
    hasColumnGroup?: boolean;
    /** The DOM elements for the header table. */
    headerTable?: JQuery;
    /** DOM elements for the `TH` headers of this DataTable. */
    headers?: JQuery;
    /** Whether to ignore row hover event. */
    ignoreRowHoverEvent?: boolean;
    /** Whether the writing direction is set to right-to-left. */
    isRTL?: boolean;
    /** Whether a row toggler was clicked. */
    isRowTogglerClicked?: boolean;
    /** Target of the context menu, when a context menu is used. */
    jqTargetId?: string;
    /** Whether live scrolling is currently active. */
    liveScrollActive?: boolean;
    /** List of row keys of the expansion rows that had their content already loaded via AJAX. */
    loadedExpansionRows?: string[];
    /** Whether data is currently being loaded due to the live scrolling feature. */
    loadingLiveScroll?: boolean;
    /** Whether a mousedown event occurred on a row. */
    mousedownOnRow?: boolean;
    /** INPUT element storing the current column / row order. */
    orderStateHolder?: JQuery;
    /** The original row index of the row that was clicked. */
    originRowMeta?: PrimeFacesTypes.widget.DataTable.RowMeta | null;
    /** Localized message for removing the sort order and showing rows in their original order. */
    otherMessage?: string;
    /** When pagination is enabled: The paginator widget instance used for paging. */
    paginator?: PrimeFacesTypes.Widget<"Paginator">;
    /** Whether the scroll height is a relative (percentage) value. */
    percentageScrollHeight?: boolean | "" | undefined;
    /** Whether the scroll width is a relative (percentage) value. */
    percentageScrollWidth?: boolean | "" | undefined;
    /** DOM element of the reflow dropdown for sorting. */
    reflowDD?: JQuery;
    /** The height of the table viewport, relative to the total height, used for scrolling. */
    relativeHeight?: number;
    /** A list with the current widths for each resizable column. */
    resizableState?: string[];
    /** INPUT element storing the current widths for each resizable column. */
    resizableStateHolder?: JQuery;
    /** The set-timeout timer ID of the timer used for resizing. */
    resizeTimeout?: number;
    /** The DOM element for the resize helper. */
    resizerHelper?: JQuery;
    /** Constant height in pixels for each row, when virtual scrolling is enabled. */
    rowHeight?: number;
    /** The CSS selector for the table rows. */
    rowSelector?: string;
    /** The CSS selector for the table rows that can be clicked. */
    rowSelectorForRowClick?: string;
    /** The DOM element for the scrollable body of the table. */
    scrollBody?: JQuery;
    /** The DOM element for the scrollable footer of the table. */
    scrollFooter?: JQuery;
    /** The DOM element for the scrollable footer box of the table. */
    scrollFooterBox?: JQuery;
    /** The DOM element for the scrollable header of the table. */
    scrollHeader?: JQuery;
    /** The DOM element for the scrollable header box of the table. */
    scrollHeaderBox?: JQuery;
    /** The current scroll position. */
    scrollOffset?: number;
    /** INPUT element storing the current scroll position. */
    scrollStateHolder?: JQuery;
    /** The DOM element for the scrollable TBODY. */
    scrollTbody?: JQuery;
    /** The set-timeout timer ID of the timer used for scrolling. */
    scrollTimeout?: number;
    /** The width of the scrollbar in pixels. */
    scrollbarWidth?: number;
    /** List of row keys for the currently selected rows. */
    selection?: string[];
    /** ID of the INPUT element storing the currently selected rows. */
    selectionHolder?: string;
    /** Whether live scrolling is currently enabled. */
    shouldLiveScroll?: boolean;
    /** Information about how the columns are sorted, in the order of the sort priority. */
    sortMeta?: PrimeFacesTypes.widget.DataTable.SortMeta[];
    /** DOM elements for the columns that are sortable. */
    sortableColumns?: JQuery;
    /** The DOM element for the sticky container of the table. */
    stickyContainer?: JQuery;
    /** DOM element of the `TBODY` element of this DataTable, if it exists. */
    tbody: JQuery = $();
    /** DOM element of the `TFOOT` element of this DataTable, if it exists. */
    tfoot: JQuery = $();
    /** DOM element of the `THEAD` element of this DataTable, if it exists. */
    thead: JQuery = $();
    /** The DOM element for the cloned table head. */
    theadClone?: JQuery;
    /** Whether virtual scrolling is currently active. */
    virtualScrollActive?: boolean;

    /**
     * Builds a selector for body cells, which are `td` elements or `th` row headers (`rowHeader="true"`).
     * @param suffix Selector part appended to each cell type, e.g. `.p-editable-column`.
     * @return The cell selector.
     */
    static cellSelector(suffix: string = ''): string {
        return 'td' + suffix + ', th[role="rowheader"]' + suffix;
    }

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

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
        else {
            this.cfg.selectionRowMode = this.cfg.selectionRowMode || 'none';
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
            // ensure DOM memory is cleaned up by releasing document event handlers
            this.addDestroyListener(() => {
                $(document).off('.datatable' + this.id);
                $(document).off('mouseup.datatable-cell-blur' + this.id);
            });
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
                this.resizableState = (this.resizableStateHolder.val() as string).split(',');
            }
        }

        this.updateEmptyColspan();
        this.renderDeferred();
    }

    protected override _render(): void {
        this.isRTL = this.jq.hasClass('p-datatable-rtl');
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
            PrimeFaces.queueTask(() => {this.setupStickyHeader();}, 1);
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

        // cell navigation is enabled by default, but it is disabled when editing, reflow, orrow selection is enabled (unless explicitly specified by the user).
        if (this.cfg.cellNavigation === undefined) {
            this.cfg.cellNavigation = !(this.cfg.editMode || this.cfg.reflow || this.cfg.selectionRowMode !== 'none');
        }
        if (this.cfg.cellNavigation) {
            this.setupNavigableCells();
        }
    }

    /**
     * Retrieves the table header of this DataTable.
     * @return DOM element of the table header.
     */
    getThead(): JQuery {
        return $(this.jqId + '_head');
    }

    /**
     * Retrieves the table body of this DataTable.
     * @return DOM element of the table body.
     */
    getTbody(): JQuery {
        return $(this.jqId + '_data');
    }

    /**
     * Retrieves the table footer of this DataTable.
     * @return DOM element of the table footer.
     */
    getTfoot(): JQuery {
        return $(this.jqId + '_foot');
    }

    /**
     * Sets the given HTML string as the content of the body of this DataTable. Afterwards, sets up all required event
     * listeners etc.
     * @param data HTML string to set on the body.
     * @param clear Whether the contents of the table body should be removed beforehand.
     */
    updateData(data: string, clear?: boolean): void {
        var empty = (clear === undefined) ? true: clear;

        if(empty)
            this.tbody.html(data);
        else
            this.tbody.append(data);

        this.postUpdateData();
    }

    /**
     * Called after an AJAX update. Binds the appropriate event listeners again.
     */
    postUpdateData(): void {
        if (this.cfg.editable) {
            this.bindEditEvents();
        }

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

        if (this.cfg.cellNavigation) {
            this.setupNavigableCells();
        }
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this.columnWidthsFixed = false;
        this.ignoreRowHoverEvent = false;

        this.unbindEvents();

        super.refresh(cfg);
    }

    /**
     * Removes event listeners needed if refreshing to prevent multiple sort and pagination events.
     *
     * Cancels all current drag and drop events.
     */
    unbindEvents(): void {
        if (this.sortableColumns) {
            this.sortableColumns.off();
        }
        if (this.paginator) {
            this.paginator.unbindEvents();
        }

        // #5582: destroy any current draggable items
        if (this.cfg.draggableColumns || this.cfg.draggableRows) {
            var dragdrop = PrimeFaces.interactions.dragDrop.current;
            if (dragdrop && dragdrop.helper) {
                var item = dragdrop.item();
                if(item.closest('.p-datatable')[0] === this.jq[0]) {
                    dragdrop.cancel();
                }
            }
        }
    }

    /**
     * Binds the change event listener and renders the paginator
     */
    bindPaginator(): void {
        this.cfg.paginator!.paginate = (newState) => {
            if(this.cfg.clientCache) {
                this.loadDataWithCache(newState);
            }
            else {
                this.paginate(newState);
            }
        };

        this.paginator = new PrimeFaces.widget.Paginator();
        this.paginator.init(this.cfg.paginator!);
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
    }

    /**
     * Applies events related to sorting in a non-obtrusive way
     */
    bindSortEvents(): void {
        var hasAriaSort = false;
        this.cfg.tabindex = this.cfg.tabindex||'0';
        this.cfg.multiSort = this.cfg.multiSort||false;
        this.cfg.allowUnsorting = this.cfg.allowUnsorting||false;
        this.headers = this.thead.find('> tr > th');
        this.sortableColumns = this.headers.filter('.p-sortable-column');
        this.sortableColumns.attr('tabindex', this.cfg.tabindex);

        //aria messages
        this.ascMessage = this.getAriaLabel('datatable.sort.ASC');
        this.descMessage = this.getAriaLabel('datatable.sort.DESC');
        if (this.cfg.allowUnsorting) {
            this.otherMessage = this.getAriaLabel('datatable.sort.NONE');
        }
        else {
            this.otherMessage = this.getAriaLabel('datatable.sort.ASC');
        }

        //reflow dropdown
        this.reflowDD = $(this.jqId + '_reflowDD');

        this.sortMeta = [];

        for(var i = 0; i < this.sortableColumns.length; i++) {
            var columnHeader = this.sortableColumns.eq(i),
            columnHeaderId = columnHeader.attr('id'),
            sortIcon = columnHeader.children('span.p-sortable-column-icon'),
            sortOrder: -1 | 0 | 1 | null = null,
            resolvedSortMetaIndex: number | null = null,
            ariaLabel = columnHeader.attr('aria-label');

            if (columnHeader.hasClass('p-state-active')) {
                if (sortIcon.hasClass('p-datatable-sort-asc-icon')) {
                    sortOrder = DataTable.SORT_ORDER.ASCENDING;
                    columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.descMessage));
                    if (!hasAriaSort) {
                        columnHeader.attr('aria-sort', 'ascending');
                        hasAriaSort = true;
                    }
                }
                else if (sortIcon.hasClass('p-datatable-sort-desc-icon')) {
                    sortOrder = DataTable.SORT_ORDER.DESCENDING;
                    columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.otherMessage));
                    if (!hasAriaSort) {
                        columnHeader.attr('aria-sort', 'descending');
                        hasAriaSort = true;
                    }
                } else {
                    sortOrder = DataTable.SORT_ORDER.UNSORTED;
                    columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.ascMessage));
                    if (!hasAriaSort) {
                        columnHeader.attr('aria-sort', 'other');
                        hasAriaSort = true;
                    }
                }

                if (this.cfg.multiSort && this.cfg.sortMetaOrder) {
                    resolvedSortMetaIndex = $.inArray(columnHeaderId, this.cfg.sortMetaOrder);

                    this.sortMeta[resolvedSortMetaIndex] = {
                        col: columnHeaderId!,
                        order: sortOrder
                    };
                }

                this.updateReflowDD(columnHeader, sortOrder);
            }
            else {
                sortOrder = DataTable.SORT_ORDER.UNSORTED;
                columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.ascMessage));
                if(!hasAriaSort && i == (this.sortableColumns.length - 1)) {
                    this.sortableColumns.eq(0).attr('aria-sort', 'other');
                    hasAriaSort = true;
                }
            }

            columnHeader.data('sortorder', sortOrder);
        }

        this.sortableColumns.on('mouseenter.dataTable', (e) => {
            var column = $(e.currentTarget);
            column.addClass('p-state-hover');
        })
        .on('mouseleave.dataTable', (e) => {
            var column = $(e.currentTarget);
            column.removeClass('p-state-hover');
        })
        .on('blur.dataTable', (e) => {
            $(e.currentTarget).removeClass('p-state-focus');
        })
        .on('focus.dataTable', (e) => {
            $(e.currentTarget).addClass('p-state-focus');
        })
        .on('keydown.dataTable', (e) => {
            if((e.key === 'Enter') && $(e.target).is(':not(:input)')) {
                $(e.currentTarget).trigger('click.dataTable', (e.metaKey||e.ctrlKey));
                e.preventDefault();
            }
        })
        .on('click.dataTable', (e, metaKeyOn?: boolean) => {
            if(!this.shouldSort(e, e.currentTarget)) {
                return;
            }

            PrimeFaces.clearSelection();

            var columnHeader = $(e.currentTarget),
                sortOrderData = columnHeader.data('sortorder'),
                sortOrder = (sortOrderData === DataTable.SORT_ORDER.UNSORTED) ? DataTable.SORT_ORDER.ASCENDING :
                    (sortOrderData === DataTable.SORT_ORDER.ASCENDING) ? DataTable.SORT_ORDER.DESCENDING :
                        this.cfg.allowUnsorting ? DataTable.SORT_ORDER.UNSORTED : DataTable.SORT_ORDER.ASCENDING,
                metaKey = e.metaKey || e.ctrlKey || metaKeyOn;

            if(!this.cfg.multiSort || !metaKey) {
                this.sortMeta = [];
            }

            this.addSortMeta({
                col: columnHeader.attr('id')!,
                order: sortOrder
            });

            this.sort(columnHeader, sortOrder, this.cfg.multiSort && metaKey);

            if(this.cfg.scrollable) {
                $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).trigger('focus');
            }

            this.updateReflowDD(columnHeader, sortOrder);
        });

        this.updateSortPriorityIndicators();

        if(this.reflowDD && this.cfg.reflow) {
            PrimeFaces.skinSelect(this.reflowDD);
            this.reflowDD.on('change', (e) => {
                var selectedOption = $(e.currentTarget).find(":selected");
                var columnKey = selectedOption.data('columnkey');
                var sortOrder = selectedOption.data('sortorder');
                var columnHeader = this.jq.find(PrimeFaces.escapeClientId(columnKey));

                columnHeader.data('sortorder', sortOrder);
                columnHeader.trigger('click.dataTable');
            });
        }
    }

    /**
     * Creates the sort order message shown to indicate what the current sort order is.
     * @param ariaLabel Optional label text from an aria attribute.
     * @param sortOrderMessage Sort order message.
     * @return The sort order message to use.
     */
    getSortMessage(ariaLabel: string | undefined, sortOrderMessage: string | undefined): string {
        var headerName = ariaLabel ? ariaLabel.split(':')[0] : '';
        return headerName + ': ' + sortOrderMessage;
    }

    /**
     * Called in response to a click. Checks whether this DataTable should now be sorted. Returns `false` when there
     * are no items to be sorted, or when no sorting button was clicked.
     * @param event (Click) event that occurred.
     * @param column Column Column of this DataTable on which the event occurred.
     * @return `true` to perform a sorting operation, `false` otherwise.
     */
    shouldSort(event: JQuery.TriggeredEvent, column: Element): boolean {
        if(this.isEmpty()) {
            return false;
        }

        var target = $(event.target);
        if(target.closest('.p-column-customfilter', column).length) {
            return false;
        }

        return target.is('th,span');
    }

    /**
     * Adds the given sorting to the list of sort rows. Each sorting describes a column by which to sort. This data
     * table may be sorted by multiple columns.
     * @param meta Sorting to add.
     */
    addSortMeta(meta: PrimeFacesTypes.widget.DataTable.SortMeta): void {
        this.sortMeta = $.grep(this.sortMeta!, function(value) {
            return value.col !== meta.col;
        });

        this.sortMeta.push(meta);
    }

    /**
     * Binds filter events to standard filters
     */
    setupFiltering(): void {
        var filterColumns = this.thead.find('> tr > th.p-filter-column');
        this.cfg.filterEvent = this.cfg.filterEvent||'keyup';
        this.cfg.filterDelay = this.cfg.filterDelay||300;

        filterColumns.children('.p-column-filter').each((_i, el) => {
            var filter = $(el);

            if(filter.is("input[type='search']")) {
                PrimeFaces.skinInput(filter);
                this.bindTextFilter(filter);
            }
            else {
                PrimeFaces.skinSelect(filter);
                this.bindChangeFilter(filter);
            }

        });

        // ARIA labels for filters
        filterColumns.each((_i, el) => {
            var filterColumn = $(el);
            var filter = filterColumn.find(':input');
            var title = filterColumn.find('.p-column-title')

            if (filter && title) {
                filter.attr('aria-label', this.getLabel('filter') + " " + title.text());
            }
        });
    }

    /**
     * Sets up the event listeners for the text filters on a column.
     * @param filter INPUT element of the text filter.
     */
    bindTextFilter(filter: JQuery): void {
        if(this.cfg.filterEvent === 'enter')
            this.bindEnterKeyFilter(filter);
        else
            this.bindFilterEvent(filter);

        // #8113 clear 'x' event handler
        this.bindClearFilterEvent(filter);

        // #7562 draggable columns cannot be filtered with touch
        if (PrimeFaces.env.isTouchable(this.cfg)) {
            filter.on('touchstart', function(e) {
                e.stopPropagation();
            });
        }
    }

    /**
     * Sets up the change event listeners on the column filter elements.
     * @param filter DOM element of a column filter
     */
    bindChangeFilter(filter: JQuery): void {
        filter.off('change')
        .on('change', () => {
            this.filter();
        });
    }

    /**
     * Sets up the enter key event listeners for the text filters on a column.
     * @param filter INPUT element of the text filter.
     */
    bindEnterKeyFilter(filter: JQuery): void {
        filter.off('keydown').on('keydown', (e) => {
            if(PrimeFaces.utils.blockEnterKey(e)) {
                this.filter();
            }
        });
    }

    /**
     * Sets up the 'search' event which for HTML5 text search fields handles the clear 'x' button.
     * @param filter INPUT element of the text filter.
     */
    bindClearFilterEvent(filter: JQuery): void {
        filter.off('search.filterClear').on('search.filterClear', (e) => {
            // only care when 'X'' is clicked
            if ($(e.currentTarget).val() == "") {
                this.filter();
            }
        });

        // #14480 Safari does not trigger 'search' event on clear button
        if (PrimeFaces.env.browser.safari) {
            filter.off('input.filterClear').on('input.filterClear', (e) => {
                if ((e.currentTarget as HTMLInputElement).value === "") {
                    this.filter();
                }
            });
        }
    }

    /**
     * Sets up all event listeners for the given filter element of a column filter.
     * @param filter DOM element of a column filter.
     */
    bindFilterEvent(filter: JQuery): void {
        var filterEventName = this.cfg.filterEvent + '.dataTable';

        filter.off('keydown ' + filterEventName).on(filterEventName, (e) => {
            //prevent form submit on enter key
            if (e.key && (PrimeFaces.utils.ignoreFilterKey(e) || PrimeFaces.utils.blockEnterKey(e))) {
                return;
            }

            PrimeFaces.debounce(() => this.filter(), this.cfg.filterDelay);
        })
        .on('keydown', function(e) {
            // #12327 do not submit form on ENTER
            PrimeFaces.utils.blockEnterKey(e);
        });
    }

    /**
     * Sets up the DataTable and adds all event listeners required for hovering over rows.
     */
    setupRowHover(): void {
        var selector = '> tr.p-widget-content';
        if(!this.cfg.selectionMode || this.cfg.selectionMode === 'checkbox') {
            this.bindRowHover(selector);
        }
    }

    /**
     * Sets up WCAG keyboard navigation of cells.
     */
    setupNavigableCells(): void {
        if (!this.cfg.cellNavigation) {
            return;
        }
        var cellTabIndex = this.cfg.tabindex || "0";
        var pageRows = this.cfg.paginator && this.cfg.paginator.rows ? this.cfg.paginator.rows : 1000;
        var clickSelector = ':button:enabled, :input:enabled, a, [role="combobox"], .p-row-toggler, .p-chkbox-box';

        // helper function to set the current and next cell focus
        function makeFocusable(e: JQuery.TriggeredEvent | null, cell: JQuery | null, nextCell: JQuery | null): void {
            if (cell && cell.length) {
                cell.attr("tabindex", "-1");
            }

            if (nextCell && nextCell.length) {
                nextCell.attr("tabindex", cellTabIndex).trigger("focus");
                if (e) {
                    e.preventDefault();
                }
            }
        }

        // helper function to reset the state of the whole table
        const resetFocusable = (resetFirstCell: boolean): void => {
            var selector = resetFirstCell ? DataTable.cellSelector(':not(.p-hidden)') : DataTable.cellSelector('[tabindex="' + cellTabIndex + '"]');
            // default all cells to not focusable
            var focusableCells = this.getTbody().find(selector);
            focusableCells.attr("tabindex", "-1");

            if (resetFirstCell) {
                // the very first cell should be focusable
                focusableCells.first().attr("tabindex", cellTabIndex);
            }
        };

        // default all cells to not focusable except the very first cell
        resetFocusable(true);

        // on click should make it focusable
        this.getTbody().find(DataTable.cellSelector()).off('.focuscell')
            .on("click.focuscell", (e) => {
                resetFocusable(false);

                if ($(e.target).is(clickSelector)) {
                    return;
                }

                makeFocusable(null, null, $(e.currentTarget));
            })
            .on("keydown.focuscell", (e) => {
                if ($(e.target).is(":input:enabled")) {
                    return;
                }

                var cell = $(e.currentTarget);
                var nextCell: JQuery | null = null;
                var prevCell: JQuery | null = null;
                var currentRow: JQuery | null = null;

                switch (e.code) {
                    case "KeyW":
                        if (this.cfg.resizableColumns && e.altKey) {
                            this.autosizeColumnWidth(cell);
                        }
                        break;
                    case "ArrowLeft":
                        prevCell = this.isRTL ? cell.nextAll('[tabindex="-1"]:first') : cell.prevAll('[tabindex="-1"]:first');
                        makeFocusable(e, cell, prevCell);
                        break;
                    case "ArrowRight":
                        nextCell = this.isRTL ? cell.prevAll('[tabindex="-1"]:first') : cell.nextAll('[tabindex="-1"]:first');
                        makeFocusable(e, cell, nextCell);
                        break;
                    case "ArrowDown":
                        nextCell = cell.closest("tr[data-ri]").nextAll("tr[data-ri]:first").find(DataTable.cellSelector('[tabindex="-1"]') + ', ' + DataTable.cellSelector('.p-hidden')).eq(cell.index());
                        makeFocusable(e, cell, nextCell);
                        break;
                    case "ArrowUp":
                        prevCell = cell.closest("tr[data-ri]").prevAll("tr[data-ri]:first").find(DataTable.cellSelector('[tabindex="-1"]') + ', ' + DataTable.cellSelector('.p-hidden')).eq(cell.index());
                        makeFocusable(e, cell, prevCell);
                        break;
                    case "Home":
                        prevCell = cell.prevAll('[tabindex="-1"]').last();
                        if (e.ctrlKey) {
                            prevCell = cell.closest("tr[data-ri]").prevAll().last().find(DataTable.cellSelector('[tabindex="-1"]')).first();
                        }
                        makeFocusable(e, cell, prevCell);
                        break;
                    case "End":
                        nextCell = cell.nextAll('[tabindex="-1"]').last();
                        if (e.ctrlKey) {
                            nextCell = cell.closest("tr[data-ri]").nextAll().last().find(DataTable.cellSelector('[tabindex="-1"]')).last();
                        }
                        makeFocusable(e, cell, nextCell);
                        break;
                    case "PageUp":
                        // Select the current record
                        currentRow = cell.closest("tr[data-ri]");

                        // Navigate back 1 page records
                        for (var i = 0; i < pageRows; i++) {
                            currentRow = currentRow.prev("tr[data-ri]");
                            if (currentRow.length) {
                                prevCell = currentRow.find(DataTable.cellSelector('[tabindex="-1"]')).eq(cell.index());
                            } else {
                                break;
                            }
                        }
                        makeFocusable(e, cell, prevCell);
                        break;
                    case "PageDown":
                        // Select the current row
                        currentRow = cell.closest("tr[data-ri]");

                        // Navigate forward 1 page rows
                        for (var i = 0; i < pageRows; i++) {
                            currentRow = currentRow.next("tr[data-ri]");
                            if (currentRow.length) {
                                nextCell = currentRow.find(DataTable.cellSelector('[tabindex="-1"]')).eq(cell.index());
                            } else {
                                break;
                            }
                        }
                        makeFocusable(e, cell, nextCell);
                        break;
                    case "Space":
                    case "Enter":
                    case "NumpadEnter":
                        // #12198 allow meta keys for multiselection
                        if (this.cfg.selectionRowMode !== 'none' && (e.metaKey || e.ctrlKey || e.shiftKey)) {
                            return;
                        }
                        // Find the first child element with a click event bound
                        var $clickable = cell.find(clickSelector).first();
                        if ($clickable.length) {
                            $clickable.trigger('click');
                            e.stopPropagation();
                            e.preventDefault();
                        }
                        else {
                            cell.trigger('click');
                        }
                        break;
                    default:
                        break;
                }
            });
    }

    /**
     * Sets up the DataTable and adds all event listener required for selecting rows.
     */
    setupSelection(): void {
        this.selectionHolder = this.jqId + '_selection';
        this.cfg.selectionRowMode = this.cfg.selectionRowMode||'new';
        this.rowSelector = '> tr.p-widget-content.p-datatable-selectable';
        this.cfg.disabledTextSelection = this.cfg.disabledTextSelection === false ? false : true;
        this.cfg.selectionPageOnly = this.cfg.selectionPageOnly !== false;
        this.rowSelectorForRowClick = this.cfg.rowSelector||DataTable.cellSelector(':not(.p-column-unselectable):not(.p-grouped-column)') + ',span:not(.p-c)';

        var preselection = $(this.selectionHolder).val() as string | undefined;
        this.selection = !preselection ? [] : preselection.split(',');

        // set aria labels
        this.tbody.find(this.rowSelector).each((_i, el) => {
            this.updateSelectionAria($(el))
        });

        this.bindSelectionEvents();

        //shift key based range selection
        if (this.isCheckboxSelectionEnabled()) {
            this.originRowMeta = null;
            this.cursorRowMeta = null;
        }

        this.originRowMeta = this.originRowMeta ? this.getRowMeta(this.tbody.find("[data-rk='" + this.originRowMeta.key + "']")) : null;
        if (this.cursorRowMeta) {
            // #3551 lookup row after update by row key
            var cursorRow = this.tbody.find("[data-rk='" + this.cursorRowMeta.key + "']");
            this.cursorRowMeta = this.getRowMeta(cursorRow);

            if (this.isMultipleSelection() && this.originRowMeta !== null) {
                this.selectRowsInRange(cursorRow, true);
            }
            else {
                this.originRowMeta = this.getRowMeta(cursorRow);
                this.cursorRowMeta = null;
                this.selectRow(cursorRow, true);
            }
        }
        else {
            this.cursorRowMeta = null;
        }
    }

    /**
     * Applies events related to selection in a non-obtrusive way
     */
    bindSelectionEvents(): void {
        if(this.cfg.selectionMode === 'radio') {
            this.bindRadioEvents();

            if(this.cfg.selectionRowMode !== 'none') {
                this.bindRowEvents();
            }
            else {
                this.jq.find('tr.p-datatable-selectable').css('cursor', 'default');
            }
        }
        else if(this.cfg.selectionMode === 'checkbox') {
            this.bindCheckboxEvents();
            this.updateHeaderCheckbox();

            if(this.cfg.selectionRowMode !== 'none') {
                this.bindRowEvents();
            }
            else {
                this.jq.find('tr.p-datatable-selectable').css('cursor', 'default');
            }
        }
        else {
            this.bindRowEvents();
        }
    }

    /**
     * Sets up all event listeners for event triggered on a row of this DataTable.
     */
    bindRowEvents(): void {
        this.bindRowHover(this.rowSelector!);

        this.tbody.off('click.dataTable mousedown.dataTable', this.rowSelector).on('mousedown.dataTable', this.rowSelector!, null, () => {
            this.mousedownOnRow = true;
        })
        .on('click.dataTable', this.rowSelector!, null, (e) => {
            this.onRowClick(e, e.currentTarget);
            this.mousedownOnRow = false;
        });

        //double click
        if (this.hasBehavior('rowDblselect')) {
            this.tbody.off('dblclick.dataTable', this.rowSelector).on('dblclick.dataTable', this.rowSelector!, null, (e) => {
                this.onRowDblclick(e, $(e.currentTarget));
            });
        };

        this.bindSelectionKeyEvents();
    }

    /**
     * Sets up all delegated event listeners on the table body.
     */
    bindSelectionKeyEvents(): void {
        this.getFocusableTbody().on('focus', () => {
            //ignore mouse click on row
            if(!this.mousedownOnRow) {
                this.focusedRow = this.tbody.children('tr.p-widget-content.p-datatable-selectable.p-state-highlight').eq(0);
                if (this.focusedRow.length == 0) {
                    this.focusedRow = this.tbody.children('tr.p-widget-content.p-datatable-selectable').eq(0);
                }

                this.highlightFocusedRow();

                if(this.cfg.scrollable) {
                    PrimeFaces.scrollInView(this.scrollBody!, this.focusedRow);
                }
            }
        })
        .on('blur', () => {
            if(this.focusedRow) {
                this.unhighlightFocusedRow();
                this.focusedRow = null;
            }
        })
        .on('keydown', (e) => {
            if($(e.target).is(':input')) {
                return;
            }

            if(this.focusedRow) {
                switch(e.code) {
                    case 'ArrowUp':
                    case 'ArrowDown':
                        var rowSelector = 'tr.p-widget-content.p-datatable-selectable',
                        row = e.key === 'ArrowUp' ? this.focusedRow.prevAll(rowSelector).eq(0) : this.focusedRow.nextAll(rowSelector).eq(0);

                        if(row.length) {
                            this.unhighlightFocusedRow();

                            if(this.isCheckboxSelectionEnabled()) {
                                row.find('> td.p-selection-column .p-chkbox-box').trigger('focus');
                            }
                            else {
                                this.focusedRow = row;
                            }

                            this.highlightFocusedRow();

                            if(this.cfg.scrollable) {
                                PrimeFaces.scrollInView(this.scrollBody!, this.focusedRow!);
                            }
                        }
                        e.preventDefault();
                    break;

                    case 'Enter':
                    case 'NumpadEnter':
                    case 'Space':
                        if(this.focusedRowWithCheckbox) {
                            this.focusedRow.find('> td.p-selection-column > div.p-chkbox > div.p-chkbox-box').trigger('click.dataTable');
                        }
                        else {
                            e.target = this.focusedRow.children().eq(0).get(0)!;
                            this.onRowClick(e,this.focusedRow.get(0)!);
                        }

                        e.preventDefault();
                    break;

                    default:
                    break;
                };
            }
        });

    }

    /**
     * Highlights the currently focused row (if any) by adding the appropriate CSS class.
     */
    highlightFocusedRow(): void {
        this.focusedRow!.addClass('p-state-hover');
    }

    /**
     * Unhighlights the currently focused row (if any) by adding the appropriate CSS class.
     */
    unhighlightFocusedRow(): void {
        this.focusedRow!.removeClass('p-state-hover');
    }

    /**
     * Stores the row which is currently focused.
     * @param row Row to set as the focused row.
     */
    assignFocusedRow(row: JQuery): void {
        this.focusedRow = row;
    }

    /**
     * Sets up the event listeners for hovering over a DataTable row.
     * @param rowSelector Selector for the row elements. Any hover event that does not reach an element that matches
     * this selector will be ignored.
     */
    bindRowHover(rowSelector: string): void {
        this.tbody.off('mouseenter.dataTable mouseleave.dataTable', rowSelector)
            .on('mouseenter.dataTable', rowSelector, null, (e) => {
                if (!this.ignoreRowHoverEvent) {
                    $(e.currentTarget).addClass('p-state-hover');
                }
            })
            .on('mouseleave.dataTable', rowSelector, null, (e) => {
                if (!this.ignoreRowHoverEvent) {
                    $(e.currentTarget).removeClass('p-state-hover');
                }
            });

        if (this.cfg.groupColumnIndexes) {
            var columnSelector = rowSelector + ' > *';
            this.tbody.off('mouseenter.dataTable mouseleave.dataTable', columnSelector)
                .on('mouseenter.dataTable', columnSelector, null, (e) => {
                    var row = $(e.currentTarget).parent();
                    if ($(e.currentTarget).hasClass('p-grouped-column')) {
                        row.removeClass('p-state-hover');
                        this.ignoreRowHoverEvent = true;
                    }
                    else {
                        row.addClass('p-state-hover');
                    }
                })
                .on('mouseleave.dataTable', columnSelector, null, (e) => {
                    if (!$(e.currentTarget).hasClass('p-grouped-column')) {
                        this.ignoreRowHoverEvent = false;
                    }
                });
        }
    }

    /**
     * Sets up the event listeners for radio buttons contained in this DataTable.
     */
    bindRadioEvents(): void {
        var radioInputSelector = '> tr.p-widget-content:not(.p-datatable-empty-message) > td.p-selection-column :radio';

        if(this.cfg.nativeElements) {
            this.tbody.off('click.dataTable', radioInputSelector).on('click.dataTable', radioInputSelector, null, (e) => {
                var radioButton = $(e.currentTarget);

                if(!radioButton.prop('checked'))
                    this.selectRowWithRadio(radioButton);
            });
        }
        else {
            var radioSelector = '> tr.p-widget-content:not(.p-datatable-empty-message) > td.p-selection-column .p-radiobutton .p-radiobutton-box';
            this.tbody.off('click.dataTable mouseenter.dataTable mouseleave.dataTable', radioSelector)
                .on('mouseenter.dataTable', radioSelector, null, (e) => {
                    var radio = $(e.currentTarget);
                    if(!radio.hasClass('p-state-disabled')) {
                        radio.addClass('p-state-hover');
                    }
                })
                .on('mouseleave.dataTable', radioSelector, null, (e) => {
                    var radio = $(e.currentTarget);
                    radio.removeClass('p-state-hover');
                })
                .on('click.dataTable', radioSelector, null, (e) => {
                    var radio = $(e.currentTarget),
                    checked = radio.hasClass('p-state-active'),
                    disabled = radio.hasClass('p-state-disabled');

                    if (!disabled) {
                        radio.prev().children(':radio').trigger('focus.dataTable');
                        if (!checked) {
                            this.selectRowWithRadio(radio);
                        }
                    }
                });
        }

        //keyboard support
        this.tbody.off('focus.dataTable blur.dataTable change.dataTable', radioInputSelector)
            .on('focus.dataTable', radioInputSelector, null, (e) => {
                var input = $(e.currentTarget),
                box = input.parent().next();

                box.addClass('p-state-focus');
            })
            .on('blur.dataTable', radioInputSelector, null, (e) => {
                var input = $(e.currentTarget),
                box = input.parent().next();

                box.removeClass('p-state-focus');
            })
            .on('change.dataTable', radioInputSelector, null, () => {
                var currentInput = this.tbody.find(radioInputSelector).filter(':checked'),
                currentRadio = currentInput.parent().next();

                this.selectRowWithRadio(currentRadio);
            });

    }

    /**
     * Sets up the event listeners for radio buttons contained in this DataTable.
     */
    bindCheckboxEvents(): void {
        var checkboxSelector: string;

        if(this.cfg.nativeElements) {
            checkboxSelector = 'tr.p-widget-content.p-datatable-selectable > td.p-selection-column :checkbox';
            this.checkAllToggler = this.thead.find('> tr > th.p-selection-column > :checkbox');

            this.checkAllToggler.on('click', () => {
                this.toggleCheckAll();
            });

            this.jq.off('click.dataTable', checkboxSelector).on('click.dataTable', checkboxSelector, null, (e) => {
                var checkbox = $(e.currentTarget);

                if(checkbox.prop('checked'))
                    this.selectRowWithCheckbox(checkbox, e);
                else
                    this.unselectRowWithCheckbox(checkbox, e);

                e.preventDefault();
                e.stopPropagation();
            });
        }
        else {
            checkboxSelector = 'tr.p-widget-content.p-datatable-selectable > td.p-selection-column > div.p-chkbox > div.p-chkbox-box';
            this.checkAllToggler = this.thead.find('> tr > th.p-selection-column > div.p-chkbox.p-chkbox-all > div.p-chkbox-box');

            this.checkAllToggler.on('mouseenter', (e) => {
                var box = $(e.currentTarget);
                if(!box.hasClass('p-state-disabled')) {
                    box.addClass('p-state-hover');
                }
            })
            .on('mouseleave', (e) => {
                $(e.currentTarget).removeClass('p-state-hover');
            })
            .on('click', (e) => {
                var box = $(e.currentTarget);
                if(!box.hasClass('p-state-disabled')) {
                    this.toggleCheckAll();
                }
            })
            .on('keydown', (e) => {
                if (PrimeFaces.utils.isActionKey(e)) {
                    if(!$(e.currentTarget).hasClass('p-state-disabled')) {
                        this.toggleCheckAll();
                        e.preventDefault();
                    }
                }
            });

            this.jq.off('mouseenter.dataTable mouseleave.dataTable click.dataTable keydown.dataTable', checkboxSelector)
            .on({
                'mouseenter.dataTable': (e: JQuery.TriggeredEvent) => {
                    $(e.currentTarget).addClass('p-state-hover');
                },
                'mouseleave.dataTable': (e: JQuery.TriggeredEvent) => {
                    $(e.currentTarget).removeClass('p-state-hover');
                },
                'click.dataTable': (e: JQuery.TriggeredEvent) => {
                    var checkbox = $(e.currentTarget)
                    if(checkbox.attr('aria-checked') === "true") {
                        this.unselectRowWithCheckbox(checkbox, e);
                    }
                    else {
                        this.selectRowWithCheckbox(checkbox, e);
                    }
                    e.preventDefault();
                    e.stopPropagation();
                },
                'keydown.dataTable': (e: JQuery.TriggeredEvent) => {
                    if (this.cfg.selectionRowMode === 'none' && PrimeFaces.utils.isActionKey(e)) {
                        $(e.currentTarget).trigger('click');
                        e.preventDefault();
                    }
                }
            }, checkboxSelector);
        }
        this.configureSelectAllAria();

        //keyboard support
        this.tbody.off('focus.dataTable blur.dataTable change.dataTable', checkboxSelector)
                    .on('focus.dataTable', checkboxSelector, null, (e) => {
                        var input = $(e.currentTarget);

                        input.addClass('p-state-focus');

                        this.focusedRow = input.closest('.p-datatable-selectable');
                        this.focusedRowWithCheckbox = true;
                    })
                    .on('blur.dataTable', checkboxSelector, null, (e) => {
                        var input = $(e.currentTarget);

                        input.removeClass('p-state-focus');

                        this.unhighlightFocusedRow();
                        this.focusedRow = null;
                        this.focusedRowWithCheckbox = false;
                    })
                    .on('change.dataTable', checkboxSelector, null, (e) => {
                        var input = $(e.currentTarget);

                        if(input.attr('aria-checked') === "true" || input.prop('checked')) {
                            this.selectRowWithCheckbox(input, e);
                        }
                        else {
                            this.unselectRowWithCheckbox(input, e);
                        }
                    });

        this.checkAllToggler.on('focus.dataTable', (e) => {
                        var input = $(e.currentTarget);

                        if(!input.hasClass('p-state-disabled')) {
                            input.addClass('p-state-focus');
                        }
                    })
                    .on('blur.dataTable', (e) => {
                        var input = $(e.currentTarget);

                        input.removeClass('p-state-focus');
                    })
                    .on('change.dataTable', (e) => {
                        var input = $(e.currentTarget);

                        if(!input.hasClass('p-state-disabled')) {
                            if((input.attr('aria-checked') !== "true") && !input.prop('checked')) {
                                input.addClass('p-state-active');
                            }

                            this.toggleCheckAll();

                            if(input.attr('aria-checked') === "true" || input.prop('checked')) {
                                input.removeClass('p-state-active');
                            }
                        }
                    });
    }

    /**
     * Expands or collapses the given row, depending on whether it is currently collapsed or expanded, respectively.
     * @param row A row (`TR`) to expand or collapse.
     */
    toggleRow(row: JQuery): void {
        if(row && !this.isRowTogglerClicked) {
            var toggler = row.find('> td > div.p-row-toggler');
            this.toggleExpansion(toggler);
        }
        this.isRowTogglerClicked = false;
    }

    /**
     * Applies events related to row expansion in a non-obtrusive way
     */
    bindExpansionEvents(): void {
        var togglerSelector = '> tr > td > div.p-row-toggler';

        this.tbody.off('click.datatable-expansion', togglerSelector)
            .on('click.datatable-expansion', togglerSelector, null, (e) => {
                this.isRowTogglerClicked = true;
                this.toggleExpansion($(e.currentTarget));
            })
            .on('keydown.datatable-expansion', togglerSelector, null, (e) => {
                if(e.key === 'Enter') {
                    this.toggleExpansion($(e.currentTarget));
                    e.preventDefault();
                }
            });
        // set aria labels
        this.tbody.find(togglerSelector).each((_i, el) => {
            this.updateExpansionAria($(el))
        });
    }

    /**
     * Configures the ARIA label for the row expander.
     * @param toggler the toggler button
     */
    updateExpansionAria(toggler: JQuery): void {
        if (toggler) {
            var row = toggler.closest('tr');
            var rowMeta = this.getRowMeta(row);
            var expanded = toggler.attr('aria-expanded') === "true";
            var ariaLabel = expanded ? this.getAriaLabel('collapseLabel') : this.getAriaLabel('expandLabel');
            if (rowMeta && rowMeta.key) {
                ariaLabel += " " + rowMeta.key;
            }
            toggler.attr('aria-label', ariaLabel);
        }
    }

    override bindContextMenu(
        menuWidget: PrimeFacesTypes.widget.ContextMenuLikeWidget,
        targetWidget: this,
        targetId: string,
        cfg: PrimeFacesTypes.widget.PartialWidgetCfg<PrimeFacesTypes.widget.ContextMenuLikeWidgetCfg>,
    ): void {
        var targetSelector = targetId + ' tbody.p-datatable-data > tr.p-widget-content';
        var targetEvent = cfg.event + '.row' + this.id;
        var containerEvent = cfg.event!.split(' ').map(e => e + '.datatable' + this.id).join(' ');
        this.contextMenuWidget = menuWidget;

        $(document).off(targetEvent, targetSelector).on(targetEvent, targetSelector, null, (e: JQuery.TriggeredEvent) => {
            var row = $(e.currentTarget);

            if(targetWidget.cfg.selectionMode && row.hasClass('p-datatable-selectable')) {
                var isContextMenuDelayed = targetWidget.onRowRightClick(e, e.currentTarget, cfg.selectionMode!, () => {
                    this.contextMenuWidget!.show(e);
                });
                targetWidget.updateContextMenuCell(e, targetWidget);

                if(isContextMenuDelayed) {
                    e.preventDefault();
                    e.stopPropagation();
                }
            }
            else if(targetWidget.cfg.editMode === 'cell') {
                targetWidget.updateContextMenuCell(e, targetWidget);
                this.contextMenuWidget!.show(e);
            }
            else if(row.hasClass('p-datatable-empty-message') && !this.cfg.disableContextMenuIfEmpty) {
                this.contextMenuWidget!.show(e);
            }
        });
        $(document).off(containerEvent, targetId).on(containerEvent, targetId, null, (e: JQuery.TriggeredEvent) => {
            if (e.target.id == targetWidget.id + '_data') {
                this.contextMenuWidget!.show(e);
            }
        });
        this.addDestroyListener(function() {
            $(document).off(targetEvent + ' ' + containerEvent);
        });

        if(this.cfg.scrollable && this.scrollBody) {
            this.scrollBody.off('scroll.dataTable-contextmenu').on('scroll.dataTable-contextmenu', () => {
                if(this.contextMenuWidget!.jq.is(':visible')) {
                    this.contextMenuWidget!.hide();
                }
            });
        }
    }

    /**
     * Updates the currently selected cell based on where the context menu right click occurred.
     * @param event Event that occurred.
     * @param targetWidget The current widget
     */
    updateContextMenuCell(event: JQuery.TriggeredEvent, targetWidget: DataTable): void {
        var target = $(event.target),
        cell = target.closest(DataTable.cellSelector('.p-editable-column'));

        if(targetWidget.contextMenuCell) {
            targetWidget.contextMenuCell.removeClass('p-state-highlight');
        }

        targetWidget.contextMenuCell = cell;
        targetWidget.contextMenuCell.addClass('p-state-highlight');
    }

    /**
     * Sets up the event listeners for clicking on a row.
     */
    bindRowClick(): void {
        var rowSelector = '> tr.p-widget-content:not(.p-expanded-row-content)';
        this.tbody.off('click.dataTable-rowclick', rowSelector).on('click.dataTable-rowclick', rowSelector, null, (e) => {
            var target = $(e.target),
            row = target.is('tr.p-widget-content') ? target : target.closest('tr.p-widget-content');

            this.cfg.onRowClick!.call(e.currentTarget, row);
        });
    }

    /**
     * Reflow mode is a responsive mode to display columns as stacked depending on screen size.
     */
    initReflow(): void {
        var headerColumns = this.thead.find('> tr > th');

        for(var i = 0; i < headerColumns.length; i++) {
            var headerColumn = headerColumns.eq(i),
            reflowHeaderText = headerColumn.find('.p-reflow-headertext:first').text(),
            colTitleEl = headerColumn.children('.p-column-title'),
            title = (reflowHeaderText && reflowHeaderText.length) ? reflowHeaderText : colTitleEl.text();

            var column = this.tbody.find('> tr:not(.p-datatable-empty-message,.p-datatable-summaryrow) > *:nth-child(' + (i + 1) + ')')
            column.find(".p-column-title").remove(); // #11078
            column.prepend('<span class="p-column-title">' + PrimeFaces.escapeHTML(title) + '</span>');
        }
    }

    /**
     * Prepares this DataTable for the current scrolling settings and sets up all related event handlers.
     */
    setupScrolling(): void {
        this.scrollHeader = this.jq.children('.p-datatable-scrollable-header');
        this.scrollBody = this.jq.children('.p-datatable-scrollable-body');
        this.scrollFooter = this.jq.children('.p-datatable-scrollable-footer');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        this.scrollHeaderBox = this.scrollHeader.children('div.p-datatable-scrollable-header-box');
        this.scrollFooterBox = this.scrollFooter.children('div.p-datatable-scrollable-footer-box');
        this.headerTable = this.scrollHeaderBox.children('table');
        this.bodyTable = this.cfg.virtualScroll ? this.scrollBody.children('div').children('table') : this.scrollBody.children('table');
        this.scrollTbody = this.bodyTable.children('tbody');
        this.footerTable = this.scrollFooter.children('table');
        this.footerCols = this.scrollFooter.find('> .p-datatable-scrollable-footer-box > table > tfoot > tr > td');
        this.percentageScrollHeight = this.cfg.scrollHeight && (this.cfg.scrollHeight.indexOf('%') !== -1);
        this.percentageScrollWidth = this.cfg.scrollWidth && (this.cfg.scrollWidth.indexOf('%') !== -1);
        var scrollBarWidth = this.getScrollbarWidth() + 'px';

        if(this.cfg.scrollHeight) {
            if(this.percentageScrollHeight) {
                this.adjustScrollHeight();
            }

            if(this.hasVerticalOverflow()) {
                var marginProperty = this.isRTL ? 'margin-left' : 'margin-right';
                this.scrollHeaderBox.css(marginProperty, scrollBarWidth);
                this.scrollFooterBox.css(marginProperty, scrollBarWidth);
            }
        }

        if (!this.cfg.reflow) {
            this.fixColumnWidths();
        }

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
            this.cfg.liveScrollBuffer = (100 - this.cfg.liveScrollBuffer!) / 100;
            this.shouldLiveScroll = true;
            this.loadingLiveScroll = false;
            this.allLoadedLiveScroll = this.cfg.scrollStep! >= this.cfg.scrollLimit!;
        }

        this.restoreScrollState();

        if(this.cfg.virtualScroll) {
            var row = this.bodyTable.children('tbody').children('tr.p-widget-content');
            if(row) {
                var hasEmptyMessage = row.eq(0).hasClass('p-datatable-empty-message'),
                scrollLimit = this.cfg.scrollLimit;

                if(hasEmptyMessage) {
                    scrollLimit = 1;
                    this.bodyTable.css('top', '0px');
                }

                this.rowHeight = row.outerHeight();
                this.scrollBody.children('div').css('height', parseFloat((scrollLimit! * this.rowHeight! + 1) + 'px'));

                if(hasEmptyMessage && this.cfg.scrollHeight && this.percentageScrollHeight) {
                    PrimeFaces.queueTask(() => {
                        this.adjustScrollHeight();
                    });
                }
            }
        }

        this.scrollBody.on('scroll.dataTable', (e) => {
            var scrollShift = this.scrollBody!.scrollLeft()!;

            if (this.isRTL) {
                this.scrollHeaderBox!.css('margin-right', scrollShift + 'px');
                this.scrollFooterBox!.css('margin-right', scrollShift + 'px');
            }
            else {
                this.scrollHeaderBox!.css('margin-left', -scrollShift + 'px');
                this.scrollFooterBox!.css('margin-left', -scrollShift + 'px');
            }

            if(this.isEmpty()) {
                return;
            }

            if(this.cfg.virtualScroll) {
                var virtualScrollBody = e.currentTarget as HTMLElement;

                clearTimeout(this.scrollTimeout);
                this.scrollTimeout = PrimeFaces.queueTask(() => {
                    var viewportHeight = this.scrollBody!.outerHeight()!,
                    tableHeight = this.bodyTable!.outerHeight()!,
                    pageHeight = this.rowHeight! * this.cfg.scrollStep!,
                    virtualTableHeight = parseFloat((this.cfg.scrollLimit! * this.rowHeight!) + 'px'),
                    pageCount = (virtualTableHeight / pageHeight)||1;

                    if(virtualScrollBody.scrollTop + viewportHeight > parseFloat(this.bodyTable!.css('top')) + tableHeight || virtualScrollBody.scrollTop < parseFloat(this.bodyTable!.css('top'))) {
                        var page = Math.floor((virtualScrollBody.scrollTop * pageCount) / (virtualScrollBody.scrollHeight)) + 1;
                        this.loadRowsWithVirtualScroll(page, () => {
                            this.bodyTable!.css('top', ((page - 1) * pageHeight) + 'px');
                        });
                    }
                }, 200);
            }
            else if(this.shouldLiveScroll) {
                var scrollTop = Math.ceil((e.currentTarget as HTMLElement).scrollTop),
                scrollHeight = (e.currentTarget as HTMLElement).scrollHeight,
                viewportHeight = (e.currentTarget as HTMLElement).clientHeight;

                if((scrollTop >= ((scrollHeight * this.cfg.liveScrollBuffer!) - (viewportHeight))) && this.shouldLoadLiveScroll()) {
                    this.loadLiveRows();
                }
            }

            this.saveScrollState();
        });

        this.scrollHeader.on('scroll.dataTable', () => {
            this.scrollHeader!.scrollLeft(0);
        });

        this.scrollFooter.on('scroll.dataTable', () => {
            this.scrollFooter!.scrollLeft(0);
        });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', this.jq, () => {
            if (this.percentageScrollHeight) {
                this.adjustScrollHeight();
            }
            if (this.percentageScrollWidth) {
                this.adjustScrollWidth();
            }
        });

        // Add or remove CSS classes based on scroll overflow direction
        const el = this.scrollBody && this.scrollBody[0];
        if (el) {
            const hasHorizontalScroll = el.scrollWidth > el.clientWidth;
            const hasVerticalScroll = el.scrollHeight > el.clientHeight;

            el.classList.toggle('p-scrollable-x', hasHorizontalScroll);
            el.classList.toggle('p-scrollable-y', hasVerticalScroll);
        }
    }

    /**
     * When live scrolling (loading more items on-demand) is enabled, checks whether more items are allowed to be loaded
     * right now. Returns `false` when live scroling is disabled or items are currently being loaded already.
     * @return `true` if more items may be loaded, `false` otherwise.
     */
    shouldLoadLiveScroll(): boolean {
        return (!this.loadingLiveScroll && !this.allLoadedLiveScroll);
    }

    /**
     * Resets the column widths of this DataTable.
     */
    resetColumnsWidth(): void {
        this.bodyTable!.find(".p-datatable-scrollable-theadclone").remove();
        this.cloneTableHeader(this.thead, this.bodyTable!);
    }

    /**
     * Clones a table header and removes duplicate IDs.
     * @param thead The head (`THEAD`) of the table to clone.
     * @param table The table to which the head belongs.
     * @return The cloned table head.
     */
    cloneTableHeader(thead: JQuery, table: JQuery): JQuery {
        const theadClone = thead.clone();
        const firstRow = this.firstRow();
        const isAutoLayout = firstRow.length > 0 && this.isAutoLayout();

        if (isAutoLayout) {
            theadClone.find("th").each((_i, el) => {
                $(el).css({width: "", minWidth: ""}); // clear out width so table-layout:auto is properly recalculated
            });
            theadClone.insertBefore(this.tbody);
        }

        theadClone.find("th").each((i, el) => {
            const th = $(el);

            if (isAutoLayout) {
                const td = firstRow.children().eq(i);
                const width = td.width();
                th.css({width: width + "px"}).data("width", width!); // remember width in data so minWidth can be set after prependTo
            }

            th.attr("id", th.attr("id") + "_clone");
            th.removeAttr("aria-label");
            th.children().not(".p-column-title").remove();
            th.children(".p-column-title").children().remove();
        });

        theadClone.removeAttr("id").addClass("p-datatable-scrollable-theadclone").height(0).prependTo(table);

        if (isAutoLayout) {
            thead.find("th").each((i, el) => {
                const th = $(el);
                const thClone = theadClone.find("th").eq(i);
                const width = thClone.data("width"); // because setting minWidth doesn't work before prependTo
                th.css({width: width + "px", minWidth: width + "px"});
                thClone.css({width: width + "px", minWidth: width + "px"});
            });

            if (!thead.data("resizeTimer")) {
                thead.data("resizeTimer", true);
                const thiz = this;
                const resizeNamespace = 'resize.' + this.id + '_autolayout';
                var resizeTimer: number | undefined;

                $(window).off(resizeNamespace).on(resizeNamespace, function() {
                    clearTimeout(resizeTimer);
                    resizeTimer = setTimeout(thiz.resetColumnsWidth.bind(thiz), 100);
                });
                const unbindResize = () => {
                    clearTimeout(resizeTimer);
                    $(window).off(resizeNamespace);
                };
                this.addRefreshListener(unbindResize);
                this.addDestroyListener(unbindResize);
            }
        }

        return theadClone;
    }

    /**
     * Creates and stores a cloned copy of the table head(er) of this DataTable, and sets up some event handlers.
     */
    cloneHead(): void {
        if (this.theadClone) {
            PrimeFaces.utils.cleanseDomElement(this.theadClone);
        }
        this.theadClone = this.cloneTableHeader(this.thead, this.bodyTable!);

        //reflect events from clone to original
        if(this.cfg.sorting) {
            this.sortableColumns!.removeAttr('tabindex').off('blur.dataTable focus.dataTable keydown.dataTable');

            var clonedColumns = this.theadClone.find('> tr > th'),
            clonedSortableColumns = clonedColumns.filter('.p-sortable-column');
            clonedColumns.each((_i, el) => {
                var col = $(el),
                originalId = col.attr('id')!.split('_clone')[0]!;
                if(col.hasClass('p-sortable-column')) {
                    col.data('original', originalId);
                }

                $(PrimeFaces.escapeClientId(originalId))[0]!.style.width = col[0]!.style.width;
            });

            clonedSortableColumns.on('blur.dataTable', (e) => {
                $(PrimeFaces.escapeClientId($(e.currentTarget).data('original'))).removeClass('p-state-focus');
            })
            .on('focus.dataTable', (e) => {
                $(PrimeFaces.escapeClientId($(e.currentTarget).data('original'))).addClass('p-state-focus');
            })
            .on('keydown.dataTable', (e) => {
                if((e.key === 'Enter') && $(e.target).is(':not(:input)')) {
                    $(PrimeFaces.escapeClientId($(e.currentTarget).data('original'))).trigger('click.dataTable', (e.metaKey||e.ctrlKey));
                    e.preventDefault();
                }
            });
        }
    }

    /**
     * Adjusts the height of the body of this DataTable for the current scrolling settings.
     */
    adjustScrollHeight(): void {
        var relativeHeight = this.jq.parent().innerHeight()! * (parseInt(this.cfg.scrollHeight!) / 100),
        headerChilden = this.jq.children('.p-datatable-header'),
        footerChilden = this.jq.children('.p-datatable-footer'),
        tableHeaderHeight = (headerChilden.length > 0) ? headerChilden.outerHeight(true)! : 0,
        tableFooterHeight = (footerChilden.length > 0) ? footerChilden.outerHeight(true)! : 0,
        scrollersHeight = (this.scrollHeader!.outerHeight(true)! + this.scrollFooter!.outerHeight(true)!),
        paginatorsHeight: number = this.paginator ? this.paginator.getContainerHeight(true) : 0,
        height = (relativeHeight - (scrollersHeight + paginatorsHeight + tableHeaderHeight + tableFooterHeight));

        if(this.cfg.virtualScroll) {
            this.scrollBody!.css('max-height', height + 'px');
        }
        else {
            this.scrollBody!.height(height);
        }
    }

    /**
     * Adjusts the width of the header, body, and footer of this DataTable to fit the current settings.
     */
    adjustScrollWidth(): void {
        // parseInt of a number, String() is what parseInt does implicitly
        var width = parseInt(String(this.jq.parent().innerWidth()! * (parseInt(this.cfg.scrollWidth!) / 100)));
        this.setScrollWidth(width);
    }

    /**
     * Applies the given width to this DataTable.
     * @param element Element of the DataTable.
     * @param width New width in pixels to set.
     */
    setOuterWidth(element: JQuery, width: number): void {
        if (element.css('box-sizing') === 'border-box') { // Github issue: #5014
            element.outerWidth(width);
        }
        else {
            element.width(width);
        }
    }

    /**
     * Retrieves width information of the given column.
     * @param col The column of which the width should be retrieved.
     * @param isIncludeResizeableState Tells whether the width should be retrieved from the resizable state, if it
     * exists.
     * @return The width information of the given column.
     */
    getColumnWidthInfo(col: JQuery, isIncludeResizeableState?: boolean): PrimeFacesTypes.widget.DataTable.WidthInfo {
        var width: string | number | null | undefined, isOuterWidth: boolean | string | undefined;

        if(isIncludeResizeableState && this.resizableState) {
            width = this.findColWidthInResizableState(col.attr('id')!);
            isOuterWidth = false;
        }

        if(!width) {
            width = col[0]!.style.width;
            isOuterWidth = width && (col.css('box-sizing') === 'border-box');
        }

        if(!width) {
            width = col.width();
            isOuterWidth = false;
        }

        return {
            width: width!,
            isOuterWidth: isOuterWidth as boolean
        };
    }

    /**
     * Applies the width information to the given element.
     * @param element The element to which the width should be applied.
     * @param widthInfo The width information (retrieved using the method {@link getColumnWidthInfo}).
     */
    applyWidthInfo(element: JQuery, widthInfo: PrimeFacesTypes.widget.DataTable.WidthInfo): void {
        if(widthInfo.isOuterWidth) {
            element.outerWidth(widthInfo.width);
        }
        else {
            element.width(widthInfo.width);
        }
    }

    /**
     * Applies the given scroll width to this DataTable.
     * @param width Scroll width in pixels to set.
     */
    setScrollWidth(width: number): void {
        this.jq.children('.p-widget-header').each((_i, el) => {
            this.setOuterWidth($(el), width);
        });
        this.scrollHeader!.width(width);
        this.scrollBody!.css('margin-right', '0px').width(width);
        this.scrollFooter!.width(width);
    }

    /**
     * Adds some margin to the scroll body to make it align properly.
     */
    alignScrollBody(): void {
        var margin = this.hasVerticalOverflow() ? this.getScrollbarWidth() + 'px' : '0px';

        var marginProperty = this.isRTL ? 'margin-left' : 'margin-right';
        this.scrollHeaderBox!.css(marginProperty, margin);
        this.scrollFooterBox!.css(marginProperty, margin);
    }

    /**
     * Finds the width of the current scrollbar used for this DataTable.
     * @return The width in pixels of the scrollbar of this DataTable.
     */
    getScrollbarWidth(): number {
        if(!this.scrollbarWidth) {
            this.scrollbarWidth = PrimeFaces.calculateScrollbarWidth();
        }

        return this.scrollbarWidth;
    }

    /**
     * Checks whether the body of this DataTable overflow vertically.
     * @return `true` if any content overflow vertically, a falsy value otherwise.
     */
    hasVerticalOverflow(): boolean | "" | undefined {
        return (this.cfg.scrollHeight && this.bodyTable!.outerHeight()! > this.scrollBody!.outerHeight()!);
    }

    /**
     * Reads the saved scroll state and applies it. This helps to preserve the current scrolling position during AJAX
     * updates.
     */
    restoreScrollState(): void {
        var scrollState = this.scrollStateHolder!.val() as string,
        scrollValues: (string | number)[] = scrollState.split(',');

        if (scrollValues[0] == '-1') {
            scrollValues[0] = this.scrollBody![0]!.scrollWidth;
        }

        // jQuery accepts the string values, too
        this.scrollBody!.scrollLeft(scrollValues[0] as number);
        this.scrollBody!.scrollTop(scrollValues[1] as number);
    }

    /**
     * Saves the current scrolling position. This helps to preserve the current scrolling position during AJAX updates.
     */
    saveScrollState(): void {
        var scrollState = this.scrollBody!.scrollLeft() + ',' + this.scrollBody!.scrollTop();

        this.scrollStateHolder!.val(scrollState);
    }

    /**
     * Clears the saved scrolling position.
     */
    clearScrollState(): void {
        this.scrollStateHolder!.val('0,0');
    }

    /**
     * Adjusts the width of a column in a table to fit the widest cell content.
     *
     * This function calculates the maximum width needed for a column in a table
     * to accommodate the widest cell content. It creates a temporary span element
     * to measure the width of cell contents and adjusts the column header width accordingly.
     *
     * @param cell - A jQuery object representing a cell in the column to be resized.
     */
    autosizeColumnWidth(cell: JQuery): void {
        // Create a temporary span element
        var $span = $("<span></span>")
            .css({
                visibility: "hidden",
                position: "absolute",
                whiteSpace: "nowrap",
            })
            .appendTo(document.body);

        // Function to set the span's styles based on a cell's styles
        var setSpanStyles = function($element: JQuery): void {
            $span.text($element.text()).css({
                font: $element.css("font"),
                fontSize: $element.css("fontSize"),
                fontWeight: $element.css("fontWeight"),
                fontFamily: $element.css("fontFamily"),
            });
        };

        // Get the index of the cell's column within its row
        var columnIndex = cell.index();

        // Select all the cells in the same column of other rows
        var $whichBody = this.cfg.scrollable ? this.scrollTbody! : this.tbody;
        var cellsInSameColumn = $whichBody.find("> tr:not(.p-expanded-row-content) > *:nth-child(" + (columnIndex + 1) + ")");

        // Find the max width of the largest column
        var maxWidth = 0;
        cellsInSameColumn.each((_i, el) => {
            var $td = $(el);
            setSpanStyles($td);
            var cellWidth = $span.outerWidth(true)!;
            maxWidth = Math.max(maxWidth, cellWidth);
        });

        // Find the header for the column
        var $header = this.thead.find(".p-resizable-column").eq(columnIndex);
        setSpanStyles($header);

        // get header width and add 20px to account for possible sort icon
        var headerWidth = $span.outerWidth(true)! + 20;
        maxWidth = Math.max(maxWidth, headerWidth);

        // Remove the span from the document body
        $span.remove();

        // set the TH header to the new width
        $header.css("width", maxWidth);
        // set the cloned scroll header to the same width
        if (this.cfg.scrollable) {
            this.theadClone!.find(".p-resizable-column").eq(columnIndex).css("width", maxWidth);
        }

        // fire the AJAX event if necessary
        this.fireColumnResizeEvent($header);
    }

    /**
     * Adjusts the width of the given columns to fit the current settings.
     */
    fixColumnWidths(): void {
        if(!this.columnWidthsFixed) {
            if(this.cfg.scrollable) {
                this.scrollHeader!.find('> .p-datatable-scrollable-header-box > table > thead > tr > th').each((_i, el) => {
                    var headerCol = $(el),
                    colIndex = headerCol.index(),
                    widthInfo = this.getColumnWidthInfo(headerCol, true);

                    this.applyWidthInfo(headerCol, widthInfo);

                    if(this.footerCols!.length > 0) {
                        var footerCol = this.footerCols!.eq(colIndex);
                        this.applyWidthInfo(footerCol, widthInfo);
                    }
                });
            }
            else {
                var columns = this.jq.find('> .p-datatable-tablewrapper > table > thead > tr > th'),
                    visibleColumns = columns.filter(':visible'),
                    hiddenColumns = columns.filter(':hidden');

                this.setColumnsWidth(visibleColumns);
                /* IE fixes */
                this.setColumnsWidth(hiddenColumns);
            }

            this.columnWidthsFixed = true;
        }
    }

    /**
     * Applies the appropriated width to all given column elements.
     * @param columns A list of column elements.
     */
    setColumnsWidth(columns: JQuery): void {
        if(columns.length) {
            columns.each((_i, el) => {
                var col = $(el),
                widthInfo = this.getColumnWidthInfo(col, true);

                this.applyWidthInfo(col, widthInfo);
            });
        }
    }

    /**
     * Use only when live scrolling is enabled: Loads the next set of rows on-the-fly.
     */
    loadLiveRows(): void {
        if(this.liveScrollActive||(this.scrollOffset! + this.cfg.scrollStep! > this.cfg.scrollLimit!)) {
            return;
        }

        this.liveScrollActive = true;
        this.scrollOffset! += this.cfg.scrollStep!;

        //Disable scroll if there is no more data left
        if(this.scrollOffset === this.cfg.scrollLimit) {
            this.shouldLiveScroll = false;
        }

        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            formId: this.getParentFormId(),
            params: [{name: this.id + '_scrolling', value: true},
                            {name: this.id + '_first', value: 1},
                            {name: this.id + '_skipChildren', value: true},
                            {name: this.id + '_scrollOffset', value: this.scrollOffset},
                            {name: this.id + '_encodeFeature', value: true}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        //insert new rows
                        this.updateData(content, false);

                        this.liveScrollActive = false;
                    }
                });

                return true;
            },
            oncomplete: (xhr, status, args, data) => {
                if(typeof args!['totalRecords'] !== 'undefined') {
                    this.cfg.scrollLimit = args!['totalRecords'] as number;
                }

                this.loadingLiveScroll = false;
                this.allLoadedLiveScroll = (this.scrollOffset! + this.cfg.scrollStep!) >= this.cfg.scrollLimit!;

                // reset index of shift selection on multiple mode
                this.originRowMeta = null;
            }
        };

        if (this.hasBehavior('liveScroll')) {
            this.callBehavior('liveScroll', options);
        } else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * When live scrolling is enabled: Loads the next set of rows via AJAX.
     * @param page 0-based index of the page to load.
     * @param callback Callback that is invoked after the rows have been loaded and inserted into the DOM.
     */
    loadRowsWithVirtualScroll(page: number, callback: () => void): void {
        if(this.virtualScrollActive) {
            return;
        }

        this.virtualScrollActive = true;

        var first = (page - 1) * this.cfg.scrollStep!,
        options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            formId: this.getParentFormId(),
            params: [{name: this.id + '_scrolling', value: true},
                            {name: this.id + '_skipChildren', value: true},
                            {name: this.id + '_first', value: first},
                            {name: this.id + '_encodeFeature', value: true}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        //insert new rows
                        this.updateData(content);
                        callback();
                        this.virtualScrollActive = false;
                    }
                });

                return true;
            },
            oncomplete: (xhr, status, args, data) => {
                if(typeof args!['totalRecords'] !== 'undefined') {
                    this.cfg.scrollLimit = args!['totalRecords'] as number;
                }

                // reset index of shift selection on multiple mode
                this.originRowMeta = null;
            }
        };
        if (this.hasBehavior('virtualScroll')) {
            this.callBehavior('virtualScroll', options);
        } else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Switches to the given page by loading the content via AJAX. Compare with `loadDataWithCache`, which first checks
     * whether the data is already cached and loads it from the server only when not found in the cache.
     * @param newState The new values for the current page and the rows per page count.
     */
    paginate(newState: PrimeFacesTypes.widget.Paginator.PaginationState): void {
        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            formId: this.getParentFormId(),
            params: [{name: this.id + '_pagination', value: true},
                    {name: this.id + '_first', value: newState.first},
                    {name: this.id + '_rows', value: newState.rows},
                    {name: this.id + '_skipChildren', value: true},
                    {name: this.id + '_encodeFeature', value: true}]
        };

        if (!this.cfg.partialUpdate) {
            options.params!.push({name: this.id + '_fullUpdate', value: true});

            options.onsuccess = (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            this.jq.replaceWith(content);
                        }
                    });

                return true;
            };
        }
        else {
            options.onsuccess = (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            this.updateData(content);

                            if(this.checkAllToggler) {
                                this.updateHeaderCheckbox();
                            }

                            if(this.cfg.scrollable) {
                                this.alignScrollBody();
                            }

                            if(this.cfg.clientCache) {
                                this.cacheMap![newState.first] = content;
                            }
                        }
                    });

                return true;
            };

            options.oncomplete = (xhr, status, args, data) => {
                this.paginator!.cfg.page = newState.page;
                if(args && typeof args['totalRecords'] !== 'undefined') {
                    this.paginator!.updateTotalRecords(args['totalRecords'] as number);
                }
                else {
                    this.paginator!.updateUI();
                }
                this.updateColumnsView();
                // reset index of shift selection on multiple mode
                this.originRowMeta = null;
            };
        }

        if(this.hasBehavior('page')) {
            this.callBehavior('page', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Loads next page asynchronously to keep it at viewstate and Updates viewstate
     * @param newState The new values for the current page and the rows per page count.
     */
    fetchNextPage(newState: PrimeFacesTypes.widget.Paginator.PaginationState): void {
        var rows = newState.rows,
        first = newState.first,
        options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            global: false,
            params: [{name: this.id + '_skipChildren', value: true},
                    {name: this.id + '_encodeFeature', value: true},
                    {name: this.id + '_first', value: first},
                    {name: this.id + '_rows', value: rows},
                    {name: this.id + '_pagination', value: true},
                    {name: this.id + '_clientCache', value: true}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        if(content.length) {
                            var nextFirstValue = first + rows;
                            this.cacheMap![nextFirstValue] = content;
                        }
                    }
                });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Updates and syncs the current pagination state with the server.
     * @param newState The new values for the current page and the rows per page count.
     */
    updatePageState(newState: PrimeFacesTypes.widget.Paginator.PaginationState): void {
        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            global: false,
            params: [{name: this.id + '_pagination', value: true},
                    {name: this.id + '_encodeFeature', value: true},
                    {name: this.id + '_pageState', value: true},
                    {name: this.id + '_first', value: newState.first},
                    {name: this.id + '_rows', value: newState.rows}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        // do nothing
                    }
                });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Performs a sorting operation on the rows of this DataTable via AJAX
     * @param columnHeader Header of the column by which to sort.
     * @param order `-1` to sort column values in ascending order, `+1` to sort column values in descending order, or
     * `0` to remove the sorting order and display rows in their original order.
     * @param multi `true` if sorting by multiple columns is enabled, or `false` otherwise.
     */
    sort(columnHeader: JQuery, order: -1 | 0 | 1, multi?: boolean): void {
        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            formId: this.getParentFormId(),
            params: [{name: this.id + '_sorting', value: true},
                     {name: this.id + '_skipChildren', value: true},
                     {name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_sortKey', value: this.joinSortMetaOption('col')},
                     {name: this.id + '_sortDir', value: this.joinSortMetaOption('order')}]
        };

        if (!this.cfg.partialUpdate) {
            options.params!.push({name: this.id + '_fullUpdate', value: true});

            options.onsuccess = (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            this.jq.replaceWith(content);
                        }
                    });

                return true;
            };
        }
        else {
            options.onsuccess = (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            this.updateData(content);

                            if(this.checkAllToggler) {
                              this.updateHeaderCheckbox();
                            }
                        }
                    });

                return true;
            };

            options.oncomplete = (xhr, status, args, data) => {
                var paginator = this.getPaginator();
                if(args) {
                    if(args['totalRecords']) {
                        this.cfg.scrollLimit = args['totalRecords'] as number;

                        if(paginator && paginator.cfg.rowCount !== args['totalRecords']) {
                            paginator.setTotalRecords(args['totalRecords'] as number);
                        }
                    }

                    if(!args['validationFailed']) {
                        if(paginator) {
                            paginator.setPage(0, true);
                        }

                        // remove aria-sort
                        var activeColumns = this.sortableColumns!.filter('.p-state-active');
                        if(activeColumns.length) {
                            activeColumns.removeAttr('aria-sort');
                        }
                        else {
                            this.sortableColumns!.eq(0).removeAttr('aria-sort');
                        }

                        if(!multi) {
                            //aria reset
                            for(var i = 0; i < activeColumns.length; i++) {
                                var activeColumn = $(activeColumns.get(i)!),
                                    ariaLabelOfActive = activeColumn.attr('aria-label');

                                activeColumn.attr('aria-label', this.getSortMessage(ariaLabelOfActive, this.ascMessage));
                                $(PrimeFaces.escapeClientId(activeColumn.attr('id') + '_clone')).removeAttr('aria-sort').attr('aria-label', this.getSortMessage(ariaLabelOfActive, this.ascMessage));
                            }

                            activeColumns.data('sortorder', DataTable.SORT_ORDER.UNSORTED).removeClass('p-state-active')
                                        .find('.p-sortable-column-icon').removeClass('p-datatable-sort-asc-icon p-datatable-sort-desc-icon');
                        }

                        columnHeader.data('sortorder', order).addClass('p-state-active');
                        var sortIcon = columnHeader.find('.p-sortable-column-icon'),
                        ariaLabel = columnHeader.attr('aria-label');

                        if (order === DataTable.SORT_ORDER.DESCENDING) {
                            sortIcon.removeClass('p-datatable-sort-asc-icon').addClass('p-datatable-sort-desc-icon');
                            columnHeader.attr('aria-sort', 'descending').attr('aria-label', this.getSortMessage(ariaLabel, this.otherMessage));
                            $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).attr('aria-sort', 'descending')
                                .attr('aria-label', this.getSortMessage(ariaLabel, this.otherMessage));
                        } else if (order === DataTable.SORT_ORDER.ASCENDING) {
                            sortIcon.removeClass('p-datatable-sort-desc-icon').addClass('p-datatable-sort-asc-icon');
                            columnHeader.attr('aria-sort', 'ascending').attr('aria-label', this.getSortMessage(ariaLabel, this.descMessage));
                            $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).attr('aria-sort', 'ascending')
                                .attr('aria-label', this.getSortMessage(ariaLabel, this.descMessage));
                        } else {
                            sortIcon.removeClass('p-datatable-sort-asc-icon p-datatable-sort-desc-icon');
                            columnHeader.removeClass('p-state-active').attr('aria-sort', 'other')
                                .attr('aria-label', this.getSortMessage(ariaLabel, this.ascMessage));
                            $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).attr('aria-sort', 'other')
                                .attr('aria-label', this.getSortMessage(ariaLabel, this.ascMessage));
                        }

                        this.updateSortPriorityIndicators();
                    }
                }

                if(this.cfg.virtualScroll) {
                    this.resetVirtualScrollBody();
                }
                else if(this.cfg.liveScroll) {
                    this.scrollOffset = 0;
                    this.liveScrollActive = false;
                    this.shouldLiveScroll = true;
                    this.loadingLiveScroll = false;
                    this.allLoadedLiveScroll = this.cfg.scrollStep! >= this.cfg.scrollLimit!;
                }

                if(this.cfg.clientCache) {
                    this.clearCacheMap();
                }

                this.updateColumnsView();

                // reset index of shift selection on multiple mode
                this.originRowMeta = null;
            }
        }

        if (this.hasBehavior('sort')) {
            this.callBehavior('sort', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * In multi-sort mode this will add number indicators to let the user know the current
     * sort order. If only one column is sorted then no indicator is displayed and will
     * only be displayed once more than one column is sorted.
     */
    updateSortPriorityIndicators(): void {
        // remove all indicator numbers first
        this.sortableColumns!.find('.p-sortable-column-badge').text('').addClass('p-hidden');

        // add 1,2,3 etc to columns if more than 1 column is sorted
        const sortMeta =  this.sortMeta;
        if (sortMeta && sortMeta.length > 1) {
            this.sortableColumns!.each((_i, el) => {
                var id = $(el).attr("id");
                for (var i = 0; i < sortMeta.length; i++) {
                    if (sortMeta[i]!.col == id) {
                        $(el).find('.p-sortable-column-badge').text(i + 1).removeClass('p-hidden');
                    }
                }
            });
        }
    }

    /**
     * Serializes the option from the sort meta items.
     * @param option Property of the sort meta to use.
     * @return All values from the current sort meta list for the given option.
     */
    joinSortMetaOption(option: keyof PrimeFacesTypes.widget.DataTable.SortMeta): string {
        var value = '';

        for(var i = 0; i < this.sortMeta!.length; i++) {
            value += this.sortMeta![i]![option];

            if(i !== (this.sortMeta!.length - 1)) {
                value += ',';
            }
        }

        return value;
    }

    /**
     * Filters this DataTable. Uses the current values of the filter inputs. This will result in an AJAX request being
     * sent.
     */
    filter(): void {
        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            formId: this.getParentFormId(),
            params: [{name: this.id + '_filtering', value: true},
                     {name: this.id + '_encodeFeature', value: true}]
        };


        if (!this.cfg.partialUpdate){
            options.params!.push({name: this.id + '_fullUpdate', value: true});

            options.onsuccess = (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            this.jq.replaceWith(content);
                        }
                    });

                return true;
            };
        }
        else {
            options.onsuccess = (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
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

            options.oncomplete = (xhr, status, args, data) => {
                var paginator = this.getPaginator();
                if(args && typeof args['totalRecords'] !== 'undefined') {
                    this.cfg.scrollLimit = args['totalRecords'] as number;

                    if(paginator) {
                        paginator.setTotalRecords(args['totalRecords'] as number);
                    }
                }

                if(this.cfg.clientCache) {
                    this.clearCacheMap();
                }

                if(this.cfg.virtualScroll) {
                    var row = this.bodyTable!.children('tbody').children('tr.p-widget-content');
                    if(row) {
                        var hasEmptyMessage = row.eq(0).hasClass('p-datatable-empty-message'),
                        scrollLimit = this.cfg.scrollLimit;

                        if(hasEmptyMessage) {
                            scrollLimit = 1;
                        }

                        this.resetVirtualScrollBody();

                        this.rowHeight = row.outerHeight();
                        this.scrollBody!.children('div').css({'height': parseFloat((scrollLimit! * this.rowHeight! + 1) + 'px')});

                        if(hasEmptyMessage && this.cfg.scrollHeight && this.percentageScrollHeight) {
                            PrimeFaces.queueTask(() => {
                                this.adjustScrollHeight();
                            });
                        }
                    }
                }
                else if(this.cfg.liveScroll) {
                    this.scrollOffset = 0;
                    this.liveScrollActive = false;
                    this.shouldLiveScroll = true;
                    this.loadingLiveScroll = false;
                    this.allLoadedLiveScroll = this.cfg.scrollStep! >= this.cfg.scrollLimit!;
                }

                this.updateColumnsView();
                this.updateEmptyColspan();

                // reset index of shift selection on multiple mode
                this.originRowMeta = null;
            }
        }

        if(this.hasBehavior('filter')) {
            this.callBehavior('filter', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Callback for a click event on a row.
     * @param event Click event that occurred.
     * @param rowElement Row that was clicked
     * @param silent `true` to prevent behaviors from being invoked, `false` otherwise.
     */
    onRowClick(event: JQuery.TriggeredEvent, rowElement: HTMLElement, silent?: boolean): void {
        // Check if row click triggered this event not a clickable element in row content
        if($(event.target).is(this.rowSelectorForRowClick!)) {
            var row = $(rowElement),
            selected = row.hasClass('p-state-highlight'),
            metaKey = event.metaKey||event.ctrlKey||PrimeFaces.env.isTouchable(this.cfg),
            shiftKey = event.shiftKey;

            this.assignFocusedRow(row);

            // Unselect a selected row if meta key is on
            if(selected && metaKey) {
                this.unselectRow(row, silent);
            }
            else {
                //unselect previous selection if this is single selection or multiple one with no keys
                if(this.isSingleSelection() || (this.isMultipleSelection() && event && !metaKey && !shiftKey && this.cfg.selectionRowMode === 'new' )) {
                    this.unselectAllRows();
                }

                //range selection with shift key
                if(this.isMultipleSelection() && event && event.shiftKey && this.originRowMeta !== null) {
                    this.selectRowsInRange(row, false);
                }
                else if(this.cfg.selectionRowMode === 'add' && selected) {
                    this.unselectRow(row, silent);
                }
                //select current row
                else {
                    this.originRowMeta = this.getRowMeta(row);
                    this.cursorRowMeta = null;
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
    }

    /**
     * Callback for a double click event on a row.
     * @param event Event that occurred.
     * @param row Row that was clicked.
     */
    onRowDblclick(event: JQuery.TriggeredEvent, row: JQuery): void {
        if(this.cfg.disabledTextSelection) {
            PrimeFaces.clearSelection();
        }

        //Check if row click triggered this event not a clickable element in row content
        if($(event.target).is(this.rowSelectorForRowClick!)) {
            var rowMeta = this.getRowMeta(row);

            this.fireRowSelectEvent(rowMeta.key, 'rowDblselect');
        }
    }

    /**
     * Callback for a right click event on a row. May bring up the context menu
     * @param event Event that occurred.
     * @param rowElement Row that was clicked.
     * @param cmSelMode The current selection mode.
     * @param fnShowMenu Optional callback function invoked when the menu was opened.
     * @return true to hide the native browser context menu, false to display it
     */
    onRowRightClick(event: JQuery.TriggeredEvent, rowElement: HTMLElement | JQuery, cmSelMode: PrimeFacesTypes.widget.DataTable.CmSelectionMode, fnShowMenu?: () => void): boolean {
        var row = $(rowElement),
        rowMeta = this.getRowMeta(row),
        selected = row.hasClass('p-state-highlight');

        this.assignFocusedRow(row);

        if(cmSelMode === 'single' || !selected) {
            this.unselectAllRows();
        }

        this.selectRow(row, true);

        this.fireRowSelectEvent(rowMeta.key, 'contextMenu', fnShowMenu);

        if(this.cfg.disabledTextSelection) {
            PrimeFaces.clearSelection();
        }

        return this.hasBehavior('contextMenu');
    }

    /**
     * Converts a row specifier to the row element. The row specifier is either a row index or the row element itself.
     *
     * __In case this DataTable has got expandable rows, please not that a new table row is created for each expanded row.__
     * This may result in the given index not pointing to the intended row.
     * @param r The row to convert.
     * @return The row, or an empty JQuery instance of no row was found.
     */
    findRow(r: PrimeFacesTypes.widget.DataTable.RowSpecifier): JQuery {
        var row = r;

        if(PrimeFaces.isNumber(r)) {
            row = this.tbody.children('tr:eq(' + r + ')');
        }

        return row as JQuery;
    }

    /**
     * Select the rows between the cursor and the given row.
     * @param row A row of this DataTable.
     * @param silent `true` to prevent behaviors and event listeners from being invoked, or `false` otherwise.
     */
    selectRowsInRange(row: JQuery, silent?: boolean): void {
        var rows = this.tbody.children('.p-datatable-selectable'),
            rowMeta = this.getRowMeta(row),
            offset = (this.cfg.paginator && this.cfg.paginator.page! >= 0) ? this.cfg.paginator.rows! * this.cfg.paginator.page! : 0,
            newCursorIndex = offset > this.originRowMeta!.index ? this.originRowMeta!.index : this.originRowMeta!.index - offset,
            currentIndex = offset > rowMeta.index ? rowMeta.index : rowMeta.index - offset;

        // Unselect previously selected rows with shift
        if (this.cursorRowMeta !== null) {
            var oldCursorIndex = this.cursorRowMeta!.index,
                rowsToUnselect = rows.slice(Math.min(oldCursorIndex, newCursorIndex), Math.max(oldCursorIndex, newCursorIndex) + 1);

            rowsToUnselect.each((i, item) => {
                this.unselectRow($(item), true);
            });
        }

        // Select rows between cursor and origin
        this.cursorRowMeta = rowMeta;
        var rowsToSelect = rows.slice(Math.min(currentIndex, newCursorIndex), Math.max(currentIndex, newCursorIndex) + 1);

        rowsToSelect.each((i, item) => {
            this.selectRow($(item), true);
        });

        if (!silent) {
            this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
        }
    }

    /**
     * Selects the given row, according to the current selection mode.
     * @param r A row of this DataTable to select.
     * @param silent `true` to prevent behaviors and event listeners from being invoked, or `false` otherwise.
     */
    selectRow(r: PrimeFacesTypes.widget.DataTable.RowSpecifier, silent?: boolean): void {
        var row = this.findRow(r);
        if(!row.hasClass('p-datatable-selectable')) {
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
                row.children('td.p-selection-column').find(':checkbox').prop('checked', true);
            else
                this.selectCheckbox(row.children('td.p-selection-column').find('> div.p-chkbox > div.p-chkbox-box'));

            this.updateHeaderCheckbox();
        }

        if(this.isRadioSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.p-selection-column').find(':radio').prop('checked', true);
            else
                this.selectRadio(row.children('td.p-selection-column').find('> div.p-radiobutton > div.p-radiobutton-box'));
        }

        this.addSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
        }
    }

    /**
     * Unselects the given row.
     * @param r A row of this DataTable to unselect.
     * @param silent `true` to prevent behaviors and event listeners from being invoked, or `false` otherwise.
     */
    unselectRow(r: PrimeFacesTypes.widget.DataTable.RowSpecifier, silent?: boolean): void {
        var row = this.findRow(r);
        if(!row.hasClass('p-datatable-selectable')) {
            return;
        }

        var rowMeta = this.getRowMeta(row);

        this.unhighlightRow(row);

        if(this.isCheckboxSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.p-selection-column').find(':checkbox').prop('checked', false);
            else
                this.unselectCheckbox(row.children('td.p-selection-column').find('> div.p-chkbox > div.p-chkbox-box'));

            this.updateHeaderCheckbox();
        }

        if(this.isRadioSelectionEnabled()) {
            if(this.cfg.nativeElements)
                row.children('td.p-selection-column').find(':radio').prop('checked', false);
            else
                this.unselectRadio(row.children('td.p-selection-column').find('> div.p-radiobutton > div.p-radiobutton-box'));
        }

        this.removeSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselect");
        }
    }

    /**
     * Configures the ARIA label for the row checkbox/radio button.
     * @param row the row key to identify
     */
    updateSelectionAria(row: JQuery): void {
        if (row) {
            var jq = row.children('td.p-selection-column').find(":radio,:checkbox,div.p-chkbox-box");
            // Only update aria-label if one doesn't already exist
            if (jq && !jq.attr('aria-label')) {
                var rowMeta = this.getRowMeta(row);
                var checked = row.attr('aria-selected') === "true"
                var ariaLabel = checked ? this.getAriaLabel('unselectLabel') : this.getAriaLabel('selectLabel');
                ariaLabel += " " + rowMeta.key;
                jq.attr('aria-label', ariaLabel);
            }
        }
    }

    /**
     * Highlights row to mark it as selected.
     * @param row Row to highlight.
     */
    highlightRow(row: JQuery): void {
        row.addClass('p-state-highlight').attr('aria-selected', 'true');
        this.updateSelectionAria(row)
    }

    /**
     * Removes the highlight of a row so it is no longer marked as selected.
     * @param row Row to unhighlight.
     */
    unhighlightRow(row: JQuery): void {
        row.removeClass('p-state-highlight').attr('aria-selected', 'false');
        this.updateSelectionAria(row)
    }

    /**
     * Sends a row select event on server side to invoke a row select listener if defined.
     * @param rowKey The key of the row that was selected.
     * @param behaviorEvent Name of the event to fire.
     * @param fnShowMenu Optional callback function invoked when the menu was opened.
     */
    fireRowSelectEvent(rowKey: string | undefined, behaviorEvent: string, fnShowMenu?: () => void): void {
        if(this.hasBehavior(behaviorEvent)) {
            var ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                    params: [{name: this.id + '_instantSelectedRowKey', value: rowKey}
                ],
                oncomplete: function() {
                    if(typeof fnShowMenu === "function") {
                        fnShowMenu();
                    }
                }
            };

            this.callBehavior(behaviorEvent, ext);
        }
        else {
            if(typeof fnShowMenu === "function") {
                fnShowMenu();
            }
        }
    }

    /**
     * Sends a row unselect event on server side to invoke a row unselect listener if defined
     * @param rowKey The key of the row that was deselected.
     * @param behaviorEvent Name of the event to fire.
     */
    fireRowUnselectEvent(rowKey: string | undefined, behaviorEvent: string): void {
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
    }

    /**
     * Selects the corresponding row of a radio based column selection
     * @param radio A radio INPUT element
     */
    selectRowWithRadio(radio: JQuery): void {
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
    }

    /**
     * Selects the corresponding row of a checkbox based column selection
     * @param checkbox A checkox INPUT element
     * @param event Event that occurred.
     * @param silent `true` to prevent behaviors from being invoked, `false` otherwise.
     */
    selectRowWithCheckbox(checkbox: JQuery, event: JQuery.TriggeredEvent | null, silent?: boolean): void {
        var row = checkbox.closest('tr');
        if (!row.hasClass('p-datatable-selectable')) {
            return;
        }

        var rangeSelected = false;
        var rowMeta = this.getRowMeta(row);

        if (this.getSelectedRowsCount() > 0 && this.originRowMeta) {
            this.cursorRowMeta = rowMeta;

            if (event && event.shiftKey) {
                this.selectRowsInRange(row, true);
                rangeSelected = true;
            }
        }
        else {
            this.originRowMeta = rowMeta;
            this.cursorRowMeta = null;
        }

        if (!rangeSelected) {
            this.addSelection(rowMeta.key);
            this.highlightRow(row);
            if (!this.cfg.nativeElements) {
                this.selectCheckbox(checkbox);
            }
        }
        this.writeSelections();

        if (!silent) {
            this.updateHeaderCheckbox();
            this.fireRowSelectEvent(rowMeta.key, "rowSelectCheckbox");
        }
    }

    /**
     * Unselects the corresponding row of a checkbox based column selection
     * @param checkbox A checkox INPUT element
     * @param event Event that occurred.
     * @param silent `true` to prevent behaviors from being invoked, `false` otherwise.
     */
    unselectRowWithCheckbox(checkbox: JQuery, event: JQuery.TriggeredEvent | null, silent?: boolean): void {
        var row = checkbox.closest('tr');
        if(!row.hasClass('p-datatable-selectable')) {
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

        this.originRowMeta = null;

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselectCheckbox");
        }
    }

    /**
     * Unselects all rows of this DataTable so that no rows are selected. This includes all rows on all pages,
     * irrespective of whether they are on the currently shown page.
     */
    unselectAllRows(): void {
        var selectedRows = this.jq.find('tr.p-state-highlight'),
        checkboxSelectionEnabled = this.isCheckboxSelectionEnabled(),
        radioSelectionEnabled = this.isRadioSelectionEnabled();

        for(var i = 0; i < selectedRows.length; i++) {
            var row = selectedRows.eq(i);
            if(!row.hasClass('p-datatable-selectable')) {
                continue;
            }

            this.unhighlightRow(row);

            if(checkboxSelectionEnabled) {
                if(this.cfg.nativeElements)
                    row.children('td.p-selection-column').find(':checkbox').prop('checked', false);
                else
                    this.unselectCheckbox(row.children('td.p-selection-column').find('> div.p-chkbox > div.p-chkbox-box'));
            }
            else if(radioSelectionEnabled) {
                if(this.cfg.nativeElements)
                    row.children('td.p-selection-column').find(':radio').prop('checked', false);
                else
                    this.unselectRadio(row.children('td.p-selection-column').find('> div.p-radiobutton > div.p-radiobutton-box'));
            }
        }

        if(checkboxSelectionEnabled) {
            this.uncheckHeaderCheckbox();
        }

        this.selection = [];
        this.writeSelections();
    }

    /**
     * Select all rows on the currently shown page. Compare with `selectAllRows`.
     */
    selectAllRowsOnPage(): void {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            this.selectRow(row, true);
        }
    }

    /**
     * Unselect all rows on the currently shown page. Compare with `unselectAllRows`.
     */
    unselectAllRowsOnPage(): void {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            this.unselectRow(row, true);
        }
    }

    /**
     * Selects all rows of this DataTable so that no rows are selected. This includes all rows on all pages,
     * irrespective of whether they are on the currently shown page.
     */
    selectAllRows(): void {
        this.selectAllRowsOnPage();
        this.selection = new Array('@all');
        this.writeSelections();
    }

    /**
     * Configures the ARIA label for the select all checkbox.
     */
    configureSelectAllAria(): void {
        if (this.checkAllToggler) {
           var checked = this.checkAllToggler.attr('aria-checked') === "true" || this.checkAllToggler.prop('checked');
           var ariaLabel = checked ? this.getAriaLabel('selectAll') : this.getAriaLabel('unselectAll');
           this.checkAllToggler.attr('aria-label', ariaLabel);
        }
    }

    /**
     * Toggles the `selected all` checkbox in the header of this DataTable. When no rows are selected, this will select
     * all rows. When some rows are selected, this will unselect all rows.
     */
    toggleCheckAll(): void {
        var shouldCheckAll = true;
        if(this.cfg.nativeElements) {
            var checkboxes = this.jq.find('tr.p-datatable-selectable > td.p-selection-column > :checkbox:visible'),
            checked = this.checkAllToggler!.prop('checked') as boolean;

            checkboxes.each((_i, el) => {
                if(checked) {
                    var checkbox = $(el);
                    checkbox.prop('checked', true);
                    this.selectRowWithCheckbox(checkbox, null, true);
                }
                else {
                    var checkbox = $(el);
                    checkbox.prop('checked', false);
                    this.unselectRowWithCheckbox(checkbox, null, true);
                    shouldCheckAll = false;
                }
            });
        }
        else {
            var checkboxes = this.jq.find('tr.p-datatable-selectable > td.p-selection-column > div.p-chkbox > div.p-chkbox-box:visible'),
            checked = this.checkAllToggler!.attr('aria-checked') === "true";

            if(checked) {
                this.checkAllToggler!.removeClass('p-state-active').children('span.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
                this.checkAllToggler!.attr('aria-checked', 'false');
                shouldCheckAll = false;

                checkboxes.each((_i, el) => {
                    this.unselectRowWithCheckbox($(el), null, true);
                });
            }
            else {
                this.checkAllToggler!.addClass('p-state-active').children('span.p-chkbox-icon').addClass('p-checkbox-checked-icon');
                this.checkAllToggler!.attr('aria-checked', 'true');

                checkboxes.each((_i, el) => {
                    this.selectRowWithCheckbox($(el), null, true);
                });
            }
        }

        this.configureSelectAllAria();

        // GitHub #6730 user wants all rows not just displayed rows
        if(!this.cfg.selectionPageOnly) {
            if (shouldCheckAll) {
                this.selectAllRows();
            }
            else {
                this.unselectAllRows();
            }
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
    }

    /**
     * Selects the given checkbox from a row.
     * @param checkbox A checkbox to select.
     */
    selectCheckbox(checkbox: JQuery): void {
        checkbox.addClass('p-state-active');

        if (this.cfg.nativeElements) {
            checkbox.prop('checked', true);
        }
        else {
            checkbox.children('span.p-chkbox-icon:first').addClass('p-checkbox-checked-icon');
            checkbox.attr('aria-checked', 'true');
        }
    }

    /**
     * Unselects the given checkbox from a row.
     * @param checkbox A checkbox to unselect.
     */
    unselectCheckbox(checkbox: JQuery): void {
        checkbox.removeClass('p-state-active');

        if (this.cfg.nativeElements) {
            checkbox.prop('checked', false);
        }
        else {
            checkbox.children('span.p-chkbox-icon:first').removeClass('p-checkbox-checked-icon');
            checkbox.attr('aria-checked', 'false');
        }
    }

    /**
     * Selects the given radio button from a row.
     * @param radio A radio button to select.
     */
    selectRadio(radio: JQuery): void {
        radio.addClass('p-state-active');
        radio.prev().children('input').prop('checked', true);
    }

    /**
     * Unselects the given radio button from a row.
     * @param radio A radio button to unselect.
     */
    unselectRadio(radio: JQuery): void {
        radio.removeClass('p-state-active');
        radio.prev().children('input').prop('checked', false);
    }

    /**
     * Expands a row to display its detailed content
     * @param toggler The row toggler of a row to expand.
     */
    toggleExpansion(toggler: JQuery): void {
        var row = toggler.closest('tr'),
        rowIndex = this.getRowMeta(row).index,
        iconOnly = toggler.hasClass('p-icon'),
        labels = toggler.children('span'),
        expanded = iconOnly ? toggler.attr('aria-expanded') === 'true' : toggler.children('span').eq(0).hasClass('p-hidden');

        //Run toggle expansion if row is not being toggled already to prevent conflicts
        if($.inArray(rowIndex, this.expansionProcess!) === -1) {
            this.expansionProcess!.push(rowIndex);

            if(expanded) {
                toggler.attr('aria-expanded', 'false');
                if(iconOnly) {
                    this.updateExpansionAria(toggler);
                }
                else {
                    labels.eq(0).removeClass('p-hidden');
                    labels.eq(1).addClass('p-hidden');
                }

                this.collapseRow(row);
                this.expansionProcess = $.grep(this.expansionProcess!, function(r) {
                    return (r !== rowIndex);
                });
                this.fireRowCollapseEvent(row);
            }
            else {
                if(this.cfg.rowExpandMode === 'single') {
                    this.collapseAllRows();
                }

                toggler.attr('aria-expanded', 'true');
                if(iconOnly) {
                    this.updateExpansionAria(toggler);
                }
                else {
                    labels.eq(0).addClass('p-hidden');
                    labels.eq(1).removeClass('p-hidden');
                }

                this.loadExpandedRowContent(row);
            }
        }
    }

    /**
     * Loads the detailed content for the given expandable row.
     * @param row A row with content to load.
     */
    loadExpandedRowContent(row: JQuery): void {
        // To check whether or not any hidden expansion content exists to avoid reloading multiple duplicate nodes in DOM
        var expansionContent = row.next('.p-expanded-row-content');
        if(expansionContent.length > 0) {
            expansionContent.remove();
        }

        var rowMeta = this.getRowMeta(row),
        options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            formId: this.getParentFormId(),
            params: [{name: this.id + '_rowExpansion', value: true},
                     {name: this.id + '_expandedRowIndex', value: rowMeta.index},
                     {name: this.id + '_expandedRowKey', value: rowMeta.key},
                     {name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_skipChildren', value: true}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            if(content && PrimeFaces.trim(content).length) {
                                row.addClass('p-expanded-row');
                                this.displayExpandedRow(row, content);
                            }
                        }
                    });

                return true;
            },
            oncomplete: () => {
                this.expansionProcess = $.grep(this.expansionProcess!, function(r) {
                    return r !== rowMeta.index;
                });
            }
        };

        if(!PrimeFaces.inArray(this.loadedExpansionRows!, rowMeta.key)) {
            this.loadedExpansionRows!.push(rowMeta.key!);
            this.writeRowExpansions();
        }

        if(this.hasBehavior('rowToggle')) {
            this.callBehavior('rowToggle', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Display the given HTML string in the specified row. Called mainly after an AJAX request.
     * @param row Row to display.
     * @param content HTML string of the content to add to the row
     */
    displayExpandedRow(row: JQuery, content: string): void {
        row.after(content);
        this.updateRowspan(row);
        this.updateColspan(row.next());
    }

    /**
     * Calls the behaviors and event listeners when a row is collapsed.
     * @param row A row of this DataTable.
     */
    fireRowCollapseEvent(row: JQuery): void {
        var rowMeta = this.getRowMeta(row);

        if(this.hasBehavior('rowToggle')) {
            var ext = {
                params: [
                    {name: this.id + '_collapsedRowIndex', value: rowMeta.index},
                    {name: this.id + '_collapsedRowKey', value: rowMeta.key},
                    {name: this.id + '_skipChildren', value: true}
                ]
            };
            this.callBehavior('rowToggle', ext);
        }
    }

    /**
     * Collapses the given row, if it is expandable. Use `findRow` to get a row by its index. Does not update the row
     * expansion toggler button.
     * @param row Row to collapse.
     */
    collapseRow(row: JQuery): void {
        // #942: need to use "hide" instead of "remove" to avoid incorrect form mapping when a row is collapsed
        row.removeClass('p-expanded-row').next('.p-expanded-row-content').hide();

        var rowMeta = this.getRowMeta(row);
        if(PrimeFaces.inArray(this.loadedExpansionRows!, rowMeta.key)) {
            this.loadedExpansionRows = this.loadedExpansionRows!.filter(function(value, index, arr){
                return value != rowMeta.key;
            });
            this.writeRowExpansions();
        }

        this.updateRowspan(row);
    }

    /**
     * Collapses all rows that are currently expanded.
     */
    collapseAllRows(): void {
        this.getExpandedRows().each((_i, el) => {
            var expandedRow = $(el);
            this.collapseRow(expandedRow);

            var columns = expandedRow.children('td');
            for(var i = 0; i < columns.length; i++) {
                var column = columns.eq(i),
                toggler = column.children('.p-row-toggler');

                if(toggler.length > 0) {
                    toggler.attr('aria-expanded', 'false');
                    if(!toggler.hasClass('p-icon')) {
                        var labels = toggler.children('span');
                        labels.eq(0).removeClass('p-hidden');
                        labels.eq(1).addClass('p-hidden');
                    }
                    this.updateExpansionAria(toggler);
                    break;
                }
            }
        });
    }

    /**
     * Finds the list of row that are currently expanded.
     * @return All rows (`TR`) that are currently expanded.
     */
    getExpandedRows(): JQuery {
        return this.tbody.children('.p-expanded-row');
    }

    /**
     * Disables all cell editors to prevent extra data on form posts.
     * @param element the row or cell to find inputs to enable for editing
     */
    disableCellEditors(element?: JQuery): void {
        if (element) {
            $(element).find(":input:enabled").attr('disabled', 'disabled');
        }
        else {
            this.tbody.find(".p-cell-editor-input :input:enabled").attr('disabled', 'disabled').attr("data-disabled-by-editor", "true");
            //#13159: re-enable for all rows that are rowEditing="true"
            this.enableCellEditors(this.tbody.find('.p-row-editing'));

        }
    }

    /**
     * Enables all cell editors that were previously disabled by the UI and not already disabled from user.
     * @param element the row or cell to find inputs to enable for editing
     */
    enableCellEditors(element: JQuery): void {
        if (element) {
            element.find(":input[data-disabled-by-editor='true']").removeAttr('disabled');
        }
    }

    /**
     * Binds editor events non-obtrusively.
     */
    bindEditEvents(): void {
        var namespace = '.datatable' + this.id;
        this.cfg.saveOnCellBlur = (this.cfg.saveOnCellBlur === false) ? false : true;

        // #3571 Set all fields to disabled by default
        this.disableCellEditors();

        if(this.cfg.editMode === 'row') {
            var rowEditorSelector = '> tr > td > div.p-row-editor > a';

            // add aria to buttons
            this.tbody.find('a.p-row-editor-pencil').attr('aria-label', this.getAriaLabel('editRow'));
            this.tbody.find('a.p-row-editor-check').attr('aria-label', this.getAriaLabel('saveEdit'));
            this.tbody.find('a.p-row-editor-close').attr('aria-label', this.getAriaLabel('cancelEdit'));

            this.tbody.off('click.datatable focus.datatable blur.datatable', rowEditorSelector)
                        .on('click.datatable', rowEditorSelector, null, (e) => {
                            var element = $(e.currentTarget),
                            row = element.closest('tr');

                            if(element.hasClass('p-row-editor-pencil')) {
                                this.switchToRowEdit(row);
                                element.hide().siblings().show();
                            }
                            else if(element.hasClass('p-row-editor-check')) {
                                this.saveRowEdit(row);
                            }
                            else if(element.hasClass('p-row-editor-close')) {
                                this.cancelRowEdit(row);
                            }

                            e.preventDefault();
                        })
                        .on('focus.datatable', rowEditorSelector, null, (e) => {
                            $(e.currentTarget).addClass('p-row-editor-outline');
                        })
                        .on('blur.datatable', rowEditorSelector, null, (e) => {
                            $(e.currentTarget).removeClass('p-row-editor-outline');
                        });

            // GitHub #433 Allow ENTER to submit ESC to cancel row editor
            $(document).off("keydown" + namespace, this.jqId + " tr.p-row-editing")
                        .on("keydown" + namespace, this.jqId + " tr.p-row-editing", (e) => {
                            switch (e.key) {
                                case 'Enter':
                                    // #7028/#13927 Do not proceed if target is a textarea, button, link, or TextEditor
                                    if(PrimeFaces.utils.isEnterKeyBlocked(e)) {
                                         return true;
                                    }
                                    $(e.currentTarget).closest("tr").find(".p-row-editor-check").trigger("click");
                                    return false; // prevents executing other event handlers (adding new row to the table)
                                case 'Escape':
                                    $(e.currentTarget).closest("tr").find(".p-row-editor-close").trigger("click");
                                    return false;
                                default:
                                    break;
                }
                return undefined;
            });

        }
        else if(this.cfg.editMode === 'cell') {
            var originalCellSelector = '> tr > .p-editable-column',
            cellSelector = this.cfg.cellSeparator || originalCellSelector,
            editEvent = (this.cfg.editInitEvent !== 'click') ? this.cfg.editInitEvent + '.datatable-cell click.datatable-cell' : 'click.datatable-cell';

            this.tbody.off(editEvent, cellSelector)
                        .on(editEvent, cellSelector, null, (e) => {
                            var item = $(e.currentTarget),
                            cell = item.hasClass('p-editable-column') ? item : item.closest('.p-editable-column');

                            if(!cell.hasClass('p-cell-editing') && e.type === this.cfg.editInitEvent) {
                                this.showCellEditor(cell);
                            }
                        });

            // save/cancel on mouseup to queue the event request before whatever was clicked reacts
            $(document).off('mouseup.datatable-cell-blur' + this.id)
                        .on('mouseup.datatable-cell-blur' + this.id, (e: JQuery.TriggeredEvent) => {
                            // ignore if not editing
                            if(!this.currentCell)
                                return;

                            var currentCell = $(this.currentCell);
                            var target = $(e.target);

                            // ignore clicks inside edited cell
                            if(currentCell.is(target) || currentCell.has(target[0]!).length)
                                return;

                            // ignore clicks inside input overlays like calendar popups etc
                            var ignoredOverlay = '.p-input-overlay, .p-editor-popup, #keypad-div, .clr-picker';
                            // and menus - in case smth like menubutton is inside the table
                            ignoredOverlay += ', .p-menuitem, .p-menuitem-link';
                            // and blockers
                            ignoredOverlay += ', .p-blockui, .blockUI';
                            if(target.is(ignoredOverlay) || target.closest(ignoredOverlay).length)
                                return;

                            if(this.cfg.saveOnCellBlur)
                                this.saveCell(this.currentCell);
                            else
                                this.doCellEditCancelRequest(this.currentCell);
                        });
        }
    }

    /**
     * Switch all editable columns of the given row to their editing mode, if editing is enabled on this DataTable.
     * Use `findRow` to get a row by its index.
     * @param row A row (`TR`) to switch to edit mode.
     */
    switchToRowEdit(row: JQuery): void {
        // #1499 disable rowReorder while editing
        if (this.cfg.draggableRows) {
            PrimeFaces.interactions.disable(this.tbody, "sortable");
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

        // #12184 disable other row editors as you should only edit one row at a time
        this.tbody.find('a.p-row-editor-pencil').addClass('p-state-disabled');
    }

    /**
     * Shows the row editor(s) for the given row (and hides the normal output display).
     * @param row Row for which to show the row editor.
     */
    showRowEditors(row: JQuery): void {
        row.addClass('p-state-highlight p-row-editing').children('.p-editable-column').each((_i, el) => {
            var column = $(el);

            column.find('.p-cell-editor-output').hide();
            column.find('.p-cell-editor-input').show();
        });

        this.enableCellEditors(row);

        var inputs=row.find(':input:enabled');
        if (inputs.length > 0) {
            inputs.first().trigger('focus');
        }
    }

    /**
     * Finds the meta data for a given cell.
     * @param cell A cell for which to get the meta data.
     * @return The meta data of the given cell or NULL if not found
     */
    getCellMeta(cell: JQuery): string | null {
        var rowMeta = this.getRowMeta(cell.closest('tr')),
            cellIndex = cell.index();

        if(this.cfg.scrollable && this.cfg.frozenColumns) {
            cellIndex = (this.scrollTbody!.is(cell.closest('tbody'))) ? (cellIndex + this.cfg.frozenColumns) : cellIndex;
        }

        if (rowMeta === undefined || rowMeta.index === undefined) {
            return null;
        }
        var cellInfo = rowMeta.index + ',' + cellIndex;
        if(rowMeta.key) {
            cellInfo = cellInfo + ',' + rowMeta.key;
        }

        return cellInfo;
    }

    /**
     * Initializes the given cell so that its content can be edited (when row editing is enabled)
     * @param cell A cell of this DataTable to set up.
     */
    cellEditInit(cell: JQuery): void {
        var cellInfo = this.getCellMeta(cell),
        cellEditor = cell.children('.p-cell-editor');

        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            global: false,
            params: [{name: this.id + '_encodeFeature', value: true},
                    {name: this.id + '_cellEditInit', value: true},
                    {name: this.id + '_cellInfo', value: cellInfo}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            cellEditor.children('.p-cell-editor-input').html(content);
                        }
                    });

                return true;
            },
            oncomplete: (xhr, status, args, data) => {
                cell.data('edit-events-bound', false);
                this.showCurrentCell(cell);
            }
        };

        if(this.hasBehavior('cellEditInit')) {
            this.callBehavior('cellEditInit', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * When cell editing is enabled, shows the cell editor for the given cell that lets the user edit the cell content.
     * @param c A cell (`TD`) of this DataTable to edit.
     */
    showCellEditor(c?: JQuery): void {
        var cell = null;

        if(c) {
            cell = c;

            //remove contextmenu selection highlight
            if(this.contextMenuCell) {
                this.contextMenuCell.parent().removeClass('p-state-highlight');
            }
        }
        else {
            cell = this.contextMenuCell!;
        }

        var editorInput = cell.find('> .p-cell-editor > .p-cell-editor-input');
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
    }

    /**
     * Shows the cell editors for the given cell.
     * @param cell A cell of this DataTable.
     */
    showCurrentCell(cell: JQuery): void {
        if(this.currentCell) {
            if(this.cfg.saveOnCellBlur)
                this.saveCell(this.currentCell);
            else if(!this.currentCell.is(cell))
                this.doCellEditCancelRequest(this.currentCell);
        }

        if(cell && cell.length) {
            this.currentCell = cell;

            var cellEditor = cell.children('div.p-cell-editor'),
            displayContainer = cellEditor.children('div.p-cell-editor-output'),
            inputContainer = cellEditor.children('div.p-cell-editor-input');
            this.enableCellEditors(inputContainer);
            // Look for selectonemenu inputs first since they have special DOM structure, otherwise find any enabled visible inputs
            var inputs = inputContainer.find('.p-selectonemenu .p-inputfield');
            if (!inputs.length) {
                inputs = inputContainer.find(':input:enabled:not([type="hidden"])');
            }
            var multi = inputs.length > 1;

            cell.addClass('p-state-highlight p-cell-editing');
            displayContainer.hide();
            inputContainer.show();
            var input = inputs.eq(0);
            input.trigger('focus');
            input.trigger('select');

            //metadata
            if(multi) {
                var oldValues: unknown[] = [];
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
                cell.data('old-value', inputs.eq(0).val()!);
            }

            //bind events on demand
            if(!cell.data('edit-events-bound')) {
                cell.data('edit-events-bound', true);

                inputs.on('keydown.datatable-cell', (e) => {
                        var shiftKey = e.shiftKey,
                        key = e.key,
                        input = $(e.currentTarget);

                        if(key === 'Enter') {
                            // #7028/#13927 Do not proceed if target is a textarea, button, link, or TextEditor
                            if(PrimeFaces.utils.isEnterKeyBlocked(e)) {
                                return true;
                            }
                            this.saveCell(cell);
                            this.currentCell = null;

                            e.preventDefault();
                        }
                        else if(key === 'Tab') {
                            if(multi) {
                                // Calculate next/prev input index based on shift key
                                var focusIndex = shiftKey ? input.index() - 1 : input.index() + 1;

                                var parent = input.parent();
                                // Move to next/prev cell if:
                                // - Index out of bounds
                                // - Parent is special input component that handles its own tab behavior
                                if(focusIndex < 0 || focusIndex === inputs.length ||
                                   parent.hasClass('p-inputnumber') ||
                                   parent.hasClass('p-selectonemenu') ||
                                   parent.hasClass('p-hidden-accessible')) {
                                    this.tabCell(cell, !shiftKey);
                                } else {
                                    inputs.eq(focusIndex).trigger('focus');
                                }
                            }
                            else {
                                this.tabCell(cell, !shiftKey);
                            }

                            e.preventDefault();
                        }
                        else if(key === 'Escape') {
                            this.doCellEditCancelRequest(cell);
                            e.preventDefault();
                        }
                        return undefined;
                    })
                    .on('focus.datatable-cell click.datatable-cell', () => {
                        this.currentCell = cell;
                    });
            }
        }
        else {
            this.currentCell = null;
        }
    }

    /**
     * Moves to the next or previous editable cell when the tab key was pressed.
     * @param cell The currently focused cell
     * @param forward `true` if tabbing forward, `false` otherwise.
     */
    tabCell(cell: JQuery, forward: boolean): void {
        var targetCell = forward ? cell.nextAll('.p-editable-column:first') : cell.prevAll('.p-editable-column:first');
        if(targetCell.length == 0) {
            var tabRow = forward ? cell.parent().next() : cell.parent().prev();
            targetCell = forward ? tabRow.children('.p-editable-column:first') : tabRow.children('.p-editable-column:last');
        }

        var cellEditor = targetCell.children('div.p-cell-editor'),
        inputContainer = cellEditor.children('div.p-cell-editor-input');

        if(inputContainer.length) {
            var inputs = inputContainer.find(':input[type!=hidden]'),
            disabledInputs = inputs.filter(':disabled:not([data-disabled-by-editor="true"])');

            if(inputs.length === disabledInputs.length) {
                this.tabCell(targetCell, forward);
                return;
            }
        }

        this.showCellEditor(targetCell);
    }

    /**
     * After the user is done editing a cell, saves the content of the given cell and switches back to view mode.
     * @param cell A cell (`TD`) in edit mode.
     */
    saveCell(cell: JQuery): void {
        if (!cell) {
            return;
        }
        var inputs = cell.find('div.p-cell-editor-input :input:enabled'),
        changed = false,
        valid = cell.data('valid');

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
            this.doCellEditRequest(cell);
        else
            this.viewMode(cell);

        if(this.cfg.saveOnCellBlur) {
            this.currentCell = null;
        }
    }

    /**
     * Switches the given cell to its view mode (not editable).
     * @param cell A cell of this DataTable.
     */
    viewMode(cell: JQuery): void {
        var cellEditor = cell.children('div.p-cell-editor'),
        editableContainer = cellEditor.children('div.p-cell-editor-input'),
        displayContainer = cellEditor.children('div.p-cell-editor-output');

        cell.removeClass('p-cell-editing p-state-error p-state-highlight');
        displayContainer.show();
        editableContainer.hide();
        cell.removeData('old-value').removeData('multi-edit');

        if(this.cfg.cellEditMode === "lazy") {
            editableContainer.children().remove();
        }
    }

    /**
     * When the users clicks on an editable cell, runs the AJAX request to show the inline editor for the given cell.
     * @param cell The cell to switch to edit mode.
     */
    doCellEditRequest(cell: JQuery): void {
        var rowMeta = this.getRowMeta(cell.closest('tr')),
        cellEditor = cell.children('.p-cell-editor'),
        cellEditorId = cellEditor.attr('id'),
        cellIndex = cell.index();

        if(this.cfg.scrollable && this.cfg.frozenColumns) {
            cellIndex = (this.scrollTbody!.is(cell.closest('tbody'))) ? (cellIndex + this.cfg.frozenColumns) : cellIndex;
        }

        var cellInfo = rowMeta.index + ',' + cellIndex;
        if(rowMeta.key) {
            cellInfo = cellInfo + ',' + rowMeta.key;
        }

        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            params: [{name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_cellInfo', value: cellInfo},
                     {name: cellEditorId!, value: cellEditorId}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            cellEditor.children('.p-cell-editor-output').html(content);
                        }
                    });

                return true;
            },
            oncomplete: (xhr, status, args, data) => {
                if(args!['validationFailed']){
                    cell.data('valid', false);
                    cell.addClass('p-state-error');
                }
                else{
                    cell.data('valid', true);
                    this.disableCellEditors(cell);
                    this.viewMode(cell);
                }

                if(this.cfg.clientCache) {
                    this.clearCacheMap();
                }
            }
        };

        if(this.hasBehavior('cellEdit')) {
            this.callBehavior('cellEdit', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * When the user wants to discard the edits to a cell, performs the required AJAX request for that.
     * @param cell The cell in edit mode with changes to discard.
     */
    doCellEditCancelRequest(cell: JQuery): void {
        var rowMeta = this.getRowMeta(cell.closest('tr')),
        cellEditor = cell.children('.p-cell-editor'),
        cellIndex = cell.index();

        if(this.cfg.scrollable && this.cfg.frozenColumns) {
            cellIndex = (this.scrollTbody!.is(cell.closest('tbody'))) ? (cellIndex + this.cfg.frozenColumns) : cellIndex;
        }

        var cellInfo = rowMeta.index + ',' + cellIndex;
        if(rowMeta.key) {
            cellInfo = cellInfo + ',' + rowMeta.key;
        }

        this.currentCell = null;

        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            params: [{name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_cellEditCancel', value: true},
                     {name: this.id + '_cellInfo', value: cellInfo}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            cellEditor.children('.p-cell-editor-input').html(content);
                        }
                    });

                return true;
            },
            oncomplete: (xhr, status, args, data) => {
                this.viewMode(cell);
                cell.data('edit-events-bound', false);

                if(this.cfg.clientCache) {
                    this.clearCacheMap();
                }

                this.disableCellEditors();
            }
        };

        if(this.hasBehavior('cellEditCancel')) {
            this.callBehavior('cellEditCancel', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * When the given row is currently being edited, saves the contents of the edited row and switch back to view mode.
     * Use `findRow` to get a row by its index.
     * @param rowEditor A row (`TR`) in edit mode to save.
     */
    saveRowEdit(rowEditor: JQuery): void {
        this.doRowEditRequest(rowEditor, 'save');
    }

    /**
     * When the given row is currently being edited, cancel the editing operation and discard the entered data. Use
     * `findRow` to get a row by its index.
     * @param rowEditor A row (`TR`) in edit mode.
     */
    cancelRowEdit(rowEditor: JQuery): void {
        this.doRowEditRequest(rowEditor, 'cancel');
    }

    /**
     * Sends an AJAX request to handle row save or cancel
     * @param rowEditor The current row editor.
     * @param action Whether to save or cancel the row edit.
     */
    doRowEditRequest(rowEditor: JQuery, action: PrimeFacesTypes.widget.DataTable.RowEditAction): void {
        var row = rowEditor.closest('tr'),
        rowIndex = this.getRowMeta(row).index,
        expanded = row.hasClass('p-expanded-row'),
        options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            formId: this.getParentFormId(),
            params: [{name: this.id + '_rowEditIndex', value: this.getRowMeta(row).index},
                     {name: this.id + '_rowEditAction', value: action},
                     {name: this.id + '_encodeFeature', value: true}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            if(expanded) {
                                this.collapseRow(row);
                            }

                            this.updateRow(row, content);

                            // #1499 enable rowReorder when done editing
                            if (this.cfg.draggableRows && $('tr.p-row-editing').length === 0) {
                                PrimeFaces.interactions.enable(this.tbody, "sortable");
                            }

                            // #258 must reflow after editing
                            this.postUpdateData();

                            // #12184 enable other row edits now that we are done editing
                            this.tbody.find('a.p-row-editor-pencil').removeClass('p-state-disabled');
                        }
                    });

                return true;
            },
            oncomplete: (xhr, status, args, data) => {
                if(args && args['validationFailed']) {
                    this.invalidateRow(rowIndex);
                }
                else {
                    if(this.cfg.rowEditMode === "lazy") {
                        var index = (this.paginator) ? (rowIndex % this.paginator.getRows()) : rowIndex,
                        newRow = this.tbody.children('tr').eq(index);
                        this.getRowEditors(newRow).children('.p-cell-editor-input').children().remove();
                    }

                    this.disableCellEditors();
                }

                if(this.cfg.clientCache) {
                    this.clearCacheMap();
                }
            }
        };

        if(action === 'save') {
            this.getRowEditors(row).each((_i, el) => {
                options.params!.push({name: el.id, value: el.id});
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
    }

    /**
     * Performs the required initialization for making a row editable. Only called on-demand when the row actually needs
     * to be edited.
     * @param row A row of this DataTable.
     */
    lazyRowEditInit(row: JQuery): void {
        var rowIndex = this.getRowMeta(row).index;

        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            global: false,
            params: [{name: this.id + '_encodeFeature', value: true},
                    {name: this.id + '_rowEditInit', value: true},
                    {name: this.id + '_rowEditIndex', value: rowIndex}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            this.updateRow(row, content);
                        }
                    });

                return true;
            },
            oncomplete: (xhr, status, args, data) => {
                var index = (this.paginator) ? (rowIndex % this.paginator.getRows()) : rowIndex,
                newRow = this.tbody.children('tr').eq(index);
                this.showRowEditors(newRow);
            }
        };

        if(this.hasBehavior('rowEditInit')) {
            this.cfg.behaviors!['rowEditInit']!.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Updates a row with the given content and ensures it is visible.
     * @param row Row to update.
     * @param content HTML string to set on the row.
     */
    updateRow(row: JQuery, content: string): void {
        const $content = $(content).show();
        row.replaceWith($content);
    }

    /**
     * Displays row editors in invalid format.
     * @param index 0-based index of the row to invalidate.
     */
    invalidateRow(index: number): void {
        var i = (this.paginator) ? (index % this.paginator.getRows()) : index;
        var row =  this.tbody.children('tr[data-ri]').eq(i);
        row.addClass('p-widget-content p-row-editing p-state-error');
        this.enableCellEditors(row);
    }

    /**
     * Finds all editors of a row. Usually each editable column has got an editor.
     * @param row A row for which to find its row editors.
     * @return A list of row editors for each editable column of the given row
     */
    getRowEditors(row: JQuery): JQuery {
        return row.find('div.p-cell-editor');
    }

    /**
     * Returns the paginator instance if any exists.
     * @return The paginator instance for this widget, or `undefined` if paging is not enabled.
     */
    getPaginator(): PrimeFacesTypes.Widget<"Paginator"> | undefined {
        return this.paginator;
    }

    /**
     * Writes selected row ids to state holder
     */
    writeSelections(): void {
        $(this.selectionHolder!).val(this.selection!.join(','));
    }

    /**
     * Checks whether only one row may be selected at a time.
     * @return `true` if selection mode is set to `single`, or `false` otherwise.
     */
    isSingleSelection(): boolean {
        return this.cfg.selectionMode == 'single';
    }

    /**
     * Checks whether multiples rows may be selected at a time.
     * @return `true` if selection mode is set to `multiple`, or `false` otherwise.
     */
    isMultipleSelection(): boolean {
        return this.cfg.selectionMode == 'multiple' || this.isCheckboxSelectionEnabled();
    }

    /**
     * Clears the saved list of selected rows.
     */
    clearSelection(): void {
        this.selection = [];

        $(this.selectionHolder!).val('');
    }

    /**
     * Checks whether the user may select the rows of this DataTable.
     * @return `true` is rows may be selected, or `false` otherwise.
     */
    isSelectionEnabled(): boolean {
        return this.cfg.selectionMode != undefined || this.cfg.columnSelectionMode != undefined;
    }

    /**
     * Checks whether the rows of this DataTable are selected via checkboxes.
     * @return `true` if selection mode is set to `checkbox`, or `false` otherwise.
     */
    isCheckboxSelectionEnabled(): boolean {
        return this.cfg.selectionMode === 'checkbox';
    }

    /**
     * Checks whether the rows of this DataTable are selected via radio buttons.
     * @return `true` if selection mode is set to `radio`, or `false` otherwise.
     */
    isRadioSelectionEnabled(): boolean {
        return this.cfg.selectionMode === 'radio';
    }

    /**
     * Clears all table filters and shows all rows that may have been hidden by filters.
     */
    clearFilters(): void {
        var resetInputFields = function(inputFields: JQuery): void {
            inputFields.val('');
        };

        var resetWidget = function(widgetElement: JQuery): void {
            const selector = ':input:not(:disabled):not([readonly]), textarea:not(:disabled):not([readonly])';
            var widget = PrimeFaces.getWidgetById(widgetElement.attr('id')!);
            if (widget && 'resetValue' in widget && typeof widget.resetValue === 'function') {
                widget.resetValue(true);
            } else if (widgetElement.is(selector)) {
                resetInputFields(widgetElement);
            } else {
                // was "$(this).find(selector)" with "this" being undefined in this plain function (strict mode), so
                // this always was and still is an empty set
                resetInputFields($().find(selector));
            }
        };

        var standardFilters = this.thead.find('> tr > th.p-filter-column > .p-column-filter:not(:disabled):not([readonly])');
        resetInputFields(standardFilters);

        var customFilters = this.thead.find('> tr > th.p-filter-column > .p-column-customfilter');
        customFilters.each((_i, el) => {
            var widgetElement = $(el).find('.p-widget');
            if (widgetElement.length > 0) {
                resetWidget(widgetElement);
            } else {
                resetInputFields($(el).find(':input:not(:disabled):not([readonly]), textarea:not(:disabled):not([readonly])'));
            }
        });

        var globalFilter = $(this.jqId + '\\:globalFilter');
        resetInputFields(globalFilter);

        this.filter();
    }

    /**
     * Sets up the event listeners to enable columns to be resized.
     */
    setupResizableColumns(): void {
        this.cfg.resizeMode = this.cfg.resizeMode||'fit';

        this.fixColumnWidths();

        this.hasColumnGroup = this.hasColGroup();
        if(this.hasColumnGroup) {
            this.addGhostRow();
        }

        if(!this.cfg.liveResize) {
            this.resizerHelper = $('<div class="p-column-resizer-helper p-state-highlight"></div>').appendTo(this.jq);
        }

        this.addResizers();

        var resizers = this.thead.find('> tr > th > span.p-column-resizer');

        // #11918 double click resizes column like Excel
        resizers.on('dblclick', (e) => {
            this.autosizeColumnWidth($(e.currentTarget).parent());
        });

        PrimeFaces.interactions.draggable(resizers, {
            axis: 'x',
            start: (event, ui: PrimeFacesTypes.interactions.DraggableUi) => {
                ui.helper.data('originalposition', ui.helper.offset()!);

                if(this.cfg.liveResize) {
                    this.jq.css('cursor', 'col-resize');
                }
                else {
                    var header = this.cfg.stickyHeader ? this.clone! : this.thead,
                        height = this.cfg.scrollable ? this.scrollBody!.height()! : header.parent().height()! - header.height()! - 1;

                    if(this.cfg.stickyHeader) {
                        height = height - this.relativeHeight!;
                    }

                    this.resizerHelper!.height(height);
                    this.resizerHelper!.show();
                }
            },
            drag: (event, ui: PrimeFacesTypes.interactions.DraggableUi) => {
                if(this.cfg.liveResize) {
                    this.resize(event, ui);
                }
                else {
                    this.resizerHelper!.offset({
                        left: ui.helper.offset()!.left + ui.helper.width()! / 2,
                        top: this.thead.offset()!.top + this.thead.height()!
                    });
                }
            },
            stop: (event, ui: PrimeFacesTypes.interactions.DraggableUi) => {
                ui.helper.css({
                    'left': '',
                    'top': '0px'
                });

                if(this.cfg.liveResize) {
                    this.jq.css('cursor', 'default');
                } else {
                    this.resize(event, ui);
                    this.resizerHelper!.hide();
                }

                if(this.cfg.resizeMode === 'expand') {
                    PrimeFaces.queueTask(() => {
                        this.fireColumnResizeEvent(ui.helper.parent());
                    });
                }
                else {
                    this.fireColumnResizeEvent(ui.helper.parent());
                }

                if(this.cfg.stickyHeader) {
                    this.reclone();
                }
            },
            containment: this.cfg.resizeMode === "expand" ? "document" : this.jq
        });
    }

    /**
     * Invokes the behaviors and event listeners when a column is resized.
     * @param columnHeader Header of the column which was resized.
     */
    fireColumnResizeEvent(columnHeader: JQuery): void {
        if(this.hasBehavior('colResize')) {
            var options = {
                source: this.getId(),
                process: this.getId(),
                params: [
                    {name: this.id + '_colResize', value: true},
                    {name: this.id + '_columnId', value: columnHeader.attr('id')},
                    // parseInt of a number, String() is what parseInt does implicitly
                    {name: this.id + '_width', value: parseInt(String(columnHeader.width()))},
                    {name: this.id + '_height', value: parseInt(String(columnHeader.height()))}
                ]
            };

            this.callBehavior('colResize', options);
        }
    }

    /**
     * Checks whether this DataTable has got any column groups.
     * @return `true` if this DataTable has got any column groups, or `false` otherwise.
     */
    hasColGroup(): boolean {
        return this.thead.children('tr').length > 1;
    }

    /**
     * Checks whether this DataTable has got an auto layout.
     * @return `true` if this DataTable has got an auto layout, or `false` otherwise.
     */
    isAutoLayout(): boolean {
        return $(this.jq).find("table").css("table-layout") === "auto";
    }

    /**
     * Retrieves the first data row in the table body, excluding any empty message rows.
     * This is used to calculate the proper column widths.
     * @returns The first table row that does not have the 'p-datatable-empty-message' class.
     */
    firstRow(): JQuery {
        return this.tbody.find("tr:not(.p-datatable-empty-message)").first();
    }

    /**
     * Adds and sets up an invisible row for internal purposes.
     */
    addGhostRow(): void {
        const firstRow = this.firstRow();
        if(firstRow.length === 0) {
            return;
        }

        var columnsOfFirstRow = firstRow.children(),
        dataColumnsCount = columnsOfFirstRow.length,
        columnMarkup = '';

        for(var i = 0; i < dataColumnsCount; i++) {
            var colWidth: string | number = columnsOfFirstRow.eq(i).width()! + 1,
            id = this.id + '_ghost_' + i;

            if (this.resizableState) {
                colWidth = this.findColWidthInResizableState(id) || colWidth;
            }

            columnMarkup += '<th id="' + id + '" style="height:0px;border-bottom-width: 0px;border-top-width: 0px;padding-top: 0px;padding-bottom: 0px;outline: 0 none; width:' + colWidth + 'px" class="p-resizable-column"></th>';
        }

        this.thead.prepend('<tr>' + columnMarkup + '</tr>');

        if(this.cfg.scrollable) {
            this.theadClone!.prepend('<tr>' + columnMarkup + '</tr>');
            this.footerTable!.children('tfoot').prepend('<tr>' + columnMarkup + '</tr>');
        }
    }

    /**
     * Finds the group resizer element for the given drag event data.
     * @param ui Data for the drag event.
     * @return The resizer DOM element.
     */
    findGroupResizer(ui: PrimeFacesTypes.interactions.DraggableUi): JQuery | null {
        for(var i = 0; i < this.groupResizers!.length; i++) {
            var groupResizer = this.groupResizers!.eq(i);
            if(groupResizer.offset()!.left === ui.helper.data('originalposition').left) {
                return groupResizer;
            }
        }

        return null;
    }

    /**
     * Adds the resizers for change the width of a column of this DataTable.
     */
    addResizers(): void {
        var resizableColumns = this.thead.find('> tr > th.p-resizable-column');
        resizableColumns.prepend('<span class="p-column-resizer">&nbsp;</span>');

        if(this.cfg.resizeMode === 'fit') {
            var child = this.isRTL ? ':first-child' : ':last-child';
            resizableColumns.filter(child).children('span.p-column-resizer').hide();
        }

        if(this.hasColumnGroup) {
            this.groupResizers = this.thead.find('> tr:first > th > .p-column-resizer');
        }
    }

    /**
     * Resizes this DataTable, row, or columns in response to a drag event of a resizer element.
     * @param event Event triggered for the drag.
     * @param ui Data for the drag event.
     */
    resize(event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DraggableUi): void {
        var columnHeader: JQuery, nextColumnHeader: JQuery, change: number | null = null, newWidth: number | null = null, nextColumnWidth: number | null = null,
        expandMode = (this.cfg.resizeMode === 'expand'),
        table = this.thead.parent();

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

        var nextColumnHeader: JQuery = this.isRTL ? columnHeader.prevAll(':visible:first') : columnHeader.nextAll(':visible:first');

        if(this.cfg.liveResize) {
            change = columnHeader.outerWidth()! - (event.pageX! - columnHeader.offset()!.left);
            newWidth = (columnHeader.width()! - change);
            nextColumnWidth = (nextColumnHeader.width()! + change);
        }
        else {
            change = (ui.position.left - ui.originalPosition.left);
            newWidth = (columnHeader.width()! + change);
            nextColumnWidth = (nextColumnHeader.width()! - change);
        }

        var minWidth = parseInt(columnHeader.css('min-width'));
        minWidth = (minWidth == 0) ? 15 : minWidth;

        if((newWidth > minWidth && nextColumnWidth > minWidth) || (expandMode && newWidth > minWidth)) {
            if(expandMode) {
                table.width(table.width()! + change);
                PrimeFaces.queueTask(() => {
                    columnHeader.width(newWidth!);
                    this.updateResizableState(columnHeader, nextColumnHeader, table, newWidth!, null);
                });
            }
            else {
                columnHeader.width(newWidth);
                nextColumnHeader.width(nextColumnWidth);
                this.updateResizableState(columnHeader, nextColumnHeader, table, newWidth, nextColumnWidth);
            }

            if(this.cfg.scrollable) {
                var cloneTable = this.theadClone!.parent(),
                colIndex = columnHeader.index();

                if(expandMode) {
                    //body
                    cloneTable.width(cloneTable.width()! + change);

                    //footer
                    this.footerTable!.width(this.footerTable!.width()! + change);

                    PrimeFaces.queueTask(() => {
                        if(this.hasColumnGroup) {
                            this.theadClone!.find('> tr:first').children('th').eq(colIndex).width(newWidth!);            //body
                            this.footerTable!.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth!);   //footer
                        }
                        else {
                            this.theadClone!.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth!);   //body
                            this.footerCols!.eq(colIndex).width(newWidth!);                                                          //footer
                        }
                    });
                }
                else {
                    if(this.hasColumnGroup) {
                        //body
                        this.theadClone!.find('> tr:first').children('th').eq(colIndex).width(newWidth);
                        this.theadClone!.find('> tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);

                        //footer
                        this.footerTable!.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth);
                        this.footerTable!.find('> tfoot > tr:first').children('th').eq(colIndex + 1).width(nextColumnWidth);
                    }
                    else {
                        //body
                        this.theadClone!.find(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).width(newWidth);
                        this.theadClone!.find(PrimeFaces.escapeClientId(nextColumnHeader.attr('id') + '_clone')).width(nextColumnWidth);

                        //footer
                        if(this.footerCols!.length > 0) {
                            var footerCol = this.footerCols!.eq(colIndex),
                            nextFooterCol = footerCol.next();

                            footerCol.width(newWidth);
                            nextFooterCol.width(nextColumnWidth);
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove given row from the list of selected rows.
     * @param rowKey Key of the row to remove.
     */
    removeSelection(rowKey: string | undefined): void {
        if(this.selection!.includes('@all')) {
            // GitHub #14538,#3535: Keep @all and add negative rowKey to mark this row as unselected
            this.selection!.push('!' + rowKey);
        }
        else {
            this.selection = $.grep(this.selection!, function(value) {
                return value !== rowKey;
            });
        }
    }

    /**
     * Adds given row to the list of selected rows.
     * @param rowKey Key of the row to add.
     */
    addSelection(rowKey: string | undefined): void {
        if(this.selection!.includes('@all')) {
            // GitHub #14538,#3535: Remove the deselection marker if it exists (reselecting a deselected row)
            var deselectionMarker = '!' + rowKey;
            this.selection = $.grep(this.selection!, function(value) {
                return value !== deselectionMarker;
            });
        }
        else if(!this.isSelected(rowKey)) {
            this.selection!.push(rowKey!);
        }
    }

    /**
     * Checks whether the given row is currently selected.
     * @param rowKey The key of a row from this DataTable.
     * @return `true` if the given row is currently selected, or `false` otherwise.
     */
    isSelected(rowKey: string | undefined): boolean {
        return PrimeFaces.inArray(this.selection!, rowKey);
    }

    /**
     * Finds the index and the row key for the given row.
     * @param row The element (`TR`) of a row of this DataTable.
     * @return The meta for the row with the index and the row key.
     */
    getRowMeta(row: JQuery): PrimeFacesTypes.widget.DataTable.RowMeta {
        var meta = {
            index: row.data('ri'),
            key:  row.attr('data-rk')
        };

        return meta;
    }

    /**
     * Sets up all event listeners required for making column draggable and reorderable.
     */
    setupDraggableColumns(): void {
        this.orderStateHolder = $(this.jqId + '_columnOrder');
        this.saveColumnOrder();

        this.dragIndicatorTop = $('<span class="p-icon p-arrow-down-icon p-datatable-column-reorder-top-icon" style="position:absolute"></span>').hide().appendTo(this.jq);
        this.dragIndicatorBottom = $('<span class="p-icon p-arrow-up-icon p-datatable-column-reorder-bottom-icon" style="position:absolute"></span>').hide().appendTo(this.jq);

        var $this = this,
            columns = $(this.jqId + ' thead th.p-draggable-column');

        PrimeFaces.interactions.draggable(columns, {
            appendTo: 'body',
            opacity: 0.75,
            cursor: 'move',
            scope: this.getId(),
            cancel: ':input,.p-column-resizer',
            start: function(event, ui: PrimeFacesTypes.interactions.DraggableUi) {
                PrimeFaces.nextZindex(ui.helper);
            },
            drag: function(event, ui: PrimeFacesTypes.interactions.DraggableUi) {
                var droppable: JQuery | undefined = ui.helper.data('droppable-column');

                if(droppable) {
                    var droppableOffset = droppable.offset()!,
                    topArrowY = droppableOffset.top - 10,
                    bottomArrowY = droppableOffset.top + droppable.height()! + 8,
                    arrowX = null;

                    //calculate coordinates of arrow depending on mouse location
                    if((event.originalEvent as MouseEvent).pageX >= droppableOffset.left + (droppable.width()! / 2)) {
                        var nextDroppable = droppable.next();
                        if(nextDroppable.length == 1)
                            arrowX = nextDroppable.offset()!.left - 9;
                        else
                            arrowX = droppable.offset()!.left + droppable.innerWidth()! - 9;

                        ui.helper.data('drop-location', 1);     //right
                    }
                    else {
                        arrowX = droppableOffset.left  - 9;
                        ui.helper.data('drop-location', -1);    //left
                    }

                    $this.dragIndicatorTop!.offset({
                        'left': arrowX,
                        'top': topArrowY - 3
                    }).show();

                    $this.dragIndicatorBottom!.offset({
                        'left': arrowX,
                        'top': bottomArrowY - 3
                    }).show();
                }
            },
            stop: function(event, ui: PrimeFacesTypes.interactions.DraggableUi) {
                //hide dnd arrows
                $this.dragIndicatorTop!.css({
                    'left':'0px',
                    'top':'0px'
                }).hide();

                $this.dragIndicatorBottom!.css({
                    'left':'0px',
                    'top':'0px'
                }).hide();
            },
            helper: function(this: HTMLElement) {
                var header = $(this),
                helper = $('<div class="p-widget p-state-default" style="padding:4px 10px;text-align:center;"></div>');

                helper.width(header.width()!);
                helper.height(header.height()!);

                helper.html(header.html());

                return helper.get(0)!;
            }
        });

        PrimeFaces.interactions.droppable(columns, {
            hoverClass:'p-state-highlight',
            tolerance:'pointer',
            scope: this.getId(),
            over: function(this: HTMLElement, event, ui: PrimeFacesTypes.interactions.DroppableUi) {
                ui.helper!.data('droppable-column', $(this));
            },
            drop: function(this: HTMLElement, event, ui: PrimeFacesTypes.interactions.DroppableUi) {
                var draggedColumnHeader = ui.draggable,
                dropLocation = ui.helper!.data('drop-location'),
                droppedColumnHeader =  $(this),
                draggedColumnFooter: JQuery | null = null,
                droppedColumnFooter: JQuery | null = null;

                var draggedCells = $this.tbody.find('> tr:not(.p-expanded-row-content) > *:nth-child(' + (draggedColumnHeader.index() + 1) + ')'),
                droppedCells = $this.tbody.find('> tr:not(.p-expanded-row-content) > *:nth-child(' + (droppedColumnHeader.index() + 1) + ')');

                if($this.tfoot.length) {
                    var footerColumns = $this.tfoot.find('> tr > td'),
                    draggedColumnFooter: JQuery | null = footerColumns.eq(draggedColumnHeader.index()),
                    droppedColumnFooter: JQuery | null = footerColumns.eq(droppedColumnHeader.index());
                }

                //drop right
                if(dropLocation > 0) {
                    if($this.cfg.resizableColumns) {
                        if(droppedColumnHeader.next().length === 0) {
                            droppedColumnHeader.children('span.p-column-resizer').show();
                            draggedColumnHeader.children('span.p-column-resizer').hide();
                        }
                    }

                    draggedColumnHeader.insertAfter(droppedColumnHeader);

                    draggedCells.each(function(i, item) {
                        $(item).insertAfter(droppedCells.eq(i));
                    });

                    if(draggedColumnFooter && droppedColumnFooter) {
                        draggedColumnFooter.insertAfter(droppedColumnFooter);
                    }

                    //sync clone
                    if($this.cfg.scrollable) {
                        var draggedColumnClone = $(document.getElementById(draggedColumnHeader.attr('id') + '_clone')!),
                        droppedColumnClone = $(document.getElementById(droppedColumnHeader.attr('id') + '_clone')!);
                        draggedColumnClone.insertAfter(droppedColumnClone);
                    }
                }
                //drop left
                else {
                    draggedColumnHeader.insertBefore(droppedColumnHeader);

                    draggedCells.each(function(i, item) {
                        $(item).insertBefore(droppedCells.eq(i));
                    });

                    if(draggedColumnFooter && droppedColumnFooter) {
                        draggedColumnFooter.insertBefore(droppedColumnFooter);
                    }

                    //sync clone
                    if($this.cfg.scrollable) {
                        var draggedColumnClone = $(document.getElementById(draggedColumnHeader.attr('id') + '_clone')!),
                        droppedColumnClone = $(document.getElementById(droppedColumnHeader.attr('id') + '_clone')!);
                        draggedColumnClone.insertBefore(droppedColumnClone);
                    }
                }

                //save order
                $this.saveColumnOrder();

                // update headers
                $this.headers = $this.thead.find('> tr > th');

                //fire colReorder event
                if($this.hasBehavior('colReorder')) {
                    // was null before, callBehavior only accepts undefined
                    var ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> | undefined = undefined;

                    if($this.cfg.multiViewState) {
                        ext = {
                            // "this" is the dropped column header element here
                            params: [{name: this.id + '_encodeFeature', value: true}]
                        };
                    }

                    $this.callBehavior('colReorder', ext);
                }
            }
        });

        // GitHub #5013 Frozen Columns should not be draggable/droppable
        if($this.cfg.frozenColumns) {
            var frozenHeaders = this.frozenThead!.find('.p-frozen-column');
            PrimeFaces.interactions.disable(frozenHeaders, 'draggable');
            PrimeFaces.interactions.disable(frozenHeaders, 'droppable');
            frozenHeaders.on('selectstart', function(e) {
                e.preventDefault();
            });
        }
    }

    /**
     * Saves the current column order, used to preserve the state between AJAX updates etc.
     */
    saveColumnOrder(): void {
        var columnIds: (string | undefined)[] = [],
        columns = $(this.jqId + ' thead:first th');

        columns.each(function(i, item) {
            columnIds.push($(item).attr('id'));
        });

        this.orderStateHolder!.val(columnIds.join(','));
    }

    /**
     * Makes the rows of this DataTable draggable.
     */
    makeRowsDraggable(): void {
        var draggableHandle = this.cfg.rowDragSelector||DataTable.cellSelector() + ',span:not(.p-c)';

        PrimeFaces.interactions.sortable(this.tbody, {
            placeholder: 'p-datatable-rowordering p-state-active',
            cursor: 'move',
            handle: draggableHandle,
            appendTo: document.body,
            start: (event, ui: PrimeFacesTypes.interactions.SortableUi) => {
                PrimeFaces.nextZindex(ui.helper!);
            },
            helper: (event, ui) => {
                var cells = ui.children(),
                helper = $('<div class="p-datatable p-widget"><table><tbody class="p-datatable-data"></tbody></table></div>'),
                helperRow = ui.clone(),
                helperCells = helperRow.children();

                for(var i = 0; i < helperCells.length; i++) {
                    var helperCell = helperCells.eq(i);
                    helperCell.width(cells.eq(i).width()!);
                    // #5584 reflow must remove column title span
                    helperCell.children().remove('.p-column-title');
                }

                helperRow.appendTo(helper.find('tbody'));

                return helper;
            },
            update: (event, ui: PrimeFacesTypes.interactions.SortableUi) => {
                var fromIndex = ui.item.data('ri');

                // #5296 must not count header group rows
                // #6557 must not count expanded rows
                var itemIndex = ui.item.prevAll('tr').not('.p-rowgroup-header').not('.p-expanded-row-content').length;
                var toIndex = this.paginator ? this.paginator.getFirst() + itemIndex : itemIndex;

                this.syncRowParity();

                var options: PrimeFacesTypes.ajax.Configuration = {
                    source: this.getId(),
                    process: this.getId(),
                    params: [
                        {name: this.id + '_rowreorder', value: true},
                        {name: this.id + '_fromIndex', value: fromIndex},
                        {name: this.id + '_toIndex', value: toIndex},
                        {name: this.id + '_skipChildren', value: true}
                    ]
                }

                if(this.hasBehavior('rowReorder')) {
                    this.callBehavior('rowReorder', options);
                }
                else {
                    PrimeFaces.ajax.Request.handle(options);
                }
            },
            change: (event, ui: PrimeFacesTypes.interactions.SortableUi) => {
                if(this.cfg.scrollable) {
                    PrimeFaces.scrollInView(this.scrollBody!, ui.placeholder);
                }
            }
        });
    }

    /**
     * Sets the style class on each, depending whether it is an even-numbered or odd-numbered row.
     */
    syncRowParity(): void {
        var rows = this.tbody.children('tr.p-widget-content'),
        first: number = this.paginator ? this.paginator.getFirst(): 0;

        for(var i = first; i < rows.length; i++) {
            var row = rows.eq(i);

            row.data('ri', i).removeClass('p-datatable-even p-datatable-odd');

            if(i % 2 === 0)
                row.addClass('p-datatable-even');
            else
                row.addClass('p-datatable-odd');

        }
    }

    /**
     * Checks whether this DataTable has got any rows. When there are no rows, usually the message `no items found` is
     * shown.
     * @return `true` if this DataTable has got no rows, `false` otherwise.
     */
    isEmpty(): boolean {
        return this.tbody.children('tr.p-datatable-empty-message').length === 1;
    }

    /**
     * Finds the number of rows that are selected.
     * @return The number of rows that are currently selected.
     */
    getSelectedRowsCount(): number {
        return this.isSelectionEnabled() ? this.selection!.length : 0;
    }

    /**
     * Updates the `check all` checkbox in the header of this DataTable.
     */
    updateHeaderCheckbox(): void {
        if(this.isEmpty()) {
            this.uncheckHeaderCheckbox();
            this.disableHeaderCheckbox();
        }
        else if(!this.cfg.selectionPageOnly && this.selection!.includes('@all')) {
            this.enableHeaderCheckbox();
            this.checkHeaderCheckbox();
        }
        else {
            var checkboxes: JQuery, selectedCheckboxes: JQuery, enabledCheckboxes: JQuery, disabledCheckboxes: JQuery;

            if(this.cfg.nativeElements) {
                checkboxes = this.tbody.find('> tr > td.p-selection-column > :checkbox');
                enabledCheckboxes = checkboxes.filter(':enabled');
                disabledCheckboxes = checkboxes.filter(':disabled');
                selectedCheckboxes = enabledCheckboxes.filter(':checked');
            }
            else {
                checkboxes = this.tbody.find('> tr > td.p-selection-column > div.p-chkbox > .p-chkbox-box');
                enabledCheckboxes = checkboxes.filter(':not(.p-state-disabled)');
                disabledCheckboxes = checkboxes.filter('.p-state-disabled');
                selectedCheckboxes = enabledCheckboxes.filter("div[aria-checked='true']");
            }

            var totalEnabled = enabledCheckboxes.length;
            if (this.cfg.selectionPageOnly) {
                if(totalEnabled && totalEnabled === selectedCheckboxes.length)
                    this.checkHeaderCheckbox();
                else
                    this.uncheckHeaderCheckbox();
            }

            if(checkboxes.length === disabledCheckboxes.length)
               this.disableHeaderCheckbox();
            else
               this.enableHeaderCheckbox();
        }
    }

    /**
     * Checks the `select all` checkbox in the header of this DataTable.
     */
    checkHeaderCheckbox(): void {
        if(this.cfg.nativeElements) {
            this.checkAllToggler!.prop('checked', true);
        }
        else {
            this.checkAllToggler!.addClass('p-state-active').children('span.p-chkbox-icon').addClass('p-checkbox-checked-icon');
            this.checkAllToggler!.attr('aria-checked', 'true');
        }
    }

    /**
     * Unchecks the `select all` checkbox in the header of this data table.
     */
    uncheckHeaderCheckbox(): void {
        if(this.cfg.nativeElements) {
            this.checkAllToggler!.prop('checked', false);
        }
        else {
            this.checkAllToggler!.removeClass('p-state-active').children('span.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
            this.checkAllToggler!.attr('aria-checked', 'false');
        }
    }

    /**
     * Disables the `select all` checkbox in the header of this DataTable.
     */
    disableHeaderCheckbox(): void {
        if(this.cfg.nativeElements)
            this.checkAllToggler!.prop('disabled', true);
        else
            this.checkAllToggler!.addClass('p-state-disabled');
    }

    /**
     * Enables the `select all` checkbox in the header of this DataTable.
     */
    enableHeaderCheckbox(): void {
        if(this.cfg.nativeElements)
            this.checkAllToggler!.prop('disabled', false);
        else
            this.checkAllToggler!.removeClass('p-state-disabled');
    }

    /**
     * Applies the styling and event listeners required for the sticky headers feature.
     */
    setupStickyHeader(): void {
        var table = this.thead.parent(),
            offset = table.offset()!,
            orginTableContent = this.jq.find('> .p-datatable-tablewrapper > table'),
            fixedElementsOnTop = this.cfg.stickyTopAt ? $(this.cfg.stickyTopAt) : null,
            fixedElementsHeight = 0;

        if (fixedElementsOnTop && fixedElementsOnTop.length) {
            fixedElementsHeight = fixedElementsOnTop.toArray().reduce(function(height: number, elem: HTMLElement): number {
                return height + $(elem).outerHeight()!;
            }, 0);
        }

        this.stickyContainer = $('<div class="p-datatable p-datatable-sticky p-widget"><table></table></div>');
        this.clone = this.thead.clone(false);
        this.stickyContainer.children('table').append(this.thead);
        table.prepend(this.clone);

        this.stickyContainer.css({
            position: 'absolute',
            width: table.outerWidth()!,
            top: offset.top,
            left: offset.left,
            display: 'none'
        });

        this.jq.prepend(this.stickyContainer);

        if (this.cfg.resizableColumns) {
            this.relativeHeight = 0;
        }

        var updateStickyHeaderPosition = () => {
            var scrollTop = $(window).scrollTop()!,
                tableOffset = table.offset()!;

            if (scrollTop + fixedElementsHeight > tableOffset.top) {
                if (!this.stickyContainer!.hasClass('p-sticky')) {
                    this.stickyContainer!.css({ 'z-index': PrimeFaces.utils.nextStickyZindex() });
                }
                this.stickyContainer!.css({
                    position: 'fixed',
                    top: fixedElementsHeight
                }).addClass('p-sticky');

                if (this.cfg.resizableColumns) {
                    this.relativeHeight = (scrollTop + fixedElementsHeight) - tableOffset.top;
                }

                if (scrollTop + fixedElementsHeight >= (tableOffset.top + this.tbody.height()!)) {
                    this.stickyContainer!.hide();
                } else {
                    this.stickyContainer!.show();
                }
            } else {
                this.stickyContainer!.css({
                    position: 'absolute',
                    top: tableOffset.top
                }).removeClass('p-sticky');

                if (this.stickyContainer!.is(':hidden')) {
                    this.stickyContainer!.css({ 'z-index': PrimeFaces.utils.nextStickyZindex() });
                    this.stickyContainer!.show();
                }

                if (this.cfg.resizableColumns) {
                    this.relativeHeight = 0;
                }
            }
        };

        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id, updateStickyHeaderPosition);

        PrimeFaces.utils.registerResizeHandler(this, 'resize.sticky-' + this.id, null, (e) => {
            var _delay = e.data.delay || 0;

            if (_delay !== null && typeof _delay === 'number' && _delay > -1) {
                if (this.resizeTimeout) {
                    clearTimeout(this.resizeTimeout);
                }

                this.stickyContainer!.hide();
                this.resizeTimeout = PrimeFaces.queueTask(() => {
                    this.stickyContainer!.css({ left: orginTableContent.offset()!.left + 'px', 'z-index': PrimeFaces.utils.nextStickyZindex() });
                    this.stickyContainer!.width(table.outerWidth()!);
                    // Dispatch the scroll event on the window
                    window.dispatchEvent(new Event('scroll'));
                }, _delay);
            }
            else {
                this.stickyContainer!.width(table.outerWidth()!);
            }
        // the event data, the parameter is declared as string in core, but any data is passed on to jQuery
        }, { delay: null } as unknown as string);

        //filter support
        this.clone.find('.p-column-filter').prop('disabled', true);

        // set original state upon loading
        updateStickyHeaderPosition();

        // must clean up memory when this widget is destroyed
        this.addDestroyListener(() => {
            PrimeFaces.utils.cleanseDomElement(this.clone);
            PrimeFaces.utils.cleanseDomElement(this.stickyContainer);
        });
    }

    /**
     * Initializes the expansion state
     */
    initRowExpansion(): void {
        this.expansionHolder = $(this.jqId + '_rowExpansionState');
        this.loadedExpansionRows = this.tbody.children('.p-expanded-row-content').prev().map((_i, el) => {
            return this.getRowMeta($(el)).key;
        }).get();

        this.writeRowExpansions();

        PrimeFaces.utils.registerResizeHandler(this, 'resize.expansion-' + this.id, null, (e) => {
            this.updateExpandedRowsColspan();
        });
    }

    /**
     * Write row expansion state.
     */
    writeRowExpansions(): void {
        this.expansionHolder!.val(this.loadedExpansionRows!.join(','));
    }

    /**
     * Finds the body of this DataTable with the property that the user can focus it.
     * @return The body of this DataTable.
     */
    getFocusableTbody(): JQuery {
        return this.tbody;
    }

    /**
     * Removes the current clone of the table header from the DOM, and creates a new clone.
     */
    reclone(): void {
        PrimeFaces.utils.cleanseDomElement(this.clone);
        this.clone = this.thead.clone(false);
        this.jq.find('.p-datatable-tablewrapper > table').prepend(this.clone);
    }

    /**
     * Fetches the last row from the backend and inserts a row instead of updating the table itself.
     */
    addRow(): void {
        var options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            params: [{name: this.id + '_addrow', value: true},
                    {name: this.id + '_skipChildren', value: true},
                    {name: this.id + '_encodeFeature', value: true}],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.tbody.append(content);
                    }
                });

                if (this.isEmpty()) {
                    this.tbody.children('tr.p-datatable-empty-message').remove();
                }

                if (this.isCheckboxSelectionEnabled()) {
                    this.enableHeaderCheckbox();
                }

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Clears all cached rows so that they are loaded from the server the next time they are requested.
     */
    clearCacheMap(): void {
        this.cacheMap = {};
    }

    /**
     * Loads the data for the given page and displays it. When some rows exist in the cache, do not reload them from the
     * server.
     * @param newState The new values for the current page and the rows per page count.
     */
    loadDataWithCache(newState: PrimeFacesTypes.widget.Paginator.PaginationState): void {
        var isRppChanged = false;
        if(this.cacheRows != newState.rows) {
            this.clearCacheMap();
            this.cacheRows = newState.rows;
            isRppChanged = true;
        }

        var pageFirst = newState.first,
            nextPageFirst = newState.rows + pageFirst,
            lastPageFirst = this.cfg.paginator!.pageCount! * newState.rows,
            hasNextPage = (!this.cacheMap![nextPageFirst]) && nextPageFirst < lastPageFirst;

        if(this.cacheMap![pageFirst] && !isRppChanged) {
            this.updateData(this.cacheMap![pageFirst]!);
            this.paginator!.cfg.page = newState.page;
            this.paginator!.updateUI();

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
    }

    /**
     * Reflow mode is a responsive mode to display columns as stacked depending on screen size. Updates the reflow for
     * the given header.
     * @param columnHeader Header of a column to update.
     * @param sortOrder Sort order of the column.
     */
    updateReflowDD(columnHeader: JQuery, sortOrder: number): void {
        if(this.reflowDD && this.cfg.reflow) {
            sortOrder = sortOrder > 0 ? 0 : 1;

            // was "var columnHeader", re-declaring the parameter as a string
            var columnHeaderText = columnHeader.text().replace(/[^a-zA-Z0-9\u00C0-\u017F]/g, '');
            var filterby = columnHeaderText.indexOf("Filter by");
            if (filterby !== -1) {
                columnHeaderText = columnHeaderText.substring(0, filterby);
            }
            columnHeaderText = CSS.escape(columnHeaderText);

            this.reflowDD.children('option').each((_i, el) => {
                const option = el as HTMLOptionElement;
                var optionLabel = CSS.escape(option.text);
                var optionSortOrder = $(option).data('sortorder');
                option.selected = optionLabel.startsWith(columnHeaderText) && optionSortOrder == sortOrder;
            });
        }
    }

    /**
     * When row grouping is enabled, groups all rows accordingly.
     */
    groupRows(): void {
        var rows = this.tbody.children('tr');

        // see #8027
        // remember the original column index
        // columns are removed because of grouping and mapping to the header isnt possible anymore in #updateColumnsView
        if(this.headers && !this.hasColGroup()) {
            for(var i = 0; i < this.headers.length; i++) {
                var header = this.headers.eq(i),
                    index = header.index(),
                    column = this.tbody.find('> tr:not(.p-expanded-row-content) > *:nth-child(' + (index + 1) + ')');

                column.data('ci', index);
            }
        }

        for(var i = 0; i < this.cfg.groupColumnIndexes!.length; i++) {
            this.groupRow(this.cfg.groupColumnIndexes![i]!, rows);
        }

        rows.children('.p-duplicated-column').remove();
    }

    /**
     * Called by `groupRows`, this method performs the grouping of a single set of rows that belong to one row group.
     * @param colIndex Index of the column to group.
     * @param rows Rows to group into one row group.
     */
    groupRow(colIndex: number, rows: JQuery): void {
        var groupStartIndex: number | null = null, rowGroupCellData: string | null = null, rowGroupCount: number | null = null;

        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            var column = row.children().eq(colIndex);
            var columnData = column.text();

            if(rowGroupCellData != columnData) {
                groupStartIndex = i;
                rowGroupCellData = columnData;
                rowGroupCount = 1;

                if (this.cfg.liveScroll && column[0]!.hasAttribute('rowspan')) {
                    rowGroupCount = parseInt(column.attr('rowspan')!);
                    i += rowGroupCount - 1;
                }

                row.addClass('p-datatable-grouped-row');
            }
            else {
                column.addClass('p-duplicated-column');
                rowGroupCount!++;
            }

            if(groupStartIndex != null && rowGroupCount! > 1) {
                rows.eq(groupStartIndex).children().eq(colIndex).attr('rowspan', rowGroupCount);
            }
        }
    }

    /**
     * Sets up the event handlers for row group events.
     */
    bindToggleRowGroupEvents(): void {
        var expandableRows = this.tbody.children('tr.p-rowgroup-header'),
            toggler = expandableRows.find('> td:first > a.p-rowgroup-toggler');

        toggler.off('click.dataTable-rowgrouptoggler').on('click.dataTable-rowgrouptoggler', (e) => {
           var link = $(e.currentTarget),
           parentRow = link.closest('tr.p-rowgroup-header');

           if(link.attr('aria-expanded') === 'true') {
               link.attr('aria-expanded', 'false').attr('aria-label', this.getAriaLabel('expandLabel'));
               parentRow.nextUntil('tr.p-rowgroup-header').hide();
           }
           else {
               link.attr('aria-expanded', 'true').attr('aria-label', this.getAriaLabel('collapseLabel'));
               parentRow.nextUntil('tr.p-rowgroup-header').show();
           }

           e.preventDefault();
        });
        // set aria labels
        toggler.each((_i, el) => {
            this.updateExpansionAria($(el))
        });
    }

    /**
     * Computes the `colspan value for the table rows.
     * @param visibleOnly If true, only visible columns are considered.
     * @return The computed `colspan` value.
     */
    calculateColspan(visibleOnly: boolean = false): number {
        var headerSelector = '> tr:first th:not(.p-hidden):not(.p-grouped-column)';
        if (visibleOnly) {
            headerSelector += ':visible';
        }
        var visibleHeaderColumns = this.thead.find(headerSelector),
            colSpanValue = 0;

        for(var i = 0; i < visibleHeaderColumns.length; i++) {
            var column = visibleHeaderColumns.eq(i);
            if(column.is('[colspan]')) {
                colSpanValue += parseInt(column.attr('colspan')!);
            }
            else {
                colSpanValue++;
            }
        }

        return colSpanValue;
    }

    /**
     * Updates the `colspan` attribute of the given row.
     * @param row A row to update.
     * @param colspanValue The new `colspan` value. If not given, computes the value automatically.
     */
    updateColspan(row: JQuery, colspanValue?: number): void {
        row.children('td').attr('colspan', colspanValue || this.calculateColspan());
    }

    /**
     * Updates the colspan attribute for the message shown when no rows are available.
     */
    updateEmptyColspan(): void {
        var emptyRow = this.tbody.children('tr:first');
        if(emptyRow && emptyRow.hasClass('p-datatable-empty-message')) {
            let colspanValue = this.calculateColspan();
            // #13742 need to re-add the grouped column to fill out the empty row
            if (this.thead.find('> tr:first th.p-grouped-column').length > 0) {
                colspanValue = colspanValue + 1;
            }
            this.updateColspan(emptyRow, colspanValue);
        }
    }

    /**
     * Updates the `rowspan` attribute of the given row.
     * @param row A column to update.
     */
    updateRowspan(row: JQuery): void {
        if (this.cfg.groupColumnIndexes) {
            var isGroupedRow = row.hasClass('p-datatable-grouped-row');
            var groupedRow = isGroupedRow ? row : row.prevAll('.p-datatable-grouped-row:first');
            var groupedColumn = groupedRow.children('.p-grouped-column:first');
            var rowSpan = groupedRow.nextUntil('.p-datatable-grouped-row').not(':hidden').length + 1;
            // parseInt of the number 1 if there is no rowspan
            var diff = rowSpan - parseInt((groupedColumn.attr('rowspan') || 1) as string);
            groupedColumn.attr('rowspan', rowSpan);

            var groupedColumnIndex = groupedColumn.index();
            if (groupedColumnIndex > 0) {
                var columns = row.children(':visible');
                for (var i = 0; i < groupedColumnIndex; i++) {
                    var column = columns.eq(i);
                    if (column) {
                        column.attr('rowspan', parseInt((column.attr('rowspan') || 1) as string) + diff);
                    }
                }
            }
        }
    }

    /**
     * Updates the `colspan` attributes of all expanded rows.
     */
    updateExpandedRowsColspan(): void {
        var colspanValue = this.calculateColspan(true);
        this.getExpandedRows().each((_i, el) => {
            this.updateColspan($(el).next('.p-expanded-row-content'), colspanValue);
        });
    }

    /**
     * Computes and saves the resizable state of this DataTable, ie. which columns have got which width. May be used
     * later to restore the current column width after an AJAX update.
     * @param columnHeader Element of a column header of this DataTable.
     * @param nextColumnHeader Element of the column header next to the given column header.
     * @param table The element for this DataTable.
     * @param newWidth New width to be applied.
     * @param nextColumnWidth Width of the column next to the given column header.
     */
    updateResizableState(columnHeader: JQuery, nextColumnHeader: JQuery, table: JQuery, newWidth: number, nextColumnWidth: number | null): void {
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

        for(var i = 0; i < this.resizableState!.length; i++) {
            var state = this.resizableState![i]!;
            if(state.indexOf(currentColumnId!) === 0) {
                this.resizableState![i] = currentColumnState;
                currentColumnMatch = true;
            }
            else if(!expandMode && state.indexOf(nextColumnId!) === 0) {
                this.resizableState![i] = nextColumnState;
                nextColumnMatch = true;
            }
            else if(expandMode && state.indexOf(tableId) === 0) {
                this.resizableState![i] = tableState;
                tableMatch = true;
            }
        }

        if(!currentColumnMatch) {
            this.resizableState!.push(currentColumnState);
        }

        if(!expandMode && !nextColumnMatch) {
            this.resizableState!.push(nextColumnState);
        }

        if(expandMode && !tableMatch) {
            this.resizableState!.push(tableState);
        }

        this.resizableStateHolder!.val(this.resizableState!.join(','));
    }

    /**
     * Finds the saved width of the given column. The width of resizable columns may be saved to restore it after an
     * AJAX update.
     * @param id ID of a column
     * @return The saved width of the given column in pixels. `null` when the given column does not exist.
     */
    findColWidthInResizableState(id: string): string | null {
        for (var i = 0; i < this.resizableState!.length; i++) {
            var state = this.resizableState![i]!;
            if (state.indexOf(id) === 0) {
                return state.substring(state.lastIndexOf('_') + 1, state.length);
            }
        }

        return null;
    }

    /**
     * Updates some style classes for all columns.
     */
    updateColumnsView(): void {
        if(this.isEmpty()) {
            return;
        }

        // update the visibility of columns but ignore expanded rows and GitHub #7255 grouped headers
        if(this.headers && !this.hasColGroup()) {
            var rows = this.tbody.find('> tr:not(.p-expanded-row-content):not(.p-datatable-headerrow)');
            for(var i = 0; i < rows.length; i++) {
                var row = rows.eq(i);
                var columns = row.children();

                for(var j = 0; j < columns.length; j++) {
                    var column = columns.eq(j);
                    var columnIndex = column.data('ci') || j;

                    var header = this.headers.eq(columnIndex);
                    if(header.hasClass('p-hidden')) {
                        column.addClass('p-hidden');
                    }
                    else {
                        column.removeClass('p-hidden');
                    }
                }
            }
        }

        // update the colspan of the expanded rows
        if(this.cfg.expansion) {
            this.updateExpandedRowsColspan();
        }

        // reset the position and column widths if scrollable
        if(this.scrollBody) {
            // reset scroll position to top.
            this.scrollBody.scrollTop(0);

            // reset the column widths if the first row exists
            const firstRow = this.firstRow();
            if (firstRow.length && this.isAutoLayout()) {
                this.resetColumnsWidth();
            }
        }
    }

    /**
     * Resets the scroll state of the body to a non-scrolled state.
     */
    resetVirtualScrollBody(): void {
        this.bodyTable!.css('top', '0px');
        this.scrollBody!.scrollTop(0);
        this.clearScrollState();
    }

    /**
     * In case of a toggleable filter datatable toggle all filter input components
     * @param speed speed of fade animation. (see jquery fadeToggle method)
     * @param callback will be executed after animation is finished. (see jquery fadeToggle method)
     */
    toggleFilter(speed: string | null, callback: (() => void) | null): void {
        this.jq.find(".p-column-filter, .p-column-customfilter").fadeToggle(speed || 0, callback!);
    }

}
