/**
 * PrimeFaces Accordion Widget
 */
PrimeFaces.widget.AccordionPanel = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.cfg.id);
    this.jq = $(this.jqId);
    this.stateHolder = $(this.jqId + '_active');
    this.headers = this.jq.children('.ui-accordion-header');
    this.panels = this.jq.children('.ui-accordion-content');
    this.headers.children('a').disableSelection();
    this.onshowHandlers = [];
    
    //options
    this.cfg.active = this.cfg.multiple ? this.stateHolder.val().split(',') : this.stateHolder.val();
    
    this.bindEvents();
    
    if(this.cfg.dynamic && this.cfg.cache) {
        this.markAsLoaded(this.panels.eq(this.cfg.active));
    }
    
    this.panels.data('widget', this);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.AccordionPanel, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.AccordionPanel.prototype.bindEvents = function() {
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
}

/**
 *  Activates a tab with given index
 */
PrimeFaces.widget.AccordionPanel.prototype.select = function(index) {
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
        this.show(panel);
        this.fireTabChangeEvent(panel);
    }
    
    return true;
}

/**
 *  Deactivates a tab with given index
 */
PrimeFaces.widget.AccordionPanel.prototype.unselect = function(index) {
    var panel = this.panels.eq(index),
    header = panel.prev();
    
    header.children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
    header.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all');
    panel.slideUp();
    
    this.removeFromSelection(index);
    this.saveState();
}

PrimeFaces.widget.AccordionPanel.prototype.show = function(panel) {
    var _self = this;
    
    //deactivate current
    if(!this.cfg.multiple) {
        var oldHeader = this.headers.filter('.ui-state-active');
        oldHeader.children('.ui-icon').removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
        oldHeader.removeClass('ui-state-active ui-corner-top').addClass('ui-corner-all').next().slideUp();
    }
    
    //activate selected
    var newHeader = panel.prev();
    newHeader.addClass('ui-state-active ui-corner-top').removeClass('ui-state-hover ui-corner-all')
             .children('.ui-icon').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');
             
    panel.slideDown('normal', function() {
        _self.postTabShow(panel);
    });
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
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
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
    params[this.id + '_tabindex'] = parseInt(panel.index() / 2);

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
        ext.params[this.id + '_tabindex'] = parseInt(panel.index() / 2);


        tabChangeBehavior.call(this, null, ext);
    }
}

PrimeFaces.widget.AccordionPanel.prototype.markAsLoaded = function(panel) {
    panel.data('loaded', true);
}

PrimeFaces.widget.AccordionPanel.prototype.isLoaded = function(panel) {
    return panel.data('loaded') == true;
}

PrimeFaces.widget.AccordionPanel.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}

PrimeFaces.widget.AccordionPanel.prototype.addToSelection = function(nodeId) {
    this.cfg.active.push(nodeId);
}

PrimeFaces.widget.AccordionPanel.prototype.removeFromSelection = function(nodeId) {
    this.cfg.active = $.grep(this.cfg.active, function(r) {
        return r != nodeId;
    });
}

PrimeFaces.widget.AccordionPanel.prototype.saveState = function() {
    if(this.cfg.multiple)
        this.stateHolder.val(this.cfg.active.join(','));
    else
        this.stateHolder.val(this.cfg.active);
}

PrimeFaces.widget.AccordionPanel.prototype.addOnshowHandler = function(fn) {
    this.onshowHandlers.push(fn);
}

PrimeFaces.widget.AccordionPanel.prototype.postTabShow = function(newPanel) {            
    //Call user onTabShow callback
    if(this.cfg.onTabShow) {
        this.cfg.onTabShow.call(this, newPanel);
    }
    
    //execute onshowHandlers and remove successful ones
    this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
		return !fn.call();
	});
}