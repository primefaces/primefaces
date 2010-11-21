/*
 * Copyright 2010 Prime Technology.
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

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
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

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write(rating.resolveWidgetVar() + " = new PrimeFaces.widget.Rating('" + clientId + "'");
        writer.write(",{");
        if (rating.getRateListener() != null) {
            UIComponent form = ComponentUtils.findParentForm(facesContext, rating);

            if (form == null) {
                throw new FacesException("Rating:\"" + clientId + "\" needs to be enclosed in a form when using a rateListener");
            }

            writer.write("hasRateListener:true");
            writer.write(",formId:'" + form.getClientId(facesContext) + "'");
            writer.write(",url:'" + getActionURL(facesContext) + "'");

            if(rating.getUpdate() != null) {
                writer.write(",update:'" + ComponentUtils.findClientIds(facesContext, rating, rating.getUpdate()) + "'");
            }
        }
        writer.write("});");

        writer.endElement("script");
    }

    private void encodeMarkup(FacesContext facesContext, Rating rating) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = rating.getClientId(facesContext);
        Object value = rating.getValue();

        if (value != null && !(value instanceof Double)) {
            throw new FacesException("Value of rating component with id \"" + clientId + "\" value must be of type java.lang.Double");
        }

        Double ratingValue = (Double) value;
        writer.startElement("span", rating);
        writer.writeAttribute("id", clientId, "id");

        for (int i = 1; i <= rating.getStars(); i++) {
            writer.startElement("input", null);
            writer.writeAttribute("name", clientId + "_input", null);
            writer.writeAttribute("type", "radio", null);
            writer.writeAttribute("value", i, null);
            writer.writeAttribute("class", "ui-rating-star", null);

            if (ratingValue != null && ratingValue.intValue() == i) {
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
    public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
        String value = (String) submittedValue;

        if(value == null)
            return null;

        try {
            return Double.valueOf(value);
        } catch (NumberFormatException exception) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion error", submittedValue + " is not a valid value for " + component.getClientId(facesContext));

            throw new ConverterException(msg);
        }
    }
}
