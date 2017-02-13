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
        this.itemMouseDown = false;
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
                
                if(target.is('.ui-menuitem-link') || target.closest('.ui-menuitem-link').length)
                    $this.itemMouseDown = true;
                else
                    $this.hide(e);
            }
        });
        
        var hideUpNS = 'mouseup.' + this.id;
        $(document.body).off(hideUpNS).on(hideUpNS, function (e) {
            if($this.itemMouseDown) {
                $this.hide(e);
                $this.itemMouseDown = false;
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
        var pos = null;
        
        if(menuitem.parent().hasClass('ui-menu-child')) {
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
                        var currentLink = currentitem.children('.ui-menuitem-link');
                        currentLink.trigger('click');
                        $this.jq.blur();
                        var href = currentLink.attr('href');
                        if(href && href !== '#') {
                            window.location.href = href;
                        }
                        
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
                $this = this;

                if(hiddenParent.length) {
                    PrimeFaces.addDeferredRender(this.id, hiddenParent.attr('id'), function() {
                        return $this.render();
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
        if(!this.rootList.is(':animated')) {
            var _self = this,
            last = this.pop(),
            depth = this.depth();

            var rootLeft = -1 * (depth * this.jqWidth);

            this.rootList.animate({
                left: rootLeft
            }, 500, 'easeInOutCirc', function() {
                if(last) {
                    last.hide();
                }

                if(depth == 0) {
                    _self.backward.fadeOut('fast');
                }
            });
        }
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
            
            this.trigger.on('keydown.ui-menu', function(e) {
                var keyCode = $.ui.keyCode;
            
                switch(e.which) {
                    case keyCode.DOWN:
                        $this.keyboardTarget.trigger('focus.menu');
                        e.preventDefault();
                    break;
                    
                    case keyCode.TAB:
                        if($this.jq.is(':visible')) {
                            $this.hide();
                        }
                    break;
                }
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
                        var href = currentLink.attr('href');
                        if(href && href !== '#') {
                            window.location.href = href;
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.ESCAPE:
                        $this.hide();
                        
                        if($this.cfg.overlay) {
                            $this.trigger.focus();
                        }
                    break;
                    
            }        
        });
    },
            
    collapseSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');
        
        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');

        items.filter('.ui-submenu-child').hide();

        if(stateful) {
            this.collapsedIds.push(header.attr('id'));
            this.saveState();
        }
    },

    expandSubmenu: function(header, stateful) {
        var items = header.nextUntil('li.ui-widget-header');

        header.attr('aria-expanded', false)
                .find('> h3 > .ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        items.filter('.ui-submenu-child').show();

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

        //keyboard support
        this.button.keydown(function(e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                    if($this.menu.is(':visible')) {
                        var highlightedItem = $this.menuitems.filter('.ui-state-hover'),
                        prevItems = highlightedItem.length ? highlightedItem.prevAll(':not(.ui-separator)') : null;

                        if(prevItems && prevItems.length) {
                            highlightedItem.removeClass('ui-state-hover');
                            prevItems.eq(0).addClass('ui-state-hover');
                        }
                    }
                    e.preventDefault();
                break;
                
                case keyCode.DOWN:
                    if($this.menu.is(':visible')) {
                        var highlightedItem = $this.menuitems.filter('.ui-state-hover'),
                        nextItems = highlightedItem.length ? highlightedItem.nextAll(':not(.ui-separator)') : $this.menuitems.eq(0);

                        if(nextItems.length) {
                            highlightedItem.removeClass('ui-state-hover');
                            nextItems.eq(0).addClass('ui-state-hover');
                        }
                    }
                    e.preventDefault();                    
                break;
                
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.SPACE:
                    if($this.menu.is(':visible'))
                        $this.menuitems.filter('.ui-state-hover').children('a').trigger('click');
                    else
                        $this.show();
                    
                    e.preventDefault();
                break;
                

                case keyCode.ESCAPE:
                case keyCode.TAB:
                    $this.hide();
                break;
            }
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
        this.menuitems.filter('.ui-state-hover').removeClass('ui-state-hover');
        
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
            var binded = false;

            if (this.cfg.targetWidgetVar) {
                var targetWidget = PrimeFaces.widgets[this.cfg.targetWidgetVar];

                if (targetWidget) {
                    if (typeof targetWidget.bindContextMenu === 'function') {
                        targetWidget.bindContextMenu(this, targetWidget, this.jqTargetId, this.cfg);
                        binded = true;
                    }
                }
                else {
                    PrimeFaces.warn("ContextMenu targets a widget which is not available yet. Please place the contextMenu after the target component. targetWidgetVar: " + this.cfg.targetWidgetVar);
                }
            }
            
            if (binded === false) {
                var event = this.cfg.event + '.ui-contextmenu';

                $(document).off(event, this.jqTargetId).on(event, this.jqTargetId, null, function(e) {
                    _self.show(e);
                });
            }
        }
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
        this.links.bind('click', function(e) {
            var target = $(e.target),
                submenuLink = target.hasClass('ui-submenu-link') ? target : target.closest('.ui-submenu-link');
                
            if(submenuLink.length) {
                return;
            }
            
            _self.hide();
        });
    },
    
    bindDocumentHandler: function() {
        var $this = this,
        clickNS = 'click.' + this.id;
        
        //hide overlay when document is clicked
        $(document.body).off(clickNS).on(clickNS, function(e) {
            var target = $(e.target),
                link = target.hasClass('ui-menuitem-link') ? target : target.closest('.ui-menuitem-link');
            
            if($this.jq.is(":hidden") || link.is('.ui-menuitem-link,.ui-state-disabled')) {
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

        if(this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this, e);
            if(retVal === false) {
                return;
            }
        }
        
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
        e.stopPropagation();
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
        this.keyboardTarget = this.jq.children('.ui-helper-hidden-accessible');
        
        if(this.cfg.activeIndex !== undefined) {
            this.rootLinks.eq(this.cfg.activeIndex).addClass('ui-state-hover').closest('li.ui-menuitem').addClass('ui-menuitem-active');
        }
        
        this.bindEvents();
        this.bindKeyEvents();
    },
    
    bindEvents: function() {
        var $this = this;
  
        this.rootLinks.mouseenter(function(e) {
            var link = $(this),
            menuitem = link.parent();
            
            var current = menuitem.siblings('.ui-menuitem-active');
            if(current.length > 0) {
                current.find('li.ui-menuitem-active').each(function() {
                    $this.deactivate($(this));
                });
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
                else {
                    var href = link.attr('href');
                    if(href && href !== '#') {
                        window.location.href = href;
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
            if($this.activeitem && !$this.isRootLink($this.activeitem)) {
                $this.deactivate($this.activeitem);    
            } 
            $this.highlight($(this).parent());
        })
        .mouseleave(function() {
            if($this.activeitem && !$this.isRootLink($this.activeitem)) {
                $this.deactivate($this.activeitem);    
            }
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
    
    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('focus.megamenu', function(e) {
            $this.highlight($this.rootLinks.eq(0).parent());
        })
        .on('blur.megamenu', function() {
            $this.reset();
        })
        .on('keydown.megamenu', function(e) {
            var currentitem = $this.activeitem;
            if(!currentitem) {
                return;
            }
            
            var isRootLink = $this.isRootLink(currentitem),
            keyCode = $.ui.keyCode;
            
            switch(e.which) {
                    case keyCode.LEFT:
                        if(isRootLink && !$this.cfg.vertical) {
                            var prevItem = currentitem.prevAll('.ui-menuitem:first');
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
                                var parentItem = currentitem.closest('ul.ui-menu-child').parent();
                                if(parentItem.length) {
                                    $this.deactivate(currentitem);
                                    $this.deactivate(parentItem);
                                    $this.highlight(parentItem);
                                }
                            }
                        }
                    break;
                    
                    case keyCode.RIGHT:
                        if(isRootLink && !$this.cfg.vertical) {
                            var nextItem = currentitem.nextAll('.ui-menuitem:visible:first');
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }

                            e.preventDefault();
                        }
                        else {
                            
                            if(currentitem.hasClass('ui-menu-parent')) {
                                var submenu = currentitem.children('.ui-menu-child');
                                if(submenu.is(':visible')) {
                                    $this.highlight(submenu.find('ul.ui-menu-list:visible > .ui-menuitem:visible:first'));
                                }
                                else {
                                    $this.activate(currentitem);
                                }
                            }
                        }
                    break;
                    
                    case keyCode.UP:
                        if(!isRootLink || $this.cfg.vertical) {         
                            var prevItem = $this.findPrevItem(currentitem);
                            if(prevItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(prevItem);
                            }                   
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.DOWN:
                        if(isRootLink && !$this.cfg.vertical) {
                            var submenu = currentitem.children('ul.ui-menu-child');
                            if(submenu.is(':visible')) {
                                var firstMenulist = $this.getFirstMenuList(submenu); 
                                $this.highlight(firstMenulist.children('.ui-menuitem:visible:first'));
                            }
                            else {
                                $this.activate(currentitem);
                            }
                        }
                        else {
                            var nextItem = $this.findNextItem(currentitem);
                            if(nextItem.length) {
                                $this.deactivate(currentitem);
                                $this.highlight(nextItem);
                            }
                        }
                        
                        e.preventDefault();
                    break;
                    
                    case keyCode.ENTER:
                    case keyCode.NUMPAD_ENTER:
                        var currentLink = currentitem.children('.ui-menuitem-link');
                        currentLink.trigger('click');
                        $this.jq.blur();
                        var href = currentLink.attr('href');
                        if(href && href !== '#') {
                            window.location.href = href;
                        }
                        $this.deactivate(currentitem);
                        e.preventDefault();
                    break;
                    
                    case keyCode.ESCAPE:
                        if(currentitem.hasClass('ui-menu-parent')) {
                            var submenu = currentitem.children('ul.ui-menu-list:visible');
                            if(submenu.length > 0) {
                                submenu.hide();
                            }
                        }
                        else {
                            var parentItem = currentitem.closest('ul.ui-menu-child').parent();
                            if(parentItem.length) {
                                $this.deactivate(currentitem);
                                $this.deactivate(parentItem);
                                $this.highlight(parentItem);
                            }
                        }
                        e.preventDefault();
                    break;    
            }        
        });
    },
    
    findPrevItem: function(menuitem) {
        var previtem = menuitem.prev('.ui-menuitem');

        if(!previtem.length) {
            var prevSubmenu = menuitem.closest('ul.ui-menu-list').prev('.ui-menu-list');

            if(!prevSubmenu.length) {
                prevSubmenu = menuitem.closest('td').prev('td').children('.ui-menu-list:visible:last');
            }

            if(prevSubmenu.length) {
                previtem = prevSubmenu.find('li.ui-menuitem:visible:last');
            }
        }
        return previtem;
    },
    
    findNextItem: function(menuitem) {
        var nextitem = menuitem.next('.ui-menuitem');

        if(!nextitem.length) {
            var nextSubmenu = menuitem.closest('ul.ui-menu-list').next('.ui-menu-list');
            if(!nextSubmenu.length) {
                nextSubmenu = menuitem.closest('td').next('td').children('.ui-menu-list:visible:first');
            }

            if(nextSubmenu.length) {
                nextitem = nextSubmenu.find('li.ui-menuitem:visible:first');
            }
        }
        return nextitem;
    },
    
    getFirstMenuList: function(submenu) {
        return submenu.find('.ui-menu-list:not(.ui-state-disabled):first');
    },
    
    isRootLink: function(menuitem) {
        var submenu = menuitem.closest('ul');
        return submenu.parent().hasClass('ui-menu');
    },
    
    reset: function() {
        var $this = this;
        this.active = false;

        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });
    },
    
    deactivate: function(menuitem, animate) {
        var link = menuitem.children('a.ui-menuitem-link'),
        submenu = link.next();
        
        menuitem.removeClass('ui-menuitem-active');
        link.removeClass('ui-state-hover');
        this.activeitem = null;
        
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
        this.activeitem = menuitem;
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
        this.menuContent = this.jq.find('> .ui-panelmenu-panel > .ui-panelmenu-content');
        this.menuitemLinks = this.menuContent.find('.ui-menuitem-link:not(.ui-state-disabled)');
        this.menuText = this.menuitemLinks.find('.ui-menuitem-text');
        this.treeLinks = this.menuContent.find('.ui-menu-parent > .ui-menuitem-link:not(.ui-state-disabled)');
        
        //keyboard support
        this.focusedItem = null;
        this.menuText.attr('tabindex', -1);
        
        //ScreenReader support
        this.menuText.attr('role', 'menuitem');
        this.treeLinks.find('> .ui-menuitem-text').attr('aria-expanded', false);
        
        this.bindEvents();
        
        if(this.cfg.stateful) {
            this.stateKey = 'panelMenu-' + this.id;
        }
        
        this.restoreState();
    },

    bindEvents: function() {
        var $this = this;

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
                $this.collapseRootSubmenu($(this));
            else
                $this.expandRootSubmenu($(this), false);

            $this.removeFocusedItem();
            header.focus();
            e.preventDefault();
        });

        this.menuitemLinks.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).click(function(e) {
            var currentLink = $(this);
            $this.focusItem(currentLink.closest('.ui-menuitem'));
            
            var href = currentLink.attr('href');
            if(href && href !== '#') {
                window.location.href = href;
            }
            e.preventDefault();
        });

        this.treeLinks.click(function(e) {
            var link = $(this),
            submenu = link.parent(),
            submenuList = link.next();

            if(submenuList.is(':visible'))
                $this.collapseTreeItem(submenu);
            else
                $this.expandTreeItem(submenu, false);

            e.preventDefault();
        });
        
        this.bindKeyEvents();
    },

    bindKeyEvents: function() {
        var $this = this;
        
        if(PrimeFaces.env.isIE()) {
            this.focusCheck = false;
        }
        
        this.headers.on('focus.panelmenu', function(){
            $(this).addClass('ui-menuitem-outline');
        })
        .on('blur.panelmenu', function(){
            $(this).removeClass('ui-menuitem-outline ui-state-hover');
        })
        .on('keydown.panelmenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                $(this).trigger('click');
                e.preventDefault();
            }       
        });
        
        this.menuContent.on('mousedown.panelmenu', function(e) {
            if($(e.target).is(':not(:input:enabled)')) {
                e.preventDefault();
            }
        }).on('focus.panelmenu', function(){
            if(!$this.focusedItem) {
                $this.focusItem($this.getFirstItemOfContent($(this)));
                if(PrimeFaces.env.isIE()) {
                    $this.focusCheck = false;
                }
            }
        });
        
        this.menuContent.off('keydown.panelmenu blur.panelmenu').on('keydown.panelmenu', function(e) {
            if(!$this.focusedItem) {
                return;
            }
            
            var keyCode = $.ui.keyCode;
            
            switch(e.which) {
                case keyCode.LEFT:
                    if($this.isExpanded($this.focusedItem)) {
                        $this.focusedItem.children('.ui-menuitem-link').trigger('click');
                    }
                    else {
                        var parentListOfItem = $this.focusedItem.closest('ul.ui-menu-list');
                        
                        if(parentListOfItem.parent().is(':not(.ui-panelmenu-content)')) {
                            $this.focusItem(parentListOfItem.closest('li.ui-menuitem'));
                        }
                    }
                        
                    e.preventDefault();
                break;

                case keyCode.RIGHT:
                    if($this.focusedItem.hasClass('ui-menu-parent') && !$this.isExpanded($this.focusedItem)) {
                        $this.focusedItem.children('.ui-menuitem-link').trigger('click');
                    }
                    e.preventDefault();
                break;

                case keyCode.UP:
                    var itemToFocus = null,
                    prevItem = $this.focusedItem.prev();
                    
                    if(prevItem.length) {
                        itemToFocus = prevItem.find('li.ui-menuitem:visible:last');
                        if(!itemToFocus.length) {
                            itemToFocus = prevItem;    
                        }
                    }
                    else {
                        itemToFocus = $this.focusedItem.closest('ul').parent('li');
                    }

                    if(itemToFocus.length) {
                        $this.focusItem(itemToFocus);
                    }
                        
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    var itemToFocus = null,
                    firstVisibleChildItem = $this.focusedItem.find('> ul > li:visible:first');
                    
                    if(firstVisibleChildItem.length) {
                        itemToFocus = firstVisibleChildItem;
                    }
                    else if($this.focusedItem.next().length) {
                        itemToFocus = $this.focusedItem.next();
                    }
                    else {                       
                        if($this.focusedItem.next().length === 0) {
                            itemToFocus = $this.searchDown($this.focusedItem);
                        }
                    }

                    if(itemToFocus && itemToFocus.length) {
                        $this.focusItem(itemToFocus);
                    }
                        
                    e.preventDefault();
                break;
                
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.SPACE:
                    var currentLink = $this.focusedItem.children('.ui-menuitem-link');
                    //IE fix
                    setTimeout(function(){
                        currentLink.trigger('click');
                    },1);
                    $this.jq.blur();
                    
                    var href = currentLink.attr('href');
                    if(href && href !== '#') {
                        window.location.href = href;
                    }
                    e.preventDefault();
                break;
                
                case keyCode.TAB:
                    if($this.focusedItem) {
                        if(PrimeFaces.env.isIE()) {
                            $this.focusCheck = true;
                        }
                        $(this).focus();
                    }
                break;
            }       
        }).on('blur.panelmenu', function(e) {
            if(PrimeFaces.env.isIE() && !$this.focusCheck) {
                return;
            }
            
            $this.removeFocusedItem();
        });
        
        var clickNS = 'click.' + this.id;  
        //remove focusedItem when document is clicked
        $(document.body).off(clickNS).on(clickNS, function(event) {
            if(!$(event.target).closest('.ui-panelmenu').length) {
               $this.removeFocusedItem();
            }
        });
    },
    
    searchDown: function(item) {
        var nextOfParent = item.closest('ul').parent('li').next(),
        itemToFocus = null;
        
        if(nextOfParent.length) {
            itemToFocus = nextOfParent;
        }
        else if(item.closest('ul').parent('li').length === 0){   
            itemToFocus = item;
        }
        else {            
            itemToFocus = this.searchDown(item.closest('ul').parent('li'));
        }
        
        return itemToFocus;
    },
    
    getFirstItemOfContent: function(content) {
        return content.find('> .ui-menu-list > .ui-menuitem:visible:first-child');
    },
    
    getItemText: function(item) {
        return item.find('> .ui-menuitem-link > span.ui-menuitem-text');
    },
    
    focusItem: function(item) {
        this.removeFocusedItem();
        this.getItemText(item).addClass('ui-menuitem-outline').focus();
        this.focusedItem = item;
    },
    
    removeFocusedItem: function() {
        if(this.focusedItem) {
            this.getItemText(this.focusedItem).removeClass('ui-menuitem-outline');
            this.focusedItem = null;
        }
    },
    
    isExpanded: function(item) {
        return item.children('ul.ui-menu-list').is(':visible');
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

        header.attr('aria-expanded', true).addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
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
        var submenuLink = submenu.find('> .ui-menuitem-link');
        
        submenuLink.find('> .ui-menuitem-text').attr('aria-expanded', true);
        submenuLink.find('> .ui-panelmenu-icon').addClass('ui-icon-triangle-1-s');
        submenu.children('.ui-menu-list').show();
        
        if(!restoring) {
            this.addAsExpanded(submenu);
        }
    },

    collapseTreeItem: function(submenu) {
        var submenuLink = submenu.find('> .ui-menuitem-link');
        
        submenuLink.find('> .ui-menuitem-text').attr('aria-expanded', false);
        submenuLink.find('> .ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s');
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
        this.bindKeyEvents();
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
    },
    
    bindKeyEvents: function() {
        /* For Keyboard accessibility and Screen Readers */
        this.items.attr('tabindex', 0);
        
        this.items.on('focus.tabmenu', function(e) {
            $(this).addClass('ui-menuitem-outline');
        })
        .on('blur.tabmenu', function(){
            $(this).removeClass('ui-menuitem-outline');
        })
        .on('keydown.tabmenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER || key === keyCode.NUMPAD_ENTER) {
                var currentLink = $(this).children('a');
                currentLink.trigger('click');
                var href = currentLink.attr('href');
                if(href && href !== '#') {
                    window.location.href = href;
                }
                e.preventDefault();
            }
        });
    }
});
