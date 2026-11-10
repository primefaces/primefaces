import type { RaphaelElement, RaphaelPaper } from "raphael";

/**
 * The configuration for the {@link SimpleDateFormat} helper. The properties besides `pattern`, `locale` and
 * `minimalDaysInFirstWeek` are set by the constructor.
 */
export interface SimpleDateFormatCfg {
    /** Localized day names (`Monday`, `Tuesday` etc.) */
    dayNames: string[];
    /** The locale for formatting dates. */
    locale: string | undefined;
    /**
     * Minimal number of days a week is allowed to have to be considered a "full" week, used by `getWeekInMonth` in
     * `getWeekInYear`.
     */
    minimalDaysInFirstWeek?: number;
    /** Localized month names (`January`, `February` etc.) */
    monthNames: string[];
    /** The date format pattern, such as `MM/dd/yyyy HH:mm:ss`. */
    pattern: string;
    /** A regex for splitting a date format into its components. */
    regex: RegExp;
    /** Object with the different keywords used by the date format. */
    types: Record<string, number>;
    /** Type of a pattern letter: text, at least 2 letters. */
    TEXT2: number;
    /** Type of a pattern letter: text, at least 3 letters. */
    TEXT3: number;
    /** Type of a pattern letter: number. */
    NUMBER: number;
    /** Type of a pattern letter: year. */
    YEAR: number;
    /** Type of a pattern letter: month. */
    MONTH: number;
    /** Type of a pattern letter: time zone. */
    TIMEZONE: number;
    /** One day in milliseconds. */
    ONE_DAY: number;
    /** One week in milliseconds. */
    ONE_WEEK: number;
    /** The default of `minimalDaysInFirstWeek`. */
    DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK: number;
}

/**
 * Code ported from Tim Down's http://www.timdown.co.uk/code/simpledateformat.php
 *
 * Helper class for working with `Date`s and date formats.
 */
export class SimpleDateFormat {
    /** The configuration of this helper. */
    cfg: SimpleDateFormatCfg;

