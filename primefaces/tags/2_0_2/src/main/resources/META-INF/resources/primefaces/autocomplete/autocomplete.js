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
	
	if(this.cfg.pojo || this.cfg.ajaxSelect) {
		this.itemSelectEvent.subscribe(this.handleItemSelect);
	}
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
	
	handleItemSelect : function(sType, aArgs) {
        var item = aArgs[2],
        itemValue = item[1],
        inputField = this.cfg.pojo ? this.clientId + "_hinput" : this.clientId + "_input";
        
        if(this.cfg.pojo) {
        	document.getElementById(inputField).value = itemValue;
        }
        
        if(this.cfg.ajaxSelect) {
        	var params = {};
			params[this.clientId + "_ajaxSelect"] = true;
			params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.clientId;
			
			if(this.cfg.onSelectUpdate) {
				params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.onSelectUpdate;
			}
			
			PrimeFaces.ajax.AjaxRequest(this.cfg.url,{formId:this.cfg.formId}, params);
        }
    }
});