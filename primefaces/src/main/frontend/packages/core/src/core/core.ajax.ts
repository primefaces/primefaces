import { csp } from "./core.csp.js";
import { expressions } from "./core.expressions.js";
import { core } from "./core.js";
import { utils } from "./core.utils.js";
import type { BaseWidget } from "./core.widget.js";

import { AjaxExceptionHandler } from "../ajaxexceptionhandler/ajaxexceptionhandler.js";

const unloadEvent = ("onpagehide" in window) ? "pagehide" : "unload";

/**
 * Retrieves the text content of a node.
 * @param node Node from which to get its text.
 * @returns The text content of the node. 
 */
function getNodeTextContent(node: Node): string {
    if (node.textContent) {
        return node.textContent;
    }
    if (node instanceof HTMLElement && node.innerText) {
        return node.innerText;
    }
    // Previously, we also tried node.text, but that's a old IE (IE6?) feature
    return "";
}

/**
 * The class containing utility methods for AJAX requests, primarily used internally.
 */
export class AjaxUtils {
    /**
     * Iterates over all immediate children of the given node and returns the concatenated content (`node value`)
     * of each such child node. For the document itself, the node value is `null`.
     * For text, comment, and CDATA nodes, the `node value` is the (text) content of the node.
     * For attribute nodes, the value of the attribute is used.
     * @param node An element for which to retrieve the content.
     * @return The content of all immediate child nodes, concatenated together.
     */
    getContent(node: Element | undefined | null): string {
        var content = '';

        if (node) {
            for (const child of node.childNodes) {
                content += child.nodeValue;
            }
        }

        return content;
    }

