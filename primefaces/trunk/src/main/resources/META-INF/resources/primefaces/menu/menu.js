/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menubar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var _self = this,
        menuitems = null;

        if(this.cfg.autoDisplay) {
            menuitems = this.jq.find('li.ui-menuitem');
        }
        else {
            this.rootList = this.jq.children('ul.ui-menu-list');
            this.rootMenuitems = this.rootList.children('li.ui-menuitem');  //immediate children of root
            menuitems = this.rootMenuitems.find('li.ui-menuitem');            //descendants of root menuitems

            this.bindRootItemEvents();
        }

        //menuitems are all items in autoDisplay case and descendants items of root items otherwise
        menuitems.mouseenter(function() {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitemLink.addClass('ui-state-hover');

                var submenu = menuitem.children('ul.ui-menu-child');
                if(submenu.length == 1) {
                    _self.showSubmenu(menuitem, submenu);
                }
            }
        })
        .mouseleave(function() {
            var menuitem = $(this);
            menuitem.children('.ui-menuitem-link').removeClass('ui-state-hover');
            menuitem.find('.ui-menu-child:visible').hide();
        })
        .click(function(e) {        
            //hide all visible submenus of menubar and reset state
            _self.jq.find('.ui-menu-child:visible').fadeOut('fast');
            _self.jq.find('a.ui-menuitem-link').removeClass('ui-state-hover');

            //do not trigger root click event in non-autodisplay
            e.stopPropagation();
        });        
    },
    
    bindRootItemEvents: function() {
        var _self = this;

        //root menuitems    
        this.rootMenuitems.mouseenter(function() {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitemLink.addClass('ui-state-hover');
            }
        })
        .mouseleave(function() {
            var menuitem = $(this);
            menuitem.children('.ui-menuitem-link').removeClass('ui-state-hover');
            menuitem.find('.ui-menu-child:visible').hide();
        })
        .click(function(e) {
            var menuitem = $(this),
            submenu = menuitem.children('ul.ui-menu-child');

            if(submenu.length == 1) {
                if(submenu.is(':visible')) {
                    menuitem.children('.ui-menu-child:visible').fadeOut('fast');
                }
                else {
                    _self.showSubmenu(menuitem, submenu);
                }

                e.preventDefault();
            }
        });
    },
    
    showSubmenu: function(menuitem, submenu) {
        submenu.css('z-index', ++PrimeFaces.zindex);

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
});


/**
 * PrimeFaces Menu Widget
 */
