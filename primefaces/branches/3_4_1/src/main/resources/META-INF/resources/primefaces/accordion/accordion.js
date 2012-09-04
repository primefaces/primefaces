/**
 * PrimeFaces Accordion Panel Widget
 */
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.stateHolder = $(this.jqId + '_active');
        this.headers = this.jq.children('.ui-accordion-header');
        this.panels = this.jq.children('.ui-accordion-content');
        this.headers.children('a').disableSelection();
        this.onshowHandlers = [];

        //active index
        this.cfg.active = this.cfg.multiple ? this.stateHolder.val().split(',') : this.stateHolder.val();

        this.bindEvents();

        if(this.cfg.dynamic && this.cfg.cache) {
            this.markAsLoaded(this.panels.eq(this.cfg.active));
        }

        this.jq.data('widget', this);
    },
    
    bindEvents: function() {
        var _self = this;
    
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
                    _self.unselect(tabIndex);
                }
                else {
                    _self.select(tabIndex);
                }
            }

            e.preventDefault();
        });
    },
    
    /**
     *  Activates a tab with given index
     */
    select: function(index) {
        var panel = this.panels.eq(index);

        //Call user onTabChange callback
        if(this.cfg.onTabChange) {
            var result = this.cfg.onTabChange.call(this, panel);
            if(result == false)
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
            if(this.hasBehavior('tabChange')) {
                this.fireTabChangeEvent(panel);
            }
            else {
                this.show(panel);
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

        header.attr('aria-expanded', false).children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
        header.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all');
        panel.attr('aria-hidden', true).slideUp();

        this.removeFromSelection(index);
        this.saveState();
    },
    
    show: function(panel) {
        var _self = this;

        //deactivate current
        if(!this.cfg.multiple) {
            var oldHeader = this.headers.filter('.ui-state-active');
            oldHeader.children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
            oldHeader.attr('aria-expanded', false).removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all').next().attr('aria-hidden', true).slideUp();
        }

        //activate selected
        var newHeader = panel.prev();
        newHeader.attr('aria-expanded', true).addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
                .children('.ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');

        panel.attr('aria-hidden', false).slideDown('normal', function() {
            _self.postTabShow(panel);
        });
    },
    
    loadDynamicTab: function(panel) {
        var _self = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id
        };

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    $(panel).html(content);

                    if(_self.cfg.cache) {
                        _self.markAsLoaded(panel);
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
            _self.show(panel);
        };

        options.params = [
            {name: this.id + '_contentLoad', value: true},
            {name: this.id + '_newTab', value: panel.attr('id')},
            {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
        ];

        if(this.hasBehavior('tabChange')) {
            var tabChangeBehavior = this.cfg.behaviors['tabChange'];

            tabChangeBehavior.call(this, null, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
    
    /**
     * Fires an ajax tabChangeEvent if a tabChangeListener is defined on server side
     */
    fireTabChangeEvent : function(panel) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        _self = this,
        ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(panel.index() / 2)}
            ]
        };
        
        ext.oncomplete = function() {
            _self.show(panel);
        };

        tabChangeBehavior.call(this, null, ext);
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

    addOnshowHandler : function(fn) {
        this.onshowHandlers.push(fn);
    },

    postTabShow: function(newPanel) {            
        //Call user onTabShow callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel);
        }

        //execute onshowHandlers and remove successful ones
        this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
            return !fn.call();
        });
    }
    
});