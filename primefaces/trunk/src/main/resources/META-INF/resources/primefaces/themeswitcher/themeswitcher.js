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
    this.labelContainer = this.jq.find('.ui-selectonemenu-label-container');
    this.label = this.jq.find('.ui-selectonemenu-label');
    this.menuIcon = this.jq.children('.ui-selectonemenu-trigger');
    this.triggers = this.jq.find('.ui-selectonemenu-trigger, .ui-selectonemenu-label');
    this.panel = this.jq.children(this.panelId);
    this.disabled = this.jq.hasClass('ui-state-disabled');
    
    //options
    if(!this.cfg.effectDuration) {
        this.cfg.effectDuration = 400;
    }
    
    //add selector
    this.jq.addClass('ui-themeswitcher');

    //visuals and behaviors
    this.bindEvents();

    //client Behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors);
    }
    
    //panel management
    $(document.body).children(this.panelId).remove();
    this.panel.appendTo(document.body);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.ThemeSwitcher, PrimeFaces.widget.BaseWidget);
        
PrimeFaces.widget.ThemeSwitcher.prototype.bindEvents = function() {

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
        
        //update theme
        PrimeFaces.changeTheme(option.attr('value'));
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
          _self.menuIcon.addClass("ui-state-focus");
          _self.label.addClass("ui-state-focus");
        }
    }).blur(function(){
        _self.triggers.removeClass('ui-state-focus');
    });

}

PrimeFaces.widget.ThemeSwitcher.prototype.show = function() {
    this.alignPanel();

    this.panel.css('z-index', '100000');
    
    if($.browser.msie && /^[6,7]\.[0-9]+/.test($.browser.version)) {
        this.panel.parent().css('z-index', '99999');
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
}

PrimeFaces.widget.ThemeSwitcher.prototype.enable = function() {
    this.disabled = false;
    this.jq.removeclass('ui-state-disabled');
}

PrimeFaces.widget.ThemeSwitcher.prototype.alignPanel = function() {
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