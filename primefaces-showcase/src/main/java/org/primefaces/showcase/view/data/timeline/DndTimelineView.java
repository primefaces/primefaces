/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.showcase.view.data.timeline;

import org.primefaces.component.timeline.TimelineUpdater;
import org.primefaces.event.timeline.TimelineDragDropEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.showcase.domain.Event;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("dndTimelineView")
@ViewScoped
public class DndTimelineView implements Serializable {

    private TimelineModel<Event, ?> model;

    private LocalDateTime start;
    private LocalDateTime end;

    private final List<Event> events = new ArrayList<>();

    @PostConstruct
    public void init() {
        start = LocalDateTime.now().minusHours(4);
        end = LocalDateTime.now().plusHours(8);

        model = new TimelineModel<>();

        for (int i = 1; i <= 10; i++) {
            events.add(new Event("Event " + i));
        }

    }

    public void onDrop(TimelineDragDropEvent<Event> e) {
        // get dragged model object (event class) if draggable item is within a data iteration component,
        // update event's start and end dates.
        Event dndEvent = e.getData();
        dndEvent.setStart(e.getStartDate());
        dndEvent.setEnd(e.getEndDate());

        // create a timeline event (not editable)
        TimelineEvent event = TimelineEvent.builder()
                .data(dndEvent)
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .editable(false)
                .group(e.getGroup())
                .build();

        // add a new event
        TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(":form:timeline");
        model.add(event, timelineUpdater);

        // remove from the list of all events
        events.remove(dndEvent);

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "The " + dndEvent.getName() + " was added", null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onSwitchTimeZone(AjaxBehaviorEvent e) {
        model.clear();
    }

    public TimelineModel<Event, ?> getModel() {
        return model;
    }

    public List<Event> getEvents() {
        return events;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
