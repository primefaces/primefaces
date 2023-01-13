/**
 * __PrimeFaces SelectCheckboxMenu Widget__
 * 
 * SelectCheckboxMenu is a multi select component that displays options in an overlay.
 * 
 * @typedef {"startsWith" |  "contains" |  "endsWith" | "custom"} PrimeFaces.widget.SelectCheckboxMenu.FilterMatchMode
 * Available modes for filtering the options of a select list box. When `custom` is set, a `filterFunction` must be
 * specified.
 * 
 * @typedef PrimeFaces.widget.SelectCheckboxMenu.FilterFunction A function for filtering the options of a select list
 * box.
 * @param {string} PrimeFaces.widget.SelectCheckboxMenu.FilterFunction.itemLabel The label of the currently selected
 * text.
 * @param {string} PrimeFaces.widget.SelectCheckboxMenu.FilterFunction.filterValue The value to search for.
 * @return {boolean} PrimeFaces.widget.SelectCheckboxMenu.FilterFunction `true` if the item label matches the filter
 * value, or `false` otherwise.
 * 
 * @typedef PrimeFaces.widget.SelectCheckboxMenu.OnChangeCallback Callback that is invoked when a checkbox option was
 * checked or unchecked. See also {@link SelectCheckboxMenuCfg.onChange}.
 * @this {PrimeFaces.widget.SelectCheckboxMenu} PrimeFaces.widget.SelectCheckboxMenu.OnChangeCallback 
 * 
 * @typedef PrimeFaces.widget.SelectCheckboxMenu.OnHideCallback Callback that is invoked when the overlay panel is
 * brought up. See also {@link SelectCheckboxMenuCfg.onHide}.
 * @this {PrimeFaces.widget.SelectCheckboxMenu} PrimeFaces.widget.SelectCheckboxMenu.OnHideCallback 
 * 
 * @typedef PrimeFaces.widget.SelectCheckboxMenu.OnShowCallback Callback that is invoked when the overlay panel is
 * hidden. See also {@link SelectCheckboxMenuCfg.onShow}.
 * @this {PrimeFaces.widget.SelectCheckboxMenu} PrimeFaces.widget.SelectCheckboxMenu.OnShowCallback 
 * 
 * @prop {JQuery} checkboxes The DOM element for the checkboxes that can be selected.
 * @prop {JQuery} [closer] The DOM element for the button that closes the overlay panel with the select
 * options (when the panel is shown).
 * @prop {JQuery} defaultLabel The DOM element for the default label.
 * @prop {boolean} disabled Whether this widget is currently disabled.
 * @prop {JQuery} [filterInput] DOM element of the input element used for entering a filter text, if filtering is
 * enabled.
 * @prop {JQuery} [filterInputWrapper] DOM element of the wrapper that holds the input element used for entering a
 * filter text, if filtering is enabled.
 * @prop {JQuery} groupHeaders The DOM elements for the headers of each option group.
 * @prop {PrimeFaces.widget.SelectCheckboxMenu.FilterFunction} filterMatcher The filter that was selected and is
 * currently used.
 * @prop {Record<PrimeFaces.widget.SelectCheckboxMenu.FilterMatchMode, PrimeFaces.widget.SelectCheckboxMenu.FilterFunction>} filterMatchers
 * Map between the available filter types and the filter implementation.
 * @prop {JQuery} [header] DOM element of the header of the overlay panel with the available select items,
 * when the overlay panel is shown.
 * @prop {PrimeFaces.UnbindCallback} [hideOverlayHandler] Unbind callback for the hide overlay handler.
 * @prop {JQuery} inputs The DOM elements for the hidden inputs for each checkbox option.
 * @prop {boolean} isDynamicLoaded When loading the panel with the available options lazily: if they have been loaded
 * already.
 * @prop {JQuery} items The DOM elements for the the available checkbox options.
 * @prop {JQuery} itemContainer The DOM element for the container with the available checkbox options.
 * @prop {JQuery} itemContainerWrapper The DOM element for the wrapper with the container with the available checkbox
 * options.
 * @prop {JQuery} keyboardTarget The DOM element for the hidden input element that that can be selected via pressing
 * tab. 
 * @prop {JQuery} label The DOM element for the label indicating the currently selected option.
 * @prop {JQuery} labelContainer The DOM element for the container with the label indicating the currently selected
 * option.
 * @prop {string} labelId ID of the label element that indicates the currently selected option.
 * @prop {JQuery} menuIcon The DOM element for the icon for bringing up the overlay panel.
 * @prop {JQuery} multiItemContainer The DOM element for the container with the tags representing the selected options.
 * @prop {JQuery} panel The DOM element for the overlay panel with the available checkbox options.
 * @prop {JQuery} panelId ID of the DOM element for the overlay panel with the available checkbox options.
 * @prop {PrimeFaces.UnbindCallback} [resizeHandler] Unbind callback for the resize handler.
 * @prop {PrimeFaces.UnbindCallback} [scrollHandler] Unbind callback for the scroll handler.
 * @prop {string} tabindex Tab index of this widget.
 * @prop {JQuery} [toggler] The wrapping DOM element of the toggler for selecting or unselecting all options
 * in the overlay panel with all selected items (when the overlay panel is shown).
 * @prop {JQuery} [togglerBox] The DOM element with the toggler checkbox for selecting or unselecting all
 * options in the overlay panel with all selected items (when the overlay panel is shown).
 * @prop {PrimeFaces.CssTransitionHandler | null} [transition] Handler for CSS transitions used by this widget.
 * @prop {JQuery} triggers The DOM elements for the buttons that can trigger (hide or show) the overlay panel with the
 * available checkbox options.
 * @prop {boolean} widthAligned Whether the width of the overlay panel was aligned already.
 * 
 * @interface {PrimeFaces.widget.SelectCheckboxMenuCfg} cfg The configuration for the {@link  SelectCheckboxMenu| SelectCheckboxMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.appendTo The search expression for the element to which the overlay panel should be appended.
 * @prop {boolean} cfg.caseSensitive Defines if filtering would be case sensitive.
 * @prop {boolean} cfg.dynamic Defines if dynamic loading is enabled for the element's panel. If the value is `true`,
 * the overlay is not rendered on page load to improve performance.
 * @prop {string} cfg.emptyLabel Label to be shown in updateLabel mode when no item is selected. If not set the label is
 * shown.
 * @prop {boolean} cfg.filter `true` if the options can be filtered, or `false` otherwise.
 * @prop {PrimeFaces.widget.SelectCheckboxMenu.FilterFunction} cfg.filterFunction A custom filter function that is used
 * when `filterMatchMode` is set to `custom`.
 * @prop {PrimeFaces.widget.SelectCheckboxMenu.FilterMatchMode} cfg.filterMatchMode Mode of the filter. When set to
 * `custom`, a `filterFunction` must be specified.
 * @prop {string} cfg.filterPlaceholder Placeholder text to show when filter input is empty.
 * @prop {number} cfg.initialHeight Initial height of the item container.
 * @prop {string} cfg.labelSeparator Separator for joining item labels if updateLabel is set to true. Default is `,`.
 * @prop {boolean} cfg.multiple Whether to show selected items as multiple labels.
 * @prop {PrimeFaces.widget.SelectCheckboxMenu.OnChangeCallback} cfg.onChange Callback that is invoked when a checkbox
 * option was checked or unchecked.
 * @prop {PrimeFaces.widget.SelectCheckboxMenu.OnHideCallback} cfg.onHide Callback that is invoked when the overlay
 * panel is brought up.
 * @prop {PrimeFaces.widget.SelectCheckboxMenu.OnShowCallback} cfg.onShow Callback that is invoked when the overlay
 * panel is hidden.
 * @prop {string} cfg.panelStyle Inline style of the overlay panel.
 * @prop {string} cfg.panelStyleClass Style class of the overlay panel
 * @prop {number} cfg.scrollHeight Height of the overlay panel.
 * @prop {boolean} cfg.showHeader When enabled, the header of overlay panel is displayed.
 * @prop {boolean} cfg.updateLabel When enabled, the selected items are displayed on the label.
 */
