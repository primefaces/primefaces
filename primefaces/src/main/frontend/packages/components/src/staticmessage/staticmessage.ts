/**
 * The configuration for the {@link StaticMessage} component.
 * 
 * You can access this configuration via {@link StaticMessage.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface StaticMessageCfg extends PrimeFaces.component.BaseComponentCfg {
}

/**
 * __PrimeFaces StaticMessage Component__
 * 
 * StaticMessage component to handle events.
 *
 * @typeParam Cfg Type of the configuration object.
 */
export class StaticMessage<Cfg extends StaticMessageCfg = StaticMessageCfg> extends PrimeFaces.component.BaseComponent<Cfg> {

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);
        this.bindEvents();
    }

    /**
     * Bind behavior events.
     */
    bindEvents(): void {
        const closer = $('.p-messages-close', this.jq);

        closer.on('click.staticmessage', () => {
            if (this.hasBehavior('close')) {
                this.callBehavior('close');
            }
        });
        PrimeFaces.skinCloseAction(closer);
    }
}
