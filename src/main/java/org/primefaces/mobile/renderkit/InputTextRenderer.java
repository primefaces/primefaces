/*
 * Copyright 2009-2014 PrimeTek.
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
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.component.inputtext.InputText;

public class InputTextRenderer extends org.primefaces.component.inputtext.InputTextRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		InputText inputText = (InputText) component;
        if(inputText.isDisabled() || inputText.isReadonly()) {
            return;
        }
        
        decodeBehaviors(context, inputText);
        
        String inputId = inputText.getClientId(context) + "_input";
		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(inputId);
        if(submittedValue != null) {
            inputText.setSubmittedValue(submittedValue);
        }
	}

	@Override
	public void encodeMarkup(FacesContext context, InputText inputText) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputText.getClientId(context);
        String inputId = clientId + "_input";
        String type = inputText.getType();
        boolean search = type.equals("search");
        String style = inputText.getStyle();
        String defaultStyleClass = search ? InputText.MOBILE_SEARCH_STYLE_CLASS: InputText.MOBILE_STYLE_CLASS;
        String styleClass = inputText.getStyleClass();
        styleClass = (styleClass == null) ? defaultStyleClass : defaultStyleClass + " " + styleClass;
        if(inputText.isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) { 
            writer.writeAttribute("style", style, null);
        }

        encodeInput(context, inputText, inputId);
        encodeClearIcon(context, inputText);
        
        writer.endElement("div");
	}
    
    protected void encodeInput(FacesContext context, InputText inputText, String inputId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String valueToRender = ComponentUtils.getValueToRender(context, inputText);
        
        writer.startElement("input", inputText);
        writer.writeAttribute("data-role", "none", null);
        writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		writer.writeAttribute("type", inputText.getType(), null);           
      
        if(inputText.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(inputText.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        if(valueToRender != null) writer.writeAttribute("value", valueToRender , null);
        
        renderPassThruAttributes(context, inputText, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputText, HTML.INPUT_TEXT_EVENTS);
        
        writer.endElement("input");
    }
    
    protected void encodeClearIcon(FacesContext context, InputText inputText) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", InputText.MOBILE_CLEAR_ICON_CLASS, null);
        writer.endElement("a");
    }
}
