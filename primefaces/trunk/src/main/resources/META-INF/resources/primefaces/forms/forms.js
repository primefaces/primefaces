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
    this.input = $(this.jqId + '_input');
    var _self = this;
    
    //Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.input);
    }

    //AutoResize
    if(this.cfg.autoResize) {
        this.input.autoResizable({
            maxHeight: this.cfg.maxHeight,
            animateDuration: this.cfg.effectDuration
        });
    }
    
    //max length
    if(this.cfg.maxlength){
        //backspace, tab, pageup/down, end, arrows..
        var ignore = [8,9,33,34,35,36,37,38,39,40,46];
        this.input.keydown(function(e){
            return $(this).val().length < _self.cfg.maxlength 
            || $.inArray(e.which, ignore) !== -1
            || e.metaKey;
        });
    }

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.InputTextarea, PrimeFaces.widget.BaseWidget);

/**
 * PrimeFaces SelectOneMenu Widget
 */
PrimeFaces.widget.SelectOneMenu = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
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
    this.options = this.input.children('option');
    this.items = this.itemContainer.find('.ui-selectonemenu-item');
    this.cfg.effectDuration = this.cfg.effectDuration||400;
    var _self = this;

    //disable options
    this.options.filter(':disabled').each(function() {
        _self.itemContainer.children().eq($(this).index()).addClass('ui-state-disabled');
    });

    //populate label and activate selected item
    var selectedOption = this.options.filter(':selected'),
    label = selectedOption.text();
    if(label == '') {
        this.label.html('&nbsp;');
    } else {
        this.label.text(label);
    }
    this.items.eq(selectedOption.index()).addClass('ui-state-active');
    
    this.bindEvents();

    //disable tabbing if disabled
    if(this.disabled) {
        this.labelContainer.attr("tabindex", -1);
    }

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    //Append panel to body
    $(document.body).children(this.panelId).remove();
    this.panel.appendTo(document.body);
            
    //Hide overlay on resize
    var resizeNS = 'resize.' + this.id;
    $(window).unbind(resizeNS).bind(resizeNS, function() {
        if(_self.panel.is(':visible')) {
            _self.hide();
        }
    });
    
    //dialog support
    this.setupDialogSupport();
    
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
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectOneMenu, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectOneMenu.prototype.setupDialogSupport = function() {
    var dialog = this.jq.parents('.ui-dialog:first');
    
    if(dialog.length == 1) {
        var dialogWidget = dialog.data('widget'),
        _self = this;
        
        _self.panel.css('position', 'fixed');
        _self.triggers.mousedown(function(e) {
            dialogWidget.moveToTop();
            _self.panel.css('z-index', ++PrimeFaces.zindex);
            e.stopPropagation();
        });
    }
}

PrimeFaces.widget.SelectOneMenu.prototype.initWidths = function() {
    this.jq.width(this.input.outerWidth());
    var jqWidth = this.jq.innerWidth();
    
    //align panel and container
    if(this.panel.outerWidth() < jqWidth) {
        this.panel.width(jqWidth);
    }
}

