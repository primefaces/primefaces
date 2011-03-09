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
package org.primefaces.mobile.component.commandbutton;

import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class CommandButtonRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext facesContext, UIComponent component) {
        CommandButton button = (CommandButton) component;
        if(button.isDisabled()) {
            return;
        }

		String param = component.getClientId(facesContext);

		if(facesContext.getExternalContext().getRequestParameterMap().containsKey(param)) {
			component.queueEvent(new ActionEvent(component));
		}
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        CommandButton button = (CommandButton) component;
        String clientId = button.getClientId(context);
        String type = button.getType();

        writer.startElement("button", button);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("type", type, "type");

		String onclick = button.getOnclick();
		if(!type.equals("reset") && !type.equals("button")) {
			UIComponent form = ComponentUtils.findParentForm(context, button);
			if(form == null) {
				throw new FacesException("CommandButton : \"" + clientId + "\" must be inside a form element");
			}

			String formClientId = form.getClientId(context);
			String request = button.isAjax() ? buildAjaxRequest(context, button, formClientId, clientId) + "return false;" : buildNonAjaxRequest(context, button, formClientId, clientId);
			onclick = onclick != null ? onclick + ";" + request : request;
		}

        if(button.getIcon() != null) writer.writeAttribute("data-icon", button.getIcon(), null);
        if(button.getIconPos() != null) writer.writeAttribute("data-iconpos", button.getIconPos(), null);
        if(button.getStyle() != null) writer.writeAttribute("style", button.getStyle(), null);
        if(button.getStyleClass() != null) writer.writeAttribute("class", button.getStyleClass(), null);
		if(!isValueBlank(onclick)) writer.writeAttribute("onclick", onclick, "onclick");
        
		if(button.getValue() != null) writer.write(button.getValue().toString());

		writer.endElement("button");
    }
}
