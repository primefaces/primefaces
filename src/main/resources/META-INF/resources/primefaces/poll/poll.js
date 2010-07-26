if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Poll = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.start();
}

PrimeFaces.widget.Poll.prototype.start = function() {
	var options = this.cfg,
	clientId = this.id;
	
	this.timer = setInterval(
			function() {
				PrimeFaces.ajax.AjaxRequest(options.url, 
							{
								formClientId:options.formClientId, 
								partialSubmit:options.partialSubmit,
								onstart:options.onstart,
								oncomplete:options.oncomplete,
							}
							,"update=" + options.update + "&" + clientId + "=" + clientId);
		}
		,(options.frequency * 1000));
}

PrimeFaces.widget.Poll.prototype.stop = function() {
	clearInterval(this.timer);
}