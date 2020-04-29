/**
 * Namespace for the masked input JQueryUI plugin.
 * 
 * It allows a user to more easily enter fixed width input where you would like them to enter the data in a certain
 * format (dates,phone numbers, etc).
 */
declare namespace JQueryMaskedInput {
    /**
     * A mask is defined by a format made up of mask literals and mask definitions. Any character not in the definitions
     * list below is considered a mask literal. Mask literals will be automatically entered for the user as they type
     * and will not be able to be removed by the user.The following mask definitions are predefined:
     * 
     * - `a` - Represents an alpha character (A-Z,a-z)
     * - `9` - Represents a numeric character (0-9)
     * - `*` - Represents an alphanumeric character (A-Z,a-z,0-9)
     * 
     * You can have part of your mask be optional. Anything listed after `?` within the mask is considered optional user
     * input. The common example for this is phone number `+` optional extension.
     * 
     * You can also supply your own mask definitions by extending the `$.mask.definitions` object.
     * 
     * Examples for valid masks:
     * 
     * - Date: `99/99/9999`
     * - Phone: `(999) 999-9999`
     * - Phone + Ext: `(999) 999-9999? x99999` 
     * - Product Key: `a*-999-a999`
     * - Eye Script: `~9.99 ~9.99 999`	
     */
    export type MaskString = string;
    /**
     * Settings for the mask when setting up an input field with a mask.
     */
    export interface MaskSettings {
        /**
         * If `true`, the input field will be cleared when the user leaves the input field and did not enter some input
         * that completed the mask. If `false`, the partially entered text will remaing in the input field.
         */
        autoclear: boolean;
        /**
         * Placeholder string that is shown when no text is entered in the input field.
         */
        placeholder: string;
        /**
         * Callback that is invoked once the mask has been completed (every character required by the mask was entered).
         */
        completed(this: JQuery): void;
        /**
         * Callback that is invoked when the value of the input has changed. 
         * @param event Event that triggered the change.
         */
        onChange(this: JQuery, event: JQuery.Event): void;
    }
    /**
     * Global settings for the masked input plugin available in `$.mask`. Also contains the default values for the
     * `MaskSettings`.
     */
    export interface GlobalSettings extends MaskSettings {
        /**
         * An object with custom mask character definition. The key must be a (single) character used in a mask
         * definition. The value must be a RegExp string (converted to a RegExp via `new RegExp`) and match those
         * characters to which the mask character applies. For example, default definitions are as follows:
         * 
         * ```javascript
         * {
         *   "9": "[0-9]",
         *   "a": "[A-Za-z]",
         *   "*": "[A-Za-z0-9]",
         * }
         * ```
         */
        definitions: Record<string, string>;
        /**
         * Name of the HTML data attribute used for storing information related to the masked input in the DOM element.
         */
        dataName: string;
    }
}

interface JQuery {
    /**
     * Selects the text at the given range. If end is not given, just moves the cursor to the start position,
     * @param begin 0-based index of the start of the range, inclusive.
     * @param end 0-based index of the end of the range, exclusive.
     * @return this for chaining.
     */
    caret(begin: number, end?: number): this;
    /**
     * Removes the mask from the input field, returning it to the previous state before the masked input plugin was
     * initialized.
     * @return this for chaining.
     */
    unmask(): this;
    /**
     * Sets up the masked input plugin on this INPUT element. The user must now enter data in the given mask format.
     * 
     * @param mask Mask to use for the input, such as `99/99/9999` or `(999) 999-9999? x99999`.
     * @param settings Additional settings for the mask.
     * @return this for chaining.
     */
    mask(mask: JQueryMaskedInput.MaskString, settings?: Partial<JQueryMaskedInput.MaskSettings>): this;

}

interface JQueryStatic {
    /**
     * Global settings for the masked input plugin available in `$.mask`. Also contains the default values for the
     * `MaskSettings`.
     */
    mask: JQueryMaskedInput.GlobalSettings;
}