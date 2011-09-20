/**
 * PrimeFaces DataTable Widget
 */
PrimeFaces.widget.DataTable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $(this.jqId);
    this.tbody = this.jqId + '_data';

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

        this.setupSelectionEvents();
    }

    if(this.cfg.expansion) {
        this.expansionProcess = [];
        this.setupExpansionEvents();
    }

    var rowEditors = this.getRowEditors();
    if(rowEditors.length > 0) {
        this.setupCellEditorEvents(rowEditors);
    }
    
    if(this.cfg.scrollable) {
        this.setupScrolling();
    }

    if(this.cfg.resizableColumns) {
        this.setupResizableColumns();
    }
}

/**
 * Binds the change event listener and renders the paginator
 */
PrimeFaces.widget.DataTable.prototype.setupPaginator = function() {
    var paginator = this.getPaginator();

    paginator.subscribe('changeRequest', this.paginate, null, this);
    paginator.render();
}

/**
 * Applies events related to sorting in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupSortEvents = function() {
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

            var columnId = $(this).attr('id');
            
            //Reset previous sorted columns
            $(this).siblings().removeClass('ui-state-active').
                find('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');

            //Update sort state
            $(this).addClass('ui-state-active');
            var sortIcon = $(this).find('.ui-sortable-column-icon');
            
            if(sortIcon.hasClass('ui-icon-triangle-1-n')) {
                sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');

                _self.sort(columnId, "DESCENDING");
                PrimeFaces.clearSelection();
            }
            else if(sortIcon.hasClass('ui-icon-triangle-1-s')) {
                sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, "ASCENDING");
            } 
            else {
                sortIcon.addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, "ASCENDING");
            }
        });
}

/**
 * Applies events related to selection in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupSelectionEvents = function() {
    var _self = this;

    //Row mouseover, mouseout, click
    if(this.cfg.selectionMode) {
        var selectEvent = this.cfg.dblclickSelect ? 'dblclick' : 'click';

        $(this.jqId + ' tbody.ui-datatable-data > tr.ui-widget-content').css('cursor', 'pointer')
            .die('mouseover.datatable mouseout.datatable contextmenu.datatable ' + selectEvent + '.datatable')
            .live('mouseover.datatable', function() {
                var element = $(this);

                if(!element.hasClass('ui-state-highlight')) {
                    element.addClass('ui-state-hover');
                }

            })
            .live('mouseout.datatable', function() {
                var element = $(this);

                if(!element.hasClass('ui-state-highlight')) {
                    element.removeClass('ui-state-hover');
                }
            })
            .live(selectEvent + '.datatable', function(event) {
                _self.onRowClick(event, this);
            })
            .live('contextmenu.datatable', function(event) {
               _self.onRowClick(event, this);
               event.preventDefault();
            });
    }
    //Radio-Checkbox based rowselection
    else if(this.cfg.columnSelectionMode) {
        
        if(this.cfg.columnSelectionMode == 'single') {
            $(this.jqId + ' tbody.ui-datatable-data td.ui-selection-column input:radio')
                .die('click')
                .live('click', function() {
                    _self.selectRowWithRadio(this);
                });
        }
        else {
            $(this.jqId + ' tbody.ui-datatable-data td.ui-selection-column input:checkbox')
                .die('click')
                .live('click', function() {
                    _self.clickRowWithCheckbox(this);
                });
        }
    }
}

/**
 * Applies events related to row expansion in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupExpansionEvents = function() {
    var _self = this;

    $(this.jqId + ' tbody.ui-datatable-data tr td span.ui-row-toggler')
            .die()
            .live('click', function() {
                _self.toggleExpansion(this);
            });
}

/**
 * Initialize data scrolling, for live scrolling listens scroll event to load data dynamically
 */
