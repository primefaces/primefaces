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
 * @prop {string} initialValue The initial, numerical value that is displayed, such as `0.0` or `5.3`.
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
    init: function(cfg) {
        this._super(cfg);
        this.input = $(this.jqId + '_input');
        this.hiddenInput = $(this.jqId + '_hinput');
        this.plugOptArray = cfg.pluginOptions;
        this.initialValue = cfg.valueToRender;
        this.disabled = cfg.disabled;
        this.cfg.decimalPlaces = Number(this.cfg.decimalPlaces);
        this.cfg.decimalPlacesRawValue = Number(this.cfg.decimalPlacesRawValue || this.cfg.decimalPlaces);

        // GitHub #8125 minValue>0 shows js warning and quirky behavior
        if (this.cfg.minimumValue > 0.0000001 || this.cfg.maximumValue < 0) {
            this.cfg.overrideMinMaxLimits = 'invalid';
        }

        //bind events if not disabled
        if (this.disabled) {
            this.input.attr("disabled", "disabled");
            this.input.addClass("ui-state-disabled");
            this.hiddenInput.attr("disabled", "disabled");
        }

        //Visual effects
        PrimeFaces.skinInput(this.input);

        this.autonumeric = new AutoNumeric(this.jqId + '_input', this.cfg);

        if (this.initialValue !== "") {
            //set the value to the input the plugin will format it.
            this.autonumeric.set(this.initialValue);
            // GitHub #6940 blur firing too many change events
            this.autonumeric.rawValueOnFocus = this.initialValue;
        }

        this.setValueToHiddenInput(this.getValue());

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hiddenInput.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        // GitHub #10046 delay registering events until CSP has registered
        var $this = this;
        PrimeFaces.queueTask(function () {$this.wrapEvents();});
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this._cleanup();
        this._super(cfg);
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();
        this._cleanup();
    },
    
   /**
    * Clean up this widget and remove events from the DOM.
    * @private
    */
    _cleanup: function() {
        if (this.autonumeric) {
            this.autonumeric.remove();
        }
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

        // Helper to get original handler
        var getOriginalHandler = function(eventName) {
            var originalProp = $this.input.prop('on' + eventName);
            return !originalProp && events && events[eventName] ? events[eventName][0].handler : originalProp;
        };

        // Helper to wrap event handler
        var wrapEventHandler = function(eventName, additionalLogic) {
            var originalHandler = getOriginalHandler(eventName);
            $this.input.prop('on' + eventName, null)
                      .off(eventName)
                      .on(eventName + '.inputnumber', function(e) {
                var oldValue;
                if (additionalLogic) {
                    oldValue = additionalLogic.call(this, e);
                } else {
                    oldValue = $this.copyValueToHiddenInput();
                }
                
                if (originalHandler && originalHandler.call(this, e) === false) {
                    if (oldValue) {
                        $this.setValueToHiddenInput(oldValue);
                    }
                    return false;
                }
            });
        };

        // Keyup handler with special key checks
        wrapEventHandler('keyup', function(e) {
            var key = e.key;
            var cutCopyPaste = (e.ctrlKey && ['KeyX', 'KeyC', 'KeyV'].includes(e.code));
            if (cutCopyPaste || ['Backspace', 'Enter', 'Delete'].includes(key) || PrimeFaces.utils.isPrintableKey(e)) {
                return $this.copyValueToHiddenInput();
            }
        });

        // Change handler with value comparison
        wrapEventHandler('change', function(e) {
            var element = $this.autonumeric;
            if (!element) {
                return;
            }

            // Get the numeric value
            var newValue = '';
            if ($this.cfg.decimalPlacesRawValue > $this.cfg.decimalPlaces) {
                // if using raw decimal places we need to get the numeric string
                newValue = element.getNumericString();
            } else {
                // if using decimal places we can use the input value
                newValue = this.value;
            }

            // Process the value if it exists, remove formatting characters
            if (newValue && newValue.length > 0) {
                if ($this.cfg.digitGroupSeparator) {
                    newValue = newValue.replaceAll($this.cfg.digitGroupSeparator, '');
                }
                if ($this.cfg.currencySymbol) {
                    newValue = newValue.replaceAll($this.cfg.currencySymbol, '');
                }

                // Set the cleaned value
                element.set(newValue.trim(), null, true);

                // GitHub #8610: reset the raw values so we don't fire change event if 1.0 == 1.00
                if (element.rawValueOnFocus !== '' && element.rawValue !== '' && Number(element.rawValue) === Number(element.rawValueOnFocus)) {
                    element.rawValueOnFocus = element.rawValue;
                }
            }

            var newValue = $this.copyValueToHiddenInput();
            // #10046 do not call on Change if the value has not changed
            if (newValue === $this.initialValue || 
                ($this.initialValue !== '' && newValue !== '' && Number(newValue) === Number($this.initialValue))) {
                return false;
            }
            $this.initialValue = newValue;
            return newValue;
        });

        // Simple input and keydown handlers
        wrapEventHandler('input');
        wrapEventHandler('keydown');
    },

    /**
     * Wraps the events on the external (visible) input to copy the value to the hidden input.
     * @private
     * @return {number} The original value of the hidden input.
     */
    copyValueToHiddenInput: function() {
        var oldVal = this.hiddenInput.val();
        var newVal = this.getValue();

        if (this.cfg.emptyInputBehavior === 'null' && newVal === null) {
            newVal = '';
        }

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
        this.hiddenInput.val(value).trigger('input.slider');
    },

    /**
     * Enables this input field, so that the user can enter data.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.input);
        PrimeFaces.utils.enableInputWidget(this.hiddenInput);
        this.disabled = false;
    },

    /**
     * Enables this input field, so that the user cannot enter data.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.input);
        PrimeFaces.utils.disableInputWidget(this.hiddenInput);
        this.disabled = true;
    },

    /**
     * Sets the value of this input number widget to the given value. Makes sure that the number is formatted correctly.
     * @param {number | string} value The new value to set. If a number, it will be formatted appropriately. If the
     * empty string, resets the value. Any other string is parsed into a number and then the number is set.
     */
    setValue: function(value) {
        this.autonumeric.set(value);
        var cleanVal = this.getValue();
        this.hiddenInput.attr('value', cleanVal);
    },

    /**
     * Finds the current value, which is the raw numerical value without any formatting applied.
     * @return {string} The current value of this input number widget, in its string representation according to the
     * configured format.
     */
    getValue: function() {
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

        // GitHub #7911 Autonumeric bug workaround. 
        // Remove when issue fixed: https://github.com/autoNumeric/autoNumeric/issues/670
        if (val && val.endsWith(this.cfg.decimalCharacter)) {
            val = val.substring(0, val.length - 1);
        }
        return val;
    }
});
