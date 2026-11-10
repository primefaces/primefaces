/**
 * The configuration for the {@link InputMask} widget.
 */
export interface InputMaskCfg extends PrimeFacesTypes.widget.BaseWidgetCfg, Inputmask.Options {
    /** Whether an incomplete value is cleared on blur, mapped to the `clearIncomplete` option. */
    autoClear: boolean;
    /** The mask template to use. */
    mask: string;
}

/**
 * __PrimeFaces InputMask Widget__
 *
 * InputMask forces an input to fit in a defined mask template.
 */
export class InputMask<Cfg extends InputMaskCfg = InputMaskCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Is this component wrapped in a float label. */
    hasFloatLabel: boolean = false;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.hasFloatLabel = PrimeFaces.utils.hasFloatLabel(this.jq);

        this.applyMask();

        //Visuals
        PrimeFaces.skinInput(this.jq);
    }

    override destroy(): void {
        this.jq.inputmask('remove');

        super.destroy();
    }

    /**
     * Applys the mask to the input.
     */
    private applyMask(): void {
        if (this.hasFloatLabel) {
            this.cfg.showMaskOnHover = false;
        }
        if (this.cfg.mask && !this.jq.is('[readonly]') && !this.jq.is(':disabled')) {
            this.cfg.clearIncomplete = (this.cfg.autoClear === undefined) ? true : this.cfg.autoClear;
            // two calls instead of a chain, @types/inputmask declares 'remove' as void (it returns the jQuery object)
            this.jq.inputmask('remove');
            this.jq.inputmask(this.cfg);
        }
    }

    /**
     * Sets the value of this input field to the given value. If the value does not fit the mask, it is adjusted
     * appropriately.
     * @param value New value to set on this input field
     */
    setValue(value: string): void {
        this.jq.inputmask("setvalue", value);
    }

    /**
     * Returns the current value of this input field including the mask like "12/31/1999".
     * @returns The current value of this input field with mask.
     */
    getValue(): string {
        return this.jq.val() as string;
    }

    /**
     * Returns the current value of this input field without the mask like "12311999".
     * @returns The current value of this input field without mask.
     */
    getValueUnmasked(): string {
        return this.jq.inputmask('unmaskedvalue');
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    override disable(): void {
        this.jq.inputmask('remove');
        PrimeFaces.utils.disableInputWidget(this.jq, undefined);
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq);
        this.applyMask();
    }
}
