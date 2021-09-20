/**
 * Namespace for the jQuery keypad plugin.
 * 
 * This plugin attaches a popup keyboard to a text field for mouse-driven entry or adds an inline keypad in a division or
 * span.
 * 
 * See http://keith-wood.name/keypad.html and https://github.com/kbwood/keypad
 */
declare namespace JQueryKeypad {

    /**
     * A function that is called after the keypad is constructed but before it is displayed.
     */
    export type BeforeShowListener =
        /**
         * @param division The division to be displayed
         * @param instance The current keypad instance controlling the keyboard.
         */
        (this: HTMLInputElement | HTMLTextAreaElement, division: JQuery, instance: KeypadInstance) => void;

    /**
     * A function that is called when a key is pressed on the keypad.
     */
    export type KeypressListener =
        /**
         * @param currentKey The value of they key that was pressed.
         * @param inputFieldValue The current value of the input field.
         * @param instance The current keypad instance controlling the keyboard.
         */
        (this: HTMLInputElement | HTMLTextAreaElement, currentKey: string, inputFieldValue: string, instance: KeypadInstance) => void;

    /**
     * A function that is called when the keypad is closed.
     */
    export type CloseListener =
        /**
         * @param inputFieldValue The current value of the input field.
         * @param instance The current keypad instance controlling the keyboard.
         */
        (this: HTMLInputElement | HTMLTextAreaElement, inputFieldValue: string, instance: KeypadInstance) => void;

    /**
     * Optional settings that can be passed when creating a new keypad instance to customize its behavior.
     */
    export interface KeypadSettings {
        /**
         * Control when the keypad is displayed:
         * - `focus` for only on focus of the text field
         * - `button` for only on clicking the trigger button
         * - `both` for both focus and button
         */
        showOn: "focus" | "button" | "both";

        /**
         * The URL of an image to use for the trigger button.
         */
        buttonImage: string;

        /**
         * Set to true to indicate that the trigger image should appear by itself and not on a button.
         */
        buttonImageOnly: boolean;

        /**
         * Set which animation is used to display the keypad. Choose from three standard animations `show`, `fadeIn`, or
         * `slideDown`; or use one of the jQuery UI effects if you include that package. For no animation use the empty
         * string.
         * 
         * @since 1.2.3 - use empty string for no animation.
         */
        showAnim: "show" | "fadeIn" | "slideDown" | string;

        /**
         * If you use one of the jQuery UI effects for the animation above, you can supply additional options for that
         * effect via this setting:
         * 
         * ```javascript
         * $(selector).keypad({
         *   showAnim: "clip",
         *   showOptions: {
         *     direction: "horizontal"
         *   }
         * });
         * ```
         */
        showOptions: Record<string, unknown> | null;

        /**
         * Control the speed of the show/hide animation with this setting: `slow`, `normal`, `fast`, or a number of
         * milliseconds.
         */
        duration: "slow" | "normal" | "fast" | number;

        /**
         * Text that is to be appended to all affected fields, such as to describe the presence or purpose of the
         * keypad.
         */
        appendText: string;

        /**
         * Set to `true` to add ThemeRoller classes to the keypad to allow the keypad to integrate with other UI
         * components using a theme.
         * 
         * @since 1.3.0
         */
        useThemeRoller: boolean;

        /**
         * One popup keypad is shared by all instances, so this setting allows you to apply different CSS styling by
         * adding an extra class to the keypad for each instance.
         */
        keypadClass: string;

        /**
         * Text that is displayed at the top of the keypad. The value may include HTML markup.
         */
        prompt: string;

        /**
         * Set the layout of the keypad by defining the characters present on each row. All alphabetic characters should
         * be in lower case. Make use of the keypad constants to add special features into the layout:
         * 
         * - $.keypad.CLOSE - the button to close the keypad
         * - $.keypad.CLEAR - the button to clear the input field
         * - $.keypad.BACK - the button to erase the previous character
         * - $.keypad.SHIFT - the button to toggle between upper/lower case characters
         * - $.keypad.SPACE_BAR - an extended space character
         * - $.keypad.ENTER - the button to add a carriage return
         * - $.keypad.TAB - the button to add a tab character
         * - $.keypad.SPACE - blank space equivalent to one key
         * - $.keypad.HALF_SPACE - blank space equivalent to half a key
         * 
         * Use the regional settings to set what is shown for each of these buttons.
         * 
         * @since 1.2.0 - added `SPACE_BAR`.
         * @since 1.2.4 - added `ENTER`.
         * @since 1.4.0 - added `TAB`.
         */
        layout: string[];

