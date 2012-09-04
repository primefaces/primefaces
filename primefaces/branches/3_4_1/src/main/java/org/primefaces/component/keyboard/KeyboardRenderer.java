/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.keyboard;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class KeyboardRenderer extends InputRenderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		Keyboard keyboard = (Keyboard) component;

        if(keyboard.isDisabled() || keyboard.isReadonly()) {
            return;
        }

        decodeBehaviors(context, keyboard);

		String clientId = keyboard.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId);

        if(submittedValue != null) {
            keyboard.setSubmittedValue(submittedValue);
        }
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Keyboard keyboard = (Keyboard) component;
		
		encodeMarkup(context, keyboard);
		encodeScript(context, keyboard);
	}
	
	protected void encodeScript(FacesContext context, Keyboard keyboard) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = keyboard.getClientId(context);

		startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Keyboard','" + keyboard.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
		writer.write(",showOn:'" + keyboard.getShowMode() + "'");
		writer.write(",showAnim:'" + keyboard.getEffect() + "'");
		
		if(keyboard.isButtonImageOnly()) writer.write(",buttonImageOnly:true");
		if(keyboard.getButtonImage() != null) writer.write(",buttonImage:'" + getResourceURL(context, keyboard.getButtonImage()) + "'");
		if(keyboard.getEffectDuration() != null) writer.write(",duration:'" + keyboard.getEffectDuration() + "'");
		if(!keyboard.isKeypadOnly()) {
			writer.write(",keypadOnly:false");
			writer.write(",layoutName:'" + keyboard.getLayout() + "'");
			
			if(keyboard.getLayoutTemplate() != null)
				writer.write(",layoutTemplate:'" + keyboard.getLayoutTemplate() + "'");
		}
		
		if(keyboard.getStyleClass() != null) writer.write(",keypadClass:'" + keyboard.getStyleClass() + "'");
		if(keyboard.getPromptLabel() != null) writer.write(",prompt:'" + keyboard.getPromptLabel() + "'");
		if(keyboard.getBackspaceLabel() != null) writer.write(",backText:'" + keyboard.getBackspaceLabel() + "'");
		if(keyboard.getClearLabel() != null) writer.write(",clearText:'" + keyboard.getClearLabel() + "'");
		if(keyboard.getCloseLabel() != null) writer.write(",closeText:'" + keyboard.getCloseLabel() + "'");

        encodeClientBehaviors(context, keyboard);
	
		writer.write("});");
		
		endScript(writer);
	}

	protected void encodeMarkup(FacesContext context, Keyboard keyboard) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = keyboard.getClientId(context);
		String type = keyboard.isPassword() ? "password" : "text";
        String defaultClass = Keyboard.STYLE_CLASS;
        String styleClass = keyboard.getStyleClass();
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;
        String valueToRender = ComponentUtils.getValueToRender(context, keyboard);

		writer.startElement("input", keyboard);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", type, null);
        
        if(valueToRender != null) {
            writer.writeAttribute("value", valueToRender, "value");
        }

        renderPassThruAttributes(context, keyboard, HTML.INPUT_TEXT_ATTRS);

        writer.writeAttribute("class", styleClass, "styleClass");

        if(keyboard.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if(keyboard.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
        if(keyboard.getStyle() != null) writer.writeAttribute("style", keyboard.getStyle(), "style");

		writer.endElement("input");		
	}
}