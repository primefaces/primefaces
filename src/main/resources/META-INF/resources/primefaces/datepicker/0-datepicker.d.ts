/**
 * Namespace for the JQuery UI PrimeFaces date picker.
 *
 * The Prime date picker is a JQuery UI widget used to select a date featuring display modes, paging, localization,
 * AJAX selection and more.
 */
declare namespace JQueryPrimeDatePicker {

    /**
     * Defines the selection mode, whether one or multiple dates can be selected.
     */
    export type SelectionMode = "single" | "multiple" | "range";

    /**
     * Defines the view mode, whether a date or month is to be selected.
     */
    export type ViewMode = "date" | "month";

    /**
     * Defines the hour format or the clock convention.
     * - `12`: Time convention in which the 24 hours of the day are divided into two periods: `a.m.` and `p.m.`.
     * - `24`: Time convention in which the 24 hours of the day are divided into one period.
     */
    export type ClockConvention = "12" | "24";

    /**
     * Represents the available periods of a 12 hour clock convention.
     */
    export type HalfDayPeriod = "AM" | "PM";

    /**
     * Represents whether a numerical value is to be decremented or incremented.
     */
    export type AlterationMode = "DECREMENT" | "INCREMENT";

    /**
     * Specifies which part of the time is changed:
     * - `-1`: The time is not changed.
     * - `0`: Changes the hour.
     * - `1`: Changes the minutes.
     * - `2`: Changes the second.
     */
    export type ChangeTimeType = -1 | 0 | 1 | 2;

    /**
     * Represents a one dimensional direction:
     * - `-1`: Backwards.
     * - `+1`: Forwards.
     */
    export type OneDimensionalDirection = -1 | 1;

    /**
     * Javascript function that takes a date object and returns the content for the date cell.
     */
    export type DateTemplate =
        /**
         * @param monthNameOrDate Either the name of a month or a date to be rendered.
         * @return An HTML snippets with the contents of the date cell.
         */
        (this: PickerInstance, monthNameOrDate: string | DayInstantSelectableRelative) => string;

    /**
     * Base callback that is only passed the current date picker instance.
     */
    export type BaseCallback = (this: PickerInstance) => void

    /**
     * Base callback that, in addition to the current date picker instance, is also passed the event that occured.
     */
    export type BaseEventCallback =
        /**
         * @param event The event that occurred.
         */
        (this: PickerInstance, event: JQuery.TriggeredEvent) => void;

    /**
     * Callbacks for when a value has changed.
     */
    export type MutationCallback<T extends any[]> =
        /**
         * @param newValues The value or set of values that represent the new state.
         */
        (this: PickerInstance, ...newValues: T) => void;

    /**
     * Callback for when a value has changed. It is also passed the event that occurred.
     */
    export type MutationEventCallback<T extends any[]> =
        /**
         * @param event The event that occurred.
         * @param newValues The value or set of values that represent the new state.
         */
        (this: PickerInstance, event: JQuery.TriggeredEvent, ...newValues: T) => void;

    /**
     * A cardinal number, i.e. a number that represents an amount of something. Some common examples include the number
     * of days in a month or the number of seconds in a year.
     */
    export type Cardinal = number;

    // Of-types representing a temporal index, or partial information of an instant

    /**
     * Integer value representing the month, beginning with `0` for `January` and ending with `11` for `December`.
     */
    export type MonthOfTheYear = number;

    /**
     * Integer value representing the month, beginning with `1` for `January` and ending with `12` for `December`.
     */
    export type MonthOfTheYearOneBased = number;

    /**
     * Integer value representing the day of the month. `1` represents the first day of the month.
     */
    export type DayOfTheMonth = number;

    /**
     * Integer value representing the day of the week, starting with `0` for `Sunday` and ending with `6` for
     * `Saturday`.
     */
    export type DayOfTheWeek = number;

    /**
     * Integer value representing the hour of the day. `0` represents midnight.
     */
    export type HourOfTheDay = number;

    /**
     * Integer value representing the minute segment of a time. `0` represents 0 minutes past the hour.
     */
    export type MinuteOfTheHour = number;

    /**
     * Integer value representing the second segment of a time. `0` represents 0 seconds past the minute.
     */
    export type SecondOfTheMinute = number;

    /**
     * Represents the time of a day.
     */
    export interface TimeOfTheDay {
        /**
         * The hour of the day, between 0 and 23.
         */
        hour: HourOfTheDay;

        /**
         * The minute of the hour, between 0 and 59.
         */
        minute: MinuteOfTheHour;

        /**
         * The second of the minute, between 0 and 59.
         */
        second: SecondOfTheMinute;
    }

    // Instants representing an instant in time (to a certain precision)

    /**
     * An integer value representing the year. All values represent the actual year.
     */
    export type YearInstant = number;

    /**
     * Represents a month of a particular year.
     */
    export interface MonthInstant {
        /**
         * The month of the year.
         */
        month: MonthOfTheYear;

        /**
         * The year of the month instant.
         */
        year: YearInstant;
    }

    /**
     * Represents a day of a particular month and year.
     */
    export interface DayInstant {
        /**
         * The day of the month.
         */
        day: DayOfTheMonth;

        /**
         * The month of the year.
         */
        month: MonthOfTheYear;

        /**
         * The year of the month instant.
         */
        year: YearInstant;
    }

    /**
     * Represents a {@link DayInstant} and whether that day can be selected by the user.
     */
    export interface DayInstantSelectable extends DayInstant {
        /**
         * Whether the day can be selected as the currently selected value of the date picker.
         */
        selectable: boolean;
    }