        /**
         * The character that separates the text content of the keys, used in conjunction with the layout setting.
         * 
         * By default it is blank, so each key contains only one character.
         * 
         * ```javascript
         * $(selector).keypad({
         *   separator: "|",
         *   layout: [
         *     "ACT|NSW|NT",
         *     "QLD|SA|TAS",
         *     "VIC|WA"
         *   ]
         * });
         * ```
         * 
         * @since 1.2.0
         */
        separator: string;

        /**
         * When using an inline keypad you can set this field to an input field to have the keypad update it
         * automatically. The value can be either the element itself, a jQuery wrapper around the element, or the jQuery
         * selector for it.
         * 
         * When not given, uses the onKeypress callback instead.
         * 
         * @since 1.2.1
         */
        target: HTMLElement | JQuery | string | null;

        /**
         * Set to `true` to indicate that only the keypad can be used to enter text, or to `false` to allow normal
         * entry as well. This option does not apply to inline keypads.
         */
        keypadOnly: boolean;

        /**
         * Set to `true` to indicate that the alphabetic characters in the layout should be randomised for each display.
         * The `isAlphabetic` setting determines which characters are alphabetic.
         */
        randomiseAlphabetic: boolean;

        /**
         * Set to `true` to indicate that the numeric characters in the layout should be randomised for each display. The
         * `isNumeric` setting determines which characters are numeric.
         */
        randomiseNumeric: boolean;

        /**
         * Set to `true` to indicate that the non-alphanumeric characters in the layout should be randomised for each
         * display.
         */
        randomiseOther: boolean;

        /**
         * Set to `true` to indicate that all characters in the layout should be randomised for each display. When
         * `true`, this setting overrides the other randomized settings.
         * 
         * @since 1.0.2
         */
        randomiseAll: boolean;

        /**
         * A function that is called after the keypad is constructed but before it is displayed, allowing you to update
         * it. For example, you could add extra buttons that perform tasks outside the scope of the normal keypad.
         * 
         * The function receives the division to be displayed and the keypad instance object as parameters, while `this`
         * refers to the text field.
         * 
         * ```javascript
         * $(selector).keypad({
         *   beforeShow: function(div, inst) {
         *     $("<button id=\"clickMe\" class=\"keypad-key\">Click me</button>")
         *       .appendTo(div)
         *       .click(function() {
         *         alert("Clicked");
         *       });
         *   }
         * });
         * ```
         * 
         * @since 1.2.0
         */
        beforeShow: BeforeShowListener | null;

        /**
         * A function that is called when a key is pressed on the keypad.
         * 
         * The function receives the current key value ($.keypad.BS for the Back key, $.keypad.DEL for the Clear key,
         * and the empty string for other control keys), the full field value, and the keypad instance object as
         * parameters, while `this` refers to the text field.
         * 
         * Of course, you can still have an `onchange` handler for the input field itself.
         * 
         * ```javascript
         * $(selector).keypad({
         *   onKeypress: function(key, value, inst) {
         *     $("#keypress").text(key || " ");
         *     $("#current").text(value);
         *   }
         * });
         * ```
         * 
         * @since 1.2.0 - added current key parameter.
         * @since 1.2.1 - added `$.keypad.BS` and `$.keypad.DEL` characters for `Back` and `Clear` keys.
         */
        onKeypress: KeypressListener | null;

        /**
         * A function that is called when the keypad is closed.
         * 
         * The function receives the current field value and the keypad instance object as parameters, while `this`
         * refers to the text field.
         * 
         * ```javascript
         * $(selector).keypad({
         *   onClose: function(value, inst) {
         *     alert("Closed with value " + value);
         *   }
         * });
         * ```
         */
        onClose: CloseListener | null;
    }

    /**
     * Keypad related constants and utility method available on the `$.keypad` object.
     */
    export interface KeypadGlobals {
        /**
         * The button to close the keypad 
         */
        CLOSE: string;

        /**
         * The button to clear the input field 
         */
        CLEAR: string;

        /**
         * The button to erase the previous character 
         */
        BACK: string;

        /**
         * The button to add a carriage return 
         */
        ENTER: string;

        /**
         * Blank space equivalent to half a key 
         */
        HALF_SPACE: string;

