/**
 * The configuration for the {@link StaticMessage} widget.
 * 
 * You can access this configuration via {@link StaticMessage.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface StaticMessageCfg extends PrimeType.widget.BaseWidgetCfg {
}

/**
 * __PrimeFaces StaticMessage Widget__
 * 
 * StaticMessage widget to handle events.
 *
 * @typeParam Cfg Type of the configuration object.
 */
export class StaticMessage<Cfg extends StaticMessageCfg = StaticMessageCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.bindEvents();
    }

    /**
     * Bind behavior events.
     */
    bindEvents(): void {
        const closer = $('.ui-messages-close', this.jq);

        closer.on('click.staticmessage', () => {
            if (this.hasBehavior('close')) {
                this.callBehavior('close');
            }
        });
        PrimeFaces.skinCloseAction(closer);
    }
}
