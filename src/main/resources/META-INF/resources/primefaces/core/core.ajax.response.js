PrimeFaces.ajax.Response = {
		
	handleResponse: function(xml, status, xhr, updateHandler) {
        var partialResponseNode = xml.getElementsByTagName("partial-response")[0];
        var responseTypeNode = partialResponseNode.firstChild;
        
        if (responseTypeNode.nodeName === "redirect") {
        	PrimeFaces.ajax.ResponseProcessor.doRedirect(responseTypeNode);
        }

        if (responseTypeNode.nodeName === "changes") {
    		var changesNode = responseTypeNode.childNodes;

            for (var i = 0; i < changesNode.length; i++) {
            	var currentChangeNode = changesNode[i];
                switch (currentChangeNode.nodeName) {
                    case "update":
                		PrimeFaces.ajax.ResponseProcessor.doUpdate(currentChangeNode, updateHandler);
                        break;
                    case "delete":
                    	PrimeFaces.ajax.ResponseProcessor.doDelete(currentChangeNode);
                        break;
                    case "insert":
                    	PrimeFaces.ajax.ResponseProcessor.doInsert(currentChangeNode);
                        break;
                    case "attributes":
                    	PrimeFaces.ajax.ResponseProcessor.doAttributes(currentChangeNode);
                        break;
                    case "eval":
                    	PrimeFaces.ajax.ResponseProcessor.doEval(currentChangeNode);
                        break;
                    case "extension":
                    	PrimeFaces.ajax.ResponseProcessor.doExtension(currentChangeNode, xhr);
                        break;
                }
            }

    	    PrimeFaces.ajax.Response.handleReFocus();
    	    PrimeFaces.ajax.Response.destroyDetachedWidgets();
        }
	},

	handleReFocus : function() {
		var activeElementId = $(document.activeElement).attr('id');
		
	    // re-focus element
	    if (PrimeFaces.customFocus == false
	            && activeElementId
	            // do we really need to refocus? we just check the current activeElement here
	            && activeElementId != $(document.activeElement).attr('id')) {
	
	    	var elementToFocus = $(PrimeFaces.escapeClientId(activeElementId));
	    	elementToFocus.focus();
	
	    	// double check it - required for IE
	    	setTimeout(function() {
	    		if (!elementToFocus.is(":focus")) {
	    			elementToFocus.focus();
	    		}
	    	}, 150);
	    }

	    PrimeFaces.customFocus = false;
	},

	destroyDetachedWidgets : function() {
	    // destroy detached widgets
	    for (var i = 0; i < PrimeFaces.updatedWidgets.length; i++) {
	    	var widgetVar = PrimeFaces.updatedWidgets[i];
	
	    	var widget = PF(widgetVar);
	    	if (widget) {
				if (widget.isDetached()) {
					widget.destroy();
					PrimeFaces.widgets[widgetVar] = null;
	
					try {
						delete widget;
					} catch (e) {}
				}
	    	}
	    }

	    PrimeFaces.updatedWidgets = [];
	}
}


PrimeFaces.ajax.ResponseProcessor = {

    doRedirect : function(node) {
		window.location = node.getAttribute('url');
	},
	
	doUpdate : function(node, handler) {
		var id = node.getAttribute('id');
		var content = PrimeFaces.ajax.Utils.getContent(node);

		if (handler) {
			// if the handler returns "true", it handles the current id
			if (handler.call(this, id, content) === false) {
				PrimeFaces.ajax.Utils.updateElement(id, content);
			}
		} else {
			PrimeFaces.ajax.Utils.updateElement(id, content);
		}
	},
	
	doEval : function(node) {
		$.globalEval(node.textContent);
	},
	
	doExtension : function(node, xhr) {
		if (xhr) {
			if (node.getAttribute("ln") === "primefaces" && node.getAttribute("type") === "args") {
				xhr.pfArgs = $.parseJSON(node.textContent);
			}
		}
	},
	
	doDelete : function(node) {
		
	},
	
	doInsert : function(node) {
		
	},
	
	doAttributes : function() {
		
	}
}

