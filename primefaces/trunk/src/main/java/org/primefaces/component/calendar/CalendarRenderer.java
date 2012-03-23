/*
 * Copyright 2009-2012 Prime Teknoloji.
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

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.MessageFactory;

public class CalendarRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Calendar calendar = (Calendar) component;

        if(calendar.isDisabled() || calendar.isReadonly()) {
            return;
        }

        String param = calendar.getClientId(context) + "_input";
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(param);

        if(submittedValue != null) {
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
        String inputId = clientId + "_input";
        boolean popup = calendar.isPopup();

        writer.startElement("span", calendar);
        writer.writeAttribute("id", clientId, null);
        if(calendar.getStyle() != null) writer.writeAttribute("style", calendar.getStyle(), null);
        if(calendar.getStyleClass() != null) writer.writeAttribute("class", calendar.getStyleClass(), null);

        //inline container
        if(!popup) {
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
        boolean disabled = calendar.isDisabled();

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", type, null);

        if(!isValueBlank(value)) {
            writer.writeAttribute("value", value, null);
        }

        if(popup) {
            String inputStyleClass = Calendar.INPUT_STYLE_CLASS;
            if(disabled) inputStyleClass = inputStyleClass + " ui-state-disabled";
            if(!calendar.isValid()) inputStyleClass = inputStyleClass + " ui-state-error";
            
            writer.writeAttribute("class", inputStyleClass, null);
            
            renderPassThruAttributes(context, calendar, HTML.INPUT_TEXT_ATTRS);
  
            if(calendar.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
            if(calendar.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        }

        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, Calendar calendar, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = calendar.getClientId(context);
        Locale locale = calendar.calculateLocale(context);
        String pattern = calendar.isTimeOnly() ? calendar.calculateTimeOnlyPattern() : calendar.calculatePattern();

        startScript(writer, clientId);

        writer.write("$(function(){");

        writer.write("PrimeFaces.cw('Calendar','" + calendar.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        writer.write(",popup:" + calendar.isPopup());
        writer.write(",locale:'" + locale.toString() + "'");
        writer.write(",dateFormat:'" + CalendarUtils.convertPattern(pattern) + "'");

        //default date
        Object pagedate = calendar.getPagedate();
        String defaultDate = null;
        
        if(calendar.isConversionFailed()) {
            defaultDate = CalendarUtils.getValueAsString(context, calendar, new Date());
        }
        else if(!isValueBlank(value)) {
            defaultDate = value;
        } 
        else if(pagedate != null) {
            defaultDate = CalendarUtils.getValueAsString(context, calendar, pagedate);
        }
        
        if(defaultDate != null) {
            writer.write(",defaultDate:'" + defaultDate + "'");
        }
        
        if(calendar.getPages() != 1) writer.write(",numberOfMonths:" + calendar.getPages());
        if(calendar.getMindate() != null) writer.write(",minDate:'" + CalendarUtils.getValueAsString(context, calendar, calendar.getMindate()) + "'");
        if(calendar.getMaxdate() != null) writer.write(",maxDate:'" + CalendarUtils.getValueAsString(context, calendar, calendar.getMaxdate()) + "'");
        if(calendar.isShowButtonPanel()) writer.write(",showButtonPanel:true");
        if(calendar.isShowWeek()) writer.write(",showWeek:true");
        if(calendar.isDisabledWeekends()) writer.write(",disabledWeekends:true");
        if(calendar.isDisabled()) writer.write(",disabled:true");
        if(calendar.getYearRange() != null) writer.write(",yearRange:'" + calendar.getYearRange() + "'");
        if(calendar.getBeforeShowDay() != null) writer.write(",preShowDay:" + calendar.getBeforeShowDay());
        if(calendar.isReadonlyInput()) writer.write(",readonlyInput:true");

        if(calendar.isNavigator()) {
            writer.write(",changeMonth:true");
            writer.write(",changeYear:true");
        }

        if(calendar.getEffect() != null) {
            writer.write(",showAnim:'" + calendar.getEffect() + "'");
            writer.write(",duration:'" + calendar.getEffectDuration() + "'");
        }

        String showOn = calendar.getShowOn();
        if(!showOn.equalsIgnoreCase("focus")) {
            writer.write(",showOn:'" + showOn + "'");
        }

        if(calendar.isShowOtherMonths()) {
            writer.write(",showOtherMonths:true");
            writer.write(",selectOtherMonths:" + calendar.isSelectOtherMonths());
        }

        //time
        if(calendar.hasTime()) {
            writer.write(",timeOnly:" + calendar.isTimeOnly());

            //step
            writer.write(",stepHour:" + calendar.getStepHour());
            writer.write(",stepMinute:" + calendar.getStepMinute());
            writer.write(",stepSecond:" + calendar.getStepSecond());
            
            //minmax
            writer.write(",hourMin:" + calendar.getMinHour());
            writer.write(",hourMax:" + calendar.getMaxHour());
            writer.write(",minuteMin:" + calendar.getMinMinute());
            writer.write(",minuteMax:" + calendar.getMaxMinute());
            writer.write(",secondMin:" + calendar.getMinSecond());
            writer.write(",secondMax:" + calendar.getMaxSecond());
        }

        encodeClientBehaviors(context, calendar);

        writer.write("});});");

        endScript(writer);
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
        Calendar calendar = (Calendar) component;
        String submittedValue = (String) value;
        Converter converter = calendar.getConverter();

        if(isValueBlank(submittedValue)) {
            return null;
        }

        //Delegate to user supplied converter if defined
        if(converter != null) {
            return converter.getAsObject(context, calendar, submittedValue);
        }

        //Use built-in converter
        SimpleDateFormat format = null;
        try {
            format = new SimpleDateFormat(calendar.calculatePattern(), calendar.calculateLocale(context));
            format.setTimeZone(calendar.calculateTimeZone());
            
            Object date = format.parse(submittedValue);
            
            return date;
        } 
        catch (ParseException e) {
            calendar.setConversionFailed(true);
            
            FacesMessage message = null;
            Object[] params = new Object[3];
            params[0] = submittedValue;
            params[1] = format.format(new Date());
            params[2] = MessageFactory.getLabel(context, calendar);
            
            if(calendar.isTimeOnly()) {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.TIME", FacesMessage.SEVERITY_ERROR, params);
            } 
            else if(calendar.hasTime()) {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATETIME", FacesMessage.SEVERITY_ERROR, params);
            } 
            else {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATE", FacesMessage.SEVERITY_ERROR, params);
            }
            
            throw new ConverterException(message);
        }
    }
}