        /**
         * An extended space character 
         */
        SPACE_BAR: string;

        /**
         * The button to toggle between upper/lower case characters 
         */
        SHIFT: string;

        /**
         * Blank space equivalent to one key 
         */
        SPACE: string;

        /**
         * The button to add a tab character 
         */
        TAB: string;

        /**
         * The set of regional settings for the keypad fields. Entries are indexed by the country or region code with
         * the empty string providing the default (English) settings. Each entry is an object with the localized
         * messages. Language packages load new entries into this array and automatically apply them as global defaults.
         * 
         * ```html
         * <script type="text/javascript"  src="jquery.keypad-fr.js"></script>
         * ```
         * 
         * If necessary, you can then revert to the default language settings with
         * 
         * ```javascript
         * $.keypad.setDefaults($.keypad.regionalOptions[""]);
         * ```
         * 
         * and apply the language settings to individual fields with
         * 
         * ```javascript
         * $("#frenchKeypad").keypad($.keypad.regionalOptions["fr"]);
         * ```
         * 
         * @since 2.0.0 - previously called regional.
         */
        regionalOptions: Record<string, Record<string, RegionalSettings>>;

        /**
         * The standard numeric button layout. This is the default value for the `layout` setting.
         * 
         * @since 2.0.0
         */
        numericLayout: string[];

        /**
         * The alphabetic character layout for the standard Qwerty keyboard. This is the default value for the
         * `alphabeticLayout` setting.
         */
        qwertyAlphabetic: string[];

        /**
         * The full keyboard layout for the standard Qwerty keyboard. This is the default value for the `fullLayout`
         * setting.
         * 
         * @since 1.2.0 - Reordered and space bar added.
         * @since 1.2.4 - Enter added
         * @since 1.4.0 - Tab added
         */
        qwertyLayout: string[];

        /**
         * Default test for English alphabetic characters - accepting "A" to "Z" and "a" to "z". See also the
         * `isAlphabetic` setting.
         * 
         * @param character A character to check.
         * @return `true` if the given character is contained in the set of alphabetic characters, or `false` otherwise.
         */
        isAlphabetic(character: string): boolean;

        /**
         * Default test for English numeric characters - accepting '0' to '9'. See also the `isNumeric` setting.
         * 
         * @param character A character to check.
         * @return `true` if the given character is contained in the set of numerical characters, or `false` otherwise.
         */
        isNumeric(character: string): boolean;

        /**
         * Convert a character into its upper case form - using the standard JavaScript `toUpperCase` function. See also
         * the `toUpper` setting.
         * @param character A character to convert to upper case.
         * @return The given character, converted to upper case, or the given character itself, if it cannot be
         * converted.
         * @since 1.5.0
         */
        toUpper(character: string): string;

        /**
         * Insert text into an input field at its current cursor position. This replaces text if a selection has been
         * made.
         * @param input An input element into which to insert the given value. May be either an element or a CSS
         * selector.
         * @param value The text to insert.
         */
        insertValue(input: HTMLElement | JQuery | string, value: string): void;

        /**
         * Update the default instance settings to use with all keypad instances.
         * @param settings The new default settings to apply.
         */
        setDefaults(settings: Partial<KeypadSettings>): void;

        /**
         * Define a new action key for use on the keypad. Up to 32 keys may be defined, including the built-in ones.
         * 
         * ```javascript
         * $.keypad.addKeyDef("UPPER", "upper", function(inst) { 
         *   this.val(this.val().toUpperCase()).focus(); 
         * });
         * $("#keypad").keypad({
         *   upperText: "U/C", 
         *   upperStatus: "Convert to upper case", 
         *   layout: [
         *     "abc" + $.keypad.CLOSE, 
         *     "def" + $.keypad.CLEAR, 
         *     "ghi" + $.keypad.BACK, 
         *     "jkl" + $.keypad.UPPER
         *   ]
         * });
         * ```
         * 
         * @param id Name of the variable to use to include this key in a layout.
         * @param name Name used for a CSS styling class for the key (`keypad-<name>`) and for finding its display text
         * (using `<name>Text` and `<name>Status`).
         * @param action Code to be run when the key is pressed. it receives the keypad instance as a parameter, while this refers to the attached input field,
         * @param noHighlight `true` to suppress the highlight class when using `ThemeRoller` styling.
         * 
         * @since 1.4.0
         */
        addKeyDef(id: string, name: string, action: (this: HTMLInputElement, instance: KeypadInstance) => void, noHighlight?: boolean): void;
    }

