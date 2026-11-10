/**
 * The configuration for the {@link InputText} widget.
 */
export interface InputTextCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Client ID of the component that displays the counter. */
    counter: string;
    /** Template of the counter: `{0}` remaining, `{1}` entered and `{2}` maximum characters, defaults to `{0}`. */
    counterTemplate: string;
    /** Whether the counter counts bytes instead of characters. */
    countBytesAsChars: boolean;
    /** Maximum number of characters, only sent if set. */
    maxlength: number;
}

/**
 * __PrimeFaces InputText Widget__
 *
 * InputText is an extension to standard inputText with skinning capabilities.
 */
export class InputText<Cfg extends InputTextCfg = InputTextCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The element that displays the counter, only with `counter`. */
    counter: JQuery | null = null;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        PrimeFaces.skinInput(this.jq);

        if (this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate || '{0}';
            this.updateCounter();

            if (this.counter) {
                this.jq.on('input.inputtext-counter', () => {
                    this.updateCounter();
                });
            }
        }
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, undefined);
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq);
    }

    /**
     * @returns The current value.
     */
    getValue(): string {
        return this.jq.val() as string;
    }

    /**
     * Updates the counter with the remaining and entered characters.
     */
    private updateCounter(): void {
        const value = this.normalizeNewlines(this.jq.val() as string);
        const length = this.cfg.countBytesAsChars ? PrimeFaces.utils.countBytes(value) : value.length;

        if (this.counter && this.cfg.maxlength) {
            let remaining = this.cfg.maxlength - length;
            if (remaining < 0) {
                remaining = 0;
            }

            const counterText = (this.cfg.counterTemplate ?? '')
                    .replace('{0}', String(remaining))
                    .replace('{1}', String(length))
                    .replace('{2}', String(this.cfg.maxlength));

            this.counter.text(counterText);
        }
    }

    /**
     * Replaces all line breaks with carriage return + line feed.
     * @param text Text to normalize.
     * @returns The text with normalized line breaks.
     */
    private normalizeNewlines(text: string): string {
        return text.replace(/(\r\n|\r|\n)/g, '\r\n');
    }
}
