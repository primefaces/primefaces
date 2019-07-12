if (!PrimeFaces.env) {

    PrimeFaces.env = {

        mobile : false,
        touch : false,
        ios: false,
        browser : null,

        init : function() {
            this.mobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(window.navigator.userAgent);
            this.touch = 'ontouchstart' in window || window.navigator.msMaxTouchPoints || PrimeFaces.env.mobile;
            this.ios = /iPhone|iPad|iPod/i.test(window.navigator.userAgent);

            this.resolveUserAgent();
        },
        
        //adapted from jquery browser plugin
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

        isIE: function(version) {
            return (version === undefined) ? this.browser.msie: (this.browser.msie && parseInt(this.browser.version, 10) === version);
        },

        isLtIE: function(version) {
            return (this.browser.msie) ? parseInt(this.browser.version, 10) < version : false;
        },

        /**
         * Get the current media size of the browser.
         *
         * @return int mode represented in 5 down to 0 from largest to smallest
         */
        getMediaSize : function() {
            var size;
            if (window.matchMedia('(min-width: 960px) and (max-width:1200px)').matches) {
                /* Large desktop */
                size = 5;
            } else if (window.matchMedia('(min-width: 960px)').matches) {
                /* Intermediate sized desktop */   
               size = 4;
            } else if (window.matchMedia('(min-width: 640px) and (max-width: 960px)').matches) {   
                /* Portrait tablet to landscape and small desktop */
                size = 3
            } else if (window.matchMedia('(min-width:480px) and (max-width: 640px)').matches) {   
                /* Landscape phone to portrait tablet */
                size = 2;
            } else  if (window.matchMedia('(min-width:320px) and (max-width: 480px)').matches) {
                /* Portrait phones and down */
                size = 1;
            } else {
                /* Extremely small */
                size = 0;
            }
            return size;
        }
    };

    PrimeFaces.env.init();

};