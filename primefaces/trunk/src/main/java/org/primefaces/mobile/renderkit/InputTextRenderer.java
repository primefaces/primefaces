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
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.component.inputtext.InputText;

public class InputTextRenderer extends InputRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		InputText inputText = (InputText) component;
        if(inputText.isDisabled() || inputText.isReadonly()) {
            return;
        }
        
        decodeBehaviors(context, inputText);
        
        String clientId = inputText.getClientId(context);
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId);
        if(submittedValue != null) {
            inputText.setSubmittedValue(submittedValue);
        }
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		InputText inputText = (InputText) component;
        String clientId = inputText.getClientId(context);
        String valueToRender = ComponentUtils.getValueToRender(context, inputText);
        String style = inputText.getStyle();
        String styleClass = inputText.getStyle();
        
        writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", inputText.getType(), null);           
      
        if(inputText.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(inputText.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        if(style != null) writer.writeAttribute("style", style, null);  
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);  
        if(valueToRender != null) writer.writeAttribute("value", valueToRender , null);
        
        renderPassThruAttributes(context, inputText, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputText, HTML.INPUT_TEXT_EVENTS);
        
        writer.endElement("input");
	}
}
