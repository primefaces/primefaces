if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.ajax == undefined) PrimeFaces.ajax = {};

PrimeFaces.ajax.AjaxUtils = {
		
	VIEW_STATE_PARAM : "javax.faces.ViewState",
	
	STATE_MARKER_EXPR : new RegExp("~com.sun.faces.saveStateFieldMarker~|<!--@@JSF_FORM_STATE_MARKER@@-->|~facelets.VIEW_STATE~", "ig"),
	
	encodeViewState : function() {
		var viewstateValue = document.getElementById("javax.faces.ViewState").value;
		var re = new RegExp("\\+", "g");
		var encodedViewState = viewstateValue.replace(re, "\%2B");
		
		return encodedViewState;
	},
	
	getFormParam : function(formId) {
		var mojarraFormId = formId;
		var myfacesFormId = formId + "_SUBMIT";
		
		//unfortunately implementations behave differently when marking form as submitted
		var isMyfaces = document.getElementsByName(myfacesFormId).length == 1;
		
		if(isMyfaces)
			return myfacesFormId + "=1"; 
		else
			return mojarraFormId + "=" + mojarraFormId;
	},
	
	updateState: function(state) {
		jQuery("input[name=" + this.VIEW_STATE_PARAM + "]").replaceWith(state);
	}
};

PrimeFaces.ajax.AjaxRequestEvent = function(url,cfg,parameters) {
	this.url = url;
	this.cfg = cfg;
	this.parameters = parameters;
}

/**
 * Custom Ajax event handler to attach to dom events to initiate an ajax request
 */
PrimeFaces.ajax.AjaxRequestEventHandler = function(domEvent, ajaxRequestEvent) {
	PrimeFaces.ajax.AjaxRequest(ajaxRequestEvent.url, ajaxRequestEvent.cfg, ajaxRequestEvent.parameters);
}

/**
 * Static AjaxRequest that initializes and sends an ajax request
 */
PrimeFaces.ajax.AjaxRequest = function(actionURL,cfg,parameters) {
	var formParams = null;

	if(cfg.partialSubmit) {
		formParams = PrimeFaces.ajax.AjaxUtils.getFormParam(cfg.formClientId);													//form id to mark form as submitted
		formParams = formParams + "&javax.faces.ViewState=" + PrimeFaces.ajax.AjaxUtils.encodeViewState();						//viewstate
		
		//ajaxified component to partially process
		if(cfg.ajaxifiedComponent) {
			var ajaxifiedComponent = cfg.ajaxifiedComponent;
			
			if(ajaxifiedComponent.type === 'checkbox') {
				var arrChkbox = document.getElementsByName(ajaxifiedComponent.name);
				for(var i=0; i < arrChkbox.length; i++) {
					var chkbox = arrChkbox[i];
					if(chkbox.checked) {
						formParams = formParams + "&" + chkbox.name + "=" + chkbox.value;
					}
				}
			} 
			else {
				formParams = formParams + "&" + ajaxifiedComponent.name + "=" + ajaxifiedComponent.value;
			}
		}
								
	}
	else {
		var formParams = jQuery(PrimeFaces.escapeClientId(cfg.formClientId)).serialize();
	}
	
	var requestParams = formParams + "&primefacesAjaxRequest=true";
	if(parameters) {
		requestParams = requestParams + "&" + parameters; 
	}
	
	var xhrOptions = {
			  url: actionURL,
			  type: "POST",
			  cache: false,
			  dataType: "xml",
			  data: requestParams,
			  success: PrimeFaces.ajax.AjaxResponse,
			  complete: function(xhrReq, status) {
					//Process user supplied oncomplete callback
					if(cfg.oncomplete != undefined) {
						cfg.oncomplete(xhrReq, status);
					}
					
					PrimeFaces.ajax.RequestManager.poll();
			   }
			};

	if(cfg.onstart) {
		xhrOptions.beforeSend = cfg.onstart;
	}

	PrimeFaces.ajax.RequestManager.offer(xhrOptions);
}

/**
 * Response Handler for PPR based ajax requests
 */
PrimeFaces.ajax.AjaxResponse = function(responseXML) {
	var xmlDoc = responseXML.documentElement;
	var components = xmlDoc.getElementsByTagName("component");
	
	//Update state on client side
	if(components.length > 0) {
		var state = xmlDoc.getElementsByTagName("state")[0].firstChild.data;
		PrimeFaces.ajax.AjaxUtils.updateState(state);
	}
	
	for(var i=0; i < components.length; i++) {
		var clientId = components[i].childNodes[0].firstChild.data;
		var output = components[i].childNodes[1].firstChild.data;
		
		//Replace any statemarkers with actual state
		var filteredOutput = output.replace(PrimeFaces.ajax.AjaxUtils.STATE_MARKER_EXPR, state);
		
		jQuery(PrimeFaces.escapeClientId(clientId)).replaceWith(filteredOutput);
	}	
}

/**
 * Ajax Request Manager to avoid race conditions
 */
PrimeFaces.ajax.RequestManager = {
		
    requests : new Array(),

    offer : function(req) {
        this.requests.push(req);

        if(this.requests.length == 1) {
        	jQuery.ajax(req);
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