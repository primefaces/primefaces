/**
 * PrimeFaces InputText Widget
 */
PrimeFaces.widget.InputText = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinInput(this.jq);
    },

    disable: function() {
        this.jq.prop('disabled', true).addClass('ui-state-disabled');
    },

    enable: function() {
        this.jq.prop('disabled', false).removeClass('ui-state-disabled');
    }
});

/**
 * PrimeFaces InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.autoResize)
            this.renderDeferred();
        else
            this._render();
    },
    
    _render: function() {
        //Visuals
        PrimeFaces.skinInput(this.jq);

        //autoComplete
        if(this.cfg.autoComplete) {
            this.setupAutoComplete();
        }
        
        //Counter
        if(this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate||'{0}';
            this.updateCounter();
        }

        //maxLength
        if(this.cfg.maxlength) {
            this.applyMaxlength();
        }
        
        //autoResize
        if(this.cfg.autoResize) {
            this.setupAutoResize();
        }
    },

    refresh: function(cfg) {
        //remove autocomplete panel
        if(cfg.autoComplete) {
            $(PrimeFaces.escapeClientId(cfg.id + '_panel')).remove();
        }

        this.init(cfg);
    },

    setupAutoResize: function() {
        autosize(this.jq);
    },

    applyMaxlength: function() {
        var _self = this;

        if(!this.nativeMaxlengthSupported()) {
            this.jq.on('keyup.inputtextarea-maxlength', function(e) {
                var value = _self.jq.val(),
                length = value.length;

                if(length > _self.cfg.maxlength) {
                    _self.jq.val(value.substr(0, _self.cfg.maxlength));
                }
            });
        }
        
        if(_self.counter) {
            this.jq.on('keyup.inputtextarea-counter', function(e) {
                _self.updateCounter();
            });
        }
    },

    updateCounter: function() {
        var value = this.jq.val(),
        length = value.length;

        if(this.counter) {
            var remaining = this.cfg.maxlength - length;
            if(remaining < 0) {
                remaining = 0;
            }
            
            var remainingText = this.cfg.counterTemplate.replace('{0}', remaining);

            this.counter.html(remainingText);
        }
    },

    setupAutoComplete: function() {
        var panelMarkup = '<div id="' + this.id + '_panel" class="ui-autocomplete-panel ui-widget-content ui-corner-all ui-helper-hidden ui-shadow"></div>',
        _self = this;

        this.panel = $(panelMarkup).appendTo(document.body);

        this.jq.keyup(function(e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {

                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.TAB:
                case keyCode.SPACE:
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case keyCode.ESCAPE:
                case 224:   //mac command
                    //do not search
                break;

                default:
                    var query = _self.extractQuery();
                    if(query && query.length >= _self.cfg.minQueryLength) {

                         //Cancel the search request if user types within the timeout
                        if(_self.timeout) {
                            _self.clearTimeout(_self.timeout);
                        }

                        _self.timeout = setTimeout(function() {
                            _self.search(query);
                        }, _self.cfg.queryDelay);

                    }
                break;
            }

        }).keydown(function(e) {
            var overlayVisible = _self.panel.is(':visible'),
            keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                case keyCode.LEFT:
                    if(overlayVisible) {
                        var highlightedItem = _self.items.filter('.ui-state-highlight'),
                        prev = highlightedItem.length == 0 ? _self.items.eq(0) : highlightedItem.prev();

                        if(prev.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            prev.addClass('ui-state-highlight');

                            if(_self.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(_self.panel, prev);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    if(overlayVisible) {
                        var highlightedItem = _self.items.filter('.ui-state-highlight'),
                        next = highlightedItem.length == 0 ? _self.items.eq(0) : highlightedItem.next();

                        if(next.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            next.addClass('ui-state-highlight');

                            if(_self.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(_self.panel, next);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    if(overlayVisible) {
                        _self.items.filter('.ui-state-highlight').trigger('click');

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.SPACE:
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case keyCode.BACKSPACE:
                case keyCode.ESCAPE:
                case 224:   //mac command
                    _self.clearTimeout();

                    if(overlayVisible) {
                        _self.hide();
                    }
                break;

                case keyCode.TAB:
                    _self.clearTimeout();

                    if(overlayVisible) {
                        _self.items.filter('.ui-state-highlight').trigger('click');
                        _self.hide();
                    }
                break;
            }
        });

        //hide panel when outside is clicked
        $(document.body).bind('mousedown.ui-inputtextarea', function (e) {
            if(_self.panel.is(":hidden")) {
                return;
            }
            var offset = _self.panel.offset();
            if(e.target === _self.jq.get(0)) {
                return;
            }

            if (e.pageX < offset.left ||
                e.pageX > offset.left + _self.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + _self.panel.height()) {
                _self.hide();
            }
        });

        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if(_self.panel.is(':visible')) {
                _self.hide();
            }
        });

        //dialog support
        this.setupDialogSupport();
    },

    bindDynamicEvents: function() {
        var _self = this;

        //visuals and click handler for items
        this.items.bind('mouseover', function() {
            var item = $(this);

            if(!item.hasClass('ui-state-highlight')) {
                _self.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');
                item.addClass('ui-state-highlight');
            }
        })
        .bind('click', function(event) {
            var item = $(this),
            itemValue = item.attr('data-item-value'),
            insertValue = itemValue.substring(_self.query.length);

            _self.jq.focus();

            _self.jq.insertText(insertValue, _self.jq.getSelection().start, true);

            _self.invokeItemSelectBehavior(event, itemValue);

            _self.hide();
        });
    },

    invokeItemSelectBehavior: function(event, itemValue) {
        if(this.cfg.behaviors) {
            var itemSelectBehavior = this.cfg.behaviors['itemSelect'];

            if(itemSelectBehavior) {
                var ext = {
                    params : [
                        {name: this.id + '_itemSelect', value: itemValue}
                    ]
                };

                itemSelectBehavior.call(this, ext);
            }
        }
    },

    clearTimeout: function() {
        if(this.timeout) {
            clearTimeout(this.timeout);
        }

        this.timeout = null;
    },

    extractQuery: function() {
        var end = this.jq.getSelection().end,
        result = /\S+$/.exec(this.jq.get(0).value.slice(0, end)),
        lastWord = result ? result[0] : null;

        return lastWord;
    },

    search: function(query) {
        this.query = query;

        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [
                {name: this.id + '_query', value: query}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.panel.html(content);
                            this.items = $this.panel.find('.ui-autocomplete-item');

                            this.bindDynamicEvents();

                            if(this.items.length > 0) {
                                //highlight first item
                                this.items.eq(0).addClass('ui-state-highlight');

                                //adjust height
                                if(this.cfg.scrollHeight && this.panel.height() > this.cfg.scrollHeight) {
                                    this.panel.height(this.cfg.scrollHeight);
                                }

                                if(this.panel.is(':hidden')) {
                                    this.show();
                                }  else {
                                    this.alignPanel(); //with new items
                                }

                            }
                            else {
                                this.panel.hide();
                            }
                        }
                    });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    alignPanel: function() {
        var pos = this.jq.getCaretPosition(),
        offset = this.jq.offset();

        this.panel.css({
                        'left': offset.left + pos.left,
                        'top': offset.top + pos.top,
                        'width': this.jq.innerWidth(),
                        'z-index': ++PrimeFaces.zindex
                });
    },

    show: function() {
        this.alignPanel();

        this.panel.show();
    },

    hide: function() {
        this.panel.hide();
    },

    setupDialogSupport: function() {
        var dialog = this.jq.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            this.panel.css('position', 'fixed');
        }
    },
    
    nativeMaxlengthSupported: function() {
        if(PrimeFaces.env.browser.msie)
            return (parseInt(PrimeFaces.env.browser.version, 10) > 9);
        else if(PrimeFaces.env.browser.opera)
            return (parseInt(PrimeFaces.env.browser.version, 10) > 12);
        else
            return true;
    }

});

/**
 * PrimeFaces SelectOneMenu Widget
 */