PrimeFaces.widget.SelectCheckboxMenu = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.labelContainer = this.jq.find('.ui-selectcheckboxmenu-label-container');
        this.label = this.jq.find('.ui-selectcheckboxmenu-label');
        this.menuIcon = this.jq.children('.ui-selectcheckboxmenu-trigger');
        this.triggers = this.jq.find('.ui-selectcheckboxmenu-trigger, .ui-selectcheckboxmenu-label');
        this.disabled = this.jq.hasClass('ui-state-disabled');
        //Only include checkboxes with an ID that starts like ID of the menu!
        this.inputs = this.jq.find(':checkbox').filter('[id^="' + this.id.replace(/:/g,"\\:") + '"]');
        this.panelId = this.id + '_panel';
        this.labelId = this.id + '_label';
        this.panel = $(this.jqId + '_panel');
        this.itemContainerWrapper = this.panel.children('.ui-selectcheckboxmenu-items-wrapper');
        this.keyboardTarget = $(this.jqId + '_focus');
        this.tabindex = this.keyboardTarget.attr('tabindex');
        this.cfg.showHeader = (this.cfg.showHeader === undefined) ? true : this.cfg.showHeader;
        this.cfg.dynamic = this.cfg.dynamic === true ? true : false;
        this.isDynamicLoaded = false;
        this.cfg.labelSeparator = (this.cfg.labelSeparator === undefined) ? ', ' : this.cfg.labelSeparator;

        if (!this.disabled) {
            if (this.cfg.multiple) {
                this.triggers = this.jq.find('.ui-selectcheckboxmenu-trigger, .ui-selectcheckboxmenu-multiple-container');
            }

            if (!this.cfg.dynamic) {
                this._renderPanel();
            }

            this.bindEvents();
            this.bindKeyEvents();

            //mark trigger and descandants of trigger as a trigger for a primefaces overlay
            this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

            if (!this.cfg.multiple) {
                this.label.attr('id', this.labelId);
                this.keyboardTarget.attr('aria-labelledby', this.labelId);
            }
            this.keyboardTarget.attr('aria-expanded', false);
        }

        this.renderLabel();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
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
     * Creates the overlay panel with the checkboxes for the selectable option, and also sets up the event listeners
     * for the panel.
     * @private
     */
    _renderPanel: function() {
        this.renderPanel();

        if (this.tabindex) {
            this.panel.find('a, input').attr('tabindex', this.tabindex);
        }

        this.checkboxes = this.itemContainer.find('.ui-chkbox-box:not(.ui-state-disabled)');

        this.bindPanelContentEvents();
        this.bindPanelKeyEvents();

        this.isDynamicLoaded = true;
    },

    /**
     * Creates the overlay panel with the checkboxes for the selectable option.
     * @private
     */
    renderPanel: function() {
        //init panel content rendered by SelectCheckboxMenuRenderer
        this.header = this.panel.children('.ui-selectcheckboxmenu-header');
        this.toggler = this.header.children('.ui-chkbox');
        this.togglerBox = this.toggler.children('.ui-chkbox-box');
        this.filterInputWrapper = this.header.children('.ui-selectcheckboxmenu-filter-container');
        this.filterInput = this.filterInputWrapper.children('.ui-inputtext');
        this.closer = this.header.children('.ui-selectcheckboxmenu-close');

        if (this.cfg.renderPanelContentOnClient && this.itemContainerWrapper.children().length === 0) {
            this.renderItems();
        }
        else {
            //init items rendered by SelectCheckboxMenuRenderer
            this.itemContainer = this.itemContainerWrapper.children('.ui-selectcheckboxmenu-items');
            this.items = this.itemContainer.find('.ui-selectcheckboxmenu-item');
            this.groupHeaders = this.itemContainer.find('.ui-selectcheckboxmenu-item-group');
        }

        if (this.cfg.scrollHeight) {
            this.itemContainerWrapper.height(this.cfg.scrollHeight);
        }
        else if (this.inputs.length > 10) {
            this.itemContainerWrapper.height(200);
        }

        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this, this.jq, this.panel);

        PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
        this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');

        this.keyboardTarget.attr('aria-controls', this.panelId);
    },

    /**
     * Create the label to display values
     * @private
     */
    renderLabel: function() {
        if (!this.cfg.updateLabel) {
            return;
        }
        if (this.cfg.multiple) {
            this.multiItemContainer = this.multiItemContainer || this.jq.children('.ui-selectcheckboxmenu-multiple-container');
            var items = this.multiItemContainer.children();
            if (!items.length) {
                this.multiItemContainer.empty().append('<li class="ui-selectcheckboxmenu-emptylabel">' + (this.multiItemContainer.data('label') || '&nbsp;') + '</li>');
            }
        }
        else {
            // single select
            this.defaultLabel = this.label.text();
            this.label.css({
                'text-overflow': 'ellipsis',
                overflow: 'hidden'
            });

            this.updateLabel();
        }
    },

    /**
     * Creates the individual checkboxes for each selectable option in the overlay panel.
     * @private
     */
    renderItems: function() {
        var $this = this;

        this.itemContainer = $('<ul class="ui-selectcheckboxmenu-items ui-selectcheckboxmenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset"></ul>')
            .appendTo(this.itemContainerWrapper);

        //check if inputs must be grouped
        var grouped = this.inputs.filter('[data-group-label]');

        var currentGroupName = null;
        for (var i = 0; i < this.inputs.length; i++) {
            var input = this.inputs.eq(i),
                label = input.next(),
                disabled = input.is(':disabled'),
                checked = input.is(':checked'),
                title = input.attr('title'),
                boxClass = 'ui-chkbox-box ui-widget ui-corner-all ui-state-default',
                itemClass = 'ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all',
                escaped = input.data('escaped');

            if (grouped.length && currentGroupName !== input.attr('data-group-label')) {
                currentGroupName = input.attr('data-group-label');
                var itemGroup = $('<li class="ui-selectcheckboxmenu-item-group ui-selectcheckboxmenu-group-list-item ui-corner-all"></li>');
                itemGroup.text(currentGroupName);
                $this.itemContainer.append(itemGroup);
            }

            if (disabled) {
                boxClass += " ui-state-disabled";
            }

            if (checked) {
                boxClass += " ui-state-active";
            }

            var iconClass = checked ? 'ui-chkbox-icon ui-icon ui-icon-check' : 'ui-chkbox-icon ui-icon ui-icon-blank',
                itemClass = checked ? itemClass + ' ui-selectcheckboxmenu-checked' : itemClass + ' ui-selectcheckboxmenu-unchecked';

            var item = $('<li class="' + itemClass + '"></li>');
            item.append('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox"></input></div>' +
                '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div></div>');

            var uuid = PrimeFaces.uuid();
            var itemLabel = $('<label for=' + uuid + '></label>'),
                labelHtml = label.html().trim(),
                labelLength = labelHtml.length;
            if (labelLength > 0 && labelHtml !== '&nbsp;')
                if (escaped)
                    itemLabel.text(label.text());
                else
                    itemLabel.html(label.html());
            else
                itemLabel.text(input.val());

            itemLabel.appendTo(item);

            if (title) {
                item.attr('title', title);
            }

            if ($this.cfg.multiple) {
                item.attr('data-item-value', input.val());
            }

            item.find('> .ui-chkbox > .ui-helper-hidden-accessible > input').prop('checked', checked).attr('id', uuid);
            $this.itemContainer.attr('role', 'group');

            $this.itemContainer.append(item);
        }

        this.items = this.itemContainer.children('li.ui-selectcheckboxmenu-item');
        this.groupHeaders = this.itemContainer.children('li.ui-selectcheckboxmenu-item-group');
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        //Events to show/hide the panel
        this.triggers.on('mouseenter.selectCheckboxMenu', function() {
            if (!$this.disabled) {
                $this.jq.addClass('ui-state-hover');
                $this.triggers.addClass('ui-state-hover');
            }
        }).on('mouseleave.selectCheckboxMenu', function() {
            if (!$this.disabled) {
                $this.jq.removeClass('ui-state-hover');
                $this.triggers.removeClass('ui-state-hover');
            }
        }).on('mousedown.selectCheckboxMenu', function(e) {
            if (!$this.disabled) {
                if ($this.cfg.multiple && $(e.target).is('.ui-selectcheckboxmenu-token-icon')) {
                    return;
                }

                if (!$this.isLoaded()) {
                    $this._renderPanel();
                }

                $this.togglePanel();
            }
        }).on('click.selectCheckboxMenu', function(e) {
            $this.keyboardTarget.trigger('focus');
            e.preventDefault();
        });

        if (this.cfg.multiple) {
            this.bindMultipleModeEvents();
        }

        //Client Behaviors
        if (this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
    },

    /**
     * Sets up the event listeners for the overlay panel with the selectable checkbox options.
     * @private
     */
    bindPanelContentEvents: function() {
        var $this = this;

        //Events for checkboxes
        this.bindCheckboxHover(this.checkboxes);

        //Toggler
        if (this.cfg.showHeader) {
            this.bindCheckboxHover(this.togglerBox);
            this.togglerBox.on('click.selectCheckboxMenu', function() {
                var el = $(this);
                $this.toggleSelection(!el.hasClass('ui-state-active'));
            });

            //filter
            if (this.cfg.filter) {
                this.setupFilterMatcher();

                PrimeFaces.skinInput(this.filterInput);

                this.filterInput.on('keyup.selectCheckboxMenu', function() {
                    $this.filter($(this).val());
                }).on('keydown.selectCheckboxMenu', function(e) {
                    if (e.which === $.ui.keyCode.ESCAPE) {
                        $this.hide();
                    }
                });
            }

            //Closer
            this.closer.on('mouseenter.selectCheckboxMenu', function() {
                $(this).addClass('ui-state-hover');
            }).on('mouseleave.selectCheckboxMenu', function() {
                $(this).removeClass('ui-state-hover');
            }).on('click.selectCheckboxMenu', function(e) {
                $this.hide();

                e.preventDefault();
            });
        }

        //Checkboxes and labels
        this.items.on('click.selectCheckboxMenu', function(e) {
            var checkbox = $(this).find('.ui-chkbox').children('.ui-chkbox-box');
            $this.toggleItem(checkbox);
            checkbox.removeClass('ui-state-hover');
            PrimeFaces.clearSelection();
            e.preventDefault();
        });
    },

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents: function() {
        var $this = this;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            function() { return $this.triggers; },
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
     * Sets up the event listeners for all keyboard related events other than the overlay panel, such as pressing space
     * to bring up the overlay panel.
     * @private
     */
    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('focus.selectCheckboxMenu', function() {
            $this.jq.addClass('ui-state-focus');
            $this.menuIcon.addClass('ui-state-focus');
        }).on('blur.selectCheckboxMenu', function() {
            $this.jq.removeClass('ui-state-focus');
            $this.menuIcon.removeClass('ui-state-focus');
        }).on('keydown.selectCheckboxMenu', function(e) {
            var keyCode = $.ui.keyCode,
                key = e.which;

            if (!$this.isLoaded()) {
                $this._renderPanel();
            }

            switch (key) {
                case keyCode.ENTER:
                case keyCode.SPACE:
                    $this.togglePanel();
                    if ($this.panel.is(":hidden")) {
                        e.stopPropagation(); // GitHub #8340
                    }
                    e.preventDefault();
                    break;

                case keyCode.DOWN:
                    if (e.altKey) {
                        $this.togglePanel();
                    }

                    e.preventDefault();
                    break;

                case keyCode.TAB:
                    if ($this.panel.is(':visible')) {
                        if (!$this.cfg.showHeader) {
                            //".ui-chkbox" is a grandchild when columns are used!
                            $this.items.filter(':not(.ui-state-disabled):first').find('div.ui-chkbox > div.ui-helper-hidden-accessible > input').trigger('focus');
                        }
                        else {
                            $this.toggler.find('> div.ui-helper-hidden-accessible > input').trigger('focus');
                        }
                        e.preventDefault();
                    }

                    break;

                case keyCode.ESCAPE:
                    $this.hide();
                    break;
            };
        });
    },

    /**
     * Sets up the event listeners for all keyboard related events of the overlay panel, such as pressing space to
     * toggle a checkbox.
     * @private
     */
    bindPanelKeyEvents: function() {
        var $this = this;

        if (this.cfg.showHeader) {
            this.closer.on('focus.selectCheckboxMenu', function(e) {
                $this.closer.addClass('ui-state-focus');
            })
                .on('blur.selectCheckboxMenu', function(e) {
                    $this.closer.removeClass('ui-state-focus');
                })
                .on('keydown.selectCheckboxMenu', function(e) {
                    var keyCode = $.ui.keyCode,
                        key = e.which;

                    switch (key) {
                        case keyCode.ENTER:
                        case keyCode.SPACE:
                            $this.hide();

                            e.preventDefault();
                            break;

                        case keyCode.ESCAPE:
                            $this.hide();
                            break;
                    };
                });

            var togglerCheckboxInput = this.toggler.find('> div.ui-helper-hidden-accessible > input');
            this.bindCheckboxKeyEvents(togglerCheckboxInput);
            togglerCheckboxInput.on('keydown.selectCheckboxMenu', function(e) {
                if (e.which === $.ui.keyCode.TAB && e.shiftKey) {
                    e.preventDefault();
                }
            }).on('keyup.selectCheckboxMenu', function(e) {
                if (e.which === $.ui.keyCode.SPACE) {
                    var input = $(this);
                    $this.toggleSelection(!input.prop('checked'));
                    e.preventDefault();
                }
            }).on('change.selectCheckboxMenu', function(e) {
                var input = $(this);
                $this.toggleSelection(!input.prop('checked'));
            });
        }

        //".ui-chkbox" is a grandchild when columns are used!
        var itemKeyInputs = this.items.find('div.ui-chkbox > div.ui-helper-hidden-accessible > input');
        this.bindCheckboxKeyEvents(itemKeyInputs);
        itemKeyInputs.on('keydown.selectCheckboxMenu', function(e) {
            var index = $this.items.index($(this).closest("li"));

            if (e.which === $.ui.keyCode.TAB) {
                if (!e.shiftKey && index === $this.items.length - 1) {
                    e.preventDefault();
                } else if (e.shiftKey && !$this.cfg.showHeader && index === 0) {
                    e.preventDefault();
                }
            }
        }).on('keyup.selectCheckboxMenu', function(e) {
            if (e.which === $.ui.keyCode.SPACE) {
                var input = $(this),
                    box = input.parent().next();
                $this.toggleItem(box, input);
                e.preventDefault();
            }
        }).on('change.selectCheckboxMenu', function(e) {
            var input = $(this),
                box = input.parent().next();
            $this.toggleItem(box, input);
        });
    },

    /**
     * When multi select mode is enabled: Sets up the event listeners for the overlay panel.
     * @private
     */
    bindMultipleModeEvents: function() {
        var $this = this;
        this.multiItemContainer = this.jq.children('.ui-selectcheckboxmenu-multiple-container');

        var closeSelector = '> li.ui-selectcheckboxmenu-token > .ui-selectcheckboxmenu-token-icon';
        this.multiItemContainer.off('click', closeSelector).on('click', closeSelector, null, function(e) {
            var itemValue = $(this).parent().data("item-value");
            var item = $this.items.filter('[data-item-value="' + $.escapeSelector(itemValue) + '"]');
            if (item && item.length) {
                if (!$this.isLoaded()) {
                    $this._renderPanel();
                }

                //".ui-chkbox" is a grandchild when columns are used!
                $this.uncheck(item.find('.ui-chkbox').children('.ui-chkbox-box'), true);

                if ($this.hasBehavior('itemUnselect')) {
                    var ext = {
                        params: [
                            { name: $this.id + '_itemUnselect', value: itemValue }
                        ]
                    };

                    $this.callBehavior('itemUnselect', ext);
                }
            }

            e.stopPropagation();
        });
    },

    /**
     * Sets up the event listeners for hovering over the checkboxes. Adds the appropriate hover style classes.
     * @private
     * @param {JQuery} item A checkbox for which to add the event listeners.
     */
    bindCheckboxHover: function(item) {
        item.on('mouseenter.selectCheckboxMenu', function() {
            var item = $(this);
            if (!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-hover');
            }
        }).on('mouseleave.selectCheckboxMenu', function() {
            $(this).removeClass('ui-state-hover');
        });
    },

    /**
     * Filters the available options in the overlay panel by the given search value. Note that this does not bring up
     * the overlay panel, use `show` for that.
     * @param {string} value A value against which the available options are matched.
     */
    filter: function(value) {
        var filterValue = PrimeFaces.normalize(PrimeFaces.trim(value), !this.cfg.caseSensitive);

        if (filterValue === '') {
            this.items.filter(':hidden').show();
            this.groupHeaders.show();
        }
        else {
            for (var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i),
                    label = input.next(),
                    itemLabel = PrimeFaces.normalize(label.text(), !this.cfg.caseSensitive),
                    item = this.items.eq(i);

                if(item.hasClass('ui-noselection-option')) {
                    item.hide();
                }
                else {
                    if(this.filterMatcher(itemLabel, filterValue)) {
                        item.show();
                    }
                    else {
                        item.hide();
                    }
                }
            }

            var groupHeaderLength = this.groupHeaders.length;
            for (var i = 0; i < groupHeaderLength; i++) {
                var header = $(this.groupHeaders[i]),
                    groupedItems = header.nextUntil('.ui-selectcheckboxmenu-item-group');

                if (groupedItems.length === groupedItems.filter(':hidden').length) {
                    header.hide();
                }
                else {
                    header.show();
                }
            }
        }

        var firstVisibleItem = this.items.filter(':visible:not(.ui-state-disabled):first');
        if (firstVisibleItem.length) {
            PrimeFaces.scrollInView(this.itemContainerWrapper, firstVisibleItem);
        }

        if (this.cfg.scrollHeight) {
            if (this.itemContainer.height() < this.cfg.initialHeight) {
                this.itemContainerWrapper.css('height', 'auto');
            }
            else {
                this.itemContainerWrapper.height(this.cfg.initialHeight);
            }
        }

        this.updateToggler();
        this.alignPanel();
    },

    /**
     * Finds and stores the filter function which is to be used for filtering the options of this select checkbox menu.
     * @private
     */
    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode || 'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            , 'contains': this.containsFilter
            , 'endsWith': this.endsWithFilter
            , 'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectCheckboxMenu.FilterFunction` that matches the given option when it
     * starts with the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the options starts with the filter value, or `false` otherwise.
     */
    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectCheckboxMenu.FilterFunction` that matches the given option when it
     * contains the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the contains the filter value, or `false` otherwise.
     */
    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectCheckboxMenu.FilterFunction` that matches the given option when it
     * ends with the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the options ends with the filter value, or `false` otherwise.
     */
    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    /**
     * Toggles either selecting all items or unselecting all items.
     * @param {boolean} selectAll true to select all items and false to uncheck all items
     */
    toggleSelection: function(selectAll) {
        if (selectAll) {
            this.checkAll();
        }
        else {
            this.uncheckAll();
        }
    },

    /**
     * Selects all available options.
     * @param {boolean} [silent] `true` to suppress triggering event listeners, or `false` otherwise.
     */
    checkAll: function(silent) {
        if (!this.isLoaded()) {
            this._renderPanel();
        }
        var panelVisible = this.panel.is(':visible');
        for (var i = 0; i < this.items.length; i++) {
            var el = this.items.eq(i);
            if (!panelVisible || (panelVisible && el.is(':visible'))) {
                var input = this.inputs.eq(i);
                var inputNative = input[0];

                if (!inputNative.disabled) {
                    input.prop('checked', true);
                    //".ui-chkbox" is a grandchild when columns are used!
                    this.check(el.find('.ui-chkbox').children('.ui-chkbox-box'));

                    if (this.cfg.multiple) {
                        this.createMultipleItem(el);
                    }
                }
            }
        }

        this.check(this.togglerBox);

        if (!silent) {
            var togglerInput = this.togglerBox.prev().children('input');
            if (this.cfg.onChange) {
                this.cfg.onChange.call(this);
            }

            if (!this.togglerBox.hasClass('ui-state-disabled')) {
                togglerInput.trigger('focus.selectCheckboxMenu');
                this.togglerBox.addClass('ui-state-active');
            }

            if (this.cfg.multiple) {
                this.alignPanel();
            }

            this.fireToggleSelectEvent(true);
        }
    },

    /**
     * Unselects all available options.
     * @param {boolean} [silent] `true` to suppress triggering event listeners, or `false` otherwise.
     */
    uncheckAll: function(silent) {
        if (!this.isLoaded()) {
            this._renderPanel();
        }
        var panelVisible = this.panel.is(':visible');
        for (var i = 0; i < this.items.length; i++) {
            var el = this.items.eq(i);
            if (!panelVisible || (panelVisible && el.is(':visible'))) {
                var input = this.inputs.eq(i);
                var inputNative = input[0];

                if (!inputNative.disabled) {
                    this.inputs.eq(i).prop('checked', false);
                    //".ui-chkbox" is a grandchild when columns are used!
                    this.uncheck(el.find('.ui-chkbox').children('.ui-chkbox-box'));

                    if (this.cfg.multiple) {
                        this.multiItemContainer.children().remove();
                    }
                }
            }
        }

        this.uncheck(this.togglerBox);

        if (!silent) {
            var togglerInput = this.togglerBox.prev().children('input');
            if (this.cfg.onChange) {
                this.cfg.onChange.call(this);
            }

            if (!this.togglerBox.hasClass('ui-state-disabled')) {
                togglerInput.trigger('focus.selectCheckboxMenu');
            }

            if (this.cfg.multiple) {
                this.alignPanel();
            }

            this.fireToggleSelectEvent(false);
        }
        
        this.renderLabel();
    },

    /**
     * Triggers the select behavior, if any, when a checkbox option was selected or unselected.
     * @private
     * @param {boolean} checked Whether the checkbox option is now checked.
     */
    fireToggleSelectEvent: function(checked) {
        if (this.hasBehavior('toggleSelect')) {
            var ext = {
                params: [{ name: this.id + '_checked', value: checked }]
            };

            this.callBehavior('toggleSelect', ext);
        }
    },

    /**
     * Selects the given checkbox option.
     * @private
     * @param {JQuery} checkbox Checkbox option to select.
     * @param {boolean} updateInput If `true`, update the hidden input field with the current value of this widget.
     */
    check: function(checkbox, updateInput) {
        if (!checkbox.hasClass('ui-state-disabled')) {
            var checkedInput = checkbox.prev().children('input'),
                item = checkbox.closest('.ui-selectcheckboxmenu-item');

            checkedInput.prop('checked', true);
            if (updateInput) {
                checkedInput.trigger('focus.selectCheckboxMenu');
            }

            checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
            item.removeClass('ui-selectcheckboxmenu-unchecked').addClass('ui-selectcheckboxmenu-checked');

            if (updateInput) {
                var itemGroups = item.prevAll('.ui-selectcheckboxmenu-item-group'),
                    input = this.inputs.eq(item.index() - itemGroups.length);
                input.prop('checked', true).trigger('change');

                this.updateToggler();

                if (this.cfg.multiple) {
                    this.createMultipleItem(item);
                    this.alignPanel();
                }
            }

            this.updateLabel();
        }
    },

    /**
     * Unselects the given checkbox option.
     * @private
     * @param {JQuery} checkbox Checkbox option to unselect.
     * @param {boolean} updateInput If `true`, update the hidden input field with the current value of this widget.
     */
    uncheck: function(checkbox, updateInput) {
        if (!checkbox.hasClass('ui-state-disabled')) {
            var uncheckedInput = checkbox.prev().children('input'),
                item = checkbox.closest('.ui-selectcheckboxmenu-item');
            checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            checkbox.closest('.ui-selectcheckboxmenu-item').addClass('ui-selectcheckboxmenu-unchecked').removeClass('ui-selectcheckboxmenu-checked');
            uncheckedInput.prop('checked', false);

            if (updateInput) {
                var itemGroups = item.prevAll('.ui-selectcheckboxmenu-item-group'),
                    input = this.inputs.eq(item.index() - itemGroups.length);
                input.prop('checked', false).trigger('change');
                uncheckedInput.trigger('focus.selectCheckboxMenu');
                this.updateToggler();

                if (this.cfg.multiple) {
                    this.removeMultipleItem(item);
                    this.alignPanel();
                }
            }

            this.updateLabel();
        }
    },

    /**
     * Bring up the overlay panel if its not showing or hide it if it is showing.
     */
    togglePanel: function() {
        if (this.panel.is(":hidden")) {
            this.show();
        }
        else {
            this.hide();
        }
    },

    /**
     * Brings up the overlay panel with the available checkbox options.
     */
    show: function() {
        var $this = this;

        if (this.panel.is(":hidden") && this.transition) {
            this.transition.show({
                onEnter: function() {
                    $this.panel.css('z-index', PrimeFaces.nextZindex());
                    $this.alignPanel();
                },
                onEntered: function() {
                    $this.keyboardTarget.attr('aria-expanded', true);
                    $this.postShow();
                    $this.bindPanelEvents();
                }
            });
        }
    },

    /**
     * Hides the overlay panel with the available checkbox options.
     */
    hide: function() {
        if (this.panel.is(':visible') && this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    $this.keyboardTarget.attr('aria-expanded', false);
                    $this.postHide();
                }
            });

            this.keyboardTarget.trigger('focus');
        }
    },

    /**
     * Callback that is invoked after the overlay panel with the checkbox options was made visible.
     * @private
     */
    postShow: function() {
        if (this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },

    /**
     * Callback that is invoked after the overlay panel with the checkbox options was hidden.
     * @private
     */
    postHide: function() {
        if (this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },

    /**
     * Align the overlay panel with the available checkbox options so that is is positioned next to the the button.
     */
    alignPanel: function() {
        var fixedPosition = this.panel.css('position') == 'fixed',
            win = $(window),
            positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null,
            panelStyle = this.panel.attr('style');

        this.panel.css({
            'left': '',
            'top': '',
            'z-index': PrimeFaces.nextZindex(),
            'transform-origin': 'center top'
        });

        if (this.panel.parent().attr('id') === this.id) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px'
            });
        }
        else {
            this.panel.position({
                my: 'left top'
                , at: 'left bottom'
                , of: this.jq
                , offset: positionOffset
                , collision: 'flipfit'
                , using: function(pos, directions) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }

        if (!this.widthAligned && (this.panel.width() < this.jq.width()) && (!panelStyle || panelStyle.toLowerCase().indexOf('width') === -1)) {
            this.panel.width(this.jq.width());
            this.widthAligned = true;
        }
    },

    /**
     * Select or unselect the given checkbox option.
     * @param {JQuery} checkbox One of the checkbox options of this widget to toggle.
     * @param {JQuery} input (optional) input element representing the value of the checkbox
     */
    toggleItem: function(checkbox, input) {
        if (!checkbox.hasClass('ui-state-disabled')) {
            var isChecked = !input || input.prop('checked');
            if (checkbox.hasClass('ui-state-active') && isChecked) {
                this.uncheck(checkbox, true);
            }
            else {
                this.check(checkbox, true);
            }
        }
    },

    /**
     * Updates the `select all` / `unselect all` toggler so that it reflects the currently selected options.
     * @private
     */
    updateToggler: function() {
        if (this.cfg.showHeader) {
            var visibleItems = this.items.filter(':visible');

            if (visibleItems.length && visibleItems.filter('.ui-selectcheckboxmenu-unchecked').length === 0) {
                this.check(this.togglerBox);
            }
            else {
                this.uncheck(this.togglerBox);
            }
        }
    },

    /**
     * Sets up the keyboard event listeners for the given checkbox options.
     * @private
     * @param {JQuery} items Checkbo options for which to add the event listeners.
     */
    bindCheckboxKeyEvents: function(items) {
        var $this = this;

        items.on('focus.selectCheckboxMenu', function(e) {
            var input = $(this),
                box = input.parent().next();

            box.addClass('ui-state-focus');

            PrimeFaces.scrollInView($this.itemContainerWrapper, box);
        })
            .on('blur.selectCheckboxMenu', function(e) {
                var input = $(this),
                    box = input.parent().next();

                box.removeClass('ui-state-focus');
            })
            .on('keydown.selectCheckboxMenu', function(e) {
                var keyCode = $.ui.keyCode,
                    key = e.which;

                if (key === keyCode.SPACE) {
                    e.preventDefault();
                }
                else if (key === keyCode.ESCAPE) {
                    $this.hide();
                }
            });
    },

    /**
     * When multi mode is disabled: Upates the label that indicates the currently selected item.
     * @private
     */
    updateLabel: function() {
        var checkedItems = this.jq.find(':checked'),
            labelText = '';

        if (checkedItems && checkedItems.length) {
            for (var i = 0; i < checkedItems.length; i++) {
                if (i > 0) {
                    labelText = labelText + this.cfg.labelSeparator;
                }
                labelText = labelText + ($(checkedItems[i]).next().text() || '');
            }
            this.labelContainer.addClass('ui-state-active');
        }
        else {
            labelText = this.cfg.emptyLabel || this.defaultLabel || '';
            this.labelContainer.removeClass('ui-state-active');
        }

        if (this.cfg.updateLabel && !(this.cfg.multiple && labelText.length > 0)) {
             this.label.text(labelText);
             this.labelContainer.attr('title', labelText);
        }
    },

    /**
     * When multi mode is enabled: Creates a tag for the given item that was checked.
     * @private
     * @param {JQuery} item The checkbox item that was checked. 
     */
    createMultipleItem: function(item) {
        var items = this.multiItemContainer.children();
        var itemCount = !items.length ? 0 : items.filter('[data-item-value="' + $.escapeSelector(item.data('item-value')) + '"]').length;
        if (itemCount > 0) {
            return;
        }

        var itemGroups = item.prevAll('.ui-selectcheckboxmenu-item-group'),
            input = this.inputs.eq(item.index() - itemGroups.length),
            escaped = input.data('escaped'),
            labelHtml = input.next().html().trim(),
            labelLength = labelHtml.length,
            label = labelLength > 0 && labelHtml !== '&nbsp;' ? (escaped ? PrimeFaces.escapeHTML(input.next().text()) : input.next().html()) : PrimeFaces.escapeHTML(input.val()),
            itemDisplayMarkup = '<li class="ui-selectcheckboxmenu-token ui-state-active ui-corner-all" data-item-value="' + PrimeFaces.escapeHTML(input.val()) + '">';
        itemDisplayMarkup += '<span class="ui-selectcheckboxmenu-token-icon ui-icon ui-icon-close"></span>';
        itemDisplayMarkup += '<span class="ui-selectcheckboxmenu-token-label">' + label + '</span></li>';

        if (items.filter('[class="ui-selectcheckboxmenu-emptylabel"]').length) {
            this.multiItemContainer.empty();
        }
        this.multiItemContainer.append(itemDisplayMarkup);
    },

    /**
     * When multi mode is enabled: Removes all visible tags with the same value as the given checkbox item.
     * @private
     * @param {JQuery} item Checkbox item that was unchecked.
     */
    removeMultipleItem: function(item) {
        var items = this.multiItemContainer.children();
        if (items.length) {
            items.filter('[data-item-value="' + $.escapeSelector(item.data('item-value')) + '"]').remove();
        }
        // update the label if there are no more items to display empty
        this.renderLabel();
    },

    /**
     * Checks the checkbox option with the given value.
     * @param {string} value Value of the option to check.
     */
    selectValue: function(value) {
        var idx = -1;
        // find input-index
        for (var i = 0; i < this.inputs.length; i++) {
            if (this.inputs.eq(i).val() === value) {
                idx = i;
                break;
            }
        }
        if (idx === -1) {
            return;
        }
        var input = this.inputs.eq(idx);   // the hidden input
        var item = this.items.eq(idx);    // the Overlay-Panel-Item (li)

        // check (see this.checkAll())
        input.prop('checked', true);
        //".ui-chkbox" is a grandchild when columns are used!
        this.check(item.find('.ui-chkbox').children('.ui-chkbox-box'));

        if (this.cfg.multiple) {
            this.createMultipleItem(item);
        }
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq, this.inputs);
        this.disabled = false;
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.inputs);
        this.disabled = true;
    },

    /**
     * Has the panel been loaded with checkbox data yet?
     * @return {boolean} `true` when the panel has been loaded with checkbox items 
     */
    isLoaded: function() {
        return this.cfg.dynamic === false || this.isDynamicLoaded;
    },

    /**
     * Resets the input.
     * @param {boolean} [silent] `true` to suppress triggering event listeners, or `false` otherwise.
     */
    resetValue: function(silent) {
        if (this.isLoaded()) {
            this.uncheckAll(silent);
        }
    }

});
