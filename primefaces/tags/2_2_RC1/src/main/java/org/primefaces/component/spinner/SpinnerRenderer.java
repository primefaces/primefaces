/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.component.spinner;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class SpinnerRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		Spinner spinner = (Spinner) component;
		String clientId = spinner.getClientId(context);
		
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId);
		spinner.setSubmittedValue(submittedValue);
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Spinner spinner = (Spinner) component;
		
		//IE8 Standards mode fix
		context.getResponseWriter().write("<!--[if IE 8.0]><style type=\"text/css\">.ui-spinner {border:1px solid transparent;}</style><![endif]-->");
		
		encodeMarkup(context, spinner);
		encodeScript(context, spinner);
	}
	
	protected void encodeScript(FacesContext context, Spinner spinner) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = spinner.getClientId(context);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("jQuery(function(){");

		writer.write(spinner.resolveWidgetVar() + " = new PrimeFaces.widget.Spinner('" + clientId + "',{");

        writer.write("step:" + spinner.getStepFactor());
		
		if(spinner.getMin() != Double.MIN_VALUE) writer.write(",min:" + spinner.getMin());
		if(spinner.getMax() != Double.MAX_VALUE) writer.write(",max:" + spinner.getMax());
		if(spinner.getWidth() != Integer.MIN_VALUE) writer.write(",width:" + spinner.getWidth());
		if(spinner.getShowOn() != null) writer.write(",showOn:'" + spinner.getShowOn() + "'");
		if(spinner.getPrefix() != null) writer.write(",prefix:'" + spinner.getPrefix() + "'");
		if(spinner.getSuffix() != null) writer.write(",suffix:'" + spinner.getSuffix() + "'");

        encodeClientBehaviors(context, spinner);
 		
		writer.write("});});");
		
		writer.endElement("script");
	}
	
	protected void encodeMarkup(FacesContext facesContext, Spinner spinner) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = spinner.getClientId(facesContext);
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", "text", null);

		String valueToRender = ComponentUtils.getStringValueToRender(facesContext, spinner);
		if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender , null);
		}
		
		renderPassThruAttributes(facesContext, spinner, HTML.INPUT_TEXT_ATTRS);

        if(spinner.getStyleClass() != null) {
            writer.writeAttribute("class", spinner.getStyleClass(), "styleClass");
        }
		
		writer.endElement("input");
	}

	@Override
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		Spinner spinner = (Spinner) component;
		String value = (String) submittedValue;
		Converter converter = spinner.getConverter();
		
		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(facesContext, spinner, value);
		}
		//Try to guess
		else {
			Class<?> valueType = spinner.getValueExpression("value").getType(facesContext.getELContext());
			Converter converterForType = facesContext.getApplication().createConverter(valueType);
			
			if(converterForType != null) {
				return converterForType.getAsObject(facesContext, spinner, value);
			}
		}
		
		return value;
	}
}