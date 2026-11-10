/**
 * The configuration for the {@link Effect} widget.
 */
export interface EffectCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Client ID of the element that triggers the effect. */
    source: string;
    /** Delay before the effect runs, in milliseconds. */
    delay: number;
    /** Event(s) of the source that trigger the effect, `load` to run it on page load. */
    event: string;
    /** Runs the effect. */
    fn: () => void;
}

/**
 * __PrimeFaces Effect Widget__
 *
 * Effect component runs an effect of {@link PrimeFaces.effects} on an element.
 */
export class Effect<Cfg extends EffectCfg = EffectCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The element that triggers the effect. */
    source: JQuery = $();
    /** Schedules the effect after the delay, a scheduled run is replaced. */
    runner: () => void = () => {};
    /** The timer ID of the scheduled run. */
    timeoutId?: number;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        // a refresh does not run the destroy listeners: a run scheduled by the previous initialization is cancelled here
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
            this.timeoutId = undefined;
        }
        super.init(cfg);

        this.source = $(PrimeFaces.escapeClientId(this.cfg.source ?? ''));
        // destroyed with the source
        this.bindLifecycleTo(this.source);

        this.runner = () => {
            // avoid queuing multiple runs
            if (this.timeoutId) {
                clearTimeout(this.timeoutId);
            }

            this.timeoutId = PrimeFaces.queueTask(this.cfg.fn ?? (() => {}), this.cfg.delay);
        };

        const namespace = '.effect' + this.getId();
        if (this.cfg.event === 'load') {
            this.runner();
        }
        else {
            const events = (this.cfg.event ?? '').split(' ').map((e) => e + namespace).join(' ');
            this.source.off(namespace).on(events, this.runner);
        }

        // the source may stay when the effect is removed
        this.addDestroyListener(() => {
            if (this.timeoutId) {
                clearTimeout(this.timeoutId);
            }
            this.source.off(namespace);
        });
    }
}
