PrimeFaces.ajax.Queue = {

	delayHandler : null,

	requests : new Array(),

	offer : function(request) {
		if (this.delayHandler) {
			clearTimeout(this.delayHandler);
			this.delayHandler = null;
		}

		if (request.delay && request.delay > 0) {
			var $this = this;

			this.delayHandler = setTimeout(function() {
				$this.requests.push(request);

				if($this.requests.length == 1) {
					PrimeFaces.ajax.Request.send(request);
				}
			}, request.delay);

		} else {

			this.requests.push(request);

			if(this.requests.length == 1) {
				PrimeFaces.ajax.Request.send(request);
			}
		}
	},

	poll : function() {
		if(this.isEmpty()) {
			return null;
		}

		var processed = this.requests.shift(),
		next = this.peek();

		if(next != null) {
			PrimeFaces.ajax.Request.send(next);
		}

		return processed;
	},

	peek : function() {
		if(this.isEmpty()) {
			return null;
		}

		return this.requests[0];
	},

	isEmpty : function() {
		return this.requests.length == 0;
	}
};


PrimeFaces.ajax.Request = {

	handleRequest : function(cfg, ext) {
	    cfg.ext = ext;

	    if(cfg.async) {
	        return PrimeFaces.ajax.Request.send(cfg);
	    }
	    else {
	        return PrimeFaces.ajax.Queue.offer(cfg);
	    }
	},

    send: function(cfg) {
        PrimeFaces.debug('Initiating ajax request.');
        
        PrimeFaces.customFocus = false;

        var global = (cfg.global === true || cfg.global === undefined) ? true : false,
        form = null,
        sourceId = null;

        if(cfg.onstart) {
            var retVal = cfg.onstart.call(this, cfg);
            if(retVal === false) {
                PrimeFaces.debug('Ajax request cancelled by onstart callback.');

                //remove from queue
                if(!cfg.async) {
                    PrimeFaces.ajax.Queue.poll();
                }
                
                return false;  //cancel request
            }
        }
        
        if(global) {
            $(document).trigger('pfAjaxStart');
        }
        
        //source can be a client id or an element defined by this keyword
        if(typeof(cfg.source) == 'string') {
            sourceId = cfg.source;
        } else {
            sourceId = $(cfg.source).attr('id');
        }

        if(cfg.formId) {
            form = $(PrimeFaces.escapeClientId(cfg.formId));                         //Explicit form is defined
        }
        else {
            form = $(PrimeFaces.escapeClientId(sourceId)).parents('form:first');     //look for a parent of source

            //source has no parent form so use first form in document
            if(form.length == 0) {
                form = $('form').eq(0);
            }
        }

        PrimeFaces.debug('Form to post ' + form.attr('id') + '.');

        var postURL = form.attr('action'),
        encodedURLfield = form.children("input[name='javax.faces.encodedURL']"),
        postParams = [];

        //portlet support
        var pFormsSelector = null;
        if(encodedURLfield.length > 0) {
            pFormsSelector = 'form[action="' + postURL + '"]';
            postURL = encodedURLfield.val();
        }

        PrimeFaces.debug('URL to post ' + postURL + '.');

        //partial ajax
        postParams.push({
            name:PrimeFaces.PARTIAL_REQUEST_PARAM, 
            value:true
        });

        //source
        postParams.push({
            name:PrimeFaces.PARTIAL_SOURCE_PARAM, 
            value:sourceId
        });
        
        //resetValues
        if (cfg.resetValues) {
            postParams.push({
                name:PrimeFaces.RESET_VALUES_PARAM, 
                value:true
            });
        }

        //ignoreAutoUpdate
        if (cfg.ignoreAutoUpdate) {
            postParams.push({
                name:PrimeFaces.IGNORE_AUTO_UPDATE_PARAM, 
                value:true
            });
        }
        
        //process
        var processArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'process');
        if(cfg.fragmentId) {
            processArray.push(cfg.fragmentId);
        }
        var processIds = processArray.length > 0 ? processArray.join(' ') : '@all';
        if (processIds != '@none') {
            postParams.push({
                name:PrimeFaces.PARTIAL_PROCESS_PARAM, 
                value:processIds
            });
        }

        //update
        var updateArray = PrimeFaces.ajax.Request.resolveComponentsForAjaxCall(cfg, 'update');
        if(cfg.fragmentId && cfg.fragmentUpdate) {
            updateArray.push(cfg.fragmentId);
        }
        if(updateArray.length > 0) {
            postParams.push({
                name:PrimeFaces.PARTIAL_UPDATE_PARAM, 
                value:updateArray.join(' ')
            });
        }

        //behavior event
        if(cfg.event) {
            postParams.push({
                name:PrimeFaces.BEHAVIOR_EVENT_PARAM, 
                value:cfg.event
            });

            var domEvent = cfg.event;

            if(cfg.event == 'valueChange')
                domEvent = 'change';
            else if(cfg.event == 'action')
                domEvent = 'click';

            postParams.push({
                name:PrimeFaces.PARTIAL_EVENT_PARAM, 
                value:domEvent
            });
        } 
        else {
            postParams.push({
                name:sourceId, 
                value:sourceId
            });
        }

        //params
        if(cfg.params) {
            $.merge(postParams, cfg.params);
        }
        if(cfg.ext && cfg.ext.params) {
            $.merge(postParams, cfg.ext.params);
        }

        /**
         * Only add params of process components and their children 
         * if partial submit is enabled and there are components to process partially
         */
        if(cfg.partialSubmit && processIds.indexOf('@all') == -1) {
        	var formProcessed = false;

            if(processIds.indexOf('@none') == -1) {
            	for (var i = 0; i < processArray.length; i++) {
                    var jqProcess = $(PrimeFaces.escapeClientId(processArray[i]));
                    var componentPostParams = null;

                    if(jqProcess.is('form')) {
                        componentPostParams = jqProcess.serializeArray();
                        formProcessed = true;
                    }
                    else if(jqProcess.is(':input')) {
                        componentPostParams = jqProcess.serializeArray();
                    }
                    else {
                        componentPostParams = jqProcess.find(':input').serializeArray();
                    }

                    $.merge(postParams, componentPostParams);
                }
            }

            //add form state if necessary
            if(!formProcessed) {
                postParams.push({
                    name:PrimeFaces.VIEW_STATE, 
                    value:form.children("input[name='" + PrimeFaces.VIEW_STATE + "']").val()
                });
                
                var clientWindowInput = form.children("input[name='" + PrimeFaces.CLIENT_WINDOW + "']");
                if (clientWindowInput.length > 0) {
                    postParams.push({ name:PrimeFaces.CLIENT_WINDOW, value:clientWindowInput.val() });
                }
            }

        }
        else {
            $.merge(postParams, form.serializeArray());
        }

        //serialize
        var postData = $.param(postParams);

        PrimeFaces.debug('Post Data:' + postData);

        var xhrOptions = {
            url : postURL,
            type : "POST",
            cache : false,
            dataType : "xml",
            data : postData,
            portletForms: pFormsSelector,
            source: cfg.source,
            global: false,
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Faces-Request', 'partial/ajax');
                
                if(global) {
                    $(document).trigger('pfAjaxSend', [xhr, this]);
                }       
            },
            error: function(xhr, status, errorThrown) {                    
                if(cfg.onerror) {
                    cfg.onerror.call(this, [xhr, status, errorThrown]);
                }
                
                if(global) {
                    $(document).trigger('pfAjaxError', xhr, this, errorThrown);
                }

                PrimeFaces.error('Request return with error:' + status + '.');
            },
            success: function(data, status, xhr) {
                PrimeFaces.debug('Response received succesfully.');
                
                var parsed;

                //call user callback
                if(cfg.onsuccess) {
                    parsed = cfg.onsuccess.call(this, data, status, xhr);
                }
                
                //extension callback that might parse response
                if(cfg.ext && cfg.ext.onsuccess && !parsed) {
                    parsed = cfg.ext.onsuccess.call(this, data, status, xhr); 
                }
                
                if(global) {
                    $(document).trigger('pfAjaxSuccess', [xhr, this]);
                }
                
                //do not execute default handler as response already has been parsed
                if(parsed) {
                    return;
                } 
                else {
                    PrimeFaces.ajax.Response.handleResponse(data, status, xhr);
                }
                
                PrimeFaces.debug('DOM is updated.');
            },
            complete: function(xhr, status) {
                if(cfg.oncomplete) {
                    cfg.oncomplete.call(this, xhr, status, xhr.pfArgs);
                }

                if(cfg.ext && cfg.ext.oncomplete) {
                    cfg.ext.oncomplete.call(this, xhr, status, xhr.pfArgs);
                }
                
                if(global) {
                    $(document).trigger('pfAjaxComplete', [xhr, this]);
                }
                
                PrimeFaces.debug('Response completed.');

                if(!cfg.async) {
                    PrimeFaces.ajax.Queue.poll();
                }
            }
        };
        
        $.ajax(xhrOptions);
    },

    /**
     * Type: update/process
     */
    resolveComponentsForAjaxCall: function(cfg, type) {

        var expressions = '';
        
        if (cfg[type]) {
            expressions += cfg[type];
        }

        if (cfg.ext && cfg.ext[type]) {
            expressions += " " + cfg.ext[type];
        }
        
        return PrimeFaces.Expressions.resolveComponents(expressions);
    }
}