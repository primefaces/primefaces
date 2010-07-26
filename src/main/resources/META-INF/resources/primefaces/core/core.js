jQuery(document).ready(function() {
	jQuery('body').addClass('yui-skin-sam');
});

PrimeFaces = {

	escapeClientId : function(id) {
		return "#" + id.replace(/:/g,"\\:");
	},
	
	onContentReady : function(id, fn) {
		YAHOO.util.Event.onContentReady(id, fn, window, true);
	},
	
	cleanWatermarks : function(){
		jQuery.watermark.hideAll();
	},
	
	showWatermarks : function(){
		jQuery.watermark.showAll();
	},
	
	addSubmitParam : function(parent, params) {
		var escapedId = this.escapeClientId(parent);
		
		for(var param in params) {
			if(jQuery(escapedId).children("input[name=\"" + param + "\"]").length == 0)
 				jQuery(escapedId).append("<input type=\"hidden\" name=\"" + param + "\" value=\"" + params[param] + "\"/>");
 		}
	},
	
	PARTIAL_REQUEST_PARAM : "primefacesPartialRequest",
	PARTIAL_UPDATE_PARAM : "primefacesPartialUpdate",
	PARTIAL_PROCESS_PARAM : "primefacesPartialProcess",
	PARTIAL_SOURCE_PARAM : "primefacesPartialSource"
};

PrimeFaces.ajax = {};
PrimeFaces.widget = {};

PrimeFaces.ajax.AjaxUtils = {
	
	STATE_MARKER_EXPR : new RegExp("~com.sun.faces.saveStateFieldMarker~|<!--@@JSF_FORM_STATE_MARKER@@-->|~facelets.VIEW_STATE~", "ig"),
	
	encodeViewState : function() {
		var viewstateValue = document.getElementById("javax.faces.ViewState").value;
		var re = new RegExp("\\+", "g");
		var encodedViewState = viewstateValue.replace(re, "\%2B");
		
		return encodedViewState;
	},
	
	updateState: function(state) {
		jQuery('#javax\\.faces\\.ViewState').each(function(index) {
			jQuery(this).replaceWith(state);
		}); 
	},
	
	serialize: function(params) {
		var serializedParams = "";
		
		for(var param in params) {
			serializedParams = serializedParams + "&" + param + "=" + params[param];	
		}
		
		return serializedParams;
	}
};

PrimeFaces.ajax.AjaxRequest = function(actionURL, cfg, params) {
	var requestParams = "";

	if(cfg.formId) {
		var jqFormId = PrimeFaces.escapeClientId(cfg.formId),
		submitHandlerAttr = jQuery(jqFormId).attr('onsubmit');
		if(submitHandlerAttr) {
			new Function(submitHandlerAttr).call();
		}

		requestParams = jQuery(jqFormId).serialize();
	} else {
		requestParams = "javax.faces.ViewState=" + PrimeFaces.ajax.AjaxUtils.encodeViewState();
	}
	
	requestParams = requestParams + "&" + PrimeFaces.PARTIAL_REQUEST_PARAM + "=true";
	
	if(params) {
		requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params); 
	}
	
	var xhrOptions = {
		url : actionURL,
		type : "POST",
		cache : false,
		dataType : "xml",
		data : requestParams,
		success : function(data, status, xhr) {
			if(cfg.onsuccess) {
				var value = cfg.onsuccess(data, status, xhr, this.args);
				if(value === false)
					return;
			}
		
			PrimeFaces.ajax.AjaxResponse.call(this, data, status, xhr);
		},
		complete : function(xhr, status) {
			if(cfg.oncomplete) {
				cfg.oncomplete(xhr, status, this.args);
			}

			PrimeFaces.ajax.RequestManager.poll();
		}
	};
	
	xhrOptions.global = cfg.global;

	if(cfg.onstart) {
		xhrOptions.beforeSend = cfg.onstart;
	}
	
	if(cfg.onerror) {
		xhrOptions.error = cfg.onerror;
	}

	if(cfg.async) {
		jQuery.ajax(xhrOptions);
	} else {
		PrimeFaces.ajax.RequestManager.offer(xhrOptions);
	}
}

PrimeFaces.ajax.AjaxResponse = function(responseXML) {
	var xmlDoc = responseXML.documentElement;
	
	var redirect = xmlDoc.getElementsByTagName("redirect-url");
	if(redirect.length > 0) {
		window.location = redirect[0].firstChild.data;
	} else {
		var components = xmlDoc.getElementsByTagName("component"),
		state = xmlDoc.getElementsByTagName("state")[0].firstChild.data,
		callbackParams = xmlDoc.getElementsByTagName("callbackParam");
		
		PrimeFaces.ajax.AjaxUtils.updateState(state);
		
		for(var i=0; i < components.length; i++) {
			var clientId = components[i].childNodes[0].firstChild.data,
			output = components[i].childNodes[1].firstChild.data;
			
			//Replace any statemarkers with actual state
			var filteredOutput = output.replace(PrimeFaces.ajax.AjaxUtils.STATE_MARKER_EXPR, state);
			
			jQuery(PrimeFaces.escapeClientId(clientId)).replaceWith(filteredOutput);
		}
		
		var args = {};
		for(var j=0; j < callbackParams.length; j++) {
			var jsonObj = jQuery.parseJSON(callbackParams[j].firstChild.data);
			
			for(var paramName in jsonObj) {
				if(paramName)
					args[paramName] = jsonObj[paramName];
			}
		}
		
		this.args = args;
	}
}

PrimeFaces.ajax.RequestManager = {
		
    requests : new Array(),

    offer : function(req) {
        this.requests.push(req);

        if(this.requests.length == 1) {
        	var retVal = jQuery.ajax(req);
        	if(retVal === false)
        		this.poll();
        }
    },

    poll : function() {
    	if(this.isEmpty()) {
    		return null;
    	}
 
    	var processedRequest = this.requests.shift();
    	var nextRequest = this.peek();
    	if(nextRequest != null) {
    		jQuery.ajax(nextRequest);
    	}

    	return processedRequest;
    },

    peek : function() {
    	if(this.isEmpty()) {
    		return null;
    	}
    
    	var nextRequest = this.requests[0];
  
    	return nextRequest;
    },
    
    isEmpty : function() {
        return this.requests.length == 0;
    }
};