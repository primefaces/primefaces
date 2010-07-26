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
        params[PrimeFaces.PARTIAL_SOURCE_PARAM] = _self.id;
        params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
        params[PrimeFaces.PARTIAL_PROCESS_PARAM] = _self.id;
		
        var requestParams = jQuery(PrimeFaces.escapeClientId(_self.cfg.formId)).serialize();
        requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params);
		
        jQuery.ajax({
            url: _self.cfg.url,
            type: "POST",
            cache: false,
            dataType: "xml",
            data: requestParams,
            success: function(responseXML) {
                var xmlDoc = responseXML.documentElement,
                content = xmlDoc.getElementsByTagName("tabContent")[0].firstChild.data,
                state = xmlDoc.getElementsByTagName("state")[0].firstChild.data;
						
                PrimeFaces.ajax.AjaxUtils.updateState(state);
						
                jQuery(panel).html(content);
						
                if(_self.cfg.cache) {
                    _self.markAsLoaded(panel);
                }
            },
            failure: function() {
                alert('Error in loading dynamic tab content');
            }
        });
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