// Type declarations for the JSF core, see for example
// https://docs.oracle.com/javaee/7/javaserver-faces-2-2/jsdocs/symbols/jsf.html

/**
 * The top level global namespace for JavaServer Faces functionality.
 * 
 * Please note that this functionality may only be available when you use certain tags such as `<f:ajax .../>` tags.
 */
declare namespace jsf {
    /**
     * An integer specifying the implementation version that this file implements. It's a monotonically increasing
     * number, reset with every increment of `jsf.specversion` This number is implementation dependent.
     */
    export const implversion: number;

    /**
     * The result of calling `UINamingContainer.getNamingContainerSeparatorChar()`: Used for separating IDs, such as a
     * colon (`:`).
     */
    export const separatorchar: string;

    /**
     * An integer specifying the specification version that this file implements. It's format is: rightmost two digits,
     * bug release number, next two digits, minor release number, leftmost digits, major release number. This number may
     * only be incremented by a new release of the specification.
     */
    export const specversion: number;

    /**
     * Return the `windowId` of the window in which the argument form is rendered.
     *
     * @param node Determine the nature of the argument. If not present, search for the `windowId` within
     * `document.forms`. If present and the value is a string, assume the string is a DOM id and get the element with
     * that id and start the search from there. If present and the value is a DOM element, start the search from there.
     * @return The `windowId` of the current window, or `null` if the `windowId` cannot be determined.
     * @throws An error if more than one unique `WindowId` is found.
     */
    export function getClientWindow(node?: HTMLElement | string): string | null;

    /**
     * Return the value of `Application.getProjectStage()` for the currently running application instance. Calling this
     * method must not cause any network transaction to happen to the server.
     *
     * __Usage:__
     *
     * ```javascript
     * var stage = jsf.getProjectStage();
     * if (stage === ProjectStage.Development) {
     *     // ...
     * } else if stage === ProjectStage.Production) {
     *    // ...
     * }
     * ```
     *
     * @return A string representing the current state of the running application in a typical product development
     * life cycle. Refer to `javax.faces.application.Application.getProjectStage` and
     * `javax.faces.application.ProjectStage`.
     */
    export function getProjectStage(): string;

    /**
     * Collect and encode state for input controls associated with the specified form element. This will include all
     * input controls of type hidden.
     *
     * __Usage:__
     *
     * ```javascript
     * var state = jsf.getViewState(form);
     * ```
     *
     * @param form The form element whose contained input controls will be collected and encoded. Only successful
     * controls will be collected and encoded in accordance with: `Section 17.13.2` of the HTML Specification.
     * @return The encoded state for the specified form's input controls.
     */
    export function getViewState(form: HTMLFormElement): string;
}

/**
 * The namespace for Ajax functionality provided by JSF.
 * 
 * Please note that this functionality may only be available when you use certain tags such as `<f:ajax .../>` tags.
 */
declare namespace jsf.ajax {
    /**
     * Possible status codes when an AJAX request fails.
     */
    export type ErrorStatus = "httpError" | "emptyResponse" | "serverError" | "malformedXML";

    /**
     * Possible status codes when an AJAX request succeeds.
     */
    export type EventStatus = "complete" | "success" | "begin";

    /**
     * Possible status codes when an AJAX request succeeds or fails.
     */
    export type Status = ErrorStatus | EventStatus;

    /**
     * A reference to a function to call on an error, see {@link addOnError}.
     */
    export type OnErrorCallback =
        /**
         * @param data Data with details about the error and the received response.
         */
        (data: OnErrorCallbackData) => void;

    /**
     * A reference to a function to call on an event, see {@link addOnEvent}.
     */
    export type OnEventCallback =
        /**
         * @param data Data with details about the received response.
         */
        (data: OnEventCallbackData) => void;

    /**
     * Base data for an event callback, see {@link addOnError} and {@link addOnEvent}.
     * @typeparam T Type of the `type` property.
     * @typeparam S Type of the `status` property.
     */
    export interface CallbackData<T extends string, S extends string> {
        /**
         * Type of the request result.
         */
        type: T;

