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
package org.primefaces.component.datepicker;

import org.primefaces.util.CalendarUtils;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.primefaces.event.DateViewChangeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.*;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "datepicker/datepicker.js")
})
public class DatePicker extends DatePickerBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.DatePicker";
    public static final String CONTAINER_EXTENSION_CLASS = "p-datepicker";

    private static final Collection<String> EVENT_NAMES = LangUtils.unmodifiableList("blur", "change", "valueChange", "click", "dblclick",
            "focus", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "select", "dateSelect", "viewChange",
            "close");
    private static final Collection<String> UNOBSTRUSIVE_EVENT_NAMES = LangUtils.unmodifiableList("dateSelect", "viewChange", "close");
    private final Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>();

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

        if (isValid() && !isEmpty(value)) {
            boolean isDisabledDate = false;
            boolean isRangeDatesSequential = true;

            if (value instanceof Date) {
                isDisabledDate = validateDateValue(context, (Date) value);
            }
            else if (value instanceof List && getSelectionMode().equals("range")) {
                List rangeValues = (List) value;

                Date startDate = (Date) rangeValues.get(0);
                isDisabledDate = validateDateValue(context, startDate);

                if (!isDisabledDate) {
                    Date endDate = (Date) rangeValues.get(1);
                    isDisabledDate = validateDateValue(context, endDate);

                    if (isValid() && startDate.after(endDate)) {
                        setValid(false);
                        isRangeDatesSequential = false;
                    }
                }
            }

            if (!isValid()) {
                FacesMessage msg = null;
                String validatorMessage = getValidatorMessage();
                Object[] params = new Object[] {MessageFactory.getLabel(context, this)};
                if (validatorMessage != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
                }
                else if (isDisabledDate) {
                    msg = MessageFactory.getMessage(DATE_INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                }
                else if (!isRangeDatesSequential) {
                    msg = MessageFactory.getMessage(DATE_INVALID_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                }
                else {
                    msg = MessageFactory.getMessage(DATE_OUT_OF_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                }
                context.addMessage(getClientId(context), msg);
            }
        }
    }

    protected boolean validateDateValue(FacesContext context, Date date) {
        boolean isDisabledDate = false;

        Date minDate = CalendarUtils.getObjectAsDate(context, this, getMindate());
        if (minDate != null && !date.equals(minDate) && date.before(minDate)) {
            setValid(false);
        }

        if (isValid()) {
            Date maxDate = CalendarUtils.getObjectAsDate(context, this, getMaxdate());
            if (maxDate != null && !date.equals(maxDate) && date.after(maxDate)) {
                setValid(false);
            }
        }

        if (isValid()) {
            List<Object> disabledDates = getDisabledDates();
            if (disabledDates != null) {
                Calendar c = Calendar.getInstance(CalendarUtils.calculateTimeZone(getTimeZone()), calculateLocale(context));
                c.setTime(date);
                int sYear = c.get(Calendar.YEAR);
                int sMonth = c.get(Calendar.MONTH);
                int sDay = c.get(Calendar.DAY_OF_MONTH);

                for (Object disabledDate : disabledDates) {
                    if (disabledDate instanceof Date) {
                        c.clear();
                        c.setTime((Date) disabledDate);

                        if (sYear == c.get(Calendar.YEAR) && sMonth == c.get(Calendar.MONTH) && sDay == c.get(Calendar.DAY_OF_MONTH)) {
                            setValid(false);
                            isDisabledDate = true;
                            break;
                        }
                    }
                }
            }
        }

        if (isValid()) {
            List<Object> disabledDays = getDisabledDays();
            if (disabledDays != null) {
                Calendar c = Calendar.getInstance(CalendarUtils.calculateTimeZone(getTimeZone()), calculateLocale(context));
                c.setTime(date);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

                if (disabledDays.contains(dayOfWeek)) {
                    setValid(false);
                    isDisabledDate = true;
                }
            }
        }

        return isDisabledDate;
    }
}
