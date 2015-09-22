/*
 * Copyright 2009-2014 PrimeTek.
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
import javax.faces.application.FacesMessage;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.context.RequestContext;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.WidgetBuilder;

public class CalendarRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Calendar calendar = (Calendar) component;

        if (calendar.isDisabled() || calendar.isReadonly()) {
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
        String type = popup ? "text" : "hidden";
        String labelledBy = calendar.getLabelledBy();

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", type, null);

        if (calendar.isRequired()) writer.writeAttribute("aria-required", "true", null);
        
        if (!isValueBlank(value)) {
            writer.writeAttribute("value", value, null);
        }

        if (popup) {
            String inputStyleClass = Calendar.INPUT_STYLE_CLASS;
            if (calendar.isDisabled()) inputStyleClass = inputStyleClass + " ui-state-disabled";
            if (!calendar.isValid()) inputStyleClass = inputStyleClass + " ui-state-error";

            writer.writeAttribute("class", inputStyleClass, null);

            if (calendar.isReadonly()||calendar.isReadonlyInput()) writer.writeAttribute("readonly", "readonly", null);
            if (calendar.isDisabled()) writer.writeAttribute("disabled", "disabled", null);

            renderPassThruAttributes(context, calendar, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
            renderDomEvents(context, calendar, HTML.INPUT_TEXT_EVENTS);
        }
        
        if(labelledBy != null) {
            writer.writeAttribute("aria-labelledby", labelledBy, null);
        }

        if (RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, calendar);
        }

        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, Calendar calendar, String value) throws IOException {
        String clientId = calendar.getClientId(context);
        Locale locale = calendar.calculateLocale(context);
        String pattern = calendar.isTimeOnly() ? calendar.calculateTimeOnlyPattern() : calendar.calculatePattern();
        String mask = calendar.getMask();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Calendar", calendar.resolveWidgetVar(), clientId);

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
            .attr("showWeek", calendar.isShowWeek(), false)
            .attr("disabledWeekends", calendar.isDisabledWeekends(), false)
            .attr("disabled", calendar.isDisabled(), false)
            .attr("yearRange", calendar.getYearRange(), null);

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
        if(beforeShow != null) {
            wb.nativeAttr("preShow", beforeShow);
        }

        String showOn = calendar.getShowOn();
        if (!showOn.equalsIgnoreCase("focus")) {
            wb.attr("showOn", showOn);
        }

        if (calendar.isShowOtherMonths()) {
            wb.attr("showOtherMonths", true).attr("selectOtherMonths", calendar.isSelectOtherMonths());
        }

        if (calendar.hasTime()) {
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
                .attr("controlType", calendar.getTimeControlType(), null);
        }

        if (mask != null && !mask.equals("false")) {
            String patternTemplate = calendar.getPattern() == null ? pattern : calendar.getPattern();
            String maskTemplate = (mask.equals("true")) ? patternTemplate.replaceAll("[a-zA-Z]", "9") : mask;
            wb.attr("mask", maskTemplate).attr("maskSlotChar", calendar.getMaskSlotChar(), null);
        }

        encodeClientBehaviors(context, calendar);

        wb.finish();
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
        catch(ConverterException e){
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
