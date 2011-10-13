/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menubar = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    
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
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Menubar, PrimeFaces.widget.BaseWidget);

/**
 * PrimeFaces Menu Widget
 */
PrimeFaces.widget.Menu = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.menuitems = this.jq.find('.ui-menuitem');
    this.cfg.tiered = this.cfg.type == 'tiered';

    var _self = this;
        
    //dynamic position
    if(this.cfg.position == 'dynamic') {        
        this.cfg.trigger = $(PrimeFaces.escapeClientId(this.cfg.trigger));
        
        this.cfg.position = {
            my: this.cfg.my
            ,at: this.cfg.at
            ,of: this.cfg.trigger
        }
        
        this.cfg.trigger.bind(this.cfg.triggerEvent + '.ui-menu', function(e) {
            _self.jq.css({left:'', top:''}).position(_self.cfg.position);
            _self.show(e);
        });
        
        //hide overlay when outside is clicked
        $(document.body).bind('click.ui-menu', function (e) {
            if(_self.jq.is(":hidden")) {
                return;
            }
            
            if(e.target === _self.cfg.trigger.get(0) || _self.cfg.trigger.find($(e.target)).length > 0) {
                return;
            }

            _self.hide();
        });
    }

    //visuals
    this.bindEvents();
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Menu, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Menu.prototype.bindEvents = function() {  
    var _self = this;
    
    this.menuitems.mouseenter(function(e) {
        var menuitem = $(this),
        menuitemLink = menuitem.children('.ui-menuitem-link');

        if(menuitemLink.hasClass('ui-state-disabled')) {
            return false;
        }
        
        menuitemLink.addClass('ui-state-hover');
        
        if(_self.cfg.tiered) {
            var submenu = menuitem.children('ul.ui-menu-child');
            if(submenu.length == 0) {
                return false;
            }

            //show submenu
            submenu.css({
                left: menuitem.outerWidth()
                ,top: 0
            });

            submenu.show();
        }

    }).mouseleave(function(e) {
        var menuitem = $(this),
        menuitemLink = menuitem.children('.ui-menuitem-link');
        
        menuitemLink.removeClass('ui-state-hover')
        
        if(_self.cfg.tiered) {
            menuitem.find('.ui-menu-child:visible').hide();
        }
    });
    
    if(this.cfg.tiered) {
        //hide overlay when outside is clicked
        $(document.body).bind('click.ui-menu-tiered', function (e) {
            if(_self.jq.is(":hidden")) {
                return;
            }

            _self.jq.find('.ui-menu-child:visible').fadeOut('fast');
        });
    }
}

PrimeFaces.widget.Menu.prototype.show = function(e) {                
    this.jq.show();
    
    e.preventDefault();
}

PrimeFaces.widget.Menu.prototype.hide = function(e) {
    this.jq.hide();
}

/*
 * PrimeFaces MenuButton Widget
 */
PrimeFaces.widget.MenuButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $(this.jqId);
    this.button = this.jq.children('button');
    this.menu = this.jq.children('.ui-menu');
    this.menuitems = this.jq.find('.ui-menuitem');

    this.button.button({icons:{primary:'ui-icon-triangle-1-s'}});

    this.bindEvents();
    
    this.menu.css('z-index', this.cfg.zindex);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.MenuButton, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.MenuButton.prototype.bindEvents = function() {  
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
    
    this.cfg.position = {
        my: 'left top'
        ,at: 'left bottom'
        ,of: this.button
    }
    
    //button event
    this.button.click(function(e) {
        _self.menu.css({left:'', top:''}).position(_self.cfg.position);
        _self.menu.show();
    });
    
    //hide overlay when outside is clicked
    $(document.body).bind('click.ui-menubutton', function (e) {
        if(_self.jq.is(":hidden")) {
            return;
        }
        
        if(e.target === _self.button.get(0) || _self.button.find($(e.target)).length > 0) {
            return;
        }

        _self.menu.hide();
    });
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

    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.ContextMenu, PrimeFaces.widget.BaseWidget);

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