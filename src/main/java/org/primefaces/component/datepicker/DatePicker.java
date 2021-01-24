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
package org.primefaces.component.datepicker;

import java.time.*;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import org.primefaces.event.DateViewChangeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.datepicker.DateMetadata;
import org.primefaces.model.datepicker.DateMetadataModel;
import org.primefaces.model.datepicker.LazyDateMetadataModel;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "inputmask/inputmask.js")
@ResourceDependency(library = "primefaces", name = "datepicker/datepicker.js")
public class DatePicker extends DatePickerBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.DatePicker";
    public static final String CONTAINER_EXTENSION_CLASS = "p-datepicker";

    private Map<String, AjaxBehaviorEvent> customEvents = new HashMap<>(1);

    @Override
    public Collection<String> getEventNames() {
        return CALENDAR_EVENT_NAMES;
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
                if ("dateSelect".equals(eventName) || "close".equals(eventName)) {
                    customEvents.put(eventName, (AjaxBehaviorEvent) event);
                }
                else if ("viewChange".equals(eventName)) {
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

        if (isValid() && ComponentUtils.isRequestSource(this, context) && customEvents != null) {
            for (Map.Entry<String, AjaxBehaviorEvent> event : customEvents.entrySet()) {
                SelectEvent<?> selectEvent = new SelectEvent(this, event.getValue().getBehavior(), getValue());

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

        if (isValid() && !isEmpty(value)) {
            ValidationResult validationResult = validateValueInternal(context, value);

            if (!isValid()) {
                createFacesMessageFromValidationResult(context, validationResult);
            }
        }
    }

    protected ValidationResult validateValueInternal(FacesContext context, Object value) {
        if (isTimeOnly()) {
            LocalTime timeValue = CalendarUtils.getObjectAsLocalTime(context, this, value);
            return validateTimeOnlyValue(context, timeValue);
        }

        ValidationResult validationResult = ValidationResult.OK;

        if (value instanceof LocalDate) {
            validationResult = validateDateValue(context, (LocalDate) value);
        }
        else if (value instanceof LocalDateTime) {
            if (isShowTime()) {
                validationResult = validateDateValue(context, ((LocalDateTime) value).toLocalDate(), ((LocalDateTime) value).toLocalTime());
            }
            else {
                validationResult = validateDateValue(context, ((LocalDateTime) value).toLocalDate());
            }
        }
        else if (value instanceof YearMonth) {
            validationResult = validateDateValue(context, ((YearMonth) value).atDay(1));
        }
        else if (value instanceof Date) {
            if (isShowTime()) {
                ZoneId zoneId = CalendarUtils.calculateZoneId(getTimeZone());
                validationResult = validateDateValue(context, CalendarUtils.convertDate2LocalDate((Date) value, zoneId),
                        CalendarUtils.convertDate2LocalTime((Date) value, zoneId));
            }
            else {
                validationResult = validateDateValue(context, CalendarUtils.convertDate2LocalDate((Date) value, CalendarUtils.calculateZoneId(getTimeZone())));
            }
        }
        else if (value instanceof List && getSelectionMode().equals("multiple")) {
            //TODO: needs to be validated
        }
        else if (value instanceof List && getSelectionMode().equals("range")) {
            List<?> rangeValues = (List) value;

            if (rangeValues.get(0) instanceof LocalDate) {
                LocalDate startDate = (LocalDate) rangeValues.get(0);
                validationResult = validateDateValue(context, startDate);

                if (isValid()) {
                    LocalDate endDate = (LocalDate) rangeValues.get(1);
                    validationResult = validateDateValue(context, endDate);

                    if (isValid() && startDate.isAfter(endDate)) {
                        setValid(false);
                        validationResult = ValidationResult.INVALID_RANGE_DATES_SEQUENTIAL;
                    }
                }
            }
            else if (rangeValues.get(0) instanceof Date) {
                Date startDate = (Date) rangeValues.get(0);
                validationResult = validateDateValue(context, CalendarUtils.convertDate2LocalDate(startDate, CalendarUtils.calculateZoneId(getTimeZone())));

                if (isValid()) {
                    Date endDate = (Date) rangeValues.get(1);
                    validationResult = validateDateValue(context, CalendarUtils.convertDate2LocalDate(endDate, CalendarUtils.calculateZoneId(getTimeZone())));

                    if (isValid() && startDate.after(endDate)) {
                        setValid(false);
                        validationResult = ValidationResult.INVALID_RANGE_DATES_SEQUENTIAL;
                    }
                }
            }
            else {
                //no other type possible (as of 05/2019)
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, rangeValues.get(0).getClass().getTypeName() + " not supported", null);
                context.addMessage(getClientId(context), message);
            }
        }

        return validationResult;
    }

    protected ValidationResult validateDateValue(FacesContext context, LocalDate date) {
        return validateDateValue(context, date, null);
    }

    protected ValidationResult validateDateValue(FacesContext context, LocalDate date, LocalTime timePart) {
        LocalDate minDate = CalendarUtils.getObjectAsLocalDate(context, this, getMindate());
        LocalDate maxDate = CalendarUtils.getObjectAsLocalDate(context, this, getMaxdate());
        LocalTime minTime = null;
        LocalTime maxTime = null;
        if (timePart != null) {
            minTime = CalendarUtils.getObjectAsLocalTime(context, this, getMindate());
            maxTime = CalendarUtils.getObjectAsLocalTime(context, this, getMaxdate());
        }

        if (minDate != null && (date.isBefore(minDate) || (minTime != null && date.equals(minDate) && timePart.isBefore(minTime)))) {
            setValid(false);
            if (maxDate != null) {
                return ValidationResult.INVALID_OUT_OF_RANGE;
            }
            else {
                return ValidationResult.INVALID_MIN_DATE;
            }
        }

        if (isValid()) {
            if (maxDate != null && (date.isAfter(maxDate) || maxTime != null && date.equals(maxDate) && timePart.isAfter(maxTime))) {
                setValid(false);
                if (minDate != null) {
                    return ValidationResult.INVALID_OUT_OF_RANGE;
                }
                else {
                    return ValidationResult.INVALID_MAX_DATE;
                }
            }
        }

        if (isValid()) {
            List<Object> disabledDates = getDisabledDates();
            if (disabledDates != null) {

                for (Object disabledDate : disabledDates) {
                    if (disabledDate instanceof LocalDate) {
                        if (((LocalDate) disabledDate).isEqual(date)) {
                            setValid(false);
                            return ValidationResult.INVALID_DISABLED_DATE;
                        }
                    }
                    else if (disabledDate instanceof Date) {
                        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(CalendarUtils.calculateZoneId(getTimeZone())), calculateLocale(context));
                        c.setTime((Date) disabledDate);

                        if (date.getYear() == c.get(Calendar.YEAR) &&
                                date.getMonthValue() == c.get(Calendar.MONTH) &&
                                date.getDayOfMonth() == c.get(Calendar.DAY_OF_MONTH)) {
                            setValid(false);
                            return ValidationResult.INVALID_DISABLED_DATE;
                        }
                    }
                }
            }
        }

        if (isValid()) {
            List<Integer> disabledDays = getDisabledDays();
            if (disabledDays != null) {
                if (disabledDays.contains(date.getDayOfWeek().getValue())) {
                    setValid(false);
                    return ValidationResult.INVALID_DISABLED_DATE;
                }
            }
        }

        return ValidationResult.OK;
    }

    protected ValidationResult validateTimeOnlyValue(FacesContext context, LocalTime time) {
        LocalTime minTime = CalendarUtils.getObjectAsLocalTime(context, this, getMindate());
        LocalTime maxTime = CalendarUtils.getObjectAsLocalTime(context, this, getMaxdate());

        if (minTime != null && !time.equals(minTime) && time.isBefore(minTime)) {
            setValid(false);
            if (maxTime != null) {
                return ValidationResult.INVALID_OUT_OF_RANGE;
            }
            else {
                return ValidationResult.INVALID_MIN_DATE;
            }
        }

        if (isValid()) {
            if (maxTime != null && !time.equals(maxTime) && time.isAfter(maxTime)) {
                setValid(false);
                if (minTime != null) {
                    return ValidationResult.INVALID_OUT_OF_RANGE;
                }
                else {
                    return ValidationResult.INVALID_MAX_DATE;
                }
            }
        }

        return ValidationResult.OK;
    }

    @Override
    public Object saveState(FacesContext context) {
        // reset component for MyFaces view pooling
        if (customEvents != null) {
            customEvents.clear();
        }

        return super.saveState(context);
    }

    protected List<Object> getInitialDisabledDates(FacesContext context) {
        List<Object> disabledDates = getDisabledDates();
        if (disabledDates != null) {
            return disabledDates;
        }
        DateMetadataModel model = getModel();
        if (model != null) {
            disabledDates = new ArrayList<>();
            for (Map.Entry<LocalDate, DateMetadata> entry : model.getDateMetadata().entrySet()) {
                if (entry.getValue().isDisabled()) {
                    disabledDates.add(entry.getKey());
                }
            }
        }
        return disabledDates;
    }

    protected void loadInitialLazyMetadata(FacesContext context) {
        DateMetadataModel model = getModel();
        if (model instanceof LazyDateMetadataModel) {
            LocalDate startDate = getInitalStartDate(context);
            LocalDate endDate = startDate.plusMonths(getNumberOfMonths()).minusDays(1);

            LazyDateMetadataModel lazyModel = ((LazyDateMetadataModel) model);
            lazyModel.clear();
            lazyModel.loadDateMetadata(startDate, endDate);
        }
    }

    protected LocalDate getInitalStartDate(FacesContext context) {
        LocalDate date = CalendarUtils.getObjectAsLocalDate(context, this, getValue());
        if (date == null) {
            date = LocalDate.from(CalendarUtils.now(this));
        }
        return date.withDayOfMonth(1);
    }
}
