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
package org.primefaces.touch.component.inputswitch;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class InputSwitchRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		InputSwitch inputSwitch = (InputSwitch) component;
		String clientId = inputSwitch.getClientId(facesContext);
		
		if(params.containsKey(clientId)) {
			String value = params.get(clientId);
			
			if(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")) {
				inputSwitch.setSubmittedValue(true);
			} 
		} else {
			inputSwitch.setSubmittedValue(false);
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		InputSwitch inputSwitch = (InputSwitch) component;
		String clientId = inputSwitch.getClientId(facesContext);
		Boolean value = (Boolean) inputSwitch.getValue();
		
		writer.startElement("span", inputSwitch);
		writer.writeAttribute("class", "toggle", null);
		
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", "checkbox", null);
		
		if(value != null && value.booleanValue() == true) {
			writer.writeAttribute("checked", "checked", null);
		}
		
		writer.endElement("input");
		
		writer.endElement("span");
	}
}
