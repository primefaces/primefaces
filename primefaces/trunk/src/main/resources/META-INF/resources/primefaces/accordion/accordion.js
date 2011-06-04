/**
 * PrimeFaces Accordion Widget
 */
PrimeFaces.widget.AccordionPanel = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = jQuery(this.jqId + '_acco');
    this.stateHolder = jQuery(this.jqId + '_active');
    var _self = this;

    //Create accordion
    this.jq.accordion(this.cfg);

    if(this.cfg.dynamic && this.cfg.cache) {
        this.markAsLoaded(this.jq.children('div').get(this.cfg.active));
    }
    
    this.jq.bind('accordionchangestart', function(event, ui) {
        _self.onTabChange(event, ui);
    });
}

/**
 * TabChange handler
 */
PrimeFaces.widget.AccordionPanel.prototype.onTabChange = function(event, ui) {
    var panel = ui.newContent.get(0),
    shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);

    //Call user onTabChange callback
    if(this.cfg.onTabChange) {
        this.cfg.onTabChange.call(this, event, ui);
    }

    //Write state
    this.stateHolder.val(ui.options.active);

    if(shouldLoad) {
        this.loadDynamicTab(event, panel);
    } else {
        this.fireTabChangeEvent(event, panel);
    }
}

/**
 * Loads tab contents with ajax
 */
PrimeFaces.widget.AccordionPanel.prototype.loadDynamicTab = function(event, panel) {
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
                jQuery(panel).html(content);

                if(_self.cfg.cache) {
                    _self.markAsLoaded(panel);
                }

            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
            }

        }
        
        PrimeFaces.ajax.AjaxUtils.handleResponse(xmlDoc);

        return false;
    };
    
    options.oncomplete = function() {
        _self.fireTabChangeEvent(event, panel);
    }

    var params = {};
    params[this.id + '_contentLoad'] = true;
    params[this.id + '_newTab'] = panel.id;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

/**
 * Fires an ajax tabChangeEvent if a tabChangeListener is defined on server side
 */
PrimeFaces.widget.AccordionPanel.prototype.fireTabChangeEvent = function(event, panel) {
    if(this.cfg.behaviors) {
        var tabChangeBehavior = this.cfg.behaviors['tabChange'];
        if(tabChangeBehavior) {
            var params = {};
            params[this.id + '_newTab'] = panel.id;

            tabChangeBehavior.call(this, event, params);
        }
    }
}

PrimeFaces.widget.AccordionPanel.prototype.markAsLoaded = function(panel) {
    jQuery(panel).data('loaded', true);
}

PrimeFaces.widget.AccordionPanel.prototype.isLoaded = function(panel) {
    return jQuery(panel).data('loaded') == true;
}

PrimeFaces.widget.AccordionPanel.prototype.select = function(index) {
    this.jq.accordion('activate', index);
}

PrimeFaces.widget.AccordionPanel.prototype.collapseAll = function() {
    this.jq.accordion('activate', false);
}