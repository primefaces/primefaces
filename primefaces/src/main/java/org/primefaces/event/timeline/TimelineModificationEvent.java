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
import org.primefaces.model.timeline.TimelineEvent;

public class TimelineModificationEvent<T> extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private final TimelineEvent<T> timelineEvent;

    public TimelineModificationEvent(UIComponent component, Behavior behavior, TimelineEvent<T> timelineEvent) {
        super(component, behavior);
        this.timelineEvent = timelineEvent;
    }

    /**
     * Gets a cloned {@link TimelineEvent} with the modifications if any.
     * You should update your {@link org.primefaces.model.timeline.TimelineModel} with this instance to keep sync between
     * UI and model.
     * @return
     */
    public TimelineEvent<T> getTimelineEvent() {
        return timelineEvent;
    }
}
