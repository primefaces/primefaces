declare namespace JQueryJqPrint {
    /**
     * Settings for the jQuery JQPrint plugin. 
     */
    export interface JqPrintSettings {
        /**
         * Whether debug mode is enabled. 
         */
        debug: boolean,

        /**
         * Whether CSS declared via `<link media="print" rel="..."/>` should be applied.
         */
        importCSS: boolean,

        /**
         * Whether a fix for Opera support should be applied. 
         */
        operaSupport: boolean;

        /**
         * `true` to print the container element, or `false` to print only the children. 
         */
		printContainer: boolean,
    }

    /**
     * Contains the main function of the JQPrinter plugin and also the default settings
     */
    export interface JqPrintNamespace<TElement = HTMLElement> {
        /**
         * Prints the currently selected element.
         * @param settings Optional settings for printing.
         * @return This jQuery instance for chaining.
         */
        (settings?: Partial<JqPrintSettings>): JQuery<TElement>;

        /**
         * The default settings of the jQuery JQPrinter plugin.
         */
        defaults: JqPrintSettings;
    }
}

interface JQuery<TElement = HTMLElement> {
    /**
     * The main function of the JQPrinter plugin that prints an element. Also contains the default settings.
     */
    jqprint: JQueryJqPrint.JqPrintNamespace<TElement>;

    /**
     * Finds the outer HTML of the first element in this selection.
     * @return The outer HTML of the first element in this selection, or the empty string if this selection contains no
     * elements.
     */
    outer(): string;
}