if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Dialog = function(clientId, config) {
	PrimeFaces.widget.Dialog.superclass.constructor.call(this, clientId, config);
	this.clientId = clientId;
	this.config = config;
	
	if(this.config.resizable) {
		this.setupResizer();
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
		if(this.config.minWidth != undefined) 
			resizeConfig.minWidth =  this.config.minWidth;
		if(this.config.minHeight != undefined) 
			resizeConfig.minHeight =  this.config.minHeight;
		     
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
	}
});