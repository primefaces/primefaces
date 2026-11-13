import "./src/jquery.keypad.js";
import { KeyboardUtils } from "./src/keyboard.utils.js";
import { Keyboard } from "./src/keyboard.js";

// KeyboardUtils is not a component, but it was always available in the component scope
Object.assign(PrimeFaces.component, { KeyboardUtils });
PrimeFaces.component.Keyboard = Keyboard;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Keyboard: typeof Keyboard;
        }
    }

    namespace PrimeFacesTypes.component {
        export type KeyboardCfg = import("./src/keyboard.js").KeyboardCfg;

        /**
         * Contains a few utilities for parsing and working with different keyboard layout. Used by the Keyboard
         * component, available as `PrimeFaces.component.KeyboardUtils`.
         */
        export interface KeyboardUtils {
            /**
             * The keyboard layouts that are available for the keyboard component.
             */
            readonly layouts: KeyboardUtils.PresetLayouts;
            /**
             * The character codes for the available control characters on the keyboard, such as space and return.
             */
            readonly controls: KeyboardUtils.Controls;
            /**
             * Finds and returns a built-in layout with the given name. Currently available layout are `qwerty`,
             * `qwertyBasic`, and `alphabetic`.
             * @param name Name of a layout to get.
             * @returns The layout with the given name, if it exists.
             */
            getPresetLayout(name: string): string[] | undefined;
            /**
             * Finds and returns the keycode for the given control character. You can use this keycode in a custom
             * keyboard layout.
             * @param name string Name of the control keycode to get.
             * @returns The keycode with the given name, if it exists.
             */
            getPresetControl(name: string): string | undefined;
            /**
             * Checks whether a built-in control with the given name exists. If it does, you can retrieve it via
             * `getPresetControl`.
             * @param key string Name of the control keycode to check.
             * @returns `true` if a control for the given key is defined, `false` otherwise.
             */
            isDefinedControl(key: string): boolean;
            /**
             * Parses a custom template layout that was specified by the user. Rows are separated by commas, keys on a
             * row may be separated by dashes.
             * @param template A custom layout template specified by the user.
             * @returns The parsed keyboard layout template. Contains one item for each row, each item contains the
             * characters on that keyboard row.
             */
            createLayoutFromTemplate(this: KeyboardUtils, template: string): string[];
        }
    }

    namespace PrimeFacesTypes.component.KeyboardUtils {
        /**
         * The keyboard layouts that are available for the keyboard component. Each keyboard layout is a list with one
         * entry for each keyboard row. Each row is a string that contains the characters available on that row.
         */
        export interface PresetLayouts {
            /**
             * A basic `qwerty` layout without many special characters.
             */
            qwertyBasic: string[];
            /**
             * A `qwerty` layout with some special characters.
             */
            qwerty: string[];
            /**
             * An alphabetical layout with the letter keys in alphabetical order.
             */
            alphabetic: string[];
            /**
             * Further layouts, e.g. added by the application.
             */
            [name: string]: string[];
        }

        /**
         * The character codes for the available control characters on the keyboard, such as space and return.
         */
        export interface Controls {
            /** The keyboard code for the button that closes (hides) the keyboard. */
            close: string;
            /** The keyboard code for the button that clears the entered text. */
            clear: string;
            /** The keyboard code for the back button that removes the character to the left of the cursor. */
            back: string;
            /** The keyboard code for the modifying shift button. */
            shift: string;
            /** The keyboard code for the space button that insert a whitespace. */
            spacebar: string;
            /** The keyboard code for the space button that inserts a full-width space. */
            space: string;
            /** The keyboard code for the space button that inserts a half-width space. */
            halfspace: string;
            /**
             * Further controls, e.g. added by the application.
             */
            [name: string]: string;
        }
    }
}
