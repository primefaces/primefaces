/**
 * __PrimeFaces AutoComplete Widget__
 *
 * AutoComplete provides live suggestions while the user is entering text
 *
 * @typedef {"blank" | "current"} PrimeFaces.widget.AutoComplete.DropdownMode Specifies the behavior of the dropdown
 * button.
 * - `blank`: Sends an empty string.
 * - `current`: Send the input value.
 *
 * @typedef {"keyup" | "enter"} PrimeFaces.widget.AutoComplete.QueryEvent  Event to initiate the autocomplete search.
 * - `enter`: Starts the search for suggestion items when the enter key is pressed.
 * - `keyup`: Starts the search for suggestion items as soon as a key is released.
 *
 * @typedef {"server" | "client" | "hybrid"} PrimeFaces.widget.AutoComplete.QueryMode Specifies whether filter requests
 * are evaluated by the client's browser or whether they are sent to the server.
 *
 * @typedef PrimeFaces.widget.AutoComplete.OnChangeCallback Client side callback to invoke when value changes.
 * @param {JQuery} PrimeFaces.widget.AutoComplete.OnChangeCallback.input (Input) element on which the change occurred.
 *
 * @prop {boolean} active Whether the autocomplete is active.
 * @prop {Record<string, string>} [cache] The cache for the results of an autocomplete search.
 * @prop {number} [cacheTimeout] The set-interval timer ID for the cache timeout.
 * @prop {boolean} [checkMatchedItem] Whether the click event is fired on the selected items when a `blur` occurs.
 * @prop {number} [colspan] Column span count for the options in the overlay panel with the available completion items.
 * @prop {string} [currentGroup] Current option group when creating the options in the overlay  with the available
 * completion items.
 * @prop {string} currentInputValue Current value in the input field where the user can search for completion items.
 * @prop {string[]} [currentItems] Currently selected items, when `forceSelection` is enabled.
 * @prop {string} [currentText] Text currently entered in the input field.
 * @prop {JQuery} dropdown The DOM element for the container with the dropdown suggestions.
 * @prop {PrimeFaces.UnbindCallback} [hideOverlayHandler] Unbind callback for the hide overlay handler.
 * @prop {JQuery} input The DOM element for the input element.
 * @prop {boolean} isDynamicLoaded If dynamic loading is enabled, whether the content was loaded already.
 * @prop {boolean} isTabPressed Whether the tab key is currently pressed.
 * @prop {JQuery} hinput The DOM element for the hidden input with the selected value.
 * @prop {JQuery} inputContainer When multiple mode is enabled that allows multiple items to be selected: The DOM
 * element of the container with the input element used to enter text and search for an item to select.
 * @prop {boolean} [isSearchWithDropdown] Whether to use a drop down menu when searching for completion options.
 * @prop {JQuery} [items] The DOM elements for the suggestion items.
 * @prop {JQuery} itemtip The DOM element for the tooltip of a suggestion item.
 * @prop {boolean} [itemClick] Whether an item was clicked.
 * @prop {JQuery} [itemContainer] The DOM element for the container with the suggestion items.
 * @prop {boolean} [itemSelectedWithEnter] Whether an item was selected via the enter key.
 * @prop {JQuery} [multiItemContainer] The DOM element for the container with multiple selection items.
 * @prop {JQuery} panel The DOM element for the overlay panel with the suggestion items.
 * @prop {string} panelId The client ID of the overlay panel with the suggestion items.
 * @prop {string} placeholder Placeholder shown in the input field when no text is entered.
 * @prop {boolean} [preventInputChangeEvent] Whether to suppress the change event when the input's value changes.
 * @prop {string} [previousText] Text previously entered in the input field.
 * @prop {boolean} [querying] Whether an AJAX request for the autocompletion items is currently in progress.
 * @prop {PrimeFaces.UnbindCallback} [resizeHandler] Unbind callback for the resize handler.
 * @prop {PrimeFaces.UnbindCallback} [scrollHandler] Unbind callback for the scroll handler.
 * @prop {number} requestId Tracking number to make sure search requests match up in query mode
 * @prop {JQuery} status The DOM element for the autocomplete status ARIA element.
 * @prop {boolean} suppressInput Whether key input events should be ignored currently.
 * @prop {number} [timeout] Timeout ID for the timer used to clear the autocompletion cache in regular
 * intervals.
 * @prop {boolean} touchToDropdownButton Whether a touch is made on the dropdown button.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * @prop {string} wrapperStartTag The starting HTML with the wrapper element of the suggestions box.
 * @prop {string} wrapperEndTag The finishing HTML with the wrapper element of the suggestions box.
 * @prop {string} resultsMessage Hint text for screen readers to provide information about the search results.
 * @prop {string} emptyMessage Text to display when there is no data to display.
 *
 * @interface {PrimeFaces.widget.AutoCompleteCfg} cfg The configuration for the {@link  AutoComplete| AutoComplete widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.active Whether autocompletion search is initially active.
 * @prop {string} cfg.appendTo ID of the container to which the suggestion box is appended.
 * @prop {string} cfg.atPos Defines which position on the target element to align the positioned element against.
 * @prop {boolean} cfg.autoHighlight Highlights the first suggested item automatically.
 * @prop {boolean} cfg.autoSelection Defines if auto selection of items that are equal to the typed input is enabled. If
 * `true`, an item that is equal to the typed input is selected.
 * @prop {boolean} cfg.cache When enabled autocomplete caches the searched result list.
 * @prop {number} cfg.cacheTimeout Timeout in milliseconds value for cached results.
 * @prop {number} cfg.delay The delay in milliseconds before an autocomplete search is triggered.
 * @prop {PrimeFaces.widget.AutoComplete.DropdownMode} cfg.dropdownMode Specifies the behavior of the dropdown button.
 * @prop {boolean} cfg.dynamic Defines if dynamic loading is enabled for the element's panel. If the value is `true`,
 * the overlay is not rendered on page load to improve performance.
 * @prop {boolean} cfg.escape Whether the text of the suggestion items is escaped for HTML.
 * @prop {boolean} cfg.hasFooter Whether a footer facet is present.
 * @prop {boolean} cfg.forceSelection Whether one of the available suggestion items is forced to be preselected.
 * @prop {boolean} cfg.grouping Whether suggestion items are grouped.
 * @prop {boolean} cfg.itemtip Whether a tooltip is shown for the suggestion items.
 * @prop {string} cfg.itemtipAtPosition Position of item corner relative to item tip.
 * @prop {string} cfg.itemtipMyPosition Position of itemtip corner relative to item.
 * @prop {number} cfg.minLength Minimum length before an autocomplete search is triggered.
 * @prop {boolean} cfg.multiple When `true`, enables multiple selection.
 * @prop {string} cfg.myPos Defines which position on the element being positioned to align with the target element.
 * @prop {PrimeFaces.widget.AutoComplete.OnChangeCallback} cfg.onChange Client side callback to invoke when value
 * changes.
 * @prop {PrimeFaces.widget.AutoComplete.QueryEvent} cfg.queryEvent Event to initiate the the autocomplete search.
 * @prop {PrimeFaces.widget.AutoComplete.QueryMode} cfg.queryMode Specifies query mode, whether autocomplete contacts
 * the server.
 * @prop {number} cfg.selectLimit Limits the number of simultaneously selected items. Default is unlimited.
 * @prop {number} cfg.scrollHeight Height of the container with the suggestion items.
 * @prop {boolean} cfg.unique Ensures uniqueness of the selected items.
 * @prop {string} cfg.completeEndpoint REST endpoint for fetching autocomplete suggestions. Takes precedence over the
 * bean command specified via `completeMethod` on the component.
 * @prop {string} cfg.moreText The text shown in the panel when the number of suggestions is greater than `maxResults`.
 */
