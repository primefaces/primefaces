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

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PhaseListener;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

public class TimelineListener implements SystemEventListener {

    @Override
    public void processEvent(SystemEvent cse) throws AbortProcessingException {

        FacesContext context = FacesContext.getCurrentInstance();
        Timeline timeline = (Timeline) cse.getSource();
        String widgetVar = timeline.resolveWidgetVar(context);

        Map<String, TimelineUpdater> map
                = (Map<String, TimelineUpdater>) context.getAttributes().get(TimelineUpdater.class.getName());
        if (map == null) {
            map = new HashMap<>();
            context.getAttributes().put(TimelineUpdater.class.getName(), map);
        }

        boolean alreadyRegistred = false;
        for (PhaseListener listener : context.getViewRoot().getPhaseListeners()) {
            if (listener instanceof DefaultTimelineUpdater
                    && ((DefaultTimelineUpdater) listener).getWidgetVar().equals(widgetVar)) {

                if (!map.containsKey(widgetVar)) {
                    map.put(widgetVar, (DefaultTimelineUpdater) listener);
                }

                alreadyRegistred = true;
            }
        }

        if (!alreadyRegistred) {
            DefaultTimelineUpdater timelineUpdater = new DefaultTimelineUpdater();
            timelineUpdater.setWidgetVar(widgetVar);
            map.put(widgetVar, timelineUpdater);

            context.getViewRoot().addPhaseListener(timelineUpdater);
        }
    }

    @Override
    public boolean isListenerForSource(Object o) {
        return true;
    }

}
