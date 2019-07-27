/* PrimeFaces Extensions */
(function () {
    var original_gotoToday = $.datepicker._gotoToday;

    $.datepicker._gotoToday = function (id) {
        var target = $(id),
                inst = this._getInst(target[0]);

        original_gotoToday.call(this, id);
        this._selectDate(id, this._formatDate(inst, inst.selectedDay, inst.drawMonth, inst.drawYear));
    };

    $.datepicker._attachHandlers = function (inst) {
        var stepMonths = this._get(inst, "stepMonths"),
                id = "#" + inst.id.replace(/\\\\/g, "\\");
        inst.dpDiv.find("[data-handler]").map(function () {
            var handler = {
                prev: function () {
                    $.datepicker._adjustDate(id, -stepMonths, "M");
                    $.datepicker._updateDatePickerPosition(inst);
                },
                next: function () {
                    $.datepicker._adjustDate(id, +stepMonths, "M");
                    $.datepicker._updateDatePickerPosition(inst);
                },
                hide: function () {
                    $.datepicker._hideDatepicker();
                },
                today: function () {
                    $.datepicker._gotoToday(id);
                    $.datepicker._updateDatePickerPosition(inst);
                },
                selectDay: function () {
                    $.datepicker._selectDay(id, +this.getAttribute("data-month"), +this.getAttribute("data-year"), this);
                    $.datepicker._updateDatePickerPosition(inst);
                    return false;
                },
                selectMonth: function () {
                    $.datepicker._selectMonthYear(id, this, "M");
                    $.datepicker._updateDatePickerPosition(inst);
                    return false;
                },
                selectYear: function () {
                    $.datepicker._selectMonthYear(id, this, "Y");
                    $.datepicker._updateDatePickerPosition(inst);
                    return false;
                }
            };
            $(this).bind(this.getAttribute("data-event"), handler[this.getAttribute("data-handler")]);
        });
    };

    $.datepicker._updateDatePickerPosition = function (inst) {
        if (!$.datepicker._pos) { // position below input
            $.datepicker._pos = $.datepicker._findPos(inst.input[0]);
            $.datepicker._pos[1] += inst.input[0].offsetHeight; // add the height
        }

        var offset = {left: $.datepicker._pos[0], top: $.datepicker._pos[1]};
        $.datepicker._pos = null;
        var isFixed = false;
        $(inst.input[0]).parents().each(function () {
            isFixed |= $(this).css("position") === "fixed";
            return !isFixed;
        });
        var checkedOffset = $.datepicker._checkOffset(inst, offset, isFixed);
        inst.dpDiv.css({top: checkedOffset.top + "px"});
    };

    $.datepicker._generateMonthYearHeader = function (inst, drawMonth, drawYear, minDate, maxDate, secondary, monthNames, monthNamesShort) {

        var inMinYear, inMaxYear, month, years, thisYear, determineYear, year, endYear,
                changeMonth = this._get(inst, "changeMonth"),
                changeYear = this._get(inst, "changeYear"),
                showMonthAfterYear = this._get(inst, "showMonthAfterYear"),
                html = "<div class='ui-datepicker-title'>",
                monthHtml = "";

        // month selection
        if (secondary || !changeMonth) {
            monthHtml += "<span class='ui-datepicker-month' aria-label='select month'>" + monthNames[drawMonth] + "</span>";
        } else {
            inMinYear = (minDate && minDate.getFullYear() === drawYear);
            inMaxYear = (maxDate && maxDate.getFullYear() === drawYear);
            monthHtml += "<select class='ui-datepicker-month' data-handler='selectMonth' data-event='change' aria-label='select month'>";
            for (month = 0; month < 12; month++) {
                if ((!inMinYear || month >= minDate.getMonth()) && (!inMaxYear || month <= maxDate.getMonth())) {
                    monthHtml += "<option value='" + month + "'" +
                            (month === drawMonth ? " selected='selected'" : "") +
                            ">" + monthNamesShort[month] + "</option>";
                }
            }
            monthHtml += "</select>";
        }

        if (!showMonthAfterYear) {
            html += monthHtml + (secondary || !(changeMonth && changeYear) ? "&#xa0;" : "");
        }

        // year selection
        if (!inst.yearshtml) {
            inst.yearshtml = "";
            if (secondary || !changeYear) {
                html += "<span class='ui-datepicker-year' aria-label='select year'>" + drawYear + "</span>";
            } else {
                // determine range of years to display
                years = this._get(inst, "yearRange").split(":");
                thisYear = new Date().getFullYear();
                determineYear = function (value) {
                    var year = (value.match(/c[+\-].*/) ? drawYear + parseInt(value.substring(1), 10) :
                            (value.match(/[+\-].*/) ? thisYear + parseInt(value, 10) :
                                    parseInt(value, 10)));
                    return (isNaN(year) ? thisYear : year);
                };
                year = determineYear(years[0]);
                endYear = Math.max(year, determineYear(years[1] || ""));
                year = (minDate ? Math.max(year, minDate.getFullYear()) : year);
                endYear = (maxDate ? Math.min(endYear, maxDate.getFullYear()) : endYear);
                inst.yearshtml += "<select class='ui-datepicker-year' data-handler='selectYear' data-event='change' aria-label='select year'>";
                for (; year <= endYear; year++) {
                    inst.yearshtml += "<option value='" + year + "'" +
                            (year === drawYear ? " selected='selected'" : "") +
                            ">" + year + "</option>";
                }
                inst.yearshtml += "</select>";

                html += inst.yearshtml;
                inst.yearshtml = null;
            }
        }

        html += this._get(inst, "yearSuffix");
        if (showMonthAfterYear) {
            html += (secondary || !(changeMonth && changeYear) ? "&#xa0;" : "") + monthHtml;
        }
        html += "</div>"; // Close datepicker_header
        return html;
    };
    
    $.datepicker._updateDatepicker = function (inst) {

        // don't popup the datepicker if there is another instance already opened
        var input = inst.input[0];
        if ($.datepicker._curInst && $.datepicker._curInst !== inst && $.datepicker._datepickerShowing && $.datepicker._lastInput !== input) {
                return;
        }

        if (typeof(inst.stay_open) !== 'boolean' || inst.stay_open === false) {
                var $this = this;
                setTimeout( function() {
                    $this._base_updateDatepicker(inst);

                    // Reload the time control when changing something in the input text field.
                    var tp_inst = $this._get(inst, 'timepicker');
                    if (tp_inst) {
                            tp_inst._addTimePicker(inst);
                    }

                    $this._updateDatePickerPosition(inst);
                }, 0);
        }
    };
})();




