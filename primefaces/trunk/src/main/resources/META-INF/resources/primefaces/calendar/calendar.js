/**
 * PrimeFaces Calendar Widget
 */
PrimeFaces.widget.Calendar = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jq);
    this.jqElId = this.cfg.popup ? this.jqId + '_input' : this.jqId + '_inline';
    this.jqEl = $(this.jqElId);
    this.cfg.formId = this.jq.parents('form:first').attr('id');
    
    //i18n and l7n
    this.configureLocale();

    //image title
    this.cfg.buttonText = this.jq.attr('title') || '';

    //Override locale pattern with user pattern
    if(this.cfg.pattern) {
        this.cfg.dateFormat = this.cfg.pattern;
    }

    //Select listener
    this.bindDateSelectListener();

    //Form field to use in inline mode
    if(!this.cfg.popup) {
        this.cfg.altField = $(this.jqId + '_input');
    }
    
    //weekends
    if(this.cfg.disabledWeekends) {
        this.cfg.beforeShowDay = $.datepicker.noWeekends;
    }

    //Setup timepicker
    var hasTimePicker = this.hasTimePicker();
    if(hasTimePicker) {
        this.configureTimePicker();
    }

	//Initialize calendar
    if(!this.cfg.disabled) {
        if(hasTimePicker) {
            if(this.cfg.timeOnly)
                this.jqEl.timepicker(this.cfg);
            else
                this.jqEl.datetimepicker(this.cfg);
        }
        else {
            this.jqEl.datepicker(this.cfg);
        }
    }

    //Client behaviors and input skinning
    if(this.cfg.popup) {
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jqEl, this.cfg.behaviors);
        }

        //Visuals
        if(this.cfg.popup && this.cfg.theme != false) {
            PrimeFaces.skinInput(this.jqEl);
        }
    }
    
    //button title
    this.jqEl.siblings('.ui-datepicker-trigger:button').attr('title', this.cfg.buttonText);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Calendar, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Calendar.prototype.configureLocale = function() {
    var localeSettings = PrimeFaces.locales[this.cfg.locale];
	
    if(localeSettings) {
        for(var setting in localeSettings) {
            this.cfg[setting] = localeSettings[setting];
        }
    }
}

PrimeFaces.widget.Calendar.prototype.bindDateSelectListener = function() {
    var _self = this;

    if(this.cfg.behaviors) {
        this.cfg.onSelect = function(dateText, input) {
            var dateSelectBehavior = _self.cfg.behaviors['dateSelect'];

            if(dateSelectBehavior) {
                dateSelectBehavior.call(_self);
            }
        };
    }
    
}

PrimeFaces.widget.Calendar.prototype.configureTimePicker = function() {
    var pattern = this.cfg.dateFormat,
    timeSeparatorIndex = pattern.indexOf('h');
    
    this.cfg.dateFormat = pattern.substring(0, timeSeparatorIndex - 1);
    this.cfg.timeFormat = pattern.substring(timeSeparatorIndex, pattern.length);

    //second
    if(this.cfg.timeFormat.indexOf('ss') != -1) {
        this.cfg.showSecond = true;
    }

    //ampm
    if(this.cfg.timeFormat.indexOf('TT') != -1) {
        this.cfg.ampm = true;
    }
}

PrimeFaces.widget.Calendar.prototype.hasTimePicker = function() {
    return this.cfg.dateFormat.indexOf('h') != -1;
}

PrimeFaces.widget.Calendar.prototype.setDate = function(date) {
    this.jqEl.datetimepicker('setDate', date);
}

PrimeFaces.widget.Calendar.prototype.getDate = function() {
    return this.jqEl.datetimepicker('getDate');
}

PrimeFaces.widget.Calendar.prototype.enable = function() {
    this.jqEl.datetimepicker('enable');
}

PrimeFaces.widget.Calendar.prototype.disable = function() {
    this.jqEl.datetimepicker('disable');
}