    /**
     * Represents a {@link DayInstantSelectable} and additionally indicates whether the day is the current date.
     */
    export interface DayInstantSelectableRelative extends DayInstantSelectable {
        /**
         * Whether this day is today (equal to the current date).
         */
        today: boolean;
    }

    /**
     * Represents a list of all days in a particular month.
     */
    export interface DayListInMonth extends MonthInstant {
        /**
         * An array with the weeks of the month, each week being an array containing the days of that week.
         */
        dates: DayInstantSelectableRelative[][];
        /**
         * 0-based index of the month in the year.
         */
        index: MonthOfTheYear;
    }

    /**
     * Localized strings for various messages displayed by the date or time picker.
     */
    export interface PickerLocale {
        /**
         * Index of the day that represents the first day of the week.
         */
        firstDayOfWeek: number;

        /**
         * Names of the weekdays, starting at `Sunday`.
         */
        dayNames: [string, string, string, string, string, string, string];

        /**
         * Short names of the weekdays, starting at `Sunday` (`Sun`).
         */
        dayNamesShort: [string, string, string, string, string, string, string];

        /**
         * Extra short names of the weekdays, starting at `Sunday` (`Su`).
         */
        dayNamesMin: [string, string, string, string, string, string, string];

        /**
         * Names of the months in the year, starting at `January`.
         */
        monthNames: [string, string, string, string, string, string, string, string, string, string, string, string];

        /**
         * Short names of the months in the year, starting at `January` (`Jan`).
         */
        monthNamesShort: [string, string, string, string, string, string, string, string, string, string, string, string];

        /**
         * Name of `today` button for navigating to the current date.
         */
        today: string;

        /**
         * Name of `clear` button for clearing the selected date or time.
         */
        clear: string;
    }

    /**
     * Represents the available options for the date or time picker.
     */
    export interface PickerOptions {
        /**
         * The ID of this widget, usually the ID of the DOM element.
         */
        id: string | string[];

        /**
         * The name of this widget.
         */
        name: string | null;

        /**
         * The default date that is shown when none was specified.
         */
        defaultDate: string | Date | Date[] | null;

        /**
         * The date that is shown in the picker.
         */
        viewDate: string | Date | Date[] | null;

        /**
         * Inline style of the component.
         */
        style: string | null;

        /**
         * Style class of the component.
         */
        styleClass: string | null;

        /**
         * Whether the date picker is rendered inline or as an overlay.
         */
        inline: boolean;

        /**
         * Defines the selection mode, whether one or multiple dates can be selected.
         */
        selectionMode: SelectionMode;

        /**
         * Separator for joining start and end dates on range selection mode, such as `-`.
         */
        rangeSeparator: string;

        /**
         * ID of the input element that stores the selected date or time.
         */
        inputId: string | null;

        /**
         * Inline style of the input element. Used when mode is popup.
         */
        inputStyle: string | null;

        /**
         * Style class of the input element. Used when mode is popup.
         */
        inputStyleClass: string | null;

        /**
         * Whether an input is required.
         */
        required: boolean;

        /**
         * Whether the input is set to `readonly`.
         */
        readOnlyInput: boolean;

        /**
         * Whether the input is disabled.
         */
        disabled: boolean;

        /**
         * Tabindex of the date picker button
         */
        tabIndex: string | null;

        /**
         * Specifies a short hint.
         */
        placeholder: string | null;

        /**
         * Whether to show an icon to display the picker in an overlay
         */
        showIcon: boolean;

        /**
         * Icon of the date picker element that toggles the visibility in popup mode.
         */
        icon: string;

        /**
         * Whether the date picker overlay is shown when the element focused.
         */
        showOnFocus: boolean;

        /**
         * Separator for joining the hour and minute part of a time, defaults to `:`.
         */
        timeSeparator: string;

        /**
         * Whether the current input is a  valid date / time.
         */
        valid: boolean;

        /**
         * Whether to keep the invalid inputs in the field or not.
         */
        keepInvalid: boolean;

        /**
         * Number of months to display concurrently.
         */
        numberOfMonths: Cardinal;

        /**
         * Defines the view mode, whether a date or month is to be selected.
         */
        view: ViewMode;

        /**
         * Activates touch friendly mode
         */
        touchUI: boolean;

        /**
         * Specifies if the time picker should be displayed
         */
        showTime: boolean;

        /**
         * Shows only time picker without date.
         */
        timeOnly: boolean;

        /**
         * Whether to show the seconds in time picker. Default is `false`.
         */
        showSeconds: boolean;

        /**
         * Defines the hour format, either 12 hour mode or 24 hour mode.
         */
        hourFormat: ClockConvention;

        /**
         * Hour steps.
         */
        stepHour: Cardinal;

        /**
         * Minute steps.
         */
        stepMinute: Cardinal;

        /**
         * Second steps.
         */
        stepSecond: Cardinal;

        /**
         * The cutoff year for determining the century for a date. Any dates entered with a year value less than or
         * equal to the cutoff year are considered to be in the current century, while those greater than it are deemed
         * to be in the previous century.
         */
        shortYearCutoff: string;

        /**
         * Defines if the popup should be hidden when a time is selected.
         */
        hideOnDateTimeSelect: boolean;

        /**
         * Custom localized settings for the {@link locale}.
         */
        userLocale: Partial<PickerLocale> | null;

        /**
         * Localized strings for various messages displayed by the date or time picker.
         */
        locale: Partial<PickerLocale>;

