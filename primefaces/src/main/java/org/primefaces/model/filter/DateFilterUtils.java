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
}
