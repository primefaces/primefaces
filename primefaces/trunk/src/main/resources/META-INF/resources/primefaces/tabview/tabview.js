/**
 * PrimeFaces TabView Widget
 */
PrimeFaces.widget.TabView = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.panelContainer = this.jq.children('.ui-tabs-panels');
        this.stateHolder = $(this.jqId + '_activeIndex');
        this.cfg.selected = parseInt(this.stateHolder.val());
        this.onshowHandlers = this.onshowHandlers||{};
        
        if(this.cfg.scrollable) {
            this.navscroller = this.jq.children('.ui-tabs-navscroller');
            this.navcrollerLeft = this.navscroller.children('.ui-tabs-navscroller-btn-left');
            this.navcrollerRight = this.navscroller.children('.ui-tabs-navscroller-btn-right');
            this.navContainer = this.navscroller.children('.ui-tabs-nav');
            this.firstTab = this.navContainer.children(':first-child');
            this.lastTab = this.navContainer.children(':last-child');
            this.scrollStateHolder = $(this.jqId + '_scrollState');
        }
        else {
            this.navContainer = this.jq.children('.ui-tabs-nav');
        }

        this.bindEvents();

        //Cache initial active tab
        if(this.cfg.dynamic && this.cfg.cache) {
            this.markAsLoaded(this.panelContainer.children().eq(this.cfg.selected));
        }
        
        if(this.cfg.scrollable) {
            if(this.jq.is(':visible')) {
                this.initScrolling();
            }
            else {
                var hiddenParent = this.jq.parent().closest('.ui-hidden-container'),
                hiddenParentWidgetVar = hiddenParent.data('widget'),
                $this = this;

                if(hiddenParentWidgetVar) {
                    var hiddenParentWidget = PF(hiddenParentWidgetVar);
                    
                    if(hiddenParentWidget) {
                        hiddenParentWidget.addOnshowHandler(this.id, function() {
                            return $this.initScrolling();
                        });
                    }
                    
                }
            } 
        }
    },
    
    bindEvents: function() {
        var $this = this;

        //Tab header events
        this.navContainer.children('li')
                .on('mouseover.tabview', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-disabled')) {
                        element.addClass('ui-state-hover');
                    }
                })
                .on('mouseout.tabview', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-disabled')) {
                        element.removeClass('ui-state-hover');
                    }
                })
                .on('click.tabview', function(e) {
                    var element = $(this);

                    if($(e.target).is(':not(.ui-icon-close)')) {
                        var index = element.index();

                        if(!element.hasClass('ui-state-disabled') && index !== $this.cfg.selected) {
                            $this.select(index);
                        }
                    }

                    e.preventDefault();
                });

        //Closable tabs
        this.navContainer.find('li .ui-icon-close')
            .on('click.tabview', function(e) {
                var index = $(this).parent().index();
              
                if($this.cfg.onTabClose) {
                    var retVal = $this.cfg.onTabClose.call($this, index);
                    
                    if(retVal !== false) {
                        $this.remove(index);
                    }
                }
                else {
                    $this.remove(index);
                }

                e.preventDefault();
            });

        //Scrolling
        if(this.cfg.scrollable) {
            this.navscroller.children('.ui-tabs-navscroller-btn')
                            .on('mouseover.tabview', function() {
                                var el = $(this);
                                if(!el.hasClass('ui-state-disabled'))
                                    $(this).addClass('ui-state-hover');
                            })
                            .on('mouseout.tabview', function() {
                                var el = $(this);
                                if(!el.hasClass('ui-state-disabled'))
                                    $(this).removeClass('ui-state-hover ui-state-active');
                            })
                            .on('mousedown.tabview', function() {
                                var el = $(this);
                                if(!el.hasClass('ui-state-disabled'))
                                    $(this).removeClass('ui-state-hover').addClass('ui-state-active');
                            })
                            .on('mouseup.tabview', function() {
                                var el = $(this);
                                if(!el.hasClass('ui-state-disabled'))
                                    $(this).addClass('ui-state-hover').removeClass('ui-state-active');
                            });
            
            
            this.navcrollerLeft.on('click.tabview', function(e) {
                                $this.scroll(100);
                                e.preventDefault();
                            });
                            
            this.navcrollerRight.on('click.tabview', function(e) {
                                $this.scroll(-100);
                                e.preventDefault();
                            });
        }
    },
        
    initScrolling: function() {
        if(this.jq.is(':visible')) {
            var overflown = ((this.lastTab.position().left + this.lastTab.width()) - this.firstTab.position().left) > this.navscroller.innerWidth();
            if(overflown) {
                this.navscroller.css('padding-left', '18px');
                this.navcrollerLeft.show();
                this.navcrollerRight.show();
                this.restoreScrollState();
            }
            
            return true;
        }
        else {
            return false;
        }
    },
        
    scroll: function(step) {
        if(this.navContainer.is(':animated')) {
            return;
        }
        
        var oldMarginLeft = parseInt(this.navContainer.css('margin-left')),
        newMarginLeft = oldMarginLeft + step,
        viewportWidth = this.navscroller.innerWidth(),
        $this = this;

        if(step < 0) {
            var lastTabBoundry = this.lastTab.position().left + parseInt(this.lastTab.innerWidth());
            
            if(lastTabBoundry > viewportWidth)
                this.navContainer.animate({'margin-left': newMarginLeft + 'px'}, 'fast', 'easeInOutCirc', function() {
                    $this.saveScrollState(newMarginLeft);
                    
                    if((lastTabBoundry + step) < viewportWidth)
                        $this.disableScrollerButton($this.navcrollerRight);
                    else if($this.navcrollerLeft.hasClass('ui-state-disabled'))
                        $this.enableScrollerButton($this.navcrollerLeft);
                });
        }
        else {
            if(newMarginLeft <= 0) {
                this.navContainer.animate({'margin-left': newMarginLeft + 'px'}, 'fast', 'easeInOutCirc', function() {
                    $this.saveScrollState(newMarginLeft);
                    
                    if(newMarginLeft === 0)
                       $this.disableScrollerButton($this.navcrollerLeft);
                    else if($this.navcrollerRight.hasClass('ui-state-disabled'))
                        $this.enableScrollerButton($this.navcrollerRight);
                });        
            }           
        }
    },
    
    disableScrollerButton: function(btn) {
        btn.addClass('ui-state-disabled').removeClass('ui-state-hover ui-state-active');
    },
            
    enableScrollerButton: function(btn) {
        btn.removeClass('ui-state-disabled');
    },
            
    saveScrollState: function(value) {
        this.scrollStateHolder.val(value);
    },
            
    restoreScrollState: function() {
        var value = parseInt(this.scrollStateHolder.val());
        if(value === 0) {
            this.disableScrollerButton(this.navcrollerLeft);
        }
        
        this.navContainer.css('margin-left', this.scrollStateHolder.val() + 'px');
    },
             
    /**
     * Selects an inactive tab given index
     */
    select: function(index, silent) {
        //Call user onTabChange callback
        if(this.cfg.onTabChange && !silent) {
            var result = this.cfg.onTabChange.call(this, index);
            if(result === false)
                return false;
        }

        var newPanel = this.panelContainer.children().eq(index),
        shouldLoad = this.cfg.dynamic && !this.isLoaded(newPanel);

        //update state
        this.stateHolder.val(index);
        this.cfg.selected = index;

        if(shouldLoad) {
            this.loadDynamicTab(newPanel);
        }
        else {
            this.show(newPanel);
            
            if(this.hasBehavior('tabChange') && !silent) {
                this.fireTabChangeEvent(newPanel);
            }
        }

        return true;
    },
    
    show: function(newPanel) {
        var headers = this.navContainer.children(),
        oldHeader = headers.filter('.ui-state-active'),
        newHeader = headers.eq(newPanel.index()),
        oldPanel = this.panelContainer.children('.ui-tabs-panel:visible'),
        _self = this;

        //aria
        oldPanel.attr('aria-hidden', true);
        oldHeader.attr('aria-expanded', false);
        newPanel.attr('aria-hidden', false);
        newHeader.attr('aria-expanded', true);

        if(this.cfg.effect) {
                oldPanel.hide(this.cfg.effect, null, this.cfg.effectDuration, function() {
                oldHeader.removeClass('ui-state-focus ui-tabs-selected ui-state-active');

                newHeader.addClass('ui-state-focus ui-tabs-selected ui-state-active');
                newPanel.show(_self.cfg.effect, null, _self.cfg.effectDuration, function() {
                    _self.postTabShow(newPanel);
                });
            });
        }
        else {
            oldHeader.removeClass('ui-state-focus ui-tabs-selected ui-state-active');
            oldPanel.hide();

            newHeader.addClass('ui-state-focus ui-tabs-selected ui-state-active');
            newPanel.show();

            this.postTabShow(newPanel);
        }
    },
    
    /**
     * Loads tab contents with ajax
     */
    loadDynamicTab: function(newPanel) {
        var $this = this,
        tabindex = newPanel.index(),
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: newPanel.attr('id')},
                {name: this.id + '_tabindex', value: tabindex}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            newPanel.html(content);

                            if(this.cfg.cache) {
                                this.markAsLoaded(newPanel);
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.show(newPanel);
            }
        };

        if(this.hasBehavior('tabChange')) {
            var tabChangeBehavior = this.cfg.behaviors['tabChange'];

            tabChangeBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    /**
     * Removes a tab with given index
     */
    remove: function(index) {    
        var header = this.navContainer.children().eq(index),
        panel = this.panelContainer.children().eq(index);

        this.fireTabCloseEvent(panel);

        header.remove();
        panel.remove();
        
        if(index < this.cfg.selected) {
            this.cfg.selected--;
        }
        else if(index === this.cfg.selected) {
            var newIndex = (this.cfg.selected === this.getLength()) ? this.cfg.selected - 1: this.cfg.selected;
            this.select(newIndex, true);
        }
    },
    
    getLength: function() {
        return this.navContainer.children().length;
    },
    
    getActiveIndex: function() {
        return this.cfg.selected;
    },
    
    fireTabChangeEvent: function(panel) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: panel.index()}
            ]
        };
        
        tabChangeBehavior.call(this, ext);
    },
    
    fireTabCloseEvent: function(panel) {    
        if(this.hasBehavior('tabClose')) {
            var tabCloseBehavior = this.cfg.behaviors['tabClose'],
            ext = {
                params: [
                    {name: this.id + '_closeTab', value: panel.attr('id')},
                    {name: this.id + '_tabindex', value: panel.index()}
                ]
            };

            tabCloseBehavior.call(this, ext);
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    },
    
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },
    
    isLoaded: function(panel) {
        return panel.data('loaded') === true;
    },
    
    disable: function(index) {
        this.navContainer.children().eq(index).addClass('ui-state-disabled');
    },
    
    enable: function(index) {
        this.navContainer.children().eq(index).removeClass('ui-state-disabled');
    },
    
    addOnshowHandler: function(id, fn) {
        this.onshowHandlers[id] = fn;
    },
    
    postTabShow: function(newPanel) {    
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel.index());
        }

        //execute onshowHandlers and remove successful ones
        for(var id in this.onshowHandlers) {
            if(this.onshowHandlers.hasOwnProperty(id)) {
                var fn = this.onshowHandlers[id];
                
                if(fn.call()) {
                    delete this.onshowHandlers[id];
                }
            }
        }
    }

});