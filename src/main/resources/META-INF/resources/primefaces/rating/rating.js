if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Rating = function(clientId, cfg) {
	this.clientId = clientId;
	var escapedClientId = PrimeFaces.escapeClientId(this.clientId);
	var ratingConfig = {};
	
	if(cfg.hasRateListener != undefined) {
		var xhrOptions = {partialSubmit:true,formClientId:cfg.formClientId};

		if(cfg.update != undefined) {
			ratingConfig.callback = function(value, link) {
				PrimeFaces.ajax.AjaxRequest(cfg.actionURL, xhrOptions, "update=" + cfg.update + "&rating=" + value + "&" + clientId + "=" + clientId);
			};
		}
		else {
			ratingConfig.callback = function(value, link) {
				PrimeFaces.ajax.AjaxRequest(cfg.actionURL, xhrOptions);
			};
		}
	}
	
	jQuery(escapedClientId + ' .pf-rating-star').rating(ratingConfig);	
}