PrimeFaces.widget.SelectOneMenu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.panelId = this.jqId + '_panel';
        this.input = $(this.jqId + '_input');
        this.focusInput = $(this.jqId + '_focus');
        this.label = this.jq.find('.ui-selectonemenu-label');
        this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');
        this.panel = this.jq.children(this.panelId);
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.itemsWrapper = this.panel.children('.ui-selectonemenu-items-wrapper');
        this.itemsContainer = this.itemsWrapper.children('.ui-selectonemenu-items');
        this.items = this.itemsContainer.find('.ui-selectonemenu-item');
        this.options = this.input.children('option');
        this.cfg.effect = this.cfg.effect||'fade';
        this.cfg.effectSpeed = this.cfg.effectSpeed||'normal';
        this.optGroupsSize = this.itemsContainer.children('li.ui-selectonemenu-item-group').length;

        var $this = this,
        selectedOption = this.options.filter(':selected'),
        highlightedItem = this.items.eq(selectedOption.index());

        //disable options
        this.options.filter(':disabled').each(function() {
            $this.items.eq($(this).index()).addClass('ui-state-disabled');
        });

        //triggers
        this.triggers = this.cfg.editable ? this.jq.find('.ui-selectonemenu-trigger') : this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');

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

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        if(!this.disabled) {
            this.bindEvents();
            this.bindConstantEvents();
            this.appendPanel();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        // see #7602
        if (PrimeFaces.env.touch) {
            this.focusInput.attr('readonly', true);
        }
        
        //for Screen Readers
        for(var i = 0; i < this.items.size(); i++) {
            this.items.eq(i).attr('id', this.id + '_' + i);
        }
        
        var highlightedItemId = highlightedItem.attr('id');
        this.focusInput.attr('aria-autocomplete', 'list')
            .attr('aria-owns', this.itemsContainer.attr('id'))
            .attr('aria-activedescendant', highlightedItemId)
            .attr('aria-describedby', highlightedItemId)
            .attr('aria-disabled', this.disabled);
        this.itemsContainer.attr('aria-activedescendant', highlightedItemId);
    },
    
    refresh: function(cfg) {
        this.panelWidthAdjusted = false;
        
        this._super(cfg);
    },

    appendPanel: function() {
        var container = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo): $(document.body);

        if(!container.is(this.jq)) {
            container.children(this.panelId).remove();
            this.panel.appendTo(container);
        }
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
            $this.selectItem($(this));
            $this.changeAriaValue($(this));
        });

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
        });

        //onchange handler for editable input
        if(this.cfg.editable) {
            this.label.change(function() {
                $this.triggerChange(true);
                $this.customInput = true;
                $this.customInputVal = $(this).val();
                $this.items.filter('.ui-state-active').removeClass('ui-state-active');
                $this.items.eq(0).addClass('ui-state-active');
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

    bindConstantEvents: function() {
        var $this = this,
        hideNS = 'mousedown.' + this.id;

        //hide overlay when outside is clicked
        $(document).off(hideNS).on(hideNS, function (e) {
            if($this.panel.is(":hidden")) {
                return;
            }

            var offset = $this.panel.offset();
            if (e.target === $this.label.get(0) ||
                e.target === $this.menuIcon.get(0) ||
                e.target === $this.menuIcon.children().get(0)) {
                return;
            }

            if (e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {

                $this.hide();

                $this.revert();
                $this.changeAriaValue($this.getActiveItem());
            }
        });

        this.resizeNS = 'resize.' + this.id;
        this.unbindResize();
        this.bindResize();
    },

    bindResize: function() {
        var _self = this;

        $(window).bind(this.resizeNS, function(e) {
            if(_self.panel.is(':visible')) {
                _self.alignPanel();
            }
        });
    },

    unbindResize: function() {
        $(window).unbind(this.resizeNS);
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
    
    triggerItemSelect: function() {
        if(this.cfg.behaviors) {
            var itemSelectBehavior = this.cfg.behaviors['itemSelect'];
            if(itemSelectBehavior) {
                itemSelectBehavior.call(this);
            }
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
            this.triggerItemSelect();
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
                    $this.highlightPrev(e);
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    $this.highlightNext(e);
                break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
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
                case keyCode.NUMPAD_ENTER:
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
                case 91: //left window or cmd:
                case 92: //right window:
                case 93: //right cmd:
                case 20: //capslock:
                break;

                default:
                    var text = $(this).val(),
                    matchedOptions = null,
                    metaKey = e.metaKey||e.ctrlKey||e.shiftKey;

                    if(!metaKey) {
                        clearTimeout($this.searchTimer);

                        matchedOptions = $this.options.filter(function() {
                            return $(this).text().toLowerCase().indexOf(text.toLowerCase()) === 0;
                        });

                        if(matchedOptions.length) {
                            var highlightItem = $this.items.eq(matchedOptions.index());
                            if($this.panel.is(':hidden')) {
                                $this.selectItem(highlightItem);
                            }
                            else {
                                $this.highlightItem(highlightItem);
                                PrimeFaces.scrollInView($this.itemsWrapper, highlightItem);
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
                case keyCode.NUMPAD_ENTER:
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
                case 91: //left window or cmd:
                case 92: //right window:
                case 93: //right cmd:
                case 20: //capslock:
                break;

                default:
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
                case keyCode.NUMPAD_ENTER:
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
        });
    },

    highlightNext: function(event) {
        var activeItem = this.getActiveItem(),
        next = this.panel.is(':hidden') ? activeItem.nextAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):first')
                                : activeItem.nextAll(':not(.ui-state-disabled,.ui-selectonemenu-item-group):visible:first');

        if(next.length === 1) {
            if(this.panel.is(':hidden')) {
                if(event.altKey)
                    this.show();
                else
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
        event.stopPropagation();
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

    show: function() {
        var $this = this;
        this.alignPanel();

        this.panel.css('z-index', ++PrimeFaces.zindex);

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
    },

    hide: function() {
        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', '');
        }

        this.panel.css('z-index', '').hide();
        this.focusInput.attr('aria-expanded', false);
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
            this.panel.css({left:'', top:''}).position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
            });
        }
    },

    setLabel: function(value) {
        var displayedLabel = this.getLabelToDisplay(value);
        
        if(this.cfg.editable) {
            if(value === '&nbsp;')
                this.label.val('');
            else
                this.label.val(displayedLabel);
        }
        else {
            if(value === '&nbsp;')
                this.label.html('&nbsp;');
            else
                this.label.text(displayedLabel);
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
            for(var i = 0; i < this.options.length; i++) {
                var option = this.options.eq(i),
                itemLabel = this.cfg.caseSensitive ? option.text() : option.text().toLowerCase(),
                item = this.items.eq(i);

                if(item.hasClass('ui-noselection-option')) {
                    item.hide();
                }
                else {
                    if(this.filterMatcher(itemLabel, filterValue))
                        item.show();
                    else
                        item.hide();
                }
            }
            
            //Toggle groups
            var groups = this.itemsContainer.children('.ui-selectonemenu-item-group');
            for(var g = 0; g < groups.length; g++) {
                var group = groups.eq(g);
                
                if(g === (groups.length - 1)) {
                    if(group.nextAll().filter(':visible').length === 0)
                        group.hide();
                    else
                        group.show();
                }
                else {
                    if(group.nextUntil('.ui-selectonemenu-item-group').filter(':visible').length === 0)
                        group.hide();
                    else
                        group.show();
                }
            }
        }

        var firstVisibleItem = this.items.filter(':visible:first');
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
        return value;
    },
    
    changeAriaValue: function (item) {
        var itemId = item.attr('id');

        this.focusInput.attr('aria-activedescendant', itemId)
                .attr('aria-describedby', itemId);
        this.itemsContainer.attr('aria-activedescendant', itemId);
    }       

});

/**
 * PrimeFaces SelectOneRadio Widget
 */
PrimeFaces.widget.SelectOneRadio = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        //custom layout
        if(this.cfg.custom) {
            this.originalInputs = this.jq.find(':radio');
            this.inputs = $('input:radio[name="' + this.id + '"].ui-radio-clone');
            this.outputs = this.inputs.parent().next('.ui-radiobutton-box');
            this.labels = $();

            //labels
            for(var i=0; i < this.outputs.length; i++) {
                this.labels = this.labels.add('label[for="' + this.outputs.eq(i).parent().attr('id') + '"]');
            }
            
            //update radio state
            for(var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i),
                itemindex = input.data('itemindex'),
                original = this.originalInputs.eq(itemindex);
        
                input.val(original.val());
                
                if(original.is(':checked')) {
                    input.prop('checked', true).parent().next().addClass('ui-state-active').children('.ui-radiobutton-icon')
                            .addClass('ui-icon-bullet').removeClass('ui-icon-blank');
                }
            }
        }
        //regular layout
        else {
            this.outputs = this.jq.find('.ui-radiobutton-box');
            this.inputs = this.jq.find(':radio');       
            this.labels = this.jq.find('label');
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');
        this.checkedRadio = this.outputs.filter('.ui-state-active');

        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    bindEvents: function() {
        var $this = this;

        this.outputs.filter(':not(.ui-state-disabled)').on('mouseover.selectOneRadio', function() {
            $(this).addClass('ui-state-hover');
        })
        .on('mouseout.selectOneRadio', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.selectOneRadio', function() {
            var radio = $(this),
            input = radio.prev().children(':radio');

            if(!radio.hasClass('ui-state-active')) {
                $this.unselect($this.checkedRadio);
                $this.select(radio);
                input.trigger('click');
                input.trigger('change');
            }
            else {
                input.trigger('click');
            }
        });

        this.labels.filter(':not(.ui-state-disabled)').on('click.selectOneRadio', function(e) {
            var target = $(PrimeFaces.escapeClientId($(this).attr('for'))),
            radio = null;

            //checks if target is input or not(custom labels)
            if(target.is(':input'))
                radio = target.parent().next();
            else
                radio = target.children('.ui-radiobutton-box'); //custom layout

            radio.trigger('click.selectOneRadio');

            e.preventDefault();
        });

        this.enabledInputs.on('focus.selectOneRadio', function() {
            var input = $(this),
            radio = input.parent().next();

            if(input.prop('checked')) {
                radio.removeClass('ui-state-active');
            }

            radio.addClass('ui-state-focus');
        })
        .on('blur.selectOneRadio', function() {
            var input = $(this),
            radio = input.parent().next();

            if(input.prop('checked')) {
                radio.addClass('ui-state-active');
            }

            radio.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneRadio', function(e) {
            var input = $(this),
            currentRadio = input.parent().next(),
            index = $this.enabledInputs.index(input),
            size = $this.enabledInputs.length,
            keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                    var prevRadioInput = (index === 0) ? $this.enabledInputs.eq((size - 1)) : $this.enabledInputs.eq(--index),
                    prevRadio = prevRadioInput.parent().next();

                    input.blur();
                    $this.unselect(currentRadio);
                    $this.select(prevRadio);
                    prevRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    var nextRadioInput = (index === (size - 1)) ? $this.enabledInputs.eq(0) : $this.enabledInputs.eq(++index),
                    nextRadio = nextRadioInput.parent().next();

                    input.blur();
                    $this.unselect(currentRadio);
                    $this.select(nextRadio);
                    nextRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                break;

                case keyCode.SPACE:
                    input.blur();
                    if(!input.prop('checked')) {
                        $this.select(currentRadio);
                    }

                    e.preventDefault();
                break;
            }
        });
    },

    unselect: function(radio) {
        radio.prev().children(':radio').prop('checked', false);
        radio.removeClass('ui-state-active').children('.ui-radiobutton-icon').removeClass('ui-icon-bullet').addClass('ui-icon-blank');
    },

    select: function(radio) {
        this.checkedRadio = radio;
        radio.addClass('ui-state-active').children('.ui-radiobutton-icon').addClass('ui-icon-bullet').removeClass('ui-icon-blank');
        radio.prev().children(':radio').prop('checked', true);
    }

});

