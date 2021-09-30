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
package org.primefaces.event.timeline;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import org.primefaces.event.AbstractAjaxBehaviorEvent;

import java.time.LocalDateTime;

public class TimelineLazyLoadEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    /**
     * start time of the first time range for lazy loading
     */
    private final LocalDateTime startDateFirst;

    /**
     * end time of the first time range for lazy loading
     */
    private final LocalDateTime endDateFirst;

    /**
     * start time of the second time range for lazy loading (if any)
     */
    private final LocalDateTime startDateSecond;

    /**
     * end time of the second time range for lazy loading (if any)
     */
    private final LocalDateTime endDateSecond;

    public TimelineLazyLoadEvent(UIComponent component, Behavior behavior, LocalDateTime startDateFirst, LocalDateTime endDateFirst,
                                 LocalDateTime startDateSecond, LocalDateTime endDateSecond) {
        super(component, behavior);
        this.startDateFirst = startDateFirst;
        this.endDateFirst = endDateFirst;
        this.startDateSecond = startDateSecond;
        this.endDateSecond = endDateSecond;
    }

    public LocalDateTime getStartDate() { // alias for getStartDateFirst()
        return startDateFirst;
    }

    public LocalDateTime getEndDate() { // alias for getEndDateFirst()
        return endDateFirst;
    }

    public LocalDateTime getStartDateFirst() {
        return startDateFirst;
    }

    public LocalDateTime getEndDateFirst() {
        return endDateFirst;
    }

    public LocalDateTime getStartDateSecond() {
        return startDateSecond;
    }

    public LocalDateTime getEndDateSecond() {
        return endDateSecond;
    }

    public boolean hasTwoRanges() {
        return startDateSecond != null && endDateSecond != null;
    }
}
