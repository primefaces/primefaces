/**
 * PrimeFaces TabView Widget
 */
PrimeFaces.widget.TabView = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.panelContainer = this.jq.children('.ui-tabs-panels');
        this.stateHolder = $(this.jqId + '_activeIndex');
        this.cfg.selected = parseInt(this.stateHolder.val());
        this.focusedTabHeader = null;
        this.cfg.tabindex = this.cfg.tabindex||'0';
        
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

        this.navContainerItems = this.navContainer.children('li');
        for(var i = 0; i < this.navContainerItems.length; i++) {
            if(this.cfg.selected === i && !this.navContainerItems.eq(i).hasClass('ui-state-disabled')) {
                this.navContainerItems.eq(i).attr('tabindex', this.cfg.tabindex);
            }
            else {
                this.navContainerItems.eq(i).attr('tabindex', '-1');
            }
        }
        
        this.bindEvents();

        //Cache initial active tab
        if(this.cfg.dynamic && this.cfg.cache) {
            this.markAsLoaded(this.panelContainer.children().eq(this.cfg.selected));
        }
        
        this.renderDeferred();
    },
    
    //@Override
    renderDeferred: function() {     
        if(this.jq.is(':visible')) {
            this._render();
        }
        else {
            var container = this.jq.parent().closest('.ui-hidden-container'),
            $this = this;
    
            if(container.length) {
                this.addDeferredRender(this.id, container, function() {
                    return $this.render();
                });
            }
        }
    },
    
    _render: function() {
        if(this.cfg.scrollable) {
            this.initScrolling();
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
                            element.trigger('focus.tabview');
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
        
        this.bindKeyEvents();
    },
        
    bindKeyEvents: function() {
        var $this = this;
        
        this.navContainer.children('li').on('focus.tabview', function() {
            $this.focusedTabHeader = $(this);
            if(!$this.focusedTabHeader.hasClass('ui-state-disabled')) {
                $this.navContainer.children('li[tabindex="'+ $this.cfg.tabindex +'"]').attr('tabindex', '-1').removeClass('ui-tabs-outline');
                $this.focusedTabHeader.attr('tabindex', $this.cfg.tabindex).addClass('ui-tabs-outline');
            }
        })
        .on('blur.tabview', function(){
            if($this.focusedTabHeader) {
                $this.focusedTabHeader.removeClass('ui-tabs-outline');                
            }
        })
        .on('keydown.tabview', function(e) {
            var keyCode = $.ui.keyCode;
 
            switch(e.which) {
                case keyCode.LEFT:
                case keyCode.UP: 
                    if($this.focusedTabHeader) {
                        if($this.cfg.scrollable && ($this.focusedTabHeader.index() === 0)) {
                            break;
                        }
                      
                        if($this.focusedTabHeader.index() === 0) {
                            $this.focusedTabHeader = $this.navContainer.children('li:not(.ui-state-disabled):last');
                        }
                        else {
                            $this.focusedTabHeader = $this.focusedTabHeader.prevAll('li:not(.ui-state-disabled):first');
                            if(!$this.focusedTabHeader.length) {
                                $this.focusedTabHeader = $this.navContainer.children('li:not(.ui-state-disabled):last');
                            }
                        }
                        $this.focusedTabHeader.trigger('focus.tabview');

                        if($this.cfg.scrollable) {
                            var leftScroll = $this.focusedTabHeader.position().left < $this.navcrollerLeft.position().left;
                            if(leftScroll) {
                                $this.navcrollerLeft.trigger('click.tabview');
                            }
                        }
                    }
                    e.preventDefault();
                    clearTimeout($this.activating);
                    
                    $this.activating = setTimeout(function() {                
                        $this.focusedTabHeader.trigger('click');
                    }, 500);
                break;

                case keyCode.RIGHT:
                case keyCode.DOWN:
                    if($this.focusedTabHeader) {
                        if($this.cfg.scrollable && ($this.focusedTabHeader.index() === ($this.getLength() - 1))) {
                            break;
                        }
                        
                        if($this.focusedTabHeader.index() === ($this.getLength() - 1)) {
                            $this.focusedTabHeader = $this.navContainer.children('li:not(.ui-state-disabled):first');
                        }
                        else {
                            $this.focusedTabHeader = $this.focusedTabHeader.nextAll('li:not(.ui-state-disabled):first');
                            if(!$this.focusedTabHeader.length) {
                                $this.focusedTabHeader = $this.navContainer.children('li:not(.ui-state-disabled):first');
                            }
                        } 
                        $this.focusedTabHeader.trigger('focus.tabview');

                        if($this.cfg.scrollable) {
                            var rightScroll = $this.focusedTabHeader.position().left + $this.focusedTabHeader.width() > $this.navcrollerRight.position().left;
                            if(rightScroll) {
                                $this.navcrollerRight.trigger('click.tabview');
                            }
                        }
                    }
                    e.preventDefault();
                    clearTimeout($this.activating);
                    
                    $this.activating = setTimeout(function() {
                        $this.focusedTabHeader.trigger('click');
                    }, 500);
                break;
            }       
        });
    },
        
    initScrolling: function() {
        if(this.panelContainer.children().length) {
            var overflown = ((this.lastTab.position().left + this.lastTab.width()) - this.firstTab.position().left) > this.navscroller.innerWidth();
            if(overflown) {
                this.navscroller.css('padding-left', '18px');
                this.navcrollerLeft.show();
                this.navcrollerRight.show();
                this.restoreScrollState();
            }
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
                    if($this.navcrollerLeft.hasClass('ui-state-disabled'))
                        $this.enableScrollerButton($this.navcrollerLeft);
                });
        }
        else {
            if(newMarginLeft <= 0) {
                this.navContainer.animate({'margin-left': newMarginLeft + 'px'}, 'fast', 'easeInOutCirc', function() {
                    $this.saveScrollState(newMarginLeft);
                    
                    if(newMarginLeft === 0)
                        $this.disableScrollerButton($this.navcrollerLeft);
                    if($this.navcrollerRight.hasClass('ui-state-disabled'))
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
        oldHeader.attr('aria-selected', false);
        newPanel.attr('aria-hidden', false);
        newHeader.attr('aria-expanded', true);
        newHeader.attr('aria-selected', true);

        if(this.cfg.effect) {
                oldPanel.hide(this.cfg.effect, null, this.cfg.effectDuration, function() {
                oldHeader.removeClass('ui-tabs-selected ui-state-active');

                newHeader.addClass('ui-tabs-selected ui-state-active');
                newPanel.show(_self.cfg.effect, null, _self.cfg.effectDuration, function() {
                    _self.postTabShow(newPanel);
                });
            });
        }
        else {
            oldHeader.removeClass('ui-tabs-selected ui-state-active');
            oldPanel.hide();

            newHeader.addClass('ui-tabs-selected ui-state-active');
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

        header.remove();
        panel.remove();
        
        var length = this.getLength();
        
        if(length > 0) {
            if(index < this.cfg.selected) {
                this.cfg.selected--;
            }
            else if(index === this.cfg.selected) {
                var newIndex = (this.cfg.selected === (length)) ? (this.cfg.selected - 1): this.cfg.selected;
                this.select(newIndex, true);
            }
        }
        else {
            this.cfg.selected = -1;
        }
        
        this.fireTabCloseEvent(panel.attr('id'), index);
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
    
    fireTabCloseEvent: function(id, index) {    
        if(this.hasBehavior('tabClose')) {
            var tabCloseBehavior = this.cfg.behaviors['tabClose'],
            ext = {
                params: [
                    {name: this.id + '_closeTab', value: id},
                    {name: this.id + '_tabindex', value: index}
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
    
    postTabShow: function(newPanel) {    
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel.index());
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }

});