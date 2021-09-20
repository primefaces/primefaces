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
            this.mobile = (this.browser.mobile) ? true : false;
            this.touch = 'ontouchstart' in window || window.navigator.msMaxTouchPoints || PrimeFaces.env.mobile;
            this.ios = /iPhone|iPad|iPod/i.test(window.navigator.userAgent) || (/mac/i.test(window.navigator.userAgent) && PrimeFaces.env.touch);
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
