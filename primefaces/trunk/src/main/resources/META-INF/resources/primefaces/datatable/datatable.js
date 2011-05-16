/**
 * PrimeFaces DataTable Widget
 */
PrimeFaces.widget.DataTable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
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
        this.setupExpansionEvents();
    }

    if(this.cfg.scrollable) {
        this.setupScrolling();
    }

    var rowEditors = this.getRowEditors();
    if(rowEditors.length > 0) {
        this.setupCellEditorEvents(rowEditors);
    }

    //Resizable columns
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

            if($(event.target).is(':not(th,span)')) {
                return;
            }

            var columnId = $(this).attr('id');
            //Reset previous sorted columns
            $(this).siblings().removeClass('ui-state-active').
                children('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');

            //Update sort state
            $(this).addClass('ui-state-active');
            var sortIcon = $(this).children('.ui-sortable-column-icon');
            
            if(sortIcon.hasClass('ui-icon-triangle-1-n')) {
                sortIcon.removeClass('ui-icon-triangle-1-n').addClass('ui-icon-triangle-1-s');

                _self.sort(columnId, false);
            }
            else if(sortIcon.hasClass('ui-icon-triangle-1-s')) {
                sortIcon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, true);
            } else {
                sortIcon.addClass('ui-icon-triangle-1-n');

                _self.sort(columnId, true);
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
        var selectEvent = this.cfg.dblclickSelect ? 'dblclick' : 'click',
        selector = this.isCellSelectionEnabled() ? this.jqId + ' tbody.ui-datatable-data tr td' : this.jqId + ' tbody.ui-datatable-data tr';

        $(selector)
            .css('cursor', 'pointer')
            .die()
            .live('mouseover', function() {
                var element = $(this);

                if(!element.hasClass('ui-selected')) {
                    element.addClass('ui-state-highlight');
                }

            })
            .live('mouseout', function() {
                var element = $(this);

                if(!element.hasClass('ui-selected')) {
                    element.removeClass('ui-state-highlight');
                }

            })
            .live(selectEvent, function(event) {
                if(this.nodeName == 'TR')
                    _self.onRowClick(event, this);
                else
                    _self.onCellClick(event, this);
            });
            
    }
    //Radio-Checkbox based rowselection
    else if(this.cfg.columnSelectionMode) {
        
        if(this.cfg.columnSelectionMode == 'single') {
            $(this.jqId + ' tbody.ui-datatable-data td.ui-selection-column input:radio')
                .die()
                .live('click', function() {
                    _self.selectRowWithRadio(this);
                });
        }
        else {
            $(this.jqId + ' tbody.ui-datatable-data td.ui-selection-column input:checkbox')
                .die()
                .live('click', function() {
                    _self.selectRowWithCheckbox(this);
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
    var bodyContainer = $(this.jqId + ' .ui-datatable-scrollable-body'),
    bodyContainerEl = bodyContainer.get(0),
    tbodyElement = $(this.tbody).get(0);
    
    var containerWidth = (bodyContainerEl.scrollHeight > bodyContainerEl.clientHeight) ? (tbodyElement.parentNode.clientWidth + 16) + "px" : (tbodyElement.parentNode.clientWidth - 1) + "px";
    $(this.jqId).css('width', containerWidth);

    //live scroll
    
    if(this.cfg.liveScroll) {
        this.scrollOffset = this.cfg.scrollStep;
        this.shouldLiveScroll = true;
        var _self = this;
        
        bodyContainer.scroll(function() {

            if(_self.shouldLiveScroll) {
                var viewport = $(this);
                var scrollTop = viewport.attr('scrollTop'),
                scrollHeight = viewport.attr('scrollHeight'),
                viewportHeight = viewport.attr('clientHeight');

                if(scrollTop >= (scrollHeight - (viewportHeight))) {
                    _self.loadLiveRows();
                }
            }
            
        });
        
    }
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
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                $(_self.jqId + ' .ui-datatable-scrollable-body table tr:last').after(content);

                _self.scrollOffset += _self.cfg.scrollStep;

                //Disable scroll if there is no more data left
                if(_self.scrollOffset == _self.cfg.scrollLimit) {
                    _self.shouldLiveScroll = false;
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
        }

        return false;
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
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                $(_self.tbody).replaceWith(content);

                _self.getPaginator().setState(newState);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_paging"] = true;
    params[this.id + "_first"] = newState.recordOffset;
    params[this.id + "_rows"] = newState.rowsPerPage;
    params[this.id + "_page"] = newState.page;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
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
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                $(_self.tbody).replaceWith(content);

                //reset paginator
                var paginator = _self.getPaginator();
                if(paginator) {
                   paginator.setPage(1, true);
                }

            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_sorting"] = true;
    params[this.id + "_sortKey"] = columnId;
    params[this.id + "_sortDir"] = asc;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
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
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        var paginator = _self.getPaginator();
        if(paginator) {
            var extensions = xmlDoc.getElementsByTagName("extension"),
            totalRecords = _self.getPaginator().getTotalRecords();

            for(var i=0; i < extensions.length; i++) {
                if(extensions[i].attributes.getNamedItem("primefacesCallbackParam").nodeValue == 'totalRecords') {
                    totalRecords = $.parseJSON(extensions[i].firstChild.data).totalRecords;

                    //Reset paginator state
                    paginator.setPage(1, true);
                    paginator.setTotalRecords(totalRecords, true);
                }
            }
        }

        for(i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                $(_self.tbody).replaceWith(content);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_filtering"] = true;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 * Row select handler
 *
 * - Unselects a row if it's already selected
 * - For single selection, clears previous selection
 */
PrimeFaces.widget.DataTable.prototype.onRowClick = function(event, rowElement) {
    
    //Check if rowclick triggered this event not an element in row content
    if($(event.target).is('td,span')) {
        
        var row = $(rowElement);

        if(row.hasClass('ui-selected'))
            this.unselectRow(row);
        else
           this.selectRow(row);
       
    }
    
}

PrimeFaces.widget.DataTable.prototype.selectRow = function(row) {
    var rowId = row.attr('id').split('_row_')[1];

    //unselect previous selection
    if(this.isSingleSelection()) {
        row.siblings('.ui-selected').removeClass('ui-selected ui-state-highlight'); 
        this.selection = [];
    }

    //add to selection
    row.addClass('ui-state-highlight ui-selected');
    this.selection.push(rowId);

    //save state
    this.writeSelections();

    if(this.cfg.instantSelect) {
        this.fireRowSelectEvent(rowId);
    }
}

PrimeFaces.widget.DataTable.prototype.unselectRow = function(row) {
    var rowId = row.attr('id').split('_row_')[1];

    //remove visual style
    row.removeClass('ui-selected ui-state-highlight');

    //remove from selection
    this.selection = $.grep(this.selection, function(r) {
        return r != rowId;
    });

    //save state
    this.writeSelections();

    if(this.cfg.instantUnselect) {
        this.fireRowUnselectEvent(rowId);
    }
}

/**
 *  Sends a rowSelectEvent on server side to invoke a rowSelectListener if defined
 */
PrimeFaces.widget.DataTable.prototype.fireRowSelectEvent = function(rowId) {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onRowSelectUpdate) {
        options.update = this.cfg.onRowSelectUpdate;
    }
    
    if(this.cfg.onRowSelectStart) 
        options.onstart = this.cfg.onRowSelectStart;
    if(this.cfg.onRowSelectComplete)
        options.oncomplete = this.cfg.onRowSelectComplete;
    
    var params = {};
    params[this.id + '_instantSelectedRowIndex'] = rowId;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 *  Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if defined
 */
PrimeFaces.widget.DataTable.prototype.fireRowUnselectEvent = function(rowId) {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onRowUnselectUpdate) {
        options.update = this.cfg.onRowUnselectUpdate;
    }

    var params = {};
    params[this.id + '_instantUnselectedRowIndex'] = rowId;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 *  Selects the corresping row of a radio based column selection
 */
PrimeFaces.widget.DataTable.prototype.selectRowWithRadio = function(radio) {
    var row = $(radio).parent().parent(),
    rowId = row.attr('id').split('_row_')[1];

    this.selection = [];
    this.selection.push(rowId);

    //save state
    this.writeSelections();
}

/**
 *  Selects the corresping row of a checkbox based column selection
 */
PrimeFaces.widget.DataTable.prototype.selectRowWithCheckbox = function(element) {
    var checkbox = $(element),
    row = checkbox.parent().parent(),
    rowId = row.attr('id').split('_row_')[1],
    checked = checkbox.attr('checked');

    if(checked) {
        //add to selection
        this.selection.push(rowId);

    } else {
        //remove from selection
        this.selection = $.grep(this.selection, function(r) {
            return r != rowId;
        });
        
    }

    //save state
    this.writeSelections();
}

/**
 * Selects all rows with checkbox
 */
PrimeFaces.widget.DataTable.prototype.toggleCheckAll = function(element) {
    var checkbox = $(element),
    checked = checkbox.attr('checked');

    this.clearSelection();

    if(checked) {
        $(this.jqId + ' tbody.ui-datatable-data td.ui-selection-column input:checkbox').attr('checked', true);

        if(this.getPaginator() != null) {
            for(var i=0; i < this.getPaginator().getTotalRecords(); i++) {
                this.selection.push(i);
            }

        } else {
            var _self = this;
            $(this.jqId + ' tbody.ui-datatable-data tr').each(function() {
                _self.selection.push($(this).attr('id').split('_row_')[1]);
            });
        }

    }
    else {
        $(this.jqId + ' tbody.ui-datatable-data td.ui-selection-column input:checkbox').attr('checked', false);
    }

    //save state
    this.writeSelections();
}

/**
 * Cell select handler
 *
 * - Unselects a cell if it's already selected
 * - For single cell selection, clears previous selection
 */
PrimeFaces.widget.DataTable.prototype.onCellClick = function(event, cellElement) {

    //Check if rowclick triggered this event not an element in row content
    if($(event.target).is('td,span')) {

        var cell = $(cellElement);

        if(cell.hasClass('ui-selected'))
            this.unselectCell(cell);
        else
           this.selectCell(cell);
    }
}

PrimeFaces.widget.DataTable.prototype.selectCell = function(cell) {
    var rowId = cell.parent().attr('id').split('_row_')[1],
    columnIndex = cell.index();

    //unselect previous selection
    if(this.cfg.selectionMode === 'singlecell') {
        $(this.jqId + ' tbody.ui-datatable-data td').removeClass('ui-selected ui-state-highlight');
        this.selection = [];
    }

    //add to selection
    cell.addClass('ui-state-highlight ui-selected');
    this.selection.push(rowId + '#' + columnIndex);

    //save state
    this.writeSelections();
}

PrimeFaces.widget.DataTable.prototype.unselectCell = function(cell) {
    var rowId = cell.parent().attr('id').split('_row_')[1],
    columnIndex = cell.index(),
    cellId = rowId + '#' + columnIndex;

    //remove visual style
    cell.removeClass('ui-selected ui-state-highlight');

    //remove from selection
    this.selection = $.grep(this.selection, function(c) {
        return c != cellId;
    });

    //save state
    this.writeSelections();
}

/**
 * Expands a row to display detail content
 */
PrimeFaces.widget.DataTable.prototype.toggleExpansion = function(expanderElement) {
    var expander = $(expanderElement),
    row = expander.parent().parent(),
    expanded = row.hasClass('ui-expanded-row');

    if(expanded) {
        expander.removeClass('ui-icon-circle-triangle-s');
        row.removeClass('ui-expanded-row');
        row.next().fadeOut(function() {
           $(this).remove();
        });
    }
    else {
        expander.addClass('ui-icon-circle-triangle-s');
        row.addClass('ui-expanded-row');

        this.loadExpandedRowContent(row);
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
    rowId = row.attr('id').split('_row_')[1],
    _self = this;

    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                row.after(content);
                row.next().fadeIn();
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + '_rowExpansion'] = true;
    params[this.id + '_expandedRowId'] = rowId;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 * Displays in-cell editors for given row
 */
PrimeFaces.widget.DataTable.prototype.showEditors = function(element) {
    $(element).parents('tr:first').addClass('ui-state-highlight').children('td.ui-editable-column').each(function() {
       var column = $(this);

       column.find('span.ui-cell-editor-output').hide();
       column.find('span.ui-cell-editor-input').show();

       $(element).hide();
       $(element).siblings().show();
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
    var row = $($(element).parents('tr').get(0)),
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
        editorsToProcess.push(rowEditorId);

        row.find('span.ui-cell-editor').each(function() {
           editorsToProcess.push($(this).attr('id'));
        });

        options.process = editorsToProcess.join(' ');

        //Additional components to update after row edit request
        if(this.cfg.onRowEditUpdate) {
            options.update += ' ' + this.cfg.onRowEditUpdate;
        }
    }

    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update"),
        extensions = xmlDoc.getElementsByTagName("extension");

        this.args = {};
        for(i=0; i < extensions.length; i++) {
            var extension = extensions[i];

            if(extension.getAttributeNode('primefacesCallbackParam')) {
                var jsonObj = $.parseJSON(extension.firstChild.data);

                for(var paramName in jsonObj) {
                    if(paramName)
                        this.args[paramName] = jsonObj[paramName];
                }
            }
        }

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

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
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
        }

        return false;
    };

    var params = {};
    params[rowEditorId] = rowEditorId;
    params[this.id + '_rowEdit'] = true;
    params[this.id + '_editedRowId'] = row.attr('id').split('_row_')[1];

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
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
 * Returns true|false if selection is enabled|disabled
 */
PrimeFaces.widget.DataTable.prototype.isCellSelectionEnabled = function() {
    return this.cfg.selectionMode === 'singlecell' || this.cfg.selectionMode === 'multiplecell';
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
    //Add resizers
    $(this.jqId + ' thead :not(th:last)').children('.ui-header-column').append('<div class="ui-column-resizer"></div>');

    //Setup resizing
    this.columnWidthsCookie = this.id + '_columnWidths',
    resizers = $(this.jqId + ' thead th div.ui-column-resizer'),
    columns = $(this.jqId + ' thead th'),
    _self = this;

    resizers.draggable({
        axis:'x',
        drag: function(event, ui) {
            var column = ui.helper.parents('th:first'),
            newWidth = ui.position.left + ui.helper.outerWidth();

            column.css('width', newWidth);
        },
        stop: function(event, ui) {
            ui.helper.css('left', '');
            
            var columnWidths = [];
            
            columns.each(function(i, item) {
                var columnHeader = $(item);
                columnWidths.push(columnHeader.css('width'));
            });
            PrimeFaces.setCookie(_self.columnWidthsCookie, columnWidths.join(','));
        }
    });

    resizers.css('height', $(this.jqId + ' thead').css('height'));

    //restore widths on postback
    var widths = PrimeFaces.getCookie(this.columnWidthsCookie);
    if(widths) {
        widths = widths.split(',');
        for(var i in widths) {
            $(columns.get(i)).css('width', widths[i]);
        }
    }
}