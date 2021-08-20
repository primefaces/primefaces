/**
 * __PrimeFaces Carousel Widget__
 * Carousel is a multi purpose component to display a set of data or general content with slide effects.
 *
 * @interface {PrimeFaces.widget.CarouselCfg} cfg The configuration for the {@link  Carousel| Carousel widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {number} cfg.page Index of the first item.
 * @prop {boolean} cfg.circular Sets continuous scrolling
 * @prop {number} cfg.autoplayInterval Sets the time in milliseconds to have Carousel start scrolling automatically
 * after being initialized.
 * @prop {number} cfg.numVisible Number of visible items per page
 * @prop {number} cfg.numScroll Number of items to scroll
 * @prop {Array.<{breakpoint:string, numVisible:number, numScroll:number}>} an array of options for responsive design
 * @prop {string} cfg.orientation Specifies the layout of the component, valid layouts are horizontal or vertical
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
        this.itemsContainer = this.itemsContent.children('.ui-carousel-items-container');
        this.items = this.itemsContainer.children('div');
        this.itemsCount = this.items.length;
        this.prevNav = this.container.children('.ui-carousel-prev');
        this.nextNav = this.container.children('.ui-carousel-next');
        this.indicatorsContainer = this.content.children('.ui-carousel-indicators');
        this.indicators = this.content.find('li');
        this.indicatorCount = this.indicators.length;

        debugger;

        this.cfg.page = this.cfg.page || 0;
        this.cfg.numVisible = this.cfg.numVisible || 1;
        this.cfg.numScroll = this.cfg.numScroll || 1;
        this.cfg.responsiveOptions = this.cfg.responsiveOptions || [];
        this.cfg.orientation = this.cfg.orientation || 'horizontal';
        this.cfg.circular = this.cfg.circular || false;
        this.cfg.autoplayInterval = this.cfg.autoplayInterval || 0;

        this.remainingItems = 0;
        this.isRemainingItemsAdded = false;
        this.d_numVisible = this.cfg.numVisible;
        this.d_numScroll = this.cfg.numScroll;
        this.d_oldNumScroll = 0;
        this.d_oldNumVisible = 0;
        this.d_page = this.cfg.page;
        this.totalShiftedItems = this.cfg.page * this.cfg.numScroll * -1;
        this.allowAutoplay = !!this.cfg.autoplayInterval;
        this.d_circular = this.cfg.circular || this.allowAutoplay;
        this.swipeThreshold = 20;
        this.totalIndicators = this.totalIndicators();
        this.isCircular = this.itemsCount !== 0 && this.d_circular && this.itemsCount >= this.d_numVisible;
        this.isVertical = this.cfg.orientation === 'vertical';
        this.isAutoplay = this.cfg.autoplayInterval && this.allowAutoplay;

        this.renderDeferred();
    },

    update: function() {
        var stateChanged = false;
        var totalShiftedItems = this.totalShiftedItems;

        if (this.autoplayInterval) {
            this.stopAutoplay();
        }

        if(this.d_oldNumScroll !== this.d_numScroll || this.d_oldNumVisible !== this.d_numVisible ) {
            this.remainingItems = (this.itemsCount - this.d_numVisible) % this.d_numScroll;

            var page = this.d_page;
            if (this.totalIndicators !== 0 && page >= this.totalIndicators) {
                page = this.totalIndicators - 1;

                this.d_page = page;

                stateChanged = true;
            }

            totalShiftedItems = (page * this.d_numScroll) * -1;
            if (this.isCircular) {
                totalShiftedItems -= this.d_numVisible;
            }

            if (page === (this.totalIndicators - 1) && this.remainingItems > 0) {
                totalShiftedItems += (-1 * this.remainingItems) + this.d_numScroll;
                this.isRemainingItemsAdded = true;
            }
            else {
                this.isRemainingItemsAdded = false;
            }

            if (totalShiftedItems !== this.totalShiftedItems) {
                this.totalShiftedItems = totalShiftedItems;

                stateChanged = true;
            }

            this.d_oldNumScroll = this.d_numScroll;
            this.d_oldNumVisible = this.d_numVisible;

            this.changePosition(totalShiftedItems);
        }

        if (this.isCircular) {
            if (this.d_page === 0) {
                totalShiftedItems = -1 * this.d_numVisible;
            }
            else if (totalShiftedItems === 0) {
                totalShiftedItems = -1 * this.value.length;
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

        this.updateIndicators();

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
     * @include
     * @override
     * @inheritdoc
     * @protected
     */
    _render: function() {
        this.createStyle();
        this.calculatePosition();
        this.update();
        this.changePosition();
        this.bindEvents();

        if (this.cfg.responsiveOptions) {
            this.bindDocumentListeners();
        }
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function () {
        var $this = this;

        this.indicators.on('click', function(e) {
            var index = $(this).data('index');
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
            this.itemsContainer.swipe({
                swipeLeft:function(e) {
                    $this.navBackward(e);
                },
                swipeRight: function(e) {
                    $this.navForward(e);
                },
                excludedElements: PrimeFaces.utils.excludedSwipeElements()
            });
        }
    },

    changePosition: function(totalShiftedItems) {
        if (this.itemsContainer) {
            this.itemsContainer.get(0).style.transform = this.isVertical ? `translate3d(0, ${totalShiftedItems * (100/ this.d_numVisible)}%, 0)` : `translate3d(${totalShiftedItems * (100/ this.d_numVisible)}%, 0, 0)`;
        }
    },

    step: function(dir, page) {
        var totalShiftedItems = this.totalShiftedItems;
        var isCircular = this.isCircular;

        if (page != null) {
            totalShiftedItems = (this.d_numScroll * page) * -1;

            if (isCircular) {
                totalShiftedItems -= this.d_numVisible;
            }

            this.isRemainingItemsAdded = false;
        }
        else {
            totalShiftedItems += (this.d_numScroll * dir);

            if (this.isRemainingItemsAdded) {
                totalShiftedItems += this.remainingItems - (this.d_numScroll * dir);
                this.isRemainingItemsAdded = false;
            }

            var originalShiftedItems = isCircular ? (totalShiftedItems + this.d_numVisible) : totalShiftedItems;
            page = Math.abs(Math.floor(originalShiftedItems / this.d_numScroll));
        }

        if (isCircular && this.d_page === (this.totalIndicators - 1) && dir === -1) {
            totalShiftedItems = -1 * (this.itemsCount + this.d_numVisible);
            page = 0;
        }
        else if (isCircular && this.d_page === 0 && dir === 1) {
            totalShiftedItems = 0;
            page = (this.totalIndicators - 1);
        }
        else if (page === (this.totalIndicators - 1) && this.remainingItems > 0) {
            totalShiftedItems += ((this.remainingItems * -1) - (this.d_numScroll * dir));
            this.isRemainingItemsAdded = true;
        }

        if (this.itemsContainer) {
            this.itemsContainer.removeClass('ui-items-hidden');
            this.changePosition(this.totalShiftedItems);
            this.itemsContainer.get(0).style.transition = 'transform 500ms ease 0s';
        }

        this.totalShiftedItems = totalShiftedItems;
        this.d_page = page;

        this.update();
    },

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

            if (this.d_numScroll !== matchedResponsiveOptionsData.numScroll) {
                var page = this.d_page;
                page = parseInt((page * this.d_numScroll) / matchedResponsiveOptionsData.numScroll);

                this.totalShiftedItems = (matchedResponsiveOptionsData.numScroll * page) * -1;

                if (this.isCircular) {
                    this.totalShiftedItems -= matchedResponsiveOptionsData.numVisible;
                }

                this.d_numScroll = matchedResponsiveOptionsData.numScroll;

                this.d_page = page;
            }

            if (this.d_numVisible !== matchedResponsiveOptionsData.numVisible) {
                this.d_numVisible = matchedResponsiveOptionsData.numVisible;
            }
        }
    },

    navBackward: function(e, index){
        if (this.d_circular || this.d_page !== 0) {
            this.step(1, index);
        }

        if (this.cfg.autoplayInterval) {
            this.stopAutoplay();
        }

        this.updateNavigators();

        if (e.cancelable) {
            e.preventDefault();
        }
    },

    navForward: function(e, index){
        if (this.d_circular || this.d_page < (this.totalIndicators - 1)) {
            this.step(-1, index);
        }

        if (this.cfg.autoplayInterval) {
            this.stopAutoplay();
        }

        this.updateNavigators();

        if (e.cancelable) {
            e.preventDefault();
        }
    },

    /**
     * Updates the navigators to reflect the current page.
     * @private
     */
    updateNavigators: function() {
        var backwardIsDisabled = this.backwardIsDisabled();
        var forwardIsDisabled = this.forwardIsDisabled();

        if (backwardIsDisabled) {
            this.prevNav.addClass('ui-state-disabled');
            this.prevNav.prop('disabled', true);
        }
        else {
            this.prevNav.removeClass('ui-state-disabled');
            this.prevNav.prop('disabled', false);
        }

        if (forwardIsDisabled) {
            this.nextNav.addClass('ui-state-disabled');
            this.nextNav.prop('disabled', true);
        }
        else {
            this.nextNav.removeClass('ui-state-disabled');
            this.nextNav.prop('disabled', false);
        }
    },

    updateIndicators: function() {
        this.indicators.removeClass('ui-highlight');
        this.indicators.eq(this.d_page).addClass('ui-highlight');
    },

    onIndicatorClick: function(e, index) {
        var page = this.d_page;

        if (index > page) {
            this.navForward(e, index);
        }
        else if (index < page) {
            this.navBackward(e, index);
        }
    },

    onTransitionEnd: function() {
        if (this.itemsContainer) {
            this.itemsContainer.addClass('ui-items-hidden');
            this.itemsContainer.get(0).style.transition = '';

            if ((this.d_page === 0 || this.d_page === (this.totalIndicators - 1)) && this.isCircular) {
                this.changePosition(this.totalShiftedItems)
            }
        }
    },

    bindDocumentListeners: function() {
        if (!this.documentResizeListener) {
            this.documentResizeListener = (e) => {
                this.calculatePosition(e);
            };

            window.addEventListener('resize', this.documentResizeListener);
        }
    },

    startAutoplay: function() {
        var $this = this;
        this.interval = setInterval(() => {
                if($this.d_page === ($this.totalIndicators - 1)) {
                    $this.step(-1, 0);
                }
                else {
                    $this.step(-1, this.d_page + 1);
                }
            },
            this.cfg.autoplayInterval);
    },

    stopAutoplay: function() {
        if (this.interval) {
            clearInterval(this.interval);
        }
    },

    createStyle: function() {
        if (!this.carouselStyle) {
            this.carouselStyle = document.createElement('style');
            this.carouselStyle.type = 'text/css';
            document.body.appendChild(this.carouselStyle);
        }

        var innerHTML = `
            div[id*="${this.id}"] .ui-carousel-item {
                flex: 1 0 ${ (100/ this.d_numVisible) }%
            }
        `;

        if (this.cfg.responsiveOptions) {
            var _responsiveOptions = this.cfg.responsiveOptions;
            _responsiveOptions.sort((data1, data2) => {
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

                innerHTML += `
                    @media screen and (max-width: ${res.breakpoint}) {
                        div[id*="${this.id}"] .ui-carousel-item {
                            flex: 1 0 ${ (100/ res.numVisible) }%
                        }
                    }
                `
            }
        }

        if (this.cfg.circular) {
            this.cloneItems();
            this.startAutoplay();
        }

        this.carouselStyle.innerHTML = innerHTML;
    },

    cloneItems: function () {
        this.items.find('ui-carousel-item-cloned').remove();
        var cloned = this.items.slice(-1 * this.d_numVisible).clone();
        var cloneSize = cloned.length;
        var i;
        for (i = 0; i < cloneSize; i++) {
            this.styleClone(cloned.eq(i), i, cloneSize);
        }
        this.itemsContainer.prepend(cloned);

        cloned = this.items.slice(0, this.d_numVisible).clone();
        cloneSize = cloned.length;
        for (i = 0; i < cloned.length; i++) {
            this.styleClone(cloned.eq(i), i, cloneSize);
        }
        this.itemsContainer.append(cloned);
    },

    styleClone: function (elem, index, length) {
        elem.removeClass('ui-carousel-item-start ui-carousel-item-end');
        elem.addClass('ui-carousel-item-cloned ui-carousel-item-active');
        if (index === 0) {
            elem.addClass('ui-carousel-item-start');
        }
        if (index + 1 === length) {
            elem.addClass('ui-carousel-item-end');
        }
        //elem.find("*").removeAttr("id")
        elem.find("[id]").add(elem).each(function() {
            if (this.id) {
                this.id = this.id + '_clone';
            }
        });
    },

    totalIndicators: function() {
        return this.itemsCount !== 0 ? Math.ceil((this.itemsCount - this.d_numVisible) / this.d_numScroll) + 1 : 0;
    },

    backwardIsDisabled: function() {
        return (this.itemsCount !== 0 && (!this.cfg.circular || this.itemsCount < this.d_numVisible) && this.d_page === 0);
    },

    forwardIsDisabled: function() {
        return (this.itemsCount !== 0 && (!this.cfg.circular || this.itemsCount < this.d_numVisible) && (this.d_page === (this.totalIndicators - 1) || this.totalIndicators === 0));
    },

    firstIndex: function() {
        return this.isCircular ? (-1 * (this.totalShiftedItems + this.d_numVisible)) : (this.totalShiftedItems * -1);
    },

    lastIndex: function() {
        return (this.firstIndex() + this.d_numVisible - 1);
    }

});