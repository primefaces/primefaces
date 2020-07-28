/**
 * Namespace for the jQuery.print plugin. Contains some required types and interfaces.
 * 
 * jQuery.print is a plugin for printing specific parts of a page.
 * 
 * See https://github.com/DoersGuild/jQuery.print
 */
declare namespace JQueryPrint {
    /**
     * Settings for the jQuery.print plugin. 
     */
    export interface PrintSettings {
        /**
         * Whether or not the styles from the parent document should be included.
         */
        globalStyles: boolean;

        /**
         * Whether or not link tags with `media='print'` should be included; overridden by the {@link globalStyles}.
         * option.
         */
        mediaPrint: boolean;

        /**
         * URL of an external stylesheet to be included.
         */
        stylesheet: string | null;

        /**
         * A selector for the items that are to be excluded from printing.
         */
        noPrintSelector: string;

        /**
         * Whether to print from an iframe instead of a pop-up window; can take the CSS selector of an existing iframe.
         */
        iframe: string | boolean;

        /**
         * Adds custom HTML after the selected content.
         */
        append: string | JQuery | null;

        /**
         * Adds custom HTML before the selected content.
         */
        prepend: string | JQuery | null;

        /**
         * Should it copy user-updated form input values onto the printed markup (this is done by manually iterating over
         * each form element).
         */
        manuallyCopyFormValues: boolean;

        /**
         * A jQuery deferred object that is resolved once the print function is called. Can be used to setup callbacks.
         */
        deferred: JQuery.Deferred<void>;

        /**
         * To change the amount of max time to wait for the content, etc to load before printing the element from the
         * new window or iframe created, as a fallback if the load event for the new window or iframe has not fired yet.
         */
        timeout: number;

        /**
         * To change the printed title. Must be a single line.
         */
        title: string | null;

        /**
         * To prepend a doctype to the printed document frame.
         */
        doctype: string;
    }
}

interface JQuery {
    /**
     * Prints the currently selected element.
     * @param settings Optional settings for printing.
     * @return This jQuery instance for chaining.
     */
    print(settings?: Partial<JQueryPrint.PrintSettings>): this;
}

interface JQueryStatic {
    /**
     * Prints the currently selected element.
     * @param selector CSS selector for the element to print.
     * @param settings Optional settings for printing.
     * @return This jQuery instance for chaining.
     */
    print(selector: string, settings?: Partial<JQueryPrint.PrintSettings>): this;
}