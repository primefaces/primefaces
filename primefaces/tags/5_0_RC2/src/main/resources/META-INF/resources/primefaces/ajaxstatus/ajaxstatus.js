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
        
        doc.on('pfAjaxStart', function() {
            $this.trigger('start');
        })
        .on('pfAjaxError', function() {
            $this.trigger('error');
        })
        .on('pfAjaxSuccess', function() {
            $this.trigger('success');
        })
        .on('pfAjaxComplete', function() {
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
        	var doc = $(document);

        	jsf.ajax.addOnEvent(function(data) {
                if(data.status === 'begin') {
                    doc.trigger('pfAjaxStart');
                }
                else if(data.status === 'complete') {
                    doc.trigger('pfAjaxSuccess');
                }
                else if(data.status === 'success') {
                    doc.trigger('pfAjaxComplete');
                }
            });

            jsf.ajax.addOnError(function(data) {
                doc.trigger('pfAjaxError');
            });
        }
    }
    
});