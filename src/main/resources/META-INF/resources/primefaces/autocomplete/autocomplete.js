/**
 * PrimeFaces AutoComplete Widget
 */
PrimeFaces.widget.AutoComplete = PrimeFaces.widget.BaseWidget.extend({

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
        this.cfg.cache = this.cfg.cache||false;
        this.cfg.resultsMessage = this.cfg.resultsMessage||' results are available, use up and down arrow keys to navigate';
        this.cfg.ariaEmptyMessage = this.cfg.emptyMessage||'No search results are available.';
        this.cfg.dropdownMode = this.cfg.dropdownMode||'blank';
        this.cfg.autoHighlight = (this.cfg.autoHighlight === undefined) ? true : this.cfg.autoHighlight;
        this.cfg.myPos = this.cfg.myPos||'left top';
        this.cfg.atPos = this.cfg.atPos||'left bottom';
        this.cfg.active = (this.cfg.active === false) ? false : true;
        this.cfg.dynamic = this.cfg.dynamic === true ? true : false;
        this.cfg.autoSelection = this.cfg.autoSelection === false ? false : true;
        this.cfg.escape = this.cfg.escape === false ? false : true;
        this.suppressInput = true;
        this.touchToDropdownButton = false;
        this.isTabPressed = false;
        this.isDynamicLoaded = false;

        if(this.cfg.cache) {
            this.initCache();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        
        this.placeholder = this.input.attr('placeholder');

        if(this.cfg.multiple) {
            this.setupMultipleMode();

            this.multiItemContainer.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

            if(this.cfg.selectLimit >= 0 && this.multiItemContainer.children('li.ui-autocomplete-token').length === this.cfg.selectLimit) {
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
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
        }

        //force selection
        if(this.cfg.forceSelection) {
            this.setupForceSelection();
        }

        //Panel management
        if(this.panel.length) {
            this.appendPanel();
        }

        //itemtip
        if(this.cfg.itemtip) {
            this.itemtip = $('<div id="' + this.id + '_itemtip" class="ui-autocomplete-itemtip ui-state-highlight ui-widget ui-corner-all ui-shadow"></div>').appendTo(document.body);
            this.cfg.itemtipMyPosition = this.cfg.itemtipMyPosition||'left top';
            this.cfg.itemtipAtPosition = this.cfg.itemtipAtPosition||'right bottom';
            this.cfg.checkForScrollbar = (this.cfg.itemtipAtPosition.indexOf('right') !== -1);
        }

        //aria
        this.input.attr('aria-autocomplete', 'list');
        this.jq.attr('role', 'application');
        this.jq.append('<span role="status" aria-live="polite" class="ui-autocomplete-status ui-helper-hidden-accessible"></span>');
        this.status = this.jq.children('.ui-autocomplete-status');
    },

    //@override
    refresh: function(cfg) {
        this._super(cfg);
    },

    appendPanel: function() {
        PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
    },

    initCache: function() {
        this.cache = {};
        var $this=this;

        this.cacheTimeout = setInterval(function(){
            $this.clearCache();
        }, this.cfg.cacheTimeout);
    },

    clearCache: function() {
        this.cache = {};
    },

    /**
     * Binds events for multiple selection mode
     */
    setupMultipleMode: function() {
        var $this = this;
        this.multiItemContainer = this.jq.children('ul');
        this.inputContainer = this.multiItemContainer.children('.ui-autocomplete-input-token');

        this.multiItemContainer.hover(function() {
                $(this).addClass('ui-state-hover');
            },
            function() {
                $(this).removeClass('ui-state-hover');
            }
        ).click(function() {
            $this.input.focus();
        });

        //delegate events to container
        this.input.focus(function() {
            $this.multiItemContainer.addClass('ui-state-focus');
        }).blur(function(e) {
            $this.multiItemContainer.removeClass('ui-state-focus');
        });

        var closeSelector = '> li.ui-autocomplete-token > .ui-autocomplete-token-icon';
        this.multiItemContainer.off('click', closeSelector).on('click', closeSelector, null, function(event) {
            if($this.multiItemContainer.children('li.ui-autocomplete-token').length === $this.cfg.selectLimit) {
                $this.input.css('display', 'inline');
                $this.enableDropdown();
            }
            $this.removeItem(event, $(this).parent());
        });
    },

    bindStaticEvents: function() {
        var $this = this;

        this.bindKeyEvents();

        this.bindDropdownEvents();

        if(PrimeFaces.env.browser.mobile) {
            this.dropdown.bind('touchstart', function() {
                $this.touchToDropdownButton = true;
            });
        }

        PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.panel,
            function() { return $this.itemtip; },
            function(e, eventTarget) {
                if(!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide();
                }
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.panel, function() {
             $this.alignPanel();
        });
    },

    bindDropdownEvents: function() {
        var $this = this;

        PrimeFaces.skinButton(this.dropdown);

        this.dropdown.mouseup(function() {
            if($this.active) {
                $this.searchWithDropdown();
                $this.input.focus();
            }
        }).keyup(function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                $this.searchWithDropdown();
                $this.input.focus();
                e.preventDefault();
                e.stopPropagation();
            }
        });
    },

    disableDropdown: function() {
        if(this.dropdown.length) {
            this.dropdown.off().prop('disabled', true).addClass('ui-state-disabled');
        }
    },

    enableDropdown: function() {
        if(this.dropdown.length && this.dropdown.prop('disabled')) {
            this.bindDropdownEvents();
            this.dropdown.prop('disabled', false).removeClass('ui-state-disabled');
        }
    },

    bindKeyEvents: function() {
        var $this = this;

        if(this.cfg.queryEvent !== 'enter') {
            this.input.on('input propertychange', function(e) {
                $this.processKeyEvent(e);
            });
        }

        this.input.on('keyup.autoComplete', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(PrimeFaces.env.isIE(9) && (key === keyCode.BACKSPACE || key === keyCode.DELETE)) {
                $this.processKeyEvent(e);
            }

            if($this.cfg.queryEvent === 'enter' && (key === keyCode.ENTER)) {
                if($this.itemSelectedWithEnter)
                    $this.itemSelectedWithEnter = false;
                else
                    $this.search($this.input.val());
            }

            if($this.panel.is(':visible')) {
                if(key === keyCode.ESCAPE) {
                    $this.hide();
                }
                else if(key === keyCode.UP || key === keyCode.DOWN) {
                    var highlightedItem = $this.items.filter('.ui-state-highlight');
                    if(highlightedItem.length) {
                        $this.displayAriaStatus(highlightedItem.data('item-label'));
                    }
                }
            }

            $this.checkMatchedItem = true;
            $this.isTabPressed = false;

        }).on('keydown.autoComplete', function(e) {
            var keyCode = $.ui.keyCode;

            $this.suppressInput = false;
            if($this.panel.is(':visible')) {
                var highlightedItem = $this.items.filter('.ui-state-highlight');

                switch(e.which) {
                    case keyCode.UP:
                        var prev = highlightedItem.length == 0 ? $this.items.eq(0) : highlightedItem.prevAll('.ui-autocomplete-item:first');

                        if(prev.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            prev.addClass('ui-state-highlight');

                            if($this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView($this.panel, prev);
                            }

                            if($this.cfg.itemtip) {
                                $this.showItemtip(prev);
                            }
                        }

                        e.preventDefault();
                        break;

                    case keyCode.DOWN:
                        var next = highlightedItem.length == 0 ? $this.items.eq(0) : highlightedItem.nextAll('.ui-autocomplete-item:first');

                        if(next.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            next.addClass('ui-state-highlight');

                            if($this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView($this.panel, next);
                            }

                            if($this.cfg.itemtip) {
                                $this.showItemtip(next);
                            }
                        }

                        e.preventDefault();
                        break;

                    case keyCode.ENTER:
                        if ($this.timeout) {
                            $this.deleteTimeout();
                        }

                        if (highlightedItem.length > 0) {
                            $(this).trigger('change');
                            highlightedItem.click();
                            $this.itemSelectedWithEnter = true;
                        }

                        e.preventDefault();
                        e.stopPropagation();

                        break;

                    case 18: //keyCode.ALT:
                    case 224:
                        break;

                    case keyCode.TAB:
                        if(highlightedItem.length && $this.cfg.autoSelection) {
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
                switch(e.which) {
                    case keyCode.TAB:
                        if ($this.timeout) {
                            $this.deleteTimeout();
                        }
                        $this.isTabPressed = true;
                    break;

                    case keyCode.ENTER:
                        if($this.cfg.queryEvent === 'enter' || ($this.timeout > 0) || $this.querying) {
                            e.preventDefault();
                        }

                        if($this.cfg.queryEvent !== 'enter') {
                            $this.isValid($(this).val());
                        }
                    break;

                    case keyCode.BACKSPACE:
                        if ($this.cfg.multiple && !$this.input.val().length) {
                            $this.removeItem(e, $(this).parent().prev());

                            e.preventDefault();
                        }
                    break;
                };
            }

        }).on('paste.autoComplete', function() {
			$this.suppressInput = false;
            $this.checkMatchedItem = true;
		});
    },

    bindDynamicEvents: function() {
        var $this = this;

        //visuals and click handler for items
        this.items.on('mouseover', function() {
            var item = $(this);

            if(!item.hasClass('ui-state-highlight')) {
                $this.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');
                item.addClass('ui-state-highlight');

                if($this.cfg.itemtip) {
                    $this.showItemtip(item);
                }
            }
        })
        .on('click', function(event) {
            var item = $(this),
            isMoreText = item.hasClass('ui-autocomplete-moretext');

            if(isMoreText) {
                $this.input.focus();
                $this.invokeMoreTextBehavior();
            }
            else {
                var itemValue = item.attr('data-item-value');

                if($this.cfg.multiple) {
                    var found = false;
                    if($this.cfg.unique) {
                        found = $this.multiItemContainer.children("li[data-token-value='" + $.escapeSelector(itemValue) + "']").length != 0;
                    }

                    if(!found) {
                        var itemStyleClass = item.attr('data-item-class');
                        var itemDisplayMarkup = '<li data-token-value="' + PrimeFaces.escapeHTML(itemValue);
                        itemDisplayMarkup += '"class="ui-autocomplete-token ui-state-active ui-corner-all ui-helper-hidden';
                        itemDisplayMarkup += (itemStyleClass === '' ? '' : ' '+itemStyleClass) + '">';
                        itemDisplayMarkup += '<span class="ui-autocomplete-token-icon ui-icon ui-icon-close" />';
                        itemDisplayMarkup += '<span class="ui-autocomplete-token-label">' + PrimeFaces.escapeHTML(item.attr('data-item-label')) + '</span></li>';

                        $this.inputContainer.before(itemDisplayMarkup);
                        $this.multiItemContainer.children('.ui-helper-hidden').fadeIn();
                        $this.input.val('');
                        $this.input.removeAttr('placeholder');

                        $this.hinput.append('<option value="' + PrimeFaces.escapeHTML(itemValue) + '" selected="selected"></option>');
                        if($this.multiItemContainer.children('li.ui-autocomplete-token').length >= $this.cfg.selectLimit) {
                            $this.input.css('display', 'none').blur();
                            $this.disableDropdown();
                        }

                        $this.invokeItemSelectBehavior(event, itemValue);
                    }
                }
                else {
                    $this.input.val(item.attr('data-item-label'));

                    this.currentText = $this.input.val();
                    this.previousText = $this.input.val();

                    if($this.cfg.pojo) {
                        $this.hinput.val(itemValue);
                    }

                    if(PrimeFaces.env.isLtIE(10)) {
                        var length = $this.input.val().length;
                        $this.input.setSelection(length,length);
                    }

                    $this.invokeItemSelectBehavior(event, itemValue);
                }

                if(!$this.isTabPressed) {
                    $this.input.focus();
                }
            }

            $this.hide();
        })
        .on('mousedown', function() {
            $this.checkMatchedItem = false;
        });

        if(PrimeFaces.env.browser.mobile) {
            this.items.bind('touchstart', function() {
                if(!$this.touchToDropdownButton) {
                    $this.itemClick = true;
                }
            });
        }
    },

    processKeyEvent: function(e) {
        var $this = this;

        if($this.suppressInput) {
            e.preventDefault();
            return;
        }

        // for touch event on mobile
        if(PrimeFaces.env.browser.mobile) {
            $this.touchToDropdownButton = false;
            if($this.itemClick) {
                $this.itemClick = false;
                return;
            }
        }

        var value = $this.input.val();

        if($this.cfg.pojo && !$this.cfg.multiple) {
            $this.hinput.val(value);
        }

        if(!value.length) {
            $this.hide();
            $this.deleteTimeout();
        }

        if(value.length >= $this.cfg.minLength) {
            if($this.timeout) {
                $this.deleteTimeout();
            }

            var delay = $this.cfg.delay;
            $this.timeout = setTimeout(function() {
                $this.timeout = null;
                $this.search(value);
            }, delay);
        }
        else if(value.length === 0) {
            if($this.timeout) {
                $this.deleteTimeout();
            }
            $this.fireClearEvent();
        }
    },

    showItemtip: function(item) {
        if(item.hasClass('ui-autocomplete-moretext')) {
            this.itemtip.hide();
        }
        else {
            var content;
            if(item.is('li')) {
                content = item.next('.ui-autocomplete-itemtip-content');
            } else {
                if(item.children('td:last').hasClass('ui-autocomplete-itemtip-content')) {
                    content = item.children('td:last');
                } else {
                    this.itemtip.hide();
                    return;
                }
            }

            this.itemtip.html(content.html())
                        .css({
                            'left':'',
                            'top':'',
                            'z-index': ++PrimeFaces.zindex,
                            'width': content.outerWidth()
                        })
                        .position({
                            my: this.cfg.itemtipMyPosition
                            ,at: this.cfg.itemtipAtPosition
                            ,of: item
                        });

            //scrollbar offset
            if(this.cfg.checkForScrollbar) {
                if(this.panel.innerHeight() < this.panel.children('.ui-autocomplete-items').outerHeight(true)) {
                    var panelOffset = this.panel.offset();
                    this.itemtip.css('left', panelOffset.left + this.panel.outerWidth());
                }
            }

            this.itemtip.show();
        }
    },

    showSuggestions: function(query) {
        this.items = this.panel.find('.ui-autocomplete-item');
        this.items.attr('role', 'option');

        if(this.cfg.grouping) {
            this.groupItems();
        }

        this.bindDynamicEvents();

        var $this=this,
        hidden = this.panel.is(':hidden');

        if(hidden) {
            this.show();
        }
        else {
            this.alignPanel();
        }

        if(this.items.length > 0) {
            var firstItem = this.items.eq(0);

            //highlight first item
            if(this.cfg.autoHighlight && firstItem.length) {
                firstItem.addClass('ui-state-highlight');
            }

            //highlight query string
            if(this.panel.children().is('ul') && query.length > 0) {
                this.items.filter(':not(.ui-autocomplete-moretext)').each(function() {
                    var item = $(this);
                    var text = $this.cfg.escape ? item.html() : item.text();
                    var re = new RegExp(PrimeFaces.escapeRegExp(query), 'gi'),
                    highlighedText = text.replace(re, '<span class="ui-autocomplete-query">$&</span>');

                    item.html(highlighedText);
                });
            }

            if(this.cfg.forceSelection) {
                this.currentItems = [];
                this.items.each(function(i, item) {
                    $this.currentItems.push($(item).attr('data-item-label'));
                });
            }

            //show itemtip if defined
            if(this.cfg.autoHighlight && this.cfg.itemtip && firstItem.length === 1) {
                this.showItemtip(firstItem);
            }

            this.displayAriaStatus(this.items.length + this.cfg.resultsMessage);
        }
        else {
            if(this.cfg.emptyMessage) {
                var emptyText = '<div class="ui-autocomplete-emptyMessage ui-widget">' + PrimeFaces.escapeHTML(this.cfg.emptyMessage) + '</div>';
                this.panel.html(emptyText);
            }
            else {
                this.panel.hide();
            }

            this.displayAriaStatus(this.cfg.ariaEmptyMessage);
        }
    },

    searchWithDropdown: function() {
        if(this.cfg.dropdownMode === 'current')
            this.search(this.input.val());
        else
            this.search('');
    },

    search: function(query) {
        //allow empty string but not undefined or null
        if(!this.cfg.active || query === undefined || query === null) {
            return;
        }

        if(this.cfg.cache && this.cache[query]) {
            this.panel.html(this.cache[query]);
            this.showSuggestions(query);
            return;
        }

        if(!this.active) {
            return;
        }

        this.querying = true;

        var $this = this;

        if(this.cfg.itemtip) {
            this.itemtip.hide();
        }

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        if(this.cfg.dynamic && !this.isDynamicLoaded) {
                            this.panel = $(content);
                            this.appendPanel();
                            content = this.panel.get(0).innerHTML;
                        }
                        else {
                            this.panel.html(content);
                        }

                        if(this.cfg.cache) {
                            this.cache[query] = content;
                        }

                        this.showSuggestions(query);
                    }
                });

                return true;
            },
            oncomplete: function() {
                $this.querying = false;
                $this.isDynamicLoaded = true;
            }
        };

        options.params = [
          {name: this.id + '_query', value: query}
        ];

        if(this.cfg.dynamic && !this.isDynamicLoaded) {
            options.params.push({name: this.id + '_dynamicload', value: true});
        }

        if(this.hasBehavior('query')) {
            this.callBehavior('query', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    show: function() {
        this.alignPanel();

        if(this.cfg.effect)
            this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
        else
            this.panel.show();
    },

    hide: function() {
        this.panel.hide();
        this.panel.css('height', 'auto');

        if(this.cfg.itemtip) {
            this.itemtip.hide();
        }
    },

    invokeItemSelectBehavior: function(event, itemValue) {
        if(this.hasBehavior('itemSelect')) {
            var ext = {
                params : [
                    {name: this.id + '_itemSelect', value: itemValue}
                ]
            };

            this.callBehavior('itemSelect', ext);
        }
    },

    invokeItemUnselectBehavior: function(event, itemValue) {
        if(this.hasBehavior('itemUnselect')) {
            var ext = {
                params : [
                    {name: this.id + '_itemUnselect', value: itemValue}
                ]
            };

            this.callBehavior('itemUnselect', ext);
        }
    },

    invokeMoreTextBehavior: function() {
        if(this.hasBehavior('moreText')) {
            var ext = {
                params : [
                    {name: this.id + '_moreText', value: true}
                ]
            };

            this.callBehavior('moreText', ext);
        }
    },

    removeItem: function(event, item) {
        var itemValue = item.attr('data-token-value'),
        itemIndex = this.multiItemContainer.children('li.ui-autocomplete-token').index(item),
        $this = this;

        //remove from options
        this.hinput.children('option').eq(itemIndex).remove();

        //remove from items
        item.fadeOut('fast', function() {
            var token = $(this);

            token.remove();

            $this.invokeItemUnselectBehavior(event, itemValue);
        });
        
        // if empty return placeholder
        if (this.placeholder && this.hinput.children('option').length === 0) {
            this.input.attr('placeholder', this.placeholder);
        }
    },

    setupForceSelection: function() {
        this.currentItems = [this.input.val()];
        var $this = this;

        this.input.blur(function() {
            var value = $(this).val(),
            valid = $this.isValid(value);

            if($this.cfg.autoSelection && valid && $this.checkMatchedItem && $this.items && !$this.isTabPressed && !$this.itemSelectedWithEnter) {
                var selectedItem = $this.items.filter('[data-item-label="' + $.escapeSelector(value) + '"]');
                if (selectedItem.length) {
                    selectedItem.click();
                }
            }

            $this.checkMatchedItem = false;
        });
    },

    disable: function() {
        this.input.addClass('ui-state-disabled').prop('disabled', true);

        if(this.dropdown.length) {
            this.dropdown.addClass('ui-state-disabled').prop('disabled', true);
        }
    },

    enable: function() {
        this.input.removeClass('ui-state-disabled').prop('disabled', false);

        if(this.dropdown.length) {
            this.dropdown.removeClass('ui-state-disabled').prop('disabled', false);
        }
    },

    close: function() {
        this.hide();
    },

    deactivate: function() {
        this.active = false;
    },

    activate: function() {
        this.active = true;
    },

    alignPanel: function() {
        var panelWidth = null;

        if(this.cfg.multiple) {
            panelWidth = this.multiItemContainer.outerWidth();
        }
        else {
            if(this.panel.is(':visible')) {
                panelWidth = this.panel.children('.ui-autocomplete-items').outerWidth();
            }
            else {
                this.panel.css({'visibility':'hidden','display':'block'});
                panelWidth = this.panel.children('.ui-autocomplete-items').outerWidth();
                this.panel.css({'visibility':'visible','display':'none'});
            }

            var inputWidth = this.input.outerWidth();
            if(panelWidth < inputWidth) {
                panelWidth = inputWidth;
            }
        }

        if(this.cfg.scrollHeight) {
            var heightConstraint = this.panel.is(':hidden') ? this.panel.height() : this.panel.children().height();
            if(heightConstraint > this.cfg.scrollHeight)
                this.panel.height(this.cfg.scrollHeight);
            else
                this.panel.css('height', 'auto');
        }

        this.panel.css({'left':'',
                        'top':'',
                        'width': panelWidth,
                        'z-index': ++PrimeFaces.zindex
                });

        if(this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.panel.position({
                    my: this.cfg.myPos
                    ,at: this.cfg.atPos
                    ,of: this.cfg.multiple ? this.jq : this.input
                    ,collision: 'flipfit'
                });
        }
    },

    displayAriaStatus: function(text) {
        this.status.html('<div>' + PrimeFaces.escapeHTML(text) + '</div>');
    },

    groupItems: function() {
        var $this = this;

        if(this.items.length) {
            this.itemContainer = this.panel.children('.ui-autocomplete-items');

            var firstItem = this.items.eq(0);
            if(!firstItem.hasClass('ui-autocomplete-moretext')) {
                this.currentGroup = firstItem.data('item-group');
                var currentGroupTooltip = firstItem.data('item-group-tooltip');

                firstItem.before(this.getGroupItem($this.currentGroup, $this.itemContainer, currentGroupTooltip));
            }

            this.items.filter(':not(.ui-autocomplete-moretext)').each(function(i) {
                var item = $this.items.eq(i),
                itemGroup = item.data('item-group'),
                itemGroupTooltip = item.data('item-group-tooltip');

                if($this.currentGroup !== itemGroup) {
                    $this.currentGroup = itemGroup;
                    item.before($this.getGroupItem(itemGroup, $this.itemContainer, itemGroupTooltip));
                }
            });
        }
    },

    getGroupItem: function(group, container, tooltip) {
        var element = null;

        if(container.is('.ui-autocomplete-table')) {
            if(!this.colspan) {
                this.colspan = this.items.eq(0).children('td').length;
            }

            element = $('<tr class="ui-autocomplete-group ui-widget-header"><td colspan="' + this.colspan + '">' + group + '</td></tr>');
        }
        else {
            element = $('<li class="ui-autocomplete-group ui-autocomplete-list-item ui-widget-header">' + group + '</li>');
        }

        if(element) {
            element.attr('title', tooltip);
        }

        return element;
    },

    deleteTimeout: function() {
        clearTimeout(this.timeout);
        this.timeout = null;
    },

    fireClearEvent: function() {
        this.callBehavior('clear');
    },

    isValid: function(value) {
        if(!this.cfg.forceSelection) {
            return;
        }

        var valid = false;

        for(var i = 0; i < this.currentItems.length; i++) {
            var stripedItem = this.currentItems[i];
            if (stripedItem) {
                stripedItem = stripedItem.replace(/\r?\n/g, '');
            }

            if(stripedItem === value) {
                valid = true;
                break;
            }
        }

        if(!valid) {
            this.input.val('');
            if(!this.cfg.multiple) {
                this.hinput.val('');
            }
            this.fireClearEvent();
        }

        return valid;
    }

});
