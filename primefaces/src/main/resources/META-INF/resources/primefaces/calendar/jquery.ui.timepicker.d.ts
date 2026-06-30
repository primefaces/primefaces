// Global pollution ...

/**
 * Additional properties that will be set on the global `Date` object when the `Calendar` widget is loaded.
 */
interface Date {
    /**
     * Gets the microseconds.
     * 
     * Defined globally by the Calendar widget. __Do not use this.__
     * 
     * @deprecated
     * @return The microseconds field of this date.
     */
    getMicroseconds(): number;
    /**
     * Set the microseconds.
     * 
     * Defined globally by the Calendar widget. __Do not use this.__
     * 
     * @deprecated
     * @param microseconds The microseconds to set.
     * @return this for chaining.
     */
    setMicroseconds(microseconds: number): this;
}

/**
 * Namespace for the timepicker JQueryUI plugin, available as `JQuery.fn.timepicker` and `JQuery.fn.datetimepicker`.
 * Contains some additional types and interfaces required for the typings.
 */
declare namespace JQueryUITimepickerAddon {
    /**
     * Time units for selecting a time in the calendar widget.
     */
    export type TimeUnit = "hour" | "minute" | "second" | "millisec" | "microsec";

    /**
     * Whether to use sliders, select elements or a custom control type for selecting a time (hour / minute / second) in
     * the time picker.
     */
    export type ControlType = "slider" | "select";

    /**
     * An offset of a timezone, in minutes relative to UTC. For example, `UTC-4` is represented as `-240`.
     */
    export type TimezoneOffset = number;

    /**
     * How dates are parsed by the Timepicker.
     * 
     * - `loose`: Uses the JavaScript method `new Date(timeString)` to guess the time
     * - `strict`: A date text must match the timeFormat exactly.
     */
    export type TimeParseType = "loose" | "strict";

    /**
     * A custom function for parsing a time string.
     */
    export type TimeParseFunction =
    /**
     * @param timeFormat Format according to which to parse the time.
     * @param timeString Time string to parse.
     * @param optins Current options of the time picker.
     * @return The parsed time, or `undefined` if the time string could not be parsed.
     */
    (timeFormat: string, timeString: string, options: Partial<DatetimepickerOptions>) => TimeParseResult | undefined;

    /**
     * Represents the available methods on a JQuery instance for the date and / or time picker.
     */
    export type PickerMethod = "datepicker" | "timepicker" | "datetimepicker";

    /**
     * Represents a timezone of the world.
     */
    export interface Timezone {
        /**
         * Name of the timezone.
         */
        label: string;
        /**
         * Offset of the timezone.
         */
        value: TimezoneOffset;
    }

    /**
     * The timepicker for working with times, such as formatting and parsing times.
     */
    export interface Timepicker {
        /**
         * A map with a locale name (`fr`, `de`, etc.) as the key and the locale as the value.
         */
        regional: Record<string, Locale>;

        /**
         * Current version of the DateTimePicker JQueryUI add-on.
         */
        version: string;

        /**
         * Override the default settings for all instances of the time picker.
         * @param settings  The new settings to use as defaults.
         * @return this for chaining.
         */
        setDefaults(settings: Partial<DatetimepickerOptions>): this;

        /**
         * Calls `datetimepicker` on the `startTime` and `endTime` elements, and configures them to enforce the date /
         * time range limits.
         * @param startTime DOM element of the date/time picker with the start date/time.
         * @param endTime DOM element of the date/time picker with the end date/time
         * @param options Options for the `$.fn.datetimepicker` call.
         */
        datetimeRange(startTime: JQuery, endTime: JQuery, options: Partial<RangeOptions>): void;

        /**
         * Calls `timepicker` on the `startTime` and `endTime` elements, and configures them to enforce the time range
         * limits.
         * @param startTime DOM element of the date/time picker with the start date/time.
         * @param endTime DOM element of the date/time picker with the end date/time
         * @param options Options for the `$.fn.timepicker` call.
         */
        timeRange(startTime: JQuery, endTime: JQuery, options: Partial<RangeOptions>): void;

        /**
         * Calls `datepicker` on the `startTime` and `endTime` elements, and configures them to enforce the date
         * range limits.
         * @param startTime DOM element of the date/time picker with the start date/time.
         * @param endTime DOM element of the date/time picker with the end date/time
         * @param options Options for the `$.fn.datepicker` call.
         */
        dateRange(startTime: JQuery, endTime: JQuery, options: Partial<RangeOptions>): void;

