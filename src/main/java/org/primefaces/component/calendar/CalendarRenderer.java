/*
 * Copyright 2009-2011 Prime Technology.
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.event.DateSelectEvent;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;

public class CalendarRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Calendar calendar = (Calendar) component;

        if(calendar.isDisabled() || calendar.isReadonly()) {
            return;
        }

        decodeBehaviors(context, calendar);
        
        String param = calendar.getClientId(context) + "_input";
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(param);

        if(submittedValue != null) {
            calendar.setSubmittedValue(submittedValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Calendar calendar = (Calendar) component;
        String value = CalendarUtils.getValueAsString(context, calendar);

        encodeMarkup(context, calendar, value);
        encodeScript(context, calendar, value);
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
        String type = popup ? "text" : "hidden";

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", type, null);

        if(!isValueBlank(value)) {
            writer.writeAttribute("value", value, null);
        }

        if(popup) {
            String inputStyleClass = calendar.getInputStyleClass();
            inputStyleClass = inputStyleClass == null ? Calendar.INPUT_STYLE_CLASS : Calendar.INPUT_STYLE_CLASS + " " + inputStyleClass;

            writer.writeAttribute("class", inputStyleClass, null);

            if(calendar.getInputStyle() != null) writer.writeAttribute("style", calendar.getInputStyle(), null);
            if(calendar.isReadOnlyInputText()) writer.writeAttribute("readonly", "readonly", null);
            if(calendar.isDisabled()) writer.writeAttribute("disabled", "disabled", null);

            renderPassThruAttributes(context, calendar, Calendar.INPUT_TEXT_ATTRS);
        }

        writer.endElement("input");

        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, Calendar calendar, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = calendar.getClientId(context);
       
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("jQuery(function(){");

        writer.write(calendar.resolveWidgetVar() + " = new PrimeFaces.widget.Calendar('" + clientId + "', {");

        writer.write("popup:" + calendar.isPopup());
        writer.write(",locale:'" + calendar.calculateLocale(context).toString() + "'");

        if(!isValueBlank(value)) writer.write(",defaultDate:'" + value + "'");
        if(calendar.getPattern() != null) writer.write(",pattern:'" + CalendarUtils.convertPattern(calendar.getPattern()) + "'");
        if(calendar.getPages() != 1) writer.write(",numberOfMonths:" + calendar.getPages());
        if(calendar.getMindate() != null) writer.write(",minDate:'" + CalendarUtils.getDateAsString(calendar, calendar.getMindate()) + "'");
        if(calendar.getMaxdate() != null) writer.write(",maxDate:'" + CalendarUtils.getDateAsString(calendar, calendar.getMaxdate()) + "'");
        if(calendar.isShowButtonPanel()) writer.write(",showButtonPanel:true");
        if(calendar.isShowWeek()) writer.write(",showWeek:true");
        if(calendar.isDisabled()) writer.write(",disabled:true");
        if(calendar.getYearRange() != null) writer.write(",yearRange:'" + calendar.getYearRange() + "'");

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
            String iconSrc = calendar.getPopupIcon() != null ? getResourceURL(context, calendar.getPopupIcon()) : getResourceRequestPath(context, Calendar.POPUP_ICON);

            writer.write(",showOn:'" + showOn + "'");
            writer.write(",buttonImage:'" + iconSrc + "'");
            writer.write(",buttonImageOnly:" + calendar.isPopupIconOnly());
        }

        if(calendar.isShowOtherMonths()) {
            writer.write(",showOtherMonths:true");
            writer.write(",selectOtherMonths:" + calendar.isSelectOtherMonths());
        }

        if(calendar.getSelectListener() != null) {
            writer.write(",hasSelectListener:true");

            String onSelectProcess = calendar.getOnSelectProcess();
            onSelectProcess = onSelectProcess == null ? clientId : ComponentUtils.findClientIds(context, calendar, onSelectProcess);
            
            writer.write(",onSelectProcess:'" + onSelectProcess + "'");

            if(calendar.getOnSelectUpdate() != null) {
                writer.write(",onSelectUpdate:'" + ComponentUtils.findClientIds(context, calendar, calendar.getOnSelectUpdate()) + "'");
            }
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

        writer.endElement("script");
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
        try {
            Date convertedValue;
            Locale locale = calendar.calculateLocale(context);
            SimpleDateFormat format = new SimpleDateFormat(calendar.getPattern(), locale);
            format.setTimeZone(calendar.calculateTimeZone());

            convertedValue = format.parse(submittedValue);

            if(calendar.isInstantSelection()) {
                calendar.queueEvent(new DateSelectEvent(calendar, convertedValue));
            }
            
            return convertedValue;

        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
}
