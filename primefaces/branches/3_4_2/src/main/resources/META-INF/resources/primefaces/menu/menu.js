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
            if(_self.jq.is(':visible')) {
                _self.hide();
            }
            else {
                _self.show();
                e.preventDefault();
            }
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
                _self.align();
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
            var win = $(window),
            offset = menuitem.offset(),
            menuitemTop = offset.top,
            submenuHeight = submenu.outerHeight(),
            menuitemHeight = menuitem.outerHeight(),
            top = (menuitemTop + submenuHeight) > (win.height() + win.scrollTop()) ? (-1 * submenuHeight) + menuitemHeight : 0;  //viewport check

            submenu.css({
                'left': menuitem.outerWidth(),
                'top': top,
                'z-index': ++PrimeFaces.zindex
            }).show();
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

        if(this.cfg.overlay) {
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

        //Realign overlay on window resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if(_self.menu.is(':visible')) {
                _self.alignPanel();
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

/**
 * PrimeFaces ContextMenu Widget
 */
PrimeFaces.widget.ContextMenu = PrimeFaces.widget.TieredMenu.extend({
    
    init: function(cfg) {
        cfg.autoDisplay = true;
        this._super(cfg);
        
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
        var rowSelector = this.jqTargetId + ' tbody.ui-datatable-data > tr.ui-widget-content:not(.ui-datatable-empty-message)',
        event = this.cfg.event + '.datatable',
        _self = this;
        
        $(document).off(event, rowSelector)
                    .on(event, rowSelector, null, function(e) {
                        var widget = window[_self.cfg.targetWidgetVar];
                        widget.onRowClick(e, this, true);
                        
                        if(widget.hasBehavior('contextMenu')) {
                            var rowMeta = widget.getRowMeta($(this));
        
                            widget.fireRowSelectEvent(rowMeta.key, 'contextMenu');
                        }
                        
                        _self.show(e);

                        e.preventDefault();
                    });
    },
    
    bindTreeTable: function() {
        var rowSelector = this.jqTargetId + ' .ui-treetable-data > ' + (this.cfg.nodeType ? 'tr.ui-treetable-selectable-node.' + this.cfg.nodeType : 'tr.ui-treetable-selectable-node'),
        event = this.cfg.event + '.treetable',
        _self = this;
        
        $(document).off(event, rowSelector)
                    .on(event, rowSelector, null, function(e) {
                        window[_self.cfg.targetWidgetVar].onRowClick(e, $(this));
                        _self.show(e);
                        e.preventDefault();
                    });
    },
    
    bindTree: function() {
        var nodeContentSelector = this.jqTargetId + ' .ui-tree-selectable',
        event = this.cfg.nodeType ? this.cfg.event + '.tree.' + this.cfg.nodeType : this.cfg.event + '.tree',
        _self = this;
                
        $(document).off(event, nodeContentSelector)
                    .on(event, nodeContentSelector, null, function(e) {
                        var nodeContent = $(this);
                        
                        if(_self.cfg.nodeType === undefined || nodeContent.parent().data('nodetype') === _self.cfg.nodeType) {
                            window[_self.cfg.targetWidgetVar].nodeClick(e, nodeContent);
                            _self.show(e);
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
    }

});

/**
 * PrimeFaces MegaMenu Widget
 */
PrimeFaces.widget.MegaMenu = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.rootList = this.jq.children('ul.ui-menu-list');
        this.rootLinks = this.rootList.find('> li.ui-menuitem > a.ui-menuitem-link:not(.ui-state-disabled)');                  
        this.subLinks = this.jq.find('.ui-menu-child a.ui-menuitem-link:not(.ui-state-disabled)');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var _self = this;
  
        this.rootLinks.mouseenter(function(e) {
            var link = $(this),
            menuitem = link.parent();
            
            var current = menuitem.siblings('.ui-menuitem-active');
            if(current.length > 0) {
                _self.deactivate(current, false);
            }
            
            if(_self.cfg.autoDisplay||_self.active) {
                _self.activate(menuitem);
            }
            else {
                _self.highlight(menuitem);
            }
            
        });
        
        if(this.cfg.autoDisplay == false) {
            this.rootLinks.data('primefaces-megamenu', this.id).find('*').data('primefaces-megamenu', this.id)
            
            this.rootLinks.click(function(e) {
                var link = $(this),
                menuitem = link.parent(),
                submenu = link.next();

                if(submenu.length == 1) {
                    if(submenu.is(':visible')) {
                        _self.active = false;
                        _self.deactivate(menuitem, true);
                    }
                    else {                                        
                        _self.active = true;
                        _self.activate(menuitem);
                    }
                }
            });
        }

        this.subLinks.mouseenter(function() {
            $(this).addClass('ui-state-hover');
        })
        .mouseleave(function() {
            $(this).removeClass('ui-state-hover');
        });
        
        this.rootList.mouseleave(function(e) {
            var activeitem = _self.rootList.children('.ui-menuitem-active');
            if(activeitem.length == 1) {
                _self.deactivate(activeitem, false);
            }
        });
        
        this.rootList.find('> li.ui-menuitem > ul.ui-menu-child').mouseleave(function(e) {            
            e.stopPropagation();
        });
        
        $(document.body).click(function(e) {
            var target = $(e.target);
            if(target.data('primefaces-megamenu') == _self.id) {
                return;
            }
            
            _self.active = false;
            _self.deactivate(_self.rootList.children('li.ui-menuitem-active'), true);
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
        _self = this;
        
        _self.highlight(menuitem);
        
        if(submenu.length > 0) {
            _self.showSubmenu(menuitem, submenu);
        }
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
        this.stateKey = 'panelMenu-' + this.id;
        
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

            if(header.hasClass('ui-state-active')) {
                _self.collapseRootSubmenu($(this));
            }
            else {
                _self.expandRootSubmenu($(this), false);
            }

            e.preventDefault();
        });

        this.menuitemLinks.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        });

        this.treeLinks.click(function(e) {
            var link = $(this),
            submenu = link.next();

            if(submenu.is(':visible')) {
                _self.collapseTreeItem(link, submenu);
            }
            else {
                _self.expandTreeItem(link, submenu, false);
            }

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

    expandTreeItem: function(link, submenu, restoring) {
        link.children('.ui-panelmenu-icon').addClass('ui-icon-triangle-1-s');
        submenu.show();
        
        if(!restoring) {
            this.addAsExpanded(link);
        }
    },

    collapseTreeItem: function(link, submenu) {
        link.children('.ui-panelmenu-icon').removeClass('ui-icon-triangle-1-s');
        submenu.hide();
        
        this.removeAsExpanded(link);
    },
    
    saveState: function() {
        var expandedNodeIds = this.expandedNodes.join(',');
        
        PrimeFaces.setCookie(this.stateKey, expandedNodeIds);
    },
    
    restoreState: function() {
        var expandedNodeIds = PrimeFaces.getCookie(this.stateKey);
        
        if(expandedNodeIds) {
            this.expandedNodes = expandedNodeIds.split(',');
            for(var i = 0 ; i < this.expandedNodes.length; i++) {
                var element = $(PrimeFaces.escapeClientId(this.expandedNodes[i]));
                if(element.is('div.ui-panelmenu-content')) {
                    this.expandRootSubmenu(element.prev(), true);
                }
                else if(element.is('a.ui-menuitem-link')) {
                    this.expandTreeItem(element, element.next(), true);
                }
            }
        }
        else {
            this.expandedNodes = [];
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
        PrimeFaces.setCookie(this.stateKey, null);
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