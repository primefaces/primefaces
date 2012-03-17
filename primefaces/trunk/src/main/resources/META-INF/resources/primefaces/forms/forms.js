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

        //AutoResize
        if(this.cfg.autoResize) {
            this.setupAutoResize();
        }

        //max length
        if(this.cfg.maxlength){
            this.applyMaxlength();
        }

        //Client behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
        }
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
        });
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
        this.options = this.input.children('option');
        this.items = this.itemContainer.find('.ui-selectonemenu-item');
        this.cfg.effectDuration = this.cfg.effectDuration||400;
        var _self = this;

        //disable options
        this.options.filter(':disabled').each(function() {
            _self.itemContainer.children().eq($(this).index()).addClass('ui-state-disabled');
        });

        //set initial selected option
        var selectedOption = this.options.filter(':selected');
        this.selectedOption = selectedOption;
        this.label.val(selectedOption.text());

        //disable tabbing
        if(this.disabled) {
            this.input.attr("tabindex", -1);
        }
        else {
            if(!this.cfg.editable) {
                this.label.css('cursor', 'pointer').mousedown(function(e) {
                    e.preventDefault(); 
                });

                this.triggers = this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');
            } 
            else {
                this.triggers = this.jq.find('.ui-selectonemenu-trigger');
            }
            
            //mark trigger and descandants of trigger as a trigger for a primefaces overlay
            this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);
            
            this.bindEvents();
            
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
    },
    
    setupDialogSupport: function() {
        var dialog = this.jq.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            this.panel.css('position', 'fixed');
        }
    },
    
    initWidths: function() {
        this.jq.width(this.input.outerWidth(true) + 5);
        this.label.width(this.jq.width() - this.menuIcon.width());
        var jqWidth = this.jq.innerWidth();

        //align panel and container
        if(this.panel.outerWidth() < jqWidth) {
            this.panel.width(jqWidth);
        }
    },
    
    bindEvents: function() {
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
                _self.jq.addClass('ui-state-hover');
                _self.menuIcon.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            if(!_self.disabled) {
                _self.jq.removeClass('ui-state-hover');
                _self.menuIcon.removeClass('ui-state-hover');
            }
        }).click(function(e) {
            if(!_self.disabled) {
                if(_self.panel.is(":hidden"))
                    _self.show();
                else
                    _self.hide();
            }

            _self.jq.removeClass('ui-state-hover');
            _self.menuIcon.removeClass('ui-state-hover');          
            _self.input.focus();
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

        this.input.focus(function(){
            if(!_self.disabled){
                _self.jq.addClass('ui-state-focus');
                _self.menuIcon.addClass('ui-state-focus');
            }
        }).blur(function(){
            if(!_self.disabled){
                _self.jq.removeClass('ui-state-focus');
                _self.menuIcon.removeClass('ui-state-focus');

                //restore current selection if hidden select is changed via arrow keys
                _self.options.removeAttr('selected');
                _self.selectedOption.attr('selected', 'selected');
            }
        });

        //key bindings
        this.bindKeyEvents();
        
        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if(_self.panel.is(':visible')) {
                _self.hide();
            }
        });
    },
    
    highlightItem: function(item) {
        this.unhighlightItem(this.items.filter('.ui-state-highlight'));
        item.addClass('ui-state-highlight');

        this.alignScroller(item);
    },
    
    unhighlightItem: function(item) {
        item.removeClass('ui-state-highlight');
    },
    
    selectItem: function(item) {
        var newOption = this.options.eq(item.index());

        //unselect active item as current gets activated on show
        this.unhighlightItem(this.items.filter('.ui-state-highlight'));

        //select item if item is not already selected
        if(newOption.val() != this.selectedOption.val()) {
            this.options.removeAttr('selected');
            newOption.attr('selected', 'selected');

            this.label.val(newOption.text());
            this.selectedOption = newOption;

            this.fireChangeEvent();
        }

        this.input.focus();
        this.hide();
    },
    
    bindKeyEvents: function() {
        var _self = this;

        this.input.keyup(function(e) {
            if(_self.disabled)
                return;

            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key != keyCode.UP && key != keyCode.LEFT
                    && key != keyCode.DOWN && key != keyCode.RIGHT
                    && key != keyCode.ENTER && key != keyCode.NUMPAD_ENTER
                    && key != keyCode.ALT && key != keyCode.TAB) {

                    var selectedOption = _self.options.filter(':selected');

                    if(selectedOption.length > 0) {
                        _self.highlightItem(_self.items.eq(selectedOption.index()));
                    }
            }
        });

        this.input.keydown(function(e) {
            if(_self.disabled)
                return;

            var keyCode = $.ui.keyCode;

            switch(e.which) { 
                case keyCode.UP:
                case keyCode.LEFT:
                    var highlightedItem = _self.items.filter('.ui-state-highlight'),
                    prev = highlightedItem.prevAll(':not(.ui-state-disabled):first');

                    if(prev.length == 1) {
                        _self.highlightItem(prev);
                    }

                    e.preventDefault();
                    break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    var highlightedItem = _self.items.filter('.ui-state-highlight'), 
                    next = highlightedItem.nextAll(':not(.ui-state-disabled):first');

                    if(next.length == 1 && _self.panel.is(':visible')) {
                    _self.highlightItem(next);
                    }

                    e.preventDefault();
                    break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    if(_self.panel.is(":visible"))
                        _self.selectItem(_self.items.filter('.ui-state-highlight'));
                    else
                        _self.show();

                    e.preventDefault();
                    break;

                case keyCode.ALT: 
                case 224:
                    e.preventDefault();
                    break;

                case keyCode.TAB:
                    if(_self.panel.is(':visible')) {
                        _self.selectItem(_self.items.filter('.ui-state-highlight'));
                    }
                    break;

            };
        });
    },
    
    alignScroller: function(item) {
        if(this.panel.height() < this.itemContainer.height()){
            var diff = item.offset().top + item.outerHeight(true) - this.panel.offset().top;

            if(diff > this.panel.height()) {
                this.panel.scrollTop(this.panel.scrollTop() + (diff - this.panel.height()));
            }
            else if((diff -= item.outerHeight(true)*2 - item.height()) < 0) {
                this.panel.scrollTop( this.panel.scrollTop() + diff);
            }
        }
    },
    
    show: function() {
        //highlight current
        this.highlightItem(this.items.eq(this.selectedOption.index()));

        //calculate panel position
        this.alignPanel();

        this.panel.css('z-index', ++PrimeFaces.zindex);

        if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
            this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
        }

        this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
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
    
    fireChangeEvent: function() {
        //call user onchange callback by passing current option value
        if(this.cfg.onchange) {
            this.cfg.onchange.call(this, this.selectedOption.attr('value'));
        }

        if(this.cfg.behaviors) {
            var changeBehavior = this.cfg.behaviors['change'];
            if(changeBehavior)
                changeBehavior.call(this);
        }
    }
    
});

