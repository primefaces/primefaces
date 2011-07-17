/**
 * PrimeFaces TimeLine Widget
 */
PrimeFaces.widget.Timeline = function(id, cfg) {
    this.id = id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.cfg = cfg;
    this.band = this.jq.children('.ui-timeline-container');
    
    //set width of band
    var columnCount = this.band.children().length;
    this.band.width(columnCount * 320);
    
    //bind events
    this.jq.mousedown(function (event) {
        $(this)
        .data('down', true)
        .data('x', event.clientX)
        .data('scrollLeft', this.scrollLeft);
                
        return false;
    }).mouseup(function (event) {
        $(this).data('down', false);
    }).mousemove(function (event) {
        if ($(this).data('down') == true) {
            this.scrollLeft = $(this).data('scrollLeft') + $(this).data('x') - event.clientX;
        }
    });
}