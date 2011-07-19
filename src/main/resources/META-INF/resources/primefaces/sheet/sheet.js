/**
 * PrimeFaces Sheet Widget
 */
PrimeFaces.widget.Sheet = function(id, cfg) {
    this.id = id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.header = this.jq.children('.ui-sheet-header');
    this.body = this.jq.children('.ui-sheet-body');
    this.cfg = cfg;
    var _self = this;
    
    //sync body scroll with header
    this.body.scroll(function() {
        _self.header.scrollLeft(_self.body.scrollLeft());
    });
    
    
}