PrimeFaces.widget.DataTable.prototype.setupScrolling = function() {
    var scrollHeader = $(this.jqId + ' .ui-datatable-scrollable-header'),
    scrollBody = $(this.jqId + ' .ui-datatable-scrollable-body'),
    scrollFooter = $(this.jqId + ' .ui-datatable-scrollable-footer'),
    _self = this;
    
    if(this.cfg.liveScroll) {
        this.scrollOffset = this.cfg.scrollStep;
        this.shouldLiveScroll = true;       
    }
    
    scrollBody.scroll(function() {
        scrollHeader.scrollLeft(scrollBody.scrollLeft());
        scrollFooter.scrollLeft(scrollBody.scrollLeft());
        
        if(_self.shouldLiveScroll) {
            var scrollTop = this.scrollTop,
            scrollHeight = this.scrollHeight,
            viewportHeight = this.clientHeight;

            if(scrollTop >= (scrollHeight - (viewportHeight))) {
                _self.loadLiveRows();
            }
        }
    });
}

/**
 * Loads rows on-the-fly when scrolling live
 */
PrimeFaces.widget.DataTable.prototype.loadLiveRows = function() {
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

            if(id == _self.id){
                $(_self.jqId + ' .ui-datatable-scrollable-body table tr:last').after(content);

                _self.scrollOffset += _self.cfg.scrollStep;

                //Disable scroll if there is no more data left
                if(_self.scrollOffset == _self.cfg.scrollLimit) {
                    _self.shouldLiveScroll = false;
                }

                if(_self.cfg.resizableColumns) {
                    _self.restoreColumnWidths();
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };

    var params = {};
    params[this.id + "_scrolling"] = true;
    params[this.id + "_scrollOffset"] = this.scrollOffset;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 * Ajax pagination
 */
PrimeFaces.widget.DataTable.prototype.paginate = function(newState) {
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
                $(_self.tbody).replaceWith(content);

                _self.getPaginator().setState(newState);
                
                if(_self.cfg.resizableColumns) {
                    _self.restoreColumnWidths();
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };

    var params = {};
    params[this.id + "_paging"] = true;
    params[this.id + "_first"] = newState.recordOffset;
    params[this.id + "_rows"] = newState.rowsPerPage;
    params[this.id + "_page"] = newState.page;
    params[this.id + "_updateBody"] = true;

    options.params = params;
    
    if(this.hasBehavior('page')) {
       var pageBehavior = this.cfg.behaviors['page'];
       
       pageBehavior.call(this, newState, options);
    } else {
       PrimeFaces.ajax.AjaxRequest(options); 
    }
}

/**
 * Ajax sort
 */
PrimeFaces.widget.DataTable.prototype.sort = function(columnId, asc) {
    if(this.isSelectionEnabled()) {
        this.clearSelection();
    }
    
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
                $(_self.tbody).replaceWith(content);
                
                //reset paginator
                var paginator = _self.getPaginator();
                if(paginator) {
                   paginator.setPage(1, true);
                }
                
                if(_self.cfg.resizableColumns) {
                    _self.restoreColumnWidths();
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };
    
    var params = {};
    params[this.id + "_sorting"] = true;
    params[this.id + "_sortKey"] = columnId;
    params[this.id + "_sortDir"] = asc;
    params[this.id + "_updateBody"] = true;

    options.params = params;
    
    if(this.hasBehavior('sort')) {
       var sortBehavior = this.cfg.behaviors['sort'];
       
       sortBehavior.call(this, columnId, options);
    } else {
       PrimeFaces.ajax.AjaxRequest(options); 
    }
}

/**
 * Ajax filter
 */
PrimeFaces.widget.DataTable.prototype.filter = function() {
    if(this.isSelectionEnabled()) {
        this.clearSelection();
    }
    
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
                $(_self.tbody).replaceWith(content);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
        
        //update paginator
        var paginator = _self.getPaginator();
        if(paginator) {
            paginator.setPage(1, true);
            paginator.setTotalRecords(this.args.totalRecords, true);
        }

        if(_self.cfg.resizableColumns) {
            _self.restoreColumnWidths();
        }
        
        return true;
    };
    
    var params = {};
    params[this.id + "_filtering"] = true;
    params[this.id + "_updateBody"] = true;

    options.params = params;

    if(this.hasBehavior('filter')) {
       var filterBehavior = this.cfg.behaviors['filter'];
       
       filterBehavior.call(this, {}, options);
    } else {
       PrimeFaces.ajax.AjaxRequest(options); 
    }
}

PrimeFaces.widget.DataTable.prototype.onRowClick = function(event, rowElement) {    
    //Check if rowclick triggered this event not a clickable element in row content
    if($(event.target).is('.ui-dt-c,td,span')) {
        var row = $(rowElement),
        selected = row.hasClass('ui-state-highlight');

        if(selected)
            this.unselectRow(event, row);
        else
            this.selectRow(event, row);
        
        PrimeFaces.clearSelection();
    }
}

PrimeFaces.widget.DataTable.prototype.selectRow = function(event, row) {
    var rowMeta = this.getRowMeta(row),
    _self = this;

    //unselect previous selection if this is single selection or multiple one with no keys
    if(this.isSingleSelection() || (this.isMultipleSelection() && (!event.metaKey && !event.shiftKey))) {
        row.siblings('.ui-state-highlight').removeClass('ui-state-highlight'); 
        this.selection = [];
    }
    
    if(this.isMultipleSelection() && event.shiftKey) {
        var rows = $(this.tbody).children();
        this.originRow = this.originRow||rows.eq(0);
        var originIndex = this.originRow.index();

        //unselect previously selected rows with shift
        if(this.cursor) {
            var oldCursorIndex = this.cursor.index(),
            rowsToUnselect = oldCursorIndex > originIndex ? rows.slice(originIndex, oldCursorIndex + 1) : rows.slice(oldCursorIndex, originIndex + 1);
            
            rowsToUnselect.each(function(i, item) {
                var r = $(item),
                rkey = _self.getRowMeta(r).key;

                r.removeClass('ui-state-highlight');
                _self.removeSelection(rkey);
            });
        }
        
        //select rows between cursor and origin
        this.cursor = row;

        var cursorIndex = this.cursor.index(),
        rowsToSelect = cursorIndex > originIndex ? rows.slice(originIndex, cursorIndex + 1) : rows.slice(cursorIndex, originIndex + 1);

        rowsToSelect.each(function(i, item) {
            var r = $(item),
            rkey = _self.getRowMeta(r).key;

            r.removeClass('ui-state-hover').addClass('ui-state-highlight');
            _self.addSelection(rkey);
        });
        
    }
    else {
        this.originRow = row;
        this.cursor = null;
                
        //add to selection
        row.removeClass('ui-state-hover').addClass('ui-state-highlight');
        this.addSelection(rowMeta.key);
    }

    //save state
    this.writeSelections();

    this.fireRowSelectEvent(rowMeta.key);
}

PrimeFaces.widget.DataTable.prototype.unselectRow = function(event, row) {
    var rowMeta = this.getRowMeta(row);

    if(event.metaKey) {
        //remove visual style
        row.removeClass('ui-state-highlight');

        //remove from selection
        this.removeSelection(rowMeta.key);

        //save state
        this.writeSelections();

        this.fireRowUnselectEvent(rowMeta.key);
    }
    else if(this.isMultipleSelection()){
        this.selectRow(event, row);
    }
}

/**
 *  Sends a rowSelectEvent on server side to invoke a rowSelectListener if defined
 */
PrimeFaces.widget.DataTable.prototype.fireRowSelectEvent = function(rowKey) {
    if(this.cfg.behaviors) {
        var selectBehavior = this.cfg.behaviors['rowSelect'];
        
        if(selectBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_instantSelectedRowKey'] = rowKey;
            
            selectBehavior.call(this, rowKey, ext);
        }
    }
}

/**
 *  Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if defined
 */
PrimeFaces.widget.DataTable.prototype.fireRowUnselectEvent = function(rowKey) {
    if(this.cfg.behaviors) {
        var unselectBehavior = this.cfg.behaviors['rowUnselect'];
        
        if(unselectBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_instantUnselectedRowKey'] = rowKey;
            
            unselectBehavior.call(this, rowKey, ext);
        }
    }
}

/**
 *  Selects the corresping row of a radio based column selection
 */
PrimeFaces.widget.DataTable.prototype.selectRowWithRadio = function(element) {
    var radio = $(element),
    row = radio.parents('tr:first'),
    rowMeta = this.getRowMeta(row);

    //clean previous selection
    this.selection = [];
    row.siblings('.ui-state-highlight').removeClass('ui-state-highlight'); 
    
    //add to selection
    this.addSelection(rowMeta.key);
    row.addClass('ui-state-highlight');

    //save state
    this.writeSelections();
    
    this.fireRowSelectEvent(rowMeta.key);
}

/**
 *  Selects the corresping row of a checkbox based column selection
 */
PrimeFaces.widget.DataTable.prototype.clickRowWithCheckbox = function(element) {
    var checkbox = $(element),
    row = checkbox.parents('tr:first'),
    rowMeta = this.getRowMeta(row),
    checked = checkbox.attr('checked');

    if(checked) {
        this.addSelection(rowMeta.key);
        row.addClass('ui-state-highlight');
        this.writeSelections();
        this.fireRowSelectEvent(rowMeta.key);
    }
    else {
        this.removeSelection(rowMeta.key);
        row.removeClass('ui-state-highlight');
        this.writeSelections();
        this.fireRowUnselectEvent(rowMeta.key);
    }

    
}

/**
 * Selects all rows with checkbox
 */
PrimeFaces.widget.DataTable.prototype.toggleCheckAll = function(element) {
    var checkbox = $(element),
    checked = checkbox.attr('checked'),
    rows = $(this.jqId + ' .ui-datatable-data > tr'),
    checkboxes = rows.children('td.ui-selection-column').find('input:checkbox'),
    _self = this;

    if(checked) {
        checkboxes.attr('checked', true);

        //add to selection
        rows.each(function() {
            _self.addSelection(_self.getRowMeta($(this)).key);
        });
    }
    else {
        checkboxes.attr('checked', false);
        
        //remove from selection
        rows.each(function() {
            _self.removeSelection(_self.getRowMeta($(this)).key);
        });
    }

    //save state
    this.writeSelections();
}

/**
 * Expands a row to display detail content
 */
PrimeFaces.widget.DataTable.prototype.toggleExpansion = function(expanderElement) {
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
        }
        else {
            this.expansionProcess.push(rowIndex);
            expander.addClass('ui-icon-circle-triangle-s');
            row.addClass('ui-expanded-row');

            this.loadExpandedRowContent(row);
        }
    }
}

