/**
 * __PrimeFaces TabView Widget__
 *
 * TabView is a container component to group content in tabs.
 *
 * @typedef PrimeFaces.widget.TabView.OnTabChangeCallback Client side callback to execute when a tab is clicked. If the
 * callback returns `false`, the tab is not selected. See also {@link TabViewCfg.onTabChange}.
 * @this {PrimeFaces.widget.TabView} PrimeFaces.widget.TabView.OnTabChangeCallback
 * @param {number} PrimeFaces.widget.TabView.OnTabChangeCallback.index 0-based index of the tab that is about to be
 * selected.
 * @return {boolean} PrimeFaces.widget.TabView.OnTabChangeCallback `true` to switch to the tab, `false` to stay at the
 * current tab.
 *
 * @typedef PrimeFaces.widget.TabView.OnTabCloseCallback Client side callback to execute on tab close. When the callback
 * returns `false`, the tab is not closed. See also {@link TabViewCfg.onTabClose}.
 * @this {PrimeFaces.widget.TabView} PrimeFaces.widget.TabView.OnTabCloseCallback
 * @param {number} PrimeFaces.widget.TabView.OnTabCloseCallback.index 0-based index of the tab that is about to be
 * closed.
 * @return {boolean} PrimeFaces.widget.TabView.OnTabCloseCallback `true` to close the tab, `false` to keep the tab
 * open.
 *
 * @typedef PrimeFaces.widget.TabView.OnTabShowCallback Client side callback to execute when a tab is shown. See also
 * {@link TabViewCfg.onTabShow}.
 * @this {PrimeFaces.widget.TabView} PrimeFaces.widget.TabView.OnTabShowCallback
 * @param {number} PrimeFaces.widget.TabView.OnTabShowCallback.index 0-based index of the tab that was
 * shown.
 *
 * @prop {JQuery} firstTab The DOM element for the first tab.
 * @prop {JQuery} focusedTab The DOM element for the tab that is currently focused.
 * @prop {JQuery} headerContainer The DOM element for the container element with the tab header.
 * @prop {JQuery} lastTab The DOM element for the last tab.
 * @prop {JQuery} navscroller The DOM element for the tab navigation bar.
 * @prop {JQuery} navcrollerLeft The DOM element for the button that scrolls the tab navigation bar to the left.
 * @prop {JQuery} navcrollerRight The DOM element for the button that scrolls the tab navigation bar to the right.
 * @prop {JQuery} navContainer The DOM element for the container element with the tab navigation bar.
 * @prop {JQuery} panelContainer The DOM element for the panel with the tab's contents.
 * @prop {JQuery} scrollStateHolder The DOM element for the hidden input field storing the current scroll position.
 * @prop {JQuery} stateHolder The DOM element for the hidden input field storing which is tab is active and visible.
 * @prop {number} tabindex Position of the element in the tabbing order.
 *
 * @interface {PrimeFaces.widget.TabViewCfg} cfg The configuration for the {@link  TabView| TabView widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {boolean} cfg.cache When tab contents are lazy loaded via AJAX toggle mode, caching only retrieves the tab
 * contents once and subsequent toggles of a cached tab does not communicate with server. If caching is turned off, tab
 * contents are reloaded from server each time tab is clicked.
 * @prop {boolean} cfg.dynamic Enables lazy loading of inactive tabs.
 * @prop {string} cfg.effect Name of the transition effect.
 * @prop {number} cfg.effectDuration Duration of the transition effect.
 * @prop {PrimeFaces.widget.TabView.OnTabChangeCallback} cfg.onTabChange Client side callback to execute when a tab is
 * clicked. If the callback returns `false`, the tab is not selected.
 * @prop {PrimeFaces.widget.TabView.OnTabCloseCallback} cfg.onTabClose Client side callback to execute on tab close.
 * When the callback returns `false`, the tab is not closed.
 * @prop {PrimeFaces.widget.TabView.OnTabShowCallback} cfg.onTabShow Client side callback to execute when a tab is
 * shown.
 * @prop {boolean} cfg.scrollable When enabled, tab headers can be scrolled horizontally instead of wrapping.
 * @prop {number} cfg.selected The currently selected tab.
 * @prop {number} cfg.tabindex Position of the element in the tabbing order.
 * @prop {boolean} cfg.multiViewState Whether to keep TabView state across views.
 */