        /**
         * Calls the given method on the `startTime` and `endTime` elements, and configures them to enforce the date /
         * time range limits.
         * @param method Whether to call the `datepicker`, `timepicker`, or `datetimepicker` method on the elements.
         * @param startTime DOM element of the date/time picker with the start date/time.
         * @param endTime DOM element of the date/time picker with the end date/time
         * @param options Options for the `$.fn.datepicker` call.
         * @return A JQuery instance containing the given `startTime` and `endTime` elements.
         */
        handleRange(method: PickerMethod, startTime: JQuery, endTime: JQuery, options: Partial<RangeOptions>): JQuery;

        /**
         * Get the timezone offset as string from a timezone offset.
         * @param tzMinutes If not a number, less than `-720` (`UTC-12`), or greater than `840` (`UTC+14`),
         * this value is returned as-is
         * @param iso8601 If `true` formats in accordance to `iso8601` (sucha as `+12:45`).
         * @return The timezone offset as a string, such as `+0530` for `UTC+5.5`. 
         */
        timezoneOffsetString(tzMinutes: TimezoneOffset | string, iso8601: boolean): string;

        /**
         * Get the number in minutes that represents a timezone string.
         * @param tzString A formatted time zone string, such as `+0500`, `-1245`, or `Z`.
         * @return The offset in minutes, or the given `tzString` when it does not represent a valid timezone.
         */
        timezoneOffsetNumber(tzString: string): TimezoneOffset | string;

        /**
         * JavaScript `Date`s have not support for timezones, so we must adjust the minutes to compensate.
         * @param date Date to adjust.
         * @param fromTimezone Timezone of the given date.
         * @param toTimezone Timezone to adjust the date to, relative to the `fromTimezone`.
         * @return The given date, adjusted from the `fromTimezone` to the `toTimezone`.
         */
        timezoneAdjust(date: Date, fromTimezone: string, toTimezone: string): Date;

        /**
         * Log error or data to the console during error or debugging.
         * @param args Data to log.
         */
        log(...args: readonly unknown[]): void;
    }

    /**
     * Represents localized messages for a certain locale that are displayed by the datetimepicker.
     */
    export interface Locale {
        /**
         * Default: `["AM", "A"]`, A Localization Setting - Array of strings to try and parse against to determine AM.
         */
        amNames: string[];
        /**
         * Default: `["PM", "P"]`, A Localization Setting - Array of strings to try and parse against to determine PM.
         */
        pmNames: string[];
        /**
         * Default: `HH:mm`, A Localization Setting - String of format tokens to be replaced with the time.
         */
        timeFormat: string;
        /**
         * Default: Empty string, A Localization Setting - String to place after the formatted time.
         */
        timeSuffix: string;
        /**
         * Default: `Choose Time`, A Localization Setting - Title of the wigit when using only timepicker.
         */
        timeOnlyTitle: string;
        /**
         * Default: `Time`, A Localization Setting - Label used within timepicker for the formatted time.
         */
        timeText: string;
        /**
         * Default: `Hour`, A Localization Setting - Label used to identify the hour slider.
         */
        hourText: string;
        /**
         * Default: `Minute`, A Localization Setting - Label used to identify the minute slider.
         */
        minuteText: string;
        /**
         * Default: `Second`, A Localization Setting - Label used to identify the second slider.
         */
        secondText: string;
        /**
         * Default: `Millisecond`, A Localization Setting - Label used to identify the millisecond slider.
         */
        millisecText: string;
        /**
         * Default: `Microsecond`, A Localization Setting - Label used to identify the microsecond slider.
         */
        microsecText: string;
        /**
         * Default: `Timezone`, A Localization Setting - Label used to identify the timezone slider.
         */
        timezoneText: string;
    }

    /**
     * Represents the result of parsing a time string.
     */
    export interface TimeParseResult {
        /**
         * Hour of the time, starting at `0`.
         */
        hour: number;
        /**
         * Minute of the time, starting at `0`.
         */
        minute: number;
        /**
         * Seconds of the time, starting at `0`.
         */
        seconds: number;
        /**
         * Milliseconds of the time, starting at `0`.
         */
        millisec: number;
        /**
         * Microseconds of the time, starting at `0`.
         */
        microsec: number;
        /**
         * Timezone of the time.
         */
        timezone?: TimezoneOffset; 
    }

