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
package org.primefaces.component.timeline;

import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.model.timeline.TimelineEvent;

public abstract class TimelineUpdater {

    /**
     * The same id of the Timeline component in terms of findComponent() as in {@link #getCurrentInstance(String)}
     */
    protected String id;

    /**
     * Gets the current thread-safe TimelineUpdater instance by Id.
     *
     * @param id Id of the Timeline component in terms of findComponent()
     * @return TimelineUpdater instance.
     * @throws FacesException if the Timeline component can not be found by the given Id
     */
    public static TimelineUpdater getCurrentInstance(String id) {
        FacesContext context = FacesContext.getCurrentInstance();

        @SuppressWarnings("unchecked")
        Map<String, TimelineUpdater> map = (Map<String, TimelineUpdater>) context.getAttributes().get(TimelineUpdater.class.getName());
        if (map == null) {
            return null;
        }

        UIComponent timeline = context.getViewRoot().findComponent(id);
        if (timeline == null || !(timeline instanceof Timeline)) {
            throw new FacesException("Timeline component with Id " + id + " was not found");
        }

        TimelineUpdater timelineUpdater = map.get(((Timeline) timeline).resolveWidgetVar(context));
        if (timelineUpdater != null) {
            timelineUpdater.id = id;
        }

        return timelineUpdater;
    }

    public abstract void add(TimelineEvent event);

    public abstract void update(TimelineEvent event, int index);

    public abstract void delete(int index);

    public abstract void select(int index);

    public abstract void clear();
}
