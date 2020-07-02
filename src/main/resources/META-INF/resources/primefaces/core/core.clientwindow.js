if (!PrimeFaces.clientwindow) {

    /**
     * The object with functionality related to multiple window support in PrimeFaces applications.
     * 
     * @namespace
     */
    PrimeFaces.clientwindow = {

        /**
         * The name of the URL parameter holding the client window ID.
         * @type {string}
         * @readonly
         */
        CLIENT_WINDOW_URL_PARAM : "jfwid",

        /**
         * The key for the session storage entry holding the client window ID.
         * @type {string}
         * @readonly
         */
        CLIENT_WINDOW_SESSION_STORAGE : "pf.windowId",

        /**
         * The value of the temporary client window ID, used for requesting a new ID, see
         * {@link requestNewClientWindowId}.
         * @type {string}
         * @readonly
         */
        TEMP_CLIENT_WINDOW_ID : "temp",

        /**
         * The number of characters of the client window ID. Each client window ID must be of this length, or it is
         * invalid.
         * @type {number}
         * @readonly
         */
        LENGTH_CLIENT_WINDOW_ID : 5,

        /**
         * Whether the {@link init} function was called already.
         * @type {boolean}
         */
        initialized : false,

        /**
         * The current window ID, as received from the server. May be `null` when to ID was provided.
         * @type {null | string}
         */
        clientWindowId : null,

        /**
         * Whether the currently loaded page is from the first redirect.
         * @type {boolean}
         */
        initialRedirect : false,

        /**
         * Initializes the client window feature. Usually invoked on page load. This method should only be called once
         * per page.
         * @param {string} clientWindowId The current client window ID.
         * @param {boolean} initialRedirect Whether the currently loaded page is from the first redirect.
         */
        init: function(clientWindowId, initialRedirect) {
            if (PrimeFaces.clientwindow.initialized === true) {
                return;
            }

            this.initialized = true;

            this.clientWindowId = clientWindowId;
            this.initialRedirect = initialRedirect;

            this.cleanupCookies();
            this.assertClientWindowId();
        },

        /**
         * Makes sure the temporary cookie for the client window ID is expired.
         */
        cleanupCookies : function() {
            var urlWindowId = this.getUrlParameter(window.location.href, this.CLIENT_WINDOW_URL_PARAM);
            if (urlWindowId) {
                this.expireCookie('pf.initialredirect-' + urlWindowId);
            }
        },

        /**
         * Checks whether the client window ID is valid. If not, requests a new client window ID from the server via
         * reloading the current page.
         */
        assertClientWindowId: function() {
            var urlClientWindowId = this.getUrlParameter(window.location.href, this.CLIENT_WINDOW_URL_PARAM);
            var sessionStorageClientWindowId = sessionStorage.getItem(this.CLIENT_WINDOW_SESSION_STORAGE);

            // session story empty -> "open in new tab/window" was used
            if (sessionStorageClientWindowId === null) {
                // initial redirect
                // -> the windowId is valid - we don't need to a second request
                if (this.initialRedirect && urlClientWindowId === this.clientWindowId) {
                    sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.clientWindowId);
                }
                // != initial redirect
                // -> request a new windowId to avoid multiple tabs with the same windowId
                else {
                    this.requestNewClientWindowId();
                }
            }
            else {
                // we triggered the windowId recreation last request
                if (sessionStorageClientWindowId === this.TEMP_CLIENT_WINDOW_ID) {
                    sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.clientWindowId);
                }
                // security check length
                else if (sessionStorageClientWindowId.length !== this.LENGTH_CLIENT_WINDOW_ID) {
                    this.requestNewClientWindowId();
                }
                // session storage windowId doesn't match requested windowId
                // -> redirect to the same view with current windowId from the window name
                else if (sessionStorageClientWindowId !== urlClientWindowId || sessionStorageClientWindowId !== this.clientWindowId) {
                    window.location = this.replaceUrlParam(window.location.href, this.CLIENT_WINDOW_URL_PARAM, sessionStorageClientWindowId);
                }
            }
        },
        
        /**
         * Expires the current client window ID by replacing it with a temporary, invalid client window ID. Then reloads
         * the current page to request a new ID from the server.
         */
        requestNewClientWindowId : function() {
            sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.TEMP_CLIENT_WINDOW_ID);
            
            // we remove the windowId if available and redirect to the same url again to create a new windowId
            window.location = this.replaceUrlParam(window.location.href, this.CLIENT_WINDOW_URL_PARAM, null);
        },

        /**
         * Returns the value of the URL parameter with the given name. When the URL contains multiple URL parameters
         * with the same name, the value of the first URL parameter is returned.
         * @param {string} uri An URL from which to extract an URL parameter.
         * @param {string} name Name of the URL parameter to retrieve.
         * @return {string | null} The value of the given URL parameter. Returns the empty string when the URL parameter
         * is present, but has no value. Returns `null` when no URL parameter with the given name exists.
         */
        getUrlParameter : function(uri, name) {
             // create an anchor object with the uri and let the browser parse it
             var a = document.createElement('a');
             a.href = uri;

             // check if a query string is available
             var queryString = a.search;
             if (queryString && queryString.length > 0) {
                 // create an array of query parameters - substring(1) removes the ? at the beginning of the query
                 var queryParameters = queryString.substring(1).split("&");
                 for (var i = 0; i < queryParameters.length; i++) {
                     var queryParameter = queryParameters[i].split("=");
                     if (queryParameter[0] === name) {
                         return queryParameter.length > 1 ? decodeURIComponent(queryParameter[1]) : "";
                     }
                 }
             }

             return null;
        },

        /**
         * Given a URL, removes all URL parameters with the given name, adds a new URL parameter with the given value,
         * and returns the new URL with the replaced parameter. If the URL contains multiple URL parameters with the
         * same name, they are all removed.
         * @param {string} uri The URL for which to change an URL parameter.
         * @param {string} parameterName Name of the URL parameter to change.
         * @param {string | null} [parameterValue] New value for the URL parameter. If `null` or not given, the empty
         * string is used.
         * @return {string} The given URL, but with value of the given URL parameter changed to the new value.
         */
        replaceUrlParam : function(uri, parameterName, parameterValue) {
            var a = document.createElement('a');
            a.href = uri;

            // set empty string as value if not defined or empty
            if (!parameterValue || parameterValue.replace(/^\s+|\s+$/g, '').length === 0) {
                parameterValue = '';
            }

            // check if value is empty
            if (parameterValue.length === 0) {

                // both value and query string is empty (or doesn't contain the param), don't touch the url
                if (a.search.length === 0 || a.search.indexOf(parameterName + "=") === -1) {
                    return a.href;
                }
            }

            // query string is empty, just append our new parameter
            if (a.search.length === 0) {
                a.search = '?' + encodeURIComponent(parameterName) + "=" + encodeURIComponent(parameterValue);

                return a.href;
            }

            var oldParameters = a.search.substring(1).split('&');
            var newParameters = [];
            newParameters.push(parameterName + "=" + encodeURIComponent(parameterValue));

            // loop old parameters, remove empty ones and remove the parameter with the same name as the new one
            for (var i = 0; i < oldParameters.length; i++) {
                var oldParameterPair = oldParameters[i];

                if (oldParameterPair.length > 0) {
                    var oldParameterName = oldParameterPair.split('=')[0];
                    var oldParameterValue = oldParameterPair.split('=')[1];

                    // don't add empty parameters again
                    if (oldParameterValue && oldParameterValue.replace(/^\s+|\s+$/g, '').length > 0) {
                        // skip the the old parameter if it's the same as the new parameter
                        if (oldParameterName !== parameterName) {
                            newParameters.push(oldParameterName + "=" + oldParameterValue);
                        }
                    }
                }
            }

            // join new parameters
            a.search = '?' + newParameters.join('&');

            return a.href;
        },

        /**
         * Expires the cookie with the given name by setting a cookie with the appropriate `max-age` and `expires`
         * settings.
         * @param {string} cookieName Name of the cookie to expire.
         */
        expireCookie : function(cookieName) {
            PrimeFaces.setCookie(cookieName, 'true', { path: '/', expires: -10, 'max-age': '0' });
        }
    };
}	
