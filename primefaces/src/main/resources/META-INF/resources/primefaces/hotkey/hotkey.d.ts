/**
 * Namespace for the masked input jQuery Hotkeys plugin.
 * 
 * jQuery Hotkeys is a plug-in that lets you easily add and remove handlers for keyboard events anywhere in your
 * code supporting almost any key combination. The official webpage is available at
 * https://github.com/tzuryby/jquery.hotkeys
 * 
 * Usage is as follows:
 * 
 * ```javascript
 * $(expression).on(types.keys, handler);
 * $(expression).off(types.keys, handler);
 * 
 * $(document).on('keydown.ctrl_a', fn);
 * 
 * // e.g. replace '$' sign with 'EUR'
 * $('input.foo').on('keyup.$', function(){
 *   this.value = this.value.replace('$', 'EUR');
 * });
 * ```
 */
declare namespace JQueryHotkey {
    /**
     * Represents some global data required by the hotkey plugin, such as the version of the library and the default
     * options.
     */
    export interface GlobalSettings {
        /**
         * The version of this library. 
         */
        version: string;
        /**
         * A map of keys that may be part of a hotkey combination. The map key is the character code of the key, the
         * mapped value is the name of the key, such as `capslock` or `+`.
         */
        specialKeys: Record<number, string>;
        /**
         * A map between the character a key produces normally and the character it produces while the shift key is
         * pressed. Both the map key and the mapped value is the name of the key, e.g. `;` or `3`.
         */
        shiftNums: Record<string, string>;
        /**
         * A list of values for the `type` attribute of an HTML INPUT element that are allowed to trigger a hotkey.
         * Excludes `button`, `checkbox`, `file`, `hidden`, `image`, `password`, `radio`, `reset`, `search`, `submit`, and
         * `url`,
         */
        textAcceptingInputTypes: HTMLInputElement["type"];
        /**
         * A RegExp that the tag name of an HTML element needs to match for hotkeys to be available for that element.
         * Defaults to TEXTAREA, INPUT, and SELECT elements.
         */
        textInputTypes: RegExp;
        /**
         * The default settings for the hotkey plugin.
         */
        options: HotkeyOptions;
    } 
    /**
     * The options for the jQuery Hotkeys plugin you may pass when initializing this plugin on an input element.
     */
    export interface HotkeyOptions {
        /**
         * `true` to exclude all elements with a tag name not matched by `jQuery.hotkeys.textInputTypes`, or `false`
         * otherwise.
         */
        filterInputAcceptingElements: boolean,
        /**
         * `true` to exclude all input elements with a type attribute not set to one of the values contained in
         * `jQuery.hotkeys.textAcceptingInputTypes`, or `false` otherwise.
         */
        filterTextInputs: boolean,
        /**
         * `true` to exclude input elements that are content-editable (i.e. have the `contenteditable` attribute set),
         * or `false` otherwise.
         */
        filterContentEditable: boolean;  
    }
}

interface JQueryStatic {
    /**
     * The global settings for the jQuery Hotkeys plugin.
     */
    hotkeys: JQueryHotkey.GlobalSettings;
}