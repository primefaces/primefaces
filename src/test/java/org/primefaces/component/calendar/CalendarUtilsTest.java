/*
 * Copyright 2010 Prime Technology.
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

import org.junit.Test;

public class CalendarUtilsTest {

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