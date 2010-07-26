/*
 * Copyright 2009 Prime Technology.
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

public class CalendarRendererTest {

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
	public void dateAsStringIsNullIfValueIsNull() {
		Calendar calendar = new Calendar();
		String dateAsString = renderer.getValueAsString(null, calendar, "MM/dd/yyyy");
		
		assertEquals(null, dateAsString);
	}
	
	@Test
	public void pagedateCanBeOverridenByUser() {
		Calendar calendar = new Calendar();
		calendar.setPagedate("01/2009");
		
		assertEquals("01/2009", renderer.getPageDate(null, calendar));
	}
	
	@Test
	public void convertedValueShouldBeNullWhenEmptyStringIsSubmitted() {
		Calendar calendar = new Calendar();
		
		Object convertedValue = new CalendarRenderer().getConvertedValue(null, calendar, "");
		assertNull(convertedValue);
		
		convertedValue = new CalendarRenderer().getConvertedValue(null, calendar, "  ");
		assertNull(convertedValue);
	}
}