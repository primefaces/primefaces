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
        });
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
        var sel = window.getSelection ? window.getSelection() : document.selection;
        if(sel) {
            if(sel.removeAllRanges)
                sel.removeAllRanges();
            else if(sel.empty)
                sel.empty();
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
            $.getScript(location.protocol + '//' + location.host + scriptURI, function() {
                setTimeout(function() {
                    window[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
                }, 100);
            });
        }
    },

    isNumber: function(value) {
        return typeof value === 'number' && isFinite(value);
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
    var pForms = null;
    if(encodedURLfield.length > 0) {
        postURL = encodedURLfield.val();
        pForms = $('form[action="' + form.attr('action') + '"]');   //find forms of the portlet
    }
    
    PrimeFaces.debug('URL to post ' + postURL + '.');

    //partial ajax
    postParams = postParams + "&" + PrimeFaces.PARTIAL_REQUEST_PARAM + "=true";

    //source
    postParams = postParams + "&" + PrimeFaces.PARTIAL_SOURCE_PARAM + "=" + cfg.source;

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

/*jQuery MouseWheel Plugin
 *
 *! Copyright (c) 2009 Brandon Aaron (http://brandonaaron.net)
 * Dual licensed under the MIT (http://www.opensource.org/licenses/mit-license.php)
 * and GPL (http://www.opensource.org/licenses/gpl-license.php) licenses.
 * Thanks to: http://adomas.org/javascript-mouse-wheel/ for some pointers.
 * Thanks to: Mathias Bank(http://www.mathias-bank.de) for a scope bug fix.
 *
 * Version: 3.0.2
 * 
 * Requires: 1.2.2+
 */

(function($) {

    var types = ['DOMMouseScroll', 'mousewheel'];

    $.event.special.mousewheel = {
        setup: function() {
            if ( this.addEventListener )
                for ( var i=types.length; i; )
                    this.addEventListener( types[--i], handler, false );
            else
                this.onmousewheel = handler;
        },

        teardown: function() {
            if ( this.removeEventListener )
                for ( var i=types.length; i; )
                    this.removeEventListener( types[--i], handler, false );
            else
                this.onmousewheel = null;
        }
    };

    $.fn.extend({
        mousewheel: function(fn) {
            return fn ? this.bind("mousewheel", fn) : this.trigger("mousewheel");
        },

        unmousewheel: function(fn) {
            return this.unbind("mousewheel", fn);
        }
    });


    function handler(event) {
        var args = [].slice.call( arguments, 1 ), delta = 0, returnValue = true;

        event = $.event.fix(event || window.event);
        event.type = "mousewheel";

        if ( event.wheelDelta ) delta = event.wheelDelta/120;
        if ( event.detail     ) delta = -event.detail/3;

        // Add events and delta to the front of the arguments
        args.unshift(event, delta);

        return $.event.handle.apply(this, args);
    }

})(jQuery);

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