        /**
         * Date format to be used for parsing and formatting dates, such as `mm/dd/yy`.
         */
        dateFormat: string;

        /**
         * The range of years displayed in the year drop-down in (`nnnn:nnnn`) format such as (`2000:2020`). Default
         * value is `displayed_date - 10 : displayed_date + 10`.
         */
        yearRange: string | null;

        /**
         * Inline style of the container element.
         */
        panelStyle: string | null;

        /**
         * Style class of the container element.
         */
        panelStyleClass: string | null;

        /**
         * Style class of the individual date elements.
         */
        dateStyleClasses: string | null;

        /**
         * Whether to show the month navigator
         */
        monthNavigator: boolean;

        /**
         * Whether to show the year navigator
         */
        yearNavigator: boolean;

        /**
         * List of dates that should be disabled.
         */
        disabledDates: string[] | null;

        /**
         * List of week day indexes that should be disabled.
         */
        disabledDays: DayOfTheWeek | null;

        /**
         * Sets date picker's minimum selectable value. Also used for validation on the server-side.
         */
        minDate: string | null;

        /**
         * Sets date picker's maximum selectable value. Also used for validation on the server-side.
         */
        maxDate: string | null;

        /**
         * Defines the maximum number of selectable dates in multiple selection mode.
         */
        maxDateCount: Cardinal | null;

        /**
         * Displays days belonging to other months.
         */
        showOtherMonths: boolean;

        /**
         * Enables selection of days belonging to other months.
         */
        selectOtherMonths: boolean;

        /**
         * Whether to display buttons at the footer.
         */
        showButtonBar: boolean;

        /**
         * Style class for the button that switches to the current date.
         */
        todayButtonStyleClass: string;

        /**
         * Style class for the button that clear the selected date or time.
         */
        clearButtonStyleClass: string;

        /**
         * Appends the dialog to the element defined by the CSS selector.
         */
        appendTo: string | null;

        /**
         * Javascript function that takes a date object and returns the content for the date cell.
         */
        dateTemplate: DateTemplate | null;

        /**
         * Whether an input is rendered for the time, or a text element only.
         */
        timeInput: boolean;

        /**
         * Client side callback to execute when input element receives focus.
         */
        onFocus: BaseEventCallback | null;

        /**
         * Client side callback to execute when input element loses focus.
         */
        onBlur: BaseEventCallback | null;

        /**
         * Client side callback to execute when data was entered into the input field.
         */
        onInput: BaseEventCallback | null;

        /**
         * Client side callback to execute when text within input element is selected by user.
         */
        onSelect: MutationEventCallback<[Date]> | null;

        /**
         * Client side callback to execute when the selected date has changed.
         */
        onChange: BaseEventCallback | null;

        /**
         * Client side callback to execute when the displayed date changes.
         */
        onViewDateChange: MutationEventCallback<[Date]> | null;

        /**
         * Client side callback to execute when the button to switch to the current date was clicked.
         */
        onTodayButtonClick: BaseEventCallback | null;

        /**
         * Client side callback to execute when the button to clear the selected date or time was clicked.
         */
        onClearButtonClick: BaseEventCallback | null;

        /**
         * Client side callback to execute before the date picker overlay is shown.
         */
        onBeforeShow: BaseCallback | null;

        /**
         * Client side callback to execute before the date picker overlay is hidden.
         */
        onBeforeHide: BaseCallback | null;

        /**
         * Client side callback to execute when the selected month has changed.
         */
        onMonthChange: MutationCallback<[MonthOfTheYearOneBased, YearInstant]> | null;

        /**
         * Client side callback to execute when the panel with the date picker was created.
         */
        onPanelCreate: BaseCallback | null;

        /**
         * Client side callback to execute when the selected year has changed.
         */
        onYearChange: MutationCallback<[MonthOfTheYear, YearInstant]> | null
    }

    /**
     * Base interface for the {@link PickerInstance} that contains all methods that are available via the JQuery
     * wrapper, see {@link JQuery.datePicker}.
     */
    export interface PickerWidgetMethods {

        // =========================
        // === Getters / setters ===
        // =========================

        /**
         * Changes the selected date of this date picker to the given date, and updates the UI.
         * @param date The new date to set.
         */
        setDate(date: string | Date): void;

        /**
         * Retrieves the currently selected date of this date picker.
         * @return The currently selected date.
         */
        getDate(): Date | "" | null;

        // ==========
        // === UI ===
        // ==========

        /**
         * If the date picker is shown in an overlay panel, adjusts the position of that overlay panel so that is shown
         * at its proper location.
         */
        alignPanel(): void;

        /**
         * Clears the select time of the time picker.
         */
        clearTimePickerTimer(): void;

        /**
         * Shows the overlay panel with the date picker.
         */
        showOverlay(): void;

        /**
         * Hides the overlay panel with the date picker.
         */
        hideOverlay(): void;

        /**
         * Makes the overlay panel a modal dialog so that other elements in the page cannot be interacted with while the
         * overlay date picker is shown.
         */
        enableModality(): void;

        /**
         * Removes the modality feature from the overlay panel so that other elements in the page can be interacted with
         * while the overlay date picker is shown.
         */
        disableModality(): void;

        /**
         * Adjust the UI so that the given date now appears selected.
         * @param event The event that triggered the selection, such as a mouse click.
         * @param dateMeta The date that is to be selected.
         */
        selectDate(event: JQuery.TriggeredEvent, dateMeta: DayInstantSelectable): void;

