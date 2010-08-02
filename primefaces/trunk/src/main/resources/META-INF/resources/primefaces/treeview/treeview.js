PrimeFaces.widget.TreeView = function(id, definition, config) {
	PrimeFaces.widget.TreeView.superclass.constructor.call(this, id + "_container", definition);
	this.id = id;
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
		
		document.getElementById(this.id + "_selection").value = selected;
	},

	doNodeSelectRequest : function(args) {
        var options = {
            source: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        if(this.cfg.update) {
            options.update = this.cfg.update;
        }

        if(this.cfg.onselectStart)
			options.onstart = this.cfg.onselectStart;
		if(this.cfg.onselectComplete)
			options.oncomplete = this.cfg.onselectComplete;

        var params = {};
		params[this.id + '_rowKey'] = args.node.data.rowKey;
		params[this.id + '_action'] = "SELECT";
		
		PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
	},
	
	collapseListener : function(node) {
		if(!this.cfg.cache) {
            var _self = this;
            
            node.isLoading=true;
			node.updateIcon();
            
            var options = {
                source: this.id,
                process: this.id,
                formId: this.cfg.formId,
                oncomplete: function() {
                    _self.removeChildren(node);
                    node.isLoading=false;
                    node.updateIcon();
                }
            };

            var params = {};
            params[this.id + '_rowKey'] = node.data.rowKey;
            params[this.id + '_action'] = "COLLAPSE";

			PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
		}
	},
	
	doDynamicLoadNodeRequest : function(node, fnLoadComplete) {
        var _self = this.tree;

        var options = {
            source: _self.id,
            process: _self.id,
            update: _self.id,
            formId: _self.cfg.formId,
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
                        var nodes = jQuery.parseJSON(content).nodes;

                        for(var n in nodes) {
                            var nodeData = nodes[n],
                            tempNode = new YAHOO.widget.HTMLNode(nodeData, node, false);
                        }
                    }
                    else {
                        jQuery(PrimeFaces.escapeClientId(id)).replaceWith(content);
                    }
                }

                fnLoadComplete();

                return false;
            }
        };

        var params = {};
        params[_self.id + '_dynamicLoad'] = true;
        params[_self.id + '_rowKey'] = node.data.rowKey;
        params[_self.id + '_action'] = "EXPAND";

        PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
	},
	
	isSelectionEnabled : function() {
		return this.cfg.selectionMode != undefined;
	}
});