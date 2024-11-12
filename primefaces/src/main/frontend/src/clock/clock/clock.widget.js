import Raphael from "raphael";

/**
 * Code ported from Tim Down's http://www.timdown.co.uk/code/simpledateformat.php
 * 
 * Helper class for working with `Date`s and date formats.
 * 
 * @template {PrimeFaces.widget.SimpleDateFormatCfg} [TCfg=PrimeFaces.widget.SimpleDateFormatCfg] Type of the configuration
 * object for this widget.
 * @interface {PrimeFaces.widget.SimpleDateFormatCfg} cfg The configuration for the
 * {@link  SimpleDateFormat| SimpleDateFormat widget}. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 * 
 * @prop {PrimeFaces.PartialWidgetCfg<PrimeFaces.widget.SimpleDateFormatCfg>} cfg The configuration of this widget
 * instance. Please note that no property is guaranteed to be present, you should always check for `undefined` before
 * accessing a property. This is partly because the value of a property is not transmitted from the server to the client
 * when it equals the default.
 * 
 * @prop {string[]} cfg.dayNames Localized day names (`Monday`, `Tuesday` etc.)
 * @prop {string | string[]} cfg.id The client-side ID of this widget, with all parent naming containers, such as
 * `myForm:myWidget`. This is also the ID of the container HTML element for this widget. In case the widget needs
 * multiple container elements (such as {@link Paginator}), this may also be an array if IDs.
 * @prop {string} cfg.locale The locale for formatting dates. 
 * @prop {number} cfg.minimalDaysInFirstWeek Minimal number of days a week is allowed to have to be considered a "full"
 * week, used by `getWeekInMonth` in `getWeekInYear`.
 * @prop {string[]} cfg.monthNames Localized month names (`January`, `February` etc.)
 * @prop {RegExp} cfg.regex A regex for splitting a date format into its components.
 * @prop {Record<string, number>} cfg.types Object with the different keywords used by the date format.
 * @prop {string} cfg.widgetVar The name of the widget variables of this widget. The widget variable can be used to
 * access a widget instance by calling `PF('myWidgetVar')`.
 */
class SimpleDateFormat {

    /**
     * A widget class should not have an explicit constructor. Instead, this initialize method is called after the widget
     * was created. You can use this method to perform any initialization that is required. For widgets that need to create
     * custom HTML on the client-side this is also the place where you should call your render method.
     * 
     * @param {Partial<TCfg>} cfg The widget configuration to be used for this widget instance. This widget
     * configuration is usually created on the server by the `javax.faces.render.Renderer` for this component.
     */
    constructor(cfg) {
        this.cfg = cfg;
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

        var localeSettings = PrimeFaces.getLocaleSettings(this.cfg.locale);
        if(localeSettings) {
            this.cfg.monthNames = localeSettings.monthNames;
            this.cfg.dayNames = localeSettings.dayNames;
        }
        else {
            this.cfg.monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
            this.cfg.dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
        }
    }

    /**
     * Creates a new date object that represents midnighht of the given year, month, and day.
     * @param {number} year A year to set. `0` repesents the year `1900`, `100` the year `2000`.
     * @param {number} month A month (of the year) to set. `0` is January, `11` is `December`.
     * @param {number} day  A day (of the month) to set, in the range `1...31`.
     * @return {Date} A date for the given year, month, and day at at midnight.
     */
    newDateAtMidnight(year, month, day) {
        var d = new Date(year, month, day, 0, 0, 0);
        d.setMilliseconds(0);
        return d;
    }

    /**
     * Computes the difference between the two given dates.
     * @param {Date} date1 First input date
     * @param  {Date} date2 Second input date
     * @return {number} Time in milliseconds between the two dates (`date1-date2`).
     */
    getDifference(date1, date2) {
        return date1.getTime() - date2.getTime();
    }

    /**
     * Checks whether the first given date lies before the second given date.
     * @param {Date} date1 First input date
     * @param {Date} date2 Second input date
     * @return {boolean} `true` if `date1` lies before `date2`, or `false` otherwise.
     */
    isBefore(date1, date2) {
        return date1.getTime() < date2.getTime();
    }

