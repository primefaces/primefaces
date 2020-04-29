/**
 * __PrimeFaces DatePicker Widget__
 * 
 * DatePicker is an input component used to select a date featuring display modes, paging, localization, ajax selection
 * and more.
 * 
 * DatePicker is designed to replace the old {@link Calendar|p:calendar} component.
 * 
 * To interact with the calendar, use the methods of this widget, or for more advanced usages, use the `datePicker`
 * JQueryUI widget plugin, for example:
 * 
 * ```javascript
 * PF("datePickerWidget").getDate();
 * PF("datePickerWidget").jq.datePicker("getDate");
 * 
 * PF("datePickerWidget").setDate(new Date());
 * PF("datePickerWidget").jq.datePicker("setDate", new Date());
 * 
 * PF("datePickerWidget").jq.datePicker("enableModality");
 * ```
 * 
 * @typedef PrimeFaces.widget.DatePicker.PreShowCallback User-defined callback invoked before the date picker overlay is
 * shown.
 * @this {PrimeFaces.widget.DatePickerCfg} PrimeFaces.widget.DatePicker.PreShowCallback
 * @param {JQueryPrimeDatePicker.PickerInstance} PrimeFaces.widget.DatePicker.PreShowCallback.datePicker The current
 * date picker instance.
 * 
 * @prop {JQuery} input The DOM element for the hidden input element with the selected date.
 * @prop {JQuery} jqEl The DOM element for the inline picker or the input.
 * @prop {boolean} refocusInput Whether focus should be put on the input again.
 * @prop {Date | Date[]} viewDateOption The date that is displayed in the date picker.
 * 
 * @interface {PrimeFaces.widget.DatePickerCfg} cfg The configuration for the {@link  DatePicker| DatePicker widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryPrimeDatePicker.PickerOptions} cfg 
 * 
 * @prop {string} cfg.buttonTabindex Tabindex of the datepicker button
 * @prop {boolean} cfg.focusOnSelect When enabled, input receives focus after a value is picked.
 * @prop {PrimeFaces.widget.DatePicker.PreShowCallback} cfg.preShow User-defined callback that may be overridden by the
 * user. Invoked before the date picker overlay is shown.
 * @prop {string} cfg.triggerButtonIcon Icon of the datepicker element that toggles the visibility in popup mode.
 */
PrimeFaces.widget.DatePicker = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
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
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        if (this.cfg.appendTo) {
            PrimeFaces.utils.cleanupDynamicOverlay(this, this.panel, this.id + '_panel', this.cfg.appendTo);
        }

        this._super(cfg);
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        if (this.cfg.appendTo) {
            PrimeFaces.utils.removeDynamicOverlay(this, null, this.id + '_panel', this.cfg.appendTo);
        }

        this._super();
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
        // #5830: do not fire in range mode if only the first value is set
        if (this.cfg.selectionMode === "range" && this.input.val().indexOf(this.cfg.rangeSeparator) === -1) {
            return;
        }

        this.callBehavior('dateSelect');
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
     * @param {Date | string} date The new date for this widget.
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
     * @param {string | Date | Date[]} date The date to be shown in the calendar. 
     */
    setViewDate: function(date) {
        var viewDate = this.jq.data().primeDatePicker.parseValue(date);
        this.jq.datePicker('updateViewDate', null, viewDate);
    },

    /**
     * Gets the displayed visible calendar date. This refers to the currently displayed month page.
     * @return {Date | Date[]} The currently displayed date or dates. 
     */
    getViewDate: function() {
        return this.jq.datePicker().data().primeDatePicker.viewDate;
    },

});