import { csp } from "./core.csp.js";
import { expressions } from "./core.expressions.js";
import { core } from "./core.js";
import { ajax, pageUnloading } from "./core.ajax.js";

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
     * rejection handler receives an object of type {@link PrimeFaces.ajax.FailedRequestData}.
     */
    handle(
        cfg: PrimeFaces.ajax.Configuration,
        ext?: PrimeFaces.ajax.ConfigurationExtender
    ): PromiseLike<PrimeFaces.ajax.ResponseData>  {
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
    collectEarlyPostParams(cfg: PrimeFaces.ajax.Configuration): JQuery.NameValuePair[] {

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
    send(cfg: PrimeFaces.ajax.Configuration): boolean | undefined {
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
                form = $<HTMLFormElement>('form').filter(function(index, element) {
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
        let postParams: PrimeFaces.ajax.RequestParameter<string, string | Blob>[] = [];

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
        // use defined process + resolved keywords (@ref, @widgetVar, PFS)?
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

        let xhrOptions: PrimeFaces.ajax.PrimeFacesSettings = {
            url: postURL,
            type: "POST",
            cache: false,
            dataType: "xml",
            portletForms: parameterPrefix ? ajax.Utils.getPorletForms(form, parameterPrefix) : null,
            source: cfg.source,
            skipErrorHandling: cfg.skipErrorHandling,
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
            .fail(function(this: PrimeFaces.ajax.PrimeFacesSettings, xhr, status, errorThrown) {
                if (cfg.promise) {
                    cfg.promise.reject({ jqXHR: xhr, textStatus: status, errorThrown: errorThrown });
                }

                // Navigation / tab close cancels requests; do not surface as real errors.
                // Rely on pageUnloading, not jqXHR.status === 0 alone (0 is also used for CORS/network failures).
                if (pageUnloading || status === "abort") {
                    core.debug('Page unloaded or aborted.');
                    return;
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
            .done(function(this: PrimeFaces.ajax.PrimeFacesSettings, data, status, xhr: PrimeFaces.ajax.pfXHR) {
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
            .always(function(this: PrimeFaces.ajax.PrimeFacesSettings, data, status, xhr: string | PrimeFaces.ajax.pfXHR) {
                const { pfArgs = {} } = typeof xhr === "string" ? {} : xhr;

                // first call the extension callback (e.g. datatable paging)
                if (cfg.ext && cfg.ext.oncomplete) {
                    cfg.ext.oncomplete.call(this, xhr, status, pfArgs, data);
                }

                // after that, call the end user's callback, which should be called when everything is ready
                if (cfg.oncomplete) {
                    cfg.oncomplete.call(this, xhr, status, pfArgs, data);
                }

                // finally, call the programmatic scripts
                ajax.Response.executeScripts(xhr, status, data);

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
    resolveExpressionsForAjaxCall(cfg: PrimeFaces.ajax.Configuration, type: "process" | "update"): string {
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
    resolveComponentsForAjaxCall(source: JQuery, cfg: PrimeFaces.ajax.Configuration, type: "process" | "update"): string[] {
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
    addParam<Value>(params:PrimeFaces.ajax.RequestParameter<string, Value>[], name: string, value: Value, parameterPrefix?: string | null): void {
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
        params: PrimeFaces.ajax.RequestParameter<string, Value>[],
        paramsToAdd: PrimeFaces.ajax.RequestParameter<string, Value>[],
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
    addParamFromInput(params: PrimeFaces.ajax.RequestParameter[], name: string, form: JQuery, parameterPrefix?: string | null): void {
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
        arr1: PrimeFaces.ajax.RequestParameter<string, Item>[],
        arr2: PrimeFaces.ajax.RequestParameter<string, Item>[]
    ): PrimeFaces.ajax.RequestParameter<string, Item>[] {
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
