/**
 * The configuration for the {@link ToggleSwitch} widget.
 */
export interface ToggleSwitchCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Requests a confirmation before the value changes, called with the switch element as `this`. */
    confirmation: (this: HTMLElement) => void;
}

/**
 * __PrimeFaces ToggleSwitch Widget__
 *
 * ToggleSwitch is used to select a boolean value.
 */
export class ToggleSwitch<Cfg extends ToggleSwitchCfg = ToggleSwitchCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The slider. */
    slider: JQuery = $();
    /** The handle of the slider. */
    handler: JQuery = $();
    /** The hidden checkbox input with the value. */
    input: JQuery<HTMLInputElement> = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.slider = this.jq.children('.p-toggleswitch-slider');
        this.handler = this.slider.children('.p-toggleswitch-handler');
        this.input = $(this.jqId + '_input') as JQuery<HTMLInputElement>;

        if (!this.input.is(':disabled')) {
            this._bindEvents();
        }

        if (this.input.attr('aria-checked') === 'true' || this.input.prop('checked')) {
            this.input.attr('aria-label', this.getAriaLabel('switch.ON'));
        }
        else {
            this.input.attr('aria-label', this.getAriaLabel('switch.OFF'));
        }

        // pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    private _bindEvents(): void {
        this.slider.off('click.toggleSwitch').on('click.toggleSwitch', () => {
            if (this.cfg.confirmation) {
                this.input.trigger('focus.toggleSwitch');
                this.requestToggle();
            }
            else {
                this.input.trigger('click').trigger('focus.toggleSwitch');
            }
        });

        this.input.off('click.toggleSwitch').on('click.toggleSwitch', (e) => {
            if (this.cfg.confirmation) {
                e.preventDefault();
                this.requestToggle();
            }
        });

        this.input.off('focus.toggleSwitch').on('focus.toggleSwitch', () => {
            this.jq.addClass('p-toggleswitch-focus');
        })
        .off('blur.toggleSwitch').on('blur.toggleSwitch', () => {
            this.jq.removeClass('p-toggleswitch-focus');
        })
        .off('keydown.toggleSwitch').on('keydown.toggleSwitch', (e) => {
            if (e.key === ' ') {
                e.preventDefault();
            }
        })
        .off('keyup.toggleSwitch').on('keyup.toggleSwitch', (e) => {
            if (e.key === ' ') {
                this.requestToggle();

                e.preventDefault();
            }
        })
        .off('change.toggleSwitch').on('change.toggleSwitch', () => {
            if (this.isChecked()) {
                this.check(true);
            }
            else {
                this.uncheck(true);
            }
        });
    }

    /**
     * Toggles the switch, after a confirmation if one is configured.
     */
    private requestToggle(): void {
        if (this.input.is(':disabled')) {
            return;
        }

        if (this.cfg.confirmation) {
            this.jq.data('pfconfirmcommand', () => this.toggle());
            this.cfg.confirmation.call(this.jq[0] as HTMLElement);
        }
        else {
            this.toggle();
        }
    }

    /**
     * @returns `true` if this switch is on.
     */
    isChecked(): boolean {
        return this.input.prop('checked');
    }

    /**
     * Turns this switch off if it is on, or on if it is off.
     */
    toggle(): void {
        if (this.isChecked()) {
            this.uncheck();
        }
        else {
            this.check();
        }
    }

    /**
     * Turns this switch on.
     * @param silent `true` to suppress the `change` event.
     */
    check(silent?: boolean): void {
        this.input.prop('checked', true).attr('aria-checked', 'true').attr('aria-label', this.getAriaLabel('switch.ON'));
        if (!silent) {
            this.input.trigger('change');
        }
        this.jq.addClass('p-toggleswitch-checked');
    }

    /**
     * Turns this switch off.
     * @param silent `true` to suppress the `change` event.
     */
    uncheck(silent?: boolean): void {
        this.input.prop('checked', false).attr('aria-checked', 'false').attr('aria-label', this.getAriaLabel('switch.OFF'));
        if (!silent) {
            this.input.trigger('change');
        }
        this.jq.removeClass('p-toggleswitch-checked');
    }

    /**
     * Disables this switch so that the user cannot change it anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    }

    /**
     * Enables this switch so that the user can change it.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        // disabled at render, the events were not bound
        this._bindEvents();
    }
}
