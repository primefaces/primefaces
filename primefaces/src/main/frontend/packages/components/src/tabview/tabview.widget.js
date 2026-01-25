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
 * @prop {JQuery} [firstTab] The DOM element for the first tab.
 * @prop {null} focusedTabHeader Always `null`.
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
 * @prop {boolean} cfg.focusOnError Whether to focus the first tab that has an error associated to it.
 * @prop {boolean} cfg.focusOnLastActiveTab Whether to focus on the last active tab that a user selected.
 */
PrimeFaces.widget.TabView = class TabView extends PrimeFaces.widget.DeferredWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.panelContainer = this.jq.children('.ui-tabs-panels');
        this.stateHolder = $(this.jqId + '_activeIndex');
        this.cfg.selected = parseInt(this.stateHolder.val());
        this.focusedTabHeader = null;
        this.tabindex = this.cfg.tabindex||0;
        this.cfg.focusOnError = this.cfg.focusOnError || false;
        this.cfg.focusOnLastActiveTab = this.cfg.focusOnLastActiveTab || false;

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
    }

    /**
     * @override
     * @inheritdoc
     */
    renderDeferred() {
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
    }

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render() {
        if(this.cfg.scrollable) {
            this.initScrolling();

            var $this = this;

            PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', this.jq, function() {
                $this.initScrolling();
            });
        }
    }
    
   /**
     * @override
     * @inheritdoc
     */
    destroy() {
        super.destroy();
        if (PrimeFaces.env.isTouchable(this.cfg)) {
            this.jq.swipe('destroy');
        }
    }

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents() {
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
                            element.find('a').trigger('focus.tabview');
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
        this.bindRefreshListener();
    }

    /**
     * Binds swipe events to this tabview.
     * @private
     */
    bindSwipeEvents() {
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
    }

    /**
     * Sets up all keyboard related event listeners that are required by this widget.
     * @private
     */
    bindKeyEvents() {
        var $this = this,
            tabs = this.headerContainer;

        /* For Screen Reader and Keyboard accessibility */
        tabs = tabs.not('.ui-state-disabled').find('a');
        tabs.attr('tabindex', this.tabindex);

        tabs.on('focus.tabview', function(e) {
            var focusedTab = $(this).parent();

            if(!focusedTab.hasClass('ui-state-disabled')) {
                focusedTab.addClass('ui-tabs-outline');

                if($this.cfg.scrollable) {
                    if($this.navcrollerRight.is(':visible') && (focusedTab.position().left + focusedTab.width() > $this.navcrollerRight.position().left)) {
                        $this.navcrollerRight.trigger('click.tabview');
                    }
                    else if($this.navcrollerLeft.is(':visible') && (focusedTab.position().left < $this.navcrollerLeft.position().left)) {
                        $this.navcrollerLeft.trigger('click.tabview');
                    }
                }
            }
        })
        .on('blur.tabview', function(){
            $(this).parent().removeClass('ui-tabs-outline');
        })
        .on('keydown.tabview', function(e) {
            var element = $(this).parent();
            switch (e.code) {
                case 'ArrowRight':
                    var nextTab = element.nextAll('.ui-tabs-header:not(.ui-state-disabled)');
                    if (nextTab.length) {
                        nextTab.first().find('a').trigger('focus.tabview');
                    }
                    e.preventDefault();
                    break;
                case 'ArrowLeft':
                    var prevTab = element.prevAll('.ui-tabs-header:not(.ui-state-disabled)');
                    if (prevTab.length) {
                        prevTab.first().find('a').trigger('focus.tabview');
                    }
                    e.preventDefault();
                    break;
                case 'NumpadEnter':
                case 'Enter':
                case 'Space':
                    if (!element.hasClass('ui-state-disabled')) {
                        $this.select(element.data('index'));
                        e.preventDefault();
                    }
                    break;
                case 'Home':
                case 'PageUp':
                    $this.headerContainer.first().find('a').trigger('focus.tabview');
                    e.preventDefault();
                    break;
                case 'End':
                case 'PageDown':
                    $this.headerContainer.last().find('a').trigger('focus.tabview');
                    e.preventDefault();
                    break;
            }
        });

        //Scrolling
        if(this.cfg.scrollable) {
            this.navcrollerLeft.on('keydown.tabview', function(e) {
                if (PrimeFaces.utils.isActionKey(e)) {
                    $this.scroll(100);
                    e.preventDefault();
                }
            });

            this.navcrollerRight.on('keydown.tabview', function(e) {
                if (PrimeFaces.utils.isActionKey(e)) {
                    $this.scroll(-100);
                    e.preventDefault();
                }
            });
        }
    }

    /**
     * Binds refresh listener to update error highlighting or restore the last active tab on component udpate.
     * @private
     */
    bindRefreshListener() {
        var $this = this;
        var focusIndex = -1;
        this.addRefreshListener(function() {
			
            // update error highlighting and set focusIndex
            $(this.jqId + '>ul>li.ui-tabs-header').each(function() {
                var tabId = $('a', this).attr('href').slice(1);
                tabId = PrimeFaces.escapeClientId(tabId);
                if ($(tabId + ' .ui-state-error').length > 0 || $(tabId + ' .ui-message-error-detail').length > 0) {
                    $(this).addClass('ui-state-error');
                    if (focusIndex < 0) {
                        focusIndex = $(this).data('index');
                    }
                } else {
                    $(this).removeClass('ui-state-error');
                }
            });
            
            // set focusIndex to restore the last active tab
            // "focusOnError" always takes precedence over "focusOnLastActiveTab"
            if (focusIndex < 0 || !$this.cfg.focusOnError) {
                    focusIndex = $this.cfg.selected;
            }

            if (($this.cfg.focusOnError || $this.cfg.focusOnLastActiveTab) && focusIndex >= 0) {
               PrimeFaces.queueTask(function () {$this.select(focusIndex, true)}, 10);
            }
        });
    }

    /**
     * Sets up the classes and attributes required for scrolling the tab navigation bar.
     * @private
     */
    initScrolling() {
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
    }

    /**
     * Scrolls the tab navigation bar by the given amount.
     * @param {number} step Amount to scroll the navigation bar, positive to scroll to the right, negative to scroll to
     * the left.
     */
    scroll(step) {
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
        else if(newMarginLeft <= 0) {
            this.navContainer.animate({'margin-left': newMarginLeft + 'px'}, 'fast', 'easeInOutCirc', function() {
                $this.saveScrollState(newMarginLeft);

                if(newMarginLeft === 0)
                    $this.disableScrollerButton($this.navcrollerLeft);
                if($this.navcrollerRight.hasClass('ui-state-disabled'))
                    $this.enableScrollerButton($this.navcrollerRight);
            });
        }
    }

    /**
     * Disables the buttons for scrolling the contents of the navigation bar.
     * @param {JQuery} btn The scroll button to enable.
     */
    disableScrollerButton(btn) {
        btn.addClass('ui-state-disabled').removeClass('ui-state-hover ui-state-active ui-state-focus').attr('tabindex', -1);
    }

    /**
     * Enables the buttons for scrolling the contents of the navigation bar.
     * @param {JQuery} btn The scroll button to enable.
     */
    enableScrollerButton(btn) {
        btn.removeClass('ui-state-disabled').attr('tabindex', this.tabindex);
    }

    /**
     * Stores the current scroll position in a hidden input field, called before an AJAX request.
     * @private
     * @param {number} value The scroll position to be saved.
     */
    saveScrollState(value) {
        this.scrollStateHolder.val(value);
    }

    /**
     * Restores the current scroll position in a hidden input field, called after an AJAX request.
     * @private
     */
    restoreScrollState() {
        var value = parseInt(this.scrollStateHolder.val());
        if(value === 0) {
            this.disableScrollerButton(this.navcrollerLeft);
        }

        this.navContainer.css('margin-left', this.scrollStateHolder.val() + 'px');
    }

    /**
     * Selects the given tab, if it is not selected already.
     * @param {number} index 0-based index of the tab to select.
     * @param {boolean} [silent] Controls whether events are triggered.
     * @return {boolean} Whether the given tab is now selected.
     */
    select(index, silent) {
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
                        partialSubmitFilter: PrimeFaces.escapeClientId(this.id + '_activeIndex'),
                        process: this.id,
                        ignoreAutoUpdate: true,
                        global: false,
                        params: [
                            {name: this.id + '_skipChildren', value: true}
                        ]
                    };

                    PrimeFaces.ajax.Request.handle(options);
                }
            }
        }

        return true;
    }

    /**
     * After a tab was loaded from the server, prepares the given tab and shows it.
     * @private
     * @param {JQuery} newPanel New tab to be shown.
     */
    show(newPanel) {
        var oldPanel = this.panelContainer.children('.ui-tabs-panel:visible');
        
        // it is possible the current panel has been removed from the DOM with rendered flag
        if (!newPanel || newPanel.length === 0) {
            newPanel = oldPanel;
        }
        
        var headers = this.headerContainer,
        actionsQuery = '.ui-tabs-actions:not(.ui-tabs-actions-global)',
        oldHeader = headers.filter('.ui-state-active'),
        oldActions = oldHeader.next(actionsQuery),
        hasOldActions = oldActions.length,
        newHeader = headers.eq(newPanel.index()),
        newActions = newHeader.next(actionsQuery),
        hasNewActions = newActions.length,
        globalActions = this.navContainer.children('.ui-tabs-actions.ui-tabs-actions-global'),
        $this = this;

        globalActions.hide();
        if(!hasNewActions){
            newActions = globalActions;
            hasNewActions = newActions.length;
        }

        //aria
        oldPanel.attr('aria-hidden', true);
        oldPanel.addClass('ui-helper-hidden');
        oldHeader.find('a').attr('aria-expanded', false);
        oldHeader.find('a').attr('aria-selected', false);
        if(hasOldActions) {
            oldActions.attr('aria-hidden', true);
        }

        newPanel.attr('aria-hidden', false);
        newPanel.removeClass('ui-helper-hidden');
        newHeader.find('a').attr('aria-expanded', true);
        newHeader.find('a').attr('aria-selected', true);
        if(hasNewActions) {
            newActions.attr('aria-hidden', false);
        }

        if(this.cfg.effect && oldPanel.length) {
            oldPanel.hide(this.cfg.effect, null, this.cfg.effectDuration, function() {
                oldHeader.removeClass('ui-tabs-selected ui-state-active');
                if(hasOldActions) {
                    oldActions.hide($this.cfg.effect, null, $this.cfg.effectDuration);
                }

                newHeader.addClass('ui-tabs-selected ui-state-active');
                newPanel.show($this.cfg.effect, null, $this.cfg.effectDuration, function() {
                    $this.postTabShow(newPanel);
                });
                if(hasNewActions) {
                    newActions.show($this.cfg.effect, null, $this.cfg.effectDuration);
                }
            });
        }
        else {
            oldHeader.removeClass('ui-tabs-selected ui-state-active');
            oldPanel.hide();
            if(hasOldActions) {
                oldActions.hide();
            }

            newHeader.addClass('ui-tabs-selected ui-state-active');
            newPanel.show();
            if(hasNewActions) {
                newActions.show();
            }

            this.postTabShow(newPanel);
        }
    }

    /**
     * Dynamically loads contents of a tab from the server via AJAX.
     * @private
     * @param {JQuery} newPanel The tab whose content needs to be loaded.
     */
    loadDynamicTab(newPanel) {
        var $this = this,
        tabIndex = newPanel.data('index'),
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            ignoreAutoUpdate: true,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_currentTab', value: newPanel.attr('id')},
                {name: this.id + '_tabindex', value: tabIndex}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            // #8994 get new tab reference in case AJAX update removed the old one from DOM
                            var updatedTab = $this.panelContainer.children().eq(tabIndex);
                            if($this.cfg.effect) {
                                // hide first, otherwise it will be displayed after replacing the content with .html()
                                updatedTab.hide();
                            }
                            updatedTab.html(content);

                            if($this.cfg.cache) {
                                $this.markAsLoaded(updatedTab);
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                // #8994 get new tab reference in case AJAX update removed the old one from DOM
                var updatedTab = $this.panelContainer.children().eq(tabIndex);
                $this.show(updatedTab);
            }
        };

        if(this.hasBehavior('tabChange')) {
            this.callBehavior('tabChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Closes the tab at the given index.
     * @param {number} index 0-based index of the tab to close.
     */
    remove(index) {
        var header = this.headerContainer.eq(index),
        panel = this.panelContainer.children().eq(index);

        // remove and cleanse all traces of the header and contents of the panel
        PrimeFaces.utils.cleanseDomElement(header);
        PrimeFaces.utils.cleanseDomElement(panel);
        
        // refresh "cached" selectors
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
    }

    /**
     * Fins the number of tabs of this tab view.
     * @return {number} The number of tabs.
     */
    getLength() {
        return this.headerContainer.length;
    }

    /**
     * Finds and returns the tab that is currently selected.
     * @return {number} The 0-based index of the currently selected tab.
     */
    getActiveIndex() {
        return this.cfg.selected;
    }

    /**
     * Calls the appropriate behaviors when a different tab was selected.
     * @private
     * @param {JQuery} panel The tab that was selected.
     */
    fireTabChangeEvent(panel) {
        var ext = {
            params: [
                {name: this.id + '_currentTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: panel.data('index')}
            ]
        };

        this.callBehavior('tabChange', ext);
    }

    /**
     * Calls the appropriate behaviors when a tab was closed.
     * @private
     * @param {string} id Client ID of the tab that was closed.
     * @param {number} index 0-based index of the tab that was closed.
     */
    fireTabCloseEvent(id, index) {
        if(this.hasBehavior('tabClose')) {
            var ext = {
                params: [
                    {name: this.id + '_currentTab', value: id},
                    {name: this.id + '_tabindex', value: index}
                ]
            };

            this.callBehavior('tabClose', ext);
        }
    }

    /**
     * Reloads a dynamic tab even if it has already been loaded once. Forces an AJAX refresh of the tab.
     * @param {number} index 0-based index of the tab to reload.
     */
    reload(index) {
        var reloadPanel = this.panelContainer.children().eq(index);
        this.markAsUnloaded(reloadPanel);
        this.select(index);
    }

    /**
     * Marks the content of the given tab as loaded.
     * @private
     * @param {JQuery} panel A panel with content that was loaded.
     */
    markAsLoaded(panel) {
        panel.data('loaded', true);
    }

    /**
     * Marks the content of the given tab as unloaded.
     * @private
     * @param {JQuery} panel A panel with content that was unloaded.
     */
    markAsUnloaded(panel) {
        panel.data('loaded', false);
    }

    /**
     * If the content of the tab is loaded dynamically via AJAX, checks if the content was loaded already.
     * @private
     * @param {JQuery} panel A panel to check.
     * @return {boolean} Whether the content of the given panel was loaded from the server.
     */
    isLoaded(panel) {
        return panel.data('loaded') === true;
    }

    /**
     * Disables the tab at the given index. Disabled tabs may not be selected.
     * @param {number} [index] 0-based index of the tab to disable. Disables all tabs when omitted.
     */
    disable(index) {
        const target = index !== null && index !== undefined ? this.headerContainer.eq(index) : this.headerContainer;
        target.addClass('ui-state-disabled').find('a').attr('tabindex', '-1');
    }

    /**
     * Enables the tab at the given index. Enabled tabs may be selected.
     * @deprecated Use {@link enableTab} (where the argument is non-optional).
     * @param {number} [index] 0-based index of the tab to enable. Enables all tabs when omitted.
     */
    enable(index) {
        const target = index !== null && index !== undefined ? this.headerContainer.eq(index) : this.headerContainer; 
        target.removeClass('ui-state-disabled').find('a').attr('tabindex', this.tabindex);
    }

    /**
     * Callback that is invoked after a tab was shown.
     * @private
     * @param {JQuery} newPanel The panel with the content of the tab.
     */
    postTabShow(newPanel) {
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel.index());
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }

    /**
     * Selects the tab with the given ID. The ID can be a full client ID (e.g., "form:tabView:tabFoo")
     * or a partial ID relative to this tab view (e.g., "tabFoo" or ":tabFoo").
     * @param {string | HTMLElement | JQuery | undefined | null} tabId Client ID of the tab to select.
     * @param {boolean} [silent=false] Controls whether events are triggered. When `true`, the
     * `onTabChange` callback and AJAX events are not fired.
     * @return {boolean} `true` if the tab was found and selected, `false` otherwise.
     */
    selectTabById(tabId, silent) {
        if (!tabId) {
            return false;
        }

        const tabPanel = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, tabId);
        if (tabPanel.length === 0) {
            return false;
        }

        // Get the index of the panel within the panelContainer
        const tabIndex = this.panelContainer.children().index(tabPanel);
        if (tabIndex === -1) {
            return false;
        }

        return this.select(tabIndex, silent);
    }

}
