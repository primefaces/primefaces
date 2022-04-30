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
         * `true` if the user's current OS setting prefers dark mode, `false` otherwise.
         * @type {boolean}
         */
        preferredColorSchemeDark : false,
        /**
         * `true` if the user's current OS setting prefers light mode, `false` otherwise.
         * @type {boolean}
         */
        preferredColorSchemeLight : false,

        /**
         * Initializes the environment by reading the browser environment.
         */
        init : function() {
            this.browser = $.browser;
            this.mobile = (this.browser.mobile) ? true : false;
            this.touch = 'ontouchstart' in window || window.navigator.msMaxTouchPoints || PrimeFaces.env.mobile;
            this.ios = /iPhone|iPad|iPod/i.test(window.navigator.userAgent) || (/mac/i.test(window.navigator.userAgent) && PrimeFaces.env.touch);
            this.preferredColorSchemeDark = PrimeFaces.env.evaluateMediaQuery('(prefers-color-scheme: dark)');
            this.preferredColorSchemeLight = !this.preferredModeDark;
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
         * A widget is touch enabled if the browser supports touch AND the widget has the touchable property enabled.
         * The default will be true if it widget status can't be determined.
         * 
         * @param {PrimeFaces.widget.BaseWidgetCfg} cfg the widget configuration
         * @return {boolean} true if touch is enabled, false if disabled
         */
        isTouchable: function(cfg) {
            var widgetTouchable = (cfg == undefined) || (cfg.touchable != undefined ? cfg.touchable : true);
            return PrimeFaces.env.touch && widgetTouchable;
        },

        /**
         * Gets the user's preferred color scheme set in their operating system.
         * 
         * @return {string} either 'dark' or 'light'
         */
        getOSPreferredColorScheme: function() {
            return PrimeFaces.env.preferredColorSchemeLight ? 'light' : 'dark';
        },

       /**
         * Based on the current PrimeFaces theme determine if light or dark contrast is being applied.
         * 
         * @return {string} either 'dark' or 'light'
         */
        getThemeContrast: function() {
            var theme = PrimeFaces.env.getTheme();
            var darkRegex = /(^(arya|vela|.+-(dim|dark))$)/gm;
            return darkRegex.test(theme) ? 'dark' : 'light';
        },
        
        /**
         * Evaluate a media query and return true/false if its a match.
         *
         * @param {string} mediaquery the media query to evaluate
         * @return {boolean} true if it matches the query false if not
         */
        evaluateMediaQuery: function(mediaquery) {
            return window.matchMedia && window.matchMedia(mediaquery).matches;
        },

        /**
         * Media query to determine if screen size is below pixel count.
         * @param {number} pixels the number of pixels to check
         * @return {boolean} true if screen is less than number of pixels
         */
        isScreenSizeLessThan: function(pixels) {
            return PrimeFaces.env.evaluateMediaQuery('(max-width: ' + pixels + 'px)');
        },

        /**
         * Media query to determine if screen size is above pixel count.
         * @param {number} pixels the number of pixels to check
         * @return {boolean} true if screen is greater than number of pixels
         */
        isScreenSizeGreaterThan: function(pixels) {
            return PrimeFaces.env.evaluateMediaQuery('(min-width: ' + pixels + 'px)');
        }
    };

    PrimeFaces.env.init();

};
