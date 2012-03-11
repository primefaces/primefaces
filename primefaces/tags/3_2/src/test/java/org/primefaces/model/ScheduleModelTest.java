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
package org.primefaces.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ScheduleModelTest {

	@Test
	public void addEvents() {
		ScheduleModel model = new DefaultScheduleModel();
		model.addEvent(new DefaultScheduleEvent("Entry 1", null, null));
		model.addEvent(new DefaultScheduleEvent("Entry 2", null, null));
		
		assertEquals(2, model.getEventCount());
	}
	
	@Test
	public void deleteEvent() {
		ScheduleModel model = new DefaultScheduleModel();
		ScheduleEvent event1 = new DefaultScheduleEvent("Entry 1", null, null);
		ScheduleEvent event2 = new DefaultScheduleEvent("Entry 2", null, null);
		
		model.addEvent(event1);
		model.addEvent(event2);

		model.deleteEvent(event2);
		
		assertEquals(1, model.getEventCount());
		assertEquals("Entry 1", model.getEvents().get(0).getTitle());
	}
	
	@Test
	public void findEventById() {
		ScheduleModel model = new DefaultScheduleModel();
		model.addEvent(new DefaultScheduleEvent("Entry 1", null, null));
		model.addEvent(new DefaultScheduleEvent("Entry 2", null, null));
		model.addEvent(new DefaultScheduleEvent("Entry 3", null, null));
		model.addEvent(new DefaultScheduleEvent("Entry 4", null, null));
		model.addEvent(new DefaultScheduleEvent("Entry 5", null, null));
		
		String id = model.getEvents().get(2).getId();
		
		assertEquals("Entry 3", model.getEvent(id).getTitle());
	}
}
