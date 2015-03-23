/**
 * PrimeFaces Accordion Panel Widget
 */
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.stateHolder = $(this.jqId + '_active');
        this.headers = this.jq.children('.ui-accordion-header');
        this.panels = this.jq.children('.ui-accordion-content');
        this.cfg.rtl = this.jq.hasClass('ui-accordion-rtl');
        this.cfg.expandedIcon = 'ui-icon-triangle-1-s';
        this.cfg.collapsedIcon = this.cfg.rtl ? 'ui-icon-triangle-1-w' : 'ui-icon-triangle-1-e';
        this.focusedHeader = null;
        
        this.initActive();
        this.bindEvents();

        if(this.cfg.dynamic && this.cfg.cache) {
            this.markLoadedPanels();
        }
    },
            
    initActive: function() {
        var firstActiveIndex = 0;
        
        if(this.cfg.multiple) {
            var indexes = this.stateHolder.val().split(',');
            for(var i = 0; i < indexes.length; i++) {
                indexes[i] = parseInt(indexes[i]);
            }
            
            this.cfg.active = indexes;
        }
        else {
            this.cfg.active = parseInt(this.stateHolder.val());
        }
        
        if(this.cfg.multiple) {
            firstActiveIndex = this.cfg.active[0];
        }
        else {
            firstActiveIndex = this.cfg.active;
        }
        
        for(var i = 0; i < this.headers.length; i++) {
            if(firstActiveIndex === i && !this.headers.eq(i).hasClass('ui-state-disabled')) {
                this.headers.eq(i).attr('tabindex', '0');
            }
            else {
                this.headers.eq(i).attr('tabindex', '-1');
            }
        }

        if(this.headers.filter('[tabindex="-1"]').length === this.headers.length) {
            this.headers.filter('h3:not(.ui-state-disabled):first').attr('tabindex', '0');
        }
    },
        
    bindEvents: function() {
        var $this = this;
    
        this.headers.mouseover(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')&&!element.hasClass('ui-state-disabled')) {
                element.addClass('ui-state-hover');
            }
        }).mouseout(function() {
            var element = $(this);
            if(!element.hasClass('ui-state-active')&&!element.hasClass('ui-state-disabled')) {
                element.removeClass('ui-state-hover');
            }
        }).click(function(e) {            
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                var tabIndex = element.index() / 2;

                if(element.hasClass('ui-state-active')) {
                    $this.unselect(tabIndex);
                }
                else {
                    $this.select(tabIndex);
                    $(this).trigger('focus.accordion');
                }
            }

            e.preventDefault();
        });
        
        this.bindKeyEvents();
    },
    
    bindKeyEvents: function() {
        var $this = this;
        
        this.headers.on('focus.accordion', function(){
            $this.focusedHeader = $(this);
            if(!$this.focusedHeader.hasClass('ui-state-disabled')) {
                $this.headers.filter('[tabindex="0"]').attr('tabindex', '-1').removeClass('ui-tabs-outline');
                $this.focusedHeader.attr('tabindex', '0').addClass('ui-tabs-outline');
            }
        })
        .on('blur.accordion', function(){
            if($this.focusedHeader) {
                $this.focusedHeader.removeClass('ui-tabs-outline');                
            }
        })
        .on('keydown.accordion', function(e) {
            var keyCode = $.ui.keyCode;
 
            switch(e.which) {
                case keyCode.LEFT:
                case keyCode.UP: 
                    if($this.focusedHeader) {
                        if(($this.focusedHeader.index()/2) === 0) {
                            $this.focusedHeader = $this.headers.filter('h3:not(.ui-state-disabled):last');
                        }
                        else {
                            $this.focusedHeader = $this.focusedHeader.prevAll('h3:not(.ui-state-disabled):first');
                            if(!$this.focusedHeader.length) {
                                $this.focusedHeader = $this.headers.filter('h3:not(.ui-state-disabled):last');
                            }
                        }
                        $this.focusedHeader.trigger('focus.accordion');
                    }
                    e.preventDefault();
                break;

                case keyCode.RIGHT:
                case keyCode.DOWN:
                    if($this.focusedHeader) {
                        if(($this.focusedHeader.index()/2) === ($this.headers.length - 1)) {
                            $this.focusedHeader = $this.headers.filter('h3:not(.ui-state-disabled):first');
                        }
                        else {
                            $this.focusedHeader = $this.focusedHeader.nextAll('h3:not(.ui-state-disabled):first');
                            if(!$this.focusedHeader.length) {
                                $this.focusedHeader = $this.headers.filter('h3:not(.ui-state-disabled):first');
                            }
                        } 
                        $this.focusedHeader.trigger('focus.accordion');
                    }
                    e.preventDefault();
                break;
                
                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                case keyCode.SPACE:
                    if($this.focusedHeader) {
                        $this.focusedHeader.trigger('click');
                    }
                    e.preventDefault();
                break;
            }       
        });
    },
            
    markLoadedPanels: function() {
        if(this.cfg.multiple) {
            for(var i = 0; i < this.cfg.active.length; i++) {
                if(this.cfg.active[i] >= 0)
                    this.markAsLoaded(this.panels.eq(this.cfg.active[i]));
            }
        } else {
            if(this.cfg.active >= 0)
                this.markAsLoaded(this.panels.eq(this.cfg.active));
        }
    },
    
    /**
     *  Activates a tab with given index
     */
    select: function(index) {
        var panel = this.panels.eq(index);

        //Call user onTabChange callback
        if(this.cfg.onTabChange) {
            var result = this.cfg.onTabChange.call(this, panel);
            if(result === false)
                return false;
        }

        var shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

        //update state
        if(this.cfg.multiple)
            this.addToSelection(index);
        else
            this.cfg.active = index;

        this.saveState();

        if(shouldLoad) {
            this.loadDynamicTab(panel);
        }
        else {
            this.show(panel);
            
            if(this.hasBehavior('tabChange')) {
                this.fireTabChangeEvent(panel);
            }
        }

        return true;
    },
    
    /**
     *  Deactivates a tab with given index
     */
    unselect: function(index) {
        var panel = this.panels.eq(index),
        header = panel.prev();

        header.attr('aria-selected', false);
        header.attr('aria-expanded', false).children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
        header.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all');
        panel.attr('aria-hidden', true).slideUp();

        this.removeFromSelection(index);
        this.saveState();
        
        if(this.hasBehavior('tabClose')) {
            this.fireTabCloseEvent(panel);
        }
    },
    
    show: function(panel) {
        var _self = this;

        //deactivate current
        if(!this.cfg.multiple) {
            var oldHeader = this.headers.filter('.ui-state-active');
            oldHeader.children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
            oldHeader.attr('aria-selected', false);
            oldHeader.attr('aria-expanded', false).removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all').next().attr('aria-hidden', true).slideUp();
        }

        //activate selected
        var newHeader = panel.prev();
        newHeader.attr('aria-selected', true);
        newHeader.attr('aria-expanded', true).addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
                .children('.ui-icon').removeClass(this.cfg.collapsedIcon).addClass(this.cfg.expandedIcon);

        panel.attr('aria-hidden', false).slideDown('normal', function() {
            _self.postTabShow(panel);
        });
    },
    
    loadDynamicTab: function(panel) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            panel.html(content);

                            if(this.cfg.cache) {
                                this.markAsLoaded(panel);
                            }   
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.show(panel);
            }
        };

        if(this.hasBehavior('tabChange')) {
            var tabChangeBehavior = this.cfg.behaviors['tabChange'];

            tabChangeBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
    
    fireTabChangeEvent : function(panel) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
            ]
        };
        
        tabChangeBehavior.call(this, ext);
    },

    fireTabCloseEvent : function(panel) {
        var tabCloseBehavior = this.cfg.behaviors['tabClose'],
        ext = {
            params: [
                {name: this.id + '_tabId', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
            ]
        };
        
        tabCloseBehavior.call(this, ext);
    },
    
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },

    isLoaded: function(panel) {
        return panel.data('loaded') == true;
    },

    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },

    addToSelection: function(nodeId) {
        this.cfg.active.push(nodeId);
    },

    removeFromSelection: function(nodeId) {
        this.cfg.active = $.grep(this.cfg.active, function(r) {
            return r != nodeId;
        });
    },
    
    saveState: function() {
        if(this.cfg.multiple)
            this.stateHolder.val(this.cfg.active.join(','));
        else
            this.stateHolder.val(this.cfg.active);
    },

    postTabShow: function(newPanel) {            
        //Call user onTabShow callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel);
        }

        PrimeFaces.invokeDeferredRenders(this.id);
    }
    
});