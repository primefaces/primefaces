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
package org.primefaces.component.commandbutton;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class CommandButtonRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext facesContext, UIComponent component) {		
		String param = component.getClientId(facesContext);
		
		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(param)) {
			component.queueEvent(new ActionEvent(component));
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		CommandButton button = (CommandButton) component;
		
		//myfaces fix
		if(button.getType() == null)
			button.setType("submit");
		
		encodeMarkup(facesContext, button);
		encodeScript(facesContext, button);
	}
	
	protected void encodeMarkup(FacesContext facesContext, CommandButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String type = button.getType();

		writer.startElement("button", button);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, "name");
		if(button.getStyleClass() != null) writer.writeAttribute("class", button.getStyleClass() , "styleClass");

		String onclick = button.getOnclick();
		if(!type.equals("reset") && !type.equals("button")) {
			UIComponent form = ComponentUtils.findParentForm(facesContext, button);
			if(form == null) {
				throw new FacesException("CommandButton : \"" + clientId + "\" must be inside a form element");
			}
			
			String formClientId = form.getClientId(facesContext);		
			String request = button.isAjax() ? buildAjaxRequest(facesContext, button, formClientId, clientId) + "return false;" : buildNonAjaxRequest(facesContext, button, formClientId);
			onclick = onclick != null ? onclick + ";" + request : request;
		}
		
		if(!isValueBlank(onclick)) {
			writer.writeAttribute("onclick", onclick, "onclick");
		}
		
		renderPassThruAttributes(facesContext, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);
		
		if(button.getValue() != null) {
			writer.write(button.getValue().toString());
		} else if(button.getImage() != null) {
			writer.write("ui-button");
		}
			
		writer.endElement("button");
	}
	
	protected void encodeScript(FacesContext facesContext, CommandButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, button);
		String type = button.getType();
		boolean hasValue = (button.getValue() != null);
		
		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(widgetVar + " = new PrimeFaces.widget.CommandButton('" + clientId + "', {");
		
		if(type.equals("image") || button.getImage() != null) {
			writer.write("text:" + hasValue);
			writer.write(",icons:{");
			writer.write("primary:'" + button.getImage() + "'");
			writer.write("}");
		} 
		
		writer.write("});");
		
		writer.endElement("script");
	}

	protected String buildNonAjaxRequest(FacesContext facesContext, CommandButton button, String formId) {
		boolean isPartialProcess = button.getProcess() != null;
        boolean hasParam = false;
        StringBuilder request = new StringBuilder();
        
        for(UIComponent component : button.getChildren()) {
			if(component instanceof UIParameter) {
                UIParameter param = (UIParameter) component;
                
                if(!hasParam) {
                    request.append("PrimeFaces");
                    hasParam = true;
                }

                request.append(addSubmitParam(formId, param.getName(), (String) param.getValue()));
			}
		}

        if(isPartialProcess) {
            if(!hasParam) {
                request.append("PrimeFaces");
            }

            request.append(addSubmitParam(formId, PartialViewContext.PARTIAL_EXECUTE_PARAM_NAME, ComponentUtils.findClientIds(facesContext, button, button.getProcess())));
        }

		return request.toString();
	}
}