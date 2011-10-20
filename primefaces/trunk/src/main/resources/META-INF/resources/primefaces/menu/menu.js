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
    })
    .click(function(e) {
        var menuitem = $(this);
        if(menuitem.children('.ui-menu-child').length == 0) {
            _self.jq.find('.ui-menu-child:visible').fadeOut('fast');
        }
        
        e.stopPropagation();
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
        
        /*
         * we might have two menus with same ids if an ancestor of a menu is updated,
         * if so remove the previous one and refresh jq
         */
        if(this.jq.length > 1){
            $(document.body).children(this.jqId).remove();
            this.jq = $(this.jqId);
            this.jq.appendTo(document.body);
        }
        else if(this.jq.parent().is(':not(body)')) {
            this.jq.appendTo(document.body);
        }
        
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
        this.setupSliding();
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
        this.menuitems.click(function(e) {
            var menuitem = $(this);
            if(menuitem.children('.ui-menu-child').length == 0) {
                _self.jq.find('.ui-menu-child:visible').fadeOut('fast');
            }

            e.stopPropagation();
        });
    }
    else if(this.cfg.sliding){
        this.menuitems.click(function(e){
            if(_self.slidingCfg.animating)
                return;
            
            var menuitem = $(this), 
            submenu = menuitem.children('ul.ui-menu-child');
            
            //invalid event
            if(submenu.length > 0) {
                _self.slidingCfg.currentSubMenu = submenu.css({display : 'block'});
                _self.forward();
            }
            
            e.stopPropagation();
       });
       
       this.slidingCfg.backButton.click(function(e){
           _self.backward();
           e.stopPropagation();
       });
    }
}

PrimeFaces.widget.Menu.prototype.setupSliding = function() {
    this.slidingCfg  = {};
    this.slidingCfg.scroll = this.jq.children('div.ui-menu-sliding-scroll:first');
    this.slidingCfg.state = this.slidingCfg.scroll.children('div.ui-menu-sliding-state:first');
    this.slidingCfg.wrapper = this.slidingCfg.state.children('div.ui-menu-sliding-wrapper:first');
    this.slidingCfg.content = this.slidingCfg.wrapper.children('div.ui-menu-sliding-content:first');
    this.slidingCfg.heighter = this.slidingCfg.content.children('div:first');
    this.slidingCfg.rootList = this.slidingCfg.heighter.children('ul:first');
    this.slidingCfg.backButton = this.jq.children('.ui-menu-backward');
    this.slidingCfg.easing= 'easeInOutCirc';
    this.slidingCfg.level = 0;

    this.slidingCfg.scroll.css({width : this.jq.width(), height : this.jq.height() - 18});
    this.slidingCfg.state.css({width : this.jq.width(), height : this.jq.height() - 18});

    this.slidingCfg.width = this.slidingCfg.scroll.width();

    this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width()});
    this.slidingCfg.rootList.find("ul.ui-menu-child").css({left : this.slidingCfg.width, width : this.slidingCfg.width});

    this.slidingCfg.heighter.css({height : this.slidingCfg.rootList.height()});

    if(this.slidingCfg.wrapper.height() > this.slidingCfg.state.height())
        this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width() - 18});
    else
        this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width()});
}

PrimeFaces.widget.Menu.prototype.forward = function(){
    this.slide(++this.slidingCfg.level);
}

PrimeFaces.widget.Menu.prototype.backward = function(){
    if(!this.slidingCfg.level)
        return;
    
    var prev = this.slidingCfg.currentSubMenu, 
    back = function(){
        prev.css({display : 'none'});
    };
    
    this.slidingCfg.currentSubMenu = this.slidingCfg.currentSubMenu.parents('ul.ui-menu-list:first');
    this.slide(--this.slidingCfg.level, back);
}

PrimeFaces.widget.Menu.prototype.slide = function(level, fn){
    var _self = this;
    this.slidingCfg.animating = true;
    
    if(level == 0){
        this.slidingCfg.backButton.css({display : 'none'});
    }

    //scroll control
    this.slidingCfg.heighter.height(this.slidingCfg.currentSubMenu.outerHeight(true));
    if(this.slidingCfg.wrapper.height() > this.slidingCfg.state.height())
        this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width() - 18});
    else
        this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width()});
    
    this.slidingCfg.currentSubMenu.css({width : this.slidingCfg.wrapper.width()});
    
    this.slidingCfg.rootList.animate( 
    {
        left : -level * _self.slidingCfg.width
    },
    {
        easing: this.slidingCfg.easing,
        complete: function() {
            _self.slidingCfg.animating = false;
            if(fn) 
                fn.call();
            
            if(_self.slidingCfg.level > 0)
                _self.slidingCfg.backButton.css({display : 'block'});
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
    this.menuId = this.jqId + '_menu';
    this.jq = $(this.jqId);
    this.button = this.jq.children('button');
    this.menu = this.jq.children('.ui-menu');
    this.menuitems = this.jq.find('.ui-menuitem');

    this.button.button({icons:{primary:'ui-icon-triangle-1-s'}});

    this.bindEvents();
    
    this.menu.css('z-index', this.cfg.zindex);
    
    $(document.body).children(this.menuId).remove();
    this.menu.appendTo(document.body);
    
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
        this.cfg.trigger = this.cfg.target + ' .ui-treetable-data ' + (this.cfg.nodeType ? 'tr.' + this.cfg.nodeType : 'tr');
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