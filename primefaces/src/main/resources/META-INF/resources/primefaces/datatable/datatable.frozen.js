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
