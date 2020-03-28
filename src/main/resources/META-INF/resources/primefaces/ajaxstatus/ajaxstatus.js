/**
 * __PrimeFaces AjaxStatus Widget__
 * 
 * AjaxStatus is a global notifier for AJAX requests.
 * 
 * @typedef {"default" | "start" | "success" | "error" | "complete"} PrimeFaces.widget.AjaxStatus.AjaxStatusEventType Available types of AJAX related
 * events to which you can listen.
 * 
 * @interface {PrimeFaces.widget.AjaxStatusCfg} cfg The configuration for the {@link  AjaxStatus| AjaxStatus widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.AjaxStatus = PrimeFaces.widget.BaseWidget.extend({

	/**
	 * @override
	 * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
	 */
    init: function(cfg) {
        this._super(cfg);

        this.bind();
    },

    /**
     * Listen to the relevant events on the document element.
     * 
     * @private
     */
    bind: function() {
        var doc = $(document),
        $this = this;

        doc.on('pfAjaxStart', function() {
            $this.trigger('start', arguments);
        })
        .on('pfAjaxError', function() {
            $this.trigger('error', arguments);
        })
        .on('pfAjaxSuccess', function() {
            $this.trigger('success', arguments);
        })
        .on('pfAjaxComplete', function() {
            $this.trigger('complete', arguments);
        });

        this.bindToStandard();
    },

    /**
     * Triggers the given event.
     * @param {PrimeFaces.widget.AjaxStatus.AjaxStatusEventType} event A name of one of the supported events that should be triggered
     * @param {any[]} args Arguments that are passed to the event handler (usually defined on the `<p:ajaxStatus/>` tag)
     */
    trigger: function(event, args) {
        var callback = this.cfg[event];
        if(callback) {
            callback.apply(document, args);
        }

        if (event !== 'complete' || this.jq.children().filter(this.toFacetId('complete')).length) {
            this.jq.children().hide().filter(this.toFacetId(event)).show();
        }
    },

    /**
     * Finds the facet ID of the given event.
     * @private
     * @param {PrimeFaces.widget.AjaxStatus.AjaxStatusEventType} event One of the supported event
     * @return {string} The ID of the facet element for the given event
     */
    toFacetId: function(event) {
        return this.jqId + '_' + event;
    },

    /**
     * If the global `jsf` object is available, forwards the standard AJAX events to the PrimeFaces AJAX status.
     * @private
     */
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
    }

});