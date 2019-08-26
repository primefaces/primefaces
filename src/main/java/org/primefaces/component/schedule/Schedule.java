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
package org.primefaces.component.schedule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
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
import org.primefaces.util.CalendarUtils;
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

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("dateSelect", SelectEvent.class)
            .put("eventSelect", SelectEvent.class)
            .put("eventMove", ScheduleEntryMoveEvent.class)
            .put("eventResize", ScheduleEntryResizeEvent.class)
            .put("viewChange", SelectEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private java.util.Locale appropriateLocale;

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

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = getClientId(context);
        ZoneId zoneId = CalendarUtils.calculateZoneId(this.getTimeZone());

        if (isSelfRequest(context)) {

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            FacesEvent wrapperEvent = null;

            if (eventName.equals("dateSelect")) {
                Long milliseconds = Long.valueOf(params.get(clientId + "_selectedDate"));
                LocalDateTime selectedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), zoneId);
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
                int dayDelta = Double.valueOf(params.get(clientId + "_dayDelta")).intValue();
                int minuteDelta = Double.valueOf(params.get(clientId + "_minuteDelta")).intValue();

                LocalDateTime startDate = movedEvent.getStartDate();
                LocalDateTime endDate = movedEvent.getEndDate();
                startDate = startDate.plusDays(dayDelta).plusMinutes(minuteDelta);
                endDate = endDate.plusDays(dayDelta).plusMinutes(minuteDelta);
                movedEvent.setStartDate(startDate);
                movedEvent.setEndDate(endDate);

                wrapperEvent = new ScheduleEntryMoveEvent(this, behaviorEvent.getBehavior(), movedEvent, dayDelta, minuteDelta);
            }
            else if (eventName.equals("eventResize")) {
                String resizedEventId = params.get(clientId + "_resizedEventId");
                ScheduleEvent resizedEvent = getValue().getEvent(resizedEventId);
                int dayDelta = Double.valueOf(params.get(clientId + "_dayDelta")).intValue();
                int minuteDelta = Double.valueOf(params.get(clientId + "_minuteDelta")).intValue();

                LocalDateTime endDate = resizedEvent.getEndDate();
                endDate = endDate.plusDays(dayDelta).plusMinutes(minuteDelta);
                resizedEvent.setEndDate(endDate);

                wrapperEvent = new ScheduleEntryResizeEvent(this, behaviorEvent.getBehavior(), resizedEvent, dayDelta, minuteDelta);
            }
            else if (eventName.equals("viewChange")) {
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getView());
            }

            if (wrapperEvent == null) {
                throw new FacesException("Component " + this.getClass().getName() + " does not support event " + eventName + "!");
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