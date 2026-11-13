/**
 * The configuration for the {@link ImageCompare} component.
 */
export interface ImageCompareCfg extends PrimeFacesTypes.component.BaseComponentCfg {
    /** Whether image links are shown completely or abbreviated. */
    showFullLinks: boolean;
    /** URL of the image to the left. */
    leftimage: string;
    /** URL of the image to the right. */
    rightimage: string;
}

/**
 * __PrimeFaces ImageCompare Component__
 *
 * ImageCompare provides a user interface to compare two images. Uses the `ImageCompare` library. To access an instance
 * of the image slider programmatically, you can use the list of instantiated sliders:
 *
 * ```javascript
 * const component = PF("myImageCompare");
 * const slider = juxtapose.sliders.filter(slider => slider.wrapper === a.getJQ().get(0))[0];
 *
 * // Smoothly move the slider to the right.
 * slider.updateSlider("80%", true);
 * ```
 */
export class ImageCompare<Cfg extends ImageCompareCfg = ImageCompareCfg> extends PrimeFaces.component.BaseComponent<Cfg> {
    /** The image slider instance of this component. */
    slider?: juxtapose.JXSlider | null;

    override init(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.showFullLinks = false;

        this.slider = new juxtapose.JXSlider("[id='"+this.cfg.id+"']",
            [
                {
                    src: this.cfg.leftimage!,
                    label: '',
                    credit: ''
                },
                {
                    src: this.cfg.rightimage!,
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

    override refresh(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): void {
        this.destroySlider();
        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();
        this.destroySlider();
    }

    /**
     * Releases the global listeners and references of the slider.
     */
    private destroySlider(): void {
        if (this.slider) {
            this.slider.destroy();
            this.slider = null;
        }
    }
}
