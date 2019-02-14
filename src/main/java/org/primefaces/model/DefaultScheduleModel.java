/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultScheduleModel implements ScheduleModel, Serializable {

    private static final long serialVersionUID = 1L;

    private List<ScheduleEvent> events;
    private boolean eventLimit = false;

    public DefaultScheduleModel() {
        events = new ArrayList<>();
    }

    public DefaultScheduleModel(List<ScheduleEvent> events) {
        this.events = events;
    }

    @Override
    public void addEvent(ScheduleEvent event) {
        event.setId(UUID.randomUUID().toString());

        events.add(event);
    }

    @Override
    public boolean deleteEvent(ScheduleEvent event) {
        return events.remove(event);
    }

    @Override
    public List<ScheduleEvent> getEvents() {
        return events;
    }

    @Override
    public ScheduleEvent getEvent(String id) {
        for (ScheduleEvent event : events) {
            if (event.getId().equals(id)) {
                return event;
            }
        }

        return null;
    }

    @Override
    public void updateEvent(ScheduleEvent event) {
        int index = -1;

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(event.getId())) {
                index = i;

                break;
            }
        }

        if (index >= 0) {
            events.set(index, event);
        }
    }

    @Override
    public int getEventCount() {
        return events.size();
    }

    @Override
    public void clear() {
        events = new ArrayList<>();
    }

    @Override
    public boolean isEventLimit() {
        return eventLimit;
    }

    public void setEventLimit(boolean eventLimit) {
        this.eventLimit = eventLimit;
    }
}
