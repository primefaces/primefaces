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
        
        this.cfg.numVisible = this.cfg.numVisible||3;
        this.cfg.firstVisible = this.cfg.firstVisible||0;
        this.cfg.effectDuration = this.cfg.effectDuration||500;
        this.cfg.circular = this.cfg.circular||false;
        this.page = this.cfg.firstVisible / this.cfg.numVisible;
        this.totalPages = Math.ceil(this.itemsCount / this.cfg.numVisible);
        
        this.renderDeferred();        
    },
    
    _render: function() {
        this.updateNavigators();
        this.initDimensions(this.cfg.numVisible);
        this.bindEvents();
    },
    
    initDimensions: function(columns) {
        var firstItem = this.items.eq(0);
        if(firstItem.length) {
            var itemFrameWidth = firstItem.outerWidth(true) - firstItem.width();    //sum of margin, border and padding
            this.items.width(this.viewport.innerWidth()/columns- itemFrameWidth);
        }
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
        
        if(this.dropdown.length) {
            this.dropdown.on('change', function() {
                $this.setPage(parseInt($(this).val()) - 1);
            });
        }
        
        if(this.cfg.autoplayInterval) {
            this.cfg.circular = true;
            this.startAutoplay();
        }
        
        if(this.cfg.responsive) {
            var resizeNS = 'resize.' + this.id,
            $this = this;
    
            $(window).off(resizeNS).on(resizeNS, function() {
                if($this.viewport.innerWidth() < 560) {
                    $this.initDimensions(1);
                }
                else {
                    $this.initDimensions($this.cfg.numVisible);
                }
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
    },
    
    setPage: function(p) {                
        if(p !== this.page && !this.itemsContainer.is(':animated')) {
            var $this = this;
            
            this.itemsContainer.animate({
                left: -1 * (this.viewport.innerWidth() * p)
                ,easing: this.cfg.easing
            }, this.cfg.effectDuration, function() {
                $this.page = p;
                $this.updateNavigators();
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