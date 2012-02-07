/**
 * PrimeFaces DataList Widget
 */
PrimeFaces.widget.DataList = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.cfg.formId = $(this.jqId).parents('form:first').attr('id');
    this.content = this.jqId + '_content';

    if(this.cfg.paginator) {
        this.setupPaginator();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.DataList, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.DataList.prototype.setupPaginator = function() {
    var _self = this;
    this.cfg.paginator.paginate = function(newState) {
        _self.handlePagination(newState);
    };

    this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
}

PrimeFaces.widget.DataList.prototype.handlePagination = function(newState) {

    var _self = this,
    options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId,
        onsuccess: function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    $(_self.content).html(content);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }
            
            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        }
    };

    var params = {};
    params[this.id + "_ajaxPaging"] = true;
    params[this.id + "_first"] = newState.first;
    params[this.id + "_rows"] = newState.rows;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.DataList.prototype.getPaginator = function() {
    return this.paginator;
}