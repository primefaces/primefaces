PrimeFaces.widget.Poll = function(id, cfg, params) {
	this.id = id;
	this.cfg = cfg;
	this.params = params;
	
	this.start();
}

PrimeFaces.widget.Poll.prototype.start = function() {
	var scope = this;
	
	this.timer = setInterval(
			function() {
				PrimeFaces.ajax.AjaxRequest(scope.cfg.url, scope.cfg, scope.params);
			}
			,(this.cfg.frequency * 1000));
}

PrimeFaces.widget.Poll.prototype.stop = function() {
	clearInterval(this.timer);
}