        /**
         * Changes the current date of the navigation, i.e. the dates or times that are displayed from which the user
         * can select an option.
         * @param newViewDate New view date to set.
         */
        setNavigationState(newViewDate: Date): void;
        
        /**
         * @return Whether the date picker panel is currently displayed.
         */
        isPanelVisible(): boolean;

        /**
         * When the time picker up or down arrows are clicked and the mouse button is held down for a prolonged period
         * of time: repeatedly increment the minute or hour.
         * @param event Event that occurred, such as a click event.
         * @param interval Amount of time between successive increments.
         * @param type Which part of the time is to be incremented or decremented (hour, minute, or second).
         * @param direction Whether to increment or decrement the time part.
         */
        repeat(event: JQuery.TriggeredEvent, interval: Cardinal, type: ChangeTimeType, direction: OneDimensionalDirection): void;

        /**
         * Updates the time display so that is shows the given time.
         * @param event Event that occurred.
         * @param hour Current hour.
         * @param minute Current minute.
         * @param second Current second.
         */
        updateTime(event: JQuery.TriggeredEvent, hour: HourOfTheDay, minute: MinuteOfTheHour, second: SecondOfTheMinute): void;

        /**
         * After a time was entered, updates the time display so that is shows the given time.
         * @param event Event that occurred.
         * @param newDateTime The time to display.
         */
        updateTimeAfterInput(event: JQuery.TriggeredEvent, newDateTime: Date): void;

        /**
         * Updates the year navigator element that lets the user choose a year so that it reflects the current settings. 
         */
        updateYearNavigator(): void;

        /**
         * Updates the currently displayed date range.
         * @param event Event that occurred.
         * @param value The date to be displayed.
         */
        updateViewDate(event: JQuery.TriggeredEvent, value: Date): void;

        /**
         * Updates the hidden input field and saves the currently selected value.
         * @param event Event that occurred.
         * @param value Date that is selected.
         */
        updateModel(event: JQuery.TriggeredEvent | null, value: Date | Date[] | null): void;

        // ===========================
        // === Date and time logic ===
        // ===========================

        /**
         * Parses a string that either represents a date time, a list of date times, or a date time range, depending on
         * the current {@link PickerOptions.selectionMode} setting.
         *
         * When the given value is a date time, a list of date times, or a date time range already, returns that value
         * unchanged.
         * @param text The string with the date time, date times, or date time range to parse.
         * @return The parsed date time, date times, or date time range.
         */
        parseValue(option: string | Date | Date[]): Date | Date[];

        /**
         * Parses a string that either represent a date time, a list of date times, or a date time range, depending on
         * the current {@link PickerOptions.selectionMode} setting.
         * @param text The string with the date time, date times, or date time range to parse.
         * @return The parsed date time, date times, or date time range.
         */
        parseValueFromString(text: string): Date | Date[];

        /**
         * Parses a string with a time (but no date).
         * @param value The time string to parse.
         * @param ampm Whether it is currently before or after midday.
         * @return The parses date.
         */
        parseTime(value: string, ampm?: HalfDayPeriod): TimeOfTheDay;

        /**
         * Parses a string with a date (but no time).
         * @param value The date string to parse.
         * @param format The format to use for parsing.
         * @return The parsed date.
         */
        parseDate(value: string, format: string): Date;

        /**
         * Parses a string with a date and a time.
         * @param text The date time string to parse.
         * @return The parsed date.
         */
        parseDateTime(text: string): Date;

        /**
          * Parses a texual representation of a date that is stored in the {@link PickerOptions.disabledDates} list.
          * @param option Value to parse as a date.
          * @return The parsed date.
          */
        parseOptionValue(option: string): Date;

        /**
         * Parses the textual representation of a date as stored in {@link PickerOptions.minDate} and
         * {@link PickerOptions.maxDate}.
         * @param option A textual representation of a date to parse.
         * @return The parsed date.
         */
        parseMinMaxValue(option: string | Date): Date | Date[];

        /**
         * Formats the given point in time as a string with a date and a time.
         * @param date A point in time to format.
         * @return A textual representation of the given point in time, with a date and a time part.
         */
        formatDateTime(date: Date | undefined): string | null;

        /**
         * Formats the given point in time as a string, omitting the time part.
         * @param date A point in time to format.
         * @param format Date format to use.
         * @return A textual representation of the given point in time, including the date but omitting the time part.
         */
        formatDate(date: Date | undefined, format: string): string;

        /**
         * Formats the given point in time as a string, omitting the date part.
         * @param date A point in time to parse.
         * @return A text represent of the given point in time, including the time part but omitting the date part.
         */
        formatTime(date: Date | undefined): string;

        /**
         * Converts a date object to an ISO date (date only, no time) string. Useful to check if a dates matches with a
         * date sent from the backend without needing to parse the backend date first.
         * @param date Date to convert.
         * @return The data as an ISO date string.
         */
        toISODateString(date: Date): string;

        /**
         * Finds the day of the week index that represents the first day of the week for the given month.
         * @param month Month to check.
         * @param year Year to check.
         * @return The day of the week index that represents the first day of the week for the given month.
         */
        getFirstDayOfMonthIndex(month: MonthOfTheYear, year: YearInstant): DayOfTheWeek;

        /**
         * Finds the day of the week index that represents sunday.
         * @return The day of the week index that represents sunday.
         */
        getSundayIndex(): number;

        /**
         * Finds the number of day in the given month.
         * @param month Month to check.
         * @param year Year to check.
         * @return The number of days in the given month.
         */
        getDaysCountInMonth(month: MonthOfTheYear, year: YearInstant): Cardinal;

