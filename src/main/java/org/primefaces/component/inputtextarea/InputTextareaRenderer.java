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
package org.primefaces.component.inputtextarea;

import java.io.IOException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class InputTextareaRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		InputTextarea inputTextarea = (InputTextarea) component;
		String clientId = inputTextarea.getClientId(context);

		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId);
		inputTextarea.setSubmittedValue(submittedValue);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		InputTextarea inputTextarea = (InputTextarea) component;

		encodeMarkup(context, inputTextarea);
		encodeScript(context, inputTextarea);
	}

	protected void encodeScript(FacesContext context, InputTextarea inputTextarea) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inputTextarea.getClientId(context);
        boolean autoResize = inputTextarea.isAutoResize();

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(inputTextarea.resolveWidgetVar() + " = new PrimeFaces.widget.InputTextarea('" + clientId + "', {");

        writer.write("autoResize:" + autoResize);
        writer.write(",maxHeight:" + inputTextarea.getMaxHeight());
        writer.write(",effectDuration:" + inputTextarea.getEffectDuration());

        encodeClientBehaviors(context, inputTextarea);

        writer.write("});");

		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, InputTextarea inputTextarea) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inputTextarea.getClientId(context);
        String styleClass = inputTextarea.getStyleClass();
        styleClass = styleClass == null ? InputTextarea.STYLE_CLASS : InputTextarea.STYLE_CLASS + " " + styleClass;

        if(inputTextarea.isAutoResize()) {
            styleClass = styleClass + " ui-inputtextarea-resizable";
        }

		writer.startElement("textarea", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);

		renderPassThruAttributes(context, inputTextarea, HTML.INPUT_TEXTAREA_ATTRS);

        writer.writeAttribute("class", styleClass, "styleClass");

        String valueToRender = ComponentUtils.getStringValueToRender(context, inputTextarea);
		if(valueToRender != null) {
			writer.writeText(valueToRender, "value");
		}

        writer.endElement("textarea");
	}

	@Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
		InputTextarea inputTextarea = (InputTextarea) component;
		String value = (String) submittedValue;
		Converter converter = inputTextarea.getConverter();

		//first ask the converter
		if(converter != null) {
			return converter.getAsObject(context, inputTextarea, value);
		}
		//Try to guess
		else {
            ValueExpression ve = inputTextarea.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if(converterForType != null) {
                    return converterForType.getAsObject(context, inputTextarea, value);
                }
            }
		}

		return value;
	}
}
