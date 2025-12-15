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
         * PrimeFaces.escapeClientId("form:input"); // => "#form\\:input"
         * PrimeFaces.escapeClientId("form#input"); // => "#form#input"
         * PrimeFaces.escapeClientId("_5e08119c-7a6e-4fad-8061-51208ade4c4f|2"); // => "#_5e08119c-7a6e-4fad-8061-51208ade4c4f\\|2"
         * ```
         *
         * __Please note that this method does not escape all characters that need to be escaped and will not work with arbitrary IDs__
         * @param {string} id ID to convert.
         * @return {string} A CSS ID selector for the given ID.
         */
        escapeClientId : function(id) {
            return "#" + id.replace(/[:|]/g,"\\$&");
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
         * @template {new(...args: never[]) => unknown} TWidget Type of the widgets of interest, e.g.
         * `PrimeFaces.widget.DataTable`.
         * @param {TWidget} type The (proto)type of the widgets of interest, e.g., `PrimeFaces.widget.DataTable`.
         * @return  {InstanceType<TWidget>[]} An array of widgets that are of the requested type. If no suitable widgets
         * are found on the current page, an empty array will be returned.
         */
        getWidgetsByType: function(type) {
            return $.map(this.widgets, function(widget, key) {
                return type.prototype.isPrototypeOf(widget) ? widget : null;
            });
        },

        /**
         * Resolves the given target as $.
         *
         * @param {string | HTMLElement | JQuery | PrimeFaces.widget.BaseWidget} target Either id, element, jQuery object or PF widget.
         * @return {JQuery} The resolved $.
         */
        resolveAs$: function(target) {
            if (target instanceof PrimeFaces.widget.BaseWidget) {
                return target.getJQ();
            }
            else if (target instanceof $) {
                return target;
            }
            else if (target instanceof HTMLElement) {
                return $(target)
            }
            else if (typeof target === 'string') {
                return $(document.getElementById(target));
            }

            throw new Error("Unsupported type: " + (typeof target));
        },

        /**
         * Resolves the given target as id.
         *
         * @param {string | HTMLElement | JQuery | PrimeFaces.widget.BaseWidget} target Either id, element, jQuery object or PF widget.
         * @return {string} The id of the target.
         */
        resolveAsId: function(target) {
            if (target instanceof PrimeFaces.widget.BaseWidget) {
                var widgetJq = target.getJQ();
                return widgetJq.data(PrimeFaces.CLIENT_ID_DATA)||widgetJq.attr('id');
            }
            else if (target instanceof $) {
                return target.data(PrimeFaces.CLIENT_ID_DATA)||target.attr('id');
            }
            else if (target instanceof HTMLElement) {
                return target.id;
            }
            else if (typeof target === 'string') {
                return target;
            }

            throw new Error("Unsupported type: " + (typeof target));
        },

        /**
         * Gets the form by id or the closest form if the id is not a form itself.
         * In AJAX we also have a fallback for the first form in DOM, this should not be used here.
         *
         * @param {string} id ID of the component to get the closest form or if its a form itself
         * @return {JQuery} the form or NULL if no form found
         */
        getClosestForm: function(id) {
            var form = $(PrimeFaces.escapeClientId(id));
            if (!form.is('form')) {
                form = form.closest('form');
            }
            if (!form) {
                PrimeFaces.error('Form element could not be found for id: ' + id);
            }
            return form;
        },

        /**
         * Adds hidden input elements to the given form. For each key-value pair, a new hidden input element is created
         * with the given value and the key used as the name.
         * @param {string} parent The ID of a FORM element.
         * @param {Record<string, string>} params An object with key-value pairs.
         * @return {typeof PrimeFaces} This object for chaining.
         */
        addSubmitParam : function(parent, params) {
            var form = PrimeFaces.getClosestForm(parent);

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
            var form = PrimeFaces.getClosestForm(formId);
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
         * Sets the value of a specified cookie with additional security configurations.
         * If the page is served over HTTPS and cookies are configured to be secure in the settings,
         * the secure flag will be set. The SameSite attribute is set based on the settings or defaults to 'Lax'.
         * @param {string} name The name of the cookie.
         * @param {string} value The value to set for the cookie.
         * @param {Partial<Cookies.CookieAttributes>} [cfg] Configuration for this cookie: when it expires, its
         * paths and domain and whether it is secure cookie.
         */
        setCookie : function(name, value, cfg) {
            cfg.secure = location.protocol === 'https:' && PrimeFaces.settings.cookiesSecure;
            cfg.sameSite = PrimeFaces.settings.cookiesSameSite || "Lax";
            // "None" is only allowed when Secure attribute so default to Lax if unsecure
            if (!cfg.secure && cfg.sameSite === "None") {
                cfg.sameSite = "Lax"
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
            if (navigator.cookieEnabled) {
                return true;
            } else {
                document.cookie = "testcookie";
                return document.cookie.includes("testcookie");
            }
        },

        /**
         * Generates a unique key for using in HTML5 local storage by combining the context, view, id, and key.
         * @param {string} id ID of the component
         * @param {string} key a unique key name such as the component name
         * @param {boolean} global if global then do not include the view id
         * @return {string} the generated key comprising of context + view + id + key
         */
        createStorageKey : function(id, key, global) {
            var sk = PrimeFaces.settings.contextPath.replace(/\//g, '-')
                    + (global ? '' : PrimeFaces.settings.viewId.replace(/\//g, '-'))
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

            // normal inputs just check for a value
            var isFilled = value.length > 0;

            // #11974: Autocomplete multiple mode must be handled differently
            if(parent.hasClass('ui-autocomplete-multiple')) {
                isFilled = parent.find('li.ui-autocomplete-token').length > 0;
            }

            if (isFilled) {
                input.addClass('ui-state-filled');

                if(parent.is("div, span:not('.ui-float-label')")) {
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
            let parent = input.parent();

            // #11974: Autocomplete multiple mode must be handled differently
            if(parent.hasClass('ui-autocomplete-input-token')) {
                parent = parent.parent().parent();
            }

            const updateFilledStateOnBlur = function () {
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

                if(parent.is("div, span:not('.ui-float-label')")) {
                    parent.addClass('ui-inputwrapper-focus');
                }
            }).on("blur animationstart", function() {
                //animationstart is to fix autofill issue https://github.com/primefaces/primefaces/issues/12444
                $(this).removeClass('ui-state-focus');

                // if the input is a datepicker or a number input, wait a bit before updating the filled state
                if(input.hasClass('hasDatepicker') || input.attr('inputmode') === 'numeric') {
                    setTimeout(function() {
                        updateFilledStateOnBlur();
                    }, 150);
                }
                else {
                    updateFilledStateOnBlur();
                }
            });

            if(input.is('textarea')) {
                input.attr('aria-multiline', true);
            }
            
            // ARIA for filter type inputs
            if (input.is('[class*="-filter"]')) {
                var ariaLabel = input.attr('aria-label');
                if (!ariaLabel) {
                    input.attr('aria-label', PrimeFaces.getLocaleLabel('filter'));
                }
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
                if(e.code === 'Space' || e.key === 'Enter') {
                    $(this).addClass('ui-state-active');
                }
            }).on("keyup", function() {
                $(this).removeClass('ui-state-active');
            });

            return this;
        },
        
        /**
         * There are many Close buttons in PF that should get aria-label="close" and role="button".
         * @param {JQuery} element BUTTON or LINK element
         * @return {JQuery} this for chaining
         */
        skinCloseAction : function(element) {
            if (!element || element.length === 0) return element;
            element.attr('aria-label', PrimeFaces.getAriaLabel('close'));
            element.attr('role', 'button');
            return element;
        },

        /**
         * Applies the inline AJAX status (ui-state-loading) to the given widget / button.
         * @param {PrimeFaces.widget.BaseWidget} [widget] the widget.
         * @param {JQuery} [button] The button DOM element.
         * @param {(widget: PrimeFaces.widget.BaseWidget, settings: JQuery.AjaxSettings) => boolean} [isXhrSource] Callback that checks if the widget is the source of the current AJAX request.
         */
        bindButtonInlineAjaxStatus: function(widget, button, isXhrSource) {
            if (!isXhrSource) {
                isXhrSource = function(widget, settings) {
                    return PrimeFaces.ajax.Utils.isXhrSource(widget, settings);
                };
            }

            widget.ajaxCount = 0;
            var namespace = '.' + widget.id;
            $(document).on('pfAjaxSend' + namespace, function(e, xhr, settings) {
                if (isXhrSource.call(this, widget, settings)) {
                    widget.ajaxCount++;
                    if (widget.ajaxCount > 1) {
                        return;
                    }

                    button.addClass('ui-state-loading');
                    widget.ajaxStart = Date.now();

                    if (typeof widget.disable === 'function'
                        && widget.cfg.disableOnAjax !== false) {
                        widget.disable();
                    }

                    var loadIcon = $('<span class="ui-icon-loading ui-icon ui-c pi pi-spin pi-spinner"></span>');
                    var uiIcon = button.find('.ui-icon');
                    if (uiIcon.length) {
                        var prefix = 'ui-button-icon-';
                        loadIcon.addClass(prefix + uiIcon.attr('class').includes(prefix + 'left') ? 'left' : 'right');
                    }
                    button.prepend(loadIcon);
                }
            }).on('pfAjaxComplete' + namespace, function(e, xhr, settings, args) {
                if (isXhrSource.call(this, widget, settings)) {
                    widget.ajaxCount--;
                    if (widget.ajaxCount > 0 || !args || args.redirect) {
                        return;
                    }

                    PrimeFaces.queueTask(
                        function(){ PrimeFaces.buttonEndAjaxDisabled(widget, button); },
                        Math.max(PrimeFaces.ajax.minLoadAnimation + widget.ajaxStart - Date.now(), 0)
                    );
                    delete widget.ajaxStart;
                }
            });
            widget.addDestroyListener(function() {
                $(document).off(namespace);
            });
        },

        /**
         * Ends the AJAX disabled state.
         * @param {PrimeFaces.widget.BaseWidget} [widget] the widget.
         * @param {JQuery} [button] The button DOM element.
         */
        buttonEndAjaxDisabled: function(widget, button) {
            button.removeClass('ui-state-loading');

            if (typeof widget.enable === 'function'
                && widget.cfg.disableOnAjax !== false
                && !widget.cfg.disabledAttr) {
                widget.enable();
            }

            button.find('.ui-icon-loading').remove();
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
            if (PrimeFaces.isDevelopmentProjectStage() && window.console) {
                console.info(log);
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
            if (PrimeFaces.isDevelopmentProjectStage() && window.console) {
                console.debug(log);
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
                console.warn(log);
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
         * Checks whether the current application is running in a production environment.
         * @return {boolean} `true` if this is a production environment, `false` otherwise.
         */
        isProductionProjectStage: function() {
            return PrimeFaces.settings.projectStage === 'Production';
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
            return PrimeFaces.env.getTheme();
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
         * @param {boolean | undefined} preventDoubleEscaping if true will not include ampersand to prevent double escaping
         * @return {string} The given value, escaped to be used as a text-literal within an HTML document.
         */
        escapeHTML: function(value, preventDoubleEscaping) {
            var regex = preventDoubleEscaping ? /[<>"'`=/]/g : /[&<>"'`=/]/g;
            return String(value).replace(regex, function (s) {
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
                    if (cfg.postRefresh) {
                        cfg.postRefresh.call(widget, widget);
                    }
                }
                //page init
                else {
                    if (cfg.preConstruct) {
                        if (!cfg.labels) {
                            cfg.labels = {};
                        }
                        if (!cfg.labels.aria) {
                            cfg.labels.aria = {};
                        }
                        cfg.preConstruct.call(null, cfg);
                    }
                    var newWidget = new this.widget[widgetName](cfg);
                    this.widgets[widgetVar] = newWidget;
                    if (cfg.postConstruct) {
                       cfg.postConstruct.call(newWidget, newWidget);
                    }
                }
            }
            // widget script not loaded
            else {
                // should be loaded by our dynamic resource handling, log a error
                PrimeFaces.error("Widget class '" + widgetName + "' not found!");
            }
        },

        /**
         * Checks whether an items is contained in the given array. The items is compared against the array entries
         * via the `===` operator.
         * @template [T=unknown] Type of the array items
         * @param {T[]} arr An array with items
         * @param {T} item An item to check
         * @return {boolean} `true` if the given item is in the given array, `false` otherwise.
         */
        inArray: function(arr, item) {
            return arr.includes(item);
        },

        /**
         * Checks whether a value is of type `number` and is neither `Infinity` nor `NaN`.
         * @param {unknown} value A value to check
         * @return {boolean} `true` if the given value is a finite number (neither `NaN` nor +/- `Infinity`),
         * `false` otherwise.
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
            var selector = ':not(:submit):not(:button):not([readonly]):input:visible:enabled[name]';
            
            // if looking in container like dialog also check for first link
            if (context) {
                var container = $(PrimeFaces.escapeClientId(context));
                if (container.hasClass('ui-dialog')) {
                     selector += ', a:first';
                }
            }

            setTimeout(function() {
                var focusFirstElement = function(elements) {
                    if (!elements || elements.length === 0) {
                        return;
                    }
                    
                    // first element could be the dialog close button
                    var firstElement = elements.eq(0);
                    // loop over elements looking for an input
                    var inputs = elements.filter(":input");
                    if (inputs.length > 0) {
                        firstElement = inputs.eq(0);
                    }
                    
                    PrimeFaces.focusElement(firstElement);
                };
            
                if(id) {
                    var jq = $(PrimeFaces.escapeClientId(id));

                    if(jq.is(selector)) {
                        jq.trigger('focus');
                    }
                    else {
                        focusFirstElement(jq.find(selector))
                    }
                }
                else if(context) {
                     focusFirstElement($(PrimeFaces.escapeClientId(context)).find(selector))
                }
                else {
                    focusFirstElement($(selector));
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
                    var checkedRadio = $(':radio[name="' + CSS.escape(el.attr('name')) + '"]').filter(':checked');
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
                cookieName = cookieName.substr(0, cookieName.lastIndexOf("."));
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
         * Scrolls to a component with given client id or jQuery element. The scroll animation can be customized with a duration
         * and an optional offset from the top of the target element.
         * @param {string | JQuery} scrollTarget The ID of an element or jQuery element to scroll to
         * @param {string | number} [duration=400] Duration of the scroll animation in milliseconds or a string like 'slow', 'fast'
         * @param {number} [topOffset=0] Additional offset in pixels from the top of the target element
         * @example
         * // Scroll to element with ID 'myElement' over 1 second
         * PrimeFaces.scrollTo('myElement', 1000);
         * 
         * // Scroll to jQuery element with 50px offset from top
         * PrimeFaces.scrollTo($('#myElement'), 'slow', 50);
         */
        scrollTo: function(scrollTarget, duration = 400, topOffset = 0) {
            var element = typeof scrollTarget === 'string' ? $(PrimeFaces.escapeClientId(scrollTarget)) : scrollTarget;
            var offset = element.offset();
            var scrollBehavior = 'scroll-behavior';
            var target = $('html,body');
            var sbValue = target.css(scrollBehavior);
            target.css(scrollBehavior, 'auto');
            target.animate(
                    { scrollTop: offset.top - topOffset, scrollLeft: offset.left },
                    duration,
                    'easeInCirc',
                    function(){ target.css(scrollBehavior, sbValue) }
            );
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
                for(const fn of functions) {
                    var retVal = fn.call(element, event);
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
            if (fns) {
                for (const fn of fns) {
                    if (fn.call(this, ext, event) === false) {
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
            if (PrimeFaces.dialog) {
        	PrimeFaces.dialog.DialogHandler.openDialog(cfg);
            }
        },

        /**
    	 * Deprecated, use `PrimeFaces.dialog.DialogHandler.closeDialog` instead.
         * @deprecated
         * @param {PrimeFaces.dialog.DialogHandlerCfg} cfg Configuration of the dialog.
         */
        closeDialog: function(cfg) {
            if (PrimeFaces.dialog) {
        	PrimeFaces.dialog.DialogHandler.closeDialog(cfg);
            }
        },

        /**
    	 * Deprecated, use {@link PrimeFaces.dialog.DialogHandler.showMessageInDialog} instead.
         * @deprecated
         * @param {PrimeFaces.widget.ConfirmDialog.ConfirmDialogMessage} msg Message to show in a dialog.
         */
        showMessageInDialog: function(msg) {
            if (PrimeFaces.dialog) {
        	PrimeFaces.dialog.DialogHandler.showMessageInDialog(msg);
            }
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
            else if (PrimeFaces.dialog) {
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
         * Adds a deferred render to the global list.  If this widdget has already been added only the last instance
         * will be added to the stack.
         *
         * @param {string} widgetId The ID of a deferred widget.
         * @param {string} containerId ID of the container that should be visible before the widget can be rendered.
         * @param {() => boolean} fn Callback that is invoked when the widget _may_ possibly have become visible. Should
         * return `true` when the widget was rendered, or `false` when the widget still needs to be rendered later.
         */
        addDeferredRender: function(widgetId, containerId, fn) {
            // remove existing
            this.deferredRenders = this.deferredRenders.filter(deferredRender => {
                return !(deferredRender.widget === widgetId && deferredRender.container === containerId);
            });

            // add new
            this.deferredRenders.push({ widget: widgetId, container: containerId, callback: fn });
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
            this.deferredRenders = this.deferredRenders.filter(function(deferredRender) {
                return deferredRender.widget !== widgetId;
            });
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
            for (const deferredRender of this.deferredRenders) {

                if(deferredRender.container === containerId) {
                    var rendered = deferredRender.callback.call();
                    if(rendered) {
                        widgetsToRemove.push(deferredRender.widget);
                    }
                }
            }

            for (const widget of widgetsToRemove) {
                this.removeDeferredRenders(widget);
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
            } else {
                // global settings so return cached value if already loaded
                if(this.localeSettings) {
                    return this.localeSettings;
                }
                locale = PrimeFaces.locales[PrimeFaces.settings.locale];
            }

            // try and strip specific language from nl_BE to just nl
            if (!locale) {
                var fullLocaleKey = cfgLocale || PrimeFaces.settings.locale;
                var splitLocaleKey = fullLocaleKey ? fullLocaleKey.split('_')[0] : null;
                if (splitLocaleKey) {
                    locale = PrimeFaces.locales[splitLocaleKey];
                }
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
         * Retrieves a localized ARIA label based on the provided key. If the key is not found in the current locale,
         * it falls back to the US English locale. If the key is still not found, it uses a default value or a placeholder
         * indicating the missing key. This method also supports dynamic replacement of placeholders within the label
         * string using the `options` object.
         * 
         * @param {string} key - The key to retrieve the ARIA label for.
         * @param {string} [defaultValue] - The default value to use if the key is not found.
         * @param {unknown} [options] - An object containing placeholder replacements in the format `{placeholderKey: replacementValue}`.
         * @returns {string} - The localized ARIA label, with placeholders replaced by their corresponding values from `options` if provided.
         */
        getAriaLabel: function(key, defaultValue, options) {
            var ariaLocaleSettings = this.getLocaleSettings()['aria'] || {};
            var label = ariaLocaleSettings[key] || PrimeFaces.locales['en_US']['aria'][key] || defaultValue || "???" + key + "???";
            if (options) {
                for (const valKey in options) {
                    label = label.replace(`{${valKey}}`, options[valKey]);
                }
            }
            return label.trim();
        },

        /**
         * Attempt to look up the locale key by current locale and fall back to US English if not found.
         * @param {string} key The locale key
         * @return {string} The translation for the given key
         */
        getLocaleLabel: function(key) {
            var locale = this.getLocaleSettings();
            return (locale&&locale[key]) ? locale[key] : PrimeFaces.locales['en_US'][key];
        },
        
        /**
         * Loop over all locales and set the label to the new value in all locales.
         * @param {string} localeKey The locale key
         * @param {string} localeValue The locale value
         */
        setGlobalLocaleValue: function(localeKey, localeValue) {
            // Recursive function to iterate over nested objects
            function iterateLocale(locale, lkey, lvalue) {
                for (var key in locale) {
                    if (typeof locale[key] === 'object') {
                        // If the value is an object, call the function recursively
                        iterateLocale(locale[key], lkey, lvalue);
                    } 
                    // Otherwise, set the new value if found
                    else if (key === lkey) {
                        locale[key] = lvalue;
                    }
                }
            }

            // iterate over all locales and try and set the key in each locale
            for (var lang in PrimeFaces.locales) {
                if (typeof PrimeFaces.locales[lang] === 'object') {
                    iterateLocale(PrimeFaces.locales[lang], localeKey, localeValue)
                }
            }
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
         * Uses crypto.randomUUID() if available, otherwise falls back to a custom implementation.
         *
         * See https://www.ietf.org/rfc/rfc4122.txt
         *
         * @return {string} A random UUID.
         */
        uuid: function() {
            if (typeof crypto.randomUUID === 'function') {
                return crypto.randomUUID();
            }
            else {
                return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
                    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
                );
            }
        },

        /**
         * Increment and return the next `z-index` for CSS as a string. If an element is provided, apply the new
         * `z-index` to it.
         * Note that jQuery will no longer accept numeric values in {@link JQuery.css | $.fn.css} as of version 4.0.
         *
         * @param {JQuery} element Element to apply new `z-index` to.
         * @return {string} the next `z-index` as a string.
         */
        nextZindex: function(element) {
            var zIndex = String(++PrimeFaces.zindex);
            if (element) {
                element.css('z-index', zIndex);
            }
            return zIndex;
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
         * Converts the provided string to searchable form.
         * 
         * @param {string} string to normalize.
         * @param {boolean} lowercase flag indicating whether the string should be lower cased.
         * @param {boolean} normalize flag indicating whether the string should be normalized (accents to be removed
         * from characters).
         * @returns {string} searchable string.
         */
        toSearchable: function(string, lowercase, normalize) {
            if (!string) return '';
            var result = normalize ? string.normalize('NFD').replace(/[\u0300-\u036f]/g, '') : string;
            return lowercase ? result.toLowerCase() : result;
        },

        /**
         * Reset any state variables on update="@all".
         */
        resetState: function() {
            // terminate all AJAX requests, pollers, etc
            PrimeFaces.utils.killswitch();

            PrimeFaces.zindex = 1000;
            PrimeFaces.detachedWidgets = [];
            PrimeFaces.animationActive = false;
            PrimeFaces.customFocus = false;
            PrimeFaces.widgets = {};            
        },

        /**
         * Queue a microtask if delay is 0 or less and setTimeout if > 0.
         *
         * @param {() => void} fn the function to call after the delay
         * @param {number | undefined} [delay] the optional delay in milliseconds
         * @return {number | undefined} the id associated to the timeout or undefined if no timeout used
         */
        queueTask: function(fn, delay) {
            return PrimeFaces.utils.queueTask(fn, delay);
        },

        /**
         * Creates a debounced version of the provided function that delays invoking the function until after the specified delay.
         * The debounced function will only execute once the delay has elapsed and no additional function calls were made.
         * Each new function call resets the delay timer.
         * 
         * @param {() => void} fn The function to debounce
         * @param {number} [delay=400] The number of milliseconds to delay. Defaults to 400ms. Negative values are coerced to 400ms.
         */
        debounce: function(fn, delay = 400) {
            if (delay < 0) {
                delay = 400;
            }

            if (PrimeFaces.debounceTimer) {
                clearTimeout(PrimeFaces.debounceTimer);
            }

            PrimeFaces.debounceTimer = PrimeFaces.queueTask(function() {
                fn();
                PrimeFaces.debounceTimer = null;
            }, delay);
        },

        /**
         * Returns the current PrimeFaces and jQuery version as a string and logs it to the console.
         * @return {string} The current PrimeFaces and jQuery version as a string.
         */
        version: function() {
            var version = 'PrimeFaces ' + this.VERSION + ' (jQuery ' + jQuery.fn.jquery + ' / UI ' + $.ui.version + ')';
            console.log(version);
            return version;
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
         * PrimeFaces per defaults hides all overlays on scrolling/resizing to avoid positioning problems.
         * This is really hard to overcome in selenium tests and we can disable this behavior with this setting.
         * @type {boolean}
         */
        hideOverlaysOnViewportChange : true,

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
        RESET_VALUES_PARAM : "javax.faces.partial.resetValues",

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
     *
     * @prop {string} contextPath The current servlet-context path.
     * @readonly contextPath
     *
     * @prop {boolean} cookiesSecure If cookies are secured.
     * @readonly cookiesSecure
     *
     * @prop {string} cookiesSameSite The cookies same site.
     * @readonly cookiesSameSite
     *
     * @prop {boolean} earlyPostParamEvaluation If AJAX post params are evaluated early.
     * @readonly earlyPostParamEvaluation
     *
     * @prop {boolean} partialSubmit If AJAX partial-submit is enabled.
     * @readonly partialSubmit
     *
     * @prop {string} projectStage The Faces ProjectStage.
     * @readonly projectStage
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
            "accept": "Yes",
            "addRule": "Add Rule",
            "am": "AM",
            "apply": "Apply",
            "cancel": "Cancel",
            "choose": "Choose",
            "chooseDate": "Choose Date",
            "chooseMonth": "Choose Month",
            "chooseYear": "Choose Year",
            "clear": "Clear",
            "completed": "Completed",
            "contains": "Contains",
            "custom": "Custom",
            "dateAfter": "Date is after",
            "dateBefore": "Date is before",
            "dateFormat": "mm/dd/yy",
            "dateIs": "Date is",
            "dateIsNot": "Date is not",
            "dayNames": ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
            "dayNamesMin": ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
            "dayNamesShort": ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            "emptyFilterMessage": "No results found",
            "emptyMessage": "No available options",
            "emptySearchMessage": "No results found",
            "emptySelectionMessage": "No selected item",
            "endsWith": "Ends with",
            "equals": "Equals",
            "fileSizeTypes": ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
            "filter": "Filter",
            "firstDayOfWeek": 0,
            "gt": "Greater than",
            "gte": "Greater than or equal to",
            "lt": "Less than",
            "lte": "Less than or equal to",
            "matchAll": "Match All",
            "matchAny": "Match Any",
            "medium": "Medium",
            "monthNames": ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            "monthNamesShort": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            "nextDecade": "Next Decade",
            "nextHour": "Next Hour",
            "nextMinute": "Next Minute",
            "nextMonth": "Next Month",
            "nextSecond": "Next Second",
            "nextYear": "Next Year",
            "noFilter": "No Filter",
            "notContains": "Not contains",
            "notEquals": "Not equals",
            "now": "Now",
            "passwordPrompt": "Enter a password",
            "pending": "Pending",
            "pm": "PM",
            "prevDecade": "Previous Decade",
            "prevHour": "Previous Hour",
            "prevMinute": "Previous Minute",
            "prevMonth": "Previous Month",
            "prevSecond": "Previous Second",
            "prevYear": "Previous Year",
            "reject": "No",
            "removeRule": "Remove Rule",
            "searchMessage": "{0} results are available",
            "selectionMessage": "{0} items selected",
            "showMonthAfterYear": false,
            "startsWith": "Starts with",
            "strong": "Strong",
            "today": "Today",
            "upload": "Upload",
            "weak": "Weak",
            "weekHeader": "Wk",
            "weekNumberTitle": "W",
            "isRTL": false,
            "yearSuffix": "",
            "timeOnlyTitle": "Only Time",
            "timeText": "Time",
            "hourText": "Hour",
            "minuteText": "Minute",
            "secondText": "Second",
            "millisecondText": "Millisecond",
            "year": "Year",
            "month": "Month",
            "week": "Week",
            "day": "Day",
            "list": "Agenda",
            "allDayText": "All Day",
            "moreLinkText": "More...",
            "noEventsText": "No Events",
            "aria": {
                "cancelEdit": "Cancel Edit",
                "close": "Close",
                "collapseLabel": "Collapse",
                "collapseRow": "Row Collapsed",
                "editRow": "Edit Row",
                "expandLabel": "Expand",
                "expandRow": "Row Expanded",
                "falseLabel": "False",
                "filterConstraint": "Filter Constraint",
                "filterOperator": "Filter Operator",
                "firstPageLabel": "First Page",
                "gridView": "Grid View",
                "hideFilterMenu": "Hide Filter Menu",
                "jumpToPageDropdownLabel": "Jump to Page Dropdown",
                "jumpToPageInputLabel": "Jump to Page Input",
                "lastPageLabel": "Last Page",
                "listLabel": "Options List",
                "listView": "List View",
                "moveAllToSource": "Move All to Source",
                "moveAllToTarget": "Move All to Target",
                "moveBottom": "Move Bottom",
                "moveDown": "Move Down",
                "moveToSource": "Move to Source",
                "moveToTarget": "Move to Target",
                "moveTop": "Move Top",
                "moveUp": "Move Up",
                "navigation": "Navigation",
                "next": "Next",
                "nextPageLabel": "Next Page",
                "nullLabel": "Not Selected",
                "pageLabel": "Page {page}",
                "otpLabel": "Please enter one time password character {0}",
                "passwordHide": "Hide Password",
                "passwordShow": "Show Password",
                "previous": "Previous",
                "prevPageLabel": "Previous Page",
                "rotateLeft": "Rotate Left",
                "rotateRight": "Rotate Right",
                "rowsPerPageLabel": "Rows per page",
                "saveEdit": "Save Edit",
                "scrollTop": "Scroll Top",
                "selectAll": "All items selected",
                "selectLabel": "Select",
                "selectRow": "Row Selected",
                "showFilterMenu": "Show Filter Menu",
                "slide": "Slide",
                "slideNumber": "{slideNumber}",
                "star": "1 star",
                "stars": "{star} stars",
                "trueLabel": "True",
                "unselectAll": "All items unselected",
                "unselectLabel": "Unselect",
                "unselectRow": "Row Unselected",
                "zoomImage": "Zoom Image",
                "zoomIn": "Zoom In",
                "zoomOut": "Zoom Out",
                "datatable.sort.ASC": "activate to sort column ascending",
                "datatable.sort.DESC": "activate to sort column descending",
                "datatable.sort.NONE": "activate to remove sorting on column",
                "colorpicker.OPEN": "Open color picker",
                "colorpicker.CLOSE": "Close color picker",
                "colorpicker.CLEAR": "Clear the selected color",
                "colorpicker.MARKER": "Saturation: {s}. Brightness: {v}.",
                "colorpicker.HUESLIDER": "Hue slider",
                "colorpicker.ALPHASLIDER": "Opacity slider",
                "colorpicker.INPUT": "Color value field",
                "colorpicker.FORMAT": "Color format",
                "colorpicker.SWATCH": "Color swatch",
                "colorpicker.INSTRUCTION": "Saturation and brightness selector. Use up, down, left and right arrow keys to select.",
                "spinner.INCREASE": "Increase Value",
                "spinner.DECREASE": "Decrease Value",
                "switch.ON": "On",
                "switch.OFF": "Off",
                "messages.ERROR": "Error",
                "messages.FATAL": "Fatal",
                "messages.INFO": "Information",
                "messages.WARN": "Warning"
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