    /**
     * Converts the given date to UTC time, that is, the number of milliseconds between midnight, January 1, 1970
     * Universal Coordinated Time (UTC) (or GMT) and the given date.
     * @param {Date} date Date to convert to UTC.
     * @return {number} The given date, converted to UTC time.
     */
    getUTCTime(date) {
        if(date != undefined){
            return Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds(), date.getMilliseconds());
        }

    }

    /**
     * Finds the difference in milliseconds between the two given date (`date1-date2`).
     * @param {Date} date1 First input date
     * @param {Date} date2 Second input date
     * @return {number} The numer of milliseconds between the two given date (`date1-date2`).
     */
    getTimeSince(date1, date2) {
        return this.getUTCTime(date1) - this.getUTCTime(date2);
    }

    /**
     * Finds closest Sunday preceding the given date. If the date is already a Sunday, that day is returned.
     * @param {Date} date Input date.
     * @return {Date} The date at midnight of the first Sunday before the given date. If the given date is already a
     * Sunday, that day is returned.
     */
    getPreviousSunday(date) {
        // Using midday avoids any possibility of DST messing things up
        var midday = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12, 0, 0);
        var previousSunday = new Date(midday.getTime() - date.getDay() * this.cfg.ONE_DAY);

        return this.newDateAtMidnight(previousSunday.getFullYear(), previousSunday.getMonth(), previousSunday.getDate());
    }

    /**
     * Computes the ordinal index of the week of the year of the given date.
     * @param {Date} date Date to check.
     * @param {number} minimalDaysInFirstWeek Minimal number of days the first week of the year is allowed to have. If
     * the first week contains less days, the returned output is decremented by one (if you do not want to count, say,
     * 2 days, as week).
     * @return {number} The week of the year of the given date, starting at `0`.
     */
    getWeekInYear(date, minimalDaysInFirstWeek) {
        var previousSunday = this.getPreviousSunday(date);
        var startOfYear = this.newDateAtMidnight(date.getFullYear(), 0, 1);
        var numberOfSundays = this.isBefore(previousSunday, startOfYear) ? 0 : 1 + Math.floor(this.getTimeSince(previousSunday,startOfYear) / this.cfg.ONE_WEEK);
        var numberOfDaysInFirstWeek =  7 - startOfYear.getDay();
        var weekInYear = numberOfSundays;
        if (numberOfDaysInFirstWeek < minimalDaysInFirstWeek) {
            weekInYear--;
        }

        return weekInYear;
    }

    /**
     * Computes the ordinal index of the week of the month of the given date.
     * @param {Date} date Date with a month to check.
     * @param {number} minimalDaysInFirstWeek Minimal number of days the first week of the month is allowed to have. If
     * the first week contains less days, the returned output is decremented by one (if you do not want to count, say,
     * 2 days, as week).
     * @return {number} The week of the month of the given date, starting at `0`.
     */
    getWeekInMonth(date, minimalDaysInFirstWeek) {
        var previousSunday = this.getPreviousSunday(date);
        var startOfMonth = this.newDateAtMidnight(date.getFullYear(), date.getMonth(), 1);
        var numberOfSundays = this.isBefore(previousSunday,startOfMonth) ? 0 : 1 + Math.floor((this.getTimeSince(previousSunday, startOfMonth)) / this.cfg.ONE_WEEK);
        var numberOfDaysInFirstWeek =  7 - startOfMonth.getDay();
        var weekInMonth = numberOfSundays;
        if (numberOfDaysInFirstWeek >= minimalDaysInFirstWeek) {
            weekInMonth++;
        }

        return weekInMonth;
    }

    /**
     * Computes the ordinal index of the given day in the given year.
     * @param {Date} date A day to check.
     * @return {number} The ordinal index of the given day relative to the beginning of the year, starting at `1`.
     */
    getDayInYear(date) {
        var startOfYear = this.newDateAtMidnight(date.getFullYear(), 0, 1);

        return 1 + Math.floor(this.getTimeSince(date, startOfYear) / this.cfg.ONE_DAY);
    }

    /**
     * Finds the currently configured value of how many days a week must have at least to be considered a "full" week.
     * Weeks with less that that number of days are disregarded in `getWeekInMonth` and `getWeekInYear`.
     * @param {unknown} days Unused.
     * @return {number} The minimal number of days a week is allowed to have to be considered a "full" week. 
     */
    getMinimalDaysInFirstWeek(days) {
        return this.cfg.minimalDaysInFirstWeek	? this.cfg.DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK : this.cfg.minimalDaysInFirstWeek;
    }

    /**
     * Format sthe given given according to the pattern of the current widget configuration.
     * @param {Date} date A date to format
     * @return {string} The given date as a formatted string.
     */
    format(date) {
        var formattedString = "";
        var result;

        var padWithZeroes = function(str, len) {
            while (str.length < len) {
                str = "0" + str;
            }
            return str;
        };

        var formatText = function(data, numberOfLetters, minLength) {
            return (numberOfLetters >= 4) ? data : data.substr(0, Math.max(minLength, numberOfLetters));
        };

        var formatNumber = function(data, numberOfLetters) {
            var dataString = "" + data;
            // Pad with 0s as necessary
            return padWithZeroes(dataString, numberOfLetters);
        };

        var searchString = this.cfg.pattern;
        while ((result = this.cfg.regex.exec(searchString))) {
            var matchedString = result[0];
            var quotedString = result[1];
            var patternLetters = result[2];
            var otherLetters = result[3];
            var otherCharacters = result[4];

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
                var patternLetter = patternLetters.charAt(0);
                var numberOfLetters = patternLetters.length;
                var rawData = "";
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
                        rawData = this.cfg.dayNames[date.getDay()];
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
                        formattedString += formatText(rawData, numberOfLetters, 2);
                        break;
                    case this.cfg.TEXT3:
                        formattedString += formatText(rawData, numberOfLetters, 3);
                        break;
                    case this.cfg.NUMBER:
                        formattedString += formatNumber(rawData, numberOfLetters);
                        break;
                    case this.cfg.YEAR:
                        if (numberOfLetters <= 3) {
                            // Output a 2-digit year
                            var dataString = "" + rawData;
                            formattedString += dataString.substr(2, 2);
                        } else {
                            formattedString += formatNumber(rawData, numberOfLetters);
                        }
                        break;
                    case this.cfg.MONTH:
                        if (numberOfLetters >= 3) {
                            formattedString += formatText(this.cfg.monthNames[rawData], numberOfLetters, numberOfLetters);
                        } else {
                            // NB. Months returned by getMonth are zero-based
                            formattedString += formatNumber(rawData + 1, numberOfLetters);
                        }
                        break;
                    case this.cfg.TIMEZONE:
                        var isPositive = (rawData > 0);
                        // The following line looks like a mistake but isn't
                        // because of the way getTimezoneOffset measures.
                        var prefix = isPositive ? "-" : "+";
                        var absData = Math.abs(rawData);

                        // Hours
                        var hours = "" + Math.floor(absData / 60);
                        hours = padWithZeroes(hours, 2);
                        // Minutes
                        var minutes = "" + (absData % 60);
                        minutes = padWithZeroes(minutes, 2);

                        formattedString += prefix + hours + minutes;
                        break;
                }
            }

            searchString = searchString.substr(result.index + result[0].length);
        }
        return formattedString;
    }
}

