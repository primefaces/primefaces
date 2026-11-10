import { DataTable, type DataTableCfg } from "./datatable.js";

/**
 * The configuration for the {@link FrozenDataTable} widget. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg | BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 */
export interface FrozenDataTableCfg extends DataTableCfg {
}

/**
 * __PrimeFaces DataTable with Frozen Columns Widget__
 */
export class FrozenDataTable<Cfg extends FrozenDataTableCfg = FrozenDataTableCfg> extends DataTable<Cfg> {
    /** The DOM element for the frozen body. */
    frozenBody?: JQuery;
    /** The DOM element for the frozen body TABLE. */
    frozenBodyTable?: JQuery;
    /** The DOM element for the container of the frozen table. */
    frozenContainer?: JQuery;
    /** The DOM element for the frozen footer. */
    frozenFooter?: JQuery;
    /** Always `undefined` and not used. */
    frozenColgroup?: undefined;
    /** The DOM elements for the frozen columns of the footer. */
    frozenFooterCols?: JQuery;
    /** The DOM element for the frozen data table footer TABLE. */
    frozenFooterTable?: JQuery;
    /** The DOM element for the frozen group resizers of the footer. */
    frozenGroupResizers?: JQuery;
    /** The DOM element for the frozen header. */
    frozenHeader?: JQuery;
    /** The DOM element for the frozen layout container. */
    frozenLayout?: JQuery;
    /** The DOM element for the header TBODY. */
    frozenTbody?: JQuery;
    /** The DOM element for the clone of the frozen THEAD. */
    frozenTheadClone?: JQuery;
    /** The DOM element for the header TFOOT. */
    frozenTfoot?: JQuery;
    /** The DOM element for the TABLE of the scrollable body. */
    scrollBodyTable?: JQuery;
    /** The DOM element for the container of the scrollable body. */
    scrollContainer?: JQuery;
    /** Always `undefined` and not used. */
    scrollColgroup?: undefined;
    /** The DOM element for the scrollable columns of the footer. */
    scrollFooterCols?: JQuery;
    /** The DOM element for the TABLE of the scrollable footer. */
    scrollFooterTable?: JQuery;
    /** The DOM element for the group resizers of the scrollable body. */
    scrollGroupResizers?: JQuery;
    /** The DOM element for the TABLE of the scrollable header. */
    scrollHeaderTable?: JQuery;
    /** The DOM element for the scrollable layout container. */
    scrollLayout?: JQuery;
    /** The DOM element for the scrollable THEAD. */
    scrollThead?: JQuery;
    /** The DOM element for the clone of the scrollable THEAD. */
    scrollTheadClone?: JQuery;
    /** The DOM element for the scrollable TFOOT. */
    scrollTfoot?: JQuery;

