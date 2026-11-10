/**
 * The configuration for the {@link Carousel} widget.
 */
export interface CarouselCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** Index of the first item. */
    page: number;
    /** Number of visible items per page. */
    numVisible: number;
    /** Number of items to scroll. */
    numScroll: number;
    /** An array of options for responsive design. */
    responsiveOptions: PrimeFacesTypes.widget.Carousel.ResponsiveOption[];
    /** Specifies the layout of the component, valid layouts are horizontal or vertical. */
    orientation: PrimeFacesTypes.widget.Carousel.Orientation;
    /** Sets continuous scrolling. */
    circular: boolean;
    /** Whether to display the paginator or not. */
    paginator: boolean;
    /** Sets the time in milliseconds to have Carousel start scrolling automatically after being initialized. */
    autoplayInterval: number;
    /** Client side callback to execute when the page was changed. */
    onPageChange: PrimeFacesTypes.widget.Carousel.OnPageChangeCallback;
}

/**
 * __PrimeFaces Carousel Widget__
 * Carousel is a content slider featuring various customization options.
 */
export class Carousel<Cfg extends CarouselCfg = CarouselCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** Whether autoplay is allowed or not. */
    allowAutoplay: boolean = false;
    /** ARIA label for the page indicators, with the placeholder `{page}`. */
    ariaPageLabel: string = "";
    /** Style element with the custom CSS for the carousel. */
    carouselStyle?: HTMLStyleElement | null;
    /** Whether the viewport is circular or not. */
    circular: boolean = false;
    /** The DOM element for the content of the carousel that shows the carousel. */
    content: JQuery = $();
    /** The DOM element for the container of the carousel that contains items container and buttons. */
    container: JQuery = $();
    /** Callback used to listen to resize events and adjust the carousel accordingly. */
    documentResizeListener?: (() => void) | null;
    /** DOM elements of the `LI` indicator of the carousel. */
    indicators?: JQuery;
    /** The DOM element for the indicators container of the carousel. */
    indicatorsContainer: JQuery = $();
    /** Timeout ID of the timer used for autoplay. */
    interval?: number;
    /** Whether autoplay is allowed or not. */
    isAutoplay: boolean | number = false;
    /** Whether the circular mode is on or not. */
    isCircular: boolean = false;
    /** Whether the remaining items have been added or not. */
    isRemainingItemsAdded: boolean = false;
    /** Whether the viewport is vertical or not. */
    isVertical: boolean = false;
    /** The DOM elements for the carousel items. */
    items: JQuery = $();
    /** The DOM element for the item container of the carousel. */
    itemsContainer: JQuery = $();
    /** The DOM element for the item container of the carousel. */
    itemsContent: JQuery = $();
    /** The number of simultaneously visible items. */
    itemsCount: number = 0;
    /** The DOM element for the button to switch to the next carousel item. */
    nextNav: JQuery = $();
    /** Instant number of how many items will scroll when scrolled. */
    numScroll: number = 0;
    /** Instant number of items visible on the carousel viewport. */
    numVisible: number = 0;
    /** Old number of how many items will scroll when scrolled. */
    oldNumScroll: number = 0;
    /** Old number of items visible on the carousel viewport. */
    oldNumVisible: number = 0;
    /** The currently displayed page of carousel items. */
    page: number = 0;
    /** The DOM element for the button to switch to the previous carousel item. */
    prevNav: JQuery = $();
    /** How many items remaining for the show. */
    remainingItems: number = 0;
    /** The number of indicators currently in the viewport. */
    totalIndicators: number = 0;
    /** The number of how many items shifted. */
    totalShiftedItems: number = 0;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.content = this.jq.children('.p-carousel-content');
        this.container = this.content.children('.p-carousel-container');
        this.itemsContent = this.container.children('.p-carousel-items-content');
        this.indicatorsContainer = this.content.children('.p-carousel-indicators');
        this.itemsContainer = this.itemsContent.children('.p-carousel-items-container');
        this.items = this.itemsContainer.children('.p-carousel-item');
        this.itemsCount = this.items.length;
        this.prevNav = this.container.children('.p-carousel-prev');
        this.nextNav = this.container.children('.p-carousel-next');

        this.cfg.page = this.cfg.page || 0;
        this.cfg.numVisible = this.cfg.numVisible || 1;
        this.cfg.numScroll = this.cfg.numScroll || 1;
        this.cfg.responsiveOptions = this.cfg.responsiveOptions || [];
        this.cfg.orientation = this.cfg.orientation || 'horizontal';
        this.cfg.circular = this.cfg.circular || false;
        this.cfg.autoplayInterval = this.cfg.autoplayInterval || 0;
        this.cfg.paginator = this.cfg.paginator === undefined ? true : this.cfg.paginator;

        this.remainingItems = 0;
        this.isRemainingItemsAdded = false;
        this.numVisible = this.cfg.numVisible;
        this.numScroll = this.cfg.numScroll;
        this.oldNumScroll = 0;
        this.oldNumVisible = 0;
        this.page = this.cfg.page;
        this.totalShiftedItems = this.cfg.page * this.cfg.numScroll * -1;
        this.allowAutoplay = !!this.cfg.autoplayInterval;
        this.circular = this.cfg.circular || this.allowAutoplay;
        this.totalIndicators = this.getTotalIndicators();
        this.isCircular = this.itemsCount !== 0 && this.circular && this.itemsCount >= this.numVisible;
        this.isVertical = this.cfg.orientation === 'vertical';
        this.isAutoplay = this.totalIndicators && this.cfg.autoplayInterval && this.allowAutoplay;

        this.renderDeferred();
    }

    protected override _render(): void {
        this.createStyle();

        if (this.cfg.circular) {
            this.cloneItems();
        }

        this.renderAria();
        this.calculatePosition();
        this.updatePage();
        this.bindEvents();

        if (this.cfg.responsiveOptions) {
            this.bindDocumentListeners();
        }
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this.stopAutoplay();

        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();

        this.stopAutoplay();
        if (this.documentResizeListener) {
            $(window).off('resize', this.documentResizeListener);
            this.documentResizeListener = null;
        }
        if (this.carouselStyle) {
            this.carouselStyle.remove();
            this.carouselStyle = null;
        }
    }

    /**
     * Renders ARIA labels for the carousel navigation elements.
     */
    private renderAria(): void {
        this.ariaPageLabel = this.getAriaLabel('pageLabel');
        this.prevNav.attr('aria-label', this.getAriaLabel('prevPageLabel'));
        this.nextNav.attr('aria-label', this.getAriaLabel('nextPageLabel'));
    }

    /**
     * Sets up all event listeners required by this widget.
     */
    private bindEvents(): void {
        const indicatorSelector = '.p-carousel-indicator';
        this.indicatorsContainer.off('click.indicator', indicatorSelector).on('click.indicator', indicatorSelector, null, (e) => {
            const index = $(e.currentTarget).index();
            this.onIndicatorClick(e, index);
        });
        this.prevNav.on('click', (e) => {
            this.navBackward(e);
        });
        this.nextNav.on('click', (e) => {
            this.navForward(e);
        });
        this.itemsContainer.on('transitionend', () => {
            this.onTransitionEnd();
        });
        if (PrimeFaces.env.isTouchable(this.cfg)) {
            if (this.isVertical) {
                this.itemsContent.swipe({
                    swipeUp: (e) => {
                        this.navForward(e);
                    },
                    swipeDown: (e) => {
                        this.navBackward(e);
                    },
                    excludedElements: PrimeFaces.utils.excludedSwipeElements()
                });
            }
            else {
                this.itemsContent.swipe({
                    swipeLeft: (e) => {
                        this.navForward(e);
                    },
                    swipeRight: (e) => {
                        this.navBackward(e);
                    },
                    excludedElements: PrimeFaces.utils.excludedSwipeElements()
                });
            }
        }
    }

    /**
     * Updates the current page of the carousel.
     */
    private updatePage(): void {
        this.initPageState();
        this.updateNavigators();
        this.updateIndicators();
        this.styleActiveItems();
    }

    /**
     * Initialize current page and variables.
     */
    private initPageState(): void {
        this.totalIndicators = this.getTotalIndicators();
        let stateChanged = false;
        let totalShiftedItems = this.totalShiftedItems;

        if (this.cfg.autoplayInterval) {
            this.stopAutoplay();
        }

        if(this.oldNumScroll !== this.numScroll || this.oldNumVisible !== this.numVisible) {
            this.remainingItems = (this.itemsCount - this.numVisible) % this.numScroll;

            let page = this.page;
            if (this.totalIndicators !== 0 && page >= this.totalIndicators) {
                page = this.totalIndicators - 1;

                this.page = page;

                stateChanged = true;
            }

            totalShiftedItems = (page * this.numScroll) * -1;
            if (this.isCircular) {
                totalShiftedItems -= this.numVisible;
            }

            if (page === (this.totalIndicators - 1) && this.remainingItems > 0) {
                totalShiftedItems += (-1 * this.remainingItems) + this.numScroll;
                this.isRemainingItemsAdded = true;
            }
            else {
                this.isRemainingItemsAdded = false;
            }

            if (totalShiftedItems !== this.totalShiftedItems) {
                this.totalShiftedItems = totalShiftedItems;

                stateChanged = true;
            }

            this.oldNumScroll = this.numScroll;
            this.oldNumVisible = this.numVisible;

            this.changePosition(totalShiftedItems);
        }

        if (this.isCircular) {
            if (this.page === 0) {
                totalShiftedItems = -1 * this.numVisible;
            }
            else if (totalShiftedItems === 0) {
                totalShiftedItems = -1 * this.itemsCount;
                if (this.remainingItems > 0) {
                    this.isRemainingItemsAdded = true;
                }
            }

            if (totalShiftedItems !== this.totalShiftedItems) {
                this.totalShiftedItems = totalShiftedItems;

                stateChanged = true;
            }
        }

        if (!stateChanged && this.isAutoplay) {
            this.startAutoplay();
        }

        if (stateChanged) {
            this.initPageState();
        }
    }

    /**
     * Moves this carousel to the given page.
     * @param dir direction of the move and takes a value of -1 or 1.
     * @param page 0-based index of the page to display.
     */
    step(dir: number, page?: number | null): void {
        let totalShiftedItems = this.totalShiftedItems;
        const isCircular = this.isCircular;

        if (page != null) {
            totalShiftedItems = (this.numScroll * page) * -1;

            if (isCircular) {
                totalShiftedItems -= this.numVisible;
            }

            this.isRemainingItemsAdded = false;
        }
        else {
            totalShiftedItems += (this.numScroll * dir);

            if (this.isRemainingItemsAdded) {
                totalShiftedItems += this.remainingItems - (this.numScroll * dir);
                this.isRemainingItemsAdded = false;
            }

            const originalShiftedItems = isCircular ? (totalShiftedItems + this.numVisible) : totalShiftedItems;
            page = Math.abs(Math.floor(originalShiftedItems / this.numScroll));
        }

        if (isCircular && this.page === (this.totalIndicators - 1) && dir === -1) {
            totalShiftedItems = -1 * (this.itemsCount + this.numVisible);
            page = 0;
        }
        else if (isCircular && this.page === 0 && dir === 1) {
            totalShiftedItems = 0;
            page = (this.totalIndicators - 1);
        }
        else if (page === (this.totalIndicators - 1) && this.remainingItems > 0) {
            totalShiftedItems += ((this.remainingItems * -1) - (this.numScroll * dir));
            this.isRemainingItemsAdded = true;
        }

        if (this.itemsContainer) {
            this.itemsContainer.removeClass('p-items-hidden');
            this.changePosition(totalShiftedItems);
            this.itemsContainer.get(0)!.style.transition = 'transform 500ms ease 0s';
        }

        this.totalShiftedItems = totalShiftedItems;
        this.page = page;

        //Call user onPageChange callback
        if(this.cfg.onPageChange) {
            this.cfg.onPageChange.call(this, page);
        }

        //Call ajax pageChange behaviour
        if(this.hasBehavior('pageChange')) {
            const ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                params: [
                    {name: this.id + '_pageValue', value: page},
                ]
            };

            this.callBehavior('pageChange', ext);
        }

        this.updatePage();
    }

    /**
     * Scrolls the item container based on the total number of shifted items
     * @param totalShiftedItems total number of shifted items.
     */
    private changePosition(totalShiftedItems: number): void {
        if (this.itemsContainer) {
            this.itemsContainer.get(0)!.style.transform = this.isVertical ? 'translate3d(0,' + totalShiftedItems * (100/ this.numVisible) + '%, 0)' : 'translate3d(' + totalShiftedItems * (100/ this.numVisible) + '%, 0, 0)';
        }
    }

    /**
     * Calculates position and visible items and the number of how many items will be scrolled when screen aspect ratio
     * changes then updates current page of the current Carousel widget.
     */
    private calculatePosition(): void {
        if (this.itemsContainer && this.cfg.responsiveOptions) {
            const windowWidth = window.innerWidth;
            let matchedResponsiveOptionsData: { numVisible: number; numScroll: number } = {
                numVisible: this.cfg.numVisible as number,
                numScroll: this.cfg.numScroll as number
            };

            for (let i = 0; i < this.cfg.responsiveOptions.length; i++) {
                const res = this.cfg.responsiveOptions[i] as PrimeFacesTypes.widget.Carousel.ResponsiveOption;

                if (parseInt(res.breakpoint, 10) >= windowWidth) {
                    matchedResponsiveOptionsData = res;
                }
            }

            let stateChanged = false;

            if (this.numScroll !== matchedResponsiveOptionsData.numScroll) {
                let page = this.page;
                page = parseInt(String((page * this.numScroll) / matchedResponsiveOptionsData.numScroll));

                this.totalShiftedItems = (matchedResponsiveOptionsData.numScroll * page) * -1;

                if (this.isCircular) {
                    this.totalShiftedItems -= matchedResponsiveOptionsData.numVisible;
                }

                this.numScroll = matchedResponsiveOptionsData.numScroll;

                this.page = page;
                stateChanged = true;
            }

            if (this.numVisible !== matchedResponsiveOptionsData.numVisible) {
                this.numVisible = matchedResponsiveOptionsData.numVisible;
                stateChanged = true;
            }

            if (stateChanged) {
                this.updatePage();

                if (this.cfg.circular) {
                    this.cloneItems();
                }
            }
        }
    }

    /**
     * Moves this carousel to the previous page. If autoplay is active, it will stop.
     * @param event Event that occurred.
     * @param index 0-based index of the page to display.
     */
    navBackward(event?: Event | JQuery.TriggeredEvent, index?: number): void {
        this.isAutoplay = false;

        if (this.circular || this.page !== 0) {
            this.step(1, index);
        }

        if (event?.cancelable) {
            event.preventDefault();
        }
    }

    /**
     * Moves this carousel to the next page. If autoplay is active, it will stop.
     * @param event Event that occurred.
     * @param index 0-based index of the page to display.
     */
    navForward(event?: Event | JQuery.TriggeredEvent, index?: number): void {
        this.isAutoplay = false;

        if (this.circular || this.page < (this.totalIndicators - 1)) {
            this.step(-1, index);
        }

        if (event?.cancelable) {
            event.preventDefault();
        }
    }

    /**
     * Update styles of the navigator buttons.
     */
    private updateNavigators(): void {
        const prevButton = this.prevNav,
            nextButton = this.nextNav;

        this.backwardIsDisabled()
            ? PrimeFaces.utils.disableButton(prevButton)
            : PrimeFaces.utils.enableButton(prevButton);

        this.forwardIsDisabled()
            ? PrimeFaces.utils.disableButton(nextButton)
            : PrimeFaces.utils.enableButton(nextButton);
    }

    /**
     * Render the indicators based on the current page state.
     */
    private updateIndicators(): void {
        this.indicatorsContainer.get(0)!.innerHTML = this.renderIndicators();
        this.indicators = this.indicatorsContainer.children('li');
    }

    /**
     * It moves the current Carousel to the index of the clicked indicator on that Carousel viewport.
     * @param event Event that occurred.
     * @param index 0-based index of the indicator.
     */
    private onIndicatorClick(event: Event | JQuery.TriggeredEvent, index: number): void {
        const page = this.page;

        if (index > page) {
            this.navForward(event, index);
        }
        else if (index < page) {
            this.navBackward(event, index);
        }
    }

    /**
     * Changes current page according to the state of the page when the transition ends.
     */
    private onTransitionEnd(): void {
        if (this.itemsContainer) {
            this.itemsContainer.addClass('p-items-hidden');
            this.itemsContainer.get(0)!.style.transition = '';

            if ((this.page === 0 || this.page === (this.totalIndicators - 1)) && this.isCircular) {
                this.changePosition(this.totalShiftedItems);
            }
        }
    }

    /**
     * Adds the resize event listener to the window.
     */
    private bindDocumentListeners(): void {
        if (!this.documentResizeListener) {
            this.documentResizeListener = () => {
                this.calculatePosition();
            };

            $(window).on('resize', this.documentResizeListener);
        }
    }

    /**
     * Enables autoplay and starts the slideshow.
     */
    startAutoplay(): void {
        this.interval = setInterval(() => {
                if (this.page === (this.totalIndicators - 1)) {
                    this.step(-1, 0);
                }
                else {
                    this.step(-1, this.page + 1);
                }
            },
            this.cfg.autoplayInterval);
    }

    /**
     * Disables autoplay and stops the slideshow.
     */
    stopAutoplay(): void {
        if (this.interval) {
            clearInterval(this.interval);
        }
    }

    /**
     * Creates responsive styles of the carousel container.
     */
    private createStyle(): void {
        if (!this.carouselStyle) {
            this.carouselStyle = document.createElement('style');
            this.carouselStyle.type = 'text/css';
            document.body.appendChild(this.carouselStyle);
        }

        let innerHTML = 'div[id*="' + this.id + '"] .p-carousel-item {flex: 1 0 ' + (100/ this.numVisible) + '%}';

        if (this.cfg.responsiveOptions) {
            const _responsiveOptions = this.cfg.responsiveOptions;
            _responsiveOptions.sort((data1, data2) => {
                const value1 = data1.breakpoint;
                const value2 = data2.breakpoint;
                let result: number | null = null;

                if (value1 == null && value2 != null)
                    result = -1;
                else if (value1 != null && value2 == null)
                    result = 1;
                else if (value1 == null && value2 == null)
                    result = 0;
                else if (typeof value1 === 'string' && typeof value2 === 'string')
                    result = value1.localeCompare(value2, undefined, { numeric: true });
                else {
                    if (value1 < value2) {
                        result = -1;
                    }
                    else if (value1 > value2) {
                        result = 1;
                    }
                    else {
                        result = 0;
                    }
                }

                return -1 * result;
            });

            for (let i = 0; i < _responsiveOptions.length; i++) {
                const res = _responsiveOptions[i] as PrimeFacesTypes.widget.Carousel.ResponsiveOption;

                innerHTML += '@media screen and (max-width: ' + res.breakpoint + ') '
                    + '{div[id*="' + this.id + '"] .p-carousel-item '
                    + '{flex: 1 0 ' + (100/ res.numVisible) + '%}}'
            }
        }

        this.carouselStyle.innerHTML = innerHTML;
    }

    /**
     * Clones items if the carousel widget is circular
     */
    private cloneItems(): void {
        this.itemsContainer.children('.p-carousel-item-cloned').remove();

        let clonedElements = this.items.slice(-1 * this.numVisible).clone();
        let cloneSize = clonedElements.length;
        let i;
        for (i = 0; i < cloneSize; i++) {
            this.styleClone(clonedElements.eq(i), i, cloneSize);
        }
        this.itemsContainer.prepend(clonedElements);

        clonedElements = this.items.slice(0, this.numVisible).clone();
        cloneSize = clonedElements.length;
        for (i = 0; i < cloneSize; i++) {
            this.styleClone(clonedElements.eq(i), i, cloneSize);
        }
        this.itemsContainer.append(clonedElements);
    }

    /**
     * Applies styles to the clones
     * @param element cloned dom element of the item
     * @param index index of the element
     * @param length length of the clones
     */
    private styleClone(element: JQuery, index: number, length: number): void {
        element.removeClass('p-carousel-item-start p-carousel-item-end');
        element.addClass('p-carousel-item-cloned p-carousel-item-active');
        element.prop('inert', true);
        if (index === 0) {
            element.addClass('p-carousel-item-start');
        }
        if (index + 1 === length) {
            element.addClass('p-carousel-item-end');
        }
        element.find('*').removeAttr('id');
    }

    /**
     * Styles visible items
     */
    private styleActiveItems(): void {
        const items = this.itemsContainer.children(':not(.p-carousel-item-cloned)');
        items.removeClass('p-carousel-item-active p-carousel-item-start p-carousel-item-end');

        const firstIndex = this.firstIndex(),
            lastIndex = this.lastIndex();

        for (let i = 0; i < items.length; i++) {
            items.eq(i).prop('inert', i < firstIndex || i > lastIndex);
            if (firstIndex <= i && lastIndex >= i) {
                items.eq(i).addClass('p-carousel-item-active');
            }

            if (firstIndex === i) {
                items.eq(i).addClass('p-carousel-item-start');
            }

            if (lastIndex === i) {
                items.eq(i).addClass('p-carousel-item-end');
            }
        }
    }

    /**
     * Retrieves the indicators html of the carousel.
     * @returns html of the indicators container.
     */
    private renderIndicators(): string {
        let indicatorsHtml = '';

        if (this.cfg.paginator) {
            for (let i = 0; i < this.totalIndicators; i++) {
                indicatorsHtml += '<li class="p-carousel-indicator ' + (this.page === i ? 'p-state-highlight' : '') + '"><button class="p-link" type="button" aria-label="' + this.ariaPageLabel.replace('{page}', String(i+1)) + '"></button></li>';
            }
        }

        return indicatorsHtml;
    }

    /**
     * Retrieves the total number of the indicators floor to 0 so it can't be negative.
     * @returns total number of the indicators.
     */
    private getTotalIndicators(): number {
        return Math.max(Math.ceil((this.itemsCount - this.numVisible) / this.numScroll) + 1, 0);
    }

    /**
     * Retrieves whether the backward button is disabled.
     * @returns backward button is disabled.
     */
    private backwardIsDisabled(): boolean {
        return (this.itemsCount !== 0 && (!this.cfg.circular || this.itemsCount < this.numVisible) && this.page === 0);
    }

    /**
     * Retrieves whether the forward button is disabled.
     * @returns forward button is disabled.
     */
    private forwardIsDisabled(): boolean {
        return (this.itemsCount !== 0 && (!this.cfg.circular || this.itemsCount < this.numVisible) && (this.page === (this.totalIndicators - 1) || this.totalIndicators === 0));
    }

    /**
     * Retrieves the first index of visible items.
     * @returns first index of the visible items.
     */
    private firstIndex(): number {
        return this.isCircular ? (-1 * (this.totalShiftedItems + this.numVisible)) : (this.totalShiftedItems * -1);
    }

    /**
     * Retrieves the last index of visible items.
     * @returns last index of the visible items.
     */
    private lastIndex(): number {
        return (this.firstIndex() + this.numVisible - 1);
    }

}
