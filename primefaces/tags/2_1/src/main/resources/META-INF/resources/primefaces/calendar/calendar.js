PrimeFaces.widget.Calendar = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.jq = this.cfg.popup ? this.jqId + '_input' : this.jqId + '_inline';
	
	this.configureOnSelectHandler();
	this.configureLocale();
	
	if(!this.cfg.disabled) {
		jQuery(this.jq).datepicker(this.cfg);
	}
}

PrimeFaces.widget.Calendar.prototype.configureLocale = function() {
	jQuery.datepicker.setDefaults(jQuery.datepicker.regional['']);
	var regional = jQuery.datepicker.regional;
	var localeSettings = jQuery.datepicker.regional[this.cfg.locale];
	
	if(localeSettings) {
		for(var setting in localeSettings) {
			this.cfg[setting] = localeSettings[setting];
		}
	}
}

PrimeFaces.widget.Calendar.prototype.configureOnSelectHandler = function() {
	var _self = this;
	
	this.cfg.onSelect = function(dateText, input) {
		
		if(!_self.cfg.popup) {
			jQuery(_self.jqId + '_input').val(dateText);
		}
		
		if(_self.cfg.hasSelectListener) {
			var params = {};
			params[PrimeFaces.PARTIAL_PROCESS_PARAM] = _self.id;
			
			if(_self.cfg.onSelectUpdate) {
				params[PrimeFaces.PARTIAL_UPDATE_PARAM] = _self.cfg.onSelectUpdate;
			}
			
			PrimeFaces.ajax.AjaxRequest(_self.cfg.url, {
					formId:_self.cfg.formId,
					global:true
				},
				params);
		}
	}
}