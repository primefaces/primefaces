PrimeFaces.ajax = {};

PrimeFaces.ajax.Utils = {
 
    getContent: function(node) {
        var content = '';

        for(var i = 0; i < node.childNodes.length; i++) {
            content += node.childNodes[i].nodeValue;
        }

        return content;
    },

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

    updateElement: function(id, content) {
        if(id.indexOf(PrimeFaces.VIEW_STATE) !== -1) {
        	PrimeFaces.ajax.Utils.updateFormStateInput(PrimeFaces.VIEW_STATE, content);
        }
        else if(id.indexOf(PrimeFaces.CLIENT_WINDOW) !== -1) {
        	PrimeFaces.ajax.Utils.updateFormStateInput(PrimeFaces.CLIENT_WINDOW, content);
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
    }
};


//backward compatibility
PrimeFaces.ajax.AjaxRequest = function(cfg, ext) {
	return PrimeFaces.ajax.Request.handleRequest(cfg, ext);
}

PrimeFaces.ajax.AjaxUtils = {
	getContent : function(update) {
		return PrimeFaces.ajax.Utils.getContent(update.get(0));
	},

	updateElement : function(id, data) {
		PrimeFaces.ajax.Utils.updateElement(id, data);
	},
	
	handleResponse: function(xml) {
		//just for backward compatibility
		//we just always return true, so that no update will be processed
		//it will actually be done in the widgets itself
		PrimeFaces.ajax.Response.handleResponse(xml, null, null, function(id, content) {
			return true;
		});
	}
}






