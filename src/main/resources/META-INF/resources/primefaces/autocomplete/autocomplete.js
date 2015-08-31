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
        this.suppressInput = true;
        this.touchToDropdownButton = false;
        
        if(this.cfg.cache) {
            this.initCache();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        if(this.cfg.multiple) {
            this.setupMultipleMode();

            this.multiItemContainer.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
            
            if(this.cfg.selectLimit >= 0 && this.multiItemContainer.children('li.ui-autocomplete-token').length === this.cfg.selectLimit) {
                this.input.hide();
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
        this.appendPanel();

        //itemtip
        if(this.cfg.itemtip) {
            this.itemtip = $('<div id="' + this.id + '_itemtip" class="ui-autocomplete-itemtip ui-state-highlight ui-widget ui-corner-all ui-shadow"></div>').appendTo(document.body);
            this.cfg.itemtipMyPosition = this.cfg.itemtipMyPosition||'left top';
            this.cfg.itemtipAtPosition = this.cfg.itemtipAtPosition||'right bottom';
            this.cfg.checkForScrollbar = (this.cfg.itemtipAtPosition.indexOf('right') !== -1);
        }
        
        //aria
        this.input.attr('aria-autocomplete', 'listbox');
        this.jq.attr('role', 'application');
        this.jq.append('<span role="status" aria-live="polite" class="ui-autocomplete-status ui-helper-hidden-accessible"></span>');
        this.status = this.jq.children('.ui-autocomplete-status');
    },

    appendPanel: function() {
        var container = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo): $(document.body);

        if(!container.is(this.jq)) {
            container.children(this.panelId).remove();
            this.panel.appendTo(container);
        }
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
                if(PrimeFaces.isIE(8)) {
                    $this.input.val('');
                }
                $this.input.css('display', 'inline');
            }
            $this.removeItem(event, $(this).parent());
        });
    },

    bindStaticEvents: function() {
        var $this = this;

        this.bindKeyEvents();

        this.dropdown.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).mousedown(function() {
            if($this.active) {
                $(this).addClass('ui-state-active');
            }
        }).mouseup(function() {
            if($this.active) {
                $(this).removeClass('ui-state-active');

                $this.searchWithDropdown();
                $this.input.focus();
            }
        }).focus(function() {
            $(this).addClass('ui-state-focus');
        }).blur(function() {
            $(this).removeClass('ui-state-focus');
        }).keydown(function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                $(this).addClass('ui-state-active');
            }
        }).keyup(function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                $(this).removeClass('ui-state-active');
                $this.searchWithDropdown();
                $this.input.focus();
                e.preventDefault();
                e.stopPropagation();
            }
        });

        if(PrimeFaces.env.browser.mobile) {
            this.dropdown.bind('touchstart', function() {
                $this.touchToDropdownButton = true;
            });
        }
        
        //hide overlay when outside is clicked
        this.hideNS = 'mousedown.' + this.id;
        $(document.body).off(this.hideNS).on(this.hideNS, function (e) {
            if($this.panel.is(":hidden")) {
                return;
            }
            
            var offset = $this.panel.offset();
            if(e.target === $this.input.get(0)) {
                return;
            }
            
            if (e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {
                $this.hide();
            }
        });

        this.resizeNS = 'resize.' + this.id;
        $(window).off(this.resizeNS).on(this.resizeNS, function(e) {
            if($this.panel.is(':visible')) {
                $this.alignPanel();
            }
        });
    },

    bindKeyEvents: function() {
        var $this = this;

        if(this.cfg.queryEvent !== 'enter') {
            this.input.on('input propertychange', function(e) {
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
                
                // for click event on IE8
                if(PrimeFaces.isIE(8) && ($this.itemClick || e.originalEvent.propertyName !== 'value')) {
                    $this.itemClick = false;
                    return;
                }
                
                var value = $this.input.val();

                if($this.cfg.pojo && !$this.cfg.multiple) {
                    $this.hinput.val(value);
                }

                if(!value.length) {
                    $this.hide();
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
            });
        }
        
        this.input.on('keyup.autoComplete', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;
            
            if($this.cfg.queryEvent === 'enter' && (key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER)) {
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
                    case keyCode.NUMPAD_ENTER:
                        if ($this.timeout) {
                            $this.deleteTimeout();
                        }
                    
                        highlightedItem.click();

                        e.preventDefault();
                        e.stopPropagation();
                        $this.itemSelectedWithEnter = true;
                        break;

                    case 18: //keyCode.ALT:
                    case 224:
                        break;

                    case keyCode.TAB:
                        if(highlightedItem.length) {
                            highlightedItem.trigger('click');
                        }
                        $this.hide();
                        break;
                }
            }
            else {
                switch(e.which) {
                    case keyCode.TAB:
                        if ($this.timeout) {
                            $this.deleteTimeout();
                        }
                    break;
                    
                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:                        
                        if($this.cfg.queryEvent === 'enter' || ($this.timeout > 0) || $this.querying) {
                            e.preventDefault();
                        }
                    break;
                };
            }
            
        });
    },

    bindDynamicEvents: function() {
        var $this = this;

        //visuals and click handler for items
        this.items.bind('mouseover', function() {
            var item = $(this);

            if(!item.hasClass('ui-state-highlight')) {
                $this.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');
                item.addClass('ui-state-highlight');

                if($this.cfg.itemtip) {
                    $this.showItemtip(item);
                }
            }
        })
        .bind('click', function(event) {
            var item = $(this),
            itemValue = item.attr('data-item-value');
            
            if(PrimeFaces.isIE(8)) {
                $this.itemClick = true;
            }

            if($this.cfg.multiple) {
                var itemDisplayMarkup = '<li data-token-value="' + item.attr('data-item-value') + '"class="ui-autocomplete-token ui-state-active ui-corner-all ui-helper-hidden">';
                itemDisplayMarkup += '<span class="ui-autocomplete-token-icon ui-icon ui-icon-close" />';
                itemDisplayMarkup += '<span class="ui-autocomplete-token-label">' + item.attr('data-item-label') + '</span></li>';

                $this.inputContainer.before(itemDisplayMarkup);
                $this.multiItemContainer.children('.ui-helper-hidden').fadeIn();
                $this.input.val('').focus();

                $this.hinput.append('<option value="' + itemValue + '" selected="selected"></option>');
                if($this.multiItemContainer.children('li.ui-autocomplete-token').length >= $this.cfg.selectLimit) {
                    $this.input.css('display', 'none').blur();
                }
            }
            else {
                $this.input.val(item.attr('data-item-label')).focus();

                this.currentText = $this.input.val();
                this.previousText = $this.input.val();

                if($this.cfg.pojo) {
                    $this.hinput.val(itemValue);
                }
                
                if(PrimeFaces.env.isLtIE(10)) {
                    var length = $this.input.val().length;
                    $this.input.setSelection(length,length);
                }
            }

            $this.invokeItemSelectBehavior(event, itemValue);

            $this.hide();
        });
        
        if(PrimeFaces.env.browser.mobile) {
            this.items.bind('touchstart', function() {
                if(!$this.touchToDropdownButton) {
                    $this.itemClick = true;
                }
            });
        }
    },

    showItemtip: function(item) {
        var content = item.is('li') ? item.next('.ui-autocomplete-itemtip-content') : item.children('td:last');

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
                this.items.each(function() {
                    var item = $(this),
                    text = item.html(),
                    re = new RegExp(PrimeFaces.escapeRegExp(query), 'gi'),
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
            if(this.cfg.itemtip && firstItem.length === 1) {
                this.showItemtip(firstItem);
            }
            
            this.displayAriaStatus(this.items.length + this.cfg.resultsMessage);
        }
        else {
            if(this.cfg.emptyMessage) {
                var emptyText = '<div class="ui-autocomplete-emptyMessage ui-widget">'+this.cfg.emptyMessage+'</div>';
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
                        this.panel.html(content);

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
            }
        };

        options.params = [
          {name: this.id + '_query', value: query}
        ];

        if(this.hasBehavior('query')) {
            var queryBehavior = this.cfg.behaviors['query'];
            queryBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
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

    invokeItemUnselectBehavior: function(event, itemValue) {
        if(this.cfg.behaviors) {
            var itemUnselectBehavior = this.cfg.behaviors['itemUnselect'];

            if(itemUnselectBehavior) {
                var ext = {
                    params : [
                        {name: this.id + '_itemUnselect', value: itemValue}
                    ]
                };

                itemUnselectBehavior.call(this, ext);
            }
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
    },

    setupForceSelection: function() {
        this.currentItems = [this.input.val()];
        var $this = this;

        this.input.blur(function() {
            var value = $(this).val(),
            valid = false;
            
            if(PrimeFaces.isIE(8)) {
                $this.itemClick = true;
            }
            
            for(var i = 0; i < $this.currentItems.length; i++) {
                if($this.currentItems[i] === value) {
                    valid = true;
                    break;
                }
            }

            if(!valid) {
                if($this.cfg.multiple) {
                    $this.input.val('');
                }
                else {
                    $this.input.val('');
                    $this.hinput.val('');
                }
            }
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

    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },

    alignPanel: function() {
        var panelWidth = null;

        if(this.cfg.multiple) {
            panelWidth = this.multiItemContainer.innerWidth() - (this.input.position().left - this.multiItemContainer.position().left);
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
        this.status.html('<div>' + text + '</div>');
    },
    
    groupItems: function() {
        var $this = this;
        
        if(this.items.length) {
            this.itemContainer = this.panel.children('.ui-autocomplete-items');
            this.currentGroup = this.items.eq(0).data('item-group');
            var currentGroupTooltip = this.items.eq(0).data('item-group-tooltip');
            
            this.items.eq(0).before(this.getGroupItem($this.currentGroup, $this.itemContainer, currentGroupTooltip));
            
            this.items.each(function(i) {
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
    }

});