/**
 * PrimeFaces Carousel Widget
 */
PrimeFaces.widget.Carousel = PrimeFaces.widget.DeferredWidget.extend({

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

        this.cfg.numVisible = this.cfg.numVisible||3;
        this.cfg.firstVisible = this.cfg.firstVisible||0;
        this.columns = this.cfg.numVisible;
        this.first = this.cfg.firstVisible;
        this.cfg.effectDuration = this.cfg.effectDuration||500;
        this.cfg.circular = this.cfg.circular||false;
        this.cfg.breakpoint = this.cfg.breakpoint||640;
        this.page = parseInt(this.first/this.columns);
        this.totalPages = Math.ceil(this.itemsCount/this.cfg.numVisible);

        if(this.cfg.stateful) {
            this.stateKey = 'carousel-' + this.id;

            this.restoreState();
        }

        this.renderDeferred();
    },

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

    calculateItemWidths: function() {
        var firstItem = this.items.eq(0);
        if(firstItem.length) {
            var itemFrameWidth = firstItem.outerWidth(true) - firstItem.width();    //sum of margin, border and padding
            this.items.width((this.viewport.innerWidth() - itemFrameWidth * this.columns) / this.columns);
        }
    },

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
            this.itemsContainer.css('top', -1 * (this.viewport.innerHeight() * this.page));
        }
    },

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
        this.itemsContainer.css('left', (-1 * (this.viewport.innerWidth() * this.page)));
    },

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
            excludedElements: "button, input, select, textarea, a, .noSwipe"
        });

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

    startAutoplay: function() {
        var $this = this;

        this.interval = setInterval(function() {
            if($this.page === ($this.totalPages - 1))
                $this.setPage(0);
            else
                $this.setPage($this.page + 1);
        }, this.cfg.autoplayInterval);
    },

    stopAutoplay: function() {
        clearInterval(this.interval);
    },

    toggle: function() {
        if(this.cfg.collapsed) {
            this.expand();
        }
        else {
            this.collapse();
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    },

    expand: function() {
        this.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');

        this.slideDown();
    },

    collapse: function() {
        this.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');

        this.slideUp();
    },

    slideUp: function() {
        this.toggleableContent.slideUp(this.cfg.toggleSpeed, 'easeInOutCirc');
    },

    slideDown: function() {
        this.toggleableContent.slideDown(this.cfg.toggleSpeed, 'easeInOutCirc');
    },

    toggleState: function(collapsed, removeIcon, addIcon) {
        this.toggler.children('span.ui-icon').removeClass(removeIcon).addClass(addIcon);
        this.cfg.collapsed = collapsed;
        this.toggleStateHolder.val(collapsed);

        if(this.cfg.stateful) {
            this.saveState();
        }
    },

    restoreState: function() {
        var carouselStateAsString = PrimeFaces.getCookie(this.stateKey) || "first: null, collapsed: null";
        this.carouselState = eval('({' + carouselStateAsString + '})');

        this.first = this.carouselState.first||this.first;
        this.page = parseInt(this.first/this.columns);

        this.stateholder.val(this.page);

        if(this.cfg.toggleable && (this.carouselState.collapsed === false || this.carouselState.collapsed === true)) {
            this.cfg.collapsed = !this.carouselState.collapsed;
            this.toggle();
        }
    },

    saveState: function() {
        var carouselStateAsString = "first:" + this.first;

        if(this.cfg.toggleable) {
            carouselStateAsString += ", collapsed: " + this.toggleStateHolder.val();
        }

        PrimeFaces.setCookie(this.stateKey, carouselStateAsString, {path:'/'});
    },

    clearState: function() {
        if(this.cfg.stateful) {
            PrimeFaces.deleteCookie(this.stateKey, {path:'/'});
        }
    }

});