    override setupScrolling(): void {
        this.scrollLayout = this.jq.find('> table > tbody > tr > td.p-datatable-frozenlayout-right');
        this.frozenLayout = this.jq.find('> table > tbody > tr > td.p-datatable-frozenlayout-left');
        this.scrollContainer = this.jq.find('> table > tbody > tr > td.p-datatable-frozenlayout-right > .p-datatable-scrollable-container');
        this.frozenContainer = this.jq.find('> table > tbody > tr > td.p-datatable-frozenlayout-left > .p-datatable-frozen-container');
        this.scrollHeader = this.scrollContainer.children('.p-datatable-scrollable-header');
        this.scrollHeaderBox = this.scrollHeader.children('div.p-datatable-scrollable-header-box');
        this.scrollBody = this.scrollContainer.children('.p-datatable-scrollable-body');
        this.scrollFooter = this.scrollContainer.children('.p-datatable-scrollable-footer');
        this.scrollFooterBox = this.scrollFooter.children('div.p-datatable-scrollable-footer-box');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        this.scrollHeaderTable = this.scrollHeaderBox.children('table');
        this.scrollBodyTable = this.cfg.virtualScroll ? this.scrollBody.children('div').children('table') : this.scrollBody.children('table');
        var isFrozenColumnsRight = this.cfg.frozenColumnsAlignment === "right";
        this.scrollThead = this.thead.eq(isFrozenColumnsRight ? 0 : 1);
        this.scrollTbody = this.tbody.eq(isFrozenColumnsRight ? 0 : 1);
        this.scrollTfoot = this.tfoot.eq(isFrozenColumnsRight ? 0 : 1);
        this.scrollFooterTable = this.scrollFooterBox.children('table');
        this.scrollFooterCols = this.scrollFooter.find('> .p-datatable-scrollable-footer-box > table > tfoot > tr > td');
        this.frozenHeader = this.frozenContainer.children('.p-datatable-scrollable-header');
        this.frozenBody = this.frozenContainer.children('.p-datatable-scrollable-body');
        this.frozenBodyTable = this.cfg.virtualScroll ? this.frozenBody.children('div').children('table') : this.frozenBody.children('table');
        this.frozenThead = this.thead.eq(isFrozenColumnsRight ? 1 : 0);
        this.frozenTbody = this.tbody.eq(isFrozenColumnsRight ? 1 : 0);
        this.frozenTfoot = this.tfoot.eq(isFrozenColumnsRight ? 1 : 0);
        this.frozenFooter = this.frozenContainer.children('.p-datatable-scrollable-footer');
        this.frozenFooterTable = this.frozenFooter.find('> .p-datatable-scrollable-footer-box > table');
        this.frozenFooterCols = this.frozenFooter.find('> .p-datatable-scrollable-footer-box > table > tfoot > tr > td');
        this.percentageScrollHeight = this.cfg.scrollHeight && (this.cfg.scrollHeight.indexOf('%') !== -1);
        this.percentageScrollWidth = this.cfg.scrollWidth && (this.cfg.scrollWidth.indexOf('%') !== -1);

        this.frozenThead.find('> tr > th').addClass('p-frozen-column');

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

        if(this.cfg.selectionMode) {
            this.scrollTbody.removeAttr('tabindex');
        }

        if (!this.cfg.reflow) {
            this.fixColumnWidths();
        }

        if(this.cfg.scrollWidth) {
            if(this.percentageScrollWidth)
                this.adjustScrollWidth();
            else
                this.setScrollWidth(parseInt(this.cfg.scrollWidth));

            if(this.hasVerticalOverflow()) {
                this.frozenBody.append('<div style="height:' + scrollBarWidth + ';border:1px solid transparent"></div>');
            }
        }

        this.cloneHead();
        this.fixRowHeightsAll();

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
            var row = this.scrollTbody.children('tr.p-widget-content');
            if(row) {
                this.rowHeight = row.outerHeight();
                this.scrollBody.children('div').css('height', parseFloat((this.cfg.scrollLimit! * this.rowHeight!) + 'px'));
                this.frozenBody.children('div').css('height', parseFloat((this.cfg.scrollLimit! * this.rowHeight!) + 'px'));
            }

            if(!this.cfg.scrollHeight) {
                this.frozenBody.css('height', this.scrollBody.height()!);
            }
        }