/**
 * PrimeFaces SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanCheckbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.box = this.jq.find('.ui-chkbox-box');
        this.icon = this.box.children('.ui-chkbox-icon');
        this.itemLabel = this.jq.find('.ui-chkbox-label');
        this.disabled = this.input.is(':disabled');

        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.box.on('mouseover.selectBooleanCheckbox', function() {
                $this.box.addClass('ui-state-hover');
            })
            .on('mouseout.selectBooleanCheckbox', function() {
                $this.box.removeClass('ui-state-hover');
            })
            .on('click.selectBooleanCheckbox', function() {
                $this.toggle();
            });

            this.input.on('focus.selectBooleanCheckbox', function() {
                if($(this).prop('checked')) {
                    $this.box.removeClass('ui-state-active');
                }

                $this.box.addClass('ui-state-focus');
            })
            .on('blur.selectBooleanCheckbox', function() {
                if($(this).prop('checked')) {
                    $this.box.addClass('ui-state-active');
                }

                $this.box.removeClass('ui-state-focus');
            })
            .on('keydown.selectBooleanCheckbox', function(e) {
                var keyCode = $.ui.keyCode;
                if(e.which === keyCode.SPACE) {
                    e.preventDefault();
                }
            })
            .on('keyup.selectBooleanCheckbox', function(e) {
                var keyCode = $.ui.keyCode;
                if(e.which === keyCode.SPACE) {
                    $this.toggle();
                    $this.input.trigger('focus');

                    e.preventDefault();
                }
            });

            //toggle state on label click
            this.itemLabel.click(function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    toggle: function() {
        if(this.isChecked())
            this.uncheck();
        else
            this.check();
    },

    isChecked: function() {
        return this.input.prop('checked');
    },

    check: function() {
        if(!this.isChecked()) {
            this.input.prop('checked', true).trigger('change');
            this.input.attr('aria-checked', true);
            this.box.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
        }
    },

    uncheck: function() {
        if(this.isChecked()) {
            this.input.prop('checked', false).trigger('change');
            this.input.attr('aria-checked', false);
            this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        }
    }

});

/**
 * PrimeFaces SelectManyCheckbox Widget
 */
PrimeFaces.widget.SelectManyCheckbox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.custom) {
            this.originalInputs = this.jq.find(':checkbox');
            this.inputs = $('input:checkbox[name="' + this.id + '"].ui-chkbox-clone');
            this.outputs = this.inputs.parent().next('.ui-chkbox-box');

            //update checkbox state
            for(var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i),
                itemindex = input.data('itemindex'),
                original = this.originalInputs.eq(itemindex);

                input.val(original.val());

                if(original.is(':checked')) {
                    input.prop('checked', true).parent().next().addClass('ui-state-active').children('.ui-chkbox-icon')
                            .addClass('ui-icon-check').removeClass('ui-icon-blank');
                }
            }
        }
        else {
            this.outputs = this.jq.find('.ui-chkbox-box:not(.ui-state-disabled)');
            this.inputs = this.jq.find(':checkbox:not(:disabled)');
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');

        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    bindEvents: function() {
        this.outputs.filter(':not(.ui-state-disabled)').on('mouseover', function() {
            $(this).addClass('ui-state-hover');
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function() {
            var checkbox = $(this),
            input = checkbox.prev().children(':checkbox');

            input.trigger('click');

            if($.browser.msie && parseInt($.browser.version) < 9) {
                input.trigger('change');
            }
        });

        //delegate focus-blur-change states
        this.enabledInputs.on('focus', function() {
            var input = $(this),
            checkbox = input.parent().next();

            if(input.prop('checked')) {
                checkbox.removeClass('ui-state-active');
            }

            checkbox.addClass('ui-state-focus');
        })
        .on('blur', function() {
            var input = $(this),
            checkbox = input.parent().next();

            if(input.prop('checked')) {
                checkbox.addClass('ui-state-active');
            }

            checkbox.removeClass('ui-state-focus');
        })
        .on('change', function(e) {
            var input = $(this),
            checkbox = input.parent().next(),
            hasFocus = input.is(':focus'),
            disabled = input.is(':disabled');

            if(disabled) {
                return;
            }

            if(input.is(':checked')) {
                checkbox.children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');

                if(!hasFocus) {
                    checkbox.addClass('ui-state-active');
                }
            }
            else {
                checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            }
        });
    }

});

