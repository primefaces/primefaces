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
package org.primefaces.component.uiajax;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class UIAjaxRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		UIAjax ajax = (UIAjax) component;
		String clientId = ajax.getClientId(facesContext);
		
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId))
			ajax.queueEvent(new ActionEvent(ajax));
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		UIAjax uiajax = (UIAjax) component;
		UIComponent parent = uiajax.getParent();
		
		String formClientId = null;
		String clientId = uiajax.getClientId(facesContext);
		UIComponent form = ComponentUtils.findParentForm(facesContext, uiajax);
		
		if(form != null)
			formClientId = ComponentUtils.findParentForm(facesContext, uiajax).getClientId(facesContext);
		else
			throw new FacesException("UIAjax:" + clientId + " needs to be enclosed in a form");
		
		String ajaxRequest = getAjaxRequest(facesContext, uiajax, formClientId);
		
		ComponentUtils.decorateAttribute(parent, "on" + uiajax.getEvent(), ajaxRequest);
		
		/*
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		
		writer.write("PrimeFaces.onContentReady('" + parentClientId + "', function() {\n");
		writer.write("var ajaxRequestEvent = new PrimeFaces.ajax.AjaxRequestEvent(");
		writer.write("'" + getActionURL(facesContext) + "',");
		
		writer.write("{formClientId:'" + formClientId + "', partialSubmit:" + ajax.isPartialSubmit() + ", ajaxifiedComponent:'" + parentClientId + "'");
		if(ajax.getOnstart() != null) {
			writer.write(",onstart: function() {" + ajax.getOnstart() + ";}");
		}
		if(ajax.getOncomplete() != null) {
			writer.write(",oncomplete: function() {" + ajax.getOncomplete() + ";}");
		}
		writer.write("},");
		
		writer.write("'update=");
		if(ajax.getUpdate() != null)
			writer.write(ajax.getUpdate());
		else
			writer.write(formClientId);
		
		writer.write("&" + clientId + "=" + clientId + "');\n");
		writer.write("YAHOO.util.Event.addListener('" + parentClientId + "', '" + ajax.getEvent() + "', PrimeFaces.ajax.AjaxRequestEventHandler, ajaxRequestEvent);\n");
		writer.write("});\n");
		writer.endElement("script");
		*/
	}
	
	//TODO: A common AjaxRequest builder sounds better
	private String getAjaxRequest(FacesContext facesContext, UIAjax uiajax, String formClientId) {
		String clientId = uiajax.getClientId(facesContext);

		StringBuilder req = new StringBuilder();
		req.append("PrimeFaces.ajax.AjaxRequest('");
		req.append(getActionURL(facesContext));
		req.append("',{");
		req.append("formClientId:'");
		req.append(formClientId);
		req.append("', ajaxifiedComponent:this");
		
		if(uiajax.getOnstart() != null) {
			req.append(",onstart:function(){" + uiajax.getOnstart() + ";}");
		}
		if(uiajax.getOncomplete() != null) {
			req.append(",oncomplete:function(){" + uiajax.getOncomplete() + ";}");
		}
		if(uiajax.isPartialSubmit()) {
			req.append(",partialSubmit:true");
		}
		
		req.append("},");
		
		req.append("'update=");
		if(uiajax.getUpdate() != null) {
			req.append(uiajax.getUpdate());
		}
		else {
 			req.append(formClientId);
		}
		
		req.append("&");
		req.append(clientId);
		req.append("=");
		req.append(clientId);
		req.append("');");
		
		return req.toString();
	}
}