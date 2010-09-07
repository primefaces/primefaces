/**
 * PrimeFaces Accordion Widget
 */
PrimeFaces.widget.AccordionPanel = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jqAcco = this.jqId + '_acco';
    this.stateHolder = this.jqId + '_active';
	
    jQuery(this.jqAcco).accordion(this.cfg);

    if(this.cfg.dynamic) {
        this.markAsLoaded(jQuery(this.jqAcco).children('div').get(this.cfg.active));
    }

    var _self = this;
    jQuery(this.jqAcco).bind('accordionchangestart', function(event, ui) {
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
    jQuery(this.stateHolder).val(ui.options.active);

    if(shouldLoad) {
        this.loadDynamicTab(panel);
    }
    else if(this.cfg.ajaxTabChange) {
        this.fireAjaxTabChangeEvent(panel);
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
        formId: this.cfg.formId
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
                PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
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

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Fires an ajax tabChangeEvent if a tabChangeListener is defined on server side
 */
PrimeFaces.widget.AccordionPanel.prototype.fireAjaxTabChangeEvent = function(panel) {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onTabChangeUpdate) {
        options.update = this.cfg.onTabChangeUpdate;
    }

    var params = {};
    params[this.id + '_tabChange'] = true;
    params[this.id + '_newTab'] = panel.id;

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

PrimeFaces.widget.AccordionPanel.prototype.markAsLoaded = function(panel) {
    jQuery(panel).data('loaded', true);
}

PrimeFaces.widget.AccordionPanel.prototype.isLoaded = function(panel) {
    return jQuery(panel).data('loaded') == true;
}

PrimeFaces.widget.AccordionPanel.prototype.select = function(index) {
    jQuery(this.jqAcco).accordion('activate', index);
}

PrimeFaces.widget.AccordionPanel.prototype.collapseAll = function() {
    jQuery(this.jqAcco).accordion('activate', false);
}