    /**
     * Options for the date time picker that lets the user select a time.
     */
    export interface DatetimepickerOptions extends JQueryUI.DatepickerOptions, Locale {
        /**
         * Default: `true` - When `altField` is used from datepicker, `altField` will only receive the formatted time
         * and the original field only receives date.
         */
        altFieldTimeOnly: boolean;
        /**
         * Default: (separator option) - String placed between formatted date and formatted time in the altField.
         */
        altSeparator: string;
        /**
         * Default: (timeSuffix option) - String always placed after the formatted time in the altField.
         */
        altTimeSuffix: string;
        /**
         * Default: (timeFormat option) - The time format to use with the altField.
         */
        altTimeFormat: string;
        /**
         * Default: true - Whether to immediately focus the main field whenever the altField receives focus. Effective
         * at construction time only, changing it later has no effect.
         */
        altRedirectFocus: boolean;
        /**
         * Default: [generated timezones] - An array of timezones used to populate the timezone select.
         */
        timezoneList: Timezone[] | Record<string, TimezoneOffset>;
        /**
         * Default: `slider` - How to select a time (hour / minute / second). If `slider` is unavailable through
         * jQueryUI, `select` will be used. For advanced usage you may set this to a custom control to use controls
         * other than sliders or selects.
         */
        controlType: ControlType | CustomControl;
        /**
         * Default: `null` - Whether to show the hour control.  The default of `null` will use detection from timeFormat.
         */
        showHour: boolean | null;
        /**
         * Default: `null` - Whether to show the minute control.  The default of `null` will use detection from
         * timeFormat.
         */
        showMinute: boolean | null;
        /**
         * Default: `null` - Whether to show the second control.  The default of `null` will use detection from
         * timeFormat.
         */
        showSecond: boolean | null;
        /**
         * Default: `null` - Whether to show the millisecond control.  The default of `null` will use detection from
         * timeFormat.
         */
        showMillisec: boolean | null;
        /**
         * Default: `null` - Whether to show the microsecond control.  The default of `null` will use detection from
         * timeFormat.
         */
        showMicrosec: boolean | null;
        /**
         * Default: `null` - Whether to show the timezone select.
         */
        showTimezone: boolean | null;
        /**
         * Default: true - Whether to show the time selected within the datetimepicker.
         */
        showTime: boolean;
        /**
         * Default: `1` - Hours per step the slider makes.
         */
        stepHour: number;
        /**
         * Default: `1` - Minutes per step the slider makes.
         */
        stepMinute: number;
        /**
         * Default: `1` - Seconds per step the slider makes.
         */
        stepSecond: number;
        /**
         * Default: `1` - Milliseconds per step the slider makes.
         */
        stepMillisec: number;
        /**
         * Default: `1` - Microseconds per step the slider makes.
         */
        stepMicrosec: number;
        /**
         * Default: `0` - Initial hour set.
         */
        hour: number;
        /**
         * Default: `0` - Initial minute set.
         */
        minute: number;
        /**
         * Default: `0` - Initial second set.
         */
        second: number;
        /**
         * Default: `0` - Initial millisecond set.
         */
        millisec: number;
        /**
         * Default: `0` - Initial microsecond set.  Note: Javascript's native `Date` object does not natively support
         * microseconds.  Timepicker extends the Date object with `Date.prototype.setMicroseconds(m)` and
         * `Date.prototype.getMicroseconds()`. Date comparisons will not acknowledge microseconds. Use this only for
         * display purposes.
         */
        microsec: number;
        /**
         * Default: `null` - Initial timezone set.  If `null`, the browser's local timezone will be used.
         */
        timezone: TimezoneOffset | null;
        /**
         * Default: `0` - The minimum hour allowed for all dates.
         */
        hourMin: number;
        /**
         * Default: `0` - The minimum minute allowed for all dates.
         */
        minuteMin: number;
        /**
         * Default: `0` - The minimum second allowed for all dates.
         */
        secondMin: number;
        /**
         * Default: `0` - The minimum millisecond allowed for all dates.
         */
        millisecMin: number;
        /**
         * Default: `0` - The minimum microsecond allowed for all dates.
         */
        microsecMin: number;
        /**
         * Default: `23` - The maximum hour allowed for all dates.
         */
        hourMax: number;
        /**
         * Default: `59` - The maximum minute allowed for all dates.
         */
        minuteMax: number;
        /**
         * Default: `59` - The maximum second allowed for all dates.
         */
        secondMax: number;
        /**
         * Default: `999` - The maximum millisecond allowed for all dates.
         */
        millisecMax: number;
        /**
         * Default: `999` - The maximum microsecond allowed for all dates.
         */
        microsecMax: number;
        /**
         * Default: `0` - When greater than `0` a label grid will be generated under the slider.  This number represents
         * the units (in hours) between labels.
         */
        hourGrid: number;
        /**
         * Default: `0` - When greater than `0` a label grid will be generated under the slider. This number represents
         * the units (in minutes) between labels.
         */
        minuteGrid: number;
        /**
         * Default: `0` - When greater than `0` a label grid will be genereated under the slider. This number represents
         * the units (in seconds) between labels.
         */
        secondGrid: number;
        /**
         * Default: `0` - When greater than `0` a label grid will be genereated under the slider. This number represents
         * the units (in milliseconds) between labels.
         */
        millisecGrid: number;
        /**
         * Default: `0` - When greater than `0` a label grid will be genereated under the slider. This number represents
         * the units (in microseconds) between labels.
         */
        microsecGrid: number;
        /**
         * Default: `true` - Whether to show the button panel at the bottom. This is generally needed.
         */
        showButtonPanel: boolean;
        /**
         * Default: `false` - Allows direct input in time field
         */
        timeInput: boolean;
        /**
         * Default: `false` - Hide the datepicker and only provide a time interface.
         */
        timeOnly: boolean;
        /**
         * Default: `false` - Show the date and time in the input, but only allow the timepicker.
         */
        timeOnlyShowDate: boolean;
        /**
         * Default: unset - Function to be called when the timepicker or selection control is injected or re-rendered.
         */
        afterInject(this: Timepicker): void;
        /**
         * Default: unset - Function to be called when a date is chosen or time has changed.
         * @param datetimeText Currently selected date as text.
         * @param timepicker The current timepicker instance.
         */
        onSelect(this: HTMLElement | null, datetimeText: string, timepicker: Timepicker): void;
        /**
         * Default: `true` - Always have a time set internally, even before user has chosen one.
         */
        alwaysSetTime: boolean;
        /**
         * Default: space (` `) - When formatting the time this string is placed between the formatted date and
         * formatted time.
         */
        separator: string;
        /**
         * Default: (timeFormat option) - How to format the time displayed within the timepicker.
         */
        pickerTimeFormat: string;
        /**
         * Default: (timeSuffix option) - String to place after the formatted time within the timepicker.
         */
        pickerTimeSuffix: string;
        /**
         * Default: `true` - Whether to show the timepicker within the datepicker.
         */
        showTimepicker: boolean;
        /**
         * Default: `false` - Try to show the time dropdowns all on one line. This should be used with `controlType`
         * `select` and as few units as possible.
         */
        oneLine: boolean;
        /**
         * Default: `null` - String of the default time value placed in the input on focus when the input is empty.
         */
        defaultValue: string | null;
        /**
         * Default: `null` - Date object of the minimum datetime allowed.  Also available as minDate.
         */
        minDateTime: Date | null;
        /**
         * Default: `null` - Date object of the maximum datetime allowed. Also Available as maxDate.
         */
        maxDateTime: Date | null;
        /**
         * Default: `null` - String of the minimum time allowed. '8:00 am' will restrict to times after 8am
         */
        minTime: string | null;
        /**
         * Default: `null` - String of the maximum time allowed. '8:00 pm' will restrict to times before 8pm
         */
        maxTime: string | null;
        /**
         * Default: `strict` - How to parse the time string. You may also set this to a function to handle the parsing
         * yourself.
         */
        parse: TimeParseType | TimeParseFunction;
    }

