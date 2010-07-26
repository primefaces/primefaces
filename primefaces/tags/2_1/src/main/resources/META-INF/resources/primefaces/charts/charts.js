PrimeFaces.widget.ChartUtils = {
	
	createPollingParams : function(chartId) {
		var viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
	
		var params = PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + chartId;
		params = params + "&" + PrimeFaces.PARTIAL_REQUEST_PARAM + "=true";
		params = params + "&javax.faces.ViewState=" + viewstate;
		
		return params;
	},
	
	itemSelectHandler: function(event, cfg) {
		var requestConfig = {formId : cfg.formId};
		
		var params = {};
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = cfg.clientId;
		params['itemIndex'] = event.index;
		params['seriesIndex'] = event.seriesIndex;
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = cfg.update;
		params[cfg.clientId] = cfg.clientId;
		
		if(cfg.oncomplete) {
			requestConfig.oncomplete = cfg.oncomplete;
		}
		
		PrimeFaces.ajax.AjaxRequest(cfg.url, requestConfig, params);
	}
};