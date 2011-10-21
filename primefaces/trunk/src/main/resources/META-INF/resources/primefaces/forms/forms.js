(function($) {
    /**
     * The autoResizable() function is used to animate auto-resizable textareas on a given selector. To change the default options,
     * simply pass the options in an object as the only argument to the autoResizable() function.
     */
    $.fn.autoResizable = function(options) {

        // Defines the abstract settings.
        var settings = $.extend({
            animate: true,
            animateDuration: 200,
            maxHeight: 500,
            onBeforeResize: null,
            onAfterResize: null,
            padding: 20,
            paste: true,
            pasteInterval: 100
        }, options);

        // Filters the selectors to just textareas.
        return this.filter('textarea').each(function() {
            var textarea = $(this),
            originalHeight = textarea.height(),
            currentHeight = 0,
            pasteListener = null,
            animate = settings.animate,
            animateDuration = settings.animateDuration,
            maxHeight = settings.maxHeight,
            onBeforeResize = settings.onBeforeResize,
            onAfterResize = settings.onAfterResize,
            padding = settings.padding,
            paste = settings.paste,
            pasteInterval = settings.pasteInterval;

            // Creates a clone of the textarea, used to determine the textarea height.
            var clone = (function() {
                var cssKeys = ['height', 'letterSpacing', 'lineHeight', 'textDecoration', 'width'],
                properties = {};

                $.each(cssKeys, function(i, key) {
                    properties[key] = textarea.css(key);
                });

                return textarea.clone().removeAttr('id').removeAttr('name').css({
                    left: -99999,
                    position: 'absolute',
                    top: -99999
                }).css(properties).attr('tabIndex', -1).insertBefore(textarea);
            })();

            /**
             * Automatically resizes the textarea.
             */
            var autoResize = function() {
                if (originalHeight <= 0) {
                    originalHeight = textarea.height();
                }

                // Prepares the clone.
                clone.height(0).val(textarea.val()).scrollTop(10000);

                // Determines the height of the text.
                var newHeight = Math.max((clone.scrollTop() + padding), originalHeight);
                if (newHeight === currentHeight || (newHeight >= maxHeight && currentHeight === maxHeight)) {
                    return;
                }

                if (newHeight >= maxHeight) {
                    newHeight = maxHeight;
                    textarea.css('overflow-y', 'auto');
                } else {
                    textarea.css({
                        overflow: 'hidden', 
                        overflowY: 'hidden'
                    });
                }

                // Fires off the onBeforeResize event.
                var resize = true;
                if (onBeforeResize !== null) {
                    resize = onBeforeResize.call(textarea, currentHeight, newHeight);
                }

                currentHeight = newHeight;

                // Determines if the resizing should actually take place.
                if (resize === false) {
                    return;
                }

                // Adjusts the height of the textarea.
                if (animate && textarea.css('display') === 'block') {
                    textarea.stop().animate({
                        height: newHeight
                    }, animateDuration, function() {
                        if (onAfterResize !== null) {
                            onAfterResize.call(textarea);
                        }
                    });
                } else {
                    textarea.height(newHeight);
                    if (onAfterResize !== null) {
                        onAfterResize.call(textarea);
                    }
                }
            };

            /**
             * Initialises the paste listener and invokes the autoResize method.
             */
            var init = function() {
                if (paste) {
                    pasteListener = setInterval(autoResize, pasteInterval);
                }

                autoResize();
            };

            /**
             * Uninitialises the paste listener.
             */
            var uninit = function() {
                if (pasteListener !== null) {
                    clearInterval(pasteListener);
                    pasteListener = null;
                }
            };

            // Hides scroll bars and disables WebKit resizing.
            textarea.css({
                overflow: 'hidden', 
                resize: 'none'
            });

            // Binds the textarea event handlers.
            textarea.unbind('.autoResizable')
            .bind('keydown.autoResizable', autoResize)
            .bind('keyup.autoResizable', autoResize)
            .bind('change.autoResizable', autoResize)
            .bind('focus.autoResizable', init)
            .bind('blur.autoResizable', uninit);
        });
    };
})(jQuery);

