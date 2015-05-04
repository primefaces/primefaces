/**
 * PrimeFaces Menu Widget
 */
PrimeFaces.widget.Menu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        if(this.cfg.overlay) {
            this.initOverlay();
        }
        
        this.keyboardTarget = this.jq.children('.ui-helper-hidden-accessible');
    },
    
    initOverlay: function() {
        var $this = this;
        
        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.trigger);

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
            var trigger = $(this);
            
            if($this.jq.is(':visible')) {
                $this.hide();
            }
            else {
                $this.show();
                
                if(trigger.is(':button')) {
                    trigger.addClass('ui-state-focus');
                }
                
                e.preventDefault();
            }   
        });

        //hide overlay on document click
        var hideNS = 'mousedown.' + this.id;
        $(document.body).off(hideNS).on(hideNS, function (e) {            
            if($this.jq.is(":hidden")) {
                return;
            }

            //do nothing if mousedown is on trigger
            var target = $(e.target);
            if(target.is($this.trigger.get(0))||$this.trigger.has(target).length > 0) {
                return;
            }

            //hide if mouse is outside of overlay except trigger
            var offset = $this.jq.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.jq.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.jq.height()) {
                
                $this.hide(e);
            }
        });

        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
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
    
    show: function() {
        this.align();
        this.jq.css('z-index', ++PrimeFaces.zindex).show();
    },
    
    hide: function() {
        this.jq.fadeOut('fast');
        
        if(this.trigger && this.trigger.is(':button')) {
            this.trigger.removeClass('ui-state-focus');
        }
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
        
        this.cfg.toggleEvent = this.cfg.toggleEvent||'hover';
        this.links = this.jq.find('a.ui-menuitem-link:not(.ui-state-disabled)');
        this.rootLinks = this.jq.find('> ul.ui-menu-list > .ui-menuitem > .ui-menuitem-link');
                
        this.bindEvents();
    },
    
    bindEvents: function() {        
        this.bindItemEvents();
        this.bindKeyEvents();
        this.bindDocumentHandler();
    },
    
    bindItemEvents: function() {        
        if(this.cfg.toggleEvent === 'hover')
            this.bindHoverModeEvents();
        else if(this.cfg.toggleEvent === 'click')
            this.bindClickModeEvents();
    },
    
    bindHoverModeEvents: function() {
        var $this = this;
        
        this.links.mouseenter(function() {
            var link = $(this),
            menuitem = link.parent();

            var activeSibling = menuitem.siblings('.ui-menuitem-active');
            if(activeSibling.length === 1) {
                activeSibling.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(activeSibling);
            }

            if($this.cfg.autoDisplay||$this.active) {
                if(menuitem.hasClass('ui-menuitem-active'))
                    $this.reactivate(menuitem);
                else
                    $this.activate(menuitem);
            }
            else {
                $this.highlight(menuitem);
            }
        });

        this.rootLinks.click(function(e) {
            var link = $(this),
            menuitem = link.parent(),
            submenu = menuitem.children('ul.ui-menu-child');

            $this.itemClick = true;

            if(submenu.length === 1) {
                if(submenu.is(':visible')) {
                    $this.active = false;
                    $this.deactivate(menuitem);
                }
                else {                     
                    $this.active = true;
                    $this.highlight(menuitem);
                    $this.showSubmenu(menuitem, submenu);
                }
            }
        });

        this.links.filter('.ui-submenu-link').click(function(e) {
            $this.itemClick = true;
            e.preventDefault();
        });

        this.jq.find('ul.ui-menu-list').mouseleave(function(e) {
           if($this.activeitem) {
               $this.deactivate($this.activeitem);
           }

           e.stopPropagation();
        });
    },
    
    bindClickModeEvents: function() {
        var $this = this;
        
        this.links.mouseenter(function() {
            var menuitem = $(this).parent();
            
            if(!menuitem.hasClass('ui-menuitem-active')) {
                menuitem.addClass('ui-menuitem-highlight').children('a.ui-menuitem-link').addClass('ui-state-hover');
            }
        })
        .mouseleave(function() {
            var menuitem = $(this).parent();

            if(!menuitem.hasClass('ui-menuitem-active')) {
                menuitem.removeClass('ui-menuitem-highlight').children('a.ui-menuitem-link').removeClass('ui-state-hover');
            }
        });

        this.links.filter('.ui-submenu-link').on('click.tieredMenu', function(e) {
            var link = $(this),
            menuitem = link.parent(),
            submenu = menuitem.children('ul.ui-menu-child');

            $this.itemClick = true;

            var activeSibling = menuitem.siblings('.ui-menuitem-active');
            if(activeSibling.length) {
                activeSibling.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
                $this.deactivate(activeSibling);
            }

            if(submenu.length) {
                if(submenu.is(':visible')) {
                    $this.deactivate(menuitem);
                    menuitem.addClass('ui-menuitem-highlight').children('a.ui-menuitem-link').addClass('ui-state-hover');
                }
                else {
                    menuitem.addClass('ui-menuitem-active').children('a.ui-menuitem-link').removeClass('ui-state-hover').addClass('ui-state-active');
                    $this.showSubmenu(menuitem, submenu);
                }
            }

            e.preventDefault();
        });
    },
            
    bindKeyEvents: function() {
        //not implemented
    },
    
    bindDocumentHandler: function() {
        var $this = this,
        clickNS = 'click.' + this.id;
        
        $(document.body).off(clickNS).on(clickNS, function(e) {
            if($this.itemClick) {
                $this.itemClick = false;
                return;
            }
            
            $this.reset();
        });
    },
    
    deactivate: function(menuitem, animate) {
        this.activeitem = null;
        menuitem.children('a.ui-menuitem-link').removeClass('ui-state-hover ui-state-active');
        menuitem.removeClass('ui-menuitem-active ui-menuitem-highlight');
        
        if(animate)
            menuitem.children('ul.ui-menu-child').fadeOut('fast');
        else
            menuitem.children('ul.ui-menu-child').hide();
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
        var pos ={
            my: 'left top',
            at: 'right top',
            of: menuitem,
            collision: 'flipfit'
        };

        submenu.css('z-index', ++PrimeFaces.zindex)
            .show()
            .position(pos);
    },
            
    reset: function() {
        var $this = this;
        this.active = false;

        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });
    }
    
});

