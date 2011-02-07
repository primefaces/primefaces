/* PrimeFaces ScrollPanel Widget */
PrimeFaces.widget.ScrollPanel = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    this.jq.wijsuperpanel(this.cfg);
}