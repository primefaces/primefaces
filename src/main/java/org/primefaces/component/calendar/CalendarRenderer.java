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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.WidgetBuilder;

public class CalendarRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Calendar calendar = (Calendar) component;

        if (!shouldDecode(calendar)) {
            return;
        }

        String param = calendar.getClientId(context) + "_input";
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(param);

        if (submittedValue != null) {
            calendar.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(context, calendar);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Calendar calendar = (Calendar) component;
        String markupValue = CalendarUtils.getValueAsString(context, calendar);
        String widgetValue = calendar.isTimeOnly() ? CalendarUtils.getTimeOnlyValueAsString(context, calendar) : markupValue;

        encodeMarkup(context, calendar, markupValue);
        encodeScript(context, calendar, widgetValue);
    }

    protected void encodeMarkup(FacesContext context, Calendar calendar, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = calendar.getClientId(context);
        String styleClass = calendar.getStyleClass();
        styleClass = (styleClass == null) ? Calendar.CONTAINER_CLASS : Calendar.CONTAINER_CLASS + " " + styleClass;
        String inputId = clientId + "_input";
        boolean popup = calendar.isPopup();

        writer.startElement("span", calendar);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (calendar.getStyle() != null) {
            writer.writeAttribute("style", calendar.getStyle(), null);
        }

        //inline container
        if (!popup) {
            writer.startElement("div", null);
            writer.writeAttribute("id", clientId + "_inline", null);
            writer.endElement("div");
        }

        //input
        encodeInput(context, calendar, inputId, value, popup);

        writer.endElement("span");

    }

    protected void encodeInput(FacesContext context, Calendar calendar, String id, String value, boolean popup) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String type = popup ? calendar.getType() : "hidden";
        String inputStyle = calendar.getInputStyle();
        String inputStyleClass = calendar.getInputStyleClass();

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", type, null);

        if (!isValueBlank(value)) {
            writer.writeAttribute("value", value, null);
        }

        boolean readonly = false;
        boolean disabled = false;

        if (popup) {
            inputStyleClass = (inputStyleClass == null) ? Calendar.INPUT_STYLE_CLASS
                                                        : Calendar.INPUT_STYLE_CLASS + " " + inputStyleClass;
            readonly = calendar.isReadonly() || calendar.isReadonlyInput();

            if (calendar.isDisabled()) {
                inputStyleClass = inputStyleClass + " ui-state-disabled";
                disabled = true;
            }
            if (!calendar.isValid()) {
                inputStyleClass = inputStyleClass + " ui-state-error";
            }

            writer.writeAttribute("class", inputStyleClass, null);

            if (inputStyle != null) {
                writer.writeAttribute("style", inputStyle, null);
            }

            renderPassThruAttributes(context, calendar, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
            renderDomEvents(context, calendar, HTML.INPUT_TEXT_EVENTS);
        }

        renderAccessibilityAttributes(context, calendar, disabled, readonly);
        renderValidationMetadata(context, calendar);

        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, Calendar calendar, String value) throws IOException {
        String clientId = calendar.getClientId(context);
        Locale locale = calendar.calculateLocale(context);
        String pattern = calendar.isTimeOnly() ? calendar.calculateTimeOnlyPattern() : calendar.calculatePattern();
        String mask = calendar.getMask();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Calendar", calendar.resolveWidgetVar(), clientId);

        wb.attr("popup", calendar.isPopup())
                .attr("locale", locale.toString())
                .attr("dateFormat", CalendarUtils.convertPattern(pattern));

        //default date
        Object pagedate = calendar.getPagedate();
        String defaultDate = null;

        if (calendar.isConversionFailed()) {
            defaultDate = CalendarUtils.getValueAsString(context, calendar, new Date());
        }
        else if (!isValueBlank(value)) {
            defaultDate = value;
        }
        else if (pagedate != null) {
            defaultDate = CalendarUtils.getValueAsString(context, calendar, pagedate);
        }

        wb.attr("defaultDate", defaultDate, null)
                .attr("numberOfMonths", calendar.getPages(), 1)
                .attr("minDate", CalendarUtils.getValueAsString(context, calendar, calendar.getMindate()), null)
                .attr("maxDate", CalendarUtils.getValueAsString(context, calendar, calendar.getMaxdate()), null)
                .attr("showButtonPanel", calendar.isShowButtonPanel(), false)
                .attr("showTodayButton", calendar.isShowTodayButton(), true)
                .attr("showWeek", calendar.isShowWeek(), false)
                .attr("disabledWeekends", calendar.isDisabledWeekends(), false)
                .attr("disabled", calendar.isDisabled(), false)
                .attr("yearRange", calendar.getYearRange(), null)
                .attr("focusOnSelect", calendar.isFocusOnSelect(), false);

        if (calendar.isNavigator()) {
            wb.attr("changeMonth", true).attr("changeYear", true);
        }

        if (calendar.getEffect() != null) {
            wb.attr("showAnim", calendar.getEffect()).attr("duration", calendar.getEffectDuration());
        }

        String beforeShowDay = calendar.getBeforeShowDay();
        if (beforeShowDay != null) {
            wb.nativeAttr("preShowDay", beforeShowDay);
        }

        String beforeShow = calendar.getBeforeShow();
        if (beforeShow != null) {
            wb.nativeAttr("preShow", beforeShow);
        }

        String showOn = calendar.getShowOn();
        if (!showOn.equalsIgnoreCase("focus")) {
            wb.attr("showOn", showOn).attr("buttonTabindex", calendar.getButtonTabindex());
        }

        if (calendar.isShowOtherMonths()) {
            wb.attr("showOtherMonths", true).attr("selectOtherMonths", calendar.isSelectOtherMonths());
        }

        if (calendar.hasTime()) {
            String timeControlType = calendar.getTimeControlType();

            wb.attr("timeOnly", calendar.isTimeOnly())
                    .attr("stepHour", calendar.getStepHour())
                    .attr("stepMinute", calendar.getStepMinute())
                    .attr("stepSecond", calendar.getStepSecond())
                    .attr("hourMin", calendar.getMinHour())
                    .attr("hourMax", calendar.getMaxHour())
                    .attr("minuteMin", calendar.getMinMinute())
                    .attr("minuteMax", calendar.getMaxMinute())
                    .attr("secondMin", calendar.getMinSecond())
                    .attr("secondMax", calendar.getMaxSecond())
                    .attr("timeInput", calendar.isTimeInput())
                    .attr("controlType", timeControlType, null)
                    .attr("showHour", calendar.getShowHour(), null)
                    .attr("showMinute", calendar.getShowMinute(), null)
                    .attr("showSecond", calendar.getShowSecond(), null)
                    .attr("showMillisec", calendar.getShowMillisec(), null)
                    .attr("oneLine", calendar.isOneLine())
                    .attr("hour", calendar.getDefaultHour())
                    .attr("minute", calendar.getDefaultMinute())
                    .attr("second", calendar.getDefaultSecond())
                    .attr("millisec", calendar.getDefaultMillisec());

            String timeControlObject = calendar.getTimeControlObject();
            if (timeControlObject != null && timeControlType.equalsIgnoreCase("custom")) {
                wb.nativeAttr("timeControlObject", timeControlObject);
            }
        }

        if (mask != null && !mask.equals("false")) {
            String patternTemplate = calendar.getPattern() == null ? pattern : calendar.getPattern();
            String maskTemplate = (mask.equals("true")) ? convertPattern(patternTemplate) : mask;
            wb.attr("mask", maskTemplate).attr("maskSlotChar", calendar.getMaskSlotChar(), null).attr("maskAutoClear", calendar.isMaskAutoClear(), true);
        }

        encodeClientBehaviors(context, calendar);

        wb.finish();
    }

    public String convertPattern(String patternTemplate) {
        String pattern = patternTemplate.replaceAll("MMM", "###");
        int patternLen = pattern.length();
        int countM = patternLen - pattern.replaceAll("M", "").length();
        int countD = patternLen - pattern.replaceAll("d", "").length();
        if (countM == 1) {
            pattern = pattern.replaceAll("M", "mm");
        }

        if (countD == 1) {
            pattern = pattern.replaceAll("d", "dd");
        }

        pattern = pattern.replaceAll("[a-zA-Z]", "9");
        pattern = pattern.replaceAll("###", "aaa");
        return pattern;
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
        Calendar calendar = (Calendar) component;
        String submittedValue = (String) value;
        SimpleDateFormat format = null;

        if (isValueBlank(submittedValue)) {
            return null;
        }

        //Delegate to user supplied converter if defined
        try {
            Converter converter = calendar.getConverter();
            if (converter != null) {
                return converter.getAsObject(context, calendar, submittedValue);
            }
        }
        catch (ConverterException e) {
            calendar.setConversionFailed(true);

            throw e;
        }

        //Delegate to global defined converter (e.g. joda or java8)
        try {
            ValueExpression ve = calendar.getValueExpression("value");
            if (ve != null) {
                Class type = ve.getType(context.getELContext());
                if (type != null && type != Object.class && type != Date.class) {
                    Converter converter = context.getApplication().createConverter(type);
                    if (converter != null) {
                        return converter.getAsObject(context, calendar, submittedValue);
                    }
                }
            }
        }
        catch (ConverterException e) {
            calendar.setConversionFailed(true);

            throw e;
        }

        //Use built-in converter
        format = new SimpleDateFormat(calendar.calculatePattern(), calendar.calculateLocale(context));
        format.setLenient(false);
        format.setTimeZone(calendar.calculateTimeZone());
        try {
            return format.parse(submittedValue);
        }
        catch (ParseException e) {
            calendar.setConversionFailed(true);

            FacesMessage message = null;
            Object[] params = new Object[3];
            params[0] = submittedValue;
            params[1] = format.format(new Date());
            params[2] = MessageFactory.getLabel(context, calendar);

            if (calendar.isTimeOnly()) {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.TIME", FacesMessage.SEVERITY_ERROR, params);
            }
            else if (calendar.hasTime()) {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATETIME", FacesMessage.SEVERITY_ERROR, params);
            }
            else {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATE", FacesMessage.SEVERITY_ERROR, params);
            }

            throw new ConverterException(message);
        }
    }
}
