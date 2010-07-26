if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.IdleMonitor = function(clientId, cfg) {
	
	jQuery(document).bind("idle.idleTimer", function(){
		
		if(cfg.hasIdleListener != undefined) {
			var xhrOptions = {partialSubmit:true,formClientId:cfg.formClientId};
			
			if(cfg.onidle != undefined) {
				xhrOptions.oncomplete = cfg.onidle;
			}
			
			if(cfg.update != undefined)
				PrimeFaces.ajax.AjaxRequest(cfg.actionURL, xhrOptions, "update=" + cfg.update + "&" + clientId + "=" + clientId);
			else
				PrimeFaces.ajax.AjaxRequest(cfg.actionURL, xhrOptions);
			
		} else if(cfg.onidle != null) {
			cfg.onidle();
		}
	});
	
	jQuery(document).bind("active.idleTimer", function(){
		if(cfg.onactive != null) {
			cfg.onactive();
		}
	});
    
    jQuery.idleTimer(cfg.timeout);
}