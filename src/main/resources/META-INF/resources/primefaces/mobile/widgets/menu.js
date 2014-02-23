/**
 * PrimeFaces Mobile Menu Widget
 */
PrimeFaces.widget.PlainMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.items = this.jq.children('li');
        this.items.filter(':first-child').addClass('ui-first-child');
        this.items.filter(':last-child').addClass('ui-last-child');
    }
});

/**
 * PrimeFaces Mobile TabMenu Widget
 */
PrimeFaces.widget.TabMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.links = this.jq.find('a.ui-link');
        this.links.eq(this.cfg.activeIndex).addClass('ui-btn-active');
        
        this.jq.navbar();
    }
});