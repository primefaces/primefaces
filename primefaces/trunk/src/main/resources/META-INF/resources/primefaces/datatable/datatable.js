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
    if(this.cfg.selectionMode) {
        this.selectionHolder = this.jqId + '_selection';
        this.selection = [];
    
        this.setupSelectionEvents();
    }

    if(this.cfg.expansion) {
        this.setupExpansionEvents();
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
    
    jQuery(this.jqId + ' th.ui-sortable-column').
        mouseover(function(){
            jQuery(this).toggleClass('ui-state-hover');
        })
        .mouseout(function(){
            jQuery(this).toggleClass('ui-state-hover');}
        )
        .click(function(event) {
            //Check if filter triggered this column header event
            if(event.target.tagName == 'INPUT') {
                return;
            }

            var columnId = jQuery(this).attr('id');
            //Reset previous sorted columns
            jQuery(this).siblings().removeClass('ui-state-active').
                children('.ui-sortable-column-icon').removeClass('ui-icon-triangle-1-n ui-icon-triangle-1-s');

            //Update sort state
            jQuery(this).addClass('ui-state-active');
            var sortIcon = jQuery(this).children('.ui-sortable-column-icon');
            
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
 * Applies events related to sorting in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupSelectionEvents = function() {
    var _self = this,
    selectEvent = this.cfg.dblclickSelect ? 'dblclick' : 'click';
    
    jQuery(this.jqId + ' .ui-datatable-data tr')
            .css('cursor', 'pointer')
            .mouseover(function() {
                var row = jQuery(this);

                if(!row.hasClass('ui-selected')) {
                    row.addClass('ui-state-highlight');
                }

            }).mouseout(function() {
                var row = jQuery(this);

                if(!row.hasClass('ui-selected')) {
                    row.removeClass('ui-state-highlight');
                }

            }).bind(selectEvent, function(event) {
                _self.onRowClick(this);
            });
}

/**
 * Applies events related to row expansion in a non-obstrusive way
 */
PrimeFaces.widget.DataTable.prototype.setupExpansionEvents = function() {
    var _self = this;

    jQuery(this.jqId + ' tbody tr td span.ui-row-expander')
            .click(function(event) {
                _self.toggleExpansion(this);
            });
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
                jQuery(_self.tbody).replaceWith(content);

                _self.getPaginator().setState(newState);

                //apply selection events
                if(_self.cfg.selectionMode) {
                    _self.setupSelectionEvents();
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_paging"] = true;
    params[this.id + "_first"] = newState.recordOffset;
    params[this.id + "_rows"] = newState.rowsPerPage;
    params[this.id + "_page"] = newState.page;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Ajax sort
 */
PrimeFaces.widget.DataTable.prototype.sort = function(columnId, asc) {
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
                jQuery(_self.tbody).replaceWith(content);

                //reset paginator
                var paginator = _self.getPaginator();
                if(paginator) {
                   paginator.setPage(1, true);
                }

                //apply selection events
                if(_self.cfg.selectionMode) {
                    _self.setupSelectionEvents();
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_sorting"] = true;
    params[this.id + "_sortKey"] = columnId;
    params[this.id + "_sortDir"] = asc;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Ajax filter
 */
PrimeFaces.widget.DataTable.prototype.filter = function() {
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
                    totalRecords = jQuery.parseJSON(extensions[i].firstChild.data).totalRecords;

                    //Reset paginator state
                    paginator.setPage(1);
                    paginator.setTotalRecords(totalRecords, true);
                }
            }
        }

        for(i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                jQuery(_self.tbody).replaceWith(content);

                //apply selection events
                if(_self.cfg.selectionMode) {
                    _self.setupSelectionEvents();
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + "_filtering"] = true;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Row select handler
 *
 * - Unselects a row if it's already selected
 * - For single selection, clears previous selection
 */
PrimeFaces.widget.DataTable.prototype.onRowClick = function(rowElement) {
    var row = jQuery(rowElement);

    if(row.hasClass('ui-selected')) {
        this.unselectRow(row);
    }
    else {
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
    row.addClass('ui-selected');
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
    this.selection = jQuery.grep(this.selection, function(r) {
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

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
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

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Expands a row to display detail content
 */
PrimeFaces.widget.DataTable.prototype.toggleExpansion = function(expanderElement) {
    var expander = jQuery(expanderElement),
    row = expander.parent().parent(),
    expanded = row.hasClass('ui-expanded-row');

    if(expanded) {
        expander.removeClass('ui-icon-circle-triangle-s');
        row.removeClass('ui-expanded-row');
        row.next().fadeOut(function() {
           jQuery(this).remove();
        });
    }
    else {
        expander.addClass('ui-icon-circle-triangle-s');
        row.addClass('ui-expanded-row');

        this.loadExpandedRowContent(row);
    }
    
}

PrimeFaces.widget.DataTable.prototype.loadExpandedRowContent = function(row) {
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
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
            }
        }

        return false;
    };

    var params = {};
    params[this.id + '_rowExpansion'] = true;
    params[this.id + '_expandedRowId'] = rowId;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Displays in-cell editors for given row
 */
PrimeFaces.widget.DataTable.prototype.showEditors = function(element) {
    jQuery(element).parents('tr').addClass('ui-state-highlight').children('td.ui-editable-cell').each(function() {
       var column = jQuery(this);

       column.children().hide();
       column.children('span.ui-cell-editor').show();

       jQuery(element).hide();
       jQuery(element).siblings().show();
    });
}

/**
 * Saves the edited row
 */
PrimeFaces.widget.DataTable.prototype.saveRowEdit = function(element) {
    var options = {
        source: this.id,
        process: this.id,
        update: this.id,
        formId: this.cfg.formId
    };

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options);
}

/**
 * Cancels row editing
 */
PrimeFaces.widget.DataTable.prototype.cancelRowEdit = function(element) {
    jQuery(element).parents('tr').removeClass('ui-state-highlight').children('td.ui-editable-cell').each(function() {
       var column = jQuery(this);

       column.children().show();
       column.children('span.ui-cell-editor').hide();

       jQuery(element).hide();
       jQuery(element).siblings().hide();
       jQuery(element).siblings('.ui-icon-pencil').show();
    });
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
    jQuery(this.selectionHolder).val(this.selection.join(','));
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
    
    jQuery(this.selectionHolder).val('');
}