/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.model.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultTimelineModel implements TimelineModel {
    
    private List<TimelineEvent> events;

    public DefaultTimelineModel() {
        this.events = new ArrayList<TimelineEvent>();
    }

    public void addEvent(TimelineEvent event) {
        event.setId(UUID.randomUUID().toString());
		
		events.add(event);
    }

    public boolean deleteEvent(TimelineEvent event) {
        return events.remove(event);
    }

    public List<TimelineEvent> getEvents() {
        return events;
    }
}
