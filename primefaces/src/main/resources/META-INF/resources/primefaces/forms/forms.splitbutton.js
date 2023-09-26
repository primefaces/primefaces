/**
 * __PrimeFaces SplitButton Widget__
 *
 * SplitButton displays a command by default and additional ones in an overlay.
 *
 * @typedef PrimeFaces.widget.SplitButton.FilterFunction A filter function that takes a term and returns whether the
 * search term matches the value.
 * @param {string} PrimeFaces.widget.SplitButton.FilterFunction.value A value to check.
 * @param {string} PrimeFaces.widget.SplitButton.FilterFunction.query A search term against which the value is checked.
 * @return {string} PrimeFaces.widget.SplitButton.FilterFunction `true` if the search term matches the value, or `false`
 * otherwise.
 *
 * @typedef {"startsWith" |  "contains" |  "endsWith" | "custom"} PrimeFaces.widget.SplitButton.FilterMatchMode
 * Available modes for filtering the options of the available buttons actions of a split button. When `custom` is set, a
 * `filterFunction` must be specified.

 * @prop {JQuery} button The DOM element for the main button.
 * @prop {PrimeFaces.widget.SplitButton.FilterFunction} filterMatcher The current filter function.
 * @prop {Record<string, PrimeFaces.widget.SplitButton.FilterFunction>} filterMatchers A map of all flter functions. The
 * key is the name of the filter function.
 * @prop {JQuery} filterInput The DOM element for the filter input field
 * @prop {PrimeFaces.UnbindCallback} [hideOverlayHandler] Unbind callback for the hide overlay handler.
 * @prop {JQuery} menu The DOM element for the additional buttons actions.
 * @prop {JQuery} menuitemContainer The DOM element for the container of the additional buttons actions.
 * @prop {JQuery} menuitems The DOM elements for the individual additional button actions.
 * @prop {JQuery} menuButton The DOM element for the button that triggers the overlay panel with the additional buttons
 * actions.
 * @prop {string} menuId The prefix shared ny the different IDs of the components of this widget.
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * @prop {PrimeFaces.UnbindCallback} [resizeHandler] Unbind callback for the resize handler.
 * @prop {PrimeFaces.UnbindCallback} [scrollHandler] Unbind callback for the scroll handler.
 * @prop {number} [ajaxCount] Number of concurrent active Ajax requests.
 *
 * @interface {PrimeFaces.widget.SplitButtonCfg} cfg The configuration for the {@link  SplitButton| SplitButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {string} cfg.appendTo The search expression for the element to which the overlay panel should be appended.
 * @prop {PrimeFaces.widget.SplitButton.FilterMatchMode} cfg.filterMatchMode Match mode for filtering, how the search
 * term is matched against the items.
 * @prop {boolean} cfg.disabled Whether this input is currently disabled.
 * @prop {boolean} cfg.filter Whether client side filtering feature is enabled.
 * @prop {boolean} cfg.filterNormalize Defines if filtering would be done using normalized values.
 * @prop {boolean} cfg.filterInputAutoFocus Defines if the filter should receive focus on overlay popup.
 * @prop {PrimeFaces.widget.SplitButton.FilterFunction} cfg.filterFunction Custom JavaScript function for filtering the
 * available split button actions.
 */