PrimeFaces.widget.SelectOneMenu.prototype.bindEvents = function() {
    var _self = this;

    //Events for items
    this.items.mouseover(function() {
        var item = $(this);
        if(!item.hasClass('ui-state-disabled'))
            _self.highlightItem(item);
    }).click(function() {
        var item = $(this);
        if(!item.hasClass('ui-state-disabled'))
            _self.selectItem(item);
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
        
        _self.triggers.removeClass('ui-state-hover').addClass('ui-state-focus');
        _self.labelContainer.focus();
        e.preventDefault();
    });


    var offset;
    //hide overlay when outside is clicked
    $(document.body).bind('mousedown.ui-selectonemenu', function (e) {
        if(_self.panel.is(":hidden")) {
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
    
    //on tab receive
    this.input.focus(function() {
       _self.labelContainer.focus();
    });
    
    //key bindings
    this.bindKeyEvents();
}

PrimeFaces.widget.SelectOneMenu.prototype.highlightItem = function(item) {
    this.unhighlightItem(this.items.filter('.ui-state-active'));
    item.addClass('ui-state-active');
    
    this.alignScroller(item);
}

PrimeFaces.widget.SelectOneMenu.prototype.unhighlightItem = function(item) {
    item.removeClass('ui-state-active');
}

PrimeFaces.widget.SelectOneMenu.prototype.selectItem = function(item) {
    var option = this.options.eq(item.index()),
    optionLabel = option.text();
    
    if(!option.is(':selected')) {
        //select item
        this.unhighlightItem(this.items.filter('.ui-state-active'));
        item.addClass('ui-state-active');
        option.attr('selected', 'selected');

        if($.trim(optionLabel) != '')
            this.label.text(optionLabel);
        else
            this.label.html('&nbsp;');
        
        this.input.change();
    }

    this.labelContainer.focus();
    this.hide();
}

PrimeFaces.widget.SelectOneMenu.prototype.bindKeyEvents = function() {
    this.highlightItems = [];
    this.highlightKeyPath = '';
    this.highlightOption = null;
    this.highlightTimer = null;
    
    var _self = this;

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
                var highlightedItem = _self.items.filter('.ui-state-active'),
                prev = highlightedItem.prevAll(':not(.ui-state-disabled):first');
                
                if(prev.length == 1) {
                    if(_self.panel.is(':visible'))
                       _self.highlightItem(prev);
                   else
                        _self.selectItem(prev);
                }
                
                e.preventDefault();
                break;

            case keyCode.DOWN:
            case keyCode.RIGHT:
                var highlightedItem = _self.items.filter('.ui-state-active'), 
                next = highlightedItem.nextAll(':not(.ui-state-disabled):first');
                
                if(next.length == 1) {
                   if(_self.panel.is(':visible'))
                       _self.highlightItem(next);
                   else
                        _self.selectItem(next);
                }
                
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
                e.preventDefault();
                break;
            case keyCode.TAB:
                var highlightedItem = _self.items.filter('.ui-state-active');
                
                _self.selectItem(highlightedItem);
            default:
                var letter = String.fromCharCode(e.keyCode).toLowerCase();

                if( _self.highlightKeyPath != letter ){

                    _self.highlightKeyPath += letter;
                    _self.highlightItems = [];
                    // find matches
                    for( var index = 0 ; index < _self.options.length; index++){
                        if(_self.options[index].text.toLowerCase().startsWith(_self.highlightKeyPath))
                            _self.highlightItems.push(_self.items.eq(index));
                    }
                }

                // no change
                if(_self.highlightItems.length < 1)
                    return;

                if(_self.highlightOption){

                    // similar
                    if($(_self.highlightOption).html().toLowerCase().startsWith(_self.highlightKeyPath)){
                        if(_self.highlightKeyPath.length < 2){
                            var i = 0;
                            for( ; i < _self.highlightItems.length && $(_self.highlightItems[i]).html() != $(_self.highlightOption).html(); i++);
                            _self.highlightIndex = i + 1;
                        }
                        else
                            return;
                    }
                    else{ // not similar

                        var o = _self.items.index(_self.highlightOption);
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

                _self.highlightOption = _self.highlightItems[_self.highlightIndex];
                _self.selectItem(_self.highlightOption);
                
                e.preventDefault();
        };
        
        e.preventDefault();
    }); 
}
                    
PrimeFaces.widget.SelectOneMenu.prototype.alignScroller = function(item) {
    if(this.panel.height() < this.itemContainer.height()){
        var diff = item.offset().top + item.outerHeight(true) - this.panel.offset().top;
        
        if(diff > this.panel.height()) {
            this.panel.scrollTop(this.panel.scrollTop() + (diff - this.panel.height()));
        }
        else if((diff -= item.outerHeight(true)*2 - item.height()) < 0) {
            this.panel.scrollTop( this.panel.scrollTop() + diff);
        }
    }
}

PrimeFaces.widget.SelectOneMenu.prototype.show = function() {
    //highlight current
    this.highlightItem(this.items.eq(this.options.filter(':selected').index()));
    
    //calculate panel position
    this.alignPanel();
    
    this.panel.css('z-index', ++PrimeFaces.zindex);
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
    }

    this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
}

PrimeFaces.widget.SelectOneMenu.prototype.hide = function() {
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '');
    }
    
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
    var fixedPosition = this.panel.css('position') == 'fixed',
    win = $(window),
    positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;
    
    this.panel.css({left:'', top:''}).position({
                                    my: 'left top'
                                    ,at: 'left bottom'
                                    ,of: this.jq
                                    ,offset : positionOffset
                                });
}

/**
 * PrimeFaces SelectOneRadio Widget
 */
