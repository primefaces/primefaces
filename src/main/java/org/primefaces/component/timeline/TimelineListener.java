/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        String widgetVar = timeline.resolveWidgetVar();

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