/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menubar = PrimeFaces.widget.TieredMenu.extend({
    
    showSubmenu: function(menuitem, submenu) {
        var win = $(window),
        submenuOffsetTop = null,
        submenuCSS = {
            'z-index': ++PrimeFaces.zindex
        };
        
        if(menuitem.parent().hasClass('ui-menu-child')) {
            submenuCSS.left = menuitem.outerWidth();
            submenuCSS.top = 0; 
            submenuOffsetTop = menuitem.offset().top - win.scrollTop();
        } 
        else {
            submenuCSS.left = 0;
            submenuCSS.top = menuitem.outerHeight(); 
            menuitem.offset().top - win.scrollTop();
            submenuOffsetTop = menuitem.offset().top + submenuCSS.top - win.scrollTop();
        }
        
        //adjust height within viewport
        submenu.css('height', 'auto');
        if((submenuOffsetTop + submenu.outerHeight()) > win.height()) {
            submenuCSS.overflow = 'auto';
            submenuCSS.height = win.height() - (submenuOffsetTop + 20);
        }
        
        submenu.css(submenuCSS).show();
    },
          
    //@Override
    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('focus.menubar', function(e) {
            $this.highlight($this.links.eq(0).parent());
        })
        .on('blur.menubar', function() {
            $this.reset();
        })
        .on('keydown.menu', function(e) {
            var currentitem = $this.activeitem;
            if(!currentitem) {
                return;
            }
            
            var isRootLink = !currentitem.closest('ul').hasClass('ui-menu-child'),
            keyCode = $.ui.keyCode;
            
            switch(e.which) {
                    case keyCode.LEFT:
                        if(isRootLink) {
                            var prevItem = currentitem.prevAll('.ui-menuitem:not(.ui-menubar-options):first');
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }
                            
                            e.preventDefault();
                        }
                        else {
                            if(currentitem.hasClass('ui-menu-parent') && currentitem.children('.ui-menu-child').is(':visible')) {
                                $this.deactivate(currentitem);
                                $this.highlight(currentitem);
                            }
                            else {
                                var parentItem = currentitem.parent().parent();
                                $this.deactivate(currentitem);
                                $this.deactivate(parentItem);
                                $this.highlight(parentItem);
                            }
                        }
                    break;
                    
                    case keyCode.RIGHT:
                        if(isRootLink) {
                            var nextItem = currentitem.nextAll('.ui-menuitem:not(.ui-menubar-options):first');
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }

                            e.preventDefault();
                        }
                        else {
                            if(currentitem.hasClass('ui-menu-parent')) {
                                var submenu = currentitem.children('.ui-menu-child');
                                
                                if(submenu.is(':visible'))
                                    $this.highlight(submenu.children('.ui-menuitem:first'));
                                else
                                    $this.activate(currentitem);
                            }
                        }
                    break;
                    
                    case keyCode.UP:
                        if(!isRootLink) {         
                            var prevItem = currentitem.prev('.ui-menuitem');
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }                   
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.DOWN:
                        if(isRootLink) {
                            var submenu = currentitem.children('ul.ui-menu-child');
                            if(submenu.is(':visible'))
                                $this.highlight(submenu.children('.ui-menuitem:first'));
                            else
                                $this.activate(currentitem);                        
                        }
                        else {
                            var nextItem = currentitem.next('.ui-menuitem');
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:
                        currentitem.children('.ui-menuitem-link').trigger('click');
                        $this.jq.blur();
                        e.preventDefault();
                    break;
                    
            }        
        });
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
        this.rendered = false;
                
        //config
        this.stack = [];
        this.jqWidth = this.jq.width();
        
        if(!this.jq.hasClass('ui-menu-dynamic')) {
            
            if(this.jq.is(':not(:visible)')) {
                var hiddenParent = this.jq.closest('.ui-hidden-container'),
                hiddenParentWidgetVar = hiddenParent.data('widget'),
                $this = this;

                if(hiddenParentWidgetVar) {
                    var hiddenParentWidget = PF(hiddenParentWidgetVar);
                    
                    if(hiddenParentWidget) {
                        hiddenParentWidget.addOnshowHandler(function() {
                            return $this.render();
                        });
                    }
                }
            }
            else {
                this.render();
            }
        }
                
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.links.mouseenter(function() {
           $(this).addClass('ui-state-hover'); 
        })
        .mouseleave(function() {
           $(this).removeClass('ui-state-hover'); 
        })
        .click(function(e) {
            var link = $(this),
            submenu = link.next();
           
            if(submenu.length) {
               $this.forward(submenu);
               e.preventDefault();
            }
        });
        
        this.backward.click(function() {
            $this.back();
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
            if(last !== null) {
                last.hide();
            }
            
            if(depth == 0) {
                _self.backward.fadeOut('fast');
            }
        });
    },
    
    push: function(submenu) {
        this.stack.push(submenu);
    },
    
    pop: function() {
        return this.stack.length !== 0 ? this.stack.pop() : null;
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
    
    show: function() {                
        this.align();
        this.jq.css('z-index', ++PrimeFaces.zindex).show();
        
        if(!this.rendered) {
            this.render();
        }
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
        
        if(this.cfg.toggleable) {
            this.collapsedIds = [];
            this.stateKey = 'menu-' + this.id;
            this.restoreState();
        }
    },
            
    bindEvents: function() {  
        var $this = this;
        
        this.menuitemLinks.mouseenter(function(e) {
            if($this.jq.is(':focus')) {
                $this.jq.blur();
            }
            
            $(this).addClass('ui-state-hover');
        })
        .mouseleave(function(e) {
            $(this).removeClass('ui-state-hover');
        });

        if(this.cfg.overlay) {
            this.menuitemLinks.click(function() {
                $this.hide();
            });  
        }
        
        if(this.cfg.toggleable) {
            this.jq.find('> .ui-menu-list > .ui-widget-header').on('mouseover.menu', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.menu', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.menu', function(e) {
                var header = $(this);

                if(header.find('> h3 > .ui-icon').hasClass('ui-icon-triangle-1-s'))
                    $this.collapseSubmenu(header, true);
                else
                    $this.expandSubmenu(header, true);
                
                PrimeFaces.clearSelection();
                e.preventDefault();
            });
        }
        
        this.keyboardTarget.on('focus.menu', function() {
            $this.menuitemLinks.eq(0).addClass('ui-state-hover');
        })
        .on('blur.menu', function() {
            $this.menuitemLinks.filter('.ui-state-hover').removeClass('ui-state-hover');
        })
        .on('keydown.menu', function(e) {
            var currentLink = $this.menuitemLinks.filter('.ui-state-hover'),
            keyCode = $.ui.keyCode;
            
            switch(e.which) {
                    case keyCode.UP:
                        var prevItem = currentLink.parent().prevAll('.ui-menuitem:first');
                        if(prevItem.length) {
                            currentLink.removeClass('ui-state-hover');
                            prevItem.children('.ui-menuitem-link').addClass('ui-state-hover');
                        }

                        e.preventDefault();
                    break;
                    
                    case keyCode.DOWN:
                        var nextItem = currentLink.parent().nextAll('.ui-menuitem:first');
                        if(nextItem.length) {
                            currentLink.removeClass('ui-state-hover');
                            nextItem.children('.ui-menuitem-link').addClass('ui-state-hover');
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:
                        currentLink.trigger('click');
                        $this.jq.blur();
                        e.preventDefault();
                    break;
                    
            }        
        });
    },
            
    collapseSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');
        
        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        items.hide();

        if(stateful) {
            this.collapsedIds.push(header.attr('id'));
            this.saveState();
        }
    },

    expandSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');

        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        items.show();

        if(stateful) {
            var id = header.attr('id');        
            this.collapsedIds = $.grep(this.collapsedIds, function(value) {
                return (value !== id);
            });
            this.saveState();
        }
    },
    
    saveState: function() {        
        PrimeFaces.setCookie(this.stateKey, this.collapsedIds.join(','));
    },
    
    restoreState: function() {
        var collapsedIdsAsString = PrimeFaces.getCookie(this.stateKey);

        if(collapsedIdsAsString) {
            this.collapsedIds = collapsedIdsAsString.split(',');
            
            for(var i = 0 ; i < this.collapsedIds.length; i++) {
                this.collapseSubmenu($(PrimeFaces.escapeClientId(this.collapsedIds[i])), false);
            }
        }
    },
    
    clearState: function() {
        PrimeFaces.setCookie(this.stateKey, null);
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
            this.appendPanel();
        }
    },
        
    bindEvents: function() {  
        var $this = this;

        //button visuals
        this.button.mouseover(function(){
            if(!$this.button.hasClass('ui-state-focus')) {
                $this.button.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            if(!$this.button.hasClass('ui-state-focus')) {
                $this.button.removeClass('ui-state-hover ui-state-active');
            }
        }).mousedown(function() {
            $(this).removeClass('ui-state-focus ui-state-hover').addClass('ui-state-active');
        }).mouseup(function() {
            var el = $(this);
            el.removeClass('ui-state-active')

            if($this.menu.is(':visible')) {
                el.addClass('ui-state-hover');
                $this.hide();
            } 
            else {
                el.addClass('ui-state-focus');
                $this.show();
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
            $this.button.removeClass('ui-state-focus');
            $this.hide();
        });

        /**
        * handler for document mousedown to hide the overlay
        **/
        var hideNS = 'mousedown.' + this.id;
        $(document.body).off(hideNS).on(hideNS, function (e) {
            //do nothing if hidden already
            if($this.menu.is(":hidden") || $this.cfg.disabled) {
                return;
            }

            //do nothing if mouse is on button
            var target = $(e.target);
            if(target.is($this.button)||$this.button.has(target).length > 0) {
                return;
            }

            //hide overlay if mouse is outside of overlay except button
            var offset = $this.menu.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.menu.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.menu.height()) {

                $this.button.removeClass('ui-state-focus ui-state-hover');
                $this.hide();
            }
        });

        //Realign overlay on window resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.menu.is(':visible')) {
                $this.alignPanel();
            }
        });

        //aria
        this.button.attr('role', 'button').attr('aria-disabled', this.button.is(':disabled'));
    },
    
    appendPanel: function() {
        var container = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo): $(document.body);
        
        if(!container.is(this.jq)) {
            container.children(this.menuId).remove();
            this.menu.appendTo(container);
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
        this.menu.css({left:'', top:'','z-index': ++PrimeFaces.zindex});
        
        if(this.menu.parent().is(this.jq)) {
            this.menu.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.menu.position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.button
            });
        }
    }
    
});

