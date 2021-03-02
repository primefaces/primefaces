/**
 * __PrimeFaces InputNumber Widget__
 * 
 * InputNumber formats input fields with numeric strings. It supports currency symbols, minimum and maximum value,
 * negative numbers, and a lot of round methods.
 * 
 * @typedef {import("autonumeric").Options} PrimeFaces.widget.InputNumber.AutoNumericOptions Alias for the AutoNumeric
 * options, required for technical reasons.
 * 
 * @prop {import("autonumeric")} autonumeric The current AutoNumeric instance.
 * @prop {boolean} disabled Whether this widget is currently disabled, i.e. whether the user can enter a number.
 * @prop {JQuery} hiddenInput The DOM element for the hidden input field with the current value of this widget.
 * @prop {JQuery} input The DOM element for the visible input field with autoNumeric.
 * @prop {undefined} plugOptArray Always `undefined`.
 * @prop {string} valueToRender The initial, numerical value that is displayed, such as `0.0` or `5.3`.
 * 
 * @interface {PrimeFaces.widget.InputNumberCfg} cfg The configuration for the {@link  InputNumber| InputNumber widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {PrimeFaces.widget.InputNumber.AutoNumericOptions} cfg
 * 
 * @prop {boolean} cfg.disabled Whether this widget is initially disabled.
 * @prop {undefined} cfg.pluginOptions Always undefined.
 * @prop {string} cfg.valueToRender The initial, numerical value that is displayed, such as `0.0` or `5.3`.
 */
PrimeFaces.widget.InputNumber = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
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
        this.bindInputEvents();

        this.autonumeric = new AutoNumeric(this.jqId + '_input', this.cfg);

        if (this.valueToRender !== "") {
            //set the value to the input the plugin will format it.
            this.autonumeric.set(this.valueToRender);
            // GitHub #6940 blur firing too many change events
            this.autonumeric.rawValueOnFocus = this.valueToRender;
        }

        this.copyValueToHiddenInput();

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

        // get the current attached events if using CSP
        var events = this.input[0] ? $._data(this.input[0], "events") : null;

        // use DOM if non-CSP and JQ event if CSP
        var originalOnkeyup = this.input.prop('onkeyup');
        if (!originalOnkeyup && events && events.keyup) {
            originalOnkeyup = events.keyup[0].handler;
        }
        this.input.prop('onkeyup', null).off('keyup').on('keyup.inputnumber', function (e) {

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

        // use DOM if non-CSP and JQ event if CSP
        var originalOnchange = this.input.prop('onchange');
        if (!originalOnchange && events && events.change) {
            originalOnchange = events.change[0].handler;
        }
        this.input.prop('onchange', null).off('change').on('change.inputnumber', function (e) {

            var oldValue = $this.copyValueToHiddenInput();
            if (originalOnchange && originalOnchange.call(this, e) === false) {
                $this.setValueToHiddenInput(oldValue);
                return false;
            }
        });

        // use DOM if non-CSP and JQ event if CSP 
        var originalOnkeydown = this.input.prop('onkeydown');
        if (!originalOnkeydown && events && events.keydown) {
            originalOnkeydown = events.keydown[0].handler;
        }
        this.input.prop('onkeydown', null).off('keydown').on('keydown.inputnumber', function (e) {

            var oldValue = $this.copyValueToHiddenInput();
            if (originalOnkeydown && originalOnkeydown.call(this, e) === false) {
                $this.setValueToHiddenInput(oldValue);
                return false;
            }
        });
    },

    /**
     * Binds input listener which fixes a browser autofill issue.
     * See: https://github.com/autoNumeric/autoNumeric/issues/536
     * @private
     */
    bindInputEvents: function() {
        var $this = this;

        // GitHub #6447: browser auto fill fix
        this.input.off('blur.inputnumber').on('blur.inputnumber', function(e) {
            var element = AutoNumeric.getAutoNumericElement(this);
            if (element && this.value && this.value.length > 0) {
                element.set(this.value, null, true);
            }
            $this.copyValueToHiddenInput();
        });
    },

    /**
     * Wraps the events on the external (visible) input to copy the value to the hidden input.
     * @private
     * @return {number} The original value of the hidden input.
     */
    copyValueToHiddenInput: function() {
        var oldVal = this.hiddenInput.val();
        var newVal = this.getValue();

        if (((oldVal === '') ^ (newVal === '')) || Number(oldVal) !== Number(newVal)) {
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
        this.hiddenInput.trigger('change');
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
     * @param {number | string} value The new value to set. If a number, it will be formatted appropriately. If the
     * empty string, resets the value. Any other string is parsed into a number and then the number is set.
     */
    setValue: function (value) {
        this.autonumeric.set(value);
        var cleanVal = this.getValue();
        this.hiddenInput.attr('value', cleanVal);
    },

    /**
     * Finds the current value, which is the raw numerical value without any formatting applied.
     * @return {string} The current value of this input number widget, in its string representation according to the
     * configured format.
     */
    getValue: function () {
        var val = this.autonumeric.getNumericString();
        if (this.autonumeric.getSettings().allowDecimalPadding && val && parseInt(this.cfg.decimalPlaces, 10) > 0) {
            var decimalPlacesToPad;
            if (val.indexOf('.') === -1) {
                decimalPlacesToPad = this.cfg.decimalPlaces;
                val += '.';
            } else {
                var decimalPlacesAlreadyRendered = val.length - val.indexOf('.') - 1;
                decimalPlacesToPad = this.cfg.decimalPlaces - decimalPlacesAlreadyRendered;
            }
            while (decimalPlacesToPad-- > 0) {
                val += '0';
            }
        }
        return val;
    }
});
