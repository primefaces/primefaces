PrimeFaces.widget.AutoComplete = function(clientId, cfg) {
	this.clientId = clientId;
	this.cfg = cfg;
	
	var datasource = new YAHOO.util.XHRDataSource(this.cfg.url);
	datasource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
	datasource.connMethodPost = true;
	datasource.responseSchema = {
		resultsList : "ResultSet.Result",
		fields: this.cfg.fields
	};

	PrimeFaces.widget.AutoComplete.superclass.constructor.call(this, clientId + "_input", clientId + "_container", datasource, {});
	this.queryQuestionMark = false;
	
	if(this.cfg.fields.length > 1)
		this.itemSelectEvent.subscribe(this.itemSelectHandler);
}

YAHOO.lang.extend(PrimeFaces.widget.AutoComplete, YAHOO.widget.AutoComplete, {
	
	generateRequest : function(query) {
		var params = {};
		params["javax.faces.ViewState"] = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		params[this.clientId + "_query"] = query;
		params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
		params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.clientId;
		
		return PrimeFaces.ajax.AjaxUtils.serialize(params);
	},
	
    itemSelectHandler : function(sType, aArgs) {
        var item = aArgs[2];
        
        document.getElementById(this.clientId + "_hinput").value = item[1];
    }
});