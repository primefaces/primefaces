/*
 * Copyright 2009-2012 Prime Teknoloji.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.calendar;

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