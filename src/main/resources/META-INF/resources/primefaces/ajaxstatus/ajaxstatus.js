/**
 * __PrimeFaces AjaxStatus Widget__
 * 
 * AjaxStatus is a global notifier for AJAX requests.
 * 
 * @prop {number | null} timeout The set-timeout timer ID for the timer of the delay before the AJAX status is
 * triggered.
 * 
 * @typedef {"default" | "start" | "success" | "error" | "complete"} PrimeFaces.widget.AjaxStatus.AjaxStatusEventType Available types of AJAX related
 * events to which you can listen.
 * 
 * @interface {PrimeFaces.widget.AjaxStatusCfg} cfg The configuration for the {@link  AjaxStatus| AjaxStatus widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {number} delay Delay in milliseconds before displaying the AJAX status. Default is `0`, meaning immediate.
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

        // also bind to JSF (f:ajax) events
        // NOTE: PF always fires "complete" as last event, whereas JSF last events are either "success" or "error"
        if (window.jsf && jsf.ajax) {
            jsf.ajax.addOnEvent(function(data) {
                if(data.status === 'begin') {
                    var delay = $this.cfg.delay;
                    if (delay > 0 ) {
                        $this.timeout = setTimeout(function () {
                            $this.trigger('start', arguments);
                        }, delay);
                    } else {
                        $this.trigger('start', arguments);
                    }
                }
                else if(data.status === 'complete') {

                }
                else if(data.status === 'success') {
                    if($this.timeout) {
                        $this.deleteTimeout();
                    }
                    $this.trigger('success', arguments);
                    $this.trigger('complete', arguments);
                }
            });

            jsf.ajax.addOnError(function(data) {
                if($this.timeout) {
                    $this.deleteTimeout();
                }
                $this.trigger('error', arguments);
                $this.trigger('complete', arguments);
            });
        }
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
     * Clears the ste-timeout timer for the delay.
     * @private
     */
    deleteTimeout: function() {
        clearTimeout(this.timeout);
        this.timeout = null;
    }

});