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
import org.primefaces.expression.SearchExpressionHint;
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
        wb.init("DatePicker", datepicker.resolveWidgetVar(context), clientId);

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
            .attr("showOnFocus", datepicker.isShowOnFocus())
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
            .attr("appendTo",
                    SearchExpressionFacade.resolveClientId(context, datepicker, datepicker.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null)
            .attr("icon", datepicker.getTriggerButtonIcon(), null)
            .attr("rangeSeparator", datepicker.getRangeSeparator(), null);

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

        String onMonthChange = datepicker.getOnMonthChange();
        if (onMonthChange != null) {
            wb.nativeAttr("onMonthChange", onMonthChange);
        }

        String onYearChange = datepicker.getOnYearChange();
        if (onYearChange != null) {
            wb.nativeAttr("onYearChange", onYearChange);
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
                String[] parts = submittedValue.split(datepicker.getRangeSeparator());
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

