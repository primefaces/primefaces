PrimeFaces.ajax = {};

//backward compatibility
PrimeFaces.ajax.AjaxRequest = function(cfg, ext) {
	return PrimeFaces.ajax.Request.handleRequest(cfg, ext);
}