        /**
         * Status describing the type of success or error.
         */
        status: S;

        /**
         * ID of the source component that triggered the AJAX request.
         */
        source: string;

        /**
         * HTTP response code of the request.
         */
        responseCode?: number;

        /**
         * XML of the response.
         */
        responseXML?: XMLDocument;

        /**
         * Raw text of the response.
         */
        responseText?: string;
    }

    /**
     * Data for the callback when an AJAX request fails, see {@link addOnError}.
     */
    export interface OnErrorCallbackData extends CallbackData<"error", ErrorStatus> {
        /**
         * Name of the error, if {@link status} is set to `serverError`.
         */
        errorName?: string;

        /**
         * Message of the error, if {@link status} is set to `serverError`.
         */
        errorMessage?: string;

        /**
         * Human readable description of the error.
         */
        description: string;
    }

    /**
     * Data for the callback when an AJAX request succeeds, see {@link addOnEvent}.
     */
    export interface OnEventCallbackData extends CallbackData<"event", EventStatus> {
    }

    /**
     * The request context that will be used during error/event handling. {@link jsf.ajax.response}.
     */
    export interface RequestContext {
        /**
         * The source DOM element for the request.
         */
        source: HTMLElement;

        /**
         * The error handler for the request.
         */
        onerror: OnErrorCallback;

        /**
         * The event handler for the request.
         */
        onevent: OnEventCallback;
    }

    /**
     * The set of available options that can be sent as request parameters to control client and/or server side request
     * processing. Used by {@link jsf.ajax.request}.
     */
    export interface RequestOptions {
        /**
         * Space separated list of client identifiers
         */
        execute: string;

        /**
         * Space separated list of client identifiers.
         */
        render: string;

        /**
         * Function to callback for event.
         */
        onevent: OnEventCallback;

        /**
         * Function to callback for error.
         */
        onerror: OnErrorCallback;

        /**
         * An object containing parameters to include in the request.
         */
        params: Record<string, unknown>;

        /**
         * If less than delay milliseconds elapses between calls to request() only the most recent one is sent and all
         * other requests are discarded. If the value of delay is the literal string 'none' without the quotes, or no
         * delay is specified, no delay is used.
         */
        delay: number;

        /**
         * If true, ensure a post data argument with the name `javax.faces.partial.resetValues` and the value true is
         * sent in addition to the other post data arguments. This will cause `UIViewRoot.resetValues()` to be called,
         * passing the value of the "render" attribute. Note: do not use any of the `@` keywords such as `@form` or
         * `@this` with this option because `UIViewRoot.resetValues()` does not descend into the children of the listed
         * components.
         */
        resetValues: boolean;
    }

    /**
     * Register a callback for error handling.
     *
     * __Usage:__
     *
     * ```javascript
     * jsf.ajax.addOnError(handleError);
     * // ...
     * var handleError = function handleError(data) {
     *   //...
     * }
     * ```
     *
     * __Implementation Requirements:__
     *
     * This function must accept a reference to an existing JavaScript function. The JavaScript function reference must
     * be added to a list of callbacks, making it possible to register more than one callback by invoking
     * {@link jsf.ajax.addOnError} more than once. This function must throw an error if the callback argument is not a
     * function.
     *
     * @param callback A reference to a function to call on an error.
     */
    export function addOnError(callback: OnErrorCallback): void;

    /**
     * Register a callback for event handling.
     *
     * __Usage:__
     *
     * ```javascript
     * jsf.ajax.addOnEvent(statusUpdate);
     *   // ...
     * var statusUpdate = function statusUpdate(data) {
     *   // ...
     * }
     * ```
     *
     * __Implementation Requirements:__
     *
     * This function must accept a reference to an existing JavaScript function. The JavaScript function reference must
     * be added to a list of callbacks, making it possible to register more than one callback by invoking
     * {@link jsf.ajax.addOnEvent} more than once. This function must throw an error if the callback argument is not a
     * function.
     *
     * @param callback A reference to a function to call on an event.
     */
    export function addOnEvent(callback: OnEventCallback): void;

