/**
 * The configuration for the {@link Dock} widget.
 */
export interface DockCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether opening and closing the dock should be animated. */
    animate: boolean;
    /** The duration for the opening and closing animation in milliseconds. */
    animationDuration: number;
    /** Whether to block scrolling of the document. */
    blockScroll: boolean;
}

/**
 * __PrimeFaces Dock Widget__
 *
 * Dock component mimics the well known dock interface of Mac OS X.
 */
export class Dock<Cfg extends DockCfg = DockCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM elements for the dock items. */
    items: JQuery = $();
    /** The DOM elements for the clickable anchors of the dock items. */
    links: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.items = this.jq.find('ul li');
        this.links = this.jq.find('li a');

        if (this.cfg.blockScroll) {
            PrimeFaces.utils.preventScrolling();
        }

        this.bindAnimations();
    }

    /**
     * Sets up the hover and click event listeners required for the dock.
     */
    private bindAnimations(): void {
        this.items.on('mouseenter.dock',
            (e) => {
                const item = $(e.currentTarget);
                item.addClass('active');
                item.prev().addClass('prev').prev().addClass('prev-anchor');
                item.next().addClass('next').next().addClass('next-anchor');
            }
        ).on('mouseleave.dock',
            () => {
                this.items.removeClass('active prev next next-anchor prev-anchor');
            }
        );

        // add bounce effect
        if (this.cfg.animate) {
            this.links.on("click.dock", (e) => {
                const item = $(e.currentTarget);
                item.addClass('p-dock-bounce').delay(this.cfg.animationDuration as number).queue(function () {
                    item.removeClass('p-dock-bounce');
                    item.dequeue();
                });
            });
        }

    }
}
