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
package org.primefaces.component.rating;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.RateEvent;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class Rating extends RatingBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Rating";

    public static final String CONTAINER_CLASS = "ui-rating";
    public static final String CANCEL_CLASS = "ui-rating-cancel";
    public static final String STAR_CLASS = "ui-rating-star";
    public static final String STAR_ON_CLASS = "ui-rating-star ui-rating-star-on";

    private static final String DEFAULT_EVENT = "rate";
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("rate", RateEvent.class)
            .put("cancel", null)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private final Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (event instanceof AjaxBehaviorEvent) {
            String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if (eventName.equals("rate")) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
            else if (eventName.equals("cancel")) {
                super.queueEvent(event);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if (isValid()) {
            for (Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext(); ) {
                AjaxBehaviorEvent behaviorEvent = customEvents.get(customEventIter.next());
                RateEvent rateEvent = new RateEvent(this, behaviorEvent.getBehavior(), getValue());

                rateEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(rateEvent);
            }
        }
    }
}