import { AjaxQueue } from "./core.ajax.queue.js";
import { AjaxRequest } from "./core.ajax.request.js";
import { AjaxResponse } from "./core.ajax.response.js";
import { AjaxResponseProcessor } from "./core.ajax.responseprocessor.js";
import { AjaxUtils } from "./core.ajax.utils.js";

const unloadEvent = ("onpagehide" in window) ? "pagehide" : "unload";

/**
 * Set when the browser is tearing down or navigating away so in-flight XHR failures are not treated as application errors.
 * Teardown does not consistently report jQuery {@code textStatus} of {@code "abort"} or a non-zero HTTP status.
 */
export let pageUnloading: boolean = false;

/**
 * The class with functionality related to sending and receiving AJAX requests that are made by PrimeFaces. Each
 * request receives an XML response, which consists of one or multiple actions that are to be performed. This
 * includes creating new DOM elements, deleting or updating existing elements, or executing some JavaScript.
 */
export class Ajax {
    /**
     * Name for the ID of the HEAD element, used in AJAX requests.
     * @type {string}
     * @readonly
     */
    VIEW_HEAD = "jakarta.faces.ViewHead";

    /**
     * Name for the ID of the BODY element, used in AJAX requests.
     * @type {string}
     * @readonly
     */
    VIEW_BODY = "jakarta.faces.ViewBody";

    /**
     * Name for the ID of a resource entry, used in AJAX requests.
     * @type {string}
     * @readonly
     */
    RESOURCE = "jakarta.faces.Resource";

    /**
     * Error name that is used when an AJAX response cannot be received at all, e.g. because of an HTTP error
     * status, a timeout or a network failure. Register a `p:ajaxExceptionHandler` with this type to handle
     * exactly these kind of errors. Same as the `httpError` status of `faces.ajax`.
     * @type {string}
     * @readonly
     */
    ERROR_HTTP = "httpError";

    /**
     * Error name that is used when an AJAX response was received but cannot be parsed or does not contain a
     * `partial-response`, e.g. a login page after a session timeout. Same as the `malformedXML` status of `faces.ajax`.
     * @type {string}
     * @readonly
     */
    ERROR_MALFORMED_XML = "malformedXML";

    /**
     * Error name that is used when an AJAX response was received but did not contain any data.
     * Same as the `emptyResponse` status of `faces.ajax`.
     * @type {string}
     * @readonly
     */
    ERROR_EMPTY_RESPONSE = "emptyResponse";

    /**
     * Parameter shortcut mapping for the method {@link ab}.
     */
    CFG_SHORTCUTS: PrimeFaces.ajax.ShorthandToArticulateConfigurationMap = {
        's': 'source',
        'f': 'formId',
        'p': 'process',
        'u': 'update',
        'e': 'event',
        'a': 'async',
        'g': 'global',
        'd': 'delay',
        't': 'timeout',
        'sc': 'skipChildren',
        'iau': 'ignoreAutoUpdate',
        'ps': 'partialSubmit',
        'psf': 'partialSubmitFilter',
        'rv': 'resetValues',
        'fp': 'fragmentProcess',
        'fu': 'fragmentUpdate',
        'pa': 'params',
        'onst': 'onstart',
        'oner': 'onerror',
        'onsu': 'onsuccess',
        'onco': 'oncomplete'
    };

    /**
     * Minimum number of milliseconds to show inline Ajax load animations.
     */
    minLoadAnimation: number = 500;

    /**
     * This object contains utility methods for AJAX requests, primarily used internally.
     */
    readonly Utils: AjaxUtils = new AjaxUtils();

    /**
     * This object contains functionality related to queuing AJAX requests to ensure that they are (a) sent in the
     * proper order and (b) that each response is processed in the same order as the requests were sent.
     */
    readonly Queue: AjaxQueue = new AjaxQueue();

    /**
     * The the object containing low-level functionality related to sending AJAX requests.
     */
    readonly Request: AjaxRequest = new AjaxRequest();

    /**
     * The object containing low-level functionality related to handling AJAX responses. Note that
     * the different types of AJAX actions are handles by the {@link AjaxResponseProcessor | PrimeFaces.ResponseProcessor}.
     */
    readonly Response: AjaxResponse = new AjaxResponse();