(function () {
    $.fn.extend({
        focus: (function (orig) {
            return function (delay, fn) {
                return typeof delay === "number" ?
                        this.each(function () {
                            var elem = this;
                            setTimeout(function () {
                                $(elem).focus();
                                if (fn) {
                                    fn.call(elem);
                                }
                            }, delay);
                        }) :
                        orig.apply(this, arguments);
            };
        })($.fn.focus),

        disableSelection: (function () {
            var eventType = "onselectstart" in document.createElement("div") ?
                    "selectstart" :
                    "mousedown";

            return function () {
                return this.bind(eventType + ".ui-disableSelection", function (event) {
                    event.preventDefault();
                });
            };
        })(),

        enableSelection: function () {
            return this.unbind(".ui-disableSelection");
        },

        zIndex: function (zIndex) {
            if (zIndex !== undefined) {
                return this.css("zIndex", zIndex);
            }

            if (this.length) {
                var elem = $(this[ 0 ]), position, value;
                while (elem.length && elem[ 0 ] !== document) {
                    // Ignore z-index if position is set to a value where z-index is ignored by the browser
                    // This makes behavior of this function consistent across browsers
                    // WebKit always returns auto if the element is positioned
                    position = elem.css("position");
                    if (position === "absolute" || position === "relative" || position === "fixed") {
                        // IE returns 0 when zIndex is not specified
                        // other browsers return a string
                        // we ignore the case of nested elements with an explicit value of 0
                        // <div style="z-index: -10;"><div style="z-index: 0;"></div></div>
                        value = parseInt(elem.css("zIndex"), 10);
                        if (!isNaN(value) && value !== 0) {
                            return value;
                        }
                    }
                    elem = elem.parent();
                }
            }

            return 0;
        }
    });
})();

