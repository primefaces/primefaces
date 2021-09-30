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

public class ScheduleEntryResizeEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private ScheduleEvent scheduleEvent;

    private int yearDeltaStart;
    private int monthDeltaStart;
    private int dayDeltaStart;
    private int minuteDeltaStart;

    private int yearDeltaEnd;
    private int monthDeltaEnd;
    private int dayDeltaEnd;
    private int minuteDeltaEnd;

    //CHECKSTYLE:OFF
    public ScheduleEntryResizeEvent(UIComponent component, Behavior behavior, ScheduleEvent scheduleEvent,
                                    int yearDeltaStart, int monthDeltaStart, int dayDeltaStart, int minuteDeltaStart,
                                    int yearDeltaEnd, int monthDeltaEnd, int dayDeltaEnd, int minuteDeltaEnd) {
        super(component, behavior);
        this.scheduleEvent = scheduleEvent;

        this.yearDeltaStart = yearDeltaStart;
        this.monthDeltaStart = monthDeltaStart;
        this.dayDeltaStart = dayDeltaStart;
        this.minuteDeltaStart = minuteDeltaStart;

        this.yearDeltaEnd = yearDeltaEnd;
        this.monthDeltaEnd = monthDeltaEnd;
        this.dayDeltaEnd = dayDeltaEnd;
        this.minuteDeltaEnd = minuteDeltaEnd;
    }
    //CHECKSTYLE:ON

    public ScheduleEvent getScheduleEvent() {
        return scheduleEvent;
    }

    public int getYearDeltaStart() {
        return yearDeltaStart;
    }

    public int getMonthDeltaStart() {
        return monthDeltaStart;
    }

    public int getDayDeltaStart() {
        return dayDeltaStart;
    }

    public int getMinuteDeltaStart() {
        return minuteDeltaStart;
    }

    public int getYearDeltaEnd() {
        return yearDeltaEnd;
    }

    public int getMonthDeltaEnd() {
        return monthDeltaEnd;
    }

    public int getDayDeltaEnd() {
        return dayDeltaEnd;
    }

    public int getMinuteDeltaEnd() {
        return minuteDeltaEnd;
    }

    /**
     * Get the start-delta as duration.
     * Attention: Contains only dayDelta and minuteDelta. Does not contain yearDelta and monthDelta.
     * @return
     */
    public Duration getDeltaStartAsDuration() {
        Duration duration = Duration.ofMinutes(minuteDeltaStart);
        duration = duration.plusDays(dayDeltaStart);
        return duration;
    }

    /**
     * Get the end-delta as duration.
     * Attention: Contains only dayDelta and minuteDelta. Does not contain yearDelta and monthDelta.
     * @return
     */
    public Duration getDeltaEndAsDuration() {
        Duration duration = Duration.ofMinutes(minuteDeltaEnd);
        duration = duration.plusDays(dayDeltaEnd);
        return duration;
    }
}
