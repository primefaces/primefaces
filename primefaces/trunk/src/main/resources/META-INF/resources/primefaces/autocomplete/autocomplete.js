/**
 * PrimeFaces AutoComplete Widget
 */
PrimeFaces.widget.AutoComplete = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.panelId = this.jqId + '_panel';
    this.input = $(this.jqId + '_input');
    this.hinput = $(this.jqId + '_hinput');
    this.panel = this.jq.children(this.panelId);
    this.dropdown = this.jq.children('.ui-button');
    this.disabled = this.input.is(':disabled');
    this.active = true;
    
    //options
    this.cfg.minLength = this.cfg.minLength != undefined ? this.cfg.minLength : 1;
    this.cfg.delay = this.cfg.delay != undefined ? this.cfg.delay : 300;
    
    //visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.input);
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
    $(document.body).children(this.panelId).remove();
    this.panel.appendTo(document.body);
}

PrimeFaces.widget.AutoComplete.prototype.bindStaticEvents = function() {
    var _self = this;
    
    //bind keyup handler
    this.input.keyup(function(e) {
        var keyCode = $.ui.keyCode,
        key = e.which,
        shouldSearch = true;
                
        if(key == keyCode.UP 
            || key == keyCode.LEFT 
            || key == keyCode.DOWN 
            || key == keyCode.RIGHT 
            || key == keyCode.TAB 
            || key == keyCode.ENTER
            || key == keyCode.NUMPAD_ENTER) {
            shouldSearch = false;
        } 
        else if(_self.cfg.pojo) {
            _self.hinput.val($(this).val());
        }
        
        if(shouldSearch) {
            var value = _self.input.val();
        
            if(value.length >= _self.cfg.minLength) {

                //Cancel the search request if user types within the timeout
                if(_self.timeout) {
                    clearTimeout(_self.timeout);
                }

                _self.timeout = setTimeout(function() {
                    _self.search(value);
                }, 
                _self.cfg.delay);
            }
        }
 
    });
  
    //key events
    this.input.keydown(function(e) {
        if(_self.panel.is(':visible')) {
            var keyCode = $.ui.keyCode,
            currentItems = _self.panel.find('.ui-autocomplete-item'),
            highlightItem = _self.panel.find('.ui-autocomplete-item.ui-state-highlight');
        
            switch(e.which) {
                case keyCode.UP:
                case keyCode.LEFT:
                    var prev;
                    if(highlightItem.length > 0) {
                        prev = highlightItem.removeClass('ui-state-highlight').prev();
                        if(prev.length > 0){
                            prev.addClass('ui-state-highlight');
                            var diff = prev.offset().top - _self.panel.offset().top - prev.outerHeight(true) + prev.height();
                            if( diff < 0 )
                                _self.panel.scrollTop( _self.panel.scrollTop() + diff);
                        }
                    } 
                    
                    if(!prev || prev.length == 0) {
                        prev = currentItems.eq(currentItems.length - 1).addClass('ui-state-highlight');
                        _self.panel.scrollTop(prev.offset().top + prev.outerHeight(true) - _self.panel.offset().top - _self.panel.height());
                    }

                    e.preventDefault();
                    break;
                    
                case keyCode.DOWN:
                case keyCode.RIGHT:
                    var next;
                    if(highlightItem.length > 0) {
                        next = highlightItem.removeClass('ui-state-highlight').next();
                        if(next.length > 0){
                            next.addClass('ui-state-highlight');
                            var diff = next.offset().top + next.outerHeight(true) - _self.panel.offset().top;
                            if( diff > _self.panel.height() )
                                _self.panel.scrollTop(_self.panel.scrollTop() + (diff - _self.panel.height()));
                       }
                    } 
                    
                    if(!next || next.length == 0) {
                        currentItems.eq(0).addClass('ui-state-highlight');
                        _self.panel.scrollTop(0);
                    }

                    e.preventDefault();
                    break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    highlightItem.click();

                    e.preventDefault();
                    break;

                case keyCode.ALT: 
                case 224:
                    break;

                case keyCode.TAB:
                    _self.hide();
                    break;
            }
        }
         
    });
    
    //dropdown
    this.dropdown.mouseover(function() {
        if(!_self.disabled) {
            $(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        if(!_self.disabled) {
            $(this).removeClass('ui-state-hover');
        }
    }).mousedown(function() {
        if(!_self.disabled && _self.active) {
            $(this).addClass('ui-state-active');
        }
    }).mouseup(function() {
        if(!_self.disabled && _self.active) {
            $(this).removeClass('ui-state-active');
            
            _self.search('');
            _self.input.focus();
        }
    });

    //hide overlay when outside is clicked
    var offset;
    $(document.body).bind('click', function (e) {
        if(_self.panel.is(":hidden")) {
            return;
        }
        offset = _self.panel.offset();
        if(e.target === _self.input.get(0)) {
            return;
        }
        if (e.pageX < offset.left ||
            e.pageX > offset.left + _self.panel.width() ||
            e.pageY < offset.top ||
            e.pageY > offset.top + _self.panel.height()) {
            _self.hide();
        }
        _self.hide();
    });
}

PrimeFaces.widget.AutoComplete.prototype.bindDynamicEvents = function() {
    var _self = this,
    items = this.panel.find('.ui-autocomplete-item');
    
    //visuals and click handler for items
    items.bind('mouseover', function() {
        $(this).addClass('ui-state-highlight');
    })
    .bind('mouseout', function() {
        $(this).removeClass('ui-state-highlight');
    })
    .bind('click', function(event) {
        var item = $(this);
        
        _self.input.val(item.attr('data-item-label'));
        
        if(_self.cfg.pojo) {
            _self.hinput.val(item.attr('data-item-value'));            
        } 

        _self.invokeItemSelectBehavior(event);
    });
}

PrimeFaces.widget.AutoComplete.prototype.search = function(value) {
    if(!this.active) {
        return;
    }
    
    var _self = this;
    
    //start callback
    if(this.cfg.onstart) {
        this.cfg.onstart.call(this, value);
    }

    var options = {
        source: this.id,
        process: this.id,
        update: this.id,
        formId: this.cfg.formId,
        onsuccess: function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                data = update.text();

                if(id == _self.id) {
                    _self.panel.html(data);
                    _self.bindDynamicEvents();
                    
                    var items = _self.panel.find('.ui-autocomplete-item');
                    
                    if(items.length > 0) {
                        if(_self.cfg.forceSelection) {
                            _self.cachedResults = [];
                            items.each(function(i, item) {
                                _self.cachedResults.push($(item).attr('data-item-label'));
                            });
                        }
                        
                        if(_self.panel.is(':hidden')) {
                            _self.show();
                        }

                        //adjust height
                        _self.panel.css('height', '');
                        if(_self.cfg.scrollHeight && _self.panel.height() > _self.cfg.scrollHeight) {
                            _self.panel.css('height', _self.cfg.scrollHeight + 'px');
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
    
    //complete callback
    if(this.cfg.oncomplete) {
        options.oncomplete = this.cfg.oncomplete;
    }

    if(this.cfg.global === false) {
        options.global = false;
    }

    var params = {};
    params[this.id + '_query'] = encodeURIComponent(value);

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.AutoComplete.prototype.show = function() {
    this.alignPanel();
    this.panel.css('z-index', '100000');
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '99999');
    }

    if(this.cfg.effect)
        this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
    else
        this.panel.show();
}

PrimeFaces.widget.AutoComplete.prototype.hide = function() {
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '');
    }
    
    this.panel.css('z-index', '').hide();
}

PrimeFaces.widget.AutoComplete.prototype.invokeItemSelectBehavior = function(event) {
    if(this.cfg.behaviors) {
        var itemSelectBehavior = this.cfg.behaviors['itemSelect'];

        if(itemSelectBehavior) {
            itemSelectBehavior.call(this, event);
        }
    }
}

PrimeFaces.widget.AutoComplete.prototype.setupForceSelection = function() {
    var _self = this;
	
    this.input.blur(function() {
        var value = $(this).val(),
        valid = false;
		
        if(_self.cachedResults) {
            for(var i = 0; i < _self.cachedResults.length; i++) {
                if(_self.cachedResults[i] == value) {
                    valid = true;
                    break;
                }
            }
        }
		
        if(!valid) {
            $(this).val('');
        }
    });
}

PrimeFaces.widget.AutoComplete.prototype.disable = function() {
    this.disabled = true;
    this.input.addClass('ui-state-disabled').attr('disabled', 'disabled');
}

PrimeFaces.widget.AutoComplete.prototype.enable = function() {
    this.disabled = false;
    this.input.removeClass('ui-state-disabled').removeAttr('disabled');
}

PrimeFaces.widget.AutoComplete.prototype.close = function() {
    this.hide();
}

PrimeFaces.widget.AutoComplete.prototype.deactivate = function() {
    this.active = false;
}

PrimeFaces.widget.AutoComplete.prototype.activate = function() {
    this.active = true;
}

PrimeFaces.widget.AutoComplete.prototype.alignPanel = function() {
    var offset = this.input.offset();
    
    this.panel.css({
        'top':  offset.top + this.input.outerHeight(),
        'left': offset.left,
        'width': this.input.innerWidth() + 'px'
    });
}