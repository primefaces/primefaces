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

public class TimelineAddEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String group;

    public TimelineAddEvent(UIComponent component, Behavior behavior, String id, LocalDateTime startDate, LocalDateTime endDate, String group) {
        super(component, behavior);
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getGroup() {
        return group;
    }
}
