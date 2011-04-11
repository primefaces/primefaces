/* PrimeFaces ScrollPanel Widget */
PrimeFaces.widget.ScrollPanel = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    this.cfg.hScroller = {
        scrollMode : this.cfg.scrollMode
    };

    this.cfg.vScroller = {
        scrollMode : this.cfg.scrollMode
    };

    this.cfg.animationOptions = {
        easing : this.cfg.easing
    };

    this.jq.wijsuperpanel(this.cfg);
}

PrimeFaces.widget.ScrollPanel.prototype.scrollTo = function(x, y) {
    this.jq.wijsuperpanel('scrollTo', x, y);
}