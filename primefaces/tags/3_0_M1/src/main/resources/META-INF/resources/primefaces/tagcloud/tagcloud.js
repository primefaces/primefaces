/**
 * PrimeFaces TagCloud Widget
 */
PrimeFaces.widget.TagCloud = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jq = jQuery(PrimeFaces.escapeClientId(this.id));

    this.jq.find('li').mouseover(function() {
        jQuery(this).addClass('ui-state-hover');
    }).mouseout(function() {
        jQuery(this).removeClass('ui-state-hover');
    })
}