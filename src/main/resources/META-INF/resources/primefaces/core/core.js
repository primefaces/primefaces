(function(window) {

    if(window.PrimeFaces) {
        window.PrimeFaces.debug("PrimeFaces already loaded, ignoring duplicate execution.");
        return;
    }

    /**
     * This is the main global object for accessing the client-side API of PrimeFaces. Broadly speaking, it consists
     * of the following entries:
     *
     * - {@link PrimeFaces.ajax} The AJAX module with functionality for sending AJAX requests
     * - {@link PrimeFaces.clientwindow} The client window module for multiple window support in PrimeFaces applications.
     * - {@link PrimeFaces.csp} The  CSP module for the HTTP Content-Security-Policy (CSP) policy `script-src` directive.
     * - {@link PrimeFaces.dialog} The dialog module with functionality related to the dialog framework
     * - {@link PrimeFaces.env} The environment module with information about the current browser
     * - {@link PrimeFaces.expressions} The search expressions module with functionality for working with search expression
     * - {@link PrimeFaces.resources} The resources module with functionality for creating resource links
     * - {@link PrimeFaces.utils} The utility module with functionality that does not fit anywhere else
     * - {@link PrimeFaces.widget} The registry with all available widget classes
     * - {@link PrimeFaces.widgets} The registry with all currently instantiated widgets
     * - Several other utility methods defined directly on the `PrimeFaces` object, such as
     * {@link PrimeFaces.monitorDownload}, {@link PrimeFaces.getWidgetById}, or {@link PrimeFaces.escapeHTML}.
     *
     * @namespace {PrimeFaces}
     *
     * @interface {PrimeFaces.DeferredRender} DeferredRender Represents a deferred render added for a deferred widget.
     * Some widgets need to compute their dimensions based on their parent element(s). This requires that such widgets
     * are not rendered until they have become visible. A widget may not be visible, for example, when it is inside a
     * tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for widgets to render
     * once they are visible. This is done by keeping a list of widgets that need to be rendered, and checking on every
     * change (AJAX request, tab change etc.) whether any of those have become visible. A widgets should extend
     * `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
     * @prop {string} DeferredRender.widget The ID of a deferred widget.
     * @prop {string} DeferredRender.container ID of the container that should be visible before the widget can be rendered.
     * @method DeferredRender.callback Callback that is invoked when the widget _may_ possibly have become visible.
     * Checks whether the widget can be rendered and if so, renders it.
     * @return {boolean} DeferredRender.callback `true` when the widget was rendered, or `false` when the widget still
     * needs to be rendered later.
     */
    var PrimeFaces = {

        /**
         * Creates an ID to a CSS ID selector that matches elements with that ID. For example:
         * ```
         * PrimeFaces.escapeClientId("form:input"); // => "#form\:input"
         * PrimeFaces.escapeClientId("form#input"); // => "#form#input"
         * ```
         *
         * __Please note that this method does not escape all characters that need to be escaped and will not work with arbitrary IDs__
         * @param {string} id ID to convert.
         * @return {string} A CSS ID selector for the given ID.
         */
        escapeClientId : function(id) {
            return "#" + id.replace(/:/g,"\\:");
        },

        /**
         * Registeres a listener that will be called as soon as the given element was loaded completely. Please note the
         * listener may be called synchronously (immediately) or asynchronously, depending on whether the element is
         * already loaded.
         * @param {JQuery} element Element to wait for
         * @param {() => void} listener Listener to call once the element is loaded
         */
        onElementLoad: function(element, listener) {
            if (element.prop('complete')) {
                listener();
            }
            else {
                element.on('load', listener);
            }
        },

        /**
         * Finds a widget in the current page with the given ID.
         * @param {string} id ID of the widget to retrieve.
         * @return {PrimeFaces.widget.BaseWidget | null} The widget with the given ID, of `null` if no such widget was
         * found.
         */
        getWidgetById : function(id) {
            for (var widgetVar in PrimeFaces.widgets) {
                var widget = PrimeFaces.widgets[widgetVar];
                if (widget && widget.id === id) {
                    return widget;
                }
            }

            return null;
        },

        /**
         * Finds all widgets in the current page that are of the given type.
         * @template {new(...args: any[]) => any} TWidget Type of the widgets of interest, e.g.
         * `PrimeFaces.widget.DataTable`.
         * @param {TWidget} type The (proto)type of the widgets of interest, e.g. `PrimeFaces.widget.DataTable`.
         * @return  {InstanceType<TWidget>[]} An array of widgets that are of the requested type. If no suitable widgets
         * are found on the current page, an empty array will be returned.
         */
        getWidgetsByType: function(type) {
            return $.map(this.widgets, function(widget, key) {
                return type.prototype.isPrototypeOf(widget) ? widget : null;
            });
        },

        /**
         * Adds hidden input elements to the given form. For each key-value pair, a new hidden input element is created
         * with the given value and the key used as the name.
         * @param {string} parent The ID of a FORM element.
         * @param {Record<string, string>} params An object with key-value pairs.
         * @return {typeof PrimeFaces} This object for chaining.
         */
        addSubmitParam : function(parent, params) {
            var form = $(this.escapeClientId(parent));

            for(var key in params) {
                form.append("<input type=\"hidden\" name=\"" + PrimeFaces.escapeHTML(key) + "\" value=\"" + PrimeFaces.escapeHTML(params[key]) + "\" class=\"ui-submit-param\"></input>");
            }

            return this;
        },

        /**
         * Submits the given form, and clears all `ui-submit-param`s after that to prevent dom caching issues.
         *
         * If a target is given, it is set on the form temporarily before it is submitted. Afterwards, the original
         * target attribute of the form is restored.
         * @param {string} formId ID of the FORM element.
         * @param {string} [target] The target attribute to use on the form during the submit process.
         */
        submit : function(formId, target) {
            var form = $(this.escapeClientId(formId));
            var prevTarget;

            if (target) {
                prevTarget = form.attr('target');
                form.attr('target', target);
            }

            form.trigger('submit');
            form.children('input.ui-submit-param').remove();

            if (target) {
                if (prevTarget !== undefined) {
                    form.attr('target', prevTarget);
                } else {
                    form.removeAttr('target');
                }
            }
        },

        /**
         * Aborts all pending AJAX requests. This includes both requests that were already sent but did not receive a
         * response yet, as well as requests that are waiting in the queue and have not been sent yet.
         */
        abortXHRs : function() {
            PrimeFaces.ajax.Queue.abortAll();
        },

        /**
         * Attaches the given behaviors to the element. For each behavior, an event listener is registered on the
         * element. Then, when the event is triggered, the behavior callback is invoked.
         * @param {JQuery} element The element for which to attach the behaviors.
         * @param {Record<string, (this: JQuery, event: JQuery.TriggeredEvent) => void>} behaviors An object with an event name
         * as the key and event handlers for that event as the value. Each event handler is called with the given
         * element as the this context and the event that occurred as the first argument.
         */
        attachBehaviors : function(element, behaviors) {
            $.each(behaviors, function(event, fn) {
                element.on(event, function(e) {
                    fn.call(element, e);
                });
            });
        },

        /**
         * Fetches the value of a cookie by its name
         * @param {string} name Name of a cookie
         * @return {string | undefined} The value of the given cookie, or `undefined` if no such cookie exists
         */
        getCookie : function(name) {
            return Cookies.get(name);
        },

        /**
         * Sets the value of a given cookie.
         * It will set secure=true, if using HTTPS and session-config/cookie-config/secure is set to true in web.xml.
         * It will set sameSite, if secure=true, with the value of the primefaces.COOKIES_SAME_SITE parameter.
         * @param {string} name Name of the cookie to set
         * @param {string} value Value to set
         * @param {Partial<Cookies.CookieAttributes>} [cfg] Configuration for this cookie: when it expires, its
         * paths and domain and whether it is secure cookie.
         */
        setCookie : function(name, value, cfg) {
            if (location.protocol === 'https:' && PrimeFaces.settings.cookiesSecure) {
                cfg.secure = true;

                if (PrimeFaces.settings.cookiesSameSite) {
                    cfg.sameSite = PrimeFaces.settings.cookiesSameSite;
                }
            }
            Cookies.set(name, value, cfg);
        },

        /**
         * Deletes the given cookie.
         * @param {string} name Name of the cookie to delete
         * @param {Partial<Cookies.CookieAttributes>} [cfg] The cookie configuration used to set the cookie.
         */
        deleteCookie: function(name, cfg) {
            Cookies.remove(name, cfg);
        },

        /**
         * Checks whether cookies are enabled in the current browser.
         * @return {boolean} `true` if cookies are enabled and can be used, `false` otherwise.
         */
        cookiesEnabled: function() {
            var cookieEnabled = (navigator.cookieEnabled) ? true : false;

            if(typeof navigator.cookieEnabled === 'undefined' && !cookieEnabled) {
                document.cookie="testcookie";
                cookieEnabled = (document.cookie.indexOf("testcookie") !== -1) ? true : false;
            }

            return (cookieEnabled);
        },

        /**
         * Generates a unique key for using in HTML5 local storage by combining the context, view, id, and key.
         * @param {string} id ID of the component
         * @param {string} key a unique key name such as the component name
         * @return {string} the generated key comprising of context + view + id + key
         */
        createStorageKey : function(id, key) {
            var sk = PrimeFaces.settings.contextPath.replace(/\//g, '-')
                    + PrimeFaces.settings.viewId.replace(/\//g, '-')
                    + id + '-'
                    + key;
            return sk.toLowerCase();
        },

        /**
         * Updates the class of the given INPUT element to indicate whether the element contains data or not. Used for
         * example in floating labels.
         * @param {JQuery} input The text input to modify
         * @param {JQuery} parent The parent element of the input.
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

        /**
         * INPUT elements may have different states, such as `hovering` or `focused`. For each state, there is a
         * corresponding style class that is added to the input when it is in that state, such as `ui-state-hover` or
         * `ui-state-focus`. These classes are used by CSS rules for styling. This method sets up an input element so
         * that the classes are added correctly (by adding event listeners).
         * @param {JQuery} input INPUT element to skin
         * @return {typeof PrimeFaces} this for chaining
         */
        skinInput : function(input) {
            var parent = input.parent(),
            updateFilledStateOnBlur = function () {
                if(parent.hasClass('ui-inputwrapper-focus')) {
                    parent.removeClass('ui-inputwrapper-focus');
                }
                PrimeFaces.updateFilledState(input, parent);
            };

            PrimeFaces.updateFilledState(input, parent);

            input.on("mouseenter", function() {
                $(this).addClass('ui-state-hover');
            }).on("mouseleave", function() {
                $(this).removeClass('ui-state-hover');
            }).on("focus", function() {
                $(this).addClass('ui-state-focus');

                if(parent.is("span:not('.ui-float-label')")) {
                    parent.addClass('ui-inputwrapper-focus');
                }
            }).on("blur", function() {
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
            if(input.is(':not([type="password"])')) {
                input.attr('role', 'textbox')
                     .attr('aria-readonly', input.prop('readonly'));
            }
            input.attr('aria-disabled', input.is(':disabled'));

            if(input.is('textarea')) {
                input.attr('aria-multiline', true);
            }

            return this;
        },

        /**
         * BUTTON elements may have different states, such as `hovering` or `focused`. For each state, there is a
         * corresponding style class that is added to the button when it is in that state, such as `ui-state-hover` or
         * `ui-state-focus`. These classes are used by CSS rules for styling. This method sets up a button element so
         * that the classes are added correctly (by adding event listeners).
         * @param {JQuery} button BUTTON element to skin
         * @return {typeof PrimeFaces} this for chaining
         */
        skinButton : function(button) {
            button.on("mouseover", function(){
                var el = $(this);
                if(!button.prop('disabled')) {
                    el.addClass('ui-state-hover');
                }
            }).on("mouseout", function() {
                $(this).removeClass('ui-state-active ui-state-hover');
            }).on("mousedown", function() {
                var el = $(this);
                if(!button.prop('disabled')) {
                    el.addClass('ui-state-active').removeClass('ui-state-hover');
                }
            }).on("mouseup", function() {
                $(this).removeClass('ui-state-active').addClass('ui-state-hover');
            }).on("focus", function() {
                $(this).addClass('ui-state-focus');
            }).on("blur", function() {
                $(this).removeClass('ui-state-focus ui-state-active');
            }).on("keydown", function(e) {
                if(e.which === $.ui.keyCode.SPACE || e.which === $.ui.keyCode.ENTER) {
                    $(this).addClass('ui-state-active');
                }
            }).on("keyup", function() {
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

        /**
         * SELECT elements may have different states, such as `hovering` or `focused`. For each state, there is a
         * corresponding style class that is added to the select when it is in that state, such as `ui-state-hover` or
         * `ui-state-focus`. These classes are used by CSS rules for styling. This method sets up a select element so
         * that the classes are added correctly (by adding event listeners).
         * @param {JQuery} select SELECT element to skin
         * @return {typeof PrimeFaces} this for chaining
         */
        skinSelect : function(select) {
            select.on("mouseover", function() {
                var el = $(this);
                if(!el.hasClass('ui-state-focus'))
                    el.addClass('ui-state-hover');
            }).on("mouseout", function() {
                $(this).removeClass('ui-state-hover');
            }).on("focus", function() {
                $(this).addClass('ui-state-focus').removeClass('ui-state-hover');
            }).on("blur", function() {
                $(this).removeClass('ui-state-focus ui-state-hover');
            });

            return this;
        },

        /**
         * Logs the given message at the `info` level.
         * @param {string} log Message to log
         */
        info: function(log) {
            if(this.logger) {
                this.logger.info(log);
            }
        },

        /**
         * Logs the given message at the `debug` level.
         * @param {string} log Message to log
         */
        debug: function(log) {
            if(this.logger) {
                this.logger.debug(log);
            }
        },

        /**
         * Logs the given message at the `warn` level.
         * @param {string} log Message to log
         */
        warn: function(log) {
            if(this.logger) {
                this.logger.warn(log);
            }

            if (PrimeFaces.isDevelopmentProjectStage() && window.console) {
                console.log(log);
            }
        },

        /**
         * Logs the given message at the `error` level.
         * @param {string} log Message to log
         */
        error: function(log) {
            if(this.logger) {
                this.logger.error(log);
            }

            if (PrimeFaces.isDevelopmentProjectStage() && window.console) {
                console.error(log);
            }
        },

        /**
         * Checks whether the current application is running in a development environment or a production environment.
         * @return {boolean} `true` if this is a development environment, `false` otherwise.
         */
        isDevelopmentProjectStage: function() {
            return PrimeFaces.settings.projectStage === 'Development';
        },

        /**
         * Handles the error case when a widget was requested that is not available. Currently just logs an error
         * message.
         * @param {string} widgetVar Widget variables of a widget
         */
        widgetNotAvailable: function(widgetVar) {
           PrimeFaces.error("Widget for var '" + widgetVar + "' not available!");
        },

        /**
         * Takes an input or textarea element and sets the caret (text cursor) position to the end of the the text.
         * @param {JQuery} element An input or textarea element.
         */
        setCaretToEnd: function(element) {
            if(element) {
                element.trigger('focus');
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

        /**
         * Gets the currently loaded PrimeFaces theme CSS link.
         * @return {string} The full URL to the theme CSS
         */
        getThemeLink : function() {
            var themeLink = $('link[href*="' + PrimeFaces.RESOURCE_IDENTIFIER + '/theme.css"]');
            // portlet
            if (themeLink.length === 0) {
                themeLink = $('link[href*="' + PrimeFaces.RESOURCE_IDENTIFIER + '=theme.css"]');
            }
            return themeLink;
        },

        /**
         * Gets the currently loaded PrimeFaces theme.
         * @return {string} The current theme, such as `omega` or `luna-amber`. Empty string when no theme is loaded.
         */
        getTheme : function() {
            var themeLink = PrimeFaces.getThemeLink();
            if (themeLink.length === 0) {
                return "";
            }

            var themeURL = themeLink.attr('href'),
                plainURL = themeURL.split('&')[0],
                oldTheme = plainURL.split('ln=primefaces-')[1];

            return oldTheme;
        },

        /**
         * Changes the current theme to the given theme (by exchanging CSS files). Requires that the theme was
         * installed and is available.
         * @param {string} newTheme The new theme, eg. `luna-amber`, `nova-dark`, or `omega`.
         */
        changeTheme: function(newTheme) {
            if(newTheme && newTheme !== '') {
                var themeLink = PrimeFaces.getThemeLink();

                var themeURL = themeLink.attr('href'),
                    plainURL = themeURL.split('&')[0],
                    oldTheme = plainURL.split('ln=')[1],
                    newThemeURL = themeURL.replace(oldTheme, 'primefaces-' + newTheme);

                themeLink.attr('href', newThemeURL);
            }
        },

        /**
         * Creates a regexp that matches the given text literal, and HTML-escapes that result.
         * @param {string} text The literal text to escape.
         * @return {string} A regexp that matches the given text, escaped to be used as a text-literal within an HTML
         * document.
         */
        escapeRegExp: function(text) {
            return this.escapeHTML(text.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1"));
        },

        /**
         * Escapes the given value to be used as the content of an HTML element or attribute.
         * @param {string} value A string to be escaped
         * @return {string} The given value, escaped to be used as a text-literal within an HTML document.
         */
        escapeHTML: function(value) {
            return String(value).replace(/[&<>"'`=\/]/g, function (s) {
                return PrimeFaces.entityMap[s];
            });
        },

        /**
         * Clears the text selected by the user on the current page.
         */
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

        /**
         * Finds the text currently selected by the user on the current page.
         * @return {string | Selection} The text currently selected by the user on the current page.
         */
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

        /**
         * Checks whether any text on the current page is selected by the user.
         * @return {boolean} `true` if text is selected, `false` otherwise.
         */
        hasSelection: function() {
            return this.getSelection().length > 0;
        },

        /**
         * A shortcut for {@link createWidget}.
         * @param {string} widgetName Name of the widget class, as registered in {@link PrimeFaces.widget}.
         * @param {string} widgetVar Widget variable of the widget
         * @param {PrimeFaces.widget.BaseWidgetCfg} cfg Configuration for the widget
         */
        cw : function(widgetName, widgetVar, cfg) {
            this.createWidget(widgetName, widgetVar, cfg);
        },

        /**
         * Deprecated, use {@link PrimeFaces.resources.getFacesResource} instead.
         * @deprecated
         * @param {string} name Name of the resource
         * @param {string} library Library of the resource
         * @param {string} version Version of the resource
         * @return {string} The URL for accessing the given resource.
         */
        getFacesResource : function(name, library, version) {
           return PrimeFaces.resources.getFacesResource(name, library, version);
        },

        /**
         * Creates a new widget of the given type and with the given configuration. Registers that widget in the widgets
         * registry {@link PrimeFaces.widgets}. If this method is called in response to an AJAX request and the method
         * exists already, it is refreshed.
         * @param {string} widgetName Name of the widget class, as registered in `PrimeFaces.widget`
         * @param {string} widgetVar Widget variable of the widget
         * @param {PrimeFaces.widget.BaseWidgetCfg} cfg Configuration for the widget
         */
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

        /**
         * Checks whether an items is contained in the given array. The items is compared against the array entries
         * via the `===` operator.
         * @template [T=any] Type of the array items
         * @param {T[]} arr An array with items
         * @param {T} item An item to check
         * @return {boolean} `true` if the given item is in the given array, `false` otherwise.
         */
        inArray: function(arr, item) {
            for(var i = 0; i < arr.length; i++) {
                if(arr[i] === item) {
                    return true;
                }
            }

            return false;
        },

        /**
         * Checks whether a value is of type `number` and is neither `Infinity` nor `NaN`.
         * @param {any} value A value to check
         * @return {boolean} `true` if the given value is a finite number, `false` otherwise.
         */
        isNumber: function(value) {
            return typeof value === 'number' && isFinite(value);
        },

        /**
         * Attempts to put focus an element:
         *
         * - When `id` is given, puts focus on the element with that `id`
         * - Otherwise, when `context` is given, puts focus on the first focusable element within that context
         * (container)
         * - Otherwise, puts focus on the first focusable element in the page.
         * @param {string} [id] ID of an element to focus.
         * @param {string} [context] The ID of a container with an element to focus
         */
        focus: function(id, context) {
            var selector = ':not(:submit):not(:button):input:visible:enabled[name]';

            setTimeout(function() {
                if(id) {
                    var jq = $(PrimeFaces.escapeClientId(id));

                    if(jq.is(selector)) {
                        jq.trigger('focus');
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

        /**
         * Puts focus on the given element.
         * @param {JQuery} el Element to focus
         */
        focusElement: function(el) {
            if(el.is(':radio')) {
                // github issue: #2582
                if(el.hasClass('ui-helper-hidden-accessible')) {
                    el.parent().trigger('focus');
                }
                else {
                    var checkedRadio = $(':radio[name="' + $.escapeSelector(el.attr('name')) + '"]').filter(':checked');
                    if(checkedRadio.length)
                        checkedRadio.trigger('focus');
                    else
                        el.trigger('focus');
                }
            }
            else {
                el.trigger('focus');
            }
        },

        /**
         * As a `<p:fileDownload>` process is implemented as a norma, non-AJAX request, `<p:ajaxStatus>` will not work.
         * Still, PrimeFaces provides a feature to monitor file downloads via this client-side function. This is done
         * by sending a cookie with the HTTP response of the file download request. On the client-side, polling is used
         * to check when the cookie is set.
         *
         * The example below displays a modal dialog when a download begins and hides it when the download is complete:
         *
         * Client-side callbacks:
         *
         * ```javascript
         * function showStatus() {
         *   PF('statusDialog').show();
         * }
         * function hideStatus() {
         *   PF('statusDialog').hide();
         * }
         * ```
         *
         * Server-side XHTML view:
         *
         * ```xml
         * <p:commandButton value="Download" ajax="false" onclick="PrimeFaces.monitorDownload(showStatus, hideStatus)">
         *   <p:fileDownload value="#{fileDownloadController.file}"/>
         * </p:commandButton>
         * ```
         * @param {() => void} start Callback that is invoked when the download starts.
         * @param {() => void} complete Callback that is invoked when the download ends.
         * @param {string} [monitorKey] Name of the cookie for monitoring the download. The cookie name defaults to
         * `primefaces.download` + the current viewId. When a monitor key is given, the name of the cookie will consist of a prefix and the
         * given monitor key.
         */
        monitorDownload: function(start, complete, monitorKey) {
            if(this.cookiesEnabled()) {
                if(start) {
                    start();
                }

                var cookieName = 'primefaces.download' + PrimeFaces.settings.viewId.replace(/\//g, '_');
                if (monitorKey && monitorKey !== '') {
                    cookieName += '_' + monitorKey;
                }

                var cookiePath = PrimeFaces.settings.contextPath;
                if (!cookiePath || cookiePath === '') {
                    cookiePath = '/';
                }

                window.downloadMonitor = setInterval(function() {
                    var downloadComplete = PrimeFaces.getCookie(cookieName);

                    if(downloadComplete === 'true') {
                        if(complete) {
                            complete();
                        }
                        clearInterval(window.downloadMonitor);
                        PrimeFaces.setCookie(cookieName, null, { path: cookiePath });
                    }
                }, 1000);
            }
        },

        /**
         *  Scrolls to a component with given client id
         * @param {string} id The ID of an element to scroll to.
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
         * Aligns container scrollbar to keep item in container viewport, algorithm copied from JQueryUI menu widget.
         * @param {JQuery} container The container with a scrollbar that contains the item.
         * @param {JQuery} item The item to scroll into view.
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

        /**
         * Finds the width of the scrollbar that is used by the current browser, as scrollbar widths are different for
         * across different browsers.
         * @return {number} The width of the scrollbars of the current browser.
         */
        calculateScrollbarWidth: function() {
            if(!this.scrollbarWidth) {
                var $div = $('<div></div>')
                    .css({ width: '100px', height: '100px', overflow: 'auto', position: 'absolute', top: '-1000px', left: '-1000px' })
                    .prependTo('body').append('<div></div>').find('div')
                        .css({ width: '100%', height: '200px' });
                this.scrollbarWidth = 100 - $div.width();
                $div.parent().remove();
            }

            return this.scrollbarWidth;
        },

        /**
         * A function that is used as the handler function for HTML event tags (`onclick`, `onkeyup` etc.). When a
         * component has got an `onclick` etc attribute, the JavaScript for that attribute is called by this method.
         * @param {HTMLElement} element Element on which the event occurred.
         * @param {Event} event Event that occurred.
         * @param {((this: HTMLElement, event: Event) => boolean | undefined)[]} functions A list of callback
         * functions. If any returns `false`, the default action of the event is prevented.
         */
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

        /**
         * A function that is used as the handler function for AJAX behaviors. When a component has got an AJAX
         * behavior, the JavaScript that implements behavior's client-side logic is called by this method.
         * @param {Partial<PrimeFaces.ajax.ConfigurationExtender>} ext Additional options to override the current
         * options.
         * @param {Event} event Event that occurred.
         * @param {((this: typeof PrimeFaces, ext: Partial<PrimeFaces.ajax.ConfigurationExtender>, event: Event) => boolean | undefined)[]} fns
         * A list of callback functions. If any returns `false`, the other callbacks are not invoked.
         */
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
    	 * Deprecated, use `PrimeFaces.dialog.DialogHandler.openDialog` instead.
         * @deprecated
         * @param {PrimeFaces.dialog.DialogHandlerCfg} cfg Configuration of the dialog.
    	 */
        openDialog: function(cfg) {
        	PrimeFaces.dialog.DialogHandler.openDialog(cfg);
        },

        /**
    	 * Deprecated, use `PrimeFaces.dialog.DialogHandler.closeDialog` instead.
         * @deprecated
         * @param {PrimeFaces.dialog.DialogHandlerCfg} cfg Configuration of the dialog.
         */
        closeDialog: function(cfg) {
        	PrimeFaces.dialog.DialogHandler.closeDialog(cfg);
        },

        /**
    	 * Deprecated, use {@link PrimeFaces.dialog.DialogHandler.showMessageInDialog} instead.
         * @deprecated
         * @param {PrimeFaces.widget.ConfirmDialog.ConfirmDialogMessage} msg Message to show in a dialog.
         */
        showMessageInDialog: function(msg) {
        	PrimeFaces.dialog.DialogHandler.showMessageInDialog(msg);
        },

        /**
         * Displays dialog or popup according to the type of confirm component.
         * @deprecated Deprecated, use {@link PrimeFaces.dialog.DialogHandler.confirm} instead.
         * @param {PrimeFaces.dialog.ExtendedConfirmDialogMessage} msg Message to show with the confirm dialog or popup.
         */
        confirm: function(msg) {
            if (msg.type === 'popup' && PrimeFaces.confirmPopup) {
                PrimeFaces.confirmPopup.showMessage(msg);
            }
            else {
                PrimeFaces.dialog.DialogHandler.confirm(msg);
            }
        },

        /**
         * Some widgets need to compute their dimensions based on their parent element(s). This requires that such
         * widgets are not rendered until they have become visible. A widget may not be visible, for example, when it
         * is inside a tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for
         * widgets to render once they are visible. This is done by keeping a list of widgets that need to be rendered,
         * and checking on every change (AJAX request, tab change etc.) whether any of those have become visible. A
         * widgets should extend `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
         *
         * This is the list of renders for widgets that are currently waiting to become visible.
         *
         * @type {PrimeFaces.DeferredRender[]}
         */
        deferredRenders: [],

        /**
         * Some widgets need to compute their dimensions based on their parent element(s). This requires that such
         * widgets are not rendered until they have become visible. A widget may not be visible, for example, when it
         * is inside a tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for
         * widgets to render once they are visible. This is done by keeping a list of widgets that need to be rendered,
         * and checking on every change (AJAX request, tab change etc.) whether any of those have become visible. A
         * widgets should extend `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
         *
         * Adds a deferred render to the global list.
         *
         * @param {string} widgetId The ID of a deferred widget.
         * @param {string} containerId ID of the container that should be visible before the widget can be rendered.
         * @param {() => boolean} fn Callback that is invoked when the widget _may_ possibly have become visible. Should
         * return `true` when the widget was rendered, or `false` when the widget still needs to be rendered later.
         */
        addDeferredRender: function(widgetId, containerId, fn) {
            this.deferredRenders.push({widget: widgetId, container: containerId, callback: fn});
        },

        /**
         * Some widgets need to compute their dimensions based on their parent element(s). This requires that such
         * widgets are not rendered until they have become visible. A widget may not be visible, for example, when it
         * is inside a tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for
         * widgets to render once they are visible. This is done by keeping a list of widgets that need to be rendered,
         * and checking on every change (AJAX request, tab change etc.) whether any of those have become visible. A
         * widgets should extend `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
         *
         * Removes a deferred render from the global list.
         *
         * @param {string} widgetId The ID of a deferred widget.
         */
        removeDeferredRenders: function(widgetId) {
            for(var i = (this.deferredRenders.length - 1); i >= 0; i--) {
                var deferredRender = this.deferredRenders[i];

                if(deferredRender.widget === widgetId) {
                    this.deferredRenders.splice(i, 1);
                }
            }
        },

        /**
         * Some widgets need to compute their dimensions based on their parent element(s). This requires that such
         * widgets are not rendered until they have become visible. A widget may not be visible, for example, when it
         * is inside a tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for
         * widgets to render once they are visible. This is done by keeping a list of widgets that need to be rendered,
         * and checking on every change (AJAX request, tab change etc.) whether any of those have become visible. A
         * widgets should extend `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
         *
         * Invokes all deferred renders. This is usually called when an action was performed that _may_ have resulted
         * in a container now being visible. This includes actions such as an AJAX request request was made or a tab
         * change.
         *
         * @param {string} containerId ID of the container that _may_ have become visible.
         */
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
        
         /**
         * Finds the current locale with the i18n keys and the associated translations. Uses the current language key
         * as specified by `PrimeFaces.settings.locale`. When no locale was found for the given locale, falls back to
         * the default English locale.
         * @param {string} [cfgLocale] optional configuration locale from the widget
         * @return {PrimeFaces.Locale} The current locale with the key-value pairs.
         */
        getLocaleSettings: function(cfgLocale) {
            var locale;
            if(cfgLocale) {
                // widget locale must not be cached since it can change per widget
                locale = PrimeFaces.locales[cfgLocale];
            }
            else {
                // global settings so return cached value if already loaded
                if(this.localeSettings) {
                   return this.localeSettings;
                }
                locale = PrimeFaces.locales[PrimeFaces.settings.locale];
            }

            // try and strip specific language from nl_BE to just nl
            if (!locale) {
                var localeKey = cfgLocale ? cfgLocale : PrimeFaces.settings.locale;
                locale = PrimeFaces.locales[localeKey.split('_')[0]];
            }

            // if all else fails default to US English
            if(!locale) {
                locale = PrimeFaces.locales['en_US'];
            }

            // cache default global settings
            if(!cfgLocale) {
                this.localeSettings = locale;
            }

            return locale;
        },

        /**
         * Some ARIA attributes have a value that depends on the current locale. This returns the localized version for
         * the given aria key.
         * @param {string} key An aria key
         * @return {string} The translation for the given aria key
         */
        getAriaLabel: function(key) {
            var ariaLocaleSettings = this.getLocaleSettings()['aria'];
            return (ariaLocaleSettings&&ariaLocaleSettings[key]) ? ariaLocaleSettings[key] : PrimeFaces.locales['en_US']['aria'][key];
        },

        /**
         * For 4.0 jQuery deprecated $.trim in favor of PrimeFaces.trim however that does not handle
         * NULL and jQuery did so this function allows a drop in replacement.
         *
         * @param {string} value the String to trim
         * @return {string} trimmed value or "" if it was NULL
         */
        trim: function(value) {
            if (!value) {
                return "";
            }

            if (typeof value === 'string' || value instanceof String) {
                return value.trim();
            }

            // return original value if it was not a string
            return value;
        },

        /**
         * Generate a RFC-4122 compliant UUID to be used as a unique identifier.
         *
         * See https://www.ietf.org/rfc/rfc4122.txt
         *
         * @return {string} A random UUID.
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

        /**
         * Increment and return the next `z-index` for CSS as a string.
         * Note that jQuery will no longer accept numeric values in {@link JQuery.css | $.fn.css} as of version 4.0.
         *
         *  @return {string} the next `z-index` as a string.
         */
        nextZindex: function() {
            return String(++PrimeFaces.zindex);
        },

       /**
         * Converts a date into an ISO-8601 date without using the browser timezone offset.
         *
         * See https://stackoverflow.com/questions/10830357/javascript-toisostring-ignores-timezone-offset
         *
         * @param {Date} date the date to convert
         * @return {string} ISO-8601 version of the date
         */
        toISOString: function(date) {
            return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString();
        },

        /**
         * Logs the current PrimeFaces and jQuery version to console.
         */
        version: function() {
            var version = 'PrimeFaces ' + PrimeFaces.VERSION + ' (jQuery ' + jQuery.fn.jquery + ')';
            console.log(version);
        },

        /**
         * A tracker for the current z-index, used for example when creating multiple modal dialogs.
         * @type {number}
         */
        zindex : 1000,

        /**
         * Global flag for enabling or disabling both jQuery and CSS animations.
         * @type {boolean}
         */
        animationEnabled : true,

         /**
         * Flag for detecting whether animation is currently running. Similar to jQuery.active flag and is useful
         * for scripts or automation tests to determine if the animation is currently running.
         * @type {boolean}
         */
        animationActive : false,

        /**
         * Used to store whether a custom focus has been rendered. This avoids having to retain the last focused element
         * after AJAX update.
         * @type {boolean}
         */
        customFocus : false,

        /**
         * A list of widgets that were once instantiated, but are not removed from the DOM, such as due to the result
         * of an AJAX update request.
         * @type {PrimeFaces.widget.BaseWidget[]}
         * @readonly
         */
        detachedWidgets : [],

        /**
         * Name of the POST parameter that indicates whether the request is an AJAX request.
         * @type {string}
         * @readonly
         */
        PARTIAL_REQUEST_PARAM : "javax.faces.partial.ajax",

        /**
         * Name of the POST parameter that contains the list of components to be updated.
         * @type {string}
         * @readonly
         */
        PARTIAL_UPDATE_PARAM : "javax.faces.partial.render",

        /**
         * Name of the POST parameter that contains the list of components to process.
         * @type {string}
         * @readonly
         */
        PARTIAL_PROCESS_PARAM : "javax.faces.partial.execute",

        /**
         * Name of the POST parameter that indicates which element or component triggered the AJAX request.
         * @type {string}
         * @readonly
         */
        PARTIAL_SOURCE_PARAM : "javax.faces.source",

        /**
         * Name of the POST parameter that contains the name of the current behavior event.
         * @type {string}
         * @readonly
         */
        BEHAVIOR_EVENT_PARAM : "javax.faces.behavior.event",

        /**
         * Name of the POST parameter that contains the name of the current partial behavior event.
         * @type {string}
         * @readonly
         */
        PARTIAL_EVENT_PARAM : "javax.faces.partial.event",

        /**
         * Name of the POST parameter that indicates whether forms should have their values reset.
         * @type {string}
         * @readonly
         */
        RESET_VALUES_PARAM : "primefaces.resetvalues",

        /**
         * Name of the POST parameter that indicates whether `<p:autoUpdate>` tags should be ignored.
         * @type {string}
         * @readonly
         */
        IGNORE_AUTO_UPDATE_PARAM : "primefaces.ignoreautoupdate",

        /**
         * Name of the POST parameter that indicates whether children should be skipped.
         * @type {string}
         * @readonly
         */
        SKIP_CHILDREN_PARAM : "primefaces.skipchildren",

        /**
         * Name of the POST parameter that contains the current view state.
         * @type {string}
         * @readonly
         */
        VIEW_STATE : "javax.faces.ViewState",

        /**
         * Name of the POST parameter with the current client window.
         * @type {string}
         * @readonly
         */
        CLIENT_WINDOW : "javax.faces.ClientWindow",

        /**
         * Name of the POST parameter that contains the view root.
         * @type {string}
         * @readonly
         */
        VIEW_ROOT : "javax.faces.ViewRoot",

        /**
         * Name of the POST parameter with the current client ID
         * @type {string}
         * @readonly
         */
        CLIENT_ID_DATA : "primefaces.clientid",

        /**
         * Name of the faces resource servlet, eg. `javax.faces.resource`.
         * @type {string}
         * @readonly
         */
        RESOURCE_IDENTIFIER: 'javax.faces.resource',

        /**
         * The current version of PrimeFaces.
         * @type {string}
         * @readonly
         */
        VERSION: '${project.version}'
    };

    // PrimeFaces Namespaces

    /**
     * An object with some runtime settings, such as the current locale.
     * @namespace
     *
     * @prop {string} locale The current locale, such as `en`,`en_US`, or `ja`.
     * @readonly locale
     *
     * @prop {boolean} validateEmptyFields `true` if empty (input etc.) fields should be validated, or `false` otherwise.
     * @readonly validateEmptyFields
     *
     * @prop {boolean} considerEmptyStringNull `true` if the empty string and `null` should be treated the same way, or
     * `false` otherwise.
     * @readonly considerEmptyStringNull
     */
    PrimeFaces.settings = {};
    PrimeFaces.util = {};
    /**
     * A registry of all instantiated widgets that are available on the current page.
     * @type {Record<string, PrimeFaces.widget.BaseWidget>}
     */
    PrimeFaces.widgets = {};

    /**
     * A map with language specific translations. This is a map between the language keys and another map with the i18n
     * keys mapped to the translation.
     * @type {Record<string, PrimeFaces.Locale>}
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
            year: 'Year',
            month: 'Month',
            week: 'Week',
            day: 'Day',
            list: 'Agenda',
            allDayText: 'All Day',
            moreLinkText: 'More...',
            noEventsText: 'No Events',
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

    /**
     * A map between some HTML entities and their HTML-escaped equivalent.
     * @type {Record<string, string>}
     */
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

    /**
     * Finds and returns a widget
     *
     * Note to typescript users: You should define a method that takes a widget variables and widget constructor, and
     * check whether the widget is of the given type. If so, you can return the widget and cast it to the desired type:
     * ```typescript
     * function getWidget<T extends PrimeFaces.widget.BaseWidget>(widgetVar, widgetClass: new() => T): T | undefined {
     *   const widget = PrimeFaces.widget[widgetVar];
     *   return widget !== undefined && widget instanceof constructor ? widgetClass : undefined;
     * }
     * ```
     * @function
     * @param {string} widgetVar The widget variable of a widget.
     * @return {PrimeFaces.widget.BaseWidget | undefined} The widget instance, or `undefined` if no such widget exists
     * currently.
     */
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
