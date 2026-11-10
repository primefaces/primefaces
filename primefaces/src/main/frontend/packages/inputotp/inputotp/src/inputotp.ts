/**
 * The configuration for the {@link InputOtp} widget.
 */
export interface InputOtpCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether the component is disabled. */
    disabled: boolean;
    /** Whether only digits are accepted. */
    integerOnly: boolean;
    /** Called when the value was changed by the user. */
    onchange: (this: InputOtp, value: string) => void;
    /** Whether the component is read only. */
    readonly: boolean;
}

/**
 * __PrimeFaces InputOtp Widget__
 *
 * InputOtp is an input for one-time passwords and PINs with one input per character. Typing moves to the next input,
 * Backspace in an empty input to the previous one, a pasted (or by the browser filled in) code is distributed over
 * the inputs. The value is the concatenation of the characters, kept in a hidden input.
 */
export class InputOtp<Cfg extends InputOtpCfg = InputOtpCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The inputs, one per character. */
    inputs: JQuery<HTMLInputElement> = $();
    /** The hidden input with the submitted value. */
    hiddenInput: JQuery<HTMLInputElement> = $();

    /** The character of each input. */
    private chars: string[] = [];
    /** The value when the focus entered the component, the `change` behavior is called when it leaves with another value. */
    private valueOnFocus: string = '';
    /** Whether the component can't be changed. */
    private disabled: boolean = false;

    /**
     * Initializes the widget: reads the characters rendered by the server and binds the events.
     * @param cfg The configuration of the widget.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.inputs = this.jq.children('.p-inputotp-input') as JQuery<HTMLInputElement>;
        this.hiddenInput = $(this.jqId + '_hidden') as JQuery<HTMLInputElement>;
        this.chars = this.inputs.toArray().map((input) => input.value);
        // the value is what the inputs show (e.g. inputs restored by the browser)
        this.hiddenInput.val(this.getValue());
        this.valueOnFocus = this.getValue();

        const ariaLabel = this.getAriaLabel('otpLabel');
        this.inputs.each((index, input) => {
            if (!input.hasAttribute('aria-label') && ariaLabel) {
                input.setAttribute('aria-label', ariaLabel.replace('{0}', String(index + 1)));
            }
        });

        // client side validation
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hiddenInput.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        PrimeFaces.skinInput(this.inputs);
        this.bindEvents();
        this.setDisabled(!!this.cfg.disabled);
    }

    /**
     * Binds the events of the inputs.
     */
    private bindEvents(): void {
        this.inputs
            .on('input.inputotp', (e) => this.onInput(e))
            .on('paste.inputotp', (e) => this.onPaste(e))
            .on('keydown.inputotp', (e) => this.onKeyDown(e))
            .on('focus.inputotp', (e) => (e.currentTarget as HTMLInputElement).select());

        // the change behavior is called when the focus leaves the component, not when it moves between the inputs
        this.jq
            .on('focusin.inputotp', (e) => {
                if (!this.jq[0]!.contains(e.relatedTarget as Node | null)) {
                    this.valueOnFocus = this.getValue();
                }
            })
            .on('focusout.inputotp', (e) => {
                if (!this.jq[0]!.contains(e.relatedTarget as Node | null) && this.getValue() !== this.valueOnFocus) {
                    this.valueOnFocus = this.getValue();
                    if (this.hasBehavior('change')) {
                        this.callBehavior('change');
                    }
                }
            });
    }

    /**
     * Handles a change of an input: a typed character replaces the one of the input and moves to the next input,
     * several characters (e.g. filled in by the browser) are distributed over the following inputs.
     * @param e The input event.
     */
    private onInput(e: JQuery.TriggeredEvent): void {
        const input = e.currentTarget as HTMLInputElement;
        const index = this.inputs.index(input);
        const event = e.originalEvent as InputEvent | undefined;

        if (event?.inputType?.startsWith('delete')) {
            this.remove(index);
            return;
        }

        // a typed character: the input still contains the old one, the selection may have been lost
        const text = event?.inputType === 'insertText' && event.data ? event.data : input.value;
        const chars = this.sanitize(text);
        // the characters are written by fill(), e.g. a letter with integerOnly is rejected
        input.value = this.chars[index] ?? '';
        if (chars.length > 0) {
            this.fill(index, chars);
        }
    }

    /**
     * Distributes a pasted code over the inputs, starting at the focused one.
     * @param e The paste event.
     */
    private onPaste(e: JQuery.TriggeredEvent): void {
        e.preventDefault();
        if (this.disabled || this.cfg.readonly) {
            return;
        }
        const text = (e.originalEvent as ClipboardEvent | undefined)?.clipboardData?.getData('text') ?? '';
        const chars = this.sanitize(text);
        if (chars.length > 0) {
            this.fill(this.inputs.index(e.currentTarget as HTMLInputElement), chars);
        }
    }

    /**
     * Keyboard navigation: the arrow keys and Home move between the inputs, End to the first empty one, Backspace in an
     * empty input deletes the previous character and moves to it.
     * @param e The keydown event.
     */
    private onKeyDown(e: JQuery.TriggeredEvent): void {
        const index = this.inputs.index(e.currentTarget as HTMLInputElement);
        const rtl = getComputedStyle(this.jq[0]!).direction === 'rtl';
        let target: number | null = null;

        switch (e.key) {
            case 'ArrowLeft':
                target = rtl ? index + 1 : index - 1;
                break;
            case 'ArrowRight':
                target = rtl ? index - 1 : index + 1;
                break;
            case 'Home':
                target = 0;
                break;
            case 'End':
                target = Math.min(this.getFilledCount(), this.inputs.length - 1);
                break;
            case 'ArrowUp':
            case 'ArrowDown':
                // no caret movement
                e.preventDefault();
                return;
            case 'Backspace':
                if (!this.chars[index] && index > 0 && !this.cfg.readonly) {
                    e.preventDefault();
                    this.focusInput(index - 1);
                    this.remove(index - 1);
                }
                return;
            default:
                return;
        }

        e.preventDefault();
        this.focusInput(target);
    }

    /**
     * Splits a text into the characters the inputs accept: no whitespace, only digits if `integerOnly` is set, only
     * letters and digits if the text is longer than the inputs.
     * @param text The text to split.
     * @returns The accepted characters.
     */
    private sanitize(text: string): string[] {
        const chars = Array.from(text.replace(/\s/g, ''));
        if (this.cfg.integerOnly) {
            return chars.filter((char) => char >= '0' && char <= '9');
        }
        // a code longer than the inputs (pasted or filled in, e.g. "123-456"): without separators
        return chars.length > this.inputs.length ? chars.filter((char) => /[\p{L}\p{N}]/u.test(char)) : chars;
    }

    /**
     * Writes characters into the inputs, starting at the given one, and moves the focus to the input after the last
     * character. A complete code (as many characters as inputs) starts at the first input, an input after the first
     * empty one starts at the empty one: the characters are always the first inputs, so the value matches the inputs.
     * @param start The index of the first input.
     * @param chars The characters, the ones beyond the last input are dropped.
     */
    private fill(start: number, chars: string[]): void {
        let index = chars.length >= this.inputs.length ? 0 : Math.min(start, this.getFilledCount());
        for (const char of chars) {
            if (index >= this.inputs.length) {
                break;
            }
            this.chars[index] = char;
            this.inputs[index]!.value = char;
            index++;
        }
        this.focusInput(Math.min(index, this.inputs.length - 1));
        this.update();
    }

    /**
     * Deletes the character of an input, the following characters move one input to the front.
     * @param index The index of the input.
     */
    private remove(index: number): void {
        this.chars.splice(index, 1);
        this.chars.push('');
        this.inputs.each((i, input) => {
            if (i >= index) {
                input.value = this.chars[i]!;
            }
        });
        this.update();
    }

    /**
     * The number of filled inputs, the characters always fill the first inputs. Not the length of the value: a
     * character outside the BMP (e.g. an emoji) has a length of 2.
     * @returns The number of filled inputs, the index of the first empty one.
     */
    private getFilledCount(): number {
        const empty = this.chars.indexOf('');
        return empty === -1 ? this.chars.length : empty;
    }

    /**
     * Focuses an input and selects its character, so typing replaces it.
     * @param index The index of the input, ignored if there is none.
     */
    private focusInput(index: number): void {
        const input = this.inputs[index];
        if (input) {
            input.focus();
            input.select();
        }
    }

    /**
     * Stores the value in the hidden input, calls the `onchange` callback if it changed and the `complete` behavior
     * if all characters are entered.
     */
    private update(): void {
        const oldValue = this.hiddenInput.val();
        const value = this.getValue();
        if (value === oldValue) {
            return;
        }
        this.hiddenInput.val(value);
        PrimeFaces.updateFilledState(this.hiddenInput, this.jq);

        this.cfg.onchange?.call(this, value);
        if (this.chars.every((char) => char !== '') && this.hasBehavior('complete')) {
            this.callBehavior('complete');
        }
    }

    /**
     * Enables or disables the component: a disabled component can't be changed and its value is not submitted.
     * @param disabled Whether the component is disabled.
     */
    private setDisabled(disabled: boolean): void {
        this.disabled = disabled;
        this.jq.toggleClass('p-state-disabled', disabled);
        this.inputs.prop('disabled', disabled).toggleClass('p-state-disabled', disabled);
        this.hiddenInput.prop('disabled', disabled);
    }

    /**
     * Retrieves the current value, the characters of all inputs.
     * @returns The current value.
     */
    getValue(): string {
        return this.chars.join('');
    }

    /**
     * Sets the value without calling the `onchange` callback or a behavior.
     * @param value The new value, characters the inputs do not accept and the ones beyond the last input are dropped.
     */
    setValue(value: string | number | null | undefined): void {
        const chars = this.sanitize(value == null ? '' : String(value));
        this.inputs.each((index, input) => {
            this.chars[index] = chars[index] ?? '';
            input.value = this.chars[index]!;
        });
        this.hiddenInput.val(this.getValue());
        this.valueOnFocus = this.getValue();
        PrimeFaces.updateFilledState(this.hiddenInput, this.jq);
    }

    /**
     * Clears all inputs, without calling the `onchange` callback or a behavior.
     */
    clear(): void {
        this.setValue('');
    }

    /**
     * Focuses the first empty input, the last one if all are filled.
     */
    focus(): void {
        const empty = this.chars.findIndex((char) => char === '');
        this.focusInput(empty === -1 ? this.inputs.length - 1 : empty);
    }

    /**
     * Whether the component is disabled.
     * @returns `true` if the component is disabled.
     */
    isDisabled(): boolean {
        return this.disabled;
    }

    /**
     * Disables the component, the value is not submitted.
     */
    override disable(): void {
        this.setDisabled(true);
    }

    /**
     * Enables the component.
     */
    override enable(): void {
        this.setDisabled(false);
    }
}
