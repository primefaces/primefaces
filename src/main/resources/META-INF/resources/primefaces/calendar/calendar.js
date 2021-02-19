/**
 * __PrimeFaces Calendar Widget__
 *
 * __Deprecated__: Use the {@link DatePicker|p:datePicker} component instead.
 *
 * Calendar is an input component used to select a date featuring display modes, paging, localization, ajax selection
 * and more.
 *
 * To interact with the calendar, use the `timepicker` or `datetimepicker` JQuery plugin, for example:
 *
 * ```javascript
 * PF("calendarWidget").jqEl.datetimepicker("getDate");
 * PF("calendarWidget").jqEl.datetimepicker("setDate", new Date());
 * ```
 *
 * @typedef {"focus" | "button" | "both"} PrimeFaces.widget.Calendar.ShowOnType Client-side event to display the
 * calendar. `focus` is when the input field receives focus. `popup` is when the popup button is clicked. `both` is
 * both `focus` and `popup`.
 *
 * @typedef PrimeFaces.widget.Calendar.PreShowCallback Callback invoked before the calendar is opened.
 * @this {PrimeFaces.widget.Calendar} PrimeFaces.widget.Calendar.PreShowCallback
 * @param {JQuery} PrimeFaces.widget.Calendar.PreShowCallback.input Input element for the date.
 * @param {JQueryUITimepickerAddon.Timepicker} PrimeFaces.widget.Calendar.PreShowCallback.instance Current time picker
 * instance controlling the calendar. `false` to prevent the time picker from being shown.
 * @return {Partial<JQueryUI.DatepickerOptions> | boolean | undefined} PrimeFaces.widget.Calendar.PreShowCallback A new
 * set of options for the time picker.
 *
 * @typedef PrimeFaces.widget.Calendar.PreShowDayCallback Callback invoked before a day is shown.
 * @this {Window} PrimeFaces.widget.Calendar.PreShowDayCallback
 * @param {Date} PrimeFaces.widget.Calendar.PreShowDayCallback.date The current date of the calendar.
 * @return {[boolean, string] | [boolean, string, string]} PrimeFaces.widget.Calendar.PreShowDayCallback Two to three
 * values indicating:
 * 1. true/false indicating whether or not this date is selectable
 * 1. a CSS class name to add to the date's cell or "" for the default presentation
 * 1. an optional popup tooltip for this date
 *
 * @prop {JQuery} input DOM element of the plain-text input field for the date and/or time.
 * @prop {JQuery} jqEl The DOM element on which the JQuery plugin `datepicker` or `datetimepicker` was initialized. You
 * can use this element to interact with the date picker.
 * @prop {boolean} refocusInput Whether the input needs to be refocused.
 *
 * @interface {PrimeFaces.widget.CalendarCfg} cfg The configuration for the {@link  Calendar| Calendar widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {string} cfg.buttonTabindex Position of the button in the tabbing order.
 * @prop {JQueryUITimepickerAddon.ControlType | "custom"} cfg.controlType How the user selects a time (hour / minute /
 * second). When set to `custom`, the `timeControlObject` must be set.
 * @prop {string} cfg.dateFormat Date format pattern for localization
 * @prop {boolean} cfg.disabled Disables the calendar when set to true.
 * @prop {boolean} cfg.disabledWeekends Disables weekend columns.
 * @prop {string} cfg.duration Duration of the effect.
 * @prop {boolean} cfg.focusOnSelect If enabled, the input is focused again after selecting a date. Default is false.
 * @prop {number} cfg.hour Default for hour selection, if no date is given. Default is 0.
 * @prop {number} cfg.hourMax Maximum boundary for hour selection.
 * @prop {number} cfg.hourMin Minimum boundary for hour selection.
 * @prop {string} cfg.locale Locale to be used for labels and conversion.
 * @prop {string} cfg.mask Applies a mask using the pattern.
 * @prop {boolean} cfg.maskAutoClear Clears the field on blur when incomplete input is entered
 * @prop {string} cfg.maskSlotChar Placeholder in mask template.
 * @prop {string} cfg.maxDate Sets calendar's maximum visible date; Also used for validation on the server-side.
 * @prop {number} cfg.millisec Default for millisecond selection, if no date is given. Default is 0.
 * @prop {string} cfg.minDate Sets calendar's minimum visible date; Also used for validation on the server-side.
 * @prop {number} cfg.minute Default for minute selection, if no date is given. Default is 0.
 * @prop {number} cfg.minuteMax Maximum boundary for hour selection.
 * @prop {number} cfg.minuteMin Minimum boundary for minute selection.
 * @prop {number} cfg.numberOfMonths Enables multiple page rendering.
 * @prop {boolean} cfg.oneLine Try to show the time dropdowns all on one line. This should be used with the
 * `controlType` set to `select`.
 * @prop {boolean} cfg.popup `true` if `mode` is set to `popup`.
 * @prop {PrimeFaces.widget.Calendar.PreShowCallback} cfg.preShow Callback invoked before the calendar is opened.
 * @prop {PrimeFaces.widget.Calendar.PreShowDayCallback} cfg.preShowDay Callback invoked before a day is shown.
 * @prop {boolean} cfg.readonly Makes the calendar readonly when set to true.
 * @prop {number} cfg.second Default for second selection, if no date is given. Default is 0.
 * @prop {number} cfg.secondMax Maximum boundary for second selection.
 * @prop {number} cfg.secondMin Minimum boundary for second selection.
 * @prop {boolean} cfg.selectOtherMonths Enables selection of days belonging to other months.
 * @prop {string} cfg.showAnim Effect to use when displaying and showing the popup calendar.
 * @prop {boolean} cfg.showButtonPanel Visibility of button panel containing today and done buttons.
 * @prop {string} cfg.showHour Whether to show the hour control.
 * @prop {string} cfg.showMillisec Whether to show the millisec control
 * @prop {string} cfg.showMinute Whether to show the minute control.
 * @prop {PrimeFaces.widget.Calendar.ShowOnType} cfg.showOn Client side event that displays the popup calendar.
 * @prop {boolean} cfg.showOtherMonths Displays days belonging to other months.
 * @prop {string} cfg.showSecond Whether to show the second control.
 * @prop {boolean} cfg.showTodayButton Whether to show the `Current Date` button if `showButtonPanel` is rendered.
 * @prop {boolean} cfg.showWeek Displays the week number next to each week.
 * @prop {string} cfg.stepHour Hour steps.
 * @prop {number} cfg.stepMinute Minute steps.
 * @prop {number} cfg.stepSecond Second steps.
 * @prop {JQueryUITimepickerAddon.CustomControl} cfg.timeControlObject When `controlType` is set to `custom`, an
 * object for creating and handling custom controls for the hour / minute / second inputs.
 * @prop {boolean} cfg.timeInput Allows direct input in time field.
 * @prop {boolean} cfg.timeOnly Shows only timepicker without date.
 * @prop {string} cfg.yearRange Year range for the navigator, default is `c-10:c+10`.
 */
