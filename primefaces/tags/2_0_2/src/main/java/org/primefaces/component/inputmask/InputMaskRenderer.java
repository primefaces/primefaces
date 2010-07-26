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
package org.primefaces.component.inputmask;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class InputMaskRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		InputMask inputMask = (InputMask) component;
		String clientId = inputMask.getClientId(facesContext);
		
		String submittedValue = (String) facesContext.getExternalContext().getRequestParameterMap().get(clientId);
		inputMask.setSubmittedValue(submittedValue);
	}
	
	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		InputMask inputMask = (InputMask) component;
		
		encodeMarkup(facesContext, inputMask);
		encodeScript(facesContext, inputMask);
	}
	
	private void encodeScript(FacesContext facesContext, InputMask inputMask) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = inputMask.getClientId(facesContext);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("jQuery(PrimeFaces.escapeClientId('" + clientId + "')).mask('"+inputMask.getMask()+"'");
		
		if(inputMask.getPlaceHolder()!=null)
			writer.write(",{placeholder:'"+inputMask.getPlaceHolder()+"'}");

		writer.write(");");
	
		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, InputMask inputMask) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = inputMask.getClientId(facesContext);
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", "text", null);
		
		String valueToRender = ComponentUtils.getStringValueToRender(facesContext, inputMask);
		if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender , null);
		}
		
		renderPassThruAttributes(facesContext, inputMask, HTML.INPUT_TEXT_ATTRS);
		
		if(inputMask.getStyleClass() != null) {
			writer.writeAttribute("class", inputMask.getStyleClass(), "styleClass");
		}
		
		writer.endElement("input");
	}

	@Override
	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue) throws ConverterException {
		InputMask inputMask = (InputMask) component;
		String value = (String) submittedValue;
		Converter converter = inputMask.getConverter();
		
		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(facesContext, inputMask, value);
		}
		//Try to guess
		else {
			Class<?> valueType = inputMask.getValueExpression("value").getType(facesContext.getELContext());
			Converter converterForType = facesContext.getApplication().createConverter(valueType);
			
			if(converterForType != null) {
				return converterForType.getAsObject(facesContext, inputMask, value);
			}
		}
		
		return value;
	}
}