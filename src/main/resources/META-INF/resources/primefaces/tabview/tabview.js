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
        this.tabindex = this.cfg.tabindex||0;

        if(this.cfg.scrollable) {
            this.navscroller = this.jq.children('.ui-tabs-navscroller');
            this.navcrollerLeft = this.navscroller.children('.ui-tabs-navscroller-btn-left');
            this.navcrollerRight = this.navscroller.children('.ui-tabs-navscroller-btn-right');
            this.navContainer = this.navscroller.children('.ui-tabs-nav');
            this.firstTab = this.navContainer.children('li.ui-tabs-header:first-child');
            this.lastTab = this.navContainer.children('li.ui-tabs-header:last-child');
            this.scrollStateHolder = $(this.jqId + '_scrollState');
        }
        else {
            this.navContainer = this.jq.children('.ui-tabs-nav');
        }

        this.headerContainer = this.navContainer.children('li.ui-tabs-header');

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

            var $this = this;

            PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', null, function() {
                $this.initScrolling();
            });
        }
    },

    bindEvents: function() {
        var $this = this;

        // Touch Swipe Events
        this.jq.swipe({
            swipeLeft:function(event) {
                var activeIndex = $this.getActiveIndex();
                if (activeIndex < $this.getLength() - 1) {
                    $this.select(activeIndex + 1);
                }
            },
            swipeRight: function(event) {
                var activeIndex = $this.getActiveIndex();
                if (activeIndex > 0) {
                    $this.select(activeIndex - 1);
                }
            },
            excludedElements: "button, input, select, textarea, a, .noSwipe"
        });

        //Tab header events
        this.headerContainer
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
                        var index = $this.headerContainer.index(element);

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
                            })
                            .on('focus.tabview', function() {
                                $(this).addClass('ui-state-focus');
                            })
                            .on('blur.tabview', function() {
                                $(this).removeClass('ui-state-focus');
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
        var $this = this,
            tabs = this.headerContainer;

        /* For Screen Reader and Keyboard accessibility */
        tabs.attr('tabindex', this.tabindex);

        tabs.on('focus.tabview', function(e) {
            var focusedTab = $(this);

            if(!focusedTab.hasClass('ui-state-disabled')) {
                focusedTab.addClass('ui-tabs-outline');

                if($this.cfg.scrollable) {
                    if(focusedTab.position().left + focusedTab.width() > $this.navcrollerRight.position().left) {
                        $this.navcrollerRight.trigger('click.tabview');
                    }
                    else if(focusedTab.position().left < $this.navcrollerLeft.position().left) {
                        $this.navcrollerLeft.trigger('click.tabview');
                    }
                }
            }
        })
        .on('blur.tabview', function(){
            $(this).removeClass('ui-tabs-outline');
        })
        .on('keydown.tabview', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which,
            element = $(this);

            if((key === keyCode.SPACE || key === keyCode.ENTER) && !element.hasClass('ui-state-disabled')) {
                $this.select(element.index());
                e.preventDefault();
            }
        });

        //Scrolling
        if(this.cfg.scrollable) {
            this.navcrollerLeft.on('keydown.tabview', function(e) {
                var keyCode = $.ui.keyCode,
                key = e.which;

                if(key === keyCode.SPACE || key === keyCode.ENTER) {
                    $this.scroll(100);
                    e.preventDefault();
                }
            });

            this.navcrollerRight.on('keydown.tabview', function(e) {
                var keyCode = $.ui.keyCode,
                key = e.which;

                if(key === keyCode.SPACE || key === keyCode.ENTER) {
                    $this.scroll(-100);
                    e.preventDefault();
                }
            });
        }
    },

    initScrolling: function() {
        if(this.headerContainer.length) {
            var overflown = ((this.lastTab.position().left + this.lastTab.width()) - this.firstTab.position().left) > this.navscroller.innerWidth();
            if (overflown) {
                this.navscroller.removeClass('ui-tabs-navscroller-btn-hidden');
                this.navcrollerLeft.attr('tabindex', this.tabindex);
                this.navcrollerRight.attr('tabindex', this.tabindex);
                this.restoreScrollState();
            }
            else {
                this.navscroller.addClass('ui-tabs-navscroller-btn-hidden');
                this.navcrollerLeft.attr('tabindex', this.tabindex);
                this.navcrollerRight.attr('tabindex', this.tabindex);
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
        btn.addClass('ui-state-disabled').removeClass('ui-state-hover ui-state-active ui-state-focus').attr('tabindex', -1);
    },

    enableScrollerButton: function(btn) {
        btn.removeClass('ui-state-disabled').attr('tabindex', this.tabindex);
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
        this.stateHolder.val(newPanel.data('index'));
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
        var headers = this.headerContainer,
        oldHeader = headers.filter('.ui-state-active'),
        oldActions = oldHeader.next('.ui-tabs-actions'),
        newHeader = headers.eq(newPanel.index()),
        newActions = newHeader.next('.ui-tabs-actions'),
        oldPanel = this.panelContainer.children('.ui-tabs-panel:visible'),
        _self = this;

        //aria
        oldPanel.attr('aria-hidden', true);
        oldPanel.addClass('ui-helper-hidden');
        oldHeader.attr('aria-expanded', false);
        oldHeader.attr('aria-selected', false);
        if(oldActions.length != 0) {
            oldActions.attr('aria-hidden', true);
        }

        newPanel.attr('aria-hidden', false);
        newPanel.removeClass('ui-helper-hidden');
        newHeader.attr('aria-expanded', true);
        newHeader.attr('aria-selected', true);
        if(newActions.length != 0) {
            newActions.attr('aria-hidden', false);
        }

        if(this.cfg.effect) {
            oldPanel.hide(this.cfg.effect, null, this.cfg.effectDuration, function() {
                oldHeader.removeClass('ui-tabs-selected ui-state-active');
                if(oldActions.length != 0) {
                    oldActions.hide(_self.cfg.effect, null, _self.cfg.effectDuration);
                }

                newHeader.addClass('ui-tabs-selected ui-state-active');
                newPanel.show(_self.cfg.effect, null, _self.cfg.effectDuration, function() {
                    _self.postTabShow(newPanel);
                });
                if(newActions.length != 0) {
                    newActions.show(_self.cfg.effect, null, _self.cfg.effectDuration);
                }
            });
        }
        else {
            oldHeader.removeClass('ui-tabs-selected ui-state-active');
            oldPanel.hide();
            if(oldActions.length != 0) {
                oldActions.hide();
            }

            newHeader.addClass('ui-tabs-selected ui-state-active');
            newPanel.show();
            if(newActions.length != 0) {
                newActions.show();
            }

            this.postTabShow(newPanel);
        }
    },

    /**
     * Loads tab contents with ajax
     */
    loadDynamicTab: function(newPanel) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: newPanel.attr('id')},
                {name: this.id + '_tabindex', value: newPanel.data('index')}
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
            this.callBehavior('tabChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Removes a tab with given index
     */
    remove: function(index) {
        // remove old header and content
        var header = this.headerContainer.eq(index),
        panel = this.panelContainer.children().eq(index);

        header.remove();
        panel.remove();

        // refresh "chached" selectors
        this.headerContainer = this.navContainer.children('li.ui-tabs-header');
        this.panelContainer = this.jq.children('.ui-tabs-panels');

        // select next tab
        var length = this.getLength();
        if(length > 0) {
            if(index < this.cfg.selected) {
                this.cfg.selected--;
            }
            else if(index === this.cfg.selected) {
                var newIndex = (this.cfg.selected === (length)) ? (this.cfg.selected - 1): this.cfg.selected,
                newPanelHeader = this.headerContainer.eq(newIndex);

                if(newPanelHeader.hasClass('ui-state-disabled')) {
                    var newHeader = this.headerContainer.filter(':not(.ui-state-disabled):first');
                    if(newHeader.length) {
                        this.select(newHeader.index(), true);
                    }
                }
                else {
                    this.select(newIndex, true);
                }
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
        var ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: panel.data('index')}
            ]
        };

        this.callBehavior('tabChange', ext);
    },

    fireTabCloseEvent: function(id, index) {
        if(this.hasBehavior('tabClose')) {
            var ext = {
                params: [
                    {name: this.id + '_closeTab', value: id},
                    {name: this.id + '_tabindex', value: index}
                ]
            };

            this.callBehavior('tabClose', ext);
        }
    },

    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },

    isLoaded: function(panel) {
        return panel.data('loaded') === true;
    },

    disable: function(index) {
        this.headerContainer.eq(index).addClass('ui-state-disabled');
    },

    enable: function(index) {
        this.headerContainer.eq(index).removeClass('ui-state-disabled');
    },

    postTabShow: function(newPanel) {
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel.index());
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }

});
