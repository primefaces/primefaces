/**
 * PrimeFaces InputText Widget
 */
PrimeFaces.widget.InputText = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        //Client behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
        }

        //Visuals
        PrimeFaces.skinInput(this.jq);
    }
});

/**
 * PrimeFaces InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.rowsDefault = this.jq.attr('rows');
        this.cfg.colsDefault = this.jq.attr('cols');
        
        //Visuals
        PrimeFaces.skinInput(this.jq);

        //autoComplete
        if(this.cfg.autoComplete) {
            this.setupAutoComplete();
        }
        
        //autoResize
        if(this.cfg.autoResize) {
            this.setupAutoResize();
        }

        //maxLength
        if(this.cfg.maxlength) {
            this.applyMaxlength();
        }

        //Client behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
        }
        
        //Counter
        if(this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate||'{0}';
            this.updateCounter();
        }
    },
    
    refresh: function(cfg) {
        //remove autocomplete panel
        if(cfg.autoComplete) {
            $(PrimeFaces.escapeClientId(cfg.id + '_panel')).remove();
            $(PrimeFaces.escapeClientId('textarea_simulator')).remove();
        }
        
        this.init(cfg);
    },
    
    setupAutoResize: function() {
        var _self = this;

        this.jq.keyup(function() {
            _self.resize();
        }).focus(function() {
            _self.resize();
        }).blur(function() {
            _self.resize();
        });
    },
    
    resize: function() {
        var linesCount = 0,
        lines = this.jq.val().split('\n');

        for(var i = lines.length-1; i >= 0 ; --i) {
            linesCount += Math.floor((lines[i].length / this.cfg.colsDefault) + 1);
        }

        var newRows = (linesCount >= this.cfg.rowsDefault) ? (linesCount + 1) : this.cfg.rowsDefault;

        this.jq.attr('rows', newRows);
    },
    
    applyMaxlength: function() {
        var _self = this;

        this.jq.keyup(function(e) {
            var value = _self.jq.val(),
            length = value.length;

            if(length > _self.cfg.maxlength) {
                _self.jq.val(value.substr(0, _self.cfg.maxlength));
            }
            
            if(_self.counter) {
                _self.updateCounter();
            }
        });
    },
    
    updateCounter: function() {
        var value = this.jq.val(),
        length = value.length;

        if(this.counter) {
            var remaining = this.cfg.maxlength - length,
            remainingText = this.cfg.counterTemplate.replace('{0}', remaining);

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
                case keyCode.CONTROL:
                case keyCode.ALT:
                case keyCode.BACKSPACE:
                case 224:   //mac command
                    //do not search
                break;

                default:
                    var query = _self.extractQuery();           
                    if(query && query.length >= _self.cfg.minQueryLength) {
                        
                         //Cancel the search request if user types within the timeout
                        if(_self.timeout) {
                            clearTimeout(_self.timeout);
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
                        }
                        
                        if(_self.cfg.scrollHeight) {
                            _self.alignScrollbar(prev);
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
                        }
                        
                        if(_self.cfg.scrollHeight) {
                            _self.alignScrollbar(next);
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
                case keyCode.CONTROL:
                case keyCode.ALT:
                case keyCode.BACKSPACE:
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

                itemSelectBehavior.call(this, event, ext);
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
        var _self = this;
        this.query = query;

        var options = {
            source: this.id,
            update: this.id,
            onsuccess: function(responseXML) {
                var xmlDoc = $(responseXML.documentElement),
                updates = xmlDoc.find("update");

                for(var i=0; i < updates.length; i++) {
                    var update = updates.eq(i),
                    id = update.attr('id'),
                    data = update.text();

                    if(id == _self.id) {
                        _self.panel.html(data);
                        _self.items = _self.panel.find('.ui-autocomplete-item');
                        
                        _self.bindDynamicEvents();
                        
                        if(_self.items.length > 0) {                            
                            //highlight first item
                            _self.items.eq(0).addClass('ui-state-highlight');
                            
                            //adjust height
                            if(_self.cfg.scrollHeight && _self.panel.height() > _self.cfg.scrollHeight) {
                                _self.panel.height(_self.cfg.scrollHeight);
                            }

                            if(_self.panel.is(':hidden')) {
                                _self.show();
                            } 
                            else {
                                _self.alignPanel(); //with new items
                            }

                        }
                        else {
                            _self.panel.hide();
                        }
                    } 
                    else {
                        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, data);
                    }
                }

                PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

                return true;
            }
        };

        options.params = [
          {name: this.id + '_query', value: query}  
        ];
        
        PrimeFaces.ajax.AjaxRequest(options);
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
    
    alignScrollbar: function(item) {
        var relativeTop = item.offset().top - this.items.eq(0).offset().top,
        visibleTop = relativeTop + item.height(),
        scrollTop = this.panel.scrollTop(),
        scrollBottom = scrollTop + this.cfg.scrollHeight,
        viewportCapacity = parseInt(this.cfg.scrollHeight / item.outerHeight(true));
        
        //scroll up
        if(visibleTop < scrollTop) {
            this.panel.scrollTop(relativeTop);
        }
        //scroll down
        else if(visibleTop > scrollBottom) {
            var viewportTopitem = this.items.eq(item.index() - viewportCapacity + 1);
            
            this.panel.scrollTop(viewportTopitem.offset().top - this.items.eq(0).offset().top);
        }
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
        this.label = this.jq.find('.ui-selectonemenu-label');
        this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');
        this.panel = this.jq.children(this.panelId);
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.itemContainer = this.panel.children('.ui-selectonemenu-items');
        this.items = this.itemContainer.find('.ui-selectonemenu-item');
        this.options = this.input.children('option');
        this.cfg.effectDuration = this.cfg.effectDuration||400;
        var _self = this,
        selectedOption = this.options.filter(':selected');

        //disable options
        this.options.filter(':disabled').each(function() {
            _self.itemContainer.children().eq($(this).index()).addClass('ui-state-disabled');
        });
        
        //triggers and default label value
        if(this.cfg.editable) {
            this.triggers = this.jq.find('.ui-selectonemenu-trigger');
        } 
        else {
            this.triggers = this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');
            this.setLabel(selectedOption.text());
            this.value = selectedOption.val();
        }
        
        //highlight selected
        this.highlightItem(this.items.eq(selectedOption.index()), false);
        
        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        if(!this.disabled) {            
            this.bindEvents();
            
            this.bindConstantEvents();
            
            //dialog support
            this.setupDialogSupport();
        }

        //Append panel to body
        $(document.body).children(this.panelId).remove();
        this.panel.appendTo(document.body);

        if(this.jq.is(':visible')) {
            this.initWidths();
        }
        else {
            var hiddenParent = this.jq.parents('.ui-hidden-container:first'),
            hiddenParentWidget = hiddenParent.data('widget');

            if(hiddenParentWidget) {
                hiddenParentWidget.addOnshowHandler(function() {
                    return _self.initWidths();
                });
            }
        }
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    setupDialogSupport: function() {
        var dialog = this.jq.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            this.panel.css('position', 'fixed');
        }
    },
    
    initWidths: function() {
        var userStyle = this.jq.attr('style');
        
        //do not adjust width of container if there is user width defined
        if(!userStyle||userStyle.indexOf('width') == -1) {
            this.jq.width(this.input.outerWidth(true) + 5);  
        }
        
        //width of label
        this.label.width(this.jq.width() - this.menuIcon.width());
        
        //align panel and container
        var jqWidth = this.jq.innerWidth();
        if(this.panel.outerWidth() < jqWidth) {
            this.panel.width(jqWidth);
        }
    },
    
    bindEvents: function() {
        var _self = this;

        //Events for items
        this.items.filter(':not(.ui-state-disabled)').mouseover(function() {
            _self.highlightItem($(this), false);
        })
        .click(function() {
            _self.selectItem($(this));   
        });

        //Events to show/hide the panel
        this.triggers.mouseover(function() {
            _self.jq.addClass('ui-state-hover');
            _self.menuIcon.addClass('ui-state-hover');
        }).mouseout(function() {
            _self.jq.removeClass('ui-state-hover');
            _self.menuIcon.removeClass('ui-state-hover');
        }).click(function(e) {
            if(_self.panel.is(":hidden"))
                _self.show();
            else
                _self.hide();

            _self.jq.removeClass('ui-state-hover');
            _self.menuIcon.removeClass('ui-state-hover');          
            _self.input.focus();
            e.preventDefault();
        });
        
        this.input.focus(function(){
            _self.jq.addClass('ui-state-focus');
            _self.menuIcon.addClass('ui-state-focus');
        })
        .blur(function(){
            _self.jq.removeClass('ui-state-focus');
            _self.menuIcon.removeClass('ui-state-focus');
            
            if(_self.changed) {
                _self.triggerChange();
            }
        });
        
        //onchange handler for editable input
        if(this.cfg.editable) {
            this.label.change(function() {
                _self.triggerChange(true);
            });
        }
        
        //key bindings
        this.bindKeyEvents();
    },
    
    bindConstantEvents: function(item) {
        var _self = this;
        
        //hide overlay when outside is clicked
        $(document.body).bind('mousedown.ui-selectonemenu', function (e) {
            if(_self.panel.is(":hidden")) {
                return;
            }
            var offset = _self.panel.offset();
            if (e.target === _self.label.get(0) ||
                e.target === _self.menuIcon.get(0) ||
                e.target === _self.menuIcon.children().get(0)) {
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
    },
    
    unbindEvents: function() {
        this.items.filter(':not(.ui-state-disabled)').unbind('mouseover click');
        this.triggers.unbind('mouseover mouseout click');
        this.input.unbind('focus blur keydown keyup');
    },
    
    highlightItem: function(item, updateLabel) {
        this.unhighlightItem(this.items.filter('.ui-state-highlight'));
        item.addClass('ui-state-highlight');
        
        if(updateLabel) {
            this.setLabel(item.text());
        }

        this.alignScroller(item);
    },
    
    unhighlightItem: function(item) {
        item.removeClass('ui-state-highlight');
    },
    
    triggerChange: function(edited) {
        this.changed = false;
        
        var inputEl = this.input.get(0);
        if(this.cfg.onchange) {
            this.cfg.onchange.call(inputEl);
        }
        
        if(this.cfg.behaviors && this.cfg.behaviors['change']) {
            this.cfg.behaviors['change'].call(inputEl);
        }
        
        if(!edited) {
            this.value = this.options.filter(':selected').val();
        }
    },
    
    /**
     * Handler to process item selection with mouse
     */
    selectItem: function(item, silent) {
        //option to select
        var newOption = this.options.eq(item.index()),
        currentOption = this.options.filter(':selected'),
        sameOption = newOption.val() == currentOption.val(),
        shouldChange = null;
        
        if(this.cfg.editable) {
            shouldChange = (!sameOption)||(newOption.text() != this.label.val());
        }
        else {
            shouldChange = !sameOption;
        }
        
        if(shouldChange) {
            //update selected option
            this.input.val(newOption.val());
            this.value = newOption.val();
            
            //update label
            this.setLabel(newOption.text());
            
            //trigger change
            this.triggerChange();
        }

        if(!silent) {
            this.input.focus();
        }
        
        this.hide();
    },
    
    bindKeyEvents: function() {
        var _self = this;

        this.input.keyup(function(e) {            
            var keyCode = $.ui.keyCode,
            mozilla = $.browser.mozilla;

            switch(e.which) { 
                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                    if(mozilla) {
                        var highlightedItem = _self.items.filter('.ui-state-highlight');
                        _self.options.filter(':selected').removeAttr('selected');
                        _self.options.eq(highlightedItem.index()).attr('selected', 'selected');
                    }
                    
                    e.preventDefault();
                break;
                
                case keyCode.TAB:
                case keyCode.ESCAPE:
                    //do nothing
                break;
                                
                default:
                    var currentOption = _self.options.filter(':selected'),
                    item = _self.items.eq(currentOption.index());

                    _self.highlightItem(item, true);
                    
                    _self.changed = (currentOption.val() != _self.value);
                    
                    e.preventDefault();
                break;
            }            
        })
        .keydown(function(e) {            
            var keyCode = $.ui.keyCode;

            switch(e.which) { 
                case keyCode.UP:
                case keyCode.LEFT:
                    var highlightedItem = _self.items.filter('.ui-state-highlight'),
                    prev = highlightedItem.prevAll(':not(.ui-state-disabled):first');
                    
                    

                    if(prev.length == 1) {
                        _self.highlightItem(prev, true);
                        _self.changed = true;
                        
                        _self.options.filter(':selected').removeAttr('selected');
                        
                        var option = _self.options.eq(prev.index());
                        option.attr('selected', 'selected');
                        
                        _self.changed = (option.val() != _self.value);
                    }
                    
                    e.preventDefault();
                    
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    var highlightedItem = _self.items.filter('.ui-state-highlight'),
                    next = highlightedItem.nextAll(':not(.ui-state-disabled):first');

                    if(next.length == 1) {
                        _self.highlightItem(next, true);
                        _self.options.filter(':selected').removeAttr('selected');
                        
                        var option = _self.options.eq(next.index());
                        option.attr('selected', 'selected');
                        
                        _self.changed = (option.val() != _self.value);
                    }
                    
                    e.preventDefault();
                    
                break;
      
                case keyCode.TAB:
                case keyCode.ESCAPE:
                    if(_self.panel.is(":visible")) {
                        _self.hide();
                    }
                    
                    if(_self.changed) {
                        _self.triggerChange();
                    }                    
                break;
                

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    if(_self.panel.is(":visible")) {
                        _self.hide();
                    }
                    
                    if(_self.changed) {
                        _self.triggerChange();
                    }
                    
                    e.preventDefault();
                break;
            }
        });
    },
         
    alignScroller: function(item) {
        var scrollHeight = this.panel.height();
        
        if(scrollHeight < this.itemContainer.height()) {
            var itemTop = item.offset().top - this.items.eq(0).offset().top,
            visibleTop = itemTop + item.height(),
            scrollTop = this.panel.scrollTop(),
            scrollBottom = scrollTop + scrollHeight;
            
            //scroll up
            if(itemTop < scrollTop) {
                this.panel.scrollTop(itemTop);
            }
            //scroll down
            else if(visibleTop > scrollBottom) {
                this.panel.scrollTop(itemTop);
            }
        }
    },
    
    show: function() {
        //calculate panel position
        this.alignPanel();

        this.panel.css('z-index', ++PrimeFaces.zindex);

        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
        }

        this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
        
        //highlight current
        this.highlightItem(this.items.eq(this.options.filter(':selected').index()), false);
    },
    
    hide: function() {
        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', '');
        }

        this.panel.css('z-index', '').hide();
    },
    
    focus: function() {
        this.input.focus();
    },
    
    blur: function() {
        this.input.blur();
    },
    
    disable: function() {
        this.disabled = true;
        this.jq.addClass('ui-state-disabled');
        this.input.attr('disabled', 'disabled');
        if(this.cfg.editable) {
            this.label.attr('disabled', 'disabled');
        }
        this.unbindEvents();
    },
    
    enable: function() {
        this.disabled = false;
        this.jq.removeClass('ui-state-disabled');
        this.input.removeAttr('disabled');
        if(this.cfg.editable) {
            this.label.removeAttr('disabled');
        }
        this.bindEvents();
    },
    
    alignPanel: function() {
        var fixedPosition = this.panel.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

        this.panel.css({left:'', top:''}).position({
                                        my: 'left top'
                                        ,at: 'left bottom'
                                        ,of: this.jq
                                        ,offset : positionOffset
                                    });
    },
    
    setLabel: function(value) {
        if(this.cfg.editable) {
            this.label.val(value);
        }
        else {
            if(value == '')
                this.label.html('&nbsp;');
            else
                this.label.text(value);
        }
    },
    
    selectValue : function(value) {
        var option = this.options.filter('[value="' + value + '"]');

        this.selectItem(this.items.eq(option.index()), true);
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
            this.inputs = $('input:radio[name="' + this.id + '"]:not:(:disabled)');
            this.outputs = this.inputs.parent().next('.ui-radiobutton-box:not(.ui-state-disabled)');
            this.labels = $();
            this.icons = this.outputs.find('.ui-radiobutton-icon');
            
            //labels
            for(var i=0; i < this.outputs.length; i++) {
                this.labels = this.labels.add('label[for="' + this.outputs.eq(i).parent().attr('id') + '"]');
            }
        }
        //regular layout
        else {
            this.outputs = this.jq.find('.ui-radiobutton-box:not(.ui-state-disabled)');
            this.inputs = this.jq.find(':radio:not(:disabled)');
            this.labels = this.jq.find('label:not(.ui-state-disabled)');
            this.icons = this.jq.find('.ui-radiobutton-icon');
        }
        
        this.checkedRadio = this.outputs.filter('.ui-state-active');
    
        this.bindEvents();    
        
        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    bindEvents: function() {
        var _self = this;
        
        //events for displays
        this.outputs.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            var radio = $(this),
            input = radio.prev().children(':radio');
            
            if(!input.is(':checked')) {
                input.trigger('click');
                
                if($.browser.msie && parseInt($.browser.version) < 9) {
                    input.trigger('change');
                }
            }
        });
        
        //selects radio when label is clicked
        this.labels.click(function(e) {
            var target = $(PrimeFaces.escapeClientId($(this).attr('for'))),
            radio = null;

            //checks if target is input or not(custom labels)
            if(target.is(':input'))
                radio = target.parent().next();
            else
                radio = target.children('.ui-radiobutton-box'); //custom layout

            radio.click();
            
            e.preventDefault();
        });
        
        //delegate focus-blur-change states
        this.inputs.focus(function() {
            var input = $(this),
            radio = input.parent().next();
            
            if(input.prop('checked')) {
                radio.removeClass('ui-state-active');
            }
            
            radio.addClass('ui-state-focus');
        })
        .blur(function() {
            var input = $(this),
            radio = input.parent().next();
            
            if(input.prop('checked')) {
                radio.addClass('ui-state-active');
            }
                        
            radio.removeClass('ui-state-focus');
        })
        .change(function(e) {
            //unselect previous
            _self.checkedRadio.removeClass('ui-state-active').children('.ui-radiobutton-icon').removeClass('ui-icon ui-icon-bullet');
            
            //select current
            var currentInput = _self.inputs.filter(':checked'),
            currentRadio = currentInput.parent().next();
            currentRadio.children('.ui-radiobutton-icon').addClass('ui-icon ui-icon-bullet');
            
            if(!currentInput.is(':focus')) {
                currentRadio.addClass('ui-state-active');
            }
            
            _self.checkedRadio = currentRadio;
        });
        
        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
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
        
        var _self = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.box.mouseover(function() {
                _self.box.addClass('ui-state-hover');
            }).mouseout(function() {
                _self.box.removeClass('ui-state-hover');
            }).click(function() {
                _self.input.trigger('click');
    
                if($.browser.msie && parseInt($.browser.version) < 9) {
                    _self.input.trigger('change');
                }
            });
            
            //reflect focus-blur-change states of hidden checkbox
            this.input.focus(function() {
                if(_self.input.prop('checked')) {
                    _self.box.removeClass('ui-state-active');
                }

                _self.box.addClass('ui-state-focus');
            })
            .blur(function() {
                if(_self.input.prop('checked')) {
                    _self.box.addClass('ui-state-active');
                }

                _self.box.removeClass('ui-state-focus');
            })
            .change(function() {
                if(_self.input.is(':checked')) {
                    _self.box.children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');

                    if(!_self.input.is(':focus')) {
                        _self.box.addClass('ui-state-active');
                    }             
                }
                else {
                    _self.box.removeClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');     
                }
            });

            //toggle state on label click
            this.itemLabel.click(function() {
                _self.toggle();
            });

            //Client Behaviors
            if(this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
            }
        }
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    toggle: function() {  
        this.box.trigger('click');
    },
    
    check: function() {
        this.input.prop('checked', true);
        this.box.addClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
    },
    
    uncheck: function() {
        this.input.prop('checked', false);
        this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
    }
    
});

