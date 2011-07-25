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
package org.primefaces.component.inputtextarea;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class InputTextareaRenderer extends InputRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		InputTextarea inputTextarea = (InputTextarea) component;

        if(inputTextarea.isDisabled() || inputTextarea.isReadonly()) {
            return;
        }

        decodeBehaviors(context, inputTextarea);

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

        writer.write(inputTextarea.resolveWidgetVar() + " = new PrimeFaces.widget.InputTextarea('" + clientId + "',{");

        writer.write("autoResize:" + autoResize);
        writer.write(",maxHeight:" + inputTextarea.getMaxHeight());
        writer.write(",effectDuration:" + inputTextarea.getEffectDuration());
        writer.write(",maxLength:" + inputTextarea.getMaxLength());
        
        encodeClientBehaviors(context, inputTextarea);

        if(!themeForms()) {
            writer.write(",theme:false");
        }

        writer.write("});");

		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, InputTextarea inputTextarea) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = inputTextarea.getClientId(context);

		writer.startElement("textarea", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);

		renderPassThruAttributes(context, inputTextarea, HTML.INPUT_TEXTAREA_ATTRS);

        if(inputTextarea.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(inputTextarea.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        if(inputTextarea.getStyle() != null) writer.writeAttribute("style", inputTextarea.getStyle(), null);

        writer.writeAttribute("class", createStyleClass(inputTextarea), "styleClass");

        String valueToRender = ComponentUtils.getStringValueToRender(context, inputTextarea);
		if(valueToRender != null) {
			writer.writeText(valueToRender, "value");
		}

        writer.endElement("textarea");
	}
    
    protected String createStyleClass(InputTextarea inputTextarea) {
        String defaultClass = themeForms() ? InputTextarea.THEME_INPUT_CLASS : InputTextarea.PLAIN_INPUT_CLASS;
        defaultClass = inputTextarea.isValid() ? defaultClass : defaultClass + " ui-state-error";
        defaultClass = !inputTextarea.isDisabled() ? defaultClass : defaultClass + " ui-state-disabled";
        
        String styleClass = inputTextarea.getStyleClass();
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;
        
        if(inputTextarea.isAutoResize()) {
            styleClass = styleClass + " ui-inputtextarea-resizable";
        }
        
        return styleClass;
    }
}
