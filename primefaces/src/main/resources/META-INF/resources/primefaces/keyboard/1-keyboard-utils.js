/**
 * Contains a few utilities for parsing and working with different keyboard layout. Used by the Keyboard widget.
 * @namespace
 */
(function(){
    /**
     * This object contains the keyboard layouts that are available for the keyboard widget. Each keyboard layout is
     * a list with one entry for each keyboard row. Each row is a string that contains the characters available on that
     * row.
     * @interface {PrimeFaces.widget.KeyboardUtils.PresetLayouts} . Contains the various different available keyboard layouts.
     * @type {PrimeFaces.widget.KeyboardUtils.PresetLayouts}
     * @readonly
     */
    const layouts = {
        /**
         * A basic `qwerty` layout without many special characters. This is a list with one entry for each keyboard row.
         * Each row is a string that contains the characters available on that row.
         * @type {string[]}
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
         * @type {string[]}
         */
        qwerty : $.keypad.qwertyLayout,

        /**
         * An alphabetical layout with the letter keys in alphabetical order. This is a list with one entry for each
         * keyboard row. Each row is a string that contains the characters available on that row.
         * @type {string[]}
         */
        alphabetic :
            ['abcdefghij' + $.keypad.CLOSE,
                'klmnopqrst' + $.keypad.CLEAR,
                'uvwxyz' + $.keypad.SPACE + $.keypad.SPACE +
                $.keypad.SHIFT + $.keypad.BACK]
    };

    /**
     * Contains the character codes for the available control characters on the keyboard, such as space and return.
     * @interface {PrimeFaces.widget.KeyboardUtils.Controls} . Contains the character codes for the available control
     * characters on the keyboard, such as space and return.
     * @type {PrimeFaces.widget.KeyboardUtils.Controls}
     * @readonly
     */
    const controls = {
        /**
         * The keyboard code for the button that closes (hides) the keyboard
         * @type {string}
         */
        close : $.keypad.CLOSE,
        /**
         * The keyboard code for the button that clears the entered text.
         * @type {string}
         */
        clear : $.keypad.CLEAR,
        /**
         * The keyboard code for the back button that removes the character to the left of the cursor.
         * @type {string}
         */
        back : $.keypad.BACK,
        /**
         * The keyboard code for the modifying shift button.
         * @type {string}
         */
        shift : $.keypad.SHIFT,
        /**
         * The keyboard code for the space button that insert a whitespace.
         * @type {string}
         */
        spacebar : $.keypad.SPACE_BAR,
        /**
         * The keyboard code for the space button that inserts a full-width space.
         * @type {string}
         */
        space : $.keypad.SPACE,
        /**
         * The keyboard code for the space button that inserts a half-width space.
         * @type {string}
         */
        halfspace : $.keypad.HALF_SPACE
    };

    /**
     * Finds and returns a built-in layout with the given name. Currently available layout are `qwerty`, `qwertyBasic`,
     * and `alphabetic`.
     * @param {string} name Name of a layout to get.
     * @return {string | undefined} The layout with the given name, if it exists.
     */
    function getPresetLayout(name) {
        return layouts[name];
    }

    /**
     * Finds and returns the keycode for the given control character. You can use this keycode in a custom keyboard
     * layout.
     * @param {string} name string Name of the control keycode to get.
     * @return {string | undefined} The keycode with the given name, if it exists.
     */
    function getPresetControl(name) {
        return controls[name];
    }

    /**
     * Checks whether a built-in control with the given name exists. If it does, you can retrieve it via
     * `getPresetControl`.
     * @param {string} key string Name of the control keycode to check.
     * @return {boolean} `true` if a control for the given key is defined, `false` otherwise.
     */
    function isDefinedControl(key) {
        return controls[key] != undefined;
    }

    /**
     * Parses a custom template layout that was specified by the user. Rows are separated by commas, keys on a row may
     * be separated by dashes.
     * @param {string} template A custom layout template specified by the user.
     * @return {string[]} The parsed keyboard layout template. Contains one item for each row, each item contains the
     * characters on that keyboard row.
     */
    function createLayoutFromTemplate(template) {
        // GitHub #3487: Unicode conversion
        template =  decodeURIComponent(JSON.parse('"' + template.replace(/\"/g, '\\"') + '"'));
        var lines = template.split(','),
            template = new Array(lines.length);

        for(var i = 0; i < lines.length;i++) {
            template[i] = "";
            var lineControls = lines[i].split('-');

            for(var j = 0; j < lineControls.length;j++) {
                if(isDefinedControl(lineControls[j]))
                    template[i] = template[i] + getPresetControl(lineControls[j])
                else
                    template[i] = template[i] + lineControls[j];
            }
        }

        return template;
    }

    /**
     * Contains a few utilities for parsing and working with different keyboard layout. Used by the Keyboard widget.
     * @namespace
     */
    PrimeFaces.widget.KeyboardUtils = {
        layouts,
        controls,
        getPresetLayout,
        getPresetControl,
        isDefinedControl,
        createLayoutFromTemplate,
    };
})();