    /**
     * Send an asynchronous Ajax request to the server.
     *
     * __Usage:__
     *
     * Example showing all optional arguments:
     *
     * ```xml
     * <commandButton id="button1" value="submit"
     *     onclick="jsf.ajax.request(this,event,
     *       {execute:'button1',render:'status',onevent: handleEvent,onerror: handleError});return false;"/>
     * </commandButton/>
     * ```
     *
     * __Implementation Requirements:__
     *
     * This function must:
     * - Be used within the context of a form.
     * - Capture the element that triggered this Ajax request (from the source argument, also known as the source
     *   element.
     * - If the source element is `null` or `undefined` throw an error.
     * - If the source argument is not a string or DOM element object, throw an error.
     * - If the source argument is a string, find the DOM element for that string identifier.
     * - If the DOM element could not be determined, throw an error.
     * - If the `onerror` and `onevent` arguments are set, they must be functions, or throw an error.
     * - Determine the source element's form element.
     * - Get the form view state by calling {@link jsf.getViewState} passing the form element as the argument.
     * - Collect post data arguments for the Ajax request.
     *     - The following name/value pairs are required post data arguments:
     *         - `javax.faces.ViewState` - Contents of `javax.faces.ViewState` hidden field. This is included when
     *           {@link jsf.getViewState} is used.
     *         - `javax.faces.partial.ajax` - `true`
     *         - `javax.faces.source` - The identifier of the element that triggered this request.
     *         - `javax.faces.ClientWindow` - Call {@link jsf.getClientWindow}, passing the current form. If the return
     *           is non-null, it must be set as the value of this name/value pair, otherwise, a name/value pair for
     *           client window must not be sent.
     * - Collect optional post data arguments for the Ajax request.
     *     - Determine additional arguments (if any) from the `options` argument. If `options.execute` exists:
     *         - If the keyword `@none` is present, do not create and send the post data argument
     *           `javax.faces.partial.execute`.
     *         - If the keyword `@all` is present, create the post data argument with the name
     *           `javax.faces.partial.execute` and the value `@all`.
     *         - Otherwise, there are specific identifiers that need to be sent. Create the post data argument with the
     *           name `javax.faces.partial.execute` and the value as a space delimited string of client identifiers.
     *     - If `options.execute` does not exist, create the post data argument with the name
     *       `javax.faces.partial.execute` and the value as the identifier of the element that caused this request.
     *     - If `options.render` exists:
     *         - If the keyword `@none` is present, do not create and send the post data argument
     *           `javax.faces.partial.render`.
     *         - If the keyword `@all` is present, create the post data argument with the name
     *           `javax.faces.partial.render` and the value `@all`.
     *         - Otherwise, there are specific identifiers that need to be sent. Create the post data argument with the
     *           name `javax.faces.partial.render` and the value as a space delimited string of client identifiers.
     *     - If `options.render` does not exist do not create and send the post data argument
     *       `javax.faces.partial.render`.
     *     - If `options.delay` exists let it be the value delay, for this discussion. If options.delay does not exist,
     *       or is the literal string 'none', without the quotes, no delay is used. If less than delay milliseconds
     *       elapses between calls to `request()` only the most recent one is sent and all other requests are discarded.
     *     - If `options.resetValues` exists and its value is true, ensure a post data argument with the name
     *       `javax.faces.partial.resetValues` and the value true is sent in addition to the other post data arguments.
     *       This will cause `UIViewRoot.resetValues()` to be called, passing the value of the `render` attribute. Note:
     *       do not use any of the `@` keywords such as `@form` or `@this` with this option because
     *       `UIViewRoot.resetValues()` does not descend into the children of the listed components.
     *     - Determine additional arguments (if any) from the event argument. The following name/value pairs may be used
     *       from the event object:
     *         - `target` - the `ID` of the element that triggered the event.
     *         - `captured` - the `ID` of the element that captured the event.
     *         - `type` - the type of event (ex: `onkeypress`)
     *         - `alt` - true if `ALT` key was pressed.
     *         - `ctrl` - true if `CTRL` key was pressed.
     *         - `shift` - true if `SHIFT` key was pressed.
     *         - `meta` - true if `META` key was pressed.
     *         - `right` - true if right mouse button was pressed.
     *         - `left` - true if left mouse button was pressed.
     *         - `keycode` - the key code.
     * - Encode the set of post data arguments.
     * - Join the encoded view state with the encoded set of post data arguments to form the query string that will be
     *   sent to the server.
     * - Create a request context object that will be used during error/event handling. The function must set the
     *   properties:
     *     - `source` (the source DOM element for this request)
     *     - `onerror` (the error handler for this request)
     *     - `onevent` (the event handler for this request)
     * - Send a begin event following the procedure as outlined in the Chapter 13 `Sending Events` section of the Java
     *   Server Faces spec.
     * - Set the request header with the name: `Faces-Request` and the value: `partial/ajax`.
     * - Determine the `posting URL` as follows: If the hidden field `javax.faces.encodedURL` is present in the
     *   submitting form, use its value as the posting URL. Otherwise, use the action property of the form element as
     *   the URL.
     * - Determine whether or not the submitting form is using `multipart/form-data` as its enctype attribute. If not,
     *   send the request as an asynchronous POST using the posting URL that was determined in the previous step.
     *   Otherwise, send the request using a multi-part capable transport layer, such as a hidden inline frame. Note
     *   that using a hidden inline frame does not use `XMLHttpRequest`, but the request must be sent with all the
     *   parameters that a JSF `XMLHttpRequest` would have been sent with. In this way, the server side processing of
     *   the request will be identical whether or the request is multipart or not.
     *
     *   The begin, complete, and success events must be emulated when using the multipart transport. This allows any
     * listeners to behave uniformly regardless of the multipart or XMLHttpRequest nature of the transport.
     *
     * Form serialization should occur just before the request is sent to minimize the amount of time between the
     * creation of the serialized form data and the sending of the serialized form data (in the case of long requests in
     * the queue). Before the request is sent it must be put into a queue to ensure requests are sent in the same order
     * as when they were initiated. The request callback function must examine the queue and determine the next request
     * to be sent. The behavior of the request callback function must be as follows:
     *
     * - If the request completed successfully invoke {@link jsf.ajax.response} passing the request object.
     * - If the request did not complete successfully, notify the client.
     * - Regardless of the outcome of the request (success or error) every request in the queue must be handled. Examine
     *   the status of each request in the queue starting from the request that has been in the queue the longest. If
     *   the status of the request is complete (readyState 4), dequeue the request (remove it from the queue). If the
     *   request has not been sent (readyState 0), send the request. Requests that are taken off the queue and sent
     *   should not be put back on the queue.
     *
     * @param source The DOM element that triggered this AJAX request, or an ID string of the element to use as the
     * triggering element.
     * @param event The DOM event that triggered this Ajax request. The event argument is optional.
     * @param options The set of available options that can be sent as request parameters to control client and/or
     * server side request processing.
     * @throws Error if first required argument element is not specified, or if one or more of the components in the
     * `options.execute` list is a file upload component, but the form's enctype is not set to `multipart/form-data`.
     */
    export function request(source: HTMLElement | string, event?: Event, options?: Partial<RequestOptions>): void;

