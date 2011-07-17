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
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DefaultTimeLine implements Timeline {
    
    private String id;
    
    private String title;
    
    private String description;
    
    private Date focusDate;
    
    private int initialZoom = 20;
    
    private List<TimelineEvent> events;
    
    public DefaultTimeLine() {
        this.events = new ArrayList<TimelineEvent>();
    }

    public DefaultTimeLine(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.events = new ArrayList<TimelineEvent>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFocusDate() {
        return focusDate;
    }

    public void setFocusDate(Date focusDate) {
        this.focusDate = focusDate;
    }

    public int getInitialZoom() {
        return initialZoom;
    }

    public void setInitialZoom(int initialZoom) {
        this.initialZoom = initialZoom;
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

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final DefaultTimeLine other = (DefaultTimeLine) obj;
        if((this.id == null) ? (other.id != null) : !this.id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return title;
    }    
}
