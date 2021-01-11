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
package org.primefaces.component.schedule;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.util.*;

@ResourceDependency(library = "primefaces", name = "schedule/schedule.css")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "moment/moment.js")
@ResourceDependency(library = "primefaces", name = "moment/moment-timezone-with-data.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "schedule/schedule.js")
public class Schedule extends ScheduleBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Schedule";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("dateSelect", SelectEvent.class)
            .put("dateDblSelect", SelectEvent.class)
            .put("eventSelect", SelectEvent.class)
            .put("eventMove", ScheduleEntryMoveEvent.class)
            .put("eventResize", ScheduleEntryResizeEvent.class)
            .put("viewChange", SelectEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    Locale calculateLocale(FacesContext facesContext) {
        return LocaleUtils.resolveLocale(facesContext, getLocale(), getClientId(facesContext));
    }

    public boolean isEventRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_event");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = getClientId(context);

        if (ComponentUtils.isRequestSource(this, context)) {

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            FacesEvent wrapperEvent = null;

            if ("dateSelect".equals(eventName) || "dateDblSelect".equals(eventName)) {
                String selectedDateStr = params.get(clientId + "_selectedDate");
                ZoneId zoneId = CalendarUtils.calculateZoneId(this.getTimeZone());
                LocalDateTime selectedDate =  CalendarUtils.toLocalDateTime(zoneId, selectedDateStr);
                SelectEvent<?> selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), selectedDate);
                selectEvent.setPhaseId(behaviorEvent.getPhaseId());

                wrapperEvent = selectEvent;
            }
            else if ("eventSelect".equals(eventName)) {
                String selectedEventId = params.get(clientId + "_selectedEventId");
                ScheduleEvent<?> selectedEvent = getValue().getEvent(selectedEventId);

                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), selectedEvent);
            }
            else if ("eventMove".equals(eventName)) {
                String movedEventId = params.get(clientId + "_movedEventId");
                ScheduleEvent<?> movedEvent = getValue().getEvent(movedEventId);
                int yearDelta = Double.valueOf(params.get(clientId + "_yearDelta")).intValue();
                int monthDelta = Double.valueOf(params.get(clientId + "_monthDelta")).intValue();
                int dayDelta = Double.valueOf(params.get(clientId + "_dayDelta")).intValue();
                int minuteDelta = Double.valueOf(params.get(clientId + "_minuteDelta")).intValue();
                boolean allDay = Boolean.parseBoolean(params.get(clientId + "_allDay"));

                LocalDateTime startDate = movedEvent.getStartDate();
                LocalDateTime endDate = movedEvent.getEndDate();
                startDate = startDate.plusYears(yearDelta).plusMonths(monthDelta).plusDays(dayDelta).plusMinutes(minuteDelta);
                endDate = endDate.plusYears(yearDelta).plusMonths(monthDelta).plusDays(dayDelta).plusMinutes(minuteDelta);
                movedEvent.setAllDay(allDay);
                movedEvent.setStartDate(startDate);
                movedEvent.setEndDate(endDate);

                wrapperEvent = new ScheduleEntryMoveEvent(this, behaviorEvent.getBehavior(), movedEvent,
                        yearDelta, monthDelta, dayDelta, minuteDelta);
            }
            else if ("eventResize".equals(eventName)) {
                String resizedEventId = params.get(clientId + "_resizedEventId");
                ScheduleEvent<?> resizedEvent = getValue().getEvent(resizedEventId);

                int startDeltaYear = Double.valueOf(params.get(clientId + "_startDeltaYear")).intValue();
                int startDeltaMonth = Double.valueOf(params.get(clientId + "_startDeltaMonth")).intValue();
                int startDeltaDay = Double.valueOf(params.get(clientId + "_startDeltaDay")).intValue();
                int startDeltaMinute = Double.valueOf(params.get(clientId + "_startDeltaMinute")).intValue();

                LocalDateTime startDate = resizedEvent.getStartDate();
                startDate = startDate.plusYears(startDeltaYear).plusMonths(startDeltaMonth).plusDays(startDeltaDay).plusMinutes(startDeltaMinute);
                resizedEvent.setStartDate(startDate);

                int endDeltaYear = Double.valueOf(params.get(clientId + "_endDeltaYear")).intValue();
                int endDeltaMonth = Double.valueOf(params.get(clientId + "_endDeltaMonth")).intValue();
                int endDeltaDay = Double.valueOf(params.get(clientId + "_endDeltaDay")).intValue();
                int endDeltaMinute = Double.valueOf(params.get(clientId + "_endDeltaMinute")).intValue();

                LocalDateTime endDate = resizedEvent.getEndDate();
                endDate = endDate.plusYears(endDeltaYear).plusMonths(endDeltaMonth).plusDays(endDeltaDay).plusMinutes(endDeltaMinute);
                resizedEvent.setEndDate(endDate);

                wrapperEvent = new ScheduleEntryResizeEvent(this, behaviorEvent.getBehavior(), resizedEvent,
                        startDeltaYear, startDeltaMonth, startDeltaDay, startDeltaMinute,
                        endDeltaYear, endDeltaMonth, endDeltaDay, endDeltaMinute);
            }
            else if ("viewChange".equals(eventName)) {
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

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        super.processUpdates(context);

        ELContext elContext = getFacesContext().getELContext();
        ValueExpression expr = ValueExpressionAnalyzer.getExpression(elContext,
                getValueExpression(PropertyKeys.view.toString()));
        if (expr != null) {
            expr.setValue(elContext, getView());
            getStateHelper().remove(PropertyKeys.view);
        }
    }
}