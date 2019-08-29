/**
 * PrimeFaces DataTable Widget
 */
PrimeFaces.widget.DataTable = PrimeFaces.widget.DeferredWidget.extend({

    SORT_ORDER: {
        ASCENDING: 1,
        DESCENDING: -1,
        UNSORTED: 0
    },

    init: function(cfg) {
        this._super(cfg);

        this.thead = this.getThead();
        this.tbody = this.getTbody();
        this.tfoot = this.getTfoot();

        if(this.cfg.paginator) {
            this.bindPaginator();
        }

        this.bindSortEvents();

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

        if(this.cfg.multiViewState && this.cfg.resizableColumns) {
            this.resizableStateHolder = $(this.jqId + '_resizableColumnState');
            this.resizableState = [];

            if(this.resizableStateHolder.attr('value')) {
                this.resizableState = this.resizableStateHolder.val().split(',');
            }
        }

        this.updateEmptyColspan();
        this.renderDeferred();
    },

    _render: function() {
        this.isRTL = this.jq.hasClass('ui-datatable-rtl');
        
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
    },

    getThead: function() {
        return $(this.jqId + '_head');
    },

    getTbody: function() {
        return $(this.jqId + '_data');
    },

    getTfoot: function() {
        return $(this.jqId + '_foot');
    },

    updateData: function(data, clear) {
        var empty = (clear === undefined) ? true: clear;

        if(empty)
            this.tbody.html(data);
        else
            this.tbody.append(data);

        this.postUpdateData();
    },

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
    },

    //@override
    refresh: function(cfg) {
        this.columnWidthsFixed = false;

        this.unbindEvents();

        this._super(cfg);
    },

    /**
     * Unbinds events needed if refreshing to prevent multiple sort and pagination events.
     */
    unbindEvents: function() {
        if (this.sortableColumns) {
            this.sortableColumns.off();
        }
        if (this.paginator) {
            this.paginator.unbindEvents();
        }
    },

    /**
     * Binds the change event listener and renders the paginator
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
     */
    bindSortEvents: function() {
        var $this = this,
            hasAriaSort = false;
        this.cfg.tabindex = this.cfg.tabindex||'0';
        this.headers = this.thead.find('> tr > th');
        this.sortableColumns = this.headers.filter('.ui-sortable-column');
        this.sortableColumns.attr('tabindex', this.cfg.tabindex);

        //aria messages
        this.ascMessage = PrimeFaces.getAriaLabel('datatable.sort.ASC');
        this.descMessage = PrimeFaces.getAriaLabel('datatable.sort.DESC');

        //reflow dropdown
        this.reflowDD = $(this.jqId + '_reflowDD');

        if(this.cfg.multiSort) {
            this.sortMeta = [];
        }

        for(var i = 0; i < this.sortableColumns.length; i++) {
            var columnHeader = this.sortableColumns.eq(i),
            columnHeaderId = columnHeader.attr('id'),
            sortIcon = columnHeader.children('span.ui-sortable-column-icon'),
            sortOrder = null,
            resolvedSortMetaIndex = null,
            ariaLabel = columnHeader.attr('aria-label');

            if(columnHeader.hasClass('ui-state-active')) {
                if(sortIcon.hasClass('ui-icon-triangle-1-n')) {
                    sortOrder = this.SORT_ORDER.ASCENDING;
                    columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.descMessage));
                    if(!hasAriaSort) {
                        columnHeader.attr('aria-sort', 'ascending');
                        hasAriaSort = true;
                    }
                }
                else {
                    sortOrder = this.SORT_ORDER.DESCENDING;
                    columnHeader.attr('aria-label', this.getSortMessage(ariaLabel, this.ascMessage));
                    if(!hasAriaSort) {
                        columnHeader.attr('aria-sort', 'descending');
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

            if(!column.hasClass('ui-state-active'))
                column.addClass('ui-state-hover');
        })
        .on('mouseleave.dataTable', function() {
            var column = $(this);

            if(!column.hasClass('ui-state-active'))
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
            sortOrder = (sortOrderData === $this.SORT_ORDER.UNSORTED) ? $this.SORT_ORDER.ASCENDING : -1 * sortOrderData,
            metaKey = e.metaKey||e.ctrlKey||metaKeyOn;

            if($this.cfg.multiSort) {
                if(metaKey) {
                    $this.addSortMeta({
                        col: columnHeader.attr('id'),
                        order: sortOrder
                    });
                    $this.sort(columnHeader, sortOrder, true);
                }
                else {
                    $this.sortMeta = [];
                    $this.addSortMeta({
                        col: columnHeader.attr('id'),
                        order: sortOrder
                    });
                    $this.sort(columnHeader, sortOrder);
                }
            }
            else {
                $this.sort(columnHeader, sortOrder);
            }

            if($this.cfg.scrollable) {
                $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).trigger('focus');
            }

            $this.updateReflowDD(columnHeader, sortOrder);
        });

        if(this.reflowDD && this.cfg.reflow) {
            PrimeFaces.skinSelect(this.reflowDD);
            this.reflowDD.change(function(e) {
                var arrVal = $(this).val().split('_'),
                    columnHeader = $this.sortableColumns.eq(parseInt(arrVal[0])),
                    sortOrder = parseInt(arrVal[1]);

                    columnHeader.data('sortorder', sortOrder);
                    columnHeader.trigger('click.dataTable');
            });
        }
    },

    getSortMessage: function(ariaLabel, sortOrderMessage) {
        var headerName = ariaLabel ? ariaLabel.split(':')[0] : '';
        return headerName + ': ' + sortOrderMessage;
    },

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

    addSortMeta: function(meta) {
        this.sortMeta = $.grep(this.sortMeta, function(value) {
            return value.col !== meta.col;
        });

        this.sortMeta.push(meta);
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

            if((key === keyCode.ENTER)) {
                e.preventDefault();
            }
        }).on('keyup', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                $this.filter();

                e.preventDefault();
            }
        });
    },

    bindFilterEvent: function(filter) {
        var $this = this;

        //prevent form submit on enter key
        filter.on('keydown.dataTable-blockenter', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                e.preventDefault();
            }
        })
        .on(this.cfg.filterEvent + '.dataTable', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode,
            ignoredKeys = [keyCode.END, keyCode.HOME, keyCode.LEFT, keyCode.RIGHT, keyCode.UP, keyCode.DOWN,
                keyCode.TAB, 16/*Shift*/, 17/*Ctrl*/, 18/*Alt*/, 91, 92, 93/*left/right Win/Cmd*/,
                keyCode.ESCAPE, keyCode.PAGE_UP, keyCode.PAGE_DOWN,
                19/*pause/break*/, 20/*caps lock*/, 44/*print screen*/, 144/*num lock*/, 145/*scroll lock*/];

            if (ignoredKeys.indexOf(key) > -1) {
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
            filter.on('mouseup.dataTable', function(e) {
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

    setupRowHover: function() {
        var selector = '> tr.ui-widget-content';
        if(!this.cfg.selectionMode || this.cfg.selectionMode === 'checkbox') {
            this.bindRowHover(selector);
        }
    },

    setupSelection: function() {
        this.selectionHolder = this.jqId + '_selection';
        this.cfg.rowSelectMode = this.cfg.rowSelectMode||'new';
        this.rowSelector = '> tr.ui-widget-content.ui-datatable-selectable';
        this.cfg.disabledTextSelection = this.cfg.disabledTextSelection === false ? false : true;
        this.rowSelectorForRowClick = this.cfg.rowSelector||'td:not(.ui-column-unselectable),span:not(.ui-c)';

        var preselection = $(this.selectionHolder).val();
        this.selection = (preselection === "") ? [] : preselection.split(',');

        //shift key based range selection
        this.originRowIndex = null;
        this.cursorIndex = null;

        this.bindSelectionEvents();
    },

    /**
     * Applies events related to selection in a non-obstrusive way
     */
    bindSelectionEvents: function() {
        if(this.cfg.selectionMode === 'radio') {
            this.bindRadioEvents();
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
        if(this.hasBehavior('rowDblselect')) {
            this.tbody.off('dblclick.dataTable', this.rowSelector).on('dblclick.dataTable', this.rowSelector, null, function(e) {
                $this.onRowDblclick(e, $(this));
            });
        };

        this.bindSelectionKeyEvents();
    },

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

            if($(e.target).is(':input') && $this.cfg.editable) {
                return;
            }

            if($this.focusedRow) {
                switch(key) {
                    case keyCode.UP:
                    case keyCode.DOWN:
                        var rowSelector = 'tr.ui-widget-content.ui-datatable-selectable',
                        row = key === keyCode.UP ? $this.focusedRow.prev(rowSelector) : $this.focusedRow.next(rowSelector);

                        if(row.length) {
                            $this.unhighlightFocusedRow();

                            if($this.isCheckboxSelectionEnabled()) {
                                row.find('> td.ui-selection-column .ui-chkbox input').focus();
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
                            $this.focusedRow.find('> td.ui-selection-column .ui-chkbox .ui-chkbox-box').trigger('click.dataTable');
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

    highlightFocusedRow: function() {
        this.focusedRow.addClass('ui-state-hover');
    },

    unhighlightFocusedRow: function() {
        this.focusedRow.removeClass('ui-state-hover');
    },

    assignFocusedRow: function(row) {
        this.focusedRow = row;
    },

    bindRowHover: function(selector) {
        this.tbody.off('mouseenter.dataTable mouseleave.dataTable', selector)
                    .on('mouseenter.dataTable', selector, null, function() {
                        var element = $(this);

                        if(!element.hasClass('ui-state-highlight')) {
                            element.addClass('ui-state-hover');
                        }
                    })
                    .on('mouseleave.dataTable', selector, null, function() {
                        var element = $(this);

                        if(!element.hasClass('ui-state-highlight')) {
                            element.removeClass('ui-state-hover');
                        }
                    });
    },

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
            this.tbody.off('click.dataTable mouseover.dataTable mouseout.dataTable', radioSelector)
                .on('mouseover.dataTable', radioSelector, null, function() {
                    var radio = $(this);
                    if(!radio.hasClass('ui-state-disabled')&&!radio.hasClass('ui-state-active')) {
                        radio.addClass('ui-state-hover');
                    }
                })
                .on('mouseout.dataTable', radioSelector, null, function() {
                    var radio = $(this);
                    radio.removeClass('ui-state-hover');
                })
                .on('click.dataTable', radioSelector, null, function() {
                    var radio = $(this),
                    checked = radio.hasClass('ui-state-active'),
                    disabled = radio.hasClass('ui-state-disabled');

                    if(!disabled && !checked) {
                        $this.selectRowWithRadio(radio);
                    }
                });
        }

        //keyboard support
        this.tbody.off('focus.dataTable blur.dataTable change.dataTable', radioInputSelector)
            .on('focus.dataTable', radioInputSelector, null, function() {
                var input = $(this),
                box = input.parent().next();

                if(input.prop('checked')) {
                    box.removeClass('ui-state-active');
                }

                box.addClass('ui-state-focus');
            })
            .on('blur.dataTable', radioInputSelector, null, function() {
                var input = $(this),
                box = input.parent().next();

                if(input.prop('checked')) {
                    box.addClass('ui-state-active');
                }

                box.removeClass('ui-state-focus');
            })
            .on('change.dataTable', radioInputSelector, null, function() {
                var currentInput = $this.tbody.find(radioInputSelector).filter(':checked'),
                currentRadio = currentInput.parent().next();

                $this.selectRowWithRadio(currentRadio);
            });

    },

    bindCheckboxEvents: function() {
        var $this = this,
        checkboxInputSelector = '> tr.ui-widget-content.ui-datatable-selectable > td.ui-selection-column :checkbox';

        if(this.cfg.nativeElements) {
            this.checkAllToggler = this.thead.find('> tr > th.ui-selection-column > :checkbox');
            this.checkAllTogglerInput = this.checkAllToggler;

            this.checkAllToggler.on('click', function() {
                $this.toggleCheckAll();
            });

            this.tbody.off('click.dataTable', checkboxInputSelector).on('click.dataTable', checkboxInputSelector, null, function(e) {
                var checkbox = $(this);

                if(checkbox.prop('checked'))
                    $this.selectRowWithCheckbox(checkbox);
                else
                    $this.unselectRowWithCheckbox(checkbox);
            });
        }
        else {
            this.checkAllToggler = this.thead.find('> tr > th.ui-selection-column > .ui-chkbox.ui-chkbox-all > .ui-chkbox-box');
            this.checkAllTogglerInput = this.checkAllToggler.prev().children(':checkbox');

            this.checkAllToggler.on('mouseover', function() {
                var box = $(this);
                if(!box.hasClass('ui-state-disabled')&&!box.hasClass('ui-state-active')) {
                    box.addClass('ui-state-hover');
                }
            })
            .on('mouseout', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click', function() {
                var box = $(this);
                if(!box.hasClass('ui-state-disabled')) {
                    $this.toggleCheckAll();
                }
            });

            var checkboxSelector = '> tr.ui-widget-content.ui-datatable-selectable > td.ui-selection-column .ui-chkbox .ui-chkbox-box';
            this.tbody.off('mouseover.dataTable mouseover.dataTable click.dataTable', checkboxSelector)
                        .on('mouseover.dataTable', checkboxSelector, null, function() {
                            var box = $(this);
                            if(!box.hasClass('ui-state-active')) {
                                box.addClass('ui-state-hover');
                            }
                        })
                        .on('mouseout.dataTable', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover');
                        })
                        .on('click.dataTable', checkboxSelector, null, function() {
                            var checkbox = $(this),
                            input = checkbox.prev().children('input');

                            if(input.prop('checked'))
                                $this.unselectRowWithCheckbox(checkbox);
                            else
                                $this.selectRowWithCheckbox(checkbox);
                        });
        }

        //keyboard support
        this.tbody.off('focus.dataTable blur.dataTable change.dataTable', checkboxInputSelector)
                    .on('focus.dataTable', checkboxInputSelector, null, function() {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked')) {
                            box.removeClass('ui-state-active');
                        }

                        box.addClass('ui-state-focus');

                        $this.focusedRow = input.closest('.ui-datatable-selectable');
                        $this.focusedRowWithCheckbox = true;
                    })
                    .on('blur.dataTable', checkboxInputSelector, null, function() {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked')) {
                            box.addClass('ui-state-active');
                        }

                        box.removeClass('ui-state-focus');

                        $this.unhighlightFocusedRow();
                        $this.focusedRow = null;
                        $this.focusedRowWithCheckbox = false;
                    })
                    .on('change.dataTable', checkboxInputSelector, null, function(e) {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked')) {
                            $this.selectRowWithCheckbox(box);
                        }
                        else {
                            $this.unselectRowWithCheckbox(box);
                        }
                    });

        this.checkAllTogglerInput.on('focus.dataTable', function(e) {
                        var input = $(this),
                        box = input.parent().next();

                        if(!box.hasClass('ui-state-disabled')) {
                            if(input.prop('checked')) {
                                box.removeClass('ui-state-active');
                            }

                            box.addClass('ui-state-focus');
                        }
                    })
                    .on('blur.dataTable', function(e) {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked')) {
                            box.addClass('ui-state-active');
                        }

                        box.removeClass('ui-state-focus');
                    })
                    .on('change.dataTable', function(e) {
                        var input = $(this),
                        box = input.parent().next();

                        if(!box.hasClass('ui-state-disabled')) {
                            if(!input.prop('checked')) {
                                box.addClass('ui-state-active');
                            }

                            $this.toggleCheckAll();

                            if(input.prop('checked')) {
                                box.removeClass('ui-state-active').addClass('ui-state-focus');
                            }
                        }
                    });
    },

    toggleRow: function(row) {
        if(row && !this.isRowTogglerClicked) {
            var toggler = row.find('> td > div.ui-row-toggler');
            this.toggleExpansion(toggler);
        }
        this.isRowTogglerClicked = false;
    },

    /**
     * Applies events related to row expansion in a non-obstrusive way
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

    bindContextMenu : function(menuWidget, targetWidget, targetId, cfg) {
        var targetSelector = targetId + ' tbody.ui-datatable-data > tr.ui-widget-content';
        var targetEvent = cfg.event + '.datatable';
        this.contextMenuWidget = menuWidget;

        $(document).off(targetEvent, targetSelector).on(targetEvent, targetSelector, null, function(e) {
            var row = $(this);

            if(targetWidget.cfg.selectionMode && row.hasClass('ui-datatable-selectable')) {
                targetWidget.onRowRightClick(e, this, cfg.selectionMode);

                menuWidget.show(e);
            }
            else if(targetWidget.cfg.editMode === 'cell') {
                var target = $(e.target),
                cell = target.is('td.ui-editable-column') ? target : target.parents('td.ui-editable-column:first');

                if(targetWidget.contextMenuCell) {
                    targetWidget.contextMenuCell.removeClass('ui-state-highlight');
                }

                targetWidget.contextMenuClick = true;
                targetWidget.contextMenuCell = cell;
                targetWidget.contextMenuCell.addClass('ui-state-highlight');

                menuWidget.show(e);
            }
            else if(row.hasClass('ui-datatable-empty-message') && !$this.cfg.disableContextMenuIfEmpty) {
                menuWidget.show(e);
            }
        });

        if(this.cfg.scrollable && this.scrollBody) {
            var $this = this;
            this.scrollBody.off('scroll.dataTable-contextmenu').on('scroll.dataTable-contextmenu', function() {
                if($this.contextMenuWidget.jq.is(':visible')) {
                    $this.contextMenuWidget.hide();
                }
            });
        }
    },

    bindRowClick: function() {
        var $this = this,
        rowSelector = '> tr.ui-widget-content:not(.ui-expanded-row-content)';
        this.tbody.off('click.dataTable-rowclick', rowSelector).on('click.dataTable-rowclick', rowSelector, null, function(e) {
            var target = $(e.target),
            row = target.is('tr.ui-widget-content') ? target : target.closest('tr.ui-widget-content');

            $this.cfg.onRowClick.call(this, row);
        });
    },

    initReflow: function() {
        var headerColumns = this.thead.find('> tr > th');

        for(var i = 0; i < headerColumns.length; i++) {
            var headerColumn = headerColumns.eq(i),
            reflowHeaderText = headerColumn.find('.ui-reflow-headertext:first').text(),
            colTitleEl = headerColumn.children('.ui-column-title'),
            title = (reflowHeaderText && reflowHeaderText.length) ? reflowHeaderText : colTitleEl.text();
            this.tbody.find('> tr:not(.ui-datatable-empty-message) > td:nth-child(' + (i + 1) + ')').prepend('<span class="ui-column-title">' + PrimeFaces.escapeHTML(title) + '</span>');
        }
    },

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

        this.restoreScrollState();

        if(this.cfg.liveScroll) {
            this.scrollOffset = 0;
            this.cfg.liveScrollBuffer = (100 - this.cfg.liveScrollBuffer) / 100;
            this.shouldLiveScroll = true;
            this.loadingLiveScroll = false;
            this.allLoadedLiveScroll = $this.cfg.scrollStep >= $this.cfg.scrollLimit;
        }

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
                $this.scrollHeaderBox.css('margin-right', (scrollLeft - hScrollWidth + this.clientWidth));
                $this.scrollFooterBox.css('margin-right', (scrollLeft - hScrollWidth + this.clientWidth));
            }
            else {
                $this.scrollHeaderBox.css('margin-left', -scrollLeft);
                $this.scrollFooterBox.css('margin-left', -scrollLeft);
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

    shouldLoadLiveScroll: function() {
        return (!this.loadingLiveScroll && !this.allLoadedLiveScroll);
    },

    /**
     * Clones a table header and removes duplicate ids.
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

    cloneHead: function() {
        var $this = this;

        this.theadClone = this.cloneTableHeader(this.thead, this.bodyTable);

        //reflect events from clone to original
        if(this.sortableColumns.length) {
            this.sortableColumns.removeAttr('tabindex').off('blur.dataTable focus.dataTable keydown.dataTable');

            var clonedColumns = this.theadClone.find('> tr > th'),
            clonedSortableColumns = clonedColumns.filter('.ui-sortable-column');
            clonedColumns.each(function() {
                var col = $(this),
                originalId = col.attr('id').split('_clone')[0];
                if(col.hasClass('ui-sortable-column')) {
                    col.data('original', originalId);
                }

                $this.setOuterWidth($(PrimeFaces.escapeClientId(originalId)), col[0].style.width);
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
            this.scrollBody.css('max-height', height);
        }
        else {
            this.scrollBody.height(height);
        }
    },

    adjustScrollWidth: function() {
        var width = parseInt((this.jq.parent().innerWidth() * (parseInt(this.cfg.scrollWidth) / 100)));
        this.setScrollWidth(width);
    },

    setOuterWidth: function(element, width) {
        if (element.css('box-sizing') === 'border-box') { // Github issue: #5014
            var diff = element.outerWidth() - element.width();
            element.width(parseFloat(width) - diff);
        }
        else {
            element.width(width);
        }
    },

    setScrollWidth: function(width) {
        var $this = this;
        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), width);
        });
        this.scrollHeader.width(width);
        this.scrollBody.css('margin-right', 0).width(width);
        this.scrollFooter.width(width);
    },

    alignScrollBody: function() {
        var marginRight = this.hasVerticalOverflow() ? this.getScrollbarWidth() + 'px' : '0px';

        this.scrollHeaderBox.css('margin-right', marginRight);
        this.scrollFooterBox.css('margin-right', marginRight);
    },

    getScrollbarWidth: function() {
        if(!this.scrollbarWidth) {
            this.scrollbarWidth = PrimeFaces.env.browser.webkit ? '15' : PrimeFaces.calculateScrollbarWidth();
        }

        return this.scrollbarWidth;
    },

    hasVerticalOverflow: function() {
        return (this.cfg.scrollHeight && this.bodyTable.outerHeight() > this.scrollBody.outerHeight());
    },

    restoreScrollState: function() {
        var scrollState = this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        if (scrollValues[0] == '-1') {
            scrollValues[0] = this.scrollBody[0].scrollWidth;
        }
        
        this.scrollBody.scrollLeft(scrollValues[0]);
        this.scrollBody.scrollTop(scrollValues[1]);
    },

    saveScrollState: function() {
        var scrollState = this.scrollBody.scrollLeft() + ',' + this.scrollBody.scrollTop();

        this.scrollStateHolder.val(scrollState);
    },

    clearScrollState: function() {
        this.scrollStateHolder.val('0,0');
    },

    fixColumnWidths: function() {
        var $this = this;

        if(!this.columnWidthsFixed) {
            if(this.cfg.scrollable) {
                this.scrollHeader.find('> .ui-datatable-scrollable-header-box > table > thead > tr > th').each(function() {
                    var headerCol = $(this),
                    colIndex = headerCol.index(),
                    headerStyle = headerCol[0].style,
                    width = headerStyle.width||headerCol.width();

                    if($this.cfg.multiViewState && $this.resizableStateHolder && $this.resizableStateHolder.attr('value')) {
                        width = ($this.findColWidthInResizableState(headerCol.attr('id')) || width);
                    }

                    $this.setOuterWidth(headerCol, width);

                    if($this.footerCols.length > 0) {
                        var footerCol = $this.footerCols.eq(colIndex);
                        $this.setOuterWidth(footerCol, width);
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

    setColumnsWidth: function(columns) {
        if(columns.length) {
            var $this = this;

            columns.each(function() {
                var col = $(this),
                colStyle = col[0].style,
                width = colStyle.width||col.width();

                if($this.cfg.multiViewState && $this.resizableStateHolder && $this.resizableStateHolder.attr('value')) {
                    width = ($this.findColWidthInResizableState(col.attr('id')) || width);
                }

                col.width(width);
            });
        }
    },

    /**
     * Loads rows on-the-fly when scrolling live
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
            formId: this.cfg.formId,
            params: [{name: this.id + '_scrolling', value: true},
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

        PrimeFaces.ajax.Request.handle(options);
    },

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
            formId: this.cfg.formId,
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
     * Ajax pagination
     */
    paginate: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_pagination', value: true},
                    {name: this.id + '_first', value: newState.first},
                    {name: this.id + '_rows', value: newState.rows},
                    {name: this.id + '_skipChildren', value: true},
                    {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
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
            },
            oncomplete: function(xhr, status, args, data) {
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
     * Loads next page asynchronously to keep it at viewstate and Updates viewstate
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
     * Ajax sort
     */
    sort: function(columnHeader, order, multi) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [{name: this.id + '_sorting', value: true},
                     {name: this.id + '_skipChildren', value: true},
                     {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
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
            },
            oncomplete: function(xhr, status, args, data) {
                var paginator = $this.getPaginator();
                if(args && args.totalRecords) {
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

                    columnHeader.data('sortorder', order).removeClass('ui-state-hover').addClass('ui-state-active');
                    var sortIcon = columnHeader.find('.ui-sortable-column-icon'),
                    ariaLabel = columnHeader.attr('aria-label');

                    if(order === $this.SORT_ORDER.DESCENDING) {
                        sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');
                        columnHeader.attr('aria-sort', 'descending').attr('aria-label', $this.getSortMessage(ariaLabel, $this.ascMessage));
                        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).attr('aria-sort', 'descending').attr('aria-label', $this.getSortMessage(ariaLabel, $this.ascMessage));
                    } else if(order === $this.SORT_ORDER.ASCENDING) {
                        sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');
                        columnHeader.attr('aria-sort', 'ascending').attr('aria-label', $this.getSortMessage(ariaLabel, $this.descMessage));
                        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).attr('aria-sort', 'ascending').attr('aria-label', $this.getSortMessage(ariaLabel, $this.descMessage));
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
        };

        if(multi) {
            options.params.push({name: this.id + '_multiSorting', value: true});
            options.params.push({name: this.id + '_sortKey', value: $this.joinSortMetaOption('col')});
            options.params.push({name: this.id + '_sortDir', value: $this.joinSortMetaOption('order')});
        }
        else {
            options.params.push({name: this.id + '_sortKey', value: columnHeader.attr('id')});
            options.params.push({name: this.id + '_sortDir', value: order});
        }

        if(this.hasBehavior('sort')) {
            this.callBehavior('sort', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

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
            },
            oncomplete: function(xhr, status, args, data) {
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

                // reset index of shift selection on multiple mode
                $this.originRowIndex = null;
            }
        };

        if(this.hasBehavior('filter')) {
            this.callBehavior('filter', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

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
        }
    },

    onRowDblclick: function(event, row) {
        if(this.cfg.disabledTextSelection) {
            PrimeFaces.clearSelection();
        }

        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is('td,span:not(.ui-c)')) {
            var rowMeta = this.getRowMeta(row);

            this.fireRowSelectEvent(rowMeta.key, 'rowDblselect');
        }
    },

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
     * @param r {Row Index || Row Element}
     */
    findRow: function(r) {
        var row = r;

        if(PrimeFaces.isNumber(r)) {
            row = this.tbody.children('tr:eq(' + r + ')');
        }

        return row;
    },

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

    selectRow: function(r, silent) {
        var row = this.findRow(r);
        if(!row.hasClass('ui-datatable-selectable')) {
            return;
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

        this.addSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
        }
    },

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

        this.removeSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselect");
        }
    },

    /*
     * Highlights row as selected
     */
    highlightRow: function(row) {
        row.removeClass('ui-state-hover').addClass('ui-state-highlight').attr('aria-selected', true);
    },

    /*
     * Clears selected visuals
     */
    unhighlightRow: function(row) {
        row.removeClass('ui-state-highlight').attr('aria-selected', false);
    },

    /**
     * Sends a rowSelectEvent on server side to invoke a rowSelectListener if defined
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
     * Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if defined
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
     * Selects the corresping row of a radio based column selection
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

    selectAllRowsOnPage: function() {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            this.selectRow(row, true);
        }
    },

    unselectAllRowsOnPage: function() {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < rows.length; i++) {
            var row = rows.eq(i);
            this.unselectRow(row, true);
        }
    },

    selectAllRows: function() {
        this.selectAllRowsOnPage();
        this.selection = new Array('@all');
        this.writeSelections();
    },

    /**
     * Toggles all rows with checkbox
     */
    toggleCheckAll: function() {
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
                }
            });
        }
        else {
            var checkboxes = this.tbody.find('> tr.ui-datatable-selectable > td.ui-selection-column .ui-chkbox-box:visible'),
            checked = this.checkAllToggler.hasClass('ui-state-active'),
            $this = this;

            if(checked) {
                this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
                this.checkAllTogglerInput.prop('checked', false).attr('aria-checked', false);

                checkboxes.each(function() {
                    $this.unselectRowWithCheckbox($(this), true);
                });
            }
            else {
                this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
                this.checkAllTogglerInput.prop('checked', true).attr('aria-checked', true);

                checkboxes.each(function() {
                    $this.selectRowWithCheckbox($(this), true);
                });
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
    },

    selectCheckbox: function(checkbox) {
        if(!checkbox.hasClass('ui-state-focus')) {
            checkbox.addClass('ui-state-active');
        }
        checkbox.children('span.ui-chkbox-icon:first').removeClass('ui-icon-blank').addClass(' ui-icon-check');
        checkbox.prev().children('input').prop('checked', true).attr('aria-checked', true);
    },

    unselectCheckbox: function(checkbox) {
        checkbox.removeClass('ui-state-active');
        checkbox.children('span.ui-chkbox-icon:first').addClass('ui-icon-blank').removeClass('ui-icon-check');
        checkbox.prev().children('input').prop('checked', false).attr('aria-checked', false);
    },

    selectRadio: function(radio){
        radio.removeClass('ui-state-hover');
        if(!radio.hasClass('ui-state-focus')) {
            radio.addClass('ui-state-active');
        }
        radio.children('.ui-radiobutton-icon').addClass('ui-icon-bullet').removeClass('ui-icon-blank');
        radio.prev().children('input').prop('checked', true);
    },

    unselectRadio: function(radio){
        radio.removeClass('ui-state-active').children('.ui-radiobutton-icon').addClass('ui-icon-blank').removeClass('ui-icon-bullet');
        radio.prev().children('input').prop('checked', false);
    },

    /**
     * Expands a row to display detail content
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
            formId: this.cfg.formId,
            params: [{name: this.id + '_rowExpansion', value: true},
                     {name: this.id + '_expandedRowIndex', value: rowIndex},
                     {name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_skipChildren', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            if(content && $.trim(content).length) {
                                row.addClass('ui-expanded-row');
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

    displayExpandedRow: function(row, content) {
        row.after(content);
    },

    fireRowCollapseEvent: function(row) {
        var rowIndex = this.getRowMeta(row).index;

        if(this.hasBehavior('rowToggle')) {
            var ext = {
                params: [
                {
                    name: this.id + '_collapsedRowIndex',
                    value: rowIndex
                }
                ]
            };

            this.callBehavior('rowToggle', ext);
        }
    },

    collapseRow: function(row) {
        // #942: need to use "hide" instead of "remove" to avoid incorrect form mapping when a row is collapsed
        row.removeClass('ui-expanded-row').next('.ui-expanded-row-content').hide();
    },

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

    getExpandedRows: function() {
        return this.tbody.children('.ui-expanded-row');
    },

    /**
     * Binds editor events non-obstrusively
     */
    bindEditEvents: function() {
        var $this = this;
        this.cfg.cellSeparator = this.cfg.cellSeparator||' ';
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
        }
        else if(this.cfg.editMode === 'cell') {
            var cellSelector = '> tr > td.ui-editable-column',
            editEvent = (this.cfg.editInitEvent !== 'click') ? this.cfg.editInitEvent + '.datatable-cell click.datatable-cell' : 'click.datatable-cell';

            this.tbody.off(editEvent, cellSelector)
                        .on(editEvent, cellSelector, null, function(e) {
                            $this.incellClick = true;

                            var cell = $(this);
                            if(!cell.hasClass('ui-cell-editing') && e.type === $this.cfg.editInitEvent) {
                                $this.showCellEditor($(this));

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

                            if(!$this.incellClick && $this.currentCell && !$this.contextMenuClick && !$.datepicker._datepickerShowing) {
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

    switchToRowEdit: function(row) {
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

    showRowEditors: function(row) {
        row.addClass('ui-state-highlight ui-row-editing').children('td.ui-editable-column').each(function() {
            var column = $(this);

            column.find('.ui-cell-editor-output').hide();
            column.find('.ui-cell-editor-input').show();
        });
    },

    cellEditInit: function(cell) {
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
            inputs.eq(0).focus().select();

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
                    .on('focus.datatable-cell click.datatable-cell', function(e) {
                        $this.currentCell = cell;
                    });
            }
        }
        else {
            this.currentCell = null;
        }
    },

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
        rowIndex = this.getRowMeta(row).index,
        expanded = row.hasClass('ui-expanded-row'),
        $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
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

    /*
     * Updates row with given content
     */
    updateRow: function(row, content) {
        row.replaceWith(content);
    },

    /**
     * Displays row editors in invalid format
     */
    invalidateRow: function(index) {
        var i = (this.paginator) ? (index % this.paginator.getRows()) : index;
        this.tbody.children('tr[data-ri]').eq(i).addClass('ui-widget-content ui-row-editing ui-state-error');
    },

    /**
     * Finds all editors of a row
     */
    getRowEditors: function(row) {
        return row.find('div.ui-cell-editor');
    },

    /**
     * Returns the paginator instance if any defined
     */
    getPaginator: function() {
        return this.paginator;
    },

    /**
     * Writes selected row ids to state holder
     */
    writeSelections: function() {
        $(this.selectionHolder).val(this.selection.join(','));
    },

    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },

    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple' || this.isCheckboxSelectionEnabled();
    },

    /**
     * Clears the selection state
     */
    clearSelection: function() {
        this.selection = [];

        $(this.selectionHolder).val('');
    },

    /**
     * Returns true|false if selection is enabled|disabled
     */
    isSelectionEnabled: function() {
        return this.cfg.selectionMode != undefined || this.cfg.columnSelectionMode != undefined;
    },

    /**
     * Returns true|false if checkbox selection is enabled|disabled
     */
    isCheckboxSelectionEnabled: function() {
        return this.cfg.selectionMode === 'checkbox';
    },

    /**
     * Returns true|false if radio selection is enabled|disabled
     */
    isRadioSelectionEnabled: function() {
        return this.cfg.selectionMode === 'radio';
    },

    /**
     * Clears table filters
     */
    clearFilters: function() {
        this.thead.find('> tr > th.ui-filter-column > .ui-column-filter').val('');
        $(this.jqId + '\\:globalFilter').val('');

        this.filter();
    },

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

    hasColGroup: function() {
        return this.thead.children('tr').length > 1;
    },

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

            if(this.cfg.multiViewState && this.resizableStateHolder.attr('value')) {
                colWidth = (this.findColWidthInResizableState(id) || colWidth);
            }

            columnMarkup += '<th id="' + id + '" style="height:0px;border-bottom-width: 0px;border-top-width: 0px;padding-top: 0px;padding-bottom: 0px;outline: 0 none; width:' + colWidth + 'px" class="ui-resizable-column"></th>';
        }

        this.thead.prepend('<tr>' + columnMarkup + '</tr>');

        if(this.cfg.scrollable) {
            this.theadClone.prepend('<tr>' + columnMarkup + '</tr>');
            this.footerTable.children('tfoot').prepend('<tr>' + columnMarkup + '</tr>');
        }
    },

    findGroupResizer: function(ui) {
        for(var i = 0; i < this.groupResizers.length; i++) {
            var groupResizer = this.groupResizers.eq(i);
            if(groupResizer.offset().left === ui.helper.data('originalposition').left) {
                return groupResizer;
            }
        }

        return null;
    },

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
     * Remove given rowIndex from selection
     */
    removeSelection: function(rowIndex) {
        this.selection = $.grep(this.selection, function(value) {
            return value != rowIndex;
        });
    },

    /**
     * Adds given rowKey to selection if it doesn't exist already
     */
    addSelection: function(rowKey) {
        if(!this.isSelected(rowKey)) {
            this.selection.push(rowKey);
        }
    },

    /**
     * Finds if given rowKey is in selection
     */
    isSelected: function(rowKey) {
        return PrimeFaces.inArray(this.selection, rowKey);
    },

    getRowMeta: function(row) {
        var meta = {
            index: row.data('ri'),
            key:  row.attr('data-rk')
        };

        return meta;
    },

    setupDraggableColumns: function() {
        this.orderStateHolder = $(this.jqId + '_columnOrder');
        this.saveColumnOrder();

        this.dragIndicatorTop = $('<span class="ui-icon ui-icon-arrowthick-1-s" style="position:absolute"/></span>').hide().appendTo(this.jq);
        this.dragIndicatorBottom = $('<span class="ui-icon ui-icon-arrowthick-1-n" style="position:absolute"/></span>').hide().appendTo(this.jq);

        var $this = this;

        $(this.jqId + ' thead th').draggable({
            appendTo: 'body',
            opacity: 0.75,
            cursor: 'move',
            scope: this.id,
            cancel: ':input,.ui-column-resizer',
            start: function(event, ui) {
                ui.helper.css('z-index', ++PrimeFaces.zindex);
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
                    'left':0,
                    'top':0
                }).hide();

                $this.dragIndicatorBottom.css({
                    'left':0,
                    'top':0
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
    },

    saveColumnOrder: function() {
        var columnIds = [],
        columns = $(this.jqId + ' thead:first th');

        columns.each(function(i, item) {
            columnIds.push($(item).attr('id'));
        });

        this.orderStateHolder.val(columnIds.join(','));
    },

    makeRowsDraggable: function() {
        var $this = this,
        draggableHandle = this.cfg.rowDragSelector||'td,span:not(.ui-c)';

        this.tbody.sortable({
            placeholder: 'ui-datatable-rowordering ui-state-active',
            cursor: 'move',
            handle: draggableHandle,
            appendTo: document.body,
            start: function(event, ui) {
                ui.helper.css('z-index', ++PrimeFaces.zindex);
            },
            helper: function(event, ui) {
                var cells = ui.children(),
                helper = $('<div class="ui-datatable ui-widget"><table><tbody class="ui-datatable-data"></tbody></table></div>'),
                helperRow = ui.clone(),
                helperCells = helperRow.children();

                for(var i = 0; i < helperCells.length; i++) {
                    helperCells.eq(i).width(cells.eq(i).width());
                }

                helperRow.appendTo(helper.find('tbody'));

                return helper;
            },
            update: function(event, ui) {
                var fromIndex = ui.item.data('ri'),
                toIndex = $this.paginator ? $this.paginator.getFirst() + ui.item.index(): ui.item.index();

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
     * Returns if there is any data displayed
     */
    isEmpty: function() {
        return this.tbody.children('tr.ui-datatable-empty-message').length === 1;
    },

    getSelectedRowsCount: function() {
        return this.isSelectionEnabled() ? this.selection.length : 0;
    },

    updateHeaderCheckbox: function() {
        if(this.isEmpty()) {
            this.uncheckHeaderCheckbox();
            this.disableHeaderCheckbox();
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
                checkboxes = this.tbody.find('> tr > td.ui-selection-column .ui-chkbox-box');
                enabledCheckboxes = checkboxes.filter(':not(.ui-state-disabled)');
                disabledCheckboxes = checkboxes.filter('.ui-state-disabled');
                selectedCheckboxes = enabledCheckboxes.prev().children(':checked');
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

    checkHeaderCheckbox: function() {
        if(this.cfg.nativeElements) {
            this.checkAllToggler.prop('checked', true);
        }
        else {
            this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
            this.checkAllTogglerInput.prop('checked', true).attr('aria-checked', true);
        }
    },

    uncheckHeaderCheckbox: function() {
        if(this.cfg.nativeElements) {
            this.checkAllToggler.prop('checked', false);
        }
        else {
            this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            this.checkAllTogglerInput.prop('checked', false).attr('aria-checked', false);
        }
    },

    disableHeaderCheckbox: function() {
        if(this.cfg.nativeElements)
            this.checkAllToggler.prop('disabled', true);
        else
            this.checkAllToggler.addClass('ui-state-disabled');
    },

    enableHeaderCheckbox: function() {
        if(this.cfg.nativeElements)
            this.checkAllToggler.prop('disabled', false);
        else
            this.checkAllToggler.removeClass('ui-state-disabled');
    },

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
            width: table.outerWidth(),
            top: offset.top,
            left: offset.left,
            'z-index': ++PrimeFaces.zindex
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
                                        'top': fixedElementsHeight
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

        PrimeFaces.utils.registerResizeHandler(this, 'resize.sticky-' + this.id, null, function(e) {
            var _delay = e.data.delay;

            if (_delay !== null && typeof _delay === 'number' && _delay > -1) {
                if ($this.resizeTimeout) {
                    clearTimeout($this.resizeTimeout);
                }

                $this.stickyContainer.hide();
                $this.resizeTimeout = setTimeout(function() {
                    $this.stickyContainer.css('left', orginTableContent.offset().left);
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

    getFocusableTbody: function() {
        return this.tbody;
    },

    reclone: function() {
        this.clone.remove();
        this.clone = this.thead.clone(false);
        this.jq.find('.ui-datatable-tablewrapper > table').prepend(this.clone);
    },

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

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    clearCacheMap: function() {
        this.cacheMap = {};
    },

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

    updateReflowDD: function(columnHeader, sortOrder) {
        if(this.reflowDD && this.cfg.reflow) {
            var options = this.reflowDD.children('option'),
            orderIndex = sortOrder > 0 ? 0 : 1;

            options.filter(':selected').prop('selected', false);
            options.filter('[value="' + columnHeader.index() + '_' + orderIndex + '"]').prop('selected', true);
        }
    },

    groupRows: function() {
        var rows = this.tbody.children('tr');
        for(var i = 0; i < this.cfg.groupColumnIndexes.length; i++) {
            this.groupRow(this.cfg.groupColumnIndexes[i], rows);
        }

        rows.children('td.ui-duplicated-column').remove();
    },

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

    updateEmptyColspan: function() {
        var emptyRow = this.tbody.children('tr:first');
        if(emptyRow && emptyRow.hasClass('ui-datatable-empty-message')) {
            var visibleHeaderColumns = this.thead.find('> tr:first th:not(.ui-helper-hidden)'),
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

            emptyRow.children('td').attr('colspan', colSpanValue);
        }
    },

    updateResizableState: function(columnHeader, nextColumnHeader, table, newWidth, nextColumnWidth) {
        if(this.cfg.multiViewState) {
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
        }
    },

    findColWidthInResizableState: function(id) {
        for(var i = 0; i < this.resizableState.length; i++) {
            var state = this.resizableState[i];
            if(state.indexOf(id) === 0) {
                return state.substring(state.lastIndexOf('_') + 1, state.length);
            }
        }
    },

    updateColumnsView: function() {
        for(var i = 0; i < this.headers.length; i++) {
            var header = this.headers.eq(i),
            col = this.tbody.find('> tr > td:nth-child(' + (header.index() + 1) + ')');

            if(header.hasClass('ui-helper-hidden')) {
                col.addClass('ui-helper-hidden');
            }
            else {
                col.removeClass('ui-helper-hidden');
            }
        }
    },

    resetVirtualScrollBody: function() {
        this.bodyTable.css('top', '0px');
        this.scrollBody.scrollTop(0);
        this.clearScrollState();
    }

});

/**
 * PrimeFaces DataTable with Frozen Columns Widget
 */
PrimeFaces.widget.FrozenDataTable = PrimeFaces.widget.DataTable.extend({

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

        this.restoreScrollState();

        if(this.cfg.liveScroll) {
            this.scrollOffset = 0;
            this.cfg.liveScrollBuffer = (100 - this.cfg.liveScrollBuffer) / 100;
            this.shouldLiveScroll = true;
            this.loadingLiveScroll = false;
            this.allLoadedLiveScroll = $this.cfg.scrollStep >= $this.cfg.scrollLimit;
        }

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

        this.scrollBody.scroll(function() {
            var scrollLeft = $this.scrollBody.scrollLeft(),
            scrollTop = $this.scrollBody.scrollTop();
            
            if ($this.isRTL) {
                $this.scrollHeaderBox.css('margin-right', (scrollLeft - hScrollWidth + this.clientWidth));
                $this.scrollFooterBox.css('margin-right', (scrollLeft - hScrollWidth + this.clientWidth));
            }
            else {
                $this.scrollHeaderBox.css('margin-left', -scrollLeft);
                $this.scrollFooterBox.css('margin-left', -scrollLeft);
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

    cloneHead: function() {
        this.frozenTheadClone = this.cloneTableHeader(this.frozenThead, this.frozenBodyTable);
        this.scrollTheadClone = this.cloneTableHeader(this.scrollThead, this.scrollBodyTable);
    },

    hasVerticalOverflow: function() {
        return this.scrollBodyTable.outerHeight() > this.scrollBody.outerHeight();
    },

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
            this.scrollBody.css('max-height', height);
            this.frozenBody.css('max-height', height);
        }
        else {
            this.scrollBody.height(height);
            this.frozenBody.height(height);
        }
    },

    //@Override
    adjustScrollWidth: function() {
        var scrollLayoutWidth = this.jq.parent().innerWidth() - this.frozenLayout.innerWidth(),
        width = parseInt((scrollLayoutWidth * (parseInt(this.cfg.scrollWidth) / 100)));

        this.setScrollWidth(width);
    },

    setScrollWidth: function(width) {
        this.scrollHeader.width(width);
        this.scrollBody.css('margin-right', 0).width(width);
        this.scrollFooter.width(width);

        var $this = this,
        headerWidth = width + this.frozenLayout.width();

        this.jq.children('.ui-widget-header').each(function() {
            $this.setOuterWidth($(this), headerWidth);
        });
    },

    fixColumnWidths: function() {
        if(!this.columnWidthsFixed) {

            if(this.cfg.scrollable) {
                this._fixColumnWidths(this.scrollHeader, this.scrollFooterCols, this.scrollColgroup);
                this._fixColumnWidths(this.frozenHeader, this.frozenFooterCols, this.frozenColgroup);
            }
            else {
                this.jq.find('> .ui-datatable-tablewrapper > table > thead > tr > th').each(function() {
                    var col = $(this),
                    colStyle = col[0].style,
                    width = colStyle.width||col.width();
                    col.width(width);
                });
            }

            this.columnWidthsFixed = true;
        }
    },

    _fixColumnWidths: function(header, footerCols) {
        var $this = this;

        header.find('> .ui-datatable-scrollable-header-box > table > thead > tr > th').each(function() {
            var headerCol = $(this),
            colIndex = headerCol.index(),
            headerStyle = headerCol[0].style,
            width = headerStyle.width||headerCol.width();

            $this.setOuterWidth(headerCol, width);

            if(footerCols.length > 0) {
                var footerCol = footerCols.eq(colIndex);
                $this.setOuterWidth(footerCol, width);
            }
        });
    },

    //@Override
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

    copyRow: function(original) {
        return $('<tr></tr>').attr('data-ri', original.data('ri')).attr('data-rk', original.data('rk')).addClass(original.attr('class')).
                attr('role', 'row').attr('aria-selected', original.attr('aria-selected'));
    },

    getThead: function() {
        return $(this.jqId + '_frozenThead,' + this.jqId + '_scrollableThead');
    },

    getTbody: function() {
        return $(this.jqId + '_frozenTbody,' + this.jqId + '_scrollableTbody');
    },

    getTfoot: function() {
        return $(this.jqId + '_frozenTfoot,' + this.jqId + '_scrollableTfoot');
    },

    bindRowHover: function(selector) {
        var $this = this;

        this.tbody.off('mouseover.datatable mouseout.datatable', selector)
                    .on('mouseover.datatable', selector, null, function() {
                        var row = $(this),
                        twinRow = $this.getTwinRow(row);

                        if(!row.hasClass('ui-state-highlight')) {
                            row.addClass('ui-state-hover');
                            twinRow.addClass('ui-state-hover');
                        }
                    })
                    .on('mouseout.datatable', selector, null, function() {
                        var row = $(this),
                        twinRow = $this.getTwinRow(row);

                        if(!row.hasClass('ui-state-highlight')) {
                            row.removeClass('ui-state-hover');
                            twinRow.removeClass('ui-state-hover');
                        }
                    });
    },

    getTwinRow: function(row) {
        var twinTbody = (this.tbody.index(row.parent()) === 0) ? this.tbody.eq(1) : this.tbody.eq(0);

        return twinTbody.children().eq(row.index());
    },

    //@Override
    highlightRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },

    //@Override
    unhighlightRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },

    //@Override
    displayExpandedRow: function(row, content) {
        var twinRow = this.getTwinRow(row);
        row.after(content);
        var expansionRow = row.next();
        expansionRow.show();

        twinRow.after('<tr class="ui-expanded-row-content ui-widget-content"><td></td></tr>');
        twinRow.next().children('td').attr('colspan', twinRow.children('td').length).height(expansionRow.children('td').height());
    },

    //@Override
    collapseRow: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },

    //@Override
    getExpandedRows: function() {
        return this.frozenTbody.children('.ui-expanded-row');
    },

    //@Override
    showRowEditors: function(row) {
        this._super(row);
        this._super(this.getTwinRow(row));
    },

    //@Override
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

    //@Override
    invalidateRow: function(index) {
        this.frozenTbody.children('tr').eq(index).addClass('ui-widget-content ui-row-editing ui-state-error');
        this.scrollTbody.children('tr').eq(index).addClass('ui-widget-content ui-row-editing ui-state-error');
    },

    //@Override
    getRowEditors: function(row) {
        return row.find('div.ui-cell-editor').add(this.getTwinRow(row).find('div.ui-cell-editor'));
    },

    //@Override
    findGroupResizer: function(ui) {
        var resizer = this._findGroupResizer(ui, this.frozenGroupResizers);
        if(resizer) {
            return resizer;
        }
        else {
            return this._findGroupResizer(ui, this.scrollGroupResizers);
        }
    },

    _findGroupResizer: function(ui, resizers) {
        for(var i = 0; i < resizers.length; i++) {
            var groupResizer = resizers.eq(i);
            if(groupResizer.offset().left === ui.helper.data('originalposition').left) {
                return groupResizer;
            }
        }

        return null;
    },

    //@Override
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

    //@Override
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

    //@Override
    hasColGroup: function() {
        return this.frozenThead.children('tr').length > 1 || this.scrollThead.children('tr').length > 1;
    },

    //@Override
    addGhostRow: function() {
        this._addGhostRow(this.frozenTbody, this.frozenThead, this.frozenTheadClone, this.frozenFooter.find('table'), 'ui-frozen-column');
        this._addGhostRow(this.scrollTbody, this.scrollThead, this.scrollTheadClone, this.scrollFooterTable);
    },

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

    getFocusableTbody: function() {
        return this.tbody.eq(0);
    },

    highlightFocusedRow: function() {
        this._super();
        this.getTwinRow(this.focusedRow).addClass('ui-state-hover');
    },

    unhighlightFocusedRow: function() {
        this._super();
        this.getTwinRow(this.focusedRow).removeClass('ui-state-hover');
    },

    assignFocusedRow: function(row) {
        this._super(row);

        if(!row.parent().attr('tabindex')) {
            this.frozenTbody.trigger('focus');
        }
    },

    //@Override
    saveColumnOrder: function() {
        var columnIds = [],
        columns = $(this.jqId + '_frozenThead:first th,' + this.jqId + '_scrollableThead:first th');

        columns.each(function(i, item) {
            columnIds.push($(item).attr('id'));
        });

        this.orderStateHolder.val(columnIds.join(','));
    },

    //Override
    resetVirtualScrollBody: function() {
        this.scrollBodyTable.css('top', '0px');
        this.frozenBodyTable.css('top', '0px');
        this.scrollBody.scrollTop(0);
        this.frozenBody.scrollTop(0);
        this.clearScrollState();
    },

    //Override
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
