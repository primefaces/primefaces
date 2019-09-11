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
            var delay = $this.cfg.delay;
            if (delay > 0 ) {
                $this.timeout = setTimeout(function () {
                    $this.trigger('start', arguments);
                }, delay);
            } else {
                $this.trigger('start', arguments);
            }
        })
        .on('pfAjaxError', function() {
            $this.trigger('error', arguments);
        })
        .on('pfAjaxSuccess', function() {
            $this.trigger('success', arguments);
        })
        .on('pfAjaxComplete', function() {
            if($this.timeout) {
                $this.deleteTimeout();
            }
            $this.trigger('complete', arguments);
        });

        this.bindToStandard();
    },

    trigger: function(event, args) {
        var callback = this.cfg[event];
        if(callback) {
            callback.apply(document, args);
        }

        if (event !== 'complete' || this.jq.children().filter(this.toFacetId('complete')).length) {
            this.jq.children().hide().filter(this.toFacetId(event)).show();
        }
    },

    toFacetId: function(event) {
        return this.jqId + '_' + event;
    },

    bindToStandard: function() {
        if (window.jsf && window.jsf.ajax) {
            var doc = $(document);

            jsf.ajax.addOnEvent(function(data) {
                if(data.status === 'begin') {
                    doc.trigger('pfAjaxStart', arguments);
                }
                else if(data.status === 'complete') {
                    doc.trigger('pfAjaxSuccess', arguments);
                }
                else if(data.status === 'success') {
                    doc.trigger('pfAjaxComplete', arguments);
                }
            });

            jsf.ajax.addOnError(function(data) {
                doc.trigger('pfAjaxError', arguments);
            });
        }
    },

    deleteTimeout: function() {
        clearTimeout(this.timeout);
        this.timeout = null;
    }

});