PrimeFaces.widget.SplitButton = PrimeFaces.widget.BaseWidget.extend({

     /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.button = $(this.jqId + '_button');
        this.menuButton = $(this.jqId + '_menuButton');
        this.menuId = this.jqId + '_menu';
        this.menu = $(this.menuId);
        this.menuitemContainer = this.menu.find('.ui-menu-list');
        this.menuitems = this.menuitemContainer.children('.ui-menuitem:not(.ui-state-disabled)');
        this.cfg.disabled = this.button.is(':disabled');
        this.cfg.filterInputAutoFocus = (this.cfg.filterInputAutoFocus === undefined) ? true : this.cfg.filterInputAutoFocus;

        this.bindEvents();

        PrimeFaces.utils.registerDynamicOverlay(this, this.menu, this.id + '_menu');
        this.transition = PrimeFaces.utils.registerCSSTransition(this.menu, 'ui-connected-overlay');

        //pfs metadata
        this.button.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.menuButton.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.menuButton.off('click.splitbutton');
        this.menuitems.off('mouseover.splitbutton mouseout.splitbutton click.splitbutton');
        this.menuButton.on('keydown.splitbutton keyup.splitbutton');
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);

        this._super(cfg);
    },

    /**
     * Disables this button so that the user cannot press the button anymore.
     */
    disable: function() {
        this.cfg.disabled = true;
        this.hide();
        PrimeFaces.utils.disableButton(this.button);
        PrimeFaces.utils.disableButton(this.menuButton);
    },

    /**
     * Enables this button so that the user can press the button.
     */
    enable: function() {
        this.cfg.disabled = false;
        PrimeFaces.utils.enableButton(this.button);
        PrimeFaces.utils.enableButton(this.menuButton);
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        PrimeFaces.skinButton(this.button).skinButton(this.menuButton);

        // mark button and descendants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        // toggle menu
        this.menuButton.on('click.splitbutton', function() {
            if(!$this.cfg.disabled && $this.menu.is(':hidden'))
                $this.show();
            else
                $this.hide();
        });

        //menuitem visuals
        this.menuitems.on('mouseover.splitbutton', function(e) {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitem.addClass('ui-state-hover');
            }
        }).on('mouseout.splitbutton', function(e) {
            $(this).removeClass('ui-state-hover');
        }).on('click.splitbutton', function() {
            $this.hide();
        });

        //keyboard support
        this.menuButton.on('keydown.splitbutton', function(e) {
            if ($this.cfg.disabled) {
                return;
            }
            switch(e.key) {
                case 'ArrowUp':
                    $this.highlightPrev(e);
                break;

                case 'ArrowDown':
                    $this.highlightNext(e);
                break;

                case 'Enter':
                case ' ':
                    $this.handleEnterKey(e);
                break;


                case 'Escape':
                case 'Tab':
                    $this.handleEscapeKey();
                break;
            }
        }).on('keyup.splitbutton', function(e) {
            if (e.key === ' ') {
                e.preventDefault(); // Keep menu open in Firefox #7614
            }
        });

        $this.ajaxCount = 0;
        $(document).on('pfAjaxSend.' + this.id, function(e, xhr, settings) {
            if ($this.isXhrSource(settings)) {
                $this.ajaxCount++;
                if ($this.ajaxCount > 1) {
                    return;
                }
                $this.button.addClass('ui-state-loading');
                if ($this.cfg.disableOnAjax !== false) {
                    $this.disable();
                }
                var loadIcon = $('<span class="ui-icon-loading ui-icon ui-c pi pi-spin pi-spinner"></span>');
                var uiIcon = $this.button.find('.ui-icon');
                if (uiIcon.length) {
                    var prefix = 'ui-button-icon-';
                    loadIcon.addClass(prefix + uiIcon.attr('class').includes(prefix + 'left') ? 'left' : 'right');
                }
                $this.button.prepend(loadIcon);
            }
        }).on('pfAjaxComplete.' + this.id, function(e, xhr, settings, args) {
            if ($this.isXhrSource(settings)) {
                $this.ajaxCount--;
                if ($this.ajaxCount > 0 || args.redirect) {
                    return;
                }
                $this.button.removeClass('ui-state-loading');
                if ($this.cfg.disableOnAjax !== false && !$this.cfg.disabledAttr) {
                    $this.enable();
                }
                $this.button.find('.ui-icon-loading').remove();
            }
        });        

        if(this.cfg.filter) {
            this.setupFilterMatcher();
            this.filterInput = this.menu.find('> div.ui-splitbuttonmenu-filter-container > input.ui-splitbuttonmenu-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.bindFilterEvents();
        }
    },

    /**
     * Checks whether the ID of the button, or one if its menu items equals the source ID from the provided settings.
     *
     * @param {JQuery.AjaxSettings} settings containing source ID.
     * @returns {boolean} `true` if the ID of the button, or one if its menu items equals the source ID from the
     * provided settings.
     * @private
     */
    isXhrSource: function(settings) {
        var sourceId = PrimeFaces.ajax.Utils.getSourceId(settings);
        if (sourceId === null) {
            return false;
        }
        if (this.id === sourceId) {
            return true;
        }
        return this.menuitems.find('[id="' + sourceId + '"]').length;
    },

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents: function() {
        var $this = this;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.menu, null,
            function(e, eventTarget) {
                if(!($this.menu.is(eventTarget) || $this.menu.has(eventTarget).length > 0)) {
                    $this.button.removeClass('ui-state-focus ui-state-hover');
                    $this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', this.menu, function() {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                $this.hide();
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, function() {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                $this.hide();
            }
        });
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
     * Sets up the event listeners for filtering the available buttons actions via a search field.
     * @private
     */
    bindFilterEvents: function() {
        var $this = this;

        this.filterInput.on('keyup.ui-splitbutton', function(e) {
            if (PrimeFaces.utils.ignoreFilterKey(e)) {
                return;
            }
            var metaKey = e.metaKey||e.ctrlKey;
            if(!metaKey) {
                $this.filter($(this).val());
            }
        })
        .on('keydown.ui-splitbutton',function(e) {
            switch(e.key) {
                case 'ArrowUp':
                    $this.highlightPrev(e);
                break;

                case 'ArrowDown':
                    $this.highlightNext(e);
                break;

                case 'Enter':
                    $this.handleEnterKey(e);
                break;

                case ' ':
                    var target = $(e.target);

                    if(target.is('input') && target.hasClass('ui-splitbuttonmenu-filter')) {
                        return;
                    }

                    $this.handleEnterKey(e);
                break;

                case 'Escape':
                case 'Tab':
                    $this.handleEscapeKey();
                break;

                default:
                break;
            }
        }).on('paste.ui-splitbutton', function() {
            PrimeFaces.queueTask(function(){
                $this.filter($this.filterInput.val());
            });
	});
    },

    /**
     * Highlights the next button action, usually when the user navigates via the keyboard arrows.
     * @private
     * @param {JQuery.TriggeredEvent} event Keyboard arrow event that caused the next item to be highlighted.
     */
    highlightNext: function(event) {
        var highlightedItem = this.menuitems.filter('.ui-state-hover'),
        nextItems = highlightedItem.length ? highlightedItem.nextAll(':not(.ui-separator, .ui-widget-header):visible') : this.menuitems.filter(':visible').eq(0);

        if(nextItems.length) {
            highlightedItem.removeClass('ui-state-hover');
            nextItems.eq(0).addClass('ui-state-hover');
        }

        event.preventDefault();
    },

    /**
     * Highlights the previous button action, usually when the user navigates via the keyboard arrows.
     * @private
     * @param {JQuery.TriggeredEvent} event Keyboard arrow event that caused the previous item to be highlighted.
     */
    highlightPrev: function(event) {
        var highlightedItem = this.menuitems.filter('.ui-state-hover'),
        prevItems = highlightedItem.length ? highlightedItem.prevAll(':not(.ui-separator, .ui-widget-header):visible') : null;

        if(prevItems && prevItems.length) {
            highlightedItem.removeClass('ui-state-hover');
            prevItems.eq(0).addClass('ui-state-hover');
        }

        event.preventDefault();
    },

    /**
     * Callback that is invoked when the enter key is pressed. When overlay panel with the additional buttons actions is
     * shown, activates the selected buttons action. Otherwise, opens the overlay panel.
     * @private
     * @param {JQuery.TriggeredEvent} event Keyboard event of the enter press.
     */
    handleEnterKey: function(event) {
        if(this.menu.is(':visible')) {
            var link = this.menuitems.filter('.ui-state-hover').children('a');
            link.trigger('click');

            var href = link.attr('href');
            if(href && href !== '#') {
                window.location.href = href;
            }
        }
        else {
            this.show();
        }

        event.preventDefault();
    },

    /**
     * Callback that is invoked when the escape key is pressed while the overlay panel with the additional buttons
     * actions is shown. Hides that overlay panel.
     * @private
     */
    handleEscapeKey: function() {
        this.hide();
    },

    /**
     * Creates the filter functions for filtering the button actions.
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
     * A filter function that takes a value and a search and returns true if the value starts with the search term.
     * @param {string} value Value to be filtered
     * @param {string} filter Filter or search term to apply.
     * @return {boolean} `true` if the given value starts with the search term, or `false` otherwise.
     */
    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    /**
     * A filter function that takes a value and a search and returns true if the value contains the search term.
     * @param {string} value Value to be filtered
     * @param {string} filter Filter or search term to apply.
     * @return {boolean} `true` if the given value contains the search term, or `false` otherwise.
     */
    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    /**
     * A filter function that takes a value and a search and returns true if the value ends with the search term.
     * @param {string} value Value to be filtered
     * @param {string} filter Filter or search term to apply.
     * @return {boolean} `true` if the given value ends with the search term, or `false` otherwise.
     */
    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    /**
     * Filters the overlay panel with the additional buttons actions, leaving only the buttons that match the given
     * search term.
     * @param {string} value Search term for filtering.
     */
    filter: function(value) {
        var normalize = this.cfg.filterNormalize,
            filterValue = PrimeFaces.toSearchable(PrimeFaces.trim(value), true, normalize);

        if(filterValue === '') {
            this.menuitems.filter(':hidden').show();
            this.menuitemContainer.children('.ui-widget-header').show();
            this.menuitemContainer.children('.ui-separator').show();
        }
        else {
            for(var i = 0; i < this.menuitems.length; i++) {
                var menuitem = this.menuitems.eq(i),
                itemLabel = PrimeFaces.toSearchable(menuitem.find('.ui-menuitem-text').text(), true, normalize);

                /* for keyboard support */
                menuitem.removeClass('ui-state-hover');

                if(this.filterMatcher(itemLabel, filterValue))
                    menuitem.show();
                else
                    menuitem.hide();

            }

            //groups
            var groupHeaders = this.menuitemContainer.children('.ui-widget-header');
            for(var g = 0; g < groupHeaders.length; g++) {
                var group = groupHeaders.eq(g);

                if(g === (groupHeaders.length - 1)) {
                    if(group.nextAll('.ui-submenu-child').filter(':visible').length === 0)
                        group.hide();
                    else
                        group.show();
                }
                else {
                    if(group.nextUntil('.ui-widget-header').filter(':visible').length === 0)
                        group.hide();
                    else
                        group.show();
                }
            }

            var separators = this.menuitemContainer.children('.ui-separator');
            for(var s = 0; s < separators.length; s++) {
                var separator = separators.eq(s);
                if(separator.nextAll().filter(':visible').length === 0 || separator.prevAll().filter(':visible').length === 0) {
                    separator.hide();
                }
                else {
                    separator.show();
                }
            }
        }

        this.alignPanel();
    },

    /**
     * Shows the overlay panel with the additional buttons actions.
     * @private
     */
    show: function() {
        if(this.cfg.disabled) {
           return;
        }
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    $this.menu.css('z-index', PrimeFaces.nextZindex());
                    $this.alignPanel();
                },
                onEntered: function() {
                    $this.bindPanelEvents();

                    $this.jq.attr('aria-expanded', true);

                    if ($this.cfg.filter && $this.cfg.filterInputAutoFocus) {
                        $this.filterInput.trigger('focus');
                    }
                    else {
                        $this.menuButton.trigger('focus');
                    }
                }
            });
        }
    },

    /**
     * Hides the overlay panel with the additional buttons actions.
     * @private
     */
    hide: function() {
        if (this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    $this.jq.attr('aria-expanded', false);
                    $this.menuitems.filter('.ui-state-hover').removeClass('ui-state-hover');
                    $this.menuButton.removeClass('ui-state-focus');
                }
            });
        }
    },

    /**
     * Align the overlay panel with the additional buttons actions.
     */
    alignPanel: function() {
        this.menu.css({ left:'', top:'', 'transform-origin': 'center top' });

        if(this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px'
            });
        }
        else {
            this.menu.position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
                ,using: function(pos, directions) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    }

});
