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
        this.mobileDropdown = this.header.children('.ui-carousel-mobiledropdown');
        this.stateholder = $(this.jqId + '_page');
        
        this.cfg.numVisible = this.cfg.numVisible||3;
        this.cfg.firstVisible = this.cfg.firstVisible||0;
        this.columns = this.cfg.numVisible;
        this.first = this.cfg.firstVisible;
        this.cfg.effectDuration = this.cfg.effectDuration||500;
        this.cfg.circular = this.cfg.circular||false;
        this.cfg.breakpoint = this.cfg.breakpoint||640;
        this.page = parseInt(this.first/this.columns);
        this.totalPages = Math.ceil(this.itemsCount/this.cfg.numVisible);
        this.renderDeferred();
    },
    
    _render: function() {
        this.updateNavigators();
        this.bindEvents();
        
        if(this.cfg.responsive) {
            this.refreshDimensions();
        }
        else {
            this.calculateItemWidths(this.columns);
            this.jq.width(this.jq.width());
            this.updateNavigators();
        }
    },
    
    calculateItemWidths: function() {
        var firstItem = this.items.eq(0);
        if(firstItem.length) {
            var itemFrameWidth = firstItem.outerWidth(true) - firstItem.width();    //sum of margin, border and padding
            this.items.width((this.viewport.innerWidth() - itemFrameWidth * this.columns) / this.columns);
        }
    },
    
    refreshDimensions: function() {
        var win = $(window);
        if(win.width() <= this.cfg.breakpoint) {
            this.columns = 1;
            this.calculateItemWidths(this.columns);
            this.totalPages = this.itemsCount;
            this.mobileDropdown.show();
            this.pageLinks.hide();
        }
        else {
            this.columns = this.cfg.numVisible;
            this.calculateItemWidths();
            this.totalPages = Math.ceil(this.itemsCount / this.cfg.numVisible);
            this.mobileDropdown.hide();
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
            swipe:function(event, direction) {
                if(direction === 'left') {
                    if($this.page === ($this.totalPages - 1)) {
                        if($this.cfg.circular)
                            $this.setPage(0);
                    }
                    else {
                        $this.setPage($this.page + 1);
                    }
                }
                else if(direction === 'right') {
                    if($this.page === 0) {
                        if($this.cfg.circular)
                            $this.setPage($this.totalPages - 1);
                    }
                    else {
                        $this.setPage($this.page - 1);
                    }
                }
            }
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
            var resizeNS = 'resize.' + this.id;
            $(window).off(resizeNS).on(resizeNS, function() {
                $this.refreshDimensions();
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
        
        if(this.mobileDropdown.length) {
            this.mobileDropdown.val(this.page + 1);
        }
    },
    
    setPage: function(p) {      
        if(p !== this.page && !this.itemsContainer.is(':animated')) {
            var $this = this;
            
            this.itemsContainer.animate({
                left: -1 * (this.viewport.innerWidth() * p)
                ,easing: this.cfg.easing
            }, 
            {
                duration: this.cfg.effectDuration,
                easing: this.cfg.easing,
                complete: function() {
                    $this.page = p;
                    $this.first = $this.page * $this.columns;
                    $this.updateNavigators();
                    $this.stateholder.val($this.page);
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
    }
    
});  