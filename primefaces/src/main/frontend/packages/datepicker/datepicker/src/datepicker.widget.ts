/**
 * A day of the calendar.
 */
export interface DateMeta {
    /** Day of the month, 1-based. */
    day: number;
    /** 0-based month. */
    month: number;
    /** Full year, e.g. `2026`. */
    year: number;
    /**
     * Whether the day belongs to the previous or next month and only fills the first or last week of the displayed
     * month.
     */
    otherMonth?: boolean;
    /** Whether the day is today, in the time zone of the date picker. */
    today?: boolean;
    /**
     * Whether the day can be selected: within min / max date, not disabled and not a day of another month unless
     * `selectOtherMonths` is set.
     */
    selectable?: boolean;
}

/** A month displayed in the date view, with its weeks of days. */
interface MonthMeta {
    /** 0-based month. */
    month: number;
    /** Full year. */
    year: number;
    /**
     * The weeks of the month with 7 days each, including the days of the previous and next month that fill the first
     * and last week.
     */
    dates: DateMeta[][];
    /** Index of the month among the displayed months, see `numberOfMonths`. */
    index: number;
}

/**
 * The selected value of a {@link DatePicker}: a date (single selection), a list of dates (multiple and range selection,
 * the end of an incomplete range is `null`), the raw text of the input if it could not be parsed, or `null` /
 * `undefined` if nothing or an invalid value is selected.
 */
export type DatePickerValue = Date | (Date | null)[] | string | null | undefined;

/**
 * Localized texts and settings of the date picker.
 */
export interface DatePickerLocale {
    /** First day of the week, 0 = sunday. */
    firstDayOfWeek: number;
    /** Offset of the first day of the first week of the year, see `showWeek`. */
    firstDayWeekOffset?: number;
    /** Whether the year is displayed before the month in the header of the panel. */
    showMonthAfterYear: boolean;
    /**
     * Full names of the week days, starting with sunday, used by the `DD` date format and as title of the column
     * headers.
     */
    dayNames: string[];
    /** Short names of the week days, starting with sunday, used by the `D` date format. */
    dayNamesShort: string[];
    /** Minimal names of the week days, starting with sunday, displayed as column headers of the date grid. */
    dayNamesMin: string[];
    /** Full names of the months, used by the `MM` date format, the header and with `showLongMonthNames`. */
    monthNames: string[];
    /** Short names of the months, used by the `M` date format and the month navigator. */
    monthNamesShort: string[];
    /** Header of the week number column, see `showWeek`. */
    weekHeader?: string;
    /** Label of the today button. */
    today: string;
    /** Label of the clear button. */
    clear: string;
    /** Label of the today button when the time is shown. */
    now: string;
    /** Accessible label of the year navigator. */
    year: string;
    /** Accessible label of the month navigator. */
    month: string;
    /** Localized text for a week. */
    week: string;
    /** Localized text for a day. */
    day: string;
    /** Accessible label of the hour input of the time picker. */
    hourText: string;
    /** Accessible label of the minute input of the time picker. */
    minuteText: string;
    /** Accessible label of the second input of the time picker. */
    secondText: string;
    /** Accessible label of the millisecond input of the time picker. */
    millisecondText: string;
    /** Text of the period before noon in the 12 hour format. */
    am: string;
    /** Text of the period after noon in the 12 hour format. */
    pm: string;
    /** Accessible label of the panel and the trigger button. */
    chooseDate: string;
    /** Accessible label for navigating to the previous decade. */
    prevDecade: string;
    /** Accessible label for navigating to the next decade. */
    nextDecade: string;
    /** Accessible label of the button that navigates to the previous year in the month view. */
    prevYear: string;
    /** Accessible label of the button that navigates to the next year in the month view. */
    nextYear: string;
    /** Accessible label of the button that navigates to the previous month. */
    prevMonth: string;
    /** Accessible label of the button that navigates to the next month. */
    nextMonth: string;
    /** Accessible label of the button that decrements the hour. */
    prevHour: string;
    /** Accessible label of the button that increments the hour. */
    nextHour: string;
    /** Accessible label of the button that decrements the minute. */
    prevMinute: string;
    /** Accessible label of the button that increments the minute. */
    nextMinute: string;
    /** Accessible label of the button that decrements the second. */
    prevSecond: string;
    /** Accessible label of the button that increments the second. */
    nextSecond: string;
    /** Accessible label of the button that decrements the millisecond. */
    prevMillisecond: string;
    /** Accessible label of the button that increments the millisecond. */
    nextMillisecond: string;
}

/**
 * The configuration for the {@link DatePicker} widget.
 */
export interface DatePickerCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Search expression of the element to which the panel is appended. */
    appendTo: string | null;
    /** Whether to switch to the touch UI below the responsive breakpoint. */
    autoDetectDisplay: boolean;
    /** Whether the month navigator respects min / max date when the input changes. */
    autoMonthFormat: boolean;
    /** Tabindex of the trigger button. */
    buttonTabindex: string;
    /** Style class of the clear button. */
    clearButtonStyleClass: string;
    /** Date format, e.g. `mm/dd/yy`. */
    dateFormat: string;
    /** Style classes of single dates, by ISO date. */
    dateStyleClasses: Record<string, string> | null;
    /** Custom content of a date cell, or of a month in the month view. */
    dateTemplate: ((this: DatePicker, date: DateMeta | string) => string) | null;
    /** The selected value as rendered on the server. */
    defaultDate: string | Date | Date[] | null;
    /** Hour of the initial view date when no date is selected. */
    defaultHour: number;
    /** Millisecond of the initial view date when no date is selected, only used with `showMilliseconds`. */
    defaultMillisecond: number;
    /** Minute of the initial view date when no date is selected. */
    defaultMinute: number;
    /** Second of the initial view date when no date is selected, only used with `showSeconds` or `showMilliseconds`. */
    defaultSecond: number;
    /** Whether the date picker is disabled. */
    disabled: boolean;
    /** Disabled dates. */
    disabledDates: (string | Date)[] | null;
    /** Disabled week days, 0 = sunday. */
    disabledDays: number[] | null;
    /** Enabled dates, all others are disabled. */
    enabledDates: (string | Date)[] | null;
    /** Whether to use the flex grid for the button bar. */
    flex: boolean;
    /** Whether the input receives focus after a value is picked. */
    focusOnSelect: boolean;
    /** Separator of the milliseconds. */
    fractionSeparator: string;
    /** Whether to hide the panel when a date and time was selected. */
    hideOnDateTimeSelect: boolean;
    /** Whether to hide the panel when the range was selected. */
    hideOnRangeSelection: boolean;
    /** `12` or `24`. */
    hourFormat: string;
    /** Icon of the trigger button. */
    icon: string;
    /** Whether the date picker is displayed inline instead of an overlay. */
    inline: boolean;
    /** Whether invalid input is kept. */
    keepInvalid: boolean;
    /** Whether a lazy date metadata model is used. */
    lazyModel: boolean;
    /** The localized texts, merged from `userLocale`. */
    locale: DatePickerLocale;
    /** AM text of the locale. */
    localeAm: string;
    /** PM text of the locale. */
    localePm: string;
    /** Input mask pattern. */
    mask: string;
    /** Whether to clear the input on blur when the mask is incomplete. */
    maskAutoClear: boolean;
    /** Placeholder of the mask. */
    maskSlotChar: string;
    /** Maximum selectable date. */
    maxDate: string | Date | null;
    /** Maximum number of selected dates in multiple selection mode. */
    maxDateCount: number | null;
    /** Minimum selectable date. */
    minDate: string | Date | null;
    /** Whether the month can be selected with a dropdown. */
    monthNavigator: boolean;
    /** Number of displayed months. */
    numberOfMonths: number;
    /** Called when the month changed, with the 1-based month and the year. */
    onMonthChange: ((this: DatePicker, month: number, year: number) => void) | null;
    /** Called when the year changed, with the 0-based month and the year. */
    onYearChange: ((this: DatePicker, month: number, year: number) => void) | null;
    /** Inline style of the panel. */
    panelStyle: string | null;
    /** Style class of the panel. */
    panelStyleClass: string | null;
    /** Called before the panel is shown. */
    preShow: ((this: DatePicker, datePicker: DatePicker) => void) | null;
    /** Separator of the range dates. */
    rangeSeparator: string;
    /** Whether the date picker is readonly. */
    readonly: boolean;
    /** Screen width below which the touch UI is used. */
    responsiveBreakpoint: number;
    /** Whether a single date, multiple dates or a range of dates can be selected. */
    selectionMode: "single" | "multiple" | "range";
    /** Whether dates of other months can be selected. */
    selectOtherMonths: boolean;
    /** Whether to display the today and clear buttons. */
    showButtonBar: boolean;
    /** Whether to show the trigger button. */
    showIcon: boolean;
    /** Whether to display full month names. */
    showLongMonthNames: boolean;
    /** Whether the time picker displays the milliseconds. */
    showMilliseconds: boolean;
    /** Whether to restrict the navigation to min / max date. */
    showMinMaxRange: boolean;
    /** Whether to show the panel when the input receives focus. */
    showOnFocus: boolean;
    /** Whether to display dates of other months. */
    showOtherMonths: boolean;
    /** Whether the time picker displays the seconds. */
    showSeconds: boolean;
    /** Whether to display a time picker. */
    showTime: boolean;
    /** Whether to display week numbers. */
    showWeek: boolean;
    /** Cutoff of 2-digit years, e.g. `+10`. */
    shortYearCutoff: string | number;
    /** Hours to add or subtract per step of the time picker buttons. */
    stepHour: number;
    /** Milliseconds to add or subtract per step of the time picker buttons. */
    stepMillisecond: number;
    /** Minutes to add or subtract per step of the time picker buttons, entered minutes are rounded to this step too. */
    stepMinute: number;
    /** Seconds to add or subtract per step of the time picker buttons. */
    stepSecond: number;
    /** Whether the time is edited with inputs. */
    timeInput: boolean;
    /** Whether to only pick the time. */
    timeOnly: boolean;
    /** Separator of hours, minutes and seconds. */
    timeSeparator: string;
    /** Java time zone of the date picker. */
    timeZone: string | null;
    /** Style class of the today button. */
    todayButtonStyleClass: string;
    /** Whether to display the panel as a modal touch UI. */
    touchUI: boolean;
    /** Locale of the date picker, e.g. `en_US`. */
    userLocale: string | null;
    /** Whether the value is valid. */
    valid: boolean;
    /** `date`, `month` or `week`. */
    view: "date" | "month" | "week";
    /** Initially displayed date. */
    viewDate: string | Date | Date[] | null;
    /** Calculates the week number of a date. */
    weekCalculator: ((date: DateMeta) => number) | null;
    /** `false`, `true` / `input` or `select`. */
    yearNavigator: string;
    /** Range of the year navigator, e.g. `2000:2030`. */
    yearRange: string | null;
}

/**
 * English defaults of the localized texts, merged with the `locale` option and the PrimeFaces locale settings of the
 * `userLocale`.
 */
const DEFAULT_LOCALE: DatePickerLocale = {
    firstDayOfWeek: 0,
    showMonthAfterYear: false,
    dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
    dayNamesShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
    dayNamesMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
    monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
    monthNamesShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
    today: 'Today',
    clear: 'Clear',
    now: 'Now',
    year: 'Year',
    month: 'Month',
    week: 'Week',
    day: 'Day',
    hourText: 'Hour',
    minuteText: 'Minute',
    secondText: 'Second',
    millisecondText: 'Millisecond',
    am: 'AM',
    pm: 'PM',
    chooseDate: "Choose Date",
    prevDecade: "Previous Decade",
    nextDecade: "Next Decade",
    prevYear: "Previous Year",
    nextYear: "Next Year",
    prevMonth: "Previous Month",
    nextMonth: "Next Month",
    prevHour: "Previous Hour",
    nextHour: "Next Hour",
    prevMinute: "Previous Minute",
    nextMinute: "Next Minute",
    prevSecond: "Previous Second",
    nextSecond: "Next Second",
    prevMillisecond: "Previous Millisecond",
    nextMillisecond: "Next Millisecond"
};

/**
 * Default values of the configuration, applied to {@link DatePicker.options} for every option that is `undefined` or
 * `null`.
 */
const DEFAULTS: Omit<DatePickerCfg, Exclude<keyof PrimeFacesTypes.widget.BaseWidgetCfg, "disabled"> | "appendTo" | "locale"> = {
    autoDetectDisplay: true,
    autoMonthFormat: true,
    buttonTabindex: '',
    clearButtonStyleClass: 'ui-priority-secondary',
    dateFormat: 'mm/dd/yy',
    dateStyleClasses: null,
    dateTemplate: null,
    defaultDate: null,
    defaultHour: 0,
    defaultMillisecond: 0,
    defaultMinute: 0,
    defaultSecond: 0,
    disabled: false,
    disabledDates: null,
    disabledDays: null,
    enabledDates: null,
    flex: false,
    focusOnSelect: false,
    fractionSeparator: '.',
    hideOnDateTimeSelect: false,
    hideOnRangeSelection: false,
    hourFormat: '24',
    icon: 'ui-icon ui-icon-calendar',
    inline: false,
    keepInvalid: false,
    lazyModel: false,
    localeAm: '',
    localePm: '',
    mask: '',
    maskAutoClear: true,
    maskSlotChar: '_',
    maxDate: null,
    maxDateCount: null,
    minDate: null,
    monthNavigator: false,
    numberOfMonths: 1,
    onMonthChange: null,
    onYearChange: null,
    panelStyle: null,
    panelStyleClass: null,
    preShow: null,
    rangeSeparator: '-',
    readonly: false,
    responsiveBreakpoint: 576,
    selectionMode: 'single',
    selectOtherMonths: false,
    showButtonBar: false,
    showIcon: false,
    showLongMonthNames: false,
    showMilliseconds: false,
    showMinMaxRange: true,
    showOnFocus: true,
    showOtherMonths: false,
    showSeconds: false,
    showTime: false,
    showWeek: false,
    shortYearCutoff: '+10',
    stepHour: 1,
    stepMillisecond: 1,
    stepMinute: 1,
    stepSecond: 1,
    timeInput: false,
    timeOnly: false,
    timeSeparator: ':',
    timeZone: null,
    todayButtonStyleClass: 'ui-priority-secondary',
    touchUI: false,
    userLocale: null,
    valid: true,
    view: 'date',
    viewDate: null,
    weekCalculator: null,
    yearNavigator: 'false',
    yearRange: null
};

/** Offset of the `!` date format (ticks since 0001-01-01). */
const TICKS_TO_1970 = (((1970 - 1) * 365 + Math.floor(1970 / 4) - Math.floor(1970 / 100) + Math.floor(1970 / 400)) * 24 * 60 * 60 * 10000000);

/**
 * Pads a number with leading zeros (`String.prototype.padStart` is not part of the configured ES2016 lib).
 * @param value Number to pad.
 * @param length Minimum length of the result, `2` by default.
 * @returns The number as a string with at least `length` digits.
 */
function pad0(value: number, length: number = 2): string {
    let text = String(value);
    while (text.length < length) {
        text = '0' + text;
    }
    return text;
}

/**
 * Gets the physical key code of a keyboard event, e.g. `ArrowLeft` or `Enter`.
 * @param event A keyboard event, native or triggered via jQuery.
 * @returns The `code` of the event, or an empty string if it has none.
 */
function eventCode(event: JQuery.TriggeredEvent): string {
    // jQuery copies `code` onto its event, so key events triggered via jQuery work too
    return (event as JQuery.KeyboardEventBase).code || '';
}

/**
 * __PrimeFaces DatePicker Widget__
 *
 * DatePicker is an input component used to select a date featuring display modes, paging, localization, ajax selection
 * and more.
 */
