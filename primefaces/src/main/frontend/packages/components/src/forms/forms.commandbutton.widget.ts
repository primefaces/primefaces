/**
 * The configuration for the {@link CommandButton} widget.
 * 
 * You can access this configuration via {@link CommandButton.cfg | cfg}. Please
 * note that this configuration is usually meant to be read-only and should not
 * be modified. 
 */
export interface CommandButtonCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * When set to `true` this button is only enabled after successful client
     * side validation, otherwise classic behavior. Used together with
     * `p:clientValidator`.
     */
    validateClientDynamic: boolean;
}

/**
 * __PrimeFaces CommandButton Widget__
 * 
 * CommandButton is an extended version of standard commandButton with AJAX and theming.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class CommandButton<Cfg extends CommandButtonCfg = CommandButtonCfg> extends PrimeFaces.widget.BaseWidget {
    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        PrimeFaces.skinButton(this.jq);

        this.bindTriggers();

        if (cfg.validateClientDynamic) {
            // init enabled/disabled-state after initial page-load
            PrimeFaces.queueTask(() => {
                const button = this.jq[0];
                if (button) {
                    PrimeFaces.validation.validateButtonCsvRequirements(button);
                }
            }, 0);

            // update enabled/disabled-state after ajax-updates
            PrimeFaces.validation.bindAjaxComplete();
        }
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);

        super.refresh(cfg);
    }

    /**
     * Sets up the global event listeners on the button.
     */
    private bindTriggers(): void {
        PrimeFaces.bindButtonInlineAjaxStatus(this, this.jq);
    }

    /**
     * Disables this button so that the user cannot press the button anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableButton(this.jq);
    }

    /**
     * Enables this button so that the user can press the button.
     */
    override enable(): void {
        PrimeFaces.utils.enableButton(this.jq);
    }
}
