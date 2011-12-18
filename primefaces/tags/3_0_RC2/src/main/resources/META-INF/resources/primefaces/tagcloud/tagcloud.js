/**
 * PrimeFaces TagCloud Widget
 */
PrimeFaces.widget.TagCloud = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jq = $(PrimeFaces.escapeClientId(this.id));

    this.jq.find('li').mouseover(function() {
        $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    });
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.TagCloud, PrimeFaces.widget.BaseWidget);