PrimeFaces.widget.AutoComplete = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.panelId = this.jqId + '_panel';
        this.input = $(this.jqId + '_input');
        this.hinput = $(this.jqId + '_hinput');
        this.panel = this.jq.children(this.panelId);
        this.dropdown = this.jq.children('.ui-button');
        this.active = true;
        this.cfg.pojo = this.hinput.length == 1;
        this.cfg.minLength = this.cfg.minLength != undefined ? this.cfg.minLength : 1;
        this.cfg.cache = this.cfg.cache || false;
        this.cfg.dropdownMode = this.cfg.dropdownMode || 'blank';
        this.cfg.autoHighlight = (this.cfg.autoHighlight === undefined) ? true : this.cfg.autoHighlight;
        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this, this.jq, this.panel);
        this.cfg.myPos = this.cfg.myPos || 'left top';
        this.cfg.atPos = this.cfg.atPos || 'left bottom';
        this.cfg.active = (this.cfg.active === false) ? false : true;
        this.cfg.dynamic = this.cfg.dynamic === true ? true : false;
        this.cfg.autoSelection = this.cfg.autoSelection === false ? false : true;
        this.cfg.escape = this.cfg.escape === false ? false : true;
        this.cfg.hasFooter = this.cfg.hasFooter === true ? true : false;
        this.cfg.forceSelection = this.cfg.forceSelection === true ? true : false;
        this.suppressInput = true;
        this.touchToDropdownButton = false;
        this.isTabPressed = false;
        this.isDynamicLoaded = false;
        this.currentInputValue = '';
        this.configureLocale();

        if (this.cfg.cache) {
            this.initCache();
        }

        if (this.cfg.queryMode !== 'server') {
            this.fetchItems();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        this.placeholder = this.input.attr('placeholder');

        if (this.cfg.multiple) {
            this.setupMultipleMode();

            this.multiItemContainer.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

            if (this.cfg.selectLimit >= 0 && this.multiItemContainer.children('li.ui-autocomplete-token').length === this.cfg.selectLimit) {
                this.input.hide();
                this.disableDropdown();
            }
        }
        else {
            //visuals
            PrimeFaces.skinInput(this.input);

            this.input.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
            this.dropdown.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
        }

        //core events
        this.bindStaticEvents();

        //client Behaviors
        if (this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
        }

        //force selection
        if (this.cfg.forceSelection) {
            this.setupForceSelection();
        }

        //Panel management
        if (this.panel.length) {
            this.appendPanel();
            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');
        }

        //itemtip
        if (this.cfg.itemtip) {
            this.itemtip = $('<div id="' + this.id + '_itemtip" class="ui-autocomplete-itemtip ui-state-highlight ui-widget ui-corner-all ui-shadow"></div>').appendTo(document.body);
            this.cfg.itemtipMyPosition = this.cfg.itemtipMyPosition || 'left top';
            this.cfg.itemtipAtPosition = this.cfg.itemtipAtPosition || 'right bottom';
            this.cfg.checkForScrollbar = (this.cfg.itemtipAtPosition.indexOf('right') !== -1);
        }

        //aria
        this.input.attr('aria-autocomplete', 'list');
        this.jq.append('<span role="status" aria-live="polite" class="ui-autocomplete-status ui-helper-hidden-accessible"></span>');
        this.status = this.jq.children('.ui-autocomplete-status');
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this._super(cfg);
    },

    /**
     * Localizes the ARIA accessibility labels for the autocomplete.
     * @private
     */
    configureLocale: function() {
        this.emptyMessage = PrimeFaces.getLocaleLabel('emptySearchMessage');
        this.resultsMessage = PrimeFaces.getLocaleLabel('searchMessage');
        if (this.dropdown) {
            this.dropdown.attr('aria-label', PrimeFaces.getLocaleLabel('choose'));
        }
    },

    /**
     * Appends the overlay panel to the DOM.
     * @private
     */
    appendPanel: function() {
        PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
    },

    /**
     * Initializes the cache that stores the retrieved suggestions for a search term.
     * @private
     */
    initCache: function() {
        this.cache = {};
        var $this = this;

        this.cacheTimeout = setInterval(function() {
            $this.clearCache();
        }, this.cfg.cacheTimeout);
    },

    /**
     * Clears the cache with the results of an autocomplete search.
     * @private
     */
    clearCache: function() {
        this.cache = {};
    },

    /**
     * Binds events for multiple selection mode.
     * @private
     */
    setupMultipleMode: function() {
        var $this = this;
        this.multiItemContainer = this.jq.children('ul');
        this.inputContainer = this.multiItemContainer.children('.ui-autocomplete-input-token');

        this.multiItemContainer.on("mouseenter", function() {
            $(this).addClass('ui-state-hover');
        }).on("mouseleave", function() {
            $(this).removeClass('ui-state-hover');
        }).on("click", function() {
            $this.input.trigger('focus');
        });

        //delegate events to container
        this.input.on("focus", function() {
            $this.multiItemContainer.addClass('ui-state-focus');
        }).on("blur", function(e) {
            $this.multiItemContainer.removeClass('ui-state-focus');
        });

        var closeSelector = '> li.ui-autocomplete-token > .ui-autocomplete-token-icon';
        this.multiItemContainer.off('click', closeSelector).on('click', closeSelector, null, function(event) {
            if ($this.multiItemContainer.children('li.ui-autocomplete-token').length === $this.cfg.selectLimit) {
                $this.input.css('display', 'inline');
                $this.enableDropdown();
            }
            $this.removeItem($(this).parent());
        });
    },

    /**
     * Sets up all global event listeners for the overlay.
     * @private
     */
    bindStaticEvents: function() {
        var $this = this;

        this.bindKeyEvents();

        this.bindDropdownEvents();

        if (PrimeFaces.env.browser.mobile) {
            this.dropdown.on('touchstart', function() {
                $this.touchToDropdownButton = true;
            });
        }
    },

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents: function() {
        var $this = this;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            function() { return $this.itemtip; },
            function(e, eventTarget) {
                if (!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, function() {
            $this.handleViewportChange();
        });

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
     * Sets up all event listeners for the dropdown menu.
     * @private
     */
    bindDropdownEvents: function() {
        var $this = this;

        PrimeFaces.skinButton(this.dropdown);

        this.dropdown.on("mouseup", function() {
            if ($this.active) {
                $this.searchWithDropdown();
                $this.input.trigger('focus');
            }
        }).on("keyup", function(e) {
            if (PrimeFaces.utils.isActionKey(e)) {
                $this.searchWithDropdown();
                $this.input.trigger('focus');
                e.preventDefault();
                e.stopPropagation();
            }
        });
    },

    /**
     * Disables the dropdown menu.
     * @private
     */
    disableDropdown: function() {
        if (this.dropdown.length) {
            this.dropdown.off().prop('disabled', true).addClass('ui-state-disabled');
        }
    },

    /**
     * Enables the dropdown menu.
     * @private
     */
    enableDropdown: function() {
        if (this.dropdown.length && this.dropdown.prop('disabled')) {
            this.bindDropdownEvents();
            this.dropdown.prop('disabled', false).removeClass('ui-state-disabled');
        }
    },

    /**
     * Sets up all keyboard related event listeners.
     * @private
     */
    bindKeyEvents: function() {
        var $this = this;

        // GitHub #6711 use DOM if non-CSP and JQ event if CSP
        var originalOnchange = this.input.prop('onchange');
        if (!originalOnchange && this.input[0]) {
            var events = $._data(this.input[0], "events");
            if (events.change) {
                originalOnchange = events.change[0].handler;
            }
        }
        this.cfg.onChange = originalOnchange;
        if (originalOnchange) {
            this.input.prop('onchange', null).off('change');
        }

        if (this.cfg.queryEvent !== 'enter') {
            this.input.on('input propertychange', function(e) {
                $this.processKeyEvent(e);
            });
        }

        this.input.on('keyup.autoComplete', function(e) {
            var key = e.key;

            if ($this.cfg.queryEvent === 'enter' && (key === 'Enter')) {
                if ($this.itemSelectedWithEnter)
                    $this.itemSelectedWithEnter = false;
                else
                    $this.search($this.input.val());
            }

            if ($this.panel.is(':visible')) {
                if (key === 'Escape') {
                    $this.hide();
                }
                else if (key === 'ArrowUp' || key === 'ArrowDown') {
                    var highlightedItem = $this.items.filter('.ui-state-highlight');
                    if (highlightedItem.length) {
                        $this.displayAriaStatus(highlightedItem.data('item-label'));
                        $this.changeAriaValue(highlightedItem[0]);
                    }
                }
            }

            $this.checkMatchedItem = true;
            $this.isTabPressed = false;

        }).on('keydown.autoComplete', function(e) {
            $this.suppressInput = false;
            if ($this.panel.is(':visible')) {
                var highlightedItem = $this.items.filter('.ui-state-highlight');

                switch (e.key) {
                    case 'ArrowUp':
                        var prev = highlightedItem.length == 0 ? $this.items.eq(0) : highlightedItem.prevAll('.ui-autocomplete-item:first');

                        if (prev.length == 1) {
                            $this.highlightItem(highlightedItem, false);
                            $this.highlightItem(prev, true);

                            if ($this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView($this.panel, prev);
                            }

                            if ($this.cfg.itemtip) {
                                $this.showItemtip(prev);
                            }
                        }

                        e.preventDefault();
                        break;

                    case 'ArrowDown':
                        var next = highlightedItem.length == 0 ? $this.items.eq(0) : highlightedItem.nextAll('.ui-autocomplete-item:first');

                        if (next.length == 1) {
                            $this.highlightItem(highlightedItem, false);
                            $this.highlightItem(next, true);

                            if ($this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView($this.panel, next);
                            }

                            if ($this.cfg.itemtip) {
                                $this.showItemtip(next);
                            }
                        }

                        e.preventDefault();
                        break;

                    case 'Enter':
                        if ($this.timeout) {
                            $this.deleteTimeout();
                        }

                        if (highlightedItem.length > 0) {
                            $this.preventInputChangeEvent = true;
                            highlightedItem.trigger("click");
                            $this.itemSelectedWithEnter = true;
                        }

                        e.preventDefault();
                        e.stopPropagation();

                        break;

                    case 'Alt':
                        break;

                    case 'Tab':
                        if (highlightedItem.length && $this.cfg.autoSelection) {
                            highlightedItem.trigger('click');
                        } else {
                            $this.hide();
                            if ($this.timeout) {
                                $this.deleteTimeout();
                            }
                        }
                        $this.isTabPressed = true;
                        break;
                }
            }
            else {
                switch (e.key) {
                    case 'Tab':
                        if ($this.timeout) {
                            $this.deleteTimeout();
                        }
                        $this.isTabPressed = true;
                        break;

                    case 'Enter':
                        var itemValue = $(this).val();
                        var valid = true;
                        if ($this.cfg.queryEvent === 'enter' || ($this.timeout > 0) || $this.querying) {
                            e.preventDefault();
                        }

                        if ($this.cfg.queryEvent !== 'enter') {
                            valid = $this.isValid(itemValue, true);
                            if (!$this.cfg.forceSelection) {
                                valid = true;
                            }
                        }

                        if ($this.cfg.multiple && itemValue) {
                            if (valid) {
                                $this.addItem(itemValue);
                            }

                            e.preventDefault();
                            e.stopPropagation();
                        }
                        break;

                    case 'Backspace':
                        if ($this.cfg.multiple && !$this.input.val().length) {

                            if (e.metaKey || e.ctrlKey || e.shiftKey) {
                                $this.removeAllItems();
                            } else {
                                $this.removeItem($(this).parent().prev());
                            }

                            e.preventDefault();
                        }
                        break;
                };
            }

        }).on('paste.autoComplete', function() {
            $this.suppressInput = false;
            $this.checkMatchedItem = true;
        }).on('change.autoComplete', function(e) {
            var value = e.currentTarget.value,
                valid = $this.isValid(value, true);

            if ($this.cfg.forceSelection && $this.currentInputValue === '' && !valid) {
                $this.preventInputChangeEvent = true;
            }

            if ($this.cfg.onChange && !$this.preventInputChangeEvent) {
                $this.cfg.onChange.call(this);
            }

            $this.currentInputValue = $this.cfg.forceSelection && !valid ? '' : value;
            $this.preventInputChangeEvent = false;
        });
    },

    /**
     * Sets up all event listeners for mouse and click events.
     * @private
     */
    bindDynamicEvents: function() {
        var $this = this;

        //visuals and click handler for items
        this.items.off('click.autocomplete mousedown.autocomplete mouseover.autocomplete')
            .on('mouseover.autocomplete', function() {
                var item = $(this);

                if (!item.hasClass('ui-state-highlight')) {
                    $this.items.filter('.ui-state-highlight').removeClass('ui-state-highlight').attr('aria-selected', false);
                    $this.highlightItem(item, true);

                    if ($this.cfg.itemtip) {
                        $this.showItemtip(item);
                    }
                }
            })
            .on('click.autocomplete', function(event) {
                var item = $(this),
                    isMoreText = item.hasClass('ui-autocomplete-moretext');

                if (isMoreText) {
                    $this.input.trigger('focus');
                    $this.invokeMoreTextBehavior();
                }
                else {
                    $this.addItem(item);
                }

                $this.hide();
            })
            .on('mousedown.autocomplete', function() {
                $this.preventInputChangeEvent = true;
                $this.checkMatchedItem = false;
            });

        this.panel.on('click.emptyMessage', function() {
            if (!this.children) {
                return;
            }
            var item = $(this.children[0]),
                isEmptyMessage = item.hasClass('ui-autocomplete-empty-message');

            if (isEmptyMessage) {
                $this.invokeEmptyMessageBehavior();
            }
        });

        if (PrimeFaces.env.browser.mobile) {
            this.items.on('touchstart.autocomplete', function() {
                if (!$this.touchToDropdownButton) {
                    $this.itemClick = true;
                }
            });
        }
    },

    /**
     * Callback for when a key event occurred.
     * @private
     * @param {JQuery.TriggeredEvent} e Key event that occurred.
     */
    processKeyEvent: function(e) {
        var $this = this;

        if ($this.suppressInput) {
            e.preventDefault();
            return;
        }

        // for touch event on mobile
        if (PrimeFaces.env.browser.mobile) {
            $this.touchToDropdownButton = false;
            if ($this.itemClick) {
                $this.itemClick = false;
                return;
            }
        }

        var value = $this.input.val();

        if ($this.cfg.pojo && !$this.cfg.multiple) {
            $this.hinput.val(value);
        }

        if (!value.length) {
            $this.hide();
            $this.deleteTimeout();
        }

        if (value.length >= $this.cfg.minLength) {
            if ($this.timeout) {
                $this.deleteTimeout();
            }
            $this.timeout = PrimeFaces.queueTask(function() {
                $this.timeout = null;
                $this.search(value);
            }, $this.cfg.delay);
        }
        else if (value.length === 0) {
            if ($this.timeout) {
                $this.deleteTimeout();
            }
            $this.fireClearEvent();
        }
    },

    /**
     * Shows the tooltip for the given suggestion item.
     * @private
     * @param {JQuery} item Item with a tooltip.
     */
    showItemtip: function(item) {
        if (item.hasClass('ui-autocomplete-moretext')) {
            this.itemtip.hide();
        }
        else {
            var content;
            if (item.is('li')) {
                content = item.next('.ui-autocomplete-itemtip-content');
            } else {
                if (item.children('td:last').hasClass('ui-autocomplete-itemtip-content')) {
                    content = item.children('td:last');
                } else {
                    this.itemtip.hide();
                    return;
                }
            }

            this.itemtip.html(content.html())
                .css({
                    'left': '',
                    'top': '',
                    'z-index': PrimeFaces.nextZindex(),
                    'width': content.outerWidth() + 'px'
                })
                .position({
                    my: this.cfg.itemtipMyPosition
                    , at: this.cfg.itemtipAtPosition
                    , of: item
                });

            //scrollbar offset
            if (this.cfg.checkForScrollbar) {
                if (this.panel.innerHeight() < this.panel.children('.ui-autocomplete-items').outerHeight(true)) {
                    var panelOffset = this.panel.offset();
                    this.itemtip.css('left', (panelOffset.left + this.panel.outerWidth()) + 'px');
                }
            }

            this.itemtip.show();
        }
    },

    /**
     * Performs the search for the available suggestion items.
     * @private
     * @param {string} query Keyword for the search.
     */
    showSuggestions: function(query) {
        this.items = this.panel.find('.ui-autocomplete-item');
        this.items.attr('role', 'option');

        if (this.cfg.grouping) {
            this.groupItems();
        }

        this.bindDynamicEvents();

        var $this = this,
            hidden = this.panel.is(':hidden');

        if (hidden) {
            this.show();
        }
        else {
            this.alignPanel();
        }

        // #8717 always clear list before trying to fill it
        if (this.cfg.forceSelection) {
            this.currentItems = [];
        }

        if (this.items.length > 0) {
            var firstItem = this.items.eq(0);

            //highlight first item
            if (this.cfg.autoHighlight && firstItem.length) {
                this.highlightItem(firstItem, true);
                this.changeAriaValue(firstItem[0]);
            }

            //highlight query string
            if (query.length > 0) {
                var cleanedQuery = query.trim().replaceAll(/(\s+)/g, ' ');
                if (cleanedQuery.length > 0) {
                    var queryParts = cleanedQuery.split(' ');
                    for (var i = 0; i < queryParts.length; i++) {
                        queryParts[i] = PrimeFaces.escapeRegExp(queryParts[i]);
                    }
                    var re = new RegExp('(' + queryParts.join('|') + ')', 'gi');
                    var isCustomContent = this.panel.children().is('table');
                    var queryResults = isCustomContent ? this.panel.children().find('span') : this.items;
                    queryResults.filter(':not(.ui-autocomplete-moretext)').each(function() {
                        var item = $(this);
                        var escape = $this.cfg.escape;
                        var text = escape ? item.html() : item.text();
                        var escaped_re = escape ? /${PrimeFaces.escapeHTML(text)}/g : /${text}/g;
                        item.html(text.replace(escaped_re, '<span class="ui-autocomplete-query">$&</span>'));
                    });
                }
            }

            if (this.cfg.forceSelection) {
                this.items.each(function(i, item) {
                    $this.currentItems.push($(item).attr('data-item-label'));
                });
            }

            //show itemtip if defined
            if (this.cfg.autoHighlight && this.cfg.itemtip && firstItem.length === 1) {
                this.showItemtip(firstItem);
            }

            this.displayAriaStatus(this.resultsMessage.replace('{0}', this.items.length));
        }
        else {
            if (this.emptyMessage && this.cfg.forceSelection) {
                var emptyText = '<div class="ui-autocomplete-empty-message ui-widget">' + PrimeFaces.escapeHTML(this.emptyMessage) + '</div>';
                this.panel.prepend(emptyText);
            }
            else if (!this.cfg.hasFooter) {
                this.panel.hide();
            }

            this.input.removeAttr('aria-activedescendant');
            this.displayAriaStatus(this.emptyMessage);
        }
    },

    /**
     * Performs a search the same ways as if the user had opened the dropdown menu. Depending on the configured
     * `dropdownMode`, performs the search either with an empty string or with the current value.
     */
    searchWithDropdown: function() {
        this.isSearchWithDropdown = true;

        if (this.cfg.dropdownMode === 'current')
            this.search(this.input.val());
        else
            this.search('');
    },

    /**
     * Initiates a search with given value, that is, look for matching options and present the options that were found
     * to the user.
     * @param {string} query Keyword for the search.
     */
    search: function(query) {
        //allow empty string but not undefined or null
        if (!this.cfg.active || query === undefined || query === null) {
            return;
        }

        if (this.cfg.cache && !(this.cfg.dynamic && !this.isDynamicLoaded)) {
            if (this.cache[query]) {
                this.panel.html(this.cache[query]);
                this.showSuggestions(query);
                return;
            }
            else if (this.cfg.queryMode === 'client') {
                if (this.isSearchWithDropdown) {
                    var suggestions = this.wrapperStartTag,
                        re = new RegExp(this.wrapperStartTag + '|' + this.wrapperEndTag, 'g');
                    Object.entries(this.cache).map(function(item) {
                        suggestions += item[1].replace(re, '');
                    });
                    suggestions += this.wrapperEndTag;

                    this.panel.html(suggestions);

                    this.isSearchWithDropdown = false;
                }
                else {
                    this.panel.empty();
                }

                this.showSuggestions(query);
                return;
            }
        }

        if (!this.active) {
            return;
        }

        this.setQuerying(true);

        var $this = this;

        if (this.cfg.itemtip) {
            this.itemtip.hide();
        }

        var options;

        if (!this.cfg.completeEndpoint) {
            this.requestId = this.requestId + 1 || 1;
            var currentRequestId = this.requestId;
            options = {
                source: this.id,
                process: this.id,
                update: this.id,
                formId: this.getParentFormId(),
                onsuccess: function(responseXML, status, xhr) {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            // #8725 ensure its the same request when slow server
                            if (this.requestId !== currentRequestId) {
                                return;
                            }
                            if (this.cfg.dynamic && !this.isDynamicLoaded) {
                                this.panel = $(content);
                                this.appendPanel();
                                this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');
                                content = this.panel.get(0).innerHTML;
                            } else {
                                this.panel.html(content);
                            }

                            if (this.cfg.cache) {
                                if (this.cfg.queryMode !== 'server' && !this.isDynamicLoaded && this.cache[query]) {
                                    this.panel.html(this.cache[query]);
                                } else {
                                    this.cache[query] = content;
                                }
                            }

                            this.showSuggestions(query);
                        }
                    });

                    return true;
                },
                oncomplete: function() {
                    $this.setQuerying(false);
                    $this.isDynamicLoaded = $this.requestId === currentRequestId;
                }
            };

            options.params = [
                { name: this.id + '_query', value: query }
            ];

            if (this.cfg.queryMode === 'hybrid') {
                options.params.push({ name: this.id + '_clientCache', value: true });
            }

            if (this.cfg.dynamic && !this.isDynamicLoaded) {
                options.params.push({ name: this.id + '_dynamicload', value: true });
            }
        }

        if (this.hasBehavior('query')) {
            this.callBehavior('query', options);
        }
        else {
            if (this.cfg.completeEndpoint) {
                $.ajax({
                    url: this.cfg.completeEndpoint,
                    data: { query: query },
                    dataType: 'json'
                })
                    .done(function(suggestions) {
                        var html = '<ul class="ui-autocomplete-items ui-autocomplete-list ui-widget-content ui-widget ui-corner-all ui-helper-reset" role="listbox">';
                        suggestions.suggestions.forEach(function(suggestion, index) {
                            var labelEncoded = $("<div>").text(suggestion.label).html();
                            var itemValue = labelEncoded;
                            if (!!suggestion.value) {
                                itemValue = $("<div>").text(suggestion.value).html();
                            }
                            html += '<li id="' + $this.id + '_item_' + index + '" class="ui-autocomplete-item ui-autocomplete-list-item ui-corner-all" data-item-value="' + PrimeFaces.escapeHTML(itemValue) + '" data-item-label="' + PrimeFaces.escapeHTML(labelEncoded) + '" role="option">' + PrimeFaces.escapeHTML(labelEncoded) + '</li>';
                        });
                        if (suggestions.moreAvailable == true && $this.cfg.moreText) {
                            var moreTextEncoded = $("<div>").text($this.cfg.moreText).html();
                            html += '<li id="' + $this.id + '_item_more' + '" class="ui-autocomplete-item ui-autocomplete-moretext ui-corner-all" role="option">' + PrimeFaces.escapeHTML(moreTextEncoded) + '</li>';
                        }
                        html += '</ul>';

                        $this.panel.html(html);

                        $this.showSuggestions(query);
                    })
                    .always(function() {
                        $this.setQuerying(false);
                    });
            }
            else {
                PrimeFaces.ajax.Request.handle(options);
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
            this.jq.addClass('ui-state-loading')
                .append('<span class="ui-icon-loading pi pi-spin pi-spinner"></span>');
        }
        else if (!state && this.querying) {
            this.jq.removeClass('ui-state-loading')
                .find('.ui-icon-loading').remove();
        }
        this.querying = state;
    },

    /**
     * Shows the panel with the suggestions.
     * @private
     */
    show: function() {
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    $this.panel.css('z-index', PrimeFaces.nextZindex());
                    $this.alignPanel();
                },
                onEntered: function() {
                    $this.bindPanelEvents();
                    $this.input.attr('aria-expanded', true);
                }
            });
        }
    },

    /**
     * Hides the panel with the suggestions.
     * @private
     */
    hide: function() {
        if (this.panel.is(':visible') && this.transition) {
            var $this = this;
            if (this.cfg.dynamic && this.cfg.queryMode === 'server') {
                this.isDynamicLoaded = false;
            }

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    $this.panel.css('height', 'auto');
                    $this.input.attr('aria-expanded', false);
                    $this.input.removeAttr('aria-activedescendant', false);
                }
            });
        }

        if (this.cfg.itemtip) {
            this.itemtip.hide();
        }
    },

    /**
     * Invokes the appropriate behavior for when a suggestion item was selected.
     * @private
     * @param {string} itemValue Value of the selected item.
     */
    invokeItemSelectBehavior: function(itemValue) {
        if (this.hasBehavior('itemSelect')) {
            var ext = {
                params: [
                    { name: this.id + '_itemSelect', value: itemValue }
                ]
            };

            this.callBehavior('itemSelect', ext);
        }
    },

    /**
     * Invokes the appropriate behavior when a suggestion item was unselected.
     * @private
     * @param {string} itemValue Value of the unselected item.
     */
    invokeItemUnselectBehavior: function(itemValue) {
        if (this.hasBehavior('itemUnselect')) {
            var ext = {
                params: [
                    { name: this.id + '_itemUnselect', value: itemValue }
                ]
            };

            this.callBehavior('itemUnselect', ext);
        }
    },

    /**
     * Invokes the appropriate behavior for when more text was selected.
     * @private
     */
    invokeMoreTextBehavior: function() {
        if (this.hasBehavior('moreTextSelect')) {
            var ext = {
                params: [
                    { name: this.id + '_moreTextSelect', value: true }
                ]
            };

            this.callBehavior('moreTextSelect', ext);
        }
    },

    /**
     * Invokes the appropriate behavior for when empty message was selected.
     * @private
     */
    invokeEmptyMessageBehavior: function() {
        if (this.hasBehavior('emptyMessageSelect')) {
            var ext = {
                params: [
                    { name: this.id + '_emptyMessageSelect', value: true }
                ]
            };

            this.callBehavior('emptyMessageSelect', ext);
        }
    },

    /**
     * Add the given suggestion item.
     * @param {JQuery | string} item Suggestion item to add.
     */
    addItem: function(item) {
        var $this = this,
            itemValue = '',
            itemStyleClass = '',
            itemLabel = '';

        if ($this.input.hasClass('ui-state-disabled') || $this.input.attr("readonly")) {
            return;
        }

        if (typeof item === 'string' || item instanceof String) {
            itemValue = item;
            itemLabel = item;
        }
        else {
            itemValue = item.attr('data-item-value');
            itemLabel = item.attr('data-item-label');
            itemStyleClass = item.attr('data-item-class');
        }

        if (!itemValue) {
            return;
        }

        if ($this.cfg.multiple) {
            var found = false;
            if ($this.cfg.unique) {
                found = $this.multiItemContainer.children("li[data-token-value='" + $.escapeSelector(itemValue) + "']").length != 0;
            }

            if (!found) {
                if ($this.multiItemContainer.children('li.ui-autocomplete-token').length >= $this.cfg.selectLimit) {
                    return;
                }

                var itemLabelEscaped = PrimeFaces.escapeHTML(itemLabel);
                var itemValueEscaped = PrimeFaces.escapeHTML(itemValue);
                var itemDisplayMarkup = '<li data-token-value="' + itemValueEscaped;
                itemDisplayMarkup += '"class="ui-autocomplete-token ui-state-active ui-corner-all ui-helper-hidden';
                itemDisplayMarkup += (itemStyleClass === '' ? '' : ' ' + itemStyleClass) + '" '
                itemDisplayMarkup += 'role="option" aria-label="' + itemLabelEscaped + '" ';
                itemDisplayMarkup += 'aria-selected="true">';
                itemDisplayMarkup += '<span class="ui-autocomplete-token-icon ui-icon ui-icon-close" aria-hidden="true"></span>';
                itemDisplayMarkup += '<span class="ui-autocomplete-token-label">' + itemLabelEscaped + '</span></li>';

                $this.inputContainer.before(itemDisplayMarkup);
                $this.multiItemContainer.children('.ui-helper-hidden').fadeIn();
                $this.input.val('');
                $this.input.removeAttr('placeholder');

                $this.hinput.append('<option value="' + itemValueEscaped + '" selected="selected"></option>');
                if ($this.multiItemContainer.children('li.ui-autocomplete-token').length >= $this.cfg.selectLimit) {
                    $this.input.css('display', 'none').trigger("blur");
                    $this.disableDropdown();
                }

                $this.invokeItemSelectBehavior(itemValue);
            }
        } else {
            $this.input.val(item.attr('data-item-label'));

            this.currentText = $this.input.val();
            this.previousText = $this.input.val();

            if ($this.cfg.pojo) {
                $this.hinput.val(itemValue);
            }

            $this.invokeItemSelectBehavior(itemValue);
        }

        if ($this.cfg.onChange) {
            $this.cfg.onChange.call(this);
        }

        if (!$this.isTabPressed) {
            $this.input.trigger('focus');
        }
    },

    /**
     * Removes the given suggestion item.
     * @param {JQuery | string} item Suggestion item to remove.
     */
    removeItem: function(item) {
        var $this = this,
            itemValue = '';
        if ($this.input.hasClass('ui-state-disabled') || $this.input.attr("readonly")) {
            return;
        }

        if (typeof item === 'string' || item instanceof String) {
            itemValue = item;
        }
        else {
            itemValue = item.attr('data-token-value');
        }

        var foundItem = this.multiItemContainer.children("li.ui-autocomplete-token[data-token-value='" + $.escapeSelector(itemValue) + "']");
        if (!foundItem.length) {
            return;
        }
        var itemIndex = foundItem.index();
        if (!itemValue || itemIndex === -1) {
            return;
        }

        //remove from options
        this.hinput.children('option').eq(itemIndex).remove();

        //remove from items
        foundItem.fadeOut('fast', function() {
            var token = $(this);
            token.remove();
            $this.invokeItemUnselectBehavior(itemValue);
        });

        // if empty return placeholder
        if (this.placeholder && this.hinput.children('option').length === 0) {
            this.input.attr('placeholder', this.placeholder);
        }
    },

    /**
     * Removes all items if in multiple mode.
     */
    removeAllItems: function() {
        var $this = this;
        if (this.cfg.multiple && !this.input.val().length) {
            this.multiItemContainer.find('.ui-autocomplete-token').each(function(index) {
                $this.removeItem($(this));
            });
        }
    },

    /**
     * Sets up the event listener for the blur event to force a selection, when that feature is enabled.
     * @private
     */
    setupForceSelection: function() {
        this.currentItems = [this.input.val()];
        var $this = this;

        this.input.on('blur', function(e) {
            // #5731: do not fire clear event if selecting item
            var isNotPanel = e.relatedTarget == null || PrimeFaces.escapeClientId(e.relatedTarget.id) !== $this.panelId,
                value = $(this).val(),
                valid = $this.isValid(value, isNotPanel);

            if ($this.cfg.autoSelection && valid && $this.checkMatchedItem && $this.items && !$this.isTabPressed && !$this.itemSelectedWithEnter && isNotPanel) {
                var selectedItem = $this.items.filter('[data-item-label="' + $.escapeSelector(value) + '"]');
                if (selectedItem.length) {
                    selectedItem.trigger("click");
                }
            }

            $this.checkMatchedItem = false;
        });
    },

    /**
     * Disables the component.
     */
    disable: function() {
        this.jq.addClass("ui-state-disabled");
        PrimeFaces.utils.disableInputWidget(this.input);
        if (this.dropdown.length) {
            this.disableDropdown();
        }
    },

    /**
     * Enables the component.
     */
    enable: function() {
        this.jq.removeClass("ui-state-disabled");
        PrimeFaces.utils.enableInputWidget(this.input);
        if (this.dropdown.length) {
            this.enableDropdown();
        }
    },

    /**
     * Hides suggested items menu.
     */
    close: function() {
        this.hide();
    },

    /**
     * Deactivates search behavior.
     */
    deactivate: function() {
        this.active = false;
    },

    /**
     * Activates search behavior.
     */
    activate: function() {
        this.active = true;
    },

    /**
     * Aligns (positions) the overlay panel that shows the found suggestions.
     */
    alignPanel: function() {
        var panelWidth = null;

        if (this.cfg.multiple) {
            panelWidth = this.multiItemContainer.outerWidth();
        }
        else {
            if (this.panel.is(':visible')) {
                panelWidth = this.panel.children('.ui-autocomplete-items').outerWidth();
            }
            else {
                this.panel.css({ 'visibility': 'hidden', 'display': 'block' });
                panelWidth = this.panel.children('.ui-autocomplete-items').outerWidth();
                this.panel.css({ 'visibility': 'visible', 'display': 'none' });
            }

            var inputWidth = this.input.outerWidth();
            if (panelWidth < inputWidth) {
                panelWidth = inputWidth;
            }
        }

        if (this.cfg.scrollHeight) {
            var heightConstraint = this.panel.is(':hidden') ? this.panel.height() : this.panel.children().height();
            if (heightConstraint > this.cfg.scrollHeight)
                this.panel.height(this.cfg.scrollHeight);
            else
                this.panel.css('height', 'auto');
        }

        this.panel.css({
            'left': '',
            'top': '',
            'width': panelWidth + 'px',
            'z-index': PrimeFaces.nextZindex(),
            'transform-origin': 'center top'
        });

        if (this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px',
                'transform-origin': 'center top'
            });
        }
        else {
            this.panel.position({
                my: this.cfg.myPos
                , at: this.cfg.atPos
                , of: this.cfg.multiple ? this.jq : this.input
                , collision: 'flipfit'
                , using: function(pos, directions) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    },

    /**
     * Adds the given text to the ARIA status label element.
     * @private
     * @param {string} text Label text to display.
     */
    displayAriaStatus: function(text) {
        this.status.html('<div>' + PrimeFaces.escapeHTML(text) + '</div>');
    },

    /**
     * Adjusts the value of the aria attributes for the given selectable option.
     * @private
     * @param {Element} item An option for which to set the aria attributes.
     */
    changeAriaValue: function (item) {
        if (item) {
            this.input.attr('aria-activedescendant', item.id);
        }
    },

    /**
     * Adjusts the highlighting and aria attributes for the given selectable option.
     * @private
     * @param {Element} item An option for which to set the aria attributes.
     * @param {boolean} highlight Flag to indicate to highlight or not
     */
    highlightItem: function (item, highlight) {
        if (highlight) {
            item.addClass('ui-state-highlight');
        }
        else {
            item.removeClass('ui-state-highlight');
        }
        item.attr('aria-selected', highlight);
    },

    /**
     * Takes the available suggestion items and groups them.
     * @private
     */
    groupItems: function() {
        var $this = this;

        if (this.items.length) {
            this.itemContainer = this.panel.children('.ui-autocomplete-items');

            var firstItem = this.items.eq(0);
            if (!firstItem.hasClass('ui-autocomplete-moretext')) {
                this.currentGroup = firstItem.data('item-group');
                var currentGroupTooltip = firstItem.data('item-group-tooltip');

                firstItem.before(this.getGroupItem($this.currentGroup, $this.itemContainer, currentGroupTooltip));
            }

            this.items.filter(':not(.ui-autocomplete-moretext)').each(function(i) {
                var item = $this.items.eq(i),
                    itemGroup = item.data('item-group'),
                    itemGroupTooltip = item.data('item-group-tooltip');

                if ($this.currentGroup !== itemGroup) {
                    $this.currentGroup = itemGroup;
                    item.before($this.getGroupItem(itemGroup, $this.itemContainer, itemGroupTooltip));
                }
            });
        }
    },

    /**
     * Creates the grouped suggestion item for the given parameters.
     * @private
     * @param {string} group A group where to look for the item.
     * @param {JQuery} container Container element of the group.
     * @param {string} tooltip Optional tooltip for the group item.
     * @return {JQuery} The newly created group item.
     */
    getGroupItem: function(group, container, tooltip) {
        var element = null;

        if (container.is('.ui-autocomplete-table')) {
            if (!this.colspan) {
                this.colspan = this.items.eq(0).children('td').length;
            }

            element = $('<tr class="ui-autocomplete-group ui-widget-header"><td colspan="' + this.colspan + '">' + group + '</td></tr>');
        }
        else {
            element = $('<li class="ui-autocomplete-group ui-autocomplete-list-item ui-widget-header">' + group + '</li>');
        }

        if (element) {
            element.attr('title', tooltip);
        }

        return element;
    },

    /**
     * Clears the set-timeout timer for the autocomplete search.
     * @private
     */
    deleteTimeout: function() {
        clearTimeout(this.timeout);
        this.timeout = null;
    },

    /**
     * Triggers the behavior for when the input was cleared.
     * @private
     */
    fireClearEvent: function() {
        this.callBehavior('clear');
        this.previousText = this.currentText;
        this.currentText = '';
    },

    /**
     * Checks whether the given value is part of the available suggestion items.
     * @param {string} value A value to check.
     * @param {boolean} [shouldFireClearEvent] `true` if clear event should be fired.
     * @return {boolean | undefined} Whether the given value matches a value in the list of available suggestion items;
     * or `undefined` if {@link AutoCompleteCfg.forceSelection} is set to `false`.
     */
    isValid: function(value, shouldFireClearEvent) {
        if (!this.cfg.forceSelection) {
            return;
        }

        var valid = false;

        for (var i = 0; i < this.currentItems.length; i++) {
            var strippedItem = this.currentItems[i];
            if (strippedItem) {
                strippedItem = strippedItem.replace(/\r?\n/g, '');
            }

            if (strippedItem === value) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            this.input.val('');
            if (!this.cfg.multiple) {
                this.hinput.val('');
            }
            shouldFireClearEvent = shouldFireClearEvent && (!this.cfg.multiple && this.currentText);
            if (shouldFireClearEvent) {
                this.fireClearEvent();
            }
        }

        return valid;
    },

    /**
     * Fetches the suggestion items for the current query from the server.
     * @private
     */
    fetchItems: function() {
        var $this = this;

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.getParentFormId(),
            global: false,
            params: [{ name: this.id + '_clientCache', value: true }],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        $this.setCache($(content));
                    }
                });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    /**
     * Adds the suggestions items in the given wrapper to the local cache of suggestion items.
     * @private
     * @param {JQuery} wrapper Wrapper element with the suggestions fetched from the server.
     */
    setCache: function(wrapper) {
        var $this = this,
            items = wrapper.find('.ui-autocomplete-item'),
            prevKey = null;

        if (!this.wrapperStartTag || !this.wrapperEndTag) {
            this.findWrapperTag(wrapper);
        }

        for (var i = 0; i < items.length; i++) {
            var item = items.eq(i),
                key = item.data('item-key');

            this.cache[key] = (this.cache[key] || this.wrapperStartTag) + item.get(0).outerHTML;

            if ((prevKey !== null && prevKey !== key) || (i === items.length - 1)) {
                this.cache[prevKey] += $this.wrapperEndTag;
            }

            prevKey = key;
        }
    },

    /**
     * Finds and sets the wrapper HTML snippets on this instance.
     * @private
     * @param {JQuery} wrapper Wrapper element with the suggestions fetched from the server.
     */
    findWrapperTag: function(wrapper) {
        if (wrapper.is('ul')) {
            this.wrapperStartTag = '<ul class="ui-autocomplete-items ui-autocomplete-list ui-widget-content ui-widget ui-corner-all ui-helper-reset" role="listbox">';
            this.wrapperEndTag = '</ul>';
        }
        else {
            var header = wrapper.find('> table > thead');
            this.wrapperStartTag = '<table class="ui-autocomplete-items ui-autocomplete-table ui-widget-content ui-widget ui-corner-all ui-helper-reset" role="listbox">' +
                (header.length ? header.eq(0).outherHTML : '') +
                '<tbody>';
            this.wrapperEndTag = '</tbody></table>';
        }
    },

    /**
     * Clears the input field.
     */
    clear: function() {
        this.input.val('');
        if (this.cfg.multiple) {
            this.removeAllItems();
        }
        else if (this.cfg.pojo) {
            this.hinput.val('');
        }
    }

});