        this.scrollBody.on('scroll.datatable', (e) => {
            var scrollShift = this.scrollBody!.scrollLeft()!;

            if (this.isRTL) {
                this.scrollHeaderBox!.css('margin-right', scrollShift + 'px');
                this.scrollFooterBox!.css('margin-right', scrollShift + 'px');
            }
            else {
                this.scrollHeaderBox!.css('margin-left', -scrollShift + 'px');
                this.scrollFooterBox!.css('margin-left', -scrollShift + 'px');
            }

            this.frozenBody!.scrollTop(this.scrollBody!.scrollTop()!);

            if(this.cfg.virtualScroll) {
                var virtualScrollBody = e.currentTarget as HTMLElement;

                clearTimeout(this.scrollTimeout);
                this.scrollTimeout = setTimeout(() => {
                    var viewportHeight = this.scrollBody!.outerHeight()!,
                    tableHeight = this.scrollBodyTable!.outerHeight()!,
                    pageHeight = this.rowHeight! * this.cfg.scrollStep!,
                    virtualTableHeight = parseFloat((this.cfg.scrollLimit! * this.rowHeight!) + 'px'),
                    pageCount = (virtualTableHeight / pageHeight)||1;

                    if(virtualScrollBody.scrollTop + viewportHeight > parseFloat(this.scrollBodyTable!.css('top')) + tableHeight || virtualScrollBody.scrollTop < parseFloat(this.scrollBodyTable!.css('top'))) {
                        var page = Math.floor((virtualScrollBody.scrollTop * pageCount) / (virtualScrollBody.scrollHeight)) + 1;
                        this.loadRowsWithVirtualScroll(page, () => {
                            this.scrollBodyTable!.css('top', ((page - 1) * pageHeight) + 'px');
                            this.frozenBodyTable!.css('top', ((page - 1) * pageHeight) + 'px');
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

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', this.jq, () => {
            if (this.resizeTimeout) {
                clearTimeout(this.resizeTimeout);
            }
            this.resizeTimeout = setTimeout(() => {
                this.fixRowHeightsAll();
                if (this.percentageScrollHeight) {
                    this.adjustScrollHeight();
                }
                if (this.percentageScrollWidth) {
                    this.adjustScrollWidth();
                }
            }, 150);
        });
    }

    override cloneHead(): void {
        if (this.frozenTheadClone) {
            this.frozenTheadClone.remove();
        }
        this.frozenTheadClone = this.cloneTableHeader(this.frozenThead!, this.frozenBodyTable!);

        if (this.scrollTheadClone) {
            this.scrollTheadClone.remove();
        }
        this.scrollTheadClone = this.cloneTableHeader(this.scrollThead!, this.scrollBodyTable!);
    }

    override hasVerticalOverflow(): boolean {
        return this.scrollBodyTable!.outerHeight()! > this.scrollBody!.outerHeight()!;
    }

    override adjustScrollHeight(): void {
        var relativeHeight = this.jq.parent().innerHeight()! * (parseInt(this.cfg.scrollHeight!) / 100),
        headerChilden = this.jq.children('.p-datatable-header'),
        footerChilden = this.jq.children('.p-datatable-footer'),
        tableHeaderHeight = (headerChilden.length > 0) ? headerChilden.outerHeight(true)! : 0,
        tableFooterHeight = (footerChilden.length > 0) ? footerChilden.outerHeight(true)! : 0,
        scrollersHeight = (this.scrollHeader!.innerHeight()! + this.scrollFooter!.innerHeight()!),
        paginatorsHeight: number = this.paginator ? this.paginator.getContainerHeight(true) : 0,
        height = (relativeHeight - (scrollersHeight + paginatorsHeight + tableHeaderHeight + tableFooterHeight));

        if(this.cfg.virtualScroll) {
            this.scrollBody!.css('max-height', height + 'px');
            this.frozenBody!.css('max-height', height + 'px');
        }
        else {
            this.scrollBody!.height(height);
            this.frozenBody!.height(height);
        }
    }

    override adjustScrollWidth(): void {
        var scrollLayoutWidth = this.jq.parent().innerWidth()! - this.frozenLayout!.innerWidth()!,
        // parseInt of a number, String() is what parseInt does implicitly
        width = parseInt(String(scrollLayoutWidth * (parseInt(this.cfg.scrollWidth!) / 100)));

        this.setScrollWidth(width);
    }

    override setScrollWidth(width: number): void {
        this.scrollHeader!.width(width);
        this.scrollBody!.css('margin-right', '0px').width(width);
        this.scrollFooter!.width(width);

        var headerWidth = width + this.frozenLayout!.width()!;

        this.jq.children('.p-widget-header').each((_i, el) => {
            this.setOuterWidth($(el), headerWidth);
        });
    }

    override fixColumnWidths(): void {
        if(!this.columnWidthsFixed) {

            if(this.cfg.scrollable) {
                // the third argument scrollColgroup / frozenColgroup was always undefined and not a parameter
                this._fixColumnWidths(this.scrollHeader!, this.scrollFooterCols!);
                this._fixColumnWidths(this.frozenHeader!, this.frozenFooterCols!);
            }
            else {
                this.jq.find('> .p-datatable-tablewrapper > table > thead > tr > th').each((_i, el) => {
                    var col = $(el),
                    widthInfo = this.getColumnWidthInfo(col);

                    this.applyWidthInfo(col, widthInfo);
                });
            }

            this.columnWidthsFixed = true;
        }
    }

    /**
     * Adjusts the width of the given columns to fit the current settings.
     * @param header Header of this data table.
     * @param footerCols The columns to adjust.
     */
    _fixColumnWidths(header: JQuery, footerCols: JQuery): void {
        header.find('> .p-datatable-scrollable-header-box > table > thead > tr > th').each((_i, el) => {
            var headerCol = $(el),
            colIndex = headerCol.index(),
            widthInfo = this.getColumnWidthInfo(headerCol);

            this.applyWidthInfo(headerCol, widthInfo);

            if(footerCols.length > 0) {
                var footerCol = footerCols.eq(colIndex);
                this.applyWidthInfo(footerCol, widthInfo);
            }
        });
    }

    override updateData(data: string, clear?: boolean): void {
        var table = $('<table><tbody>' + data + '</tbody></table>'),
        rows = table.find('> tbody > tr'),
        empty = (clear === undefined) ? true: clear;

        if(empty) {
            this.frozenTbody!.children().remove();
            this.scrollTbody!.children().remove();
        }

        //find slice index by checking how many rendered columns there are in frozen part
        var firstRow = this.frozenTbody!.children('tr:first'),
        frozenColumnCount = firstRow.length ? firstRow.children().length: this.cfg.frozenColumns;

        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i),
            columns = row.children(),
            frozenRow = this.copyRow(row),
            scrollableRow = this.copyRow(row);

            if(row.hasClass('p-datatable-empty-message')) {
                var colspan = columns.attr('colspan'),
                cloneColumns = columns.clone();

                frozenRow.append(columns.attr('colspan', this.cfg.frozenColumns!));
                // Number() is what the "-" operator does implicitly with the string
                scrollableRow.append(cloneColumns.attr('colspan', (Number(colspan) - this.cfg.frozenColumns!)));
            }
            else {
                frozenRow.append(columns.slice(0, frozenColumnCount!));
                scrollableRow.append(columns.slice(frozenColumnCount!));
            }

            this.frozenTbody!.append(frozenRow);
            this.scrollTbody!.append(scrollableRow);
        }

        this.postUpdateData();
        this.fixRowHeightsAll();
    }

    /**
     * Clones the given row and returns it
     * @param original DOM element of the original row.
     * @return The cloned row.
     */
    copyRow(original: JQuery): JQuery {
        return $('<tr></tr>').attr('data-ri', original.data('ri')).attr('data-rk', original.data('rk')).addClass(original.attr('class')!).
                attr('role', 'row').attr('aria-selected', original.attr('aria-selected')!);
    }

    override getThead(): JQuery {
        return $(this.jqId + '_frozenThead,' + this.jqId + '_scrollableThead');
    }

    override getTbody(): JQuery {
        return $(this.jqId + '_frozenTbody,' + this.jqId + '_scrollableTbody');
    }

    override getTfoot(): JQuery {
        return $(this.jqId + '_frozenTfoot,' + this.jqId + '_scrollableTfoot');
    }

    override bindRowHover(selector: string): void {
        this.tbody.off('mouseenter.datatable mouseleave.datatable', selector)
                    .on('mouseenter.datatable', selector, null, (e) => {
                        var row = $(e.currentTarget),
                        twinRow = this.getTwinRow(row);

                        row.addClass('p-state-hover');
                        twinRow.addClass('p-state-hover');
                    })
                    .on('mouseleave.datatable', selector, null, (e) => {
                        var row = $(e.currentTarget),
                        twinRow = this.getTwinRow(row);

                        row.removeClass('p-state-hover');
                        twinRow.removeClass('p-state-hover');
                    });
    }

    /**
     * Finds the twin row of the given row. The data table body has got two sets of rows.
     * @param row Row for which to find the twin
     * @return DOM element of the twin row.
     */
    getTwinRow(row: JQuery): JQuery {
        var twinTbody = (this.tbody.index(row.parent()) === 0) ? this.tbody.eq(1) : this.tbody.eq(0);

        return twinTbody.children().eq(row.index());
    }

    override highlightRow(row: JQuery): void {
        super.highlightRow(row);
        super.highlightRow(this.getTwinRow(row));
    }

    override unhighlightRow(row: JQuery): void {
        super.unhighlightRow(row);
        super.unhighlightRow(this.getTwinRow(row));
    }

    override displayExpandedRow(row: JQuery, content: string): void {
        var twinRow = this.getTwinRow(row);
        row.after(content);
        this.updateRowspan(row);
        var expansionRow = row.next();
        this.updateColspan(expansionRow);
        expansionRow.show();

        twinRow.after('<tr class="p-expanded-row-content p-widget-content"><td></td></tr>');
        // was ".attr('colspan', this.updateColspan(twinRow))" in the chain: updateColspan returns nothing, and jQuery's
        // attr(name, undefined) does not change anything, so only the call of updateColspan remains (same order)
        var twinExpansionCells = twinRow.next().children('td');
        this.updateColspan(twinRow);
        twinExpansionCells.height(expansionRow.children('td').height()!);
    }

    override collapseRow(row: JQuery): void {
        super.collapseRow(row);
        super.collapseRow(this.getTwinRow(row));
    }

    override getExpandedRows(): JQuery {
        return this.frozenTbody!.children('.p-expanded-row');
    }

    override showRowEditors(row: JQuery): void {
        super.showRowEditors(row);
        super.showRowEditors(this.getTwinRow(row));
    }

    override updateRow(row: JQuery, content: string): void {
        var table = $('<table><tbody>' + content + '</tbody></table>'),
        newRow = table.find('> tbody > tr'),
        columns = newRow.children(),
        frozenRow = this.copyRow(newRow),
        scrollableRow = this.copyRow(newRow),
        twinRow = this.getTwinRow(row);

        frozenRow.append(columns.slice(0, this.cfg.frozenColumns!));
        scrollableRow.append(columns.slice(this.cfg.frozenColumns!));

        row.replaceWith(frozenRow);
        twinRow.replaceWith(scrollableRow);
    }

    override invalidateRow(index: number): void {
        this.frozenTbody!.children('tr').eq(index).addClass('p-widget-content p-row-editing p-state-error');
        this.scrollTbody!.children('tr').eq(index).addClass('p-widget-content p-row-editing p-state-error');
    }

    override getRowEditors(row: JQuery): JQuery {
        return row.find('div.p-cell-editor').add(this.getTwinRow(row).find('div.p-cell-editor'));
    }

    override findGroupResizer(ui: PrimeFacesTypes.interactions.DraggableUi): JQuery | null {
        var resizer = this._findGroupResizer(ui, this.frozenGroupResizers!);
        if(resizer) {
            return resizer;
        }
        else {
            return this._findGroupResizer(ui, this.scrollGroupResizers!);
        }
    }

    /**
     * Finds the resizer DOM element that matches the given draggable event params.
     * @param ui Data of the drag event.
     * @param resizers List of all available resizers.
     * @return DOM element of the resizer.
     */
    _findGroupResizer(ui: PrimeFacesTypes.interactions.DraggableUi, resizers: JQuery): JQuery | null {
        for(var i = 0; i < resizers.length; i++) {
            var groupResizer = resizers.eq(i);
            if(groupResizer.offset()!.left === ui.helper.data('originalposition').left) {
                return groupResizer;
            }
        }

        return null;
    }

    override addResizers(): void {
        var frozenColumns = this.frozenThead!.find('> tr > th.p-resizable-column'),
        scrollableColumns = this.scrollThead!.find('> tr > th.p-resizable-column');

        frozenColumns.prepend('<span class="p-column-resizer">&nbsp;</span>');
        scrollableColumns.prepend('<span class="p-column-resizer">&nbsp;</span>')

        if(this.cfg.resizeMode === 'fit') {
            frozenColumns.filter(':last-child').addClass('p-frozen-column-last');
            scrollableColumns.filter(':last-child').children('span.p-column-resizer').hide();
        }

        if(this.hasColumnGroup) {
            this.frozenGroupResizers = this.frozenThead!.find('> tr:first > th > .p-column-resizer');
            this.scrollGroupResizers = this.scrollThead!.find('> tr:first > th > .p-column-resizer');
        }
    }

    override resize(event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DraggableUi): void {
        var columnHeader: JQuery | null = null,
        change: number | null = null,
        newWidth: number | null = null,
        nextColumnWidth: number | null = null,
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
        lastFrozen = columnHeader.hasClass('p-frozen-column-last');

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
        var shouldChange = (expandMode && newWidth > minWidth) || (lastFrozen ? (newWidth > minWidth) : (newWidth > minWidth && nextColumnWidth > minWidth));
        if(shouldChange) {
            var frozenColumn = columnHeader.hasClass('p-frozen-column'),
            theadClone = frozenColumn ? this.frozenTheadClone! : this.scrollTheadClone!,
            originalTable = frozenColumn ? this.frozenThead!.parent() : this.scrollThead!.parent(),
            cloneTable = theadClone.parent(),
            footerCols = frozenColumn ? this.frozenFooterCols! : this.scrollFooterCols!,
            footerTable = frozenColumn ? this.frozenFooterTable!:  this.scrollFooterTable!;

            if(expandMode) {
                if(lastFrozen) {
                    this.frozenLayout!.width(this.frozenLayout!.width()! + change);
                }

                var originalTableWidth = originalTable.width()!,
                cloneTableWidth = cloneTable.width()!,
                footerTableWidth = footerTable.width()!;

                //header
                originalTable.width(originalTableWidth + change);

                //body
                cloneTable.width(cloneTableWidth + change);

                //footer
                footerTable.width(footerTableWidth + change);

                PrimeFaces.queueTask(() => {
                    columnHeader!.width(newWidth!);

                    if(this.hasColumnGroup) {
                        theadClone.find('> tr:first').children('th').eq(colIndex).width(newWidth!);                          //body
                        footerTable.find('> tfoot > tr:first').children('th').eq(colIndex).width(newWidth!);                 //footer
                    }
                    else {
                        theadClone.find(PrimeFaces.escapeClientId(columnHeader!.attr('id') + '_clone')).width(newWidth!);     //body
                        footerCols.eq(colIndex).width(newWidth!);                                                            //footer
                    }
                });


            }
            else {
                if(lastFrozen) {
                    this.frozenLayout!.width(this.frozenLayout!.width()! + change);
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
    }

    override hasColGroup(): boolean {
        return this.frozenThead!.children('tr').length > 1 || this.scrollThead!.children('tr').length > 1;
    }

    override addGhostRow(): void {
        this._addGhostRow(this.frozenTbody!, this.frozenThead!, this.frozenTheadClone!, this.frozenFooter!.find('table'), 'p-frozen-column');
        this._addGhostRow(this.scrollTbody!, this.scrollThead!, this.scrollTheadClone!, this.scrollFooterTable!);
    }

    /**
     * Adds an invisible row for internal purposes.
     * @param body Body of this data table.
     * @param header Header of this data table.
     * @param headerClone Cloned header of this data table, see method `cloneHead`.
     * @param footerTable Footer of this data table.
     * @param columnClass Optional CSS class for the ghost columns.
     */
    _addGhostRow(body: JQuery, header: JQuery, headerClone: JQuery, footerTable: JQuery, columnClass?: string): void {
        var dataColumns = body.find('tr:first').children(),
        dataColumnsCount = dataColumns.length,
        columnMarkup = '',
        columnStyleClass = columnClass ? 'p-resizable-column ' + columnClass : 'p-resizable-column';

        for(var i = 0; i < dataColumnsCount; i++) {
            columnMarkup += '<th style="height:0px;border-bottom-width: 0px;border-top-width: 0px;padding-top: 0px;padding-bottom: 0px;outline: 0 none;width:' + dataColumns.eq(i).width() + 'px" class="' + columnStyleClass + '"></th>';
        }

        header.prepend('<tr>' + columnMarkup + '</tr>');

        if(this.cfg.scrollable) {
            headerClone.prepend('<tr>' + columnMarkup + '</tr>');
            footerTable.children('tfoot').prepend('<tr>' + columnMarkup + '</tr>');
        }
    }

    override getFocusableTbody(): JQuery {
        return this.tbody.eq(0);
    }

    override highlightFocusedRow(): void {
        super.highlightFocusedRow();
        this.getTwinRow(this.focusedRow!).addClass('p-state-hover');
    }

    override unhighlightFocusedRow(): void {
        super.unhighlightFocusedRow();
        this.getTwinRow(this.focusedRow!).removeClass('p-state-hover');
    }

    override assignFocusedRow(row: JQuery): void {
        super.assignFocusedRow(row);

        if(!row.parent().attr('tabindex')) {
            this.frozenTbody!.trigger('focus');
        }
    }

    override saveColumnOrder(): void {
        var columnIds: (string | undefined)[] = [],
        columns = $(this.jqId + '_frozenThead:first th,' + this.jqId + '_scrollableThead:first th');

        columns.each(function(i, item) {
            columnIds.push($(item).attr('id'));
        });

        this.orderStateHolder!.val(columnIds.join(','));
    }

    override resetVirtualScrollBody(): void {
        this.scrollBodyTable!.css('top', '0px');
        this.frozenBodyTable!.css('top', '0px');
        this.scrollBody!.scrollTop(0);
        this.frozenBody!.scrollTop(0);
        this.clearScrollState();
    }

    override groupRows(): void {
        var scrollRows = this.scrollTbody!.children('tr'),
        frozenRows = this.frozenTbody!.children('tr');

        for (var i = 0; i < this.cfg.groupColumnIndexes!.length; i++) {
            var groupColumnIndex = this.cfg.groupColumnIndexes![i]!;

            if (groupColumnIndex >= this.cfg.frozenColumns!) {
                this.groupRow(groupColumnIndex - this.cfg.frozenColumns!, scrollRows);
            }
            else {
                this.groupRow(groupColumnIndex, frozenRows);
            }
        }

        scrollRows.children('td.p-duplicated-column').remove();
        frozenRows.children('td.p-duplicated-column').remove();
    }

    /**
     * Adjusts the height of all rows to fit the current settings.
     */
    fixRowHeightsAll(): void {
        this.fixRowHeights(this.scrollThead!.children(), this.frozenThead!.children());
        this.fixRowHeights(this.scrollTbody!.children(), this.frozenTbody!.children());
        var frozenFootRows = this.frozenTfoot!.children();
        if (frozenFootRows.length > 0) {
            this.fixRowHeights(this.scrollTfoot!.children(), frozenFootRows);
            var scrollBarHeight = this.scrollContainer!.height()! - this.frozenContainer!.height()!;
            if (scrollBarHeight > 0) {
                this.frozenBody!.append('<div style="height:' + scrollBarHeight + 'px"></div>');
            }
        }
    }

    /**
     * Adjusts the height of the given rows to fit the current settings.
     * @param scrollRows The scrollable rows to adjust.
     * @param frozenRows The frozen rows to adjust.
     */
    fixRowHeights(scrollRows: JQuery, frozenRows: JQuery): void {
        frozenRows.each((index, el) => {
            var frozenRow = $(el);
            var scrollRow = scrollRows.eq(index);

            frozenRow.css('height', '');
            scrollRow.css('height', '');

            var scrollRowHeight = scrollRow.innerHeight()!;
            var frozenRowHeight = frozenRow.innerHeight()!;

            if (scrollRowHeight === frozenRowHeight) {
                return;
            }
            var height = scrollRowHeight > frozenRowHeight ? scrollRowHeight : frozenRowHeight;
            // compensation for decimal fractions
            height += 1;

            frozenRow.innerHeight(height);
            scrollRow.innerHeight(height);
        });
    }

}
