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
 * @prop {JQuery} triggerButton The DOM element for the trigger button if using showIcon.
 * @prop {JQuery} jqEl The DOM element for the inline picker or the input.
 * @prop {JQuery} [panel] The DOM element for the panel with the datepicker.
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
PrimeFaces.widget.DatePicker = class DatePicker extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.input = $(this.jqId + '_input');
        this.jqEl = this.cfg.inline ? $(this.jqId + '_inline') : this.input;
        var $this = this;

        // auto detect touch interface for mobile
        this.cfg.autoDetectDisplay = (this.cfg.autoDetectDisplay === undefined) ? true : this.cfg.autoDetectDisplay;
        this.cfg.responsiveBreakpoint = this.cfg.responsiveBreakpoint || 576;
        
        // default date should be input value before widget value
        this.cfg.defaultDate = this.input.val() || this.cfg.defaultDate;
        this.cfg.focusOnSelect = !!this.cfg.focusOnSelect;

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
            this.applyMask(); // must be before datepicker/input see #6445/#7176/#13059
            PrimeFaces.skinInput(this.jqEl);

            if(this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.jqEl, this.cfg.behaviors);
            }

            this.cfg.onBeforeShow = function() {
                if($this.refocusInput) {
                    $this.refocusInput = false;
                    return false;
                }

                PrimeFaces.nextZindex(this.panel);

                var inst = this; // the instance of prime.datePicker API

                // touch support - prevents keyboard popup
                if(touchEnabled) {
                    $this.jqEl.prop("readonly", true);
                }

                // user callback
                var preShow = $this.cfg.preShow;
                if(preShow) {
                    return $this.cfg.preShow.call($this, inst);
                }

                // #7457 trigger view change if lazy model is used
                if ($this.cfg.lazyModel) {
                    $this.updateLazyModel();
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
        this.cfg.rangeSeparator = this.cfg.rangeSeparator||'-';
        this.cfg.timeSeparator = this.cfg.timeSeparator||':';
        
        if (this.cfg.selectionMode === "range") {
            this.cfg.viewDate = this.viewDateOption;
        }
        else {
            this.cfg.viewDate = this.cfg.defaultDate;
        }
        
        this.jq.datePicker(this.cfg);

        // create the trigger button if showIcon is enabled
        this.bindTriggerButton();

        //mark target and descendants of target as a trigger for a PrimeFaces overlay
        if(!this.cfg.inline) {
            this.jq.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
        }

        // set original responsive display
        this.jq.data().primeDatePicker.updateResponsiveness();

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        // #11645 trigger view change if lazy model is used
        if (this.cfg.inline && this.cfg.lazyModel) {
            this.updateLazyModel();
        }
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        if (this.panel && this.cfg.appendTo) {
            var appendTo = PrimeFaces.utils.resolveDynamicOverlayContainer(this);
            PrimeFaces.utils.cleanupDynamicOverlay(this, this.panel, this.id + '_panel', appendTo);
        }

        super.refresh(cfg);
    }

    /**
     * @override
     * @inheritdoc
     */
    destroy() {
        if (this.panel && this.cfg.appendTo) {
            var appendTo = PrimeFaces.utils.resolveDynamicOverlayContainer(this);
            PrimeFaces.utils.removeDynamicOverlay(this, null, this.id + "_panel", appendTo);
        }

        this.jq.datePicker().data().primeDatePicker._destroy();
        // DOM de-reference memory clean up
        for (var key in this.jq.data().primeDatePicker) {
            this.jq.data().primeDatePicker[key] = null;
        }

        if (this.cfg.mask && this.input) {
            this.input.inputmask("remove");
            this.input.off();
        }

        super.destroy();
    }

    /**
     * Initializes the localized messages for the currently configured language.
     * @private
     */
    configureLocale() {
        var localeSettings = PrimeFaces.getLocaleSettings(this.cfg.userLocale);

        if(localeSettings) {
            var locale = {};
            for(var setting in localeSettings) {
                locale[setting] = localeSettings[setting];
            }
            if (this.cfg.localeAm) {
                locale["am"] = this.cfg.localeAm;
            }
            if (this.cfg.localePm) {
                locale["pm"] = this.cfg.localePm;
            }
            this.cfg.userLocale = locale;
        }
    }

    /**
     * Initializes the mask on the input if using a mask and not an inline picker.
     * @private
     */
    applyMask() {
        if (this.cfg.inline || this.input.is('[readonly]') || this.input.is(':disabled')) {
            return;
        }
        var $this = this;
        if (this.cfg.mask) {
            var isAutoClear = (this.cfg.maskAutoClear === undefined) ? true : this.cfg.maskAutoClear;
            var maskCfg = {
                placeholder: this.cfg.maskSlotChar||'_',
                clearMaskOnLostFocus: isAutoClear,
                clearIncomplete: isAutoClear,
                autoUnmask: false,
                showMaskOnHover: false,
                onBeforePaste: function (pastedValue, opts) {
                    // GitHub #8319 issue with pasting mask
                    // TODO: Remove if InputMask 5.0.8+ fixes the issue
                    PrimeFaces.queueTask(function(){ $this.input.trigger("input")}, 20);
                    return pastedValue;
                }
            };
            var pattern = /[mdyhs]/i;
            var isAlias = pattern.test(this.cfg.mask);
            if (isAlias) {
                maskCfg.alias = 'datetime';
                maskCfg.inputFormat = this.cfg.mask;
            } else {
                maskCfg.mask = this.cfg.mask;
            }
            this.input.inputmask('remove').inputmask(maskCfg);
            this.input.off("blur"); // GitHub #9259/#12428
        }
    }

    /**
     * Initializes and configures the trigger button for the datepicker.
     * Only applies when the datepicker is not inline and showIcon is enabled.
     * Sets up accessibility attributes, copies title from input, handles tabindex,
     * applies skinning, and manages disabled state.
     * @private
     */
    bindTriggerButton() {
        if(!this.cfg.inline && this.cfg.showIcon) {
            this.triggerButton = this.jqEl.siblings('.ui-datepicker-trigger:button');
            this.triggerButton.attr('aria-label',this.getLabel('chooseDate')).attr('aria-haspopup', true);

            var title = this.jqEl.attr('title');
            if(title) {
                this.triggerButton.attr('title', title);
            }

            var buttonIndex = this.cfg.buttonTabindex||this.jqEl.attr('tabindex');
            if(buttonIndex) {
                this.triggerButton.attr('tabindex', buttonIndex);
            }

            PrimeFaces.skinButton(this.triggerButton);
            if (this.cfg.disabled) {
                PrimeFaces.utils.disableButton(this.triggerButton);
            }
        }
    }

    /**
     * Callback for after the overlay panel is created.
     * @private
     */
    bindPanelCreationListener() {
        var $this = this;

        this.cfg.onPanelCreate = function() {
            $this.panel = this.panel;
            $this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo($this, $this.jq, $this.panel);
            // #8423
            if ($this.cfg.inline) {
                $this.panel.css('position', '');
            }
            this.options.appendTo = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector($this.jq, $this.cfg.appendTo);
        };
    }

    /**
     * Sets up the event listener for when another date was selected.
     * @private
     */
    bindDateSelectListener() {
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

                PrimeFaces.queueTask(function() {
                    $this.refocusInput = false;
                }, 10);
            }
        };
    }

    /**
     * Sets up the event listener for when the Clear button is selected.
     * @private
     */
    bindClearButtonListener() {
        var $this = this;

        this.cfg.onClearButtonClick = function(event) {
            $this.input.trigger('change');
            $this.callBehavior('dateSelect');
        };
    }

    /**
     * Triggers the event for when another date was selected.
     * @private
     */
    fireDateSelectEvent() {
        // #5830: do not fire in range mode if only the first value is set
        if (this.cfg.selectionMode === "range" && this.input.val().indexOf(this.cfg.rangeSeparator) === -1) {
            return;
        }

        this.input.trigger('change');
        this.callBehavior('dateSelect');
    }

    /**
     * Sets up the event listener for when the date picker changes to a different month or year page.
     * @private
     */
    bindViewChangeListener() {
        var $this = this;
        this.cfg.onViewDateChange = function(event, date) {
            $this.viewDateOption = date;
            $this.fireViewChangeEvent(date);
        };
    }

    /**
     * Triggers the event for when the date picker changed to a different month or year page.
     * @private
     * @param {Date} date The date to which the date picker changed.
     */
    fireViewChangeEvent(date) {
        var $this = this;
        var lazy = this.cfg.lazyModel;
        var options = {
            params: [
                {name: this.id + '_year', value: date.getFullYear()},
                {name: this.id + '_month', value: date.getMonth()}
            ]
        }
        if (lazy) {
            if (!this.panel.parent().is("div.ui-state-disabled")) {
                this.panel.wrap("<div class='ui-state-disabled ui-datepicker-disabled'/>");
            }
            options.onsuccess = function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        var dateMetadata = JSON.parse(content).dateMetadata;
                        var pdp = $this.jq.data().primeDatePicker;
                        var disabledDates = [];
                        var enabledDates = [];
                        var dateStyleClasses = {};
                        for (date in dateMetadata) {
                            var parsedDate = pdp.parseOptionValue(date);
                            if (dateMetadata[date].disabled) {
                                disabledDates.push(parsedDate);
                            }
                            if (dateMetadata[date].enabled) {
                                enabledDates.push(parsedDate);
                            }
                            if (dateMetadata[date].styleClass) {
                                dateStyleClasses[pdp.toISODateString(parsedDate)] = dateMetadata[date].styleClass;
                            }
                        }
                        pdp.options.dateStyleClasses = dateStyleClasses;
                        $this.setDisabledDates(disabledDates);
                        $this.setEnabledDates(enabledDates);
                        if ($this.panel.parent().is("div.ui-state-disabled")) {
                            $this.panel.unwrap();
                        }
                        
                        // focus the overly correct item for accessibility
                        pdp.focusOverlay();
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
            options.formId = this.getParentFormId();
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Triggers a viewChange event which updates the lazy model through an Ajax request using the current date.
     */
    updateLazyModel() {
        if (this.cfg.lazyModel) {
            this.fireViewChangeEvent(this.getViewDate());
        }
    }

    /**
     * Sets up the event listeners for when the date picker is closed.
     * @private
     */
    bindCloseListener() {
        if(this.hasBehavior('close')) {
            var $this = this;
            this.cfg.onBeforeHide = function() {
                $this.fireCloseEvent();
            };
        }
    }

    /**
     * Fires the close event when the date picker is closed.
     * @private
     */
    fireCloseEvent() {
        if(this.cfg.behaviors) {
            var closeBehavior = this.cfg.behaviors['close'];
            if(closeBehavior) {
                closeBehavior.call(this);
            }
        }
    }

    /**
     * Sets the date value the date picker.
     * @param {Date | string} date The new date for this widget.
     */
    setDate(date) {
        this.jq.datePicker('setDate', date);

        // #14487 AJAX is not triggered on setDate(null). Specifically not done in the Prime Datepicker widget.
        if (!date) {
            this.fireDateSelectEvent();
        }
    }

    /**
     * Gets the currently selected date value of the date picker.
     * @return {Date | string | null} The date, if one is currently selected. The empty `string` or `null` when no date
     * is selected.
     */
    getDate() {
        return this.jq.datePicker('getDate');
    }

    /**
     * Checks whether a date is selected.
     * @returns {boolean} true if a date is selected.
     */
    hasDate() {
        return (this.getDate() instanceof Date);
    }

    /**
     * Sets the displayed visible calendar date. This refers to the currently displayed month page.
     * @param {string | Date | Date[]} date The date to be shown in the calendar.
     * @param {boolean} [silent=false] Whether to update the view date without triggering the AJAX viewChange event.
     */
    setViewDate(date, silent = false) {
        var viewDate = this.jq.data().primeDatePicker.parseValue(date);
        this.jq.datePicker('updateViewDate', null, viewDate, silent);
    }

    /**
     * Gets the displayed visible calendar date. This refers to the currently displayed month page.
     * @return {Date | Date[]} The currently displayed date or dates.
     */
    getViewDate() {
        return this.jq.datePicker().data().primeDatePicker.viewDate;
    }

    /**
     * Sets the disabled dates.
     * @param {string[] | Date[]} disabledDates The dates to disable.
     */
    setDisabledDates(disabledDates) {
        var pdp = this.jq.data().primeDatePicker;
        pdp.options.disabledDates = disabledDates;
        if (pdp.options.disabledDates) {
            for (var i = 0; i < pdp.options.disabledDates.length; i++) {
                pdp.options.disabledDates[i] = pdp.parseOptionValue(pdp.options.disabledDates[i]);
            }
        }
        this.updatePanel();
    }

    /**
     * Sets the enabled dates.
     * @param {string[] | Date[]} enabledDates The dates to enable.
     */
    setEnabledDates(enabledDates) {
        var pdp = this.jq.data().primeDatePicker;

        if (enabledDates != null && enabledDates.length > 0) {
            pdp.options.enabledDates = enabledDates;
            if (pdp.options.enabledDates) {
                for (var i = 0; i < pdp.options.enabledDates.length; i++) {
                    pdp.options.enabledDates[i] = pdp.parseOptionValue(pdp.options.enabledDates[i]);
                }
            }
        }
        this.updatePanel();
    }

    /**
     * Sets the disabled days.
     * @param {number[]} disabledDays The days to disable.
     */
    setDisabledDays(disabledDays) {
        this.jq.data().primeDatePicker.options.disabledDays = disabledDays;
        this.updatePanel();
    }

    /**
     * Update panel.
     * @private
     */
    updatePanel() {
        var pdp = this.jq.data().primeDatePicker;
        pdp.panel.get(0).innerHTML = pdp.renderPanelElements();
    }

    /**
     * Shows the popup panel.
     */
    show() {
        this.jq.data().primeDatePicker.showOverlay();
    }

    /**
     * Hide the popup panel.
     */
    hide() {
        this.jq.data().primeDatePicker.hideOverlay();
    }

    /**
     * Enables the datepicker, so that the user can select a date.
     */
    enable() {
        this.jq.data().primeDatePicker.options.disabled = false;
        this.updatePanel();
        PrimeFaces.utils.enableInputWidget(this.input);
        PrimeFaces.utils.enableButton(this.triggerButton);
    }

    /**
     * Disables the datepicker, so that the user can no longer select any date.
     */
    disable() {
        this.hide();
        this.jq.data().primeDatePicker.options.disabled = true;
        this.updatePanel();
        PrimeFaces.utils.disableInputWidget(this.input);
        PrimeFaces.utils.disableButton(this.triggerButton);
    }

}
