/**
 * __PrimeFaces SelectOneMenu Widget__
 *
 * SelectOneMenu is an extended version of the standard SelectOneMenu.
 *
 * @typedef {"slow" | "normal" | "fast"} PrimeFaces.widget.SelectOneMenu.EffectSpeed Duration of toggle animation of the
 * overlay panel.
 *
 * @typedef {"startsWith" |  "contains" |  "endsWith" | "custom"} PrimeFaces.widget.SelectOneMenu.FilterMatchMode
 * Available modes for filtering the options of a select list box. When `custom` is set, a `filterFunction` must be
 * specified.
 *
 * @typedef PrimeFaces.widget.SelectOneMenu.FilterFunction A function for filtering the options of a select list box.
 * @param {string} PrimeFaces.widget.SelectOneMenu.FilterFunction.itemLabel The label of the currently selected text.
 * @param {string} PrimeFaces.widget.SelectOneMenu.FilterFunction.filterValue The value to search for.
 * @return {boolean} PrimeFaces.widget.SelectOneMenu.FilterFunction `true` if the item label matches the filter value,
 * or `false` otherwise.
 *
 * @prop {boolean} changed Whether the value of this widget was changed from its original value.
 * @prop {JQuery} customInput The DOM element for the input field that lets the user enter a custom value which does not
 * have to match one of the available options.
 * @prop {string} customInputVal The custom value that was entered by the user which does not have to match one the
 * available options.
 * @prop {boolean} disabled Whether this widget is currently disabled.
 * @prop {JQuery} filterInput The DOM element for the input field that lets the user enter a search term to filter the
 * list of available options.
 * @prop {PrimeFaces.widget.SelectOneMenu.FilterFunction} filterMatcher The filter that was selected and is
 * currently used.
 * @prop {Record<PrimeFaces.widget.SelectOneMenu.FilterMatchMode, PrimeFaces.widget.SelectOneMenu.FilterFunction>} filterMatchers
 * Map between the available filter types and the filter implementation.
 * @prop {PrimeFaces.UnbindCallback} [hideOverlayHandler] Unbind callback for the hide overlay handler.
 * @prop {JQuery} input The DOM element for the hidden input with the current value.
 * @prop {boolean} isDynamicLoaded Whether the contents of the overlay panel were loaded.
 * @prop {boolean} isTabbing Whether the current process is handling the tab key.
 * @prop {JQuery} [items] The DOM elements for the available selectable options.
 * @prop {JQuery} [itemsContainer] The DOM element for the container with the available selectable options.
 * @prop {boolean} [querying] Whether an AJAX request for the autocompletion items is currently in progress.
 * @prop {JQuery} itemsWrapper The DOM element for the wrapper with the container with the available selectable options.
 * @prop {JQuery} focusInput The hidden input that can be focused via the tab key etc. (only used with editable="true")
 * @prop {boolean} hasFloatLabel Is this component wrapped in a float label.
 * @prop {JQuery} label The DOM element for the label indicating the currently selected option.
 * @prop {JQuery} keyboardTarget The DOM element used as target for keyboard - events.
 * @prop {JQuery} menuIcon The DOM element for the icon for bringing up the overlay panel.
 * @prop {JQuery} options The DOM elements for the available selectable options.
 * @prop {number} optGroupsSize The number of option groups.
 * @prop {JQuery} panel The DOM element for the overlay panel with the available selectable options.
 * @prop {JQuery} panelId ID of the DOM element for the overlay panel with the available selectable options.
 * @prop {number} panelWidthAdjusted The adjusted width of the overlay panel.
 * @prop {JQuery} preShowValue The DOM element for the selected option that is shown before the overlay panel is brought.
 * @prop {JQuery} labeledBy The DOM element for the label connected to he SelectOneMenu.
 * up.
 * @prop {PrimeFaces.UnbindCallback} [resizeHandler] Unbind callback for the resize handler.
 * @prop {PrimeFaces.UnbindCallback} [scrollHandler] Unbind callback for the scroll handler.
 * @prop {number} searchValue Letters typed for selection. (#4682, only used with editable="false")
 * @prop {number} searchTimer ID of the timeout for the delay of the filter input in the overlay panel.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * @prop {JQuery} triggers The DOM elements for the buttons that can trigger (hide or show) the overlay panel with the
 * available selectable options.
 * @prop {string} value The current value of this select one menu.
 *
 * @interface {PrimeFaces.widget.SelectOneMenuCfg} cfg The configuration for the {@link  SelectOneMenu| SelectOneMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {boolean} cfg.alwaysDisplayLabel `true` if the label of the selected item should always be set on the visible
 * input, `false` otherwise.
 * @prop {string} cfg.appendTo Appends the overlay to the element defined by search expression. Defaults to the document
 * body.
 * @prop {boolean} cfg.autoWidth Calculates a fixed width based on the width of the maximum option label. Possible values: `auto`,
 * `true`, `false`.
 * @prop {boolean} cfg.caseSensitive Defines if filtering would be case sensitive.
 * @prop {boolean} cfg.filterNormalize Defines if filtering would be done using normalized values.
 * @prop {boolean} cfg.dynamic Defines if dynamic loading is enabled for the element's panel. If the value is `true`,
 * the overlay is not rendered on page load to improve performance.
 * @prop {boolean} cfg.editable When true, the input field becomes editable.
 * @prop {boolean} cfg.filter `true` if the options can be filtered, or `false` otherwise.
 * @prop {PrimeFaces.widget.SelectOneMenu.FilterFunction} cfg.filterFunction A custom filter function that is used
 * when `filterMatchMode` is set to `custom`.
 * @prop {PrimeFaces.widget.SelectOneMenu.FilterMatchMode} cfg.filterMatchMode Mode of the filter. When set to
 * `custom` a `filterFunction` must be specified.
 * @prop {number} cfg.initialHeight Initial height of the overlay panel in pixels.
 * @prop {string} cfg.label Text of the label for the input.
 * @prop {string} cfg.labelTemplate Displays label of the element in a custom template. Valid placeholder is `{0}`,
 * which is replaced with the value of the currently selected item.
 * @prop {boolean} cfg.syncTooltip Updates the title of the component with the description of the selected item.
 * @prop {boolean} cfg.renderPanelContentOnClient Renders panel content on client.
 */
