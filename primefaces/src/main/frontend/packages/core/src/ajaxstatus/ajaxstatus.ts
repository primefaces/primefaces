import { core } from "../core/core.js";
import { BaseWidget, type BaseWidgetCfg } from "../core/core.widget.js";

/**
 * The configuration for the {@link  AjaxStatus AjaxStatus widget}.
 * 
 * You can access this configuration via {@link BaseWidget.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface AjaxStatusCfg extends BaseWidgetCfg {
    /**
     * Client-side callback for when the AJAX behavior completes, i.e. when the request finishes, irrespective of whether it
     * succeeded or failed.
     */
    complete: PrimeType.widget.AjaxStatus.PfAjaxCompleteCallback | faces.ajax.OnEventCallback | faces.ajax.OnErrorCallback;

    /**
     * Delay in milliseconds before displaying the AJAX status. Default is `0`, meaning immediate.
     */
    delay: number;

    /**
     * Client-side callback
     * for when the AJAX behavior fails, i.e. when the request fails.
     */
    error: PrimeType.widget.AjaxStatus.PfAjaxErrorCallback | faces.ajax.OnErrorCallback;

    /**
     * Client-side callback for when the AJAX behavior completes, i.e. when the request finishes, irrespective of whether it
     * succeeded or failed.
     */
    facesComplete: PrimeType.widget.AjaxStatus.PfAjaxCompleteCallback | faces.ajax.OnEventCallback | faces.ajax.OnErrorCallback;

    /**
     * Client-side callback
     * for when the AJAX behavior starts, i.e. the request is about to be sent.
     */
    start: PrimeType.widget.AjaxStatus.PfAjaxStartCallback | faces.ajax.OnEventCallback;

    /**
     * Client-side
     * callback for when the AJAX  behavior completes successfully, i.e. when the request succeeds.
     */
    success: PrimeType.widget.AjaxStatus.PfAjaxSuccessCallback | faces.ajax.OnEventCallback;
}

/**
 * __PrimeFaces AjaxStatus Widget__
 * 
 * AjaxStatus is a global notifier for AJAX requests.
 * 
 * For the callbacks that can be set via the `onstart`, `onsuccess`, `onerror` and `oncomplete` attributes, see
 * {@link PrimeType.widget.AjaxStatus.PfAjaxStartCallback PfAjaxStartCallback},
 * {@link PrimeType.widget.AjaxStatus.PfAjaxSuccessCallback PfAjaxSuccessCallback},
 * {@link PrimeType.widget.AjaxStatus.PfAjaxErrorCallback PfAjaxErrorCallback}, and
 * {@link PrimeType.widget.AjaxStatus.PfAjaxCompleteCallback PfAjaxCompleteCallback}.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class AjaxStatus<Cfg extends AjaxStatusCfg = AjaxStatusCfg> extends BaseWidget<Cfg> {
    private hasSuccessOrErrorFacet: boolean = false;
    private timeout: number | null | undefined = null;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.hasSuccessOrErrorFacet = false;

        this.bind();
    }

    /**
     * Binds event handlers to AJAX events on the document element.
     * Handles both PrimeFaces AJAX events and native Jakarta AJAX events.
     * 
     * For PrimeFaces AJAX events:
     * - pfAjaxStart: Triggered when AJAX request starts. After configured delay, triggers 'start' event.
     * - pfAjaxError: Triggered when AJAX request fails. Triggers 'error' event with xhr, settings, error details.
     * - pfAjaxSuccess: Triggered when AJAX request succeeds. Triggers 'success' event with xhr, settings.
     * - pfAjaxComplete: Triggered after success/error. Clears timeout if no redirect. Triggers 'complete' event.
     * 
     * For Jakarta AJAX events:
     * - begin: Triggers 'start' event after configured delay
     * - complete: IGNORED since it fires before success/error
     * - success: Clears timeout, triggers 'success' then 'complete' events
     * - error: Logs error, clears timeout, triggers 'error' then 'complete' events
     * 
     * Events are namespaced with component ID for cleanup.
     * Configured delay controls timing of showing AJAX status.
     * Cleanup is handled via destroy listener that removes document event handlers.
     */
    private bind(): void {
        var namespace = '.status' + this.id;
        // Handle start of AJAX request
        $(document).on('pfAjaxStart' + namespace, (...args) => {
            // Queue task to trigger start event after configured delay
            this.timeout = core.queueTask(() => {
                this.trigger('start', args);
            }, this.cfg.delay);
        })
        // Handle AJAX error
        .on('pfAjaxError' + namespace, (e, xhr, settings, error) => {
            this.trigger('error', [xhr, settings, error]);
        })
        // Handle AJAX success
        .on('pfAjaxSuccess' + namespace, (e, xhr, settings) => {
            this.trigger('success', [xhr, settings]);
        })
        // Handle AJAX completion (after success/error)
        .on('pfAjaxComplete' + namespace, (e, xhr, settings, args) => {
            if(this.timeout && args && !args.redirect) {
                this.deleteTimeout();
            }
            this.trigger('complete', [xhr, settings, args]);
        });
        this.addDestroyListener(() => {
            $(document).off(namespace);
        });

        // also bind to Faces (f:ajax) events
        // NOTE: PrimeFaces fires "complete" as the final event, while Faces ends with either "success" or "error", 
        // requiring us to manually trigger a "complete" event in those cases
        if (window.faces && faces.ajax) {
            faces.ajax.addOnEvent((...args) => {
                const data = args[0];
                if(data.status === 'begin') {
                    this.timeout = core.queueTask(() => {
                        this.trigger('start', args);
                    }, this.cfg.delay);
                }
                else if(data.status === 'complete') {
                    // Ignore Jakarta Faces complete event since it fires before success/error.
                    // We'll trigger complete manually after success/error to match PrimeFaces event order
                }
                else if(data.status === 'success') {
                    this.deleteTimeout();
                    this.trigger('success', args);
                    this.trigger('complete', args);
                }
            });

            faces.ajax.addOnError((...args) => {
                this.deleteTimeout();
                this.trigger('error', args);
                this.trigger('complete', args);
            });
        }
    }

    /**
     * Triggers the given event by invoking the event handler, usually defined on the `<p:ajaxStatus/>` tag.
     * @typeParam Event A name of one of the supported events that should be triggered.
     * @param event A name of one of the supported events that should be triggered.
     * @param args Arguments that are passed to the
     * event handler.
     */
    trigger<Event extends PrimeType.widget.AjaxStatus.AjaxStatusEventType>(
        event: Event, 
        args: Parameters<PrimeType.widget.AjaxStatus.EventToCallbackMap[Event]>
    ): void {
        var callback = this.cfg[event satisfies PrimeType.widget.AjaxStatus.AjaxStatusEventType];
        if (callback) {
            // @ts-expect-error
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
                if (args.length > 1) { // PrimeFaces passes 3 args, Jakarta Faces passes 1 arg
                    const pfArgs = args[2] as PrimeType.ajax.PrimeFacesArgs | undefined;
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
     * @param event One of the supported event
     * @return {string} The ID of the facet element for the given event
     */
    private toFacetId(event: PrimeType.widget.AjaxStatus.AjaxStatusEventType): string {
        return this.jqId + '_' + event;
    }

    /**
     * Clears the ste-timeout timer for the delay.
     */
    private deleteTimeout() {
        if (this.timeout) {
            clearTimeout(this.timeout);
            this.timeout = null;
        }
    }
}