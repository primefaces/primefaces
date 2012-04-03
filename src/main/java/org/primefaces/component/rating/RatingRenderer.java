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
package org.primefaces.component.rating;

import java.io.IOException;
import javax.el.ValueExpression;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class RatingRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Rating rating = (Rating) component;
        if(rating.isDisabled()||rating.isReadonly()) {
            return;
        }

        String clientId = rating.getClientId();
        String value = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");
        String submittedValue = value == null ? "0" : value;

        rating.setSubmittedValue(submittedValue);

        decodeBehaviors(context, rating);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Rating rating = (Rating) component;

        encodeMarkup(context, rating);
        encodeScript(context, rating);
    }

    private void encodeScript(FacesContext context, Rating rating) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = rating.getClientId(context);

        startScript(writer, clientId);

        writer.write("$(function() {");

        writer.write("PrimeFaces.cw('Rating','" + rating.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        if(rating.getOnRate() != null) writer.write(",onRate:function(value) {" + rating.getOnRate() + ";}");        
        if(rating.isReadonly()) writer.write(",readonly:true");
        if(rating.isDisabled()) writer.write(",disabled:true");
        
        encodeClientBehaviors(context, rating);

        writer.write("},'rating');});");

        endScript(writer);
    }

    protected void encodeMarkup(FacesContext context, Rating rating) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = rating.getClientId(context);
        String valueToRender = ComponentUtils.getValueToRender(context, rating);
        int value = isValueBlank(valueToRender) ? 0 : Integer.valueOf(valueToRender);
        int stars = rating.getStars();
        boolean disabled = rating.isDisabled();
        boolean readonly = rating.isReadonly();
        String style = rating.getStyle();
        String styleClass = rating.getStyleClass();
        styleClass = styleClass == null ? Rating.CONTAINER_CLASS : Rating.CONTAINER_CLASS + " " + styleClass;
        
        if(disabled) {
            styleClass = styleClass + " ui-state-disabled"; 
        }

        writer.startElement("div", rating);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        if(rating.isCancel() && !disabled && !readonly) {
            encodeIcon(context, Rating.CANCEL_CLASS);
        }

        for(int i = 0; i < stars; i++) {
            String starClass = (i < value) ? Rating.STAR_ON_CLASS : Rating.STAR_CLASS;
            encodeIcon(context, starClass);
        }
        
        encodeInput(context, clientId + "_input", valueToRender);

        writer.endElement("div");
    }
    
    protected void encodeIcon(FacesContext context, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("a", null);
        writer.endElement("a");

        writer.endElement("div");
    }
    
    protected void encodeInput(FacesContext context, String id, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
        writer.writeAttribute("autocomplete", "off", null);
        if(value != null) {
            writer.writeAttribute("value", value, null);
        }
		writer.endElement("input");
    }
    
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        String value = (String) submittedValue;
        Rating rating = (Rating) component;
        Converter converter = rating.getConverter();

        //first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, rating, value);
		}
		//Try to guess
		else {
            ValueExpression ve = rating.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(context, rating, value);
                }
            }
		}

        //Built-in converter
        if(value == null) {
            return null;
        }

        try {
            return Double.valueOf(value);
        } catch (NumberFormatException exception) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion error", submittedValue + " is not a valid value for " + component.getClientId(context));

            throw new ConverterException(msg);
        }
    }
}
