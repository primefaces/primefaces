/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.model.filter;

import org.primefaces.util.CalendarUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Normalizes the various date/time types a bean's filtered field might use ({@link LocalDate},
 * {@link LocalDateTime}, {@link Date}, {@link java.sql.Date}, {@link java.sql.Timestamp}, {@link Calendar},
 * {@link Instant}) into a single day-granular {@link LocalDate}, so the relative-date match modes
 * ("today", "this week", "last N days", ...) can compare them uniformly regardless of which type the
 * developer's model uses. See GitHub #7427.
 */
final class DateFilterUtils {

    private DateFilterUtils() {
        // NOOP
    }

    /**
     * @param value the field value to normalize; any of the types listed above, or {@code null}
     * @return the value as a {@link LocalDate} (using the system default time zone for zone-aware types),
     *         or {@code null} if {@code value} is {@code null} or not a recognized date/time type
     */
    static LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate();
        }
        if (value instanceof Instant) {
            return ((Instant) value).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (value instanceof Calendar) {
            return CalendarUtils.convertDate2LocalDate(((Calendar) value).getTime());
        }
        if (value instanceof Date) {
            return CalendarUtils.convertDate2LocalDate((Date) value);
        }
        return null;
    }

    /**
     * Normalizes into a moment-in-time {@link LocalDateTime}, for "last/next N minutes/hours" on a field that
     * has both a date and a time component. A bare {@link LocalTime} has no date to attach, so it deliberately
     * returns {@code null} here - see {@link #toLocalTime(Object)} for the cyclic (time-of-day only) case instead.
     *
     * @return the value as a {@link LocalDateTime}, or {@code null} if {@code value} is {@code null}, a bare
     *         {@link LocalTime}, or not a recognized date/time type
     */
    static LocalDateTime toLocalDateTime(Object value) {
        if (value == null || value instanceof LocalTime) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).atStartOfDay();
        }
        if (value instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) value, ZoneId.systemDefault());
        }
        if (value instanceof Calendar) {
            return CalendarUtils.convertDate2LocalDateTime(((Calendar) value).getTime());
        }
        if (value instanceof Date) {
            return CalendarUtils.convertDate2LocalDateTime((Date) value);
        }
        return null;
    }

    /**
     * Normalizes into a time-of-day {@link LocalTime} - a cyclic 24h clock with no date attached, unlike
     * {@link #toLocalDateTime(Object)}. A {@link LocalDateTime}/{@link Date}/etc has its date component
     * discarded, keeping only the time of day.
     *
     * @return the value's time of day as a {@link LocalTime}, or {@code null} if {@code value} is {@code null},
     *         a bare {@link LocalDate} (no time component to extract), or not a recognized date/time type
     */
    static LocalTime toLocalTime(Object value) {
        if (value == null || value instanceof LocalDate) {
            return null;
        }
        if (value instanceof LocalTime) {
            return (LocalTime) value;
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalTime();
        }
        if (value instanceof Instant) {
            return ((Instant) value).atZone(ZoneId.systemDefault()).toLocalTime();
        }
        if (value instanceof Calendar) {
            return CalendarUtils.convertDate2LocalTime(((Calendar) value).getTime());
        }
        if (value instanceof Date) {
            return CalendarUtils.convertDate2LocalTime((Date) value);
        }
        return null;
    }

    /**
     * The first day (locale-aware, e.g. Monday for ISO / Sunday for US) of the calendar week containing {@code date}.
     */
    static LocalDate startOfWeek(LocalDate date, Locale locale) {
        WeekFields weekFields = WeekFields.of(locale != null ? locale : Locale.getDefault());
        return date.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));
    }

    static LocalDate startOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    static LocalDate endOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * The first day of the calendar quarter (Jan-Mar, Apr-Jun, Jul-Sep, Oct-Dec) containing {@code date}.
     * {@code java.time} has no built-in quarter support, so this is computed manually.
     */
    static LocalDate startOfQuarter(LocalDate date) {
        int quarterStartMonth = ((date.getMonthValue() - 1) / 3) * 3 + 1;
        return LocalDate.of(date.getYear(), quarterStartMonth, 1);
    }

    static LocalDate endOfQuarter(LocalDate date) {
        return startOfQuarter(date).plusMonths(3).minusDays(1);
    }

    static LocalDate startOfYear(LocalDate date) {
        return LocalDate.of(date.getYear(), 1, 1);
    }

    static LocalDate endOfYear(LocalDate date) {
        return LocalDate.of(date.getYear(), 12, 31);
    }

    /**
     * Parses a filter value as a plain {@link Integer} - for the "last/next N days/minutes/hours" and
     * "relative date" match modes, whose value is a count rather than a date. {@code UITable} normally hands
     * this an already-converted {@link Integer} (it bypasses the column's date converter for these modes), but
     * a raw {@link String} is tolerated too, e.g. when a constraint is exercised directly. See GitHub #7427.
     *
     * @return the parsed value, or {@code null} if {@code filter} is {@code null}, blank, or not parsable
     */
    static Integer toInteger(Object filter) {
        if (filter instanceof Number) {
            return ((Number) filter).intValue();
        }
        if (filter instanceof String) {
            try {
                return Integer.valueOf(((String) filter).trim());
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
