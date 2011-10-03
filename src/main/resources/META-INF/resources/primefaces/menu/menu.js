/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menubar = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId + '_menu');

    if(!this.cfg.autoSubmenuDisplay) {
        this.cfg.trigger = this.jqId + ' li';
        this.cfg.triggerEvent = 'click';
    }

    var _self = this;
    this.cfg.select = function(event, ui) {
        _self.jq.wijmenu('deactivate');
    };

    this.jq.wijmenu(this.cfg);

    if(this.cfg.style)
        this.jq.parent().parent().attr('style', this.cfg.style);
    if(this.cfg.styleClass)
        this.jq.parent().parent().addClass(this.cfg.styleClass);
}

/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menu = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId + '_menu');

    this.cfg.orientation = 'vertical';

    if(this.cfg.position == 'dynamic') {
        this.cfg.position = {
            my: this.cfg.my
            ,at: this.cfg.at
        }

        this.cfg.trigger = PrimeFaces.escapeClientId(this.cfg.trigger);
    }

    var _self = this;
    this.cfg.select = function(event, ui) {
        _self.jq.wijmenu('deactivate');
    };

    this.jq.wijmenu(this.cfg);

    this.element = this.jq.parent().parent();       //overlay element
    this.element.css('z-index', this.cfg.zindex);

    if(this.cfg.style)
        this.element.attr('style', this.cfg.style);
    if(this.cfg.styleClass)
        this.element.addClass(this.cfg.styleClass);
}

/*
 * PrimeFaces MenuButton Widget
 */
PrimeFaces.widget.MenuButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.jqbutton = jQuery(this.jqId + '_button');
    this.jqMenu = jQuery(this.jqId + '_menu');

    //menu options
    this.cfg.trigger = this.jqId + '_button';
    this.cfg.orientation = 'vertical';
    this.cfg.position = {
        my: 'left top'
        ,at: 'left bottom'
    };

    var _self = this;
    this.cfg.select = function(event, ui) {
        _self.jqMenu.wijmenu('deactivate');
    };

    //crete button and menu
    this.jqbutton.button({icons:{primary:'ui-icon-triangle-1-s'}});
    this.jqMenu.wijmenu(this.cfg);

    if(this.cfg.disabled) {
        this.jqbutton.button('disable');
    }

    this.jqMenu.parent().parent().css('z-index', this.cfg.zindex);      //overlay element
}

/*
 * PrimeFaces ContextMenu Widget
 */
PrimeFaces.widget.ContextMenu = function(id, cfg) {
	this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.menuitems = this.jq.find('.ui-menuitem');
    var _self = this,
    documentTrigger = this.cfg.target === document;
    
    //defaults
    this.cfg.effect = this.cfg.effect == 'none' ? undefined : this.cfg.effect;
        
    //trigger
    this.cfg.target = documentTrigger ? document : PrimeFaces.escapeClientId(this.cfg.target);
    var jqTarget = $(this.cfg.target);

    if(jqTarget.hasClass('ui-datatable')) {
        this.cfg.trigger = this.cfg.target + ' .ui-datatable-data tr';
    }
    else if(jqTarget.hasClass('ui-treetable')) {
        this.cfg.trigger = this.cfg.target + ' .ui-treetable-data tr';
    }
    else if(jqTarget.hasClass('ui-tree')) {
        this.cfg.trigger = this.cfg.target + ' ' + (this.cfg.nodeType ? 'li.' + this.cfg.nodeType + ' .ui-tree-node-content': '.ui-tree-node-content');
    }
    else {
        this.cfg.trigger = this.cfg.target;
    }
    
    //visuals
    this.bindEvents();
       
    //append to body
    this.jq.appendTo('body');
    
    //attach contextmenu
    if(documentTrigger) {
        $(this.cfg.trigger).bind('contextmenu.ui-contextmenu', function(e) {
            _self.show(e);
        });
    } 
    else {
        $(this.cfg.trigger).live('contextmenu.ui-contextmenu', function(e) {
            _self.show(e);
        });
    }
    
}

PrimeFaces.widget.ContextMenu.prototype.bindEvents = function() {
    var _self = this;
    
    //menuitem visuals
    this.menuitems.mouseover(function(e) {
        var element = $(this);
        if(!element.hasClass('ui-state-disabled'))
            element.addClass('ui-state-hover');
        
    }).mouseout(function(e) {
        var element = $(this);
        element.removeClass('ui-state-hover');
    });
    
    //hide overlay when document is clicked
    $(document.body).bind('click.ui-contextmenu', function (e) {
        if(_self.jq.is(":hidden")) {
            return;
        }
        
        _self.hide();
    });
}

PrimeFaces.widget.ContextMenu.prototype.show = function(e) {            
    //hide all context menus
    $(document.body).children('.ui-contextmenu:visible').hide();
    
    this.jq.css({
        'left': e.pageX,
        'top': e.pageY
    });
    
    if(this.cfg.effect) {
        this.jq.show(this.cfg.effect, {}, this.cfg.effectDuration);
    } 
    else {
        this.jq.show();
    }
    
    e.preventDefault();
}

PrimeFaces.widget.ContextMenu.prototype.hide = function(e) {
    if(this.cfg.effect) {
        this.jq.hide(this.cfg.effect, {}, this.cfg.effectDuration);
    } 
    else {
        this.jq.hide();
    }
}

PrimeFaces.widget.ContextMenu.prototype.isVisible = function() {
    return this.jq.is(':visible');
}