/**
 * PrimeFaces TagCloud Widget
 */
PrimeFaces.widget.TagCloud = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jq.find('li').mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        });
    }
    
});