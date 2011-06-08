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

    //tab click handler
    this.cfg.select = function(event, ui) {
        _self.onTabSelect(event, ui);
    };

    //tab show handler
    if(this.cfg.onTabShow) {
        this.cfg.show = function(event, ui) {
            _self.cfg.onTabShow.call(_self, event, ui);
        };
    }

    //Create tabs
    this.jq.tabs(this.cfg);
	
    if(this.cfg.dynamic && this.cfg.cache) {
        this.markAsLoaded(this.jq.children('div').get(this.cfg.selected));
    }
}

/**
 * Tab change handler
 *
 * - Saves the activeIndex and loads content if necessary for dynamic tabs
 * - Invokes tabChange behavior event if defined
 */
PrimeFaces.widget.TabView.prototype.onTabSelect = function(event, ui) {
    var panel = ui.panel,
    shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

    //Call user onTabChange callback
    if(this.cfg.onTabChange) {
        this.cfg.onTabChange.call(this, event, ui);
    }

    //Write state
    this.stateHolder.val(ui.index);

    if(shouldLoad) {
        this.loadDynamicTab(event, panel);
    } else {
        this.fireTabChangeEvent(event, panel);
    }
}

/**
 * Loads tab contents with ajax
 */
PrimeFaces.widget.TabView.prototype.loadDynamicTab = function(event, panel) {
    var _self = this,
    options = {
        source: this.id,
        process: this.id,
        update: this.id
    };

    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

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
        
        PrimeFaces.ajax.AjaxUtils.handleResponse(xmlDoc);

        return true;
    };


    var params = {};
    params[this.id + '_contentLoad'] = true;
    params[this.id + '_newTab'] = panel.id;

    options.params = params;

    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'];
        
        tabChangeBehavior.call(this, event, options);
    }
    else {
        PrimeFaces.ajax.AjaxRequest(options);
    }
}

PrimeFaces.widget.TabView.prototype.markAsLoaded = function(panel) {
    $(panel).data('loaded', true);
}

PrimeFaces.widget.TabView.prototype.isLoaded = function(panel) {
    return $(panel).data('loaded') == true;
}

PrimeFaces.widget.TabView.prototype.select = function(index) {
    this.jq.tabs('select', index);
}

//Backward compatibility
PrimeFaces.widget.TabView.prototype.selectTab = function(index) {
    this.jq.tabs('select', index);
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
    this.jq.tabs('remove', index);
}

PrimeFaces.widget.TabView.prototype.getLength = function() {
    return this.jq.tabs('length');
}

PrimeFaces.widget.TabView.prototype.getActiveIndex = function() {
    return this.jq.tabs('option', 'selected');
}

PrimeFaces.widget.TabView.prototype.fireTabChangeEvent = function(event, panel) {
    if(this.hasBehavior('tabChange')) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'],
        ext = {
            params: {}
        };
        ext.params[this.id + '_newTab'] = panel.id;

        tabChangeBehavior.call(this, event, ext);
    }
}

PrimeFaces.widget.TabView.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}