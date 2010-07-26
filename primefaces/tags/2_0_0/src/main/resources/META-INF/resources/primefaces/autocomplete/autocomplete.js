PrimeFaces.widget.AutoComplete = function(clientId, config) {
	this.clientId = clientId;
	
	var datasource = new YAHOO.util.XHRDataSource(config.url);
	datasource.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;;
	datasource.connMethodPost = true;
	datasource.responseSchema = {
					resultsList : "ResultSet.Result",
					fields: config.fields
	};

	PrimeFaces.widget.AutoComplete.superclass.constructor.call(this, clientId + "_input", clientId + "_container", datasource, {});
	this.queryQuestionMark = false;
	
	if(config.fields.length > 1)
		this.itemSelectEvent.subscribe(this.itemSelectHandler);
}

YAHOO.lang.extend(PrimeFaces.widget.AutoComplete, YAHOO.widget.AutoComplete, {
	
	generateRequest : function(query) {
		var viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		
		return "&autoCompleteQuery=" + query + "&" + PrimeFaces.PARTIAL_REQUEST_PARAM + "=true&" + PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + this.clientId + "&javax.faces.ViewState=" + viewstate;
	},
	
    itemSelectHandler : function(sType, aArgs) {
        var item = aArgs[2];
        
        document.getElementById(this.clientId + "_hinput").value = item[1];
    }
});