PrimeFaces.ajax.Response = {

	handleResponse: function(xml, status, xhr) {
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
                    	PrimeFaces.ajax.ResponseProcessor.doUpdate(currentChangeNode);
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

	updateFormStateInput: function(name, value) {
        var trimmedValue = $.trim(value);
        //TODO porletforms
        var forms = this.portletForms ? $(this.portletForms) : $('form');

        forms.each(function() {
            var form = $(this);
            var input = form.children("input[name='" + name + "']");

            if(input.length > 0) {
            	input.val(trimmedValue);
            }
            else
            {
                form.append('<input type="hidden" name="' + name + '" value="' + trimmedValue + '" autocomplete="off" />');
            }
        });
    },
    
    getContent: function(update) {
        var nodes = update.get(0).childNodes,
        content = '';

        for(var i = 0; i < nodes.length; i++) {
            content += nodes[i].nodeValue;
        }
       
        return content;
    },

    doRedirect : function(node) {
		window.location = node.getAttribute('url');
	},
	
	doUpdate : function(node) {
        var id = node.getAttribute('id');
        var content = '';

        for(var i = 0; i < node.childNodes.length; i++) {
            content += node.childNodes[i].nodeValue;
        }

        if(id.indexOf(PrimeFaces.VIEW_STATE) !== -1) {
            PrimeFaces.ajax.ResponseProcessor.updateFormStateInput.call(this, PrimeFaces.VIEW_STATE, content);
        }
        else if(id.indexOf(PrimeFaces.CLIENT_WINDOW) !== -1) {
        	PrimeFaces.ajax.ResponseProcessor.updateFormStateInput.call(this, PrimeFaces.CLIENT_WINDOW, content);
        }
        else if(id === PrimeFaces.VIEW_ROOT) {
        	$.ajaxSetup({'cache' : true});
            $('head').html(content.substring(content.indexOf("<head>") + 6, content.lastIndexOf("</head>")));
            $.ajaxSetup({'cache' : false});

            var bodyStartTag = new RegExp("<body[^>]*>", "gi").exec(content)[0];
            var bodyStartIndex = content.indexOf(bodyStartTag) + bodyStartTag.length;
            $('body').html(content.substring(bodyStartIndex, content.lastIndexOf("</body>")));
        }
        else {
            $(PrimeFaces.escapeClientId(id)).replaceWith(content);
        }
	},
	
	doEval : function(node) {
		$.globalEval(node.textContent);
	},
	
	doExtension : function(node, xhr) {
		if (node.getAttribute("ln") === "primefaces" && node.getAttribute("type") === "args") {
			xhr.pfArgs = $.parseJSON(node.textContent);
		}
	},
	
	doDelete : function(node) {
		
	},
	
	doInsert : function(node) {
		
	},
	
	doAttributes : function() {
		
	}
}

