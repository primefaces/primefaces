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

        //events
        this.bindDateSelectListener();
        this.bindViewChangeListener();

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

            this.cfg.beforeShow = function(input, inst) {
                //display on top
                setTimeout(function() {
                    $('#ui-datepicker-div').css('z-index', ++PrimeFaces.zindex);
                }, 1);
                
                // touch support - prevents keyboard popup
                if(PrimeFaces.env.touch && !_self.input.attr("readonly") && _self.cfg.showOn && _self.cfg.showOn === 'button') {
                    $(this).prop("readonly", true);
                }
                
                //user callback
                var preShow = _self.cfg.preShow;
                if(preShow) {
                    return _self.cfg.preShow.call(_self, input, inst);
                }
            };
        }

        // touch support - prevents keyboard popup
        if (PrimeFaces.env.touch && !this.input.attr("readonly") && this.cfg.showOn && this.cfg.showOn === 'button') {
            this.cfg.onClose = function(dateText, inst) {
                $(this).attr("readonly", false);
            };
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

        //extensions
        if(this.cfg.popup && this.cfg.showOn) {
            var triggerButton = this.jqEl.siblings('.ui-datepicker-trigger:button');
            triggerButton.html('').addClass('ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only')
                        .append('<span class="ui-button-icon-left ui-icon ui-icon-calendar"></span><span class="ui-button-text">ui-button</span>');

            var title = this.jqEl.attr('title');
            if(title) {
                triggerButton.attr('title', title);
            }

            PrimeFaces.skinButton(triggerButton);
            $('#ui-datepicker-div').addClass('ui-shadow');
        }

        //mark target and descandants of target as a trigger for a primefaces overlay
        if(this.cfg.popup) {
            this.jq.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        if (this.cfg.mask) {
            var maskCfg = {placeholder:this.cfg.maskSlotChar||'_'};
            this.input.mask(this.cfg.mask, maskCfg);
        }
    },

    refresh: function(cfg) {
        if(cfg.popup && $.datepicker._lastInput && (cfg.id + '_input') === $.datepicker._lastInput.id) {
            $.datepicker._hideDatepicker();
        }

        this.init(cfg);
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

    bindViewChangeListener: function() {
        if(this.hasBehavior('viewChange')) {
            var $this = this;
            this.cfg.onChangeMonthYear = function(year, month) {
                $this.fireViewChangeEvent(year, month);
            };
        }
    },

    fireViewChangeEvent: function(year, month) {
        if(this.cfg.behaviors) {
            var viewChangeBehavior = this.cfg.behaviors['viewChange'];

            if(viewChangeBehavior) {
                var ext = {
                        params: [
                            {name: this.id + '_month', value: month},
                            {name: this.id + '_year', value: year}
                        ]
                };

                viewChangeBehavior.call(this, ext);
            }
        }
    },

    configureTimePicker: function() {
        var pattern = this.cfg.dateFormat,
        timeSeparatorIndex = pattern.toLowerCase().indexOf('h');

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

        //restrains
        if(this.cfg.minDate) {
            this.cfg.minDate = $.datepicker.parseDateTime(this.cfg.dateFormat, this.cfg.timeFormat, this.cfg.minDate, {}, {});
        }

        if(this.cfg.maxDate) {
            this.cfg.maxDate = $.datepicker.parseDateTime(this.cfg.dateFormat, this.cfg.timeFormat, this.cfg.maxDate, {}, {});
        }

        if(!this.cfg.showButtonPanel) {
            this.cfg.showButtonPanel = false;
        }
    },

    hasTimePicker: function() {
        return this.cfg.dateFormat.toLowerCase().indexOf('h') != -1;
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
    },

    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] !== undefined;
        }

        return false;
    }

});