(function(window) {

    if(window.PrimeFaces) {
        window.PrimeFaces.debug("PrimeFaces already loaded, ignoring duplicate execution.");
        return;
    }

    var PrimeFaces = {

        escapeClientId : function(id) {
            return "#" + id.replace(/:/g,"\\:");
        },

        cleanWatermarks : function(){
            $.watermark.hideAll();
        },

        showWatermarks : function(){
            $.watermark.showAll();
        },

        getWidgetById : function(id) {

            for (var widgetVar in PrimeFaces.widgets) {
                var widget = PrimeFaces.widgets[widgetVar];
                if (widget && widget.id === id) {
                    return widget;
                }
            }

            return null;
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
        submit : function(formId, target) {
            var form = $(this.escapeClientId(formId));
            if(target) {
                form.attr('target', target);
            }

            form.submit().children('input.ui-submit-param').remove();
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

        setCookie : function(name, value, cfg) {
            $.cookie(name, value, cfg);
        },

        deleteCookie: function(name, cfg) {
            $.removeCookie(name, cfg);
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
            input.attr('role', 'textbox')
                    .attr('aria-disabled', input.is(':disabled'))
                    .attr('aria-readonly', input.prop('readonly'));

            if(input.is('textarea')) {
                input.attr('aria-multiline', true);
            }

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
            var role = button.attr('role');
            if(!role) {
                button.attr('role', 'button');
            }
            button.attr('aria-disabled', button.prop('disabled'));

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

        //Deprecated, use PrimeFaces.env.isIE instead
        isIE: function(version) {
            return PrimeFaces.env.isIE(version);
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

            if (PrimeFaces.isDevelopmentProjectStage() && window.console) {
                console.log(log);
            }
        },

        error: function(log) {
            if(this.logger) {
                this.logger.error(log);
            }

            if (PrimeFaces.isDevelopmentProjectStage() && window.console) {
                console.log(log);
            }
        },

        isDevelopmentProjectStage: function() {
            return PrimeFaces.settings.projectStage === 'Development';
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
            if(newTheme && newTheme !== '') {
                var themeLink = $('link[href*="javax.faces.resource/theme.css"]');
                // portlet
                if (themeLink.length === 0) {
                    themeLink = $('link[href*="javax.faces.resource=theme.css"]');
                }

                var themeURL = themeLink.attr('href'),
                    plainURL = themeURL.split('&')[0],
                    oldTheme = plainURL.split('ln=')[1],
                    newThemeURL = themeURL.replace(oldTheme, 'primefaces-' + newTheme);

                themeLink.attr('href', newThemeURL);
            }
        },

        escapeRegExp: function(text) {
            return this.escapeHTML(text.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1"));
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
            }
            else if(document.selection && document.selection.empty) {
                try {
                    document.selection.empty();
                } catch(error) {
                    //ignore IE bug
                }
            }
        },

        getSelection: function() {
            var text = '';
            if (window.getSelection) {
                text = window.getSelection();
            } else if (document.getSelection) {
                text = document.getSelection();
            } else if (document.selection) {
                text = document.selection.createRange().text;
            }

            return text;
        },

        hasSelection: function() {
            return this.getSelection().length > 0;
        },

        cw : function(widgetConstructor, widgetVar, cfg, resource) {
            PrimeFaces.createWidget(widgetConstructor, widgetVar, cfg, resource);
        },

        createWidget : function(widgetConstructor, widgetVar, cfg, resource) {
            cfg.widgetVar = widgetVar;

            if(PrimeFaces.widget[widgetConstructor]) {
                var widget = PrimeFaces.widgets[widgetVar];

                //ajax update
                if(widget && (widget.constructor === PrimeFaces.widget[widgetConstructor])) {
                    widget.refresh(cfg);
                }
                //page init
                else {
                    PrimeFaces.widgets[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
                    if(PrimeFaces.settings.legacyWidgetNamespace) {
                        window[widgetVar] = PrimeFaces.widgets[widgetVar];
                    }
                }
            }
            // widget script not loaded -> lazy load script + stylesheet
            else {
                var scriptURI = PrimeFaces.getFacesResource(resource + '/' + resource + '.js', 'primefaces');
                var cssURI = PrimeFaces.getFacesResource(resource + '/' + resource + '.css', 'primefaces');

                //load css
                var cssResource = '<link type="text/css" rel="stylesheet" href="' + cssURI + '" />';
                $('head').append(cssResource);

                //load script and initialize widget
                PrimeFaces.getScript(scriptURI, function() {
                    setTimeout(function() {
                        PrimeFaces.widgets[widgetVar] = new PrimeFaces.widget[widgetConstructor](cfg);
                    }, 100);
                });
            }
        },

        /**
         * Builds a resource URL for given parameters.
         *
         * @param {string} name The name of the resource. For example: primefaces.js
         * @param {string} library The library of the resource. For example: primefaces
         * @param {string} version The version of the library. For example: 5.1
         * @returns {string} The resource URL.
         */
        getFacesResource : function(name, library, version) {
            var scriptURI = $('script[src*="/javax.faces.resource/' + PrimeFaces.getCoreScriptName() + '"]').attr('src');
            // portlet
            if (!scriptURI) {
                scriptURI = $('script[src*="javax.faces.resource=' + PrimeFaces.getCoreScriptName() + '"]').attr('src');
            }

            scriptURI = scriptURI.replace(PrimeFaces.getCoreScriptName(), name);
            scriptURI = scriptURI.replace('ln=primefaces', 'ln=' + library);

            if (version) {
                var extractedVersion = new RegExp('[?&]v=([^&]*)').exec(scriptURI)[1];
                scriptURI = scriptURI.replace('v=' + extractedVersion, 'v=' + version);
            }

            var prefix = window.location.protocol + '//' + window.location.host;
            return scriptURI.indexOf(prefix) >= 0 ? scriptURI : prefix + scriptURI;
        },

        getCoreScriptName: function() {
            return 'primefaces.js';
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
            var selector = ':not(:submit):not(:button):input:visible:enabled[name]';

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

            // remember that a custom focus has been rendered
            // this avoids to retain the last focus after ajax update
            PrimeFaces.customFocus = true;
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
            if(item.length === 0) {
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
                if(PrimeFaces.env.browser.msie) {
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

        bcn: function(element, event, functions) {
            if(functions) {
                for(var i = 0; i < functions.length; i++) {
                    var retVal = functions[i].call(element, event);
                    if(retVal === false) {
                        if(event.preventDefault) {
                            event.preventDefault();
                        }
                        else {
                            event.returnValue = false;
                        }

                        break;
                    }
                }
            }
        },

        bcnu: function(ext, event, fns) {
            if(fns) {
                for(var i = 0; i < fns.length; i++) {
                    var retVal = fns[i].call(ext, event);
                    if(retVal === false) {
                        break;
                    }
                }
            }
        },

    	/**
    	 * moved to core.dialog.js
    	 */
        openDialog: function(cfg) {
        	PrimeFaces.dialog.DialogHandler.openDialog(cfg);
        },
        closeDialog: function(cfg) {
        	PrimeFaces.dialog.DialogHandler.closeDialog(cfg);
        },
        showMessageInDialog: function(msg) {
        	PrimeFaces.dialog.DialogHandler.showMessageInDialog(msg);
        },
        confirm: function(msg) {
        	PrimeFaces.dialog.DialogHandler.confirm(msg);
        },

        deferredRenders: [],

        addDeferredRender: function(widgetId, containerId, fn) {
            this.deferredRenders.push({widget: widgetId, container: containerId, callback: fn});
        },

        removeDeferredRenders: function(widgetId) {
            for(var i = (this.deferredRenders.length - 1); i >= 0; i--) {
                var deferredRender = this.deferredRenders[i];

                if(deferredRender.widget === widgetId) {
                    this.deferredRenders.splice(i, 1);
                }
            }
        },

        invokeDeferredRenders: function(containerId) {
            var widgetsToRemove = [];
            for(var i = 0; i < this.deferredRenders.length; i++) {
                var deferredRender = this.deferredRenders[i];

                if(deferredRender.container === containerId) {
                    var rendered = deferredRender.callback.call();
                    if(rendered) {
                        widgetsToRemove.push(deferredRender.widget);
                    }
                }
            }

            for(var j = 0; j < widgetsToRemove.length; j++) {
                this.removeDeferredRenders(widgetsToRemove[j]);
            }
        },

        zindex : 1000,

        customFocus : false,

        detachedWidgets : [],

        PARTIAL_REQUEST_PARAM : "javax.faces.partial.ajax",

        PARTIAL_UPDATE_PARAM : "javax.faces.partial.render",

        PARTIAL_PROCESS_PARAM : "javax.faces.partial.execute",

        PARTIAL_SOURCE_PARAM : "javax.faces.source",

        BEHAVIOR_EVENT_PARAM : "javax.faces.behavior.event",

        PARTIAL_EVENT_PARAM : "javax.faces.partial.event",

        RESET_VALUES_PARAM : "primefaces.resetvalues",

        IGNORE_AUTO_UPDATE_PARAM : "primefaces.ignoreautoupdate",

        VIEW_STATE : "javax.faces.ViewState",

        CLIENT_WINDOW : "javax.faces.ClientWindow",

        VIEW_ROOT : "javax.faces.ViewRoot",

        CLIENT_ID_DATA : "primefaces.clientid"
    };

    /**
     * PrimeFaces Namespaces
     */
    PrimeFaces.settings = {};
    PrimeFaces.util = {};
    PrimeFaces.widgets = {};

    /**
     * Locales
     */
    PrimeFaces.locales = {

        'en_US': {
            closeText: 'Close',
            prevText: 'Previous',
            nextText: 'Next',
            monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
            monthNamesShort: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ],
            dayNames: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
            dayNamesShort: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            dayNamesMin: ['S', 'M', 'T', 'W ', 'T', 'F ', 'S'],
            weekHeader: 'Week',
            firstDay: 0,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix:'',
            timeOnlyTitle: 'Only Time',
            timeText: 'Time',
            hourText: 'Hour',
            minuteText: 'Minute',
            secondText: 'Second',
            currentText: 'Current Date',
            ampm: false,
            month: 'Month',
            week: 'week',
            day: 'Day',
            allDayText: 'All Day'
        }

    };

    PF = function(widgetVar) {
    	var widgetInstance = PrimeFaces.widgets[widgetVar];

    	if (!widgetInstance) {
	        PrimeFaces.error("Widget for var '" + widgetVar + "' not available!");
    	}

        return widgetInstance;
    };

    //expose globally
    window.PrimeFaces = PrimeFaces;

})(window);
