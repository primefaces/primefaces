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
package org.primefaces.component.datepicker;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.primefaces.component.api.UICalendar;
import org.primefaces.component.datepicker.DatePicker;
import org.primefaces.component.datepicker.DatePickerRenderer;
import org.primefaces.util.CalendarUtils;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.mockito.Mockito.*;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.*;

/*
This tests covers DatePicker and partially (due to shared code) Calendar.
 */
public class DatePickerTest {

    class MyDatePicker extends DatePicker {
        private boolean valid;

        @Override
        public boolean isValid() {
            return valid;
        }

        @Override
        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }

    class MyDatePickerRenderer extends DatePickerRenderer {
        @Override
        public Class resolveDateType(FacesContext context, UICalendar calendar) {
            return super.resolveDateType(context, calendar);
        }

        @Override
        public Temporal convertToJava8DateTimeAPI(FacesContext context, UICalendar calendar, Class type, String submittedValue) {
            return super.convertToJava8DateTimeAPI(context, calendar, type, submittedValue);
        }

        @Override
        public Date convertToLegacyDateAPI(FacesContext context, UICalendar calendar, String submittedValue) {
            return super.convertToLegacyDateAPI(context, calendar, submittedValue);
        }

        @Override
        public ConverterException createConverterException(FacesContext context, UICalendar calendar, String submittedValue, Object param1) {
            return super.createConverterException(context, calendar, submittedValue, param1);
        }
    }

    private MyDatePickerRenderer renderer;
    private MyDatePicker datePicker;
    private FacesContext context;
    private ExternalContext externalContext;
    private ELContext elContext;
    private ValueExpression valueExpression;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        renderer = mock(MyDatePickerRenderer.class);
        datePicker = mock(MyDatePicker.class);
        when(datePicker.calculatePattern()).thenCallRealMethod();
        when(datePicker.calculateTimeOnlyPattern()).thenReturn("HH:mm");
        when(datePicker.isValid()).thenCallRealMethod();
        doCallRealMethod().when(datePicker).setValid(anyBoolean());
        when(datePicker.getSelectionMode()).thenReturn("single");
        datePicker.setValid(true);

        context = mock(FacesContext.class);
        when(renderer.resolveDateType(context, datePicker)).thenCallRealMethod();
        when(renderer.convertToJava8DateTimeAPI(eq(context), eq(datePicker), any(), any())).thenCallRealMethod();
        when(renderer.convertToLegacyDateAPI(eq(context), eq(datePicker), any())).thenCallRealMethod();
        when(datePicker.validateDateValue(eq(context), any())).thenCallRealMethod();
        when(datePicker.validateValueInternal(eq(context), any())).thenCallRealMethod();

        externalContext = mock(ExternalContext.class);
        elContext = mock(ELContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(context.getELContext()).thenReturn(elContext);

        valueExpression = mock(ValueExpression.class);
        when(datePicker.getValueExpression(anyString())).thenReturn(valueExpression);
    }

    @After
    public void teardown() {
        renderer = null;
        datePicker = null;
        context = null;
        externalContext = null;
        elContext = null;
        valueExpression = null;
    }

    private void setupValues(Class type, Locale locale) {
        when(datePicker.calculateLocale(any())).thenReturn(locale);
        when(valueExpression.getType(elContext)).thenReturn(type);
    }

    @Test
    public void dateAsStringShouldBeNullIfValueIsNull() {
        org.primefaces.component.calendar.Calendar calendar = new org.primefaces.component.calendar.Calendar();
        String dateAsString = CalendarUtils.getValueAsString(null, calendar);

        assertEquals(null, dateAsString);
    }

    @Test
    public void dateAsStringShouldBeSubmittedValueIfExists() {
        org.primefaces.component.calendar.Calendar calendar = new org.primefaces.component.calendar.Calendar();
        calendar.setSubmittedValue("05.07.2010");
        String dateAsString = CalendarUtils.getValueAsString(null, calendar);

        assertEquals("05.07.2010", dateAsString);
    }

    @Test
    public void convertedValueShouldBeNullWhenEmptyStringIsSubmitted() {
        DatePicker datePicker = new DatePicker();

        Object convertedValue = renderer.getConvertedValue(null, datePicker, "");
        assertNull(convertedValue);

        convertedValue = renderer.getConvertedValue(null, datePicker, "  ");
        assertNull(convertedValue);
    }

