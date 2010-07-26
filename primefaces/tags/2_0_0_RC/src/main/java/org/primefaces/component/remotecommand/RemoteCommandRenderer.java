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
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class RemoteCommandRenderer extends CoreRenderer {
	
	public void decode(FacesContext facesContext, UIComponent component) {		
		String param = component.getClientId(facesContext);

		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(param))
			component.queueEvent(new ActionEvent(component));
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		RemoteCommand command = (RemoteCommand) component;
		String clientId = command.getClientId(facesContext);
		UIComponent form = ComponentUtils.findParentForm(facesContext, command);
		if(form == null) {
			throw new FacesException("Remote Command '" + command.getName() + "' must be enclosed inside a form component.");
		}
		
		writer.startElement("script", command);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(command.getName() + " = function() {");
		
		writer.write("PrimeFaces.ajax.AjaxRequest('");
		writer.write(getActionURL(facesContext));
		writer.write("',{");
		writer.write("formClientId:'" + form.getClientId(facesContext) + "'");
		writer.write(",partialSubmit:true");
		
		if(command.getOnstart() != null)
			writer.write(",onstart:function(){" + command.getOnstart() + ";}");
		if(command.getOncomplete() != null)
			writer.write(",oncomplete:function(){" + command.getOncomplete() + ";}");
		
		writer.write("},");
		
		writer.write("'update=");
		if(command.getUpdate() != null)
			writer.write(command.getUpdate());
		else
 			writer.write(form.getClientId(facesContext));
		
		//UIParams
		for(UIComponent kid : command.getChildren()) {
			if(kid instanceof UIParameter) {
				UIParameter parameter = (UIParameter) component;
				
				writer.write("&");
				writer.write(parameter.getName());
				writer.write("=");
				writer.write((String) parameter.getValue());
			}
		}
		
		writer.write("&" + clientId + "=" + clientId);
		writer.write("');}");

		writer.endElement("script");
	}
}