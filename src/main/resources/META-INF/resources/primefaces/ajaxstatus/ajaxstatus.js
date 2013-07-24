/**
 * PrimeFaces AjaxStatus Widget
 */
PrimeFaces.widget.AjaxStatus = PrimeFaces.widget.BaseWidget.extend({
             
    init: function(cfg) {
        this._super(cfg);

        this.bind();
    },
            
    bind: function() {
        var doc = $(document),
        $this = this;
        
        doc.on('ajaxSend', function() {
            $this.trigger('start');
        })
        .on('ajaxError', function() {
            $this.trigger('error');
        })
        .on('ajaxSuccess', function() {
            $this.trigger('success');
        })
        .on('ajaxComplete', function() {
            $this.trigger('complete');
        });
        
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
        if(window.jsf && window.jsf.ajax) {
            jsf.ajax.addOnEvent(function(data) {
                var doc = $(document);

                if(data.status === 'begin') {
                    doc.trigger('ajaxSend');
                }
                else if(data.status === 'complete') {
                    doc.trigger('ajaxSuccess');
                }
                else if(data.status === 'success') {
                    doc.trigger('ajaxComplete');
                }
            });

            jsf.ajax.addOnError(function(data) {
                doc.trigger('ajaxError');
            });
        }
    }
    
});