if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.TreeView = function(id, definition, config) {
	PrimeFaces.widget.TreeView.superclass.constructor.call(this, id, definition);
	this.cfg = config;
	
	this.subscribe("clickEvent", this.labelClickListener);
	this.subscribe("expand", this.expandListener);
	this.subscribe("collapse", this.collapseListener);
	this.setupToggleMode();
}

YAHOO.lang.extend(PrimeFaces.widget.TreeView, YAHOO.widget.TreeView,
{
	TOGGLE_MODE_CLIENT : "client",
	
	TOGGLE_MODE_ASYNC : "async",
	
	labelClickListener : function(e) {
		if(this.cfg.onNodeClick != undefined) {
			this.cfg.onNodeClick(e);
		}
		
		if(this.isAsync()) {
			document.getElementById(this.cfg.eventFieldId).value = "SELECT";
			document.getElementById(this.cfg.rowKeyFieldId).value = e.node.data.rowKey;
			
			var url = this.cfg.actionURL;
			var params = this.getAsyncRequestParams();
			var callback = {
			  success: function(response){/*nothing to do yet with response*/}
			};
			
			YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
			
			return false;
		}
	},
	
	expandListener : function(node) {
		if(this.isAsync()) {
			document.getElementById(this.cfg.eventFieldId).value = "EXPAND";
			document.getElementById(this.cfg.rowKeyFieldId).value = node.data.rowKey;
		}
	},
	
	collapseListener : function(node) {
		if(this.isAsync() && !this.cfg.cache) {
			document.getElementById(this.cfg.eventFieldId).value = "COLLAPSE";
			document.getElementById(this.cfg.rowKeyFieldId).value = node.data.rowKey;
			
			node.isLoading=true;
			node.updateIcon();
			
			var url = this.cfg.actionURL;
			var params = this.getAsyncRequestParams();
			var callback = {
			  success: this.handleCollapseComplete,
			  argument: {
				"node": node
				},
			  scope:this
			};
			
			YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
		}
	},
	
	handleCollapseComplete : function(response) {
		var node = response.argument.node;
		this.removeChildren(node);
		node.isLoading=false;
		node.updateIcon();
	},
	
	setupToggleMode : function() {
		if(this.cfg.toggleMode == this.TOGGLE_MODE_ASYNC)
			 this.setDynamicLoad(this.doDynamicLoadNodeRequest);
	},

	doDynamicLoadNodeRequest : function(node, fnLoadComplete) {
		var url = this.tree.cfg.actionURL;
		var params = this.tree.getAsyncRequestParams();
		
		var callback = {
		  success: this.tree.handleDynamicLoadSuccess,
		  failure: this.tree.handleDynamicLoadFailure,
		  argument: {
			"node": node,
			"fnLoadComplete": fnLoadComplete
			}
		};

		YAHOO.util.Connect.asyncRequest('POST', url, callback, params);
	},
	
	handleDynamicLoadSuccess : function(response) {
		var node = response.argument.node;
		var xmlDoc = response.responseXML.documentElement;
		var nodes = xmlDoc.getElementsByTagName("node");
		
		for(var i=0; i < nodes.length; i++) {
			var labelValue = nodes[i].childNodes[0].firstChild.data;
			var rowKeyValue = nodes[i].childNodes[1].firstChild.data;
			var isLeafStringValue = nodes[i].childNodes[2].firstChild.data;
			var isLeafValue = (isLeafStringValue == "true") ? true : false;
			
			var nodeData = {label: labelValue, rowKey: rowKeyValue, isLeaf: isLeafValue};
			
			var tempNode = new YAHOO.widget.TextNode(nodeData, node, false);
		}
		
		response.argument.fnLoadComplete();
	},
	
	handleDynamicLoadFailure : function(response) {
		alert("Failed:" + response.responseText);
	},
	
	isAsync : function() {
		return this.cfg.toggleMode == this.TOGGLE_MODE_ASYNC;
	},
	
	getAsyncRequestParams : function() {
		var viewstate = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		
		var params = "ajaxSource=" + this.cfg.clientId;
		params = params + "&primefacesAjaxRequest=true";
		params = params + "&javax.faces.ViewState=" + viewstate;
		params = params + "&" + this.cfg.rowKeyFieldId + "=" + document.getElementById(this.cfg.rowKeyFieldId).value;
		params = params + "&" + this.cfg.eventFieldId + "=" + document.getElementById(this.cfg.eventFieldId).value;
		params = params + "&" + this.cfg.clientId + "=" + this.cfg.clientId;
		params = params + "&" + PrimeFaces.ajax.AjaxUtils.getFormParam(this.cfg.formClientId);
		
		return params;
	}
});