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
package org.primefaces.component.schedule;

import java.util.*;

import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.util.Constants;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "schedule/schedule.css"),
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "moment/moment.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "schedule/schedule.js")
})
public class Schedule extends ScheduleBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Schedule";

    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("dateSelect", SelectEvent.class)
            .put("eventSelect", SelectEvent.class)
            .put("eventMove", ScheduleEntryMoveEvent.class)
            .put("eventResize", ScheduleEntryResizeEvent.class)
            .put("viewChange", SelectEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private java.util.Locale appropriateLocale;
    private TimeZone appropriateTimeZone;

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    java.util.Locale calculateLocale(FacesContext facesContext) {
        if (appropriateLocale == null) {
            appropriateLocale = LocaleUtils.resolveLocale(getLocale(), getClientId(facesContext));
        }

        return appropriateLocale;
    }

    protected TimeZone calculateTimeZone() {
        if (appropriateTimeZone == null) {
            Object usertimeZone = getTimeZone();
            if (usertimeZone != null) {
                if (usertimeZone instanceof String) {
                    appropriateTimeZone = TimeZone.getTimeZone((String) usertimeZone);
                }
                else if (usertimeZone instanceof java.util.TimeZone) {
                    appropriateTimeZone = (TimeZone) usertimeZone;
                }
                else {
                    throw new IllegalArgumentException("TimeZone could be either String or java.util.TimeZone");
                }
            }
            else {
                appropriateTimeZone = DEFAULT_TIME_ZONE;
            }
        }

        return appropriateTimeZone;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = getClientId(context);
        TimeZone tz = calculateTimeZone();

        if (isSelfRequest(context)) {

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            FacesEvent wrapperEvent = null;

            if (eventName.equals("dateSelect")) {
                Long milliseconds = Long.valueOf(params.get(clientId + "_selectedDate"));
                Calendar calendar = Calendar.getInstance(tz);
                calendar.setTimeInMillis(milliseconds - tz.getOffset(milliseconds));
                Date selectedDate = calendar.getTime();
                SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), selectedDate);
                selectEvent.setPhaseId(behaviorEvent.getPhaseId());

                wrapperEvent = selectEvent;
            }
            else if (eventName.equals("eventSelect")) {
                String selectedEventId = params.get(clientId + "_selectedEventId");
                ScheduleEvent selectedEvent = getValue().getEvent(selectedEventId);

                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), selectedEvent);
            }
            else if (eventName.equals("eventMove")) {
                String movedEventId = params.get(clientId + "_movedEventId");
                ScheduleEvent movedEvent = getValue().getEvent(movedEventId);
                int dayDelta = (int) Double.parseDouble(params.get(clientId + "_dayDelta"));
                int minuteDelta = (int) Double.parseDouble(params.get(clientId + "_minuteDelta"));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(movedEvent.getStartDate());
                calendar.setTimeZone(tz);
                calendar.add(Calendar.DATE, dayDelta);
                calendar.add(Calendar.MINUTE, minuteDelta);
                movedEvent.getStartDate().setTime(calendar.getTimeInMillis());

                calendar = Calendar.getInstance();
                calendar.setTime(movedEvent.getEndDate());
                calendar.setTimeZone(tz);
                calendar.add(Calendar.DATE, dayDelta);
                calendar.add(Calendar.MINUTE, minuteDelta);
                movedEvent.getEndDate().setTime(calendar.getTimeInMillis());

                wrapperEvent = new ScheduleEntryMoveEvent(this, behaviorEvent.getBehavior(), movedEvent, dayDelta, minuteDelta);
            }
            else if (eventName.equals("eventResize")) {
                String resizedEventId = params.get(clientId + "_resizedEventId");
                ScheduleEvent resizedEvent = getValue().getEvent(resizedEventId);
                int dayDelta = Integer.valueOf(params.get(clientId + "_dayDelta"));
                int minuteDelta = Integer.valueOf(params.get(clientId + "_minuteDelta"));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(resizedEvent.getEndDate());
                calendar.setTimeZone(tz);
                calendar.add(Calendar.DATE, dayDelta);
                calendar.add(Calendar.MINUTE, minuteDelta);
                resizedEvent.getEndDate().setTime(calendar.getTimeInMillis());

                wrapperEvent = new ScheduleEntryResizeEvent(this, behaviorEvent.getBehavior(), resizedEvent, dayDelta, minuteDelta);
            }
            else if (eventName.equals("viewChange")) {
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getView());
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    private boolean isSelfRequest(FacesContext context) {
        return getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        super.processUpdates(context);

        ValueExpression expr = getValueExpression(PropertyKeys.view.toString());
        if (expr != null) {
            expr.setValue(getFacesContext().getELContext(), getView());
            getStateHelper().remove(PropertyKeys.view);
        }
    }
}