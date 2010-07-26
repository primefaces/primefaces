PrimeFaces.widget.IdleMonitor = function(clientId, cfg) {
	
	jQuery(document).bind("idle.idleTimer", function(){
		
		if(cfg.hasIdleListener) {
			var xhrOptions = {formId:cfg.formId};
			
			if(cfg.onidle) {
				xhrOptions.oncomplete = cfg.onidle;
			}
			
			var params = {};
			params[clientId] = clientId;
			params[PrimeFaces.PARTIAL_PROCESS_PARAM] = clientId;
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = cfg.update;

			PrimeFaces.ajax.AjaxRequest(cfg.actionURL, xhrOptions, params);
			
		} else if(cfg.onidle) {
			cfg.onidle();
		}
	});
	
	jQuery(document).bind("active.idleTimer", function(){
		if(cfg.onactive) {
			cfg.onactive();
		}
	});
    
    jQuery.idleTimer(cfg.timeout);
}