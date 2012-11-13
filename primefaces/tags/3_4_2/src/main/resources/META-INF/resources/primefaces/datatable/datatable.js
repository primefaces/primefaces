/**
 * PrimeFaces DataTable Widget
 */
PrimeFaces.widget.DataTable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.tbody = $(this.jqId + '_data');
        this.cfg.formId = this.jq.parents('form:first').attr('id');

        //Paginator
        if(this.cfg.paginator) {
            this.setupPaginator();
        }

        //Sort events
        this.setupSortEvents();

        //Selection events
        if(this.cfg.selectionMode || this.cfg.columnSelectionMode) {
            this.selectionHolder = this.jqId + '_selection';

            var preselection = $(this.selectionHolder).val();
            this.selection = preselection == "" ? [] : preselection.split(',');
            
            //shift key based range selection
            this.originRowIndex = 0;
            this.cursorIndex = null;

            this.bindSelectionEvents();
        }

        //Filtering
        if(this.cfg.filter) {
            this.setupFiltering();
        }

        if(this.cfg.expansion) {
            this.expansionProcess = [];
            this.setupExpansionEvents();
        }

        if(this.cfg.editable) {
            this.setupCellEditorEvents();
        }

        if(this.cfg.scrollable) {
            this.setupScrolling();
        }

        if(this.cfg.resizableColumns) {
            this.setupResizableColumns();
        }

        if(this.cfg.draggableColumns) {
            this.setupDraggableColumns();
        }
    },
    
    /**
     * @Override
     */
    refresh: function(cfg) {
        //remove arrows
        if(cfg.draggableColumns) {
            var jqId = PrimeFaces.escapeClientId(cfg.id);
            $(jqId + '_dnd_top,' + jqId + '_dnd_bottom').remove();
        }
        
        this.init(cfg);
    },
    
    /**
     * Binds the change event listener and renders the paginator
     */
    setupPaginator: function() {
        var _self = this;
        this.cfg.paginator.paginate = function(newState) {
            _self.paginate(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    /**
     * Applies events related to sorting in a non-obstrusive way
     */
    setupSortEvents: function() {
        var _self = this;

        $(this.jqId + ' th.ui-sortable-column').
            mouseover(function(){
                $(this).toggleClass('ui-state-hover');
            })
            .mouseout(function(){
                $(this).toggleClass('ui-state-hover');}
            )
            .click(function(event) {
                //Stop event if target is a clickable element inside header
                if($(event.target).is(':not(th,span,.ui-dt-c)')) {
                    return;
                }

                PrimeFaces.clearSelection();
                
                var columnHeader = $(this),
                sortorder = columnHeader.data('sortorder');
                
                if(sortorder) {
                    if(sortorder === 'DESCENDING')
                        _self.sort(columnHeader, 'ASCENDING');
                    else if(sortorder === 'ASCENDING')
                        _self.sort(columnHeader, 'DESCENDING');
                }
                else {
                    _self.sort(columnHeader, 'ASCENDING');
                }
            });
    },
      
    /**
     * Binds filter events to filters
     */
    setupFiltering: function() {
        var _self = this;
        this.cfg.filterEvent = this.cfg.filterEvent||'keyup';
        this.cfg.filterDelay = this.cfg.filterDelay||300;

        $(this.jqId + ' thead:first th.ui-filter-column div.ui-dt-c .ui-column-filter').each(function() {
            var filter = $(this);

            if(filter.is('input:text')) {
                PrimeFaces.skinInput(filter);

                if(_self.cfg.filterEvent === 'enter')
                    _self.bindEnterKeyFilter(filter);
                else
                    _self.bindFilterEvent(filter);
            } 
            else {
                PrimeFaces.skinSelect(filter);
                
                filter.change(function(e) {
                    _self.filter();
                });
            }
        });
    },
    
    bindEnterKeyFilter: function(filter) {
        var _self = this;
    
        filter.bind('keydown', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                e.preventDefault();
            }
        }).bind('keyup', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
                _self.filter();

                e.preventDefault();
            }
        });
    },
    
    bindFilterEvent: function(filter) {
        var _self = this;
    
        filter.bind(this.cfg.filterEvent, function(e) {
            if(_self.filterTimeout) {
                clearTimeout(_self.filterTimeout);
            }

            _self.filterTimeout = setTimeout(function() {
                _self.filter();
                _self.filterTimeout = null;
            }, 
            _self.cfg.filterDelay);
        });
    },
    
    /**
     * Applies events related to selection in a non-obstrusive way
     */
    bindSelectionEvents: function() {
        var _self = this;
        this.rowSelector = this.jqId + ' tbody.ui-datatable-data > tr.ui-widget-content:not(.ui-datatable-empty-message)';

        if(this.cfg.selectionMode) {
            this.bindRowHover();
            
            $(document).off('click.datatable', this.rowSelector).on('click.datatable', this.rowSelector, null, function(e) {
                _self.onRowClick(e, this);
            });
        }
        //Radio-Checkbox based rowselection
        else if(this.cfg.columnSelectionMode) {

            if(this.cfg.columnSelectionMode == 'single') {
                this.bindRadioEvents();
            }
            else {
                this.bindCheckboxEvents();
                this.updateHeaderCheckbox();
            }
        }
        
        //double click
        if(this.hasBehavior('rowDblselect')) {
            $(document).off('dblclick.datatable', this.rowSelector).on('dblclick.datatable', this.rowSelector, null, function(e) {
                _self.onRowDblclick(e, $(this));
            });
        };
    },
    
    bindRowHover: function() {
        $(document).off('mouseover.datatable mouseout.datatable', this.rowSelector)
                    .on('mouseover.datatable', this.rowSelector, null, function() {
                        var element = $(this);

                        if(!element.hasClass('ui-state-highlight')) {
                            element.addClass('ui-state-hover');
                        }
                    })
                    .on('mouseout.datatable', this.rowSelector, null, function() {
                        var element = $(this);

                        if(!element.hasClass('ui-state-highlight')) {
                            element.removeClass('ui-state-hover');
                        }
                    });
    },
    
    bindRadioEvents: function() {
        var radioSelector = this.jqId + ' tbody.ui-datatable-data > tr.ui-widget-content:not(.ui-datatable-empty-message) > td.ui-selection-column .ui-radiobutton .ui-radiobutton-box',
        radioInputSelector = this.jqId + ' tbody.ui-datatable-data > tr.ui-widget-content:not(.ui-datatable-empty-message) > td.ui-selection-column .ui-radiobutton input',
        _self = this;
                
        $(document).off('click.ui-radiobutton mouseover.ui-radiobutton mouseout.ui-radiobutton', radioSelector)
                        .on('mouseover.ui-radiobutton', radioSelector, null, function() {
                            var radio = $(this);
                            if(!radio.hasClass('ui-state-disabled')&&!radio.hasClass('ui-state-active')) {
                                radio.addClass('ui-state-hover');
                            }
                        })
                        .on('mouseout.ui-radiobutton', radioSelector, null, function() {
                            var radio = $(this);
                            radio.removeClass('ui-state-hover');
                        })
                        .on('click.ui-radiobutton', radioSelector, null, function() {
                            var radio = $(this),
                            checked = radio.hasClass('ui-state-active'),
                            disabled = radio.hasClass('ui-state-disabled');

                            if(!disabled && !checked) {
                                _self.selectRowWithRadio(radio);
                            }
                        });
    
                       
        //keyboard support
        $(document).off('focus.ui-radiobutton blur.ui-radiobutton change.ui-radiobutton', radioInputSelector)
                        .on('focus.ui-radiobutton', radioInputSelector, null, function() {
                            var input = $(this),
                            box = input.parent().next();

                            if(input.prop('checked')) {
                                box.removeClass('ui-state-active');
                            }

                            box.addClass('ui-state-focus');
                        })
                        .on('blur.ui-radiobutton', radioInputSelector, null, function() {
                            var input = $(this),
                            box = input.parent().next();

                            if(input.prop('checked')) {
                                box.addClass('ui-state-active');
                            }

                            box.removeClass('ui-state-focus');
                        })
                        .on('change.ui-radiobutton', radioInputSelector, null, function() {
                            var currentInput = $(radioInputSelector).filter(':checked'),
                            currentRadio = currentInput.parent().next();

                            _self.selectRowWithRadio(currentRadio);
                        });
                      
    },
    
    bindCheckboxEvents: function() {
        var checkAllTogglerSelector = this.jqId + ' table thead th.ui-selection-column .ui-chkbox.ui-chkbox-all .ui-chkbox-box',
        _self = this;
        
        this.checkAllToggler = $(checkAllTogglerSelector);
        
        //check-uncheck all
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
            _self.toggleCheckAll();
        });

        //row checkboxes
        var checkboxSelector = this.jqId + ' tbody.ui-datatable-data > tr.ui-widget-content:not(.ui-datatable-empty-message) > td.ui-selection-column .ui-chkbox .ui-chkbox-box',
        checkboxInputSelector = this.jqId + ' tbody.ui-datatable-data > tr.ui-widget-content:not(.ui-datatable-empty-message) > td.ui-selection-column .ui-chkbox input';

        $(document).off('mouseover.ui-chkbox mouseover.ui-chkbox click.ui-chkbox', checkboxSelector)
                    .on('mouseover.ui-chkbox', checkboxSelector, null, function() {
                        var box = $(this);
                        if(!box.hasClass('ui-state-disabled')&&!box.hasClass('ui-state-active')) {
                            box.addClass('ui-state-hover');
                        }
                    })
                    .on('mouseout.ui-chkbox', checkboxSelector, null, function() {
                        $(this).removeClass('ui-state-hover');
                    })
                    .on('click.ui-chkbox', checkboxSelector, null, function() {
                        var checkbox = $(this);

                        if(!checkbox.hasClass('ui-state-disabled')) {
                            var checked = checkbox.hasClass('ui-state-active');

                            if(checked) {
                                _self.unselectRowWithCheckbox(checkbox);
                            } 
                            else {                        
                                _self.selectRowWithCheckbox(checkbox);
                            }
                        }
                    });

        //keyboard support
        $(document).off('focus.ui-chkbox blur.ui-chkbox keydown.ui-chkbox keyup.ui-chkbox', checkboxInputSelector)
                .on('focus.ui-chkbox', checkboxInputSelector, null, function() {
                    var input = $(this),
                    box = input.parent().next();

                    if(input.prop('checked')) {
                        box.removeClass('ui-state-active');
                    }

                    box.addClass('ui-state-focus');
                })
                .on('blur.ui-chkbox', checkboxInputSelector, null, function() {
                    var input = $(this),
                    box = input.parent().next();

                    if(input.prop('checked')) {
                        box.addClass('ui-state-active');
                    }

                    box.removeClass('ui-state-focus');
                })
                .on('keydown.ui-chkbox', checkboxInputSelector, null, function(e) {
                    var keyCode = $.ui.keyCode;
                    if(e.which == keyCode.SPACE) {
                        e.preventDefault();
                    }
                })
                .on('keyup.ui-chkbox', checkboxInputSelector, null, function(e) {
                    var keyCode = $.ui.keyCode;

                    if(e.which == keyCode.SPACE) {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked')) {
                            _self.unselectRowWithCheckbox(box);
                        } 
                        else {                        
                            _self.selectRowWithCheckbox(box);
                        }

                        e.preventDefault();
                    }
                });
    },
    
    /**
     * Applies events related to row expansion in a non-obstrusive way
     */
    setupExpansionEvents: function() {
        var _self = this;

        $(this.jqId + ' tbody.ui-datatable-data tr td span.ui-row-toggler')
                .die()
                .live('click', function() {
                    _self.toggleExpansion(this);
                });
    },
    
    /**
     * Initialize data scrolling, for live scrolling listens scroll event to load data dynamically
     */
    setupScrolling: function() {
        this.scrollHeader = $(this.jqId + ' .ui-datatable-scrollable-header');
        this.scrollBody = $(this.jqId + ' .ui-datatable-scrollable-body');
        this.scrollFooter = $(this.jqId + ' .ui-datatable-scrollable-footer');
        this.scrollStateHolder = $(this.jqId + '_scrollState');
        var _self = this;
        
        this.restoreScrollState();

        if(this.cfg.liveScroll) {
            this.scrollOffset = this.cfg.scrollStep;
            this.shouldLiveScroll = true;       
        }

        this.scrollHeader.scroll(function() {
            _self.scrollBody.scrollLeft(_self.scrollHeader.scrollLeft());
            _self.scrollFooter.scrollLeft(_self.scrollHeader.scrollLeft());
        });
        
        this.scrollBody.scroll(function() {
            _self.scrollHeader.scrollLeft(_self.scrollBody.scrollLeft());
            _self.scrollFooter.scrollLeft(_self.scrollBody.scrollLeft());

            if(_self.shouldLiveScroll) {
                var scrollTop = this.scrollTop,
                scrollHeight = this.scrollHeight,
                viewportHeight = this.clientHeight;

                if(scrollTop >= (scrollHeight - (viewportHeight))) {
                    _self.loadLiveRows();
                }
            }
            
            _self.saveScrollState();
        });
        
        if(_self.isEmpty()) {
            _self.alignEmptyMessage();
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
    
    /**
     * Loads rows on-the-fly when scrolling live
     */
    loadLiveRows: function() {
        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId
        },
        _self = this;

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id) {
                    var lastRow = $(_self.jqId + ' .ui-datatable-scrollable-body table tr:last'),
                    lastRowColumnWrappers = lastRow.find('div.ui-dt-c');

                    //insert new rows
                    lastRow.after(content);

                    //align column widths of newly added rows with older ones
                    lastRow.nextAll('tr').each(function() {
                        var row = $(this);
                        row.find('div.ui-dt-c').each(function(i) {
                            var wrapper = $(this),
                            column = wrapper.parent();

                            wrapper.width(lastRowColumnWrappers.eq(i).width());
                            column.width('');
                        });
                    });

                    _self.scrollOffset += _self.cfg.scrollStep;

                    //Disable scroll if there is no more data left
                    if(_self.scrollOffset == _self.cfg.scrollLimit) {
                        _self.shouldLiveScroll = false;
                    }
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };

        options.params = [
            {name: this.id + '_scrolling', value: true},
            {name: this.id + '_scrollOffset', value: this.scrollOffset},
            {name: this.id + '_encodeFeature', value: true}
        ];

        PrimeFaces.ajax.AjaxRequest(options);
    },
    
    /**
     * Ajax pagination
     */
    paginate: function(newState) {
        var options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        var _self = this;

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id) {
                    //update body
                    _self.tbody.html(content);

                    //update header checkbox if all enabled checkboxes are checked in new page
                    if(_self.checkAllToggler) {
                        _self.updateHeaderCheckbox();
                    }
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
            
            return true;
        };
        
        options.oncomplete = function() {
            //update paginator state
            _self.paginator.cfg.page = newState.page;
            
            _self.paginator.updateUI();
        };

        options.params = [
            {name: this.id + '_pagination', value: true},
            {name: this.id + '_first', value: newState.first},
            {name: this.id + '_rows', value: newState.rows},
            {name: this.id + '_encodeFeature', value: true}
        ];

        if(this.hasBehavior('page')) {
            var pageBehavior = this.cfg.behaviors['page'];

            pageBehavior.call(this, newState, options);
        } 
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    },
    
    /**
     * Ajax sort
     */
    sort: function(columnHeader, order) {  
        columnHeader.data('sortorder', order);
    
        var options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        var _self = this;
        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id) {
                    //update body
                    _self.tbody.html(content);

                    //reset paginator
                    var paginator = _self.getPaginator();
                    if(paginator) {
                        paginator.setPage(0, true);
                    }
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
                
                //Update sort state visuals
                columnHeader.siblings('.ui-state-active').removeData('sortorder').removeClass('ui-state-active')
                            .find('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');
                
                columnHeader.addClass('ui-state-active');
                var sortIcon = columnHeader.find('.ui-sortable-column-icon');

                if(order === 'DESCENDING') {
                    sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');
                }
                else if(order === 'ASCENDING') {
                    sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };
        
        var columnId = columnHeader.attr('id');
        
        options.params = [
            {name: this.id + '_sorting', value: true},
            {name: this.id + '_sortKey', value: columnId},
            {name: this.id + '_sortDir', value: order},
            {name: this.id + '_skipChildren', value: true},
            {name: this.id + '_encodeFeature', value: true}
        ];
        
        if(columnHeader.hasClass('ui-dynamic-column')) {
            options.params.push({name: this.id + '_dynamic_column', value: true});
        }

        if(this.hasBehavior('sort')) {
            var sortBehavior = this.cfg.behaviors['sort'];

            sortBehavior.call(this, columnHeader, options);
        } 
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    },
    
    /**
     * Ajax filter
     */
    filter: function() {
        var options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        var _self = this;
        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    //update body
                    _self.tbody.html(content);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            //update paginator
            var paginator = _self.getPaginator();
            if(paginator) {
                paginator.setTotalRecords(this.args.totalRecords);
            }
            
            //align empty message width for scrolling table
            if(_self.cfg.scrollable && _self.isEmpty()) {
                _self.alignEmptyMessage();
            }
            
            return true;
        };

        options.params = [
            {name: this.id + '_filtering', value: true},
            {name: this.id + '_encodeFeature', value: true}
        ];

        if(this.hasBehavior('filter')) {
            var filterBehavior = this.cfg.behaviors['filter'];

            filterBehavior.call(this, {}, options);
        } 
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    },
    
    alignEmptyMessage: function() {
        var emptyMessageContainer = this.tbody.find('div.ui-dt-c'),
        columns = this.scrollHeader.find('thead:first > tr > th > div.ui-dt-c'),
        emptyMessageWidth = 0;

        for(var i = 0; i < columns.length; i++) {
            emptyMessageWidth += columns.eq(i).outerWidth();
        }

        emptyMessageContainer.width(emptyMessageWidth);
        
        this.restoreScrollState();
    },
    
    onRowClick: function(event, rowElement, silent) {    
        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is('.ui-dt-c,td,span')) {
            var row = $(rowElement),
            selected = row.hasClass('ui-state-highlight'),
            metaKey = event.metaKey||event.ctrlKey;

            //unselect a selected row if metakey is on
            if(selected && metaKey) {
                this.unselectRow(row, silent);
            }
            else {
                //unselect previous selection if this is single selection or multiple one with no keys
                if(this.isSingleSelection() || (this.isMultipleSelection() && event && !metaKey && !event.shiftKey)) {
                    this.unselectAllRows();
                }
                
                //range selection with shift key
                if(this.isMultipleSelection() && event && event.shiftKey) {                    
                    this.selectRowsInRange(row);
                }
                //select current row
                else {
                    this.originRowIndex = row.index();
                    this.cursorIndex = null;
                    
                    this.selectRow(row, silent);
                }
            } 

            PrimeFaces.clearSelection();
        }
    },
    
    onRowDblclick: function(event, row) {
        PrimeFaces.clearSelection();
        
        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is('.ui-dt-c,td,span')) {
            var rowMeta = this.getRowMeta(row);

            this.fireRowSelectEvent(rowMeta.key, 'rowDblselect');
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
        _self = this;
       
        //unselect previously selected rows with shift
        if(this.cursorIndex) {
            var oldCursorIndex = this.cursorIndex,
            rowsToUnselect = oldCursorIndex > this.originRowIndex ? rows.slice(this.originRowIndex, oldCursorIndex + 1) : rows.slice(oldCursorIndex, this.originRowIndex + 1);

            rowsToUnselect.each(function(i, item) {
                _self.unselectRow($(item), true);
            });
        }

        //select rows between cursor and origin
        this.cursorIndex = row.index();

        var rowsToSelect = this.cursorIndex > this.originRowIndex ? rows.slice(this.originRowIndex, this.cursorIndex + 1) : rows.slice(this.cursorIndex, this.originRowIndex + 1);

        rowsToSelect.each(function(i, item) {
            _self.selectRow($(item), true);
        });
    },
    
    selectRow: function(r, silent) {
        var row = this.findRow(r),
        rowMeta = this.getRowMeta(row);

        //add to selection
        row.removeClass('ui-state-hover').addClass('ui-state-highlight').attr('aria-selected', true);
        this.addSelection(rowMeta.key);

        //save state
        this.writeSelections();

        if(!silent) {
            this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
        }
    },
    
    unselectRow: function(r, silent) {
        var row = this.findRow(r),
        rowMeta = this.getRowMeta(row);

        //remove visual style
        row.removeClass('ui-state-highlight').attr('aria-selected', false);

        //remove from selection
        this.removeSelection(rowMeta.key);

        //save state
        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselect");
        }
    },
    
    /**
     * Sends a rowSelectEvent on server side to invoke a rowSelectListener if defined
     */
    fireRowSelectEvent: function(rowKey, behaviorEvent) {
        if(this.cfg.behaviors) {
            var selectBehavior = this.cfg.behaviors[behaviorEvent];

            if(selectBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_instantSelectedRowKey', value: rowKey}
                    ]
                };

                selectBehavior.call(this, rowKey, ext);
            }
        }
    },
    
    /**
     * Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if defined
     */
    fireRowUnselectEvent: function(rowKey, behaviorEvent) {
        if(this.cfg.behaviors) {
            var unselectBehavior = this.cfg.behaviors[behaviorEvent];

            if(unselectBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_instantUnselectedRowKey', value: rowKey}
                    ]
                };

                unselectBehavior.call(this, rowKey, ext);
            }
        }
    },
    
    /**
     * Selects the corresping row of a radio based column selection
     */
    selectRowWithRadio: function(radio) {
        var row = radio.parents('tr:first'),
        rowMeta = this.getRowMeta(row);

        //clean previous selection
        this.selection = [];
        row.siblings('.ui-state-highlight').removeClass('ui-state-highlight').attr('aria-selected', false)      //row
            .find('td.ui-selection-column .ui-radiobutton .ui-radiobutton-box').removeClass('ui-state-active')  //radio
            .children('span.ui-radiobutton-icon').removeClass('ui-icon ui-icon-bullet');                        //radio icon
        
        if(this.currentRadio) {
            this.currentRadio.prev().children('input').removeAttr('checked');        
        }
               
        //select current
        if(!radio.hasClass('ui-state-focus')) {
            radio.addClass('ui-state-active');
        }
        radio.children('.ui-radiobutton-icon').addClass('ui-icon ui-icon-bullet');
        radio.prev().children('input').attr('checked', 'checked');
        this.currentRadio = radio;

        //add to selection
        this.addSelection(rowMeta.key);
        row.addClass('ui-state-highlight').attr('aria-selected', true); 

        //save state
        this.writeSelections();

        this.fireRowSelectEvent(rowMeta.key, 'rowSelectRadio');
    },
    
    /**
     * Selects the corresping row of a checkbox based column selection
     */
    selectRowWithCheckbox: function(checkbox, silent) {
        var row = checkbox.parents('tr:first'),
        rowMeta = this.getRowMeta(row);

        //update visuals
        if(!checkbox.hasClass('ui-state-focus')) {
            checkbox.addClass('ui-state-active');
        }
        checkbox.children('span.ui-chkbox-icon:first').addClass('ui-icon ui-icon-check');
        row.addClass('ui-state-highlight').attr('aria-selected', true);

        //check input
        checkbox.prev().children('input').attr('checked', 'checked');

        //add to selection
        this.addSelection(rowMeta.key);

        this.updateHeaderCheckbox();

        this.writeSelections();

        if(!silent) {
            this.fireRowSelectEvent(rowMeta.key, "rowSelectCheckbox");
        }
    },
    
    /**
     * Unselects the corresping row of a checkbox based column selection
     */
    unselectRowWithCheckbox: function(checkbox, silent) {
        var row = checkbox.parents('tr:first'),
        rowMeta = this.getRowMeta(row);

        checkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon:first').removeClass('ui-icon ui-icon-check');
        row.removeClass('ui-state-highlight').attr('aria-selected', false);

        //uncheck input
        checkbox.prev().children('input').removeAttr('checked');

        //remove from selection
        this.removeSelection(rowMeta.key);

        //unselect header checkbox
        this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon:first').removeClass('ui-icon ui-icon-check');

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselectCheckbox");
        }
    },
    
    unselectAllRows: function() {
        this.tbody.children('tr.ui-state-highlight').removeClass('ui-state-highlight').attr('aria-selected', false); 
        this.selection = [];
        this.writeSelections();
    },
    
    /**
     * Toggles all rows with checkbox
     */
    toggleCheckAll: function() {
        var checkboxes = this.tbody.find('> tr > td.ui-selection-column .ui-chkbox-box:not(.ui-state-disabled)'),
        checked = this.checkAllToggler.hasClass('ui-state-active'),
        _self = this;

        if(checked) {
            this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');

            checkboxes.each(function() {
                _self.unselectRowWithCheckbox($(this), true);
            });
        } 
        else {
            this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon ui-icon-check');

            checkboxes.each(function() {
                _self.selectRowWithCheckbox($(this), true);

            });
        }

        //save state
        this.writeSelections();

        //fire toggleSelect event
        if(this.cfg.behaviors) {
            var toggleSelectBehavior = this.cfg.behaviors['toggleSelect'];

            if(toggleSelectBehavior) {            
                toggleSelectBehavior.call(this);
            }
        }
    },
    
    /**
     * Expands a row to display detail content
     */
    toggleExpansion: function(expanderElement) {
        var expander = $(expanderElement),
        row = expander.parents('tr:first'),
        rowIndex = this.getRowMeta(row).index,
        expanded = row.hasClass('ui-expanded-row'),
        _self = this;

        //Run toggle expansion if row is not being toggled already to prevent conflicts
        if($.inArray(rowIndex, this.expansionProcess) == -1) {
            if(expanded) {
                this.expansionProcess.push(rowIndex);
                expander.removeClass('ui-icon-circle-triangle-s');
                row.removeClass('ui-expanded-row');

                row.next().fadeOut(function() {
                $(this).remove();

                _self.expansionProcess = $.grep(_self.expansionProcess, function(r) {
                        return r != rowIndex;
                    });
                });
                
                this.fireRowCollapseEvent(row);
            }
            else {
                this.expansionProcess.push(rowIndex);
                expander.addClass('ui-icon-circle-triangle-s');
                row.addClass('ui-expanded-row');

                this.loadExpandedRowContent(row);
            }
        }
    },
    
    loadExpandedRowContent: function(row) {
        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId
        },
        rowIndex = this.getRowMeta(row).index,
        _self = this;

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    row.after(content);
                    row.next().fadeIn();
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };

        options.oncomplete = function() {
            _self.expansionProcess = $.grep(_self.expansionProcess, function(r) {
                return r != rowIndex;
            });
        };

        options.params = [
            {name: this.id + '_rowExpansion', value: true},
            {name: this.id + '_expandedRowIndex', value: rowIndex},
            {name: this.id + '_encodeFeature', value: true},
            {name: this.id + '_skipChildren', value: true}
        ];
        
        if(this.hasBehavior('rowToggle')) {
            var rowToggleBehavior = this.cfg.behaviors['rowToggle'];

            rowToggleBehavior.call(this, row, options);
        } 
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    },
    
    fireRowCollapseEvent: function(row) {
        var rowIndex = this.getRowMeta(row).index;
        
        if(this.hasBehavior('rowToggle')) {
            var ext = {
                params: [
                    {name: this.id + '_collapsedRowIndex', value: rowIndex}
                ]
            };
        
            var rowToggleBehavior = this.cfg.behaviors['rowToggle'];

            rowToggleBehavior.call(this, row, ext);
        } 
    },
    
    /**
     * Displays in-cell editors for given row
     */
    showEditors: function(el) {
        var element = $(el);

        element.parents('tr:first').addClass('ui-state-highlight').children('td.ui-editable-column').each(function() {
            var column = $(this);

            column.find('span.ui-cell-editor-output').hide();
            column.find('span.ui-cell-editor-input').show();

            if(element.hasClass('ui-icon-pencil')) {
                element.hide().siblings().show();
            }
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
        var row = rowEditor.parents('tr:first'),
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId
        },
        expanded = row.hasClass('ui-expanded-row'),
        _self = this;

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    if(!this.args.validationFailed) {
                        //remove row expansion
                        if(expanded) {
                            row.next().remove();
                        }

                        row.replaceWith(content);
                    }
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            return true;
        };

        options.params = [
            {name: this.id + '_rowEditIndex', value: this.getRowMeta(row).index},
            {name: this.id + '_rowEditAction', value: action},
            {name: this.id + '_encodeFeature', value: true}
        ];
        
        if(action === 'save') {
            row.find('span.ui-cell-editor').each(function() {
                var editorId = $(this).attr('id');
                
                options.params.push({name: editorId, value: editorId});
            });
        }

        if(action === 'save' && this.hasBehavior('rowEdit')) {
            this.cfg.behaviors['rowEdit'].call(this, row, options);
        }
        else if(action === 'cancel' && this.hasBehavior('rowEditCancel')) {
            this.cfg.behaviors['rowEditCancel'].call(this, row, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options); 
        }
    }

    /**
     * Returns the paginator instance if any defined
     */
    ,getPaginator: function() {
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
        return this.cfg.selectionMode == 'multiple';
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
     * Binds cell editor events non-obstrusively
     */
    setupCellEditorEvents: function() {
        var _self = this,
        rowEditors = $(this.jqId + ' tbody.ui-datatable-data > tr > td span.ui-row-editor');

        rowEditors.find('span.ui-icon-pencil').die().live('click', function() {
            _self.showEditors(this);
        });

        rowEditors.find('span.ui-icon-check').die().live('click', function() {
            _self.saveRowEdit($(this).parent());
        });

        rowEditors.find('span.ui-icon-close').die().live('click', function() {
            _self.cancelRowEdit($(this).parent());
        }); 
    },
    
    /**
     * Clears table filters
     */
    clearFilters: function() {
        $(this.jqId + ' thead th .ui-column-filter').val('');
        
        this.filter();
    },
    
    /**
     * Add resize behavior to columns
     */
    setupResizableColumns: function() {
        //Add resizers and resizer helper
        $(this.jqId + ' thead tr th.ui-resizable-column div.ui-dt-c').prepend('<span class="ui-column-resizer">&nbsp;</span>');
        $(this.jqId).append('<div class="ui-column-resizer-helper ui-state-highlight"></div>');

        //Variables
        var resizerHelper = $(this.jqId + ' .ui-column-resizer-helper'),
        resizers = $(this.jqId + ' thead th span.ui-column-resizer'),
        scrollHeader = $(this.jqId + ' .ui-datatable-scrollable-header'),
        scrollBody = $(this.jqId + ' .ui-datatable-scrollable-body'),
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
                oldPos = ui.originalPosition.left,
                newPos = ui.position.left,
                change = (newPos - oldPos),
                newWidth = (columnHeaderWrapper.width() + change - (ui.helper.width() / 2));

                ui.helper.css('left','');
                resizerHelper.hide();

                columnHeaderWrapper.width(newWidth);
                columnHeader.css('width', '');

                _self.tbody.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);            
                tfoot.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);

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
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }
    
        return false;
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
     * Adds given rowIndex to selection if it doesn't exist already
     */
    addSelection: function(rowIndex) {
        if(!this.isSelected(rowIndex)) {
            this.selection.push(rowIndex);
        }
    },
    
    /**
     * Finds if given rowIndex is in selection
     */
    isSelected: function(rowIndex) {
        return PrimeFaces.inArray(this.selection, rowIndex);
    },
    
    getRowMeta: function(row) {
        var meta = {
            index: row.data('ri'),
            key:  row.attr('data-rk')
        };

        return meta;
    },
    
    setupDraggableColumns : function() {
        this.orderStateHolder = $(this.jqId + '_columnOrder');
        this.saveColumnOrder();
        
        this.dragIndicatorTop = $('<div id="' + this.id + '_dnd_top" class="ui-column-dnd-top"><span class="ui-icon ui-icon-arrowthick-1-s" /></div>').appendTo(document.body);
        this.dragIndicatorBottom = $('<div id="' + this.id + '_dnd_bottom" class="ui-column-dnd-bottom"><span class="ui-icon ui-icon-arrowthick-1-n" /></div>').appendTo(document.body);


        var _self = this;

        $(this.jqId + ' thead th').draggable({
            appendTo: 'body'
            ,opacity: 0.75
            ,cursor: 'move'
            ,drag: function(event, ui) {
                var droppable = ui.helper.data('droppable-column');

                if(droppable) {
                    var droppableOffset = droppable.offset(),
                    topArrowY = droppableOffset.top - 10,
                    bottomArrowY = droppableOffset.top + droppable.height() + 8,
                    arrowX = null;
                    
                    //calculate coordinates of arrow depending on mouse location
                    if(event.originalEvent.pageX >= droppableOffset.left + (droppable.width() / 2)) {
                        arrowX = droppable.next().offset().left - 9;
                        ui.helper.data('drop-location', 1);     //right
                    }
                    else {
                        arrowX = droppableOffset.left  - 9;
                        ui.helper.data('drop-location', -1);    //left
                    }
                    
                    _self.dragIndicatorTop.offset({'left': arrowX, 'top': topArrowY}).show();
                    _self.dragIndicatorBottom.offset({'left': arrowX, 'top': bottomArrowY}).show();
                }
            }
            ,stop: function(event, ui) {
                //hide dnd arrows
                _self.dragIndicatorTop.css({'left':0, 'top':0}).hide();
                _self.dragIndicatorBottom.css({'left':0, 'top':0}).hide();
            }
            ,helper: function() {
                var header = $(this),
                helper = $('<div class="ui-widget ui-state-default" style="padding:4px 10px;text-align:center;"></div>');

                helper.width(header.width());
                helper.height(header.height());

                helper.html(header.html());

                return helper.get(0);
            }

        }).droppable({
            hoverClass:'ui-state-highlight'
            ,tolerance:'pointer'
            ,over: function(event, ui) {
                ui.helper.data('droppable-column', $(this));
            }
            ,drop: function(event, ui) {
                
                var draggedColumn = ui.draggable,
                dropLocation = ui.helper.data('drop-location'),
                droppedColumn =  $(this);
                
                var draggedCells = _self.tbody.find('> tr > td:nth-child(' + (draggedColumn.index() + 1) + ')'),
                droppedCells = _self.tbody.find('> tr > td:nth-child(' + (droppedColumn.index() + 1) + ')');
                
                //drop right
                if(dropLocation > 0) {
                    draggedColumn.insertAfter(droppedColumn);

                    draggedCells.each(function(i, item) {
                        $(this).insertAfter(droppedCells.eq(i));
                    });
                }
                //drop left
                else {
                    draggedColumn.insertBefore(droppedColumn);

                    draggedCells.each(function(i, item) {
                        $(this).insertBefore(droppedCells.eq(i));
                    });
                }
               
                //save order
                _self.saveColumnOrder();

                //fire toggleCheckAll event
                if(_self.cfg.behaviors) {
                    var columnReorderBehavior = _self.cfg.behaviors['colReorder'];

                    if(columnReorderBehavior) {            
                        columnReorderBehavior.call(_self);
                    }
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
    
    /**
     * Returns if there is any data displayed
     */
    isEmpty: function() {
        return this.tbody.children('tr.ui-datatable-empty-message').length == 1;
    },
    
    getSelectedRowsCount: function() {
        return this.isSelectionEnabled() ? this.selection.length : 0;
    },
    
    updateHeaderCheckbox: function() {
        var checkboxes = $(this.jqId + ' tbody.ui-datatable-data:first > tr > td.ui-selection-column .ui-chkbox-box'),
        uncheckedBoxes = $.grep(checkboxes, function(element) {
            var checkbox = $(element),
            disabled = checkbox.hasClass('ui-state-disabled'),
            checked = checkbox.hasClass('ui-state-active');

            return !(checked || disabled); 
        });

        if(uncheckedBoxes.length == 0)
            this.checkAllToggler.addClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
        else
            this.checkAllToggler.removeClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
    }  

});