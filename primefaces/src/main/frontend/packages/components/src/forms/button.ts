/**
 * The configuration for the {@link Button} widget.
 */
export interface ButtonCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
}

/**
 * __PrimeFaces Button Widget__
 *
 * Button is an extension to the standard h:button component with skinning capabilities.
 */
export class Button<Cfg extends ButtonCfg = ButtonCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        PrimeFaces.skinButton(this.jq);
    }

    /**
     * Disables this button so that the user cannot press it.
     */
    override disable(): void {
        PrimeFaces.utils.disableButton(this.jq);
    }

    /**
     * Enables this button so that the user can press it.
     */
    override enable(): void {
        PrimeFaces.utils.enableButton(this.jq);
    }
}
