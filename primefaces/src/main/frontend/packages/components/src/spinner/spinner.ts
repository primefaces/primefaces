/**
 * The configuration for the {@link Spinner} widget.
 */
export interface SpinnerCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Number of decimal places, sent as string. */
    decimalPlaces: string;
    /** Separator of the integral and fractional part, defaults to `.`. */
    decimalSeparator: string;
    /** Maximum value. */
    max: number;
    /** Maximum number of characters, read from the `maxlength` attribute of the input on init. */
    maxlength: number;
    /** Minimum value. */
    min: number;
    /** Number of digits after the decimal point, computed on init from `decimalPlaces` or `step`. */
    precision: number;
    /** Prefix of the displayed value. */
    prefix: string;
    /** Whether the value rotates from the maximum to the minimum and vice versa. */
    rotate: boolean;
    /** Whether the value is rounded to the nearest step. */
    round: boolean;
    /** Step of an increment or decrement, defaults to `1`. */
    step: number;
    /** Suffix of the displayed value. */
    suffix: string;
    /** Separator of the groups of three digits, defaults to an empty string. */
    thousandSeparator: string;
    /** Whether the mouse wheel changes the value of the focused input, normalized on init (default `true`). */
    modifyValueOnWheel: boolean;
}

/**
 * __PrimeFaces Spinner Widget__
 *
 * Spinner is an input component to provide a numerical input via increment and decrement buttons.
 */
