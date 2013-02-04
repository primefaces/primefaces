/*
 * Copyright 2009,2010 Prime Technology.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultScheduleModel implements ScheduleModel, Serializable {

	private List<ScheduleEvent> events;

	public DefaultScheduleModel() {
		events = new ArrayList<ScheduleEvent>(); 
	}
	
	public DefaultScheduleModel(List<ScheduleEvent> events) {
		this.events = events;
	}
	
	public void addEvent(ScheduleEvent event) {
		event.setId(UUID.randomUUID().toString());
		
		events.add(event);
	}
	
	public boolean deleteEvent(ScheduleEvent event) {
		return events.remove(event);
	}
	
	public List<ScheduleEvent> getEvents() {
		return events;
	}
	
	public ScheduleEvent getEvent(String id) {
		for(ScheduleEvent event : events) {
			if(event.getId().equals(id))
				return event;
		}
		
		return null;
	}
	
	public void updateEvent(ScheduleEvent event) {
		int index = -1;
		
		for(int i = 0 ; i < events.size(); i++) {
			if(events.get(i).getId().equals(event.getId())) {
				index = i;
				
				break;
			}
		}
		
		if(index >= 0) {
			events.set(index, event);
		}
	}
	
	public int getEventCount() {
		return events.size();
	}

	public void clear() {
		events = new ArrayList<ScheduleEvent>();
	}
}