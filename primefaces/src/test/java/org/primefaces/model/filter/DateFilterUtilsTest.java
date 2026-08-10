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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateFilterUtilsTest {

    private static final LocalDate DATE = LocalDate.of(2026, Month.AUGUST, 11);

    @Test
    void toLocalDate_null_returnsNull() {
        assertNull(DateFilterUtils.toLocalDate(null));
    }

    @Test
    void toLocalDate_unsupportedType_returnsNull() {
        assertNull(DateFilterUtils.toLocalDate("2026-08-11"));
        assertNull(DateFilterUtils.toLocalDate(42));
    }

    @Test
    void toLocalDate_localDate_returnsItself() {
        assertEquals(DATE, DateFilterUtils.toLocalDate(DATE));
    }

    @Test
    void toLocalDate_localDateTime_truncatesTimeOfDay() {
        assertEquals(DATE, DateFilterUtils.toLocalDate(LocalDateTime.of(DATE, java.time.LocalTime.of(23, 59))));
    }

    @Test
    void toLocalDate_sqlDate() {
        assertEquals(DATE, DateFilterUtils.toLocalDate(java.sql.Date.valueOf(DATE)));
    }

    @Test
    void toLocalDate_javaUtilDate() {
        Date date = Date.from(DATE.atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(DATE, DateFilterUtils.toLocalDate(date));
    }

    @Test
    void toLocalDate_timestamp() {
        Timestamp timestamp = Timestamp.from(DATE.atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(DATE, DateFilterUtils.toLocalDate(timestamp));
    }

    @Test
    void toLocalDate_calendar() {
        Calendar calendar = new GregorianCalendar(2026, Calendar.AUGUST, 11);
        assertEquals(DATE, DateFilterUtils.toLocalDate(calendar));
    }

    @Test
    void toLocalDate_instant() {
        Instant instant = DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        assertEquals(DATE, DateFilterUtils.toLocalDate(instant));
    }

    @Test
    void startOfWeek_iso_isMonday() {
        // 2026-08-11 is a Tuesday
        assertEquals(LocalDate.of(2026, Month.AUGUST, 10), DateFilterUtils.startOfWeek(DATE, Locale.GERMANY));
    }

    @Test
    void startOfWeek_us_isSunday() {
        assertEquals(LocalDate.of(2026, Month.AUGUST, 9), DateFilterUtils.startOfWeek(DATE, Locale.US));
    }

    @Test
    void startAndEndOfMonth() {
        assertEquals(LocalDate.of(2026, Month.AUGUST, 1), DateFilterUtils.startOfMonth(DATE));
        assertEquals(LocalDate.of(2026, Month.AUGUST, 31), DateFilterUtils.endOfMonth(DATE));
    }

    @Test
    void endOfMonth_february_leapYear() {
        assertEquals(LocalDate.of(2024, Month.FEBRUARY, 29), DateFilterUtils.endOfMonth(LocalDate.of(2024, Month.FEBRUARY, 10)));
    }

    @Test
    void endOfMonth_february_nonLeapYear() {
        assertEquals(LocalDate.of(2026, Month.FEBRUARY, 28), DateFilterUtils.endOfMonth(LocalDate.of(2026, Month.FEBRUARY, 10)));
    }

    @Test
    void startAndEndOfQuarter_q3() {
        // August is in Q3 (Jul-Sep)
        assertEquals(LocalDate.of(2026, Month.JULY, 1), DateFilterUtils.startOfQuarter(DATE));
        assertEquals(LocalDate.of(2026, Month.SEPTEMBER, 30), DateFilterUtils.endOfQuarter(DATE));
    }

    @Test
    void startAndEndOfQuarter_q1() {
        LocalDate februaryDate = LocalDate.of(2026, Month.FEBRUARY, 15);
        assertEquals(LocalDate.of(2026, Month.JANUARY, 1), DateFilterUtils.startOfQuarter(februaryDate));
        assertEquals(LocalDate.of(2026, Month.MARCH, 31), DateFilterUtils.endOfQuarter(februaryDate));
    }

    @Test
    void startAndEndOfQuarter_q4() {
        LocalDate decemberDate = LocalDate.of(2026, Month.DECEMBER, 25);
        assertEquals(LocalDate.of(2026, Month.OCTOBER, 1), DateFilterUtils.startOfQuarter(decemberDate));
        assertEquals(LocalDate.of(2026, Month.DECEMBER, 31), DateFilterUtils.endOfQuarter(decemberDate));
    }

    @Test
    void startAndEndOfYear() {
        assertEquals(LocalDate.of(2026, Month.JANUARY, 1), DateFilterUtils.startOfYear(DATE));
        assertEquals(LocalDate.of(2026, Month.DECEMBER, 31), DateFilterUtils.endOfYear(DATE));
    }
}
