/**
 * PrimeFaces Mobile AccordionPanel Widget
 */
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.tabs = this.jq.children('.ui-collapsible');
        this.headers = this.tabs.children('.ui-collapsible-heading');
        this.contents = this.tabs.children('.ui-collapsible-content');
        this.stateHolder = $(this.jqId + '_active');
        
        this.initActive();
        this.bindEvents();
        
        if(this.cfg.dynamic && this.cfg.cache) {
            this.markLoadedPanels();
        }
    },
    
    initActive: function() {
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
    },
    
    bindEvents: function() {
        var $this = this;
    
        this.headers.on('click.accordionPanel', function(e) {            
            var element = $(this);
            if(!element.hasClass('ui-state-disabled')) {
                var tabIndex = element.parent().index();

                if(element.hasClass('ui-collapsible-heading-collapsed'))
                    $this.select(tabIndex);
                else
                    $this.unselect(tabIndex);
            }

            e.preventDefault();
        });
    },
    
    markLoadedPanels: function() {
        if(this.cfg.multiple) {
            for(var i = 0; i < this.cfg.active.length; i++) {
                if(this.cfg.active[i] >= 0)
                    this.markAsLoaded(this.tabs.eq(this.cfg.active[i]));
            }
        } else {
            if(this.cfg.active >= 0)
                this.markAsLoaded(this.tabs.eq(this.cfg.active));
        }
    },
    
    select: function(index) {
        var tab = this.tabs.eq(index);

        if(this.cfg.onTabChange) {
            var result = this.cfg.onTabChange.call(this, tab);
            if(result === false)
                return false;
        }

        var shouldLoad = this.cfg.dynamic && !this.isLoaded(tab);

        if(this.cfg.multiple)
            this.addToSelection(index);
        else
            this.cfg.active = index;

        this.saveState();

        if(shouldLoad) {
            this.loadDynamicTab(tab);
        }
        else {
            this.show(tab);
            this.fireTabChangeEvent(tab);
        }

        return true;
    },
    
    show: function(tab) {
        var header = tab.children('.ui-collapsible-heading'),
        content = tab.children('.ui-collapsible-content');

        //deactivate current
        if(!this.cfg.multiple) {
            this.close(this.tabs.filter(':not(.ui-collapsible-collapsed)'));
        }

        tab.removeClass('ui-collapsible-collapsed').attr('aria-expanded', true);
        header.removeClass('ui-collapsible-heading-collapsed')
                .children('.ui-collapsible-heading-toggle').removeClass('ui-icon-plus').addClass('ui-icon-minus');
        content.removeClass('ui-collapsible-content-collapsed').attr('aria-hidden', false).show();
    },
    
    close: function(tab) {
        tab.addClass('ui-collapsible-collapsed').attr('aria-expanded', false);
        tab.children('.ui-collapsible-heading').addClass('ui-collapsible-heading-collapsed')
                .children('.ui-collapsible-heading-toggle').addClass('ui-collapsible-heading-collapsed').removeClass('ui-icon-minus').addClass('ui-icon-plus');
        tab.children('.ui-collapsible-content').attr('aria-hidden', true)
                .addClass('ui-collapsible-content-collapsed').attr('aria-hidden', true).hide();
    },

    unselect: function(index) {
        this.close(this.tabs.eq(index))

        this.removeFromSelection(index);
        this.saveState();
        
        if(this.hasBehavior('tabClose')) {
            this.fireTabCloseEvent(tab);
        }
    },
    
    addToSelection: function(nodeId) {
        this.cfg.active.push(nodeId);
    },

    removeFromSelection: function(index) {
        this.cfg.active = $.grep(this.cfg.active, function(r) {
            return (r !== index);
        });
    },
    
    saveState: function() {
        if(this.cfg.multiple)
            this.stateHolder.val(this.cfg.active.join(','));
        else
            this.stateHolder.val(this.cfg.active);
    },
    
    loadDynamicTab: function(tab) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: tab.attr('id')},
                {name: this.id + '_tabindex', value: tab.index()}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            tab.find('> .ui-collapsible-content > p').html(content);

                            if(this.cfg.cache) {
                                this.markAsLoaded(tab);
                            }   
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.show(tab);
            }
        };

        if(this.hasBehavior('tabChange')) {
            this.cfg.behaviors['tabChange'].call(this, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
    
    fireTabChangeEvent : function(tab) {
        if(this.hasBehavior('tabChange')) {
            var tabChangeBehavior = this.cfg.behaviors['tabChange'],
            ext = {
                params: [
                    {name: this.id + '_newTab', value: tab.attr('id')},
                    {name: this.id + '_tabindex', value: parseInt(tab.index())}
                ]
            };

            tabChangeBehavior.call(this, ext);
        }        
    },

    fireTabCloseEvent : function(tab) {
        var tabCloseBehavior = this.cfg.behaviors['tabClose'],
        ext = {
            params: [
                {name: this.id + '_tabId', value: tab.attr('id')},
                {name: this.id + '_tabindex', value: parseInt(tab.index())}
            ]
        };
        
        tabCloseBehavior.call(this, ext);
    },
    
    markAsLoaded: function(tab) {
        tab.data('loaded', true);
    },

    isLoaded: function(tab) {
        return tab.data('loaded') === true;
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    }
    
});