/**
 * PrimeFaces SelectOneRadio Widget
 */
PrimeFaces.widget.SelectOneRadio = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.outputs = this.jq.find('.ui-radiobutton-box:not(.ui-state-disabled)');
        this.inputs = this.jq.find(':radio:not(:disabled)');
        this.labels = this.jq.find('label:not(.ui-state-disabled)');
        this.icons = this.jq.find('.ui-radiobutton-icon');
        
        this.checkedRadio = this.outputs.filter('.ui-state-active');
                
        this.bindEvents();        
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
                radio = target; //custom layout

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
            }).click(function(e) {
                _self.input.trigger('click');
                
                if($.browser.msie && parseInt($.browser.version) < 9) {
                    _self.input.trigger('change');
                }
            });
            
            //delegate focus-blur-change states
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
    },
    
    toggle: function() {
        this.box.click();
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
 * PrimeFaces RadioButton Widget
 */
PrimeFaces.widget.RadioButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        
        this.output = this.jq.find('.ui-radiobutton-box');
        this.input = this.jq.find('input:radio');
        this.icon = this.jq.find('.ui-radiobutton-icon');
        this.label = $('label[for="' + this.id + '"]');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
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
        
        //delegate focus-blur states
        this.input.focus(function() {
            _self.output.addClass('ui-state-focus');
        }).blur(function() {
            _self.output.removeClass('ui-state-focus');
        });

        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
        }
    },
    
    check: function() {
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
        var _self = this;
        
        //attach keypress listener to parent form
        this.jqTarget.parents('form:first').keydown(function(e) {
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
            $(this).addClass('ui-state-hover');
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