/**
 * PrimeFaces SelectManyCheckbox Widget
 */
PrimeFaces.widget.SelectManyCheckbox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.outputs = this.jq.find('.ui-chkbox-box:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':checkbox:not(:disabled)');
                        
        this.bindEvents();
        
        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    bindEvents: function() {        
        this.outputs.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            var checkbox = $(this),
            input = checkbox.prev().children(':checkbox');
            
            input.trigger('click');
            
            if($.browser.msie && parseInt($.browser.version) < 9) {
                input.trigger('change');
            }
        });
        
        //delegate focus-blur-change states
        this.inputs.focus(function() {
            var input = $(this),
            checkbox = input.parent().next();
            
            if(input.prop('checked')) {
                checkbox.removeClass('ui-state-active');
            }
            
            checkbox.addClass('ui-state-focus');
        })
        .blur(function() {
            var input = $(this),
            checkbox = input.parent().next();
            
            if(input.prop('checked')) {
                checkbox.addClass('ui-state-active');
            }
            
            checkbox.removeClass('ui-state-focus');
        })
        .change(function(e) {
            var input = $(this),
            checkbox = input.parent().next(),
            hasFocus = input.is(':focus'),
            disabled = input.is(':disabled');

            if(disabled) {
                return;
            }

            if(input.is(':checked')) {
                checkbox.children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');

                if(!hasFocus) {
                    checkbox.addClass('ui-state-active');
                }
            }
            else {
                checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
            }
        });

        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
    }
    
});

            /**
 * PrimeFaces SelectListbox Widget
 */
