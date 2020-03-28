/**
 * __PrimeFaces DatePicker Widget__
 * 
 * DatePicker is an input component used to select a date featuring display modes, paging, localization, ajax selection
 * and more.
 * 
 * DatePicker is designed to replace the old `p:calendar` component.
 * 
 * @typedef {"single" | "multiple"} PrimeFaces.widget.Diagram.SelectionMode Indicates whether only one date or multiple
 * can be selected at any given time.
 * 
 * @typedef {"date" | "month"} PrimeFaces.widget.Diagram.ViewMode View mode of the date picker, either a date picker
 * or a month picker.
 * 
 * @typedef PrimeFaces.widget.DatePicker.OnBeforeHideCallback Callback that is invoked before the date picker popup is
 * hidden.
 * @this {unknown} PrimeFaces.widget.DatePicker.OnBeforeHideCallback
 * 
 * @typedef PrimeFaces.widget.DatePicker.OnSelectCallback Callback that is invoked when the user has selected a date.
 * @this {unknown} PrimeFaces.widget.DatePicker.OnSelectCallback 
 * @param {JQuery.Event} PrimeFaces.widget.DatePicker.OnSelectCallback.event Event that occurred.
 * @param {Date} PrimeFaces.widget.DatePicker.OnSelectCallback.date The selected date.
 * 
 * @typedef PrimeFaces.widget.DatePicker.OnBeforeShowCallback Callback that is invoked before the date picker popup is
 * shown.
 * @this {unknown} PrimeFaces.widget.DatePicker.OnBeforeShowCallback
 * @return {boolean} PrimeFaces.widget.DatePicker.OnBeforeShowCallback Whether the datepicker should be shown.
 * 
 * @typedef PrimeFaces.widget.DatePicker.OnViewDateChangeCallback Callback that is invoked when the month page of the
 * datepicker (inline or popup) has changed. 
 * @this {unknown} PrimeFaces.widget.DatePicker.OnViewDateChangeCallback
 * @param {JQuery.Event} PrimeFaces.widget.DatePicker.OnViewDateChangeCallback.event The event that occurred.
 * @param {Date} PrimeFaces.widget.DatePicker.OnViewDateChangeCallback.value The new date that is shown.
 * 
 * @typedef PrimeFaces.widget.DatePicker.PreShowCallback User-defined callback invoked before the date picker overlay is
 * shown.
 * @this {PrimeFaces.widget.DatePickerCfg} PrimeFaces.widget.DatePicker.PreShowCallback
 * @param {unknown} PrimeFaces.widget.DatePicker.PreShowCallback.datePicker The current date picker instance.
 * 
 * @typedef PrimeFaces.widget.DatePicker.DateTemplate Javascript function that takes a date object and returns the
 * content for the date cell.
 * @this {unknown} PrimeFaces.widget.DatePicker.DateTemplate
 * @param {string | {day: number, month: number, year: number, today: boolean, selectable: boolean}} PrimeFaces.widget.DatePicker.DateTemplate.monthNameOrMonth
 * A month or month name to format.
 * @return {string} PrimeFaces.widget.DatePicker.DateTemplate The content for the date cell.
 * 
 * @prop {JQuery} input The DOM element with the hidden input element.
 * @prop {JQuery} panel The DOM element with the inline or popup element with the date picker.
 * @prop {Date} viewDate Current date shown by the date picker.
 * 
 * @interface {PrimeFaces.widget.DatePickerCfg} cfg The configuration for the {@link  DatePicker| DatePicker widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {JQuery} cfg.appendTo Appends the dialog to the given element.
 * @prop {string} cfg.buttonTabindex Tabindex of the datepicker button
 * @prop {string} cfg.dateFormat Date format to use, e.g. `mm/yy`
 * @prop {PrimeFaces.widget.DatePicker.DateTemplate} cfg.dateTemplate Javascript function that takes a date object and
 * returns the content for the date cell.
 * @prop {(string | Date)[]} disabledDates List of dates that should be disabled.
 * @prop {number[]} disabledDays List of week day indexes that should be disabled.
 * @prop {boolean} cfg.focusOnSelect When enabled, input receives focus after a value is picked.
 * @prop {boolean} cfg.hideOnDateTimeSelect Defines if the popup should be hidden when a time is selected.
 * @prop {string} cfg.hourFormat Defines the hour format, valid values are '12' and '24'.
 * @prop {boolean} cfg.inline Whether to show the datepicker inline or as a popup.
 * @prop {string} cfg.inputStyle Inline style of the input element. Used when mode is popup.
 * @prop {string} cfg.inputStyleClass Style class of the input element. Used when mode is popup.
 * @prop {boolean} cfg.keepInvalid Whether to keep the invalid inputs in the field or not.
 * @prop {Record<string, any>} locale Locale to be used for labels and conversion.
 * @prop {string | Date} maxdate Sets DatePicker's maximum visible date.
 * @prop {number} maxDateCount Defines the maximum number of selectable dates in multiple selection mode.
 * @prop {string | Date} mindate Sets DatePicker's minimum visible date.
 * @prop {boolean} cfg.monthNavigator Whether to show the month navigator.
 * @prop {number} cfg.numberOfMonths Number of months to display concurrently.
 * @prop {PrimeFaces.widget.DatePicker.OnBeforeHideCallback} cfg.onBeforeHide Callback that is invoked before the date
 * picker popup is hidden. Used internally, should not be overridden by the user.
 * @prop {PrimeFaces.widget.DatePicker.OnBeforeShowCallback} cfg.onBeforeShow Callback that is invoked before the date
 * picker popup is shown. Used internally, should not be overridden by the user.
 * @prop {PrimeFaces.widget.DatePicker.OnSelectCallback} cfg.onSelect Callback that is invoked when the user has
 * selected a date. Used internally should not be overridden by the user.
 * @prop {PrimeFaces.widget.DatePicker.OnViewDateChangeCallback} cfg.onViewDateChange Callback that is invoked when the
 * month page of the datepicker (inline or popup) has changed. Used internally, should not be overridden by the user.
 * @prop {string} cfg.panelStyleClass Style class of the container element.
 * @prop {PrimeFaces.widget.DatePicker.PreShowCallback} cfg.preShow User-defined callback that may be overridden by the
 * user. Invoked before the date picker overlay is shown.
 * @prop {string} cfg.rangeSeparator Character for separating ranges, e.g. a dash (`-`).
 * @prop {boolean} cfg.readonlyInput Makes input text of a popup DatePicker readonly.
 * @prop {PrimeFaces.widget.Diagram.SelectionMode} cfg.selectionMode Indicates whether only one date or multiple can be
 * selected at any given time.
 * @prop {boolean} cfg.selectOtherMonths Enables selection of days belonging to other months.
 * @prop {string} cfg.shortYearCutoff The cutoff year for determining the century for a date. Any dates entered with a
 * year value less than or equal to the cutoff year are considered to be in the current century, while those greater
 * than it are deemed to be in the previous century. Defaults to `+10`.
 * @prop {boolean} cfg.showButtonBar Whether to display buttons at the footer.
 * @prop {boolean} cfg.showIcon Whether to show an icon to display the picker in an overlay.
 * @prop {boolean} cfg.showOnFocus Whether to show the popup when input receives focus.
 * @prop {boolean} cfg.showOtherMonths Displays days belonging to other months.
 * @prop {boolean} cfg.showSeconds Whether to show the seconds in time picker. Default is false.
 * @prop {boolean} cfg.showTime Specifies if the time picker should be displayed.
 * @prop {boolean} cfg.timeOnly Shows only time picker without date.
 * @prop {number} cfg.stepHour Hour steps.
 * @prop {number} cfg.stepMinute Minute steps.
 * @prop {number} cfg.stepSeconds Seconds steps.
 * @prop {boolean} cfg.touchUI Activates touch friendly mode.
 * @prop {string} cfg.triggerButtonIcon Icon of the datepicker element that toggles the visibility in popup mode.
 * @prop {Record<string, any>} cfg.userLocale Localized message for days, months etc.
 * @prop {PrimeFaces.widget.Diagram.ViewMode} cfg.view Defines the view mode, valid values are `date` for datepicker and
 * `month` for month picker.
 * @prop {Date | null} cfg.viewDate Date to highlight in the date picker.
 * @prop {boolean} cfg.yearNavigator Whether to show the year navigator. The yearRange attribute is required to use this
 * feature.
 * @prop {string} cfg.yearRange Years to show in the date picker, e.g. `2000:2030`.
 */
