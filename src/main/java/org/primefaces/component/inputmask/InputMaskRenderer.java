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
package org.primefaces.component.inputmask;

import java.io.IOException;
import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class InputMaskRenderer extends InputRenderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		InputMask inputMask = (InputMask) component;

        if(inputMask.isDisabled() || inputMask.isReadonly()) {
            return;
        }

        decodeBehaviors(context, inputMask);

		String clientId = inputMask.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId);

        if(submittedValue != null) {
            inputMask.setSubmittedValue(submittedValue);
        }
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		InputMask inputMask = (InputMask) component;
		
		encodeMarkup(context, inputMask);
		encodeScript(context, inputMask);
	}
	
	protected void encodeScript(FacesContext context, InputMask inputMask) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inputMask.getClientId(context);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(inputMask.resolveWidgetVar() + " = new PrimeFaces.widget.InputMask('" + clientId + "', {");

        writer.write("mask:'" + inputMask.getMask() + "'");

        if(inputMask.getPlaceHolder()!=null) {
			writer.write(",placeholder:'" + inputMask.getPlaceHolder() + "'");
        }

        encodeClientBehaviors(context, inputMask);

		writer.write("});");
	
		writer.endElement("script");
	}
	
	protected void encodeMarkup(FacesContext context, InputMask inputMask) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inputMask.getClientId(context);
        String styleClass = inputMask.getStyleClass();
        styleClass = styleClass == null ? InputMask.STYLE_CLASS : InputMask.STYLE_CLASS + " " + styleClass;
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", "text", null);
		
		String valueToRender = ComponentUtils.getStringValueToRender(context, inputMask);
		if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender , null);
		}
		
		renderPassThruAttributes(context, inputMask, HTML.INPUT_TEXT_ATTRS);
		
        writer.writeAttribute("class", styleClass, "styleClass");

        writer.endElement("input");
	}

	@Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		InputMask inputMask = (InputMask) component;
		String value = (String) submittedValue;
		Converter converter = inputMask.getConverter();
		
		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, inputMask, value);
		}
		//Try to guess
		else {
            ValueExpression ve = inputMask.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(context, inputMask, value);
                }
            }
		}
		
		return value;
	}
}