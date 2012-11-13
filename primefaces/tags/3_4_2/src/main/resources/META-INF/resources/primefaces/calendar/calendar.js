/**
 * PrimeFaces Calendar Widget
 */
PrimeFaces.widget.Calendar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.input = $(this.jqId + '_input');
        this.jqEl = this.cfg.popup ? this.input : $(this.jqId + '_inline');
        var _self = this;

        //i18n and l7n
        this.configureLocale();

        //Select listener
        this.bindDateSelectListener();

        //disabled dates
        this.cfg.beforeShowDay = function(date) { 
            if(_self.cfg.preShowDay) {
                return _self.cfg.preShowDay(date);
            }
            else if(_self.cfg.disabledWeekends) {
                return $.datepicker.noWeekends(date);
            }
            else {
                return [true,''];
            }
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
        
        //readonly input
        if(this.cfg.popup && this.cfg.readonlyInput) {
            this.input.focus(function(e) {
                e.preventDefault();
                $(this).blur();
            });
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
        
        //mark target and descandants of target as a trigger for a primefaces overlay
        if(this.cfg.popup) {
            this.jq.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
        }
        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
    
    configureLocale: function() {
        var localeSettings = PrimeFaces.locales[this.cfg.locale];

        if(localeSettings) {
            for(var setting in localeSettings) {
                this.cfg[setting] = localeSettings[setting];
            }
        }
    },
    
    bindDateSelectListener: function() {
        var _self = this;

        this.cfg.onSelect = function() {
            if(_self.cfg.popup) {
                _self.fireDateSelectEvent();
            }
            else {
                var newDate = $.datepicker.formatDate(_self.cfg.dateFormat, _self.getDate());

                _self.input.val(newDate);
                _self.fireDateSelectEvent();
            }
        };
    },
    
    fireDateSelectEvent: function() {
        if(this.cfg.behaviors) {
            var dateSelectBehavior = this.cfg.behaviors['dateSelect'];

            if(dateSelectBehavior) {
                dateSelectBehavior.call(this);
            }
        }
    },
    
    configureTimePicker: function() {
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
    },
    
    hasTimePicker: function() {
        return this.cfg.dateFormat.indexOf('h') != -1;
    },
    
    setDate: function(date) {
        this.jqEl.datetimepicker('setDate', date);
    },
    
    getDate: function() {
        return this.jqEl.datetimepicker('getDate');
    },
    
    enable: function() {
        this.jqEl.datetimepicker('enable');
    },
    
    disable: function() {
        this.jqEl.datetimepicker('disable');
    }
    
});