PrimeFaces.widget.SelectListbox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.input = $(this.jqId + '_input'),
        this.listContainer = this.jq.children('ul'),
        this.options = $(this.input).children('option');

        this.generateItems(cfg);

        this.bindEvents();

        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
        }
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    /**
     * Creates items for each option 
     */
    generateItems: function() {
        var _self = this;

        this.options.each(function(i) {
            var option = $(this),
            selected = option.is(':selected'),
            disabled = option.is(':disabled'),
            styleClass = 'ui-selectlistbox-item ui-corner-all';
            styleClass = disabled ? styleClass + ' ui-state-disabled' : styleClass;
            styleClass = selected ? styleClass + ' ui-state-active' : styleClass;

            _self.listContainer.append('<li class="' + styleClass + '">' + option.text() + '</li>');
        });

        this.items = this.listContainer.children('li:not(.ui-state-disabled)');
        
        //scroll to selected
        var selected = this.options.filter(':selected');
        if(selected.length > 0) {
            var selectedItem = this.items.eq(selected.eq(0).index()),
            itemBottom = selectedItem.position().top + selectedItem.height(),
            scrollBottom = this.jq.scrollTop() + this.jq.height();

            if(itemBottom > scrollBottom) {
                this.jq.scrollTop(selectedItem.position().top);
            }
        }
    },
    
    bindEvents: function() {
        var _self = this;

        //items
        this.items.mouseover(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')) {
                $(this).addClass('ui-state-hover');
            }
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).mousedown(function(e) {        
            var element = $(this),
            option = $(_self.options.get(element.index())),
            metaKey = (e.metaKey||e.ctrlKey);

            //clear previous selection if single or multiple with no metakey
            if(_self.cfg.selection == 'single' || (_self.cfg.selection == 'multiple' && !metaKey)) {
                _self.items.removeClass('ui-state-active ui-state-hover');
                _self.options.removeAttr('selected');
            }

            //unselect current selected item if multiple with metakey
            if(_self.cfg.selection == 'multiple' && metaKey && element.hasClass('ui-state-active')) {
                element.removeClass('ui-state-active');
                option.removeAttr('selected');
            } 
            //select item
            else {
                element.addClass('ui-state-active').removeClass('ui-state-hover');
                option.attr('selected', 'selected');
            }

            _self.input.change();

            PrimeFaces.clearSelection();

            e.preventDefault();
        });

        //input
        this.input.focus(function() {
            _self.jq.addClass('ui-state-focus');
        }).blur(function() {
            _self.jq.removeClass('ui-state-focus');
        })
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
        
        this.buttons = this.jq.children('div:not(:disabled)');
        this.inputs = this.jq.find(':checkbox:not(:disabled)');
        var _self = this;

        this.buttons.mouseover(function() {
            var button = $(this);
            if(!button.hasClass('ui-state-active'))
                button.addClass('ui-state-hover'); 
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover'); 
        }).click(function() {
            var button = $(this);

            if(button.hasClass('ui-state-active'))
                _self.unselect(button);
            else
                _self.select(button);
        });

        //Client behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
        
        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    select: function(button) {
        button.removeClass('ui-state-hover').addClass('ui-state-active')
                                .children(':checkbox').attr('checked','checked').change();

    },
    
    unselect: function(button) {
        button.removeClass('ui-state-active').addClass('ui-state-hover')
                                .children(':checkbox').removeAttr('checked').change();
    }
    
});