/**
 * PrimeFaces ContextMenu Widget
 */
PrimeFaces.widget.ContextMenu = PrimeFaces.widget.TieredMenu.extend({
    
    init: function(cfg) {
        cfg.autoDisplay = true;
        this._super(cfg);
        this.cfg.selectionMode = this.cfg.selectionMode||'multiple';
        
        var _self = this,
        documentTarget = (this.cfg.target === undefined);

        //event
        this.cfg.event = this.cfg.event||'contextmenu';

        //target
        this.jqTargetId = documentTarget ? document : PrimeFaces.escapeClientId(this.cfg.target);
        this.jqTarget = $(this.jqTargetId);
        
        //append to body
        if(!this.jq.parent().is(document.body)) {
            this.jq.appendTo('body');
        }
        
        //attach contextmenu
        if(documentTarget) {
            $(document).off('contextmenu.ui-contextmenu').on('contextmenu.ui-contextmenu', function(e) {
                _self.show(e);
            });
        }
        else {
            if(this.cfg.type === 'DataTable') {
                this.bindDataTable();
            }
            else if(this.cfg.type === 'TreeTable') {
                this.bindTreeTable();
            }
            else if(this.cfg.type === 'Tree') {
                this.bindTree();
            }
            else {                
                var event = this.cfg.event + '.ui-contextmenu';
                
                $(document).off(event, this.jqTargetId).on(event, this.jqTargetId, null, function(e) {
                    _self.show(e);
                });
            }
            
        }
    },
    
    bindDataTable: function() {
        var rowSelector = this.jqTargetId + ' tbody.ui-datatable-data > tr.ui-widget-content.ui-datatable-selectable:not(.ui-datatable-empty-message)',
        event = this.cfg.event + '.datatable',
        $this = this;
        
        $(document).off(event, rowSelector)
                    .on(event, rowSelector, null, function(e) {
                        var widget = PrimeFaces.widgets[$this.cfg.targetWidgetVar];
                        
                        if(widget.cfg.selectionMode) {
                            widget.onRowRightClick(e, this, $this.cfg.selectionMode);

                            $this.show(e);
                        }
                        else if(widget.cfg.editMode === 'cell') {
                            var target = $(e.target),
                            cell = target.is('td.ui-editable-column') ? target : target.parents('td.ui-editable-column:first');
                            
                            if(widget.contextMenuCell) {
                                widget.contextMenuCell.removeClass('ui-state-highlight');
                            }
                            
                            widget.contextMenuClick = true;
                            widget.contextMenuCell = cell;
                            widget.contextMenuCell.addClass('ui-state-highlight');
                            
                            $this.show(e);
                        }
                    });
    },
    
    bindTreeTable: function() {
        var rowSelector = this.jqTargetId + ' .ui-treetable-data > ' + (this.cfg.nodeType ? 'tr.ui-treetable-selectable-node.' + this.cfg.nodeType : 'tr.ui-treetable-selectable-node'),
        event = this.cfg.event + '.treetable',
        _self = this;
        
        $(document).off(event, rowSelector)
                    .on(event, rowSelector, null, function(e) {
                    	PrimeFaces.widgets[_self.cfg.targetWidgetVar].onRowRightClick(e, $(this));
                        _self.show(e);
                        e.preventDefault();
                    });
    },
    
    bindTree: function() {
        var nodeContentSelector = this.jqTargetId + ' .ui-tree-selectable',
        nodeEvent = this.cfg.nodeType ? this.cfg.event + '.treenode.' + this.cfg.nodeType : this.cfg.event + '.treenode',
        containerEvent = this.cfg.event + '.tree',
        $this = this;
                
        $(document).off(nodeEvent, nodeContentSelector)
                    .on(nodeEvent, nodeContentSelector, null, function(e) {
                        var nodeContent = $(this);
                        
                        if($this.cfg.nodeType === undefined || nodeContent.parent().data('nodetype') === $this.cfg.nodeType) {
                        	PrimeFaces.widgets[$this.cfg.targetWidgetVar].nodeRightClick(e, nodeContent);
                            $this.show(e);
                            e.preventDefault();
                        }
                    });
                                    
        $(document).off(containerEvent, this.jqTargetId)
                    .on(containerEvent, this.jqTargetId, null, function(e) {
                if(PrimeFaces.widgets[$this.cfg.targetWidgetVar].isEmpty()) {
                    $this.show(e);
                    e.preventDefault();
                }
        });
    },
    
    refresh: function(cfg) {
        var jqId = PrimeFaces.escapeClientId(cfg.id),
        instances = $(jqId);
        
        if(instances.length > 1) {
            $(document.body).children(jqId).remove();
        }

        this.init(cfg);
    },
    
    bindItemEvents: function() {
        this._super();
        
        var _self = this;
        
        //hide menu on item click
        this.links.bind('click', function() {
            _self.hide();
        });
    },
    
    bindDocumentHandler: function() {
        var $this = this,
        clickNS = 'click.' + this.id;
        
        //hide overlay when document is clicked
        $(document.body).off(clickNS).on(clickNS, function(e) {
            if($this.jq.is(":hidden")) {
                return;
            }
                        
            $this.hide();
        });
    },
    
    show: function(e) { 
        if(this.cfg.targetFilter && $(e.target).is(':not(' + this.cfg.targetFilter + ')')) {
            return;
        }
        
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
        
        if(this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this, e);
            if(retVal === false) {
                return;
            }
        }

        this.jq.css({
            'left': left,
            'top': top,
            'z-index': ++PrimeFaces.zindex
        }).show();

        e.preventDefault();
    },
    
    hide: function() {
        var _self = this;
        
        //hide submenus
        this.jq.find('li.ui-menuitem-active').each(function() {
            _self.deactivate($(this), true);
        });
        
        this.jq.fadeOut('fast');
    },
    
    isVisible: function() {
        return this.jq.is(':visible');
    },
    
    getTarget: function() {
        return this.jqTarget;
    }

});

