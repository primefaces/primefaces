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
package org.primefaces.component.calendar;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.component.api.UICalendar;
import org.primefaces.component.datepicker.DatePicker;
import org.primefaces.component.datepicker.DatePickerRenderer;
import org.primefaces.util.CalendarUtils;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.StringStartsWith.startsWith;

import org.primefaces.util.MessageFactory;

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

@RunWith(PowerMockRunner.class)
public class CalendarAndDatePickerTest {

	private DatePickerRenderer renderer;
	private UICalendar calendar;
	private FacesContext context;
	private ExternalContext externalContext;
	private ELContext elContext;
	private ValueExpression valueExpression;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Before
	public void setup() {
		renderer = new DatePickerRenderer();
		calendar = mock(Calendar.class);
		context = mock(FacesContext.class);
		externalContext = mock(ExternalContext.class);
		elContext = mock(ELContext.class);
		when(context.getExternalContext()).thenReturn(externalContext);
		when(context.getELContext()).thenReturn(elContext);
	}

	@After
	public void teardown() {
		renderer = null;
		calendar = null;
		context = null;
		externalContext = null;
		elContext = null;
		valueExpression = null;
	}

	private void setupValues(Class type, Locale locale) {
		when(calendar.calculatePattern()).thenCallRealMethod();
		when(calendar.calculateLocale(context)).thenReturn(locale);
		when(calendar.calculateLocale(null)).thenReturn(locale);
		when(calendar.calculateTimeOnlyPattern()).thenReturn("HH:mm");

		valueExpression = mock(ValueExpression.class);
		when(calendar.getValueExpression(anyString())).thenReturn(valueExpression);
		when(valueExpression.getType(elContext)).thenReturn(type);
	}

	@Test
	public void dateAsStringShouldBeNullIfValueIsNull() {
		Calendar calendar = new Calendar();
		String dateAsString = CalendarUtils.getValueAsString(null, calendar);

		assertEquals(null, dateAsString);
	}

	@Test
	public void dateAsStringShouldBeSubmittedValueIfExists() {
		Calendar calendar = new Calendar();
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
		Class type = renderer.resolveDateType(context, calendar);
		assertEquals(Date.class, type);
	}

	@Test
	public void resolveDateType_LocalDate() {
		setupValues(LocalDate.class, Locale.ENGLISH);
		Class type = renderer.resolveDateType(context, calendar);
		assertEquals(LocalDate.class, type);
	}

	@Test
	public void resolveDateType_LocalTime() {
		setupValues(LocalTime.class, Locale.ENGLISH);
		Class type = renderer.resolveDateType(context, calendar);
		assertEquals(LocalTime.class, type);
	}

	@Test
	public void resolveDateType_LocalDateTime() {
		setupValues(LocalDateTime.class, Locale.ENGLISH);
		Class type = renderer.resolveDateType(context, calendar);
		assertEquals(LocalDateTime.class, type);
	}

	@Test
	@Ignore
	public void resolveDateType_ListLocalDate() {
		//TODO: need´s more mocking-work
		setupValues(List.class, Locale.ENGLISH);
		when(valueExpression.getValueReference(elContext)).thenReturn(null);
		Class type = renderer.resolveDateType(context, calendar);
		assertEquals(LocalDate.class, type);
	}

	@Test
	public void convertToJava8DateTimeAPI_LocalDate() {
		Class type = LocalDate.class;
		setupValues(type, Locale.ENGLISH);
		Temporal temporal = renderer.convertToJava8DateTimeAPI(context, calendar, type, "7/23/19");
		assertEquals(type, temporal.getClass());
		assertEquals(LocalDate.of(2019, 07, 23), temporal);
	}

	@Test
	public void convertToJava8DateTimeAPI_LocalDate_German() {
		Class type = LocalDate.class;
		setupValues(type, Locale.GERMAN);
		Temporal temporal = renderer.convertToJava8DateTimeAPI(context, calendar, type, "23.07.19");
		assertEquals(type, temporal.getClass());
		assertEquals(LocalDate.of(2019, 07, 23), temporal);
	}

	@Test
	public void convertToJava8DateTimeAPI_LocalDate_German_ExplicitPattern() {
		Class type = LocalDate.class;
		setupValues(type, Locale.GERMAN);
		when(calendar.getPattern()).thenReturn("dd.MM.yyyy");

		Temporal temporal = renderer.convertToJava8DateTimeAPI(context, calendar, type, "23.07.2019");
		assertEquals(type, temporal.getClass());
		assertEquals(LocalDate.of(2019, 07, 23), temporal);
	}

	@Test
	public void convertToJava8DateTimeAPI_LocalTime() {
		Class type = LocalTime.class;
		setupValues(type, Locale.ENGLISH);
		Temporal temporal = renderer.convertToJava8DateTimeAPI(context, calendar, type, "21:31");
		assertEquals(type, temporal.getClass());
		assertEquals(LocalTime.of(21, 31), temporal);
	}

	@Test
	public void convertToJava8DateTimeAPI_LocalDateTime() {
		Class type = LocalDateTime.class;
		setupValues(type, Locale.ENGLISH);
		Temporal temporal = renderer.convertToJava8DateTimeAPI(context, calendar, type, "7/23/19 21:31");
		assertEquals(type, temporal.getClass());
		assertEquals(LocalDateTime.of(2019, 7, 23,  21, 31), temporal);
	}

	@Test(expected = ConverterException.class)
	@PrepareForTest(MessageFactory.class)
	public void convertToJava8DateTimeAPI_LocalDate_WrongFormat() {
		//https://stackoverflow.com/questions/21105403/mocking-static-methods-with-mockito
		//https://www.baeldung.com/junit-assert-exception

		PowerMockito.mockStatic(MessageFactory.class);
		BDDMockito.given(MessageFactory.getMessage(eq("javax.faces.converter.DateTimeConverter.DATE"), eq(FacesMessage.SEVERITY_ERROR), any())).willReturn(new FacesMessage("dummy"));

		Class type = LocalDate.class;
		setupValues(type, Locale.ENGLISH);
		Temporal temporal = renderer.convertToJava8DateTimeAPI(context, calendar, type, "23.07.2019");
		assertEquals(type, temporal.getClass());
		assertEquals(LocalDate.of(2019, 07, 23), temporal);
	}

	@Test
	public void convertToJava8DateTimeAPI_LocalDate_WrongPattern() {
		Class type = LocalDate.class;
		setupValues(type, Locale.GERMAN);
		when(calendar.getPattern()).thenReturn("ddaMMbyyyy");

		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(startsWith("Unknown pattern lette"));

		Temporal temporal = renderer.convertToJava8DateTimeAPI(context, calendar, type, "23.07.2019");
		assertEquals(type, temporal.getClass());
		assertEquals(LocalDate.of(2019, 07, 23), temporal);
	}
}
