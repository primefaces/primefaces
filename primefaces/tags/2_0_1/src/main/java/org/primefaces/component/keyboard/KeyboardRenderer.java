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
package org.primefaces.component.keyboard;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class KeyboardRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Keyboard keyboard = (Keyboard) component;
		String clientId = keyboard.getClientId(facesContext);
		
		String submittedValue = (String) facesContext.getExternalContext().getRequestParameterMap().get(clientId);
		keyboard.setSubmittedValue(submittedValue);
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Keyboard keyboard = (Keyboard) component;
		
		encodeMarkup(facesContext, keyboard);
		encodeScript(facesContext, keyboard);
	}
	
	private void encodeScript(FacesContext facesContext, Keyboard keyboard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = keyboard.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, keyboard);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(var + " = new PrimeFaces.widget.Keyboard('" + clientId + "', {");
		writer.write("showOn:'" + keyboard.getShowMode() + "'");
		writer.write(",showAnim:'" + keyboard.getEffect() + "'");
		
		if(keyboard.isButtonImageOnly()) writer.write(",buttonImageOnly:true");
		if(keyboard.getButtonImage() != null) writer.write(",buttonImage:'" + getResourceURL(facesContext, keyboard.getButtonImage()) + "'");
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
	
		writer.write("});");
		
		writer.endElement("script");
	}

	private void encodeMarkup(FacesContext facesContext, Keyboard keyboard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = keyboard.getClientId(facesContext);
		String type = keyboard.isPassword() ? "password" : "text";
		
		writer.startElement("input", keyboard);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", type, null);
		writer.writeAttribute("value", ComponentUtils.getStringValueToRender(facesContext, keyboard), null);
		renderPassThruAttributes(facesContext, keyboard, HTML.INPUT_TEXT_ATTRS);
		writer.endElement("input");		
	}
}