    /**
     * Checks if the given form is a Faces form.
     * @param form The form to check.
     * @return `true` if the form is a Faces form.
     */
    isFacesForm(form: HTMLFormElement): boolean {
        if (form.method === 'post') {
            for (let child of form.children) {
                if (child instanceof HTMLInputElement && child.name && child.name.includes(core.VIEW_STATE)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Resolves the URL which should be used for the POST request.
     * For portlets, a different URL is used.
     *
     * @param form The closest form of the request source.
     * @return The POST url.
     */
    getPostUrl(form: JQuery): string {
        let postURL: string | string[] | number | undefined = form.attr('action');
        let encodedURLInput = form.children("input[name*='jakarta.faces.encodedURL']");

        if (encodedURLInput.length > 0) {
            postURL = encodedURLInput.val();
        }

        return typeof postURL === "string" ? postURL : "";
    }

    /**
     * Gets a selector to resolve all forms which needs to be updated with a new ViewState.
     * This is required in portlets as the DOM contains forms of multiple Faces views / applications.
     *
     * @param form The closest form of the request source.
     * @param parameterPrefix The portlet parameter prefix.
     * @return The selector for the forms, or `null` when no forms need to be updated.
     */
    getPorletForms(form: JQuery, parameterPrefix: string): string | null {
        const encodedURLInput = form.children("input[name*='jakarta.faces.encodedURL']");

        if (encodedURLInput.length > 0) {
            return 'form[id*="' + parameterPrefix + '"]';
        }

        return null;
    }

    /**
     * Get source ID from settings.
     *
     * @param settings containing source ID.
     * @return The source ID from settings or `null` if settings does not contain a source.
     */
    getSourceId(settings: PrimeType.ajax.PrimeFacesSettings): string | null {
        if (settings && settings.source) {
            if (typeof settings.source === 'string') {
                return settings.source;
            }
            if (settings.source instanceof Element) {
                return settings.source.getAttribute("name");
            }
            return settings.source.attr("name") ?? null;
        }
        return null;
    }

    /**
     * Checks whether the component ID from the provided widget equals the source ID from the provided
     * settings.
     *
     * @param widget of the component to check for being the source.
     * @param settings containing source ID.
     * @returns `true` if the component ID from the provided widget equals the source ID from the
     * provided settings.
     */
    isXhrSource(widget: BaseWidget, settings: JQuery.AjaxSettings): boolean {
        return widget.id === ajax.Utils.getSourceId(settings);
    }

    /**
     * Checks whether one of component's triggers equals the source ID from the provided settings.
     *
     * @param widget of the component to check for being the source.
     * @param settings containing source ID.
     * @param triggerMustExist flag to check if the trigger must exist
     * @returns `true` if if one of component's triggers equals the source ID from the provided settings.
     */
    isXhrSourceATrigger(widget: BaseWidget<any>, settings: JQuery.AjaxSettings, triggerMustExist: boolean): boolean {
        var sourceId = ajax.Utils.getSourceId(settings);
        if (!sourceId) {
            return false;
        }
        var cfgTrigger = widget.cfg.trigger || widget.cfg.triggers;
        // we must evaluate it each time as the DOM might has been changed
        var triggers = expressions.SearchExpressionFacade.resolveComponents(widget.jq, cfgTrigger);

        // if trigger is null it has been removed from DOM so we need to hide the block UI
        if (!triggers || triggers.length === 0) {
            return !triggerMustExist;
        }

        return $.inArray(sourceId, triggers) !== -1;
    }

    /**
     * Is this script an AJAX request?
     * @param script the JS script to check
     * @returns `true` if this script contains an AJAX request
     */
    isAjaxRequest(script: string): boolean {
        return script.includes("PrimeFaces.ab(") || script.includes("pf.ab(")
            || script.includes("mojarra.ab(")
            || script.includes("myfaces.ab(")
            || script.includes("faces.ajax.request");
    }

    /**
     * Updates the main hidden input element for each form.
     * @param name Name of the hidden form input element, usually the same as the form.
     * @param value Value to set on the hidden input element.
     * @param xhr Optional XHR request with `pfSettings` or `pfArgs` with further
     * data, such as which forms should be updated.
     */
    updateFormStateInput(name: string, value: string, xhr?: PrimeType.ajax.pfXHR): void {
        var trimmedValue = core.trim(value);

        var forms = null;
        if (xhr && xhr.pfSettings && xhr.pfSettings.portletForms) {
            forms = $(xhr.pfSettings.portletForms);
        }
        else {
            forms = $('form');
        }

        let parameterPrefix = '';
        if (xhr && xhr.pfArgs && xhr.pfArgs.parameterPrefix) {
            parameterPrefix = typeof xhr.pfArgs.parameterPrefix === "string" ? xhr.pfArgs.parameterPrefix : "";
        }

        for (var i = 0; i < forms.length; i++) {
            var form = forms.eq(i);

            if (form.attr('method') === 'post') {
                var input = form.children("input[name='" + CSS.escape(parameterPrefix + name) + "']");

                if (input.length > 0) {
                    input.val(trimmedValue);
                } else {
                    form.append('<input type="hidden" name="' + parameterPrefix + name + '" value="' + trimmedValue + '"></input>');
                }
            }
        }
    }

    /**
     * Updates the HTML `head` element of the current document with the content received from an AJAX request.
     * This method ensures that any new JavaScript or CSS resources are only added if they are not already present.
     * If the content does not contain any JavaScript or CSS links, it is directly appended to the head.
     * 
     * @param {string} content The content of the changeset that was returned by an AJAX request.
     */
    updateResource(content: string): void {
        var $head = $("head");
        try {
            var $content = $(content);
            var filteredContent = $content.length > 0 ? $content.filter("link[href], script[src]") : $();

            if (filteredContent.length === 0) {
                core.debug("Adding content to the head because it lacks any JavaScript or CSS links...");
                $head.append(content);
            } else {
                // #11714 Iterate through each script and stylesheet tag in the content
                // checking if resource is already attached to the head and adding it if not
                for (const resource of filteredContent) {
                    var $resource = $(resource);
                    var src = $resource.attr("href") || $resource.attr("src");
                    var type = resource.tagName.toLowerCase();
                    var $resources = $head.find(type + '[src="' + src + '"], ' + type + '[href="' + src + '"]');

                    // Check if script or stylesheet already exists and add it to head if it does not
                    if ($resources.length === 0) {
                        core.debug("Appending " + type + " to head: " + src);
                        $head.append($resource);
                    }
                }
            }
        } catch (error) {
            // MYFACES-4378 is incorrectly sending executable code here in the Resource section
            core.debug("Appending content to the head as it contains only raw JavaScript code...");
            $head.append(content);
        }
    }

    /**
     * Updates the HTML `head` element of the current document with the content received from an AJAX request.
     * @param content The content of the changeset that was returned by an AJAX request.
     */
    updateHead(content: string): void {
        var cache = $.ajaxSetup({})['cache'];
        $.ajaxSetup({})['cache'] = true;

        const headStartTag = /<head[^>]*>/gi.exec(content)?.[0] ?? "";
        const headStartIndex = content.indexOf(headStartTag) + headStartTag.length;
        $('head').html(content.substring(headStartIndex, content.lastIndexOf("</head>")));

        $.ajaxSetup({})['cache'] = cache;
    }

    /**
     * Updates the HTML `body` element of the current document with the content received from an AJAX request.
     * @param content The content of the changeset that was returned by an AJAX request.
     */
    updateBody(content: string): void {
        const bodyStartTag = /<body[^>]*>/gi.exec(content)?.[0] ?? "";
        const bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
        $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
    }

    /**
     * Updates an element with the given ID by applying a change set that was returned by an AJAX request. This
     * involves replacing the HTML content of the element with the new content.
     * @param id ID of the element that is to be updated.
     * @param content The new content of the changeset as returned by an AJAX request.
     * @param xhr Optional XHR request with `pfSettings` or `pfArgs` with further
     * data, such as which forms should be updated.
     */
    updateElement(id: string, content: string, xhr?: PrimeType.ajax.pfXHR): void {

        if (id.indexOf(core.VIEW_STATE) !== -1) {
            ajax.Utils.updateFormStateInput(core.VIEW_STATE, content, xhr);
        }
        else if (id.indexOf(core.CLIENT_WINDOW) !== -1) {
            ajax.Utils.updateFormStateInput(core.CLIENT_WINDOW, content, xhr);
        }
        // used by @all
        else if (id === core.VIEW_ROOT) {

            // reset PrimeFaces JS state because the view is completely replaced with a new one
            core.resetState();

            ajax.Utils.updateHead(content);
            ajax.Utils.updateBody(content);
        }
        else if (id === ajax.VIEW_HEAD) {
            ajax.Utils.updateHead(content);
        }
        else if (id === ajax.VIEW_BODY) {
            ajax.Utils.updateBody(content);
        }
        else if (id === ajax.RESOURCE) {
            ajax.Utils.updateResource(content);
        }
        else if (id === $('head')[0]?.id) {
            ajax.Utils.updateHead(content);
        }
        else {
            var target = $(core.escapeClientId(id));
            if (target.length === 0) {
                core.warn("DOM element with id '" + id + "' cant be found; skip update...");
            }
            else {
                var removedContent = target.replaceWith(content);
                // detach all handlers and data to clean up DOM
                utils.cleanseDomElement(removedContent);
            }
        }
    }

    /**
     * Handle the error either by calling the p:ajaxExceptionHandlers, trying to redirect to the error-page or by logging.
     * @param errorName The error name.
     * @param errorMessage The error message.
     */
    handleError(errorName: string, errorMessage: string): void {
        let exceptionHandlers: AjaxExceptionHandler[] = [];
        if (errorName) {
            // try to invoke specific AjaxExceptionHandler
            exceptionHandlers = core.getWidgetsByType(AjaxExceptionHandler);
            for (var exceptionHandler of exceptionHandlers) {
                if (exceptionHandler.handles(errorName)) {
                    exceptionHandler.handle(errorName, errorMessage);
                    return;
                }
            }
        }

        // try to invoke global AjaxExceptionHandler
        var globalExceptionHandler = exceptionHandlers.find(widget => widget.isGlobal());
        if (globalExceptionHandler) {
            globalExceptionHandler.handle(errorName, errorMessage);
            return;
        }

        if (core.settings.errorPages) {
            var errorPageUrl = null;
            if (errorName) {
                // strip off 'class ' prefix, which is logged with exception class
                if (errorName.startsWith('class ')) {
                    errorPageUrl = core.settings.errorPages[errorName.replace('class ', '')];
                }
                else {
                    errorPageUrl = core.settings.errorPages[errorName];
                }
            }
            if (!errorPageUrl) {
                errorPageUrl = core.settings.errorPages['java.lang.Throwable'];
            }
            if (!errorPageUrl) {
                errorPageUrl = core.settings.errorPages[''];
            }

            if (errorPageUrl) {
                try {
                    core.debug("Redirect to error page: " + errorPageUrl)
                    window.location.assign(errorPageUrl);
                } catch (error) {
                    core.warn('Error redirecting to URL: ' + errorPageUrl);
                }

                return;
            }
        }

        core.error("No ajaxExceptionHandler or error page found for '" + errorName + "' defined!");
        if (errorMessage) {
            core.error(errorMessage);
        }
    }
}

/**
 * This class contains functionality related to queuing AJAX requests to ensure that they are (a) sent in the
 * proper order and (b) that each response is processed in the same order as the requests were sent.
 */
export class AjaxQueue {
    /**
     * A map between the source ID and  the timeout IDs (as returned by `setTimeout`). Used for AJAX requests
     * with a specified delay (such as remote commands that have a delay set).
     */
    private delays: Record<string, { timeout: number | undefined }> = {};

    /**
     * A list of requests that are waiting to be sent.
     */
    private requests: PrimeType.ajax.Configuration[] = [];

    /**
     * A list of sent AJAX requests, i.e. HTTP requests that were already started. This is used, for example, to
     * abort requests that were sent already when that becomes necessary.
     */
    private xhrs: PrimeType.ajax.pfXHR[] = [];

    /**
     * Offers an AJAX request to this queue. The request is sent once all other requests in this queue have
     * been sent. If a delay is set on the request configuration, the request is not sent before the specified
     * delay has elapsed.
     * @param request The request to send.
     */
    offer(request: PrimeType.ajax.Configuration): void {
        if (request.delay) {
            const sourceId = typeof request.source === "string"
                ? request.source
                : request.source !== undefined ? $(request.source).attr("id") ?? "" : "";
            const createTimeout = () => {
                return core.queueTask(() => {
                    this.requests.push(request);

                    if (this.requests.length === 1) {
                        ajax.Request.send(request);
                    }
                }, request.delay);
            };

            if (this.delays[sourceId]) {
                clearTimeout(this.delays[sourceId].timeout);
                this.delays[sourceId].timeout = createTimeout();
            }
            else {
                this.delays[sourceId] = {
                    timeout: createTimeout()
                };
            }
        }
        else {
            this.requests.push(request);

            if (this.requests.length === 1) {
                ajax.Request.send(request);
            }
        }
    }

    /**
     * Removes the topmost request (the requests that was just sent) from this queue; and starts the second
     * topmost request.
     * @return The topmost request in this queue, or `null` if this queue is empty.
     */
    poll(): PrimeType.ajax.Configuration | null {
        if (this.isEmpty()) {
            return null;
        }

        const processed = this.requests.shift();
        const next = this.peek();

        if (next) {
            ajax.Request.send(next);
        }

        return processed ?? null;
    }

    /**
     * Returns the request that is scheduled to be sent next, but does not modify the queue in any way.
     * @return The topmost request in this queue that is to be sent next,
     * or `null` when this queue is empty.
     */
    peek(): PrimeType.ajax.Configuration | null {
        if (this.isEmpty()) {
            return null;
        }

        return this.requests[0] ?? null;
    }

    /**
     * Checks whether this queue contains any scheduled AJAX requests.
     * @return `true` if this queue contains no scheduled requests, `false` otherwise.
     */
    isEmpty(): boolean {
        return this.requests.length === 0;
    }

    /**
     * Adds a newly sent XHR request to the list of sent requests ({@link AjaxQueue.xhrs | PrimeFaces.ajax.Queue.xhrs}).
     * @param xhr XHR request to add.
     */
    addXHR(xhr: PrimeType.ajax.pfXHR): void {
        this.xhrs.push(xhr);
    }

    /**
     * Removes an XHR request from the list of sent requests ({@link AjaxQueue.xhrs | PrimeFaces.ajax.Queue.xhrs}). Usually
     * called once the AJAX request is done, having resulted in either a success or an error.
     * @param xhr XHR request to remove.
     */
    removeXHR(xhr: PrimeType.ajax.pfXHR): void {
        var index = $.inArray(xhr, this.xhrs);
        if (index > -1) {
            this.xhrs.splice(index, 1);
        }
    }

    /**
     * Aborts all requests that were already sent, but have not yet received an answer from the server. Also
     * removes all requests that are waiting in the queue and have not been sent yet.
     */
    abortAll(): void {
        // clear out any pending requests
        this.requests = [];

        // abort any in-flight that are not DONE(4)
        for (const xhr of this.xhrs) {
            if (xhr.readyState !== 4) {
                xhr.abort();
            }
        }

        this.xhrs = [];
    }
}

/**
 * The class containing low-level functionality related to sending AJAX requests.
 */
export class AjaxRequest {
    /**
     * Handles the given AJAX request, either by sending it immediately (if `async` is set to `true`), or by
     * adding it to the AJAX queue otherwise. The AJAX queue ensures that requests are sent and handled in the
     * order they were started. See also {@link faces.ajax.request}.
     * @param cfg Configuration for the AJAX request to send, such as
     * the HTTP method, the URL, and the content of the request.
     * @param ext Optional extender with additional options
     * that overwrite the options given in `cfg`.
     * @return A promise that resolves once the AJAX requests is done.
     * Use this to run custom JavaScript logic. When the AJAX request succeeds, the promise is fulfilled.
     * Otherwise, when the AJAX request fails, the promise is rejected. If the promise is rejected, the
     * rejection handler receives an object of type {@link PrimeType.ajax.FailedRequestData}.
     */
    handle(
        cfg: PrimeType.ajax.Configuration,
        ext?: PrimeType.ajax.ConfigurationExtender
    ): PromiseLike<PrimeType.ajax.ResponseData>  {
        cfg.ext = ext;
        cfg.promise = cfg.promise || $.Deferred();

        if (core.settings.earlyPostParamEvaluation) {
            cfg.earlyPostParams = this.collectEarlyPostParams(cfg);
        }

        if (cfg.async) {
            this.send(cfg);
        }
        else {
            ajax.Queue.offer(cfg);
        }

        return cfg.promise.promise();
    }

    /**
     * Performs the early collection of post parameters (form element values) if the request is configured that
     * way. See: https://github.com/primefaces/primefaces/issues/109
     *
     * @param cfg Configuration for the AJAX request to send, such as
     * the HTTP method, the URL, and the content of the request.
     * @return The collected form element values to be sent with the request.
     */
    collectEarlyPostParams(cfg: PrimeType.ajax.Configuration): JQuery.NameValuePair[] {

        let earlyPostParams: JQuery.NameValuePair[];

        let sourceElement: JQuery;
        if (typeof (cfg.source) === 'string') {
            sourceElement = $(core.escapeClientId(cfg.source));
        }
        else {
            sourceElement = cfg.source ? $(cfg.source) : $();
        }
        if (sourceElement.is(':input') && sourceElement.is(':not(:button)')) {
            earlyPostParams = [];

            if (sourceElement.is(':checkbox')) {
                var checkboxPostParams = $("input[name='" + CSS.escape(sourceElement.attr('name') ?? "") + "']")
                        .filter(':checked').serializeArray();
                $.merge(earlyPostParams, checkboxPostParams);
            }
            else {
                earlyPostParams.push({
                    name: sourceElement.attr('name') ?? "",
                    value: String(sourceElement.val()),
                });
            }
        }
        else {
            earlyPostParams = sourceElement.serializeArray();
        }

        return earlyPostParams;
    }

    /**
     * Starts the given AJAX request immediately by sending the data to the server. Contrast with
     * {@link handle}, which may queue AJAX requests, depending on how they are configured.
     * @param cfg Configuration for the AJAX request to send, such as
     * the HTTP method, the URL, and the content of the request.
     * @return `false` if the AJAX request is to be canceled, `true` or `undefined` otherwise.
     */
    send(cfg: PrimeType.ajax.Configuration): boolean | undefined {
        core.debug('Initiating ajax request.');

        core.customFocus = false;

        let global = cfg.global !== false;
        let form: null | JQuery = null;
        let retVal: unknown = null;

        if (cfg.onstart) {
            retVal = cfg.onstart.call(this, cfg);
        }
        if (cfg.ext && cfg.ext.onstart) {
            retVal = cfg.ext.onstart.call(this, cfg);
        }

        if (retVal === false) {
            core.debug('AJAX request cancelled by onstart callback.');

            //remove from queue
            if (!cfg.async) {
                ajax.Queue.poll();
            }

            if (cfg.promise) {
                cfg.promise.reject({ textStatus: 'error', errorThrown: 'AJAX request cancelled by onstart callback.' });
            }

            return false;  //cancel request
        }

        if (global) {
            $(document).trigger('pfAjaxStart');
        }

        let sourceId: string;
        if (typeof (cfg.source) === 'string') {
            sourceId = cfg.source;
        } else if (cfg.source !== undefined) {
            sourceId = $(cfg.source).attr('id') ?? "";
        } else {
            sourceId = "";
        }

        var $source = $(core.escapeClientId(sourceId));

        if (cfg.formId) {
            //Explicit form is defined
            form = expressions.SearchExpressionFacade.resolveComponentsAsSelector($source, cfg.formId);
        }
        else {
            //look for a parent of source
            form = $source.closest('form');

            //source has no parent form so use first form in document
            if (form.length === 0) {
                form = $('form').filter(function(index, element) {
                    return core.ajax.Utils.isFacesForm(element);
                }).first();
            }
        }

        core.debug('Form to post ' + form?.attr('id') + '.');

        let formData: FormData | undefined = undefined;
        let scanForFiles: JQuery = $();
        let multipart = form?.attr('enctype') === 'multipart/form-data';
        if (multipart) {
            formData = new FormData();
            scanForFiles = $();
        }

        const postURL = ajax.Utils.getPostUrl(form);
        let postParams: PrimeType.ajax.RequestParameter<string, string | Blob>[] = [];

        // See #6857 - parameter namespace for Portlets
        const parameterPrefix = this.extractParameterNamespace(form);

        core.debug('URL to post ' + postURL + '.');

        //partial ajax
        this.addParam(postParams, core.PARTIAL_REQUEST_PARAM, "true", parameterPrefix);

        //source
        this.addParam(postParams, core.PARTIAL_SOURCE_PARAM, sourceId, parameterPrefix);

        //resetValues
        if (cfg.resetValues) {
            this.addParam(postParams, core.RESET_VALUES_PARAM, "true", parameterPrefix);
        }

        //ignoreAutoUpdate
        if (cfg.ignoreAutoUpdate) {
            this.addParam(postParams, core.IGNORE_AUTO_UPDATE_PARAM, "true", parameterPrefix);
        }

        //skip children
        if (cfg.skipChildren === false) {
            this.addParam(postParams, core.SKIP_CHILDREN_PARAM, "false", parameterPrefix);
        }

        //process
        var processArray = this.resolveComponentsForAjaxCall($source, cfg, 'process');
        if (cfg.fragmentProcess) {
            processArray.push(cfg.fragmentProcess);
        }
        // default == @none
        var processIds = '@none';
        // use defined process + resolved keywords (@widget, PFS)?
        if (processArray.length > 0) {
            processIds = processArray.join(' ');
        }
        // fallback to @all if no process was defined by the user
        else {
            var definedProcess = this.resolveComponentsForAjaxCall($source, cfg, 'process');
            if (definedProcess === undefined || definedProcess.length === 0) {
                processIds = '@all';
            }
        }
        if (!processIds.includes('@none')) {
            this.addParam(postParams, core.PARTIAL_PROCESS_PARAM, processIds, parameterPrefix);
        }

        //update
        var updateArray = this.resolveComponentsForAjaxCall($source, cfg, 'update');
        if (cfg.fragmentUpdate) {
            updateArray.push(cfg.fragmentUpdate);
        }
        if (updateArray.length > 0) {
            this.addParam(postParams, core.PARTIAL_UPDATE_PARAM, updateArray.join(' '), parameterPrefix);
        }

        //behavior event
        if (cfg.event) {
            this.addParam(postParams, core.BEHAVIOR_EVENT_PARAM, cfg.event, parameterPrefix);

            var domEvent = cfg.event;

            if (cfg.event === 'valueChange')
                domEvent = 'change';
            else if (cfg.event === 'action')
                domEvent = 'click';

            this.addParam(postParams, core.PARTIAL_EVENT_PARAM, domEvent, parameterPrefix);
        }
        else {
            this.addParam(postParams, sourceId, sourceId, parameterPrefix);
        }

        //params
        if (cfg.params) {
            this.addParams(postParams, cfg.params, parameterPrefix);
        }
        if (cfg.ext && cfg.ext.params) {
            this.addParams(postParams, cfg.ext.params, parameterPrefix);
        }

        // try to get partialSubmit from global config
        if (cfg.partialSubmit === undefined) {
            cfg.partialSubmit = core.settings.partialSubmit;
        }
        // check for overwrite
        if (cfg.ext && cfg.ext.partialSubmit) {
            cfg.partialSubmit = cfg.ext.partialSubmit;
        }

        /**
         * Only add params of process components and their children
         * if partial submit is enabled and there are components to process partially
         */
        if (cfg.partialSubmit && processIds.indexOf('@all') === -1) {
            var formProcessed = false;

            if (processIds.indexOf('@none') === -1) {
                var partialSubmitFilter = cfg.partialSubmitFilter || ':input';
                for (const processId of processArray) {
                    var jqProcess = $(core.escapeClientId(processId));
                    var componentPostParams = null;

                    if (jqProcess.is('form')) {
                        componentPostParams = jqProcess.serializeArray();
                        formProcessed = true;
                        if (multipart) {
                            scanForFiles = scanForFiles.add(jqProcess);
                        }
                    }
                    else if (jqProcess.is(':input')) {
                        componentPostParams = jqProcess.serializeArray();
                        if (multipart) {
                            scanForFiles = scanForFiles.add(jqProcess);
                        }
                    }
                    else {
                        var filtered = jqProcess.find(partialSubmitFilter);
                        componentPostParams = filtered.serializeArray();
                        if (multipart) {
                            scanForFiles = scanForFiles.add(filtered);
                        }
                    }

                    postParams = this.arrayCompare(componentPostParams, postParams);

                    if (cfg.ext && cfg.ext.partialSubmitParameterFilter) {
                        var filteredParams = cfg.ext.partialSubmitParameterFilter.call(this, componentPostParams);
                        $.merge(postParams, filteredParams);
                    }
                    else {
                        $.merge(postParams, componentPostParams);
                    }
                }
            }

            //add form state if necessary
            if (!formProcessed) {
                // Faces
                this.addParamFromInput(postParams, core.VIEW_STATE, form, parameterPrefix);
                this.addParamFromInput(postParams, core.CLIENT_WINDOW, form, parameterPrefix);
                // PrimeFaces
                this.addParamFromInput(postParams, csp.NONCE_INPUT, form, parameterPrefix);
                // DeltaSpike
                this.addParamFromInput(postParams, 'dsPostWindowId', form, parameterPrefix);
                this.addParamFromInput(postParams, 'dspwid', form, parameterPrefix);
                // Spring Security
                this.addParamFromInput(postParams, '_csrf', form, parameterPrefix);
            }

        }
        else {
            $.merge(postParams, form.serializeArray());
            if (multipart) {
                scanForFiles = scanForFiles.add(form);
            }
        }

        // remove postParam if already available in earlyPostParams
        // we can skip files here, they likely wont change during that time
        if (core.settings.earlyPostParamEvaluation && cfg.earlyPostParams) {
            postParams = this.arrayCompare(cfg.earlyPostParams, postParams);

            $.merge(postParams, cfg.earlyPostParams);
        }

        // scan for files and append to formData
        if (multipart && formData !== undefined) {
            let fileInputs = $();
            for (const value of scanForFiles) {
                const $value = $(value);
                if ($value.is(':input[type="file"]')) {
                    fileInputs = fileInputs.add($value);
                }
                else {
                    fileInputs = fileInputs.add($value.find('input[type="file"]'));
                }
            }

            for (const value of fileInputs) {
                if (value instanceof HTMLInputElement) {
                    for (const file of value.files ?? []) {
                        formData.append(value.id, file);
                    }
                }
            }
        }

        let xhrOptions: PrimeType.ajax.PrimeFacesSettings = {
            url: postURL,
            type: "POST",
            cache: false,
            dataType: "xml",
            portletForms: parameterPrefix ? ajax.Utils.getPorletForms(form, parameterPrefix) : null,
            source: cfg.source,
            global: false,
            beforeSend: function(xhr, settings) {
                xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                xhr.pfSettings = settings;
                xhr.pfArgs = {}; // default should be an empty object

                if (global) {
                    $(document).trigger('pfAjaxSend', [xhr, this]);
                }
            }
        };

        // #6360 respect form enctype multipart/form-data
        if (multipart && formData !== undefined) {
            $.each(postParams, (_, value) => {
                formData.append(value.name, value.value);
            });

            xhrOptions.data = formData;
            xhrOptions.enctype = 'multipart/form-data';
            xhrOptions.processData = false;
            xhrOptions.contentType = false;
        }
        else {
            const postData = $.param(postParams);

            core.debug('Post Data:' + postData);

            xhrOptions.data = postData;
        }

        const nonce = form.children("input[name='" + CSS.escape(csp.NONCE_INPUT) + "']");
        if (nonce.length > 0) {
            const nonceValue = nonce.val();
            if (typeof nonceValue === "string") {
                xhrOptions.nonce = nonceValue;
            }
        }

        if (cfg.timeout) {
            xhrOptions['timeout'] = cfg.timeout;
        }

        const jqXhr = $.ajax(xhrOptions)
            .fail(function(this: PrimeType.ajax.PrimeFacesSettings, xhr, status, errorThrown) {
                if (cfg.promise) {
                    cfg.promise.reject({ jqXHR: xhr, textStatus: status, errorThrown: errorThrown });
                }

                const location = xhr.getResponseHeader("Location");
                if (xhr.status === 401 && location) {
                    core.debug('Unauthorized status received. Redirecting to ' + location);
                    // There are subtle differences between
                    // window.location = "..." and window.location.href = "..."
                    // https://github.com/microsoft/TypeScript/issues/48949
                    (window as Window).location = location;
                    return;
                }
                if (cfg.onerror) {
                    cfg.onerror.call(this, xhr, status, errorThrown);
                }
                if (cfg.ext && cfg.ext.onerror) {
                    cfg.ext.onerror.call(this, xhr, status, errorThrown);
                }

                $(document).trigger('pfAjaxError', [xhr, this, errorThrown]);

                core.error('Request return with error:' + status + '.');
            })
            .done(function(this: PrimeType.ajax.PrimeFacesSettings, data, status, xhr: PrimeType.ajax.pfXHR) {
                core.debug('Response received successfully.');
                try {
                    var parsed;

                    // Resolve promise for custom JavaScript handler
                    // Promise handlers are called asynchronously so they are run after the response was handled
                    if (cfg.promise) {
                        cfg.promise.resolve({ document: data, textStatus: status, jqXHR: xhr });
                    }

                    //call user callback
                    if (cfg.onsuccess) {
                        parsed = cfg.onsuccess.call(this, data, status, xhr);
                    }

                    //extension callback that might parse response
                    if (cfg.ext && cfg.ext.onsuccess && !parsed) {
                        parsed = cfg.ext.onsuccess.call(this, data, status, xhr);
                    }

                    if (global) {
                        $(document).trigger('pfAjaxSuccess', [xhr, this]);
                    }

                    //do not execute default handler as response already has been parsed
                    if (parsed) {
                        return;
                    }
                    else {
                        ajax.Response.handle(data, status, xhr);
                    }
                }
                catch (err) {
                    core.error(err);
                }

                if (global) {
                    $(document).trigger('pfAjaxUpdated', [xhr, this, xhr.pfArgs]);
                }

                core.debug('DOM is updated.');
            })
            .always(function(this: PrimeType.ajax.PrimeFacesSettings, data, status, xhr: string | PrimeType.ajax.pfXHR) {
                const { pfArgs = {}, pfScripts = [], pfSettings = {} } = typeof xhr === "string" ? {} : xhr;

                // first call the extension callback (e.g. datatable paging)
                if (cfg.ext && cfg.ext.oncomplete) {
                    cfg.ext.oncomplete.call(this, xhr, status, pfArgs, data);
                }

                // after that, call the end user's callback, which should be called when everything is ready
                if (cfg.oncomplete) {
                    cfg.oncomplete.call(this, xhr, status, pfArgs, data);
                }

                // finally, call the programmatic scripts
                if (pfScripts.length) {
                    try {
                        (window.PrimeFaces as any).scriptContext = {
                            xhr: xhr,
                            status: status,
                            args: pfArgs,
                            data: data
                        };

                        for (const script of pfScripts) {
                            csp.eval(`(function(xhr, status, args, data) {
                                ${script}
                            })(
                                window.PrimeFaces.scriptContext.xhr,
                                window.PrimeFaces.scriptContext.status,
                                window.PrimeFaces.scriptContext.args,
                                window.PrimeFaces.scriptContext.data
                            );`, pfSettings.nonce, window);
                        }
                    } finally {
                        delete (window.PrimeFaces as any).scriptContext;
                    }
                }

                if (global) {
                    $(document).trigger('pfAjaxComplete', [xhr, this, pfArgs]);
                }

                core.debug('Response completed.');

                if (typeof xhr !== "string") {
                    ajax.Queue.removeXHR(xhr);
                }

                if (!cfg.async) {
                    ajax.Queue.poll();
                }
            });

        ajax.Queue.addXHR(jqXhr);

        return undefined;
    }

    /**
     * Collects all `process` or `update` search expressions from the given AJAX call configuration and returns
     * them as one search expression.
     * @param cfg An AJAX call configuration.
     * @param type Whether to resolve the `process` or `update` expressions.
     * @return All process or update search expression from the given configuration.
     */
    resolveExpressionsForAjaxCall(cfg: PrimeType.ajax.Configuration, type: "process" | "update"): string {
        var expressions = '';

        if (cfg[type]) {
            expressions += cfg[type];
        }

        if (cfg.ext && cfg.ext[type]) {
            expressions += " " + cfg.ext[type];
        }

        return expressions;
    }

    /**
     * Given an AJAX call configuration, resolves the components for the `process` or `update` search
     * expressions given by the configurations. Resolves the search expressions to the actual components and
     * returns a list of their IDs.
     *
     * @param source the source element.
     * @param cfg An AJAX call configuration.
     * @param type Whether to resolve the `process` or `update` expressions.
     * @return A list of IDs with the components to which the process or update expressions refer.
     */
    resolveComponentsForAjaxCall(source: JQuery, cfg: PrimeType.ajax.Configuration, type: "process" | "update"): string[] {
        const resolvedExpressions = this.resolveExpressionsForAjaxCall(cfg, type);
        return expressions.SearchExpressionFacade.resolveComponents(source, resolvedExpressions);
    }

    /**
     * Appends a request parameter to the given list of parameters.
     * Optionally add a prefix to the name, this is used for portlet namespacing.
     * @template Value Type of the parameter value.
     * @param params List of parameters to which a new
     * parameter is added.
     * @param name Name of the new parameter to add.
     * @param value Value of the parameter to add.
     * @param parameterPrefix Optional prefix that is added in front of the name.
     */
    addParam<Value>(params:PrimeType.ajax.RequestParameter<string, Value>[], name: string, value: Value, parameterPrefix?: string | null): void {
        // add namespace if not available
        if (parameterPrefix && !name.startsWith(parameterPrefix)) {
            params.push({ name: parameterPrefix + name, value: value });
        }
        else {
            params.push({ name: name, value: value });
        }

    }

    /**
     * Appends a request parameter to the given list of parameters.
     * Optionally add a prefix to the name, this is used for portlet namespacing.
     * @param formData the form data to add to the form.
     * @param name Name of the new parameter to add.
     * @param value Value of the parameter to add.
     * @param parameterPrefix Optional prefix that is added in front of the name.
     */
    addFormData(formData: FormData, name: string, value: string | Blob, parameterPrefix?: string | null): void {
        // add namespace if not available
        if (parameterPrefix && !name.startsWith(parameterPrefix)) {
            formData.append(parameterPrefix + name, value);
        }
        else {
            formData.append(name, value);
        }
    }

    /**
     * Adds a list of callback parameters to the given list. Optionally prepends a prefix to the name of each
     * added parameter.
     * @template Value Type of the parameter values.
     * @param params List of callback parameters to which
     * parameters are added.
     * @param paramsToAdd List of callback parameters to
     * add.
     * @param parameterPrefix Optional prefix that is added in front of the name of the added
     * callback parameters.
     */
    addParams<Value>(
        params: PrimeType.ajax.RequestParameter<string, Value>[],
        paramsToAdd: PrimeType.ajax.RequestParameter<string, Value>[],
        parameterPrefix?: string | null,
    ): void {
        for (const param of paramsToAdd) {
            // add namespace if not available
            if (parameterPrefix && !param.name.startsWith(parameterPrefix)) {
                param.name = parameterPrefix + param.name;
            }

            params.push(param);
        }
    }

    /**
     * Adds a new request parameter to the given list. The value of the parameter is taken from the input
     * element of the given form. The input element must have the same name as the name of the parameter to add.
     * Optionally add a prefix to the name, which used for portlet namespacing.
     * @param params List of request parameters to the new parameter is added.
     * @param name Name of the new parameter to add
     * @param form An HTML FORM element that contains an INPUT element with the given name.
     * @param parameterPrefix Optional prefix that is added in front of the name.
     */
    addParamFromInput(params: PrimeType.ajax.RequestParameter[], name: string, form: JQuery, parameterPrefix?: string | null): void {
        var input = null,
            escapedName = CSS.escape(name);
        if (parameterPrefix) {
            input = form.children("input[name*='" + escapedName + "']");
        }
        else {
            input = form.children("input[name='" + escapedName + "']");
        }

        if (input && input.length > 0) {
            var value = input.val();
            this.addParam(params, name, value, parameterPrefix);
        }
    }


    /**
     * Adds a new request parameter to the given FormData. The value of the parameter is taken from the input
     * element of the given form. The input element must have the same name as the name of the parameter to add.
     * Optionally add a prefix to the name, which used for portlet namespacing.
     * @param formData The FormData.
     * @param name Name of the new parameter to add
     * @param form An HTML FORM element that contains an INPUT element with the given name.
     * @param parameterPrefix Optional prefix that is added in front of the name.
     */
    addFormDataFromInput(formData: FormData, name: string, form: JQuery, parameterPrefix?: string | null): void {
        let input: JQuery;
        const escapedName = CSS.escape(name);
        if (parameterPrefix) {
            input = form.children("input[name*='" + escapedName + "']");
        }
        else {
            input = form.children("input[name='" + escapedName + "']");
        }

        if (input && input.length > 0) {
            const value = input.val();
            this.addFormData(formData, name, typeof value === "string" ? value : "", parameterPrefix);
        }
    }

    /**
     * Finds the namespace (prefix) for the parameters of the given form.
     * This is required for Porlets as a Portlet contains multiple Faces views and we must only process and update the forms/inputs of the current view / application.
     * Later the namespace is used for all post params.
     * @param form An HTML FORM element.
     * @return The namespace for the parameters of the given form, or `null` when the form does
     * not specify a namespace.
     */
    extractParameterNamespace(form: JQuery): string | null {
        var input = form.children("input[name*='" + core.VIEW_STATE + "']")[0];
        if (input instanceof HTMLInputElement) {
            const name = input.name;
            if (name.length > core.VIEW_STATE.length) {
                return name.substring(0, name.indexOf(core.VIEW_STATE));
            }
        }

        return null;
    }

    /**
     * Creates a new array with all parameters from the second array that are not in the first array. That is,
     * removes all parameters from the second array whose name is equal to one of the parameters in the first
     * array. The given input array are not modified.
     * @typeParam Item Type of the parameter values.
     * @param arr1 A list of parameters for comparison.
     * @param arr2 A list of additional parameters.
     * @return A list of parameters that are in the second array, but not in the first.
     */
    arrayCompare<Item>(
        arr1: PrimeType.ajax.RequestParameter<string, Item>[],
        arr2: PrimeType.ajax.RequestParameter<string, Item>[]
    ): PrimeType.ajax.RequestParameter<string, Item>[] {
        // loop arr1 params
        $.each(arr1, function(index1, param1) {
            // loop arr2 params and remove it, if it's the same param as the arr1 param
            arr2 = $.grep(arr2, function(param2, index2) {
                return param2.name !== param1.name;
            });
        });

        return arr2;
    }

    /**
     * Creates a FormData which can be used for a Faces AJAX request on the current view.
     * It already contains all required parameters like ViewState or ClientWindow.
     *
     * @param form The closest form of the request source.
     * @param parameterPrefix The Portlet parameter namespace.
     * @param source The id of the request source.
     * @param process A comma separated list of components which should be processed.
     * @param update A comma separated list of components which should be updated.
     * @param ignoreAutoUpdate If true, components which use `p:autoUpdate` will not be updated for this request.
     * @return The newly created form data.
     */
    createFacesAjaxFormData(
        form: JQuery,
        parameterPrefix: string | undefined | null,
        source: string,
        process?: string | null,
        update?: string | null,
        ignoreAutoUpdate?: boolean
    ): FormData {
        var formData = new FormData();

        this.addFormData(formData, core.PARTIAL_REQUEST_PARAM, "true", parameterPrefix);
        this.addFormData(formData, core.PARTIAL_SOURCE_PARAM, source, parameterPrefix);
        if (process) {
            this.addFormData(formData, core.PARTIAL_PROCESS_PARAM, process, parameterPrefix);
        }
        if (update) {
            this.addFormData(formData, core.PARTIAL_UPDATE_PARAM, update, parameterPrefix);
        }
        if (ignoreAutoUpdate) {
            this.addFormData(formData, core.IGNORE_AUTO_UPDATE_PARAM, "true", parameterPrefix);
        }

        // Faces
        this.addFormDataFromInput(formData, core.VIEW_STATE, form, parameterPrefix);
        this.addFormDataFromInput(formData, core.CLIENT_WINDOW, form, parameterPrefix);
        // PrimeFaces
        this.addFormDataFromInput(formData, csp.NONCE_INPUT, form, parameterPrefix);
        // DeltaSpike
        this.addFormDataFromInput(formData, 'dsPostWindowId', form, parameterPrefix);
        this.addFormDataFromInput(formData, 'dspwid', form, parameterPrefix);
        // Spring Security
        this.addFormDataFromInput(formData, '_csrf', form, parameterPrefix);

        return formData;
    }
}

/**
 * The class containing low-level functionality related to handling AJAX responses. Note that
 * the different types of AJAX actions are handled by the {@link AjaxResponseProcessor PrimeFaces.ajax.ResponseProcessor}.
 */
export class AjaxResponse {
    /**
     * Handles the response of an AJAX request. The response consists of one or more actions such as executing a
     * script or updating a DOM element. See also {@link faces.ajax.response}.
     *
     * Also updates the specified components if any and synchronizes the client side Faces state. DOM updates are
     * implemented using jQuery which uses a very algorithm.
     *
     * @typeParam Widget Type of the widget which triggered the AJAX request.
     * @param xml The XML that was returned by the AJAX request.
     * @param status Text status of the request.
     * @param xhr The XHR request to which a response was received.
     * @param updateHandler Optional handler for `update` actions.
     */
    handle<Widget extends BaseWidget<any>>(
        xml: XMLDocument,
        status: JQuery.Ajax.SuccessTextStatus,
        xhr: PrimeType.ajax.pfXHR,
        updateHandler?: PrimeType.ajax.UpdateHandler<Widget> | null
    ): void {
        if (xml === undefined || xml === null) {
            return;
        }

        const partialResponseNode = xml.getElementsByTagName("partial-response")[0];

        for (const currentNode of partialResponseNode?.childNodes ?? []) {

            switch (currentNode.nodeName) {
                case "redirect":
                    // will be done afterwards, we execute all changes (especially 'eval') first. See #13289.
                    break;

                case "changes":
                    var activeElement = document.activeElement ? $(document.activeElement) : $();
                    var activeElementId = activeElement.attr('id');
                    var activeElementSelection;
                    if (activeElement.length > 0 && activeElement.is('input') && typeof $.fn.getSelection === "function") {
                        activeElementSelection = activeElement.getSelection();
                    }

                    for (const currentChangeNode of currentNode.childNodes) {
                        switch (currentChangeNode.nodeName) {
                            case "update":
                                ajax.ResponseProcessor.doUpdate(currentChangeNode, xhr, updateHandler);
                                break;
                            case "delete":
                                ajax.ResponseProcessor.doDelete(currentChangeNode);
                                break;
                            case "insert":
                                ajax.ResponseProcessor.doInsert(currentChangeNode);
                                break;
                            case "attributes":
                                ajax.ResponseProcessor.doAttributes(currentChangeNode);
                                break;
                            case "eval":
                                ajax.ResponseProcessor.doEval(currentChangeNode, xhr);
                                break;
                            case "extension":
                                ajax.ResponseProcessor.doExtension(currentChangeNode, xhr);
                                break;
                        }
                    }

                    this.handleReFocus(activeElementId, activeElementSelection);
                    this.destroyDetachedWidgets();
                    break;

                case "eval":
                    ajax.ResponseProcessor.doEval(currentNode);
                    break;

                case "extension":
                    ajax.ResponseProcessor.doExtension(currentNode, xhr);
                    break;

                case "error":
                    ajax.ResponseProcessor.doError(currentNode, xhr);
                    break;
            }
        }

        // handle redirect as last step, see #13289
        const redirectNodes = Array.from(partialResponseNode?.childNodes ?? []).filter(node => node.nodeName === "redirect");
        for (const currentNode of redirectNodes) {
            const pfArgs = xhr.pfArgs;
            if (pfArgs) {
                pfArgs.redirect = true;
            }
            ajax.ResponseProcessor.doRedirect(currentNode);
            break;
        }
    }

    /**
     * Puts focus on the given element if necessary.
     * @param activeElementId ID of the active to refocus.
     * @param activeElementSelection The range to select, for INPUT and TEXTAREA elements.
     */
    handleReFocus(activeElementId: string | undefined, activeElementSelection?: PrimeType.ajax.ActiveElementSelection): void {
        // skip when customFocus is active
        if (core.customFocus === true) {
            core.customFocus = false;
            return;
        }

        // no active element remembered
        if (!activeElementId) {
            return;
        }

        var elementToFocus = $(core.escapeClientId(activeElementId));
        if (elementToFocus.length > 0) {

            var refocus = function() {
                // already focussed?
                if (activeElementId !== document.activeElement?.id) {
                    // focus
                    elementToFocus.trigger('focus');

                    // reapply cursor / selection
                    if (activeElementSelection) {
                        elementToFocus.setSelection(activeElementSelection.start, activeElementSelection.end);
                    }
                }
            };

            refocus();
        }
    }

    /**
     * Destroys all widgets that are not part of the DOM anymore, usually because they were removed by an AJAX
     * update. Calls the `destroy` method on the widget and removes the widget from the global widget registry.
     */
    destroyDetachedWidgets(): void {
        // destroy detached widgets
        for (const widgetVar of core.detachedWidgets) {
            // Do not use PF() which logs an error when the widget is missing
            // Since the widget is detached, it may already be gone.
            const widget = core.widgets[widgetVar];
            if (widget && widget.isDetached() === true) {
                try {
                    widget.destroy();
                    delete core.widgets[widgetVar];
                } catch (e) { core.warn("Error destroying widget: " + widgetVar) }
            }
        }

        core.detachedWidgets = [];
    }
}

/**
 * The class containing low-level functionality related to processing the different types
 * of actions from AJAX responses.
 */
export class AjaxResponseProcessor {
    /**
     * Handles a `redirect` AJAX action by performing a redirect to the target URL.
     * @param node The XML node of the `redirect` action.
     */
    doRedirect(node: Node): void {
        if (!(node instanceof Element)) {
            return;
        }
        try {
            window.location.assign(node.getAttribute('url') ?? "");
        } catch (error) {
            core.warn('Error redirecting to URL: ' + node.getAttribute('url'));
        }
    }

    /**
     * Handles an `update` AJAX action by calling the given update handler. When no update handler is given,
     * replaces the HTML content of the element with the new content.
     * @typeParam Widget Type of the widget which triggered the AJAX request.
     * @param node The XML node of the `update` action.
     * @param xhr The XHR request to which a response was received.
     * @param updateHandler Optional handler for the update.
     */
    doUpdate<Widget extends BaseWidget>(node: Node, xhr: PrimeType.ajax.pfXHR, updateHandler?: PrimeType.ajax.UpdateHandler<Widget> | null): void {
        if (!(node instanceof Element)) {
            return;
        }
        const id = node.getAttribute('id') ?? "";
        const content = ajax.Utils.getContent(node);

        if (updateHandler && updateHandler.widget && updateHandler.widget.id === id) {
            updateHandler.handle.call(updateHandler.widget, content);
        } else {
            ajax.Utils.updateElement(id, content, xhr);
        }
    }

    /**
     * Handles an `eval` AJAX action by evaluating the returned JavaScript.
     * @param node The XML node of the `eval` action.
     * @param xhr The XHR request to which a response was received.
     */
    doEval(node: Node, xhr?: PrimeType.ajax.pfXHR): void {
        const textContent = getNodeTextContent(node);

        let nonce: string | undefined;
        if (xhr && xhr.pfSettings && typeof xhr.pfSettings.nonce === "string") {
            nonce = xhr.pfSettings.nonce;
        }
        csp.eval(textContent, nonce);
    }

    /**
     * Handles an `extension` AJAX action by extending the `pfArgs` property on the jQuery XHR object.
     * @param node The XML node of the `extension` action.
     * @param xhr The XHR request to which a response was received.
     */
    doExtension(node: Node, xhr?: PrimeType.ajax.pfXHR): void {
        if (xhr && node instanceof Element) {
            if (node.getAttribute("ln") === "primefaces") {
                if (node.getAttribute("type") === "args") {
                    const textContent = getNodeTextContent(node);
                    // it's possible that pfArgs are already defined e.g. if Portlet parameter namespacing is enabled
                    // the "parameterPrefix" will be encoded on document start
                    // the other parameters will be encoded on document end
                    // --> see PrimePartialResponseWriter
                    if (xhr.pfArgs) {
                        const json = JSON.parse(textContent);
                        for (var name in json) {
                            xhr.pfArgs[name] = json[name];
                        }
                    }
                    else {
                        xhr.pfArgs = JSON.parse(textContent);
                    }
                }
                else if (node.getAttribute("type") === "script") {
                    xhr.pfScripts ||= [];
                    xhr.pfScripts.push(getNodeTextContent(node));
                }
            }
        }
    }

    /**
     * Handles an `error` AJAX action by doing nothing currently.
     * @param node The XML node of the `error` action.
     * @param xhr The XHR request to which a response was received.
     */
    doError(node: Node, xhr?: PrimeType.ajax.pfXHR): void {
        if (!(node instanceof Element)) {
            return;
        }
        var errorName = ajax.Utils.getContent(node.getElementsByTagName("error-name")[0]);
        var errorMessage = ajax.Utils.getContent(node.getElementsByTagName("error-message")[0]);

        ajax.Utils.handleError(errorName, errorMessage);
    }

    /**
     * Handles a `delete` AJAX action by remove the DOM element.
     * @param node The XML node of the `delete` action.
     */
    doDelete(node: Node): void {
        if (!(node instanceof Element)) {
            return;
        }
        var id = node.getAttribute('id') ?? "";
        $(core.escapeClientId(id)).remove();
    }

    /**
     * Handles an `insert` AJAX action by inserting a newly creating DOM element.
     * @param node The XML node of the `insert` action.
     * @return `false` if the AJAX action could not be performed, `true` or `undefined`
     * otherwise.
     */
    doInsert(node: Node): boolean | undefined {
        if (!node.childNodes) {
            return false;
        }

        for (const childNode of node.childNodes) {
            if (!(childNode instanceof Element)) {
                continue;
            }
            var id = childNode.getAttribute('id') ?? "";
            var jq = $(core.escapeClientId(id));
            var content = ajax.Utils.getContent(childNode);

            if (childNode.nodeName === "after") {
                $(content).insertAfter(jq);
            }
            else if (childNode.nodeName === "before") {
                $(content).insertBefore(jq);
            }
        }

        return undefined;
    }

    /**
     * Handles an `attributes` AJAX action by setting the attributes on the DOM element.
     * @param node The XML node of the `attributes` action.
     * @return `false` if the AJAX action could not be performed, `true` or `undefined`
     * otherwise.
     */
    doAttributes(node: Node): boolean | undefined {
        if (!node.childNodes || !(node instanceof Element)) {
            return false;
        }

        var id = node.getAttribute('id') ?? "";
        var jq = $(core.escapeClientId(id));

        for (const attrNode of node.childNodes) {
            if (!(attrNode instanceof Element)) {
                continue;
            }
            const attrName = attrNode.getAttribute("name");
            let attrValue = attrNode.getAttribute("value");

            if (!attrName) {
                return;
            }

            if (!attrValue || attrValue === null) {
                attrValue = "";
            }

            jq.attr(attrName, attrValue);
        }

        return undefined;
    }
}

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
     * Parameter shortcut mapping for the method {@link ab}.
     */
    CFG_SHORTCUTS: PrimeType.ajax.ShorthandToArticulateConfigurationMap = {
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
     * {@link PrimeType.ajax.FailedRequestData} .
     */
    AjaxRequest(cfg: PrimeType.ajax.Configuration, ext?: PrimeType.ajax.ConfigurationExtender): PromiseLike<PrimeType.ajax.ResponseData> {
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
    $(window).on(unloadEvent, ()  => ajax.Queue.abortAll());

    $(() => {
        if (window.faces && faces.ajax) {
            faces.ajax.addOnError((data) => {
                // serverError means a real server-side exception where a p:ajaxExceptionHandler or error-page mapping might exist
                if (data.status === "serverError") {
                    ajax.Utils.handleError(data.errorName, data.errorMessage);
                }
                // malformedXML, emptyResponse, httpError, clientError, timeout
                else {
                    // this are very likely very strange errors or client connection errors
                    // just invoke the same logic, this will likely result in a global p:ajaxExceptionHandler or global error-page
                    ajax.Utils.handleError(data.status, "AJAX failure");
                }
            });
        }
    });
}
