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
package org.primefaces.model;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ScheduleModelTest {

	@Test
	public void addEvents() {
		ScheduleModel model = new DefaultScheduleModel();
		model.addEvent(DefaultScheduleEvent.builder().title("Entry 1").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusHours(1)).build());
		model.addEvent(DefaultScheduleEvent.builder().title("Entry 2").startDate(LocalDateTime.now().plusDays(1)).endDate(LocalDateTime.now().plusDays(1).plusHours(1)).build());

		assertEquals(2, model.getEventCount());
	}

	@Test
	public void deleteEvent() {
		ScheduleModel model = new DefaultScheduleModel();
		ScheduleEvent event1 = DefaultScheduleEvent.builder().title("Entry 1").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusHours(1)).build();
		ScheduleEvent event2 = DefaultScheduleEvent.builder().title("Entry 2").startDate(LocalDateTime.now().plusDays(1)).endDate(LocalDateTime.now().plusDays(1).plusHours(1)).build();

		model.addEvent(event1);
		model.addEvent(event2);

		model.deleteEvent(event2);

		assertEquals(1, model.getEventCount());
		assertEquals("Entry 1", model.getEvents().get(0).getTitle());
	}

	@Test
	public void findEventById() {
		ScheduleModel model = new DefaultScheduleModel();
		model.addEvent(DefaultScheduleEvent.builder().title("Entry 1").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusHours(1)).build());
		model.addEvent(DefaultScheduleEvent.builder().title("Entry 2").startDate(LocalDateTime.now().plusDays(1)).endDate(LocalDateTime.now().plusDays(1).plusHours(1)).build());
		model.addEvent(DefaultScheduleEvent.builder().title("Entry 3").startDate(LocalDateTime.now().plusDays(2)).endDate(LocalDateTime.now().plusDays(2).plusHours(1)).build());
		model.addEvent(DefaultScheduleEvent.builder().title("Entry 4").startDate(LocalDateTime.now().plusDays(3)).endDate(LocalDateTime.now().plusDays(3).plusHours(1)).build());
		model.addEvent(DefaultScheduleEvent.builder().title("Entry 5").startDate(LocalDateTime.now().plusDays(4)).endDate(LocalDateTime.now().plusDays(4).plusHours(1)).build());
		String id = model.getEvents().get(2).getId();

		assertEquals("Entry 3", model.getEvent(id).getTitle());
	}
}