PrimeFaces.widget.DataTable.prototype.loadExpandedRowContent = function(row) {
    if(this.cfg.onExpandStart) {
        this.cfg.onExpandStart.call(this, row);
    }

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

    var params = {};
    params[this.id + '_rowExpansion'] = true;
    params[this.id + '_expandedRowIndex'] = rowIndex;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 * Displays in-cell editors for given row
 */
PrimeFaces.widget.DataTable.prototype.showEditors = function(el) {
    var element = $(el);
    
    element.parents('tr:first').addClass('ui-state-highlight').children('td.ui-editable-column').each(function() {
        var column = $(this);

        column.find('span.ui-cell-editor-output').hide();
        column.find('span.ui-cell-editor-input').show();

        if(element.hasClass('ui-icon-pencil')) {
            element.hide().siblings().show();
        }
    });
}

/**
 * Saves the edited row
 */
PrimeFaces.widget.DataTable.prototype.saveRowEdit = function(element) {
    this.doRowEditRequest(element, 'save');
}

/**
 * Cancels row editing
 */
PrimeFaces.widget.DataTable.prototype.cancelRowEdit = function(element) {
    this.doRowEditRequest(element, 'cancel');
}

/**
 * Sends an ajax request to handle row save or cancel
 */
PrimeFaces.widget.DataTable.prototype.doRowEditRequest = function(element, action) {
    var row = $(element).parents('tr').eq(0),
    options = {
        source: this.id,
        update: this.id,
        formId: this.cfg.formId
    },
    _self = this,
    rowEditorId = row.find('span.ui-row-editor').attr('id'),
    expanded = row.hasClass('ui-expanded-row');

    if(action === 'save') {
        //Only process cell editors of current row
        var editorsToProcess = new Array();

        row.find('span.ui-cell-editor').each(function() {
           editorsToProcess.push($(this).attr('id'));
        });

        options.process = editorsToProcess.join(' ');
    }

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

    var params = {};
    params[rowEditorId] = rowEditorId;
    params[this.id + '_rowEdit'] = true;
    params[this.id + '_editedRowIndex'] = this.getRowMeta(row).index;
    
    if(action === 'cancel') {
        params[this.id + '_rowEditCancel'] = true;
    }

    options.params = params;
    
    if(this.hasBehavior('rowEdit')) {
       var rowEditBehavior = this.cfg.behaviors['rowEdit'];
       
       rowEditBehavior.call(this, row, options);
    } else {
       PrimeFaces.ajax.AjaxRequest(options); 
    }
}


/**
 * Returns the paginator instance if any defined
 */
PrimeFaces.widget.DataTable.prototype.getPaginator = function() {
    return this.cfg.paginator;
}

/**
 * Writes selected row ids to state holder
 */
PrimeFaces.widget.DataTable.prototype.writeSelections = function() {
    $(this.selectionHolder).val(this.selection.join(','));
}

/**
 * Returns type of selection
 */
PrimeFaces.widget.DataTable.prototype.isSingleSelection = function() {
    return this.cfg.selectionMode == 'single';
}

PrimeFaces.widget.DataTable.prototype.isMultipleSelection = function() {
    return this.cfg.selectionMode == 'multiple';
}

/**
 * Clears the selection state
 */
PrimeFaces.widget.DataTable.prototype.clearSelection = function() {
    this.selection = [];
    
    $(this.selectionHolder).val('');
}

/**
 * Returns true|false if selection is enabled|disabled
 */
PrimeFaces.widget.DataTable.prototype.isSelectionEnabled = function() {
    return this.cfg.selectionMode != undefined || this.cfg.columnSelectionMode != undefined;
}

/**
 * Returns true|false if datatable has incell editors
 */
PrimeFaces.widget.DataTable.prototype.getRowEditors = function() {
    return $(this.jqId + ' tbody.ui-datatable-data tr td span.ui-row-editor');
}

/**
 * Binds cell editor events non-obstrusively
 */
PrimeFaces.widget.DataTable.prototype.setupCellEditorEvents = function(rowEditors) {
    var _self = this;
    
    rowEditors.find('span.ui-icon-pencil').die().live('click', function() {
        _self.showEditors(this);
    });

    rowEditors.find('span.ui-icon-check').die().live('click', function() {
        _self.saveRowEdit(this);
    });

    rowEditors.find('span.ui-icon-close').die().live('click', function() {
        _self.cancelRowEdit(this);
    });
}

/**
 * Clears table filters and then update the datatable
 */
PrimeFaces.widget.DataTable.prototype.clearFilters = function() {
    $(this.jqId + ' thead th .ui-column-filter').val('');
}

/**
 * Add resize behavior to columns
 */
PrimeFaces.widget.DataTable.prototype.setupResizableColumns = function() {
    //Add resizers and resizer helper
    $(this.jqId + ' thead tr th div.ui-dt-c').prepend('<div class="ui-column-resizer"></div>');
    $(this.jqId).append('<div class="ui-column-resizer-helper ui-state-highlight"></div>');

    //Variables
    var resizerHelper = $(this.jqId + ' .ui-column-resizer-helper'),
    resizers = $(this.jqId + ' thead th div.ui-column-resizer'),
    scrollHeader = $(this.jqId + ' .ui-datatable-scrollable-header'),
    scrollBody = $(this.jqId + ' .ui-datatable-scrollable-body'),
    table = $(this.jqId + ' table'),
    thead = $(this.jqId + ' thead'),  
    tfoot = $(this.jqId + ' tfoot'),
    _self = this;
     
    //State cookie
    this.columnWidthsCookie = this.id + '_columnWidths';
    
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
            newWidth = columnHeaderWrapper.width() + change
            tbody = $(_self.jqId + ' tbody');
            
            ui.helper.css('left','');
            resizerHelper.hide();
            
            columnHeaderWrapper.width(newWidth);
                        
            tbody.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);            
            tfoot.find('tr td:nth-child(' + (columnHeader.index() + 1) + ')').width('').children('div').width(newWidth);

            scrollHeader.scrollLeft(scrollBody.scrollLeft());

            //Save state
            _self.saveColumnWidths();
            
            //Invoke colResize behavior
            if(_self.hasBehavior('colResize')) {
                var colResizeBehavior = _self.cfg.behaviors['colResize'];
                
                var ext = {
                    params: {}
                };
                ext.params[_self.id + '_columnId'] = columnHeader.attr('id');
                ext.params[_self.id + '_width'] = newWidth;
                ext.params[_self.id + '_height'] = columnHeader.height();
                
                colResizeBehavior.call(_self, event, ext);
                
            }
        },
        containment: this.jq
    });
    
    this.restoreColumnWidths();
}

