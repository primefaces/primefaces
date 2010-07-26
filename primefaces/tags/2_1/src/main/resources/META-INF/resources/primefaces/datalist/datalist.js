PrimeFaces.widget.DataList = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	
	if(this.cfg.paginator) {
		this.setupPaginator();
	}
}

PrimeFaces.widget.DataList.prototype.setupPaginator = function() {
	this.cfg.paginator.subscribe('changeRequest', this.handlePagination, null, this);
	this.cfg.paginator.render();
}

PrimeFaces.widget.DataList.prototype.handlePagination = function(newState) {
	var params = {};
	params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.id;
	params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
	params[this.id + "_ajaxPaging"] = true;
	params[this.id + "_first"] = newState.recordOffset;
	params[this.id + "_rows"] = newState.rowsPerPage;
	params[this.id + "_page"] = newState.page;

	var requestParams = jQuery(PrimeFaces.escapeClientId(this.cfg.formId)).serialize();
	requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params);
	
	var dl = this;
	jQuery.ajax({url: this.cfg.url,
			  	type: "POST",
			  	cache: false,
			  	dataType: "xml",
			  	data: requestParams,
			  	success: function(responseXML) {
					var xmlDoc = responseXML.documentElement,
					list = xmlDoc.getElementsByTagName("list")[0].firstChild.data,
					state = xmlDoc.getElementsByTagName("state")[0].firstChild.data,
					listId = PrimeFaces.escapeClientId(dl.id + "_list");
					
					PrimeFaces.ajax.AjaxUtils.updateState(state);
					
					if(dl.cfg.effect) {
						jQuery(listId).fadeOut(dl.cfg.effectSpeed, function() {
							jQuery(listId).replaceWith(list);
							jQuery(listId).fadeIn(dl.cfg.effectSpeed);
						});
					} else {
						jQuery(listId).replaceWith(list);
					}
					
					dl.cfg.paginator.setState(newState);
				}
			});
}