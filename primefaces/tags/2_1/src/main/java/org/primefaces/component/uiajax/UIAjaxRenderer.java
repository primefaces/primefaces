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
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class UIAjaxRenderer extends CoreRenderer {

	public void decode(FacesContext facesContext, UIComponent component) {
		UIAjax ajax = (UIAjax) component;
		String clientId = ajax.getClientId(facesContext);
		
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId)) {
			ajax.queueEvent(new ActionEvent(ajax));
		}
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
		
		String ajaxRequest = createAjaxRequest(facesContext, uiajax, formClientId, clientId);

		ComponentUtils.decorateAttribute(parent, "on" + uiajax.getEvent(), ajaxRequest);
	}
	
	protected String createAjaxRequest(FacesContext facesContext, UIAjax uiajax, String formId, String decodeParam) {	
		StringBuilder req = new StringBuilder();
		req.append("PrimeFaces.ajax.AjaxRequest('");
		req.append(getActionURL(facesContext));
		req.append("',{");
		req.append("formId:'");
		req.append(formId);
		req.append("'");
		
		if(uiajax.isAsync()) req.append(",async:true");
		
		//source
		if(uiajax.getOnstart() != null) req.append(",onstart:function(xhr){" + uiajax.getOnstart() + ";}");
		if(uiajax.getOnerror() != null) req.append(",onerror:function(xhr, status, error){" + uiajax.getOnerror() + ";}");
		if(uiajax.getOnsuccess() != null) req.append(",onsuccess:function(data, status, xhr, args){" + uiajax.getOnsuccess() + ";}"); 
		if(uiajax.getOncomplete() != null) req.append(",oncomplete:function(xhr, status, args){" + uiajax.getOncomplete() + ";}");

		req.append(",global:" + uiajax.isGlobal());
		
		req.append("},{");
		
		req.append("'" + decodeParam + "'");
		req.append(":");
		req.append("'" + decodeParam + "'");
		
		if(uiajax.getUpdate() != null) {
			req.append(",'" + Constants.PARTIAL_UPDATE_PARAM + "':");
			req.append("'" + ComponentUtils.findClientIds(facesContext, uiajax.getParent(), uiajax.getUpdate()) + "'");
		}
		
		if(uiajax.getProcess() != null) {
			req.append(",'" + Constants.PARTIAL_PROCESS_PARAM + "':");
			req.append("'" + ComponentUtils.findClientIds(facesContext, uiajax.getParent(), uiajax.getProcess()) + "'");
		}
		
		for(UIComponent child : uiajax.getChildren()) {
			if(child instanceof UIParameter) {
				UIParameter parameter = (UIParameter) child;
				
				req.append(",");
				req.append("'" + parameter.getName() + "'");
				req.append(":");
				req.append("'" + parameter.getValue() + "'");
			}
		}
		
		req.append("});");
		
		return req.toString();
	}
}