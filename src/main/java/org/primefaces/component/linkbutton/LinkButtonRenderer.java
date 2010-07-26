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
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class LinkButtonRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		LinkButton button = (LinkButton) component;

		encodeButtonScript(facesContext, button);
		encodeButtonMarkup(facesContext, button);
	}

	protected void encodeButtonMarkup(FacesContext facesContext, LinkButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", "yui-button yui-link-button", null);
		
		writer.startElement("span", null);
		writer.writeAttribute("class", "first-child", null);
		
		writer.startElement("a", button);
		writer.writeAttribute("href", getResourceURL(facesContext, button.getHref()), null);
		if(button.getStyle() != null)
			writer.writeAttribute("style", button.getStyle(), null);
		if(button.getStyleClass() != null)
			writer.writeAttribute("class", button.getStyleClass(), null);
		
		if(button.getTarget() != null) writer.writeAttribute("target", button.getTarget(), null);
		
		String value = ComponentUtils.getStringValueToRender(facesContext, button);
		if(value != null)
			writer.write(value);
		
		writer.endElement("a");
		
		writer.endElement("span");
		writer.endElement("span");
	}

	protected void encodeButtonScript(FacesContext facesContext, LinkButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String buttonVar = createUniqueWidgetVar(facesContext, button);
		
		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("PrimeFaces.onContentReady(\"" + clientId + "\", function() {\n");		
		writer.write(buttonVar + " = new YAHOO.widget.Button(\"" + clientId  + "\");");
		renderPassThruAttributes(facesContext, button, buttonVar, HTML.BUTTON_EVENTS);
		writer.write("});");

		writer.endElement("script");
	}
}