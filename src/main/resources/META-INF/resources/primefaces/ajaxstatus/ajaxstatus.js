/**
 * __PrimeFaces AjaxStatus Widget__
 * 
 * AjaxStatus is a global notifier for AJAX requests.
 * 
 * For the callbacks that can be set via the `onstart`, `onsuccess`, `onerror` and `oncomplete` attributes, see
 * {@link PfAjaxStartCallback}, {@link PfAjaxSuccessCallback}, {@link PfAjaxErrorCallback}, and
 * {@link PfAjaxCompleteCallback}.
 * 
 * @typedef {"start" | "success" | "error" | "complete"} PrimeFaces.widget.AjaxStatus.AjaxStatusEventType Available
 * types of AJAX related events to which you can listen.
 * 
 * @typedef PrimeFaces.widget.AjaxStatus.PfAjaxStartCallback Callback for when an AJAX request starts. Usually set via
 * `<p:ajaxStatus onstart="..."/>`. This callback applies when `<p:ajax />` is used.
 * @this {Document} PrimeFaces.widget.AjaxStatus.PfAjaxStartCallback
 * 
 * @typedef PrimeFaces.widget.AjaxStatus.PfAjaxErrorCallback Callback for when an AJAX request fails. Usually set via
 * `<p:ajaxStatus onerror="..."/>`. This callback applies when `<p:ajax />` is used.
 * @this {Document} PrimeFaces.widget.AjaxStatus.PfAjaxErrorCallback
 * @param {JQuery.jqXHR} PrimeFaces.widget.AjaxStatus.PfAjaxErrorCallback.xhr The request that failed.
 * @param {JQuery.AjaxSettings} PrimeFaces.widget.AjaxStatus.PfAjaxErrorCallback.settings The settings of the jQuery
 * AJAX request.
 * @param {string} PrimeFaces.widget.AjaxStatus.PfAjaxErrorCallback.errorThrown The error that cause the request to
 * fail.

 * @typedef PrimeFaces.widget.AjaxStatus.PfAjaxSuccessCallback Callback for when an AJAX request succeeds. Usually set
 * via `<p:ajaxStatus onsuccess="..."/>`. This callback applies when `<p:ajax />` is used.
 * @this {Document} PrimeFaces.widget.AjaxStatus.PfAjaxSuccessCallback
 * @param {JQuery.jqXHR} PrimeFaces.widget.AjaxStatus.PfAjaxSuccessCallback.xhr The request that succeeded.
 * @param {JQuery.AjaxSettings} PrimeFaces.widget.AjaxStatus.PfAjaxSuccessCallback.settings The settings of the jQuery
 * AJAX request.
 * 
 * @typedef PrimeFaces.widget.AjaxStatus.PfAjaxCompleteCallback Callback for when an AJAX request completes, either
 * successfully or with an error. Usually set via `<p:ajaxStatus oncomplete="..."/>`. This callback applies when
 * `<p:ajax />` is used.
 * @this {Document} PrimeFaces.widget.AjaxStatus.PfAjaxCompleteCallback
 * @param {JQuery.jqXHR} PrimeFaces.widget.AjaxStatus.PfAjaxCompleteCallback.xhr The request that succeeded.
 * @param {JQuery.AjaxSettings} PrimeFaces.widget.AjaxStatus.PfAjaxCompleteCallback.settings The settings of the jQuery
 * AJAX request.
 * 
 * @interface {PrimeFaces.widget.AjaxStatus.EventToCallbackMap} EventToCallbackMap Maps between the
 * {@link AjaxStatusEventType} and the corresponding event handlers. Used by the {@link AjaxStatus} component.
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxCompleteCallback | jsf.ajax.OnEventCallback | jsf.ajax.OnErrorCallback} EventToCallbackMap.complete
 * Callback for when an AJAX request completes, either successfully or with an error. Usually set via
 * `<p:ajaxStatus oncomplete="..."/>`.
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxErrorCallback | jsf.ajax.OnErrorCallback} EventToCallbackMap.error Callback
 * for when an AJAX request fails. Usually set via `<p:ajaxStatus onerror="..."/>`.
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxStartCallback | jsf.ajax.OnEventCallback} EventToCallbackMap.start Callback
 * for when an AJAX request starts. Usually set via `<p:ajaxStatus onstart="..."/>`.
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxSuccessCallback | jsf.ajax.OnEventCallback} EventToCallbackMap.success
 * Callback for when an AJAX request succeeds. Usually set via `<p:ajaxStatus onsuccess="..."/>`.
 * 
 * @prop {number | null} timeout The set-timeout timer ID for the timer of the delay before the AJAX status is
 * triggered.
 * 
 * @interface {PrimeFaces.widget.AjaxStatusCfg} cfg The configuration for the {@link  AjaxStatus| AjaxStatus widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxCompleteCallback | jsf.ajax.OnEventCallback | jsf.ajax.OnErrorCallback} cfg.complete
 * Client-side callback for when the AJAX behavior completes, i.e. when the request finishes, irrespective of whether it
 * succeeded or failed. 
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxErrorCallback | jsf.ajax.OnErrorCallback} cfg.error Client-side callback
 * for when the AJAX behavior fails, i.e. when the request fails.
 * @prop {number} cfg.delay Delay in milliseconds before displaying the AJAX status. Default is `0`, meaning immediate.
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxStartCallback | jsf.ajax.OnEventCallback} start Client-side callback for
 * when the AJAX behavior starts, i.e. the request is about to be sent.
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxSuccessCallback | jsf.ajax.OnEventCallback} cfg.success Client-side
 * callback for when the AJAX  behavior completes successfully, i.e. when the request succeeds.
 */
PrimeFaces.widget.AjaxStatus = PrimeFaces.widget.BaseWidget.extend({

	/**
	 * @override
	 * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
	 */
    init: function(cfg) {
        this._super(cfg);

        this.bind();
    },

    /**
     * Listen to the relevant events on the document element.
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
     * Triggers the given event by invoking the event handler, usually defined on the `<p:ajaxStatus/>` tag.
     * @template {PrimeFaces.widget.AjaxStatus.AjaxStatusEventType} K A name of one of the supported events that should
     * be triggered.
     * @param {K} event A name of one of the supported events that should
     * be triggered.
     * @param {Parameters<PrimeFaces.widget.AjaxStatus.EventToCallbackMap[K]>} args Arguments that are passed to the
     * event handler.
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