/**
 * PrimeFaces InputText Widget
 */
PrimeFaces.widget.InputText = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    //Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.jq);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.InputText, PrimeFaces.widget.BaseWidget);

/**
 * PrimeFaces InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    var _self = this;
    
    //Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.jq);
    }

    //AutoResize
    if(this.cfg.autoResize) {
        this.jq.autoResizable({
            maxHeight: this.cfg.maxHeight
            ,
            animateDuration: this.cfg.effectDuration
        });
    }
    
    //max length
    if(this.cfg.maxLength){
        //backspace, tab, pageup/down, end, arrows..
        var ignore = [8,9,33,34,35,36,37,38,39,40,46];
        this.jq.keydown(function(e){
            return $(this).val().length < _self.cfg.maxLength 
            || $.inArray(e.which, ignore) !== -1
            || e.metaKey;
        });
    }

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.InputTextarea, PrimeFaces.widget.BaseWidget);

/**
 * PrimeFaces SelectOneMenu Widget
 */
PrimeFaces.widget.SelectOneMenu = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.panelId = this.jqId + '_panel';
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');
    this.labelContainer = this.jq.find('.ui-selectonemenu-label-container');
    this.label = this.jq.find('.ui-selectonemenu-label');
    this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');
    this.triggers = this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');
    this.panel = this.jq.children(this.panelId);
    this.disabled = this.jq.hasClass('ui-state-disabled');
    this.tabindex = this.labelContainer.attr("tabindex") || 0;
    this.itemContainer = this.panel.children('.ui-selectonemenu-items');
    this.items = this.itemContainer.find('.ui-selectonemenu-item');

    if(!this.cfg.effectDuration) {
        this.cfg.effectDuration = 400;
    }

    this.bindEvents();

    if(this.disabled)
        this.labelContainer.attr("tabindex", -1);

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    //Panel management
    $(document.body).children(this.panelId).remove();
    this.panel.appendTo(document.body);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectOneMenu, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectOneMenu.prototype.bindEvents = function() {

    var options = $(this.input).children('option'),
    _self = this;

    //Events for items
    this.items.mouseover(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active')) {
            $(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active')) {
            $(this).removeClass('ui-state-hover');
        }
    }).click(function() {
        var element = $(this),
        option = $(options.get(element.index())),
        label = option.text();
        
        _self.items.removeClass('ui-state-active ui-state-hover');
        element.addClass('ui-state-active');

        option.attr('selected', 'selected');
        
        if(label && $.trim(label))
            _self.label.text(label);
        else
            _self.label.html('&nbsp;');

        _self.labelContainer.focus();
        _self.input.change();
        _self.hide();
    });

    //Events to show/hide the panel
    this.triggers.mouseover(function() {
        if(!_self.disabled) {
            _self.triggers.addClass('ui-state-hover');
        }
    }).mouseout(function() {
        if(!_self.disabled) {
            _self.triggers.removeClass('ui-state-hover');
        }
    }).click(function(e) {
        
        if(!_self.disabled) {
            if(_self.panel.is(":hidden"))
                _self.show();
            else
                _self.hide();
        }
        _self.labelContainer.focus();
        e.preventDefault();
    });

    var offset;

    //hide overlay when outside is clicked
    $(document.body).bind('click', function (e) {
        if (_self.panel.is(":hidden")) {
            return;
        }
        offset = _self.panel.offset();
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
        _self.hide();
    });

    this.labelContainer.focus(function(){
        if(!_self.disabled){
            _self.triggers.addClass('ui-state-focus');
        }
    }).blur(function(){
        if(!_self.disabled){
            _self.triggers.removeClass('ui-state-focus');
        }
    });

    //key bindings
    this.highlightItems = [];
    this.highlightKeyPath = '';
    this.highlightItem = null;
    this.highlightTimer = null;

    this.labelContainer.keydown(function(e) {
        if(_self.disabled)
            return;
        
        if(_self.highlightTimer != null)
            clearTimeout(_self.highlightTimer);

        _self.highlightTimer = setTimeout(function(){
            _self.highlightKeyPath = '';
        }, 1000);

        var keyCode = $.ui.keyCode;

        switch (e.which) {
            case keyCode.UP:
            case keyCode.LEFT:
                var highlightedItem = _self.items.filter('.ui-state-active'), prev;
                if(highlightedItem.length > 0) 
                    prev = highlightedItem.removeClass('ui-state-active').prev();

                if(!prev || prev.length == 0) 
                    prev = _self.items.eq(_self.items.length - 1);
                
                _self.selectItem(prev);

                e.preventDefault();
                break;

            case keyCode.DOWN:
            case keyCode.RIGHT:
                var highlightedItem = _self.items.filter('.ui-state-active'), next;
                
                if(highlightedItem.length > 0) 
                    next = highlightedItem.removeClass('ui-state-active').next();

                if(!next || next.length == 0) 
                    next = _self.items.eq(0);
                
                _self.selectItem(next);

                e.preventDefault();
                break;
                
            case keyCode.ENTER:
            case keyCode.NUMPAD_ENTER:
                if(_self.panel.is(":visible"))
                    _self.items.filter('.ui-state-active').click();
                else
                    _self.show();
                break;
            
            case keyCode.ALT: 
            case 224:
                break;
            case keyCode.TAB:
                _self.hide();
            default:
                var letter = String.fromCharCode(e.keyCode).toLowerCase();
                options = $(_self.input).children('option');

                if( _self.highlightKeyPath != letter ){

                    _self.highlightKeyPath += letter;
                    _self.highlightItems = [];
                    // find matches
                    for( var index = 0 ; index < options.length; index++){
                        if(options[index].text.toLowerCase().startsWith(_self.highlightKeyPath))
                            _self.highlightItems.push(_self.items.eq(index));
                    }
                }

                // no change
                if(_self.highlightItems.length < 1)
                    return;

                if(_self.highlightItem ){

                    // similar
                    if($(_self.highlightItem).html().toLowerCase().startsWith(_self.highlightKeyPath)){
                        if(_self.highlightKeyPath.length < 2){
                            var i = 0;
                            for( ; i < _self.highlightItems.length && $(_self.highlightItems[i]).html() != $(_self.highlightItem).html(); i++);
                            _self.highlightIndex = i + 1;
                        }
                        else
                            return;
                    }
                    else{ // not similar

                        var o = _self.items.index(_self.highlightItem);
                        var n = _self.items.index(_self.highlightItems[0]);

                        // find nearest
                        for( var i = 0; i < _self.highlightItems.length && _self.items.index(_self.highlightItems[i]) < o ; i++);
                        _self.highlightIndex = i;
                    }
                }
                else{ // new
                    _self.highlightIndex = 0;
                }

                //round
                if(_self.highlightIndex == _self.highlightItems.length) {
                    _self.highlightIndex = 0;
                }

                _self.highlightItem = _self.highlightItems[_self.highlightIndex];
                _self.selectItem(_self.highlightItem);
        };

        e.preventDefault();
    });
}


