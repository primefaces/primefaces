(function(window) {
    
    if(window.PrimeFaces) {
        PrimeFaces.debug("PrimeFaces already loaded, ignoring duplicate execution.");
        return;
    }
    
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
                
        cookiesEnabled: function() {
            var cookieEnabled = (navigator.cookieEnabled) ? true : false;

            if(typeof navigator.cookieEnabled === 'undefined' && !cookieEnabled) { 
                document.cookie="testcookie";
                cookieEnabled = (document.cookie.indexOf("testcookie") !== -1) ? true : false;
            }
            
            return (cookieEnabled);
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
                if(!button.prop('disabled')) {
                    el.addClass('ui-state-hover');
                }
            }).mouseout(function() {
                $(this).removeClass('ui-state-active ui-state-hover');
            }).mousedown(function() {
                var el = $(this);
                if(!button.prop('disabled')) {
                    el.addClass('ui-state-active').removeClass('ui-state-hover');
                }
            }).mouseup(function() {
                $(this).removeClass('ui-state-active').addClass('ui-state-hover');
            }).focus(function() {
                $(this).addClass('ui-state-focus');
            }).blur(function() {
                $(this).removeClass('ui-state-focus ui-state-active');
            }).keydown(function(e) {
                if(e.keyCode === $.ui.keyCode.SPACE || e.keyCode === $.ui.keyCode.ENTER || e.keyCode === $.ui.keyCode.NUMPAD_ENTER) {
                    $(this).addClass('ui-state-active');
                }
            }).keyup(function() {
                $(this).removeClass('ui-state-active');
            });

            //aria
            button.attr('role', 'button').attr('aria-disabled', button.prop('disabled'));

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
            return PrimeFaces.ajax.AjaxRequest(cfg, ext);
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

        setCaretToEnd: function(element) {
            if(element) {
                element.focus();
                var length = element.value.length;

                if(length > 0) {
                    if(element.setSelectionRange) {
                        element.setSelectionRange(0, length);
                    } 
                    else if (element.createTextRange) {
                      var range = element.createTextRange();
                      range.collapse(true);
                      range.moveEnd('character', 1);
                      range.moveStart('character', 1);
                      range.select();
                    }
                }
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
                } else if(window.getSelection().removeAllRanges) {
                    window.getSelection().removeAllRanges();
                }
            } else if(document.selection && document.selection.empty) {
                    document.selection.empty();
            }
        },

        cw : function(widgetConstructor, widgetVar, cfg, resource) {
            PrimeFaces.createWidget(widgetConstructor, widgetVar, cfg, resource);
        },

        createWidget : function(widgetConstructor, widgetVar, cfg, resource) { 
            if(PrimeFaces.widget[widgetConstructor]) {
                if(PrimeFaces.widgets[widgetVar])
                    PrimeFaces.widgets[widgetVar].refresh(cfg);                                                    //ajax spdate
                else
                    PrimeFaces.widgets[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);  //page init
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
                        PrimeFaces.widgets[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
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
            if(this.cookiesEnabled()) {
                if(start) {
                    start();
                }

                window.downloadMonitor = setInterval(function() {
                    var downloadComplete = PrimeFaces.getCookie('primefaces.download');

                    if(downloadComplete === 'true') {
                        if(complete) {
                            complete();
                        }
                        clearInterval(window.downloadMonitor);
                        PrimeFaces.setCookie('primefaces.download', null);
                    }
                }, 250);
            }
        },

        /**
         *  Scrolls to a component with given client id
         */
        scrollTo: function(id) {
            var offset = $(PrimeFaces.escapeClientId(id)).offset();

            $('html,body').animate({
                scrollTop:offset.top
                ,
                scrollLeft:offset.left
            },{
                easing: 'easeInCirc'
            },1000);

        },

        /**
         *  Aligns container scrollbar to keep item in container viewport, algorithm copied from jquery-ui menu widget
         */
        scrollInView: function(container, item) { 
            if(item.length == 0) {
                return;
            }

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
        
        calculateScrollbarWidth: function() {
            if(!this.scrollbarWidth) {
                if($.browser.msie) {
                    var $textarea1 = $('<textarea cols="10" rows="2"></textarea>')
                            .css({ position: 'absolute', top: -1000, left: -1000 }).appendTo('body'),
                        $textarea2 = $('<textarea cols="10" rows="2" style="overflow: hidden;"></textarea>')
                            .css({ position: 'absolute', top: -1000, left: -1000 }).appendTo('body');
                    this.scrollbarWidth = $textarea1.width() - $textarea2.width();
                    $textarea1.add($textarea2).remove();
                } 
                else {
                    var $div = $('<div />')
                        .css({ width: 100, height: 100, overflow: 'auto', position: 'absolute', top: -1000, left: -1000 })
                        .prependTo('body').append('<div />').find('div')
                            .css({ width: '100%', height: 200 });
                    this.scrollbarWidth = 100 - $div.width();
                    $div.parent().remove();
                }
            }

            return this.scrollbarWidth;
        },
                
        openDialog: function(cfg) {
            var dialogId = cfg.sourceComponentId + '_dlg',
            dialogWidgetVar = cfg.sourceComponentId.replace(/:/g, '_') + '_dlgwidget',
            dialogDOM = $('<div id="' + dialogId + '" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-overlay-hidden"' + 
                    ' data-pfdlgcid="' + cfg.pfdlgcid + '" data-widgetvar="' + dialogWidgetVar + '"/>')
                    .append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top"><span class="ui-dialog-title"></span>' +
                    '<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a></div>' + 
                    '<div class="ui-dialog-content ui-widget-content" style="height: auto;">' +
                    '<iframe style="border:0 none"/>' + 
                    '</div>')
                    .appendTo(document.body),
            dialogFrame = dialogDOM.find('iframe'),
            symbol = cfg.url.indexOf('?') === -1 ? '?' : '&',
            frameURL = cfg.url + symbol + 'pfdlgcid=' + cfg.pfdlgcid,
            frameWidth = cfg.options.contentWidth||640,
            frameHeight = cfg.options.contentHeight||480;
    
            dialogFrame.css({
               width: frameWidth + 'px',
               height: frameHeight + 'px'
            });
    
            dialogFrame.on('load', function() {
                var $frame = $(this),
                titleElement = $frame.contents().find('title');
                
                if(!$frame.data('initialized')) {
                    PrimeFaces.cw('Dialog', dialogWidgetVar, {
                        id: dialogId,
                        position: 'center',
                        sourceComponentId: cfg.sourceComponentId,
                        sourceWidget: cfg.sourceWidget,
                        onHide: function() {
                            this.jq.remove();
                            window[dialogWidgetVar] = undefined;
                        },
                        modal: cfg.options.modal,
                        resizable: cfg.options.resizable,
                        draggable: cfg.options.draggable,
                        width: cfg.options.width,
                        height: cfg.options.height
                    });
                }
                
                if(titleElement.length > 0) {
                    PF(dialogWidgetVar).titlebar.children('span.ui-dialog-title').html(titleElement.text());
                }
                
                PF(dialogWidgetVar).show();
                
                dialogFrame.data('initialized', true);
            })
            .attr('src', frameURL);
        },

        closeDialog: function(cfg) {
            var dlg = $(parent.document.body).children('div.ui-dialog').filter(function() {
                return $(this).data('pfdlgcid') === cfg.pfdlgcid;
            }),
            dlgWidget = parent.PF(dlg.data('widgetvar')),
            sourceWidget = dlgWidget.cfg.sourceWidget;

            dlgWidget.hide();
            dlgWidget.jq.remove(); 
            
            if(sourceWidget && sourceWidget.cfg.behaviors) {
                var dialogReturnBehavior = sourceWidget.cfg.behaviors['dialogReturn'];

                if(dialogReturnBehavior) {
                    var ext = {
                            params: [
                                {name: dlgWidget.cfg.sourceComponentId + '_pfdlgcid', value: cfg.pfdlgcid}
                            ]
                        };

                    dialogReturnBehavior.call(this, null, ext);
                }
            }
        },
                
        showMessageInDialog: function(msg) {
            if(!this.messageDialog) {
                var messageDialogDOM = $('<div id="primefacesmessagedlg" class="ui-message-dialog ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-overlay-hidden"/>')
                            .append('<div class="ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top"><span class="ui-dialog-title"></span>' +
                            '<a class="ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all" href="#" role="button"><span class="ui-icon ui-icon-closethick"></span></a></div>' + 
                            '<div class="ui-dialog-content ui-widget-content" style="height: auto;"></div>')
                            .appendTo(document.body);

                PrimeFaces.cw('Dialog', 'primefacesmessagedialog', {
                    id: 'primefacesmessagedlg', 
                    modal:true,
                    draggable: false, 
                    resizable: false,
                    showEffect: 'fade',
                    hideEffect: 'fade'
                });
                this.messageDialog = PF('primefacesmessagedialog');
                this.messageDialog.titleContainer = this.messageDialog.titlebar.children('span.ui-dialog-title');
            }

            this.messageDialog.titleContainer.text(msg.summary);
            this.messageDialog.content.html('').append('<span class="ui-dialog-message ui-messages-' + msg.severity.split(' ')[0].toLowerCase() + '-icon" />').append(msg.detail);
            this.messageDialog.show();
        },
                
        confirm: function(msg) {
            if(PrimeFaces.confirmDialog) {
                PrimeFaces.confirmSource = $(PrimeFaces.escapeClientId(msg.source));
                PrimeFaces.confirmDialog.showMessage(msg);
            }
            else {
                PrimeFaces.warn('No global confirmation dialog available.');
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
        
        RESET_VALUES_PARAM : "primefaces.resetvalues",
        
        IGNORE_AUTO_UPDATE_PARAM : "primefaces.ignoreautoupdate",

        VIEW_STATE : "javax.faces.ViewState",

        VIEW_ROOT : "javax.faces.ViewRoot",

        CLIENT_ID_DATA : "primefaces.clientid"
    };

    PrimeFaces.Behavior = {
    		
    	chain : function(source, event, ext, behaviorsArray) {

    		for (var i = 0; i < behaviorsArray.length; ++i) {
    			var behavior = behaviorsArray[i];
    			var success;

	            if (typeof behavior == 'function') {
	            	success = behavior.call(source, event, ext);
	            } else {	                
	            	if (!ext) {
	            		ext = { };
	            	}

	            	//either a function or a string can be passed in case of a string we have to wrap it into another function
	            	success = new Function("event", behavior).call(source, event, ext);
	            }

	            if (success === false) {
	                return false;
	            }
    		}
    		
    		return true;
    	}
    };
    
    PrimeFaces.Expressions = {

    	resolveComponentsAsSelector: function(expressions) {

            var splittedExpressions = PrimeFaces.Expressions.splitExpressions(expressions);
            var elements = $();

            if (splittedExpressions) {
                for (var i = 0; i < splittedExpressions.length; ++i) {
                    var expression =  $.trim(splittedExpressions[i]);
                    if (expression.length > 0) {

                    	// skip unresolvable keywords
                    	if (expression == '@none' || expression == '@all') {
                    		continue;
                    	}
                    	
                        // just a id
                        if (expression.indexOf("@") == -1) {
                        	elements = elements.add(
                            		$(document.getElementById(expression)));
                        }
                        // @widget
                        else if (expression.indexOf("@widgetVar(") == 0) {
                            var widgetVar = expression.substring(11, expression.length - 1);
                            var widget = PrimeFaces.widgets[widgetVar];

                            if (widget) {
                            	elements = elements.add(
                                		$(document.getElementById(widget.id)));
                            } else {
                                PrimeFaces.error("Widget for widgetVar \"" + widgetVar + "\" not avaiable");
                            }
                        }
                        // PFS
                        else if (expression.indexOf("@(") == 0) {
                            //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
							elements = elements.add(
                        			$(expression.substring(2, expression.length - 1)));
                        }
                    }
                }
            }

            return elements;
    	},

        resolveComponents: function(expressions) {
            var splittedExpressions = PrimeFaces.Expressions.splitExpressions(expressions);
            var ids = [];
            
            if (splittedExpressions) {
                for (var i = 0; i < splittedExpressions.length; ++i) {
                    var expression =  $.trim(splittedExpressions[i]);
                    if (expression.length > 0) {

                        // just a id or passtrough keywords
                        if (expression.indexOf("@") == -1 || expression == '@none' || expression == '@all') {
                            if (!PrimeFaces.inArray(ids, expression)) {
                                ids.push(expression);
                            }
                        }
                        // @widget
                        else if (expression.indexOf("@widgetVar(") == 0) {
                            var widgetVar = expression.substring(11, expression.length - 1);
                            var widget = PrimeFaces.widgets[widgetVar];

                            if (widget) {
                                if (!PrimeFaces.inArray(ids, widget.id)) {
                                    ids.push(widget.id);
                                }
                            } else {
                                PrimeFaces.error("Widget for widgetVar \"" + widgetVar + "\" not avaiable");
                            }
                        }
                        // PFS
                        else if (expression.indexOf("@(") == 0) {
                            //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
                            var elements = $(expression.substring(2, expression.length - 1));

                            for (var i = 0; i < elements.length; i++) {
                                var element = $(elements[i]);
                                var clientId = element.data(PrimeFaces.CLIENT_ID_DATA) || element.attr('id');

                                if (!PrimeFaces.inArray(ids, clientId)) {
                                    ids.push(clientId);
                                }
                            }
                        }
                    }
                }
            }

            return ids;
        },
        
        splitExpressions: function(value) {

    		var expressions = [];
    		var buffer = '';

    		var parenthesesCounter = 0;

    		for (var i = 0; i < value.length; i++) {
    			var c = value[i];

    			if (c == '(') {
    				parenthesesCounter++;
    			}

    			if (c == ')') {
    				parenthesesCounter--;
    			}

    			if ((c == ' ' || c == ',') && parenthesesCounter == 0) {
					// lets add token inside buffer to our tokens
    				expressions.push(buffer);
					// now we need to clear buffer
    				buffer = '';
    			} else {
    				buffer += c;
    			}
    		}

    		// lets not forget about part after the separator
    		expressions.push(buffer);

    		return expressions;
        }	
    };
    
    /**
     * PrimeFaces Namespaces
     */
    PrimeFaces.ajax = {};
    PrimeFaces.widget = {};
    PrimeFaces.settings = {};
    PrimeFaces.util = {};
    PrimeFaces.widgets = {};
    
    PF = function(widgetVar) {
        return PrimeFaces.widgets[widgetVar];
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
            forms = this.portletForms ? $(this.portletForms) : $('form');

            forms.each(function() {
                var form = $(this),
                formViewStateElement = form.children("input[name='javax.faces.ViewState']").get(0);

                if(formViewStateElement) {
                    $(formViewStateElement).val(viewstateValue);
                }
                else
                {
                    form.append('<input type="hidden" name="javax.faces.ViewState" value="' + viewstateValue + '" autocomplete="off" />');
                }
            });
        },

        updateElement: function(id, content) {        
            if(id.indexOf(PrimeFaces.VIEW_STATE) !== -1) {
                PrimeFaces.ajax.AjaxUtils.updateState.call(this, content);
            }
            else if(id === PrimeFaces.VIEW_ROOT) {
            	$.ajaxSetup({'cache' : true});
                $('head').html(content.substring(content.indexOf("<head>") + 6, content.lastIndexOf("</head>")));
                $.ajaxSetup({'cache' : false});

                var bodyStartTag = new RegExp("<body[^>]*>", "gi").exec(content)[0];
                var bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
                $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
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

        /**
         * Type: update/process
         */
        resolveComponentsForAjaxCall: function(cfg, type) {

            var expressions = '';
            
            if (cfg[type]) {
                expressions += cfg[type];
            }

            if (cfg.ext && cfg.ext[type]) {
                expressions += " " + cfg.ext[type];
            }
            
            return PrimeFaces.Expressions.resolveComponents(expressions);
        },

        send: function(cfg) {
            PrimeFaces.debug('Initiating ajax request.');
            
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
            
            if(global) {
                $(document).trigger('pfAjaxStart');
            }
            
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
            var pFormsSelector = null;
            if(encodedURLfield.length > 0) {
                pFormsSelector = 'form[action="' + postURL + '"]';
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
            var processArray = PrimeFaces.ajax.AjaxUtils.resolveComponentsForAjaxCall(cfg, 'process');
            if(cfg.fragmentId) {
                processArray.push(cfg.fragmentId);
            }
            var processIds = processArray.length > 0 ? processArray.join(' ') : '@all';
            if (processIds != '@none') {
	            postParams.push({
	                name:PrimeFaces.PARTIAL_PROCESS_PARAM, 
	                value:processIds
	            });
            }

            //update
            var updateArray = PrimeFaces.ajax.AjaxUtils.resolveComponentsForAjaxCall(cfg, 'update');
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

                if(cfg.event == 'valueChange')
                    domEvent = 'change';
                else if(cfg.event == 'action')
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
            if(cfg.partialSubmit && processIds.indexOf('@all') == -1) {
            	var hasViewstate = false;

                if(processIds.indexOf('@none') == -1) {
                	for (var i = 0; i < processArray.length; i++) {
                        var jqProcess = $(PrimeFaces.escapeClientId(processArray[i]));
                        var componentPostParams = null;

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
                    }
                }

                //add viewstate if necessary
                if(!hasViewstate) {
                    postParams.push({
                        name:PrimeFaces.VIEW_STATE, 
                        value:form.children("input[name='javax.faces.ViewState']").val()
                    });
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
                portletForms: pFormsSelector,
                source: cfg.source,
                global: false,
                beforeSend: function(xhr) {
                    xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                    
                    if(global) {
                        $(document).trigger('pfAjaxSend', [xhr, this]);
                    }       
                },
                error: function(xhr, status, errorThrown) {                    
                    if(cfg.onerror) {
                        cfg.onerror.call(this, [xhr, status, errorThrown]);
                    }
                    
                    if(global) {
                        $(document).trigger('pfAjaxError', xhr, this, errorThrown);
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
                        PrimeFaces.ajax.AjaxResponse.call(this, data, status, xhr);
                    }
                    
                    PrimeFaces.debug('DOM is updated.');
                },
                complete: function(xhr, status) {
                    if(cfg.oncomplete) {
                        cfg.oncomplete.call(this, xhr, status, this.args);
                    }

                    if(cfg.ext && cfg.ext.oncomplete) {
                        cfg.ext.oncomplete.call(this, xhr, status, this.args);
                    }
                    
                    if(global) {
                        $(document).trigger('pfAjaxComplete', [xhr, this]);
                    }
                    
                    PrimeFaces.debug('Response completed.');

                    if(!cfg.async) {
                        PrimeFaces.ajax.Queue.poll();
                    }
                }
            };
            
            $.ajax(xhrOptions);
        }
    };

    PrimeFaces.ajax.AjaxRequest = function(cfg, ext) {
        cfg.ext = ext;

        if(cfg.async) {
            return PrimeFaces.ajax.AjaxUtils.send(cfg);
        }
        else {
            return PrimeFaces.ajax.Queue.offer(cfg);
        }
    }

    PrimeFaces.ajax.AjaxResponse = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find('update');

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.get(0).childNodes[0].nodeValue;

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

    /* Simple JavaScript Inheritance
     * By John Resig http://ejohn.org/
     * MIT Licensed.
     */
    // Inspired by base2 and Prototype
    (function(){
      var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;
      // The base Class implementation (does nothing)
      this.Class = function(){};

      // Create a new Class that inherits from this class
      Class.extend = function(prop) {
        var _super = this.prototype;

        // Instantiate a base class (but only create the instance,
        // don't run the init constructor)
        initializing = true;
        var prototype = new this();
        initializing = false;

        // Copy the properties over onto the new prototype
        for (var name in prop) {
          // Check if we're overwriting an existing function
          prototype[name] = typeof prop[name] == "function" && 
            typeof _super[name] == "function" && fnTest.test(prop[name]) ?
            (function(name, fn){
              return function() {
                var tmp = this._super;

                // Add a new ._super() method that is the same method
                // but on the super-class
                this._super = _super[name];

                // The method only need to be bound temporarily, so we
                // remove it when we're done executing
                var ret = fn.apply(this, arguments);        
                this._super = tmp;

                return ret;
              };
            })(name, prop[name]) :
            prop[name];
        }

        // The dummy class constructor
        function Class() {
          // All construction is actually done in the init method
          if ( !initializing && this.init )
            this.init.apply(this, arguments);
        }

        // Populate our constructed prototype object
        Class.prototype = prototype;

        // Enforce the constructor to be what we expect
        Class.prototype.constructor = Class;

        // And make this class extendable
        Class.extend = arguments.callee;

        return Class;
      };
    })();

    /**
     * BaseWidget for PrimeFaces Widgets
     */
    PrimeFaces.widget.BaseWidget = Class.extend({

        init: function(cfg) {
            this.cfg = cfg;
            this.id = cfg.id;
            this.jqId = PrimeFaces.escapeClientId(this.id);
            this.jq = $(this.jqId);

            //remove script tag
            $(this.jqId + '_s').remove();
        },

        //used in ajax updates, reloads the widget configuration
        refresh: function(cfg) {
            return this.init(cfg);
        },

        //returns jquery object representing the main dom element related to the widget
        getJQ: function(){
            return this.jq;
        }

    });
    
    /**
     * Widgets that require to be visible to initialize properly for hidden container support
     */
    PrimeFaces.widget.DeferredWidget = PrimeFaces.widget.BaseWidget.extend({

        renderDeferred: function() {     
            if(this.jq.is(':visible')) {
                this._render();
            }
            else {
                var hiddenParent = this.jq.closest('.ui-hidden-container'),
                hiddenParentWidgetVar = hiddenParent.data('widget'),
                $this = this;

                if(hiddenParentWidgetVar) {
                    var hiddenParentWidget = PF(hiddenParentWidgetVar);
                    
                    if(hiddenParentWidget) {
                        hiddenParentWidget.addOnshowHandler(function() {
                            return $this.render();
                        });
                    }
                }
            }
        },
        
        render: function() {
            if(this.jq.is(':visible')) {
                this._render();
                return true;
            }
            else {
                return false;
            }
        },
        
        _render: function() {
            throw 'Unsupported Operation';
        }
    });
    
    //expose globally
    window.PrimeFaces = PrimeFaces;

})(window);