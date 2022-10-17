/**
 * __PrimeFaces Carousel Widget__
 * Carousel is a content slider featuring various customization options.
 *
 * @prop {boolean} allowAutoplay Whether autoplay is allowed or not.
 * @prop {HTMLStyleElement} carouselStyle Style element with the custom CSS for the carousel. 
 * @prop {boolean} circular Whether the viewport is circular or not.
 * @prop {JQuery} content The DOM element for the content of the carousel that shows the carousel.
 * @prop {JQuery} container The DOM element for the container of the carousel that contains items container and buttons.
 * @prop {((event: UIEvent) => void) | undefined} documentResizeListener Callback used to listen to resize events and
 * adjust the carousel accordingly.
 * @prop {JQuery} [indicators] DOM elements of the `LI` indicator of the carousel.
 * @prop {JQuery} indicatorsContainer The DOM element for the indicators container of the carousel.
 * @prop {number} [interval] Timeout ID of the timer used for autoplay.
 * @prop {boolean} isAutoplay Whether autoplay is allowed or not.
 * @prop {boolean} isCircular Whether the circular mode is on or not.
 * @prop {boolean} isRemainingItemsAdded Whether the remaining items have been added or not.
 * @prop {boolean} isVertical Whether the viewport is vertical or not.
 * @prop {JQuery} items The DOM elements for the carousel items.
 * @prop {JQuery} itemsContainer The DOM element for the item container of the carousel.
 * @prop {JQuery} itemsContent The DOM element for the item container of the carousel.
 * @prop {number} itemsCount The number of simultaneously visible items.
 * @prop {JQuery} nextNav The DOM element for the button to switch to the previous carousel item.
 * @prop {number} numScroll Instant number of how many items will scroll when scrolled.
 * @prop {number} numVisible Instant number of items visible on the carousel viewport.
 * @prop {number} oldNumScroll Old number of items visible on the carousel viewport.
 * @prop {number} oldNumVisible Old number of how many items will scroll when scrolled.
 * @prop {number} page The currently displayed page of carousel items.
 * @prop {JQuery} prevNav The DOM element for the button to switch to the next carousel item.
 * @prop {number} remainingItems How many items remaining for the show.
 * @prop {number} totalIndicators The number of indicators currently in the viewport.
 * @prop {number} totalShiftedItems The number of how many items shifted.
 *
 * @interface {PrimeFaces.widget.CarouselCfg} cfg The configuration for the {@link  Carousel| Carousel widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {number} cfg.page Index of the first item.
 * @prop {number} cfg.numVisible Number of visible items per page
 * @prop {number} cfg.numScroll Number of items to scroll
 * @prop {{breakpoint:string, numVisible:number, numScroll:number}[]} cfg.responsiveOptions An array of options for responsive design
 * @prop {string} cfg.orientation Specifies the layout of the component, valid layouts are horizontal or vertical
 * @prop {boolean} cfg.circular Sets continuous scrolling
 * @prop {boolean} cfg.paginator Whether to display the paginator or not.
 * @prop {number} cfg.autoplayInterval Sets the time in milliseconds to have Carousel start scrolling automatically
 * after being initialized.
 *
 */
