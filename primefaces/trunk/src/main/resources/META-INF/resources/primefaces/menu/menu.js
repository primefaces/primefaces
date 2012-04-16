/**
 * PrimeFaces Menu Widget
 */
PrimeFaces.widget.Menu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        if(this.cfg.overlay) {
            this.initOverlay();
        }
    },
    
    initOverlay: function() {
        var _self = this;
        
        this.trigger = $(PrimeFaces.escapeClientId(this.cfg.trigger));

        //mark trigger and descandants of trigger as a trigger for a primefaces overlay
        this.trigger.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

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
            ,of: this.trigger
        }

        this.trigger.bind(this.cfg.triggerEvent + '.ui-menu', function(e) {
            if(_self.jq.is(':visible'))
                _self.hide(e);
            else
                _self.show(e);
        });

        //hide overlay on document mousedown
        $(document.body).bind('mousedown.ui-menu', function (e) {            
            if(_self.jq.is(":hidden")) {
                return;
            }

            //do nothing if mousedown is on trigger
            var target = $(e.target);
            if(target.is(_self.trigger.get(0))||_self.trigger.has(target).length > 0) {
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
    },
    
    setupDialogSupport: function() {
        var dialog = this.trigger.parents('.ui-dialog:first');

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
    }
});

/**
 * PrimeFaces TieredMenu Widget
 */
PrimeFaces.widget.TieredMenu = PrimeFaces.widget.Menu.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        
        this.bindEvents();
    },
    
    bindEvents: function() {        
        this.bindItemEvents();
        
        this.bindDocumentHandler();
    },
    
    bindItemEvents: function() {
        var _self = this;
        
        this.links.mouseenter(function() {
            var link = $(this),
            menuitem = link.parent(),
            autoDisplay = _self.cfg.autoDisplay;
            
            var activeSibling = menuitem.siblings('.ui-menuitem-active');
            if(activeSibling.length == 1) {
                _self.deactivate(activeSibling);
            }
            
            if(autoDisplay||_self.active) {
                if(menuitem.hasClass('ui-menuitem-active')) {
                    _self.reactivate(menuitem);
                }
                else {
                    _self.activate(menuitem);
                }  
            }
            else {
                _self.highlight(menuitem);
            }
        });
        
        if(this.cfg.autoDisplay == false) {
            this.rootLinks = this.jq.find('> ul.ui-menu-list > .ui-menuitem > .ui-menuitem-link');
            
            this.rootLinks.data('primefaces-menubar', this.id).find('*').data('primefaces-menubar', this.id)
            
            this.rootLinks.click(function(e) {
                var link = $(this),
                menuitem = link.parent(),
                submenu = menuitem.children('ul.ui-menu-child');

                if(submenu.length == 1) {
                    if(submenu.is(':visible')) {
                        _self.active = false;
                        _self.deactivate(menuitem);
                    }
                    else {                                        
                        _self.active = true;
                        _self.highlight(menuitem);
                        _self.showSubmenu(menuitem, submenu);
                    }
                }
            });
        }
        
        this.jq.find('ul.ui-menu-list').mouseleave(function(e) {
           if(_self.activeitem) {
               _self.deactivate(_self.activeitem);
           }
           
           e.stopPropagation();
        });
    },
    
    bindDocumentHandler: function() {
        var _self = this;
        
        $(document.body).click(function(e) {
            var target = $(e.target);
            if(target.data('primefaces-menubar') == _self.id) {
                return;
            }
            
            _self.active = false;
            _self.hideAll = true;

            _self.jq.find('li.ui-menuitem-active').each(function() {
                _self.deactivate($(this), true);
            });
        });
    },
    
    deactivate: function(menuitem, animate) {
        this.activeitem = null;
        menuitem.children('a.ui-menuitem-link').removeClass('ui-state-hover');
        menuitem.removeClass('ui-menuitem-active');
        
        if(animate)
            menuitem.children('ul.ui-menu-child:visible').fadeOut('fast');
        else
            menuitem.children('ul.ui-menu-child:visible').hide();
    },
    
    activate: function(menuitem) {
        this.highlight(menuitem);

        var submenu = menuitem.children('ul.ui-menu-child');
        if(submenu.length == 1) {
            this.showSubmenu(menuitem, submenu);
        }
    },
    
    reactivate: function(menuitem) {
        this.activeitem = menuitem;
        var submenu = menuitem.children('ul.ui-menu-child'),
        activeChilditem = submenu.children('li.ui-menuitem-active:first'),
        _self = this;
        
        if(activeChilditem.length == 1) {
            _self.deactivate(activeChilditem);
        }
    },
    
    highlight: function(menuitem) {
        this.activeitem = menuitem;
        menuitem.children('a.ui-menuitem-link').addClass('ui-state-hover');
        menuitem.addClass('ui-menuitem-active');
    },
    
    showSubmenu: function(menuitem, submenu) {
        
        submenu.css({
            'left': menuitem.outerWidth()
            ,'top': 0
            ,'z-index': ++PrimeFaces.zindex
        });

        submenu.show();
    }
    
});