PrimeFaces.widget.DataTable.prototype.saveColumnWidths = function() {
    var columnWidths = [],
    columnHeaders = this.cfg.scrollable ? this.jq.find('.ui-datatable-scrollable-header thead th') : this.jq.find('table thead th');
    
    columnHeaders.each(function(i, item) {
        columnWidths.push($(item).children('.ui-dt-c').width());
    });
    PrimeFaces.setCookie(this.columnWidthsCookie, columnWidths.join(','));
}

PrimeFaces.widget.DataTable.prototype.restoreColumnWidths = function() {
    var widths = PrimeFaces.getCookie(this.columnWidthsCookie),
    columnHeaders = this.cfg.scrollable ? this.jq.find('.ui-datatable-scrollable-header thead th') : this.jq.find('table thead th'),
    columnFooters = this.cfg.scrollable ? this.jq.find('.ui-datatable-scrollable-footer tfoot td') : this.jq.find('table tfoot td');
    
    if(widths) {
        widths = widths.split(',');
        for(var i = 0; i < widths.length; i++) {
            var width = widths[i];
            
            columnHeaders.eq(i).children('.ui-dt-c').width(width);
            $(this.jqId + ' tbody.ui-datatable-data').find('tr td:nth-child(' + (i + 1) + ')').children('.ui-dt-c').width(width);
            columnFooters.eq(i).children('.ui-dt-c').width(width);
        }
    }
}

