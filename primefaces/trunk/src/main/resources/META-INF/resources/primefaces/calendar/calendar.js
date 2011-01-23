PrimeFaces.widget.Calendar = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jqEl = this.cfg.popup ? this.jqId + '_input' : this.jqId + '_inline';
    this.jq = jQuery(this.jqEl);
	
    this.configureOnSelectHandler();
    this.configureLocale();
	
    if(!this.cfg.disabled) {
        this.jq.datepicker(this.cfg);
    }

    if(this.cfg.popup) {
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
        }

        //Visuals
        if(this.cfg.popup) {
            PrimeFaces.skinInput(this.jq);
        }
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
    this.jq.datepicker('setDate', date);
}

PrimeFaces.widget.Calendar.prototype.getDate = function() {
    return this.jq.datepicker('getDate');
}

PrimeFaces.widget.Calendar.prototype.enable = function() {
    this.jq.datepicker('enable');
}

PrimeFaces.widget.Calendar.prototype.disable = function() {
    this.jq.datepicker('disable');
}