    @Test
    public void shouldConvertPattern() {
        String pattern = "dd.MM.yyyy";
        assertEquals("dd.mm.yy", CalendarUtils.convertPattern(pattern));

        pattern = "dd/MM/yy";
        assertEquals("dd/mm/y", CalendarUtils.convertPattern(pattern));

        pattern = "d, MMM, yyyy";
        assertEquals("d, M, yy", CalendarUtils.convertPattern(pattern));

        pattern = "dd-MMMMMM-yyyy";
        assertEquals("dd-MM-yy", CalendarUtils.convertPattern(pattern));

        pattern = "dd-MM-yyyy EEE";
        assertEquals("dd-mm-yy D", CalendarUtils.convertPattern(pattern));

        pattern = "dd-MM-yyyy EEEEEE";
        assertEquals("dd-mm-yy DD", CalendarUtils.convertPattern(pattern));
    }

    @Test
    public void resolveDateType_Date() {
        setupValues(Date.class, Locale.ENGLISH);
        Class type = renderer.resolveDateType(context, datePicker);
        assertEquals(Date.class, type);
    }

    @Test
    public void resolveDateType_LocalDate() {
        setupValues(LocalDate.class, Locale.ENGLISH);
        Class type = renderer.resolveDateType(context, datePicker);
        assertEquals(LocalDate.class, type);
    }

    @Test
    public void resolveDateType_LocalTime() {
        setupValues(LocalTime.class, Locale.ENGLISH);
        Class type = renderer.resolveDateType(context, datePicker);
        assertEquals(LocalTime.class, type);
    }

    @Test
    public void resolveDateType_LocalDateTime() {
        setupValues(LocalDateTime.class, Locale.ENGLISH);
        Class type = renderer.resolveDateType(context, datePicker);
        assertEquals(LocalDateTime.class, type);
    }

