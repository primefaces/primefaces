if (PrimeFaces == undefined) var PrimeFaces = {};
if (PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Uploader = function(id, cfg) {
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
     responseXML.documentElement = new DOMParser().parseFromString(response,"text/xml");

     PrimeFaces.ajax.AjaxResponse(responseXML);
	
	return true;
}

PrimeFaces.widget.Uploader.prototype.createPostParams = function() {
	var jsessionid = YAHOO.util.Cookie.get("JSESSIONID"),
	params = {};
	
	params['primefacesAjaxRequest'] = true;
	if(this.cfg.update != undefined) {
		params['update'] = this.cfg.update;
	}
	
	if (this.cfg.script.indexOf('jsessionid') == -1) {
		this.cfg.script = this.cfg.script + ";jsessionid=" + jsessionid;
	}

	params['javax.faces.ViewState'] = PrimeFaces.ajax.AjaxUtils.encodeViewState();

	var myfacesFormSubmitMarker = this.cfg.formId + '_SUBMIT';
	if (document.getElementsByName(this.cfg.formId + '_SUBMIT').length == 1)
		params[myfacesFormSubmitMarker] = 1;
	else
		params[this.cfg.formId] = this.cfg.formId;

	return params;
}