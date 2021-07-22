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
package org.primefaces.integrationtests.schedule;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Schedule001 implements Serializable {

    private static final long serialVersionUID = 2014707183985306105L;

    private ScheduleModel eventModel;
    private String locale = "en";

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();

        DefaultScheduleEvent event = DefaultScheduleEvent.builder()
                    .title("Champions League Match")
                    .startDate(previousDay8Pm())
                    .endDate(previousDay11Pm())
                    .description("Team A vs. Team B")
                    .build();
        eventModel.addEvent(event);

        event = DefaultScheduleEvent.builder()
                    .title("Birthday Party")
                    .startDate(today1Pm())
                    .endDate(today6Pm())
                    .description("Aragon")
                    .overlapAllowed(true)
                    .build();
        eventModel.addEvent(event);

        event = DefaultScheduleEvent.builder()
                    .title("Breakfast at Tiffanys")
                    .startDate(nextDay9Am())
                    .endDate(nextDay11Am())
                    .description("all you can eat")
                    .overlapAllowed(true)
                    .build();
        eventModel.addEvent(event);

        event = DefaultScheduleEvent.builder()
                    .title("Plant the new garden stuff")
                    .startDate(theDayAfter3Pm())
                    .endDate(fourDaysLater3pm())
                    .description("Trees, flowers, ...")
                    .build();
        eventModel.addEvent(event);

        DefaultScheduleEvent scheduleEventAllDay = DefaultScheduleEvent.builder()
                    .title("Holidays (AllDay)")
                    .startDate(sevenDaysLater0am())
                    .endDate(eightDaysLater0am())
                    .description("sleep as long as you want")
                    .allDay(true)
                    .build();
        eventModel.addEvent(scheduleEventAllDay);
    }

    private LocalDateTime previousDay8Pm() {
        return LocalDateTime.now().minusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime previousDay11Pm() {
        return LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime today1Pm() {
        return LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime theDayAfter3Pm() {
        return LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime today6Pm() {
        return LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime nextDay9Am() {
        return LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime nextDay11Am() {
        return LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime fourDaysLater3pm() {
        return LocalDateTime.now().plusDays(4).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime sevenDaysLater0am() {
        return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime eightDaysLater0am() {
        return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event selected",
                    selectEvent.getObject().getGroupId() + ": " + selectEvent.getObject().getTitle());
        addMessage(message);
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Date selected", selectEvent.getObject().toString());
        addMessage(message);
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Delta:" + event.getDeltaAsDuration());
        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized",
                    "Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());
        addMessage(message);
    }

    public void english() {
        setLocale("en");
    }

    public void french() {
        setLocale("fr");
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}
