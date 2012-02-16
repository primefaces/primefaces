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
        form.children('input.ui-submit-param').remove();

        for(var key in params) {
            form.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + params[key] + "\" class=\"ui-submit-param\"/>");
        }

        return this;
    },

    submit : function(formId) {
        $(this.escapeClientId(formId)).submit();
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
                el.addClass('ui-state-active');
            }
        }).mouseup(function() {
            $(this).removeClass('ui-state-active');
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

    //ajax shortcut
    ab : function(cfg, ext) {
        PrimeFaces.ajax.AjaxRequest(cfg, ext);
    },

    //mobile
    navigate : function(to, cfg) {
        var options = cfg ? cfg : {};
        options.changeHash = false;
        
        $.mobile.changePage(to, options);
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
    
    extend: function(subClass, superClass) {
        subClass.prototype = new superClass;
        subClass.prototype.constructor = subClass;
    },
    
    cw : function(widgetConstructor, widgetVar, cfg, resource) {
        PrimeFaces.createWidget(widgetConstructor, widgetVar, cfg, resource);
    },
    
    createWidget : function(widgetConstructor, widgetVar, cfg, resource) {            
        if(PrimeFaces.widget[widgetConstructor]) {
            window[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
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
    
    monitorDownload : function(start, complete) {
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

    locales : {},
    
    zindex : 1000,
	
    PARTIAL_REQUEST_PARAM : "javax.faces.partial.ajax",

    PARTIAL_UPDATE_PARAM : "javax.faces.partial.render",

    PARTIAL_PROCESS_PARAM : "javax.faces.partial.execute",

    PARTIAL_SOURCE_PARAM : "javax.faces.source",

    BEHAVIOR_EVENT_PARAM : "javax.faces.behavior.event",

    PARTIAL_EVENT_PARAM : "javax.faces.partial.event",

    VIEW_STATE : "javax.faces.ViewState"
};

PrimeFaces.ajax = {};
PrimeFaces.widget = {};
PrimeFaces.websockets = {};

/**
 * BaseWidget for PrimeFaces Widgets to implement common tasks
 */
PrimeFaces.widget.BaseWidget = function() {}

PrimeFaces.widget.BaseWidget.prototype.postConstruct = function() {
    this.getScriptTag().remove();
};

PrimeFaces.widget.BaseWidget.prototype.getScriptTag = function() {
    return $(this.jqId + '_s');
};

PrimeFaces.widget.BaseWidget.prototype.getJQ = function() {
    return this.jq;
};

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
	
    serialize: function(params) {
        var serializedParams = '';
		
        for(var param in params) {
            serializedParams = serializedParams + "&" + param + "=" + params[param];
        }
		
        return serializedParams;
    },

    updateElement: function(id, content) {        
        if(id == PrimeFaces.VIEW_STATE) {
            PrimeFaces.ajax.AjaxUtils.updateState.call(this, content);
        }
        else {
            $(PrimeFaces.escapeClientId(id)).replaceWith(content);

            //PrimeFaces Mobile
            if($.mobile) {
                var controls = $(PrimeFaces.escapeClientId(id)).parent().find("input, textarea, select, button, ul");

                //input and textarea
                controls
                    .filter("input, textarea")
                    .not("[type='radio'], [type='checkbox'], [type='button'], [type='submit'], [type='reset'], [type='image'], [type='hidden']")
                    .textinput();
                    
                //lists
                controls.filter("[data-role='listview']").listview();
                
                //buttons
                controls.filter("button, [type='button'], [type='submit'], [type='reset'], [type='image']" ).button();

                //slider
                controls.filter("input, select")
                        .filter("[data-role='slider'], [data-type='range']")
                        .slider();
                
                //selects
                controls.filter("select:not([data-role='slider'])" ).selectmenu();
            }
        }
    },

    /**
     *  Handles response handling tasks after updating the dom
     **/
    handleResponse: function(xmlDoc) {
        var redirect = xmlDoc.find('redirect'),
        callbackParams = xmlDoc.find('extension[ln="primefaces"][type="args"]'),
        pushData = xmlDoc.find('extension[ln="primefaces"][type="push-data"]'),
        scripts = xmlDoc.find('eval');

        if(redirect.length > 0) {
            window.location = redirect.attr('url');
        }
        else {
            //args
            this.args = callbackParams.length > 0 ? $.parseJSON(callbackParams.text()) : {};
            
            //push data
            this.pushData = pushData.length > 0 ? $.parseJSON(pushData.text()) : null;

            //scripts to execute
            for(var i=0; i < scripts.length; i++) {
                $.globalEval(scripts.eq(i).text());
            }
        }
        
        //Handle push data
        if(this.pushData) {
            for(var channel in this.pushData) {
                if(channel) {
                    var message = JSON.stringify(this.pushData[channel].data);

                    PrimeFaces.websockets[channel].send(message);
                }
            }
        }
    }
};

PrimeFaces.ajax.AjaxRequest = function(cfg, ext) {
    PrimeFaces.debug('Initiating ajax request.');
    
    if(cfg.onstart) {
       var retVal = cfg.onstart.call(this);
       if(retVal == false) {
           PrimeFaces.debug('Ajax request cancelled by onstart callback.');
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
        form = $(PrimeFaces.escapeClientId(cfg.formId));                    //Explicit form is defined
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
    postParams = form.serialize(),
    encodedURLfield = form.children("input[name='javax.faces.encodedURL']");

    //portlet support
    var pForms = null;
    if(encodedURLfield.length > 0) {
        postURL = encodedURLfield.val();
        pForms = $('form[action="' + form.attr('action') + '"]');   //find forms of the portlet
    }
    
    PrimeFaces.debug('URL to post ' + postURL + '.');

    //partial ajax
    postParams = postParams + "&" + PrimeFaces.PARTIAL_REQUEST_PARAM + "=true";

    //source
    postParams = postParams + "&" + PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + sourceId;

    //process
    var process = [];
    if(cfg.process)
        process.push(cfg.process);
    if(ext && ext.process)
        process.push(ext.process);
    
    if(process.length > 0)
        postParams = postParams + "&" + PrimeFaces.PARTIAL_PROCESS_PARAM + "=" + process.join(' ');

    //update
    var update = [];
    if(cfg.update)
        update.push(cfg.update);
    if(ext && ext.update)
        update.push(ext.update);
    
    if(update.length > 0)
        postParams = postParams + "&" + PrimeFaces.PARTIAL_UPDATE_PARAM + "=" + update.join(' ');

    //behavior event
    if(cfg.event) {
        postParams = postParams + "&" + PrimeFaces.BEHAVIOR_EVENT_PARAM + "=" + cfg.event;
        var domEvent = cfg.event;

        if(cfg.event == 'valueChange')
            domEvent = 'change';
        else if(cfg.event == 'action')
            domEvent = 'click';

        postParams = postParams + "&" + PrimeFaces.PARTIAL_EVENT_PARAM + "=" + domEvent;
    } else {
        postParams = postParams + "&" + cfg.source + "=" + cfg.source;
    }
    
    //params
    if(cfg.params) {
        postParams = postParams + PrimeFaces.ajax.AjaxUtils.serialize(cfg.params);
    }
    if(ext && ext.params) {
        postParams = postParams + PrimeFaces.ajax.AjaxUtils.serialize(ext.params);
    }
    
    PrimeFaces.debug('Post Data:' + postParams);
	
    var xhrOptions = {
        url : postURL,
        type : "POST",
        cache : false,
        dataType : "xml",
        data : postParams,
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
            if(ext && ext.onsuccess && !parsed) {
                parsed = ext.onsuccess.call(this, data, status, xhr); 
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
            
            if(ext && ext.oncomplete) {
                ext.oncomplete.call(this, xhr, status, this.args);
            }
            
            PrimeFaces.debug('Response completed.');
            
            if(this.queued) {
                PrimeFaces.ajax.Queue.poll();
            }
        }
    };
	
    xhrOptions.global = cfg.global == true || cfg.global == undefined ? true : false;
    
    if(cfg.async) {
        $.ajax(xhrOptions);
    }
    else {
        PrimeFaces.ajax.Queue.offer(xhrOptions);
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
        request.queued = true;
        this.requests.push(request);
        
        if(this.requests.length == 1) {
            $.ajax(this.peek());
        }
    },
    
    poll : function() {
        if(this.isEmpty()) {
            return null;
        }
        
        var processed = this.requests.shift(),
        next = this.peek();
        
        if(next != null) {
            $.ajax(next);
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

/**
 * Utilities
 */
Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length);
  this.length = from < 0 ? this.length + from : from;
  return this.push.apply(this, rest);
};

String.prototype.startsWith = function(str){
    return (this.indexOf(str) === 0);
}

/**
 * Prime Push Widget
 */
PrimeFaces.widget.PrimeWebSocket = function(cfg) {
    this.cfg = cfg;

    if(this.cfg.autoConnect) {
        this.connect();
    }
}

PrimeFaces.widget.PrimeWebSocket.prototype.send = function(data) {
    this.ws.send(data);
}

PrimeFaces.widget.PrimeWebSocket.prototype.connect = function() {
    this.ws = $.browser.mozilla ? new MozWebSocket(this.cfg.url) : new WebSocket(this.cfg.url);

    var _self = this;

    this.ws.onmessage = function(evt) {
        var pushData = $.parseJSON(evt.data);

        if(_self.cfg.onmessage) {
            _self.cfg.onmessage.call(_self, evt, pushData);
        }
    };

    this.ws.onclose = function() {
        
    };
    
    this.ws.onerror = function(evt) {
        alert(evt.data);
    };

    PrimeFaces.websockets[this.cfg.channel] = this;
}

PrimeFaces.widget.PrimeWebSocket.prototype.close = function() {
    this.ws.close();
}