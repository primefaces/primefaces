/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.util.HTML;

public class CommandButtonRenderer extends org.primefaces.component.commandbutton.CommandButtonRenderer {
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		CommandButton button = (CommandButton) component;
        String clientId = button.getClientId(context);
        Object value = button.getValue();
        String type = button.getType();
        String request = (type.equals("reset")||type.equals("button")) ? null: buildRequest(context, button, clientId);        
        String onclick = buildDomEvent(context, button, "onclick", "click", "action", request);
        
		writer.startElement("button", button);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("class", button.resolveMobileStyleClass(), null);
        
		if(onclick != null) {
            if(button.requiresConfirmation()) {
                writer.writeAttribute("data-pfconfirmcommand", onclick, null);
                writer.writeAttribute("onclick", button.getConfirmationScript(), "onclick");
            }
            else
                writer.writeAttribute("onclick", onclick, "onclick");
		}
		
        renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);
        
        if (button.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if (button.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
        
        if(value == null) {
            writer.write("ui-button");
        }
        else {
            if(button.isEscape())
                writer.writeText(value, "value");
            else
                writer.write(value.toString());
        }
        
        writer.endElement("button");
	}
}