PrimeFaces.widget.Carousel = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.content = this.jq.children('.ui-carousel-content');
        this.container = this.content.children('.ui-carousel-container');
        this.itemsContent = this.container.children('.ui-carousel-items-content');
        this.indicatorsContainer = this.content.children('.ui-carousel-indicators');
        this.itemsContainer = this.itemsContent.children('.ui-carousel-items-container');
        this.items = this.itemsContainer.children('.ui-carousel-item');
        this.itemsCount = this.items.length;
        this.prevNav = this.container.children('.ui-carousel-prev');
        this.nextNav = this.container.children('.ui-carousel-next');

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
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.createStyle();

        if (this.cfg.circular) {
            this.cloneItems();
        }

        this.calculatePosition();
        this.updatePage();
        this.bindEvents();

        if (this.cfg.responsiveOptions) {
            this.bindDocumentListeners();
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.stopAutoplay();

        this._super(cfg);
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function () {
        var $this = this;

        var indicatorSelector = '.ui-carousel-indicator';
        this.indicatorsContainer.off('click.indicator', indicatorSelector).on('click.indicator', indicatorSelector, null, function (e) {
            var index = $(this).index();
            $this.onIndicatorClick(e, index);
        });
        this.prevNav.on('click', function(e) {
            $this.navBackward(e);
        });
        this.nextNav.on('click', function(e) {
            $this.navForward(e);
        });
        this.itemsContainer.on('transitionend', function() {
            $this.onTransitionEnd();
        });
        if (PrimeFaces.env.isTouchable(this.cfg)) {
            if (this.isVertical) {
                this.itemsContent.swipe({
                    swipeUp:function(e) {
                        $this.navForward(e);
                    },
                    swipeDown: function(e) {
                        $this.navBackward(e);
                    },
                    excludedElements: PrimeFaces.utils.excludedSwipeElements()
                });
            }
            else {
                this.itemsContent.swipe({
                    swipeLeft:function(e) {
                        $this.navForward(e);
                    },
                    swipeRight: function(e) {
                        $this.navBackward(e);
                    },
                    excludedElements: PrimeFaces.utils.excludedSwipeElements()
                });
            }
        }
    },

    /**
     * Updates the current page of the carousel.
     * @private
     */
    updatePage: function() {
        this.initPageState();
        this.updateNavigators();
        this.updateIndicators();
        this.styleActiveItems();
    },

    /**
     * Initialize current page and variables.
     * @private
     */
    initPageState: function() {
        this.totalIndicators = this.getTotalIndicators();
        var stateChanged = false;
        var totalShiftedItems = this.totalShiftedItems;

        if (this.cfg.autoplayInterval) {
            this.stopAutoplay();
        }

        if(this.oldNumScroll !== this.numScroll || this.oldNumVisible !== this.numVisible) {
            this.remainingItems = (this.itemsCount - this.numVisible) % this.numScroll;

            var page = this.page;
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
    },

    /**
     * Moves this carousel to the given page.
     * @param {number} dir direction of the move and takes a value of -1 or 1.
     * @param {number} page 0-based index of the page to display.
     */
    step: function(dir, page) {
        var totalShiftedItems = this.totalShiftedItems;
        var isCircular = this.isCircular;

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

            var originalShiftedItems = isCircular ? (totalShiftedItems + this.numVisible) : totalShiftedItems;
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
            this.itemsContainer.removeClass('ui-items-hidden');
            this.changePosition(totalShiftedItems);
            this.itemsContainer.get(0).style.transition = 'transform 500ms ease 0s';
        }

        this.totalShiftedItems = totalShiftedItems;
        this.page = page;

        //Call user onPageChange callback
        if(this.cfg.onPageChange) {
            this.cfg.onPageChange.call(this, page);
        }

        //Call ajax pageChange behaviour
        if(this.hasBehavior('pageChange')) {
            var ext = {
                params: [
                    {name: this.id + '_pageValue', value: page},
                ]
            };

            this.callBehavior('pageChange', ext);
        }

        this.updatePage();
    },

    /**
     * Scrolls the item container based on the total number of shifted items
     * @param {number} totalShiftedItems total number of shifted items.
     * @private
     */
    changePosition: function(totalShiftedItems) {
        if (this.itemsContainer) {
            this.itemsContainer.get(0).style.transform = this.isVertical ? 'translate3d(0,' + totalShiftedItems * (100/ this.numVisible) + '%, 0)' : 'translate3d(' + totalShiftedItems * (100/ this.numVisible) + '%, 0, 0)';
        }
    },

    /**
     * Calculates position and visible items and the number of how many items will be scrolled when screen aspect ratio
     * changes then updates current page of the current Carousel widget.
     * @private
     */
    calculatePosition: function() {
        var $this = this;

        if (this.itemsContainer && this.cfg.responsiveOptions) {
            var windowWidth = window.innerWidth;
            var matchedResponsiveOptionsData = {
                numVisible: $this.cfg.numVisible,
                numScroll: $this.cfg.numScroll
            };

            for (var i = 0; i < this.cfg.responsiveOptions.length; i++) {
                var res = this.cfg.responsiveOptions[i];

                if (parseInt(res.breakpoint, 10) >= windowWidth) {
                    matchedResponsiveOptionsData = res;
                }
            }

            var stateChanged = false;

            if (this.numScroll !== matchedResponsiveOptionsData.numScroll) {
                var page = this.page;
                page = parseInt((page * this.numScroll) / matchedResponsiveOptionsData.numScroll);

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
    },

    /**
     * Moves this carousel to the previous page. If autoplay is active, it will stop.
     * @param {Event} event Event that occurred.
     * @param {number} index 0-based index of the page to display.
     */
    navBackward: function(event, index){
        this.isAutoplay = false;

        if (this.circular || this.page !== 0) {
            this.step(1, index);
        }

        if (event.cancelable) {
            event.preventDefault();
        }
    },

    /**
     * Moves this carousel to the next page. If autoplay is active, it will stop.
     * @param {Event} event Event that occurred.
     * @param {number} index 0-based index of the page to display.
     */
    navForward: function(event, index){
        this.isAutoplay = false;

        if (this.circular || this.page < (this.totalIndicators - 1)) {
            this.step(-1, index);
        }

        if (event.cancelable) {
            event.preventDefault();
        }
    },

    /**
     * Update styles of the navigator buttons.
     * @private
     */
    updateNavigators: function() {
        var prevButton = this.prevNav,
            nextButton = this.nextNav;

        this.backwardIsDisabled()
            ? PrimeFaces.utils.disableButton(prevButton)
            : PrimeFaces.utils.enableButton(prevButton);

        this.forwardIsDisabled()
            ? PrimeFaces.utils.disableButton(nextButton)
            : PrimeFaces.utils.enableButton(nextButton);
    },

    /**
     * Render the indicators based on the current page state.
     * @private
     */
    updateIndicators: function() {
        this.indicatorsContainer.get(0).innerHTML = this.renderIndicators();
        this.indicators = this.indicatorsContainer.children('li');
    },

    /**
     * It moves the current Carousel to the index of the clicked indicator on that Carousel viewport.
     * @private
     * @param {Event} event Event that occurred.
     * @param {number} index 0-based index of the indicator.
     */
    onIndicatorClick: function(event, index) {
        var page = this.page;

        if (index > page) {
            this.navForward(event, index);
        }
        else if (index < page) {
            this.navBackward(event, index);
        }
    },

    /**
     * Changes current page according to the state of the page when the transition ends.
     * @private
     */
    onTransitionEnd: function() {
        if (this.itemsContainer) {
            this.itemsContainer.addClass('ui-items-hidden');
            this.itemsContainer.get(0).style.transition = '';

            if ((this.page === 0 || this.page === (this.totalIndicators - 1)) && this.isCircular) {
                this.changePosition(this.totalShiftedItems);
            }
        }
    },

    /**
     * Adds the resize event listener to the window.
     * @private
     */
    bindDocumentListeners: function() {
        var $this = this;

        if (!this.documentResizeListener) {
            this.documentResizeListener = function () {
                $this.calculatePosition();
            };

            $(window).on('resize', this.documentResizeListener);
        }
    },

    /**
     * Enables autoplay and starts the slideshow.
     */
    startAutoplay: function() {
        var $this = this;
        this.interval = setInterval(function () {
                if ($this.page === ($this.totalIndicators - 1)) {
                    $this.step(-1, 0);
                }
                else {
                    $this.step(-1, $this.page + 1);
                }
            },
            this.cfg.autoplayInterval);
    },

    /**
     * Disables autoplay and stops the slideshow.
     */
    stopAutoplay: function() {
        if (this.interval) {
            clearInterval(this.interval);
        }
    },

    /**
     * Creates responsive styles of the carousel container.
     * @private
     */
    createStyle: function() {
        if (!this.carouselStyle) {
            this.carouselStyle = document.createElement('style');
            this.carouselStyle.type = 'text/css';
            document.body.appendChild(this.carouselStyle);
        }

        var innerHTML = 'div[id*="' + this.id + '"] .ui-carousel-item {flex: 1 0 ' + (100/ this.numVisible) + '%}';

        if (this.cfg.responsiveOptions) {
            var _responsiveOptions = this.cfg.responsiveOptions;
            _responsiveOptions.sort(function (data1, data2) {
                var value1 = data1.breakpoint;
                var value2 = data2.breakpoint;
                var result = null;

                if (value1 == null && value2 != null)
                    result = -1;
                else if (value1 != null && value2 == null)
                    result = 1;
                else if (value1 == null && value2 == null)
                    result = 0;
                else if (typeof value1 === 'string' && typeof value2 === 'string')
                    result = value1.localeCompare(value2, undefined, { numeric: true });
                else
                    result = (value1 < value2) ? -1 : (value1 > value2) ? 1 : 0;

                return -1 * result;
            });

            for (var i = 0; i < _responsiveOptions.length; i++) {
                var res = _responsiveOptions[i];

                innerHTML += '@media screen and (max-width: ' + res.breakpoint + ') '
                    + '{div[id*="' + this.id + '"] .ui-carousel-item '
                    + '{flex: 1 0 ' + (100/ res.numVisible) + '%}}'
            }
        }

        this.carouselStyle.innerHTML = innerHTML;
    },

    /**
     * Clones items if the carousel widget is circular
     * @private
     */
    cloneItems: function () {
        this.itemsContainer.children('.ui-carousel-item-cloned').remove();

        var clonedElements = this.items.slice(-1 * this.numVisible).clone();
        var cloneSize = clonedElements.length;
        var i;
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
    },

    /**
     * Applies styles to the clones
     * @private
     * @param {JQuery} element cloned dom element of the item
     * @param {number} index index of the element
     * @param {number} length length of the clones
     */
    styleClone: function (element, index, length) {
        element.removeClass('ui-carousel-item-start ui-carousel-item-end');
        element.addClass('ui-carousel-item-cloned ui-carousel-item-active');
        if (index === 0) {
            element.addClass('ui-carousel-item-start');
        }
        if (index + 1 === length) {
            element.addClass('ui-carousel-item-end');
        }
        element.find('*').removeAttr('id');
    },

    /**
     * Styles visible items
     * @private
     */
    styleActiveItems: function () {
        var items = this.itemsContainer.children(':not(.ui-carousel-item-cloned)');
        items.removeClass('ui-carousel-item-active ui-carousel-item-start ui-carousel-item-end');

        var firstIndex = this.firstIndex(),
            lastIndex = this.lastIndex();

        for (var i = 0; i < items.length; i++) {
            if (firstIndex <= i && lastIndex >= i) {
                items.eq(i).addClass('ui-carousel-item-active');
            }

            if (firstIndex === i) {
                items.eq(i).addClass('ui-carousel-item-start');
            }

            if (lastIndex === i) {
                items.eq(i).addClass('ui-carousel-item-end');
            }
        }
    },

    /**
     * Retrieves the indicators html of the carousel.
     * @return {string} html of the indicators container.
     * @private
     */
    renderIndicators: function() {
        var indicatorsHtml = '';

        if (this.cfg.paginator) {
            for (var i = 0; i < this.totalIndicators; i++) {
                indicatorsHtml += '<li class="ui-carousel-indicator ' + (this.page === i ? 'ui-state-highlight' : '') + '"><button class="ui-link" type="button"></button></li>';
            }
        }
        
        return indicatorsHtml;
    },

    /**
     * Retrieves the total number of the indicators floor to 0 so it can't be negative.
     * @private
     * @return {number} total number of the indicators.
     */
    getTotalIndicators: function() {
        return Math.max(Math.ceil((this.itemsCount - this.numVisible) / this.numScroll) + 1, 0);
    },

    /**
     * Retrieves whether the backward button is disabled.
     * @private
     * @return {boolean} backward button is disabled.
     */
    backwardIsDisabled: function() {
        return (this.itemsCount !== 0 && (!this.cfg.circular || this.itemsCount < this.numVisible) && this.page === 0);
    },

    /**
     * Retrieves whether the forward button is disabled.
     * @private
     * @return {boolean} forward button is disabled.
     */
    forwardIsDisabled: function() {
        return (this.itemsCount !== 0 && (!this.cfg.circular || this.itemsCount < this.numVisible) && (this.page === (this.totalIndicators - 1) || this.totalIndicators === 0));
    },

    /**
     * Retrieves the first index of visible items.
     * @private
     * @return {number} first index of the visible items.
     */
    firstIndex: function() {
        return this.isCircular ? (-1 * (this.totalShiftedItems + this.numVisible)) : (this.totalShiftedItems * -1);
    },

    /**
     * Retrieves the last index of visible items.
     * @private
     * @return {number} last index of the visible items.
     */
    lastIndex: function() {
        return (this.firstIndex() + this.numVisible - 1);
    }

});
