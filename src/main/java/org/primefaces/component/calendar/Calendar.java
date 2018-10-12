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
package org.primefaces.component.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.convert.DateTimeConverter;
import org.primefaces.event.DateViewChangeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.*;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class Calendar extends CalendarBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Calendar";

    public static final String CONTAINER_CLASS = "ui-calendar";
    public static final String INPUT_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";
    public static final String DATE_OUT_OF_RANGE_MESSAGE_ID = "primefaces.calendar.OUT_OF_RANGE";

    private static final Collection<String> EVENT_NAMES = LangUtils.unmodifiableList("blur", "change", "valueChange", "click", "dblclick",
            "focus", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "select", "dateSelect", "viewChange",
            "close");
    private static final Collection<String> UNOBSTRUSIVE_EVENT_NAMES = LangUtils.unmodifiableList("dateSelect", "viewChange", "close");
    private final Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>();
    private java.util.Locale calculatedLocale;
    private java.util.TimeZone appropriateTimeZone;
    private String timeOnlyPattern = null;
    private boolean conversionFailed = false;

    public java.util.Locale calculateLocale(FacesContext facesContext) {
        if (calculatedLocale == null) {
            calculatedLocale = LocaleUtils.resolveLocale(getLocale(), getClientId(facesContext));
        }

        return calculatedLocale;
    }

    public java.util.TimeZone calculateTimeZone() {
        if (appropriateTimeZone == null) {
            Object usertimeZone = getTimeZone();
            if (usertimeZone != null) {
                if (usertimeZone instanceof String) {
                    appropriateTimeZone = java.util.TimeZone.getTimeZone((String) usertimeZone);
                }
                else if (usertimeZone instanceof java.util.TimeZone) {
                    appropriateTimeZone = (java.util.TimeZone) usertimeZone;
                }
                else {
                    throw new IllegalArgumentException("TimeZone could be either String or java.util.TimeZone");
                }
            }
            else {
                appropriateTimeZone = java.util.TimeZone.getDefault();
            }
        }

        return appropriateTimeZone;
    }

    public boolean isPopup() {
        return getMode().equalsIgnoreCase("popup");
    }

    public boolean hasTime() {
        String pattern = getPattern();

        return (pattern != null && (pattern.contains("HH") || pattern.contains("mm") || pattern.contains("ss")));
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public Collection<String> getUnobstrusiveEventNames() {
        return UNOBSTRUSIVE_EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && (event instanceof AjaxBehaviorEvent)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName != null) {
                if (eventName.equals("dateSelect")) {
                    customEvents.put("dateSelect", (AjaxBehaviorEvent) event);
                }
                else if (eventName.equals("close")) {
                    customEvents.put("close", (AjaxBehaviorEvent) event);
                }
                else if (eventName.equals("viewChange")) {
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
        }
        else {
            super.queueEvent(event);            //valueChange
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if (isValid() && ComponentUtils.isRequestSource(this, context)) {
            for (Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext(); ) {
                AjaxBehaviorEvent behaviorEvent = customEvents.get(customEventIter.next());
                SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getValue());

                if (behaviorEvent.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
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

        if (isValid() && !isEmpty(value) && value instanceof Date) {
            Date date = (Date) value;

            Date minDate = CalendarUtils.getObjectAsDate(context, this, getMindate());
            if (minDate != null && date.before(minDate)) {
                setValid(false);
            }

            if (isValid()) {
                Date maxDate = CalendarUtils.getObjectAsDate(context, this, getMaxdate());
                if (maxDate != null && date.after(maxDate)) {
                    setValid(false);
                }
            }

            if (!isValid()) {
                FacesMessage msg = null;
                String validatorMessage = getValidatorMessage();
                if (validatorMessage != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
                }
                else {
                    msg = MessageFactory.getMessage(DATE_OUT_OF_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, null);
                }
                context.addMessage(getClientId(context), msg);
            }
        }
    }

    public String calculatePattern() {
        String pattern = getPattern();
        Locale locale = calculateLocale(getFacesContext());

        return pattern == null ? ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale)).toPattern() : pattern;
    }

    public String calculateTimeOnlyPattern() {
        if (timeOnlyPattern == null) {
            String localePattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, calculateLocale(getFacesContext()))).toPattern();
            String userTimePattern = getPattern();

            timeOnlyPattern = localePattern + " " + userTimePattern;
        }

        return timeOnlyPattern;
    }

    public boolean isConversionFailed() {
        return conversionFailed;
    }

    public void setConversionFailed(boolean value) {
        conversionFailed = value;
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    @Override
    public Converter getConverter() {
        Converter converter = super.getConverter();

        if (converter == null && PrimeApplicationContext.getCurrentInstance(getFacesContext()).getConfig().isClientSideValidationEnabled()) {
            DateTimeConverter con = new DateTimeConverter();
            con.setPattern(calculatePattern());
            con.setTimeZone(calculateTimeZone());
            con.setLocale(calculateLocale(getFacesContext()));

            return con;
        }

        return converter;
    }

}