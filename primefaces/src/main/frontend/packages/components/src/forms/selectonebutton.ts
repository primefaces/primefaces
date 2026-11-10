/**
 * The configuration for the {@link SelectOneButton} widget.
 */
export interface SelectOneButtonCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Requests a confirmation before the value changes, called with the component element as `this`. */
    confirmation: (this: HTMLElement) => void;
    /** Whether a custom layout is used. */
    custom: boolean;
    /** Whether the selection can be cleared, normalized to a boolean on init (default `true`). */
    unselectable: boolean;
    /** Called when the value has changed. */
    change: PrimeFacesTypes.widget.SelectOneButton.ChangeCallback;
}

/**
 * __PrimeFaces SelectOneButton Widget__
 *
 * SelectOneButton is an input component to do a single select.
 */
export class SelectOneButton<Cfg extends SelectOneButtonCfg = SelectOneButtonCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Whether the custom layout uses a facet. */
    facet: boolean = false;
    /** The enabled radio inputs with the value. */
    inputs: JQuery<HTMLInputElement> = $();
    /** The enabled buttons. */
    buttons: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.unselectable = this.cfg.unselectable !== false;
        // the default layout root is also a radiogroup, so the role alone doesn't identify the facet span
        this.facet = !!this.cfg.custom && this.jq.attr('role') === 'radiogroup';
        this.inputs = this.jq.find((this.facet ? '.p-hidden ' : '') + ':radio:not(:disabled)') as JQuery<HTMLInputElement>;

        // custom layout
        if (this.cfg.custom) {
            // For facet layout, buttons are within jq. For referenced layout, search in parent container
            if (this.facet) {
                this.buttons = this.jq.find('[role="radio"]:not(.p-state-disabled)');
            }
            else {
                this.buttons = this.jq.parent().find('[role="radio"]:not(.p-state-disabled)');
            }

            // update button state
            for (let i = 0; i < this.inputs.length; i++) {
                const original = this.inputs.eq(i);

                if (original.is(':checked')) {
                    this.buttons.eq(i).addClass('p-state-active').attr('aria-checked', 'true');
                }

                if (original.is(':disabled')) {
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
        this.buttons.filter(':not(.p-state-disabled)').off('mouseover.selectOneButton mouseout.selectOneButton click.selectOneButton')
        .on('mouseover.selectOneButton', (e) => {
            $(e.currentTarget).addClass('p-state-hover');
        })
        .on('mouseout.selectOneButton', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('click.selectOneButton', (e) => {
            // clicks on the native radio itself (e.g. fired by keyboard activation) are handled by the change listener
            if ($(e.target).is(':radio')) {
                return;
            }
            this.handleButtonInteraction(e.currentTarget, e);
            e.stopPropagation();
            e.preventDefault();
        });

        // keyboard interaction and focus are handled by the native radio inputs
        this.inputs.off('focus.selectOneButton blur.selectOneButton change.selectOneButton')
        .on('focus.selectOneButton', (e) => {
            $(e.currentTarget).closest('.p-button').addClass('p-state-focus');
        })
        .on('blur.selectOneButton', (e) => {
            $(e.currentTarget).closest('.p-button').removeClass('p-state-focus');
        })
        .on('change.selectOneButton', (e) => {
            if (e.currentTarget.checked) {
                const index = this.inputs.index(e.currentTarget);
                const button = this.buttons.eq(index);
                if (this.cfg.confirmation) {
                    this.inputs.each((i, input) => {
                        input.checked = this.buttons.eq(i).hasClass('p-state-active');
                    });
                }
                this.confirmChange(() => this.select(button));
            }
        });
    }

    /**
     * Selects or unselects the button after a click.
     * @param target The button that was clicked.
     * @param _e The click event, unused.
     */
    private handleButtonInteraction(target: HTMLElement, _e: JQuery.TriggeredEvent): void {
        const button = $(target);
        const isActive = button.hasClass('p-state-active');

        button.find(':radio').trigger('focus');

        if (isActive && !this.cfg.unselectable) {
            return;
        }

        this.confirmChange(() => {
            if (isActive) {
                this.unselect(button);
            }
            else {
                this.select(button);
            }
        });
    }

    /**
     * Runs a change of the selection, after a confirmation if one is configured.
     * @param action The change of the selection.
     */
    private confirmChange(action: () => void): void {
        if (this.cfg.confirmation) {
            this.jq.data('pfconfirmcommand', action);
            this.cfg.confirmation.call(this.jq[0] as HTMLElement);
        }
        else {
            action();
        }
    }

    /**
     * Selects the given button, a selected button is unselected.
     * @param button A button of this widget.
     */
    select(button: JQuery): void {
        const active = this.buttons.filter('.p-state-active');
        active.removeClass('p-state-active p-state-hover');

        if (this.cfg.custom) {
            active.attr('aria-checked', 'false');
            const index = this.buttons.index(button);
            if (index >= 0) {
                this.inputs.prop('checked', false);
                this.inputs.eq(index).prop('checked', true);
            }
            button.attr('aria-checked', 'true');
        }
        else {
            active.find(':radio').prop('checked', false);
            button.find(':radio').prop('checked', true);
        }

        button.addClass('p-state-active');

        this.triggerChange();
    }

    /**
     * Unselects the given button, if the selection can be cleared.
     * @param button A button of this widget.
     */
    unselect(button: JQuery): void {
        if (this.cfg.unselectable) {
            button.removeClass('p-state-active p-state-hover');

            if (this.cfg.custom) {
                button.attr('aria-checked', 'false');
                const index = this.buttons.index(button);
                if (index >= 0) {
                    this.inputs.eq(index).prop('checked', false);
                }
            }
            else {
                button.find(':radio').prop('checked', false);
            }

            this.triggerChange();
        }
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
     * Disables the buttons so that the user cannot select an option anymore.
     * @param index Index of the button to disable, all buttons if omitted.
     */
    override disable(index?: number | null): void {
        if (index === null || index === undefined) {
            this.buttons.removeClass('p-state-hover p-state-focus p-state-active')
                    .addClass('p-state-disabled').attr('disabled', 'disabled');
            this.inputs.attr('disabled', 'disabled');
        }
        else {
            const button = this.buttons.eq(index);
            button.removeClass('p-state-hover p-state-focus p-state-active')
                    .addClass('p-state-disabled').attr('disabled', 'disabled');
            this.inputs.eq(index).attr('disabled', 'disabled');
        }
    }

    /**
     * Enables the buttons so that the user can select an option.
     * @param index Index of the button to enable, all buttons if omitted.
     */
    override enable(index?: number | null): void {
        if (index === null || index === undefined) {
            this.buttons.removeClass('p-state-disabled').removeAttr('disabled');
            this.inputs.removeAttr('disabled');
        }
        else {
            const button = this.buttons.eq(index);
            button.removeClass('p-state-disabled').removeAttr('disabled');
            this.inputs.eq(index).removeAttr('disabled');
        }
    }
}