PrimeFaces.widget.Menu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.menuitems = this.jq.find('.ui-menuitem');
        this.cfg.tiered = this.cfg.type == 'tiered';
        this.cfg.sliding = this.cfg.type == 'sliding';

        var _self = this;

        //dynamic position
        if(this.cfg.position == 'dynamic') {        
            this.cfg.trigger = $(PrimeFaces.escapeClientId(this.cfg.trigger));

            //mark trigger and descandants of trigger as a trigger for a primefaces overlay
            this.cfg.trigger.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

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

            this.cfg.pos = {
                my: this.cfg.my
                ,at: this.cfg.at
                ,of: this.cfg.trigger
            }

            this.cfg.trigger.bind(this.cfg.triggerEvent + '.ui-menu', function(e) {
                if(_self.jq.is(':visible'))
                    _self.hide(e);
                else
                    _self.show(e);

                //sliding rescue
                if(_self.cfg.sliding && !_self.slidingCfg.heighter.height()){
                    _self.slidingCfg.heighter.css({height : _self.slidingCfg.rootList.height()});  
                }
            });

            //hide overlay on document mousedown
            $(document.body).bind('mousedown.ui-menu', function (e) {            
                if(_self.jq.is(":hidden")) {
                    return;
                }

                //do nothing if mousedown is on trigger
                var target = $(e.target);
                if(target.is(_self.cfg.trigger.get(0))||_self.cfg.trigger.has(target).length > 0) {
                    return;
                }

                //hide if mouse is outside of overlay except trigger
                var offset = _self.jq.offset();
                if(e.pageX < offset.left ||
                    e.pageX > offset.left + _self.jq.width() ||
                    e.pageY < offset.top ||
                    e.pageY > offset.top + _self.jq.height()) {
                    _self.hide(e);
                }
            });

            //Hide overlay on resize
            var resizeNS = 'resize.' + this.id;
            $(window).unbind(resizeNS).bind(resizeNS, function() {
                if(_self.jq.is(':visible')) {
                    _self.hide();
                }
            });

            //dialog support
            this.setupDialogSupport();
        }

        if(this.cfg.sliding){
            this.setupSliding();
        }

        //events
        this.bindEvents();
    }
    
    ,bindEvents: function() {  
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
                        'left': menuitem.outerWidth()
                        ,'top': 0
                        ,'z-index': ++PrimeFaces.zindex
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

        if(this.cfg.position == 'dynamic') {
            this.menuitems.click(function() {
                _self.hide();
            });  
        }

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
                parents = menuitem.parents('ul.ui-menu-list').length,
                submenu = menuitem.children('ul.ui-menu-child');

                //invalid menuitem target
                if(submenu.length < 1||_self.slidingCfg.level!=parents-1)
                    return;

                _self.slidingCfg.currentSubMenu = submenu.css({display : 'block'});
                _self.forward();

                e.stopPropagation();
        });

        this.slidingCfg.backButton.click(function(e){
                _self.backward();
                e.stopPropagation();
            });
        }
    },
    
    setupDialogSupport: function() {
        var dialog = this.cfg.trigger.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            this.jq.css('position', 'fixed');
        }
    },
    
    show: function(e) {
        this.align();
        this.jq.css('z-index', ++PrimeFaces.zindex).show();

        e.preventDefault();
    },
    
    hide: function(e) {
        this.jq.fadeOut('fast');
    },
    
    align: function() {
        var fixedPosition = this.jq.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

        this.cfg.pos.offset = positionOffset;

        this.jq.css({left:'', top:''}).position(this.cfg.pos);
    },
    
    setupSliding: function() {
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

        var viewportWidth = this.jq.width(), viewportHeight = this.jq.height() - this.slidingCfg.backButton.height();
        this.slidingCfg.scroll.css({width : viewportWidth, height : viewportHeight});
        this.slidingCfg.state.css({width : viewportWidth, height : viewportHeight});
        this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width()});
        this.slidingCfg.rootList.find("ul.ui-menu-child").css({left : viewportWidth, width : viewportWidth - 18});
        this.slidingCfg.heighter.css({height : this.slidingCfg.rootList.height()});
        this.slidingCfg.width = viewportWidth;

        if(this.slidingCfg.wrapper.height() > this.slidingCfg.state.height())
            this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width() - 18});
        else
            this.slidingCfg.wrapper.css({width : this.slidingCfg.state.width()});
    },
    
    forward: function(){
        this.slide(++this.slidingCfg.level);
    },
    
    backward: function(){
        if(!this.slidingCfg.level)
            return;

        var prev = this.slidingCfg.currentSubMenu, 
        back = function(){
            prev.css({display : 'none'});
        };

        this.slidingCfg.currentSubMenu = this.slidingCfg.currentSubMenu.parents('ul.ui-menu-list:first');
        this.slide(--this.slidingCfg.level, back);
    },
    
    slide: function(level, fn){
        var _self = this, 
        currentHeight = _self.slidingCfg.currentSubMenu.outerHeight(true), 
        stateWidth = this.slidingCfg.state.width(),
        longer = currentHeight > this.slidingCfg.heighter.height();

        this.slidingCfg.animating = true;

        if(level == 0){
            this.slidingCfg.backButton.css({display : 'none'});
        }

        if(longer){
            _self.slidingCfg.heighter.height(currentHeight);
            var scrolled = this.slidingCfg.wrapper.height() > this.slidingCfg.state.height();
            if(scrolled){
                stateWidth = stateWidth - 18;
            }
        }

        if(currentHeight > this.slidingCfg.state.height()){
            this.slidingCfg.state.css({'overflow' : 'hidden', 'overflow-y' : 'auto'});
        }
        else{
            this.slidingCfg.state.css({'overflow' : 'hidden'});
        }

        this.slidingCfg.wrapper.css({width : stateWidth});
        _self.slidingCfg.state.scrollTop(0);

        this.slidingCfg.rootList.animate( 
        {
            left : -level * _self.slidingCfg.width
        },
        {
            easing: this.slidingCfg.easing,
            complete: function() {
                _self.slidingCfg.animating = false;

                if(!longer){
                    _self.slidingCfg.heighter.height(currentHeight);
                    var scrolled = _self.slidingCfg.wrapper.height() > _self.slidingCfg.state.height();
                    if(scrolled){
                        stateWidth = _self.slidingCfg.state.width() - 18;
                    }
                    else{
                        stateWidth =  _self.slidingCfg.state.width();
                    }
                    _self.slidingCfg.wrapper.css({width : stateWidth});
                }

                _self.slidingCfg.currentSubMenu.css({width : stateWidth});

                if(fn) 
                    fn.call();

                if(_self.slidingCfg.level > 0)
                    _self.slidingCfg.backButton.css({display : 'block'});
            }
        });
    }
});
            
/*
 * PrimeFaces MenuButton Widget
 */
