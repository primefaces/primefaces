PrimeFaces.widget.Poll = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
	
    if(this.cfg.autoStart) {
        this.start();
    }
}

PrimeFaces.widget.Poll.prototype.start = function() {
    this.timer = setInterval(this.cfg.fn, (this.cfg.frequency * 1000));
}

PrimeFaces.widget.Poll.prototype.stop = function() {
    clearInterval(this.timer);
}