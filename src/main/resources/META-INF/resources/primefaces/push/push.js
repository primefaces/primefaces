PrimeFaces.widget.Subscriber = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	
	document.getElementById(id).setAttribute("src", this.cfg.channel);
}

PrimeFaces.widget.Subscriber.prototype.handlePublish = function(data) {
	this.cfg.onpublish(data);
}