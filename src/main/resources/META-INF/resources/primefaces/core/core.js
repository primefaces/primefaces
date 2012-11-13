PrimeFaces = {

    escapeClientId : function(id) {
        return "#" + id.replace(/:/g,"\\:");
    },
		
    cleanWatermarks : function(){
        $.watermark.hideAll();
    },
	
    showWatermarks : function(){
        $.watermark.showAll();
    },
	
    addSubmitParam : function(parent, params) {
        var form = $(this.escapeClientId(parent));
        
        for(var key in params) {
            form.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + params[key] + "\" class=\"ui-submit-param\"/>");
        }

        return this;
    },

    /**
     * Submits a form and clears ui-submit-param after that to prevent dom caching issues
     */ 
    submit : function(formId) {
        $(this.escapeClientId(formId)).submit().children('input.ui-submit-param').remove();
    },

    attachBehaviors : function(element, behaviors) {
        $.each(behaviors, function(event, fn) {
            element.bind(event, function(e) {
                fn.call(element, e);
            });
        });
    },

    getCookie : function(name) {
        return $.cookie(name);
    },

    setCookie : function(name, value) {
        $.cookie(name, value);
    },

    skinInput : function(input) {
        input.hover(
            function() {
                $(this).addClass('ui-state-hover');
            },
            function() {
                $(this).removeClass('ui-state-hover');
            }
        ).focus(function() {
                $(this).addClass('ui-state-focus');
        }).blur(function() {
                $(this).removeClass('ui-state-focus');
        });
        
        //aria
        input.attr('role', 'textbox').attr('aria-disabled', input.is(':disabled'))
                                      .attr('aria-readonly', input.prop('readonly'))
                                      .attr('aria-multiline', input.is('textarea'));
        
        
        return this;
    },
    
    skinButton : function(button) {
        button.mouseover(function(){
            var el = $(this);
            if(!button.hasClass('ui-state-disabled')) {
                el.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            $(this).removeClass('ui-state-active ui-state-hover');
        }).mousedown(function() {
            var el = $(this);
            if(!button.hasClass('ui-state-disabled')) {
                el.addClass('ui-state-active').removeClass('ui-state-hover');
            }
        }).mouseup(function() {
            $(this).removeClass('ui-state-active').addClass('ui-state-hover');
        }).focus(function() {
            $(this).addClass('ui-state-focus');
        }).blur(function() {
            $(this).removeClass('ui-state-focus');
        }).keydown(function(e) {
            if(e.keyCode == $.ui.keyCode.SPACE || e.keyCode == $.ui.keyCode.ENTER || e.keyCode == $.ui.keyCode.NUMPAD_ENTER) {
                $(this).addClass('ui-state-active');
            }
        }).keyup(function() {
            $(this).removeClass('ui-state-active');
        });
        
        //aria
        button.attr('role', 'button').attr('aria-disabled', button.is(':disabled'));
        
        return this;
    },
    
    skinSelect : function(select) {
       select.mouseover(function() {
            var el = $(this);
            if(!el.hasClass('ui-state-focus'))
                el.addClass('ui-state-hover'); 
       }).mouseout(function() {
            $(this).removeClass('ui-state-hover'); 
       }).focus(function() {
            $(this).addClass('ui-state-focus').removeClass('ui-state-hover');
       }).blur(function() {
            $(this).removeClass('ui-state-focus ui-state-hover'); 
       });
        
        return this;
    },
    
    isIE: function(version) {
       return ($.browser.msie && parseInt($.browser.version, 10) == version);
    },

    //ajax shortcut
    ab: function(cfg, ext) {
        PrimeFaces.ajax.AjaxRequest(cfg, ext);
    },
    
    info: function(log) {
        if(this.logger) {
            this.logger.info(log);
        }
    },
    
    debug: function(log) {
        if(this.logger) {
            this.logger.debug(log);
        }
    },
    
    warn: function(log) {
        if(this.logger) {
            this.logger.warn(log);
        }
    },
    
    error: function(log) {
        if(this.logger) {
            this.logger.error(log);
        }
    },
    
    changeTheme: function(newTheme) {
        if(newTheme && newTheme != '') {
            var themeLink = $('link[href*="javax.faces.resource/theme.css"]'),
            themeURL = themeLink.attr('href'),
            plainURL = themeURL.split('&')[0],
            oldTheme = plainURL.split('ln=')[1],
            newThemeURL = themeURL.replace(oldTheme, 'primefaces-' + newTheme);

            themeLink.attr('href', newThemeURL);
        }
    },
    
    escapeRegExp: function(text) {
        return text.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1");
    },
    
    escapeHTML: function(value) {
        return value.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    },
    
    clearSelection: function() {
        if(window.getSelection) {
            if(window.getSelection().empty) {
                window.getSelection().empty();
            } else if (window.getSelection().removeAllRanges) {
                window.getSelection().removeAllRanges();
            } else if (document.selection) {
                document.selection.empty();
            }
        }
    },
    
    cw : function(widgetConstructor, widgetVar, cfg, resource) {
        PrimeFaces.createWidget(widgetConstructor, widgetVar, cfg, resource);
    },
    
    createWidget : function(widgetConstructor, widgetVar, cfg, resource) {            
        if(PrimeFaces.widget[widgetConstructor]) {
            if(window[widgetVar])
                window[widgetVar].refresh(cfg);                                     //ajax update
            else
                window[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);  //page init
        }
        else {
            var scriptURI = $('script[src*="/javax.faces.resource/primefaces.js"]').attr('src').replace('primefaces.js', resource + '/' + resource + '.js'),
            cssURI = $('link[href*="/javax.faces.resource/primefaces.css"]').attr('href').replace('primefaces.css', resource + '/' + resource + '.css'),
            cssResource = '<link type="text/css" rel="stylesheet" href="' + cssURI + '" />';

            //load css
            $('head').append(cssResource);

            //load script and initialize widget
            PrimeFaces.getScript(location.protocol + '//' + location.host + scriptURI, function() {
                setTimeout(function() {
                    window[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
                }, 100);
            });
        }
    },
    
    inArray: function(arr, item) {
        for(var i = 0; i < arr.length; i++) {
            if(arr[i] === item) {
                return true;
            }
        }
        
        return false;
    },

    isNumber: function(value) {
        return typeof value === 'number' && isFinite(value);
    },
    
    getScript: function(url, callback) {
        $.ajax({
			type: "GET",
			url: url,
			success: callback,
			dataType: "script",
			cache: true
        });
    },
    
    focus : function(id, context) {
        var selector = ':not(:submit):not(:button):input:visible:enabled';

        setTimeout(function() {
            if(id) {
                var jq = $(PrimeFaces.escapeClientId(id));

                if(jq.is(selector)) {
                    jq.focus();
                } 
                else {
                    jq.find(selector).eq(0).focus();
                }
            }
            else if(context) {
                $(PrimeFaces.escapeClientId(context)).find(selector).eq(0).focus();
            }
            else {
                $(selector).eq(0).focus();
            }
        }, 250);
    },
    
    monitorDownload: function(start, complete) {
        if(start) {
            start();
        }

        window.downloadMonitor = setInterval(function() {
            var downloadComplete = PrimeFaces.getCookie('primefaces.download');

            if(downloadComplete == 'true') {
                if(complete) {
                    complete();
                }
                clearInterval(window.downloadPoll);
                PrimeFaces.setCookie('primefaces.download', null);
            }
        }, 500);
    },
    
    /**
     *  Scrolls to a component with given client id
     */
    scrollTo: function(id) {
        var offset = $(PrimeFaces.escapeClientId(id)).offset();

        $('html,body').animate({
                scrollTop:offset.top
                ,scrollLeft:offset.left
            },{
               easing: 'easeInCirc'
            },1000);
            
    },
    
    /**
     *  Aligns container scrollbar to keep item in container viewport, algorithm copied from jquery-ui menu widget
     */
    scrollInView: function(container, item) {        
        var borderTop = parseFloat(container.css('borderTopWidth')) || 0,
        paddingTop = parseFloat(container.css('paddingTop')) || 0,
        offset = item.offset().top - container.offset().top - borderTop - paddingTop,
        scroll = container.scrollTop(),
        elementHeight = container.height(),
        itemHeight = item.outerHeight(true);

        if(offset < 0) {
            container.scrollTop(scroll + offset);
        }
        else if((offset + itemHeight) > elementHeight) {
            container.scrollTop(scroll + offset - elementHeight + itemHeight);
        }
    },

    locales : {},
    
    zindex : 1000,
	
    PARTIAL_REQUEST_PARAM : "javax.faces.partial.ajax",

    PARTIAL_UPDATE_PARAM : "javax.faces.partial.render",

    PARTIAL_PROCESS_PARAM : "javax.faces.partial.execute",

    PARTIAL_SOURCE_PARAM : "javax.faces.source",

    BEHAVIOR_EVENT_PARAM : "javax.faces.behavior.event",

    PARTIAL_EVENT_PARAM : "javax.faces.partial.event",

    VIEW_STATE : "javax.faces.ViewState",
    
    VIEW_ROOT : "javax.faces.ViewRoot",
    
    CLIENT_ID_DATA : "primefaces.clientid"
};

/**
 * PrimeFaces Namespaces
 */
PrimeFaces.ajax = {};
PrimeFaces.widget = {};

/**
 * BaseWidget for PrimeFaces Widgets
 */
PrimeFaces.widget.BaseWidget = Class.extend({
    
  init: function(cfg) {
    this.cfg = cfg;
    this.id = cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id),
    this.jq = $(this.jqId);
    
    //remove script tag
    $(this.jqId + '_s').remove();
  },
  
  //used mostly in ajax updates, reloads the widget configuration
  refresh: function(cfg) {
    return this.init(cfg);
  },
  
  //returns jquery object representing the main dom element related to the widget
  getJQ: function(){
    return this.jq;
  }
  
});