PrimeFaces.widget.SelectOneMenu.prototype.selectItem = function(item){
    if(!item || !item.length || item.length == 0)
        return;
    
    var yScrolled = this.panel.height() < this.itemContainer.height();

    //closed panel
    if(this.panel.is(":hidden"))
        item.click();
    else{
        this.items.removeClass("ui-state-active");
        item.addClass('ui-state-active');
        
        // check & align up/down overflow
        if(yScrolled){
            var diff = item.offset().top + item.outerHeight(true) - this.panel.offset().top;
            if( diff > this.panel.height() )
                this.panel.scrollTop(this.panel.scrollTop() + (diff - this.panel.height()));
            else if( (diff -= item.outerHeight(true)*2 - item.height()) < 0 )
                this.panel.scrollTop( this.panel.scrollTop() + diff);
        }
    }
}

PrimeFaces.widget.SelectOneMenu.prototype.show = function() {
    this.alignPanel();
    
    this.panel.css('z-index', '100000');
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '99999');
    }

    this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);

    //rematch selection
    this.selectItem(this.items.filter(".ui-state-active"));
}

PrimeFaces.widget.SelectOneMenu.prototype.hide = function() {
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '');
    }
    
    //rematch selection
    this.items.removeClass("ui-state-active")
        .eq($(this.input).children('option:selected').index())
        .addClass("ui-state-active");
    
    this.panel.css('z-index', '').hide();
}

