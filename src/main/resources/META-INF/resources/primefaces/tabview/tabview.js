/**
 * PrimeFaces TabView Widget
 */
PrimeFaces.widget.TabView = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $(this.jqId);
    this.stateHolder = $(this.jqId + '_activeIndex');
    var _self = this;
    
    this.bindEvents();
	
    //Cache initial active tab
    if(this.cfg.dynamic && this.cfg.cache) {
        this.markAsLoaded(this.jq.children('.ui-tabs-panel').eq(this.cfg.selected));
    }
}

PrimeFaces.widget.TabView.prototype.bindEvents = function() {
    var _self = this;
    
    //Tab header events
    $(this.jqId + ' .ui-tabs-nav li').die('mouseover.tabview mouseout.tabview click.tabview')
            .live('mouseover.tabview', function(e) {
                var element = $(this);
                if(!element.hasClass('ui-state-disabled')) {
                    element.addClass('ui-state-hover');
                }
            })
            .live('mouseout.tabview', function(e) {
                var element = $(this);
                if(!element.hasClass('ui-state-disabled')) {
                    element.removeClass('ui-state-hover');
                }
            })
            .live('click.tabview', function(e) {
                var element = $(this),
                index = element.index();
                
                if(!element.hasClass('ui-state-disabled') && index != _self.cfg.selected) {
                    element.addClass('ui-state-focus ui-tabs-selected ui-state-active')
                            .siblings('.ui-state-active').removeClass('ui-state-focus ui-tabs-selected ui-state-active');
                            
                    _self.select(index);
                }
                
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
    
    var panel = this.jq.children('.ui-tabs-panel').eq(index),
    shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);
    
    //update state
    this.stateHolder.val(index);
    this.cfg.selected = index;
        
    if(shouldLoad) {
        this.loadDynamicTab(panel);
    } 
    else {
        panel.removeClass('ui-tabs-hide').siblings('.ui-tabs-panel:not(ui-tabs-hide)').addClass('ui-tabs-hide');
        
        //Call user onTabShow callback
        if(this.cfg.onTabShow) {
            this.cfg.onTabShow.call(this, index);
        }
        
        //invoke ajax callback if defined
        this.fireTabChangeEvent(panel);
    }
    
    return true;
}

/**
 * Loads tab contents with ajax
 */
PrimeFaces.widget.TabView.prototype.loadDynamicTab = function(panel) {
    var _self = this,
    options = {
        source: this.id,
        process: this.id,
        update: this.id
    },
    tabindex = panel.index() - 1;

    options.onsuccess = function(responseXML) {
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update");

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == _self.id){
                panel.html(content);

                if(_self.cfg.cache) {
                    _self.markAsLoaded(panel);
                }
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }
        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };
    
    options.oncomplete = function() {
        panel.removeClass('ui-tabs-hide').siblings('.ui-tabs-panel:not(ui-tabs-hide)').addClass('ui-tabs-hide');
        
        //Call user onTabShow callback
        if(_self.cfg.onTabShow) {
            _self.cfg.onTabShow.call(_self, tabindex);
        }
    };


    var params = {};
    params[this.id + '_contentLoad'] = true;
    params[this.id + '_newTab'] = panel.attr('id');
    params[this.id + '_tabindex'] = tabindex;

    options.params = params;

    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'];
        
        tabChangeBehavior.call(this, panel, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

PrimeFaces.widget.TabView.prototype.markAsLoaded = function(panel) {
    panel.data('loaded', true);
}

PrimeFaces.widget.TabView.prototype.isLoaded = function(panel) {
    return panel.data('loaded') == true;
}

PrimeFaces.widget.TabView.prototype.disable = function(index) {
    this.jq.tabs('disable', index);
}

PrimeFaces.widget.TabView.prototype.enable = function(index) {
    this.jq.tabs('enable', index);
}

PrimeFaces.widget.TabView.prototype.add = function(url, label, index) {
    this.jq.tabs('add', url, label, index);
}

PrimeFaces.widget.TabView.prototype.remove = function(index) {    
    this.fireTabCloseEvent(null, this.jq.children('.ui-tabs-panel').get(index));
    
    this.jq.tabs('remove', index);
}

PrimeFaces.widget.TabView.prototype.getLength = function() {
    return this.jq.tabs('length');
}

PrimeFaces.widget.TabView.prototype.getActiveIndex = function() {
    return this.jq.tabs('option', 'selected');
}

PrimeFaces.widget.TabView.prototype.fireTabChangeEvent = function(panel) {
    var _self = this;
    
    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_newTab'] = panel.attr('id');
        ext.params[this.id + '_tabindex'] = $(_self.jqId).children('.ui-tabs-panel').index(panel) - 1;

        tabChangeBehavior.call(this, panel, ext);
    }
}

PrimeFaces.widget.TabView.prototype.fireTabCloseEvent = function(panel) {
    var _self = this;
    
    if(this.hasBehavior('tabClose')) {
        var tabCloseBehavior = this.cfg.behaviors['tabClose'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_closeTab'] = panel.id;
        ext.params[this.id + '_tabindex'] = $(_self.jqId).children('.ui-tabs-panel').index(panel) - 1;

        tabCloseBehavior.call(this, panel, ext);
    }
}

PrimeFaces.widget.TabView.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

PrimeFaces.widget.TabView.prototype.bindCloseEvents = function() {
    var _self = this;
    $(this.jqId + ' .ui-tabs-nav li .ui-icon-close').click(function(e) {
        var element = $(this),
        tabindex = element.parent().index();
        
        _self.remove(tabindex);
    });
}