PrimeFaces.widget.Calendar = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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
        this.applyMask();

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
        };

        //Setup timepicker
        var hasTimePicker = this.hasTimePicker();
        if(hasTimePicker) {
            this.configureTimePicker();
        }

        // is touch support enabled
        var touchEnabled = PrimeFaces.env.isTouchable(this.cfg) && !this.input.attr("readonly") && this.cfg.showOn && this.cfg.showOn === 'button';

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

                // #4119 do not popup if readonly
                if ($this.cfg.readonly) {
                    return false;
                }

                //display on top
                setTimeout(function() {
                    $('#ui-datepicker-div').addClass('ui-input-overlay').css('z-index', PrimeFaces.nextZindex());

                    if ($this.cfg.showTodayButton === false) {
                        $(input).datepicker("widget").find(".ui-datepicker-current").hide();
                    }

                    $this.alignPanel();
                }, 50);

                // touch support - prevents keyboard popup
                if(touchEnabled) {
                    $(this).prop("readonly", true);
                }

                //user callback
                var preShow = $this.cfg.preShow;
                if(preShow) {
                    return $this.cfg.preShow.call($this, input, inst);
                }
            };

            PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', $('#ui-datepicker-div'), function() {
                $.datepicker._hideDatepicker();
            });
            PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_hide', function() {
                $.datepicker._hideDatepicker();
            });
        }

        // touch support - prevents keyboard popup
        if (touchEnabled) {
            var fireCloseEvent = this.cfg.onClose;
            this.cfg.onClose = function(dateText, inst) {
                $(this).attr("readonly", false);

                if (fireCloseEvent) {
                    fireCloseEvent();
                }
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

            if(this.cfg.disabled || this.readonly) {
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
    },

    /**
     * Initializes the mask on the input if using a mask and not an inline picker.
     * @private
     */
    applyMask: function() {
        if (this.cfg.inline || this.input.prop('readonly')) {
            return;
        }
        if (this.cfg.mask) {
            var maskCfg = {
                placeholder: this.cfg.maskSlotChar||'_',
                clearMaskOnLostFocus: this.cfg.maskAutoClear||true,
                clearIncomplete: this.cfg.maskAutoClear||true,
                autoUnmask: false
            };
            var pattern = new RegExp("m|d|y|h|s", 'i');
            var isAlias = pattern.test(this.cfg.mask);
            if (isAlias) {
                maskCfg.alias = 'datetime';
                maskCfg.inputFormat = this.cfg.mask;
            } else {
                maskCfg.mask = this.cfg.mask;
            }
            this.input.inputmask('remove').inputmask(maskCfg);
        }
    },

    /**
     * Aligns the overlay panel with the date picker according to the current configuration. It is usually positioned
     * next to or below the input field to which it is attached.
     */
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

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        if(cfg.popup && $.datepicker._lastInput && (cfg.id + '_input') === $.datepicker._lastInput.id) {
            $.datepicker._hideDatepicker();
        }

        this._super(cfg);
    },

    /**
     * Sets up the locale so that this calendar is displayed in the configured langauge.
     * @private
     */
    configureLocale: function() {
        var localeSettings = PrimeFaces.locales[this.cfg.locale];

        if(localeSettings) {
            for(var setting in localeSettings) {
                this.cfg[setting] = localeSettings[setting];
            }
        }
    },

    /**
     * Sets up the event listeners for when the user selects a particular date.
     * @private
     */
    bindDateSelectListener: function() {
        var $this = this;

        this.cfg.onSelect = function() {
            if($this.cfg.popup) {
                $this.fireDateSelectEvent();

                if($this.cfg.focusOnSelect) {
                    $this.refocusInput = true;
                    $this.jqEl.trigger('focus');
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

    /**
     * Triggers the behaviors and event listener for when the user has selected a certain date.
     * @private
     */
    fireDateSelectEvent: function() {
        this.callBehavior('dateSelect');
    },

    /**
     * Sets up the event listeners for when the user switches to a different month or year.
     * @private
     */
    bindViewChangeListener: function() {
        if(this.hasBehavior('viewChange')) {
            var $this = this;
            this.cfg.onChangeMonthYear = function(year, month) {
                $this.fireViewChangeEvent(year, month);
            };
        }
    },

    /**
     * Triggers the behaviors and event listener for when the user has switched to a different month or year.
     * @private
     * @param {number} year New year for which a calendar is shown.
     * @param {number} month New month for which a calendar is shown (0=January).
     */
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

    /**
     * Sets up the event listeners for when this calendar is closed.
     * @private
     */
    bindCloseListener: function() {
        if(this.hasBehavior('close')) {
            var $this = this;
            this.cfg.onClose = function() {
                $this.fireCloseEvent();
            };
        }
    },

    /**
     * Triggers the `close` event when this calendar is closed.
     * @private
     */
    fireCloseEvent: function() {
        this.callBehavior('close');
    },

    /**
     * Creates and initializes the confiugration options for the time picker.
     * @private
     */
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

    /**
     * Checks whether this calendar lets the user specify a clock time (and not just a date).
     * @return {boolean} `true` when this calendar includes a clock time picker, `false` otherwise.
     */
    hasTimePicker: function() {
        return this.cfg.dateFormat.toLowerCase().indexOf('h') != -1;
    },

    /**
     * Sets the currently selected date of the datepicker.
     * @param {Date | null | undefined} date Date to display, or `null` or `undefined` to clear the date.
     */
    setDate: function(date) {
        this.jqEl.datetimepicker('setDate', date);
    },

    /**
     * Finds the currently selected date.
     * @return {Date | null} The selected date of the calendar, or `null` when no date is selected.
     */
    getDate: function() {
        return this.jqEl.datetimepicker('getDate');
    },

    /**
     * Enables the calendar, so that the user can select a date.
     */
    enable: function() {
        this.jqEl.datetimepicker('enable');
    },

    /**
     * Disables the calendar, so that the user can no longer select any date..
     */
    disable: function() {
        this.jqEl.datetimepicker('disable');
    }

});