    /**
     * Optionts for the various methods of the `Timepicker` for working time date / time ranges.
     */
    export interface RangeOptions extends DatetimepickerOptions {
        /**
         * Min allowed interval in milliseconds
         */
        minInterval: number;
        /**
         * Max allowed interval in milliseconds
         */
        maxInterval: number;
        /**
         * Options that are applied only to the date / time picker for the start date / time.
         */
        start: Partial<DatetimepickerOptions>;
        /**
         * Options that are applied only to the date / time picker for the end date / time.
         */
        end: Partial<DatetimepickerOptions>;
    }


    /**
     * Options for a custom control for selecting an hour, minute, or seconds. The control should behave in such a way
     * that the user may select a number in the set `{ min, min+step, min+2*step, ..., max }`.
     */
    export interface ControlOptions {
        /**
         * Maximum allowed value for the time unit the user may select.
         */
        max: number;
        /**
         * Minumum allowed value for the time unit the user may select.
         */
        min: number;
        /**
         * Desired step size for selecting a value.
         */
        step: number;
    }

    /**
     * For advanced usage of the Calendar, you may pass an object of this type to use controls other than sliders and
     * selects for selecting an hour, minute, or second.
     */
    export interface CustomControl {
        /**
         * Creates the control for the given time unit and appends it to the given `container` element.
         * @param instance The current date time picker instance.
         * @param container The container element to which the created control must be appended.
         * @param unit The type of control for which to set the value.
         * @param val Initial value for the control
         * @param min Minumum allowed value for the time unit the user may select.
         * @param max Maximum allowed value for the time unit the user may select.
         * @param step Desired step size for selecting a value.
         * @return The `container` element as passed to this method.
         */
        create(instance: Timepicker, container: JQuery, unit: TimeUnit, val: number, min: number, max: number, step: number): JQuery;
        /**
         * Sets the given ooptions on the control for the given time unit.
         * @param instance The current date time picker instance.
         * @param container The container element of the control, as passed to `create`.
         * @param unit The type of control for which to apply the options.
         * @param opts Options to apply on the control
         * @return The `container` element as passed to this method.
         */
        options(instance: Timepicker, container: JQuery, unit: TimeUnit, opts: Partial<ControlOptions>): JQuery;
        /**
         * Sets the value of control for the given time uit.
         * @param instance The current date time picker instance.
         * @param container The container element of the control, as passed to `create`.
         * @param unit The type of control for which to set the value.
         * @param val Value to set on this control.
         * @return The `container` element as passed to this method.
         */
        value(instance: Timepicker, container: JQuery, unit: TimeUnit, val: number): JQuery;
        /**
         * Gets the current value of the control for the given time unit.
         * @param instance The current date time picker instance.
         * @param container The container element of the control, as passed to `create`.
         * @param unit The type of control for which to get the value.
         * @return The current value of the control.
         */
        value(instance: Timepicker, container: JQuery, unit: TimeUnit): number;
    }
}

