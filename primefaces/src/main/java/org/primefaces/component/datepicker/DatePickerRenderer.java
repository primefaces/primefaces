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
package org.primefaces.component.datepicker;

import org.primefaces.component.api.UICalendar;
import org.primefaces.component.calendar.BaseCalendarRenderer;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.datepicker.DateMetadata;
import org.primefaces.model.datepicker.DateMetadataModel;
import org.primefaces.model.datepicker.LazyDateMetadataModel;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

import org.json.JSONObject;

@FacesRenderer(rendererType = DatePicker.DEFAULT_RENDERER, componentFamily = DatePicker.COMPONENT_FAMILY)
public class DatePickerRenderer extends BaseCalendarRenderer<DatePicker> {

    @Override
    public void decode(FacesContext context, DatePicker component) {
        if (!shouldDecode(component)) {
            return;
        }

        initializeDefaults(context, component);
        super.decode(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, DatePicker component) throws IOException {
        if (component.getModel() instanceof LazyDateMetadataModel && ComponentUtils.isRequestSource(component, context, "viewChange")) {
            encodeDateMetadata(context, component);
            return;
        }

        initializeDefaults(context, component);

        super.encodeEnd(context, component);
    }

    /**
     * Initializes the default settings for the DatePicker component based on its pattern and value type.
     *
     * @param context the FacesContext instance
     * @param datePicker the DatePicker component to initialize
     */
    protected void initializeDefaults(FacesContext context, DatePicker datePicker) {
        String pattern = datePicker.getPattern() == null ? datePicker.calculatePattern() : datePicker.getPattern();

        if (datePicker.isShowTimeWithoutDefault() == null) {
            Class<?> type = datePicker.getValueType();

            if (type != null) {
                datePicker.setShowTime(LocalDateTime.class.isAssignableFrom(type));
            }
            else {
                datePicker.setShowTime(CalendarUtils.hasTime(pattern));
            }
        }

        if (datePicker.getTimeOnlyWithoutDefault() == null) {
            Class<?> type = datePicker.getValueType();

            if (type != null) {
                datePicker.setTimeOnly(LocalTime.class.isAssignableFrom(type));
            }
        }

        if (datePicker.isShowSecondsWithoutDefault() == null) {
            datePicker.setShowSeconds(pattern.contains("s") || pattern.contains("S"));
        }

        if (datePicker.isShowMillisecondsWithoutDefault() == null) {
            datePicker.setShowMilliseconds(pattern.contains("S"));
        }
    }

    protected void encodeDateMetadata(FacesContext context, DatePicker component) throws IOException {
        LazyDateMetadataModel model = (LazyDateMetadataModel) component.getModel();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(context);
        int year = Integer.parseInt(params.get(clientId + "_year"));
        int month = Integer.parseInt(params.get(clientId + "_month")) + 1;

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(component.getNumberOfMonths()).minusDays(1);

        model.clear();
        model.loadDateMetadata(startDate, endDate);
        encodeDateMetadataAsJSON(context, component, model);
    }

    protected void encodeDateMetadataAsJSON(FacesContext context, DatePicker datePicker, DateMetadataModel model) throws IOException {
        JSONObject jsonDateMetadata = new JSONObject();
        String pattern = datePicker.calculateWidgetPattern();
        for (Entry<LocalDate, DateMetadata> entry : model.getDateMetadata().entrySet()) {
            String date = CalendarUtils.getValueAsString(context, datePicker, entry.getKey(), pattern);
            DateMetadata metadata = entry.getValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("disabled", metadata.isDisabled());
            jsonObject.put("enabled", metadata.isEnabled());
            jsonObject.put("styleClass", metadata.getStyleClass());
            jsonDateMetadata.put(date, jsonObject);
        }

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("dateMetadata", jsonDateMetadata);

        ResponseWriter writer = context.getResponseWriter();
        writer.write(jsonResponse.toString());
    }

    @Override
    protected void encodeMarkup(FacesContext context, DatePicker component, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? UICalendar.CONTAINER_CLASS : UICalendar.CONTAINER_CLASS + " " + styleClass;
        styleClass = DatePicker.CONTAINER_EXTENSION_CLASS + " " + styleClass;
        String inputId = clientId + "_input";
        boolean inline = component.isInline();

        writer.startElement("span", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        //input
        encodeInput(context, component, inputId, value, !inline);

        writer.endElement("span");

    }

    @Override
    protected void encodeScript(FacesContext context, DatePicker component, String value) throws IOException {
        Locale locale = component.calculateLocale(context);
        String pattern = component.calculateWidgetPattern();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DatePicker", component);

        String defaultDate = null;

        if (component.isConversionFailed()) {
            Class<?> dateType = resolveDateType(context, component);
            defaultDate = CalendarUtils.getValueAsString(context, component, CalendarUtils.now(component, dateType), true);
        }
        else if (!isValueBlank(value)) {
            defaultDate = value;
        }

        // #9559 java locale must match UI local for AM/PM
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] ampm = symbols.getAmPmStrings();

        String selectionMode = component.getSelectionMode();

        wb.attr("defaultDate", defaultDate, null)
            .attr("inline", component.isInline())
            .attr("userLocale", locale.toString())
            .attr("flex", ComponentUtils.isFlex(context, component), false)
            .attr("localeAm", ampm[0], "AM")
            .attr("localePm", ampm[1], "PM")
            .attr("dateFormat", CalendarUtils.convertPattern(pattern))
            .attr("showIcon", component.isShowIcon(), false)
            .attr("buttonTabindex", component.getButtonTabindex())
            .attr("focusOnSelect", component.isFocusOnSelect(), false)
            .attr("disabled", component.isDisabled(), false)
            .attr("valid", component.isValid(), true)
            .attr("yearRange", component.getYearRange(), null)
            .attr("minDate", getMinMaxDate(context, component, component.getMindate(), false), null)
            .attr("maxDate", getMinMaxDate(context, component, component.getMaxdate(), true), null)
            .attr("selectionMode", selectionMode, null)
            .attr("showOnFocus", component.isShowOnFocus())
            .attr("shortYearCutoff", component.getShortYearCutoff(), null)
            .attr("monthNavigator", component.isMonthNavigator(), false)
            .attr("yearNavigator", component.getYearNavigator().toLowerCase(Locale.ROOT), "false")
            .attr("showButtonBar", component.isShowButtonBar(), false)
            .attr("showMinMaxRange", component.isShowMinMaxRange(), true)
            .attr("autoMonthFormat", component.isAutoMonthFormat(), true)
            .attr("panelStyleClass", component.getPanelStyleClass(), null)
            .attr("panelStyle", component.getPanelStyle(), null)
            .attr("keepInvalid", component.isKeepInvalid(), false)
            .attr("maxDateCount", component.getMaxDateCount(), Integer.MAX_VALUE)
            .attr("numberOfMonths", component.getNumberOfMonths(), 1)
            .attr("view", component.getView(), null)
            .attr("autoDetectDisplay", component.isAutoDetectDisplay(), true)
            .attr("responsiveBreakpoint", component.getResponsiveBreakpoint(), DatePicker.RESPONSIVE_BREAKPOINT_SMALL)
            .attr("touchUI", component.isTouchUI(), false)
            .attr("showWeek", component.isShowWeek(), false)
            .attr("showLongMonthNames", component.isShowLongMonthNames(), false)
            .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
            .attr("icon", component.getTriggerButtonIcon(), null)
            .attr("rangeSeparator", component.getRangeSeparator(), "-")
            .attr("timeSeparator", component.getTimeSeparator(), ":")
            .attr("fractionSeparator", component.getFractionSeparator(), ".")
            .attr("timeInput", component.isTimeInput())
            .attr("timeZone", component.getTimeZone() == null ? null : CalendarUtils.calculateZoneId(component.getTimeZone()).toString(), null)
            .attr("touchable", ComponentUtils.isTouchable(context, component), true)
            .attr("readonly", component.isReadonly(), false)
            .attr("lazyModel", component.getModel() instanceof LazyDateMetadataModel, false);

        List<Integer> disabledDays = component.getDisabledDays();
        if (disabledDays != null) {
            CalendarUtils.encodeListValue(context, component, "disabledDays", disabledDays, pattern);
        }

        List<Object> disabledDates = component.getInitialDisabledDates(context);
        if (disabledDates != null) {
            CalendarUtils.encodeListValue(context, component, "disabledDates", disabledDates, pattern);
        }

        List enabledDates = component.getInitialEnabledDates(context);
        if (enabledDates != null && !enabledDates.isEmpty()) {
            CalendarUtils.encodeListValue(context, component, "enabledDates", enabledDates, pattern);
        }
        encodeScriptDateStyleClasses(wb, component);

        String dateTemplate = component.getDateTemplate();
        if (dateTemplate != null) {
            wb.nativeAttr("dateTemplate", dateTemplate);
        }

        String beforeShow = component.getBeforeShow();
        if (beforeShow != null) {
            wb.nativeAttr("preShow", beforeShow);
        }

        String onMonthChange = component.getOnMonthChange();
        if (onMonthChange != null) {
            wb.nativeAttr("onMonthChange", onMonthChange);
        }

        String onYearChange = component.getOnYearChange();
        if (onYearChange != null) {
            wb.nativeAttr("onYearChange", onYearChange);
        }

        String weekCalculator = component.getWeekCalculator();
        if (weekCalculator != null) {
            wb.nativeAttr("weekCalculator", weekCalculator);
        }

        if (component.isShowOtherMonths()) {
            wb.attr("showOtherMonths", true).attr("selectOtherMonths", component.isSelectOtherMonths());
        }

        if (component.hasTime()) {
            wb.attr("showTime", component.isShowTime(), false)
                .attr("hourFormat", component.getHourFormat(), null)
                .attr("timeOnly", component.isTimeOnly(), false)
                .attr("showSeconds", component.isShowSeconds(), false)
                .attr("showMilliseconds", component.isShowMilliseconds(), false)
                .attr("stepHour", component.getStepHour(), 1)
                .attr("stepMinute", component.getStepMinute(), 1)
                .attr("stepSecond", component.getStepSecond(), 1)
                .attr("stepMillisecond", component.getStepSecond(), 1)
                .attr("hideOnDateTimeSelect", component.isHideOnDateTimeSelect(), false)
                .attr("defaultHour", component.getDefaultHour())
                .attr("defaultMinute", component.getDefaultMinute())
                .attr("defaultSecond", component.getDefaultSecond())
                .attr("defaultMillisecond", component.getDefaultMillisecond());
        }


        if ("range".equalsIgnoreCase(selectionMode)) {
            wb.attr("hideOnRangeSelection", component.isHideOnRangeSelection(), false);
        }

        String mask = component.getMask();
        if (mask != null && !"false".equals(mask)) {
            wb.attr("mask", resolveMask(component, mask))
                .attr("maskSlotChar", component.getMaskSlotChar(), "_")
                .attr("maskAutoClear", component.isMaskAutoClear(), true);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected String resolveMask(DatePicker component, String mask) {
        if (!"true".equals(mask)) {
            return mask;
        }
        String patternMask = component.convertPattern(component.calculatePattern());
        switch (component.getSelectionMode()) {
            case "multiple":
                throw new FacesException("Mask is not supported on selectionMode multiple");
            case "range":
                return patternMask + " " + component.getRangeSeparator() + " " + patternMask;
            default:
                return patternMask;
        }
    }

    protected void encodeScriptDateStyleClasses(WidgetBuilder wb, DatePicker component) throws IOException {
        if (component.getModel() == null) {
            return;
        }
        JSONObject styleClasses = new JSONObject();
        for (Entry<LocalDate, DateMetadata> entry : component.getModel().getDateMetadata().entrySet()) {
            DateMetadata metadata = entry.getValue();
            if (metadata.getStyleClass() != null) {
                styleClasses.put(entry.getKey().toString(), metadata.getStyleClass());
            }
        }
        wb.nativeAttr("dateStyleClasses", styleClasses.toString());
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
        DatePicker datePicker = (DatePicker) component;
        String submittedValue = (String) value;

        if (isValueBlank(submittedValue)) {
            return null;
        }

        String selectionMode = datePicker.getSelectionMode();
        switch (selectionMode) {
            case "multiple":
                String[] parts = submittedValue.split(",");
                List<Object> multi = new ArrayList<>();
                for (String part : parts) {
                    multi.add(super.getConvertedValue(context, component, part));
                }

                return multi;
            case "range":
                List<String> rangeStr = CalendarUtils.splitRange(submittedValue, datePicker.calculatePattern(),
                        datePicker.getRangeSeparator(), "week".equals(datePicker.getView()));
                List<Object> range = new ArrayList<>();
                if (rangeStr.size() == 2) {
                    for (int i = 0; i < rangeStr.size(); i++) {
                        range.add(super.getConvertedValue(context, component, rangeStr.get(i)));
                    }
                    // #8351 adjust end date to end of day
                    Object end = range.get(1);
                    boolean isDate = end instanceof Date;
                    if (end instanceof LocalDateTime || isDate) {
                        LocalDateTime endDate = isDate
                                ? CalendarUtils.convertDate2LocalDateTime((Date) end)
                                : (LocalDateTime) end;
                        endDate = endDate.plusDays(1).minus(1, ChronoUnit.NANOS);
                        range.set(1, isDate ? CalendarUtils.convertLocalDateTime2Date(endDate) : endDate);
                    }
                }
                return range;
            default:
                return super.getConvertedValue(context, component, value);
        }
    }

    private String getMinMaxDate(FacesContext context, DatePicker component, Object value, boolean max) {
        if (value instanceof LocalDate && component.isShowTime()) {
            LocalDate date = (LocalDate) value;
            value = date.atTime(max ? LocalTime.MAX : LocalTime.MIN);
        }
        return CalendarUtils.getValueAsString(context, component, value);
    }
}

