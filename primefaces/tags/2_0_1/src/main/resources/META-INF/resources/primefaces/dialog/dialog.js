PrimeFaces.widget.Dialog = function(clientId, cfg) {
	PrimeFaces.widget.Dialog.superclass.constructor.call(this, clientId, cfg);
	this.clientId = clientId;
	
	if(this.cfg.initialConfig.resizable) {
		this.setupResizer();
	}
	
	if(this.cfg.initialConfig.ajaxclose) {
		this.hideEvent.subscribe(this.handleAjaxClose, true);
	}
}

YAHOO.lang.extend(PrimeFaces.widget.Dialog, YAHOO.widget.Panel,
{
	setupResizer : function() {
		var resizeConfig = {
			handles: ["br"],
	        autoRatio: false,
	        status: false
		};
		if(this.cfg.initialConfig.minwidth != undefined) 
			resizeConfig.minWidth =  this.cfg.initialConfig.minwidth;
		if(this.cfg.initialConfig.minheight != undefined) 
			resizeConfig.minHeight =  this.cfg.initialConfig.minheight;
		     
		var resize = new YAHOO.util.Resize(this.clientId, resizeConfig);
		
        resize.on("startResize", function(args) {

		    if(this.cfg.getProperty("constraintoviewport")) {
                var D = YAHOO.util.Dom;

                var clientRegion = D.getClientRegion();
                var elRegion = D.getRegion(this.element);

                resize.set("maxWidth", clientRegion.right - elRegion.left - YAHOO.widget.Overlay.VIEWPORT_OFFSET);
                resize.set("maxHeight", clientRegion.bottom - elRegion.top - YAHOO.widget.Overlay.VIEWPORT_OFFSET);
            } else {
                resize.set("maxWidth", null);
                resize.set("maxHeight", null);
        	}

        }, this, true);

        resize.on("resize", function(args) {
            var panelHeight = args.height;
            this.cfg.setProperty("height", panelHeight + "px");
        }, this, true);
	},
	
	handleAjaxClose : function() {
		var params = {};
		params[this.clientId + "_closed"] = true;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.clientId;
		
		if(this.cfg.initialConfig.oncloseupdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.initialConfig.oncloseupdate;
		}
		
		PrimeFaces.ajax.AjaxRequest(this.cfg.initialConfig.url, {}, params);
	}
});