/**
 * PrimeFaces SelectListbox Widget
 */
PrimeFaces.widget.SelectListbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input'),
        this.listContainer = this.jq.children('.ui-selectlistbox-listcontainer');
        this.listElement = this.listContainer.children('.ui-selectlistbox-list');
        this.options = $(this.input).children('option');
        this.allItems = this.listElement.find('.ui-selectlistbox-item');
        this.items = this.allItems.filter(':not(.ui-state-disabled)');

        //scroll to selected
        var selected = this.options.filter(':selected:not(:disabled)');
        if(selected.length) {
            PrimeFaces.scrollInView(this.listContainer, this.items.eq(selected.eq(0).index()));
        }

        this.bindEvents();

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    bindEvents: function() {
        var $this = this;

        //items
        this.items.on('mouseover.selectListbox', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-highlight')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.selectListbox', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('dblclick.selectListbox', function(e) {
            $this.input.trigger('dblclick');

            PrimeFaces.clearSelection();
            e.preventDefault();
        });

        //input
        this.input.on('focus.selectListbox', function() {
            $this.jq.addClass('ui-state-focus');
        }).on('blur.selectListbox', function() {
            $this.jq.removeClass('ui-state-focus');
        });

        if(this.cfg.filter) {
            this.filterInput = this.jq.find('> div.ui-selectlistbox-filter-container > input.ui-selectlistbox-filter');
            PrimeFaces.skinInput(this.filterInput);
            this.filterInput.on('keyup.selectListbox', function(e) {
                $this.filter(this.value);
            });

            this.setupFilterMatcher();
        }
    },

    unselectAll: function() {
        this.items.removeClass('ui-state-highlight ui-state-hover');
        this.options.filter(':selected').prop('selected', false);
    },

    selectItem: function(item) {
        item.addClass('ui-state-highlight').removeClass('ui-state-hover');
        this.options.eq(item.index()).prop('selected', true);
    },

    unselectItem: function(item) {
        item.removeClass('ui-state-highlight');
        this.options.eq(item.index()).prop('selected', false);
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
        var filterValue = this.cfg.caseSensitive ? $.trim(value) : $.trim(value).toLowerCase();

        if(filterValue === '') {
            this.items.filter(':hidden').show();
        }
        else {
            for(var i = 0; i < this.options.length; i++) {
                var option = this.options.eq(i),
                itemLabel = this.cfg.caseSensitive ? option.text() : option.text().toLowerCase(),
                item = this.items.eq(i);

                if(this.filterMatcher(itemLabel, filterValue))
                    item.show();
                else
                    item.hide();
            }
        }
    }
});

/**
 * PrimeFaces SelectOneListbox Widget
 */
PrimeFaces.widget.SelectOneListbox = PrimeFaces.widget.SelectListbox.extend({

    bindEvents: function() {
        this._super();
        var $this = this;
        
        if(!this.cfg.disabled) {
            this.items.on('click.selectListbox', function(e) {
                var item = $(this),
                selectedItem = $this.items.filter('.ui-state-highlight');

                if(item.index() !== selectedItem.index()) {
                    if(selectedItem.length) {
                        $this.unselectItem(selectedItem);
                    }

                    $this.selectItem(item);
                    $this.input.trigger('change');
                }

                $this.input.trigger('click');

                PrimeFaces.clearSelection();
                e.preventDefault();
            });
        }
    }
});

/**
 * PrimeFaces SelectManyMenu Widget
 */
PrimeFaces.widget.SelectManyMenu = PrimeFaces.widget.SelectListbox.extend({

    bindEvents: function() {
        this._super();
        var $this = this;

        if(!this.cfg.disabled) {
            this.items.on('click.selectListbox', function(e) {
                //stop propagation
                if($this.checkboxClick) {
                    $this.checkboxClick = false;
                    return;
                }

                var item = $(this),
                selectedItems = $this.items.filter('.ui-state-highlight'),
                metaKey = (e.metaKey||e.ctrlKey),
                unchanged = (!metaKey && selectedItems.length === 1 && selectedItems.index() === item.index());

                if(!e.shiftKey) {
                    if(!metaKey) {
                        $this.unselectAll();
                    }

                    if(metaKey && item.hasClass('ui-state-highlight')) {
                        $this.unselectItem(item);
                    }
                    else {
                        $this.selectItem(item);
                        $this.cursorItem = item;
                    }
                }
                else {
                    //range selection
                    if($this.cursorItem) {
                        $this.unselectAll();

                        var currentItemIndex = item.index(),
                        cursorItemIndex = $this.cursorItem.index(),
                        startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex,
                        endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1);

                        for(var i = startIndex ; i < endIndex; i++) {
                            var it = $this.allItems.eq(i);

                            if(it.is(':visible') && !it.hasClass('ui-state-disabled')) {
                                $this.selectItem(it);
                            }
                        }
                    }
                    else {
                        $this.selectItem(item);
                        $this.cursorItem = item;
                    }
                }

                if(!unchanged) {
                    $this.input.trigger('change');
                }

                $this.input.trigger('click');
                PrimeFaces.clearSelection();
                e.preventDefault();
            });

            if(this.cfg.showCheckbox) {
                this.checkboxes = this.jq.find('div.ui-chkbox > div.ui-chkbox-box');

                this.checkboxes.on('mouseover.selectManyMenu', function(e) {
                    var chkbox = $(this);

                    if(!chkbox.hasClass('ui-state-active'))
                        chkbox.addClass('ui-state-hover');
                })
                .on('mouseout.selectManyMenu', function(e) {
                    $(this).removeClass('ui-state-hover');
                })
                .on('click.selectManyMenu', function(e) {
                    $this.checkboxClick = true;

                    var item = $(this).closest('.ui-selectlistbox-item');
                    if(item.hasClass('ui-state-highlight'))
                        $this.unselectItem(item);
                    else
                        $this.selectItem(item);

                    $this.input.trigger('change');
                });
            }
        }
    },

    unselectAll: function() {
        for(var i = 0; i < this.items.length; i++) {
            this.unselectItem(this.items.eq(i));
        }
    },

    selectItem: function(item) {
        this._super(item);

        if(this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },

    unselectItem: function(item) {
        this._super(item);

        if(this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },

    selectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-hover').addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
    },

    unselectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
    }
});

/**
 * PrimeFaces CommandButton Widget
 */
PrimeFaces.widget.CommandButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);
    },

    disable: function() {
        this.jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled').attr('disabled', 'disabled');
    },

    enable: function() {
        this.jq.removeClass('ui-state-disabled').removeAttr('disabled');
    }

});

/*
 * PrimeFaces Button Widget
 */
PrimeFaces.widget.Button = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);
    },

    disable: function() {
        this.jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled').attr('disabled', 'disabled');
    },

    enable: function() {
        this.jq.removeClass('ui-state-disabled').removeAttr('disabled');
    }

});

/**
 * PrimeFaces SelecyManyButton Widget
 */
