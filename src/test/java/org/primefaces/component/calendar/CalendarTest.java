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

import org.primefaces.util.CalendarUtils;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CalendarTest {

	private CalendarRenderer renderer;

	@Before
	public void setup() {
		renderer = new CalendarRenderer();
	}

	@After
	public void teardown() {
		renderer = null;
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
		Calendar calendar = new Calendar();

		Object convertedValue = renderer.getConvertedValue(null, calendar, "");
		assertNull(convertedValue);

		convertedValue = renderer.getConvertedValue(null, calendar, "  ");
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
}