PrimeFaces.widget.SelectOneRadio = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.output = this.jq.find('.ui-radiobutton-box:not(.ui-state-disabled)');
    this.inputs = this.jq.find(':radio:not(:disabled)');
    this.labels = this.jq.find('label:not(.ui-state-disabled)');
    this.icons = this.jq.find('.ui-radiobutton-icon');

    var _self = this;

    this.output.mouseover(function() {
        var radio = $(this);
        if(!radio.hasClass('ui-state-active'))
            $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    }).click(function() {
        var radio = $(this);
        if(!radio.hasClass('ui-state-active')) 
            _self.check($(this));
    });

    //selects radio when label is clicked
    this.labels.click(function(e) {
        var target = $(PrimeFaces.escapeClientId($(this).attr('for'))),
        radio = null;
        
        //checks if target is input or not(custom labels)
        if(target.is(':input'))
            radio = target.parent().siblings('.ui-radiobutton-box');
        else
            radio = target; //custom layout

        if(!radio.hasClass('ui-state-active'))
            _self.check(radio);
    });

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectOneRadio, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectOneRadio.prototype.check = function(radio) {
    //unselect previous
    var previousRadio = this.output.filter('.ui-state-active'),
    previousInput = previousRadio.siblings('.ui-helper-hidden').children('input:radio');
    previousRadio.removeClass('ui-state-active').children('.ui-radiobutton-icon').removeClass('ui-icon ui-icon-bullet');
    previousInput.removeAttr('checked');

    //select current
    var input = radio.siblings('.ui-helper-hidden').children('input:radio');

    radio.addClass('ui-state-active');
    input.attr('checked', 'checked');
    radio.children('.ui-radiobutton-icon').addClass('ui-icon ui-icon-bullet');

    input.change();
}

/**
 * PrimeFaces SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanCheckbox = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
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
            _self.toggle();
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
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectBooleanCheckbox, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectBooleanCheckbox.prototype.toggle = function() {
    if(!this.disabled) {
        if(this.input.is(":checked"))
            this.uncheck();
        else
            this.check();
    }
}

PrimeFaces.widget.SelectBooleanCheckbox.prototype.check = function() {
    if(!this.disabled) {
        this.input.attr('checked', 'checked');
        this.box.addClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');

        this.input.change();
    }
}

PrimeFaces.widget.SelectBooleanCheckbox.prototype.uncheck = function() {
    if(!this.disabled) {
        this.input.removeAttr('checked');
        this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');

        this.input.change();
    }
}

/**
 * PrimeFaces SelectManyCheckbox Widget
 */
PrimeFaces.widget.SelectManyCheckbox = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.outputs = this.jq.find('.ui-chkbox-box:not(.ui-state-disabled)');
    this.inputs = this.jq.find(':checkbox:not(:disabled)');
    this.labels = this.jq.find('label:not(.ui-state-disabled)');
    var _self = this;

    this.outputs.mouseover(function() {
        $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    }).click(function() {
        _self.toggle($(this));
    });

    this.labels.click(function(e) {
        var element = $(this),
        input = $(PrimeFaces.escapeClientId(element.attr('for'))),
        checkbox = input.parent().next();
        checkbox.click();
        
        e.preventDefault();
    });

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectManyCheckbox, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectManyCheckbox.prototype.toggle = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        if(checkbox.hasClass('ui-state-active'))
            this.uncheck(checkbox);
        else
            this.check(checkbox);
    }
}

PrimeFaces.widget.SelectManyCheckbox.prototype.check = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
        checkbox.siblings('.ui-helper-hidden').children('input:checkbox').attr('checked', 'checked').change();
    }
}

PrimeFaces.widget.SelectManyCheckbox.prototype.uncheck = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
        checkbox.siblings('.ui-helper-hidden').children('input:checkbox').removeAttr('checked').change();
    }
}

/**
 * PrimeFaces SelectListbox Widget
 */
PrimeFaces.widget.SelectListbox = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');

    var listContainer = this.jq.children('ul'),
    options = $(this.input).children('option'),
    _self = this;

    //create elements for each option
    options.each(function(i) {
        var option = $(this),
        selected = option.is(':selected'),
        disabled = option.is(':disabled'),
        styleClass = 'ui-selectlistbox-item ui-corner-all';
        styleClass = disabled ? styleClass + ' ui-state-disabled' : styleClass;
        styleClass = selected ? styleClass + ' ui-state-active' : styleClass;
        
        listContainer.append('<li class="' + styleClass + '">' + option.text() + '</li>');
    });

    var items = listContainer.children('li:not(.ui-state-disabled)');

    items.mouseover(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active')) {
            $(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    }).click(function(e) {
        var element = $(this),
        option = $(options.get(element.index()));

        //clear previous selections
        if(_self.cfg.selection == 'single' || (_self.cfg.selection == 'multiple' && !e.metaKey)) {
            items.removeClass('ui-state-active ui-state-hover');
            options.removeAttr('selected');
        }
        
        if(_self.cfg.selection == 'multiple' && e.metaKey && element.hasClass('ui-state-active')) {
            element.removeClass('ui-state-active');
            option.removeAttr('selected');
        } 
        else {
            element.addClass('ui-state-active').removeClass('ui-state-hover');
            option.attr('selected', 'selected');
        }
        
        _self.input.change();
        
        PrimeFaces.clearSelection();
    });

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectListbox, PrimeFaces.widget.BaseWidget);

