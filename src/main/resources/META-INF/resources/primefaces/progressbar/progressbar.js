PrimeFaces.widget.ProgressBar = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	
	jQuery(this.jqId).progressbar(this.cfg);
}

PrimeFaces.widget.ProgressBar.prototype.setValue = function(value) {
	jQuery(this.jqId).progressbar('value', value);
}

PrimeFaces.widget.ProgressBar.prototype.getValue  = function() {
	return jQuery(this.jqId).progressbar('value');
}

PrimeFaces.widget.ProgressBar.prototype.start = function() {
	var p = this;
	
	if(this.cfg.ajax) {
		
		this.progressPoll = setInterval(function() {
			var params = {};
			params[PrimeFaces.PARTIAL_PROCESS_PARAM] = p.id;
			
			PrimeFaces.ajax.AjaxRequest(p.cfg.url, {
				formId:p.cfg.formId,
				global:false,
				async:true,
				oncomplete:function(xhr, status, args) {
						var value = args[p.id + '_value'];
						p.setValue(value);
						
						//trigger close listener
						if(value === 100) {
							p.fireCloseEvent();
						}
					}
				},
				params);
		}, this.cfg.interval);	
	}
}

PrimeFaces.widget.ProgressBar.prototype.fireCloseEvent = function() {
	clearInterval(this.progressPoll);
	
	var params = {};
	params[this.id + '_complete'] = true;
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
	
	if(this.cfg.onCompleteUpdate) {
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.onCompleteUpdate;
	}

	PrimeFaces.ajax.AjaxRequest(this.cfg.url, {formId:this.cfg.formId, global:false, async:true}, params);
}

PrimeFaces.widget.ProgressBar.prototype.cancel = function() {
	clearInterval(this.progressPoll);
	
	var params = {};
	params[this.id + '_cancel'] = true;
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
	
	if(this.cfg.onCancelUpdate) {
		params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.onCancelUpdate;
	}

	var p = this;
	PrimeFaces.ajax.AjaxRequest(this.cfg.url, {
			formId:this.cfg.formId, 
			global:false,
			async:true,
			oncomplete:function(xhr, status, args) {
					p.setValue(0);
				}
			}, params);
}