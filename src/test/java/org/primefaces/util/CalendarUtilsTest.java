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

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.primefaces.component.datepicker.DatePicker;

import javax.el.ELContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CalendarUtilsTest {

    private DatePicker datePicker;
    private FacesContext context;
    private ExternalContext externalContext;
    private ELContext elContext;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        datePicker = mock(DatePicker.class);
        when(datePicker.getSelectionMode()).thenReturn("single");

        context = mock(FacesContext.class);
        externalContext = mock(ExternalContext.class);
        elContext = mock(ELContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(context.getELContext()).thenReturn(elContext);
    }

    @After
    public void teardown() {
        datePicker = null;
        context = null;
        externalContext = null;
        elContext = null;
    }

    private void setupValues(Object value, Locale locale) {
        when(datePicker.getValue()).thenReturn(value);
        when(datePicker.calculatePattern()).thenCallRealMethod();
        when(datePicker.calculateLocale(any())).thenReturn(locale);
        when(datePicker.calculateTimeOnlyPattern()).thenReturn("HH:mm");
    }

    @Test
    public void getValueAsString_LocalDate() {
        LocalDate localDate = LocalDate.of(2019, 7, 23);
        setupValues(localDate, Locale.ENGLISH);

        String value = CalendarUtils.getValueAsString (context, datePicker);
        assertEquals("7/23/19", value);
    }

    @Test
    public void getValueAsString_LocalDate_German() {
        LocalDate localDate = LocalDate.of(2019, 7, 23);
        setupValues(localDate, Locale.GERMAN);

        String value = CalendarUtils.getValueAsString (context, datePicker);
        assertEquals("23.07.19", value);
    }

    @Test
    public void getValueAsString_Date() {
        Calendar cal = new GregorianCalendar();
        cal.set(2019, 6, 23);
        Date date = cal.getTime();
        setupValues(date, Locale.ENGLISH);

        String value = CalendarUtils.getValueAsString (context, datePicker);
        assertEquals("7/23/19", value);
    }

    @Test
    public void getValueAsString_Date_German() {
        Calendar cal = new GregorianCalendar();
        cal.set(2019, 6, 23);
        Date date = cal.getTime();
        setupValues(date, Locale.GERMAN);

        String value = CalendarUtils.getValueAsString (context, datePicker);
        assertEquals("23.07.19", value);
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
}