/* 
 * PrimeFaces CommandButton Widget
 */
PrimeFaces.widget.CommandButton = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    
    PrimeFaces.skinButton(this.jq);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.CommandButton, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.CommandButton.prototype.disable = function() {
    this.jq.addClass('ui-state-disabled').attr('disabled', 'disabled');
}

PrimeFaces.widget.CommandButton.prototype.enable = function() {
    this.jq.removeClass('ui-state-disabled').removeAttr('disabled');
}

/*
 * PrimeFaces Button Widget
 */
PrimeFaces.widget.Button = function(cfg) {
	this.cfg = cfg;
    this.id = this.cfg.id;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

	PrimeFaces.skinButton(this.jq);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Button, PrimeFaces.widget.BaseWidget);

/**
 * PrimeFaces RadioButton Widget
 */
PrimeFaces.widget.RadioButton = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.output = this.jq.find('.ui-radiobutton-box');
    this.input = this.jq.find('input:radio');
    this.icon = this.jq.find('.ui-radiobutton-icon');
    this.label = $('label[for="' + this.id + '"]');
    var _self = this;
    
    this.output.mouseover(function() {
        var radio = $(this);
        if(!radio.hasClass('ui-state-active') && !radio.hasClass('ui-state-disabled'))
            $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    }).click(function() {
        var radio = $(this);
        if(!radio.hasClass('ui-state-active') && !radio.hasClass('ui-state-disabled'))
            _self.check();
    });
    
    this.label.click(function(e) {
        _self.check();
    });
    
    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.RadioButton, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.RadioButton.prototype.check = function() {
    //unselect current
    var selectedRadioInput = $('input:radio[name="' + this.input.attr('name') + '"]').filter(':checked');
    if(selectedRadioInput.length > 0) {
        var radio = selectedRadioInput.parents('.ui-radiobutton:first');
        selectedRadioInput.removeAttr('checked');
        radio.children('.ui-radiobutton-box').removeClass('ui-state-active');
        radio.find('.ui-radiobutton-icon').removeClass('ui-icon ui-icon-bullet');
    }

    //select itself
    this.output.addClass('ui-state-active');
    this.input.attr('checked', 'checked');
    this.icon.addClass('ui-icon ui-icon-bullet');
    this.input.change();
}

/**
 * PrimeFaces SelecyManyButton Widget
 */
PrimeFaces.widget.SelectManyButton = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.buttons = this.jq.children('button:not(:disabled)');
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
}

PrimeFaces.widget.SelectManyButton.prototype.select = function(button) {
    button.removeClass('ui-state-hover').addClass('ui-state-active')
                            .children(':checkbox').attr('checked','checked').change();
    
}

PrimeFaces.widget.SelectManyButton.prototype.unselect = function(button) {
    button.removeClass('ui-state-active').addClass('ui-state-hover')
                            .children(':checkbox').removeAttr('checked').change()
}

/**
 * PrimeFaces SelecyOneButton Widget
 */
PrimeFaces.widget.SelectOneButton = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.buttons = this.jq.children('button:not(:disabled)');
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
}

PrimeFaces.widget.SelectOneButton.prototype.select = function(button) {
    this.unselect(this.buttons.filter('.ui-state-active'));
    
    button.addClass('ui-state-active').children(':radio').attr('checked','checked').change();
}

PrimeFaces.widget.SelectOneButton.prototype.unselect = function(button) {
    button.removeClass('ui-state-active ui-state-hover').children(':radio').removeAttr('checked').change();
}

/**
 * PrimeFaces SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanButton = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
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
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectBooleanButton, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectBooleanButton.prototype.toggle = function() {
    if(!this.disabled) {
        if(this.input.is(":checked"))
            this.uncheck();
        else
            this.check();
    }
}

PrimeFaces.widget.SelectBooleanButton.prototype.check = function() {
    if(!this.disabled) {
        this.input.attr('checked', 'checked');
        this.jq.addClass('ui-state-active').children('.ui-button-text').html(this.cfg.onLabel);
        
        if(this.icon.length > 0) {
            this.icon.removeClass(this.cfg.offIcon).addClass(this.cfg.onIcon);
        }

        this.input.change();
    }
}

PrimeFaces.widget.SelectBooleanButton.prototype.uncheck = function() {
    if(!this.disabled) {
        this.input.removeAttr('checked', 'checked');
        this.jq.removeClass('ui-state-active').children('.ui-button-text').html(this.cfg.offLabel);
        
        if(this.icon.length > 0) {
            this.icon.removeClass(this.cfg.onIcon).addClass(this.cfg.offIcon);
        }

        this.input.change();
    }
}

/** 
 * PrimeFaces SelectCheckboxMenu Widget
 */
