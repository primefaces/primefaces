/**
 * __PrimeFaces Spinner Widget__
 * 
 * Spinner is an input component to provide a numerical input via increment and decrement buttons.
 * 
 * @prop {number} cursorOffset Index where the number starts in the input field's string value, i.e. after the
 * {@link SpinnerCfg.prefix}.
 * @prop {JQuery} downButton The DOM element for the button that decrements this spinner's value.
 * @prop {JQuery} input The DOM element for the input with the current value.
 * @prop {number} timer The set-timeout ID for the timer for incrementing or decrementing this spinner when an arrow key
 * is pressed.
 * @prop {JQuery} upButton The DOM element for the button that increments this spinner's value.
 * @prop {number} value The current numerical value of this spinner.
 * 
 * @interface {PrimeFaces.widget.SpinnerCfg} cfg The configuration for the {@link  Spinner| Spinner widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.decimalPlaces Number of decimal places.
 * @prop {string} cfg.decimalSeparator The character separating the integral and fractional parts of the number.
 * @prop {number} cfg.max Minimum allowed value for this spinner.
 * @prop {number} cfg.maxlength Maximum number of characters that may be entered in this field.
 * @prop {number} cfg.min Minimum allowed value for this spinner.
 * @prop {number} cfg.precision The number of digits to appear after the decimal point. 
 * @prop {string} cfg.prefix Prefix added to the displayed value.
 * @prop {boolean} cfg.required Whether this spinner is a required field.
 * @prop {boolean} cfg.rotate Rotate to the minimum value when maximum value is reached and vice versa.
 * @prop {number} cfg.step Stepping factor for each increment and decrement
 * @prop {string} cfg.suffix Suffix added to the displayed value.
 * @prop {string} cfg.thousandSeparator Character for the integral part of the number that separates each group of three
 * digits.
 */
