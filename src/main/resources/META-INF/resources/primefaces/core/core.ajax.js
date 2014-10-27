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

            for (var i = 0; i < forms.length; i++) {
                var form = forms.eq(i);

                if (form.attr('method') === 'post') {
                    var input = form.children("input[name='" + name + "']");

                    if (input.length > 0) {
                        input.val(trimmedValue);
                    } else {
                        form.append('<input type="hidden" name="' + name + '" value="' + trimmedValue + '" autocomplete="off" />');
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
            encodedURLfield = form.children("input[name='javax.faces.encodedURL']"),
            postParams = [];

            //portlet support
            var porletFormsSelector = null;
            if(encodedURLfield.length > 0) {
                porletFormsSelector = 'form[action="' + postURL + '"]';
                postURL = encodedURLfield.val();
            }

            PrimeFaces.debug('URL to post ' + postURL + '.');

            //partial ajax
            postParams.push({
                name:PrimeFaces.PARTIAL_REQUEST_PARAM,
                value:true
            });

            //source
            postParams.push({
                name:PrimeFaces.PARTIAL_SOURCE_PARAM,
                value:sourceId
            });

            //resetValues
            if (cfg.resetValues) {
                postParams.push({
                    name:PrimeFaces.RESET_VALUES_PARAM,
                    value:true
                });
            }

            //ignoreAutoUpdate
            if (cfg.ignoreAutoUpdate) {
                postParams.push({
                    name:PrimeFaces.IGNORE_AUTO_UPDATE_PARAM,
                    value:true
                });
            }

            //process
            var processArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'process');
            if(cfg.fragmentId) {
                processArray.push(cfg.fragmentId);
            }
            var processIds = processArray.length > 0 ? processArray.join(' ') : '@all';
            if (processIds !== '@none') {
                postParams.push({
                    name:PrimeFaces.PARTIAL_PROCESS_PARAM,
                    value:processIds
                });
            }

            //update
            var updateArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'update');
            if(cfg.fragmentId && cfg.fragmentUpdate) {
                updateArray.push(cfg.fragmentId);
            }
            if(updateArray.length > 0) {
                postParams.push({
                    name:PrimeFaces.PARTIAL_UPDATE_PARAM,
                    value:updateArray.join(' ')
                });
            }

            //behavior event
            if(cfg.event) {
                postParams.push({
                    name:PrimeFaces.BEHAVIOR_EVENT_PARAM,
                    value:cfg.event
                });

                var domEvent = cfg.event;

                if(cfg.event === 'valueChange')
                    domEvent = 'change';
                else if(cfg.event === 'action')
                    domEvent = 'click';

                postParams.push({
                    name:PrimeFaces.PARTIAL_EVENT_PARAM,
                    value:domEvent
                });
            }
            else {
                postParams.push({
                    name:sourceId,
                    value:sourceId
                });
            }

            //params
            if(cfg.params) {
                $.merge(postParams, cfg.params);
            }
            if(cfg.ext && cfg.ext.params) {
                $.merge(postParams, cfg.ext.params);
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
                if(!formProcessed) {
                    postParams.push({
                        name:PrimeFaces.VIEW_STATE,
                        value:form.children("input[name='" + PrimeFaces.VIEW_STATE + "']").val()
                    });

                    var clientWindowInput = form.children("input[name='" + PrimeFaces.CLIENT_WINDOW + "']");
                    if (clientWindowInput.length > 0) {
                        postParams.push({ name:PrimeFaces.CLIENT_WINDOW, value:clientWindowInput.val() });
                    }

                    // DS compatiblity
                    var dsClientWindowInput = form.children("input[name='dsPostWindowId']");
                    if (dsClientWindowInput.length > 0) {
                        postParams.push({ name:'dsPostWindowId', value:dsClientWindowInput.val() });
                    }
                    dsClientWindowInput = form.children("input[name='dspwid']");
                    if (dsClientWindowInput.length > 0) {
                        postParams.push({ name:'dspwid', value:dsClientWindowInput.val() });
                    }
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
                    xhr.pfArgs = $.parseJSON(textContent);
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