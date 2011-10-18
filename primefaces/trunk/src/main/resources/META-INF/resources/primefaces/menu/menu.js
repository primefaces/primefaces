/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menubar = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.menuitems = this.jq.find('.ui-menuitem');
    this.zIndex = 10000;

    this.bindEvents();

    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Menubar, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Menubar.prototype.bindEvents = function() {
    var _self = this;

    this.menuitems.mouseenter(function() {
        var menuitem = $(this),
        menuitemLink = menuitem.children('.ui-menuitem-link');

        if(!menuitemLink.hasClass('ui-state-disabled')) {
            menuitemLink.addClass('ui-state-hover');
        }

        var submenu = menuitem.children('ul.ui-menu-child');
        if(submenu.length == 1) {
            submenu.css('zIndex', _self.zIndex++);

            if(!menuitem.parent().hasClass('ui-menu-child')) {    //root menuitem
                submenu.css({
                    'left': 0
                    ,'top': menuitem.outerHeight()
                });
            } 
            else {                                              //submenu menuitem
                submenu.css({
                    'left': menuitem.outerWidth()
                    ,'top': 0
                });
            }

            submenu.show();
        }
    })
    .mouseleave(function() {
        var menuitem = $(this),
        menuitemLink = menuitem.children('.ui-menuitem-link');

        menuitemLink.removeClass('ui-state-hover');

        menuitem.find('.ui-menu-child:visible').hide();
    });
}

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
    this.cfg.sliding = this.cfg.type == 'sliding';

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

    if(this.cfg.sliding){
//        this.viewport = this.jq.innerWidth();
        this.scroll = this.jq.children('div.ui-menu-sliding-scroll:first');
        this.state = this.scroll.children('div.ui-menu-sliding-state:first');
        this.wrapper = this.state.children('div.ui-menu-sliding-wrapper:first');
        this.content = this.wrapper.children('div.ui-menu-sliding-content:first');
        this.heighter = this.content.children('div:first');
        this.rootList = this.heighter.children('ul:first');
        this.backButton = this.jq.children('.ui-menu-backward');
        
        

        this.cfg.easing = this.cfg.easing||'easeInOutCirc';
        this.level = 0;
        
        
        this.scroll.css({width : this.jq.width(), height : this.jq.height() - 18});
        this.state.css({width : this.jq.width(), height : this.jq.height() - 18});
        
        this.width = this.scroll.width();
        
//        this.wrapper.css({width : this.state.width(), height : this.state.height()});
        
        this.wrapper.css({width : this.state.width()});
        this.rootList.find("ul.ui-menu-child").css({left : this.width, width : this.width});
        
        this.heighter.css({height : this.rootList.height() - 18});
        
        if(this.wrapper.height() > this.state.height())
            this.wrapper.css({width : this.state.width() - 18});
        else
            this.wrapper.css({width : this.state.width()});
        
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
            if(submenu.length == 1) {
                submenu.css({
                    left: menuitem.outerWidth()
                    ,top: 0
                });

                submenu.show();
            }
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
    else if(this.cfg.sliding){
        this.menuitems.click(function(e){
            if(_self.animating)
                return;
            
            var menuitem = $(this), 
            parents = menuitem.parents('ul.ui-menu-list').length,
            submenu = menuitem.children('ul.ui-menu-child');
            
            //invalid event
            if(!submenu.length||_self.level!=parents-1)
                return;

            _self.currentSubMenu = submenu.css({display : 'block'});
            _self.forward();
            e.stopPropagation();
       });
       
       this.backButton.click(function(e){
           _self.backward();
           e.stopPropagation();
       });
    }
}

PrimeFaces.widget.Menu.prototype.forward = function(){
    this.slide(++this.level);
}

PrimeFaces.widget.Menu.prototype.backward = function(){
    if(!this.level)
        return;
    
    var prev = this.currentSubMenu, back = function(){
        prev.css({display : 'none'});
    };
    
    this.currentSubMenu = this.currentSubMenu.parents('ul.ui-menu-list:first');
    this.slide(--this.level, back);
}

PrimeFaces.widget.Menu.prototype.slide = function(level, fn){
    var _self = this;
    this.animating = true;
    
    if(level == 0){
        this.backButton.css({display : 'none'});
    }

    //scroll control
    this.heighter.height(this.currentSubMenu.outerHeight(true));
    if(this.wrapper.height() > this.state.height())
        this.wrapper.css({width : this.state.width() - 18});
    else
        this.wrapper.css({width : this.state.width()});
    
    this.currentSubMenu.css({width : this.wrapper.width()});
    
    this.rootList.animate( 
    {
        left : -level * _self.width
    },
    {
        easing: this.cfg.easing,
        complete: function() {
            _self.animating = false;
            if(fn) fn.call();
            
            if(_self.level > 0)
                _self.backButton.css({display : 'block'});
        }
    });
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
    
   this.jq.show();
    
    e.preventDefault();
}

PrimeFaces.widget.ContextMenu.prototype.hide = function(e) {
    this.jq.fadeOut('fast');
}

PrimeFaces.widget.ContextMenu.prototype.isVisible = function() {
    return this.jq.is(':visible');
}