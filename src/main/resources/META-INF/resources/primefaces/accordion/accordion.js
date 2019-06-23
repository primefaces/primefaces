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

        this.initActive();
        this.bindEvents();

        if(this.cfg.dynamic && this.cfg.cache) {
            this.markLoadedPanels();
        }
    },

    initActive: function() {
        if(this.cfg.multiple) {
            this.cfg.active = [];

            if (this.stateHolder.val().length > 0) {
                var indexes = this.stateHolder.val().split(',');
                for(var i = 0; i < indexes.length; i++) {
                    this.cfg.active.push(parseInt(indexes[i]));
                }
            }
        }
        else {
            this.cfg.active = parseInt(this.stateHolder.val());
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
                var tabIndex = $this.headers.index(element);

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
        this.headers.on('focus.accordion', function(){
            $(this).addClass('ui-tabs-outline');
        })
        .on('blur.accordion', function(){
            $(this).removeClass('ui-tabs-outline');
        })
        .on('keydown.accordion', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                $(this).trigger('click');
                e.preventDefault();
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
            if(this.cfg.controlled) {
                this.fireTabChangeEvent(panel);
            }
            else {
                this.show(panel);

                this.fireTabChangeEvent(panel);
            }

        }

        return true;
    },

    /**
     *  Deactivates a tab with given index
     */
    unselect: function(index) {
        if(this.cfg.controlled) {
            this.fireTabCloseEvent(index);
        }
        else {
            this.hide(index);

            this.fireTabCloseEvent(index);
        }
    },

    show: function(panel) {
        var _self = this;

        //deactivate current
        if(!this.cfg.multiple) {
            var oldHeader = this.headers.filter('.ui-state-active');
            oldHeader.children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
            oldHeader.attr('aria-selected', false);
            oldHeader.attr('aria-expanded', false).removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all')
                .next().attr('aria-hidden', true).slideUp(function(){
                    if(_self.cfg.onTabClose)
                        _self.cfg.onTabClose.call(_self, panel);
                });
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

    hide: function(index) {
        var _self = this,
        panel = this.panels.eq(index),
        header = panel.prev();

        header.attr('aria-selected', false);
        header.attr('aria-expanded', false).children('.ui-icon').removeClass(this.cfg.expandedIcon).addClass(this.cfg.collapsedIcon);
        header.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all');
        panel.attr('aria-hidden', true).slideUp(function(){
            if(_self.cfg.onTabClose)
                _self.cfg.onTabClose.call(_self, panel);
        });

        this.removeFromSelection(index);
        this.saveState();
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
            this.callBehavior('tabChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    fireTabChangeEvent : function(panel) {
        if(this.hasBehavior('tabChange')) {
            var ext = {
                params: [
                    {name: this.id + '_newTab', value: panel.attr('id')},
                    {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
                ]
            };

            if(this.cfg.controlled) {
                var $this = this;
                ext.oncomplete = function(xhr, status, args, data) {
                    if(args.access && !args.validationFailed) {
                        $this.show(panel);
                    }
                };
            }

            this.callBehavior('tabChange', ext);
        }
    },

    fireTabCloseEvent : function(index) {
        if(this.hasBehavior('tabClose')) {
            var panel = this.panels.eq(index),
            ext = {
                params: [
                    {name: this.id + '_tabId', value: panel.attr('id')},
                    {name: this.id + '_tabindex', value: parseInt(index)}
                ]
            };

            if(this.cfg.controlled) {
                var $this = this;
                ext.oncomplete = function(xhr, status, args, data) {
                    if(args.access && !args.validationFailed) {
                        $this.hide(index);
                    }
                };
            }

            this.callBehavior('tabClose', ext);
        }
    },

    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },

    isLoaded: function(panel) {
        return panel.data('loaded') == true;
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
