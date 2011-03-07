$(document).ready(function() {
    $('body').addClass('yui-skin-sam');
});

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
            handlers = behaviors[domEvent];

            for(var i in handlers) {
                var handler = handlers[i];

                element.bind(domEvent, function(e) {
                    handler.call(this, e);
                });
            }
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
    ab : function(cfg) {
        PrimeFaces.ajax.AjaxRequest(cfg);
    },
	
    PARTIAL_REQUEST_PARAM : "javax.faces.partial.ajax",

    PARTIAL_UPDATE_PARAM : "javax.faces.partial.render",

    PARTIAL_PROCESS_PARAM : "javax.faces.partial.execute",

    PARTIAL_SOURCE_PARAM : "javax.faces.source",

    BEHAVIOR_EVENT_PARAM : "javax.faces.behavior.event",

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
        $("form").each(function() {
            var form = $(this),
            formViewState = form.children("input[name='javax.faces.ViewState']").get(0);

            if(formViewState) {
                $(formViewState).val(value);
            }
            else
            {
                form.append('<input type="hidden" name="javax.faces.ViewState" id="javax.faces.ViewState" value="' + value + '" autocomplete="off" />');
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
        }
    }
};

PrimeFaces.ajax.AjaxRequest = function(cfg) {
    if(cfg.onstart) {
       var retVal = cfg.onstart.call(this);
       if(retVal == false) {
           return;  //cancel request
       }
    }

    var form = null;

    if(cfg.formId) {
        form = $(PrimeFaces.escapeClientId(cfg.formId));
    } else {
        form = $('form').eq(0);
    }
    
    var postURL = form.attr('action'),
    postParams = form.serialize(),
    encodedURLfield = form.children("input[name='javax.faces.encodedURL']");

    //portlet support
    if(encodedURLfield.length > 0) {
        postURL = encodedURLfield.val();
    }

    //partial ajax
    postParams = postParams + "&" + PrimeFaces.PARTIAL_REQUEST_PARAM + "=true";

    //source
    if(typeof cfg.source == 'string')
        postParams = postParams + "&" + PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + cfg.source;
    else
        postParams = postParams + "&" + PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + cfg.source.id;

    //process
    if(cfg.process) {
        postParams = postParams + "&" + PrimeFaces.PARTIAL_PROCESS_PARAM + "=" + cfg.process;
    }

    //update
    if(cfg.update) {
        postParams = postParams + "&" + PrimeFaces.PARTIAL_UPDATE_PARAM + "=" + cfg.update;
    }

    //behavior event
    if(cfg.event) {
        postParams = postParams + "&" + PrimeFaces.BEHAVIOR_EVENT_PARAM + "=" + cfg.event;
    } else {
        postParams = postParams + "&" + cfg.source + "=" + cfg.source;
    }
    
    //params
    if(cfg.params) {
        postParams = postParams + PrimeFaces.ajax.AjaxUtils.serialize(cfg.params);
    }
	
    var xhrOptions = {
        url : postURL,
        type : "POST",
        cache : false,
        dataType : "xml",
        data : postParams,
        beforeSend: function(xhr) {
           xhr.setRequestHeader('Faces-Request', 'partial/ajax');
        },
        success : function(data, status, xhr) {
            if(cfg.onsuccess) {
                var value = cfg.onsuccess.call(this, data, status, xhr);
                if(value === false)
                    return;
            }
		
            PrimeFaces.ajax.AjaxResponse.call(this, data, status, xhr);
        },
        complete : function(xhr, status) {
            if(cfg.oncomplete) {
                cfg.oncomplete.call(this, xhr, status, this.args);
            }

            PrimeFaces.ajax.RequestManager.poll();
        }
    };
	
    xhrOptions.global = cfg.global == true || cfg.global == undefined ? true : false;
	
    if(cfg.onerror) {
        xhrOptions.error = cfg.onerror;
    }

    if(cfg.async) {
        $.ajax(xhrOptions);
    } else {
        PrimeFaces.ajax.RequestManager.offer(xhrOptions);
    }
}

PrimeFaces.ajax.AjaxResponse = function(responseXML) {
    var xmlDoc = responseXML.documentElement,
    updates = xmlDoc.getElementsByTagName("update"),
    redirect = xmlDoc.getElementsByTagName("redirect"),
    extensions = xmlDoc.getElementsByTagName("extension");

    if(redirect.length > 0) {
        window.location = redirect[0].attributes.getNamedItem("url").nodeValue;
    } else {

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
        }
    }

    this.args = {};
    for(i=0; i < extensions.length; i++) {
        var extension = extensions[i];
        
        if(extension.getAttributeNode('primefacesCallbackParam')) {
            var jsonObj = $.parseJSON(extension.firstChild.data);

            for(var paramName in jsonObj) {
                if(paramName)
                    this.args[paramName] = jsonObj[paramName];
            }
        }
    }
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
