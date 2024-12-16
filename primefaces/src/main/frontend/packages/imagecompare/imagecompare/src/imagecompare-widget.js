/**
 * __PrimeFaces ImageCompare Widget__
 * 
 * ImageCompare provides a user interface to compare two images. Uses the `ImageCompare` library. To access an instance
 * of the image slider programmatically, you can use the list of instantiated sliders:
 * 
 * ```javascript
 * const widget = PF("myImageCompareWidget");
 * const slider = juxtapose.sliders.filter(slider => slider.wrapper === a.getJQ().get(0))[0];
 * 
 * // Smoothly move the slider to the right.
 * slider.updateSlider("80%", true);
 * ```
 * 
 * @interface {PrimeFaces.widget.ImageCompareCfg} cfg The configuration for the {@link  ImageCompare| ImageCompare widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.showFullLinks Whether image links are shown completely or abbreviated.
 * @prop {string} cfg.leftImage URL of the image to the left
 * @prop {string} cfg.rightImage URL of the image to the right
 */
PrimeFaces.widget.ImageCompare = class ImageCompare extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.cfg.showFullLinks = false;

        new juxtapose.JXSlider("[id='"+this.cfg.id+"']",
            [
                {
                    src: this.cfg.leftimage,
                    label: '',
                    credit: ''
                },
                {
                    src: this.cfg.rightimage,
                    label: '',
                    credit: ''
                }
            ],
            {
                animate: true,
                showLabels: false,
                showCredits: false,
                startingPosition: "50%",
                makeResponsive: false
            });

    }
    
}
