/**
 * The configuration for the {@link  Steps} widget.
 * 
 * You can access this configuration via {@link Steps.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface StepsCfg extends PrimeType.widget.BaseWidgetCfg {
}
    
/**
 * __PrimeFaces Steps Widget__
 * 
 * Steps is a menu component that displays steps of a workflow.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class Steps<Cfg extends StepsCfg = StepsCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * All enabled menu item elements.
     */
    enabledItems: JQuery = $();

    /**
     * All menu item elements.
     */
    items: JQuery= $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.items = this.jq.find('.ui-steps-item');
        this.enabledItems = this.items.filter(".ui-state-default:not(.ui-state-disabled)");

        this.bindEvents();
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        this.enabledItems.on('mouseover', function(_) {
            const element = $(this);
            element.addClass('ui-state-highlight');
        })
        .on('mouseout', function(_) {
            const element = $(this);
            element.removeClass('ui-state-highlight');
        });
    }
}
