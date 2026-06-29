/**
 * __PrimeFaces Keyboard Widget__
 * 
 * Keyboard is an input component that uses a virtual keyboard to provide the input. Notable features are the
 * customizable layouts and skinning capabilities.
 * 
 * @interface {PrimeFaces.widget.KeyboardCfg} cfg The configuration for the {@link  Keyboard| Keyboard widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryKeypad.KeypadSettings} cfg
 * 
 * @prop {JQueryKeypad.BeforeShowListener} cfg.beforeShow Callback that is invoked by the keyboard JQuery plugin before
 * the keyboard is brought up.
 * @prop {string[]} cfg.layout The resolved and parsed keyboard layout to be used. Contains on item for each row, each
 * keyboard row contains the character codes of the keys shown on that row.
 * @prop {string} cfg.layoutName The name of the built-in keyboard layout to use. Mutually exclusive with
 * `layoutTemplate`.
 * @prop {string} cfg.layoutTemplate An optional custom keyboard layout template specified by the user. The keyboard rows
 * must be separated by a comma. Each row contains the keys that should be displayed on that row. To specify a control
 * button (space, back, shift etc.), separate the name of the control key with a dash.   
 */
 PrimeFaces.widget.Keyboard = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        var $this = this;   
        if (this.cfg.layoutTemplate)
            this.cfg.layout = PrimeFaces.widget.KeyboardUtils.createLayoutFromTemplate(this.cfg.layoutTemplate);
        else
            this.cfg.layout = PrimeFaces.widget.KeyboardUtils.getPresetLayout(this.cfg.layoutName);

        this.cfg.beforeShow = function(div, inst) {
            $(div).addClass('ui-input-overlay').css('z-index', PrimeFaces.nextZindex());
            $this.bindPanelEvents();
        };

        this.cfg.onClose = function() {
            $this.unbindPanelEvents();
        };

        this.jq.keypad(this.cfg);

        //Visuals
        PrimeFaces.skinInput(this.jq);
    },

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents: function() {
        var $this = this;

        //Hide overlay on resize/scroll
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', null, function() {
            $this.jq.keypad('hide');
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, function() {
            $this.jq.keypad('hide');
        });
    },

    /**
     * Unbind all panel event listeners
     * @private
     */
    unbindPanelEvents: function() {
        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }
    
        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    }
});

/**
 * Contains a few utilities for parsing and working with different keyboard layout. Used by the Keyboard widget.
 * @namespace
 */
PrimeFaces.widget.KeyboardUtils = {

    /**
     * This object contains the keyboard layouts that are available for the keyboard widget. Each keyboard layout is
     * a list with one entry for each keyboard row. Each row is a string that contains the characters available on that
     * row. 
     * @interface {PrimeFaces.widget.KeyboardUtils.PresetLayouts} . Contains the various different available keyboard layouts.
     * @type {PrimeFaces.widget.KeyboardUtils.PresetLayouts}
     * @readonly
     */
    layouts : {
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
    },

    /**
     * Contains the character codes for the available control characters on the keyboard, such as space and return.
     * @interface {PrimeFaces.widget.KeyboardUtils.Controls} . Contains the character codes for the available control
     * characters on the keyboard, such as space and return.
     * @type {PrimeFaces.widget.KeyboardUtils.Controls}
     * @readonly
     */
    controls : {
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
    },

    /**
     * Finds and returns a built-in layout with the given name. Currently available layout are `qwerty`, `qwertyBasic`,
     * and `alphabetic`.
     * @param {string} name Name of a layout to get.
     * @return {string | undefined} The layout with the given name, if it exists.
     */
    getPresetLayout : function(name) {
        return this.layouts[name];
    },

    /**
     * Finds and returns the keycode for the given control character. You can use this keycode in a custom keyboard
     * layout.
     * @param {string} name string Name of the control keycode to get.
     * @return {string | undefined} The keycode with the given name, if it exists.
     */
    getPresetControl : function(name) {
        return this.controls[name];
    },

    /**
     * Checks whether a built-in control with the given name exists. If it does, you can retrieve it via
     * `getPresetControl`.
     * @param {string} key string Name of the control keycode to check.
     * @return {boolean} `true` if a control for the given key is defined, `false` otherwise.
     */
    isDefinedControl : function(key) {
        return this.controls[key] != undefined;
    },

    /**
     * Parses a custom template layout that was specified by the user. Rows are separated by commas, keys on a row may
     * be separated by dashes.
     * @param {string} template A custom layout template specified by the user.
     * @return {string[]} The parsed keyboard layout template. Contains one item for each row, each item contains the
     * characters on that keyboard row.
     */
    createLayoutFromTemplate : function(template) {
        // GitHub #3487: Unicode conversion
        template =  decodeURIComponent(JSON.parse('"' + template.replace(/\"/g, '\\"') + '"'));
        var lines = template.split(','),
        template = new Array(lines.length);

        for(var i = 0; i < lines.length;i++) {
            template[i] = "";
            var lineControls = lines[i].split('-');

            for(var j = 0; j < lineControls.length;j++) {
                if(this.isDefinedControl(lineControls[j]))
                    template[i] = template[i] + this.getPresetControl(lineControls[j])
                else
                    template[i] = template[i] + lineControls[j];
            }
        }

        return template;
    }

};