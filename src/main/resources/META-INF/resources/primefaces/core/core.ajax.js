if (!PrimeFaces.ajax) {

    /**
     * AJAX parameter shortcut mapping for PrimeFaces.ab
     */
    PrimeFaces.AB_MAPPING = {
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
    };

    /**
     * Ajax shortcut
     */
    PrimeFaces.ab = function(cfg, ext) {
        for (var option in cfg) {
            if (!cfg.hasOwnProperty(option)) {
                continue;
            }

            // just pass though if no mapping is available
            if (this.AB_MAPPING[option]) {
                cfg[this.AB_MAPPING[option]] = cfg[option];
                delete cfg[option];
            }
        }

        PrimeFaces.ajax.Request.handle(cfg, ext);
    };

    PrimeFaces.ajax = {

        VIEW_HEAD : "javax.faces.ViewHead",
        VIEW_BODY : "javax.faces.ViewBody",
        RESOURCE : "javax.faces.Resource",

        Utils: {
            getContent: function(node) {
                var content = '';

                for(var i = 0; i < node.childNodes.length; i++) {
                    content += node.childNodes[i].nodeValue;
                }

                return content;
            },

            updateFormStateInput: function(name, value, xhr) {
                var trimmedValue = $.trim(value);

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
                        var input = form.children("input[name='" + parameterPrefix + name + "']");

                        if (input.length > 0) {
                            input.val(trimmedValue);
                        } else {
                            form.append('<input type="hidden" name="' + parameterPrefix + name + '" value="' + trimmedValue + '" autocomplete="off" />');
                        }
                    }
                }
            },

            updateHead: function(content) {
                var cache = $.ajaxSetup()['cache'];
                $.ajaxSetup()['cache'] = true;

                var headStartTag = new RegExp("<head[^>]*>", "gi").exec(content)[0];
                var headStartIndex = content.indexOf(headStartTag) + headStartTag.length;
                $('head').html(content.substring(headStartIndex, content.lastIndexOf("</head>")));

                $.ajaxSetup()['cache'] = cache;
            },

            updateBody: function(content) {
                var bodyStartTag = new RegExp("<body[^>]*>", "gi").exec(content)[0];
                var bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
                $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
            },

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

        Queue: {

            delays: {},

            requests: new Array(),

            xhrs: new Array(),

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

            peek: function() {
                if(this.isEmpty()) {
                    return null;
                }

                return this.requests[0];
            },

            isEmpty: function() {
                return this.requests.length === 0;
            },

            addXHR: function(xhr) {
                this.xhrs.push(xhr);
            },

            removeXHR: function(xhr) {
                var index = $.inArray(xhr, this.xhrs);
                if(index > -1) {
                    this.xhrs.splice(index, 1);
                }
            },

            abortAll: function() {
                for(var i = 0; i < this.xhrs.length; i++) {
                    this.xhrs[i].abort();
                }

                this.xhrs = new Array();
                this.requests = new Array();
            }
        },

        Request: {

            handle: function(cfg, ext) {
                cfg.ext = ext;

                if (PrimeFaces.settings.earlyPostParamEvaluation) {
                    cfg.earlyPostParams = PrimeFaces.ajax.Request.collectEarlyPostParams(cfg);
                }

                if(cfg.async) {
                    PrimeFaces.ajax.Request.send(cfg);
                }
                else {
                    PrimeFaces.ajax.Queue.offer(cfg);
                }
            },

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
                        var checkboxPostParams = $("input[name='" + sourceElement.attr('name') + "']")
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
                    PrimeFaces.debug('Ajax request cancelled by onstart callback.');

                    //remove from queue
                    if(!cfg.async) {
                        PrimeFaces.ajax.Queue.poll();
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

                var postURL = form.attr('action'),
                encodedURLfield = form.children("input[name*='javax.faces.encodedURL']"),
                postParams = [];

                // See #6857 - parameter namespace for porlet
                var parameterPrefix = PrimeFaces.ajax.Request.extractParameterNamespace(form);

                //portlet support
                var portletFormsSelector = null;
                if(encodedURLfield.length > 0) {
                    portletFormsSelector = 'form[id*="' + parameterPrefix + '"]';
                    postURL = encodedURLfield.val();
                }

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
                    definedProcess = $.trim(definedProcess);
                    if (definedProcess === '') {
                        processIds = '@all';
                    }
                }
                if (processIds !== '@none') {
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
                            }
                            else if(jqProcess.is(':input')) {
                                componentPostParams = jqProcess.serializeArray();
                            }
                            else {
                                componentPostParams = jqProcess.find(partialSubmitFilter).serializeArray();
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
                }

                // remove postParam if already available in earlyPostParams
                if (PrimeFaces.settings.earlyPostParamEvaluation && cfg.earlyPostParams) {
                    postParams = PrimeFaces.ajax.Request.arrayCompare(cfg.earlyPostParams, postParams);

                    $.merge(postParams, cfg.earlyPostParams);
                }

                //serialize
                var postData = $.param(postParams);

                PrimeFaces.debug('Post Data:' + postData);

                var xhrOptions = {
                    url : postURL,
                    type : "POST",
                    cache : false,
                    dataType : "xml",
                    data : postData,
                    portletForms: portletFormsSelector,
                    source: cfg.source,
                    global: false,
                    beforeSend: function(xhr, settings) {
                        xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                        xhr.pfSettings = settings;
                        xhr.pfArgs = {}; // default should be an empty object
                        PrimeFaces.nonAjaxPosted = false;

                        if(global) {
                            $(document).trigger('pfAjaxSend', [xhr, this]);
                        }
                    }
                };

                var nonce = form.children("input[name='" + PrimeFaces.csp.NONCE_INPUT + "']");
                if (nonce.length > 0) {
                    xhrOptions.nonce = nonce.val();
                }

                if (cfg.timeout) {
                    xhrOptions['timeout'] = cfg.timeout;
                }

                var jqXhr = $.ajax(xhrOptions)
                    .fail(function(xhr, status, errorThrown) {
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
                        PrimeFaces.debug('Response received succesfully.');

                        try {
                            var parsed;

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

                        // after that, call the endusers callback, which should be called when everything is ready
                        if(cfg.oncomplete) {
                            cfg.oncomplete.call(this, xhr, status, xhr.pfArgs, data);
                        }

                        if(global) {
                            $(document).trigger('pfAjaxComplete', [xhr, this]);
                        }

                        PrimeFaces.debug('Response completed.');

                        PrimeFaces.ajax.Queue.removeXHR(xhr);

                        if(!cfg.async && !PrimeFaces.nonAjaxPosted) {
                            PrimeFaces.ajax.Queue.poll();
                        }
                    });

                PrimeFaces.ajax.Queue.addXHR(jqXhr);
            },

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

            resolveComponentsForAjaxCall: function(cfg, type) {
                var expressions = PrimeFaces.ajax.Request.resolveExpressionsForAjaxCall(cfg, type);
                return PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(expressions);
            },

            addParam: function(params, name, value, parameterPrefix) {
                // add namespace if not available
                if (parameterPrefix || !name.indexOf(parameterPrefix) === 0) {
                    params.push({ name:parameterPrefix + name, value:value });
                }
                else {
                    params.push({ name:name, value:value });
                }

            },

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

            addParamFromInput: function(params, name, form, parameterPrefix) {
                var input = null;
                if (parameterPrefix) {
                    input = form.children("input[name*='" + name + "']");
                }
                else {
                    input = form.children("input[name='" + name + "']");
                }

                if (input && input.length > 0) {
                    var value = input.val();
                    PrimeFaces.ajax.Request.addParam(params, name, value, parameterPrefix);
                }
            },

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
            }
        },

        Response: {

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
                            if (activeElement.length > 0 && activeElement.is('input') && $.isFunction($.fn.getSelection)) {
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

            handleReFocus : function(activeElementId, activeElementSelection) {

                // re-focus element
                if (PrimeFaces.customFocus === false
                        && activeElementId
                        // do we really need to refocus? we just check the current activeElement here
                        && activeElementId !== $(document.activeElement).attr('id')) {

                    var elementToFocus = $(PrimeFaces.escapeClientId(activeElementId));
                    var refocus = function() {
                        elementToFocus.focus();

                        if (activeElementSelection && activeElementSelection.start) {
                            elementToFocus.setSelection(activeElementSelection.start, activeElementSelection.end);
                        }
                    };

                    if(elementToFocus.length) {
                        refocus();

                        // double check it - required for IE
                        setTimeout(function() {
                            if (!elementToFocus.is(":focus")) {
                                refocus();
                            }
                        }, 50);
                    }
                }

                PrimeFaces.customFocus = false;
            },

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

        ResponseProcessor: {

            doRedirect : function(node) {
                try {
                    window.location.assign(node.getAttribute('url'));
                } catch (error) {
                    PrimeFaces.warn('Error redirecting to URL: ' + node.getAttribute('url'));
                }
            },

            doUpdate : function(node, xhr, updateHandler) {
                var id = node.getAttribute('id'),
                content = PrimeFaces.ajax.Utils.getContent(node);

                if (updateHandler && updateHandler.widget && updateHandler.widget.id === id) {
                    updateHandler.handle.call(updateHandler.widget, content);
                } else {
                    PrimeFaces.ajax.Utils.updateElement(id, content, xhr);
                }
            },

            doEval : function(node, xhr) {
                var textContent = node.textContent || node.innerText || node.text;

                if (xhr && xhr.pfSettings && xhr.pfSettings.nonce) {
                    // $.globalEval doesn't support nonce currently
                    // and the internal used DOMEval can't be used from outside?
                    var script = document.createElement('script');
                    script.nonce = xhr.pfSettings.nonce;
                    script.setAttribute('nonce', xhr.pfSettings.nonce);
                    script.innerHTML = textContent;
                    document.head.appendChild(script);
                }
                else {
                    $.globalEval(textContent);
                }
            },

            doExtension : function(node, xhr) {
                if (xhr) {
                    if (node.getAttribute("ln") === "primefaces" && node.getAttribute("type") === "args") {
                        var textContent = node.textContent || node.innerText || node.text;
                        // it's possible that pfArgs are already defined e.g. if portlet parameter namespacing is enabled
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

            doError : function(node, xhr) {
                // currently nothing...
            },

            doDelete : function(node) {
                var id = node.getAttribute('id');
                $(PrimeFaces.escapeClientId(id)).remove();
            },

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

        //Backward compatibility
        AjaxRequest: function(cfg, ext) {
            return PrimeFaces.ajax.Request.handle(cfg, ext);
        }
    };

    $(window).on('beforeunload', function() {
        PrimeFaces.ajax.Queue.abortAll();
    });

}