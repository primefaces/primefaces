/**
 * Prime DatePicker Widget
 */
(function (factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery'], factory);
    } else if (typeof module === 'object' && module.exports) {
        // Node/CommonJS
        module.exports = function (root, jQuery) {
            factory(jQuery);
            return jQuery;
        };
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {
    $.widget("prime.datePicker", {

        options: {
            id: null,
            name: null,
            defaultDate: null,
            viewDate: null,
            style: null,
            styleClass: null,
            inline: false,
            selectionMode: 'single',
            rangeSeparator: '-',
            timeSeparator: ':',
            inputId: null,
            inputStyle: null,
            inputStyleClass: null,
            required: false,
            readOnlyInput: false,
            disabled: false,
            valid: true,
            tabIndex: null,
            placeholder: null,
            showIcon: false,
            icon: 'ui-icon ui-icon-calendar',
            showOnFocus: true,
            keepInvalid: false,
            numberOfMonths: 1,
            view: 'date',
            touchUI: false,
            showWeek: false,
            weekCalculator: null,
            showTime: false,
            timeOnly: false,
            showSeconds: false,
            hourFormat: '24',
            stepHour: 1,
            stepMinute: 1,
            stepSecond: 1,
            shortYearCutoff: '+10',
            hideOnDateTimeSelect: false,
            userLocale: null,
            locale: {
                firstDayOfWeek: 0,
                dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
                dayNamesShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
                dayNamesMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
                monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
                monthNamesShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
                today: 'Today',
                clear: 'Clear'
            },
            dateFormat: 'mm/dd/yy',
            yearRange: null,
            panelStyle: null,
            panelStyleClass: null,
            monthNavigator: false,
            yearNavigator: false,
            dateStyleClasses: null,
            disabledDates: null,
            disabledDays: null,
            minDate: null,
            maxDate: null,
            maxDateCount: null,
            showOtherMonths: false,
            selectOtherMonths: false,
            showButtonBar: false,
            todayButtonStyleClass: 'ui-priority-secondary',
            clearButtonStyleClass: 'ui-priority-secondary',
            appendTo: null,
            dateTemplate: null,
            timeInput: false,
            onFocus: null,
            onBlur: null,
            onInput: null,
            onSelect: null,
            onChange: null,
            onViewDateChange: null,
            onTodayButtonClick: null,
            onClearButtonClick: null,
            onBeforeShow: null,
            onBeforeHide: null,
            onMonthChange: null,
            onYearChange: null
        },

        _create: function () {
            this.container = this.element;
            this.inputfield = this.element.children('input');

            this._setInitValues();
            this._render();
        },

        _setInitValues: function () {
            if (this.options.userLocale && typeof this.options.userLocale === 'object') {
                $.extend(this.options.locale, this.options.userLocale);
            }

            if (this.options.showWeek && !this.options.weekCalculator) {
                this.options.weekCalculator = this.calculateWeekNumber.bind(this);

                // initialize the potentially missing firstDayWeekOffset option
                // based on the firstDay(OfWeek) option:
                //  - firstDay = saturday => firstDayWeekOffset = 12
                //  - firstDay = sunday => firstDayWeekOffset = 6
                //  - firstDay = monday (ISO8601) => firstDayWeekOffset = 4
                //
                // those defaults are based on the locales on the library
                // moment.js and won't be correct for _all_ locales.
                var sundayIndex = this.getSundayIndex();
                if (this.options.locale.firstDayWeekOffset === undefined) {
                    if (sundayIndex == 0) {
                        this.options.locale.firstDayWeekOffset = 6;
                    }
                    else if (sundayIndex == 1) {
                        this.options.locale.firstDayWeekOffset = 12;
                    }
                    else if (sundayIndex == 6) {
                        this.options.locale.firstDayWeekOffset = 4;
                    }
                    else {
                        this.options.showWeek = false;
                    }
                }
            }

            var parsedDefaultDate = this.parseValue(this.options.defaultDate);

            var viewDateDefaultsToNow=false;

            this.value = parsedDefaultDate;
            if (this.options.viewDate) {
                this.viewDate = this.parseValue(this.options.viewDate);
            }
            else {
                if ((this.isMultipleSelection() || this.isRangeSelection()) && parsedDefaultDate instanceof Array) {
                    this.viewDate = parsedDefaultDate[0];
                }
                else {
                    this.viewDate = parsedDefaultDate;
                }
                if (this.viewDate === null) {
                    this.viewDate = new Date();
                    if (!this.options.showSeconds) {
                        this.viewDate.setSeconds(0);
                    }
                    this.viewDate.setMilliseconds(0);
                    viewDateDefaultsToNow = true;
                }
            }

            // #6047 round to nearest stepMinute on even if editing using keyboard
            this.viewDate.setMinutes(this.stepMinute(this.viewDate.getMinutes()));
            if (!this.options.viewDate) {
                this.options.viewDate = this.viewDate;
            }

            this.options.minDate = this.parseMinMaxValue(this.options.minDate);
            this.options.maxDate = this.parseMinMaxValue(this.options.maxDate);
            this.ticksTo1970 = (((1970 - 1) * 365 + Math.floor(1970 / 4) - Math.floor(1970 / 100) + Math.floor(1970 / 400)) * 24 * 60 * 60 * 10000000);

            if (this.options.timeOnly && viewDateDefaultsToNow) {
                if (this.options.minDate) {
                    if (this.viewDate < this.options.minDate) {
                        this.viewDate = new Date(this.options.minDate.getTime());
                    }
                }
                if (this.options.maxDate) {
                    if (this.viewDate > this.options.maxDate) {
                        this.viewDate = new Date(this.options.maxDate.getTime());
                    }
                }
            }

            this.hasCustomYearRange = this.options.yearRange !== null;
            this.updateYearNavigator();

            if (this.options.disabledDates) {
                for (var i = 0; i < this.options.disabledDates.length; i++) {
                    this.options.disabledDates[i] = this.parseOptionValue(this.options.disabledDates[i]);
                }
            }
        },

        parseOptionValue: function(option) {
            if (option && typeof option === 'string') {
                return this.parseDate(option, this.options.dateFormat);
            }

            return option;
        },

        parseMinMaxValue: function(option) {
            if (option && typeof option === 'string') {
                return this.parseDateTime(option);
            }

            return option;
        },

        parseValue: function(option) {
            if (option && typeof option === 'string') {
                return this.parseValueFromString(option);
            }

            return option;
        },

        setDate: function(date) {
            if (!date) {
                this.updateModel(null, null);
                return;
            }

            var newDate = this.parseValue(date);
            var newDateMeta = { day: newDate.getDate(), month: newDate.getMonth(), year: newDate.getFullYear(), selectable: true /*, today: true*/ };

            /* set changes */
            this.value = newDate;
            this.updateViewDate(null, newDate);
            this.onDateSelect(null, newDateMeta);
        },

        getDate: function() {
            return this.value;
        },

        getFirstDayOfMonthIndex: function (month, year) {
            var day = new Date();
            day.setDate(1);
            day.setMonth(month);
            day.setFullYear(year);

            var dayIndex = day.getDay() + this.getSundayIndex();
            return dayIndex >= 7 ? dayIndex - 7 : dayIndex;
        },

        getSundayIndex: function () {
            var firstDayOfWeek = this.options.locale.firstDay !== undefined ? this.options.locale.firstDay : this.options.locale.firstDayOfWeek;
            return firstDayOfWeek > 0 ? 7 - firstDayOfWeek : 0;
        },

        getDaysCountInMonth: function (month, year) {
            return 32 - this.daylightSavingAdjust(new Date(year, month, 32)).getDate();
        },

        getDaysCountInPrevMonth: function (month, year) {
            var prev = this.getPreviousMonthAndYear(month, year);
            return this.getDaysCountInMonth(prev.month, prev.year);
        },

        daylightSavingAdjust: function (date) {
            if (!date) {
                return null;
            }

            date.setHours(date.getHours() > 12 ? date.getHours() + 2 : 0);

            return date;
        },

        getPreviousMonthAndYear: function (month, year) {
            var m, y;

            if (month === 0) {
                m = 11;
                y = year - 1;
            }
            else {
                m = month - 1;
                y = year;
            }

            return { 'month': m, 'year': y };
        },

        getNextMonthAndYear: function (month, year) {
            var m, y;

            if (month === 11) {
                m = 0;
                y = year + 1;
            }
            else {
                m = month + 1;
                y = year;
            }

            return { 'month': m, 'year': y };
        },

        createWeekDaysInternal: function (dayNames) {
            var weekDays = [],
                dayIndex = this.options.locale.firstDay !== undefined ? this.options.locale.firstDay : this.options.locale.firstDayOfWeek;
            for (var i = 0; i < 7; i++) {
                weekDays.push(dayNames[dayIndex]);
                dayIndex = (dayIndex === 6) ? 0 : ++dayIndex;
            }

            return weekDays;
        },

        createWeekDaysMin: function () {
            return this.createWeekDaysInternal(this.options.locale.dayNamesMin);
        },

        createWeekDaysShort: function () {
            return this.createWeekDaysInternal(this.options.locale.dayNamesShort);
        },

        createWeekDays: function () {
            return this.createWeekDaysInternal(this.options.locale.dayNames);
        },

        createMonths: function (month, year) {
            var months = [];
            for (var i = 0; i < this.options.numberOfMonths; i++) {
                var m = month + i,
                    y = year;
                if (m > 11) {
                    m = m % 11 - 1;
                    y = year + 1;
                }

                months.push(this.createMonth(m, y, i));
            }

            return months;
        },

        createMonth: function (month, year, index) {
            var dates = [];
            firstDay = this.getFirstDayOfMonthIndex(month, year);
            daysLength = this.getDaysCountInMonth(month, year);
            prevMonthDaysLength = this.getDaysCountInPrevMonth(month, year);
            dayNo = 1;
            today = new Date();

            for (var i = 0; i < 6; i++) {
                var week = [];

                if (i === 0) {
                    for (var j = (prevMonthDaysLength - firstDay + 1); j <= prevMonthDaysLength; j++) {
                        var prev = this.getPreviousMonthAndYear(month, year);
                        week.push({
                            day: j, month: prev.month, year: prev.year, otherMonth: true,
                            today: this.isToday(today, j, prev.month, prev.year), selectable: this.isSelectable(j, prev.month, prev.year, true)
                        });
                    }

                    var remainingDaysLength = 7 - week.length;
                    for (var j = 0; j < remainingDaysLength; j++) {
                        week.push({
                            day: dayNo, month: month, year: year, today: this.isToday(today, dayNo, month, year),
                            selectable: this.isSelectable(dayNo, month, year, false)
                        });
                        dayNo++;
                    }
                }
                else {
                    for (var j = 0; j < 7; j++) {
                        if (dayNo > daysLength) {
                            var next = this.getNextMonthAndYear(month, year);
                            week.push({
                                day: dayNo - daysLength, month: next.month, year: next.year, otherMonth: true,
                                today: this.isToday(today, dayNo - daysLength, next.month, next.year),
                                selectable: this.isSelectable((dayNo - daysLength), next.month, next.year, true)
                            });
                        }
                        else {
                            week.push({
                                day: dayNo, month: month, year: year, today: this.isToday(today, dayNo, month, year),
                                selectable: this.isSelectable(dayNo, month, year, false)
                            });
                        }

                        dayNo++;
                    }
                }

                dates.push(week);
            }

            return {
                month: month,
                year: year,
                dates: dates,
                index: index
            };
        },

        isSelectable: function (day, month, year, otherMonth) {
            var validMin = true;
            validMax = true;
            validDate = true;
            validDay = true;
            validMonth = true;

            if (this.options.minDate) {
                if (this.options.minDate.getFullYear() > year) {
                    validMin = false;
                }
                else if (this.options.minDate.getFullYear() === year) {
                    if (this.options.minDate.getMonth() > month) {
                        validMin = false;
                    }
                    else if (this.options.minDate.getMonth() === month) {
                        if (this.options.minDate.getDate() > day) {
                            validMin = false;
                        }
                    }
                }
            }

            if (this.options.maxDate) {
                if (this.options.maxDate.getFullYear() < year) {
                    validMax = false;
                }
                else if (this.options.maxDate.getFullYear() === year) {
                    if (this.options.maxDate.getMonth() < month) {
                        validMax = false;
                    }
                    else if (this.options.maxDate.getMonth() === month) {
                        if (this.options.maxDate.getDate() < day) {
                            validMax = false;
                        }
                    }
                }
            }

            if (this.options.disabledDates) {
                validDate = !this.isDateDisabled(day, month, year);
            }

            if (this.options.disabledDays) {
                validDay = !this.isDayDisabled(day, month, year)
            }

            if (this.options.selectOtherMonths === false && otherMonth) {
                validMonth = false;
            }

            return validMin && validMax && validDate && validDay && validMonth;
        },

        isSelected: function (dateMeta) {
            if (this.value) {
                if (this.isSingleSelection()) {
                    return this.isDateEquals(this.value, dateMeta);
                }
                else if (this.isMultipleSelection()) {
                    var selected = false;
                    for (var i = 0; i < this.value.length; i++) {
                        var date = this.value[i];
                        selected = this.isDateEquals(date, dateMeta);
                        if (selected) {
                            break;
                        }
                    }

                    return selected;
                }
                else if (this.isRangeSelection()) {
                    if (this.value[1])
                        return this.isDateEquals(this.value[0], dateMeta) || this.isDateEquals(this.value[1], dateMeta) || this.isDateBetween(this.value[0], this.value[1], dateMeta);
                    else
                        return this.isDateEquals(this.value[0], dateMeta)
                }
            }
            else {
                return false;
            }
        },

        isMonthSelected: function (month) {
            if (this.value) {
                if (this.isRangeSelection()) {
                    var dateMeta = { year: this.viewDate.getFullYear(), month: month, day: 1, selectable: true };

                    if (this.value[1])
                        return this.isDateEquals(this.value[0], dateMeta) || this.isDateEquals(this.value[1], dateMeta) || this.isDateBetween(this.value[0], this.value[1], dateMeta);
                    else
                        return this.isDateEquals(this.value[0], dateMeta)
                }
                else {
                    return (this.value.getMonth() === month && this.value.getFullYear() === this.viewDate.getFullYear());
                }
            }

            return false;
        },

        isDateEquals: function (value, dateMeta) {
            if (this.isDate(value))
                return value.getDate() === dateMeta.day && value.getMonth() === dateMeta.month && value.getFullYear() === dateMeta.year;
            else
                return false;
        },

        isDateBetween: function (start, end, dateMeta) {
            var between = false;
            if (start && end) {
                var date = new Date(dateMeta.year, dateMeta.month, dateMeta.day);
                return start.getTime() <= date.getTime() && end.getTime() >= date.getTime();
            }

            return between;
        },

        isSingleSelection: function () {
            return this.options.selectionMode === 'single';
        },

        isRangeSelection: function () {
            return this.options.selectionMode === 'range';
        },

        isMultipleSelection: function () {
            return this.options.selectionMode === 'multiple';
        },

        isToday: function (today, day, month, year) {
            return today.getDate() === day && today.getMonth() === month && today.getFullYear() === year;
        },

        isDateDisabled: function (day, month, year) {
            if (this.options.disabledDates) {
                for (var i = 0; i < this.options.disabledDates.length; i++) {
                    var disabledDate = this.options.disabledDates[i];
                    if (disabledDate.getFullYear() === year && disabledDate.getMonth() === month && disabledDate.getDate() === day) {
                        return true;
                    }
                }
            }

            return false;
        },

        isDayDisabled: function (day, month, year) {
            if (this.options.disabledDays) {
                var weekday = new Date(year, month, day),
                    weekdayNumber = weekday.getDay();
                return this.options.disabledDays.indexOf(weekdayNumber) !== -1;
            }
            return false;
        },

        getValueToRender: function () {
            var formattedValue = '';

            if (this.value) {
                try {
                    if (this.isSingleSelection()) {
                        formattedValue = this.formatDateTime(this.value);
                    }
                    else if (this.isMultipleSelection()) {
                        for (var i = 0; i < this.value.length; i++) {
                            var dateAsString = this.formatDateTime(this.value[i]);
                            formattedValue += dateAsString;
                            if (i !== (this.value.length - 1)) {
                                formattedValue += ', ';
                            }
                        }
                    }
                    else if (this.isRangeSelection()) {
                        if (this.value && this.value.length) {
                            var startDate = this.value[0],
                                endDate = this.value[1];

                            formattedValue = this.formatDateTime(startDate);
                            if (endDate) {
                                formattedValue += ' ' + this.options.rangeSeparator + ' ' + this.formatDateTime(endDate);
                            }
                        }
                    }
                }
                catch (err) {
                    formattedValue = this.value;
                }
            }

            return formattedValue;
        },

        formatDateTime: function (date) {
            var formattedValue = null;
            if (date) {
                if (this.options.timeOnly) {
                    formattedValue = this.formatTime(date);
                }
                else {
                    formattedValue = this.formatDate(date, this.options.dateFormat);
                    if (this.options.showTime) {
                        formattedValue += ' ' + this.formatTime(date);
                    }
                }
            }

            return formattedValue;
        },

        // Ported from jquery-ui datepicker formatDate
        formatDate: function (date, format) {
            if (!date) {
                return '';
            }

            var iFormat,
                lookAhead = function (match) {
                    var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) === match);
                    if (matches) {
                        iFormat++;
                    }
                    return matches;
                },
                formatNumber = function (match, value, len) {
                    var num = '' + value;
                    if (lookAhead(match)) {
                        while (num.length < len) {
                            num = '0' + num;
                        }
                    }
                    return num;
                },
                formatName = function (match, value, shortNames, longNames) {
                    return (lookAhead(match) ? longNames[value] : shortNames[value]);
                };
            var output = '',
                literal = false;

            if (date) {
                for (iFormat = 0; iFormat < format.length; iFormat++) {
                    if (literal) {
                        if (format.charAt(iFormat) === '\'' && !lookAhead('\'')) {
                            literal = false;
                        } else {
                            output += format.charAt(iFormat);
                        }
                    } else {
                        switch (format.charAt(iFormat)) {
                            case 'd':
                                output += formatNumber('d', date.getDate(), 2);
                                break;
                            case 'D':
                                output += formatName('D', date.getDay(), this.options.locale.dayNamesShort, this.options.locale.dayNames);
                                break;
                            case 'o':
                                output += formatNumber('o',
                                    Math.round((
                                        new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime() -
                                        new Date(date.getFullYear(), 0, 0).getTime()) / 86400000), 3);
                                break;
                            case 'm':
                                output += formatNumber('m', date.getMonth() + 1, 2);
                                break;
                            case 'M':
                                output += formatName('M', date.getMonth(), this.options.locale.monthNamesShort, this.options.locale.monthNames);
                                break;
                            case 'y':
                                output += lookAhead('y') ? date.getFullYear() : (date.getFullYear() % 100 < 10 ? '0' : '') + (date.getFullYear() % 100);
                                break;
                            case '@':
                                output += date.getTime();
                                break;
                            case '!':
                                output += date.getTime() * 10000 + this.ticksTo1970;
                                break;
                            case '\'':
                                if (lookAhead('\'')) {
                                    output += '\'';
                                } else {
                                    literal = true;
                                }
                                break;
                            default:
                                output += format.charAt(iFormat);
                        }
                    }
                }
            }
            return output;
        },

        formatTime: function (date) {
            if (!date) {
                return '';
            }

            var output = '',
                hours = date.getHours(),
                minutes = date.getMinutes(),
                seconds = date.getSeconds();

            if (this.options.hourFormat == '12' && hours > 11 && hours != 12) {
                hours -= 12;
            }

            if (this.options.hourFormat == '12') {
                output += hours === 0 ? 12 : (hours < 10) ? '0' + hours : hours;
            } else {
                output += (hours < 10) ? '0' + hours : hours;
            }
            output += this.options.timeSeparator;
            output += (minutes < 10) ? '0' + minutes : minutes;

            if (this.options.showSeconds) {
                output += this.options.timeSeparator;
                output += (seconds < 10) ? '0' + seconds : seconds;
            }

            if (this.options.hourFormat == '12') {
                output += date.getHours() > 11 ? ' PM' : ' AM';
            }

            return output;
        },

        parseTime: function (value, ampm) {
            var tokens = value.split(this.options.timeSeparator),
                validTokenLength = this.options.showSeconds ? 3 : 2;

            if (tokens.length !== validTokenLength) {
                throw "Invalid time";
            }

            var h = parseInt(tokens[0]),
                m = parseInt(tokens[1]),
                s = this.options.showSeconds ? parseInt(tokens[2]) : null;

            if (isNaN(h) || isNaN(m) || h > 23 || m > 59 || (this.options.hourFormat == '12' && h > 12) || (this.options.showSeconds && (isNaN(s) || s > 59))) {
                throw "Invalid time";
            }
            else {
                if (this.options.hourFormat == '12' && h !== 12 && ampm === 'PM') {
                    h += 12;
                }

                return { hour: h, minute: m, second: s };
            }
        },

        // Ported from jquery-ui datepicker parseDate
        parseDate: function (value, format) {
            if (format == null || value == null) {
                throw "Invalid arguments";
            }

            value = (typeof value === "object" ? value.toString() : value + "");
            if (value === "") {
                return null;
            }

            var iFormat, dim, extra,
                iValue = 0,
                shortYearCutoff = (typeof this.options.shortYearCutoff !== "string" ? this.options.shortYearCutoff : new Date().getFullYear() % 100 + parseInt(this.options.shortYearCutoff, 10)),
                year = -1,
                month = -1,
                day = -1,
                doy = -1,
                literal = false,
                date,
                lookAhead = function (match) {
                    var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) === match);
                    if (matches) {
                        iFormat++;
                    }
                    return matches;
                },
                getNumber = function (match) {
                    var isDoubled = lookAhead(match),
                        size = (match === "@" ? 14 : (match === "!" ? 20 :
                            (match === "y" && isDoubled ? 4 : (match === "o" ? 3 : 2)))),
                        minSize = (match === "y" ? size : 1),
                        digits = new RegExp("^\\d{" + minSize + "," + size + "}"),
                        num = value.substring(iValue).match(digits);
                    if (!num && match === "y" && isDoubled) {
                        //allow 2 digit-inputs for 4-digit-year-pattern (gets reformated to 4 digits onBlur)
                        digits = new RegExp("^\\d{" + 2 + "," + size + "}"),
                            num = value.substring(iValue).match(digits);
                    }
                    if (!num) {
                        throw "Missing number at position " + iValue;
                    }
                    iValue += num[0].length;
                    return parseInt(num[0], 10);
                },
                getName = function (match, shortNames, longNames) {
                    var index = -1,
                        arr = lookAhead(match) ? longNames : shortNames,
                        names = [];

                    for (var i = 0; i < arr.length; i++) {
                        names.push([i, arr[i]]);
                    }
                    names.sort(function (a, b) {
                        return -(a[1].length - b[1].length);
                    });

                    for (var i = 0; i < names.length; i++) {
                        var name = names[i][1];
                        if (value.substr(iValue, name.length).toLowerCase() === name.toLowerCase()) {
                            index = names[i][0];
                            iValue += name.length;
                            break;
                        }
                    }

                    if (index !== -1) {
                        return index + 1;
                    } else {
                        throw "Unknown name at position " + iValue;
                    }
                },
                checkLiteral = function() {
                    if (value.charAt(iValue) !== format.charAt(iFormat)) {
                        throw "Unexpected literal at position " + iValue;
                    }
                    iValue++;
                };

            if (this.options.view === 'month') {
                day = 1;
            }

            for (iFormat = 0; iFormat < format.length; iFormat++) {
                if (literal) {
                    if (format.charAt(iFormat) === "'" && !lookAhead("'")) {
                        literal = false;
                    } else {
                        checkLiteral();
                    }
                } else {
                    switch (format.charAt(iFormat)) {
                        case "d":
                            day = getNumber("d");
                            break;
                        case "D":
                            getName("D", this.options.locale.dayNamesShort, this.options.locale.dayNames);
                            break;
                        case "o":
                            doy = getNumber("o");
                            break;
                        case "m":
                            month = getNumber("m");
                            break;
                        case "M":
                            month = getName("M", this.options.locale.monthNamesShort, this.options.locale.monthNames);
                            break;
                        case "y":
                            year = getNumber("y");
                            break;
                        case "@":
                            date = new Date(getNumber("@"));
                            year = date.getFullYear();
                            month = date.getMonth() + 1;
                            day = date.getDate();
                            break;
                        case "!":
                            date = new Date((getNumber("!") - this.ticksTo1970) / 10000);
                            year = date.getFullYear();
                            month = date.getMonth() + 1;
                            day = date.getDate();
                            break;
                        case "'":
                            if (lookAhead("'")) {
                                checkLiteral();
                            } else {
                                literal = true;
                            }
                            break;
                        default:
                            checkLiteral();
                    }
                }
            }

            if (iValue < value.length) {
                extra = value.substr(iValue);
                if (!/^\s+/.test(extra)) {
                    throw "Extra/unparsed characters found in date: " + extra;
                }
            }

            if (year === -1) {
                year = new Date().getFullYear();
            } else if (year < 100) {
                year += new Date().getFullYear() - new Date().getFullYear() % 100 +
                    (year <= shortYearCutoff ? 0 : -100);
            }

            if (doy > -1) {
                month = 1;
                day = doy;
                do {
                    dim = this.getDaysCountInMonth(year, month - 1);
                    if (day <= dim) {
                        break;
                    }
                    month++;
                    day -= dim;
                } while (true);
            }

            date = this.daylightSavingAdjust(new Date(year, month - 1, day));
            if (date.getFullYear() !== year || date.getMonth() + 1 !== month || date.getDate() !== day) {
                throw "Invalid date"; // E.g. 31/02/00
            }

            return date;
        },

        parseValueFromString: function (text) {
            if (!text || text.trim().length === 0) {
                return null;
            }

            var value;

            if (this.isSingleSelection()) {
                value = this.parseDateTime(text);
            }
            else if (this.isMultipleSelection()) {
                var tokens = text.split(',');
                value = [];
                for (var i = 0; i < tokens.length; i++) {
                    value.push(this.parseDateTime(tokens[i].trim()));
                }
            }
            else if (this.isRangeSelection()) {
                var tokens = text.split(new RegExp(this.options.rangeSeparator + '| ' + this.options.rangeSeparator + ' ', 'g'));
                value = [];
                for (var i = 0; i < tokens.length; i++) {
                    value[i] = this.parseDateTime(tokens[i].trim());
                }
            }

            return value;
        },

        parseDateTime: function (text) {
            var date,
                parts = text.split(' ');

            if (this.options.timeOnly) {
                date = new Date();
                this.populateTime(date, parts[0], parts[1]);
            }
            else {
                if (this.options.showTime) {
                    var ampm = this.options.hourFormat == '12' ? parts.pop() : null;
                    var timeString = parts.pop();

                    date = this.parseDate(parts.join(' '), this.options.dateFormat);
                    this.populateTime(date, timeString, ampm);
                }
                else {
                    date = this.parseDate(text, this.options.dateFormat);
                }
            }

            return date;
        },

        populateTime: function (value, timeString, ampm) {
            if (this.options.hourFormat === '12' && (ampm !== 'PM' && ampm !== 'AM')) {
                throw new Error('Invalid Time');
            }

            var time = this.parseTime(timeString, ampm);
            value.setHours(time.hour);
            value.setMinutes(this.stepMinute(time.minute));
            if (this.options.showSeconds) {
                value.setSeconds(time.second);
            }
            else {
                value.setSeconds(0);
            }
            value.setMilliseconds(0);
        },

        isInMinYear: function() {
            return this.options.minDate && this.options.minDate.getFullYear() === this.viewDate.getFullYear();
        },

        isInMaxYear: function() {
            return this.options.maxDate && this.options.maxDate.getFullYear() === this.viewDate.getFullYear();
        },

        _destroy: function () {
            this.hideOverlay();
        },

        /**
         * @override
         * @protected
         */
        _render: function () {
            if (this.options.styleClass) {
                this.container.addClass(this.options.styleClass);
            }

            if (this.options.style) {
                this.container.attr('style', this.options.style);
            }

            if (!this.options.inline) {
                if (this.options.inputStyleClass) {
                    this.inputfield.addClass(this.options.inputStyleClass);
                }

                if (this.options.inputStyle) {
                    this.inputfield.attr('style', this.options.inputStyle);
                }
            }

            if (this.options.showIcon && !this.options.inline) {
                if (this.triggerButton) {
                    this.triggerButton.remove();
                }
                this.renderTriggerButton();
                this.container.append(this.triggerButton);
                this.container.addClass('ui-trigger-calendar');
            }

            if (this.panel) {
                this.panel.remove();
            }
            this.renderDatePickerPanel();

            if (this.options.panelStyleClass) {
                this.panel.addClass(this.options.panelStyleClass);
            }

            if (this.options.panelStyle) {
                this.panel.attr('style', this.options.panelStyle);
            }

            if (!this.options.inline && this.options.appendTo) {
                // remove old overlay first
                // See PrimeFaces.utils.appendDynamicOverlay
                $(this.options.appendTo)
                        .children("[id='" + $(this.container).attr('id') + "_panel']")
                        .not(this.panel)
                        .remove();

                this.panel.appendTo(this.options.appendTo);
            }
            else {
                this.panel.appendTo(this.container);
            }

            this._setInitOptionValues();

            this._bindEvents();
        },

        _setInitOptionValues: function () {
            if (this.options.yearNavigator) {
                var year = this.viewDate.getFullYear();
                var month = this.viewDate.getMonth();
                var yearElts = this.panel.find('.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-year');

                yearElts.each(function( index, yearElt ) {
                    $(yearElt).val(year);
                    month = month + 1;
                    if (month === 12) {
                        month = 0;
                        year = year + 1;
                    }
                });
            }

            if (this.options.monthNavigator && this.options.view !== 'month') {
                var month = this.viewDate.getMonth();
                var monthElts = this.panel.find('.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-month');

                monthElts.each(function( index, monthElt ) {
                    $(monthElt).val(month);
                    month = month + 1;
                    if (month === 12) {
                        month = 0;
                    }
                });
            }

            // #6379 set state of navigator buttons
            this.setNavigationState(this.viewDate);
        },

        renderTriggerButton: function () {
            this.triggerButton = $('<button type="button" class="ui-datepicker-trigger ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only' + (this.options.disabled ? ' ui-state-disabled' : '') + '" tabindex="-1">' +
                '<span class="ui-button-icon-left ' + this.options.icon + '"></span>' +
                '<span class="ui-button-text">ui-button</span>' +
                '</button>');
        },

        renderDatePickerPanel: function () {
            //add classes according to conditions
            var _classes = this.getClassesToAdd({
                'ui-datepicker-inline': this.options.inline,
                'ui-shadow': !this.options.inline,
                'ui-input-overlay': !this.options.inline,
                'ui-state-disabled': this.options.disabled,
                'ui-state-error': this.options.inline && !this.options.valid,
                'ui-datepicker-timeonly': this.options.timeOnly,
                'ui-datepicker-multiple-month': this.options.numberOfMonths > 1,
                'ui-datepicker-monthpicker': (this.options.view === 'month'),
                'ui-datepicker-touch-ui': this.options.touchUI
            });

            var panelId = this.container.attr('id') + '_panel';
            this.panel = $('<div id="' + panelId + '" class="ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all ' + _classes + '"></div>');

            //render inner elements
            this.panel.get(0).innerHTML = this.renderPanelElements();

            this.panel.css({
                'display': this.options.inline ? 'block' : 'none',
                'position': this.options.inline || this.options.touchUI ? '' : 'absolute'
            });

            if (this.options.onPanelCreate) {
                this.options.onPanelCreate.call(this);
            }
        },

        renderPanelElements: function () {
            var elementsHtml = '';

            if (!this.options.timeOnly) {
                if (this.options.view == 'date') {
                    elementsHtml += this.renderDateView();
                }
                else if (this.options.view == 'month') {
                    elementsHtml += this.renderMonthView();
                }
            }

            if (this.options.showTime || this.options.timeOnly) {
                elementsHtml += this.renderTimePicker();
            }

            if (this.options.showButtonBar) {
                elementsHtml += this.renderButtonBar();
            }

            return elementsHtml;
        },

        renderDateView: function () {
            this.monthsMetadata = this.createMonths(this.viewDate.getMonth(), this.viewDate.getFullYear());
            var months = this.renderMonths(this.monthsMetadata);

            return months;
        },

        renderMonthView: function () {
            var backwardNavigator = this.renderBackwardNavigator(),
                forwardNavigator = this.renderForwardNavigator(),
                yearElement = this.renderTitleYearElement(this.viewDate.getFullYear()),
                months = this.renderMonthViewMonths();

            return ('<div class="ui-datepicker-header ui-widget-header ui-helper-clearfix ui-corner-all">' +
                backwardNavigator +
                forwardNavigator +
                '<div class="ui-datepicker-title">' +
                yearElement +
                '</div>' +
                '</div>' +
                '<div class="ui-monthpicker">' +
                months +
                '</div>' +
                '</div>');
        },

        renderTimePicker: function () {
            var timepicker = '<div class="ui-timepicker ui-widget-header ui-corner-all' + (this.options.timeInput ? ' ui-timepicker-timeinput' : '') + '">';

            //hour
            timepicker += this.renderHourPicker();
            //separator
            timepicker += this.renderSeparator();
            //minute
            timepicker += this.renderMinutePicker();
            //separator
            timepicker += this.options.showSeconds ? this.renderSeparator() : '';
            //second
            timepicker += this.renderSecondPicker();
            //ampm
            timepicker += this.renderAmPmPicker();

            //end
            timepicker += '</div>';

            return timepicker;
        },

        renderButtonBar: function () {
            return '<div class="ui-datepicker-buttonbar ui-widget-header">' +
                '<div class="ui-g">' +
                '<div class="ui-g-6">' +
                '<button type="button" class="ui-today-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ' + this.options.todayButtonStyleClass + '"><span class="ui-button-text">' + this.options.locale.today + '</span></button>' +
                '</div>' +
                '<div class="ui-g-6">' +
                '<button type="button" class="ui-clear-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ' + this.options.clearButtonStyleClass + '"><span class="ui-button-text">' + this.options.locale.clear + '</span></button>' +
                '</div>' +
                '</div>' +
                '</div>';
        },

        renderMonthViewMonth: function (index) {
            var monthName = this.options.locale.monthNamesShort[index],
                content = this.options.dateTemplate ? this.options.dateTemplate.call(this, monthName) : this.escapeHTML(monthName),
                compareDate = new Date(this.viewDate.getFullYear(), index, 1),
                minDate = this.options.minDate,
                maxDate = this.options.maxDate,
                disabled = false;

            if (minDate && minDate > compareDate) {
                disabled = true;
            }

            if (maxDate && maxDate < compareDate) {
                disabled = true;
            }

            var monthClass = this.getClassesToAdd({
                'ui-state-active': this.isMonthSelected(index),
                'ui-state-disabled': disabled
            });

            return '<a tabindex="0" class="ui-monthpicker-month' + monthClass + '">' + content + '</a>';
        },

        renderMonthViewMonths: function () {
            var months = '';
            for (var i = 0; i <= 11; i++) {
                months += this.renderMonthViewMonth(i);
            }

            return months;
        },

        renderMonths: function (monthsMetadata) {
            var monthsHtml = '';

            for (var i = 0; i < monthsMetadata.length; i++) {
                monthsHtml += this.renderMonth(monthsMetadata[i], i);
            }

            return monthsHtml;
        },

        renderMonth: function (monthMetadata, index) {
            var weekDaysMin = this.createWeekDaysMin(),
                weekDays = this.createWeekDays(),
                backwardNavigator = (index === 0) ? this.renderBackwardNavigator() : '',
                forwardNavigator = (this.options.numberOfMonths === 1) || (index === this.options.numberOfMonths - 1) ? this.renderForwardNavigator() : '',
                title = this.renderTitle(monthMetadata),
                dateViewGrid = this.renderDateViewGrid(monthMetadata, weekDaysMin, weekDays);

            return ('<div class="ui-datepicker-group ui-widget-content">' +
                '<div class="ui-datepicker-header ui-widget-header ui-helper-clearfix ui-corner-all">' +
                backwardNavigator +
                forwardNavigator +
                title +
                '</div>' +
                dateViewGrid +
                '</div>');
        },

        renderBackwardNavigator: function () {
            return '<a class="ui-datepicker-prev ui-corner-all" tabindex="0">' +
                '<span class="ui-icon ui-icon-circle-triangle-w"></span>' +
                '</a>';
        },

        renderForwardNavigator: function () {
            return '<a class="ui-datepicker-next ui-corner-all" tabindex="0">' +
                '<span class="ui-icon ui-icon-circle-triangle-e"></span>' +
                '</a>';
        },

        renderTitleMonthElement: function (month, index) {
            if (this.options.monthNavigator && this.options.view !== 'month' && index === 0) {
                return '<select class="ui-datepicker-month">' + this.renderTitleOptions('month', this.options.locale.monthNamesShort) + '</select>';
            }
            else {
                return '<span class="ui-datepicker-month">' + this.escapeHTML(this.options.locale.monthNames[month]) + '</span>' + '&#xa0;';
            }
        },

        renderTitleYearElement: function (year, index) {
            if (this.options.yearNavigator && index === 0) {
                this.updateYearNavigator();
                var yearOptions = [],
                    years = this.options.yearRange.split(':'),
                    yearStart = parseInt(years[0], 10),
                    yearEnd = parseInt(years[1], 10);

                for (var i = yearStart; i <= yearEnd; i++) {
                    yearOptions.push(i);
                }

                return '<select class="ui-datepicker-year">' + this.renderTitleOptions('year', yearOptions) + '</select>';
            }
            else {
                return '<span class="ui-datepicker-year">' + year + '</span>';
            }
        },

        renderTitleOptions: function (name, options) {
            var _options = '',
                minDate = this.options.minDate,
                maxDate = this.options.maxDate;

            for (var i = 0; i < options.length; i++) {
                switch(name) {
                    case 'month':
                        if ((!this.isInMinYear() || i >= minDate.getMonth()) && (!this.isInMaxYear() || i <= maxDate.getMonth())) {
                            _options += '<option value="' + i + '">' + this.escapeHTML(options[i]) + '</option>';
                        }
                        break;
                    case 'year':
                        var option = options[i];
                        if (!(minDate && minDate.getFullYear() > option) && !(maxDate && maxDate.getFullYear() < option)) {
                            _options += '<option value="' +  option + '">' +  option + '</option>';
                        }
                        break;
                }
            }

            return _options;
        },

        renderTitle: function (monthMetadata) {
            var month = this.renderTitleMonthElement(monthMetadata.month, monthMetadata.index),
                year = this.renderTitleYearElement(monthMetadata.year, monthMetadata.index);

            return (
                '<div class="ui-datepicker-title">' +
                month +
                year +
                '</div>'
            );
        },

        renderDayNames: function (weekDaysMin, weekDays) {
            var dayNamesHtml = '';

            if(this.options.showWeek) {
                dayNamesHtml += '<th scope="col">' +
                    '<span>' +
                    this.options.locale.weekHeader +
                    '</span>' +
                    '</th>';
            }

            for (var i = 0; i < weekDaysMin.length; i++) {
                dayNamesHtml += '<th scope="col">' +
                    '<span title="' + this.escapeHTML(weekDays[i]) + '">' +
                    weekDaysMin[i] +
                    '</span>' +
                    '</th>';
            }

            return dayNamesHtml;
        },

        // utility methods for calculateWeekNumber. Based on the implementation from moment.js
        // start-of-first-week - start-of-year
        firstWeekOffset: function(year, dow, doy) {
                // first-week day -- which january is always in the first week (4 for iso, 1 for other)
            var fwd = 7 + dow - doy,
                // first-week day local weekday -- which local weekday is fwd
                fwdlw = (7 + new Date(Date.UTC(year, 0, fwd)).getUTCDay() - dow) % 7;

            return -fwdlw + fwd - 1;
        },

        dayOfYear: function(d) {
            return Math.round( ( new Date( d.year, d.month, d.day ).getTime() - new Date( d.year, 0, 0 ).getTime() ) / 86400000 );
        },

        daysInYear: function(year) {
            if((year % 4 === 0 && year % 100 !== 0) || year % 400 === 0) {
                return 366;
            }
            return 365;
        },

        weeksInYear: function(year, dow, doy) {
            var weekOffset = this.firstWeekOffset(year, dow, doy),
                weekOffsetNext = this.firstWeekOffset(year + 1, dow, doy);

            return (this.daysInYear(year) - weekOffset + weekOffsetNext) / 7;
        },

        calculateWeekNumber: function(d) {
            var dow = this.options.locale.firstDay !== undefined ? this.options.locale.firstDay : this.options.locale.firstDayOfWeek,
                doy = this.options.locale.firstDayWeekOffset,
                weekOffset = this.firstWeekOffset(d.year, dow, doy),
                week = Math.floor((this.dayOfYear(d) - weekOffset - 1) / 7) + 1;

            if(week < 1) {
                return week + this.weeksInYear(resYear, dow, doy);
            }
            else if(week > this.weeksInYear(d.year, dow, doy)) {
                return  week - this.weeksInYear(d.year, dow, doy);
            }
            else {
                return  week;
            }
        },

        renderWeek: function (weekDates) {
            var weekHtml = '';

            if(this.options.showWeek) {
                var firstDate = weekDates[0],
                    lastDate = weekDates[6],
                    cellClass = firstDate.otherMonth && lastDate.otherMonth && !this.options.showOtherMonths ? ' ui-datepicker-other-month-hidden' : '';

                weekHtml += '<td class="ui-datepicker-weeknumber' + cellClass + '"><span class="ui-state-disabled">' +
                    this.options.weekCalculator(firstDate) +
                    '</span></td>';
            }

            for (var i = 0; i < weekDates.length; i++) {
                var date = weekDates[i],
                    cellClass = this.getClassesToAdd({
                        'ui-datepicker-other-month': date.otherMonth,
                        'ui-datepicker-today': date.today,
                        'ui-datepicker-other-month-hidden': date.otherMonth && !this.options.showOtherMonths
                    }),
                    dateClass = this.getClassesToAdd({
                        'ui-state-default': true,
                        'ui-state-active': this.isSelected(date),
                        'ui-state-disabled': !date.selectable,
                        'ui-state-highlight': date.today
                    }),
                    content = this.renderDateCellContent(date, dateClass);

                weekHtml += (
                    '<td class="' + cellClass + '">' +
                    content +
                    '</td>'
                );
            }

            return weekHtml;
        },

        renderDateCellContent: function (date, dateClass) {
            var content = this.options.dateTemplate ? this.options.dateTemplate.call(this, date) : date.day;
            var classes = this.options.dateStyleClasses;
            if (classes !== null) {
                var isoDateStr = this.toISODateString(new Date(date.year, date.month, date.day));
                if (classes[isoDateStr]) {
                    dateClass += ' ' + classes[isoDateStr];
                }
            }
            if (date.selectable) {
                return '<a tabindex="0" class="' + dateClass + '">' + content + '</a>';
            }
            else {
                return '<span class="' + dateClass + '">' + content + '</span>';
            }
        },

        renderDates: function (monthMetadata) {
            var datesHtml = '';

            for (var i = 0; i < monthMetadata.dates.length; i++) {
                var week = monthMetadata.dates[i];

                datesHtml += '<tr>' +
                    this.renderWeek(week) +
                    '</tr>';
            }

            return datesHtml;
        },

        renderDateViewGrid: function (monthMetadata, weekDaysMin, weekDays) {
            var dayNames = this.renderDayNames(weekDaysMin, weekDays),
                dates = this.renderDates(monthMetadata);

            return (
                '<div class="ui-datepicker-calendar-container">' +
                '<table class="ui-datepicker-calendar">' +
                '<thead>' +
                '<tr>' +
                dayNames +
                '</tr>' +
                '</thead>' +
                '<tbody>' +
                dates +
                '</tbody>' +
                '</table>' +
                '</div>'
            );
        },

        renderHourPicker: function () {
            var hour = this.isDate(this.value) ? this.value.getHours() : this.viewDate.getHours();

            if (this.options.hourFormat === '12') {
                if (hour === 0)
                    hour = 12;
                else if (hour > 11 && hour !== 12)
                    hour = hour - 12;
            }

            var hourDisplay = hour < 10 ? '0' + hour : hour;

            //type="number" min="' + minHour + '" max="' + maxHour + '" - does not work well on Firefox 70, so we don´t use it
            var html = this.options.timeInput ? '<input value="' + hourDisplay + '" size="2" maxlength="2" tabindex="1"></input>' : '<span>' + hourDisplay + '</span>';
            return this.renderTimeElements("ui-hour-picker", html, 0);
        },

        renderMinutePicker: function () {
            var minute = this.isDate(this.value) ? this.value.getMinutes() : this.viewDate.getMinutes(),
                minuteDisplay = minute < 10 ? '0' + minute : minute;

            //type="number" min="0" max="59" does not work well on Firefox 70, so we don´t use it
            var html = this.options.timeInput ? '<input value="' + minuteDisplay + '" size="2" maxlength="2" tabindex="2"></input>' : '<span>' + minuteDisplay + '</span>';
            return this.renderTimeElements("ui-minute-picker", html, 1);
        },

        renderSecondPicker: function () {
            if (this.options.showSeconds) {
                var second = this.isDate(this.value) ? this.value.getSeconds() : this.viewDate.getSeconds(),
                    secondDisplay = second < 10 ? '0' + second : second;

                //type="number" min="0" max="59" does not work well on Firefox 70, so we don´t use it
                var html =  this.options.timeInput ? '<input value="' + secondDisplay + '" size="2" maxlength="2" tabindex="3"></input>' : '<span>' + secondDisplay + '</span>';
                return this.renderTimeElements("ui-second-picker", html, 2);
            }

            return '';
        },

        renderAmPmPicker: function () {
            if (this.options.hourFormat === '12') {
                var hour = this.isDate(this.value) ? this.value.getHours() : this.viewDate.getHours(),
                    display = hour > 11 ? 'PM' : 'AM';

                return this.renderTimeElements("ui-ampm-picker", '<span>' + display + '</span>', 3);
            }

            return '';
        },

        renderSeparator: function () {
            return this.renderTimeElements("ui-separator", '<span>:</span>', -1);
        },

        renderTimeElements: function (containerClass, text, type) {
            var container = '<div class="' + containerClass + '" data-type="' + type + '">';

            //up
            container += this.renderTimePickerUpButton();
            //text
            container += text;
            //down
            container += this.renderTimePickerDownButton();

            //end
            container += '</div>';

            return container;
        },

        renderTimePickerUpButton: function () {
            return '<a tabindex="0" class="ui-picker-up">' +
                '<span class="ui-icon ui-icon-carat-1-n"></span>' +
                '</a>';
        },

        renderTimePickerDownButton: function () {
            return '<a tabindex="0" class="ui-picker-down">' +
                '<span class="ui-icon ui-icon-carat-1-s"></span>' +
                '</a>';
        },

        getClassesToAdd: function (classes) {
            var _classes = '';
            $.each(classes, function (key, value) {
                if (value) {
                    _classes += ' ' + key;
                }
            });

            return _classes;
        },

        /**
         * Converts a date object to an ISO date (only, no time) string. Useful to check if a dates matches with a date
         * sent from the backend whithout needing to parse the backend date first.
         * @private
         * @param {Date} date The date to convert.
         */
        toISODateString: function (date) {
            return date.toISOString().substring(0, 10);
        },

        _bindEvents: function () {
            var $this = this;
            if (!this.options.inline) {
                this.inputfield.off('focus.datePicker blur.datePicker keydown.datePicker input.datePicker click.datePicker')
                    .on('focus.datePicker', this.onInputFocus.bind($this))
                    .on('blur.datePicker', this.onInputBlur.bind($this))
                    .on('keydown.datePicker', this.onInputKeyDown.bind($this))
                    .on('input.datePicker', this.onUserInput.bind($this))
                    .on('click.datePicker', this.onInputClick.bind($this));

                if (this.triggerButton) {
                    this.triggerButton.off('click.datePicker-triggerButton').on('click.datePicker-triggerButton', this.onButtonClick.bind($this));
                }
            }

            this.panel.off('click.datePicker').on('click.datePicker', this.onPanelClick.bind($this));

            var navBackwardSelector = '.ui-datepicker-header > .ui-datepicker-prev',
                navForwardSelector = '.ui-datepicker-header > .ui-datepicker-next';
            this.panel.off('click.datePicker-navBackward', navBackwardSelector).on('click.datePicker-navBackward', navBackwardSelector, null, this.navBackward.bind($this));
            this.panel.off('click.datePicker-navForward', navForwardSelector).on('click.datePicker-navForward', navForwardSelector, null, this.navForward.bind($this));

            var monthNavigatorSelector = '.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-month',
                yearNavigatorSelector = '.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-year';
            this.panel.off('change.datePicker-monthNav', monthNavigatorSelector).on('change.datePicker-monthNav', monthNavigatorSelector, null, this.onMonthDropdownChange.bind($this));
            this.panel.off('change.datePicker-yearNav', yearNavigatorSelector).on('change.datePicker-yearNav', yearNavigatorSelector, null, this.onYearDropdownChange.bind($this));

            var monthViewMonthSelector = '.ui-monthpicker > .ui-monthpicker-month';
            this.panel.off('change.datePicker-monthViewMonth', monthViewMonthSelector).on('click.datePicker-monthViewMonth', monthViewMonthSelector, null, function (e) {
                $this.onMonthSelect(e, $(this).index());
            });

            var timeSelector = '.ui-hour-picker > a,  .ui-minute-picker > a, .ui-second-picker > a',
                ampmSelector = '.ui-ampm-picker > a';
            this.panel.off('mousedown.datePicker-time mouseup.datePicker-time mouseleave.datePicker-time', timeSelector).off('click.datePicker-ampm', ampmSelector)
                .on('mousedown.datePicker-time', timeSelector, null, function (event) {
                    var button = $(this),
                        parentEl = button.parent();

                    $this.onTimePickerElementMouseDown(event, parseInt(parentEl.data('type'), 10), button.hasClass('ui-picker-up') ? 1 : -1);
                })
                .on('mouseup.datePicker-time', timeSelector, null, function (event) {
                    $this.onTimePickerElementMouseUp(event);
                })
                .on('mouseleave.datePicker-time', timeSelector, null, function (event) {
                    if ($this.timePickerTimer) {
                        $this.onTimePickerElementMouseUp(event);
                    }
                })
                .on('click.datePicker-ampm', ampmSelector, null, function (event) {
                    $this.toggleAmPm(event);
                });

            if (this.options.timeInput) {
                this.panel.off('focus', '.ui-hour-picker input').on('focus', '.ui-hour-picker input', null, function (event) {
                    $this.oldHours = this.value;
                }).off('focus', '.ui-minute-picker input').on('focus', '.ui-minute-picker input', null, function (event) {
                    $this.oldMinutes = this.value;
                }).off('focus', '.ui-second-picker input').on('focus', '.ui-second-picker input', null, function (event) {
                    $this.oldSeconds = this.value;
                }).off('change', '.ui-hour-picker input').on('change', '.ui-hour-picker input', null, function (event) {
                    $this.handleHoursInput(this, event);
                }).off('change', '.ui-minute-picker input').on('change', '.ui-minute-picker input', null, function (event) {
                    $this.handleMinutesInput(this, event);
                }).off('change', '.ui-second-picker input').on('change', '.ui-second-picker input', null, function (event) {
                    $this.handleSecondsInput(this, event);
                });
            }

            var todayButtonSelector = '.ui-datepicker-buttonbar .ui-today-button',
                clearButtonSelector = '.ui-datepicker-buttonbar .ui-clear-button';
            this.panel.off('click.datePicker-todayButton', todayButtonSelector).on('click.datePicker-todayButton', todayButtonSelector, null, this.onTodayButtonClick.bind($this));
            this.panel.off('click.datePicker-clearButton', clearButtonSelector).on('click.datePicker-clearButton', clearButtonSelector, null, this.onClearButtonClick.bind($this));

            var dateSelector = '.ui-datepicker-calendar td a';
            this.panel.off('click.datePicker-date', dateSelector).on('click.datePicker-date', dateSelector, null, function (event) {
                if ($this.monthsMetadata) {
                    var dayEl = $(this),
                        calendarIndex = dayEl.closest('.ui-datepicker-group').index(),
                        weekIndex = dayEl.closest('tr').index(),
                        dayIndex = dayEl.closest('td').index() - ($this.options.showWeek ? 1 : 0);
                    $this.onDateSelect(event, $this.monthsMetadata[calendarIndex].dates[weekIndex][dayIndex]);
                }
            });
        },

        onInputClick: function (event) {
            if (this.documentClickListener) {
                this.datepickerClick = true;
            }

            if (this.options.showOnFocus && !this.panel.is(':visible')) {
                this.showOverlay();
            }
        },

        onInputFocus: function (event) {
            if (this.options.showOnFocus && !this.panel.is(':visible')) {
                this.showOverlay();
            }

            if (this.options.onFocus) {
                this.options.onFocus.call(this, event);
            }

            this.inputfield.addClass('ui-state-focus');
            this.container.addClass('ui-inputwrapper-focus');
        },

        onInputBlur: function (event) {
            if (this.options.onBlur) {
                this.options.onBlur.call(this, event);
            }

            this.inputfield.val(this.getValueToRender());

            this.inputfield.removeClass('ui-state-focus');
            this.container.removeClass('ui-inputwrapper-focus');
        },

        onInputKeyDown: function (event) {
            this.isKeydown = true;
            if (event.keyCode === 27) {
                //put the focus back to the inputfield
                this.inputfield.trigger('focus');
            }

            if (event.keyCode === 9 || event.keyCode === 27) {
                if (this.options.touchUI) {
                    this.disableModality();
                }

                this.hideOverlay();
            }
        },

        onUserInput: function (event) {
            // IE 11 Workaround for input placeholder
            if (!this.isKeydown) {
                return;
            }
            this.isKeydown = false;

            var rawValue = event.target.value;

            try {
                var value = this.parseValueFromString(rawValue);
                this.updateModel(event, value, false);
                this.updateViewDate(event, value.length ? value[0] : value);
            }
            catch (err) {
                if (!this.options.mask) {
                    this.updateModel(event, rawValue, false);
                }
            }

            if (this.options.onInput) {
                this.options.onInput.call(this, event);
            }
        },

        onButtonClick: function (event) {
            if (!this.panel.is(':visible')) {
                this.inputfield.trigger('focus');
                this.showOverlay();
            }
            else {
                this.hideOverlay();
            }

            this.datepickerClick = true;
        },

        onPanelClick: function (event) {
            if (this.documentClickListener) {
                this.datepickerClick = true;
            }
        },

        onMonthDropdownChange: function (event) {
            var newViewDate = new Date(this.viewDate.getTime());
            newViewDate.setMonth(parseInt(event.target.value, 10));

            if (this.options.onMonthChange) {
                this.options.onMonthChange.call(this, newViewDate.getMonth() + 1, newViewDate.getFullYear());
            }
            this.updateViewDate(event, newViewDate);
        },

        onYearDropdownChange: function (event) {
            var newViewDate = new Date(this.viewDate.getTime());
            newViewDate.setFullYear(parseInt(event.target.value, 10));

            if (this.options.onYearChange) {
                this.options.onYearChange.call(this, newViewDate.getMonth(), newViewDate.getFullYear());
            }
            this.updateViewDate(event, newViewDate);
        },

        onMonthSelect: function (event, month) {
            this.onDateSelect(event, { year: this.viewDate.getFullYear(), month: month, day: 1, selectable: true });
            event.preventDefault();
        },

        navBackward: function (event) {
            if (this.options.disabled) {
                event.preventDefault();
                event.stopPropagation();
                return;
            }

            var newViewDate = new Date(this.viewDate.getTime());

            if (this.options.view === 'date') {
                if (newViewDate.getMonth() === 0) {
                    newViewDate.setMonth(11, 1);
                    newViewDate.setFullYear(newViewDate.getFullYear() - 1);
                }
                else {
                    newViewDate.setMonth(newViewDate.getMonth() - 1, 1);
                }

                // #5967 check if month can be navigated to by checking last day in month
                var testDate = new Date(newViewDate.getTime()),
                    minDate = this.options.minDate;
                testDate.setMonth(testDate.getMonth()+1)
                testDate.setHours(-1);
                if (minDate && minDate > testDate) {
                    this.setNavigationState(newViewDate);
                    event.preventDefault();
                    event.stopPropagation();
                    return;
                }

                if (this.options.onMonthChange) {
                    this.options.onMonthChange.call(this, newViewDate.getMonth() + 1, newViewDate.getFullYear());
                }
            }
            else if (this.options.view === 'month') {
                var currentYear = newViewDate.getFullYear(),
                    newYear = currentYear - 1;

                if (this.options.yearNavigator) {
                    var minYear = parseInt(this.options.yearRange.split(':')[0], 10);

                    if (newYear < minYear) {
                        newYear = minYear;
                    }
                }

                newViewDate.setFullYear(newYear);

                if (this.options.onYearChange) {
                    this.options.onYearChange.call(this, newViewDate.getMonth(), newViewDate.getFullYear());
                }
            }

            this.updateViewDate(event, newViewDate);

            event.preventDefault();
            event.stopPropagation();
        },

        navForward: function (event) {
            if (this.options.disabled) {
                event.preventDefault();
                event.stopPropagation();
                return;
            }

            var newViewDate = new Date(this.viewDate.getTime());

            if (this.options.view === 'date') {
                if (newViewDate.getMonth() === 11) {
                    newViewDate.setMonth(0, 1);
                    newViewDate.setFullYear(newViewDate.getFullYear() + 1);
                }
                else {
                    newViewDate.setMonth(newViewDate.getMonth() + 1, 1);
                }

                // #5967 check if month can be navigated to by checking first day next month
                var maxDate = this.options.maxDate;
                if (maxDate && maxDate < newViewDate) {
                    this.setNavigationState(newViewDate);
                    event.preventDefault();
                    event.stopPropagation();
                    return;
                }

                if (this.options.onMonthChange) {
                    this.options.onMonthChange.call(this, newViewDate.getMonth() + 1, newViewDate.getFullYear());
                }
            }
            else if (this.options.view === 'month') {
                var currentYear = newViewDate.getFullYear(),
                    newYear = currentYear + 1;

                if (this.options.yearNavigator) {
                    var maxYear = parseInt(this.options.yearRange.split(':')[1], 10);

                    if (newYear > maxYear) {
                        newYear = maxYear;
                    }
                }

                newViewDate.setFullYear(newYear);

                if (this.options.onYearChange) {
                    this.options.onYearChange.call(this, newViewDate.getMonth(), newViewDate.getFullYear());
                }
            }

            this.updateViewDate(event, newViewDate);

            event.preventDefault();
            event.stopPropagation();
        },

        setNavigationState: function(newViewDate) {
            if (this.options.view !== 'date') {
                return;
            }

            var navPrev = this.panel.find('.ui-datepicker-header > .ui-datepicker-prev');
            var navNext = this.panel.find('.ui-datepicker-header > .ui-datepicker-next');

            if (this.options.disabled) {
                navPrev.addClass('ui-state-disabled');
                navNext.addClass('ui-state-disabled');
                return;
            }

            // previous
            var testDate = new Date(newViewDate.getTime()),
                minDate = this.options.minDate;
            testDate.setMonth(testDate.getMonth()+1)
            testDate.setHours(-1);
            if (minDate && minDate > testDate) {
                navPrev.addClass('ui-state-disabled');
            } else {
                navPrev.removeClass('ui-state-disabled');
            }

            // next
            var maxDate = this.options.maxDate;
            if (maxDate && maxDate < newViewDate) {
                navNext.addClass('ui-state-disabled');
            } else {
                navNext.removeClass('ui-state-disabled');
            }
        },

        onTimePickerElementMouseDown: function (event, type, direction) {
            if (!this.options.disabled && event.which === 1) {
                this.repeat(event, null, type, direction);
                event.preventDefault();
            }
        },

        onTimePickerElementMouseUp: function (event) {
            if (!this.options.disabled) {
                this.clearTimePickerTimer();

                if (this.options.onSelect) {
                    this.options.onSelect.call(this, event, this.value);
                }
            }
        },

        repeat: function (event, interval, type, direction) {
            var i = interval || 500,
                $this = this;

            this.clearTimePickerTimer();
            this.timePickerTimer = setTimeout(function () {
                $this.repeat(event, 100, type, direction);
            }, i);

            switch (type) {
                case 0:
                    if (direction === 1)
                        this.incrementHour(event);
                    else
                        this.decrementHour(event);
                    break;

                case 1:
                    if (direction === 1)
                        this.incrementMinute(event);
                    else
                        this.decrementMinute(event);
                    break;

                case 2:
                    if (direction === 1)
                        this.incrementSecond(event);
                    else
                        this.decrementSecond(event);
                    break;
            }
        },

        clearTimePickerTimer: function () {
            if (this.timePickerTimer) {
                clearTimeout(this.timePickerTimer);
                this.timePickerTimer = null;
            }
        },

        showOverlay: function () {
            if (this.options.onBeforeShow) {
                this.options.onBeforeShow.call(this);
            }

            this.panel.show();
            this.alignPanel();

            if (!this.options.touchUI) {
                var $this = this;
                setTimeout(function () {
                    $this.bindDocumentClickListener();
                    $this.bindWindowResizeListener();
                }, 10);
            }

            if ((this.options.showTime || this.options.timeOnly) && this.options.timeInput) {
                this.panel.find('.ui-hour-picker input').trigger('focus');
            }
        },

        hideOverlay: function () {
            if (this.isPanelVisible()) {
                if (this.options.onBeforeHide) {
                    this.options.onBeforeHide.call(this);
                }

                this.unbindDocumentClickListener();
                this.unbindWindowResizeListener();
                this.datepickerClick = false;

                this.panel.hide();

                var viewDate = this.options.viewDate && !this.value ?
                    this.parseValue(this.options.viewDate)
                    :
                    ((((this.isMultipleSelection() || this.isRangeSelection()) && this.value instanceof Array) ? this.value[0] : this.value) || this.parseValue(new Date()));

                if(viewDate instanceof Date) {
                    this.updateViewDate(null, viewDate);
                }
            }
        },

        bindDocumentClickListener: function () {
            var $this = this;
            if (!this.documentClickListener) {
                this.documentClickListener = function () {
                    if (!$this.datepickerClick) {
                        $this.hideOverlay();
                    }

                    $this.datepickerClick = false;
                };

                document.addEventListener('click', this.documentClickListener);
            }
        },

        unbindDocumentClickListener: function () {
            if (this.documentClickListener) {
                document.removeEventListener('click', this.documentClickListener);
                this.documentClickListener = null;
            }
        },

        bindWindowResizeListener: function () {
            if (this.options.inline) {
                return;
            }
            var $this = this;
            $(window).on('resize.' + this.options.id, function() {
                $this.alignPanel();
            });
        },

        unbindWindowResizeListener: function () {
            $(window).off('resize.'+ this.options.id);
        },

        isPanelVisible: function () {
           return this.panel && this.panel.is(":visible");
        },

        isDate: function (value) {
           return value && Object.prototype.toString.call(value) === "[object Date]" && !isNaN(value);
        },

        alignPanel: function () {
            if (!this.isPanelVisible()) {
               return;
            }

            if (this.options.touchUI) {
                this.enableModality();
            }
            else {
                if (this.options.appendTo) {
                    this.panel.css('min-width', this.container.outerWidth() + 'px');
                }

                if (this.panel.parent().is(this.container)) {
                    this.panel.css({
                        left: '0px',
                        top: String(this.container.innerHeight())
                    });
                }
                else {
                    this.panel.css({ left: '', top: '' }).position({
                        my: 'left top'
                        , at: 'left bottom'
                        , of: this.container
                        , collision: 'flipfit'
                    });
                }
            }
        },

        enableModality: function () {
            if (!this.mask) {
                this.mask = $('<div class="ui-widget-overlay ui-datepicker-mask ui-datepicker-mask-scrollblocker"></div>');
                this.mask.css('z-index', String(parseInt(this.panel.css('z-index'), 10) - 1));

                var $this = this;
                this.mask.on('click.datePicker-mask', function () {
                    $this.disableModality();
                });

                $(document.body).append(this.mask).addClass('ui-overflow-hidden');
            }
        },

        disableModality: function () {
            if (this.mask) {
                this.mask.off('click.datePicker-mask');
                this.mask.remove();
                this.mask = null;

                var bodyChildren = $(document.body).children('.ui-datepicker-mask-scrollblocker');

                if (!bodyChildren.length) {
                    $(document.body).removeClass('ui-overflow-hidden');
                }

                this.hideOverlay();
            }
        },

        onDateSelect: function (event, dateMeta) {
            if (this.options.disabled || !dateMeta.selectable) {
                if(event) {
                    event.preventDefault();
                }
                return;
            }

            var $this = this;

            if (this.isMultipleSelection()) {
                if (this.isSelected(dateMeta)) {
                    var value = this.value.filter(function (date, i) {
                        return !$this.isDateEquals(date, dateMeta);
                    });
                    this.updateModel(event, value);
                }
                else if (!this.options.maxDateCount || !this.value || this.options.maxDateCount > this.value.length) {
                    this.selectDate(event, dateMeta);
                }
            }
            else {
                this.selectDate(event, dateMeta);
            }

            if (!this.options.inline && this.isSingleSelection() && (!this.options.showTime || this.options.hideOnDateTimeSelect)) {
                //put the focus back to the inputfield
                this.inputfield.trigger('focus');

                setTimeout(function () {
                    $this.hideOverlay();
                }, 100);

                if (this.mask) {
                    this.disableModality();
                }
            }

            if(event) {
                event.preventDefault();
            }
        },

        selectDate: function (event, dateMeta) {
            var date = new Date(dateMeta.year, dateMeta.month, dateMeta.day);

            if (this.options.showTime) {
                var time = this.isDate(this.value) ? this.value : new Date();
                date.setHours(time.getHours());
                date.setMinutes(this.stepMinute(time.getMinutes()));
                date.setSeconds(time.getSeconds());
                date.setMilliseconds(0);
            }

            if (this.options.minDate && this.options.minDate > date) {
                date = this.options.minDate;
            }

            if (this.options.maxDate && this.options.maxDate < date) {
                date = this.options.maxDate;
            }

            if (this.isSingleSelection()) {
                this.updateModel(event, date);
            }
            else if (this.isMultipleSelection()) {
                this.updateModel(event, this.value ? this.value.concat(date) : [date]);
            }
            else if (this.isRangeSelection()) {
                if (this.value && this.value.length) {
                    var startDate = this.value[0],
                        endDate = this.value[1];

                    if (!endDate && date.getTime() >= startDate.getTime()) {
                        endDate = date;
                    }
                    else {
                        startDate = date;
                        endDate = null;
                    }

                    this.updateModel(event, [startDate, endDate]);
                }
                else {
                    this.updateModel(event, [date, null]);
                }
            }

            if (this.options.onSelect) {
                this.options.onSelect.call(this, event, date);
            }
        },

        incrementHour: function (event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                currentHour = currentTime.getHours(),
                newHour = currentHour + this.options.stepHour;
            newHour = (newHour >= 24) ? (newHour - 24) : newHour;

            if (this.validateTime(newHour, currentTime.getMinutes(), currentTime.getSeconds(), currentTime, "INCREMENT")) {
                this.updateTime(event, newHour, currentTime.getMinutes(), currentTime.getSeconds());
            }

            event.preventDefault();
        },

        decrementHour: function (event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                currentHour = currentTime.getHours(),
                newHour = currentHour - this.options.stepHour;
            newHour = (newHour < 0) ? (newHour + 24) : newHour;

            if (this.validateTime(newHour, currentTime.getMinutes(), currentTime.getSeconds(), currentTime, "DECREMENT")) {
                this.updateTime(event, newHour, currentTime.getMinutes(), currentTime.getSeconds());
            }

            event.preventDefault();
        },

        incrementMinute: function (event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                currentMinute = currentTime.getMinutes(),
                newMinute = this.stepMinute(currentMinute, this.options.stepMinute);
            newMinute = (newMinute > 59) ? (newMinute - 60) : newMinute;

            if (this.validateTime(currentTime.getHours(), newMinute, currentTime.getSeconds(), currentTime, "INCREMENT")) {
                this.updateTime(event, currentTime.getHours(), newMinute, currentTime.getSeconds());
            }

            event.preventDefault();
        },

        decrementMinute: function (event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                currentMinute = currentTime.getMinutes(),
                newMinute = this.stepMinute(currentMinute, -this.options.stepMinute);
            newMinute = (newMinute < 0) ? (newMinute + 60) : newMinute;

            if (this.validateTime(currentTime.getHours(), newMinute, currentTime.getSeconds(), currentTime, "DECREMENT")) {
                this.updateTime(event, currentTime.getHours(), newMinute, currentTime.getSeconds());
            }

            event.preventDefault();
        },

        stepMinute: function(currentMinute, step) {
            if (this.options.stepMinute <= 1) {
                if (!step) {
                    return currentMinute;
                } else {
                    return currentMinute + step;
                }
            }
            if (!step) {
                step = this.options.stepMinute;
                if (currentMinute % step === 0) {
                    return currentMinute;
                }
            }

            var newMinute = currentMinute + step;
            newMinute = Math.floor(newMinute / step) * step;
            return newMinute;
        },

        incrementSecond: function (event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                currentSecond = currentTime.getSeconds(),
                newSecond = currentSecond + this.options.stepSecond;
            newSecond = (newSecond > 59) ? (newSecond - 60) : newSecond;

            if (this.validateTime(currentTime.getHours(), currentTime.getMinutes(), newSecond, currentTime, "INCREMENT")) {
                this.updateTime(event, currentTime.getHours(), currentTime.getMinutes(), newSecond);
            }

            event.preventDefault();
        },

        decrementSecond: function (event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                currentSecond = currentTime.getSeconds(),
                newSecond = currentSecond - this.options.stepSecond;
            newSecond = (newSecond < 0) ? (newSecond + 60) : newSecond;

            if (this.validateTime(currentTime.getHours(), currentTime.getMinutes(), newSecond, currentTime, "DECREMENT")) {
                this.updateTime(event, currentTime.getHours(), currentTime.getMinutes(), newSecond);
            }

            event.preventDefault();
        },

        toggleAmPm: function (event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                currentHour = currentTime.getHours(),
                newHour = (currentHour >= 12) ? currentHour - 12 : currentHour + 12;

            this.updateTime(event, newHour, currentTime.getMinutes(), currentTime.getSeconds());
            event.preventDefault();
        },

        handleHoursInput: function(input, event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                value = input.value,
                valid = false,
                newHours;

            var reg = new RegExp('^([0-9]){1,2}$');
            if (reg.test(value)) {
                newHours = parseInt(value);
                if (this.options.hourFormat === '12') {
                    if (newHours >= 1 || newHours <= 12) {
                        valid = this.validateTime(newHours, currentTime.getMinutes(), currentTime.getSeconds(), currentTime);
                    }
                } else {
                    if (newHours >= 0 || newHours <= 23) {
                        valid = this.validateTime(newHours, currentTime.getMinutes(), currentTime.getSeconds(), currentTime);
                    }
                }
            }

            if (!valid) {
                event.preventDefault();
                input.value = this.oldHours;
                return;
            }

            var newDateTime = this.isDate(this.value) ? new Date(this.value) : new Date();
            newDateTime.setHours(newHours);

            this.updateTimeAfterInput(event, newDateTime);
        },

        handleMinutesInput: function(input, event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                value = input.value,
                valid = false,
                newMinutes;

            var reg = new RegExp('^([0-9]){1,2}$');
            if (reg.test(value)) {
                newMinutes = parseInt(value);
                if (newMinutes >= 0 || newMinutes <= 59) {
                    valid = this.validateTime(currentTime.getHours(), newMinutes, currentTime.getSeconds(), currentTime);
                }
            }

            if (!valid) {
                event.preventDefault();
                input.value = this.oldMinutes;
                return;
            }

            var newDateTime = this.isDate(this.value) ? new Date(this.value) : new Date();
            newDateTime.setMinutes(newMinutes);

            this.updateTimeAfterInput(event, newDateTime);
        },

        handleSecondsInput: function(input, event) {
            var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
                value = input.value,
                valid = false,
                newSeconds;

            var reg = new RegExp('^([0-9]){1,2}$');
            if (reg.test(value)) {
                newSeconds = parseInt(value);
                if (newSeconds >= 0 || newSeconds <= 59) {
                    valid = this.validateTime(currentTime.getHours(), currentTime.getMinutes(), newSeconds, currentTime);
                }
            }

            if (!valid) {
                event.preventDefault();
                input.value = this.oldSeconds;
                return;
            }

            var newDateTime = this.isDate(this.value) ? new Date(this.value) : new Date();
            newDateTime.setSeconds(newSeconds);

            this.updateTimeAfterInput(event, newDateTime);
        },

        validateTime: function(hour, minute, second, value, direction) {
            var valid = true;
            var dateNew = new Date(value.getFullYear(), value.getMonth(), value.getDate(), hour, minute, second, 0);

            if (this.options.minDate && value) {
                if (this.options.minDate > dateNew) {
                    if (direction === "INCREMENT" && this.options.minDate > value)  {
                        ; //the new time is still outside the allowed range, but we come nearer to it
                    }
                    else {
                        valid = false;
                    }
                }
            }

            if (this.options.maxDate && value) {
                if (this.options.maxDate < dateNew) {
                    if (direction === "DECREMENT" && this.options.maxDate < value) {
                        ; //the new time is still outside the allowed range, but we come nearer to it
                    }
                    else {
                        valid = false;
                    }
                }
            }

            return valid;
        },

        updateTime: function (event, hour, minute, second) {
            var newDateTime = this.isDate(this.value) ? new Date(this.value) : new Date();

            newDateTime.setHours(hour);
            newDateTime.setMinutes(minute);
            newDateTime.setSeconds(second);
            newDateTime.setMilliseconds(0);

            this.updateModel(event, newDateTime);

            if (this.options.onSelect) {
                if (this.timePickerTimer === 'undefined' || this.timePickerTimer === null) {
                    this.options.onSelect.call(this, event, newDateTime);
                }
            }
        },

        updateTimeAfterInput: function (event, newDateTime) {
            this.value = newDateTime;
            this.inputfield.val(this.getValueToRender());

            if (this.options.onSelect) {
                this.options.onSelect.call(this, event, newDateTime);
            }
        },

        onTodayButtonClick: function (event) {
            var today = new Date(),
                dateMeta = { day: today.getDate(), month: today.getMonth(), year: today.getFullYear(), today: true, selectable: true };

            this.updateViewDate(event, today);
            this.onDateSelect(event, dateMeta);

            if (this.options.onTodayButtonClick) {
                this.options.onTodayButtonClick.call(this, event);
            }
        },

        onClearButtonClick: function (event) {
            this.updateModel(event, null);

            if (this.options.onClearButtonClick) {
                this.options.onClearButtonClick.call(this, event);
            }
        },

        escapeHTML: function(value) {
            var entityMap = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#39;',
                '/': '&#x2F;',
                '`': '&#x60;',
                '=': '&#x3D;'
            };

            return String(value).replace(/[&<>"'`=\/]/g, function (s) {
                return entityMap[s];
            });
        },

        updateYearNavigator: function() {
            if (this.hasCustomYearRange) {
                return;
            }
            if (this.options.yearNavigator) {
                var viewYear = this.viewDate.getFullYear();
                this.options.yearRange = (viewYear - 10) + ':' + (viewYear + 10);
            }
        },

        updateViewDate: function (event, value) {
            if (this.options.onViewDateChange) {
                this.options.onViewDateChange.call(this, event, value);
            }

            this.viewDate = value;

            if (this.options.monthNavigator && this.options.view !== 'month') {
                var viewMonth = this.viewDate.getMonth();
                viewMonth = (this.isInMaxYear() && Math.min(this.options.maxDate.getMonth(), viewMonth)) || (this.isInMinYear() && Math.max(this.options.minDate.getMonth(), viewMonth)) || viewMonth;
                this.viewDate.setMonth(viewMonth);
            }

            this.panel.get(0).innerHTML = this.renderPanelElements();

            this._setInitOptionValues();
        },

        updateModel: function (event, value, updateInput) {
            this.value = (value === '' ? null : value);
            if (updateInput != false) {
                this.inputfield.val(this.getValueToRender());
            }

            this.panel.get(0).innerHTML = this.renderPanelElements();

            this._setInitOptionValues();
        }

    });

}));