    /**
     * The object containing low-level functionality related to processing the different types
     * of actions from AJAX responses.
     */
    readonly ResponseProcessor: AjaxResponseProcessor = new AjaxResponseProcessor();

    /**
     * Only available for backward compatibility, do not use in new code.
     * @deprecated Use {@link AjaxRequest.handle | PrimeFaces.ajax.Request.handle} instead.
     * @param cfg Configuration for the AJAX request to send, such as
     * the HTTP method, the URL, and the content of the request.
     * @param ext Optional extender with additional options
     * that overwrite the options given in `cfg`.
     * @return A promise that resolves once the AJAX requests is done. Use this to run custom JavaScript logic. When the
     * AJAX request succeeds, the promise is fulfilled. Otherwise, when the AJAX request fails, the promise is rejected.
     * If the promise is rejected, the rejection handler receives an object of type
     * {@link PrimeFaces.ajax.FailedRequestData} .
     */
    AjaxRequest(cfg: PrimeFaces.ajax.Configuration, ext?: PrimeFaces.ajax.ConfigurationExtender): PromiseLike<PrimeFaces.ajax.ResponseData> {
        return ajax.Request.handle(cfg, ext);
    }
}

/**
 * The object with functionality related to sending and receiving AJAX requests that are made by PrimeFaces. Each
 * request receives an XML response, which consists of one or multiple actions that are to be performed. This
 * includes creating new DOM elements, deleting or updating existing elements, or executing some JavaScript.
 */
export const ajax: Ajax = new Ajax();

export function globalAjaxSetup(): void {
    // Add listeners to detect when the page is unloading or being shown again, in order to properly 
    // manage AJAX request state. Setting `pageUnloading` prevents treating XHR failures from navigation 
    // as application errors. Reset on pageshow if coming from bfcache (event.persisted).
    window.addEventListener("beforeunload", () => {
        pageUnloading = true;
    });

    window.addEventListener("pageshow", (event: PageTransitionEvent) => {
        if (event.persisted) {
            // The page is loaded from the bfcache, restore AJAX handling
            pageUnloading = false;
        }
    });

    // Use jQuery to listen for browser unload events (including pagehide for SPA/PR and legacy support).
    // Abort all queued AJAX requests when the page is really going away to avoid side effects or ghost requests.
    $(window).on(unloadEvent, () => {
        pageUnloading = true;
        ajax.Queue.abortAll();
    });

    $(() => {
        if (window.faces && faces.ajax) {
            faces.ajax.addOnError((data) => {
                // serverError means a real server-side exception where a p:ajaxExceptionHandler or error-page mapping might exist
                if (data.status === "serverError") {
                    ajax.Utils.handleError(data.errorName, data.errorMessage);
                }
                // httpError, malformedXML, emptyResponse, clientError, timeout
                // p:ajax reports the same error names, so a p:ajaxExceptionHandler can be registered for them
                else {
                    var errorMessage = 'AJAX failure';
                    if (data.responseCode && data.responseCode > 0) {
                        errorMessage += ' with HTTP status ' + data.responseCode;
                    }
                    ajax.Utils.handleError(data.status, errorMessage);
                }
            });
        }
    });

    $(document).on('pfAjaxError', function(e, xhr, settings, error){
        // e.g. the request of a p:ajaxExceptionHandler itself, see Configuration.skipErrorHandling
        if (settings?.skipErrorHandling) {
            return;
        }

        var errorMessage = 'AJAX failure';
        if (xhr && xhr.status) {
            errorMessage += ' with HTTP status ' + xhr.status;
        }
        if (error) {
            errorMessage += ': ' + error;
        }

        // a successful HTTP status means that the response was received but could not be parsed,
        // e.g. a proxy returned a HTML login page
        if (xhr && xhr.status >= 200 && xhr.status < 300) {
            ajax.Utils.handleError(ajax.ERROR_MALFORMED_XML, errorMessage);
            return;
        }

        // the response could not be received at all, e.g. HTTP error status, timeout or network failure;
        // report it like faces.ajax does, so that a p:ajaxExceptionHandler can be registered for it
        ajax.Utils.handleError(ajax.ERROR_HTTP, errorMessage);
    });
}
