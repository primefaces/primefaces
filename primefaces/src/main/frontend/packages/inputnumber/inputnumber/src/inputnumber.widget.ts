import AutoNumeric from "autonumeric";

/**
 * The AutoNumeric instance, with the fields that exist at runtime but are missing in the AutoNumeric typings.
 */
type AutoNumericInstance = AutoNumeric & {
    /** The raw (unformatted) value. */
    rawValue: string;
    /** The raw value when the input was focused, used to decide whether a change event is dispatched. */
    rawValueOnFocus: string | undefined;
};

/**
 * The configuration for the {@link InputNumber} widget.
 */
export interface InputNumberCfg extends PrimeFacesTypes.widget.BaseWidgetCfg, PrimeFacesTypes.widget.InputNumber.AutoNumericOptions {
    /** Whether this widget is initially disabled. */
    disabled: boolean;
    /** Always undefined. */
    pluginOptions: undefined;
    /** The initial, numerical value that is displayed, such as `0.0` or `5.3`. */
    valueToRender: string;
}

/**
 * __PrimeFaces InputNumber Widget__
 *
 * InputNumber formats input fields with numeric strings. It supports currency symbols, minimum and maximum value,
 * negative numbers, and a lot of round methods.
 */
export class InputNumber<Cfg extends InputNumberCfg = InputNumberCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The configuration, the decimal places are converted to numbers by {@link init}. */
    declare cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg> & { decimalPlaces: number; decimalPlacesRawValue: number; };

    /** The current AutoNumeric instance. */
    autonumeric?: AutoNumericInstance;
    /** Whether this widget is currently disabled, i.e. whether the user can enter a number. */
    disabled: boolean = false;
    /** The DOM element for the hidden input field with the current value of this widget. */
    hiddenInput: JQuery = $();
    /** The DOM element for the visible input field with autoNumeric. */
    input: JQuery = $();
    /** Always `undefined`. */
    plugOptArray: undefined;
    /** The initial, numerical value that is displayed, such as `0.0` or `5.3`. */
    initialValue: string = "";

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.input = $(this.jqId + '_input');
        this.hiddenInput = $(this.jqId + '_hinput');
        this.plugOptArray = cfg.pluginOptions;
        this.initialValue = cfg.valueToRender!;
        this.disabled = cfg.disabled!;
        this.cfg.decimalPlaces = Number(this.cfg.decimalPlaces);
        this.cfg.decimalPlacesRawValue = Number(this.cfg.decimalPlacesRawValue || this.cfg.decimalPlaces);

        // GitHub #8125 minValue>0 shows js warning and quirky behavior
        // (the values are strings, compared as numbers like before)
        if (Number(this.cfg.minimumValue) > 0.0000001 || Number(this.cfg.maximumValue) < 0) {
            this.cfg.overrideMinMaxLimits = 'invalid';
        }

        //bind events if not disabled
        if (this.disabled) {
            this.input.attr("disabled", "disabled");
            this.input.addClass("p-state-disabled");
            this.hiddenInput.attr("disabled", "disabled");
        }

        //Visual effects
        PrimeFaces.skinInput(this.input);

        this.autonumeric = new AutoNumeric(this.jqId + '_input', this.cfg) as AutoNumericInstance;

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
        PrimeFaces.queueTask(() => { this.wrapEvents(); });
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this._cleanup();
        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();
        this._cleanup();
    }

    /**
     * Clean up this widget and remove events from the DOM.
     */
    private _cleanup(): void {
        if (this.autonumeric) {
            this.autonumeric.remove();
        }
    }

    /**
     * Wraps the events on the external (visible) input to copy the value to the hidden input, before calling the
     * callback.
     */
    private wrapEvents(): void {
        // Helper to wrap event handler
        const wrapEventHandler = (eventName: string, additionalLogic?: (this: HTMLInputElement, e: JQuery.TriggeredEvent) => string | false | undefined) => {
            // DOM handler if non-CSP, JQ handler if CSP
            const originalHandler = PrimeFaces.utils.getOriginalEventHandler(this.input, eventName);
            this.input.prop('on' + eventName, null)
                .off(eventName)
                .on(eventName + '.inputnumber', (e) => {
                    let oldValue;
                    if (additionalLogic) {
                        oldValue = additionalLogic.call(e.currentTarget as HTMLInputElement, e);
                    } else {
                        oldValue = this.copyValueToHiddenInput();
                    }

                    if (originalHandler && originalHandler.call(e.currentTarget, e) === false) {
                        if (oldValue) {
                            this.setValueToHiddenInput(oldValue);
                        }
                        return false;
                    }
                    return undefined;
                });
        };

        // Keyup handler with special key checks
        wrapEventHandler('keyup', (e) => {
            const key = e.key;
            const cutCopyPaste = (e.ctrlKey && ['KeyX', 'KeyC', 'KeyV'].includes((e as JQuery.TriggeredEvent & { code?: string }).code!));
            if (cutCopyPaste || ['Backspace', 'Enter', 'Delete'].includes(key!) || PrimeFaces.utils.isPrintableKey(e)) {
                return this.copyValueToHiddenInput();
            }
            return undefined;
        });

        // Change handler with value comparison
        const $this = this;
        wrapEventHandler('change', function (e) {
            const element = $this.autonumeric;
            if (!element) {
                return undefined;
            }

            // Get the numeric value
            let newValue: string | null = '';
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
                    newValue = newValue.split($this.cfg.digitGroupSeparator).join(''); // replaceAll, not in the ES2016 lib
                }
                if ($this.cfg.currencySymbol) {
                    newValue = newValue.split($this.cfg.currencySymbol).join(''); // replaceAll, not in the ES2016 lib
                }

                // Set the cleaned value
                // (AutoNumeric uses null as default for the options, undefined is the same)
                element.set(newValue.trim(), undefined, true);

                // GitHub #8610: reset the raw values so we don't fire change event if 1.0 == 1.00
                if (element.rawValueOnFocus !== '' && element.rawValue !== '' && Number(element.rawValue) === Number(element.rawValueOnFocus)) {
                    element.rawValueOnFocus = element.rawValue;
                }
            }

            const copiedValue = $this.copyValueToHiddenInput();
            // #10046 do not call on Change if the value has not changed
            if (copiedValue === $this.initialValue ||
                ($this.initialValue !== '' && copiedValue !== '' && Number(copiedValue) === Number($this.initialValue))) {
                return false;
            }
            $this.initialValue = copiedValue;
            return copiedValue;
        });

        // Simple input and keydown handlers
        wrapEventHandler('input');
        wrapEventHandler('keydown');
    }

    /**
     * Wraps the events on the external (visible) input to copy the value to the hidden input.
     * @return The original value of the hidden input.
     */
    private copyValueToHiddenInput(): string {
        const oldVal = this.hiddenInput.val() as string;
        let newVal = this.getValue();

        if (this.cfg.emptyInputBehavior === 'null' && newVal === null) {
            newVal = '';
        }

        if (((oldVal === '') !== (newVal === '')) || Number(oldVal) !== Number(newVal)) {
            this.setValueToHiddenInput(newVal);
        }

        return oldVal;
    }

    /**
     * Writes the given value to the hidden input field that stores the actual value of this widget.
     * @param value A value to set on the hidden input.
     */
    private setValueToHiddenInput(value: string | null): void {
        // jQuery sets null as the empty string
        this.hiddenInput.val(value as string).trigger('input.slider');
    }

    /**
     * Enables this input field, so that the user can enter data.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.input);
        PrimeFaces.utils.enableInputWidget(this.hiddenInput);
        this.disabled = false;
    }

    /**
     * Enables this input field, so that the user cannot enter data.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.input, undefined);
        PrimeFaces.utils.disableInputWidget(this.hiddenInput, undefined);
        this.disabled = true;
    }

    /**
     * Sets the value of this input number widget to the given value. Makes sure that the number is formatted correctly.
     * @param value The new value to set. If a number, it will be formatted appropriately. If the
     * empty string, resets the value. Any other string is parsed into a number and then the number is set.
     */
    setValue(value: number | string): void {
        this.autonumeric!.set(value);
        const cleanVal = this.getValue();
        this.hiddenInput.attr('value', cleanVal);
    }

    /**
     * Finds the current value, which is the raw numerical value without any formatting applied.
     * @return The current value of this input number widget, in its string representation according to the
     * configured format.
     */
    getValue(): string | null {
        let val = this.autonumeric!.getNumericString();
        if (this.autonumeric!.getSettings().allowDecimalPadding && val && parseInt(String(this.cfg.decimalPlaces), 10) > 0) {
            let decimalPlacesToPad;
            if (val.indexOf('.') === -1) {
                decimalPlacesToPad = this.cfg.decimalPlaces;
                val += '.';
            } else {
                const decimalPlacesAlreadyRendered = val.length - val.indexOf('.') - 1;
                decimalPlacesToPad = this.cfg.decimalPlaces - decimalPlacesAlreadyRendered;
            }
            while (decimalPlacesToPad-- > 0) {
                val += '0';
            }
        }

        // GitHub #7911 Autonumeric bug workaround.
        // Remove when issue fixed: https://github.com/autoNumeric/autoNumeric/issues/670
        if (val && val.endsWith(this.cfg.decimalCharacter!)) {
            val = val.substring(0, val.length - 1);
        }
        return val;
    }
}
