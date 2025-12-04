/**
 * __PrimeFaces Spinner Widget__
 *
 * Spinner is an input component to provide a numerical input via increment and decrement buttons.
 *
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
 * @prop {boolean} cfg.rotate Rotate to the minimum value when maximum value is reached and vice versa.
 * @prop {number} cfg.step Stepping factor for each increment and decrement
 * @prop {string} cfg.suffix Suffix added to the displayed value.
 * @prop {string} cfg.thousandSeparator Character for the integral part of the number that separates each group of three
 * digits.
 * @prop {boolean} cfg.modifyValueOnWheel Increment or decrement the element value with the mouse wheel if true.
 */
PrimeFaces.widget.Spinner = class Spinner extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.input = this.jq.children('.ui-spinner-input');
        this.upButton = this.jq.children('button.ui-spinner-up');
        this.downButton = this.jq.children('button.ui-spinner-down');
        this.cfg.step = this.cfg.step || 1;
        if (this.cfg.thousandSeparator == undefined) {
          this.cfg.thousandSeparator = '';
        }
        if (this.cfg.decimalSeparator == undefined) {
          this.cfg.decimalSeparator = '.';
        }
        this.cfg.modifyValueOnWheel = this.cfg.modifyValueOnWheel !== false;

        var inputValue = this.input.val();

        this.cfg.precision = 0;
        var decPlaces = parseInt(this.cfg.decimalPlaces, 10);
        if(decPlaces > 0) {
            this.cfg.precision = decPlaces;
        }
        else if(!(typeof this.cfg.step === 'number' && this.cfg.step % 1 === 0)) {
            this.cfg.precision = this.cfg.step.toString().split(/,|[.]/)[1].length;
        }

        var maxlength = this.input.attr('maxlength');
        if(maxlength) {
            this.cfg.maxlength = parseInt(maxlength);
        }

        this.value = this.parseValue(inputValue);

        this.format();

        if(this.input.prop('disabled')||this.input.prop('readonly')) {
            return;
        }

        this.bindEvents();

        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        PrimeFaces.skinInput(this.input);
        this.addARIA();
    }

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents() {
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
                // only act on left click
                if (e.button !== 0) {
                    return;
                }
                var element = $(this),
                dir = element.hasClass('ui-spinner-up') ? 1 : -1;

                element.removeClass('ui-state-hover').addClass('ui-state-active');

                $this.repeat(null, dir);
        });

        this.input.on('keydown.spinner', function (e) {
            switch(e.key) {
                case 'ArrowUp':
                    $this.spin(1);
                break;

                case 'ArrowDown':
                    $this.spin(-1);
                break;

                case 'Enter':
                case 'NumpadEnter':
                    $this.updateValue();
                    $this.format();
                break;
                
                case 'Backspace':
                case 'Delete':
                    return;

                default:
                    //do nothing
                break;
            }

            // #8958 allow TAB, F1, F12 etc
            if (!PrimeFaces.utils.isPrintableKey(e)) {
                return;
            }
            // #10714 allow clipboard events
            if (PrimeFaces.utils.isClipboardKey(e)) {
                return;
            }

            /* Github #1964 do not allow minus */
            var isNegative = e.key === '-';
            if ($this.cfg.min >= 0 && isNegative) {
                e.preventDefault();
                return;
            }

            /* GitHub #5579 do not allow decimal separator for integers */
            var isDecimalSeparator = e.key === $this.cfg.decimalSeparator;
            if (isDecimalSeparator && $this.cfg.precision === 0) {
                e.preventDefault();
                return;
            }

            /* GitHub #5579 prevent non numeric characters and duplicate separators */
            var value = $(this).val();
            var isNumber = isFinite(e.key);
            var isThousandsSeparator = e.key === $this.cfg.thousandSeparator;
            if ((isNegative && value.indexOf('-') != -1)
                    || (isDecimalSeparator && value.indexOf($this.cfg.decimalSeparator)!= -1)
                    || (isThousandsSeparator && value.indexOf($this.cfg.thousandSeparator)!= -1)) {
                e.preventDefault();
                return;
            }

            if (!isNumber && !(isNegative || isDecimalSeparator || isThousandsSeparator)) {
                e.preventDefault();
            }
        })
        .on('keyup.spinner', function (e) {
            $this.updateValue();

            if(e.key === 'ArrowUp' || e.key === 'ArrowDown') {
                $this.input.trigger('change');
                $this.format();
            }
        })
        .on('paste.spinner', function(e) {
            PrimeFaces.queueTask(function() {
                $this.updateValue();
                if ($this.value) {
                    $this.input.trigger('change');
                    $this.format();
                }
                else {
                    $this.input.val('');
                }
            });
        })
        .on('blur.spinner', function(e) {
            $this.format();
        })
        .on('mousewheel.spinner', function(event, delta) {
            if($this.cfg.modifyValueOnWheel && $this.input.is(':focus')) {
                if(delta > 0)
                    $this.spin(1);
                else
                    $this.spin(-1);

                $this.input.trigger('change');

                return false;
            }
        });
    }

    /**
     * Increments or decrements this spinner rapidly, at a rate of one step each few frames. Used when the user keeps
     * pressing the up or down arrow button.
     * @private
     * @param {number} interval Initial delay in milliseconds, applied after the first increment or decrement, before
     * this spinner starts incrementing or decrementing rapidly.
     * @param {-1 | 1} dir `-1` to decrement this spinner, or `+1` to increment this spinner.
     */
    repeat(interval, dir) {
        var $this = this,
        i = interval||500;

        clearTimeout(this.timer);
        this.timer = PrimeFaces.queueTask(function() {
            $this.repeat(40, dir);
        }, i);

        this.spin(dir);
    }

    /**
     * Increments or decrements this spinner by one {@link SpinnerCfg.step}.
     * @param {-1 | 1} dir `-1` to decrement this spinner, or `+1` to increment this spinner.
     */
    spin(dir) {
        var step = this.cfg.step * dir,
        currentValue = this.value ? this.value : 0,
        newValue = currentValue + step;
        
        if (Number.isSafeInteger(step)) {
            // GitHub #8631 round to nearest step
            newValue = (dir > 0) ? Math.floor(newValue / step) * step : Math.ceil(newValue / step) * step;
        }

        newValue = this.parseValue(newValue);

        if(this.cfg.maxlength !== undefined && newValue.toString().length > this.cfg.maxlength) {
            newValue = currentValue;
        }

        this.value = newValue;
        this.format();
        this.input.attr('aria-valuenow', this.getValue());
    }

    /**
     * Callback for when the value of the input was changed. Parses the current values and saves it.
     * @private
     */
    updateValue() {
        var value = this.input.val();

        if(this.cfg.prefix && value.indexOf(this.cfg.prefix) === 0) {
            value = value.substring(this.cfg.prefix.length, value.length);
        }  else {
            var ix = value.indexOf(this.cfg.suffix);
            if(this.cfg.suffix && ix > -1 && ix === (value.length - this.cfg.suffix.length)) {
                value = value.substring(0, value.length - this.cfg.suffix.length);
            }
        }

        if(this.cfg.thousandSeparator) {
            value = value.replace(new RegExp(PrimeFaces.escapeRegExp(this.cfg.thousandSeparator), 'g'), '');
        }
        if(this.cfg.decimalSeparator) {
            value = value.replace(new RegExp(PrimeFaces.escapeRegExp(this.cfg.decimalSeparator), 'g'), '.');
        }

        this.value = this.parseValue(value);
    }

    /**
     * Takes the string representation of a number, parses it and restricts it to the limits imposed by the
     * {@link SpinnerCfg|configuration of this widget}.
     * @private
     * @param {string} value String to parse as a number.
     * @return {number | null} The parsed value, clamped to the allowed range, or `null` if the value could not be
     * parsed.
     */
    parseValue(value) {
        var parsedValue;
        if(this.cfg.prefix && value && isNaN(value) && value.indexOf(this.cfg.prefix) === 0) {
            value = value.substring(this.cfg.prefix.length, value.length);
        }
        if(this.cfg.precision) {
            parsedValue = parseFloat(value);
        } else {
            parsedValue = parseInt(value);
        }
        if(isNaN(parsedValue)) {
            parsedValue = null;
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
    }

    /**
     * Takes the current numerical value of this spinner, formats it according to the
     * {@link SpinnerCfg|configuration of this widget}, and writes the result to the input field.
     * @private
     */
    format() {
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
    }

    /**
     * If roundStep is enabled then round to the nearest step value.
     * For example if step=5 and value=8 it would be rounded 10.
     * @private
     * @param {number} value The value for this spinner.
     * @return {number} Original value if rounding disabled, else a rounded value.
     */
    roundStep(value) {
        if (!this.cfg.round) {
            return value;
        }
        var numericValue = parseFloat(typeof value === 'string' ? value.replace(this.cfg.thousandSeparator, '') : value);
        return +(Math.ceil(numericValue / this.cfg.step) * this.cfg.step).toFixed(this.cfg.precision);
    }

    /**
     * Adds the required ARIA attributes to the elements of this spinner.
     * @private
     */
    addARIA() {
        this.input.attr('role', 'spinbutton');
        this.input.attr('aria-valuenow', this.getValue());

        if(this.cfg.min !== undefined)
            this.input.attr('aria-valuemin', this.cfg.min);

        if(this.cfg.max !== undefined)
            this.input.attr('aria-valuemax', this.cfg.max);

        if(this.input.prop('disabled'))
            this.input.attr('aria-disabled', true);

        if(this.input.prop('readonly'))
            this.input.attr('aria-readonly', true);
        
        this.upButton.attr('aria-label', this.getAriaLabel('spinner.INCREASE'));
        this.downButton.attr('aria-label', this.getAriaLabel('spinner.DECREASE'));
    }

    /**
     * Reads and returns the value of this spinner.
     * @return {number} The current numerical value of this spinner.
     */
    getValue() {
        if(this.cfg.precision) {
            return parseFloat(this.value).toFixed(this.cfg.precision);
        }
        else {
            return this.value;
        }
    }

    /**
     * Sets the value of this spinner to the given number.
     * @param {number} value The new value for this spinner.
     */
    setValue(value) {
        this.value = value;
        this.format();
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    enable() {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    }
}