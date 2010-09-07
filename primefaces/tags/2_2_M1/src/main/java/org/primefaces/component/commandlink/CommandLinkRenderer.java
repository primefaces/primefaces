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
package org.primefaces.component.commandlink;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.component.api.AjaxSource;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class CommandLinkRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext facesContext, UIComponent component) {	
		String param = component.getClientId();

		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(param)) {
			component.queueEvent(new ActionEvent(component));
		}
	}

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		CommandLink link = (CommandLink) component;
		String clientId = link.getClientId(facesContext);

		UIComponent form = ComponentUtils.findParentForm(facesContext, link);
		if(form == null) {
			throw new FacesException("Commandlink \"" + clientId + "\" must be inside a form component");
		}
		
		if(!link.isDisabled()) {
			writer.startElement("a", link);
			writer.writeAttribute("id", clientId, "id");
			writer.writeAttribute("href", "javascript:void(0);", null);
			if(link.getStyleClass() != null) writer.writeAttribute("class", link.getStyleClass(), null);
			
			String formClientId = form.getClientId(facesContext);
			String request = link.isAjax() ? buildAjaxRequest(facesContext, (AjaxSource) link, formClientId, clientId) : buildNonAjaxRequest(facesContext, link, formClientId, clientId);
			String onclick = link.getOnclick() != null ? link.getOnclick() + ";" + request : request;
			writer.writeAttribute("onclick", onclick, "onclick");
			
			renderPassThruAttributes(facesContext, link, HTML.LINK_ATTRS, HTML.CLICK_EVENT);

			if(link.getValue() != null)
				writer.write(link.getValue().toString());
			else
				renderChildren(facesContext, link);
			
			writer.endElement("a");
		} else {
			writer.startElement("span", link);
			writer.writeAttribute("id", clientId, "id");
			
			if(link.getValue() != null)
				writer.write(link.getValue().toString());
			else
				renderChildren(facesContext, link);
			
			writer.endElement("span");
		}
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}