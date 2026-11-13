import { core } from "../core/core.js";
import { BaseComponent, type BaseComponentCfg } from "../core/core.component.js";

/**
 * Available
 * types of AJAX related events to which you can listen.
 */
export type AjaxStatusEventType = "start" | "success" | "error" | "complete" | "facesComplete";

/**
 * The configuration for the {@link  AjaxStatus AjaxStatus component}.
 * 
 * You can access this configuration via {@link BaseComponent.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface AjaxStatusCfg extends BaseComponentCfg {
    /**
     * Client-side callback for when the AJAX behavior completes, i.e. when the request finishes, irrespective of whether it
     * succeeded or failed. Usually set via `<p:ajaxStatus oncomplete="..."/>`. The parameters below apply when
     * `<p:ajax />` is used.
     * @param xhr The request that completed.
     * @param settings The settings of the jQuery AJAX request.
     * @param args The additional arguments of the PrimeFaces AJAX request.
     */
    complete: ((this: Document, xhr: JQuery.jqXHR, settings: JQuery.AjaxSettings, args: PrimeFaces.ajax.PrimeFacesArgs) => void)
        | faces.ajax.OnEventCallback | faces.ajax.OnErrorCallback;

    /**
     * Delay in milliseconds before displaying the AJAX status. Default is `0`, meaning immediate.
     */
    delay: number;

    /**
     * Client-side callback
     * for when the AJAX behavior fails, i.e. when the request fails. Usually set via `<p:ajaxStatus onerror="..."/>`.
     * The parameters below apply when `<p:ajax />` is used.
     * @param xhr The request that failed.
     * @param settings The settings of the jQuery AJAX request.
     * @param errorThrown The error that cause the request to fail.
     */
    error: ((this: Document, xhr: JQuery.jqXHR, settings: JQuery.AjaxSettings, errorThrown: string) => void)
        | faces.ajax.OnErrorCallback;

    /**
     * Client-side callback for when the AJAX behavior completes, i.e. when the request finishes, irrespective of whether it
     * succeeded or failed. The parameters below apply when `<p:ajax />` is used.
     * @param xhr The request that completed.
     * @param settings The settings of the jQuery AJAX request.
     * @param args The additional arguments of the PrimeFaces AJAX request.
     */
    facesComplete: ((this: Document, xhr: JQuery.jqXHR, settings: JQuery.AjaxSettings, args: PrimeFaces.ajax.PrimeFacesArgs) => void)
        | faces.ajax.OnEventCallback | faces.ajax.OnErrorCallback;

    /**
     * Client-side callback
     * for when the AJAX behavior starts, i.e. the request is about to be sent. Usually set via
     * `<p:ajaxStatus onstart="..."/>`.
     */
    start: ((this: Document, xhr: JQuery.TriggeredEvent, ...args: unknown[]) => void) | faces.ajax.OnEventCallback;

    /**
     * Client-side
     * callback for when the AJAX  behavior completes successfully, i.e. when the request succeeds. Usually set via
     * `<p:ajaxStatus onsuccess="..."/>`. The parameters below apply when `<p:ajax />` is used.
     * @param xhr The request that succeeded.
     * @param settings The settings of the jQuery AJAX request.
     */
    success: ((this: Document, xhr: JQuery.jqXHR, settings: JQuery.AjaxSettings) => void) | faces.ajax.OnEventCallback;
}

/**
 * Maps between the
 * {@link AjaxStatusEventType} and the corresponding event handlers. Used by the {@link AjaxStatus} component.
 */
export interface EventToCallbackMap {
    /**
     * Callback for when an AJAX request completes, either successfully or with an error. Usually set via
     * `<p:ajaxStatus oncomplete="..."/>`.
     */
    complete: AjaxStatusCfg["complete"];
    /**
     * Callback
     * for when an AJAX request fails. Usually set via `<p:ajaxStatus onerror="..."/>`.
     */
    error: AjaxStatusCfg["error"];
    /**
     * Callback for when an AJAX request completes, either successfully or with an error. Usually set via
     * `<p:ajaxStatus oncomplete="..."/>`.
     */
    facesComplete: AjaxStatusCfg["facesComplete"];
    /**
     * Callback
     * for when an AJAX request starts. Usually set via `<p:ajaxStatus onstart="..."/>`.
     */
    start: AjaxStatusCfg["start"];
    /**
     * Callback for when an AJAX request succeeds. Usually set via `<p:ajaxStatus onsuccess="..."/>`.
     */
    success: AjaxStatusCfg["success"];
}

/**
 * __PrimeFaces AjaxStatus Component__
 * 
 * AjaxStatus is a global notifier for AJAX requests.
 * 
 * For the callbacks that can be set via the `onstart`, `onsuccess`, `onerror` and `oncomplete` attributes, see
 * {@link AjaxStatusCfg.start | start},
 * {@link AjaxStatusCfg.success | success},
 * {@link AjaxStatusCfg.error | error}, and
 * {@link AjaxStatusCfg.complete | complete}.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class AjaxStatus<Cfg extends AjaxStatusCfg = AjaxStatusCfg> extends BaseComponent<Cfg> {
    private hasSuccessOrErrorFacet: boolean = false;
    private timeout: number | null | undefined = null;
    private facesListenersAdded: boolean = false;

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
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
        $(document).off(namespace).on('pfAjaxStart' + namespace, (...args) => {
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
        // faces listeners can't be removed, so register them only once for this component instance which survives refreshes
        if (window.faces && faces.ajax && !this.facesListenersAdded) {
            this.facesListenersAdded = true;
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
    trigger<Event extends AjaxStatusEventType>(
        event: Event, 
        args: Parameters<EventToCallbackMap[Event]>
    ): void {
        var callback = this.cfg[event satisfies AjaxStatusEventType];
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
                    const pfArgs = args[2] as PrimeFaces.ajax.PrimeFacesArgs | undefined;
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
    private toFacetId(event: AjaxStatusEventType): string {
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