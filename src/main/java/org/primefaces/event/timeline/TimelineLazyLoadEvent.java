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
package org.primefaces.event.timeline;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class TimelineLazyLoadEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    /**
     * start time of the first time range for lazy loading
     */
    private Date startDateFirst;

    /**
     * end time of the first time range for lazy loading
     */
    private Date endDateFirst;

    /**
     * start time of the second time range for lazy loading (if any)
     */
    private Date startDateSecond;

    /**
     * end time of the second time range for lazy loading (if any)
     */
    private Date endDateSecond;

    public TimelineLazyLoadEvent(UIComponent component, Behavior behavior, Date startDateFirst, Date endDateFirst,
            Date startDateSecond, Date endDateSecond) {
        super(component, behavior);
        this.startDateFirst = startDateFirst;
        this.endDateFirst = endDateFirst;
        this.startDateSecond = startDateSecond;
        this.endDateSecond = endDateSecond;
    }

    public Date getStartDate() { // alias for getStartDateFirst()
        return startDateFirst;
    }

    public Date getEndDate() { // alias for getEndDateFirst()
        return endDateFirst;
    }

    public Date getStartDateFirst() {
        return startDateFirst;
    }

    public Date getEndDateFirst() {
        return endDateFirst;
    }

    public Date getStartDateSecond() {
        return startDateSecond;
    }

    public Date getEndDateSecond() {
        return endDateSecond;
    }

    public boolean hasTwoRanges() {
        return startDateSecond != null && endDateSecond != null;
    }
}
