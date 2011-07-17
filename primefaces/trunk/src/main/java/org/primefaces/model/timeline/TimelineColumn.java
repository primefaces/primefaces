package org.primefaces.model.timeline;

import java.util.Collection;

public interface TimelineColumn {
    
    public String getTitle();
    
    public Collection<TimelineEvent> getEvents();
}
