/**
 * The configuration for the {@link  Steps} component.
 * 
 * You can access this configuration via {@link Steps.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface StepsCfg extends PrimeFaces.component.BaseComponentCfg {
}
    
/**
 * __PrimeFaces Steps Component__
 * 
 * Steps is a menu component that displays steps of a workflow.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class Steps<Cfg extends StepsCfg = StepsCfg> extends PrimeFaces.component.BaseComponent<Cfg> {
    /**
     * All enabled menu item elements.
     */
    enabledItems: JQuery = $();

    /**
     * All menu item elements.
     */
    items: JQuery= $();

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);

        this.items = this.jq.find('.p-steps-item');
        this.enabledItems = this.items.filter(".p-state-default:not(.p-state-disabled)");

        this.bindEvents();
    }

    /**
     * Sets up all event listeners that are required by this component.
     */
    private bindEvents(): void {
        this.enabledItems.on('mouseover', function(_) {
            const element = $(this);
            element.addClass('p-state-highlight');
        })
        .on('mouseout', function(_) {
            const element = $(this);
            element.removeClass('p-state-highlight');
        });
    }
}
