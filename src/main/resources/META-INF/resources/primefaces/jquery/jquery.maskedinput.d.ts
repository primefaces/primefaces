/**
 * Namespace for the Input Mask plugin.
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
     * You can have part of your mask be optional. Anything listed in between `[` and `]` within the mask is considered 
     * optional user input. The common example for this is phone number `+` optional extension.
     * 
     * You can also supply your own mask definitions and aliases by setting defauls.
     * See: https://github.com/RobinHerbots/Inputmask#set-defaults
     */
    export type MaskString = string;
    /**
     * Settings for the mask when setting up an input field with a mask.
     */
    export interface MaskSettings {
        /**
         * With an alias you can define a complex mask definition and call it by using an alias name. So this is mainly
         * to simplify the use of your masks. Some aliases found in the extensions are: email, currency, decimal, 
         * integer, date, datetime, dd/mm/yyyy, etc.
         */
        alias: string;
       /**
         * When using an alias like `datetime` its the format such as `dd\mm\yyyy`.
         */
        inputFormat: string;
       /**
         * When not using an alias this is the mask to use on the field such as `99-999-9999`.
         */
        mask: string;
        /**
         * If `true`, the input field will be cleared when the user leaves the input field and did not enter some input
         * that completed the mask. If `false`, the partially entered text will remaining in the input field.
         */
        clearIncomplete: boolean;
        /**
         * Placeholder string that is shown when no text is entered in the input field.
         */
        placeholder: string;
        /**
         * Callback that is invoked once the mask has been completed (every character required by the mask was entered).
         */
        oncomplete(this: JQuery): void;
        /**
         * Callback that is invoked once the input field is cleared.
         */
        oncleared(this: JQuery): void;
        /**
         * Callback that is invoked when the value of the input has changed. 
         * @param event Event that triggered the change.
         */
        onChange(this: JQuery, event: JQuery.Event): void;
    }
}

interface JQuery {
    /**
     * Sets up the masked input plugin on this INPUT element. The user must now enter data in the given mask format.
     * 
     * @param settings Additional settings for the mask.
     * @return this for chaining.
     */
    inputmask(settings?: Partial<JQueryMaskedInput.MaskSettings>): this;
}