/**
 * __PrimeFaces Clock Widget__
 * 
 * Clock displays server or client datetime live.
 * 
 * @typedef {"client" | "server"} PrimeFaces.widget.Clock.TimeMode Indicates which time the clock widget uses. `client`
 * uses the time from the client (browser), `server` uses the time from the server.
 * 
 * @typedef {"analog" | "digital"} PrimeFaces.widget.Clock.DisplayMode Display mode for the clock widget. `analog`
 * displays an analog clock, `digital` a digitial clock.
 * 
 * @interface {PrimeFaces.widget.Clock.Dimensions} Dimensions Computed dimensions for the individual parts of the analog
 * clock, all in pixels.
 * @prop {number} Dimensions.size Width of the clock element in pixels.
 * @prop {number} Dimensions.half Half width of the clock element in pixels.
 * @prop {number} Dimensions.clock_width Width of the clock face in pixels.
 * @prop {number} Dimensions.hour_sign_min_size Distance in pixels from the center of the circle where the hour mark
 * starts.
 * @prop {number} Dimensions.hour_sign_max_size Distance in pixels from the center of the circle where the hour mark
 * ends.
 * @prop {number} Dimensions.hour_hand_start_position Radial distance in pixels from the circumference of the circle
 * where the hour hand starts. 
 * @prop {number} Dimensions.hour_hand_stroke_width Stroke width in pixels of the hour hand.
 * @prop {number} Dimensions.minute_hand_start_position Radial distance in pixels from the circumference of the circle
 * where the minute hand starts.
 * @prop {number} Dimensions.minute_hand_stroke_width Stroke width in pixels of the minute hand.
 * @prop {number} Dimensions.second_hand_start_position Radial distance in pixels from the circumference of the circle
 * where the seconds hand starts. 
 * @prop {number} Dimensions.second_hand_stroke_width Stroke width in pixels of the seconds hand.
 * @prop {number} Dimensions.pin_width Radius in pixels of the pin at the center of the clock face.
 * 
 * @prop {import("raphael").RaphaelPaper} canvas The canvas for the analog clock.
 * @prop {import("raphael").RaphaelElement} clock The drawn element for the clock outline.
 * @prop {Date} current The currently displayed time.
 * @prop {PrimeFaces.widget.Clock.Dimensions} dimensions Calculated sizes for the analog clock elements.
 * @prop {import("raphael").RaphaelElement[]} hour_sign The drawn elements for the hour signs (1-12).
 * @prop {import("raphael").RaphaelElement} hour_hand The drawn element for the hour hand.
 * @prop {number} interval The set-interval timer ID for the ticking of the clock.
 * @prop {import("raphael").RaphaelElement} minute_hand The drawn element for the minute hand.
 * @prop {import("raphael").RaphaelElement} pin The drawn element for the pin at the center of the clock.
 * @prop {import("raphael").RaphaelElement} second_hand The drawn element for the second hand.
 * 
 * @interface {PrimeFaces.widget.ClockCfg} cfg The configuration for the {@link  Clock| Clock widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.autoSync When `mode` is set to `server`: `true` to automatically sync the time with the server
 * according to the specified `syncInterval`, or `false` otherwise.
 * @prop {PrimeFaces.widget.Clock.DisplayMode} cfg.displayMode Whether the clock is displayed as an analog or digital
 * clock.
 * @prop {string} cfg.locale Locale for the clock, determines the time format.
 * @prop {PrimeFaces.widget.Clock.TimeMode} cfg.mode Whether the clock uses the time of the browser or the time from the
 * server.
 * @prop {string} cfg.pattern Datetime format.
 * @prop {number} cfg.syncInterval Defines the sync in ms interval in when `autoSync` is set to `true`.
 * @prop {string} cfg.value The initial time value for the clock to display.
 */
