/**
 * The configuration for the {@link Chips} widget.
 */
export interface ChipsCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether the input is added as item when it loses the focus. */
    addOnBlur: boolean;
    /** Whether pasted text is added as item(s). */
    addOnPaste: boolean;
    /** Whether duplicate items are prevented. */
    unique: boolean;
    /** Maximum number of items. */
    max: number;
    /** Separator of several values in the input, only its first character is used, defaults to `,`. */
    separator: string;
}

/**
 * __PrimeFaces Chips Widget__
 *
 * Chips is used to enter multiple values on an inputfield.
 */
export class Chips<Cfg extends ChipsCfg = ChipsCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The visible input. */
    input: JQuery<HTMLInputElement> = $();
    /** The hidden select with the values. */
    hinput: JQuery<HTMLSelectElement> = $();
    /** The container of the items. */
    itemContainer: JQuery = $();
    /** The container of the visible input. */
    inputContainer: JQuery = $();
    /** The placeholder of the input. */
    placeholder?: string;
    /** Whether this component is wrapped in a float label. */
    hasFloatLabel: boolean = false;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.input = $(this.jqId + '_input') as JQuery<HTMLInputElement>;
        this.hinput = $(this.jqId + '_hinput') as JQuery<HTMLSelectElement>;
        this.itemContainer = this.jq.children('ul');
        this.inputContainer = this.itemContainer.children('.p-chips-input-token');
        this.hasFloatLabel = PrimeFaces.utils.hasFloatLabel(this.jq);

        // Ensure separator is only 1 character
        this.cfg.separator = this.cfg.separator !== undefined ? this.cfg.separator : ',';
        if (typeof this.cfg.separator === 'string' && this.cfg.separator.length > 1) {
            this.cfg.separator = this.cfg.separator.charAt(0);
        }

        // pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.placeholder = this.input.attr('placeholder');

        this.bindEvents();
    }

    private bindEvents(): void {
        this.itemContainer.on('mouseenter.chips', (e) => {
            $(e.currentTarget).addClass('p-state-hover');
        }).on('mouseleave.chips', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        }).on('click.chips', () => {
            this.input.trigger('focus');
        });

        this.input.on('focus.chips', () => {
            this.itemContainer.addClass('p-state-focus');
            if (this.hasFloatLabel) {
                this.jq.addClass('p-inputwrapper-focus');
            }
        }).on('blur.chips', (e) => {
            this.itemContainer.removeClass('p-state-focus');
            if (this.cfg.addOnBlur) {
                this.addItem($(e.currentTarget).val() as string, false);
            }
            if (this.hasFloatLabel) {
                this.jq.removeClass('p-inputwrapper-focus');
                this.updateFloatLabel();
            }
        }).on('paste.chips', (e) => {
            if (this.cfg.addOnPaste) {
                const pasteData = (e.originalEvent as ClipboardEvent).clipboardData?.getData('text') ?? '';
                this.addItem(pasteData, false);
                e.preventDefault();
                e.stopPropagation();
            }
        }).on('keydown.chips', (e) => {
            const value = $(e.currentTarget).val() as string;

            switch (e.key) {
                case 'Backspace':
                    if (value.length === 0 && this.hinput.children('option').length > 0) {
                        const index = this.hinput.children('option').last().index();
                        this.removeItem($(this.itemContainer.children('li.p-chips-token').get(index) as HTMLElement));
                    }
                    break;

                case 'Enter':
                case 'NumpadEnter':
                    this.addItem(value, true);
                    e.preventDefault();
                    e.stopPropagation();
                    break;

                default:
                    this.updateFloatLabel();
                    if (this.cfg.max && this.cfg.max === this.hinput.children('option').length) {
                        e.preventDefault();
                        break;
                    }

                    // Check if the pressed key matches the separator character
                    if (e.key === this.cfg.separator) {
                        this.addItem(value, true);
                        e.preventDefault();
                        e.stopPropagation();
                        break;
                    }
                    break;
            }
        });

        const closeSelector = '> li.p-chips-token > .p-chips-token-icon';
        this.itemContainer.off('click.chips', closeSelector).on('click.chips', closeSelector, null, (e) => {
            this.removeItem($(e.currentTarget).parent());
        });
    }

    private updateFloatLabel(): void {
        PrimeFaces.utils.updateFloatLabel(this.jq, this.input.add(this.hinput), this.hasFloatLabel);
    }

    /**
     * Adds an item for each separated value of the given text.
     * @param value The text with the value(s) of the items.
     * @param refocus `true` to focus the input after the item was added.
     */
    addItem(value: string, refocus?: boolean): void {
        if (!value || !value.trim().length) {
            return;
        }

        const tokens = value.split(this.cfg.separator ?? ',');
        for (const token of tokens) {
            if (token && token.trim().length && (!this.cfg.max || this.cfg.max > this.hinput.children('option').length)) {
                const escapedValue = PrimeFaces.escapeHTML(token);

                if (this.cfg.unique) {
                    const duplicateFound = this.hinput.children('option').toArray()
                            .some((option) => (option as HTMLOptionElement).value === token);
                    if (duplicateFound) {
                        this.refocus(refocus);
                        continue;
                    }
                }

                let itemDisplayMarkup = '<li class="p-chips-token p-state-active">';
                itemDisplayMarkup += '<span class="p-chips-token-icon p-icon p-clear-icon p-chips-chip-remove-icon"></span>';
                itemDisplayMarkup += '<span class="p-chips-token-label">' + escapedValue + '</span></li>';

                this.inputContainer.before(itemDisplayMarkup);
                this.refocus(refocus);

                this.hinput.append('<option value="' + escapedValue + '" selected="selected"></option>');
                // send an event per token
                if (refocus) {
                    this.invokeItemSelectBehavior(escapedValue);
                }
            }
        }

        // send only 1 event for all tokens pasted
        if (!refocus) {
            this.invokeItemSelectBehavior(PrimeFaces.escapeHTML(value));
        }

        this.updateFloatLabel();
    }

    /**
     * Clears the input and focuses it if requested.
     * @param refocus `true` to focus the input.
     */
    private refocus(refocus?: boolean): void {
        this.input.val('');
        this.input.removeAttr('placeholder');

        if (refocus) {
            this.input.trigger('focus');
        }
    }

    /**
     * Removes an item.
     * @param item The item (LI element) to remove.
     * @param silent `true` to remove it without animation and without the `itemUnselect` behavior.
     */
    removeItem(item: JQuery, silent?: boolean): void {
        const itemIndex = this.itemContainer.children('li.p-chips-token').index(item);
        const itemValue = item.find('span.p-chips-token-label').html();

        // remove from options
        this.hinput.children('option').eq(itemIndex).remove();

        if (silent) {
            item.remove();
        }
        else {
            item.fadeOut('fast', () => {
                item.remove();
                this.invokeItemUnselectBehavior(itemValue);
            });
        }

        // if empty return placeholder
        if (this.placeholder && this.hinput.children('option').length === 0) {
            this.input.attr('placeholder', this.placeholder);
        }
        this.updateFloatLabel();
    }

    /**
     * Turns the items into a separated text in the input for mass editing, or the text of the input back into items.
     */
    toggleEditor(): void {
        const tokens = this.itemContainer.children('li.p-chips-token');

        if (tokens.length) {
            let editor = '';
            tokens.each((_i, el) => {
                const token = $(el);
                // text, not html: addItem escapes the value again
                const tokenValue = token.find('span.p-chips-token-label').text();
                editor = editor + tokenValue + (this.cfg.separator !== undefined ? this.cfg.separator : '');
                this.removeItem(token, true);
            });

            if (editor) {
                editor = editor.slice(0, -1);
                this.input.val(editor);
            }
        }
        else {
            this.addItem(this.input.val() as string, true);
        }
    }

    private invokeItemSelectBehavior(itemValue: string): void {
        if (this.hasBehavior('itemSelect')) {
            const ext = {
                params: [
                    { name: this.getId() + '_itemSelect', value: itemValue }
                ]
            };

            this.callBehavior('itemSelect', ext);
        }
    }

    private invokeItemUnselectBehavior(itemValue: string): void {
        if (this.hasBehavior('itemUnselect')) {
            const ext = {
                params: [
                    { name: this.getId() + '_itemUnselect', value: itemValue }
                ]
            };

            this.callBehavior('itemUnselect', ext);
        }
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