/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menubar = PrimeFaces.widget.TieredMenu.extend({
    
    showSubmenu: function(menuitem, submenu) {
        submenu.css('z-index', ++PrimeFaces.zindex);

        if(menuitem.parent().hasClass('ui-menu-child')) {    //submenu menuitem
            submenu.css({
                'left': menuitem.outerWidth()
                ,'top': 0
            });
        } 
        else {  
            submenu.css({                                    //root menuitem         
                'left': 0
                ,'top': menuitem.outerHeight()
            });
            
        }

        submenu.show();
    }
});

/**
 * PrimeFaces SlideMenu Widget
 */
PrimeFaces.widget.SlideMenu = PrimeFaces.widget.Menu.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        //elements
        this.submenus = this.jq.find('ul.ui-menu-list');
        this.wrapper = this.jq.children('div.ui-slidemenu-wrapper');
        this.content = this.wrapper.children('div.ui-slidemenu-content');
        this.rootList = this.content.children('ul.ui-menu-list');
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        this.backward = this.wrapper.children('div.ui-slidemenu-backward');
                
        //config
        this.stack = [];
        this.jqWidth = this.jq.width();
                     
        var _self = this;
        
        if(!this.jq.hasClass('ui-menu-dynamic')) {
            
            if(this.jq.is(':not(:visible)')) {
                var hiddenParent = this.jq.parents('.ui-hidden-container:first'),
                hiddenParentWidget = hiddenParent.data('widget');

                if(hiddenParentWidget) {
                    hiddenParentWidget.addOnshowHandler(function() {
                        return _self.render();
                    });
                }
            }
            else {
                this.render();
            }
        }
                
        this.bindEvents();
    },
    
    bindEvents: function() {
        var _self = this;
        
        this.links.mouseenter(function() {
           $(this).addClass('ui-state-hover'); 
        })
        .mouseleave(function() {
           $(this).removeClass('ui-state-hover'); 
        })
        .click(function() {
           var link = $(this),
           submenu = link.next();
           
           if(submenu.length == 1) {
               _self.forward(submenu)
           }
        });
        
        this.backward.click(function() {
            _self.back();
        });
    },
    
    forward: function(submenu) {
        var _self = this;
        
        this.push(submenu);
        
        var rootLeft = -1 * (this.depth() * this.jqWidth);
        
        submenu.show().css({
            left: this.jqWidth
        });
               
        this.rootList.animate({
            left: rootLeft
        }, 500, 'easeInOutCirc', function() {
            if(_self.backward.is(':hidden')) {
                _self.backward.fadeIn('fast');
            }
        });
    },
    
    back: function() {
        var _self = this,
        last = this.pop(),
        depth = this.depth();
            
        var rootLeft = -1 * (depth * this.jqWidth);

        this.rootList.animate({
            left: rootLeft
        }, 500, 'easeInOutCirc', function() {
            last.hide();
            
            if(depth == 0) {
                _self.backward.fadeOut('fast');
            }
        });
    },
    
    push: function(submenu) {
        this.stack.push(submenu);
    },
    
    pop: function() {
        return this.stack.pop();
    },
    
    last: function() {
        return this.stack[this.stack.length - 1];
    },
    
    depth: function() {
        return this.stack.length;
    },
    
    render: function() {
        this.submenus.width(this.jq.width());
        this.wrapper.height(this.rootList.outerHeight(true) + this.backward.outerHeight(true));
        this.content.height(this.rootList.outerHeight(true));
        this.rendered = true;
    },
    
    show: function(e) {                
        this.align();
        this.jq.css('z-index', ++PrimeFaces.zindex).show();
        
        if(!this.rendered) {
            this.render();
        }

        e.preventDefault();
    }
});


/**
 * PrimeFaces PlainMenu Widget
 */
PrimeFaces.widget.PlainMenu = PrimeFaces.widget.Menu.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.menuitemLinks = this.jq.find('.ui-menuitem-link:not(.ui-state-disabled)');

        //events
        this.bindEvents();
    }
    
    ,bindEvents: function() {  
        var _self = this;

        this.menuitemLinks.mouseenter(function(e) {
            $(this).addClass('ui-state-hover');
        }).mouseleave(function(e) {
            $(this).removeClass('ui-state-hover');
        });

        if(this.cfg.dynamic) {
            this.menuitemLinks.click(function() {
                _self.hide();
            });  
        }   
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
PrimeFaces.widget.ContextMenu = PrimeFaces.widget.TieredMenu.extend({
    
    init: function(cfg) {
        cfg.autoDisplay = true;
        this._super(cfg);

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
    
    bindDocumentHandler: function() {
        var _self = this;
        
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