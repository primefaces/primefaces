/*
 * PrimeFaces SplitButton Widget
 */
PrimeFaces.widget.SplitButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.button = $(this.jqId + '_button');
        this.menuButton = $(this.jqId + '_menuButton');
        this.menuId = this.jqId + "_menu";
        this.menu = $(this.menuId);
        this.menuitemContainer = this.menu.find('.ui-menu-list');
        this.menuitems = this.menuitemContainer.children('.ui-menuitem:not(.ui-state-disabled)');
        this.cfg.disabled = this.button.is(':disabled');

        if(!this.cfg.disabled) {
            this.bindEvents();

            PrimeFaces.utils.registerDynamicOverlay(this, this.menu, this.id + '_menu');
        }

        //pfs metadata
        this.button.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.menuButton.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    //@override
    refresh: function(cfg) {
        this._super(cfg);
    },

    bindEvents: function() {
        var $this = this;

        PrimeFaces.skinButton(this.button).skinButton(this.menuButton);

        //mark button and descandants of button as a trigger for a primefaces overlay
        this.button.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

        //toggle menu
        this.menuButton.click(function() {
            if($this.menu.is(':hidden'))
                $this.show();
            else
                $this.hide();
        });

        //menuitem visuals
        this.menuitems.mouseover(function(e) {
            var menuitem = $(this),
            menuitemLink = menuitem.children('.ui-menuitem-link');

            if(!menuitemLink.hasClass('ui-state-disabled')) {
                menuitem.addClass('ui-state-hover');
            }
        }).mouseout(function(e) {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            $this.hide();
        });

        //keyboard support
        this.menuButton.keydown(function(e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                    $this.highlightPrev(e);
                break;

                case keyCode.DOWN:
                    $this.highlightNext(e);
                break;

                case keyCode.ENTER:
                case keyCode.SPACE:
                    $this.handleEnterKey(e);
                break;


                case keyCode.ESCAPE:
                case keyCode.TAB:
                    $this.handleEscapeKey();
                break;
            }
        });

        PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.menu, null,
            function(e, eventTarget) {
                if(!($this.menu.is(eventTarget) || $this.menu.has(eventTarget).length > 0)) {
                    $this.button.removeClass('ui-state-focus ui-state-hover');
                    $this.hide();
                }
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.menu, function() {
            $this.alignPanel();
        });
        
        if(this.cfg.filter) {
            this.setupFilterMatcher();
            this.filterInput = this.menu.find('> div.ui-splitbuttonmenu-filter-container > input.ui-splitbuttonmenu-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.bindFilterEvents();
        }
    },
    
    bindFilterEvents: function() {
        var $this = this;

        this.filterInput.on('keyup.ui-splitbutton', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                case keyCode.ENTER:
                case keyCode.TAB:
                case keyCode.ESCAPE:
                case keyCode.SPACE:
                case keyCode.HOME:
                case keyCode.PAGE_DOWN:
                case keyCode.PAGE_UP:
                case keyCode.END:
                case 16: //shift
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case 91: //left window or cmd:
                case 92: //right window:
                case 93: //right cmd:
                case 20: //capslock:
                break;

                default:
                    //function keys (F1,F2 etc.)
                    if(key >= 112 && key <= 123) {
                        break;
                    }

                    var metaKey = e.metaKey||e.ctrlKey;

                    if(!metaKey) {
                        $this.filter($(this).val());
                    }
                break;
            }
        })
        .on('keydown.ui-splitbutton',function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                    $this.highlightPrev(e);
                break;

                case keyCode.DOWN:
                    $this.highlightNext(e);
                break;
                
                case keyCode.ENTER:
                    $this.handleEnterKey(e);
                break;
                
                case keyCode.SPACE:
                    var target = $(e.target);

                    if(target.is('input') && target.hasClass('ui-splitbuttonmenu-filter')) {
                        return;
                    }
                    
                    $this.handleEnterKey(e);
                break;
                
                case keyCode.ESCAPE:
                case keyCode.TAB:
                    $this.handleEscapeKey();
                break;

                default:
                break;
            }
        }).on('paste.ui-splitbutton', function() {
            setTimeout(function(){
                $this.filter($this.filterInput.val());
            },2);
	});
    },
    
    highlightNext: function(event) {
        var highlightedItem = this.menuitems.filter('.ui-state-hover'),
        nextItems = highlightedItem.length ? highlightedItem.nextAll(':not(.ui-separator, .ui-widget-header):visible') : this.menuitems.filter(':visible').eq(0);
        
        if(nextItems.length) {
            highlightedItem.removeClass('ui-state-hover');
            nextItems.eq(0).addClass('ui-state-hover');
        }

        event.preventDefault();
    },
    
    highlightPrev: function(event) {
        var highlightedItem = this.menuitems.filter('.ui-state-hover'),
        prevItems = highlightedItem.length ? highlightedItem.prevAll(':not(.ui-separator, .ui-widget-header):visible') : null;

        if(prevItems && prevItems.length) {
            highlightedItem.removeClass('ui-state-hover');
            prevItems.eq(0).addClass('ui-state-hover');
        }

        event.preventDefault();
    },
    
    handleEnterKey: function(event) {
        if(this.menu.is(':visible')) {
            var link = this.menuitems.filter('.ui-state-hover').children('a');
            link.trigger('click');
            
            var href = link.attr('href');
            if(href && href !== '#') {
                window.location.href = href;
            } 
        }
        else {
            this.show();
        }

        event.preventDefault();
    },
    
    handleEscapeKey: function() {
        this.hide();
    },
    
    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    filter: function(value) {
        var filterValue = $.trim(value).toLowerCase();

        if(filterValue === '') {
            this.menuitems.filter(':hidden').show();
            this.menuitemContainer.children('.ui-widget-header').show();
            this.menuitemContainer.children('.ui-separator').show();
        }
        else {
            for(var i = 0; i < this.menuitems.length; i++) {
                var menuitem = this.menuitems.eq(i),
                itemLabel = menuitem.find('.ui-menuitem-text').text().toLowerCase();

                /* for keyboard support */
                menuitem.removeClass('ui-state-hover');
                
                if(this.filterMatcher(itemLabel, filterValue))
                    menuitem.show();
                else
                    menuitem.hide();

            }

            //groups
            var groupHeaders = this.menuitemContainer.children('.ui-widget-header');
            for(var g = 0; g < groupHeaders.length; g++) {
                var group = groupHeaders.eq(g);

                if(g === (groupHeaders.length - 1)) {
                    if(group.nextAll('.ui-submenu-child').filter(':visible').length === 0)
                        group.hide();
                    else
                        group.show();
                }
                else {
                    if(group.nextUntil('.ui-widget-header').filter(':visible').length === 0)
                        group.hide();
                    else
                        group.show();
                }
            }
            
            var separators = this.menuitemContainer.children('.ui-separator');
            for(var s = 0; s < separators.length; s++) {
                var separator = separators.eq(s);
                if(separator.nextAll().filter(':visible').length === 0 || separator.prevAll().filter(':visible').length === 0) {
                    separator.hide();
                }
                else {
                    separator.show();
                }
            }
        }
        
        this.alignPanel();
    },

    show: function() {
        this.alignPanel();

        this.menu.show();
        
        if(this.cfg.filter) {
            this.filterInput.focus();
        }
        else {
            this.menuButton.focus();
        }
    },

    hide: function() {
        this.menuitems.filter('.ui-state-hover').removeClass('ui-state-hover');
        this.menuButton.removeClass('ui-state-focus');

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
