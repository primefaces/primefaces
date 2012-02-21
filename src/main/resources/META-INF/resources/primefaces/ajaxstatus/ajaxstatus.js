/**
 * PrimeFaces AjaxStatus Widget
 */
PrimeFaces.widget.AjaxStatus = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
    },
    
    bindFacet: function(eventName, facetToShow) {
        var _self = this;

        $(document).bind(eventName, function() {
            $(_self.jqId).children().hide();

            $(_self.jqId + '_' + facetToShow).show();
        });
    }
    
    ,bindCallback: function(eventName, fn) {
        $(document).bind(eventName, fn);
    }
});