export class DatePicker<Cfg extends DatePickerCfg = DatePickerCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The input with the selected date. */
    input: JQuery = $();
    /** The panel with the calendar. */
    panel: JQuery = $();
    /** The trigger button if using `showIcon`. */
    triggerButton: JQuery | undefined;
    /** A copy of the configuration with the defaults applied, also holds the runtime state (e.g. lazy loaded dates). */
    options!: Cfg;
    /** The selected value: a date, a list of dates (multiple and range selection) or `null`. */
    value: DatePickerValue = null;
    /** The first displayed month. */
    viewDate: Date = new Date();
    /** The view date to restore after an update of a range date picker. */
    viewDateOption: Date | undefined;

    /** The resolved `appendTo` element the overlay panel is appended to, or `null` to keep the panel in the widget. */
    private appendTarget: JQuery | null = null;
    /** Whether a `yearRange` was configured, otherwise it is derived from the view date by `updateYearNavigator`. */
    private hasCustomYearRange: boolean = false;
    /** Metadata of the months currently rendered in the date view, used to map the clicked or focused cells to days. */
    private monthsMetadata: MonthMeta[] = [];
    /** The modal mask of the touch UI while the panel is shown, `null` otherwise. */
    private mask: JQuery | null = null;
    /** Handler of the CSS transition that shows and hides the overlay panel. */
    private transition: PrimeFacesTypes.CssTransitionHandler | null = null;
    /** Listener that hides the overlay on a click outside of the panel, only set while the overlay is shown. */
    private documentClickListener: ((event: JQuery.TriggeredEvent) => void) | null = null;
    /** The scrollable parents of the widget, scrolling one of them hides the overlay. */
    private scrollableParents: (Window | Element)[] = [];
    /** Scroll listener bound to the scrollable parents while the overlay is shown. */
    private scrollableListener: (() => void) | null = null;
    /**
     * Set when a click happened inside the panel or on the input, so the document click listener does not hide the
     * overlay.
     */
    private datepickerClick: boolean = false;
    /**
     * Set while the overlay is shown because the input received focus, so the following click on the input does not
     * hide it again (#11928).
     */
    private datepickerFocus: boolean = false;
    /**
     * Set while the input is focused again after a selection with `focusOnSelect`, so `beforeShow` skips its
     * preparations.
     */
    private refocusInput: boolean = false;
    /**
     * Whether the input is made readonly while the panel is shown, to prevent the virtual keyboard on touch devices
     * with a trigger button.
     */
    private touchEnabled: boolean = false;
    /** The selected value when the input received focus, used to detect changes made via the input mask (#12754). */
    private valueOnFocus: unknown;
    /** Timer that repeats the time change while a time picker button is held down. */
    private timePickerTimer: number | null = null;
    /**
     * The original `onchange` handler of the input, detached from it and called by `onInputChange` after the value was
     * reformatted.
     */
    private changeHandler: ((this: unknown, event: JQuery.TriggeredEvent) => void) | null = null;
    /** Values of the time inputs when they received focus, by unit, restored when an invalid value is entered. */
    private oldTime: Record<string, string> = {};

    /** @deprecated Use {@link input}, kept for callbacks written for the old date picker plugin. */
    get inputfield(): JQuery {
        return this.input;
    }

    /** @deprecated Use {@link jq}, kept for callbacks written for the old date picker plugin. */
    get container(): JQuery {
        return this.jq;
    }

    /** @deprecated Use {@link input}. */
    get jqEl(): JQuery {
        return this.input;
    }

    /**
     * The minimum selectable date, parsed from the `minDate` option on each access so it can be changed at runtime, or
     * `null` if there is none.
     */
    private get minDate(): Date | null {
        return this.parseMinMaxValue(this.options.minDate);
    }

    /**
     * The maximum selectable date, parsed from the `maxDate` option on each access so it can be changed at runtime, or
     * `null` if there is none.
     */
    private get maxDate(): Date | null {
        return this.parseMinMaxValue(this.options.maxDate);
    }

    /** The selected dates of a multiple or range selection, empty if the value is not a list of dates. */
    private get selectedDates(): (Date | null)[] {
        return Array.isArray(this.value) ? this.value : [];
    }

    /**
     * Initializes the widget: applies the defaults to the {@link options}, sets up the locale, input mask and
     * behaviors, parses the initial value and view date and renders the trigger button and panel. The instance is
     * reused when the widget is refreshed.
     * @param cfg The widget configuration from the server.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        // the options are a copy of the configuration with the defaults applied, they also hold the runtime state
        const options: Record<string, unknown> = { ...this.cfg };
        const defaults = DEFAULTS as unknown as Record<string, unknown>;
        for (const key of Object.keys(defaults)) {
            if (options[key] === undefined || options[key] === null) {
                options[key] = defaults[key];
            }
        }
        this.options = options as unknown as Cfg;

        // the instance is reused on refresh
        this.changeHandler = null;
        this.triggerButton = undefined;

        this.input = $(this.jqId + '_input');

        // default date should be input value before widget value
        this.options.defaultDate = String(this.input.val() ?? '') || this.options.defaultDate;
        // #6450 empty separators fall back to the defaults
        this.options.rangeSeparator = this.options.rangeSeparator || '-';
        this.options.timeSeparator = this.options.timeSeparator || ':';
        this.configureLocale();

        this.touchEnabled = PrimeFaces.env.isTouchable(this.cfg) && !this.input.attr('readonly') && this.options.showIcon;

        if (!this.options.inline) {
            this.applyMask(); // must be before datepicker/input see #6445/#7176/#13059
            PrimeFaces.skinInput(this.input);

            if (this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.input,this.cfg.behaviors as unknown as Parameters<typeof PrimeFaces.attachBehaviors>[1]);
            }
        }

        this.options.panelStyleClass = (this.options.panelStyleClass || '') + ' p-datepicker-panel';
        this.options.viewDate = this.options.selectionMode === 'range' ? (this.viewDateOption ?? null) : this.options.defaultDate;

        this.input.addClass('hasDatepicker'); // needed for ui-float-label
        this.setInitValues();
        this.renderDatePicker();
        this.updateResponsiveness();
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        // #11645 trigger view change if lazy model is used
        if (this.options.inline && this.options.lazyModel) {
            this.updateLazyModel();
        }
    }

    /**
     * Unbinds the global listeners of an open overlay and removes the panel appended to another element before the
     * widget is initialized again, e.g. after an AJAX update.
     * @param cfg The new widget configuration from the server.
     */
    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this.unbindOverlayListeners();

        if (this.panel.length && this.cfg.appendTo) {
            const appendTo = PrimeFaces.utils.resolveDynamicOverlayContainer(this);
            PrimeFaces.utils.cleanupDynamicOverlay(this, this.panel, this.id + '_panel', appendTo);
        }

        super.refresh(cfg);
    }

    /**
     * Unbinds the global and responsive listeners, removes the panel appended to another element and the input mask
     * when the widget is destroyed.
     */
    override destroy(): void {
        this.unbindOverlayListeners();

        if (this.panel.length && this.cfg.appendTo) {
            const appendTo = PrimeFaces.utils.resolveDynamicOverlayContainer(this);
            PrimeFaces.utils.removeDynamicOverlay(this, null, this.id + '_panel', appendTo);
        }

        this.unbindResponsiveResizeListener();
        PrimeFaces.utils.cleanseDomElement(this.panel);

        if (this.options.mask) {
            this.input.inputmask('remove');
        }

        // otherwise BaseWidget#destroy removes all handlers of the append target, e.g. the form
        this.appendTarget = null;

        super.destroy();
    }

    /** Unbinds the global listeners of an open overlay right away, the animated hideOverlay unbinds them on exit only. */
    private unbindOverlayListeners(): void {
        this.clearTimePickerTimer();
        this.unbindDocumentClickListener();
        this.unbindWindowResizeListener();
        this.unbindScrollListener();
        this.disableModality();
    }

    // =========================
    // === PrimeFaces widget ===
    // =========================

    /**
     * Builds the localized texts from the English defaults, the `locale` option and the PrimeFaces locale settings of
     * the `userLocale`; with locale settings, the `localeAm` / `localePm` options override the AM / PM texts. The
     * result is stored in the `locale` option.
     */
    private configureLocale(): void {
        const locale: DatePickerLocale = { ...DEFAULT_LOCALE, ...(this.options.locale as Partial<DatePickerLocale> | undefined) };
        const settings = PrimeFaces.getLocaleSettings(this.options.userLocale ?? undefined) as Partial<DatePickerLocale> | undefined;
        if (settings) {
            Object.assign(locale, settings);
            if (this.options.localeAm) {
                locale.am = this.options.localeAm;
            }
            if (this.options.localePm) {
                locale.pm = this.options.localePm;
            }
        }
        this.options.locale = locale;
    }

    /**
     * Applies the `mask` option to the input with Inputmask, using the `datetime` alias if the mask contains date or
     * time tokens. Skipped for inline, readonly and disabled date pickers.
     */
    private applyMask(): void {
        if (this.options.inline || this.input.is('[readonly]') || this.input.is(':disabled') || !this.options.mask) {
            return;
        }

        const maskCfg: Inputmask.Options = {
            placeholder: this.options.maskSlotChar || '_',
            clearMaskOnLostFocus: this.options.maskAutoClear,
            clearIncomplete: this.options.maskAutoClear,
            autoUnmask: false,
            showMaskOnHover: false,
            onBeforePaste: (pastedValue: string) => {
                // GitHub #8319 issue with pasting mask
                PrimeFaces.queueTask(() => { this.input.trigger('input'); }, 20);
                return pastedValue;
            }
        };
        if (/[mdyhs]/i.test(this.options.mask)) {
            maskCfg.alias = 'datetime';
            maskCfg.inputFormat = this.options.mask;
        }
        else {
            maskCfg.mask = this.options.mask;
        }
        this.input.inputmask('remove');
        this.input.inputmask(maskCfg);
        this.input.off('blur'); // GitHub #9259/#12428
    }

    /**
     * Sets up the trigger button rendered for `showIcon`: adds the ARIA label, copies the title and tabindex of
     * the input, skins it and disables it for a disabled date picker.
     */
    private bindTriggerButton(): void {
        const button = this.triggerButton;
        if (!button) {
            return;
        }

        button.attr('aria-label', this.getLabel('chooseDate') ?? '');

        const title = this.input.attr('title');
        if (title) {
            button.attr('title', title);
        }

        const buttonIndex = this.options.buttonTabindex || this.input.attr('tabindex');
        if (buttonIndex) {
            button.attr('tabindex', buttonIndex);
        }

        PrimeFaces.skinButton(button);
        if (this.options.disabled) {
            PrimeFaces.utils.disableButton(button);
        }
    }

    /**
     * Called before the overlay is shown: brings the panel to the front, makes the input readonly on touch devices and
     * calls the `preShow` callback, or otherwise updates a lazy model (#7457). Skipped while the input is focused again
     * after a selection.
     */
    private beforeShow(): void {
        if (this.refocusInput) {
            this.refocusInput = false;
            return;
        }

        PrimeFaces.nextZindex(this.panel);

        // touch support - prevents keyboard popup
        if (this.touchEnabled) {
            this.input.prop('readonly', true);
        }

        if (this.options.preShow) {
            this.options.preShow.call(this, this);
            return;
        }

        // #7457 trigger view change if lazy model is used
        if (this.options.lazyModel) {
            this.updateLazyModel();
        }
    }

    /**
     * Called before the overlay is hidden: makes the input editable again on touch devices and calls the `close`
     * behavior.
     */
    private beforeHide(): void {
        if (this.touchEnabled) {
            this.input.attr('readonly', null);
        }
        const closeBehavior = this.cfg.behaviors?.['close'];
        if (closeBehavior) {
            closeBehavior.call(this);
        }
    }

    /**
     * Called after a date or time was selected: triggers the `change` event of the input and the `dateSelect` behavior
     * (in range selection only once both dates are set, #5830) and focuses the input again with `focusOnSelect`.
     */
    private fireDateSelectEvent(): void {
        this.viewDateOption = this.viewDate;

        // #5830: do not fire in range mode if only the first value is set
        if (this.options.selectionMode !== 'range' || String(this.input.val()).indexOf(this.options.rangeSeparator) !== -1) {
            this.input.trigger('change');
            this.callBehavior('dateSelect');
        }

        if (!this.options.inline && this.options.focusOnSelect) {
            this.refocusInput = true;
            this.input.trigger('focus');
            if (!this.options.showIcon) {
                this.input.off('click.datepicker').on('click.datepicker', () => this.showOverlay());
            }

            PrimeFaces.queueTask(() => {
                this.refocusInput = false;
            }, 10);
        }
    }

    /**
     * Notifies the server that the displayed month changed by calling the `viewChange` behavior. With a lazy model, the
     * panel is disabled during the request and the disabled dates, enabled dates and date style classes of the response
     * are applied; without a `viewChange` behavior a plain AJAX request is sent in that case.
     * @param date The new view date, its year and month are sent as request parameters.
     */
    private fireViewChangeEvent(date: Date): void {
        const lazy = this.options.lazyModel;
        const options: Partial<PrimeFacesTypes.ajax.Configuration> = {
            params: [
                { name: this.id + '_year', value: date.getFullYear() },
                { name: this.id + '_month', value: date.getMonth() }
            ]
        };

        if (lazy) {
            if (!this.panel.parent().is('div.ui-state-disabled')) {
                this.panel.wrap("<div class='ui-state-disabled ui-datepicker-disabled'/>");
            }
            options.onsuccess = (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content: string) => {
                        const dateMetadata: Record<string, { disabled?: boolean; enabled?: boolean; styleClass?: string }> = JSON.parse(content).dateMetadata;
                        const disabledDates: Date[] = [];
                        const enabledDates: Date[] = [];
                        const dateStyleClasses: Record<string, string> = {};
                        for (const key of Object.keys(dateMetadata)) {
                            const metadata = dateMetadata[key]!;
                            const parsedDate = this.parseOptionValue(key) as Date;
                            if (metadata.disabled) {
                                disabledDates.push(parsedDate);
                            }
                            if (metadata.enabled) {
                                enabledDates.push(parsedDate);
                            }
                            if (metadata.styleClass) {
                                dateStyleClasses[this.toISODateString(parsedDate)] = metadata.styleClass;
                            }
                        }
                        this.options.dateStyleClasses = dateStyleClasses;
                        this.setDisabledDates(disabledDates);
                        this.setEnabledDates(enabledDates);
                        if (this.panel.parent().is('div.ui-state-disabled')) {
                            this.panel.unwrap();
                        }

                        // focus the overlay correct item for accessibility
                        this.focusOverlay();
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
            options.source = this.getId();
            options.process = this.getId();
            options.update = this.getId();
            options.formId = this.getParentFormId();
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /** Triggers a viewChange event which updates the lazy model through an AJAX request using the current date. */
    updateLazyModel(): void {
        if (this.options.lazyModel) {
            this.fireViewChangeEvent(this.getViewDate());
        }
    }

    /**
     * Selects the given date, displays its month and fires the same events as a selection in the panel (`change` and
     * `dateSelect`). Passing `null` or an empty string clears the selection without firing events.
     * @param date The date to select, a `Date` or a string in the date (and time) format of the date picker.
     */
    setDate(date: Date | string | null): void {
        if (!date) {
            this.updateModel(null, null);
            return;
        }

        const newDate = this.parseValue(date) as Date;
        this.value = newDate;
        this.updateViewDate(null, newDate);
        this.onDateSelect(null, { day: newDate.getDate(), month: newDate.getMonth(), year: newDate.getFullYear(), selectable: true });
    }

    /**
     * Gets the selected value.
     * @returns The selected date, a list of dates in multiple and range selection (the end of an incomplete range is
     * `null`), the raw text of the input if it could not be parsed, or `null` if nothing is selected.
     */
    getDate(): DatePickerValue {
        return this.value;
    }

    /**
     * Checks whether a single date is selected.
     * @returns `true` if the selected value is a valid `Date`, `false` if nothing or a list of dates is selected.
     */
    hasDate(): boolean {
        return this.isDate(this.getDate());
    }

    /**
     * Displays the month of the given date without changing the selection.
     * @param date The date whose month to display, a `Date` or a string in the date format of the date picker.
     * @param silent `true` to skip the AJAX `viewChange` event, e.g. to avoid a request for a lazy model.
     */
    setViewDate(date: string | Date, silent: boolean = false): void {
        this.updateViewDate(null, this.parseValue(date) as Date, silent);
    }

    /**
     * Gets the date whose month is displayed first in the panel.
     * @returns The current view date.
     */
    getViewDate(): Date {
        return this.viewDate;
    }

    /**
     * Disables the given dates so they cannot be selected anymore and renders the panel again.
     * @param disabledDates The dates to disable, as `Date` or string in the date format, or `null` to enable all dates
     * again.
     */
    setDisabledDates(disabledDates: (string | Date)[] | null): void {
        this.options.disabledDates = disabledDates ? disabledDates.map((date) => this.parseOptionValue(date) as Date) : null;
        this.updatePanel();
    }

    /**
     * Restricts the selectable dates to the given dates and renders the panel again.
     * @param enabledDates The only dates that can be selected, as `Date` or string in the date format. An empty list or
     * `null` keeps the current enabled dates.
     */
    setEnabledDates(enabledDates: (string | Date)[] | null): void {
        if (enabledDates && enabledDates.length > 0) {
            this.options.enabledDates = enabledDates.map((date) => this.parseOptionValue(date) as Date);
        }
        this.updatePanel();
    }

    /**
     * Disables the given week days and renders the panel again.
     * @param disabledDays The week days to disable, 0 = sunday, or `null` to enable all week days again.
     */
    setDisabledDays(disabledDays: number[] | null): void {
        this.options.disabledDays = disabledDays;
        this.updatePanel();
    }

    /** Shows the overlay panel, unless the date picker is inline, disabled or the panel is already shown. */
    show(): void {
        this.showOverlay();
    }

    /** Hides the overlay panel and puts the focus back to the input. */
    hide(): void {
        this.hideOverlay();
    }

    /** Enables the date picker, so the input, the trigger button and the panel can be used again. */
    override enable(): void {
        this.options.disabled = false;
        this.updatePanel();
        PrimeFaces.utils.enableInputWidget(this.input);
        if (this.triggerButton) {
            PrimeFaces.utils.enableButton(this.triggerButton);
        }
    }

    /** Disables the date picker: hides the overlay and disables the input, the trigger button and the panel. */
    override disable(): void {
        this.hide();
        this.options.disabled = true;
        this.updatePanel();
        PrimeFaces.utils.disableInputWidget(this.input, null);
        if (this.triggerButton) {
            PrimeFaces.utils.disableButton(this.triggerButton);
        }
    }

    // =============
    // === Model ===
    // =============

    /**
     * Initializes the runtime state from the options: parses the selected value, the view date (now with the default
     * time if there is none, kept within min / max date), min / max date and the disabled and enabled dates, sets up
     * the default week calculator for `showWeek` and writes the formatted value to the input (#13634).
     */
    private setInitValues(): void {
        const o = this.options;

        if (o.timeOnly) {
            o.showTime = true;
        }

        if (o.showWeek && !o.weekCalculator) {
            o.weekCalculator = this.calculateWeekNumber.bind(this);

            // initialize the potentially missing firstDayWeekOffset option based on the first day of week, the
            // defaults are based on the locales of moment.js
            if (o.locale.firstDayWeekOffset === undefined) {
                const sundayIndex = this.getSundayIndex();
                if (sundayIndex === 0) {
                    o.locale.firstDayWeekOffset = 6;
                }
                else if (sundayIndex === 1) {
                    o.locale.firstDayWeekOffset = 12;
                }
                else if (sundayIndex === 6) {
                    o.locale.firstDayWeekOffset = 4;
                }
                else {
                    o.showWeek = false;
                }
            }
        }

        const parsedDefaultDate = this.parseValue(o.defaultDate);
        const hasMultipleDates = (this.isMultipleSelection() || this.isRangeSelection()) && Array.isArray(parsedDefaultDate);
        let viewDateDefaultsToNow = false;
        let viewDate: DatePickerValue;

        this.value = parsedDefaultDate;
        if (o.viewDate && !hasMultipleDates) {
            viewDate = this.parseValue(o.viewDate) || this.getNow();
        }
        else {
            viewDate = hasMultipleDates ? parsedDefaultDate[0] : parsedDefaultDate;
            if (viewDate === null) {
                viewDate = this.getNow();
                viewDate.setHours(o.defaultHour);
                viewDate.setMinutes(o.defaultMinute);
                viewDate.setSeconds(!o.showSeconds && !o.showMilliseconds ? 0 : o.defaultSecond);
                viewDate.setMilliseconds(!o.showMilliseconds ? 0 : o.defaultMillisecond);
                viewDateDefaultsToNow = true;
            }
        }

        // #6047 round to nearest stepMinute on even if editing using keyboard
        this.viewDate = this.isDate(viewDate) ? new Date(viewDate) : this.getNow();
        this.viewDate.setMinutes(this.stepMinute(this.viewDate.getMinutes()));

        o.minDate = this.parseMinMaxValue(o.minDate);
        o.maxDate = this.parseMinMaxValue(o.maxDate);

        if (viewDateDefaultsToNow) {
            if (this.minDate && this.viewDate < this.minDate) {
                this.viewDate = new Date(this.minDate.getTime());
            }
            if (this.maxDate && this.viewDate > this.maxDate) {
                this.viewDate = new Date(this.maxDate.getTime());
            }
        }

        if (!o.viewDate) {
            o.viewDate = this.viewDate;
        }

        this.hasCustomYearRange = !!o.yearRange;
        this.updateYearNavigator();

        if (o.disabledDates) {
            o.disabledDates = o.disabledDates.map((date) => this.parseOptionValue(date) as Date);
        }
        if (o.enabledDates && o.enabledDates.length > 0) {
            o.enabledDates = o.enabledDates.map((date) => this.parseOptionValue(date) as Date);
        }
        this.bindResponsiveResizeListener();

        // #13634 ensure input is formatted correctly after AJAX update
        let inputValue: unknown = this.getValueToRender();
        if (!inputValue && o.keepInvalid && o.defaultDate) {
            inputValue = o.defaultDate;
        }
        this.input.val(String(inputValue ?? ''));
    }

    /**
     * Parses a date option, e.g. a disabled date, in the date format without time.
     * @param option The option value, a string in the date format or already a `Date`.
     * @returns The parsed date, or the given value if it is not a non-empty string.
     */
    parseOptionValue(option: string | Date | null | undefined): Date | null | undefined {
        if (option && typeof option === 'string') {
            return this.parseDate(option, this.options.dateFormat);
        }
        return option as Date | null | undefined;
    }

    /**
     * Parses the `minDate` or `maxDate` option, which may contain a time.
     * @param option The option value, a string in the date (and time) format or already a `Date`.
     * @returns The parsed date, or `null` if no value is set.
     */
    private parseMinMaxValue(option: string | Date | null | undefined): Date | null {
        if (option && typeof option === 'string') {
            return this.parseDateTime(option);
        }
        return (option as Date | null | undefined) ?? null;
    }

    /**
     * Parses a value as entered in the input: depending on the selection mode a date, a comma separated list of dates
     * or a range of two dates.
     * @param option The value to parse; anything but a non-empty string, e.g. a `Date` or a list of dates, is returned
     * unchanged.
     * @returns The parsed date or dates, or `undefined` if the text is invalid (the error is logged).
     */
    parseValue(option: DatePickerValue): DatePickerValue {
        if (option && typeof option === 'string') {
            return this.parseValueFromString(option);
        }
        return option;
    }

    /**
     * Gets the column of the first day of a month in the date grid, respecting the first day of the week of the locale.
     * @param month 0-based month.
     * @param year Full year.
     * @returns Column index from 0 to 6.
     */
    private getFirstDayOfMonthIndex(month: number, year: number): number {
        const day = this.getNow();
        day.setDate(1);
        day.setMonth(month);
        day.setFullYear(year);

        const dayIndex = day.getDay() + this.getSundayIndex();
        return dayIndex >= 7 ? dayIndex - 7 : dayIndex;
    }

    /**
     * Gets the first day of the week of the locale.
     * @returns 0 = sunday to 6 = saturday.
     */
    private getFirstDayOfWeek(): number {
        return this.options.locale.firstDayOfWeek;
    }

    /**
     * Gets the column of the sundays in the date grid, which depends on the first day of the week.
     * @returns Column index from 0 to 6.
     */
    private getSundayIndex(): number {
        const firstDayOfWeek = this.getFirstDayOfWeek();
        return firstDayOfWeek > 0 ? 7 - firstDayOfWeek : 0;
    }

    /**
     * Gets the column of the saturdays in the date grid, which depends on the first day of the week.
     * @returns Column index from 0 to 6.
     */
    private getSaturdayIndex(): number {
        return 7 - this.getFirstDayOfWeek() - 1;
    }

    /**
     * Gets the number of days of a month.
     * @param month 0-based month.
     * @param year Full year.
     * @returns Number of days, 28 to 31.
     */
    private getDaysCountInMonth(month: number, year: number): number {
        return 32 - this.daylightSavingAdjust(new Date(year, month, 32)).getDate();
    }

    /**
     * Gets the number of days of the month before the given month.
     * @param month 0-based month.
     * @param year Full year.
     * @returns Number of days, 28 to 31.
     */
    private getDaysCountInPrevMonth(month: number, year: number): number {
        const prev = this.getPreviousMonthAndYear(month, year);
        return this.getDaysCountInMonth(prev.month, prev.year);
    }

    /**
     * Moves the hours of a date so that a daylight saving time change cannot move it to another day, ported from the
     * jQuery UI datepicker: hours after 12 are moved 2 hours ahead, all others are set to 0.
     * @param date The date to adjust, modified in place.
     * @returns The given date.
     */
    private daylightSavingAdjust(date: Date): Date {
        date.setHours(date.getHours() > 12 ? date.getHours() + 2 : 0);
        return date;
    }

    /**
     * Gets the month before the given month.
     * @param month 0-based month.
     * @param year Full year.
     * @returns The 0-based month and the year of the previous month.
     */
    private getPreviousMonthAndYear(month: number, year: number): { month: number; year: number } {
        return month === 0 ? { month: 11, year: year - 1 } : { month: month - 1, year };
    }

    /**
     * Gets the month after the given month.
     * @param month 0-based month.
     * @param year Full year.
     * @returns The 0-based month and the year of the next month.
     */
    private getNextMonthAndYear(month: number, year: number): { month: number; year: number } {
        return month === 11 ? { month: 0, year: year + 1 } : { month: month + 1, year };
    }

    /**
     * Orders the given week day names so they start with the first day of the week of the locale.
     * @param dayNames Week day names, starting with sunday.
     * @returns The 7 names, starting with the first day of the week.
     */
    private createWeekDays(dayNames: string[]): string[] {
        const weekDays: string[] = [];
        let dayIndex = this.getFirstDayOfWeek();
        for (let i = 0; i < 7; i++) {
            weekDays.push(dayNames[dayIndex]!);
            dayIndex = dayIndex === 6 ? 0 : dayIndex + 1;
        }
        return weekDays;
    }

    /**
     * Creates the metadata of all displayed months, see `numberOfMonths`.
     * @param month 0-based month of the first displayed month.
     * @param year Year of the first displayed month.
     * @returns The metadata of each displayed month.
     */
    private createMonths(month: number, year: number): MonthMeta[] {
        const months: MonthMeta[] = [];
        for (let i = 0; i < this.options.numberOfMonths; i++) {
            let m = month + i;
            let y = year;
            if (m > 11) {
                y = year + Math.floor(m / 12);
                m = m % 12;
            }
            months.push(this.createMonth(m, y, i));
        }
        return months;
    }

    /**
     * Creates the metadata of a month: its weeks with the days, including the days of the previous and next month that
     * fill the first and last week.
     * @param month 0-based month.
     * @param year Full year.
     * @param index Index of the month among the displayed months.
     * @returns The metadata of the month.
     */
    private createMonth(month: number, year: number, index: number): MonthMeta {
        const dates: DateMeta[][] = [];
        const firstDay = this.getFirstDayOfMonthIndex(month, year);
        const daysLength = this.getDaysCountInMonth(month, year);
        const prevMonthDaysLength = this.getDaysCountInPrevMonth(month, year);
        const today = this.getNow();
        const monthRows = Math.ceil((daysLength + firstDay) / 7);
        const prev = this.getPreviousMonthAndYear(month, year);
        const next = this.getNextMonthAndYear(month, year);
        const meta = (day: number, m: number, y: number, otherMonth: boolean): DateMeta => ({
            day, month: m, year: y, otherMonth: otherMonth || undefined,
            today: this.isToday(today, day, m, y), selectable: this.isSelectable(day, m, y, otherMonth)
        });
        let dayNo = 1;

        for (let i = 0; i < monthRows; i++) {
            const week: DateMeta[] = [];

            if (i === 0) {
                for (let j = prevMonthDaysLength - firstDay + 1; j <= prevMonthDaysLength; j++) {
                    week.push(meta(j, prev.month, prev.year, true));
                }
                const remainingDaysLength = 7 - week.length;
                for (let j = 0; j < remainingDaysLength; j++) {
                    week.push(meta(dayNo, month, year, false));
                    dayNo++;
                }
            }
            else {
                for (let j = 0; j < 7; j++) {
                    week.push(dayNo > daysLength ? meta(dayNo - daysLength, next.month, next.year, true) : meta(dayNo, month, year, false));
                    dayNo++;
                }
            }

            dates.push(week);
        }

        return { month, year, dates, index };
    }

    /**
     * Checks whether a day can be selected: it must be within min / max date, not disabled (or enabled if enabled dates
     * are set), not on a disabled week day and not a day of another month unless `selectOtherMonths` is set.
     * @param day Day of the month.
     * @param month 0-based month.
     * @param year Full year.
     * @param otherMonth Whether the day belongs to the previous or next month of the displayed month.
     * @returns Whether the day can be selected.
     */
    private isSelectable(day: number, month: number, year: number, otherMonth: boolean): boolean {
        const compare = (date: Date) => (date.getFullYear() - year) || (date.getMonth() - month) || (date.getDate() - day);
        const validMin = !this.minDate || compare(this.minDate) <= 0;
        const validMax = !this.maxDate || compare(this.maxDate) >= 0;
        let validDate = true;

        if (this.options.disabledDates) {
            validDate = !this.isDateDisabled(day, month, year);
        }
        if (this.options.enabledDates) {
            validDate = this.isDateEnabled(day, month, year);
        }

        const validDay = !this.options.disabledDays || !this.isDayDisabled(day, month, year);
        const validMonth = !(this.options.selectOtherMonths === false && otherMonth);

        return validMin && validMax && validDate && validDay && validMonth;
    }

    /**
     * Checks whether a day is selected: it equals the selected date or one of the selected dates, is within the
     * selected range, or is in the selected week in the week view.
     * @param dateMeta The day to check.
     * @returns Whether the day is selected.
     */
    private isSelected(dateMeta: DateMeta): boolean {
        if (!this.value) {
            return false;
        }

        if (this.options.view === 'week') {
            const currentDate = this.selectedDates[0]!;
            const weekCalculator = this.options.weekCalculator!;
            return weekCalculator({ day: currentDate.getDate(), month: currentDate.getMonth(), year: currentDate.getFullYear() }) === weekCalculator(dateMeta);
        }

        if (this.isSingleSelection()) {
            return this.isDateEquals(this.value, dateMeta);
        }
        if (this.isMultipleSelection()) {
            return this.selectedDates.some((date) => this.isDateEquals(date, dateMeta));
        }
        if (this.isRangeSelection()) {
            return this.isInRange(dateMeta);
        }
        return false;
    }

    /**
     * Checks whether a day is the start or the end of the selected range, or between them.
     * @param dateMeta The day to check.
     * @returns Whether the day is within the selected range, only the start counts while the range is incomplete.
     */
    private isInRange(dateMeta: DateMeta): boolean {
        const [start, end] = this.selectedDates;
        return end
            ? this.isDateEquals(start, dateMeta) || this.isDateEquals(end, dateMeta) || this.isDateBetween(start, end, dateMeta)
            : this.isDateEquals(start, dateMeta);
    }

    /**
     * Checks whether a month of the displayed year is selected in the month view, or within the selected range.
     * @param month 0-based month.
     * @returns Whether the month is selected.
     */
    private isMonthSelected(month: number): boolean {
        if (!this.value) {
            return false;
        }
        if (this.isRangeSelection()) {
            return this.isInRange({ year: this.viewDate.getFullYear(), month, day: 1, selectable: true });
        }
        return this.isDate(this.value) && this.value.getMonth() === month && this.value.getFullYear() === this.viewDate.getFullYear();
    }

    /**
     * Checks whether a date is on the given day, ignoring the time.
     * @param value The date to compare, any other value never matches.
     * @param dateMeta The day to compare with.
     * @returns Whether both are on the same day.
     */
    private isDateEquals(value: unknown, dateMeta: DateMeta): boolean {
        return this.isDate(value) && value.getDate() === dateMeta.day && value.getMonth() === dateMeta.month && value.getFullYear() === dateMeta.year;
    }

    /**
     * Checks whether a day is within a date range, including its start and end.
     * @param start Start of the range.
     * @param end End of the range.
     * @param dateMeta The day to check.
     * @returns Whether the day is within the range, `false` if start or end is no valid date.
     */
    private isDateBetween(start: unknown, end: unknown, dateMeta: DateMeta): boolean {
        if (this.isDate(start) && this.isDate(end)) {
            const date = new Date(dateMeta.year, dateMeta.month, dateMeta.day);
            return start.getTime() <= date.getTime() && end.getTime() >= date.getTime();
        }
        return false;
    }

    /**
     * Checks whether the `selectionMode` is `single`.
     * @returns Whether only a single date can be selected.
     */
    private isSingleSelection(): boolean {
        return this.options.selectionMode === 'single';
    }

    /**
     * Checks whether the `selectionMode` is `range`.
     * @returns Whether a range of dates can be selected.
     */
    private isRangeSelection(): boolean {
        return this.options.selectionMode === 'range';
    }

    /**
     * Checks whether the `selectionMode` is `multiple`.
     * @returns Whether multiple dates can be selected.
     */
    private isMultipleSelection(): boolean {
        return this.options.selectionMode === 'multiple';
    }

    /**
     * Checks whether a day is today.
     * @param today The current date, see {@link getNow}.
     * @param day Day of the month.
     * @param month 0-based month.
     * @param year Full year.
     * @returns Whether the day is today.
     */
    private isToday(today: Date, day: number, month: number, year: number): boolean {
        return today.getDate() === day && today.getMonth() === month && today.getFullYear() === year;
    }

    /**
     * Checks whether a day is one of the `disabledDates`.
     * @param day Day of the month.
     * @param month 0-based month.
     * @param year Full year.
     * @returns Whether the day is disabled.
     */
    private isDateDisabled(day: number, month: number, year: number): boolean {
        return (this.options.disabledDates as Date[] | null ?? [])
            .some((date) => date.getFullYear() === year && date.getMonth() === month && date.getDate() === day);
    }

    /**
     * Checks whether a day is one of the `enabledDates`.
     * @param day Day of the month.
     * @param month 0-based month.
     * @param year Full year.
     * @returns Whether the day is enabled, always `true` if no enabled dates are set.
     */
    private isDateEnabled(day: number, month: number, year: number): boolean {
        const enabledDates = this.options.enabledDates as Date[] | null;
        if (enabledDates && enabledDates.length > 0) {
            return enabledDates.some((date) => date.getFullYear() === year && date.getMonth() === month && date.getDate() === day);
        }
        return true;
    }

    /**
     * Checks whether a day falls on one of the `disabledDays` of the week.
     * @param day Day of the month.
     * @param month 0-based month.
     * @param year Full year.
     * @returns Whether the week day of the day is disabled.
     */
    private isDayDisabled(day: number, month: number, year: number): boolean {
        return !!this.options.disabledDays && this.options.disabledDays.indexOf(new Date(year, month, day).getDay()) !== -1;
    }

    /**
     * Formats the selected value for the input: a single date, comma separated dates, or the dates of a range separated
     * by the `rangeSeparator`, followed by the week number in the week view.
     * @returns The formatted value, an empty string if nothing is selected, or the raw value if it cannot be formatted.
     */
    private getValueToRender(): string {
        let formattedValue = '';
        if (!this.value) {
            return formattedValue;
        }

        // a raw text that could not be parsed fails in formatDate and is kept by the catch
        try {
            if (this.isRangeSelection()) {
                const dates = this.value as (Date | null)[];
                if (dates.length) {
                    const startDate = dates[0]!;
                    const endDate = dates[1];

                    formattedValue = this.formatDateTime(startDate) ?? '';
                    if (endDate) {
                        formattedValue += ' ' + this.options.rangeSeparator + ' ' + this.formatDateTime(endDate);
                    }

                    if (this.options.view === 'week' && this.options.showWeek && this.options.weekCalculator) {
                        const week = this.options.weekCalculator({ day: startDate.getDate(), month: startDate.getMonth(), year: startDate.getFullYear() });
                        formattedValue += ' (' + this.options.locale.weekHeader + ' ' + week + ')';
                    }
                }
            }
            else if (this.isSingleSelection()) {
                formattedValue = this.formatDateTime(this.value as Date) ?? '';
            }
            else if (this.isMultipleSelection()) {
                formattedValue = (this.value as Date[]).map((date) => this.formatDateTime(date)).join(', ');
            }
        }
        catch (err) {
            formattedValue = String(this.value);
        }

        return formattedValue;
    }

    /**
     * Formats a date with the date format of the date picker, followed by the time if `showTime` is set, or only the
     * time with `timeOnly`.
     * @param date The date to format.
     * @returns The formatted date and time, or `null` if no date is given.
     */
    formatDateTime(date: Date | null | undefined): string | null {
        if (!date) {
            return null;
        }
        if (this.options.timeOnly) {
            return this.formatTime(date);
        }
        let formattedValue = this.formatDate(date, this.options.dateFormat);
        if (this.options.showTime) {
            formattedValue += ' ' + this.formatTime(date);
        }
        return formattedValue;
    }

    /**
     * Formats a date with the given format, ported from the jQuery UI datepicker. Supports `d` / `dd` (day of month),
     * `o` / `oo` (day of year), `D` / `DD` (day name), `m` / `mm` (month), `M` / `MM` (month name), `y` / `yy` (2 /
     * 4-digit year), `@` (Unix timestamp in ms), `!` (Windows ticks) and literal text in single quotes.
     * @param date The date to format, or a day of the calendar.
     * @param format The date format, e.g. `mm/dd/yy`.
     * @returns The formatted date, or an empty string if no date is given.
     */
    formatDate(date: Date | DateMeta | null | undefined, format: string): string {
        if (!date) {
            return '';
        }
        // a day of the calendar (DateMeta); other values that are not a Date, e.g. an unparsable text typed into the
        // input, must still fail, so getValueToRender keeps the text
        if (!(date instanceof Date) && typeof date === 'object' && 'day' in date) {
            date = new Date(date.year, date.month, date.day);
        }

        const locale = this.options.locale;
        let iFormat = 0;
        const lookAhead = (match: string) => {
            const matches = iFormat + 1 < format.length && format.charAt(iFormat + 1) === match;
            if (matches) {
                iFormat++;
            }
            return matches;
        };
        const formatNumber = (match: string, value: number, len: number) => lookAhead(match) ? pad0(value, len) : String(value);
        const formatName = (match: string, value: number, shortNames: string[], longNames: string[]) =>
            (lookAhead(match) ? longNames[value] : shortNames[value]) ?? '';

        let output = '';
        let literal = false;
        for (iFormat = 0; iFormat < format.length; iFormat++) {
            const char = format.charAt(iFormat);
            if (literal) {
                if (char === "'" && !lookAhead("'")) {
                    literal = false;
                }
                else {
                    output += char;
                }
                continue;
            }

            switch (char) {
                case 'd':
                    output += formatNumber('d', date.getDate(), 2);
                    break;
                case 'D':
                    output += formatName('D', date.getDay(), locale.dayNamesShort, locale.dayNames);
                    break;
                case 'o':
                    output += formatNumber('o', Math.round((new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime()
                        - new Date(date.getFullYear(), 0, 0).getTime()) / 86400000), 3);
                    break;
                case 'm':
                    output += formatNumber('m', date.getMonth() + 1, 2);
                    break;
                case 'M':
                    output += formatName('M', date.getMonth(), locale.monthNamesShort, locale.monthNames);
                    break;
                case 'y': {
                    const year = date.getFullYear();
                    output += lookAhead('y') ? year : (year % 100 < 10 ? '0' : '') + (year % 100);
                    break;
                }
                case '@':
                    output += date.getTime();
                    break;
                case '!':
                    output += date.getTime() * 10000 + TICKS_TO_1970;
                    break;
                case "'":
                    if (lookAhead("'")) {
                        output += "'";
                    }
                    else {
                        literal = true;
                    }
                    break;
                default:
                    output += char;
            }
        }
        return output;
    }

    /**
     * Formats the time of a date with the time format of the date picker, i.e. its `hourFormat`, `timeSeparator`,
     * `fractionSeparator` and the seconds and milliseconds if they are shown.
     * @param date The date whose time to format.
     * @returns The formatted time, e.g. `14:30` or `02:30 PM`, or an empty string if no date is given.
     */
    formatTime(date: Date | null | undefined): string {
        if (!date) {
            return '';
        }

        const o = this.options;
        let hours = date.getHours();

        if (o.hourFormat === '12' && hours > 11 && hours !== 12) {
            hours -= 12;
        }

        let output = o.hourFormat === '12' && hours === 0 ? '12' : pad0(hours);
        output += o.timeSeparator + pad0(date.getMinutes());

        if (o.showSeconds) {
            output += o.timeSeparator + pad0(date.getSeconds());
        }
        if (o.showMilliseconds) {
            output += o.fractionSeparator + pad0(date.getMilliseconds(), 3);
        }
        if (o.hourFormat === '12') {
            output += ' ' + (date.getHours() > 11 ? o.locale.pm : o.locale.am);
        }

        return output;
    }

    /**
     * Parses a time in the time format of the date picker.
     * @param value The time to parse, e.g. `14:30`.
     * @param ampm The AM / PM text of the locale, for the 12 hour format.
     * @returns The parsed hour (0 to 23), minute, second and millisecond.
     * @throws If the time has the wrong number of parts or a value is out of range.
     */
    private parseTime(value: string, ampm: string | null | undefined): { hour: number; minute: number; second: number; millisecond: number } {
        const o = this.options;
        const tokens = value.replace(o.fractionSeparator, o.timeSeparator).split(o.timeSeparator);
        const showSeconds = o.showSeconds || o.showMilliseconds;
        const validTokenLength = 2 + (showSeconds ? 1 : 0) + (o.showMilliseconds ? 1 : 0);

        if (tokens.length !== validTokenLength) {
            throw "Invalid time";
        }

        let h = parseInt(tokens[0]!, 10);
        const m = parseInt(tokens[1]!, 10);
        const s = showSeconds ? parseInt(tokens[2]!, 10) : 0;
        const ms = o.showMilliseconds ? parseInt(tokens[3]!, 10) : 0;

        if (isNaN(h) || isNaN(m) || h > 23 || m > 59 || (o.hourFormat === '12' && h > 12)
                || (o.showSeconds && (isNaN(s) || s > 59)) || (o.showMilliseconds && (isNaN(ms) || ms > 999))) {
            throw "Invalid time";
        }

        if (o.hourFormat === '12' && h !== 12 && ampm === o.locale.pm) {
            h += 12;
        }
        else if (o.hourFormat === '12' && h === 12 && ampm === o.locale.am) {
            h -= 12;
        }

        return { hour: h, minute: m, second: s, millisecond: ms };
    }

    /**
     * Parses a date with the given format, ported from the jQuery UI datepicker. Supports the same tokens as
     * {@link formatDate}, 2-digit years are expanded using the `shortYearCutoff`.
     * @param text The text to parse.
     * @param format The date format, e.g. `mm/dd/yy`.
     * @returns The parsed date without time, or `null` for an empty text.
     * @throws If the text or format is missing, or the text does not match the format or is no valid date.
     */
    parseDate(text: string | null | undefined, format: string): Date | null {
        if (format == null || text == null) {
            throw "Invalid arguments";
        }

        const value = String(text);
        if (value === "") {
            return null;
        }

        const o = this.options;
        const shortYearCutoff = typeof o.shortYearCutoff !== "string" ? o.shortYearCutoff
            : this.getNow().getFullYear() % 100 + parseInt(o.shortYearCutoff, 10);
        let iFormat = 0;
        let iValue = 0;
        let year = -1;
        let month = -1;
        let day = -1;
        let doy = -1;
        let literal = false;

        const lookAhead = (match: string) => {
            const matches = iFormat + 1 < format.length && format.charAt(iFormat + 1) === match;
            if (matches) {
                iFormat++;
            }
            return matches;
        };
        const getNumber = (match: string) => {
            const isDoubled = lookAhead(match);
            const size = match === "@" ? 14 : match === "!" ? 20 : match === "y" && isDoubled ? 4 : match === "o" ? 3 : 2;
            const minSize = match === "y" ? size : 1;
            let num = value.substring(iValue).match(new RegExp("^\\d{" + minSize + "," + size + "}"));
            if (!num && match === "y" && isDoubled) {
                // allow 2 digit-inputs for 4-digit-year-pattern (gets reformated to 4 digits onBlur)
                num = value.substring(iValue).match(new RegExp("^\\d{2," + size + "}"));
            }
            if (!num) {
                throw "Missing number at position " + iValue;
            }
            iValue += num[0].length;
            return parseInt(num[0], 10);
        };
        const getName = (match: string, shortNames: string[], longNames: string[]) => {
            const names = (lookAhead(match) ? longNames : shortNames)
                .map((name, index) => ({ name, index }))
                .sort((a, b) => b.name.length - a.name.length);

            for (const { name, index } of names) {
                if (value.substr(iValue, name.length).toLowerCase() === name.toLowerCase()) {
                    iValue += name.length;
                    return index + 1;
                }
            }
            throw "Unknown name at position " + iValue;
        };
        const checkLiteral = () => {
            if (value.charAt(iValue) !== format.charAt(iFormat)) {
                throw "Unexpected literal at position " + iValue;
            }
            iValue++;
        };

        if (o.view === 'month') {
            day = 1;
        }

        for (iFormat = 0; iFormat < format.length; iFormat++) {
            if (literal) {
                if (format.charAt(iFormat) === "'" && !lookAhead("'")) {
                    literal = false;
                }
                else {
                    checkLiteral();
                }
                continue;
            }

            switch (format.charAt(iFormat)) {
                case "d":
                    day = getNumber("d");
                    break;
                case "D":
                    getName("D", o.locale.dayNamesShort, o.locale.dayNames);
                    break;
                case "o":
                    doy = getNumber("o");
                    break;
                case "m":
                    month = getNumber("m");
                    break;
                case "M":
                    month = getName("M", o.locale.monthNamesShort, o.locale.monthNames);
                    break;
                case "y":
                    year = getNumber("y");
                    break;
                case "@": {
                    const date = new Date(getNumber("@"));
                    year = date.getFullYear();
                    month = date.getMonth() + 1;
                    day = date.getDate();
                    break;
                }
                case "!": {
                    const date = new Date((getNumber("!") - TICKS_TO_1970) / 10000);
                    year = date.getFullYear();
                    month = date.getMonth() + 1;
                    day = date.getDate();
                    break;
                }
                case "'":
                    if (lookAhead("'")) {
                        checkLiteral();
                    }
                    else {
                        literal = true;
                    }
                    break;
                default:
                    checkLiteral();
            }
        }

        if (iValue < value.length) {
            const extra = value.substr(iValue);
            if (!/^\s+/.test(extra)) {
                throw "Extra/unparsed characters found in date: " + extra;
            }
        }

        if (year === -1) {
            year = this.getNow().getFullYear();
        }
        else if (year < 100) {
            year += this.getNow().getFullYear() - this.getNow().getFullYear() % 100 + (year <= shortYearCutoff ? 0 : -100);
        }

        if (doy > -1) {
            month = 1;
            day = doy;
            for (;;) {
                const dim = this.getDaysCountInMonth(month - 1, year);
                if (day <= dim) {
                    break;
                }
                month++;
                day -= dim;
            }
        }

        const date = this.daylightSavingAdjust(new Date(year, month - 1, day));
        if (date.getFullYear() !== year || date.getMonth() + 1 !== month || date.getDate() !== day) {
            throw "Invalid date"; // E.g. 31/02/00
        }

        return date;
    }

    /**
     * Parses the text of the input depending on the selection mode: a single date, comma separated dates, or a range
     * separated by the `rangeSeparator`. Errors are logged, not thrown.
     * @param text The text to parse.
     * @returns The parsed date or dates, `null` for an empty text, or `undefined` if the text is invalid.
     */
    private parseValueFromString(text: string): DatePickerValue {
        if (!text || text.trim().length === 0) {
            return null;
        }

        let value: DatePickerValue;
        try {
            if (this.isSingleSelection()) {
                value = this.parseDateTime(text);
            }
            else if (this.isMultipleSelection()) {
                value = text.split(',').map((token) => this.parseDateTime(token.trim()));
            }
            else if (this.isRangeSelection()) {
                const separator = this.options.rangeSeparator;
                value = text.split(new RegExp(separator + '| ' + separator + ' ', 'g')).map((token) => this.parseDateTime(token.trim()));
            }
        }
        catch (error) {
            PrimeFaces.error("DatePicker Error: " + error);
        }

        return value;
    }

    /**
     * Parses a single date with the date format, followed by the time if `showTime` is set; with `timeOnly`, the parsed
     * time is applied to the current date.
     * @param text The text to parse, e.g. `10/24/2026 14:30`.
     * @returns The parsed date, or `null` if the date text is empty.
     * @throws If the date or the time is invalid.
     */
    private parseDateTime(text: string): Date | null {
        const o = this.options;
        const parts = text.split(' ');
        let date: Date | null;

        if (o.timeOnly) {
            date = this.getNow();
            this.populateTime(date, parts[0] ?? '', parts[1]);
        }
        else if (o.showTime) {
            let ampm = o.hourFormat === '12' ? parts.pop() : null;
            let timeString = parts.pop() ?? '';

            // #9559 some locales are "a. m." with a space
            if (/\d/.test(timeString) === false) {
                ampm = timeString + ' ' + ampm;
                timeString = parts.pop() ?? '';
            }

            date = this.parseDate(parts.join(' '), o.dateFormat);
            if (date) {
                this.populateTime(date, timeString, ampm);
            }
        }
        else {
            date = this.parseDate(text, o.dateFormat);
        }

        return date;
    }

    /**
     * Applies a time in the time format of the date picker to a date, rounding the minute to the `stepMinute` and
     * dropping seconds and milliseconds that are not shown.
     * @param value The date to modify in place.
     * @param timeString The time to parse.
     * @param ampm The AM / PM text of the locale, for the 12 hour format.
     * @throws If the time or the AM / PM text is invalid.
     */
    private populateTime(value: Date, timeString: string, ampm: string | null | undefined): void {
        const o = this.options;
        if (o.hourFormat === '12' && ampm !== o.locale.pm && ampm !== o.locale.am) {
            throw new Error('Invalid Time');
        }

        const time = this.parseTime(timeString, ampm);
        value.setHours(time.hour);
        value.setMinutes(this.stepMinute(time.minute));
        value.setSeconds(o.showSeconds || o.showMilliseconds ? time.second : 0);
        value.setMilliseconds(o.showMilliseconds ? time.millisecond : 0);
    }

    /**
     * Checks whether the displayed year is the year of the min date.
     * @returns `true` if a min date is set and the view date is in its year.
     */
    private isInMinYear(): boolean {
        return !!this.minDate && this.minDate.getFullYear() === this.viewDate.getFullYear();
    }

    /**
     * Checks whether the displayed year is the year of the max date.
     * @returns `true` if a max date is set and the view date is in its year.
     */
    private isInMaxYear(): boolean {
        return !!this.maxDate && this.maxDate.getFullYear() === this.viewDate.getFullYear();
    }

    // ==============
    // === Render ===
    // ==============

    /**
     * Renders the trigger button and the panel, appends the panel to the widget or to the `appendTo` target (removing
     * an old panel there), binds the events, registers the CSS transition of the overlay, sets up the trigger button and
     * marks the widget as overlay target. Called on init and when switching between the touch UI and the overlay.
     */
    private renderDatePicker(): void {
        const o = this.options;

        if (o.showIcon && !o.inline) {
            this.triggerButton?.remove();
            this.renderTriggerButton();
            this.jq.append(this.triggerButton!).addClass('ui-trigger-calendar');
        }

        this.panel.remove();
        this.renderDatePickerPanel();

        if (o.panelStyleClass) {
            this.panel.addClass(o.panelStyleClass);
        }
        if (o.panelStyle) {
            this.panel.attr('style', o.panelStyle);
        }

        if (!o.inline && this.appendTarget?.length) {
            // remove old overlay first, see PrimeFaces.utils.appendDynamicOverlay
            $(this.appendTarget).children("[id='" + this.id + "_panel']").not(this.panel).remove();
            this.panel.appendTo(this.appendTarget);
        }
        else {
            this.panel.appendTo(this.jq);
        }

        this.setInitOptionValues();
        this.bindEvents();
        this.bindPanelEvents();

        this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');

        this.bindTriggerButton();

        // mark target and descendants of target as a trigger for a PrimeFaces overlay
        if (!o.inline) {
            this.jq.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
        }
    }

    /**
     * Syncs the month and year navigators of the rendered panel with the view date and updates the state of the
     * navigator buttons (#6379).
     */
    private setInitOptionValues(): void {
        if (this.isYearNavigator()) {
            let year = this.viewDate.getFullYear();
            let month = this.viewDate.getMonth();
            this.panel.find('.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-year').each((_, yearElt) => {
                $(yearElt).val(year);
                month = month + 1;
                if (month === 12) {
                    month = 0;
                    year = year + 1;
                }
            });
        }

        if (this.options.monthNavigator && this.options.view !== 'month') {
            let month = this.viewDate.getMonth();
            this.panel.find('.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-month').each((_, monthElt) => {
                $(monthElt).val(month);
                month = month === 11 ? 0 : month + 1;
            });
        }

        // #6379 set state of navigator buttons
        this.setNavigationState(this.viewDate);
    }

    /**
     * Creates the trigger button for `showIcon` and stores it in {@link triggerButton}, the caller appends it to the
     * widget.
     */
    private renderTriggerButton(): void {
        const aria = ' aria-haspopup="dialog" aria-expanded="false" aria-controls="' + this.id + '_panel" ';
        this.triggerButton = $('<button type="button" ' + aria + ' class="ui-datepicker-trigger ui-button ui-widget ui-state-default ui-button-icon-only'
            + (this.options.disabled ? ' ui-state-disabled' : '') + '" tabindex="0">'
            + '<span class="ui-button-icon-left ' + this.options.icon + '"></span>'
            + '<span class="ui-button-text">ui-button</span>'
            + '</button>');
    }

    /**
     * Creates the panel element with its content and its state classes, stores it in {@link panel} and resolves the
     * `appendTo` target the overlay panel is appended to. An inline panel is not positioned (#8423).
     */
    private renderDatePickerPanel(): void {
        const o = this.options;
        const classes = this.getClassesToAdd({
            'ui-datepicker-inline': o.inline,
            'ui-input-overlay': !o.inline,
            'ui-state-disabled': o.disabled,
            'ui-state-error': o.inline && !o.valid,
            'ui-datepicker-timeonly': o.timeOnly,
            'ui-datepicker-multiple-month': o.numberOfMonths > 1,
            'ui-datepicker-monthpicker': o.view === 'month',
            'ui-datepicker-touch-ui': o.touchUI
        });

        this.panel = $('<div id="' + this.id + '_panel" role="dialog" aria-modal="true" aria-label="' + o.locale.chooseDate + '"'
            + ' class="ui-datepicker ui-widget ui-widget-content' + classes + '"></div>');
        this.panel[0]!.innerHTML = this.renderPanelElements();
        this.panel.css({
            'display': o.inline ? 'block' : 'none',
            'position': o.inline || o.touchUI ? '' : 'absolute'
        });

        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this as unknown as Parameters<typeof PrimeFaces.utils.resolveAppendTo>[0], this.jq, this.panel) ?? null;
        // #8423
        if (o.inline) {
            this.panel.css('position', '');
        }
        this.appendTarget = this.cfg.appendTo
            ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.appendTo)
            : null;
    }

    /**
     * Renders the inner elements of the panel depending on the options: the date or month view, the time picker and the
     * button bar. Also updates the disabled state class of the panel.
     * @returns The HTML of the panel content.
     */
    renderPanelElements(): string {
        const o = this.options;
        let html = '';

        this.panel.toggleClass('ui-state-disabled', o.disabled);

        if (!o.timeOnly) {
            if (o.view === 'date' || o.view === 'week') {
                html += this.renderDateView();
            }
            else if (o.view === 'month') {
                html += this.renderMonthView();
            }
        }
        if (o.showTime || o.timeOnly) {
            html += this.renderTimePicker();
        }
        if (o.showButtonBar) {
            html += this.renderButtonBar();
        }

        return html;
    }

    /**
     * Creates the metadata of the displayed months and renders them.
     * @returns The HTML of the date view.
     */
    private renderDateView(): string {
        this.monthsMetadata = this.createMonths(this.viewDate.getMonth(), this.viewDate.getFullYear());
        return this.monthsMetadata.map((month, i) => this.renderMonth(month, i)).join('');
    }

    /**
     * Renders the month view: a header with the year navigation and the 12 months of the displayed year.
     * @returns The HTML of the month view.
     */
    private renderMonthView(): string {
        let months = '';
        for (let i = 0; i <= 11; i++) {
            months += this.renderMonthViewMonth(i);
        }

        return '<div class="ui-datepicker-header ui-widget-header">'
            + this.renderNavigator('prev', this.options.locale.prevYear)
            + this.renderNavigator('next', this.options.locale.nextYear)
            + '<div class="ui-datepicker-title">' + this.renderTitleYearElement(this.viewDate.getFullYear(), 0) + '</div>'
            + '</div>'
            + '<div class="ui-monthpicker">' + months + '</div>';
    }

    /**
     * Renders the time picker with the hour, minute, second, millisecond and AM / PM pickers, depending on the options.
     * @returns The HTML of the time picker.
     */
    private renderTimePicker(): string {
        const o = this.options;
        return '<div class="ui-timepicker ui-widget-header' + (o.timeInput ? ' ui-timepicker-timeinput' : '') + '">'
            + this.renderHourPicker()
            + this.renderTimeElements('ui-divider', '<span>:</span>', -1)
            + this.renderMinutePicker()
            + (o.showSeconds ? this.renderTimeElements('ui-divider', '<span>:</span>', -1) : '')
            + this.renderSecondPicker()
            + (o.showMilliseconds ? this.renderTimeElements('ui-divider', '<span>.</span>', -1) : '')
            + this.renderMillisecondPicker()
            + this.renderAmPmPicker()
            + '</div>';
    }

    /**
     * Renders the button bar with the today (or now) and clear buttons. The today button is hidden if today is not
     * within min / max date.
     * @returns The HTML of the button bar.
     */
    private renderButtonBar(): string {
        const o = this.options;
        const grid = o.flex ? 'grid' : 'ui-g';
        const today = o.flex ? 'col-6 text-left' : 'ui-g-6';
        const clear = o.flex ? 'col-6 text-right' : 'ui-g-6';
        let todayStyleClass = 'ui-today-button ui-button ui-widget ui-state-default ui-button-text-only ' + o.todayButtonStyleClass;
        // only use date at 00:00 for comparison without time
        const now = o.showTime ? this.getNow() : this.truncateDate(this.getNow());

        if ((this.minDate && this.minDate > now) || (this.maxDate && this.maxDate < now)) {
            todayStyleClass += ' p-hidden';
        }

        return '<div class="ui-datepicker-buttonbar ui-widget-header">'
            + '<div class="' + grid + '">'
            + '<div class="' + today + '">'
            + '<button type="button" class="' + todayStyleClass + '"><span class="ui-button-text">' + (o.showTime ? o.locale.now : o.locale.today) + '</span></button>'
            + '</div>'
            + '<div class="' + clear + '">'
            + '<button type="button" class="ui-clear-button ui-button ui-widget ui-state-default ui-button-text-only ' + o.clearButtonStyleClass + '">'
            + '<span class="ui-button-text">' + o.locale.clear + '</span></button>'
            + '</div>'
            + '</div>'
            + '</div>';
    }

    /**
     * Renders a month of the month view, disabled if it is not within min / max date.
     * @param index 0-based month.
     * @returns The HTML of the month.
     */
    private renderMonthViewMonth(index: number): string {
        const o = this.options;
        const monthName = (o.showLongMonthNames ? o.locale.monthNames[index] : o.locale.monthNamesShort[index]) ?? '';
        const content = o.dateTemplate ? o.dateTemplate.call(this, monthName) : PrimeFaces.escapeHTML(monthName);
        const compareDate = new Date(this.viewDate.getFullYear(), index, 1);
        const disabled = !!((this.minDate && this.minDate > compareDate) || (this.maxDate && this.maxDate < compareDate));
        const monthClass = this.getClassesToAdd({
            'ui-state-active': this.isMonthSelected(index),
            'ui-state-disabled': disabled
        });

        return '<a role="button" tabindex="0" class="ui-monthpicker-month' + monthClass + '" aria-disabled="' + disabled + '">' + content + '</a>';
    }

    /**
     * Renders a month of the date view with its header and date grid; the first month gets the backward and the last
     * month the forward navigator.
     * @param monthMetadata The month to render.
     * @param index Index of the month among the displayed months.
     * @returns The HTML of the month.
     */
    private renderMonth(monthMetadata: MonthMeta, index: number): string {
        const o = this.options;
        const backwardNavigator = index === 0 ? this.renderNavigator('prev', o.locale.prevMonth) : '';
        const forwardNavigator = o.numberOfMonths === 1 || index === o.numberOfMonths - 1 ? this.renderNavigator('next', o.locale.nextMonth) : '';

        return '<div class="ui-datepicker-group ui-widget-content">'
            + '<div class="ui-datepicker-header ui-widget-header">'
            + backwardNavigator
            + forwardNavigator
            + this.renderTitle(monthMetadata)
            + '</div>'
            + this.renderDateViewGrid(monthMetadata, this.createWeekDays(o.locale.dayNamesMin), this.createWeekDays(o.locale.dayNames))
            + '</div>';
    }

    /**
     * Renders a button that navigates to the previous or next month, or year in the month view.
     * @param type Whether the button navigates backward (`prev`) or forward (`next`).
     * @param ariaLabel Accessible label of the button.
     * @returns The HTML of the button.
     */
    private renderNavigator(type: 'prev' | 'next', ariaLabel: string): string {
        return '<button type="button" aria-label="' + ariaLabel + '" class="ui-datepicker-' + type + '" tabindex="0">'
            + '<span class="ui-icon ui-icon-circle-triangle-' + (type === 'prev' ? 'w' : 'e') + '"></span>'
            + '</button>';
    }

    /**
     * Renders the month of a header: a dropdown for the first displayed month with `monthNavigator`, otherwise the
     * month name.
     * @param month 0-based month.
     * @param index Index of the month among the displayed months.
     * @returns The HTML of the month element.
     */
    private renderTitleMonthElement(month: number, index: number): string {
        const o = this.options;
        if (o.monthNavigator && o.view !== 'month' && index === 0) {
            const monthNames = o.showLongMonthNames ? o.locale.monthNames : o.locale.monthNamesShort;
            return '<select class="ui-datepicker-month" tabindex="0" aria-label="' + o.locale.month + '">' + this.renderTitleOptions('month', monthNames, month) + '</select>';
        }
        return '<span class="ui-datepicker-month">' + PrimeFaces.escapeHTML(o.locale.monthNames[month] ?? '') + '</span>';
    }

    /**
     * Renders the year of a header: for the first displayed month with a year navigator a number input or a dropdown
     * limited to the year range and min / max date, otherwise the plain year.
     * @param year The year to render.
     * @param index Index of the month among the displayed months.
     * @returns The HTML of the year element.
     */
    private renderTitleYearElement(year: number, index: number): string {
        const o = this.options;
        if (!this.isYearNavigator() || index !== 0) {
            return '<span class="ui-datepicker-year">' + year + '</span>';
        }

        this.updateYearNavigator();
        const years = (o.yearRange ?? '').split(':');
        const yearStart = parseInt(years[0] ?? '', 10);
        const yearEnd = parseInt(years[1] ?? '', 10);
        const minYear = this.minDate ? Math.max(this.minDate.getFullYear(), yearStart) : yearStart;
        const maxYear = this.maxDate ? Math.min(this.maxDate.getFullYear(), yearEnd) : yearEnd;

        if (this.isYearNavigatorInput()) {
            return '<input class="ui-datepicker-year" size="6" maxlength="4" tabindex="0" aria-label="' + o.locale.year + '" type="number" min="'
                + minYear + '" max="' + maxYear + '" step="1" value="' + year + '"></input>';
        }

        const yearOptions: number[] = [];
        for (let i = yearStart; i <= yearEnd; i++) {
            yearOptions.push(i);
        }
        return '<select class="ui-datepicker-year" tabindex="0" aria-label="' + o.locale.year + '">' + this.renderTitleOptions('year', yearOptions, year) + '</select>';
    }

    /**
     * Renders the options of the month or year dropdown, restricted to min / max date with `showMinMaxRange`.
     * @param name Whether the options are months or years.
     * @param options The month names or the years.
     * @param current The selected month index or year.
     * @returns The HTML of the options.
     */
    private renderTitleOptions(name: 'month' | 'year', options: (string | number)[], current: number): string {
        const showMinMaxRange = this.options.showMinMaxRange;
        let html = '';

        options.forEach((option, i) => {
            if (name === 'month') {
                if (!showMinMaxRange || ((!this.isInMinYear() || i >= this.minDate!.getMonth()) && (!this.isInMaxYear() || i <= this.maxDate!.getMonth()))) {
                    html += '<option value="' + i + '"' + (i === current ? ' selected' : '') + '>' + PrimeFaces.escapeHTML(String(option)) + '</option>';
                }
            }
            else {
                const year = option as number;
                if (!showMinMaxRange || (!(this.minDate && this.minDate.getFullYear() > year) && !(this.maxDate && this.maxDate.getFullYear() < year))) {
                    html += '<option value="' + year + '"' + (year === current ? ' selected' : '') + '>' + year + '</option>';
                }
            }
        });

        return html;
    }

    /**
     * Renders the title of a month header with the month and the year, ordered according to `showMonthAfterYear` of the
     * locale.
     * @param monthMetadata The month to render.
     * @returns The HTML of the title.
     */
    private renderTitle(monthMetadata: MonthMeta): string {
        const month = this.renderTitleMonthElement(monthMetadata.month, monthMetadata.index);
        const year = this.renderTitleYearElement(monthMetadata.year, monthMetadata.index);
        const content = this.options.locale.showMonthAfterYear ? year + '&#xa0;' + month : month + '&#xa0;' + year;
        return '<div class="ui-datepicker-title">' + content + '</div>';
    }

    /**
     * Renders the column headers of the date grid with the week day names, preceded by the week number header with
     * `showWeek`.
     * @param weekDaysMin Minimal week day names, displayed as header.
     * @param weekDays Full week day names, used as title of the headers.
     * @returns The HTML of the header cells.
     */
    private renderDayNames(weekDaysMin: string[], weekDays: string[]): string {
        const o = this.options;
        const colOffset = o.showWeek ? 1 : 0;
        let html = '';

        if (o.showWeek) {
            html += '<th scope="col" aria-colindex="1" abbr="' + o.locale.weekHeader + '"><span>' + o.locale.weekHeader + '</span></th>';
        }
        weekDaysMin.forEach((weekDayMin, i) => {
            // screen readers announce the abbr as column header, so it holds the full week day name
            const weekDay = PrimeFaces.escapeHTML(weekDays[i] ?? '');
            html += '<th scope="col" aria-colindex="' + (i + 1 + colOffset) + '" abbr="' + weekDay + '">'
                + '<span title="' + weekDay + '">' + weekDayMin + '</span>'
                + '</th>';
        });

        return html;
    }

    /**
     * Calculates the offset of the start of the first week to the start of the year (start-of-first-week -
     * start-of-year), based on the implementation of moment.js.
     * @param year Full year.
     * @param dow First day of the week, 0 = sunday.
     * @param doy The `firstDayWeekOffset` of the locale, determines which day of january is always in the first week.
     * @returns The offset in days, may be negative.
     */
    private firstWeekOffset(year: number, dow: number, doy: number): number {
        // first-week day -- which january is always in the first week (4 for iso, 1 for other)
        const fwd = 7 + dow - doy;
        // first-week day local weekday -- which local weekday is fwd
        const fwdlw = (7 + new Date(Date.UTC(year, 0, fwd)).getUTCDay() - dow) % 7;
        return -fwdlw + fwd - 1;
    }

    /**
     * Calculates the day of the year of a day.
     * @param d The day.
     * @returns The 1-based day of the year.
     */
    private dayOfYear(d: DateMeta): number {
        return Math.round((new Date(d.year, d.month, d.day).getTime() - new Date(d.year, 0, 0).getTime()) / 86400000);
    }

    /**
     * Gets the number of days of a year.
     * @param year Full year.
     * @returns 366 for a leap year, otherwise 365.
     */
    private daysInYear(year: number): number {
        return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0 ? 366 : 365;
    }

    /**
     * Calculates the number of weeks of a year, based on the implementation of moment.js.
     * @param year Full year.
     * @param dow First day of the week, 0 = sunday.
     * @param doy The `firstDayWeekOffset` of the locale.
     * @returns The number of weeks, 52 or 53.
     */
    private weeksInYear(year: number, dow: number, doy: number): number {
        return (this.daysInYear(year) - this.firstWeekOffset(year, dow, doy) + this.firstWeekOffset(year + 1, dow, doy)) / 7;
    }

    /**
     * Default week calculator for `showWeek`, calculates the week of the year according to the first day of the week
     * and the `firstDayWeekOffset` of the locale.
     * @param d The day.
     * @returns The week number.
     */
    private calculateWeekNumber(d: DateMeta): number {
        const firstDayOfWeek = this.getFirstDayOfWeek();
        const doy = this.options.locale.firstDayWeekOffset ?? 6;
        const week = Math.floor((this.dayOfYear(d) - this.firstWeekOffset(d.year, firstDayOfWeek, doy) - 1) / 7) + 1;

        if (week < 1) {
            return week + this.weeksInYear(d.year - 1, firstDayOfWeek, doy);
        }
        if (week > this.weeksInYear(d.year, firstDayOfWeek, doy)) {
            return week - this.weeksInYear(d.year, firstDayOfWeek, doy);
        }
        return week;
    }

    /**
     * Renders the cells of a week row: the week number with `showWeek` and the days with their state classes and ARIA
     * attributes.
     * @param weekDates The 7 days of the week.
     * @returns The HTML of the cells.
     */
    private renderWeek(weekDates: DateMeta[]): string {
        const o = this.options;
        let html = '';

        if (o.showWeek && o.weekCalculator) {
            const firstDate = weekDates[0]!;
            const lastDate = weekDates[6]!;
            const cellClass = firstDate.otherMonth && lastDate.otherMonth && !o.showOtherMonths ? ' ui-datepicker-other-month-hidden' : '';
            html += '<td aria-colindex="1" class="ui-datepicker-weeknumber' + cellClass + '"><span class="ui-state-disabled">' + o.weekCalculator(firstDate) + '</span></td>';
        }

        const saturdayIndex = this.getSaturdayIndex();
        const sundayIndex = this.getSundayIndex();
        weekDates.forEach((date, i) => {
            const disabled = !date.selectable;
            const selected = this.isSelected(date);
            const hidden = !!date.otherMonth && !o.showOtherMonths;
            const cellClass = this.getClassesToAdd({
                'ui-datepicker-other-month': !!date.otherMonth,
                'ui-datepicker-today': !!date.today,
                'ui-datepicker-week-end': i === sundayIndex || i === saturdayIndex,
                'ui-datepicker-other-month-hidden': hidden
            }).trim();
            const dateClass = this.getClassesToAdd({
                'ui-state-default': true,
                'ui-state-active': selected,
                'ui-state-disabled': disabled,
                'ui-state-highlight': !!date.today
            }).trim();
            const dateLabel = PrimeFaces.escapeHTML(this.formatDate(new Date(date.year, date.month, date.day), o.dateFormat));

            // the focusable <a> carries the full date label so screen readers announce it while navigating;
            // cells without a link (disabled) keep the label on the cell itself, hidden cells stay unlabeled
            html += '<td role="gridcell" aria-colindex="' + (i + 1 + (o.showWeek ? 1 : 0)) + '"'
                + (cellClass ? ' class="' + cellClass + '"' : '')
                + (disabled ? ' aria-disabled="true"' : '')
                + (selected ? ' aria-selected="true"' : '')
                + (date.selectable || hidden ? '' : ' aria-label="' + dateLabel + '"') + '>'
                + this.renderDateCellContent(date, dateClass, dateLabel)
                + '</td>';
        });

        return html;
    }

    /**
     * Renders the content of a day cell using the `dateTemplate` and the `dateStyleClasses`: a focusable link for a
     * selectable day, otherwise a span.
     * @param date The day.
     * @param dateClass Style classes of the day.
     * @param dateLabel Accessible label of the day, its formatted date.
     * @returns The HTML of the cell content.
     */
    private renderDateCellContent(date: DateMeta, dateClass: string, dateLabel: string): string {
        const content = this.options.dateTemplate ? this.options.dateTemplate.call(this, date) : date.day;
        const styleClass = this.options.dateStyleClasses?.[this.toISODateString(new Date(date.year, date.month, date.day))];
        if (styleClass) {
            dateClass += ' ' + styleClass;
        }
        if (date.selectable) {
            // role button, as an <a> without href has no role and its aria-label is ignored by screen readers
            return '<a role="button" tabindex="0" class="' + dateClass + '"' + (dateLabel ? ' aria-label="' + dateLabel + '"' : '') + '>' + content + '</a>';
        }
        return '<span class="' + dateClass + '">' + content + '</span>';
    }

    /**
     * Renders the date grid of a month: a table with the week day headers and a row per week.
     * @param monthMetadata The month to render.
     * @param weekDaysMin Minimal week day names, starting with the first day of the week.
     * @param weekDays Full week day names, starting with the first day of the week.
     * @returns The HTML of the grid.
     */
    private renderDateViewGrid(monthMetadata: MonthMeta, weekDaysMin: string[], weekDays: string[]): string {
        return '<div class="ui-datepicker-calendar-container">'
            + '<table class="ui-datepicker-calendar" role="grid">'
            + '<thead><tr>' + this.renderDayNames(weekDaysMin, weekDays) + '</tr></thead>'
            + '<tbody>' + monthMetadata.dates.map((week) => '<tr>' + this.renderWeek(week) + '</tr>').join('') + '</tbody>'
            + '</table>'
            + '</div>';
    }

    /**
     * Gets the date whose time is displayed in the time picker.
     * @returns The selected date if a single date is selected, otherwise the view date.
     */
    private getTimeSource(): Date {
        return this.isDate(this.value) ? this.value : this.viewDate;
    }

    /**
     * Renders a number input of the time picker, used with `timeInput`.
     * @param value The current value.
     * @param min The minimum value.
     * @param max The maximum value.
     * @param label Accessible label of the input.
     * @param size Size and maximum length of the input.
     * @returns The HTML of the input.
     */
    private renderTimeInput(value: string | number, min: number, max: number, label: string, size: number): string {
        return '<input type="number" min="' + min + '" max="' + max + '" value="' + value + '" aria-label="' + label + '" size="' + size
            + '" maxlength="' + size + '" tabindex="0" class="ui-inputfield"></input>';
    }

    /**
     * Renders the hour picker, converting the hour for the 12 hour format.
     * @returns The HTML of the hour picker.
     */
    private renderHourPicker(): string {
        const o = this.options;
        let hour = this.getTimeSource().getHours();
        let maxHour = 23;

        if (o.hourFormat === '12') {
            if (hour === 0) {
                hour = 12;
                maxHour = 12;
            }
            else if (hour > 11 && hour !== 12) {
                hour = hour - 12;
            }
        }

        const display = pad0(hour);
        return this.renderTimeElements('ui-hour-picker', o.timeInput ? this.renderTimeInput(display, 0, maxHour, o.locale.hourText, 2) : '<span>' + display + '</span>', 0);
    }

    /**
     * Renders the minute picker.
     * @returns The HTML of the minute picker.
     */
    private renderMinutePicker(): string {
        const o = this.options;
        const display = pad0(this.getTimeSource().getMinutes());
        return this.renderTimeElements('ui-minute-picker', o.timeInput ? this.renderTimeInput(display, 0, 59, o.locale.minuteText, 2) : '<span>' + display + '</span>', 1);
    }

    /**
     * Renders the second picker if `showSeconds` is set.
     * @returns The HTML of the second picker, or an empty string.
     */
    private renderSecondPicker(): string {
        const o = this.options;
        if (!o.showSeconds) {
            return '';
        }
        const display = pad0(this.getTimeSource().getSeconds());
        return this.renderTimeElements('ui-second-picker', o.timeInput ? this.renderTimeInput(display, 0, 59, o.locale.secondText, 2) : '<span>' + display + '</span>', 2);
    }

    /**
     * Renders the millisecond picker if `showMilliseconds` is set.
     * @returns The HTML of the millisecond picker, or an empty string.
     */
    private renderMillisecondPicker(): string {
        const o = this.options;
        if (!o.showMilliseconds) {
            return '';
        }
        const display = pad0(this.getTimeSource().getMilliseconds(), 3);
        return this.renderTimeElements('ui-millisecond-picker', o.timeInput ? this.renderTimeInput(display, 0, 999, o.locale.millisecondText, 3) : '<span>' + display + '</span>', 3);
    }

    /**
     * Renders the AM / PM picker for the 12 hour format.
     * @returns The HTML of the AM / PM picker, or an empty string for the 24 hour format.
     */
    private renderAmPmPicker(): string {
        const o = this.options;
        if (o.hourFormat !== '12') {
            return '';
        }
        const display = this.getTimeSource().getHours() > 11 ? o.locale.pm : o.locale.am;
        return this.renderTimeElements('ui-ampm-picker', '<span>' + display + '</span>', 4);
    }

    /**
     * Renders an element of the time picker with an up and a down button around the given content.
     * @param containerClass Style class of the element, e.g. `ui-hour-picker`.
     * @param text The HTML content between the buttons.
     * @param type Type of the element, stored as `data-type` and used for the button labels: 0 = hour, 1 = minute, 2 =
     * second, 3 = millisecond, 4 = AM / PM, -1 = divider.
     * @returns The HTML of the element.
     */
    private renderTimeElements(containerClass: string, text: string, type: number): string {
        const locale = this.options.locale;
        const upLabels = [locale.nextHour, locale.nextMinute, locale.nextSecond, locale.nextMillisecond, locale.am];
        const downLabels = [locale.prevHour, locale.prevMinute, locale.prevSecond, locale.prevMillisecond, locale.pm];
        const button = (direction: 'up' | 'down', label: string | undefined, icon: string) =>
            '<button type="button" aria-label="' + (label ?? '') + '" class="ui-picker-' + direction + '" tabindex="0"><span class="ui-icon ' + icon + '"></span></button>';

        return '<div class="' + containerClass + '" data-type="' + type + '">'
            + button('up', upLabels[type], 'ui-icon-carat-1-n')
            + text
            + button('down', downLabels[type], 'ui-icon-carat-1-s')
            + '</div>';
    }

    /**
     * Joins the names of the enabled style classes.
     * @param classes The style classes, mapped to whether to add them.
     * @returns The enabled style classes, each with a leading space.
     */
    private getClassesToAdd(classes: Record<string, boolean>): string {
        return Object.keys(classes).filter((key) => classes[key]).map((key) => ' ' + key).join('');
    }

    /**
     * Formats the local date of a date as ISO date, e.g. `2026-10-24`, as used for the keys of the `dateStyleClasses`.
     * @param date The date to format.
     * @returns The ISO date without time.
     */
    toISODateString(date: Date): string {
        return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString().substring(0, 10);
    }

    // ==============
    // === Events ===
    // ==============

    /**
     * Binds the event handlers of the input, the trigger button and the panel. The input handlers are bound with a
     * delay so the CSP event handlers are registered first (#13269), the original `onchange` handler of the input is
     * moved to `changeHandler`. Nothing is bound for a readonly date picker (#12385).
     */
    private bindEvents(): void {
        if (this.options.readonly) {
            // #12385 readonly input should not allow any events
            return;
        }

        if (!this.options.inline) {
            // #13269 delay registering events until CSP has registered
            PrimeFaces.queueTask(() => {
                const input = this.input[0];
                // the input was removed meanwhile, e.g. by an AJAX update
                if (!input?.isConnected) {
                    return;
                }
                if (!this.changeHandler) {
                    // get the current attached events if using CSP, use DOM if non-CSP and JQ event if CSP
                    // @ts-expect-error Internal JQuery method
                    const events = $._data(input, 'events');
                    let originalOnchange = this.input.prop('onchange') as ((event: JQuery.TriggeredEvent) => void) | null;
                    if (!originalOnchange && events?.change) {
                        originalOnchange = events.change[0]?.handler ?? null;
                    }
                    this.input.prop('onchange', null).off('change');
                    this.changeHandler = (event) => originalOnchange?.call(this, event);
                }

                this.input.off('.datePicker')
                    .on('focus.datePicker', () => this.onInputFocus())
                    .on('blur.datePicker', (e) => this.onInputBlur(e))
                    .on('change.datePicker', (e) => this.onInputChange(e))
                    .on('keydown.datePicker', (e) => this.onInputKeyDown(e))
                    .on('input.datePicker', (e) => this.onUserInput(e))
                    .on('click.datePicker', () => this.onInputClick());
            }, 1);

            this.triggerButton?.off('click.datePicker-triggerButton').on('click.datePicker-triggerButton', () => {
                if (this.isPanelVisible()) {
                    this.hideOverlay();
                }
                else {
                    this.showOverlay();
                }
            });
        }

        const $this = this;
        const panel = this.panel.off('.datePicker');
        const header = '.ui-datepicker-header';
        const yearSelector = header + ' > .ui-datepicker-title > .ui-datepicker-year';
        const dateMeta = (element: Element) => {
            const dayEl = $(element);
            const calendarIndex = dayEl.closest('.ui-datepicker-group').index();
            const weekIndex = dayEl.closest('tr').index();
            const dayIndex = dayEl.closest('td').index() - (this.options.showWeek ? 1 : 0);
            return this.monthsMetadata[calendarIndex]?.dates[weekIndex]?.[dayIndex];
        };

        panel.on('click.datePicker', () => {
            if (this.documentClickListener) {
                this.datepickerClick = true;
            }
        })
        .on('keydown.datePicker', (e) => {
            if (e.key === 'Escape') {
                e.preventDefault();
                e.stopPropagation();
                this.hideOverlay();
            }
        })
        .on('click.datePicker', header + ' > .ui-datepicker-prev', (e) => this.navBackward(e))
        .on('click.datePicker', header + ' > .ui-datepicker-next', (e) => this.navForward(e))
        .on('change.datePicker', header + ' > .ui-datepicker-title > .ui-datepicker-month', (e) => this.onMonthDropdownChange(e))
        .on('change.datePicker', yearSelector, (e) => this.onYearInputChange(e));

        if (this.isYearNavigatorInput()) {
            panel.on('keydown.datePicker', yearSelector, (e) => this.onTimeInputKeyDown(e))
                .on('keyup.datePicker', yearSelector, (e) => this.onTimeInputKeyUp(e));
        }

        const monthSelector = '.ui-monthpicker > .ui-monthpicker-month';
        panel.on('click.datePicker', monthSelector, function(e) {
            $this.onDateSelect(e, { year: $this.viewDate.getFullYear(), month: $(this).index(), day: 1, selectable: true });
            e.preventDefault();
        })
        .on('keydown.datePicker', monthSelector, function(e) {
            $this.onMonthKeyDown(e, $(this).index());
        });

        const timeSelector = '.ui-hour-picker > button, .ui-minute-picker > button, .ui-second-picker > button, .ui-millisecond-picker > button';
        panel.on('mousedown.datePicker keydown.datePicker', timeSelector, function(e) {
            const button = $(this);
            if (!e.key || PrimeFaces.utils.isActionKey(e)) {
                $this.onTimePickerElementMouseDown(e, parseInt(button.parent().data('type'), 10), button.hasClass('ui-picker-up') ? 1 : -1);
            }
        })
        .on('mouseup.datePicker keyup.datePicker', timeSelector, (e) => this.onTimePickerElementMouseUp(e))
        // mouseleave, mouseout would also fire when the pointer moves between the button and its icon
        .on('mouseleave.datePicker', timeSelector, (e) => {
            if (this.timePickerTimer) {
                this.onTimePickerElementMouseUp(e);
            }
        })
        .on('click.datePicker', '.ui-ampm-picker > button', (e) => this.toggleAmPm(e));

        if (this.options.timeInput) {
            for (const [unit, picker] of [['hour', 'ui-hour-picker'], ['minute', 'ui-minute-picker'], ['second', 'ui-second-picker'], ['millisecond', 'ui-millisecond-picker']] as const) {
                const selector = '.' + picker + ' input';
                panel.on('focus.datePicker', selector, function() {
                    $this.oldTime[unit] = (this as HTMLInputElement).value;
                })
                .on('change.datePicker', selector, function(e) {
                    $this.handleTimeInput(unit, this as HTMLInputElement, e);
                })
                .on('keydown.datePicker', selector, (e) => this.onTimeInputKeyDown(e))
                .on('keyup.datePicker', selector, (e) => this.onTimeInputKeyUp(e));
            }
        }

        panel.on('click.datePicker', '.ui-datepicker-buttonbar .ui-today-button', (e) => this.onTodayButtonClick(e))
            .on('click.datePicker', '.ui-datepicker-buttonbar .ui-clear-button', (e) => this.onClearButtonClick(e));

        const dateSelector = '.ui-datepicker-calendar td a';
        panel.on('click.datePicker', dateSelector, function(e) {
            const meta = dateMeta(this);
            if (meta) {
                $this.onDateSelect(e, meta);
            }
        })
        .on('keydown.datePicker', dateSelector, function(e) {
            const meta = dateMeta(this);
            if (meta) {
                $this.onDateKeyDown(e, meta);
            }
        });
    }

    /**
     * Highlights the hovered and the selected week in the week view, called whenever the panel content was rendered.
     */
    private bindPanelEvents(): void {
        if (this.options.view === 'week') {
            // highlight the whole week
            this.panel.find('.ui-datepicker-calendar tr')
                .on('mousemove', function() {
                    $(this).find('td a').addClass('ui-state-hover');
                })
                .on('mouseleave', function() {
                    $(this).find('td a').removeClass('ui-state-hover');
                });

            // highlight the active week
            this.panel.find('.ui-state-active').closest('tr').find('td a').addClass('ui-state-active');
        }
    }

    /**
     * Moves the focus within the days or months of the panel with the arrow keys, navigating to the previous or next
     * month (or year) at the edges; PageUp and PageDown navigate directly.
     * @param event The keydown event.
     * @param rowSize Number of items per row, 7 days or 3 months.
     * @returns `true` if an arrow key was handled and its default action should be prevented.
     */
    private moveFocus(event: JQuery.TriggeredEvent, rowSize: number): boolean {
        const tabbables = this.panel.find('a:tabbable');
        const currentIndex = tabbables.index($(event.currentTarget));
        const focusAt = (index: number) => tabbables.eq(index).trigger('focus');

        switch (eventCode(event)) {
            case 'ArrowLeft':
                focusAt((currentIndex - 1) % tabbables.length);
                return true;
            case 'ArrowRight':
                focusAt((currentIndex + 1) % tabbables.length);
                return true;
            case 'ArrowDown': {
                const nextIndex = (currentIndex + rowSize) % tabbables.length;
                if (nextIndex < currentIndex) {
                    this.focusNextInterval(event);
                }
                else {
                    focusAt(nextIndex);
                }
                return true;
            }
            case 'ArrowUp': {
                const nextIndex = (currentIndex - rowSize) % tabbables.length;
                if (nextIndex < 0) {
                    this.focusPreviousInterval(event);
                }
                else {
                    focusAt(nextIndex);
                }
                return true;
            }
            case 'PageUp':
                this.focusPreviousInterval(event);
                return false;
            case 'PageDown':
                this.focusNextInterval(event);
                return false;
        }
        return false;
    }

    /**
     * Handles the keyboard on a day: Enter and Space select it, Home and End focus the first or last day of the week,
     * the arrow and page keys move the focus.
     * @param event The keydown event.
     * @param dateMeta The focused day.
     */
    private onDateKeyDown(event: JQuery.TriggeredEvent, dateMeta: DateMeta): void {
        if (this.options.disabled || !dateMeta.selectable) {
            event.preventDefault();
            return;
        }

        switch (eventCode(event)) {
            case 'Enter':
            case 'NumpadEnter':
            case 'Space':
                this.onDateSelect(event, dateMeta);
                event.preventDefault();
                return;
            case 'Home':
                this.focusDate($(event.currentTarget).closest('tr'), 'a:visible:first');
                event.preventDefault();
                return;
            case 'End':
                this.focusDate($(event.currentTarget).closest('tr'), 'a:visible:last');
                event.preventDefault();
                return;
        }

        if (this.moveFocus(event, 7)) {
            event.preventDefault();
        }
    }

    /**
     * Handles the keyboard on a month of the month view: Enter and Space select it, Home and End focus the first or
     * last month, the arrow and page keys move the focus.
     * @param event The keydown event.
     * @param index 0-based month of the focused month.
     */
    private onMonthKeyDown(event: JQuery.TriggeredEvent, index: number): void {
        if (this.options.disabled) {
            event.preventDefault();
            return;
        }

        switch (eventCode(event)) {
            case 'Enter':
            case 'NumpadEnter':
            case 'Space':
                this.onDateSelect(event, { year: this.viewDate.getFullYear(), month: index, day: 1, selectable: true });
                event.preventDefault();
                return;
            case 'Home':
                this.focusDate(this.panel, 'a:visible:first');
                event.preventDefault();
                return;
            case 'End':
                this.focusDate(this.panel, 'a:visible:last');
                event.preventDefault();
                return;
        }

        if (this.moveFocus(event, 3)) {
            event.preventDefault();
        }
    }

    /**
     * Restricts the keys of the time inputs and the year input to digits, navigation keys and clipboard shortcuts.
     * Enter in a 2-digit time input hides the overlay, a digit in a full year input clears it first. Skipped on
     * Android.
     * @param event The keydown event.
     */
    private onTimeInputKeyDown(event: JQuery.TriggeredEvent): void {
        if (this.options.disabled) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }
        if (PrimeFaces.env.android) {
            return;
        }

        const input = event.currentTarget as HTMLInputElement;
        const key = event.key ?? '';

        if (input.maxLength === 2 && key === 'Enter') {
            this.hideOverlay();
            // not to the form, e.g. its default command
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        // allow text selection and cut, copy, paste
        if (event.ctrlKey && ['a', 'c', 'v', 'x', 'z'].includes(key)) {
            return;
        }

        // allow navigation keys, control keys, and numeric keys
        const isNumericKey = /^[0-9]$/.test(key);
        if (!isNumericKey && !['ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'Backspace', 'Delete', 'End', 'Home', 'Shift', 'Tab'].includes(key)) {
            event.preventDefault();
        }

        // if input is for year and already full, reset value
        if (isNumericKey && input.maxLength === 4 && input.value.length === 4) {
            input.value = '';
        }
    }

    /**
     * Removes all non-digits from a time input or the year input and clamps its value to its min / max; a year is only
     * checked once it has 4 digits.
     * @param event The keyup event.
     */
    private onTimeInputKeyUp(event: JQuery.TriggeredEvent): void {
        const input = event.currentTarget as HTMLInputElement;
        let newValue = input.value.replace(/[^0-9]/g, '');

        // for year input, only evaluate if the input is 4 digits long
        if (input.maxLength === 4 && newValue.length < 4) {
            return;
        }

        if (newValue.length > input.maxLength) {
            newValue = newValue.substring(0, input.maxLength);
        }

        // reset to the closest valid value
        const inputMin = parseInt(input.min, 10);
        const inputMax = parseInt(input.max, 10);
        if (newValue && parseInt(newValue, 10) < inputMin) {
            newValue = String(inputMin);
        }
        else if (newValue && parseInt(newValue, 10) > inputMax) {
            newValue = String(inputMax);
        }

        input.value = newValue;
    }

    /**
     * Focuses the element matching the selector within the given element.
     * @param jq The element to search in.
     * @param selector Selector of the element to focus, e.g. `a:visible:first`.
     */
    private focusDate(jq: JQuery, selector: string): void {
        jq.find(selector).trigger('focus');
    }

    /**
     * Navigates to the next month (or year in the month view) and focuses its first item.
     * @param event The keyboard event that triggered the navigation.
     */
    private focusNextInterval(event: JQuery.TriggeredEvent): void {
        this.navForward(event);
        this.focusDate(this.panel, 'a:visible:first');
    }

    /**
     * Navigates to the previous month (or year in the month view) and focuses its last item.
     * @param event The keyboard event that triggered the navigation.
     */
    private focusPreviousInterval(event: JQuery.TriggeredEvent): void {
        this.navBackward(event);
        this.focusDate(this.panel, 'a:visible:last');
    }

    /**
     * Handles a click on the input: hides the shown overlay unless it was just shown by the focus (#11928), otherwise
     * shows it with `showOnFocus` (#12361).
     */
    private onInputClick(): void {
        if (this.documentClickListener) {
            this.datepickerClick = true;
        }

        if (this.isPanelVisible()) {
            // #11928 allow the input to be clicked again to close panel and allow typing of date
            if (!this.datepickerFocus) {
                this.hideOverlay();
            }
        }
        else if (this.options.showOnFocus && !this.options.showIcon) {
            // #12361 allow the input to be clicked again to open the panel if showOnFocus is true
            this.showOverlay();
        }
    }

    /**
     * Handles the focus of the input: shows the overlay with `showOnFocus` (unless a trigger button is used), adds the
     * focus styles and remembers the value to detect a change on blur.
     */
    private onInputFocus(): void {
        if (this.options.showOnFocus && !this.isPanelVisible() && !this.datepickerFocus && !this.options.showIcon) {
            this.datepickerFocus = true;
            this.showOverlay();
        }
        this.input.addClass('ui-state-focus');
        this.jq.addClass('ui-inputwrapper-focus');
        this.valueOnFocus = this.getDate();
    }

    /**
     * Handles the blur of the input: removes the focus styles and, if a mask is used, handles a changed value like a
     * change of the input (#12754).
     * @param event The blur event, if any.
     */
    private onInputBlur(event?: JQuery.TriggeredEvent): void {
        this.input.removeClass('ui-state-focus');
        this.jq.removeClass('ui-inputwrapper-focus');

        // #12754 if mask is used, fire the change event
        if (this.options.mask && this.valueOnFocus !== this.getDate()) {
            this.valueOnFocus = undefined;
            this.onInputChange(event);
        }
    }

    /**
     * Handles the change of the input: keeps the displayed month within min / max date, reformats the input unless
     * `keepInvalid` is set and calls the original `onchange` handler of the input.
     * @param event The change event, if any.
     */
    private onInputChange(event?: JQuery.TriggeredEvent): void {
        this.alignViewMonth();

        if (!this.options.keepInvalid) {
            this.input.val(this.getValueToRender());
        }

        this.changeHandler?.(event as JQuery.TriggeredEvent);
    }

    /**
     * Handles the keyboard on the input: ArrowDown shows the overlay, Escape hides it.
     * @param event The keydown event.
     */
    private onInputKeyDown(event: JQuery.TriggeredEvent): void {
        switch (event.key) {
            case 'ArrowDown':
                this.input.val(this.getValueToRender());
                this.showOverlay();
                break;
            case 'Escape':
                this.hideOverlay();
                break;
        }
    }

    /**
     * Handles typing in the input: updates the selected value and the displayed month without reformatting the input. A
     * text that cannot be parsed is kept as the value, unless a mask is used.
     * @param event The input event.
     */
    private onUserInput(event: JQuery.TriggeredEvent): void {
        const rawValue = (event.target as HTMLInputElement).value;
        const value = this.parseValueFromString(rawValue);
        const viewDate = Array.isArray(value) ? value[0] : value;

        if (this.isDate(viewDate)) {
            this.updateModel(event, value, false);
            this.updateViewDate(event, viewDate);
        }
        else {
            this.updateModel(event, this.options.mask ? value : rawValue, false);
        }
    }

    /**
     * Handles the change of the month dropdown: calls `onMonthChange` and displays the selected month.
     * @param event The change event of the dropdown.
     */
    private onMonthDropdownChange(event: JQuery.TriggeredEvent): void {
        const newViewDate = new Date(this.viewDate.getTime());
        newViewDate.setDate(1); // always set to first of month
        newViewDate.setMonth(parseInt((event.target as HTMLSelectElement).value, 10));

        this.options.onMonthChange?.call(this, newViewDate.getMonth() + 1, newViewDate.getFullYear());
        this.updateViewDate(event, newViewDate);
    }

    /**
     * Handles the change of the year input or dropdown: calls `onYearChange` and displays the selected year.
     * @param event The change event.
     */
    private onYearInputChange(event: JQuery.TriggeredEvent): void {
        const newViewDate = new Date(this.viewDate.getTime());
        newViewDate.setFullYear(parseInt((event.target as HTMLInputElement).value, 10));

        this.options.onYearChange?.call(this, newViewDate.getMonth(), newViewDate.getFullYear());
        this.updateViewDate(event, newViewDate);
    }

    /**
     * Displays the previous or next month (or year in the month view) and calls `onMonthChange` or `onYearChange`. With
     * `showMinMaxRange`, a month outside of min / max date is not displayed (#5967); in the month view, the year is
     * kept within the year range of the year navigator.
     * @param event The event that triggered the navigation, its default action is prevented.
     * @param direction `-1` to navigate backward, `1` to navigate forward.
     */
    private navigate(event: JQuery.TriggeredEvent, direction: 1 | -1): void {
        event.preventDefault();
        event.stopPropagation();
        if (this.options.disabled) {
            return;
        }

        const o = this.options;
        const newViewDate = new Date(this.viewDate.getTime());
        newViewDate.setDate(1);

        if (o.view === 'date' || o.view === 'week') {
            newViewDate.setMonth(newViewDate.getMonth() + direction, 1);

            // #5967 check if month can be navigated to
            const testDate = this.truncateDate(new Date(newViewDate.getTime()));
            if (direction < 0) {
                // last moment of the previous month
                testDate.setMonth(testDate.getMonth() + 1);
                testDate.setHours(-1);
            }
            if (o.showMinMaxRange && ((direction < 0 && this.minDate && this.minDate > testDate) || (direction > 0 && this.maxDate && this.maxDate < testDate))) {
                this.setNavigationState(newViewDate);
                return;
            }

            o.onMonthChange?.call(this, newViewDate.getMonth() + 1, newViewDate.getFullYear());
        }
        else if (o.view === 'month') {
            let newYear = newViewDate.getFullYear() + direction;
            if (this.isYearNavigator()) {
                const limit = parseInt((o.yearRange ?? '').split(':')[direction < 0 ? 0 : 1] ?? '', 10);
                // a range that is not numeric (e.g. c-10:c+10) does not limit the navigation
                if (!isNaN(limit)) {
                    newYear = direction < 0 ? Math.max(newYear, limit) : Math.min(newYear, limit);
                }
            }
            newViewDate.setFullYear(newYear);

            o.onYearChange?.call(this, newViewDate.getMonth(), newViewDate.getFullYear());
        }

        this.updateViewDate(event, newViewDate);
    }

    /**
     * Displays the previous month, or the previous year in the month view.
     * @param event The event that triggered the navigation, e.g. a click on the navigator button.
     */
    navBackward(event: JQuery.TriggeredEvent): void {
        this.navigate(event, -1);
    }

    /**
     * Displays the next month, or the next year in the month view.
     * @param event The event that triggered the navigation, e.g. a click on the navigator button.
     */
    navForward(event: JQuery.TriggeredEvent): void {
        this.navigate(event, 1);
    }

    /**
     * Disables the navigator buttons if the previous or next month is outside of min / max date, or both if the date
     * picker is disabled. Only applies with `showMinMaxRange` outside of the month view.
     * @param newViewDate The displayed month.
     */
    private setNavigationState(newViewDate: Date | null | undefined): void {
        if (!newViewDate || !this.options.showMinMaxRange || this.options.view === 'month') {
            return;
        }

        const navPrev = this.panel.find('.ui-datepicker-header > .ui-datepicker-prev');
        const navNext = this.panel.find('.ui-datepicker-header > .ui-datepicker-next');

        if (this.options.disabled) {
            navPrev.addClass('ui-state-disabled');
            navNext.addClass('ui-state-disabled');
            return;
        }

        if (this.minDate) {
            const firstDayOfMonth = new Date(newViewDate.getTime());
            firstDayOfMonth.setMonth(firstDayOfMonth.getMonth(), 1);
            navPrev.toggleClass('ui-state-disabled', this.minDate > this.truncateDate(firstDayOfMonth));
        }

        // check last day of month at 23:59:59
        if (this.maxDate) {
            const lastDayOfMonth = new Date(newViewDate.getTime());
            lastDayOfMonth.setMonth(lastDayOfMonth.getMonth() + 1, 1);
            this.truncateDate(lastDayOfMonth).setSeconds(-1);
            navNext.toggleClass('ui-state-disabled', this.maxDate < lastDayOfMonth);
        }
    }

    /**
     * Starts changing the time when a time picker button is pressed with the left mouse button (repeating while it is
     * held down) or with Enter / Space.
     * @param event The mousedown or keydown event.
     * @param type The part of the time to change: 0 = hour, 1 = minute, 2 = second, 3 = millisecond.
     * @param direction `1` to increment, `-1` to decrement.
     */
    private onTimePickerElementMouseDown(event: JQuery.TriggeredEvent, type: number, direction: 1 | -1): void {
        // only left button, Enter and Space key allowed
        const isActionKey = PrimeFaces.utils.isActionKey(event);
        if (!this.options.disabled && (event.button === 0 || isActionKey)) {
            this.repeat(event, isActionKey ? -1 : null, type, direction);
            if (!isActionKey) {
                event.preventDefault();
            }
        }
    }

    /**
     * Stops changing the time when a time picker button is released and fires the selection events if a value is
     * selected.
     * @param event The mouseup, keyup or mouseleave event.
     */
    private onTimePickerElementMouseUp(event: JQuery.TriggeredEvent): void {
        if (this.options.disabled) {
            return;
        }
        this.clearTimePickerTimer();
        // the time was changed on mousedown or on keydown of Enter / Space
        if ((!event.key || PrimeFaces.utils.isActionKey(event)) && this.value) {
            this.fireDateSelectEvent();
        }
    }

    /**
     * Changes the time by one step and, for a mouse press, schedules the next change: after 500 ms for the first
     * repetition, then every 100 ms until the button is released.
     * @param event The event that started the change.
     * @param interval Delay of the next change in ms, `null` for the first one or `-1` for a single change by keyboard.
     * @param type The part of the time to change: 0 = hour, 1 = minute, 2 = second, 3 = millisecond.
     * @param direction `1` to increment, `-1` to decrement.
     */
    private repeat(event: JQuery.TriggeredEvent, interval: number | null, type: number, direction: 1 | -1): void {
        if (interval === null || interval > -1) {
            this.clearTimePickerTimer();
            this.timePickerTimer = PrimeFaces.queueTask(() => this.repeat(event, 100, type, direction), interval || 500) ?? null;
        }

        const steps = [this.options.stepHour, this.options.stepMinute, this.options.stepSecond, this.options.stepMillisecond];
        if (type >= 0 && type <= 3) {
            this.stepTime(event, type, direction * steps[type]!);
        }
    }

    /**
     * Increments (positive step) or decrements the hour (0), minute (1), second (2) or millisecond (3), wrapping around
     * at the end of the range, and updates the selected value if the new time is within min / max date.
     * @param event The event that triggered the change.
     * @param type The part of the time to change.
     * @param step The amount to add, negative to decrement.
     */
    private stepTime(event: JQuery.TriggeredEvent, type: number, step: number): void {
        const currentTime = this.getTimeSource();
        const time = [currentTime.getHours(), currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds()];
        const ranges = [24, 60, 60, 1000];
        let value = type === 1 ? this.stepMinute(time[1]!, step) : time[type]! + step;
        const range = ranges[type]!;
        value = value >= range ? value - range : value < 0 ? value + range : value;
        time[type] = value;

        if (this.validateTime(time[0]!, time[1]!, time[2]!, time[3]!, currentTime, step > 0 ? 'INCREMENT' : 'DECREMENT')) {
            this.updateTime(event, time[0]!, time[1]!, time[2]!, time[3]!);
        }
    }

    /** Stops the repeated time change of a held down time picker button. */
    private clearTimePickerTimer(): void {
        if (this.timePickerTimer) {
            clearTimeout(this.timePickerTimer);
            this.timePickerTimer = null;
        }
    }

    /**
     * Focuses the selected day, today or the first day (the first month in the month view) of the panel and marks the
     * input and the trigger button as expanded. Called after the overlay was shown and after a lazy model was loaded.
     */
    focusOverlay(): void {
        let focused: JQuery | null = null;
        if (this.options.view === 'month') {
            focused = this.panel.find('a.ui-monthpicker-month');
        }
        if (this.options.view === 'date' || this.options.view === 'week') {
            // focus first selected day or today
            focused = this.panel.find('a.ui-state-active');
            if (focused.length === 0) {
                focused = this.panel.find('.ui-datepicker-today a');
            }
            if (focused.length === 0) {
                focused = this.panel.find('a.ui-state-default');
            }
            if (focused.length === 0) {
                focused = this.panel.find(':button, :input');
            }
        }
        if (focused) {
            const target = focused;
            PrimeFaces.queueTask(() => { target.first().trigger('focus'); }, 1);
        }

        this.input.attr('aria-expanded', 'true').removeClass('ui-state-focus');
        this.triggerButton?.attr('aria-expanded', 'true');
    }

    /**
     * Shows the overlay panel with a transition, unless the date picker is inline, disabled or the panel is already
     * shown. Once shown, the listeners that hide it on an outside click, resize and scroll are bound and the panel is
     * focused.
     */
    private showOverlay(): void {
        if (this.options.disabled || this.documentClickListener || this.options.inline || this.isPanelVisible() || !this.transition) {
            return;
        }

        // #14158 needed for float label
        const parent = this.input.parent();
        if (parent.is("div, span:not('.ui-float-label')")) {
            parent.addClass('ui-inputwrapper-focus');
        }

        this.transition.show({
            onEnter: () => {
                this.beforeShow();
                this.alignPanel();
            },
            onEntered: () => {
                this.datepickerClick = true;
                PrimeFaces.queueTask(() => {
                    this.datepickerClick = false;
                    this.datepickerFocus = false;
                }, 200);

                this.bindDocumentClickListener();
                this.bindWindowResizeListener();
                this.bindScrollListener();
                this.focusOverlay();
            }
        });
    }

    /**
     * Hides the overlay panel with a transition and unbinds its listeners. Afterwards, the month of the selected (or
     * initial view) date is displayed again.
     * @param event The event that caused the hiding; without an event, the focus is put back to the input.
     */
    private hideOverlay(event?: JQuery.TriggeredEvent): void {
        if (!this.documentClickListener || this.options.inline || !this.isPanelVisible() || !this.transition) {
            return;
        }

        // put the focus back to the input
        if (!event) {
            this.input.trigger('focus');
        }

        this.disableModality();

        this.transition.hide({
            onExit: () => {
                this.beforeHide();
                this.unbindDocumentClickListener();
                this.unbindWindowResizeListener();
                this.unbindScrollListener();
                this.datepickerClick = false;
            },
            onExited: () => {
                const multiple = this.isMultipleSelection() || this.isRangeSelection();
                const viewDate = this.options.viewDate && !this.value
                    ? this.parseValue(this.options.viewDate)
                    : ((multiple && Array.isArray(this.value) ? this.value[0] : this.value) || this.getNow());

                if (viewDate instanceof Date) {
                    this.updateViewDate(null, viewDate);
                }

                this.input.attr('aria-expanded', 'false');
                this.triggerButton?.attr('aria-expanded', 'false');
            }
        });
    }

    /**
     * Binds the listener that hides the overlay on a click outside of the panel and the input, and prevents tabbing
     * outside of the panel.
     */
    private bindDocumentClickListener(): void {
        if (this.documentClickListener) {
            return;
        }

        this.documentClickListener = (event) => {
            if (!this.datepickerClick) {
                if (this.options.timeInput) {
                    this.panel.find('.ui-hour-picker input, .ui-minute-picker input, .ui-second-picker input, .ui-millisecond-picker input')
                        .filter(':focus').first().trigger('blur');
                }

                this.hideOverlay(event);
                this.onInputBlur();
                PrimeFaces.queueTask(() => { $(event.target).trigger('focus'); }, 1);
            }

            this.datepickerClick = false;
        };
        $(document).on('click', this.documentClickListener);

        // prevent tabbing outside of panel
        PrimeFaces.utils.preventTabbing(undefined, this.id + '_panel', this.panel.zIndex(), () => this.panel.find(':tabbable'));
    }

    /** Unbinds the outside click listener and allows tabbing outside of the panel again. */
    private unbindDocumentClickListener(): void {
        if (this.documentClickListener) {
            $(document).off('click', this.documentClickListener);
            PrimeFaces.utils.enableTabbing(undefined, this.id + '_panel');
            this.documentClickListener = null;
        }
    }

    /** Binds a window resize listener that switches between the touch UI and the overlay, see `autoDetectDisplay`. */
    private bindResponsiveResizeListener(): void {
        if (this.options.autoDetectDisplay && !this.options.inline) {
            const namespace = 'resize.responsive' + this.id;
            $(window).off(namespace).on(namespace, () => this.updateResponsiveness());
        }
    }

    /** Unbinds the window resize listener that switches between the touch UI and the overlay. */
    private unbindResponsiveResizeListener(): void {
        $(window).off('resize.responsive' + this.id);
    }

    /** Binds a window resize listener that hides the overlay, not for inline, touch UI and mobile date pickers. */
    private bindWindowResizeListener(): void {
        if (this.options.inline || this.options.touchUI || PrimeFaces.env.mobile) {
            return;
        }
        $(window).on('resize.' + this.id, () => {
            // TODO: lazy model for some reason triggers a windows resize
            if (!this.options.lazyModel) {
                this.hideOverlay();
            }
        });
    }

    /** Unbinds the window resize listener that hides the overlay. */
    private unbindWindowResizeListener(): void {
        $(window).off('resize.' + this.id);
    }

    /**
     * Binds scroll listeners to the scrollable parents that hide the overlay, not for inline and touch UI date pickers.
     */
    private bindScrollListener(): void {
        // focusing a navigator control on iOS can scroll the viewport while the touch UI panel is open
        if (this.options.touchUI || this.options.inline) {
            return;
        }

        this.scrollableParents = PrimeFaces.utils.getScrollableParents(this.jq[0]);
        this.scrollableListener = () => this.hideOverlay();
        for (const parent of this.scrollableParents) {
            $(parent).on('scroll', this.scrollableListener);
        }
    }

    /** Unbinds the scroll listeners of the scrollable parents. */
    private unbindScrollListener(): void {
        if (this.scrollableListener) {
            for (const parent of this.scrollableParents) {
                $(parent).off('scroll', this.scrollableListener);
            }
            this.scrollableListener = null;
        }
    }

    /**
     * Switches between the touch UI and the overlay depending on the screen size and the `responsiveBreakpoint`,
     * rendering the date picker again when the mode changes. Only applies with `autoDetectDisplay` to a date picker
     * that is not inline.
     */
    updateResponsiveness(): void {
        if (this.options.autoDetectDisplay && this.options.responsiveBreakpoint && !this.options.inline) {
            const touchUI = PrimeFaces.env.mobile || PrimeFaces.env.isScreenSizeLessThan(this.options.responsiveBreakpoint);
            if (this.options.touchUI !== touchUI) {
                this.options.touchUI = touchUI;
                // the open overlay is removed with the panel: unbind its listeners, otherwise it can't be opened again
                if (this.documentClickListener) {
                    if (this.isPanelVisible()) {
                        this.beforeHide();
                    }
                    this.unbindOverlayListeners();
                    this.datepickerClick = false;
                    this.input.attr('aria-expanded', 'false');
                    this.triggerButton?.attr('aria-expanded', 'false');
                }
                this.renderDatePicker();
            }
        }
    }

    /**
     * Checks whether the panel is displayed.
     * @returns `true` if the panel is visible and the date picker is not disabled.
     */
    isPanelVisible(): boolean {
        return !this.options.disabled && this.panel.is(':visible');
    }

    /**
     * Checks whether a value is a valid date.
     * @param value The value to check.
     * @returns `true` for a `Date` that is not invalid.
     */
    private isDate(value: unknown): value is Date {
        // not instanceof: a date of another frame (e.g. an iframe) has another Date constructor
        return Object.prototype.toString.call(value) === '[object Date]' && !isNaN((value as Date).getTime());
    }

    /**
     * Checks whether the year can be selected in the header, see `yearNavigator`.
     * @returns `true` for a year input or dropdown.
     */
    private isYearNavigator(): boolean {
        return ['input', 'select', 'true'].includes(this.options.yearNavigator);
    }

    /**
     * Checks whether the year navigator is a number input rather than a dropdown.
     * @returns `true` if `yearNavigator` is `true` or `input`.
     */
    private isYearNavigatorInput(): boolean {
        return ['input', 'true'].includes(this.options.yearNavigator);
    }

    /**
     * Positions the overlay panel below the input, flipping it when there is not enough space, or shows the modal mask
     * in the touch UI. Does nothing if the panel is not visible.
     */
    alignPanel(): void {
        if (!this.isPanelVisible()) {
            return;
        }

        if (this.options.touchUI) {
            this.enableModality();
            return;
        }

        if (this.appendTarget?.length) {
            this.panel.css('min-width', this.jq.outerWidth() + 'px');
        }

        if (this.panel.parent().is(this.jq)) {
            this.panel.css({ left: '0px', top: this.jq.innerHeight() + 'px', 'transform-origin': 'center top' });
        }
        else {
            PrimeFaces.utils.position(this.panel.css({ left: '', top: '', 'transform-origin': 'center top' }), {
                my: 'left top',
                at: 'left bottom',
                of: this.jq,
                collision: 'flipfit',
                using: function(pos, directions) {
                    $(this).css({ 'transform-origin': 'center ' + directions.vertical, left: pos.left, top: pos.top });
                }
            });
        }
    }

    /**
     * Displays the panel as a modal touch UI: adds a mask below the panel that blocks scrolling of the page, a click on
     * the mask removes it again.
     */
    enableModality(): void {
        if (this.mask) {
            return;
        }
        this.mask = $('<div class="ui-widget-overlay ui-datepicker-mask ui-datepicker-mask-scrollblocker"></div>')
            .css('z-index', String(parseInt(this.panel.css('z-index'), 10) - 1))
            .on('click.datePicker-mask', () => this.disableModality());
        $(document.body).append(this.mask).addClass('ui-overflow-hidden');
    }

    /**
     * Removes the modal mask of the touch UI and allows scrolling of the page again, unless the mask of another date
     * picker is still shown.
     */
    disableModality(): void {
        if (!this.mask) {
            return;
        }
        this.mask.off('click.datePicker-mask').remove();
        this.mask = null;
        if (!$(document.body).children('.ui-datepicker-mask-scrollblocker').length) {
            $(document.body).removeClass('ui-overflow-hidden');
        }
    }

    /**
     * Finds the week of a day among the displayed months, comparing month and year too since a week can contain days of
     * the previous or next month.
     * @param dateMeta The day.
     * @returns The 7 days of the week, or `null` if the day is not displayed.
     */
    private findWeekMetadata(dateMeta: DateMeta): DateMeta[] | null {
        // compare month and year too, a week can contain days of the previous or next month
        for (const month of this.monthsMetadata) {
            const week = month.dates.find((w) => w.some((day) => day.day === dateMeta.day && day.month === dateMeta.month && day.year === dateMeta.year));
            if (week) {
                return week;
            }
        }
        return null;
    }

    /**
     * Handles the selection of a day or month: selects the whole week in the week view, toggles the day in multiple
     * selection (respecting `maxDateCount`, an unselect is notified too, #10850) or selects it, then hides the overlay
     * if configured via `focusOnSelect` / `hideOnDateTimeSelect` or `hideOnRangeSelection`.
     * @param event The click or keydown event, or `null` when called via the API.
     * @param dateMeta The selected day, ignored if it is not selectable.
     */
    private onDateSelect(event: JQuery.TriggeredEvent | null, dateMeta: DateMeta): void {
        if (this.options.disabled || !dateMeta.selectable) {
            event?.preventDefault();
            return;
        }

        const o = this.options;
        const hideSoon = () => PrimeFaces.queueTask(() => this.hideOverlay(), 100);

        if (o.view === 'week') {
            // simulate click on start and end day of the week
            const week = this.findWeekMetadata(dateMeta);
            if (week) {
                this.selectDate(event, week[0]!);
                this.selectDate(event, week[week.length - 1]!);
            }
            hideSoon();
        }
        else if (this.isMultipleSelection()) {
            if (this.isSelected(dateMeta)) {
                this.updateModel(event, this.selectedDates.filter((date) => !this.isDateEquals(date, dateMeta)));
                // #10850 notify unselect
                this.fireDateSelectEvent();
            }
            else if (!o.maxDateCount || o.maxDateCount > this.selectedDates.length) {
                this.selectDate(event, dateMeta);
            }
        }
        else {
            this.selectDate(event, dateMeta);
        }

        if (!o.inline && o.focusOnSelect && this.isSingleSelection() && (!o.showTime || o.hideOnDateTimeSelect)) {
            hideSoon();
        }
        if (!o.inline && this.isRangeSelection() && o.hideOnRangeSelection && this.selectedDates[1]) {
            hideSoon();
        }

        event?.preventDefault();
    }

    /**
     * Selects a day, keeping the time of the current value with `showTime` and clamping it to min / max date. In range
     * selection, the day becomes the end of the range if it is not before the start, otherwise it starts a new range.
     * Fires the selection events and updates the filled state of the input (#14158).
     * @param event The event that triggered the selection, or `null`.
     * @param dateMeta The day to select.
     */
    private selectDate(event: JQuery.TriggeredEvent | null, dateMeta: DateMeta): void {
        let date = new Date(dateMeta.year, dateMeta.month, dateMeta.day);

        if (this.options.showTime) {
            const time = this.getCurrentTime();
            date.setHours(time.getHours());
            date.setMinutes(this.stepMinute(time.getMinutes()));
            date.setSeconds(time.getSeconds());
            date.setMilliseconds(time.getMilliseconds());
        }

        if (this.minDate && this.minDate > date) {
            date = this.minDate;
        }
        if (this.maxDate && this.maxDate < date) {
            date = this.maxDate;
        }

        if (this.isSingleSelection()) {
            this.updateModel(event, date);
        }
        else if (this.isMultipleSelection()) {
            this.updateModel(event, this.selectedDates.concat(date));
        }
        else if (this.isRangeSelection()) {
            // the day ends an incomplete range unless it is before its start, otherwise it starts a new range
            const [startDate, endDate] = this.selectedDates;
            this.updateModel(event, startDate && !endDate && date.getTime() >= startDate.getTime() ? [startDate, date] : [date, null]);
        }

        this.fireDateSelectEvent();

        // #14158 needed for float label
        PrimeFaces.updateFilledState(this.input, this.input.parent());
    }

    /**
     * Rounds a minute to the `stepMinute`, or adds a step to it (#6047).
     * @param currentMinute The current minute.
     * @param step Minutes to add, rounded to a multiple of the step; if omitted, the minute is rounded up to the next
     * multiple of `stepMinute` unless it already is one.
     * @returns The new minute, not wrapped to the range 0 to 59.
     */
    private stepMinute(currentMinute: number, step?: number): number {
        if (this.options.stepMinute <= 1) {
            return step ? currentMinute + step : currentMinute;
        }
        if (!step) {
            step = this.options.stepMinute;
            if (currentMinute % step === 0) {
                return currentMinute;
            }
        }
        return Math.floor((currentMinute + step) / step) * step;
    }

    /**
     * Switches the time between AM and PM by adding or subtracting 12 hours and fires the selection events.
     * @param event The click event of the AM / PM button.
     */
    private toggleAmPm(event: JQuery.TriggeredEvent): void {
        const currentTime = this.getTimeSource();
        const currentHour = currentTime.getHours();
        this.updateTime(event, currentHour >= 12 ? currentHour - 12 : currentHour + 12, currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds());
        this.fireDateSelectEvent();
        event.preventDefault();
    }

    /**
     * Handles the change of a time input with `timeInput`: applies a valid value within min / max date to the selected
     * value and fires the selection events, otherwise restores the value from before the input received focus.
     * @param unit The part of the time the input edits.
     * @param input The changed input.
     * @param event The change event.
     */
    private handleTimeInput(unit: 'hour' | 'minute' | 'second' | 'millisecond', input: HTMLInputElement, event: JQuery.TriggeredEvent): void {
        const currentTime = this.getTimeSource();
        const time = [currentTime.getHours(), currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds()];
        const index = ['hour', 'minute', 'second', 'millisecond'].indexOf(unit);
        const twelve = unit === 'hour' && this.options.hourFormat === '12';
        const [min, max] = unit === 'hour' ? (twelve ? [1, 12] : [0, 23]) : unit === 'millisecond' ? [0, 999] : [0, 59];
        let valid = false;
        let newValue = NaN;

        if (new RegExp(unit === 'millisecond' ? '^([0-9]){1,3}$' : '^([0-9]){1,2}$').test(input.value)) {
            newValue = parseInt(input.value, 10);
            if (newValue >= min && newValue <= max) {
                if (twelve) {
                    // 1-12 in the current half of the day, e.g. 3 at 2 PM is 15:00
                    newValue = newValue % 12 + (currentTime.getHours() >= 12 ? 12 : 0);
                }
                time[index] = newValue;
                valid = this.validateTime(time[0]!, time[1]!, time[2]!, time[3]!, currentTime);
            }
        }

        if (!valid) {
            event.preventDefault();
            input.value = this.oldTime[unit] ?? '';
            return;
        }

        const newDateTime = this.getCurrentTime();
        newDateTime.setHours(time[0]!, time[1]!, time[2]!, time[3]!);

        this.value = newDateTime;
        this.input.val(this.getValueToRender());
        this.fireDateSelectEvent();
    }

    /**
     * Checks whether a time on the day of the given date is within min / max date. When the time is stepped, a new time
     * still outside of the range is valid if it comes nearer to it.
     * @param hour The new hour.
     * @param minute The new minute.
     * @param second The new second.
     * @param millisecond The new millisecond.
     * @param value The current date, provides the day and the time the direction refers to.
     * @param direction Whether the time is incremented or decremented, if it is stepped.
     * @returns Whether the new time is valid.
     */
    private validateTime(hour: number, minute: number, second: number, millisecond: number, value: Date, direction?: 'INCREMENT' | 'DECREMENT'): boolean {
        const dateNew = new Date(value.getFullYear(), value.getMonth(), value.getDate(), hour, minute, second, millisecond);

        // a new time still outside the allowed range is valid if it comes nearer to it
        if (this.minDate && this.minDate > dateNew && !(direction === 'INCREMENT' && this.minDate > value)) {
            return false;
        }
        if (this.maxDate && this.maxDate < dateNew && !(direction === 'DECREMENT' && this.maxDate < value)) {
            return false;
        }
        return true;
    }

    /**
     * Sets the time of the selected date (or of the view date if no single date is selected) as the new value, without
     * firing events.
     * @param event The event that caused the change.
     * @param hour The new hour.
     * @param minute The new minute.
     * @param second The new second.
     * @param millisecond The new millisecond.
     */
    private updateTime(event: JQuery.TriggeredEvent, hour: number, minute: number, second: number, millisecond: number): void {
        const newDateTime = this.getCurrentTime();
        newDateTime.setHours(hour);
        newDateTime.setMinutes(minute);
        newDateTime.setSeconds(second);
        newDateTime.setMilliseconds(millisecond);

        // the callers notify the selection, the time picker buttons on mouseup / keyup
        this.updateModel(event, newDateTime);
    }

    /**
     * Gets the current date and time in the `timeZone` of the date picker, or in the time zone of the browser if none
     * is set.
     * @returns The current date and time.
     */
    getNow(): Date {
        let now = new Date();
        if (this.options.timeZone) {
            const localString = now.toLocaleString('en-GB', { // use English so we can parse it back
                year: "numeric",
                month: "long",
                day: "numeric",
                hour: "numeric",
                minute: "numeric",
                second: "numeric",
                timeZone: this.convertTimeZone(this.options.timeZone)
            }).replace(' at ', ', ');
            now = new Date(localString);
        }
        return now;
    }

    /**
     * Gets a copy of the date whose time is displayed in the time picker.
     * @returns A new date with the date and time of the selected date or the view date.
     */
    private getCurrentTime(): Date {
        return new Date(this.getTimeSource());
    }

    /**
     * Converts a Java time zone like `GMT+02:00` to an IANA time zone like `Etc/GMT-2`, which `Intl` understands. Other
     * time zones are returned unchanged.
     * @param javaTimeZone The Java time zone ID.
     * @returns The IANA time zone.
     */
    private convertTimeZone(javaTimeZone: string): string {
        if (['ETC/UTC', 'ETC/GMT', 'UTC', 'GMT'].includes(javaTimeZone.toUpperCase())) {
            return javaTimeZone;
        }

        const matches = javaTimeZone.match(/^(GMT|UTC)([+-])(\d{2}):(\d{2})$/);
        if (!matches) {
            return javaTimeZone;
        }

        const hours = parseInt(matches[3]!, 10);
        return hours === 0 ? 'Etc/GMT' : 'Etc/GMT' + (matches[2] === '+' ? '-' : '+') + hours;
    }

    /**
     * Handles a click on the today button: displays and selects today, with the current time if `showTime` is set.
     * @param event The click event.
     */
    private onTodayButtonClick(event: JQuery.TriggeredEvent): void {
        const today = this.getNow();
        this.updateViewDate(event, today);
        if (this.options.showTime) {
            this.updateTime(event, today.getHours(), today.getMinutes(), today.getSeconds(), today.getMilliseconds());
        }
        this.onDateSelect(event, { day: today.getDate(), month: today.getMonth(), year: today.getFullYear(), today: true, selectable: true });
    }

    /**
     * Handles a click on the clear button: clears the selection, fires the `change` event and the `dateSelect` behavior
     * and hides the overlay.
     * @param event The click event.
     */
    private onClearButtonClick(event: JQuery.TriggeredEvent): void {
        this.updateViewDate(event, this.getNow());
        this.updateModel(event, null);
        this.input.trigger('change');
        this.callBehavior('dateSelect');
        this.hideOverlay();
    }

    /**
     * Sets the time of the date to 00:00.
     * @param value The date to modify in place.
     * @returns The given date.
     */
    private truncateDate(value: Date): Date {
        value.setHours(0, 0, 0, 0);
        return value;
    }

    /**
     * Derives the `yearRange` of the year navigator from the view date, 10 years around it for a dropdown and 1000 for
     * an input, unless a year range was configured.
     */
    private updateYearNavigator(): void {
        const isYearInput = this.isYearNavigatorInput();
        if (this.hasCustomYearRange || (isYearInput && this.options.yearRange)) {
            return;
        }
        if (this.isYearNavigator()) {
            const viewYear = this.viewDate.getFullYear();
            const yearIncrement = isYearInput ? 1000 : 10;
            this.options.yearRange = (viewYear - yearIncrement) + ':' + (viewYear + yearIncrement);
        }
    }

    /** Keeps the displayed month within min / max date when using the month navigator. */
    private alignViewMonth(): void {
        const o = this.options;
        if ((o.autoMonthFormat || !this.input.val()) && o.monthNavigator && o.view !== 'month') {
            let viewMonth = this.viewDate.getMonth();
            viewMonth = (this.isInMaxYear() && Math.min(this.maxDate!.getMonth(), viewMonth))
                || (this.isInMinYear() && Math.max(this.minDate!.getMonth(), viewMonth)) || viewMonth;
            this.viewDate.setMonth(viewMonth);
        }
    }

    /**
     * Displays the month of the given date and renders the panel again. Unless silent, the date is remembered for range
     * selection and the AJAX `viewChange` event is fired, which also updates a lazy model.
     * @param event The event that caused the change, or `null`.
     * @param value The date whose month to display.
     * @param silent `true` to skip the `viewChange` event.
     */
    updateViewDate(event: JQuery.TriggeredEvent | null, value: Date, silent: boolean = false): void {
        if (!silent) {
            this.viewDateOption = value;
            this.fireViewChangeEvent(value);
        }

        this.viewDate = value;
        this.alignViewMonth();
        this.updatePanel();
    }

    /**
     * Sets the selected value, writes it formatted to the input and renders the panel again, without firing events.
     * @param event The event that caused the change, or `null`.
     * @param value The new value: a date, a list of dates, `null`, or the raw text of the input; an empty string clears
     * the value.
     * @param updateInput `false` to keep the text of the input, e.g. while the user is typing.
     */
    private updateModel(event: JQuery.TriggeredEvent | null, value: DatePickerValue, updateInput: boolean = true): void {
        this.value = value === '' ? null : value;
        if (updateInput) {
            this.input.val(this.getValueToRender());
        }
        this.updatePanel();
    }

    /**
     * Renders the content of the panel again and restores the focus to the new element with the same ARIA label. Call
     * it after changing options at runtime, e.g. the disabled dates, so the panel reflects them.
     */
    updatePanel(): void {
        // remember the focused element before we destroy the inner elements
        const el = document.activeElement;

        this.panel[0]!.innerHTML = this.renderPanelElements();

        // attempt to refocus the newly created version of the same element
        const label = el?.getAttribute('aria-label');
        if (label) {
            const refocus = this.panel.find('[aria-label]').filter((_, element) => element.getAttribute('aria-label') === label);
            if (refocus.length) {
                if (PrimeFaces.env.ios && el?.tagName === 'SELECT') {
                    // focus the calendar instead of reopening the native iOS picker after replacing its select
                    this.focusOverlay();
                }
                else {
                    PrimeFaces.queueTask(() => { refocus.first().trigger('focus'); });
                }
            }
        }

        this.setInitOptionValues();
        this.bindPanelEvents();
    }
}
