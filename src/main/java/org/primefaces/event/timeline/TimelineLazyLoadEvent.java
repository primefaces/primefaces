/*
 * Copyright 2009-2016 PrimeTek.
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
package org.primefaces.event.timeline;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import org.primefaces.event.AbstractAjaxBehaviorEvent;


public class TimelineLazyLoadEvent extends AbstractAjaxBehaviorEvent {

	/** start time of the first time range for lazy loading */
	private Date startDateFirst;

	/** end time of the first time range for lazy loading */
	private Date endDateFirst;

	/** start time of the second time range for lazy loading (if any) */
	private Date startDateSecond;

	/** end time of the second time range for lazy loading (if any) */
	private Date endDateSecond;

	public TimelineLazyLoadEvent(UIComponent component, Behavior behavior, Date startDateFirst, Date endDateFirst,
	                             Date startDateSecond, Date endDateSecond) {
		super(component, behavior);
		this.startDateFirst = startDateFirst;
		this.endDateFirst = endDateFirst;
		this.startDateSecond = startDateSecond;
		this.endDateSecond = endDateSecond;
	}

	public Date getStartDate() { // alias for getStartDateFirst()
		return startDateFirst;
	}

	public Date getEndDate() { // alias for getEndDateFirst()
		return endDateFirst;
	}

	public Date getStartDateFirst() {
		return startDateFirst;
	}

	public Date getEndDateFirst() {
		return endDateFirst;
	}

	public Date getStartDateSecond() {
		return startDateSecond;
	}

	public Date getEndDateSecond() {
		return endDateSecond;
	}

	public boolean hasTwoRanges() {
		return startDateSecond != null && endDateSecond != null;
	}
}