export class Spinner<Cfg extends SpinnerCfg = SpinnerCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The input with the value. */
    input: JQuery<HTMLInputElement> = $();
    /** The button that increments the value. */
    upButton: JQuery = $();
    /** The button that decrements the value. */
    downButton: JQuery = $();
    /** The timer ID of the repeated increment or decrement while a button is held down. */
    timer?: number;
    /** The current value, `null` if the input is empty or invalid. */
    value?: number | null;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.input = this.jq.children('.p-spinner-input') as JQuery<HTMLInputElement>;
        this.upButton = this.jq.children('button.p-spinner-up');
        this.downButton = this.jq.children('button.p-spinner-down');
        this.cfg.step = this.cfg.step || 1;
        if (this.cfg.thousandSeparator == undefined) {
            this.cfg.thousandSeparator = '';
        }
        if (this.cfg.decimalSeparator == undefined) {
            this.cfg.decimalSeparator = '.';
        }
        this.cfg.modifyValueOnWheel = this.cfg.modifyValueOnWheel !== false;

        const inputValue = this.input.val() as string;

        this.cfg.precision = 0;
        const decPlaces = parseInt(this.cfg.decimalPlaces ?? '', 10);
        if (decPlaces > 0) {
            this.cfg.precision = decPlaces;
        }
        else if (!(typeof this.cfg.step === 'number' && this.cfg.step % 1 === 0)) {
            this.cfg.precision = (this.cfg.step.toString().split(/,|[.]/)[1] ?? '').length;
        }

        const maxlength = this.input.attr('maxlength');
        if (maxlength) {
            this.cfg.maxlength = parseInt(maxlength);
        }

        this.value = this.parseValue(inputValue);

        this.format();

        if (this.input.prop('disabled') || this.input.prop('readonly')) {
            return;
        }

        this.bindEvents();

        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        PrimeFaces.skinInput(this.input);
        this.addARIA();
    }

    private bindEvents(): void {
        this.jq.children('.p-spinner-button')
            .on('mouseover.spinner', (e) => {
                $(e.currentTarget).addClass('p-state-hover');
            })
            .on('mouseout.spinner', (e) => {
                $(e.currentTarget).removeClass('p-state-hover p-state-active');

                if (this.timer) {
                    clearInterval(this.timer);
                }
            })
            .on('mouseup.spinner', (e) => {
                clearInterval(this.timer);
                $(e.currentTarget).removeClass('p-state-active').addClass('p-state-hover');
                this.input.trigger('change');
            })
            .on('mousedown.spinner', (e) => {
                // only act on left click
                if (e.button !== 0) {
                    return;
                }
                const element = $(e.currentTarget);
                const dir = element.hasClass('p-spinner-up') ? 1 : -1;

                element.removeClass('p-state-hover').addClass('p-state-active');

                this.repeat(null, dir);
            });

        this.input.on('keydown.spinner', (e) => {
            switch (e.key) {
                case 'ArrowUp':
                    this.spin(1);
                    break;

                case 'ArrowDown':
                    this.spin(-1);
                    break;

                case 'Enter':
                case 'NumpadEnter':
                    this.updateValue();
                    this.format();
                    break;

                case 'Backspace':
                case 'Delete':
                    return;

                default:
                    // do nothing
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

            // Github #1964 do not allow minus
            const isNegative = e.key === '-';
            if (this.cfg.min !== undefined && this.cfg.min >= 0 && isNegative) {
                e.preventDefault();
                return;
            }

            // GitHub #5579 do not allow decimal separator for integers
            const isDecimalSeparator = e.key === this.cfg.decimalSeparator;
            if (isDecimalSeparator && this.cfg.precision === 0) {
                e.preventDefault();
                return;
            }

            // GitHub #5579 prevent non numeric characters and duplicate separators
            const value = $(e.currentTarget).val() as string;
            const isNumber = isFinite(Number(e.key));
            const isThousandsSeparator = e.key === this.cfg.thousandSeparator;
            if ((isNegative && value.indexOf('-') != -1)
                    || (isDecimalSeparator && value.indexOf(this.cfg.decimalSeparator ?? '') != -1)
                    || (isThousandsSeparator && value.indexOf(this.cfg.thousandSeparator ?? '') != -1)) {
                e.preventDefault();
                return;
            }

            if (!isNumber && !(isNegative || isDecimalSeparator || isThousandsSeparator)) {
                e.preventDefault();
            }
        })
        .on('keyup.spinner', (e) => {
            this.updateValue();

            if (e.key === 'ArrowUp' || e.key === 'ArrowDown') {
                this.input.trigger('change');
                this.format();
            }
        })
        .on('paste.spinner', () => {
            PrimeFaces.queueTask(() => {
                this.updateValue();
                if (this.value !== null && this.value !== undefined) {
                    this.input.trigger('change');
                    this.format();
                }
                else {
                    this.input.val('');
                }
            });
        })
        .on('blur.spinner', () => {
            this.format();
        })
        .on('wheel.spinner', (event) => {
            const delta = (event.originalEvent as WheelEvent | undefined)?.deltaY;
            if (this.cfg.modifyValueOnWheel && this.input.is(':focus') && delta) {
                // wheel up (negative deltaY) increments
                if (delta < 0) {
                    this.spin(1);
                }
                else {
                    this.spin(-1);
                }

                this.input.trigger('change');

                return false;
            }
            return undefined;
        });
    }

    /**
     * Increments or decrements the value repeatedly while a button is held down.
     * @param interval Delay in milliseconds before the next step, `500` if not given.
     * @param dir `-1` to decrement, `1` to increment.
     */
    private repeat(interval: number | null, dir: -1 | 1): void {
        const i = interval || 500;

        clearTimeout(this.timer);
        this.timer = PrimeFaces.queueTask(() => {
            this.repeat(40, dir);
        }, i);

        this.spin(dir);
    }

    /**
     * Increments or decrements the value by one step.
     * @param dir `-1` to decrement, `1` to increment.
     */
    spin(dir: -1 | 1): void {
        const step = (this.cfg.step ?? 1) * dir;
        const currentValue = this.value ? this.value : 0;
        let newValue: number | null | undefined = currentValue + step;

        if (Number.isSafeInteger(step)) {
            // GitHub #8631 round to nearest step
            newValue = (dir > 0) ? Math.floor(newValue / step) * step : Math.ceil(newValue / step) * step;
        }

        newValue = this.parseValue(newValue);

        if (this.cfg.maxlength !== undefined && newValue !== null && newValue !== undefined && newValue.toString().length > this.cfg.maxlength) {
            newValue = currentValue;
        }

        this.value = newValue;
        this.format();
        const ariaValue = this.getValue();
        if (ariaValue !== undefined) {
            this.input.attr('aria-valuenow', ariaValue);
        }
    }

    /**
     * Parses the value of the input, without prefix, suffix and separators.
     */
    private updateValue(): void {
        let value = this.input.val() as string;

        if (this.cfg.prefix && value.indexOf(this.cfg.prefix) === 0) {
            value = value.substring(this.cfg.prefix.length, value.length);
        }
        else {
            const ix = this.cfg.suffix ? value.indexOf(this.cfg.suffix) : -1;
            if (this.cfg.suffix && ix > -1 && ix === (value.length - this.cfg.suffix.length)) {
                value = value.substring(0, value.length - this.cfg.suffix.length);
            }
        }

        if (this.cfg.thousandSeparator) {
            value = value.replace(new RegExp(PrimeFaces.escapeRegExp(this.cfg.thousandSeparator), 'g'), '');
        }
        if (this.cfg.decimalSeparator) {
            value = value.replace(new RegExp(PrimeFaces.escapeRegExp(this.cfg.decimalSeparator), 'g'), '.');
        }

        this.value = this.parseValue(value);
    }

    /**
     * Parses the given value and restricts it to the minimum and maximum.
     * @param value The value to parse.
     * @returns The parsed value, `null` if it is not a number (`undefined` if rotated to a limit that is not set).
     */
    private parseValue(value: string | number | null | undefined): number | null | undefined {
        let parsedValue: number | null | undefined;
        if (this.cfg.prefix && value && typeof value === 'string' && isNaN(Number(value)) && value.indexOf(this.cfg.prefix) === 0) {
            value = value.substring(this.cfg.prefix.length, value.length);
        }
        if (this.cfg.precision) {
            parsedValue = parseFloat(String(value));
        }
        else {
            parsedValue = parseInt(String(value));
        }
        if (isNaN(parsedValue)) {
            parsedValue = null;
        }
        else {
            const minimum = this.cfg.min;
            const maximum = this.cfg.max;

            if (this.cfg.rotate) {
                // without the other limit the value stops at the limit
                if (minimum !== undefined && parsedValue < minimum) {
                    parsedValue = maximum !== undefined ? maximum : minimum;
                }
                else if (maximum !== undefined && parsedValue > maximum) {
                    parsedValue = minimum !== undefined ? minimum : maximum;
                }
            }
            else {
                if (maximum !== undefined && parsedValue > maximum) {
                    parsedValue = maximum;
                }
                if (minimum !== undefined && parsedValue < minimum) {
                    parsedValue = minimum;
                }
            }
        }
        return parsedValue;
    }

    /**
     * Formats the value with separators, prefix and suffix and writes it to the input.
     */
    private format(): void {
        // undefined after a rotate to a limit that is not set, the old code threw here and kept the input
        if (this.value !== null && this.value !== undefined) {
            const numAndFract = String(this.getValue()).split('.');
            let value: string | number = (numAndFract[0] ?? '').replace(/(\d)(?=(?:\d{3})+\b)/g, '$1' + this.cfg.thousandSeparator);
            if (numAndFract.length === 2) {
                value += this.cfg.decimalSeparator + (numAndFract[1] ?? '');
            }
            value = this.roundStep(value);
            if (this.cfg.prefix) {
                value = this.cfg.prefix + value;
            }

            if (this.cfg.suffix) {
                value = value + this.cfg.suffix;
            }

            this.input.val(value);
        }
    }

    /**
     * Rounds the value to the nearest step, if `round` is enabled.
     * @param value The formatted value.
     * @returns The value, or the rounded value as number.
     */
    private roundStep(value: string): string | number {
        if (!this.cfg.round) {
            return value;
        }
        const step = this.cfg.step ?? 1;
        // all thousand separators, the decimal separator may be another than '.'
        let plain = value;
        if (this.cfg.thousandSeparator) {
            plain = plain.split(this.cfg.thousandSeparator).join('');
        }
        if (this.cfg.decimalSeparator) {
            plain = plain.split(this.cfg.decimalSeparator).join('.');
        }
        const numericValue = parseFloat(plain);
        return +(Math.ceil(numericValue / step) * step).toFixed(this.cfg.precision);
    }

    private addARIA(): void {
        this.input.attr('role', 'spinbutton');
        const value = this.getValue();
        if (value !== undefined) {
            this.input.attr('aria-valuenow', value);
        }

        if (this.cfg.min !== undefined) {
            this.input.attr('aria-valuemin', this.cfg.min);
        }

        if (this.cfg.max !== undefined) {
            this.input.attr('aria-valuemax', this.cfg.max);
        }

        if (this.input.prop('disabled')) {
            this.input.attr('aria-disabled', 'true');
        }

        if (this.input.prop('readonly')) {
            this.input.attr('aria-readonly', 'true');
        }

        this.upButton.attr('aria-label', this.getAriaLabel('spinner.INCREASE'));
        this.downButton.attr('aria-label', this.getAriaLabel('spinner.DECREASE'));
    }

    /**
     * @returns The current value: a string with `precision` decimals, otherwise the number (`null` if empty).
     */
    getValue(): string | number | null | undefined {
        if (this.cfg.precision) {
            return parseFloat(String(this.value)).toFixed(this.cfg.precision);
        }
        else {
            return this.value;
        }
    }

    /**
     * Sets the value and formats the input.
     * @param value The new value.
     */
    setValue(value: number | null): void {
        this.value = value;
        this.format();
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    }
}
