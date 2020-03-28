/**
 * __PrimeFaces InputNumber Widget__
 * 
 * InputNumber formats input fields with numeric strings. It supports currency symbols, minimum and maximum value,
 * negative numbers, and a lot of round methods.
 * 
 * @prop {boolean} disabled Whether this widget is currently disabled, i.e. whether the user can enter a number.
 * @prop {JQuery} hiddenInput The DOM element for the hidden input field with the current value of this widget.
 * @prop {JQuery} input The DOM element for the visibled input field with autoNumeric.
 * @prop {Partial<JQueryAutoNumeric.InitOptions>} plugOptArray The initial options for autoNumeric.
 * @prop {string} valueToRender The initial, numerical value that is displayed, such as `0.0` or `5.3`.
 * 
 * @interface {PrimeFaces.widget.InputNumberCfg} cfg The configuration for the {@link  InputNumber| InputNumber widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.disabled Whether this widget is initially disabled.
 * @prop {Partial<JQueryAutoNumeric.InitOptions>} cfg.pluginOptions The initial options for autoNumeric.
 * @prop {string} cfg.valueToRender The initial, numerical value that is displayed, such as `0.0` or `5.3`.
 */
PrimeFaces.widget.InputNumber = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.input = $(this.jqId + '_input');
        this.hiddenInput = $(this.jqId + '_hinput');
        this.plugOptArray = cfg.pluginOptions;
        this.valueToRender = cfg.valueToRender;
        this.disabled = cfg.disabled;

        //bind events if not disabled
        if (this.disabled) {
            this.input.attr("disabled", "disabled");
            this.input.addClass("ui-state-disabled");
            this.hiddenInput.attr("disabled", "disabled");
        }

        //Visual effects
        PrimeFaces.skinInput(this.input);

        this.wrapEvents();

        this.input.autoNumeric('init', this.plugOptArray);

        if (this.valueToRender !== "") {
            //set the value to the input the plugin will format it.
            this.input.autoNumeric('set', this.valueToRender);
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hiddenInput.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Wraps the events on the external (visible) input to copy the value to the hidden input, before calling the
     * callback.
     * @private
     */
    wrapEvents: function() {
        var $this = this;

        // copy the value from the input to the hidden input
        var originalOnkeyup = this.input.prop('onkeyup');
        this.input.prop('onkeyup', null).off('keyup').on('keyup', function (e) {

            var oldValue;

            var keyCode = e.which;
            if (keyCode === 8 || keyCode === 13 || keyCode === 32
                    || (keyCode >= 46 && keyCode <= 90)
                    || (keyCode >= 96 && keyCode <= 111)
                    || (keyCode >= 186 && keyCode <= 222)) {

                oldValue = $this.copyValueToHiddenInput();
            }

            if (originalOnkeyup && originalOnkeyup.call(this, e) === false) {
                if (oldValue) {
                    $this.setValueToHiddenInput(oldValue);
                }
                return false;
            }
        });

        var originalOnchange = this.input.prop('onchange');
        this.input.prop('onchange', null).off('change').on('change', function (e) {

            var oldValue = $this.copyValueToHiddenInput();
            if (originalOnchange && originalOnchange.call(this, e) === false) {
                $this.setValueToHiddenInput(oldValue);
                return false;
            }
        });

        var originalOnkeydown = this.input.prop('onkeydown');
        this.input.prop('onkeydown', null).off('keydown').on('keydown', function (e) {

            var oldValue = $this.copyValueToHiddenInput();
            if (originalOnkeydown && originalOnkeydown.call(this, e) === false) {
                $this.setValueToHiddenInput(oldValue);
                return false;
            }
        });
    },

    /**
     * Wraps the events on the external (visible) input to copy the value to the hidden input.
     * @return {number} The original value of the hidden input.
     * @private
     */
    copyValueToHiddenInput: function() {
        var oldVal = this.hiddenInput.val();

        var newVal = this.input.autoNumeric('get');

        if (oldVal !== newVal) {
            this.setValueToHiddenInput(newVal);
        }

        return oldVal;
    },

    /**
     * Writes the given value to the hidden input field that stores the actual value of this widget.
     * @private
     * @param {string} value A value to set on the hidden input.
     */
    setValueToHiddenInput: function(value) {
        this.hiddenInput.val(value);
    },

    /**
     * Enables this input field, so that the user can enter data.
     */
    enable: function () {
        this.input.removeAttr("disabled");
        this.input.removeClass("ui-state-disabled");
        this.hiddenInput.removeAttr("disabled");
        this.disabled = false;
    },

    /**
     * Enables this input field, so that the user cannot enter data.
     */
    disable: function () {
        this.input.attr("disabled", "disabled");
        this.input.addClass("ui-state-disabled");
        this.hiddenInput.attr("disabled", "disabled");
        this.disabled = true;
    },

    /**
     * Sets the value of this input number widget to the given value. Makes sure that the number is formatted correctly.
     * @param {number} value The new numeric value to set. It will be formatted appropriately.
     */
    setValue: function (value) {
        this.input.autoNumeric('set', value);
        var cleanVal = this.input.autoNumeric('get');
        this.hiddenInput.attr('value', cleanVal);
    },

    /**
     * Finds the current value, which is the raw numerical value without any formatting applied.
     * @return {number} The current numerical value of this input number widget.
     */
    getValue: function () {
        return this.input.autoNumeric('get');
    }
});
