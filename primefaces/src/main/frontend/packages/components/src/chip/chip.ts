/**
 * The configuration for the {@link Chip} component.
 */
export interface ChipCfg extends PrimeFaces.component.BaseComponentCfg {
}

/**
 * __PrimeFaces Chip Component__
 *
 * Chip represents entities using icons, labels and images.
 */
export class Chip<Cfg extends ChipCfg = ChipCfg> extends PrimeFaces.component.BaseComponent<Cfg> {
    /** The icon for closing this chip, when this chip is closable (an `x` by default). */
    removeIcon: JQuery = $();

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);

        this.removeIcon = this.jq.children('.p-chip-remove-icon');

        this.bindEvents();
    }

    private bindEvents(): void {
        this.jq.on('click.chip', () => {
            this.callBehavior('select');
        });

        this.removeIcon.on('keydown.chip', (e) => {
            if (PrimeFaces.utils.isActionKey(e)) {
                this.close();
                e.preventDefault();
            }
        }).on('click.chip', () => {
            this.close();
        });
    }

    private close(): void {
        this.jq.remove();
        this.callBehavior('close');
    }
}
