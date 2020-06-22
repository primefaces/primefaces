if (!PrimeFaces.clientwindow) {

    /**
     * 
     * @namespace
     */
    PrimeFaces.clientwindow = {

        /**
         * 
         * @type {string}
         * @readonly
         */
        CLIENT_WINDOW_URL_PARAM : "jfwid",

        /**
         * 
         * @type {string}
         * @readonly
         */
        CLIENT_WINDOW_SESSION_STORAGE : "pf.windowId",

        /**
         * 
         * @type {string}
         * @readonly
         */
        TEMP_WINDOW_ID : "temp",

        /**
         * 
         * @type {int}
         * @readonly
         */
        LENGTH_WINDOW_ID : 5,

        initialized : false,
        windowId : null,
        initialRedirect : false,

        init: function(windowId, initialRedirect) {
            if (PrimeFaces.clientwindow.initialized === true) {
                return;
            }

            this.initialized = true;

            this.windowId = windowId;
            this.initialRedirect = initialRedirect;

            this.cleanupCookies();
            this.assertWindowId();
        },

        cleanupCookies : function() {
            var urlWindowId = this.getUrlParameter(window.location.href, this.CLIENT_WINDOW_URL_PARAM);
            if (urlWindowId) {
                this.expireCookie('pf.initialredirect-' + urlWindowId);
            }
        },

        assertWindowId: function() {
            var urlWindowId = this.getUrlParameter(window.location.href, this.CLIENT_WINDOW_URL_PARAM);
            var sessionStorageWindowId = sessionStorage.getItem(this.CLIENT_WINDOW_SESSION_STORAGE);

            // session story empty -> "open in new tab/window" was used
            if (sessionStorageWindowId === null) {
                // initial redirect
                // -> the windowId is valid - we don't need to a second request
                if (this.initialRedirect && urlWindowId === this.windowId) {
                    sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.windowId);
                }
                // != initial redirect
                // -> request a new windowId to avoid multiple tabs with the same windowId
                else {
                    this.requestNewWindowId();
                }
            }
            else {
                // we triggered the windowId recreation last request
                if (sessionStorageWindowId === this.TEMP_WINDOW_ID) {
                    sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.windowId);
                }
                // security check length
                else if (sessionStorageWindowId.length !== this.LENGTH_WINDOW_ID) {
                    this.requestNewWindowId();
                }
                // session storage windowId doesn't match requested windowId
                // -> redirect to the same view with current windowId from the window name
                else if (sessionStorageWindowId !== urlWindowId || sessionStorageWindowId !== this.windowId) {
                    window.location = this.replaceUrlParam(window.location.href, this.CLIENT_WINDOW_URL_PARAM, sessionStorageWindowId);
                }
            }
        },
        
        requestNewWindowId : function() {
            sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.TEMP_WINDOW_ID);
            
            // we remove the windowId if available and redirect to the same url again to create a new windowId
            window.location = this.replaceUrlParam(window.location.href, this.CLIENT_WINDOW_URL_PARAM, null);
        },

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

        expireCookie : function(cookieName) {
            var date = new Date();
            date.setTime(date.getTime() - (10 * 24 * 60 * 60 * 1000)); // - 10 day
            var expires = ";max-age=0;expires=" + date.toGMTString();

            document.cookie = cookieName + "=" + expires + "; path=/";
        }
    };
}