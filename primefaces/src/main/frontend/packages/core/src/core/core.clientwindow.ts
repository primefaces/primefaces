import { core } from "./core.js";

/**
 * The class with functionality related to multiple window support in PrimeFaces applications.
 */
export class ClientWindow {
    /**
    * The name of the URL parameter holding the client window ID.
    */
    readonly CLIENT_WINDOW_URL_PARAM: string = "jfwid";
    
    /**
    * The key for the session storage entry holding the client window ID.
    */
    readonly CLIENT_WINDOW_SESSION_STORAGE: string = "pf.windowId";
    
    /**
    * The value of the temporary client window ID, used for requesting a new ID, see
    * {@link requestNewClientWindowId}.
    */
    readonly TEMP_CLIENT_WINDOW_ID: string = "temp";
    
    /**
    * The number of characters of the client window ID. Each client window ID must be of this length, or it is
    * invalid.
    */
    readonly LENGTH_CLIENT_WINDOW_ID: number = 5;
    
    /**
    * Whether the {@link init} function was called already.
    */
    initialized: boolean = false;
    
    /**
    * The current window ID, as received from the server. May be `null` when to ID was provided.
    */
    clientWindowId: null | string = null;
    
    /**
    * Whether the currently loaded page is from the first redirect.
    */
    initialRedirect: boolean = false;
    
    /**
    * Initializes the client window feature. Usually invoked on page load. This method should only be called once
    * per page.
    * @param clientWindowId The current client window ID.
    * @param initialRedirect Whether the currently loaded page is from the first redirect.
    */
    init(clientWindowId: string, initialRedirect: boolean): void {
        if (this.initialized === true) {
            return;
        }
        
        this.initialized = true;
        
        this.clientWindowId = clientWindowId;
        this.initialRedirect = initialRedirect;
        
        this.cleanupCookies();
        this.assertClientWindowId();
    }

    /**
    * Makes sure the temporary cookie for the client window ID is expired.
    */
    cleanupCookies(): void {
        var urlWindowId = this.getUrlParameter(window.location.href, this.CLIENT_WINDOW_URL_PARAM);
        if (urlWindowId) {
            this.expireCookie('pf.initialredirect-' + urlWindowId);
        }
    }
    
    /**
    * Checks whether the client window ID is valid. If not, requests a new client window ID from the server via
    * reloading the current page.
    */
    assertClientWindowId(): void {
        var urlClientWindowId = this.getUrlParameter(window.location.href, this.CLIENT_WINDOW_URL_PARAM);
        var sessionStorageClientWindowId = sessionStorage.getItem(this.CLIENT_WINDOW_SESSION_STORAGE);
        
        // session story empty -> "open in new tab/window" was used
        if (sessionStorageClientWindowId === null) {
            // initial redirect
            // -> the windowId is valid - we don't need to a second request
            if (this.initialRedirect && urlClientWindowId === this.clientWindowId) {
                sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.clientWindowId ?? "");
            }
            // != initial redirect
            // -> request a new windowId to avoid multiple tabs with the same windowId
            else {
                this.requestNewClientWindowId();
            }
        }
        else if (sessionStorageClientWindowId === this.TEMP_CLIENT_WINDOW_ID) {
            // we triggered the windowId recreation last request
            sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.clientWindowId ?? "");
        }
        else if (sessionStorageClientWindowId.length !== this.LENGTH_CLIENT_WINDOW_ID) {
            // security check length
            this.requestNewClientWindowId();
        }
        else if (sessionStorageClientWindowId !== urlClientWindowId || sessionStorageClientWindowId !== this.clientWindowId) {
            // There are subtle differences between
            // window.location = "..." and window.location.href = "..."
            // https://github.com/microsoft/TypeScript/issues/48949

            // session storage windowId doesn't match requested windowId
            // -> redirect to the same view with current windowId from the window name
            (window as Window).location = this.replaceUrlParam(window.location.href, this.CLIENT_WINDOW_URL_PARAM, sessionStorageClientWindowId);
        }
    }
    
    /**
    * Expires the current client window ID by replacing it with a temporary, invalid client window ID. Then reloads
    * the current page to request a new ID from the server.
    */
    requestNewClientWindowId(): void {
        sessionStorage.setItem(this.CLIENT_WINDOW_SESSION_STORAGE, this.TEMP_CLIENT_WINDOW_ID);
        
        // There are subtle differences between
        // window.location = "..." and window.location.href = "..."
        // https://github.com/microsoft/TypeScript/issues/48949

        // we remove the windowId if available and redirect to the same url again to create a new windowId
        (window as Window).location = this.replaceUrlParam(window.location.href, this.CLIENT_WINDOW_URL_PARAM, null);
    }
    
    /**
    * Returns the value of the URL parameter with the given name. When the URL contains multiple URL parameters
    * with the same name, the value of the first URL parameter is returned.
    * @param uri An URL from which to extract an URL parameter.
    * @param name Name of the URL parameter to retrieve.
    * @return The value of the given URL parameter. Returns the empty string when the URL parameter
    * is present, but has no value. Returns `null` when no URL parameter with the given name exists.
    */
    getUrlParameter(uri: string, name: string): string | null {
        // create an anchor object with the uri and let the browser parse it
        var a = document.createElement('a');
        a.href = uri;
        
        // check if a query string is available
        var queryString = a.search;
        if (queryString && queryString.length > 0) {
            // create an array of query parameters - substring(1) removes the ? at the beginning of the query
            var queryParameters = queryString.substring(1).split("&");
            for (const queryParameterString of queryParameters) {
                const queryParameter = queryParameterString.split("=");
                if (queryParameter[0] === name) {
                    return queryParameter.length > 1 ? decodeURIComponent(queryParameter[1] ?? "") : "";
                }
            }
        }
        
        return null;
    }
    
    /**
    * Given a URL, removes all URL parameters with the given name, adds a new URL parameter with the given value,
    * and returns the new URL with the replaced parameter. If the URL contains multiple URL parameters with the
    * same name, they are all removed.
    * @param uri The URL for which to change an URL parameter.
    * @param parameterName Name of the URL parameter to change.
    * @param parameterValue New value for the URL parameter. If `null` or not given, the empty
    * string is used.
    * @return The given URL, but with value of the given URL parameter changed to the new value.
    */
    replaceUrlParam (uri: string, parameterName: string, parameterValue?: string | null): string {
        var a = document.createElement('a');
        a.href = uri;
        
        // set empty string as value if not defined or empty
        if (!parameterValue || parameterValue.trim().length === 0) {
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
        for (const oldParameterPair of oldParameters) {
            if (oldParameterPair.length > 0) {
                var oldParameterName = oldParameterPair.split('=')[0];
                var oldParameterValue = oldParameterPair.split('=')[1];
                
                // don't add empty parameters again
                if (oldParameterValue && oldParameterValue.trim().length > 0) {
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
    }
    
    /**
    * Expires the cookie with the given name by setting a cookie with the appropriate `max-age` and `expires`
    * settings.
    * @param cookieName Name of the cookie to expire.
    */
    expireCookie(cookieName: string): void {
        core.setCookie(cookieName, 'true', { path: '/', expires: -10, 'max-age': '0' });
    }
}

/**
 * The object with functionality related to multiple window support in PrimeFaces applications.
 */
export const clientwindow: ClientWindow = new ClientWindow();
