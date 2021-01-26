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
 * @prop {string} cfg.appendTo The search expression for the element to which the overlay panel should be appended.
 * @prop {string} cfg.buttonTabindex Tabindex of the datepicker button
 * @prop {boolean} cfg.focusOnSelect When enabled, input receives focus after a value is picked.
 * @prop {boolean} cfg.inline Whether the datepicker is rendered inline or as an overlay.
 * @prop {string} cfg.mask Applies a mask using the pattern.
 * @prop {boolean} cfg.maskAutoClear Clears the field on blur when incomplete input is entered
 * @prop {string} cfg.maskSlotChar Placeholder in mask template.
 * @prop {JQueryPrimeDatePicker.BaseCallback} cfg.onPanelCreate Callback invoked after the datepicker panel was created.
 * @prop {PrimeFaces.widget.DatePicker.PreShowCallback} cfg.preShow User-defined callback that may be overridden by the
 * user. Invoked before the date picker overlay is shown.
 * @prop {string} cfg.rangeSeparator Separator for joining start and end dates when selection mode is `range`, defaults
 * to `-`.
 * @prop {string} cfg.timeSeparator Separator for joining hour and minute, defaults to `:`.
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
        var $this = this;

        //i18n and l7n
        this.configureLocale();

        //events
        this.bindPanelCreationListener();
        this.bindDateSelectListener();
        this.bindClearButtonListener();
        this.bindViewChangeListener();
        this.bindCloseListener();

        // is touch support enabled
        var touchEnabled = PrimeFaces.env.isTouchable(this.cfg) && !this.input.attr("readonly") && this.cfg.showIcon;

        //Client behaviors, input skinning and z-index
        if(!this.cfg.inline) {
            PrimeFaces.skinInput(this.jqEl);

            if(this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.jqEl, this.cfg.behaviors);
            }

            this.cfg.onBeforeShow = function() {
                if($this.refocusInput) {
                    $this.refocusInput = false;
                    return false;
                }

                this.panel.css('z-index', PrimeFaces.nextZindex());

                var inst = this; // the instance of prime.datePicker API

                // touch support - prevents keyboard popup
                if(touchEnabled) {
                    $this.jqEl.prop("readonly", true);
                }

                //user callback
                var preShow = $this.cfg.preShow;
                if(preShow) {
                    return $this.cfg.preShow.call($this, inst);
                }
            };
        }

        // touch support - prevents keyboard popup
        if (touchEnabled) {
            this.cfg.onBeforeHide = function() {
                $this.jqEl.attr("readonly", false);
            };
        }

        //Initialize datepicker
        this.cfg.panelStyleClass = (this.cfg.panelStyleClass || '') + ' p-datepicker-panel';
        this.cfg.viewDate = this.viewDateOption;
        this.cfg.rangeSeparator = this.cfg.rangeSeparator||'-';
        this.cfg.timeSeparator = this.cfg.timeSeparator||':';

        this.jq.datePicker(this.cfg);
        this.applyMask(); // moved below datapicker initialization because of race condition in event handling. See https://github.com/primefaces/primefaces/issues/6445

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

        //mark target and descendants of target as a trigger for a PrimeFaces overlay
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
     * Callback for after the overlay panel is created.
     * @private
     */
    bindPanelCreationListener: function() {
        var $this = this;

        this.cfg.onPanelCreate = function() {
            $this.panel = this.panel;
            this.options.appendTo = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(PrimeFaces.utils.resolveAppendTo($this));

            if (!$this.cfg.inline) {
                PrimeFaces.utils.registerConnectedOverlayScrollHandler($this, 'scroll.' + $this.id + '_hide', function() {
                    $this.jq.data().primeDatePicker.hideOverlay();
                });
            }
        };
    },

    /**
     * Sets up the event listener for when another date was selected.
     * @private
     */
    bindDateSelectListener: function() {
        var $this = this;

        this.cfg.onSelect = function(event, date) {
            $this.viewDateOption = this.viewDate;

            $this.fireDateSelectEvent();

            if(!$this.cfg.inline && $this.cfg.focusOnSelect) {
                $this.refocusInput = true;
                $this.jqEl.trigger('focus');
                if(!$this.cfg.showIcon) {
                    var inst = this;

                    $this.jqEl.off('click.datepicker').on('click.datepicker', function() {
                        inst.showOverlay();
                    });
                }

                setTimeout(function() {
                    $this.refocusInput = false;
                }, 10);
            }
        };
    },

    /**
     * Sets up the event listener for when the Clear button is selected.
     * @private
     */
    bindClearButtonListener: function() {
        var $this = this;

        this.cfg.onClearButtonClick = function(event) {
            $this.input.trigger('change');
            $this.callBehavior('dateSelect');
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

        this.input.trigger('change');
        this.callBehavior('dateSelect');
    },

    /**
     * Sets up the event listener for when the date picker changes to a different month or year page.
     * @private
     */
    bindViewChangeListener: function() {
        var $this = this;
        this.cfg.onViewDateChange = function(event, date) {
            $this.viewDateOption = date;
            $this.fireViewChangeEvent(date);
        };
    },

    /**
     * Triggers the event for when the date picker changed to a different month or year page.
     * @private
     * @param {Date} date The date to which the date picker changed.
     */
    fireViewChangeEvent: function(date) {
        var $this = this;
        var lazy = this.cfg.lazyModel;
        var options = {
            params: [
                {name: this.id + '_year', value: date.getFullYear()},
                {name: this.id + '_month', value: date.getMonth()}
            ]
        }
        if (lazy) {
            options.onsuccess = function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        var dateMetadata = JSON.parse(content).dateMetadata;
                        var pdp = $this.jq.data().primeDatePicker;
                        var disabledDates = [];
                        var dateStyleClasses = {};
                        for (date in dateMetadata) {
                            var parsedDate = pdp.parseOptionValue(date);
                            if (dateMetadata[date].disabled) {
                                disabledDates.push(parsedDate);
                            }
                            if (dateMetadata[date].styleClass) {
                                dateStyleClasses[pdp.toISODateString(parsedDate)] = dateMetadata[date].styleClass;
                            }
                        }
                        pdp.options.dateStyleClasses = dateStyleClasses;
                        $this.setDisabledDates(disabledDates);
                    }
                });
                return true;
            };
        }

        if (this.hasBehavior('viewChange')) {
            if (lazy) {
                options.update = (options.update || '') + ' ' + this.id;
            }
            this.callBehavior('viewChange', options);
        }
        else if (lazy) {
            options.event = 'viewChange';
            options.source = this.id;
            options.process = this.id;
            options.update = this.id;
            options.formId = this.cfg.formId;
            PrimeFaces.ajax.Request.handle(options);
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

    /**
     * Sets the disabled dates.
     * @param {string[] | Date[]} disabledDates The dates to disable.
     */
    setDisabledDates: function(disabledDates) {
        var pdp = this.jq.data().primeDatePicker;
        pdp.options.disabledDates = disabledDates;
        if (pdp.options.disabledDates) {
            for (var i = 0; i < pdp.options.disabledDates.length; i++) {
                pdp.options.disabledDates[i] = pdp.parseOptionValue(pdp.options.disabledDates[i]);
            }
        }
        this.updatePanel();
    },

    /**
     * Sets the disabled days.
     * @param {number[]} disabledDays The days to disable.
     */
    setDisabledDays: function(disabledDays) {
        this.jq.data().primeDatePicker.options.disabledDays = disabledDays;
        this.updatePanel();
    },

    /**
     * Update panel.
     * @private
     */
    updatePanel: function() {
        var pdp = this.jq.data().primeDatePicker;
        pdp.panel.get(0).innerHTML = pdp.renderPanelElements();
    },

    /**
     * Shows the popup panel.
     */
    show: function() {
        this.jq.data().primeDatePicker.showOverlay();
    },

    /**
     * Hide the popup panel.
     */
    hide: function() {
        this.jq.data().primeDatePicker.hideOverlay();
    },

    /**
     * Enables the datepicker, so that the user can select a date.
     */
    enable: function() {
        this.jq.data().primeDatePicker.options.disabled = false;
        this.updatePanel();
        this.input.prop('disabled', false).removeClass('ui-state-disabled');
    },

    /**
     * Disables the datepicker, so that the user can no longer select any date.
     */
    disable: function() {
        this.hide();
        this.jq.data().primeDatePicker.options.disabled = true;
        this.updatePanel();
        this.input.prop('disabled', true).addClass('ui-state-disabled');
    }

});
