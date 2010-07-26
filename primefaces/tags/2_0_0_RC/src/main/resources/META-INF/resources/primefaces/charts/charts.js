if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.ChartUtils = {
	
	createPollingParams : function(chartId) {
		var viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
	
		var params = "ajaxSource=" + chartId;
		params = params + "&primefacesAjaxRequest=true";
		params = params + "&javax.faces.ViewState=" + viewstate;
		
		return params;
	},
	
	itemSelectHandler: function(event, cfg) {
		
		var requestConfig = {
			partialSubmit : true,
			formClientId : cfg.formClientId
		};
		
		if(cfg.oncomplete != undefined) {
			requestConfig.oncomplete=cfg.oncomplete;
		}
		
		var params = cfg.clientId + "=" + cfg.clientId;
		params = params + "&seriesIndex=" + event.index;
		params = params + "&itemIndex=" + event.index;
		params = params + "&update=" + cfg.update;
		
		PrimeFaces.ajax.AjaxRequest(cfg.url, requestConfig, params);
	}
};