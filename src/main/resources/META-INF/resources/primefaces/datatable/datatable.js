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
    var _self = this;
    
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

            }).click(function(event) {
                _self.onRowClick(this);
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

    this.writeSelections();
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
}

PrimeFaces.widget.DataTable.prototype.unselectRow = function(row) {
    var rowId = row.attr('id').split('_row_')[1];

    //remove visual style
    row.removeClass('ui-selected ui-state-highlight');

    //remove from selection
    this.selection = jQuery.grep(this.selection, function(r) {
        return r != rowId;
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