PrimeFaces.widget.TabView = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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

    /**
     * @override
     * @inheritdoc
     */
    renderDeferred: function() {
        if(this.jq.is(':visible')) {
            this._render();
        }
        else if (this.jq.parent()[0]) {
            var container = this.jq.parent()[0].closest('.ui-hidden-container');
            if (container) {
                var $container = $(container);
                if ($container.length) {
                    var $this = this;
                    this.addDeferredRender(this.id, $container, function() {
                        return $this.render();
                    });
                }
            }
        }
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        if(this.cfg.scrollable) {
            this.initScrolling();

            var $this = this;

            PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', null, function() {
                $this.initScrolling();
            });
        }
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

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

        this.bindSwipeEvents();
        this.bindKeyEvents();
    },

    /**
     * Binds swipe events to this tabview.
     * @private
     */
    bindSwipeEvents: function() {
        if (!PrimeFaces.env.isTouchable(this.cfg)) {
            return;
        }
        var $this = this;
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
            excludedElements: PrimeFaces.utils.excludedSwipeElements()
        });
    },

   /**
    * Sets up all keyboard related event listeners that are required by this widget.
    * @private
    */
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

    /**
     * Sets up the classes and attributes required for scrolling the tab navigation bar.
     * @private
     */
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

    /**
     * Scrolls the tab navigation bar by the given amount.
     * @param {number} step Amount to scroll the navigation bar, positive to scroll to the right, negative to scroll to
     * the left.
     */
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

    /**
     * Disables the buttons for scrolling the contents of the navigation bar.
     * @param {JQuery} btn The scroll button to enable.
     */
    disableScrollerButton: function(btn) {
        btn.addClass('ui-state-disabled').removeClass('ui-state-hover ui-state-active ui-state-focus').attr('tabindex', -1);
    },

    /**
     * Enables the buttons for scrolling the contents of the navigation bar.
     * @param {JQuery} btn The scroll button to enable.
     */
    enableScrollerButton: function(btn) {
        btn.removeClass('ui-state-disabled').attr('tabindex', this.tabindex);
    },

    /**
     * Stores the current scroll position in a hidden input field, called before an AJAX request.
     * @private
     * @param {number} value The scroll position to be saved.
     */
    saveScrollState: function(value) {
        this.scrollStateHolder.val(value);
    },

    /**
     * Restores the current scroll position in a hidden input field, called after an AJAX request.
     * @private
     */
    restoreScrollState: function() {
        var value = parseInt(this.scrollStateHolder.val());
        if(value === 0) {
            this.disableScrollerButton(this.navcrollerLeft);
        }

        this.navContainer.css('margin-left', this.scrollStateHolder.val() + 'px');
    },

    /**
     * Selects the given tab, if it is not selected already.
     * @param {number} index 0-based index of the tab to select.
     * @param {boolean} [silent] Controls whether events are triggered.
     * @return {boolean} Whether the given tab is now selected.
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

            if (!silent) {
                if (this.hasBehavior('tabChange')) {
                    this.fireTabChangeEvent(newPanel);
                }
                else if (this.cfg.multiViewState) {
                    var options = {
                        source: this.id,
                        partialSubmit: true,
                        partialSubmitFilter: this.id + '_activeIndex',
                        process: this.id,
                        ignoreAutoUpdate: true,
                        params: [
                            {name: this.id + '_activeIndex', value: this.getActiveIndex()}
                        ]
                    };

                    PrimeFaces.ajax.Request.handle(options);
                }
            }
        }

        return true;
    },

    /**
     * After a tab was loaded from the server, prepares the given tab and shows it.
     * @private
     * @param {JQuery} newPanel New tab to be shown.
     */
    show: function(newPanel) {
        var headers = this.headerContainer,
        oldHeader = headers.filter('.ui-state-active'),
        oldActions = oldHeader.next('.ui-tabs-actions'),
        newHeader = headers.eq(newPanel.index()),
        newActions = newHeader.next('.ui-tabs-actions'),
        oldPanel = this.panelContainer.children('.ui-tabs-panel:visible'),
        $this = this;

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
                    oldActions.hide($this.cfg.effect, null, $this.cfg.effectDuration);
                }

                newHeader.addClass('ui-tabs-selected ui-state-active');
                newPanel.show($this.cfg.effect, null, $this.cfg.effectDuration, function() {
                    $this.postTabShow(newPanel);
                });
                if(newActions.length != 0) {
                    newActions.show($this.cfg.effect, null, $this.cfg.effectDuration);
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
     * Dynamically loads contents of a tab from the server via AJAX.
     * @private
     * @param {JQuery} newPanel The tab whose content needs to be loaded.
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
                            // hide first
                            // otherwise it will already be displayed after replacing the content with .html()
                            if($this.cfg.effect) {
                                newPanel.hide();
                            }
                            newPanel.html(content);

                            if($this.cfg.cache) {
                                $this.markAsLoaded(newPanel);
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
     * Closes the tab at the given index.
     * @param {number} index 0-based index of the tab to close.
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

    /**
     * Fins the number of tabs of this tab view.
     * @return {number} The number of tabs.
     */
    getLength: function() {
        return this.navContainer.children().length;
    },

    /**
     * Finds and returns the tab that is currently selected.
     * @return {number} The 0-based index of the currently selected tab.
     */
    getActiveIndex: function() {
        return this.cfg.selected;
    },

    /**
     * Calls the appropriate behaviors when a different tab was selected.
     * @private
     * @param {JQuery} panel The tab that was selected.
     */
    fireTabChangeEvent: function(panel) {
        var ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: panel.data('index')}
            ]
        };

        this.callBehavior('tabChange', ext);
    },

    /**
     * Calls the appropriate behaviors when a tab was closed.
     * @private
     * @param {string} id Client ID of the tab that was closed.
     * @param {number} index 0-based index of the tab that was closed.
     */
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

    /**
     * Marks the content of the given tab as loaded.
     * @private
     * @param {JQuery} panel A panel with content that was loaded.
     */
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },

    /**
     * If the content of the tab is loaded dynamically via AJAX, checks if the content was loaded already.
     * @private
     * @param {JQuery} panel A panel to check.
     * @return {boolean} Whether the content of the given panel was loaded from the server.
     */
    isLoaded: function(panel) {
        return panel.data('loaded') === true;
    },

    /**
     * Disables the tab at the given index. Disabled tabs may not be selected.
     * @param {number} index 0-based index of the tab to disable.
     */
    disable: function(index) {
        this.headerContainer.eq(index).addClass('ui-state-disabled');
    },

    /**
     * Enables the tab at the given index. Enabled tabs may be selected.
     * @param {number} index 0-based index of the tab to enable.
     */
    enable: function(index) {
        this.headerContainer.eq(index).removeClass('ui-state-disabled');
    },

    /**
     * Callback that is invoked after a tab was shown.
     * @private
     * @param {JQuery} newPanel The panel with the content of the tab.
     */
    postTabShow: function(newPanel) {
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel.index());
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }

});