    /**
     * Creates a new date format helper.
     * @param cfg The pattern and locale of the date format.
     */
    constructor(cfg: Pick<SimpleDateFormatCfg, "pattern" | "locale" | "minimalDaysInFirstWeek">) {
        // the remaining properties are set below
        this.cfg = cfg as SimpleDateFormatCfg;
        this.cfg.regex = /('[^']*')|(G+|y+|M+|w+|W+|D+|d+|F+|E+|a+|H+|k+|K+|h+|m+|s+|S+|Z+)|([a-zA-Z]+)|([^a-zA-Z']+)/
        this.cfg.TEXT2 = 0;
        this.cfg.TEXT3 = 1;
        this.cfg.NUMBER = 2;
        this.cfg.YEAR = 3;
        this.cfg.MONTH = 4;
        this.cfg.TIMEZONE = 6;
        this.cfg.types = {
            G : this.cfg.TEXT2,
            y : this.cfg.YEAR,
            M : this.cfg.MONTH,
            w : this.cfg.NUMBER,
            W : this.cfg.NUMBER,
            D : this.cfg.NUMBER,
            d : this.cfg.NUMBER,
            F : this.cfg.NUMBER,
            E : this.cfg.TEXT3,
            a : this.cfg.TEXT2,
            H : this.cfg.NUMBER,
            k : this.cfg.NUMBER,
            K : this.cfg.NUMBER,
            h : this.cfg.NUMBER,
            m : this.cfg.NUMBER,
            s : this.cfg.NUMBER,
            S : this.cfg.NUMBER,
            Z : this.cfg.TIMEZONE
        };

        this.cfg.ONE_DAY = 24 * 60 * 60 * 1000;
        this.cfg.ONE_WEEK = 7 * this.cfg.ONE_DAY;
        this.cfg.DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK = 1;

        const localeSettings = PrimeFaces.getLocaleSettings(this.cfg.locale);
        if(localeSettings) {
            this.cfg.monthNames = localeSettings.monthNames!;
            this.cfg.dayNames = localeSettings.dayNames!;
        }
        else {
            this.cfg.monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
            this.cfg.dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
        }
    }

    /**
     * Creates a new date object that represents midnighht of the given year, month, and day.
     * @param year A year to set. `0` repesents the year `1900`, `100` the year `2000`.
     * @param month A month (of the year) to set. `0` is January, `11` is `December`.
     * @param day  A day (of the month) to set, in the range `1...31`.
     * @returns A date for the given year, month, and day at at midnight.
     */
    newDateAtMidnight(year: number, month: number, day: number): Date {
        const d = new Date(year, month, day, 0, 0, 0);
        d.setMilliseconds(0);
        return d;
    }

    /**
     * Computes the difference between the two given dates.
     * @param date1 First input date
     * @param date2 Second input date
     * @returns Time in milliseconds between the two dates (`date1-date2`).
     */
    getDifference(date1: Date, date2: Date): number {
        return date1.getTime() - date2.getTime();
    }

    /**
     * Checks whether the first given date lies before the second given date.
     * @param date1 First input date
     * @param date2 Second input date
     * @returns `true` if `date1` lies before `date2`, or `false` otherwise.
     */
    isBefore(date1: Date, date2: Date): boolean {
        return date1.getTime() < date2.getTime();
    }

    /**
     * Converts the given date to UTC time, that is, the number of milliseconds between midnight, January 1, 1970
     * Universal Coordinated Time (UTC) (or GMT) and the given date.
     * @param date Date to convert to UTC.
     * @returns The given date, converted to UTC time.
     */
    getUTCTime(date: Date): number;
    /**
     * Converts the given date to UTC time, `undefined` when no date is given.
     * @param date `null` or `undefined`.
     * @returns `undefined`.
     */
    getUTCTime(date: null | undefined): undefined;
    getUTCTime(date: Date | null | undefined): number | undefined {
        if(date != undefined){
            return Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds(), date.getMilliseconds());
        }

        return undefined;
    }

    /**
     * Finds the difference in milliseconds between the two given date (`date1-date2`).
     * @param date1 First input date
     * @param date2 Second input date
     * @returns The numer of milliseconds between the two given date (`date1-date2`).
     */
    getTimeSince(date1: Date, date2: Date): number {
        return this.getUTCTime(date1) - this.getUTCTime(date2);
    }

    /**
     * Finds closest Sunday preceding the given date. If the date is already a Sunday, that day is returned.
     * @param date Input date.
     * @returns The date at midnight of the first Sunday before the given date. If the given date is already a
     * Sunday, that day is returned.
     */
    getPreviousSunday(date: Date): Date {
        // Using midday avoids any possibility of DST messing things up
        const midday = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12, 0, 0);
        const previousSunday = new Date(midday.getTime() - date.getDay() * this.cfg.ONE_DAY);

        return this.newDateAtMidnight(previousSunday.getFullYear(), previousSunday.getMonth(), previousSunday.getDate());
    }

    /**
     * Computes the ordinal index of the week of the year of the given date.
     * @param date Date to check.
     * @param minimalDaysInFirstWeek Minimal number of days the first week of the year is allowed to have. If
     * the first week contains less days, the returned output is decremented by one (if you do not want to count, say,
     * 2 days, as week).
     * @returns The week of the year of the given date, starting at `0`.
     */
    getWeekInYear(date: Date, minimalDaysInFirstWeek: number): number {
        const previousSunday = this.getPreviousSunday(date);
        const startOfYear = this.newDateAtMidnight(date.getFullYear(), 0, 1);
        const numberOfSundays = this.isBefore(previousSunday, startOfYear) ? 0 : 1 + Math.floor(this.getTimeSince(previousSunday,startOfYear) / this.cfg.ONE_WEEK);
        const numberOfDaysInFirstWeek =  7 - startOfYear.getDay();
        let weekInYear = numberOfSundays;
        if (numberOfDaysInFirstWeek < minimalDaysInFirstWeek) {
            weekInYear--;
        }

        return weekInYear;
    }

    /**
     * Computes the ordinal index of the week of the month of the given date.
     * @param date Date with a month to check.
     * @param minimalDaysInFirstWeek Minimal number of days the first week of the month is allowed to have. If
     * the first week contains less days, the returned output is decremented by one (if you do not want to count, say,
     * 2 days, as week).
     * @returns The week of the month of the given date, starting at `0`.
     */
    getWeekInMonth(date: Date, minimalDaysInFirstWeek: number): number {
        const previousSunday = this.getPreviousSunday(date);
        const startOfMonth = this.newDateAtMidnight(date.getFullYear(), date.getMonth(), 1);
        const numberOfSundays = this.isBefore(previousSunday,startOfMonth) ? 0 : 1 + Math.floor((this.getTimeSince(previousSunday, startOfMonth)) / this.cfg.ONE_WEEK);
        const numberOfDaysInFirstWeek =  7 - startOfMonth.getDay();
        let weekInMonth = numberOfSundays;
        if (numberOfDaysInFirstWeek >= minimalDaysInFirstWeek) {
            weekInMonth++;
        }

        return weekInMonth;
    }

    /**
     * Computes the ordinal index of the given day in the given year.
     * @param date A day to check.
     * @returns The ordinal index of the given day relative to the beginning of the year, starting at `1`.
     */
    getDayInYear(date: Date): number {
        const startOfYear = this.newDateAtMidnight(date.getFullYear(), 0, 1);

        return 1 + Math.floor(this.getTimeSince(date, startOfYear) / this.cfg.ONE_DAY);
    }

    /**
     * Finds the currently configured value of how many days a week must have at least to be considered a "full" week.
     * Weeks with less that that number of days are disregarded in `getWeekInMonth` and `getWeekInYear`.
     * @param days Unused.
     * @returns The minimal number of days a week is allowed to have to be considered a "full" week.
     */
    getMinimalDaysInFirstWeek(days?: unknown): number {
        return this.cfg.minimalDaysInFirstWeek === undefined ? this.cfg.DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK : this.cfg.minimalDaysInFirstWeek;
    }

    /**
     * Format sthe given given according to the pattern of the current widget configuration.
     * @param date A date to format
     * @returns The given date as a formatted string.
     */
    format(date: Date): string {
        let formattedString = "";
        let result: RegExpExecArray | null;

        const padWithZeroes = function(str: string, len: number): string {
            while (str.length < len) {
                str = "0" + str;
            }
            return str;
        };

        const formatText = function(data: string, numberOfLetters: number, minLength: number): string {
            return (numberOfLetters >= 4) ? data : data.substr(0, Math.max(minLength, numberOfLetters));
        };

        const formatNumber = function(data: string | number, numberOfLetters: number): string {
            const dataString = "" + data;
            // Pad with 0s as necessary
            return padWithZeroes(dataString, numberOfLetters);
        };

        let searchString = this.cfg.pattern;
        while ((result = this.cfg.regex.exec(searchString))) {
            const quotedString = result[1];
            const patternLetters = result[2];
            const otherLetters = result[3];
            const otherCharacters = result[4];

            // If the pattern matched is quoted string, output the text between the quotes
            if (quotedString) {
                if (quotedString == "''") {
                    formattedString += "'";
                } else {
                    formattedString += quotedString.substring(1, quotedString.length - 1);
                }
            } else if (otherLetters) {
            // Swallow non-pattern letters by doing nothing here
            } else if (otherCharacters) {
                // Simply output other characters
                formattedString += otherCharacters;
            } else if (patternLetters) {
                // Replace pattern letters
                const patternLetter = patternLetters.charAt(0);
                const numberOfLetters = patternLetters.length;
                // a string for the text types (G, E, a), a number otherwise
                let rawData: string | number = "";
                switch (patternLetter) {
                    case "G":
                        rawData = "AD";
                        break;
                    case "y":
                        rawData = date.getFullYear();
                        break;
                    case "M":
                        rawData = date.getMonth();
                        break;
                    case "w":
                        rawData = this.getWeekInYear(date, this.getMinimalDaysInFirstWeek());
                        break;
                    case "W":
                        rawData = this.getWeekInMonth(date, this.getMinimalDaysInFirstWeek());
                        break;
                    case "D":
                        rawData = this.getDayInYear(date);
                        break;
                    case "d":
                        rawData = date.getDate();
                        break;
                    case "F":
                        rawData = 1 + Math.floor((date.getDate() - 1) / 7);
                        break;
                    case "E":
                        rawData = this.cfg.dayNames[date.getDay()]!;
                        break;
                    case "a":
                        rawData = (date.getHours() >= 12) ? "PM" : "AM";
                        break;
                    case "H":
                        rawData = date.getHours();
                        break;
                    case "k":
                        rawData = date.getHours() || 24;
                        break;
                    case "K":
                        rawData = date.getHours() % 12;
                        break;
                    case "h":
                        rawData = (date.getHours() % 12) || 12;
                        break;
                    case "m":
                        rawData = date.getMinutes();
                        break;
                    case "s":
                        rawData = date.getSeconds();
                        break;
                    case "S":
                        rawData = date.getMilliseconds();
                        break;
                    case "Z":
                        rawData = date.getTimezoneOffset(); // This is returns the number of minutes since GMT was this time.
                        break;
                }
                // Format the raw data depending on the type
                switch (this.cfg.types[patternLetter]) {
                    case this.cfg.TEXT2:
                        formattedString += formatText(rawData as string, numberOfLetters, 2);
                        break;
                    case this.cfg.TEXT3:
                        formattedString += formatText(rawData as string, numberOfLetters, 3);
                        break;
                    case this.cfg.NUMBER:
                        formattedString += formatNumber(rawData, numberOfLetters);
                        break;
                    case this.cfg.YEAR:
                        if (numberOfLetters <= 3) {
                            // Output a 2-digit year
                            const dataString = "" + rawData;
                            formattedString += dataString.substr(2, 2);
                        } else {
                            formattedString += formatNumber(rawData, numberOfLetters);
                        }
                        break;
                    case this.cfg.MONTH:
                        if (numberOfLetters >= 3) {
                            formattedString += formatText(this.cfg.monthNames[rawData as number]!, numberOfLetters, numberOfLetters);
                        } else {
                            // NB. Months returned by getMonth are zero-based
                            formattedString += formatNumber((rawData as number) + 1, numberOfLetters);
                        }
                        break;
                    case this.cfg.TIMEZONE: {
                        const isPositive = ((rawData as number) > 0);
                        // The following line looks like a mistake but isn't
                        // because of the way getTimezoneOffset measures.
                        const prefix = isPositive ? "-" : "+";
                        const absData = Math.abs(rawData as number);

                        // Hours
                        let hours = "" + Math.floor(absData / 60);
                        hours = padWithZeroes(hours, 2);
                        // Minutes
                        let minutes = "" + (absData % 60);
                        minutes = padWithZeroes(minutes, 2);

                        formattedString += prefix + hours + minutes;
                        break;
                    }
                }
            }

            searchString = searchString.substr(result.index + result[0].length);
        }
        return formattedString;
    }
}

