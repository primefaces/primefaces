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

public class UIAjaxRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext facesContext, UIComponent component) {
		UIAjax ajax = (UIAjax) component;
		String clientId = ajax.getClientId(facesContext);
		
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(clientId)) {
			ajax.queueEvent(new ActionEvent(ajax));
		}
	}

    @Override
	public void encodeEnd(FacesContext fc, UIComponent component) throws IOException {
		UIAjax uiajax = (UIAjax) component;
		UIComponent parent = uiajax.getParent();
		String clientId = uiajax.getClientId(fc);
		UIComponent form = ComponentUtils.findParentForm(fc, uiajax);
		
		if(form == null) {
			throw new FacesException("UIAjax:" + clientId + " needs to be enclosed in a form");
        }
		
		String request = buildAjaxRequest(fc, uiajax, form.getClientId(fc), clientId);

		ComponentUtils.decorateAttribute(parent, "on" + uiajax.getEvent(), request);
	}
}