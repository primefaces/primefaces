/**
 * __PrimeFaces AccordionPanel Widget__
 *
 * The AccordionPanel is a container component that displays content in a stacked format.
 *
 * @prop {JQuery} headers The DOM elements for the header of each tab.
 * @prop {JQuery} panels The DOM elements for the content of each tab panel.
 * @prop {JQuery} stateHolder The DOM elements for the hidden input storing which panels are expanded and collapsed.
 *
 * @interface {PrimeFaces.widget.AccordionPanelCfg} cfg The configuration for the
 * {@link  AccordionPanel| AccordionPanel widget}. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {number[]} cfg.active List of tabs that are currently active (open). Each item is either a key or a 0-based index of a tab.
 * @prop {boolean} cfg.cache `true` if activating a dynamic tab should not load the contents from server again and use
 * the cached contents; or `false` if the caching is disabled.
 * @prop {string} cfg.collapsedIcon The icon class name for the collapsed icon.
 * @prop {boolean} cfg.controlled `true` if a tab controller was specified for this widget; or `false` otherwise. A tab
 * controller is a server side listener that decides whether a tab change or tab close should be allowed.
 * @prop {boolean} cfg.dynamic `true` if the contents of each panel are loaded on-demand via AJAX; `false` otherwise.
 * @prop {string} cfg.expandedIcon The icon class name for the expanded icon.
 * @prop {boolean} cfg.multiple `true` if multiple tabs may be open at the same time; or `false` if opening one tab
 * closes all other tabs.
 * @prop {boolean} cfg.rtl `true` if the current text direction `rtl` (right-to-left); or `false` otherwise.
 * @prop {boolean} cfg.multiViewState Whether to keep AccordionPanel state across views.
 * @prop {number} cfg.toggleSpeed Speed of toggling in milliseconds.
 * @prop {number} cfg.scrollIntoView Should the tab scroll into view. One of start, center, end, nearest, or NULL if disabled.
 */