PrimeFaces.widget.MenuButton = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.menuId = this.jqId + '_menu';
        this.button = this.jq.children('button');
        this.menu = this.jq.children('.ui-menu');
        this.menuitems = this.jq.find('.ui-menuitem');
        this.cfg.disabled = this.button.is(':disabled');

        if(!this.cfg.disabled) {
            this.bindEvents();

            $(document.body).children(this.menuId).remove();
            this.menu.appendTo(document.body);

            //dialog support
            this.setupDialogSupport();
        }
    },
    
    bindEvents: function() {  
        var _self = this;

        //button visuals
        this.button.mouseover(function(){
            if(!_self.button.hasClass('ui-state-focus')) {
                _self.button.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            if(!_self.button.hasClass('ui-state-focus')) {
                _self.button.removeClass('ui-state-hover ui-state-active');
            }
        }).mousedown(function() {
            $(this).removeClass('ui-state-focus ui-state-hover').addClass('ui-state-active');
        }).mouseup(function() {
            var el = $(this);
            el.removeClass('ui-state-active')

            if(_self.menu.is(':visible')) {
                el.addClass('ui-state-hover');
                _self.hide();
            } 
            else {
                el.addClass('ui-state-focus');
                _self.show();
            }
        }).focus(function() {
            $(this).addClass('ui-state-focus');
        }).blur(function() {
            $(this).removeClass('ui-state-focus');
        });

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //menuitem visuals
        this.menuitems.mouseover(function(e) {
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                element.addClass('ui-state-hover');
            }
        }).mouseout(function(e) {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            _self.button.removeClass('ui-state-focus');
            _self.hide();
        });

        this.cfg.position = {
            my: 'left top'
            ,at: 'left bottom'
            ,of: this.button
        }

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

        //aria
        this.button.attr('role', 'button').attr('aria-disabled', this.button.is(':disabled'));
    },
    
    setupDialogSupport: function() {
        var dialog = this.button.parents('.ui-dialog:first');

        if(dialog.length == 1) {        
            this.menu.css('position', 'fixed');
        }
    },
    
    show: function() {
        this.alignPanel();

        this.menu.show();
    },
    
    hide: function() {
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


/*
 * PrimeFaces ContextMenu Widget
 */
PrimeFaces.widget.ContextMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
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
            this.cfg.trigger = this.cfg.target + ' .ui-treetable-data ' + (this.cfg.nodeType ? 'tr.ui-treetable-selectable-node.' + this.cfg.nodeType : 'tr.ui-treetable-selectable-node');
        }
        else if(jqTarget.hasClass('ui-tree')) {
            this.cfg.trigger = this.cfg.target + ' ' + (this.cfg.nodeType ? 'li.' + this.cfg.nodeType + ' .ui-tree-selectable-node': '.ui-tree-selectable-node');
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
    },
    
    bindEvents: function() {
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
    },
    
    show: function(e) {  
        //hide other contextmenus if any
        $(document.body).children('.ui-contextmenu:visible').hide();

        var win = $(window),
        left = e.pageX,
        top = e.pageY,
        width = this.jq.outerWidth(),
        height = this.jq.outerHeight();

        //collision detection for window boundaries
        if((left + width) > (win.width())+ win.scrollLeft()) {
            left = left - width;
        }
        if((top + height ) > (win.height() + win.scrollTop())) {
            top = top - height;
        }

        this.jq.css({
            'left': left,
            'top': top,
            'z-index': ++PrimeFaces.zindex
        }).show();

        e.preventDefault();
    },
    
    hide: function(e) {
        this.jq.fadeOut('fast');
    },
    
    isVisible: function() {
        return this.jq.is(':visible');
    }

});

/**
 * PrimeFaces MegaMenu Widget
 */
PrimeFaces.widget.MegaMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var _self = this,
        menuitems = null;

        this.rootList = this.jq.children('ul.ui-menu-list');
        this.rootMenuitems = this.rootList.children('li.ui-menuitem');  //immediate children of root
        this.descandantMenuitems = this.rootMenuitems.find('li.ui-menuitem');          //descendants of root menuitems

        //root menuitems    
        this.rootMenuitems.mouseenter(function(e) {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link'),
            submenu = menuitem.children('ul.ui-menu-child');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitemLink.addClass('ui-state-hover');
            }

            if(submenu.length == 1) {
                _self.showSubmenu(menuitem, submenu);

                e.preventDefault();
            }
        })
        .mouseleave(function() {
            var menuitem = $(this);
            menuitem.children('.ui-menuitem-link').removeClass('ui-state-hover');
            menuitem.find('.ui-menu-child:visible').hide();
        });

        //descandant menuitems
        this.descandantMenuitems.mouseenter(function() {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitemLink.addClass('ui-state-hover');
            }
        })
        .mouseleave(function() {
            var menuitem = $(this);
            menuitem.children('.ui-menuitem-link').removeClass('ui-state-hover');
        })
        .click(function(e) {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            //reset state
            menuitemLink.removeClass('ui-state-hover');

            //hide
            menuitem.parents('.ui-menu-child:first').fadeOut('fast');


        });    
    },
    
    showSubmenu: function(menuitem, submenu) {
        submenu.css('z-index', ++PrimeFaces.zindex);

        submenu.css({
            'left': 0
            ,'top': menuitem.outerHeight()
        });

        submenu.show();
    }
});