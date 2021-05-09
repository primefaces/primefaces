/*
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.*;

import javax.el.ELContext;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.component.datepicker.DatePicker;

public class CalendarUtilsTest {

    private DatePicker datePicker;
    private FacesContext context;
    private ExternalContext externalContext;
    private ELContext elContext;


    @BeforeEach
    public void setup() {
        datePicker = mock(DatePicker.class);
        when(datePicker.getSelectionMode()).thenReturn("single");

        context = mock(FacesContext.class);
        externalContext = mock(ExternalContext.class);
        elContext = mock(ELContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(context.getELContext()).thenReturn(elContext);
    }

    @AfterEach
    public void teardown() {
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
    public void getValueAsString_LocalDate() {
        LocalDate localDate = LocalDate.of(2019, 7, 23);
        setupValues(localDate, Locale.ENGLISH);

        String value = CalendarUtils.getValueAsString (context, datePicker);
        assertEquals("7/23/2019", value);
    }

    @Test
    public void getValueAsString_LocalDate_German() {
        LocalDate localDate = LocalDate.of(2019, 7, 23);
        setupValues(localDate, Locale.GERMAN);

        String value = CalendarUtils.getValueAsString (context, datePicker);
        assertEquals("23.07.2019", value);
    }

    @Test
    public void getValueAsString_Date() {
        Calendar cal = new GregorianCalendar();
        cal.set(2019, 6, 23);
        Date date = cal.getTime();
        setupValues(date, Locale.ENGLISH);

        String value = CalendarUtils.getValueAsString (context, datePicker);
        assertEquals("7/23/2019", value);
    }

    @Test
    public void getValueAsString_Date_German() {
        Calendar cal = new GregorianCalendar();
        cal.set(2019, 6, 23);
        Date date = cal.getTime();
        setupValues(date, Locale.GERMAN);

        String value = CalendarUtils.getValueAsString (context, datePicker);
        assertEquals("23.07.2019", value);
    }

    @Test
    public void calculateZoneId_String() {
        String timeZone = "GMT+2";
        ZoneId calculateZoneId = CalendarUtils.calculateZoneId(timeZone);
        assertEquals(ZoneId.of("GMT+2"), calculateZoneId);
    }

    @Test
    public void calculateZoneId_ZoneId() {
        ZoneId zoneId = ZoneId.of("GMT+2");
        ZoneId calculateZoneId = CalendarUtils.calculateZoneId(zoneId);
        assertEquals(ZoneId.of("GMT+2"), calculateZoneId);
    }

    @Test
    public void calculateZoneId_TimeZone() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
        ZoneId calculateZoneId = CalendarUtils.calculateZoneId(timeZone);
        assertEquals(ZoneId.of("GMT+2"), calculateZoneId);
    }

    @Test
    public void removeTime() {
        String pattern = "dd/MM/yy";
        String cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals(cleanedPattern, pattern);
        pattern = "MM/dd/yy HH:mm";
        cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals(cleanedPattern, "MM/dd/yy");
        pattern = "dd.MM.yyyy KK:mm a";
        cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals(cleanedPattern, "dd.MM.yyyy");
        pattern = "HH:mm:ss";
        cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals(cleanedPattern, "");
        pattern = "HH:mm:ss dd/MM/yyyy";
        cleanedPattern = CalendarUtils.removeTime(pattern);
        assertEquals(cleanedPattern, "");
    }

    @Test
    public void now_LocalDate() {
        when(datePicker.hasTime()).thenReturn(false);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        Temporal now = CalendarUtils.now(datePicker);
        assertEquals(LocalDate.now(), now);
    }

    @Test
    public void now_LocalDateTime() {
        when(datePicker.hasTime()).thenReturn(true);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        LocalDateTime now = (LocalDateTime) CalendarUtils.now(datePicker);
        assertTrue(LocalDateTime.now().compareTo(now) >= 0);
    }

    @Test
    public void now_DateTime() {
        when(datePicker.hasTime()).thenReturn(true);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        Date now = (Date) CalendarUtils.now(datePicker, java.util.Date.class);
        assertTrue(new Date().compareTo(now) >= 0);
    }

    @Test
    public void now_Date() {
        when(datePicker.hasTime()).thenReturn(false);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        Date now = (Date) CalendarUtils.now(datePicker, java.util.Date.class);
        assertTrue(new Date().compareTo(now) >= 0);
    }

    @Test
    public void now_Time() {
        when(datePicker.hasTime()).thenReturn(true);
        when(datePicker.isTimeOnly()).thenReturn(true);
        when(datePicker.getTimeZone()).thenReturn(ZoneId.systemDefault());
        Date now = (Date) CalendarUtils.now(datePicker, java.util.Date.class);
        assertTrue(new Date().compareTo(now) >= 0);
    }

    @Test
    public void convertPattern() {
        assertNull(CalendarUtils.convertPattern(null));
        assertEquals("", CalendarUtils.convertPattern(""));
        assertEquals("mm/dd/yy HH:mm:ss", CalendarUtils.convertPattern("MM/dd/yyyy HH:mm:ss"));
        // more in-depth tests are in DateTimePatternConverterTest
    }

    @Test
    public void splitRange() {
        List<String> splitRange = CalendarUtils.splitRange("2021-03-01 - 2021-03-31", "yyyy-MM-dd", "-");
        assertEquals(2, splitRange.size());
        assertEquals("2021-03-01", splitRange.get(0));
        assertEquals("2021-03-31", splitRange.get(1));
        splitRange = CalendarUtils.splitRange("2021-03-01", "yyyy-MM-dd", "-");
        assertTrue(splitRange.isEmpty());
        splitRange = CalendarUtils.splitRange("", "yyyy-MM-dd", "-");
        assertTrue(splitRange.isEmpty());

        Assertions.assertThrows(FacesException.class, () -> {
            CalendarUtils.splitRange("2021 - 03 - 01 - 2021 - 03 - 31", "yyyy - MM - dd", "-");
        });
    }

}
