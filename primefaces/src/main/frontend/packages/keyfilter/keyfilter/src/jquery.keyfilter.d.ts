/**
 * Namespace for the jQuery keyfilter plugin.
 * 
 * KeyFilter is used to filter keyboard input on specified input components.
 */
declare namespace JQueryKeyfilter {
    /**
     * A test function that checks whether a newly entered characters is allowed or should be blocked. The this context
     * is set to the DOM element for the target input field.
     * @param character A newly entered character to check.
     * @return `true` to allow the entered character, or `false` to block it.
     */
    export type TestFunction = (this: JQuery, character: string) => boolean;

    /**
     * Default, global configuration for the key filter.
     */
    export interface Defaults {
        /**
         * A map between the built-in masks and the corresponding regular expressions.
         */
        masks: DefaultMasks;
    }

    /**
     * A map between the built-in masks and the corresponding regular expressions.
     */
    export interface DefaultMasks {
        /**
         * The mask that allows only letters. 
         */
        alpha: RegExp;

        /**
         * The mask that allows only letters and numbers. 
         */
        alphanum: RegExp;

        /**
         * The mask that allows only valid email characters (this does **NOT** check for well-formed emails) 
         */
        email: RegExp;

        /**
         * The mask that allows only hexadecimal characters. 
         */
        hex: RegExp;

        /**
         * The mask that allows only digits and dashes. 
         */
        int: RegExp;

        /**
         * The mask that allows only digits, spaces, commas, and periods. 
         */
        money: RegExp;

        /**
         * The mask that allows only digits, commas, and periods. 
         */
        num: RegExp;

        /**
         * The mask that allows only digits. 
         */
        pint: RegExp;

        /**
         * The mask that allows only digits and periods. 
         */
        pnum: RegExp;
    }

    /**
     * The main object of the key filter plugin that contains both the JQuery plugin function as well as some global
     * configuration for the key filter.
     */
    export interface KeyfilterNamespace<TElement = HTMLElement> {
        /**
         * Enables the key filter on the current input filter. Only text that matches the given regular expression is
         * allowed in the input field.
         * @param regExp Regular expression the value of the input field needs to match.
         * @return this jQuery instance for chaining.
         */
        (regExp: RegExp): JQuery<TElement>;

        /**
         * Enables the key filter on the current input filter. An entered character needs to match the given test
         * function to be allowed. If it does not match, the character is blocked and the entered character is
         * discarded.
         * @param testFunction A function that tests whether an entered character is allowed.
         * @return this jQuery instance for chaining.
         */
        (testFunction: JQueryKeyfilter.TestFunction): JQuery<TElement>;

        /**
         * Default, global configuration for the key filter.
         */
        defaults: JQueryKeyfilter.Defaults;

        /**
         * The version of this plugin.
         */
        version: number;
    }
}

interface JQuery<TElement = HTMLElement> {
    /**
     * The main function of the key filter plugin that can be called on an input field to set up the key filter. Also
     * contains some default and global configuration for the key filter.
     */
    keyfilter: JQueryKeyfilter.KeyfilterNamespace<TElement>;
}