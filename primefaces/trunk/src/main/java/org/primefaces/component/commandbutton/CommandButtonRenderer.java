/*
 * Copyright 2009-2011 Prime Technology.
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
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class CommandButtonRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
        CommandButton button = (CommandButton) component;
        if(button.isDisabled()) {
            return;
        }
        
		String param = component.getClientId(context);
		if(context.getExternalContext().getRequestParameterMap().containsKey(param)) {
			component.queueEvent(new ActionEvent(component));
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		CommandButton button = (CommandButton) component;
		
		encodeMarkup(context, button);
		encodeScript(context, button);
	}
	
	protected void encodeMarkup(FacesContext context, CommandButton button) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
		String type = button.getType();
        String value = (String) button.getValue();
        String icon = button.resolveIcon();

		writer.startElement("button", button);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("class", button.resolveStyleClass(), "styleClass");

		String onclick = button.getOnclick();
		if(!type.equals("reset") && !type.equals("button")) {
			UIComponent form = ComponentUtils.findParentForm(context, button);
			if(form == null) {
				throw new FacesException("CommandButton : \"" + clientId + "\" must be inside a form element");
			}
			
			String formClientId = form.getClientId(context);		
			String request = button.isAjax() ? buildAjaxRequest(context, button) + "return false;" : buildNonAjaxRequest(context, button, formClientId);
			onclick = onclick != null ? onclick + ";" + request : request;
		}
		
		if(!isValueBlank(onclick)) {
			writer.writeAttribute("onclick", onclick, "onclick");
		}
		
		renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);

        if(button.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if(button.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
		
        //icon
        if(icon != null) {
            String defaultIconClass = button.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS; 
            String iconClass = defaultIconClass + " " + icon;
            
            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }
        
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        
        if(value == null)
            writer.write("ui-button");
        else
            writer.writeText(value, "value");
        
        writer.endElement("span");
			
		writer.endElement("button");
	}
	
	protected void encodeScript(FacesContext context, CommandButton button) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
		String type = button.getType();
		boolean hasValue = (button.getValue() != null);
		
        startScript(writer, clientId);

        writer.write("PrimeFaces.cw('CommandButton','" + button.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");		
		writer.write("});");
		
		endScript(writer);
	}

	protected String buildNonAjaxRequest(FacesContext context, CommandButton button, String formId) {
        boolean hasParam = false;
        StringBuilder request = new StringBuilder();
        
        for(UIComponent component : button.getChildren()) {
			if(component instanceof UIParameter) {
                UIParameter param = (UIParameter) component;
                
                if(!hasParam) {
                    request.append("PrimeFaces");
                    hasParam = true;
                }

                request.append(addSubmitParam(formId, param.getName(), String.valueOf(param.getValue())));
			}
		}

        request.append(";");

		return request.toString();
	}
}