/**
 * PrimeFaces SelectOneButton Widget
 */
PrimeFaces.widget.SelectOneButton = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.buttons = this.jq.children('div:not(:disabled)');
        this.inputs = this.jq.find(':radio:not(:disabled)');
        var _self = this;

        this.buttons.mouseover(function() {
            var button = $(this);
            if(!button.hasClass('ui-state-active')) {
                button.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            var button = $(this);

            if(!button.hasClass('ui-state-active')) {
                _self.select(button);
            }
        });

        //Client behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
        
        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    select: function(button) {
        this.unselect(this.buttons.filter('.ui-state-active'));

        button.addClass('ui-state-active').children(':radio').attr('checked','checked').change();
    },
    
    unselect: function(button) {
        button.removeClass('ui-state-active ui-state-hover').children(':radio').removeAttr('checked').change();
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
        var _self = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.jq.mouseover(function() {
                if(!_self.jq.hasClass('ui-state-active')) {
                    _self.jq.addClass('ui-state-hover');
                }
            }).mouseout(function() {
                if(!_self.jq.hasClass('ui-state-active')) {
                    _self.jq.removeClass('ui-state-hover');
                }
            }).click(function() {
                _self.toggle();
            });

            //Client Behaviors
            if(this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
            }
        }
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    toggle: function() {
        if(!this.disabled) {
            if(this.jq.hasClass('ui-state-active'))
                this.uncheck();
            else
                this.check();
        }
    },
    
    check: function() {
        if(!this.disabled) {
            this.input.attr('checked', 'checked');
            this.jq.addClass('ui-state-active').children('.ui-button-text').html(this.cfg.onLabel);

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.offIcon).addClass(this.cfg.onIcon);
            }

            this.input.change();
        }
    },
    
    uncheck: function() {
        if(!this.disabled) {
            this.input.removeAttr('checked', 'checked');
            this.jq.removeClass('ui-state-active').children('.ui-button-text').html(this.cfg.offLabel);

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.onIcon).addClass(this.cfg.offIcon);
            }

            this.input.change();
        }
    }
    
});




