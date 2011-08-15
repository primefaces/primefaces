/**
 * PrimeFaces Accordion Widget
 */
PrimeFaces.widget.AccordionPanel = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $(this.jqId);
    this.stateHolder = $(this.jqId + '_active');
    this.headers = this.jq.children('.ui-accordion-header');
    this.panels = this.jq.children('.ui-accordion-content');
    
    //options
    this.cfg.active = parseInt(this.stateHolder.val());
    
    this.bindEvents();
    
    if(this.cfg.dynamic && this.cfg.cache) {
        this.markAsLoaded(this.panels.eq(this.cfg.active));
    }
}

PrimeFaces.widget.AccordionPanel.prototype.bindEvents = function() {
    var _self = this;
    
    this.headers.mouseover(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active') && !element.hasClass('ui-state-disabled')) {
            element.addClass('ui-state-hover');
        }
    }).mouseout(function() {
        var element = $(this);
        if(!element.hasClass('ui-state-active') && !element.hasClass('ui-state-disabled')) {
            element.removeClass('ui-state-hover');
        }
    }).click(function(e) {
        var element = $(this);
        if(!element.hasClass('ui-state-active') && !element.hasClass('ui-state-disabled')) {
            var tabIndex = element.index() / 2;
            
            _self.select(tabIndex);
        }
        
        e.preventDefault();
    });
}

PrimeFaces.widget.AccordionPanel.prototype.select = function(index) {
    //Call user onTabChange callback
    if(this.cfg.onTabChange) {
        var result = this.cfg.onTabChange.call(this, index);
        if(result == false)
            return false;
    }
    
    var panel = this.panels.eq(index),
    shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

    //update state
    this.stateHolder.val(index);
    this.cfg.active = index;
    
    if(shouldLoad) {
        this.loadDynamicTab(panel);
    }
    else {
        this.show(panel);
        this.fireTabChangeEvent(panel);
    }
    
    return true;
}

PrimeFaces.widget.AccordionPanel.prototype.show = function(panel) {
    //deactivate current
    this.headers.filter('.ui-state-active').removeClass('ui-state-active').next().slideUp();
    
    //activate selected
    panel.slideDown().prev().addClass('ui-state-active').removeClass('ui-state-hover');
    
    //Call user onTabShow callback
    if(this.cfg.onTabShow) {
        this.cfg.onTabShow.call(this, panel);
    }
}

/**
 * Loads tab contents with ajax
 */
PrimeFaces.widget.AccordionPanel.prototype.loadDynamicTab = function(panel) {
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
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }

        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

        return true;
    };
    
    options.oncomplete = function() {
        _self.show(panel);
    };

    var params = {};
    params[this.id + '_contentLoad'] = true;
    params[this.id + '_newTab'] = panel.attr('id');

    options.params = params;
    
    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'];
        
        tabChangeBehavior.call(this, null, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

/**
 * Fires an ajax tabChangeEvent if a tabChangeListener is defined on server side
 */
PrimeFaces.widget.AccordionPanel.prototype.fireTabChangeEvent = function(panel) {
    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_newTab'] = panel.attr('id');

        tabChangeBehavior.call(this, null, ext);
    }
}

PrimeFaces.widget.AccordionPanel.prototype.markAsLoaded = function(panel) {
    panel.data('loaded', true);
}

PrimeFaces.widget.AccordionPanel.prototype.isLoaded = function(panel) {
    return panel.data('loaded') == true;
}

PrimeFaces.widget.AccordionPanel.prototype.collapseAll = function() {
    this.jq.accordion('activate', false);
}

PrimeFaces.widget.AccordionPanel.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}