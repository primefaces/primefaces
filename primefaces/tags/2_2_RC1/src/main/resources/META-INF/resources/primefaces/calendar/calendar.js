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

    if(this.cfg.behaviors && this.cfg.popup) {
        PrimeFaces.attachBehaviors(jQuery(this.jq), this.cfg.behaviors);
    }
}

PrimeFaces.widget.Calendar.prototype.configureLocale = function() {
    jQuery.datepicker.setDefaults(jQuery.datepicker.regional['']);
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
            var options = {
                source: _self.id,
                process: _self.id,
                formId: _self.cfg.formId
            };

            if(_self.cfg.onSelectUpdate) {
                options.update = _self.cfg.onSelectUpdate;
            }
	
            PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options);
        }

    }
}

PrimeFaces.widget.Calendar.prototype.setDate = function(date) {
    jQuery(this.jq).datepicker('setDate', date);
}

PrimeFaces.widget.Calendar.prototype.getDate = function() {
    return jQuery(this.jq).datepicker('getDate');
}

PrimeFaces.widget.Calendar.prototype.enable = function() {
    jQuery(this.jq).datepicker('enable');
}

PrimeFaces.widget.Calendar.prototype.disable = function() {
    jQuery(this.jq).datepicker('disable');
}