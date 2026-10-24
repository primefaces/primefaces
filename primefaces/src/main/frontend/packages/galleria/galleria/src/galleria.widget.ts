/**
 * Responsive option of the {@link Galleria} thumbnails.
 */
export interface GalleriaResponsiveOption {
    /** Max width of the screen, e.g. `768px`. */
    breakpoint: string;
    /** Number of visible thumbnails up to the breakpoint. */
    numVisible: number;
}

/**
 * The configuration for the {@link Galleria} widget.
 */
export interface GalleriaCfg extends PrimeType.widget.DeferredWidgetCfg {
    /** Index of the first item. */
    activeIndex: number;
    /** Whether the items change on hover of an indicator. */
    changeItemOnIndicatorHover: boolean;
    /** Whether the items are displayed with a slideshow. */
    autoPlay: boolean;
    /** Whether scrolling is infinite. */
    circular: boolean;
    /** Close icon in fullscreen mode. */
    closeIcon: string;
    /** Whether to display the gallery in fullscreen. */
    fullScreen: boolean;
    /** Position of the indicators: `bottom`, `top`, `left` or `right`. */
    indicatorsPosition: string;
    /** Number of visible thumbnails. */
    numVisible: number;
    /** Number of visible thumbnails per breakpoint. */
    responsiveOptions: GalleriaResponsiveOption[] | null;
    /** Whether to display the caption. */
    showCaption: boolean;
    /** Whether to display the indicators. */
    showIndicators: boolean;
    /** Whether to display the indicators on the item. */
    showIndicatorsOnItem: boolean;
    /** Whether to display the navigation buttons of the items. */
    showItemNavigators: boolean;
    /** Whether to display the navigation buttons of the items only on hover. */
    showItemNavigatorsOnHover: boolean;
    /** Whether to display the navigation buttons of the thumbnails. */
    showThumbnailNavigators: boolean;
    /** Whether to display the thumbnails. */
    showThumbnails: boolean;
    /** Interval of the slideshow in milliseconds. */
    transitionInterval: number;
    /** Position of the thumbnails: `bottom`, `top`, `left` or `right`. */
    thumbnailsPosition: string;
    /** Height of the viewport in vertical layout. */
    verticalViewPortHeight: string;
}

const TRANSITION = 'transform 500ms ease 0s';

/** Defaults of the options the server does not render when they have the default value. */
const DEFAULTS: Partial<GalleriaCfg> = {
    activeIndex: 0,
    closeIcon: 'pi pi-times',
    numVisible: 3,
    showThumbnails: true,
    showThumbnailNavigators: true,
    transitionInterval: 4000,
    thumbnailsPosition: 'bottom',
    verticalViewPortHeight: '450px',
    indicatorsPosition: 'bottom'
};

function toggleDisabled(button: JQuery, disabled: boolean): void {
    button.prop('disabled', disabled).toggleClass('ui-state-disabled', disabled);
}

function translate(vertical: boolean, percent: number): string {
    return vertical ? 'translate3d(0, ' + percent + '%, 0)' : 'translate3d(' + percent + '%, 0, 0)';
}

/**
 * __PrimeFaces Galleria Widget__
 *
 * Galleria is a content gallery component.
 */
