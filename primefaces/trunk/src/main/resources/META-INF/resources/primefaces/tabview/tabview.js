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
 * - Fires an ajax tabChangeEvent if there is one on server side
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
        this.loadDynamicTab(panel);
    }
    else if(this.cfg.behaviors) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'];
        if(tabChangeBehavior) {
            this.fireAjaxTabChangeEvent(event, panel);
        }
    }
}

/**
 * Loads tab contents with ajax
 */
PrimeFaces.widget.TabView.prototype.loadDynamicTab = function(panel) {
    var _self = this,
    options = {
        source: this.id,
        process: this.id
    };

    options.update = this.cfg.ajaxTabChange ? this.id + ' ' + this.cfg.onTabChangeUpdate : this.id;

    options.onsuccess = function(responseXML) {
        var xmlDoc = responseXML.documentElement,
        updates = xmlDoc.getElementsByTagName("update");

        for(var i=0; i < updates.length; i++) {
            var id = updates[i].attributes.getNamedItem("id").nodeValue,
            content = updates[i].firstChild.data;

            if(id == _self.id){
                jQuery(panel).html(content);

                if(_self.cfg.cache) {
                    _self.markAsLoaded(panel);
                }
                
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }

        }

        return false;
    };

    var params = {};
    params[this.id + '_contentLoad'] = true;
    params[this.id + '_newTab'] = panel.id;

    if(this.cfg.ajaxTabChange) {
        params[this.id + '_tabChange'] = true;
    }

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 * Fires an ajax tabChangeEvent if a tabChangeListener is defined on server side
 */
PrimeFaces.widget.TabView.prototype.fireAjaxTabChangeEvent = function(event, panel) {
    var tabChangeBehavior = this.cfg.behaviors['tabChange'],
    params = {};
    params[this.id + '_newTab'] = panel.id;
    
    tabChangeBehavior.call(this, event, params);
}

PrimeFaces.widget.TabView.prototype.markAsLoaded = function(panel) {
    jQuery(panel).data('loaded', true);
}

PrimeFaces.widget.TabView.prototype.isLoaded = function(panel) {
    return jQuery(panel).data('loaded') == true;
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