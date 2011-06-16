PrimeFaces = {

    escapeClientId : function(id) {
        return "#" + id.replace(/:/g,"\\:");
    },
	
    onContentReady : function(id, fn) {
        YAHOO.util.Event.onContentReady(id, fn, window, true);
    },
	
    cleanWatermarks : function(){
        $.watermark.hideAll();
    },
	
    showWatermarks : function(){
        $.watermark.showAll();
    },
	
    addSubmitParam : function(parent, name, value) {
        $(this.escapeClientId(parent)).append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
	
        return this;
    },

    submit : function(formId) {
        $(this.escapeClientId(formId)).submit();
    },

    attachBehaviors : function(element, behaviors) {
        for(var event in behaviors) {
            var domEvent = event,
            handler = behaviors[domEvent];

            element.bind(domEvent, function(e) {
                handler.call(element, e);
            });
        }
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
    },

    //ajax shortcut
    ab : function(cfg, ext) {
        PrimeFaces.ajax.AjaxRequest(cfg, ext);
    },

    //mobile
    navigate : function(to, transition, reverse, changeHash) {
        $.mobile.changePage(to, transition, reverse, changeHash);
    },
    
    info: function(msg) {
        if(window.log) {
            log.info(msg);
        }
    },
    
    debug: function(msg) {
        if(window.log) {
            log.debug(msg);
        }
    },
    
    warn: function(msg) {
        if(window.log) {
            log.warn(msg);
        }
    },
    
    error: function(msg) {
        if(window.log) {
            log.error(msg);
        }
    },

    locales : {},
	
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

PrimeFaces.ajax.AjaxUtils = {
	
    encodeViewState : function() {
        var viewstateValue = document.getElementById(PrimeFaces.VIEW_STATE).value;
        var re = new RegExp("\\+", "g");
        var encodedViewState = viewstateValue.replace(re, "\%2B");
		
        return encodedViewState;
    },
	
    updateState: function(value) {
        var viewstateValue = $.trim(value);
        
        $("form").each(function() {
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
            PrimeFaces.ajax.AjaxUtils.updateState(content);
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
        var redirect = xmlDoc.getElementsByTagName("redirect"),
        extensions = xmlDoc.getElementsByTagName("extension"),
        scripts = xmlDoc.getElementsByTagName("eval");

        if(redirect.length > 0) {
            window.location = redirect[0].attributes.getNamedItem("url").nodeValue;
        }
        else {
            //callbsack arguments
            this.args = {};
            for(var i=0; i < extensions.length; i++) {
                var extension = extensions[i];

                if(extension.getAttributeNode('primefacesCallbackParam')) {
                    var jsonObj = $.parseJSON(extension.firstChild.data);

                    for(var paramName in jsonObj) {
                        if(paramName)
                            this.args[paramName] = jsonObj[paramName];
                    }
                }
            }

            //scripts to execute
            for(i=0; i < scripts.length; i++) {
                $.globalEval(scripts[i].firstChild.data);
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
    
    //block ui
    $('body').append('<div class="ui-block" style="background-color:transparent;margin:0;position:absolute;top:0;left:0;height:100%;width:100%;z-index:1000"></div>');

    var form = null;
    if(cfg.formId) {
        form = $(PrimeFaces.escapeClientId(cfg.formId));                    //Explicit form is defined
    }
    else {
        form = $(PrimeFaces.escapeClientId(cfg.source)).parents('form:first');     //look for a parent of source

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
    if(encodedURLfield.length > 0) {
        postURL = encodedURLfield.val();
    }
    
    PrimeFaces.debug('URL to post ' + postURL + '.');

    //partial ajax
    postParams = postParams + "&" + PrimeFaces.PARTIAL_REQUEST_PARAM + "=true";

    //source
    if(typeof cfg.source == 'string')
        postParams = postParams + "&" + PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + cfg.source;
    else
        postParams = postParams + "&" + PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + cfg.source.id;

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
            
            //unblock
            $('body').children('.ui-block').remove();
            
            PrimeFaces.debug('Response completed.');

            PrimeFaces.ajax.RequestManager.poll();
        }
    };
	
    xhrOptions.global = cfg.global == true || cfg.global == undefined ? true : false;

    if(cfg.async) {
        $.ajax(xhrOptions);
    } else {
        PrimeFaces.ajax.RequestManager.offer(xhrOptions);
    }
}

PrimeFaces.ajax.AjaxResponse = function(responseXML) {
    var xmlDoc = responseXML.documentElement,
    updates = xmlDoc.getElementsByTagName("update");

    for(var i=0; i < updates.length; i++) {
        var id = updates[i].attributes.getNamedItem("id").nodeValue,
        content = updates[i].firstChild.data;

        PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
    }

    PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
}

PrimeFaces.ajax.RequestManager = {
		
    requests : new Array(),

    offer : function(req) {
        this.requests.push(req);

        if(this.requests.length == 1) {
            var retVal = $.ajax(req);
            if(retVal === false)
                this.poll();
        }
    },

    poll : function() {
        if(this.isEmpty()) {
            return null;
        }
 
        var processedRequest = this.requests.shift();
        var nextRequest = this.peek();
        if(nextRequest != null) {
            $.ajax(nextRequest);
        }

        return processedRequest;
    },

    peek : function() {
        if(this.isEmpty()) {
            return null;
        }
    
        var nextRequest = this.requests[0];
  
        return nextRequest;
    },
    
    isEmpty : function() {
        return this.requests.length == 0;
    }
};

/* General Utilities */

/**
 * jQuery Cookie plugin
 *
 * Copyright (c) 2010 Klaus Hartl (stilbuero.de)
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 *
 */
jQuery.cookie = function (key, value, options) {

    // key and value given, set cookie...
    if (arguments.length > 1 && (value === null || typeof value !== "object")) {
        options = jQuery.extend({}, options);

        if (value === null) {
            options.expires = -1;
        }

        if (typeof options.expires === 'number') {
            var days = options.expires, t = options.expires = new Date();
            t.setDate(t.getDate() + days);
        }

        return (document.cookie = [
            encodeURIComponent(key), '=',
            options.raw ? String(value) : encodeURIComponent(String(value)),
            options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
            options.path ? '; path=' + options.path : '',
            options.domain ? '; domain=' + options.domain : '',
            options.secure ? '; secure' : ''
        ].join(''));
    }

    // key and possibly options given, get cookie...
    options = value || {};
    var result, decode = options.raw ? function (s) {return s;} : decodeURIComponent;
    return (result = new RegExp('(?:^|; )' + encodeURIComponent(key) + '=([^;]*)').exec(document.cookie)) ? decode(result[1]) : null;
};