PrimeFaces.widget.SelectManyButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.buttons = this.jq.children('div:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':checkbox:not(:disabled)');
        var $this = this;
                
        this.buttons.mouseover(function() {
            var button = $(this);
            if(!button.hasClass('ui-state-active'))
                button.addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            var button = $(this);

            if(button.hasClass('ui-state-active'))
                $this.unselect(button);
            else
                $this.select(button);
        });

        /* For keyboard accessibility */
        this.buttons.on('focus.selectManyButton', function(){
            var button = $(this),
            checkbox = button.children(':checkbox');
        
            if(checkbox.prop('checked')) { 
                button.removeClass('ui-state-active');
            }
            
            button.addClass('ui-state-focus');
        })
        .on('blur.selectManyButton', function(){
            var button = $(this),
            checkbox = button.children(':checkbox');
    
            if(checkbox.prop('checked')) { 
                button.addClass('ui-state-active');
            }
            
            button.removeClass('ui-state-focus');
        })
        .on('keydown.selectManyButton', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                var button = $(this),
                checkbox = button.children(':checkbox');
                
                if(checkbox.prop('checked')) {
                    $this.unselect(button);
                    button.removeClass('ui-state-hover');
                }
                else {
                    $this.select(button);
                }
                e.preventDefault();
            }  
        });
        
        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    select: function(button) {
        button.removeClass('ui-state-hover').addClass('ui-state-active')
                                .children(':checkbox').prop('checked', true).change();

    },

    unselect: function(button) {
        button.removeClass('ui-state-active').addClass('ui-state-hover')
                                .children(':checkbox').prop('checked', false).change();
    }

});

/**
 * PrimeFaces SelectOneButton Widget
 */
PrimeFaces.widget.SelectOneButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.buttons = this.jq.children('div:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':radio:not(:disabled)');
                
        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    bindEvents: function() {
        var $this = this;

        this.buttons.on('mouseover', function() {
            var button = $(this);
            if(!button.hasClass('ui-state-active')) {
                button.addClass('ui-state-hover');
            }
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function() {
            var button = $(this);

            if(!button.hasClass('ui-state-active')) {
                $this.select(button);
            }
        });
        
        /* For keyboard accessibility */
        this.buttons.on('focus.selectOneButton', function(){
            var button = $(this),
            radio = button.children(':radio');
        
            if(radio.prop('checked')) { 
                button.removeClass('ui-state-active');
            }
            
            button.addClass('ui-state-focus');
        })
        .on('blur.selectOneButton', function(){
            var button = $(this),
            radio = button.children(':radio');
    
            if(radio.prop('checked')) { 
                button.addClass('ui-state-active');
            }
            
            button.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneButton', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                var button = $(this),
                radio = button.children(':radio');
                
                if(!radio.prop('checked')) {
                    $this.select(button);
                }
                e.preventDefault();
            }  
        });
    },

    select: function(button) {
        this.buttons.filter('.ui-state-active').removeClass('ui-state-active ui-state-hover').children(':radio').prop('checked', false);

        button.addClass('ui-state-active').children(':radio').prop('checked', true).change();
    }

});

/**
 * PrimeFaces SelectBooleanButton Widget
 */
PrimeFaces.widget.SelectBooleanButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.disabled = this.input.is(':disabled');
        this.icon = this.jq.children('.ui-button-icon-left');
        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.jq.on('mouseover', function() {
                if(!$this.jq.hasClass('ui-state-active')) {
                    $this.jq.addClass('ui-state-hover');
                }
            }).on('mouseout', function() {
                $this.jq.removeClass('ui-state-hover');
            })
            .on('click', function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }
        
        this.input.on('focus', function() {            
            $this.jq.addClass('ui-state-focus');
        })
        .on('blur', function() {            
            $this.jq.removeClass('ui-state-focus');
        })
        .on('keydown', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                e.preventDefault();
            }
        })
        .on('keyup', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                $this.toggle();

                e.preventDefault();
            }
        });

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    toggle: function() {
        if(!this.disabled) {
            if(this.input.prop('checked'))
                this.uncheck();
            else
                this.check();
        }
    },

    check: function() {
        if(!this.disabled) {
            this.input.prop('checked', true);
            this.jq.addClass('ui-state-active').children('.ui-button-text').html(this.cfg.onLabel);

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.offIcon).addClass(this.cfg.onIcon);
            }

            this.input.trigger('change');
        }
    },

    uncheck: function() {
        if(!this.disabled) {
            this.input.prop('checked', false);
            this.jq.removeClass('ui-state-active').children('.ui-button-text').html(this.cfg.offLabel);

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.onIcon).addClass(this.cfg.offIcon);
            }

            this.input.trigger('change');
        }
    }

});

/**
 * PrimeFaces SelectCheckboxMenu Widget
 */