/**
 * PrimeFaces MegaMenu Widget
 */
PrimeFaces.widget.MegaMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.vertical = this.jq.hasClass('ui-megamenu-vertical');
        this.rootList = this.jq.children('ul.ui-menu-list');
        this.rootLinks = this.rootList.find('> li.ui-menuitem > a.ui-menuitem-link:not(.ui-state-disabled)');                  
        this.subLinks = this.jq.find('.ui-menu-child a.ui-menuitem-link:not(.ui-state-disabled)');
        
        if(this.cfg.activeIndex !== undefined) {
            this.rootLinks.eq(this.cfg.activeIndex).addClass('ui-state-active');
        }
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
  
        this.rootLinks.mouseenter(function(e) {
            var link = $(this),
            menuitem = link.parent();
            
            var current = menuitem.siblings('.ui-menuitem-active');
            if(current.length > 0) {
                $this.deactivate(current, false);
            }
            
            if($this.cfg.autoDisplay||$this.active) {
                $this.activate(menuitem);
            }
            else {
                $this.highlight(menuitem);
            }
            
        });
        
        if(this.cfg.autoDisplay === false) {
            this.rootLinks.data('primefaces-megamenu', this.id).find('*').data('primefaces-megamenu', this.id)
            
            this.rootLinks.click(function(e) {
                var link = $(this),
                menuitem = link.parent(),
                submenu = link.next();

                if(submenu.length === 1) {
                    if(submenu.is(':visible')) {
                        $this.active = false;
                        $this.deactivate(menuitem, true);
                    }
                    else {                                        
                        $this.active = true;
                        $this.activate(menuitem);
                    }
                }
                
                e.preventDefault();
            });
        }
        else {
            this.rootLinks.filter('.ui-submenu-link').click(function(e) {
                e.preventDefault();
            });
        }

        this.subLinks.mouseenter(function() {
            $(this).addClass('ui-state-hover');
        })
        .mouseleave(function() {
            $(this).removeClass('ui-state-hover');
        });
        
        this.rootList.mouseleave(function(e) {
            var activeitem = $this.rootList.children('.ui-menuitem-active');
            if(activeitem.length === 1) {
                $this.deactivate(activeitem, false);
            }
        });
        
        this.rootList.find('> li.ui-menuitem > ul.ui-menu-child').mouseleave(function(e) {            
            e.stopPropagation();
        });
        
        $(document.body).click(function(e) {
            var target = $(e.target);
            if(target.data('primefaces-megamenu') === $this.id) {
                return;
            }
            
            $this.active = false;
            $this.deactivate($this.rootList.children('li.ui-menuitem-active'), true);
        });
    },
    
    deactivate: function(menuitem, animate) {
        var link = menuitem.children('a.ui-menuitem-link'),
        submenu = link.next();
        
        menuitem.removeClass('ui-menuitem-active');
        link.removeClass('ui-state-hover');
        
        if(submenu.length > 0) {
            if(animate)
                submenu.fadeOut('fast');
            else
                submenu.hide();
        }
    },
    
    highlight: function(menuitem) {
        var link = menuitem.children('a.ui-menuitem-link');

        menuitem.addClass('ui-menuitem-active');
        link.addClass('ui-state-hover');
    },
    
    activate: function(menuitem) {
        var submenu = menuitem.children('.ui-menu-child'),
        $this = this;
        
        $this.highlight(menuitem);
        
        if(submenu.length > 0) {
            $this.showSubmenu(menuitem, submenu);
        }
    },
    
    showSubmenu: function(menuitem, submenu) {
        var pos = null;
        
        if(this.cfg.vertical) {
            pos = {
                my: 'left top',
                at: 'right top',
                of: menuitem,
                collision: 'flipfit'
            };
        }
        else {
            pos = {
                my: 'left top',
                at: 'left bottom',
                of: menuitem,
                collision: 'flipfit'
            };
        }

        submenu.css('z-index', ++PrimeFaces.zindex)
                .show()
                .position(pos);
    }
    
});
/**
 * PrimeFaces PanelMenu Widget
 */
