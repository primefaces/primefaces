/**
 * This object contains the keyboard layouts that are available for the keyboard widget. Each keyboard layout is
 * a list with one entry for each keyboard row. Each row is a string that contains the characters available on that
 * row.
 */
const layouts: PrimeFacesTypes.widget.KeyboardUtils.PresetLayouts = {
    /**
     * A basic `qwerty` layout without many special characters. This is a list with one entry for each keyboard row.
     * Each row is a string that contains the characters available on that row.
     */
    qwertyBasic :
        [$.keypad.qwertyAlphabetic[0] + $.keypad.CLOSE,
            $.keypad.HALF_SPACE + $.keypad.qwertyAlphabetic[1] +
            $.keypad.HALF_SPACE + $.keypad.CLEAR,
            $.keypad.SPACE + $.keypad.qwertyAlphabetic[2] +
            $.keypad.SHIFT + $.keypad.BACK],

    /**
     * A `qwerty` layout with some special characters. This is a list with one entry for each keyboard row. Each row
     * is a string that contains the characters available on that row.
     */
    qwerty : $.keypad.qwertyLayout,

    /**
     * An alphabetical layout with the letter keys in alphabetical order. This is a list with one entry for each
     * keyboard row. Each row is a string that contains the characters available on that row.
     */
    alphabetic :
        ['abcdefghij' + $.keypad.CLOSE,
            'klmnopqrst' + $.keypad.CLEAR,
            'uvwxyz' + $.keypad.SPACE + $.keypad.SPACE +
            $.keypad.SHIFT + $.keypad.BACK]
};

/**
 * Contains the character codes for the available control characters on the keyboard, such as space and return.
 */
const controls: PrimeFacesTypes.widget.KeyboardUtils.Controls = {
    /**
     * The keyboard code for the button that closes (hides) the keyboard
     */
    close : $.keypad.CLOSE,
    /**
     * The keyboard code for the button that clears the entered text.
     */
    clear : $.keypad.CLEAR,
    /**
     * The keyboard code for the back button that removes the character to the left of the cursor.
     */
    back : $.keypad.BACK,
    /**
     * The keyboard code for the modifying shift button.
     */
    shift : $.keypad.SHIFT,
    /**
     * The keyboard code for the space button that insert a whitespace.
     */
    spacebar : $.keypad.SPACE_BAR,
    /**
     * The keyboard code for the space button that inserts a full-width space.
     */
    space : $.keypad.SPACE,
    /**
     * The keyboard code for the space button that inserts a half-width space.
     */
    halfspace : $.keypad.HALF_SPACE
};

/**
 * Finds and returns a built-in layout with the given name. Currently available layout are `qwerty`, `qwertyBasic`,
 * and `alphabetic`.
 * @param name Name of a layout to get.
 * @returns The layout with the given name, if it exists.
 */
function getPresetLayout(name: string): string[] | undefined {
    return layouts[name];
}

/**
 * Finds and returns the keycode for the given control character. You can use this keycode in a custom keyboard
 * layout.
 * @param name string Name of the control keycode to get.
 * @returns The keycode with the given name, if it exists.
 */
function getPresetControl(name: string): string | undefined {
    return controls[name];
}

/**
 * Checks whether a built-in control with the given name exists. If it does, you can retrieve it via
 * `getPresetControl`.
 * @param key string Name of the control keycode to check.
 * @returns `true` if a control for the given key is defined, `false` otherwise.
 */
function isDefinedControl(key: string): boolean {
    return controls[key] != undefined;
}

/**
 * Parses a custom template layout that was specified by the user. Rows are separated by commas, keys on a row may
 * be separated by dashes.
 * @param template A custom layout template specified by the user.
 * @returns The parsed keyboard layout template. Contains one item for each row, each item contains the characters
 * on that keyboard row.
 */
function createLayoutFromTemplate(this: PrimeFacesTypes.widget.KeyboardUtils, template: string): string[] {
    // GitHub #3487: Unicode conversion
    template =  decodeURIComponent(JSON.parse('"' + template.replace(/"/g, '\\"') + '"'));
    const lines = template.split(','),
        newTemplate: string[] = new Array(lines.length);

    for (let i = 0; i < lines.length;i++) {
        newTemplate[i] = "";
        const lineControls = lines[i]!.split('-');

        for (let j = 0; j < lineControls.length;j++) {
            if (this.isDefinedControl(lineControls[j]!))
                newTemplate[i] = newTemplate[i]! + this.getPresetControl(lineControls[j]!)
            else
                newTemplate[i] = newTemplate[i]! + lineControls[j];
        }
    }

    return newTemplate;
}

/**
 * Contains a few utilities for parsing and working with different keyboard layout. Used by the Keyboard widget.
 * Available as `PrimeFaces.widget.KeyboardUtils`.
 */
export const KeyboardUtils: PrimeFacesTypes.widget.KeyboardUtils = {
    layouts,
    controls,
    getPresetLayout,
    getPresetControl,
    isDefinedControl,
    createLayoutFromTemplate,
};
