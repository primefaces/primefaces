/**
 * PrimeFaces SimpleDateFormat widget, code ported from Tim Down's http://www.timdown.co.uk/code/simpledateformat.php
 */
PrimeFaces.widget.SimpleDateFormat = Class.extend({

    init: function(cfg) {
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

        if(this.cfg.locale && PrimeFaces.locales[this.cfg.locale]) {
            this.cfg.monthNames = PrimeFaces.locales[this.cfg.locale].monthNames;
            this.cfg.dayNames = PrimeFaces.locales[this.cfg.locale].dayNames;
        }
        else {
            this.cfg.monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
            this.cfg.dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
        }
    },

    newDateAtMidnight: function(year, month, day) {
        var d = new Date(year, month, day, 0, 0, 0);
        d.setMilliseconds(0);
        return d;
    },

    getDifference : function(date1, date2) {
        return date1.getTime() - date2.getTime();
    },

    isBefore : function(date1, date2) {
        return date1.getTime() < date2.getTime();
    },

    getUTCTime: function(date) {
        if(date != undefined){
            return Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds(), date.getMilliseconds());
        }

    },

    getTimeSince: function(date1, date2) {
        return this.getUTCTime(date1) - this.getUTCTime(date2);
    },

    getPreviousSunday: function(date) {
        // Using midday avoids any possibility of DST messing things up
        var midday = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12, 0, 0);
        var previousSunday = new Date(midday.getTime() - date.getDay() * this.cfg.ONE_DAY);

        return this.newDateAtMidnight(previousSunday.getFullYear(), previousSunday.getMonth(), previousSunday.getDate());
    },

    getWeekInYear : function(date, minimalDaysInFirstWeek) {
        var previousSunday = this.getPreviousSunday(date);
        var startOfYear = this.newDateAtMidnight(date.getFullYear(), 0, 1);
        var numberOfSundays = this.isBefore(previousSunday, startOfYear) ? 0 : 1 + Math.floor(this.getTimeSince(previousSunday,startOfYear) / this.cfg.ONE_WEEK);
        var numberOfDaysInFirstWeek =  7 - startOfYear.getDay();
        var weekInYear = numberOfSundays;
        if (numberOfDaysInFirstWeek < minimalDaysInFirstWeek) {
            weekInYear--;
        }

        return weekInYear;
    },

   getWeekInMonth: function(date, minimalDaysInFirstWeek) {
        var previousSunday = this.getPreviousSunday(date);
        var startOfMonth = this.newDateAtMidnight(date.getFullYear(), date.getMonth(), 1);
        var numberOfSundays = this.isBefore(previousSunday,startOfMonth) ? 0 : 1 + Math.floor((this.getTimeSince(previousSunday, startOfMonth)) / this.cfg.ONE_WEEK);
        var numberOfDaysInFirstWeek =  7 - startOfMonth.getDay();
        var weekInMonth = numberOfSundays;
        if (numberOfDaysInFirstWeek >= minimalDaysInFirstWeek) {
            weekInMonth++;
        }

        return weekInMonth;
    },

    getDayInYear: function(date) {
        var startOfYear = this.newDateAtMidnight(date.getFullYear(), 0, 1);

        return 1 + Math.floor(this.getTimeSince(date, startOfYear) / this.cfg.ONE_DAY);
    },

    getMinimalDaysInFirstWeek: function(days) {
        return this.cfg.minimalDaysInFirstWeek	? this.cfg.DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK : this.cfg.minimalDaysInFirstWeek;
    },

    format: function(date) {
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
});

/**
 *  PrimeFaces Clock Widget
 */

PrimeFaces.widget.Clock = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.cfg.pattern = this.cfg.pattern||"MM/dd/yyyy HH:mm:ss";
        this.cfg.dateFormat = new PrimeFaces.widget.SimpleDateFormat({
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
    },

    //@override
    refresh: function(cfg) {
        clearInterval(this.interval);

        this._super(cfg);
    },

    isClient: function() {
        return this.cfg.mode === 'client';
    },

    start: function() {
        var $this = this;
        this.interval = setInterval(function(){
            $this.updateOutput();
        }, 1000);
    },

    stop: function() {
        clearInterval(this.interval);
    },

    updateOutput: function() {
        this.current.setSeconds(this.current.getSeconds() + 1);
        this.jq.text(this.cfg.dateFormat.format(this.current));
    },

    sync: function() {
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
    },

    draw: function() {

        this.dimensions = this.getDimensions(this.jq.width());

        this.canvas = new Raphael(this.id, this.dimensions.size,this.dimensions.size);

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
    },

    draw_hour_signs: function() {
        this.hour_sign = [];

        for (i = 0; i < 12; i++) {
            (function (i,that){
                var start_x = that.dimensions.half + Math.round(that.dimensions.hour_sign_min_size * Math.cos(30 * i * Math.PI / 180));
                var start_y = that.dimensions.half + Math.round(that.dimensions.hour_sign_min_size * Math.sin(30 * i * Math.PI / 180));
                var end_x = that.dimensions.half + Math.round(that.dimensions.hour_sign_max_size * Math.cos(30 * i * Math.PI / 180));
                var end_y = that.dimensions.half + Math.round(that.dimensions.hour_sign_max_size * Math.sin(30 * i * Math.PI / 180));

                that.hour_sign.push(that.canvas.path("M" + start_x + " " + start_y + "L" + end_x + " " + end_y));
            })(i,this);
        }
    },

    draw_hands: function() {
        this.hour_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.hour_hand_start_position);
        this.minute_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.minute_hand_start_position);
        this.second_hand = this.canvas.path("M" + this.dimensions.half + " " + this.dimensions.half + "L" + this.dimensions.half + " " + this.dimensions.second_hand_start_position);
    },

    update: function() {
        this.hour_hand.animate({transform: "R" + (30 * this.current.getHours() + (this.current.getMinutes() / 2.5)) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);
        this.minute_hand.animate({transform: "R" + (6 * this.current.getMinutes()) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);
        this.second_hand.animate({transform: "R" + (6 * this.current.getSeconds()) + "," + this.dimensions.half + "," + this.dimensions.half}, 1);

        this.current.setSeconds(this.current.getSeconds() + 1);
    },

    getDimensions: function(size) {
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
    },

    isAnalogClock: function() {
        return this.cfg.displayMode === "analog";
    }
});