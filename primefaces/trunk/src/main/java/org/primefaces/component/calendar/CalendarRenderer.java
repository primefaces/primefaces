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
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.primefaces.event.DateSelectEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class CalendarRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Calendar calendar = (Calendar) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String param = calendar.getClientId(context) + "_input";

        if (params.containsKey(param)) {
            calendar.setSubmittedValue(params.get(param));
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

        writer.startElement("span", calendar);
        writer.writeAttribute("id", clientId, null);
        if(calendar.getStyle() != null) writer.writeAttribute("style", calendar.getStyle(), null);
        if(calendar.getStyleClass() != null) writer.writeAttribute("class", calendar.getStyleClass(), null);

        //popup container
        if(!calendar.isPopup()) {
            writer.startElement("div", null);
            writer.writeAttribute("id", clientId + "_inline", null);
            writer.endElement("div");
        }

        //input
        String type = calendar.isPopup() ? "text" : "hidden";

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", type, null);

        if(!isValueBlank(value)) {
            writer.writeAttribute("value", value, null);
        }

        if(calendar.isPopup()) {
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
        if(calendar.getPattern() != null) writer.write(",dateFormat:'" + CalendarUtils.convertPattern(calendar.getPattern()) + "'");
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
            UIComponent form = ComponentUtils.findParentForm(context, calendar);
            if (form == null) {
                throw new FacesException("Calendar \"" + calendar.getClientId(context) + "\" must be enclosed with a form when using ajax selection.");
            }

            writer.write(",formId:'" + form.getClientId(context) + "'");
            writer.write(",url:'" + getActionURL(context) + "'");
            writer.write(",hasSelectListener:true");

            String onSelectProcess = calendar.getOnSelectProcess();
            onSelectProcess = onSelectProcess == null ? clientId : ComponentUtils.findClientIds(context, calendar, onSelectProcess);
            
            writer.write(",onSelectProcess:'" + onSelectProcess + "'");

            if(calendar.getOnSelectUpdate() != null) {
                writer.write(",onSelectUpdate:'" + ComponentUtils.findClientIds(context, calendar, calendar.getOnSelectUpdate()) + "'");
            }
        }

        encodeClientBehaviors(context, calendar);

        writer.write("});});");

        writer.endElement("script");
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
        Calendar calendar = (Calendar) component;
        String submittedValue = (String) value;

        if (isValueBlank(submittedValue)) {
            return null;
        }

        //Delegate to user supplied converter if defined
        if (calendar.getConverter() != null) {
            return calendar.getConverter().getAsObject(context, calendar, submittedValue);
        }

        try {
            Date convertedValue;
            Locale locale = calendar.calculateLocale(context);
            SimpleDateFormat format = new SimpleDateFormat(calendar.getPattern(), locale);
            format.setTimeZone(calendar.calculateTimeZone());

            convertedValue = format.parse(submittedValue);

            if(calendar.getSelectListener() != null) {
                calendar.queueEvent(new DateSelectEvent(calendar, convertedValue));
            }
            
            return convertedValue;

        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
}
