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
package org.primefaces.component.rating;

import java.io.IOException;
import java.util.Map;
import javax.el.ValueExpression;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.PhaseId;

import org.primefaces.event.RateEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class RatingRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        Rating rating = (Rating) component;
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String clientId = rating.getClientId();
        String value = params.get(clientId + "_input");
        String submittedValue = value == null ? "0" : value;
        boolean isAjaxRating = params.containsKey(clientId + "_ajaxRating");

        rating.setSubmittedValue(submittedValue);

        if(isAjaxRating) {
            RateEvent rateEvent;

            if(isValueBlank(value))
                rateEvent = new RateEvent(rating, 0D);
            else
                rateEvent = new RateEvent(rating, Double.valueOf(value));

            if(rating.isImmediate())
                rateEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            else
                rateEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);

            rating.queueEvent(rateEvent);
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        Rating rating = (Rating) component;

        encodeMarkup(facesContext, rating);
        encodeScript(facesContext, rating);
    }

    private void encodeScript(FacesContext facesContext, Rating rating) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = rating.getClientId(facesContext);
        boolean hasRateListener = rating.getRateListener() != null;

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");

        writer.write(rating.resolveWidgetVar() + " = new PrimeFaces.widget.Rating('" + clientId + "'");
        writer.write(",{");

        writer.write("hasRateListener:" + hasRateListener);

        if(rating.getOnRate() != null) writer.write(",onRate:function(value) {" + rating.getOnRate() + ";}");
        if(rating.getUpdate() != null) writer.write(",update:'" + ComponentUtils.findClientIds(facesContext, rating, rating.getUpdate()) + "'");

        writer.write("});});");

        writer.endElement("script");
    }

    private void encodeMarkup(FacesContext context, Rating rating) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = rating.getClientId(context);
        String valueToRender = ComponentUtils.getStringValueToRender(context, rating);
        Double value = isValueBlank(valueToRender) ? null : Double.valueOf(valueToRender);

        writer.startElement("span", rating);
        writer.writeAttribute("id", clientId, "id");

        for(int i = 1; i <= rating.getStars(); i++) {
            writer.startElement("input", null);
            writer.writeAttribute("name", clientId + "_input", null);
            writer.writeAttribute("type", "radio", null);
            writer.writeAttribute("value", i, null);

            if(value != null && value.intValue() == i) {
                writer.writeAttribute("checked", "checked", null);
            }

            if (rating.isDisabled()) {
                writer.writeAttribute("disabled", "disabled", null);
            }

            writer.endElement("input");
        }

        writer.endElement("span");
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
