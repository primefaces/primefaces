/**
 * PrimeFaces TabView Widget
 */
PrimeFaces.widget.TabView = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.navContainer = this.jq.children('.ui-tabs-nav');
        this.panelContainer = this.jq.children('.ui-tabs-panels');
        this.stateHolder = $(this.jqId + '_activeIndex');
        this.cfg.selected = parseInt(this.stateHolder.val());
        this.onshowHandlers = [];

        this.bindEvents();

        //Cache initial active tab
        if(this.cfg.dynamic && this.cfg.cache) {
            this.markAsLoaded(this.panelContainer.children().eq(this.cfg.selected));
        }

        this.jq.data('widget', this);
    },
    
    bindEvents: function() {
        var _self = this;

        //Tab header events
        this.navContainer.children('li')
                .bind('mouseover.tabview', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-disabled')) {
                        element.addClass('ui-state-hover');
                    }
                })
                .bind('mouseout.tabview', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-disabled')) {
                        element.removeClass('ui-state-hover');
                    }
                })
                .bind('click.tabview', function(e) {
                    var element = $(this);

                    if($(e.target).is(':not(.ui-icon-close)')) {
                        var index = element.index();

                        if(!element.hasClass('ui-state-disabled') && index != _self.cfg.selected) {
                            _self.select(index);
                        }
                    }

                    e.preventDefault();
                });

        //Closable tabs
        this.navContainer.find('li .ui-icon-close')
            .bind('click.tabview', function(e) {
                _self.remove($(this).parent().index());

                e.preventDefault();
            });
    },
    
    /**
     * Selects an inactive tab given index
     */
    select: function(index) {
        //Call user onTabChange callback
        if(this.cfg.onTabChange) {
            var result = this.cfg.onTabChange.call(this, index);
            if(result == false)
                return false;
        }

        var newPanel = this.panelContainer.children().eq(index),
        shouldLoad = this.cfg.dynamic && !this.isLoaded(newPanel);

        //update state
        this.stateHolder.val(index);
        this.cfg.selected = index;

        if(shouldLoad) {
            this.loadDynamicTab(newPanel);
        }
        else {
            if(this.hasBehavior('tabChange')) {
                this.fireTabChangeEvent(newPanel);
            }
            else {
                this.show(newPanel);
            }
        }

        return true;
    },
    
    show: function(newPanel) {
        var headers = this.navContainer.children(),
        oldHeader = headers.filter('.ui-state-active'),
        newHeader = headers.eq(newPanel.index()),
        oldPanel = this.panelContainer.children('.ui-tabs-panel:visible'),
        _self = this;

        //aria
        oldPanel.attr('aria-hidden', true);
        oldHeader.attr('aria-expanded', false);
        newPanel.attr('aria-hidden', false);
        newHeader.attr('aria-expanded', true);

        if(this.cfg.effect) {
                oldPanel.hide(this.cfg.effect.name, null, this.cfg.effect.duration, function() {
                oldHeader.removeClass('ui-state-focus ui-tabs-selected ui-state-active');

                newHeader.addClass('ui-state-focus ui-tabs-selected ui-state-active');
                newPanel.show(_self.cfg.effect.name, null, _self.cfg.effect.duration, function() {
                    _self.postTabShow(newPanel);
                });
            });
        }
        else {
            oldHeader.removeClass('ui-state-focus ui-tabs-selected ui-state-active');
            oldPanel.hide();

            newHeader.addClass('ui-state-focus ui-tabs-selected ui-state-active');
            newPanel.show();

            this.postTabShow(newPanel);
        }
    },
    
    /**
     * Loads tab contents with ajax
     */
    loadDynamicTab: function(newPanel) {
        var _self = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id
        },
        tabindex = newPanel.index();

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    newPanel.html(content);

                    if(_self.cfg.cache) {
                        _self.markAsLoaded(newPanel);
                    }
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };

        options.oncomplete = function() {
            _self.show(newPanel);
        };
        
        options.params = [
            {name: this.id + '_contentLoad', value: true},
            {name: this.id + '_newTab', value: newPanel.attr('id')},
            {name: this.id + '_tabindex', value: tabindex}
        ];

        if(this.hasBehavior('tabChange')) {
            var tabChangeBehavior = this.cfg.behaviors['tabChange'];

            tabChangeBehavior.call(this, newPanel, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
    
    /**
     * Removes a tab with given index
     */
    remove: function(index) {    
        var header = this.navContainer.children().eq(index),
        panel = this.panelContainer.children().eq(index);

        this.fireTabCloseEvent(panel);

        header.remove();
        panel.remove();

        //active next tab if active tab is removed
        if(index == this.cfg.selected) {
            var newIndex = this.cfg.selected == this.getLength() ? this.cfg.selected - 1: this.cfg.selected;
            this.select(newIndex);
        }
    },
    
    getLength: function() {
        return this.navContainer.children().length;
    },
    
    getActiveIndex: function() {
        return this.cfg.selected;
    },
    
    fireTabChangeEvent: function(panel) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        _self = this,
        ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: panel.index()}
            ]
        };
        
        ext.oncomplete = function() {
            _self.show(panel);
        };

        tabChangeBehavior.call(this, panel, ext);
    },
    
    fireTabCloseEvent: function(panel) {    
        if(this.hasBehavior('tabClose')) {
            var tabCloseBehavior = this.cfg.behaviors['tabClose'],
            ext = {
                params: [
                    {name: this.id + '_closeTab', value: panel.attr('id')},
                    {name: this.id + '_tabindex', value: panel.index()}
                ]
            };

            tabCloseBehavior.call(this, null, ext);
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },
    
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },
    
    isLoaded: function(panel) {
        return panel.data('loaded') == true;
    },
    
    disable: function(index) {
        this.navContainer.children().eq(index).addClass('ui-state-disabled');
    },
    
    enable: function(index) {
        this.navContainer.children().eq(index).removeClass('ui-state-disabled');
    },
    
    addOnshowHandler: function(fn) {
        this.onshowHandlers.push(fn);
    },
    
    postTabShow: function(newPanel) {    
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel);
        }

        //execute onshowHandlers and remove successful ones
        this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
            return !fn.call();
        });
    }

});