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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.timeline.*;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.showcase.domain.Event;

@Named("allEventsTimelineView")
@ViewScoped
public class AllEventsTimelineView implements Serializable {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private TimelineModel<String, ?> model;
    private LocalDateTime start;
    private LocalDateTime end;

    @PostConstruct
    public void init() {
        // set initial start / end dates for the axis of the timeline
        start = LocalDateTime.now().minusHours(4);
        end = LocalDateTime.now().plusHours(8);

        // groups
        String[] names = new String[]{"User 1", "User 2", "User 3", "User 4", "User 5", "User 6"};

        // create timeline model
        model = new TimelineModel<>();

        for (String name : names) {
            LocalDateTime end = LocalDateTime.now().minusHours(12).withMinute(0).withSecond(0).withNano(0);

            for (int i = 0; i < 5; i++) {
                LocalDateTime start = end.plusHours(Math.round(Math.random() * 5));
                end = start.plusHours(4 + Math.round(Math.random() * 5));

                long r = Math.round(Math.random() * 2);
                String availability = (r == 0 ? "Unavailable" : (r == 1 ? "Available" : "Maybe"));

                // create an event with content, start / end dates, editable flag, group name and custom style class
                TimelineEvent event = TimelineEvent.builder()
                        .data(availability)
                        .startDate(start)
                        .endDate(end)
                        .editable(true)
                        .group(name)
                        .styleClass(availability.toLowerCase())
                        .build();

                model.add(event);
            }
        }
    }

    public void onAdd(TimelineAddEvent e) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String dates =  e.getStartDate().format(FORMATTER);
        if (e.getEndDate() != null) {
            dates += " - " + e.getEndDate().format(FORMATTER);
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "add", dates));
    }

    public void onChange(TimelineModificationEvent<String> e) {
        TimelineEvent<String> timelineEvent = e.getTimelineEvent();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "change",
                timelineEvent.getData() + ": " + timelineEvent.getStartDate().format(FORMATTER)
                + " - " + timelineEvent.getEndDate().format(FORMATTER)));
    }

    public void onChanged(TimelineModificationEvent<String> e) {
        TimelineEvent<String> timelineEvent = e.getTimelineEvent();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "changed",
                timelineEvent.getData() + ": " + timelineEvent.getStartDate().format(FORMATTER)
                + " - " + timelineEvent.getEndDate().format(FORMATTER)));
    }

    public void onEdit(TimelineModificationEvent<String> e) {
        TimelineEvent<String> timelineEvent = e.getTimelineEvent();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "edit",
                timelineEvent.getData() + ": " + timelineEvent.getStartDate().format(FORMATTER)
                + " - " + timelineEvent.getEndDate().format(FORMATTER)));
    }

    public void onDelete(TimelineModificationEvent<String> e) {
        TimelineEvent<String> timelineEvent = e.getTimelineEvent();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "delete",
                timelineEvent.getData() + ": " + timelineEvent.getStartDate().format(FORMATTER)
                + " - " + timelineEvent.getEndDate().format(FORMATTER)));
    }

    public void onSelect(TimelineSelectEvent<String> e) {
        TimelineEvent<String> timelineEvent = e.getTimelineEvent();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "select",
                timelineEvent.getData() + ": " + timelineEvent.getStartDate().format(FORMATTER)
                + " - " + timelineEvent.getEndDate().format(FORMATTER)));
    }

    public void onRangeChange(TimelineRangeEvent e) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "rangechange",
                e.getStartDate().format(FORMATTER) + " - " + e.getEndDate().format(FORMATTER)));
    }

    public void onRangeChanged(TimelineRangeEvent e) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "rangechanged",
                e.getStartDate().format(FORMATTER) + " - " + e.getEndDate().format(FORMATTER)));
    }

    public void onLazyLoad(TimelineLazyLoadEvent e) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "lazyload",
                e.getStartDate().format(FORMATTER) + " - " + e.getEndDate().format(FORMATTER)));
    }

    public void onDrop(TimelineDragDropEvent<Event> e) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "drop",
                e.getData().getStart().format(FORMATTER) + " - " + e.getData().getEnd().format(FORMATTER)));
    }

    public TimelineModel<String, ?> getModel() {
        return model;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
