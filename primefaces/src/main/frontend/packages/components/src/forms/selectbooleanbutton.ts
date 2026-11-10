/**
 * The configuration for the {@link SelectBooleanButton} widget.
 */
export interface SelectBooleanButtonCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Label when the button is on, defaults to the locale. */
    onLabel: string;
    /** Icon when the button is on. */
    onIcon: string;
    /** Label when the button is off, defaults to the locale. */
    offLabel: string;
    /** Icon when the button is off. */
    offIcon: string;
}

/**
 * __PrimeFaces SelectBooleanButton Widget__
 *
 * SelectBooleanButton is used to select a binary decision with a toggle button.
 */
export class SelectBooleanButton<Cfg extends SelectBooleanButtonCfg = SelectBooleanButtonCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The hidden checkbox input with the value. */
    input: JQuery<HTMLInputElement> = $();
    /** Whether this button is disabled. */
    disabled: boolean = false;
    /** The icon of the button. */
    icon: JQuery = $();
    /** The label when the button is on. */
    onLabel: string = '';
    /** The label when the button is off. */
    offLabel: string = '';

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.input = $(this.jqId + '_input') as JQuery<HTMLInputElement>;
        this.disabled = this.input.is(':disabled');
        this.icon = this.jq.children('.p-button-icon-left');
        this.onLabel = this.cfg.onLabel || this.getAriaLabel('switch.ON');
        this.offLabel = this.cfg.offLabel || this.getAriaLabel('switch.OFF');

        // bind events if not disabled
        if (!this.disabled) {
            this.bindButtonEvents();
        }

        if (this.input.prop('checked')) {
            this.input.attr('aria-label', this.onLabel);
            this.jq.children('.p-button-text').text(this.onLabel);
        }
        else {
            this.input.attr('aria-label', this.offLabel);
            this.jq.children('.p-button-text').text(this.offLabel);
        }

        this.input.on('focus.selectBooleanButton', () => {
            this.jq.addClass('p-state-focus');
        })
        .on('blur.selectBooleanButton', () => {
            this.jq.removeClass('p-state-focus');
        })
        .on('keydown.selectBooleanButton', (e) => {
            if (e.key === ' ') {
                e.preventDefault();
            }
        })
        .on('keyup.selectBooleanButton', (e) => {
            if (e.key === ' ') {
                this.toggle();

                e.preventDefault();
            }
        });

        // pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    /**
     * Binds the mouse events of the button, also by {@link enable} if it was disabled at render.
     */
    private bindButtonEvents(): void {
        this.jq.off('mouseover.selectBooleanButton mouseout.selectBooleanButton click.selectBooleanButton')
        .on('mouseover.selectBooleanButton', () => {
            if (!this.jq.hasClass('p-state-active')) {
                this.jq.addClass('p-state-hover');
            }
        }).on('mouseout.selectBooleanButton', () => {
            this.jq.removeClass('p-state-hover');
        })
        .on('click.selectBooleanButton', () => {
            this.toggle();
            this.input.trigger('focus');
        });
    }

    /**
     * Turns this button on if it is off, or off if it is on.
     */
    toggle(): void {
        if (!this.disabled) {
            if (this.input.prop('checked')) {
                this.uncheck();
            }
            else {
                this.check();
            }
        }
    }

    /**
     * Turns this button on, which checks the underlying checkbox.
     */
    check(): void {
        if (!this.disabled) {
            this.input.prop('checked', true);
            this.input.attr('aria-label', this.onLabel);
            this.jq.addClass('p-state-active').children('.p-button-text').text(this.onLabel);

            if (this.icon.length > 0) {
                this.icon.removeClass(this.cfg.offIcon ?? '').addClass(this.cfg.onIcon ?? '');
            }

            this.input.trigger('change');
        }
    }

    /**
     * Turns this button off, which unchecks the underlying checkbox.
     */
    uncheck(): void {
        if (!this.disabled) {
            this.input.prop('checked', false);
            this.input.attr('aria-label', this.offLabel);
            this.jq.removeClass('p-state-active').children('.p-button-text').text(this.offLabel);

            if (this.icon.length > 0) {
                this.icon.removeClass(this.cfg.onIcon ?? '').addClass(this.cfg.offIcon ?? '');
            }

            this.input.trigger('change');
        }
    }

    /**
     * Enables this button so that the user can change it.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.disabled = false;
        this.bindButtonEvents();
    }

    /**
     * Disables this button so that the user cannot change it anymore.
     */
    override disable(): void {
        // the input, not the div, is disabled: a disabled input is not submitted and not focusable
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.disabled = true;
    }
}