export class Galleria<Cfg extends GalleriaCfg = GalleriaCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** Index of the displayed item. */
    activeIndex: number = 0;

    private items: GalleriaItems | null = null;
    private thumbnails: GalleriaThumbnails | null = null;
    private mask: JQuery = $();
    private transition: PrimeType.CssTransitionHandler | null = null;
    private interval: number | undefined;
    private itemCount: number = 0;
    private visible: boolean = false;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg = { ...DEFAULTS, ...this.cfg };
        this.renderDeferred();
    }

    protected override _render(): void {
        const content = this.jq.children('.ui-galleria-content');
        const itemsContainer = content.children('.ui-galleria-items');
        this.itemCount = itemsContainer.children('.ui-galleria-item').length;

        if (this.cfg.fullScreen) {
            this.jq.css('display', 'none');
        }
        if (!this.itemCount) {
            return;
        }

        this.activeIndex = this.cfg.activeIndex || 0;
        const vertical = this.cfg.thumbnailsPosition === 'left' || this.cfg.thumbnailsPosition === 'right';
        const position = (prefix: string, value: string) => ['top', 'left', 'bottom', 'right'].includes(value) ? prefix + '-' + value : '';

        this.jq.addClass(PrimeFaces.utils.styleClass({
            'ui-galleria-fullscreen': !!this.cfg.fullScreen,
            'ui-galleria-indicator-onitem': !!this.cfg.showIndicatorsOnItem,
            'ui-galleria-item-nav-onhover': !!this.cfg.showItemNavigatorsOnHover && !this.cfg.fullScreen
        }, this.cfg.showThumbnails ? position('ui-galleria-thumbnails', this.cfg.thumbnailsPosition ?? 'bottom') : '',
        this.cfg.showIndicators ? position('ui-galleria-indicators', this.cfg.indicatorsPosition ?? 'bottom') : ''));

        if (this.cfg.fullScreen) {
            $('<button type="button" class="ui-galleria-close ui-galleria-link"></button>')
                .attr('aria-label', PrimeFaces.getAriaLabel('close'))
                .append($('<span class="ui-button-icon-left ui-galleria-close-icon"></span>').addClass(this.cfg.closeIcon || 'pi pi-times'))
                .prependTo(this.jq)
                .on('click.galleria', () => this.hide());
        }

        const thumbnailContainer = content.children('.ui-galleria-thumbnail-items');
        if (this.cfg.showThumbnails) {
            this.thumbnails = new GalleriaThumbnails(this, thumbnailContainer, vertical);
        }
        else {
            thumbnailContainer.hide();
        }
        this.items = new GalleriaItems(this, itemsContainer, vertical);

        if (this.cfg.fullScreen) {
            this.jq.unwrap('.ui-galleria-mask').wrap('<div class="ui-galleria-mask"></div>');
        }
        this.mask = this.jq.parent('.ui-galleria-mask');
        this.transition = PrimeFaces.utils.registerCSSTransition(this.jq, 'ui-galleria');

        if (vertical) {
            content.css('height', this.cfg.verticalViewPortHeight ?? '450px');
        }
        if (this.cfg.autoPlay) {
            this.startSlideShow();
        }
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        this.cleanup();
        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();
        this.cleanup();
    }

    private cleanup(): void {
        $(document.body).removeClass('ui-overflow-hidden');
        this.stopSlideShow();
        this.thumbnails?.destroy();
    }

    /** Displays the gallery in fullscreen mode, optionally with the item at the given index. */
    show(index?: number): void {
        if (index !== undefined) {
            this.setActiveIndex(index);
        }
        if (this.visible) {
            return;
        }
        this.visible = true;
        this.mask.addClass('ui-galleria-visible');
        this.transition?.show({
            onEnter: () => {
                $(document.body).addClass('ui-overflow-hidden');
            },
            onEntering: () => {
                PrimeFaces.nextZindex(this.mask.addClass('ui-widget-overlay'));
            }
        });
    }

    /** Hides the gallery in fullscreen mode. */
    hide(): void {
        if (!this.visible) {
            return;
        }
        this.visible = false;
        this.mask.removeClass('ui-galleria-visible');
        this.transition?.hide({
            onExit: () => {
                $(document.body).removeClass('ui-overflow-hidden');
                this.mask.addClass('ui-galleria-mask-leave');
            },
            onExited: () => {
                this.mask.removeClass('ui-galleria-mask-leave ui-widget-overlay').css('z-index', '');
            }
        });
    }

    /** Displays the next item. */
    next(): void {
        this.setActiveIndex(this.activeIndex === this.itemCount - 1 ? (this.cfg.circular ? 0 : this.activeIndex) : this.activeIndex + 1);
    }

    /** Displays the previous item. */
    prev(): void {
        this.setActiveIndex(this.activeIndex === 0 ? (this.cfg.circular ? this.itemCount - 1 : 0) : this.activeIndex - 1);
    }

    /** Whether the slideshow is running. */
    isAutoPlayActive(): boolean {
        return this.interval !== undefined;
    }

    /** Starts the slideshow. */
    startSlideShow(): void {
        this.stopSlideShow();
        this.interval = window.setInterval(() => {
            this.setActiveIndex(this.cfg.circular && this.activeIndex === this.itemCount - 1 ? 0 : this.activeIndex + 1);
        }, this.cfg.transitionInterval || 4000);
    }

    /** Stops the slideshow. */
    stopSlideShow(): void {
        if (this.interval !== undefined) {
            clearInterval(this.interval);
            this.interval = undefined;
        }
    }

    /** Displays the item at the given index. */
    setActiveIndex(index: number): void {
        if (index === this.activeIndex || index < 0 || index >= this.itemCount) {
            return;
        }
        const previous = this.activeIndex;
        this.activeIndex = index;
        this.items?.update();
        this.thumbnails?.update(previous);
    }

    /** Called by the navigators, indicators and thumbnails: the user takes over, so the slideshow stops. */
    navigate(index: number): void {
        this.stopSlideShow();
        this.setActiveIndex(index);
    }
}

