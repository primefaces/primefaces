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
 * @prop {number[]} cfg.active List of tabs that are currently active (open). Each item is a 0-based index of a tab.
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
 */
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.stateHolder = $(this.jqId + '_active');
        this.headers = this.jq.children('.ui-accordion-header');
        this.panels = this.jq.children('.ui-accordion-content');
        this.cfg.rtl = this.jq.hasClass('ui-accordion-rtl');
        this.cfg.expandedIcon = 'ui-icon-triangle-1-s';
        this.cfg.collapsedIcon = this.cfg.rtl ? 'ui-icon-triangle-1-w' : 'ui-icon-triangle-1-e';

        this.initActive();
        this.bindEvents();

        if(this.cfg.dynamic && this.cfg.cache) {
            this.markLoadedPanels();
        }
    },

    /**
     * Called when this accordion panel is initialized. Reads the selected panels from the saved state, see also
     * `saveState`.
     * @private
     */
    initActive: function() {
        var stateHolderVal = this.stateHolder.val();
        if (this.cfg.multiple) {
            this.cfg.active = [];

            if (stateHolderVal != null && stateHolderVal.length > 0) {
                var indexes = this.stateHolder.val().split(',');
                for(var i = 0; i < indexes.length; i++) {
                    this.cfg.active.push(parseInt(indexes[i]));
                }
            }
        }
        else if (stateHolderVal != null) {
            this.cfg.active = parseInt(this.stateHolder.val());
        }
    },

    /**
     * Binds all event listeners required by this accordion panel.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.headers.on("mouseover", function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')&&!element.hasClass('ui-state-disabled')) {
                element.addClass('ui-state-hover');
            }
        }).on("mouseout", function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')&&!element.hasClass('ui-state-disabled')) {
                element.removeClass('ui-state-hover');
            }
        }).on("click", function(e) {
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                var tabIndex = $this.headers.index(element);

                if(element.hasClass('ui-state-active')) {
                    $this.unselect(tabIndex);
                }
                else {
                    $this.select(tabIndex);
                    $(this).trigger('focus.accordion');
                }
            }

            e.preventDefault();
        });

        this.bindKeyEvents();
    },

    /**
     * Sets up all event listeners for keyboard interactions.
     * @private
     */
    bindKeyEvents: function() {
        this.headers.on('focus.accordion', function(){
            $(this).addClass('ui-tabs-outline');
        })
        .on('blur.accordion', function(){
            $(this).removeClass('ui-tabs-outline');
        })
        .on('keydown.accordion', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                $(this).trigger('click');
                e.preventDefault();
            }
        });
    },

    /**
     * Marks the currently active panels as loaded; their content does not need to be retrieved from the server anymore.
     * @private
     */
    markLoadedPanels: function() {
        if(this.cfg.multiple) {
            for(var i = 0; i < this.cfg.active.length; i++) {
                if(this.cfg.active[i] >= 0)
                    this.markAsLoaded(this.panels.eq(this.cfg.active[i]));
            }
        } else {
            if(this.cfg.active >= 0)
                this.markAsLoaded(this.panels.eq(this.cfg.active));
        }
    },

    /**
     * Activates (opens) the tab with given index. This may fail by returning `false`, such
     * as when a callback is registered that prevent the tab from being opened.
     * @param {number} index 0-based index of the tab to open. Must not be out of range.
     * @return {boolean} `true` when the given panel is now active, `false` otherwise. 
     */
    select: function(index) {
        var panel = this.panels.eq(index),
            header = panel.prev();

        // don't select already selected panel
        if (header.hasClass('ui-state-active')) {
            return;
        }

        //Call user onTabChange callback
        if(this.cfg.onTabChange) {
            var result = this.cfg.onTabChange.call(this, panel);
            if(result === false)
                return false;
        }

        var shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

        //update state
        if(this.cfg.multiple)
            this.addToSelection(index);
        else
            this.cfg.active = index;

        this.saveState();

        if(shouldLoad) {
            this.loadDynamicTab(panel);
        }
        else {
            if(this.cfg.controlled) {
                this.fireTabChangeEvent(panel);
            }
            else {
                this.show(panel);

                this.fireTabChangeEvent(panel);
            }

        }

        return true;
    },

    /**
     * Activates (opens) all the tabs if multiple mode is enabled and the first tab in single mode.
     */
    selectAll: function() {
        var $this = this;
        this.panels.each(function(index) {
            $this.select(index);
            if (!$this.cfg.multiple) {
                return false; // breaks
            }
        });
    },

    /**
     * Deactivates (closes) the tab with given index.
     * @param {number} index 0-based index of the tab to close. Must not be out of range.
     */
    unselect: function(index) {
        var panel = this.panels.eq(index),
            header = panel.prev();

        // don't unselect already unselected panel
        if (!header.hasClass('ui-state-active')) {
            return;
        }
        
        if(this.cfg.controlled) {
            this.fireTabCloseEvent(index);
        }
        else {
            this.hide(index);

            this.fireTabCloseEvent(index);
        }
    },

    /**
     * Deactivates (closes) all the tabs.
     */
    unselectAll: function() {
        var $this = this;
        this.panels.each(function(index) {
            $this.unselect(index);
        });
    },

    /**
     * Hides other panels and makes the given panel visible, such as by adding or removing the appropriate CSS classes.
     * @private
     * @param {JQuery} panel A tab panel to show.
     */
    show: function(panel) {
        var $this = this;

        //deactivate current
        if(!this.cfg.multiple) {
            var oldHeader = this.headers.filter('.ui-state-active');
            oldHeader.children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
            oldHeader.attr('aria-selected', false);
            oldHeader.attr('aria-expanded', false).removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all')
                .next().attr('aria-hidden', true).slideUp(function(){
                    if($this.cfg.onTabClose)
                        $this.cfg.onTabClose.call($this, panel);
                });
        }

        //activate selected
        var newHeader = panel.prev();
        newHeader.attr('aria-selected', true);
        newHeader.attr('aria-expanded', true).addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
                .children('.ui-icon').removeClass(this.cfg.collapsedIcon).addClass(this.cfg.expandedIcon);

        panel.attr('aria-hidden', false).slideDown('normal', function() {
            $this.postTabShow(panel);
        });
    },

    /**
     * Hides one of the panels of this accordion.
     * @private
     * @param {number} index 0-based index of the panel to hide.
     */
    hide: function(index) {
        var $this = this,
        panel = this.panels.eq(index),
        header = panel.prev();

        header.attr('aria-selected', false);
        header.attr('aria-expanded', false).children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
        header.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all');
        panel.attr('aria-hidden', true).slideUp(function(){
            if($this.cfg.onTabClose)
                $this.cfg.onTabClose.call($this, panel);
        });

        this.removeFromSelection(index);
        this.saveState();
    },

    /**
     * The content of a tab panel may be loaded dynamically on demand via AJAX. This method loads the content of the
     * given tab. Make sure to check first that this widget has got a dynamic tab panel (see
     * {@link AccordionPanelCfg.dynamic}) and that the given tab panel is not loaded already (see {@link isLoaded}).
     * @param {JQuery} panel A tab panel to load.
     */
    loadDynamicTab: function(panel) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            panel.html(content);

                            if(this.cfg.cache) {
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

        if(this.hasBehavior('tabChange')) {
            this.callBehavior('tabChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Handles the event listeners and behaviors when switching to a different tab.
     * @private
     * @param {JQueryStatic} panel The tab which is now active.
     */
    fireTabChangeEvent : function(panel) {
        if(this.hasBehavior('tabChange')) {
            var ext = {
                params: [
                    {name: this.id + '_newTab', value: panel.attr('id')},
                    {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
                ]
            };

            if(this.cfg.controlled) {
                var $this = this;
                ext.oncomplete = function(xhr, status, args, data) {
                    if(args.access && !args.validationFailed) {
                        $this.show(panel);
                    }
                };
            }

            this.callBehavior('tabChange', ext);
        }
    },

    /**
     * Handles the event listeners and behaviors when a tab was closed.
     * @private
     * @param {number} index 0-based index of the closed tab.
     */
    fireTabCloseEvent : function(index) {
        if(this.hasBehavior('tabClose')) {
            var panel = this.panels.eq(index),
            ext = {
                params: [
                    {name: this.id + '_tabId', value: panel.attr('id')},
                    {name: this.id + '_tabindex', value: parseInt(index)}
                ]
            };

            if(this.cfg.controlled) {
                var $this = this;
                ext.oncomplete = function(xhr, status, args, data) {
                    if(args.access && !args.validationFailed) {
                        $this.hide(index);
                    }
                };
            }

            this.callBehavior('tabClose', ext);
        }
    },

    /**
     * When loading tab content dynamically, marks the content as loaded.
     * @private
     * @param {JQuery} panel A panel of this accordion that was loaded.
     */
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },

    /**
     * The content of a tab panel may be loaded dynamically on demand via AJAX. This method checks whether the content
     * of a tab panel is currently loaded.
     * @param {JQuery} panel A tab panel to check.
     * @return {boolean} `true` if the content of the tab panel is loaded, `false` otherwise.
     */
    isLoaded: function(panel) {
        return panel.data('loaded') == true;
    },

    /**
     * Adds the given panel node to the list of currently selected nodes.
     * @private
     * @param {string} nodeId ID of a panel node.
     */
    addToSelection: function(nodeId) {
        this.cfg.active.push(nodeId);
    },

    /**
     * Removes the given panel node from the list of currently selected nodes.
     * @private
     * @param {string} nodeId ID of a panel node.
     */
    removeFromSelection: function(nodeId) {
        this.cfg.active = $.grep(this.cfg.active, function(r) {
            return r != nodeId;
        });
    },

    /**
     * Saves the current state of this widget, used for example to preserve the state during AJAX updates.
     * @private
     */
    saveState: function() {
        if(this.cfg.multiple)
            this.stateHolder.val(this.cfg.active.join(','));
        else
            this.stateHolder.val(this.cfg.active);
    },

    /**
     * Handles event listeners and behaviors when switching to a different tab.
     * @private
     * @param {JQuery} newPanel The new tab the is shown.
     */
    postTabShow: function(newPanel) {
        //Call user onTabShow callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel);
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }

});