PrimeFaces.widget.SelectCheckboxMenu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.labelContainer = this.jq.find('.ui-selectcheckboxmenu-label-container');
        this.label = this.jq.find('.ui-selectcheckboxmenu-label');
        this.menuIcon = this.jq.children('.ui-selectcheckboxmenu-trigger');
        this.triggers = this.jq.find('.ui-selectcheckboxmenu-trigger, .ui-selectcheckboxmenu-label');
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.inputs = this.jq.find(':checkbox');
        this.panelId = this.id + '_panel';
        this.keyboardTarget = $(this.jqId + '_focus');
        this.tabindex = this.keyboardTarget.attr('tabindex');
        this.cfg.showHeader = (this.cfg.showHeader === undefined) ? true : this.cfg.showHeader;
        
        if(!this.disabled) {
            this.renderPanel();
            
            if(this.tabindex) {
                this.panel.find('a, input').attr('tabindex', this.tabindex);  
            }
            
            this.checkboxes = this.itemContainer.find('.ui-chkbox-box:not(.ui-state-disabled)');
            this.labels = this.itemContainer.find('label');

            this.bindEvents();
            
            //mark trigger and descandants of trigger as a trigger for a primefaces overlay
            this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
        }

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    refresh: function(cfg) {
        $(PrimeFaces.escapeClientId(this.panelId)).remove();

        this.init(cfg);
    },

    renderPanel: function() {
        this.panel = $('<div id="' + this.panelId + '" class="ui-selectcheckboxmenu-panel ui-widget ui-widget-content ui-corner-all ui-helper-hidden"></div>');

        this.appendPanel();

        if(this.cfg.panelStyle) {
            this.panel.attr('style', this.cfg.panelStyle);
        }

        if(this.cfg.panelStyleClass) {
            this.panel.addClass(this.cfg.panelStyleClass);
        }

        this.renderHeader();

        this.renderItems();

        if(this.cfg.scrollHeight) {
            this.itemContainerWrapper.height(this.cfg.scrollHeight);
        }
        else if(this.inputs.length > 10) {
            this.itemContainerWrapper.height(200)
        }
    },

    renderHeader: function() {
        this.header = $('<div class="ui-widget-header ui-corner-all ui-selectcheckboxmenu-header ui-helper-clearfix"></div>')
                        .appendTo(this.panel);

        if(!this.cfg.showHeader) {
            this.header.removeClass('ui-helper-clearfix').addClass('ui-helper-hidden');
        }
        //toggler
        this.toggler = $('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox" readonly="readonly"/></div><div class="ui-chkbox-box ui-widget ui-corner-all ui-state-default"><span class="ui-chkbox-icon ui-icon ui-icon-blank"></span></div></div>')
                            .appendTo(this.header);
        this.togglerBox = this.toggler.children('.ui-chkbox-box');
        if(this.inputs.filter(':not(:checked)').length === 0) {
            this.check(this.togglerBox);
        }

        //filter
        if(this.cfg.filter) {
            this.filterInputWrapper = $('<div class="ui-selectcheckboxmenu-filter-container"></div>').appendTo(this.header);
            this.filterInput = $('<input type="text" aria-multiline="false" aria-readonly="false" aria-disabled="false" role="textbox" class="ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all">')
                                .appendTo(this.filterInputWrapper);

            this.filterInputWrapper.append("<span class='ui-icon ui-icon-search'></span>");
        }

        //closer
        this.closer = $('<a class="ui-selectcheckboxmenu-close ui-corner-all" href="#"><span class="ui-icon ui-icon-circle-close"></span></a>')
                    .appendTo(this.header);

    },

    renderItems: function() {
        var $this = this;

        this.itemContainerWrapper = $('<div class="ui-selectcheckboxmenu-items-wrapper"><ul class="ui-selectcheckboxmenu-items ui-selectcheckboxmenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset"></ul></div>')
                .appendTo(this.panel);

        this.itemContainer = this.itemContainerWrapper.children('ul.ui-selectcheckboxmenu-items');

        this.inputs.each(function() {
            var input = $(this),
            label = input.next(),
            disabled = input.is(':disabled'),
            checked = input.is(':checked'),
            title = input.attr('title'),
            boxClass = 'ui-chkbox-box ui-widget ui-corner-all ui-state-default',
            itemClass = 'ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all',
            escaped = input.data('escaped');

            if(disabled) {
                boxClass += " ui-state-disabled";
            }

            if(checked) {
                boxClass += " ui-state-active";
            }

            var iconClass = checked ? 'ui-chkbox-icon ui-icon ui-icon-check' : 'ui-chkbox-icon ui-icon ui-icon-blank',
            itemClass = checked ? itemClass + ' ui-selectcheckboxmenu-checked' : itemClass + ' ui-selectcheckboxmenu-unchecked';

            var item = $('<li class="' + itemClass + '"></li>');
            item.append('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox" readonly="readonly"></input></div>' +
                    '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div></div>');
            
            var itemLabel = $('<label></label>');
            if(escaped)
                itemLabel.text(label.text());
            else
                itemLabel.html(label.html());
            
            itemLabel.appendTo(item);
        
            if(title) {
                item.attr('title', title);
            }

            item.find('> .ui-chkbox > .ui-helper-hidden-accessible > input').prop('checked', checked);

            $this.itemContainer.append(item);
        });
    },

    appendPanel: function() {
        if(this.cfg.appendTo) {
            this.panel.appendTo(PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo));
        }
        else {
            this.panel.appendTo(document.body);
        }
    },

    bindEvents: function() {
        var $this = this,
        hideNS = 'mousedown.' + this.id,
        resizeNS = 'resize.' + this.id;

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
            });
        }

        //Closer
        this.closer.on('mouseenter.selectCheckboxMenu', function(){
            $(this).addClass('ui-state-hover');
        }).on('mouseleave.selectCheckboxMenu', function() {
            $(this).removeClass('ui-state-hover');
        }).on('click.selectCheckboxMenu', function(e) {
            $this.hide(true);

            e.preventDefault();
        });

        //Labels
        this.labels.on('click.selectCheckboxMenu', function() {
            var checkbox = $(this).prev().children('.ui-chkbox-box');
            $this.toggleItem(checkbox);
            checkbox.removeClass('ui-state-hover');
            PrimeFaces.clearSelection();
        });

        //Events to show/hide the panel
        this.triggers.on('mouseover.selectCheckboxMenu', function() {
            if(!$this.disabled&&!$this.triggers.hasClass('ui-state-focus')) {
                $this.triggers.addClass('ui-state-hover');
            }
        }).on('mouseout.selectCheckboxMenu', function() {
            if(!$this.disabled) {
                $this.triggers.removeClass('ui-state-hover');
            }
        }).on('mousedown.selectCheckboxMenu', function(e) {
            if(!$this.disabled) {
                if($this.panel.is(":hidden")) {
                    $this.show();
                }
                else {
                    $this.hide(true);
                }
            }
        }).on('click.selectCheckboxMenu', function(e) {
            $this.keyboardTarget.trigger('focus');
            e.preventDefault();
        });
        
        this.bindKeyEvents();

        //hide overlay when outside is clicked
        $(document.body).off(hideNS).on(hideNS, function (e) {
            if($this.panel.is(':hidden')) {
                return;
            }

            //do nothing on trigger mousedown
            var target = $(e.target);
            if($this.triggers.is(target)||$this.triggers.has(target).length > 0) {
                return;
            }

            //hide the panel and remove focus from label
            var offset = $this.panel.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {

                $this.hide(true);
            }
        });

        //Realign overlay on resize
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.panel.is(':visible')) {
                $this.alignPanel();
            }
        });

        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
    },
    
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
    
            switch(key) {
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    if($this.panel.is(":hidden"))
                        $this.show();
                    else
                        $this.hide(true);

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
                
            };
        });
        
        this.closer.on('focus.selectCheckboxMenu', function(e) {
            $this.closer.addClass('ui-state-focus');
        })
        .on('blur.selectCheckboxMenu', function(e) {
            $this.closer.removeClass('ui-state-focus');
        })
        .on('keydown.selectCheckboxMenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if(key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                $this.hide(true);
                e.preventDefault();
            }
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
                });
    },

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

    filter: function(value) {
        var filterValue = this.cfg.caseSensitive ? $.trim(value) : $.trim(value).toLowerCase();

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

        if(this.cfg.scrollHeight) {
            if(this.itemContainer.height() < this.cfg.initialHeight) {
                this.itemContainerWrapper.css('height', 'auto');
            }
            else {
                this.itemContainerWrapper.height(this.cfg.initialHeight);
            }
        }

        this.updateToggler();
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

    checkAll: function() {
        var visibleItems = this.itemContainer.children('li.ui-selectcheckboxmenu-item').filter(':visible'),
        $this = this;

        visibleItems.each(function() {
            $this.inputs.eq($(this).index()).prop('checked', true);
            $this.check($(this).children('.ui-chkbox').children('.ui-chkbox-box'));
        });

        this.check(this.togglerBox);
        
        if(!this.togglerBox.hasClass('ui-state-disabled')) {
            this.togglerBox.prev().children('input').trigger('focus.selectCheckboxMenu');
            this.togglerBox.addClass('ui-state-active');    
        }
        
        this.fireToggleSelectEvent(true);
    },

    uncheckAll: function() {
        var visibleItems = this.itemContainer.children('li.ui-selectcheckboxmenu-item').filter(':visible'),
        $this = this;

        visibleItems.each(function() {
            $this.inputs.eq($(this).index()).prop('checked', false);
            $this.uncheck($(this).children('.ui-chkbox').children('.ui-chkbox-box'));
        });

        this.uncheck(this.togglerBox);
        
        if(!this.togglerBox.hasClass('ui-state-disabled')) {
            this.togglerBox.prev().children('input').trigger('focus.selectCheckboxMenu');
        }

        this.fireToggleSelectEvent(false);
    },

    fireToggleSelectEvent: function(checked) {
        if(this.cfg.behaviors) {
            var toggleSelectBehavior = this.cfg.behaviors['toggleSelect'];

            if(toggleSelectBehavior) {
                var ext = {
                    params: [{name: this.id + '_checked', value: checked}]
                }

                toggleSelectBehavior.call(this, ext);
            }
        }
    },

    check: function(checkbox, updateInput) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var checkedInput = checkbox.prev().children('input');
            
            checkedInput.prop('checked', true);
            if(updateInput) {
                checkedInput.trigger('focus.selectCheckboxMenu');
            }

            checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
            checkbox.closest('li.ui-selectcheckboxmenu-item').removeClass('ui-selectcheckboxmenu-unchecked').addClass('ui-selectcheckboxmenu-checked');

            if(updateInput) {
                var input = this.inputs.eq(checkbox.parents('li:first').index());
                input.prop('checked', true).change();

                this.updateToggler();
            }
        }
    },

    uncheck: function(checkbox, updateInput) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var uncheckedInput = checkbox.prev().children('input');
            checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            checkbox.closest('li.ui-selectcheckboxmenu-item').addClass('ui-selectcheckboxmenu-unchecked').removeClass('ui-selectcheckboxmenu-checked');
            uncheckedInput.prop('checked', false);

            if(updateInput) {
                var input = this.inputs.eq(checkbox.parents('li:first').index());
                input.prop('checked', false).change();
                uncheckedInput.trigger('focus.selectCheckboxMenu');
                this.updateToggler();
            }
        }
    },

    show: function() {
        this.alignPanel();

        this.panel.show();

        this.postShow();
    },

    hide: function(animate) {
        var $this = this;

        if(animate) {
            this.panel.fadeOut('fast', function() {
                $this.postHide();
            });
        }

        else {
            this.panel.hide();
            this.postHide();
        }
    },

    postShow: function() {
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },

    postHide: function() {
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },

    alignPanel: function() {
        var fixedPosition = this.panel.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null,
        panelStyle = this.panel.attr('style');

        this.panel.css({
                'left':'',
                'top':'',
                'z-index': ++PrimeFaces.zindex
        });

        if(this.panel.parent().attr('id') === this.id) {
            this.panel.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.panel.position({
                                my: 'left top'
                                ,at: 'left bottom'
                                ,of: this.jq
                                ,offset : positionOffset
                            });
        }

        if(!this.widthAligned && (this.panel.width() < this.jq.width()) && (!panelStyle||panelStyle.toLowerCase().indexOf('width') === -1)) {
            this.panel.width(this.jq.width());
            this.widthAligned = true;
        }
    },

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

    updateToggler: function() {
        var visibleItems = this.itemContainer.children('li.ui-selectcheckboxmenu-item:visible');
        
        if(visibleItems.length && visibleItems.filter('.ui-selectcheckboxmenu-unchecked').length === 0) {
            this.check(this.togglerBox);
        }
        else {
            this.uncheck(this.togglerBox);
        }
    },
    
    bindCheckboxKeyEvents: function(items) {
        var $this = this;
        
        items.on('focus.selectCheckboxMenu', function(e) {
            var input = $(this),
            box = input.parent().next();

            if(input.prop('checked')) {
                box.removeClass('ui-state-active');
            }

            box.addClass('ui-state-focus');
            
            PrimeFaces.scrollInView($this.itemContainerWrapper, box);
        })
        .on('blur.selectCheckboxMenu', function(e) {
            var input = $(this),
            box = input.parent().next();

            if(input.prop('checked')) {
                box.addClass('ui-state-active');
            }

            box.removeClass('ui-state-focus');
        })
        .on('keydown.selectCheckboxMenu', function(e) {
            if(e.which === $.ui.keyCode.SPACE) {
                e.preventDefault();
            }
        });
    }

});