    /**
     * These settings comprise the regional settings that may be localised by a language package. They can be overridden
     * for individual instances:
     * 
     * ```javascript
     * $(selector).keypad({backText: "BS"});
     * ```
     */
    export interface RegionalSettings {
        /**
         * The layout for alphabetic keyboard characters in this language.
         */
        alphabeticLayout: string[];

        /**
         * The description of the back button's purpose, used in a tool tip.
         */
        backStatus: string;

        /**
         * The text to display for the button to erase the previous character.
         */
        backText: string;

        /**
         * The description of the button's purpose, used in a tool tip.
         */
        buttonStatus: string;

        /**
         * The text to display on a trigger button for the keypad.
         */
        buttonText: string;

        /**
         * The description of the clear button's purpose, used in a tool tip.
         */
        clearStatus: string;

        /**
         * The text to display for the button to clear the text field.
         */
        clearText: string;

        /**
         * The description of the close button's purpose, used in a tool tip.
         */
        closeStatus: string;

        /**
         * The text to display for the button to close the keypad.
         */
        closeText: string;

        /**
         * The description of the enter button's purpose, used in a tool tip.
         * 
         * @since 1.2.4
         */
        enterStatus: string;

        /**
         * The text to display for the button to add a carriage return.
         * 
         * @since 1.2.4
         */
        enterText: string;

        /**
         * The layout for the full standard keyboard in this language.
         */
        fullLayout: string[];

        /**
         * A function to determine whether or not a character is alphabetic.
         * 
         * The character to test is passed as the parameter and a boolean response is expected.
         * 
         * The default accepts `A` to `Z` and `a` to `z`.
         * 
         * @param character A character to test.
         * @return `true` if the given character is contained in the set of alphabetical characters, or `false`
         * otherwise.
         */
        isAlphabetic(character: string): boolean;

        /**
         * A function to determine whether or not a character is numeric.
         * 
         * The character to test is passed as the parameter and a boolean response is expected.
         * 
         * The default accepts `0` to `9`.
         * 
         * @param character A character to test.
         * @return `true` if the given character is contained in the set of numerical characters, or `false`
         * otherwise.
         */
        isNumeric(character: string): boolean;

        /**
         * Set to `true` to indicate that the current language flows right-to-left.
         */
        isRTL: boolean;

        /**
         * The description of the shift button's purpose, used in a tool tip.
         */
        shiftStatus: string;

        /**
         * The text to display for the button to shift between upper and lower case characters.
         */
        shiftText: string;

        /**
         * The description of the space bar button's purpose, used in a tool tip.
         * 
         * @since 1.4.0.
         */
        spacebarStatus: string;

        /**
         * The text to display for the extended button to add a space.
         * 
         * @since 1.4.0.
         */
        spacebarText: string;

        /**
         * The description of the tab button's purpose, used in a tool tip.
         * 
         * @since 1.4.0.
         */
        tabStatus: string;

        /**
         * The text to display for the button to add a tab.
         * 
         * @since 1.4.0.
         */
        tabText: string;

        /**
         * A function to convert a character into its upper case form.
         * 
         * It accepts one parameter being the current character and returns the corresponding upper case character.
         * 
         * The default uses the standard JavaScript `toUpperCase` function.
         *
         * ```javascript 
         * $(selector).keypad({
         *   layout: [
         *     "12345",
         *     $.keypad.SHIFT
         *   ],
         *   toUpper: function(ch) {
         *     return {
         *       "1": "!",
         *       "2": "@",
         *       "3": "#",
         *       "4": "$",
         *       "5": "%"
         *     }[ch] || ch;
         *   }
         * });
         * ```
         * 
         * @param character A character to convert to upper case.
         * @return The given character, converted to upper case; or the given character itself if it cannot be
         * converted.
         * 
         * @since 1.5.0.
         */
        toUpper(character: string): string;
    }

    /**
     * The keypad instance that is used to control the keypad. It is passed to the callback function.
     */
    export interface KeypadInstance {
        /**
         * The name of this plugin, i.e. `keypad`.
         */
        name: "keypad";

        /**
         * The HTML input element for entering text.
         */
        elem: JQuery;

        /**
         * The current settings of this keypad instance.
         */
        options: KeypadSettings;

