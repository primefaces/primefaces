PrimeFaces.widget.Poll = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.active = false;

    if(this.cfg.autoStart) {
        this.start();
    }
}

PrimeFaces.widget.Poll.prototype.start = function() {
    this.timer = setInterval(this.cfg.fn, (this.cfg.frequency * 1000));
    this.active = true;
}

PrimeFaces.widget.Poll.prototype.stop = function() {
    clearInterval(this.timer);
    this.active = false;
}

PrimeFaces.widget.Poll.prototype.handleComplete = function(xhr, status, args) {
    if(args.stop) {
        this.stop();
    }
}

PrimeFaces.widget.Poll.prototype.isActive = function() {
    return this.active;
}