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
 * @prop {boolean} hasSuccessOrErrorFacet True if this component contains a success/error facet.
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
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxStartCallback | jsf.ajax.OnEventCallback} cfg.start Client-side callback
 * for when the AJAX behavior starts, i.e. the request is about to be sent.
 * @prop {PrimeFaces.widget.AjaxStatus.PfAjaxSuccessCallback | jsf.ajax.OnEventCallback} cfg.success Client-side
 * callback for when the AJAX  behavior completes successfully, i.e. when the request succeeds.
 */
PrimeFaces.widget.AjaxStatus = class AjaxStatus extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.hasSuccessOrErrorFacet = false;

        this.bind();
    }

    /**
     * Binds event handlers to AJAX events on the document element.
     * Handles both PrimeFaces AJAX events and native JSF AJAX events.
     * 
     * For PrimeFaces AJAX events:
     * - pfAjaxStart: Triggered when AJAX request starts. After configured delay, triggers 'start' event.
     * - pfAjaxError: Triggered when AJAX request fails. Triggers 'error' event with xhr, settings, error details.
     * - pfAjaxSuccess: Triggered when AJAX request succeeds. Triggers 'success' event with xhr, settings.
     * - pfAjaxComplete: Triggered after success/error. Clears timeout if no redirect. Triggers 'complete' event.
     * 
     * For JSF AJAX events:
     * - begin: Triggers 'start' event after configured delay
     * - complete: IGNORED since it fires before success/error
     * - success: Clears timeout, triggers 'success' then 'complete' events
     * - error: Logs error, clears timeout, triggers 'error' then 'complete' events
     * 
     * Events are namespaced with component ID for cleanup.
     * Configured delay controls timing of showing AJAX status.
     * Cleanup is handled via destroy listener that removes document event handlers.
     * @private
     */
    bind() {
        var $this = this;
        var namespace = '.status' + this.id;

        // Handle start of AJAX request
        $(document).on('pfAjaxStart' + namespace, function() {
            // Queue task to trigger start event after configured delay
            $this.timeout = PrimeFaces.queueTask(function() {
                $this.trigger('start', arguments);
            }, $this.cfg.delay);
        })
        // Handle AJAX error
        .on('pfAjaxError' + namespace, function(e, xhr, settings, error) {
            $this.trigger('error', [xhr, settings, error]);
        })
        // Handle AJAX success
        .on('pfAjaxSuccess' + namespace, function(e, xhr, settings) {
            $this.trigger('success', [xhr, settings]);
        })
        // Handle AJAX completion (after success/error)
        .on('pfAjaxComplete' + namespace, function(e, xhr, settings, args) {
            if($this.timeout && args && !args.redirect) {
                $this.deleteTimeout();
            }
            $this.trigger('complete', [xhr, settings, args]);
        });
        this.addDestroyListener(function() {
            $(document).off(namespace);
        });

        // also bind to Faces (f:ajax) events
        // NOTE: PrimeFaces fires "complete" as the final event, while Faces ends with either "success" or "error", 
        // requiring us to manually trigger a "complete" event in those cases
        if (window.jsf && jsf.ajax) {
            jsf.ajax.addOnEvent(function(data) {
                if(data.status === 'begin') {

                    $this.timeout = PrimeFaces.queueTask(function() {
                        $this.trigger('start', arguments);
                    }, $this.cfg.delay);
                }
                else if(data.status === 'complete') {
                    // Ignore JSF complete event since it fires before success/error. We'll trigger complete manually after success/error to match PrimeFaces event order
                }
                else if(data.status === 'success') {
                    $this.deleteTimeout();
                    $this.trigger('success', arguments);
                    $this.trigger('complete', arguments);
                }
            });

            jsf.ajax.addOnError(function(data) {
                PrimeFaces.error(data);
                $this.deleteTimeout();
                $this.trigger('error', arguments);
                $this.trigger('complete', arguments);
            });
        }
    }

    /**
     * Triggers the given event by invoking the event handler, usually defined on the `<p:ajaxStatus/>` tag.
     * @template {PrimeFaces.widget.AjaxStatus.AjaxStatusEventType} K A name of one of the supported events that should
     * be triggered.
     * @param {K} event A name of one of the supported events that should
     * be triggered.
     * @param {Parameters<PrimeFaces.widget.AjaxStatus.EventToCallbackMap[K]>} args Arguments that are passed to the
     * event handler.
     */
    trigger(event, args) {
        var callback = this.cfg[event];
        if (callback) {
            callback.apply(document, args);
        }

        // Get the facet based on the event
        var facets = this.jq.children();
        var facet = facets.filter(this.toFacetId(event));
        var hasFacet = facet && facet.length > 0;

        // We have the following events:
        // 1) start
        // 2) success or error
        // 3) complete
        switch (event) {
            case 'start':
                // always hide other facets on start
                facets.hide();

                if (hasFacet) {
                    facet.show();
                }
                break;

            case 'success':
            case 'error':
                // we now expect that either a complete or success/error facet is defined
                // if no success/error is defined, lets just rely upon the complete-facet
                if (hasFacet) {
                    facets.hide();
                    facet.show();
                    this.hasSuccessOrErrorFacet = true;
                }
                break;

            case 'complete':
                // Skip hiding the previous facet (typically the start facet) if the request results in a redirect
                // Note: This won't work properly with success/error facets since redirect info isn't available beforehand
                // Only check for redirect if PrimeFaces is used
                if (args.length > 1) { // PrimeFaces passes 3 args, JSF passes 1 arg
                    var pfArgs = args[2];
                    if (!pfArgs || pfArgs.redirect) {
                        return;
                    }
                }

                // #11824 hide the start facet if there was no error/success facet or there is a complete facet
                if (this.hasSuccessOrErrorFacet === false || hasFacet) {
                    facets.hide();
                }
                // Show complete-facet if defined
                if (hasFacet) {
                    facet.show();
                }
                break;
        }
    }

    /**
     * Finds the facet ID of the given event.
     * @private
     * @param {PrimeFaces.widget.AjaxStatus.AjaxStatusEventType} event One of the supported event
     * @return {string} The ID of the facet element for the given event
     */
    toFacetId(event) {
        return this.jqId + '_' + event;
    }

    /**
     * Clears the ste-timeout timer for the delay.
     * @private
     */
    deleteTimeout() {
        if (this.timeout) {
            clearTimeout(this.timeout);
            this.timeout = null;
        }
    }

}