PrimeFaces.widget.DatePicker = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.jqEl = this.cfg.inline ? $(this.jqId + '_inline') : this.input;
        var _self = this;

        //i18n and l7n
        this.configureLocale();
        
        //events
        this.bindDateSelectListener();
        this.bindViewChangeListener();
        this.bindCloseListener();
        
        //Client behaviors, input skinning and z-index
        if(!this.cfg.inline) {
            PrimeFaces.skinInput(this.jqEl);

            if(this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.jqEl, this.cfg.behaviors);
            }

            this.cfg.onBeforeShow = function() {
                if(_self.refocusInput) {
                    _self.refocusInput = false;
                    return false;
                }
                
                this.panel.css('z-index', ++PrimeFaces.zindex);
                
                var inst = this; // the instance of prime.datePicker API

                // touch support - prevents keyboard popup
                if(PrimeFaces.env.touch && !inst.inputfield.attr("readonly") && _self.cfg.showIcon) {
                    _self.jqEl.prop("readonly", true);
                }

                //user callback
                var preShow = _self.cfg.preShow;
                if(preShow) {
                    return _self.cfg.preShow.call(_self, inst);
                }
            };
        }

        // touch support - prevents keyboard popup
        if (PrimeFaces.env.touch && !this.input.attr("readonly") && this.cfg.showIcon) {
            this.cfg.onBeforeHide = function() {
                _self.jqEl.attr("readonly", false);
            };
        }

        //Initialize datepicker
        this.cfg.panelStyleClass = (this.cfg.panelStyleClass || '') + ' p-datepicker-panel';
        this.cfg.viewDate = this.viewDateOption;
        this.cfg.appendTo = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo) : null;
        
        this.jq.datePicker(this.cfg);

        //extensions
        if(!this.cfg.inline && this.cfg.showIcon) {
            var triggerButton = this.jqEl.siblings('.ui-datepicker-trigger:button');
            triggerButton.attr('aria-label',PrimeFaces.getAriaLabel('calendar.BUTTON')).attr('aria-haspopup', true);
                        
            var title = this.jqEl.attr('title');
            if(title) {
                triggerButton.attr('title', title);
            }
            
            var buttonIndex = this.cfg.buttonTabindex||this.jqEl.attr('tabindex');
            if(buttonIndex) {
                triggerButton.attr('tabindex', buttonIndex);
            }

            PrimeFaces.skinButton(triggerButton);
        }

        //mark target and descandants of target as a trigger for a primefaces overlay
        if(!this.cfg.inline) {
            this.jq.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);        
    },
    
    /**
     * Initializes the localized messages for the currently configured language.
     * @private
     */
    configureLocale: function() {
        var localeSettings = PrimeFaces.locales[this.cfg.userLocale];

        if(localeSettings) {
            var locale = {};
            for(var setting in localeSettings) {
                locale[setting] = localeSettings[setting];
            }
            
            this.cfg.userLocale = locale;
        }
    },
    
    /**
     * Sets up the event listener for when another date was selected.
     * @private
     */
    bindDateSelectListener: function() {
        var _self = this;

        this.cfg.onSelect = function(event, date) {
            _self.viewDateOption = this.viewDate;
            
            _self.fireDateSelectEvent();
            
            if(!_self.cfg.inline && _self.cfg.focusOnSelect) {
                _self.refocusInput = true;
                _self.jqEl.focus();
                if(!_self.cfg.showIcon) {
                    var inst = this;
                    
                    _self.jqEl.off('click.datepicker').on('click.datepicker', function() {
                        inst.showOverlay();
                    });
                }

                setTimeout(function() {
                    _self.refocusInput = false;
                }, 10);
            }
        };
    },

    /**
     * Triggers the event for when another date was selected.
     * @private
     */
    fireDateSelectEvent: function() {
        if(this.cfg.behaviors) {
            var dateSelectBehavior = this.cfg.behaviors['dateSelect'];

            if(dateSelectBehavior) {
                dateSelectBehavior.call(this);
            }
        }
    },

    /**
     * Sets up the event listener for when the date picker changes to a different month or year page.
     * @private
     */
    bindViewChangeListener: function() {
        var _self = this;
        this.cfg.onViewDateChange = function(event, date) {
            _self.viewDateOption = date;
            
            if(_self.hasBehavior('viewChange')) {
                _self.fireViewChangeEvent(date.getFullYear(), date.getMonth());
            }
        }; 
    },

    /**
     * Triggers the event for when the date picker changed to a different month or year page.
     * @private
     * @param {number} year The year to which the date picker changed.
     * @param {number} month The year to which the date picker changed, starting with `0` for `Janurary`.
     */
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

    /**
     * Sets up the event listeners for when the date picker is closed.
     * @private
     */
    bindCloseListener: function() {
        if(this.hasBehavior('close')) {
            var $this = this;
            this.cfg.onBeforeHide = function() {
                $this.fireCloseEvent();
            };
        }
    },

    /**
     * Fires the close event when the date picker is closed.
     * @private
     */
    fireCloseEvent: function() {
        if(this.cfg.behaviors) {
            var closeBehavior = this.cfg.behaviors['close'];
            if(closeBehavior) {
                closeBehavior.call(this);
            }
        }
    },
    
    /**
     * Sets the date value the date picker.
     * @param {Date} date The new date for this widget.
     */
    setDate: function(date) {
        this.jq.datePicker('setDate', date);
    },

    /**
     * Gets the currently selected date value of the date picker.
     * @return {Date | string | null} The date, if one is currently selected. The empty `string` or `null` when no date
     * is selected.
     */
    getDate: function() {
        return this.jq.datePicker('getDate');
    },
    
    /**
     * Sets the displayed visible calendar date. This refers to the currently displayed month page.
     * @param {Date} date The date to be shown in the calendar. 
     */
    setViewDate: function(date) {
        this.jq.datePicker('updateViewDate', null, date);
    },

    /**
     * Gets the displayed visible calendar date. This refers to the currently displayed month page.
     * @return {Date} The date curre 
     */
    getViewDate: function() {
        return this.jq.datePicker().data().primeDatePicker.viewDate;
    },
    
});