/** 
 * PrimeFaces SelectCheckboxMenu Widget
 */
PrimeFaces.widget.SelectCheckboxMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.panelId = this.jqId + '_panel';
        this.labelContainer = this.jq.find('.ui-selectcheckboxmenu-label-container');
        this.label = this.jq.find('.ui-selectcheckboxmenu-label');
        this.menuIcon = this.jq.children('.ui-selectcheckboxmenu-trigger');
        this.triggers = this.jq.find('.ui-selectcheckboxmenu-trigger, .ui-selectcheckboxmenu-label');
        this.panel = this.jq.children(this.panelId);
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.itemContainer = this.panel.children('.ui-selectcheckboxmenu-items');
        this.inputs = this.jq.find(':checkbox');

        this.generateItems();

        this.checkboxes = this.itemContainer.find('.ui-chkbox-box:not(.ui-state-disabled)');
        this.labels = this.itemContainer.find('label');

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        this.bindEvents();

        this.setupDialogSupport();

        //Append panel to body
        $(document.body).children(this.panelId).remove();
        this.panel.appendTo(document.body);
        
        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    generateItems: function() {
        var _self = this;

        this.inputs.each(function() {
            var input = $(this),
            label = input.next(),
            disabled = input.is(':disabled'),
            checked = input.is(':checked'),
            boxClass = 'ui-chkbox-box ui-widget ui-corner-all ui-state-default';

            if(disabled) {
                boxClass += " ui-state-disabled";
            }

            if(checked) {
                boxClass += " ui-state-active";
            }

            var iconClass = checked ? 'ui-chkbox-icon ui-icon ui-icon-check' : 'ui-chkbox-icon';

            var dom = '<li class="ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all">';
            dom += '<div class="ui-chkbox ui-widget"><div class="' + boxClass + '"><span class="' + iconClass + '"></span></div></div>';
            dom += '<label>' + label.text() +  '</label></li>';

            _self.itemContainer.append(dom);
        });
    },
    
    bindEvents: function() {
        var _self = this;

        //Events for checkboxes
        this.checkboxes.mouseover(function() {
            var item = $(this);
            if(!item.hasClass('ui-state-active')&&!item.hasClass('ui-state-disabled'))
                item.addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            _self.toggleItem($(this));
        });

        //Labels
        this.labels.click(function() {
            var checkbox = $(this).prev().children('.ui-chkbox-box');
            _self.toggleItem(checkbox);
            checkbox.removeClass('ui-state-hover');
            PrimeFaces.clearSelection();
        });

        //Events to show/hide the panel
        this.triggers.mouseover(function() {
            if(!_self.disabled&&!_self.triggers.hasClass('ui-state-focus')) {
                _self.triggers.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            if(!_self.disabled) {
                _self.triggers.removeClass('ui-state-hover');
            }
        }).mousedown(function(e) {
            if(!_self.disabled) {
                if(_self.panel.is(":hidden")) {
                    _self.show();
                }
                else {
                    _self.hide(true);
                }
            }
        }).click(function(e) {
            e.preventDefault(); 
        });

        //hide overlay when outside is clicked
        $(document.body).bind('mousedown.selectcheckboxmenu', function (e) {        
            if(_self.panel.is(':hidden')) {
                return;
            }

            //do nothing on trigger mousedown
            var target = $(e.target);
            if(_self.triggers.is(target)||_self.triggers.has(target).length > 0) {
                return;
            }

            //hide the panel and remove focus from label
            var offset = _self.panel.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + _self.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + _self.panel.height()) {

                _self.hide(true);
            }
        });

        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if(_self.panel.is(':visible')) {
                _self.hide(false);
            }
        });
        
        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
    },
    
    show: function() {    
        //calculate panel position
        this.alignPanel();

        this.panel.css('z-index', ++PrimeFaces.zindex);

        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
        }

        this.panel.show();
    },
    
    hide: function(animate) {
        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', '');
        }

        this.panel.css('z-index', '');
        this.triggers.removeClass('ui-state-focus');

        if(animate)
            this.panel.fadeOut('fast');
        else
            this.panel.hide();
    },
    
    alignPanel: function() {
        var fixedPosition = this.panel.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

        this.panel.css({left:'', top:''}).position({
                                        my: 'left top'
                                        ,at: 'left bottom'
                                        ,of: this.jq
                                        ,offset : positionOffset
                                    });
    },
    
    toggleItem: function(checkbox) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            if(checkbox.hasClass('ui-state-active'))
                this.uncheck(checkbox);
            else
                this.check(checkbox);
        }
    },
    
    check: function(checkbox) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var input = this.inputs.eq(checkbox.parents('li:first').index());
            checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
            input.attr('checked', 'checked').change();
        }
    },
    
    uncheck : function(checkbox) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var input = this.inputs.eq(checkbox.parents('li:first').index());
            checkbox.removeClass('ui-state-active').addClass('ui-state-hover').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
            input.removeAttr('checked').change();
        }
    },
    
    setupDialogSupport: function() {
        var dialog = this.jq.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            this.panel.css('position', 'fixed');
        }
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

        //Client behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
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

            //Client Behaviors
            if(this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
            }

            //Visuals
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
                    _self.panel.hide();
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
    
    show: function() {
        //align panel before showing
        if(!this.cfg.inline) {
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
        var _self = this;
        
        //attach keypress listener to parent form
        this.jqTarget.parents('form:first').keydown(function(e) {
           //do not proceed if event target is not in this scope or target is a textarea
           if((_self.scope && _self.scope.find(e.target).length == 0)||$(e.target).is('textarea')) {
               return true;
           }
               
           var keyCode = $.ui.keyCode;
           
           if(e.which == keyCode.ENTER || e.which == keyCode.NUMPAD_ENTER) {
               _self.jqTarget.click();
               e.preventDefault();
           }
        });
        
        $(this.jqId + '_s').remove();
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
        this.menu = $(this.jqId + '_menu');
        this.menuitems = this.menu.find('.ui-menuitem:not(.ui-state-disabled)');
        this.cfg.disabled = this.button.is(':disabled');
        
        if(!this.cfg.disabled) {
            this.cfg.position = {
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.button
            };
        
            this.menu.appendTo(document.body);
            
            this.bindEvents();

            this.setupDialogSupport();
        }
        
        //pfs metadata
        this.button.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.menuButton.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    //override
    refresh: function(cfg) {
        //remove previous overlay
        $(document.body).children(PrimeFaces.escapeClientId(cfg.id + '_menu')).remove();
        
        this.init(cfg);
    },
    
    bindEvents: function() {  
        var _self = this;

        PrimeFaces.skinButton(this.button).skinButton(this.menuButton);

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //toggle menu
        this.menuButton.click(function() {
            if(_self.menu.is(':hidden')) {   
                _self.show();
            }
            else {
                _self.hide();
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
            _self.hide();
        });

        /**
        * handler for document mousedown to hide the overlay
        **/
        $(document.body).bind('mousedown.ui-menubutton', function (e) {
            //do nothing if hidden already
            if(_self.menu.is(":hidden")) {
                return;
            }

            //do nothing if mouse is on button
            var target = $(e.target);
            if(target.is(_self.button)||_self.button.has(target).length > 0) {
                return;
            }

            //hide overlay if mouse is outside of overlay except button
            var offset = _self.menu.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + _self.menu.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + _self.menu.height()) {

                _self.button.removeClass('ui-state-focus ui-state-hover');
                _self.hide();
            }
        });

        //hide overlay on window resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if(_self.menu.is(':visible')) {
                _self.menu.hide();
            }
        });
    },
    
    setupDialogSupport: function() {
        var dialog = this.button.parents('.ui-dialog:first');

        if(dialog.length == 1) {        
            this.menu.css('position', 'fixed');
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
        var fixedPosition = this.menu.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

        this.cfg.position.offset = positionOffset;

        this.menu.css({left:'', top:'','z-index': ++PrimeFaces.zindex}).position(this.cfg.position);
    }
    
});

/*
 * PrimeFaces ThemeSwitcher Widget
 */
PrimeFaces.widget.ThemeSwitcher = PrimeFaces.widget.SelectOneMenu.extend({
    
    init: function(cfg) { 
        var _self = this;
        cfg.onchange = function() {
            var value = _self.options.filter(':selected').val();
            
            PrimeFaces.changeTheme(value);
        };
        
        this._super(cfg);
    }
});