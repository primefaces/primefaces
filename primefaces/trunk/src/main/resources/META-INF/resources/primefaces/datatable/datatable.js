PrimeFaces.widget.DataTable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.data = this.jqId + '_data';

    if(this.cfg.paginator) {
        this.setupPaginator();
    }

    this.setupSortableColumns();
}

PrimeFaces.widget.DataTable.prototype.setupPaginator = function() {
    var paginator = this.getPaginator();

    paginator.subscribe('changeRequest', this.paginate, null, this);
    paginator.render();
}

PrimeFaces.widget.DataTable.prototype.setupSortableColumns = function() {
    var _self = this;
    
    jQuery(this.jqId + ' th.ui-sortable-column').
        mouseover(function(){
            jQuery(this).toggleClass('ui-state-hover');
        })
        .mouseout(function(){
            jQuery(this).toggleClass('ui-state-hover');}
        )
        .click(function(event) {
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
                jQuery(_self.data).replaceWith(content);

                _self.getPaginator().setState(newState);
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
                jQuery(_self.data).replaceWith(content);

                //reset paginator
                var paginator = _self.getPaginator();
                if(paginator) {
                   paginator.setPage(1, true);
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

                    paginator.setPage(1);
                    paginator.setTotalRecords(totalRecords, true);
                }
            }
        }

        for(i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                jQuery(_self.data).replaceWith(content);
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

PrimeFaces.widget.DataTable.prototype.getPaginator = function() {
    return this.cfg.paginator;
}