/**
 * The configuration for the {@link Clock} widget.
 */
export interface ClockCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /**
     * When `mode` is set to `server`: `true` to automatically sync the time with the server according to the specified
     * `syncInterval`, or `false` otherwise.
     */
    autoSync: boolean;
    /** The date format helper for the `pattern`, created by the widget. */
    dateFormat: SimpleDateFormat;
    /** Whether the clock is displayed as an analog or digital clock. */
    displayMode: PrimeFacesTypes.widget.Clock.DisplayMode;
    /** Locale for the clock, determines the time format. */
    locale: string;
    /** Whether the clock uses the time of the browser or the time from the server. */
    mode: PrimeFacesTypes.widget.Clock.TimeMode;
    /** Datetime format. */
    pattern: string;
    /** Defines the sync in ms interval in when `autoSync` is set to `true`. */
    syncInterval: number;
    /** The initial time value for the clock to display. */
    value: string;
}

/**
 * __PrimeFaces Clock Widget__
 *
 * Clock displays server or client datetime live.
 */
export class Clock<Cfg extends ClockCfg = ClockCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The canvas for the analog clock. */
    canvas!: RaphaelPaper;
    /** The drawn element for the clock outline. */
    clock!: RaphaelElement;
    /** The currently displayed time. */
    current!: Date;
    /** Calculated sizes for the analog clock elements. */
    dimensions!: PrimeFacesTypes.widget.Clock.Dimensions;
    /** The drawn elements for the hour signs (1-12). */
    hour_sign: RaphaelElement[] = [];
    /** The drawn element for the hour hand. */
    hour_hand!: RaphaelElement;
    /** The set-interval timer ID for the ticking of the clock. */
    interval?: number;
    /** The set-interval timer ID for syncing the clock with the server when `autoSync` is set to `true`. */
    syncInterval?: number;
    /** The drawn element for the minute hand. */
    minute_hand!: RaphaelElement;
    /** The drawn element for the pin at the center of the clock. */
    pin!: RaphaelElement;
    /** The drawn element for the second hand. */
    second_hand!: RaphaelElement;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.cfg.pattern = this.cfg.pattern||"MM/dd/yyyy HH:mm:ss";
        this.cfg.dateFormat = new SimpleDateFormat({
            pattern: this.cfg.pattern,
            locale: this.cfg.locale
        });
        this.current = this.isClient() ? new Date() : new Date(this.cfg.value!);

        if(this.isAnalogClock()) {
            this.interval = window.setInterval(() => {
                this.update();
            }, 1000);

            this.draw();
        }
        else {
            this.start();
        }

        if(!this.isClient() && this.cfg.autoSync) {
            this.syncInterval = window.setInterval(() => {
                this.sync();
            }, this.cfg.syncInterval);
        }
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        clearInterval(this.interval);
        clearInterval(this.syncInterval);

        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();

        clearInterval(this.interval);
        clearInterval(this.syncInterval);
    }

    /**
     * Checks whether the time of the client is used for this clock.
     * @returns `true` if the time of the client is used, or `false` if the time of the server is used.
     */
    isClient(): boolean {
        return this.cfg.mode === 'client';
    }

    /**
     * Starts this clock if it is not already running.
     */
    start(): void {
        this.interval = window.setInterval(() => {
            this.updateOutput();
        }, 1000);
    }

    /**
     * Stops this clock it is currently running.
     */
    stop(): void {
        clearInterval(this.interval);
    }

    /**
     * Called after a tick of the clock, updates the visual display of this clock.
     */
    private updateOutput(): void {
        this.current.setSeconds(this.current.getSeconds() + 1);
        this.jq.text(this.cfg.dateFormat!.format(this.current));
    }

    /**
     * Synchronizes this clock so that it shows the current time. This will trigger an AJAX update of this component.
     */
    sync(): void {
        if(!this.isAnalogClock()) {
            this.stop();
        }

        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            async: true,
            global: false,
            params: [{
                name: this.id + '_sync', value: true
            }],
            oncomplete: (xhr, status, args) => {
                if(this.isAnalogClock()) {
                    this.current = new Date(args!['datetime'] as number);
                }
                else {
                    this.stop();
                    this.current = new Date(args!['datetime'] as number);
                    this.jq.text(this.cfg.dateFormat!.format(this.current));
                    this.start();
                }
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Draws this clock according the the current widget configuration.
     */
    private draw(): void {

        this.dimensions = this.getDimensions(this.jq.width()!);

        this.canvas = Raphael(this.getId(), this.dimensions.size,this.dimensions.size);

        this.clock = this.canvas.circle(this.dimensions.half,this.dimensions.half, this.dimensions.clock_width);

        this.draw_hour_signs();

        this.draw_hands();

        this.pin = this.canvas.circle(this.dimensions.half,this.dimensions.half, this.dimensions.pin_width);

        this.clock.attr({
            "fill": "#ffffff",
            "stroke": "#4A4A4A",
            "stroke-width": 3
        });

        for (let i=0; i<this.hour_sign.length; i++) {
            this.hour_sign[i]!.attr({
                "stroke": "#000000",
                "stroke-width": this.dimensions.hour_sign_stroke_width
            });
        }

        this.hour_hand.attr({
            "stroke": "#4A4A4A",
            "stroke-width": this.dimensions.hour_hand_stroke_width
        });

        this.minute_hand.attr({
            "stroke": '#4A4A4A',
            "stroke-width": this.dimensions.minute_hand_stroke_width
        });

        this.second_hand.attr({
            "stroke": "#4A4A4A",
            "stroke-width": this.dimensions.second_hand_stroke_width
        });

        this.pin.attr({
            "fill": "#F58503"
        });

        this.update();
    }

    /**
     * Draws the hour marks for the analog clock.
     */
    private draw_hour_signs(): void {
        this.hour_sign = [];

        for (let i = 0; i < 12; i++) {
            ((i: number, that: this) => {
                const start_x = that.dimensions.half + Math.round(that.dimensions.hour_sign_min_size * Math.cos(30 * i * Math.PI / 180));
                const start_y = that.dimensions.half + Math.round(that.dimensions.hour_sign_min_size * Math.sin(30 * i * Math.PI / 180));
                const end_x = that.dimensions.half + Math.round(that.dimensions.hour_sign_max_size * Math.cos(30 * i * Math.PI / 180));
                const end_y = that.dimensions.half + Math.round(that.dimensions.hour_sign_max_size * Math.sin(30 * i * Math.PI / 180));

                that.hour_sign.push(that.canvas.path("M" + start_x + " " + start_y + "L" + end_x + " " + end_y));
            })(i,this);
        }
    }

    /**
     * Draws the clock hands for the analog clock.
     */
    private draw_hands(): void {
        this.hour_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.hour_hand_start_position);
        this.minute_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.minute_hand_start_position);
        this.second_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.second_hand_start_position);
    }

    /**
     * Called each click of the clock, animates the clock hands.
     */
    private update(): void {
        this.hour_hand.animate({transform: "R" + (30 * this.current.getHours() + (this.current.getMinutes() / 2.5)) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);
        this.minute_hand.animate({transform: "R" + (6 * this.current.getMinutes()) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);
        this.second_hand.animate({transform: "R" + (6 * this.current.getSeconds()) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);

        this.current.setSeconds(this.current.getSeconds() + 1);
    }

    /**
     * Computes the width of the individual elements of the analog clock for the given target width.
     * @param size Target width of the clock in pixels
     * @returns Calculated sizes for the analog clock elements.
     */
    private getDimensions(size: number): PrimeFacesTypes.widget.Clock.Dimensions {
        return {
            'size': size,
            'half': Math.floor(size / 2),
            'clock_width': Math.floor(size * 47.5 / 100),
            'hour_sign_min_size': Math.floor(size * 40 / 100),
            'hour_sign_max_size': Math.floor(size * 45 / 100),
            'hour_sign_stroke_width': Math.floor(size * 0.5 / 100) || 1,
            'hour_hand_start_position': Math.floor(size / 4),
            'hour_hand_stroke_width': Math.floor(size * 3 / 100) || 1,
            'minute_hand_start_position': Math.floor(size / 6),
            'minute_hand_stroke_width': Math.floor(size * 2 / 100) || 1,
            'second_hand_start_position': Math.floor(size * 12.5 / 100),
            'second_hand_stroke_width': Math.floor(size * 1 / 100) || 1,
            'pin_width': Math.floor(size * 2.5 / 100)
        };
    }

    /**
     * Checks whether this clock is displayed as an analog or digital clock.
     * @returns `true` if this clock is displayed as an analog clock, or `false` if it is displayed in an
     * INPUT field.
     */
    isAnalogClock(): boolean {
        return this.cfg.displayMode === "analog";
    }
}