PrimeFaces.ajax.AjaxUtils = {
	
    encodeViewState : function() {
        var viewstateValue = document.getElementById(PrimeFaces.VIEW_STATE).value;
        var re = new RegExp("\\+", "g");
        var encodedViewState = viewstateValue.replace(re, "\%2B");
		
        return encodedViewState;
    },
	
    updateState: function(value) {
        var viewstateValue = $.trim(value),
        forms = this.portletForms ? this.portletForms : $('form');
        
        forms.each(function() {
            var form = $(this),
            formViewStateElement = form.children("input[name='javax.faces.ViewState']").get(0);

            if(formViewStateElement) {
                $(formViewStateElement).val(viewstateValue);
            }
            else
            {
                form.append('<input type="hidden" name="javax.faces.ViewState" id="javax.faces.ViewState" value="' + viewstateValue + '" autocomplete="off" />');
            }
        });
    },

    updateElement: function(id, content) {        
        if(id == PrimeFaces.VIEW_STATE) {
            PrimeFaces.ajax.AjaxUtils.updateState.call(this, content);
        }
        else if(id == PrimeFaces.VIEW_ROOT) {
            document.open();
            document.write(content);
            document.close();
        }
        else {
            $(PrimeFaces.escapeClientId(id)).replaceWith(content);
        }
    },

    /**
     *  Handles response handling tasks after updating the dom
     **/
    handleResponse: function(xmlDoc) {
        var redirect = xmlDoc.find('redirect'),
        callbackParams = xmlDoc.find('extension[ln="primefaces"][type="args"]'),
        scripts = xmlDoc.find('eval');

        if(redirect.length > 0) {
            window.location = redirect.attr('url');
        }
        else {
            //args
            this.args = callbackParams.length > 0 ? $.parseJSON(callbackParams.text()) : {};

            //scripts to execute
            for(var i=0; i < scripts.length; i++) {
                $.globalEval(scripts.eq(i).text());
            }
        }
    },
    
    findComponents: function(selector) {
        //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
        var jqSelector = selector.substring(2, selector.length - 1),
        components = $(jqSelector),
        ids = [];
        
        components.each(function() {
            var element = $(this),
            clientId = element.data(PrimeFaces.CLIENT_ID_DATA)||element.attr('id');
            
            ids.push(clientId);
        });
        
        return ids;
    },
    
    idsToArray: function(cfg, type, selector) {
        var arr = [],
        def = cfg[type],
        ext = cfg.ext ? cfg.ext[type] : null;
        
        if(def) {
            $.merge(arr, def.split(' '));
        }
        
        if(ext) {
            var extArr = ext.split(' ');
            
            for(var i = 0; i < extArr.length; i++) {
                if(!PrimeFaces.inArray(arr, extArr[i])) {
                    arr.push(extArr[i]);
                }
            }
        }

        if(selector) {
            $.merge(arr, PrimeFaces.ajax.AjaxUtils.findComponents(selector));
        }
        
        return arr;
    },
    
    send: function(cfg) {
        PrimeFaces.debug('Initiating ajax request.');
    
        if(cfg.onstart) {
            var retVal = cfg.onstart.call(this, cfg);
            if(retVal == false) {
                PrimeFaces.debug('Ajax request cancelled by onstart callback.');
                
                //remove from queue
                if(!cfg.async) {
                    PrimeFaces.ajax.Queue.poll();
                }
                
                return;  //cancel request
            }
        }

        var form = null,
        sourceId = null;

        //source can be a client id or an element defined by this keyword
        if(typeof(cfg.source) == 'string') {
            sourceId = cfg.source;
        } else {
            sourceId = $(cfg.source).attr('id');
        }

        if(cfg.formId) {
            form = $(PrimeFaces.escapeClientId(cfg.formId));                         //Explicit form is defined
        }
        else {
            form = $(PrimeFaces.escapeClientId(sourceId)).parents('form:first');     //look for a parent of source

            //source has no parent form so use first form in document
            if(form.length == 0) {
                form = $('form').eq(0);
            }
        }

        PrimeFaces.debug('Form to post ' + form.attr('id') + '.');

        var postURL = form.attr('action'),
        encodedURLfield = form.children("input[name='javax.faces.encodedURL']"),
        postParams = [];

        //portlet support
        var pForms = null;
        if(encodedURLfield.length > 0) {
            postURL = encodedURLfield.val();
            pForms = $('form[action="' + form.attr('action') + '"]');   //find forms of the portlet
        }

        PrimeFaces.debug('URL to post ' + postURL + '.');

        //partial ajax
        postParams.push({name:PrimeFaces.PARTIAL_REQUEST_PARAM, value:true});

        //source
        postParams.push({name:PrimeFaces.PARTIAL_SOURCE_PARAM, value:sourceId});

        //process
        var processArray = PrimeFaces.ajax.AjaxUtils.idsToArray(cfg, 'process', cfg.processSelector),
        processIds = processArray.length > 0 ? processArray.join(' ') : '@all';
        postParams.push({name:PrimeFaces.PARTIAL_PROCESS_PARAM, value:processIds});
        
        //update
        var updateArray = PrimeFaces.ajax.AjaxUtils.idsToArray(cfg, 'update', cfg.updateSelector);
        if(updateArray.length > 0) {
            postParams.push({name:PrimeFaces.PARTIAL_UPDATE_PARAM, value:updateArray.join(' ')});
        }
        
        //behavior event
        if(cfg.event) {
            postParams.push({name:PrimeFaces.BEHAVIOR_EVENT_PARAM, value:cfg.event});

            var domEvent = cfg.event;

            if(cfg.event == 'valueChange')
                domEvent = 'change';
            else if(cfg.event == 'action')
                domEvent = 'click';

            postParams.push({name:PrimeFaces.PARTIAL_EVENT_PARAM, value:domEvent});
        } 
        else {
            postParams.push({name:sourceId, value:sourceId});
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
        if(cfg.partialSubmit && processIds != '@all') {
            var hasViewstate = false;
            
            if(processIds != '@none') {
                var processIdsArray = processIds.split(' ');

                $.each(processIdsArray, function(i, item) {
                    var jqProcess = $(PrimeFaces.escapeClientId(item)),
                    componentPostParams = null;
                    
                    if(jqProcess.is('form')) {
                        componentPostParams = jqProcess.serializeArray();
                        hasViewstate = true;
                    }
                    else if(jqProcess.is(':input')) {
                        componentPostParams = jqProcess.serializeArray();
                    }
                    else {
                        componentPostParams = jqProcess.find(':input').serializeArray();
                    }
                    
                    $.merge(postParams, componentPostParams);
                });
            }
            
            //add viewstate if necessary
            if(!hasViewstate) {
                postParams.push({name:PrimeFaces.VIEW_STATE, value:form.children("input[name='javax.faces.ViewState']").val()});
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
            portletForms: pForms,
            source: cfg.source,
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Faces-Request', 'partial/ajax');
            },
            error: function(xhr, status, errorThrown) {
                if(cfg.onerror) {
                    cfg.onerror.call(xhr, status, errorThrown);
                }

                PrimeFaces.error('Request return with error:' + status + '.');
            },
            success : function(data, status, xhr) {
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

                //do not execute default handler as response already has been parsed
                if(parsed) {
                    return;
                } 
                else {
                    PrimeFaces.ajax.AjaxResponse.call(this, data, status, xhr);
                }

                PrimeFaces.debug('DOM is updated.');
            },
            complete : function(xhr, status) {
                if(cfg.oncomplete) {
                    cfg.oncomplete.call(this, xhr, status, this.args);
                }

                if(cfg.ext && cfg.ext.oncomplete) {
                    cfg.ext.oncomplete.call(this, xhr, status, this.args);
                }

                PrimeFaces.debug('Response completed.');

                if(!cfg.async) {
                    PrimeFaces.ajax.Queue.poll();
                }
            }
        };

        xhrOptions.global = cfg.global == true || cfg.global == undefined ? true : false;
        
        $.ajax(xhrOptions);
    }
};

PrimeFaces.ajax.AjaxRequest = function(cfg, ext) {
    cfg.ext = ext;
    
    if(cfg.async) {
        PrimeFaces.ajax.AjaxUtils.send(cfg);
    }
    else {
        PrimeFaces.ajax.Queue.offer(cfg);
    }
}

PrimeFaces.ajax.AjaxResponse = function(responseXML) {
    var xmlDoc = $(responseXML.documentElement),
    updates = xmlDoc.find('update');

    for(var i=0; i < updates.length; i++) {
        var update = updates.eq(i),
        id = update.attr('id'),
        content = update.text();

        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
    }

    PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
}

PrimeFaces.ajax.Queue = {
		
    requests : new Array(),
    
    offer : function(request) {
        this.requests.push(request);
        
        if(this.requests.length == 1) {
            PrimeFaces.ajax.AjaxUtils.send(request);
        }
    },
    
    poll : function() {
        if(this.isEmpty()) {
            return null;
        }
        
        var processed = this.requests.shift(),
        next = this.peek();
        
        if(next != null) {
            PrimeFaces.ajax.AjaxUtils.send(next);
        }

        return processed;
    },
    
    peek : function() {
        if(this.isEmpty()) {
            return null;
        }
        
        return this.requests[0];
    },
        
    isEmpty : function() {
        return this.requests.length == 0;
    }
};