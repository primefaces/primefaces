/**
 * PrimeFaces SelectOneMenu Widget
 */
PrimeFaces.widget.SelectOneMenu = PrimeFaces.widget.DeferredWidget.extend({

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
        this.options = this.input.children('option');
        this.cfg.effect = this.cfg.effect||'fade';
        this.cfg.effectSpeed = this.cfg.effectSpeed||'normal';
        this.cfg.autoWidth = this.cfg.autoWidth === false ? false : true;
        this.cfg.dynamic = this.cfg.dynamic === true ? true : false;
        this.cfg.appendTo = this.getAppendTo();
        this.isDynamicLoaded = false;

        if(this.cfg.dynamic) {
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
        if (PrimeFaces.env.touch) {
            this.focusInput.attr('readonly', true);
        }

        this.renderDeferred();
    },

    initContents: function() {
        this.itemsContainer = this.itemsWrapper.children('.ui-selectonemenu-items');
        this.items = this.itemsContainer.find('.ui-selectonemenu-item');
        this.optGroupsSize = this.itemsContainer.children('li.ui-selectonemenu-item-group').length;

        var $this = this,
        selectedOption = this.options.filter(':selected'),
        highlightedItem = this.items.eq(selectedOption.index());

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

    _render: function() {
        var contentStyle = this.jq.attr('style'),
        hasWidth = contentStyle && contentStyle.indexOf('width') != -1;

        if(this.cfg.autoWidth && !hasWidth) {
            this.jq.css('min-width', this.input.outerWidth());
        }
    },

    //@override
    refresh: function(cfg) {
        this.panelWidthAdjusted = false;

        this._super(cfg);
    },

    alignPanelWidth: function() {
        //align panel and container
        if(!this.panelWidthAdjusted) {
            var jqWidth = this.jq.outerWidth();
            if(this.panel.outerWidth() < jqWidth) {
                this.panel.width(jqWidth);
            }

            this.panelWidthAdjusted = true;
        }
    },

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
        this.triggers.mouseenter(function() {
            if(!$this.jq.hasClass('ui-state-focus')) {
                $this.jq.addClass('ui-state-hover');
                $this.menuIcon.addClass('ui-state-hover');
            }
        })
        .mouseleave(function() {
            $this.jq.removeClass('ui-state-hover');
            $this.menuIcon.removeClass('ui-state-hover');
        })
        .click(function(e) {
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
            this.label.change(function(e) {
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

    bindItemEvents: function() {
        var $this = this;

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

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.panel, function() {
            $this.alignPanel();
        });
    },

    unbindEvents: function() {
        this.items.off();
        this.triggers.off();
        this.input.off();
        this.focusInput.off();
        this.label.off();
    },

    revert: function() {
        if(this.cfg.editable && this.customInput) {
            this.setLabel(this.customInputVal);
            this.items.filter('.ui-state-active').removeClass('ui-state-active');
            this.items.eq(0).addClass('ui-state-active');
        }
        else {
            this.highlightItem(this.items.eq(this.preShowValue.index()));
        }
    },

    highlightItem: function(item) {
        this.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');

        if(item.length > 0) {
            item.addClass('ui-state-highlight');
            this.setLabel(item.data('label'));
        }
    },

    triggerChange: function(edited) {
        this.changed = false;

        this.input.trigger('change');

        if(!edited) {
            this.value = this.options.filter(':selected').val();
        }
    },

    /**
     * Handler to process item selection with mouse
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
            this.focusInput.focus();
            this.callBehavior('itemSelect');
        }

        if(this.panel.is(':visible')) {
            this.hide();
        }
    },

    syncTitle: function(option) {
        var optionTitle = this.items.eq(option.index()).attr('title');
        if(optionTitle)
            this.jq.attr('title', this.items.eq(option.index()).attr('title'));
        else
            this.jq.removeAttr('title');
    },

    resolveItemIndex: function(item) {
        if(this.optGroupsSize === 0)
            return item.index();
        else
            return item.index() - item.prevAll('li.ui-selectonemenu-item-group').length;
    },

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
                    metaKey = e.metaKey||e.ctrlKey||e.shiftKey;

                    if(!metaKey) {
                        clearTimeout($this.searchTimer);

                        // #4682: check for word match
                        var text = $(this).val();
                        matchedOptions = $this.matchOptions(text);
                        if(matchedOptions.length) {
                            var highlightItem = $this.items.eq(matchedOptions.index());
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
                                   var currentIndex = option.index();
                                   var currentItem = $this.items.eq(currentIndex);
                                   if (currentItem.hasClass('ui-state-highlight')) {
                                       selectedIndex = currentIndex;
                                       return false;
                                   }
                                });

                                matchedOptions.each(function() {
                                    var option = $(this);
                                    var currentIndex = option.index();
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

    matchOptions: function(text) {
        return this.options.filter(function() {
            var option = $(this);
            return (option.is(':not(:disabled)') && (option.text().toLowerCase().indexOf(text) === 0));
        });
    },

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
                    e.stopPropagation();
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

    handleEnterKey: function(event) {
        if(this.panel.is(':visible')) {
            this.selectItem(this.getActiveItem());
        }

        event.preventDefault();
    },

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

    handleEscapeKey: function(event) {
        if(this.panel.is(':visible')) {
            this.revert();
            this.hide();
        }

        event.preventDefault();
    },

    handleTabKey: function() {
        if(this.panel.is(':visible')) {
            this.selectItem(this.getActiveItem());
        }
    },

    handleLabelChange: function(event) {
        this.customInput = true;
        this.customInputVal = $(event.target).val();
        this.items.filter('.ui-state-active').removeClass('ui-state-active');
        this.items.eq(0).addClass('ui-state-active');
    },

    show: function() {
        this.callHandleMethod(this._show, null);
    },

    _show: function() {
        var $this = this;

        this.panel.css({'display':'block', 'opacity':0, 'pointer-events': 'none'});

        this.alignPanel();

        this.panel.css({'display':'none', 'opacity':'', 'pointer-events': '', 'z-index': ++PrimeFaces.zindex});

        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
        }

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
        this.focusInput.attr('aria-expanded', true);
        this.jq.attr('aria-expanded', true);
    },

    hide: function() {
        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', '');
        }

        this.panel.css('z-index', '').hide();
        this.focusInput.attr('aria-expanded', false);
        this.jq.attr('aria-expanded', false);
    },

    focus: function() {
        this.focusInput.focus();
    },

    focusFilter: function(timeout) {
        if(timeout) {
            var $this = this;
            setTimeout(function() {
                $this.focusFilter();
            }, timeout);
        }
        else {
            this.filterInput.focus();
        }
    },

    blur: function() {
        this.focusInput.blur();

        this.callBehavior('blur');
    },

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

    alignPanel: function() {
        this.alignPanelWidth();

        if(this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.panel.css({left:0, top:0}).position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
            });
        }
    },

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

                if (option && option.data('escape') == false) {
                    this.label.html(displayedLabel);
                } else {
                    this.label.text(displayedLabel);
                }
            }
        }
    },

    selectValue : function(value) {
        var option = this.options.filter('[value="' + value + '"]');

        this.selectItem(this.items.eq(option.index()), true);
    },

    getActiveItem: function() {
        return this.items.filter('.ui-state-highlight');
    },

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

    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    filter: function(value) {
        this.cfg.initialHeight = this.cfg.initialHeight||this.itemsWrapper.height();
        var filterValue = this.cfg.caseSensitive ? $.trim(value) : $.trim(value).toLowerCase();

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

    getSelectedValue: function() {
        return this.input.val();
    },

    getSelectedLabel: function() {
        return this.options.filter(':selected').text();
    },

    getLabelToDisplay: function(value) {
        if(this.cfg.labelTemplate && value !== '&nbsp;') {
            return this.cfg.labelTemplate.replace('{0}', value);
        }
        return String(value);
    },

    changeAriaValue: function (item) {
        var itemId = item.attr('id');

        this.focusInput.attr('aria-activedescendant', itemId)
                .attr('aria-describedby', itemId);
        this.itemsContainer.attr('aria-activedescendant', itemId);
    },

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
                $this.initContents();
                $this.bindItemEvents();
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

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
            handleMethod.call(this, event);
        }
    },

    getAppendTo: function() {
        var dialog = this.jq.closest('.ui-dialog');

        if(dialog.length == 1) {
            //set position as fixed to scroll with dialog
            if(dialog.css('position') === 'fixed') {
                this.panel.css('position', 'fixed');
            }

            //append to body if not already appended by user choice
            if(!this.panel.parent().is(document.body)) {
                return "@(body)";
            }
        }

        return this.cfg.appendTo;
    },

    updatePlaceholderClass: function(add) {
        if (add) {
            this.label.addClass('ui-selectonemenu-label-placeholder');
        }
        else {
            this.label.removeClass('ui-selectonemenu-label-placeholder');
        }
    }

});
