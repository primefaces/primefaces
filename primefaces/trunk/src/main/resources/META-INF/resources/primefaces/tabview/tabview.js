PrimeFaces.widget.TabView = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.activeIndexHolder = this.jqId + '_activeIndex';
	
    jQuery(this.jqId).tabs(this.cfg);
    
    jQuery(this.jqId).bind('tabsselect', {tabview:this}, this.onTabSelect);
	
    if(this.cfg.dynamic) {
        this.markAsLoaded(jQuery(this.jqId).children('div').get(this.cfg.selected));
    }
}

PrimeFaces.widget.TabView.prototype.onTabSelect = function(event, ui) {
    var _self = event.data.tabview,
    panel = ui.panel,
    shouldLoad = _self.cfg.dynamic && !_self.isLoaded(panel);
	
    jQuery(_self.activeIndexHolder).val(ui.index);

     if(shouldLoad) {
        var params = {};
        params[_self.id + '_dynamicTabRequest'] = true;

        PrimeFaces.ajax.AjaxRequest(
                    _self.cfg.url, 
                    {
                        source: _self.id,
                        process: _self.id,
                        update: _self.id,
                        formId:_self.cfg.formId,
                        onsuccess: function(responseXML) {
                            var xmlDoc = responseXML.documentElement,
                            updates = xmlDoc.getElementsByTagName("update");

                            for(var i=0; i < updates.length; i++) {
                                var id = updates[i].attributes.getNamedItem("id").nodeValue,
                                content = updates[i].firstChild.data;

                                if(id == PrimeFaces.VIEW_STATE) {
                                    PrimeFaces.ajax.AjaxUtils.updateState(content);
                                }
                                else if(id == _self.id){
                                    jQuery(panel).html(content);

                                     if(_self.cfg.cache) {
                                        _self.markAsLoaded(panel);
                                    }
                                }
                                else {
                                    jQuery(PrimeFaces.escapeClientId(id)).replaceWith(content);
                                }
                            }

                            return false;
                        },
                        error: function() {
                            alert('Error in loading dynamic tab content');
                        }
                    },
                    params);
     }
}

PrimeFaces.widget.TabView.prototype.markAsLoaded = function(panel) {
    jQuery(panel).data('loaded', true);
}

PrimeFaces.widget.TabView.prototype.isLoaded = function(panel) {
    return jQuery(panel).data('loaded') == true;
}

PrimeFaces.widget.TabView.prototype.select = function(index) {
    jQuery(this.jqId).tabs('select', index);
}

PrimeFaces.widget.TabView.prototype.disable = function(index) {
    jQuery(this.jqId).tabs('disable', index);
}

PrimeFaces.widget.TabView.prototype.enable = function(index) {
    jQuery(this.jqId).tabs('enable', index);
}

PrimeFaces.widget.TabView.prototype.add = function(url, label, index) {
    jQuery(this.jqId).tabs('add', url, label, index);
}

PrimeFaces.widget.TabView.prototype.remove = function(index) {
    jQuery(this.jqId).tabs('remove', index);
}

PrimeFaces.widget.TabView.prototype.getLength = function() {
    return jQuery(this.jqId).tabs('length');
}