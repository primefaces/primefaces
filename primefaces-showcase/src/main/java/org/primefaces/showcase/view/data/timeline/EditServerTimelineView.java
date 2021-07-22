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
import org.primefaces.event.timeline.TimelineAddEvent;
import org.primefaces.event.timeline.TimelineModificationEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.showcase.domain.Booking;
import org.primefaces.showcase.domain.RoomCategory;

@Named("editServerTimelineView")
@ViewScoped
public class EditServerTimelineView implements Serializable {

    private TimelineModel<Booking, ?> model;
    private TimelineEvent<Booking> event; // current event to be changed, edited, deleted or added
    private long zoomMax;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean editableTime = true;

    @PostConstruct
    protected void initialize() {
        // initial zooming is ca. one month to avoid hiding of event details (due to wide time range of events)
        zoomMax = 1000L * 60 * 60 * 24 * 30;

        // set initial start / end dates for the axis of the timeline (just for testing)
        start = LocalDate.of(2019, Month.FEBRUARY, 9).atStartOfDay();
        end = LocalDate.of(2019, Month.MARCH, 10).atStartOfDay();

        // create timeline model
        model = new TimelineModel<>();

        model.add(TimelineEvent.<Booking>builder()
                .data(new Booking(211, RoomCategory.DELUXE, "(0034) 987-111", "One day booking"))
                .startDate(LocalDateTime.of(2019, Month.JANUARY, 2, 0, 0))
                .build());

        model.add(TimelineEvent.<Booking>builder()
                .data(new Booking(202, RoomCategory.EXECUTIVE_SUITE, "(0034) 987-333", "Three day booking"))
                .startDate(LocalDateTime.of(2019, Month.JANUARY, 26, 0, 0))
                .endDate(LocalDateTime.of(2019, Month.JANUARY, 28, 23, 59, 59))
                .build());

        model.add(TimelineEvent.<Booking>builder()
                .data(new Booking(150, RoomCategory.STANDARD, "(0034) 987-222", "Six day booking"))
                .startDate(LocalDateTime.of(2019, Month.FEBRUARY, 10, 0, 0))
                .endDate(LocalDateTime.of(2019, Month.FEBRUARY, 15, 23, 59, 59))
                .build());

        model.add(TimelineEvent.<Booking>builder()
                .data(new Booking(178, RoomCategory.STANDARD, "(0034) 987-555", "Five day booking"))
                .startDate(LocalDateTime.of(2019, Month.FEBRUARY, 23, 0, 0))
                .endDate(LocalDateTime.of(2019, Month.FEBRUARY, 27, 23, 59, 59))
                .build());

        model.add(TimelineEvent.<Booking>builder()
                .data(new Booking(101, RoomCategory.SUPERIOR, "(0034) 987-999", "One day booking"))
                .startDate(LocalDateTime.of(2019, Month.MARCH, 6, 0, 0))
                .build());

        model.add(TimelineEvent.<Booking>builder()
                .data(new Booking(80, RoomCategory.JUNIOR, "(0034) 987-444", "Four day booking"))
                .startDate(LocalDateTime.of(2019, Month.MARCH, 19, 0, 0))
                .endDate(LocalDateTime.of(2019, Month.MARCH, 22, 23, 59, 59))
                .build());

        model.add(TimelineEvent.<Booking>builder()
                .data(new Booking(96, RoomCategory.DELUXE, "(0034) 987-777", "Two day booking"))
                .startDate(LocalDateTime.of(2019, Month.APRIL, 3, 0, 0))
                .endDate(LocalDateTime.of(2019, Month.APRIL, 4, 23, 59, 59))
                .build());

        model.add(TimelineEvent.<Booking>builder()
                .data(new Booking(80, RoomCategory.JUNIOR, "(0034) 987-444", "Ten day booking"))
                .startDate(LocalDateTime.of(2019, Month.APRIL, 22, 0, 0))
                .endDate(LocalDateTime.of(2019, Month.MAY, 1, 23, 59, 59))
                .build());
    }

    public void onChange(TimelineModificationEvent<Booking> e) {
        // get clone of the TimelineEvent to be changed with new start / end dates
        event = e.getTimelineEvent();

        // update booking in DB...
        // if everything was ok, no UI update is required. Only the model should be updated
        model.update(event);

        FacesMessage msg
                = new FacesMessage(FacesMessage.SEVERITY_INFO, "The booking dates " + getRoom() + " have been updated", null);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        // otherwise (if DB operation failed) a rollback can be done with the same response as follows:
        // TimelineEvent oldEvent = model.getEvent(model.getIndex(event));
        // TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(":form:timeline");
        // model.update(oldEvent, timelineUpdater);
    }

    public void onEdit(TimelineModificationEvent<Booking> e) {
        // get clone of the TimelineEvent to be edited
        event = e.getTimelineEvent();
    }

    public void onAdd(TimelineAddEvent e) {
        // get TimelineEvent to be added
        event = TimelineEvent.<Booking>builder()
                // the id generated from the UI must be set
                .id(e.getId())
                .data(new Booking())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .editable(true)
                .build();

        // add the new event to the model in case if user will close or cancel the "Add dialog"
        // without to update details of the new event. Note: the event is already added in UI.
        model.add(event);
    }

    public void onDelete(TimelineModificationEvent<Booking> e) {
        // get clone of the TimelineEvent to be deleted
        event = e.getTimelineEvent();
    }

    public void delete() {
        // delete booking in DB...

        // if everything was ok, delete the TimelineEvent in the model and update UI with the same response.
        // otherwise no server-side delete is necessary (see timelineWdgt.cancelDelete() in the p:ajax onstart).
        // we assume, delete in DB was successful
        TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(":form:timeline");
        model.delete(event, timelineUpdater);

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "The booking " + getRoom() + " has been deleted", null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void saveDetails() {
        // save the updated booking in DB...

        // if everything was ok, update the TimelineEvent in the model and update UI with the same response.
        // otherwise no server-side update is necessary because UI is already up-to-date.
        // we assume, save in DB was successful
        TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(":form:timeline");
        model.update(event, timelineUpdater);

        FacesMessage msg
                = new FacesMessage(FacesMessage.SEVERITY_INFO, "The booking details " + getRoom() + " have been saved", null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public TimelineModel<Booking, ?> getModel() {
        return model;
    }

    public TimelineEvent<Booking> getEvent() {
        return event;
    }

    public void setEvent(TimelineEvent<Booking> event) {
        this.event = event;
    }

    public long getZoomMax() {
        return zoomMax;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public boolean isEditableTime() {
        return editableTime;
    }

    public void toggleEditableTime() {
        editableTime = !editableTime;
    }

    public String getDeleteMessage() {
        Integer room = event.getData().getRoomNumber();
        if (room == null) {
            return "Do you really want to delete the new booking?";
        }

        return "Do you really want to delete the booking for the room " + room + "?";
    }

    public String getRoom() {
        Integer room = event.getData().getRoomNumber();
        if (room == null) {
            return "(new booking)";
        }
        else {
            return "(room " + room + ")";
        }
    }
}