    /**
     * Receive an Ajax response from the server.
     *
     * __Usage:__
     *
     * ```javascript
     * jsf.ajax.response(request, context);
     * ```
     *
     * __Implementation Requirements:__
     *
     * This function must evaluate the markup returned in the `request.responseXML` object and perform the following
     * action:
     *
     * - If there is no XML response returned, signal an emptyResponse error. If the XML response does not follow the
     *   format as outlined in Appendix A of the Java Server Faces spec prose document signal a `malformedError` error.
     *   Refer to section `Signaling Errors` in Chapter 13 of the Java Server Faces spec prose document.
     * - If the response was successfully processed, send a success event as outlined in Chapter 13 `Sending Events`
     *   section of the Java Server Faces spec prose document.
     *
     * _Update Element Processing_
     *
     * The `update` element is used to update a single DOM element. The `id` attribute of the update element refers to
     * the DOM element that will be updated. The contents of the `CDATA` section is the data that will be used when
     * updating the contents of the DOM element as specified by the `<update>` element identifier.
     *
     * - If an `<update>` element is found in the response with the identifier `javax.faces.ViewRoot`:
     *
     *   ```xml
     *   <update id="javax.faces.ViewRoot">
     *       <![CDATA[...]]>
     *   </update>
     *   ```
     *
     *   Update the entire DOM replacing the appropriate `head` and/or `body` sections with the content from the
     *   response.
     *
     * - If an `<update>` element is found in the response with an identifier containing `javax.faces.ViewState`:
     *
     *   ```xml
     *   <update id="<VIEW_ROOT_CONTAINER_CLIENT_ID><SEP>javax.faces.ViewState<SEP><UNIQUE_PER_VIEW_NUMBER>">
     *       <![CDATA[...]]>
     *   </update>
     *   ```
     *
     *   locate and update the submitting form's `javax.faces.ViewState` value with the `CDATA` contents from the
     *   response. `<SEP>`: is the currently configured `UINamingContainer.getSeparatorChar()`.
     *   `<VIEW_ROOT_CONTAINER_CLIENT_ID>` is the return from `UIViewRoot.getContainerClientId()` on the view from
     *   whence this state originated. `<UNIQUE_PER_VIEW_NUMBER>` is a number that must be unique within this view, but
     *   must not be included in the view state. This requirement is simply to satisfy XML correctness in parity with
     *   what is done in the corresponding non-partial JSF view. Locate and update the `javax.faces.ViewState` value for
     *   all forms specified in the render target list.
     *
     * - If an update element is found in the response with an identifier containing `javax.faces.ClientWindow`:
     *
     *   ```xml
     *   <update id="<VIEW_ROOT_CONTAINER_CLIENT_ID><SEP>javax.faces.ClientWindow<SEP><UNIQUE_PER_VIEW_NUMBER>">
     *       <![CDATA[...]]>
     *   </update>
     *   ```
     *
     *   locate and update the submitting form's `javax.faces.ClientWindow` value with the `CDATA` contents from the
     *   response. `<SEP>`: is the currently configured `UINamingContainer.getSeparatorChar()`.
     *   `<VIEW_ROOT_CONTAINER_CLIENT_ID>` is the return from `UIViewRoot.getContainerClientId()` on the view from
     *   whence this state originated. `<UNIQUE_PER_VIEW_NUMBER>` is a number that must be unique within this view, but
     *   must not be included in the view state. This requirement is simply to satisfy XML correctness in parity with
     *   what is done in the corresponding non-partial JSF view. Locate and update the `javax.faces.ClientWindow` value
     *   for all forms specified in the render target list.
     *
     * - If an update element is found in the response with the identifier `javax.faces.ViewHead`:
     *
     *   ```xml
     *   <update id="javax.faces.ViewHead">
     *       <![CDATA[...]]>
     *   </update>
     *   ```
     *
     *   update the document's `head` section with the `CDATA` contents from the response.
     *
     * - If an update element is found in the response with the identifier `javax.faces.ViewBody`:
     *
     *   ```xml
     *   <update id="javax.faces.ViewBody">
     *       <![CDATA[...]]>
     *   </update>
     *   ```
     *
     *   update the document's `body` section with the `CDATA` contents from the response.
     *
     * - For any other `<update>` element:
     *
     *   ```xml
     *   <update id="update id">
     *       <![CDATA[...]]>
     *   </update>
     *   ```
     *
     *   Find the DOM element with the identifier that matches the `<update>` element identifier, and replace its
     *   contents with the `<update>` element's `CDATA` contents.
     *
     * _Insert Element Processing_
     *
     * - If an `<insert>` element is found in the response with a nested `<before>` element:
     *
     *   ```xml
     *   <insert>
     *       <before id="before id">
     *           <![CDATA[...]]>
     *       </before>
     *   </insert>
     *   ```
     *
     *     - Extract this `<before>` element's `CDATA` contents from the response.
     *     - Find the DOM element whose identifier matches before id and insert the `<before>` element's `CDATA` content
     *       before the DOM element in the document.
     *
     * - If an <insert> element is found in the response with a nested <after> element:
     *
     *   ```xml
     *   <insert>
     *       <after id="after id">
     *           <![CDATA[...]]>
     *       </after>
     *   </insert>
     *   ```
     *
     *     - Extract this `<after>` element's `CDATA` contents from the response.
     *     - Find the DOM element whose identifier matches after id and insert the `<after>` element's `CDATA` content
     *       after the DOM element in the document.
     *
     * _Delete Element Processing_
     *
     * - If a `<delete>` element is found in the response:
     *
     *   ```xml
     *   <delete id="delete id"/>
     *   ```
     *
     *   Find the DOM element whose identifier matches `delete id` and remove it from the DOM.
     *
     * _Element Attribute Update Processing_
     *
     * - If an `<attributes>` element is found in the response:
     *
     *   ```xml
     *   <attributes id="id of element with attribute">
     *       <attribute name="attribute name" value="attribute value">
     *        <!-- ... -->
     *   </attributes>
     *   ```
     *
     *     - Find the DOM element that matches the `<attributes>` identifier.
     *     - For each nested `<attribute>` element in `<attribute>`, update the DOM element attribute value (whose name
     *       matches attribute name), with attribute value.
     *
     * _JavaScript Processing_
     *
     * - If an `<eval>` element is found in the response:
     *
     *   ```xml
     *   <eval>
     *       <![CDATA[...JavaScript...]]>
     *   </eval>
     *   ```
     *
     *     - Extract this `<eval>` element's `CDATA` contents from the response and execute it as if it were JavaScript
     *       code.
     *
     * _Redirect Processing_
     *
     * - If a `<redirect>` element is found in the response:
     *
     *   ```xml
     *   <redirect url="redirect url"/>
     *   ```
     *
     *   Cause a redirect to the `url redirect url`.
     *
     * _Error Processing_
     *
     * - If an `<error>` element is found in the response:
     *
     *   ```xml
     *   <error>
     *       <error-name>..fully qualified class name string...<error-name>
     *       <error-message><![CDATA[...]]><error-message>
     *   </error>
     *   ```
     *
     *   Extract this `<error>` element's error-name contents and the error-message contents. Signal a `serverError`
     *   passing the `errorName` and `errorMessage`. Refer to section `Signaling Errors` in Chapter 13 of the Java
     *   Server Faces spec prose document.
     *
     * _Extensions_
     *
     * - The `<extensions>` element provides a way for framework implementations to provide their own information.
     * - The implementation must check if `<script>` elements in the response can be automatically run, as some browsers
     *   support this feature and some do not. If they can not be run, then scripts should be extracted from the
     *   response and run separately.
     *
     * @param request The `XMLHttpRequest` instance that contains the status code and response message from the server.
     * @param context An object containing the request context, including the following properties: the source element,
     * per call onerror callback function, and per call onevent callback function.
     * @throws Error if request contains no data.
     */
    export function response(request: XMLHttpRequest, context: RequestContext): void;
}

/**
 * The namespace for JavaServer Faces JavaScript utilities.
 * 
 * Please note that this functionality may only be available when you use certain tags such as `<f:ajax .../>` tags.
 */
declare namespace jsf.util {
    /**
     * A varargs function that invokes an arbitrary number of scripts. If any script in the chain returns false, the
     * chain is short-circuited and subsequent scripts are not invoked. Any number of scripts may specified after the
     * `event` argument.
     * @param source The DOM element that triggered this Ajax request, or an id string of the element to use as the
     * triggering element.
     * @param event The DOM event that triggered this Ajax request. The `event` argument is optional.
     * @param scripts List of scripts to execute in a function scope. Receives the source as the this context and one
     * parameter `event`, set to the value passed to this function.
     * @return `false` if any scripts in the chain return `false`, otherwise returns `true`.
     */
    export function chain(source: HTMLElement | string, event?: Event | undefined, ...scripts: string[]): boolean;
}