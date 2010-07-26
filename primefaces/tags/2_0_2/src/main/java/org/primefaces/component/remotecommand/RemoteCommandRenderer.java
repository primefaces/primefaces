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
package org.primefaces.component.remotecommand;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class RemoteCommandRenderer extends CoreRenderer {
	
	public void decode(FacesContext facesContext, UIComponent component) {
		RemoteCommand command = (RemoteCommand) component;
		
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(command.getClientId(facesContext))) {
			command.queueEvent(new ActionEvent(command));
		}
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		RemoteCommand command = (RemoteCommand) component;
		String clientId = command.getClientId(facesContext);
		UIComponent form = ComponentUtils.findParentForm(facesContext, command);
		
		if(form == null) {
			throw new FacesException("Remote Command '" + command.getName() + "' must be enclosed inside a form component.");
		}
		
		String formClientId = form.getClientId(facesContext);
		
		writer.startElement("script", command);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(command.getName() + " = function() {");
		
		writer.write(buildAjaxRequest(facesContext, command, formClientId, clientId));
		
		writer.write("}");

		writer.endElement("script");
	}
}