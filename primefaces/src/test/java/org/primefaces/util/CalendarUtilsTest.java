/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.util;

import org.primefaces.component.datepicker.DatePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import jakarta.el.ELContext;
import jakarta.faces.FacesException;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalendarUtilsTest {

    private DatePicker datePicker;
    private FacesContext context;
    private ExternalContext externalContext;
    private ELContext elContext;

    @BeforeEach
    void setup() {
        datePicker = mock(DatePicker.class);
        when(datePicker.getSelectionMode()).thenReturn("single");

        context = mock(FacesContext.class);
        externalContext = mock(ExternalContext.class);
        elContext = mock(ELContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(context.getELContext()).thenReturn(elContext);
    }

    @AfterEach
    void teardown() {
        datePicker = null;
        context = null;
        externalContext = null;
        elContext = null;
    }

    private void setupValues(Object value, Locale locale) {
        when(datePicker.getValue()).thenReturn(value);
        when(datePicker.calculatePattern()).thenCallRealMethod();
        when(datePicker.calculateTimeOnlyPattern()).thenCallRealMethod();
        when(datePicker.calculateWidgetPattern()).thenCallRealMethod();
        when(datePicker.calculateLocalizedPattern()).thenCallRealMethod();
        when(datePicker.calculateLocale(any())).thenReturn(locale);
    }

    @Test
    void getValueAsString_LocalDate() {
        LocalDate localDate = LocalDate.of(2019, 7, 23);
        setupValues(localDate, Locale.ENGLISH);

        String value = CalendarUtils.getValueAsString(context, datePicker);
        assertEquals("7/23/2019", value);
    }

    @Test
    void getValueAsString_LocalDate_German() {
        LocalDate localDate = LocalDate.of(2019, 7, 23);
        setupValues(localDate, Locale.GERMAN);

        String value = CalendarUtils.getValueAsString(context, datePicker);
        assertEquals("23.07.2019", value);
    }

    @Test
    void getValueAsString_Date() {
        Calendar cal = new GregorianCalendar();
        cal.set(2019, 6, 23);
        Date date = cal.getTime();
        setupValues(date, Locale.ENGLISH);

        String value = CalendarUtils.getValueAsString(context, datePicker);
        assertEquals("7/23/2019", value);
    }

    @Test
    void getValueAsString_Date_German() {
        Calendar cal = new GregorianCalendar();
        cal.set(2019, 6, 23);
        Date date = cal.getTime();
        setupValues(date, Locale.GERMAN);

        String value = CalendarUtils.getValueAsString(context, datePicker);
        assertEquals("23.07.2019", value);
    }

    @Test
    void calculateZoneId_String() {
        String timeZone = "GMT+2";
        ZoneId calculateZoneId = CalendarUtils.calculateZoneId(timeZone);
        assertEquals(ZoneId.of("GMT+2"), calculateZoneId);
    }

    @Test
    void calculateZoneId_ZoneId() {
        ZoneId zoneId = ZoneId.of("GMT+2");
        ZoneId calculateZoneId = CalendarUtils.calculateZoneId(zoneId);
        assertEquals(ZoneId.of("GMT+2"), calculateZoneId);
    }

    @Test
    void calculateZoneId_TimeZone() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
        ZoneId calculateZoneId = CalendarUtils.calculateZoneId(timeZone);
        assertEquals(ZoneId.of("GMT+2"), calculateZoneId);
    }

    @Test
    void removeTime() {
        String pattern = "dd/MM/yy";
        String cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals(pattern, cleanedPattern);
        pattern = "MM/dd/yy HH:mm";
        cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals("MM/dd/yy", cleanedPattern);
        pattern = "dd.MM.yyyy KK:mm a";
        cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals("dd.MM.yyyy", cleanedPattern);
        pattern = "HH:mm:ss";
        cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals("", cleanedPattern);
        pattern = "HH:mm:ss dd/MM/yyyy";
        cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals("", cleanedPattern);
    }

    @Test
    void now_LocalDate() {
        when(datePicker.hasTime()).thenReturn(false);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        Temporal now = CalendarUtils.now(datePicker);
        assertEquals(LocalDate.now(), now);
    }

    @Test
    void now_LocalDateTime() {
        when(datePicker.hasTime()).thenReturn(true);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        LocalDateTime now = (LocalDateTime) CalendarUtils.now(datePicker);
        assertTrue(LocalDateTime.now().compareTo(now) >= 0);
    }

    @Test
    void now_DateTime() {
        when(datePicker.hasTime()).thenReturn(true);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        Date now = (Date) CalendarUtils.now(datePicker, java.util.Date.class);
        assertTrue(new Date().compareTo(now) >= 0);
    }

    @Test
    void now_Date() {
        when(datePicker.hasTime()).thenReturn(false);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        Date now = (Date) CalendarUtils.now(datePicker, java.util.Date.class);
        assertTrue(new Date().compareTo(now) >= 0);
    }

    @Test
    void now_Time() {
        when(datePicker.hasTime()).thenReturn(true);
        when(datePicker.isTimeOnly()).thenReturn(true);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        Date now = (Date) CalendarUtils.now(datePicker, java.util.Date.class);
        assertTrue(new Date().compareTo(now) >= 0);
    }

    @Test
    void convertPattern() {
        assertNull(CalendarUtils.convertPattern(null));
        assertEquals("", CalendarUtils.convertPattern(""));
        assertEquals("mm/dd/yy HH:mm:ss", CalendarUtils.convertPattern("MM/dd/yyyy HH:mm:ss"));
        // more in-depth tests are in DateTimePatternConverterTest
    }

    @Test
    void splitRange() {
        List<String> splitRange = CalendarUtils.splitRange("2021-03-01 - 2021-03-31", "yyyy-MM-dd", "-", false);
        assertEquals(2, splitRange.size());
        assertEquals("2021-03-01", splitRange.get(0));
        assertEquals("2021-03-31", splitRange.get(1));

        splitRange = CalendarUtils.splitRange("2021-03-01", "yyyy-MM-dd", "-", false);
        assertTrue(splitRange.isEmpty());

        splitRange = CalendarUtils.splitRange("", "yyyy-MM-dd", "-", false);
        assertTrue(splitRange.isEmpty());

        assertThrows(FacesException.class, () -> {
            CalendarUtils.splitRange("2021 - 03 - 01 - 2021 - 03 - 31", "yyyy - MM - dd", "-", false);
        });
    }

    @Test
    void splitRangeWeek() {
        List<String> splitRange = CalendarUtils.splitRange("2021-03-01 - 2021-03-31 (Wk 21)", "yyyy-MM-dd", "-", true);
        assertEquals(2, splitRange.size());
        assertEquals("2021-03-01", splitRange.get(0));
        assertEquals("2021-03-31", splitRange.get(1));

        splitRange = CalendarUtils.splitRange("2021-03-01 (Wk 21)", "yyyy-MM-dd", "-", true);
        assertTrue(splitRange.isEmpty());

        splitRange = CalendarUtils.splitRange("", "yyyy-MM-dd (Wk 21)", "-", true);
        assertTrue(splitRange.isEmpty());

        splitRange = CalendarUtils.splitRange("2021-03-01 - 2021-03-31 (Wk NaN)", "yyyy-MM-dd", "-", true);
        assertEquals(2, splitRange.size());
        assertEquals("2021-03-01", splitRange.get(0));
        assertEquals("2021-03-31", splitRange.get(1));

        splitRange = CalendarUtils.splitRange("2021-03-01 - 2021-03-31", "yyyy-MM-dd", "-", true);
        assertEquals(2, splitRange.size());
        assertEquals("2021-03-01", splitRange.get(0));
        assertEquals("2021-03-31", splitRange.get(1));

        assertThrows(FacesException.class, () -> {
            CalendarUtils.splitRange("2021 - 03 - 01 - 2021 - 03 - 31 (Wk 21)", "yyyy - MM - dd", "-", true);
        });
    }

}
