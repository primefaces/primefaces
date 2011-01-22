/*
 * Copyright 2009-2010 Prime Technology.
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
package org.primefaces.component.inputtext;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class InputTextRenderer extends CoreRenderer {
@Override
	public void decode(FacesContext context, UIComponent component) {
		InputText inputText = (InputText) component;
		String clientId = inputText.getClientId(context);

		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId);
		inputText.setSubmittedValue(submittedValue);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		InputText inputText = (InputText) component;

		encodeMarkup(context, inputText);
		encodeScript(context, inputText);
	}

	protected void encodeScript(FacesContext context, InputText inputText) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inputText.getClientId(context);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(inputText.resolveWidgetVar() + " = new PrimeFaces.widget.InputText({");

        writer.write("id:'" + clientId + "'");

        encodeClientBehaviors(context, inputText);

        writer.write("});");

		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, InputText inputText) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inputText.getClientId(context);
        String styleClass = inputText.getStyleClass();
        styleClass = styleClass == null ? InputText.STYLE_CLASS : InputText.STYLE_CLASS + " " + styleClass;

		writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", "text", null);

		String valueToRender = ComponentUtils.getStringValueToRender(context, inputText);
		if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender , null);
		}

		renderPassThruAttributes(context, inputText, HTML.INPUT_TEXT_ATTRS);

        writer.writeAttribute("class", styleClass, "styleClass");

        writer.endElement("input");
	}

	@Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		InputText inputText = (InputText) component;
		String value = (String) submittedValue;
		Converter converter = inputText.getConverter();

		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, inputText, value);
		}
		//Try to guess
		else {
			Class<?> valueType = inputText.getValueExpression("value").getType(context.getELContext());
			Converter converterForType = context.getApplication().createConverter(valueType);

			if(converterForType != null) {
				return converterForType.getAsObject(context, inputText, value);
			}
		}

		return value;
	}
}