PrimeFaces.widget.SelectCheckboxMenu = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.panelId = this.jqId + '_panel';
    this.jq = $(this.jqId);
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
    
    this.bindEvents();
    
    //Append panel to body
    $(document.body).children(this.panelId).remove();
    this.panel.appendTo(document.body);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.SelectCheckboxMenu, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.SelectCheckboxMenu.prototype.generateItems = function() {
    var _self = this;
    
    this.inputs.each(function() {
        var input = $(this),
        label = input.next(),
        disabled = input.is(':disabled'),
        boxClass = 'ui-chkbox-box ui-widget ui-corner-all ui-state-default';
        if(disabled) {
            boxClass += " ui-state-disabled";
        }

        var dom = '<li class="ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all">';
        dom += '<div class="ui-chkbox ui-widget"><div class="' + boxClass + '"><span class="ui-chkbox-icon"></span></div></div>';
        dom += '<label>' + label.html() +  '</label></li>';
        
        _self.itemContainer.append(dom);
    });
}

PrimeFaces.widget.SelectCheckboxMenu.prototype.bindEvents = function() {
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
    }).click(function(e) {
        if(!_self.disabled) {
            if(_self.panel.is(":hidden"))
                _self.show();
            else
                _self.hide();
        }
        
        _self.triggers.removeClass('ui-state-hover').addClass('ui-state-focus');
        e.preventDefault();
    });
    
    //hide overlay when outside is clicked
    $(document.body).bind('click.selectcheckboxmenu', function (e) {        
        //do nothing if event target is component itself
        if(e.target === _self.label.get(0) ||
            e.target === _self.menuIcon.get(0) ||
            e.target === _self.menuIcon.children().get(0)) {
            return;
        }
        
        //hide the panel and remove focus from label
        var offset = _self.panel.offset();
        if(e.pageX < offset.left ||
            e.pageX > offset.left + _self.panel.width() ||
            e.pageY < offset.top ||
            e.pageY > offset.top + _self.panel.height()) {
            
            _self.triggers.removeClass('ui-state-focus');
            
            if(_self.panel.is(":visible")) {
                _self.hide(true);
            }
        }
    });
    
    //Hide overlay on resize
    var resizeNS = 'resize.' + this.id;
    $(window).unbind(resizeNS).bind(resizeNS, function() {
        if(_self.panel.is(':visible')) {
            _self.hide(false);
        }
    });
}

PrimeFaces.widget.SelectCheckboxMenu.prototype.show = function() {    
    //calculate panel position
    this.alignPanel();
    
    this.panel.css('z-index', ++PrimeFaces.zindex);
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
    }

    this.panel.show();
}

PrimeFaces.widget.SelectCheckboxMenu.prototype.hide = function(animate) {
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '');
    }

    this.panel.css('z-index', '');
    this.triggers.removeClass('ui-state-focus');
    
    if(animate)
        this.panel.fadeOut('fast');
    else
        this.panel.hide();
}

PrimeFaces.widget.SelectCheckboxMenu.prototype.alignPanel = function() {
    this.panel.css({left:'', top:''})
    .position({
        my: 'left top'
        ,at: 'left bottom'
        ,of: this.jq
    });
}

PrimeFaces.widget.SelectCheckboxMenu.prototype.toggleItem = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        if(checkbox.hasClass('ui-state-active'))
            this.uncheck(checkbox);
        else
            this.check(checkbox);
    }
}

PrimeFaces.widget.SelectCheckboxMenu.prototype.check = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        var input = this.inputs.eq(checkbox.parents('li:first').index());
        checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
        input.attr('checked', 'checked').change();
    }
}

PrimeFaces.widget.SelectCheckboxMenu.prototype.uncheck = function(checkbox) {
    if(!checkbox.hasClass('ui-state-disabled')) {
        var input = this.inputs.eq(checkbox.parents('li:first').index());
        checkbox.removeClass('ui-state-active').addClass('ui-state-hover').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
        input.removeAttr('checked').change();
    }
}