PrimeFaces.widget.TabView = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.activeIndexHolder = this.jqId + '_activeIndex';
	
    jQuery(this.jqId).tabs(this.cfg);

    var _self = this;
    jQuery(this.jqId).bind('tabsselect', function(event, ui) {
        _self.onTabSelect(event, ui);
    });
	
    if(this.cfg.dynamic) {
        this.markAsLoaded(jQuery(this.jqId).children('div').get(this.cfg.selected));
    }
}

PrimeFaces.widget.TabView.prototype.onTabSelect = function(event, ui) {
    var panel = ui.panel,
    shouldLoad = this.cfg.dynamic && !this.isLoaded(panel);
	
    jQuery(this.activeIndexHolder).val(ui.index);

    if(shouldLoad) {
        var _self = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId:this.cfg.formId
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
                    PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
                }

            }

            return false;
        };
        
        var params = {};
        params[this.id + '_dynamicTabRequest'] = true;

        PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
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