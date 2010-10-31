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
import java.util.Date;

public class DefaultScheduleEvent implements ScheduleEvent, Serializable {

	private String id;
	
	private String title;
	
	private Date startDate;
	
	private Date endDate;
	
	private boolean allDay = true;
	
	private String styleClass;
	
	private Object data;

	public DefaultScheduleEvent() {}
	
	public DefaultScheduleEvent(String title, Date start, Date end) {
		this.title = title;
		this.startDate = start;
		this.endDate = end;
	}
	
	public DefaultScheduleEvent(String title, Date start, Date end, boolean allDay) {
		this.title = title;
		this.startDate = start;
		this.endDate = end;
		this.allDay = allDay;
	}
	
	public DefaultScheduleEvent(String title, Date start, Date end, String styleClass) {
		this.title = title;
		this.startDate = start;
		this.endDate = end;
		this.styleClass = styleClass;
	}
	
	public DefaultScheduleEvent(String title, Date start, Date end, Object data) {
		this.title = title;
		this.startDate = start;
		this.endDate = end;
		this.data = data;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyleClass() {
		return styleClass;
	}
	
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}