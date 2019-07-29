(function(window) {

    if(window.PrimeFaces) {
        window.PrimeFaces.debug("PrimeFaces already loaded, ignoring duplicate execution.");
        return;
    }

    var PrimeFaces = {

        escapeClientId : function(id) {
            return "#" + id.replace(/:/g,"\\:");
        },

        onElementLoad: function(element, listener) {
            if (element.prop('complete')) {
                listener();
            }
            else {
                element.on('load', listener);
            }
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
                form.append("<input type=\"hidden\" name=\"" + PrimeFaces.escapeHTML(key) + "\" value=\"" + PrimeFaces.escapeHTML(params[key]) + "\" class=\"ui-submit-param\"/>");
            }

            return this;
        },

        /**
         * Submits a form and clears ui-submit-param after that to prevent dom caching issues
         */
        submit : function(formId, target) {
            var form = $(this.escapeClientId(formId));
            var prevTarget;

            if (target) {
                prevTarget = form.attr('target');
                form.attr('target', target);
            }

            form.submit();
            form.children('input.ui-submit-param').remove();

            if (target) {
                if (prevTarget !== undefined) {
                    form.attr('target', prevTarget);
                } else {
                    form.removeAttr('target');
                }
            }
        },

        onPost : function() {
            this.nonAjaxPosted = true;
            this.abortXHRs();
        },

        abortXHRs : function() {
            PrimeFaces.ajax.Queue.abortAll();
        },

        attachBehaviors : function(element, behaviors) {
            $.each(behaviors, function(event, fn) {
                element.on(event, function(e) {
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

        /**
         * Updates the Input to add style whether it contains data or not. Used particularly in Floating Labels.
         * 
         * @param the text input to modify
         * @param the parent element of input
         */
        updateFilledState: function(input, parent) {
            var value = input.val();
            
            if (typeof(value) == 'undefined') {
                return;
            }
            
            if (value.length) {
                input.addClass('ui-state-filled');
                
                if(parent.is("span:not('.ui-float-label')")) {
                    parent.addClass('ui-inputwrapper-filled');
                }
            } else {
                input.removeClass('ui-state-filled');
                parent.removeClass('ui-inputwrapper-filled');
            }
        },

        skinInput : function(input) {
            var parent = input.parent(),
            updateFilledStateOnBlur = function () {
                if(parent.hasClass('ui-inputwrapper-focus')) {
                    parent.removeClass('ui-inputwrapper-focus');
                }
                PrimeFaces.updateFilledState(input, parent);
            };

            PrimeFaces.updateFilledState(input, parent);

            input.hover(
                function() {
                    $(this).addClass('ui-state-hover');
                },
                function() {
                    $(this).removeClass('ui-state-hover');
                }
            ).focus(function() {
                $(this).addClass('ui-state-focus');
                
                if(parent.is("span:not('.ui-float-label')")) {
                    parent.addClass('ui-inputwrapper-focus');
                }
            }).blur(function() {
                $(this).removeClass('ui-state-focus');
                
                if(input.hasClass('hasDatepicker')) {
                    setTimeout(function() {
                        updateFilledStateOnBlur();
                    }, 150);
                }
                else {
                    updateFilledStateOnBlur();
                }
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
                if(e.which === $.ui.keyCode.SPACE || e.which === $.ui.keyCode.ENTER) {
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
                console.error(log);
            }
        },

        isDevelopmentProjectStage: function() {
            return PrimeFaces.settings.projectStage === 'Development';
        },

        widgetNotAvailable: function(widgetVar) {
           PrimeFaces.error("Widget for var '" + widgetVar + "' not available!");
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
                var themeLink = $('link[href*="' + PrimeFaces.RESOURCE_IDENTIFIER + '/theme.css"]');
                // portlet
                if (themeLink.length === 0) {
                    themeLink = $('link[href*="' + PrimeFaces.RESOURCE_IDENTIFIER + '=theme.css"]');
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
            return String(value).replace(/[&<>"'`=\/]/g, function (s) {
                return PrimeFaces.entityMap[s];
            });
        },

        clearSelection: function() {
            if(window.getSelection) {
                if(window.getSelection().empty) {
                    window.getSelection().empty();
                } else if(window.getSelection().removeAllRanges && window.getSelection().rangeCount > 0 && window.getSelection().getRangeAt(0).getClientRects().length > 0) {
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

        cw : function(widgetName, widgetVar, cfg) {
            this.createWidget(widgetName, widgetVar, cfg);
        },


        /**
         * @deprecated moved to PrimeFaces.resources.getFacesResource
         */
        getFacesResource : function(name, library, version) {
           return PrimeFaces.resources.getFacesResource(name, library, version);
        },

        createWidget : function(widgetName, widgetVar, cfg) {
            cfg.widgetVar = widgetVar;

            if(this.widget[widgetName]) {
                var widget = this.widgets[widgetVar];

                //ajax update
                if(widget && (widget.constructor === this.widget[widgetName])) {
                    widget.refresh(cfg);
                }
                //page init
                else {
                    this.widgets[widgetVar] = new this.widget[widgetName](cfg);
                    if(this.settings.legacyWidgetNamespace) {
                        window[widgetVar] = this.widgets[widgetVar];
                    }
                }
            }
            // widget script not loaded
            else {
                // should be loaded by our dynamic resource handling, log a error
                PrimeFaces.widgetNotAvailable(widgetName);
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

        focus: function(id, context) {
            var selector = ':not(:submit):not(:button):input:visible:enabled[name]';

            setTimeout(function() {
                if(id) {
                    var jq = $(PrimeFaces.escapeClientId(id));

                    if(jq.is(selector)) {
                        jq.focus();
                    }
                    else {
                        var firstElement = jq.find(selector).eq(0);
                        PrimeFaces.focusElement(firstElement);
                    }
                }
                else if(context) {
                    var firstElement = $(PrimeFaces.escapeClientId(context)).find(selector).eq(0);
                    PrimeFaces.focusElement(firstElement);
                }
                else {
                    var elements = $(selector),
                    firstElement = elements.eq(0);
                    PrimeFaces.focusElement(firstElement);
                }
            }, 50);

            // remember that a custom focus has been rendered
            // this avoids to retain the last focus after ajax update
            PrimeFaces.customFocus = true;
        },

        focusElement: function(el) {
            if(el.is(':radio')) {
                // github issue: #2582
                if(el.hasClass('ui-helper-hidden-accessible')) {
                    el.parent().focus();
                }
                else {
                    var checkedRadio = $(':radio[name="' + el.attr('name') + '"]').filter(':checked');
                    if(checkedRadio.length)
                        checkedRadio.focus();
                    else
                        el.focus();
                }
            }
            else {
                el.focus();
            }
        },

        monitorDownload: function(start, complete, monitorKey) {
            if(this.cookiesEnabled()) {
                if(start) {
                    start();
                }

                var cookieName = monitorKey ? 'primefaces.download_' + monitorKey : 'primefaces.download';
                window.downloadMonitor = setInterval(function() {
                    var downloadComplete = PrimeFaces.getCookie(cookieName);

                    if(downloadComplete === 'true') {
                        if(complete) {
                            complete();
                        }
                        clearInterval(window.downloadMonitor);
                        PrimeFaces.setCookie(cookieName, null);
                    }
                }, 1000);
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
            if(item === null || item.length === 0) {
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
                var $div = $('<div />')
                    .css({ width: 100, height: 100, overflow: 'auto', position: 'absolute', top: -1000, left: -1000 })
                    .prependTo('body').append('<div />').find('div')
                        .css({ width: '100%', height: 200 });
                this.scrollbarWidth = 100 - $div.width();
                $div.parent().remove();
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
                    var retVal = fns[i].call(this, ext, event);
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

        getLocaleSettings: function() {
            if(!this.localeSettings) {
                var localeKey = PrimeFaces.settings.locale;
                this.localeSettings = PrimeFaces.locales[localeKey];

                if(!this.localeSettings) {
                    if(localeKey) {
                       this.localeSettings = PrimeFaces.locales[localeKey.split('_')[0]];
                    }

                    if(!this.localeSettings)
                        this.localeSettings = PrimeFaces.locales['en_US'];
                }
            }

            return this.localeSettings;
        },

        getAriaLabel: function(key) {
            var ariaLocaleSettings = this.getLocaleSettings()['aria'];
            return (ariaLocaleSettings&&ariaLocaleSettings[key]) ? ariaLocaleSettings[key] : PrimeFaces.locales['en_US']['aria'][key];
        },
        
        /**
         * Generate an RFC-4122 compliant UUID to be used to unique identifiers.
         * 
         * https://www.ietf.org/rfc/rfc4122.txt
         */
        uuid: function() {
            var lut = []; 
            for (var i=0; i<256; i++) { lut[i] = (i<16?'0':'')+(i).toString(16); }
            var d0 = Math.random()*0xffffffff|0;
            var d1 = Math.random()*0xffffffff|0;
            var d2 = Math.random()*0xffffffff|0;
            var d3 = Math.random()*0xffffffff|0;
            return lut[d0&0xff]+lut[d0>>8&0xff]+lut[d0>>16&0xff]+lut[d0>>24&0xff]+'-'+
              lut[d1&0xff]+lut[d1>>8&0xff]+'-'+lut[d1>>16&0x0f|0x40]+lut[d1>>24&0xff]+'-'+
              lut[d2&0x3f|0x80]+lut[d2>>8&0xff]+'-'+lut[d2>>16&0xff]+lut[d2>>24&0xff]+
              lut[d3&0xff]+lut[d3>>8&0xff]+lut[d3>>16&0xff]+lut[d3>>24&0xff];
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

        SKIP_CHILDREN_PARAM : "primefaces.skipchildren",

        VIEW_STATE : "javax.faces.ViewState",

        CLIENT_WINDOW : "javax.faces.ClientWindow",

        VIEW_ROOT : "javax.faces.ViewRoot",

        CLIENT_ID_DATA : "primefaces.clientid",

        RESOURCE_IDENTIFIER: 'javax.faces.resource',

        VERSION: '${project.version}'
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
            weekNumberTitle: 'W',
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
            week: 'Week',
            day: 'Day',
            allDayText: 'All Day',
            aria: {
                'paginator.PAGE': 'Page {0}',
                'calendar.BUTTON': 'Show Calendar',
                'datatable.sort.ASC': 'activate to sort column ascending',
                'datatable.sort.DESC': 'activate to sort column descending',
                'columntoggler.CLOSE': 'Close',
                'overlaypanel.CLOSE': 'Close'
            }
        }

    };

    PrimeFaces.locales['en'] = PrimeFaces.locales['en_US'];

    PrimeFaces.entityMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;',
        '/': '&#x2F;',
        '`': '&#x60;',
        '=': '&#x3D;'
    };

    PF = function(widgetVar) {
    	var widgetInstance = PrimeFaces.widgets[widgetVar];

    	if (!widgetInstance) {
	        PrimeFaces.widgetNotAvailable(widgetVar);
    	}

        return widgetInstance;
    };

    //expose globally
    window.PrimeFaces = PrimeFaces;

})(window);
