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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.json.JSONObject;
import org.primefaces.component.api.UICalendar;
import org.primefaces.component.calendar.BaseCalendarRenderer;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.model.datepicker.DateMetadata;
import org.primefaces.model.datepicker.DateMetadataModel;
import org.primefaces.model.datepicker.LazyDateMetadataModel;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class DatePickerRenderer extends BaseCalendarRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DatePicker datePicker = (DatePicker) component;

        if (datePicker.getModel() instanceof LazyDateMetadataModel && ComponentUtils.isRequestSource(datePicker, context, "viewChange")) {
            encodeDateMetadata(context, datePicker);
            return;
        }

        String pattern = datePicker.getPattern() == null ? datePicker.calculatePattern() : datePicker.getPattern();

        if (datePicker.isShowTimeWithoutDefault() == null) {
            Class<?> type = datePicker.getTypeFromValueByValueExpression(context);

            if (type != null) {
                datePicker.setShowTime(LocalDateTime.class.isAssignableFrom(type));
            }
            else {
                datePicker.setShowTime(CalendarUtils.hasTime(pattern));
            }
        }

        if (datePicker.isTimeOnlyWithoutDefault() == null) {
            Class<?> type = datePicker.getTypeFromValueByValueExpression(context);

            if (type != null) {
                datePicker.setTimeOnly(LocalTime.class.isAssignableFrom(type));
            }
        }

        if (datePicker.isShowSecondsWithoutDefault() == null) {
            datePicker.setShowSeconds(pattern.contains("s"));
        }

        super.encodeEnd(context, component);
    }

    protected void encodeDateMetadata(FacesContext context, DatePicker datePicker) throws IOException {
        LazyDateMetadataModel model = (LazyDateMetadataModel) datePicker.getModel();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = datePicker.getClientId(context);
        int year = Integer.parseInt(params.get(clientId + "_year"));
        int month = Integer.parseInt(params.get(clientId + "_month")) + 1;

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(datePicker.getNumberOfMonths()).minusDays(1);

        model.clear();
        model.loadDateMetadata(startDate, endDate);
        encodeDateMetadataAsJSON(context, datePicker, model);
    }

    protected void encodeDateMetadataAsJSON(FacesContext context, DatePicker datePicker, DateMetadataModel model) throws IOException {
        JSONObject jsonDateMetadata = new JSONObject();
        String pattern = datePicker.calculateWidgetPattern();
        for (Entry<LocalDate, DateMetadata> entry : model.getDateMetadata().entrySet()) {
            String date = CalendarUtils.getValueAsString(context, datePicker, entry.getKey(), pattern);
            DateMetadata metadata = entry.getValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("disabled", metadata.isDisabled());
            jsonObject.put("styleClass", metadata.getStyleClass());
            jsonDateMetadata.put(date, jsonObject);
        }

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("dateMetadata", jsonDateMetadata);

        ResponseWriter writer = context.getResponseWriter();
        writer.write(jsonResponse.toString());
    }

    @Override
    protected void encodeMarkup(FacesContext context, UICalendar uicalendar, String value) throws IOException {
        DatePicker datePicker = (DatePicker) uicalendar;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = datePicker.getClientId(context);
        String styleClass = datePicker.getStyleClass();
        styleClass = (styleClass == null) ? UICalendar.CONTAINER_CLASS : UICalendar.CONTAINER_CLASS + " " + styleClass;
        styleClass = DatePicker.CONTAINER_EXTENSION_CLASS + " " + styleClass;
        String inputId = clientId + "_input";
        boolean inline = datePicker.isInline();

        writer.startElement("span", datePicker);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (datePicker.getStyle() != null) {
            writer.writeAttribute("style", datePicker.getStyle(), null);
        }

        //input
        encodeInput(context, datePicker, inputId, value, !inline);

        writer.endElement("span");

    }

    @Override
    protected void encodeScript(FacesContext context, UICalendar uicalendar, String value) throws IOException {
        DatePicker datePicker = (DatePicker) uicalendar;
        Locale locale = datePicker.calculateLocale(context);
        String pattern = datePicker.calculateWidgetPattern();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DatePicker", datePicker);

        String defaultDate = null;

        if (datePicker.isConversionFailed()) {
            Class<?> dateType = resolveDateType(context, datePicker);
            defaultDate = CalendarUtils.getValueAsString(context, datePicker, CalendarUtils.now(uicalendar, dateType));
        }
        else if (!isValueBlank(value)) {
            defaultDate = value;
        }

        wb.attr("defaultDate", defaultDate, null)
            .attr("inline", datePicker.isInline())
            .attr("userLocale", locale.toString())
            .attr("dateFormat", CalendarUtils.convertPattern(pattern))
            .attr("showIcon", datePicker.isShowIcon(), false)
            .attr("buttonTabindex", datePicker.getButtonTabindex())
            .attr("focusOnSelect", datePicker.isFocusOnSelect(), false)
            .attr("disabled", datePicker.isDisabled(), false)
            .attr("valid", datePicker.isValid(), true)
            .attr("yearRange", datePicker.getYearRange(), null)
            .attr("minDate", getMinMaxDate(context, datePicker, datePicker.getMindate(), false), null)
            .attr("maxDate", getMinMaxDate(context, datePicker, datePicker.getMaxdate(), true), null)
            .attr("selectionMode", datePicker.getSelectionMode(), null)
            .attr("showOnFocus", datePicker.isShowOnFocus())
            .attr("shortYearCutoff", datePicker.getShortYearCutoff(), null)
            .attr("monthNavigator", datePicker.isMonthNavigator(), false)
            .attr("yearNavigator", datePicker.isYearNavigator(), false)
            .attr("showButtonBar", datePicker.isShowButtonBar(), false)
            .attr("panelStyleClass", datePicker.getPanelStyleClass(), null)
            .attr("panelStyle", datePicker.getPanelStyle(), null)
            .attr("keepInvalid", datePicker.isKeepInvalid(), false)
            .attr("maxDateCount", datePicker.getMaxDateCount(), Integer.MAX_VALUE)
            .attr("numberOfMonths", datePicker.getNumberOfMonths(), 1)
            .attr("view", datePicker.getView(), null)
            .attr("touchUI", datePicker.isTouchUI(), false)
            .attr("showWeek", datePicker.isShowWeek(), false)
            .attr("appendTo", SearchExpressionFacade.resolveClientId(context, datePicker, datePicker.getAppendTo(),
                            SearchExpressionUtils.SET_RESOLVE_CLIENT_SIDE), null)
            .attr("icon", datePicker.getTriggerButtonIcon(), null)
            .attr("rangeSeparator", datePicker.getRangeSeparator(), "-")
            .attr("timeSeparator", datePicker.getTimeSeparator(), ":")
            .attr("timeInput", datePicker.isTimeInput())
            .attr("touchable", ComponentUtils.isTouchable(context, datePicker), true)
            .attr("lazyModel", datePicker.getModel() instanceof LazyDateMetadataModel, false);

        List<Integer> disabledDays = datePicker.getDisabledDays();
        if (disabledDays != null) {
            CalendarUtils.encodeListValue(context, datePicker, "disabledDays", disabledDays, pattern);
        }

        datePicker.loadInitialLazyMetadata(context);
        List<Object> disabledDates = datePicker.getInitialDisabledDates(context);
        if (disabledDates != null) {
            CalendarUtils.encodeListValue(context, datePicker, "disabledDates", disabledDates, pattern);
        }
        encodeScriptDateStyleClasses(wb, datePicker);

        String dateTemplate = datePicker.getDateTemplate();
        if (dateTemplate != null) {
            wb.nativeAttr("dateTemplate", dateTemplate);
        }

        String beforeShow = datePicker.getBeforeShow();
        if (beforeShow != null) {
            wb.nativeAttr("preShow", beforeShow);
        }

        String onMonthChange = datePicker.getOnMonthChange();
        if (onMonthChange != null) {
            wb.nativeAttr("onMonthChange", onMonthChange);
        }

        String onYearChange = datePicker.getOnYearChange();
        if (onYearChange != null) {
            wb.nativeAttr("onYearChange", onYearChange);
        }

        String weekCalculator = datePicker.getWeekCalculator();
        if (weekCalculator != null) {
            wb.nativeAttr("weekCalculator", weekCalculator);
        }

        if (datePicker.isShowOtherMonths()) {
            wb.attr("showOtherMonths", true).attr("selectOtherMonths", datePicker.isSelectOtherMonths());
        }

        if (datePicker.hasTime()) {
            wb.attr("showTime", datePicker.isShowTime(), false)
                .attr("hourFormat", datePicker.getHourFormat(), null)
                .attr("timeOnly", datePicker.isTimeOnly(), false)
                .attr("showSeconds", datePicker.isShowSeconds(), false)
                .attr("stepHour", datePicker.getStepHour(), 1)
                .attr("stepMinute", datePicker.getStepMinute(), 1)
                .attr("stepSecond", datePicker.getStepSecond(), 1)
                .attr("hideOnDateTimeSelect", datePicker.isHideOnDateTimeSelect(), false);
        }

        String mask = datePicker.getMask();
        if (mask != null && !"false".equals(mask)) {
            String patternTemplate = datePicker.calculatePattern();
            String maskTemplate = ("true".equals(mask)) ? datePicker.convertPattern(patternTemplate) : mask;
            wb.attr("mask", maskTemplate)
                .attr("maskSlotChar", datePicker.getMaskSlotChar(), "_")
                .attr("maskAutoClear", datePicker.isMaskAutoClear(), true);
        }

        encodeClientBehaviors(context, datePicker);

        wb.finish();
    }

    protected void encodeScriptDateStyleClasses(WidgetBuilder wb, DatePicker datePicker) throws IOException {
        if (datePicker.getModel() == null) {
            return;
        }
        JSONObject styleClasses = new JSONObject();
        for (Entry<LocalDate, DateMetadata> entry : datePicker.getModel().getDateMetadata().entrySet()) {
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
            case "multiple": {
                String[] parts = submittedValue.split(",");
                List<Object> multi = new ArrayList<>();
                for (String part : parts) {
                    multi.add(super.getConvertedValue(context, component, part));
                }

                return multi;
            }
            case "range": {
                String[] parts = submittedValue.split(datePicker.getRangeSeparator());
                List<Object> range = new ArrayList<>();
                if (parts.length == 2) {
                    for (String part : parts) {
                        range.add(super.getConvertedValue(context, component, part));
                    }
                }

                return range;
            }
            default:
                return super.getConvertedValue(context, component, value);
        }
    }

    private String getMinMaxDate(FacesContext context, DatePicker datePicker, Object value, boolean max) {
        if (value instanceof LocalDate && datePicker.isShowTime()) {
            LocalDate date = (LocalDate) value;
            value = date.atTime(max ? LocalTime.MAX : LocalTime.MIN);
        }
        return CalendarUtils.getValueAsString(context, datePicker, value);
    }
}

