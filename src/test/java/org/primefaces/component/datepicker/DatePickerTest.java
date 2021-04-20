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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.util.*;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.component.api.UICalendar;
import org.primefaces.util.CalendarUtils;

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
        public Class<?> resolveDateType(FacesContext context, UICalendar calendar) {
            return super.resolveDateType(context, calendar);
        }

        @Override
        public Temporal convertToJava8DateTimeAPI(FacesContext context, UICalendar calendar, Class<?> type, String submittedValue) {
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


    @BeforeEach
    public void setup() {
        renderer = mock(MyDatePickerRenderer.class);
        datePicker = mock(MyDatePicker.class);
        when(datePicker.calculatePattern()).thenCallRealMethod();
        when(datePicker.calculateTimeOnlyPattern()).thenCallRealMethod();
        when(datePicker.calculateWidgetPattern()).thenCallRealMethod();
        when(datePicker.getTimeSeparator()).thenCallRealMethod();
        when(datePicker.isValid()).thenCallRealMethod();
        doCallRealMethod().when(datePicker).setValid(anyBoolean());
        when(datePicker.getSelectionMode()).thenReturn("single");
        datePicker.setValid(true);

        context = mock(FacesContext.class);
        when(renderer.resolveDateType(context, datePicker)).thenCallRealMethod();
        when(renderer.convertToJava8DateTimeAPI(eq(context), eq(datePicker), any(), any())).thenCallRealMethod();
        when(renderer.convertToLegacyDateAPI(eq(context), eq(datePicker), any())).thenCallRealMethod();
        when(renderer.createConverterException(eq(context), any(), anyString(), any())).thenAnswer(invocation -> new ConverterException());
        when(datePicker.validateDateValue(eq(context), any())).thenCallRealMethod();
        when(datePicker.validateDateValue(eq(context), any(), any())).thenCallRealMethod();
        when(datePicker.validateTimeOnlyValue(eq(context), any())).thenCallRealMethod();
        when(datePicker.validateValueInternal(eq(context), any())).thenCallRealMethod();

        externalContext = mock(ExternalContext.class);
        elContext = mock(ELContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(context.getELContext()).thenReturn(elContext);

        valueExpression = mock(ValueExpression.class);
        when(datePicker.getValueExpression(anyString())).thenReturn(valueExpression);
    }

    @AfterEach
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
        when(datePicker.calculateLocalizedPattern()).thenCallRealMethod();
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
        Class<?> type = renderer.resolveDateType(context, datePicker);
        assertEquals(Date.class, type);
    }

    @Test
    public void resolveDateType_LocalDate() {
        setupValues(LocalDate.class, Locale.ENGLISH);
        Class<?> type = renderer.resolveDateType(context, datePicker);
        assertEquals(LocalDate.class, type);
    }

    @Test
    public void resolveDateType_LocalTime() {
        setupValues(LocalTime.class, Locale.ENGLISH);
        Class<?> type = renderer.resolveDateType(context, datePicker);
        assertEquals(LocalTime.class, type);
    }

    @Test
    public void resolveDateType_LocalDateTime() {
        setupValues(LocalDateTime.class, Locale.ENGLISH);
         Class<?> type = renderer.resolveDateType(context, datePicker);
        assertEquals(LocalDateTime.class, type);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "7/23/2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 07, 23), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate_German() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.GERMAN);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "23.07.2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 07, 23), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate_German_ExplicitPattern() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.GERMAN);
        when(datePicker.getPattern()).thenReturn("dd.MM.yyyy");

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "23.07.2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 07, 23), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalTime() {
        Class<?> type = LocalTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "21:31");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalTime.of(21, 31), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalTimeWithSeconds() {
        Class<?> type = LocalTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.isShowSeconds()).thenReturn(Boolean.TRUE);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "21:31:47");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalTime.of(21, 31, 47), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalTimeWithAmPm() {
        Class<?> type = LocalTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getHourFormat()).thenReturn("12");
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "09:31 PM");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalTime.of(21, 31), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalTimeWithSecondsAndAmPm() {
         Class<?> type = LocalTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.isShowSeconds()).thenReturn(Boolean.TRUE);
        when(datePicker.getHourFormat()).thenReturn("12");
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "09:31:47 PM");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalTime.of(21, 31, 47), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDateTime() {
         Class<?> type = LocalDateTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.hasTime()).thenReturn(Boolean.TRUE);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "7/23/2019 21:31");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDateTime.of(2019, 7, 23,  21, 31), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDateTime_NoTime() {
         Class<?> type = LocalDateTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.hasTime()).thenReturn(Boolean.FALSE);
        when(datePicker.isShowTime()).thenReturn(Boolean.FALSE);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "07/23/2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDateTime.of(2019, 7, 23,  0, 0), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDateTimeSecondsAmPm() {
         Class<?> type = LocalDateTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.hasTime()).thenReturn(Boolean.TRUE);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getHourFormat()).thenReturn("12");
        when(datePicker.isShowSeconds()).thenReturn(Boolean.TRUE);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "7/23/2019 09:31:48 PM");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDateTime.of(2019, 7, 23,  21, 31, 48), temporal);
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate_WrongFormat() {
        // Arrange
         Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);

        FacesMessage message=new FacesMessage("dummy");
        when(renderer.createConverterException(eq(context), eq(datePicker), any(), any())).thenReturn(new ConverterException(message));

        // Act
        ConverterException thrown = Assertions.assertThrows(ConverterException.class, () -> {
            Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "23.07.2019");
            assertEquals(type, temporal.getClass());
            assertEquals(LocalDate.of(2019, 07, 23), temporal);
        });

        assertEquals("dummy", thrown.getMessage());
    }

    @Test
    public void convertToJava8DateTimeAPI_LocalDate_WrongPattern() {
         Class<?> type = LocalDate.class;
        setupValues(type, Locale.GERMAN);
        when(datePicker.getPattern()).thenReturn("ddaMMbyyyy");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "23.07.2019");
            assertEquals(type, temporal.getClass());
            assertEquals(LocalDate.of(2019, 07, 23), temporal);
        });

        assertEquals("Unknown pattern letter: b", thrown.getMessage());
    }

    /**
     * {@link ResolverStyle} == SMART (default value). The date 02/30/2019 is silently parsed to 02/28/2019.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Smart_implicit() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "2/30/2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 02, 28), temporal);
    }

    /**
     * {@link ResolverStyle} == SMART (explicitly set). The date 02/30/2019 is silently parsed to 02/28/2019.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Smart_explicit() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.getResolverStyle()).thenReturn("SMART");
        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "2/30/2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 02, 28), temporal);
    }

    /**
     * {@link ResolverStyle} == STRICT. The date 02/30/2019 should lead to a thrown ConverterException.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Strict() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.getResolverStyle()).thenReturn("STRICT");

        Assertions.assertThrows(ConverterException.class, () -> renderer.convertToJava8DateTimeAPI(context, datePicker, type, "2/30/2019"));
    }

    /**
     * {@link ResolverStyle} == STRICT. The date 02/30/2019 should lead to a thrown ConverterException.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Strict_differentCase() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.getResolverStyle()).thenReturn("strict");

        Assertions.assertThrows(ConverterException.class, () -> renderer.convertToJava8DateTimeAPI(context, datePicker, type, "2/30/2019"));
    }

    /**
     * {@link ResolverStyle} == STRICT. The valid date 02/20/2019 should be correctly parsed.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Strict_ValidDate() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.getResolverStyle()).thenReturn("STRICT");

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "2/20/2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 02, 20), temporal);
    }

    /**
     * {@link ResolverStyle} == STRICT. The valid time 10:11 should be correctly parsed.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Strict_ValidTime() {
        Class<?> type = LocalTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.getResolverStyle()).thenReturn("STRICT");

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "10:11");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalTime.of(10, 11), temporal);
    }

    /**
     * {@link ResolverStyle} == LENIENT. The time 10:65 should leniently parsed.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Lenient_Time() {
        Class<?> type = LocalTime.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.getResolverStyle()).thenReturn("LENIENT");

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "10:65");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalTime.of(11, 05), temporal);
    }

    /**
     * {@link ResolverStyle} == LENIENT. The date 02/30/2019 is silently parsed to 03/02/2019.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Lenient() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.getResolverStyle()).thenReturn("LENIENT");

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "2/30/2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 03, 02), temporal);
    }

    /**
     * Invalid {@link ResolverStyle}. The date 02/30/2019 is silently parsed to 02/28/2019 as default value 'SMART' is used.
     */
    @Test
    public void convertToJava8DateTimeAPI_ResolveStyle_Invalid() {
        Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);
        when(datePicker.getResolverStyle()).thenReturn("what?");

        Temporal temporal = renderer.convertToJava8DateTimeAPI(context, datePicker, type, "2/30/2019");
        assertEquals(type, temporal.getClass());
        assertEquals(LocalDate.of(2019, 02, 28), temporal);
    }

    @Test
    public void getConvertedValue_Date() {
         Class<?> type = Date.class;
        setupValues(type, Locale.ENGLISH);

        when(renderer.getConvertedValue(eq(context), eq(datePicker), any())).thenCallRealMethod();

        Object object = renderer.getConvertedValue(context, datePicker, "7/23/2019");
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
         Class<?> type = LocalDate.class;
        setupValues(type, Locale.ENGLISH);

        when(renderer.getConvertedValue(eq(context), eq(datePicker), any())).thenCallRealMethod();

        Object object = renderer.getConvertedValue(context, datePicker, "7/23/2019");
        assertEquals(type, object.getClass());
        LocalDate localDate = (LocalDate)object;
        assertEquals(LocalDate.of(2019, 07, 23), localDate);
    }

    @Test
    public void validateValueInternal_simple() {
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_minDate_LocalDate() {
        when(datePicker.getMindate()).thenReturn(LocalDate.of(2019, 1, 1));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_minDate_LocalDate_wrong() {
        when(datePicker.getMindate()).thenReturn(LocalDate.of(2019, 1, 1));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2018, 7, 23));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minDate_String() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.getMindate()).thenReturn("1/1/2019");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_minDate_String_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.getMindate()).thenReturn("1/1/2019");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2018, 7, 23));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minDate_Date_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.set(2019, 0, 1);

        when(datePicker.getMindate()).thenReturn(cal.getTime());
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2018, 7, 23));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxDate_LocalDate() {
        when(datePicker.getMaxdate()).thenReturn(LocalDate.of(2019, 12, 31));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_maxDate_LocalDate_wrong() {
        when(datePicker.getMaxdate()).thenReturn(LocalDate.of(2019, 12, 31));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2020, 7, 23));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxDate_String() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.getMaxdate()).thenReturn("12/31/2019");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_maxDate_String_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.getMaxdate()).thenReturn("12/31/2019");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2020, 7, 23));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxDate_Date_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.set(2019, 11, 31);

        when(datePicker.getMaxdate()).thenReturn(cal.getTime());
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2020, 7, 23));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minAndMaxDate_LocalDate_wrong() {
        when(datePicker.getMindate()).thenReturn(LocalDate.of(2019, 1, 1));
        when(datePicker.getMaxdate()).thenReturn(LocalDate.of(2019, 12, 31));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2018, 7, 23));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_OUT_OF_RANGE, validationResult);
    }

    @Test
    public void validateValueInternal_disabledDates_LocalDate() {
        when(datePicker.getDisabledDates()).thenReturn(Arrays.asList(LocalDate.of(2019, 7, 22), LocalDate.of(2019, 7, 24)));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_disabledDates_LocalDate_wrong() {
        when(datePicker.getDisabledDates()).thenReturn(Arrays.asList(LocalDate.of(2019, 7, 22), LocalDate.of(2019, 7, 24)));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 22));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_DISABLED_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_disabledDays() {
        when(datePicker.getDisabledDays()).thenReturn(Arrays.asList(0, 1));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 23));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_disabledDays_wrong() {
        when(datePicker.getDisabledDays()).thenReturn(Arrays.asList(0, 1));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDate.of(2019, 7, 22));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_DISABLED_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_LocalDate_range() {
        when(datePicker.getSelectionMode()).thenReturn("range");
        List<LocalDate> range=Arrays.asList(LocalDate.of(2019, 7, 23), LocalDate.of(2019, 7, 30));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, range);
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_LocalDate_range_wrong() {
        when(datePicker.getSelectionMode()).thenReturn("range");
        List<LocalDate> range=Arrays.asList(LocalDate.of(2019, 7, 30), LocalDate.of(2019, 7, 23));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, range);
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_RANGE_DATES_SEQUENTIAL, validationResult);
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
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, range);
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
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
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, range);
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_RANGE_DATES_SEQUENTIAL, validationResult);
    }

    @Test
    public void validateValueInternal_minTime_LocalTime() {
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(LocalTime.of(8, 35));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(17, 24));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_minTime_LocalTime_wrong() {
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(LocalTime.of(17, 24));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(8, 35));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minTime_String() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn("08:35");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(17, 24));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_minTime_String_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn("12:00");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(11, 59));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minTime_Date_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 0);

        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(cal.getTime());
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(8, 0));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxTime_LocalTime() {
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn(LocalTime.of(18, 00));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(15, 00));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_maxTime_LocalTime_wrong() {
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn(LocalTime.of(12, 00));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(15, 00));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxTime_String() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn("20:00");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(15, 00));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_maxTime_String_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn("15:00");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(18, 00));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxTime_Date_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 0);

        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn(cal.getTime());
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(14, 00));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minAndMaxTime_LocalTime_wrong() {
        when(datePicker.isTimeOnly()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(LocalTime.of(10, 0));
        when(datePicker.getMaxdate()).thenReturn(LocalTime.of(11, 59));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalTime.of(8, 00));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_OUT_OF_RANGE, validationResult);
    }

    @Test
    public void validateValueInternal_minDateTime_LocalDateTime() {
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(LocalDateTime.of(2019, 12, 31, 23, 59));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2020, 1, 1, 8, 35));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_minDate_LocalDateTime() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(LocalDate.of(2019, 12, 31));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2020, 1, 1, 8, 35));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_minDateTime_LocalDateTime_wrong() {
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(LocalDateTime.of(2019, 12, 31, 23, 59));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 11, 12, 17, 24));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minDate_LocalDateTime_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(LocalDate.of(2019, 12, 31));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 11, 12, 17, 24));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minDateTime_String() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn("1/1/2019 00:00");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 1, 1, 02, 00));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_minDateTime_String_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn("1/1/2019 12:00");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 1, 1, 11, 59));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minDateTime_Date_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.set(2019, 0, 1);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 0);

        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(cal.getTime());
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2018, 1, 1, 12, 01));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MIN_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxDateTime_LocalDateTime() {
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn(LocalDateTime.of(2019, 7, 1, 15, 00));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 7, 1, 11, 00));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_maxDate_LocalDateTime() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn(LocalDate.of(2019, 7, 1));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 7, 1, 11, 00));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_maxDateTime_LocalDateTime_wrong() {
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn(LocalDateTime.of(2019, 7, 1, 15, 00));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 7, 2, 14, 59));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxDate_LocalDateTime_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn(LocalDate.of(2019, 7, 1));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 7, 2, 14, 59));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxDateTime_String() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn("12/1/2019 20:00");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 11, 30, 16, 00));
        assertTrue(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.OK, validationResult);
    }

    @Test
    public void validateValueInternal_maxDateTime_String_wrong() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn("12/1/2019 15:00");
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 12, 31, 18, 00));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_maxDateTime_Date_wrong() {
        setupValues(null, Locale.ENGLISH);
        java.util.Calendar cal = GregorianCalendar.getInstance();
        cal.set(2019, 8, 17);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 0);

        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMaxdate()).thenReturn(cal.getTime());
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 9, 17, 12, 05));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_MAX_DATE, validationResult);
    }

    @Test
    public void validateValueInternal_minAndMaxDateTime_LocalDateTime_wrong() {
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getMindate()).thenReturn(LocalDateTime.of(2019, 11, 12, 10, 0));
        when(datePicker.getMaxdate()).thenReturn(LocalDateTime.of(2019, 11, 12, 11, 59));
        DatePicker.ValidationResult validationResult = datePicker.validateValueInternal(context, LocalDateTime.of(2019, 11, 12, 8, 00));
        assertFalse(datePicker.isValid());
        assertEquals(DatePicker.ValidationResult.INVALID_OUT_OF_RANGE, validationResult);
    }

    @Test
    public void calculatePatternDefault() {
        setupValues(null, Locale.ENGLISH);
        assertEquals("M/d/yyyy", datePicker.calculatePattern());
    }

    @Test
    public void calculatePatternWithTime() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        assertEquals("M/d/yyyy HH:mm", datePicker.calculatePattern());
    }

    @Test
    public void calculatePatternWithSeconds() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.isShowSeconds()).thenReturn(Boolean.TRUE);
        assertEquals("M/d/yyyy HH:mm:ss", datePicker.calculatePattern());
    }

    @Test
    public void calculatePatternWithSecondsAndAmPm() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.isShowSeconds()).thenReturn(Boolean.TRUE);
        when(datePicker.getHourFormat()).thenReturn("12");
        assertEquals("M/d/yyyy hh:mm:ss a", datePicker.calculatePattern());
    }

    @Test
    public void calculatePatternWithAmPm() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getHourFormat()).thenReturn("12");
        assertEquals("M/d/yyyy hh:mm a", datePicker.calculatePattern());
    }


    @Test
    public void calculatePatternWithTimeRemove() {
        setupValues(null, Locale.ENGLISH);
        when(datePicker.isShowTime()).thenReturn(Boolean.TRUE);
        when(datePicker.getPattern()).thenReturn("yyyy-MM-dd KK:mm:ss a");
        assertEquals("yyyy-MM-dd HH:mm", datePicker.calculatePattern());
    }

}