PrimeFaces.widget.AccordionPanel = class AccordionPanel extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.stateHolder = $(this.jqId + '_active');
        this.headers = this.jq.children('.ui-accordion-header');
        this.panels = this.jq.children('.ui-accordion-content');
        this.cfg.rtl = this.jq.hasClass('ui-accordion-rtl');
        this.cfg.expandedIcon = 'ui-icon-triangle-1-s';
        this.cfg.collapsedIcon = this.cfg.rtl ? 'ui-icon-triangle-1-w' : 'ui-icon-triangle-1-e';

        this.initActive();
        this.bindEvents();

        if (this.cfg.dynamic && this.cfg.cache) {
            this.markLoadedPanels();
        }
    }

    /**
     * Called when this accordion panel is initialized. Reads the selected panels from the saved state, see also
     * `saveState`.
     * @private
     */
    initActive() {
        var stateHolderVal = this.stateHolder.val();
        if (this.cfg.multiple) {
            this.cfg.active = [];

            if (stateHolderVal != null && stateHolderVal.length > 0) {
                var activated = this.stateHolder.val().split(',');
                for (const active of activated) {
                    this.cfg.active.push(active);
                }
            }
        }
        else if (stateHolderVal != null) {
            this.cfg.active = this.stateHolder.val();
        }

        this.headers.each(function() {
            var containerId = PrimeFaces.escapeClientId(this.id.replace('_header', ''));
            if ($(containerId + ' .ui-state-error').length > 0 || $(containerId + ' .ui-message-error-detail').length > 0) {
                $(this).addClass('ui-state-error');
            } else {
                $(this).removeClass('ui-state-error');
            }
        });
    }

    /**
     * Binds all event listeners required by this accordion panel.
     * @private
     */
    bindEvents() {
        var $this = this;

        this.headers.on("mouseover", function() {
            var element = $(this);
            if (!element.hasClass('ui-state-active') && !element.hasClass('ui-state-disabled')) {
                element.addClass('ui-state-hover');
            }
        }).on("mouseout", function() {
            var element = $(this);
            if (!element.hasClass('ui-state-active') && !element.hasClass('ui-state-disabled')) {
                element.removeClass('ui-state-hover');
            }
        }).on("click", function(e) {
            var header = $(this);
            if (!header.hasClass('ui-state-disabled')) {
                var tabIndex = $this.headers.index(header);

                if (header.hasClass('ui-state-active')) {
                    $this.unselect(tabIndex);
                }
                else {
                    $this.select(tabIndex);
                    header.trigger('focus.accordion');

                    if ($this.cfg.scrollIntoView) {
                        // 300 ms delay is so the content actually exists that we are trying to scroll to
                        PrimeFaces.queueTask(function() {
                            header.next()[0].scrollIntoView({
                                behavior: "smooth",
                                block: $this.cfg.scrollIntoView,
                                inline: "center"
                            });
                        }, 300);
                    }
                }
            }

            e.preventDefault();
        });

        this.bindKeyEvents();
    }

    /**
     * Sets up all event listeners for keyboard interactions.
     * @private
     */
    bindKeyEvents() {
        var $this = this;
        this.headers.on({
            'focus.accordion': function() {
                $(this).addClass('ui-tabs-outline');
            },
            'blur.accordion': function() {
                $(this).removeClass('ui-tabs-outline');
            },
            'keydown.accordion': function(e) {
                var currentHeader = $(this);
                var index = 0;
                switch (e.code) {
                    case 'Enter':
                    case 'NumpadEnter':
                    case 'Space':
                        currentHeader.trigger('click');
                        e.preventDefault();
                        break;
                    case 'PageUp':
                    case 'Home':
                        $this.headers.first().trigger('focus');
                        e.preventDefault();
                        break;
                    case 'PageDown':
                    case 'End':
                        $this.headers.last().trigger('focus');
                        e.preventDefault();
                        break;
                    case 'ArrowDown':
                        index = $this.headers.index(currentHeader) + 1;
                        while (index < $this.headers.length && !$this.headers.eq(index).is(':visible')) {
                            index++;
                        }
                        if (index < $this.headers.length) {
                            $this.headers.eq(index).trigger('focus');
                        }
                        e.preventDefault();
                        break;
                        
                    case 'ArrowUp':
                        index = $this.headers.index(currentHeader) - 1;
                        while (index < $this.headers.length && !$this.headers.eq(index).is(':visible')) {
                            index--;
                        }
                        if (index >= 0) {
                            $this.headers.eq(index).trigger('focus');
                        }
                        e.preventDefault();
                        break;
                };
            }
        });
    }

    /**
     * Marks the currently active panels as loaded; their content does not need to be retrieved from the server anymore.
     * @private
     */
    markLoadedPanels() {
        const markPanelAsLoaded = (index) => {
            if (index >= 0) {
                this.markAsLoaded(this.panels.eq(index));
            }
        };

        if (this.cfg.multiple) {
            this.cfg.active.forEach(markPanelAsLoaded);
        } else {
            markPanelAsLoaded(this.cfg.active);
        }
    }

    /**
     * Activates (opens) the tab with given index. This may fail by returning `false`, such
     * as when a callback is registered that prevent the tab from being opened.
     * @param {number} index 0-based index of the tab to open. Must not be out of range.
     * @return {boolean} `true` when the given panel is now active, `false` otherwise. 
     */
    select(index) {
        var panel = this.panels.eq(index),
            header = panel.prev();

        // don't select already selected panel
        if (header.hasClass('ui-state-active')) {
            return;
        }

        //Call user onTabChange callback
        if (this.cfg.onTabChange) {
            var result = this.cfg.onTabChange.call(this, panel);
            if (result === false)
                return false;
        }

        var shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

        let key = header.attr('data-key') ?? index;

        //update state
        if (this.cfg.multiple)
            this.addToSelection(key);
        else
            this.cfg.active = key;

        this.saveState();

        if (shouldLoad) {
            this.loadDynamicTab(panel);
        }
        else if (this.cfg.controlled) {
            this.fireTabChangeEvent(panel);
        }
        else {
            this.show(panel);
            this.fireTabChangeEvent(panel);
        }

        return true;
    }

    /**
     * Activates (opens) all the tabs if multiple mode is enabled and the first tab in single mode.
     */
    selectAll() {
        var $this = this;
        this.panels.each(function(index) {
            $this.select(index);

            if (!$this.cfg.multiple) {
                return false; // breaks
            }
        });
    }

    /**
     * Deactivates (closes) the tab with given index.
     * @param {number} index 0-based index of the tab to close. Must not be out of range.
     */
    unselect(index) {
        var panel = this.panels.eq(index),
            header = panel.prev();

        // don't unselect already unselected panel
        if (!header.hasClass('ui-state-active')) {
            return;
        }

        if (!this.cfg.controlled) {
            this.hide(index);
        }

        this.fireTabCloseEvent(index);
    }

    /**
     * Deactivates (closes) all the tabs.
     */
    unselectAll() {
        var $this = this;
        this.panels.each(function(index) {
            $this.unselect(index);
        });
    }

    /**
     * Hides other panels and makes the given panel visible, such as by adding or removing the appropriate CSS classes.
     * @private
     * @param {JQuery} panel A tab panel to show.
     */
    show(panel) {
        var $this = this;

        //deactivate current
        if (!this.cfg.multiple) {
            var oldHeader = this.headers.filter('.ui-state-active');
            oldHeader.children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
            oldHeader.attr('aria-expanded', false).removeClass('ui-state-active')
                .next().attr('aria-hidden', true).slideUp(this.cfg.toggleSpeed, function() {
                    if ($this.cfg.onTabClose)
                        $this.cfg.onTabClose.call($this, panel);
                });
        }

        //activate selected
        var newHeader = panel.prev();
        newHeader.attr('aria-expanded', true).addClass('ui-state-active').removeClass('ui-state-hover')
            .children('.ui-icon').removeClass(this.cfg.collapsedIcon).addClass(this.cfg.expandedIcon);

        panel.attr('aria-hidden', false).slideDown(this.cfg.toggleSpeed, function() {
            $this.postTabShow(panel);
        });
    }

    /**
     * Hides one of the panels of this accordion.
     * @private
     * @param {number} index 0-based index of the panel to hide.
     */
    hide(index) {
        var $this = this,
            panel = this.panels.eq(index),
            header = panel.prev();

        header.attr('aria-expanded', false).children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
        header.removeClass('ui-state-active');
        panel.attr('aria-hidden', true).slideUp(this.cfg.toggleSpeed, function() {
            if ($this.cfg.onTabClose)
                $this.cfg.onTabClose.call($this, panel);
        });

        let key = header.attr('data-key') ?? index;

        this.removeFromSelection(key);
        this.saveState();
    }

    /**
     * The content of a tab panel may be loaded dynamically on demand via AJAX. This method loads the content of the
     * given tab. Make sure to check first that this widget has got a dynamic tab panel (see
     * {@link AccordionPanelCfg.dynamic}) and that the given tab panel is not loaded already (see {@link isLoaded}).
     * @param {JQuery} panel A tab panel to load.
     */
    loadDynamicTab(panel) {
        var $this = this,
            options = {
                source: this.id,
                process: this.id,
                update: this.id,
                ignoreAutoUpdate: true,
                params: [
                    { name: this.id + '_contentLoad', value: true },
                    { name: this.id + '_currentTab', value: panel.attr('id') },
                    { name: this.id + '_tabindex', value: parseInt(panel.index() / 2) },
                    { name: this.id + '_active', value: this.cfg.active } // #13551: normally set through hidden state input but necessary here for accordion not wrapped in form
                ],
                onsuccess: function(responseXML, status, xhr) {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            panel.html(content);

                            if (this.cfg.cache) {
                                this.markAsLoaded(panel);
                            }
                        }
                    });

                    return true;
                },
                oncomplete: function() {
                    $this.show(panel);
                }
            };

        if (this.hasBehavior('tabChange')) {
            this.callBehavior('tabChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Handles the event listeners and behaviors when switching to a different tab.
     * @private
     * @param {JQueryStatic} panel The tab which is now active.
     */
    fireTabChangeEvent(panel) {
        if (this.hasBehavior('tabChange')) {
            var ext = {
                params: [
                    { name: this.id + '_currentTab', value: panel.attr('id') },
                    { name: this.id + '_tabindex', value: parseInt(panel.index() / 2) }
                ]
            };

            if (this.cfg.controlled) {
                var $this = this;
                ext.oncomplete = function(xhr, status, args, data) {
                    if (args.access && !args.validationFailed) {
                        $this.show(panel);
                    }
                };
            }

            this.callBehavior('tabChange', ext);
        }
        else if (this.cfg.multiViewState) {
            var options = {
                source: this.id,
                partialSubmit: true,
                process: this.id,
                ignoreAutoUpdate: true,
                global: false,
                params: [
                    { name: this.id + '_skipChildren', value: true },
                    { name: this.id + '_currentTab', value: panel.attr('id') },
                    { name: this.id + '_tabindex', value: parseInt(panel.index() / 2) }
                ]
            };

            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Handles the event listeners and behaviors when a tab was closed.
     * @private
     * @param {number} index 0-based index of the closed tab.
     */
    fireTabCloseEvent(index) {
        var panel = this.panels.eq(index);
        if (this.hasBehavior('tabClose')) {
            var ext = {
                params: [
                    { name: this.id + '_currentTab', value: panel.attr('id') },
                    { name: this.id + '_tabindex', value: parseInt(index) }
                ]
            };

            if (this.cfg.controlled) {
                var $this = this;
                ext.oncomplete = function(xhr, status, args, data) {
                    if (args.access && !args.validationFailed) {
                        $this.hide(index);
                    }
                };
            }

            this.callBehavior('tabClose', ext);
        }
        else if (this.cfg.multiViewState) {
            var options = {
                source: this.id,
                partialSubmit: true,
                process: this.id,
                ignoreAutoUpdate: true,
                global: false,
                params: [
                    { name: this.id + '_skipChildren', value: true },
                    { name: this.id + '_currentTab', value: panel.attr('id') },
                    { name: this.id + '_tabindex', value: parseInt(index) }
                ]
            };

            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * When loading tab content dynamically, marks the content as loaded.
     * @private
     * @param {JQuery} panel A panel of this accordion that was loaded.
     */
    markAsLoaded(panel) {
        panel.data('loaded', true);
    }

    /**
     * The content of a tab panel may be loaded dynamically on demand via AJAX. This method checks whether the content
     * of a tab panel is currently loaded.
     * @param {JQuery} panel A tab panel to check.
     * @return {boolean} `true` if the content of the tab panel is loaded, `false` otherwise.
     */
    isLoaded(panel) {
        return panel.data('loaded') === true;
    }

    /**
     * Adds the given panel node to the list of currently selected nodes.
     * @private
     * @param {string} nodeId ID of a panel node.
     */
    addToSelection(nodeId) {
        this.cfg.active.push(nodeId);
    }

    /**
     * Removes the given panel node from the list of currently selected nodes.
     * @private
     * @param {string} nodeId ID of a panel node.
     */
    removeFromSelection(nodeId) {
        this.cfg.active = $.grep(this.cfg.active, function(r) {
            return r != nodeId;
        });
    }

    /**
     * Saves the current state of this widget, used for example to preserve the state during AJAX updates.
     * @private
     */
    saveState() {
        if (this.cfg.multiple)
            this.stateHolder.val(this.cfg.active.join(','));
        else
            this.stateHolder.val(this.cfg.active);
    }

    /**
     * Handles event listeners and behaviors when switching to a different tab.
     * @private
     * @param {JQuery} newPanel The new tab the is shown.
     */
    postTabShow(newPanel) {
        //Call user onTabShow callback
        if (this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel);
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }
}