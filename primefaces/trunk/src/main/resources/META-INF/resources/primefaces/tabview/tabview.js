/**
 * PrimeFaces TabView Widget
 */
PrimeFaces.widget.TabView = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
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
    
    this.panelContainer.children('.ui-tabs-panel').data('widget', this);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.TabView, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.TabView.prototype.bindEvents = function() {
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
}

/**
 * Selects an inactive tab given index
 */
PrimeFaces.widget.TabView.prototype.select = function(index) {
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
        this.show(newPanel);

        this.fireTabChangeEvent(newPanel);
    }
    
    return true;
}

PrimeFaces.widget.TabView.prototype.show = function(newPanel) {
    var headers = this.navContainer.children(),
    oldHeader = headers.filter('.ui-state-active'),
    newHeader = headers.eq(newPanel.index()),
    oldPanel = this.panelContainer.children('.ui-tabs-panel:visible'),
    _self = this;
    
    if(this.cfg.effect) {
        oldPanel.hide(this.cfg.effect.name, null, this.cfg.effect.duration, function() {
            oldHeader.removeClass('ui-state-focus ui-tabs-selected ui-state-active');
            $(this).hide();
            
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
}

/**
 * Loads tab contents with ajax
 */
PrimeFaces.widget.TabView.prototype.loadDynamicTab = function(newPanel) {
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


    var params = {};
    params[this.id + '_contentLoad'] = true;
    params[this.id + '_newTab'] = newPanel.attr('id');
    params[this.id + '_tabindex'] = tabindex;

    options.params = params;

    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'];
        
        tabChangeBehavior.call(this, newPanel, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

/**
 * Removes a tab with given index
 */
PrimeFaces.widget.TabView.prototype.remove = function(index) {    
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
}

PrimeFaces.widget.TabView.prototype.getLength = function() {
    return this.navContainer.children().length;
}

PrimeFaces.widget.TabView.prototype.getActiveIndex = function() {
    return this.cfg.selected;
}

PrimeFaces.widget.TabView.prototype.fireTabChangeEvent = function(panel) {
    var _self = this;
    
    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_newTab'] = panel.attr('id');
        ext.params[this.id + '_tabindex'] = panel.index();

        tabChangeBehavior.call(this, panel, ext);
    }
}

PrimeFaces.widget.TabView.prototype.fireTabCloseEvent = function(panel) {    
    if(this.hasBehavior('tabClose')) {
        var tabCloseBehavior = this.cfg.behaviors['tabClose'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_closeTab'] = panel.attr('id')
        ext.params[this.id + '_tabindex'] = panel.index();

        tabCloseBehavior.call(this, null, ext);
    }
}

PrimeFaces.widget.TabView.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

PrimeFaces.widget.TabView.prototype.markAsLoaded = function(panel) {
    panel.data('loaded', true);
}

PrimeFaces.widget.TabView.prototype.isLoaded = function(panel) {
    return panel.data('loaded') == true;
}

PrimeFaces.widget.TabView.prototype.disable = function(index) {
    this.navContainer.children().eq(index).addClass('ui-state-disabled');
}

PrimeFaces.widget.TabView.prototype.enable = function(index) {
    this.navContainer.children().eq(index).removeClass('ui-state-disabled');
}

PrimeFaces.widget.TabView.prototype.addOnshowHandler = function(fn) {
    this.onshowHandlers.push(fn);
}

PrimeFaces.widget.TabView.prototype.postTabShow = function(newPanel) {    
    //execute user defined callback
    if(this.cfg.onTabShow) {
        this.cfg.onTabShow.call(this, newPanel);
    }
    
    //execute onshowHandlers and remove successful ones
    this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
		return !fn.call();
	});
}