PrimeFaces.widget.Clock = class Clock extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.cfg.pattern = this.cfg.pattern||"MM/dd/yyyy HH:mm:ss";
        this.cfg.dateFormat = new SimpleDateFormat({
            pattern: this.cfg.pattern,
            locale: this.cfg.locale
        });
        this.current = this.isClient() ? new Date() : new Date(this.cfg.value);

        var $this = this;
        if(this.isAnalogClock()) {
            this.interval = setInterval(function() {
                $this.update();
            }, 1000);

            this.draw();
        }
        else {
            this.start();
        }

        if(!this.isClient() && this.cfg.autoSync) {
            setInterval(function() {
                $this.sync();
            }, this.cfg.syncInterval);
        }
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        clearInterval(this.interval);

        super.refresh(cfg);
    }

    /**
     * Checks whether the time of the client is used for this clock.
     * @return {boolean} `true` if the time of the client is used, or `false` if the time of the server is used.
     */
    isClient() {
        return this.cfg.mode === 'client';
    }

    /**
     * Starts this clock if it is not already running.
     */
    start() {
        var $this = this;
        this.interval = setInterval(function(){
            $this.updateOutput();
        }, 1000);
    }

    /**
     * Stops this clock it is currently running.
     */
    stop() {
        clearInterval(this.interval);
    }

    /**
     * Called after a tick of the clock, updates the visual display of this clock.
     * @private
     */
    updateOutput() {
        this.current.setSeconds(this.current.getSeconds() + 1);
        this.jq.text(this.cfg.dateFormat.format(this.current));
    }

    /**
     * Synchronizes this clock so that it shows the current time. This will trigger an AJAX update of this component.
     */
    sync() {
        if(!this.isAnalogClock()) {
            this.stop();
        }

        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            async: true,
            global: false,
            params: [{
                name: this.id + '_sync', value: true
            }],
            oncomplete: function(xhr, status, args, data) {
                if($this.isAnalogClock()) {
                    $this.current = new Date(args.datetime);
                }
                else {
                    $this.stop();
                    $this.current = new Date(args.datetime);
                    $this.jq.text($this.cfg.dateFormat.format($this.current));
                    $this.start();
                }
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Draws this clock according the the current widget configuation.
     * @private
     */
    draw() {

        this.dimensions = this.getDimensions(this.jq.width());

        this.canvas = Raphael(this.id, this.dimensions.size,this.dimensions.size);

        this.clock = this.canvas.circle(this.dimensions.half,this.dimensions.half, this.dimensions.clock_width);

        this.draw_hour_signs();

        this.draw_hands();

        this.pin = this.canvas.circle(this.dimensions.half,this.dimensions.half, this.dimensions.pin_width);

        this.clock.attr({
            "fill": "#ffffff",
            "stroke": "#4A4A4A",
            "stroke-width": "3"
        });

        for (var i=0; i<this.hour_sign.length; i++) {
            this.hour_sign[i].attr({
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
     * @private
     */
    draw_hour_signs() {
        this.hour_sign = [];

        for (var i = 0; i < 12; i++) {
            (function (i,that){
                var start_x = that.dimensions.half + Math.round(that.dimensions.hour_sign_min_size * Math.cos(30 * i * Math.PI / 180));
                var start_y = that.dimensions.half + Math.round(that.dimensions.hour_sign_min_size * Math.sin(30 * i * Math.PI / 180));
                var end_x = that.dimensions.half + Math.round(that.dimensions.hour_sign_max_size * Math.cos(30 * i * Math.PI / 180));
                var end_y = that.dimensions.half + Math.round(that.dimensions.hour_sign_max_size * Math.sin(30 * i * Math.PI / 180));

                that.hour_sign.push(that.canvas.path("M" + start_x + " " + start_y + "L" + end_x + " " + end_y));
            })(i,this);
        }
    }

    /**
     * Draws the clock hands for the analog clock.
     * @private
     */
    draw_hands() {
        this.hour_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.hour_hand_start_position);
        this.minute_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.minute_hand_start_position);
        this.second_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.second_hand_start_position);
    }

    /**
     * Called each click of the clock, animates the clock hands.
     * @private
     */
    update() {
        this.hour_hand.animate({transform: "R" + (30 * this.current.getHours() + (this.current.getMinutes() / 2.5)) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);
        this.minute_hand.animate({transform: "R" + (6 * this.current.getMinutes()) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);
        this.second_hand.animate({transform: "R" + (6 * this.current.getSeconds()) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);

        this.current.setSeconds(this.current.getSeconds() + 1);
    }

    /**
     * Computes the width of the individual elements of the analog clock for the given target width.
     * @private
     * @param {number} size Target width of the clock in pixels
     * @return {PrimeFaces.widget.Clock.Dimensions} Calculated sizes for the analog clock elements.
     */
    getDimensions(size) {
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
     * @return {boolean} `true` if this clock is displayed as an analog clock, or `false` if it is displayed in an
     * INPUT field.
     */
    isAnalogClock() {
        return this.cfg.displayMode === "analog";
    }
}

// TODO SimpleDateFormat is NOT a widget, just a class, and should not be added to the PrimeFaces.widget scope
// Add it for now to preserve backwards compatibility in case external code uses it.
PrimeFaces.widget.SimpleDateFormat = SimpleDateFormat;