        /**
         * Whether the keypad is currently showing upper case characters (usually triggered by pressing the shift key).
         */
        ucase: boolean;

        /**
         * Whether the readonly attribute is set or removed from the input field.
         */
        saveReadonly: boolean;
    }
}

interface JQuery {
    /**
     * Initializes the keypad on the currently select elements.
     * @param settings Optional settings for customizing the keypad.
     * @return This jQuery instance for chaining.
     */
    keypad(settings?: Partial<JQueryKeypad.KeypadSettings>): this;

    /**
     * Determine whether the keypad functionality has been disabled for the first of the given field(s).
     * @param method The method to call on the existing keypad instance.
     * @return Whether the input field is disabled.
     */
    keypad(method: "isDisabled"): boolean;

    /**
     * Disable the keypad for the given field(s) as well as the field itself. Note that a field that is disabled when
     * the keypad is applied to it causes the keypad to become disabled as well.
     * @param method The method to call on the existing keypad instance.
     * @return this jQuery instance for chaining.
     */
    keypad(method: "disable"): this;

    /**
     * Enable the keypad for the given field(s) as well as the field itself.
     * @param method The method to call on the existing keypad instance.
     * @return this jQuery instance for chaining.
     */
    keypad(method: "enable"): this;

    /**
     * Hide the keypad for the given field.
     * @param method The method to call on the existing keypad instance.
     * @return this jQuery instance for chaining.
     */
    keypad(method: "hide"): this;

    /**
     * Pop up the keypad for the given field.
     * @param method The method to call on the existing keypad instance.
     * @return this jQuery instance for chaining.
     */
    keypad(method: "show"): this;

    /**
     * Remove the keypad functionality from the given field(s).
     * @param method The method to call on the existing keypad instance.
     * @return this jQuery instance for chaining.
     */
    keypad(method: "destroy"): this;

    /**
     * Retrieve all of the current settings for the first keypad instance attached to the given field(s).
     * 
     * ```javascript
     * var settings = $(selector).keypad("option"); 
     * var prompt = $(selector).keypad("option").prompt;
     * ```
     * 
     * @param method The method to call on the existing keypad instance.
     * @return this jQuery instance for chaining.
     * 
     * @since 1.5.0.
     */
    keypad(method: "option"): JQueryKeypad.KeypadSettings;

    /**
     * Retrieve one of the current settings for the first keypad instance attached to the given field(s).
     * 
     * @param optionName The name of the setting to retrieve.
     * 
     * ```javascript
     * var settings = $(selector).keypad("option"); 
     * var prompt = $(selector).keypad("option", "prompt");
     * ```
     * 
     * @typeparam K The key of the setting to retrieve.
     * @param method The method to call on the existing keypad instance.
     * @return this jQuery instance for chaining.
     * 
     * @since 1.5.0.
     */
    keypad<K extends keyof JQueryKeypad.KeypadSettings>(method: "option", optionName: K): JQueryKeypad.KeypadSettings[K];

    /**
     * Update the settings for the keypad instance(s) attached to the given field(s).
     * 
     * ```javascript
     * $(selector).keypad("option", {
     *   prompt: "Keypad",
     *   keypadOnly: false
     * });
     * ```
     * 
     * @param method The method to call on the existing keypad instance.
     * @param options The new options to set on the keypad instance.
     * @return this jQuery instance for chaining.
     * 
     * @since 1.5.0 - previously you used the `change` command.
     */
    keypad(method: "option", options: Partial<JQueryKeypad.KeypadSettings>): this;

    /**
     * Update a particular setting for the keypad instance(s) attached to the given field(s).
     * 
     * ```javascript
     * $(selector).keypad("option", "prompt", "Keypad");
     * ```
     * 
     * @typeparam K The key of the setting to update.
     * @param method The method to call on the existing keypad instance.
     * @param optionName Name of the option to update.
     * @param optionValue The new value for the option
     * @return this jQuery instance for chaining.
     * 
     * @since 1.5.0 - previously you used the `change` command.
     */
    keypad<K extends keyof JQueryKeypad.KeypadSettings>(method: "option", optionName: K, optionValue: JQueryKeypad.KeypadSettings[K] | undefined): this;
}

interface JQueryStatic {
    /**
     * Keypad related constants and utility method available on the `$.keypad` object.
     */
    keypad: JQueryKeypad.KeypadGlobals;
}