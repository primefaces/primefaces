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
package org.primefaces.component.keyboard;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class KeyboardRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		Keyboard keyboard = (Keyboard) component;
		String clientId = keyboard.getClientId(context);

        if(params.containsKey(clientId)) {
            keyboard.setSubmittedValue((String) params.get(clientId));
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

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(keyboard.resolveWidgetVar() + " = new PrimeFaces.widget.Keyboard('" + clientId + "', {");
		writer.write("showOn:'" + keyboard.getShowMode() + "'");
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
		
		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, Keyboard keyboard) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = keyboard.getClientId(context);
		String type = keyboard.isPassword() ? "password" : "text";
		
		writer.startElement("input", keyboard);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", type, null);
		writer.writeAttribute("value", ComponentUtils.getStringValueToRender(context, keyboard), null);
		renderPassThruAttributes(context, keyboard, HTML.INPUT_TEXT_ATTRS);
		writer.endElement("input");		
	}
}
