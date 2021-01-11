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
package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import org.primefaces.model.ScheduleEvent;

import java.time.Duration;

public class ScheduleEntryMoveEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private ScheduleEvent scheduleEvent;

    private int yearDelta;
    private int monthDelta;
    private int dayDelta;
    private int minuteDelta;

    public ScheduleEntryMoveEvent(UIComponent component, Behavior behavior, ScheduleEvent scheduleEvent,
                                  int yearDelta, int monthDelta, int dayDelta, int minuteDelta) {
        super(component, behavior);
        this.scheduleEvent = scheduleEvent;
        this.yearDelta = yearDelta;
        this.monthDelta = monthDelta;
        this.dayDelta = dayDelta;
        this.minuteDelta = minuteDelta;
    }

    public ScheduleEvent getScheduleEvent() {
        return scheduleEvent;
    }

    public int getYearDelta() {
        return yearDelta;
    }

    public int getMonthDelta() {
        return monthDelta;
    }

    public int getDayDelta() {
        return dayDelta;
    }

    public int getMinuteDelta() {
        return minuteDelta;
    }

    /**
     * Get the delta as duration.
     * Attention: Contains only dayDelta and minuteDelta. Does not contain yearDelta and monthDelta.
     * @return
     */
    public Duration getDeltaAsDuration() {
        Duration duration = Duration.ofMinutes(minuteDelta);
        duration = duration.plusDays(dayDelta);
        return duration;
    }
}
