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
export interface GalleriaCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
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

/** CSS transition used when the items or the thumbnails scroll to another item. */
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

/**
 * Enables or disables a navigator button, via its `disabled` property and the style class `ui-state-disabled`.
 * @param button The navigator button.
 * @param disabled `true` to disable, `false` to enable the button.
 */
function toggleDisabled(button: JQuery, disabled: boolean): void {
    button.prop('disabled', disabled).toggleClass('ui-state-disabled', disabled);
}

/**
 * Builds the CSS transform that scrolls the items or the thumbnails along the given axis.
 * @param vertical `true` to scroll vertically, `false` to scroll horizontally.
 * @param percent The distance in percent of the size of the container, negative to scroll forward.
 * @returns The CSS `transform` value.
 */
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

    /** The items with their navigators, caption and indicators, `null` before rendering or without items. */
    private items: GalleriaItems | null = null;
    /** The thumbnails with their navigators, `null` before rendering or when `showThumbnails` is disabled. */
    private thumbnails: GalleriaThumbnails | null = null;
    /** The mask around the gallery in fullscreen mode, empty otherwise. */
    private mask: JQuery = $();
    /** Handler for the CSS transitions when showing / hiding the gallery in fullscreen mode. */
    private transition: PrimeFacesTypes.CssTransitionHandler | null = null;
    /** ID of the interval of the running slideshow, `undefined` when the slideshow is stopped. */
    private interval: number | undefined;
    /** The number of items. */
    private itemCount: number = 0;
    /** Whether the gallery is shown in fullscreen mode. */
    private visible: boolean = false;

    /**
     * Initializes the gallery: applies the defaults of the options the server omits and renders the gallery once it
     * is visible.
     * @param cfg The configuration of the gallery.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg = { ...DEFAULTS, ...this.cfg };
        this.renderDeferred();
    }

    /**
     * Renders the client-side parts of the gallery once it is visible: the layout classes, the close button and mask
     * in fullscreen mode, the items and the thumbnails. Starts the slideshow when `autoPlay` is enabled. Does nothing
     * without items.
     */
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
        /**
         * Builds the style class for a position of the thumbnails or indicators.
         * @param prefix The prefix of the style class.
         * @param value The position: `top`, `left`, `bottom` or `right`.
         * @returns The style class, e.g. `ui-galleria-thumbnails-left`, empty for an invalid position.
         */
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

    /**
     * Cleans up the previous rendering (slideshow, thumbnail listeners and styles) before the gallery is updated.
     * @param cfg The new configuration of the gallery.
     */
    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this.cleanup();
        super.refresh(cfg);
    }

    /** Stops the slideshow and removes the global resize listener and styles of the thumbnails. */
    override destroy(): void {
        super.destroy();
        this.cleanup();
    }

    /**
     * Releases what the rendering added outside of the gallery: the scroll lock of the body, the slideshow interval
     * and the resize listener and styles of the thumbnails. Resets the rendering state for a refresh.
     */
    private cleanup(): void {
        $(document.body).removeClass('ui-overflow-hidden');
        this.stopSlideShow();
        this.thumbnails?.destroy();
        // refresh renders into the same instance
        this.items = null;
        this.thumbnails = null;
        this.visible = false;
    }

    /**
     * Displays content in fullscreen mode. The index will correspond to the item to be shown, otherwise, it will
     * default to the active item. The body cannot be scrolled while the gallery is shown.
     * @param index Optional index of the item to show.
     */
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

    /** Hides content on fullscreen mode and makes the body scrollable again. */
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

    /**
     * Moves to the next content that comes after the currently shown content. At the last item, moves to the first
     * item when `circular` is enabled, otherwise does nothing.
     */
    next(): void {
        this.setActiveIndex(this.activeIndex === this.itemCount - 1 ? (this.cfg.circular ? 0 : this.activeIndex) : this.activeIndex + 1);
    }

    /**
     * Moves to the previous content that comes before the currently shown content. At the first item, moves to the
     * last item when `circular` is enabled, otherwise does nothing.
     */
    prev(): void {
        this.setActiveIndex(this.activeIndex === 0 ? (this.cfg.circular ? this.itemCount - 1 : 0) : this.activeIndex - 1);
    }

    /**
     * Checks whether the slideshow is running.
     * @returns `true` if the slideshow is running, `false` otherwise.
     */
    isAutoPlayActive(): boolean {
        return this.interval !== undefined;
    }

    /**
     * Starts (or restarts) the slideshow, which moves to the next item every `transitionInterval` milliseconds.
     * Without `circular`, the slideshow stops at the last item.
     */
    startSlideShow(): void {
        this.stopSlideShow();
        this.interval = window.setInterval(() => {
            this.setActiveIndex(this.cfg.circular && this.activeIndex === this.itemCount - 1 ? 0 : this.activeIndex + 1);
            // without circular the slideshow ends at the last item
            if (!this.cfg.circular && this.activeIndex === this.itemCount - 1) {
                this.stopSlideShow();
            }
        }, this.cfg.transitionInterval || 4000);
    }

    /** Stops the slideshow, if it is running. */
    stopSlideShow(): void {
        if (this.interval !== undefined) {
            clearInterval(this.interval);
            this.interval = undefined;
        }
    }

    /**
     * Displays the item at the given index and scrolls the thumbnails to it. Does nothing for the active item or an
     * index out of range. Does not stop the slideshow.
     * @param index The index of the item to display.
     */
    setActiveIndex(index: number): void {
        if (index === this.activeIndex || index < 0 || index >= this.itemCount) {
            return;
        }
        const previous = this.activeIndex;
        this.activeIndex = index;
        this.items?.update();
        this.thumbnails?.update(previous);
    }

    /**
     * Called by the navigators, indicators and thumbnails: the user takes over, so the slideshow stops.
     * @param index The index of the item to display.
     */
    navigate(index: number): void {
        this.stopSlideShow();
        this.setActiveIndex(index);
    }
}

