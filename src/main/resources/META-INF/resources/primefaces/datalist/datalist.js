PrimeFaces.widget.DataList = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.content = this.jqId + '_content';
	
    if(this.cfg.paginator) {
        this.setupPaginator();
    }
}

PrimeFaces.widget.DataList.prototype.setupPaginator = function() {
    var paginator = this.getPaginator();

    paginator.subscribe('changeRequest', this.handlePagination, null, this);
    paginator.render();
}

PrimeFaces.widget.DataList.prototype.handlePagination = function(newState) {
    var params = {};
    params[this.id + "_ajaxPaging"] = true;
    params[this.id + "_first"] = newState.recordOffset;
    params[this.id + "_rows"] = newState.rowsPerPage;
    params[this.id + "_page"] = newState.page;

    var _self = this;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url,
            {
                source: this.id,
                update: this.id,
                process: this.id,
                formId: this.cfg.formId,
                onsuccess: function(responseXML) {
                    var xmlDoc = responseXML.documentElement,
                    updates = xmlDoc.getElementsByTagName("update");

                    for(var i=0; i < updates.length; i++) {
                        var id = updates[i].attributes.getNamedItem("id").nodeValue,
                        data = updates[i].firstChild.data;

                        if(id == PrimeFaces.VIEW_STATE) {
                            PrimeFaces.ajax.AjaxUtils.updateState(data);
                        }
                        else if(id == _self.id){

                            if(_self.cfg.effect) {
                                var _data = data;
                                jQuery(_self.content).fadeOut(_self.cfg.effectSpeed, function() {
                                    jQuery(_self.content).html(_data);
                                    jQuery(_self.content).fadeIn(_self.cfg.effectSpeed);
                                });
                            } else {
                                jQuery(_self.content).html(data);
                            }

                            _self.getPaginator().setState(newState);

                        }
                        else {
                            jQuery(PrimeFaces.escapeClientId(id)).replaceWith(data);
                        }
                    }

                    return false;
                }
            },
            params);
}

PrimeFaces.widget.DataList.prototype.getPaginator = function() {
    return this.cfg.paginator;
}