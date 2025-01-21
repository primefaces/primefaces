/**
 * __PrimeFaces ImageSwitch Widget__
 *
 * ImageSwitch is an image gallery component with 25+ effects. Use the JQuery cycle plugin. You can also interact with
 * the imageswitch programatically via `$.fn.cycle`:
 *
 * ```javascript
 * const widget = PF("myImageSwitchWidget");
 * widget.getJQ().cycle("resume");
 * ```
 *
 * @interface {PrimeFaces.widget.ImageSwitchCfg} cfg The configuration for the {@link  ImageSwitch| ImageSwitch widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryCycle.Configuration} cfg
 */
PrimeFaces.widget.ImageSwitch = class ImageSwitch extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.jq.cycle(this.cfg);
    }

    /**
     * Starts slideshow mode.
     */
    resumeSlideshow() {
        this.jq.cycle('resume');
    }

    /**
     * Stops slideshow mode.
     */
    stopSlideshow() {
        this.jq.cycle('stop');
    }

    /**
     * Stops or starts slideshow mode.
     */
    toggleSlideshow() {
        this.jq.cycle('toggle');
    }

    /**
     * Pauses slideshow mode.
     */
    pauseSlideshow() {
        this.jq.cycle('pause');
    }

    /**
     * Switches to the next image.
     */
    next() {
        this.jq.cycle('next');
    }

    /**
     * Switches to the previous image.
     */
    previous() {
        this.jq.cycle('prev');
    }

    /**
     * Switches to the image with given index.
     * @param {number} index 0-based index of the image to switch to.
     */
    switchTo(index) {
        this.jq.cycle(index);
    }
}