interface JQuery {
    /**
     * Initializes the datetimepicker on this element. It lets the user select both a date and a time (hour and
     * minute).
     * @param cfg Options for the datetimepicker.
     * @return this for chaining.
     */
    datetimepicker(cfg?: Partial<JQueryUITimepickerAddon.DatetimepickerOptions>): this;

    /**
     * Sets and selects the given date.
     * @param methodName Name of the method to invoke.
     * @param date The new date to select. When not given, unselects the date.
     * @return this for chaining.
     */
    datetimepicker(methodName: "setDate", date?: Date): this;

    /**
     * Finds the currently selected date of the datetimepicker.
     * @param methodName Name of the method to invoke.
     * @return The currently selected date, or `null` if no date is selected.
     */
    datetimepicker(methodName: "getDate"): Date | null;

    /**
     * Enables the datetimepicker so that the user can now select a date.
     * @param methodName Name of the method to invoke.
     * @return this for chaining.
     */
    datetimepicker(methodName: "enable"): this;

    /**
     * Disables the datetimepicker so that the user cannot select a date anymore.
     * @param methodName Name of the method to invoke.
     * @return this for chaining.
     */
    datetimepicker(methodName: "disable"): this;

    /**
     * Sets the minimum allowed date the user may select.
     * @param methodName Name of the method to invoke.
     * @param optionName Name of the option to set.
     * @param date New value for the option.
     * @return this for chaining.
     */
    datetimepicker(methodName: "option", optionName: "minDate", date: Date): this;

    /**
     * Sets the maximum allowed date the user may select.
     * @param methodName Name of the method to invoke.
     * @param optionName Name of the option to set.
     * @param date New value for the option.
     * @return this for chaining.
     */
    datetimepicker(methodName: "option", optionName: "maxDate", date: Date): this;

    /**
     * Initializes the timepicker on this element. It lets the user select a time (hour and minute).
     * @param cfg Options for the datetimepicker.
     * @return this for chaining.
     */
    timepicker(cfg?: Partial<JQueryUITimepickerAddon.DatetimepickerOptions>): this;
}

interface JQueryStatic {
    /**
     * The global instance of the timepicker utility class for working with times.
     */
    timepicker: JQueryUITimepickerAddon.Timepicker;
}