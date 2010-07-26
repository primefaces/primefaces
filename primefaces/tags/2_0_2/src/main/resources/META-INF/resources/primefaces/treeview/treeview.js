PrimeFaces.widget.TreeView = function(id, definition, config) {
	PrimeFaces.widget.TreeView.superclass.constructor.call(this, id + "_container", definition);
	this.clientId = id;
	this.cfg = config;
	
	//Custom nodeclick handler
	if(this.cfg.onNodeClick) {
		this.subscribe('clickEvent', this.cfg.onNodeClick);
	}
	
	this.subscribe("clickEvent", this.handleNodeClick);

	//Selection
	if(this.isSelectionEnabled()) {
		this.setNodesProperty('propagateHighlightDown', this.cfg.propagateHighlightDown); 
		this.setNodesProperty('propagateHighlightUp', this.cfg.propagateHighlightUp);
		
		if(this.cfg.selectionMode === 'single') {
			this.singleNodeHighlight = true;
		}		
	}
	
	if(this.cfg.dynamic) {
		this.subscribe("expand", this.expandListener);
		this.subscribe("collapse", this.collapseListener);
	}
	
	//Dynamic tree
	if(this.cfg.dynamic) {
		this.setDynamicLoad(this.doDynamicLoadNodeRequest);
	}
}

YAHOO.lang.extend(PrimeFaces.widget.TreeView, YAHOO.widget.TreeView,
{
	handleNodeClick : function(args) {
		if(this.isSelectionEnabled()) {
			this.handleNodeSelection(args);
			
			if(this.cfg.hasSelectListener || this.cfg.update) {
				this.doNodeSelectRequest(args);
			}
		} else {
			args.node.focus();
		}

		return false;
	},
	
	handleNodeSelection : function(args) {
		this.onEventToggleHighlight(args);
		
		var selected,
		nodes = this.getNodesByProperty('highlightState', 1),
		rowKeys = [];
		
		if(nodes) {
			for(var i = 0; i < nodes.length; i++) {
				rowKeys.push(nodes[i].data.rowKey);
			}
			
			selected = rowKeys.join(",");
		} else {
			selected = "";
		}
		
		document.getElementById(this.clientId + "_selection").value = selected;
		
		return false;
	},

	doNodeSelectRequest : function(args) {
		this.selectedAction = "SELECT";
		this.selectedRowKey = args.node.data.rowKey;
	
		var params = this.getNodeSelectRequestParams();
		var options = {formId: this.cfg.formId};
		
		if(this.cfg.onselectStart)
			options.onstart = this.cfg.onselectStart;
		if(this.cfg.onselectComplete)
			options.oncomplete = this.cfg.onselectComplete;
		
		PrimeFaces.ajax.AjaxRequest(this.cfg.actionURL, options, params);
		
		return false;
	},
	
	expandListener : function(node) {
		this.selectedAction = "EXPAND";
		this.selectedRowKey = node.data.rowKey;
	},
	
	collapseListener : function(node) {
		if(!this.cfg.cache) {
			this.selectedAction = "COLLAPSE";
			this.selectedRowKey = node.data.rowKey;
			
			node.isLoading=true;
			node.updateIcon();
			
			var callback = {
			  success: this.handleCollapseComplete,
			  argument: {
				"node": node
				},
			  scope:this
			};
			
			YAHOO.util.Connect.asyncRequest('POST', this.cfg.actionURL, callback, this.getToggleRequestParams());
		}
	},
	
	handleCollapseComplete : function(response) {
		var node = response.argument.node;
		this.removeChildren(node);
		node.isLoading=false;
		node.updateIcon();
	},
	
	doDynamicLoadNodeRequest : function(node, fnLoadComplete) {
		var url = this.tree.cfg.actionURL,
		params = this.tree.getToggleRequestParams(),
		callback = {
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
		var node = response.argument.node,
		xmlDoc = response.responseXML.documentElement,
		nodes = xmlDoc.getElementsByTagName("node");
		
		for(var i=0; i < nodes.length; i++) {
			var content = nodes[i].childNodes[0].firstChild.data,
			rowKeyValue = nodes[i].childNodes[1].firstChild.data,
			isLeafStringValue = nodes[i].childNodes[2].firstChild.data,
			isLeafValue = (isLeafStringValue == "true") ? true : false,
			nodeData = {html: content, rowKey: rowKeyValue, isLeaf: isLeafValue};
			
			if(nodes[i].childNodes[3]) {
				nodeData.contentStyle = nodes[i].childNodes[3].firstChild.data;
			}
			
			var tempNode = new YAHOO.widget.HTMLNode(nodeData, node, false);
		}
		
		response.argument.fnLoadComplete();
	},

	handleDynamicLoadFailure : function(response) {
		alert("Exception occured in dynamically loading tree node:" + response.responseText);
	},
	
	getToggleRequestParams : function() {
		var requestParams = jQuery(PrimeFaces.escapeClientId(this.cfg.formId)).serialize();
		
		var params = {};
		params[this.clientId] = this.clientId;
		params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.clientId;
		params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.clientId;
		params[this.clientId + "_rowKey"] = this.selectedRowKey;
		params[this.clientId + "_action"] = this.selectedAction;
		
		requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params); 
		
		return requestParams;
	},
	
	getNodeSelectRequestParams : function() {
		var params = {};
		params[this.clientId] = this.clientId;
		params[this.clientId + '_rowKey'] = this.selectedRowKey;
		params[this.clientId + '_action'] = this.selectedAction;
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.update;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.clientId;
		
		return params;
	},
	
	isSelectionEnabled : function() {
		return this.cfg.selectionMode != undefined;
	}
});