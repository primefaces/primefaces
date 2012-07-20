/**
 * PrimeFaces AjaxStatus Widget
 */
PrimeFaces.widget.AjaxStatus = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.bindToCore();
    },
    
    bindFacet: function(eventName, facetToShow) {
        var _self = this;

        $(document).bind(eventName, function() {
            $(_self.jqId).children().hide();

            $(_self.jqId + '_' + facetToShow).show();
        });
    },
    
    bindCallback: function(eventName, fn) {
        $(document).bind(eventName, fn);
    },
    
    bindToCore: function() {
        $(function() {
            if(window.jsf && window.jsf.ajax) {
                jsf.ajax.addOnEvent(function(data) {
                    var doc = $(document);

                    if(data.status == 'begin') {
                        doc.trigger('ajaxStart');
                    }
                    else if(data.status == 'complete') {
                        doc.trigger('ajaxSuccess');
                    }
                    else if(data.status == 'success') {
                        doc.trigger('ajaxComplete');
                    }
                });

                jsf.ajax.addOnError(function(data) {
                    $(document).trigger('ajaxError');
                });
            }
        });
    }
    
});
