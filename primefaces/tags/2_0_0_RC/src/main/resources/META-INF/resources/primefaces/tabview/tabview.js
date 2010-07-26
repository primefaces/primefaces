if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.TabView = function(id, config) {
	PrimeFaces.widget.TabView.superclass.constructor.call(this, id, config);
	
	this.clientId = config.clientId;
	this.toggleMode = config.toggleMode;
	this.actionURL = config.actionURL;
	this.cache = config.cache;
	
	this.setupDefaultTabClickListeners(config.activeTabIndexHolder);
	this.setupContentTransition(config);
	
	if(this.isAsyncToggling()) {
		this.setupDynamicLoading();
	}
}

YAHOO.lang.extend(PrimeFaces.widget.TabView, YAHOO.widget.TabView,
{
	TOGGLE_MODE_CLIENT : "client",
	TOGGLE_MODE_ASYNC : "async",
	
	toggleMode : null,
	
	actionURL : null,
	
	clientId : null,
	
	cache: null,
	
	setupDefaultTabClickListeners : function(activeTabIndexHolder) {
	
		for (var i=0; i < this.get('tabs').length; i=i+1) {
		    this.getTab(i).addListener('click', 
		    					function(e, tabview) {
								var activeIndex = tabview.get('activeIndex');
								document.getElementById(activeTabIndexHolder).value = activeIndex;
							}, this);
		    
		    if(this.isAsyncToggling()) {
		    	this.getTab(i).addListener('click', 
    					function(e, tabview) {
							tabview.doDynamicTabContentRequest(tabview.get('activeIndex'));
					}, this);
			}
		}
	},
	
	setupDynamicLoading : function() {
		if(this.cache == true)
			this.get("activeTab").cacheData = true;
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
	},
	
	doDynamicTabContentRequest : function(activeTabIndex) {
		var tab = this.getTab(activeTabIndex);
		if(tab.cacheData == true)
			return;
			
		tab.set('content', "");
		YAHOO.util.Dom.addClass(tab.get('contentEl').parentNode, tab.LOADING_CLASSNAME);
		
		var url = this.actionURL;
		var viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		
		var params = "ajaxSource=" + this.clientId;
		params = params + "&primefacesAjaxRequest=true";
		params = params + "&javax.faces.ViewState=" + viewstate;
		params = params + "&activeTabIndex=" + activeTabIndex;
		
		var callback = {
				  success: this.handleDynamicLoadSuccess,
				  failure: this.handleDynamicLoadFailure,
				  scope: this,
				  argument: {
					"tab": tab
					}
				};

		YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
	},
	
	handleDynamicLoadSuccess : function(response) {
		var tab = response.argument.tab;
		YAHOO.util.Dom.removeClass(tab.get('contentEl').parentNode, tab.LOADING_CLASSNAME);
		tab.set('content', response.responseText);
		
		if(this.cache == true)
			tab.cacheData = true;
	},
	
	isAsyncToggling : function() {
		return (this.toggleMode != null && this.toggleMode == this.TOGGLE_MODE_ASYNC);
	}
	
});