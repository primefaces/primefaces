package org.primefaces.model.timeline;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultTimelineColumn implements TimelineColumn {
    
    public String title;
    
    public Collection<TimelineEvent> events;
    
    public DefaultTimelineColumn(String title) {
        this.title = title;
        this.events = new ArrayList<TimelineEvent>();
    }

    public Collection<TimelineEvent> getEvents() {
        return events;
    }

    public void setEvents(Collection<TimelineEvent> events) {
        this.events = events;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    
}
