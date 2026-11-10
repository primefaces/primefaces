/**
 * The configuration for the {@link ImageSwitch} widget.
 */
export interface ImageSwitchCfg extends PrimeFacesTypes.widget.BaseWidgetCfg, JQueryCycle.Configuration {
}

/**
 * The container element of the cycle plugin, with the properties of its timer chain.
 */
type CycleContainer = HTMLElement & {
    /** Counter that is incremented to stop the pending callbacks of the timer chain. */
    cycleStop?: number;
    /** The ID of the timeout of the next transition. */
    cycleTimeout?: number;
};

/**
 * __PrimeFaces ImageSwitch Widget__
 *
 * ImageSwitch is an image gallery component with 25+ effects. Use the JQuery cycle plugin. You can also interact with
 * the imageswitch programmatically via `$.fn.cycle`:
 *
 * ```javascript
 * const widget = PF("myImageSwitchWidget");
 * widget.getJQ().cycle("resume");
 * ```
 */
export class ImageSwitch<Cfg extends ImageSwitchCfg = ImageSwitchCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.jq.cycle(this.cfg);
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this.stopCycleTimer();
        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();
        this.stopCycleTimer();
    }

    /**
     * Stops the timer chain of the cycle plugin on the (possibly already detached) element. The plugin data is already
     * gone when the element was removed by an update, so `cycle('stop')` can't be used here.
     */
    private stopCycleTimer(): void {
        const cont: CycleContainer | undefined = this.jq.get(0);
        if (cont) {
            cont.cycleStop = (cont.cycleStop || 0) + 1; // pending callbacks look for this change
            clearTimeout(cont.cycleTimeout);
            cont.cycleTimeout = 0;
        }
    }

    /**
     * Starts slideshow mode.
     */
    resumeSlideshow(): void {
        this.jq.cycle('resume');
    }

    /**
     * Stops slideshow mode.
     */
    stopSlideshow(): void {
        this.jq.cycle('stop');
    }

    /**
     * Stops or starts slideshow mode.
     */
    toggleSlideshow(): void {
        this.jq.cycle('toggle');
    }

    /**
     * Pauses slideshow mode.
     */
    pauseSlideshow(): void {
        this.jq.cycle('pause');
    }

    /**
     * Switches to the next image.
     */
    next(): void {
        this.jq.cycle('next');
    }

    /**
     * Switches to the previous image.
     */
    previous(): void {
        this.jq.cycle('prev');
    }

    /**
     * Switches to the image with given index.
     * @param index 0-based index of the image to switch to.
     */
    switchTo(index: number): void {
        this.jq.cycle(index);
    }
}
