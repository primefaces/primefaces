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
package org.primefaces.component.linkbutton;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

public class LinkButtonRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		LinkButton button = (LinkButton) component;

		encodeMarkup(facesContext, button);
		encodeScript(facesContext, button);
	}

	protected void encodeMarkup(FacesContext facesContext, LinkButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		
		writer.startElement("button", button);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, "name");
		if(button.getStyleClass() != null) 
			writer.writeAttribute("class", button.getStyleClass() , "styleClass");
		
		renderPassThruAttributes(facesContext, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);
		
		writer.writeAttribute("onclick", "window.location='" + button.getHref() + "'", null);
		
		if(button.getValue() != null) {
			writer.write(button.getValue().toString());
		}
			
		writer.endElement("button");
	}

	protected void encodeScript(FacesContext facesContext, LinkButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, button);
		boolean hasValue = (button.getValue() != null);
		
		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(widgetVar + " = new PrimeFaces.widget.LinkButton('" + clientId + "', {");
		
		if(button.getImage() != null) {
			writer.write("text:" + hasValue);
			writer.write(",icons:{");
			writer.write("primary:'" + button.getImage() + "'");
			writer.write("}");
		} 
		
		writer.write("});");
		
		writer.endElement("script");
	}
}