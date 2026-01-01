/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.calendar;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.event.DateViewChangeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;

@FacesComponent(value = Calendar.COMPONENT_TYPE, namespace = Calendar.COMPONENT_FAMILY)
@FacesComponentDescription("Calendar is an input component used to provide a date.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "inputmask/inputmask.js")
@ResourceDependency(library = "primefaces", name = "calendar/calendar.css")
@ResourceDependency(library = "primefaces", name = "calendar/calendar.js")
public class Calendar extends CalendarBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Calendar";

    protected static final List<PrimeClientBehaviorEventKeys> UNOBTRUSIVE_EVENT_KEYS = LangUtils.unmodifiableList(
            ClientBehaviorEventKeys.dateSelect, ClientBehaviorEventKeys.viewChange, ClientBehaviorEventKeys.close);

    private Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>(1);

    @Override
    public Collection<PrimeClientBehaviorEventKeys> getUnobtrusiveClientBehaviorEventKeys() {
        return UNOBTRUSIVE_EVENT_KEYS;
    }

    @Override
    public Boolean getTimeOnlyWithoutDefault() {
        return (Boolean) getStateHelper().eval(PropertyKeys.timeOnly);
    }

    public boolean isPopup() {
        return "popup".equalsIgnoreCase(getMode());
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();

        if (isAjaxBehaviorEventSource(event)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.dateSelect) || isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.close)) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.viewChange)) {
                int month = Integer.parseInt(params.get(clientId + "_month"));
                int year = Integer.parseInt(params.get(clientId + "_year"));
                DateViewChangeEvent dateViewChangeEvent = new DateViewChangeEvent(this, behaviorEvent.getBehavior(), month, year);
                dateViewChangeEvent.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(dateViewChangeEvent);
            }
            else {
                super.queueEvent(event);        //regular events like change, click, blur
            }
        }
        else {
            super.queueEvent(event);            //valueChange
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if (isValid() && ComponentUtils.isRequestSource(this, context) && customEvents != null) {
            for (Map.Entry<String, AjaxBehaviorEvent> event : customEvents.entrySet()) {
                SelectEvent<?> selectEvent = new SelectEvent<>(this, event.getValue().getBehavior(), getValue());

                if (event.getValue().getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
                    selectEvent.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
                }
                else {
                    selectEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
                }

                super.queueEvent(selectEvent);
            }
        }
    }

    @Override
    protected void validateValue(FacesContext context, Object value) {
        super.validateValue(context, value);

        ValidationResult validationResult = ValidationResult.OK;

        if (isValid() && !isEmpty(value) && (value instanceof LocalDate || value instanceof LocalDateTime || value instanceof Date)) {
            LocalDate date = null;

            if (value instanceof LocalDate) {
                date = (LocalDate) value;
            }
            else if (value instanceof LocalDateTime) {
                date = ((LocalDateTime) value).toLocalDate();
            }
            else if (value instanceof LocalTime) {
                //no check necessary
            }
            else if (value instanceof Date) {
                date = CalendarUtils.convertDate2LocalDate((Date) value, CalendarUtils.calculateZoneId(getTimeZone()));
            }

            if (date != null) {
                LocalDate minDate = CalendarUtils.getObjectAsLocalDate(context, this, getMindate());
                LocalDate maxDate = CalendarUtils.getObjectAsLocalDate(context, this, getMaxdate());
                if (minDate != null && date.isBefore(minDate)) {
                    setValid(false);
                    if (maxDate != null) {
                        validationResult = ValidationResult.INVALID_OUT_OF_RANGE;
                    }
                    else {
                        validationResult = ValidationResult.INVALID_MIN_DATE;
                    }
                }

                if (isValid()) {
                    if (maxDate != null && date.isAfter(maxDate)) {
                        setValid(false);
                        if (minDate != null) {
                            validationResult = ValidationResult.INVALID_OUT_OF_RANGE;
                        }
                        else {
                            validationResult = ValidationResult.INVALID_MAX_DATE;
                        }
                    }
                }
            }

            if (!isValid()) {
                createFacesMessageFromValidationResult(context, validationResult);
            }
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        // reset component for MyFaces view pooling
        if (customEvents != null) {
            customEvents.clear();
        }

        return super.saveState(context);
    }
}