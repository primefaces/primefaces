PrimeFaces.widget.Rating = function(clientId, cfg) {
	this.clientId = clientId;
	var escapedClientId = PrimeFaces.escapeClientId(this.clientId);
	var ratingConfig = {};
	
	if(cfg.hasRateListener) {		
		ratingConfig.callback = function(value, link) {
			var params = {};
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = cfg.update;
			params[PrimeFaces.PARTIAL_PROCESS_PARAM] = clientId;
			
			PrimeFaces.ajax.AjaxRequest(cfg.actionURL, {formId:cfg.formId}, params);
		};
	}
	
	jQuery(escapedClientId + ' .pf-rating-star').rating(ratingConfig);	
}