PrimeFaces.widget.DataTable.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

/**
 * Remove given rowIndex from selection
 */
PrimeFaces.widget.DataTable.prototype.removeSelection = function(rowIndex) {
    var selection = this.selection;
    
    $.each(selection, function(index, value) {
        if(value === rowIndex) {
            selection.remove(index);
            
            return false;       //break
        } 
        else {
            return true;        //continue
        }
    });
}

/**
 * Adds given rowIndex to selection if it doesn't exist already
 */
PrimeFaces.widget.DataTable.prototype.addSelection = function(rowIndex) {
    if(!this.isSelected(rowIndex)) {
        this.selection.push(rowIndex);
    }
}

/**
 * Finds if given rowIndex is in selection
 */
PrimeFaces.widget.DataTable.prototype.isSelected = function(rowIndex) {
    var selection = this.selection,
    selected = false;
    
    $.each(selection, function(index, value) {
        if(value === rowIndex) {
            selected = true;
            
            return false;       //break
        } 
        else {
            return true;        //continue
        }
    });
    
    return selected;
}

PrimeFaces.widget.DataTable.prototype.getRowMeta = function(row) {
    var identifier = row.attr('id').split('_r_')[1].split('_'),
    meta = {
        index: identifier[0],
        key: identifier[1]
    };
    
    return meta;
}