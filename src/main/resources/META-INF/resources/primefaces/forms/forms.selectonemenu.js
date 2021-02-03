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
 * @prop {JQuery} input The DOM element for the hidden input with the current value.
 * @prop {boolean} isDynamicLoaded Whether the contents of the overlay panel were loaded.
 * @prop {JQuery} items The DOM elements for the the available selectable options.
 * @prop {JQuery} itemContainer The DOM element for the container with the available selectable options.
 * @prop {JQuery} itemContainerWrapper The DOM element for the wrapper with the container with the available selectable
 * options.
 * @prop {JQuery} focusInput The hidden input that can be focused via the tab key etc.
 * @prop {JQuery} label The DOM element for the label indicating the currently selected option.
 * @prop {JQuery} menuIcon The DOM element for the icon for bringing up the overlay panel.
 * @prop {JQuery} options The DOM elements for the available selectable options.
 * @prop {number} optGroupsSize The number of option groups.
 * @prop {JQuery} panel The DOM element for the overlay panel with the available selectable options.
 * @prop {JQuery} panelId ID of the DOM element for the overlay panel with the available selectable options.
 * @prop {number} panelWidthAdjusted The adjusted width of the overlay panel.
 * @prop {JQuery} preShowValue The DOM element for the selected option that is shown before the overlay panel is brought
 * up.
 * @prop {number} searchTimer ID of the timeout for the delay of the filter input in the overlay panel.
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
 * @prop {boolean} cfg.autoWidth Calculates a fixed width based on the width of the maximum option label. Set to false
 * for custom width.
 * @prop {boolean} cfg.caseSensitive Defines if filtering would be case sensitive.
 * @prop {boolean} cfg.dynamic Defines if dynamic loading is enabled for the element's panel. If the value is `true`,
 * the overlay is not rendered on page load to improve performance.
 * @prop {boolean} cfg.editable When true, the input field becomes editable.
 * @prop {string} cfg.effect Name of the toggle animation for the overlay panel.
 * @prop {PrimeFaces.widget.SelectOneMenu.EffectSpeed} cfg.effectSpeed Duration of toggle animation.
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
        this.focusInput = $(this.jqId + '_focus');
        this.label = this.jq.find('.ui-selectonemenu-label');
        this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');

        this.panel = $(this.panelId);
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.itemsWrapper = this.panel.children('.ui-selectonemenu-items-wrapper');
        this.options = this.input.find('option');
        this.cfg.effect = this.cfg.effect||'fade';

        this.cfg.effectSpeed = this.cfg.effectSpeed||'normal';
        this.cfg.autoWidth = this.cfg.autoWidth === false ? false : true;
        this.cfg.dynamic = this.cfg.dynamic === true ? true : false;
        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this);
        this.cfg.renderPanelContentOnClient = this.cfg.renderPanelContentOnClient === true;
        this.isDynamicLoaded = false;

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
            this.bindConstantEvents();

            PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
        }

        // see #7602
        if (PrimeFaces.env.isTouchable(this.cfg)) {
            this.focusInput.attr('readonly', true);
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
        this.optGroupsSize = this.itemsContainer.children('li.ui-selectonemenu-item-group').length;

        var $this = this,
        selectedOption = this.options.filter(':selected'),
        highlightedItem = this.items.eq(this.options.index(selectedOption));

        //disable options
        this.options.filter(':disabled').each(function() {
            $this.items.eq($(this).index()).addClass('ui-state-disabled');
        });

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

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        //for Screen Readers
        for(var i = 0; i < this.items.length; i++) {
            this.items.eq(i).attr('id', this.id + '_' + i);
        }

        var highlightedItemId = highlightedItem.attr('id');
        this.jq.attr('aria-owns', this.itemsContainer.attr('id'));
        this.focusInput.attr('aria-autocomplete', 'list')
            .attr('aria-activedescendant', highlightedItemId)
            .attr('aria-describedby', highlightedItemId)
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
        var contentStyle = this.jq.attr('style'),
        hasWidth = contentStyle && contentStyle.indexOf('width') != -1;

        if(this.cfg.autoWidth && !hasWidth) {
            this.jq.css('min-width', this.input.outerWidth() + 'px');
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
     * Adjust the width of the overlay panel.
     * @private 
     */
    alignPanelWidth: function() {
        //align panel and container
        if(!this.panelWidthAdjusted) {
            var jqWidth = this.jq.outerWidth();
            if(this.panel.outerWidth() < jqWidth) {
                this.panel.width(jqWidth);
            }
            else {
                this.panel.width(this.panel.width());
            }

            this.panelWidthAdjusted = true;
        }
    },

    /**
     * Sets up all event listenters required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        // Screen Reader(JAWS) hack on Chrome
        if(PrimeFaces.env.browser.webkit) {
            this.input.on('focus', function(){
                setTimeout(function(){
                    $this.focusInput.trigger('focus.ui-selectonemenu');
                },2);
            });
        }

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
            $this.focusInput.trigger('focus.ui-selectonemenu');
            e.preventDefault();
        });

        this.focusInput.on('focus.ui-selectonemenu', function() {
            $this.jq.addClass('ui-state-focus');
            $this.menuIcon.addClass('ui-state-focus');
        })
        .on('blur.ui-selectonemenu', function(){
            $this.jq.removeClass('ui-state-focus');
            $this.menuIcon.removeClass('ui-state-focus');

            $this.callBehavior('blur');
        });

        //onchange handler for editable input
        if(this.cfg.editable) {
            this.label.on('change', function(e) {
                $this.triggerChange(true);
                $this.callHandleMethod($this.handleLabelChange, e);
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
     * Sets up the event listeners that only need to be set up once.
     * @private
     */
    bindConstantEvents: function() {
        var $this = this;

        PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.panel,
            function() { return  $this.label.add($this.menuIcon); },
            function(e, eventTarget) {
                if(!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide();
                    setTimeout(function() {
                        $this.revert();
                        $this.changeAriaValue($this.getActiveItem());
                    }, 2);
                }
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', $this.panel, function() {
            $this.hide();
        });

        // GitHub #1173/#4609 keep panel with select while scrolling
        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_hide', function() {
            $this.hide();
        });
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
        this.focusInput.off();
        this.label.off();
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
            this.highlightItem(this.items.eq(this.options.index(this.preShowValue)));
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
     * @param {boolean} silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    selectItem: function(item, silent) {
        var selectedOption = this.options.eq(this.resolveItemIndex(item)),
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
            this.input.val(selectedOption.val())

            this.triggerChange();

            if(this.cfg.editable) {
                this.customInput = false;
            }

            if(this.cfg.syncTooltip) {
                this.syncTitle(selectedOption);
            }
        }

        if(!silent) {
            this.callBehavior('itemSelect');
            this.focusInput.trigger('focus');
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
        var optionTitle = this.items.eq(option.index()).attr('title');
        if(optionTitle)
            this.jq.attr('title', this.items.eq(option.index()).attr('title'));
        else
            this.jq.removeAttr('title');
    },

    /**
     * Finds the index of the given selectable option.
     * @param {JQuery} item One of the available selectable options.
     * @return {number} The index of the given item. 
     */
    resolveItemIndex: function(item) {
        if(this.optGroupsSize === 0)
            return item.index();
        else
            return item.index() - item.prevAll('li.ui-selectonemenu-item-group').length;
    },

    /**
     * Sets up the event listeners for all keyboard related events other than the overlay panel, such as pressing space
     * to bring up the overlay panel.
     * @private
     */
    bindKeyEvents: function() {
        var $this = this;

        this.focusInput.on('keydown.ui-selectonemenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                    $this.callHandleMethod($this.highlightPrev, e);
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    $this.callHandleMethod($this.highlightNext, e);
                break;

                case keyCode.ENTER:
                    $this.handleEnterKey(e);
                break;

                case keyCode.TAB:
                    $this.handleTabKey();
                break;

                case keyCode.ESCAPE:
                    $this.handleEscapeKey(e);
                break;

                case keyCode.SPACE:
                    $this.handleSpaceKey(e);
                break;
            }
        })
        .on('keyup.ui-selectonemenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                case keyCode.ENTER:
                case keyCode.TAB:
                case keyCode.ESCAPE:
                case keyCode.SPACE:
                case keyCode.HOME:
                case keyCode.PAGE_DOWN:
                case keyCode.PAGE_UP:
                case keyCode.END:
                case keyCode.DELETE:
                case 16: //shift
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case 19: //Pause/Break:
                case 20: //capslock:
                case 44: //Print Screen:
                case 45: //Insert:
                case 91: //left window or cmd:
                case 92: //right window:
                case 93: //right cmd:
                case 144: //num lock:
                case 145: //scroll lock:
                break;

                default:
                    //function keys (F1,F2 etc.)
                    if(key >= 112 && key <= 123) {
                        break;
                    }

                    var matchedOptions = null,
                    metaKey = e.metaKey||e.ctrlKey||e.altKey;

                    if(!metaKey) {
                        clearTimeout($this.searchTimer);

                        // #4682: check for word match
                        var text = $(this).val();
                        matchedOptions = $this.matchOptions(text);
                        if(matchedOptions.length) {
                            var matchIndex = matchedOptions[0].index;
                            var highlightItem = $this.items.eq(matchIndex);
                            if($this.panel.is(':hidden')) {
                                $this.selectItem(highlightItem);
                            }
                            else {
                                $this.highlightItem(highlightItem);
                                PrimeFaces.scrollInView($this.itemsWrapper, highlightItem);
                            }
                        } else {
                            // #4682: check for first letter match
                            text = String.fromCharCode(key).toLowerCase();
                            // find all options with the same first letter
                            matchedOptions = $this.matchOptions(text);
                            if(matchedOptions.length) {
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
                                         if($this.panel.is(':hidden')) {
                                             $this.selectItem(currentItem);
                                         }
                                         else {
                                             $this.highlightItem(currentItem);
                                             PrimeFaces.scrollInView($this.itemsWrapper, currentItem);
                                         }
                                         return false;
                                     }
                                });
                            }
                        }

                        $this.searchTimer = setTimeout(function(){
                            $this.focusInput.val('');
                        }, 1000);
                    }
                break;
            }
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
            if(option.is(':disabled')) {
                return false;
            }
            if(option.text().toLowerCase().indexOf(text.toLowerCase()) !== 0) {
                return false;
            }
            return true;
        });
    },

    /**
     * Sets up the event listeners for the filter input in the overlay panel.
     * @private
     */
    bindFilterEvents: function() {
        var $this = this;

        this.filterInput.on('keyup.ui-selectonemenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                case keyCode.ENTER:
                case keyCode.TAB:
                case keyCode.ESCAPE:
                case keyCode.SPACE:
                case keyCode.HOME:
                case keyCode.PAGE_DOWN:
                case keyCode.PAGE_UP:
                case keyCode.END:
                case 16: //shift
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case 91: //left window or cmd:
                case 92: //right window:
                case 93: //right cmd:
                case 20: //capslock:
                break;

                default:
                    //function keys (F1,F2 etc.)
                    if(key >= 112 && key <= 123) {
                        break;
                    }

                    var metaKey = e.metaKey||e.ctrlKey;

                    if(!metaKey) {
                        $this.filter($(this).val());
                    }
                break;
            }
        })
        .on('keydown.ui-selectonemenu',function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                    $this.highlightPrev(e);
                break;

                case keyCode.DOWN:
                    $this.highlightNext(e);
                break;

                case keyCode.ENTER:
                    $this.handleEnterKey(e);
                break;

                case keyCode.TAB:
                    $this.handleTabKey();
                break;

                case keyCode.ESCAPE:
                    $this.handleEscapeKey(e);
                break;

                case keyCode.SPACE:
                    $this.handleSpaceKey(e);
                break;

                default:
                break;
            }
        }).on('paste.ui-selectonemenu', function() {
            setTimeout(function(){
                $this.filter($this.filterInput.val());
            },2);
		});
    },

    /**
     * Highlights the next option after the currently highlighted option in the overlay panel.
     * @private
     * @param {JQuery.TriggeredEvent} event The event of the keypress.
     */
    highlightNext: function(event) {
        var activeItem = this.getActiveItem(),
        next = this.panel.is(':hidden') ? activeItem.nextAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):first')
                                : activeItem.nextAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):visible:first');

        if(event.altKey) {
            this.show();
        }
        else {
            if(next.length === 1) {
                if(this.panel.is(':hidden')) {
                    this.selectItem(next);
                }
                else {
                    this.highlightItem(next);
                    PrimeFaces.scrollInView(this.itemsWrapper, next);
                }
                this.changeAriaValue(next);
            }
        }

        event.preventDefault();
    },

    /**
     * Highlights the previous option before the currently highlighted option in the overlay panel.
     * @private
     * @param {JQuery.TriggeredEvent} event The event of the keypress.
     */
    highlightPrev: function(event) {
        var activeItem = this.getActiveItem(),
        prev = this.panel.is(':hidden') ? activeItem.prevAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):first')
                                : activeItem.prevAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):visible:first');

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
        }

        event.preventDefault();
        event.stopPropagation();
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
        }

        event.preventDefault();
    },

    /**
     * Callback for when the tab key was pressed. Selects the next option.
     * @private
     */
    handleTabKey: function() {
        if(this.panel.is(':visible')) {
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

        this.panel.css({'display':'block', 'opacity':'0', 'pointer-events': 'none'});
        this.itemsWrapper.css({'overflow': 'scroll'});

        this.alignPanel();

        this.panel.css({'display':'none', 'opacity':'', 'pointer-events': '', 'z-index': PrimeFaces.nextZindex()});
        this.itemsWrapper.css({'overflow': ''});

        if(this.cfg.effect !== 'none') {
            this.panel.show(this.cfg.effect, {}, this.cfg.effectSpeed, function() {
                PrimeFaces.scrollInView($this.itemsWrapper, $this.getActiveItem());

                if($this.cfg.filter)
                    $this.focusFilter();
            });
        }
        else {
            this.panel.show();
            PrimeFaces.scrollInView(this.itemsWrapper, this.getActiveItem());

            if($this.cfg.filter)
                this.focusFilter(10);
        }

        //value before panel is shown
        this.preShowValue = this.options.filter(':selected');
        this.jq.attr('aria-expanded', true);
    },

    /**
     * Hides the overlay panel with the available selectable options.
     */
    hide: function() {
        if (this.panel.is(':visible')) {
            this.panel.css('z-index', '').hide();
            this.jq.attr('aria-expanded', false);
        }
    },

    /**
     * Puts focus on this widget.
     */
    focus: function() {
        this.focusInput.trigger('focus');
    },

    /**
     * Puts focus on the filter input in the overlay panel.
     * @param {number | undefined} timeout Amount of time in milliseconds to wait before attempting to focus the input. 
     */
    focusFilter: function(timeout) {
        if(timeout) {
            var $this = this;
            setTimeout(function() {
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
        this.focusInput.trigger("blur");

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
                top: this.jq.innerHeight() + 'px'
            });
        }
        else {
            this.panel.css({left:'0px', top:'0px'}).position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
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

        if (this.cfg.editable) {
            if (value === '&nbsp;')
                this.label.val('');
            else
                this.label.val(displayedLabel);

            var hasPlaceholder = this.label[0].hasAttribute('placeholder');
            this.updatePlaceholderClass((hasPlaceholder && value === '&nbsp;'));
        }
        else if (this.cfg.alwaysDisplayLabel && this.cfg.label) {
            this.label.text(this.cfg.label);
        }
        else {
            var labelText = this.label.data('placeholder');
            if (labelText == null || labelText == "") {
                labelText = '&nbsp;';
            }

            this.updatePlaceholderClass((value === '&nbsp;' && labelText !== '&nbsp;'));

            if (value === '&nbsp;') {
                if (labelText != '&nbsp;') {
                   this.label.text(labelText);
                } else {
                    this.label.html(labelText);
                }
            }
            else {
                this.label.removeClass('ui-state-disabled');

                var option = null;
                if(this.items) {
                    var selectedItem = this.items.filter('[data-label="' + $.escapeSelector(value) + '"]');
                    option = this.options.eq(this.resolveItemIndex(selectedItem));
                }
                else {
                    option = this.options.filter(':selected');
                }

                if (option && option.data('escape') === false) {
                    this.label.html(displayedLabel);
                } else {
                    this.label.text(displayedLabel);
                }
            }
        }
    },

    /**
     * Selects the option with the given value.
     * @param {string} value Value of the option to select.
     */
    selectValue : function(value) {
        var option = this.options.filter('[value="' + $.escapeSelector(value) + '"]');

        this.selectItem(this.items.eq(option.index()), true);
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
        var filterValue = this.cfg.caseSensitive ? PrimeFaces.trim(value) : PrimeFaces.trim(value).toLowerCase();

        if(filterValue === '') {
            this.items.filter(':hidden').show();
            this.itemsContainer.children('.ui-selectonemenu-item-group').show();
        }
        else {
            var hide = [];
            var show = [];

            for(var i = 0; i < this.options.length; i++) {
                var option = this.options.eq(i),
                itemLabel = this.cfg.caseSensitive ? option.text() : option.text().toLowerCase(),
                item = this.items.eq(i);

                if(item.hasClass('ui-noselection-option')) {
                    hide.push(item);
                }
                else {
                    if(this.filterMatcher(itemLabel, filterValue))
                        show.push(item);
                    else
                        hide.push(item);
                }
            }

            $.each(hide, function(i, o) { o.hide() });
            $.each(show, function(i, o) { o.show() });
            hide = [];
            show = [];

            //Toggle groups
            var groups = this.itemsContainer.children('.ui-selectonemenu-item-group');
            for(var g = 0; g < groups.length; g++) {
                var group = groups.eq(g);

                if(g === (groups.length - 1)) {
                    if(group.nextAll().filter(':visible').length === 0)
                        hide.push(group);
                    else
                        show.push(group);
                }
                else {
                    if(group.nextUntil('.ui-selectonemenu-item-group').filter(':visible').length === 0)
                        hide.push(group);
                    else
                        show.push(group);
                }
            }

            $.each(hide, function(i, o) { o.hide() });
            $.each(show, function(i, o) { o.show() });
        }

        var firstVisibleItem = this.items.filter(':visible:not(.ui-state-disabled):first');
        if(firstVisibleItem.length) {
            this.highlightItem(firstVisibleItem);
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

        this.focusInput.attr('aria-activedescendant', itemId)
                .attr('aria-describedby', itemId);
        this.itemsContainer.attr('aria-activedescendant', itemId);
    },

    /**
     * Loads the overlay panel with the selectable options, if dynamic mode is enabled.
     * @private
     */
    dynamicPanelLoad: function() {
        var $this = this,
        options = {
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

                        var $ul = $content.filter('ul');
                        $this.itemsWrapper.empty();
                        $this.itemsWrapper.append($ul);

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
                    handleMethod.call($this, event);

                    clearInterval(interval);
                }
            }, 10);
        }
        else {
            this.renderPanelContentFromHiddenSelect(true);

            handleMethod.call(this, event);
        }
    },

    /**
     * Renders panel content based on hidden select.
     * @private
     * @param {boolean} initContentsAndBindItemEvents `true` to call {@link initContents} and {@link bindItemEvents}
     * after rendering, `false` otherwise.
     */
    renderPanelContentFromHiddenSelect: function(initContentsAndBindItemEvents) {
         if (this.cfg.renderPanelContentOnClient && this.itemsWrapper.children().length === 0) {
             var panelContent = '<ul id="' + this.id + '_items" class="ui-selectonemenu-items ui-selectonemenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset" role="listbox">';
             panelContent += this.renderSelectItems(this.input);
             panelContent += '</ul>';

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
        opts.each(function(index, element) {
            content += $this.renderSelectItem(element, isGrouped);
        });
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
        var content = "";
        var $item = $(item);
        var label;
        var title = $item.data("title");
        var escape = $item.data("escape");
        var cssClass;

        if (item.tagName === "OPTGROUP") {
            label = $item.attr("label");
            if (escape) {
                label = $("<div>").text(label).html();
            }
            cssClass = "ui-selectonemenu-item-group ui-corner-all";
        }
        else { //OPTION
            if (escape) {
                label = $item.html();
                if ($item.text() === "&nbsp;") {
                    label = $item.text();
                }
            }
            else {
                label = $item.text();
            }
            cssClass = "ui-selectonemenu-item ui-selectonemenu-list-item ui-corner-all";
            if (isGrouped) {
                cssClass += " ui-selectonemenu-item-group-children"
            }
        }

        var dataLabel = label.replace(/(<([^>]+)>)/gi, "");
        if ($item.data("noselection-option")) {
            cssClass += " ui-noselection-option";
        }

        content += '<li class="' + cssClass + '" tabindex="-1" role="option"';
        if (title) {
            content += ' title="' + title + '"';
        }
        if ($item.is(':disabled')) {
            content += ' disabled';
        }
        content += ' data-label="' + dataLabel + '"';
        content += '>';
        content += label;
        content += '</li>';

        if (item.tagName === "OPTGROUP") {
            content += this.renderSelectItems($item, true);
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
