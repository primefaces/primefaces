/**
 * __PrimeFaces Carousel Widget__
 *
 * Carousel is a multi purpose component to display a set of data or general content with slide effects.
 *
 * @typedef {"fade" | "slide"} PrimeFaces.widget.Carousel.Effect Name of the animation for the carousel widget.
 *
 * @prop {number} columns The number of simultaneously visible items.
 * @prop {JQuery} dropdown The DOM element for the dropdown for selecting the item to show.
 * @prop {number} first 0-based index of the the first items that is shown currently.
 * @prop {JQuery} header The DOM element for the header of the carousel.
 * @prop {JQuery} items The DOM elements for the carousel items.
 * @prop {JQuery} itemsContainer The DOM element for the container of the carousel items.
 * @prop {number} itemsCount The total number of carousel items.
 * @prop {JQuery} nextNav The DOM element for the button to switch to the next carousel item.
 * @prop {number} page The currently displayed page of carousel items.
 * @prop {JQuery} pageLinks The DOM elements for the links to other carousel pages.
 * @prop {JQuery} prevNav The DOM element for the button to switch to the previous carousel item.
 * @prop {JQuery} responsiveDropdown The DOM element for the responsive dropdown for selecting the item show.
 * @prop {JQuery} stateholder The DOM element for the hidden input storing the currently visible carousel items.
 * @prop {string} stateKey The key of the HTML5 Local Storage that stores the current carousel state.
 * @prop {JQuery} toggler The DOM element for the carousel toggler.
 * @prop {JQuery} toggleableContent The DOM element for the toggleable content of the carousel.
 * @prop {JQuery} toggleStateHolder The DOM element for the hidden input with the current toggle state.
 * @prop {number} totalPages The total number of available carousel pages.
 * @prop {JQuery} viewport The DOM element for the viewport of the carousel that shows the carousel items.
 *
 * @interface {PrimeFaces.widget.CarouselCfg} cfg The configuration for the {@link  Carousel| Carousel widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {number} cfg.autoplayInterval Sets the time in milliseconds to have Carousel start scrolling automatically
 * after being initialized.
 * @prop {number} cfg.breakpoint Breakpoint value in pixels to switch between small and large viewport.
 * @prop {boolean} cfg.circular Sets continuous scrolling
 * @prop {boolean} cfg.collapsed Whether the carousel is initially collapsed.
 * @prop {string} cfg.easing Name of the easing animation.
 * @prop {PrimeFaces.widget.Carousel.Effect} cfg.effect Name of the animation for transitioning between pages.
 * @prop {number} cfg.effectDuration Duration of the animation in milliseconds.
 * @prop {number} cfg.firstVisible 0-based index of the first element to be displayed
 * @prop {number} cfg.numVisible Number of visible items per page
 * @prop {number} cfg.pageLinks Defines the number of page links of paginator.
 * @prop {boolean} cfg.responsive In responsive mode, carousel adjusts its content based on screen size.
 * @prop {boolean} cfg.stateful Whether the state of the carousel is saved between page loads.
 * @prop {number} cfg.toggleSpeed The speed at which the carousel toggles.
 * @prop {boolean} cfg.toggleable Whether the carousel is toggleable.
 * @prop {boolean} cfg.vertical Sets vertical scrolling
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
        this.viewport = this.jq.children('.ui-carousel-viewport');
        this.itemsContainer = this.viewport.children('.ui-carousel-items');
        this.items = this.itemsContainer.children('li');
        this.itemsCount = this.items.length;
        this.header = this.jq.children('.ui-carousel-header');
        this.prevNav = this.header.children('.ui-carousel-prev-button');
        this.nextNav = this.header.children('.ui-carousel-next-button');
        this.pageLinks = this.header.find('> .ui-carousel-page-links > .ui-carousel-page-link');
        this.dropdown = this.header.children('.ui-carousel-dropdown');
        this.responsiveDropdown = this.header.children('.ui-carousel-dropdown-responsive');
        this.stateholder = $(this.jqId + '_page');

        if(this.cfg.toggleable) {
            this.toggler = $(this.jqId + '_toggler');
            this.toggleStateHolder = $(this.jqId + '_collapsed');
            this.toggleableContent = this.jq.find(' > .ui-carousel-viewport > .ui-carousel-items, > .ui-carousel-footer');
        }

        this.cfg.numVisible = this.cfg.numVisible || 3;
        this.cfg.firstVisible = this.cfg.firstVisible || 0;
        this.columns = this.cfg.numVisible;
        this.first = this.cfg.firstVisible;
        this.cfg.effectDuration = this.cfg.effectDuration || 500;
        this.cfg.circular = this.cfg.circular || false;
        this.cfg.breakpoint = this.cfg.breakpoint || 640;
        this.page = parseInt(this.first / this.columns);
        this.totalPages = Math.ceil(this.itemsCount / this.cfg.numVisible);

        if(this.cfg.stateful) {
            this.stateKey = PrimeFaces.createStorageKey(this.id, 'Carousel');

            this.restoreState();
        }

        this.renderDeferred();
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
     * @include
     * @override
     * @inheritdoc
     * @protected
     */
    _render: function() {
        this.updateNavigators();
        this.bindEvents();

        if(this.cfg.vertical) {
            this.calculateItemHeights();
        }
        else if(this.cfg.responsive) {
            this.refreshDimensions();
        }
        else {
            this.calculateItemWidths(this.columns);
            this.jq.width(this.jq.width());
            this.updateNavigators();
        }

        if(this.cfg.collapsed) {
            this.toggleableContent.hide();
        }
    },

    /**
     * Calculates the required width of each item, and applies that width.
     * @private
     */
    calculateItemWidths: function() {
        var firstItem = this.items.eq(0);
        if(firstItem.length) {
            var itemFrameWidth = firstItem.outerWidth(true) - firstItem.width();    //sum of margin, border and padding
            this.items.width((this.viewport.innerWidth() - itemFrameWidth * this.columns) / this.columns);
        }
    },

    /**
     * Calculates the required height of each item, and applies that height.
     * @private
     */
    calculateItemHeights: function() {
        var firstItem = this.items.eq(0);
        if(firstItem.length) {
            if(!this.cfg.responsive) {
                this.items.width(firstItem.width());
                this.jq.width(this.jq.width());
                var maxHeight = 0;
                for(var i = 0; i < this.items.length; i++) {
                    var item = this.items.eq(i),
                    height = item.height();

                    if(maxHeight < height) {
                        maxHeight = height;
                    }
                }
                this.items.height(maxHeight);
            }
            var totalMargins = ((firstItem.outerHeight(true) - firstItem.outerHeight()) / 2) * (this.cfg.numVisible);
            this.viewport.height((firstItem.outerHeight() * this.cfg.numVisible) + totalMargins);
            this.updateNavigators();
            this.itemsContainer.css('top', (-1 * (this.viewport.innerHeight() * this.page))+ 'px');
        }
    },

    /**
     * Calculates the proper size for this widget and applies it.
     * @private
     */
    refreshDimensions: function() {
        var win = $(window);
        if(win.width() <= this.cfg.breakpoint) {
            this.columns = 1;
            this.calculateItemWidths(this.columns);
            this.totalPages = this.itemsCount;
            this.responsiveDropdown.show();
            this.pageLinks.hide();
        }
        else {
            this.columns = this.cfg.numVisible;
            this.calculateItemWidths();
            this.totalPages = Math.ceil(this.itemsCount / this.cfg.numVisible);
            this.responsiveDropdown.hide();
            this.pageLinks.show();
        }

        this.page = parseInt(this.first / this.columns);
        this.updateNavigators();
        this.itemsContainer.css('left', (-1 * (this.viewport.innerWidth() * this.page))+ 'px');
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.prevNav.on('click', function() {
            if($this.page !== 0) {
                $this.setPage($this.page - 1);
            }
            else if($this.cfg.circular) {
                $this.setPage($this.totalPages - 1);
            }
        });

        this.nextNav.on('click', function() {
            var lastPage = ($this.page === ($this.totalPages - 1));

            if(!lastPage) {
                $this.setPage($this.page + 1);
            }
            else if($this.cfg.circular) {
                $this.setPage(0);
            }
        });

        if (PrimeFaces.env.isTouchable(this.cfg)) {
            this.itemsContainer.swipe({
                swipeLeft:function(event) {
                    if($this.page === ($this.totalPages - 1)) {
                        if($this.cfg.circular)
                            $this.setPage(0);
                    }
                    else {
                        $this.setPage($this.page + 1);
                    }
                },
                swipeRight: function(event) {
                    if($this.page === 0) {
                        if($this.cfg.circular)
                            $this.setPage($this.totalPages - 1);
                    }
                    else {
                        $this.setPage($this.page - 1);
                    }
                },
                excludedElements: PrimeFaces.utils.excludedSwipeElements()
            });
        }

        if(this.pageLinks.length) {
            this.pageLinks.on('click', function(e) {
                $this.setPage($(this).index());
                e.preventDefault();
            });
        }

        this.header.children('select').on('change', function() {
            $this.setPage(parseInt($(this).val()) - 1);
        });

        if(this.cfg.autoplayInterval) {
            this.cfg.circular = true;
            this.startAutoplay();
        }

        if(this.cfg.responsive) {
            PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', null, function() {
                if($this.cfg.vertical) {
                    $this.calculateItemHeights();
                }
                else {
                    $this.refreshDimensions();
                }
            });
        }

        if(this.cfg.toggleable) {
            this.toggler.on('mouseover.carouselToggler',function() {
                $(this).addClass('ui-state-hover');
            }).on('mouseout.carouselToggler',function() {
                $(this).removeClass('ui-state-hover');
            }).on('click.carouselToggler', function(e) {
                $this.toggle();
                e.preventDefault();
            });
        }
    },

    /**
     * Updates the navigator icons to reflect the current page.
     * @private
     */
    updateNavigators: function() {
        if(!this.cfg.circular) {
            if(this.page === 0) {
                this.prevNav.addClass('ui-state-disabled');
                this.nextNav.removeClass('ui-state-disabled');
            }
            else if(this.page === (this.totalPages - 1)) {
                this.prevNav.removeClass('ui-state-disabled');
                this.nextNav.addClass('ui-state-disabled');
            }
            else {
                this.prevNav.removeClass('ui-state-disabled');
                this.nextNav.removeClass('ui-state-disabled');
            }
        }

        if(this.pageLinks.length) {
            this.pageLinks.filter('.ui-icon-radio-on').removeClass('ui-icon-radio-on');
            this.pageLinks.eq(this.page).addClass('ui-icon-radio-on');
        }

        if(this.dropdown.length) {
            this.dropdown.val(this.page + 1);
        }

        if(this.responsiveDropdown.length) {
            this.responsiveDropdown.val(this.page + 1);
        }
    },

    /**
     * Moves this carousel to the given page.
     * @param {number} p 0-based index of the page to display.
     */
    setPage: function(p) {
        if(p !== this.page && !this.itemsContainer.is(':animated')) {
            var $this = this,
            animationProps = this.cfg.vertical ? {top: -1 * (this.viewport.innerHeight() * p)} : {left: -1 * (this.viewport.innerWidth() * p)};
            animationProps.easing = this.cfg.easing;

            this.itemsContainer.animate(animationProps,
            {
                duration: this.cfg.effectDuration,
                easing: this.cfg.easing,
                complete: function() {
                    $this.page = p;
                    $this.first = $this.page * $this.columns;
                    $this.updateNavigators();
                    $this.stateholder.val($this.page);
                    if($this.cfg.stateful) {
                        $this.saveState();
                    }
                }
            });
        }
    },

    /**
     * Enables autoplay and starts the slideshow.
     */
    startAutoplay: function() {
        var $this = this;

        this.interval = setInterval(function() {
            if($this.page === ($this.totalPages - 1))
                $this.setPage(0);
            else
                $this.setPage($this.page + 1);
        }, this.cfg.autoplayInterval);
    },

    /**
     * Disables autoplay and stops the slideshow.
     */
    stopAutoplay: function() {
        clearInterval(this.interval);
    },

    /**
     * Expands or collapses the content this carousel, depending on whether it is currently collapsed or expanded,
     * respectively.
     */
    toggle: function() {
        if(this.cfg.collapsed) {
            this.expand();
        }
        else {
            this.collapse();
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    },

    /**
     * If enabled, expands the content of this carousel.
     */
    expand: function() {
        this.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');

        this.slideDown();
    },

    /**
     * If enabled, collapses the content of this carousel.
     */
    collapse: function() {
        this.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');

        this.slideUp();
    },

    /**
     * Slides up the toggleable content.
     * @private
     */
    slideUp: function() {
        this.toggleableContent.slideUp(this.cfg.toggleSpeed, 'easeInOutCirc');
    },

    /**
     * Slides down the toggleable content.
     * @private
     */
    slideDown: function() {
        this.toggleableContent.slideDown(this.cfg.toggleSpeed, 'easeInOutCirc');
    },

    /**
     * Expands or collapses this carousel as indicated by the given arguments.
     * @private
     * @param {boolean} collapsed `false` to expand, `true` to collapse.
     * @param {string} removeIcon Class of the remove icon
     * @param {string} addIcon Class of the add icon.
     */
    toggleState: function(collapsed, removeIcon, addIcon) {
        this.toggler.children('span.ui-icon').removeClass(removeIcon).addClass(addIcon);
        this.cfg.collapsed = collapsed;
        this.toggleStateHolder.val(collapsed);

        if(this.cfg.stateful) {
            this.saveState();
        }
    },

    /**
     * Restores the state as saved by `saveState` to this carousel.
     * @private
     */
    restoreState: function() {
        var carouselStateAsString = localStorage.getItem(this.stateKey) || "first: null, collapsed: null";
        this.carouselState = PrimeFaces.csp.evalResult('({' + carouselStateAsString + '})');

        this.first = this.carouselState.first||this.first;
        this.page = parseInt(this.first/this.columns);

        this.stateholder.val(this.page);

        if(this.cfg.toggleable && (this.carouselState.collapsed === false || this.carouselState.collapsed === true)) {
            this.cfg.collapsed = !this.carouselState.collapsed;
            this.toggle();
        }
    },

    /**
     * Saves the current state of this carousel (current page etc.) in HTML5 Local Store.
     * @private
     */
    saveState: function() {
        var carouselStateAsString = "first:" + this.first;

        if(this.cfg.toggleable) {
            carouselStateAsString += ", collapsed: " + this.toggleStateHolder.val();
        }

        localStorage.setItem(this.stateKey, carouselStateAsString);
    },

    /**
     * Clears the state as saved by `saveState`.
     * @private
     */
    clearState: function() {
        if(this.cfg.stateful) {
            localStorage.removeItem(this.stateKey);
        }
    }

});