PrimeFaces.widget.SelectOneMenu = PrimeFaces.widget.DeferredWidget.extend({

	/**
	 * @override
	 * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
	 */
    init: function(cfg) {
        this._super(cfg);

        this.panelId = this.jqId + '_panel';
        this.input = $(this.jqId + '_input');
        if (this.cfg.editable) {
            this.focusInput = $(this.jqId + '_focus');
        }
        this.label = this.jq.find('.ui-selectonemenu-label');
        this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');

        this.keyboardTarget = this.cfg.editable ? this.focusInput : this.label;

        this.panel = $(this.panelId);
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.hasFloatLabel = PrimeFaces.utils.hasFloatLabel(this.jq);
        this.itemsWrapper = this.panel.children('.ui-selectonemenu-items-wrapper');
        this.options = this.input.find('option');
        this.cfg.effect = this.cfg.effect||'fade';

        this.cfg.effectSpeed = this.cfg.effectSpeed||'normal';
        this.cfg.autoWidth = this.cfg.autoWidth === undefined ? 'auto' : this.cfg.autoWidth;
        this.cfg.dynamic = !!this.cfg.dynamic;
        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this, this.jq, this.panel);
        this.cfg.renderPanelContentOnClient = this.cfg.renderPanelContentOnClient === true;
        this.isDynamicLoaded = false;
        this.isTabbing = false;
        this.searchValue = '';
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        if(this.cfg.dynamic || (this.itemsWrapper.children().length === 0)) {
            var selectedOption = this.options.filter(':selected'),
            labelVal = this.cfg.editable ? this.label.val() : selectedOption.text();

            this.setLabel(labelVal);
        }
        else {
            this.initContents();
            this.bindItemEvents();
        }

        //triggers
        this.triggers = this.cfg.editable ? this.jq.find('.ui-selectonemenu-trigger') : this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        if(!this.disabled) {
            this.bindEvents();

            PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');
        }

        // float label
        this.bindFloatLabel();

        // see #7602
        if (this.focusInput && PrimeFaces.env.isTouchable(this.cfg)) {
            this.focusInput.attr('readonly', true);
        }

        if (!this.cfg.editable) {
            // work-around because clicking a label referring to a div/span (not input) via for-attribute does focus this div/span
            var labeledBy = this.label.attr('aria-labelledby');
            this.labeledBy = null;
            if (labeledBy) {
                var eltLabeledBy = document.getElementById(labeledBy);
                if (eltLabeledBy) {
                    var $this = this;
                    this.labeledBy = $(eltLabeledBy);
                    this.labeledBy.on('click', function (e) {
                        $this.keyboardTarget.focus();
                    });
                }
            }
        }

        this.renderDeferred();
    },

    /**
     * Finds and initializes the DOM elements that make up this widget.
     * @private
     */
    initContents: function() {
        this.itemsContainer = this.itemsWrapper.children('.ui-selectonemenu-items');
        this.items = this.itemsContainer.find('.ui-selectonemenu-item');
        this.optGroupsSize = this.itemsContainer.find('li.ui-selectonemenu-item-group').length;

        var $this = this,
        selectedOption = this.options.filter(':selected'),
        highlightedItem = this.selectItemFromOption(selectedOption);

        //disable options
        $this.items.filter('[disabled]').addClass('ui-state-disabled');

        //activate selected
        if(this.cfg.editable) {
            var customInputVal = this.label.val();

            //predefined input
            if(customInputVal === selectedOption.text()) {
                this.highlightItem(highlightedItem);
            }
            //custom input
            else {
                this.items.eq(0).addClass('ui-state-highlight');
                this.customInput = true;
                this.customInputVal = customInputVal;
            }
        }
        else {
            this.highlightItem(highlightedItem);
        }

        if(this.cfg.syncTooltip) {
            this.syncTitle(selectedOption);
        }

        // ARIA
        for(var i = 0; i < this.items.length; i++) {
            this.items.eq(i).attr('id', this.id + '_' + i);
        }

        var highlightedItemId = highlightedItem.attr('id'),
            itemsContainerId = this.itemsContainer.attr('id');
        this.jq.attr('aria-owns', itemsContainerId);
        this.keyboardTarget
            .attr('aria-activedescendant', highlightedItemId)
            .attr('aria-disabled', this.disabled);

        this.itemsContainer.attr('aria-activedescendant', highlightedItemId);
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        if (this.cfg.autoWidth != 'false') {
            var contentStyle = this.jq.attr('style');
            var hasWidth = contentStyle && contentStyle.indexOf('width') != -1;

            if (!hasWidth) {
                // 'true' -> always calculate min-width
                var calculateMinWidth = true;

                // 'auto' -> only calculate it without a ui-fluid parent
                if (this.cfg.autoWidth == 'auto') {
                    var hasFluidParent = this.jq[0].closest('.ui-fluid') != undefined;
                    calculateMinWidth = !hasFluidParent;
                }

                if (calculateMinWidth) {
                    this.jq.css('min-width', this.input.outerWidth() + 'px');
                }
            }
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.panelWidthAdjusted = false;
        this.items = null;

        this._super(cfg);
    },

    /**
     * Adjust the width of the overlay panel to match the width of the select menu.
     * If the panel is narrower than the select menu, expands it to match.
     * If the panel is wider, preserves its current width.
     * Only adjusts width once per panel instance.
     * @private
     */
    alignPanelWidth: function() {
        if(!this.panelWidthAdjusted) {
            var jqWidth = this.jq.outerWidth();

            /* Ensures that the width of a potential scrollbar is considered in the following panel width calculation */
            let hasVerticalScrollbar = this.itemsWrapper[0].scrollHeight > this.itemsWrapper[0].clientHeight;
            if (hasVerticalScrollbar) {
                this.itemsWrapper.css({'overflow': 'scroll'});
            }

            if(this.panel.outerWidth() < jqWidth) {
                this.panel.outerWidth(jqWidth);
            }
            else {
                this.panel.outerWidth(this.panel.outerWidth());
            }

            this.itemsWrapper.css({'overflow': ''});

            this.panelWidthAdjusted = true;
        }
    },

    /**
     * Handles floating label CSS if wrapped in a floating label.
     * @private
     * @param {JQuery | undefined} input the input
     */
    updateFloatLabel: function(input) {
        PrimeFaces.utils.updateFloatLabel(this.jq, input, this.hasFloatLabel);
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        //Triggers
        this.triggers.on("mouseenter", function() {
            if(!$this.jq.hasClass('ui-state-focus')) {
                $this.jq.addClass('ui-state-hover');
                $this.menuIcon.addClass('ui-state-hover');
            }
        })
        .on("mouseleave", function() {
            $this.jq.removeClass('ui-state-hover');
            $this.menuIcon.removeClass('ui-state-hover');
        })
        .on("click", function(e) {
            if($this.panel.is(":hidden")) {
                $this.show();
            }
            else {
                $this.hide();

                $this.revert();
                $this.changeAriaValue($this.getActiveItem());
            }

            $this.jq.removeClass('ui-state-hover');
            $this.menuIcon.removeClass('ui-state-hover');
            if ($this.focusInput) {
                $this.focusInput.trigger('focus.ui-selectonemenu')
            }
            else if ($(e.currentTarget).hasClass('ui-selectonemenu-trigger')) {
                $this.keyboardTarget.trigger('focus.ui-selectonemenu');
            }
            e.preventDefault();
            e.stopPropagation();
        });
        
        this.input.off('focus.ui-selectonemenu').on('focus.ui-selectonemenu', function() {
            $this.keyboardTarget.trigger('focus.ui-selectonemenu');
        });

        this.keyboardTarget.on('focus.ui-selectonemenu', function(e) {
            $this.jq.addClass('ui-state-focus');
            $this.menuIcon.addClass('ui-state-focus');
            if(!$this.cfg.dynamic && !$this.items) {
                $this.callHandleMethod($this.handleTabKey(), e);
            }
            if ($this.hasFloatLabel) {
                $this.jq.addClass('ui-inputwrapper-focus');
            }
        })
        .on('blur.ui-selectonemenu', function(){
            $this.jq.removeClass('ui-state-focus');
            $this.menuIcon.removeClass('ui-state-focus');
            if ($this.hasFloatLabel) {
                $this.jq.removeClass('ui-inputwrapper-focus');
            }
            $this.callBehavior('blur');
        });

        //onchange handler for editable input
        if(this.cfg.editable) {
            this.label.on('change', function(e) {
                $this.triggerChange(true);
                $this.callHandleMethod($this.handleLabelChange, e);
            }).on('keyup', function(e) {
                switch (e.code) {
                    case 'Backspace':
                    case 'Delete':
                        if ($(this).val() === '') {
                            $this.callBehavior('clear');
                        }
                        break;
                }
            });
        }

        //key bindings
        this.bindKeyEvents();

        //filter
        if(this.cfg.filter) {
            this.cfg.initialHeight = this.itemsWrapper.height();
            this.setupFilterMatcher();
            this.filterInput = this.panel.find('> div.ui-selectonemenu-filter-container > input.ui-selectonemenu-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.bindFilterEvents();
        }
    },
    
    /**
     * Sets up the event listeners if this is bound to a floating label.
     * @private
     */
    bindFloatLabel: function() {
        if (!this.hasFloatLabel) {
            return;
        }
        var $this = this;
        this.panel.addClass('ui-input-overlay-panel');
        this.jq.addClass('ui-inputwrapper');

        this.updateFloatLabel(this.input);

        this.input.off('change').on('change', function() {
            $this.updateFloatLabel($(this));
        });

        if (this.cfg.editable) {
            this.label.on('input', function(e) {
                $this.updateFloatLabel($(this));
            }).on('focus', function() {
                $this.jq.addClass('ui-inputwrapper-focus');
            }).on('blur', function() {
                $this.jq.removeClass('ui-inputwrapper-focus');
                $this.updateFloatLabel($(this));
            });
        }
    },

    /**
     * Sets up the event listeners for the selectable items.
     * @private
     */
    bindItemEvents: function() {
        var $this = this;
        if(!this.items) {
            return;
        }

        //Items
        this.items.filter(':not(.ui-state-disabled)').on('mouseover.selectonemenu', function() {
            var el = $(this);

            if(!el.hasClass('ui-state-highlight'))
                $(this).addClass('ui-state-hover');
        })
        .on('mouseout.selectonemenu', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.selectonemenu', function() {
            $this.revert();
            $this.selectItem($(this));
            $this.changeAriaValue($(this));
        });
    },

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents: function() {
        var $this = this;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            function() { return  $this.label.add($this.menuIcon); },
            function(e, eventTarget) {
                if(!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide();
                    PrimeFaces.queueTask(function() {
                        $this.revert();
                        $this.changeAriaValue($this.getActiveItem());
                    });
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, function() {
            $this.handleViewportChange();
        });

        // GitHub #1173/#4609 keep panel with select while scrolling
        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, function() {
            $this.handleViewportChange();
        });
    },

    /**
     * Fired when the browser viewport is resized or scrolled.  In Mobile environment we don't want to hider the overlay
     * we want to re-align it.  This is because on some mobile browser the popup may force the browser to trigger a
     * resize immediately and close the overlay. See GitHub #7075.
     * @private
     */
    handleViewportChange: function() {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.alignPanel();
        } else {
            this.hide();
        }
    },

    /**
     * Unbind all panel event listeners
     * @private
     */
    unbindPanelEvents: function() {
        if (this.hideOverlayHandler) {
            this.hideOverlayHandler.unbind();
        }

        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }

        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    },

    /**
     * Removes some event listeners when this widget was disabled.
     * @private
     */
    unbindEvents: function() {
        if (this.items) {
            this.items.off();
        }
        this.triggers.off();
        this.input.off();
        if (this.focusInput) {
            this.focusInput.off();
        }
        this.label.off();
        if (this.labeledBy) {
            this.labeledBy.off();
        }
    },

    /**
     * Unselect the selected item, if any, and select the `please select` option.
     */
    revert: function() {
        if(this.cfg.editable && this.customInput) {
            this.setLabel(this.customInputVal);
            this.items.filter('.ui-state-active').removeClass('ui-state-active');
            this.items.eq(0).addClass('ui-state-active');
        }
        else {
            this.highlightItem(this.selectItemFromOption(this.preShowValue));
        }
    },

    /**
     * Highlight the given selectable option.
     * @private
     * @param {JQuery} item Option to highlight.
     */
    highlightItem: function(item) {
        this.items.attr('aria-selected', false);
        this.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');

        if(item.length > 0) {
            item.addClass('ui-state-highlight');
            item.attr('aria-selected', true);
            this.setLabel(item.data('label'));
        }
    },

    /**
     * Triggers the event listeners when the value of this widget changed.
     * @private
     * @param {boolean} edited Whether the value was edited by the user. If it was, checks which option is now selected.
     */
    triggerChange: function(edited) {
        this.changed = false;

        this.input.trigger('change');

        if(!edited) {
            this.value = this.options.filter(':selected').val();
        }
    },

    /**
     * Callback for when the user selects an item with the mouse.
     * @private
     * @param {JQuery} item The option to select.
     * @param {boolean} [silent] `true` to suppress triggering event listeners, or `false` otherwise.
     */
    selectItem: function(item, silent) {
        var selectedOption = this.selectOptionFromItem(item),
        currentOption = this.options.filter(':selected'),
        sameOption = selectedOption.val() == currentOption.val(),
        shouldChange = null;

        if(this.cfg.editable) {
            shouldChange = (!sameOption)||(selectedOption.text() != this.label.val());
        }
        else {
            shouldChange = !sameOption;
        }

        if(shouldChange) {
            this.highlightItem(item);
            this.input.val(selectedOption.val());

            if(!silent) {
                this.triggerChange();
            }

            if(this.cfg.editable) {
                this.customInput = false;
            }

            if(this.cfg.syncTooltip) {
                this.syncTitle(selectedOption);
            }
        }

        if(!silent) {
            this.callBehavior('itemSelect');
            if(this.focusInput) {
                this.focusInput.trigger('focus');
            }
        }

        if(this.panel.is(':visible')) {
            this.hide();
        }
    },

    /**
     * Adjust the value of the title attribute to match selected option.
     * @private
     * @param {JQuery} option The option that was selected.
     */
    syncTitle: function(option) {
        var optionTitle = this.selectItemFromOption(option).attr('title');
        if(optionTitle)
            this.jq.attr('title', optionTitle);
        else
            this.jq.removeAttr('title');
    },

    /**
     * Sets up the event listeners for all keyboard related events other than the overlay panel, such as pressing space
     * to bring up the overlay panel.
     * @private
     */
    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('keydown.ui-selectonemenu', function(e) {
            switch (e.code) {
                case 'ArrowUp':
                case 'ArrowLeft':
                    $this.callHandleMethod($this.highlightPrev, e);
                    break;

                case 'ArrowDown':
                case 'ArrowRight':
                    $this.callHandleMethod($this.highlightNext, e);
                    break;

                case 'Enter':
                case 'NumpadEnter':
                    $this.handleEnterKey(e);
                    break;

                case 'Tab':
                    $this.handleTabKey(e);
                    break;

                case 'Escape':
                    $this.handleEscapeKey(e);
                    break;

                case 'Space':
                    $this.handleSpaceKey(e);
                    break;
            }
        }).on('keyup.ui-selectonemenu', function(e) {
            if (PrimeFaces.utils.ignoreFilterKey(e)) {
                return;
            }
            if (!PrimeFaces.utils.isPrintableKey(e) || e.metaKey || e.ctrlKey || e.altKey) {
                return;
            }

            var matchedOptions = null;

            clearTimeout($this.searchTimer);

            // #4682: check for word match
            var text = $(this).val();
            if (!$this.focusInput) {
                $this.searchValue += e.key;
                text = $this.searchValue;
            }

            matchedOptions = $this.matchOptions(text);
            if (matchedOptions.length) {
                var matchIndex = matchedOptions[0].index;
                if ($this.panel.is(':hidden')) {
                    $this.callHandleMethod(function() {
                        var highlightItem = $this.items.eq(matchIndex);
                        $this.selectItem(highlightItem);
                    }, e);
                }
                else {
                    var highlightItem = $this.items.eq(matchIndex);
                    $this.highlightItem(highlightItem);
                    PrimeFaces.scrollInView($this.itemsWrapper, highlightItem);
                }
            } else {
                // #4682: check for first letter match
                text = e.key.toLowerCase();
                // find all options with the same first letter
                matchedOptions = $this.matchOptions(text);
                if (matchedOptions.length) {
                    $this.callHandleMethod(function() {
                        var selectedIndex = -1;

                        // is current selection one of our matches?
                        matchedOptions.each(function() {
                            var option = $(this);
                            var currentIndex = option[0].index;
                            var currentItem = $this.items.eq(currentIndex);
                            if (currentItem.hasClass('ui-state-highlight')) {
                                selectedIndex = currentIndex;
                                return false;
                            }
                        });

                        matchedOptions.each(function() {
                            var option = $(this);
                            var currentIndex = option[0].index;
                            var currentItem = $this.items.eq(currentIndex);

                            // select next item after the current selection
                            if (currentIndex > selectedIndex) {
                                if ($this.panel.is(':hidden')) {
                                    $this.selectItem(currentItem);
                                }
                                else {
                                    $this.highlightItem(currentItem);
                                    PrimeFaces.scrollInView($this.itemsWrapper, currentItem);
                                }
                                return false;
                            }
                        });
                    }, e);
                }
            }

            $this.searchTimer = PrimeFaces.queueTask(function() {
                $this.searchValue = '';
                if ($this.focusInput) {
                    $this.focusInput.val('');
                }
            }, 1000);
        });
    },

    /**
     * Finds all options that match the given search string.
     * @private
     * @param {string} text The search string against which to match the options.
     * @return {JQuery} All selectable options that match (contain) the given search string.
     */
    matchOptions: function(text) {
        if(!text) {
            return false;
        }
        return this.options.filter(function() {
            var option = $(this);
            return !option.is(':disabled') && option.text().toLowerCase().indexOf(text.toLowerCase()) === 0;
        });
    },

    /**
     * Sets up the event listeners for the filter input in the overlay panel.
     * @private
     */
    bindFilterEvents: function() {
        var $this = this;

        this.filterInput.on('keyup.ui-selectonemenu', function(e) {
            if (PrimeFaces.utils.ignoreFilterKey(e)) {
                return;
            }
            var metaKey = e.metaKey||e.ctrlKey;

            if(!metaKey) {
                $this.filter($(this).val());
            }
        })
        .on('keydown.ui-selectonemenu',function(e) {

            switch(e.code) {
                case 'ArrowUp':
                    $this.highlightPrev(e);
                break;

                case 'ArrowDown':
                    $this.highlightNext(e);
                break;

                case 'Enter':
                case 'NumpadEnter':
                case 'Tab':
                    $this.keyboardTarget.trigger("focus");
                    $this.keyboardTarget.trigger(jQuery.Event('keydown', { key: e.key, code: e.code }));
                break;

                case 'Escape':
                    $this.handleEscapeKey(e);
                break;

                case 'Space':
                    $this.handleSpaceKey(e);
                break;

                default:
                break;
            }
        }).on('input.ui-selectonemenu paste.ui-selectonemenu drop.ui-selectonemenu', function(e) {
            if (e.type === 'paste' || e.type === 'drop') {                
                PrimeFaces.queueTask(() => $this.filter($this.filterInput.val()), 10);
            }
		});
    },

    /**
     * Highlights the next option after the currently highlighted option in the overlay panel.
     * @private
     * @param {JQuery.TriggeredEvent} event The event of the keypress.
     */
    highlightNext: function(event) {
        // Get the currently active item
        var activeItem = this.getActiveItem();
        
        // Check if the panel is hidden
        var isHidden = this.panel.is(':hidden');
        
        // Create a selector for the next valid option
        // If panel is hidden, select the first option, otherwise select the first visible option
        var selector = '[role="option"]:not(.ui-state-disabled)' + (isHidden ? ':first' : ':visible:first');
        
        // Try to find the next valid option after the active item
        var next = activeItem.nextAll(selector);

        if (next.length === 0) {
            var index = this.items.index(activeItem) + 1;
            if (index >= 0) {
                next = this.items.eq(index);
            }
        }
    
        if(event.altKey) {
            this.show();
        }
        else if(next.length === 1) {
            if(this.panel.is(':hidden')) {
                this.selectItem(next);
            }
            else {
                this.highlightItem(next);
                PrimeFaces.scrollInView(this.itemsWrapper, next);
            }
            this.changeAriaValue(next);
        }

        event.preventDefault();
    },

    /**
     * Highlights the previous option before the currently highlighted option in the overlay panel.
     * @private
     * @param {JQuery.TriggeredEvent} event The event of the keypress.
     */
    highlightPrev: function(event) {
        // Get the currently active item
        var activeItem = this.getActiveItem();
        
        // Check if the panel is hidden
        var isHidden = this.panel.is(':hidden');
        
        // Create a selector for the next valid option
        // If panel is hidden, select the first option, otherwise select the first visible option
        var selector = '[role="option"]:not(.ui-state-disabled)' + (isHidden ? ':first' : ':visible:first');
        
        // Try to find the previous valid option after the active item
        var prev = activeItem.prevAll(selector);
        
        // If no next option found, look for the first option in the next group (ul)
        if (prev.length === 0) {
            var index = this.items.index(activeItem) - 1;
            if (index >= 0) {
                prev = this.items.eq(index);
            }
        }

        if(prev.length === 1) {
            if(this.panel.is(':hidden')) {
                this.selectItem(prev);
            }
            else {
                this.highlightItem(prev);
                PrimeFaces.scrollInView(this.itemsWrapper, prev);
            }
            this.changeAriaValue(prev);
        }

        event.preventDefault();
    },

    /**
     * Callback for when the enter key was pressed. Brings up the overlay panel or accepts the highlighted option.
     * @private
     * @param {JQuery.TriggeredEvent} event The event of the keypress.
     */
    handleEnterKey: function(event) {
        if(this.panel.is(':visible')) {
            this.selectItem(this.getActiveItem());
            // #8308 prevent Default Command while panel is open
            event.stopPropagation();
        }

        event.preventDefault();
    },

    /**
     * Callback for when the space key was pressed. Brings up or hides the overlay panel.
     * @private
     * @param {JQuery.TriggeredEvent} event The event of the keypress.
     */
    handleSpaceKey: function(event) {
        var target = $(event.target);

        if(target.is('input') && target.hasClass('ui-selectonemenu-filter')) {
            return;
        }

        if(this.panel.is(":hidden")) {
            this.show();
        }
        else {
            this.hide();

            this.revert();
            this.changeAriaValue(this.getActiveItem());
        }

        event.preventDefault();
    },

    /**
     * Callback for when the escape key was pressed. Hides the overlay panel.
     * @private
     * @param {JQuery.TriggeredEvent} event The event of the keypress.
     */
    handleEscapeKey: function(event) {
        if(this.panel.is(':visible')) {
            this.revert();
            this.hide();
            event.preventDefault();
        }
    },

    /**
     * Callback for when the tab key was pressed. Selects the next option.
     * @private
     * @param {JQuery.TriggeredEvent} event The keyboard event for the TAB.
     */
    handleTabKey: function(event) {
        this.isTabbing = this.panel.is(':visible');
        if(this.isTabbing) {
            this.selectItem(this.getActiveItem());
        }
    },

    /**
     * Callback that adjusts the label, invoked when the selected option has changed.
     * @private
     * @param {JQuery.TriggeredEvent} event The event that triggered the change.
     */
    handleLabelChange: function(event) {
        this.customInput = true;
        this.customInputVal = $(event.target).val();
        this.items.filter('.ui-state-active').removeClass('ui-state-active');
        this.items.eq(0).addClass('ui-state-active');
    },

    /**
     * Brings up the overlay panel with the available selectable options.
     */
    show: function() {
        this.callHandleMethod(this._show, null);
    },

    /**
     * Brings up the overlay panel with the available selectable options. Compared this `show`, this does not ensure
     * the the overlay panel is loaded already (when dynamic loading is enabled).
     * @private
     */
    _show: function() {
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    PrimeFaces.nextZindex($this.panel);
                    $this.alignPanel();
                },
                onEntered: function() {
                    $this.bindPanelEvents();

                    //value before panel is shown
                    $this.preShowValue = $this.options.filter(':selected');
                    $this.keyboardTarget.attr('aria-expanded', true);

                    PrimeFaces.scrollInView($this.itemsWrapper, $this.getActiveItem());

                    if ($this.cfg.filter) {
                        $this.focusFilter();
                    }
                }
            });
        }
    },

    /**
     * Hides the overlay panel with the available selectable options.
     */
    hide: function() {
        if (this.panel.is(':visible') && this.transition) {
            var $this = this;
            this.searchValue = '';

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    $this.panel.css('z-index', '');
                    $this.keyboardTarget.attr('aria-expanded', false);

                    // refocus input if not tabbing and active is owned by this component
                    if (!$this.isTabbing) {
                        var activeElement = $(document.activeElement);
                        if ($this.jq.has(activeElement).length || $this.panel.has(activeElement).length) {
                            $this.keyboardTarget.trigger('focus.ui-selectonemenu');
                        }
                    }
                    $this.isTabbing = false;
                }
            });
        }
    },

    /**
     * Puts focus on this widget.
     */
    focus: function() {
        this.keyboardTarget.trigger('focus');
    },

    /**
     * Puts focus on the filter input in the overlay panel.
     * @param {number} [timeout] Amount of time in milliseconds to wait before attempting to focus the input.
     */
    focusFilter: function(timeout) {
        if(timeout) {
            var $this = this;
            PrimeFaces.queueTask(function() {
                $this.focusFilter();
            }, timeout);
        }
        else {
            this.filterInput.trigger('focus');
        }
    },

    /**
     * Removes focus from this widget.
     */
    blur: function() {
        this.keyboardTarget.trigger("blur");
        this.searchValue = '';

        this.callBehavior('blur');
    },

    /**
     * Disables this widget so that the user cannot select any option.
     */
    disable: function() {
    	if (!this.disabled) {
	        this.disabled = true;
	        this.jq.addClass('ui-state-disabled');
	        this.input.attr('disabled', 'disabled');
	        if(this.cfg.editable) {
	            this.label.attr('disabled', 'disabled');
	        }
	        this.unbindEvents();
    	}
    },

    /**
     * Enables this widget so that the user can select an option.
     */
    enable: function() {
    	if (this.disabled) {
	        this.disabled = false;
	        this.jq.removeClass('ui-state-disabled');
	        this.input.removeAttr('disabled');
	        if(this.cfg.editable) {
	            this.label.removeAttr('disabled');
	        }

            this.bindEvents();
            this.bindItemEvents();
    	}
    },

    /**
     * Align the overlay panel with the available selectable options so that is is positioned next to the the button.
     */
    alignPanel: function() {
        this.alignPanelWidth();

        if(this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px',
                'transform-origin': 'center top'
            });
        }
        else {
            this.panel.css({left:'0px', top:'0px', 'transform-origin': 'center top'}).position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
                ,using: function(pos, directions) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    },

    /**
     * Sets the label text that indicates the currently selected item to the item with the given value.
     * @private
     * @param {string} value Value of the item that was selected.
     */
    setLabel: function(value) {
        var displayedLabel = this.getLabelToDisplay(value);
        var valueEmpty = value === '&nbsp;' || value === '\xa0';
        if (this.cfg.editable) {
            if (valueEmpty)
                this.label.val('');
            else
                this.label.val(displayedLabel);

            var hasPlaceholder = this.label[0].hasAttribute('placeholder');
            this.updatePlaceholderClass((hasPlaceholder && valueEmpty));
        }
        else if (this.cfg.alwaysDisplayLabel && this.cfg.label) {
            this.label.text(this.cfg.label);
        }
        else {
            var labelText = this.label.data('placeholder');
            if (labelText == null || labelText == "") {
                labelText = '&nbsp;';
            }

            this.updatePlaceholderClass((valueEmpty && labelText !== '&nbsp;'));

            if (valueEmpty) {
                if (labelText != '&nbsp;') {
                    this.label.text(labelText);
                } else {
                    this.label.html(labelText);
                }
            }
            else {
                this.label.removeClass('ui-state-disabled');

                var option = null;
                if (this.items) {
                    var selectedItem = this.items.filter('[data-label="' + CSS.escape(value) + '"]');
                    option = this.selectOptionFromItem(selectedItem);
                }
                else {
                    option = this.options.filter(':selected');
                }

                if (option && option.data('escape') === false) {
                    this.label.html(displayedLabel);
                } else {
                    this.label.text(displayedLabel);
                }
                this.label.attr('aria-label', displayedLabel);
            }
        }
    },

    /**
     * Selects the option with the given value.
     * @param {string} value Value of the option to select.
     */
    selectValue: function(value) {
        if(!this.items || this.items.length === 0) {
           this.callHandleMethod(null, null);
        }

        var item;
        if (this.optGroupsSize === 0) {
            var option = this.options.filter('[value="' + CSS.escape(value) + '"]');
            item = this.selectItemFromOption(option);
        }
        else {
            item = this.items.filter('[data-value="' + CSS.escape(value) + '"]');
        }
        this.selectItem(item, true);
    },

    /**
     * Selects the item corresponding to the given option.
     * @param {JQuery} option The option element to select.
     * @return {JQuery} The selected item element.
     */
    selectItemFromOption: function(option) {
        var item;
        if (this.optGroupsSize === 0) {
            item = this.items.eq(option.index());
        }
        else {
            item = this.items.filter('[data-value="' + CSS.escape(option.val()) + '"]');
        }
        return item;
    },

    /**
     * Selects the option corresponding to the given item.
     * @param {JQuery} item The item element to select.
     * @return {JQuery} The selected option element.
     */
    selectOptionFromItem: function(item) {
        var option;
        if (this.optGroupsSize === 0) {
            option = this.options.eq(item.index());
        }
        else {
            option = this.options.filter('[value="' + CSS.escape(item.attr('data-value')) + '"]');
        }
        return option;
    },

    /**
     * Resets the input.
     * @param {boolean} [silent] `true` to suppress triggering event listeners, or `false` otherwise.
     */
    resetValue: function(silent) {
        if(!this.items || this.items.length === 0) {
           this.callHandleMethod(null, null);
        }

        var option = this.options.filter('[value=""]');
        if (option.length === 0) {
            // if no empty value option found, fallback to first in list like JSF default
            option = this.options.eq(0);
        }
        this.selectItem(this.selectItemFromOption(option), silent);
    },

    /**
     * Finds the element for the currently select option of this select one menu.
     * @return {JQuery} The DOM element that represents the currently selected option.
     */
    getActiveItem: function() {
        return this.items.filter('.ui-state-highlight');
    },

    /**
     * Finds and stores the filter function which is to be used for filtering the options of this select one menu.
     * @private
     */
    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectOneMenu.FilterFunction` that matches the given option when it starts
     * with the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the options starts with the filter value, or `false` otherwise.
     */
    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectOneMenu.FilterFunction` that matches the given option when it
     * contains the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the contains the filter value, or `false` otherwise.
     */
    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectOneMenu.FilterFunction` that matches the given option when it ends
     * with the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the options ends with the filter value, or `false` otherwise.
     */
    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    /**
     * Filters the available options in the overlay panel by the given search value. Note that this does not bring up
     * the overlay panel, use `show` for that.
     * @param {string} value A value against which the available options are matched.
     */
    filter: function(value) {
        this.cfg.initialHeight = this.cfg.initialHeight||this.itemsWrapper.height();
        var lowercase = !this.cfg.caseSensitive,
                normalize = this.cfg.filterNormalize,
                filterValue = PrimeFaces.toSearchable(PrimeFaces.trim(value), lowercase, normalize);

        if(filterValue === '') {
            this.items.filter(':hidden').show();
            this.itemsContainer.find('li.ui-selectonemenu-item-group').show();
        }
        else {
            var hide = [];
            var show = [];

            for(var i = 0; i < this.options.length; i++) {
                var option = this.options.eq(i),
                item = this.items.eq(i);

                if(item.hasClass('ui-noselection-option')) {
                    hide.push(item);
                } else {
                    var itemLabel = PrimeFaces.toSearchable(option.text(), lowercase, normalize);
                    if(this.filterMatcher(itemLabel, filterValue)) {
                        show.push(item);
                    } else {
                        if(item.is('.ui-selectonemenu-item-group-children')) {
                            itemLabel = PrimeFaces.toSearchable(option.parent().attr('label'), lowercase, normalize);
                            if (this.filterMatcher(itemLabel, filterValue)) {
                                show.push(item);
                                continue;
                            }
                        }
                        hide.push(item);
                    }
                }
            }

            $.each(hide, function(i, o) { o.hide(); });
            $.each(show, function(i, o) { o.show(); });
            hide = [];
            show = [];

            //Toggle groups
            var groups = this.itemsContainer.find('li.ui-selectonemenu-item-group');
            for(var g = 0; g < groups.length; g++) {
                var group = groups.eq(g);

                if(g === (groups.length - 1)) {
                    if(group.nextAll().filter('.ui-selectonemenu-item-group-children:visible').length === 0)
                        hide.push(group);
                    else
                        show.push(group);
                }
                else if(group.nextUntil('li.ui-selectonemenu-item-group').filter('.ui-selectonemenu-item-group-children:visible').length === 0) {
                    hide.push(group);
                } else {
                    show.push(group);
                }
            }

            $.each(hide, function(i, o) { o.hide(); });
            $.each(show, function(i, o) { o.show(); });
        }

        var firstVisibleItem = this.items.filter(':visible:not(.ui-state-disabled):first');
        if(firstVisibleItem.length) {
            this.highlightItem(firstVisibleItem);
            PrimeFaces.scrollInView(this.itemsWrapper, firstVisibleItem);
        }

        if(this.itemsContainer.height() < this.cfg.initialHeight) {
            this.itemsWrapper.css('height', 'auto');
        }
        else {
            this.itemsWrapper.height(this.cfg.initialHeight);
        }

        this.alignPanel();
    },

    /**
     * Finds the value of the currently selected item, if any.
     * @return {string} The value of the currently selected item. Empty string if none is selected.
     */
    getSelectedValue: function() {
        return this.input.val();
    },

    /**
     * Finds the label of the currently selected item, if any.
     * @return {string} The label of the currently selected item. Empty string if none is selected.
     */
    getSelectedLabel: function() {
        return this.options.filter(':selected').text();
    },

    /**
     * Finds the label of the option with the given value.
     * @private
     * @param {string} value The value of a selectable option.
     * @return {string} The label of the option with the given value.
     */
    getLabelToDisplay: function(value) {
        if(this.cfg.labelTemplate && value !== '&nbsp;') {
            return this.cfg.labelTemplate.replace('{0}', value);
        }
        return String(value);
    },

    /**
     * Adjusts the value of the aria attributes for the given selectable option.
     * @private
     * @param {JQuery} item An option for which to set the aria attributes.
     */
    changeAriaValue: function (item) {
        var itemId = item.attr('id');

        this.keyboardTarget.attr('aria-activedescendant', itemId);
        this.itemsContainer.attr('aria-activedescendant', itemId);
    },

    /**
     * Loads the overlay panel with the selectable options, if dynamic mode is enabled.
     * @private
     */
    dynamicPanelLoad: function() {
        var $this = this;
        $this.setQuerying(true);
        
        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            global: false,
            params: [{name: this.id + '_dynamicload', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        var $content = $($.parseHTML(content));

                        var $itemContent = $content.filter('ul, table');
                        $this.itemsWrapper.empty();
                        $this.itemsWrapper.append($itemContent);

                        var $select = $content.filter('select');
                        $this.input.replaceWith($select);
                    }
                });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                $this.isDynamicLoaded = true;
                $this.input = $($this.jqId + '_input');
                $this.options = $this.input.children('option');

                $this.renderPanelContentFromHiddenSelect(false);

                $this.initContents();
                $this.bindItemEvents();
                $this.setQuerying(false);
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    /**
     * Invokes the given method after making sure that the overlay panel was loaded (in case dynamic mode is enabled).
     * @private
     * @param {(this: PrimeFaces.widget.SelectOneMenu, event: JQuery.TriggeredEvent) => void} handleMethod Callback method to
     * invoke after the dynamic overlay panel was loaded.
     * @param {JQuery.TriggeredEvent} event An event that is passed to the callback.
     */
    callHandleMethod: function(handleMethod, event) {
        var $this = this;
        if(this.cfg.dynamic && !this.isDynamicLoaded) {
            this.dynamicPanelLoad();

            var interval = setInterval(function() {
                if($this.isDynamicLoaded) {
                    if (handleMethod) {
                        handleMethod.call($this, event);
                    }

                    clearInterval(interval);
                }
            }, 10);
        }
        else {
            this.renderPanelContentFromHiddenSelect(true);

            if (handleMethod) {
                handleMethod.call(this, event);
            }
        }
    },

    /**
     * Sets the querying state.
     * @param {boolean} state Querying state to set.
     * @private
     */
    setQuerying: function(state) {
        if (state && !this.querying) {
            this.jq.addClass('ui-state-loading');
            this.menuIcon.prepend('<span class="ui-icon-loading pi pi-spin pi-spinner"></span>');
                
        }
        else if (!state && this.querying) {
            this.jq.removeClass('ui-state-loading');
            this.menuIcon.find('.ui-icon-loading').remove();
        }
        this.querying = state;
    },

    /**
     * Renders panel content based on hidden select.
     * @private
     * @param {boolean} initContentsAndBindItemEvents `true` to call {@link initContents} and {@link bindItemEvents}
     * after rendering, `false` otherwise.
     */
    renderPanelContentFromHiddenSelect: function(initContentsAndBindItemEvents) {
         if (this.cfg.renderPanelContentOnClient && this.itemsWrapper.children().length === 0) {
             var panelContent = '<div id="' + this.id + '_items" class="ui-selectonemenu-items ui-widget-content ui-widget ui-helper-reset" role="listbox">';
             panelContent += this.renderSelectItems(this.input);
             panelContent += '</div>';

             this.itemsWrapper.append(panelContent);

             if (initContentsAndBindItemEvents) {
                 this.initContents();
                 this.bindItemEvents();
             }
         }
    },

    /**
     * Renders panel HTML-code for all select items.
     * @private
     * @param {JQuery} parentItem A parent item (select, optgroup) for which to render HTML code.
     * @param {boolean} [isGrouped] Indicated whether the elements of the parent item should be marked as grouped.
     * @return {string} The rendered HTML string.
     */
    renderSelectItems: function(parentItem, isGrouped) {
        var $this = this;
        var content = "";
        isGrouped = isGrouped || false;

        var opts = parentItem.children("option, optgroup");
        var hasOptgroup = opts.filter("optgroup").length > 0;
        
        if (!hasOptgroup && !isGrouped) {
            content += '<ul role="group" class="ui-selectonemenu-list ui-widget-content ui-widget ui-helper-reset">';
        }

        content += opts.map(function(index, element) {
            return $this.renderSelectItem(element, isGrouped);
        }).get().join('');
        
        if (!hasOptgroup && !isGrouped) {
            content += '</ul>';
        }
        
        return content;
    },

    /**
     * Renders panel HTML code for one select item (group).
     * @private
     * @param {JQuery} item An option (group) for which to render HTML code.
     * @param {boolean} [isGrouped] Indicates whether the item is part of a group.
     * @return {string} The rendered HTML string.
     */
    renderSelectItem: function(item, isGrouped) {
        var $item = $(item);
        var isOptgroup = item.tagName === "OPTGROUP";
        var id = PrimeFaces.uuid();
        var escape = $item.data("escape");
        var label = isOptgroup ? $item.attr("label") : $item.text();

        if (escape) {
            label = isOptgroup ? $("<div>").text(label).html() : $item.html();
            label = label === "&nbsp;" ? " " : label;
        }

        var cssClass = isOptgroup ? "ui-selectonemenu-item-group" : 
                       "ui-selectonemenu-item ui-selectonemenu-list-item" + 
                       (isGrouped ? " ui-selectonemenu-item-group-children" : "") +
                       ($item.data("noselection-option") ? " ui-noselection-option" : "");

        var content = isOptgroup ? '<ul role="group" class="ui-selectonemenu-list ui-widget-content ui-widget ui-helper-reset" aria-labelledby="' + id + '">' : '';
        
        content += '<li id="' + id + '" class="' + cssClass + '" tabindex="-1" role="' + (isOptgroup ? "presentation" : "option") + '"';
        
        var title = $item.data("title");
        if (title) {
            content += ' title="' + (escape ? PrimeFaces.escapeHTML(title) : title) + '"';
        }
        if ($item.is(':disabled')) {
            content += ' disabled';
        }
        
        var dataLabel = escape ? label.replaceAll('"', '&quot;') : PrimeFaces.escapeHTML(label, true);
        label = label === "&amp;nbsp;" ? "&nbsp;" : label;
        content += ' data-label="' + dataLabel + '" data-value="' + PrimeFaces.escapeHTML($item.val(), true)+ '">' + label + '</li>';

        if (isOptgroup) {
            content += this.renderSelectItems($item, true) + '</ul>';
        }

        return content;
    },

    /**
     * Updates the style class of the label that indicates the currently selected item.
     * @param {boolean} add `true` if a placeholder should be displayed, or `false` otherwise.
     */
    updatePlaceholderClass: function(add) {
        if (add) {
            this.label.addClass('ui-selectonemenu-label-placeholder');
        }
        else {
            this.label.removeClass('ui-selectonemenu-label-placeholder');
        }
    }

});
