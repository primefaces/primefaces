/**
 * PrimeFaces ThemeSwitcher Widget
 */
PrimeFaces.widget.ThemeSwitcher = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.panelId = this.jqId + '_panel';
    this.jq = $(this.jqId);
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

    if(!this.cfg.editable) {
        this.label.css('cursor', 'pointer').mousedown(function(e) {
           e.preventDefault(); 
        });
        
        this.label.val(selectedOption.text());
        
        this.triggers = this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');
    } 
    else {
        this.triggers = this.jq.find('.ui-selectonemenu-trigger');
    }
    
    this.bindEvents();

    //disable tabbing if disabled
    if(this.disabled) {
        this.input.attr("tabindex", -1);
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

PrimeFaces.extend(PrimeFaces.widget.ThemeSwitcher, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.ThemeSwitcher.prototype.setupDialogSupport = function() {
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

PrimeFaces.widget.ThemeSwitcher.prototype.initWidths = function() {
    this.jq.width(this.input.outerWidth(true));
    var jqWidth = this.jq.innerWidth();
    
    //align panel and container
    if(this.panel.outerWidth() < jqWidth) {
        this.panel.width(jqWidth);
    }
}

PrimeFaces.widget.ThemeSwitcher.prototype.bindEvents = function() {
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
}

PrimeFaces.widget.ThemeSwitcher.prototype.highlightItem = function(item) {
    this.unhighlightItem(this.items.filter('.ui-state-active'));
    item.addClass('ui-state-active');
    
    this.alignScroller(item);
}

PrimeFaces.widget.ThemeSwitcher.prototype.unhighlightItem = function(item) {
    item.removeClass('ui-state-active');
}

PrimeFaces.widget.ThemeSwitcher.prototype.selectItem = function(item) {
    var newOption = this.options.eq(item.index());
    
    //unselect active item as current gets activated on show
    this.unhighlightItem(this.items.filter('.ui-state-active'));
    
    //select item if item is not already selected
    if(newOption.val() != this.selectedOption.val()) {
        this.options.removeAttr('selected');
        newOption.attr('selected', 'selected');

        this.label.val(newOption.text());
        this.selectedOption = newOption;
        
        //update theme
        PrimeFaces.changeTheme(newOption.attr('value'));
        
        this.fireChangeEvent();
    }

    this.input.focus();
    this.hide();
}

PrimeFaces.widget.ThemeSwitcher.prototype.bindKeyEvents = function() {
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
                var highlightedItem = _self.items.filter('.ui-state-active'),
                prev = highlightedItem.prevAll(':not(.ui-state-disabled):first');
                
                if(prev.length == 1) {
                    _self.highlightItem(prev);
                }
                
                e.preventDefault();
                break;

            case keyCode.DOWN:
            case keyCode.RIGHT:
                var highlightedItem = _self.items.filter('.ui-state-active'), 
                next = highlightedItem.nextAll(':not(.ui-state-disabled):first');
                
                if(next.length == 1 && _self.panel.is(':visible')) {
                   _self.highlightItem(next);
                }
                
                e.preventDefault();
                break;
                
            case keyCode.ENTER:
            case keyCode.NUMPAD_ENTER:
                if(_self.panel.is(":visible"))
                    _self.selectItem(_self.items.filter('.ui-state-active'));
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
                    _self.selectItem(_self.items.filter('.ui-state-active'));
                }
                break;
            
        };
    }); 
}
                    
PrimeFaces.widget.ThemeSwitcher.prototype.alignScroller = function(item) {
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

PrimeFaces.widget.ThemeSwitcher.prototype.show = function() {
    //highlight current
    this.highlightItem(this.items.eq(this.selectedOption.index()));
    
    //calculate panel position
    this.alignPanel();
    
    this.panel.css('z-index', ++PrimeFaces.zindex);
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', PrimeFaces.zindex - 1);
    }

    this.panel.show(this.cfg.effect, {}, this.cfg.effectDuration);
}

PrimeFaces.widget.ThemeSwitcher.prototype.hide = function() {
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '');
    }
    
    this.panel.css('z-index', '').hide();
}

PrimeFaces.widget.ThemeSwitcher.prototype.focus = function() {
    this.input.focus();
}

PrimeFaces.widget.ThemeSwitcher.prototype.blur = function() {
    this.input.blur();
}

PrimeFaces.widget.ThemeSwitcher.prototype.alignPanel = function() {
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

PrimeFaces.widget.ThemeSwitcher.prototype.fireChangeEvent = function() {
    if(this.cfg.onchange) {
        this.cfg.onchange.call(this);
    }
    
    if(this.cfg.behaviors) {
        var changeBehavior = this.cfg.behaviors['change'];
        if(changeBehavior)
            changeBehavior.call(this);
    }
}