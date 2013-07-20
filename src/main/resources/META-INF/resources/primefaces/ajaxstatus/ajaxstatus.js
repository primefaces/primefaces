/**
 * PrimeFaces AjaxStatus Widget
 */
PrimeFaces.widget.AjaxStatus = PrimeFaces.widget.BaseWidget.extend({
             
    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.ajaxStatus = this;
        
        this.bindToStandard();
    },
            
    trigger: function(event) {
        var callback = this.cfg[event];
        if(callback) {
            callback.call(document);
        }
        
        this.jq.children().hide().filter(this.jqId + '_' + event).show();
    },
         
    bindToStandard: function() {
        var $this = this;
        
        $(function() {
            if(window.jsf && window.jsf.ajax) {
                jsf.ajax.addOnEvent(function(data) {
                    var doc = $(document);

                    if(data.status === 'begin') {
                        $this.trigger('start');
                    }
                    else if(data.status === 'complete') {
                        $this.trigger('success');
                    }
                    else if(data.status === 'success') {
                        $this.trigger('complete');
                    }
                });

                jsf.ajax.addOnError(function(data) {
                    $this.trigger('error');
                });
            }
        });
    }
    
});