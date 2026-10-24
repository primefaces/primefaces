/**
 * A test function that checks whether an entered character is allowed. The this context is the DOM element of the
 * input field.
 * @param character A newly entered character to check.
 * @returns `true` to allow the entered character, or `false` to block it.
 */
export type KeyFilterTestFunction = (this: HTMLInputElement | HTMLTextAreaElement, character: string) => boolean;

/** The name of a predefined mask, see {@link KeyFilter.masks}. */
export type KeyFilterMask = "pint" | "int" | "pnum" | "money" | "num" | "hex" | "email" | "alpha" | "alphanum";

/**
 * The configuration for the {@link KeyFilter} widget. The options `testFunction`, `regEx`, `inputRegEx` and `mask` are
 * mutually exclusive.
 */
export interface KeyFilterCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** The regular expression the complete text of the input must match. */
    inputRegEx: RegExp;
    /** The predefined mask each entered character must match. */
    mask: KeyFilterMask | string;
    /** Whether pasting into the input is prevented. */
    preventPaste: boolean;
    /** The regular expression each entered character must match. */
    regEx: RegExp;
    /** Search expression of the input, the parent of the component by default. */
    target: string;
    /** A function that checks each entered character. */
    testFunction: KeyFilterTestFunction;
}

/**
 * __PrimeFaces KeyFilter Widget__
 *
 * KeyFilter is used to filter keyboard input on specified input components.
 */
export class KeyFilter<Cfg extends KeyFilterCfg = KeyFilterCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * The predefined masks, each a regular expression an entered character must match. Can be extended with own masks,
     * e.g. `PrimeFaces.widget.KeyFilter.masks.upper = /[A-Z]/` (formerly `$.fn.keyfilter.defaults.masks`).
     */
    static masks: Record<KeyFilterMask | string, RegExp> = {
        pint: /[\d]/,
        'int': /[\d\-]/,
        pnum: /[\d\.]/,
        money: /[\d\.\s,]/,
        num: /[\d\-\.]/,
        hex: /[0-9a-f]/i,
        email: /[a-z0-9_\.\-@]/i,
        alpha: /[a-z_]/i,
        alphanum: /[a-z0-9_]/i
    };

    /** The DOM element of the target the key filter is applied to: the input itself or a container of it. */
    target: JQuery = $();

    /**
     * Initializes the key filter: applies it to the target input, or to the first enabled input inside the target.
     * @param cfg The configuration of the key filter.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.target);

        if (this.target.is(':input')) {
            this.applyKeyFilter(this.target);
        }
        else {
            this.applyKeyFilter($(':not(:submit):not(:button):not([readonly]):input:visible:enabled:first', this.target));
        }
    }

    /**
     * Applies the key filter to the given input or textarea element.
     * @param input The input or textarea.
     */
    private applyKeyFilter(input: JQuery): void {
        // the input is not replaced when only the keyFilter itself is updated, so remove the handlers of the
        // previous init - only the own ones, as other keyFilters could be applied to the same input
        const namespace = '.keyFilter' + this.id;
        input.off(namespace);
        this.addDestroyListener(() => {
            input.off(namespace);
        });

        if (this.cfg.inputRegEx) {
            const inputRegEx = this.cfg.inputRegEx;
            let previousInputValue = '';
            input.on('input' + namespace, (e) => {
                const element = e.currentTarget as HTMLInputElement;
                if (inputRegEx.test(element.value)) {
                    previousInputValue = element.value;
                }
                else {
                    element.value = previousInputValue;
                }
            });
        }
        else {
            const test = this.createCharacterTest();
            if (test) {
                this.bindCharacterFilter(input, test, namespace);
            }
        }

        // disable drop
        input.on('drop' + namespace, (e) => {
            e.preventDefault();
        });

        if (this.cfg.preventPaste) {
            input.on('paste' + namespace, (e) => {
                e.preventDefault();
            });
        }
    }

    /**
     * Creates the test for a single entered character from the `regEx`, `testFunction` or `mask` option.
     * @returns The test, or `null` if none of these options is set.
     */
    private createCharacterTest(): KeyFilterTestFunction | null {
        const regEx = this.cfg.regEx || (this.cfg.mask ? KeyFilter.masks[this.cfg.mask] : null);
        if (regEx) {
            return (character) => regEx.test(character);
        }
        return this.cfg.testFunction || null;
    }

    /**
     * Removes entered characters that do not pass the test. Typed text is checked in `beforeinput`, which also works with
     * the virtual keyboards of mobile devices (unlike the deprecated `keypress` event); of a text with several
     * characters (e.g. an autocompletion of the keyboard) only the allowed ones are inserted. Text composed with an input
     * method editor (IME) can't be changed there, so its disallowed characters are removed when the composition ends.
     * Shortcuts like Ctrl+C, pasted and deleted text are not filtered.
     * @param input The input or textarea element.
     * @param test The test for a single character.
     * @param namespace The event namespace of this widget.
     */
    private bindCharacterFilter(input: JQuery, test: KeyFilterTestFunction, namespace: string): void {
        const filter = (element: HTMLInputElement | HTMLTextAreaElement, text: string): string => {
            let result = '';
            for (const character of text) {
                if (test.call(element, character)) {
                    result += character;
                }
            }
            return result;
        };

        input.on('beforeinput' + namespace, (e) => {
            const element = e.currentTarget as HTMLInputElement;
            const event = e.originalEvent as InputEvent;
            if (event.inputType !== 'insertText' || !event.data) {
                return;
            }
            let filtered = filter(element, event.data);
            if (filtered === event.data) {
                return;
            }
            e.preventDefault();
            // insert the allowed characters, within the maxlength like the browser; inputs without a text selection
            // (e.g. type="email") get nothing
            const selection = PrimeFaces.utils.getTextSelection(element);
            if (filtered && selection) {
                if (element.maxLength >= 0) {
                    filtered = filtered.slice(0, Math.max(0, element.maxLength - (element.value.length - selection.length)));
                }
                if (filtered) {
                    PrimeFaces.utils.replaceTextSelection(element, filtered);
                    $(element).trigger('input');
                }
            }
        })
        .on('compositionend' + namespace, (e) => {
            const element = e.currentTarget as HTMLInputElement;
            const text = (e.originalEvent as CompositionEvent).data;
            if (!text) {
                return;
            }
            const filtered = filter(element, text);
            if (filtered === text) {
                return;
            }
            // after the browser inserted the composed text (some do that after compositionend): the composed text is
            // before the caret, it may have replaced more than the selection (e.g. a whole word on Android)
            setTimeout(() => {
                const selection = PrimeFaces.utils.getTextSelection(element);
                const start = selection ? selection.start - text.length : -1;
                if (!selection || start < 0 || element.value.slice(start, selection.start) !== text) {
                    return;
                }
                element.value = element.value.slice(0, start) + filtered + element.value.slice(selection.start);
                PrimeFaces.utils.setTextSelection(element, start + filtered.length);
                $(element).trigger('input');
            });
        });
    }
}
