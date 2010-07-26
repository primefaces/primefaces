PrimeFaces.widget.AutoComplete = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.jq = this.jqId + '_input';
	this.jqh = this.jqId + '_hinput';
	var _self = this;
	
	this.setupDataSource();
		
	jQuery(this.jq).autocomplete(this.cfg);
	
	jQuery(this.jq).bind('autocompleteselect', {ac: this}, this.onSelect);
	
	if(this.cfg.forceSelection) {
		this.setupForceSelection();
	}
	
	if(this.cfg.pojo) {
		jQuery(this.jq).keyup(function() {
			jQuery(_self.jqh).val(jQuery(this).val());
		});
	}
}

PrimeFaces.widget.AutoComplete.prototype.setupDataSource = function() {
	var _self = this;
	
	this.cfg.source = function(request, response) {
		//start event
		if(_self.cfg.onstart) {
			_self.cfg.onstart.call(_self, request);
		}
		
		var params = {};
		params['javax.faces.ViewState'] = PrimeFaces.ajax.AjaxUtils.encodeViewState();
		params[_self.id + '_query'] = request.term;
		params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = _self.id;
		params[PrimeFaces.PARTIAL_SOURCE_PARAM] = _self.id;
				
		jQuery.ajax({
			url: _self.cfg.url,
			dataType: "json",
			data: params,
			type : "POST",
			cache : false,
			success: function(data) {
			
				//complete event
				if(_self.cfg.oncomplete) {
					_self.cfg.oncomplete.call(_self, data);
				}
			
				var results = data.results;
				
				//max results
				if(_self.cfg.maxResults < results.length) {
					results = results.slice(0, _self.cfg.maxResults);
				}
				
				//force selection
				if(_self.cfg.forceSelection) {
					_self.cachedResults = results;
				}
				
				response(results);
			}
		});
	};
}

PrimeFaces.widget.AutoComplete.prototype.onSelect = function(event, ui) {
	var _self = event.data.ac;
	
	if(_self.cfg.pojo)
		jQuery(_self.jqh).val(ui.item.data);
	else
		jQuery(_self.jq).val(ui.item.label);
	
	//Fire instant selection event
	if(_self.cfg.ajaxSelect) {
		var params = {};
		params[_self.id + "_ajaxSelect"] = true;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = _self.id;
		
		if(_self.cfg.onSelectUpdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = _self.cfg.onSelectUpdate;
		}
		
		PrimeFaces.ajax.AjaxRequest(_self.cfg.url,{formId:_self.cfg.formId}, params);
	}
}

PrimeFaces.widget.AutoComplete.prototype.setupForceSelection = function() {
	var _self = this;
	
	jQuery(this.jq).blur(function() {
		var value = jQuery(this).val(),
		valid = false;
		
		if(_self.cachedResults) {
			for(var i = 0; i < _self.cachedResults.length; i++) {
				if(_self.cachedResults[i].label == value) {
					valid = true;
					break;
				}
			}
		}
		
		if(!valid) {
			jQuery(this).val('');
		}
	});
}