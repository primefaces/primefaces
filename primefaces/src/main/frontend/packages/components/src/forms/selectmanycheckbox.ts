/**
 * The configuration for the {@link SelectManyCheckbox} widget.
 */
export interface SelectManyCheckboxCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether a custom layout is used. */
    custom: boolean;
}

/**
 * __PrimeFaces SelectManyCheckbox Widget__
 *
 * SelectManyCheckbox is an extended version of the standard SelectManyCheckbox.
 */
export class SelectManyCheckbox<Cfg extends SelectManyCheckboxCfg = SelectManyCheckboxCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Whether this widget was disabled via {@link disable}, `undefined` before. */
    disabled?: boolean;
    /** The enabled inputs. */
    enabledInputs: JQuery<HTMLInputElement> = $();
    /** The checkbox inputs (the clones with a custom layout). */
    inputs: JQuery<HTMLInputElement> = $();
    /** The original checkbox inputs with the value, only with a custom layout. */
    originalInputs?: JQuery<HTMLInputElement>;
    /** The visible checkboxes. */
    outputs: JQuery = $();
    /** The labels of the checkboxes. */
    labels: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        if (this.cfg.custom) {
            const originalInputs = this.jq.find(':checkbox') as JQuery<HTMLInputElement>;
            this.originalInputs = originalInputs;
            this.inputs = $('input:checkbox[name="' + this.id + '"].p-chkbox-clone') as JQuery<HTMLInputElement>;
            this.outputs = this.inputs.parent().next('.p-chkbox-box');
            this.labels = $();

            // labels
            for (let j = 0; j < this.outputs.length; j++) {
                this.labels = this.labels.add('label[for="' + this.outputs.eq(j).parent().find('input').attr('id') + '"]');
            }

            // update checkbox state
            for (let i = 0; i < this.inputs.length; i++) {
                const input = this.inputs.eq(i);
                const itemindex = input.data('itemindex');
                const originalInput = originalInputs.eq(itemindex);

                input.val(originalInput.val() as string);

                if (originalInput.is(':checked')) {
                    const checkbox = input.prop('checked', true).parent().next();
                    this.check(input, checkbox);
                }
            }
        }
        else {
            this.outputs = this.jq.find('.p-chkbox-box:not(.p-state-disabled)');
            this.inputs = this.jq.find(':checkbox:not(:disabled)') as JQuery<HTMLInputElement>;
            this.labels = this.jq.find('label');
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');

        this.bindEvents();

        // pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    private bindEvents(): void {
        const namespace = '.selectManyCheckbox';

        this.outputs.filter(':not(.p-state-disabled)').off(namespace).on('mouseenter' + namespace, (e) => {
            $(e.currentTarget).addClass('p-state-hover');
        })
        .on('mouseleave' + namespace, (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('click' + namespace, (e) => {
            const checkbox = $(e.currentTarget);
            const input = checkbox.prev().children(':checkbox') as JQuery<HTMLInputElement>;

            this.toggle(input, checkbox, e);
        });

        this.labels.filter(':not(.p-state-disabled)').off(namespace).on('click' + namespace, (e) => {
            const target = $(PrimeFaces.escapeClientId($(e.currentTarget).attr('for') ?? ''));
            let checkbox: JQuery;

            // checks if target is input or not(custom labels)
            if (target.is(':input')) {
                checkbox = target.parent().next();
            }
            else {
                checkbox = target.children('.p-chkbox-box'); // custom layout
            }

            checkbox.trigger('click');

            e.preventDefault();
        });

        // delegate focus-blur states
        this.enabledInputs.off(namespace).on('focus' + namespace, (e) => {
            $(e.currentTarget).parent().next().addClass('p-state-focus');
        })
        .on('blur' + namespace, (e) => {
            $(e.currentTarget).parent().next().removeClass('p-state-focus');
        })
        .on('keydown' + namespace, (e) => {
            if (PrimeFaces.utils.isActionKey(e)) {
                const input = $(e.currentTarget);
                const checkbox = input.parent().next();

                this.toggle(input, checkbox, e);
                e.preventDefault();
                e.stopPropagation();
            }
        });
    }

    /**
     * Toggles the given checkbox and calls the click and change listeners.
     * @param input The checkbox input.
     * @param checkbox The visible checkbox.
     * @param event The event that toggles the checkbox.
     */
    private toggle(input: JQuery<HTMLInputElement>, checkbox: JQuery, event: JQuery.TriggeredEvent): void {
        if (input.is(':disabled')) {
            return;
        }

        if (!checkbox.hasClass('p-state-active')) {
            this.check(input, checkbox);
        }
        else {
            this.uncheck(input, checkbox);
        }

        this.fireClickEvent(input, event);
        input.trigger('change');
        input.trigger('focus');
    }

    /**
     * Checks the given checkbox.
     * @param input The checkbox input.
     * @param checkbox The visible checkbox.
     */
    private check(input: JQuery<HTMLInputElement>, checkbox: JQuery): void {
        checkbox.addClass('p-state-active');
        checkbox.children('.p-chkbox-icon').addClass('p-checkbox-checked-icon');
        input.attr('aria-checked', 'true');
        input.prop('checked', true);

        if (this.cfg.custom) {
            const itemindex = input.data('itemindex');
            this.originalInputs?.eq(itemindex).prop('checked', true);
        }
    }

    /**
     * Unchecks the given checkbox.
     * @param input The checkbox input.
     * @param checkbox The visible checkbox.
     */
    private uncheck(input: JQuery<HTMLInputElement>, checkbox: JQuery): void {
        checkbox.removeClass('p-state-active');
        checkbox.children('.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
        input.attr('aria-checked', 'false');
        input.prop('checked', false);

        if (this.cfg.custom) {
            const itemindex = input.data('itemindex');
            this.originalInputs?.eq(itemindex).prop('checked', false);
        }
    }

    /**
     * Enables the checkboxes so that the user can check them.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.inputs);
        this.disabled = false;
    }

    /**
     * Disables the checkboxes so that the user cannot check them anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.inputs);
        this.disabled = true;
    }

    /**
     * Checks all enabled checkboxes.
     * @param silent `true` to suppress the `change` events.
     */
    checkAll(silent?: boolean): void {
        this.outputs.filter(':not(.p-state-disabled)').each((_i, el) => {
            const checkbox = $(el);
            const input = checkbox.prev().children(':checkbox') as JQuery<HTMLInputElement>;

            if (!input.is(':checked')) {
                this.check(input, checkbox);
                if (!silent) {
                    input.trigger('change');
                }
            }
        });
    }

    /**
     * Unchecks all enabled checkboxes.
     * @param silent `true` to suppress the `change` events.
     */
    uncheckAll(silent?: boolean): void {
        this.outputs.filter(':not(.p-state-disabled)').each((_i, el) => {
            const checkbox = $(el);
            const input = checkbox.prev().children(':checkbox') as JQuery<HTMLInputElement>;

            if (input.is(':checked')) {
                this.uncheck(input, checkbox);
                if (!silent) {
                    input.trigger('change');
                }
            }
        });
    }

    /**
     * Resets the value by unchecking all checkboxes.
     * @param silent `true` to suppress the `change` events.
     */
    resetValue(silent?: boolean): void {
        this.uncheckAll(silent);
    }

    /**
     * Calls the `onclick` of the checkbox input, with this widget as `this`.
     * @param input The checkbox input that was clicked.
     * @param event The click event.
     */
    private fireClickEvent(input: JQuery<HTMLInputElement>, event: JQuery.TriggeredEvent): void {
        const userOnClick = input.prop('onclick') as ((this: SelectManyCheckbox<Cfg>, event: JQuery.TriggeredEvent) => void) | null;
        if (userOnClick) {
            userOnClick.call(this, event);
        }
    }
}
