import { AjaxQueue } from "./core.ajax.queue.js";
import { AjaxRequest } from "./core.ajax.request.js";
import { AjaxResponse } from "./core.ajax.response.js";
import { AjaxResponseProcessor } from "./core.ajax.responseprocessor.js";
import { AjaxUtils } from "./core.ajax.utils.js";
import type { BaseComponent } from "./core.component.js";

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

// AJAX types
declare global {
    /**
     * Namespace related to AJAX functionality, such as sending post-back requests
     * to the server and handling the response.
     */
    namespace PrimeFaces.ajax {
        /**
         * An entry on the {@link JQuery.jqXHR} request object with additional values added by PrimeFaces. For example, when
         * you call `PrimeFaces.current().ajax().addCallbackParam(...)` on the server in a bean method, the added parameters
         * are available in this object. This is also how you can access pass values from the server to the client after
         * calling a remote command. See {@link PrimeFaces.ajax.pfXHR} and {@link PrimeFaces.ab}.
         */
        export type PrimeFacesArgs = Record<string, unknown>;
        /**
         * Additional settings on a {@link JQuery.jqXHR | jqXHR} request, such as portlet forms and nonces.
         */
        export interface PrimeFacesSettings extends JQuery.AjaxSettings<PrimeFacesSettings> {
            /**
             * Selector to resolve all forms which needs to be updated with a new ViewState. This is required in
             * portlets, as the DOM contains forms of multiple Faces views / applications.
             */
            portletForms?: string | null | undefined;
            /**
             * The source that triggered the AJAX request.
             */
            source?: string | HTMLElement | JQuery<HTMLElement> | undefined;
            /**
             * `true` if errors of the request should not be passed to a `p:ajaxExceptionHandler` or `error-page`.
             * See {@link Configuration.skipErrorHandling}.
             */
            skipErrorHandling?: boolean;
            beforeSend?(this: PrimeFacesSettings, jqXHR: pfXHR, settings: PrimeFacesSettings): false | void;
            nonce?: string;
        }
        /**
         * Callback for an AJAX request that is always called after the request completes, irrespective of whether it
         * succeeded or failed.
         *
         * The parameters passed to this function depend on whether the requested was successful.
         * 
         * This is the type of function that you can set as a client side callback for the `oncomplete` attribute of a
         * component or an AJX behavior.
         */
        export type CallbackOncomplete =
            /**
             * @this The current AJAX settings as they were passed to JQuery when the request was made.
             * @param xhrOrErrorThrown Either the XHR request that was made (in case of success), or the error that was
             * thrown (in case of an error).
             * @param status The type of error or success.
             * @param pfArgs Additional arguments returned by PrimeFaces, such as AJAX callback params from beans (in
             * case of success); or undefined (in case of an error).
             * @param dataOrXhr Either the XMLDocument (in case of success), or the XHR request (in case of an error).
             */
            (
                this: PrimeFacesSettings,
                xhrOrErrorThrown: unknown,
                status: JQuery.Ajax.TextStatus,
                pfArgs: PrimeFacesArgs | undefined,
                dataOrXhr: XMLDocument | pfXHR
            ) => void;
        /**
         * Callback for an AJAX request that is called in case any error occurred during the request, such as a a network
         * error. Note that this is not called for errors in the application logic, such as when bean validation fails.
         *
         * This is the type of function that you can set as a client side callback for the `onerror` attribute of a
         * component or an AJX behavior.
         */
        export type CallbackOnerror =
            /**
             * @this The current AJAX settings as they were passed to JQuery when the request was made.
             * @param xhr The XHR request that failed.
             * @param status The type of error that occurred.
             * @param errorThrown The error with details on why the request failed.
             */
            (this: PrimeFacesSettings, xhr: pfXHR, status: JQuery.Ajax.ErrorTextStatus, errorThrown: string) => void;
        /**
         * Callback for an AJAX request that is called before the request is sent. Return `false` to cancel the request.
         *
         * This is the type of function that you can set as a client side callback for the `onstart` attribute of a
         * component or an AJX behavior.
         */
        export type CallbackOnstart =
            /**
             * @this The {@link PrimeFaces.ajax.Request} singleton instance responsible for handling the request.
             * @param cfg The current AJAX configuration.
             * @return {boolean | undefined} `false` to abort and not send the request, `true` or `undefined` otherwise.
             */
            (this: AjaxRequest, cfg: Configuration) => boolean;
        /**
         * Callback for an AJAX request that is called when the request succeeds.
         *
         * This is the type of function that you can set as a client side callback for the `onsuccess` attribute of a
         * component or an AJX behavior.
         */
        export type CallbackOnsuccess =
            /**
             * @this The current AJAX settings as they were passed to JQuery when the request was made.
             * @param data The XML document representing the partial response returned the Faces application in response
             * to the faces request. It usually looks like this: `<changes>...</changes>`
             * @param status The type of success, usually `success`.
             * @param xhr The XHR request that succeeded.
             * @return `true` if this handler already handle and/or parsed the response, `false` or `undefined` otherwise.
             */
            (this: PrimeFacesSettings, data: XMLDocument, status: JQuery.Ajax.SuccessTextStatus, xhr: pfXHR) => boolean | undefined;
        /**
         * The XHR request object used by PrimeFaces. It extends the `jqXHR` object as used by JQuery, but adds additional
         * properties specific to PrimeFaces.
         * @typeParam P Data made available by the server via {@link pfXHR.pfArgs}.
         */
        export interface pfXHR<P extends PrimeFacesArgs = PrimeFacesArgs> extends JQuery.jqXHR {
            /**
             * An object with additional values added by PrimeFaces. For example, when you call
             * `PrimeFaces.current().ajax().addCallbackParam(...)` on the server in a bean method, the added parameters are
             * available in this object. This is also how you can access pass values from the server to the client after
             * calling a remote command.  See {@link PrimeFaces.ajax.pfXHR} and {@link PrimeFaces.ab}.
             */
            pfArgs?: P;
            /**
             * An array with scripts added by PrimeFaces.current().executeScript(...).
             * These will be invoked the same way as the oncomplete attribute of an ajax source.
             */
            pfScripts?: string[];
            /**
             * Additional settings, such as portlet forms and nonces.
             */
            pfSettings?: PrimeFacesSettings;
        }
        /**
         * Represents the data of a PrimeFaces AJAX request. This is the value that is returned by {@link PrimeFaces.ab} and
         * {@link PrimeFaces.ajax.Request.handle}.
         * @typeParam P Record type of the data made available in the property {@link PrimeFaces.ajax.pfXHR.pfArgs} by the
         * server.
         */
        export interface ResponseData<P extends PrimeFacesArgs = PrimeFacesArgs> {
            /**
             * The XML document that was returned by the server. This may include several elements such as `update` for DOM
             * updates that need to be performed, `executeScript` for running JavaScript code. A typical response might look
             * as follows:
             *
             * ```xml
             * <?xml version="1.0" encoding="UTF-8"?>
             * <partial-response>
             *    <changes>
             *       <update id="content:msgs"><![CDATA[
             *           <span id="content:msgs" class="p-growl-pl">...</span>
             *           <script id="content:msgs_s" type="text/javascript">...</script>
             *       ]]></update>
             *       <update id="content:jakarta.faces.ViewState:0"><![CDATA[3644438980748093603:2519460806875181703]]></update>
             *    </changes>
             * </partial-response>
             * ```
             */
            document: XMLDocument;
            /**
             * The jQuery XHR request object that was used for the request.
             *
             * __Note__: This object has a `pfArgs` entry that contains the values added to the response by the server. See
             * {@link PrimeFaces.ajax.pfXHR.pfArgs} and {@link PrimeFaces.ajax.RemoteCommand}.
             */
            jqXHR: pfXHR<P>;
            /**
             * A string describing the type of success. Usually the HTTP status text.
             */
            textStatus: JQuery.Ajax.SuccessTextStatus;
        }
        /**
         * Represents the data passed to the exception handler of the promise returned by {@link PrimeFaces.ab} and
         * {@link PrimeFaces.ajax.Request.handle}.
         */
        export interface FailedRequestData {
            /**
             * An optional exception message, if an error occurred.
             */
            errorThrown: string;
            /**
             * The jQuery XHR request object that was used for the request. May not be available when no HTTP request was
             * sent, such as when validation failed.
             */
            jqXHR?: pfXHR;
            /**
             * A string describing the type of error that occurred.
             */
            textStatus: JQuery.Ajax.SuccessTextStatus;
        }
        /**
         * Describes a server callback parameter for an AJAX call. For example, when you call a
         * `<p:remoteCommand name="myCommand" />` from the client, you may pass additional parameters to the backing
         * bean like this:
         *
         * ```javascript
         * myCommand([
         *   {
         *     name: "MyParam",
         *     value: "MyValue",
         *   }
         * ]);
         * ```
         *
         * In the backing bean, you can access this parameter like this:
         *
         * ```java
         * final String myParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("myParam");
         * ```
         * @typeParam K Name of this parameter.
         * @typeParam V Type of the value of the callback parameter. Please note that it will be converted to string
         * before it is passed to the server.
         */
        export interface RequestParameter<K extends string = string, V = unknown> {
            /**
             * The name of the parameter to pass to the server.
             */
            name: K;
            /**
             * The value of the parameter to pass to the server.
             */
            value: V;
        }
        /**
         * The response of an AJAX request may contain one or more actions such as executing scripts or updating DOM nodes.
         * This interface represents a handler for an `update` action.
         * @typeParam Component Type of the component which
         * triggered the AJAX request.
         */
        export interface UpdateHandler<Component extends BaseComponent = BaseComponent> {
            /**
             * The component which triggered the AJAX request.
             */
            component?: Component;
            /**
             * @deprecated Use {@link component} instead.
             */
            widget?: Component;
            /**
             * The handle function which is given the HTML string of the update
             * @param content The new HTML content from the update.
             */
            handle(this: Component, content: string): void;
        }
        /**
         * Represents the selection of an INPUT or TEXTAREA element.
         */
        export interface ActiveElementSelection {
            /**
             * Start of the selection, that is, the index of the first selected character.
             */
            start: number;
            /**
             * End of the selection, that is, one plus the index of the last selected character.
             */
            end: number;
            /**
             * The number of selected characters.
             */
            length: number;
            /**
             * The selected text
             */
            text: string;
        }
        /**
         * The options that can be passed to AJAX calls made by PrimeFaces. Note that you do not have to provide a value
         * for all these property. Most methods methods such as `PrimeFaces.ab` have got sensible defaults in case you
         * do not.
         */
        export interface Configuration {
            /**
             * If `true`, the the request is sent immediately. When set to `false`, the AJAX request is added to a
             * global queue to ensure that only one request is active at a time, and that each response is processed
             * in order. Defaults to `false`.
             */
            async?: boolean;
            /**
             * Delay in milliseconds. If less than this delay elapses between AJAX requests, only the most recent one is
             * sent and all other requests are discarded. If this option is not specified, no delay is used.
             */
            delay?: number;
            /**
             * A PrimeFaces client-side search expression (such as `@ref(name)` or `@(.my-class)` for locating the form
             * to with the input elements that are serialized. If not given, defaults to the enclosing form.
             */
            formId?: string;
            /**
             * Params to be added early when preparing an AJAX request, if
             * {@link PFSettings.earlyPostParamEvaluation | earlyPostParamEvaluation}
             * is enabled. 
             */
            earlyPostParams?: PrimeFaces.ajax.RequestParameter<string, string | Blob>[];
            /**
             * The AJAX behavior event that triggered the AJAX request.
             */
            event?: string;
            /**
             * Additional options that can be passed when sending an AJAX request to override the current options.
             */
            ext?: Partial<ConfigurationExtender>;
            /**
             * Additional search expression that is added to the `process` option.
             */
            fragmentProcess?: string;
            /**
             * Additional search expression that is added to the `update` option.
             */
            fragmentUpdate?: string;
            /**
             * Whether this AJAX request is global, ie whether it should trigger the global `<p:ajaxStatus />`. Defaults
             * to `true`.
             */
            global?: boolean;
            /**
             * `true` if components with `<p:autoUpdate/`> should be ignored and updated only if specified explicitly
             * in the `update` option; or `false` otherwise. Defaults to `false`.
             */
            ignoreAutoUpdate?: boolean;
            /**
             * Callback that is always called after the request completes, irrespective of whether it succeeded or
             * failed.
             */
            oncomplete?: CallbackOncomplete;
            /**
             * Callback that is called in case any error occurred during the request, such as a a network error. Note
             * that this is not called for errors in the application logic, such as when bean validation fails.
             */
            onerror?: CallbackOnerror;
            /**
             * Callback that is called before the request is sent. Return `false` to cancel the request.
             */
            onstart?: CallbackOnstart;
            /**
             * Callback that is called when the request succeeds.
             */
            onsuccess?: CallbackOnsuccess;
            /**
             * Additional parameters that are passed to the server. These can be accessed as follows:
             *
             * ```java
             * final String myParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("myParam");
             * ```
             */
            params?: RequestParameter[];
            /**
             * `true` to perform a partial submit and not send the entire form data, but only the processed components;
             * or `false` to send the entire form data. Defaults to `false`.
             */
            partialSubmit?: boolean;
            /**
             * A CSS selector for finding the input elements of partially processed components. Defaults to `:input`.
             */
            partialSubmitFilter?: string;
            /**
             * A (client-side) PrimeFaces search expression for the components to process in the AJAX request.
             */
            process?: string;
            /**
             * A promise object that is resolved when the AJAX request is complete. You can use this option to register
             * a custom callback. Please note that usually you do not have to set this option explicitly, you can use the
             * return value of {@link PrimeFaces.ab} or {@link PrimeFaces.ajax.Request.handle}. It will create a new promise
             * object when none was provided, and return that.
             */
            promise?: JQuery.Deferred<ResponseData>;
            /**
             * `true` if the AJAX request is a reset request that resets the value of all form elements to their
             * initial values, or `false` otherwise. Defaults to `false`.
             */
            resetValues?: boolean;
            /**
             * `true` if child components should be skipped for the AJAX request, `false` otherwise. Used only by a few
             * specific components.
             */
            skipChildren?: boolean;
            /**
             * `true` if errors of this request should not be passed to a `p:ajaxExceptionHandler` or `error-page`, e.g.
             * for the request of a `p:ajaxExceptionHandler` itself; `false` otherwise. Defaults to `false`.
             */
            skipErrorHandling?: boolean;
            /**
             * The source that triggered the AJAX request. Either a client ID or
             * an (HTML) element.
             */
            source?: string | JQuery<HTMLElement> | HTMLElement;
            /**
             * Set a timeout (in milliseconds) for the request. A value of 0 means there will be no timeout.
             */
            timeout?: number;
            /**
             * A (client-side) PrimeFaces search expression for the components to update in the AJAX request.
             */
            update?: string;
        }
        /**
         * Additional options that can be passed when sending an AJAX request to override the current options.
         */
        export type ConfigurationExtender = Pick<Configuration, "update" | "process" | "onstart" | "params" | "partialSubmit" | "onerror" | "onsuccess" | "oncomplete"> & {
            /**
             * If given, this function is called once for each component. It is passed that serialized values for the
             * component and should return the filtered values that are to be sent to the server. If not given, no
             * values are filtered, and all values are send to the server.
             * @param componentPostParams The serialized values of a component.
             * @return The filtered values that are to be sent to the server.
             */
            partialSubmitParameterFilter?: (this: AjaxRequest, componentPostParams: JQuery.NameValuePair[]) => JQuery.NameValuePair[];
        };
        /**
         * Mapping from articulate to shortname names for the options passed to AJAX calls made by PrimeFaces.
         */
        export interface ArticulateToShorthandConfigurationMap {
            source: "s";
            formId: "f";
            process: "p";
            update: "u";
            event: "e";
            async: "a";
            global: "g";
            delay: "d";
            timeout: "t";
            skipChildren: "sc";
            ignoreAutoUpdate: "iau";
            partialSubmit: "ps";
            partialSubmitFilter: "psf";
            resetValues: "rv";
            fragmentProcess: "fp";
            fragmentUpdate: "fu";
            params: "pa";
            onstart: "onst";
            onerror: "oner";
            onsuccess: "onsu";
            oncomplete: "onco";
        }
        /**
         * Mapping from articulate to shortname names for the options passed to AJAX calls made by PrimeFaces.
         */
        export type ShorthandToArticulateConfigurationMap = PrimeFaces.InvertRecord<ArticulateToShorthandConfigurationMap>;

        /**
         * Options passed to AJAX calls made by PrimeFaces. This is the same as `Configuration`, but with shorter
         * option names and is used mainly by the method `PrimeFaces.ab`. See `Configuration` for a detailed description
         * of these options.
         */
        export type ShorthandConfiguration = RenameKeys<Configuration, ArticulateToShorthandConfigurationMap>;

        /**
         * Helper type for the parameters of the remote command. You can specify an object type with the allowed parameter
         * names and their expected value types. This helps to increase type safety for remote commands. For example, when
         * this remote command with an appropriate bean implementation is defined:
         *
         * ```xml
         * <p:remoteCommand name="RemoteCommands.checkMaturity" ... />
         * ```
         *
         * Then you can declare (or generate automatically from the bean method!) this remote command in TypeScript like
         * this:
         *
         * ```typescript
         * declare const RemoteCommands {
         *   const checkMaturity: RemoteCommand<
         *     {name: string, age: number},
         *     {success: boolean, message: string}
         *   >;
         * }
         *
         * RemoteCommand.checkMaturity( [ { name: "name", value: "John Doe" } ] ) // works
         * RemoteCommand.checkMaturity( [ { name: "age", value: 12 } ] ) // works
         *
         * RemoteCommand.checkMaturity( [ { name: "username", value: "John Doe" } ] ) // error
         * RemoteCommand.checkMaturity( [ { name: "age", value: "12" } ] ) // error
         *
         * const response = await RemoteCommand.checkMaturity( [ { name: "name", value: "John Doe" } ];
         *
         * const success: boolean = response.jqXHR.pfArgs.success; // works
         * const message: string = response.jqXHR.pfArgs.message; // works
         * const message: string = response.jqXHR.pfArgs.errormessage; // error
         * ```
         * @typeParam T Record type with the param names and the corresponding param values.
         * @return An array type of {@link PrimeFaces.ajax.RequestParameter | request parameters} where the `name` can be
         * one of the keys of `T` and the `value` is the corresponding value from `T`. Array values are mapped to the item
         * type, so that `RemoteCommandParams<{names: string[]}>` is the same as `RemoteCommandParams<{names: string}>`.
         * This is done because multiple values for the same name should be send by including multiple items in the request
         * callback parameter array.
         */
        export type RemoteCommandParams<T extends Record<string, unknown> = Record<string, unknown>> = {
            [P in keyof T]: P extends string ? RequestParameter<P, T[P] extends (infer R)[] ? R : T[P]> : never;
        }[keyof T][];
        /**
         * Type for the JavaScript remote command function that is created via
         *
         * ```xml
         * <p:remoteCommand name="myCommand" listener="#{myBean.action}" />
         * ```
         *
         * This creates a variable `window.myCommand` that is of this type. On the client-side, you can pass parameters to
         * the remote command via
         *
         * ```javascript
         * window.myCommand([ { name: "myParamName", value: 9 } ]);
         * ```
         *
         * On the server-side, you can access them as follows:
         *
         * ```java
         * String myParamValue = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("myParamName")
         * ```
         *
         * To send data back to the client, use
         *
         * ```java
         * PrimeFaces.current().ajax().addCallbackParam("returnParamName", true);
         * ```
         *
         * Finally, to access the returned value on the client, do
         *
         * ```javascript
         * try {
         *   const response = await window.myCommand([ { name: "myParamName", value: 9 } ]);
         *   // Success, do something with the data
         *   const value = response.jqXHR.pfArgs.returnParamName;
         * }
         * catch (e) {
         *   // Handle error
         *   console.error("Could not invoke remote command", e);
         * }
         * ```
         *
         * Please note that you should not use async-await if you need to target old browsers, use `then`/`catch` on the
         * returned promise instead. See {@link RemoteCommandParams} for more details on how to use this TypeScript type.
         * @typeParam T Object type with the param names and the corresponding param values.
         * @typeParam R Object type of the data returned by the remote command.
         */
        export type RemoteCommand<T extends Record<string, unknown> = Record<string, unknown>, R extends PrimeFacesArgs = PrimeFacesArgs> =
            /**
             * @param params Optional parameters that are passed to the remote command.
             * @return A promise that is settled when the remote command it complete. It is resolved with the data received
             * from the server, and rejected when a network or server error occurred.
             */
            (params?: RemoteCommandParams<T>) => Promise<ResponseData<R>>;
    }
}
