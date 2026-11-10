/**
 * The configuration for the {@link SelectOneRadio} widget.
 */
export interface SelectOneRadioCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether a custom layout is used. */
    custom: boolean;
    /** Whether a click on the selected radio clears the selection. */
    unselectable: boolean;
    /** Whether the radio group is readonly, normalized to a boolean on init. */
    readonly: boolean;
}

/**
 * __PrimeFaces SelectOneRadio Widget__
 *
 * SelectOneRadio is an extended version of the standard SelectOneRadio with theme integration.
 */
export class SelectOneRadio<Cfg extends SelectOneRadioCfg = SelectOneRadioCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Whether the custom layout uses a facet. */
    facet?: boolean;
    /** The original radio inputs with the value, only with a custom layout. */
    originalInputs?: JQuery<HTMLInputElement>;
    /** The enabled radio inputs. */
    enabledInputs: JQuery<HTMLInputElement> = $();
    /** The radio inputs (the clones with a custom layout). */
    inputs: JQuery<HTMLInputElement> = $();
    /** The visible radio buttons. */
    outputs: JQuery = $();
    /** The selected visible radio button. */
    checkedRadio: JQuery = $();
    /** The labels of the radio buttons. */
    labels: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.readonly = this.cfg.readonly === true;

        // custom layout
        if (this.cfg.custom) {
            this.facet = this.jq.attr('role') === 'radiogroup';
            const originalInputs = this.jq.find((this.facet ? '.p-hidden ' : '') + ':radio') as JQuery<HTMLInputElement>;
            this.originalInputs = originalInputs;
            this.inputs = $('input:radio[name="' + this.id + '_clone"].p-radio-clone') as JQuery<HTMLInputElement>;
            this.outputs = this.inputs.parent().next('.p-radiobutton-box');
            this.labels = $();

            // labels
            const base = this.facet ? this.inputs : this.outputs;
            for (let j = 0; j < base.length; j++) {
                this.labels = this.labels.add('label[for="'
                    + (this.facet ? base.eq(j).attr('id') : base.eq(j).prev().find('.p-radio-clone').attr('id')) + '"]');
            }

            // update radio state
            for (let i = 0; i < this.inputs.length; i++) {
                const input = this.inputs.eq(i);
                const itemindex = input.data('itemindex');
                const original = originalInputs.eq(itemindex);

                input.val(original.val() as string);

                if (original.is(':checked')) {
                    input.prop('checked', true).parent().next().addClass('p-state-active');
                }
                this.setAriaChecked(input, original.is(':checked'));

                if (original.is(':disabled')) {
                    this.disable(i);
                }
            }

            // pfs metadata
            originalInputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        }
        // regular layout
        else {
            this.outputs = this.jq.find('.p-radiobutton-box');
            this.inputs = this.jq.find(':radio') as JQuery<HTMLInputElement>;
            this.labels = this.jq.find('label');

            this.inputs.each((_i, el) => {
                this.setAriaChecked($(el), el.checked);
            });

            // pfs metadata
            this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');
        this.checkedRadio = this.outputs.filter('.p-state-active');

        this.bindEvents();
    }

    /**
     * Sets `aria-checked` of the radio role element of the given input or radio button.
     * @param input The input or the visible radio button.
     * @param checked Whether it is checked.
     */
    private setAriaChecked(input: JQuery, checked: boolean): void {
        input.closest('[role=radio]').attr('aria-checked', String(checked));
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        if (this.cfg.custom) {
            for (let i = 0; i < this.inputs.length; i++) {
                const input = this.inputs.eq(i);

                this.enable(i);
                input.prop('checked', false).parent().next().removeClass('p-state-active');
                this.setAriaChecked(input, false);
            }
        }

        this.init(cfg);
    }

    private bindEvents(): void {
        if (!this.cfg.readonly) {
            this.outputs.filter(':not(.p-state-disabled)').off('mouseenter.selectOneRadio mouseleave.selectOneRadio click.selectOneRadio')
            .on('mouseenter.selectOneRadio', (e) => {
                $(e.currentTarget).addClass('p-state-hover');
            })
            .on('mouseleave.selectOneRadio', (e) => {
                $(e.currentTarget).removeClass('p-state-hover');
            })
            .on('click.selectOneRadio', (e) => {
                const radio = $(e.currentTarget);
                const input = radio.prev().children(':radio') as JQuery<HTMLInputElement>;

                const isSelectableRadio = this.cfg.unselectable || !this.checkedRadio || !this.checkedRadio.is(radio);

                // configure ARIA
                this.jq.find('[role=radio]').attr('aria-checked', 'false');
                this.setAriaChecked(radio, true);

                // configure UI
                if (!radio.hasClass('p-state-active') && isSelectableRadio) {
                    this.unselect(this.checkedRadio);
                    this.select(radio);
                }
                else if (this.cfg.unselectable) {
                    this.unselect(this.checkedRadio);
                }

                // fire events only if the radio is different
                if (isSelectableRadio) {
                    this.fireClickEvent(input, e);
                    input.trigger('change');
                    input.trigger('focus.selectOneRadio');
                }

                // Github issue #4467
                e.stopPropagation();
                e.preventDefault();
            });

            this.labels.filter(':not(.p-state-disabled)').off('click.selectOneRadio')
            .on('click.selectOneRadio', (e) => {
                const target = $(PrimeFaces.escapeClientId($(e.currentTarget).attr('for') ?? ''));
                let radio: JQuery;

                // checks if target is input or not(custom labels)
                if (target.is(':input')) {
                    radio = target.parent().next();
                }
                else {
                    radio = target.children('.p-radiobutton-box'); // custom layout
                }

                // #13546 only trigger radio if its not already active
                if (!radio.hasClass('p-state-active')) {
                    radio.trigger('click.selectOneRadio');
                }

                e.preventDefault();
            });
        }

        this.enabledInputs.off('focus.selectOneRadio blur.selectOneRadio keydown.selectOneRadio')
        .on('focus.selectOneRadio', (e) => {
            $(e.currentTarget).parent().next().addClass('p-state-focus');
        })
        .on('blur.selectOneRadio', (e) => {
            $(e.currentTarget).parent().next().removeClass('p-state-focus');
        })
        .on('keydown.selectOneRadio', (e) => {
            if (this.cfg.readonly) {
                return;
            }

            const input = $(e.currentTarget);
            const currentRadio = input.parent().next();
            let index = this.enabledInputs.index(e.currentTarget);
            const size = this.enabledInputs.length;

            switch (e.key) {
                case 'ArrowUp':
                case 'ArrowLeft': {
                    const prevRadioInput = (index === 0) ? this.enabledInputs.eq((size - 1)) : this.enabledInputs.eq(--index);
                    const prevRadio = prevRadioInput.parent().next();

                    input.trigger('blur');
                    this.unselect(currentRadio);
                    this.select(prevRadio);
                    prevRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                    break;
                }

                case 'ArrowDown':
                case 'ArrowRight': {
                    const nextRadioInput = (index === (size - 1)) ? this.enabledInputs.eq(0) : this.enabledInputs.eq(++index);
                    const nextRadio = nextRadioInput.parent().next();

                    input.trigger('blur');
                    this.unselect(currentRadio);
                    this.select(nextRadio);
                    nextRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                    break;
                }

                case ' ':
                    if (!input.prop('checked')) {
                        this.select(currentRadio);
                        input.trigger('focus').trigger('change');
                    }
                    else if (this.cfg.unselectable) {
                        this.unselect(currentRadio);
                        input.trigger('focus').trigger('change');
                    }

                    e.preventDefault();
                    break;
            }
        });
    }

    /**
     * Unselects the given radio button.
     * @param radio A visible radio button of this widget.
     */
    unselect(radio: JQuery): void {
        if (this.cfg.readonly) {
            return;
        }

        const radioInput = radio.prev().children(':radio');
        radioInput.prop('checked', false);
        radio.removeClass('p-state-active');

        if (this.cfg.custom) {
            const itemindex = radioInput.data('itemindex');
            this.originalInputs?.eq(itemindex).prop('checked', false);
        }
    }

    /**
     * Selects the given radio button, the selected one is not unselected.
     * @param radio A visible radio button of this widget.
     */
    select(radio: JQuery): void {
        if (this.cfg.readonly) {
            return;
        }

        const radioInput = radio.prev().children(':radio');
        this.checkedRadio = radio;
        radio.addClass('p-state-active');
        radioInput.prop('checked', true);

        if (this.cfg.custom) {
            const itemindex = radioInput.data('itemindex');
            this.originalInputs?.eq(itemindex).prop('checked', true);
        }
    }

    /**
     * Removes all event listeners of the given radio input, its radio button and label, or of all of them.
     * @param input The radio input, all inputs if omitted.
     */
    private unbindEvents(input?: JQuery): void {
        if (input) {
            input.off();
            input.parent().nextAll('.p-radiobutton-box').off();
            this.labels.filter("label[for='" + input.attr('id') + "']").off();
        }
        else {
            this.inputs.off();
            this.labels.off();
            this.outputs.off();
        }
    }

    /**
     * Disables a radio button, or all of them.
     * @param index Index of the radio button to disable, all radio buttons if omitted.
     */
    override disable(index?: number | null): void {
        if (this.cfg.readonly) {
            return;
        }

        if (index === null || index === undefined) {
            this.inputs.attr('disabled', 'disabled');
            this.labels.addClass('p-state-disabled');
            this.outputs.addClass('p-state-disabled');
            this.unbindEvents();
        }
        else {
            const input = this.inputs.eq(index);
            const label = this.labels.filter("label[for='" + input.attr('id') + "']");
            input.attr('disabled', 'disabled').parent().nextAll('.p-radiobutton-box').addClass('p-state-disabled');
            label.addClass('p-state-disabled');
            this.unbindEvents(input);
        }
    }

    /**
     * Enables a radio button, or all of them.
     * @param index Index of the radio button to enable, all radio buttons if omitted.
     */
    override enable(index?: number | null): void {
        if (this.cfg.readonly) {
            return;
        }

        if (index === null || index === undefined) {
            this.inputs.removeAttr('disabled');
            this.labels.removeClass('p-state-disabled');
            this.outputs.removeClass('p-state-disabled');
        }
        else {
            const input = this.inputs.eq(index);
            const label = this.labels.filter("label[for='" + input.attr('id') + "']");
            input.removeAttr('disabled').parent().nextAll('.p-radiobutton-box').removeClass('p-state-disabled');
            label.removeClass('p-state-disabled');
        }
        this.bindEvents();
    }

    /**
     * Calls the `onclick` of the radio input, with this widget as `this`.
     * @param input The radio input that was clicked.
     * @param event The click event.
     */
    private fireClickEvent(input: JQuery<HTMLInputElement>, event: JQuery.TriggeredEvent): void {
        const userOnClick = input.prop('onclick') as ((this: SelectOneRadio<Cfg>, event: JQuery.TriggeredEvent) => void) | null;
        if (userOnClick) {
            userOnClick.call(this, event);
        }
    }
}