PrimeFaces.widget.PanelMenu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.headers = this.jq.find('> .ui-panelmenu-panel > h3.ui-panelmenu-header:not(.ui-state-disabled)');
        this.menuitemLinks = this.jq.find('.ui-menuitem-link:not(.ui-state-disabled)');
        this.treeLinks = this.jq.find('.ui-menu-parent > .ui-menuitem-link:not(.ui-state-disabled)');
        this.bindEvents();
        
        if(this.cfg.stateful) {
            this.stateKey = 'panelMenu-' + this.id;
        }
        
        this.restoreState();
    },

    bindEvents: function() {
        var _self = this;

        this.headers.mouseover(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')) {
                element.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')) {
                element.removeClass('ui-state-hover');
            }
        }).click(function(e) {
            var header = $(this);

            if(header.hasClass('ui-state-active'))
                _self.collapseRootSubmenu($(this));
            else
                _self.expandRootSubmenu($(this), false);

            e.preventDefault();
        });

        this.menuitemLinks.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        });

        this.treeLinks.click(function(e) {
            var link = $(this),
            submenu = link.parent(),
            submenuList = link.next();

            if(submenuList.is(':visible'))
                _self.collapseTreeItem(submenu);
            else
                _self.expandTreeItem(submenu, false);

            e.preventDefault();
        });
    },

    collapseRootSubmenu: function(header) {
        var panel = header.next();

        header.attr('aria-expanded', false).removeClass('ui-state-active ui-corner-top').addClass('ui-state-hover ui-corner-all')
                            .children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        panel.attr('aria-hidden', true).slideUp('normal', 'easeInOutCirc');
        
        this.removeAsExpanded(panel);
    },

    expandRootSubmenu: function(header, restoring) {
        var panel = header.next();

        header.attr('aria-expanded', false).addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
                .children('.ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        if(restoring) {
            panel.attr('aria-hidden', false).show();
        }
        else {
            panel.attr('aria-hidden', false).slideDown('normal', 'easeInOutCirc');
            
            this.addAsExpanded(panel);
        }
    },

    expandTreeItem: function(submenu, restoring) {
        submenu.find('> .ui-menuitem-link > .ui-panelmenu-icon').addClass('ui-icon-triangle-1-s');
        submenu.children('.ui-menu-list').show();
        
        if(!restoring) {
            this.addAsExpanded(submenu);
        }
    },

    collapseTreeItem: function(submenu) {
        submenu.find('> .ui-menuitem-link > .ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s');
        submenu.children('.ui-menu-list').hide();
        
        this.removeAsExpanded(submenu);
    },
    
    saveState: function() {
        if(this.cfg.stateful) {
            var expandedNodeIds = this.expandedNodes.join(',');

            PrimeFaces.setCookie(this.stateKey, expandedNodeIds, {path:'/'});
        }
    },
    
    restoreState: function() {
        var expandedNodeIds = null; 
        
        if(this.cfg.stateful) {
            expandedNodeIds = PrimeFaces.getCookie(this.stateKey);
        }

        if(expandedNodeIds) {
            this.collapseAll();
            this.expandedNodes = expandedNodeIds.split(',');
            
            for(var i = 0 ; i < this.expandedNodes.length; i++) {
                var element = $(PrimeFaces.escapeClientId(this.expandedNodes[i]));
                if(element.is('div.ui-panelmenu-content'))
                    this.expandRootSubmenu(element.prev(), true);
                else if(element.is('li.ui-menu-parent'))
                    this.expandTreeItem(element, true);
            }
        }
        else {
            this.expandedNodes = [];
            var activeHeaders = this.headers.filter('.ui-state-active'),
            activeTreeSubmenus = this.jq.find('.ui-menu-parent > .ui-menu-list:not(.ui-helper-hidden)');
    
            for(var i = 0; i < activeHeaders.length; i++) {
                this.expandedNodes.push(activeHeaders.eq(i).next().attr('id'));
            }
            
            for(var i = 0; i < activeTreeSubmenus.length; i++) {
                this.expandedNodes.push(activeTreeSubmenus.eq(i).parent().attr('id'));
            }
        }
    },
    
    removeAsExpanded: function(element) {
        var id = element.attr('id');
        
        this.expandedNodes = $.grep(this.expandedNodes, function(value) {
            return value != id;
        });
        
        this.saveState();
    },

    addAsExpanded: function(element) {
        this.expandedNodes.push(element.attr('id'));

        this.saveState();
    },
    
    clearState: function() {
        if(this.cfg.stateful) {
            PrimeFaces.deleteCookie(this.stateKey, {path:'/'});
        }
    },
    
    collapseAll: function() {
        this.headers.filter('.ui-state-active').each(function() {
            var header = $(this);
            header.removeClass('ui-state-active').children('.ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
            header.next().addClass('ui-helper-hidden');
        });
        
        this.jq.find('.ui-menu-parent > .ui-menu-list:not(.ui-helper-hidden)').each(function() {
            $(this).addClass('ui-helper-hidden').prev().children('.ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
        });
    }

});

/**
 * PrimeFaces TabMenu Widget
 */
PrimeFaces.widget.TabMenu = PrimeFaces.widget.Menu.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.items = this.jq.find('> .ui-tabmenu-nav > li:not(.ui-state-disabled)');

        this.bindEvents();
    },
    
    bindEvents: function() {
        this.items.on('mouseover.tabmenu', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-active')) {
                        element.addClass('ui-state-hover');
                    }
                })
                .on('mouseout.tabmenu', function(e) {
                    $(this).removeClass('ui-state-hover');
                });
    }
});