PrimeFaces.widget.Spinner = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.input = this.jq.children('.ui-spinner-input');
        this.upButton = this.jq.children('a.ui-spinner-up');
        this.downButton = this.jq.children('a.ui-spinner-down');
        this.cfg.step = this.cfg.step || 1;
        if (this.cfg.thousandSeparator == undefined) {
          this.cfg.thousandSeparator = '';
        }
        if (!this.cfg.decimalSeparator) {
          this.cfg.decimalSeparator = '.';
        }
        this.cursorOffset = this.cfg.prefix ? this.cfg.prefix.length: 0;

        var inputValue = this.input.val();

        this.cfg.precision = 0;
        var decPlaces = parseInt(this.cfg.decimalPlaces, 10);
        if(decPlaces > 0) {
            this.cfg.precision = decPlaces;
        }
        else if(!(typeof this.cfg.step === 'number' && this.cfg.step % 1 === 0)) {
            this.cfg.precision = this.cfg.step.toString().split(/[,]|[.]/)[1].length;
        }

        var maxlength = this.input.attr('maxlength');
        if(maxlength) {
            this.cfg.maxlength = parseInt(maxlength);
        }

        this.value = this.parseValue(inputValue);

        this.format();

        this.addARIA();

        if(this.input.prop('disabled')||this.input.prop('readonly')) {
            return;
        }

        this.bindEvents();

        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        PrimeFaces.skinInput(this.input);
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.jq.children('.ui-spinner-button')
            .on('mouseover.spinner', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.spinner', function() {
                $(this).removeClass('ui-state-hover ui-state-active');

                if($this.timer) {
                    clearInterval($this.timer);
                }
            })
            .on('mouseup.spinner', function() {
                clearInterval($this.timer);
                $(this).removeClass('ui-state-active').addClass('ui-state-hover');
                $this.input.trigger('change');
            })
            .on('mousedown.spinner', function(e) {
                var element = $(this),
                dir = element.hasClass('ui-spinner-up') ? 1 : -1;

                element.removeClass('ui-state-hover').addClass('ui-state-active');

                if($this.input.is(':not(:focus)')) {
                    $this.input.trigger('focus');
                }

                $this.repeat(null, dir);

                //keep focused
                e.preventDefault();
        });

        this.input.on('keydown.spinner', function (e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                    $this.spin(1);
                break;

                case keyCode.DOWN:
                    $this.spin(-1);
                break;

                case keyCode.ENTER:
                    $this.updateValue();
                    $this.format();
                break;

                case keyCode.BACKSPACE:
                case keyCode.DELETE:
                case keyCode.LEFT:
                case keyCode.RIGHT:
                    return;
                break;

                default:
                    //do nothing
                break;
            }

            /* Github #1964 do not allow minus */
            var isNegative = event.key === '-';
            if ($this.cfg.min >= 0 && isNegative) {
                e.preventDefault();
                return;
            }

            /* GitHub #5579 do not allow decimal separator for integers */
            var isDecimalSeparator = event.key === $this.cfg.decimalSeparator;
            if (isDecimalSeparator && $this.cfg.precision === 0) {
                e.preventDefault();
                return;
            }

            /* GitHub #5579 prevent non numeric characters and duplicate separators */
            var value = $(this).val();
            var isNumber = isFinite(event.key);
            var isThousandsSeparator = event.key === $this.cfg.thousandSeparator;
            if ((isNegative && value.indexOf('-') != -1) 
                    || (isDecimalSeparator && value.indexOf($this.cfg.decimalSeparator)!= -1)
                    || (isThousandsSeparator && value.indexOf($this.cfg.thousandSeparator)!= -1)) {
                e.preventDefault();
                return;
            } 

            if (!isNumber && !(isNegative || isDecimalSeparator || isThousandsSeparator)) {
                e.preventDefault();
                return;
            }
        })
        .on('keyup.spinner', function (e) {
            $this.updateValue();

            var keyCode = $.ui.keyCode;

            /* Github #2636 */
            var checkForIE = (PrimeFaces.env.isIE(11) || PrimeFaces.env.isLtIE(11)) && (e.which === keyCode.ENTER);

            if(e.which === keyCode.UP||e.which === keyCode.DOWN||checkForIE) {
                $this.input.trigger('change');
                $this.format();
            }
        })
        .on('blur.spinner', function(e) {
            $this.format();
        })
        .on('mousewheel.spinner', function(event, delta) {
            if($this.input.is(':focus')) {
                if(delta > 0)
                    $this.spin(1);
                else
                    $this.spin(-1);
                
                $this.input.trigger('change');

                return false;
            }
        });
    },

    /**
     * Increments or decrements this spinner rapidly, at a rate of one step each few frames. Used when the user keeps
     * pressing the up or down arrow button.
     * @private
     * @param {number} interval Initial delay in milliseconds, applied after the first increment or decrement, before
     * this spinner starts incrementing or decrementing rapidly.
     * @param {-1 | 1} dir `-1` to decrement this spinner, or `+1` to increment this spinner.
     */
    repeat: function(interval, dir) {
        var $this = this,
        i = interval||500;

        clearTimeout(this.timer);
        this.timer = setTimeout(function() {
            $this.repeat(40, dir);
        }, i);

        this.spin(dir);
    },

    /**
     * Increments or decrements this spinner by one {@link SpinnerCfg.step}.
     * @param {-1 | 1} dir `-1` to decrement this spinner, or `+1` to increment this spinner.
     */
    spin: function(dir) {
        var step = this.cfg.step * dir,
        currentValue = this.value ? this.value : 0,
        newValue = this.parseValue(currentValue + step);

        if(this.cfg.maxlength !== undefined && newValue.toString().length > this.cfg.maxlength) {
            newValue = currentValue;
        }

        this.value = newValue;
        this.format();
        this.input.attr('aria-valuenow', this.getValue());
    },

    /**
     * Callback for when the value of the input was changed. Parses the current values and saves it.
     * @private
     */
    updateValue: function() {
        var value = this.input.val();

        if(this.cfg.prefix && value.indexOf(this.cfg.prefix) === 0) {
            value = value.substring(this.cfg.prefix.length, value.length);
        }  else {
            var ix = value.indexOf(this.cfg.suffix);
            if(this.cfg.suffix && ix > -1 && ix === (value.length - this.cfg.suffix.length)) {
                value = value.substring(0, value.length - this.cfg.suffix.length);
            }
        }

        value = value.replace(new RegExp(PrimeFaces.escapeRegExp(this.cfg.thousandSeparator), 'g'), '');
        value = value.replace(new RegExp(PrimeFaces.escapeRegExp(this.cfg.decimalSeparator), 'g'), '\.');
        this.value = this.parseValue(value);
    },

    /**
     * Takes the string representation of a number, parses it and restricts it to the limits imposed by the 
     * {@link SpinnerCfg|configuration of this widget}.
     * @private
     * @param {string} value String to parse as a number.
     * @return {number | null} The parsed value, clamped to the allowed range, or `null` if the value could not be
     * parsed.
     */
    parseValue: function(value) {
        var parsedValue;
        if(this.cfg.precision) {
            parsedValue = parseFloat(value);
        } else {
            parsedValue = parseInt(value);
        }
        if(isNaN(parsedValue)) {
            if(PrimeFaces.trim(value) === '' && this.cfg.min !== undefined && this.cfg.required) {
                parsedValue = this.cfg.min;
            } else {
                parsedValue = null;
            }
        } else {
            var minimum = this.cfg.min;
            var maximum = this.cfg.max;

            if (this.cfg.rotate) {
                if(parsedValue < minimum) {
                    parsedValue = maximum;
                }
                if(parsedValue > maximum) {
                    parsedValue = minimum;
                }
            } else {
                if(parsedValue > maximum) {
                    parsedValue = maximum;
                }
                if(parsedValue < minimum) {
                    parsedValue = minimum;
                }
            }
        }
        return parsedValue;
    },

    /**
     * Takes the current numerical value of this spinner, formats it according to the
     * {@link SpinnerCfg|configuration of this widget}, and writes the result to the input field.
     * @private
     */
    format: function() {
        if(this.value !== null) {
            var value = this.getValue();
            var numAndFract = value.toString().split('.');
            value = numAndFract[0].replace(/(\d)(?=(?:\d{3})+\b)/g, '$1' + this.cfg.thousandSeparator);
            if (numAndFract.length === 2) {
              value += this.cfg.decimalSeparator + numAndFract[1];
            }
            value = this.roundStep(value);
            if(this.cfg.prefix)
                value = this.cfg.prefix + value;

            if(this.cfg.suffix)
                value = value + this.cfg.suffix;

            this.input.val(value);
        }
    },

    /**
     * If roundStep is enabled then round to the nearest step value. 
     * For example if step=5 and value=8 it would be rounded 10.
     * @private
     * @param {number} value The value for this spinner.
     * @return {number} Original value if rounding disabled, else a rounded value.
     */
    roundStep: function(value) {
        if (!this.cfg.round) {
            return value;
        }
        return (Math.ceil(value / this.cfg.step) * this.cfg.step).toFixed(this.cfg.precision);
    },

    /**
     * Adds the required ARIA attributes to the elements of this spinner.
     * @private
     */
    addARIA: function() {
        this.input.attr('role', 'spinbutton');
        this.input.attr('aria-multiline', false);
        this.input.attr('aria-valuenow', this.getValue());

        if(this.cfg.min !== undefined)
            this.input.attr('aria-valuemin', this.cfg.min);

        if(this.cfg.max !== undefined)
            this.input.attr('aria-valuemax', this.cfg.max);

        if(this.input.prop('disabled'))
            this.input.attr('aria-disabled', true);

        if(this.input.prop('readonly'))
            this.input.attr('aria-readonly', true);
    },

    /**
     * Reads and returns the value of this spinner.
     * @return {number} The current numerical value of this spinner.
     */
    getValue: function() {
        if(this.cfg.precision) {
            return parseFloat(this.value).toFixed(this.cfg.precision);
        }
        else {
            return this.value;
        }
    },

    /**
     * Sets the value of this spinner to the given number.
     * @param {number} value The new value for this spinner.
     */
    setValue: function(value) {
        this.value = value;
        this.format();
    }
});