/**
 * PrimeFaces InputMask Widget
 */
PrimeFaces.widget.InputMask = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.mask) {
            this.jq.mask(this.cfg.mask, this.cfg);
        }

        //Visuals
        PrimeFaces.skinInput(this.jq);
    },

    setValue: function(value) {
        this.jq.val(value);
        this.jq.unmask().mask(this.cfg.mask, this.cfg);
    },

    getValue: function() {
        return this.jq.val();
    }

});

/**
 * PrimeFaces Password
 */
PrimeFaces.widget.Password = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        if(!this.jq.is(':disabled')) {
            if(this.cfg.feedback) {
                this.setupFeedback();
            }

            PrimeFaces.skinInput(this.jq);
        }
    },

    setupFeedback: function() {
        var _self = this;

        //remove previous panel if any
        var oldPanel = $(this.jqId + '_panel');
        if(oldPanel.length == 1) {
            oldPanel.remove();
        }

        //config
        this.cfg.promptLabel = this.cfg.promptLabel||'Please enter a password';
        this.cfg.weakLabel = this.cfg.weakLabel||'Weak';
        this.cfg.goodLabel = this.cfg.goodLabel||'Medium';
        this.cfg.strongLabel = this.cfg.strongLabel||'Strong';

        var panelStyle = this.cfg.inline ? 'ui-password-panel-inline' : 'ui-password-panel-overlay';

        //create panel element
        var panelMarkup = '<div id="' + this.id + '_panel" class="ui-password-panel ui-widget ui-state-highlight ui-corner-all ui-helper-hidden ' + panelStyle + '">';
        panelMarkup += '<div class="ui-password-meter" style="background-position:0pt 0pt">&nbsp;</div>';
        panelMarkup += '<div class="ui-password-info">' + this.cfg.promptLabel + '</div>';
        panelMarkup += '</div>';

        this.panel = $(panelMarkup).insertAfter(this.jq);
        this.meter = this.panel.children('div.ui-password-meter');
        this.infoText = this.panel.children('div.ui-password-info');

        if(!this.cfg.inline) {
            this.panel.addClass('ui-shadow');
        }

        //events
        this.jq.focus(function() {
            _self.show();
        })
        .blur(function() {
            _self.hide();
        })
        .keyup(function() {
            var value = _self.jq.val(),
            label = null,
            meterPos = null;

            if(value.length == 0) {
                label = _self.cfg.promptLabel;
                meterPos = '0px 0px';
            }
            else {
                var score = _self.testStrength(_self.jq.val());

                if(score < 30) {
                    label = _self.cfg.weakLabel;
                    meterPos = '0px -10px';
                }
                else if(score >= 30 && score < 80) {
                    label = _self.cfg.goodLabel;
                    meterPos = '0px -20px';
                }
                else if(score >= 80) {
                    label = _self.cfg.strongLabel;
                    meterPos = '0px -30px';
                }
            }

            //update meter and info text
            _self.meter.css('background-position', meterPos);
            _self.infoText.text(label);
        });

        //overlay setting
        if(!this.cfg.inline) {
            this.panel.appendTo('body');

            //Hide overlay on resize
            var resizeNS = 'resize.' + this.id;
            $(window).unbind(resizeNS).bind(resizeNS, function() {
                if(_self.panel.is(':visible')) {
                    _self.align();
                }
            });
        }
    },

    testStrength: function(str) {
        var grade = 0,
        val = 0,
        _self = this;

        val = str.match('[0-9]');
        grade += _self.normalize(val ? val.length : 1/4, 1) * 25;

        val = str.match('[a-zA-Z]');
        grade += _self.normalize(val ? val.length : 1/2, 3) * 10;

        val = str.match('[!@#$%^&*?_~.,;=]');
        grade += _self.normalize(val ? val.length : 1/6, 1) * 35;

        val = str.match('[A-Z]');
        grade += _self.normalize(val ? val.length : 1/6, 1) * 30;

        grade *= str.length / 8;

        return grade > 100 ? 100 : grade;
    },

    normalize: function(x, y) {
        var diff = x - y;

        if(diff <= 0) {
            return x / y;
        }
        else {
            return 1 + 0.5 * (x / (x + y/4));
        }
    },

    align: function() {
        this.panel.css({
            left:'',
            top:'',
            'z-index': ++PrimeFaces.zindex
        })
        .position({
            my: 'left top',
            at: 'right top',
            of: this.jq
        });
    },

    show: function() {
        if(!this.cfg.inline) {
            this.align();

            this.panel.fadeIn();
        }
        else {
            this.panel.slideDown();
        }
    },

    hide: function() {
        if(this.cfg.inline)
            this.panel.slideUp();
        else
            this.panel.fadeOut();
    }

});

/**
 * PrimeFaces DefaultCommand Widget
 */