/**
 * The items of a {@link Galleria} with their navigators, caption and indicators.
 */
class GalleriaItems {
    private readonly galleria: Galleria;
    private readonly container: JQuery;
    private readonly vertical: boolean;
    private readonly caption: JQuery;
    private readonly count: number;
    private readonly prevButton: JQuery;
    private readonly nextButton: JQuery;
    private readonly indicators: JQuery;

    constructor(galleria: Galleria, container: JQuery, vertical: boolean) {
        const cfg = galleria.cfg;
        this.galleria = galleria;
        this.container = container;
        this.vertical = vertical;
        this.caption = container.nextAll('.ui-galleria-caption-items');
        this.count = container.children('.ui-galleria-item').length;

        const navigator = (type: 'prev' | 'next', icon: string) => $('<button type="button" class="ui-galleria-item-nav ui-galleria-link"></button>')
            .addClass('ui-galleria-item-' + type)
            .attr('aria-label', PrimeFaces.getAriaLabel(type === 'prev' ? 'previous' : 'next'))
            .append('<span class="ui-galleria-item-' + type + '-icon ui-icon ' + icon + '"></span>');
        this.prevButton = cfg.showItemNavigators ? navigator('prev', 'ui-icon-circle-triangle-w') : $();
        this.nextButton = cfg.showItemNavigators ? navigator('next', 'ui-icon-circle-triangle-e') : $();

        let indicatorContainer = container.nextAll('.ui-galleria-indicators');
        if (!indicatorContainer.length && cfg.showIndicators) {
            indicatorContainer = $('<ul class="ui-galleria-indicators"></ul>')
                .append(Array.from({ length: this.count }, () => '<li class="ui-galleria-indicator" tabindex="0"><button type="button" tabindex="-1" class="ui-galleria-link"></button></li>').join(''));
        }

        if (cfg.showCaption) {
            this.caption.show();
        }
        container.wrap('<div class="ui-galleria-item-wrapper"><div class="ui-galleria-item-container"></div></div>');
        container.before(this.prevButton).after(this.nextButton, this.caption);
        container.parent().after(indicatorContainer);

        this.indicators = indicatorContainer.children('.ui-galleria-indicator');
        this.indicators.each((index, indicator) => {
            $(indicator).find('button').attr('aria-label', PrimeFaces.getAriaLabel('pageLabel').replace('{page}', String(index + 1)));
        });

        this.bindEvents();
        this.update();
    }

    private bindEvents(): void {
        const cfg = this.galleria.cfg;
        const active = () => this.galleria.activeIndex;

        this.prevButton.on('click.galleria-item-nav', (e) => {
            e.preventDefault();
            this.galleria.navigate(cfg.circular && active() === 0 ? this.count - 1 : Math.max(0, active() - 1));
        });
        this.nextButton.on('click.galleria-item-nav', (e) => {
            e.preventDefault();
            this.galleria.navigate(cfg.circular && active() === this.count - 1 ? 0 : active() + 1);
        });

        this.indicators.off('.galleria-indicator')
            .on('click.galleria-indicator', (e) => this.galleria.navigate($(e.currentTarget).index()))
            .on('mouseenter.galleria-indicator', (e) => {
                if (cfg.changeItemOnIndicatorHover) {
                    this.galleria.navigate($(e.currentTarget).index());
                }
            })
            .on('keydown.galleria-indicator', (e) => {
                if (e.key === 'Enter') {
                    this.galleria.navigate($(e.currentTarget).index());
                }
            });
    }

