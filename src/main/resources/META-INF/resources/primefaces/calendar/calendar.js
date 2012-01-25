/**
 * PrimeFaces Calendar Widget
 */
PrimeFaces.widget.Calendar = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jq);
    this.input = $(this.jqId + '_input');
    this.jqEl = this.cfg.popup ? this.input : $(this.jqId + '_inline');
    var _self = this;
    
    //i18n and l7n
    this.configureLocale();

    //Select listener
    this.bindDateSelectListener();
    
    //weekends
    if(this.cfg.disabledWeekends) {
        this.cfg.beforeShowDay = $.datepicker.noWeekends;
    }

    //Setup timepicker
    var hasTimePicker = this.hasTimePicker();
    if(hasTimePicker) {
        this.configureTimePicker();
    }
    
    //Client behaviors, input skinning and z-index
    if(this.cfg.popup) {
        PrimeFaces.skinInput(this.jqEl);
        
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jqEl, this.cfg.behaviors);
        }

        this.cfg.beforeShow = function() {
            setTimeout(function() {
                $('#ui-datepicker-div').css('z-index', ++PrimeFaces.zindex);
            }, 250);
        };
    }

    //image title
    this.cfg.buttonText = this.jqEl.attr('title') || '';

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

    //button title
    if(this.cfg.popup && this.cfg.showOn) {
        var triggerButton = this.jqEl.siblings('.ui-datepicker-trigger:button');
        triggerButton.attr('title', this.cfg.buttonText);
        
        PrimeFaces.skinButton(triggerButton);
    }
    
    //Hide overlay on resize
    if(this.cfg.popup) {
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            _self.jqEl.datepicker('hide');
        });
    }
    
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

    this.cfg.onSelect = function() {
        if(_self.cfg.popup) {
            _self.fireDateSelectEvent();
        }
        else {
            var newDate = $.datepicker.formatDate(_self.cfg.dateFormat, _self.getDate()),
            oldDate = _self.input.val();

            if(oldDate == newDate) {
                _self.setDate(null);
                _self.input.val('');
            }
            else {
                _self.input.val(newDate);
                _self.fireDateSelectEvent();
            }
        }
    };
}

PrimeFaces.widget.Calendar.prototype.fireDateSelectEvent = function() {
    if(this.cfg.behaviors) {
        var dateSelectBehavior = this.cfg.behaviors['dateSelect'];

        if(dateSelectBehavior) {
            dateSelectBehavior.call(this);
        }
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