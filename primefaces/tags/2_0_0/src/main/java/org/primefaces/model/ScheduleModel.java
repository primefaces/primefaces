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
package org.primefaces.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ScheduleModel<T extends ScheduleEvent> implements Serializable {
	
	private List<T> events;

	public ScheduleModel() {
		events = new ArrayList<T>(); 
	}
	
	public ScheduleModel(List<T> events) {
		this.events = events;
	}
	
	public void addEvent(T t) {
		t.setId(UUID.randomUUID().toString());
		
		events.add(t);
	}
	
	public boolean deleteEvent(T t) {
		return events.remove(t);
	}
	
	public List<T> getEvents() {
		return events;
	}
	
	public void setEvents(List<T> events) {
		this.events = events;
	}
	
	public T getEvent(String id) {
		for(T t: events) {
			if(t.getId().equals(id))
				return t;
		}
		
		return null;
	}
	
	public void updateEvent(T event) {
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
	
	/**
	 * Method to be used when implementing lazy loading, implementers should override to fetch events that belong to a particular period
	 * 
	 * @param start	Start date of period
	 * @param end 	End date of period
	 */
	public void fetchEvents(Date start, Date end) {}
	
	/**
	 * When implementing lazy loading, isLazy() should return true
	 * 
	 */
	public boolean isLazy() {
		return false;
	}
}