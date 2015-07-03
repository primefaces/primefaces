/**
 * PrimeFaces Mobile TabView Widget
 */
PrimeFaces.widget.TabView = PrimeFaces.widget.BaseWidget.extend({
    
    GRID_MAP: {
        '2': 'a',
        '3': 'b',
        '4': 'c',
        '5': 'd'
    },
    
    BLOCK_MAP: {
        '0': 'a',
        '1': 'b',
        '2': 'c',
        '3': 'd',
        '4': 'e'
    },
    
    init: function(cfg) {
        this._super(cfg);
        this.navbar = this.jq.children('.ui-navbar');
        this.navContainer = this.navbar.children('.ui-tabs-nav');
        this.headers = this.navContainer.children('.ui-tabs-header');
        this.panelContainer = this.jq.children('.ui-tabs-panels');
        this.stateHolder = $(this.jqId + '_activeIndex');
        this.cfg.selected = parseInt(this.stateHolder.val());
        this.onshowHandlers = this.onshowHandlers||{};
        this.initGrid();
        
        this.bindEvents();
        
        if(this.cfg.dynamic && this.cfg.cache) {
            this.markAsLoaded(this.panelContainer.children().eq(this.cfg.selected));
        }
        
    },
    
    initGrid: function() {
        var tabcount = this.headers.length;
        
        this.navContainer.addClass('ui-grid-' + this.GRID_MAP[tabcount.toString()]);
        
        for(var i = 0; i < tabcount; i++) {
            this.headers.eq(i).addClass('ui-block-' + this.BLOCK_MAP[(i % 5).toString()]);
        }
    },
    
    bindEvents: function() {
        var $this = this;

        //Tab header events
        this.headers.children('a')
                .on('click.tabView', function(e) {
                    var element = $(this),
                    index = element.parent().index();

                    if(!element.hasClass('ui-state-disabled') && (index !== $this.cfg.selected)) {
                        $this.select(index);
                    }

                    e.preventDefault();
                });

    },
    
    select: function(index, silent) {
        if(this.cfg.onTabChange && !silent) {
            var result = this.cfg.onTabChange.call(this, index);
            if(result === false)
                return false;
        }

        var newPanel = this.panelContainer.children().eq(index),
        shouldLoad = this.cfg.dynamic && !this.isLoaded(newPanel);

        this.stateHolder.val(index);
        this.cfg.selected = index;

        if(shouldLoad) {
            this.loadDynamicTab(newPanel);
        }
        else {
            this.show(newPanel);
            
            if(this.hasBehavior('tabChange') && !silent) {
                this.fireTabChangeEvent(newPanel);
            }
        }

        return true;
    },
    
    show: function(newPanel) {
        var oldHeader = this.headers.filter('.ui-tabs-active'),
        newHeader = this.headers.eq(newPanel.index()),
        oldPanel = this.panelContainer.children(':visible');

        oldPanel.attr('aria-hidden', true);
        oldHeader.attr('aria-expanded', false);
        newPanel.attr('aria-hidden', false);
        newHeader.attr('aria-expanded', true);

        oldHeader.removeClass('ui-tabs-active').children('a').removeClass('ui-btn-active');
        oldPanel.hide();

        newHeader.addClass('ui-tabs-active').children('a').addClass('ui-btn-active');
        newPanel.show();

        this.postTabShow(newPanel);
    },
    
    fireTabChangeEvent: function(panel) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: [
                {name: this.id + '_newTab', value: panel.attr('id')},
                {name: this.id + '_tabindex', value: panel.index()}
            ]
        };
        
        tabChangeBehavior.call(this, ext);
    },
    
    loadDynamicTab: function(newPanel) {
        var $this = this,
        tabindex = newPanel.index(),
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true},
                {name: this.id + '_newTab', value: newPanel.attr('id')},
                {name: this.id + '_tabindex', value: tabindex}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            newPanel.html(content);

                            if(this.cfg.cache) {
                                this.markAsLoaded(newPanel);
                            }
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.show(newPanel);
            }
        };

        if(this.hasBehavior('tabChange')) {
            this.cfg.behaviors['tabChange'].call(this, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    postTabShow: function(newPanel) {    
        //execute user defined callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, newPanel.index());
        }

        //execute onshowHandlers and remove successful ones
        for(var id in this.onshowHandlers) {
            if(this.onshowHandlers.hasOwnProperty(id)) {
                var fn = this.onshowHandlers[id];
                
                if(fn.call()) {
                    delete this.onshowHandlers[id];
                }
            }
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    },
    
    markAsLoaded: function(panel) {
        panel.data('loaded', true);
    },
    
    isLoaded: function(panel) {
        return panel.data('loaded') === true;
    }
    
});