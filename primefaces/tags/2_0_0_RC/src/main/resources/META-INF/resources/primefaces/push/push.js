if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.comet == undefined) PrimeFaces.comet = {};

PrimeFaces.comet.Subscriber = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	
	document.getElementById(id).setAttribute("src", this.cfg.channel);
}

PrimeFaces.comet.Subscriber.prototype.handlePublish = function(data) {
	this.cfg.onpublish(data);
}