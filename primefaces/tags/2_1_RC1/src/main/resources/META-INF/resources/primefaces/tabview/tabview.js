PrimeFaces.widget.TabView = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.activeIndexHolder = this.jqId + '_activeIndex';
	
	jQuery(this.jqId).tabs(this.cfg);
	jQuery(this.jqId).bind('tabsselect', {tabview:this}, this.onTabSelect);
	
	if(this.cfg.dynamic) {
		this.markAsLoaded(jQuery(this.jqId).children('div').eq(this.cfg.selected));
	}
}

PrimeFaces.widget.TabView.prototype.onTabSelect = function(event, ui) {
	var t = event.data.tabview,
	panel = ui.panel,
	shouldLoad = t.cfg.dynamic && !t.isLoaded(panel);
	
	jQuery(t.activeIndexHolder).val(ui.index);
	
	if(shouldLoad) {		
		var params = {};
		params[PrimeFaces.PARTIAL_SOURCE_PARAM] = t.id;
		params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = t.id;
		
		var requestParams = jQuery(PrimeFaces.escapeClientId(t.cfg.formId)).serialize();
		requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params);
		
		jQuery.ajax({url: t.cfg.url,
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
						
						if(t.cfg.cache) {
							t.markAsLoaded(panel);
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

PrimeFaces.widget.TabView.prototype.selectTab = function(index) {
	jQuery(this.jqId).tabs('select', index);
}