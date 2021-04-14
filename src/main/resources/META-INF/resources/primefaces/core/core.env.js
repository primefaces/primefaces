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
         * Initializes the environment by reading the browser environment.
         */
        init : function() {
            this.browser = $.browser;
            this.mobile = this.browser.mobile;
            this.touch = 'ontouchstart' in window || window.navigator.msMaxTouchPoints || PrimeFaces.env.mobile;
            this.ios = /iPhone|iPad|iPod/i.test(window.navigator.userAgent) || (/mac/i.test(window.navigator.userAgent) && PrimeFaces.env.touch);
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
         * A widget is touch enabled if the browser supports touch AND the widget has the touchable property enabled.
         * The default will be true if it widget status can't be determined.
         * 
         * @param {PrimeFaces.widget.BaseWidgetCfg} cfg the widget configuration
         * @return {boolean} true if touch is enabled, false if disabled
         */
        isTouchable: function(cfg) {
            var widgetTouchable = (cfg == undefined) || (cfg.touchable != undefined ? cfg.touchable : true);
            return PrimeFaces.env.touch && widgetTouchable;
        }
    };

    PrimeFaces.env.init();

};
