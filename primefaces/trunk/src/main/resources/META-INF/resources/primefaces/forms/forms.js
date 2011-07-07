/*!
 * jQuery Animated Auto-Resizable Textarea Plugin v1.0
 *
 * Copyright (c) 2009 - 2010 Wayne Haffenden
 * http://www.waynehaffenden.com/Blog/jQuery-AutoResizable-Plugin
 *
 * $Id: jquery.autoResizable.js, v 1.0 2009-12-30 01:53:14Z whaffenden $
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */
;(function($) {
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
                    textarea.css({overflow: 'hidden', overflowY: 'hidden'});
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
                    textarea.stop().animate({height: newHeight}, animateDuration, function() {
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
            textarea.css({overflow: 'hidden', resize: 'none'});

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
    this.id = cfg.id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    //Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.jq);
    }
}

/**
 * PrimeFaces InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    //Visuals
    if(this.cfg.theme != false) {
        PrimeFaces.skinInput(this.jq);
    }

    //AutoResize
    if(this.cfg.autoResize) {
        this.jq.autoResizable({
            maxHeight: this.cfg.maxHeight
            ,animateDuration: this.cfg.effectDuration
        });
    }

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }
}

/**
 * PrimeFaces SelectOneMenu Widget
 */
PrimeFaces.widget.SelectOneMenu = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.input = $(this.jqId + '_input');
    this.labelContainer = this.jq.find('.ui-selectonemenu-label-container');
    this.label = this.jq.find('.ui-selectonemenu-label');
    this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');
    this.triggers = this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');
    this.panel = this.jq.children('.ui-selectonemenu-panel');
    this.disabled = this.jq.hasClass('ui-state-disabled');

    if(!this.cfg.effectDuration) {
        this.cfg.effectDuration = 400;
    }

    this.bindEvents();

    this.panel.hide().removeClass('ui-helper-hidden-accessible');

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
}

String.prototype.startsWith = function(str){
    return (this.indexOf(str) === 0);
}

        
PrimeFaces.widget.SelectOneMenu.prototype.bindEvents = function() {

    var itemContainer = this.panel.children('.ui-selectonemenu-items'),
    items = itemContainer.find('.ui-selectonemenu-item'),
    options = $(this.input).children('option'),
    _self = this;

    //Events for items
    items.mouseover(function() {
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
        option = $(options.get(element.index()));

        items.removeClass('ui-state-active ui-state-hover');
        element.addClass('ui-state-active');

        option.attr('selected', 'selected');

        _self.labelContainer.focus();
        _self.label.html(option.text());
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
        
        if(_self.highlightTimer != null)
            clearTimeout(_self.highlightTimer);

        _self.highlightTimer = setTimeout(function(){
            _self.highlightKeyPath = '';
        }, 1000);

        var keyCode = $.ui.keyCode;

        switch (e.which) {
            case keyCode.UP:
            case keyCode.LEFT:
                var highlightedItem = items.filter('.ui-state-active'),
                previousItem = highlightedItem.prev();

                if(previousItem.length > 0) {
                    highlightedItem.removeClass('ui-state-active');
                    previousItem.addClass('ui-state-active');
                }
                break;

            case keyCode.DOWN:
            case keyCode.RIGHT:
                var highlightedItem = items.filter('.ui-state-active'),
                nextItem = highlightedItem.next();

                if(nextItem.length > 0) {
                    highlightedItem.removeClass('ui-state-active');
                    nextItem.addClass('ui-state-active');
                }
                break;

            case keyCode.ENTER:
            case keyCode.NUMPAD_ENTER:
                if(_self.panel.is(":visible"))
                    items.filter('.ui-state-active').click();
                else
                    _self.show();
                break;
            
            case keyCode.ALT: 
            case 224: break;
            case keyCode.TAB: _self.hide();
            default:
                var letter = String.fromCharCode(e.keyCode).toLowerCase();
                options = $(_self.input).children('option');

                if( _self.highlightKeyPath != letter ){

                     _self.highlightKeyPath += letter;
                     _self.highlightItems = [];
                     // find matches
                    for( var index = 0 ; index < options.length; index++){
                        if(options[index].text.toLowerCase().startsWith(_self.highlightKeyPath))
                            _self.highlightItems.push(items.eq(index));
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

                        var o = items.index(_self.highlightItem);
                        var n = items.index(_self.highlightItems[0]);

                        // find nearest
                        for( var i = 0; i < _self.highlightItems.length && items.index(_self.highlightItems[i]) < o ; i++);
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
                items.removeClass('ui-state-active');   //clear previous highlighted ones if any
                _self.highlightItem.addClass('ui-state-active');
        };

        e.preventDefault();
    });
}

PrimeFaces.widget.SelectOneMenu.prototype.show = function() {
    this.panel.css('z-index', '100000');
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '99999');
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
}

PrimeFaces.widget.SelectOneMenu.prototype.enable = function() {
    this.disabled = false;
    this.jq.removeclass('ui-state-disabled');
}

PrimeFaces.widget.SelectOneMenu.prototype.focus = function() {
    this.labelContainer.focus();
}

PrimeFaces.widget.SelectOneMenu.prototype.blur = function() {
    this.labelContainer.blur();
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
}

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
}

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
        if(!_self.cfg.disabled) {
            jQuery(this).addClass('ui-state-hover');
        }
    }).mouseout(function() {
        if(!_self.cfg.disabled) {
            jQuery(this).removeClass('ui-state-hover');
        }
    }).click(function() {
        if(!_self.cfg.disabled) {
            var element = jQuery(this),
            input = element.prev().children('input'),
            checked = input.attr('checked');

            if(checked) {
                element.removeClass('ui-state-active');
                input.removeAttr('checked');
                element.children('.ui-checkbox-icon').removeClass('ui-icon ui-icon-check');
            } else {
                element.addClass('ui-state-active');
                input.attr('checked', 'checked');
                element.children('.ui-checkbox-icon').addClass('ui-icon ui-icon-check');
            }

            input.change();
        }
    });

    this.labels.click(function(e) {
        if(!_self.cfg.disabled) {
            e.preventDefault();

            var element = jQuery(this),
            input = jQuery(PrimeFaces.escapeClientId(element.attr('for'))),
            checkbox = input.parent().next();

            checkbox.click();
        }
    });

    //Client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }
}

PrimeFaces.widget.SelectManyCheckbox.prototype.enable = function() {
    this.jq.find('.ui-checkbox').removeClass('ui-state-disabled');
    this.cfg.disabled = false;
}

PrimeFaces.widget.SelectManyCheckbox.prototype.disable = function() {
    this.jq.find('.ui-checkbox').addClass('ui-state-disabled');
    this.cfg.disabled = true;
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
}

PrimeFaces.widget.SelectListbox.prototype.enable = function() {
    this.jq.removeClass('ui-state-disabled');
    this.cfg.disabled = false;
}

PrimeFaces.widget.SelectListbox.prototype.disable = function() {
    this.jq.addClass('ui-state-disabled');
    this.cfg.disabled = true;
}