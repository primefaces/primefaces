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
    'iau': 'ignoreAutoUpdate',
    'ps': 'partialSubmit',
    'psf': 'partialSubmitFilter',
    'rv': 'resetValues',
    'fi': 'fragmentId',
    'fu': 'fragmentUpdate',
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

            var parameterNamespace = '';
            if (xhr && xhr.pfArgs && xhr.pfArgs.parameterNamespace) {
                parameterNamespace = xhr.pfArgs.parameterNamespace;
            }

            for (var i = 0; i < forms.length; i++) {
                var form = forms.eq(i);

                if (form.attr('method') === 'post') {
                    var input = form.children("input[name='" + parameterNamespace + name + "']");

                    if (input.length > 0) {
                        input.val(trimmedValue);
                    } else {
                        form.append('<input type="hidden" name="' + parameterNamespace + name + '" value="' + trimmedValue + '" autocomplete="off" />');
                    }
                }
            }
        },

        updateElement: function(id, content, xhr) {
            if (id.indexOf(PrimeFaces.VIEW_STATE) !== -1) {
                PrimeFaces.ajax.Utils.updateFormStateInput(PrimeFaces.VIEW_STATE, content, xhr);
            }
            else if (id.indexOf(PrimeFaces.CLIENT_WINDOW) !== -1) {
                PrimeFaces.ajax.Utils.updateFormStateInput(PrimeFaces.CLIENT_WINDOW, content, xhr);
            }
            else if (id === PrimeFaces.VIEW_ROOT) {
                // reset PrimeFaces JS state
                window.PrimeFaces = null;

                var cache = $.ajaxSetup()['cache'];
                $.ajaxSetup()['cache'] = true;
                $('head').html(content.substring(content.indexOf("<head>") + 6, content.lastIndexOf("</head>")));
                $.ajaxSetup()['cache'] = cache;

                var bodyStartTag = new RegExp("<body[^>]*>", "gi").exec(content)[0];
                var bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
                $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
            }
            else if (id === PrimeFaces.ajax.VIEW_HEAD) {
                // reset PrimeFaces JS state
                window.PrimeFaces = null;

                var cache = $.ajaxSetup()['cache'];
                $.ajaxSetup()['cache'] = true;
                $('head').html(content.substring(content.indexOf("<head>") + 6, content.lastIndexOf("</head>")));
                $.ajaxSetup()['cache'] = cache;
            }
            else if (id === PrimeFaces.ajax.VIEW_BODY) {
                var bodyStartTag = new RegExp("<body[^>]*>", "gi").exec(content)[0];
                var bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
                $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
            }
            else {
                $(PrimeFaces.escapeClientId(id)).replaceWith(content);
            }
        }
    },

    Queue: {

        delays: {},

        requests: [],

        xhrs: [],

        offer: function(request) {
            if(request.delay) {
                var $this = this,
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

            this.xhrs = [];
            this.requests = [];
        }
    },

    Request: {

        handle: function(cfg, ext) {
            cfg.ext = ext;

            if(cfg.async) {
                PrimeFaces.ajax.Request.send(cfg);
            }
            else {
                PrimeFaces.ajax.Queue.offer(cfg);
            }
        },

        send: function(cfg) {
            PrimeFaces.debug('Initiating ajax request.');

            PrimeFaces.customFocus = false;

            var global = (cfg.global === true || cfg.global === undefined) ? true : false,
            form = null,
            sourceId = null;

            if(cfg.onstart) {
                var retVal = cfg.onstart.call(this, cfg);
                if(retVal === false) {
                    PrimeFaces.debug('Ajax request cancelled by onstart callback.');

                    //remove from queue
                    if(!cfg.async) {
                        PrimeFaces.ajax.Queue.poll();
                    }

                    return false;  //cancel request
                }
            }
            if(cfg.ext && cfg.ext.onstart) {
                cfg.ext.onstart.call(this, cfg);
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
                form = $(PrimeFaces.escapeClientId(cfg.formId));
            }
            else {
                //look for a parent of source
                form = $(PrimeFaces.escapeClientId(sourceId)).parents('form:first');

                //source has no parent form so use first form in document
                if (form.length === 0) {
                    form = $('form').eq(0);
                }
            }

            PrimeFaces.debug('Form to post ' + form.attr('id') + '.');

            var postURL = form.attr('action'),
            encodedURLfield = form.children("input[name*='javax.faces.encodedURL']"),
            postParams = [];

            //portlet support
            var porletFormsSelector = null;
            if(encodedURLfield.length > 0) {
                porletFormsSelector = 'form[action="' + postURL + '"]';
                postURL = encodedURLfield.val();
            }

            PrimeFaces.debug('URL to post ' + postURL + '.');

            // See #6857 - parameter namespace for porlet
            var parameterNamespace = PrimeFaces.ajax.Request.extractParameterNamespace(form);

            //partial ajax
            PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_REQUEST_PARAM, true, parameterNamespace);

            //source
            PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_SOURCE_PARAM, sourceId, parameterNamespace);

            //resetValues
            if (cfg.resetValues) {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.RESET_VALUES_PARAM, true, parameterNamespace);
            }

            //ignoreAutoUpdate
            if (cfg.ignoreAutoUpdate) {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.IGNORE_AUTO_UPDATE_PARAM, true, parameterNamespace);
            }

            //process
            var processArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'process');
            if(cfg.fragmentId) {
                processArray.push(cfg.fragmentId);
            }
            var processIds = processArray.length > 0 ? processArray.join(' ') : '@all';
            if (processIds !== '@none') {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_PROCESS_PARAM, processIds, parameterNamespace);
            }

            //update
            var updateArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'update');
            if(cfg.fragmentId && cfg.fragmentUpdate) {
                updateArray.push(cfg.fragmentId);
            }
            if(updateArray.length > 0) {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_UPDATE_PARAM, updateArray.join(' '), parameterNamespace);
            }

            //behavior event
            if(cfg.event) {
                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.BEHAVIOR_EVENT_PARAM, cfg.event, parameterNamespace);

                var domEvent = cfg.event;

                if(cfg.event === 'valueChange')
                    domEvent = 'change';
                else if(cfg.event === 'action')
                    domEvent = 'click';

                PrimeFaces.ajax.Request.addParam(postParams, PrimeFaces.PARTIAL_EVENT_PARAM, domEvent, parameterNamespace);
            }
            else {
                PrimeFaces.ajax.Request.addParam(postParams, sourceId, sourceId, parameterNamespace);
            }

            //params
            if(cfg.params) {
                PrimeFaces.ajax.Request.addParams(postParams, cfg.params, parameterNamespace);
            }
            if(cfg.ext && cfg.ext.params) {
                PrimeFaces.ajax.Request.addParams(postParams, cfg.ext.params, parameterNamespace);
            }

            /**
             * Only add params of process components and their children
             * if partial submit is enabled and there are components to process partially
             */
            if(cfg.partialSubmit && processIds.indexOf('@all') === -1) {
                var formProcessed = false,
                partialSubmitFilter = cfg.partialSubmitFilter||':input';

                if(processIds.indexOf('@none') === -1) {
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

                        $.merge(postParams, componentPostParams);
                    }
                }

                //add form state if necessary
                if (!formProcessed) {
                    PrimeFaces.ajax.Request.addParamFromInput(postParams, PrimeFaces.VIEW_STATE, form, parameterNamespace);
                    PrimeFaces.ajax.Request.addParamFromInput(postParams, PrimeFaces.CLIENT_WINDOW, form, parameterNamespace);
                    PrimeFaces.ajax.Request.addParamFromInput(postParams, 'dsPostWindowId', form, parameterNamespace);
                    PrimeFaces.ajax.Request.addParamFromInput(postParams, 'dspwid', form, parameterNamespace);
                }

            }
            else {
                $.merge(postParams, form.serializeArray());
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
                portletForms: porletFormsSelector,
                source: cfg.source,
                global: false,
                beforeSend: function(xhr, settings) {
                    xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                    xhr.pfSettings = settings;
                    xhr.pfArgs = {}; // default should be an empty object

                    if(global) {
                        $(document).trigger('pfAjaxSend', [xhr, this]);
                    }
                },
                error: function(xhr, status, errorThrown) {
                    if(cfg.onerror) {
                        cfg.onerror.call(this, xhr, status, errorThrown);
                    }
                    if(cfg.ext && cfg.ext.onerror) {
                        cfg.ext.onerror.call(this, xhr, status, errorThrown);
                    }

                    if(global) {
                        $(document).trigger('pfAjaxError', [xhr, this, errorThrown]);
                    }

                    PrimeFaces.error('Request return with error:' + status + '.');
                },
                success: function(data, status, xhr) {
                    PrimeFaces.debug('Response received succesfully.');

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

                    PrimeFaces.debug('DOM is updated.');
                },
                complete: function(xhr, status) {
                    if(cfg.oncomplete) {
                        cfg.oncomplete.call(this, xhr, status, xhr.pfArgs);
                    }

                    if(cfg.ext && cfg.ext.oncomplete) {
                        cfg.ext.oncomplete.call(this, xhr, status, xhr.pfArgs);
                    }

                    if(global) {
                        $(document).trigger('pfAjaxComplete', [xhr, this]);
                    }

                    PrimeFaces.debug('Response completed.');

                    PrimeFaces.ajax.Queue.removeXHR(xhr);

                    if(!cfg.async) {
                        PrimeFaces.ajax.Queue.poll();
                    }
                }
            };

            if (cfg.timeout) {
                xhrOptions['timeout'] = cfg.timeout;
            }

            PrimeFaces.ajax.Queue.addXHR($.ajax(xhrOptions));
        },

        resolveComponentsForAjaxCall: function(cfg, type) {

            var expressions = '';

            if (cfg[type]) {
                expressions += cfg[type];
            }

            if (cfg.ext && cfg.ext[type]) {
                expressions += " " + cfg.ext[type];
            }

            return PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(expressions);
        },

        addParam: function(params, name, value, parameterNamespace) {
            // add namespace if not available
            if (parameterNamespace || !name.indexOf(parameterNamespace) === 0) {
                params.push({ name:parameterNamespace + name, value:value });
            }
            else {
                params.push({ name:name, value:value });
            }

        },

        addParams: function(params, paramsToAdd, parameterNamespace) {

            for (var i = 0; i < paramsToAdd.length; i++) {
                var param = paramsToAdd[i];
                // add namespace if not available
                if (parameterNamespace && !param.name.indexOf(parameterNamespace) === 0) {
                    param.name = parameterNamespace + param.name;
                }

                params.push(param);
            }
        },

        addParamFromInput: function(params, name, form, parameterNamespace) {
            var input = null;
            if (parameterNamespace) {
                input = form.children("input[name*='" + name + "']");
            }
            else {
                input = form.children("input[name='" + name + "']");
            }

            if (input && input.length > 0) {
                var value = input.val();
                PrimeFaces.ajax.Request.addParam(params, name, value, parameterNamespace);
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
        }
    },

    Response: {

        handle: function(xml, status, xhr, updateHandler) {
            var partialResponseNode = xml.getElementsByTagName("partial-response")[0];

            for (var i = 0; i < partialResponseNode.childNodes.length; i++) {
                var currentNode = partialResponseNode.childNodes[i];

                switch (currentNode.nodeName) {
                    case "redirect":
                        PrimeFaces.ajax.ResponseProcessor.doRedirect(currentNode);
                        break;

                    case "changes":
                        var activeElementId = $(document.activeElement).attr('id');

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
                                    PrimeFaces.ajax.ResponseProcessor.doEval(currentChangeNode);
                                    break;
                                case "extension":
                                    PrimeFaces.ajax.ResponseProcessor.doExtension(currentChangeNode, xhr);
                                    break;
                            }
                        }

                        PrimeFaces.ajax.Response.handleReFocus(activeElementId);
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

        handleReFocus : function(activeElementId) {
            // re-focus element
            if (PrimeFaces.customFocus === false
                    && activeElementId
                    // do we really need to refocus? we just check the current activeElement here
                    && activeElementId !== $(document.activeElement).attr('id')) {

                var elementToFocus = $(PrimeFaces.escapeClientId(activeElementId));
                elementToFocus.focus();

                // double check it - required for IE
                setTimeout(function() {
                    if (!elementToFocus.is(":focus")) {
                        elementToFocus.focus();
                    }
                }, 150);
            }

            PrimeFaces.customFocus = false;
        },

        destroyDetachedWidgets : function() {
            // destroy detached widgets
            for (var i = 0; i < PrimeFaces.detachedWidgets.length; i++) {
                var widgetVar = PrimeFaces.detachedWidgets[i];

                var widget = PF(widgetVar);
                if (widget) {
                    if (widget.isDetached()) {
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
            window.location = node.getAttribute('url');
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

        doEval : function(node) {
            var textContent = node.textContent || node.innerText || node.text;
            $.globalEval(textContent);
        },

        doExtension : function(node, xhr) {
            if (xhr) {
                if (node.getAttribute("ln") === "primefaces" && node.getAttribute("type") === "args") {
                    var textContent = node.textContent || node.innerText || node.text;
                    // it's possible that pfArgs are already defined e.g. if portlet parameter namespacing is enabled
                    // the "parameterNamespace" will be encoded on document start
                    // the other parameters will be encoded on document end
                    // --> see PrimePartialResponseWriter
                    if (xhr.pfArgs) {
                        var json = $.parseJSON(textContent);
                        for (var name in json) {
                            xhr.pfArgs[name] = json[name];
                        }
                    }
                    else {
                        xhr.pfArgs = $.parseJSON(textContent);
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