    update(): void {
        const cfg = this.galleria.cfg;
        const active = this.galleria.activeIndex;
        const css = { transform: translate(this.vertical, active * -100), transition: TRANSITION };
        this.container.css(css);
        this.caption.css(css);

        if (cfg.showItemNavigators) {
            toggleDisabled(this.prevButton, !cfg.circular && active === 0);
            toggleDisabled(this.nextButton, !cfg.circular && active === this.count - 1);
        }
        if (cfg.showIndicators) {
            this.indicators.removeClass('ui-state-highlight').eq(active).addClass('ui-state-highlight');
        }
    }
}

/**
 * The thumbnails of a {@link Galleria} with their navigators.
 */
class GalleriaThumbnails {
    private readonly galleria: Galleria;
    private readonly container: JQuery;
    private readonly vertical: boolean;
    private readonly items: JQuery;
    private readonly prevButton: JQuery;
    private readonly nextButton: JQuery;
    private readonly responsiveOptions: GalleriaResponsiveOption[];
    private numVisible: number;
    private totalShiftedItems: number = 0;
    private style: HTMLStyleElement | null = null;
    private startPos: { x: number; y: number } = { x: 0, y: 0 };
    private readonly resizeListener = () => this.calculatePosition();

    constructor(galleria: Galleria, container: JQuery, vertical: boolean) {
        const cfg = galleria.cfg;
        this.galleria = galleria;
        this.container = container;
        this.vertical = vertical;
        this.items = container.children('.ui-galleria-thumbnail-item');
        this.numVisible = cfg.numVisible || 3;
        // sorted by breakpoint, largest first
        this.responsiveOptions = (cfg.responsiveOptions ?? []).slice()
            .sort((a, b) => String(b.breakpoint).localeCompare(String(a.breakpoint), undefined, { numeric: true }));

        const navigator = (type: 'prev' | 'next', icon: string) => $('<button type="button" class="ui-galleria-link"></button>')
            .addClass('ui-galleria-thumbnail-' + type)
            .append('<span class="ui-galleria-thumbnail-' + type + '-icon ui-icon ' + icon + '"></span>');
        this.prevButton = cfg.showThumbnailNavigators ? navigator('prev', vertical ? 'ui-icon-circle-triangle-n' : 'ui-icon-circle-triangle-w') : $();
        this.nextButton = cfg.showThumbnailNavigators ? navigator('next', vertical ? 'ui-icon-circle-triangle-s' : 'ui-icon-circle-triangle-e') : $();

        container.wrap('<div class="ui-galleria-thumbnail-wrapper"><div class="ui-galleria-thumbnail-container">'
            + '<div class="ui-galleria-thumbnail-items-container"></div></div></div>');
        container.parent().before(this.prevButton).after(this.nextButton);

        this.bindEvents();
        this.createStyle();
        this.calculatePosition();
        if (this.responsiveOptions.length) {
            window.addEventListener('resize', this.resizeListener);
        }
        this.updateUI();
    }

    destroy(): void {
        window.removeEventListener('resize', this.resizeListener);
        this.style?.remove();
        this.style = null;
    }

    private bindEvents(): void {
        this.prevButton.on('click.galleria-thumbnail-nav', (e) => this.navBackward(e));
        this.nextButton.on('click.galleria-thumbnail-nav', (e) => this.navForward(e));

        this.container.off('.galleria-thumbnail')
            .on('transitionend.galleria-thumbnail', (e) => {
                if ((e.originalEvent as TransitionEvent | undefined)?.propertyName === 'transform') {
                    this.container.addClass('ui-items-hidden').css('transition', '');
                }
            })
            .on('touchstart.galleria-thumbnail', (e) => {
                const touch = (e.originalEvent as TouchEvent).changedTouches[0];
                this.startPos = { x: touch?.pageX ?? 0, y: touch?.pageY ?? 0 };
            })
            .on('touchmove.galleria-thumbnail', (e) => e.preventDefault())
            .on('touchend.galleria-thumbnail', (e) => {
                const touch = (e.originalEvent as TouchEvent).changedTouches[0];
                const diff = this.vertical ? (touch?.pageY ?? 0) - this.startPos.y : (touch?.pageX ?? 0) - this.startPos.x;
                if (Math.abs(diff) < 10) {
                    // a tap, the click selects the thumbnail
                    return;
                }
                if (diff < 0) {
                    this.navForward(e);
                }
                else {
                    this.navBackward(e);
                }
            });

        this.items.off('.galleria-thumbnail-item')
            .on('click.galleria-thumbnail-item', (e) => this.galleria.navigate($(e.currentTarget).index()))
            .on('keydown.galleria-thumbnail-item', (e) => {
                if (e.key === 'Enter') {
                    this.galleria.navigate($(e.currentTarget).index());
                }
            });
    }