        /**
         * Finds the number of day in month before the given month.
         * @param month Month to check.
         * @param year Year to check.
         * @return The number of days in month before the given month.
         */
        getDaysCountInPrevMonth(month: MonthOfTheYear, year: YearInstant): Cardinal;

        /**
         * Finds the month of the year index and year index of the month preceding the given month.
         * @param month Month to check.
         * @param year Year to check.
         * @return The month before the given month.
         */
        getPreviousMonthAndYear(month: MonthOfTheYear, year: YearInstant): MonthInstant;

        /**
         * Finds the month of the year index and year index of the month succeeding the given month.
         * @param month Month to check.
         * @param year Year to check.
         * @return The month after the given month.
         */
        getNextMonthAndYear(month: MonthOfTheYear, year: YearInstant): MonthInstant;

        /**
         * Finds the formatted date or time string that is to be shown as the currently selected date or time.
         * @return The currently selected date or time, formatted according to the current options.
         */
        getValueToRender(): string;

        /**
         * Creates a list of super short day names in a week.
         * @return A list with the super sort day names in a week.
         */
        createWeekDaysMin(): string[];

        /**
         * Creates a list of short day names in a week.
         * @return A list with the sort day names in a week.
         */
        createWeekDaysShort(): string[];

        /**
         * Creates a list of long day names in a week.
         * @return A list with the long day names in a week.
         */
        createWeekDays(): string[];

        /**
         * Creates a list of all days in the year, starting at the given month.
         * @param month Month where to start. Months before that are not included in the returned list.
         * @param year Year to check.
         * @return A list with all days in the year, starting at the given month.
         */
        createMonths(month: MonthOfTheYear, year: YearInstant): DayListInMonth[];

        /**
         * Creates a list of all days in the given month.
         * @param month A month to check.
         * @param year A year to check.
         * @param index Index that will be included in the return value. 
         * @return All days in the given month.
         */
        createMonth(month: MonthOfTheYear, year: YearInstant, index: number): DayListInMonth;

        /**
         * @param value A value to check whether it is a Date instance.
         * @return `true` if the value is an instance of `Date`, and `false` otherwise.
         */
        isDate(value: unknown): value is Date;

        /**
         * Checks whether thee given day can be selected.
         * @param day A day to check.
         * @param month A month to check.
         * @param year A year to check.
         * @param otherMonth Whether the given month belongs to another month other than the currently displayed month.
         * See {@link PickerOptions.selectOtherMonths}.
         */
        isSelectable(day: DayOfTheMonth, month: MonthOfTheYear, year: YearInstant, otherMonth: boolean): boolean;

        /**
         * Checks whether the given day is selected.
         * @param dateMeta Day to check.
         * @return Whether the given day is selected.
         */
        isSelected(dateMeta: DayInstantSelectable): boolean;

        /**
         * Checks whether the {@link PickerOptions.selectionMode} is currently set to `single`.
         * @return Whether only a single date can be selected.
         */
        isSingleSelection(): boolean;

        /**
         * Checks whether the {@link PickerOptions.selectionMode} is currently set to `range`.
         * @return Whether a range of dates can be selected.
         */
        isRangeSelection(): boolean;

        /**
         * Checks whether the {@link PickerOptions.selectionMode} is currently set to `multiple`.
         * @return Whether multiple dates can be selected.
         */
        isMultipleSelection(): boolean;

        /**
         * Checks whether the given month is currently selected.
         * @param month A month to check.
         * @return Whether the given month is currently selected.
         */
        isMonthSelected(month: MonthOfTheYear): boolean;

        /**
         * Checks whether the given date equals the other given date.
         * @param value First date for the comparison.
         * @param dateMeta Other date for the comparison.
         * @return `true` if both given values represent the same date, or `false` otherwise.
         */
        isDateEquals(value: Date | undefined, dateMeta: DayInstantSelectable): boolean;

        /**
         * Checks whether the given date lies in the given range.
         * @param start Start point of the date range.
         * @param end End point of the date range.
         * @param dateMeta Date to check whether it lies in the given range.
         * @return `true` if the given date lies in the range `[start, end]` (inclusive), or `false` otherwise.
         */
        isDateBetween(start: Date | undefined, end: Date | undefined, dateMeta: DayInstantSelectable): boolean;

        /**
         * Checks whether the given date is equal to the current date.
         * @param today The date of today.
         * @param day Day to check.
         * @param month Month to check.
         * @param year Year to check.
         * @return `true` if the given `today` represents the same date as the given `day`, `month`, and `year`.
         */
        isToday(today: Date, day: DayOfTheMonth, month: MonthOfTheYear, year: YearInstant): boolean;

        /**
         * Checks whether the given date is currently disabled and cannot be selected.
         * @param day Day to check.
         * @param month Month to check.
         * @param year Year to check.
         * @return Whether the given date is currently disabled and cannot be selected.
         */
        isDateDisabled(day: DayOfTheMonth, month: MonthOfTheYear, year: YearInstant): boolean;

        /**
         * Checks whether the given day is currently disabled and cannot be selected.
         * @param day Day to check.
         * @param month Month to check.
         * @param year Year to check.
         * @return Whether the given day is currently disabled and cannot be selected.
         */
        isDayDisabled(day: DayOfTheMonth, month: MonthOfTheYear, year: YearInstant): boolean;

        /**
         * Checks whether the year of the currently displayed month page is equal to the year of the
         * {@link PickerOptions.minDate}.
         * @return Whether the year of the currently displayed month page is equal to the year of the
         * {@link PickerOptions.minDate}.
         */
        isInMinYear(): boolean;

