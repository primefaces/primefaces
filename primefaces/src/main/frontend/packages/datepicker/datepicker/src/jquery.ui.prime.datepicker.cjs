// ================================================================================
// NOTE: All the documentation and TypeScript declarations are in 0-datepicker.d.ts
// ================================================================================

/**
 * Prime DatePicker Widget
 */
$.widget("prime.datePicker", {

    options: {
        id: null,
        name: null,
        defaultDate: null,
        defaultHour: 0,
        defaultMinute: 0,
        defaultSecond: 0,
        defaultMillisecond: 0,
        viewDate: null,
        style: null,
        styleClass: null,
        inline: false,
        flex: false,
        selectionMode: 'single',
        rangeSeparator: '-',
        timeSeparator: ':',
        fractionSeparator: '.',
        inputId: null,
        inputStyle: null,
        inputStyleClass: null,
        required: false,
        readonly: false,
        readOnlyInput: false,
        disabled: false,
        valid: true,
        tabIndex: null,
        placeholder: null,
        showIcon: false,
        icon: 'ui-icon ui-icon-calendar',
        showOnFocus: true,
        focusOnSelect: false,
        keepInvalid: false,
        numberOfMonths: 1,
        view: 'date',
        touchUI: false,
        showWeek: false,
        weekCalculator: null,
        showTime: false,
        timeOnly: false,
        timeZone: null,
        showSeconds: false,
        showMilliseconds: false,
        hourFormat: '24',
        stepHour: 1,
        stepMinute: 1,
        stepSecond: 1,
        stepMillisecond: 1,
        shortYearCutoff: '+10',
        hideOnDateTimeSelect: false,
        hideOnRangeSelection: false,
        userLocale: null,
        locale: {
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
        },
        dateFormat: 'mm/dd/yy',
        yearRange: null,
        panelStyle: null,
        panelStyleClass: null,
        monthNavigator: false,
        yearNavigator: "false",
        dateStyleClasses: null,
        disabledDates: null,
        enabledDates: null,
        disabledDays: null,
        minDate: null,
        maxDate: null,
        maxDateCount: null,
        showMinMaxRange: true,
        showOtherMonths: false,
        showLongMonthNames: false,
        selectOtherMonths: false,
        autoMonthFormat: true,
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

    _create: function() {
        this.container = this.element;
        this.inputfield = this.element.children('input');
        this.inputfield.addClass('hasDatepicker'); // needed for ui-float-label

        this._setInitValues();
        this._render();
    },

    _setInitValues: function() {
        if (this.options.userLocale && typeof this.options.userLocale === 'object') {
            $.extend(this.options.locale, this.options.userLocale);
        }
        
        if (this.options.timeOnly) {
            this.options.showTime = true;
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
        var hasMultipleDates = (this.isMultipleSelection() || this.isRangeSelection()) && parsedDefaultDate instanceof Array;
        var viewDateDefaultsToNow = false;

        this.value = parsedDefaultDate;
        if (this.options.viewDate && !hasMultipleDates) {
            this.viewDate = this.parseValue(this.options.viewDate);
            if (!this.viewDate) {
                this.viewDate = this.getNow();
            }
        }
        else {
            if (hasMultipleDates) {
                this.viewDate = parsedDefaultDate[0];
            }
            else {
                this.viewDate = parsedDefaultDate;
            }
            if (this.viewDate === null) {
                this.viewDate = this.getNow();
                this.viewDate.setHours(this.options.defaultHour);
                this.viewDate.setMinutes(this.options.defaultMinute);
                if (!this.options.showSeconds && !this.options.showMilliseconds) {
                    this.viewDate.setSeconds(0);
                }
                else {
                    this.viewDate.setSeconds(this.options.defaultSecond);
                }
                if (!this.options.showMilliseconds) {
                    this.viewDate.setMilliseconds(0);
                }
                else {
                    this.viewDate.setMilliseconds(this.options.defaultMillisecond);
                }
                viewDateDefaultsToNow = true;
            }
        }

        // #6047 round to nearest stepMinute on even if editing using keyboard
        this.viewDate = this.isDate(this.viewDate) ? new Date(this.viewDate) : this.getNow();
        this.viewDate.setMinutes(this.stepMinute(this.viewDate.getMinutes()));

        this.options.minDate = this.parseMinMaxValue(this.options.minDate);
        this.options.maxDate = this.parseMinMaxValue(this.options.maxDate);
        this.ticksTo1970 = (((1970 - 1) * 365 + Math.floor(1970 / 4) - Math.floor(1970 / 100) + Math.floor(1970 / 400)) * 24 * 60 * 60 * 10000000);

        if (viewDateDefaultsToNow) {
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

        if (!this.options.viewDate) {
            this.options.viewDate = this.viewDate;
        }

        this.hasCustomYearRange = this.options.yearRange !== null;
        this.updateYearNavigator();

        if (this.options.disabledDates) {
            for (var i = 0; i < this.options.disabledDates.length; i++) {
                this.options.disabledDates[i] = this.parseOptionValue(this.options.disabledDates[i]);
            }
        }
        if (this.options.enabledDates && this.options.enabledDates.length > 0) {
            for (var i = 0; i < this.options.enabledDates.length; i++) {
                this.options.enabledDates[i] = this.parseOptionValue(this.options.enabledDates[i]);
            }
        }
        this.bindResponsiveResizeListener();
        // #13634 ensure input is formatted correctly after AJAX update
        let inputValue = this.getValueToRender();
        if (!inputValue && this.options.keepInvalid && this.options.defaultDate) {
            inputValue = this.options.defaultDate;
        }
        this.inputfield.val(inputValue);
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

    getFirstDayOfMonthIndex: function(month, year) {
        var day = this.getNow();
        day.setDate(1);
        day.setMonth(month);
        day.setFullYear(year);

        var dayIndex = day.getDay() + this.getSundayIndex();
        return dayIndex >= 7 ? dayIndex - 7 : dayIndex;
    },

    getFirstDayOfWeek: function() {
        return this.options.locale.firstDayOfWeek;
    },

    getSundayIndex: function() {
        var firstDayOfWeek = this.getFirstDayOfWeek();
        return firstDayOfWeek > 0 ? 7 - firstDayOfWeek : 0;
    },

    getSaturdayIndex: function() {
        return 7 - this.getFirstDayOfWeek() - 1;
    },

    getDaysCountInMonth: function(month, year) {
        return 32 - this.daylightSavingAdjust(new Date(year, month, 32)).getDate();
    },

    getDaysCountInPrevMonth: function(month, year) {
        var prev = this.getPreviousMonthAndYear(month, year);
        return this.getDaysCountInMonth(prev.month, prev.year);
    },

    daylightSavingAdjust: function(date) {
        if (!date) {
            return null;
        }

        date.setHours(date.getHours() > 12 ? date.getHours() + 2 : 0);

        return date;
    },

    getPreviousMonthAndYear: function(month, year) {
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

    getNextMonthAndYear: function(month, year) {
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

    createWeekDaysInternal: function(dayNames) {
        var weekDays = [],
            dayIndex = this.getFirstDayOfWeek();
        for (var i = 0; i < 7; i++) {
            weekDays.push(dayNames[dayIndex]);
            dayIndex = (dayIndex === 6) ? 0 : ++dayIndex;
        }

        return weekDays;
    },

    createWeekDaysMin: function() {
        return this.createWeekDaysInternal(this.options.locale.dayNamesMin);
    },

    createWeekDaysShort: function() {
        return this.createWeekDaysInternal(this.options.locale.dayNamesShort);
    },

    createWeekDays: function() {
        return this.createWeekDaysInternal(this.options.locale.dayNames);
    },

    createMonths: function(month, year) {
        var months = [];
        for (var i = 0; i < this.options.numberOfMonths; i++) {
            var m = month + i,
                y = year;
            if (m > 11) {
                y = year + Math.floor(m / 12);
                m = m % 12;
            }

            months.push(this.createMonth(m, y, i));
        }

        return months;
    },

    createMonth: function(month, year, index) {
        var dates = [];
        firstDay = this.getFirstDayOfMonthIndex(month, year);
        daysLength = this.getDaysCountInMonth(month, year);
        prevMonthDaysLength = this.getDaysCountInPrevMonth(month, year);
        dayNo = 1;
        today = this.getNow();
        monthRows = Math.ceil((daysLength + firstDay) / 7);

        for (var i = 0; i < monthRows; i++) {
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

    isSelectable: function(day, month, year, otherMonth) {
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

        if (this.options.enabledDates) {
            validDate = this.isDateEnabled(day, month, year);
        }

        if (this.options.disabledDays) {
            validDay = !this.isDayDisabled(day, month, year);
        }

        if (this.options.selectOtherMonths === false && otherMonth) {
            validMonth = false;
        }

        return validMin && validMax && validDate && validDay && validMonth;
    },

    isSelected: function(dateMeta) {
        if (this.value) {
            if (this.options.view === 'week') {
                var currentDate = this.value[0];
                var currentDateMeta = { day: currentDate.getDate(), month: currentDate.getMonth(), year: currentDate.getFullYear() };
                var w1 = this.options.weekCalculator(currentDateMeta);
                var w2 = this.options.weekCalculator(dateMeta);
                return w1 == w2;
            }

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
                    return this.isDateEquals(this.value[0], dateMeta);
            }
        }
        else {
            return false;
        }
    },

    isMonthSelected: function(month) {
        if (this.value) {
            if (this.isRangeSelection()) {
                var dateMeta = { year: this.viewDate.getFullYear(), month: month, day: 1, selectable: true };

                if (this.value[1])
                    return this.isDateEquals(this.value[0], dateMeta) || this.isDateEquals(this.value[1], dateMeta) || this.isDateBetween(this.value[0], this.value[1], dateMeta);
                else
                    return this.isDateEquals(this.value[0], dateMeta);
            }
            else {
                return this.isDate(this.value) && this.value.getMonth() === month && this.value.getFullYear() === this.viewDate.getFullYear();
            }
        }

        return false;
    },

    isDateEquals: function(value, dateMeta) {
        if (this.isDate(value))
            return value.getDate() === dateMeta.day && value.getMonth() === dateMeta.month && value.getFullYear() === dateMeta.year;
        else
            return false;
    },

    isDateBetween: function(start, end, dateMeta) {
        var between = false;
        if (this.isDate(start) && this.isDate(end)) {
            var date = new Date(dateMeta.year, dateMeta.month, dateMeta.day);
            return start.getTime() <= date.getTime() && end.getTime() >= date.getTime();
        }

        return between;
    },

    isSingleSelection: function() {
        return this.options.selectionMode === 'single';
    },

    isRangeSelection: function() {
        return this.options.selectionMode === 'range';
    },

    isMultipleSelection: function() {
        return this.options.selectionMode === 'multiple';
    },

    isToday: function(today, day, month, year) {
        return today.getDate() === day && today.getMonth() === month && today.getFullYear() === year;
    },

    isDateDisabled: function(day, month, year) {
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

    isDateEnabled: function(day, month, year) {
        if (this.options.enabledDates && this.options.enabledDates.length > 0) {
            for (var i = 0; i < this.options.enabledDates.length; i++) {
                var enabledDate = this.options.enabledDates[i];
                if (enabledDate.getFullYear() === year && enabledDate.getMonth() === month && enabledDate.getDate() === day) {
                    return true;
                }
            }
            return false;
        }

        return true;
    },

    isDayDisabled: function(day, month, year) {
        if (this.options.disabledDays) {
            var weekday = new Date(year, month, day),
                weekdayNumber = weekday.getDay();
            return this.options.disabledDays.indexOf(weekdayNumber) !== -1;
        }
        return false;
    },

    getValueToRender: function() {
        var formattedValue = '';

        if (this.value) {
            try {
                if (this.isRangeSelection()) {
                    if (this.value && this.value.length) {
                        var startDate = this.value[0],
                            endDate = this.value[1];

                        formattedValue = this.formatDateTime(startDate);
                        if (endDate) {
                            formattedValue += ' ' + this.options.rangeSeparator + ' ' + this.formatDateTime(endDate);
                        }

                        if (this.options.view === 'week' && this.options.showWeek) {
                            var startDateMeta = { day: startDate.getDate(), month: startDate.getMonth(), year: startDate.getFullYear() };
                            var week = this.options.weekCalculator(startDateMeta);
                            formattedValue += ' (' + this.options.locale.weekHeader + ' ' + week + ')';
                        }
                    }
                }
                else if (this.isSingleSelection()) {
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
            }
            catch (err) {
                formattedValue = this.value;
            }
        }

        return formattedValue;
    },

    formatDateTime: function(date) {
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
    formatDate: function(date, format) {
        if (!date) {
            return '';
        }

        var iFormat,
            lookAhead = function(match) {
                var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) === match);
                if (matches) {
                    iFormat++;
                }
                return matches;
            },
            formatNumber = function(match, value, len) {
                var num = '' + value;
                if (lookAhead(match)) {
                    while (num.length < len) {
                        num = '0' + num;
                    }
                }
                return num;
            },
            formatName = function(match, value, shortNames, longNames) {
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
                            var day = date.hasOwnProperty('day') ? date.day : date.getDate();
                            output += formatNumber('d', day, 2);
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
                            var month = date.hasOwnProperty('month') ? date.month : date.getMonth();
                            output += formatNumber('m', month + 1, 2);
                            break;
                        case 'M':
                            var month = date.hasOwnProperty('month') ? date.month : date.getMonth();
                            output += formatName('M', month, this.options.locale.monthNamesShort, this.options.locale.monthNames);
                            break;
                        case 'y':
                            var year = date.hasOwnProperty('year') ? date.year : date.getFullYear();
                            output += lookAhead('y') ? year : (year % 100 < 10 ? '0' : '') + (year % 100);
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

    formatTime: function(date) {
        if (!date) {
            return '';
        }

        var output = '',
            hours = date.getHours(),
            minutes = date.getMinutes(),
            seconds = date.getSeconds(),
            milliseconds = date.getMilliseconds();

        if (this.options.hourFormat === '12' && hours > 11 && hours !== 12) {
            hours -= 12;
        }

        if (this.options.hourFormat === '12') {
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

        if (this.options.showMilliseconds) {
            output += this.options.fractionSeparator;
            output += (milliseconds < 10) ? '00' + milliseconds : (milliseconds < 100) ? '0' + milliseconds : milliseconds;
        }

        if (this.options.hourFormat === '12') {
            output += date.getHours() > 11 ? ' ' + this.options.locale.pm : ' ' + this.options.locale.am;
        }

        return output;
    },

    parseTime: function(value, ampm) {
        var val = value.replace(this.options.fractionSeparator, this.options.timeSeparator),
            tokens = val.split(this.options.timeSeparator),
            showSeconds = this.options.showSeconds || this.options.showMilliseconds,
            validTokenLength = 2 + (showSeconds ? 1 : 0) + (this.options.showMilliseconds ? 1 : 0);

        if (tokens.length !== validTokenLength) {
            throw "Invalid time";
        }

        var h = parseInt(tokens[0]),
            m = parseInt(tokens[1]),
            s = showSeconds ? parseInt(tokens[2]) : null,
            ms = this.options.showMilliseconds ? parseInt(tokens[3]) : null;

        if (isNaN(h) || isNaN(m) || h > 23 || m > 59 || (this.options.hourFormat === '12' && h > 12) || (this.options.showSeconds && (isNaN(s) || s > 59)) || (this.options.showMilliseconds && (isNaN(ms) || ms > 999))) {
            throw "Invalid time";
        }
        else {
            if (this.options.hourFormat === '12' && h !== 12 && ampm === this.options.locale.pm) {
                h += 12;
            } else if (this.options.hourFormat === '12' && h === 12 && ampm === this.options.locale.am) {
                h -= 12;
            }

            return { hour: h, minute: m, second: s, millisecond: ms };
        }
    },

    // Ported from jquery-ui datepicker parseDate
    parseDate: function(value, format) {
        if (format == null || value == null) {
            throw "Invalid arguments";
        }

        value = (typeof value === "object" ? value.toString() : value + "");
        if (value === "") {
            return null;
        }

        var iFormat, dim, extra,
            iValue = 0,
            shortYearCutoff = (typeof this.options.shortYearCutoff !== "string" ? this.options.shortYearCutoff : this.getNow().getFullYear() % 100 + parseInt(this.options.shortYearCutoff, 10)),
            year = -1,
            month = -1,
            day = -1,
            doy = -1,
            literal = false,
            date,
            lookAhead = function(match) {
                var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) === match);
                if (matches) {
                    iFormat++;
                }
                return matches;
            },
            getNumber = function(match) {
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
            getName = function(match, shortNames, longNames) {
                var index = -1,
                    arr = lookAhead(match) ? longNames : shortNames,
                    names = [];

                for (var i = 0; i < arr.length; i++) {
                    names.push([i, arr[i]]);
                }
                names.sort(function(a, b) {
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
            year = this.getNow().getFullYear();
        } else if (year < 100) {
            year += this.getNow().getFullYear() - this.getNow().getFullYear() % 100 +
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

    parseValueFromString: function(text) {
        if (!text || text.trim().length === 0) {
            return null;
        }

        var value;
        try {
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
        } catch (error) {
            PrimeFaces.error("DatePicker Error: " + error);

        }

        return value;
    },

    parseDateTime: function(text) {
        var date,
            parts = text.split(' ');

        if (this.options.timeOnly) {
            date = this.getNow();
            this.populateTime(date, parts[0], parts[1]);
        }
        else {
            if (this.options.showTime) {
                var ampm = this.options.hourFormat === '12' ? parts.pop() : null;
                var timeString = parts.pop();

                // #9559 some locales are "a. m." with a space 
                if (/\d/.test(timeString) === false) {
                    ampm = timeString + ' ' + ampm;
                    timeString = parts.pop();
                }

                date = this.parseDate(parts.join(' '), this.options.dateFormat);
                this.populateTime(date, timeString, ampm);
            }
            else {
                date = this.parseDate(text, this.options.dateFormat);
            }
        }

        return date;
    },

    populateTime: function(value, timeString, ampm) {
        if (this.options.hourFormat === '12' && (ampm !== this.options.locale.pm && ampm !== this.options.locale.am)) {
            throw new Error('Invalid Time');
        }

        var time = this.parseTime(timeString, ampm);
        value.setHours(time.hour);
        value.setMinutes(this.stepMinute(time.minute));
        if (this.options.showSeconds || this.options.showMilliseconds) {
            value.setSeconds(time.second);
        }
        else {
            value.setSeconds(0);
        }
        if (this.options.showMilliseconds) {
            value.setMilliseconds(time.millisecond);
        }
        else {
            value.setMilliseconds(0);
        }
    },

    isInMinYear: function() {
        return this.options.minDate && this.options.minDate.getFullYear() === this.viewDate.getFullYear();
    },

    isInMaxYear: function() {
        return this.options.maxDate && this.options.maxDate.getFullYear() === this.viewDate.getFullYear();
    },

    _destroy: function() {
        this.hideOverlay();
        this.unbindResponsiveResizeListener();
        PrimeFaces.utils.cleanseDomElement(this.panel);
    },

    /**
     * @override
     * @protected
     */
    _render: function() {
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
        this._bindPanelEvents();

        this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');
    },

    _setInitOptionValues: function() {
        if (this.isYearNavigator()) {
            var year = this.viewDate.getFullYear();
            var month = this.viewDate.getMonth();
            var yearElts = this.panel.find('.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-year');

            yearElts.each(function(index, yearElt) {
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

            monthElts.each(function(index, monthElt) {
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

    renderTriggerButton: function() {
        var panelId = this.container.attr('id') + '_panel';
        var aria = ' aria-haspopup="dialog" aria-expanded="false" aria-controls="' + panelId + '" ';
        this.triggerButton = $('<button type="button" ' + aria + ' class="ui-datepicker-trigger ui-button ui-widget ui-state-default ui-button-icon-only' + (this.options.disabled ? ' ui-state-disabled' : '') + '" tabindex="0">' +
            '<span class="ui-button-icon-left ' + this.options.icon + '"></span>' +
            '<span class="ui-button-text">ui-button</span>' +
            '</button>');
    },

    renderDatePickerPanel: function() {
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
        var _aria = ' role="dialog" aria-modal="true" aria-label="' + this.options.locale.chooseDate + '" ';
        this.panel = $('<div id="' + panelId + '"' + _aria + ' class="ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ' + _classes + '"></div>');

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

    renderPanelElements: function() {
        var elementsHtml = '';

        if (this.options.disabled) {
            this.panel.addClass('ui-state-disabled');
        }
        else {
            this.panel.removeClass('ui-state-disabled');
        }

        if (!this.options.timeOnly) {
            if (this.options.view === 'date' || this.options.view === 'week') {
                elementsHtml += this.renderDateView();
            }
            else if (this.options.view === 'month') {
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

    renderDateView: function() {
        this.monthsMetadata = this.createMonths(this.viewDate.getMonth(), this.viewDate.getFullYear());
        var months = this.renderMonths(this.monthsMetadata);

        return months;
    },

    renderMonthView: function() {
        var backwardNavigator = this.renderBackwardNavigator(this.options.locale.prevYear),
            forwardNavigator = this.renderForwardNavigator(this.options.locale.nextYear),
            yearElement = this.renderTitleYearElement(this.viewDate.getFullYear(), 0),
            months = this.renderMonthViewMonths();

        return ('<div class="ui-datepicker-header ui-widget-header ui-helper-clearfix">' +
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

    renderTimePicker: function() {
        var timepicker = '<div class="ui-timepicker ui-widget-header' + (this.options.timeInput ? ' ui-timepicker-timeinput' : '') + '">';

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
        //separator
        timepicker += this.options.showMilliseconds ? this.renderFractionSeparator() : '';
        //millisecond
        timepicker += this.renderMillisecondPicker();
        //ampm
        timepicker += this.renderAmPmPicker();

        //end
        timepicker += '</div>';

        return timepicker;
    },

    renderButtonBar: function() {
        var grid = this.options.flex ? 'grid' : 'ui-g';
        var today = this.options.flex ? 'col-6 text-left' : 'ui-g-6';
        var clear = this.options.flex ? 'col-6 text-right' : 'ui-g-6';
        var todayLabel =  this.options.locale.today;
        var now = this.getNow();
        var minDate = this.options.minDate;
        var maxDate = this.options.maxDate;
        var todayStyleClass = 'ui-today-button ui-button ui-widget ui-state-default ui-button-text-only ' + this.options.todayButtonStyleClass;

        if (this.options.showTime){
           todayLabel = this.options.locale.now;
        }
        else {
            // only use date at 00:00 for comparison
            now = this.truncateDate(now);
        }
        if ((minDate && minDate > now) || (maxDate && maxDate < now)) {
            todayStyleClass += ' ui-helper-hidden';
        }
        return '<div class="ui-datepicker-buttonbar ui-widget-header">' +
            '<div class="' + grid + '">' +
            '<div class="' + today + '">' +
            '<button type="button" class="' + todayStyleClass + '"><span class="ui-button-text">' + todayLabel + '</span></button>' +
            '</div>' +
            '<div class="' + clear + '">' +
            '<button type="button" class="ui-clear-button ui-button ui-widget ui-state-default ui-button-text-only ' + this.options.clearButtonStyleClass + '"><span class="ui-button-text">' + this.options.locale.clear + '</span></button>' +
            '</div>' +
            '</div>' +
            '</div>';
    },

    renderMonthViewMonth: function(index) {
        var monthName = this.options.showLongMonthNames ? this.options.locale.monthNames[index] : this.options.locale.monthNamesShort[index],
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

        return '<a tabindex="0" class="ui-monthpicker-month' + monthClass + '" aria-disabled="' + disabled + '">' + content + '</a>';
    },

    renderMonthViewMonths: function() {
        var months = '';
        for (var i = 0; i <= 11; i++) {
            months += this.renderMonthViewMonth(i);
        }

        return months;
    },

    renderMonths: function(monthsMetadata) {
        var monthsHtml = '';

        for (var i = 0; i < monthsMetadata.length; i++) {
            monthsHtml += this.renderMonth(monthsMetadata[i], i);
        }

        return monthsHtml;
    },

    renderMonth: function(monthMetadata, index) {
        var weekDaysMin = this.createWeekDaysMin(),
            weekDays = this.createWeekDays(),
            backwardNavigator = (index === 0) ? this.renderBackwardNavigator(this.options.locale.prevMonth) : '',
            forwardNavigator = (this.options.numberOfMonths === 1) || (index === this.options.numberOfMonths - 1) ? this.renderForwardNavigator(this.options.locale.nextMonth) : '',
            title = this.renderTitle(monthMetadata),
            dateViewGrid = this.renderDateViewGrid(monthMetadata, weekDaysMin, weekDays);

        return ('<div class="ui-datepicker-group ui-widget-content">' +
            '<div class="ui-datepicker-header ui-widget-header ui-helper-clearfix">' +
            backwardNavigator +
            forwardNavigator +
            title +
            '</div>' +
            dateViewGrid +
            '</div>');
    },

    renderBackwardNavigator: function(ariaLabel) {
        return '<button type="button" aria-label="' + ariaLabel + '" class="ui-datepicker-prev" tabindex="0">' +
            '<span class="ui-icon ui-icon-circle-triangle-w"></span>' +
            '</button>';
    },

    renderForwardNavigator: function(ariaLabel) {
        return '<button type="button" aria-label="' + ariaLabel + '" class="ui-datepicker-next" tabindex="0">' +
            '<span class="ui-icon ui-icon-circle-triangle-e"></span>' +
            '</button>';
    },

    renderTitleMonthElement: function(month, index) {
        if (this.options.monthNavigator && this.options.view !== 'month' && index === 0) {
            const monthNames = this.options.showLongMonthNames ? this.options.locale.monthNames : this.options.locale.monthNamesShort;
            return '<select class="ui-datepicker-month" tabindex="0" aria-label="' + this.options.locale.month + '">' + this.renderTitleOptions('month', monthNames, month) + '</select>';
        }
        else {
            return '<span class="ui-datepicker-month">' + this.escapeHTML(this.options.locale.monthNames[month]) + '</span>';
        }
    },

    renderTitleYearElement: function(year, index) {
        if (this.isYearNavigator() && index === 0) {
            this.updateYearNavigator();
            var years = this.options.yearRange.split(':'),
                yearStart = parseInt(years[0], 10),
                yearEnd = parseInt(years[1], 10),
                minDate = this.options.minDate,
                maxDate = this.options.maxDate,
                minYear = yearStart,
                maxYear = yearEnd;
            if (minDate) {
                minYear = Math.max(minDate.getFullYear(), yearStart);
            }
            if (maxDate) {
                maxYear = Math.min(maxDate.getFullYear(), yearEnd);
            }

            if (this.isYearNavigatorInput()) {
                return '<input class="ui-datepicker-year" size="6" maxlength="4" tabindex="0" aria-label="' + this.options.locale.year + '" type="number" min="' + minYear + '" max="' + maxYear + '" step="1" value="' + year + '"' + '></input>';
            }
            else {
                var yearOptions = [];
                for (var i = yearStart; i <= yearEnd; i++) {
                    yearOptions.push(i);
                }
                return '<select class="ui-datepicker-year" tabindex="0" aria-label="' + this.options.locale.year + '">' + this.renderTitleOptions('year', yearOptions, year) + '</select>';
            }
        }
        else {
            return '<span class="ui-datepicker-year">' + year + '</span>';
        }
    },

    renderTitleOptions: function(name, options, current) {
        var _options = '',
            minDate = this.options.minDate,
            maxDate = this.options.maxDate;

        for (var i = 0; i < options.length; i++) {
            switch (name) {
                case 'month':
                    if (!this.options.showMinMaxRange || (!this.isInMinYear() || i >= minDate.getMonth()) && (!this.isInMaxYear() || i <= maxDate.getMonth())) {
                        _options += '<option value="' + i + '"' + (i === current ? ' selected' : '') + '>' + this.escapeHTML(options[i]) + '</option>';
                    }
                    break;
                case 'year':
                    var option = options[i];
                    if (!this.options.showMinMaxRange || (!(minDate && minDate.getFullYear() > option) && !(maxDate && maxDate.getFullYear() < option))) {
                        _options += '<option value="' + option + '"' + (option === current ? ' selected' : '') + '>' + option + '</option>';
                    }
                    break;
            }
        }

        return _options;
    },

    renderTitle: function(monthMetadata) {
        var month = this.renderTitleMonthElement(monthMetadata.month, monthMetadata.index),
            year = this.renderTitleYearElement(monthMetadata.year, monthMetadata.index),
            whitespace = '&#xa0;';

        var content = month + whitespace + year;
        if (this.options.locale.showMonthAfterYear) {
            content = year + whitespace + month;
        }

        return '<div class="ui-datepicker-title">' + content + '</div>';
    },

    renderDayNames: function(weekDaysMin, weekDays) {
        var dayNamesHtml = '';

        if (this.options.showWeek) {
            dayNamesHtml += '<th scope="col" abbr="' + this.options.locale.weekHeader + '">' +
                '<span>' +
                this.options.locale.weekHeader +
                '</span>' +
                '</th>';
        }

        for (var i = 0; i < weekDaysMin.length; i++) {
            var weekDayLabel = this.escapeHTML(weekDays[i]);
            var weekDayMinLabel = weekDaysMin[i];
            dayNamesHtml += '<th scope="col" abbr="' + weekDayMinLabel + '">' +
                '<span title="' + weekDayLabel + '">' +
                weekDayMinLabel +
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
        return Math.round((new Date(d.year, d.month, d.day).getTime() - new Date(d.year, 0, 0).getTime()) / 86400000);
    },

    daysInYear: function(year) {
        if ((year % 4 === 0 && year % 100 !== 0) || year % 400 === 0) {
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
        var firstDayOfWeek = this.getFirstDayOfWeek(),
            doy = this.options.locale.firstDayWeekOffset,
            weekOffset = this.firstWeekOffset(d.year, firstDayOfWeek, doy),
            week = Math.floor((this.dayOfYear(d) - weekOffset - 1) / 7) + 1;

        if (week < 1) {
            return week + this.weeksInYear(resYear, firstDayOfWeek, doy);
        }
        else if (week > this.weeksInYear(d.year, firstDayOfWeek, doy)) {
            return week - this.weeksInYear(d.year, firstDayOfWeek, doy);
        }
        else {
            return week;
        }
    },

    renderWeek: function(weekDates) {
        var weekHtml = '';

        if (this.options.showWeek) {
            var firstDate = weekDates[0],
                lastDate = weekDates[6],
                cellClass = firstDate.otherMonth && lastDate.otherMonth && !this.options.showOtherMonths ? ' ui-datepicker-other-month-hidden' : '';

            weekHtml += '<td class="ui-datepicker-weeknumber' + cellClass + '"><span class="ui-state-disabled">' +
                this.options.weekCalculator(firstDate) +
                '</span></td>';
        }

        var saturdayIndex = this.getSaturdayIndex();
        var sundayIndex = this.getSundayIndex();
        for (var i = 0; i < weekDates.length; i++) {
            var date = weekDates[i],
                disabled = !weekDates[i].selectable,
                selected = this.isSelected(date),
                cellClass = this.getClassesToAdd({
                    'ui-datepicker-other-month': date.otherMonth,
                    'ui-datepicker-today': date.today,
                    'ui-datepicker-week-end': i == sundayIndex || i == saturdayIndex,
                    'ui-datepicker-other-month-hidden': date.otherMonth && !this.options.showOtherMonths
                }).trim(),
                dateClass = this.getClassesToAdd({
                    'ui-state-default': true,
                    'ui-state-active': selected,
                    'ui-state-disabled': disabled,
                    'ui-state-highlight': date.today
                }).trim(),
                content = this.renderDateCellContent(date, dateClass);

            weekHtml += (
                '<td role="gridcell"' +
                (cellClass ? ' class="' + cellClass + '"' : '') + 
                (disabled ? ' aria-disabled="true"' : '') + 
                (selected ? ' aria-selected="true"' : '') + 
                ' aria-label="' + this.options.locale.monthNames[date.month] + ' ' + date.day + '">' +
                content +
                '</td>'
            );
        }

        return weekHtml;
    },

    renderDateCellContent: function(date, dateClass) {
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

    renderDates: function(monthMetadata) {
        var datesHtml = '';

        for (var i = 0; i < monthMetadata.dates.length; i++) {
            var week = monthMetadata.dates[i];

            datesHtml += '<tr>' +
                this.renderWeek(week) +
                '</tr>';
        }

        return datesHtml;
    },

    renderDateViewGrid: function(monthMetadata, weekDaysMin, weekDays) {
        var dayNames = this.renderDayNames(weekDaysMin, weekDays),
            dates = this.renderDates(monthMetadata);

        return (
            '<div class="ui-datepicker-calendar-container">' +
            '<table class="ui-datepicker-calendar" role="grid">' +
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

    renderHourPicker: function() {
        var hour = this.isDate(this.value) ? this.value.getHours() : this.viewDate.getHours();
        var minHour = 0;
        var maxHour = 23;

        if (this.options.hourFormat === '12') {
            if (hour === 0) {
                hour = 12;
                maxHour = 12;
            }
            else if (hour > 11 && hour !== 12)
                hour = hour - 12;
        }

        var hourDisplay = hour < 10 ? '0' + hour : hour;
        var tabindex = '0';
        var html = this.options.timeInput
            ? '<input type="number" min="' + minHour + '" max="' + maxHour + '" value="' + hourDisplay + '" aria-label="' + this.options.locale.hourText + '" size="2" maxlength="2" tabindex="' + tabindex + '" class="ui-inputfield"></input>'
            : '<span>' + hourDisplay + '</span>';
        return this.renderTimeElements("ui-hour-picker", html, 0);
    },

    renderMinutePicker: function() {
        var minute = this.isDate(this.value) ? this.value.getMinutes() : this.viewDate.getMinutes(),
            minuteDisplay = minute < 10 ? '0' + minute : minute;
        var tabindex = '0';
        var html = this.options.timeInput
            ? '<input type="number" min="0" max="59" value="' + minuteDisplay + '" aria-label="' + this.options.locale.minuteText + '" size="2" maxlength="2" tabindex="' + tabindex + '" class="ui-inputfield"></input>'
            : '<span>' + minuteDisplay + '</span>';
        return this.renderTimeElements("ui-minute-picker", html, 1);
    },

    renderSecondPicker: function() {
        if (this.options.showSeconds) {
            var second = this.isDate(this.value) ? this.value.getSeconds() : this.viewDate.getSeconds(),
                secondDisplay = second < 10 ? '0' + second : second;
            var tabindex = '0';
            var html = this.options.timeInput
                ? '<input type="number" min="0" max="59" value="' + secondDisplay + '" aria-label="' + this.options.locale.secondText + '" size="2" maxlength="2" tabindex="' + tabindex + '" class="ui-inputfield"></input>'
                : '<span>' + secondDisplay + '</span>';
            return this.renderTimeElements("ui-second-picker", html, 2);
        }

        return '';
    },

    renderMillisecondPicker: function() {
        if (this.options.showMilliseconds) {
            var millisecond = this.isDate(this.value) ? this.value.getMilliseconds() : this.viewDate.getMilliseconds(),
                millisecondDisplay = millisecond < 10 ? '00' + millisecond : millisecond < 100 ? '0' + millisecond : millisecond;
            var tabindex = '0';
            var html = this.options.timeInput
                ? '<input type="number" min="0" max="999" value="' + millisecondDisplay + '" aria-label="' + this.options.locale.millisecondText + '" size="3" maxlength="3" tabindex="' + tabindex + '" class="ui-inputfield"></input>' : '<span>' + millisecondDisplay + '</span>';
            return this.renderTimeElements("ui-millisecond-picker", html, 3);
        }

        return '';
    },

    renderAmPmPicker: function() {
        if (this.options.hourFormat === '12') {
            var hour = this.isDate(this.value) ? this.value.getHours() : this.viewDate.getHours(),
                display = hour > 11 ? this.options.locale.pm : this.options.locale.am;

            return this.renderTimeElements("ui-ampm-picker", '<span>' + display + '</span>', 4);
        }

        return '';
    },

    renderSeparator: function() {
        return this.renderTimeElements("ui-divider", '<span>:</span>', -1);
    },

    renderFractionSeparator: function() {
        return this.renderTimeElements("ui-divider", '<span>.</span>', -1);
    },

    renderTimeElements: function(containerClass, text, type) {
        var container = '<div class="' + containerClass + '" data-type="' + type + '">';

        //up
        container += this.renderTimePickerUpButton(type);
        //text
        container += text;
        //down
        container += this.renderTimePickerDownButton(type);

        //end
        container += '</div>';

        return container;
    },

    renderTimePickerUpButton: function(type) {
        var ariaLabel = '';
        switch (type) {
            case 0:
                ariaLabel = this.options.locale.nextHour;
                break;
            case 1:
                ariaLabel = this.options.locale.nextMinute;
                break;
            case 2:
                ariaLabel = this.options.locale.nextSecond;
                break;
            case 3:
                ariaLabel = this.options.locale.nextMillisecond;
                break;
            case 4:
                ariaLabel = this.options.locale.am;
                break;
            default:
                ariaLabel = '';
        }
        return '<button type="button" aria-label="' + ariaLabel + '" class="ui-picker-up" tabindex="0">' +
            '<span class="ui-icon ui-icon-carat-1-n"></span>' +
            '</button>';
    },

    renderTimePickerDownButton: function(type) {
        var ariaLabel = '';
        switch (type) {
            case 0:
                ariaLabel = this.options.locale.prevHour;
                break;
            case 1:
                ariaLabel = this.options.locale.prevMinute;
                break;
            case 2:
                ariaLabel = this.options.locale.prevSecond;
                break;
            case 3:
                ariaLabel = this.options.locale.prevMillisecond;
                break;
            case 4:
                ariaLabel = this.options.locale.pm;
                break;
            default:
                ariaLabel = '';
        }
        return '<button type="button" aria-label="' + ariaLabel + '" class="ui-picker-down" tabindex="0">' +
            '<span class="ui-icon ui-icon-carat-1-s"></span>' +
            '</button>';
    },

    getClassesToAdd: function(classes) {
        var _classes = '';
        $.each(classes, function(key, value) {
            if (value) {
                _classes += ' ' + key;
            }
        });

        return _classes;
    },

    toISODateString: function(date) {
        return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString().substring(0, 10);
    },

    _bindEvents: function() {
        if (this.options.readonly) {
            // #12385 readonly input should not allow any events
            return;
        }
        var $this = this;
        if (!this.options.inline) {
            // #13269 delay registering events until CSP has registered
            PrimeFaces.queueTask(function () {
                if (!$this.options) return;
                if (!$this.options.onChange) {
                    // get the current attached events if using CSP
                    var events = $this.inputfield[0] ? $._data($this.inputfield[0], "events") : null;

                    // use DOM if non-CSP and JQ event if CSP
                    var originalOnchange = $this.inputfield.prop('onchange');
                    if (!originalOnchange && events && events.change) {
                        originalOnchange = events.change[0].handler;
                    }
                    $this.inputfield.prop('onchange', null).off('change');

                    $this.options.onChange = function (event) {
                        if (originalOnchange) {
                            originalOnchange.call($this, event);
                        }
                    };
                }

                $this.inputfield.off('focus.datePicker blur.datePicker change.datePicker keydown.datePicker input.datePicker click.datePicker')
                    .on('focus.datePicker', $this.onInputFocus.bind($this))
                    .on('blur.datePicker', $this.onInputBlur.bind($this))
                    .on('change.datePicker', $this.onInputChange.bind($this))
                    .on('keydown.datePicker', $this.onInputKeyDown.bind($this))
                    .on('input.datePicker', $this.onUserInput.bind($this))
                    .on('click.datePicker', $this.onInputClick.bind($this));
            }, 1);

            if (this.triggerButton) {
                this.triggerButton.off('click.datePicker-triggerButton').on('click.datePicker-triggerButton', this.onButtonClick.bind($this));
            }
        }

        this.panel.off('click.datePicker keydown.datePicker')
            .on('click.datePicker', this.onPanelClick.bind($this))
            .on('keydown.datePicker', this.onPanelKeyDown.bind($this));

        var navBackwardSelector = '.ui-datepicker-header > .ui-datepicker-prev',
            navForwardSelector = '.ui-datepicker-header > .ui-datepicker-next';
        this.panel.off('click.datePicker-navBackward', navBackwardSelector)
            .on('click.datePicker-navBackward', navBackwardSelector, null, this.navBackward.bind($this));
        this.panel.off('click.datePicker-navForward', navForwardSelector)
            .on('click.datePicker-navForward', navForwardSelector, null, this.navForward.bind($this));

        var monthNavigatorSelector = '.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-month',
            yearNavigatorSelector = '.ui-datepicker-header > .ui-datepicker-title > .ui-datepicker-year';
        this.panel.off('change.datePicker-monthNav', monthNavigatorSelector)
            .on('change.datePicker-monthNav', monthNavigatorSelector, null, this.onMonthDropdownChange.bind($this));
        this.panel.off('change.datePicker-yearnav', yearNavigatorSelector)
            .on('change.datePicker-yearnav', yearNavigatorSelector, null, this.onYearInputChange.bind($this));
        if (this.isYearNavigatorInput()) {
            this.panel.off('keydown.datePicker-yearnav keyup.datePicker-yearnav', yearNavigatorSelector)
            .on('keydown.datePicker-yearnav', yearNavigatorSelector, null, function(event) {$this.onTimeInputKeyDown(event);})
            .on('keyup.datePicker-yearnav', yearNavigatorSelector, null, function(event) {$this.onTimeInputKeyUp(event);});
        }

        var monthViewMonthSelector = '.ui-monthpicker > .ui-monthpicker-month';
        this.panel.off('click.datePicker-monthViewMonth', monthViewMonthSelector).on('click.datePicker-monthViewMonth', monthViewMonthSelector, null, function(e) {
            $this.onMonthSelect(e, $(this).index());
        }).on('keydown.datePicker-monthViewMonth', monthViewMonthSelector, null, function(event) {
            $this.onMonthKeyDown(event, $(this).index());
        });

        var timeSelector = '.ui-hour-picker > button,  .ui-minute-picker > button, .ui-second-picker > button, .ui-millisecond-picker > button',
            ampmSelector = '.ui-ampm-picker > button';
        this.panel.off('mousedown.timepicker mouseup.timepicker mouseout.timepicker keydown.timepicker keyup.timepicker click.timepicker-ampm', timeSelector).off('click.datePicker-ampm', ampmSelector)
            .on('mousedown.timepicker keydown.timepicker', timeSelector, null, function(event) {
                var button = $(this),
                    parentEl = button.parent();

                var isActionKey = PrimeFaces.utils.isActionKey(event);
                if (!event.key || isActionKey) {
                    $this.onTimePickerElementMouseDown(event, parseInt(parentEl.data('type'), 10), button.hasClass('ui-picker-up') ? 1 : -1);
                }
            })
            .on('mouseup.timepicker keyup.timepicker', timeSelector, null, function(event) {
                $this.onTimePickerElementMouseUp(event);
            })
            .on('mouseout.timepicker', timeSelector, null, function(event) {
                if ($this.timePickerTimer) {
                    $this.onTimePickerElementMouseUp(event);
                }
            })
            .on('click.timepicker-ampm', ampmSelector, null, function(event) {
                $this.toggleAmPm(event);
            });

        if (this.options.timeInput) {
            this.panel.off('focus', '.ui-hour-picker input').on('focus', '.ui-hour-picker input', null, function(event) {
                $this.oldHours = this.value;
            }).off('focus', '.ui-minute-picker input').on('focus', '.ui-minute-picker input', null, function(event) {
                $this.oldMinutes = this.value;
            }).off('focus', '.ui-second-picker input').on('focus', '.ui-second-picker input', null, function(event) {
                $this.oldSeconds = this.value;
            }).off('focus', '.ui-millisecond-picker input').on('focus', '.ui-millisecond-picker input', null, function(event) {
                $this.oldMilliseconds = this.value;
            }).off('change', '.ui-hour-picker input').on('change', '.ui-hour-picker input', null, function(event) {
                $this.handleHoursInput(this, event);
            }).off('keydown', '.ui-hour-picker input').on('keydown', '.ui-hour-picker input', null, function(event) {
                $this.onTimeInputKeyDown(event);
            }).off('keyup', '.ui-hour-picker input').on('keyup', '.ui-hour-picker input', null, function(event) {
                $this.onTimeInputKeyUp(event);
            }).off('change', '.ui-minute-picker input').on('change', '.ui-minute-picker input', null, function(event) {
                $this.handleMinutesInput(this, event);
            }).off('keydown', '.ui-minute-picker input').on('keydown', '.ui-minute-picker input', null, function(event) {
                $this.onTimeInputKeyDown(event);
            }).off('keyup', '.ui-minute-picker input').on('keyup', '.ui-minute-picker input', null, function(event) {
                $this.onTimeInputKeyUp(event);
            }).off('change', '.ui-second-picker input').on('change', '.ui-second-picker input', null, function(event) {
                $this.handleSecondsInput(this, event);
            }).off('keydown', '.ui-second-picker input').on('keydown', '.ui-second-picker input', null, function(event) {
                $this.onTimeInputKeyDown(event);
            }).off('keyup', '.ui-second-picker input').on('keyup', '.ui-second-picker input', null, function(event) {
                $this.onTimeInputKeyUp(event);
            }).off('change', '.ui-millisecond-picker input').on('change', '.ui-millisecond-picker input', null, function(event) {
                $this.handleMillisecondsInput(this, event);
            }).off('keydown', '.ui-millisecond-picker input').on('keydown', '.ui-millisecond-picker input', null, function(event) {
                $this.onTimeInputKeyDown(event);
            }).off('keyup', '.ui-millisecond-picker input').on('keyup', '.ui-millisecond-picker input', null, function(event) {
                $this.onTimeInputKeyUp(event);
            });
        }

        var todayButtonSelector = '.ui-datepicker-buttonbar .ui-today-button',
            clearButtonSelector = '.ui-datepicker-buttonbar .ui-clear-button';
        this.panel.off('click.datePicker-todayButton', todayButtonSelector).on('click.datePicker-todayButton', todayButtonSelector, null, this.onTodayButtonClick.bind($this));
        this.panel.off('click.datePicker-clearButton', clearButtonSelector).on('click.datePicker-clearButton', clearButtonSelector, null, this.onClearButtonClick.bind($this));

        var dateSelector = '.ui-datepicker-calendar td a';
        this.panel.off('click.datePicker-date keydown.datePicker-date', dateSelector)
            .on('click.datePicker-date', dateSelector, null, function(event) {
                if ($this.monthsMetadata) {
                    var dayEl = $(this),
                        calendarIndex = dayEl.closest('.ui-datepicker-group').index(),
                        weekIndex = dayEl.closest('tr').index(),
                        dayIndex = dayEl.closest('td').index() - ($this.options.showWeek ? 1 : 0);
                    $this.onDateSelect(event, $this.monthsMetadata[calendarIndex].dates[weekIndex][dayIndex]);
                }
            }).on('keydown.datePicker-date', dateSelector, null, function(event) {
                var dayEl = $(this),
                    calendarIndex = dayEl.closest('.ui-datepicker-group').index(),
                    weekIndex = dayEl.closest('tr').index(),
                    dayIndex = dayEl.closest('td').index() - ($this.options.showWeek ? 1 : 0);
                $this.onDateKeyDown(event, $this.monthsMetadata[calendarIndex].dates[weekIndex][dayIndex]);
            });
    },

    _bindPanelEvents: function() {
        if (this.options.view === 'week') {
            // Add events to highlight whole week
            var row = this.panel.find('.ui-datepicker-calendar tr');
            row.mousemove(function() {
                $(this).find('td a').addClass('ui-state-hover');
            });
            row.mouseleave(function() {
                $(this).find('td a').removeClass('ui-state-hover');
            });

            // Highlight active week
            var activeRow = this.panel.find('.ui-state-active').closest('tr');
            activeRow.find('td').each(function () {
                $(this).find('a').addClass('ui-state-active');
            });
        }
    },

    onDateKeyDown: function(event, dateMeta) {
        if (this.options.disabled || !dateMeta.selectable) {
            if (event) {
                event.preventDefault();
            }
            return;
        }

        var $this = this;
        var currentElement = $(event.currentTarget);
        var $tabbableElements = $this.panel.find('a:tabbable');
        var currentIndex = $tabbableElements.index(currentElement);
        switch (event.code) {
            case 'Enter':
            case 'NumpadEnter':
            case 'Space':
                $this.onDateSelect(event, dateMeta);
                event.preventDefault();
                break;

            case 'ArrowLeft':
                // Moves focus to the previous day.
                var prevIndex = (currentIndex - 1) % $tabbableElements.length;
                $tabbableElements.eq(prevIndex).trigger('focus');
                event.preventDefault();
                break;

            case 'ArrowRight':
                // Moves focus to the next day.
                var nextIndex = (currentIndex + 1) % $tabbableElements.length;
                $tabbableElements.eq(nextIndex).trigger('focus');
                event.preventDefault();
                break;

            case 'ArrowDown':
                // Moves focus to the same day of the next week.
                var nextIndex = (currentIndex + 7) % $tabbableElements.length;
                if (nextIndex < currentIndex) {
                    $this.focusNextInterval(event);
                }
                else {
                    $tabbableElements.eq(nextIndex).trigger('focus');
                }
                event.preventDefault();
                break;

            case 'ArrowUp':
                // Moves focus to the same day of the previous week.
                var nextIndex = (currentIndex - 7) % $tabbableElements.length;
                if (nextIndex < 0) {
                    $this.focusPreviousInterval(event);
                }
                else {
                    $tabbableElements.eq(nextIndex).trigger('focus');
                }
                event.preventDefault();
                break;

            case 'Home':
                // Find the first focusable element within the row
                var row = currentElement.closest('tr');
                $this.focusDate(row, 'a:visible:first');
                event.preventDefault();
                break;

            case 'End':
                // Find the last focusable element within the row
                var row = currentElement.closest('tr');
                $this.focusDate(row, 'a:visible:last');
                event.preventDefault();
                break;

            case 'PageUp':
                $this.focusPreviousInterval(event);
                break;

            case 'PageDown':
                $this.focusNextInterval(event);
                break;
        };
    },

    onMonthKeyDown: function(event, index) {
        if (this.options.disabled) {
            if (event) {
                event.preventDefault();
            }
            return;
        }

        var $this = this;
        var currentElement = $(event.currentTarget);
        var $tabbableElements = $this.panel.find('a:tabbable');
        var currentIndex = $tabbableElements.index(currentElement);
        switch (event.code) {
            case 'Enter':
            case 'NumpadEnter':
            case 'Space':
                $this.onMonthSelect(event, index);
                event.preventDefault();
                break;

            case 'ArrowLeft':
                // Moves focus to the previous month.
                var prevIndex = (currentIndex - 1) % $tabbableElements.length;
                $tabbableElements.eq(prevIndex).trigger('focus');
                event.preventDefault();
                break;

            case 'ArrowRight':
                // Moves focus to the next month.
                var nextIndex = (currentIndex + 1) % $tabbableElements.length;
                $tabbableElements.eq(nextIndex).trigger('focus');
                event.preventDefault();
                break;

            case 'ArrowDown':
                // Moves focus to the next row
                var nextIndex = (currentIndex + 3) % $tabbableElements.length;
                if (nextIndex < currentIndex) {
                    $this.focusNextInterval(event);
                }
                else {
                    $tabbableElements.eq(nextIndex).trigger('focus');
                }
                event.preventDefault();
                break;

            case 'ArrowUp':
                // Moves focus to previous row
                var nextIndex = (currentIndex - 3) % $tabbableElements.length;
                if (nextIndex <= 0) {
                    $this.focusPreviousInterval(event);
                }
                else {
                    $tabbableElements.eq(nextIndex).trigger('focus');
                }
                event.preventDefault();
                break;

            case 'Home':
                // Find the first month
                $this.focusDate($this.panel, 'a:visible:first');
                event.preventDefault();
                break;

            case 'End':
                // Find the last month
                $this.focusDate($this.panel, 'a:visible:last');
                event.preventDefault();
                break;

            case 'PageUp':
                $this.focusPreviousInterval(event);
                break;

            case 'PageDown':
                $this.focusNextInterval(event);
                break;
        };
    },

    onTimeInputKeyDown: function (event) {
        if (this.options.disabled) {
            event.preventDefault();
            return false;
        }

        if (PrimeFaces.env.android) {
            return true;
        }

        var input = event.currentTarget;

        if (input.maxLength === 2 && event.key == 'Enter') {
            this.hideOverlay();
            event.preventDefault();
            return false;
        }

        // Allow text selection and cut, copy, paste
        const allowedControlKeys = [
            "a",
            "c",
            "v",
            "x",
            "z"
        ];
        if (event.ctrlKey && allowedControlKeys.includes(event.key)) {
            return true;
        }
                

        // Allow navigation keys, control keys, and numeric keys
        const allowedKeys = [
            "ArrowDown",
            "ArrowLeft",
            "ArrowRight",
            "ArrowUp",
            "Backspace",
            "Delete",
            "End",
            "Home",
            "Shift",
            "Tab",
        ];

        // Allow numeric keys (0-9)
        const isNumericKey = /^[0-9]$/.test(event.key);
    
        if (!isNumericKey && !allowedKeys.includes(event.key)) {
            event.preventDefault(); // Block all other keys
        }

        // If input is for year and already full, reset value
        if (isNumericKey && input.maxLength === 4 && input.value.length === 4) {
            input.value = "";
        }
    },
    
    onTimeInputKeyUp: function (event) {
        const input = event.currentTarget;
        let newValue = input.value;

        // Validate the input value after the key is released
        // Remove non-numeric characters
        newValue = newValue.replace(/[^0-9]/g, "");

        // For year input, only evaluate if the input is 4 digits long
        if (input.maxLength === 4 && newValue.length < 4) {
            return;
        }

        // Check min and max constraints
        const inputMin = parseInt(input.min, 10);
        const inputMax = parseInt(input.max, 10);

        if (newValue.length > input.maxLength) {
            // Truncate to maxLength
            newValue = newValue.substring(0, input.maxLength);
        }

        if (newValue && (parseInt(newValue, 10) < inputMin || parseInt(newValue, 10) > inputMax)) {
            // If the value is outside the min/max range, reset to the closest valid value
            if (parseInt(newValue, 10) < inputMin) {
                newValue = inputMin.toString();
            } else if (parseInt(newValue, 10) > inputMax) {
                newValue = inputMax.toString();
            }
        }

        // Update the input field value
        input.value = newValue;
    },

    focusDate: function(jq, selector) {
        if (!jq || !selector) {
            return;
        }
        var focusable = jq.find(selector);
        // Focus on the found element
        if (focusable.length > 0) {
            focusable.trigger('focus');
        }
    },

    focusNextInterval: function(event) {
        this.navForward(event);
        // Find the first focusable day of the month
        this.focusDate(this.panel, 'a:visible:first');
    },

    focusPreviousInterval: function(event) {
        this.navBackward(event);
        // Find the last focusable day of the month
        this.focusDate(this.panel, 'a:visible:last');
    },

    onInputClick: function(event) {
        if (this.documentClickListener) {
            this.datepickerClick = true;
        }
        
        if (this.isPanelVisible()) {
            // #11928 allow the input to be clicked again to close panel and allow typing of date
            if (!this.datepickerFocus) {
                this.hideOverlay();
            }
        } 
        else if (this.options.showOnFocus) {
            // #12361 allow the input to be clicked again to open the panel if showOnFocus is true
            this.showOverlay();
        }
    },

    onInputFocus: function(event) {
        if (this.options.showOnFocus && !this.isPanelVisible() && !this.datepickerFocus) {
            this.datepickerFocus = true;
            this.showOverlay()
        }
        if (this.options.onFocus) {
            this.options.onFocus.call(this, event);
        }
        this.inputfield.addClass('ui-state-focus');
        this.container.addClass('ui-inputwrapper-focus');
        this.valueOnFocus = this.getDate();
    },

    onInputBlur: function(event) {
        if (this.options.onBlur && event) {
            this.options.onBlur.call(this, event);
        }

        this.inputfield.removeClass('ui-state-focus');
        this.container.removeClass('ui-inputwrapper-focus');

        // #12754 if mask is used, fire the change event
        if (this.options.mask && this.valueOnFocus !== this.getDate()) {
            this.valueOnFocus = undefined;
            this.onInputChange(event);
        }
    },

    onInputChange: function(event) {
        if ((this.options.autoMonthFormat || !this.inputfield.val()) && this.options.monthNavigator && this.options.view !== 'month') { 
            var viewMonth = this.viewDate.getMonth();
            viewMonth = (this.isInMaxYear() && Math.min(this.options.maxDate.getMonth(), viewMonth)) || (this.isInMinYear() && Math.max(this.options.minDate.getMonth(), viewMonth)) || viewMonth;
            this.viewDate.setMonth(viewMonth);
        }

        !this.options.keepInvalid && this.inputfield.val(this.getValueToRender());

        if (this.options.onChange) {
            this.options.onChange.call(this, event);
        }
    },

    onInputKeyDown: function(event) {
        switch (event.key) {
            case 'ArrowDown':
                this.inputfield.val(this.getValueToRender());
                this.showOverlay();
                break;
            case 'Escape':
                this.onEscapeKey(event);
                break;
        }
    },

    onPanelKeyDown: function(event) {
        if (event.key === 'Escape') {
            event.preventDefault();
            event.stopPropagation();
            this.onEscapeKey(event);
        }
    },

    onEscapeKey: function(event) {
        this.hideOverlay();
    },

    onUserInput: function(event) {
        var rawValue = event.target.value;

        try {
            var value = this.parseValueFromString(rawValue);
            this.updateModel(event, value, false);
            this.updateViewDate(event, value.length ? value[0] : value);
        }
        catch (err) {
            //invalid date - TODO: Update according to PrimeNG/React/Vue library in future versions.
            // var value = this.options.keepInvalid ? rawValue : null;

            if (!this.options.mask) {
                this.updateModel(event, rawValue, false);
            }
        }

        if (this.options.onInput) {
            this.options.onInput.call(this, event);
        }
    },

    onButtonClick: function(event) {
        if (!this.isPanelVisible()) {
            this.showOverlay();
        }
        else {
            this.hideOverlay();
        }
    },

    onPanelClick: function(event) {
        if (this.documentClickListener) {
            this.datepickerClick = true;
        }
    },

    onMonthDropdownChange: function(event) {
        var newViewDate = new Date(this.viewDate.getTime());
        newViewDate.setDate(1); //always set to first of month
        newViewDate.setMonth(parseInt(event.target.value, 10));

        if (this.options.onMonthChange) {
            this.options.onMonthChange.call(this, newViewDate.getMonth() + 1, newViewDate.getFullYear());
        }
        this.updateViewDate(event, newViewDate);
    },

    onYearInputChange: function(event) {
        var newViewDate = new Date(this.viewDate.getTime());
        newViewDate.setFullYear(parseInt(event.target.value, 10));

        if (this.options.onYearChange) {
            this.options.onYearChange.call(this, newViewDate.getMonth(), newViewDate.getFullYear());
        }
        this.updateViewDate(event, newViewDate);
    },

    onMonthSelect: function(event, month) {
        this.onDateSelect(event, { year: this.viewDate.getFullYear(), month: month, day: 1, selectable: true });
        event.preventDefault();
    },

    navBackward: function(event) {
        if (this.options.disabled) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        var newViewDate = new Date(this.viewDate.getTime());
        newViewDate.setDate(1);

        if (this.options.view === 'date' || this.options.view === 'week') {
            if (newViewDate.getMonth() === 0) {
                newViewDate.setMonth(11, 1);
                newViewDate.setFullYear(newViewDate.getFullYear() - 1);
            }
            else {
                newViewDate.setMonth(newViewDate.getMonth() - 1, 1);
            }

            // previous (check first day of month at 00:00:00)
            newViewDate = this.truncateDate(newViewDate);

            // #5967 check if month can be navigated to by checking last day in month
            var testDate = new Date(newViewDate.getTime()),
                minDate = this.options.minDate;
            testDate.setMonth(testDate.getMonth() + 1);
            testDate.setHours(-1);
            if (this.options.showMinMaxRange && minDate && minDate > testDate) {
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

            if (this.isYearNavigator()) {
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

    navForward: function(event) {
        if (this.options.disabled) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        var newViewDate = new Date(this.viewDate.getTime());
        newViewDate.setDate(1);

        if (this.options.view === 'date' || this.options.view === 'week') {
            if (newViewDate.getMonth() === 11) {
                newViewDate.setMonth(0, 1);
                newViewDate.setFullYear(newViewDate.getFullYear() + 1);
            }
            else {
                newViewDate.setMonth(newViewDate.getMonth() + 1, 1);
            }

            // next (check last day of month)
            newViewDate = this.truncateDate(newViewDate);

            // #5967 check if month can be navigated to by checking first day next month
            var maxDate = this.options.maxDate;
            if (this.options.showMinMaxRange && maxDate && maxDate < newViewDate) {
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

            if (this.isYearNavigator()) {
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
        if (!newViewDate || !this.options.showMinMaxRange || this.options.view === 'month') {
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
        if (this.options.minDate) {
            let firstDayOfMonth = new Date(newViewDate.getTime());

            firstDayOfMonth.setMonth(firstDayOfMonth.getMonth(), 1);
            firstDayOfMonth = this.truncateDate(firstDayOfMonth);

            if (this.options.minDate > firstDayOfMonth) {
                navPrev.addClass('ui-state-disabled');
            } else {
                navPrev.removeClass('ui-state-disabled');
            }
        }
        
         // next (check last day of month at 11:59:59)
        if (this.options.maxDate) {
            let lastDayOfMonth = new Date(newViewDate.getTime());

            lastDayOfMonth.setMonth(lastDayOfMonth.getMonth() + 1, 1);
            lastDayOfMonth = this.truncateDate(lastDayOfMonth);
            lastDayOfMonth.setSeconds(-1);

            if (this.options.maxDate < lastDayOfMonth) {
                navNext.addClass('ui-state-disabled');
            } else {
                navNext.removeClass('ui-state-disabled');
            }
        }
    },

    onTimePickerElementMouseDown: function(event, type, direction) {
        // only left button, Enter and Space key allowed
        var isActionKey = PrimeFaces.utils.isActionKey(event);
        if (!this.options.disabled && (event.button === 0 || isActionKey)) {
            var interval = isActionKey ? -1 : null;
            this.repeat(event, interval, type, direction);
            if (!isActionKey) {
                event.preventDefault();
            }
        }
    },

    onTimePickerElementMouseUp: function(event) {
        if (!this.options.disabled) {
            this.clearTimePickerTimer();

            if (event.key === 'Tab') return;

            if (this.options.onSelect && this.value) {
                this.options.onSelect.call(this, event, this.value);
            }
        }
    },

    repeat: function(event, interval, type, direction) {
        var i = interval || 500,
            $this = this;

        if (interval > -1) {
            this.clearTimePickerTimer();
            this.timePickerTimer = PrimeFaces.queueTask(function() {
                $this.repeat(event, 100, type, direction);
            }, i);
        }

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

            case 3:
                if (direction === 1)
                    this.incrementMillisecond(event);
                else
                    this.decrementMillisecond(event);
                break;
        }
    },

    clearTimePickerTimer: function() {
        if (this.timePickerTimer) {
            clearTimeout(this.timePickerTimer);
            this.timePickerTimer = null;
        }
    },

    focusOverlay: function() {
        var $this = this;
        var focused = null;
        if ($this.options.view === 'month') {
            focused = $this.panel.find('a.ui-monthpicker-month');
        }
        if ($this.options.view === 'date' || $this.options.view === 'week') {
            // focus first selected day or today
            focused = $this.panel.find('a.ui-state-active');

            if (focused.length === 0) {
                focused = $this.panel.find('.ui-datepicker-today a');
            }
            if (focused.length === 0) {
                focused = $this.panel.find('a.ui-state-default');
            }
            if (focused.length === 0) {
                focused = $this.panel.find('a.ui-state-default');
            }
            if (focused.length === 0) {
                focused = $this.panel.find(':button, :input');
            }
        }
        if (focused) {
            PrimeFaces.queueTask(function() { focused.first().trigger('focus') }, 1);
        }

        $this.inputfield.attr('aria-expanded', 'true');
        $this.inputfield.removeClass('ui-state-focus');

        if ($this.triggerButton) {
            $this.triggerButton.attr('aria-expanded', 'true');
        }
    },

    showOverlay: function() {
        // Return immediately if a document click listener is set, or if the datepicker is inline, already visible, or lacks a transition
        if (this.documentClickListener || this.options.inline || this.isPanelVisible() || !this.transition) {
            return;
        }
        var $this = this;

        // #14158 needed for float label
        let parent = this.inputfield.parent();
        if (parent.is("div, span:not('.ui-float-label')")) {
            parent.addClass('ui-inputwrapper-focus');
        }

        this.transition.show({
            onEnter: function() {
                if ($this.options.onBeforeShow) {
                    $this.options.onBeforeShow.call($this);
                }
                $this.alignPanel();
            },
            onEntered: function() {
                $this.datepickerClick = true;
                PrimeFaces.queueTask(function() {
                    $this.datepickerClick = false;
                    $this.datepickerFocus = false;
                }, 200);
                
                $this.bindDocumentClickListener();
                $this.bindWindowResizeListener();
                if (!$this.options.inline) {
                    $this.bindScrollListener();
                }
                
                $this.focusOverlay();
            }
        });
    },

    hideOverlay: function(event) {
        // Return immediately if no document click listener is set, or if the datepicker is inline, not visible, or lacks a transition
        if (!this.documentClickListener || this.options.inline || !this.isPanelVisible() || !this.transition) {
            return;
        }

        var $this = this;

        //put the focus back to the inputfield
        if (!event) {
            $this.inputfield.trigger('focus'); 
        }
        
        // if using mask disable the modality
        $this.disableModality();

        this.transition.hide({
            onExit: function() {
                if ($this.options.onBeforeHide) {
                    $this.options.onBeforeHide.call($this);
                }

                $this.unbindDocumentClickListener();
                $this.unbindWindowResizeListener();

                if (!$this.options.inline) {
                    $this.unbindScrollListener();
                }

                $this.datepickerClick = false;
            },
            onExited: function() {
                var viewDate = $this.options.viewDate && !$this.value ?
                    $this.parseValue($this.options.viewDate)
                    :
                    (((($this.isMultipleSelection() || $this.isRangeSelection()) && $this.value instanceof Array) ? $this.value[0] : $this.value) || $this.parseValue(this.getNow()));

                if (viewDate instanceof Date) {
                    $this.updateViewDate(null, viewDate);
                }

                if (!$this.options.inline) {
                    $this.inputfield.attr('aria-expanded', 'false');

                    if ($this.triggerButton) {
                        $this.triggerButton.attr('aria-expanded', 'false');
                    }
                }
            }
        });
    },

    bindDocumentClickListener: function() {
        var $this = this;
        if (!this.documentClickListener) {
            this.documentClickListener = function(event) {
                if (!$this.datepickerClick) {
                    $this.hideOverlay(event);
                    $this.onInputBlur();
                    PrimeFaces.queueTask(function() { $(event.target).trigger('focus') }, 1);
                }

                $this.datepickerClick = false;
            };

            $(document).on('click', this.documentClickListener);

            // prevent tabbing outside of panel
            PrimeFaces.utils.preventTabbing(undefined, $this.panel.id, $this.panel.zIndex(), function() {
                return $this.panel.find(':tabbable');
            });
        }
    },

    unbindDocumentClickListener: function() {
        if (this.documentClickListener) {
            $(document).off('click', this.documentClickListener);
            PrimeFaces.utils.enableTabbing(undefined, this.panel.id);
            this.documentClickListener = null;
        }
    },

    bindResponsiveResizeListener: function() {
        var $this = this;
        if (this.options.autoDetectDisplay && !this.options.inline) {
            var namespace = 'resize.responsive' + this.options.id;
            $(window).off(namespace).on(namespace, function() {
                $this.updateResponsiveness();
            });
        }
    },
    
    unbindResponsiveResizeListener: function() {
        $(window).off('resize.responsive' + this.options.id);
    },

    bindWindowResizeListener: function() {
        if (this.options.inline || this.options.touchUI || PrimeFaces.env.mobile) {
            return;
        }

        var $this = this;
        $(window).on('resize.' + $this.options.id, function() {
            // TODO: lazy model for some reason triggers a windows resize
            if (!$this.options.lazyModel) {
                $this.hideOverlay();
            }
        });
    },

    unbindWindowResizeListener: function() {
        $(window).off('resize.' + this.options.id);
    },

    bindScrollListener: function() {
        var $this = this;

        this.scrollableParents = PrimeFaces.utils.getScrollableParents(this.element.get(0));
        this.scrollableListener = function() {
            $this.hideOverlay();
        };

        for (var i = 0; i < this.scrollableParents.length; i++) {
            $(this.scrollableParents[i]).on('scroll', this.scrollableListener);
        }
    },

    unbindScrollListener: function() {
        if (this.scrollableParents && this.scrollableListener) {
            for (var i = 0; i < this.scrollableParents.length; i++) {
                $(this.scrollableParents[i]).off('scroll', this.scrollableListener);
            }

            this.scrollableListener = null;
        }
    },

    updateResponsiveness: function() {
        if (this.options.autoDetectDisplay && this.options.responsiveBreakpoint && !this.options.inline) {
            var currentUI = this.options.touchUI;
            var newUi = PrimeFaces.env.mobile || PrimeFaces.env.isScreenSizeLessThan(this.options.responsiveBreakpoint);
            if (currentUI !== newUi) {
                this.options.touchUI = newUi;
                this._render();
            }
        }
    },

    isPanelVisible: function() {
        return !this.options.disabled && this.panel && this.panel.is(":visible");
    },

    isDate: function(value) {
        return value && Object.prototype.toString.call(value) === "[object Date]" && !isNaN(value);
    },

    isYearNavigator: function() {
        return ["input", "select", "true"].includes(this.options.yearNavigator);
    },

    isYearNavigatorInput: function() {
        return ["input", "true"].includes(this.options.yearNavigator);
    },

    alignPanel: function() {
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
                    top: String(this.container.innerHeight()),
                    'transform-origin': 'center top'
                });
            }
            else {
                this.panel.css({ left: '', top: '', 'transform-origin': 'center top' }).position({
                    my: 'left top'
                    , at: 'left bottom'
                    , of: this.container
                    , collision: 'flipfit'
                    , using: function(pos, directions) {
                        $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                    }
                });
            }
        }
    },

    enableModality: function() {
        if (!this.mask) {
            this.mask = $('<div class="ui-widget-overlay ui-datepicker-mask ui-datepicker-mask-scrollblocker"></div>');
            this.mask.css('z-index', String(parseInt(this.panel.css('z-index'), 10) - 1));

            var $this = this;
            this.mask.on('click.datePicker-mask', function() {
                $this.disableModality();
            });

            $(document.body).append(this.mask).addClass('ui-overflow-hidden');
        }
    },

    disableModality: function() {
        if (this.mask) {
            this.mask.off('click.datePicker-mask');
            this.mask.remove();
            this.mask = null;

            var bodyChildren = $(document.body).children('.ui-datepicker-mask-scrollblocker');

            if (!bodyChildren.length) {
                $(document.body).removeClass('ui-overflow-hidden');
            }
        }
    },

    findWeekMetadata: function(dateMeta) {
        for (let month of this.monthsMetadata) {
            if (month.month === dateMeta.month && month.year === dateMeta.year) {
                for (let week of month.dates) {
                    for (let day of week) {
                        if (day.day === dateMeta.day) {
                            return week;
                        }
                    }
                }
                break;
            }
        }

        return null;
    },

    onDateSelect: function(event, dateMeta) {
        if (this.options.disabled || !dateMeta.selectable) {
            if (event) {
                event.preventDefault();
            }
            return;
        }

        var $this = this;

        if (this.options.view === 'week') {
            // simulate click on start and end day of the week
            var week = this.findWeekMetadata(dateMeta);
            this.selectDate(event, week[0]);
            this.selectDate(event, week[week.length - 1]);

            PrimeFaces.queueTask(function() {
                $this.hideOverlay();
            }, 100);
        }
        else if (this.isMultipleSelection()) {
            if (this.isSelected(dateMeta)) {
                var value = this.value.filter(function(date, i) {
                    return !$this.isDateEquals(date, dateMeta);
                });
                this.updateModel(event, value);
                
                // #10850 notify unselect
                if (this.options.onSelect) {
                    this.options.onSelect.call(this, event, value);
                }
            }
            else if (!this.options.maxDateCount || !this.value || this.options.maxDateCount > this.value.length) {
                this.selectDate(event, dateMeta);
            }
        }
        else {
            this.selectDate(event, dateMeta);
        }

        if (!this.options.inline && this.options.focusOnSelect && this.isSingleSelection() && (!this.options.showTime || this.options.hideOnDateTimeSelect)) {
            PrimeFaces.queueTask(function() {
                $this.hideOverlay();
            }, 100);
        }
        
        if (!this.options.inline && this.isRangeSelection() && this.options.hideOnRangeSelection && this.value && this.value[1]) {
            PrimeFaces.queueTask(function() {
                $this.hideOverlay();
            }, 100);
        }

        if (event) {
            event.preventDefault();
        }
    },

    selectDate: function(event, dateMeta) {
        var date = new Date(dateMeta.year, dateMeta.month, dateMeta.day);

        if (this.options.showTime) {
            var time = this.getCurrentTime();
            date.setHours(time.getHours());
            date.setMinutes(this.stepMinute(time.getMinutes()));
            date.setSeconds(time.getSeconds());
            date.setMilliseconds(time.getMilliseconds());
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

        // #14158 needed for float label
        PrimeFaces.updateFilledState(this.inputfield, this.inputfield.parent());
    },

    incrementHour: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentHour = currentTime.getHours(),
            newHour = currentHour + this.options.stepHour;
        newHour = (newHour >= 24) ? (newHour - 24) : newHour;

        if (this.validateTime(newHour, currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds(), currentTime, "INCREMENT")) {
            this.updateTime(event, newHour, currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds());
        }

        if (!PrimeFaces.utils.isActionKey(event)) event.preventDefault();
    },

    decrementHour: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentHour = currentTime.getHours(),
            newHour = currentHour - this.options.stepHour;
        newHour = (newHour < 0) ? (newHour + 24) : newHour;

        if (this.validateTime(newHour, currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds(), currentTime, "DECREMENT")) {
            this.updateTime(event, newHour, currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds());
        }

        event.preventDefault();
    },

    incrementMinute: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentMinute = currentTime.getMinutes(),
            newMinute = this.stepMinute(currentMinute, this.options.stepMinute);
        newMinute = (newMinute > 59) ? (newMinute - 60) : newMinute;

        if (this.validateTime(currentTime.getHours(), newMinute, currentTime.getSeconds(), currentTime.getMilliseconds(), currentTime, "INCREMENT")) {
            this.updateTime(event, currentTime.getHours(), newMinute, currentTime.getSeconds(), currentTime.getMilliseconds());
        }

        event.preventDefault();
    },

    decrementMinute: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentMinute = currentTime.getMinutes(),
            newMinute = this.stepMinute(currentMinute, -this.options.stepMinute);
        newMinute = (newMinute < 0) ? (newMinute + 60) : newMinute;

        if (this.validateTime(currentTime.getHours(), newMinute, currentTime.getSeconds(), currentTime.getMilliseconds(), currentTime, "DECREMENT")) {
            this.updateTime(event, currentTime.getHours(), newMinute, currentTime.getSeconds(), currentTime.getMilliseconds());
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

    incrementSecond: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentSecond = currentTime.getSeconds(),
            newSecond = currentSecond + this.options.stepSecond;
        newSecond = (newSecond > 59) ? (newSecond - 60) : newSecond;

        if (this.validateTime(currentTime.getHours(), currentTime.getMinutes(), newSecond, currentTime.getMilliseconds(), currentTime, "INCREMENT")) {
            this.updateTime(event, currentTime.getHours(), currentTime.getMinutes(), newSecond, currentTime.getMilliseconds());
        }

        event.preventDefault();
    },

    decrementSecond: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentSecond = currentTime.getSeconds(),
            newSecond = currentSecond - this.options.stepSecond;
        newSecond = (newSecond < 0) ? (newSecond + 60) : newSecond;

        if (this.validateTime(currentTime.getHours(), currentTime.getMinutes(), newSecond, currentTime.getMilliseconds(), currentTime, "DECREMENT")) {
            this.updateTime(event, currentTime.getHours(), currentTime.getMinutes(), newSecond, currentTime.getMilliseconds());
        }

        event.preventDefault();
    },

    incrementMillisecond: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentMillisecond = currentTime.getMilliseconds(),
            newMillisecond = currentMillisecond + this.options.stepMillisecond;
        newMillisecond = (newMillisecond > 999) ? (newMillisecond - 1000) : newMillisecond;

        if (this.validateTime(currentTime.getHours(), currentTime.getMinutes(), currentTime.getSeconds(), newMillisecond, currentTime, "INCREMENT")) {
            this.updateTime(event, currentTime.getHours(), currentTime.getMinutes(), currentTime.getSeconds(), newMillisecond);
        }

        event.preventDefault();
    },

    decrementMillisecond: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentMillisecond = currentTime.getMilliseconds(),
            newMillisecond = currentMillisecond - this.options.stepMillisecond;
        newMillisecond = (newMillisecond < 0) ? (newMillisecond + 1000) : newMillisecond;

        if (this.validateTime(currentTime.getHours(), currentTime.getMinutes(), currentTime.getSeconds(), newMillisecond, currentTime, "DECREMENT")) {
            this.updateTime(event, currentTime.getHours(), currentTime.getMinutes(), currentTime.getSeconds(), newMillisecond);
        }

        event.preventDefault();
    },

    toggleAmPm: function(event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            currentHour = currentTime.getHours(),
            newHour = (currentHour >= 12) ? currentHour - 12 : currentHour + 12;

        this.updateTime(event, newHour, currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds());
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
                if (newHours >= 1 && newHours <= 12) {
                    valid = this.validateTime(newHours, currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds(), currentTime);
                }
            } else {
                if (newHours >= 0 && newHours <= 23) {
                    valid = this.validateTime(newHours, currentTime.getMinutes(), currentTime.getSeconds(), currentTime.getMilliseconds(), currentTime);
                }
            }
        }

        if (!valid) {
            event.preventDefault();
            input.value = this.oldHours;
            return;
        }

        var newDateTime = this.getCurrentTime();
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
            if (newMinutes >= 0 && newMinutes <= 59) {
                valid = this.validateTime(currentTime.getHours(), newMinutes, currentTime.getSeconds(), currentTime.getMilliseconds(), currentTime);
            }
        }

        if (!valid) {
            event.preventDefault();
            input.value = this.oldMinutes;
            return;
        }

        var newDateTime = this.getCurrentTime();
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
            if (newSeconds >= 0 && newSeconds <= 59) {
                valid = this.validateTime(currentTime.getHours(), currentTime.getMinutes(), newSeconds, currentTime.getMilliseconds(), currentTime);
            }
        }

        if (!valid) {
            event.preventDefault();
            input.value = this.oldSeconds;
            return;
        }

        var newDateTime = this.getCurrentTime();
        newDateTime.setSeconds(newSeconds);

        this.updateTimeAfterInput(event, newDateTime);
    },

    handleMillisecondsInput: function(input, event) {
        var currentTime = this.isDate(this.value) ? this.value : this.viewDate,
            value = input.value,
            valid = false,
            newMilliseconds;

        var reg = new RegExp('^([0-9]){1,3}$');
        if (reg.test(value)) {
            newMilliseconds = parseInt(value);
            if (newMilliseconds >= 0 && newMilliseconds <= 999) {
                valid = this.validateTime(currentTime.getHours(), currentTime.getMinutes(), currentTime.getSeconds(), newMilliseconds, currentTime);
            }
        }

        if (!valid) {
            event.preventDefault();
            input.value = this.oldMilliseconds;
            return;
        }

        var newDateTime = this.getCurrentTime();
        newDateTime.setMilliseconds(newMilliseconds);

        this.updateTimeAfterInput(event, newDateTime);
    },

    validateTime: function(hour, minute, second, millisecond, value, direction) {
        var valid = true;
        var dateNew = new Date(value.getFullYear(), value.getMonth(), value.getDate(), hour, minute, second, millisecond);

        if (this.options.minDate && value) {
            if (this.options.minDate > dateNew) {
                if (direction === "INCREMENT" && this.options.minDate > value) {
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

    updateTime: function(event, hour, minute, second, millisecond) {
        var newDateTime = this.getCurrentTime();

        newDateTime.setHours(hour);
        newDateTime.setMinutes(minute);
        newDateTime.setSeconds(second);
        newDateTime.setMilliseconds(millisecond);

        this.updateModel(event, newDateTime);

        if (this.options.onSelect) {
            if (this.timePickerTimer === 'undefined' || this.timePickerTimer === null) {
                this.options.onSelect.call(this, event, newDateTime);
            }
        }
    },

    updateTimeAfterInput: function(event, newDateTime) {
        this.value = newDateTime;
        this.inputfield.val(this.getValueToRender());

        if (this.options.onSelect) {
            this.options.onSelect.call(this, event, newDateTime);
        }
    },

    /**
     * Gets the current date and time. If a time zone is specified in the options, 
     * it returns the current date and time adjusted to that time zone.
     * 
     * @returns {Date} The current date and time.
     */
    getNow: function() {
        var now = new Date();
        if (this.options.timeZone) {
            var jsTimezone = this.convertTimeZone(this.options.timeZone);
            var localString = now.toLocaleString('en-GB', { // use English so we can parse it back
                year: "numeric",
                month: "long",
                day: "numeric",
                hour: "numeric",
                minute: "numeric",
                second: "numeric",
                timeZone: jsTimezone
            }).replace(' at ', ', ');
            now = new Date(localString);
        }
        return now;
    },

    /**
     * Gets the current time value of the datepicker.
     * Returns a new Date object initialized with either the current value if it's a valid date,
     * or the current view date if the value is not a valid date.
     * 
     * @returns {Date} A new Date object representing the current time
     */
    getCurrentTime: function() {
        return this.isDate(this.value) ? new Date(this.value) : new Date(this.viewDate);
    },

    /**
     * Converts a Java time zone string to an IANA time zone string.
     * 
     * @param {string} javaTimeZone - The Java time zone string to convert.
     * @returns {string} The corresponding IANA time zone string.
     * @throws {Error} If the input Java time zone string is in an invalid format.
     */
    convertTimeZone: function (javaTimeZone) {
        if (!javaTimeZone || ['ETC/UTC', 'ETC/GMT', 'UTC', 'GMT'].includes(javaTimeZone.toUpperCase())) {
            return javaTimeZone;
        }

        // Extract the sign and the offset (hours and minutes)
        const matches = javaTimeZone.match(/^(GMT|UTC)([+-])(\d{2}):(\d{2})$/);
        if (!matches) {
            return javaTimeZone;
        }

        const sign = matches[2];
        const hours = parseInt(matches[3]);

        // Convert GMT offset to IANA time zone format
        let ianaTimeZone = `Etc/GMT`;
        if (hours !== 0) {
            ianaTimeZone = `${ianaTimeZone}${sign === '+' ? '-' : '+'}${hours}`
        }
        return ianaTimeZone;
    },

    onTodayButtonClick: function(event) {
        var today = this.getNow();
        var dateMeta = { day: today.getDate(), month: today.getMonth(), year: today.getFullYear(), today: true, selectable: true };

        this.updateViewDate(event, today);
        if (this.options.showTime) {
            this.updateTime(event, today.getHours(), today.getMinutes(), today.getSeconds(), today.getMilliseconds());
        }
        this.onDateSelect(event, dateMeta);

        if (this.options.onTodayButtonClick) {
            this.options.onTodayButtonClick.call(this, event);
        }
    },

    onClearButtonClick: function(event) {
        this.updateViewDate(event, this.getNow());
        this.updateModel(event, null);

        if (this.options.onClearButtonClick) {
            this.options.onClearButtonClick.call(this, event);
        }
        this.hideOverlay();
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

        return String(value).replace(/[&<>"'`=\/]/g, function(s) {
            return entityMap[s];
        });
    },
    
    truncateDate: function(value) {
        if (value) {
            // only use date at 00:00 for comparison
            value.setHours(0);
            value.setMinutes(0);
            value.setSeconds(0);
            value.setMilliseconds(0);
        }
        return value;
    },

    updateYearNavigator: function() {
        var isYearInput = this.isYearNavigatorInput();
        if (this.hasCustomYearRange || (isYearInput && this.options.yearRange)) {
            return;
        }
        if (this.isYearNavigator()) {
            var viewYear = this.viewDate.getFullYear();
            var yearIncrement = isYearInput ? 1000 : 10;
            this.options.yearRange = (viewYear - yearIncrement) + ':' + (viewYear + yearIncrement);
        }
    },

    updateViewDate: function(event, value, silent = false) {
        if (this.options.onViewDateChange && !silent) {
            this.options.onViewDateChange.call(this, event, value);
        }

        this.viewDate = value;

        if ((this.options.autoMonthFormat || !this.inputfield.val()) && this.options.monthNavigator && this.options.view !== 'month') {
            var viewMonth = this.viewDate.getMonth();
            viewMonth = (this.isInMaxYear() && Math.min(this.options.maxDate.getMonth(), viewMonth)) || (this.isInMinYear() && Math.max(this.options.minDate.getMonth(), viewMonth)) || viewMonth;
            this.viewDate.setMonth(viewMonth);
        }

        this.updatePanel();
    },

    updateModel: function(event, value, updateInput) {
        this.value = (value === '' ? null : value);
        if (updateInput != false) {
            this.inputfield.val(this.getValueToRender());
        }

        this.updatePanel();
    },

    updatePanel: function() {
        // Remember the focused element before we destroy the inner elements
        var el = document.activeElement;

        // re-render the panels contents
        this.panel.get(0).innerHTML = this.renderPanelElements();

        // attempt to refocus the newly created version of the same element
        if (el && el.getAttribute("aria-label")) {
            var refocus = this.panel.find("[aria-label='" + PrimeFaces.escapeHTML(el.getAttribute("aria-label")) + "']");
            if (refocus.length) {
                PrimeFaces.queueTask(function() { refocus.first().trigger('focus') });
            }
        }

        this._setInitOptionValues();
        this._bindPanelEvents();
    }

});