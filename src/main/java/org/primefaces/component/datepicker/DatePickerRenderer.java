/**
 * Copyright 2009-2019 PrimeTek.
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
package org.primefaces.component.datepicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.api.UICalendar;
import org.primefaces.component.calendar.BaseCalendarRenderer;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.WidgetBuilder;

public class DatePickerRenderer extends BaseCalendarRenderer {

    @Override
    protected void encodeMarkup(FacesContext context, UICalendar uicalendar, String value) throws IOException {
        DatePicker datepicker = (DatePicker) uicalendar;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = datepicker.getClientId(context);
        String styleClass = datepicker.getStyleClass();
        styleClass = (styleClass == null) ? UICalendar.CONTAINER_CLASS : UICalendar.CONTAINER_CLASS + " " + styleClass;
        styleClass = DatePicker.CONTAINER_EXTENSION_CLASS + " " + styleClass;
        String inputId = clientId + "_input";
        boolean inline = datepicker.isInline();

        writer.startElement("span", datepicker);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (datepicker.getStyle() != null) {
            writer.writeAttribute("style", datepicker.getStyle(), null);
        }

        //inline container
        if (inline) {
            writer.startElement("div", null);
            writer.writeAttribute("id", clientId + "_inline", null);
            writer.endElement("div");
        }

        //input
        encodeInput(context, datepicker, inputId, value, !inline);

        writer.endElement("span");

    }

    @Override
    protected void encodeScript(FacesContext context, UICalendar uicalendar, String value) throws IOException {
        DatePicker datepicker = (DatePicker) uicalendar;
        String clientId = datepicker.getClientId(context);
        Locale locale = datepicker.calculateLocale(context);
        String pattern = datepicker.isTimeOnly() ? datepicker.calculateTimeOnlyPattern() : datepicker.calculatePattern();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DatePicker", datepicker.resolveWidgetVar(), clientId);

        String defaultDate = null;

        if (datepicker.isConversionFailed()) {
            defaultDate = CalendarUtils.getValueAsString(context, datepicker, new Date());
        }
        else if (!isValueBlank(value)) {
            defaultDate = value;
        }

        wb.attr("defaultDate", defaultDate, null)
            .attr("inline", datepicker.isInline())
            .attr("userLocale", locale.toString())
            .attr("dateFormat", CalendarUtils.convertPattern(pattern))
            .attr("showIcon", datepicker.isShowIcon(), false)
            .attr("buttonTabindex", datepicker.getButtonTabindex())
            .attr("focusOnSelect", datepicker.isFocusOnSelect(), false)
            .attr("disabled", datepicker.isDisabled(), false)
            .attr("yearRange", datepicker.getYearRange(), null)
            .attr("minDate", CalendarUtils.getValueAsString(context, datepicker, datepicker.getMindate()), null)
            .attr("maxDate", CalendarUtils.getValueAsString(context, datepicker, datepicker.getMaxdate()), null)
            .attr("selectionMode", datepicker.getSelectionMode(), null)
            .attr("showOnFocus", datepicker.isShowOnFocus(), false)
            .attr("shortYearCutoff", datepicker.getShortYearCutoff(), null)
            .attr("monthNavigator", datepicker.isMonthNavigator(), false)
            .attr("yearNavigator", datepicker.isYearNavigator(), false)
            .attr("showButtonBar", datepicker.isShowButtonBar(), false)
            .attr("panelStyleClass", datepicker.getPanelStyleClass(), null)
            .attr("panelStyle", datepicker.getPanelStyle(), null)
            .attr("keepInvalid", datepicker.isKeepInvalid(), false)
            .attr("maxDateCount", datepicker.getMaxDateCount(), Integer.MAX_VALUE)
            .attr("numberOfMonths", datepicker.getNumberOfMonths(), 1)
            .attr("view", datepicker.getView(), null)
            .attr("touchUI", datepicker.isTouchUI(), false)
            .attr("appendTo", SearchExpressionFacade.resolveClientId(context, datepicker, datepicker.getAppendTo()), null)
            .attr("icon", datepicker.getTriggerButtonIcon(), null);

        List<Object> disabledDays = datepicker.getDisabledDays();
        if (disabledDays != null) {
            CalendarUtils.encodeListValue(context, datepicker, "disabledDays", disabledDays);
        }

        List<Object> disabledDates = datepicker.getDisabledDates();
        if (disabledDates != null) {
            CalendarUtils.encodeListValue(context, datepicker, "disabledDates", disabledDates);
        }

        String dateTemplate = datepicker.getDateTemplate();
        if (dateTemplate != null) {
            wb.nativeAttr("dateTemplate", dateTemplate);
        }

        String beforeShow = datepicker.getBeforeShow();
        if (beforeShow != null) {
            wb.nativeAttr("preShow", beforeShow);
        }

        if (datepicker.isShowOtherMonths()) {
            wb.attr("showOtherMonths", true).attr("selectOtherMonths", datepicker.isSelectOtherMonths());
        }

        if (datepicker.hasTime()) {
            wb.attr("showTime", datepicker.isShowTime(), false)
                .attr("hourFormat", datepicker.getHourFormat(), null)
                .attr("timeOnly", datepicker.isTimeOnly(), false)
                .attr("showSeconds", datepicker.isShowSeconds(), false)
                .attr("stepHour", datepicker.getStepHour(), 1)
                .attr("stepMinute", datepicker.getStepMinute(), 1)
                .attr("stepSecond", datepicker.getStepSecond(), 1)
                .attr("hideOnDateTimeSelect", datepicker.isHideOnDateTimeSelect(), false);
        }

        encodeClientBehaviors(context, datepicker);

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
        DatePicker datepicker = (DatePicker) component;
        String submittedValue = (String) value;

        if (isValueBlank(submittedValue)) {
            return null;
        }

        String selectionMode = datepicker.getSelectionMode();
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
                String[] parts = submittedValue.split("-");
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
}

