/**
 * PrimeFaces ThemeSwitcher Widget
 * After 3.0 we should extend from PrimeFaces.widget.SelectOneMenu
 */
PrimeFaces.widget.ThemeSwitcher = function(cfg) {
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
    
    //add selector
    this.jq.addClass('ui-themeswitcher');

    //disable options
    this.options.filter(':disabled').each(function() {
        _self.itemContainer.children().eq($(this).index()).addClass('ui-state-disabled');
    });

    //populate label and activate selected item
    var selectedOption = this.options.filter(':selected');
    this.label.html(selectedOption.text());
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
    
    //align panel and label-menuicon
    var panelWidth = this.panel.width(),
    jqWidth = this.jq.width();
    
    if(panelWidth > jqWidth) {
        this.jq.width(panelWidth + this.menuIcon.width());
        this.panel.width(this.jq.width());
    }
    else {
        this.panel.width(jqWidth);
        this.jq.width(jqWidth);     //replace auto with fixed width
    }
        
    //Hide overlay on resize
    var resizeNS = 'resize.' + this.id;
    $(window).unbind(resizeNS).bind(resizeNS, function() {
        if(_self.panel.is(':visible')) {
            _self.hide();
        }
    });
    
    //dialog support
    this.setupDialogSupport();
    
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

PrimeFaces.widget.ThemeSwitcher.prototype.highlightItem = function(item) {
    this.unhighlightItem(this.items.filter('.ui-state-active'));
    item.addClass('ui-state-active');
    
    this.alignScroller(item);
}

PrimeFaces.widget.ThemeSwitcher.prototype.unhighlightItem = function(item) {
    item.removeClass('ui-state-active');
}

PrimeFaces.widget.ThemeSwitcher.prototype.selectItem = function(item) {
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
        
        //update theme
        PrimeFaces.changeTheme(option.attr('value'));
    }

    this.labelContainer.focus();
    this.hide();
}

PrimeFaces.widget.ThemeSwitcher.prototype.bindKeyEvents = function() {
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
    this.highlightItem(this.items.eq(this.options.filter(':selected').index()));
    
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

PrimeFaces.widget.ThemeSwitcher.prototype.disable = function() {
    this.disabled = true;
    this.jq.addClass('ui-state-disabled');
    this.labelContainer.attr("tabindex", -1);
}

PrimeFaces.widget.ThemeSwitcher.prototype.enable = function() {
    this.disabled = false;
    this.jq.removeClass('ui-state-disabled');
    this.labelContainer.attr("tabindex", this.tabindex);
}

PrimeFaces.widget.ThemeSwitcher.prototype.focus = function() {
    this.labelContainer.focus();
}

PrimeFaces.widget.ThemeSwitcher.prototype.blur = function() {
    this.labelContainer.blur();
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