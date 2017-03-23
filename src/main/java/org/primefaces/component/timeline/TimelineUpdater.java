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
package org.primefaces.component.timeline;

import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.model.timeline.TimelineEvent;


public abstract class TimelineUpdater {

	/** The same id of the Timeline component in terms of findComponent() as in {@link #getCurrentInstance(String)} */
	protected String id;

	/**
	 * Gets the current thread-safe TimelineUpdater instance by Id.
	 *
	 * @param  id Id of the Timeline component in terms of findComponent()
	 * @return TimelineUpdater instance.
	 * @throws FacesException if the Timeline component can not be found by the given Id
	 */
	public static TimelineUpdater getCurrentInstance(String id) {
		FacesContext fc = FacesContext.getCurrentInstance();

		@SuppressWarnings("unchecked")
		Map<String, TimelineUpdater> map = (Map<String, TimelineUpdater>) fc.getAttributes().get(TimelineUpdater.class.getName());
		if (map == null) {
			return null;
		}

		UIComponent timeline = fc.getViewRoot().findComponent(id);
		if (timeline == null || !(timeline instanceof Timeline)) {
			throw new FacesException("Timeline component with Id " + id + " was not found");
		}

		TimelineUpdater timelineUpdater = map.get(((Timeline) timeline).resolveWidgetVar());
		if (timelineUpdater != null) {
			timelineUpdater.id = id;
		}

		return timelineUpdater;
	}

	public abstract void add(TimelineEvent event);

	public abstract void update(TimelineEvent event, int index);

	public abstract void delete(int index);

	public abstract void select(int index);

	public abstract void clear();
}
