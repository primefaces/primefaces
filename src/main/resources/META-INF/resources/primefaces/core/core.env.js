if (!PrimeFaces.env) {

    /**
     * The object with functionality related to the browser environment, such as information about the current browser.
     * @namespace
     */
    PrimeFaces.env = {

        /**
         * `true` if the current browser is a mobile browser, `false` otherwise.
         * @type {boolean}
         */
        mobile : false,
        /**
         * `true` if the current browser supports touch, `false` otherwise.
         * @type {boolean}
         */
        touch : false,
        /**
         * `true` if the current browser is an IOS browser, `false` otherwise.
         * @type {boolean}
         */
        ios: false,
        /**
         * The current browser type.
         * @type {string}
         */
        browser : null,

        /**
         * Initializes the environment by reading the browser enviroment.
         */
        init : function() {
            this.mobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(window.navigator.userAgent);
            this.touch = 'ontouchstart' in window || window.navigator.msMaxTouchPoints || PrimeFaces.env.mobile;
            this.ios = /iPhone|iPad|iPod/i.test(window.navigator.userAgent);

            this.resolveUserAgent();
        },
        
        /**
         * Adapted from jquery browser plugin. Resolves the current user agent string to a browser type and saves
         * the information in the global `$.browser` object.
         */
        resolveUserAgent: function() { 
            if($.browser) {
                this.browser = $.browser;
            }
            else {
                var matched, browser;

                jQuery.uaMatch = function( ua ) {
                    ua = ua.toLowerCase();

                    var match = /(opr)[\/]([\w.]+)/.exec( ua ) ||
                        /(chrome)[ \/]([\w.]+)/.exec( ua ) ||
                        /(version)[ \/]([\w.]+).*(safari)[ \/]([\w.]+)/.exec( ua ) ||
                        /(webkit)[ \/]([\w.]+)/.exec( ua ) ||
                        /(opera)(?:.*version|)[ \/]([\w.]+)/.exec( ua ) ||
                        /(msie) ([\w.]+)/.exec( ua ) ||
                        ua.indexOf("trident") >= 0 && /(rv)(?::| )([\w.]+)/.exec( ua ) ||
                        ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec( ua ) ||
                        [];

                    var platform_match = /(ipad)/.exec( ua ) ||
                        /(iphone)/.exec( ua ) ||
                        /(android)/.exec( ua ) ||
                        /(windows phone)/.exec( ua ) ||
                        /(win)/.exec( ua ) ||
                        /(mac)/.exec( ua ) ||
                        /(linux)/.exec( ua ) ||
                        /(cros)/i.exec( ua ) ||
                        [];

                    return {
                        browser: match[ 3 ] || match[ 1 ] || "",
                        version: match[ 2 ] || "0",
                        platform: platform_match[ 0 ] || ""
                    };
                };

                matched = jQuery.uaMatch( window.navigator.userAgent );
                browser = {};

                if ( matched.browser ) {
                    browser[ matched.browser ] = true;
                    browser.version = matched.version;
                    browser.versionNumber = parseInt(matched.version);
                }

                if ( matched.platform ) {
                    browser[ matched.platform ] = true;
                }

                // These are all considered mobile platforms, meaning they run a mobile browser
                if ( browser.android || browser.ipad || browser.iphone || browser[ "windows phone" ] ) {
                    browser.mobile = true;
                }

                // These are all considered desktop platforms, meaning they run a desktop browser
                if ( browser.cros || browser.mac || browser.linux || browser.win ) {
                    browser.desktop = true;
                }

                // Chrome, Opera 15+ and Safari are webkit based browsers
                if ( browser.chrome || browser.opr || browser.safari ) {
                    browser.webkit = true;
                }

                // IE11 has a new token so we will assign it msie to avoid breaking changes
                if ( browser.rv )
                {
                    var ie = "msie";

                    matched.browser = ie;
                    browser[ie] = true;
                }

                // Opera 15+ are identified as opr
                if ( browser.opr )
                {
                    var opera = "opera";

                    matched.browser = opera;
                    browser[opera] = true;
                }

                // Stock Android browsers are marked as Safari on Android.
                if ( browser.safari && browser.android )
                {
                    var android = "android";

                    matched.browser = android;
                    browser[android] = true;
                }

                // Assign the name and platform variable
                browser.name = matched.browser;
                browser.platform = matched.platform;

                this.browser = browser;
                $.browser = browser;
            }
        },

        /**
         * Checks whether the current browser is the Internet Explorer, and optionally also whether it is a certain
         * version of Internet Explorer.
         * @param {1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11} [version] Version of IE to check for. If not given,
         * checks for any version of Internet Explorer.
         * @return {boolean} `true` if the current browser is the given version Internet Explorer, or `false` otherwise.
         */
        isIE: function(version) {
            return (version === undefined) ? this.browser.msie: (this.browser.msie && parseInt(this.browser.version, 10) === version);
        },

        /**
         * Checks whether the current browser is the Internet Explorer, and whether its version is less than the given
         * version.
         * @param {1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11} version Version of IE to check for. If not given,
         * checks for any version of Internet Explorer.
         * @return {boolean} `true` if the current browser is the Internet Explorer and its version is less than the
         * given version.
         */
        isLtIE: function(version) {
            return (this.browser.msie) ? parseInt(this.browser.version, 10) < version : false;
        }
    };

    PrimeFaces.env.init();

};