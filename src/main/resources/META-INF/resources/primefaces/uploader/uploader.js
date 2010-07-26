PrimeFaces.widget.Uploader = function(id, cfg) {
	this.clientId = id;
	this.id = PrimeFaces.escapeClientId(id);
	this.cfg = cfg;

	this.cfg.scriptData = this.createPostParams();
	if(this.cfg.update != undefined) {
		this.cfg.onComplete = this.onComplete;
	}
	
	jQuery(this.id).uploadify(this.cfg);
}

PrimeFaces.widget.Uploader.prototype.upload = function() {
	jQuery(this.id).uploadifyUpload();
}

PrimeFaces.widget.Uploader.prototype.clear = function() {
	jQuery(this.id).uploadifyClearQueue();
}

PrimeFaces.widget.Uploader.prototype.onComplete = function(event,queueID, fileObj, response, data) {
	 var responseXML = {};
	 
	 if(window.DOMParser) {
		 responseXML.documentElement = new DOMParser().parseFromString(response,"text/xml");
	 }
	 else {
		 var xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
		 xmlDoc.async = "false";
		 xmlDoc.loadXML(response);
		 responseXML.documentElement = xmlDoc.documentElement;
	 }
	
	 PrimeFaces.ajax.AjaxResponse(responseXML);
	
	 return true;
}

PrimeFaces.widget.Uploader.prototype.createPostParams = function() {
	var jsessionid = YAHOO.util.Cookie.get("JSESSIONID"),
	params = {};
	
	params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.clientId;
	params['javax.faces.ViewState'] = PrimeFaces.ajax.AjaxUtils.encodeViewState();
	
	if(this.cfg.update) {
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.update;
	}
	
	if (this.cfg.script.indexOf('jsessionid') == -1) {
		this.cfg.script = this.cfg.script + ";jsessionid=" + jsessionid;
	}

	return params;
}