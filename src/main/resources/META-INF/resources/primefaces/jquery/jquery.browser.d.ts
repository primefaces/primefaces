/**
 * Namespace for the cookie JQueryUI plugin, available as `$.browser`.
 * 
 * Contains some additional types and interfaces required for the typings.
 */
declare namespace JQueryBrowser {
    export interface BrowserInformation {
        /**
         * `true` if the current browser is an Android browser, `false` otherwise.
         */
        android?: boolean;
        /**
         * `true` if the current browser is a desktop browser, `false` otherwise.
         */
        desktop?: boolean;
        /**
         * `true` if the current browser is a mobile browser, `false` otherwise.
         */
        mobile?: boolean;
        /**
         * `true` if the current browser is the Internet Explorer browser, `false` otherwise.
         */
        msie?: boolean;
        /**
         * The name of the current browser.
         */
        name: string;
        /**
         * `true` if the current browser is the Opera browser, `false` otherwise.
         */
        opera?: boolean;
        /**
         * The current platfrm (operating system).
         */
        platform: string;
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
         * Additional browser types or platforms and whether they match the current enviroment.
         */
        [typeOrPlatform: string]: boolean | string | number | undefined;
    }
}

interface JQueryStatic {
    /**
     * Contains information about the current browser environment, such as which browser the user is using etc.
     */
    browser: JQueryBrowser.BrowserInformation;
}