        /**
         * Checks whether the year of the currently displayed month page is equal to the year of the
         * {@link PickerOptions.maxDate}.
         * @return Whether the year of the currently displayed month page is equal to the year of the
         * {@link PickerOptions.maxDate}.
         */
        isInMaxYear(): boolean;

        daylightSavingAdjust(date: Date): Date;
        populateTime(value: Date, timeString: string, ampm?: HalfDayPeriod): void;
        validateTime(hour: HourOfTheDay, minute: MinuteOfTheHour, second: SecondOfTheMinute, value: Date, direction: AlterationMode): boolean;

        // =================
        // === Rendering ===
        // =================

        /**
         * Creates the HTML snippet for the trigger button and saves it in this instance.
         */
        renderTriggerButton(): void;

        /**
         * Creates the HTML snippet for the date picker panel and saves it in this instance.
         */
        renderDatePickerPanel(): void;

        /**
         * Creates the HTML snippet for the panel elements.
         * @return The rendered HTML snippet.
         */
        renderPanelElements(): string;

        /**
         * Creates the HTML snippet for the date view that shows the current month page.
         * @return The rendered HTML snippet.
         */
        renderDateView(): string;

        /**
         * Creates the HTML snippet for the month view with the days in the current month.
         * @return The rendered HTML snippet.
         */
        renderMonthView(): string;

        /**
         * Creates the HTML snippet for the time picker that lets the user select an hour, minute, and second.
         * @return The rendered HTML snippet.
         */
        renderTimePicker(): string;

        /**
         * Creates the HTML snippet for the button bar with the today and clear buttons.
         * @return The rendered HTML snippet.
         */
        renderButtonBar(): string;

        /**
         * Creates the HTML snippet for the month part of the month view.
         * @param index Month to use.
         * @return The rendered HTML snippet.
         */
        renderMonthViewMonth(index: MonthOfTheYear): string;

        /**
         * Creates the HTML snippet for the month list part of the month view.
         * @return The rendered HTML snippet.
         */
        renderMonthViewMonths(): string;

        /**
         * Creates the HTML snippet for the given days.
         * @param monthsMetadata List of days to render.
         * @return The rendered HTML snippet.
         */
        renderMonths(monthsMetadata: DayListInMonth[]): string;

        /**
         * Creates the HTML snippet for the given days in a month.
         * @param monthMetadata List of days to render
         * @param index Month to which the days belong.
         * @return The rendered HTML snippet.
         */
        renderMonth(monthMetadata: DayListInMonth, index: MonthOfTheYear): string;

        /**
         * Creates the HTML snippet for the button for navigating to the previous month.
         * @return The rendered HTML snippet.
         */
        renderBackwardNavigator(): string;

        /**
         * Creates the HTML snippet for the button for navigating to the next month.
         * @return The rendered HTML snippet.
         */
        renderForwardNavigator(): string;

        /**
         * Creates the HTML snippet for a title bar that shows the given month.
         * @param month Month to use.
         * @param index 0-based index of the month in the year.
         * @return The rendered HTML snippet.
         */
        renderTitleMonthElement(month: MonthOfTheYear, index: MonthOfTheYear): string;

        /**
         * Creates the HTML snippet for a title bar that shows the given year.
         * @param year Year to use.
         * @param index 0-based index of the month in the year.
         * @return The rendered HTML snippet.
         */
        renderTitleYearElement(year: YearInstant, index: MonthOfTheYear): string;

        /**
         * Creates the HTML snippet for the options elements of the select element in the title bar that lets the user
         * switch to another month.
         * @param name Whether to create the options for months or years.
         * @param options List of month names.
         * @return The rendered HTML snippet.
         */
        renderTitleOptions(name: "month", options: string[]): string;

        /**
         * Creates the HTML snippet for the options elements of the select element in the title bar that lets the user
         * switch to another year.
         * @param name Whether to create the options for months or years.
         * @param options List of year numbers to use as options.
         * @return The rendered HTML snippet.
         */
        renderTitleOptions(name: "year", options: YearInstant[]): string;

        /**
         * Creates the HTML snippet for the title bar of the given month.
         * @param monthMetadata Month to use.
         * @return The rendered HTML snippet.
         */
        renderTitle(monthMetadata: DayListInMonth): string;

        /**
         * Creates the HTML snippet for the names of the given days.
         * @param weekDaysMin List of super short week day names.
         * @param weekDays List of long week day names.
         * @return The rendered HTML snippet.
         */
        renderDayNames(weekDaysMin: string[], weekDays: string[]): string;

        /**
         * Creates the HTML snippet for the days in the given week.
         * @param weekDates List of days in the week.
         * @return The rendered HTML snippet.
         */
        renderWeek(weekDates: DayInstantSelectableRelative[]): string;

        /**
         * Creates the HTML snippet for the content of a date cell with a single day.
         * @param date Date to render.
         * @param dateClass Style class to apply.
         * @return The rendered HTML snippet.
         */
        renderDateCellContent(date: DayInstantSelectableRelative, dateClass: string): string;

        /**
         * Creates the HTML snippet for the given dates.
         * @param monthMetadata List of dates to render.
         * @return The rendered HTML snippet.
         */
        renderDates(monthMetadata: DayListInMonth): string;

