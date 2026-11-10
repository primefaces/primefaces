/**
 * The configuration for the {@link ScrollTop} widget.
 */
export interface ScrollTopCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Scroll behavior when scrolling to the top. */
    behavior: ScrollBehavior;
    /** `window` to scroll the window, `parent` to scroll the parent element. */
    target: string;
    /** Vertical scroll position of the target from which on the button is shown. */
    threshold: number;
}

/**
 * __PrimeFaces ScrollTop Widget__
 *
 * ScrollTop gets displayed after a certain scroll position and is used to navigate to the top of the page quickly.
 */
export class ScrollTop<Cfg extends ScrollTopCfg = ScrollTopCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The window or the parent element that is scrolled. */
    scrollElement: JQuery<Element | Window> = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.scrollElement = this.cfg.target === 'window' ? $<Element | Window>(window) : this.jq.parent();

        this.bindEvents();
    }

    private bindEvents(): void {
        const scrollNS = 'scroll.scrollTop' + this.getId();
        const zIndex = this.jq.css('zIndex');

        this.jq.on('click.scrollTop', (e) => {
            this.scrollElement.get(0)?.scroll({
                top: 0,
                behavior: this.cfg.behavior
            });

            e.preventDefault();
        });

        this.scrollElement.off(scrollNS).on(scrollNS, () => {
            if ((this.cfg.threshold ?? 0) < (this.scrollElement.scrollTop() ?? 0)) {
                this.jq.fadeIn({
                    duration: 150,
                    start: () => {
                        if (zIndex === 'auto' && this.jq.css('zIndex') === 'auto') {
                            PrimeFaces.nextZindex(this.jq);
                        }
                    }
                });
            }
            else {
                this.jq.fadeOut({
                    duration: 150,
                    start: () => {
                        if (zIndex === 'auto') {
                            this.jq.css('zIndex', '');
                        }
                    }
                });
            }
        });

        const scrollElement = this.scrollElement;
        this.addDestroyListener(() => {
            scrollElement.off(scrollNS);
        });
    }
}