    @Test
    @Ignore
    public void resolveDateType_ListLocalDate() {
		/*
			Unable to mock this with Mockito only.
			Cause:
			BaseCalendarRenderer#resolveDateType calls a static method.
			(ValueExpressionAnalyzer.getReference)
			It would require https://github.com/powermock/powermock to mock this static method.
		 */

        setupValues(List.class, Locale.ENGLISH);
        when(valueExpression.getValueReference(elContext)).thenReturn(null);
        Class type = renderer.resolveDateType(context, datePicker);
        assertEquals(LocalDate.class, type);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate() {
        Class type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "7/23/19");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 07, 23), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate_German() {
        Class type = LocalDate.class;
        setupValues(type, Locale.GERMAN);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "23.07.19");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 07, 23), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate_German_ExplicitPattern() {
        Class type = LocalDate.class;
        setupValues(type, Locale.GERMAN);
        when(datePicker.getPattern()).thenReturn("dd.MM.yyyy");

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "23.07.2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 07, 23), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalTime() {
        Class type = LocalTime.class;
        setupValues(type, Locale.ENGLISH);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "21:31");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalTime.of(21, 31), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDateTime() {
        Class type = LocalDateTime.class;
        setupValues(type, Locale.ENGLISH);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "7/23/19 21:31");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDateTime.of(2019, 7, 23,  21, 31), temporal);
    }

    @Test(expected = ConverterException.class)
    public void convertToJava8DateTimeAPI_LocalDate_WrongFormat() {
        Class type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);

        FacesMessage message=new FacesMessage("dummy");
        when(renderer.createConverterException(eq(context), eq(datePicker), any(), any())).thenReturn(new ConverterException(message));

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "23.07.2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 07, 23), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate_WrongPattern() {
        Class type = LocalDate.class;
        setupValues(type, Locale.GERMAN);
        when(datePicker.getPattern()).thenReturn("ddaMMbyyyy");

        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage(startsWith("Unknown pattern letter"));

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "23.07.2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 07, 23), temporal);
    }

    @Test
    public void getConvertedValue_Date() {
        Class type = Date.class;
        setupValues(type, Locale.ENGLISH);

        when(renderer.getConvertedValue(eq(context), eq(datePicker), any())).thenCallRealMethod();

        Object object = renderer.getConvertedValue(context, datePicker, "7/23/19");
        assertEquals(type, object.getClass());
        Date date = (Date)object;
        java.util.Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        assertEquals(2019, calendar.get(java.util.Calendar.YEAR));
        assertEquals(6, calendar.get(java.util.Calendar.MONTH));
        assertEquals(23, calendar.get(java.util.Calendar.DAY_OF_MONTH));
    }

    @Test
    public void getConvertedValue_LocalDate() {
        Class type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);

        when(renderer.getConvertedValue(eq(context), eq(datePicker), any())).thenCallRealMethod();

        Object object = renderer.getConvertedValue(context, datePicker, "7/23/19");
        assertEquals(type, object.getClass());
        LocalDate localDate = (LocalDate)object;
        assertEquals(LocalDate.of(2019, 07, 23), localDate);
    }

    @Test
    public void validateValueInternal_simple() {
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_minDate_LocalDate() {
        when(datePicker.getMindate()).thenReturn(LocalDate.of(2019, 1, 1));
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_minDate_LocalDate_wrong() {
        when(datePicker.getMindate()).thenReturn(LocalDate.of(2019, 1, 1));
        datePicker.validateValueInternal(context, LocalDate.of(2018, 7, 23));
        assertFalse(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_minDate_String() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.getMindate()).thenReturn("1/1/19");
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_minDate_String_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.getMindate()).thenReturn("1/1/19");
        datePicker.validateValueInternal(context, LocalDate.of(2018, 7, 23));
        assertFalse(datePicker.isValid());
    }

    @Test
    @Ignore
    //TODO: test breaks - fix root cause
    public void validateValueInternal_minDate_Date_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.set(2019, 0, 1);

        when(datePicker.getMindate()).thenReturn(cal.getTime());
        datePicker.validateValueInternal(context, LocalDate.of(2018, 7, 23));
        assertFalse(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_maxDate_LocalDate() {
        when(datePicker.getMaxdate()).thenReturn(LocalDate.of(2019, 12, 31));
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_maxDate_LocalDate_wrong() {
        when(datePicker.getMaxdate()).thenReturn(LocalDate.of(2019, 12, 31));
        datePicker.validateValueInternal(context, LocalDate.of(2020, 7, 23));
        assertFalse(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_maxDate_String() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.getMaxdate()).thenReturn("12/31/19");
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_maxDate_String_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.getMaxdate()).thenReturn("12/31/19");
        datePicker.validateValueInternal(context, LocalDate.of(2020, 7, 23));
        assertFalse(datePicker.isValid());
    }

    @Test
    @Ignore
    //TODO: test breaks - fix root cause
    public void validateValueInternal_maxDate_Date_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.set(2019, 11, 31);

        when(datePicker.getMaxdate()).thenReturn(cal.getTime());
        datePicker.validateValueInternal(context, LocalDate.of(2020, 7, 23));
        assertFalse(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_disabledDates_LocalDate() {
        when(datePicker.getDisabledDates()).thenReturn(Arrays.asList(LocalDate.of(2019, 7, 22), LocalDate.of(2019, 7, 24)));
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_disabledDates_LocalDate_wrong() {
        when(datePicker.getDisabledDates()).thenReturn(Arrays.asList(LocalDate.of(2019, 7, 22), LocalDate.of(2019, 7, 24)));
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 22));
        assertFalse(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_disabledDays() {
        when(datePicker.getDisabledDays()).thenReturn(Arrays.asList(0, 1));
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_disabledDays_wrong() {
        int weekDay = LocalDate.of(2019, 7, 22).getDayOfWeek().getValue();

        when(datePicker.getDisabledDays()).thenReturn(Arrays.asList(0, 1));
        datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 22));
        assertFalse(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_LocalDate_range() {
        when(datePicker.getSelectionMode()).thenReturn("range");
        List<LocalDate> range=Arrays.asList(LocalDate.of(2019, 7, 23), LocalDate.of(2019, 7, 30));
        datePicker.validateValueInternal(context, range);
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_LocalDate_range_wrong() {
        when(datePicker.getSelectionMode()).thenReturn("range");
        List<LocalDate> range=Arrays.asList(LocalDate.of(2019, 7, 30), LocalDate.of(2019, 7, 23));
        datePicker.validateValueInternal(context, range);
        assertFalse(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_Date_range() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar calFrom = GregorianCalendar.getInstance();
        calFrom.set(2019, 6, 23);
        java.util.Calendar calTo = GregorianCalendar.getInstance();
        calTo.set(2019, 6, 30);

        when(datePicker.getSelectionMode()).thenReturn("range");
        List<Date> range=Arrays.asList(calFrom.getTime(), calTo.getTime());
        datePicker.validateValueInternal(context, range);
        assertTrue(datePicker.isValid());
    }

    @Test
    public void validateValueInternal_Date_range_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar calFrom = GregorianCalendar.getInstance();
        calFrom.set(2019, 6, 30);
        java.util.Calendar calTo = GregorianCalendar.getInstance();
        calTo.set(2019, 6, 23);

        when(datePicker.getSelectionMode()).thenReturn("range");
        List<Date> range=Arrays.asList(calFrom.getTime(), calTo.getTime());
        datePicker.validateValueInternal(context, range);
        assertFalse(datePicker.isValid());
    }
}
