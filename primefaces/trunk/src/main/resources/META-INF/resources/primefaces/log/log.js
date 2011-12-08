/**
 * PrimeFaces Log Widget
 */
PrimeFaces.widget.Log = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.itemsContainer = this.jq.children('.ui-log-items');

    PrimeFaces.logger = this;
}

PrimeFaces.widget.Log.prototype.info = function(msg) {
    this.itemsContainer.append('<li class="ui-log-item">' + msg + '</li>');
}

PrimeFaces.widget.Log.prototype.warn = function(msg) {
    this.itemsContainer.append('<li class="ui-log-item">' + msg + '</li>');
}

PrimeFaces.widget.Log.prototype.debug = function(msg) {
    this.itemsContainer.append('<li class="ui-log-item">' + msg + '</li>');
}

PrimeFaces.widget.Log.prototype.error = function(msg) {
    this.itemsContainer.append('<li class="ui-log-item">' + msg + '</li>');
}