/**
 * The items of a {@link Galleria} with their navigators, caption and indicators.
 */
class GalleriaItems {
    /** The gallery the items belong to. */
    private readonly galleria: Galleria;
    /** The container of the items, scrolled to the active item. */
    private readonly container: JQuery;
    /** Whether the items scroll vertically, i.e. the thumbnails are on the left or right. */
    private readonly vertical: boolean;
    /** The container of the captions, scrolled along with the items. */
    private readonly caption: JQuery;
    /** The number of items. */
    private readonly count: number;
    /** The button to display the previous item, empty when `showItemNavigators` is disabled. */
    private readonly prevButton: JQuery;
    /** The button to display the next item, empty when `showItemNavigators` is disabled. */
    private readonly nextButton: JQuery;
    /** The indicators, one per item. */
    private readonly indicators: JQuery;

    /**
     * Renders the item navigators, the caption and the indicators (created if the server did not render them) around
     * the items, binds their events and displays the active item.
     * @param galleria The gallery the items belong to.
     * @param container The container of the items.
     * @param vertical Whether the thumbnails are on the left or right.
     */
    constructor(galleria: Galleria, container: JQuery, vertical: boolean) {
        const cfg = galleria.cfg;
        this.galleria = galleria;
        this.container = container;
        this.vertical = vertical;
        this.caption = container.nextAll('.ui-galleria-caption-items');
        this.count = container.children('.ui-galleria-item').length;

        /**
         * Creates the button to display the previous / next item, with an ARIA label.
         * @param type Whether the button displays the previous or the next item.
         * @param icon The style class of the icon.
         * @returns The new button.
         */
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

    /**
     * Sets up the clicks of the item navigators and the click, hover (with `changeItemOnIndicatorHover`) and `Enter`
     * key of the indicators, which display the respective item and stop the slideshow.
     */
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

    /**
     * Scrolls the items and captions to the active item, disables the navigators at the first / last item (without
     * `circular`) and highlights the indicator of the active item.
     */
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
    /** The gallery the thumbnails belong to. */
    private readonly galleria: Galleria;
    /** The container of the thumbnails, scrolled so the active thumbnail is visible. */
    private readonly container: JQuery;
    /** Whether the thumbnails are on the left or right and scroll vertically. */
    private readonly vertical: boolean;
    /** The thumbnails, one per item. */
    private readonly items: JQuery;
    /** The button to display the previous item, empty when `showThumbnailNavigators` is disabled. */
    private readonly prevButton: JQuery;
    /** The button to display the next item, empty when `showThumbnailNavigators` is disabled. */
    private readonly nextButton: JQuery;
    /** The responsive options, sorted by breakpoint, largest first. */
    private readonly responsiveOptions: GalleriaResponsiveOption[];
    /** The number of visible thumbnails for the current screen width. */
    private numVisible: number;
    /** The number of thumbnails scrolled out at the start, as a negative number (or `0`). */
    private totalShiftedItems: number = 0;
    /** Style element with the widths of the thumbnails per breakpoint, appended to the body. */
    private style: HTMLStyleElement | null = null;
    /** The position where the current touch started, to detect swipes. */
    private startPos: { x: number; y: number } = { x: 0, y: 0 };
    /** Listener for the window resize, recalculates the visible thumbnails with responsive options. */
    private readonly resizeListener = () => this.calculatePosition();

    /**
     * Renders the thumbnail navigators around the thumbnails, binds their events, creates the responsive styles and
     * scrolls the thumbnails to the active item. With responsive options, listens to the resize of the window.
     * @param galleria The gallery the thumbnails belong to.
     * @param container The container of the thumbnails.
     * @param vertical Whether the thumbnails are on the left or right.
     */
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

        /**
         * Creates the button to display the previous / next item next to the thumbnails.
         * @param type Whether the button displays the previous or the next item.
         * @param icon The style class of the icon.
         * @returns The new button.
         */
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

    /** Removes the resize listener of the window and the style element of the thumbnails. */
    destroy(): void {
        window.removeEventListener('resize', this.resizeListener);
        this.style?.remove();
        this.style = null;
    }

    /**
     * Sets up the clicks of the thumbnail navigators, the swipes on the thumbnails, the end of the scroll transition,
     * and the click and `Enter` key of the thumbnails, which display the respective item and stop the slideshow.
     */
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

    /**
     * Determines the position among the visible thumbnails at which the active thumbnail is kept while scrolling.
     * @returns The index of the middle visible thumbnail, the left one of the two middle ones for an even number.
     */
    private medianIndex(): number {
        const index = Math.floor(this.numVisible / 2);
        return this.numVisible % 2 ? index : index - 1;
    }

    /**
     * Displays the previous item (the last one at the first item with `circular`) and stops the slideshow.
     * @param e The event of the navigator or the swipe.
     */
    private navBackward(e: JQuery.TriggeredEvent): void {
        e.preventDefault();
        const active = this.galleria.activeIndex;
        this.galleria.navigate(this.galleria.cfg.circular && active === 0 ? this.items.length - 1 : Math.max(0, active - 1));
    }

    /**
     * Displays the next item (the first one at the last item with `circular`) and stops the slideshow.
     * @param e The event of the navigator or the swipe.
     */
    private navForward(e: JQuery.TriggeredEvent): void {
        e.preventDefault();
        const active = this.galleria.activeIndex;
        this.galleria.navigate(this.galleria.cfg.circular && active === this.items.length - 1 ? 0 : Math.min(this.items.length - 1, active + 1));
    }

    /**
     * Creates (or updates) the style element that sizes the thumbnails, so `numVisible` thumbnails fill the
     * container, with a media query per responsive option.
     */
    private createStyle(): void {
        this.style ??= document.body.appendChild(document.createElement('style'));
        const selector = this.galleria.jqId + ' .ui-galleria-thumbnail-items .ui-galleria-thumbnail-item';
        let css = selector + ' { flex: 1 0 ' + (100 / this.numVisible) + '%; }';
        for (const option of this.responsiveOptions) {
            css += '@media screen and (max-width:' + option.breakpoint + ') { ' + selector + ' { flex: 1 0 ' + (100 / option.numVisible) + '%; }}';
        }
        this.style.innerHTML = css;
    }

    /**
     * Determines the number of visible thumbnails for the current window width from the responsive options and
     * scrolls the thumbnails again if it changed. Does nothing without responsive options.
     */
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

    /**
     * Scrolls the thumbnails to the active item after it changed, keeping it in the middle of the visible thumbnails
     * where possible. The scroll is animated when the active item changed.
     * @param previousIndex The index of the item that was active before.
     * @param numVisibleChanged Whether the number of visible thumbnails changed, which requires scrolling even if the
     * active item did not change.
     */
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

    /**
     * Updates the state classes of the thumbnails (current, visible, first and last visible), makes only the visible
     * thumbnails focusable and disables the navigators at the first / last item (without `circular`) or when all
     * thumbnails are visible.
     */
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