PrimeFaces.widget.SelectOneMenu.prototype.disable = function() {
    this.disabled = true;
    this.jq.addClass('ui-state-disabled');
    this.labelContainer.attr("tabindex", -1);
}

PrimeFaces.widget.SelectOneMenu.prototype.enable = function() {
    this.disabled = false;
    this.jq.removeClass('ui-state-disabled');
    this.labelContainer.attr("tabindex", this.tabindex);
}

PrimeFaces.widget.SelectOneMenu.prototype.focus = function() {
    this.labelContainer.focus();
}

PrimeFaces.widget.SelectOneMenu.prototype.blur = function() {
    this.labelContainer.blur();
}

PrimeFaces.widget.SelectOneMenu.prototype.alignPanel = function() {
    var offset = this.jq.offset(),
    panelWidth = this.panel.width(),
    buttonWidth = this.jq.width();
    
    this.panel.css({
        'top':  offset.top + this.jq.outerHeight(),
        'left': offset.left
    });
    
    if(panelWidth < buttonWidth) {
        this.panel.width(buttonWidth);
    }
}

/**
 * PrimeFaces SelectOneRadio Widget
 */
PrimeFaces.widget.SelectOneRadio = function(cfg) {
    this.id = cfg.id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);
    this.output = this.jq.find('.ui-radiobutton-box');
    this.labels = this.jq.find('label');
    this.icons = this.jq.find('.ui-radiobutton-icon');

    var _self = this;

    this.output.mouseover(function() {
        if(!_self.cfg.disabled) {
            jQuery(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        if(!_self.cfg.disabled) {
            jQuery(this).removeClass('ui-state-hover');
        }
    }).click(function() {
        if(!_self.cfg.disabled) {

            //unselect all
            _self.output.removeClass('ui-state-active');
            _self.icons.removeClass('ui-icon ui-icon-bullet');

            //select current
            var element = jQuery(this),
            input = element.prev().children('input'),
            checked = input.attr('checked');

            if(checked && _self.cfg.unselectable) {
                element.removeClass('ui-state-active');
                input.removeAttr('checked');
                element.children('.ui-radiobutton-icon').removeClass('ui-icon ui-icon-bullet');
            } else {
                element.addClass('ui-state-active');
                input.attr('checked', 'checked');
                element.children('.ui-radiobutton-icon').addClass('ui-icon ui-icon-bullet');
            }

            input.change();
        }
    });

    this.labels.click(function(e) {
        if(!_self.cfg.disabled) {
            e.preventDefault();

            var element = jQuery(this),
            input = jQuery(PrimeFaces.escapeClientId(element.attr('for'))),
            radiobutton = input.parent().next();

            radiobutton.click();
        }
    });

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectOneRadio, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectOneRadio.prototype.enable = function() {
    this.jq.find('.ui-radiobutton').removeClass('ui-state-disabled');
    this.cfg.disabled = false;
}

PrimeFaces.widget.SelectOneRadio.prototype.disable = function() {
    this.jq.find('.ui-radiobutton').addClass('ui-state-disabled');
    this.cfg.disabled = true;
}

/**
 * PrimeFaces SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanCheckbox = function(cfg) {
    this.id = cfg.id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);
    this.input = jQuery(this.jqId + '_input');
    this.output = this.jq.find('.ui-checkbox-box');
    this.icon = this.output.find('.ui-checkbox-icon');
    this.disabled = this.isDisabled();
    
    var _self = this;

    this.output.mouseover(function() {
        if(!_self.disabled) {
            _self.output.addClass('ui-state-hover');
        }
    }).mouseout(function() {
        if(!_self.disabled) {
            _self.output.removeClass('ui-state-hover');
        }
    }).click(function() {
        if(!_self.disabled) {
            var checked = _self.output.hasClass('ui-state-active');

            if(checked) {
                _self.output.removeClass('ui-state-active');
                _self.input.removeAttr('checked');
                _self.icon.removeClass('ui-icon ui-icon-check');
            } else {
                _self.output.addClass('ui-state-active');
                _self.input.attr('checked', 'checked');
                _self.icon.addClass('ui-icon ui-icon-check');
            }

            _self.input.change();   //delegate event
        }
    });

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectBooleanCheckbox, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectBooleanCheckbox.prototype.isDisabled = function() {
    return this.jq.hasClass('ui-state-disabled');
}

PrimeFaces.widget.SelectBooleanCheckbox.prototype.enable = function() {
    this.jq.removeClass('ui-state-disabled');
    this.disabled = false;
}

PrimeFaces.widget.SelectBooleanCheckbox.prototype.disable = function() {
    this.jq.addClass('ui-state-disabled');
    this.disabled = true;
}

/**
 * PrimeFaces SelectManyCheckbox Widget
 */
PrimeFaces.widget.SelectManyCheckbox = function(cfg) {
    this.id = cfg.id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);
    this.output = this.jq.find('.ui-checkbox-box');
    this.labels = this.jq.find('label');

    var _self = this;

    this.output.mouseover(function() {
        var element = jQuery(this);
        if(!element.parent().hasClass('ui-state-disabled')) {
            element.addClass('ui-state-hover');
        }
    }).mouseout(function() {
        var element = jQuery(this);
        if(!element.parent().hasClass('ui-state-disabled')) {
            element.removeClass('ui-state-hover');
        }
    }).click(function() {
        var element = jQuery(this),
        input = element.prev().children('input'),
        checked = input.attr('checked');

        if(element.parent().hasClass('ui-state-disabled')) {
            return;
        }
        else if(checked) {
            _self.uncheck(_self.output.index(element));
        } else {
            _self.check(_self.output.index(element));
        }
    });

    this.labels.click(function(e) {
            e.preventDefault();
            var element = jQuery(this),
            input = jQuery(PrimeFaces.escapeClientId(element.attr('for'))),
            checkbox = input.parent().next();

            checkbox.click();
    });

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectManyCheckbox, PrimeFaces.widget.BaseWidget);

//index selector. extends JQuery find.
$.fn.findByIndex = function(selector, index){
    var elements = this.find(selector);
    if(index == undefined)
        return elements;
    else if( typeof(index) == "number"){
        return elements.eq(index);
    }
    else{
        var arr = typeof(index) == "string" ? eval("[" + index + "]" ) : index;
        return !arr.length ? elements : elements.filter(function(i){
           return $.inArray(i, arr) > -1; 
        });
    }
}

PrimeFaces.widget.SelectManyCheckbox.prototype.isChecked = function(index){
    var result = true;
    
    this.jq.findByIndex('.ui-checkbox-box', index).each(function(i, item){
        if(result)
            result = $(item).prev().children('input').is(":checked");
    });

    return result;
}

PrimeFaces.widget.SelectManyCheckbox.prototype.isDisabled = function(index){
    var result = true;
    
    this.jq.findByIndex('.ui-checkbox', index).each(function(i, item){
        if(result)
            result = $(item).hasClass("ui-state-disabled");
    });

    return result;
}

PrimeFaces.widget.SelectManyCheckbox.prototype.enable = function(index) {
    var boxes = this.jq.findByIndex('.ui-checkbox', index);
    boxes.removeClass('ui-state-disabled');
}

PrimeFaces.widget.SelectManyCheckbox.prototype.disable = function(index) {
    var boxes = this.jq.findByIndex('.ui-checkbox', index);
    boxes.addClass('ui-state-disabled');
}

//check custom checkbox(es)
PrimeFaces.widget.SelectManyCheckbox.prototype.check = function(index) {
    this.jq.findByIndex('.ui-checkbox-box', index).each(function(i, item){
        var element = $(item),
        input = element.prev().children('input');
        if(input.is(":checked"))
            return;
        
        input.change();
        input.attr('checked', 'checked');
        element.children('.ui-checkbox-icon').addClass('ui-icon ui-icon-check');
    });
}

//uncheck custom checkbox(es)
PrimeFaces.widget.SelectManyCheckbox.prototype.uncheck = function(index) {
    this.jq.findByIndex('.ui-checkbox-box', index).each(function(i, item){
        var element = $(item),
        input = element.prev().children('input');
        if(!input.is(":checked"))
            return;
        
        input.change();
        input.removeAttr('checked');
        element.children('.ui-checkbox-icon').removeClass('ui-icon ui-icon-check');
    });
}

/**
 * PrimeFaces SelectListbox Widget
 */
PrimeFaces.widget.SelectListbox = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');

    var listContainer = this.jq.children('ul'),
    options = $(this.input).children('option'),
    _self = this;

    options.each(function(i) {
        var option = $(this),
        selected = option.attr('selected'),
        styleClass = 'ui-selectlistbox-item ui-corner-all';

        if(selected) {
            styleClass = styleClass + ' ui-state-active';
        }
       
        listContainer.append('<li class="' + styleClass + '">' + option.text() + '</li>');
    });

    var items = listContainer.children('li');

    items.mouseover(function() {
        var element = jQuery(this);
        if(!_self.cfg.disabled && !element.hasClass('ui-state-active')) {
            $(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        var element = jQuery(this);
        if(!_self.cfg.disabled && !element.hasClass('ui-state-active')) {
            $(this).removeClass('ui-state-hover');
        }
    }).click(function() {
        if(!_self.cfg.disabled) {
            var element = jQuery(this),
            option = jQuery(options.get(element.index()));

            if(element.hasClass('ui-state-active')) {
                element.removeClass('ui-state-active ui-state-hover');
                option.removeAttr('selected');
            }
            else {
                if(_self.cfg.selection == 'single') {
                    items.removeClass('ui-state-active ui-state-hover');
                    options.removeAttr('selected');
                }

                element.addClass('ui-state-active');
                option.attr('selected', 'selected')
            }

            _self.input.change();
        }
    });

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectListbox, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectListbox.prototype.enable = function() {
    this.jq.removeClass('ui-state-disabled');
    this.cfg.disabled = false;
}

PrimeFaces.widget.SelectListbox.prototype.disable = function() {
    this.jq.addClass('ui-state-disabled');
    this.cfg.disabled = true;
}

/* 
 * PrimeFaces CommandButton Widget
 */
PrimeFaces.widget.CommandButton = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
	
	this.jq.button(this.cfg);
	
	if(this.jq.attr('title') === 'ui-button') {
        this.jq.removeAttr('title');
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.CommandButton, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.CommandButton.prototype.disable = function() {
    this.jq.button('disable');
}

PrimeFaces.widget.CommandButton.prototype.enable = function() {
    this.jq.button('enable');
}

/*
 * PrimeFaces Button Widget
 */
PrimeFaces.widget.Button = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

	$(this.jqId).button(this.cfg);
    
    if(this.jq.attr('title') === 'ui-button') {
        this.jq.removeAttr('title');
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Button, PrimeFaces.widget.BaseWidget);