PrimeFaces.widget.DefaultCommand = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.scope = this.cfg.scope ? $(PrimeFaces.escapeClientId(this.cfg.scope)) : null;
        var $this = this;

        // container support - e.g. splitButton
        if (this.jqTarget.is(':not(:button):not(:input):not(a)')) {
        	this.jqTarget = this.jqTarget.find('button,a').filter(':visible').first();
        }

        //attach keypress listener to parent form
        this.jqTarget.closest('form').off('keydown.' + this.id).on('keydown.' + this.id, function(e) {
           var keyCode = $.ui.keyCode;
           if(e.which == keyCode.ENTER || e.which == keyCode.NUMPAD_ENTER) {
                //do not proceed if event target is not in this scope or target is a textarea,button or link
                if(($this.scope && $this.scope.find(e.target).length == 0)||$(e.target).is('textarea,button,input[type="submit"],a')) {
                    return true;
                }

               $this.jqTarget.click();
               e.preventDefault();
           }
        });

        this.removeScriptElement(this.id);
    }
});

/*
 * PrimeFaces SplitButton Widget
 */
PrimeFaces.widget.SplitButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.button = $(this.jqId + '_button');
        this.menuButton = $(this.jqId + '_menuButton');
        this.menuId = this.jqId + "_menu";
        this.menu = $(this.menuId);
        this.menuitems = this.menu.find('.ui-menuitem:not(.ui-state-disabled)');
        this.cfg.disabled = this.button.is(':disabled');

        if(!this.cfg.disabled) {
            this.bindEvents();
            this.appendPanel();
        }

        //pfs metadata
        this.button.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.menuButton.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    refresh: function(cfg) {
        this.menu.remove();

        this.init(cfg);
    },
    
    destroy: function() {
        this._super();
        
        this.menu.remove();
    },

    bindEvents: function() {
        var $this = this;

        PrimeFaces.skinButton(this.button).skinButton(this.menuButton);

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //toggle menu
        this.menuButton.click(function() {
            if($this.menu.is(':hidden')) {
                $this.show();
            }
            else {
                $this.hide();
            }
        });

        //menuitem visuals
        this.menuitems.mouseover(function(e) {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitem.addClass('ui-state-hover');
            }
        }).mouseout(function(e) {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            $this.hide();
        });

        var hideNS = 'mousedown.' + this.id;
        $(document.body).off(hideNS).on(hideNS, function (e) {
            //do nothing if hidden already
            if($this.menu.is(":hidden")) {
                return;
            }

            //do nothing if mouse is on button
            var target = $(e.target);
            if(target.is($this.button)||$this.button.has(target).length > 0) {
                return;
            }

            //hide overlay if mouse is outside of overlay except button
            var offset = $this.menu.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.menu.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.menu.height()) {

                $this.button.removeClass('ui-state-focus ui-state-hover');
                $this.hide();
            }
        });

        var resizeNS = 'resize.' + this.id;
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.menu.is(':visible')) {
                $this.alignPanel();
            }
        });
    },

    appendPanel: function() {
        var container = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo): $(document.body);

        if(!container.is(this.jq)) {
            container.children(this.menuId).remove();
            this.menu.appendTo(container);
        }
    },

    show: function() {
        this.alignPanel();

        this.menuButton.focus();

        this.menu.show();
    },

    hide: function() {
        this.menuButton.removeClass('ui-state-focus');

        this.menu.fadeOut('fast');
    },

    alignPanel: function() {
        this.menu.css({left:'', top:'','z-index': ++PrimeFaces.zindex});

        if(this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.menu.position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.button
            });
        }
    }

});

/*
 * PrimeFaces ThemeSwitcher Widget
 */
PrimeFaces.widget.ThemeSwitcher = PrimeFaces.widget.SelectOneMenu.extend({

    init: function(cfg) {
        this._super(cfg);

        var $this = this;
        this.input.on('change', function() {
            PrimeFaces.changeTheme($this.getSelectedValue());
        });
    }
});

/*
 * PrimeFaces MultiSelectListbox Widget
 */
PrimeFaces.widget.MultiSelectListbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
       this._super(cfg);

       this.root = this.jq.children('div.ui-multiselectlistbox-listcontainer');
       this.items = this.jq.find('li.ui-multiselectlistbox-item');
       this.input = $(this.jqId + '_input');
       this.cfg.disabled = this.jq.hasClass('ui-state-disabled');

       if(!this.cfg.disabled) {
           this.bindEvents();
       }

       var value = this.input.val();
       if(value !== '') {
           this.preselect(value);
       }
    },

    bindEvents: function() {
       var $this = this;

       this.items.on('mouseover.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight'))
               $(this).addClass('ui-state-hover');
       })
       .on('mouseout.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight'))
               $(this).removeClass('ui-state-hover');
       })
       .on('click.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight'))
               $this.showOptionGroup(item);
       })
    },

    unbindEvents: function() {
       this.items.off('mouseover.multiSelectListbox mouseout.multiSelectListbox click.multiSelectListbox');
    },

    showOptionGroup: function(item) {
       item.addClass('ui-state-highlight').removeClass('ui-state-hover').siblings().filter('.ui-state-highlight').removeClass('ui-state-highlight');
       item.closest('.ui-multiselectlistbox-listcontainer').nextAll().remove();
       this.input.val(item.attr('data-value'));

       var childItemsContainer = item.children('ul');

       if(childItemsContainer.length) {
           var groupContainer = $('<div class="ui-multiselectlistbox-listcontainer" style="display:none"></div>');
           childItemsContainer.clone(true).appendTo(groupContainer).addClass('ui-multiselectlistbox-list ui-inputfield ui-widget-content').removeClass('ui-helper-hidden');
           
           if(this.cfg.showHeaders) {
               groupContainer.prepend('<div class="ui-multiselectlistbox-header ui-widget-header ui-corner-top">' + item.children('span').text() + '</div>')
                       .children('.ui-multiselectlistbox-list').addClass('ui-corner-bottom');
           } else {
               groupContainer.children().addClass('ui-corner-all');
           } 
            
           this.jq.append(groupContainer);

           if(this.cfg.effect)
               groupContainer.show(this.cfg.effect);
           else
               groupContainer.show();
       }
    },

    enable: function() {
       if(this.cfg.disabled) {
           this.cfg.disabled = false;
           this.jq.removeClass('ui-state-disabled');
           this.bindEvents();
       }

    },

    disable: function() {
       if(!this.cfg.disabled) {
           this.cfg.disabled = true;
           this.jq.addClass('ui-state-disabled');
           this.unbindEvents();
           this.root.nextAll().remove();
       }
    },

    preselect: function(value) {
        var $this = this,
        item = this.items.filter('[data-value="' + value + '"]');

        if(item.length === 0) {
            return;
        }

        var ancestors = item.parentsUntil('.ui-multiselectlistbox-list'),
        selectedIndexMap = [];

        for(var i = (ancestors.length - 1); i >= 0; i--) {
            var ancestor = ancestors.eq(i);

            if(ancestor.is('li')) {
                selectedIndexMap.push(ancestor.index());
            }
            else if(ancestor.is('ul')) {
                var groupContainer = $('<div class="ui-multiselectlistbox-listcontainer ui-inputfield ui-widget-content ui-corner-all" style="display:none"></div>');
                ancestor.clone(true).appendTo(groupContainer).addClass('ui-multiselectlistbox-list').removeClass('ui-helper-hidden');

                $this.jq.append(groupContainer);
            }
        }

        //highlight item
        var lists = this.jq.children('div.ui-multiselectlistbox-listcontainer'),
        clonedItem = lists.find(' > ul.ui-multiselectlistbox-list > li.ui-multiselectlistbox-item').filter('[data-value="' + value + '"]');
        clonedItem.addClass('ui-state-highlight');

        //highlight ancestors
        for(var i = 0; i < selectedIndexMap.length; i++) {
            lists.eq(i).find('> .ui-multiselectlistbox-list > li.ui-multiselectlistbox-item').eq(selectedIndexMap[i]).addClass('ui-state-highlight');
        }

        $this.jq.children('div.ui-multiselectlistbox-listcontainer:hidden').show();
    }
});