// GitHub PrimeFaces #3675 performance
$.widget( "ui.sortable", $.ui.sortable, {
    _setHandleClassName: function() {
        this._removeClass( this.element.find( ".ui-sortable-handle" ), "ui-sortable-handle" );
        $.each( this.items, function() {
                        (this.instance.options.handle 
                        ? this.item.find( this.instance.options.handle ) 
                        : this.item
                        ).addClass('ui-sortable-handle');
        } );
    }
});

(function() {
    $.extend(Object.getPrototypeOf($.timepicker), {

            _updateDateTime: function (dp_inst) {
                dp_inst = this.inst || dp_inst;
                var dtTmp = (dp_inst.currentYear > 0?
                        new Date(dp_inst.currentYear, dp_inst.currentMonth, dp_inst.currentDay) :
                            new Date(dp_inst.selectedYear, dp_inst.selectedMonth, dp_inst.selectedDay)),
                            dt = $.datepicker._daylightSavingAdjust(dtTmp),
                            dateFmt = $.datepicker._get(dp_inst, 'dateFormat'),
                            formatCfg = $.datepicker._getFormatConfig(dp_inst),
                            timeAvailable = dt !== null && this.timeDefined;
                this.formattedDate = $.datepicker.formatDate(dateFmt, (dt === null ? new Date() : dt), formatCfg);
                var formattedDateTime = this.formattedDate;

                // if a slider was changed but datepicker doesn't have a value yet, set it
                var originalValue = dp_inst.lastVal;
                if (originalValue === "") {
                    dp_inst.currentYear = dp_inst.selectedYear;
                    dp_inst.currentMonth = dp_inst.selectedMonth;
                    dp_inst.currentDay = dp_inst.selectedDay;
                }

                if (this._defaults.timeOnly === true && this._defaults.timeOnlyShowDate === false) {
                    formattedDateTime = this.formattedTime;
                } else if ((this._defaults.timeOnly !== true && (this._defaults.alwaysSetTime || timeAvailable)) || (this._defaults.timeOnly === true && this._defaults.timeOnlyShowDate === true)) {
                    formattedDateTime += this._defaults.separator + this.formattedTime + this._defaults.timeSuffix;
                }

                this.formattedDateTime = formattedDateTime;

                if (!this._defaults.showTimepicker) {
                    this.$input.val(this.formattedDate);
                } else if (this.$altInput && this._defaults.timeOnly === false && this._defaults.altFieldTimeOnly === true) {
                    this.$altInput.val(this.formattedTime);
                    this.$input.val(this.formattedDate);
                } else if (this.$altInput) {
                    this.$input.val(formattedDateTime);
                    var altFormattedDateTime = '',
                    altSeparator = this._defaults.altSeparator !== null ? this._defaults.altSeparator : this._defaults.separator,
                            altTimeSuffix = this._defaults.altTimeSuffix !== null ? this._defaults.altTimeSuffix : this._defaults.timeSuffix;

                    if (!this._defaults.timeOnly) {
                        if (this._defaults.altFormat) {
                            altFormattedDateTime = $.datepicker.formatDate(this._defaults.altFormat, (dt === null ? new Date() : dt), formatCfg);
                        }
                        else {
                            altFormattedDateTime = this.formattedDate;
                        }

                        if (altFormattedDateTime) {
                            altFormattedDateTime += altSeparator;
                        }
                    }

                    if (this._defaults.altTimeFormat !== null) {
                        altFormattedDateTime += $.datepicker.formatTime(this._defaults.altTimeFormat, this, this._defaults) + altTimeSuffix;
                    }
                    else {
                        altFormattedDateTime += this.formattedTime + altTimeSuffix;
                    }
                    this.$altInput.val(altFormattedDateTime);
                } else {
                    this.$input.val(formattedDateTime);
                }

                if (originalValue != formattedDateTime) {
                    this.$input.trigger("change"); // PrimeFaces https://github.com/primefaces/primefaces/issues/2811
                }
            },
            
            // PrimeFaces https://github.com/primefaces/primefaces/issues/3765
            _addTimePicker: function (dp_inst) {
                var currDT = $.trim((this.$altInput && this._defaults.altFieldTimeOnly) ? this.$input.val() + ' ' + this.$altInput.val() : (dp_inst.inline ? this.$input.next().val() : this.$input.val()));

                this.timeDefined = this._parseTime(currDT);
                this._limitMinMaxDateTime(dp_inst, false);
                this._injectTimePicker();
                this._afterInject();
            },

            _controls: {
                // slider methods
                slider: {
                        create: function (tp_inst, obj, unit, val, min, max, step) {
                                var rtl = tp_inst._defaults.isRTL; // if rtl go -60->0 instead of 0->60
                                return obj.prop('slide', null).slider({
                                        orientation: "horizontal",
                                        value: rtl ? val * -1 : val,
                                        min: rtl ? max * -1 : min,
                                        max: rtl ? min * -1 : max,
                                        step: step,
                                        slide: function (event, ui) {
                                                tp_inst.control.value(tp_inst, $(this), unit, rtl ? ui.value * -1 : ui.value);
                                                tp_inst._onTimeChange();
                                        },
                                        stop: function (event, ui) {
                                                tp_inst._onSelectHandler();
                                        }
                                });
                        },
                        options: function (tp_inst, obj, unit, opts, val) {
                                if (tp_inst._defaults.isRTL) {
                                        if (typeof(opts) === 'string') {
                                                if (opts === 'min' || opts === 'max') {
                                                        if (val !== undefined) {
                                                                return obj.slider(opts, val * -1);
                                                        }
                                                        return Math.abs(obj.slider(opts));
                                                }
                                                return obj.slider(opts);
                                        }
                                        var min = opts.min,
                                                max = opts.max;
                                        opts.min = opts.max = null;
                                        if (min !== undefined) {
                                                opts.max = min * -1;
                                        }
                                        if (max !== undefined) {
                                                opts.min = max * -1;
                                        }
                                        return obj.slider(opts);
                                }
                                if (typeof(opts) === 'string' && val !== undefined) {
                                        return obj.slider(opts, val);
                                }
                                return obj.slider(opts);
                        },
                        value: function (tp_inst, obj, unit, val) {
                                if (tp_inst._defaults.isRTL) {
                                        if (val !== undefined) {
                                                return obj.slider('value', val * -1);
                                        }
                                        return Math.abs(obj.slider('value'));
                                }
                                if (val !== undefined) {
                                        return obj.slider('value', val);
                                }
                                return obj.slider('value');
                        }
                },
                // select methods
                select: {
                        create: function (tp_inst, obj, unit, val, min, max, step) {
                                var sel = '<select class="ui-timepicker-select ui-state-default ui-corner-all" data-unit="' + unit + '" data-min="' + min + '" data-max="' + max + '" data-step="' + step + '" aria-label="select ' + unit + '">',
                                        format = tp_inst._defaults.pickerTimeFormat || tp_inst._defaults.timeFormat;

                                for (var i = min; i <= max; i += step) {
                                        sel += '<option value="' + i + '"' + (i === val ? ' selected' : '') + '>';
                                        if (unit === 'hour') {
                                                sel += $.datepicker.formatTime($.trim(format.replace(/[^ht ]/ig, '')), {hour: i}, tp_inst._defaults);
                                        }
                                        else if (unit === 'millisec' || unit === 'microsec' || i >= 10) { sel += i; }
                                        else {sel += '0' + i.toString(); }
                                        sel += '</option>';
                                }
                                sel += '</select>';

                                obj.children('select').remove();

                                $(sel).appendTo(obj).change(function (e) {
                                        tp_inst._onTimeChange();
                                        tp_inst._onSelectHandler();
                                        tp_inst._afterInject();
                                });

                                return obj;
                        },
                        options: function (tp_inst, obj, unit, opts, val) {
                                var o = {},
                                        $t = obj.children('select');
                                if (typeof(opts) === 'string') {
                                        if (val === undefined) {
                                                return $t.data(opts);
                                        }
                                        o[opts] = val;
                                }
                                else { o = opts; }
                                return tp_inst.control.create(tp_inst, obj, $t.data('unit'), $t.val(), o.min>=0 ? o.min : $t.data('min'), o.max || $t.data('max'), o.step || $t.data('step'));
                        },
                        value: function (tp_inst, obj, unit, val) {
                                var $t = obj.children('select');
                                if (val !== undefined) {
                                        return $t.val(val);
                                }
                                return $t.val();
                        }
                }
        } // end _controls
    });
})();