        /**
         * Creates the HTML snippet for the date view grid of the given month.
         * @param monthMetadata Month to use.
         * @param weekDaysMin List of super short week day names.
         * @param weekDays List of long week names.
         * @return The rendered HTML snippet.
         */
        renderDateViewGrid(monthMetadata: DayListInMonth, weekDaysMin: string[], weekDays: string[]): string;

        /**
         * Creates the HTML snippet for the hour picker for selecting an hour.
         * @return The rendered HTML snippet.
         */
        renderHourPicker(): string;

        /**
         * Creates the HTML snippet for the minute picker for selecting a minute.
         * @return The rendered HTML snippet.
         */
        renderMinutePicker(): string;

        /**
         * Creates the HTML snippet for the second picker for selecting a second.
         * @return The rendered HTML snippet.
         */
        renderSecondPicker(): string;

        /**
         * Creates the HTML snippet for the picker that lets the user choose between `a.m.` and `p.m.`.
         * @return The rendered HTML snippet.
         */
        renderAmPmPicker(): string;

        /**
         * Creates the HTML snippet for separator between hours, minutes, and seconds (such as a colon).
         * @return The rendered HTML snippet.
         */
        renderSeparator(): string;

        /**
         * Creates the HTML snippet for container with the up and down button.
         * @param containerClass Style class for the container.
         * @param text Text to shown in the time element container.
         * @param type Whether to render the time elements of a hour, minute, or second.
         * @return The rendered HTML snippet.
         */
        renderTimeElements(containerClass: string, text: string, type: ChangeTimeType): string;

        /**
         * Creates the HTML snippet for the button to increment the hour, minutes, or second.
         * @return The rendered HTML snippet.
         */
        renderTimePickerUpButton(): string;

        /**
         * Creates the HTML snippet for the button to decrement the hour, minutes, or second.
         * @return The rendered HTML snippet.
         */
        renderTimePickerDownButton(): string;

        // ======================
        // === Event handling ===
        // ======================

        /**
         * Adds the event listener for click events to the document.
         */
        bindDocumentClickListener(): void;

        /**
         * Removes the event listener for click events from the document.
         */
        unbindDocumentClickListener(): void;

