/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.inputtextarea.InputTextarea;
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
        if(submittedValue != null) {
            inputTextarea.setSubmittedValue(submittedValue);
        }
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        InputTextarea inputTextarea = (InputTextarea) component;
        String clientId = inputTextarea.getClientId(context);
        String valueToRender = ComponentUtils.getValueToRender(context, inputTextarea);
        String style = inputTextarea.getStyle();
        String styleClass = inputTextarea.getStyle();

		writer.startElement("textarea", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);

		renderPassThruAttributes(context, inputTextarea, HTML.INPUT_TEXTAREA_ATTRS);

        if(inputTextarea.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if(inputTextarea.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
        if(style != null) writer.writeAttribute("style", style, null);  
        if(styleClass != null) writer.writeAttribute("class", styleClass, null); 
		if(valueToRender != null) writer.writeText(valueToRender, "value");  
        
        renderPassThruAttributes(context, inputTextarea, HTML.TEXTAREA_ATTRS);
        renderDomEvents(context, inputTextarea, HTML.INPUT_TEXT_EVENTS);

        writer.endElement("textarea");
	}
}
