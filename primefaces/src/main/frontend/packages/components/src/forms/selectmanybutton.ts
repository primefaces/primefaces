/**
 * The configuration for the {@link SelectManyButton} widget.
 */
export interface SelectManyButtonCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether a custom layout is used. */
    custom: boolean;
    /** Called when the value has changed. */
    change: PrimeFacesTypes.widget.SelectManyButton.ChangeCallback;
}

/**
 * __PrimeFaces SelectManyButton Widget__
 *
 * SelectManyButton is a multi select component using button UI.
 */
export class SelectManyButton<Cfg extends SelectManyButtonCfg = SelectManyButtonCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Whether the custom layout uses a facet. */
    facet?: boolean;
    /** The buttons. */
    buttons: JQuery = $();
    /** Whether all buttons were disabled or enabled via {@link disable} / {@link enable}, `undefined` before. */
    disabled?: boolean;
    /** The enabled checkbox inputs with the value. */
    inputs: JQuery<HTMLInputElement> = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.inputs = this.jq.find(':checkbox:not(:disabled)') as JQuery<HTMLInputElement>;

        // custom layout
        if (this.cfg.custom) {
            // For facet layout, buttons are within jq. For referenced layout, search in parent container
            const buttonsInJq = this.jq.find('[role="checkbox"]');
            if (buttonsInJq.length > 0) {
                this.facet = true;
                this.buttons = buttonsInJq;
            }
            else {
                this.facet = false;
                this.buttons = this.jq.parent().find('[role="checkbox"]');
            }

            // update button state
            for (let i = 0; i < this.inputs.length; i++) {
                const input = this.inputs.eq(i);

                if (input.is(':checked')) {
                    this.buttons.eq(i).addClass('p-state-active');
                }

                if (input.is(':disabled')) {
                    this.disable(i);
                }
            }
        }
        // regular layout
        else {
            this.buttons = this.jq.children('div:not(.p-state-disabled)');
        }

        this.bindEvents();

        // pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    private bindEvents(): void {
        if (this.cfg.custom) {
            this.bindCustomLayoutEvents();
        }
        else {
            this.bindRegularLayoutEvents();
        }

        this.bindInputEvents();
    }

    private bindCustomLayoutEvents(): void {
        this.buttons.filter(':not(.p-state-disabled)').off('mouseover.selectManyButton mouseout.selectManyButton click.selectManyButton')
        .on('mouseover.selectManyButton', (e) => {
            const button = $(e.currentTarget);
            if (!button.hasClass('p-state-active')) {
                button.addClass('p-state-hover');
            }
        })
        .on('mouseout.selectManyButton', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('click.selectManyButton', (e) => {
            this.handleButtonInteraction(e.currentTarget, e);
            e.stopPropagation();
            e.preventDefault();
        });

        // For keyboard accessibility on buttons
        this.buttons.off('focus.selectManyButton blur.selectManyButton keydown.selectManyButton')
        .on('focus.selectManyButton', (e) => {
            $(e.currentTarget).addClass('p-state-focus');
        })
        .on('blur.selectManyButton', (e) => {
            $(e.currentTarget).removeClass('p-state-focus');
        })
        .on('keydown.selectManyButton', (e) => {
            if (PrimeFaces.utils.isActionKey(e)) {
                this.handleButtonInteraction(e.currentTarget, e);
                e.preventDefault();
            }
        });
    }

    private bindRegularLayoutEvents(): void {
        this.buttons.on('mouseover.selectManyButton', (e) => {
            const button = $(e.currentTarget);
            if (!button.hasClass('p-state-active')) {
                button.addClass('p-state-hover');
            }
        })
        .on('mouseout.selectManyButton', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('click.selectManyButton', (e) => {
            const button = $(e.currentTarget);
            const input = button.children(':checkbox');

            if (button.hasClass('p-state-active')) {
                button.addClass('p-state-hover');
            }
            else {
                button.removeClass('p-state-hover');
            }

            input.trigger('focus').trigger('click');
        });
    }

    private bindInputEvents(): void {
        // keyboard support on inputs - shared for both layouts
        this.inputs.on('focus.selectManyButton', (e) => {
            const button = this.getButtonForInput($(e.currentTarget));
            if (button) {
                button.addClass('p-state-focus');
            }
        })
        .on('blur.selectManyButton', (e) => {
            const button = this.getButtonForInput($(e.currentTarget));
            if (button) {
                button.removeClass('p-state-focus');
            }
        })
        .on('change.selectManyButton', (e) => {
            const input = $(e.currentTarget);
            const button = this.getButtonForInput(input);
            if (button) {
                if (input.prop('checked')) {
                    button.addClass('p-state-active');
                }
                else {
                    button.removeClass('p-state-active');
                }
            }

            this.triggerChange();
        })
        .on('keydown.selectManyButton', (e) => {
            if (PrimeFaces.utils.isActionKey(e)) {
                const input = $(e.currentTarget);

                // Toggle the checkbox state
                const isChecked = input.prop('checked');
                input.prop('checked', !isChecked).trigger('change');
                e.preventDefault();
            }
        })
        .on('click.selectManyButton', (e) => {
            e.stopPropagation();
        });
    }

    /**
     * @param input A checkbox input.
     * @returns The button of the input, `null` if there is none.
     */
    private getButtonForInput(input: JQuery): JQuery | null {
        if (this.cfg.custom) {
            // In custom layout, find button by index
            const index = this.inputs.index(input);
            if (index >= 0 && index < this.buttons.length) {
                return this.buttons.eq(index);
            }
            return null;
        }
        else {
            // In regular layout, button is parent
            return input.parent();
        }
    }

    /**
     * Selects or unselects the button after a click or an action key.
     * @param target The button.
     * @param _e The event, unused.
     */
    private handleButtonInteraction(target: HTMLElement, _e: JQuery.TriggeredEvent): void {
        const button = $(target);
        const isActive = button.hasClass('p-state-active');

        if (isActive) {
            this.unselect(button);
        }
        else {
            this.select(button);
        }
    }

    /**
     * Selects the given button.
     * @param button A button of this widget.
     */
    select(button: JQuery): void {
        if (this.cfg.custom) {
            const index = this.buttons.index(button);
            if (index >= 0) {
                this.inputs.eq(index).prop('checked', true);
                button.addClass('p-state-active');
            }
        }
        else {
            button.children(':checkbox').prop('checked', true);
        }

        this.triggerChange();
    }

    /**
     * Unselects the given button.
     * @param button A button of this widget.
     */
    unselect(button: JQuery): void {
        if (this.cfg.custom) {
            const index = this.buttons.index(button);
            if (index >= 0) {
                this.inputs.eq(index).prop('checked', false);
                button.removeClass('p-state-active p-state-hover');
            }
        }
        else {
            button.children(':checkbox').prop('checked', false);
        }

        this.triggerChange();
    }

    /**
     * Calls the `change` callback and behavior.
     */
    private triggerChange(): void {
        if (this.cfg.change) {
            this.cfg.change.call(this);
        }

        this.callBehavior('change');
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        if (this.cfg.custom) {
            for (let i = 0; i < this.buttons.length; i++) {
                const button = this.buttons.eq(i);
                this.enable(i);
                button.removeClass('p-state-active');
                if (i < this.inputs.length) {
                    this.inputs.eq(i).prop('checked', false);
                }
            }
        }

        this.init(cfg);
    }

    /**
     * Enables the buttons so that the user can select options.
     * @param index Index of the button to enable, all buttons if omitted.
     */
    override enable(index?: number | null): void {
        if (index === null || index === undefined) {
            if (this.cfg.custom) {
                this.buttons.removeClass('p-state-disabled').removeAttr('disabled');
                this.inputs.removeAttr('disabled');
            }
            else {
                PrimeFaces.utils.enableInputWidget(this.jq, this.inputs);
            }
            this.disabled = false;
        }
        else {
            const button = this.buttons.eq(index);
            button.removeClass('p-state-disabled').removeAttr('disabled');
            const input = this.inputs.eq(index);
            input.removeAttr('disabled');
        }
    }

    /**
     * Disables the buttons so that the user cannot select options anymore.
     * @param index Index of the button to disable, all buttons if omitted.
     */
    override disable(index?: number | null): void {
        if (index === null || index === undefined) {
            if (this.cfg.custom) {
                this.buttons.removeClass('p-state-hover p-state-focus p-state-active')
                        .addClass('p-state-disabled').attr('disabled', 'disabled');
                this.inputs.attr('disabled', 'disabled');
            }
            else {
                PrimeFaces.utils.disableInputWidget(this.jq, this.inputs);
            }
            this.disabled = true;
        }
        else {
            const button = this.buttons.eq(index);
            button.removeClass('p-state-hover p-state-focus p-state-active')
                    .addClass('p-state-disabled').attr('disabled', 'disabled');
            const input = this.inputs.eq(index);
            input.attr('disabled', 'disabled');
        }
    }
}
