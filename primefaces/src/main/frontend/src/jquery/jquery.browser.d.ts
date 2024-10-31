/**
 * Namespace for the Browser JQueryUI plugin, available as `$.browser`.
 * 
 * Contains some additional types and interfaces required for the typings.
 */
declare namespace JQueryBrowser {
    /**
     * Represents the retrieved information about the current browser, such as the type of browser and the OS.
     */
    export interface BrowserInformation {
        /**
         * `true` if the current browser is an Android browser, `false` otherwise.
         */
        android?: boolean;

        /**
         * `true` if the current browser is a BB browser, `false` otherwise.
         */
        bb?: boolean;

        /**
         * `true` if the current browser is a Blackberry browser, `false` otherwise.
         */
        blackberry?: boolean;

        /**
         * `true` if the current browser is a Chrome browser, `false` otherwise.
         */
        chrome?: boolean;

        /**
         * `true` if the current browser is a Chromium OS browser, `false` otherwise.
         */
        cros?: boolean;

        /**
         * `true` if the current browser is a desktop browser, `false` otherwise.
         */
        desktop?: boolean;

        /**
         * `true` if the current browser is an Edge browser, `false` otherwise.
         */
        edge?: boolean;

        /**
         * `true` if the current browser is an IE mobile browser, `false` otherwise.
         */
        iemobile?: boolean;

        /**
         * `true` if the current browser is an iPad browser, `false` otherwise.
         */
        ipad?: boolean;

        /**
         * `true` if the current browser is an iPhone browser, `false` otherwise.
         */
        iphone?: boolean;

        /**
         * `true` if the current browser is an iPod browser, `false` otherwise.
         */
        ipod?: boolean;

        /**
         * `true` if the current browser is a Kindle browser, `false` otherwise.
         */
        kindle?: boolean;

        /**
         * `true` if the current browser is a Linux browser, `false` otherwise.
         */
        linux?: boolean;

        /**
         * `true` if the current browser is a Mac browser, `false` otherwise.
         */
        mac?: boolean;

        /**
         * `true` if the current browser is a mobile browser, `false` otherwise.
         */
        mobile?: boolean;

        /**
         * `true` if the current browser is a Mozilla browser, `false` otherwise.
         */
        mozilla?: boolean;

        /**
         * `true` if the current browser is an MS Edge browser, `false` otherwise.
         */
        msedge?: boolean;

        /**
         * `true` if the current browser is the Internet Explorer browser, `false` otherwise.
         */
        msie?: boolean;

        /**
         * The name of the current browser.
         */
        name: string;

        /**
         * `true` if the current browser is an Opera browser, `false` otherwise.
         */
        opera?: boolean;

        /**
         * `true` if the current browser is an Opr browser, `false` otherwise.
         */
        opr?: boolean;

        /**
         * The current platform (operating system).
         */
        platform: string;

        /**
         * `true` if the current browser is a PlayBook browser, `false` otherwise.
         */
        playbook?: boolean;

        /**
         * `true` if the current browser is an RV browser, `false` otherwise.
         */
        rv?: boolean;

        /**
         * `true` if the current browser is a Safari browser, `false` otherwise.
         */
        safari?: boolean;

        /**
         * `true` if the current browser is a Silk browser, `false` otherwise.
         */
        silk?: boolean;

        /**
         * The version string of the current browser.
         */
        version?: string;

        /**
         * The version number of the current browser.
         */
        versionNumber?: number;

        /**
         * `true` if the current browser is a Webkit browser, `false` otherwise.
         */
        webkit?: boolean;

        /**
         * `true` if the current browser is a Windows browser, `false` otherwise.
         */
        win?: boolean;

        /**
         * `true` if the current browser is a Windows Phone browser, `false` otherwise.
         */
        "windows phone"?: boolean;
    }

    /**
     * Analyzes the given user agent string and returns information about the browser it represents.
     * @param userAgent A user agent string, such as the one in `window.navigator.userAgent`.
     * @return Details about the given browser.
     */
    export interface BrowserInspector extends BrowserInformation {
        uaMatch(userAgent: string): BrowserInformation;
    }
}

interface JQueryStatic {
    /**
     * Contains information about the current browser environment, such as which browser the user is using etc.
     */
    browser: JQueryBrowser.BrowserInspector;
}

/**
 * Contains information about the current browser environment, such as which browser the user is using etc.
 */
declare const jQBrowser: JQueryBrowser.BrowserInspector;