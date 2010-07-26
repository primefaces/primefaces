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
package org.primefaces.component.hotkey;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class HotkeyRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		Hotkey hotkey = (Hotkey) component;

		if(params.containsKey(hotkey.getClientId(facesContext)))
			hotkey.queueEvent(new ActionEvent(hotkey));
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Hotkey hotkey = (Hotkey) component;
		String clientId = hotkey.getClientId(facesContext);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(document).bind('keydown', '" + hotkey.getBind() + "', function(){\n");
	
		if(hotkey.getHandler() == null) {
			String formClientId = null;
			UIComponent form = ComponentUtils.findParentForm(facesContext,hotkey);

			if (form != null)
				formClientId = ComponentUtils.findParentForm(facesContext,hotkey).getClientId(facesContext);
			else
				throw new FacesException("Hotkey:"+ clientId+ " needs to be enclosed in a form when using an hotkeyListener");
		
			writer.write("PrimeFaces.ajax.AjaxRequest(");
			writer.write("'" + getActionURL(facesContext) + "'");

			writer.write(",{formId:'" + formClientId + "'");
			
			if(hotkey.isAsync()) writer.write(",async:true");
			
			//Callbacks
			if(hotkey.getOnstart() != null) writer.write(",onstart:function(xhr){" + hotkey.getOnstart() + ";}");
			if(hotkey.getOnerror() != null) writer.write(",onerror:function(xhr, status, error){" + hotkey.getOnerror() + ";}");
			if(hotkey.getOnsuccess() != null) writer.write(",onsuccess:function(data, status, xhr, args){" + hotkey.getOnsuccess() + ";}"); 
			if(hotkey.getOncomplete() != null) writer.write(",oncomplete:function(xhr, status, args){" + hotkey.getOncomplete() + ";}");

			writer.write(",global:" + hotkey.isGlobal());
			
			writer.write("},");
			
			writer.write("{'" + clientId + "':'" + clientId + "'");
			if(hotkey.getUpdate() != null)
				writer.write(",'" + Constants.PARTIAL_UPDATE_PARAM + "':'" + ComponentUtils.findClientIds(facesContext, hotkey, hotkey.getUpdate()) + "'");
			
			if(hotkey.getProcess() != null)
				writer.write(",'" + Constants.PARTIAL_PROCESS_PARAM + "':'" + ComponentUtils.findClientIds(facesContext, hotkey, hotkey.getProcess()) + "'");
		
			writer.write("});");
		} else {
			writer.write(hotkey.getHandler() + ";");
		}

		writer.write("});");
		writer.endElement("script");
	}
}