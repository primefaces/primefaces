PrimeFaces.widget.TabView = function(clientId, cfg) {
	PrimeFaces.widget.TabView.superclass.constructor.call(this, clientId, cfg);
	
	this.clientId = clientId;
	this.cfg = cfg;
	this.setupTabClickListeners();
	
	if(this.cfg.dynamic && this.cfg.cache)
		this.getTab(this.get('activeIndex')).loaded = true;
	
	if(this.cfg.contentTransition)
		this.setupContentTransition(config);
}

YAHOO.lang.extend(PrimeFaces.widget.TabView, YAHOO.widget.TabView,
{
	setupTabClickListeners : function() {
		for(var i=0; i < this.get('tabs').length; i++)
		    this.getTab(i).addListener('click', this.handleTabClick, null, this);
	},
	
	handleTabClick : function(e) {
		var activeIndex = this.get('activeIndex'),
		activeTab = this.getTab(activeIndex);
		
		document.getElementById(this.clientId + '_activeIndex').value = activeIndex;
		
		if(this.cfg.dynamic && !activeTab.loaded) {
			YAHOO.util.Dom.addClass(activeTab.get('contentEl').parentNode, "loading");
			
			var params = {};
			params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.clientId;
			params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
			params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.clientId;
			
			var requestParams = jQuery(PrimeFaces.escapeClientId(this.cfg.formId)).serialize();
			requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params);
			
			jQuery.ajax({url: this.cfg.url,
					  	type: "POST",
					  	cache: false,
					  	dataType: "xml",
					  	data: requestParams,
					  	context: this,
					  	global: false,
					  	success: this.handleDynamicTabLoadSuccess,
					  	failure: this.handleDynamicTabLoadFailure
					});
		}
	},
	
	handleDynamicTabLoadSuccess : function(responseXML) {
		var xmlDoc = responseXML.documentElement,
		content = xmlDoc.getElementsByTagName("tabContent")[0].firstChild.data,
		state = xmlDoc.getElementsByTagName("state")[0].firstChild.data,
		tab = this.getTab(this.get('activeIndex')),
		tabContentEl = tab.get('contentEl');
		
		PrimeFaces.ajax.AjaxUtils.updateState(state);
		jQuery(tabContentEl).html(content);

		YAHOO.util.Dom.removeClass(tabContentEl.parentNode, "loading");
		
		if(this.cfg.cache)
			tab.loaded = true;
	},
	
	handleDynamicTabLoadFailure : function(responseXML) {
		alert('Error in loading dynamic tab content');
	},

	fadeTransition : function(newTab, oldTab) { 
		if (newTab.anim && newTab.anim.isAnimated()) {
			newTab.anim.stop(true);
		}

		newTab.set('contentVisible', true);
		YAHOO.util.Dom.setStyle(newTab.get('contentEl'), 'opacity', 0);

		newTab.anim = newTab.anim || new YAHOO.util.Anim(newTab.get('contentEl'));
		newTab.anim.attributes.opacity = {
			to :1
		};

		var hideContent = function() {
			oldTab.set('contentVisible', false);
			oldTab.anim.onComplete.unsubscribe(hideContent);
		};

		oldTab.anim = oldTab.anim || new YAHOO.util.Anim(oldTab.get('contentEl'));
		oldTab.anim.onComplete.subscribe(hideContent, this, true);
		oldTab.anim.attributes.opacity = {
			to :0
		};

		newTab.anim.animate();
		oldTab.anim.animate();
	},
	
	setupContentTransition : function(config) {
		if(config.contentTransition != undefined && config.contentTransition == true)
			this.contentTransition = this.fadeTransition;
	}
});