        /**
         * Callback that is invoked when the date input was clicked.
         * @param event Event that occurred.
         */
        onInputClick(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the date input was focused.
         * @param event Event that occurred.
         */
        onInputFocus(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the date input lost focus.
         * @param event Event that occurred.
         */
        onInputBlur(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when a key was pressed in the date input.
         * @param event Event that occurred.
         */
        onInputKeyDown(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the user made an input.
         * @param event Event that occurred.
         */
        onUserInput(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the trigger button was pressed.
         * @param event Event that occurred.
         */
        onButtonClick(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the date picker panel was clicked.
         * @param event Event that occurred.
         */
        onPanelClick(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when a different month was selected in the dropdown menu in the title bar.
         * @param event Event that occurred.
         */
        onMonthDropdownChange(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when a different year was selected in the dropdown menu in the title bar.
         * @param event Event that occurred.
         */
        onYearDropdownChange(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when a different month was selected by clicking on that month.
         * @param event Event that occurred.
         * @param month Month that was selected.
         */
        onMonthSelect(event: JQuery.TriggeredEvent, month: MonthOfTheYear): void;

        /**
         * Callback that is invoked when the left mouse button was pressed down while the cursor is over the time picker
         * element.
         * @param event Event that occurred.
         * @param type Whether the hour, minute, or second was clicked.
         * @param direction Whether the up or down button was clicked.
         */
        onTimePickerElementMouseDown(event: JQuery.TriggeredEvent, type: ChangeTimeType, direction: OneDimensionalDirection): void;

        /**
         * Callback that is invoked when the left mouse button was release while the cursor is over the time picker
         * element.
         * @param event Event that occurred.
         */
        onTimePickerElementMouseUp(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when a date was selected by clicking on it.
         * @param event Event that occurred.
         * @param dateMeta Day that was clicked.
         */
        onDateSelect(event: JQuery.TriggeredEvent, dateMeta: DayInstantSelectable): void;

        /**
         * Callback that is invoked when the today button was pressed.
         * @param event Event that occurred.
         */
        onTodayButtonClick(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the clear button was pressed.
         * @param event Event that occurred.
         */
        onClearButtonClick(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when a value was entered in the hour input.
         * @param input Hour input element.
         * @param event Event that occurred.
         */
        handleHoursInput(input: HTMLElement, event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when a value was entered in the minute input.
         * @param input Minute input element.
         * @param event Event that occurred.
         */
        handleMinutesInput(input: HTMLElement, event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when a value was entered in the second input.
         * @param input Second input element.
         * @param event Event that occurred.
         */
        handleSecondsInput(input: HTMLElement, event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the up button of the hour input was pressed.
         * @param event Event that occurred.
         */
        incrementHour(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the down button of the hour input was pressed.
         * @param event Event that occurred.
         */
        decrementHour(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the up button of the minute input was pressed.
         * @param event Event that occurred.
         */
        incrementMinute(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the down button of the minute input was pressed.
         * @param event Event that occurred.
         */
        decrementMinute(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the up button of the second input was pressed.
         * @param event Event that occurred.
         */
        incrementSecond(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the down button of the second input was pressed.
         * @param event Event that occurred.
         */
        decrementSecond(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when button for navigating to the previous month was pressed.
         * @param event Event that occurred.
         */
        navBackward(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when button for navigating to the next month was pressed.
         * @param event Event that occurred.
         */
        navForward(event: JQuery.TriggeredEvent): void;

        /**
         * Callback that is invoked when the button for switching between `a.m.` and `p.m.` was pressed.
         * @param event Event that occurred.
         */
        toggleAmPm(event: JQuery.TriggeredEvent): void;

        // ============
        // === Misc ===
        // ============

        /**
         * Joins the given style class names for use in the HTML class attribute.
         * @param classes List of style classes.
         * @return The given classes, joined with a space.
         */
        getClassesToAdd(classes: string[]): string;

        /**
         * Escapes characters that have a special meaning in HTML, so that the given value can be used safely as a value
         * in an HTML attribute or text node.
         * @param value Value to escape.
         * @return The given value, escaped for use in an HTML attribute or text node.
         */
        escapeHTML(value: string): string;
    }

    /**
     * The date picker instance used by the JQuery UI widget. You can retrieve it via `$(...).datePicker("instance")`.
     */
    export interface PickerInstance extends PickerWidgetMethods, JQueryUI.WidgetCommonProperties {
        /**
         * The current options of this widget instance.
         */
        options: PickerOptions;

        /**
         * Namespace for event triggered by this widget.
         */
        eventNamespace: string;

        bindings: JQuery;

        /**
         * The DOM element that is hoverable.
         */
        hoverable: JQuery;

        /**
         * The DOM element that is focusable.
         */
        focusable: JQuery;

        /**
         * The container element of the date picker.
         */
        container: JQuery;

        /**
         * The hidden input field containing the currently selected value.
         */
        inputfield: JQuery;

        /**
         * The currently selected date time, list of date times, or date time range.
         */
        value: Date | Date[];

        /**
         * The currently displayed date or dates.
         */
        viewDate: Date | Date[];

        /**
         * Number of microseconds since January 1, 1970.
         */
        ticksTo1970: Cardinal;

        /**
         * The panel element of the date picker.
         */
        panel: JQuery;

        /**
         * List of all days in the current year.
         */
        monthsMetadata: DayListInMonth[];

        /**
         * Mask for the modal overlay.
         */
        mask: JQuery | null;

        /**
         * Trigger button that opens or closes the date picker.
         */
        triggerButton?: JQuery;

        /**
         * Whether a custom year range was specified.
         */
        hasCustomYearRange: boolean;
    }
}

interface JQuery {
    /**
     * Initializes the date picker on the current elements.
     * @param options Optional settings for configuring the date picker.
     * @return This JQuery instance for chaining.
     */
    datePicker(...options: Partial<JQueryPrimeDatePicker.PickerOptions>[]): this;

    /**
     * Retrieves the current date picker instance.
     * @param method Name of the method to call on the widget.
     * @return The current date picker instance, or `undefined` if the widget was not yet initialized.
     */
    datePicker(method: "instance"): JQueryPrimeDatePicker.PickerInstance | undefined;

    /**
     * Removes the widget functionality completely. This will return the element back to its pre-init state.
     * @param method Name of the method to call on the widget.
     * @return This JQuery instance for chaining.
     */
    datePicker(method: "destroy"): this;

    /**
     * Returns a JQuery object containing the original element or other relevant generated elements.
     * @param method Name of the method to call on the widget.
     * @return A JQuery object with the original element or other relevant generated elements.
     */
    datePicker(method: "widget"): JQuery;

    /**
     * Finds the current option of the date picker widget.
     * @param method Name of the method to call on the widget.
     * @return The current options of this date picker widget.
     */
    datePicker(method: "option"): JQueryPrimeDatePicker.PickerOptions;

    /**
     * Updates the current options with the given options.
     * @param method Name of the method to call on the widget.
     * @return This JQuery instance for chaining.
     */
    datePicker(method: "option", options: Partial<JQueryPrimeDatePicker.PickerOptions>): this;

    /**
     * Finds the value of the given option.
     * @typeparam K Name of an option to retrieve.
     * @param method Name of the method to call on the widget.
     * @param optionName Name of an option to retrieve.
     * @return The value of the given option.
     */
    datePicker<
        K extends keyof JQueryPrimeDatePicker.PickerOptions
    >(method: "option", optionName: K): JQueryPrimeDatePicker.PickerOptions[K];

    /**
     * Sets the value of the given option to the given value.
     * @typeparam K Name of an option to set.
     * @param method Name of the method to call on the widget.
     * @param optionName Name of an option to set.
     * @param optionValue Value of the option to set.
     * @return This JQuery instance for chaining.
     */
    datePicker<
        K extends keyof JQueryPrimeDatePicker.PickerOptions
    >(method: "option", optionName: K, optionValue: JQueryPrimeDatePicker.PickerOptions[K]): this;

    /**
     * Calls a method of the {@link JQueryPrimeDatePicker.PickerInstance} and return the result.
     * @typeparam K Name of the method to call on the widget.
     * @param method Name of the method to call on the widget.
     * @param args Arguments as required by the {@link JQueryPrimeDatePicker.PickerInstance} method.
     * @return The value as returned by the {@link JQueryPrimeDatePicker.PickerInstance} method. Return this JQuery
     * instance for chaining when the instance method has no return value.
     * @see {@link PickerWidgetMethods}
     */
    datePicker<
        K extends keyof JQueryPrimeDatePicker.PickerWidgetMethods,
        >(
            method: K,
            ...args: Parameters<JQueryPrimeDatePicker.PickerWidgetMethods[K]>
        ): PrimeFaces.ToJQueryUIWidgetReturnType<
            JQueryPrimeDatePicker.PickerInstance,
            ReturnType<JQueryPrimeDatePicker.PickerWidgetMethods[K]>,
            this
        >;
}