    private medianIndex(): number {
        const index = Math.floor(this.numVisible / 2);
        return this.numVisible % 2 ? index : index - 1;
    }

    private navBackward(e: JQuery.TriggeredEvent): void {
        e.preventDefault();
        const active = this.galleria.activeIndex;
        this.galleria.navigate(this.galleria.cfg.circular && active === 0 ? this.items.length - 1 : Math.max(0, active - 1));
    }

    private navForward(e: JQuery.TriggeredEvent): void {
        e.preventDefault();
        const active = this.galleria.activeIndex;
        this.galleria.navigate(this.galleria.cfg.circular && active === this.items.length - 1 ? 0 : Math.min(this.items.length - 1, active + 1));
    }

    private createStyle(): void {
        this.style ??= document.body.appendChild(document.createElement('style'));
        const selector = this.galleria.jqId + ' .ui-galleria-thumbnail-items .ui-galleria-thumbnail-item';
        let css = selector + ' { flex: 1 0 ' + (100 / this.numVisible) + '%; }';
        for (const option of this.responsiveOptions) {
            css += '@media screen and (max-width:' + option.breakpoint + ') { ' + selector + ' { flex: 1 0 ' + (100 / option.numVisible) + '%; }}';
        }
        this.style.innerHTML = css;
    }

    private calculatePosition(): void {
        if (!this.responsiveOptions.length) {
            return;
        }
        let numVisible = this.galleria.cfg.numVisible || 3;
        for (const option of this.responsiveOptions) {
            if (parseInt(option.breakpoint, 10) >= window.innerWidth) {
                numVisible = option.numVisible;
            }
        }
        if (numVisible !== this.numVisible) {
            this.numVisible = numVisible;
            this.update(this.galleria.activeIndex, true);
        }
    }

    /** Scrolls the thumbnails to the active item after it changed. */
    update(previousIndex: number, numVisibleChanged: boolean = false): void {
        const active = this.galleria.activeIndex;
        if (!numVisibleChanged && previousIndex === active) {
            return;
        }

        const count = this.items.length;
        const median = this.medianIndex();
        if (active <= median || count <= this.numVisible) {
            this.totalShiftedItems = 0;
        }
        else if (count - this.numVisible + median < active) {
            this.totalShiftedItems = this.numVisible - count;
        }
        else if (count - this.numVisible < active && this.numVisible % 2 === 0) {
            this.totalShiftedItems = -active + median + 1;
        }
        else {
            this.totalShiftedItems = -active + median;
        }

        this.container.css('transform', translate(this.vertical, this.totalShiftedItems * (100 / this.numVisible)));
        if (previousIndex !== active) {
            this.container.removeClass('ui-items-hidden').css('transition', TRANSITION);
        }
        this.updateUI();
    }

    private updateUI(): void {
        const cfg = this.galleria.cfg;
        const active = this.galleria.activeIndex;
        const first = -this.totalShiftedItems;
        const last = first + this.numVisible - 1;

        this.items.each((index, item) => {
            const visible = first <= index && last >= index;
            $(item).toggleClass('ui-galleria-thumbnail-item-current', active === index)
                .toggleClass('ui-galleria-thumbnail-item-active', visible)
                .toggleClass('ui-galleria-thumbnail-item-start', first === index)
                .toggleClass('ui-galleria-thumbnail-item-end', last === index)
                .children('.ui-galleria-thumbnail-item-content').attr('tabindex', visible ? '0' : '');
        });

        if (cfg.showThumbnailNavigators) {
            const fits = this.items.length <= this.numVisible;
            toggleDisabled(this.prevButton, (!cfg.circular && active === 0) || fits);
            toggleDisabled(this.nextButton, (!cfg.circular && active === this.items.length - 1) || fits);
        }
    }
}
