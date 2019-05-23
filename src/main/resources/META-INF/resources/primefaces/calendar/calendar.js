/**
 * PrimeFaces Calendar Widget
 */
PrimeFaces.widget.Calendar = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.jqEl = this.cfg.popup ? this.input : $(this.jqId + '_inline');
        var $this = this;

        //i18n and l7n
        this.configureLocale();

        //events
        this.bindDateSelectListener();
        this.bindViewChangeListener();
        this.bindCloseListener();

        //disabled dates
        this.cfg.beforeShowDay = function(date) {
            if($this.cfg.preShowDay) {
                return $this.cfg.preShowDay(date);
            }
            else if($this.cfg.disabledWeekends) {
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
                if($this.refocusInput) {
                    $this.refocusInput = false;
                    return false;
                }

                //display on top
                setTimeout(function() {
                    $('#ui-datepicker-div').addClass('ui-input-overlay').css('z-index', ++PrimeFaces.zindex);

                    if ($this.cfg.showTodayButton === false) {
                        $(input).datepicker("widget").find(".ui-datepicker-current").hide();
                    }
                    
                    $this.alignPanel();
                }, 1);

                // touch support - prevents keyboard popup
                if(PrimeFaces.env.touch && !$this.input.attr("readonly") && $this.cfg.showOn && $this.cfg.showOn === 'button') {
                    $(this).prop("readonly", true);
                }

                //user callback
                var preShow = $this.cfg.preShow;
                if(preShow) {
                    return $this.cfg.preShow.call($this, input, inst);
                }
            };
            
            PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $('#ui-datepicker-div'), function() {
                $this.alignPanel();
            });
        }

        // touch support - prevents keyboard popup
        if (PrimeFaces.env.touch && !this.input.attr("readonly") && this.cfg.showOn && this.cfg.showOn === 'button') {
            this.cfg.onClose = function(dateText, inst) {
                $(this).attr("readonly", false);
            };
        }

        //Initialize calendar
        if(hasTimePicker) {
            if(this.cfg.timeOnly)
                this.jqEl.timepicker(this.cfg);
            else
                this.jqEl.datetimepicker(this.cfg);
        }
        else {
            this.jqEl.datepicker(this.cfg);
        }

        //extensions
        if(this.cfg.popup && this.cfg.showOn) {
            var triggerButton = this.jqEl.siblings('.ui-datepicker-trigger:button');
            triggerButton.attr('aria-label',PrimeFaces.getAriaLabel('calendar.BUTTON')).attr('aria-haspopup', true).html('').addClass('ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only')
                        .append('<span class="ui-button-icon-left ui-icon ui-icon-calendar"></span><span class="ui-button-text">ui-button</span>');

            var title = this.jqEl.attr('title');
            if(title) {
                triggerButton.attr('title', title);
            }

            if(this.cfg.disabled) {
                triggerButton.addClass('ui-state-disabled');
            }

            var buttonIndex = this.cfg.buttonTabindex||this.jqEl.attr('tabindex');
            if(buttonIndex) {
                triggerButton.attr('tabindex', buttonIndex);
            }

            PrimeFaces.skinButton(triggerButton);
            $('#ui-datepicker-div').addClass('ui-shadow');
            this.jq.addClass('ui-trigger-calendar');
        }

        //mark target and descandants of target as a trigger for a primefaces overlay
        if(this.cfg.popup) {
            this.jq.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
        }

        if (!this.cfg.popup && this.cfg.showTodayButton === false) {
            this.jqEl.parent().find(".ui-datepicker-current").hide();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        if (this.cfg.mask) {
            var maskCfg = {
                placeholder:this.cfg.maskSlotChar||'_',
                autoclear:this.cfg.maskAutoClear
            };
            this.input.mask(this.cfg.mask, maskCfg);
        }
    },

    alignPanel: function () {
        if($.datepicker._lastInput && (this.id + '_input') === $.datepicker._lastInput.id) {
            $('#ui-datepicker-div').css({left: '', top: ''}).position({
                my: 'left top'
                , at: 'left bottom'
                , of: this.input
                , collision: 'flipfit'
            });
        }
    },
    
    refresh: function(cfg) {
        if(cfg.popup && $.datepicker._lastInput && (cfg.id + '_input') === $.datepicker._lastInput.id) {
            $.datepicker._hideDatepicker();
        }

        this._super(cfg);
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
        var $this = this;

        this.cfg.onSelect = function() {
            if($this.cfg.popup) {
                $this.fireDateSelectEvent();

                if($this.cfg.focusOnSelect) {
                    $this.refocusInput = true;
                    $this.jqEl.focus();
                    if(!($this.cfg.showOn && $this.cfg.showOn === 'button')) {
                        $this.jqEl.off('click.calendar').on('click.calendar', function() {
                            $(this).datepicker("show");
                        });
                    }

                    setTimeout(function() {
                        $this.refocusInput = false;
                    }, 10);
                }
            }
            else {
                // GitHub #3760 Pass the config settings to TimePicker
                var settingsObj = {
                       settings : $this.cfg
                };
                var newDate = $this.cfg.timeOnly ? '' : $.datepicker.formatDate($this.cfg.dateFormat, $this.getDate(), $.datepicker._getFormatConfig(settingsObj));
                if($this.cfg.timeFormat) {
                   newDate += ' ' + $this.jqEl.find('.ui_tpicker_time_input')[0].value;
                }

                $this.input.val(newDate);
                $this.fireDateSelectEvent();
            }
        };
    },

    fireDateSelectEvent: function() {
        this.callBehavior('dateSelect');
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
        if(this.hasBehavior('viewChange')) {
            var ext = {
                    params: [
                        {name: this.id + '_month', value: month},
                        {name: this.id + '_year', value: year}
                    ]
            };

            this.callBehavior('viewChange', ext);
        }
    },

    bindCloseListener: function() {
        if(this.hasBehavior('close')) {
            var $this = this;
            this.cfg.onClose = function() {
                $this.fireCloseEvent();
            };
        }
    },

    fireCloseEvent: function() {
        this.callBehavior('close');
    },

    configureTimePicker: function() {
        var pattern = this.cfg.dateFormat,
        timeSeparatorIndex = pattern.toLowerCase().indexOf('h');

        this.cfg.dateFormat = pattern.substring(0, timeSeparatorIndex - 1);
        this.cfg.timeFormat = pattern.substring(timeSeparatorIndex, pattern.length);

        //ampm
        if(this.cfg.timeFormat.indexOf('TT') != -1) {
            this.cfg.ampm = true;
        }
        
        // GitHub #4366 pass date and time settings for min/max date
        var timeSettings = {
                settings : this.cfg
        };
        var parseSettings = $.datepicker._getFormatConfig(timeSettings);

        //restraints
        if(this.cfg.minDate) {
            this.cfg.minDate = $.datepicker.parseDateTime(this.cfg.dateFormat, this.cfg.timeFormat, this.cfg.minDate, parseSettings, this.cfg);
        }

        if(this.cfg.maxDate) {
            this.cfg.maxDate = $.datepicker.parseDateTime(this.cfg.dateFormat, this.cfg.timeFormat, this.cfg.maxDate, parseSettings, this.cfg);
        }

        if(!this.cfg.showButtonPanel) {
            this.cfg.showButtonPanel = false;
        }

        if(this.cfg.controlType == 'custom' && this.cfg.timeControlObject) {
            this.cfg.controlType = this.cfg.timeControlObject;
        }

        if(this.cfg.showHour) {
            this.cfg.showHour = (this.cfg.showHour == "true") ? true : false;
        }

        if(this.cfg.showMinute) {
            this.cfg.showMinute = (this.cfg.showMinute == "true") ? true : false;
        }

        if(this.cfg.showSecond) {
            this.cfg.showSecond = (this.cfg.showSecond == "true") ? true : false;
        }

        if(this.cfg.showMillisec) {
            this.cfg.showMillisec = (this.cfg.showMillisec == "true") ? true : false;
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
    }

});
