if (!PrimeFaces.ajax) {

    /**
     * A shortcut for `PrimeFaces.ajax.Request.handle(cfg, ext)`, with shorter option names. Sends an AJAX request to
     * the server and processes the response. You can use this method if you need more fine-grained control over which
     * components you want to update or process, or if you need to change some other AJAX options.
     * @function
     * @param {Partial<PrimeFaces.ajax.ShorthandConfiguration>} cfg Configuration for the AJAX request, with shorthand
     * options. The individual options are documented in `PrimeFaces.ajax.Configuration`.
     * @param {Partial<PrimeFaces.ajax.ConfigurationExtender>} [ext] Optional extender with additional options that
     * overwrite the options given in `cfg`.
     * @return {Promise<PrimeFaces.ajax.ResponseData>} A promise that resolves once the AJAX requests is done. Use this
     * to run custom JavaScript logic. When the AJAX request succeeds, the promise is fulfilled. Otherwise, when the
     * AJAX request fails, the promise is rejected. If the promise is rejected, the rejection handler receives an object
     * of type {@link PrimeFaces.ajax.FailedRequestData}.
     */
    PrimeFaces.ab = function(cfg, ext) {
        for (var option in cfg) {
            if (!cfg.hasOwnProperty(option)) {
                continue;
            }

            // just pass though if no mapping is available
            if (PrimeFaces.ajax.CFG_SHORTCUTS[option]) {
                cfg[PrimeFaces.ajax.CFG_SHORTCUTS[option]] = cfg[option];
                delete cfg[option];
            }
        }

        return PrimeFaces.ajax.Request.handle(cfg, ext);
    };

    /**
     * The object with functionality related to sending and receiving AJAX requests that are made by PrimeFaces. Each
     * request receives an XML response, which consists of one or multiple actions that are to be performed. This
     * includes creating new DOM elements, deleting or updating existing elements, or executing some JavaScript.
     *
     * @namespace
     */
    PrimeFaces.ajax = {

        /**
         * Name for the ID of the HEAD element, used in AJAX requests.
         * @type {string}
         * @readonly
         */
        VIEW_HEAD : "javax.faces.ViewHead",
        /**
         * Name for the ID of the BODY element, used in AJAX requests.
         * @type {string}
         * @readonly
         */
        VIEW_BODY : "javax.faces.ViewBody",
        /**
         * Name for the ID of a resource entry, used in AJAX requests.
         * @type {string}
         * @readonly
         */
        RESOURCE : "javax.faces.Resource",

        /**
         * Parameter shortcut mapping for the method `PrimeFaces.ab`.
         * @type {Record<string, string>}
         */
        CFG_SHORTCUTS : {
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
            'fi': 'fragmentId',
            'pa': 'params',
            'onst': 'onstart',
            'oner': 'onerror',
            'onsu': 'onsuccess',
            'onco': 'oncomplete'
        },

        /**
         * This object contains utility methods for AJAX requests, primarily used internally.
         * @interface {PrimeFaces.ajax.Utils} . The class for the object with the AJAX utility methods, used for
         * handling and working with AJAX requests and updates.
         * @type {PrimeFaces.ajax.Utils}
         * @readonly
         */
        Utils: {
            /**
             * Iterates over all immediate children of the given node and returns the concatenated content (`node value`)
             * of each such child node. For the document itself, the node value is `null`.
             * For text, comment, and CDATA nodes, the `node value` is the (text) content of the node.
             * For attribute nodes, the value of the attribute is used.
             * @param {HTMLElement} node An HTML node for which to retrieve the content.
             * @return {string} The content of all immediate child nodes, concatenated together.
             */
            getContent: function(node) {
                var content = '';

                for(var i = 0; i < node.childNodes.length; i++) {
                    content += node.childNodes[i].nodeValue;
                }

                return content;
            },

            /**
             * Resolves the URL which should be used for the POST.
             * In Portlet a different URL is used.
             *
             * @param {JQuery} form The closest form of the request source.
             * @return {string} The POST url.
             */
            getPostUrl: function(form) {
                var postURL = form.attr('action');
                var encodedURLInput = form.children("input[name*='javax.faces.encodedURL']");

                if (encodedURLInput.length > 0) {
                    postURL = encodedURLInput.val();
                }

                return postURL;
            },

            /**
             * Gets a selector to resolve all forms which needs to be updated with a new ViewState.
             * This is required in Portlets as the DOM contains forms of multiple JSF views / applications.
             *
             * @param {JQuery} form The closest form of the request source.
             * @param {string} parameterPrefix The Portlet parameter prefix.
             * @return {string} null or a selector.
             */
            getPorletForms: function(form, parameterPrefix) {
                var encodedURLInput = form.children("input[name*='javax.faces.encodedURL']");

                if (encodedURLInput.length > 0) {
                    return 'form[id*="' + parameterPrefix + '"]';
                }

                return null;
            },

            /**
             * Updates the main hidden input element for each form.
             * @param {string} name Name of the hidden form input element, usually the same as the form.
             * @param {string} value Value to set on the hidden input element.
             * @param {PrimeFaces.ajax.pfXHR} [xhr] Optional XHR request with `pfSettings` or `pfArgs` with further
             * data, such as which forms should be updated.
             */
            updateFormStateInput: function(name, value, xhr) {
                var trimmedValue = PrimeFaces.trim(value);

                var forms = null;
                if (xhr && xhr.pfSettings && xhr.pfSettings.portletForms) {
                    forms = $(xhr.pfSettings.portletForms);
                }
                else {
                    forms = $('form');
                }

                var parameterPrefix = '';
                if (xhr && xhr.pfArgs && xhr.pfArgs.parameterPrefix) {
                    parameterPrefix = xhr.pfArgs.parameterPrefix;
                }

                for (var i = 0; i < forms.length; i++) {
                    var form = forms.eq(i);

                    if (form.attr('method') === 'post') {
                        var input = form.children("input[name='" + $.escapeSelector(parameterPrefix + name) + "']");

                        if (input.length > 0) {
                            input.val(trimmedValue);
                        } else {
                            form.append('<input type="hidden" name="' + parameterPrefix + name + '" value="' + trimmedValue + '" autocomplete="off"></input>');
                        }
                    }
                }
            },

            /**
             * Updates the HTML `head` element of the current document with the content received from an AJAX request.
             * @param {string} content The content of the changeset that was returned by an AJAX request.
             */
            updateHead: function(content) {
                var cache = $.ajaxSetup()['cache'];
                $.ajaxSetup()['cache'] = true;

                var headStartTag = new RegExp("<head[^>]*>", "gi").exec(content)[0];
                var headStartIndex = content.indexOf(headStartTag) + headStartTag.length;
                $('head').html(content.substring(headStartIndex, content.lastIndexOf("</head>")));

                $.ajaxSetup()['cache'] = cache;
            },

            /**
             * Updates the HTML `body` element of the current document with the content received from an AJAX request.
             * @param {string} content The content of the changeset that was returned by an AJAX request.
             */
            updateBody: function(content) {
                var bodyStartTag = new RegExp("<body[^>]*>", "gi").exec(content)[0];
                var bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
                $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
            },

            /**
             * Updates an element with the given ID by applying a change set that was returned by an AJAX request. This
             * involves replacing the HTML content of the element with the new content.
             * @param {string} id ID of the element that is to be updated.
             * @param {string} content The new content of the changeset as returned by an AJAX request.
             * @param {PrimeFaces.ajax.pfXHR} [xhr] Optional XHR request with `pfSettings` or `pfArgs` with further
             * data, such as which forms should be updated.
             */
            updateElement: function(id, content, xhr) {

                if (id.indexOf(PrimeFaces.VIEW_STATE) !== -1) {
                    PrimeFaces.ajax.Utils.updateFormStateInput(PrimeFaces.VIEW_STATE, content, xhr);
                }
                else if (id.indexOf(PrimeFaces.CLIENT_WINDOW) !== -1) {
                    PrimeFaces.ajax.Utils.updateFormStateInput(PrimeFaces.CLIENT_WINDOW, content, xhr);
                }
                // used by @all
                else if (id === PrimeFaces.VIEW_ROOT) {

                    // backup our utils, we reset it soon
                    var ajaxUtils = PrimeFaces.ajax.Utils;

                    // reset PrimeFaces JS state because the view is completely replaced with a new one
                    window.PrimeFaces = null;

                    ajaxUtils.updateHead(content);
                    ajaxUtils.updateBody(content);
                }
                else if (id === PrimeFaces.ajax.VIEW_HEAD) {
                    PrimeFaces.ajax.Utils.updateHead(content);
                }
                else if (id === PrimeFaces.ajax.VIEW_BODY) {
                    PrimeFaces.ajax.Utils.updateBody(content);
                }
                else if (id === PrimeFaces.ajax.RESOURCE) {
                    $('head').append(content);
                }
                else if (id === $('head')[0].id) {
                    PrimeFaces.ajax.Utils.updateHead(content);
                }
                else {
                    $(PrimeFaces.escapeClientId(id)).replaceWith(content);
                }
            }
        },

        /**
         * This object contains functionality related to queuing AJAX requests to ensure that they are (a) sent in the
         * proper order and (b) that each response is processed in the same order as the requests were sent.
         * @interface {PrimeFaces.ajax.Queue} . The interface for the object containing functionality related to queuing
         * AJAX requests. The queue ensures that requests are (a) sent in the order as they were issued, and (b) that
         * each response is processed in the same order as the requests were sent.
         * @type {PrimeFaces.ajax.Queue}
         * @readonly
         */
        Queue: {

            /**
             * A map between the source ID and  the timeout IDs (as returned by `setTimeout`). Used for AJAX requests
             * with a specified delay (such as remote commands that have a delay set).
             * @type {Record<string, number>}
             */
            delays: {},

            /**
             * A list of requests that are waiting to be sent.
             * @type {Partial<PrimeFaces.ajax.Configuration>[]}
             */
            requests: new Array(),

            /**
             * A list of sent AJAX requests, i.e. HTTP requests that were already started. This is used, for example, to
             * abort requests that were sent already when that becomes necessary.
             *
             * @type {PrimeFaces.ajax.pfXHR}
             */
            xhrs: new Array(),

            /**
             * Offers an AJAX request to this queue. The request is sent once all other requests in this queue have
             * been sent. If a delay is set on the request configuration, the request is not sent before the specified
             * delay has elapsed.
             * @param {Partial<PrimeFaces.ajax.Configuration>} request The request to send.
             */
            offer: function(request) {
                if(request.delay) {
                    var sourceId = null,
                    $this = this,
                    sourceId = (typeof(request.source) === 'string') ? request.source: $(request.source).attr('id'),
                    createTimeout = function() {
                            return setTimeout(function() {
                                $this.requests.push(request);

                                if($this.requests.length === 1) {
                                    PrimeFaces.ajax.Request.send(request);
                                }
                            }, request.delay);
                    };

                    if(this.delays[sourceId]) {
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

                    if(this.requests.length === 1) {
                        PrimeFaces.ajax.Request.send(request);
                    }
                }
            },

            /**
             * Removes the topmost request (the requests that was just sent) from this queue; and starts the second
             * topmost request.
             * @return {Partial<PrimeFaces.ajax.Configuration> | null} The topmost request in this queue, or `null` if this queue
             * is empty.
             */
            poll: function() {
                if(this.isEmpty()) {
                    return null;
                }

                var processed = this.requests.shift(),
                next = this.peek();

                if(next) {
                    PrimeFaces.ajax.Request.send(next);
                }

                return processed;
            },

            /**
             * Returns the request that is scheduled to be sent next, but does not modify the queue in any way.
             * @return {Partial<PrimeFaces.ajax.Configuration> | null} The topmost request in this queue that is to be sent next,
             * or `null` when this queue is empty.
             */
            peek: function() {
                if(this.isEmpty()) {
                    return null;
                }

                return this.requests[0];
            },

            /**
             * Checks whether this queue contains any scheduled AJAX requests.
             * @return {boolean} `true` if this queue contains no scheduled requests, `false` otherwise.
             */
            isEmpty: function() {
                return this.requests.length === 0;
            },

            /**
             * Adds a newly sent XHR request to the list of sent requests (`PrimeFaces.ajax.xhrs`).
             * @param {PrimeFaces.ajax.pfXHR} xhr XHR request to add.
             */
            addXHR: function(xhr) {
                this.xhrs.push(xhr);
            },

            /**
             * Removes an XHR request from the list of sent requests (`PrimeFaces.ajax.xhrs`). Usually called once the
             * AJAX request is done, having resulted in either a success or an error.
             * @param {PrimeFaces.ajax.pfXHR} xhr XHR request to remove.
             */
            removeXHR: function(xhr) {
                var index = $.inArray(xhr, this.xhrs);
                if(index > -1) {
                    this.xhrs.splice(index, 1);
                }
            },

            /**
             * Aborts all requests that were already sent, but have not yet received an answer from the server. Also
             * removes all requests that are waiting in the queue and have not been sent yet.
             */
            abortAll: function() {
                // clear out any pending requests
                this.requests = new Array();

                // abort any in-flight that are not DONE(4)
                for(var i = 0; i < this.xhrs.length; i++) {
                    var xhr = this.xhrs[i];
                    if (xhr.readyState !== 4) {
                        xhr.abort();
                    }
                }

                this.xhrs = new Array();
            }
        },

        /**
         * The interface for the object containing low-level functionality related to sending AJAX requests.
         * @interface {PrimeFaces.ajax.Request}. The interface for the object containing functionality related to
         * sending AJAX requests.
         * @type {PrimeFaces.ajax.Request}
         * @readonly
         */
        Request: {

            /**
             * Handles the given AJAX request, either by sending it immediately (if `async` is set to `true`), or by
             * adding it to the AJAX queue otherwise. The AJAX queue ensures that requests are sent and handled in the
             * order they were started. See also {@link jsf.ajax.request}.
             * @param {Partial<PrimeFaces.ajax.Configuration>} cfg Configuration for the AJAX request to send, such as
             * the HTTP method, the URL, and the content of the request.
             * @param {Partial<PrimeFaces.ajax.ConfigurationExtender>} [ext] Optional extender with additional options
             * that overwrite the options given in `cfg`.
             * @return {Promise<PrimeFaces.ajax.ResponseData>} A promise that resolves once the AJAX requests is done.
             * Use this to run custom JavaScript logic. When the AJAX request succeeds, the promise is fulfilled.
             * Otherwise, when the AJAX request fails, the promise is rejected. If the promise is rejected, the
             * rejection handler receives an object of type {@link PrimeFaces.ajax.FailedRequestData}.
             */
            handle: function(cfg, ext) {
                cfg.ext = ext;
                cfg.promise = cfg.promise || $.Deferred();

                if (PrimeFaces.settings.earlyPostParamEvaluation) {
                    cfg.earlyPostParams = PrimeFaces.ajax.Request.collectEarlyPostParams(cfg);
                }

                if(cfg.async) {
                    PrimeFaces.ajax.Request.send(cfg);
                }
                else {
                    PrimeFaces.ajax.Queue.offer(cfg);
                }

                return cfg.promise.promise();
            },

            /**
             * Performs the early collection of post parameters (form element values) if the request is configured that
             * way. See: https://github.com/primefaces/primefaces/issues/109
             *
             * @param {Partial<PrimeFaces.ajax.Configuration>} cfg Configuration for the AJAX request to send, such as
             * the HTTP method, the URL, and the content of the request.
             * @return {PrimeFaces.ajax.RequestParameter[]} The collected form element values to be sent with the request.
             */
            collectEarlyPostParams: function(cfg) {

                var earlyPostParams;

                var sourceElement;
                if (typeof(cfg.source) === 'string') {
                    sourceElement = $(PrimeFaces.escapeClientId(cfg.source));
                }
                else {
                    sourceElement = $(cfg.source);
                }
                if (sourceElement.is(':input') && sourceElement.is(':not(:button)')) {
                    earlyPostParams = [];

                    if (sourceElement.is(':checkbox')) {
                        var checkboxPostParams = $("input[name='" + $.escapeSelector(sourceElement.attr('name')) + "']")
                                .filter(':checked').serializeArray();
                        $.merge(earlyPostParams, checkboxPostParams);
                    }
                    else {
                        earlyPostParams.push({
                            name: sourceElement.attr('name'),
                            value: sourceElement.val()
                        });
                    }
                }
                else {
                    earlyPostParams = sourceElement.serializeArray();
                }

                return earlyPostParams;
            },

            /**
             * Starts the given AJAX request immediately by sending the data to the server. Contrast with
             * {@link handle}, which may queue AJAX requests, depending on how they are configured.
             * @param {Partial<PrimeFaces.ajax.Configuration>} cfg Configuration for the AJAX request to send, such as
             * the HTTP method, the URL, and the content of the request.
             * @return {boolean|undefined} `false` if the AJAX request is to be canceled, `true` or `undefined`
             * otherwise.
             */
            send: function(cfg) {
                PrimeFaces.debug('Initiating ajax request.');

                PrimeFaces.customFocus = false;

                var global = (cfg.global === true || cfg.global === undefined) ? true : false,
                form = null,
                sourceId = null,
                retVal = null;

                if(cfg.onstart) {
                    retVal = cfg.onstart.call(this, cfg);
                }
                if(cfg.ext && cfg.ext.onstart) {
                    retVal = cfg.ext.onstart.call(this, cfg);
                }

                if(retVal === false) {
                    PrimeFaces.debug('AJAX request cancelled by onstart callback.');

                    //remove from queue
                    if(!cfg.async) {
                        PrimeFaces.ajax.Queue.poll();
                    }

                    if (cfg.promise) {
                        cfg.promise.reject({ textStatus: 'error', errorThrown: 'AJAX request cancelled by onstart callback.' });
                    }

                    return false;  //cancel request
                }

                if(global) {
                    $(document).trigger('pfAjaxStart');
                }

                //source can be a client id or an element defined by this keyword
                if(typeof(cfg.source) === 'string') {
                    sourceId = cfg.source;
                } else {
                    sourceId = $(cfg.source).attr('id');
                }

                if(cfg.formId) {
                    //Explicit form is defined
                    form = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(cfg.formId);
                }
                else {
                    var $source = $(PrimeFaces.escapeClientId(sourceId));
                    //look for a parent of source
                    form = $source.closest('form');

                    //source has no parent form so use first form in document
                    if (form.length === 0) {
                        form = $('form').eq(0);
                    }
                }

                PrimeFaces.debug('Form to post ' + form.attr('id') + '.');

                var formData;
                var scanForFiles;
                var multipart = form.attr('enctype') === 'multipart/form-data';
                if (multipart) {
                    formData = new FormData();
                    scanForFiles = $();
                }

                var postURL = PrimeFaces.ajax.Utils.getPostUrl(form);
                var postParams = [];

                // See #6857 - parameter namespace for Portlets
                var parameterPrefix = PrimeFaces.ajax.Request.extractParameterNamespace(form);

                PrimeFaces.debug('URL to post ' + postURL + '.');

                //partial ajax
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_REQUEST_PARAM, true, parameterPrefix);

                //source
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_SOURCE_PARAM, sourceId, parameterPrefix);

                //resetValues
                if (cfg.resetValues) {
                    PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.RESET_VALUES_PARAM, true, parameterPrefix);
                }

                //ignoreAutoUpdate
                if (cfg.ignoreAutoUpdate) {
                    PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.IGNORE_AUTO_UPDATE_PARAM, true, parameterPrefix);
                }

                //skip children
                if (cfg.skipChildren === false) {
                    PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.SKIP_CHILDREN_PARAM, false, parameterPrefix);
                }

                //process
                var processArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'process');
                if(cfg.fragmentId) {
                    processArray.push(cfg.fragmentId);
                }
                // default == @none
                var processIds = '@none';
                // use defined process + resolved keywords (@widget, PFS)?
                if (processArray.length > 0) {
                    processIds = processArray.join(' ');
                }
                // fallback to @all if no process was defined by the user
                else {
                    var definedProcess = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'process');
                    if (definedProcess === undefined || definedProcess.length === 0) {
                        processIds = '@all';
                    }
                }
                if (!processIds.includes('@none')) {
                    PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_PROCESS_PARAM, processIds, parameterPrefix);
                }

                //update
                var updateArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'update');
                if(updateArray.length > 0) {
                    PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_UPDATE_PARAM, updateArray.join(' '), parameterPrefix);
                }

                //behavior event
                if(cfg.event) {
                    PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.BEHAVIOR_EVENT_PARAM, cfg.event, parameterPrefix);

                    var domEvent = cfg.event;

                    if(cfg.event === 'valueChange')
                        domEvent = 'change';
                    else if(cfg.event === 'action')
                        domEvent = 'click';

                    PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_EVENT_PARAM, domEvent, parameterPrefix);
                }
                else {
                    PrimeFaces.ajax.Request.addParam(postParams, sourceId, sourceId, parameterPrefix);
                }

                //params
                if(cfg.params) {
                    PrimeFaces.ajax.Request.addParams(postParams, cfg.params, parameterPrefix);
                }
                if(cfg.ext && cfg.ext.params) {
                    PrimeFaces.ajax.Request.addParams(postParams, cfg.ext.params, parameterPrefix);
                }

                // try to get partialSubmit from global config
                if (cfg.partialSubmit === undefined) {
                    cfg.partialSubmit = PrimeFaces.settings.partialSubmit;
                }
                // check for overwrite
                if (cfg.ext && cfg.ext.partialSubmit) {
                    cfg.partialSubmit = cfg.ext.partialSubmit;
                }

                /**
                 * Only add params of process components and their children
                 * if partial submit is enabled and there are components to process partially
                 */
                if(cfg.partialSubmit && processIds.indexOf('@all') === -1) {
                    var formProcessed = false;

                    if (processIds.indexOf('@none') === -1) {
                        var partialSubmitFilter = cfg.partialSubmitFilter||':input';
                        for (var i = 0; i < processArray.length; i++) {
                            var jqProcess = $(PrimeFaces.escapeClientId(processArray[i]));
                            var componentPostParams = null;

                            if(jqProcess.is('form')) {
                                componentPostParams = jqProcess.serializeArray();
                                formProcessed = true;
                                if (multipart) {
                                    scanForFiles = scanForFiles.add(jqProcess);
                                }
                            }
                            else if(jqProcess.is(':input')) {
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

                            postParams = PrimeFaces.ajax.Request.arrayCompare(componentPostParams, postParams);

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
                        PrimeFaces.ajax.Request.addParamFromInput(postParams, PrimeFaces.VIEW_STATE, form, parameterPrefix);
                        PrimeFaces.ajax.Request.addParamFromInput(postParams, PrimeFaces.CLIENT_WINDOW, form, parameterPrefix);
                        PrimeFaces.ajax.Request.addParamFromInput(postParams, PrimeFaces.csp.NONCE_INPUT, form, parameterPrefix);
                        PrimeFaces.ajax.Request.addParamFromInput(postParams, 'dsPostWindowId', form, parameterPrefix);
                        PrimeFaces.ajax.Request.addParamFromInput(postParams, 'dspwid', form, parameterPrefix);
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
                if (PrimeFaces.settings.earlyPostParamEvaluation && cfg.earlyPostParams) {
                    postParams = PrimeFaces.ajax.Request.arrayCompare(cfg.earlyPostParams, postParams);

                    $.merge(postParams, cfg.earlyPostParams);
                }

                // scan for files and append to formData
                if (multipart) {
                    var fileInputs = $();
                    scanForFiles.each(function(index, value) {
                        var $value = $(value);
                        if ($value.is(':input[type="file"]')) {
                            fileInputs = fileInputs.add($value);
                        }
                        else {
                            fileInputs = fileInputs.add($value.find('input[type="file"]'));
                        }
                    });

                    fileInputs.each(function(index, value) {
                        for (var i = 0; i < value.files.length; i++) {
                            formData.append(value.id, value.files[i]);
                        }
                    });
                }

                var xhrOptions = {
                    url : postURL,
                    type : "POST",
                    cache : false,
                    dataType : "xml",
                    portletForms: PrimeFaces.ajax.Utils.getPorletForms(form, parameterPrefix),
                    source: cfg.source,
                    global: false,
                    beforeSend: function(xhr, settings) {
                        xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                        xhr.pfSettings = settings;
                        xhr.pfArgs = {}; // default should be an empty object

                        if(global) {
                            $(document).trigger('pfAjaxSend', [xhr, this]);
                        }
                    }
                };

                // #6360 respect form enctype multipart/form-data
                if (multipart) {
                    $.each(postParams, function(index, value) {
                        formData.append(value.name, value.value);
                    });

                    xhrOptions.data = formData;
                    xhrOptions.enctype = 'multipart/form-data';
                    xhrOptions.processData = false;
                    xhrOptions.contentType = false;
                }
                else {
                    var postData = $.param(postParams);

                    PrimeFaces.debug('Post Data:' + postData);

                    xhrOptions.data = postData;
                }

                var nonce = form.children("input[name='" + $.escapeSelector(PrimeFaces.csp.NONCE_INPUT) + "']");
                if (nonce.length > 0) {
                    xhrOptions.nonce = nonce.val();
                }

                if (cfg.timeout) {
                    xhrOptions['timeout'] = cfg.timeout;
                }

                var jqXhr = $.ajax(xhrOptions)
                    .fail(function(xhr, status, errorThrown) {
                        if (cfg.promise) {
                            cfg.promise.reject({jqXHR: xhr, textStatus: status, errorThrown: errorThrown});
                        }

                        var location = xhr.getResponseHeader("Location");
                        if (xhr.status === 401 && location) {
                            PrimeFaces.debug('Unauthorized status received. Redirecting to ' + location);
                            window.location = location;
                            return;
                        }
                        if(cfg.onerror) {
                            cfg.onerror.call(this, xhr, status, errorThrown);
                        }
                        if(cfg.ext && cfg.ext.onerror) {
                            cfg.ext.onerror.call(this, xhr, status, errorThrown);
                        }

                        $(document).trigger('pfAjaxError', [xhr, this, errorThrown]);

                        PrimeFaces.error('Request return with error:' + status + '.');
                    })
                    .done(function(data, status, xhr) {
                        PrimeFaces.debug('Response received successfully.');
                        try {
                            var parsed;

                            // Resolve promise for custom JavaScript handler
                            // Promise handlers are called asynchronously so they are run after the response was handled
                            if (cfg.promise) {
                                cfg.promise.resolve({document: data, textStatus: status, jqXHR: xhr});
                            }

                            //call user callback
                            if(cfg.onsuccess) {
                                parsed = cfg.onsuccess.call(this, data, status, xhr);
                            }

                            //extension callback that might parse response
                            if(cfg.ext && cfg.ext.onsuccess && !parsed) {
                                parsed = cfg.ext.onsuccess.call(this, data, status, xhr);
                            }

                            if(global) {
                                $(document).trigger('pfAjaxSuccess', [xhr, this]);
                            }

                            //do not execute default handler as response already has been parsed
                            if(parsed) {
                                return;
                            }
                            else {
                                PrimeFaces.ajax.Response.handle(data, status, xhr);
                            }
                        }
                        catch(err) {
                            PrimeFaces.error(err);
                        }

                        PrimeFaces.debug('DOM is updated.');
                    })
                    .always(function(data, status, xhr) {
                        // first call the extension callback (e.g. datatable paging)
                        if(cfg.ext && cfg.ext.oncomplete) {
                            cfg.ext.oncomplete.call(this, xhr, status, xhr.pfArgs, data);
                        }

                        // after that, call the end user's callback, which should be called when everything is ready
                        if(cfg.oncomplete) {
                            cfg.oncomplete.call(this, xhr, status, xhr.pfArgs, data);
                        }

                        if(global) {
                            $(document).trigger('pfAjaxComplete', [xhr, this]);
                        }

                        PrimeFaces.debug('Response completed.');

                        PrimeFaces.ajax.Queue.removeXHR(xhr);

                        if(!cfg.async) {
                            PrimeFaces.ajax.Queue.poll();
                        }
                    });

                PrimeFaces.ajax.Queue.addXHR(jqXhr);
            },

            /**
             * Collects all `process` or `update` search expressions from the given AJAX call configuration and returns
             * them as one search expression.
             * @param {Partial<PrimeFaces.ajax.Configuration>} cfg An AJAX call configuration.
             * @param {"process" | "update"} type Whether to resolve the `process` or `update` expressions.
             * @return {string} All process or update search expression from the given configuration.
             */
            resolveExpressionsForAjaxCall: function(cfg, type) {
                var expressions = '';

                if (cfg[type]) {
                    expressions += cfg[type];
                }

                if (cfg.ext && cfg.ext[type]) {
                    expressions += " " + cfg.ext[type];
                }

                return expressions;
            },

            /**
             * Given an AJAX call configuration, resolves the components for the `process` or `update` search
             * expressions given by the configurations. Resolves the search expressions to the actual components and
             * returns a list of their IDs.
             * @param {Partial<PrimeFaces.ajax.Configuration>} cfg An AJAX call configuration.
             * @param {"process" | "update"} type Whether to resolve the `process` or `update` expressions.
             * @return {string[]} A list of IDs with the components to which the process or update expressions refer.
             */
            resolveComponentsForAjaxCall: function(cfg, type) {
                var expressions = PrimeFaces.ajax.Request.resolveExpressionsForAjaxCall(cfg, type);
                return PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(expressions);
            },

            /**
             * Appends a request parameter to the given list of parameters.
             * Optionally add a prefix to the name, this is used for Portlet namespacing.
             * @template [TValue=any] Type of the parameter value.
             * @param {PrimeFaces.ajax.RequestParameter<TValue>[]} params List of parameters to which a new
             * parameter is added.
             * @param {string} name Name of the new parameter to add.
             * @param {TValue} value Value of the parameter to add.
             * @param {string} [parameterPrefix] Optional prefix that is added in front of the name.
             */
            addParam: function(params, name, value, parameterPrefix) {
                // add namespace if not available
                if (parameterPrefix || !name.indexOf(parameterPrefix) === 0) {
                    params.push({ name:parameterPrefix + name, value:value });
                }
                else {
                    params.push({ name:name, value:value });
                }

            },

            /**
             * Appends a request parameter to the given list of parameters.
             * Optionally add a prefix to the name, this is used for Portlet namespacing.
             * @template [TValue=any] Type of the parameter values.
             * @param {FormData} formData the FormData.
             * @param {string} name Name of the new parameter to add.
             * @param {TValue} value Value of the parameter to add.
             * @param {string} [parameterPrefix] Optional prefix that is added in front of the name.
             */
            addFormData: function(formData, name, value, parameterPrefix) {
                // add namespace if not available
                if (parameterPrefix || !name.indexOf(parameterPrefix) === 0) {
                    formData.append(parameterPrefix + name, value);
                }
                else {
                    formData.append(name, value);
                }
            },

            /**
             * Adds a list of callback parameters to the given list. Optionally prepends a prefix to the name of each
             * added parameter.
             * @template [TValue=any] Type of the parameter values.
             * @param {PrimeFaces.ajax.RequestParameter<TValue>[]} params List of callback parameters to which
             * parameters are added.
             * @param {PrimeFaces.ajax.RequestParameter<TValue>[]} paramsToAdd List of callback parameters to
             * add.
             * @param {string} [parameterPrefix] Optional prefix that is added in front of the name of the added
             * callback parameters.
             */
            addParams: function(params, paramsToAdd, parameterPrefix) {

                for (var i = 0; i < paramsToAdd.length; i++) {
                    var param = paramsToAdd[i];
                    // add namespace if not available
                    if (parameterPrefix && !param.name.indexOf(parameterPrefix) === 0) {
                        param.name = parameterPrefix + param.name;
                    }

                    params.push(param);
                }
            },

            /**
             * Adds a new request parameter to the given list. The value of the parameter is taken from the input
             * element of the given form. The input element must have the same name as the name of the parameter to add.
             * Optionally add a prefix to the name, which used for Portlet namespacing.
             * @param {PrimeFaces.ajax.RequestParameter[]} params List of request parameters to the new
             * parameter is added.
             * @param {string} name Name of the new parameter to add
             * @param {JQuery} form An HTML FORM element that contains an INPUT element with the given name.
             * @param {string} [parameterPrefix] Optional prefix that is added in front of the name.
             */
            addParamFromInput: function(params, name, form, parameterPrefix) {
                var input = null,
                    escapedName = $.escapeSelector(name);
                if (parameterPrefix) {
                    input = form.children("input[name*='" + escapedName + "']");
                }
                else {
                    input = form.children("input[name='" + escapedName + "']");
                }

                if (input && input.length > 0) {
                    var value = input.val();
                    PrimeFaces.ajax.Request.addParam(params, name, value, parameterPrefix);
                }
            },


            /**
             * Adds a new request parameter to the given FormData. The value of the parameter is taken from the input
             * element of the given form. The input element must have the same name as the name of the parameter to add.
             * Optionally add a prefix to the name, which used for Portlet namespacing.
             * @param {FormData} formData The FormData.
             * @param {string} name Name of the new parameter to add
             * @param {JQuery} form An HTML FORM element that contains an INPUT element with the given name.
             * @param {string} [parameterPrefix] Optional prefix that is added in front of the name.
             */
            addFormDataFromInput: function(formData, name, form, parameterPrefix) {
                var input = null,
                    escapedName = $.escapeSelector(name);
                if (parameterPrefix) {
                    input = form.children("input[name*='" + escapedName + "']");
                }
                else {
                    input = form.children("input[name='" + escapedName + "']");
                }

                if (input && input.length > 0) {
                    var value = input.val();
                    PrimeFaces.ajax.Request.addFormData(formData, name, value, parameterPrefix);
                }
            },

            /**
             * Finds the namespace (prefix) for the parameters of the given form.
             * This is required for Porlets as a Portlet contains multiple JSF views and we must only process and update the forms/inputs of the current view / application.
             * Later the namespace is used for all post params.
             * @param {JQuery} form An HTML FORM element.
             * @return {string | null} The namespace for the parameters of the given form, or `null` when the form does
             * not specifiy a namespace.
             */
            extractParameterNamespace: function(form) {
                var input = form.children("input[name*='" + PrimeFaces.VIEW_STATE + "']");
                if (input && input.length > 0) {
                    var name = input[0].name;
                    if (name.length > PrimeFaces.VIEW_STATE.length) {
                        return name.substring(0, name.indexOf(PrimeFaces.VIEW_STATE));
                    }
                }

                return null;
            },

            /**
             * Creates a new array with all parameters from the second array that are not in the first array. That is,
             * removes all parameters from the second array whose name is equal to one of the parameters in the first
             * array. The given input array are not modified.
             * @template [TValue=any] Type of the parameter values.
             * @param {PrimeFaces.ajax.RequestParameter<TValue>[]} arr1 A list of parameters for comparison.
             * @param {PrimeFaces.ajax.RequestParameter<TValue>[]} arr2 A list of additional parameters.
             * @return {PrimeFaces.ajax.RequestParameter<TValue>[]} An list of parameters that are in the second
             * array, but not in the first.
             */
            arrayCompare: function(arr1, arr2) {
                // loop arr1 params
                $.each(arr1, function(index1, param1) {
                    // loop arr2 params and remove it, if it's the same param as the arr1 param
                    arr2 = $.grep(arr2, function(param2, index2) {
                        if (param2.name === param1.name) {
                            return false;
                        }
                        return true;
                    });
                });

                return arr2;
            },

            /**
             * Creates a FormData which can be used for a Faces AJAX request on the current view.
             * It already contains all required parameters like ViewState or ClientWindow.
             *
             * @param {HTMLElement} form The cloest form of the request source.
             * @param {string} parameterPrefix The Portlet parameter namespace.
             * @param {string} source The id of the request source.
             * @param {string} process A comma seperated list of components which should be processed.
             * @param {string} update A comma seperated list of components which should be updated.
             * @return {FormData} The created FormData.
             */
            createFacesAjaxFormData: function(form, parameterPrefix, source, process, update) {
                var formData = new FormData();

                PrimeFaces.ajax.Request.addFormData(formData, PrimeFaces.PARTIAL_REQUEST_PARAM, true, parameterPrefix);
                PrimeFaces.ajax.Request.addFormData(formData, PrimeFaces.PARTIAL_SOURCE_PARAM, source, parameterPrefix);
                if (process) {
                    PrimeFaces.ajax.Request.addFormData(formData, PrimeFaces.PARTIAL_PROCESS_PARAM, process, parameterPrefix);
                }
                if (update) {
                    PrimeFaces.ajax.Request.addFormData(formData, PrimeFaces.PARTIAL_UPDATE_PARAM, update, parameterPrefix);
                }

                PrimeFaces.ajax.Request.addFormDataFromInput(formData, PrimeFaces.VIEW_STATE, form, parameterPrefix);
                PrimeFaces.ajax.Request.addFormDataFromInput(formData, PrimeFaces.CLIENT_WINDOW, form, parameterPrefix);
                PrimeFaces.ajax.Request.addFormDataFromInput(formData, PrimeFaces.csp.NONCE_INPUT, form, parameterPrefix);
                PrimeFaces.ajax.Request.addFormDataFromInput(formData, 'dsPostWindowId', form, parameterPrefix);
                PrimeFaces.ajax.Request.addFormDataFromInput(formData, 'dspwid', form, parameterPrefix);

                return formData;
            }
        },

        /**
         * The interface for the object containing low-level functionality related to handling AJAX responses. Note that
         * the different types of AJAX actions are handles by the `PrimeFaces.ResponseProcessor`.
         * @interface {PrimeFaces.ajax.Response} . The interface for the object containing functionality related to
         * handling AJAX responses
         * @type {PrimeFaces.ajax.Response}
         * @readonly
         */
        Response: {

            /**
             * Handles the response of an AJAX request. The response consists of one or more actions such as executing a
             * script or updating a DOM element. See also {@link jsf.ajax.response}.
             *
             * Also updates the specified components if any and synchronizes the client side JSF state. DOM updates are
             * implemented using jQuery which uses a very algorithm.
             *
             * @template {PrimeFaces.widget.BaseWidget} [TWidget=PrimeFaces.widget.BaseWidget] Type of the widget which
             * triggered the AJAX request.
             * @param {XMLDocument} xml The XML that was returned by the AJAX request.
             * @param {JQuery.Ajax.SuccessTextStatus} status Text status of the request.
             * @param {PrimeFaces.ajax.pfXHR} xhr The XHR request to which a response was received.
             * @param {PrimeFaces.ajax.UpdateHandler<TWidget>} [updateHandler] Optional handler for `update` actions.
             */
            handle: function(xml, status, xhr, updateHandler) {
                if (xml === undefined || xml === null) {
                    return;
                }

                var partialResponseNode = xml.getElementsByTagName("partial-response")[0];

                for (var i = 0; i < partialResponseNode.childNodes.length; i++) {
                    var currentNode = partialResponseNode.childNodes[i];

                    switch (currentNode.nodeName) {
                        case "redirect":
                            PrimeFaces.ajax.ResponseProcessor.doRedirect(currentNode);
                            break;

                        case "changes":
                            var activeElement = $(document.activeElement);
                            var activeElementId = activeElement.attr('id');
                            var activeElementSelection;
                            if (activeElement.length > 0 && activeElement.is('input') && typeof $.fn.getSelection === "function") {
                                activeElementSelection = activeElement.getSelection();
                            }

                            for (var j = 0; j < currentNode.childNodes.length; j++) {
                                var currentChangeNode = currentNode.childNodes[j];
                                switch (currentChangeNode.nodeName) {
                                    case "update":
                                        PrimeFaces.ajax.ResponseProcessor.doUpdate(currentChangeNode, xhr, updateHandler);
                                        break;
                                    case "delete":
                                        PrimeFaces.ajax.ResponseProcessor.doDelete(currentChangeNode);
                                        break;
                                    case "insert":
                                        PrimeFaces.ajax.ResponseProcessor.doInsert(currentChangeNode);
                                        break;
                                    case "attributes":
                                        PrimeFaces.ajax.ResponseProcessor.doAttributes(currentChangeNode);
                                        break;
                                    case "eval":
                                        PrimeFaces.ajax.ResponseProcessor.doEval(currentChangeNode, xhr);
                                        break;
                                    case "extension":
                                        PrimeFaces.ajax.ResponseProcessor.doExtension(currentChangeNode, xhr);
                                        break;
                                }
                            }

                            PrimeFaces.ajax.Response.handleReFocus(activeElementId, activeElementSelection);
                            PrimeFaces.ajax.Response.destroyDetachedWidgets();
                            break;

                        case "eval":
                            PrimeFaces.ajax.ResponseProcessor.doEval(currentNode);
                            break;

                        case "extension":
                            PrimeFaces.ajax.ResponseProcessor.doExtension(currentNode, xhr);
                            break;

                        case "error":
                            PrimeFaces.ajax.ResponseProcessor.doError(currentNode, xhr);
                            break;
                    }
                }
            },

            /**
             * Puts focus on the given element if necessary.
             * @param {string} activeElementId ID of the active to refocus.
             * @param {PrimeFaces.ajax.ActiveElementSelection} [activeElementSelection] The range to select, for INPUT
             * and TEXTAREA elements.
             */
            handleReFocus : function(activeElementId, activeElementSelection) {
                // skip when customFocus is active
                if (PrimeFaces.customFocus === true) {
                    PrimeFaces.customFocus = false;
                    return;
                }

                // no active element remembered
                if (!activeElementId) {
                    return;
                }

                var elementToFocus = $(PrimeFaces.escapeClientId(activeElementId));
                if (elementToFocus.length > 0) {

                    var refocus = function() {
                        // already focussed?
                        if (activeElementId !== $(document.activeElement).attr('id')) {
                            // focus
                            elementToFocus.trigger('focus');

                            // reapply cursor / selection
                            if (activeElementSelection) {
                                elementToFocus.setSelection(activeElementSelection.start, activeElementSelection.end);
                            }
                        }
                    };

                    refocus();

                    // double check it - required for IE
                    if (PrimeFaces.env.isIE()) {
                        setTimeout(function() {
                            refocus();
                        }, 50);
                    }
                }
            },

            /**
             * Destroys all widgets that are not part of the DOM anymore, usually because they were removed by an AJAX
             * update. Calls the `destroy` method on the widget and removes the widget from the global widget registry.
             */
            destroyDetachedWidgets : function() {
                // destroy detached widgets
                for (var i = 0; i < PrimeFaces.detachedWidgets.length; i++) {
                    var widgetVar = PrimeFaces.detachedWidgets[i];

                    var widget = PF(widgetVar);
                    if (widget) {
                        if (widget.isDetached() === true) {
                            PrimeFaces.widgets[widgetVar] = null;
                            widget.destroy();

                            try {
                                delete widget;
                            } catch (e) {}
                        }
                    }
                }

                PrimeFaces.detachedWidgets = [];
            }
        },

        /**
         * The interface for the object containing low-level functionality related to processing the different types
         * of actions from AJAX responses.
         * @interface {PrimeFaces.ajax.ResponseProcessor} . The interface for the object containing functionality related to
         * processing the different types of actions from AJAX responses.
         * @type {PrimeFaces.ajax.ResponseProcessor}
         * @readonly
         */
        ResponseProcessor: {

            /**
             * Handles a `redirect` AJAX action by performing a redirect to the target URL.
             * @param {Node} node The XML node of the `redirect` action.
             */
            doRedirect : function(node) {
                try {
                    window.location.assign(node.getAttribute('url'));
                } catch (error) {
                    PrimeFaces.warn('Error redirecting to URL: ' + node.getAttribute('url'));
                }
            },

            /**
             * Handles an `update` AJAX action by calling the given update handler. When no update handler is given,
             * replaces the HTML content of the element with the new content.
             * @template {PrimeFaces.widget.BaseWidget} [TWidget=PrimeFaces.widget.BaseWidget] Type of the widget which
             * triggered the AJAX request.
             * @param {Node} node The XML node of the `update` action.
             * @param {PrimeFaces.ajax.pfXHR} xhr The XHR request to which a response was received.
             * @param {PrimeFaces.ajax.UpdateHandler<TWidget>} [updateHandler] Optional handler for the update.
             */
            doUpdate : function(node, xhr, updateHandler) {
                var id = node.getAttribute('id'),
                content = PrimeFaces.ajax.Utils.getContent(node);

                if (updateHandler && updateHandler.widget && updateHandler.widget.id === id) {
                    updateHandler.handle.call(updateHandler.widget, content);
                } else {
                    PrimeFaces.ajax.Utils.updateElement(id, content, xhr);
                }
            },

            /**
             * Handles an `eval` AJAX action by evaluating the returned JavaScript.
             * @param {Node} node The XML node of the `eval` action.
             * @param {PrimeFaces.ajax.pfXHR} xhr The XHR request to which a response was received.
             */
            doEval : function(node, xhr) {
                var textContent = node.textContent || node.innerText || node.text;

                var nonce;
                if (xhr && xhr.pfSettings && xhr.pfSettings.nonce) {
                    nonce = xhr.pfSettings.nonce;
                }
                PrimeFaces.csp.eval(textContent, nonce);
            },

            /**
             * Handles an `extension` AJAX action by extending the `pfArgs` property on the jQuery XHR object.
             * @param {Node} node The XML node of the `extension` action.
             * @param {PrimeFaces.ajax.pfXHR} xhr The XHR request to which a response was received.
             */
            doExtension : function(node, xhr) {
                if (xhr) {
                    if (node.getAttribute("ln") === "primefaces" && node.getAttribute("type") === "args") {
                        var textContent = node.textContent || node.innerText || node.text;
                        // it's possible that pfArgs are already defined e.g. if Portlet parameter namespacing is enabled
                        // the "parameterPrefix" will be encoded on document start
                        // the other parameters will be encoded on document end
                        // --> see PrimePartialResponseWriter
                        if (xhr.pfArgs) {
                            var json = JSON.parse(textContent);
                            for (var name in json) {
                                xhr.pfArgs[name] = json[name];
                            }
                        }
                        else {
                            xhr.pfArgs = JSON.parse(textContent);
                        }
                    }
                }
            },

            /**
             * Handles an `error` AJAX action by doing nothing currently.
             * @param {Node} node The XML node of the `error` action.
             * @param {PrimeFaces.ajax.pfXHR} xhr The XHR request to which a response was received.
             */
            doError : function(node, xhr) {
                // currently nothing...
            },

            /**
             * Handles a `delete` AJAX action by remove the DOM element.
             * @param {Node} node The XML node of the `delete` action.
             */
            doDelete : function(node) {
                var id = node.getAttribute('id');
                $(PrimeFaces.escapeClientId(id)).remove();
            },

            /**
             * Handles an `insert` AJAX action by inserting a newly creating DOM element.
             * @param {Node} node The XML node of the `insert` action.
             * @return {boolean | undefined} `false` if the AJAX action could not be performed, `true` or `undefined`
             * otherwise.
             */
            doInsert : function(node) {
                if (!node.childNodes) {
                    return false;
                }

                for (var i = 0; i < node.childNodes.length; i++) {
                    var childNode = node.childNodes[i];
                    var id = childNode.getAttribute('id');
                    var jq = $(PrimeFaces.escapeClientId(id));
                    var content = PrimeFaces.ajax.Utils.getContent(childNode);

                    if (childNode.nodeName === "after") {
                        $(content).insertAfter(jq);
                    }
                    else if (childNode.nodeName === "before") {
                        $(content).insertBefore(jq);
                    }
                }
            },

            /**
             * Handles an `attributes` AJAX action by setting the attributes on the DOM element.
             * @param {Node} node The XML node of the `attributes` action.
             * @return {boolean | undefined} `false` if the AJAX action could not be performed, `true` or `undefined`
             * otherwise.
             */
            doAttributes : function(node) {
                if (!node.childNodes) {
                    return false;
                }

                var id = node.getAttribute('id');
                var jq = $(PrimeFaces.escapeClientId(id));

                for (var i = 0; i < node.childNodes.length; i++) {
                    var attrNode = node.childNodes[i];
                    var attrName = attrNode.getAttribute("name");
                    var attrValue = attrNode.getAttribute("value");

                    if (!attrName) {
                        return;
                    }

                    if (!attrValue || attrValue === null) {
                        attrValue = "";
                    }

                    jq.attr(attrName, attrValue);
                }
            }
        },

        /**
         * Only available for backward compatibility, do not use in new code.
         * @deprecated Use `PrimeFaces.ajax.Request.handle` instead.
         * @param {Partial<PrimeFaces.ajax.Configuration>} cfg Configuration for the AJAX request to send, such as
         * the HTTP method, the URL, and the content of the request.
         * @param {Partial<PrimeFaces.ajax.ConfigurationExtender>} [ext] Optional extender with additional options
         * that overwrite the options given in `cfg`.
         * @return {undefined} Always returns `undefined`.
         */
        AjaxRequest: function(cfg, ext) {
            return PrimeFaces.ajax.Request.handle(cfg, ext);
        }
    };

    $(window).on('unload', function() {
        PrimeFaces.ajax.Queue.abortAll();
    });

}