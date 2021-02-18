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
 * @prop {JQuery} defaultLabel The DOM element for the default label.
 * @prop {boolean} disabled Whether this widget is currently disabled.
 * @prop {JQuery} groupHeaders The DOM elements for the headers of each option group.
 * @prop {PrimeFaces.widget.SelectCheckboxMenu.FilterFunction} filterMatcher The filter that was selected and is
 * currently used.
 * @prop {Record<PrimeFaces.widget.SelectCheckboxMenu.FilterMatchMode, PrimeFaces.widget.SelectCheckboxMenu.FilterFunction>} filterMatchers
 * Map between the available filter types and the filter implementation.
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
 * @prop {string} tabindex Tab index of this widget.
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
 * @prop {string} cfg.labelSeparator Separator for joining item lables if updateLabel is set to true. Default is `,`.
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
        this.inputs = this.jq.find(':checkbox');
        this.panelId = this.id + '_panel';
        this.labelId = this.id + '_label';
        this.keyboardTarget = $(this.jqId + '_focus');
        this.tabindex = this.keyboardTarget.attr('tabindex');
        this.cfg.showHeader = (this.cfg.showHeader === undefined) ? true : this.cfg.showHeader;
        this.cfg.dynamic = this.cfg.dynamic === true ? true : false;
        this.isDynamicLoaded = false;
        this.cfg.labelSeparator = (this.cfg.labelSeparator === undefined) ? ', ' : this.cfg.labelSeparator;

        if(!this.disabled) {
            if(this.cfg.multiple) {
                this.triggers = this.jq.find('.ui-selectcheckboxmenu-trigger, .ui-selectcheckboxmenu-multiple-container');
            }

            if(!this.cfg.dynamic) {
                this._renderPanel();
            }

            this.bindEvents();
            this.bindKeyEvents();

            //mark trigger and descandants of trigger as a trigger for a primefaces overlay
            this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

            if(!this.cfg.multiple) {
                if(this.cfg.updateLabel) {
                    this.defaultLabel = this.label.text();
                    this.label.css({
                        'text-overflow': 'ellipsis',
                        overflow: 'hidden'
                    });

                    this.updateLabel();
                }

                this.label.attr('id', this.labelId);
                this.keyboardTarget.attr('aria-expanded', false).attr('aria-labelledby', this.labelId);
            }

            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');
        } else {
            // disabled
            if(!this.cfg.multiple) {
                if (this.cfg.updateLabel) {
                    this.defaultLabel = this.label.text();
                    this.label.css({
                        'text-overflow': 'ellipsis',
                        overflow: 'hidden'
                    });

                    this.updateLabel();
                }
            }
        }

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

        if(this.tabindex) {
            this.panel.find('a, input').attr('tabindex', this.tabindex);
        }

        this.checkboxes = this.itemContainer.find('.ui-chkbox-box:not(.ui-state-disabled)');
        this.labels = this.itemContainer.find('label');

        this.bindPanelContentEvents();
        this.bindPanelKeyEvents();

        this.isDynamicLoaded = true;
    },

    /**
     * Creates the overlay panel with the checkboxes for the selectable option.
     * @private
     */
    renderPanel: function() {
        this.panel = $('<div id="' + this.panelId + '" class="ui-selectcheckboxmenu-panel ui-widget ui-widget-content ui-corner-all ui-helper-hidden ui-input-overlay" role="dialog"></div>');
        if(this.cfg.panelStyle) {
            this.panel.attr('style', this.cfg.panelStyle);
        }
        if(this.cfg.panelStyleClass) {
            this.panel.addClass(this.cfg.panelStyleClass);
        }
        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this, this.panel);

        PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');

        this.renderHeader();

        this.renderItems();

        if(this.cfg.scrollHeight) {
            this.itemContainerWrapper.height(this.cfg.scrollHeight);
        }
        else if(this.inputs.length > 10) {
            this.itemContainerWrapper.height(200);
        }
    },

    /**
     * Creates the header of the overlay panel with the selectable checkbox options. The header contains the `select all`
     * checkbox, the filter input field and the close icon.
     * @private
     */
    renderHeader: function() {
        this.header = $('<div class="ui-widget-header ui-corner-all ui-selectcheckboxmenu-header ui-helper-clearfix"></div>')
                        .appendTo(this.panel);

        if(!this.cfg.showHeader) {
            this.header.removeClass('ui-helper-clearfix').addClass('ui-helper-hidden');
        }
        //toggler
        this.toggler = $('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox" role="checkbox" aria-label="Select All" readonly="readonly"></div><div class="ui-chkbox-box ui-widget ui-corner-all ui-state-default"><span class="ui-chkbox-icon ui-icon ui-icon-blank"></span></div></div>')
                            .appendTo(this.header);
        this.togglerBox = this.toggler.children('.ui-chkbox-box');
        if(this.inputs.filter(':not(:checked)').length === 0) {
            this.check(this.togglerBox);
        }

        //filter
        if(this.cfg.filter) {
            this.filterInputWrapper = $('<div class="ui-selectcheckboxmenu-filter-container"></div>').appendTo(this.header);
            this.filterInput = $('<input type="text" aria-multiline="false" aria-readonly="false" aria-disabled="false" aria-label="Filter Input" role="textbox" class="ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all">')
                                .appendTo(this.filterInputWrapper);

            if(this.cfg.filterPlaceholder) {
                this.filterInput.attr('placeholder', this.cfg.filterPlaceholder);
            }

            this.filterInputWrapper.append("<span class='ui-icon ui-icon-search'></span>");
        }

        //closer
        this.closer = $('<a class="ui-selectcheckboxmenu-close ui-corner-all" href="#"><span class="ui-icon ui-icon-circle-close"></span></a>')
                    .attr('aria-label', 'Close').appendTo(this.header);

    },

    /**
     * Creates the individual checkboxes for each selectable option in the overlay panel.
     * @private
     */
    renderItems: function() {
        var $this = this;

        this.itemContainerWrapper = $('<div class="ui-selectcheckboxmenu-items-wrapper"><ul class="ui-selectcheckboxmenu-items ui-selectcheckboxmenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset"></ul></div>')
                .appendTo(this.panel);

        this.itemContainer = this.itemContainerWrapper.children('ul.ui-selectcheckboxmenu-items');

        //check if inputs must be grouped
        var grouped = this.inputs.filter('[group-label]');

        var currentGroupName = null;
        for(var i = 0; i < this.inputs.length; i++) {
            var input = this.inputs.eq(i),
            label = input.next(),
            disabled = input.is(':disabled'),
            checked = input.is(':checked'),
            title = input.attr('title'),
            boxClass = 'ui-chkbox-box ui-widget ui-corner-all ui-state-default',
            itemClass = 'ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all',
            escaped = input.data('escaped');

            if(grouped.length && currentGroupName !== input.attr('group-label')) {
                currentGroupName = input.attr('group-label');
            	var itemGroup = $('<li class="ui-selectcheckboxmenu-item-group ui-selectcheckboxmenu-group-list-item ui-corner-all"></li>');
            	itemGroup.text(currentGroupName);
            	$this.itemContainer.append(itemGroup);
            }

            if(disabled) {
                boxClass += " ui-state-disabled";
            }

            if(checked) {
                boxClass += " ui-state-active";
            }

            var iconClass = checked ? 'ui-chkbox-icon ui-icon ui-icon-check' : 'ui-chkbox-icon ui-icon ui-icon-blank',
            itemClass = checked ? itemClass + ' ui-selectcheckboxmenu-checked' : itemClass + ' ui-selectcheckboxmenu-unchecked';

            var item = $('<li class="' + itemClass + '"></li>');
            item.append('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox" role="checkbox" readonly="readonly"></input></div>' +
                    '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div></div>');

            var uuid = PrimeFaces.uuid();
            var itemLabel = $('<label for='+uuid+'></label>'),
            labelHtml = label.html().trim(),
            labelLength = labelHtml.length;
            if (labelLength > 0 && labelHtml !== '&nbsp;')
                if(escaped)
                    itemLabel.text(label.text());
                else
                    itemLabel.html(label.html());
            else
                itemLabel.text(input.val());

            itemLabel.appendTo(item);

            if(title) {
                item.attr('title', title);
            }

            if($this.cfg.multiple) {
                item.attr('data-item-value', input.val());
            }

            item.find('> .ui-chkbox > .ui-helper-hidden-accessible > input').prop('checked', checked).attr('aria-checked', checked).attr('id', uuid);
            $this.itemContainer.attr('role', 'group');

            $this.itemContainer.append(item);
        }

        this.items = this.itemContainer.children('li.ui-selectcheckboxmenu-item');
        this.groupHeaders = this.itemContainer.children('li.ui-selectcheckboxmenu-item-group');
    },

    /**
     * Sets up all event listenters required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        //Events to show/hide the panel
        this.triggers.on('mouseover.selectCheckboxMenu', function() {
            if(!$this.disabled&&!$this.triggers.hasClass('ui-state-focus')) {
                $this.jq.addClass('ui-state-hover');
                $this.triggers.addClass('ui-state-hover');
            }
        }).on('mouseout.selectCheckboxMenu', function() {
            if(!$this.disabled) {
                $this.jq.removeClass('ui-state-hover');
                $this.triggers.removeClass('ui-state-hover');
            }
        }).on('mousedown.selectCheckboxMenu', function(e) {
            if(!$this.disabled) {
                if($this.cfg.multiple && $(e.target).is('.ui-selectcheckboxmenu-token-icon')) {
                    return;
                }

                if($this.cfg.dynamic && !$this.isDynamicLoaded) {
                    $this._renderPanel();
                }

                if($this.panel.is(":hidden")) {
                    $this.show();
                }
                else {
                    $this.hide();
                }
            }
        }).on('click.selectCheckboxMenu', function(e) {
            $this.jq.removeClass('ui-state-hover');
            $this.triggers.removeClass('ui-state-hover');
            $this.keyboardTarget.trigger('focus');
            e.preventDefault();
        });

        if(this.cfg.multiple) {
            this.bindMultipleModeEvents();
        }

        //Client Behaviors
        if(this.cfg.behaviors) {
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
        this.checkboxes.on('click.selectCheckboxMenu', function() {
            $this.toggleItem($(this));
        });

        //Toggler
        this.bindCheckboxHover(this.togglerBox);
        this.togglerBox.on('click.selectCheckboxMenu', function() {
            var el = $(this);
            if(el.hasClass('ui-state-active')) {
                $this.uncheckAll();
                el.addClass('ui-state-hover');
            }
            else {
                $this.checkAll();
                el.removeClass('ui-state-hover');
            }
        });

        //filter
        if(this.cfg.filter) {
            this.setupFilterMatcher();

            PrimeFaces.skinInput(this.filterInput);

            this.filterInput.on('keyup.selectCheckboxMenu', function() {
                $this.filter($(this).val());
            }).on('keydown.selectCheckboxMenu', function(e) {
                if(e.which === $.ui.keyCode.ESCAPE) {
                    $this.hide();
                }
            });
        }

        //Closer
        this.closer.on('mouseenter.selectCheckboxMenu', function(){
            $(this).addClass('ui-state-hover');
        }).on('mouseleave.selectCheckboxMenu', function() {
            $(this).removeClass('ui-state-hover');
        }).on('click.selectCheckboxMenu', function(e) {
            $this.hide();

            e.preventDefault();
        });

        //Labels
        this.labels.on('click.selectCheckboxMenu', function(e) {
            var checkbox = $(this).prev().children('.ui-chkbox-box');
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
                if(!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, function() {
            $this.hide();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, function() {
            $this.hide();
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

            if($this.cfg.dynamic && !$this.isDynamicLoaded) {
                $this._renderPanel();
            }

            switch(key) {
                case keyCode.ENTER:
                case keyCode.SPACE:
                    if ($this.panel.is(":hidden"))
                        $this.show();
                    else
                        $this.hide();

                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    if (e.altKey) {
                        if ($this.panel.is(":hidden"))
                            $this.show();
                        else
                            $this.hide();
                    }

                    e.preventDefault();
                break;

                case keyCode.TAB:
                    if($this.panel.is(':visible')) {
                       if(!$this.cfg.showHeader) {
                            $this.itemContainer.children('li:not(.ui-state-disabled):first').find('div.ui-helper-hidden-accessible > input').trigger('focus');
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

        this.closer.on('focus.selectCheckboxMenu', function(e) {
            $this.closer.addClass('ui-state-focus');
        })
        .on('blur.selectCheckboxMenu', function(e) {
            $this.closer.removeClass('ui-state-focus');
        })
        .on('keydown.selectCheckboxMenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.ENTER:
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
        togglerCheckboxInput.on('keyup.selectCheckboxMenu', function(e) {
                    if(e.which === $.ui.keyCode.SPACE) {
                        var input = $(this);

                        if(input.prop('checked'))
                            $this.uncheckAll();
                        else
                            $this.checkAll();

                        e.preventDefault();
                    }
                })
                .on('change.selectCheckboxMenu', function(e) {
                    var input = $(this);

                    if(input.prop('checked')) {
                        $this.checkAll();
                    }
                    else {
                        $this.uncheckAll();
                    }
                });

        var itemKeyInputs = this.itemContainer.find('> li > div.ui-chkbox > div.ui-helper-hidden-accessible > input');
        this.bindCheckboxKeyEvents(itemKeyInputs);
        itemKeyInputs.on('keyup.selectCheckboxMenu', function(e) {
                    if(e.which === $.ui.keyCode.SPACE) {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked'))
                            $this.uncheck(box, true);
                        else
                            $this.check(box, true);

                        e.preventDefault();
                    }
                })
                .on('change.selectCheckboxMenu', function(e) {
                    var input = $(this),
                    box = input.parent().next();

                    if(input.prop('checked')) {
                        $this.check(box, true);
                    }
                    else {
                        $this.uncheck(box, true);
                    }
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
            var item = $this.items.filter('[data-item-value="' + $.escapeSelector(itemValue) +'"]');
            if(item && item.length) {
                if($this.cfg.dynamic && !$this.isDynamicLoaded) {
                    $this._renderPanel();
                }

                $this.uncheck(item.children('.ui-chkbox').children('.ui-chkbox-box'), true);

                if($this.hasBehavior('itemUnselect')) {
                    var ext = {
                        params : [
                            {name: $this.id + '_itemUnselect', value: itemValue}
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
            if(!item.hasClass('ui-state-active')&&!item.hasClass('ui-state-disabled')) {
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
        var filterValue = this.cfg.caseSensitive ? PrimeFaces.trim(value) : PrimeFaces.trim(value).toLowerCase();

        if(filterValue === '') {
            this.itemContainer.children('li.ui-selectcheckboxmenu-item').filter(':hidden').show();
        }
        else {
            for(var i = 0; i < this.labels.length; i++) {
                var labelElement = this.labels.eq(i),
                item = labelElement.parent(),
                itemLabel = this.cfg.caseSensitive ? labelElement.text() : labelElement.text().toLowerCase();

                if(this.filterMatcher(itemLabel, filterValue)) {
                    item.show();
                }
                else {
                    item.hide();
                }
            }
        }

        var groupHeaderLength = this.groupHeaders.length;
        for(var i = 0; i < groupHeaderLength; i++) {
            var header = $(this.groupHeaders[i]),
            groupedItems = header.nextUntil('li.ui-selectcheckboxmenu-item-group');

            if(groupedItems.length === groupedItems.filter(':hidden').length) {
                header.hide();
            }
            else {
                header.show();
            }
        }

        if(this.cfg.scrollHeight) {
            if(this.itemContainer.height() < this.cfg.initialHeight) {
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
     * Selects all available options. Note that this required the overlay panel to be visible, use `show` for that.
     */
    checkAll: function() {
        for(var i = 0; i < this.items.length; i++) {
            var el = this.items.eq(i);

            if(el.is(':visible')) {
                var input = this.inputs.eq(i);
                var inputNative = input[0];

                if(!inputNative.disabled) {
                    input.prop('checked', true).attr('aria-checked', true);
                    this.check(el.children('.ui-chkbox').children('.ui-chkbox-box'));

                    if (this.cfg.multiple) {
                        this.createMultipleItem(el);
                    }
                }
            }
        }

        this.check(this.togglerBox);

        var togglerInput = this.togglerBox.prev().children('input');
        if(this.cfg.onChange) {
            this.cfg.onChange.call(this);
        }

        if(!this.togglerBox.hasClass('ui-state-disabled')) {
            togglerInput.trigger('focus.selectCheckboxMenu');
            this.togglerBox.addClass('ui-state-active');
        }

        if(this.cfg.multiple) {
            this.alignPanel();
        }

        this.fireToggleSelectEvent(true);
    },

    /**
     * Unselects all available options. Note that this required the overlay panel to be visible, use `show` for that.
     */
    uncheckAll: function() {
        for(var i = 0; i < this.items.length; i++) {
            var el = this.items.eq(i);

            if(el.is(':visible')) {
                var input = this.inputs.eq(i);
                var inputNative = input[0];

                if(!inputNative.disabled) {
                    this.inputs.eq(i).prop('checked', false).attr('aria-checked', false);
                    this.uncheck(el.children('.ui-chkbox').children('.ui-chkbox-box'));

                    if (this.cfg.multiple) {
                        this.multiItemContainer.children().remove();
                    }
                }
            }
        }

        this.uncheck(this.togglerBox);

        var togglerInput = this.togglerBox.prev().children('input');
        if(this.cfg.onChange) {
            this.cfg.onChange.call(this);
        }

        if(!this.togglerBox.hasClass('ui-state-disabled')) {
            togglerInput.trigger('focus.selectCheckboxMenu');
        }

        if(this.cfg.multiple) {
            this.alignPanel();
        }

        this.fireToggleSelectEvent(false);
    },

    /**
     * Triggers the select behavior, if any, when a checkbox option was selected or unselected.
     * @private
     * @param {boolean} checked Whether the checkbox option is now checked.
     */
    fireToggleSelectEvent: function(checked) {
        if(this.hasBehavior('toggleSelect')) {
            var ext = {
                params: [{name: this.id + '_checked', value: checked}]
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
        if(!checkbox.hasClass('ui-state-disabled')) {
            var checkedInput = checkbox.prev().children('input'),
            item = checkbox.closest('li.ui-selectcheckboxmenu-item');

            checkedInput.prop('checked', true).attr('aria-checked', true);
            if(updateInput) {
                checkedInput.trigger('focus.selectCheckboxMenu');
            }

            checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
            item.removeClass('ui-selectcheckboxmenu-unchecked').addClass('ui-selectcheckboxmenu-checked');

            if(updateInput) {
            	var itemGroups = item.prevAll('li.ui-selectcheckboxmenu-item-group'),
                input = this.inputs.eq(item.index() - itemGroups.length);
                input.prop('checked', true).attr('aria-checked', true).trigger('change');

                this.updateToggler();

                if(this.cfg.multiple) {
                    this.createMultipleItem(item);
                    this.alignPanel();
                }
            }

            if(this.cfg.updateLabel) {
                this.updateLabel();
            }
        }
    },

    /**
     * Unselects the given checkbox option.
     * @private
     * @param {JQuery} checkbox Checkbox option to unselect.
     * @param {boolean} updateInput If `true`, update the hidden input field with the current value of this widget.
     */
    uncheck: function(checkbox, updateInput) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var uncheckedInput = checkbox.prev().children('input'),
            item = checkbox.closest('li.ui-selectcheckboxmenu-item');
            checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            checkbox.closest('li.ui-selectcheckboxmenu-item').addClass('ui-selectcheckboxmenu-unchecked').removeClass('ui-selectcheckboxmenu-checked');
            uncheckedInput.prop('checked', false).attr('aria-checked', false);

            if(updateInput) {
                var itemGroups = item.prevAll('li.ui-selectcheckboxmenu-item-group'),
                input = this.inputs.eq(item.index() - itemGroups.length);
                input.prop('checked', false).attr('aria-checked', false).trigger('change');
                uncheckedInput.trigger('focus.selectCheckboxMenu');
                this.updateToggler();

                if(this.cfg.multiple) {
                    this.removeMultipleItem(item);
                    this.alignPanel();
                }
            }

            if(this.cfg.updateLabel) {
                this.updateLabel();
            }
        }
    },

    /**
     * Brings up the overlay panel with the available checkbox options.
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
        }
    },

    /**
     * Callback that is invoked after the overlay panel with the checkbox options was made visible.
     * @private
     */
    postShow: function() {
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },

    /**
     * Callback that is invoked after the overlay panel with the checkbox options was hidden.
     * @private
     */
    postHide: function() {
        if(this.cfg.onHide) {
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
                'left':'',
                'top':'',
                'z-index': PrimeFaces.nextZindex(),
                'transform-origin': 'center top'
        });

        if(this.panel.parent().attr('id') === this.id) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px'
            });
        }
        else {
            this.panel.position({
                                my: 'left top'
                                ,at: 'left bottom'
                                ,of: this.jq
                                ,offset : positionOffset
                                ,collision: 'flipfit'
                                ,using: function(pos, directions) {
                                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                                }
                            });
        }

        if(!this.widthAligned && (this.panel.width() < this.jq.width()) && (!panelStyle||panelStyle.toLowerCase().indexOf('width') === -1)) {
            this.panel.width(this.jq.width());
            this.widthAligned = true;
        }
    },

    /**
     * Select or unselect the given checkbox option.
     * @param {JQuery} checkbox One of the checkbox options of this widget to toggle.
     */
    toggleItem: function(checkbox) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            if(checkbox.hasClass('ui-state-active')) {
                this.uncheck(checkbox, true);
                checkbox.addClass('ui-state-hover');
            }
            else {
                this.check(checkbox, true);
                checkbox.removeClass('ui-state-hover');
            }
        }
    },

    /**
     * Updates the `select all` / `unselect all` toggler so that it reflects the currently selected options.
     * @private
     */
    updateToggler: function() {
        var visibleItems = this.itemContainer.children('li.ui-selectcheckboxmenu-item:visible');

        if(visibleItems.length && visibleItems.filter('.ui-selectcheckboxmenu-unchecked').length === 0) {
            this.check(this.togglerBox);
        }
        else {
            this.uncheck(this.togglerBox);
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

            if(key === keyCode.SPACE) {
                e.preventDefault();
            }
            else if(key === keyCode.ESCAPE) {
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

        if(checkedItems && checkedItems.length) {
            for(var i = 0; i < checkedItems.length; i++) {
                if(i != 0) {
                    labelText = labelText + this.cfg.labelSeparator;
                }
                labelText = labelText + $(checkedItems[i]).next().text();
            }
        }
        else {
            if (this.cfg.emptyLabel) {
                labelText = this.cfg.emptyLabel;
            } else {
                labelText = this.defaultLabel;
            }
        }

        this.label.text(labelText);
        this.labelContainer.attr('title', labelText);
    },

    /**
     * When multi mode is enabled: Creates a tag for the given item that was checked.
     * @private
     * @param {JQuery} item The checkbox item that was checked. 
     */
    createMultipleItem: function(item) {
        var items = this.multiItemContainer.children();
        if(items.length && items.filter('[data-item-value="' + $.escapeSelector(item.data('item-value')) + '"]').length > 0) {
            return;
        }

        var itemGroups = item.prevAll('li.ui-selectcheckboxmenu-item-group'),
        input = this.inputs.eq(item.index() - itemGroups.length),
        escaped = input.data('escaped'),
        labelHtml = input.next().html().trim(),
        labelLength = labelHtml.length,
        label = labelLength > 0 && labelHtml !== '&nbsp;' ? (escaped ? PrimeFaces.escapeHTML(input.next().text()) : input.next().html()) : PrimeFaces.escapeHTML(input.val()),
        itemDisplayMarkup = '<li class="ui-selectcheckboxmenu-token ui-state-active ui-corner-all" data-item-value="' + PrimeFaces.escapeHTML(input.val()) +'">';
        itemDisplayMarkup += '<span class="ui-selectcheckboxmenu-token-icon ui-icon ui-icon-close"></span>';
        itemDisplayMarkup += '<span class="ui-selectcheckboxmenu-token-label">' + label + '</span></li>';

        this.multiItemContainer.append(itemDisplayMarkup);
    },

    /**
     * When multi mode is enabled: Removes all visible tags with the same value as the given checkbox item.
     * @private
     * @param {JQuery} item Checkbox item that was unchecked.
     */
    removeMultipleItem: function(item) {
        var items = this.multiItemContainer.children();
        if(items.length) {
            items.filter('[data-item-value="' + $.escapeSelector(item.data('item-value')) + '"]').remove();
        }
    },

    /**
     * Checks the checkbox option with the given value.
     * @param {string} value Value of the option to check.
     */
    selectValue: function(value) {                                                                     // Patch
        var idx = -1;
        // find input-index
        for(var i = 0; i < this.inputs.length; i++) {
            if (this.inputs.eq(i).val() === value) {
                idx = i;
                break;
            }
        }
        if (idx === -1) {
            return;
        }
        var input = this.inputs.eq(idx);   // the hidden input
        var item  = this.items.eq(idx);    // the Overlay-Panel-Item (li)

        // check (see this.checkAll())
        input.prop('checked', true).attr('aria-checked', true);
        this.check(item.children('.ui-chkbox').children('.ui-chkbox-box'));

        if(this.cfg.multiple) {
            this.createMultipleItem(item);
        }
    }

});
